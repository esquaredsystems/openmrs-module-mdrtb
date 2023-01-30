/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb.api.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.LocationHierarchy;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.reporting.custom.PDFHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("mdrtb.MdrtbDao")
public class MdrtbDao {
	
	protected static final Log log = LogFactory.getLog(MdrtbDao.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public ReportData getReportData(Integer id) {
		return (ReportData) sessionFactory.getCurrentSession().createCriteria(ReportData.class)
		        .add(Restrictions.eq("id", id)).uniqueResult();
	}
	
	public ReportData getReportDataByUuid(String uuid) {
		return (ReportData) sessionFactory.getCurrentSession().createCriteria(ReportData.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	public ReportData saveReportData(ReportData reportData) {
		sessionFactory.getCurrentSession().saveOrUpdate(reportData);
		return reportData;
	}
	
	/**
	 * @see MdrtbDAO#getLocationsWithAnyProgramEnrollments()
	 */
	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException {
		String query = "select distinct location from PatientProgram where voided = false";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}
	
	/**
	 * @see MdrtbDAO#getAllRayonsTJK()
	 */
	@Deprecated
	public List<String> getAllRayonsTJK() throws DAOException {
		List<BaseLocation> list = getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		List<String> names = new ArrayList<String>();
		for (BaseLocation location : list) {
			names.add(location.getName());
		}
		return names;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort patients, List<Concept> drugConcepts) throws DAOException {
		Map<Integer, List<DrugOrder>> ret = new HashMap<Integer, List<DrugOrder>>();
		if (patients != null && patients.size() == 0)
			return ret;
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugOrder.class);
		criteria.setFetchMode("patient", FetchMode.JOIN);
		criteria.setCacheMode(CacheMode.IGNORE);
		
		// only include this where clause if patients were passed in
		if (patients != null)
			criteria.add(Restrictions.in("patient.personId", patients.getMemberIds()));
		
		if (drugConcepts != null)
			criteria.add(Restrictions.in("concept", drugConcepts));
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(org.hibernate.criterion.Order.asc("startDate"));
		log.debug("criteria: " + criteria);
		List<DrugOrder> temp = criteria.list();
		for (DrugOrder regimen : temp) {
			Integer ptId = regimen.getPatient().getPatientId();
			List<DrugOrder> list = ret.get(ptId);
			if (list == null) {
				list = new ArrayList<DrugOrder>();
				ret.put(ptId, list);
			}
			list.add(regimen);
		}
		return ret;
	}
	
	public PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId) {
		return (PatientIdentifier) sessionFactory.getCurrentSession().createCriteria(PatientIdentifier.class)
		        .add(Restrictions.eq("patientIdentifierId", patientIdentifierId)).uniqueResult();
	}
	
	/**
	 * Locked Reports are stored as {@link ReportData} object
	 * 
	 * @param regionId
	 * @param districtId
	 * @param facilityId
	 * @param year
	 * @param quarter
	 * @param month
	 * @param reportDate
	 * @param tableData
	 * @param reportStatus
	 * @param reportName
	 * @param reportType
	 */
	public void lockReport(Integer regionId, Integer districtId, Integer facilityId, Integer year, Integer quarter,
	        Integer month, Date reportDate, String tableData, boolean reportStatus, String reportName, ReportType reportType)
	        throws Exception {
		log.debug("Save as PDF:" + regionId + ", " + districtId + ", " + facilityId + ", " + year + ", " + reportDate + ", "
		        + tableData + ", " + reportName + ", " + reportType);
		Integer status = reportStatus ? 1 : 0;
		ReportData reportData = new ReportData();
		reportData.setRegion(Context.getLocationService().getLocation(regionId));
		reportData.setDistrict(Context.getLocationService().getLocation(districtId));
		reportData.setFacility(Context.getLocationService().getLocation(facilityId));
		reportData.setYear(year);
		reportData.setQuarter(quarter);
		reportData.setMonth(month);
		reportData.setReportDate(reportDate);
		reportData.setReportName(reportName);
		reportData.setReportType(reportType);
		reportData.setReportStatus(status);
		reportData.setTableData(tableData);
		saveReportData(reportData);
	}
	
	public int countPDFRows() {
		Session session = sessionFactory.getCurrentSession();
		int size = session.createCriteria(ReportData.class).add(Restrictions.eq("reportType", ReportType.MDRTB)).list()
		        .size();
		return size;
	}
	
	public List<String> getReportDataColumns() {
		return Arrays.asList("report_id", "region_id", "district_id", "facility_id", "report_name", "year", "quarter",
		    "month", "report_date", "report_status");
	}
	
	/**
	 * Fetch all reports and create a nested list for each column
	 * 
	 * @param reportType
	 * @return
	 */
	public List<List<Object>> getReports(String reportType) {
		String sql = "select report_id, region_id, district_id, facility_id, report_name, year, quarter, month, report_date, report_type, report_status from report_data where report_type = "
		        + reportType;
		List<List<Object>> list = Context.getAdministrationService().executeSQL(sql, true);
		return list;
	}
	
	@SuppressWarnings({ "unchecked" })
	public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String reportName, String date, String reportType) {
		String sql = "select table_data from report_data "
		        + processReportFilters(oblast, district, facility, year, quarter, month, reportName, reportType);
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<String> list = (List<String>) session.createSQLQuery(sql.toString()).list();
		return list;
	}
	
	public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String date, String reportType) {
		String sql = "delete from report_data "
		        + processReportFilters(oblast, district, facility, year, quarter, month, name, reportType);
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.createSQLQuery(sql).executeUpdate();
		session.getTransaction().commit();
	}
	
	public String processReportFilters(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String reportType) {
		StringBuffer sb = new StringBuffer();
		sb.append("where report_type='" + reportType + "' ");
		sb.append("".equals(name) ? "" : "and report_name='" + name + "' ");
		sb.append(oblast == null ? "and oblast_id IS NULL " : " and oblast_id='" + oblast + "' ");
		sb.append(district == null ? "and district_id IS NULL " : " and district_id='" + district + "' ");
		sb.append(facility == null ? "and facility_id IS NULL " : " and facility_id='" + facility + "' ");
		sb.append(year == null ? "and year IS NULL " : " and year='" + year + "' ");
		if (quarter == null) {
			sb.append("and quarter IS NULL ");
		} else if (quarter.equals(Context.getMessageSourceService().getMessage("mdrtb.annual"))) {
			sb.append("and quarter = '' ");
		} else {
			sb.append("and quarter = '" + quarter + "' ");
		}
		sb.append(month == null ? "and month IS NULL " : " and month='" + month + "' ");
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String reportType) {
		String sql = "select report_status from report_data "
		        + processReportFilters(oblast, district, facility, year, quarter, month, name, reportType);
		
		Session session = sessionFactory.getCurrentSession();
		//		session.beginTransaction();
		List<String> statusList = (List<String>) session.createSQLQuery(sql).list();
		List<String> list = new PDFHelper().byteToStrArray(statusList.toString());
		boolean reportStatus = false;
		if (list.size() > 0) {
			try {
				reportStatus = Integer.parseInt(list.get(0)) == 1;
			}
			catch (Exception e) {}
		}
		//		session.getTransaction().commit();
		return reportStatus;
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return getEncountersByEncounterTypes(encounterTypeNames, null, null, null);
	}
	
	@SuppressWarnings("unchecked")
	/* TODO: Remove unused closeDate parameter */
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Integer> encounterIds = new ArrayList<Integer>();
		List<Integer> tempList = new ArrayList<Integer>();
		String sql = "";
		Session session = sessionFactory.getCurrentSession();
		for (String encounterTypeName : encounterTypeNames) {
			sql = "select e.encounter_id from encounter e inner join encounter_type et where e.encounter_type=et.encounter_type_id and et.name='"
			        + encounterTypeName + "' and e.voided=0";
			
			if (startDate != null && endDate != null) {
				sql += " and e.encounter_datetime between '" + dbDateFormat.format(startDate) + "' and '"
				        + dbDateFormat.format(endDate) + "'";
			}
			sql += ";";
			tempList = (List<Integer>) session.createSQLQuery(sql).list();
			
			for (Integer encounterId : tempList) {
				if (!(encounterIds.contains(encounterId))) {
					encounterIds.add(encounterId);
				}
			}
		}
		
		List<Encounter> encounters = new ArrayList<Encounter>();
		Encounter encounter = new Encounter();
		for (Integer encounterId : encounterIds) {
			encounter = Context.getEncounterService().getEncounter(encounterId);
			encounters.add(encounter);
		}
		return encounters;
	}
	
	public void evict(Object obj) {
		sessionFactory.getCurrentSession().evict(obj);
	}
	
	/**
	 * Fetches Locations by their respective level of hierarchy
	 * 
	 * @param level
	 * @return
	 */
	public List<BaseLocation> getLocationsByHierarchyLevel(LocationHierarchy level) {
		Map<LocationAttributeType, Object> attributeValues = new HashMap<LocationAttributeType, Object>();
		LocationAttributeType key = Context.getLocationService().getLocationAttributeTypeByName(
		    MdrtbConstants.LOCATION_ATTRIBUTE_TYPE_LEVEL);
		attributeValues.put(key, level.toString());
		List<Location> list = Context.getLocationService().getLocations(null, null, attributeValues, false, null, null);
		List<BaseLocation> locations = new ArrayList<BaseLocation>();
		for (Location location : list) {
			locations.add(new BaseLocation(location, level));
		}
		return locations;
	}
	
	public List<BaseLocation> getLocationsByParent(BaseLocation parent) {
		List<BaseLocation> list = new ArrayList<>();
		LocationHierarchy childLevel;
		switch (parent.getLevel()) {
			case REGION:
				childLevel = LocationHierarchy.SUBREGION;
				break;
			case SUBREGION:
				childLevel = LocationHierarchy.DISTRICT;
				break;
			case DISTRICT:
				childLevel = LocationHierarchy.FACILITY;
				break;
			default:
				childLevel = LocationHierarchy.FACILITY;
		}
		for (Location location : parent.getChildLocations()) {
			list.add(new BaseLocation(location, childLevel));
		}
		return list;
	}
}
