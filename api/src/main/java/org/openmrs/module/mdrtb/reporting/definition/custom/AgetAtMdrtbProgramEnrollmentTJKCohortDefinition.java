package org.openmrs.module.mdrtb.reporting.definition.custom;

import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("dotsreports.reporting.AgetAtDotsProgramEnrollmentTJKCohortDefinition")
public class AgetAtMdrtbProgramEnrollmentTJKCohortDefinition extends ProgramEnrollmentCohortDefinition {
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
	@ConfigurationProperty
	private Integer minAge;
	
	@ConfigurationProperty
	private Integer maxAge;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public AgetAtMdrtbProgramEnrollmentTJKCohortDefinition() {
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
	public Integer getMinAge() {
		return minAge;
	}
	
	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	
	public Integer getMaxAge() {
		return maxAge;
	}
	
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	
}
