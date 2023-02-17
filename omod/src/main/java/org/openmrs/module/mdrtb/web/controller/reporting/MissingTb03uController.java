package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
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

@SuppressWarnings("unused")
@Controller
public class MissingTb03uController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/missingTb03u")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
		}
		
		else if (district == null) {
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			} else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("facilities", facilities);
			}
		}
		model.addAttribute("yearSelected", year);
		model.addAttribute("monthSelected", month);
		model.addAttribute("quarterSelected", quarter);
		return new ModelAndView("/module/mdrtb/reporting/missingTb03u", model);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/missingTb03u")
	public static String doDQ(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		SimpleDateFormat rdateSDF = Context.getDateTimeFormat();
		
		List<DQItem> missingTB03 = new ArrayList<DQItem>();
		List<Patient> errList = new ArrayList<Patient>();
		
		Boolean errorFlag = Boolean.FALSE;
		Integer errorCount = 0;
		
		Date treatmentStartDate = null;
		Calendar tCal = null;
		Calendar nowCal = null;
		long timeDiff = 0;
		double diffInWeeks = 0;
		
		DQItem dqi = null;
		
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		List<Location> locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		List<TB03uForm> tb03uList = Context.getService(MdrtbService.class)
		        .getTB03uFormsFilled(locList, year, quarter, month);
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		Integer countNum = 0;
		
		for (TB03uForm tf : tb03uList) {
			
			//INIT
			treatmentStartDate = null;
			tCal = null;
			nowCal = null;
			timeDiff = 0;
			diffInWeeks = 0;
			
			errorFlag = Boolean.FALSE;
			
			dqi = new DQItem();
			Patient patient = tf.getPatient();
			
			if (patient == null || patient.getVoided()) {
				continue;
			}
			//patientList.add(patient);
			dqi.setPatient(patient);
			dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
		}
		
		MdrtbPatientProgram temp = null;
		
		List<MdrtbPatientProgram> progList = Context.getService(MdrtbService.class)
		        .getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(locList, startDate, endDate);
		//Integer countNum = 0;
		Boolean matched = Boolean.FALSE;
		if (progList != null) {
			for (MdrtbPatientProgram p : progList) {
				matched = Boolean.FALSE;
				dqi = new DQItem();
				Patient patient = p.getPatient();//Context.getPatientService().getPatient(i);
				
				if (patient == null || patient.getVoided()) {
					continue;
				}
				
				for (TB03uForm t3f : tb03uList) {
					Patient tempPat = t3f.getPatient();
					
					if (tempPat.getId().intValue() == patient.getId().intValue()) {
						matched = Boolean.TRUE;
						break;
					}
					
				}
				
				if (!matched)
					countNum++;
				
				// patientList.add(patient);
				dqi.setPatient(patient);
				dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
				
				List<TB03uForm> x = Context.getService(MdrtbService.class).getTB03uFormsForProgram(patient, p.getId());
				
				if (x == null || x.size() == 0) {
					//errorFlag = Boolean.TRUE;
					missingTB03.add(dqi);
					
					if (!errList.contains(patient)) {
						errorCount++;
						errList.add(patient);
					}
					
				}
			}
		}
		
		Integer num = countNum;
		
		Integer errorPercentage = null;
		if (num == 0)
			errorPercentage = 0;
		else
			errorPercentage = (errorCount * 100) / num;
		
		String oName = null;
		Region obl = Context.getService(MdrtbService.class).getRegion(oblastId);
		if (obl != null)
			oName = obl.getName();
		
		String dName = null;
		if (districtId != null) {
			District dist = Context.getService(MdrtbService.class).getDistrict(districtId);
			if (dist != null)
				dName = dist.getName();
		}
		
		String fName = null;
		if (facilityId != null) {
			Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
			if (fac != null)
				fName = fac.getName();
		}
		
		model.addAttribute("num", num);
		model.addAttribute("missingTB03", missingTB03);
		
		model.addAttribute("errorCount", new Integer(errorCount));
		model.addAttribute("errorPercentage", errorPercentage.toString() + "%");
		model.addAttribute("oblastName", oName);
		model.addAttribute("oName", oName);
		model.addAttribute("dName", dName);
		model.addAttribute("fName", fName);
		
		model.addAttribute("locale", Context.getLocale().toString());
		
		// TO CHECK WHETHER REPORT IS CLOSED OR NOT
		boolean reportStatus = Context.getService(MdrtbService.class).readReportStatus(oblastId, districtId, facilityId,
		    year, quarter, month, "DQ", "MDRTB");
		System.out.println(reportStatus);
		
		model.addAttribute("oblast", oblastId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("district", districtId);
		model.addAttribute("year", year);
		if (month != null && month.length() != 0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && quarter.length() != 0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		model.addAttribute("reportDate", rdateSDF.format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		return "/module/mdrtb/reporting/missingTb03uResults";
	}
	
}
