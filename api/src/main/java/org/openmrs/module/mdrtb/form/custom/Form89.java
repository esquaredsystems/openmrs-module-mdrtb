package org.openmrs.module.mdrtb.form.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;

public class Form89 extends AbstractSimpleForm implements Comparable<Form89> {
	
	private TB03Form tb03;
	
	public Form89() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public Form89(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public Form89(Encounter encounter) {
		super(encounter);
		
	}
	
	public void initTB03(Integer patientProgramId) {
		
		TbPatientProgram tpp = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
		List<Encounter> encounters = null;
		if (tpp != null) {
			encounters = tpp.getTb03EncountersDuringProgramObs();
		}
		
		//EncounterType intakeType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
		if (encounters != null && encounters.size() != 0) {
			/*for (Encounter encounter : encounters) {
				// create a new status item for this encounter
				
				// now place the visit in the appropriate "bucket"
				if (encounter.getEncounterType().equals(intakeType)) {
					tb03 = new TB03Form(encounter);
					break;
				}
			}*/
			tb03 = new TB03Form(encounters.get(0));
		}
		
	}
	
	public TB03Form getTB03() {
		return tb03;
	}
	
	public void setTB03(TB03Form tb03) {
		this.tb03 = tb03;
	}
	
	public Integer getYearOfTB03Registration() {
		if (tb03 == null || tb03.getEncounterDatetime() == null)
			return null;
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(tb03.getEncounterDatetime().getTime());
		Integer year = gc.get(Calendar.YEAR);
		
		return year;
		
	}
	
	public String getPatientName() {
		PersonName p = getPatient().getPersonName();
		
		return p.getFamilyName() + "," + p.getGivenName();
	}
	
	public Concept getAnatomicalSite() {
		Obs obs = null;
		if (tb03 != null)
			obs = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), tb03.getEncounter());
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public Integer getAgeAtRegistration() {
		return MdrtbUtil.calculateAge(getPatient().getBirthdate(), getEncounterDatetime());
	}
	
	public String getGender() {
		if (encounter.getPatient().getGender().equals("M"))
			return Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.male");
		if (encounter.getPatient().getGender().equals("F"))
			return Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female");
		
		return "";
	}
	
	public Date getDateOfBirth() {
		return encounter.getPatient().getBirthdate();
	}
	
	public String getAddress() {
		
		PersonAddress pa = encounter.getPatient().getPersonAddress();
		if (pa == null)
			return null;
		
		String address = pa.getCountry() + "," + pa.getStateProvince() + "," + pa.getCountyDistrict();
		if (pa.getAddress1() != null && pa.getAddress1().length() != 0) {
			address += "," + pa.getAddress1();
			if (pa.getAddress2() != null && pa.getAddress2().length() != 0)
				address += "," + pa.getAddress2();
		}
		
		return address;
	}
	
	public Concept getLocationType() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setLocationType(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.LOCATION_TYPE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getProfession() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setProfession(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION),
		    encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PROFESSION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPopulationCategory() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPopulationCategory(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.POPULATION_CATEGORY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPlaceOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPlaceOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_DETECTION), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PLACE_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getDateFirstSeekingHelp() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setDateFirstSeekingHelp(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getCircumstancesOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCircumstancesOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getMethodOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setMethodOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.METHOD_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getEpSite() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setEpSite(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.SITE_OF_EPTB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPulSite() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPulSite(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getEpLocation() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setEpLocation(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.SITE_OF_EPTB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	/////////////////
	public Concept getPresenceOfDecay() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESENCE_OF_DECAY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPresenceOfDecay(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESENCE_OF_DECAY), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PRESENCE_OF_DECAY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	/////////////////
	public Date getDateOfDecaySurvey() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setDateOfDecaySurvey(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getDiabetes() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setDiabetes(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DIABETES), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getCnsdl() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCnsdl(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(),
				        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
				        encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getHtHeartDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setHtHeartDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getUlcer() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setUlcer(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(),
				        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
				        encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getMentalDisorder() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setMentalDisorder(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MENTAL_DISORDER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getIbc20() {
		Obs obs = null;
		
		if (tb03 != null) {
			obs = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST), tb03.getEncounter());
		}
		
		if (obs == null) {
			return null;
		} else {
			if (obs.getValueCoded().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE))) {
				return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
			}
			
			else if (obs.getValueCoded().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE))) {
				return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO);
			}
			
			return null;
		}
		
	}
	
	public Concept getCancer() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCancer(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class)
				        .getConcept(MdrtbConcepts.CANCER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getHepatitis() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setHepatitis(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COMORBID_HEPATITIS), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getKidneyDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setKidneyDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.KIDNEY_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getNoDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO_DISEASE),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setNoDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO_DISEASE),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do
		// nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has
		// changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs
			// when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NO_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getOtherDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
	}
	
	public void setOtherDisease(String site) {
		
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.OTHER_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getCmacDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setCmacDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCmacNumber() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
	}
	
	public void setCmacNumber(String number) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && number == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(number)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (number != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(number);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getForm89Date() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FORM89_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setForm89Date(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FORM89_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.FORM89_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPrescribedTreatment() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPrescribedTreatment(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getPatProgId() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setPatProgId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != id.intValue()) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(new Double(id));
				encounter.addObs(obs);
			}
		}
	}
	
	public List<SmearForm> getSmears() {
		if (getPatProgId() == null) {
			return new ArrayList<SmearForm>();
		}
		
		ArrayList<SmearForm> ret = new ArrayList<SmearForm>();
		List<SmearForm> allSmears = Context.getService(MdrtbService.class).getSmearForms(getPatProgId());
		
		if (allSmears == null)
			return ret;
		else {
			for (SmearForm sf : allSmears) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		
		return ret;
		
	}
	
	public List<XpertForm> getXperts() {
		/*if(getPatProgId()==null) {
			return new ArrayList<XpertForm>();
		}
		return Context.getService(MdrtbService.class).getXpertForms(getPatProgId());*/
		
		if (getPatProgId() == null) {
			return new ArrayList<XpertForm>();
		}
		
		ArrayList<XpertForm> ret = new ArrayList<XpertForm>();
		List<XpertForm> allXperts = Context.getService(MdrtbService.class).getXpertForms(getPatProgId());
		
		if (allXperts == null)
			return ret;
		else {
			for (XpertForm sf : allXperts) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		
		return ret;
		
	}
	
	public List<HAINForm> getHains() {
		/*		if(getPatProgId()==null) {
					return new ArrayList<HAINForm>();
				}
				return Context.getService(MdrtbService.class).getHAINForms(getPatProgId());*/
		if (getPatProgId() == null) {
			return new ArrayList<HAINForm>();
		}
		
		ArrayList<HAINForm> ret = new ArrayList<HAINForm>();
		List<HAINForm> allHains = Context.getService(MdrtbService.class).getHAINForms(getPatProgId());
		
		if (allHains == null)
			return ret;
		else {
			for (HAINForm sf : allHains) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		return ret;
	}
	
	public List<HAIN2Form> getHain2s() {
		if (getPatProgId() == null) {
			return new ArrayList<HAIN2Form>();
		}
		
		ArrayList<HAIN2Form> ret = new ArrayList<HAIN2Form>();
		List<HAIN2Form> allHains = Context.getService(MdrtbService.class).getHAIN2Forms(getPatProgId());
		
		if (allHains == null)
			return ret;
		else {
			for (HAIN2Form sf : allHains) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		return ret;
	}
	
	public Boolean getIsPulmonary() {
		Boolean result = null;
		
		Concept site = getAnatomicalSite();
		if (site != null) {
			
			if (site.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)))
				result = new Boolean(true);
			
			else if (site.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB)))
				result = new Boolean(false);
		}
		return result;
	}
	
	public Boolean getIsChildbearingAge() {
		Boolean result = false;
		
		if (encounter.getPatient().getGender().equals("F") && getAgeAtRegistration() >= 15 && getAgeAtRegistration() <= 49) {
			result = new Boolean(true);
		}
		return result;
	}
	
	public String getNameOfDoctor() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_DOCTOR), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setNameOfDoctor(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_DOCTOR), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NAME_OF_DOCTOR), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getLink() {
		return "/module/mdrtb/form/form89.form?patientProgramId=" + getPatProgId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public String getCountryOfOrigin() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setCountryOfOrigin(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPlaceOfCommission() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setPlaceOfCommission(Concept place) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && place == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(place)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (place != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(place);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCityOfOrigin() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY_OF_ORIGIN), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setCityOfOrigin(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY_OF_ORIGIN), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CITY_OF_ORIGIN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getOtherMethodOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setOtherMethodOfDetection(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getComplication() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.COMPLICATION), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setComplication(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.COMPLICATION), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && name == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COMPLICATION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getDateOfReturn() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_RETURN), encounter);
		return obs == null ? null : obs.getValueDatetime();
	}
	
	public void setDateOfReturn(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_RETURN), encounter);
		
		// if this obs have not been created, and there is no data to add, do
		// nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has
		// changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs
			// when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_RETURN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getRegistrationNumber() {
		log.debug("Patient ID: " + this.getPatient().getPatientId());
		if (tb03 == null) {
			if (this.getPatProgId() == null) {
				return null;
			}
			
			else {
				initTB03(this.getPatProgId());
				if (tb03 != null) {
					return tb03.getRegistrationNumber();
				}
				
				else
					return null;
			}
		}
		
		return tb03.getRegistrationNumber();
	}
	
	public int compareTo(Form89 form) {
		if (this.getRegistrationNumber() == null)
			return 1;
		if (form.getRegistrationNumber() == null)
			return -1;
		
		return this.getRegistrationNumber().compareTo(form.getRegistrationNumber());
	}
	
	public Concept getPregnant() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREGNANT),
		    encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setPregnant(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREGNANT),
		    encounter);
		
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
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PREGNANT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
}
