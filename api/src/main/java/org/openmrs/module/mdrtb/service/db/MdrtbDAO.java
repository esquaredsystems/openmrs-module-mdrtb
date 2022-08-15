package org.openmrs.module.mdrtb.service.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.db.DAOException;

public interface MdrtbDAO {

    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException;
    
    public List<String> getAllRayonsTJK();
    
    public PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId);
  
    public int countPDFRows();
    
    public int countPDFColumns();
    
    public List<List<Integer>> PDFRows(String reportType);
    
    public ArrayList<String> PDFColumns();
    
    public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String reportDate, String tableData, boolean reportStatus, String reportName, String reportType);
   
    public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String reportType);
    
    public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String date, String reportType);
    
    public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month, String name, String date, String reportType);
	
    public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames);
	
    public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate, Date closeDate);
	
	public void evict(Object obj);
}
