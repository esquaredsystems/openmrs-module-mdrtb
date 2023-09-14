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
	public final static String YES = "YES";
	
	public final static String NO = "NO";
	
	// Vitals
	public final static String WEIGHT = "WEIGHT";
	
	public final static String PULSE = "PULSE";
	
	public final static String TEMPERATURE = "TEMPERATURE";
	
	public final static String RESPIRATORY_RATE = "RESPIRATORY RATE";
	
	public final static String SYSTOLIC_BLOOD_PRESSURE = "SYSTOLIC BLOOD PRESSURE";
	
	// MDR-TB Drugs
	public final static String TUBERCULOSIS_DRUGS = "TUBERCULOSIS DRUGS";
	
	public final static String ISONIAZID = "ISONIAZID";
	
	public final static String RIFAMPICIN = "RIFAMPICIN";
	
	public final static String CAPREOMYCIN = "CAPREOMYCIN";
	
	public final static String KANAMYCIN = "KANAMYCIN";
	
	public final static String AMIKACIN = "AMIKACIN";
	
	public final static String CLOFAZIMINE = "CLOFAZIMINE";
	
	public final static String CYCLOSERINE = "CYCLOSERINE";
	
	public final static String ETHIONAMIDE = "ETHIONAMIDE";
	
	public final static String PROTHIONAMIDE = "PROTHIONAMIDE";
	
	public final static String GATIFLOXACIN = "GATIFLOXACIN";
	
	public final static String OFLOXACIN = "OFLOXACIN";
	
	public final static String P_AMINOSALICYLIC_ACID = "P-AMINOSALICYLIC ACID";
	
	public final static String TERIZIDONE = "TERIZIDONE";
	
	public final static String VIOMYCIN = "VIOMYCIN";
	
	public final static String CLARITHROMYCIN = "CLARITHROMYCIN";
	
	public final static String RIFABUTIN = "RIFABUTIN";
	
	public final static String STREPTOMYCIN = "STREPTOMYCIN";
	
	public final static String PYRAZINAMIDE = "PYRAZINAMIDE";
	
	public final static String CIPROFLOXACIN = "CIPROFLOXACIN";
	
	public final static String ETHAMBUTOL = "ETHAMBUTOL";
	
	public final static String LEVOFLOXACIN = "LEVOFLOXACIN";
	
	public final static String PYRIDOXINE = "PYRIDOXINE";
	
	public final static String MOXIFLOXACIN = "MOXIFLOXACIN";
	
	public final static String AMOXICILLIN_AND_LAVULANIC_ACID = "AMOXICILLIN AND CLAVULANIC ACID";
	
	public final static String THIOACETAZONE = "THIOACETAZONE";
	
	public final static String BEDAQUILINE = "BEDAQUILINE";
	
	public final static String DELAMANID = "DELAMANID";
	
	public final static String LINEZOLID = "LINEZOLID";
	
	public final static String IMIPENEM = "IMIPENEM";
	
	public final static String QUINOLONES = "QUINOLONES";
	
	// Drug-Related concepts
	public final static String CURRENT_MULTI_DRUG_RESISTANT_TUBERCULOSIS_TREATMENT_TYPE = "CURRENT MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT TYPE";
	
	public final static String REASON_TUBERCULOSIS_TREATMENT_CHANGED_OR_STOPPED = "REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED";
	
	public final static String STANDARDIZED = "STANDARDIZED";
	
	public final static String EMPIRIC = "EMPIRIC";
	
	public final static String INDIVIDUALIZED = "INDIVIDUALIZED";
	
	// Smear, Culture, and DSTs
	public final static String BACILLI = "BACILLI";
	
	public final static String COLONIES = "COLONIES";
	
	public final static String COLONIES_IN_CONTROL = "COLONIES IN CONTROL";
	
	public final static String CONCENTRATION = "CONCENTRATION";
	
	public final static String CULTURE_CONSTRUCT = "TUBERCULOSIS CULTURE CONSTRUCT";
	
	public final static String CULTURE_GROWTH = "GROWTH";
	
	public final static String CULTURE_METHOD = "TUBERCULOSIS CULTURE METHOD";
	
	public final static String CULTURE_RESULT = "TUBERCULOSIS CULTURE RESULT";
	
	public final static String DAYS_TO_POSITIVITY = "DAYS TO POSITIVITY";
	
	public final static String DIRECT_INDIRECT = "DIRECT/INDIRECT";
	
	public final static String DST_CONSTRUCT = "TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT";
	
	public final static String DST_CONTAMINATED = "DST CONTAMINATED";
	
	public final static String DST_METHOD = "TUBERCULOSIS DRUG SENSITIVITY TEST METHOD";
	
	public final static String DST_RESULT = "TUBERCULOSIS DRUG SENSITIVITY TEST RESULT";
	
	public final static String INTERMEDIATE_TO_TB_DRUG = "INDETERMINATE TO TUBERCULOSIS DRUG";
	
	public final static String MICROSCOPY_TEST_CONSTRUCT = "MICROSCOPY TEST CONSTRUCT";
	
	public final static String OTHER_MYCOBACTERIA_NON_CODED = "OTHER MYCOBACTERIA NON-CODED";
	
	public final static String RESISTANT_TO_TB_DRUG = "RESISTANT TO TUBERCULOSIS DRUG";
	
	public final static String SUSCEPTIBLE_TO_TB_DRUG = "SUSCEPTIBLE TO TUBERCULOSIS DRUG";
	
	public final static String SCANTY = "SCANTY";
	
	public final static String SPUTUM = "SPUTUM";
	
	public final static String SPUTUM_COLLECTION_DATE = "SPUTUM COLLECTION DATE";
	
	public final static String SAMPLE_SOURCE = "TUBERCULOSIS SAMPLE SOURCE";
	
	public final static String SMEAR_CONSTRUCT = "TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT";
	
	public final static String SMEAR_METHOD = "TUBERCULOSIS SMEAR MICROSCOPY METHOD";
	
	public final static String SMEAR_RESULT = "TUBERCULOSIS SMEAR RESULT";
	
	public final static String TEST_DATE_ORDERED = "TUBERCULOSIS TEST DATE ORDERED";
	
	public final static String TEST_DATE_RECEIVED = "TUBERCULOSIS TEST DATE RECEIVED";
	
	public final static String TEST_RESULT_DATE = "TUBERCULOSIS TEST RESULT DATE";
	
	public final static String TEST_START_DATE = "TUBERCULOSIS TEST START DATE";
	
	public final static String TYPE_OF_ORGANISM = "TYPE OF ORGANISM";
	
	public final static String TYPE_OF_ORGANISM_NON_CODED = "TYPE OF ORGANISM NON-CODED";
	
	public final static String SCANNED_LAB_REPORT = "SCANNED LAB REPORT";
	
	public final static String SPECIMEN_ID = "TUBERCULOSIS SPECIMEN ID";
	
	public final static String SPECIMEN_APPEARANCE = "APPEARANCE OF SPECIMEN";
	
	public final static String SPECIMEN_COMMENTS = "TUBERCULOSIS SPECIMEN COMMENTS";
	
	public final static String WAITING_FOR_TEST_RESULTS = "WAITING FOR TEST RESULTS";
	
	// GeneXpert and HAIN Test
	public final static String GENEXPERT = "GENEXPERT";
	
	public final static String XPERT_CONSTRUCT = "TUBERCULOSIS XPERT TEST CONSTRUCT";
	
	public final static String MTB_RESULT = "MTB RESULT";
	
	public final static String RIFAMPICIN_RESULT = "RIFAMPICIN RESULT";
	
	public final static String DETECTED = "DETECTED";
	
	public final static String NOT_DETECTED = "NOT DETECTED";
	
	public final static String ERROR = "ERROR";
	
	public final static String ERROR_CODE = "ERROR CODE";
	
	public final static String XPERT_MTB_BURDEN = "XPERT MTB BURDEN";
	
	public final static String XPERT_HIGH = "HIGH";
	
	public final static String XPERT_MEDIUM = "MEDIUM";
	
	public final static String XPERT_LOW = "LOW";
	
	public final static String HAIN_TEST = "HAIN TEST";
	
	public final static String HAIN_CONSTRUCT = "TUBERCULOSIS HAIN TEST CONSTRUCT";
	
	public final static String HAIN2_CONSTRUCT = "TUBERCULOSIS HAIN2 TEST CONSTRUCT";
	
	public final static String ISONIAZID_RESULT = "ISONIAZID RESULT";
	
	public final static String FLUOROQUINOLONE_RESULT = "FLUOROQUINOLONE RESULT";
	
	public final static String INJECTABLE_RESISTANCE = "INJECTABLE RESISTANCE";
	
	// Lab Results
	public final static String STRONGLY_POSITIVE = "STRONGLY POSITIVE";
	
	public final static String MODERATELY_POSITIVE = "MODERATELY POSITIVE";
	
	public final static String WEAKLY_POSITIVE = "WEAKLY POSITIVE";
	
	public final static String POSITIVE = "POSITIVE";
	
	public final static String NEGATIVE = "NEGATIVE";
	
	public final static String CONTAMINATED = "CONTAMINATED";
	
	public final static String UNSATISFACTORY_SAMPLE = "UNSATISFACTORY SAMPLE";
	
	public final static String LOWAFB = "LOW AFB";
	
	// MDR-TB Classification
	public final static String NEW = "NEW";
	
	public final static String PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY = "PREVIOUSLY TREATED WITH FIRST LINE DRUGS ONLY";
	
	public final static String PREVIOUSLY_TREATED_SECOND_LINE_DRUGS = "PREVIOUSLY TREATED WITH SECOND LINE DRUGS";
	
	public final static String PATIENT_GROUP = "TUBERCULOSIS PATIENT TYPE";
	
	public final static String CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE = "CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE";
	
	public final static String CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT = "CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT";
	
	public final static String TREATMENT_AFTER_FAILURE = "TREATMENT AFTER FAILURE";
	
	public final static String TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT = "TREATMENT AFTER FAILURE OF FIRST TREATMENT MDR-TB PATIENT";
	
	public final static String TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT = "TREATMENT AFTER FAILURE OF RE-TREATMENT MDR-TB PATIENT";
	
	public final static String OTHER = "OTHER";
	
	public final static String PATIENT_TRANSFERRED_IN = "PATIENT TRANSFERRED IN";
	
	public final static String CANCELLED = "DIAGNOSIS CANCELLED";
	
	// Custom classifications
	public final static String RELAPSE_AFTER_REGIMEN_1 = "RELAPSE AFTER REGIMEN 1";
	
	public final static String RELAPSE_AFTER_REGIMEN_2 = "RELAPSE AFTER REGIMEN 2";
	
	public final static String DEFAULT_AFTER_REGIMEN_1 = "DEFAULT AFTER REGIMEN 1";
	
	public final static String DEFAULT_AFTER_REGIMEN_2 = "DEFAULT AFTER REGIMEN 2";
	
	public final static String FAILURE_AFTER_REGIMEN_1 = "AFTER FAILURE REGIMEN 1";
	
	public final static String FAILURE_AFTER_REGIMEN_2 = "AFTER FAILURE REGIMEN 2";
	
	public final static String MDR_TB = "MDR-TB";//"MULTI-DRUG RESISTANT TUBERCULOSIS";
	
	public final static String XDR_TB = "XDR TB";// "EXTENSIVE DRUG RESISTANT TUBERCULOSIS";
	
	public final static String SUSPECTED_MDR_TB = "SUSPECTED MULTI-DRUG TUBERCULOSIS";
	
	public final static String TB = "TUBERCULOSIS";
	
	public final static String RR_TB = "RR-TB";
	
	public final static String PDR_TB = "PDR-TB";
	
	public final static String PRE_XDR_TB = "PRE-XDR";
	
	public final static String MONO = "MONO";
	
	public final static String TDR_TB = "TDR-TB";
	
	// Treatment Outcome
	public final static String MDR_TB_TREATMENT_OUTCOME = "MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME";
	
	public final static String CURED = "CURED";
	
	public final static String DEFAULTED = "DEFAULTED";
	
	public final static String DIED = "DIED";
	
	public final static String TREATMENT_FAILED = "TREATMENT FAILED";
	
	public final static String TREATMENT_COMPLETE = "TREATMENT COMPLETE";
	
	public final static String PATIENT_TRANSFERRED_OUT = "PATIENT TRANSFERRED OUT";
	
	public final static String STILL_ON_TREATMENT = "STILL ON TREATMENT";
	
	public final static String TB_TREATMENT_OUTCOME = "TUBERCULOSIS TREATMENT OUTCOME";
	
	public final static String LOST_TO_FOLLOWUP = "LOST TO FOLLOW UP";
	
	public final static String STARTED_SLD_TREATMENT = "Started SLD Treatment";
	
	public final static String TREATMENT_OUTCOME_DATE = "TREATMENT OUTCOME DATE";
	
	// TB Type
	public final static String PULMONARY_TB = "PULMONARY TUBERCULOSIS";
	
	public final static String EXTRA_PULMONARY_TB = "EXTRA-PULMONARY TUBERCULOSIS";
	
	public final static String ANATOMICAL_SITE_OF_TB = "SITE OF TB DISEASE";
	
	// Antiretrovirals (for HIV status section and HIV regimens)
	public final static String ANTIRETROVIRALS = "ANTIRETROVIRAL DRUGS";
	
	public final static String REASON_HIV_TREATMENT_STOPPED = "REASON ANTIRETROVIRALS CHANGED OR STOPPED";
	
	// HIV Co-infection
	public final static String COINFECTED_ARVS = "COINFECTED AND ON ANTIRETROVIRALS";
	
	public final static String CD4_COUNT = "CD4 COUNT";
	
	public final static String RESULT_OF_HIV_TEST = "RESULT OF HIV TEST";
	
	public final static String DATE_OF_HIV_TEST = "DATE OF HIV TEST";
	
	public final static String DATE_OF_ART_TREATMENT_START = "DATE OF ART TREATMENT START";
	
	public final static String DATE_OF_PCT_TREATMENT_START = "DATE OF PCT TREATMENT START";
	
	// Hospitalization states
	public final static String HOSPITALIZATION_WORKFLOW = "HOSPITALIZATION WORKFLOW";
	
	public final static String PATIENT_HOSPITALIZED = "PATIENT HOSPITALIZED";
	
	public final static String AMBULATORY = "AMBULATORY"; // legacy, has been retired
	
	// Other
	public final static String UNKNOWN = "UNKNOWN";
	
	public final static String CLINICIAN_NOTES = "CLINICIAN NOTES";
	
	public final static String RETURN_VISIT_DATE = "RETURN VISIT DATE";
	
	public final static String TELEPHONE_NUMBER = "TELEPHONE NUMBER";
	
	public final static String NONE = "NONE";
	
	// Legacy (only used by migration controller)
	public final static String CULTURE_STATUS = "MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS";
	
	// Custom concepts for Tajikistan
	// TODO: Find the right place and groups for these
	
	public final static String PREGNANT = "PREGNANT";
	
	public final static String FIRST_LINE_DRUGS = "FIRST LINE DRUGS";
	
	public final static String SECOND_LINE_DRUGS = "SECOND LINE DRUGS";
	
	public final static String TEST_REFERRAL = "TEST REFERRAL";
	
	public final static String MONTH_OF_TREATMENT = "MONTH OF TREATMENT";
	
	public final static String CAUSE_OF_DEATH = "CAUSE OF DEATH";
	
	public final static String DEATH_BY_TB = "DEATH BY TB";
	
	public final static String DEATH_BY_TBHIV = "DEATH BY TB/HIV";
	
	public final static String DEATH_BY_OTHER_DISEASES = "DEATH BY OTHER DISEASES";
	
	public final static String AGE_AT_MDR_REGISTRATION = "AGE AT MDR REGISTRATION";
	
	public final static String MDR_TREATMENT_START_DATE = "DATE OF MDR TREATMENT START";
	
	public final static String RESISTANCE_TYPE = "RESISTANCE TYPE";
	
	public final static String TUBERCULOSIS_PATIENT_CATEGORY = "TUBERCULOSIS PATIENT CATEGORY";
	
	public final static String REGIMEN_2_STANDARD = "REGIMEN 2 STANDARD";
	
	public final static String REGIMEN_2_SHORT = "REGIMEN 2 SHORT";
	
	public final static String REGIMEN_2_INDIVIDUALIZED = "REGIMEN 2 INDIVIDUALIZED";
	
	public final static String MDR_STATUS = "MDR-TB STATUS";
	
	public final static String TREATMENT_LOCATION = "TREATMENT LOCATION";
	
	public final static String DATE_OF_MDR_CONFIRMATION = "DATE OF MDR CONFIRMATION";
	
	public final static String RELAPSED = "RELAPSED";
	
	public final static String RELAPSE_MONTH = "RELAPSE MONTH";
	
	public final static String REGIMEN_2_REG_NUMBER = "REGIMEN 2 REG NUMBER";
	
	public final static String PATIENT_PROGRAM_ID = "PATIENT PROGRAM ID";
	
	public final static String MDR_TB_PROGRAM = "MDR-TB PROGRAM";
	
	public final static String DOTS_PROGRAM = "DOTS PROGRAM";
	
	public final static String FUNDING_SOURCE = "FUNDING SOURCE";
	
	public final static String PROJECT_HOPE = "PROJECT HOPE";
	
	public final static String MSF = "MSF";
	
	public final static String CM_DOSE = "CAPREOMYCIN DOSE";
	
	public final static String AM_DOSE = "AMIKACIN DOSE";
	
	public final static String MFX_DOSE = "MOXIFLOXACIN DOSE";
	
	public final static String LFX_DOSE = "LEVOFLOXACIN DOSE";
	
	public final static String PTO_DOSE = "PROTHIONAMIDE DOSE";
	
	public final static String CS_DOSE = "CYCLOSERINE DOSE";
	
	public final static String PAS_DOSE = "P-AMINOSALICYLIC DOSE";
	
	public final static String Z_DOSE = "PYRAZINAMIDE DOSE";
	
	public final static String E_DOSE = "ETHAMBUTOL DOSE";
	
	public final static String H_DOSE = "ISONIAZID DOSE";
	
	public final static String LZD_DOSE = "LINEZOLID DOSE";
	
	public final static String CFZ_DOSE = "CLOFAZAMINE DOSE";
	
	public final static String BDQ_DOSE = "BEDAQUILINE DOSE";
	
	public final static String DLM_DOSE = "DELAMANID DOSE";
	
	public final static String IMP_DOSE = "IMIPENEM DOSE";
	
	public final static String AMX_DOSE = "AMOXICLAV DOSE";
	
	public final static String HR_DOSE = "HR DOSE";
	
	public final static String HRZE_DOSE = "HRZE DOSE";
	
	public final static String S_DOSE = "STREPTOMYCIN DOSE";
	
	public final static String OTHER_DRUG_1_DOSE = "OTHER DRUG 1 DOSE";
	
	public final static String OTHER_DRUG_1_NAME = "OTHER DRUG 1 NAME";
	
	public final static String OTHER_DRUG_2_DOSE = "OTHER DRUG 2 DOSE";
	
	public final static String SHORT_MDR_REGIMEN = "SHORT MDR REGIMEN";
	
	public final static String STANDARD_MDR_REGIMEN = "STANDARD MDR REGIMEN";
	
	public final static String INDIVIDUAL_WITH_BEDAQUILINE = "INDIVIDUAL WITH BEDAQUILINE";
	
	public final static String INDIVIDUAL_WITH_DELAMANID = "INDIVIDUAL WITH DELAMANID";
	
	public final static String INDIVIDUAL_WITH_BEDAQUILINE_AND_DELAMANID = "INDIVIDUAL WITH BEDAQUILINE AND DELAMANID";
	
	public final static String INDIVIDUAL_WITH_CLOFAZIMIN_AND_LINEZOLID = "INDIVIDUAL WITH CLOFAZIMIN AND LINEZOLID";
	
	public final static String OTHER_MDRTB_REGIMEN = "OTHER MDR-TB REGIMEN";
	
	public final static String SLD_TREATMENT_REGIMEN = "SLD TREATMENT REGIMEN";
	
	public final static String SLD_REGIMEN_TYPE = "SLD REGIMEN TYPE";
	
	public final static String ADVERSE_EVENT = "ADVERSE EVENT";
	
	public final static String NAUSEA = "NAUSEA";
	
	public final static String DIARRHOEA = "DIARRHOEA";
	
	public final static String ARTHALGIA = "ARTHALGIA";
	
	public final static String DIZZINESS = "DIZZINESS";
	
	//public final static String HEARING_DISTURBANCES = "HEARING DISTURBANCES";
	public final static String HEADACHE = "HEADACHE";
	
	public final static String SLEEP_DISTURBANCES = "SLEEP DISTURBANCES";
	
	public final static String ELECTROLYTE_DISTURBANCES = "ELECTROLYTE DISTURBANCES";
	
	public final static String ABDOMINAL_PAIN = "ABDOMINAL PAIN";
	
	public final static String ANOREXIA = "ANOREXIA";
	
	public final static String GASTRITIS = "GASTRITIS";
	
	public final static String PERIPHERAL_NEUROPATHY = "PERIPHERAL NEUROPATHY";
	
	public final static String DEPRESSION = "DEPRESSION";
	
	public final static String TINNITUS = "TINNITUS";
	
	public final static String ALLERGIC_REACTION = "ALLERGIC REACTION";
	
	public final static String RASH = "RASH";
	
	public final static String VISUAL_DISTURBANCES = "VISUAL DISTURBANCES";
	
	public final static String SEIZURES = "SEIZURES";
	
	public final static String HYPOTHYROIDISM = "HYPOTHYROIDISM";
	
	public final static String PSYCHOSIS = "PSYCHOSIS";
	
	public final static String SUICIDAL_IDEATION = "SUICIDAL IDEATION";
	
	public final static String HEPATITIS_AE = "HEPATITIS (HEPATOTOXICITY)";
	
	public final static String RENAL_FAILURE = "RENAL FAILURE";
	
	public final static String QT_PROLONGATION = "QT PROLONGATION";
	
	public final static String LAB_TEST_CONFIRMING_AE = "LAB TEST CONFIRMING ADVERSE EVENT";
	
	public final static String CLINICAL_SCREEN = "CLINICAL SCREEN";
	
	public final static String VISUAL_ACUITY = "VISUAL ACUITY";
	
	public final static String SIMPLE_HEARING_TEST = "SIMPLE HEARING TEST";
	
	public final static String AUDIOGRAM = "AUDIOGRAM";
	
	public final static String NEURO_AND_PSYCHIATRIC_INVESTIGATION = "NEURO AND PSYCHIATRIC INVESTIGATION";
	
	public final static String CREATININE = "CREATININE";
	
	public final static String ALT = "ALT";
	
	public final static String AST = "AST";
	
	public final static String BILIRUBIN = "BILIRUBIN";
	
	public final static String ALKALINE_PHOSPHATASE = "ALKALINE PHOSPHATASE";
	
	public final static String YGT = "YGT";
	
	public final static String ECG = "ECG";
	
	public final static String LIPASE = "LIPASE";
	
	public final static String AMYLASE = "AMYLASE";
	
	public final static String POTASSIUM = "POTASSIUM";
	
	public final static String MAGNESIUM = "MAGNESIUM";
	
	public final static String CALCIUM = "CALCIUM";
	
	public final static String ALBUMIN = "ALBUMIN";
	
	public final static String CBC = "CBC";
	
	public final static String BLOOD_GLUCOSE = "BLOOD GLUCOSE";
	
	public final static String THYROID_TEST = "THYROID TEST";
	
	public final static String CLINICAL_SCREEN_DONE = "CLINICAL SCREEN DONE";
	
	public final static String VISUAL_ACUITY_DONE = "VISUAL ACUITY DONE";
	
	public final static String SIMPLE_HEARING_TEST_DONE = "SIMPLE HEARING TEST DONE";
	
	public final static String AUDIOGRAM_DONE = "AUDIOGRAM DONE";
	
	public final static String NEURO_INVESTIGATION_DONE = "NEURO INVESTIGATION DONE";
	
	public final static String CREATNINE_DONE = "CREATININE DONE";
	
	public final static String ALT_DONE = "ALT DONE";
	
	public final static String AST_DONE = "AST DONE";
	
	public final static String BILIRUBIN_DONE = "BILIRUBIN DONE";
	
	public final static String ALKALINE_PHOSPHATASE_DONE = "ALKALINE PHOSPHATASE DONE";
	
	public final static String YGT_DONE = "YGT DONE";
	
	public final static String ECG_DONE = "ECG DONE";
	
	public final static String LIPASE_DONE = "LIPASE DONE";
	
	public final static String AMYLASE_DONE = "AMYLASE DONE";
	
	public final static String POTASSIUM_DONE = "POTASSIUM DONE";
	
	public final static String MAGNESIUM_DONE = "MAGNESIUM DONE";
	
	public final static String CALCIUM_DONE = "CALCIUM DONE";
	
	public final static String ALBUMIN_DONE = "ALBUMIN DONE";
	
	public final static String CBC_DONE = "CBC DONE";
	
	public final static String BLOOD_GLUCOSE_DONE = "BLOOD GLUCOSE DONE";
	
	public final static String THYROID_TEST_DONE = "THYROID TEST DONE";
	
	public final static String OTHER_TEST_DONE = "OTHER TEST DONE";
	
	public final static String ADVERSE_EVENT_REGIMEN = "ADVERSE EVENT REGIMEN";
	
	public final static String ADVERSE_EVENT_TYPE = "ADVERSE EVENT TYPE";
	
	public final static String SERIOUS = "SERIOUS";
	
	public final static String OF_SPECIAL_INTEREST = "OF SPECIAL INTEREST";
	
	public final static String SAE_TYPE = "SAE TYPE";
	
	//public final static String DEATH = "DEATH";
	//public final static String HOSPITALIZATION = "HOSPITALIZATION";
	public final static String DISABILITY = "DISABILITY";
	
	public final static String CONGENITAL_ANOMALY = "CONGENITAL ANOMALY";
	
	public final static String LIFE_THREATENING_EXPERIENCE = "LIFE THREATENING EXPERIENCE";
	
	public final static String SPECIAL_INTEREST_EVENT_TYPE = "SPECIAL INTEREST EVENT TYPE";
	
	public final static String MYELOSUPPRESSION = "MYELOSUPPRESSION";
	
	public final static String LACTIC_ACIDOSIS = "LACTIC ACIDOSIS";
	
	public final static String HYPOKALEMIA = "HYPOKALAEMIA";
	
	public final static String PANCREATITIS = "PANCREATITIS";
	
	public final static String PHOSPHOLIPIDOSIS = "PHOSPHOLIPIDOSIS";
	
	public final static String YELLOW_CARD_DATE = "YELLOW CARD DATE";
	
	public final static String SUSPECTED_DRUG = "SUSPECTED DRUG";
	
	public final static String CAUSALITY_ASSESSMENT_RESULT_1 = "CAUSALITY ASSESSMENT RESULT 1";
	
	public final static String CAUSALITY_ASSESSMENT_RESULT_2 = "CAUSALITY ASSESSMENT RESULT 2";
	
	public final static String CAUSALITY_ASSESSMENT_RESULT_3 = "CAUSALITY ASSESSMENT RESULT 3";
	
	public final static String CAUSALITY_DRUG_1 = "CAUSALITY DRUG 1";
	
	public final static String CAUSALITY_DRUG_2 = "CAUSALITY DRUG 2";
	
	public final static String CAUSALITY_DRUG_3 = "CAUSALITY DRUG 3";
	
	public final static String DEFINITE = "DEFINITE";
	
	public final static String PROBABLE = "PROBABLE";
	
	public final static String POSSIBLE = "POSSIBLE";
	
	public final static String SUSPECTED = "SUSPECTED";
	
	public final static String NOT_CLASSIFIED = "NOT CLASSIFIED";
	
	public final static String ADVERSE_EVENT_ACTION = "ADVERSE EVENT ACTION";
	
	public final static String ADVERSE_EVENT_ACTION_2 = "ADVERSE EVENT ACTION 2";
	
	public final static String ADVERSE_EVENT_ACTION_3 = "ADVERSE EVENT ACTION 3";
	
	public final static String ADVERSE_EVENT_ACTION_4 = "ADVERSE EVENT ACTION 4";
	
	public final static String ADVERSE_EVENT_ACTION_5 = "ADVERSE EVENT ACTION 5";
	
	public final static String DOSE_NOT_CHANGED = "DOSE NOT CHANGED";
	
	public final static String DOSE_REDUCED = "DOSE REDUCED";
	
	public final static String DRUG_INTERRUPTED = "DRUG INTERRUPTED";
	
	public final static String DRUG_WITHDRAWN = "DRUG WITHDRAWN";
	
	public final static String ANCILLARY_DRUG_GIVEN = "ANCILLARY DRUG GIVEN";
	
	public final static String ADDITIONAL_EXAMINATION = "ADDITIONAL EXAMINATION";
	
	public final static String REQUIRES_ANCILLARY_DRUGS = "REQUIRES ANCILLARY DRUGS";
	
	public final static String REQUIRES_DOSE_CHANGE = "REQUIRES DOSE CHANGE";
	
	public final static String ADVERSE_EVENT_OUTCOME = "ADVERSE EVENT OUTCOME";
	
	public final static String RESOLVED = "RESOLVED";
	
	public final static String RESOLVED_WITH_SEQUELAE = "RESOLVED WITH SEQUELAE";
	
	public final static String FATAL = "FATAL";
	
	public final static String RESOLVING = "RESOLVING";
	
	public final static String NOT_RESOLVED = "NOT RESOLVED";
	
	public final static String ADVERSE_EVENT_OUTCOME_DATE = "ADVERSE EVENT OUTCOME DATE";
	
	public final static String DRUG_RECHALLENGE = "DRUG RECHALLENGE";
	
	public final static String NO_RECHALLENGE = "NO RECHALLENGE";
	
	public final static String RECURRENCE_OF_EVENT = "RECURRENCE OF EVENT";
	
	public final static String NO_RECURRENCE = "NO RECURRENCE";
	
	public final static String UNKNOWN_RESULT = "UNKNOWN RESULT";
	
	public final static String MEDDRA_CODE = "MEDDRA CODE";
	
	public final static String SKIN_DISORDER = "SKIN DISORDER";
	
	public final static String MUSCULOSKELETAL_DISORDER = "MUSCULOSKELETAL DISORDER";
	
	public final static String NEUROLOGICAL_DISORDER = "NEUROLOGICAL DISORDER";
	
	public final static String VISION_DISORDER = "VISION DISORDER";
	
	public final static String HEARING_DISORDER = "HEARING DISORDER";
	
	public final static String PSYCHIATRIC_DISORDER = "PSYCHIATRIC DISORDER";
	
	public final static String GASTROINTESTINAL_DISORDER = "GASTROINTESTINAL DISORDER";
	
	public final static String LIVER_DISORDER = "LIVER DISORDER";
	
	public final static String METABOLIC_DISORDER = "METABOLIC DISORDER";
	
	public final static String ENDOCRINE_DISORDER = "ENDOCRINE DISORDER";
	
	public final static String CARDIAC_DISORDER = "CARDIAC DISORDER";
	
	// For DOTS Reports
	public final static String TB_CLINICAL_DIAGNOSIS = "TB CLINICAL DIAGNOSIS";
	
	public final static String DOTS_TREATMENT_START_DATE = "DATE OF DOTS TREATMENT START";
	
	public final static String AGE_AT_DOTS_REGISTRATION = "AGE AT DOTS REGISTRATION";
	
	public final static String TREATMENT_CENTER_FOR_IP = "TREATMENT CENTER FOR IP";
	
	public final static String TREATMENT_CENTER_FOR_CP = "TREATMENT CENTER FOR CP";
	
	public final static String REGIMEN_1_NEW = "REGIMEN 1 NEW";
	
	public final static String REGIMEN_1_RETREATMENT = "REGIMEN 1 RETREATMENT";
	
	public final static String DATE_OF_DEATH_AFTER_TREATMENT_OUTCOME = "DATE OF DEATH AFTER TREATMENT OUTCOME";
	
	public final static String TB03_REGISTRATION_NUMBER = "TB03 REGISTRATION NUMBER";
	
	public final static String TB03_REGISTRATION_YEAR = "TB03 REG YEAR";
	
	public final static String LOCATION_TYPE = "LOCATION TYPE";
	
	public final static String PROFESSION = "PROFESSION";
	
	public final static String POPULATION_CATEGORY = "POPULATION CATEGORY";
	
	public final static String PLACE_OF_DETECTION = "PLACE OF DETECTION";
	
	public final static String DATE_FIRST_SEEKING_HELP = "DATE FIRST SEEKING HELP";
	
	public final static String CIRCUMSTANCES_OF_DETECTION = "CIRCUMSTANCES OF DETECTION";
	
	public final static String METHOD_OF_DETECTION = "METHOD OF DETECTION";
	
	public final static String SITE_OF_EPTB = "SITE OF EPTB";
	
	//public final static String PTB_SITE = "PTB SITE";
	//public final static String EPTB_SITE = "EPTB SITE";
	public final static String PRESENCE_OF_DECAY = "PRESENCE OF DECAY";
	
	public final static String DATE_OF_DECAY_SURVEY = "DATE OF DECAY SURVEY";
	
	public final static String DIABETES = "DIABETES";
	
	public final static String CNSDL = "CNSDL";
	
	public final static String HYPERTENSION_OR_HEART_DISEASE = "HYPERTENSION OR HEART DISEASE";
	
	public final static String ULCER = "ULCER OF STOMACH OR DUODENUM";
	
	public final static String MENTAL_DISORDER = "MENTAL DISEASE";
	
	public final static String ICD20 = "ICD B20.9";
	
	public final static String CANCER = "CANCER";
	
	public final static String COMORBID_HEPATITIS = "COMORBID HEPATITIS";
	
	public final static String KIDNEY_DISEASE = "KIDNEY DISEASE";
	
	public final static String NO_DISEASE = "NO COMORBIDITY";
	
	public final static String OTHER_DISEASE = "OTHER CONCOMITANT DISEASE";
	
	public final static String CENTRAL_COMMISSION_DATE = "DATE OF TB DIAGNOSIS IN CENTRAL COMMISSION";
	
	public final static String CENTRAL_COMMISSION_NUMBER = "CMAC NUMBER";
	
	public final static String PLACE_OF_CENTRAL_COMMISSION = "CMAC PLACE";
	
	public final static String GENERAL_PRESCRIBED_TREATMENT = "GENERAL PRESCRIBED TREATMENT";
	
	public final static String FORM89_DATE = "FORM89 DATE";
	
	public final static String AGE_AT_FORM89_REGISTRATION = "FORM89 AGE";
	
	public final static String MTB_POSITIVE = "MTB POSITIVE";
	
	public final static String MTB_NEGATIVE = "MTB NEGATIVE";
	
	public final static String CONTACT_INVESTIGATION = "CONTACT INVESTIGATION";
	
	public final static String NAME_OF_DOCTOR = "NAME OF DOCTOR";
	
	public final static String NAME_OF_IP_FACILITY = "NAME OF IP FACILITY";
	
	public final static String NAME_OF_CP_FACILITY = "NAME OF CP FACILITY";
	
	public final static String DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE = "DOTS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE";
	
	public final static String XRAY_DATE = "XRAY DATE";
	
	public final static String WORKER = "WORKER";
	
	public final static String GOVT_SERVANT = "GOVERNMENT SERVANT";
	
	public final static String STUDENT = "STUDENT";
	
	public final static String DISABLED = "DISABLED";
	
	public final static String UNEMPLOYED = "UNEMPLOYED";
	
	public final static String PHC_WORKER = "PHC WORKER";
	
	public final static String PRIVATE_SECTOR = "PRIVATE SECTOR";
	
	public final static String MILITARY_SERVANT = "MILITARY SERVANT";
	
	public final static String SCHOOLCHILD = "SCHOOLCHILD";
	
	public final static String TB_SERVICES_WORKER = "TB SERVICES WORKER";
	
	public final static String HOUSEWIFE = "HOUSEWIFE";
	
	public final static String PRESCHOOL_CHILD = "PRESCHOOL CHILD";
	
	public final static String PENSIONER = "PENSIONER";
	
	public final static String RESIDENT_OF_TERRITORY = "RESIDENT OF TERRITORY";
	
	public final static String RESIDENT_OTHER_TERRITORY = "RESIDENT OTHER TERRITORY";
	
	public final static String FOREIGNER = "FOREIGNER";
	
	public final static String RESIDENT_SOCIAL_SECURITY_FACILITY = "RESIDENT SOCIAL SECURITY FACILITY";
	
	public final static String HOMELESS = "HOMELESS";
	
	public final static String CONVICTED = "CONVICTED";
	
	public final static String ON_REMAND = "ON REMAND";
	
	public final static String CITY = "CITY";
	
	public final static String VILLAGE = "VILLAGE";
	
	public final static String PHC_FACILITY = "PHC FACILITY";
	
	public final static String OTHER_MEDICAL_FACILITY = "OTHER MEDICAL FACILITY";
	
	public final static String PRIVATE_SECTOR_FACILITY = "PRIVATE SECTOR FACILITY";
	
	public final static String TB_FACILITY = "TB FACILITY";
	
	public final static String SELF_REFERRAL = "SELF-REFERRAL";
	
	public final static String BASELINE_EXAM = "BASELINE EXAM";
	
	public final static String POSTMORTERM_IDENTIFICATION = "POSTMORTERM IDENTIFICATION";
	
	//public final static String CONTACT  = "CONTACT";
	public final static String MIGRANT = "MIGRANT";
	
	public final static String COUNTRY_OF_ORIGIN = "COUNTRY OF ORIGIN";
	
	public final static String CITY_OF_ORIGIN = "CITY OF ORIGIN";
	
	public final static String DATE_OF_RETURN = "DATE OF RETURN";
	
	//public final static String FLUOROGRAPHY = "FLUOROGRAPHY";
	public final static String TUBERCULIN_TEST = "TUBERCULIN TEST";
	
	public final static String ZIEHLNELSEN = "ZIEHLNELSEN";
	
	public final static String FLURORESCENT_MICROSCOPY = "FLURORESCENT MICROSCOPY";
	
	public final static String HISTOLOGY = "HISTOLOGY";
	
	public final static String CULTURE_TEST = "CULTURE TEST";
	
	public final static String HAIN_1_DETECTION = "HAIN 1 DETECTION";
	
	public final static String HAIN_2_DETECTION = "HAIN 2 DETECTION";
	
	public final static String DST = "DST";
	
	public final static String CXR_RESULT = "CXR RESULT";
	
	public final static String OTHER_METHOD_OF_DETECTION = "OTHER METHOD OF DETECTION";
	
	public final static String FOCAL = "FOCAL";
	
	public final static String INFILTRATIVE = "INFILTRATIVE";
	
	public final static String DISSEMINATED = "DISSEMINATED";
	
	public final static String CAVERNOUS = "CAVERNOUS";
	
	public final static String FIBROUS_CAVERNOUS = "FIBROUS CAVERNOUS";
	
	public final static String CIRRHOTIC = "CIRRHOTIC";
	
	public final static String TB_PRIMARY_COMPLEX = "TB PRIMARY COMPLEX";
	
	//public final static String MILITARY = "MILITARY";
	public final static String TUBERCULOMA = "TUBERCULOMA";
	
	public final static String BRONCHUS = "BRONCHUS";
	
	public final static String PLEVRITIS = "PLEVRITIS";
	
	public final static String OF_LYMPH_NODES = "OF LYMPH NODES";
	
	public final static String OSTEOARTICULAR = "OSTEOARTICULAR";
	
	public final static String GENITOURINARY = "GENITOURINARY";
	
	//public final static String OF_PERIPHERAL_LYMPH_NODES = "OF PERIPHERAL LYMPH NODES";
	public final static String ABDOMINAL = "ABDOMINAL";
	
	public final static String TUBERCULODERMA = "TUBERCULODERMA";
	
	public final static String OCULAR = "OCULAR";
	
	public final static String OF_CNS = "OF CNS";
	
	public final static String OF_LIVER = "OF LIVER";
	
	public final static String COMPLICATION = "COMPLICATION";
	
	public final static String OTHER_CAUSE_OF_DEATH = "OTHER CAUSE OF DEATH";
	
	public final static String UNDETERMINED = "UNDETERMINED";
	
	public final static String DRUG_RESISTANCE_DURING_TREATMENT = "DRUG RESISTANCE DURING TREATMENT";
	
	public final static String DATE_OF_DRUG_RESISTANCE_DURING_TREATMENT = "DATE OF DRUG RESISTANCE DURING TREATMENT";
	
	public final static String NAME_OF_TREATMENT_LOCATION = "NAME OF TREATMENT LOCATION";
	
	public final static String HOSPITAL = "HOSPITAL";
	
	// Contacts (potentially legacy?)
	public final static String CONTACT_KNOWN_OR_CURRENT_MDR_CASE = "PATIENT CONTACT IS KNOWN MDR-TB CASE";
	
	public final static String PATIENT_CONTACT_TB_TEST_RESULT = "PATIENT CONTACT TUBERCULOSIS TEST RESULT";
	
	public final static String SIMPLE_TB_TEST_RESULT = "SIMPLE TUBERCULOSIS TEST RESULT";
	
	public final static String SIMPLE_TB_TEST_TYPE = "SIMPLE TUBERCULOSIS TEST TYPE";
	
	public final static String TREATMENT_SUPPORTER_CURRENTLY_ACTIVE = "TREATMENT SUPPORTER IS CURRENTLY ACTIVE";
	
	/**
	 * New concepts added for v2.5 upgrade
	 */
	public final static String DURATION = "DURATION";
	
	public final static String DAYS = "DAYS";
	
	public final static String WEEKS = "WEEKS";
	
	public final static String MONTHS = "MONTHS";
	
	public final static String YEARS = "YEARS";
	
	public final static String DOSE_FREQUENCY = "DOSE FREQUENCY";
	
	public final static String EVERY_30_MIN = "EVERY 30 MIN";
	
	public final static String EVERY_EIGHT_HOURS = "EVERY EIGHT HOURS";
	
	public final static String EVERY_FIVE_HOURS = "EVERY FIVE HOURS";
	
	public final static String EVERY_FORTY_EIGHT_HOURS = "EVERY FORTY-EIGHT HOURS";
	
	public final static String EVERY_FOUR_HOURS = "EVERY FOUR HOURS";
	
	public final static String EVERY_HOUR = "EVERY HOUR";
	
	public final static String EVERY_SEVENTY_TWO_HOURS = "EVERY SEVENTY-TWO HOURS";
	
	public final static String EVERY_SIX_HOURS = "EVERY SIX HOURS";
	
	public final static String EVERY_TWENTY_FOUR_HOURS = "EVERY TWENTY-FOUR HOURS";
	
	public final static String EVERY_TWO_HOURS = "EVERY TWO HOURS";
	
	public final static String ONCE_DAILY = "ONCE DAILY";
	
	public final static String ONCE_DAILY_AT_BEDTIME = "ONCE DAILY, AT BEDTIME";
	
	public final static String ONCE_DAILY_IN_THE_EVENING = "ONCE DAILY, IN THE EVENING";
	
	public final static String ONCE_DAILY_IN_THE_MORNING = "ONCE DAILY, IN THE MORNING";
	
	public final static String ONE_TIME = "ONE TIME";
	
	public final static String THRICE_DAILY = "THRICE DAILY";
	
	public final static String THRICE_DAILY_AFTER_MEALS = "THRICE DAILY, AFTER MEALS";
	
	public final static String THRICE_DAILY_BEFORE_MEALS = "THRICE DAILY, BEFORE MEALS";
	
	public final static String THRICE_DAILY_WITH_MEALS = "THRICE DAILY, WITH MEALS";
	
	public final static String TWICE_DAILY = "TWICE DAILY";
	
	public final static String TWICE_DAILY_AFTER_MEALS = "TWICE DAILY AFTER MEALS";
	
	public final static String TWICE_DAILY_BEFORE_MEALS = "TWICE DAILY BEFORE MEALS";
	
	public final static String TWICE_DAILY_WITH_MEALS = "TWICE DAILY WITH MEALS";
	
	public final static String DOSING_UNIT = "DOSING UNIT";
	
	public final static String AMPULES = "AMPULE(S)";
	
	public final static String GRAMS = "GRAM(S)";
	
	public final static String KILOGRAMS = "KILOGRAM(S)";
	
	public final static String LITRES = "LITRE(S)";
	
	public final static String MILLIGRAMS = "MILLIGRAM(S)";
	
	public final static String MILLILITRES = "MILLILITRE(S)";
	
	public final static String TEASPOONS = "TEASPOON(S)";
	
	public final static String TABLESPOONS = "TABLESPOON(S)";
	
	public final static String TABLETS = "TABLET(S)";
	
	public final static String SYRINGES = "SYRINGE(S)";
	
	public final static String VIALS = "VIAL(S)";
	
	// Private Cache for Concepts
	private Map<String, Concept> cache = new HashMap<String, Concept>();
	
	/**
	 * @return all of the defined Concept Mappings
	 */
	public Set<String[]> getAllConceptMappings() {
		Set<String[]> ret = new TreeSet<String[]>();
		for (Field f : MdrtbConcepts.class.getFields()) {
			
			// TODO: make sure this array reflection works--this has not yet been used or tested
			
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
