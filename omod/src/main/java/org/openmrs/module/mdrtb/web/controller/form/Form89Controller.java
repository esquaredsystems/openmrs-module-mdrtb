package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
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
@RequestMapping("/module/mdrtb/form/form89.form")
public class Form89Controller {
	
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
	
	@ModelAttribute("form89")
	public Form89 getForm89(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		// if no form is specified, create a new one
		if (encounterId == -1) {
			TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
			
			Form89 form = new Form89(tbProgram.getPatient());
			
			// prepopulate the intake form with any program information
			form.setEncounterDatetime(tbProgram.getDateEnrolled());
			
			form.initTB03(patientProgramId);
			if (form.getTB03() != null)
				form.setLocation(form.getTB03().getLocation());
			return form;
		} else {
			Form89 ret = new Form89(Context.getEncounterService().getEncounter(encounterId));
			ret.initTB03(patientProgramId);
			return ret;
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm89(@RequestParam(required = false, value = "returnUrl") String returnUrl,
	        @RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			Form89 form89 = null;
			if (encounterId != -1) { //we are editing an existing encounter
				form89 = new Form89(Context.getEncounterService().getEncounter(encounterId));
			} else {
				try {
					form89 = getForm89(-1, patientProgramId);
				}
				catch (Exception e) {
					Log.error(e.getMessage());
					form89 = new Form89();
				}
			}
			
			Location location = form89.getLocation();
			String obName = location.getStateProvince();
			String distName = location.getCountyDistrict();
			String facName = location.getAddress4();
			
			Region ob = null;
			District dist = null;
			
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
			if (obName != null) {
				for (Region o : oblasts) {
					if (o.getName().equalsIgnoreCase(obName)) {
						ob = o;
						break;
					}
				}
			}
			if (ob != null) {
				model.addAttribute("oblastSelected", ob);
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(ob.getId());
				model.addAttribute("districts", districts);
				if (distName != null) {
					for (District d : districts) {
						if (d.getName().equalsIgnoreCase(distName)) {
							dist = d;
							break;
						}
					}
					if (dist != null) {
						model.addAttribute("districtSelected", dist.getId());
					}
				}
			}
			if (dist != null) {
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(dist.getId());
				if (facilities.size() == 0) { // Maybe it's for Dushanbe
					facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(ob.getId());
				}
				model.addAttribute("facilities", facilities);
				if (facName != null) {
					for (Facility f : facilities) {
						if (f.getName().equalsIgnoreCase(facName)) {
							model.addAttribute("facilitySelected", f.getId());
							break;
						}
					}
				}
			}
		} else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getRegDistricts(Integer.parseInt(oblast));
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
		} else {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getRegDistricts(Integer.parseInt(oblast));
			facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
			if (facilities.size() == 0) { // Maybe it's for Dushanbe
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(oblast));
			}
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
			model.addAttribute("districtSelected", district);
			model.addAttribute("facilities", facilities);
		}
		model.addAttribute("encounterId", encounterId);
		if (mode != null && mode.length() != 0) {
			model.addAttribute("mode", mode);
		}
		return new ModelAndView("/module/mdrtb/form/form89", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm89(@ModelAttribute("form89") Form89 form89, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		Location location = null;
		if (StringUtils.isNotBlank(facilityId)) {
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), Integer.parseInt(facilityId));
		} else if (StringUtils.isNotBlank(districtId)) {
			location = Context.getService(MdrtbService.class).getLocation(Integer.parseInt(oblastId),
			    Integer.parseInt(districtId), null);
		}
		if (location == null) {
			throw new MdrtbAPIException("Invalid Hierarchy Set selected");
		}
		
		MdrtbFormServiceImpl formService = new MdrtbFormServiceImpl();
		form89 = formService.processForm89(form89, location);
		// clears the command object from the session
		status.setComplete();
		map.clear();
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
		}
		returnUrl = MdrtbWebUtil.appendParameters(returnUrl, form89.getPatient().getPatientId(),
		    form89.getPatientProgramId());
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("tbProgram")
	public TbPatientProgram getTbPatientProgram(
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
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
	
	@ModelAttribute("sites")
	public Collection<ConceptAnswer> getAnatomicalSites() {
		return Context.getService(MdrtbService.class).getPossibleAnatomicalSites();
	}
	
	@ModelAttribute("locationtypes")
	public Collection<ConceptAnswer> getPossibleLocationTypes() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE).getAnswers();
	}
	
	@ModelAttribute("populationcategories")
	public Collection<ConceptAnswer> getPossiblePopulationCategories() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY).getAnswers();
	}
	
	@ModelAttribute("professions")
	public Collection<ConceptAnswer> getPossibleProfessions() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION).getAnswers();
	}
	
	@ModelAttribute("places")
	public Collection<ConceptAnswer> getPossiblePlacesOfDetection() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_DETECTION).getAnswers();
	}
	
	@ModelAttribute("circumstances")
	public Collection<ConceptAnswer> getPossibleCircumstancesOfDetection() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION).getAnswers();
	}
	
	@ModelAttribute("methods")
	public Collection<ConceptAnswer> getPossibleMethodsOfDetection() {
		
		ArrayList<ConceptAnswer> stateArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> bases = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.METHOD_OF_DETECTION).getAnswers();
		if (bases != null) {
			MdrtbService ms = Context.getService(MdrtbService.class);
			Set<Concept> classificationConcepts = new HashSet<Concept>();
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HISTOLOGY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.GENEXPERT));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.CXR_RESULT));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.HAIN_TEST));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.CULTURE_TEST));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.TUBERCULIN_TEST));
			classificationConcepts.add(ms.getConcept(MdrtbConcepts.OTHER));
			for (ConceptAnswer pws : bases) {
				for (Concept classification : classificationConcepts) {
					if (pws.getAnswerConcept().getId().equals(classification.getId())) {
						stateArray.add(pws);
					}
				}
			}
		}
		return stateArray;
	}
	
	@ModelAttribute("epsites")
	public Collection<ConceptAnswer> getPossibleEpSites() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SITE_OF_EPTB).getAnswers();
	}
	
	@ModelAttribute("psites")
	public Collection<ConceptAnswer> getPossiblePSites() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
	}
	
	@ModelAttribute("eplocations")
	public Collection<ConceptAnswer> getPossibleEPLocations() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SITE_OF_EPTB).getAnswers();
	}
	
	@ModelAttribute("diabetesOptions")
	public Collection<ConceptAnswer> getPossibleDiabetes() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES).getAnswers();
	}
	
	@ModelAttribute("cnsdlOptions")
	public Collection<ConceptAnswer> getPossibleCNSDL() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL).getAnswers();
	}
	
	@ModelAttribute("htHeartDiseaseOptions")
	public Collection<ConceptAnswer> getPossibleHeartDisease() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE).getAnswers();
	}
	
	@ModelAttribute("ulcerOptions")
	public Collection<ConceptAnswer> getPossibleUlcers() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER).getAnswers();
	}
	
	@ModelAttribute("presences")
	public Collection<ConceptAnswer> getPossibleDecay() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESENCE_OF_DECAY).getAnswers();
	}
	
	@ModelAttribute("mentalDisorderOptions")
	public Collection<ConceptAnswer> mentalDisorderOptions() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER).getAnswers();
	}
	
	@ModelAttribute("ibc20Options")
	public Collection<ConceptAnswer> getPossibleIbc20() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ICD20).getAnswers();
	}
	
	@ModelAttribute("cancerOptions")
	public Collection<ConceptAnswer> getPossibleCancer() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER).getAnswers();
	}
	
	@ModelAttribute("hepatitisOptions")
	public Collection<ConceptAnswer> getPossibleHepatitis() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS).getAnswers();
	}
	
	@ModelAttribute("kidneyDiseaseOptions")
	public Collection<ConceptAnswer> getPossibleKidneyDisease() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE).getAnswers();
	}
	
	@ModelAttribute("noDiseaseOptions")
	public Collection<ConceptAnswer> getPossibleND() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO_DISEASE).getAnswers();
	}
	
	@ModelAttribute("gptOptions")
	public Collection<ConceptAnswer> getPossibleGPT() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT).getAnswers();
	}
	
	@ModelAttribute("cecOptions")
	public Collection<ConceptAnswer> getPossibleCMACPlace() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION).getAnswers();
	}
	
	@ModelAttribute("yesno")
	public ArrayList<ConceptAnswer> getYesno() {
		
		ArrayList<ConceptAnswer> typeArray = new ArrayList<ConceptAnswer>();
		Collection<ConceptAnswer> ca = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.REQUIRES_ANCILLARY_DRUGS).getAnswers();
		for (int i = 0; i < 2; i++) {
			typeArray.add(null);
		}
		for (ConceptAnswer c : ca) {
			
			if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.NO).getId().intValue()) {
				typeArray.set(0, c);
			} else if (c.getAnswerConcept().getId().intValue() == Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.YES).getId().intValue()) {
				typeArray.set(1, c);
			}
			
		}
		
		return typeArray;
	}
}
