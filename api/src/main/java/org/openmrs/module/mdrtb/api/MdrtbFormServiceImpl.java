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
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public class MdrtbFormServiceImpl extends BaseOpenmrsService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Process the {@link TB03Form} for DOTS-TB patients
	 * 
	 * @param tb03
	 * @param location
	 * @return
	 * @throws MdrtbAPIException
	 */
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
	
	/**
	 * Process the {@link TB03uForm} for MDR-TB patients
	 * 
	 * @param tb03u
	 * @param location
	 * @return
	 * @throws MdrtbAPIException
	 */
	public TB03uForm processTB03uForm(TB03uForm tb03u, Location location) throws MdrtbAPIException {
		// Should have encounter property
		if (tb03u.getEncounter() == null) {
			throw new MdrtbAPIException("Encounter property not found!");
		}
		// Should have location property
		if (location == null && tb03u.getEncounter().getLocation() == null) {
			throw new MdrtbAPIException("Location not attached with Encounter!");
		}
		// If location is passed, then set it
		if (location != null) {
			tb03u.setLocation(location);
		}
		// Handle cause of death
		if (tb03u.getCauseOfDeath() != null
		        && !tb03u.getCauseOfDeath().equals(
		            Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_OTHER_DISEASES))) {
			tb03u.setOtherCauseOfDeath(null);
		}
		// Save the actual update
		Context.getEncounterService().saveEncounter(tb03u.getEncounter());
		
		//handle changes in workflows
		Concept outcome = tb03u.getTreatmentOutcome();
		Concept group = tb03u.getRegistrationGroup();
		Concept groupByDrug = tb03u.getRegistrationGroupByDrug();
		
		MdrtbPatientProgram mdrtbPatientProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(
		    tb03u.getPatientProgramId());
		// Complete Patient program if there's an output
		try {
			ProgramWorkflow outcomeFlow = Context.getService(MdrtbService.class).getProgramWorkflow(
			    mdrtbPatientProgram.getPatientProgram().getProgram(),
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME).getId());
			ProgramWorkflowState outcomeState = Context.getService(MdrtbService.class).getProgramWorkflowState(outcomeFlow,
			    outcome.getId());
			mdrtbPatientProgram.setOutcome(outcomeState);
			mdrtbPatientProgram.setDateCompleted(tb03u.getTreatmentOutcomeDate());
		}
		catch (Exception e) {
			mdrtbPatientProgram.setDateCompleted(null);
		}
		// Classify according to group
		try {
			ProgramWorkflow groupFlow = Context.getService(MdrtbService.class).getProgramWorkflow(
			    mdrtbPatientProgram.getPatientProgram().getProgram(),
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT)
			            .getId());
			ProgramWorkflowState groupState = Context.getService(MdrtbService.class).getProgramWorkflowState(groupFlow,
			    group.getId());
			mdrtbPatientProgram.setClassificationAccordingToPreviousTreatment(groupState);
		}
		catch (Exception e) {}
		try {
			ProgramWorkflow groupByDrugFlow = Context.getService(MdrtbService.class).getProgramWorkflow(
			    mdrtbPatientProgram.getPatientProgram().getProgram(),
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE)
			            .getId());
			ProgramWorkflowState groupByDrugState = Context.getService(MdrtbService.class).getProgramWorkflowState(
			    groupByDrugFlow, groupByDrug.getId());
			mdrtbPatientProgram.setClassificationAccordingToPreviousDrugUse(groupByDrugState);
		}
		catch (Exception e) {}
		
		// Update Patient Program
		Context.getProgramWorkflowService().savePatientProgram(mdrtbPatientProgram.getPatientProgram());
		
		// Update Patient if there was an outcome
		if (outcome != null
		        && (outcome.getId().intValue() == Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
		            MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID)))) {
			Patient patient = mdrtbPatientProgram.getPatient();
			if (!patient.getDead()) {
				patient.setDead(new Boolean(true));
				patient.setCauseOfDeath(tb03u.getCauseOfDeath());
			}
			Context.getPatientService().savePatient(patient);
		}
		return tb03u;
	}
	
	/**
	 * Process the {@link Form89} for DOTS-TB patients
	 * 
	 * @param form89
	 * @param location
	 * @return
	 */
	public Form89 processForm89(Form89 form89, Location location) {
		// Should have encounter property
		if (form89.getEncounter() == null) {
			throw new MdrtbAPIException("Encounter property not found!");
		}
		// Should have location property
		if (location == null && form89.getEncounter().getLocation() == null) {
			throw new MdrtbAPIException("Location not attached with Encounter!");
		}
		// If location is passed, then set it
		if (location != null) {
			form89.setLocation(location);
		}
		if (form89.getPopulationCategory() != null
		        && form89.getPopulationCategory().getId().intValue() != Context.getService(MdrtbService.class)
		                .getConcept(MdrtbConcepts.FOREIGNER).getId().intValue()) {
			form89.setCountryOfOrigin(null);
		}
		if (form89.getCircumstancesOfDetection() != null
		        && form89.getCircumstancesOfDetection().getId().intValue() != Context.getService(MdrtbService.class)
		                .getConcept(MdrtbConcepts.MIGRANT).getId().intValue()) {
			form89.setCityOfOrigin(null);
			form89.setDateOfReturn(null);
		}
		if (form89.getMethodOfDetection() != null
		        && form89.getMethodOfDetection().getId().intValue() != Context.getService(MdrtbService.class)
		                .getConcept(MdrtbConcepts.OTHER).getId().intValue()) {
			form89.setOtherMethodOfDetection(null);
		}
		// Save the actual update
		Context.getEncounterService().saveEncounter(form89.getEncounter());
		return form89;
	}
	
	public AdverseEventsForm processAdverseEventsForm(AdverseEventsForm aeForm) {
		if (aeForm.getTypeOfEvent() != null) {
			if (aeForm.getTypeOfEvent().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SERIOUS))) {
				aeForm.setTypeOfSpecialEvent(null);
			} else if (aeForm.getTypeOfEvent().equals(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_SPECIAL_INTEREST))) {
				aeForm.setTypeOfSAE(null);
			}
		} else {
			aeForm.setTypeOfSpecialEvent(null);
			aeForm.setTypeOfSAE(null);
		}
		if (aeForm.getCausalityDrug1() == null) {
			aeForm.setCausalityAssessmentResult1(null);
		}
		if (aeForm.getCausalityDrug2() == null) {
			aeForm.setCausalityAssessmentResult2(null);
		}
		if (aeForm.getCausalityDrug3() == null) {
			aeForm.setCausalityAssessmentResult3(null);
		}
		if (aeForm.getActionOutcome() != null) {
			Concept notResolving = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_RESOLVED);
			Concept resolving = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESOLVING);
			if (aeForm.getActionOutcome().equals(resolving) || aeForm.getActionOutcome().equals(notResolving)) {
				aeForm.setOutcomeDate(null);
			}
		}
		// save the actual update
		Context.getEncounterService().saveEncounter(aeForm.getEncounter());
		return aeForm;
	}
	
	public RegimenForm processRegimenForm(RegimenForm regimenForm) {
		if (regimenForm.getSldRegimenType() != null
		        && regimenForm.getSldRegimenType().getId().intValue() != Context.getService(MdrtbService.class)
		                .getConcept(MdrtbConcepts.OTHER_MDRTB_REGIMEN).getId().intValue()) {
			regimenForm.setOtherRegimen(null);
		}
		// save the actual update
		Context.getEncounterService().saveEncounter(regimenForm.getEncounter());
		return regimenForm;
		
	}
	
	public DrugResistanceDuringTreatmentForm processDrugResistanceDuringTreatmentForm(
	        DrugResistanceDuringTreatmentForm drugResistanceForm) {
		Context.getEncounterService().saveEncounter(drugResistanceForm.getEncounter());
		return drugResistanceForm;
	}
}
