package org.openmrs.module.mdrtb.reporting.custom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm.FollowupXray;
import org.openmrs.module.mdrtb.form.custom.SmearForm;

/**
 * One row of the End of Treatment report: one TB03 (DOTS) or TB03u (MDR-TB) register patient, and
 * the End of Treatment form of that patient program when one is filled. Same role as TB03Data.
 * <p>
 * The patient columns are filled by EndOfTreatmentController (omod). The End of Treatment columns
 * are filled by {@link #fillEndOfTreatmentColumns(EndOfTreatmentForm)} and stay empty when the
 * program has no End of Treatment form yet. Dates are formatted with Context.getDateFormat() and
 * concepts are shown by their name in Context.getLocale(), as in TB03Data.
 */
public class EndOfTreatmentData implements Comparable<EndOfTreatmentData> {
	
	private Patient patient;
	
	private EndOfTreatmentForm endOfTreatmentForm;
	
	/** Registration number of the patient program (TB03 or TB03u), used for sorting */
	private String identifier;
	
	/** Date of the TB03 / TB03u form */
	private String registrationDate;
	
	/** Location of the TB03 / TB03u form */
	private Location registrationLocation;
	
	private String dateOfBirth;
	
	/** Treatment outcome on the TB03 / TB03u */
	private String treatmentOutcome;
	
	/** Treatment outcome date on the TB03 / TB03u, i.e. the end of treatment */
	private String treatmentOutcomeDate;
	
	// End of Treatment columns (empty when the program has no End of Treatment form)
	
	private String dispensaryGroup;
	
	/**
	 * All follow-up X-rays, ordered by month of treatment. Each row: monthOfTreatment (Integer), date
	 * (String), result (String)
	 */
	private List<Map<String, Object>> followupXrays = new ArrayList<>();
	
	/**
	 * Follow-up smear results of months 1, 3 and 5 (EndOfTreatmentForm.FOLLOWUP_SMEAR_MONTHS). Each
	 * row: monthOfTreatment (Integer), date (String), result (String), testNumber (String), lab
	 * (String)
	 */
	private List<Map<String, Object>> followupLabTests = new ArrayList<>();
	
	private String transferToGroup2Date;
	
	private String transferToGroup3Date;
	
	private String transferToGroup3Reason;
	
	private String removalFromObservationDate;
	
	private String removalFromObservationReason;
	
	/**
	 * Copies the End of Treatment columns of this row from the form. Does nothing when the form is
	 * null (the program has no End of Treatment form yet).
	 */
	public void fillEndOfTreatmentColumns(EndOfTreatmentForm form) {
		this.endOfTreatmentForm = form;
		if (form == null) {
			return;
		}
		SimpleDateFormat sdf = Context.getDateFormat();
		dispensaryGroup = conceptName(form.getDispensaryGroup());
		followupXrays = new ArrayList<>();
		for (FollowupXray xray : form.getFollowupXrays()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("monthOfTreatment", xray.getMonthOfTreatment());
			row.put("date", formatDate(sdf, xray.getDate()));
			row.put("result", conceptName(xray.getResult()));
			followupXrays.add(row);
		}
		// Same columns as the follow-up smears of the TB03 report
		followupLabTests = new ArrayList<>();
		for (SmearForm smear : form.getFollowupSmears()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("monthOfTreatment", smear.getMonthOfTreatment());
			row.put("date", formatDate(sdf, smear.getEncounterDatetime()));
			row.put("result", conceptName(smear.getSmearResult()));
			row.put("testNumber", smear.getSpecimenId());
			Location lab = smear.getLocation();
			row.put("lab", lab == null ? null : lab.getName());
			followupLabTests.add(row);
		}
		transferToGroup2Date = formatDate(sdf, form.getTransferToGroup2Date());
		transferToGroup3Date = formatDate(sdf, form.getTransferToGroup3Date());
		transferToGroup3Reason = conceptName(form.getTransferToGroup3Reason());
		removalFromObservationDate = formatDate(sdf, form.getRemovalFromObservationDate());
		removalFromObservationReason = form.getRemovalFromObservationReason();
	}
	
	private static String formatDate(SimpleDateFormat sdf, Date date) {
		return date == null ? null : sdf.format(date);
	}
	
	private static String conceptName(Concept concept) {
		return concept == null ? null : concept.getName(Context.getLocale()).getName();
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	/** "M", "F", or "" when no gender is recorded; translated by the client, as in TB03Data */
	public String getGender() {
		if (patient == null || patient.getGender() == null) {
			return "";
		}
		return patient.getGender();
	}
	
	public EndOfTreatmentForm getEndOfTreatmentForm() {
		return endOfTreatmentForm;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	public Location getRegistrationLocation() {
		return registrationLocation;
	}
	
	public void setRegistrationLocation(Location registrationLocation) {
		this.registrationLocation = registrationLocation;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getTreatmentOutcome() {
		return treatmentOutcome;
	}
	
	public void setTreatmentOutcome(String treatmentOutcome) {
		this.treatmentOutcome = treatmentOutcome;
	}
	
	public String getTreatmentOutcomeDate() {
		return treatmentOutcomeDate;
	}
	
	public void setTreatmentOutcomeDate(String treatmentOutcomeDate) {
		this.treatmentOutcomeDate = treatmentOutcomeDate;
	}
	
	public String getDispensaryGroup() {
		return dispensaryGroup;
	}
	
	public List<Map<String, Object>> getFollowupXrays() {
		return followupXrays;
	}
	
	public List<Map<String, Object>> getFollowupLabTests() {
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
	
	@Override
	public int compareTo(EndOfTreatmentData o) {
		if (o.getIdentifier() == null || getIdentifier() == null)
			return 0;
		return identifier.compareTo(o.getIdentifier());
	}
}
