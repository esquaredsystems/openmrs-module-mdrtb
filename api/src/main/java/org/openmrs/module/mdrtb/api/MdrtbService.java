/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConfig;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.form.custom.AdverseEventsForm;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.DrugResistanceDuringTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.TransferOutForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.springframework.transaction.annotation.Transactional;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface MdrtbService extends OpenmrsService {
	
	/**
	 * Returns an item by uuid. It can be called by any authenticated user. It is fetched in read
	 * only transaction.
	 * 
	 * @param uuid
	 * @return
	 * @throws APIException
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ReportData getReportDataByUuid(String uuid) throws APIException;
	
	/**
	 * Saves an item. Sets the owner to superuser, if it is not set. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 * 
	 * @param reportData
	 * @return
	 * @throws APIException
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional
	ReportData saveReportData(ReportData reportData) throws APIException;
	
	/**
	 * Returns the Concept specified by the passed lookup string. Checks MdrtbConcepts mapping, id,
	 * name, and uuid before returning null
	 * 
	 * @param lookup
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Concept getConcept(String lookup);
	
	/**
	 * Returns collection of {@link ConceptAnswer} objects against a question concept passed as
	 * string
	 * 
	 * @param conceptQuestion
	 * @return
	 */
	//TODO: Remove this. Simply use getConcept().getConceptAnswers()
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	@Deprecated
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion);
	
	/**
	 * Check to see what color to associate with a given result concept
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public String getColorForConcept(Concept concept);
	
	/**
	 * Resets the concept map cache
	 */
	public void resetConceptMapCache();
	
	/**
	 * Resets the color map cache to null to force cache reload
	 */
	public void resetColorMapCache();
	
	/**
	 * Returns list of {@link Patient} objects by Ids
	 * 
	 * @param patientIds
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Patient> getPatients(Collection<Integer> patientIds);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Patient> getAllPatientsWithRegimenForms();
	
	/**
	 * Returns list of all users with Provider role
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<Person> getProviders();
	
	/**
	 * Returns {@link PatientState} by Id. This is because since Openmrs 2.x the getById method was
	 * removed
	 * 
	 * @param stateId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public PatientState getPatientState(Integer stateId);
	
	/**
	 * Returns all DrugOrders of given {@link Patient}
	 * 
	 * @param patient
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<DrugOrder> getDrugOrders(Patient patient);
	
	/**
	 * Returns map of Patients with their respective {@link DrugOrder} lists
	 * 
	 * @param cohort
	 * @param drugSet
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet);
	
	/**
	 * Save {@link DrugOrder}
	 * 
	 * @param drugOrder
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public DrugOrder saveDrugOrder(DrugOrder drugOrder);
	
	/**
	 * Returns all of the Drug Concepts within the ConceptSet which match the parameter
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Concept> getDrugsInSet(Concept concept);
	
	/**
	 * Returns all the possible Drug Concepts to display in a DST result, in the order we want to
	 * display
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Concept> getMdrtbDrugs();
	
	/**
	 * Returns all the possible Antiretroviral Concepts
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Concept> getAntiretrovirals();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	        Date startDate, Date endDate);
	
	/**
	 * Gets the DOTS program
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Program getTbProgram();
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId);
	
	/**
	 * Returns all the DOTS programs for a given patient
	 * 
	 * @param patient
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient);
	
	/**
	 * Returns all the MDR-TB programs in the system
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms();
	
	/**
	 * Returns all the MDR-TB programs in the system that were active during a specific date range
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate);
	
	/**
	 * Returns all MDRTB Patient Programs enrolled within given range
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	        Date startDate, Date endDate);
	
	/**
	 * Returns all the MDR-TB programs for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date
	 * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time) If the
	 * date is before any program enrollments, it returns the first program enrollment If the date
	 * is after all program enrollments, it returns the most recent program enrollment If the date
	 * is between two program enrollments, it returns the later of the two
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date);
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient);
	
	/**
	 * Returns {@link PatientIdentifier} object from {@link PatientProgram}
	 * 
	 * @param patientProgram
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public PatientIdentifier getPatientProgramIdentifier(PatientProgram patientProgram);
	
	/**
	 * Returns {@link PatientIdentifier} object from {@link MdrtbPatientProgram}
	 * 
	 * @param mdrPatientProgram
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public PatientIdentifier getPatientMdrtbProgramIdentifier(MdrtbPatientProgram mdrPatientProgram);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Program getMdrtbProgram();
	
	/**
	 * Replacing the deprecated method in {@link ProgramWorkflowService} to return
	 * {@link ProgramWorkflow} by {@link Concept}
	 * 
	 * @param program
	 * @param conceptId
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public ProgramWorkflow getProgramWorkflow(Program program, Integer conceptId);
	
	/**
	 * Replacing the deprecated method in {@link ProgramWorkflowService} to return
	 * {@link ProgramWorkflowState} by {@link ProgramWorkflow} and {@link Concept}
	 * 
	 * @param programWorkflow
	 * @param conceptId
	 * @return
	 * @throws APIException
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public ProgramWorkflowState getProgramWorkflowState(ProgramWorkflow programWorkflow, Integer conceptId)
	        throws APIException;
	
	/**
	 * Returns a specific ProgramWorkflowState, given the concept associated with the state
	 * 
	 * @param programWorkflowStateConcept
	 * @return {@link ProgramWorkflowState}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept);
	
	/**
	 * Returns set of {@link ProgramWorkflowState} objects according to any of the Patient group
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups();
	
	/**
	 * Returns set of {@link ProgramWorkflowState} objects according to Past Drug
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
	
	/**
	 * Returns all possible MDR-TB previous drug use classifications
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse();
	
	/**
	 * Returns all possible MDR-TB previous treatment classifications
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment();
	
	/**
	 * Returns all possible outcomes for the DOTS program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes();
	
	/**
	 * Returns all possible outcomes for the MDR-TB program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes();
	
	/**
	 * Creates a new specimen, associated with the given patient
	 */
	public Specimen createSpecimen(Patient patient);
	
	/**
	 * Fetches a specimen sample obj given a specimen id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Specimen getSpecimen(Integer specimedId);
	
	/**
	 * Fetches a specimen sample obj given an encounter of the Specimen Collection type
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Specimen getSpecimen(Encounter encounter);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Specimen> getSpecimens(Patient patient);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters) in a given
	 * program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Specimen> getSpecimens(Patient patient, Integer programId);
	
	/**
	 * Fetches all specimens within a certain data range
	 * 
	 * @param patient: only include specimens associated with this patient
	 * @param startDate: only include specimens with a date collected after (or equal to) this start
	 *            date
	 * @param endDate: only include specimens with a date collected before (or equal to) this end
	 *            date All parameters can be set to null
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected);
	
	/**
	 * Fetches all specimens within a certain data range and from a certain lab
	 * 
	 * @param patient: only include specimens associated with this patient
	 * @param startDate: only include specimens with a date collected after (or equal to) this start
	 *            date
	 * @param endDate: only include specimens with a date collected before (or equal to) this end
	 *            date
	 * @param location: only include specimens collected from the specified location All parameters
	 *            can be set to null
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	        Location locationCollected);
	
	/**
	 * Saves or updates a specimen object
	 * 
	 * @param specimen
	 */
	public void saveSpecimen(Specimen specimen);
	
	/**
	 * Deletes a smear, culture, or DST test
	 */
	public void deleteTest(Integer testId);
	
	/**
	 * Deletes a specimen, referenced by specimen Id
	 */
	public void deleteSpecimen(Integer patientId);
	
	/**
	 * Search for encounters by various parameters
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end,
	        Collection<EncounterType> types);
	
	/**
	 * Search for encounters by Patient and Encounter Types
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types);
	
	//TODO: Get rid of this
	@Deprecated
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate);
	
	/**
	 * Search for encounters of given {@link EncounterType} and {@link Patient} having no program
	 * attached
	 */
	//TODO: Rename to getEncountersWithNoProgram
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getEncountersWithNoProgramId(EncounterType encounterType, Patient patient);
	
	/**
	 * Attach given program Id to an encounter Id
	 * 
	 * @param encounterId
	 * @param programId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void addProgramIdToEncounter(Integer encounterId, Integer programId);
	
	/**
	 * Attach given Patient identifier to Program
	 * 
	 * @param patientIdenifierId
	 * @param patientProgramId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId);
	
	/**
	 * Gets all TB specific encounters for the given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getTbEncounters(Patient patient);
	
	/**
	 * Gets all MDR-TB specific encounters for the given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Encounter> getMdrtbEncounters(Patient patient);
	
	/**
	 * Fetches an GeneXpert test against the given obsId
	 * 
	 * @param obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Xpert getXpert(Integer obsId);
	
	/**
	 * Fetches an GeneXpert test against the given {@link Obs}
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	@Deprecated
	public Xpert getXpert(Obs obs);
	
	/**
	 * Creates a new GeneXpert test, associated with the given encounter
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Xpert createXpert(Specimen specimen);
	
	/**
	 * Saves a GeneXpert test in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveXpert(Xpert xpert);
	
	/**
	 * Fetches a HAIN test against given obsId
	 * 
	 * @param obsId
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public HAIN getHAIN(Integer obsId);
	
	/**
	 * Fetches a HAIN test against given {@link Obs}
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	@Deprecated
	public HAIN getHAIN(Obs obs);
	
	/**
	 * Creates a new HAIN, associated with the given encounter
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public HAIN createHAIN(Specimen specimen);
	
	/**
	 * Saves a HAIN in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public void saveHAIN(HAIN hain);
	
	/**
	 * Fetches a HAIN2 test against given obsId
	 * 
	 * @param obsId
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public HAIN2 getHAIN2(Integer obsId);
	
	/**
	 * Fetches a HAIN2 test against given obsId
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	@Deprecated
	public HAIN2 getHAIN2(Obs obs);
	
	/**
	 * Creates a new HAIN2, associated with the given encounter
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public HAIN2 createHAIN2(Specimen specimen);
	
	/**
	 * Saves a HAIN2 in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveHAIN2(HAIN2 hain);
	
	/**
	 * Fetches a culture given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Culture getCulture(Obs obs);
	
	/**
	 * Fetches a culture given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Culture getCulture(Integer obsId);
	
	/**
	 * Creates a new culture, associated with the given specimen
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Culture createCulture(Specimen specimen);
	
	/**
	 * Creates a new culture, associated with the given specimen, by copying the member properties
	 * of the given culture
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Culture createCulture(Specimen specimen, Culture culture);
	
	/**
	 * Saves a culture in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveCulture(Culture culture);
	
	/**
	 * Creates a new dst, associated with the given specimen
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Dst createDst(Specimen specimen);
	
	/**
	 * Creates a new dst, associated with the given specimen, by copying the member properties of
	 * the given dst
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Dst createDst(Specimen specimen, Dst dst);
	
	/**
	 * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Deprecated
	public Smear getSmear(Obs obs);
	
	/**
	 * Fetches a smear given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Smear getSmear(Integer obsId);
	
	/**
	 * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Deprecated
	public Dst getDst(Obs obs);
	
	/**
	 * Fetches a dst given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Dst getDst(Integer obsId);
	
	/**
	 * Saves a DST in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveDst(Dst dst);
	
	/**
	 * Creates a new Smear, associated with the given specimen
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Smear createSmear(Specimen specimen);
	
	/**
	 * Creates a new Smear, associated with the given specimen, by copying the member properties of
	 * the given smear
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public Smear createSmear(Specimen specimen, Smear smear);
	
	/**
	 * Saves a smear in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveSmear(Smear smear);
	
	/***************/
	/** LOCATIONS **/
	/***************/
	/**
	 * Find the {@link Location} object mapped with given parameters
	 * 
	 * @param regionId
	 * @param districtId
	 * @param facilityId
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Location getLocation(Integer regionId, Integer districtId, Integer facilityId);
	
	/**
	 * Get list of ISO countries
	 * 
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<String> getCountries();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Region> getRegions();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Region getRegion(Integer regionId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getLocationsFromRegion(Region region);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Facility> getFacilities();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Facility> getRegFacilities();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Facility> getFacilitiesByParent(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Facility> getRegFacilities(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Facility getFacility(Integer facilityId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getLocationsFromFacility(Facility facility);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<District> getDistrictsByParent(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<District> getRegDistricts(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public District getDistrict(Integer districtId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public District getDistrict(String name);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<District> getDistricts();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<District> getRegDistricts();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getLocationsFromDistrict(District district);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getEnrollmentLocations();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getCultureLocations();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getLocationsInHierarchy(Location parent);
	
	/**
	 * Should return the list of child locations based on the parameters. If only the region is
	 * supplied, then the list of all its children as well as grand children should be returned.
	 * 
	 * @param region
	 * @param district
	 * @param facility
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Location> getLocations(Region region, District district, Facility facility);
	
	public List<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId);
	
	/*****************/
	/** ANSWER SETS **/
	/*****************/
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleMtbResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRifResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleInhResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleFqResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleInjResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleAnatomicalSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCultureResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCultureMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleDstMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleOrganismTypes();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSmearResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSmearMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSpecimenTypes();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRegimens();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleHIVStatuses();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Collection<Concept> getPossibleDstResults();
	
	/***********/
	/** FORMS **/
	/***********/
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<SmearForm> getSmearForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<CultureForm> getCultureForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<XpertForm> getXpertForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<HAINForm> getHAINForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<HAIN2Form> getHAIN2Forms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<DSTForm> getDstForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public TB03uForm getTB03uFormForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<RegimenForm> getRegimenFormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TB03Form> getTB03FormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Form89> getForm89FormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Form89> getForm89FormsFilledForPatientProgram(Patient patient, Location location, Integer patientProgramId,
	        Integer year, String quarter, String month);
	
	//TODO: Rename to getPreviousRegimenForm
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public RegimenForm getPreviousRegimenFormForPatient(Patient patient, List<Location> locactions, Date beforeDate);
	
	//TODO: Rename to getPreviousRegimenForm
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public RegimenForm getCurrentRegimenFormForPatient(Patient patient, Date beforeDate);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TB03uForm> getTB03uFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<Form89> getForm89FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TB03Form> getTB03FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TransferInForm> getTransferInFormsFilled(List<Location> locations, Integer year, String quarter, String month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TransferInForm> getTransferInFormsFilledForPatient(Patient patient);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TransferOutForm> getTransferOutFormsFilled(List<Location> locations, Integer year, String quarter,
	        String month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TransferOutForm> getTransferOutFormsFilledForPatient(Patient patient);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TB03uForm> getTB03uFormsWithTreatmentStartedDuring(List<Location> locations, Integer year, String quarter,
	        String month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<TB03uForm> getTB03uFormsForProgram(Patient patient, Integer mdrtbProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<AdverseEventsForm> getAEFormsForProgram(Patient patient, Integer patientProgId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<AdverseEventsForm> getAEFormsFilled(List<Location> locations, Integer year, String quarter, String month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<RegimenForm> getRegimenFormsFilled(List<Location> locations, Integer year, String quarter, String month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void evict(Object obj);
	
	/**
	 * Returns Cohort of patients by {@link Program} and {@link ProgramWorkflowState} between given
	 * date range
	 * 
	 * @param program
	 * @param stateList
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate,
	        Date toDate);
	
	/**
	 * Handles exiting a patient from care
	 * 
	 * @param patient
	 * @param deathDate
	 * @param causeOfDeath
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath);
	
	public void unlockReport(Integer oblastId, Integer districtId, Integer facilityId, Integer year, String quarter,
	        String month, String name, String date, String type);
	
	//TODO: Change the integers to objects
	public boolean readReportStatus(Integer oblastId, Integer districtId, Integer facilityId, Integer year, String quarter,
	        String month, String name, String type);
	
	public List<String> readTableData(Integer oblastId, Integer districtId, Integer facilityId, Integer year,
	        String quarter, String month, String name, String date, String reportType);
	
	/**
	 * Fetch all reports and create a nested list for each column
	 * 
	 * @param reportType
	 * @return List of List<Objects> with objects in exactly the order: reportid, region, district,
	 *         facility, reportName, year, quarter, month, reportDate, reportType, reportStatus
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public List<List<Object>> getReportsWithoutData(ReportType reportType);
	
	public void lockReport(Region region, District district, Facility facility, Integer year, Integer quarter,
	        Integer month, Date reportDate, String tableData, boolean reportStatus, String reportName, ReportType reportType);
	
	/**
	 * Saves a scanned lab report in the appropriate obs constructs
	 * 
	 * @param report
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	public void saveScannedLabReport(ScannedLabReport report);
}
