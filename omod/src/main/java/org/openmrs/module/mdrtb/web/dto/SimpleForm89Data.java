package org.openmrs.module.mdrtb.web.dto;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.Form89Data;

public class SimpleForm89Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String form89Uuid;
	
	private String identifier;
	
	private String tb03RegistrationDate;
	
	private Integer ageAtTB03Registration;
	
	private String dateOfBirth;
	
	private String siteOfDisease;
	
	private String dateFirstSeekingHelp;
	
	private String dateOfReturn;
	
	private String dateOfDecaySurvey;
	
	private String cmacDate;
	
	private Date form89Date;
	
	private String diagnosticSmearResult;
	
	private String diagnosticSmearTestNumber;
	
	private String diagnosticSmearDate;
	
	private String diagnosticSmearLab;
	
	private String xpertMTBResult;
	
	private String xpertRIFResult;
	
	private String xpertTestDate;
	
	private String xpertTestNumber;
	
	private String xpertLab;
	
	private String hainMTBResult;
	
	private String hainINHResult;
	
	private String hainRIFResult;
	
	private String hainTestDate;
	
	private String hainTestNumber;
	
	private String hainLab;
	
	private String hain2MTBResult;
	
	private String hain2InjResult;
	
	private String hain2FqResult;
	
	private String hain2TestDate;
	
	private String hain2TestNumber;
	
	private String hain2Lab;
	
	public SimpleForm89Data(Form89Data form89Data) {
		this.patientUuid = form89Data.getPatient().getUuid();
		this.form89Uuid = form89Data.getForm89().getEncounter().getUuid();
		this.identifier = form89Data.getIdentifier();
		this.tb03RegistrationDate = form89Data.getTb03RegistrationDate();
		this.ageAtTB03Registration = form89Data.getAgeAtTB03Registration();
		this.dateOfBirth = form89Data.getDateOfBirth();
		this.siteOfDisease = form89Data.getSiteOfDisease();
		this.dateFirstSeekingHelp = form89Data.getDateFirstSeekingHelp();
		this.dateOfReturn = form89Data.getDateOfReturn();
		this.dateOfDecaySurvey = form89Data.getDateOfDecaySurvey();
		this.cmacDate = form89Data.getCmacDate();
		this.form89Date = form89Data.getForm89().getForm89Date();
		this.diagnosticSmearResult = form89Data.getDiagnosticSmearResult();
		this.diagnosticSmearTestNumber = form89Data.getDiagnosticSmearTestNumber();
		this.diagnosticSmearDate = form89Data.getDiagnosticSmearDate();
		this.diagnosticSmearLab = form89Data.getDiagnosticSmearLab();
		this.xpertMTBResult = form89Data.getXpertMTBResult();
		this.xpertRIFResult = form89Data.getXpertRIFResult();
		this.xpertTestDate = form89Data.getXpertTestDate();
		this.xpertTestNumber = form89Data.getXpertTestNumber();
		this.xpertLab = form89Data.getXpertLab();
		this.hainMTBResult = form89Data.getHainMTBResult();
		this.hainINHResult = form89Data.getHainINHResult();
		this.hainRIFResult = form89Data.getHainRIFResult();
		this.hainTestDate = form89Data.getHainTestDate();
		this.hainTestNumber = form89Data.getHainTestNumber();
		this.hainLab = form89Data.getHainLab();
		this.hain2MTBResult = form89Data.getHain2MTBResult();
		this.hain2InjResult = form89Data.getHain2InjResult();
		this.hain2FqResult = form89Data.getHain2FqResult();
		this.hain2TestDate = form89Data.getHain2TestDate();
		this.hain2TestNumber = form89Data.getHain2TestNumber();
		this.hain2Lab = form89Data.getHain2Lab();
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public String getForm89Uuid() {
		return form89Uuid;
	}
	
	public void setForm89Uuid(String form89Uuid) {
		this.form89Uuid = form89Uuid;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getTb03RegistrationDate() {
		return tb03RegistrationDate;
	}
	
	public void setTb03RegistrationDate(String tb03RegistrationDate) {
		this.tb03RegistrationDate = tb03RegistrationDate;
	}
	
	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
	}
	
	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getSiteOfDisease() {
		return siteOfDisease;
	}
	
	public void setSiteOfDisease(String siteOfDisease) {
		this.siteOfDisease = siteOfDisease;
	}
	
	public String getDateFirstSeekingHelp() {
		return dateFirstSeekingHelp;
	}
	
	public void setDateFirstSeekingHelp(String dateFirstSeekingHelp) {
		this.dateFirstSeekingHelp = dateFirstSeekingHelp;
	}
	
	public String getDateOfReturn() {
		return dateOfReturn;
	}
	
	public void setDateOfReturn(String dateOfReturn) {
		this.dateOfReturn = dateOfReturn;
	}
	
	public String getDateOfDecaySurvey() {
		return dateOfDecaySurvey;
	}
	
	public void setDateOfDecaySurvey(String dateOfDecaySurvey) {
		this.dateOfDecaySurvey = dateOfDecaySurvey;
	}
	
	public String getCmacDate() {
		return cmacDate;
	}
	
	public void setCmacDate(String cmacDate) {
		this.cmacDate = cmacDate;
	}
	
	public Date getForm89Date() {
		return form89Date;
	}
	
	public void setForm89Date(Date form89Date) {
		this.form89Date = form89Date;
	}
	
	public String getDiagnosticSmearResult() {
		return diagnosticSmearResult;
	}
	
	public void setDiagnosticSmearResult(String diagnosticSmearResult) {
		this.diagnosticSmearResult = diagnosticSmearResult;
	}
	
	public String getDiagnosticSmearTestNumber() {
		return diagnosticSmearTestNumber;
	}
	
	public void setDiagnosticSmearTestNumber(String diagnosticSmearTestNumber) {
		this.diagnosticSmearTestNumber = diagnosticSmearTestNumber;
	}
	
	public String getDiagnosticSmearDate() {
		return diagnosticSmearDate;
	}
	
	public void setDiagnosticSmearDate(String diagnosticSmearDate) {
		this.diagnosticSmearDate = diagnosticSmearDate;
	}
	
	public String getDiagnosticSmearLab() {
		return diagnosticSmearLab;
	}
	
	public void setDiagnosticSmearLab(String diagnosticSmearLab) {
		this.diagnosticSmearLab = diagnosticSmearLab;
	}
	
	public String getXpertMTBResult() {
		return xpertMTBResult;
	}
	
	public void setXpertMTBResult(String xpertMTBResult) {
		this.xpertMTBResult = xpertMTBResult;
	}
	
	public String getXpertRIFResult() {
		return xpertRIFResult;
	}
	
	public void setXpertRIFResult(String xpertRIFResult) {
		this.xpertRIFResult = xpertRIFResult;
	}
	
	public String getXpertTestDate() {
		return xpertTestDate;
	}
	
	public void setXpertTestDate(String xpertTestDate) {
		this.xpertTestDate = xpertTestDate;
	}
	
	public String getXpertTestNumber() {
		return xpertTestNumber;
	}
	
	public void setXpertTestNumber(String xpertTestNumber) {
		this.xpertTestNumber = xpertTestNumber;
	}
	
	public String getXpertLab() {
		return xpertLab;
	}
	
	public void setXpertLab(String xpertLab) {
		this.xpertLab = xpertLab;
	}
	
	public String getHainMTBResult() {
		return hainMTBResult;
	}
	
	public void setHainMTBResult(String hainMTBResult) {
		this.hainMTBResult = hainMTBResult;
	}
	
	public String getHainINHResult() {
		return hainINHResult;
	}
	
	public void setHainINHResult(String hainINHResult) {
		this.hainINHResult = hainINHResult;
	}
	
	public String getHainRIFResult() {
		return hainRIFResult;
	}
	
	public void setHainRIFResult(String hainRIFResult) {
		this.hainRIFResult = hainRIFResult;
	}
	
	public String getHainTestDate() {
		return hainTestDate;
	}
	
	public void setHainTestDate(String hainTestDate) {
		this.hainTestDate = hainTestDate;
	}
	
	public String getHainTestNumber() {
		return hainTestNumber;
	}
	
	public void setHainTestNumber(String hainTestNumber) {
		this.hainTestNumber = hainTestNumber;
	}
	
	public String getHainLab() {
		return hainLab;
	}
	
	public void setHainLab(String hainLab) {
		this.hainLab = hainLab;
	}
	
	public String getHain2MTBResult() {
		return hain2MTBResult;
	}
	
	public void setHain2MTBResult(String hain2MTBResult) {
		this.hain2MTBResult = hain2MTBResult;
	}
	
	public String getHain2InjResult() {
		return hain2InjResult;
	}
	
	public void setHain2InjResult(String hain2InjResult) {
		this.hain2InjResult = hain2InjResult;
	}
	
	public String getHain2FqResult() {
		return hain2FqResult;
	}
	
	public void setHain2FqResult(String hain2FqResult) {
		this.hain2FqResult = hain2FqResult;
	}
	
	public String getHain2TestDate() {
		return hain2TestDate;
	}
	
	public void setHain2TestDate(String hain2TestDate) {
		this.hain2TestDate = hain2TestDate;
	}
	
	public String getHain2TestNumber() {
		return hain2TestNumber;
	}
	
	public void setHain2TestNumber(String hain2TestNumber) {
		this.hain2TestNumber = hain2TestNumber;
	}
	
	public String getHain2Lab() {
		return hain2Lab;
	}
	
	public void setHain2Lab(String hain2Lab) {
		this.hain2Lab = hain2Lab;
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
	}
}
