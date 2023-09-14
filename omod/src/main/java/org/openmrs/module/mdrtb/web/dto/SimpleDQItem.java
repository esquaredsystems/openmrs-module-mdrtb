package org.openmrs.module.mdrtb.web.dto;

import java.util.ArrayList;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;

public class SimpleDQItem extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String dateOfBirth;
	
	private String locName;
	
	private ArrayList<String> tb03Links;
	
	private ArrayList<String> form89Links;
	
	public SimpleDQItem(DQItem dqItem) {
		setPatientUuid(dqItem.getPatient().getUuid());
		setDateOfBirth(dqItem.getDateOfBirth());
		setLocName(dqItem.getLocName());
		setTb03Links(dqItem.getTb03Links());
		setForm89Links(dqItem.getForm89Links());
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
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getLocName() {
		return locName;
	}
	
	public void setLocName(String locName) {
		this.locName = locName;
	}
	
	public ArrayList<String> getTb03Links() {
		return tb03Links;
	}
	
	public void setTb03Links(ArrayList<String> tb03Links) {
		this.tb03Links = tb03Links;
	}
	
	public void addTb03Link(String tb03Link) {
		if (tb03Link != null) {
			this.tb03Links.add(tb03Link);
		}
	}
	
	public ArrayList<String> getForm89Links() {
		return form89Links;
	}
	
	public void setForm89Links(ArrayList<String> form89Links) {
		this.form89Links = form89Links;
	}
	
	public void addForm89Link(String tb03form89Link) {
		if (tb03form89Link != null) {
			this.form89Links.add(tb03form89Link);
		}
	}
}
