package org.openmrs.module.labmodule;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.openmrs.Cohort;
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
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.regimen.Regimen;
import org.openmrs.module.labmodule.regimen.RegimenUtils;
import org.openmrs.module.labmodule.reporting.data.Cohorts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.Test;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

public class TbUtil {
    
    protected static final Log log = LogFactory.getLog(TbUtil.class);
    
    
    private static List<Concept> dst1Drugs = null;
    private static List<Concept> dst2Drugs = null;
    private static List<Concept> flqs = null;
    //DST1 Drugs
    public static Concept rif 		= null ;
	public static Concept inh  		= null ;
	public static Concept eth  		= null ;
	public static Concept strept 	= null ;
	public static Concept pyr 		= null ;

	//DST2 Drugs
	public static Concept ofx 	 = null ;
	public static Concept mox  	 = null ;
	public static Concept lfx  	 = null ;
	public static Concept ptoept = null ;
	public static Concept lzd 	 = null ;
	public static Concept cfz 	 = null ;
	public static Concept bdq 	 = null ;
	public static Concept dlm 	 = null ;
	public static Concept pas 	 = null ;
	public static Concept cm 	 = null ;
	public static Concept km 	 = null ;
	public static Concept am 	 = null ;

	public static List<Concept> getDst1Drugs()
	{
		if(dst1Drugs == null)
		{
			dst1Drugs =  new ArrayList<Concept>();
			dst1Drugs.add(rif);
			dst1Drugs.add(inh);
			dst1Drugs.add(eth);
			dst1Drugs.add(strept);
			dst1Drugs.add(pyr);			
		}
		
		return dst1Drugs;
	}

	public static List<Concept> getDst2Drugs()
	{
		if(dst2Drugs == null)
		{
			dst2Drugs =  new ArrayList<Concept>();
			dst2Drugs.add(getOfx() );
			dst2Drugs.add(getMox());
			dst2Drugs.add(getLfx() );
			dst2Drugs.add(getPtoept());
			dst2Drugs.add(getLzd() );
			dst2Drugs.add(getCfz() );
			dst2Drugs.add(getBdq() );
			dst2Drugs.add(getDlm() );
			dst2Drugs.add(getPas() );
			dst2Drugs.add(getCm() );
			dst2Drugs.add(getKm() );
			dst2Drugs.add(getAm() );
		}
		return dst2Drugs;
	}
	
	public static List<Concept> getFLQs()
	{
		if(flqs== null)
		{
			flqs = new ArrayList<Concept>();
			flqs.add(getOfx());
			flqs.add(getLfx());
			flqs.add(getMox());				
		}
		return flqs;
	}
			
    public static Concept getRif() {
		if(rif==null)
		{
			rif = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_R);   
		}
    	return rif;
	}

	public static Concept getInh() {
		if(inh == null)
		{
			inh = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_H);   
		}
		return inh;
	}

	public static Concept getEth() {
		if(eth == null)
		{
			eth = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_E);   
		}
		return eth;
	}
	
	public static Concept getStrept() {
		if(strept == null)
		{
			strept = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_S);			
		}
		return strept;
	}

	public static Concept getPyr() {
		if(pyr == null)
		{
			pyr = Context.getService(TbService.class).getConcept(TbConcepts.DST_RESISTANCE_Z);
		}
		return pyr;
	}

	///////////////  DST2 Drugs ///////////////
	
	public static Concept getOfx() {
		if(ofx == null)
		{
			ofx = Context.getService(TbService.class).getConcept(TbConcepts.OFX_R);
		}
		return ofx;
	}

	public static Concept getMox() {
		if(mox == null)
		{
			mox = Context.getService(TbService.class).getConcept(TbConcepts.MOX_R);
		}
		return mox;
	}

	public static Concept getLfx() {
		if(lfx == null)
		{
			lfx = Context.getService(TbService.class).getConcept(TbConcepts.LFX_R);
		}
		return lfx;
	}

	public static Concept getPtoept() {
		if(ptoept == null)
		{
			ptoept= Context.getService(TbService.class).getConcept(TbConcepts.PTO_R);
		}
		return ptoept;
	}

	public static Concept getLzd() {
		if(lzd== null)
		{
			lzd= Context.getService(TbService.class).getConcept(TbConcepts.LZD_R);
		}
		return lzd;
	}

	public static Concept getCfz() {
		if(cfz== null)
		{
			cfz= Context.getService(TbService.class).getConcept(TbConcepts.CFZ_R);
		}
		return cfz;
	}

	public static Concept getBdq() {
		if(bdq== null)
		{
			bdq= Context.getService(TbService.class).getConcept(TbConcepts.BDQ_R);
		}
		return bdq;
	}

	public static Concept getDlm() {
		if(dlm== null)
		{
			dlm= Context.getService(TbService.class).getConcept(TbConcepts.DLM_R);
		}
		return dlm;
	}

	public static Concept getPas() {
		if(pas== null)
		{
			pas= Context.getService(TbService.class).getConcept(TbConcepts.PAS_R);
		}
		return pas;
	}

	public static Concept getCm() {
		if(cm== null)
		{
			cm= Context.getService(TbService.class).getConcept(TbConcepts.CM_R);
		}
		return cm;
	}

	public static Concept getKm() {
		if(km== null)
		{
			km= Context.getService(TbService.class).getConcept(TbConcepts.KM_R);
		}
		return km;
	}

	public static Concept getAm() {
		if(am== null)
		{
			am= Context.getService(TbService.class).getConcept(TbConcepts.AM_R);
		}
		return am;
	}

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
      
      public static List<Obs> getSameObsFromObsGroup(Concept concept, Obs group) {
        List<Obs> matchingObs= new ArrayList<Obs>();	
    	  if (group.getGroupMembers() != null) {
        		for(Obs obs : group.getGroupMembers()) {
        			// need to check for voided obs here because getGroupMembers returns voided obs
        			if (!obs.isVoided() && obs.getConcept().equals(concept)) {
        				matchingObs.add(obs);
        			}
        		}
        	}
        	return matchingObs;
        }
      
      public static Obs getObsFromObsGroup(Integer obsId, Obs group) {
    	  if (group.getGroupMembers() != null) {
        		for(Obs obs : group.getGroupMembers()) {
        			// need to check for voided obs here because getGroupMembers returns voided obs        			
        			if (!obs.isVoided() && obs.getId()!=null && obs.getId().equals(obsId)) {
        				
        				return obs;
        			}
        		}
        	}
        	return null;
      }
      
      public static void voidObsInEncounter(Obs obs, Encounter encounter, String reason)
      {
    	  if(obs!= null && encounter != null)
    	  {
    		  Set<Obs> allObs = encounter.getAllObs();
    		  for(Obs currentObs: allObs)
    		  {
    			  if(currentObs.equals(obs))
    			  {
    				  currentObs.setVoided(true);
    				  if(reason != null && "".equalsIgnoreCase(reason.trim()))
    					  currentObs.setVoidReason(reason);
    				  else
    					  currentObs.setVoidReason("void by lab module");
    				  return;
    			  }
    		  }
    	  }
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
  	 * Iterates through all the top-level obs in the encounter and
  	 * returns the first one that who concept matches the specified concept
  	 * Returns null if obs not found
  	 */
  	public static List<Obs> getObsListFromEncounter(Concept concept, Encounter encounter) {
  		List<Obs> matchingObs =  new ArrayList<Obs>();
  		if (encounter.getObsAtTopLevel(false) != null) {
  			for (Obs obs : encounter.getObsAtTopLevel(false)) {
  				if (!obs.isVoided() && obs.getConcept().equals(concept)) {
  					matchingObs.add(obs);
  				}
  			}
  		}
  		return matchingObs;
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
   		
    	return types;
	}
	
	
	/**
	 * Returns all the concepts that represent positive results for a smear or culture
	 */
    public static Set<Concept> getPositiveResultConcepts() {
    	TbService service = Context.getService(TbService.class);
    	
    	// create a list of all concepts that represent positive results
    	Set<Concept> positiveResults = new HashSet<Concept>();
    	positiveResults.add(service.getConcept(TbConcepts.STRONGLY_POSITIVE));
    	positiveResults.add(service.getConcept(TbConcepts.MODERATELY_POSITIVE));
    	positiveResults.add(service.getConcept(TbConcepts.WEAKLY_POSITIVE));
    	positiveResults.add(service.getConcept(TbConcepts.POSITIVE));
    	positiveResults.add(service.getConcept(TbConcepts.SCANTY));
    	
    	//Xpert and HAIN
    	positiveResults.add(service.getConcept(TbConcepts.DETECTED));
    	
    	
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
    				Concept concept = Context.getService(TbService.class).getConcept(drug);
    				
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
    	return TbUtil.sortDrugs(drugs, Context.getService(TbService.class).getMdrtbDrugs());
    }

    /**
     * Given a list of concepts, sorts them in the same order as the list of antiretrovirals
     * (All non-antiretrovirals are ignored)
     */
	public static List<Concept> sortAntiretrovirals(List<Concept> drugs) {
    	return TbUtil.sortDrugs(drugs, Context.getService(TbService.class).getAntiretrovirals());
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
	@SuppressWarnings("unchecked")
    public static String assignIdentifier(PatientIdentifierType type) {		
		try {
			Class identifierSourceServiceClass = Context.loadClass("org.openmrs.module.idgen.service.IdentifierSourceService");
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
	 * Utility method to return patients matching passed criteria
	 * @return Cohort
	 */
	public static Cohort getDOTSPatients(String identifier, String name, String enrollment, Location location, List<ProgramWorkflowState> states) {
		
		Cohort cohort = Context.getPatientSetService().getAllPatients();
		
		TbService ms = (TbService) Context.getService(TbService.class);
		
		Date now = new Date();
		Program tbProgram = ms.getTbProgram();
		
		if ("current".equals(enrollment)) {
			Cohort current = Context.getPatientSetService().getPatientsInProgram(tbProgram, now, now);
			cohort = Cohort.intersect(cohort, current);
		}
		else {
			Cohort ever = Context.getPatientSetService().getPatientsInProgram(tbProgram, null, null);
			if ("previous".equals(enrollment)) {
				Cohort current = Context.getPatientSetService().getPatientsInProgram(tbProgram, now, now);
				Cohort previous = Cohort.subtract(ever, current);
				cohort = Cohort.intersect(cohort, previous);   			
			}
			else if ("never".equals(enrollment)) {
				cohort = Cohort.subtract(cohort, ever);
			}
			else {
				cohort = Cohort.intersect(cohort, ever);
			}	
		}
		
		if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(identifier)) {
			name = "".equals(name) ? null : name;
			identifier = "".equals(identifier) ? null : identifier;
			Cohort nameIdMatches = new Cohort(Context.getPatientService().getPatients(name, identifier, null, false));
			cohort = Cohort.intersect(cohort, nameIdMatches);
		}
		
		// If Location is specified, limit to patients at this Location
		if (location != null) {
			CohortDefinition lcd = Cohorts.getLocationFilter(location, now, now);
			Cohort locationCohort;
            try {
	            locationCohort = Context.getService(CohortDefinitionService.class).evaluate(lcd, new EvaluationContext());
            }
            catch (EvaluationException e) {
            	  throw new MdrtbAPIException("Unable to evalute location cohort",e);
            }
			cohort = Cohort.intersect(cohort, locationCohort);
		}
		
		if (states != null) {
			Cohort inStates = Context.getPatientSetService().getPatientsByProgramAndState(null, states, now, now);
			cohort = Cohort.intersect(cohort, inStates);
		}
		
		return cohort;
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
	
	/**
	 * Utility method to return patients matching passed criteria. Difference between this and main method is that locations are matched by patient rayon
	 * @return Cohort
	 */
	public static Cohort getDOTSPatientsTJK(String identifier, String name, String enrollment, Location location, List<ProgramWorkflowState> states, Integer minage, Integer maxage, String gender) {
		
		
		Cohort cohort = Context.getPatientSetService().getAllPatients();
		
		TbService ms = (TbService) Context.getService(TbService.class);
		
		Date now = new Date();
		Program tbProgram = ms.getTbProgram();
		
		if ("current".equals(enrollment)) {
			Cohort current = Context.getPatientSetService().getPatientsInProgram(tbProgram, now, now);
			cohort = Cohort.intersect(cohort, current);
		}
		else {
			Cohort ever = Context.getPatientSetService().getPatientsInProgram(tbProgram, null, null);
			if ("previous".equals(enrollment)) {
				Cohort current = Context.getPatientSetService().getPatientsInProgram(tbProgram, now, now);
				Cohort previous = Cohort.subtract(ever, current);
				cohort = Cohort.intersect(cohort, previous);   			
			}
			else if ("never".equals(enrollment)) {
				cohort = Cohort.subtract(cohort, ever);
			}
			else {
				cohort = Cohort.intersect(cohort, ever);
			}	
		}
		
		if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(identifier)) {
			name = "".equals(name) ? null : name;
			identifier = "".equals(identifier) ? null : identifier;
			Cohort nameIdMatches = new Cohort(Context.getPatientService().getPatients(name, identifier, null, false));
			cohort = Cohort.intersect(cohort, nameIdMatches);
		}
		
		
		
		if (states != null) {
			Cohort inStates = Context.getPatientSetService().getPatientsByProgramAndState(null, states, now, now);
			cohort = Cohort.intersect(cohort, inStates);
		}
		
		// If Location is specified, limit to patients at this Location
		if (location != null) {
			//System.out.println("ENTERED!!!!!!!!!");
			//System.out.println("L:" + location.getCountyDistrict());
			Cohort fc = new Cohort();
			Set<Integer> idSet = cohort.getMemberIds();
	    	//System.out.println("SET SIZE:" + idSet.size());
	    	Iterator<Integer> itr = idSet.iterator();
	    	Integer idCheck = null;
	    	Patient patient = null;
	    	PersonAddress addr = null;
	    	PatientService ps = Context.getService(PatientService.class);
	    	while(itr.hasNext()) {
	    		idCheck = (Integer)itr.next();
	    		patient = ps.getPatient(idCheck);
	    		addr = patient.getPersonAddress();
	    		//System.out.println("A:"+ addr.getCountyDistrict());
	    		
	    		if(areRussianStringsEqual(addr.getCountyDistrict(),location.getCountyDistrict())==true)
	    			fc.addMember(idCheck);
	    		
	    	}
	    	
	    	cohort  = fc;
		}
		
		if(minage != null || maxage != null) {
			Cohort ageCohort = new Cohort();
			Patient patient = null;
			Set<Integer> idSet = cohort.getMemberIds();
			Iterator<Integer> itr = idSet.iterator();
	    	Integer idCheck = null;
	   
	    	PatientService ps = Context.getService(PatientService.class);
	    	boolean use = false;
	    	while(itr.hasNext()) {
	    		use = false;
	    		idCheck = (Integer)itr.next();
	    		patient = ps.getPatient(idCheck);
	    		TbService svc = Context.getService(TbService.class);
	    		
	    		TbPatientProgram mpp = svc.getMostRecentTbPatientProgram(patient);
	    		
	    		Date tsd = mpp.getTreatmentStartDateDuringProgram();
	    		
	    		if(minage!=null) {
	    			if(patient.getAge(tsd)>= minage.intValue()) {
	    				use = true;
	    			}
	    			else
	    				use = false;
	    				
	    		}
	    		
	    		if(maxage!=null) {
	    			if(patient.getAge(tsd)<= maxage.intValue()) {
	    				use = true;
	    			}
	    			else
	    				use = false;
	    		} 
	    		
	    		if(use) {
	    			ageCohort.addMember(patient.getPatientId());
	    		}
	    		
	    	}
	    	
	    	cohort = ageCohort;
			
			
		}
		
		if(gender!=null && gender.length()!=0) {
			Cohort genderCohort = new Cohort();
			Patient patient = null;
			Set<Integer> idSet = cohort.getMemberIds();
			Iterator<Integer> itr = idSet.iterator();
	    	Integer idCheck = null;
	   
	    	PatientService ps = Context.getService(PatientService.class);
	    	boolean use = false;
	    	while(itr.hasNext()) {
	    		use = false;
	    		idCheck = (Integer)itr.next();
	    		patient = ps.getPatient(idCheck);
	    		
	    		
	    		if(patient.getGender().equalsIgnoreCase(gender)) {
	    			genderCohort.addMember(patient.getPatientId());
	    		}
	    		
	    	}
	    	
	    	cohort = genderCohort;
		}
		
		
		return cohort;
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
	
	
	//added by Ali for TB04
	public static Integer calculateDiffInDays(Date fromDate, Date toDate) {
		Integer ret = null;
		GregorianCalendar fromCal = new GregorianCalendar();
		fromCal.setTimeInMillis(fromDate.getTime());
		GregorianCalendar toCal = new GregorianCalendar();
		toCal.setTimeInMillis(toDate.getTime());
		
		
		LocalDate fromLocalDate = new LocalDate(fromCal.get(GregorianCalendar.YEAR),  fromCal.get(GregorianCalendar.MONTH)+1, fromCal.get(GregorianCalendar.DATE));
		LocalDate toLocalDate = new LocalDate(toCal.get(GregorianCalendar.YEAR),  toCal.get(GregorianCalendar.MONTH)+1, toCal.get(GregorianCalendar.DATE));
		Days age = Days.daysBetween(fromLocalDate, toLocalDate);
		ret = age.getDays();
		
		
		return ret;
	}
}
