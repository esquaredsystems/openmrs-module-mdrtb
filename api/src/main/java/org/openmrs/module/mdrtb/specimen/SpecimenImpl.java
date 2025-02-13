package org.openmrs.module.mdrtb.specimen;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;

/**
 * An implementation of the MdrtbSpecimen. This wraps an Encounter and provides access to the
 * various specimen-related data in Encounter
 */
public class SpecimenImpl implements Specimen {
	
	// TODO: could potentially cache all the get/set variables in private instance variables here...
	// to make this more like a real "command object"; then could create a "initialize" method that
	// automatically runs all the getters (and thereby loading the cache)
	// could do this for all the specimen related objects
	// TODO: or, actually, perhaps the thing to do here is simply to pre-load the concept maps that the module uses?
	// this is all that would be required to "detach" this object from the business-layer (i think... for 
	// instance if we wanted to pass this object as a web service?)  (of course, not sure exactly how lazy loading would
	// affect this)
	
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Encounter encounter; // the encounter where information about the specimen is stored
	
	// TODO: we cache the result map; do we need to worry about resetting the cache ?
	// (right now it isn't much of an issue because in the web model a new specimen is instantiated during each request)
	private Map<Integer,List<DstResult>> dstResultsMap = null; 
	
	public SpecimenImpl() {
		// empty constructor
	}
	
	// set up a specimen object from an existing encounter
	public SpecimenImpl(Encounter encounter) {
		this.encounter = encounter;
	}
	
	// initialize a new specimen, given a patient
	public SpecimenImpl(Patient patient) {
		
		if(patient == null) {
			throw new RuntimeException("Can't create new specimen if patient is null");
		}
		
		// set up the encounter for this specimen
		encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
	
	}
	
	public Object getSpecimen() {
		return this.encounter;
	}
	
	public String getId() {
		if (this.encounter.getId() != null) {
			return this.encounter.getId().toString();
		}
		else {
			return null;
		}
	}
	
	public Culture addCulture() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		CultureImpl culture = new CultureImpl(this.encounter);
		
		// add the culture to the master encounter
		this.encounter.addObs(culture.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		culture.setLab(null);
		
		return culture;
	}
	
	public Dst addDst() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		DstImpl dst = new DstImpl(this.encounter);
		
		// add the dst to the master encounter
		this.encounter.addObs(dst.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		dst.setLab(null);
		
		return dst;
	}
	
	public ScannedLabReport addScannedLabReport() {
		// cast to an Impl so that we can access protected methods from within the specimen impl
		ScannedLabReportImpl report = new ScannedLabReportImpl(this.encounter);
		
		// add the scanned lab report back to the master encounter
		this.encounter.addObs(report.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		report.setLab(null);
		
		return report;
	}
	
	public Smear addSmear() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		SmearImpl smear = new SmearImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(smear.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the encounter
		smear.setLab(null);
		
		return smear;
	}
	
	public Concept getAppearance() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}
	
	public String getComments() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_COMMENTS), encounter);
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}
	
	public List<Culture> getCultures() {
		List<Culture> cultures = new LinkedList<Culture>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT))) {
					cultures.add(new CultureImpl(obs));
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public Date getDateCollected() {
		return encounter.getEncounterDatetime();
	}
	
	public List<Dst> getDsts() {
		List<Dst> dsts = new LinkedList<Dst>();
				
		// iterate through all the obs groups, create dsts from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT))) {
					dsts.add(new DstImpl(obs));
				}
			}
		}
		Collections.sort(dsts);
		return dsts;
	}
	
	public Map<Integer,List<DstResult>> getDstResultsMap() {
	         
		List<Dst> dsts = getDsts();
				
	    if (dstResultsMap == null && dsts.size() > 0) {  
	    	dstResultsMap = new HashMap<Integer,List<DstResult>>();
	    	
	    	for(Dst dst : dsts) {
	    		for(DstResult result : dst.getResults()) {
	    		
	    			Integer drug = result.getDrug().getId();
    			
	    			// if a result for this drug already exists in the map, attach this result to that list
	    			if(dstResultsMap.containsKey(drug)) {
	    				dstResultsMap.get(drug).add(result);
	    				// re-sort, so that the concentrations are in order
	    				Collections.sort(dstResultsMap.get(drug));
	    			}
	    			// otherwise, create a new entry for this drug
	    			else {
	    				List<DstResult> drugResults = new LinkedList<DstResult>();
	    				drugResults.add(result);
	    				dstResultsMap.put(drug, drugResults);
	    			}
    			}
	    	}
	    }	
		return dstResultsMap;
	}
	
	public String getIdentifier() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}
	
	public Location getLocation() {
		return encounter.getLocation();
	}
	
	public Patient getPatient() {
		return encounter.getPatient();
	}
	
	public Person getProvider() {
		return encounter.getProvider();
	}
	
	public List<ScannedLabReport> getScannedLabReports() {
		List<ScannedLabReport> reports = new LinkedList<ScannedLabReport>();
		
		// iterate through top-level obs and create scanned lab reports
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCANNED_LAB_REPORT))) {
					// TODO: I've been unable to make this "soft" fail--if a scanned lab report is missing, it hangs the system
					// the API exception OpenMRS throws seems to kill the session
					try {
						reports.add(new ScannedLabReportImpl(obs));
					}
					catch(Exception e) {
						log.error("Unable to instantiate lab report:", e);
					}
				}
			}
		}
		return reports;
	}
	
	public List<Smear> getSmears() {
		List<Smear> smears = new LinkedList<Smear>();		
		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))) {
					smears.add(new SmearImpl(obs));
				}
			}
		}
		Collections.sort(smears);
		return smears;
	}
	
	public List<Test> getTests() {
		List<Test> tests = new LinkedList<Test>();
		
		tests.addAll(getSmears());
		tests.addAll(getCultures());
		tests.addAll(getDsts());
		tests.addAll(getXperts());
		tests.addAll(getHAINs());
		return tests;
	}
	
	public Concept getType() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}
	
	public Double getMonthOfTreatment() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT), encounter);
		
		if(obs == null) {
			return null;
		}
		
		else
			return obs.getValueNumeric();
	}
	
	public Integer getPatProgId() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if(obs == null) {
			return null;
		}
		
		else
			return obs.getValueNumeric().intValue();
	}
	
	public void removeScannedLabReport(ScannedLabReport report) {
		((ScannedLabReportImpl) report).voidScannedLabReport();
	}
	
	public void setAppearance(Concept appearance) {
		
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && appearance == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(appearance)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(appearance != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(appearance);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setComments(String comments) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_COMMENTS), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !StringUtils.equals(obs.getValueText(), comments)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			if(StringUtils.isNotBlank(comments)) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_COMMENTS), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(comments);
				encounter.addObs(obs);
			}
		}
	}
	
	public void setDateCollected(Date dateCollected) {
		encounter.setEncounterDatetime(dateCollected);
	
		// also propagate this date to all the other obs
		for(Obs obs : encounter.getAllObs()) {
			obs.setObsDatetime(dateCollected);
		}
	}
	
	public void setIdentifier(String id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		
		 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isEmpty(id)) {
			return;
		}
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !StringUtils.equals(obs.getValueText(),id)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			if(StringUtils.isNotBlank(id)) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(id);
				encounter.addObs(obs);
			}
		}
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// also propagate this location to the appropriate obs
		// TODO: remember to add any other obs here that get added!
		// ** but note that we don't want to propogate location to scanned lab result obs **
		Obs id = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		if (id != null) {
			id.setLocation(location);
		}			
		Obs type = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE), encounter);
		if (type != null) {
			type.setLocation(location);
		}
		Obs comments = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_COMMENTS), encounter);
		if (comments != null) {
			comments.setLocation(location);
		}
		Obs appearance = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE), encounter);
		if (appearance != null) {
			appearance.setLocation(location);
		}
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// also propagate this patient to the appropriate obs
		Obs id = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		if (id != null) {
			id.setPerson(patient);
		}
		Obs type = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE), encounter);
		if (id != null) { 
			type.setPerson(patient);
		}
	}
	
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public void setType(Concept type) {
		
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && type == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			if(type != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Xpert addXpert() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		XpertImpl xpert = new XpertImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(xpert.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		xpert.setLab(null);
		
		return xpert;
	}
	
	public List<Xpert> getXperts() {
		List<Xpert> xperts = new LinkedList<Xpert>();		
		// iterate through all the obs groups, create xperts from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT))) {
					xperts.add(new XpertImpl(obs));
				}
			}
		}
		Collections.sort(xperts);
		return xperts;
	}
	
	public HAIN addHAIN() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		HAINImpl hain = new HAINImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(hain.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		hain.setLab(null);
		
		return hain;
	}
	
	public List<HAIN> getHAINs() {
		List<HAIN> hains = new LinkedList<HAIN>();		
		// iterate through all the obs groups, create hains from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT))) {
					hains.add(new HAINImpl(obs));
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public HAIN2 addHAIN2() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		HAIN2Impl hain2 = new HAIN2Impl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(hain2.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		hain2.setLab(null);
		
		return hain2;
	}
	
	public List<HAIN2> getHAIN2s() {
		List<HAIN2> hains = new LinkedList<HAIN2>();		
		// iterate through all the obs groups, create hains from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT))) {
					hains.add(new HAIN2Impl(obs));
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	/**
	 * Implementation of comparable method
	 */
	public int compareTo(Specimen specimenToCompare) {
		return this.getDateCollected().compareTo(specimenToCompare.getDateCollected());
	}
}
