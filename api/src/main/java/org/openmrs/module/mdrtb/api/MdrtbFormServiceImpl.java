/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.program.TbPatientProgram;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public class MdrtbFormServiceImpl extends BaseOpenmrsService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public TB03Form processTB03Form(TB03Form tb03, Location location) throws MdrtbAPIException {
		// Should have encounter property
		if (tb03.getEncounter() == null) {
			throw new MdrtbAPIException("Encounter property not found!");
		}
		// Should have location property
		if (location == null && tb03.getEncounter().getLocation() == null) {
			throw new MdrtbAPIException("Location not attached with Encounter!");
		}
		// If location is passed, then set it
		if (location != null) {
			tb03.setLocation(location);
		}
		// Handle cause of death
		if (tb03.getCauseOfDeath() != null
		        && !tb03.getCauseOfDeath().equals(
		            Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_OTHER_DISEASES))) {
			tb03.setOtherCauseOfDeath(null);
		}
		// Save the actual update
		Context.getEncounterService().saveEncounter(tb03.getEncounter());
		
		// Workflow updates
		Concept outcome = tb03.getTreatmentOutcome();
		Concept group = tb03.getRegistrationGroup();
		Concept groupByDrug = tb03.getRegistrationGroupByDrug();
		TbPatientProgram tbPatientProgram = Context.getService(MdrtbService.class).getTbPatientProgram(
		    tb03.getPatientProgramId());
		Program program = tbPatientProgram.getPatientProgram().getProgram();
		try {
			// Complete Patient program if there's an output
			ProgramWorkflow outcomeFlow = Context.getService(MdrtbService.class).getProgramWorkflow(program,
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME).getId());
			ProgramWorkflowState outcomeState = Context.getService(MdrtbService.class).getProgramWorkflowState(outcomeFlow,
			    outcome.getId());
			tbPatientProgram.setOutcome(outcomeState);
			tbPatientProgram.setDateCompleted(tb03.getTreatmentOutcomeDate());
		}
		catch (Exception e) {
			tbPatientProgram.setDateCompleted(null);
		}
		// Classify according to group
		try {
			ProgramWorkflow groupFlow = Context.getService(MdrtbService.class).getProgramWorkflow(program,
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP).getId());
			ProgramWorkflowState groupState = Context.getService(MdrtbService.class).getProgramWorkflowState(groupFlow,
			    group.getId());
			tbPatientProgram.setClassificationAccordingToPatientGroups(groupState);
		}
		catch (Exception e) {}
		try {
			ProgramWorkflow groupByDrugFlow = Context.getService(MdrtbService.class).getProgramWorkflow(
			    program,
			    Context.getService(MdrtbService.class)
			            .getConcept(MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE).getId());
			ProgramWorkflowState groupByDrugState = Context.getService(MdrtbService.class).getProgramWorkflowState(
			    groupByDrugFlow, groupByDrug.getId());
			tbPatientProgram.setClassificationAccordingToPreviousDrugUse(groupByDrugState);
		}
		catch (Exception e) {}
		
		// Update Patient Program
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(
		    tbPatientProgram.getPatientProgram().getPatientProgramId());
		Context.getProgramWorkflowService().savePatientProgram(pp);
		
		// Update Patient if there was an outcome
		if (outcome != null
		        && (outcome.getId().intValue() == Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
		            MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID)))) {
			Patient patient = tbPatientProgram.getPatient();
			if (!patient.getDead()) {
				patient.setDead(new Boolean(true));
				patient.setCauseOfDeath(tb03.getCauseOfDeath());
			}
			Context.getPatientService().savePatient(patient);
		}
		return tb03;
	}
}
