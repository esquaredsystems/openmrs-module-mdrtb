package org.openmrs.module.mdrtb.specimen.custom;

import org.openmrs.Concept;
import org.openmrs.module.mdrtb.specimen.Bacteriology;

/**
 * Interface that defines how to interaction with a smear
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface HAIN2 extends Bacteriology {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * bacilli: the bacilli count
	 * method: the Concept that represents the method used when performing the test
	 * 
	 */
	
	public Concept getResult();
	public void setResult(Concept mtbResult);
	
	public Concept getInhResistance();
	public void setInhResistance(Concept inhResistance);
	
	public Concept getRifResistance();
	public void setRifResistance(Concept rifResistance);
	
	public String getErrorCode();
	public void setErrorCode(String code);

	public Concept getMtbBurden();
	public void setMtbBurden(Concept mtbBurden);
	
	
}
