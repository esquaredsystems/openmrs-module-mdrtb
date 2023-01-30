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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DstResultCohortDefinition and produces a Cohort
 */
@Handler(supports = { TB03UCohortDefinition.class })
public class TB03UCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public TB03UCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionExistsEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		TB03UCohortDefinition tcd = (TB03UCohortDefinition) cohortDefinition;
		Cohort c = new Cohort();
		
		EncounterType encType = MdrtbConstants.ET_TB03_TB_INTAKE;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(encType);
		
		Date startDate = tcd.getOnOrAfter();
		Date tcdEnd = tcd.getOnOrBefore();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(tcdEnd.getTime());
		gc.add(Calendar.DATE, 1);
		Date endDate = gc.getTime();
		List<Encounter> tb03EncList = Context.getService(MdrtbService.class).getEncounters(null, tcd.getLocation(),
		    startDate, endDate, typeList);
		System.out.println("Finding patients for Location:  " + tcd.getLocation());
		for (int i = 0; i < tb03EncList.size(); i++) {
			if (!Context.getPatientService().getPatient(tb03EncList.get(i).getPatient().getPatientId()).getVoided())
				c.addMember(tb03EncList.get(i).getPatient().getPatientId());
		}
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
