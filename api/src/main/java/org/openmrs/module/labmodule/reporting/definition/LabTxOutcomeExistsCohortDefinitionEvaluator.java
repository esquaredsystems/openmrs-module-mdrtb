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
package org.openmrs.module.labmodule.reporting.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.MdrtbConstants.TbClassification;
import org.openmrs.module.labmodule.reporting.MdrtbQueryService;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DstResultCohortDefinition and produces a Cohort
 */
@Handler(supports = { LabTxOutcomeExistsCohortDefinition.class })
public class LabTxOutcomeExistsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public LabTxOutcomeExistsCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionExistsEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		LabTxOutcomeExistsCohortDefinition cd = (LabTxOutcomeExistsCohortDefinition) cohortDefinition;
		//Cohort c = null;
		
		Cohort c = MdrtbQueryService.getPatientsWithTxOutcome(context, null, null, cd.getOutcomeType());
		
    	return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
