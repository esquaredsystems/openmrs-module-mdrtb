package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Obs;
import org.openmrs.module.mdrtb.lab.LabTest;

import java.util.Date;
import java.util.List;

public class SimplePatientProgramSummary extends BaseOpenmrsData {

    private static final long serialVersionUID = 121L;

    private String patientUuid;

    private String programName;

    private String patientProgramUuid;

    private Date startDate;

    private Date endDate;

    private String treatmentOutcome;

    private List<Obs> observations;

    private List<LabTest> labTests;

    public SimplePatientProgramSummary(String patientUuid, String programName, String patientProgramUuid,
        Date startDate, Date endDate, String treatmentOutcome, List<Obs> observations, List<LabTest> labTests) {
        this.patientUuid = patientUuid;
        this.programName = programName;
        this.patientProgramUuid = patientProgramUuid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.treatmentOutcome = treatmentOutcome;
        this.observations = observations;
        this.labTests = labTests;
    }

    @Override
    public Integer getId() {
        return 0;
    }

    @Override
    public void setId(Integer id) {

    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getPatientProgramUuid() {
        return patientProgramUuid;
    }

    public void setPatientProgramUuid(String patientProgramUuid) {
        this.patientProgramUuid = patientProgramUuid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTreatmentOutcome() {
        return treatmentOutcome;
    }

    public void setTreatmentOutcome(String treatmentOutcome) {
        this.treatmentOutcome = treatmentOutcome;
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
}
