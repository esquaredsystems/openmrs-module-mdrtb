package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;

public class SimpleAdverseEventsForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 5388440773197658161L;
	
	private Encounter encounter;
	
	private String patientProgramUuid;
	
	private String actionTakenSummary;
	
	private String clinicianNotes;
	
	private String comments;
	
	private String diagnosticSummary;
	
	private String facility;
	
	private String link;
	
	private String suspectedDrug;
	
	private String treatmentRegimenAtOnset;
	
	private Date outcomeDate;
	
	private Date yellowCardDate;
	
	private Concept actionOutcome;
	
	private Concept actionTaken;
	
	private Concept actionTaken2;
	
	private Concept actionTaken3;
	
	private Concept actionTaken4;
	
	private Concept actionTaken5;
	
	private Concept advereEvent;
	
	private Concept albuminDone;
	
	private Concept alkalinePhosphateDone;
	
	private Concept altDone;
	
	private Concept amylaseDone;
	
	private Concept astDone;
	
	private Concept audiogramDone;
	
	private Concept bilirubinDone;
	
	private Concept bloodGlucoseDone;
	
	private Concept calciumDone;
	
	private Concept casualityAssessmentResult;
	
	private Concept casualityAssessmentResult2;
	
	private Concept casualityAssessmentResult3;
	
	private Concept casualityDrug;
	
	private Concept casualityDrug2;
	
	private Concept casualityDrug3;
	
	private Concept cbcDone;
	
	private Concept clinicalScreenDone;
	
	private Concept diagnosticInvestigation;
	
	private Concept drugRechallenge;
	
	private Concept ecgDone;
	
	private Concept lipaseDone;
	
	private Concept magnesiumDone;
	
	private Concept meddraCode;
	
	private Concept neuroInvestigationDone;
	
	private Concept otherTestDone;
	
	private Concept potassiumDone;
	
	private Concept requiresAncillaryDrugs;
	
	private Concept requiresDoseChange;
	
	private Concept serumCreatinineDone;
	
	private Concept simpleHearingTestDone;
	
	private Concept thyroidTestDone;
	
	private Concept typeOfEvent;
	
	private Concept typeOfSAE;
	
	private Concept typeOfSpecialEvent;
	
	private Concept visualAcuityDone;
	
	private Concept ygtDone;
	
	public SimpleAdverseEventsForm() {
	}
	
	public SimpleAdverseEventsForm(AdverseEventsForm aeForm) {
		setEncounter(aeForm.getEncounter());
		setUuid(getEncounter().getUuid());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(aeForm.getPatientProgramId());
		setPatientProgramUuid(patientProgram.getUuid());
		setActionOutcome(aeForm.getActionOutcome());
		setActionTaken(aeForm.getActionTaken());
		setActionTaken2(aeForm.getActionTaken2());
		setActionTaken3(aeForm.getActionTaken3());
		setActionTaken4(aeForm.getActionTaken4());
		setActionTaken5(aeForm.getActionTaken5());
		setActionTakenSummary(aeForm.getActionTakenSummary());
		setAdvereEvent(aeForm.getAdverseEvent());
		setAlbuminDone(aeForm.getAlbuminDone());
		setAlkalinePhosphateDone(aeForm.getAlkalinePhosphataseDone());
		setAltDone(aeForm.getAltDone());
		setAmylaseDone(aeForm.getAmylaseDone());
		setAstDone(aeForm.getAstDone());
		setAudiogramDone(aeForm.getAudiogramDone());
		setBilirubinDone(aeForm.getBilirubinDone());
		setBloodGlucoseDone(aeForm.getBloodGlucoseDone());
		setCalciumDone(aeForm.getCalciumDone());
		setCasualityAssessmentResult(aeForm.getCausalityAssessmentResult1());
		setCasualityAssessmentResult2(aeForm.getCausalityAssessmentResult2());
		setCasualityAssessmentResult3(aeForm.getCausalityAssessmentResult3());
		setCasualityDrug(aeForm.getCausalityDrug1());
		setCasualityDrug2(aeForm.getCausalityDrug2());
		setCasualityDrug3(aeForm.getCausalityDrug3());
		setCbcDone(aeForm.getCbcDone());
		setClinicianNotes(aeForm.getClinicianNotes());
		setClinicalScreenDone(aeForm.getClinicalScreenDone());
		setComments(aeForm.getComments());
		setDiagnosticInvestigation(aeForm.getDiagnosticInvestigation());
		setDiagnosticSummary(aeForm.getDiagnosticSummary());
		setDrugRechallenge(aeForm.getDrugRechallenge());
		setEcgDone(aeForm.getEcgDone());
		setFacility(aeForm.getFacility());
		setLink(aeForm.getLink());
		setLipaseDone(aeForm.getLipaseDone());
		setMagnesiumDone(aeForm.getMagnesiumDone());
		setMeddraCode(aeForm.getMeddraCode());
		setNeuroInvestigationDone(aeForm.getNeuroInvestigationDone());
		setOtherTestDone(aeForm.getOtherTestDone());
		setOutcomeDate(aeForm.getOutcomeDate());
		setPotassiumDone(aeForm.getPotassiumDone());
		setRequiresAncillaryDrugs(aeForm.getRequiresAncillaryDrugs());
		setRequiresDoseChange(aeForm.getRequiresDoseChange());
		setSerumCreatinineDone(aeForm.getSerumCreatnineDone());
		setSimpleHearingTestDone(aeForm.getSimpleHearingTestDone());
		setSuspectedDrug(aeForm.getSuspectedDrug());
		setThyroidTestDone(aeForm.getThyroidTestDone());
		setTreatmentRegimenAtOnset(aeForm.getTreatmentRegimenAtOnset());
		setTypeOfEvent(aeForm.getTypeOfEvent());
		setTypeOfSAE(aeForm.getTypeOfSAE());
		setTypeOfSpecialEvent(aeForm.getTypeOfSpecialEvent());
		setVisualAcuityDone(aeForm.getVisualAcuityDone());
		setYellowCardDate(aeForm.getYellowCardDate());
		setYgtDone(aeForm.getYgtDone());
	}
	
	/**
	 * Provide {@link SimpleAdverseEventsForm} representation of {@link TB03Form}
	 */
	public AdverseEventsForm toForm() {
		AdverseEventsForm aeForm = new AdverseEventsForm();
		aeForm.setEncounter(getEncounter());
		PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(getPatientProgramUuid());
		aeForm.setPatientProgramId(patientProgram.getPatientProgramId());
		aeForm.setActionOutcome(getActionOutcome());
		aeForm.setActionTaken(getActionTaken());
		aeForm.setActionTaken2(getActionTaken2());
		aeForm.setActionTaken3(getActionTaken3());
		aeForm.setActionTaken4(getActionTaken4());
		aeForm.setActionTaken5(getActionTaken5());
		aeForm.setAdverseEvent(getAdvereEvent());
		aeForm.setAlbuminDone(getAlbuminDone());
		aeForm.setAlkalinePhosphataseDone(getAlkalinePhosphateDone());
		aeForm.setAltDone(getAltDone());
		aeForm.setAmylaseDone(getAmylaseDone());
		aeForm.setAstDone(getAstDone());
		aeForm.setAudiogramDone(getAudiogramDone());
		aeForm.setBilirubinDone(getBilirubinDone());
		aeForm.setBloodGlucoseDone(getBloodGlucoseDone());
		aeForm.setCalciumDone(getCalciumDone());
		aeForm.setCausalityAssessmentResult1(getCasualityAssessmentResult());
		aeForm.setCausalityAssessmentResult2(getCasualityAssessmentResult2());
		aeForm.setCausalityAssessmentResult3(getCasualityAssessmentResult3());
		aeForm.setCausalityDrug1(getCasualityDrug());
		aeForm.setCausalityDrug2(getCasualityDrug2());
		aeForm.setCausalityDrug3(getCasualityDrug3());
		aeForm.setCbcDone(getCbcDone());
		aeForm.setClinicalScreenDone(getClinicalScreenDone());
		aeForm.setClinicianNotes(getClinicianNotes());
		aeForm.setComments(getComments());
		aeForm.setDiagnosticInvestigation(getDiagnosticInvestigation());
		aeForm.setDrugRechallenge(getDrugRechallenge());
		aeForm.setEcgDone(getEcgDone());
		aeForm.setLipaseDone(getLipaseDone());
		aeForm.setMagnesiumDone(getMagnesiumDone());
		aeForm.setMeddraCode(getMeddraCode());
		aeForm.setNeuroInvestigationDone(getNeuroInvestigationDone());
		aeForm.setOtherTestDone(getOtherTestDone());
		aeForm.setOutcomeDate(getOutcomeDate());
		aeForm.setPotassiumDone(getPotassiumDone());
		aeForm.setRequiresAncillaryDrugs(getRequiresAncillaryDrugs());
		aeForm.setRequiresDoseChange(getRequiresDoseChange());
		aeForm.setSerumCreatinineDone(getSerumCreatinineDone());
		aeForm.setSimpleHearingTestDone(getSimpleHearingTestDone());
		aeForm.setSuspectedDrug(getSuspectedDrug());
		aeForm.setThyroidTestDone(getThyroidTestDone());
		aeForm.setTreatmentRegimenAtOnset(getTreatmentRegimenAtOnset());
		aeForm.setTypeOfEvent(getTypeOfEvent());
		aeForm.setTypeOfSAE(getTypeOfSAE());
		aeForm.setTypeOfSpecialEvent(getTypeOfSpecialEvent());
		aeForm.setVisualAcuityDone(getVisualAcuityDone());
		aeForm.setYellowCardDate(getYellowCardDate());
		aeForm.setYgtDone(getYgtDone());
		return aeForm;
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
	
	public String getActionTakenSummary() {
		return actionTakenSummary;
	}
	
	public void setActionTakenSummary(String actionTakenSummary) {
		this.actionTakenSummary = actionTakenSummary;
	}
	
	public String getClinicianNotes() {
		return clinicianNotes;
	}
	
	public void setClinicianNotes(String clinicianNotes) {
		this.clinicianNotes = clinicianNotes;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getDiagnosticSummary() {
		return diagnosticSummary;
	}
	
	public void setDiagnosticSummary(String diagnosticSummary) {
		this.diagnosticSummary = diagnosticSummary;
	}
	
	public String getFacility() {
		return facility;
	}
	
	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getSuspectedDrug() {
		return suspectedDrug;
	}
	
	public void setSuspectedDrug(String suspectedDrug) {
		this.suspectedDrug = suspectedDrug;
	}
	
	public String getTreatmentRegimenAtOnset() {
		return treatmentRegimenAtOnset;
	}
	
	public void setTreatmentRegimenAtOnset(String treatmentRegimenAtOnset) {
		this.treatmentRegimenAtOnset = treatmentRegimenAtOnset;
	}
	
	public Date getOutcomeDate() {
		return outcomeDate;
	}
	
	public void setOutcomeDate(Date outcomeDate) {
		this.outcomeDate = outcomeDate;
	}
	
	public Date getYellowCardDate() {
		return yellowCardDate;
	}
	
	public void setYellowCardDate(Date yellowCardDate) {
		this.yellowCardDate = yellowCardDate;
	}
	
	public Concept getActionOutcome() {
		return actionOutcome;
	}
	
	public void setActionOutcome(Concept actionOutcome) {
		this.actionOutcome = actionOutcome;
	}
	
	public Concept getActionTaken() {
		return actionTaken;
	}
	
	public void setActionTaken(Concept actionTaken) {
		this.actionTaken = actionTaken;
	}
	
	public Concept getActionTaken2() {
		return actionTaken2;
	}
	
	public void setActionTaken2(Concept actionTaken2) {
		this.actionTaken2 = actionTaken2;
	}
	
	public Concept getActionTaken3() {
		return actionTaken3;
	}
	
	public void setActionTaken3(Concept actionTaken3) {
		this.actionTaken3 = actionTaken3;
	}
	
	public Concept getActionTaken4() {
		return actionTaken4;
	}
	
	public void setActionTaken4(Concept actionTaken4) {
		this.actionTaken4 = actionTaken4;
	}
	
	public Concept getActionTaken5() {
		return actionTaken5;
	}
	
	public void setActionTaken5(Concept actionTaken5) {
		this.actionTaken5 = actionTaken5;
	}
	
	public Concept getAdvereEvent() {
		return advereEvent;
	}
	
	public void setAdvereEvent(Concept advereEvent) {
		this.advereEvent = advereEvent;
	}
	
	public Concept getAlbuminDone() {
		return albuminDone;
	}
	
	public void setAlbuminDone(Concept albuminDone) {
		this.albuminDone = albuminDone;
	}
	
	public Concept getAlkalinePhosphateDone() {
		return alkalinePhosphateDone;
	}
	
	public void setAlkalinePhosphateDone(Concept alkalinePhosphateDone) {
		this.alkalinePhosphateDone = alkalinePhosphateDone;
	}
	
	public Concept getAltDone() {
		return altDone;
	}
	
	public void setAltDone(Concept altDone) {
		this.altDone = altDone;
	}
	
	public Concept getAmylaseDone() {
		return amylaseDone;
	}
	
	public void setAmylaseDone(Concept amylaseDone) {
		this.amylaseDone = amylaseDone;
	}
	
	public Concept getAstDone() {
		return astDone;
	}
	
	public void setAstDone(Concept astDone) {
		this.astDone = astDone;
	}
	
	public Concept getAudiogramDone() {
		return audiogramDone;
	}
	
	public void setAudiogramDone(Concept audiogramDone) {
		this.audiogramDone = audiogramDone;
	}
	
	public Concept getBilirubinDone() {
		return bilirubinDone;
	}
	
	public void setBilirubinDone(Concept bilirubinDone) {
		this.bilirubinDone = bilirubinDone;
	}
	
	public Concept getBloodGlucoseDone() {
		return bloodGlucoseDone;
	}
	
	public void setBloodGlucoseDone(Concept bloodGlucoseDone) {
		this.bloodGlucoseDone = bloodGlucoseDone;
	}
	
	public Concept getCalciumDone() {
		return calciumDone;
	}
	
	public void setCalciumDone(Concept calciumDone) {
		this.calciumDone = calciumDone;
	}
	
	public Concept getCasualityAssessmentResult() {
		return casualityAssessmentResult;
	}
	
	public void setCasualityAssessmentResult(Concept casualityAssessmentResult) {
		this.casualityAssessmentResult = casualityAssessmentResult;
	}
	
	public Concept getCasualityAssessmentResult2() {
		return casualityAssessmentResult2;
	}
	
	public void setCasualityAssessmentResult2(Concept casualityAssessmentResult2) {
		this.casualityAssessmentResult2 = casualityAssessmentResult2;
	}
	
	public Concept getCasualityAssessmentResult3() {
		return casualityAssessmentResult3;
	}
	
	public void setCasualityAssessmentResult3(Concept casualityAssessmentResult3) {
		this.casualityAssessmentResult3 = casualityAssessmentResult3;
	}
	
	public Concept getCasualityDrug() {
		return casualityDrug;
	}
	
	public void setCasualityDrug(Concept casualityDrug) {
		this.casualityDrug = casualityDrug;
	}
	
	public Concept getCasualityDrug2() {
		return casualityDrug2;
	}
	
	public void setCasualityDrug2(Concept casualityDrug2) {
		this.casualityDrug2 = casualityDrug2;
	}
	
	public Concept getCasualityDrug3() {
		return casualityDrug3;
	}
	
	public void setCasualityDrug3(Concept casualityDrug3) {
		this.casualityDrug3 = casualityDrug3;
	}
	
	public Concept getCbcDone() {
		return cbcDone;
	}
	
	public void setCbcDone(Concept cbcDone) {
		this.cbcDone = cbcDone;
	}
	
	public Concept getClinicalScreenDone() {
		return clinicalScreenDone;
	}
	
	public void setClinicalScreenDone(Concept clinicalScreenDone) {
		this.clinicalScreenDone = clinicalScreenDone;
	}
	
	public Concept getDiagnosticInvestigation() {
		return diagnosticInvestigation;
	}
	
	public void setDiagnosticInvestigation(Concept diagnosticInvestigation) {
		this.diagnosticInvestigation = diagnosticInvestigation;
	}
	
	public Concept getDrugRechallenge() {
		return drugRechallenge;
	}
	
	public void setDrugRechallenge(Concept drugRechallenge) {
		this.drugRechallenge = drugRechallenge;
	}
	
	public Concept getEcgDone() {
		return ecgDone;
	}
	
	public void setEcgDone(Concept ecgDone) {
		this.ecgDone = ecgDone;
	}
	
	public Concept getLipaseDone() {
		return lipaseDone;
	}
	
	public void setLipaseDone(Concept lipaseDone) {
		this.lipaseDone = lipaseDone;
	}
	
	public Concept getMagnesiumDone() {
		return magnesiumDone;
	}
	
	public void setMagnesiumDone(Concept magnesiumDone) {
		this.magnesiumDone = magnesiumDone;
	}
	
	public Concept getMeddraCode() {
		return meddraCode;
	}
	
	public void setMeddraCode(Concept meddraCode) {
		this.meddraCode = meddraCode;
	}
	
	public Concept getNeuroInvestigationDone() {
		return neuroInvestigationDone;
	}
	
	public void setNeuroInvestigationDone(Concept neuroInvestigationDone) {
		this.neuroInvestigationDone = neuroInvestigationDone;
	}
	
	public Concept getOtherTestDone() {
		return otherTestDone;
	}
	
	public void setOtherTestDone(Concept otherTestDone) {
		this.otherTestDone = otherTestDone;
	}
	
	public Concept getPotassiumDone() {
		return potassiumDone;
	}
	
	public void setPotassiumDone(Concept potassiumDone) {
		this.potassiumDone = potassiumDone;
	}
	
	public Concept getRequiresAncillaryDrugs() {
		return requiresAncillaryDrugs;
	}
	
	public void setRequiresAncillaryDrugs(Concept requiresAncillaryDrugs) {
		this.requiresAncillaryDrugs = requiresAncillaryDrugs;
	}
	
	public Concept getRequiresDoseChange() {
		return requiresDoseChange;
	}
	
	public void setRequiresDoseChange(Concept requiresDoseChange) {
		this.requiresDoseChange = requiresDoseChange;
	}
	
	public Concept getSerumCreatinineDone() {
		return serumCreatinineDone;
	}
	
	public void setSerumCreatinineDone(Concept serumCreatinineDone) {
		this.serumCreatinineDone = serumCreatinineDone;
	}
	
	public Concept getSimpleHearingTestDone() {
		return simpleHearingTestDone;
	}
	
	public void setSimpleHearingTestDone(Concept simpleHearingTestDone) {
		this.simpleHearingTestDone = simpleHearingTestDone;
	}
	
	public Concept getThyroidTestDone() {
		return thyroidTestDone;
	}
	
	public void setThyroidTestDone(Concept thyroidTestDone) {
		this.thyroidTestDone = thyroidTestDone;
	}
	
	public Concept getTypeOfEvent() {
		return typeOfEvent;
	}
	
	public void setTypeOfEvent(Concept typeOfEvent) {
		this.typeOfEvent = typeOfEvent;
	}
	
	public Concept getTypeOfSAE() {
		return typeOfSAE;
	}
	
	public void setTypeOfSAE(Concept typeOfSAE) {
		this.typeOfSAE = typeOfSAE;
	}
	
	public Concept getTypeOfSpecialEvent() {
		return typeOfSpecialEvent;
	}
	
	public void setTypeOfSpecialEvent(Concept typeOfSpecialEvent) {
		this.typeOfSpecialEvent = typeOfSpecialEvent;
	}
	
	public Concept getVisualAcuityDone() {
		return visualAcuityDone;
	}
	
	public void setVisualAcuityDone(Concept visualAcuityDone) {
		this.visualAcuityDone = visualAcuityDone;
	}
	
	public Concept getYgtDone() {
		return ygtDone;
	}
	
	public void setYgtDone(Concept ygtDone) {
		this.ygtDone = ygtDone;
	}
}
