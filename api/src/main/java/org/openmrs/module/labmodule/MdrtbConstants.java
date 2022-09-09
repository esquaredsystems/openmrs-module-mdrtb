package org.openmrs.module.labmodule;


public class MdrtbConstants {
    private static final String moduleName = "dotsreports";
    
    public static final String DATE_FORMAT_DISPLAY = "dd/MMM/yyyy";
    public static final String PATIENT_CHART_REGIMEN_CELL_COLOR = "lightblue";
    
    public static final String ROLES_TO_REDIRECT_GLOBAL_PROPERTY = moduleName + ".roles_to_redirect_from_openmrs_homepage";
    
    public static enum TbClassification {FULLY_SENSITIVE,ANY_RESISTANCE,MONO_RESISTANT_TB, POLY_RESISTANT_TB, MDR_TB, XDR_TB, RR_TB, SUSPECT_TB}; // TODO: add suspected mdr-tb?
    
    public static enum TreatmentState {NOT_ON_TREATMENT, ON_TREATMENT};
    
}



