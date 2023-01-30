/**
 * 
 */
package org.openmrs.module.mdrtb;

import org.openmrs.Location;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class BaseLocation extends Location {
	
	private static final long serialVersionUID = 1L;
	
	private LocationHierarchy level;
	
	public BaseLocation(Location location, LocationHierarchy levelName) {
		this.level = levelName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return super.getName();
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return super.getId();
	}
	
	public String toString() {
		return getName();
	}
	
	/**
	 * @return the level
	 */
	public LocationHierarchy getLevel() {
		return level;
	}
	
	/**
	 * @param level the level to set
	 */
	public void setLevelName(LocationHierarchy level) {
		this.level = level;
	}
}
