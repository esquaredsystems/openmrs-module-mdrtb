package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.Context;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.reporting.custom.EndOfTreatmentData;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;

/**
 * End of Treatment report: the back page of the TB03 (DOTS) and TB03u (MDR-TB) register.
 * The patients are the same as in the TB03 report (TB03ExportController) or the TB03u report (TB03uController)
 */
public class EndOfTreatmentController {
	
	/** Rows for the TB03 (DOTS) register patients, sorted by registration number */
	public static List<EndOfTreatmentData> getDotsEndOfTreatmentPatientSet(Integer year, Integer quarter, Integer month,
	        Integer month2, List<Location> locList) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month,
		    month2);
		List<EndOfTreatmentData> patientSet = new ArrayList<>();
		for (TB03Form tf : tb03List) {
			Patient patient = tf.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			EndOfTreatmentData row = newRow(patient, tf.getPatientProgramId(), tf.getEncounterDatetime(),
			    tf.getLocation(), tf.getTreatmentOutcome(), tf.getTreatmentOutcomeDate());
			row.setIdentifier(TB03Util.getRegistrationNumber(tf));
			patientSet.add(row);
		}
		Collections.sort(patientSet);
		return patientSet;
	}
	
	/** Rows for the TB03u (MDR-TB) register patients, sorted by registration number */
	public static List<EndOfTreatmentData> getMdrEndOfTreatmentPatientSet(Integer year, Integer quarter, Integer month,
	        Integer month2, List<Location> locList) {
		List<TB03uForm> tb03uList = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month, month2);
		List<EndOfTreatmentData> patientSet = new ArrayList<>();
		for (TB03uForm tf : tb03uList) {
			Patient patient = tf.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
			}
			EndOfTreatmentData row = newRow(patient, tf.getPatientProgramId(), tf.getEncounterDatetime(),
			    tf.getLocation(), tf.getTreatmentOutcome(), tf.getTreatmentOutcomeDate());
			row.setIdentifier(TB03Util.getRegistrationNumber(tf));
			patientSet.add(row);
		}
		Collections.sort(patientSet);
		return patientSet;
	}
	
	/**
	 * One report row: the patient columns from the TB03 / TB03u form, and the End of Treatment
	 * columns from the End of Treatment form of the same patient program (if any)
	 */
	private static EndOfTreatmentData newRow(Patient patient, Integer patientProgramId, Date registrationDate,
	        Location registrationLocation, Concept treatmentOutcome, Date treatmentOutcomeDate) {
		SimpleDateFormat sdf = Context.getDateFormat();
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.setPatient(patient);
		if (registrationDate != null) {
			row.setRegistrationDate(sdf.format(registrationDate));
		}
		row.setRegistrationLocation(registrationLocation);
		if (patient.getBirthdate() != null) {
			row.setDateOfBirth(sdf.format(patient.getBirthdate()));
		}
		if (treatmentOutcome != null) {
			row.setTreatmentOutcome(treatmentOutcome.getName(Context.getLocale()).getName());
		}
		if (treatmentOutcomeDate != null) {
			row.setTreatmentOutcomeDate(sdf.format(treatmentOutcomeDate));
		}
		// Only one End of Treatment form is allowed per patient program
		// (MdrtbFormServiceImpl.processEndOfTreatmentForm)
		EndOfTreatmentForm endOfTreatmentForm = null;
		if (patientProgramId != null) {
			List<EndOfTreatmentForm> forms = Context.getService(MdrtbService.class).getEndOfTreatmentFormsForProgram(
			    patient, patientProgramId);
			if (!forms.isEmpty()) {
				endOfTreatmentForm = forms.get(0);
			}
		}
		row.fillEndOfTreatmentColumns(endOfTreatmentForm);
		return row;
	}
}
