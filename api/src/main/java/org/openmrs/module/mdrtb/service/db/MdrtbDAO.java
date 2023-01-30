package org.openmrs.module.mdrtb.service.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.BaseLocation;

public interface MdrtbDAO {
	
	/**
	 * @return all Locations which have non-voided Patient Programs associated with them
	 */
	List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException;
	
	@Deprecated
	List<String> getAllRayonsTJK();
	
	Map<Integer, List<DrugOrder>> getDrugOrders(Cohort patients, List<Concept> drugConcepts) throws DAOException;
	
	PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId);
	
	int countPDFRows();
	
	List<List<Integer>> getPDFData(String reportType);
	
	ArrayList<String> getPDFColumns();
	
	void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String reportDate, String tableData, boolean reportStatus, String reportName, String reportType);
	
	boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String reportType);
	
	List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String date, String reportType);
	
	void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String date, String reportType);
	
	List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames);
	
	List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate);
	
	void evict(Object obj);
	
	BaseLocation getAddressHierarchyLocation(Integer locationId);
	
	List<BaseLocation> getAddressHierarchyLocationsByHierarchyLevel(Integer level);
	
	BaseLocation getAddressHierarchyLocationParent(BaseLocation child);
	
	List<BaseLocation> getAddressHierarchyLocationsByParent(BaseLocation parent);
}
