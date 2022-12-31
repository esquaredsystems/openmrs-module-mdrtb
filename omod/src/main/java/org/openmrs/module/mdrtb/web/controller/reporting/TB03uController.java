package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.reporting.custom.TB03uData;
import org.openmrs.module.mdrtb.reporting.custom.TB03uUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
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
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings({ "deprecation", "unused" })
@Controller
public class TB03uController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/tb03u")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
		}
		
		else if (district == null) {
			
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		}
		
		else {
			
			/*
			* if oblast is dushanbe, return both districts and facilities
			*/
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("facilities", facilities);
			}
		}
		
		model.addAttribute("yearSelected", year);
		if (month != null && month.length() != 0)
			model.addAttribute("monthSelected", month.replace("\"", ""));
		else
			model.addAttribute("monthSelected", "");
		
		if (quarter != null && quarter.length() != 0)
			model.addAttribute("quarterSelected", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarterSelected", "");
		//"bb".get
		/*
		 * List<Location> locations =
		 * Context.getLocationService().getAllLocations(false);//
		 * Context.getLocationService().getAllLocations();//ms =
		 * (MdrtbDrugForecastService)
		 * Context.getService(MdrtbDrugForecastService.class); List<Region>
		 * oblasts = Context.getService(MdrtbService.class).getOblasts();
		 * //drugSets = ms.getMdrtbDrugs();
		 * 
		 * 
		 * 
		 * model.addAttribute("locations", locations);
		 * model.addAttribute("oblasts", oblasts);
		 */
		return new ModelAndView("/module/mdrtb/reporting/tb03u", model);
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/tb03u")
	public static String doTB03(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		System.out.println("---POST-----");
		System.out.println("PARAMS:" + oblastId + " " + districtId + " " + facilityId + " " + year + " " + quarter + " "
		        + month);
		
		ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId,
		    facilityId);
		ArrayList<TB03uForm> tb03uList = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		
		/*Cohort patients = new Cohort();// MdrtbUtil.getMdrPatientsTJK(null, null, location, oblast, null, null, null, null,year,quarter,month);
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
		
		Form tb03uForm = Context.getFormService().getForm(MdrtbConstants.TB03U_FORM_ID);
		ArrayList<Form> formList = new ArrayList<Form>();
		formList.add(tb03uForm);
		
		Form tb03Form = Context.getFormService().getForm(MdrtbConstants.TB03_FORM_ID);
		ArrayList<Form> oldformList = new ArrayList<Form>();
		oldformList.add(tb03Form);
		
		
		Set<Integer> idSet = patients.getMemberIds();*/
		ArrayList<TB03uData> patientSet = new ArrayList<TB03uData>();
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		/*ArrayList<Person> patientList = new ArrayList<Person>();
		ArrayList<Concept> conceptQuestionList = new ArrayList<Concept>();
		ArrayList<Concept> conceptAnswerList = new ArrayList<Concept>();*/
		Integer regimenConceptId = null;
		Integer codId = null;
		//List<Obs> obsList = null;
		
		/*Concept reg1New = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_NEW);
		Concept reg1Rtx = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_RETREATMENT);
		*/
		sdf.applyPattern("dd.MM.yyyy");
		SimpleDateFormat rdateSDF = new SimpleDateFormat();
		rdateSDF.applyPattern("dd.MM.yyyy HH:mm:ss");
		for (TB03uForm tf : tb03uList) {
			
			TB03uData tb03uData = new TB03uData();
			
			Patient patient = tf.getPatient();
			if (patient == null || patient.isVoided()) {
				continue;
				
			}
			
			tb03uData.setPatient(patient);
			
			//PATIENT IDENTIFIER
			/* List<PatientIdentifier> idList = patient.getActiveIdentifiers();
			
			for(PatientIdentifier pi : idList) {
				
				if(pi.getIdentifierType().getId()==2 && tb03Data.getIdentifierDOTS()==null) {
					tb03Data.setIdentifierDOTS(pi.getIdentifier());
					if(tb03Data.getIdentifierMDR()!=null)
						break;
				}
				
				if(pi.getIdentifierType().getId()==5 && tb03Data.getIdentifierMDR()==null) {
					tb03Data.setIdentifierMDR(pi.getIdentifier());
					if(tb03Data.getIdentifierDOTS()!=null)
						break;
				}
				
			}*/
			
			String identifier = TB03Util.getRegistrationNumber(tf);
			tb03uData.setIdentifierMDR(identifier);
			
			String identifierDOTS = TB03Util.getRegistrationNumber(tf.getTb03());
			tb03uData.setIdentifierDOTS(identifierDOTS);
			
			Date encDate = tf.getEncounterDatetime();
			
			tb03uData.setTb03uRegistrationDate(sdf.format(encDate));
			tb03uData.setDotsYear(tf.getTb03RegistrationYear());
			
			//    	    //DATE OF TB03U REGISTRATION
			//    	    List<Encounter> tb03uEncList = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, formList, null, null, false);
			//    	    Date encDate = null;
			//    	    if(tb03uEncList.size() > 0 && tb03uEncList.get(0)!=null) {
			//    	    	encDate = tb03uEncList.get(0).getEncounterDatetime();
			//    	    	tb03Data.setTb03uRegistrationDate(sdf.format(encDate));
			//    	    	
			//    	    }
			//    	    
			//    	    else
			//    	    	continue;
			//    	    
			//    	    List<Encounter> tb03EncList = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, formList, null, null, false);
			//    	    Date oldencDate = null;
			//    	    if(tb03uEncList.size() > 0 && tb03uEncList.get(0)!=null) {
			//    	    	oldencDate = tb03EncList.get(0).getEncounterDatetime();
			//    	    	tb03Data.setDotsYear(oldencDate.getYear()+1900);
			//    	    	
			//    	    }
			
			/* //FORMATTED DATE OF BIRTH
			tb03Data.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			//AGE AT TB03U Registration
			Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_MDR_REGISTRATION);
			
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, tb03uEncList, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setAgeAtTB03uRegistration(obsList.get(0).getValueNumeric().intValue());*/
			
			Integer ageAtReg = tf.getAgeAtMDRRegistration();
			if (ageAtReg != null) {
				tb03uData.setAgeAtTB03uRegistration(ageAtReg);
			}
			
			if (patient.getBirthdate() != null)
				tb03uData.setDateOfBirth(sdf.format(patient.getBirthdate()));
			
			/* //SITE OF DISEASE (P/EP)
			q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setSiteOfDisease(obsList.get(0).getValueCoded().getShortNameInLocale(Context.getLocale()).getName());*/
			
			Concept q = tf.getAnatomicalSite();
			
			if (q != null)
				tb03uData.setSiteOfDisease(q.getShortNameInLocale(Context.getLocale()).getName());
			
			/* //SLD Register Number
			q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_2_REG_NUMBER);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setReg2Number(obsList.get(0).getValueText());*/
			
			String reg2Number = tf.getSldRegisterNumber();
			
			tb03uData.setReg2Number(reg2Number);
			
			//REGISTRATION GROUP
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TREATMENT);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null) {
				tb03Data.setRegGroup(obsList.get(0).getValueCoded().getConceptId());
			}*/
			
			q = tf.getRegistrationGroup();
			
			if (q != null) {
				tb03uData.setRegGroup(q.getConceptId());
			}
			
			//MDR STATUS
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_STATUS);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setMdrtbStatus((obsList.get(0).getValueCoded().getName().getName()));*/
			
			q = tf.getMdrStatus();
			
			if (q != null) {
				tb03uData.setMdrtbStatus(q.getName().getName());
			}
			
			//MDR CONF DATE
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_MDR_CONFIRMATION);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setMdrConfDate(sdf.format(obsList.get(0).getValueDatetime()));*/
			
			Date confDate = tf.getConfirmationDate();
			if (confDate != null)
				tb03uData.setMdrConfDate(sdf.format(confDate));
			
			//MDR TREATMENT REGIMEN
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setTreatmentRegimen(obsList.get(0).getValueCoded().getName().getName());*/
			
			q = tf.getPatientCategory();
			
			if (q != null)
				tb03uData.setTreatmentRegimen(q.getName().getName());
			
			//DATE OF MDR TREATMENT START
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setTb03uTreatmentStartDate(sdf.format(obsList.get(0).getValueDatetime()));*/
			
			Date txStartDate = tf.getMdrTreatmentStartDate();
			if (txStartDate != null)
				tb03uData.setTb03uTreatmentStartDate(sdf.format(txStartDate));
			
			//TREATMENT LOCATION
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_LOCATION);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setTreatmentLocation(obsList.get(0).getValueCoded().getName().getName());*/
			
			q = tf.getTxLocation();
			
			if (q != null) {
				tb03uData.setTreatmentLocation(q.getName().getName());
			}
			
			//DST
			/* Dst firstDst = TB03uUtil.getDiagnosticDST(tf);
			
			if(firstDst!=null) {
				if(firstDst.getDateCollected()!=null)
					tb03Data.setDstCollectionDate(sdf.format(firstDst.getDateCollected()));
				if(firstDst.getResultDate()!=null)
					tb03Data.setDstResultDate(sdf.format(firstDst.getResultDate()));
				List<DstResult> resList = firstDst.getResults();
				String drugName = null;
				String result = null;
				for(DstResult res : resList)
				{
					if(res.getDrug()!=null) {
						drugName = res.getDrug().getShortestName(Context.getLocale(), false).toString();
						result = res.getResult().getShortNameInLocale(Context.getLocale()).getName();
						tb03Data.getDstResults().put(drugName,result);
						//System.out.println(drugName + "-" + result + " | " + res.getResult());
						
					}
				}
				
				
				
				System.out.println("-------");
			}*/
			
			Dst firstDst = TB03uUtil.getDiagnosticDST(tf);
			
			if (firstDst != null) {
				if (firstDst.getDateCollected() != null)
					tb03uData.setDstCollectionDate(sdf.format(firstDst.getDateCollected()));
				if (firstDst.getResultDate() != null)
					tb03uData.setDstResultDate(sdf.format(firstDst.getResultDate()));
				List<DstResult> resList = firstDst.getResults();
				String drugName = null;
				String result = null;
				for (DstResult res : resList) {
					if (res.getDrug() != null) {
						drugName = res.getDrug().getShortestName(Context.getLocale(), false).toString();
						result = res.getResult().getShortNameInLocale(Context.getLocale()).getName();
						tb03uData.getDstResults().put(drugName, result);
						//System.out.println(drugName + "-" + result + " | " + res.getResult());
						
					}
				}
				System.out.println("-------");
			}
			
			XpertForm firstXpert = TB03uUtil.getFirstXpertForm(tf);
			if (firstXpert != null) {
				if (firstXpert.getMtbResult() != null)
					tb03uData.setXpertMTBResult(firstXpert.getMtbResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstXpert.getRifResult() != null)
					tb03uData.setXpertRIFResult(firstXpert.getRifResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstXpert.getEncounterDatetime() != null)
					tb03uData.setXpertTestDate(sdf.format(firstXpert.getEncounterDatetime()));
				
				tb03uData.setXpertTestNumber(firstXpert.getSpecimenId());
				
				Location loc = firstXpert.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && loc.getAddress6().length() != 0) {
						tb03uData.setXpertLab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03uData.setXpertLab(loc.getCountyDistrict());
					}
				}
			}
			
			else {
				System.out.println("NULL DIAG XPERT");
			}
			
			HAINForm firstHAIN = TB03uUtil.getFirstHAINForm(tf);
			if (firstHAIN != null) {
				if (firstHAIN.getMtbResult() != null)
					tb03uData.setHainMTBResult(firstHAIN.getMtbResult().getShortNameInLocale(Context.getLocale()).getName());
				if (firstHAIN.getRifResult() != null)
					tb03uData.setHainRIFResult(firstHAIN.getRifResult().getShortNameInLocale(Context.getLocale()).getName());
				if (firstHAIN.getInhResult() != null)
					tb03uData.setHainINHResult(firstHAIN.getInhResult().getShortNameInLocale(Context.getLocale()).getName());
				if (firstHAIN.getEncounterDatetime() != null)
					tb03uData.setHainTestDate(sdf.format(firstHAIN.getEncounterDatetime()));
				
				tb03uData.setHainTestNumber(firstHAIN.getSpecimenId());
				
				Location loc = firstHAIN.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && loc.getAddress6().length() != 0) {
						tb03uData.setHainLab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03uData.setHainLab(loc.getCountyDistrict());
					}
				}
			}
			
			else {
				System.out.println("NULL DIAG HAIN");
			}
			
			HAIN2Form firstHAIN2 = TB03uUtil.getFirstHAIN2Form(tf);
			if (firstHAIN2 != null) {
				if (firstHAIN2.getMtbResult() != null)
					tb03uData.setHain2MTBResult(firstHAIN2.getMtbResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstHAIN2.getInjResult() != null)
					tb03uData.setHain2InjResult(firstHAIN2.getInjResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (firstHAIN2.getFqResult() != null)
					tb03uData.setHain2FqResult(firstHAIN2.getFqResult().getShortNameInLocale(Context.getLocale()).getName());
				if (firstHAIN2.getEncounterDatetime() != null)
					tb03uData.setHain2TestDate(sdf.format(firstHAIN2.getEncounterDatetime()));
				
				tb03uData.setHain2TestNumber(firstHAIN2.getSpecimenId());
				
				Location loc = firstHAIN2.getLocation();
				if (loc != null) {
					if (loc.getAddress6() != null && loc.getAddress6().length() != 0) {
						tb03uData.setHain2Lab(loc.getAddress6());
					}
					
					else if (loc.getCountyDistrict() != null && loc.getCountyDistrict().length() != 0) {
						tb03uData.setHain2Lab(loc.getCountyDistrict());
					}
				}
			}
			
			else {
				System.out.println("NULL DIAG HAIN2");
			}
			
			//DRUG RESISTANCE
			
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setDrugResistance(obsList.get(0).getValueCoded().getShortNameInLocale(Context.getLocale()).getName());*/
			
			q = tf.getResistanceType();
			
			if (q != null) {
				tb03uData.setDrugResistance(q.getName().getName());
			}
			
			//DIAGNOSTIC METHOD
			
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DIAGNOSTIC);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setDiagnosticMethod(obsList.get(0).getValueCoded().getName().getName());*/
			
			q = tf.getBasisForDiagnosis();
			
			if (q != null) {
				tb03uData.setDiagnosticMethod(q.getName().getName());
			}
			
			/* //HIV TEST RESULT
			q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setHivTestResult(obsList.get(0).getValueCoded().getName().getName());*/
			
			q = tf.getHivStatus();
			
			if (q != null) {
				tb03uData.setHivTestResult(q.getName().getName());
			}
			
			/*//DATE OF HIV TEST
			q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_HIV_TEST);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setHivTestDate(sdf.format(obsList.get(0).getValueDatetime()));*/
			
			Date hivTestDate = tf.getHivTestDate();
			if (hivTestDate != null)
				tb03uData.setHivTestDate(sdf.format(hivTestDate));
			
			//DATE OF ART START
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_ART_TREATMENT_START);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setArtStartDate(sdf.format(obsList.get(0).getValueDatetime()));*/
			
			Date artStartDate = tf.getArtStartDate();
			if (artStartDate != null)
				tb03uData.setArtStartDate(sdf.format(artStartDate));
			
			//DATE OF CP START
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_PCT_TREATMENT_START);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setCpStartDate(sdf.format(obsList.get(0).getValueDatetime()));  */
			
			Date pctStartDate = tf.getPctStartDate();
			if (pctStartDate != null)
				tb03uData.setCpStartDate(sdf.format(pctStartDate));
			
			//FOLLOW-UP SMEARS
			//accordingly look for smears
			
			SmearForm followupSmear = TB03uUtil.getFollowupSmearForm(tf, 0);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth0SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth0SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 1);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth1SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth1SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 2);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth2SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth2SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 3);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth3SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth3SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 4);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth4SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth4SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 5);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth5SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth5SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 6);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth6SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth6SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 7);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth7SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth7SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 8);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth8SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth8SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 9);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth9SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth9SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 10);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth10SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth10SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 11);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth11SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth11SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 12);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth12SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth12SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 15);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth15SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth15SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 18);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth18SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth18SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 21);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth21SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth21SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 24);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth24SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth24SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 27);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth27SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth27SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 30);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth30SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth30SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 33);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth33SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth33SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			followupSmear = TB03uUtil.getFollowupSmearForm(tf, 36);
			if (followupSmear != null) {
				if (followupSmear.getSmearResult() != null)
					tb03uData.setMonth36SmearResult(followupSmear.getSmearResult().getShortNameInLocale(Context.getLocale())
					        .getName());
				if (followupSmear.getEncounterDatetime() != null)
					tb03uData.setMonth36SmearResultDate(sdf.format(followupSmear.getEncounterDatetime()));
			}
			
			//follow CULTURES
			CultureForm followupCulture = TB03uUtil.getFollowupCultureForm(tf, 0);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth0CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth0CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 1);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth1CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth1CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 2);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth2CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth2CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 3);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth3CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth3CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 4);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth4CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth4CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 5);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth5CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth5CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 6);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth6CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth6CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 7);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth7CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth7CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 8);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth8CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth8CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 9);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth9CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth9CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 10);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth10CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth10CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 11);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth11CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth11CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 12);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth12CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth12CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 15);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth15CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth15CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 18);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth18CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth18CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 21);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth21CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth21CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 24);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth24CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth24CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 27);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth27CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth27CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 30);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth30CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth30CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 33);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth33CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth33CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			followupCulture = TB03uUtil.getFollowupCultureForm(tf, 36);
			if (followupCulture != null) {
				if (followupCulture.getCultureResult() != null)
					tb03uData.setMonth36CultureResult(followupCulture.getCultureResult()
					        .getShortNameInLocale(Context.getLocale()).getName());
				if (followupCulture.getEncounterDatetime() != null)
					tb03uData.setMonth36CultureResultDate(sdf.format(followupCulture.getEncounterDatetime()));
			}
			
			//TX OUTCOME
			//CHECK CAUSE OF DEATH
			
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
			{	
				codId = obsList.get(0).getValueCoded().getConceptId();
				if(codId.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()))
					tb03Data.setDiedOfTB(true);
				else
					tb03Data.setDiedOfTB(false);
			}
			
			else
				tb03Data.setDiedOfTB(false);*/
			
			q = tf.getCauseOfDeath();//Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH);
			
			if (q != null) {
				codId = q.getConceptId();
				if (codId
				        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()))
					tb03uData.setDiedOfTB(true);
				else
					tb03uData.setDiedOfTB(false);
			}
			
			else
				tb03uData.setDiedOfTB(false);
			
			//RELAPSED
			//    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSED);
			//    	    conceptQuestionList.clear();
			//    	    conceptQuestionList.add(q);
			//    	    
			//    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			//    	    if(obsList.size()>0 && obsList.get(0)!=null)
			//    	    	tb03Data.setRelapsed(obsList.get(0).getValueCoded().getName().getName());
			
			q = tf.getRelapsed();
			
			if (q != null) {
				tb03uData.setRelapsed(q.getName().getName());
			}
			
			//RELAPSED AT MONTH?
			//RELAPSED
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_MONTH);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			    if(obsList.size()>0 && obsList.get(0)!=null)
			    	tb03Data.setRelapseMonth(obsList.get(0).getValueNumeric().intValue());*/
			
			Integer relMonth = tf.getRelapseMonth();
			
			if (relMonth != null) {
				tb03uData.setRelapseMonth(relMonth);
			}
			
			//TX OUTCOME
			
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null) {
				tb03Data.setTb03uTreatmentOutcome(obsList.get(0).getValueCoded().getConceptId());
				 q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_OUTCOME_DATE);
			    conceptQuestionList.clear();
			    conceptQuestionList.add(q);
			    
			    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			    if(obsList.size()>0 && obsList.get(0)!=null) {
			    	tb03Data.setTb03uTreatmentOutcomeDate(sdf.format(obsList.get(0).getValueDatetime()));
			    }
			}*/
			
			q = tf.getTreatmentOutcome();
			if (q != null) {
				tb03uData.setTb03uTreatmentOutcome(q.getConceptId());
				
				Date txOutcomeDate = tf.getTreatmentOutcomeDate();
				if (txOutcomeDate != null) {
					tb03uData.setTb03uTreatmentOutcomeDate(sdf.format(txOutcomeDate));
				}
			}
			
			//NOTES
			
			/* q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES);
			conceptQuestionList.clear();
			conceptQuestionList.add(q);
			
			obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
			if(obsList.size()>0 && obsList.get(0)!=null)
				tb03Data.setNotes(obsList.get(0).getValueText());
			
			patientSet.add(tb03Data);*/
			
			String notes = tf.getClinicianNotes();
			
			if (notes != null)
				tb03uData.setNotes(notes);
			
			regimenConceptId = null;
			codId = null;
			q = null;
			
			patientSet.add(tb03uData);
			
		}
		
		Collections.sort(patientSet);
		Integer num = patientSet.size();
		model.addAttribute("num", num);
		model.addAttribute("patientSet", patientSet);
		
		// TO CHECK WHETHER REPORT IS CLOSED OR NOT
		Integer report_oblast = null;
		Integer report_quarter = null;
		Integer report_month = null;
		/*if(new PDFHelper().isInt(oblast)) { report_oblast = Integer.parseInt(oblast); }
		if(new PDFHelper().isInt(quartert)) { report_quarter = Integer.parseInt(quarter); }
		if(new PDFHelper().isInt(month)) { report_month = Integer.parseInt(month); }*/
		
		boolean reportStatus = Context.getService(MdrtbService.class).readReportStatus(oblastId, districtId, facilityId,
		    year, quarter, month, "TB-03u", "MDRTB");
		System.out.println(reportStatus);
		model.addAttribute("oblast", oblastId);
		model.addAttribute("district", districtId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("year", year);
		if (month != null && month.length() != 0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && quarter.length() != 0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		model.addAttribute("reportDate", rdateSDF.format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		
		return "/module/mdrtb/reporting/tb03uResults";
	}
	
}
