package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.*;
import org.openmrs.module.mdrtb.PatientSummary;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class SimplePatientSummaryData extends BaseOpenmrsData {

    private static final long serialVersionUID = 111L;

    private String patientUuid;

    private String patientName;

    private String gender;

    private String residentialAddress;

    private Date dateOfBirth;

    private Set<PatientIdentifier> patientIdentifiers;

    private List<SimplePatientProgramSummary> patientPrograms;

    public SimplePatientSummaryData(PatientSummary patientSummary) {

    }

    @Override
    public Integer getId() {
        return -1;
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

    public List<SimplePatientProgramSummary> getSimplePatientProgramSummaries() {
        return patientPrograms;
    }

    public void setSimplePatientProgramSummarys(List<SimplePatientProgramSummary> patientPrograms) {
        this.patientPrograms = patientPrograms;
    }

    public Set<PatientIdentifier> getPatientIdentifiers() {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers(Set<PatientIdentifier> patientIdentifiers) {
        this.patientIdentifiers = patientIdentifiers;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}