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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	 */
	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException {
		String query = "select distinct location from PatientProgram where voided = false";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}
	
	/**
	 */
	@Deprecated
	public List<String> getAllRayonsTJK() throws DAOException {
		List<BaseLocation> list = getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		List<String> names = new ArrayList<>();
		for (BaseLocation location : list) {
			names.add(location.getName());
		}
		return names;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort patients, List<Concept> drugConcepts) throws DAOException {
		Map<Integer, List<DrugOrder>> ret = new HashMap<>();
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
				list = new ArrayList<>();
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
	 * Fetch all reports and create a nested list for each column
	 */
	public List<List<Object>> getReports(String reportType) {
		String sql = "select report_id, region_id, district_id, facility_id, report_name, year, quarter, month, report_date, report_type, report_status from report_data where report_type = "
		        + reportType;
		List<List<Object>> list = Context.getAdministrationService().executeSQL(sql, true);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReportData> searchReportData(Location region, Location district, Location facility, Integer year, Integer quarter,
	        Integer month, String reportName, ReportType reportType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ReportData.class);
		criteria.add(Restrictions.eq("year", year));
		if (reportType != null) {
			criteria.add(Restrictions.eq("reportType", reportType));
		}
		if (reportName != null) {
			criteria.add(Restrictions.eq("reportName", reportName));
		}
		if (quarter != null) {
			criteria.add(Restrictions.eq("quarter", quarter));
		} else if (month != null) {
			criteria.add(Restrictions.eq("month", month));
		}
		List<Location> locationList = new ArrayList<>();
		boolean regionProvided = region != null;
		boolean districtProvided = district != null;
		boolean facilityProvided = facility != null;
		// If Facility is also provided, then return only the list for that facility
		if (facilityProvided) {
			locationList.add(facility);
		}
		// If Region and District are provided, then the specific Region, District and all facilities will be fetched
		else if (districtProvided) {
			locationList.add(district);
			// If District is provided, then retrieve all its children
			BaseLocation parent = new BaseLocation(district, LocationHierarchy.DISTRICT);
			List<BaseLocation> facilities = getLocationsByParent(parent);
			for (BaseLocation f : facilities) {
				locationList.add(f);
			}
		}
		// If only Region is provided, then Region, and its entire tree of children and grandchildren
		else if (regionProvided) {
			locationList.add(district);
			// O boy! We're in for a lengthy stride...
			BaseLocation parent = new BaseLocation(region, LocationHierarchy.REGION);
			List<BaseLocation> districts = getLocationsByParent(parent);
			for (BaseLocation d : districts) {
				locationList.add(d);
				List<BaseLocation> facilities = getLocationsByParent(d);
				for (BaseLocation f : facilities) {
					locationList.add(f);
				}
			}
		}		
		criteria.add(Restrictions.in("location", locationList.toArray()));
		return criteria.list();
	}
	
	public List<String> getReportDataAsList(Integer regionId, Integer districtId, Integer facilityId, Integer year, Integer quarter,
	        Integer month, String reportName, ReportType reportType) {
		Location region = regionId != null ? Context.getLocationService().getLocation(regionId) : null;
		Location district = districtId != null ? Context.getLocationService().getLocation(districtId) : null;
		Location facility = facilityId != null ? Context.getLocationService().getLocation(facilityId) : null;
		List<ReportData> reports = searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
		List<String> list = new ArrayList<>();
		for (ReportData report : reports) {
            try {
				list.add(report.getTableData());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
        }
		return list;
	}
	
	public boolean getReportArchived(Integer regionId, Integer districtId, Integer facilityId, Integer year,
	        Integer quarter, Integer month, String reportName, ReportType reportType) {
		Location region = regionId != null ? Context.getLocationService().getLocation(regionId) : null;
		Location district = districtId != null ? Context.getLocationService().getLocation(districtId) : null;
		Location facility = facilityId != null ? Context.getLocationService().getLocation(facilityId) : null;
		List<ReportData> reports = searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
		return !reports.isEmpty();
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return getEncountersByEncounterTypes(encounterTypeNames, null, null, null);
	}
	
	@SuppressWarnings("unchecked")
	/* TODO: Remove unused closeDate parameter */
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Integer> encounterIds = new ArrayList<>();
		List<Integer> tempList = new ArrayList<>();
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
		
		List<Encounter> encounters = new ArrayList<>();
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
	 */
	public List<BaseLocation> getLocationsByHierarchyLevel(LocationHierarchy level) {
		Map<LocationAttributeType, Object> attributeValues = new HashMap<>();
		LocationAttributeType key = Context.getLocationService().getLocationAttributeTypeByName(
		    MdrtbConstants.LOCATION_ATTRIBUTE_TYPE_LEVEL);
		attributeValues.put(key, level.toString());
		List<Location> list = Context.getLocationService().getLocations(null, null, attributeValues, false, null, null);
		List<BaseLocation> locations = new ArrayList<>();
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
