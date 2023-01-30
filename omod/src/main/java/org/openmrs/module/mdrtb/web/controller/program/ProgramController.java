package org.openmrs.module.mdrtb.web.controller.program;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.PatientValidator;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.status.VisitStatus;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProgramController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register binders for location and program workflow state
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("classificationsAccordingToPreviousDrugUse")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousDrugUse() {
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPreviousDrugUse();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NEW));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : classificationConcepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("classificationsAccordingToPreviousDrugUseDOTS")
	public Collection<ProgramWorkflowState> getDOTSClassificationsAccordingToPreviousDrugUse() {
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NEW));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : classificationConcepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
		
	}
	
	@ModelAttribute("classificationsAccordingToPreviousTreatment")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousTreatment() {
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPreviousTreatment();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NEW));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : classificationConcepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("classificationsAccordingToPatientGroups")
	public ArrayList<ProgramWorkflowState> getClassificationsAccordingToPatientGroups() {
		ArrayList<ProgramWorkflowState> stateArray = new ArrayList<ProgramWorkflowState>();
		Set<ProgramWorkflowState> states = Context.getService(MdrtbService.class)
		        .getPossibleClassificationsAccordingToPatientGroups();
		if (states != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.NEW));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER));
			for (ProgramWorkflowState pws : states) {
				for (Concept classification : classificationConcepts) {
					if (pws.getConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("outcomes")
	public Collection<ProgramWorkflowState> getOutcomes() {
		return Context.getService(MdrtbService.class).getPossibleMdrtbProgramOutcomes();
	}
	
	@ModelAttribute("dotsIdentifier")
	public PatientIdentifierType getDotsIdentifier() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE));
	}
	
	@ModelAttribute("mdrIdentifier")
	public PatientIdentifierType getMdrIdentifier() {
		
		return Context.getPatientService().getPatientIdentifierTypeByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE));
	}
	
	@RequestMapping("/module/mdrtb/program/showEnroll.form")
	public ModelAndView showEnrollInPrograms(@RequestParam(required = false, value = "patientId") Integer patientId,
	        ModelMap map) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			throw new RuntimeException("Show enroll called with invalid patient id " + patientId);
		}
		
		// we need to determine if this patient currently in active in an mdr-tb program to determine what fields to display
		//MdrtbPatientProgram mostRecentMdrtbProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(patient);
		//TbPatientProgram mostRecentTbProgram = Context.getService(MdrtbService.class).getMostRecentTbPatientProgram(patient);
		//map.put("hasActiveProgram", ((mostRecentMdrtbProgram != null && mostRecentMdrtbProgram.getActive()) || (mostRecentTbProgram != null && mostRecentTbProgram.getActive())) ? true : false);
		map.put("patientId", patientId);
		
		List<MdrtbPatientProgram> mdrtbPrograms = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient);
		List<TbPatientProgram> tbPrograms = Context.getService(MdrtbService.class).getTbPatientPrograms(patient);
		
		map.put("hasPrograms", ((mdrtbPrograms != null && mdrtbPrograms.size() != 0) || (tbPrograms != null && tbPrograms
		        .size() != 0)) ? true : false);
		System.out.println("Prog:" + map.get("hasPrograms"));
		
		map.put("mdrtbPrograms", mdrtbPrograms);
		map.put("tbPrograms", tbPrograms);
		
		return new ModelAndView("/module/mdrtb/program/showEnroll", map);
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/programEnroll.form", method = RequestMethod.POST)
	public ModelAndView processEnroll(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors,
	        @RequestParam(required = true, value = "patientId") Integer patientId, SessionStatus status,
	        HttpServletRequest request, ModelMap map) throws SecurityException, IllegalArgumentException,
	        NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println("ProgramCont:processEnroll");
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if (patient == null) {
			throw new RuntimeException("Process enroll called with invalid patient id " + patientId);
		}
		
		// set the patient
		program.setPatient(patient);
		
		// perform validation (validation needs to happen after patient is set since patient is used to pull up patient's previous programs)
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			MdrtbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(
			    patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/program/showEnroll", map);
		}
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer())
		        .calculate(program);
		
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl="
		        + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());
	}
	
	@RequestMapping("/module/mdrtb/program/programDelete.form")
	public ModelAndView processDelete(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        SessionStatus status, ModelMap map) {
		
		MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		
		// we need to save the patient id so that we know where to redirect to
		Integer patientId = program.getPatient().getId();
		
		// now void the program
		Context.getProgramWorkflowService().voidPatientProgram(program.getPatientProgram(), "voided by mdr-tb module");
		
		// clear the command object
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + patientId);
	}
	
	@RequestMapping("/module/mdrtb/program/enrollment.form")
	public ModelAndView showEnrollment(@RequestParam(required = false, value = "patientId") Integer patientId,
	        @RequestParam(required = false, value = "idId") Integer idId,
	        @RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "dateEnrolled", required = false) String dateEnrolled,
	        @RequestParam(value = "patGroup", required = false) Integer patGroup,
	        @RequestParam(value = "drugGroup", required = false) Integer drugGroup, ModelMap map) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			throw new RuntimeException("Show enroll called with invalid patient id " + patientId);
		}
		
		// we need to determine if this patient currently in active in an mdr-tb program to determine what fields to display
		List<MdrtbPatientProgram> mdrtbPrograms = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient);
		List<TbPatientProgram> tbPrograms = Context.getService(MdrtbService.class).getTbPatientPrograms(patient);
		map.put("patientId", patientId);
		map.put("hasPrograms",
		    ((mdrtbPrograms != null && mdrtbPrograms.size() != 0) || (tbPrograms != null && tbPrograms.size() != 0)));
		
		map.put("mdrtbPrograms", mdrtbPrograms);
		map.put("tbPrograms", tbPrograms);
		
		map.put("unassignedMdrIdentifiers", getUnassignedMdrIdentifiers(patient));
		map.put("unassignedDotsIdentifiers", getUnassignedDotsIdentifiers(patient));
		map.put("idId", idId);
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		Region locOb = null;
		District locDist = null;
		Facility locFac = null;
		
		if (oblast == null) {
			// NOTE! This method looks for the location in patient address instead of parsing from patient identifier (deprecated way)
			PersonAddress personAddress = patient.getPersonAddress();
			String distName = personAddress.getCountyDistrict();
			String facName = personAddress.getAddress4();
			List<Region> obList = Context.getService(MdrtbService.class).getRegions();
			// Get Region
			String obName = personAddress.getStateProvince();
			if (obName != null) {
				for (Region o : obList) {
					if (o.getName().equalsIgnoreCase(obName)) {
						locOb = o;
						break;
					}
				}
			}
			if (locOb != null) {
				// Add this region only
				map.addAttribute("oblasts", Arrays.asList(locOb));
				if (distName != null) {
					List<District> distList = Context.getService(MdrtbService.class).getDistrictsByParent(locOb.getId());
					for (District d : distList) {
						if (d.getName().equalsIgnoreCase(distName)) {
							locDist = d;
							break;
						}
					}
				}
				if (locDist != null) {
					// Add this district only
					map.addAttribute("districts", Arrays.asList(locDist));
					List<Facility> facilityList = Context.getService(MdrtbService.class).getFacilitiesByParent(
					    locDist.getId());
					if (facilityList.size() == 0) { // Maybe it's Dushanbe
						facilityList = Context.getService(MdrtbService.class).getFacilitiesByParent(locOb.getId());
					}
					if (facName != null) {
						for (Facility f : facilityList) {
							if (f.getName().equalsIgnoreCase(facName)) {
								locFac = f;
								break;
							}
						}
					}
					if (locFac != null) {
						map.addAttribute("facilities", Arrays.asList(locFac));
					} else {
						// Otherwise add all facilities
						map.addAttribute("facilities", facilityList);
					}
					
				} else {
					// Otherwise add all districts
					map.addAttribute("districts", Context.getService(MdrtbService.class).getDistrictsByParent(locOb.getId()));
				}
			} else {
				// Otherwise add all regions
				map.addAttribute("oblasts", Context.getService(MdrtbService.class).getRegions());
			}
			
		}
		
		else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getRegDistricts(Integer.parseInt(oblast));
			map.addAttribute("oblastSelected", oblast);
			map.addAttribute("oblasts", oblasts);
			map.addAttribute("districts", districts);
			
		} else {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getRegDistricts(Integer.parseInt(oblast));
			facilities = Context.getService(MdrtbService.class).getRegFacilities(Integer.parseInt(district));
			map.addAttribute("oblastSelected", oblast);
			map.addAttribute("oblasts", oblasts);
			map.addAttribute("districts", districts);
			map.addAttribute("districtSelected", district);
			map.addAttribute("facilities", facilities);
		}
		
		map.addAttribute("dateEnrolled", dateEnrolled);
		map.addAttribute("patientGroup", patGroup);
		map.addAttribute("previousDrugUse", drugGroup);
		
		return new ModelAndView("/module/mdrtb/program/enrollment", map);
	}
	
	@RequestMapping(value = "/module/mdrtb/program/firstEnrollment.form", method = RequestMethod.POST)
	public ModelAndView processFirstEnroll(@ModelAttribute("program") TbPatientProgram program, BindingResult errors,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = false, value = "idId") Integer idId,
	        
	        SessionStatus status, HttpServletRequest request, ModelMap map) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println("ProgramCont:processEnroll");
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if (patient == null) {
			throw new RuntimeException("Process enroll called with invalid patient id " + patientId);
		}
		
		Location location = null;
		
		System.out.println("PARAMS:\nob: " + oblastId + "\ndist: " + districtId + "\nfac: " + facilityId);
		
		if (facilityId != null && facilityId.length() != 0)
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		else
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		
		if (location == null) { // && locations!=null && (locations.size()==0 || locations.size()>1)) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		
		if (program.getLocation() == null || !location.equals(program.getLocation())) {
			System.out.println("setting loc");
			program.setLocation(location);
		}
		
		// set the patient
		program.setPatient(patient);
		program.setPatientIdentifier(patient.getPatientIdentifier());
		
		// perform validation (validation needs to happen after patient is set since patient is used to pull up patient's previous programs)
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			TbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentTbPatientProgram(
			    patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/program/enrollment", map);
		}
		
		// Set program
		PatientProgram patientProgram = program.getPatientProgram();
		patientProgram.setProgram(Context.getService(MdrtbService.class).getTbProgram());
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(patientProgram);
		Context.getService(MdrtbService.class).addIdentifierToProgram(idId,
		    program.getPatientProgram().getPatientProgramId());
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		/* VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculateTb(program);
		
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());*/
		
		return new ModelAndView("redirect:/module/mdrtb/form/tb03.form?patientProgramId=" + program.getId()
		        + "&encounterId=-1");
	}
	
	@RequestMapping(value = "/module/mdrtb/program/otherEnrollment.form", method = RequestMethod.GET)
	public ModelAndView showOtherEnrollment(@RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = true, value = "type") String type,
	        @RequestParam(required = false, value = "mdrLocation") Integer locationId,
	        @RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "dateEnrolled", required = false) String dateEnrolled,
	        @RequestParam(value = "patGroup", required = false) Integer patGroup,
	        @RequestParam(value = "drugGroup", required = false) Integer drugGroup,
	        @RequestParam(value = "idSelected", required = false) String idSelected,
	        @RequestParam(required = false, value = "programStartDate") String programStartDate,
	        /* @RequestParam(required = false, value = "previousProgramId") Integer previousProgramId,*/
	        ModelMap map) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			throw new RuntimeException("Show enroll called with invalid patient id " + patientId);
		}
		if (type == null || type.length() == 0) {
			throw new RuntimeException("No program type specified");
		}
		map.put("patientId", patientId);
		map.put("type", type);
		if (locationId != null) {
			map.put("initLocation", Context.getLocationService().getLocation(locationId));
		}
		Date parsedDate = null;
		if (programStartDate != null) {
			String[] splits = programStartDate.split("\\.");
			if (splits == null || splits.length != 3) {
				parsedDate = null;
			} else {
				int year = Integer.parseInt(splits[2]);
				int month = Integer.parseInt(splits[1]) - 1;
				int date = Integer.parseInt(splits[0]);
				
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(Calendar.YEAR, year);
				gc.set(Calendar.MONTH, month);
				gc.set(Calendar.DATE, date);
				parsedDate = gc.getTime();
			}
			map.put("programStartDate", parsedDate);
		}
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			map.addAttribute("oblasts", oblasts);
		} else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
			map.addAttribute("oblastSelected", oblast);
			map.addAttribute("oblasts", oblasts);
			map.addAttribute("districts", districts);
			
		} else {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
			facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
			map.addAttribute("oblastSelected", oblast);
			map.addAttribute("oblasts", oblasts);
			map.addAttribute("districts", districts);
			map.addAttribute("districtSelected", district);
			map.addAttribute("facilities", facilities);
		}
		map.addAttribute("dateEnrolled", dateEnrolled);
		map.addAttribute("patientGroup", patGroup);
		map.addAttribute("previousDrugUse", drugGroup);
		map.addAttribute("idSelected", idSelected);
		System.out.println("IDS:" + idSelected);
		/* if(previousProgramId!=null) {
			map.put("previousProgramId", previousProgramId);
		}*/
		return new ModelAndView("/module/mdrtb/program/otherEnrollment", map);
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/otherEnrollmentMdrtb.form", method = RequestMethod.POST)
	public ModelAndView processOtherEnrollMdrtb(@ModelAttribute("program") MdrtbPatientProgram program,
	        BindingResult errors, @RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = true, value = "identifierValue") String identifierValue, SessionStatus status,
	        HttpServletRequest request, ModelMap map) throws SecurityException, IllegalArgumentException,
	        NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			throw new RuntimeException("Process enroll called with invalid patient id " + patientId);
		}
		
		Location location = null;
		System.out.println("PARAMS:\nob: " + oblastId + "\ndist: " + districtId + "\nfac: " + facilityId + "\nID: "
		        + identifierValue);
		
		if (facilityId != null && facilityId.length() != 0)
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		else
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		
		if (location == null) { // && locations!=null && (locations.size()==0 || locations.size()>1)) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		if (program.getLocation() == null || !location.equals(program.getLocation())) {
			program.setLocation(location);
		}
		MdrtbUtil.validateIdentifierString(identifierValue, errors);
		
		if (errors.hasErrors()) {
			System.out.println("errors");
			MdrtbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(
			    patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			map.put("type", "mdr");
			map.put("ob", oblastId);
			map.put("loc", districtId);
			map.put("idSelected", identifierValue);
			map.put("dateEnrolled", program.getDateEnrolled());
			
			if (program.getClassificationAccordingToPreviousDrugUse() != null) {
				map.put("previousDrugUse", program.getClassificationAccordingToPreviousDrugUse().getConcept().getId());
			}
			if (program.getClassificationAccordingToPreviousTreatment() != null) {
				map.put("patientGroup", program.getClassificationAccordingToPreviousTreatment().getConcept().getId());
			}
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/program/otherEnrollment.form?patientId=" + patientId + "&type=mdr", map);
		}
		
		PatientIdentifier identifier = new PatientIdentifier(identifierValue, getMdrIdentifier(), program.getLocation());
		patient.addIdentifier(identifier);
		Context.getPatientService().savePatient(patient);
		
		Integer idId = null;
		Set<PatientIdentifier> identifiers = patient.getIdentifiers();
		Iterator<PatientIdentifier> idIterator = identifiers.iterator();
		PatientIdentifier temp = null;
		while (idIterator.hasNext()) {
			temp = idIterator.next();
			if (temp.getIdentifier().equals(identifierValue)) {
				idId = temp.getId();
				break;
			}
		}
		
		// set the patient
		if (program != null)
			program.setPatient(patient);
		
		PatientValidator validator = new PatientValidator();
		validator.validate(patient, errors);
		
		// perform validation (validation needs to happen after patient is set since patient is used to pull up patient's previous programs)
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			MdrtbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(
			    patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			map.put("type", "mdr");
			map.put("ob", oblastId);
			map.put("loc", districtId);
			
			//List<Region> oblasts = Context.getService(MdrtbService.class).getOblasts();
			//List<District> districts= Context.getService(MdrtbService.class).getRegDistricts(Integer.parseInt(oblastId));
			//List<Facility> facilities = Context.getService(MdrtbService.class).getRegFacilities(Integer.parseInt(districtId));
			/*if(facilityId!=null) {
				map.put("facility", facilityId);
			}*/
			//map.put("oblasts", oblasts);
			//map.put("districts", districts);
			//map.put("facilities", facilities);
			map.put("idSelected", identifierValue);
			map.put("dateEnrolled", program.getDateEnrolled());
			if (program.getClassificationAccordingToPreviousDrugUse() != null) {
				map.put("previousDrugUse", program.getClassificationAccordingToPreviousDrugUse().getConcept().getId());
			}
			if (program.getClassificationAccordingToPreviousTreatment() != null) {
				map.put("patientGroup", program.getClassificationAccordingToPreviousTreatment().getConcept().getId());
			}
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/program/otherEnrollment.form?patientId=" + patientId + "&type=mdr", map);
		}
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		Context.getService(MdrtbService.class).addIdentifierToProgram(idId,
		    program.getPatientProgram().getPatientProgramId());
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		/* VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculateTb(program);
		
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());*/
		
		return new ModelAndView("redirect:/module/mdrtb/form/tb03u.form?patientProgramId=" + program.getId()
		        + "&encounterId=-1");
	}
	
	@RequestMapping(value = "/module/mdrtb/program/otherEnrollmentTb.form", method = RequestMethod.POST)
	public ModelAndView processOtherEnrollTb(@ModelAttribute("program") TbPatientProgram program, BindingResult errors,
	        @RequestParam(required = true, value = "patientId") Integer patientId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = true, value = "identifierValue") String identifierValue, SessionStatus status,
	        HttpServletRequest request, ModelMap map) throws SecurityException, IllegalArgumentException,
	        NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			throw new RuntimeException("Process enroll called with invalid patient id " + patientId);
		}
		Location location = null;
		System.out.println("PARAMS:\nob: " + oblastId + "\ndist: " + districtId + "\nfac: " + facilityId);
		if (facilityId != null && facilityId.length() != 0)
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		else
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		if (location == null) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		if (program.getLocation() == null || !location.equals(program.getLocation())) {
			program.setLocation(location);
		}
		PatientIdentifier identifier = new PatientIdentifier(identifierValue, getDotsIdentifier(), program.getLocation());
		identifier.setPreferred(true);
		patient.addIdentifier(identifier);
		
		Integer idId = null;
		Set<PatientIdentifier> identifiers = patient.getIdentifiers();
		Iterator<PatientIdentifier> idIterator = identifiers.iterator();
		PatientIdentifier temp = null;
		while (idIterator.hasNext()) {
			temp = idIterator.next();
			if (temp.getIdentifier().equals(identifierValue)) {
				idId = temp.getId();
				break;
			}
		}
		
		// set the patient
		if (program != null)
			program.setPatient(patient);
		
		//		PatientValidator validator = new PatientValidator();
		//		validator.validate(patient, errors);
		
		// perform validation (validation needs to happen after patient is set since patient is used to pull up patient's previous programs)
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			TbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentTbPatientProgram(
			    patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			map.put("type", "tb");
			map.put("oblasts", Context.getService(MdrtbService.class).getRegions());
			if (oblastId != null) {
				map.put("oblastSelected", oblastId);
				map.put("districts", Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblastId)));
			}
			
			if (districtId != null) {
				map.put("districtSelected", districtId);
				map.put("facilities",
				    Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(districtId)));
			}
			map.put("facilitySelected", facilityId);
			map.put("identifierValue", identifierValue);
			//map.put("patientProgramId", -1);
			System.out.println("ERRORS");
			return new ModelAndView("/module/mdrtb/program/otherEnrollment", map);
		}
		
		// save the actual update
		System.out.println("Saving Patient");
		Context.getPatientService().savePatient(patient);
		System.out.println("Saving Program");
		PatientProgram patientProgram = program.getPatientProgram();
		patientProgram.setProgram(Context.getService(MdrtbService.class).getTbProgram());
		Context.getProgramWorkflowService().savePatientProgram(patientProgram);
		System.out.println("Add ID to Program");
		Context.getService(MdrtbService.class).addIdentifierToProgram(idId,
		    program.getPatientProgram().getPatientProgramId());
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		/* VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculateTb(program);
		
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());*/
		
		return new ModelAndView("redirect:/module/mdrtb/form/tb03.form?patientProgramId=" + program.getId()
		        + "&encounterId=-1");
	}
	
	public List<PatientIdentifier> getUnassignedDotsIdentifiers(Patient p) {
		List<PatientIdentifier> ids = null;
		List<PatientIdentifier> ret = new ArrayList<PatientIdentifier>();
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE));
		List<PatientIdentifierType> typeList = new ArrayList<PatientIdentifierType>();
		typeList.add(pit);
		List<Patient> patList = new ArrayList<Patient>();
		patList.add(p);
		
		ids = Context.getPatientService().getPatientIdentifiers(null, typeList, null, patList, null);
		for (PatientIdentifier pi : ids) {
			if (!isIdentifierAssigned(pi, false))
				ret.add(pi);
		}
		
		return ret;
	}
	
	public List<PatientIdentifier> getUnassignedMdrIdentifiers(Patient p) {
		List<PatientIdentifier> ids = null;
		List<PatientIdentifier> ret = new ArrayList<PatientIdentifier>();
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE));
		
		List<PatientIdentifierType> typeList = new ArrayList<PatientIdentifierType>();
		typeList.add(pit);
		List<Patient> patList = new ArrayList<Patient>();
		patList.add(p);
		
		ids = Context.getPatientService().getPatientIdentifiers(null, typeList, null, patList, null);
		for (PatientIdentifier pi : ids) {
			if (!isIdentifierAssigned(pi, true))
				ret.add(pi);
		}
		
		return ids;
	}
	
	public Boolean isIdentifierAssigned(PatientIdentifier pi, boolean mdr) {
		Collection<org.openmrs.PatientProgram> ppList = Context.getProgramWorkflowService().getPatientPrograms(
		    pi.getPatient(), null, null, null, null, null, false);
		PatientIdentifier temp = null;
		for (org.openmrs.PatientProgram pp : ppList) {
			temp = Context.getService(MdrtbService.class).getPatientProgramIdentifier(
			    Context.getProgramWorkflowService().getPatientProgram(pp.getId()));
			if (temp != null && temp.getId().intValue() == pi.getId().intValue())
				return true;
		}
		
		return false;
	}
	
	@RequestMapping("/module/mdrtb/program/addId.form")
	public ModelAndView addIdToProgram(@RequestParam(required = true, value = "ppid") Integer patientProgramId,
	        @RequestParam(required = true, value = "idToAdd") Integer patientIdentifierId, ModelMap map) {
		Context.getService(MdrtbService.class).addIdentifierToProgram(patientIdentifierId, patientProgramId);
		return new ModelAndView("redirect:/module/mdrtb/program/enrollment.form?patientProgramId=" + patientProgramId
		        + "&patientId="
		        + Context.getProgramWorkflowService().getPatientProgram(patientProgramId).getPatient().getId());
	}
	
	@RequestMapping(value = "/module/mdrtb/program/editProgramTb.form", method = RequestMethod.POST)
	public ModelAndView processEditProgramTb(
	        @ModelAttribute("program") TbPatientProgram program,
	        BindingResult errors,
	        @RequestParam(required = true, value = "programId") Integer programId,
	        @RequestParam(required = true, value = "dateEnrolled") Date enrollmentDate,
	        @RequestParam(required = true, value = "classificationAccordingToPatientGroups") Integer classificationAccordingToPatientGroups,
	        @RequestParam(required = true, value = "classificationAccordingToPreviousDrugUse") Integer classificationAccordingToPreviousDrugUse,
	        SessionStatus status, HttpServletRequest request, ModelMap map) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println("ProgramCont:processEditProgramTB");
		MdrtbService ms = Context.getService(MdrtbService.class);
		TbPatientProgram tpp = ms.getTbPatientProgram(programId);
		
		tpp.setDateEnrolled(enrollmentDate);
		//TODO: Write unit test to see if the right workflow state is returned
		ProgramWorkflowState programWorkflowStateAccordingToPatientGroups = Context.getService(MdrtbService.class)
		        .getProgramWorkflowState(Context.getConceptService().getConcept(classificationAccordingToPatientGroups));
		tpp.setClassificationAccordingToPatientGroups(programWorkflowStateAccordingToPatientGroups);
		
		ProgramWorkflowState programWorkflowStateAccordingToPreviousDrugUse = Context.getService(MdrtbService.class)
		        .getProgramWorkflowState(Context.getConceptService().getConcept(classificationAccordingToPreviousDrugUse));
		tpp.setClassificationAccordingToPreviousDrugUse(programWorkflowStateAccordingToPreviousDrugUse);
		if (tpp != null) {
			// save the actual update
			Context.getProgramWorkflowService().savePatientProgram(tpp.getPatientProgram());
			status.setComplete();
			map.clear();
			
			TB03Form tf = tpp.getTb03();
			if (tf != null) {
				if (!tf.getEncounterDatetime().equals(tpp.getDateEnrolled())) {
					tf.setEncounterDatetime(tpp.getDateEnrolled());
					Context.getEncounterService().saveEncounter(tf.getEncounter());
				}
			}
			Form89 f89 = tpp.getForm89();
			if (f89 != null) {
				if (!f89.getEncounterDatetime().equals(tpp.getDateEnrolled())) {
					f89.setEncounterDatetime(tpp.getDateEnrolled());
					Context.getEncounterService().saveEncounter(f89.getEncounter());
				}
			}
		}
		return new ModelAndView("redirect:/module/mdrtb/program/enrollment.form?patientId=" + tpp.getPatient().getId());
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		/* VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculateTb(program);
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());*/
		
	}
	
	@RequestMapping("/module/mdrtb/program/showEditEnroll.form")
	public ModelAndView showProgramForEdit(@RequestParam(required = true, value = "programId") Integer programId,
	        @RequestParam(required = true, value = "type") String type, ModelMap map) {
		MdrtbService ms = Context.getService(MdrtbService.class);
		if (type != null && type.equals("tb")) {
			TbPatientProgram tpp = ms.getTbPatientProgram(programId);
			map.put("program", tpp);
		} else if (type != null && type.equals("mdr")) {
			MdrtbPatientProgram tpp = ms.getMdrtbPatientProgram(programId);
			map.put("program", tpp);
		}
		map.put("type", type);
		return new ModelAndView("/module/mdrtb/program/showEditEnroll", map);
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/editProgramMdrtb.form", method = RequestMethod.POST)
	public ModelAndView processEditProgramMdrtb(
	        @ModelAttribute("program") TbPatientProgram program,
	        BindingResult errors,
	        @RequestParam(required = true, value = "programId") Integer programId,
	        @RequestParam(required = true, value = "dateEnrolled") Date enrollmentDate,
	        @RequestParam(required = true, value = "classificationAccordingToPreviousTreatment") Integer classificationAccordingToPreviousTreatment,
	        @RequestParam(required = true, value = "classificationAccordingToPreviousDrugUse") Integer classificationAccordingToPreviousDrugUse,
	        SessionStatus status, HttpServletRequest request, ModelMap map) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println("ProgramCont:processEditProgramTB");
		MdrtbService ms = Context.getService(MdrtbService.class);
		MdrtbPatientProgram tpp = ms.getMdrtbPatientProgram(programId);
		
		tpp.setDateEnrolled(enrollmentDate);
		
		//TODO: Write unit test to see if the right workflow state is returned
		ProgramWorkflowState stateAccordingToPreviousTreatment = Context.getService(MdrtbService.class)
		        .getProgramWorkflowState(Context.getConceptService().getConcept(classificationAccordingToPreviousTreatment));
		tpp.setClassificationAccordingToPreviousTreatment(stateAccordingToPreviousTreatment);
		
		ProgramWorkflowState stateAccordingToDrugUse = Context.getService(MdrtbService.class).getProgramWorkflowState(
		    Context.getConceptService().getConcept(classificationAccordingToPreviousDrugUse));
		tpp.setClassificationAccordingToPreviousTreatment(stateAccordingToDrugUse);
		
		if (tpp != null) {
			// save the actual update
			Context.getProgramWorkflowService().savePatientProgram(tpp.getPatientProgram());
			status.setComplete();
			map.clear();
			TB03uForm tf = tpp.getTb03u();
			if (tf != null) {
				if (!tf.getEncounterDatetime().equals(tpp.getDateEnrolled())) {
					tf.setEncounterDatetime(tpp.getDateEnrolled());
					Context.getEncounterService().saveEncounter(tf.getEncounter());
				}
			}
		}
		return new ModelAndView("redirect:/module/mdrtb/program/enrollment.form?patientId=" + tpp.getPatient().getId());
		
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		/* VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculateTb(program);
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());*/
	}
	
}
