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
package org.openmrs.module.mdrtb.reporting.definition;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.mdrtb.reporting.MdrtbQueryService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={MdrtbTreatmentStartedCohortDefinition.class})
public class MdrtbTreatmentStartedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public MdrtbTreatmentStartedCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients whose first TB regimen was during the passed period
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {	
    	MdrtbTreatmentStartedCohortDefinition cd = (MdrtbTreatmentStartedCohortDefinition) cohortDefinition;
    	//TODO: Figure out why getTreatmentStarted() was used instead of getPatientsFirstStartingDrugs()
//    	Cohort c = MdrtbQueryService.getPatientsFirstStartingDrugs(context, cd.getFromDate(), cd.getToDate(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
    	Cohort c = MdrtbQueryService.getTreatmentStarted(context,cd.getFromDate(),cd.getToDate());
    	return new EvaluatedCohort(c, cohortDefinition, context);

	}
}