package org.openmrs.module.mdrtb.service;

import java.util.ArrayList;
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
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.Region;
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

//TODO: A ton of documentation missing. Complete...
//TODO: enable privileges for all methods
@Transactional
public interface MdrtbService extends OpenmrsService {
	
	/**
	 * @return all Locations which have non-voided Patient Programs associated with them
	 */
	@Transactional(readOnly = true)
	public List<Location> getLocationsWithAnyProgramEnrollments();
	
	/**
	 * @return the Concept specified by the passed lookup string. Checks MdrtbConcepts mapping, id,
	 *         name, and uuid before returning null
	 */
	public Concept getConcept(String lookup);
	
	/**
	 * Resets the concept map cache
	 */
	public void resetConceptMapCache();
	
	/**
	 * Search for encounters by various parameters
	 */
	@Transactional(readOnly = true)
	public List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end,
	        Collection<EncounterType> types);
	
	/**
	 * Search for encounters by Patient and Encounter Types
	 */
	@Transactional(readOnly = true)
	public List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types);
	
	/**
	 * Gets all TB specific encounters for the given patient
	 */
	@Transactional(readOnly = true)
	public List<Encounter> getTbEncounters(Patient patient);
	
	/**
	 * Gets all MDR-TB specific encounters for the given patient
	 */
	@Transactional(readOnly = true)
	public List<Encounter> getMdrtbEncounters(Patient patient);
	
	/**
	 * Returns all the MDR-TB programs in the system
	 */
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms();
	
	/**
	 * Returns all the mdrtb programs in the system that were active during a specific date range
	 */
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate);
	
	/**
	 * Returns all the mdrtb programs for a given patient
	 */
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date
	 * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time) If the
	 * date is before any program enrollments, it returns the first program enrollment If the date
	 * is after all program enrollments, it returns the most recent program enrollment If the date
	 * is between two program enrollments, it returns the later of the two
	 */
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date);
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Transactional(readOnly = true)
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId);
	
	/**
	 * Creates a new specimen, associated with the given patient
	 */
	public Specimen createSpecimen(Patient patient);
	
	/**
	 * Fetches a specimen sample obj given a specimen id
	 */
	public Specimen getSpecimen(Integer specimedId);
	
	/**
	 * Fetches a specimen sample obj given an encounter of the Specimen Collection type
	 */
	public Specimen getSpecimen(Encounter encounter);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
	 */
	public List<Specimen> getSpecimens(Patient patient);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters) in a given
	 * program
	 */
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
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	        Location locationCollected);
	
	/**
	 * Deletes a specimen, referenced by specimen Id
	 */
	public void deleteSpecimen(Integer patientId);
	
	/**
	 * Saves or updates a specimen object
	 */
	@Transactional
	public void saveSpecimen(Specimen specimen);
	
	/**
	 * Deletes a smear, culture, or dst test
	 */
	@Transactional
	public void deleteTest(Integer testId);
	
	/**
	 * Creates a new Smear, associated with the given specimen
	 */
	public Smear createSmear(Specimen specimen);
	
	/**
	 * Creates a new Smear, associated with the given specimen, by copying the member properties of
	 * the given smear
	 */
	public Smear createSmear(Specimen specimen, Smear smear);
	
	/**
	 * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	public Smear getSmear(Obs obs);
	
	/**
	 * Fetches a smear given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	public Smear getSmear(Integer obsId);
	
	/**
	 * Saves a smear in the approriate obs construct
	 */
	@Transactional
	public void saveSmear(Smear smear);
	
	/**
	 * Creates a new culture, associated with the given specimen
	 */
	public Culture createCulture(Specimen specimen);
	
	/**
	 * Creates a new culture, associated with the given specimen, by copying the member properties
	 * of the given culture
	 */
	public Culture createCulture(Specimen specimen, Culture culture);
	
	/**
	 * Fetches a culture given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	public Culture getCulture(Obs obs);
	
	/**
	 * Fetches a culture given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	public Culture getCulture(Integer obsId);
	
	/**
	 * Saves a culture in the approriate obs construct
	 */
	@Transactional
	public void saveCulture(Culture culture);
	
	/**
	 * Creates a new dst, associated with the given specimen
	 */
	public Dst createDst(Specimen specimen);
	
	/**
	 * Creates a new dst, associated with the given specimen, by copying the member properties of
	 * the given dst
	 */
	public Dst createDst(Specimen specimen, Dst dst);
	
	/**
	 * Fetches a dst given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	public Dst getDst(Obs obs);
	
	/**
	 * Fetches a dst given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	public Dst getDst(Integer obsId);
	
	/**
	 * Saves a dst in the appropriate obs construct
	 */
	@Transactional
	public void saveDst(Dst dst);
	
	/**
	 * Deletes a dst result
	 */
	@Transactional
	public void deleteDstResult(Integer dstResultId);
	
	/**
	 * Saves a scanned lab report in the appropriate obs constructs
	 */
	@Transactional
	public void saveScannedLabReport(ScannedLabReport report);
	
	/**
	 * Deletes a scanned lab report
	 */
	@Transactional
	public void deleteScannedLabReport(Integer reportId);
	
	/**
	 * Handles exiting a patient from care
	 */
	@Transactional
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath);
	
	/**
	 * Gets the MDR-TB program
	 */
	@Transactional(readOnly = true)
	public Program getMdrtbProgram();
	
	/**
	 * Gets the DOTS program
	 */
	@Transactional(readOnly = true)
	public Program getTbProgram();
	
	/**
	 * Gets the possible providers
	 */
	@Transactional(readOnly = true)
	public Collection<Person> getProviders();
	
	/**
	 * Returns all the concepts that are possible coded answers for the Tuberculosis Smear Test
	 * Result
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSmearResults();
	
	/**
	 * Returns all the concepts that are possible coded answers for the Tuberculosis Smear Method
	 * concept
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSmearMethods();
	
	/**
	 * Returns all the concepts that are possible coded answers for the Tuberculosis Culture Test
	 * Result
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCultureResults();
	
	/**
	 * Returns all the concepts that are possible coded answers for the Tuberculosis Culture Method
	 * concept
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCultureMethods();
	
	/**
	 * Returns all the concepts that are possible coded answers for the Tuberculosis Drug
	 * Sensitivity Test Method concept
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleDstMethods();
	
	/**
	 * Returns all the concepts that are possible Dst results
	 */
	@Transactional(readOnly = true)
	public Collection<Concept> getPossibleDstResults();
	
	/**
	 * Returns all the concepts that are possible coded answered for Type of Organism concept
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleOrganismTypes();
	
	/**
	 * Returns all the concepts that are possible coded answer for the Tuberculosis Sample Source
	 * concept
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSpecimenTypes();
	
	/**
	 * Returns all the concepts that are possible coded answers for the appearance of a sputum
	 * specimen
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances();
	
	/**
	 * Returns all possible TB Anatomical sites
	 */
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleAnatomicalSites();
	
	/**
	 * @return all of the Drugs within the ConceptSet which match the conceptMapKeys
	 */
	public List<Concept> getDrugsInSet(Concept concept);
	
	/**
	 * Returns all the possible drugs to display in a DST result, in the order we want to display
	 * them
	 */
	public List<Concept> getMdrtbDrugs();
	
	/**
	 * Returns all the possible antiretrovirals
	 */
	public List<Concept> getAntiretrovirals();
	
	/**
	 * Returns all possible outcomes for the MDR-TB program
	 */
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes();
	
	/**
	 * Returns all possible outcomes for the MDR-TB program
	 */
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes();
	
	/**
	 * Returns all possible TB patient groups
	 */
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups();
	
	/**
	 * Returns all possible MDR-TB previous drug use classifications
	 */
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse();
	
	/**
	 * Returns all possible MDR-TB previous treatment classifications
	 */
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment();
	
	/**
	 * Check to see what color to associate with a given result concept
	 */
	public String getColorForConcept(Concept concept);
	
	/**
	 * Resets the color map cache to null to force cache reload
	 */
	public void resetColorMapCache();
	
	@Transactional(readOnly = true)
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate);
	
	/**
	 * Saves a xpert in the approriate obs construct
	 */
	@Transactional
	public void saveXpert(Xpert xpert);
	
	/**
	 * Creates a new Xpert, associated with the given encounter
	 */
	public Xpert createXpert(Specimen specimen);
	
	/**
	 * Fetches an xpert given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	@Deprecated
	public Xpert getXpert(Obs obs);
	
	/**
	 * Fetches an xpert given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	public Xpert getXpert(Integer obsId);
	
	// HAIN
	
	/**
	 * Saves a hain in the approriate obs construct
	 */
	@Transactional
	public void saveHAIN(HAIN hain);
	
	/**
	 * Creates a new Xpert, associated with the given encounter
	 */
	public HAIN createHAIN(Specimen specimen);
	
	/**
	 * Fetches an xpert given the obs of a Tuberculosis Smear Test Construct
	 * 
	 * @param obs
	 * @return
	 */
	public HAIN getHAIN(Obs obs);
	
	/**
	 * Fetches an xpert given the obs_id of a Tuberculosis Smear Test Construct
	 * 
	 * @param obsId
	 */
	public HAIN getHAIN(Integer obsId);
	
	public HAIN2 getHAIN2(Obs obs);
	
	public HAIN2 createHAIN2(Specimen specimen);
	
	@Transactional
	public void saveHAIN2(HAIN2 hain);
	
	public HAIN2 getHAIN2(Integer obsId);
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleMtbResults();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRifResistanceResults();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleInhResistanceResults();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleFqResistanceResults();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleInjResistanceResults();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens();
	
	public List<Region> getRegions();
	
	public Region getRegion(Integer regionId);
	
	public List<Location> getLocationsFromRegion(Region region);
	
	public List<Facility> getFacilities();
	
	public List<Facility> getRegFacilities();
	
	/**
	 * Find the {@link Location} object mapped with given parameters
	 * 
	 * @param regionId
	 * @param districtId
	 * @param facilityId
	 * @return
	 */
	public Location getLocation(Integer regionId, Integer districtId, Integer facilityId);
	
	public List<Facility> getFacilitiesByParent(Integer parentId);
	
	public List<Facility> getRegFacilities(Integer parentId);
	
	public Facility getFacility(Integer facilityId);
	
	public List<Location> getLocationsFromFacility(Facility facility);
	
	public List<District> getDistrictsByParent(Integer parentId);
	
	public List<District> getRegDistricts(Integer parentId);
	
	public District getDistrict(Integer districtId);
	
	public District getDistrict(String name);
	
	public List<District> getDistricts();
	
	public List<District> getRegDistricts();
	
	public List<Location> getLocationsFromDistrict(District district);
	
	public List<Location> getEnrollmentLocations();
	
	public PatientIdentifier getPatientProgramIdentifier(MdrtbPatientProgram mpp);
	
	public PatientIdentifier getGenPatientProgramIdentifier(PatientProgram mpp);
	
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate);
	
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId);
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRegimens();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleHIVStatuses();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleResistanceTypes();
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion);
	
	// ADDED BY ZOHAIB
	public int countPDFRows();
	
	public List<List<Integer>> getPDFRows(String reportType);
	
	public ArrayList<String> getPDFColumns();
	
	public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String date, String reportType);
	
	/*
	 * public void savePDF(Integer oblast, String location, Integer year, Integer
	 * quarter, Integer month, String reportDate, String tableData, boolean
	 * reportStatus, String reportName);
	 */
	// public void savePDF(Integer oblast, String location, Integer year, Integer
	// quarter, Integer month, String reportDate, String tableData, boolean
	// reportStatus, String reportName, String reportType);
	public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String reportDate, String tableData, boolean reportStatus, String reportName, String reportType);
	
	// public boolean readReportStatus(Integer oblast, Integer location, Integer
	// year, Integer quarter, Integer month, String name, String type);
	public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String type);
	
	// public List<String> readTableData(Integer oblast, Integer location, Integer
	// year, Integer quarter, Integer month, String name, String date, String
	// reportType);
	public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String date, String reportType);
	
	// public List<String> readTableData(Integer oblast, Integer district, Integer
	// facility, Integer year, Integer quarter, Integer month, String name, String
	// date, String reportType);
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames);
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> reportEncounterTypes, Date startDate, Date endDate,
	        Date closeDate);
	
	///
	public List<SmearForm> getSmearForms(Integer patientProgramId);
	
	public List<CultureForm> getCultureForms(Integer patientProgramId);
	
	public List<XpertForm> getXpertForms(Integer patientProgramId);
	
	public List<HAINForm> getHAINForms(Integer patientProgramId);
	
	public List<HAIN2Form> getHAIN2Forms(Integer patientProgramId);
	
	public List<DSTForm> getDstForms(Integer patientProgramId);
	
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId);
	
	public List<Encounter> getEncountersWithNoProgramId(EncounterType ec, Patient p);
	
	public void addProgramIdToEncounter(Integer encounterId, Integer programId);
	
	public ArrayList<TB03Form> getTB03FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month);
	
	public ArrayList<TB03Form> getTB03FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month);
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month);
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month);
	
	public ArrayList<Form89> getForm89FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month);
	
	public ArrayList<Form89> getForm89FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month);
	
	public ArrayList<Form89> getForm89FormsFilledForPatientProgram(Patient p, Location location, Integer patProgId,
	        Integer year, String quarter, String month);
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month);
	
	public ArrayList<TransferInForm> getTransferInFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month);
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilledForPatient(Patient p);
	
	public ArrayList<TransferInForm> getTransferInFormsFilledForPatient(Patient p);
	
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
	
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient);
	
	public List<Location> getCultureLocations();
	
	public ArrayList<Location> getLocationList(Integer oblastId, Integer districtId, Integer facilityId);
	
	public PatientIdentifier getPatientIdentifierById(Integer id);
	
	public ArrayList<TB03uForm> getTB03uFormsFilledWithTxStartDateDuring(ArrayList<Location> locList, Integer year,
	        String quarter, String month);
	
	public List<Country> getCountries();
	
	public List<Region> getOblasts(Integer parentId);
	
	public ArrayList<TB03Form> getTB03FormsForProgram(Patient p, Integer patientProgId);
	
	public ArrayList<Form89> getForm89FormsForProgram(Patient p, Integer patientProgId);
	
	public void evict(Object obj);
	
	public TB03uForm getTB03uFormForProgram(Patient p, Integer patientProgId);
	
	public ArrayList<RegimenForm> getRegimenFormsForProgram(Patient p, Integer patientProgId);
	
	public ArrayList<RegimenForm> getRegimenFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month);
	
	public ArrayList<Patient> getAllPatientsWithRegimenForms();
	
	public RegimenForm getPreviousRegimenFormForPatient(Patient p, ArrayList<Location> locList, Date beforeDate);
	
	public RegimenForm getCurrentRegimenFormForPatient(Patient p, Date beforeDate);
	
	public ArrayList<AdverseEventsForm> getAEFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month);
	
	public ArrayList<AdverseEventsForm> getAEFormsForProgram(Patient p, Integer patientProgId);
	
	/**
	 * Location hierarchy for Dushanbe is explicitly handled
	 * 
	 * @param oblastId
	 * @param districtId
	 * @param facilityId
	 * @return
	 */
	public ArrayList<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId);
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList);
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList);
	
	public ArrayList<TB03uForm> getTB03uFormsForProgram(Patient p, Integer patientProgId);
	
	/**
	 * Returns all the DOTS programs in the system
	 */
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getAllTbPatientPrograms();
	
	/**
	 * Returns all the dots programs in the system that were active during a specific date range
	 */
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate);
	
	/**
	 * Returns all the dots programs for a given patient
	 */
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Transactional(readOnly = true)
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Transactional(readOnly = true)
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date
	 * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time) If the
	 * date is before any program enrollments, it returns the first program enrollment If the date
	 * is after all program enrollments, it returns the most recent program enrollment If the date
	 * is between two program enrollments, it returns the later of the two
	 */
	@Transactional(readOnly = true)
	public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date);
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Transactional(readOnly = true)
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId);
	
	/**
	 * Replacing the deprecated method in {@link ProgramWorkflowService} to return
	 * {@link ProgramWorkflow} by {@link Concept}
	 * 
	 * @param program
	 * @param conceptId
	 * @return
	 */
	public ProgramWorkflow getProgramWorkflow(Program program, Integer conceptId);
	
	public ProgramWorkflowState getProgramWorkflowState(ProgramWorkflow programWorkflow, Integer conceptId)
	        throws APIException;
	
	public List<DrugOrder> getDrugOrders(Patient patient);
	
	public DrugOrder saveDrugOrder(DrugOrder drugOrder);
	
	/**
	 * Returns a specific ProgramWorkflowState, given the concept associated with the state
	 * 
	 * @param programWorkflowStateConcept
	 * @return {@link ProgramWorkflowState}
	 */
	public ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept);
	
	/**
	 * Returns {@link PatientState} by Id. This is because since Openmrs 2.x the getById method was
	 * removed
	 * 
	 * @param stateId
	 */
	public PatientState getPatientState(Integer stateId);
	
	/**
	 * Returns list of {@link Patient} objects by Ids
	 * 
	 * @param patientIds
	 * @return
	 */
	public List<Patient> getPatients(Collection<Integer> patientIds);
	
	/**
	 * Returns map of Patients with their respective {@link DrugOrder} lists
	 * 
	 * @param cohort
	 * @param drugSet
	 * @return
	 */
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet);
	
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
	Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate, Date toDate);
}
