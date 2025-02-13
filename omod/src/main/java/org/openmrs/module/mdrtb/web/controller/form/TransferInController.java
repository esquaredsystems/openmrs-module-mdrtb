package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/transferIn.form")
@SessionAttributes("transferIn")
public class TransferInController {
	
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
	
	@ModelAttribute("transferIn")
	public TransferInForm getTransferInForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException,
	        IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			mdr = true;
		}
		// if no form is specified, create a new one
		if (encounterId == -1) {
			TransferInForm form = null;
			if (!mdr) {
				TbPatientProgram tbProgram = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
				form = new TransferInForm(tbProgram.getPatient());
				form.setLocation(tbProgram.getLocation());
			}
			
			else {
				MdrtbPatientProgram mdrtbProgram = Context.getService(MdrtbService.class)
				        .getMdrtbPatientProgram(patientProgramId);
				form = new TransferInForm(mdrtbProgram.getPatient());
				form.setLocation(mdrtbProgram.getLocation());
			}
			return form;
		} else {
			return new TransferInForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showTransferInForm(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "encounterId") Integer encounterId,
	        @RequestParam(required = false, value = "mode") String mode, ModelMap model) {
		//ModelMap map = new ModelMap();
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			TransferInForm form = null;
			if (encounterId != -1) { //we are editing an existing encounter
				form = new TransferInForm(Context.getEncounterService().getEncounter(encounterId));
			} else {
				try {
					form = getTransferInForm(-1, patientProgramId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
			Location location = form.getLocation();
			if (location != null) {
				for (Region o : oblasts) {
					if (o.getName().equals(location.getStateProvince())) {
						model.addAttribute("oblastSelected", o.getId());
						districts = Context.getService(MdrtbService.class).getRegDistricts(o.getId());
						model.addAttribute("districts", districts);
						for (District d : districts) {
							if (d.getName().equals(location.getCountyDistrict())) {
								model.addAttribute("districtSelected", d.getId());
								facilities = Context.getService(MdrtbService.class).getRegFacilities(d.getId());
								if (facilities != null) {
									model.addAttribute("facilities", facilities);
									for (Facility f : facilities) {
										if (f.getName().equals(location.getAddress4())) {
											model.addAttribute("facilitySelected", f.getId());
											break;
										}
									}
								}
								break;
							}
						}
						break;
					}
				}
			}
		} else if (district == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
			model.addAttribute("oblastSelected", oblast);
			model.addAttribute("oblasts", oblasts);
			model.addAttribute("districts", districts);
		} else {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
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
		return new ModelAndView("/module/mdrtb/form/transferIn", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processTransferInForm(@ModelAttribute("transferIn") TransferInForm tif, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "oblast") String oblastId,
	        @RequestParam(required = true, value = "district") String districtId,
	        @RequestParam(required = false, value = "facility") String facilityId,
	        @RequestParam(required = false, value = "returnUrl") String returnUrl, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		Location location = null;
		
		System.out.println("TiF ID:" + tif.getId());
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
		
		if (tif.getLocation() == null || !location.equals(tif.getLocation())) {
			System.out.println("setting loc");
			tif.setLocation(location);
		}
		
		boolean mdr = false;
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		if (pp.getProgram().getConcept().getId().intValue() == Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.MDR_TB_PROGRAM).getId().intValue()) {
			//if(pp.getProgram().getConcept().getId().intValue() == Context.getConceptService().getConceptByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name")).getId().intValue()) {
			mdr = true;
		}
		// save the actual update
		Context.getEncounterService().saveEncounter(tif.getEncounter());
		
		boolean programModified = false;
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			if (!mdr) {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/tbdashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
				    Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId).getPatient().getId(),
				    patientProgramId);
			} else {
				returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
				returnUrl = MdrtbWebUtil.appendParameters(returnUrl,
				    Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId).getPatient().getId(),
				    patientProgramId);
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
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
}
