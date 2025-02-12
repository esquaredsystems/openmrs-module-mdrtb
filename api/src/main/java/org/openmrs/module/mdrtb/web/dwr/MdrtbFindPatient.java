package org.openmrs.module.mdrtb.web.dwr;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.web.dwr.PatientListItem;

public class MdrtbFindPatient {
    protected final Log log = LogFactory.getLog(getClass());
    
    public Collection<Object> findPatients(String searchValue, boolean includeVoided, boolean onlyMdrTbPatients) {
        
        Collection<Object> patientList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        log.info(userId + "|" + searchValue);
        
        PatientService ps = Context.getPatientService();
        List<Patient> patients;        
            patients = ps.getPatients(searchValue);
            patientList = new Vector<Object>(patients.size());
            String primaryIdentifier = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
            for (Patient p : patients) {
            	PatientListItem patientListItem = new PatientListItem(p);                
                // make sure the correct patient identifier is set on the patient list item
                if (StringUtils.isNotBlank(primaryIdentifier) && p.getPatientIdentifier(primaryIdentifier) != null) {
                	patientListItem.setIdentifier(p.getPatientIdentifier(primaryIdentifier).getIdentifier());
                }  
                boolean includePatient = true;
            	if(onlyMdrTbPatients){
	            	List<MdrtbPatientProgram> mdrtbPatientProgram = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(p);
	                if(mdrtbPatientProgram==null || (mdrtbPatientProgram!=null && mdrtbPatientProgram.size()<1)){
	                	includePatient=false;
	                }
            	}
            	if(includePatient){
            		patientList.add(patientListItem);
            	}
            }
                   
        
        // I'm taking out the 'minimal patients returned'
        // If this needs to be smarter, there should be a better findPatients(...)
                
        return patientList;
    }
    
    public Collection<Object> findPeople(String searchValue, String dateString, boolean includeVoided) {
        
        Collection<Object> patientList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        PersonService ps = Context.getPersonService();
        List<Person> patients;
        
        patients = ps.getPeople(searchValue, false);
        patientList = new Vector<Object>(patients.size());
        String persAttTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_person_attribute_type");
        PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(persAttTypeString);
        for (Person p : patients){
            if (p.getAttributes(pat.getPersonAttributeTypeId()) != null && !p.getAttributes(pat.getPersonAttributeTypeId()).isEmpty())
                patientList.add(new MdrtbPersonListItem(p, dateString));
        }        
        return patientList;
    }
    
 public Collection<Object> findAllPeople(String searchValue, String dateString, boolean includeVoided) {
        
        Collection<Object> patientList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        PersonService ps = Context.getPersonService();
        List<Person> patients;
        
        patients = ps.getPeople(searchValue, false);
        patientList = new Vector<Object>(patients.size());

        for (Person p : patients){
                patientList.add(new MdrtbPersonListItem(p, dateString));
        }        
        return patientList;
    }
}
