package org.openmrs.module.mdrtb.form.custom;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestType;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.specimen.DstImpl;

public class DSTForm extends AbstractSimpleForm implements Comparable<DSTForm> {
	
	public DstImpl di;
	
	public DSTForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
		LabTest dst = CommonLabUtil.getService().createLabTestOrder(this.encounter, labTestType);
		di = new DstImpl(dst);
		// di = new DstImpl(this.encounter);
	}
	
	public DSTForm(Encounter encounter) {
		super(encounter);
		LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
		LabTest dst = CommonLabUtil.getService().createLabTestOrder(this.encounter, labTestType);
		di = new DstImpl(dst);
		// di = new DstImpl(this.encounter);
	}
	
	public DstImpl addResult() {
		return di.addResult();
	}
	
	public List<DstImpl> getResults() {
		return di.getResults();
	}
	
	public List<DstImpl> getResultsMap() {
		return di.getResultsMap();
	}
	
	public void removeResult(DstImpl result) {
		di.removeResult(result);
	}
	
	public DstImpl getDi() {
		return di;
	}
	
	public void setDi(DstImpl di) {
		this.di = di;
	}
	
	public String getSpecimenId() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setSpecimenId(String id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_ID), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || obs.getValueText() != id) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.SPECIMEN_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(id);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getPatientProgramId() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setPatientProgramId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && id == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != id.intValue()) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(new Double(id));
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getMonthOfTreatment() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT), encounter);
		
		if (obs == null) {
			return 0;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setMonthOfTreatment(Integer month) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && month == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != month.intValue()) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
			
			// now create the new Obs and add it to the encounter	
			if (month != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MONTH_OF_TREATMENT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(new Double(month));
				encounter.addObs(obs);
			}
		}
	}
	
	public int compareTo(DSTForm form) {
		if (this.getMonthOfTreatment() == null)
			return 1;
		if (form.getMonthOfTreatment() == null)
			return -1;
		return this.getMonthOfTreatment().compareTo(form.getMonthOfTreatment());
	}
	
	public String getLink() {
		return "/module/mdrtb/form/dst.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
}
