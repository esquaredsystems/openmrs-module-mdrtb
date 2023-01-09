package org.openmrs.module.mdrtb.web.controller;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService.ATTR_VIEW_TYPE;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.PatientValidator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.web.controller.command.PatientCommand;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PatientIdentifierTypeEditor;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;
import org.openmrs.web.controller.person.PersonFormController;
import org.openmrs.web.dwr.PatientListItem;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
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
@RequestMapping("/module/mdrtb/mdrtbEditPatient.form")
public class MdrtbEditPatientController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10));
		
		// register other custom binders
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
	}
	
	@ModelAttribute("patientId")
	public Integer getPatientId(@RequestParam(required = false, value = "patientId") Integer patientId) {
		return patientId;
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = false, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("successURL")
	public String getSuccessUrl(@RequestParam(required = false, value = "successURL") String successUrl) {
		// as a default, just reload the same page
		if (StringUtils.isBlank(successUrl)) {
			successUrl = "mdrtbEditPatient.form";
		}
		
		return successUrl;
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getPossibleLocations() {
		List<Location> list = new ArrayList<Location>();
		list.add(Context.getLocationService().getLocation(1));
		return list;
		//return Context.getLocationService().getAllLocations(false);
	}
	
	// checks to see if the "fixedIdentifierLocation" global prop has been specified, which is used to determine if we
	// should show the location selector for identifiers
	@ModelAttribute("showIdentifierLocationSelector")
	public boolean getShowIdentifierLocationSelector() {
		return StringUtils.isBlank(Context.getAdministrationService().getGlobalProperty(
		    MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION));
	}
	
	@ModelAttribute("patientIdentifierMap")
	public Map<Integer, PatientIdentifier> getPatientIdentifierMap(
	        @RequestParam(required = false, value = "patientId") Integer patientId) {
		
		final Map<Integer, PatientIdentifier> map = new HashMap<Integer, PatientIdentifier>();
		
		if (patientId != null && patientId != -1) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			
			if (patient != null) {
				for (PatientIdentifierType type : Context.getPatientService().getAllPatientIdentifierTypes()) {
					map.put(type.getId(), patient.getPatientIdentifier(type));
				}
			}
		}
		
		return map;
	}
	
	@ModelAttribute("mdrtbIdentifier")
	public PatientIdentifierType getMdrtbIdentifierType() {
		
		return Context.getPatientService().getPatientIdentifierTypeByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE));
	}
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("patientIdentifierTypesAutoAssigned")
	public List<PatientIdentifierType> getPatientIdentifierTypesAutoAssigned() {
		// this is only relevant if we are using the idgen module
		if (!ModuleFactory.getStartedModulesMap().containsKey("idgen")) {
			return new LinkedList<PatientIdentifierType>(); // return an empty list
		} else {
			// access the idgen module via reflection
			try {
				Class<?> identifierSourceServiceClass = Context
				        .loadClass("org.openmrs.module.idgen.service.IdentifierSourceService");
				Object idgen = Context.getService(identifierSourceServiceClass);
				Method getPatientIdentifierTypesByAutoGenerationOption = identifierSourceServiceClass.getMethod(
				    "getPatientIdentifierTypesByAutoGenerationOption", Boolean.class, Boolean.class);
				
				return (List<PatientIdentifierType>) getPatientIdentifierTypesByAutoGenerationOption.invoke(idgen, false,
				    true);
			}
			catch (Exception e) {
				log.error(
				    "Unable to access IdentifierSourceService for automatic id generation.  Is the Idgen module installed and up-to-date?",
				    e);
				return new LinkedList<PatientIdentifierType>(); // return an empty list
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("patientIdentifierTypes")
	public List<PatientIdentifierType> getPatientIdentifierTypes() {
		return ListUtils.subtract(Context.getPatientService().getAllPatientIdentifierTypes(),
		    getPatientIdentifierTypesAutoAssigned());
	}
	
	@ModelAttribute("patientCommand")
	public PatientCommand getPatient(@RequestParam(required = false, value = "patientId") Integer patientId,
	        @RequestParam(required = false, value = "addName") String addName,
	        @RequestParam(required = false, value = "addBirthdate") String addBirthdate,
	        @RequestParam(required = false, value = "addAge") String addAge,
	        @RequestParam(required = false, value = "addGender") String addGender, HttpServletRequest request) {
		
		Patient patient = null;
		
		// see if we have a patient id (-1 signifies that we are looking to add a new patient)
		if (patientId != null && patientId != -1) {
			patient = Context.getPatientService().getPatient(patientId);
			
			if (patient == null) {
				throw new APIException("Invalid patient id passed to edit patient controller");
			}
		} else {
			// handle a new patient
			patient = new Patient();
			
			// initialize with any request parameters that may have been passed
			if (addName != null) {
				PersonFormController.getMiniPerson(patient, addName, addGender, addBirthdate, addAge);
			}
		}
		
		// if there is no default name for this patient, create one
		if (patient.getPersonName() == null) {
			PersonName name = new PersonName();
			name.setPreferred(true);
			patient.addName(name);
		}
		
		// if we are handling a form submission, make sure we initialize all the person attributes so we can bind to them
		if (request.getMethod().equals("POST")) {
			for (PersonAttributeType attr : Context.getPersonService().getPersonAttributeTypes(PERSON_TYPE.PATIENT,
			    ATTR_VIEW_TYPE.VIEWING)) {
				if (attr != null && patient.getAttribute(attr) == null) {
					patient.getAttributes().add(new PersonAttribute(attr, null));
				}
			}
		}
		
		PatientCommand patientCommand = new PatientCommand();
		patientCommand.setPatient(patient);
		
		// if there is no default address for this patient, create one
		PersonAddress address = patient.getPersonAddress();
		if (address == null) {
			address = new PersonAddress();
		}
		// add the address to the command object separately
		patientCommand.setAddress(address);
		
		return patientCommand;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = false, value = "patientId") Integer patientId,
	        @RequestParam(required = false, value = "addName") String addName,
	        @RequestParam(required = false, value = "addBirthdate") Date addBirthdate,
	        @RequestParam(required = false, value = "addAge") String addAge,
	        @RequestParam(required = false, value = "addGender") String addGender,
	        @RequestParam(required = false, value = "add") String add,
	        @RequestParam(required = false, value = "skipSimilarCheck") Boolean skipSimilarCheck, ModelMap map)
	        throws ParseException {
		
		// if we are dealing with a new patient (one with no id, or id=-1) we need to check for similar patients first
		if ((skipSimilarCheck == null || !skipSimilarCheck) && (patientId == null || patientId == -1)) {
			
			Integer birthYear = null;
			
			if (addBirthdate != null) {
				Calendar birthDate = Calendar.getInstance();
				birthDate.setTime(addBirthdate);
				birthYear = birthDate.get(Calendar.YEAR);
			} else if (StringUtils.isNotBlank(addAge)) {
				Calendar currentDate = Calendar.getInstance();
				currentDate.setTime(new Date());
				birthYear = currentDate.get(Calendar.YEAR) - Integer.valueOf(addAge);
			}
			
			Set<Person> similarPersons = Context.getPersonService().getSimilarPeople(addName, birthYear, addGender);
			Set<PatientListItem> similarPatients = new HashSet<PatientListItem>();
			String primaryIdentifier = Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE);
			
			// we only want to pass on similar persons who are patients in this case
			for (Person person : similarPersons) {
				if (person instanceof Patient) {
					PatientListItem patientListItem = new PatientListItem((Patient) person);
					
					// make sure the correct patient identifier is set on the patient list item
					if (StringUtils.isNotBlank(primaryIdentifier)) {
						PatientIdentifier pi = ((Patient) person).getPatientIdentifier(primaryIdentifier);
						if (pi != null) {
							patientListItem.setIdentifier(pi.getIdentifier());
						}
					}
					
					similarPatients.add(patientListItem);
				}
			}
			
			if (similarPatients.size() > 0) {
				map.put("patients", similarPatients);
				
				// add the request params to the map so that we can pass them on
				map.put("addName", addName);
				map.put("addBirthdate", addBirthdate);
				map.put("addAge", addAge);
				map.put("addGender", addGender);
				
				return new ModelAndView("/module/mdrtb/similarPatients");
			}
		}
		// if no similar patients, show the edit page
		map.put("add", add);
		return new ModelAndView("/module/mdrtb/mdrtbEditPatient");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView submitForm(@ModelAttribute("patientCommand") PatientCommand patientCommand, BindingResult result,
	        @RequestParam("identifierValue") String[] identifierValue, @RequestParam("identifierId") String[] identifierId,
	        @RequestParam(required = false, value = "identifierLocation") Location[] identifierLocation,
	        @RequestParam(required = false, value = "identifierType") PatientIdentifierType[] identifierType,
	        @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId,
	        @RequestParam(required = false, value = "add") String add, @RequestParam("successURL") String successUrl,
	        SessionStatus status, ModelMap map) {
		
		// fetch the patient off the command object
		Patient patient = patientCommand.getPatient();
		
		// first, we need to set the patient id to null if it's been set to -1
		if (patient.getId() != null && patient.getId() == -1) {
			patient.setId(null);
		}
		
		// if a fixed patient identifier location has been set, get it
		Location fixedLocation = null;
		String fixedLocationName = Context.getAdministrationService().getGlobalProperty(
		    MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION);
		if (StringUtils.isNotBlank(fixedLocationName)) {
			fixedLocation = Context.getLocationService().getLocation(fixedLocationName);
			if (fixedLocation == null) {
				throw new MdrtbAPIException("Location referenced by global prop "
				        + MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION + "does not exist.");
			}
		}
		
		// handle patient identifiers
		if (identifierValue != null) {
			for (Integer i = 0; i < identifierValue.length; i++) {
				
				//  if this identifier is blank and the idgen module is installed, see if we need to auto-generate this identifier
				if (StringUtils.isBlank(identifierValue[i]) && ModuleFactory.getStartedModulesMap().containsKey("idgen")) {
					identifierValue[i] = MdrtbUtil.assignIdentifier(identifierType[i]);
				}
				
				// update any existing identifiers (ones with ids)
				if (StringUtils.isNotBlank(identifierId[i])) {
					PatientIdentifier identifier = getIdentifierById(Integer.valueOf(identifierId[i]), patient);
					
					// if there is a value, update it
					if (StringUtils.isNotBlank(identifierValue[i])) {
						identifier.setIdentifier(identifierValue[i]);
						
						// only update the location if it hasn't been set to be fixed
						if (fixedLocation == null) {
							identifier.setLocation(identifierLocation[i]);
						}
					} else {
						// otherwise, remove it
						patient.removeIdentifier(identifier);
					}
				}
				// now add any identifiers that have a value, but no id
				else if (StringUtils.isNotBlank(identifierValue[i])) {
					PatientIdentifier identifier = new PatientIdentifier(identifierValue[i], identifierType[i],
					        (fixedLocation == null ? identifierLocation[i] : fixedLocation));
					
					// set this identifier as preferred if it is of the preferred tyoe
					String preferredIdentifierTypeName = Context.getAdministrationService().getGlobalProperty(
					    MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE);
					if (StringUtils.isNotBlank(preferredIdentifierTypeName)) {
						PatientIdentifierType preferredIdentifierType = Context.getPatientService()
						        .getPatientIdentifierTypeByName(preferredIdentifierTypeName);
						if (preferredIdentifierType != null && preferredIdentifierType == identifierType[i]) {
							identifier.setPreferred(true);
						}
					}
					
					patient.addIdentifier(identifier);
				}
			}
		}
		// perform validation
		
		// (don't know if this is the exact right thing to do, but we need to create a new binding result
		// since we are validating the patient, not the patient command)
		result = new BeanPropertyBindingResult(patient, "patient");
		PatientValidator validator = new PatientValidator();
		validator.validate(patient, result);
		if (result.hasErrors() || identifierId.length == 0) {
			map.put("errors", result);
			map.put("add", add);
			return new ModelAndView("/module/mdrtb/mdrtbEditPatient", map);
		}
		
		// sync up the patient and person voided attributes
		// TODO: is this correct... do we ever want to void a patient but keep the person (for instance, if the person is also a treatment supporter?)
		patient.setPersonVoided(patient.getVoided());
		if (patient.getVoided()) {
			patient.setPersonVoidReason(patient.getVoidReason());
		}
		
		// if this is a new address (ie, not yet linked to patient), link it to the patient
		if (patientCommand.getAddress() != null) {
			patientCommand.getAddress().setPreferred(true);
			patient.addAddress(patientCommand.getAddress());
		}
		
		// remove the address if it is blank
		//		if (MdrtbUtil.isBlank(patient.getPersonAddress())) {
		//			patient.removeAddress(patient.getPersonAddress());
		//		}
		// remove any attributes that are blank
		for (PersonAttributeType attr : Context.getPersonService().getPersonAttributeTypes(PERSON_TYPE.PATIENT,
		    ATTR_VIEW_TYPE.VIEWING)) {
			if (patient.getAttribute(attr) != null && StringUtils.isBlank(patient.getAttribute(attr).getValue())) {
				patient.removeAttribute(patient.getAttribute(attr));
			}
		}
		
		// save the patient
		Context.getPatientService().savePatient(patient);
		
		// if the patient has been set to dead, exit him/her from care
		if (patient.getDead()) {
			Context.getService(MdrtbService.class).processDeath(patient, patient.getDeathDate(), patient.getCauseOfDeath());
		}
		
		Integer idId = null;
		if ("1".equals(add)) {
			idId = Context.getPatientService()
			        .getPatientIdentifiers(identifierValue[0], null, null, Arrays.asList(patient), null).get(0).getId();
		}
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		String returnUrl;
		
		if (add == null || add.length() == 0) {
			returnUrl = "redirect:/module/mdrtb/program/enrollment.form?patientId=" + patient.getId();
		}
		returnUrl = "redirect:" + successUrl + (successUrl.contains("?") ? "&" : "?") + "patientId=" + patient.getId()
		        + (patientProgramId != null ? "&patientProgramId=" + patientProgramId : "")
		        + (idId != null ? "&idId=" + idId : "");
		return new ModelAndView(returnUrl);
	}
	
	/**
	 * Utility methods
	 */
	
	private PatientIdentifier getIdentifierById(Integer id, Patient patient) {
		for (PatientIdentifier identifier : patient.getIdentifiers()) {
			if (identifier != null && identifier.getId() != null && identifier.getId().equals(id)) {
				return identifier;
			}
		}
		return null;
	}
}
