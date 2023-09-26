package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.PDFHelper;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewClosedReportsController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/viewClosedReports")
	public void viewClosedReportsGet(@RequestParam(required = true, value = "type") String reportType, ModelMap model) {
		List<List<Integer>> closedReports = Context.getService(MdrtbService.class).getPDFRows(reportType);
		List<Integer> reportIds = closedReports.get(0);
		List<Integer> oblastIds = closedReports.get(1);
		List<Integer> districtIds = closedReports.get(2);
		List<Integer> facilityIds = closedReports.get(3);
		List<Integer> years = closedReports.get(4);
		List<Integer> quarters = closedReports.get(5);
		List<Integer> months = closedReports.get(6);
		List<Integer> reportDates = closedReports.get(7);
		List<Integer> reportStatuses = closedReports.get(8);
		List<Integer> reportNames = closedReports.get(9);
		System.out.println("TYPE:" + reportType);
		System.out.println("SIZE:" + closedReports.size());
		
		List<Region> oblasts = new ArrayList<Region>();
		List<District> districts = new ArrayList<District>();
		List<Facility> facilities = new ArrayList<Facility>();
		
		for (Integer oblastId : oblastIds) {
			oblasts.add(Context.getService(MdrtbService.class).getRegion(oblastId));
		}
		for (Integer districtId : districtIds) {
			if (districtId != null) {
				districts.add(Context.getService(MdrtbService.class).getDistrict(districtId));
			} else {
				districts.add(null);
			}
		}
		
		for (Integer facilityId : facilityIds) {
			if (facilityId != null) {
				facilities.add(Context.getService(MdrtbService.class).getFacility(facilityId));
			} else {
				facilities.add(null);
			}
		}
		
		//List<Location> locations = Context.getLocationService().getAllLocations(false);
		List<Region> o = Context.getService(MdrtbService.class).getRegions();
		List<List<Location>> oblastLocations = new ArrayList<List<Location>>();
		for (Region oblast : o) {
			List<Location> l = Context.getService(MdrtbService.class).getLocationsFromRegion(oblast);
			oblastLocations.add(l);
		}
		
		model.addAttribute("closedReports", closedReports);
		model.addAttribute("reportIds", reportIds);
		model.addAttribute("oblastIds", oblastIds);
		model.addAttribute("districtIds", districtIds);
		model.addAttribute("facilityIds", facilityIds);
		model.addAttribute("years", years);
		model.addAttribute("quarters", quarters);
		model.addAttribute("months", months);
		model.addAttribute("reportDates", reportDates);
		model.addAttribute("reportStatuses", reportStatuses);
		model.addAttribute("reportNames", reportNames);
		
		model.addAttribute("reportOblasts", oblasts);
		model.addAttribute("reportDistricts", districts);
		model.addAttribute("reportFacilities", facilities);
		//model.addAttribute("reportLocations", locations);
		model.addAttribute("oblasts", o);
		model.addAttribute("oblastLocations", oblastLocations);
		model.addAttribute("reportType", reportType);
	}
	
	@RequestMapping(method = RequestMethod.POST) //, value="/module/mdrtb/reporting/viewClosedReports")
	public ModelAndView viewClosedReportsPost(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("district") Integer districtId,
	        @RequestParam("facility") Integer facilityId, @RequestParam("year") Integer year,
	        @RequestParam("quarter") String q, @RequestParam("month") String m,
	        @RequestParam("reportName") String reportName, @RequestParam("reportDate") String reportDate,
	        @RequestParam("formAction") String formAction, @RequestParam("reportType") String reportTypeStr, ModelMap model)
	        throws EvaluationException {
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		Integer quarter = q == null ? null : Integer.parseInt(q.replace("\"", ""));
		Integer month = (m != null && m.length() != 0) ? null : Integer.parseInt(m.replace("\"", ""));
		String html = "";
		String returnStr = "";
		try {
			if (formAction.equals("unlock")) {
				System.out.println("-----UNLOCK-----");
				Context.getService(MdrtbService.class).saveScannedLabReport(null);
				ReportData reportData = new ReportData();
				reportData.setLocation(facility);
				reportData.setYear(year);
				reportData.setMonth(month);
				reportData.setQuarter(quarter);
				reportData.setReportName(reportName);
				
				Context.getService(MdrtbService.class).unlockReport(reportData);
				viewClosedReportsGet(reportTypeStr, model);
				returnStr = "/module/mdrtb/reporting/viewClosedReports";
			} else if (formAction.equals("view")) {
				System.out.println("-----VIEW-----");
				ReportType reportType = reportTypeStr == null ? null : ReportType.valueOf(reportTypeStr.toUpperCase());
				List<String> allReports = Context.getService(MdrtbService.class).readTableData(oblastId, districtId,
				    facilityId, year, quarter, month, reportName.replaceAll(" ", "_").toUpperCase(), reportType);
				
				if (allReports.isEmpty() && allReports.size() == 0) {
					html = "<p>No Data Found</p>";
				} else {
					html = new PDFHelper().decompressCode(allReports.get(0));
				}
				model.addAttribute("html", html);
				model.addAttribute("oblast", oblastId);
				model.addAttribute("district", districtId);
				model.addAttribute("facility", facility);
				model.addAttribute("year", year);
				model.addAttribute("quarter", quarter);
				model.addAttribute("month", month);
				model.addAttribute("reportName", reportName.replaceAll("_", " ").toUpperCase());
				model.addAttribute("reportDate", reportDate);
				model.addAttribute("formAction", formAction);
				returnStr = "/module/mdrtb/reporting/viewClosedReportContent";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("ex", e);
		}
		return new ModelAndView(returnStr, model);
	}
}
