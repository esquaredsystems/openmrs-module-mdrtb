package org.openmrs.module.mdrtb.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;

public abstract class TestImpl implements Test {
	
	protected Obs test; // the top-level obs that holds all the data for this smear
	
	/** implementing subclasses must override this method to define their type */
	public abstract String getTestType();
	
	public Object getTest() {
		return this.test;
	}
	
	public String getId() {
		if (this.test.getId() != null) {
			return this.test.getId().toString();
		} else {
			return null;
		}
	}
	
	public TestStatus getStatus() {
		return calculateStatus();
	}
	
	public String getSpecimenId() {
		Encounter encounter = this.test.getEncounter();
		return encounter == null ? null : encounter.getEncounterId().toString();
	}
	
	public String getAccessionNumber() {
		return this.test.getAccessionNumber();
	}
	
	public Date getDateCollected() {
		return this.test.getObsDatetime();
	}
	
	// unfortunately we have to implement this separately for each test type since
	// they handle comments differently
	abstract public String getComments();
	
	public Date getDateOrdered() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), test);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public Date getDateReceived() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_RECEIVED), test);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public Location getLab() {
		return test.getLocation();
	}
	
	public Date getResultDate() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_RESULT_DATE), test);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public Date getStartDate() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), test);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setAccessionNumber(String accessionNumber) {
		test.setAccessionNumber(accessionNumber);
		
		// also propagate this location to the all the child obs
		if (test.getGroupMembers() != null) {
			for (Obs obs : test.getGroupMembers()) {
				if (!obs.getVoided()) {
					obs.setAccessionNumber(accessionNumber);
				}
			}
		}
	}
	
	// unfortunately we have to implement this separately for each test type since
	// they handle comments differently
	abstract public void setComments(String comments);
	
	public void setDateOrdered(Date dateOrdered) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateOrdered == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (dateOrdered == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {
			// now create the new Obs and add it to the encounter
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.TEST_DATE_ORDERED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// update the value
		obs.setValueDatetime(dateOrdered);
	}
	
	public void setDateReceived(Date dateReceived) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_RECEIVED), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateReceived == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (dateReceived == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.TEST_DATE_RECEIVED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueDatetime(dateReceived);
	}
	
	public void setLab(Location location) {
		test.setLocation(location);
		
		// also propagate this location to the all the child obs
		if (test.getGroupMembers() != null) {
			for (Obs obs : test.getGroupMembers()) {
				if (!obs.getVoided()) {
					obs.setLocation(location);
				}
			}
		}
	}
	
	public void setResultDate(Date resultDate) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_RESULT_DATE), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resultDate == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (resultDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.TEST_RESULT_DATE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueDatetime(resultDate);
	}
	
	public void setStartDate(Date startDate) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), test);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && startDate == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (startDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(),
			        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), test.getObsDatetime(),
			        test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueDatetime(startDate);
	}
	
	/**
	 * Utility method for copying a test
	 */
	public void copyMembersFrom(Test source) {
		this.setAccessionNumber(source.getAccessionNumber());
		this.setDateOrdered(source.getDateOrdered());
		this.setDateReceived(source.getDateReceived());
		this.setLab(source.getLab());
		this.setResultDate(source.getResultDate());
		this.setStartDate(source.getStartDate());
		this.setComments(source.getComments());
	}
	
	/**
	 * Comparable interface method and utility methods
	 */
	
	public int compareTo(Test test1) {
		Date recent0 = oldestDate(this);
		Date recent1 = oldestDate(test1);
		
		if (recent0 == null) {
			// use the actual obs datetime if there are no value dates
			recent0 = this.getObs().getObsDatetime();
		}
		if (recent1 == null) {
			recent1 = this.getObs().getObsDatetime();
		}
		
		return recent0.compareTo(recent1);
	}
	
	// checks all the dates properties associated with the mdrtb test and returns
	// the date that is most recent
	private Date oldestDate(Test test) {
		Date oldest = null;
		
		if (test.getDateOrdered() != null) {
			if (oldest == null || test.getDateOrdered().before(oldest)) {
				oldest = test.getDateOrdered();
			}
		}
		if (test.getDateReceived() != null) {
			if (oldest == null || test.getDateReceived().before(oldest)) {
				oldest = test.getDateReceived();
			}
		}
		if (test.getResultDate() != null) {
			if (oldest == null || test.getResultDate().before(oldest)) {
				oldest = test.getResultDate();
			}
		}
		if (test.getStartDate() != null) {
			if (oldest == null || test.getStartDate().before(oldest)) {
				oldest = test.getStartDate();
			}
		}
		
		return oldest;
	}
	
	/**
	 * Protected methods used for interacting with the matching MdrSpecimenImpl
	 */
	
	protected Obs getObs() {
		return test;
	}
	
	/**
	 * Utility methods
	 */
	
	/**
	 * Determines the current status of this test by examines the values of the various date fields
	 * Auto generated method comment
	 * 
	 * @return
	 */
	private TestStatus calculateStatus() {
		
		if (getResultDate() != null) {
			return TestStatus.COMPLETED;
		} else if (getStartDate() != null) {
			return TestStatus.STARTED;
		} else if (getDateReceived() != null) {
			return TestStatus.RECEIVED;
		} else if (getDateOrdered() != null) {
			return TestStatus.ORDERED;
		} else {
			return TestStatus.UNKNOWN;
		}
	}
	
	public String getRealSpecimenId() {
		
		Encounter enc = this.getObs().getEncounter();
		ArrayList<Encounter> encounters = new ArrayList<Encounter>();
		encounters.add(enc);
		Concept question = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID);
		ArrayList<Concept> questions = new ArrayList<Concept>();
		questions.add(question);
		
		List<Obs> obsList = Context.getObsService().getObservations(null, encounters, questions, null, null, null, null,
		    null, null, null, null, false);
		
		if (obsList == null || obsList.size() == 0)
			return null;
		
		Obs obs = obsList.get(0);
		String specimenId = obs.getValueText();
		
		return specimenId;
	}
}
