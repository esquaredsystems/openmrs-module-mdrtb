package org.openmrs.module.mdrtb.web.resource;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;

public class SimpleTB03uForm extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -5237169924611683546L;
	
	private Encounter encounter;
	
	private Integer patientProgramId;
	
	private Integer relapseMonth;
	
	private String clinicalNotes;
	
	private String otherCauseOfDeath;
	
	private String nameOfTreatmentLocation;
	
	private String sldRegisterNumber;
	
	private String weight;
	
	private Date mdrTreatmentStartDate;
	
	private Date artStartDate;
	
	private Date pctStartDate;
	
	private Date hivTestDate;
	
	private Date treatmentOutcomeDate;
	
	private Date dateOfDeathAfterTreatmentOutcome;
	
	private Date confirmationDate;
	
	private Concept anatomicalSite;
	
	private Concept patientCategory;
	
	private Concept registrationGroup;
	
	private Concept hivStatus;
	
	private Concept causeOfDeath;
	
	private Concept treatmentOutcome;
	
	private Concept basisForDiagnosis;
	
	private Concept mdrStatus;
	
	private Concept registrationGroupByDrug;
	
	private Concept relapsed;
	
	private Concept treatmentLocation;
	
	public SimpleTB03uForm() {
	}
	
	public SimpleTB03uForm(TB03uForm tb03u) {
		setEncounter(tb03u.getEncounter());
		setUuid(getEncounter().getUuid());
		setPatientProgramId(tb03u.getPatientProgramId());
		setAnatomicalSite(tb03u.getAnatomicalSite());
		setArtStartDate(tb03u.getArtStartDate());
		setBasisForDiagnosis(tb03u.getBasisForDiagnosis());
		setCauseOfDeath(tb03u.getCauseOfDeath());
		setClinicalNotes(tb03u.getCliniciansNotes());
		setConfirmationDate(tb03u.getConfirmationDate());
		setDateOfDeathAfterTreatmentOutcome(tb03u.getDateOfDeathAfterOutcome());
		setHivStatus(tb03u.getHivStatus());
		setMdrStatus(tb03u.getMdrStatus());
		setMdrTreatmentStartDate(tb03u.getMdrTreatmentStartDate());
		setNameOfTreatmentLocation(tb03u.getNameOfTxLocation());
		setOtherCauseOfDeath(tb03u.getOtherCauseOfDeath());
		setPatientCategory(tb03u.getPatientCategory());
		setPctStartDate(tb03u.getPctStartDate());
		setRegistrationGroup(tb03u.getRegistrationGroup());
		setRegistrationGroupByDrug(tb03u.getRegistrationGroupByDrug());
		setRelapsed(tb03u.getRelapsed());
		setRelapseMonth(tb03u.getRelapseMonth());
		setSldRegisterNumber(tb03u.getSldRegisterNumber());
		setTreatmentOutcome(tb03u.getTreatmentOutcome());
		setTreatmentOutcomeDate(tb03u.getTreatmentOutcomeDate());
		setTreatmentLocation(tb03u.getTxLocation());
		setWeight(tb03u.getWeight());
	}
	
	/**
	 * Provide {@link SimpleTB03uForm} representation of {@link TB03Form}
	 * 
	 * @param tb03
	 * @return
	 */
	public TB03uForm toForm() {
		TB03uForm tb03u = new TB03uForm();
		tb03u.setPatientProgramId(getPatientProgramId());
		tb03u.setAnatomicalSite(getAnatomicalSite());
		tb03u.setArtStartDate(getArtStartDate());
		tb03u.setBasisForDiagnosis(getBasisForDiagnosis());
		tb03u.setCauseOfDeath(getCauseOfDeath());
		tb03u.setCliniciansNotes(getClinicalNotes());
		tb03u.setConfirmationDate(getConfirmationDate());
		tb03u.setDateOfDeathAfterOutcome(getDateOfDeathAfterTreatmentOutcome());
		tb03u.setHivStatus(getHivStatus());
		tb03u.setHivTestDate(getHivTestDate());
		tb03u.setMdrStatus(getMdrStatus());
		tb03u.setMdrTreatmentStartDate(getMdrTreatmentStartDate());
		tb03u.setNameOfTxLocation(getNameOfTreatmentLocation());
		tb03u.setOtherCauseOfDeath(getOtherCauseOfDeath());
		tb03u.setPatientCategory(getPatientCategory());
		tb03u.setPctStartDate(getPctStartDate());
		tb03u.setRegistrationGroup(getRegistrationGroup());
		tb03u.setRegistrationGroupByDrug(getRegistrationGroupByDrug());
		tb03u.setRelapsed(getRelapsed());
		tb03u.setRelapseMonth(getRelapseMonth());
		tb03u.setSldRegisterNumber(getSldRegisterNumber());
		tb03u.setTreatmentOutcome(getTreatmentOutcome());
		tb03u.setTreatmentOutcomeDate(getTreatmentOutcomeDate());
		tb03u.setTxLocation(getTreatmentLocation());
		tb03u.setWeight(getWeight());
		return tb03u;
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
	
	public Integer getPatientProgramId() {
		return patientProgramId;
	}
	
	public void setPatientProgramId(Integer patientProgramId) {
		this.patientProgramId = patientProgramId;
	}
	
	public Integer getRelapseMonth() {
		return relapseMonth;
	}
	
	public void setRelapseMonth(Integer relapseMonth) {
		this.relapseMonth = relapseMonth;
	}
	
	public String getClinicalNotes() {
		return clinicalNotes;
	}
	
	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}
	
	public String getOtherCauseOfDeath() {
		return otherCauseOfDeath;
	}
	
	public void setOtherCauseOfDeath(String otherCauseOfDeath) {
		this.otherCauseOfDeath = otherCauseOfDeath;
	}
	
	public String getNameOfTreatmentLocation() {
		return nameOfTreatmentLocation;
	}
	
	public void setNameOfTreatmentLocation(String nameOfTreatmentLocation) {
		this.nameOfTreatmentLocation = nameOfTreatmentLocation;
	}
	
	public String getSldRegisterNumber() {
		return sldRegisterNumber;
	}
	
	public void setSldRegisterNumber(String sldRegisterNumber) {
		this.sldRegisterNumber = sldRegisterNumber;
	}
	
	public String getWeight() {
		return weight;
	}
	
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public Date getMdrTreatmentStartDate() {
		return mdrTreatmentStartDate;
	}
	
	public void setMdrTreatmentStartDate(Date mdrTreatmentStartDate) {
		this.mdrTreatmentStartDate = mdrTreatmentStartDate;
	}
	
	public Date getArtStartDate() {
		return artStartDate;
	}
	
	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}
	
	public Date getPctStartDate() {
		return pctStartDate;
	}
	
	public void setPctStartDate(Date pctStartDate) {
		this.pctStartDate = pctStartDate;
	}
	
	public Date getHivTestDate() {
		return hivTestDate;
	}
	
	public void setHivTestDate(Date hivTestDate) {
		this.hivTestDate = hivTestDate;
	}
	
	public Date getTreatmentOutcomeDate() {
		return treatmentOutcomeDate;
	}
	
	public void setTreatmentOutcomeDate(Date treatmentOutcomeDate) {
		this.treatmentOutcomeDate = treatmentOutcomeDate;
	}
	
	public Date getDateOfDeathAfterTreatmentOutcome() {
		return dateOfDeathAfterTreatmentOutcome;
	}
	
	public void setDateOfDeathAfterTreatmentOutcome(Date dateOfDeathAfterTreatmentOutcome) {
		this.dateOfDeathAfterTreatmentOutcome = dateOfDeathAfterTreatmentOutcome;
	}
	
	public Date getConfirmationDate() {
		return confirmationDate;
	}
	
	public void setConfirmationDate(Date confirmationDate) {
		this.confirmationDate = confirmationDate;
	}
	
	public Concept getAnatomicalSite() {
		return anatomicalSite;
	}
	
	public void setAnatomicalSite(Concept anatomicalSite) {
		this.anatomicalSite = anatomicalSite;
	}
	
	public Concept getPatientCategory() {
		return patientCategory;
	}
	
	public void setPatientCategory(Concept patientCategory) {
		this.patientCategory = patientCategory;
	}
	
	public Concept getRegistrationGroup() {
		return registrationGroup;
	}
	
	public void setRegistrationGroup(Concept registrationGroup) {
		this.registrationGroup = registrationGroup;
	}
	
	public Concept getHivStatus() {
		return hivStatus;
	}
	
	public void setHivStatus(Concept hivStatus) {
		this.hivStatus = hivStatus;
	}
	
	public Concept getCauseOfDeath() {
		return causeOfDeath;
	}
	
	public void setCauseOfDeath(Concept causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}
	
	public Concept getTreatmentOutcome() {
		return treatmentOutcome;
	}
	
	public void setTreatmentOutcome(Concept treatmentOutcome) {
		this.treatmentOutcome = treatmentOutcome;
	}
	
	public Concept getBasisForDiagnosis() {
		return basisForDiagnosis;
	}
	
	public void setBasisForDiagnosis(Concept basisForDiagnosis) {
		this.basisForDiagnosis = basisForDiagnosis;
	}
	
	public Concept getMdrStatus() {
		return mdrStatus;
	}
	
	public void setMdrStatus(Concept mdrStatus) {
		this.mdrStatus = mdrStatus;
	}
	
	public Concept getRegistrationGroupByDrug() {
		return registrationGroupByDrug;
	}
	
	public void setRegistrationGroupByDrug(Concept registrationGroupByDrug) {
		this.registrationGroupByDrug = registrationGroupByDrug;
	}
	
	public Concept getRelapsed() {
		return relapsed;
	}
	
	public void setRelapsed(Concept relapsed) {
		this.relapsed = relapsed;
	}
	
	public Concept getTreatmentLocation() {
		return treatmentLocation;
	}
	
	public void setTreatmentLocation(Concept treatmentLocation) {
		this.treatmentLocation = treatmentLocation;
	}
}
