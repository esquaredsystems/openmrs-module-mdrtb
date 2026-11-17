package org.openmrs.module.mdrtb.form.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;

/**
 * End of Treatment form. Filled once per patient program (DOTS or MDR-TB), after treatment, and
 * shown on the program dashboard. It records the dispensary observation of the patient: the
 * dispensary group, follow-up X-rays (one per month of treatment), transfers to groups II and III,
 * and removal from observation. It belongs to one patient program, linked through the
 * PATIENT_PROGRAM_ID obs (the REST layer exposes it as patientProgramUuid). A TB03 form (DOTS
 * program) or TB03u form (MDR-TB program) must already exist for that program.
 */
public class EndOfTreatmentForm extends AbstractSimpleForm {
	
	private static final String VOID_REASON = "voided by MDRTB module";
	
	/** Months of treatment on which follow-up smear microscopy is done (as agreed 2026-11-17) */
	public static final List<Integer> FOLLOWUP_SMEAR_MONTHS = Collections.unmodifiableList(Arrays.asList(1, 3, 5));
	
	public EndOfTreatmentForm() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_END_OF_TREATMENT);
	}
	
	public EndOfTreatmentForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_END_OF_TREATMENT);
	}
	
	public EndOfTreatmentForm(Encounter encounter) {
		super(encounter);
	}
	
	/**
	 * One follow-up X-ray, read from one obs group of this form (FOLLOWUP_XRAY_CONSTRUCT).
	 */
	public static class FollowupXray {
		
		private final Integer monthOfTreatment;
		
		private final Date date;
		
		private final Concept result;
		
		public FollowupXray(Integer monthOfTreatment, Date date, Concept result) {
			this.monthOfTreatment = monthOfTreatment;
			this.date = date;
			this.result = result;
		}
		
		/** The month recorded for this X-ray (stored under the existing MONTH OF TREATMENT concept) */
		public Integer getMonthOfTreatment() {
			return monthOfTreatment;
		}
		
		public Date getDate() {
			return date;
		}
		
		/** XRAY_NORMAL, XRAY_ABNORMAL or XRAY_INCONCLUSIVE */
		public Concept getResult() {
			return result;
		}
	}
	
	public Integer getPatientProgramId() {
		Concept concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		return obs == null || obs.getValueNumeric() == null ? null : obs.getValueNumeric().intValue();
	}
	
	public void setPatientProgramId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || id == null || obs.getValueNumeric().intValue() != id) {
			// void the existing obs if it exists, because openmrs doesn't void obs when saved via encounters
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_REASON);
			}
			// now create the new Obs and add it to the encounter
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(id.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	/** Ia, Ib, Ic or Id (DISPENSARY_GROUP_IA ... DISPENSARY_GROUP_ID) */
	public Concept getDispensaryGroup() {
		Obs obs = findObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setDispensaryGroup(Concept group) {
		Concept question = conceptForWrite(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP, "DISPENSARY_REGISTRATION_GROUP",
		    group != null);
		Obs obs = findObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && group == null) {
			return;
		}
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || !Objects.equals(obs.getValueCoded(), group)) {
			voidObs(obs);
			if (group != null) {
				obs = newObs(question);
				obs.setValueCoded(group);
				encounter.addObs(obs);
			}
		}
	}
	
	/**
	 * All follow-up X-rays recorded on this form, ordered by month of treatment (a group without a
	 * month comes last). Empty when none is recorded or the X-ray concepts are missing.
	 */
	public List<FollowupXray> getFollowupXrays() {
		List<FollowupXray> xrays = new ArrayList<>();
		MdrtbService service = Context.getService(MdrtbService.class);
		Concept construct = service.getConcept(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT);
		Concept monthConcept = service.getConcept(MdrtbConcepts.MONTH_OF_TREATMENT);
		Concept dateConcept = service.getConcept(MdrtbConcepts.XRAY_DATE);
		Concept resultConcept = service.getConcept(MdrtbConcepts.FOLLOWUP_XRAY_RESULT);
		if (construct == null || monthConcept == null || dateConcept == null || resultConcept == null) {
			return xrays;
		}
		for (Obs group : MdrtbUtil.getObsListFromEncounter(construct, encounter)) {
			Obs monthObs = MdrtbUtil.getObsFromObsGroup(monthConcept, group);
			Obs dateObs = MdrtbUtil.getObsFromObsGroup(dateConcept, group);
			Obs resultObs = MdrtbUtil.getObsFromObsGroup(resultConcept, group);
			Integer month = monthObs == null || monthObs.getValueNumeric() == null ? null : monthObs.getValueNumeric()
			        .intValue();
			xrays.add(new FollowupXray(month, dateObs == null ? null : dateObs.getValueDatetime(), resultObs == null ? null
			        : resultObs.getValueCoded()));
		}
		xrays.sort(Comparator.comparing(FollowupXray::getMonthOfTreatment, Comparator.nullsLast(Comparator
		        .naturalOrder())));
		return xrays;
	}
	
	/**
	 * The follow-up X-ray of this month of treatment, or null when it is not recorded.
	 */
	public FollowupXray getFollowupXray(int monthOfTreatment) {
		for (FollowupXray xray : getFollowupXrays()) {
			if (xray.getMonthOfTreatment() != null && xray.getMonthOfTreatment() == monthOfTreatment) {
				return xray;
			}
		}
		return null;
	}
	
	/**
	 * Records (or changes) the follow-up X-ray of this month of treatment; there is at most one
	 * X-ray per month and no limit on how many months. When both date and result are null, the
	 * X-ray is removed.
	 */
	public void setFollowupXray(int monthOfTreatment, Date date, Concept result) {
		if (monthOfTreatment < 0) {
			throw new MdrtbAPIException("Month of treatment of an X-ray cannot be negative: " + monthOfTreatment);
		}
		boolean hasValue = date != null || result != null;
		Concept construct = conceptForWrite(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT, "FOLLOWUP_XRAY_CONSTRUCT", hasValue);
		Concept monthConcept = conceptForWrite(MdrtbConcepts.MONTH_OF_TREATMENT, "MONTH_OF_TREATMENT", hasValue);
		Concept dateConcept = conceptForWrite(MdrtbConcepts.XRAY_DATE, "XRAY_DATE", hasValue);
		Concept resultConcept = conceptForWrite(MdrtbConcepts.FOLLOWUP_XRAY_RESULT, "FOLLOWUP_XRAY_RESULT", hasValue);
		if (construct == null || monthConcept == null || dateConcept == null || resultConcept == null) {
			// concepts missing and nothing to store: nothing can exist either
			return;
		}
		// find the existing group of this month
		Obs existing = null;
		for (Obs group : MdrtbUtil.getObsListFromEncounter(construct, encounter)) {
			Obs monthObs = MdrtbUtil.getObsFromObsGroup(monthConcept, group);
			if (monthObs != null && monthObs.getValueNumeric() != null
			        && monthObs.getValueNumeric().intValue() == monthOfTreatment) {
				existing = group;
				break;
			}
		}
		if (existing == null && !hasValue) {
			return;
		}
		if (existing != null) {
			Obs dateObs = MdrtbUtil.getObsFromObsGroup(dateConcept, existing);
			Obs resultObs = MdrtbUtil.getObsFromObsGroup(resultConcept, existing);
			Date oldDate = dateObs == null ? null : dateObs.getValueDatetime();
			Concept oldResult = resultObs == null ? null : resultObs.getValueCoded();
			if (sameDate(oldDate, date) && Objects.equals(oldResult, result)) {
				return; // unchanged
			}
			voidObs(existing);
		}
		if (hasValue) {
			Obs group = newObs(construct);
			encounter.addObs(group);
			Obs monthObs = newObs(monthConcept);
			monthObs.setValueNumeric((double) monthOfTreatment);
			addGroupMember(group, monthObs);
			if (date != null) {
				Obs dateObs = newObs(dateConcept);
				dateObs.setValueDatetime(date);
				addGroupMember(group, dateObs);
			}
			if (result != null) {
				Obs resultObs = newObs(resultConcept);
				resultObs.setValueCoded(result);
				addGroupMember(group, resultObs);
			}
		}
	}
	
	public Date getTransferToGroup2Date() {
		return getDate(MdrtbConcepts.DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_2);
	}
	
	public void setTransferToGroup2Date(Date date) {
		setDate(MdrtbConcepts.DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_2, "DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_2", date);
	}
	
	public Date getTransferToGroup3Date() {
		return getDate(MdrtbConcepts.DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_3);
	}
	
	public void setTransferToGroup3Date(Date date) {
		setDate(MdrtbConcepts.DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_3, "DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_3", date);
	}
	
	/** MAJOR_RESIDUAL_CHANGES, DISABILITY or TB_HIV (one answer) */
	public Concept getTransferToGroup3Reason() {
		Obs obs = findObs(MdrtbConcepts.REASON_FOR_TRANSFER_TO_DISPENSARY_GROUP_3);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setTransferToGroup3Reason(Concept reason) {
		Concept question = conceptForWrite(MdrtbConcepts.REASON_FOR_TRANSFER_TO_DISPENSARY_GROUP_3,
		    "REASON_FOR_TRANSFER_TO_DISPENSARY_GROUP_3", reason != null);
		Obs obs = findObs(MdrtbConcepts.REASON_FOR_TRANSFER_TO_DISPENSARY_GROUP_3);
		if (obs == null && reason == null) {
			return;
		}
		if (obs == null || !Objects.equals(obs.getValueCoded(), reason)) {
			voidObs(obs);
			if (reason != null) {
				obs = newObs(question);
				obs.setValueCoded(reason);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getRemovalFromObservationDate() {
		return getDate(MdrtbConcepts.DATE_OF_REMOVAL_FROM_DISPENSARY_OBSERVATION);
	}
	
	public void setRemovalFromObservationDate(Date date) {
		setDate(MdrtbConcepts.DATE_OF_REMOVAL_FROM_DISPENSARY_OBSERVATION, "DATE_OF_REMOVAL_FROM_DISPENSARY_OBSERVATION",
		    date);
	}
	
	/** Free text */
	public String getRemovalFromObservationReason() {
		Obs obs = findObs(MdrtbConcepts.REASON_FOR_REMOVAL_FROM_DISPENSARY_OBSERVATION);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setRemovalFromObservationReason(String reason) {
		if (reason != null && reason.trim().isEmpty()) {
			reason = null;
		}
		Concept question = conceptForWrite(MdrtbConcepts.REASON_FOR_REMOVAL_FROM_DISPENSARY_OBSERVATION,
		    "REASON_FOR_REMOVAL_FROM_DISPENSARY_OBSERVATION", reason != null);
		Obs obs = findObs(MdrtbConcepts.REASON_FOR_REMOVAL_FROM_DISPENSARY_OBSERVATION);
		if (obs == null && reason == null) {
			return;
		}
		if (obs == null || !Objects.equals(obs.getValueText(), reason)) {
			voidObs(obs);
			if (reason != null) {
				obs = newObs(question);
				obs.setValueText(reason);
				encounter.addObs(obs);
			}
		}
	}
	
	/**
	 * Smear microscopy results of this program for the follow-up months (FOLLOWUP_SMEAR_MONTHS),
	 * read from the COMMON TEST lab results by their MONTH OF TREATMENT attribute (and from old
	 * specimen collection encounters, as MdrtbService.getSmearForms does).
	 */
	public List<SmearForm> getFollowupSmears() {
		List<SmearForm> followupSmears = new ArrayList<>();
		Integer patientProgramId = getPatientProgramId();
		if (patientProgramId == null) {
			return followupSmears;
		}
		List<SmearForm> smears = Context.getService(MdrtbService.class).getSmearForms(patientProgramId);
		for (Integer month : FOLLOWUP_SMEAR_MONTHS) {
			for (SmearForm smear : smears) {
				if (smear.getMonthOfTreatment() != null && smear.getMonthOfTreatment().intValue() == month) {
					followupSmears.add(smear);
					break;
				}
			}
		}
		return followupSmears;
	}
	
	public String getLink() {
		return "/module/mdrtb/form/endOfTreatment.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	/** The live top-level obs of this question, or null (also when the concept is not set up yet) */
	private Obs findObs(String conceptUuid) {
		Concept concept = Context.getService(MdrtbService.class).getConcept(conceptUuid);
		return concept == null ? null : MdrtbUtil.getObsFromEncounter(concept, encounter);
	}
	
	/**
	 * The concept to write under. Refuses to store a value when the concept does not exist in the
	 * database (e.g. not loaded by the ETL yet), instead of dropping it silently.
	 */
	private Concept conceptForWrite(String conceptUuid, String constantName, boolean hasValue) {
		Concept concept = Context.getService(MdrtbService.class).getConcept(conceptUuid);
		if (concept == null && hasValue) {
			throw new MdrtbAPIException("End of Treatment: concept MdrtbConcepts." + constantName
			        + " not found in the concept dictionary (UUID " + conceptUuid + ")");
		}
		return concept;
	}
	
	/** Puts the member in the group and on the encounter (as SmearForm does for its obs group) */
	private void addGroupMember(Obs group, Obs member) {
		group.addGroupMember(member);
		encounter.addObs(member);
	}
	
	private Obs newObs(Concept concept) {
		return new Obs(encounter.getPatient(), concept, encounter.getEncounterDatetime(), encounter.getLocation());
	}
	
	/**
	 * Voids the obs and, for an obs group, its members (openmrs doesn't void obs saved via
	 * encounters)
	 */
	private void voidObs(Obs obs) {
		if (obs == null) {
			return;
		}
		obs.setVoided(true);
		obs.setVoidReason(VOID_REASON);
		if (obs.getGroupMembers() != null) {
			for (Obs member : obs.getGroupMembers()) {
				member.setVoided(true);
				member.setVoidReason(VOID_REASON);
			}
		}
	}
	
	private Date getDate(String conceptUuid) {
		Obs obs = findObs(conceptUuid);
		return obs == null ? null : obs.getValueDatetime();
	}
	
	private void setDate(String conceptUuid, String constantName, Date date) {
		Concept question = conceptForWrite(conceptUuid, constantName, date != null);
		Obs obs = findObs(conceptUuid);
		if (obs == null && date == null) {
			return;
		}
		if (obs == null || !sameDate(obs.getValueDatetime(), date)) {
			voidObs(obs);
			if (date != null) {
				obs = newObs(question);
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	/** Compares by time value, because a stored date may come back as java.sql.Timestamp */
	private static boolean sameDate(Date a, Date b) {
		if (a == null || b == null) {
			return a == b;
		}
		return a.getTime() == b.getTime();
	}
}
