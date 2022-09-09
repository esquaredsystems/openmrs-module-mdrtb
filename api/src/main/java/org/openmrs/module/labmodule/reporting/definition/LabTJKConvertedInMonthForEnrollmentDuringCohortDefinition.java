package org.openmrs.module.labmodule.reporting.definition;

import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.DotsTJKConvertedInMonthForEnrollmentDuringCohortDefinition")
public class LabTJKConvertedInMonthForEnrollmentDuringCohortDefinition extends ProgramEnrollmentCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
	@ConfigurationProperty
	private Integer treatmentMonth;
	
	
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public LabTJKConvertedInMonthForEnrollmentDuringCohortDefinition() {
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
	
	
    
	public void setTreatmentMonth(Integer treatmentMonth) {
	    this.treatmentMonth = treatmentMonth;
    }

	public Integer getTreatmentMonth() {
	    return treatmentMonth;
    }

	
}
