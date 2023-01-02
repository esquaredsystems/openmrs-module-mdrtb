/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressSupport;
import org.openmrs.layout.address.AddressTemplate;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.mdrtb.service.MdrtbService;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MdrtbActivator extends BaseModuleActivator implements ModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void started() {
		log.info("Starting up MDR-TB module.");
		configureGlobalProperties();
		registerAddressTemplates();
		performCustomMigrations();
	}
	
	@Override
	public void stopped() {
		log.info("Shutting down MDR-TB module.");
		unregisterAddressTemplates();
	}
	
	@Override
	public void contextRefreshed() {
		// initialize the caches on module startup
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

		String FILE_EXTENSIONS_NAMES = ".bmp ,.jpg ,.jpeg,.jfif,.GIF,.png,.bat,.BPG,.FLV,.AVI,.MOV,.M4P,.MPG,.WMV,.3gp,.RM,.SWF,.3GP,.ACT,.AIFF,.MP3,.WAV,.OGG,.FLAC,.AU,.RAW,.docx,.docm,.dotx,.docb,.dotm,.pdf";
		setGlobalProperty("mdrtb.fileExtensions", FILE_EXTENSIONS_NAMES);
		
		//TODO: CRITICALLY IMPORTANT TO TEST THIS THOROUGHLY
		/** Concepts **/
		MdrtbService mdrtb = Context.getService(MdrtbService.class);
		setGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.NEW).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OTHER_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.OTHER).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT2_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_FAILURE2_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_AFTER_RELAPSE2_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_CANCELED_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.CANCELLED).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_CURED_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.CURED).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.DIED).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_LTFU_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.LOST_TO_FOLLOWUP).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_STARTED_SLD_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.STARTED_SLD_TREATMENT).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_OUT).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_TX_COMPLETED_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.TREATMENT_COMPLETE).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_OUTCOME_TX_FAILURE_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.TREATMENT_FAILED).getConceptId().toString());
		setGlobalProperty(MdrtbConstants.GP_TRANSFER_IN_CONCEPT_ID, mdrtb.getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN).getConceptId().toString());

		/** Encounter Types **/
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_ADVERSE_EVENT, "Adverse Event", "EXACT name of the encounter type for Adverse events", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_IN, "Transfer In", "EXACT name of the encounter type for Transfer In of Patients", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TRANSFER_OUT, "Transfer Out", "EXACT name of the encounter type for Transfer Out of Patients", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_SPECIMEN_COLLECTION, "Specimen Collection", "EXACT name of the encounter type for lab Specimen Collection", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_FORM_89, "From 89", "EXACT name of the encounter type for TB-DOTS Follow-up (Form 89)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03, "TB03", "EXACT name of the encounter type for TB Intake (TB03)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_MDR, "TB03u - MDR", "EXACT name of the encounter type for MDR-TB Intake (TB03u)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_TB03U_XDR, "TB03u - XDR", "EXACT name of the encounter type for XDR-TB Intake (TB03u)", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_LAB_RESULT, "Lab Result", "EXACT name of the encounter type for Lab Results", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_RESISTANCE_DURING_TREATMENT, "Resistance During Treatment", "EXACT name of the encounter type to record drug resistance during treatment", null);
		setGlobalProperty(MdrtbConstants.GP_ENCOUNTER_TYPE_PV_REGIMEN, "PV Regimen", "EXACT name of the encounter type for PV Regimen", null);
		
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
		setGlobalProperty(MdrtbConstants.GP_COLOR_MAP, "CONTAMINATED:lightgrey|UNSATISFACTORY SAMPLE:lightgrey|MODERATELY POSITIVE:lightcoral|STRONGLY POSITIVE:lightcoral|WEAKLY POSITIVE:lightcoral|WAITING FOR TEST RESULTS:lightgrey|SCANTY:khaki|NEGATIVE:lightgreen|POSITIVE:lightcoral|SUSCEPTIBLE TO TUBERCULOSIS DRUG:lightgreen|INDETERMINATE TO TUBERCULOSIS DRUG:khaki|RESISTANT TO TUBERCULOSIS DRUG:lightcoral|NONE:none", "Pipe-delimited list mapping concept ids or concept mappings to color names and\\/or hex codes for display in the patient chart and (potentially) elsewhere.", null);
		setGlobalProperty(MdrtbConstants.GP_MDRTB_BIRT_REPORT_LIST, "", "List all BIRT reports by name that correspond to the MDR-TB program. Pipe delimited.", null);

		setGlobalProperty(MdrtbConstants.GP_DEFAULT_DST_DRUGS, "ISONIAZID|RIFAMPICIN|ETHAMBUTOL|PYRAZINAMIDE|STREPTOMYCIN", "The list of TB drugs, separated by pipe sign. This should be replaced with a concept set ID.", null);
		setGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_CULTURE, "14", "Stores the days since culture variable for the specimen reports.", null);
		setGlobalProperty(MdrtbConstants.GP_SPECIMEN_REPORTS_DAYS_SINCE_SMEAR, "14", "Stores the days since snear variable for the specimen reports.", null);
		setGlobalProperty(MdrtbConstants.GP_FIXED_IDENTIFIER_LOCATION, "Takes as a parameter a location name. If this property exists, when creating patient identifiers, the patient identifier location is set to this value (and setting the location is not presented to the user as an option).", "", null);
		setGlobalProperty("", "", "", null);
		setGlobalProperty("", "", "", null);
		setGlobalProperty("", "", "", null);
		setGlobalProperty("", "", "", null);

/*
"mdrtb.findPatientNumResults","property_value" : "20","description" : "Number of results to display when searching for patient","uuid" : "2bb9984c-5d10-4b5f-894b-17dda35fee41"
"mdrtb.mdrtb_forms_list","property_value" : null,"description" : "LEGACY - Only used in 2.0 by migration script: List all MDR-TB forms by name, pipe delimited. These will appear in the form list on the MDR-TB home.","uuid" : "7664ecfc-9aac-4a7f-89a7-ec4ae7e52013"
"mdrtb.patient_contact_id_attribute_type","property_value" : "MDR-TB Patient Contact ID Number","description" : "LEGACY: The person attribute type corresponding to a patient contact's ID number.","uuid" : "9eeb0362-9679-4b71-ac69-c8097cafd445"
"mdrtb.patient_identifier_type_list","property_value" : null,"description" : "Pipe delimited list of all patient identifier types associated with the MDRTB program.","uuid" : "ba8be54a-d2c6-4a07-be3d-24959257c740"
"mdrtb.roles_to_redirect_from_openmrs_homepage","property_value" : null,"description" : "Comma-separated list of role names. Any user in a role listed here will not be able to see the regular OpenMRS homepage.\r\nThat will redirect them to the MDR-TB homepage instead.","uuid" : "ab91c35d-026d-4a8d-ad6c-df3c0f4d35e2"
"mdrtb.specimenReports.defaultLabId","property_value" : "47","description" : "Stores the default lab ID for the specimen reports. You should never need to modify this parameter here, as it is updated via the specimen reports UI.","uuid" : "76712eb4-5dbe-46a2-96ba-48ba7ae9d803"
"mdrtb.started","property_value" : "true","description" : "DO NOT MODIFY. true\/false whether or not the mdrtb module has been started.  This is used to make sure modules that were running  prior to a restart are started again","uuid" : "22d28f51-ba8a-411e-82ea-b953bad6e00c"
"mdrtb.treatment_supporter_person_attribute_type","property_value" : "Treatment Supporter","description" : "LEGACY: The person attribute type that corresponds to being a treatment supporter\/acompanateur\/dots worker.","uuid" : "a9fe222c-6729-4d09-841d-c8c8296729fd"
"mdrtb.treatment_supporter_relationship_type","property_value" : "Treatment Supporter\/Treatment Supportee","description" : "LEGACY: The relationship type used to describe the relationship from a treatment supporter (A) to a patient (B)","uuid" : "07a01833-960f-4703-90b6-27c0555b8c15"
*/
		//TODO: ONLY for Tajikistan implementation. Once in production, delete these lines
		deleteGlobalProperty("mdrtb.");
		deleteGlobalProperty("dotsreports.");
		deleteGlobalProperty("mdrtbdrugforecast.");
	}
	
	private static void setGlobalProperty(String prop, String val) {
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
	private static void setGlobalProperty(String prop, String val, String description, String uuid) {
		AdministrationService service = Context.getAdministrationService();
		GlobalProperty gp = service.getGlobalPropertyObject(prop);
		if (gp == null) {
			gp = new GlobalProperty(prop, val, description);
			gp.setUuid(uuid == null ? UUID.randomUUID().toString() : uuid);
			service.saveGlobalProperty(gp);
		} else if (StringUtils.isEmpty(gp.getPropertyValue())) {
			gp.setPropertyValue(val);
			gp.setDescription(description);
			service.saveGlobalProperty(gp);
		}
	}

	private static void deleteGlobalProperty(String propertyStartsWith) {
		for (GlobalProperty gp : Context.getAdministrationService().getAllGlobalProperties()) {
			if (gp.getProperty().startsWith(propertyStartsWith)) {
				Context.getAdministrationService().purgeGlobalProperty(gp);
			}
		}
	}

	/**
	 * Perform any custom migrations required due to changes in the data model
	 */
	private void performCustomMigrations() {
		// commenting this out--only want it for the few cases where it was working properly
	}
}
