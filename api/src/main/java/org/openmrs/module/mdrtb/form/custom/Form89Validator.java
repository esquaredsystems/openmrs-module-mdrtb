package org.openmrs.module.mdrtb.form.custom;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class Form89Validator implements Validator {
	
    public boolean supports(Class<?> clazz) {
		return (TB03Form.class.isAssignableFrom(clazz) || TB03Form.class.isAssignableFrom(clazz));
    }

    public void validate(Object target, Errors errors) {   
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "encounterDatetime", "mdrtb.form.errors.noEncounterDatetime", "Please specify a visit date.");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "mdrtb.form.errors.noLocation", "Please specify a location.");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "provider", "mdrtb.form.errors.noProvider", "Please specify a provider");
	    
	    if (errors.getFieldError("weight") != null) {
	    	errors.reject("mdrtb.form.errors.weight", "Weight must be a numeric value.");
	    }
	    
	    if (errors.getFieldError("temperature") != null) {
	    	errors.reject("mdrtb.form.errors.temperature", "Temperature must be a numeric value.");
	    }
	    
	    if (errors.getFieldError("pulse") != null) {
	    	errors.reject("mdrtb.form.errors.pulse", "Pulse must be a numeric value.");
	    }
	    
	    if (errors.getFieldError("systolicBloodPressure") != null) {
	    	errors.reject("mdrtb.form.errors.systolicBloodPressure", "Blood pressure must be a numeric value.");
	    }
	    
	    if (errors.getFieldError("respiratoryRate") != null) {
	    	errors.reject("mdrtb.form.errors.respiratoryRate", "Respiratory rate must be a numeric value.");
	    }
    }
}
