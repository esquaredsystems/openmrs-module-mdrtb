package org.openmrs.module.mdrtb.specimen;

import java.util.Date;

import org.openmrs.Location;

/**
 * Interface that defines how to interaction with a tests on a specimen
 * (i.e., smears, cultures, and dsts)
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface MdrtbTest extends Comparable<MdrtbTest> {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * test: the actual object that stores the data this interface is providing access to (i.e., in our current implementation, the test construct obsgroup)
	 * id: the id used to reference the test (i.e., in our current implementation, the obs_id of the test construct obs for this test)
	 * type: the type of the test (smear,culture,dst)
	 * specimen: the id used to reference the specimen (i.e., in our current implementation, the encounter_id of the parent encounter)
	 * status: the current status of the test (i.e., ordered, received, completed, etc)
	 *
	 * accessionNumber: the accession number (usually a reference # supplied by the lab)
	 * startDate: the date the test was startd
	 * resultDate: the date of the result
	 * dateOrdered: the date the test was ordered
	 * TODO: dateReceived: the date the specimen was received (by the lab?)
	 * lab: the lab that performed the smear
	 * 
	 */
	
	public Object getTest();
	public String getId(); 
	public String getStatus();	
	public String getTestType();
	public String getSpecimenId();
	
	public String getAccessionNumber();
	public void setAccessionNumber(String accessionNumber);
	
	public Date getStartDate();
	public void setStartDate(Date startDate);
	
	public Date getResultDate();
	public void setResultDate(Date resultDate);
	
	public Date getDateOrdered();
	public void setDateOrdered(Date dateOrdered);
	
	public Date getDateReceived();
	public void setDateReceived(Date dateReceived);
	
	public Location getLab();
	public void setLab(Location location);
	
	public String getComments();
	public void setComments(String comments);
}
