package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.web.util.MdrtbWebUtil;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/culture.form")
public class CultureFormController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register binders 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
	}
	
	@ModelAttribute("culture")
	public CultureForm getCultureForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			mdr = true;
			System.out.println("mdr");
		}
		
		else {
			mdr = false;
			System.out.println("not mdr");
		}
		// if no form is specified, create a new one
		if (encounterId == -1) {
			CultureForm form = null;
			if (!mdr) {
				TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
				
				form = new CultureForm(tbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(tbProgram.getDateEnrolled());
				form.setLocation(tbProgram.getLocation());
			}
			
			else {
				MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(
				    patientProgramId);
				
				form = new CultureForm(mdrtbProgram.getPatient());
				
				// prepopulate the intake form with any program information
				//form.setEncounterDatetime(mdrtbProgram.getDateEnrolled());
				form.setLocation(mdrtbProgram.getLocation());
			}
			return form;
		} else {
			return new CultureForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showCultureForm() {
		ModelMap map = new ModelMap();
		
		return new ModelAndView("/module/mdrtb/form/culture", map);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processCultureForm(@ModelAttribute("culture") CultureForm culture, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			mdr = true;
		}
		
		else {
			mdr = false;
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(culture.getEncounter());
		
		// clears the command object from the session
		status.setComplete();
		
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			if (!mdr) {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl, Context.getService(MdrtbService.class)
				        .getTbPatientProgram(patientProgramId).getPatient().getId(), patientProgramId);
			}
			
			else {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl, Context.getService(MdrtbService.class)
				        .getMdrtbPatientProgram(patientProgramId).getPatient().getId(), patientProgramId);
			}
		}
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("returnUrl")
	public String getReturnUrl(@RequestParam(required = false, value = "returnUrl") String returnUrl) {
		return returnUrl;
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
	
	@ModelAttribute("locations")
	List<Location> getPossibleLocations() {
		return Context.getService(MdrtbService.class).getCultureLocations();
	}
	
	@ModelAttribute("cultureresults")
	public Collection<ConceptAnswer> getCultureResults() {
		//return Context.getService(MdrtbService.class).getPossibleCultureResults();
		ArrayList<ConceptAnswer> resultArray = new ArrayList<ConceptAnswer>();
		for (int i = 0; i < 6; i++) {
			resultArray.add(null);
		}
		
		Collection<ConceptAnswer> results = Context.getService(MdrtbService.class).getPossibleCultureResults();
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		for (ConceptAnswer ca : results) {
			
			if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.LOWAFB).getId().intValue()) {
				resultArray.set(0, ca);
			}
			
			else if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.WEAKLY_POSITIVE).getId()
			        .intValue()) {
				resultArray.set(1, ca);
			}
			
			else if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.MODERATELY_POSITIVE).getId()
			        .intValue()) {
				resultArray.set(2, ca);
			}
			
			else if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.STRONGLY_POSITIVE).getId()
			        .intValue()) {
				resultArray.set(3, ca);
			}
			
			else if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE).getId().intValue()) {
				resultArray.set(4, ca);
			}
			
			else if (ca.getAnswerConcept().getId().intValue() == ms.getConcept(MdrtbConcepts.CULTURE_GROWTH).getId()
			        .intValue()) {
				resultArray.set(5, ca);
			}
		}
		
		return resultArray;
	}
	
}
