package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.TransferInForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.custom.TB03Util;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.DstImpl;
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

@Controller
//TODO: Will be heavily refactored
public class PatientListContoller {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/patientLists")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getOblasts();
			model.addAttribute("oblasts", oblasts);
		}
		
		else if (district == null) {
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
			/*
			* if oblast is dushanbe, return both districts and facilities
			*/
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilities(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getOblasts();
				districts = Context.getService(MdrtbService.class).getDistricts(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilities(Integer.parseInt(district));
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
		return new ModelAndView("/module/mdrtb/reporting/patientLists", model);
	}
	
	@RequestMapping("/module/mdrtb/reporting/allCasesEnrolled")
	public String allCasesEnrolled(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		model.addAttribute("listName", getMessage("mdrtb.allCasesEnrolled"));
		String report = "";
		
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentSiteIP") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.tbLocalization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		//report += openTD() + getMessage("mdrtb.tb03.tbLocalization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.microscopy") + closeTD();
		report += openTD() + getMessage("mdrtb.xpert") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + closeTD();
		report += openTD() + getMessage("mdrtb.culture") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugResistance") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.resistantTo") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.sensitiveTo") + closeTD();
		report += openTD() + getMessage("mdrtb.hivStatus") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.reregisrationNumber") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		report += openTR();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.inhShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.rifShort") + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.injectablesShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.quinShort") + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		
		report += closeTR();
		
		int i = 0;
		Person p = null;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report += openTR();
			report += openTD() + i + closeTD();
			report += openTD() + getRegistrationNumber(tf) + closeTD();
			report += openTD() + sdf.format(tf.getEncounterDatetime()) + closeTD();
			if (tf.getTreatmentStartDate() != null)
				report += openTD() + sdf.format(tf.getTreatmentStartDate()) + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			if (tf.getTreatmentSiteIP() != null) {
				report += openTD() + tf.getTreatmentSiteIP().getName().getName() + closeTD();
			}
			
			else
				report += openTD() + "" + closeTD();
			
			report += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
			report += openTD() + getGender(p) + closeTD();
			report += openTD() + sdf.format(p.getBirthdate()) + closeTD();
			report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
			
			if (tf.getAnatomicalSite() != null) {
				Integer asId = tf.getAnatomicalSite().getConceptId();
				if (asId.intValue() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)
				        .getConceptId().intValue()) {
					report += openTD() + getMessage("mdrtb.lists.pulmonaryShort") + closeTD();
				}
				
				else if (asId.intValue() == Context.getService(MdrtbService.class)
				        .getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getConceptId().intValue()) {
					report += openTD() + getMessage("mdrtb.lists.extrapulmonaryShort") + closeTD();
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
				//report += openTD() + tf.getAnatomicalSite().getName().getName().charAt(0) + closeTD();
			}
			
			else
				report += openTD() + "" + closeTD();
			
			if (tf.getRegistrationGroup() != null)
				report += openTD() + tf.getRegistrationGroup().getName().getName() + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			//SMEAR
			List<SmearForm> smears = tf.getSmears();
			if (smears != null && smears.size() != 0) {
				Collections.sort(smears);
				
				SmearForm ds = smears.get(0);
				
				if (ds.getSmearResult() != null) {
					
					if (ds.getSmearResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE).getConceptId()
					        .intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == ds.getSmearResult().getConceptId().intValue()) {
								report += openTD() + getMessage("mdrtb.positiveShort") + closeTD();
								break;
							}
							
						}
					}
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += openTD() + "" + closeTD();
				}
				
				else {
					if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId()
					                .intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == ms.getConcept(MdrtbConcepts.DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += openTD() + xr + closeTD();
							}
							
							else if (resId == ms.getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += openTD() + xr + closeTD();
							}
							
							else {
								report += openTD() + xr + closeTD();
							}
						}
						
						else {
							report += openTD() + xr + closeTD();
						}
					}
					
					else if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId()
					        .intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else {
						report += openTD() + "" + closeTD();
					}
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 1	
			List<HAINForm> hains = tf.getHains();
			if (hains != null && hains.size() != 0) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (rh != null) {
					report += openTD() + rh.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
			} else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && hain2s.size() != 0) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (fq != null) {
					report += openTD() + fq.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
			} else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.CULTURE_GROWTH)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.lists.growth") + closeTD();
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += openTD() + getMessage("mdrtb.positiveShort") + closeTD();
								break;
							}
							
						}
					}
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report += openTD() + tf.getResistanceType().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			report += openTD() + getResistantDrugs(tf) + closeTD();
			report += openTD() + getSensitiveDrugs(tf) + closeTD();
			
			if (tf.getHivStatus() != null) {
				report += openTD() + tf.getHivStatus().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcome() != null) {
				report += openTD() + tf.getTreatmentOutcome().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcomeDate() != null) {
				report += openTD() + sdf.format(tf.getTreatmentOutcomeDate()) + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//OTHER NUMBER
			report += openTD() + getReRegistrationNumber(tf) + closeTD();
			
			report += openTD() + getPatientLink(tf) + closeTD();
			report += closeTR();
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByRegistrationGroup")
	public String dotsCasesByRegistrationGroup(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsCasesByRegistrationGroup"));
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.PATIENT_GROUP);
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		Collections.sort(tb03s);
		
		//NEW CASES 
		Concept newConcept = ms.getConcept(MdrtbConcepts.NEW);
		report += "<h4>" + getMessage("mdrtb.lists.new") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Relapse
		
		Concept relapse1Concept = ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.relapses") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//AfterDefault
		Concept default1Concept = ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		
		report += "<h4>" + getMessage("mdrtb.tb03.ltfu") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//AfterFailure
		Concept failure1Concept = ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_1);
		Concept failure2Concept = ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_2);
		
		report += "<h4>" + getMessage("mdrtb.tb03.failure") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		Concept transferInConcept = ms.getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report += "<h4>" + getMessage("mdrtb.lists.transferIn") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.transferFrom") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.dateOfTransfer") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		temp = null;
		i = 0;
		p = null;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getTransferFrom(tf) + closeTD();
				report += openTD() + getTransferFromDate(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		//OTHER CASES 
		Concept otherConcept = ms.getConcept(MdrtbConcepts.OTHER);
		report += "<h4>" + getMessage("mdrtb.tb03.other") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == otherConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	//////////
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByAnatomicalSite")
	public String dotsCasesByAnatomicalSite(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsCasesByAnatomicalSite"));
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES 
		Concept pulmonaryConcept = ms.getConcept(MdrtbConcepts.PULMONARY_TB);
		report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == pulmonaryConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getRegistrationGroup(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Relapse
		
		Concept epConcept = ms.getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB);
		
		report += "<h4>" + getMessage("mdrtb.extrapulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == epConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getRegistrationGroup(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	///////////
	
	@RequestMapping("/module/mdrtb/reporting/byDrugResistance")
	public String byDrugResistance(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.byDrugResistance"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(tb03s);
		/*
		 * Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter,
		 * month);
		 * 
		 * Date startDate = (Date)(dateMap.get("startDate")); Date endDate =
		 * (Date)(dateMap.get("endDate"));
		 */
		Concept q = ms.getConcept(MdrtbConcepts.MONO);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// RIF
		q = ms.getConcept(MdrtbConcepts.RR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// POLY
		q = ms.getConcept(MdrtbConcepts.PDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// MDR
		q = ms.getConcept(MdrtbConcepts.MDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// PRE_XDR_TB
		q = ms.getConcept(MdrtbConcepts.PRE_XDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// XDR_TB
		q = ms.getConcept(MdrtbConcepts.XDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// TDR
		q = ms.getConcept(MdrtbConcepts.TDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// UNKNOWN
		q = ms.getConcept(MdrtbConcepts.UNKNOWN);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + getResistantDrugs(tf) + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// NO
		q = ms.getConcept(MdrtbConcepts.NO);
		report += "<h4>" + getMessage("mdrtb.sensitive") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.localization") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugNames") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
				report += openTD() + getSiteOfDisease(tf) + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	//////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/dotsPulmonaryCasesByRegisrationGroupAndBacStatus")
	public String dotsPulmonaryCasesByRegisrationGroupAndBacStatus(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsPulmonaryCasesByRegisrationGroupAndBacStatus"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.PATIENT_GROUP);
		Concept siteConcept = ms.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		Concept pulConcept = ms.getConcept(MdrtbConcepts.PULMONARY_TB);
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES + Positive
		Concept newConcept = ms.getConcept(MdrtbConcepts.NEW);
		report += "<h4>" + getMessage("mdrtb.lists.newPulmonaryBacPositive") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Obs temp2 = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						i++;
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//NEW CASES + Negative
		
		report += "<h4>" + getMessage("mdrtb.lists.newPulmonaryBacNegative") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		temp2 = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						i++;
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		//Relapse + positive
		Concept relapse1Concept = ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = ms.getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.relapsePulmonaryBacPositive") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		i = 0;
		p = null;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		//Relapse + negative
		report += "<h4>" + getMessage("mdrtb.lists.relapsePulmonaryBacNegative") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Retreament - Negative
		Concept default1Concept = ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = ms.getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		Concept failure1Concept = ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_1);
		Concept failure2Concept = ms.getConcept(MdrtbConcepts.AFTER_FAILURE_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.retreatmentPulmonaryBacPositive") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getRegistrationGroup(tf) + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		report += "<h4>" + getMessage("mdrtb.lists.retreatmentPulmonaryBacNegative") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getRegistrationGroup(tf) + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		Concept transferInConcept = ms.getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report += "<h4>" + getMessage("mdrtb.lists.transferInPulmonaryBacPositive") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
						
					}
				}
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		
		report += "<h4>" + getMessage("mdrtb.lists.transferInPulmonaryBacNegative") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, true);
						report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
						
					}
				}
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	//////////
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatientsNoTreatment")
	public String mdrXdrPatientsNoTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrXdrPatientsNoTreatment"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept treatmentStartDate = ms.getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE);
		
		ArrayList<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES 
		Concept mdr = ms.getConcept(MdrtbConcepts.MDR_TB);
		report += "<h4>" + getMessage("mdrtb.mdrtb") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Obs temp2 = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, false);
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		Concept xdr = ms.getConcept(MdrtbConcepts.XDR_TB);
		
		report += "<h4>" + getMessage("mdrtb.xdrtb") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		temp = null;
		
		p = null;
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, false);
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	//////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/mdrSuccessfulTreatmentOutcome")
	public String mdrSuccessfulTreatmentOutcome(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrSuccessfulTreatmentOutcome"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
		Concept curedConcept = ms.getConcept(MdrtbConcepts.CURED);
		Concept txCompleted = ms.getConcept(MdrtbConcepts.TREATMENT_COMPLETED);
		
		ArrayList<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES 
		
		report += "<h4>" + getMessage("mdrtb.mdrSuccessfulTreatment") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == curedConcept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == txCompleted.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, false);
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	//////////
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatients")
	public String mdrXdrPatients(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrXdrPatients"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		
		ArrayList<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date) (dateMap.get("startDate"));
		Date endDate = (Date) (dateMap.get("endDate"));
		//NEW CASES 
		Concept mdr = ms.getConcept(MdrtbConcepts.MDR_TB);
		report += "<h4>" + getMessage("mdrtb.mdrtb") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, false);
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		//EP
		Concept xdr = ms.getConcept(MdrtbConcepts.XDR_TB);
		
		report += "<h4>" + getMessage("mdrtb.xdrtb") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		temp = null;
		
		p = null;
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tf) + closeTD();
				report += renderPerson(p, false);
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	//
	//////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/womenOfChildbearingAge")
	public String womenOfChildbearingAge(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.womenOfChildbearingAge"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.AGE_AT_FORM89_REGISTRATION);
		
		/*ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);*/
		
		ArrayList<TB03Form> forms = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(forms);
		
		/*	System.out.println("XXXXXXX");
			for(int j=0; j<forms.size();j++) {
				//System.out.println(forms.get(j).getRegistrationNumber());
				forms.
			}
			System.out.println("XXXXXXX");*/
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.womenOfChildbearingAge") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		//Obs temp = null;
		Person p = null;
		int i = 0;
		
		for (TB03Form tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("F")) {
				
				if (tf != null) {
					Integer age = tf.getAgeAtTB03Registration();
					
					//temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
					if (age != null && age.intValue() >= 15 && age.intValue() <= 49) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += openTR();
						report += openTD() + i + closeTD();
						report += openTD() + getRegistrationNumber(tf) + closeTD();
						report += renderPerson(p, false);
						report += openTD() + age + closeTD();
						if (tf.getRegistrationGroup() != null)
							report += openTD() + tf.getRegistrationGroup().getName().getName() + closeTD();
						else
							report += openTD() + "" + closeTD();
						report += openTD() + getPatientLink(tf) + closeTD();
						report += closeTR();
					}
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	//////////
	@RequestMapping("/module/mdrtb/reporting/menOfConscriptAge")
	public String menOfConscriptAge(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.menOfConscriptAge"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.AGE_AT_FORM89_REGISTRATION);
		
		/*ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);*/
		ArrayList<TB03Form> forms = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(forms);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.menOfConscriptAge") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.caseDefinition") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		//Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("M")) {
				
				Integer age = tf.getAgeAtTB03Registration();
				
				if (age != null && age.intValue() >= 18 && age.intValue() <= 27) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tf) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					if (tf.getRegistrationGroup() != null)
						report += openTD() + tf.getRegistrationGroup().getName().getName() + closeTD();
					else
						report += openTD() + "" + closeTD();
					
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withConcomitantDisease")
	public String withConcomitantDisease(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withConcomitantDisease"));
		
		String report = "";
		
		ArrayList<Form89> forms = new ArrayList<Form89>();//Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03List);
		Concept regGroup = null;
		Form89 f89 = null;
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			f89.setTB03(tb03);
			forms.add(f89);
		}
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.DIABETES);
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withDiabetes") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.CANCER);
		report += "<h4>" + getMessage("mdrtb.withCancer") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.CNSDL);
		report += "<h4>" + getMessage("mdrtb.withCOPD") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withHypertension") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.ULCER);
		report += "<h4>" + getMessage("mdrtb.withUlcer") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.MENTAL_DISORDER);
		report += "<h4>" + getMessage("mdrtb.withMentalDisorder") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.ICD20);
		report += "<h4>" + getMessage("mdrtb.withHIV") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				Concept c = tf.getIbc20();
				
				//temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (c != null && (c.getConceptId().intValue() == yes.getConceptId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		report += "<h4>" + getMessage("mdrtb.withHepatitis") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withKidneyDisease") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = ms.getConcept(MdrtbConcepts.OTHER_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withOtherDisease") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : forms) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && temp.getValueCoded() != null
				        && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, true);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withCancer")
	public String withCancer(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withCancer"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.CANCER);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withCancer") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/detectedFromContact")
	public String detectedFromContact(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.detectedFromContact"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		Collections.sort(forms);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		Concept fromContact = ms.getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		
		report += "<h4>" + getMessage("mdrtb.detectedFromContact") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == fromContact.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withCOPD")
	public String withCOPD(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withCOPD"));
		
		String report = "";
		Concept groupConcept = ms.getConcept(MdrtbConcepts.CNSDL);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withCOPD") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withHypertension")
	public String withHypertension(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHypertension"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withHypertension") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withUlcer")
	public String withUlcer(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withUlcer"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.ULCER);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withUlcer") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withMentalDisorder")
	public String withMentalDisorder(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withMentalDisorder"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.MENTAL_DISORDER);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withMentalDisorder") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withHIV")
	public String withHIV(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHIV"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.ICD20);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withHIV") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withHepatitis")
	public String withHepatitis(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHepatitis"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withHepatitis") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withKidneyDisease")
	public String withKidneyDisease(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withKidneyDisease"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withKidneyDisease") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/withOtherDisease")
	public String withOtherDisease(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.withOtherDisease"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.OTHER_DISEASE);
		
		ArrayList<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept yes = ms.getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withOtherDisease") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += openTR();
					report += openTD() + i + closeTD();
					report += openTD() + getRegistrationNumber(tb03) + closeTD();
					report += renderPerson(p, false);
					report += openTD() + age + closeTD();
					report += openTD() + getPatientLink(tf) + closeTD();
					report += closeTR();
				}
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/bySocProfStatus")
	public String bySocProfStatus(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.bySocProfStatus"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.PROFESSION);
		
		ArrayList<Form89> workerList = new ArrayList<Form89>();
		ArrayList<Form89> govtList = new ArrayList<Form89>();
		ArrayList<Form89> studentList = new ArrayList<Form89>();
		ArrayList<Form89> disabledList = new ArrayList<Form89>();
		ArrayList<Form89> unemployedList = new ArrayList<Form89>();
		ArrayList<Form89> phcList = new ArrayList<Form89>();
		ArrayList<Form89> militaryList = new ArrayList<Form89>();
		ArrayList<Form89> schoolList = new ArrayList<Form89>();
		ArrayList<Form89> tbWorkerList = new ArrayList<Form89>();
		ArrayList<Form89> privateList = new ArrayList<Form89>();
		ArrayList<Form89> housewifeList = new ArrayList<Form89>();
		ArrayList<Form89> preschoolList = new ArrayList<Form89>();
		ArrayList<Form89> pensionerList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		Concept status = null;
		
		//category
		Concept workerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WORKER);
		int workerId = workerConcept.getConceptId().intValue();
		Concept govtConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GOVT_SERVANT);
		int govtId = govtConcept.getConceptId().intValue();
		Concept studentConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STUDENT);
		int studentId = studentConcept.getConceptId().intValue();
		Concept disabledConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISABLED);
		int disabledId = disabledConcept.getConceptId().intValue();
		Concept unemployedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNEMPLOYED);
		int unemployedId = unemployedConcept.getConceptId().intValue();
		Concept phcConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_WORKER);
		int phcId = phcConcept.getConceptId().intValue();
		Concept militaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		int militaryId = militaryConcept.getConceptId().intValue();
		Concept schoolConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCHOOLCHILD);
		int schoolId = schoolConcept.getConceptId().intValue();
		Concept tbWorkerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		int tbWorkerId = tbWorkerConcept.getConceptId().intValue();
		Concept privateConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR);
		int privateId = privateConcept.getConceptId().intValue();
		Concept housewifeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOUSEWIFE);
		int housewifeId = housewifeConcept.getConceptId().intValue();
		Concept preschoolConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESCHOOL_CHILD);
		int preschoolId = preschoolConcept.getConceptId().intValue();
		Concept pensionerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PENSIONER);
		int pensionerId = pensionerConcept.getConceptId().intValue();
		
		int statusId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			status = f89.getProfession();
			
			if (status == null)
				continue;
			
			f89.setTB03(tb03);
			
			statusId = status.getConceptId().intValue();
			
			if (statusId == workerId) {
				workerList.add(f89);
			}
			
			else if (statusId == govtId) {
				govtList.add(f89);
			}
			
			else if (statusId == studentId) {
				studentList.add(f89);
			}
			
			else if (statusId == disabledId) {
				disabledList.add(f89);
			}
			
			else if (statusId == unemployedId) {
				unemployedList.add(f89);
			}
			
			else if (statusId == phcId) {
				phcList.add(f89);
			}
			
			else if (statusId == militaryId) {
				militaryList.add(f89);
			}
			
			else if (statusId == schoolId) {
				schoolList.add(f89);
			}
			
			else if (statusId == tbWorkerId) {
				tbWorkerList.add(f89);
			}
			
			else if (statusId == privateId) {
				privateList.add(f89);
			}
			
			else if (statusId == housewifeId) {
				housewifeList.add(f89);
			}
			
			else if (statusId == preschoolId) {
				preschoolList.add(f89);
			}
			
			else if (statusId == pensionerId) {
				pensionerList.add(f89);
			}
			
		}
		
		//WORKER
		Concept q = ms.getConcept(MdrtbConcepts.WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Person p = null;
		int i = 0;
		for (Form89 tf : workerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + workerList.size();
		report += "<br/>";
		
		//GOVT SERVANT
		q = ms.getConcept(MdrtbConcepts.GOVT_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : govtList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + govtList.size();
		report += "<br/>";
		
		//STUDENT
		q = ms.getConcept(MdrtbConcepts.STUDENT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : studentList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + studentList.size();
		report += "<br/>";
		
		//DISABLED
		q = ms.getConcept(MdrtbConcepts.DISABLED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : disabledList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + disabledList.size();
		report += "<br/>";
		
		//UNEMPLOYED
		q = ms.getConcept(MdrtbConcepts.UNEMPLOYED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : unemployedList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + unemployedList.size();
		report += "<br/>";
		
		//PHC WORKER
		q = ms.getConcept(MdrtbConcepts.PHC_WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : phcList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + phcList.size();
		report += "<br/>";
		
		//MILITARY SERVANT
		q = ms.getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : militaryList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + militaryList.size();
		report += "<br/>";
		
		//SCHOOLCHILD
		q = ms.getConcept(MdrtbConcepts.SCHOOLCHILD);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : schoolList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + schoolList.size();
		report += "<br/>";
		
		//TB SERVICES WORKER
		q = ms.getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : tbWorkerList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + tbWorkerList.size();
		report += "<br/>";
		
		//PRIVATE SECTOR WORKER
		q = ms.getConcept(MdrtbConcepts.PRIVATE_SECTOR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + privateList.size();
		report += "<br/>";
		
		//HOUSEWIFE
		q = ms.getConcept(MdrtbConcepts.HOUSEWIFE);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : housewifeList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + housewifeList.size();
		report += "<br/>";
		
		//PRE-SCHOOL CHILD
		q = ms.getConcept(MdrtbConcepts.PRESCHOOL_CHILD);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : preschoolList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + preschoolList.size();
		report += "<br/>";
		
		//PENSIONER
		q = ms.getConcept(MdrtbConcepts.PENSIONER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : pensionerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + pensionerList.size();
		report += "<br/>";
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	////////////////////////////////////
	@RequestMapping("/module/mdrtb/reporting/byPopCategory")
	public String byPopulationCategory(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPopCategory"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.POPULATION_CATEGORY);
		
		ArrayList<Form89> thisList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		ArrayList<Form89> foreignerList = new ArrayList<Form89>();
		ArrayList<Form89> welfareList = new ArrayList<Form89>();
		ArrayList<Form89> homelessList = new ArrayList<Form89>();
		ArrayList<Form89> prisonerList = new ArrayList<Form89>();
		ArrayList<Form89> investigationList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		Concept category = null;
		
		//CATEGORY
		Concept thisConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		int thisId = thisConcept.getConceptId().intValue();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		int otherId = otherConcept.getConceptId().intValue();
		Concept foreignerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOREIGNER);
		int foreignerId = foreignerConcept.getConceptId().intValue();
		Concept welfareConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
		int welfareId = welfareConcept.getConceptId().intValue();
		Concept homelessConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOMELESS);
		int homelessId = homelessConcept.getConceptId().intValue();
		Concept prisonerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONVICTED);
		int prisonerId = prisonerConcept.getConceptId().intValue();
		Concept investigationConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ON_REMAND);
		int investigationId = investigationConcept.getConceptId().intValue();
		
		int catId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			category = f89.getPopulationCategory();
			
			if (category == null)
				continue;
			
			f89.setTB03(tb03);
			
			catId = category.getConceptId().intValue();
			
			if (catId == thisId) {
				thisList.add(f89);
			}
			
			else if (catId == otherId) {
				otherList.add(f89);
			}
			
			else if (catId == foreignerId) {
				foreignerList.add(f89);
			}
			
			else if (catId == welfareId) {
				welfareList.add(f89);
			}
			
			else if (catId == homelessId) {
				homelessList.add(f89);
			}
			
			else if (catId == prisonerId) {
				prisonerList.add(f89);
			}
			
			else if (catId == investigationId) {
				investigationList.add(f89);
			}
			
		}
		
		//RESIDENT_OF_TERRITORY
		Concept q = ms.getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : thisList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + thisList.size();
		report += "<br/>";
		
		//RESIDENT_OTHER_TERRITORY
		q = ms.getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		report += "<br/>";
		
		//FOREIGNER
		q = ms.getConcept(MdrtbConcepts.FOREIGNER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.form89.countryOfOrigin") + closeTD();
		
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : foreignerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + tf.getCountryOfOrigin() + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + foreignerList.size();
		report += "<br/>";
		
		//RESIDENT_SOCIAL_SECURITY_FACILITY
		q = ms.getConcept(MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : welfareList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + welfareList.size();
		report += "<br/>";
		
		//HOMELESS
		q = ms.getConcept(MdrtbConcepts.HOMELESS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : homelessList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + homelessList.size();
		report += "<br/>";
		
		//CONVICTED
		q = ms.getConcept(MdrtbConcepts.CONVICTED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : prisonerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + prisonerList.size();
		report += "<br/>";
		
		//ON_REMAND
		q = ms.getConcept(MdrtbConcepts.ON_REMAND);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : investigationList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + investigationList.size();
		/*report += "<br/>";
		
		
		report += closeTable();*/
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	////////////////////////////////////
	@RequestMapping("/module/mdrtb/reporting/byDwelling")
	public String byDwelling(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byDwelling"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.LOCATION_TYPE);
		
		/*ArrayList<Form89> tb03s = Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);
		Collections.sort(tb03s);*/
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		ArrayList<Form89> cityList = new ArrayList<Form89>();
		ArrayList<Form89> villageList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03List);
		
		Concept type = null;
		
		//PLACE
		Concept cityConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY);
		int cityId = cityConcept.getConceptId().intValue();
		Concept villageConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		int villageId = villageConcept.getConceptId().intValue();
		
		int typeId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			type = f89.getLocationType();
			
			if (type == null)
				continue;
			
			f89.setTB03(tb03);
			
			typeId = type.getConceptId().intValue();
			
			if (typeId == cityId) {
				cityList.add(f89);
			}
			
			else if (typeId == villageId) {
				villageList.add(f89);
			}
			
		}
		
		//RESIDENT_OF_TERRITORY
		Concept q = ms.getConcept(MdrtbConcepts.CITY);
		report += "<h4>" + getMessage("mdrtb.lists.city") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Person p = null;
		int i = 0;
		for (Form89 tf : cityList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cityList.size();
		report += "<br/>";
		
		//RESIDENT_OTHER_TERRITORY
		q = ms.getConcept(MdrtbConcepts.VILLAGE);
		report += "<h4>" + getMessage("mdrtb.lists.village") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : villageList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + villageList.size();
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	//////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////
	@RequestMapping("/module/mdrtb/reporting/byPlaceOfDetection")
	public String byPlaceOfDetection(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPlaceOfDetection"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.PLACE_OF_DETECTION);
		
		/*ArrayList<Form89> tb03s = Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);
		Collections.sort(tb03s);*/
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		ArrayList<Form89> tbList = new ArrayList<Form89>();
		ArrayList<Form89> privateList = new ArrayList<Form89>();
		ArrayList<Form89> phcList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03List);
		
		Concept circSite = null;
		
		//PLACE
		Concept tbConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_FACILITY);
		int tbId = tbConcept.getConceptId().intValue();
		Concept privateConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		int privateId = privateConcept.getConceptId().intValue();
		Concept phcConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_FACILITY);
		int phcId = phcConcept.getConceptId().intValue();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		int otherId = otherConcept.getConceptId().intValue();
		
		int circId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			circSite = f89.getPlaceOfDetection();
			
			if (circSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			circId = circSite.getConceptId().intValue();
			
			if (circId == tbId) {
				tbList.add(f89);
			}
			
			else if (circId == privateId) {
				privateList.add(f89);
			}
			
			else if (circId == phcId) {
				phcList.add(f89);
			}
			
			else if (circId == otherId) {
				otherList.add(f89);
			}
			
		}
		
		//TB FACILITY
		Concept q = ms.getConcept(MdrtbConcepts.TB_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : tbList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + tbList.size();
		report += "<br/>";
		
		//PHC
		q = ms.getConcept(MdrtbConcepts.PHC_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : phcList) {
			
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + phcList.size();
		report += "<br/>";
		
		//Private Sector
		q = ms.getConcept(MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + privateList.size();
		report += "<br/>";
		
		//OTHER MED FAC
		q = ms.getConcept(MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		temp = null;
		p = null;
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	////////////////////////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/byCircumstancesOfDetection")
	public String byCircumstancesOfDetection(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byCircumstancesOfDetection"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		
		//ArrayList<Form89> tb03s = Context.getService(MdrtbService.class).getForm89FormsFilled(locList,year,quarter,month);
		ArrayList<Form89> selfRefList = new ArrayList<Form89>();
		ArrayList<Form89> baselineExamList = new ArrayList<Form89>();
		ArrayList<Form89> postmortemList = new ArrayList<Form89>();
		ArrayList<Form89> contactList = new ArrayList<Form89>();
		ArrayList<Form89> migrantList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03List);
		
		Concept circSite = null;
		
		//CIRCUMSTANCES
		Concept selfRefConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SELF_REFERRAL);
		int selfRefId = selfRefConcept.getConceptId().intValue();
		Concept baselineExamConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BASELINE_EXAM);
		int baselineExamId = baselineExamConcept.getConceptId().intValue();
		Concept postmortemConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
		int postMortemId = postmortemConcept.getConceptId().intValue();
		Concept contactConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		int contactId = contactConcept.getConceptId().intValue();
		Concept migrantConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MIGRANT);
		int migrantId = migrantConcept.getConceptId().intValue();
		
		int circId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		/*Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));*/
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			circSite = f89.getCircumstancesOfDetection();
			
			if (circSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			circId = circSite.getConceptId().intValue();
			
			if (circId == selfRefId) {
				selfRefList.add(f89);
			}
			
			else if (circId == baselineExamId) {
				baselineExamList.add(f89);
			}
			
			else if (circId == postMortemId) {
				postmortemList.add(f89);
			}
			
			else if (circId == contactId) {
				contactList.add(f89);
			}
			
			else if (circId == migrantId) {
				migrantList.add(f89);
			}
			
		}
		
		//SELF_REFERRAL
		Concept q = ms.getConcept(MdrtbConcepts.SELF_REFERRAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Person p = null;
		int i = 0;
		for (Form89 tf : selfRefList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + selfRefList.size();
		report += "<br/>";
		
		//BASELINE_EXAM
		q = ms.getConcept(MdrtbConcepts.BASELINE_EXAM);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : baselineExamList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + baselineExamList.size();
		report += "<br/>";
		
		//POSTMORTERM_IDENTIFICATION
		q = ms.getConcept(MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : postmortemList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + postmortemList.size();
		//CONTACT
		q = ms.getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : contactList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + contactList.size();
		report += "<br/>";
		
		//MIGRANT
		q = ms.getConcept(MdrtbConcepts.MIGRANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.form89.cityOfOrigin") + closeTD();
		report += openTD() + getMessage("mdrtb.form89.dateOfReturn") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : migrantList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + tf.getCityOfOrigin() + closeTD();
				report += openTD() + renderDate(tf.getDateOfReturn()) + closeTD();
				
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + migrantList.size();
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	////////////////////////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/byMethodOfDetection")
	
	public String byMethodOfDetection(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byMethodOfDetection"));
		
		String report = "";
		
		Concept groupConcept = ms.getConcept(MdrtbConcepts.METHOD_OF_DETECTION);
		
		ArrayList<Form89> fluorographyList = new ArrayList<Form89>();
		ArrayList<Form89> genexpertList = new ArrayList<Form89>();
		ArrayList<Form89> microscopyList = new ArrayList<Form89>();
		ArrayList<Form89> tuberculinList = new ArrayList<Form89>();
		ArrayList<Form89> hainList = new ArrayList<Form89>();
		ArrayList<Form89> cultureList = new ArrayList<Form89>();
		ArrayList<Form89> histologyList = new ArrayList<Form89>();
		ArrayList<Form89> cxrList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		Concept method = null;
		
		//METHOD
		Concept fluorographyConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		int fluorographyId = fluorographyConcept.getConceptId().intValue();
		Concept genexpertConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENEXPERT);
		int genexpertId = genexpertConcept.getConceptId().intValue();
		Concept tuberculinConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULIN_TEST);
		int tuberculinId = tuberculinConcept.getConceptId().intValue();
		Concept hainConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_TEST);
		int hainId = hainConcept.getConceptId().intValue();
		Concept cultureConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_TEST);
		int cultureId = cultureConcept.getConceptId().intValue();
		Concept histologyConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HISTOLOGY);
		int histologyId = histologyConcept.getConceptId().intValue();
		Concept cxrConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CXR_RESULT);
		int cxrId = cxrConcept.getConceptId().intValue();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		int otherId = otherConcept.getConceptId().intValue();
		
		int methodId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			method = f89.getMethodOfDetection();
			
			if (method == null)
				continue;
			
			f89.setTB03(tb03);
			
			methodId = method.getConceptId().intValue();
			
			if (methodId == fluorographyId) {
				fluorographyList.add(f89);
			}
			
			else if (methodId == genexpertId) {
				genexpertList.add(f89);
			}
			
			else if (methodId == tuberculinId) {
				tuberculinList.add(f89);
			}
			
			else if (methodId == hainId) {
				hainList.add(f89);
			}
			
			else if (methodId == cultureId) {
				cultureList.add(f89);
			}
			
			else if (methodId == histologyId) {
				histologyList.add(f89);
			}
			
			else if (methodId == cxrId) {
				cxrList.add(f89);
			}
			
			else if (methodId == otherId) {
				otherList.add(f89);
			}
			
		}
		
		//FLUOROGRAPHY
		Concept q = ms.getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		Person p = null;
		int i = 0;
		for (Form89 tf : fluorographyList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + fluorographyList.size();
		report += "<br/>";
		
		//GENEXPERT
		q = ms.getConcept(MdrtbConcepts.GENEXPERT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : genexpertList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + genexpertList.size();
		report += "<br/>";
		
		//FLURORESCENT_MICROSCOPY
		q = ms.getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : microscopyList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + microscopyList.size();
		report += "<br/>";
		
		//TUBERCULIN_TEST
		q = ms.getConcept(MdrtbConcepts.TUBERCULIN_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : tuberculinList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + tuberculinList.size();
		report += "<br/>";
		
		//HAIN_TEST
		q = ms.getConcept(MdrtbConcepts.HAIN_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : hainList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + hainList.size();
		report += "<br/>";
		
		//CULTURE_TEST
		q = ms.getConcept(MdrtbConcepts.CULTURE_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : cultureList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cultureList.size();
		report += "<br/>";
		
		//HISTOLOGY
		q = ms.getConcept(MdrtbConcepts.HISTOLOGY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : histologyList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + histologyList.size();
		report += "<br/>";
		
		//CXR_RESULT
		q = ms.getConcept(MdrtbConcepts.CXR_RESULT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		
		for (Form89 tf : cxrList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cxrList.size();
		report += "<br/>";
		
		//OTHER
		q = ms.getConcept(MdrtbConcepts.OTHER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		
		for (Form89 tf : otherList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
			}
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/byPulmonaryLocation")
	public String byPulmonaryLocation(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		/*ArrayList<Location> locList = Context.getService(MdrtbService.class)
				.getLocationList(oblastId, districtId, facilityId);*/
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPulmonaryLocation"));
		
		String report = "";
		
		ArrayList<Form89> focalList = new ArrayList<Form89>();
		ArrayList<Form89> infilList = new ArrayList<Form89>();
		ArrayList<Form89> disList = new ArrayList<Form89>();
		ArrayList<Form89> cavList = new ArrayList<Form89>();
		ArrayList<Form89> fibCavList = new ArrayList<Form89>();
		ArrayList<Form89> cirrList = new ArrayList<Form89>();
		ArrayList<Form89> priCompList = new ArrayList<Form89>();
		ArrayList<Form89> miliaryList = new ArrayList<Form89>();
		ArrayList<Form89> tuberculomaList = new ArrayList<Form89>();
		ArrayList<Form89> bronchiList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		Concept pulSite = null;
		
		//PULMONARY
		Concept fibroCavConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		int fibroCavId = fibroCavConcept.getConceptId().intValue();
		Concept miliaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		int miliaryId = miliaryConcept.getConceptId().intValue();
		Concept focalConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOCAL);
		int focalId = focalConcept.getConceptId().intValue();
		Concept infiltrativeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INFILTRATIVE);
		int infiltrativeId = infiltrativeConcept.getConceptId().intValue();
		Concept disseminatedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISSEMINATED);
		int disseminatedId = disseminatedConcept.getConceptId().intValue();
		Concept cavernousConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAVERNOUS);
		int cavernousId = cavernousConcept.getConceptId().intValue();
		Concept cirrhoticConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRRHOTIC);
		int cirrhoticId = cirrhoticConcept.getConceptId().intValue();
		Concept primaryComplexConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		int primaryComplexId = primaryComplexConcept.getConceptId().intValue();
		Concept tuberculomaConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOMA);
		int tuberculomaId = tuberculomaConcept.getConceptId().intValue();
		Concept bronchiConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BRONCHUS);
		int bronchiId = bronchiConcept.getConceptId().intValue();
		
		int pulId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			pulSite = f89.getPulSite();
			
			if (pulSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			pulId = pulSite.getConceptId().intValue();
			
			if (pulId == focalId) {
				focalList.add(f89);
			}
			
			else if (pulId == infiltrativeId) {
				infilList.add(f89);
			}
			
			else if (pulId == disseminatedId) {
				disList.add(f89);
			}
			
			else if (pulId == cavernousId) {
				cavList.add(f89);
			}
			
			else if (pulId == fibroCavId) {
				fibCavList.add(f89);
			}
			
			else if (pulId == cirrhoticId) {
				cirrList.add(f89);
			}
			
			else if (pulId == primaryComplexId) {
				priCompList.add(f89);
			}
			
			else if (pulId == miliaryId) {
				miliaryList.add(f89);
			}
			
			else if (pulId == tuberculomaId) {
				tuberculomaList.add(f89);
			}
			
			else if (pulId == bronchiId) {
				bronchiList.add(f89);
			}
			
		}
		
		// FOCAL
		Concept q = ms.getConcept(MdrtbConcepts.FOCAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		Person p = null;
		int i = 0;
		for (Form89 tf : focalList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + focalList.size();
		report += "<br/>";
		
		// INFILTRATIVE
		q = ms.getConcept(MdrtbConcepts.INFILTRATIVE);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : infilList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + infilList.size();
		report += "<br/>";
		
		// DISSEMINATED
		q = ms.getConcept(MdrtbConcepts.DISSEMINATED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : disList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + disList.size();
		report += "<br/>";
		
		// CAVERNOUS
		q = ms.getConcept(MdrtbConcepts.CAVERNOUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : cavList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cavList.size();
		report += "<br/>";
		
		// FIBROUS_CAVERNOUS
		q = ms.getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : fibCavList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + fibCavList.size();
		report += "<br/>";
		
		// CIRRHOTIC
		q = ms.getConcept(MdrtbConcepts.CIRRHOTIC);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : cirrList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cirrList.size();
		report += "<br/>";
		
		// TB_PRIMARY_COMPLEX
		q = ms.getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : priCompList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + priCompList.size();
		report += "<br/>";
		
		// MILITARY
		q = ms.getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : miliaryList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + miliaryList.size();
		report += "<br/>";
		
		// TUBERCULOMA
		q = ms.getConcept(MdrtbConcepts.TUBERCULOMA);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : tuberculomaList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + tuberculomaList.size();
		report += "<br/>";
		
		// BRONCHUS
		q = ms.getConcept(MdrtbConcepts.BRONCHUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : bronchiList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + bronchiList.size();
		report += "<br/>";
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	// //////////////////////////////////////////////
	
	@RequestMapping("/module/mdrtb/reporting/byExtraPulmonaryLocation")
	public String byExtraPulmonaryLocation(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.byExtraPulmonaryLocation"));
		
		String report = "";
		
		ArrayList<Form89> plevlList = new ArrayList<Form89>();
		ArrayList<Form89> ofLymphList = new ArrayList<Form89>();
		ArrayList<Form89> osteoList = new ArrayList<Form89>();
		ArrayList<Form89> uroList = new ArrayList<Form89>();
		ArrayList<Form89> periLymphList = new ArrayList<Form89>();
		ArrayList<Form89> abdList = new ArrayList<Form89>();
		ArrayList<Form89> skinList = new ArrayList<Form89>();
		ArrayList<Form89> eyeList = new ArrayList<Form89>();
		ArrayList<Form89> cnsList = new ArrayList<Form89>();
		ArrayList<Form89> liverList = new ArrayList<Form89>();
		
		System.out.println("GETTING FORM89 LIST");
		
		ArrayList<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter,
		    month);
		
		Concept pulSite = null;
		
		//PULMONARY
		Concept plevConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		int plevId = plevConcept.getConceptId().intValue();
		Concept ofLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int ofLymphId = ofLymphConcept.getConceptId().intValue();
		Concept osteoConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		int osteoId = osteoConcept.getConceptId().intValue();
		Concept uroConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		int uroId = uroConcept.getConceptId().intValue();
		Concept periLymphConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int periLymphId = periLymphConcept.getConceptId().intValue();
		Concept abdConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ABDOMINAL);
		int abdId = abdConcept.getConceptId().intValue();
		Concept skinConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULODERMA);
		int skinId = skinConcept.getConceptId().intValue();
		Concept eyeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OCULAR);
		int eyeId = eyeConcept.getConceptId().intValue();
		Concept cnsConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_CNS);
		int cnsId = cnsConcept.getConceptId().intValue();
		Concept liverConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LIVER);
		int liverId = liverConcept.getConceptId().intValue();
		
		int pulId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			
			if (tb03.getPatient() == null || tb03.getPatient().isVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null || regGroup.getConceptId().intValue() != Integer
			        .parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId"))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			ArrayList<Form89> fList = Context.getService(MdrtbService.class)
			        .getForm89FormsFilledForPatientProgram(tb03.getPatient(), null, tb03.getPatProgId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			pulSite = f89.getEpLocation();
			
			if (pulSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			pulId = pulSite.getConceptId().intValue();
			
			if (pulId == plevId) {
				plevlList.add(f89);
			}
			
			else if (pulId == ofLymphId) {
				ofLymphList.add(f89);
			}
			
			else if (pulId == osteoId) {
				osteoList.add(f89);
			}
			
			else if (pulId == uroId) {
				uroList.add(f89);
			}
			
			else if (pulId == periLymphId) {
				periLymphList.add(f89);
			}
			
			else if (pulId == abdId) {
				abdList.add(f89);
			}
			
			else if (pulId == skinId) {
				skinList.add(f89);
			}
			
			else if (pulId == eyeId) {
				eyeList.add(f89);
			}
			
			else if (pulId == cnsId) {
				cnsList.add(f89);
			}
			
			else if (pulId == liverId) {
				liverList.add(f89);
			}
			
		}
		
		// FOCAL
		Concept q = ms.getConcept(MdrtbConcepts.PLEVRITIS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		Person p = null;
		int i = 0;
		for (Form89 tf : plevlList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + plevlList.size();
		report += "<br/>";
		
		// INFILTRATIVE
		q = ms.getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : ofLymphList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + ofLymphList.size();
		report += "<br/>";
		
		// DISSEMINATED
		q = ms.getConcept(MdrtbConcepts.OSTEOARTICULAR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : osteoList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + osteoList.size();
		report += "<br/>";
		
		// CAVERNOUS
		q = ms.getConcept(MdrtbConcepts.GENITOURINARY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : uroList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + uroList.size();
		report += "<br/>";
		
		// FIBROUS_CAVERNOUS
		q = ms.getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : periLymphList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + periLymphList.size();
		report += "<br/>";
		
		// CIRRHOTIC
		q = ms.getConcept(MdrtbConcepts.ABDOMINAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : abdList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + abdList.size();
		report += "<br/>";
		
		// TB_PRIMARY_COMPLEX
		q = ms.getConcept(MdrtbConcepts.TUBERCULODERMA);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : skinList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + skinList.size();
		report += "<br/>";
		
		// MILITARY
		q = ms.getConcept(MdrtbConcepts.OCULAR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : eyeList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + eyeList.size();
		report += "<br/>";
		
		// TUBERCULOMA
		q = ms.getConcept(MdrtbConcepts.OF_CNS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : cnsList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + cnsList.size();
		report += "<br/>";
		
		// BRONCHUS
		q = ms.getConcept(MdrtbConcepts.OF_LIVER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + "" + closeTD();
		report += closeTR();
		
		p = null;
		i = 0;
		for (Form89 tf : liverList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatProgId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += openTR();
				report += openTD() + i + closeTD();
				report += openTD() + getRegistrationNumber(tb03) + closeTD();
				report += renderPerson(p, true);
				report += openTD() + age + closeTD();
				report += openTD() + getPatientLink(tf) + closeTD();
				report += closeTR();
				
			}
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + liverList.size();
		report += "<br/>";
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	/////// PATIENTS WITH DR-TB
	@RequestMapping("/module/mdrtb/reporting/drTbPatients")
	public String drTbPatients(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		ArrayList<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03us);
		
		model.addAttribute("listName", getMessage("mdrtb.drTbPatients"));
		String report = "";
		
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.xpert") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + closeTD();
		report += openTD() + getMessage("mdrtb.culture") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugResistance") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.resistantTo") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.sensitiveTo") + closeTD();
		report += openTD() + getMessage("mdrtb.hivStatus") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03uRegistrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03uDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03u.changeOfRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();
		report += closeTR();
		
		report += openTR();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.inhShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.rifShort") + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.injectablesShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.quinShort") + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		
		report += closeTR();
		TB03Form tf = null;
		RegimenForm rf = null;
		int i = 0;
		Person p = null;
		for (TB03uForm tuf : tb03us) {
			tf = null;
			rf = null;
			if (tuf.getPatient() == null || tuf.getPatient().isVoided())
				continue;
			
			//tuf = Context.getService(MdrtbService.class).getTB03uFormForProgram(tf.getPatient(), tf.getPatProgId());
			
			tf = tuf.getTb03();
			
			if (tf == null)
				continue;
			
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report += openTR();
			report += openTD() + i + closeTD();
			report += openTD() + getRegistrationNumber(tf) + closeTD();
			report += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
			report += openTD() + getGender(p) + closeTD();
			report += openTD() + sdf.format(p.getBirthdate()) + closeTD();
			report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += openTD() + "" + closeTD();
				}
				
				else {
					if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId()
					                .intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == ms.getConcept(MdrtbConcepts.DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += openTD() + xr + closeTD();
							}
							
							else if (resId == ms.getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += openTD() + xr + closeTD();
							}
							
							else {
								report += openTD() + xr + closeTD();
							}
						}
						
						else {
							report += openTD() + xr + closeTD();
						}
					}
					
					else if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId()
					        .intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else {
						report += openTD() + "" + closeTD();
					}
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 1	
			List<HAINForm> hains = tf.getHains();
			if (hains != null && hains.size() != 0) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (rh != null) {
					report += openTD() + rh.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && hain2s.size() != 0) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (fq != null) {
					report += openTD() + fq.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.CULTURE_GROWTH)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.lists.growth") + closeTD();
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += openTD() + getMessage("mdrtb.positiveShort") + closeTD();
								break;
							}
							
						}
					}
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report += openTD() + tf.getResistanceType().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			report += openTD() + getResistantDrugs(tf) + closeTD();
			report += openTD() + getSensitiveDrugs(tf) + closeTD();
			
			if (tf.getHivStatus() != null) {
				report += openTD() + tf.getHivStatus().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcome() != null) {
				report += openTD() + tf.getTreatmentOutcome().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcomeDate() != null) {
				report += openTD() + sdf.format(tf.getTreatmentOutcomeDate()) + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			report += openTD() + TB03Util.getRegistrationNumber(tuf) + closeTD();
			report += openTD() + sdf.format(tuf.getEncounterDatetime()) + closeTD();
			
			if (tuf.getPatientCategory() != null)
				report += openTD() + tuf.getPatientCategory().getName().getName() + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report += openTD() + sdf.format(tuf.getMdrTreatmentStartDate()) + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatProgId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null) {
					report += openTD() + rf.getSldRegimenType().getName().getName() + closeTD();
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
				
				if (rf.getCouncilDate() != null) {
					report += openTD() + sdf.format(rf.getCouncilDate()) + closeTD();
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report += openTD() + tuf.getTreatmentOutcome().getName().getName() + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report += openTD() + sdf.format(tuf.getTreatmentOutcomeDate()) + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			report += openTD() + getPatientLink(tf) + closeTD();
			report += closeTR();
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsNoTreatment")
	public String drTbPatientsNoTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		ArrayList<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		model.addAttribute("listName", getMessage("mdrtb.drTbPatientsNoTreatment"));
		String report = "";
		
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.registrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.xpert") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + closeTD();
		report += openTD() + getMessage("mdrtb.culture") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugResistance") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.resistantTo") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.sensitiveTo") + closeTD();
		report += openTD() + getMessage("mdrtb.hivStatus") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.noTxReason") + closeTD();
		/*report += openTD() + getMessage("mdrtb.tb03uRegistrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03uDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03u.changeOfRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();*/
		report += closeTR();
		
		report += openTR();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.inhShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.rifShort") + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.injectablesShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.quinShort") + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		/*report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();*/
		
		report += closeTR();
		
		//RegimenForm rf = null;
		
		int noId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO).getConceptId().intValue();
		int unknownId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN).getConceptId().intValue();
		int monoId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONO).getConceptId().intValue();
		
		int i = 0;
		Person p = null;
		Concept resist = null;
		int resistId = 0;
		TB03Form tutf = null;
		Boolean found = false;
		
		for (TB03Form tf : tb03s) {
			resist = null;
			found = false;
			if (tf.getPatient() == null || tf.getPatient().isVoided())
				continue;
			
			resist = tf.getResistanceType();
			
			if (resist != null) {
				resistId = resist.getConceptId().intValue();
				if (resistId == noId || resistId == unknownId || resistId == monoId) {
					continue;
				}
				
			}
			
			else {
				
				continue;
			}
			
			//find mdrtb program with TB03 same as this form
			List<MdrtbPatientProgram> mdrtbPrograms = Context.getService(MdrtbService.class)
			        .getMdrtbPatientPrograms(tf.getPatient());
			if (mdrtbPrograms != null && mdrtbPrograms.size() != 0) {
				for (MdrtbPatientProgram mpp : mdrtbPrograms) {
					TB03uForm tuf = mpp.getTb03u();
					if (tuf != null) {
						tutf = tuf.getTb03();
						if (tutf != null) {
							if (!tutf.getEncounter().isVoided() && (tutf.getEncounter().getEncounterId().intValue() == tf
							        .getEncounter().getEncounterId().intValue())) {
								found = true;
								break;
							}
							
						}
						
					}
					
				}
				
			}
			
			//if program found, skip loop
			if (found == true)
				continue;
			
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report += openTR();
			report += openTD() + i + closeTD();
			report += openTD() + getRegistrationNumber(tf) + closeTD();
			report += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
			report += openTD() + getGender(p) + closeTD();
			report += openTD() + sdf.format(p.getBirthdate()) + closeTD();
			report += openTD() + tf.getAgeAtTB03Registration() + closeTD();
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += openTD() + "" + closeTD();
				}
				
				else {
					if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId()
					                .intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == ms.getConcept(MdrtbConcepts.DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += openTD() + xr + closeTD();
							}
							
							else if (resId == ms.getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += openTD() + xr + closeTD();
							}
							
							else {
								report += openTD() + xr + closeTD();
							}
						}
						
						else {
							report += openTD() + xr + closeTD();
						}
					}
					
					else if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId()
					        .intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else {
						report += openTD() + "" + closeTD();
					}
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 1	
			List<HAINForm> hains = tf.getHains();
			if (hains != null && hains.size() != 0) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (rh != null) {
					report += openTD() + rh.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tf.getHain2s();
			if (hain2s != null && hain2s.size() != 0) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (fq != null) {
					report += openTD() + fq.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.CULTURE_GROWTH)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.lists.growth") + closeTD();
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += openTD() + getMessage("mdrtb.positiveShort") + closeTD();
								break;
							}
							
						}
					}
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report += openTD() + tf.getResistanceType().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			report += openTD() + getResistantDrugs(tf) + closeTD();
			report += openTD() + getSensitiveDrugs(tf) + closeTD();
			
			if (tf.getHivStatus() != null) {
				report += openTD() + tf.getHivStatus().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcome() != null) {
				report += openTD() + tf.getTreatmentOutcome().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tf.getTreatmentOutcomeDate() != null) {
				report += openTD() + sdf.format(tf.getTreatmentOutcomeDate()) + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//////////////////////////////////////
			
			report += openTD() + "" + closeTD();
			
			report += openTD() + getPatientLink(tf) + closeTD();
			report += closeTR();
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsSuccessfulTreatment")
	public String drTbPatientsSuccessfulTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getOblast(oblastId).getName();
		}
		
		String dName = "";
		if (districtId != null) {
			dName = ms.getDistrict(districtId).getName();
			
		}
		
		String fName = "";
		if (facilityId != null) {
			fName = ms.getFacility(facilityId).getName();
			
		}
		
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		
		//ArrayList<Location> locList = Context.getService(MdrtbService.class).getLocationList(oblastId,districtId,facilityId);
		ArrayList<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			locList = Context.getService(MdrtbService.class).getLocationList(oblastId, districtId, facilityId);
		}
		
		ArrayList<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter,
		    month);
		Collections.sort(tb03us);
		
		model.addAttribute("listName", getMessage("mdrtb.drTbPatientsSuccessfulTreatment"));
		String report = "";
		
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += openTable();
		report += openTR();
		report += openTD() + getMessage("mdrtb.serialNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03uRegistrationNumber") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03uDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.name") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.gender") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.dateOfBirth") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.ageAtRegistration") + closeTD();
		report += openTD() + getMessage("mdrtb.xpert") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + closeTD();
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + closeTD();
		report += openTD() + getMessage("mdrtb.culture") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.drugResistance") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.resistantTo") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.sensitiveTo") + closeTD();
		report += openTD() + getMessage("mdrtb.hivStatus") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03u.changeOfRegimen") + closeTD();
		report += openTD() + getMessage("mdrtb.tb03.treatmentStartDate") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.outcome") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.endOfTreatmentDate") + closeTD();
		
		report += closeTR();
		
		report += openTR();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.inhShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.rifShort") + closeTD();
		report += openTD() + getMessage("mdrtb.result") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.injectablesShort") + closeTD();
		report += openTD() + getMessage("mdrtb.lists.quinShort") + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		report += openTD() + "" + closeTD();
		
		report += closeTR();
		
		RegimenForm rf = null;
		Concept outcomeConcept = null;
		int curedId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED).getConceptId().intValue();
		int txCompId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETED).getConceptId()
		        .intValue();
		int i = 0;
		Person p = null;
		for (TB03uForm tuf : tb03us) {
			
			rf = null;
			if (tuf.getPatient() == null || tuf.getPatient().isVoided())
				continue;
			
			if (tuf.getTreatmentOutcome() == null || (tuf.getTreatmentOutcome().getConceptId().intValue() != curedId
			        && tuf.getTreatmentOutcome().getConceptId().intValue() != txCompId))
				continue;
			
			//tuf = Context.getService(MdrtbService.class).getTB03uFormForProgram(tf.getPatient(), tf.getPatProgId());
			
			i++;
			p = Context.getPersonService().getPerson(tuf.getPatient().getId());
			report += openTR();
			report += openTD() + i + closeTD();
			report += openTD() + getRegistrationNumber(tuf) + closeTD();
			report += openTD() + sdf.format(tuf.getEncounterDatetime()) + closeTD();
			report += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
			report += openTD() + getGender(p) + closeTD();
			report += openTD() + sdf.format(p.getBirthdate()) + closeTD();
			report += openTD() + tuf.getAgeAtMDRRegistration() + closeTD();
			
			//XPERT
			List<XpertForm> xperts = tuf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += openTD() + "" + closeTD();
				}
				
				else {
					if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId()
					                .intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == ms.getConcept(MdrtbConcepts.DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += openTD() + xr + closeTD();
							}
							
							else if (resId == ms.getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += openTD() + xr + closeTD();
							}
							
							else {
								report += openTD() + xr + closeTD();
							}
						}
						
						else {
							report += openTD() + xr + closeTD();
						}
					}
					
					else if (mtb.getConceptId().intValue() == ms.getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId()
					        .intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else {
						report += openTD() + "" + closeTD();
					}
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 1	
			List<HAINForm> hains = tuf.getHains();
			if (hains != null && hains.size() != 0) {
				Collections.sort(hains);
				
				HAINForm h = hains.get(0);
				
				Concept ih = h.getInhResult();
				Concept rh = h.getRifResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (rh != null) {
					report += openTD() + rh.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//HAIN 2
			List<HAIN2Form> hain2s = tuf.getHain2s();
			if (hain2s != null && hain2s.size() != 0) {
				Collections.sort(hain2s);
				
				HAIN2Form h = hain2s.get(0);
				
				Concept ih = h.getInjResult();
				Concept fq = h.getFqResult();
				Concept res = h.getMtbResult();
				
				if (res != null) {
					report += openTD() + res.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (ih != null) {
					report += openTD() + ih.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
				if (fq != null) {
					report += openTD() + fq.getName().getName() + closeTD();
				} else {
					report += openTD() + "" + closeTD();
				}
				
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			//CULTURE
			List<CultureForm> cultures = tuf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.NEGATIVE)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.negativeShort") + closeTD();
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == ms.getConcept(MdrtbConcepts.CULTURE_GROWTH)
					        .getConceptId().intValue()) {
						report += openTD() + getMessage("mdrtb.lists.growth") + closeTD();
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += openTD() + getMessage("mdrtb.positiveShort") + closeTD();
								break;
							}
							
						}
					}
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			//Drug Resistance
			if (tuf.getResistanceType() != null) {
				report += openTD() + tuf.getResistanceType().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			report += openTD() + getResistantDrugs(tuf) + closeTD();
			report += openTD() + getSensitiveDrugs(tuf) + closeTD();
			
			if (tuf.getHivStatus() != null) {
				report += openTD() + tuf.getHivStatus().getName().getName() + closeTD();
			}
			
			else {
				report += openTD() + "" + closeTD();
			}
			
			if (tuf.getPatientCategory() != null)
				report += openTD() + tuf.getPatientCategory().getName().getName() + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report += openTD() + sdf.format(tuf.getMdrTreatmentStartDate()) + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatProgId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null) {
					report += openTD() + rf.getSldRegimenType().getName().getName() + closeTD();
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
				
				if (rf.getCouncilDate() != null) {
					report += openTD() + sdf.format(rf.getCouncilDate()) + closeTD();
				}
				
				else {
					report += openTD() + "" + closeTD();
				}
			}
			
			else {
				report += openTD() + "" + closeTD();
				report += openTD() + "" + closeTD();
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report += openTD() + tuf.getTreatmentOutcome().getName().getName() + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report += openTD() + sdf.format(tuf.getTreatmentOutcomeDate()) + closeTD();
			else
				report += openTD() + "" + closeTD();
			
			report += openTD() + getPatientLink(tuf) + closeTD();
			report += closeTR();
			
		}
		
		report += closeTable();
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	////////////////////// UTILITY FUNCTIONS???????????????????????????????????????
	private String getMessage(String code) {
		return Context.getMessageSourceService().getMessage(code);
	}
	
	private String openTable() {
		return "<table border=\"1\">";
		
	}
	
	private String closeTable() {
		return "</table>";
		
	}
	
	private String openTR() {
		return "<tr>";
		
	}
	
	private String closeTR() {
		return "</tr>";
		
	}
	
	private String openTD() {
		return "<td align=\"left\">";
	}
	
	private String openTD(String attribute) {
		return "<td align=\"left\">";
	}
	
	private String closeTD() {
		return "</td>";
		
	}
	
	private String renderPerson(Person p, boolean gender) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		String ret = "";
		ret += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
		
		if (gender) {
			String g = p.getGender();
			if (g.equals("M")) {
				g = Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.male");
			}
			
			else if (g.equals("F")) {
				g = Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female");
			}
			
			else {
				g = "";
			}
			
			ret += openTD() + g + closeTD();
		}
		
		ret += openTD() + dateFormat.format(p.getBirthdate()) + closeTD();
		
		return ret;
		
	}
	
	private String renderDate(Date date) {
		if (date == null)
			return "";
		
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		return dateFormat.format(date);
	}
	
	/*private String renderPerson(Person p) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		String ret = "";
		ret += openTD() + p.getFamilyName() + "," + p.getGivenName() + closeTD();
		ret += openTD() + dateFormat.format(p.getBirthdate()) + closeTD();
		
	
		//ret += openTD() + dateFormat.format(p.getBirthdate()) + closeTD();
		
		return ret;
		
	}*/
	
	private String getRegistrationNumber(TB03Form form) {
		String val = "";
		/*PatientIdentifier pi = null;
		Integer ppid = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs  = MdrtbUtil.getObsFromEncounter(ppidConcept, form.getEncounter());
		if(idObs==null) {
			val = null;
		}
		
		else {
			ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			
			if(pp!=null) {
				pi = Context.getService(MdrtbService.class).getGenPatientProgramIdentifier(pp);
				if(pi==null) {
					val = null;
				}
				
				else {
					val = pi.getIdentifier();
				}
			}
			
			else {
				val = null;
			}
		}*/
		val = form.getRegistrationNumber();
		if (val == null || val.length() == 0) {
			val = getMessage("mdrtb.unassigned");
		}
		
		return val;
	}
	
	private String getRegistrationNumber(TB03uForm form) {
		String val = "";
		PatientIdentifier pi = null;
		Integer ppid = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, form.getEncounter());
		if (idObs == null) {
			val = null;
		}
		
		else {
			ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			
			if (pp != null) {
				pi = Context.getService(MdrtbService.class).getGenPatientProgramIdentifier(pp);
				if (pi == null) {
					val = null;
				}
				
				else {
					val = pi.getIdentifier();
				}
			}
			
			else {
				val = null;
			}
		}
		if (val == null || val.length() == 0) {
			val = getMessage("mdrtb.unassigned");
		}
		
		return val;
	}
	
	public String getPatientLink(TB03Form form) {
		
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public String getPatientLink(TB03uForm form) {
		
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public String getPatientLink(Form89 form) {
		
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public String getGender(Person p) {
		
		String ret = "";
		;
		String gender = p.getGender();
		
		//  System.out.println(gender);
		
		if (gender.equals("F")) {
			return getMessage("mdrtb.tb03.gender.female");
		}
		
		else if (gender.equals("M"))
			return getMessage("mdrtb.tb03.gender.male");
		
		return ret;
	}
	
	public String getTransferFrom(TB03Form tf) {
		TransferInForm tif = getTransferInForm(tf);
		if (tif != null) {
			return tif.getLocation().toString();
		}
		
		else {
			return "";
		}
		
	}
	
	public String getTransferFromDate(TB03Form tf) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		TransferInForm tif = getTransferInForm(tf);
		if (tif != null) {
			return dateFormat.format(tif.getEncounterDatetime());
		}
		
		else {
			return "";
		}
	}
	
	public TransferInForm getTransferInForm(TB03Form tf) {
		TransferInForm tif = null;
		
		Integer ppid = tf.getPatProgId();
		TbPatientProgram tpp = Context.getService(MdrtbService.class).getTbPatientProgram(ppid);
		Date startDate = tpp.getDateEnrolled();
		Date endDate = tpp.getDateCompleted();
		
		ArrayList<TransferInForm> allTifs = Context.getService(MdrtbService.class)
		        .getTransferInFormsFilledForPatient(tf.getPatient());
		
		for (TransferInForm temp : allTifs) {
			if (tf.getEncounterDatetime().equals(temp.getEncounterDatetime())) {
				tif = temp;
				break;
			}
			
			else if (tf.getEncounterDatetime().before(temp.getEncounterDatetime())) {
				if (endDate != null && temp.getEncounterDatetime().before(endDate)) {
					tif = temp;
					break;
				}
				
				else if (endDate == null) {
					tif = temp;
					break;
				}
			}
		}
		
		return tif;
	}
	
	public String getSiteOfDisease(TB03Form tf) {
		
		if (tf.getAnatomicalSite() != null) {
			return tf.getAnatomicalSite().getName().getName();
		}
		
		else {
			return "";
		}
		
	}
	
	public String getRegistrationGroup(TB03Form tf) {
		
		if (tf.getRegistrationGroup() != null) {
			return tf.getRegistrationGroup().getName().getName();
		}
		
		else {
			return "";
		}
		
	}
	
	public String getResistantDrugs(TB03Form tf) {
		String drugs = "";
		List<DSTForm> dsts = tf.getDsts();
		
		if (dsts == null || dsts.size() == 0) {
			drugs = "";
		}
		
		else {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			return dst.getResistantDrugs();
			
		}
		
		return drugs;
	}
	
	public String getSensitiveDrugs(TB03Form tf) {
		String drugs = "";
		List<DSTForm> dsts = tf.getDsts();
		
		if (dsts == null || dsts.size() == 0) {
			drugs = "";
		}
		
		else {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			return dst.getSensitiveDrugs();
			
		}
		
		return drugs;
	}
	
	public String getResistantDrugs(TB03uForm tf) {
		String drugs = "";
		List<DSTForm> dsts = tf.getDsts();
		
		if (dsts == null || dsts.size() == 0) {
			drugs = "";
		}
		
		else {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			return dst.getResistantDrugs();
			
		}
		
		return drugs;
	}
	
	public String getSensitiveDrugs(TB03uForm tf) {
		String drugs = "";
		List<DSTForm> dsts = tf.getDsts();
		
		if (dsts == null || dsts.size() == 0) {
			drugs = "";
		}
		
		else {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			return dst.getSensitiveDrugs();
			
		}
		
		return drugs;
	}
	
	public String getReRegistrationNumber(TB03Form tf) {
		String ret = "";
		
		Integer ppid = tf.getPatProgId();
		
		if (ppid == null)
			return ret;
		
		Patient p = tf.getPatient();
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		List<TbPatientProgram> tpps = ms.getTbPatientPrograms(p);
		
		if (tpps == null || tpps.size() <= 1) {
			return ret;
		}
		
		//TbPatientProgram currentProg = ms.getTbPatientProgram(ppid);
		
		Collections.sort(tpps);
		
		int numPrograms = tpps.size();
		int index = 0;
		int foundIndex = -1;
		for (TbPatientProgram tpp : tpps) {
			
			if (tpp == null || tpp.getId() == null)
				continue;
			
			if (tpp.getId().intValue() == ppid.intValue()) {
				foundIndex = index;
				break;
			}
			
			index++;
			
		}
		
		if (foundIndex != -1) {
			if (foundIndex + 1 < numPrograms) {
				
				if (tpps.get(foundIndex + 1).getPatientIdentifier() != null)
					return tpps.get(foundIndex + 1).getPatientIdentifier().getIdentifier();
			}
		}
		
		return ret;
	}
	
	private static RegimenForm getFirstRegimenChangeForPatient(Patient p, Integer patProgId) {
		
		int patientId = p.getPatientId().intValue();
		ArrayList<RegimenForm> forms = Context.getService(MdrtbService.class).getRegimenFormsForProgram(p, patProgId);
		
		if (forms == null)
			return null;
		
		if (forms.size() >= 2) {
			return forms.get(1);
		}
		
		return null;
		
	}
	
}
