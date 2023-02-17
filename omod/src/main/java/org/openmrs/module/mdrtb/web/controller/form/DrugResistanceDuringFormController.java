package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/resistanceDuringTx.form")
@SessionAttributes("drdt")
public class DrugResistanceDuringFormController {
	
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
	
	@ModelAttribute("drdt")
	public DrugResistanceDuringTreatmentForm getDrdtForm(
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			DrugResistanceDuringTreatmentForm form = null;
			
			MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(
			    patientProgramId);
			
			form = new DrugResistanceDuringTreatmentForm(mdrtbProgram.getPatient());
			form.setLocation(mdrtbProgram.getLocation());
			
			return form;
		} else {
			return new DrugResistanceDuringTreatmentForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showDrdtForm(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, ModelMap model) {
		
		DrugResistanceDuringTreatmentForm drdt = null;
		if (encounterId != -1) { //we are editing an existing encounter
			drdt = new DrugResistanceDuringTreatmentForm(Context.getEncounterService().getEncounter(encounterId));
		} else {
			try {
				drdt = getDrdtForm(-1, patientProgramId);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		model.addAttribute("encounterId", encounterId);
		model.addAttribute("returnUrl", returnUrl);
		if (mode != null && mode.length() != 0) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/resistanceDuringTx", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processDrdtForm(@ModelAttribute("drdt") DrugResistanceDuringTreatmentForm drdt,
	        BindingResult errors, @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		// save the actual update
		Context.getEncounterService().saveEncounter(drdt.getEncounter());
		
		// clears the command object from the session
		status.setComplete();
		
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
			returnUrl = MdrtbWebUtil.appendParameters(returnUrl, Context.getService(MdrtbService.class)
			        .getMdrtbPatientProgram(patientProgramId).getPatient().getId(), patientProgramId);
			
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
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("resistancetypes")
	public ArrayList<ConceptAnswer> getPossibleResistanceTypes() {
		
		ArrayList<ConceptAnswer> typeArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> ca = Context.getService(MdrtbService.class).getPossibleConceptAnswers(
		    MdrtbConcepts.DRUG_RESISTANCE_DURING_TREATMENT);
		for (int i = 0; i < 4; i++) {
			typeArray.add(null);
		}
		for (ConceptAnswer c : ca) {
			if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.MDR_TB).getId().intValue()) {
				typeArray.set(0, c);
			} else if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.PRE_XDR_TB).getId().intValue()) {
				typeArray.set(1, c);
			} else if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.XDR_TB).getId().intValue()) {
				typeArray.set(2, c);
			} else if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.TDR_TB).getId().intValue()) {
				typeArray.set(3, c);
			}
		}
		
		return typeArray;
	}
	
}
