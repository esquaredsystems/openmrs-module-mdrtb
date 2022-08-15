package org.openmrs.module.mdrtb;

/**
 * This class defines all of the Concept Mappings that are required/used by this module
 * Note that mappings are defined as arrays in case we want to rename the mappings and temporarily support multiple mappings for a single concept
 */
public class TbConcepts {
	
	//TODO: Refactor this. Merg into MdrtbConcepts
	// Vitals 
	public final static String [] WEIGHT = {"WEIGHT"};
	public final static String [] PULSE = {"PULSE"};
	public final static String [] TEMPERATURE = {"TEMPERATURE"};
	public final static String [] RESPIRATORY_RATE = {"RESPIRATORY RATE"};
	public final static String [] SYSTOLIC_BLOOD_PRESSURE = {"SYSTOLIC BLOOD PRESSURE"};
	
	// MDR-TB Drugs
	public final static String [] TUBERCULOSIS_DRUGS = {"TUBERCULOSIS DRUGS"};
	public final static String [] FIRST_LINE_DRUGS = {"FIRST LINE DRUGS"};
	public final static String [] SECOND_LINE_DRUGS = {"SECOND LINE DRUGS"};
	public final static String [] ISONIAZID = {"ISONIAZID"};
	public final static String [] RIFAMPICIN = {"RIFAMPICIN"};
	public final static String [] CAPREOMYCIN = {"CAPREOMYCIN"};
	public final static String [] KANAMYCIN = {"KANAMYCIN"};
	public final static String [] AMIKACIN = {"AMIKACIN"};
	public final static String [] CLOFAZIMINE = {"CLOFAZIMINE"};
	public final static String [] CYCLOSERINE = {"CYCLOSERINE"};
	public final static String [] ETHIONAMIDE = {"ETHIONAMIDE"};
	public final static String [] PROTHIONAMIDE = {"PROTHIONAMIDE"};
	public final static String [] GATIFLOXACIN = {"GATIFLOXACIN"};
	public final static String [] OFLOXACIN = {"OFLOXACIN"};
	public final static String [] P_AMINOSALICYLIC_ACID = {"P-AMINOSALICYLIC ACID "};
	public final static String [] TERIZIDONE = {"TERIZIDONE"};
	public final static String [] VIOMYCIN = {"VIOMYCIN"};
	public final static String [] CLARITHROMYCIN = {"CLARITHROMYCIN"};
	public final static String [] RIFABUTINE = {"RIFABUTINE"};
	public final static String [] STREPTOMYCIN = {"STREPTOMYCIN"};
	public final static String [] PYRAZINAMIDE = {"PYRAZINAMIDE"};
	public final static String [] CIPROFLOXACIN = {"CIPROFLOXACIN"};
	public final static String [] ETHAMBUTOL = {"ETHAMBUTOL"};
	public final static String [] LEVOFLOXACIN = {"LEVOFLOXACIN"};
	public final static String [] PYRIDOXINE = {"PYRIDOXINE"};
	public final static String [] MOXIFLOXACIN = {"MOXIFLOXACIN"};
	public final static String [] AMOXICILLIN_AND_LAVULANIC_ACID = {"AMOXICILLIN AND CLAVULANIC ACID"};
	public final static String [] THIOACETAZONE = {"THIOACETAZONE"};
	public final static String [] LINEZOLID = {"LINEZOLID"};
	public final static String [] BEDAQUILINE = {"BEDAQUILINE"};
	
	public final static String [] QUINOLONES = {"QUINOLONES"};

	// Drug-Related concepts
	public final static String [] CURRENT_MULTI_DRUG_RESISTANT_TUBERCULOSIS_TREATMENT_TYPE = {"CURRENT MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT TYPE"};
	public final static String [] REASON_TUBERCULOSIS_TREATMENT_CHANGED_OR_STOPPED = {"REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED"};
	public final static String [] STANDARDIZED = {"STANDARDIZED"};
	public final static String [] EMPIRIC = {"EMPIRIC"};
	public final static String [] INDIVIDUALIZED = {"INDIVIDUALIZED"};
	
    // Smear, Culture, and DSTs
    public final static String [] BACILLI = {"BACILLI"};
    public final static String [] COLONIES = {"COLONIES"};
    public final static String [] CULTURE_CONSTRUCT = {"TUBERCULOSIS CULTURE CONSTRUCT"};
    public final static String [] CULTURE_METHOD = {"TUBERCULOSIS CULTURE METHOD"};
    public final static String [] CULTURE_RESULT = {"TUBERCULOSIS CULTURE RESULT"};
    public final static String [] DIRECT_INDIRECT = {"DIRECT/INDIRECT"};
    public final static String [] DST_CONSTRUCT = {"TUBERCULOSIS DRUG SENSITIVITY TEST CONSTRUCT"};
    public final static String [] DST_METHOD = {"TUBERCULOSIS DRUG SENSITIVITY TEST METHOD"};
    public final static String [] DST_RESULT = {"TUBERCULOSIS DRUG SENSITIVITY TEST RESULT"};
    public final static String [] CULTURE_GROWTH = {"CULTURE GROWTH"};
    
    //XPERT AND HAIN CONCEPTS
    public final static String [] XPERT_CONSTRUCT = {"TUBERCULOSIS XPERT TEST CONSTRUCT"};
    public final static String [] HAIN_CONSTRUCT = {"TUBERCULOSIS HAIN TEST CONSTRUCT"};
    public final static String [] MTB_RESULT = {"MTB RESULT"};
    public final static String [] RIFAMPICIN_RESISTANCE = {"RIFAMPICIN RESISTANCE"};
    public final static String [] DETECTED = {"DETECTED"};
    public final static String [] NOT_DETECTED = {"NOT DETECTED"};
    public final static String [] ERROR = {"ERROR"};
    public final static String [] ERROR_CODE = {"ERROR CODE"};
    public final static String [] XPERT_MTB_BURDEN = {"XPERT MTB BURDEN"};
    public final static String [] XPERT_HIGH = {"HIGH"};
    public final static String [] XPERT_MEDIUM = {"MEDIUM"};
    public final static String [] XPERT_LOW = {"LOW"};
    public final static String [] ISONIAZID_RESISTANCE = {"ISONIAZID RESISTANCE"};
    
    public final static String [] COLONIES_IN_CONTROL = {"COLONIES IN CONTROL"};
    public final static String [] CONCENTRATION = {"CONCENTRATION"};
    public final static String [] RESISTANT_TO_TB_DRUG = {"RESISTANT TO TUBERCULOSIS DRUG"};
    public final static String [] INTERMEDIATE_TO_TB_DRUG = {"INTERMEDIATE TO TUBERCULOSIS DRUG"};
    public final static String [] SUSCEPTIBLE_TO_TB_DRUG = {"SUSCEPTIBLE TO TUBERCULOSIS DRUG"};
    public final static String [] OTHER_MYCOBACTERIA_NON_CODED = {"OTHER MYCOBACTERIA NON-CODED"};
    public final static String [] SCANTY = {"SCANTY"};
    public final static String [] SPUTUM = {"SPUTUM"};
    public final static String [] SPUTUM_COLLECTION_DATE = {"SPUTUM COLLECTION DATE"};
    public final static String [] SAMPLE_SOURCE = {"TUBERCULOSIS SAMPLE SOURCE"};
    public final static String [] SMEAR_CONSTRUCT = {"TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT"};
    public final static String [] SMEAR_METHOD = {"TUBERCULOSIS SMEAR MICROSCOPY METHOD"};
    public final static String [] SMEAR_RESULT = {"TUBERCULOSIS SMEAR RESULT"};
    public final static String [] TEST_DATE_ORDERED = {"TUBERCULOSIS TEST DATE ORDERED"};
    public final static String [] TEST_DATE_RECEIVED = {"TUBERCULOSIS TEST DATE RECEIVED"};
    public final static String [] TEST_RESULT_DATE = {"TUBERCULOSIS TEST RESULT DATE"};
    public final static String [] TEST_START_DATE = {"TUBERCULOSIS TEST START DATE"};
    public final static String [] TYPE_OF_ORGANISM = {"TYPE OF ORGANISM"};
    public final static String [] TYPE_OF_ORGANISM_NON_CODED = {"TYPE OF ORGANISM NON-CODED"};
    public final static String [] SPECIMEN_ID = {"TUBERCULOSIS SPECIMEN ID"};
    public final static String [] SPECIMEN_APPEARANCE = {"APPEARANCE OF SPECIMEN"};
    public final static String [] SPECIMEN_COMMENTS =  {"TUBERCULOSIS SPECIMEN COMMENTS"};
    public final static String [] WAITING_FOR_TEST_RESULTS = {"WAITING FOR TEST RESULTS"};
    public final static String [] DST_CONTAMINATED = {"DST CONTAMINATED"};
    public final static String [] SCANNED_LAB_REPORT = {"SCANNED LAB REPORT"};
    public final static String [] DAYS_TO_POSITIVITY = {"DAYS TO POSITIVITY"};
    public final static String [] MONTH_OF_TREATMENT = {"MONTH OF TREATMENT"};
    
    // Lab Results
    public final static String [] STRONGLY_POSITIVE = {"STRONGLY POSITIVE"};
    public final static String [] MODERATELY_POSITIVE = {"MODERATELY POSITIVE"};
    public final static String [] WEAKLY_POSITIVE = {"WEAKLY POSITIVE"};
    public final static String [] POSITIVE = {"POSITIVE"};
    public final static String [] NEGATIVE = {"NEGATIVE"};
    public final static String [] CONTAMINATED = {"CONTAMINATED"};
    public final static String [] UNSATISFACTORY_SAMPLE = {"UNSATISFACTORY SAMPLE"};
    public final static String [] LOWAFB = {"LOWAFB"};
    
    // MDR-TB Classification
    public final static String [] CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE = {"CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE"};
    public final static String [] PATIENT_GROUP = {"TUBERCULOSIS PATIENT TYPE"};
    public final static String [] NEW = {"NEW"};
    public final static String [] PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY = {"PREVIOUSLY TREATED WITH FIRST LINE DRUGS ONLY"};
    public final static String [] PREVIOUSLY_TREATED_SECOND_LINE_DRUGS = {"PREVIOUSLY TREATED WITH SECOND LINE DRUGS"};
    
    public final static String [] CAT_4_CLASSIFICATION_PREVIOUS_TX = {"CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT"};
    public final static String [] AFTER_FAILURE_REGIMEN_1 = {"AFTER FAILURE REGIMEN 1"};
    public final static String [] AFTER_FAILURE_REGIMEN_2 = {"AFTER FAILURE REGIMEN 2"};
    //public final static String [] TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT = {"TREATMENT AFTER FAILURE OF RE-TREATMENT MDR-TB PATIENT"};
    public final static String [] OTHER = {"OTHER NON-CODED"};
    public final static String [] RELAPSE_AFTER_REGIMEN_1 = {"RELAPSE AFTER REGIMEN 1"};
    public final static String [] RELAPSE_AFTER_REGIMEN_2 = {"RELAPSE AFTER REGIMEN 2"};
    public final static String [] DEFAULT_AFTER_REGIMEN_1 = {"DEFAULT AFTER REGIMEN 1"};
    public final static String [] DEFAULT_AFTER_REGIMEN_2 = {"DEFAULT AFTER REGIMEN 2"};
    public final static String [] TRANSFER = {"TRANSFER"};
    //public final static String [] TREATMENT_AFTER_FAILURE = {"TREATMENT AFTER FAILURE"};
    
    public final static String [] MDR_TB = {"MULTI-DRUG RESISTANT TUBERCULOSIS"};
    public final static String [] XDR_TB = {"EXTENSIVE DRUG RESISTANT TUBERCULOSIS"};
    public final static String [] RR_TB = {"RR-TB"};
    public final static String [] PDR_TB = {"PDR-TB"};
    public final static String [] SUSPECTED_MDR_TB = {"SUSPECTED MULTI-DRUG TUBERCULOSIS"};
    public final static String [] TB = {"TUBERCULOSIS"};
    
    // Treatment Outcome
    public final static String [] TB_TX_OUTCOME = {"TUBERCULOSIS TREATMENT OUTCOME"};
    public final static String [] MDR_TB_TX_OUTCOME = {"MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME"};
    public final static String [] CURED = {"CURED"};
    public final static String [] DEFAULTED = {"DEFAULTED"};
    public final static String [] DIED = {"DIED"};
    public final static String [] FAILED = {"FAILED"};
    public final static String [] TREATMENT_COMPLETE = {"TREATMENT COMPLETE"};
    public final static String [] PATIENT_TRANSFERRED_OUT = {"PATIENT TRANSFERRED OUT"};
    public final static String [] STILL_ON_TREATMENT = {"STILL ON TREATMENT"};
    public final static String [] CANCELLED = {"DIAGNOSIS CANCELLED"};
    public final static String [] LOST_TO_FOLLOWUP = {"LOST TO FOLLOWUP"};
    public final static String [] STARTED_SLD_TX = {"Started SLD Treatment"};
    public final static String [] TX_OUTCOME_DATE = {"TX OUTCOME DATE"};
    
    
    // TB Type
    public final static String [] PULMONARY_TB = {"PULMONARY TUBERCULOSIS"};
    public final static String [] EXTRA_PULMONARY_TB = {"EXTRA-PULMONARY TUBERCULOSIS"};
    public final static String [] ANATOMICAL_SITE_OF_TB = {"ANATOMICAL SITE OF TUBERCULOSIS"};

    // Antiretrovirals (for HIV status section and HIV regimens)
    public final static String [] ANTIRETROVIRALS = {"ANTIRETROVIRAL DRUGS"};
    public final static String [] REASON_HIV_TX_STOPPED = {"REASON ANTIRETROVIRALS CHANGED OR STOPPED"};
    
    // HIV Co-infection
    public final static String [] COINFECTED_ARVS = {"COINFECTED AND ON ANTIRETROVIRALS"};
    public final static String [] CD4_COUNT = {"CD4 COUNT"};
    public final static String [] RESULT_OF_HIV_TEST = {"RESULT OF HIV TEST"};
    public final static String [] DATE_OF_HIV_TEST = {"DATE OF HIV TEST"};
    public final static String [] DATE_OF_ART_TREATMENT_START  = {"DATE OF ART TREATMENT START "};
    public final static String [] DATE_OF_PCT_TREATMENT_START  = {"DATE OF PCT TREATMENT START "};
    public final static String [] INDETERMINATE	 = {"INDETERMINATE"};
    
    // Hospitalization states
    public final static String [] HOSPITALIZATION_WORKFLOW = {"HOSPITALIZATION WORKFLOW"};
    public final static String [] HOSPITALIZED = {"HOSPITALIZED"};
    public final static String [] AMBULATORY = {"AMBULATORY"};   // legacy, has been retired
    
    // Other
    public final static String [] UNKNOWN = {"UNKNOWN"};
    public final static String [] CLINICIAN_NOTES = {"CLINICIAN NOTES"};
    public final static String [] RETURN_VISIT_DATE = {"RETURN VISIT DATE"};
    public final static String [] TELEPHONE_NUMBER = {"TELEPHONE NUMBER"};
    public final static String [] NONE = {"NONE"};
    

    // Contacts (potentially legacy?)
    public final static String [] PATIENT_CONTACT_IS_KNOWN_PRIOR_OR_CURRENT_MDR_TB_CASE = {"PATIENT CONTACT IS A KNOWN PRIOR OR CURRENT MDR-TB CASE"};
    public final static String [] PATIENT_CONTACT_TB_TEST_RESULT = {"PATIENT CONTACT TUBERCULOSIS TEST RESULT"};
    public final static String [] SIMPLE_TB_TEST_RESULT = {"SIMPLE TUBERCULOSIS TEST RESULT"};
    public final static String [] SIMPLE_TB_TEST_TYPE = {"SIMPLE TUBERCULOSIS TEST TYPE"};
    public final static String [] TREATMENT_SUPPORTER_CURRENTLY_ACTIVE = {"TREATMENT SUPPORTER IS CURRENTLY ACTIVE"};
    
    
    // Adverse Effects (potentially legacy?)
    public final static String [] ADVERSE_EFFECT = {"ADVERSE EFFECT"};
    public final static String [] ADVERSE_EFFECT_ACTION_TAKEN = {"ADVERSE EFFECT ACTION TAKEN, NON-CODED"};
    public final static String [] ADVERSE_EFFECT_CONSTRUCT = {"ADVERSE EFFECT CONSTRUCT"};
    public final static String [] ADVERSE_EFFECT_DATE = {"ADVERSE EFFECT DATE"};
    public final static String [] ADVERSE_EFFECT_MEDICATION = {"ADVERSE EFFECT MEDICATION"};
    public final static String [] ADVERSE_EFFECT_MEDICATION_NON_CODED = {"ADVERSE EFFECT MEDICATION NON-CODED"};
    public final static String [] ADVERSE_EFFECT_NON_CODED = {"ADVERSE EFFECT, NON-CODED"};
    
    
    // Legacy (only used by migration controller)
    public final static String [] CULTURE_STATUS = {"MULTI-DRUG RESISTANT TUBERCULOSIS CULTURE STATUS"};
    
    //NEW FOR DOTS REPORTS
    public final static String [] BASIS_FOR_TB_DIAGNOSIS = {"BASIS FOR TB DIAGNOSIS"};
    public final static String [] TB_CLINICAL_DIAGNOSIS = {"TB CLINICAL DIAGNOSIS"};
    public final static String [] CAUSE_OF_DEATH = {"CAUSE OF DEATH"};
    public final static String [] DEATH_BY_TB = {"DEATH BY TB"};
    public final static String [] DEATH_BY_TBHIV = {"DEATH BY TB/HIV"};
    public final static String [] DEATH_BY_OTHER_DISEASES = {"DEATH BY OTHER DISEASES"};
    public final static String [] DOTS_TREATMENT_START_DATE = {"DATE OF DOTS TREATMENT START"};
    public final static String [] MDR_TREATMENT_START_DATE = {"DATE OF MDR TREATMENT START"};
    public final static String [] AGE_AT_DOTS_REGISTRATION = {"AGE AT DOTS REGISTRATION"};
    public final static String [] AGE_AT_MDR_REGISTRATION = {"AGE AT MDR REGISTRATION"};
    public final static String [] RESISTANCE_TYPE = {"RESISTANCE TYPE"};
    public final static String [] TREATMENT_CENTER_FOR_IP = {"TREATMENT CENTER FOR IP"};
    public final static String [] TREATMENT_CENTER_FOR_CP = {"TREATMENT CENTER FOR CP"};
 	public final static String [] TUBERCULOSIS_PATIENT_CATEGORY = {"TUBERCULOSIS PATIENT CATEGORY"};
 	
 	public final static String[] REGIMEN_1_NEW = {"REGIMEN 1 NEW"};
 	public final static String[] REGIMEN_1_RETREATMENT = {"REGIMEN 1 TREATMENT"};
 	
 	public final static String[] DATE_OF_DEATH_AFTER_OUTCOME = {"DATE OF DEATH AFTER TX OUTCOME"};
 	
 	
 	public final static String[] PATIENT_PROGRAM_ID = {"PATIENT PROGRAM ID"};
 	/*public final static String[] DOSE_TAKEN_IP = {"DOSE TAKEN IP"};
 	public final static String[] DOSE_MISSED_IP = {"DOSE MISSED IP"};
 	public final static String[] DOSE_TAKEN_CP = {"DOSE TAKEN CP"};
 	public final static String[] DOSE_MISSED_CP = {"DOSE MISSED CP"};*/
 	public final static String[] TB03_REGISTRATION_NUMBER = {"TB03 REGISTRATION NUMBER"};
 	public final static String[] TB03_REGISTRATION_YEAR = {"TB03 REG YEAR"};
 	
 	public final static String[] TX_LOCATION = {"TREATMENT LOCATION"};
 	public final static String[] RELAPSED = {"RELAPSED"};
 	public final static String[] RELAPSE_MONTH = {"RELAPSE MONTH"};
 	
 	public final static String[] LOCATION_TYPE = {"LOCATION TYPE"};
 	public final static String[] PROFESSION = {"PROFESSION"};
 	public final static String[] POPULATION_CATEGORY = {"POPULATION CATEGORY "};
 	public final static String[] PLACE_OF_DETECTION  = {"PLACE OF DETECTION"};
 	public final static String[] DATE_FIRST_SEEKING_HELP = {"DATE FIRST SEEKING HELP"};
 	public final static String[] CIRCUMSTANCES_OF_DETECTION = {"CIRCUMSTANCES OF DETECTION"};
 	public final static String[] METHOD_OF_DETECTION = {"BASIS FOR TB DIAGNOSIS"};
 	public final static String[] SITE_OF_EPTB = {"SITE OF EPTB"};
 	public final static String[] PTB_SITE = {"PTB SITE"};
 	public final static String[] EPTB_SITE = {"EPTB SITE"};
 	public final static String[] PRESENCE_OF_DECAY = {"PRESENCE OF DECAY"};
 	public final static String[] DATE_OF_DECAY_SURVEY = {"DATE OF DECAY SURVEY "};
 	public final static String[] DIABETES = {"DIABETES"};
 	public final static String[] CNSDL = {"CNSDL"};
 	public final static String[] HYPERTENSION_OR_HEART_DISEASE = {"HYPERTENSION OR HEART DISEASE"};
 	public final static String[] ULCER = {"ULCER OF STOMACH OR DUODENUM"};
 	public final static String[] MENTAL_DISORDER = {"MENTAL DISEASE"};
 	public final static String[] ICD20 = {"IBC 20.0"};
 	public final static String[] CANCER = {"CANCER"};
 	public final static String[] COMORBID_HEPATITIS = {"COMORBID HEPATITIS"};
 	public final static String[] KIDNEY_DISEASE = {"KIDNEY DISEASE"};
 	public final static String[] NO_DISEASE = {"NO COMORBIDITY"};
 	public final static String[] OTHER_DISEASE = {"OTHER CONCOMITANT DISEASE"};
 	public final static String[] CMAC_DATE = {"CMAC DATE"};
 	public final static String[] CMAC_NUMBER = {"CMAC Number"};
 	public final static String[] PLACE_OF_ELECTORAL_COMMISSION = {"CMAC PLACE"};
 	public final static String[] GPT = {"GPT"};
 	public final static String[] FORM89_DATE = {"FORM89 DATE"};
 	public final static String [] AGE_AT_FORM89_REGISTRATION = {"FORM89 AGE"};
 	
 	public final static String[] MTB_POSITIVE = {"MTB POSITIVE"};
 	public final static String[] MTB_NEGATIVE = {"MTB NEGATIVE"};
 	
 	public final static String[] CONTACT_INVESTIGATION = {"CONTACT"};
 	public final static String[] YES = {"YES"};
 	public final static String[] NO = {"NO"};
 	
 	public final static String[] NAME_OF_DOCTOR = {"NAME OF DOCTOR"};
 	public final static String[] NAME_OF_IP_FACILITY = {"NAME OF IP FACILITY"};
 	public final static String[] NAME_OF_CP_FACILITY = {"NAME OF CP FACILITY"};
 	
 	public final static String[] DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE = {"DOTS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE"};
 	public final static String[] PREVIOUS_PROGRAM_ID = {"PREVIOUS PROGRAM ID"};
 	
 	public final static String[] XRAY_DATE = {"XRAY DATE"};
 	
 	public final static String[] WORKER = {"WORKER"};
 	public final static String[] GOVT_SERVANT = {"GOVERNMENT SERVANT"};
 	public final static String[] STUDENT  = {"STUDENT"};
 	public final static String[] DISABLED  = {"DISABLED "};
 	public final static String[] UNEMPLOYED = {"UNEMPLOYED"};
 	public final static String[] PHC_WORKER = {"PHC WORKER"};
 	public final static String[] PRIVATE_SECTOR = {"PRIVATE SECTOR"};
 	public final static String[] MILITARY_SERVANT = {"MILITARY SERVANT"};
 	public final static String[] SCHOOLCHILD = {"SCHOOLCHILD"};
 	public final static String[] TB_SERVICES_WORKER  = {"TB SERVICES WORKER"};
 	public final static String[] HOUSEWIFE  = {"HOUSEWIFE"};	
 	public final static String[] PRESCHOOL_CHILD = {"PRESCHOOL CHILD"};
 	public final static String[] PENSIONER = {"PENSIONER"};
 	
 	
 	public final static String[] RESIDENT_OF_TERRITORY = {"RESIDENT OF TERRITORY"};
 	public final static String[] RESIDENT_OTHER_TERRITORY = {"RESIDENT OTHER TERRITORY"};
 	public final static String[] FOREIGNER = {"FOREIGNER"};
 	public final static String[] RESIDENT_SOCIAL_SECURITY_FACILITY = {"RESIDENT SOCIAL SECURITY FACILITY"};
 	public final static String[] HOMELESS = {"HOMELESS"};
 	public final static String[] CONVICTED  = {"CONVICTED "};
 	public final static String[] ON_REMAND = {"ON REMAND"};
 	
 	public final static String[] CITY = {"CITY"};
 	public final static String[] VILLAGE = {"VILLAGE"};
 	
 	public final static String[] PHC_FACILITY = {"PMSA HEALTH CENTER"};
 	public final static String[] OTHER_MEDICAL_FACILITY = {"OTHER MEDICAL FACILITY"};
 	public final static String[] PRIVATE_SECTOR_FACILITY = {"PRIVATE SECTOR FACILITY"};
 	public final static String[] TB_FACILITY  = {"TB FACILITY "};
 
 	public final static String[] SELF_REFERRAL = {"HANDLING OF COMPLAINTS"};
 	public final static String[] BASELINE_EXAM= {"BASELINE EXAM"};
 	public final static String[] POSTMORTERM_IDENTIFICATION = {"POSTMORTERM IDENTIFICATION"};
 	public final static String[] CONTACT  = {"CONTACT"};
 	public final static String[] MIGRANT  = {"MIGRANT"};
 	public final static String[] COUNTRY_OF_ORIGIN = {"COUNTRY OF ORIGIN"};
 	public final static String[] CITY_OF_ORIGIN = {"CITY OF ORIGIN"};
 	public final static String[] DATE_OF_RETURN = {"DATE OF RETURN"};
 	

 	public final static String[] FLUOROGRAPHY = {"FLUOROGRAPHY"};
 	public final static String[] TUBERCULIN_TEST = {"TUBERCULIN TEST"};
 	public final static String[] ZIEHLNELSEN = {"ZIEHLNELSEN"};
 	public final static String[] FLURORESCENT_MICROSCOPY  = {"FLURORESCENT MICROSCOPY"};
 	public final static String[] HISTOLOGY = {"HISTOLOGY"};
 	public final static String[] CULTURE_DETECTION = {"CULTURE DETECTION"};
 	public final static String[] HAIN_1_DETECTION = {"HAIN 1 DETECTION"};
 	public final static String[] HAIN_2_DETECTION = {"HAIN 2 DETECTION"};
 	public final static String[] DST_DETECTION = {"DST DETECTION"};
 	public final static String[] GENEXPERT  = {"GENEXPERT"};
 	public final static String[] HAIN_TEST = {"HAIN TEST"};
 	public final static String[] CXR_RESULT = {"CXR RESULT"};
 	public final static String[] OTHER_METHOD_OF_DETECTION = {"OTHER METHOD OF DETECTION"};
 	
 	public final static String[] FOCAL  = {"FOCAL"};
 	public final static String[] INFILTRATIVE  = {"INFILTRATIVE"};
 	public final static String[] DISSEMINATED  = {"DISSEMINATED"};
 	public final static String[] CAVERNOUS  = {"CAVERNOUS"};
 	public final static String[] FIBROUS_CAVERNOUS = {"FIBROUS CAVERNOUS"};
 	public final static String[] CIRRHOTIC  = {"CIRRHOTIC"};
 	public final static String[] TB_PRIMARY_COMPLEX = {"TB PRIMARY COMPLEX"};
 	public final static String[] MILIARY = {"MILIARY"};
 	public final static String[] TUBERCULOMA  = {"TUBERCULOMA"};
 	public final static String[] BRONCHUS  = {"BRONCHUS"};
 	
 	public final static String[] PLEVRITIS = {"PLEVRITIS"};
 	public final static String[] OF_LYMPH_NODES = {"OF LYMPH NODES"};
 	public final static String[] OSTEOARTICULAR = {"OSTEOARTICULAR"};
 	public final static String[] GENITOURINARY = {"GENITOURINARY"};
 	public final static String[] OF_PERIPHERAL_LYMPH_NODES = {"OF PERIPHERAL LYMPH NODES"};
 	public final static String[] ABDOMINAL = {"ABDOMINAL"};
 	public final static String[] TUBERCULODERMA = {"TUBERCULODERMA"};
 	public final static String[] OCULAR = {"OCULAR"};
 	public final static String[] OF_CNS = {"OF CNS"};
 	public final static String[] OF_LIVER = {"OF LIVER"};
 	
 	public final static String[] COMPLICATION = {"COMPLICATION"};
 	public final static String[] OTHER_CAUSE_OF_DEATH = {"OTHER CAUSE OF DEATH"};
 	
 	public final static String[] UNDETERMINED = {"UNDETERMINED"};
 	
 	public final static String[] DRUG_RESISTANCE_DURING_TX = {"DRUG RESISTANCE DURING TX"};
 	public final static String[] DATE_OF_DRUG_RESISTANCE_DURING_TX = {"DATE OF DRUG RESISTANCE DURING TX"};
 	
 	public final static String[] NAME_OF_TX_LOCATION = {"NAME OF TX LOCATION"};
 	
 	public final static String[] HOSPITAL = {"HOSPITAL"};

 	
}
     
