package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.TransferOutForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
import org.openmrs.module.mdrtb.reporting.custom.DQUtil;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/*import org.openmrs.module.mdrtbdrugforecast.DrugCount;
import org.openmrs.module.mdrtbdrugforecast.MdrtbDrugStock;
import org.openmrs.module.mdrtbdrugforecast.MdrtbUtil;
import org.openmrs.module.mdrtbdrugforecast.MdrtbConcepts;
import org.openmrs.module.mdrtbdrugforecast.drugneeds.DrugForecastUtil;
import org.openmrs.module.mdrtbdrugforecast.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtbdrugforecast.regimen.Regimen;
import org.openmrs.module.mdrtbdrugforecast.regimen.RegimenUtils;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedOnDrugCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.service.MdrtbDrugForecastService;
import org.openmrs.module.mdrtbdrugforecast.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtbdrugforecast.web.controller.status.DashboardTreatmentStatusRenderer;*/
import org.springframework.web.servlet.ModelAndView;

@Controller

public class DOTSDQController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
        binder.registerCustomEditor(Location.class, new LocationEditor());
    }
        
    
    @RequestMapping(method=RequestMethod.GET, value="/module/mdrtb/reporting/dotsdq")
    public ModelAndView showRegimenOptions(@RequestParam(value="loc", required=false) String district,
			@RequestParam(value="ob", required=false) String oblast,
			@RequestParam(value="yearSelected", required=false) Integer year,
			@RequestParam(value="quarterSelected", required=false) String quarter,
			@RequestParam(value="monthSelected", required=false) String month,
			ModelMap model) {
    	
    	List<Region> oblasts;
        List<Facility> facilities;
        List<District> districts;
    	
    	if(oblast==null) {
    		oblasts = Context.getService(MdrtbService.class).getOblasts();
    		model.addAttribute("oblasts", oblasts);
    	}
    	 
    	
    	else if(district==null)
         { 
    		//DUSHANBE
    		if(Integer.parseInt(oblast)==186) {
    			oblasts = Context.getService(MdrtbService.class).getOblasts();
    			districts= Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
    			District d = districts.get(0);
    			facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
    			model.addAttribute("oblastSelected", oblast);
    			model.addAttribute("oblasts", oblasts);
           	 	model.addAttribute("districts", districts);
           	 	model.addAttribute("facilities", facilities);
           	 	model.addAttribute("dushanbe", 186);
    		}
    		
    		else {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class)
						.getDistricts(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
    		}
         }
    	
         else
         {
        	 /*
      		 * if oblast is dushanbe, return both districts and facilities
      		 */
     		if(Integer.parseInt(oblast)==186) {
     			oblasts = Context.getService(MdrtbService.class).getOblasts();
     			districts= Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
     			District d = districts.get(0);
     			facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
     			model.addAttribute("oblastSelected", oblast);
     			model.addAttribute("oblasts", oblasts);
            	 	model.addAttribute("districts", districts);
            	 	model.addAttribute("facilities", facilities);
            	 	model.addAttribute("dushanbe", 186);
     		}
     		
     		else {
        	 
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class)
						.getDistricts(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class)
						.getFacilities(Integer.parseInt(district));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("facilities", facilities);
     		}
         }
    	
    	 model.addAttribute("yearSelected", year);
    	 model.addAttribute("monthSelected", month);
    	 model.addAttribute("quarterSelected", quarter);
       
        /*List<Location> locations = Context.getLocationService().getAllLocations(false);// Context.getLocationService().getAllLocations();//ms = (MdrtbDrugForecastService) Context.getService(MdrtbDrugForecastService.class);
        List<Region> oblasts = Context.getService(MdrtbService.class).getOblasts();
        //drugSets =  ms.getMdrtbDrugs();
        
       

        model.addAttribute("locations", locations);
        model.addAttribute("oblasts", oblasts);*/
    	 return new ModelAndView("/module/mdrtb/reporting/dotsdq", model);
    
    }
    
    
    @SuppressWarnings("unused")
	@RequestMapping(method=RequestMethod.POST, value="/module/mdrtb/reporting/dotsdq")
    public static String doDQ(
    		@RequestParam("district") Integer districtId,
    		@RequestParam("oblast") Integer oblastId,
    		@RequestParam("facility") Integer facilityId,
            @RequestParam(value="year", required=true) Integer year,
            @RequestParam(value="quarter", required=false) String quarter,
            @RequestParam(value="month", required=false) String month,
            ModelMap model) throws EvaluationException {
    	
    //	System.out.println("---POST-----");
    //	System.out.println("PARAMS:" + location + " " + oblast + " " + year + " " + quarter + " " + month);
    	
    	//Cohort patients = TbUtil.getDOTSPatientsTJK(null, null, location, oblast, null, null, null, null,year,quarter,month);
    	//Cohort patients = 
    	
    	/*Region o = null;
    	if(oblast!=null && !oblast.equals("") && location == null)
			o =  Context.getService(MdrtbService.class).getOblast(Integer.parseInt(oblast));
    	String oName = "";
    	if(o!=null) {
    		oName = o.getName();
    	}
		
		List<Location> locList = new ArrayList<Location>();
		if(o != null && location == null)
			locList = Context.getService(MdrtbService.class).getLocationsFromOblastName(o);
		else if (location != null)
			locList.add(location);
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
		
		System.out.println("ST: " + startDate);
		System.out.println("ED: " + endDate);
		
		CohortDefinition baseCohort = null;
		
		//OBLAST
		if (!locList.isEmpty()){
			List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();
			for(Location loc : locList)
				cohortDefinitions.add(Cohorts.getTB03ByDatesAndLocation(startDate, endDate, loc));
				
			if(!cohortDefinitions.isEmpty()){
				baseCohort = ReportUtil.getCompositionCohort("OR", cohortDefinitions);
			}
		}
		
		else
			baseCohort = Cohorts.getTB03ByDatesAndLocation(startDate, endDate, null);
    	
    	Cohort patients = Context.getService(CohortDefinitionService.class).evaluate(baseCohort, new EvaluationContext());
 
		Form tb03Form = Context.getFormService().getForm(MdrtbConstants.TB03_FORM_ID);
		ArrayList<Form> formList = new ArrayList<Form>();
		formList.add(tb03Form);
    	
    	
    	Set<Integer> idSet = patients.getMemberIds();
    	ArrayList<TB03Data> patientSet  = new ArrayList<TB03Data>();*/
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	
    	/*ArrayList<Person> patientList = new ArrayList<Person>();
    	ArrayList<Concept> conceptQuestionList = new ArrayList<Concept>();
    	ArrayList<Concept> conceptAnswerList = new ArrayList<Concept>();
    	
    	List<Obs> obsList = null;*/
    	
    	List<DQItem> missingTB03 = new ArrayList<DQItem>();
    	List<DQItem> missingAge = new ArrayList<DQItem>();
    	List<DQItem> missingPatientGroup = new ArrayList<DQItem>();
    	List<DQItem> noForm89 = new ArrayList<DQItem>();
    	List<DQItem> invalidForm89 = new ArrayList<DQItem>();
    	List<DQItem> missingDiagnosticTests = new ArrayList<DQItem>();
    	List<DQItem> notStartedTreatment = new ArrayList<DQItem>();
    	List<DQItem> missingOutcomes = new ArrayList<DQItem>();
    	//List<DQItem> missingAddress = new ArrayList<DQItem>();
    	List<DQItem> noDOTSId = new ArrayList<DQItem>();
    	List<DQItem> noSite = new ArrayList<DQItem>();
    	List<DQItem> noTifAfterTransferOut = new ArrayList<DQItem>();
    	List<DQItem> noTofBeforeTransferIn = new ArrayList<DQItem>();
    	List<DQItem> duplicateTB03 = new ArrayList<DQItem>();
    	List<DQItem> duplicateForm89 = new ArrayList<DQItem>();
    	List<DQItem> unlinkedTB03 = new ArrayList<DQItem>();
    	List<Patient> errList = new ArrayList<Patient>();
    	
    	Boolean errorFlag = Boolean.FALSE;
    	Integer errorCount = 0;
    	
    	Date treatmentStartDate = null;
    	Calendar tCal = null;
    	Calendar nowCal = null;
    	long timeDiff = 0;
    	double diffInWeeks = 0;
    	
    	SmearForm diagnosticSmear = null;
	    XpertForm firstXpert = null;
	    HAINForm firstHAIN = null;
	    CultureForm diagnosticCulture  = null;
	    Boolean eptb = Boolean.FALSE;
	    Boolean child = Boolean.FALSE;
	    DQItem dqi = null;
	    
	    Integer eptbConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getConceptId();
	    
    	sdf.applyPattern("dd.MM.yyyy");
    	SimpleDateFormat rdateSDF = new SimpleDateFormat();
    	rdateSDF.applyPattern("dd.MM.yyyy HH:mm:ss");
    	
    	//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
    	
    	ArrayList<Location> locList = null;
    	if(oblastId!=null) {
    		if(oblastId.intValue()==186) {
    			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId,districtId,facilityId);
    		}
    		else {
    			locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
    		}
    	}
    	
    	ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
    	ArrayList<TransferOutForm> tofList = Context.getService(MdrtbService.class).getTransferOutFormsFilled(locList, year, quarter, month);
    	ArrayList<TransferInForm> tifList = Context.getService(MdrtbService.class).getTransferInFormsFilled(locList, year, quarter, month);
    	ArrayList<TransferOutForm> allTofs = null;// Context.getService(MdrtbService.class).getTransferOutFormsFilled(locList, year, quarter, month);
    	ArrayList<TransferInForm> allTifs = null;// Context.getService(MdrtbService.class).getTransferInFormsFilled(locList, year, quarter, month);
    	HashMap<Integer, Integer> dupMap = new HashMap<Integer,Integer>();
    	HashMap<Integer, Integer> f89DupMap = new HashMap<Integer,Integer>();
    	
    	
    	
    	/*List<Encounter> tb03EncList = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, formList, null, null, false);
 	    if(tb03EncList==null || tb03EncList.size() == 0) {
 	    	missingTB03.add(dqi);
 	    	errorFlag = Boolean.TRUE;
 	    }
    	*/
    	Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
    	Integer countNum = 0;
    	
    	for (TB03Form  tf : tb03List) {
    		
    		 //INIT
    	    treatmentStartDate = null;
    		tCal = null;
    		nowCal = null;
    		timeDiff = 0;
    		diffInWeeks = 0;
    		diagnosticSmear = null;
    	    firstXpert = null;
    	    firstHAIN = null;
    	    diagnosticCulture  = null;
    	   // patientList.clear();
    	    errorFlag = Boolean.FALSE;
    	    eptb = Boolean.FALSE;
    	    child = Boolean.FALSE;
    		
    		dqi = new DQItem();
    	    Patient patient = tf.getPatient();//Context.getPatientService().getPatient(i);
    	    
    	    if(patient==null || patient.isVoided()) {
    	    	continue;
    	    }
    	    
    	    if(patient.getGender().equals("F") && Context.getLocale().equals("ru")) {
    	    	patient.setGender(Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female"));
    	    }
    	   // patientList.add(patient);
    	    dqi.setPatient(patient);
    	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    	    

    	   /* //Missing TB03
    	   */
    	    
    	    //Missing Age at Registration
    	    /*Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_DOTS_REGISTRATION);
    	    
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);*/
    	    
    	    //DUPLICATE TB03
    	    Integer patProgId   = null;
    	    
    	    patProgId = tf.getPatProgId();
    	    Boolean found = Boolean.FALSE;
    	 //   HashMap<Integer,Integer> pp = new HashMap<Integer,Integer>();
    	    if(patProgId!=null && !dupMap.containsKey(patProgId)) {
    	    	List<TB03Form> dupList = Context.getService(MdrtbService.class).getTB03FormsForProgram(patient, patProgId);
    	    	
    	    	if(dupList!=null) {
    	    		
    	    		if(dupList.size() > 1) {
    	    			for(TB03Form form : dupList) {
    	    				if(form.getPatProgId().intValue()==patProgId.intValue()) {
    	    					dqi.addTb03Link(form.getLink());
    	    					found = Boolean.TRUE;
    	    					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>FOUND:" + tf.getPatProgId());
    	    				}
    	    			}
    	    		}
    	    	}
    	    	
    	    	if(found) {
    	    		dupMap.put(patProgId, patProgId);
    	    		errorFlag = Boolean.TRUE;
    	    		duplicateTB03.add(dqi);
    	    	}
    	    }
    	    
    	    else { //UNLINKED TB03
    	    	String link = "";
    	    	dqi.addTb03Link(link);
    	    	unlinkedTB03.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    
    	    //duplicate FORM89

    	    
    	     found = Boolean.FALSE;
    	 //   HashMap<Integer,Integer> pp = new HashMap<Integer,Integer>();
    	    if(patProgId!=null && !f89DupMap.containsKey(patProgId)) {
    	    	List<Form89> formList = Context.getService(MdrtbService.class).getForm89FormsForProgram(patient, patProgId);
    	    	
    	    	if(formList!=null) {
    	    		
    	    		if(formList.size() > 1) {
    	    			for(Form89 form : formList) {
    	    					dqi.addForm89Link(form.getLink());
    	    					found = Boolean.TRUE;
    	    				
    	    			}
    	    		}
    	    	}
    	    	
    	    	if(found) {
    	    		f89DupMap.put(patProgId, patProgId);
    	    		errorFlag = Boolean.TRUE;
    	    		duplicateForm89.add(dqi);
    	    	}
    	    }
    	    
    	   //Patient not new but has form89
    	   
    	    if(tf.getRegistrationGroup()!=null) {
    	    	if(tf.getRegistrationGroup().getConceptId().intValue()!=Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
    	    		List<Form89> formList = Context.getService(MdrtbService.class).getForm89FormsForProgram(patient, patProgId);
        	    	
        	    	if(formList!=null && formList.size()!=0) {
        	    		errorFlag = Boolean.TRUE;
        	    		invalidForm89.add(dqi);
        	    	}
        	    	
    	    	}	
    	   
    	    }
    	    
    	    
    	    
    	    
    	    if(tf.getAgeAtTB03Registration()==null) { //obsList==null || obsList.size()==0) {
    	    	missingAge.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    //MISSING REGISTRATION GROUP
    	   /* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList==null || obsList.size()==0) {*/
    	    
    	    if(tf.getRegistrationGroup()==null) {
    	    	missingPatientGroup.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    
    	    
    	    
    	    else if(tf.getRegistrationGroup().getId().intValue()== (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW).getId().intValue())) {
    	    	ArrayList<Form89> f89 = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(tf.getPatient(), null, tf.getPatProgId(), year, quarter, month);
    	    	if(f89==null || f89.size()==0) {
    	    		noForm89.add(dqi);
    	    		errorFlag = Boolean.TRUE;
    	    	}
    	    }
    	    
    	    //NOT STARTED TREATMENT
    	   /* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DOTS_TREATMENT_START_DATE);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList==null || obsList.size()==0) {*/
    	    
    	    if(tf.getTreatmentStartDate()==null) {
    	    	notStartedTreatment.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    else {
    	    	 //MISSING OUTCOMES
    	    	
    	    		 treatmentStartDate = tf.getTreatmentStartDate();
    	    		 tCal = new GregorianCalendar();
    	    		 tCal.setTime(treatmentStartDate);
    	    		 nowCal = new GregorianCalendar();
    	    		 timeDiff = nowCal.getTimeInMillis() - tCal.getTimeInMillis();
    	    		 diffInWeeks = DQUtil.timeDiffInWeeks(timeDiff);
    	    		 if(diffInWeeks > 32) {
    	    			 
    	    	    	    if(tf.getTreatmentOutcome()==null) {
    	    	    	    	missingOutcomes.add(dqi);
    	    	    	    	errorFlag = Boolean.TRUE;
    	    	    	    }
    	    		 } 
    	    	 }
    	    
    	  /*  if(tf.getTreatmentStartDate()==null) {
    	    	notStartedTreatment.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    else {
    	    	 //MISSING OUTCOMES
    	    	
    	    		 treatmentStartDate = tf.getTreatmentStartDate();
    	    		 tCal = new GregorianCalendar();
    	    		 tCal.setTime(treatmentStartDate);
    	    		 nowCal = new GregorianCalendar();
    	    		 timeDiff = nowCal.getTimeInMillis() - tCal.getTimeInMillis();
    	    		 diffInWeeks = DQUtil.timeDiffInWeeks(timeDiff);
    	    		 if(diffInWeeks > 32) {
    	    			 
    	    	    	    if(tf.getTreatmentOutcome()==null) {
    	    	    	    	missingOutcomes.add(dqi);
    	    	    	    	errorFlag = Boolean.TRUE;
    	    	    	    }
    	    		 } 
    	    	 }*/
    	    //NO SITE
    	    
    	   /* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList==null || obsList.size()==0) {*/
    	     if(tf.getAnatomicalSite()==null) {
    	    	noSite.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    
    	    else {
    	    	
    	    	
    	    	if(tf.getAnatomicalSite().getConceptId().intValue()==eptbConcept.intValue())
    	    	{
    	    		
    	    		eptb = Boolean.TRUE;
    	    	}
    	    }
    	    
    	    
    	    
    	   /* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_DOTS_REGISTRATION);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList!=null && obsList.size()!=0) {*/
    	     if(tf.getAgeAtTB03Registration()!=null) {
    	    	Integer age =  tf.getAgeAtTB03Registration();
    	    	
    	    	if(age > 14)
    	    		child = Boolean.FALSE;
    	    	else
    	    		child = Boolean.TRUE;
    	    }
    	    
    	    
    	    //MISSING DIAGNOSTIC TESTS

    	    
    	    diagnosticSmear = TB03Util.getDiagnosticSmearForm(tf);
    	    firstXpert = TB03Util.getFirstXpertForm(tf);
    	    firstHAIN = TB03Util.getFirstHAINForm(tf);
    	    diagnosticCulture  = TB03Util.getDiagnosticCultureForm(tf);
    	    
    	   
    	    if(diagnosticSmear == null && diagnosticCulture == null && firstXpert==null && firstHAIN == null && eptb == Boolean.FALSE && child ==Boolean.FALSE) {
    	    	
    	    	missingDiagnosticTests.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }
    	    
    	    
    	    //MISSING DOTS ID
    	   /* List<PatientIdentifier> ids = patient.getActiveIdentifiers();
    	    Boolean idFound = Boolean.FALSE;
    	    for(PatientIdentifier pi : ids) {
    	    	if(pi.getIdentifierType().getId()==2) 
    	    	{
    	    		idFound = Boolean.TRUE;
    	    		break;
    	    	}
    	    }
    	    
    	    if(!idFound) {
    	    	noDOTSId.add(dqi);
    	    	errorFlag = Boolean.TRUE;
    	    }*/
    	    
    	    if(tf.getPatProgId()!=null) {
    	    	
    	    	if(Context.getService(MdrtbService.class).getGenPatientProgramIdentifier(Context.getService(MdrtbService.class).getTbPatientProgram(tf.getPatProgId()).getPatientProgram())==null) {
    	    		noDOTSId.add(dqi);
        	    	errorFlag = Boolean.TRUE;
    	    	}
    	    	
    	    }
    	    
    	    
    	    
    	    if(errorFlag) {
    	    	errorCount ++;
    	    	errList.add(patient);
    	    	
    	    }
    	    
    	    countNum++;

    	    
    	}
    	
    	  //TRANSFER OUT BUT NO TRANSFER IN
	    //get latest transfer out with any of these locations for any patient
	    //if no transferIn in list entered after that date for patient add error
    	Boolean foundFlag = Boolean.FALSE;

    	for(TransferOutForm tof : tofList) {
    		Location tofLoc = tof.getLocation();
    		Date tofDate = tof.getEncounterDatetime();
    		Patient tofPatient = tof.getPatient();
    		dqi = new DQItem();
    	    Patient patient = tof.getPatient();//Context.getPatientService().getPatient(i);
    	    

    	    if(patient==null || patient.isVoided()) {
    	    	continue;
    	    }
    	   // patientList.add(patient);
    	    dqi.setPatient(patient);
    	    dqi.setLocName(tofLoc.getDisplayString());
    	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    		foundFlag = Boolean.FALSE;
    		errorFlag = Boolean.FALSE;
    		allTifs = Context.getService(MdrtbService.class).getTransferInFormsFilledForPatient(patient);
    		for(TransferInForm tif : allTifs) {
    			if(tofLoc.equals(tif.getLocation()) && tofPatient.equals(tif.getPatient())) {
    				if(tif.getEncounterDatetime().after(tofDate)) {
    					foundFlag = Boolean.TRUE;
    					break;
    				}
    			}
    		}
    		
    		if(!foundFlag) {
    			
    			if(!errList.contains(patient)) {
    				errorCount++;
    				errList.add(patient);
    			}
    			
    			noTifAfterTransferOut.add(dqi);
    			
    			
    		}
    	}
    	
    	
    	  //TRANSFER In  BUT NO TRANSFER Out
	    //get latest transfer out with any of these locations for any patient
	    //if no transferIn in list entered after that date for patient add error
    	foundFlag = Boolean.FALSE;
    	
    	for(TransferInForm tif : tifList) {
    		Location tifLoc = tif.getLocation();
    		Date tifDate = tif.getEncounterDatetime();
    		Patient tifPatient = tif.getPatient();
    		dqi = new DQItem();
    	    Patient patient = tif.getPatient();//Context.getPatientService().getPatient(i);
    	    
    	    if(patient==null || patient.isVoided()) {
    	    	continue;
    	    }
    	   // patientList.add(patient);
    	    dqi.setPatient(patient);
    	    dqi.setLocName(tifLoc.getDisplayString());
    	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    		foundFlag = Boolean.FALSE;
    		errorFlag = Boolean.FALSE;
    		allTofs = Context.getService(MdrtbService.class).getTransferOutFormsFilledForPatient(patient);
    		for(TransferOutForm tof : tofList) {
    			if(tifLoc.equals(tof.getLocation()) && tifPatient.equals(tof.getPatient())) {
    				if(tof.getEncounterDatetime().before(tifDate)) {
    					foundFlag = Boolean.TRUE;
    					break;
    				}
    			}
    		}
    		
    		if(!foundFlag) {
    			
    			if(!errList.contains(patient)) {
    				errorCount++;
    				errList.add(patient);
    			}
    			
    			noTofBeforeTransferIn.add(dqi);
    			
    			
    		}
    		
    		
    	}
    	
    	/*TbPatientProgram temp = null;
    	
    	List<TbPatientProgram> progList = Context.getService(MdrtbService.class).getAllTbPatientProgramsEnrolledInDateRangeAndLocations(startDate, endDate, locList);
    	//Integer countNum = 0;
    	Boolean matched = Boolean.FALSE;
    	if(progList!=null) {
    		for(TbPatientProgram p : progList) {
    			matched = Boolean.FALSE;
    			dqi = new DQItem();
        	    Patient patient = p.getPatient();//Context.getPatientService().getPatient(i);
        	    
        	    if(patient==null || patient.isVoided()) {
        	    	continue;
        	    }
        	    
        	    for(TB03Form t3f : tb03List) {
        	    	Patient tempPat = t3f.getPatient();
        	    	
        	    	if(tempPat.getId().intValue()==patient.getId().intValue()) {
        	    		matched = Boolean.TRUE;
        	    		break;
        	    	}
        	    	
        	    	
        	    }
        	    
        	    if(!matched)
        	    	countNum++;
        	    
        	    if(patient.getGender().equals("F") && Context.getLocale().equals("ru")) {
        	    	patient.setGender(Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female"));
        	    }
        	   // patientList.add(patient);
        	    dqi.setPatient(patient);
        	    dqi.setDateOfBirth(sdf.format(patient.getBirthdate()));
    			
    			ArrayList<TB03Form> x = Context.getService(MdrtbService.class).getTB03FormsForProgram(patient, p.getId());
    		
    			if(x==null || x.size()==0) {
    				//errorFlag = Boolean.TRUE;
    				missingTB03.add(dqi);
    				
    				if(!errList.contains(patient)) {
        				errorCount++;
        				errList.add(patient);
        			}
    				
    			
    			}	
    		}
    		
    	}*/
    	
    	Integer num = countNum;// + tofList.size();
    	
    	
    	Integer errorPercentage = null;
    	if(num==0)
    		errorPercentage = 0;
    	else
    		errorPercentage = (errorCount*100)/num;
    	
    	String oName = null;
    	Region obl = Context.getService(MdrtbService.class).getOblast(oblastId);
    	if(obl!=null)
    		oName = obl.getName();
    	
    	String dName = null;
    	if(districtId!=null) {
    		District dist = Context.getService(MdrtbService.class).getDistrict(districtId);
    		if(dist!=null)
    			dName = dist.getName();
    	}
    	
    	String fName = null;
    	if(facilityId!=null) {
    		Facility fac = Context.getService(MdrtbService.class).getFacility(facilityId);
    		if(fac!=null)
    			fName = fac.getName();
    	}
    	
    	model.addAttribute("num", num);
    	//model.addAttribute("missingTB03", missingTB03);
    	model.addAttribute("duplicateTB03", duplicateTB03);
    	model.addAttribute("duplicateForm89", duplicateForm89);
    	model.addAttribute("unlinkedTB03", unlinkedTB03);
    	model.addAttribute("missingForm89", noForm89);
    	model.addAttribute("invalidForm89", invalidForm89);
    	model.addAttribute("missingAge", missingAge);
    	model.addAttribute("missingPatientGroup", missingPatientGroup);
    	model.addAttribute("missingDiagnosticTests", missingDiagnosticTests);
    	model.addAttribute("notStartedTreatment", notStartedTreatment);
    	model.addAttribute("missingOutcomes", missingOutcomes);
    	model.addAttribute("noDOTSId", noDOTSId);
    	model.addAttribute("noSite", noSite);
    	model.addAttribute("noTrasnferIn", noTifAfterTransferOut);
    	model.addAttribute("noTransferOut", noTofBeforeTransferIn);
    	model.addAttribute("errorCount", new Integer(errorCount));
    	model.addAttribute("errorPercentage", errorPercentage.toString() + "%");
    	model.addAttribute("oName", oName);
    	model.addAttribute("dName", dName);
    	model.addAttribute("fName", fName);
    	
    	
    	
    	
    	model.addAttribute("locale", Context.getLocale().toString());
    	
    	// TO CHECK WHETHER REPORT IS CLOSED OR NOT
    	/*Integer report_oblast = null; Integer report_quarter = null; Integer report_month = null;
		if(new PDFHelper().isInt(oblast)) { report_oblast = Integer.parseInt(oblast); }
		if(new PDFHelper().isInt(quarter)) { report_quarter = Integer.parseInt(quarter); }
		if(new PDFHelper().isInt(month)) { report_month = Integer.parseInt(month); }*/
		
    	boolean reportStatus = Context.getService(MdrtbService.class).readReportStatus(oblastId, districtId, facilityId, year, quarter, month, "DOTSDQ", "DOTSTB");
    	
    	model.addAttribute("oblast", oblastId);
    	model.addAttribute("facility", facilityId);
    	model.addAttribute("district", districtId);
    	model.addAttribute("year", year);
    	if(month!=null && month.length()!=0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if(quarter!=null && quarter.length()!=0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
    	model.addAttribute("reportDate", rdateSDF.format(new Date()));
    	model.addAttribute("reportStatus", reportStatus);
        return "/module/mdrtb/reporting/dotsdqResults";
    }
    
    
  
    
}
