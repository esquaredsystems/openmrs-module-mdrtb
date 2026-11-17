package org.openmrs.module.mdrtb.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.module.mdrtb.reporting.custom.EndOfTreatmentData;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * One row of the End of Treatment report (REST representation of {@link EndOfTreatmentData})
 */
public class SimpleEndOfTreatmentData extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String identifier;
	
	private PersonName personName;
	
	private String gender;
	
	private PersonAddress personAddress;
	
	private String dateOfBirth;
	
	private String registrationDate;
	
	private String registrationFacility;
	
	private String treatmentOutcome;
	
	private String treatmentOutcomeDate;
	
	private String endOfTreatmentUuid;
	
	private String dispensaryGroup;
	
	private List<SimpleObject> followupXrays = new ArrayList<>();
	
	private List<SimpleObject> followupLabTests = new ArrayList<>();
	
	private String transferToGroup2Date;
	
	private String transferToGroup3Date;
	
	private String transferToGroup3Reason;
	
	private String removalFromObservationDate;
	
	private String removalFromObservationReason;
	
	public SimpleEndOfTreatmentData() {
	}
	
	public SimpleEndOfTreatmentData(EndOfTreatmentData data) {
		patientUuid = data.getPatient().getUuid();
		personName = data.getPatient().getPersonName();
		personAddress = data.getPatient().getPersonAddress();
		gender = data.getGender();
		identifier = data.getIdentifier();
		dateOfBirth = data.getDateOfBirth();
		registrationDate = data.getRegistrationDate();
		if (data.getRegistrationLocation() != null) {
			registrationFacility = data.getRegistrationLocation().getName();
		}
		treatmentOutcome = data.getTreatmentOutcome();
		treatmentOutcomeDate = data.getTreatmentOutcomeDate();
		if (data.getEndOfTreatmentForm() != null) {
			endOfTreatmentUuid = data.getEndOfTreatmentForm().getEncounter().getUuid();
		}
		dispensaryGroup = data.getDispensaryGroup();
		for (Map<String, Object> xray : data.getFollowupXrays()) {
			SimpleObject row = new SimpleObject();
			row.putAll(xray);
			followupXrays.add(row);
		}
		for (Map<String, Object> labTest : data.getFollowupLabTests()) {
			SimpleObject row = new SimpleObject();
			row.putAll(labTest);
			followupLabTests.add(row);
		}
		transferToGroup2Date = data.getTransferToGroup2Date();
		transferToGroup3Date = data.getTransferToGroup3Date();
		transferToGroup3Reason = data.getTransferToGroup3Reason();
		removalFromObservationDate = data.getRemovalFromObservationDate();
		removalFromObservationReason = data.getRemovalFromObservationReason();
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer id) {
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public PersonAddress getPersonAddress() {
		return personAddress;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getRegistrationDate() {
		return registrationDate;
	}
	
	public String getRegistrationFacility() {
		return registrationFacility;
	}
	
	public String getTreatmentOutcome() {
		return treatmentOutcome;
	}
	
	public String getTreatmentOutcomeDate() {
		return treatmentOutcomeDate;
	}
	
	public String getEndOfTreatmentUuid() {
		return endOfTreatmentUuid;
	}
	
	public String getDispensaryGroup() {
		return dispensaryGroup;
	}
	
	public List<SimpleObject> getFollowupXrays() {
		return followupXrays;
	}
	
	public List<SimpleObject> getFollowupLabTests() {
		return followupLabTests;
	}
	
	public String getTransferToGroup2Date() {
		return transferToGroup2Date;
	}
	
	public String getTransferToGroup3Date() {
		return transferToGroup3Date;
	}
	
	public String getTransferToGroup3Reason() {
		return transferToGroup3Reason;
	}
	
	public String getRemovalFromObservationDate() {
		return removalFromObservationDate;
	}
	
	public String getRemovalFromObservationReason() {
		return removalFromObservationReason;
	}
}
