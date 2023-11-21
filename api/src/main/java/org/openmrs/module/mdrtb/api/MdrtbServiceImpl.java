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
import java.util.Locale;
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
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderContext;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestType;
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.LocationHierarchy;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.ReportStatus;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.TbUtil;
import org.openmrs.module.mdrtb.api.dao.MdrtbDao;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
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
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
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

@SuppressWarnings("null")
public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	MdrtbDao dao;
	
	UserService userService;
	
	MdrtbConcepts conceptMap = new MdrtbConcepts();
	
	Map<Integer, String> colorMapCache = null;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(MdrtbDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/***********************/
	/** UTILITY FUNCTIONS **/
	/***********************/
	private static void initializeEverythingAboutConcept(Concept c) {
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
	
	public void resetConceptMapCache() {
		this.conceptMap.resetCache();
	}
	
	public void resetColorMapCache() {
		this.colorMapCache = null;
	}
	
	private static Set<ProgramWorkflowState> getPossibleMdrtbWorkflowStates(Concept workflowConcept) {
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
	
	/**
	 * Get the mdrtb program via the name listed in global properties
	 */
	private static Set<ProgramWorkflowState> getPossibleTbWorkflowStates(Concept workflowConcept) {
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
		Map<Integer, String> map = new HashMap<>();
		
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
			throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
		}
		
		return map;
	}
	
	public Concept getConcept(String lookup) {
		if (ObjectUtil.notNull(lookup)) {
			// First try MDR-TB module's known concept mappings
			try {
				return conceptMap.lookup(lookup);
			}
			catch (Exception ignored) {}
			// Next try UUID
			if (lookup.length() == 36 && lookup.matches(MdrtbConstants.UUID_REGEX)) {
				try {
					Concept c = Context.getConceptService().getConceptByUuid(lookup);
					if (c != null) {
						initializeEverythingAboutConcept(c);
						return c;
					}
				}
				catch (Exception ignored) {}
			}
			// Next try precise name
			try {
				List<Concept> list = Context.getConceptService().getConceptsByName(lookup, Locale.ENGLISH, false);
				// If there is exactly 1 object, then return it
				if (list.size() == 1) {
					Concept c = list.get(0);
					initializeEverythingAboutConcept(c);
					return c;
				}
				// For more than 1 objects, match the exact name
				for (Concept c : list) {
					if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equalsIgnoreCase(lookup)) {
						initializeEverythingAboutConcept(c);
						return c;
					}
				}
			}
			catch (Exception ignored) {}
		}
		log.warn("Concept: " + lookup + " was not found!");
		return null;
	}
	
	public Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion) {
		return this.getConcept(conceptQuestion).getAnswers();
	}
	
	/**
	 * @deprecated replace with Context.getProgramWorkflowService().getStateByUuid in all
	 *             implementations
	 */
	@Deprecated
	public PatientState getPatientState(Integer stateId) {
		if (stateId != null) {
			return (PatientState) dao.getSessionFactory().getCurrentSession()
			        .createQuery("from PatientState s where patientStateId = :psid").setInteger("psid", stateId)
			        .uniqueResult();
		}
		return null;
	}
	
	public Collection<Person> getProviders() {
		List<Provider> list = Context.getProviderService().getAllProviders();
		SortedSet<Person> people = new TreeSet<>();
		for (Provider provider : list) {
			people.add(provider.getPerson());
		}
		return people;
	}
	
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		if (!patientIds.isEmpty()) {
			return patientIds.stream().map(id -> Context.getPatientService().getPatient(id)).collect(Collectors.toList());
		}
		return null;
	}
	
	public List<Patient> getAllPatientsWithRegimenForms() {
		ArrayList<Patient> pList = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = getEncounters(null, null, null, null, typeList);
		for (Encounter e : temp) {
			if (!pList.contains(e.getPatient())) {
				pList.add(e.getPatient());
			}
		}
		return pList;
	}
	
	public List<DrugOrder> getDrugOrders(Patient patient) {
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
		List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
		List<DrugOrder> drugOrders = new ArrayList<>();
		for (Order order : orders) {
			if (order.isType(drugOrderType)) {
				drugOrders.add((DrugOrder) order);
			}
		}
		return null;
	}
	
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet) {
		List<Concept> drugConcepts = null;
		if (drugSet != null) {
			List<ConceptSet> concepts = Context.getConceptService().getConceptSetsByConcept(drugSet);
			drugConcepts = new ArrayList<>();
			for (ConceptSet cs : concepts) {
				drugConcepts.add(cs.getConcept());
			}
		}
		
		Map<Integer, List<DrugOrder>> drugOrders = dao.getDrugOrders(cohort, drugConcepts);
		return drugOrders;
	}
	
	public DrugOrder saveDrugOrder(DrugOrder drugOrder) {
		OrderContext orderContext = new OrderContext();
		orderContext.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
		orderContext.setCareSetting(Context.getOrderService().getCareSetting(1)); // Outpatient care setting
		Context.getOrderService().saveOrder(drugOrder, orderContext);
		return drugOrder;
	}
	
	public List<Concept> getDrugsInSet(Concept concept) {
		List<Concept> drugs = new LinkedList<>();
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
		return getDrugsInSet(getConcept(MdrtbConcepts.TUBERCULOSIS_DRUGS));
	}
	
	public List<Concept> getAntiretrovirals() {
		return getDrugsInSet(getConcept(MdrtbConcepts.ANTIRETROVIRALS));
	}
	
	public List<Concept> getAllDrugResistanceConcepts() {
		List<Concept> list = new ArrayList<>();
		list.add(getConcept(MdrtbConcepts.AMIKACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.BEDAQUILINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CAPREOMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CIPROFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CLARITHROMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CLOFAZAMINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.CYCLOSERINE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.DELAMANID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ETHAMBUTOL_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ETHIONAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.GATIFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.ISONIAZID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.KANAMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.LEVOFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.LINEZOLID_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.MOXIFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.OFLOXACIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.OTHER_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.P_AMINOSALICY_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.PROTHIONAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.PYRAZINAMIDE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.RIFABUTIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.STREPTOMYCIN_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.TERIZIDONE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.THIOACETAZONE_RESISTANCE));
		list.add(getConcept(MdrtbConcepts.VIOMYCIN_RESISTANCE));
		return list;
	}
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	        Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(),
		    startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<TbPatientProgram> tbPrograms = new LinkedList<>();
		TbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new TbPatientProgram(program);
			for (Location l : locations) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					tbPrograms.add(new TbPatientProgram(program));
					break;
				}
			}
		}
		return tbPrograms;
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
	
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		} else if (patientProgramId == -1) {
			return null;
		} else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			if (program == null || !program.getProgram().equals(getTbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program.");
			} else {
				return new TbPatientProgram(program);
			}
		}
	}
	
	public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(),
		    null, null, null, null, false);
		TbPatientProgram temp = null;
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<TbPatientProgram> tbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			// tbPrograms.add(new TbPatientProgram(program));
			temp = new TbPatientProgram(program);
			PatientIdentifier pid = getPatientMdrtbProgramIdentifier(new MdrtbPatientProgram(temp.getPatientProgram()));
			if (pid != null) {
				temp.setPatientIdentifier(pid);
			}
			tbPrograms.add(temp);
		}
		return tbPrograms;
	}
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<>();
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
	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
		List<TbPatientProgram> programs = getTbPatientPrograms(patient);
		if (!programs.isEmpty()) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms() {
		return getAllMdrtbPatientProgramsInDateRange(null, null);
	}
	
	//TODO: Redundant method. Remove it and use getAllMdrtbPatientProgramsInDateRange(Date, Date) only
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not
		// have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	        Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(),
		    startDate, endDate, null, null, false);
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		MdrtbPatientProgram temp = null;
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			temp = new MdrtbPatientProgram(program);
			
			for (Location l : locations) {
				if (temp.getLocation() != null
				        && (temp.getLocation().getLocationId().intValue() == l.getLocationId().intValue())) {
					mdrtbPrograms.add(new MdrtbPatientProgram(program));
					break;
				}
			}
		}
		return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<MdrtbPatientProgram> programs = new LinkedList<>();
		
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
				// throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			} else {
				return new MdrtbPatientProgram(program);
			}
		}
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient) {
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getMdrtbProgram(),
		    null, null, null, null, false);
		// sort the programs so oldest is first and most recent is last
		Collections.sort(programs, new PatientProgramComparator());
		List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<>();
		// convert to mdrtb patient programs
		for (PatientProgram program : programs) {
			mdrtbPrograms.add(new MdrtbPatientProgram(program));
		}
		return mdrtbPrograms;
	}
	
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient) {
		List<MdrtbPatientProgram> programs = getMdrtbPatientPrograms(patient);
		if (!programs.isEmpty()) {
			return programs.get(programs.size() - 1);
		} else {
			return null;
		}
	}
	
	public ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept) {
		List<ProgramWorkflowState> list = Context.getProgramWorkflowService().getProgramWorkflowStatesByConcept(
		    programWorkflowStateConcept);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
	}
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT));
	}
	
	public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.TB_TREATMENT_OUTCOME));
	}
	
	public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes() {
		return getPossibleMdrtbWorkflowStates(getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME));
	}
	
	public PatientIdentifier getPatientMdrtbProgramIdentifier(MdrtbPatientProgram mpp) {
		return getPatientProgramIdentifier(mpp.getPatientProgram());
	}
	
	public PatientIdentifier getPatientProgramIdentifier(PatientProgram pp) {
		Concept concept = pp.getProgram().getConcept();
		if (concept.equals(getConcept(MdrtbConcepts.MDR_TB_PROGRAM))) {
			String mdrtbIdName = Context.getAdministrationService().getGlobalProperty(
			    MdrtbConstants.GP_MDRTB_IDENTIFIER_TYPE);
			PatientIdentifierType mdrType = Context.getPatientService().getPatientIdentifierTypeByName(mdrtbIdName);
			return pp.getPatient().getPatientIdentifier(mdrType);
		} else if (concept.equals(getConcept(MdrtbConcepts.DOTS_PROGRAM))) {
			String dotsIdName = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_DOTS_IDENTIFIER_TYPE);
			PatientIdentifierType drType = Context.getPatientService().getPatientIdentifierTypeByName(dotsIdName);
			return pp.getPatient().getPatientIdentifier(drType);
		}
		return pp.getPatient().getPatientIdentifier();
	}
	
	public Program getMdrtbProgram() {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_MDRTB_PROGRAM_NAME);
		return Context.getProgramWorkflowService().getProgramByName(globalProperty);
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
	
	public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.PATIENT_GROUP));
	}
	
	public Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		return getPossibleTbWorkflowStates(getConcept(MdrtbConcepts.DOTS_CLASSIFICATION_ACCORDING_TO_PREVIOUS_DRUG_USE));
	}
	
	// TODO: Urgent! No new cultures are being created without this method. How to create new cultures after upgrading to Lab module?
	public Culture createCulture(Specimen specimen, Culture culture) {
		/*
		Culture newCulture = specimen.addCulture();
		newCulture.copyMembersFrom(culture);
		return newCulture;
		*/
		return null;
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
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by MDRTB module");
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
	
	public List<Specimen> getSpecimens(Patient patient, Integer programId) {
		List<Specimen> specimens = new LinkedList<>();
		List<Encounter> specimenEncounters = new LinkedList<>();
		
		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<>();
		specimenEncounterTypes.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		specimenEncounters = getEncountersByPatientAndTypes(patient, specimenEncounterTypes);
		Obs temp = null;
		for (Encounter encounter : specimenEncounters) {
			temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
			if (temp != null && temp.getValueNumeric() != null && temp.getValueNumeric().intValue() == programId)
				specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected,
	        Location locationCollected) {
		List<Specimen> specimens = new LinkedList<>();
		List<Encounter> specimenEncounters = new LinkedList<>();
		
		// create the specific specimen encounter types
		List<EncounterType> specimenEncounterTypes = new LinkedList<>();
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
			log.error("Invalid formId found in global property.");
		}
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		if (obs == null
		        || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
		                || obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT)) || obs.getConcept()
		                .equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)))) {
			throw new APIException("Unable to delete specimen test: invalid test id " + testId);
		} else {
			Context.getObsService().voidObs(obs, "voided by MDRTB module");
		}
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		} else {
			Context.getEncounterService().voidEncounter(encounter, "voided by MDRTB module");
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end,
	        Collection<EncounterType> types) {
		return Context.getEncounterService().getEncounters(patient, location, start, end, null, types, null, null, null,
		    false);
	}
	
	public List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types) {
		return getEncounters(patient, null, null, null, types);
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate) {
		return dao.getEncountersByEncounterTypes(encounterTypeNames, startDate, endDate, closeDate);
	}
	
	public List<Encounter> getEncountersWithNoProgram(EncounterType encounterType, Patient patient) {
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(encounterType);
		ArrayList<Encounter> encs = new ArrayList<>();
		List<Encounter> all = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : all) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e) == null) {
				encs.add(e);
			}
		}
		return encs;
	}
	
	public void addProgramIdToEncounter(Integer encounterId, Integer programId) {
		PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(programId);
		Encounter e = Context.getEncounterService().getEncounter(encounterId);
		Obs idObs = new Obs(pp.getPatient(), getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e.getEncounterDatetime(),
		        e.getLocation());
		idObs.setEncounter(e);
		idObs.setObsDatetime(e.getEncounterDatetime());
		idObs.setLocation(e.getLocation());
		idObs.setValueNumeric(programId.doubleValue());
		e.addObs(idObs);
		Context.getEncounterService().saveEncounter(e);
	}
	
	// TODO: There's no patient program ID in the patient_program table any more
	// FIXME: Removing this might cause bugs. Investigate
	@Deprecated
	public void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId) {
		//		String query = "UPDATE patient_program SET patient_identifier_id= " + patientIdenifierId
		//		        + " WHERE patient_program_id=" + patientProgramId + ";";
		//		Context.getAdministrationService().executeSQL(query, false);
	}
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, TbUtil.getTbEncounterTypes());
	}
	
	public List<Encounter> getMdrtbEncounters(Patient patient) {
		return getEncounters(patient, null, null, null, MdrtbUtil.getMdrtbEncounterTypes());
	}
	
	public Xpert getXpert(Integer obsId) {
		Obs obs = Context.getObsService().getObs(obsId);
		LabTestType labTestType = CommonLabUtil.getService().getCommonTestType();
		LabTest xpert = CommonLabUtil.getService().getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new XpertImpl(xpert);
	}
	
	public Smear getSmear(Obs obs) {
		LabTestType labTestType = CommonLabUtil.getService().getCommonTestType();
		LabTest smear = CommonLabUtil.getService().getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new SmearImpl(smear);
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
	
	public HAIN getHAIN(Obs obs) {
		LabTestType labTestType = CommonLabUtil.getService().getCommonTestType();
		LabTest hain = CommonLabUtil.getService().getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new HAINImpl(hain);
		// return new HAINImpl(obs);
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
	
	public HAIN2 getHAIN2(Obs obs) {
		LabTestType labTestType = CommonLabUtil.getService().getCommonTestType();
		LabTest hain = CommonLabUtil.getService().getMdrtbLabTestOrder(obs.getEncounter(), labTestType);
		return new HAIN2Impl(hain);
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
	
	// TODO: Urgent! No new DSTs are being created without this method. How to create new cultures after upgrading to Lab module?
	public Dst createDst(Specimen specimen, Dst dst) {
		/*
		Dst newDst = specimen.addDst();
		newDst.copyMembersFrom(dst);
		return newDst;
		*/
		return null;
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
	
	/***************/
	/** LOCATIONS **/
	/***************/
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
	
	public List<Location> getLocations(Region region, District district, Facility facility) {
		if (region == null && district == null && facility == null) {
			return null;
		}
		List<Location> list = new ArrayList<>();
		if (facility != null) {
			Location facilityLocation = Context.getLocationService().getLocation(facility.getId());
			list.add(facilityLocation);
			list.addAll(facilityLocation.getChildLocations());
			return list;
		}
		if (district != null) {
			Location districtLocation = Context.getLocationService().getLocation(district.getId());
			list.add(districtLocation);
			list.addAll(districtLocation.getChildLocations());
			// Also add all children of this district
			for (Location child : districtLocation.getChildLocations()) {
				list.addAll(child.getChildLocations());
			}
			return list;
		}
		Location regionLocation = Context.getLocationService().getLocation(region.getId());
		list.add(regionLocation);
		list.addAll(regionLocation.getChildLocations());
		// Add children (districts)
		for (Location child : regionLocation.getChildLocations()) {
			list.addAll(child.getChildLocations());
			// Further add grand children (Facilities)
			for (Location grandChild : child.getChildLocations()) {
				list.addAll(grandChild.getChildLocations());
			}
		}
		return list;
	}
	
	@Deprecated
	public List<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId) {
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		List<Location> list = getLocations(region, district, facility);
		return list;
	}
	
	public List<String> getCountries() {
		List<String> countries = new ArrayList<>();
		for (String s : Locale.getISOCountries()) {
			Locale crunchifyObj = new Locale("", s);
			countries.add(crunchifyObj.getDisplayCountry());
		}
		return countries;
	}
	
	public List<Region> getRegions() {
		List<Region> oblasts = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.REGION);
		for (BaseLocation baseLocation : list) {
			oblasts.add(new Region(baseLocation));
		}
		return oblasts;
	}
	
	public Region getRegion(Integer regionId) {
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.REGION);
		for (BaseLocation baseLocation : list) {
			if (baseLocation.getId().equals(regionId)) {
				return new Region(baseLocation);
			}
		}
		return null;
	}
	
	public List<Location> getLocationsFromRegion(Region region) {
		List<Location> locationList = new ArrayList<>();
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
		List<Facility> facilities = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.FACILITY);
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
		List<Facility> labs = new ArrayList<>();
		if (labIds != null) {
			for (String id : labIds) {
				try {
					for (Facility f : facilityList) {
						if (f.getId().equals(Integer.parseInt(id))) {
							labs.add(f);
						}
					}
				}
				catch (Exception ignored) {}
			}
		}
		return facilityList;
	}
	
	public List<Facility> getFacilitiesByParent(Integer parentId) {
		List<Facility> facilities = new ArrayList<>();
		Location parent = Context.getLocationService().getLocation(parentId);
		Set<Location> childLocations = parent.getChildLocations();
		for (Location location : childLocations) {
			facilities.add(new Facility(new BaseLocation(location, LocationHierarchy.FACILITY)));
		}
		return facilities;
	}
	
	public List<Facility> getRegFacilities(Integer parentId) {
		List<Facility> facilities = getFacilitiesByParent(parentId);
		Set<Facility> filtered = new HashSet<>();
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
		return new ArrayList<>(filtered);
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
		List<Location> locationList = new ArrayList<>();
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
		List<District> districts = new ArrayList<>();
		Location parent = Context.getLocationService().getLocation(parentId);
		Set<Location> childLocations = parent.getChildLocations();
		for (Location location : childLocations) {
			districts.add(new District(new BaseLocation(location, LocationHierarchy.DISTRICT)));
		}
		return districts;
	}
	
	public List<District> getRegDistricts(Integer parentId) {
		List<District> districts = getDistrictsByParent(parentId);
		Set<District> filtered = new HashSet<>();
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
		return new ArrayList<>(filtered);
	}
	
	public District getDistrict(Integer districtId) {
		Location location = Context.getLocationService().getLocation(districtId);
		return new District(new BaseLocation(location, LocationHierarchy.DISTRICT));
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
		List<District> districts = new ArrayList<>();
		List<BaseLocation> list = dao.getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		for (BaseLocation baseLocation : list) {
			districts.add(new District(baseLocation));
		}
		return districts;
	}
	
	public List<District> getRegDistricts() {
		List<District> districts = getDistricts();
		Set<District> filtered = new HashSet<>();
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
		return new ArrayList<>(filtered);
	}
	
	public List<Location> getLocationsFromDistrict(District district) {
		List<Location> locationList = new ArrayList<>();
		List<Location> locations = Context.getLocationService().getAllLocations(false);
		for (Location loc : locations) {
			if (loc.getCountyDistrict() != null) {
				if (loc.getCountyDistrict().equals(district.getName()))
					locationList.add(loc);
			}
		}
		return locationList;
	}
	
	public List<Location> getEnrollmentLocations() {
		LocationTag enrollmentTag = Context.getLocationService().getLocationTagByName(
		    MdrtbConstants.ENROLLMENT_LOCATION_TAG_NAME);
		List<Location> list = Context.getLocationService().getLocationsByTag(enrollmentTag);
		return list;
	}
	
	public List<Location> getCultureLocations() {
		LocationTag enrollmentTag = Context.getLocationService().getLocationTagByName(
		    MdrtbConstants.CULTURE_LOCATION_TAG_NAME);
		List<Location> list = Context.getLocationService().getLocationsByTag(enrollmentTag);
		return list;
	}
	
	/**
	 * This should ideally be a recursive function. But for Tajikistan, we're limiting only to 3
	 * levels
	 */
	public List<Location> getLocationsInHierarchy(Location parent) {
		List<Location> locList = new ArrayList<>();
		if (parent.getChildLocations().isEmpty()) {
			locList.add(parent);
		} 
		// If there are child locations, fetch their list
		else {
			Set<Location> children = parent.getChildLocations();
			for (Location child : children) {
				locList.add(child);
				// If there are grand children, fetch them too
				if (!child.getChildLocations().isEmpty()) {
					Set<Location> grandChildren = child.getChildLocations();
					for (Location grandChild : grandChildren) {
						locList.add(grandChild);
					}
				}
			}
		}
		return locList;
	}
	
	/*****************/
	/** ANSWER SETS **/
	/*****************/
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(MdrtbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(MdrtbConcepts.RIFAMPICIN_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(MdrtbConcepts.ISONIAZID_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleFqResistanceResults() {
		return this.getConcept(MdrtbConcepts.FLUOROQUINOLONE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInjResistanceResults() {
		return this.getConcept(MdrtbConcepts.INJECTABLE_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(MdrtbConcepts.XPERT_MTB_BURDEN).getAnswers();
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
	
	public Collection<ConceptAnswer> getPossibleIPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCPTreatmentSites() {
		return this.getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRegimens() {
		return this.getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleHIVStatuses() {
		return this.getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleResistanceTypes() {
		return this.getConcept(MdrtbConcepts.RESISTANCE_TYPE).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		// Enable the line below and change the return type to ConceptAnswer
		// return this.getConcept(MdrtbConcepts.DST_RESULT).getAnswers();
		List<Concept> results = new LinkedList<>();
		results.add(this.getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		return results;
	}
	
	/***********/
	/** FORMS **/
	/***********/
	public List<SmearForm> getSmearForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<SmearForm> smears = new ArrayList<>();
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
					SmearForm sf = new SmearForm(e);
					sf.setPatient(tpp.getPatient());
					smears.add(sf);
				}
			}
		}
		// Search in Lab Tests
		if (smears.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					SmearForm sf = new SmearForm(e, labTest);
					sf.setPatient(tpp.getPatient());
					smears.add(sf);
				}
			}
		}
		Collections.sort(smears);
		return smears;
	}
	
	public List<CultureForm> getCultureForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<CultureForm> cultures = new ArrayList<>();
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.CULTURE_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
					CultureForm sf = new CultureForm(e);
					sf.setPatient(tpp.getPatient());
					cultures.add(sf);
				}
			}
		}
		// Search in Lab Tests
		if (cultures.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					CultureForm sf = new CultureForm(e, labTest);
					sf.setPatient(tpp.getPatient());
					cultures.add(sf);
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public List<XpertForm> getXpertForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<XpertForm> xperts = new ArrayList<>();		
		// Fallback to Lab module
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.XPERT_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
					XpertForm sf = new XpertForm(e);
					sf.setPatient(tpp.getPatient());
					xperts.add(sf);
				}
			}
		}
		// Search in Lab Tests
		if (xperts.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					XpertForm sf = new XpertForm(e, labTest);
					sf.setPatient(tpp.getPatient());
					xperts.add(sf);
				}
			}
		}
		Collections.sort(xperts);
		return xperts;
	}
	
	public List<HAINForm> getHAINForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAINForm> hains = new ArrayList<>();
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.HAIN_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
					HAINForm sf = new HAINForm(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		// Search in Lab Tests
		if (hains.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					HAINForm sf = new HAINForm(e, labTest);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<HAIN2Form> getHAIN2Forms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<HAIN2Form> hains = new ArrayList<>();
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			if (MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), e) != null) {
				Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
				if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
					HAIN2Form sf = new HAIN2Form(e);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		// Search in Lab Tests
		if (hains.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					HAIN2Form sf = new HAIN2Form(e, labTest);
					sf.setPatient(tpp.getPatient());
					hains.add(sf);
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	/**
	 * Looks for an Encounter in the given program against which a Specimen sample should exist, and
	 * returns all such encounters.
	 */
	public List<DSTForm> getDstForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DSTForm> dsts = new ArrayList<>();
		// Search in Encounters
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_SPECIMEN_COLLECTION);
		// Search for all Specimen collection encounters containing this patientProgramId
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			// Add the DST form to list
			if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
				DSTForm sf = new DSTForm(e);
				sf.setPatient(tpp.getPatient());
				dsts.add(sf);
			}
		}
		// Search in Lab Tests
		if (dsts.isEmpty()) {
			List<LabTest> labTests = CommonLabUtil.getService().getLabTests(tpp.getPatient(), CommonLabUtil.getService().getCommonTestType());
			if (!labTests.isEmpty()) {
				for (LabTest labTest : labTests) {
					Encounter e = labTest.getOrder().getEncounter();
					DSTForm sf = new DSTForm(e, labTest);
					sf.setPatient(tpp.getPatient());
					dsts.add(sf);
				}
			}
		}
		return dsts;
	}
	
	public List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId) {
		PatientProgram tpp = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
		ArrayList<DrugResistanceDuringTreatmentForm> drdts = new ArrayList<>();
		ArrayList<EncounterType> et = new ArrayList<>();
		et.add(MdrtbConstants.ET_DRUG_RESISTANCE_DURING_TREATMENT);
		List<Encounter> encs = getEncountersByPatientAndTypes(tpp.getPatient(), et);
		for (Encounter e : encs) {
			Obs temp = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (temp != null && temp.getValueNumeric().intValue() == patientProgramId) {
				DrugResistanceDuringTreatmentForm drdt = new DrugResistanceDuringTreatmentForm(e);
				drdt.setPatient(tpp.getPatient());
				drdts.add(drdt);
			}
		}
		Collections.sort(drdts);
		return drdts;
	}
	
	public TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient) {
		TB03Form ret = null;
		Integer encounterId = null;
		EncounterType intakeType = MdrtbConstants.ET_TB03_TB_INTAKE;
		String query = "select encounter_id from encounter where location_id=" + location.getId()
		        + " AND encounter_datetime <= '" + encounterDate + "' AND patient_id=" + patient.getId()
		        + " AND encounter_type=" + intakeType.getId() + " AND voided=0 ORDER BY encounter_datetime DESC";
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		if (result != null && !result.isEmpty()) {
			List<Object> resp = result.get(0);
			if (resp != null) {
				encounterId = (Integer) (resp.get(0));
			}
			
		}
		if (encounterId != null)
			ret = new TB03Form(Context.getEncounterService().getEncounter(encounterId));
		return ret;
	}
	
	public List<TB03Form> getTB03FormsForProgram(Patient patient, Integer patientProgramId) {
		ArrayList<TB03Form> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgramId) {
				forms.add(new TB03Form(e));
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsForProgram(Patient patient, Integer patientProgramId) {
		ArrayList<Form89> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgramId) {
				forms.add(new Form89(e));
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsFilledForPatientProgram(Patient patient, Location location, Integer patientProgramId,
	        Integer year, String quarter, String month) {
		ArrayList<Form89> forms = new ArrayList<>();
		Map<String, Date> dateMap = null;
		if (year != null && (quarter != null || month != null))
			dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = null;
		Date endDate = null;
		if (dateMap != null) {
			startDate = (dateMap.get("startDate"));
			endDate = (dateMap.get("endDate"));
		}
		Concept ppid = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = getEncounters(patient, location, startDate, endDate, typeList);
		if (temp != null) {
			for (Encounter e : temp) {
				Obs ppObs = MdrtbUtil.getObsFromEncounter(ppid, e);
				if (ppObs != null) {
					if (ppObs.getValueNumeric() != null
					        && (ppObs.getValueNumeric().intValue() == patientProgramId)) {
						forms.add(new Form89(e));
					}
				}
			}
		}
		return forms;
	}
	
	public RegimenForm getPreviousRegimenForm(Patient patient, List<Location> locactions, Date beforeDate) {
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		ArrayList<RegimenForm> forms = new ArrayList<>();
		if (locactions == null || locactions.isEmpty()) {
			temp = getEncounters(patient, null, null, beforeDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		} else {
			for (Location l : locactions) {
				temp = getEncounters(patient, l, null, beforeDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
		}
		if (forms != null && !forms.isEmpty()) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public RegimenForm getCurrentRegimenForm(Patient patient, Date beforeDate) {
		// RegimenForm form = null;
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		Date currentDate = null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(beforeDate.getTime());
		gc.add(Calendar.DATE, 1);
		currentDate = gc.getTime();
		ArrayList<RegimenForm> forms = new ArrayList<>();
		temp = getEncounters(patient, null, null, currentDate, typeList);
		for (Encounter e : temp) {
			forms.add(new RegimenForm(e));
		}
		if (forms != null && !forms.isEmpty()) {
			Collections.sort(forms);
			return forms.get(forms.size() - 1);
		} else
			return null;
	}
	
	public List<TB03uForm> getTB03uFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TB03uForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TB03uForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<Form89> getForm89FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<Form89> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new Form89(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new Form89(e));
				}
			}
		}
		return forms;
	}
	
	public List<TB03Form> getTB03FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<TB03Form> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03_TB_INTAKE);
		List<Encounter> temp = null;
		if (locations != null && !locations.isEmpty()) {
			for (Location l : locations) {
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
	
	public List<TransferInForm> getTransferInFormsFilled(List<Location> locations, Integer year, String quarter, String month) {
		ArrayList<TransferInForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferInForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferInForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<TransferInForm> getTransferInFormsFilledForPatient(Patient patient) {
		ArrayList<TransferInForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TRANSFER_IN);
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferInForm(e));
		}
		return forms;
	}
	
	public List<TransferOutForm> getTransferOutFormsFilled(List<Location> locations, Integer year, String quarter,
	        String month) {
		ArrayList<TransferOutForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new TransferOutForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new TransferOutForm(e));
				}
			}
		}
		return forms;
	}
	
	public List<TransferOutForm> getTransferOutFormsFilledForPatient(Patient patient) {
		ArrayList<TransferOutForm> forms = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_TRANSFER_OUT;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			forms.add(new TransferOutForm(e));
		}
		return forms;
	}
	
	public TB03uForm getTB03uFormForProgram(Patient p, Integer patientProgId) {
		TB03uForm form = null;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		
		List<Encounter> temp = null;
		Concept idConcept = getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId) {
				form = new TB03uForm(e);
				break;
			}
		}
		return form;
	}
	
	public ArrayList<RegimenForm> getRegimenFormsForProgram(Patient patient, Integer patientProgId) {
		ArrayList<RegimenForm> forms = new ArrayList<>();
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		temp = getEncountersByPatientAndTypes(patient, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId) {
				forms.add(new RegimenForm(e));
			}
		}
		// Collections.reverse(forms);
		Collections.sort(forms);
		return forms;
	}
	
	public List<TB03uForm> getTB03uFormsWithTreatmentStartedDuring(List<Location> locations, Integer year, String quarter,
	        String month) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;
		
		// CHECK
		if (locations == null || locations.isEmpty()) {
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
			for (Location l : locations) {
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
	
	public List<TB03uForm> getTB03uFormsForProgram(Patient patient, Integer mdrtbProgramId) {
		ArrayList<TB03uForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_TB03U_MDRTB_INTAKE);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = Context.getService(MdrtbService.class).getEncounters(patient, null, null, null, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == mdrtbProgramId) {
				forms.add(new TB03uForm(e));
			}
		}
		return forms;
	}
	
	public List<AdverseEventsForm> getAEFormsForProgram(Patient p, Integer patientProgId) {
		ArrayList<AdverseEventsForm> forms = new ArrayList<>();
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		List<Encounter> temp = null;
		Concept idConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		temp = getEncountersByPatientAndTypes(p, typeList);
		for (Encounter e : temp) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(idConcept, e);
			if (idObs != null && idObs.getValueNumeric() != null
			        && idObs.getValueNumeric().intValue() == patientProgId) {
				forms.add(new AdverseEventsForm(e));
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public List<AdverseEventsForm> getAEFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<AdverseEventsForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(MdrtbConstants.ET_ADVERSE_EVENT);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new AdverseEventsForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new AdverseEventsForm(e));
				}
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public List<RegimenForm> getRegimenFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month) {
		ArrayList<RegimenForm> forms = new ArrayList<>();
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		Date startDate = (dateMap.get("startDate"));
		Date endDate = (dateMap.get("endDate"));
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate.getTime());
		endCal.add(Calendar.DATE, 1);
		EncounterType eType = MdrtbConstants.ET_PV_REGIMEN;
		ArrayList<EncounterType> typeList = new ArrayList<>();
		typeList.add(eType);
		List<Encounter> temp = null;
		if (locations == null || locations.isEmpty()) {
			temp = getEncounters(null, null, startDate, endDate, typeList);
			for (Encounter e : temp) {
				forms.add(new RegimenForm(e));
			}
		} else {
			for (Location l : locations) {
				temp = getEncounters(null, l, startDate, endDate, typeList);
				for (Encounter e : temp) {
					forms.add(new RegimenForm(e));
				}
			}
		}
		Collections.sort(forms);
		return forms;
	}
	
	public void evict(Object obj) {
		dao.evict(obj);
	}
	
	public Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate,
	        Date toDate) {
		Cohort patientsInStates = Context.getService(CohortQueryService.class).getPatientsInStates(stateList, fromDate,
		    toDate);
		List<PatientProgram> list = Context.getProgramWorkflowService().getPatientPrograms(patientsInStates,
		    Arrays.asList(program));
		return new Cohort(list);
	}
	
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath) {
		// first call the main Patient Service process death method
		Context.getPatientService().processDeath(patient, deathDate, causeOfDeath, null);
		// if the most recent MDR-TB program is open, we need to close it
		MdrtbPatientProgram program = getMostRecentMdrtbPatientProgram(patient);
		if (program != null && program.getActive()) {
			program.setDateCompleted(deathDate);
			program.setOutcome(getProgramWorkflowState(Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.DEATH)));
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
		// if the patient is hospitalized, we need to end the hospitalization
		if (program != null && program.getCurrentlyHospitalized()) {
			program.closeCurrentHospitalization(deathDate);
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
	}
	
	public Smear createSmear(Specimen specimen) {
		return null;
	}
	
	public Smear createSmear(Specimen specimen, Smear smear) {
		return null;
	}
	
	public void saveSmear(Smear smear) {
	}
	
	/*******************/
	/** SAVED REPORTS **/
	/*******************/
	public ReportData getReportData(Integer id) throws APIException {
		return dao.getReportData(id);
	}
	
	public ReportData getReportDataByUuid(String uuid) throws APIException {
		return dao.getReportDataByUuid(uuid);
	}
	
	public ReportData saveReportData(ReportData reportData) throws APIException {
		return dao.saveReportData(reportData);
	}
	
	public void voidReportData(ReportData reportData, String reason) throws APIException {
		if (!reportData.getVoided()) {
			reportData.setVoided(true);
			reportData.setVoidReason(reason);
			reportData.setDateVoided(new Date());
			reportData.setVoidedBy(Context.getAuthenticatedUser());
			dao.saveReportData(reportData);
		}
	}
	
	public void unvoidReportData(ReportData reportData) throws APIException {
		if (reportData.getVoided()) {
			reportData.setVoided(false);
			reportData.setVoidReason("Previously voided with reason:" + reportData.getVoidReason());
			reportData.setDateVoided(null);
			reportData.setVoidedBy(null);
			dao.saveReportData(reportData);
		}
	}
	
	public List<ReportData> searchReportData(Location region, Location district, Location facility, Integer year,
	        Integer quarter, Integer month, String reportName, ReportType reportType) {
		return dao.searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
	}
	
	public void unlockReport(ReportData reportData) {
		if (reportData.getVoided()) {
			throw new APIException("Cannot unlock a voided report");
		}
		reportData.setReportStatus(ReportStatus.UNLOCKED);
		dao.saveReportData(reportData);
	}
	
	public boolean getReportArchived(Integer region, Integer district, Integer facility, Integer year, Integer quarter,
	        Integer month, String name, ReportType reportType) {
		return dao.getReportArchived(region, district, facility, year, quarter, month, name, reportType);
	}
	
	public List<String> readTableData(Integer regionId, Integer districtId, Integer facilityId, Integer year,
	        Integer quarter, Integer month, String name, ReportType reportType) {
		return dao.getReportDataAsList(regionId, districtId, facilityId, year, quarter, month, name, reportType);
	}
	
	public List<List<Object>> getReportsWithoutData(ReportType reportType) {
		return dao.getReports(reportType.toString());
	}
	
	public void lockReport(ReportData reportData) {
		if (reportData.getReportStatus() == ReportStatus.UNLOCKED) {
			try {
				dao.saveReportData(reportData);
				saveReportData(reportData);
			}
			catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
	}
	
	@Override
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save DST: DST object is null");
			return;
		}
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation
		// that it expects, which should return a observation)
		if (!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by MDRTB module");
	}
}
