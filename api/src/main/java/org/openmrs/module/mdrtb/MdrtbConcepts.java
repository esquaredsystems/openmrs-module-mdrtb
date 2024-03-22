package org.openmrs.module.mdrtb;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.ErrorFetchingConceptException;
import org.openmrs.module.mdrtb.exception.MissingConceptException;

/**
 * This class defines all of the Concept Mappings that are required/used by this module and provides
 * a cache-enabled utility method for retrieving them Note that mappings are defined as arrays in
 * case we want to rename the mappings and temporarily support multiple mappings for a single
 * concept
 */
public class MdrtbConcepts {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	// General
	public static final String YES = "YES";
	
	public static final String NO = "NO";
	
	// Vitals
	public static final String WEIGHT = "WEIGHT";
	
	public static final String PULSE = "PULSE";
	
	public static final String TEMPERATURE = "TEMPERATURE";
	
	public static final String RESPIRATORY_RATE = "RESPIRATORY RATE";
	
	public static final String SYSTOLIC_BLOOD_PRESSURE = "SYSTOLIC BLOOD PRESSURE";
	
	// MDR-TB Drugs
	public static final String TUBERCULOSIS_DRUGS = "TUBERCULOSIS DRUGS";
	
	public static final String ISONIAZID = "ISONIAZID";
	
	public static final String RIFAMPICIN = "RIFAMPICIN";
	
	public static final String CAPREOMYCIN = "CAPREOMYCIN";
	
	public static final String KANAMYCIN = "KANAMYCIN";
	
	public static final String AMIKACIN = "AMIKACIN";
	
	public static final String CLOFAZIMINE = "CLOFAZIMINE";
	
	public static final String CYCLOSERINE = "CYCLOSERINE";
	
	public static final String ETHIONAMIDE = "ETHIONAMIDE";
	
	public static final String PROTHIONAMIDE = "PROTHIONAMIDE";
	
	public static final String GATIFLOXACIN = "GATIFLOXACIN";
	
	public static final String OFLOXACIN = "OFLOXACIN";
	
	public static final String P_AMINOSALICYLIC_ACID = "P-AMINOSALICYLIC ACID";
	
	public static final String TERIZIDONE = "TERIZIDONE";
	
	public static final String VIOMYCIN = "VIOMYCIN";
	
	public static final String CLARITHROMYCIN = "CLARITHROMYCIN";
	
	public static final String RIFABUTIN = "RIFABUTIN";
	
	public static final String STREPTOMYCIN = "STREPTOMYCIN";
	
	public static final String PYRAZINAMIDE = "PYRAZINAMIDE";
	
	public static final String CIPROFLOXACIN = "CIPROFLOXACIN";
	
	public static final String ETHAMBUTOL = "ETHAMBUTOL";
	
	public static final String LEVOFLOXACIN = "LEVOFLOXACIN";
	
	public static final String PYRIDOXINE = "PYRIDOXINE";
	
	public static final String MOXIFLOXACIN = "MOXIFLOXACIN";
	
	public static final String AMOXICILLIN_AND_LAVULANIC_ACID = "AMOXICILLIN AND CLAVULANIC ACID";
	
	public static final String THIOACETAZONE = "THIOACETAZONE";
	
	public static final String BEDAQUILINE = "BEDAQUILINE";
	
	public static final String DELAMANID = "DELAMANID";
	
	public static final String LINEZOLID = "LINEZOLID";
	
	@Deprecated
	public static final String IMIPENEM = "IMIPENEM";
	
	public static final String QUINOLONES = "QUINOLONES";
	
	// Drug-Related concepts
	public static final String CURRENT_MULTI_DRUG_RESISTANT_TUBERCULOSIS_TREATMENT_TYPE = "CURRENT MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT TYPE";
	
	public static final String REASON_TUBERCULOSIS_TREATMENT_CHANGED_OR_STOPPED = "REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED";
	
	public static final String STANDARDIZED = "STANDARDIZED";
	
	public static final String EMPIRIC = "EMPIRIC";
	
	public static final String INDIVIDUALIZED = "INDIVIDUALIZED";
	
	// Smear, Culture, and DSTs
	public static final String BACILLI = "BACILLI";
	
	public static final String COLONIES = "COLONIES";
	
	public static final String COLONIES_IN_CONTROL = "COLONIES IN CONTROL";
	
	public static final String CONCENTRATION = "CONCENTRATION";
	
	public static final String CULTURE_CONSTRUCT = "TUBERCULOSIS CULTURE CONSTRUCT";
	
	public static final String CULTURE_GROWTH = "GROWTH";
	
	public static final String CULTURE_METHOD = "TUBERCULOSIS CULTURE METHOD";
	
	public static final String CULTURE_RESULT = "TUBERCULOSIS CULTURE RESULT";
	
	public static final String DAYS_TO_POSITIVITY = "DAYS TO POSITIVITY";
	
	public static final String DIRECT_INDIRECT = "DIRECT/INDIRECT";
	
	public static final String DST_CONSTRUCT = "TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT";
	
	public static final String DST_CONTAMINATED = "DST CONTAMINATED";
	
	public static final String DST_METHOD = "TUBERCULOSIS DRUG SENSITIVITY TEST METHOD";
	
	public static final String DST_RESULT = "TUBERCULOSIS DRUG SENSITIVITY TEST RESULT";
	
	public static final String INTERMEDIATE_TO_TB_DRUG = "INDETERMINATE TO TUBERCULOSIS DRUG";
	
	public static final String MICROSCOPY_TEST_CONSTRUCT = "MICROSCOPY TEST CONSTRUCT";
	
	public static final String OTHER_MYCOBACTERIA_NON_CODED = "OTHER MYCOBACTERIA NON-CODED";
	
	public static final String RESISTANT_TO_TB_DRUG = "RESISTANT TO TUBERCULOSIS DRUG";
	
	public static final String SUSCEPTIBLE_TO_TB_DRUG = "SUSCEPTIBLE TO TUBERCULOSIS DRUG";
	
	public static final String SCANTY = "SCANTY";
	
	public static final String SPUTUM = "SPUTUM";
	
	public static final String SPUTUM_COLLECTION_DATE = "SPUTUM COLLECTION DATE";
	
	public static final String SAMPLE_SOURCE = "TUBERCULOSIS SAMPLE SOURCE";
	
	public static final String SMEAR_CONSTRUCT = "TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT";
	
	public static final String SMEAR_METHOD = "TUBERCULOSIS SMEAR MICROSCOPY METHOD";
	
	public static final String SMEAR_RESULT = "TUBERCULOSIS SMEAR RESULT";
	
	public static final String TEST_DATE_ORDERED = "TUBERCULOSIS TEST DATE ORDERED";
	
	public static final String TEST_DATE_RECEIVED = "TUBERCULOSIS TEST DATE RECEIVED";
	
	public static final String TEST_RESULT_DATE = "TUBERCULOSIS TEST RESULT DATE";
	
	public static final String TEST_START_DATE = "TUBERCULOSIS TEST START DATE";
	
	public static final String TYPE_OF_ORGANISM = "TYPE OF ORGANISM";
	
	public static final String TYPE_OF_ORGANISM_NON_CODED = "TYPE OF ORGANISM NON-CODED";
	
	public static final String SCANNED_LAB_REPORT = "SCANNED LAB REPORT";
	
	public static final String SPECIMEN_ID = "TUBERCULOSIS SPECIMEN ID";
	
	public static final String SPECIMEN_APPEARANCE = "APPEARANCE OF SPECIMEN";
	
	public static final String SPECIMEN_COMMENTS = "TUBERCULOSIS SPECIMEN COMMENTS";
	
	public static final String WAITING_FOR_TEST_RESULTS = "WAITING FOR TEST RESULTS";
	
	// GeneXpert and HAIN Test
	public static final String GENEXPERT = "GENEXPERT";
	
	public static final String XPERT_CONSTRUCT = "TUBERCULOSIS XPERT TEST CONSTRUCT";
	
	public static final String MTB_RESULT = "MTB RESULT";
	
	public static final String RIFAMPICIN_RESULT = "RIFAMPICIN RESULT";
	
	public static final String DETECTED = "DETECTED";
	
	public static final String NOT_DETECTED = "NOT DETECTED";
	
	public static final String ERROR = "ERROR";
	
	public static final String ERROR_CODE = "ERROR CODE";
	
	public static final String XPERT_MTB_BURDEN = "XPERT MTB BURDEN";
	
	public static final String XPERT_HIGH = "HIGH";
	
	public static final String XPERT_MEDIUM = "MEDIUM";
	
	public static final String XPERT_LOW = "LOW";
	
	public static final String HAIN_TEST = "HAIN TEST";
	
	public static final String HAIN_CONSTRUCT = "TUBERCULOSIS HAIN TEST CONSTRUCT";
	
	public static final String HAIN2_CONSTRUCT = "TUBERCULOSIS HAIN2 TEST CONSTRUCT";
	
	public static final String ISONIAZID_RESULT = "ISONIAZID RESULT";
	
	public static final String FLUOROQUINOLONE_RESULT = "FLUOROQUINOLONE RESULT";
	
	public static final String INJECTABLE_RESISTANCE = "INJECTABLE RESISTANCE";
	
	// Lab Results
	public static final String STRONGLY_POSITIVE = "STRONGLY POSITIVE";
	
	public static final String MODERATELY_POSITIVE = "MODERATELY POSITIVE";
	
	public static final String WEAKLY_POSITIVE = "WEAKLY POSITIVE";
	
	public static final String POSITIVE = "POSITIVE";
	
	public static final String NEGATIVE = "NEGATIVE";
	
	public static final String CONTAMINATED = "CONTAMINATED";
	
	public static final String UNSATISFACTORY_SAMPLE = "UNSATISFACTORY SAMPLE";
	
	public static final String LOWAFB = "LOW AFB";
	
	// MDR-TB Classification
	public static final String NEW = "NEW";
	
	public static final String PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY = "PREVIOUSLY TREATED WITH FIRST LINE DRUGS ONLY";
	
	public static final String PREVIOUSLY_TREATED_SECOND_LINE_DRUGS = "PREVIOUSLY TREATED WITH SECOND LINE DRUGS";
	
	public static final String PATIENT_GROUP = "TUBERCULOSIS PATIENT TYPE";
	
	public static final String CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE = "CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE";
	
	public static final String CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT = "CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT";
	
	public static final String TREATMENT_AFTER_FAILURE = "TREATMENT AFTER FAILURE";
	
	public static final String TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT = "TREATMENT AFTER FAILURE OF FIRST TREATMENT MDR-TB PATIENT";
	
	public static final String TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT = "TREATMENT AFTER FAILURE OF RE-TREATMENT MDR-TB PATIENT";
	
	public static final String OTHER = "OTHER";
	
	public static final String PATIENT_TRANSFERRED_IN = "PATIENT TRANSFERRED IN";
	
	public static final String CANCELLED = "DIAGNOSIS CANCELLED";
	
	// Custom classifications
	public static final String RELAPSE_AFTER_REGIMEN_1 = "RELAPSE AFTER REGIMEN 1";
	
	public static final String RELAPSE_AFTER_REGIMEN_2 = "RELAPSE AFTER REGIMEN 2";
	
	public static final String DEFAULT_AFTER_REGIMEN_1 = "DEFAULT AFTER REGIMEN 1";
	
	public static final String DEFAULT_AFTER_REGIMEN_2 = "DEFAULT AFTER REGIMEN 2";
	
	public static final String FAILURE_AFTER_REGIMEN_1 = "AFTER FAILURE REGIMEN 1";
	
	public static final String FAILURE_AFTER_REGIMEN_2 = "AFTER FAILURE REGIMEN 2";
	
	public static final String MDR_TB = "MDR-TB";
	
	public static final String XDR_TB = "XDR TB";
	
	public static final String SUSPECTED_MDR_TB = "SUSPECTED MULTI-DRUG TUBERCULOSIS";
	
	public static final String TB = "TUBERCULOSIS";
	
	public static final String RR_TB = "RR-TB";
	
	public static final String PDR_TB = "PDR-TB";
	
	public static final String PRE_XDR_TB = "PRE-XDR";
	
	public static final String MONO = "MONO";
	
	public static final String TDR_TB = "TDR-TB";
	
	// Treatment Outcome
	public static final String MDR_TB_TREATMENT_OUTCOME = "MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME";
	
	public static final String CURED = "CURED";
	
	public static final String DEFAULTED = "DEFAULTED";
	
	public static final String DEATH = "DEATH";
	
	public static final String TREATMENT_FAILED = "TREATMENT FAILED";
	
	public static final String TREATMENT_COMPLETE = "TREATMENT COMPLETE";
	
	public static final String PATIENT_TRANSFERRED_OUT = "PATIENT TRANSFERRED OUT";
	
	public static final String STILL_ON_TREATMENT = "STILL ON TREATMENT";
	
	public static final String TB_TREATMENT_OUTCOME = "TUBERCULOSIS TREATMENT OUTCOME";
	
	public static final String LOST_TO_FOLLOWUP = "LOST TO FOLLOW UP";
	
	public static final String STARTED_SLD_TREATMENT = "Started SLD Treatment";
	
	public static final String TREATMENT_OUTCOME_DATE = "TREATMENT OUTCOME DATE";
	
	// TB Type
	public static final String PULMONARY_TB = "PULMONARY TUBERCULOSIS";
	
	public static final String EXTRA_PULMONARY_TB = "EXTRA-PULMONARY TUBERCULOSIS";
	
	public static final String ANATOMICAL_SITE_OF_TB = "SITE OF TB DISEASE";
	
	// Antiretrovirals (for HIV status section and HIV regimens)
	public static final String ANTIRETROVIRALS = "ANTIRETROVIRAL DRUGS";
	
	public static final String REASON_HIV_TREATMENT_STOPPED = "REASON ANTIRETROVIRALS CHANGED OR STOPPED";
	
	// HIV Co-infection
	public static final String COINFECTED_ARVS = "COINFECTED AND ON ANTIRETROVIRALS";
	
	public static final String CD4_COUNT = "CD4 COUNT";
	
	public static final String RESULT_OF_HIV_TEST = "RESULT OF HIV TEST";
	
	public static final String DATE_OF_HIV_TEST = "DATE OF HIV TEST";
	
	public static final String DATE_OF_ART_TREATMENT_START = "DATE OF ART TREATMENT START";
	
	public static final String DATE_OF_PCT_TREATMENT_START = "DATE OF PCT TREATMENT START";
	
	// Hospitalization states
	public static final String HOSPITALIZATION_WORKFLOW = "HOSPITALIZATION WORKFLOW";
	
	public static final String PATIENT_HOSPITALIZED = "PATIENT HOSPITALIZED";
	
	public static final String AMBULATORY = "AMBULATORY"; // legacy, has been retired
	
	// Other
	public static final String UNKNOWN = "UNKNOWN";
	
	public static final String CLINICIAN_NOTES = "CLINICIAN NOTES";
	
	public static final String RETURN_VISIT_DATE = "RETURN VISIT DATE";
	
	public static final String TELEPHONE_NUMBER = "TELEPHONE NUMBER";
	
	public static final String NONE = "NONE";
	
	// Legacy (only used by migration controller)
	public static final String CULTURE_STATUS = "MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS";
	
	// Custom concepts for Tajikistan
	public static final String PREGNANT = "PREGNANT";
	
	public static final String FIRST_LINE_DRUGS = "FIRST LINE DRUGS";
	
	public static final String SECOND_LINE_DRUGS = "SECOND LINE DRUGS";
	
	public static final String TEST_REFERRAL = "TEST REFERRAL";
	
	public static final String MONTH_OF_TREATMENT = "MONTH OF TREATMENT";
	
	public static final String CAUSE_OF_DEATH = "CAUSE OF DEATH";
	
	public static final String DEATH_BY_TB = "DEATH BY TB";
	
	public static final String DEATH_BY_TBHIV = "DEATH BY TB/HIV";
	
	public static final String DEATH_BY_OTHER_DISEASES = "DEATH BY OTHER DISEASES";
	
	public static final String AGE_AT_MDR_REGISTRATION = "AGE AT MDR REGISTRATION";
	
	public static final String MDR_TREATMENT_START_DATE = "DATE OF MDR TREATMENT START";
	
	public static final String RESISTANCE_TYPE = "RESISTANCE TYPE";
	
	public static final String TUBERCULOSIS_PATIENT_CATEGORY = "TUBERCULOSIS PATIENT CATEGORY";
	
	public static final String REGIMEN_2_STANDARD = "REGIMEN 2 STANDARD";
	
	public static final String REGIMEN_2_SHORT = "REGIMEN 2 SHORT";
	
	public static final String REGIMEN_2_INDIVIDUALIZED = "REGIMEN 2 INDIVIDUALIZED";
	
	public static final String MDR_STATUS = "MDR-TB STATUS";
	
	public static final String TREATMENT_LOCATION = "TREATMENT LOCATION";
	
	public static final String DATE_OF_MDR_CONFIRMATION = "DATE OF MDR CONFIRMATION";
	
	public static final String RELAPSED = "PATIENT RELAPSED";
	
	public static final String RELAPSE_MONTH = "RELAPSE MONTH";
	
	public static final String REGIMEN_2_REG_NUMBER = "REGIMEN 2 REG NUMBER";
	
	public static final String PATIENT_PROGRAM_ID = "PATIENT PROGRAM ID";
	
	public static final String MDR_TB_PROGRAM = "MDR-TB PROGRAM";
	
	public static final String DOTS_PROGRAM = "DOTS PROGRAM";
	
	public static final String FUNDING_SOURCE = "FUNDING SOURCE";
	
	public static final String PROJECT_HOPE = "PROJECT HOPE";
	
	public static final String MSF = "MSF";
	
	public static final String CM_DOSE = "CAPREOMYCIN DOSE";
	
	public static final String AM_DOSE = "AMIKACIN DOSE";
	
	public static final String MFX_DOSE = "MOXIFLOXACIN DOSE";
	
	public static final String LFX_DOSE = "LEVOFLOXACIN DOSE";
	
	public static final String PTO_DOSE = "PROTHIONAMIDE DOSE";
	
	public static final String CS_DOSE = "CYCLOSERINE DOSE";
	
	public static final String PAS_DOSE = "P-AMINOSALICYLIC DOSE";
	
	public static final String Z_DOSE = "PYRAZINAMIDE DOSE";
	
	public static final String E_DOSE = "ETHAMBUTOL DOSE";
	
	public static final String H_DOSE = "ISONIAZID DOSE";
	
	public static final String LZD_DOSE = "LINEZOLID DOSE";
	
	public static final String CFZ_DOSE = "CLOFAZAMINE DOSE";
	
	public static final String BDQ_DOSE = "BEDAQUILINE DOSE";
	
	public static final String DLM_DOSE = "DELAMANID DOSE";
	
	public static final String IMP_DOSE = "IMIPENEM DOSE";
	
	public static final String AMX_DOSE = "AMOXICLAV DOSE";
	
	public static final String HR_DOSE = "HR DOSE";
	
	public static final String HRZE_DOSE = "HRZE DOSE";
	
	public static final String S_DOSE = "STREPTOMYCIN DOSE";
	
	public static final String OTHER_DRUG_1_DOSE = "OTHER DRUG 1 DOSE";
	
	public static final String OTHER_DRUG_1_NAME = "OTHER DRUG 1 NAME";
	
	public static final String OTHER_DRUG_2_DOSE = "OTHER DRUG 2 DOSE";
	
	public static final String SHORT_MDR_REGIMEN = "SHORT MDR REGIMEN";
	
	public static final String STANDARD_MDR_REGIMEN = "STANDARD MDR REGIMEN";
	
	public static final String INDIVIDUAL_WITH_BEDAQUILINE = "INDIVIDUAL WITH BEDAQUILINE";
	
	public static final String INDIVIDUAL_WITH_DELAMANID = "INDIVIDUAL WITH DELAMANID";
	
	public static final String INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID = "INDIVIDUAL WITH BEDAQUILINE AND DELAMANID";
	
	public static final String INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID = "INDIVIDUAL WITH CLOFAZIMIN AND LINEZOLID";
	
	public static final String OTHER_MDRTB_REGIMEN = "OTHER MDR-TB REGIMEN";
	
	public static final String SLD_TREATMENT_REGIMEN = "SLD TREATMENT REGIMEN";
	
	public static final String SLD_REGIMEN_TYPE = "SLD REGIMEN TYPE";
	
	public static final String ADVERSE_EVENT = "ADVERSE EVENT";
	
	public static final String NAUSEA = "NAUSEA";
	
	public static final String DIARRHOEA = "DIARRHOEA";
	
	public static final String ARTHALGIA = "ARTHALGIA";
	
	public static final String DIZZINESS = "DIZZINESS";
	
	//public static final String HEARING_DISTURBANCES = "HEARING DISTURBANCES";
	public static final String HEADACHE = "HEADACHE";
	
	public static final String SLEEP_DISTURBANCES = "SLEEP DISTURBANCES";
	
	public static final String ELECTROLYTE_DISTURBANCES = "ELECTROLYTE DISTURBANCES";
	
	public static final String ABDOMINAL_PAIN = "ABDOMINAL PAIN";
	
	public static final String ANOREXIA = "ANOREXIA";
	
	public static final String GASTRITIS = "GASTRITIS";
	
	public static final String PERIPHERAL_NEUROPATHY = "PERIPHERAL NEUROPATHY";
	
	public static final String DEPRESSION = "DEPRESSION";
	
	public static final String TINNITUS = "TINNITUS";
	
	public static final String ALLERGIC_REACTION = "ALLERGIC REACTION";
	
	public static final String RASH = "RASH";
	
	public static final String VISUAL_DISTURBANCES = "VISUAL DISTURBANCES";
	
	public static final String SEIZURES = "SEIZURES";
	
	public static final String HYPOTHYROIDISM = "HYPOTHYROIDISM";
	
	public static final String PSYCHOSIS = "PSYCHOSIS";
	
	public static final String SUICIDAL_IDEATION = "SUICIDAL IDEATION";
	
	public static final String HEPATITIS_AE = "HEPATITIS (HEPATOTOXICITY)";
	
	public static final String RENAL_FAILURE = "RENAL FAILURE";
	
	public static final String QT_PROLONGATION = "QT PROLONGATION";
	
	public static final String LAB_TEST_CONFIRMING_AE = "LAB TEST CONFIRMING ADVERSE EVENT";
	
	public static final String CLINICAL_SCREEN = "CLINICAL SCREEN";
	
	public static final String VISUAL_ACUITY = "VISUAL ACUITY";
	
	public static final String SIMPLE_HEARING_TEST = "SIMPLE HEARING TEST";
	
	public static final String AUDIOGRAM = "AUDIOGRAM";
	
	public static final String NEURO_AND_PSYCHIATRIC_INVESTIGATION = "NEURO AND PSYCHIATRIC INVESTIGATION";
	
	public static final String CREATININE = "CREATININE";
	
	public static final String ALT = "ALT";
	
	public static final String AST = "AST";
	
	public static final String BILIRUBIN = "BILIRUBIN";
	
	public static final String ALKALINE_PHOSPHATASE = "ALKALINE PHOSPHATASE";
	
	public static final String YGT = "YGT";
	
	public static final String ECG = "ECG";
	
	public static final String LIPASE = "LIPASE";
	
	public static final String AMYLASE = "AMYLASE";
	
	public static final String POTASSIUM = "POTASSIUM";
	
	public static final String MAGNESIUM = "MAGNESIUM";
	
	public static final String CALCIUM = "CALCIUM";
	
	public static final String ALBUMIN = "ALBUMIN";
	
	public static final String CBC = "CBC";
	
	public static final String BLOOD_GLUCOSE = "BLOOD GLUCOSE";
	
	public static final String THYROID_TEST = "THYROID TEST";
	
	public static final String CLINICAL_SCREEN_DONE = "CLINICAL SCREEN DONE";
	
	public static final String VISUAL_ACUITY_DONE = "VISUAL ACUITY DONE";
	
	public static final String SIMPLE_HEARING_TEST_DONE = "SIMPLE HEARING TEST DONE";
	
	public static final String AUDIOGRAM_DONE = "AUDIOGRAM DONE";
	
	public static final String NEURO_INVESTIGATION_DONE = "NEURO INVESTIGATION DONE";
	
	public static final String CREATNINE_DONE = "CREATININE DONE";
	
	public static final String ALT_DONE = "ALT DONE";
	
	public static final String AST_DONE = "AST DONE";
	
	public static final String BILIRUBIN_DONE = "BILIRUBIN DONE";
	
	public static final String ALKALINE_PHOSPHATASE_DONE = "ALKALINE PHOSPHATASE DONE";
	
	public static final String YGT_DONE = "YGT DONE";
	
	public static final String ECG_DONE = "ECG DONE";
	
	public static final String LIPASE_DONE = "LIPASE DONE";
	
	public static final String AMYLASE_DONE = "AMYLASE DONE";
	
	public static final String POTASSIUM_DONE = "POTASSIUM DONE";
	
	public static final String MAGNESIUM_DONE = "MAGNESIUM DONE";
	
	public static final String CALCIUM_DONE = "CALCIUM DONE";
	
	public static final String ALBUMIN_DONE = "ALBUMIN DONE";
	
	public static final String CBC_DONE = "CBC DONE";
	
	public static final String BLOOD_GLUCOSE_DONE = "BLOOD GLUCOSE DONE";
	
	public static final String THYROID_TEST_DONE = "THYROID TEST DONE";
	
	public static final String OTHER_TEST_DONE = "OTHER TEST DONE";
	
	public static final String ADVERSE_EVENT_REGIMEN = "ADVERSE EVENT REGIMEN";
	
	public static final String ADVERSE_EVENT_TYPE = "ADVERSE EVENT TYPE";
	
	public static final String SERIOUS = "SERIOUS";
	
	public static final String OF_SPECIAL_INTEREST = "OF SPECIAL INTEREST";
	
	public static final String SAE_TYPE = "SAE TYPE";
	
	//public static final String HOSPITALIZATION = "HOSPITALIZATION";
	public static final String DISABILITY = "DISABILITY";
	
	public static final String CONGENITAL_ANOMALY = "CONGENITAL ANOMALY";
	
	public static final String LIFE_THREATENING_EXPERIENCE = "LIFE THREATENING EXPERIENCE";
	
	public static final String SPECIAL_INTEREST_EVENT_TYPE = "SPECIAL INTEREST EVENT TYPE";
	
	public static final String MYELOSUPPRESSION = "MYELOSUPPRESSION";
	
	public static final String LACTIC_ACIDOSIS = "LACTIC ACIDOSIS";
	
	public static final String HYPOKALEMIA = "HYPOKALAEMIA";
	
	public static final String PANCREATITIS = "PANCREATITIS";
	
	public static final String PHOSPHOLIPIDOSIS = "PHOSPHOLIPIDOSIS";
	
	public static final String YELLOW_CARD_DATE = "YELLOW CARD DATE";
	
	public static final String SUSPECTED_DRUG = "SUSPECTED DRUG";
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_1 = "CAUSALITY ASSESSMENT RESULT 1";
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_2 = "CAUSALITY ASSESSMENT RESULT 2";
	
	public static final String CAUSALITY_ASSESSMENT_RESULT_3 = "CAUSALITY ASSESSMENT RESULT 3";
	
	public static final String CAUSALITY_DRUG_1 = "CAUSALITY DRUG 1";
	
	public static final String CAUSALITY_DRUG_2 = "CAUSALITY DRUG 2";
	
	public static final String CAUSALITY_DRUG_3 = "CAUSALITY DRUG 3";
	
	public static final String DEFINITE = "DEFINITE";
	
	public static final String PROBABLE = "PROBABLE";
	
	public static final String POSSIBLE = "POSSIBLE";
	
	public static final String SUSPECTED = "SUSPECTED";
	
	public static final String NOT_CLASSIFIED = "NOT CLASSIFIED";
	
	public static final String ADVERSE_EVENT_ACTION = "ADVERSE EVENT ACTION";
	
	public static final String ADVERSE_EVENT_ACTION_2 = "ADVERSE EVENT ACTION 2";
	
	public static final String ADVERSE_EVENT_ACTION_3 = "ADVERSE EVENT ACTION 3";
	
	public static final String ADVERSE_EVENT_ACTION_4 = "ADVERSE EVENT ACTION 4";
	
	public static final String ADVERSE_EVENT_ACTION_5 = "ADVERSE EVENT ACTION 5";
	
	public static final String DOSE_NOT_CHANGED = "DOSE NOT CHANGED";
	
	public static final String DOSE_REDUCED = "DOSE REDUCED";
	
	public static final String DRUG_INTERRUPTED = "DRUG INTERRUPTED";
	
	public static final String DRUG_WITHDRAWN = "DRUG WITHDRAWN";
	
	public static final String ANCILLARY_DRUG_GIVEN = "ANCILLARY DRUG GIVEN";
	
	public static final String ADDITIONAL_EXAMINATION = "ADDITIONAL EXAMINATION";
	
	public static final String REQUIRES_ANCILLARY_DRUGS = "REQUIRES ANCILLARY DRUGS";
	
	public static final String REQUIRES_DOSE_CHANGE = "REQUIRES DOSE CHANGE";
	
	public static final String ADVERSE_EVENT_OUTCOME = "ADVERSE EVENT OUTCOME";
	
	public static final String RESOLVED = "RESOLVED";
	
	public static final String RESOLVED_WITH_SEQUELAE = "RESOLVED WITH SEQUELAE";
	
	public static final String FATAL = "FATAL";
	
	public static final String RESOLVING = "RESOLVING";
	
	public static final String NOT_RESOLVED = "NOT RESOLVED";
	
	public static final String ADVERSE_EVENT_OUTCOME_DATE = "ADVERSE EVENT OUTCOME DATE";
	
	public static final String DRUG_RECHALLENGE = "DRUG RECHALLENGE";
	
	public static final String NO_RECHALLENGE = "NO RECHALLENGE";
	
	public static final String RECURRENCE_OF_EVENT = "RECURRENCE OF EVENT";
	
	public static final String NO_RECURRENCE = "NO RECURRENCE";
	
	public static final String UNKNOWN_RESULT = "UNKNOWN RESULT";
	
	@Deprecated
	public static final String MEDDRA_CODE = "MEDDRA CODE";
	
	public static final String SKIN_DISORDER = "SKIN DISORDER";
	
	public static final String MUSCULOSKELETAL_DISORDER = "MUSCULOSKELETAL DISORDER";
	
	public static final String NEUROLOGICAL_DISORDER = "NEUROLOGICAL DISORDER";
	
	public static final String VISION_DISORDER = "VISION DISORDER";
	
	public static final String HEARING_DISORDER = "HEARING DISORDER";
	
	public static final String PSYCHIATRIC_DISORDER = "PSYCHIATRIC DISORDER";
	
	public static final String GASTROINTESTINAL_DISORDER = "GASTROINTESTINAL DISORDER";
	
	public static final String LIVER_DISORDER = "LIVER DISORDER";
	
	public static final String METABOLIC_DISORDER = "METABOLIC DISORDER";
	
	public static final String ENDOCRINE_DISORDER = "ENDOCRINE DISORDER";
	
	public static final String CARDIAC_DISORDER = "CARDIAC DISORDER";
	
	// For DOTS Reports
	public static final String TB_CLINICAL_DIAGNOSIS = "TB CLINICAL DIAGNOSIS";
	
	public static final String DOTS_TREATMENT_START_DATE = "DATE OF DOTS TREATMENT START";
	
	public static final String AGE_AT_DOTS_REGISTRATION = "AGE AT DOTS REGISTRATION";
	
	public static final String TREATMENT_CENTER_FOR_IP = "TREATMENT CENTER FOR IP";
	
	public static final String TREATMENT_CENTER_FOR_CP = "TREATMENT CENTER FOR CP";
	
	public static final String REGIMEN_1_NEW = "REGIMEN 1 NEW";
	
	public static final String REGIMEN_1_RETREATMENT = "REGIMEN 1 RETREATMENT";
	
	public static final String DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME = "DATE OF DEATH AFTER TREATMENT OUTCOME";
	
	public static final String TB03_REGISTRATION_NUMBER = "TB03 REGISTRATION NUMBER";
	
	public static final String TB03_REGISTRATION_YEAR = "TB03 REG YEAR";
	
	public static final String LOCATION_TYPE = "LOCATION TYPE";
	
	public static final String PROFESSION = "PROFESSION";
	
	public static final String POPULATION_CATEGORY = "POPULATION CATEGORY";
	
	public static final String PLACE_OF_DETECTION = "PLACE OF DETECTION";
	
	public static final String DATE_FIRST_SEEKING_HELP = "DATE FIRST SEEKING HELP";
	
	public static final String CIRCUMSTANCES_OF_DETECTION = "CIRCUMSTANCES OF DETECTION";
	
	public static final String METHOD_OF_DETECTION = "METHOD OF DETECTION";
	
	public static final String SITE_OF_EPTB = "SITE OF EPTB";
	
	public static final String LOCATION_OF_EPTB = "LOCATION OF EXTRA PULMONARY TB";
	
	public static final String LOCATION_OF_PTB = "LOCATION OF PULMONARY TB";
	
	//public static final String PTB_SITE = "PTB SITE";
	//public static final String EPTB_SITE = "EPTB SITE";
	public static final String PRESENCE_OF_DECAY = "PRESENCE OF DECAY";
	
	public static final String DATE_OF_DECAY_SURVEY = "DATE OF DECAY SURVEY";
	
	public static final String DIABETES = "DIABETES";
	
	public static final String CNSDL = "CNSDL";
	
	public static final String HYPERTENSION_OR_HEART_DISEASE = "HYPERTENSION OR HEART DISEASE";
	
	public static final String ULCER = "ULCER OF STOMACH OR DUODENUM";
	
	public static final String MENTAL_DISORDER = "MENTAL DISEASE";
	
	public static final String ICD20 = "ICD B20.9";
	
	public static final String CANCER = "CANCER";
	
	public static final String COMORBID_HEPATITIS = "COMORBID HEPATITIS";
	
	public static final String KIDNEY_DISEASE = "KIDNEY DISEASE";
	
	public static final String NO_DISEASE = "NO COMORBIDITY";
	
	public static final String OTHER_DISEASE = "OTHER CONCOMITANT DISEASE";
	
	public static final String CENTRAL_COMMISSION_DATE = "DATE OF TB DIAGNOSIS IN CENTRAL COMMISSION";
	
	public static final String CENTRAL_COMMISSION_NUMBER = "CMAC NUMBER";
	
	public static final String PLACE_OF_CENTRAL_COMMISSION = "CMAC PLACE";
	
	public static final String GENERAL_PRESCRIBED_TREATMENT = "GENERAL PRESCRIBED TREATMENT";
	
	public static final String FORM89_DATE = "FORM89 DATE";
	
	public static final String AGE_AT_FORM89_REGISTRATION = "FORM89 AGE";
	
	public static final String MTB_POSITIVE = "MTB POSITIVE";
	
	public static final String MTB_NEGATIVE = "MTB NEGATIVE";
	
	public static final String CONTACT_INVESTIGATION = "CONTACT INVESTIGATION";
	
	public static final String NAME_OF_DOCTOR = "NAME OF DOCTOR";
	
	public static final String NAME_OF_IP_FACILITY = "NAME OF IP FACILITY";
	
	public static final String NAME_OF_CP_FACILITY = "NAME OF CP FACILITY";
	
	public static final String DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE = "DOTS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE";
	
	public static final String XRAY_DATE = "XRAY DATE";
	
	public static final String WORKER = "WORKER";
	
	public static final String GOVT_SERVANT = "GOVERNMENT SERVANT";
	
	public static final String STUDENT = "STUDENT";
	
	public static final String DISABLED = "DISABLED";
	
	public static final String UNEMPLOYED = "UNEMPLOYED";
	
	public static final String PHC_WORKER = "PHC WORKER";
	
	public static final String PRIVATE_SECTOR = "PRIVATE SECTOR";
	
	public static final String MILITARY_SERVANT = "MILITARY SERVANT";
	
	public static final String SCHOOLCHILD = "SCHOOLCHILD";
	
	public static final String TB_SERVICES_WORKER = "TB SERVICES WORKER";
	
	public static final String HOUSEWIFE = "HOUSEWIFE";
	
	public static final String PRESCHOOL_CHILD = "PRESCHOOL CHILD";
	
	public static final String PENSIONER = "PENSIONER";
	
	public static final String RESIDENT_OF_TERRITORY = "RESIDENT OF TERRITORY";
	
	public static final String RESIDENT_OTHER_TERRITORY = "RESIDENT OTHER TERRITORY";
	
	public static final String FOREIGNER = "FOREIGNER";
	
	public static final String RESIDENT_SOCIAL_SECURITY_FACILITY = "RESIDENT SOCIAL SECURITY FACILITY";
	
	public static final String HOMELESS = "HOMELESS";
	
	public static final String CONVICTED = "CONVICTED";
	
	public static final String ON_REMAND = "ON REMAND";
	
	public static final String CITY = "CITY";
	
	public static final String VILLAGE = "VILLAGE";
	
	public static final String PHC_FACILITY = "PHC FACILITY";
	
	public static final String OTHER_MEDICAL_FACILITY = "OTHER MEDICAL FACILITY";
	
	public static final String PRIVATE_SECTOR_FACILITY = "PRIVATE SECTOR FACILITY";
	
	public static final String TB_FACILITY = "TB FACILITY";
	
	public static final String SELF_REFERRAL = "SELF-REFERRAL";
	
	public static final String BASELINE_EXAM = "BASELINE EXAM";
	
	public static final String POSTMORTERM_IDENTIFICATION = "POSTMORTERM IDENTIFICATION";
	
	//public static final String CONTACT  = "CONTACT";
	public static final String MIGRANT = "MIGRANT";
	
	public static final String COUNTRY_OF_ORIGIN = "COUNTRY OF ORIGIN";
	
	public static final String CITY_OF_ORIGIN = "CITY OF ORIGIN";
	
	public static final String DATE_OF_RETURN = "DATE OF RETURN";
	
	//public static final String FLUOROGRAPHY = "FLUOROGRAPHY";
	public static final String TUBERCULIN_TEST = "TUBERCULIN TEST";
	
	public static final String ZIEHLNELSEN = "ZIEHLNELSEN";
	
	public static final String FLURORESCENT_MICROSCOPY = "FLURORESCENT MICROSCOPY";
	
	public static final String HISTOLOGY = "HISTOLOGY";
	
	public static final String CULTURE_TEST = "CULTURE TEST";
	
	public static final String HAIN_1_DETECTION = "HAIN 1 DETECTION";
	
	public static final String HAIN_2_DETECTION = "HAIN 2 DETECTION";
	
	public static final String DST = "DST";
	
	public static final String CXR_RESULT = "CXR RESULT";
	
	public static final String OTHER_METHOD_OF_DETECTION = "OTHER METHOD OF DETECTION";
	
	public static final String FOCAL = "FOCAL";
	
	public static final String INFILTRATIVE = "INFILTRATIVE";
	
	public static final String DISSEMINATED = "DISSEMINATED";
	
	public static final String CAVERNOUS = "CAVERNOUS";
	
	public static final String FIBROUS_CAVERNOUS = "FIBROUS CAVERNOUS";
	
	public static final String CIRRHOTIC = "CIRRHOTIC";
	
	public static final String TB_PRIMARY_COMPLEX = "TB PRIMARY COMPLEX";
	
	//public static final String MILITARY = "MILITARY";
	public static final String TUBERCULOMA = "TUBERCULOMA";
	
	public static final String BRONCHUS = "BRONCHUS";
	
	public static final String PLEVRITIS = "PLEVRITIS";
	
	public static final String OF_LYMPH_NODES = "OF LYMPH NODES";
	
	public static final String OSTEOARTICULAR = "OSTEOARTICULAR";
	
	public static final String GENITOURINARY = "GENITOURINARY";
	
	//public static final String OF_PERIPHERAL_LYMPH_NODES = "OF PERIPHERAL LYMPH NODES";
	public static final String ABDOMINAL = "ABDOMINAL";
	
	public static final String TUBERCULODERMA = "TUBERCULODERMA";
	
	public static final String OCULAR = "OCULAR";
	
	public static final String OF_CNS = "OF CNS";
	
	public static final String OF_LIVER = "OF LIVER";
	
	public static final String COMPLICATION = "COMPLICATION";
	
	public static final String OTHER_CAUSE_OF_DEATH = "OTHER CAUSE OF DEATH";
	
	public static final String UNDETERMINED = "UNDETERMINED";
	
	public static final String DRUG_RESISTANCE_DURING_TREATMENT = "DRUG RESISTANCE DURING TREATMENT";
	
	public static final String DATE_OF_DRUG_RESISTANCE_DURING_TREATMENT = "DATE OF DRUG RESISTANCE DURING TREATMENT";
	
	public static final String NAME_OF_TREATMENT_LOCATION = "NAME OF TREATMENT LOCATION";
	
	public static final String HOSPITAL = "HOSPITAL";
	
	// Contacts (potentially legacy?)
	public static final String CONTACT_KNOWN_OR_CURRENT_MDR_CASE = "PATIENT CONTACT IS KNOWN MDR-TB CASE";
	
	public static final String PATIENT_CONTACT_TB_TEST_RESULT = "PATIENT CONTACT TUBERCULOSIS TEST RESULT";
	
	public static final String SIMPLE_TB_TEST_RESULT = "SIMPLE TUBERCULOSIS TEST RESULT";
	
	public static final String SIMPLE_TB_TEST_TYPE = "SIMPLE TUBERCULOSIS TEST TYPE";
	
	public static final String TREATMENT_SUPPORTER_CURRENTLY_ACTIVE = "TREATMENT SUPPORTER IS CURRENTLY ACTIVE";
	
	/**
	 * New concepts added for v2.5 upgrade
	 */
	public static final String DURATION = "DURATION";
	
	public static final String DAYS = "DAYS";
	
	public static final String WEEKS = "WEEKS";
	
	public static final String MONTHS = "MONTHS";
	
	public static final String YEARS = "YEARS";
	
	public static final String DOSE_FREQUENCY = "DOSE FREQUENCY";
	
	public static final String EVERY_30_MIN = "EVERY 30 MIN";
	
	public static final String EVERY_EIGHT_HOURS = "EVERY EIGHT HOURS";
	
	public static final String EVERY_FIVE_HOURS = "EVERY FIVE HOURS";
	
	public static final String EVERY_FORTY_EIGHT_HOURS = "EVERY FORTY-EIGHT HOURS";
	
	public static final String EVERY_FOUR_HOURS = "EVERY FOUR HOURS";
	
	public static final String EVERY_HOUR = "EVERY HOUR";
	
	public static final String EVERY_SEVENTY_TWO_HOURS = "EVERY SEVENTY-TWO HOURS";
	
	public static final String EVERY_SIX_HOURS = "EVERY SIX HOURS";
	
	public static final String EVERY_TWENTY_FOUR_HOURS = "EVERY TWENTY-FOUR HOURS";
	
	public static final String EVERY_TWO_HOURS = "EVERY TWO HOURS";
	
	public static final String ONCE_DAILY = "ONCE DAILY";
	
	public static final String ONCE_DAILY_AT_BEDTIME = "ONCE DAILY, AT BEDTIME";
	
	public static final String ONCE_DAILY_IN_THE_EVENING = "ONCE DAILY, IN THE EVENING";
	
	public static final String ONCE_DAILY_IN_THE_MORNING = "ONCE DAILY, IN THE MORNING";
	
	public static final String ONE_TIME = "ONE TIME";
	
	public static final String THRICE_DAILY = "THRICE DAILY";
	
	public static final String THRICE_DAILY_AFTER_MEALS = "THRICE DAILY, AFTER MEALS";
	
	public static final String THRICE_DAILY_BEFORE_MEALS = "THRICE DAILY, BEFORE MEALS";
	
	public static final String THRICE_DAILY_WITH_MEALS = "THRICE DAILY, WITH MEALS";
	
	public static final String TWICE_DAILY = "TWICE DAILY";
	
	public static final String TWICE_DAILY_AFTER_MEALS = "TWICE DAILY AFTER MEALS";
	
	public static final String TWICE_DAILY_BEFORE_MEALS = "TWICE DAILY BEFORE MEALS";
	
	public static final String TWICE_DAILY_WITH_MEALS = "TWICE DAILY WITH MEALS";
	
	public static final String DOSING_UNIT = "DOSING UNIT";
	
	public static final String AMPULES = "AMPULE(S)";
	
	public static final String GRAMS = "GRAM(S)";
	
	public static final String KILOGRAMS = "KILOGRAM(S)";
	
	public static final String LITRES = "LITRE(S)";
	
	public static final String MILLIGRAMS = "MILLIGRAM(S)";
	
	public static final String MILLILITRES = "MILLILITRE(S)";
	
	public static final String TEASPOONS = "TEASPOON(S)";
	
	public static final String TABLESPOONS = "TABLESPOON(S)";
	
	public static final String TABLETS = "TABLET(S)";
	
	public static final String SYRINGES = "SYRINGE(S)";
	
	public static final String VIALS = "VIAL(S)";
	
	/* All TB Drugs for Resistance */
	public static final String AMIKACIN_RESISTANCE = "AMIKACIN RESISTANCE";
	
	public static final String BEDAQUILINE_RESISTANCE = "BEDAQUILINE RESISTANCE";
	
	public static final String CAPREOMYCIN_RESISTANCE = "CAPREOMYCIN RESISTANCE";
	
	public static final String CIPROFLOXACIN_RESISTANCE = "CIPROFLOXACIN RESISTANCE";
	
	public static final String CLARITHROMYCIN_RESISTANCE = "CLARITHROMYCIN RESISTANCE";
	
	public static final String CLOFAZAMINE_RESISTANCE = "CLOFAZAMINE RESISTANCE";
	
	public static final String CYCLOSERINE_RESISTANCE = "CYCLOSERINE RESISTANCE";
	
	public static final String DELAMANID_RESISTANCE = "DELAMANID RESISTANCE";
	
	public static final String ETHAMBUTOL_RESISTANCE = "ETHAMBUTOL RESISTANCE";
	
	public static final String ETHIONAMIDE_RESISTANCE = "ETHIONAMIDE RESISTANCE";
	
	public static final String GATIFLOXACIN_RESISTANCE = "GATIFLOXACIN RESISTANCE";
	
	public static final String ISONIAZID_RESISTANCE = "ISONIAZID RESISTANCE";
	
	public static final String KANAMYCIN_RESISTANCE = "KANAMYCIN RESISTANCE";
	
	public static final String LEVOFLOXACIN_RESISTANCE = "LEVOFLOXACIN RESISTANCE";
	
	public static final String LINEZOLID_RESISTANCE = "LINEZOLID RESISTANCE";
	
	public static final String MOXIFLOXACIN_RESISTANCE = "MOXIFLOXACIN RESISTANCE";
	
	public static final String OFLOXACIN_RESISTANCE = "OFLOXACIN RESISTANCE";
	
	public static final String OTHER_RESISTANCE = "OTHER RESISTANCE";
	
	public static final String P_AMINOSALICY_RESISTANCE = "P-AMINOSALICY RESISTANCE";
	
	public static final String PROTHIONAMIDE_RESISTANCE = "PROTHIONAMIDE RESISTANCE";
	
	public static final String PYRAZINAMIDE_RESISTANCE = "PYRAZINAMIDE RESISTANCE";
	
	public static final String RIFABUTIN_RESISTANCE = "RIFABUTIN RESISTANCE";
	
	public static final String RIFAMPICIN_RESISTANCE = "RIFAMPICIN RESISTANCE";
	
	public static final String STREPTOMYCIN_RESISTANCE = "STREPTOMYCIN RESISTANCE";
	
	public static final String TERIZIDONE_RESISTANCE = "TERIZIDONE RESISTANCE";
	
	public static final String THIOACETAZONE_RESISTANCE = "THIOACETAZONE RESISTANCE";
	
	public static final String VIOMYCIN_RESISTANCE = "VIOMYCIN RESISTANCE";
	
	// Private Cache for Concepts
	private Map<String, Concept> cache = new HashMap<>();
	
	/**
	 * @return all of the defined Concept Mappings
	 */
	public Set<String[]> getAllConceptMappings() {
		Set<String[]> ret = new TreeSet<>();
		for (Field f : MdrtbConcepts.class.getFields()) {
			
			// make sure this array reflection works
			if (f.getType() == Array.class) {
				int modifier = f.getModifiers();
				if (Modifier.isFinal(modifier) && Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
					try {
						Object value = f.get(null);
						if (value != null) {
							ret.add((String[]) value);
						}
					}
					catch (IllegalAccessException iae) {
						throw new RuntimeException("Unable to access field: " + f, iae);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Initialize concept so subsequent loading is quick for lazy collections
	 */
	protected void initializeEverythingAboutConcept(Concept c) {
		if (c != null) {
			c.getDatatype().getHl7Abbreviation();
			for (ConceptName cns : c.getNames()) {
				Collection<ConceptNameTag> tags = cns.getTags();
				for (ConceptNameTag cnTag : tags) {
					cnTag.getTag();
				}
			}
			Collection<ConceptAnswer> cas = c.getAnswers();
			if (cas != null) {
				for (ConceptAnswer ca : cas) {
					Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
					for (ConceptName cn : cnsTmp) {
						Collection<ConceptNameTag> tags = cn.getTags();
						for (ConceptNameTag cnTag : tags) {
							cnTag.getTag();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Utility method which retrieves a mapped MDR-TB concept by code
	 */
	public Concept lookup(String conceptMapping) {
		try {
			// see if we have have the concept in the cache
			Concept concept = cache.get(conceptMapping);
			
			// if we've found a concept, return it
			if (concept != null) {
				return concept;
			}
			// if not, test all the mappings in the array
			List<Concept> concepts = Context.getConceptService().getConceptsByName(conceptMapping);
			for (Concept c : concepts) {
				for (ConceptName cn : c.getNames()) {
					if (cn.getName().equalsIgnoreCase(conceptMapping)) {
						concept = c;
						break;
					}
				}
			}
			if (concept != null) {
				concepts = Arrays.asList(concept);
			}
			for (Concept c : concepts) {
				List<Concept> mappings = Context.getConceptService().getConceptsByMapping(String.valueOf(c.getConceptId()),
				    MdrtbConstants.MDRTB_CONCEPT_MAPPING_CODE);
				for (Concept mapConcept : mappings) {
					if (mapConcept.getName(Context.getLocale()).equals(c.getName(Context.getLocale()))) {
						concept = c;
						// concept = Context.getConceptService().getConceptByMapping(mapName, MDRTB_CONCEPT_MAPPING_CODE);
						// if we've found a match, initialize it and return it
						if (concept != null) {
							initializeEverythingAboutConcept(concept);
							cache.put(conceptMapping, concept);
							return concept;
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new ErrorFetchingConceptException("Error fetching concept for mapping " + conceptMapping, e);
		}
		
		// if we didn't find a match, fail hard
		throw new MissingConceptException("Can't find concept for mapping " + conceptMapping);
	}
	
	/**
	 * Resets the cache
	 */
	public void resetCache() {
		cache.clear();
	}
}
