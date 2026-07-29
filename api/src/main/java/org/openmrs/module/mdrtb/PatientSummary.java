package org.openmrs.module.mdrtb;

import org.openmrs.*;
import java.util.List;

public class PatientSummary {
	
	private Patient patient;
	
	private PersonName personName;
	
	private PersonAddress personAddress;
	
	private List<PatientIdentifier> patientIdentifiers;
	
	private List<PatientProgramSummary> patientProgramSummaries;
	
	public PatientSummary(Patient patient, PersonName personName, PersonAddress personAddress,
	    List<PatientIdentifier> patientIdentifiers, List<PatientProgramSummary> patientProgramSummaries) {
		this.patient = patient;
		this.personName = personName;
		this.personAddress = personAddress;
		this.patientIdentifiers = patientIdentifiers;
		this.patientProgramSummaries = patientProgramSummaries;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public PersonName getPersonName() {
		return personName;
	}
	
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}
	
	public PersonAddress getPersonAddress() {
		return personAddress;
	}
	
	public void setPersonAddress(PersonAddress personAddress) {
		this.personAddress = personAddress;
	}
	
	public List<PatientIdentifier> getPatientIdentifiers() {
		return patientIdentifiers;
	}
	
	public void setPatientIdentifiers(List<PatientIdentifier> patientIdentifiers) {
		this.patientIdentifiers = patientIdentifiers;
	}
	
	public List<PatientProgramSummary> getPatientProgramSummaries() {
		return patientProgramSummaries;
	}
	
	public void setPatientProgramSummaries(List<PatientProgramSummary> patientProgramSummaries) {
		this.patientProgramSummaries = patientProgramSummaries;
	}
}
