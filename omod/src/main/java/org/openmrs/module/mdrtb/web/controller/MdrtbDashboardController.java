package org.openmrs.module.mdrtb.web.controller;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgramHospitalizationValidator;
import org.openmrs.module.mdrtb.status.HivStatusCalculator;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.Status;
import org.openmrs.module.mdrtb.status.StatusFlag;
import org.openmrs.module.mdrtb.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardHivStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardLabResultsStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardTreatmentStatusRenderer;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
import org.openmrs.propertyeditor.ConceptEditor;
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
public class MdrtbDashboardController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register binders for location and program workflow state
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("outcomes")
	public Collection<ProgramWorkflowState> getOutcomes() {
		return Context.getService(MdrtbService.class).getPossibleMdrtbProgramOutcomes();
	}
	
	@ModelAttribute("classificationsAccordingToPreviousDrugUse")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousDrugUse() {
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse();
	}
	
	@ModelAttribute("classificationsAccordingToPreviousTreatment")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousTreatment() {
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment();
	}
	
	@ModelAttribute("patientDied")
	public ProgramWorkflowState getPatientDiedState() {
		return Context.getService(MdrtbService.class).getProgramWorkflowState(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED));
	}
	
	@ModelAttribute("hospitalizationState")
	public PatientState getHospitalizationState(
	        @RequestParam(required = false, value = "hospitalizationStateId") Integer hospitalizationStateId) {
		if (hospitalizationStateId != null) {
			for (PatientProgram p : Context.getProgramWorkflowService().getPatientPrograms(null, null, null, null, null,
			    null, false)) {
				PatientState state = p.getPatientState(hospitalizationStateId);
				if (state != null) {
					return state;
				}
			}
		}
		return null;
	}
	
	@ModelAttribute("program")
	public MdrtbPatientProgram getMdrtbPatientProgram(
	        @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "patientId") Integer patientId) {
		if (patientId == null || patientId == -1) {
			return null;
		}
		// if there is no patient program selected, we want to show the most recent program
		if (patientProgramId == null || patientProgramId == -1) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			
			if (patient == null) {
				throw new MdrtbAPIException("Invalid patient id passed to dashboard controller");
			} else {
				return Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(patient);
			}
			
		}
		// fetch the program that is being requested
		else {
			return Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		}
	}
	
	@ModelAttribute("labtech")
	public Boolean getLabTech() {
		User mdrUser = Context.getAuthenticatedUser();
		
		if (!mdrUser.isSuperUser() && mdrUser.hasRole("Lab Tech")) {
			return new Boolean(true);
			
		}
		
		else {
			return new Boolean(false);
			
		}
	}
	
	@RequestMapping("/module/mdrtb/dashboard/dashboard.form")
	public ModelAndView showStatus(@ModelAttribute("program") MdrtbPatientProgram program,
	        @RequestParam(required = false, value = "patientId") Integer patientId, ModelMap map) {
		if (program == null) {
			// if the patient has no program, redirect to the enroll-in-program
			map.clear();
			return new ModelAndView("redirect:/module/mdrtb/program/showEnroll.form?patientId=" + patientId);
		}
		
		// add the patient program ID
		map.put("patientProgramId", program.getId());
		
		// add the patientId
		map.put("patientId", program.getPatient().getId());
		
		// now add the status items
		Map<String, Status> statusMap = new HashMap<String, Status>();
		
		// lab reports status
		Status labReportsStatus = new LabResultsStatusCalculator(new DashboardLabResultsStatusRenderer()).calculate(program);
		statusMap.put("labResultsStatus", labReportsStatus);
		
		// treatment status
		Status treatmentStatus = new TreatmentStatusCalculator(new DashboardTreatmentStatusRenderer()).calculate(program);
		statusMap.put("treatmentStatus", treatmentStatus);
		
		// visits status
		Status visitStatus = new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(program);
		statusMap.put("visitStatus", visitStatus);
		
		// hiv status
		Status hivStatus = new HivStatusCalculator(new DashboardHivStatusRenderer()).calculate(program.getPatient());
		statusMap.put("hivStatus", hivStatus);
		
		map.put("status", statusMap);
		
		// add any flags
		addFlags(statusMap, map);
		
		List<Encounter> tb03uList = Context.getService(MdrtbService.class).getEncountersWithNoProgram(
		    MdrtbConstants.ET_TB03U_MDRTB_INTAKE, program.getPatient());
		map.put("unlinkedtb03us", tb03uList);
		
		List<Encounter> labList = Context.getService(MdrtbService.class).getEncountersWithNoProgram(
		    MdrtbConstants.ET_SPECIMEN_COLLECTION, program.getPatient());
		map.put("unlinkedlabs", labList);
		
		Integer xpertFormId = -1;
		if (Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_XPERT_FORM_ID) != null) {
			xpertFormId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_XPERT_FORM_ID));
		}
		
		Integer smearFormId = -1;
		if (Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_SMEAR_FORM_ID) != null) {
			smearFormId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_SMEAR_FORM_ID));
		}
		
		Integer cultureFormId = -1;
		if (Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_CULTURE_FORM_ID) != null) {
			cultureFormId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_CULTURE_FORM_ID));
		}
		
		Integer hainFormId = -1;
		if (Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_HAIN_FORM_ID) != null) {
			hainFormId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_HAIN_FORM_ID));
		}
		
		Integer dstFormId = -1;
		if (Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DST_FORM_ID) != null) {
			dstFormId = Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DST_FORM_ID));
		}
		
		map.put("xpertFormId", xpertFormId);
		map.put("smearFormId", smearFormId);
		map.put("cultureFormId", cultureFormId);
		map.put("hainFormId", hainFormId);
		map.put("dstFormId", dstFormId);
		
		Integer showTb03uXDR = 0;
		
		if (program.getTb03uXDR() == null) {
			int preXdrId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRE_XDR_TB).getId().intValue();
			int xdrId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB).getId().intValue();
			int tdrId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TDR_TB).getId().intValue();
			Concept resDuringTx = Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.DRUG_RESISTANCE_DURING_TREATMENT);
			List<DrugResistanceDuringTreatmentForm> drdts = Context.getService(MdrtbService.class).getDrdtForms(
			    program.getId());
			if (drdts != null && drdts.size() != 0) {
				for (DrugResistanceDuringTreatmentForm drdt : drdts) {
					Obs temp = MdrtbUtil.getObsFromEncounter(resDuringTx, drdt.getEncounter());
					if (temp != null) {
						int cid = temp.getValueCoded().getId().intValue();
						
						if (cid == preXdrId || cid == xdrId || cid == tdrId) {
							showTb03uXDR = 1;
							break;
						}
					}
				}
			}
			
			else
				showTb03uXDR = 0;
		}
		
		else {
			showTb03uXDR = 1;
		}
		
		map.addAttribute("showtb03uxdr", showTb03uXDR);
		return new ModelAndView("/module/mdrtb/dashboard/dashboard", map);
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/programEdit.form", method = RequestMethod.POST)
	public ModelAndView processEditPopup(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors,
	        @RequestParam(required = false, value = "causeOfDeath") Concept causeOfDeath, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		// perform validation 
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			map.put("programEditErrors", errors);
			// call the show status method to redisplay the page with errors
			return showStatus(program, null, map);
		}
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		
		// mark the patient as died if required
		ProgramWorkflowState patientDied = Context.getService(MdrtbService.class).getProgramWorkflowState(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED));
		if (program.getOutcome() != null && program.getOutcome().equals(patientDied) && !program.getPatient().getDead()) {
			Context.getPatientService().processDeath(
			    program.getPatient(),
			    program.getDateCompleted(),
			    (causeOfDeath != null ? causeOfDeath : Context.getService(MdrtbService.class).getConcept(
			        MdrtbConcepts.UNKNOWN)), null);
		}
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + program.getId());
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/programClose.form", method = RequestMethod.POST)
	public ModelAndView processClosePopup(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors,
	        @RequestParam(required = false, value = "causeOfDeath") Concept causeOfDeath, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		// perform validation 
		if (program != null) {
			program.validate(program, errors);
		}
		
		if (errors.hasErrors()) {
			map.put("programCloseErrors", errors);
			// call the show status method to redisplay the page with errors
			return showStatus(program, null, map);
		}
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		
		// mark the patient as died if required
		ProgramWorkflowState patientDied = Context.getService(MdrtbService.class).getProgramWorkflowState(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED));
		if (program.getOutcome() != null && program.getOutcome().equals(patientDied) && !program.getPatient().getDead()) {
			Context.getService(MdrtbService.class).processDeath(
			    program.getPatient(),
			    program.getDateCompleted(),
			    (causeOfDeath != null ? causeOfDeath : Context.getService(MdrtbService.class).getConcept(
			        MdrtbConcepts.UNKNOWN)));
		}
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + program.getId());
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/hospitalizationsEdit.form", method = RequestMethod.POST)
	public ModelAndView editHospitalization(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors,
	        @ModelAttribute("hospitalizationState") PatientState hospitalizationState, BindingResult patientStateErrors,
	        @RequestParam(required = false, value = "startDate") Date admissionDate,
	        @RequestParam(required = false, value = "endDate") Date dischargeDate, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		// add the hospitalization if necessary
		if (hospitalizationState == null) {
			program.addHospitalization(admissionDate, dischargeDate);
		}
		
		// perform validation 
		if (program != null) {
			MdrtbPatientProgramHospitalizationValidator validator = new MdrtbPatientProgramHospitalizationValidator();
			validator.validate(program, errors);
			
			// also validate that the new date is accurate
		}
		
		if (errors.hasErrors()) {
			// add the errors to the map
			map.put("hospitalizationErrors", errors);
			// add the current values to the map
			map.put("admissionDate", admissionDate);
			map.put("dischargeDate", dischargeDate);
			map.put("hospitalizationStateId", (hospitalizationState != null ? hospitalizationState.getId() : ""));
			// call the show status method to redisplay the page with errors
			return showStatus(program, null, map);
		}
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + program.getId());
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/hospitalizationsDelete.form", method = RequestMethod.GET)
	public ModelAndView deleteHospitalization(@ModelAttribute("program") MdrtbPatientProgram program,
	        BindingResult programErrors, @ModelAttribute("hospitalizationState") PatientState hospitalizationState,
	        BindingResult patientStateErrors, SessionStatus status, HttpServletRequest request, ModelMap map) {
		
		// remove the hospitalizations
		program.removeHospitalization(hospitalizationState);
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientProgramId=" + program.getId());
		
	}
	
	private void addFlags(Map<String, Status> statusMap, ModelMap map) {
		
		// calculate flags
		List<StatusFlag> flags = new LinkedList<StatusFlag>();
		
		for (Status status : statusMap.values()) {
			if (status.getFlags() != null && !status.getFlags().isEmpty()) {
				flags.addAll(status.getFlags());
			}
		}
		
		map.put("flags", flags);
		
	}
	
	@RequestMapping(value = "/module/mdrtb/program/addEncounterMdrtb.form", method = RequestMethod.GET)
	public ModelAndView processAddEncounter(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors,
	        @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = true, value = "encounterId") Integer encounterId, SessionStatus status,
	        HttpServletRequest request, ModelMap map) {
		
		Context.getService(MdrtbService.class).addProgramIdToEncounter(encounterId, patientProgramId);
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId="
		        + Context.getProgramWorkflowService().getPatientProgram(patientProgramId).getPatient().getId()
		        + "&patientProgramId=" + patientProgramId);
		
	}
}
