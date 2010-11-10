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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * Outcome Report which reports on patient outcome by registration group
 */
public class OutcomeReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return "Outcomes Report";
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return "Report on patient outcome by Category IV registration group for patients who started treatment during a given time period";
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", "Facility", Location.class));
		l.add(new Parameter("year", "Year of treatment start", Integer.class));
		l.add(new Parameter("quarter", "Quarter (optional)", Integer.class));
		l.add(new Parameter("month", "Month (optional)", Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML Report", "org/openmrs/module/mdrtb/reporting/data/output/OutcomeReport.html"));
		return l;
	}
	
	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer) parameters.get("year");
		Integer quarter = (Integer) parameters.get("quarter");
		Integer month = (Integer) parameters.get("month");
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, month));
		
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		Date today = new Date();
		
		// Base Cohort is patients who started treatment during year, optionally at location
		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("startedTreatment", new Mapped(Cohorts.getStartedTreatmentFilter(startDate, endDate), null));
		if (location != null) {
			CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
			if (locationFilter != null) {
				baseCohortDefs.put("location", new Mapped(locationFilter, null));
			}
		}
		CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
		report.setBaseCohortDefinition(baseCohort, null);
		
		CohortCrossTabDataSetDefinition dsd = new CohortCrossTabDataSetDefinition();
		
		CohortDefinition newPatient = Cohorts.getMdrtbPatientProgramStateFilter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE), 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW_MDR_TB_PATIENT), startDate, endDate);
		
		// TODO: what does this do?
		CohortDefinition prevNew = ReportUtil.minus(newPatient, Cohorts.getNewExtrapulmonaryFilter());
		CohortDefinition relapse = Cohorts.getPrevRelapseFilter();
		CohortDefinition prevDef = Cohorts.getPrevDefaultFilter();
		CohortDefinition cat1 = Cohorts.getPrevFailureCatIFilter();
		CohortDefinition cat2 = Cohorts.getPrevFailureCatIIFilter();
		CohortDefinition extra = Cohorts.getNewExtrapulmonaryFilter();
		
		dsd.addRow("New", prevNew, null);
		dsd.addRow("Relapse", relapse, null);
		dsd.addRow("AfterDefault", prevDef, null);
		dsd.addRow("AfterFailureCategoryI", cat1, null);
		dsd.addRow("AfterFailureCategoryII", cat2, null);
		dsd.addRow("NewExtraPulmonary", extra, null);
		dsd.addRow("Other", ReportUtil.minus(baseCohort, prevNew, relapse, prevDef, cat1, cat2, extra), null);
		dsd.addRow("Total", baseCohort, null);
		
		CohortDefinition cured = Cohorts.getCuredDuringFilter(null, today);
		CohortDefinition complete = Cohorts.getTreatmentCompletedDuringFilter(null, today);
		CohortDefinition failed = Cohorts.getFailedDuringFilter(null, today);
		CohortDefinition defaulted = Cohorts.getDefaultedDuringFilter(null, today);
		CohortDefinition died = Cohorts.getDiedDuringFilter(null, today);
		CohortDefinition transferred = Cohorts.getTransferredDuringFilter(null, today);
		
		dsd.addColumn("Cured", ReportUtil.getCompositionCohort("AND", baseCohort, cured), null);
		dsd.addColumn("TreatmentCompleted", ReportUtil.getCompositionCohort("AND", baseCohort, complete), null);
		dsd.addColumn("Failed", ReportUtil.getCompositionCohort("AND", baseCohort, failed), null);
		dsd.addColumn("Defaulted", ReportUtil.getCompositionCohort("AND", baseCohort, defaulted), null);
		dsd.addColumn("Died", ReportUtil.getCompositionCohort("AND", baseCohort, died), null);
		dsd.addColumn("TransferredOut", ReportUtil.getCompositionCohort("AND", baseCohort, transferred), null);
		dsd.addColumn("StillOnTreatment", ReportUtil.minus(baseCohort, cured, complete, failed, defaulted, died, transferred), null);
		dsd.addColumn("Total", baseCohort, null);
		report.addDataSetDefinition("Treatment results", dsd, null);
		
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		return data;
	}
}