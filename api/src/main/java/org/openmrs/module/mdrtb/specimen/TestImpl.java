package org.openmrs.module.mdrtb.specimen;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.commonlabtest.LabTestSample;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;

// TODO: See where AccessionNumber and LabLocation are used
public abstract class TestImpl implements Test {
	
	// protected Obs test; // the top-level obs that holds all the data for this smear
	protected LabTest test; // the top-level obs that holds all the data for this test
	
	/** implementing subclasses must override this method to define their type */
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
		Encounter encounter = this.test.getOrder().getEncounter();
		return encounter == null ? null : encounter.getEncounterId().toString();
	}
	
	public Date getDateCollected() {
		try {
			LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
			return testSample.getCollectionDate();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	// unfortunately we have to implement this separately for each test type since
	// they handle comments differently
	abstract public String getComments();
	
	public Date getDateOrdered() {
		if (test.getOrder().getEffectiveStartDate() != null) {
			return test.getOrder().getEffectiveStartDate();
		}
		return test.getOrder().getDateCreated();
		// Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), test);
		// return (obs == null) ? null : obs.getValueDatetime();
	}
	
	public Date getDateReceived() {
		return test.getOrder().getDateActivated();
		// Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_RECEIVED), test);		
		// return (obs == null) ? null : obs.getValueDatetime();
	}
	
	public Location getLab() {
		// return test.getLocation();
		return test.getOrder().getEncounter().getLocation();
	}
	
	public Date getResultDate() {
		// Get Sample processed date
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		if (testSample != null) {
			return testSample.getProcessedDate();
		}
		return null;
		// Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_RESULT_DATE), test);
		// return (obs == null) ? null : obs.getValueDatetime();
	}
	
	public Date getStartDate() {
		// Get Sample processed date
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		return testSample.getProcessedDate();
		// Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), test);
		// return (obs == null) ? null : obs.getValueDatetime();
	}
	
	// unfortunately we have to implement this separately for each test type since
	// they handle comments differently
	abstract public void setComments(String comments);
	
	public void setDateOrdered(Date dateOrdered) {
		test.getOrder().setDateCreated(dateOrdered);
		// Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), test);
	}
	
	public void setDateReceived(Date dateReceived) {
		LabTestAttribute attribute = CommonLabUtil.getService().getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.TEST_DATE_RECEIVED);
		attribute.setValue(dateReceived);
		/*
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
		*/
	}
	
	public void setLab(Location location) {
		test.getOrder().getEncounter().setLocation(location);
		/*
		test.setLocation(location);
		// also propagate this location to the all the child obs
		if (test.getGroupMembers() != null) {
			for (Obs obs : test.getGroupMembers()) {
				if (!obs.getVoided()) {
					obs.setLocation(location);
				}
			}
		}
		*/
	}
	
	public void setResultDate(Date resultDate) {
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		testSample.setProcessedDate(resultDate);
		/*
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
		*/
	}
	
	public void setStartDate(Date startDate) {
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		testSample.setProcessedDate(startDate);
		/*
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
		*/
	}
	
	public String getErrorCode() {
		LabTestAttribute attribute = CommonLabUtil.getService().getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.ERROR_CODE);
		return attribute.getValueReference();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE), test);
		return (obs == null || obs.getValueText() == null) ? null : obs.getValueText();
		*/
	}
	
	public void setErrorCode(String code) {
		LabTestAttribute attribute = CommonLabUtil.getService().getXpertAttributeByTestAndName(test,
		    MdrtbConcepts.ERROR_CODE);
		attribute.setValue(code);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE),
		    test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && code == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (code == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE),
			        test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now set the value
		obs.setValueText(code);
		*/
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
		
		/*
		if (recent0 == null) {
			// use the actual obs datetime if there are no value dates
			recent0 = this.getObs().getObsDatetime();
		}
		if (recent1 == null) {
			recent1 = this.getObs().getObsDatetime();
		}
		*/
		
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
		return null;
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
	
	public String getAccessionNumber() {
		return null;
	}
	
	public void setAccessionNumber(String accessionNumber) {
	}
	
	public String getRealSpecimenId() {
		return null;
		/*
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
		 */
	}
}
