package org.openmrs.module.mdrtb.specimen;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;

/**
 * An implementation of the MdrtbSpecimen. This wraps an Encounter and provides access to the
 * various specimen-related data in Encounter
 */

public class MdrtbSpecimenImpl implements MdrtbSpecimen {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	Encounter encounter; // the encounter where information about the specimen is stored
	
	MdrtbFactory mdrtbFactory;
	
	public MdrtbSpecimenImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	// set up a specimen object from an existing encounter
	public MdrtbSpecimenImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		this.encounter = encounter;
	}
	
	// initialize a new specimen, given a patient
	public MdrtbSpecimenImpl(Patient patient) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(patient == null) {
			throw new RuntimeException("Can't create new specimen if patient is null");
		}
		
		// set up the encounter for this specimen
		encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
	
		// add appropriate top-level obs to the encounter
		// encounter.addObs(new Obs(patient, mdrtbFactory.getConceptSpecimenID(), null, null));
		// encounter.addObs(new Obs(patient, mdrtbFactory.getConceptSampleSource(), null, null));
	}
	
	public Object getSpecimen() {
		return this.encounter;
	}
	
	public String getId() {
		return this.encounter.getId().toString();
	}
	
	public MdrtbCulture addCulture() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		MdrtbCultureImpl culture = new MdrtbCultureImpl(this.encounter);
		
		// add the culture to the master encounter
		this.encounter.addObs(culture.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		culture.setLab(null);
		
		return culture;
	}
	
	public MdrtbDst addDst() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		MdrtbDstImpl dst = new MdrtbDstImpl(this.encounter);
		
		// add the dst to the master encounter
		this.encounter.addObs(dst.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		dst.setLab(null);
		
		return dst;
	}
	
	public MdrtbSmear addSmear() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		MdrtbSmearImpl smear = new MdrtbSmearImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(smear.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		smear.setLab(null);
		
		return smear;
	}
	
	public String getComments() {
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSpecimenComments());
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}
	
	public List<MdrtbCulture> getCultures() {
		List<MdrtbCulture> cultures = new LinkedList<MdrtbCulture>();
		
		// TODO: sort by date, make smears comparable, turn this into a sorted list?
		
		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(mdrtbFactory.getConceptCultureParent())) {
					cultures.add(new MdrtbCultureImpl(obs));
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public Date getDateCollected() {
		return encounter.getEncounterDatetime();
	}
	
	public List<MdrtbDst> getDsts() {
		List<MdrtbDst> dsts = new LinkedList<MdrtbDst>();
		
		// TODO: sort by date, make smears comparable, turn this into a sorted list?
		
		// iterate through all the obs groups, create dsts from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(mdrtbFactory.getConceptDSTParent())) {
					dsts.add(new MdrtbDstImpl(obs));
				}
			}
		}
		Collections.sort(dsts);
		return dsts;
	}
	
	public String getIdentifier() {
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSpecimenID());
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
	
	public List<MdrtbSmear> getSmears() {
		List<MdrtbSmear> smears = new LinkedList<MdrtbSmear>();
		
		// TODO: sort by date, make smears comparable, turn this into a sorted list?
		
		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(mdrtbFactory.getConceptSmearParent())) {
					smears.add(new MdrtbSmearImpl(obs));
				}
			}
		}
		Collections.sort(smears);
		return smears;
	}
	
	public List<MdrtbTest> getTests() {
		List<MdrtbTest> tests = new LinkedList<MdrtbTest>();
		
		tests.addAll(getSmears());
		tests.addAll(getCultures());
		tests.addAll(getDsts());
	
		return tests;
	}
	
	public Concept getType() {
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSampleSource());
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}
	
	public void setComments(String comments) {
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSpecimenComments());
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isEmpty(comments)) {
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
			obs = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSpecimenComments(), encounter.getEncounterDatetime(), encounter.getLocation());
			obs.setValueText(comments);
			encounter.addObs(obs);
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
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSpecimenID());
		
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
			obs = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSpecimenID(), encounter.getEncounterDatetime(), encounter.getLocation());
			obs.setValueText(id);
			encounter.addObs(obs);
		}
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// also propagate this location to the appropriate obs
		// TODO: remember to add any other obs here that get added!
		Obs id = getObsFromEncounter(mdrtbFactory.getConceptSpecimenID());
		if (id != null) {
			id.setLocation(location);
		}			
		Obs type = getObsFromEncounter(mdrtbFactory.getConceptSampleSource());
		if (type != null) {
			type.setLocation(location);
		}
		Obs comments = getObsFromEncounter(mdrtbFactory.getConceptSpecimenComments());
		if (comments != null) {
			comments.setLocation(location);
		}
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// also propagate this patient to the appropriate obs
		Obs id = getObsFromEncounter(mdrtbFactory.getConceptSpecimenID());
		if (id != null) {
			id.setPerson(patient);
		}
		Obs type = getObsFromEncounter(mdrtbFactory.getConceptSampleSource());
		if (id != null) { 
			type.setPerson(patient);
		}
	}
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public void setType(Concept type) {
		// if there's an existing obs, set it to null (since this doesn't happen automatically when an encounter is saved)
		Obs obs = getObsFromEncounter(mdrtbFactory.getConceptSampleSource());
		
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
			obs = new Obs (encounter.getPatient(), mdrtbFactory.getConceptSampleSource(), encounter.getEncounterDatetime(), encounter.getLocation());
			obs.setValueCoded(type);
			encounter.addObs(obs);
		}
	}
	
	/**
	 * Utility methods 
	 */
	
	/**
	 * Iterates through all the top-level obs in the encounter and
	 * returns the first one that who concept matches the specified concept
	 * Returns null if obs not found
	 */
	Obs getObsFromEncounter(Concept concept) {
		if (encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if(!obs.isVoided() && obs.getConcept().equals(concept)) {
					return obs;
				}
			}
		}
		return null;
	}
}
