/**
o  * The contents of this file are subject to the OpenMRS Public License
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
package org.openmrs.module.mdrtb.reporting.data.custom;

/*
 * 
 * base cohort is all MDR-TB patients.
 * Number of patients started treatment before beginning of quarter - A
Number of patients started treatment during quarter - B

Table 2 = B - A
Table 3 - A
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.data.Cohorts;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * Outcome Report which reports on patient outcome by registration group
 */
public class TB08TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb08");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb08.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location",  Context.getMessageSourceService().getMessage("mdrtb.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("mdrtb.yearOfTreatmentStart"), Integer.class));
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarterOptional"), String.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("mdrtb.monthOptional"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/TB08" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer) parameters.get("year");
		/*Integer quarter = (Integer) parameters.get("quarter");
		Integer month = (Integer) parameters.get("month");*/
		String quarter = (String) parameters.get("quarter");
		String month = (String) parameters.get("month");
		if (quarter == null && month==null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("mdrtb.error.pleaseEnterAQuarterOrMonth"));
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, Integer.parseInt(quarter), Integer.parseInt(month)));
		
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		String oblast = (String) context.getParameterValue("oblast");
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		//OBLAST
		Region o = null;
		if(!oblast.equals("") && location == null)
			o =  Context.getService(MdrtbService.class).getRegion(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if(o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromRegion(o);
		else if (location != null)
			locList.add(location);
		
		if(location != null)
			context.addParameterValue("location", location.getName()); 
		else if(o != null)
			context.addParameterValue("location", o.getName()); 
		else
			context.addParameterValue("location", "All"); 
		
		// Base Cohort is patients who started treatment during year, optionally at location
		//Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		
		//OBLAST
		if (!locList.isEmpty()){
			List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();
			for(Location loc : locList)
				cohortDefinitions.add(Cohorts.getLocationFilter(loc, startDate, endDate));
				
			if(!cohortDefinitions.isEmpty()){
				report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("OR", cohortDefinitions), null);
			}
		}
		CohortDefinition drtb = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
		CohortCrossTabDataSetDefinition dsd = new CohortCrossTabDataSetDefinition();
		
		// create the rows in the chart
		Map<String, CohortDefinition> groups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
		Map<String, CohortDefinition> outcomes = ReportUtil.getMdrtbOutcomesFilterSet(startDate, endDate);
		
		CohortDefinition cured = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("Cured"));
		CohortDefinition treatmentCompleted = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("TreatmentCompleted"));
		CohortDefinition successfulTreatment = ReportUtil.getCompositionCohort("AND", drtb, ReportUtil.getCompositionCohort("OR", cured, treatmentCompleted));
		CohortDefinition failed = ReportUtil.getCompositionCohort("AND", drtb,outcomes.get("Failed"));
		CohortDefinition ltfu = ReportUtil.getCompositionCohort("AND",drtb, outcomes.get("Defaulted"));
		CohortDefinition died = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("Died"));
		CohortDefinition stillEnrolled =  ReportUtil.getCompositionCohort("AND", drtb,ReportUtil.getCompositionCohort("OR", outcomes.get("StillEnrolled"),groups.get("TransferredOut")));
		
		CohortDefinition tbDied = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH).getId(),
				null, null, SetComparator.IN, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getId());
		
		//DIED: NON-TB
		//CohortDefinition nonTbDeath = ReportUtil.minus(diedDuringTreatment, tbDied);
		CohortDefinition nonTbDeath = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH).getId(),
				null, null, SetComparator.IN, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_OTHER_DISEASES).getId());

		tbDied = ReportUtil.getCompositionCohort("AND", died, tbDied);
		nonTbDeath = ReportUtil.getCompositionCohort("AND", died, nonTbDeath);
		
		
		//CohortDefinition previousSecondLine = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate).get("PreviousSecondLine");
		
		dsd.addRow("New", groups.get("New"),null);
		dsd.addRow("Relapse1", groups.get("Relapse1"), null);
		dsd.addRow("Relapse2", groups.get("Relapse2"), null);
		dsd.addRow("AfterDefault1", groups.get("AfterDefault1") , null);
		dsd.addRow("AfterDefault2", groups.get("AfterDefault2") , null);
		dsd.addRow("AfterFailure1",groups.get("AfterFailure1") , null);
		dsd.addRow("AfterFailure2", groups.get("AfterFailure2"), null);
		dsd.addRow("Other", ReportUtil.getCompositionCohort("OR",groups.get("Other"),groups.get("TransferredIn")), null);
		dsd.addRow("Total", drtb,null);
		
		dsd.addColumn("Registered", drtb, null);
		dsd.addColumn("Cured", cured, null);
		dsd.addColumn("TreatmentCompleted", treatmentCompleted, null);
		dsd.addColumn("SuccessfulTreatment", successfulTreatment, null);
		dsd.addColumn("Failed", failed, null);
		dsd.addColumn("Defaulted", ltfu, null);
		dsd.addColumn("DiedTB", tbDied, null);
		dsd.addColumn("DiedNonTB", nonTbDeath, null);
		dsd.addColumn("StillEnrolled", stillEnrolled, null);
		dsd.addColumn("ColTotal",drtb, null);
		
		report.addDataSetDefinition("results", dsd, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
        	throw new MdrtbAPIException("Unable to evaluate Outcomes report", e);
        }
		return data;
	}
}