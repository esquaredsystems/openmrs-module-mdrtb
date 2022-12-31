package org.openmrs.module.mdrtb;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;

public class MdrtbConstants {
	
	private static final String moduleName = "mdrtb";
	
	public static final String UUID_REGEX = "^[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]+$";;
	
	public static final String DATE_FORMAT_DISPLAY = "dd/MMM/yyyy";
	
	public static final String RUSSIAN_DATE_FORMAT = "yyyy.MM.dd";
	
	public static final String PATIENT_CHART_REGIMEN_CELL_COLOR = "lightblue";
	
	public static final String ROLES_TO_REDIRECT_GLOBAL_PROPERTY = moduleName + ".roles_to_redirect_from_openmrs_homepage";
	
	public static enum TbClassification {
		MONO_RESISTANT_TB, POLY_RESISTANT_TB, MDR_TB, XDR_TB, RIF_RESISTANT_TB, PRE_XDR_TB
	};
	
	public static enum TreatmentState {
		NOT_ON_TREATMENT, ON_TREATMENT
	}
	
	public static final String ENROLLMENT_LOCATION_TAG = "1c783dca-fd54-4ea8-a0fc-2875374e9cb6";

	// The Concept Map name-space
	public static final String MDRTB_CONCEPT_MAPPING_CODE = "org.openmrs.module.mdrtb";


	/*******************************/
	/** START - Global Properties **/
	public static final String MDRTB_PROGRAM_NAME_GP = "mdrtb.program_name";

	public static final String DOTS_IDENTIFIER_TYPE_GP = "dotsreports.patient_identifier_type";

	public static final String NEW_CONCEPT_ID_GP = "dotsreports.new.conceptId";

	public static final String MDRTB_TEST_RESULT_ENCOUNTER_TYPE_DST_GP = "mdrtb.test_result_encounter_type_DST";

	public static final String MDRTB_TEST_RESULT_ENCOUNTER_TYPE_BACTERIOLOGY_GP = "mdrtb.test_result_encounter_type_bacteriology";

	public static final String MDRTB_IDENTIFIER_TYPE_GP = "mdrtb.primaryPatientIdentifierType";

	public static final String MDRTB_TREATMENT_SUPPORTER_PERSON_ATTRIBUTE_TYPE_GP = "mdrtb.treatment_supporter_person_attribute_type";

	public static final String AFTER_RELAPSE1_CONCEPT_ID_GP = "dotsreports.afterRelapse1.conceptId";

	public static final String AFTER_RELAPSE2_CONCEPT_ID_GP = "dotsreports.afterRelapse2.conceptId";

	public static final String AFTER_FAILURE1_CONCEPT_ID_GP = "dotsreports.afterFailure1.conceptId";

	public static final String AFTER_FAILURE2_CONCEPT_ID_GP = "dotsreports.afterFailure2.conceptId";

	public static final String AFTER_DEFAULT1_CONCEPT_ID_GP = "dotsreports.afterDefault1.conceptId";

	public static final String AFTER_DEFAULT2_CONCEPT_ID_GP = "dotsreports.afterDefault2.conceptId";

	public static final String OTHER_CONCEPT_ID_GP = "dotsreports.other.conceptId";

	public static final String TRANSFER_IN_CONCEPT_ID_GP = "dotsreports.transferIn.conceptId";

	public static final String OUTCOME_SLD2_CONCEPT_ID_GP = "dotsreports.outcome.sld2.conceptId";

	public static final String OUTCOME_TRANSFER_OUT_CONCEPT_ID_GP = "dotsreports.outcome.transferout.conceptId";

	public static final String OUTCOME_CANCELED_CONCEPT_ID_GP = "dotsreports.outcome.canceled.conceptId";

	public static final String OUTCOME_LTFU_CONCEPT_ID_GP = "dotsreports.outcome.ltfu.conceptId";

	public static final String OUTCOME_DIED_CONCEPT_ID_GP = "dotsreports.outcome.died.conceptId";

	public static final String OUTCOME_TX_FAILURE_CONCEPT_ID_GP = "dotsreports.outcome.txFailure.conceptId";

	public static final String OUTCOME_TX_COMPLETED_CONCEPT_ID_GP = "dotsreports.outcome.txCompleted.conceptId";

	public static final String OUTCOME_CURED_CONCEPT_ID_GP = "dotsreports.outcome.cured.conceptId";	

	/** END - Global Properties **/
	/*****************************/

	
	/***************************/
	/** START - Message Codes **/
	public static final String MESSAGE_CODE_NO = "mdrtb.no";

	public static final String MESSAGE_CODE_YES = "mdrtb.yes";
	/** END - Message Codes **/
	/*************************/


	/*****************************/
	/** START - Encounter Types **/
	public static EncounterType ADVERSE_EVENT_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Adverse Event");

	public static EncounterType TRANSFER_IN_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Transfer In");

	public static EncounterType TRANSFER_OUT_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Transfer Out");

	public static EncounterType SPECIMEN_COLLECTION_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Specimen Collection");

	public static EncounterType FORM89_FOLLOWUP_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Form 89");

	public static EncounterType TB03U_MDRTB_FOLLOWUP_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("TB03u - XDR");

	public static EncounterType TB03_INTAKE_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("TB03");

	public static EncounterType MDRTB_LAB_RESULT_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Lab Result");

	public static EncounterType RESISTANCE_DURING_TREATMENT_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType("Resistance During Treatment");

	@Deprecated
	public static EncounterType MDRTB_BACTERIOLOGY_RESULT_ENCOUNTER_TYPE = Context.getEncounterService().getEncounterType(
	Context.getAdministrationService().getGlobalProperty(MDRTB_TEST_RESULT_ENCOUNTER_TYPE_BACTERIOLOGY_GP));

	/** END - Encounter Types **/
	/***************************/
}
