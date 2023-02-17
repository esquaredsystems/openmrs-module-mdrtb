/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb.reporting.definition.custom;

import java.util.Collection;
import java.util.Iterator;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.MdrtbQueryService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbTJKPatientDistrictCohortDefinition and produces a Cohort
 */
@Handler(supports = { MdrtbTJKPatientDistrictCohortDefinition.class })
public class MdrtbTJKPatientDistrictCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public MdrtbTJKPatientDistrictCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 * @should return patients whose first TB regimen was during the passed period with the given
	 *         Rayon in their address
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		Cohort fc = new Cohort();
		MdrtbTJKPatientDistrictCohortDefinition cd = (MdrtbTJKPatientDistrictCohortDefinition) cohortDefinition;
		Concept tbDrugSet = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS);
		Cohort treatmentStartCohort = MdrtbQueryService.getPatientsFirstStartingDrugs(context, cd.getFromDate(),
		    cd.getToDate(), tbDrugSet);
		
		if (treatmentStartCohort.isEmpty())
			return new EvaluatedCohort(treatmentStartCohort, cohortDefinition, context);
		
		Collection<CohortMembership> tscIdSet = treatmentStartCohort.getMemberships();
		Iterator<CohortMembership> itr = tscIdSet.iterator();
		Integer idCheck = null;
		Patient patient = null;
		PersonAddress addr = null;
		PatientService ps = Context.getService(PatientService.class);
		while (itr.hasNext()) {
			idCheck = itr.next().getCohortMemberId();
			patient = ps.getPatient(idCheck);
			addr = patient.getPersonAddress();
			// Committed after upgrade to v2.0
			//			if (MdrtbUtil.areRussianStringsEqual(addr.getCountyDistrict(), cd.getDistrict())) {
			//				fc.addMember(idCheck);
			//			}
			if (addr.getCountyDistrict().equalsIgnoreCase(cd.getDistrict())) {
				fc.addMember(idCheck);
			}
		}
		
		return new EvaluatedCohort(fc, cohortDefinition, context);
		
	}
}
