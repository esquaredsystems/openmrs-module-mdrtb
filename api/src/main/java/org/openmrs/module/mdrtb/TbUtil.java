package org.openmrs.module.mdrtb;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Test;

public class TbUtil {
    
    protected static final Log log = LogFactory.getLog(TbUtil.class);
    
    public static String getDOTSPatientIdentifier(Patient p){
        String ret = "";
        String piList = Context.getAdministrationService().getGlobalProperty("dotsreports.patient_identifier_type");    
        Set<PatientIdentifier> identifiers = p.getIdentifiers();
        for (PatientIdentifier pi : identifiers){
            if (pi.getIdentifierType().getName().equals(piList)){
                return pi.getIdentifier();
            }
        }
        if (identifiers.size() > 0){
            for (PatientIdentifier pi : identifiers){
                return pi.getIdentifier();
            }
        } 
        return ret;
    }

    public static Obs getMostRecentObs(Integer conceptId, Patient p){
        Concept c = Context.getConceptService().getConcept(conceptId);
        List<Obs> oList = Context.getObsService().getObservationsByPersonAndConcept(p, c);
        if (oList.size() > 0)
            return oList.get(oList.size()-1);
        return null;
    }
   
      
    /**
  	 * Iterates through all the obs in the test obs group and
  	 * returns the first one that who concept matches the specified concept
  	 * Returns null if obs not found
  	 * @param group TODO
  	 */
      public static Obs getObsFromObsGroup(Concept concept, Obs group) {
      	if (group.getGroupMembers() != null) {
      		for(Obs obs : group.getGroupMembers()) {
      			// need to check for voided obs here because getGroupMembers returns voided obs
      			if (!obs.isVoided() && obs.getConcept().equals(concept)) {
      				return obs;
      			}
      		}
      	}
      	return null;
      }
      
     /**
  	 * Iterates through all the top-level obs in the encounter and
  	 * returns the first one that who concept matches the specified concept
  	 * Returns null if obs not found
  	 */
  	public static Obs getObsFromEncounter(Concept concept, Encounter encounter) {
  		if (encounter.getObsAtTopLevel(false) != null) {
  			for (Obs obs : encounter.getObsAtTopLevel(false)) {
  				if (!obs.isVoided() && obs.getConcept().equals(concept)) {
  					return obs;
  				}
  			}
  		}
  		return null;
  	}
	
	/**
	 * Gets the antiretroviral regimens for a current patient
	 */
	public static List<Regimen> getAntiretroviralRegimens(Patient patient) {
    	
    	if (patient == null) {
    		return null;
    	}
    	
    	return RegimenUtils.getHivRegimenHistory(patient).getAllRegimens();
    }

	/**
	 * Returns a set of all encounter types associated with the MDR-TB Program
	 */
	public static Set<EncounterType> getTbEncounterTypes() {

		Set<EncounterType> types = new HashSet<EncounterType>();
		types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_out_encounter_type")));
    	types.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_in_encounter_type")));
    	return types;
	}
	
	
	/**
	 * Returns all the concepts that represent positive results for a smear or culture
	 */
    public static Set<Concept> getPositiveResultConcepts() {
    	MdrtbService service = Context.getService(MdrtbService.class);
    	
    	// create a list of all concepts that represent positive results
    	Set<Concept> positiveResults = new HashSet<Concept>();
    	positiveResults.add(service.getConcept(MdrtbConcepts.STRONGLY_POSITIVE));
    	positiveResults.add(service.getConcept(MdrtbConcepts.MODERATELY_POSITIVE));
    	positiveResults.add(service.getConcept(MdrtbConcepts.WEAKLY_POSITIVE));
    	positiveResults.add(service.getConcept(MdrtbConcepts.POSITIVE));
    	positiveResults.add(service.getConcept(MdrtbConcepts.SCANTY));
    	positiveResults.add(service.getConcept(MdrtbConcepts.LOWAFB));
    	
    	//Xpert and HAIN
    	positiveResults.add(service.getConcept(MdrtbConcepts.DETECTED));
    	
    	
    	return positiveResults;
    }
    
    /**
     * Returns the concept ids of all the concepts that represent positive results for a smear or culture
     */
    public static Integer [] getPositiveResultConceptIds() {
    	Set<Concept> positiveConcepts = getPositiveResultConcepts();
    	Integer [] positiveResultIds = new Integer[positiveConcepts.size()];
    	
    	int i = 0;
    	for (Concept positiveConcept : positiveConcepts) {
    		positiveResultIds[i] = positiveConcept.getConceptId();
    		i++;
    	}
    	
    	return positiveResultIds;
    }
 
    /**
     * Loads and sorts the drugs stored in the global property mdtrb.defaultDstDrugs
     */
    public static List<List<Object>> getDefaultDstDrugs() {
    	List<List<Object>> drugs = new LinkedList<List<Object>>();
    	
    	String defaultDstDrugs = Context.getAdministrationService().getGlobalProperty("mdrtb.defaultDstDrugs");
    	
    	if(StringUtils.isNotBlank(defaultDstDrugs)) {
    		// split on the pipe
    		for (String drugString : defaultDstDrugs.split("\\|")) {
    			
    			// now split into a name and concentration 
    			String drug = drugString.split(":")[0];
    			String concentration = null;
    			if (drugString.split(":").length > 1) {
    				concentration = drugString.split(":")[1];
    			}
    			
    			try {
    				// see if this is a concept id
    				Integer conceptId = Integer.valueOf(drug);
    				Concept concept = Context.getConceptService().getConcept(conceptId);
    				
    				if (concept == null) {
    					log.error("Unable to find concept referenced by id " + conceptId);
    				}
    				// add the concept/concentration pair to the list
    				else {
    					addDefaultDstDrugToMap(drugs, concept, concentration);
    				}
    			}    	
    			catch (NumberFormatException e) {
    				// if not a concept id, must be a concept map
    				Concept concept = Context.getService(MdrtbService.class).getConcept(drug);
    				
    				if (concept == null) {
    					log.error("Unable to find concept referenced by " + drug);
    				}
    				// add the concept to the list
    				else {
    					addDefaultDstDrugToMap(drugs, concept, concentration);
    				}
    			}
    		}
    	}
    	
    	return drugs;
    }
    
    /**
     * Private helper method used by getDefaultDstDrugs
     * (Adds an element to the default dst drug map)
     */
    private static void addDefaultDstDrugToMap(List<List<Object>> drugs, Concept concept, String concentration) {
    	List<Object> data = new LinkedList<Object>();
    	data.add(concept);
    	data.add(concentration);
    	drugs.add(data);
    }
    
    
	/**
	 * Given a list of concepts, sorts them in the same order as the list of MDR-TB drugs
	 * (All non-MDR-TB drugs are ignored) 
	 */
    // returns by getMdrtbDrugs(); all non-MDR-TB drug are ignored
    public static List<Concept> sortMdrtbDrugs(List<Concept> drugs) {
    	return TbUtil.sortDrugs(drugs, Context.getService(MdrtbService.class).getMdrtbDrugs());
    }

    /**
     * Given a list of concepts, sorts them in the same order as the list of antiretrovirals
     * (All non-antiretrovirals are ignored)
     */
	public static List<Concept> sortAntiretrovirals(List<Concept> drugs) {
    	return TbUtil.sortDrugs(drugs, Context.getService(MdrtbService.class).getAntiretrovirals());
    }

	/**
	 * Given a list of drugs to sort and a drug list, sorts the first list so that the
	 * drugs are in the same order as the second list; any drugs in the list to sort not
	 * found in the drug list are discarded
	 */
    public static List<Concept> sortDrugs(List<Concept> drugsToSort, List<Concept> drugList) {
    	List<Concept> sortedDrugs = new LinkedList<Concept>();
    	
    	for (Concept drug : drugList) {
    		if (drugsToSort.contains(drug)) {
    			sortedDrugs.add(drug);
    		}
    	}
    	
    	return sortedDrugs;
    }
    
    /**
	 * Gets a specific ProgramWorkflowState, given the concept associated with the state
	 */
    public static ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept) {
		for (ProgramWorkflowState state : Context.getProgramWorkflowService().getStates()) {
			if (state.getConcept().equals(programWorkflowStateConcept)) {
				return state;
			}
		}
		return null;
	}
    
    /**
     * Auto-assign a patient identifier for a specific identifier type, if required, if the idgen module is installed, using reflection
     * Auto generated method comment
     */
    public static String assignIdentifier(PatientIdentifierType type) {		
		try {
			Class<?> identifierSourceServiceClass = Context.loadClass("org.openmrs.module.idgen.service.IdentifierSourceService");
			Object idgen = Context.getService(identifierSourceServiceClass);
	        Method generateIdentifier = identifierSourceServiceClass.getMethod("generateIdentifier", PatientIdentifierType.class, String.class);
	        // note that generate identifier returns null if this identifier type is not set to be auto-generated
	        return (String) generateIdentifier.invoke(idgen, type, "auto-assigned during patient creation");
		}
		catch (Exception e) {
			log.error("Unable to access IdentifierSourceService for automatic id generation.  Is the Idgen module installed and up-to-date?", e);
		} 
		
		return null;
	}
	
	/**
	 * Given a concept, locale, and a string that represents a concept name tag,
	 * returns the first concept name for that concept that matches the language and is tagged with the specified tag
	 */
	public static ConceptName getConceptName(Concept concept, String language, String conceptNameTag) {
		if (concept == null) {
			log.error("No concept provided to findConceptName");
			return null;
		}
		
		ConceptNameTag tag = Context.getConceptService().getConceptNameTagByName(conceptNameTag);
		
		if (tag == null) {
			log.warn("Invalid concept name tag parameter " + conceptNameTag + " passed to findConceptName");
		}
		
		for (ConceptName name : concept.getNames()) {
			if ((language == null || name.getLocale() == null || name.getLocale().getLanguage() == null || name.getLocale().getLanguage().equals(language)) 
				&& ((tag == null) || (name.getTags().contains(tag)))) {
				return name;
			}				
		}
		
		return null;
	}
	
	/**
     * Configures the default values for a Test, based on the existing values for other tests in the specimen
     * Implements the following rule:
     * 
     * If this is the first test, and the specimen has a sample id, set the accession # field with this sample id.
     * If this is not the first test, then--
     * If the Accession # on all the existing tests and the sample ID on the specimen are all the same, set the accession # field with this number.
     * If the Lab, Date Ordered, or Date Received on all the existing tests are identical, set these fields with these values.
     * 
     */
    public static void setTestDefaults(Specimen specimen, Test test) { 	
    	
    	Set<String> accessionNumberSet = new HashSet<String>();
    	Set<Date> dateOrderedSet = new HashSet<Date>();
    	Set<Date> dateReceivedSet = new HashSet<Date>();
    	Set<Location> labSet = new HashSet<Location>();
    	
    	// first add the identifier of the sample to the accession number set
    	accessionNumberSet.add(specimen.getIdentifier());
    	
    	// now loop through all the tests for this sample, excluding the test we want to set the defaults for
    	for (Test t : specimen.getTests()) {
    		if (t != test) {
    			accessionNumberSet.add(t.getAccessionNumber());
    			dateOrderedSet.add(t.getDateOrdered());
    			dateReceivedSet.add(t.getDateReceived());
    			labSet.add(t.getLab());
    		}
    	}
    	
    	// test if any of are sets contain exactly one non-null member
    	if (accessionNumberSet.size() == 1 && !accessionNumberSet.contains(null)) {
    		test.setAccessionNumber(accessionNumberSet.iterator().next());
    	}
    	if (dateOrderedSet.size() == 1 && !dateOrderedSet.contains(null)) {
    		test.setDateOrdered(dateOrderedSet.iterator().next());
    	}
    	if (dateReceivedSet.size() == 1 && !dateReceivedSet.contains(null)) {
    		test.setDateReceived(dateReceivedSet.iterator().next());
    	}
    	if (labSet.size() == 1 && !labSet.contains(null)) {
    		test.setLab(labSet.iterator().next());
    	}
    }

	/**
	 * Tests whether a String is parseable as an Integer
	 */
	public static boolean isInteger(String string) {
	    try {
	        Integer.valueOf(string);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	
	/**
	 * Returns true/false if all the fields in the address are empty or null
	 */
	public static Boolean isBlank(PersonAddress address) {
		return StringUtils.isBlank(address.getAddress1()) && StringUtils.isBlank(address.getAddress2()) && StringUtils.isBlank(address.getCityVillage())
				&& StringUtils.isBlank(address.getStateProvince()) && StringUtils.isBlank(address.getCountry()) && StringUtils.isBlank(address.getCountyDistrict())
				&& StringUtils.isBlank(address.getNeighborhoodCell()) && StringUtils.isBlank(address.getPostalCode()) && StringUtils.isBlank(address.getTownshipDivision()) 
				&& StringUtils.isBlank(address.getLatitude()) && StringUtils.isBlank(address.getLongitude()) && StringUtils.isBlank(address.getRegion()) && StringUtils.isBlank(address.getSubregion()) 
				&& StringUtils.isBlank(address.getPostalCode());
	}
	
	public static boolean areRussianStringsEqual(String s1, String s2) {
		boolean result = false;
		
		if(s1==null || s2==null)
			return false;
		
		if(s1.length()==0 || s2.length()==0)
			return false;
		
		java.text.Collator collator = java.text.Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		
		int compResult = collator.compare(s1, s2);
		if(compResult==0)
			return true;
				
		return result;
	}
}
