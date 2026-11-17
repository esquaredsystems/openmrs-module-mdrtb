package org.openmrs.module.mdrtb.web.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm.FollowupXray;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;

/**
 * REST representation of {@link EndOfTreatmentForm}. The link to the patient's treatment flow is
 * carried as patientProgramUuid (stored on the encounter as the PATIENT_PROGRAM_ID obs).
 * Values inside the rows (Date, Concept) are converted by webservices.rest exactly like the
 * top-level fields, so dates and concepts look the same everywhere in the response.
 */
public class SimpleEndOfTreatmentForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 4319608452931487115L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private Concept dispensaryGroup;
	
	private List<SimpleObject> followupXrays = new ArrayList<>();
	
	private List<SimpleObject> followupLabTests = new ArrayList<>();
	
	private Date transferToGroup2Date;
	
	private Date transferToGroup3Date;
	
	private Concept transferToGroup3Reason;
	
	private Date removalFromObservationDate;
	
	private String removalFromObservationReason;
	
	public SimpleEndOfTreatmentForm() {
	}
	
	public SimpleEndOfTreatmentForm(EndOfTreatmentForm endOfTreatmentForm) {
		setEncounter(endOfTreatmentForm.getEncounter());
		setUuid(getEncounter().getUuid());
		Integer patientProgramId = endOfTreatmentForm.getPatientProgramId();
		if (patientProgramId != null) {
			PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			if (patientProgram != null) {
				setPatientProgramUuid(patientProgram.getUuid());
			}
		}
		setDispensaryGroup(endOfTreatmentForm.getDispensaryGroup());
		for (FollowupXray xray : endOfTreatmentForm.getFollowupXrays()) {
			SimpleObject row = new SimpleObject();
			row.add("monthOfTreatment", xray.getMonthOfTreatment());
			row.add("date", xray.getDate());
			row.add("result", xray.getResult());
			followupXrays.add(row);
		}
		// Same columns as the follow-up smears of the TB03 report (TB03ExportController)
		for (SmearForm smear : endOfTreatmentForm.getFollowupSmears()) {
			SimpleObject row = new SimpleObject();
			row.add("monthOfTreatment", smear.getMonthOfTreatment());
			row.add("date", smear.getEncounterDatetime());
			row.add("result", smear.getSmearResult());
			row.add("testNumber", smear.getSpecimenId());
			Location lab = smear.getLocation();
			row.add("lab", lab == null ? null : lab.getName());
			followupLabTests.add(row);
		}
		setTransferToGroup2Date(endOfTreatmentForm.getTransferToGroup2Date());
		setTransferToGroup3Date(endOfTreatmentForm.getTransferToGroup3Date());
		setTransferToGroup3Reason(endOfTreatmentForm.getTransferToGroup3Reason());
		setRemovalFromObservationDate(endOfTreatmentForm.getRemovalFromObservationDate());
		setRemovalFromObservationReason(endOfTreatmentForm.getRemovalFromObservationReason());
	}
	
	/**
	 * Provide {@link EndOfTreatmentForm} representation of {@link SimpleEndOfTreatmentForm}.
	 */
	public EndOfTreatmentForm toForm() {
		EndOfTreatmentForm endOfTreatmentForm = new EndOfTreatmentForm();
		endOfTreatmentForm.setEncounter(getEncounter());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		endOfTreatmentForm.setPatientProgramId(patientProgram.getPatientProgramId());
		endOfTreatmentForm.setDispensaryGroup(getDispensaryGroup());
		for (Map<String, Object> row : getFollowupXrays()) {
			Integer month = (Integer) ConversionUtil.convert(row.get("monthOfTreatment"), Integer.class);
			if (month == null) {
				continue;
			}
			Date date = (Date) ConversionUtil.convert(row.get("date"), Date.class);
			endOfTreatmentForm.setFollowupXray(month, date, toConcept(row.get("result")));
		}
		endOfTreatmentForm.setTransferToGroup2Date(getTransferToGroup2Date());
		endOfTreatmentForm.setTransferToGroup3Date(getTransferToGroup3Date());
		endOfTreatmentForm.setTransferToGroup3Reason(getTransferToGroup3Reason());
		endOfTreatmentForm.setRemovalFromObservationDate(getRemovalFromObservationDate());
		endOfTreatmentForm.setRemovalFromObservationReason(getRemovalFromObservationReason());
		return endOfTreatmentForm;
	}
	
	/** A row value may be a Concept (built here) or a uuid / {uuid: ...} (posted as JSON) */
	private static Concept toConcept(Object value) {
		if (value == null || value instanceof Concept) {
			return (Concept) value;
		}
		Object uuid = value instanceof Map ? ((Map<?, ?>) value).get("uuid") : value;
		return uuid == null ? null : Context.getConceptService().getConceptByUuid(uuid.toString());
	}
	
	@Override
	public Integer getId() {
		return encounter.getEncounterId();
	}
	
	@Override
	public void setId(Integer id) {
		// Nope! Not gonna happen
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public String getPatientProgramUuid() {
		return patientProgramUuid;
	}
	
	public void setPatientProgramUuid(String patientProgramUuid) {
		this.patientProgramUuid = patientProgramUuid;
	}
	
	public Concept getDispensaryGroup() {
		return dispensaryGroup;
	}
	
	public void setDispensaryGroup(Concept dispensaryGroup) {
		this.dispensaryGroup = dispensaryGroup;
	}
	
	public List<SimpleObject> getFollowupXrays() {
		return followupXrays;
	}
	
	public void setFollowupXrays(List<SimpleObject> followupXrays) {
		this.followupXrays = followupXrays == null ? new ArrayList<>() : followupXrays;
	}
	
	public List<SimpleObject> getFollowupLabTests() {
		return followupLabTests;
	}
	
	public void setFollowupLabTests(List<SimpleObject> followupLabTests) {
		this.followupLabTests = followupLabTests == null ? new ArrayList<>() : followupLabTests;
	}
	
	public Date getTransferToGroup2Date() {
		return transferToGroup2Date;
	}
	
	public void setTransferToGroup2Date(Date transferToGroup2Date) {
		this.transferToGroup2Date = transferToGroup2Date;
	}
	
	public Date getTransferToGroup3Date() {
		return transferToGroup3Date;
	}
	
	public void setTransferToGroup3Date(Date transferToGroup3Date) {
		this.transferToGroup3Date = transferToGroup3Date;
	}
	
	public Concept getTransferToGroup3Reason() {
		return transferToGroup3Reason;
	}
	
	public void setTransferToGroup3Reason(Concept transferToGroup3Reason) {
		this.transferToGroup3Reason = transferToGroup3Reason;
	}
	
	public Date getRemovalFromObservationDate() {
		return removalFromObservationDate;
	}
	
	public void setRemovalFromObservationDate(Date removalFromObservationDate) {
		this.removalFromObservationDate = removalFromObservationDate;
	}
	
	public String getRemovalFromObservationReason() {
		return removalFromObservationReason;
	}
	
	public void setRemovalFromObservationReason(String removalFromObservationReason) {
		this.removalFromObservationReason = removalFromObservationReason;
	}
}
