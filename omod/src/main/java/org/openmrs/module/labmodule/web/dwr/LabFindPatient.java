package org.openmrs.module.labmodule.web.dwr;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.web.dwr.PatientListItem;

public class LabFindPatient {
    protected final Log log = LogFactory.getLog(getClass());
    
    public Collection findPatients(String searchValue, boolean includeVoided) {
        
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
                LabPatientListItem patientListItem = new LabPatientListItem(p);
                
                // make sure the correct patient identifier is set on the patient list item
                if (StringUtils.isNotBlank(primaryIdentifier) && p.getPatientIdentifier(primaryIdentifier) != null) {
                	patientListItem.setIdentifier(p.getPatientIdentifier(primaryIdentifier).getIdentifier());
                }
                
                String suspectIdentifierName = "Suspect Id";
                PatientIdentifierType suspectIdentifierType = ps.getPatientIdentifierTypeByName(suspectIdentifierName);
                //Following code is commented to only show up suspects for Lab patients as requested for new Tajik TOR 2017
              String pi= Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
        		if(p.getPatientIdentifier(pi) == null){
        			patientListItem.setPatientStatus("Suspect");
        		}
        		else{
        			patientListItem.setPatientStatus("Confirmed");
        		}
        		if(pi.equalsIgnoreCase(suspectIdentifierType.getName()))
        		{
        			patientListItem.setPatientStatus("Suspect");
        		}
        		Set<PersonAddress> address = p.getAddresses();
      	         for (PersonAddress personAddress : address) {
      	        	 if(personAddress.getStateProvince()!=null || personAddress.getStateProvince()!="")
      	        		 patientListItem.setRayon(personAddress.getStateProvince());
      	        	 else if(personAddress.getCityVillage()!=null || personAddress.getCityVillage()!="")
      	        		 patientListItem.setRayon(personAddress.getCityVillage());
      		     	 else if(personAddress.getCountyDistrict()!=null || personAddress.getCountyDistrict()!="")
      	        		 patientListItem.setRayon(personAddress.getCountyDistrict());
      		     }
      	         
      	       String addressString = "";
	   	        if(p.getPersonAddress().getAddress1() != null && !p.getPersonAddress().getAddress1().equals("") && p.getPersonAddress().getAddress1() != "null")
	   	        	addressString = p.getPersonAddress().getAddress1();
	   	        if(p.getPersonAddress().getAddress2() != null && !p.getPersonAddress().getAddress2().equals("") && p.getPersonAddress().getAddress2() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getAddress2();
	   	        if(p.getPersonAddress().getRegion() != null && !p.getPersonAddress().getRegion().equals("") && p.getPersonAddress().getRegion() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getRegion();
	   	        if(p.getPersonAddress().getCountyDistrict() != null && !p.getPersonAddress().getCountyDistrict().equals("") && p.getPersonAddress().getCountyDistrict() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getCountyDistrict();
	   	        if(p.getPersonAddress().getStateProvince() != null && !p.getPersonAddress().getStateProvince().equals("") && p.getPersonAddress().getStateProvince() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getStateProvince();
	   	        
	   	     if(addressString.startsWith(", "))
  	        	  addressString = addressString.replaceFirst(", ", "");
	   	        
     	       patientListItem.setAddress(addressString);
      	         
      	       patientList.add(patientListItem);
        		      	
            }
                            
        return patientList;
    }
    
    public Collection findPeople(String searchValue, String dateString, boolean includeVoided) {
        
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
                patientList.add(new LabPersonListItem(p, dateString));
        }        
        return patientList;
    }
    
 public Collection findAllPeople(String searchValue, String dateString, boolean includeVoided) {
        
        Collection<Object> patientList = new Vector<Object>();

        Integer userId = -1;
        if (Context.isAuthenticated())
            userId = Context.getAuthenticatedUser().getUserId();
        PersonService ps = Context.getPersonService();
        List<Person> patients;
        
        patients = ps.getPeople(searchValue, false);
        patientList = new Vector<Object>(patients.size());

        for (Person p : patients){
                patientList.add(new LabPersonListItem(p, dateString));
        }        
        return patientList;
    }
 
 public Collection findTestsThroughSuspect(String searchValue, boolean includeVoided){
	 
	 Collection<LabPatientListItem> patientList = new Vector<LabPatientListItem>();

     Integer userId = -1;
     if (Context.isAuthenticated())
         userId = Context.getAuthenticatedUser().getUserId();
     log.info(userId + "|" + searchValue);
     
     PatientService ps = Context.getPatientService();
     List<Patient> patients;
     
     // Get Encounter Type - LAB TEST Results
  	 Collection<EncounterType> encounterType = Context.getEncounterService().findEncounterTypes(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
  	 
  	 Concept concept = Context.getService(TbService.class).getConcept(TbConcepts.LAB_NO);
  	 List<Concept> concepts = new ArrayList<Concept>();
  	 concepts.add(concept);
  	 
         patients = ps.getPatients(searchValue);
         patientList = new Vector<LabPatientListItem>(patients.size());
         String primaryIdentifier = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
         for (Patient p : patients) {
        	 
	        // Get all Encounters for encounter name and patient...
          	 List<Encounter> encounters = Context.getEncounterService ().getEncounters (p, null, null, null, null, encounterType, false);
          	
	       	 for(Encounter e : encounters){
	       		LabPatientListItem patientListItem = new LabPatientListItem(p);
	             // make sure the correct patient identifier is set on the patient list item
	             if (StringUtils.isNotBlank(primaryIdentifier) && p.getPatientIdentifier(primaryIdentifier) != null) {
	             	patientListItem.setIdentifier(p.getPatientIdentifier(primaryIdentifier).getIdentifier());
	             }
	       		 
	       		 LabResultImpl labResult = new LabResultImpl(e);
	             
	       		 patientListItem.setRecievedDate(e.getEncounterDatetime());
	       		 patientListItem.setRayon(e.getLocation().toString());
	             patientListItem.setLabNumber(labResult.getLabNumber());
	             patientListItem.setMicroscopyTests(labResult.getMicroscopies().size());
	             patientListItem.setXpertTests(labResult.getXperts().size());
	             patientListItem.setHainTests(labResult.getHAINS().size());
	             patientListItem.setHain2Tests(labResult.getHAINS2().size());
	             patientListItem.setCultureTests(labResult.getCultures().size());
	             patientListItem.setEncounterId(Integer.parseInt(labResult.getId()));
	             patientListItem.setPatientStatus(labResult.getStatus());
	             
	            String addressString = "";
	   	        if(p.getPersonAddress().getAddress1() != null && !p.getPersonAddress().getAddress1().equals("") && p.getPersonAddress().getAddress1() != "null")
	   	        	addressString = p.getPersonAddress().getAddress1();
	   	        if(p.getPersonAddress().getAddress2() != null && !p.getPersonAddress().getAddress2().equals("") && p.getPersonAddress().getAddress2() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getAddress2();
	   	        if(p.getPersonAddress().getRegion() != null && !p.getPersonAddress().getRegion().equals("") && p.getPersonAddress().getRegion() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getRegion();
	   	        if(p.getPersonAddress().getCountyDistrict() != null && !p.getPersonAddress().getCountyDistrict().equals("") && p.getPersonAddress().getCountyDistrict() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getCountyDistrict();
	   	        if(p.getPersonAddress().getStateProvince() != null && !p.getPersonAddress().getStateProvince().equals("") && p.getPersonAddress().getStateProvince() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getStateProvince();
	   	        
	   	          if(addressString.startsWith(", "))
	   	        	  addressString = addressString.replaceFirst(", ", "");
	   	         
	     	     patientListItem.setAddress(addressString);
	             
	             patientList.add(patientListItem);
	       		 
	       	 }
	       	 
         	
         }
               
     return patientList;
	 
	 
 }
 
 
 public Collection findTestsThroughId(String searchValue, boolean includeVoided){
	 
	 Collection<Object> patientList = new Vector<Object>();
	 String primaryIdentifier = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
	 
	 // Get Encounter Type - LAB TEST Results
	 Collection<EncounterType> encounterType = Context.getEncounterService().findEncounterTypes(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
	 
	 // Get all Encounters for encounter name...
	 List<Encounter> encounters = Context.getEncounterService ().getEncounters (null, null, null, null, null, encounterType, false);

	 // Get all concepts for Lab Test Id
	 Concept concept = Context.getService(TbService.class).getConcept(TbConcepts.LAB_NO);
	 List<Concept> concepts = new ArrayList<Concept>();
	 concepts.add(concept);
	 
	 // Get All obs from the encounters for concept Lab Test Id
	 List<Obs> observations = Context.getObsService ().getObservations (null, encounters, concepts, null, null, null, null, null, null, null, null, false);
	 for (Obs obs : observations)
	 {
	
		String testId = obs.getValueText ();  // get obs value
		
		// check if search value is substring or equals the obs value
		if (testId.contains(searchValue))   
		{
			 Patient p = obs.getPatient ();
			 LabPatientListItem patientListItem = new LabPatientListItem(p);
			 
			 String pi= Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
			 if(p.getPatientIdentifier(pi) == null){
				patientListItem.setPatientStatus("Suspect");
			 }
			 else{
				patientListItem.setPatientStatus("Confirmed");
			 }
			 
			// make sure the correct patient identifier is set on the patient list item
            if (StringUtils.isNotBlank(primaryIdentifier) && p.getPatientIdentifier(primaryIdentifier) != null) {
            	patientListItem.setIdentifier(p.getPatientIdentifier(primaryIdentifier).getIdentifier());
            }
            
            patientListItem.setLabNumber(testId);
            patientListItem.setRecievedDate(obs.getEncounter().getEncounterDatetime());
            
            LabResultImpl labResult = new LabResultImpl(obs.getEncounter());
            patientListItem.setRayon(labResult.getLocation().toString());
            patientListItem.setMicroscopyTests(labResult.getMicroscopies().size());
            patientListItem.setXpertTests(labResult.getXperts().size());
            patientListItem.setHainTests(labResult.getHAINS().size());
            patientListItem.setHain2Tests(labResult.getHAINS2().size());
            patientListItem.setCultureTests(labResult.getCultures().size());
            patientListItem.setEncounterId(Integer.parseInt(labResult.getId()));
            patientListItem.setPatientStatus(labResult.getStatus());
            patientListItem.setDst1Test(labResult.getDst1s().size());
        	
            String addressString = "";
   	        if(p.getPersonAddress().getAddress1() != null && !p.getPersonAddress().getAddress1().equals("") && p.getPersonAddress().getAddress1() != "null")
   	        	addressString = p.getPersonAddress().getAddress1();
   	        if(p.getPersonAddress().getAddress2() != null && !p.getPersonAddress().getAddress2().equals("") && p.getPersonAddress().getAddress2() != "null")
   	        	addressString = addressString + ", " +p.getPersonAddress().getAddress2();
   	        if(p.getPersonAddress().getRegion() != null && !p.getPersonAddress().getRegion().equals("") && p.getPersonAddress().getRegion() != "null")
   	        	addressString = addressString + ", " +p.getPersonAddress().getRegion();
   	        if(p.getPersonAddress().getCountyDistrict() != null && !p.getPersonAddress().getCountyDistrict().equals("") && p.getPersonAddress().getCountyDistrict() != "null")
   	        	addressString = addressString + ", " +p.getPersonAddress().getCountyDistrict();
   	        if(p.getPersonAddress().getStateProvince() != null && !p.getPersonAddress().getStateProvince().equals("") && p.getPersonAddress().getStateProvince() != "null")
   	        	addressString = addressString + ", " +p.getPersonAddress().getStateProvince();
   	        
   	     if(addressString.startsWith(", "))
	        	  addressString = addressString.replaceFirst(", ", "");
   	     
 	        patientListItem.setAddress(addressString);
            
            patientList.add(patientListItem);
        	
		}
		
		
	 }
		
	 return patientList;
	 
 }
 
 
public Collection findTestThroughDates(String dateFrom, String dateTo, boolean includeVoided) throws ParseException{
	 
	 Collection<Object> patientList = new Vector<Object>();
	 String primaryIdentifier = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
	 
	 // Get Encounter Type - LAB TEST Results
	 Collection<EncounterType> encounterType = Context.getEncounterService().findEncounterTypes(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
	 
	 DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	 Date dateF = format.parse(dateFrom);
	 Date dateT = format.parse(dateTo);
	 
	// Get all Encounters for encounter name...
	List<Encounter> encounters = Context.getEncounterService ().getEncounters (null, null, dateF, dateT, null, encounterType, false);
	
	for(Encounter e : encounters){
		
		Patient p = e.getPatient();
		LabPatientListItem patientListItem = new LabPatientListItem(p);
		
		String pi= Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
		if(p.getPatientIdentifier(pi) == null){
			patientListItem.setPatientStatus("Suspect");
		}
		else{
			patientListItem.setPatientStatus("Confirmed");
		}
		
        // make sure the correct patient identifier is set on the patient list item
        if (StringUtils.isNotBlank(primaryIdentifier) && p.getPatientIdentifier(primaryIdentifier) != null) {
        	patientListItem.setIdentifier(p.getPatientIdentifier(primaryIdentifier).getIdentifier());
        }
  		 
  		 LabResultImpl labResult = new LabResultImpl(e);
        
  		patientListItem.setRecievedDate(e.getEncounterDatetime());
        patientListItem.setLabNumber(labResult.getLabNumber());
        patientListItem.setMicroscopyTests(labResult.getMicroscopies().size());
        patientListItem.setXpertTests(labResult.getXperts().size());
        patientListItem.setHainTests(labResult.getHAINS().size());
        patientListItem.setHain2Tests(labResult.getHAINS2().size());
        patientListItem.setCultureTests(labResult.getCultures().size());
        patientListItem.setDst1Test(labResult.getDst1s().size());
        patientListItem.setEncounterId(Integer.parseInt(labResult.getId()));
        patientListItem.setPatientStatus(labResult.getStatus());
        
        String addressString = "";
	        if(p.getPersonAddress().getAddress1() != null && !p.getPersonAddress().getAddress1().equals("") && p.getPersonAddress().getAddress1() != "null")
	        	addressString = p.getPersonAddress().getAddress1();
	        if(p.getPersonAddress().getAddress2() != null && !p.getPersonAddress().getAddress2().equals("") && p.getPersonAddress().getAddress2() != "null")
	        	addressString = addressString + ", " +p.getPersonAddress().getAddress2();
	        if(p.getPersonAddress().getRegion() != null && !p.getPersonAddress().getRegion().equals("") && p.getPersonAddress().getRegion() != "null")
	        	addressString = addressString + ", " +p.getPersonAddress().getRegion();
	        if(p.getPersonAddress().getCountyDistrict() != null && !p.getPersonAddress().getCountyDistrict().equals("") && p.getPersonAddress().getCountyDistrict() != "null")
	        	addressString = addressString + ", " +p.getPersonAddress().getCountyDistrict();
	        if(p.getPersonAddress().getStateProvince() != null && !p.getPersonAddress().getStateProvince().equals("") && p.getPersonAddress().getStateProvince() != "null")
	        	addressString = addressString + ", " +p.getPersonAddress().getStateProvince();
	        
	        if(addressString.startsWith(", "))
 	        	  addressString = addressString.replaceFirst(", ", "");
	        
	       patientListItem.setAddress(addressString);
        
        patientList.add(patientListItem);
	}
		
	 return patientList; 
 }
 
public Collection findLatestEncounterByUserId(int userId, int numberOfEnc, boolean includeVoided){
	 Collection<Object> patientList = new Vector<Object>();
	 
	 User user = Context.getUserService().getUser(userId);
	 
	 Collection<EncounterType> encounterType = Context.getEncounterService().findEncounterTypes(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type"));
	 List<Encounter> encounters = Context.getEncounterService ().getEncounters (null, null, null, null, null, encounterType, false);
	 
	 int counter = 0;
	 for(int i=encounters.size()-1; i>=0; i--){
		 
		 Encounter e = encounters.get(i);
		 
		 if(e.getCreator().equals(user)){
			 
			 Patient p = e.getPatient();
			 LabPatientListItem patientListItem = new LabPatientListItem(p);
			 
			 String pi= Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
				if(p.getPatientIdentifier(pi) == null){
					patientListItem.setPatientStatus("Suspect");
				}
				else{
					patientListItem.setPatientStatus("Confirmed");
				}
			 
			 LabResultImpl labResult = new LabResultImpl(e);
		        
	  		 patientListItem.setRecievedDate(e.getEncounterDatetime());
		     patientListItem.setLabNumber(labResult.getLabNumber());
		     patientListItem.setMicroscopyTests(labResult.getMicroscopies().size());
		     patientListItem.setXpertTests(labResult.getXperts().size());
		     patientListItem.setHainTests(labResult.getHAINS().size());
		     patientListItem.setHain2Tests(labResult.getHAINS2().size());
		     patientListItem.setCultureTests(labResult.getCultures().size());
		     patientListItem.setDst1Test(labResult.getDst1s().size());
		     patientListItem.setEncounterId(Integer.parseInt(labResult.getId()));
		     patientListItem.setPatientStatus(labResult.getStatus());
		     
		     String addressString = "";
	   	        if(p.getPersonAddress().getAddress1() != null && !p.getPersonAddress().getAddress1().equals("") && p.getPersonAddress().getAddress1() != "null")
	   	        	addressString = p.getPersonAddress().getAddress1();
	   	        if(p.getPersonAddress().getAddress2() != null && !p.getPersonAddress().getAddress2().equals("") && p.getPersonAddress().getAddress2() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getAddress2();
	   	        if(p.getPersonAddress().getRegion() != null && !p.getPersonAddress().getRegion().equals("") && p.getPersonAddress().getRegion() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getRegion();
	   	        if(p.getPersonAddress().getCountyDistrict() != null && !p.getPersonAddress().getCountyDistrict().equals("") && p.getPersonAddress().getCountyDistrict() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getCountyDistrict();
	   	        if(p.getPersonAddress().getStateProvince() != null && !p.getPersonAddress().getStateProvince().equals("") && p.getPersonAddress().getStateProvince() != "null")
	   	        	addressString = addressString + ", " +p.getPersonAddress().getStateProvince();
	   	        
	   	     if(addressString.startsWith(", "))
  	        	  addressString = addressString.replaceFirst(", ", "");
	   	        
  	       patientListItem.setAddress(addressString);
			 
			 patientList.add(patientListItem);
			 counter++;
		 }
		 
		 if(counter == numberOfEnc)
			 break;
	 }
	 	 
	 return patientList; 
}

}
