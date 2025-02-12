/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb.reporting.definition.custom;

import java.util.Date;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("mdrtb.reporting.TestReferralCohortDefinition")
public class TestReferralCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;
    
	@ConfigurationProperty(group="startDateGroup")
	private Date fromDate;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date toDate;
	
	private String testType;
	
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public TestReferralCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the minResultDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param minResultDate the minResultDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the maxResultDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param maxResultDate the maxResultDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	
}
