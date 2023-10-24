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
package org.openmrs.module.mdrtb.reporting.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.definition.DstResultCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbBacResultAfterTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbPatientProgramStateCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbProgramClosedAfterTreatmentStartedCohortDefintion;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbProgramLocationCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.AgeAtMDRRegistrationCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.AgetAtMdrtbProgramEnrollmentTJKCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.MdrtbAfterTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.MdrtbPreviousProgramOutcomeCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.MdrtbTJKPatientDistrictCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.ResistanceTypeCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.SLDRegimenTypeCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.TB03UCohortDefinition;
import org.openmrs.module.mdrtb.reporting.definition.custom.TestReferralCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;

/**
 * Cohort methods
 */
public class Cohorts {
	
	/**
	 * @return the CohortDefinition for the Location
	 */
	public static CohortDefinition getLocationFilter(Location location, Date startDate, Date endDate) {
		if (location != null) {
			MdrtbProgramLocationCohortDefinition cd = new MdrtbProgramLocationCohortDefinition();
			cd.setLocation(location);
			cd.setStartDate(startDate);
			cd.setEndDate(endDate);
			
			return cd;
		}
		
		return null;
	}
	
	public static CohortDefinition getEnteredStateDuringFilter(Integer stateId, Date startDate, Date endDate) {
		ProgramWorkflowState programWorkflowState = null;
		//TODO: Context.getProgramWorkflowService().getState(stateId);
		return getEnteredStateDuringFilter(programWorkflowState, startDate, endDate);
	}
	
	public static CohortDefinition getEnteredStateDuringFilter(String stateUuid, Date startDate, Date endDate) {
		ProgramWorkflowState programWorkflowState = Context.getProgramWorkflowService().getStateByUuid(stateUuid);
		return getEnteredStateDuringFilter(programWorkflowState, startDate, endDate);
	}
	
	public static CohortDefinition getEnteredStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrAfter(startDate);
		cd.setStartedOnOrBefore(endDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getInStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getInStateFilter(ProgramWorkflowState state, Date onDate) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setOnDate(onDate);
		cd.setStates(Arrays.asList(state));
		return cd;
	}
	
	public static CohortDefinition getNotInStateDuringFilter(ProgramWorkflowState state, Date startDate, Date endDate) {
		return new InverseCohortDefinition(getInStateDuringFilter(state, startDate, endDate));
	}
	
	public static CohortDefinition getMdrtbPatientProgramStateFilter(Concept workflowConcept, List<Concept> stateConcepts,
	        Date startDate, Date endDate) {
		MdrtbPatientProgramStateCohortDefinition cd = new MdrtbPatientProgramStateCohortDefinition();
		cd.setWorkflowConcept(workflowConcept);
		cd.setStateConcepts(stateConcepts);
		cd.setStartDate(startDate);
		cd.setEndDate(endDate);
		return cd;
	}
	
	public static CohortDefinition getMdrtbPatientProgramStateFilter(Concept workflowConcept, Concept stateConcept,
	        Date startDate, Date endDate) {
		List<Concept> stateConcepts = new ArrayList<>();
		if (stateConcept != null)
			stateConcepts.add(stateConcept);
		
		return getMdrtbPatientProgramStateFilter(workflowConcept, stateConcepts, startDate, endDate);
	}
	
	public static CohortDefinition getCuredDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED)), startDate, endDate);
	}
	
	public static CohortDefinition getTreatmentCompletedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE)), startDate, endDate);
	}
	
	public static CohortDefinition getFailedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_FAILED)), startDate, endDate);
	}
	
	public static CohortDefinition getDefaultedDuringFilter(Date startDate, Date endDate) {
		//TODO: MdrtbConcepts.DEFAULTED was changed to MdrtbConcepts.LOST_TO_FOLLOWUP. Ensure there's an upgrade script to migrate existing data
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOST_TO_FOLLOWUP)), startDate, endDate);
	}
	
	public static CohortDefinition getDiedDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED)), startDate, endDate);
	}
	
	public static CohortDefinition getTransferredDuringFilter(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_OUT)), startDate,
		    endDate);
	}
	
	public static CohortDefinition getRelapsedDuringFilter(Date startDate, Date endDate) {
		//TODO: MdrtbConcepts.RELAPSE was changed to MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1. Ensure there's an upgrade script to migrate existing data
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1)), startDate,
		    endDate);
		
	}
	
	public static CohortDefinition getPolydrDetectionFilter(Date startDate, Date endDate) {
		DstResultCohortDefinition polydrPats = new DstResultCohortDefinition();
		polydrPats.setTbClassification(TbClassification.POLY_RESISTANT_TB);
		polydrPats.setMinResultDate(startDate);
		polydrPats.setMaxResultDate(endDate);
		return polydrPats;
	}
	
	public static CohortDefinition getMdrDetectionFilter(Date startDate, Date endDate) {
		DstResultCohortDefinition mdrPats = new DstResultCohortDefinition();
		mdrPats.setTbClassification(TbClassification.MDR_TB);
		mdrPats.setMinResultDate(startDate);
		mdrPats.setMaxResultDate(endDate);
		return mdrPats;
	}
	
	public static CohortDefinition getXdrDetectionFilter(Date startDate, Date endDate) {
		DstResultCohortDefinition xdrPats = new DstResultCohortDefinition();
		xdrPats.setTbClassification(TbClassification.XDR_TB);
		xdrPats.setMinResultDate(startDate);
		xdrPats.setMaxResultDate(endDate);
		return xdrPats;
	}
	
	public static CohortDefinition getStartedTreatmentFilter(Date startDate, Date endDate) {
		MdrtbTreatmentStartedCohortDefinition startedTreatmentCohort = new MdrtbTreatmentStartedCohortDefinition();
		startedTreatmentCohort.setFromDate(startDate);
		startedTreatmentCohort.setToDate(endDate);
		return startedTreatmentCohort;
	}
	
	public static CohortDefinition getProgramClosedAfterTreatmentStartedFilter(Date startDate, Date endDate,
	        Integer monthsFromTreatmentStart) {
		MdrtbProgramClosedAfterTreatmentStartedCohortDefintion programClosedAfterTreatmentStartedCohort = new MdrtbProgramClosedAfterTreatmentStartedCohortDefintion();
		programClosedAfterTreatmentStartedCohort.setFromDate(startDate);
		programClosedAfterTreatmentStartedCohort.setToDate(endDate);
		programClosedAfterTreatmentStartedCohort.setMonthsFromTreatmentStart(monthsFromTreatmentStart);
		return programClosedAfterTreatmentStartedCohort;
	}
	
	public static CohortDefinition getConfirmedMdrInProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("inMdrProgram", getInMdrProgramEverDuring(startDate, endDate), null);
		confirmed.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("inMdrProgram AND detectedWithMDR AND startedTreatment");
		return confirmed;
	}
	
	public static CohortDefinition getSuspectedMdrInProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition suspected = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		suspected.addSearch("inMdrProgram", getInMdrProgramEverDuring(startDate, endDate), null);
		suspected.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		suspected.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		suspected.setCompositionString("inMdrProgram AND (NOT detectedWithMDR) AND startedTreatment");
		return suspected;
	}
	
	public static CohortDefinition getNewlyHospitalizedDuringPeriod(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED)), startDate, endDate);
	}
	
	public static CohortDefinition getEverHospitalizedDuringPeriod(Date startDate, Date endDate) {
		return getInStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED)), startDate, endDate);
	}
	
	public static CohortDefinition getMostRecentlyAmbulatoryByEnd(Date startDate, Date endDate) {
		return getNotInStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_HOSPITALIZED)), startDate, endDate);
	}
	
	public static CohortDefinition getHivPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY, 3753, startDate, endDate, SetComparator.IN, Context
		        .getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE).getId());
	}
	
	public static CohortDefinition getNewlyHivPositive(Date startDate, Date endDate) {
		CohortDefinition byStart = getHivPositiveDuring(null, startDate);
		CohortDefinition byEnd = getHivPositiveDuring(null, endDate);
		return ReportUtil.minus(byEnd, byStart);
	}
	
	public static CohortDefinition getTransferredInDuring(Date startDate, Date endDate) {
		return getEnteredStateDuringFilter(
		    Context.getService(MdrtbService.class).getProgramWorkflowState(
		        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN)), startDate, endDate);
	}
	
	public static CohortDefinition getMostRecentlySmearPositiveByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT).getId(), null, effectiveDate,
		    SetComparator.IN, MdrtbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getAnySmearPositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT).getId(), startDate, endDate,
		    SetComparator.IN, MdrtbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getMostRecentlySmearNegativeByDate(Date effectiveDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.LAST,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT).getId(), null, effectiveDate,
		    SetComparator.IN, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE).getId());
	}
	
	public static CohortDefinition getAllSmearNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SMEAR_RESULT).getId(), startDate, endDate, SetComparator.IN,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE).getId()), null);
		cd.addSearch("anyPositive", getAnySmearPositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getAnyCulturePositiveDuring(Date startDate, Date endDate) {
		return ReportUtil.getCodedObsCohort(TimeModifier.ANY,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT).getId(), startDate, endDate,
		    SetComparator.IN, MdrtbUtil.getPositiveResultConceptIds());
	}
	
	public static CohortDefinition getAllCultureNegativeDuring(Date startDate, Date endDate) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addSearch("anyNegative", ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.CULTURE_RESULT).getId(), startDate, endDate, SetComparator.IN,
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE).getId()), null);
		cd.addSearch("anyPositive", getAnyCulturePositiveDuring(startDate, endDate), null);
		cd.setCompositionString("anyNegative AND (NOT anyPositive)");
		return cd;
	}
	
	public static CohortDefinition getFirstCulturePositiveDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o, (select person_id, concept_id, min(obs_datetime) as obs_datetime from obs where concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT).getId()
		        + " group by person_id) d ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT).getId() + " ");
		q.append("and		o.obs_datetime = d.obs_datetime ");
		q.append("and		o.value_coded in (" + convertIntegerSetToString(MdrtbUtil.getPositiveResultConceptIds()) + ") ");
		if (startDate != null) {
			q.append("and	o.obs_datetime >= '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime <= '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getInMdrProgramEverDuring(Date startDate, Date endDate) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME))));
		cd.setOnOrAfter(startDate);
		cd.setOnOrBefore(endDate);
		return cd;
	}
	
	public static CohortDefinition getPendingCulturesOnDate(Date effectiveDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT) + " ");
		//	q.append("and       o.voided='0' ");
		q.append("and		o.obs_id not in ");
		q.append("		(select obs_group_id from obs ");
		q.append("		 where concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_RESULT_DATE)
		        + " and value_datetime <= '" + DateUtil.formatDate(effectiveDate, "yyyy-MM-dd") + "') ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getMdrtbBacResultAfterTreatmentStart(Date startDate, Date endDate,
	        Integer fromTreatmentMonth, Integer toTreatmentMonth, Result overallResult) {
		MdrtbBacResultAfterTreatmentStartedCohortDefinition cd = new MdrtbBacResultAfterTreatmentStartedCohortDefinition();
		cd.setFromDate(startDate);
		cd.setToDate(endDate);
		cd.setFromTreatmentMonth(fromTreatmentMonth);
		cd.setToTreatmentMonth(toTreatmentMonth);
		cd.setOverallResult(overallResult);
		return cd;
	}
	
	/**
	 * Utility methods
	 */
	private static String convertIntegerSetToString(Integer[] set) {
		StringBuilder result = new StringBuilder();
		for (Integer integer : set) {
			result.append(integer + ",");
		}
		
		// remove the trailing comma
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	/****** CUSTOM METHODS ******/
	public static CohortDefinition getResistanceTypeFilter(Date startDate, Date endDate, TbClassification rType) {
		ResistanceTypeCohortDefinition rtcd = new ResistanceTypeCohortDefinition();
		rtcd.setResistanceType(rType);
		rtcd.setStartDate(startDate);
		rtcd.setEndDate(endDate);
		return rtcd;
	}
	
	public static CohortDefinition getPatientsReferredToTestFilter(Date fromDate, Date toDate, String testType) {
		TestReferralCohortDefinition testReferredCohort = new TestReferralCohortDefinition();
		testReferredCohort.setFromDate(fromDate);
		testReferredCohort.setToDate(toDate);
		testReferredCohort.setTestType(testType);
		
		return testReferredCohort;
	}
	
	public static CohortDefinition getConfirmedMdrOnlyInProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("inMdrProgram", getInMdrProgramEverDuring(startDate, endDate), null);
		confirmed.addSearch("detectedWithMDR", getMdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("inMdrProgram AND detectedWithMDR AND startedTreatment");
		return ReportUtil.minus(confirmed, getConfirmedXdrInProgramAndStartedTreatmentFilter(startDate, endDate));
	}
	
	public static CohortDefinition getConfirmedXdrInProgramAndStartedTreatmentFilter(Date startDate, Date endDate) {
		CompositionCohortDefinition confirmed = new CompositionCohortDefinition();
		
		// TODO: this does not yet handle patient programs/relapses properly, as the MDR Detection filter start date is set to null,
		// if a patient has multiple programs, it really should be set to the end date of the most recent previous program
		confirmed.addSearch("inMdrProgram", getInMdrProgramEverDuring(startDate, endDate), null);
		confirmed.addSearch("detectedWithXDR", getXdrDetectionFilter(null, endDate), null);
		confirmed.addSearch("startedTreatment", getStartedTreatmentFilter(startDate, endDate), null);
		confirmed.setCompositionString("inMdrProgram AND detectedWithXDR AND startedTreatment");
		return confirmed;
	}
	
	public static CohortDefinition getAllPulmonaryEver() {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		q.append("and		o.value_coded = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB).getId() + " ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAllExtraPulmonaryEver() {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		q.append("and		o.value_coded = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getId() + " ");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getMdrtbAfterTreatmentStart(Date startDate, Date endDate, Integer fromTreatmentMonth,
	        Integer toTreatmentMonth) {
		MdrtbAfterTreatmentStartedCohortDefinition cd = new MdrtbAfterTreatmentStartedCohortDefinition();
		cd.setFromDate(startDate);
		cd.setToDate(endDate);
		cd.setFromTreatmentMonth(fromTreatmentMonth);
		cd.setToTreatmentMonth(toTreatmentMonth);
		
		return cd;
	}
	
	public static CohortDefinition getPreviousMdrtbProgramOutcome(Date startDate, Date endDate, String outcome) {
		MdrtbPreviousProgramOutcomeCohortDefinition cd = new MdrtbPreviousProgramOutcomeCohortDefinition();
		cd.setFromDate(startDate);
		cd.setToDate(endDate);
		cd.setOutcome(outcome);
		
		return cd;
	}
	
	public static CohortDefinition getEnrolledInMDRProgramDuring(Date startDate, Date endDate) {
		ProgramEnrollmentCohortDefinition pd = new ProgramEnrollmentCohortDefinition();
		pd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME))));
		pd.setEnrolledOnOrAfter(startDate);
		pd.setEnrolledOnOrBefore(endDate);
		return pd;
	}
	
	/**
	 * @return the CohortDefinition for the Address and date
	 */
	public static CohortDefinition getTreatmentStartAndAddressFilterTJK(String rayon, Date fromDate, Date toDate) {
		if (rayon != null) {
			MdrtbTJKPatientDistrictCohortDefinition cd = new MdrtbTJKPatientDistrictCohortDefinition();
			cd.setDistrict(rayon);
			cd.setFromDate(fromDate);
			cd.setToDate(toDate);
			
			return cd;
		}
		
		return null;
	}
	
	public static CohortDefinition getPatientsWithDistict(Location l) {
		StringBuilder q = new StringBuilder();
		q.append("select 	person_id ");
		q.append("from		person_address ");
		q.append("where		county_district = '" + l.getCountyDistrict() + "'");
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAgeAtEnrollmentInMdrtbProgram(Date startDate, Date endDate, Integer minAge,
	        Integer maxAge) {
		AgetAtMdrtbProgramEnrollmentTJKCohortDefinition cd = new AgetAtMdrtbProgramEnrollmentTJKCohortDefinition();
		cd.setEnrolledOnOrAfter(startDate);
		cd.setEnrolledOnOrBefore(endDate);
		cd.setMinAge(minAge);
		cd.setMaxAge(maxAge);
		cd.setPrograms(Arrays.asList(Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME))));
		return cd;
	}
	
	public static CohortDefinition getMalePatients() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setMaleIncluded(true);
		cd.setFemaleIncluded(false);
		cd.setUnknownGenderIncluded(false);
		
		return cd;
	}
	
	public static CohortDefinition getFemalePatients() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setMaleIncluded(false);
		cd.setFemaleIncluded(true);
		cd.setUnknownGenderIncluded(false);
		return cd;
	}
	
	public static CohortDefinition getAgeAtRegistration(Date startDate, Date endDate, Integer minAge, Integer maxAge) {
		AgeAtMDRRegistrationCohortDefinition cd = new AgeAtMDRRegistrationCohortDefinition();
		cd.setStartDate(startDate);
		cd.setEndDate(endDate);
		cd.setMinAge(minAge);
		cd.setMaxAge(maxAge);
		return cd;
	}
	
	public static CohortDefinition getAllPulmonaryDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		q.append("and		o.value_coded = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB).getId() + " ");
		if (startDate != null) {
			q.append("and	o.obs_datetime >= '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime <= '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getAllExtraPulmonaryDuring(Date startDate, Date endDate) {
		StringBuilder q = new StringBuilder();
		q.append("select 	o.person_id ");
		q.append("from		obs o ");
		q.append("where		o.concept_id = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getId() + " ");
		q.append("and		o.value_coded = "
		        + Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getId() + " ");
		if (startDate != null) {
			q.append("and	o.obs_datetime >= '" + DateUtil.formatDate(startDate, "yyyy-MM-dd") + "' ");
		}
		if (endDate != null) {
			q.append("and	o.obs_datetime <= '" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + "' ");
		}
		return new SqlCohortDefinition(q.toString());
	}
	
	public static CohortDefinition getSLDRegimenFilter(Date startDate, Date endDate, Concept regType) {
		SLDRegimenTypeCohortDefinition cd = new SLDRegimenTypeCohortDefinition();
		cd.setStartDate(startDate);
		cd.setEndDate(endDate);
		cd.setRegType(regType);
		return cd;
	}
	
	public static CohortDefinition getTB03uByDatesAndLocation(Date startDate, Date endDate, Location loc) {
		TB03UCohortDefinition tcd = new TB03UCohortDefinition();
		tcd.setLocation(loc);
		tcd.setOnOrAfter(startDate);
		tcd.setOnOrBefore(endDate);
		return tcd;
	}
}
