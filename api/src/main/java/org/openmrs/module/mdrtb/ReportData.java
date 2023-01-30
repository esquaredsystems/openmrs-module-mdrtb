/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;

/**
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */
@Entity(name = "mdrtb.ReportData")
@Table(name = "report_data")
public class ReportData extends BaseOpenmrsObject {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "report_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "region_id")
	private Location region;
	
	@ManyToOne
	@JoinColumn(name = "district_id")
	private Location district;
	
	@ManyToOne
	@JoinColumn(name = "facility_id")
	private Location facility;
	
	@Basic
	@Column(name = "report_name", length = 255)
	private String reportName;
	
	@Basic
	@Column(name = "description", length = 255)
	private String description;
	
	@Basic
	@Column(name = "year")
	private Integer year;
	
	@Basic
	@Column(name = "quarter")
	private Integer quarter;
	
	@Basic
	@Column(name = "month")
	private Integer month;
	
	@Basic
	@Column(name = "report_date")
	private Date reportDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "report_type", length = 50)
	private ReportType reportType;
	
	@Basic
	@Column(name = "report_status")
	private Integer reportStatus;
	
	@Column(name = "table_data", columnDefinition = "text")
	private String tableData;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Location getRegion() {
		return region;
	}
	
	public void setRegion(Location region) {
		this.region = region;
	}
	
	public Location getDistrict() {
		return district;
	}
	
	public void setDistrict(Location district) {
		this.district = district;
	}
	
	public Location getFacility() {
		return facility;
	}
	
	public void setFacility(Location facility) {
		this.facility = facility;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getQuarter() {
		return quarter;
	}
	
	public void setQuarter(Integer quarter) {
		this.quarter = quarter;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	public ReportType getReportType() {
		return reportType;
	}
	
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
	public Integer getReportStatus() {
		return reportStatus;
	}
	
	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	public String getTableData() {
		return tableData;
	}
	
	public void setTableData(String tableData) {
		this.tableData = tableData;
	}
}
