package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.mdrtb.lab.LabTest;

/**
 * Read-only summary of one {@link PatientProgram}: the program itself, plus the observations and
 * lab tests that fall inside the program's enrollment window. Built by
 * {@code MdrtbService.getSinglePatientSummary(Patient)} and consumed by the REST layer.
 */
public class PatientProgramSummary {
	
	private PatientProgram patientProgram;
	
	private List<Obs> observations = new ArrayList<Obs>();
	
	private List<LabTest> labTests = new ArrayList<LabTest>();
	
	public PatientProgramSummary() {
	}
	
	public PatientProgramSummary(PatientProgram patientProgram, List<Obs> observations, List<LabTest> labTests) {
		this.patientProgram = patientProgram;
		if (observations != null) {
			this.observations = observations;
		}
		if (labTests != null) {
			this.labTests = labTests;
		}
	}
	
	public PatientProgram getPatientProgram() {
		return patientProgram;
	}
	
	public void setPatientProgram(PatientProgram patientProgram) {
		this.patientProgram = patientProgram;
	}
	
	public List<Obs> getObservations() {
		return observations;
	}
	
	public void setObservations(List<Obs> observations) {
		this.observations = observations;
	}
	
	public List<LabTest> getLabTests() {
		return labTests;
	}
	
	public void setLabTests(List<LabTest> labTests) {
		this.labTests = labTests;
	}
	
	/**
	 * The treatment-outcome observation for this program, or {@code null} if none was recorded. The
	 * outcome is the observation whose question concept matches the program's configured outcomes
	 * concept ({@code program.getOutcomesConcept()}); its value holds the specific outcome. Derived
	 * from the {@link #observations} already scoped to this program's window.
	 */
	public Obs getTreatmentOutcome() {
		if (patientProgram == null || patientProgram.getProgram() == null) {
			return null;
		}
		Concept outcomesConcept = patientProgram.getProgram().getOutcomesConcept();
		if (outcomesConcept == null) {
			return null;
		}
		for (Obs obs : observations) {
			if (obs.getConcept() != null && outcomesConcept.equals(obs.getConcept())) {
				return obs;
			}
		}
		return null;
	}
}
