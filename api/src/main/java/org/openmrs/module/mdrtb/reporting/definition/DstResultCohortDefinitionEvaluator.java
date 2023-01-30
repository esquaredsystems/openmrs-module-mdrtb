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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.MdrtbQueryService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DstResultCohortDefinition and produces a Cohort
 */
@Handler(supports = { DstResultCohortDefinition.class })
public class DstResultCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public DstResultCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 * @should return confirmed poly-resistance patients for the period
	 * @should return confirmed mdrtb patients for the period
	 * @should return confirmed xdrtb patients for the period
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		DstResultCohortDefinition cd = (DstResultCohortDefinition) cohortDefinition;
		Cohort c = null;
		
		Date sd = cd.getMinResultDate();
		Date ed = cd.getMaxResultDate();
		
		if (cd.getTbClassification() == TbClassification.POLY_RESISTANT_TB) {
			// find any patients that have been resistant to two or mor drugs
			c = MdrtbQueryService.getPatientsResistantToNumberOfDrugs(context, sd, ed, 2);
		}
		
		if (cd.getTbClassification() == TbClassification.MDR_TB || cd.getTbClassification() == TbClassification.XDR_TB) {
			
			// Must be resistant to INH
			Concept inh = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID);
			c = MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, inh);
			
			// Must be resistant to RIF
			Concept rif = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN);
			c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, rif));
			
			if (cd.getTbClassification() == TbClassification.XDR_TB) {
				
				// Must be resistant to at least one fluoroquinolone
				Concept quinoloneSet = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.QUINOLONES);
				List<Concept> quinolones = new ArrayList<Concept>();
				for (ConceptSet set : quinoloneSet.getConceptSets()) {
					quinolones.add(set.getConcept());
				}
				Concept[] quins = quinolones.toArray(new Concept[quinolones.size()]);
				c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, quins));
				
				// Must be resistant to at least one of capreomycin, kanamycin, and amikacin
				Concept cap = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAPREOMYCIN);
				Concept kan = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KANAMYCIN);
				Concept amk = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AMIKACIN);
				c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, cap, kan, amk));
			}
		}
		
		if (cd.getSpecificProfile() != null) {
			Cohort profileCohort = MdrtbQueryService.getCohortWithResistanceProfile(context, ed, cd.getSpecificProfile());
			if (c == null) {
				c = profileCohort;
			} else {
				c = Cohort.union(c, profileCohort);
			}
		}
		
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
