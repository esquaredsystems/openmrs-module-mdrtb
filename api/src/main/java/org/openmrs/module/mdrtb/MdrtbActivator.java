/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.mdrtb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.LocationTag;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressSupport;
import org.openmrs.layout.address.AddressTemplate;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MdrtbActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see #started()
	 */
	public void started() {
		log.info("Starting up MDR-TB module.");
		//registerAddressTemplates();
		performCustomMigrations();
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down MDR-TB module.");
		unregisterAddressTemplates();
	}
	
	public void contextRefreshed() {
		configureGlobalProperties();
		integrityCheck();
	}
	
	/**
	 * Sets up the Default Address Template
	 */
	public void registerAddressTemplates() {
		
		log.info("Registering default address format.");
		AddressTemplate at = new AddressTemplate("default");
		at.setDisplayName("Default Address Format");
		at.setCountry("default");
		Map<String, String> nameMappings = new HashMap<String, String>();
		nameMappings.put("cityVillage", "Location.cityVillage");
		nameMappings.put("address1", "PersonAddress.address1");
		at.setNameMappings(nameMappings);
		Map<String, String> sizeMappings = new HashMap<String, String>();
		sizeMappings.put("cityVillage", "20");
		sizeMappings.put("address1", "60");
		at.setSizeMappings(sizeMappings);
		Map<String, String> elementDefaults = new HashMap<String, String>();
		elementDefaults.put("country", "default");
		at.setElementDefaults(elementDefaults);
		at.setLineByLineFormat(Arrays.asList("cityVillage address1"));
		AddressSupport.getInstance().getLayoutTemplates().add(at);
	}
	
	/**
	 * Unregisters the Default Address Template
	 */
	public void unregisterAddressTemplates() {
		for (Iterator<AddressTemplate> i = AddressSupport.getInstance().getLayoutTemplates().iterator(); i.hasNext();) {
			AddressTemplate at = i.next();
			if ("default".equals(at.getCodeName())) {
				i.remove();
			}
		}
	}
	
	/**
	 * Configures any global properties that need to be configured
	 */
	private void configureGlobalProperties() {
		
		//TODO: ONLY for Tajikistan implementation. Once in production, delete these lines
		deleteGlobalProperty("dotsreports.");
		deleteGlobalProperty("mdrtbdrugforecast.");
		
		String FILE_EXTENSIONS_NAMES = ".bmp ,.jpg ,.jpeg,.jfif,.GIF,.png,.bat,.BPG,.FLV,.AVI,.MOV,.M4P,.MPG,.WMV,.3gp,.RM,.SWF,.3GP,.ACT,.AIFF,.MP3,.WAV,.OGG,.FLAC,.AU,.RAW,.docx,.docm,.dotx,.docb,.dotm,.pdf";
		setGlobalProperty("mdrtb.fileExtensions", FILE_EXTENSIONS_NAMES);
		
		/** Encounter Types **/
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_ADVERSE_EVENT, "Adverse Event",
		    "EXACT name of the encounter type for Adverse events", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_IN, "Transfer In",
		    "EXACT name of the encounter type for Transfer In of Patients", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_OUT, "Transfer Out",
		    "EXACT name of the encounter type for Transfer Out of Patients", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION, "Specimen Collection",
		    "EXACT name of the encounter type for lab Specimen Collection", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_FORM_89, "Form 89",
		    "EXACT name of the encounter type for TB-DOTS Follow-up (Form 89)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03, "TB03",
		    "EXACT name of the encounter type for TB Intake (TB03)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_MDR, "TB03u - MDR",
		    "EXACT name of the encounter type for MDR-TB Intake (TB03u)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_XDR, "TB03u - XDR",
		    "EXACT name of the encounter type for XDR-TB Intake (TB03u)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_LAB_RESULT, "Lab Result",
		    "EXACT name of the encounter type for Lab Results", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT, "Resistance During Treatment",
		    "EXACT name of the encounter type to record drug resistance during treatment", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_PV_REGIMEN, "PV Regimen",
		    "EXACT name of the encounter type for PV Regimen", null);
		
		//TODO: CRITICALLY IMPORTANT TO TEST THIS THOROUGHLY
		/** Concepts **/
		ConceptService conceptService = Context.getConceptService();
		Map<String, String> globalPropertyConceptMap = new HashMap<String, String>();
		globalPropertyConceptMap.put(MdrtbConstants.GP_NEW_CONCEPT_ID, MdrtbConcepts.NEW);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OTHER_CONCEPT_ID, MdrtbConcepts.OTHER);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID, MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_DEFAULT2_CONCEPT_ID, MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID, MdrtbConcepts.FAILURE_AFTER_REGIMEN_1);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_FAILURE2_CONCEPT_ID, MdrtbConcepts.FAILURE_AFTER_REGIMEN_2);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID, MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		globalPropertyConceptMap.put(MdrtbConstants.GP_AFTER_RELAPSE2_CONCEPT_ID, MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_CANCELED_CONCEPT_ID, MdrtbConcepts.CANCELLED);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_CURED_CONCEPT_ID, MdrtbConcepts.CURED);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID, MdrtbConcepts.DIED);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_LTFU_CONCEPT_ID, MdrtbConcepts.LOST_TO_FOLLOWUP);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_STARTED_SLD_CONCEPT_ID, MdrtbConcepts.STARTED_SLD_TREATMENT);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID,
		    MdrtbConcepts.PATIENT_TRANSFERRED_OUT);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_TX_COMPLETED_CONCEPT_ID, MdrtbConcepts.TREATMENT_COMPLETE);
		globalPropertyConceptMap.put(MdrtbConstants.GP_OUTCOME_TX_FAILURE_CONCEPT_ID, MdrtbConcepts.TREATMENT_FAILED);
		globalPropertyConceptMap.put(MdrtbConstants.GP_TRANSFER_IN_CONCEPT_ID, MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		for (Entry<String, String> entry : globalPropertyConceptMap.entrySet()) {
			Concept concept = conceptService.getConcept(entry.getValue());
			if (concept != null) {
				setGlobalProperty(entry.getKey(), concept.getConceptId().toString());
			}
		}
		
		/** Form IDs **/
		setGlobalProperty(MdrtbConstants.GP_CULTURE_FORM_ID, "12", "Form ID of Culture test", null);
		setGlobalProperty(MdrtbConstants.GP_DST_FORM_ID, "11", "Form ID of DST test", null);
		setGlobalProperty(MdrtbConstants.GP_HAIN_FORM_ID, "15", "Form ID of HAIN test", null);
		setGlobalProperty(MdrtbConstants.GP_SMEAR_FORM_ID, "8", "Form ID of Smear test", null);
		setGlobalProperty(MdrtbConstants.GP_XPERT_FORM_ID, "14", "Form ID of GeneXpert test", null);
		setGlobalProperty(MdrtbConstants.GP_BACTERIOLOGY_ENTRY_FORM_ID, "", "", null);
		
		/** Miscellaneous **/
		//TODO: Put in TB patient ID instead of OpenMRS Identification Number
		setGlobalProperty(MdrtbConstants.GP_OPENMRS_PRIMARY_IDENTIFIER_TYPE, "OpenMRS Identification Number");
		setGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME, "MDR-TB PROGRAM", "", null);
		setGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME, "DOTS PROGRAM", "", null);
		setGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE, "Регистрационный номер Дотc");
		setGlobalProperty(MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE, "Регистрационный номер МЛУ");
		setGlobalProperty(
		    MdrtbConstants.GP_COLOR_MAP,
		    "CONTAMINATED:lightgrey|UNSATISFACTORY SAMPLE:lightgrey|MODERATELY POSITIVE:lightcoral|STRONGLY POSITIVE:lightcoral|WEAKLY POSITIVE:lightcoral|WAITING FOR TEST RESULTS:lightgrey|SCANTY:khaki|NEGATIVE:lightgreen|POSITIVE:lightcoral|SUSCEPTIBLE TO TUBERCULOSIS DRUG:lightgreen|INDETERMINATE TO TUBERCULOSIS DRUG:khaki|RESISTANT TO TUBERCULOSIS DRUG:lightcoral|NONE:none",
		    "Pipe-delimited list mapping concept ids or concept mappings to color names and\\/or hex codes for display in the patient chart and (potentially) elsewhere.",
		    null);
		setGlobalProperty(MdrtbConstants.GP_MDRTB_BIRT_REPORT_LIST, "",
		    "List all BIRT reports by name that correspond to the MDR-TB program. Pipe delimited.", null);
		
		setGlobalProperty(MdrtbConstants.GP_DEFAULT_DST_DRUGS, "ISONIAZID|RIFAMPICIN|ETHAMBUTOL|PYRAZINAMIDE|STREPTOMYCIN",
		    "The list of TB drugs, separated by pipe sign. This should be replaced with a concept set ID.", null);
		setGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_CULTURE, "14",
		    "Stores the days since culture variable for the specimen reports.", null);
		setGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_SMEAR, "14",
		    "Stores the days since snear variable for the specimen reports.", null);
		setGlobalProperty(
		    MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION,
		    "Takes as a parameter a location name. If this property exists, when creating patient identifiers, the patient identifier location is set to this value (and setting the location is not presented to the user as an option).",
		    "", null);
		setGlobalProperty("mdrtb.findPatientNumResults", "20", "Number of results to display when searching for patient",
		    null);
		setGlobalProperty(MdrtbConstants.GP_PATIENT_IDENTIFIER_TYPE_LIST, "", "", null);
		setGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DEFAULT_LAB, "Бохтар (38)",
		    "Stores the default location name for the specimen reports. This must be replaced with a better way", null);
		
		setGlobalProperty(
		    "mdrtb.performCustomMigrations",
		    "false",
		    "Caution! This is specific to ETB-Tajikistan implementation. When True, the custom migration scripts executes and turns this value back to false on success. It should run only once",
		    null);
	}
	
	private void setGlobalProperty(String prop, String val) {
		setGlobalProperty(prop, val, null, null);
	}
	
	/**
	 * Creates {@link GlobalProperty}. Updates the existing object ONLY if its value is empty
	 * 
	 * @param prop
	 * @param val
	 * @param description
	 * @param uuid
	 */
	private void setGlobalProperty(String prop, String val, String description, String uuid) {
		AdministrationService service = Context.getAdministrationService();
		GlobalProperty gp = service.getGlobalPropertyObject(prop);
		if (gp == null) {
			gp = new GlobalProperty(prop, val, description);
			gp.setUuid(uuid == null ? UUID.randomUUID().toString() : uuid);
			gp = service.saveGlobalProperty(gp);
			// Check if it actually got saved
			GlobalProperty exists = service.getGlobalPropertyObject(prop);
			System.out.println(exists.getUuid());
		} else if (StringUtils.isEmpty(gp.getPropertyValue())) {
			System.out.println("Setting/updating global property: " + prop);
			gp.setPropertyValue(val);
			gp.setDescription(description);
			gp = service.saveGlobalProperty(gp);
		}
	}
	
	private void deleteGlobalProperty(String propertyStartsWith) {
		for (GlobalProperty gp : Context.getAdministrationService().getAllGlobalProperties()) {
			if (gp.getProperty().startsWith(propertyStartsWith)) {
				Context.getAdministrationService().purgeGlobalProperty(gp);
			}
		}
	}
	
	/**
	 * Checks the integrity of the module: 1. Check all global properties 2. Check all encounter
	 * types 3. Check all module concepts
	 */
	private void integrityCheck() {
		
		System.out.println("Checking metadata integrity...");
		Field[] declaredFields = MdrtbConstants.class.getDeclaredFields();
		for (Field field : declaredFields) {
			field.setAccessible(true);
			boolean isPublic = Modifier.isPublic(field.getModifiers());
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			if (isPublic && isStatic) {
				// All public static final String variables starting with GP_ represent Global properties
				if (field.getName().startsWith("GP_") && field.getType() == String.class) {
					try {
						String property = Context.getAdministrationService().getGlobalProperty(field.get(field).toString());
						if (property.isEmpty()) {
							throw new APIException("Value NULL or empty");
						}
					}
					catch (Exception e) {
						System.out.println("Global property not found for: " + field.getName() + "\n" + e.getMessage());
					}
				}
			}
		}
	}
	
	/**
	 * Perform any custom migrations required due to changes in the data model
	 */
	private void performCustomMigrations() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.performCustomMigrations");
		try {
			if (!gp.getValue().toString().equalsIgnoreCase("true")) {
				return;
			}
		}
		catch (Exception e) {
			return;
		}
		
		// Create location tags (if they don't already exist)
		List<LocationTag> locationTags = new LinkedList<>();
		locationTags.add(new LocationTag("DOTS Facility", "Location allows DOTS patient enrollment"));
		locationTags.add(new LocationTag("MDRTB Facility", "Location allows MDR-TB patient enrollment"));
		locationTags.add(new LocationTag("Login Location", "Allow user to Login from this location"));
		locationTags.add(new LocationTag("Admission Location", "Patients may only be admitted to inpatient care"));
		locationTags.add(new LocationTag("Transfer Location", "Patients can be transferred into this location"));
		locationTags.add(new LocationTag("Laboratory", "If this is a Laboratory"));
		locationTags.add(new LocationTag("Culture Lab", "This is a Laboratory providing Culture tests"));
		locationTags.add(new LocationTag("Prison", "This location represents a Prison or Jail"));
		for (LocationTag tag : locationTags) {
			LocationTag exists = Context.getLocationService().getLocationTagByName(tag.getName());
			if (exists == null) {
				Context.getLocationService().saveLocationTag(tag);
			}
		}
		
		// Retire unwanted Encounter types
		String[] types = {"ADULTINITIAL", "ADULTRETURN", "PEDSINITIAL", "PEDSRETURN", "MDR-TB Follow Up", "MDR-TB Intake", "MDRTB DOT", "CONTACT SCREENING"};
		for (String type : types) {
			try {
				EncounterType encounterType = Context.getEncounterService().getEncounterType(type);
				if (encounterType != null) {
					Context.getEncounterService().retireEncounterType(encounterType, "No longer in use");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//TODO: Convert Lab results to Common Lab module schema; Enc-Obs to LabTest-Attributes

		// After completion, turn the global property back to false
		gp.setPropertyValue("false");
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}
