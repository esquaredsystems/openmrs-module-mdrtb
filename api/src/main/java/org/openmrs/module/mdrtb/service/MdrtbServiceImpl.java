package org.openmrs.module.mdrtb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptSet;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderContext;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.Country;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.TbUtil;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
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
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.service.db.HibernateMdrtbDAO;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected HibernateMdrtbDAO dao;
	
	private MdrtbConcepts conceptMap = new MdrtbConcepts(); // TODO: should this be a bean?
	
	// caches
	private Map<Integer, String> colorMapCache = null;
	
	public void setDao(HibernateMdrtbDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see MdrtbService#getLocationsWithAnyProgramEnrollments()
	 */
	public List<Location> getLocationsWithAnyProgramEnrollments() {
		return dao.getLocationsWithAnyProgramEnrollments();
	}
	
	/**
	 * @see MdrtbService#getConcept(String)
	 */
	public Concept getConcept(String lookup) {
		if (ObjectUtil.notNull(lookup)) {
			// First try MDR-TB module's known concept mappings
			try {
				return conceptMap.lookup(lookup);
			}
			catch (Exception e) {}
			// Next try UUID
			if (lookup.length() == 36 && lookup.matches(MdrtbConstants.UUID_REGEX)) {
				try {
					Concept c = Context.getConceptService().getConceptByUuid(lookup);
					if (c != null) {
						initializeEverythingAboutConcept(c);
						return c;
					}
				}
				catch (Exception e) {}
			}
			// Next try precise name
			try {
				Concept c = Context.getConceptService().getConceptByName(lookup);
				if (c != null) {
					initializeEverythingAboutConcept(c);
					return c;
				}
			}
			catch (Exception e) {}
		}
		log.warn("Concept: " + lookup + " was not found!");
		return null;
	}
	
	public void initializeEverythingAboutConcept(Concept c) {
		if (c != null) {
			c.getDatatype().getHl7Abbreviation();
			for (ConceptName cns : c.getNames()) {
				Collection<ConceptNameTag> tags = cns.getTags();
				for (ConceptNameTag cnTag : tags) {
					cnTag.getTag();
				}
			}
			Collection<ConceptAnswer> cas = c.getAnswers();
			if (cas != null) {
				for (ConceptAnswer ca : cas) {
					Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
					for (ConceptName cn : cnsTmp) {
						Collection<ConceptNameTag> tags = cn.getTags();
						for (ConceptNameTag cnTag : tags) {
							cnTag.getTag();
						}
					}
				}
			}
		}
	}
	
	public void resetConceptMapCache() {
		this.conceptMap.resetCache();
	}
	
	@SuppressWarnings("deprecation")
	public List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end,
	        Collection<EncounterType> types) {
		// return Context.getEncounterService().getEncounters(patient, null, null, null, null, types, false);
		return Context.getEncounterService().getEncounters(patient, location, start, end, null, types, null, null, null,
		    false);
	}
	
	public List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types) {
		return getEncounters(patient, null, null, null, types);
	}
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, TbUtil.getTbEncounterTypes());
	}
	
	public List<Encounter> getMdrtbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, MdrtbUtil.getMdrtbEncounterTypes());
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms() {
		return getAllMdrtbPatientProgramsInDateRange(null, null);
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    null, endDate, startDate, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient) {
		
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getMdrtbProgram(),
		    null, null, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		
		return mdrtbPrograms;
	}
	
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient) {
		List<MdrtbPatientProgram> programs = getMdrtbPatientPrograms(patient);
		
		if (programs.size() > 0) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<MdrtbPatientProgram> programs = new LinkedList<MdrtbPatientProgram>();
		
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate))
			        && (program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(
			            startDate))) {
				programs.add(program);
			}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date) {
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}
		
		return null;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		} else if (patientProgramId == -1) {
			return null;
		} else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getMdrtbProgram())) {
				return null;
				// TODO: Figure out why this was throwing an exception before
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			} else {
				return new MdrtbPatientProgram(program);
			}
		}
	}
	
	public Specimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if (patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(patient);
	}
	
	public Specimen getSpecimen(Integer specimenId) {
		return getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
	
	public Specimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong
		// type
		if (encounter == null || !encounter.getEncounterType().equals(MdrtbConstants.ET_SPECIMEN_COLLECTION)) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(encounter);
	}
	
	public List<Specimen> getSpecimens(Patient patient) {
		return getSpecimens(patient, null, null, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate) {
		return getSpecimens(patient, startDate, endDate, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	        Location locationCollected) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		
		specimenEncounters = getEncounters(patient, locationCollected, startDateCollected, endDateCollected,
		    specimenEncounterTypes);
		
		for (Encounter encounter : specimenEncounters) {
			specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
	}
	
	public void saveSpecimen(Specimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should an encounter)
		if (!(specimen.getSpecimen() instanceof Encounter)) {
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
		// We need the specimen encounters to potentially be viewable by a bacteriology
		// htmlform:
		Encounter enc = (Encounter) specimen.getSpecimen();
		String formIdWithWhichToViewEncounter = Context.getAdministrationService().getGlobalProperty(
		    MdrtbConstants.GP_MDRTB_FORM_ID_TO_ATTACH_TO_BACTERIOLOGY_ENTRY);
		try {
			if (formIdWithWhichToViewEncounter != null && !formIdWithWhichToViewEncounter.equals(""))
				enc.setForm(Context.getFormService().getForm(Integer.valueOf(formIdWithWhichToViewEncounter)));
		}
		catch (Exception ex) {
			log.error("Invalid formId found in global property " + MdrtbConstants.GP_BACTERIOLOGY_ENTRY_FORM_ID);
		}
		
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		} else {
			Context.getEncounterService().voidEncounter(encounter, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null
		        || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
		                || obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT)) || obs.getConcept()
		                .equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)))) {
			throw new APIException("Unable to delete specimen test: invalid test id " + testId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public Smear createSmear(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addSmear();
	}
	
	public Smear createSmear(Specimen specimen, Smear smear) {
		Smear newSmear = specimen.addSmear();
		newSmear.copyMembersFrom(smear);
		return newSmear;
	}
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new SmearImpl(obs);
	}
	
	public Smear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public void saveSmear(Smear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Culture createCulture(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}
	
	public Culture createCulture(Specimen specimen, Culture culture) {
		Culture newCulture = specimen.addCulture();
		newCulture.copyMembersFrom(culture);
		return newCulture;
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new CultureImpl(obs);
	}
	
	public Culture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public void saveCulture(Culture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Dst createDst(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create dst: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}
	
	public Dst createDst(Specimen specimen, Dst dst) {
		Dst newDst = specimen.addDst();
		newDst.copyMembersFrom(dst);
		return newDst;
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new DstImpl(obs);
	}
	
	public Dst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public void saveDst(Dst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public void deleteDstResult(Integer dstResultId) {
		Obs obs = Context.getObsService().getObs(dstResultId);
		
		// the id must refer to a valid obs, which is a dst result
		if (obs == null || !obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_RESULT))) {
			throw new APIException("Unable to delete dst result: invalid dst result id " + dstResultId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by Mdr-tb module specimen tracking UI");
	}
	
	public void deleteScannedLabReport(Integer reportId) {
		Obs obs = Context.getObsService().getObs(reportId);
		
		// the id must refer to a valid obs, which is a scanned lab report
		if (obs == null || !obs.getConcept().equals(this.getConcept(MdrtbConcepts.SCANNED_LAB_REPORT))) {
			throw new APIException("Unable to delete scanned lab report: invalid report id " + reportId);
		} else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath) {
		
		// first call the main Patient Service process death method
		Context.getPatientService().processDeath(patient, deathDate, causeOfDeath, null);
		
		// if the most recent MDR-TB program is open, we need to close it
		MdrtbPatientProgram program = getMostRecentMdrtbPatientProgram(patient);
		
		if (program != null && program.getActive()) {
			program.setDateCompleted(deathDate);
			program.setOutcome(getProgramWorkflowState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED)));
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
		
		// if the patient is hospitalized, we need to end the hospitalization
		if (program != null && program.getCurrentlyHospitalized()) {
			program.closeCurrentHospitalization(deathDate);
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
	}
	
	public Program getMdrtbProgram() {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME);
		return Context.getProgramWorkflowService().getProgramByName(globalProperty);
	}
	
	public Collection<Person> getProviders() {
		// TODO: Use the Provider service instead
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		for (User user : providers) {
			persons.add(user.getPerson());
		}
		return persons;
	}
	
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return this.getConcept(MdrtbConcepts.SMEAR_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return this.getConcept(MdrtbConcepts.SMEAR_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return this.getConcept(MdrtbConcepts.CULTURE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return this.getConcept(MdrtbConcepts.CULTURE_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return this.getConcept(MdrtbConcepts.DST_METHOD).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(this.getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		
		return results;
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return this.getConcept(MdrtbConcepts.TYPE_OF_ORGANISM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return this.getConcept(MdrtbConcepts.SAMPLE_SOURCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return this.getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
		return this.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
	}
	
	/**
	 * @return the List of Concepts that represent the Drugs within the passed Drug Set
	 */
	public List<Concept> getDrugsInSet(Concept concept) {
		List<Concept> drugs = new LinkedList<Concept>();
		if (concept != null) {
			List<ConceptSet> drugSet = Context.getConceptService().getConceptSetsByConcept(concept);
			if (drugSet != null) {
				for (ConceptSet drug : drugSet) {
					drugs.add(drug.getConcept());
				}
			}
		}
		return drugs;
	}
	
	public List<Concept> getMdrtbDrugs() {
		return getDrugsInSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
	}
	
	public List<Concept> getAntiretrovirals() {
		return getDrugsInSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANTIRETROVIRALS));
	}
	
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes() {
		return getPossibleMdrtbWorkflowStates(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse() {
		return getPossibleMdrtbWorkflowStates(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
		return getPossibleMdrtbWorkflowStates(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT));
	}
	
	public String getColorForConcept(Concept concept) {
		if (concept == null) {
			log.error("Cannot fetch color for null concept");
			return "";
		}
		
		// initialize the cache if need be
		if (colorMapCache == null) {
			String colorMap = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_COLOR_MAP);
			colorMapCache = loadCache(colorMap);
		}
		
		String color = "";
		
		try {
			color = colorMapCache.get(concept.getId());
		}
		catch (Exception e) {
			log.error("Unable to get color for concept " + concept.getId());
			color = "white";
		}
		
		return color;
	}
	
	public void resetColorMapCache() {
		this.colorMapCache = null;
	}
	
	/**
	 * Utility functions
	 */
	private Set<ProgramWorkflowState> getPossibleMdrtbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(
		    Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME));
		
		// get the workflow via the concept name
		for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
			if (workflow.getConcept().equals(workflowConcept)) {
				return workflow.getStates(false);
			}
		}
		return null;
	}
	
	private Set<ProgramWorkflowState> getPossibleTbWorkflowStates(Concept workflowConcept) {
		// get the mdrtb program via the name listed in global properties
		String programName = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME);
		List<Program> allPrograms = Context.getProgramWorkflowService().getAllPrograms();
		for (Program program : allPrograms) {
			if (program.getName().equalsIgnoreCase(programName)) {
				for (ProgramWorkflow workflow : program.getAllWorkflows()) {
					if (workflow.getConcept().equals(workflowConcept)) {
						return workflow.getStates(false);
					}
				}
			}
		}
		// get the workflow via the concept name
		return null;
	}
	
	private Map<Integer, String> loadCache(String mapAsString) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		if (StringUtils.isNotBlank(mapAsString)) {
			for (String mapping : mapAsString.split("\\|")) {
				String[] mappingFields = mapping.split(":");
				
				Integer conceptId = null;
				
				// if this is a mapping code, need to convert it to the concept id
				if (!MdrtbUtil.isInteger(mappingFields[0])) {
					Concept concept = getConcept(mappingFields[0]);
					if (concept != null) {
						conceptId = concept.getConceptId();
					} else {
						throw new MdrtbAPIException("Invalid concept mapping value in the the colorMap global property.");
					}
				}
				// otherwise, assume this is a concept id
				else {
					conceptId = Integer.valueOf(mappingFields[0]);
				}
				
				map.put(conceptId, mappingFields[1]);
			}
		} else {
			// TODO: make this error catching a little more elegant?
			throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
		}
		
		return map;
	}
	
	public List<Specimen> getSpecimens(Patient patient, Integer programId) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		
		specimenEncounters = getEncountersByPatientAndTypes(patient, specimenEncounterTypes);
		Obs temp = null;
		for (Encounter encounter : specimenEncounters) {
			temp = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == programId.intValue())
				specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
		
	}
	
	public Program getTbProgram() {
		List<Program> allPrograms = Context.getProgramWorkflowService().getAllPrograms();
		String programName = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_PROGRAM_NAME);
		for (Program program : allPrograms) {
			if (program.getName().equalsIgnoreCase(programName)) {
				return program;
			}
		}
		return null;
	}
	
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
		return getPossibleTbWorkflowStates(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
		return getPossibleTbWorkflowStates(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP));
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> tbPrograms = new LinkedList<MdrtbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			tbPrograms.add(new MdrtbPatientProgram(program));
		}
		return tbPrograms;
	}
	
	public void saveXpert(Xpert xpert) {
		if (xpert == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(xpert.getTest() instanceof Obs)) {
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) xpert.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Xpert createXpert(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addXpert();
	}
	
	@Deprecated
	public Xpert getXpert(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new XpertImpl(obs);
	}
	
	public Xpert getXpert(Integer obsId) {
		return new XpertImpl(Context.getObsService().getObs(obsId));
	}
	
	public void saveHAIN(HAIN hain) {
		if (hain == null) {
			log.warn("Unable to save hain: hain object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(hain.getTest() instanceof Obs)) {
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public HAIN createHAIN(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN();
	}
	
	public HAIN getHAIN(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new HAINImpl(obs);
	}
	
	public HAIN getHAIN(Integer obsId) {
		return getHAIN(Context.getObsService().getObs(obsId));
	}
	
	public HAIN2 getHAIN2(Obs obs) {
		// don't need to do much error checking here because the constructor will handle
		// it
		return new HAIN2Impl(obs);
	}
	
	public HAIN2 createHAIN2(Specimen specimen) {
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN2();
	}
	
	public void saveHAIN2(HAIN2 hain2) {
		if (hain2 == null) {
			log.warn("Unable to save hain: hain object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		
		if (!(hain2.getTest() instanceof Obs)) {
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain2.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public HAIN2 getHAIN2(Integer obsId) {
		return getHAIN2(Context.getObsService().getObs(obsId));
	}
	
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(MdrtbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(MdrtbConcepts.ISONIAZID_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleFqResistanceResults() {
		return this.getConcept(MdrtbConcepts.FQ_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInjResistanceResults() {
		return this.getConcept(MdrtbConcepts.INJ_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(MdrtbConcepts.XPERT_MTB_BURDEN).getAnswers();
	}
	
	public Location getLocation(Integer regionId, Integer districtId, Integer facilityId) {
		if (regionId == null || districtId == null)
			return null;
		Region o = getRegion(regionId);
		District d = getDistrict(districtId);
		Facility f = null;
		if (facilityId != null) {
			f = getFacility(facilityId);
		}
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			boolean regionFlag = loc.getStateProvince().equalsIgnoreCase(o.getName());
			if (!regionFlag) {
				continue;
			}
			boolean districtFlag = loc.getCountyDistrict().equalsIgnoreCase(d.getName());
			if (!districtFlag) {
				continue;
			}
			boolean stateFlag = f == null ? true : loc.getAddress4().equalsIgnoreCase(f.getName());
			if (regionFlag && districtFlag && stateFlag) {
				return loc;
			}
		}
		return null;
	}
	
	public List<Region> getRegions() {
		List<Region> oblasts = new ArrayList<Region>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Region.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public List<Region> getOblasts(Integer parentId) {
		List<Region> oblasts = new ArrayList<Region>();
		BaseLocation parent = new District("", parentId);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public Region getRegion(Integer regionId) {
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Region.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getId().equals(regionId)) {
				return new Region(baseLocation);
			}
		}
		return null;
	}
	
	public List<Location> getLocationsFromRegion(Region region) {
		List<Location> locationList = new ArrayList<Location>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getStateProvince() != null) {
				if (loc.getStateProvince().equals(region.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<Facility> getFacilities() {
		List<Facility> facilities = new ArrayList<Facility>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Facility.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			facilities.add(new Facility(baseLocation));
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities() {
		List<Facility> facilityList = getFacilities();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		List<Facility> labs = new ArrayList<Facility>();
		if (labIds != null) {
			for (String id : labIds) {
				try {
					for (Facility f : facilityList) {
						if (f.getId().equals(Integer.parseInt(id))) {
							labs.add(f);
						}
					}
				}
				catch (Exception e) {}
			}
		}
		return facilityList;
	}
	
	public List<Facility> getFacilitiesByParent(Integer parentId) {
		List<Facility> facilities = new ArrayList<Facility>();
		BaseLocation parent = dao.getAddressHierarchyLocation(parentId);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getLevelId().equals(Facility.HIERARCHY_LEVEL)) {
				facilities.add(new Facility(baseLocation));
			}
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities(Integer parentId) {
		List<Facility> facilities = getFacilitiesByParent(parentId);
		Set<Facility> filtered = new HashSet<Facility>();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (Facility facility : facilities) {
				if (facility.getId().equals(Integer.parseInt(id))) {
					filtered.add(facility);
				}
			}
		}
		return new ArrayList<Facility>(filtered);
	}
	
	public Facility getFacility(Integer facilityId) {
		List<Facility> facilities = getFacilities();
		for (Facility facility : facilities) {
			if (facility.getId().equals(facilityId)) {
				return facility;
			}
		}
		return null;
	}
	
	public List<Location> getLocationsFromFacility(Facility facility) {
		List<Location> locationList = new ArrayList<Location>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getAddress6() != null) {
				if (loc.getAddress6().equals(facility.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<District> getDistrictsByParent(Integer parentId) {
		List<District> districts = new ArrayList<District>();
		BaseLocation parent = dao.getAddressHierarchyLocation(parentId);
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByParent(parent);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getLevelId().equals(District.HIERARCHY_LEVEL)) {
				districts.add(new District(baseLocation));
			}
		}
		return districts;
		
	}
	
	public List<District> getRegDistricts(Integer parentId) {
		List<District> districts = getDistrictsByParent(parentId);
		Set<District> filtered = new HashSet<District>();
		//TODO: Use location tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (District district : districts) {
				if (district.getId().equals(Integer.parseInt(id))) {
					filtered.add(district);
				}
			}
		}
		return new ArrayList<District>(filtered);
	}
	
	public District getDistrict(Integer districtId) {
		return new District(dao.getAddressHierarchyLocation(districtId));
	}
	
	public District getDistrict(String districtName) {
		List<District> list = getDistricts();
		for (District district : list) {
			if (district.getName().equalsIgnoreCase(districtName)) {
				return district;
			}
		}
		return null;
	}
	
	public List<District> getDistricts() {
		List<District> districts = new ArrayList<District>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(District.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			districts.add(new District(baseLocation));
		}
		return districts;
	}
	
	public List<District> getRegDistricts() {
		List<District> districts = getDistricts();
		Set<District> filtered = new HashSet<District>();
		//TODO: Use tags
		String labIdsProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_LAB_ENTRY_IDS);
		String labIds[] = labIdsProperty.split("\\|");
		for (String id : labIds) {
			if (!StringUtils.isNumeric(id)) {
				continue;
			}
			for (District district : districts) {
				if (district.getId().equals(Integer.parseInt(id))) {
					filtered.add(district);
				}
			}
		}
		return new ArrayList<District>(filtered);
	}
	
	public List<Location> getLocationsFromDistrict(District district) {
		List<Location> locationList = new ArrayList<Location>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getCountyDistrict() != null) {
				if (loc.getCountyDistrict().equals(district.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	//TODO: Write unit test to check if only the locations with enrollment tag are returned
	public List<Location> getEnrollmentLocations() {
		LocationTag enrollmentTag = Context.getLocationService().getLocationTagByUuid(
		    MdrtbConstants.ENROLLMENT_LOCATION_TAG_UUID);
		List<Location> list = Context.getLocationService().getLocationsByTag(enrollmentTag);
		return list;
	}
	
	public PatientIdentifier getPatientProgramIdentifier(MdrtbPatientProgram mpp) {
		return getGenPatientProgramIdentifier(mpp.getPatientProgram());
	}
	
	public PatientIdentifier getGenPatientProgramIdentifier(PatientProgram pp) {
		Integer id = null;
		String query = "select patient_id from patient_program where patient_program_id = " + pp.getPatientProgramId();
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		for (List<Object> temp : result) {
			for (int i = 0; i < temp.size(); i++) {
				Object value = temp.get(i);
				if (value != null) {
					id = (Integer) value;
				}
			}
		}
		if (id != null) {
			Patient patient = Context.getPatientService().getPatient(id);
			PatientIdentifier pi = patient.getPatientIdentifier();
			return pi;
		}
		return null;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			tbPrograms.add(new TbPatientProgram(program));
		}
		
		return tbPrograms;
	}
	
	/* TODO: Is this method required? There's no patient program ID in the patient_program table any more */
	@Deprecated
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId) {
		//		String query = "UPDATE patient_program SET patient_identifier_id= " + patientIdenifierId
		//		        + " WHERE patient_program_id=" + patientProgramId + ";";
		//		Context.getAdministrationService().executeSQL(query, false);
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleRegimens() {
		return this.getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleHIVStatuses() {
		return this.getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleResistanceTypes() {
		return this.getConcept(MdrtbConcepts.RESISTANCE_TYPE).getAnswers();
	}
	
	@Transactional(readOnly = true)
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion) {
		return this.getConcept(conceptQuestion).getAnswers();
	}
	
	public int countPDFRows() {
		return dao.countPDFRows();
	}
	
	public List<List<Integer>> getPDFRows(String reportType) {
		return dao.getPDFData(reportType);
	}
	
	public ArrayList<String> getPDFColumns() {
		return dao.getPDFColumns();
	}
	
	public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String date, String type) {
		dao.unlockReport(oblast, district, facility, year, quarter, month, name, date, type);
	}
	
	public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String reportDate, String tableData, boolean reportStatus, String reportName, String reportType) {
		log.debug("Impl -> Saving PDF");
		try {
			dao.doPDF(oblast, district, facility, year, quarter, month, reportDate, tableData, reportStatus, reportName,
			    reportType);
		}
		
		catch (Exception e) {
			log.debug("caught in impl: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String type) {
		return dao.readReportStatus(oblast, district, facility, year, quarter, month, name, type);
	}
	
	public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
	        String month, String name, String date, String reportType) {
		return dao.readTableData(oblast, district, facility, year, quarter, month, name, date, reportType);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames, startDate, endDate, closeDate);
	}
	
	public List<SmearForm> getSmearForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<SmearForm> smears = new ArrayList<SmearForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					SmearForm sf = new SmearForm(e);
					sf.setPatient(tpp.getPatient());
					smears.add(sf);
				}
			}
		}
		Collections.sort(smears);
		return smears;
	}
	
	public List<CultureForm> getCultureForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<CultureForm> cultures = new ArrayList<CultureForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					CultureForm sf = new CultureForm(e);
					sf.setPatient(tpp.getPatient());
					cultures.add(sf);
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public List<XpertForm> getXpertForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<XpertForm> xperts = new ArrayList<XpertForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					XpertForm sf = new XpertForm(e);
					sf.setPatient(tpp.getPatient());
					xperts.add(sf);
				}
			}
		}
		Collections.sort(xperts);
		return xperts;
	}
	
	public List<HAINForm> getHAINForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAINForm> hains = new ArrayList<HAINForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.HAIN_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					HAINForm sf = new HAINForm(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<HAIN2Form> getHAIN2Forms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAIN2Form> hains = new ArrayList<HAIN2Form>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					HAIN2Form sf = new HAIN2Form(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<DSTForm> getDstForms(Integer patientProgramId) {
		// TbPatientProgram tpp = getTbPatientProgram(patientProgramId);
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DSTForm> dsts = new ArrayList<DSTForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
					DSTForm sf = new DSTForm(e);
					sf.setPatient(tpp.getPatient());
					dsts.add(sf);
				}
			}
		}
		Collections.sort(dsts);
		return dsts;
	}
	
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DrugResistanceDuringTreatmentForm> drdts = new ArrayList<DrugResistanceDuringTreatmentForm>();
		ArrayList<EncounterType> et = new ArrayList<EncounterType>();
		et.add(MdrtbConstants.ET_RESISTANCE_DURING_TREATMENT);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			// if(MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT),
			// e)!=null) {
			Obs temp = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (temp != null && temp.getValueNumeric().intValue() == patientProgramId.intValue()) {
				DrugResistanceDuringTreatmentForm drdt = new DrugResistanceDuringTreatmentForm(e);
				drdt.setPatient(tpp.getPatient());
				drdts.add(drdt);
			}
			// }
		}
		Collections.sort(drdts);
		return drdts;
	}
	
	public List<Encounter> getEncountersWithNoProgramId(EncounterType et, Patient p) {
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(et);
		
		ArrayList<Encounter> encs = new ArrayList<Encounter>();
		List<Encounter> all = getEncountersByPatientAndTypes(p, typeList);
		
		for (Encounter e : all) {
			if (MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e) == null) {
				encs.add(e);
			}
		}
		
		return encs;
	}
	
	public void addProgramIdToEncounter(Integer encounterId, Integer programId) {
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(programId);
		Encounter e = Context.getEncounterService().getEncounter(encounterId);
		Obs idObs = new Obs(pp.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.PATIENT_PROGRAM_ID), e.getEncounterDatetime(), e.getLocation());
		idObs.setEncounter(e);
		idObs.setObsDatetime(e.getEncounterDatetime());
		idObs.setLocation(e.getLocation());
		idObs.setValueNumeric(programId.doubleValue());
		e.addObs(idObs);
		Context.getEncounterService().saveEncounter(e);
	}
	
	public ArrayList<TB03Form> getTB03FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getRegion(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromRegion(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = getEncounters(null, l, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03Form(e));
			}
			
		}
		
		return forms;
		
	}
	
	public ArrayList<TB03Form> getTB03FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		log.debug("STR:" + startDate);
		log.debug("END:" + endDate);
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		
		List<Encounter> temp = null;
		
		if (locList != null && locList.size() != 0) {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TB03Form(e));
				}
			}
		} else {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03Form(e));
			}
		}
		return forms;
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getRegion(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromRegion(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = getEncounters(null, l, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
		}
		return forms;
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TB03uForm(e));
				}
			}
		}
		return forms;
	}
	
	public ArrayList<Form89> getForm89FormsFilled(Location location, String oblast, Integer year, String quarter,
	        String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		
		Region o = null;
		if (!oblast.equals("") && location == null)
			o = Context.getService(MdrtbService.class).getRegion(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if (o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromRegion(o);
		else if (location != null)
			locList.add(location);
		List<Encounter> temp = null;
		for (Location l : locList) {
			temp = getEncounters(null, l, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
		}
		return forms;
	}
	
	public ArrayList<Form89> getForm89FormsFilled(ArrayList<Location> locList, Integer year, String quarter, String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
		} else {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new Form89(e));
				}
			}
		}
		return forms;
	}
	
	public ArrayList<Form89> getForm89FormsFilledForPatientProgram(Patient p, Location location, Integer patProgId,
	        Integer year, String quarter, String month) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		Map<String, Date> dateMap = null;
		
		if (year != null && (quarter != null || month != null))
			dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = null;
		Date endDate = null;
		
		if (dateMap != null) {
			startDate = (Date) (dateMap.get("startDate"));
			endDate = (Date) (dateMap.get("endDate"));
		}
		
		Concept ppid = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		
		List<Encounter> temp = getEncounters(p, location, startDate, endDate, typeList);
		if (temp != null) {
			for (Encounter e : temp) {
				Obs ppObs = MdrtbUtil.getObsFromEncounter(ppid, e);
				if (ppObs != null) {
					if (ppObs.getValueNumeric() != null && (ppObs.getValueNumeric().intValue() == patProgId.intValue())) {
						forms.add(new Form89(e));
					}
				}
			}
		}
		return forms;
		
	}
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<TransferOutForm> forms = new ArrayList<TransferOutForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferOutForm(e));
			}
		} else {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferOutForm(e));
				}
			}
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferInForm> getTransferInFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<TransferInForm> forms = new ArrayList<TransferInForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferInForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferInForm(e));
				}
			}
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferOutForm> getTransferOutFormsFilledForPatient(Patient p) {
		
		ArrayList<TransferOutForm> forms = new ArrayList<TransferOutForm>();
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferOutForm(e));
		}
		
		return forms;
		
	}
	
	public ArrayList<TransferInForm> getTransferInFormsFilledForPatient(Patient p) {
		
		ArrayList<TransferInForm> forms = new ArrayList<TransferInForm>();
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferInForm(e));
		}
		
		return forms;
		
	}
	
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		Set<ProgramWorkflowState> temp = getPossibleTbWorkflowStates(Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE));
		
		return temp;
	}
	
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient) {
		TB03Form ret = null;
		Integer encounterId = null;
		EncounterType intakeType = MdrtbConstants.ET_TB03_TB_INTAKE;
		String query = "select encounter_id from encounter where location_id=" + location.getId()
		        + " AND encounter_datetime <= '" + encounterDate + "' AND patient_id=" + patient.getId()
		        + " AND encounter_type=" + intakeType.getId() + " AND voided=0 ORDER BY encounter_datetime DESC";
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		if (result != null && result.size() > 0) {
			List<Object> resp = result.get(0);
			if (resp != null) {
				encounterId = (Integer) (resp.get(0));
			}
			
		}
		
		if (encounterId != null)
			ret = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
		
		return ret;
		
	}
	
	public List<Location> getCultureLocations() {
		ArrayList<Location> ret = new ArrayList<Location>();
		HashSet<Integer> locs = new HashSet<Integer>();
		
		//TODO: use location tag instead
		String cultureLocIds = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_CULTURE_LAB_IDS);
		if (cultureLocIds == null || cultureLocIds.length() == 0) {
			return ret;
		}
		
		String[] locIds = cultureLocIds.split("\\|");
		
		if (locIds == null || locIds.length == 0) {
			return ret;
		}
		
		for (int i = 0; i < locIds.length; i++) {
			if (locIds[i].length() != 0) {
				locs.add(Integer.parseInt(locIds[i]));
			}
		}
		
		Collection<Location> allLocs = Context.getLocationService().getAllLocations(false);
		
		for (Location l : allLocs) {
			if (locs.contains(l.getId())) {
				ret.add(l);
			}
		}
		
		return ret;
	}
	
	public ArrayList<Location> getLocationList(Integer oblastId, Integer districtId, Integer facilityId) {
		if (oblastId == null && districtId == null && facilityId == null)
			return null;
		
		List<Location> allLocations = Context.getLocationService().getAllLocations();
		ArrayList<Location> locList = new ArrayList<Location>();
		for (Location location : allLocations) {
			Region oblast = oblastId == null ? null : getRegion(oblastId);
			District district = districtId == null ? null : getDistrict(districtId);
			Facility facility = facilityId == null ? null : getFacility(facilityId);
			boolean hasRegion = oblast != null;
			boolean hasDistrict = district != null;
			boolean hasFacility = facility != null;
			
			if (hasRegion) {
				if (!location.getStateProvince().equalsIgnoreCase(oblast.getName())) {
					continue;
				}
				if (hasDistrict) {
					if (!location.getCountyDistrict().equalsIgnoreCase(district.getName())) {
						continue;
					}
					if (hasFacility) {
						if (!location.getAddress4().equalsIgnoreCase(facility.getName())) {
							continue;
						}
					}
				}
				locList.add(location);
			}
		}
		log.debug("LOCS:" + locList.size());
		return locList;
	}
	
	public PatientIdentifier getPatientIdentifierById(Integer id) {
		
		return dao.getPatientIdentifierById(id);
	}
	
	public ArrayList<TB03uForm> getTB03uFormsFilledWithTxStartDateDuring(ArrayList<Location> locList, Integer year,
	        String quarter, String month) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		List<Encounter> temp = null;
		
		// CHECK
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, null, null, typeList);
			for (Encounter e : temp) {
				Obs o = MdrtbUtil.getObsFromEncounter(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), e);
				if (o != null && o.getValueDatetime() != null
				        && (o.getValueDatetime().equals(startDate) || o.getValueDatetime().after(startDate))
				        && (o.getValueDatetime().equals(endDate) || o.getValueDatetime().before(endDate))) {
					forms.add(new TB03uForm(e));
				}
			}
		} else {
			for (Location l : locList) {
				temp = getEncounters(null, l, null, null, typeList);
				for (Encounter e : temp) {
					Obs o = MdrtbUtil.getObsFromEncounter(
					    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE), e);
					if (o != null && o.getValueDatetime() != null
					        && (o.getValueDatetime().equals(startDate) || o.getValueDatetime().after(startDate))
					        && (o.getValueDatetime().equals(endDate) || o.getValueDatetime().before(endDate))) {
						forms.add(new TB03uForm(e));
					}
				}
			}
		}
		return forms;
	}
	
	public List<Country> getCountries() {
		List<Country> countries = new ArrayList<Country>();
		List<BaseLocation> list = dao.getAddressHierarchyLocationsByHierarchyLevel(Country.HIERARCHY_LEVEL);
		for (BaseLocation baseLocation : list) {
			countries.add(new Country(baseLocation.getName(), baseLocation.getId()));
		}
		return countries;
	}
	
	public ArrayList<TB03Form> getTB03FormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<TB03Form> forms = new ArrayList<TB03Form>();
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new TB03Form(e));
			}
		}
		return forms;
	}
	
	public ArrayList<Form89> getForm89FormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<Form89> forms = new ArrayList<Form89>();
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new Form89(e));
			}
		}
		return forms;
	}
	
	public void evict(Object obj) {
		dao.evict(obj);
	}
	
	public TB03uForm getTB03uFormForProgram(Patient p, Integer patientProgId) {
		
		TB03uForm form = null;
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				form = new TB03uForm(e);
				break;
			}
		}
		return form;
	}
	
	public ArrayList<RegimenForm> getRegimenFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new RegimenForm(e));
			}
		}
		Collections.sort(forms);
		// Collections.reverse(forms);
		return forms;
	}
	
	public ArrayList<RegimenForm> getRegimenFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		log.debug("STR:" + startDate);
		log.debug("END:" + endDate);
		
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		}
		
		else {
			
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
			
		}
		
		Collections.sort(forms);
		return forms;
		
	}
	
	public ArrayList<Patient> getAllPatientsWithRegimenForms() {
		ArrayList<Patient> pList = new ArrayList<Patient>();
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		List<Encounter> temp = getEncounters(null, null, null, null, typeList);
		for (Encounter e : temp) {
			if (!pList.contains(e.getPatient())) {
				pList.add(e.getPatient());
			}
		}
		return pList;
	}
	
	public RegimenForm getPreviousRegimenFormForPatient(Patient p, ArrayList<Location> locList, Date beforeDate) {
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		List<Encounter> temp = null;
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(p, null, null, beforeDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		}
		
		else {
			for (Location l : locList) {
				temp = getEncounters(p, l, null, beforeDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
		}
		if (forms != null && forms.size() != 0) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public RegimenForm getCurrentRegimenFormForPatient(Patient p, Date beforeDate) {
		// RegimenForm form = null;
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(eType);
		List<Encounter> temp = null;
		
		Date currentDate = null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(beforeDate.getTime());
		gc.add(Calendar.DATE, 1);
		currentDate = gc.getTime();
		
		ArrayList<RegimenForm> forms = new ArrayList<RegimenForm>();
		
		temp = getEncounters(p, null, null, currentDate, typeList);
		for (Encounter e : temp) {
			forms.add(new RegimenForm(e));
		}
		if (forms != null && forms.size() != 0) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public ArrayList<AdverseEventsForm> getAEFormsFilled(ArrayList<Location> locList, Integer year, String quarter,
	        String month) {
		
		ArrayList<AdverseEventsForm> forms = new ArrayList<AdverseEventsForm>();
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		
		log.debug("STR:" + startDate);
		log.debug("END:" + endDate);
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		
		List<Encounter> temp = null;
		
		if (locList == null || locList.size() == 0) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new AdverseEventsForm(e));
			}
		} else {
			for (Location l : locList) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new AdverseEventsForm(e));
				}
			}
		}
		
		Collections.sort(forms);
		return forms;
		
	}
	
	public ArrayList<AdverseEventsForm> getAEFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<AdverseEventsForm> forms = new ArrayList<AdverseEventsForm>();
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new AdverseEventsForm(e));
			}
			
		}
		Collections.sort(forms);
		return forms;
	}
	
	public ArrayList<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId) {
		
		ArrayList<Location> locList = new ArrayList<Location>();
		Location location = null;
		if (oblastId == null && districtId == null && facilityId == null)
			return null;
		
		if (districtId == null && facilityId == null) { // means they stopped at oblast??
			List<District> distList = getDistrictsByParent(oblastId);
			for (District d : distList) {
				location = getLocation(oblastId, d.getId(), null);
				if (location == null) {
					List<Facility> facs = getFacilitiesByParent(d.getId().intValue());
					for (Facility f : facs) {
						location = getLocation(oblastId, d.getId(), f.getId());
						if (location != null) {
							locList.add(location);
						}
					}
				} else {
					locList.add(location);
				}
			}
		} else if (facilityId == null) {// means they stopped at district - so fetch all facility data
			location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, null);
			if (location == null) { // district that has a set of facilities under it
				List<Facility> facs = Context.getService(MdrtbService.class).getFacilitiesByParent(districtId.intValue());
				for (Facility f : facs) {
					location = Context.getService(MdrtbService.class).getLocation(oblastId, districtId, f.getId());
					if (location != null) {
						locList.add(location);
					}
				}
			} else {
				locList.add(location);
			}
		} else if (districtId == null) { // they chose a facility so get all facilities with this name
			Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
			if (fac == null)
				return null;
			String facName = fac.getName();
			List<Location> locs = Context.getLocationService().getAllLocations(false);
			for (Location l : locs) {
				// Committed after upgrade to v2.0
				//				if (MdrtbUtil.areRussianStringsEqual(l.getName(), facName)) {
				//					locList.add(l);
				//				}
				if (l.getName().equalsIgnoreCase(facName)) {
					locList.add(l);
				}
			}
		}
		log.debug("Dushanbe locations:" + locList.size());
		return locList;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		TbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new TbPatientProgram(program);
			
			for (Location l : locList) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					tbPrograms.add(new TbPatientProgram(program));
					break;
				}
			}
		}
		return tbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(Date startDate, Date endDate,
	        ArrayList<Location> locList) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
		MdrtbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new MdrtbPatientProgram(program);
			
			for (Location l : locList) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					mdrtbPrograms.add(new MdrtbPatientProgram(program));
					break;
				}
			}
		}
		return mdrtbPrograms;
	}
	
	public ArrayList<TB03uForm> getTB03uFormsForProgram(Patient p, Integer patientProgId) {
		
		ArrayList<TB03uForm> forms = new ArrayList<TB03uForm>();
		
		ArrayList<EncounterType> typeList = new ArrayList<EncounterType>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		// temp = Context.getEncounterService().getEncounters(p, null, null, null, null, typeList, null, false);
		temp = Context.getService(MdrtbService.class).getEncounters(p, null, null, null, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId.intValue()) {
				forms.add(new TB03uForm(e));
			}
			
		}
		return forms;
	}
	
	public List<TbPatientProgram> getAllTbPatientPrograms() {
		return getAllTbPatientProgramsInDateRange(null, null);
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), null,
		    endDate, startDate, null, false);
		
		// ADD BY ALI August 13th 2017
		TbPatientProgram temp = null;
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		
		return tbPrograms;
	}
	
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
		
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(),
		    null, null, null, null, false);
		
		// ADD BY ALI Aug 13th 2017
		TbPatientProgram temp = null;
		//
		
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		
		List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
		
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		
		return tbPrograms;
	}
	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
		List<TbPatientProgram> programs = getTbPatientPrograms(patient);
		
		if (programs.size() > 0) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<TbPatientProgram>();
		
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if ((endDate == null || program.getDateEnrolled().before(endDate))
			        && (program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(
			            startDate))) {
				programs.add(program);
			}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date) {
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}
		
		return null;
	}
	
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		} else if (patientProgramId == -1) {
			return null;
		} else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			if (program == null || !program.getProgram().equals(getTbProgram())) {
				return null;
				// TODO: Figure out why this was throwing an exception before
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			} else {
				return new TbPatientProgram(program);
			}
		}
	}
	
	public ProgramWorkflow getProgramWorkflow(Program program, Integer conceptId) {
		for (ProgramWorkflow programWorkflow : program.getAllWorkflows()) {
			if (programWorkflow.getConcept().getConceptId().equals(conceptId)) {
				return programWorkflow;
			}
		}
		return null;
	}
	
	public ProgramWorkflowState getProgramWorkflowState(ProgramWorkflow programWorkflow, Integer conceptId)
	        throws APIException {
		for (ProgramWorkflowState s : programWorkflow.getStates()) {
			if (s.getConcept().getConceptId().equals(conceptId)) {
				return s;
			}
		}
		return null;
	}
	
	public List<DrugOrder> getDrugOrders(Patient patient) {
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
		List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
		List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
		for (Order order : orders) {
			if (order.isType(drugOrderType)) {
				drugOrders.add((DrugOrder) order);
			}
		}
		return null;
	}
	
	public DrugOrder saveDrugOrder(DrugOrder drugOrder) {
		OrderContext orderContext = new OrderContext();
		orderContext.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
		orderContext.setCareSetting(Context.getOrderService().getCareSetting(1)); // Outpatient care setting
		Context.getOrderService().saveOrder(drugOrder, orderContext);
		return drugOrder;
	}
	
	/**
	 * Gets a specific ProgramWorkflowState, given the concept associated with the state
	 */
	public ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept) {
		List<ProgramWorkflowState> list = Context.getProgramWorkflowService().getProgramWorkflowStatesByConcept(
		    programWorkflowStateConcept);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * @deprecated replace with Context.getProgramWorkflowService().getStateByUuid in all
	 *             implementations
	 */
	@Deprecated
	public PatientState getPatientState(Integer stateId) {
		if (stateId != null) {
			return (PatientState) dao.getSessionFactory().getCurrentSession()
			        .createQuery("from PatientState s where patientStateId = :psid").setInteger("psid", stateId.intValue())
			        .uniqueResult();
		}
		return null;
	}
	
	@Override
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		if (!patientIds.isEmpty()) {
			List<Patient> list = patientIds.stream().map(id -> Context.getPatientService().getPatient(id)).collect(Collectors.toList());
			return list;
		}
		return null;
	}
	
	@Override
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet) {
		
		List<Concept> drugConcepts = null;
		if (drugSet != null) {
			List<ConceptSet> concepts = Context.getConceptService().getConceptSetsByConcept(drugSet);
			drugConcepts = new ArrayList<Concept>();
			for (ConceptSet cs : concepts) {
				drugConcepts.add(cs.getConcept());
			}
		}
		
		Map<Integer, List<DrugOrder>> drugOrders = dao.getDrugOrders(cohort, drugConcepts);
		return drugOrders;
	}
	
	public Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate,
	        Date toDate) {
		Cohort patientsInStates = Context.getService(CohortQueryService.class).getPatientsInStates(stateList, fromDate,
		    toDate);
		List<PatientProgram> list = Context.getProgramWorkflowService().getPatientPrograms(patientsInStates,
		    Arrays.asList(program));
		return new Cohort(list);
	}
}
