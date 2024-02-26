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
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.api.MdrtbService;
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

@SuppressWarnings("null")
@Controller
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
			}
			
			else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
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
				model.addAttribute("districtSelected", district);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
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
		model.addAttribute("monthSelected", month);
		model.addAttribute("quarterSelected", quarter);
		return new ModelAndView("/module/mdrtb/reporting/patientLists", model);
	}
	
	/* All Cases Enrolled */
	
	@RequestMapping("/module/mdrtb/reporting/allCasesEnrolled")
	public String allCasesEnrolled(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		String oName = "";
		if (oblastId != null) {
			oName = Context.getService(MdrtbService.class).getRegion(oblastId).getName();
		}
		String dName = "";
		if (districtId != null) {
			dName = Context.getService(MdrtbService.class).getDistrict(districtId).getName();
		}
		String fName = "";
		if (facilityId != null) {
			fName = Context.getService(MdrtbService.class).getFacility(facilityId).getName();
		}
		model.addAttribute("oblast", oName);
		model.addAttribute("district", dName);
		model.addAttribute("facility", fName);
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("quarter", quarter);
		model.addAttribute("listName", getMessage("mdrtb.allCasesEnrolled"));
		
		Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
		District district = Context.getService(MdrtbService.class).getDistrict(districtId);
		Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
		List<Location> locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getAllCasesEnrolledTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
	}
	
	public static String getAllCasesEnrolledTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		String report = "";
		
		//NEW CASES 
		
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentSiteIP") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.tbLocalization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		//report += "<td align=\"left\">" + getMessage("mdrtb.tb03.tbLocalization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.microscopy") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.xpert") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.culture") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugResistance") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.resistantTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.sensitiveTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.hivStatus") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.reregisrationNumber") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		report += "<tr>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.inhShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.rifShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.injectablesShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.quinShort") + "</td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		
		report += "</tr>";
		
		int i = 0;
		Person p = null;
		for (TB03Form tf : tb03s) {
			System.out.println("Generating data for Patient: " + tf.getPatient().getPatientIdentifier());
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report += "<tr>";
			report += "<td align=\"left\">" + i + "</td>";
			report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
			report += "<td align=\"left\">" + Context.getDateFormat().format(tf.getEncounterDatetime()) + "</td>";
			if (tf.getTreatmentStartDate() != null)
				report += "<td align=\"left\">" + Context.getDateFormat().format(tf.getTreatmentStartDate()) + "</td>";
			else
				report += "<td></td>";
			if (tf.getTreatmentSiteIP() != null) {
				report += "<td align=\"left\">" + tf.getTreatmentSiteIP().getName().getName() + "</td>";
			} else
				report += "<td></td>";
			report += "<td align=\"left\">" + p.getFamilyName() + "," + p.getGivenName() + "</td>";
			report += "<td align=\"left\">" + getGender(p) + "</td>";
			report += "<td align=\"left\">" + Context.getDateFormat().format(p.getBirthdate()) + "</td>";
			report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
			
			if (tf.getAnatomicalSite() != null) {
				Integer asId = tf.getAnatomicalSite().getConceptId();
				if (asId.intValue() == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)
				        .getConceptId().intValue()) {
					report += "<td align=\"left\">" + getMessage("mdrtb.lists.pulmonaryShort") + "</td>";
				} else if (asId.intValue() == Context.getService(MdrtbService.class)
				        .getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB).getConceptId().intValue()) {
					report += "<td align=\"left\">" + getMessage("mdrtb.lists.extrapulmonaryShort") + "</td>";
				} else {
					report += "<td></td>";
				}
				//report += "<td align=\"left\">" + tf.getAnatomicalSite().getName().getName().charAt(0) + "</td>";
			} else
				report += "<td></td>";
			if (tf.getRegistrationGroup() != null)
				report += "<td align=\"left\">" + tf.getRegistrationGroup().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			//SMEAR
			List<SmearForm> smears = tf.getSmears();
			if (smears != null && smears.size() != 0) {
				Collections.sort(smears);
				SmearForm ds = smears.get(0);
				if (ds.getSmearResult() != null) {
					if (ds.getSmearResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					} else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == ds.getSmearResult().getConceptId().intValue()) {
								report += "<td align=\"left\">" + getMessage("mdrtb.positiveShort") + "</td>";
								break;
							}
							
						}
					}
				} else {
					report += "<td></td>";
				}
			} else {
				report += "<td></td>";
			}
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				if (mtb == null) {
					report += "<td></td>";
				} else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						if (res != null) {
							int resId = res.getConceptId().intValue();
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += "<td align=\"left\">" + xr + "</td>";
							} else if (resId == Context.getService(MdrtbService.class)
							        .getConcept(MdrtbConcepts.NOT_DETECTED).getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += "<td align=\"left\">" + xr + "</td>";
							} else {
								report += "<td align=\"left\">" + xr + "</td>";
							}
						} else {
							report += "<td align=\"left\">" + xr + "</td>";
						}
					} else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					} else {
						report += "<td></td>";
					}
				}
			} else {
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (rh != null) {
					report += "<td align=\"left\">" + rh.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
			} else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				if (fq != null) {
					report += "<td align=\"left\">" + fq.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
			} else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				CultureForm dc = cultures.get(0);
				if (dc.getCultureResult() != null) {
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					} else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.lists.growth") + "</td>";
					} else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += "<td align=\"left\">" + getMessage("mdrtb.positiveShort") + "</td>";
								break;
							}
						}
					}
				} else {
					report += "<td></td>";
				}
			} else {
				report += "<td></td>";
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report += "<td align=\"left\">" + tf.getResistanceType().getName().getName() + "</td>";
			} else {
				report += "<td></td>";
			}
			report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
			report += "<td align=\"left\">" + getSensitiveDrugs(tf) + "</td>";
			if (tf.getHivStatus() != null) {
				report += "<td align=\"left\">" + tf.getHivStatus().getName().getName() + "</td>";
			} else {
				report += "<td></td>";
			}
			if (tf.getTreatmentOutcome() != null) {
				report += "<td align=\"left\">" + tf.getTreatmentOutcome().getName().getName() + "</td>";
			} else {
				report += "<td></td>";
			}
			if (tf.getTreatmentOutcomeDate() != null) {
				report += "<td align=\"left\">" + Context.getDateFormat().format(tf.getTreatmentOutcomeDate()) + "</td>";
			} else {
				report += "<td></td>";
			}
			
			//OTHER NUMBER
			report += "<td align=\"left\">" + getReRegistrationNumber(tf) + "</td>";
			report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
			report += "</tr>";
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* DOTS Cases by Registration Group */
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByRegistrationGroup")
	public String dotsCasesByRegistrationGroup(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsCasesByRegistrationGroup"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByRegistrationGroupTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDotsCasesByRegistrationGroupTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(tb03s);
		
		//NEW CASES 
		Concept newConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
		String report = "";
		report += "<h4>" + getMessage("mdrtb.lists.new") + "</h4>";
		report += "<table border=\"1\">";
		
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == newConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Relapse
		
		Concept relapse1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.relapses") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//AfterDefault
		Concept default1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		
		report += "<h4>" + getMessage("mdrtb.tb03.ltfu") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == default2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//AfterFailure
		Concept failure1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1);
		Concept failure2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2);
		
		report += "<h4>" + getMessage("mdrtb.tb03.failure") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		Concept transferInConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report += "<h4>" + getMessage("mdrtb.lists.transferIn") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.transferFrom") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.dateOfTransfer") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		temp = null;
		i = 0;
		p = null;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == transferInConcept.getId().intValue()) {
				
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getTransferFrom(tf) + "</td>";
				report += "<td align=\"left\">" + getTransferFromDate(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		//OTHER CASES 
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		report += "<h4>" + getMessage("mdrtb.tb03.other") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == otherConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		return report;
	}
	
	/* DOTS Cases by Anatomical Site */
	
	@RequestMapping("/module/mdrtb/reporting/dotsCasesByAnatomicalSite")
	public String dotsCasesByAnatomicalSite(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsCasesByAnatomicalSite"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByAnatomicalSiteTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDotsCasesByAnatomicalSiteTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		//NEW CASES 
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		Concept pulmonaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB);
		String report = "";
		report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == pulmonaryConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getRegistrationGroup(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Relapse
		
		Concept epConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB);
		
		report += "<h4>" + getMessage("mdrtb.extrapulmonary") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == epConcept.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getRegistrationGroup(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* By Drug Resistance */
	
	@RequestMapping("/module/mdrtb/reporting/byDrugResistance")
	public String byDrugResistance(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.byDrugResistance"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsCasesByDrugResistanceTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDotsCasesByDrugResistanceTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(tb03s);
		
		String report = "";
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONO);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// RIF
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// POLY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// MDR
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// PRE_XDR_TB
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRE_XDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// XDR_TB
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// TDR
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TDR_TB);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// UNKNOWN
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		// NO
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO);
		report += "<h4>" + getMessage("mdrtb.sensitive") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.localization") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugNames") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == q.getId().intValue()) {
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
				report += "<td align=\"left\">" + getSiteOfDisease(tf) + "</td>";
				report += "<td></td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* DOTS Pulmonary cases by Registration Group and Bacteriological Status */
	
	@RequestMapping("/module/mdrtb/reporting/dotsPulmonaryCasesByRegisrationGroupAndBacStatus")
	public String dotsPulmonaryCasesByRegisrationGroupAndBacStatus(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.dotsPulmonaryCasesByRegisrationGroupAndBacStatus"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(locList, year, quarterInt,
		    monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(List<Location> locList,
	        Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
		Concept siteConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
		Concept pulConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB);
		Concept newConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW);
		
		//NEW CASES + Positive
		String report = "";
		report += "<h4>" + getMessage("mdrtb.lists.newPulmonaryBacPositive") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Obs temp2 = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
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
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//NEW CASES + Negative
		
		report += "<h4>" + getMessage("mdrtb.lists.newPulmonaryBacNegative") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		temp2 = null;
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
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
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		//Relapse + positive
		Concept relapse1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_1);
		Concept relapse2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE_AFTER_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.relapsePulmonaryBacPositive") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		i = 0;
		p = null;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		//Relapse + negative
		report += "<h4>" + getMessage("mdrtb.lists.relapsePulmonaryBacNegative") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == relapse1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == relapse2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Retreament - Negative
		Concept default1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_1);
		Concept default2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULT_AFTER_REGIMEN_2);
		Concept failure1Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_1);
		Concept failure2Concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILURE_AFTER_REGIMEN_2);
		report += "<h4>" + getMessage("mdrtb.lists.retreatmentPulmonaryBacPositive") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (!MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getRegistrationGroup(tf) + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		report += "<h4>" + getMessage("mdrtb.lists.retreatmentPulmonaryBacNegative") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(siteConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == default1Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == default2Concept.getId().intValue()
			                || temp.getValueCoded().getId().intValue() == failure1Concept.getId().intValue() || temp
			                .getValueCoded().getId().intValue() == failure2Concept.getId().intValue())) {
				if (temp2 != null && temp2.getValueCoded() != null
				        && temp2.getValueCoded().getId().intValue() == pulConcept.getId().intValue()) {
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getRegistrationGroup(tf) + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		Concept transferInConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_TRANSFERRED_IN);
		
		report += "<h4>" + getMessage("mdrtb.lists.transferInPulmonaryBacPositive") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
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
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
						
					}
				}
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		//Transfer In
		
		report += "<h4>" + getMessage("mdrtb.lists.transferInPulmonaryBacNegative") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		temp = null;
		
		p = null;
		i = 0;
		for (TB03Form tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
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
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, true);
						report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
						
					}
				}
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* MDR-XDR Patients with no Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatientsNoTreatment")
	public String mdrXdrPatientsNoTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrXdrPatientsNoTreatment"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrXdrPatientsWithNoTreatmentTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getMdrXdrPatientsWithNoTreatmentTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month);
		
		//NEW CASES 
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept treatmentStartDate = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.MDR_TREATMENT_START_DATE);
		Concept mdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		String report = "";
		report += "<h4>" + getMessage("mdrtb.mdrtb") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Obs temp2 = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, false);
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		Concept xdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		
		report += "<h4>" + getMessage("mdrtb.xdrtb") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		temp = null;
		
		p = null;
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			temp2 = MdrtbUtil.getObsFromEncounter(treatmentStartDate, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()
			        && (temp2 == null || temp2.getValueDatetime() == null)) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, false);
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* MDR Successful Treatment Outcome */
	
	@RequestMapping("/module/mdrtb/reporting/mdrSuccessfulTreatmentOutcome")
	public String mdrSuccessfulTreatmentOutcome(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrSuccessfulTreatmentOutcome"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrSuccessfulTreatmentOutcomeTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getMdrSuccessfulTreatmentOutcomeTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TREATMENT_OUTCOME);
		Concept curedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED);
		Concept txCompleted = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE);
		
		//NEW CASES 
		String report = "";
		report += "<h4>" + getMessage("mdrtb.mdrSuccessfulTreatment") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null
			        && temp.getValueCoded() != null
			        && (temp.getValueCoded().getId().intValue() == curedConcept.getId().intValue() || temp.getValueCoded()
			                .getId().intValue() == txCompleted.getId().intValue())) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, false);
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* MDR-XDR Patients */
	
	@RequestMapping("/module/mdrtb/reporting/mdrXdrPatients")
	public String mdrXdrPatients(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.mdrXdrPatients"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMdrXdrPatientsTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getMdrXdrPatientsTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03uForm> tb03s = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month);
		
		Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		dateMap.get("startDate");
		dateMap.get("endDate");
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
		Concept mdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB);
		
		//NEW CASES 
		String report = "";
		report += "<h4>" + getMessage("mdrtb.mdrtb") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == mdr.getId().intValue()) {
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, false);
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		
		//EP
		Concept xdr = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XDR_TB);
		
		report += "<h4>" + getMessage("mdrtb.xdrtb") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		temp = null;
		
		p = null;
		i = 0;
		for (TB03uForm tf : tb03s) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
			if (temp != null && temp.getValueCoded() != null
			        && temp.getValueCoded().getId().intValue() == xdr.getId().intValue()) {
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
				report += renderPerson(p, false);
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Women of Child-bearing Age */
	
	@RequestMapping("/module/mdrtb/reporting/womenOfChildbearingAge")
	public String womenOfChildbearingAge(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.womenOfChildbearingAge"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getWomenOfChildbearingAgeTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getWomenOfChildbearingAgeTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> forms = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(forms);
		
		//NEW CASES 
		String report = "";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		//Obs temp = null;
		Person p = null;
		int i = 0;
		
		for (TB03Form tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("F")) {
				
				if (tf != null) {
					Integer age = tf.getAgeAtTB03Registration();
					
					if (age != null && age.intValue() >= 15 && age.intValue() <= 49) {
						p = Context.getPersonService().getPerson(tf.getPatient().getId());
						i++;
						report += "<tr>";
						report += "<td align=\"left\">" + i + "</td>";
						report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
						report += renderPerson(p, false);
						report += "<td align=\"left\">" + age + "</td>";
						if (tf.getRegistrationGroup() != null)
							report += "<td align=\"left\">" + tf.getRegistrationGroup().getName().getName() + "</td>";
						else
							report += "<td></td>";
						report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
						report += "</tr>";
					}
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Men of Conscript Age */
	
	@RequestMapping("/module/mdrtb/reporting/menOfConscriptAge")
	public String menOfConscriptAge(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.menOfConscriptAge"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getMenOfConscriptAgeTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getMenOfConscriptAgeTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03List);
		
		//NEW CASES 
		String report = "";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.caseDefinition") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		//Obs temp = null;
		Person p = null;
		int i = 0;
		for (TB03Form tf : tb03List) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			if (tf.getPatient().getGender().equals("M")) {
				
				Integer age = tf.getAgeAtTB03Registration();
				
				if (age != null && age.intValue() >= 18 && age.intValue() <= 27) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					if (tf.getRegistrationGroup() != null)
						report += "<td align=\"left\">" + tf.getRegistrationGroup().getName().getName() + "</td>";
					else
						report += "<td></td>";
					
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Concomitant Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withConcomitantDisease")
	public String withConcomitantDisease(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withConcomitantDisease"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithConcamitantDiseasesTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithConcamitantDiseasesTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03List);
		ArrayList<Form89> forms = new ArrayList<Form89>();
		Concept regGroup = null;
		Form89 f89 = null;
		String report = "";
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
				
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			f89.setTB03(tb03);
			forms.add(f89);
		}
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		report += "<h4>" + getMessage("mdrtb.withDiabetes") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER);
		report += "<h4>" + getMessage("mdrtb.withCancer") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL);
		report += "<h4>" + getMessage("mdrtb.withCOPD") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withHypertension") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER);
		report += "<h4>" + getMessage("mdrtb.withUlcer") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER);
		report += "<h4>" + getMessage("mdrtb.withMentalDisorder") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ICD20);
		report += "<h4>" + getMessage("mdrtb.withHIV") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		report += "<h4>" + getMessage("mdrtb.withHepatitis") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withKidneyDisease") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		report += "<br/>";
		
		groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE);
		report += "<h4>" + getMessage("mdrtb.withOtherDisease") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
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
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, true);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Cancer */
	
	@RequestMapping("/module/mdrtb/reporting/withCancer")
	public String withCancer(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withCancer"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithCancerTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithCancerTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withCancer") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
		}
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases detected from Contact Tracing */
	
	@RequestMapping("/module/mdrtb/reporting/detectedFromContact")
	public String detectedFromContact(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.detectedFromContact"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesDetectedFromContactTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesDetectedFromContactTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Collections.sort(forms);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		Concept fromContact = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.detectedFromContact") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == fromContact.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with COPD */
	
	@RequestMapping("/module/mdrtb/reporting/withCOPD")
	public String withCOPD(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withCOPD"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithCopdTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithCopdTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withCOPD") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
		}
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Hypertension */
	
	@RequestMapping("/module/mdrtb/reporting/withHypertension")
	public String withHypertension(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHypertension"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHypertensionTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithHypertensionTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withHypertension") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Ulcer */
	
	@RequestMapping("/module/mdrtb/reporting/withUlcer")
	public String withUlcer(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withUlcer"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithUlcerTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithUlcerTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withUlcer") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Mental Disorder */
	
	@RequestMapping("/module/mdrtb/reporting/withMentalDisorder")
	public String withMentalDisorder(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withMentalDisorder"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithMentalDisorderTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithMentalDisorderTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withMentalDisorder") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with HIV */
	
	@RequestMapping("/module/mdrtb/reporting/withHIV")
	public String withHIV(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHIV"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHivTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithHivTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ICD20);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withHIV") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Hepatitis */
	
	@RequestMapping("/module/mdrtb/reporting/withHepatitis")
	public String withHepatitis(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withHepatitis"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithHepatitisTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithHepatitisTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withHepatitis") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Kidney Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withKidneyDisease")
	public String withKidneyDisease(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withKidneyDisease"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithKidneyDiseaseTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithKidneyDiseaseTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withKidneyDisease") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					i++;
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases with Other Disease */
	
	@RequestMapping("/module/mdrtb/reporting/withOtherDisease")
	public String withOtherDisease(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.withOtherDisease"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesWithOtherDiseaseTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesWithOtherDiseaseTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<Form89> forms = Context.getService(MdrtbService.class).getForm89FormsFilled(locList, year, quarter, month);
		
		Concept groupConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE);
		Concept yes = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		
		String report = "";
		report += "<h4>" + getMessage("mdrtb.withOtherDisease") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Obs temp = null;
		Person p = null;
		int i = 0;
		for (Form89 tf : forms) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				temp = MdrtbUtil.getObsFromEncounter(groupConcept, tf.getEncounter());
				if (temp != null && (temp.getValueCoded().getId().intValue() == yes.getId().intValue())) {
					p = Context.getPersonService().getPerson(tf.getPatient().getId());
					i++;
					report += "<tr>";
					report += "<td align=\"left\">" + i + "</td>";
					report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
					report += renderPerson(p, false);
					report += "<td align=\"left\">" + age + "</td>";
					report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
					report += "</tr>";
				}
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* Cases by Soc Prof. Status */
	
	@RequestMapping("/module/mdrtb/reporting/bySocProfStatus")
	public String bySocProfStatus(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.bySocProfStatus"));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesBySocProfStatusTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
	}
	
	public static String getCasesBySocProfStatusTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		String report = "";
		Concept status = null;
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION);
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
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
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
			
			if (statusId == workerId)
				workerList.add(f89);
			else if (statusId == govtId)
				govtList.add(f89);
			else if (statusId == studentId)
				studentList.add(f89);
			else if (statusId == disabledId)
				disabledList.add(f89);
			else if (statusId == unemployedId)
				unemployedList.add(f89);
			else if (statusId == phcId)
				phcList.add(f89);
			else if (statusId == militaryId)
				militaryList.add(f89);
			else if (statusId == schoolId)
				schoolList.add(f89);
			else if (statusId == tbWorkerId)
				tbWorkerList.add(f89);
			else if (statusId == privateId)
				privateList.add(f89);
			else if (statusId == housewifeId)
				housewifeList.add(f89);
			else if (statusId == preschoolId)
				preschoolList.add(f89);
			else if (statusId == pensionerId)
				pensionerList.add(f89);
		}
		
		//WORKER
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : workerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + workerList.size();
		report += "<br/>";
		
		//GOVT SERVANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GOVT_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : govtList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + govtList.size();
		report += "<br/>";
		
		//STUDENT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STUDENT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : studentList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + studentList.size();
		report += "<br/>";
		
		//DISABLED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISABLED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : disabledList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + disabledList.size();
		report += "<br/>";
		
		//UNEMPLOYED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNEMPLOYED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : unemployedList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + unemployedList.size();
		report += "<br/>";
		
		//PHC WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : phcList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + phcList.size();
		report += "<br/>";
		
		//MILITARY SERVANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : militaryList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + militaryList.size();
		report += "<br/>";
		
		//SCHOOLCHILD
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SCHOOLCHILD);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : schoolList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + schoolList.size();
		report += "<br/>";
		
		//TB SERVICES WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : tbWorkerList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + tbWorkerList.size();
		report += "<br/>";
		
		//PRIVATE SECTOR WORKER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + privateList.size();
		report += "<br/>";
		
		//HOUSEWIFE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOUSEWIFE);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : housewifeList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + housewifeList.size();
		report += "<br/>";
		
		//PRE-SCHOOL CHILD
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESCHOOL_CHILD);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : preschoolList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + preschoolList.size();
		report += "<br/>";
		
		//PENSIONER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PENSIONER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : pensionerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + pensionerList.size();
		report += "<br/>";
		return report;
	}
	
	/* Cases by Population Category */
	
	@RequestMapping("/module/mdrtb/reporting/byPopCategory")
	public String byPopulationCategory(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPopCategory"));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPopulationCategoryTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByPopulationCategoryTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		String report = "";
		Concept category = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY);
		
		ArrayList<Form89> thisList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		ArrayList<Form89> foreignerList = new ArrayList<Form89>();
		ArrayList<Form89> welfareList = new ArrayList<Form89>();
		ArrayList<Form89> homelessList = new ArrayList<Form89>();
		ArrayList<Form89> prisonerList = new ArrayList<Form89>();
		ArrayList<Form89> investigationList = new ArrayList<Form89>();
		
		//CATEGORY
		Concept thisConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		int thisId = thisConcept.getConceptId().intValue();
		Concept otherConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		int otherId = otherConcept.getConceptId().intValue();
		Concept foreignerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOREIGNER);
		int foreignerId = foreignerConcept.getConceptId().intValue();
		Concept welfareConcept = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
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
			
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
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
			
			if (catId == thisId)
				thisList.add(f89);
			else if (catId == otherId)
				otherList.add(f89);
			else if (catId == foreignerId)
				foreignerList.add(f89);
			else if (catId == welfareId)
				welfareList.add(f89);
			else if (catId == homelessId)
				homelessList.add(f89);
			else if (catId == prisonerId)
				prisonerList.add(f89);
			else if (catId == investigationId)
				investigationList.add(f89);
		}
		
		//RESIDENT_OF_TERRITORY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OF_TERRITORY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : thisList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + thisList.size();
		report += "<br/>";
		
		//RESIDENT_OTHER_TERRITORY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_OTHER_TERRITORY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		report += "<br/>";
		
		//FOREIGNER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOREIGNER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.form89.countryOfOrigin") + "</td>";
		
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : foreignerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + tf.getCountryOfOrigin() + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + foreignerList.size();
		report += "<br/>";
		
		//RESIDENT_SOCIAL_SECURITY_FACILITY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESIDENT_SOCIAL_SECURITY_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : welfareList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + welfareList.size();
		report += "<br/>";
		
		//HOMELESS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOMELESS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : homelessList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + homelessList.size();
		report += "<br/>";
		
		//CONVICTED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONVICTED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : prisonerList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + prisonerList.size();
		report += "<br/>";
		
		//ON_REMAND
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ON_REMAND);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : investigationList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + investigationList.size();
		return report;
	}
	
	/* Cases by Dwelling */
	
	@RequestMapping("/module/mdrtb/reporting/byDwelling")
	public String byDwelling(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byDwelling"));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByDwellingTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByDwellingTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03List);
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE);
		Concept type = null;
		String report = "";
		
		ArrayList<Form89> cityList = new ArrayList<Form89>();
		ArrayList<Form89> villageList = new ArrayList<Form89>();
		//PLACE
		Concept cityConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY);
		int cityId = cityConcept.getConceptId().intValue();
		Concept villageConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		int villageId = villageConcept.getConceptId().intValue();
		
		int typeId = 0;
		Form89 f89 = null;
		Concept regGroup = null;
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
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
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY);
		report += "<h4>" + getMessage("mdrtb.lists.city") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : cityList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cityList.size();
		report += "<br/>";
		
		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		report += "<h4>" + getMessage("mdrtb.lists.village") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : villageList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + villageList.size();
		return report;
	}
	
	/* Cases by Places of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byPlaceOfDetection")
	public String byPlaceOfDetection(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPlaceOfDetection"));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPlaceOfDetectionTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByPlaceOfDetectionTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		Collections.sort(tb03List);
		
		ArrayList<Form89> tbList = new ArrayList<Form89>();
		ArrayList<Form89> privateList = new ArrayList<Form89>();
		ArrayList<Form89> phcList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		
		Concept circSite = null;
		
		String report = "";
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
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
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
			
			if (circId == tbId)
				tbList.add(f89);
			else if (circId == privateId)
				privateList.add(f89);
			else if (circId == phcId)
				phcList.add(f89);
			else if (circId == otherId)
				otherList.add(f89);
		}
		
		//TB FACILITY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : tbList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + tbList.size();
		report += "<br/>";
		
		//PHC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : phcList) {
			
			if (tf.getPatient() == null || tf.getPatient().getVoided())
				continue;
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + phcList.size();
		report += "<br/>";
		
		//Private Sector
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : privateList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + privateList.size();
		report += "<br/>";
		
		//OTHER MED FAC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : otherList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		return report;
	}
	
	/* Cases by Circumstances of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byCircumstancesOfDetection")
	public String byCircumstancesOfDetection(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byCircumstancesOfDetection"));
		
		System.out.println("GETTING FORM89 LIST");
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByCircumstancesOfDetectionTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByCircumstancesOfDetectionTable(List<Location> locList, Integer year, Integer quarterInt,
	        Integer monthInt) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarterInt,
		    monthInt);
		Collections.sort(tb03List);
		
		ArrayList<Form89> selfRefList = new ArrayList<Form89>();
		ArrayList<Form89> baselineExamList = new ArrayList<Form89>();
		ArrayList<Form89> postmortemList = new ArrayList<Form89>();
		ArrayList<Form89> contactList = new ArrayList<Form89>();
		ArrayList<Form89> migrantList = new ArrayList<Form89>();
		
		Concept circSite = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION);
		
		String report = "";
		//CIRCUMSTANCES
		Concept selfRefConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SELF_REFERRAL);
		int selfRefId = selfRefConcept.getConceptId().intValue();
		Concept baselineExamConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BASELINE_EXAM);
		int baselineExamId = baselineExamConcept.getConceptId().intValue();
		Concept postmortemConcept = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
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
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				
				continue;
			}
			
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
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
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SELF_REFERRAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : selfRefList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				i++;
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + selfRefList.size();
		report += "<br/>";
		
		//BASELINE_EXAM
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BASELINE_EXAM);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : baselineExamList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + baselineExamList.size();
		report += "<br/>";
		
		//POSTMORTERM_IDENTIFICATION
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSTMORTERM_IDENTIFICATION);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : postmortemList) {
			
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + postmortemList.size();
		//CONTACT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : contactList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + contactList.size();
		report += "<br/>";
		
		//MIGRANT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MIGRANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.form89.cityOfOrigin") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.form89.dateOfReturn") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : migrantList) {
			
			TB03Form tb03 = null;
			
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + tf.getCityOfOrigin() + "</td>";
				report += "<td align=\"left\">" + renderDate(tf.getDateOfReturn()) + "</td>";
				
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + migrantList.size();
		return report;
	}
	
	/* Cases by Method of Detection */
	
	@RequestMapping("/module/mdrtb/reporting/byMethodOfDetection")
	public String byMethodOfDetection(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byMethodOfDetection"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByMethodOfDetectionTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByMethodOfDetectionTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		ArrayList<Form89> fluorographyList = new ArrayList<Form89>();
		ArrayList<Form89> genexpertList = new ArrayList<Form89>();
		ArrayList<Form89> microscopyList = new ArrayList<Form89>();
		ArrayList<Form89> tuberculinList = new ArrayList<Form89>();
		ArrayList<Form89> hainList = new ArrayList<Form89>();
		ArrayList<Form89> cultureList = new ArrayList<Form89>();
		ArrayList<Form89> histologyList = new ArrayList<Form89>();
		ArrayList<Form89> cxrList = new ArrayList<Form89>();
		ArrayList<Form89> otherList = new ArrayList<Form89>();
		
		Concept method = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION);
		
		String report = "";
		//METHOD
		Concept fluorographyConcept = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.FLURORESCENT_MICROSCOPY);
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
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
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
			if (methodId == fluorographyId)
				fluorographyList.add(f89);
			else if (methodId == genexpertId)
				genexpertList.add(f89);
			else if (methodId == tuberculinId)
				tuberculinList.add(f89);
			else if (methodId == hainId)
				hainList.add(f89);
			else if (methodId == cultureId)
				cultureList.add(f89);
			else if (methodId == histologyId)
				histologyList.add(f89);
			else if (methodId == cxrId)
				cxrList.add(f89);
			else if (methodId == otherId)
				otherList.add(f89);
		}
		
		//FLUOROGRAPHY
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		Person p = null;
		int i = 0;
		for (Form89 tf : fluorographyList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + fluorographyList.size();
		report += "<br/>";
		
		//GENEXPERT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENEXPERT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : genexpertList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + genexpertList.size();
		report += "<br/>";
		
		//FLURORESCENT_MICROSCOPY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FLURORESCENT_MICROSCOPY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : microscopyList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + microscopyList.size();
		report += "<br/>";
		
		//TUBERCULIN_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULIN_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : tuberculinList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + tuberculinList.size();
		report += "<br/>";
		
		//HAIN_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : hainList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + hainList.size();
		report += "<br/>";
		
		//CULTURE_TEST
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_TEST);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : cultureList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cultureList.size();
		report += "<br/>";
		
		//HISTOLOGY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HISTOLOGY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : histologyList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + histologyList.size();
		report += "<br/>";
		
		//CXR_RESULT
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CXR_RESULT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : cxrList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cxrList.size();
		report += "<br/>";
		
		//OTHER
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		
		for (Form89 tf : otherList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				i++;
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + otherList.size();
		return report;
	}
	
	/* Cases by Pulmonary Location */
	
	@RequestMapping("/module/mdrtb/reporting/byPulmonaryLocation")
	public String byPulmonaryLocation(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		model.addAttribute("listName", getMessage("mdrtb.byPulmonaryLocation"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByPulmonaryLocationTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByPulmonaryLocationTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
		ArrayList<Form89> focalList = new ArrayList<>();
		ArrayList<Form89> infilList = new ArrayList<>();
		ArrayList<Form89> disList = new ArrayList<>();
		ArrayList<Form89> cavList = new ArrayList<>();
		ArrayList<Form89> fibCavList = new ArrayList<>();
		ArrayList<Form89> cirrList = new ArrayList<>();
		ArrayList<Form89> priCompList = new ArrayList<>();
		ArrayList<Form89> miliaryList = new ArrayList<>();
		ArrayList<Form89> tuberculomaList = new ArrayList<>();
		ArrayList<Form89> bronchiList = new ArrayList<>();
		Concept pulSite;
		
		String report = "";
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
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			
			f89 = fList.get(0);
			
			anatomicalSite = f89.getAnatomicalSite();
			
			if (anatomicalSite == null)
				continue;
			
			f89.setTB03(tb03);
			
			pulId = pulSite.getConceptId();
			
			if (pulId == focalId)
				focalList.add(f89);
			else if (pulId == infiltrativeId)
				infilList.add(f89);
			else if (pulId == disseminatedId)
				disList.add(f89);
			else if (pulId == cavernousId)
				cavList.add(f89);
			else if (pulId == fibroCavId)
				fibCavList.add(f89);
			else if (pulId == cirrhoticId)
				cirrList.add(f89);
			else if (pulId == primaryComplexId)
				priCompList.add(f89);
			else if (pulId == miliaryId)
				miliaryList.add(f89);
			else if (pulId == tuberculomaId)
				tuberculomaList.add(f89);
			else if (pulId == bronchiId)
				bronchiList.add(f89);
		}
		
		// FOCAL
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOCAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		Person p = null;
		int i = 0;
		for (Form89 tf : focalList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + focalList.size();
		report += "<br/>";
		
		// INFILTRATIVE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INFILTRATIVE);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : infilList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + infilList.size();
		report += "<br/>";
		
		// DISSEMINATED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISSEMINATED);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : disList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + disList.size();
		report += "<br/>";
		
		// CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAVERNOUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : cavList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cavList.size();
		report += "<br/>";
		
		// FIBROUS_CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : fibCavList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + fibCavList.size();
		report += "<br/>";
		
		// CIRRHOTIC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRRHOTIC);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : cirrList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cirrList.size();
		report += "<br/>";
		
		// TB_PRIMARY_COMPLEX
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : priCompList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + priCompList.size();
		report += "<br/>";
		
		// MILITARY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : miliaryList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + miliaryList.size();
		report += "<br/>";
		
		// TUBERCULOMA
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOMA);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : tuberculomaList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + tuberculomaList.size();
		report += "<br/>";
		
		// BRONCHUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BRONCHUS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : bronchiList) {
			
			TB03Form tb03 = null;
			//tf.initTB03(tf.getPatientProgramId());
			tb03 = tf.getTB03();
			
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
				
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + bronchiList.size();
		report += "<br/>";
		return report;
	}
	
	/* Cases by Extra-Pulmonary Location */
	
	@RequestMapping("/module/mdrtb/reporting/byExtraPulmonaryLocation")
	public String byExtraPulmonaryLocation(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		model.addAttribute("listName", getMessage("mdrtb.byExtraPulmonaryLocation"));
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getCasesByExtraPulmonaryLocationTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getCasesByExtraPulmonaryLocationTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		
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
		
		Concept pulSite;
		
		String report = "";
		//PULMONARY
		Concept plevConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		int plevId = plevConcept.getConceptId().intValue();
		Concept ofLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int ofLymphId = ofLymphConcept.getConceptId().intValue();
		Concept osteoConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		int osteoId = osteoConcept.getConceptId().intValue();
		Concept uroConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		int uroId = uroConcept.getConceptId().intValue();
		Concept periLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
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
		
		int pulId;
		Form89 f89;
		Concept regGroup;
		Collections.sort(tb03List);
		
		for (TB03Form tb03 : tb03List) {
			if (tb03.getPatient() == null || tb03.getPatient().getVoided()) {
				System.out.println("patient void - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			regGroup = null;
			regGroup = tb03.getRegistrationGroup();
			if (regGroup == null
			        || regGroup.getConceptId().intValue() != Integer.parseInt(Context.getAdministrationService()
			                .getGlobalProperty(MdrtbConstants.GP_NEW_CONCEPT_ID))) {
				System.out.println("Not new - skipping ENC: " + tb03.getEncounter().getEncounterId());
				continue;
			}
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tb03.getPatient(), null, tb03.getPatientProgramId(), null, null, null);
			if (fList == null || fList.size() != 1) {
				System.out.println("no f89 - skipping " + tb03.getPatient().getPatientId());
				continue;
			}
			f89 = fList.get(0);
			ptbLocation = f89.getEptbLocation();
			if (ptbLocation == null)
				continue;
			f89.setTB03(tb03);
			pulId = pulSite.getConceptId();
			if (pulId == plevId)
				plevlList.add(f89);
			else if (ptbId == ofLymphId)
				ofLymphList.add(f89);
			else if (ptbId == osteoId)
				osteoList.add(f89);
			else if (ptbId == uroId)
				uroList.add(f89);
			else if (ptbId == periLymphId)
				periLymphList.add(f89);
			else if (ptbId == abdId)
				abdList.add(f89);
			else if (ptbId == skinId)
				skinList.add(f89);
			else if (ptbId == eyeId)
				eyeList.add(f89);
			else if (ptbId == cnsId)
				cnsList.add(f89);
			else if (ptbId == liverId)
				liverList.add(f89);
		}
		
		// FOCAL
		Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		Person p = null;
		int i = 0;
		for (Form89 tf : plevlList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + plevlList.size();
		report += "<br/>";
		
		// INFILTRATIVE
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : ofLymphList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + ofLymphList.size();
		report += "<br/>";
		
		// DISSEMINATED
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : osteoList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + osteoList.size();
		report += "<br/>";
		
		// CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : uroList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + uroList.size();
		report += "<br/>";
		
		// FIBROUS_CAVERNOUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : periLymphList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + periLymphList.size();
		report += "<br/>";
		
		// CIRRHOTIC
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ABDOMINAL);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : abdList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + abdList.size();
		report += "<br/>";
		
		// TB_PRIMARY_COMPLEX
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULODERMA);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : skinList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + skinList.size();
		report += "<br/>";
		
		// MILITARY
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OCULAR);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : eyeList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + eyeList.size();
		report += "<br/>";
		
		// TUBERCULOMA
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_CNS);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : cnsList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + cnsList.size();
		report += "<br/>";
		
		// BRONCHUS
		q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LIVER);
		report += "<h4>" + q.getName().getName() + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td></td>";
		report += "</tr>";
		
		p = null;
		i = 0;
		for (Form89 tf : liverList) {
			TB03Form tb03 = null;
			tb03 = tf.getTB03();
			if (tb03 != null) {
				i++;
				Integer age = tb03.getAgeAtTB03Registration();
				p = Context.getPersonService().getPerson(tf.getPatient().getId());
				report += "<tr>";
				report += "<td align=\"left\">" + i + "</td>";
				report += "<td align=\"left\">" + getRegistrationNumber(tb03) + "</td>";
				report += renderPerson(p, true);
				report += "<td align=\"left\">" + age + "</td>";
				report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
				report += "</tr>";
			}
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + liverList.size();
		report += "<br/>";
		return report;
	}
	
	/* DR-TB Patients */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatients")
	public String drTbPatients(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		model.addAttribute("listName", getMessage("mdrtb.drTbPatients"));
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDrtbCasesTable(locList, year, quarterInt, monthInt);
		
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDrtbCasesTable(List<Location> locList, Integer year, Integer quarter, Integer month) {
		
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		
		List<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarter, month);
		
		Collections.sort(tb03us);
		
		String report = "";
		
		//NEW CASES 
		
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.xpert") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.culture") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugResistance") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.resistantTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.sensitiveTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.hivStatus") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03uRegistrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03uDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03u.changeOfRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";
		report += "</tr>";
		
		report += "<tr>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.inhShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.rifShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.injectablesShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.quinShort") + "</td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		
		report += "</tr>";
		TB03Form tf = null;
		RegimenForm rf = null;
		int i = 0;
		Person p = null;
		for (TB03uForm tuf : tb03us) {
			tf = null;
			rf = null;
			if (tuf.getPatient() == null || tuf.getPatient().getVoided())
				continue;
			
			tf = tuf.getTb03();
			
			if (tf == null)
				continue;
			
			i++;
			p = Context.getPersonService().getPerson(tf.getPatient().getId());
			report += "<tr>";
			report += "<td align=\"left\">" + i + "</td>";
			report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
			report += "<td align=\"left\">" + p.getFamilyName() + "," + p.getGivenName() + "</td>";
			report += "<td align=\"left\">" + getGender(p) + "</td>";
			report += "<td align=\"left\">" + sdf.format(p.getBirthdate()) + "</td>";
			report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += "<td></td>";
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += "<td align=\"left\">" + xr + "</td>";
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += "<td align=\"left\">" + xr + "</td>";
							} else {
								report += "<td align=\"left\">" + xr + "</td>";
							}
						} else {
							report += "<td align=\"left\">" + xr + "</td>";
						}
					} else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					} else {
						report += "<td></td>";
					}
				}
			} else {
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (rh != null) {
					report += "<td align=\"left\">" + rh.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (fq != null) {
					report += "<td align=\"left\">" + fq.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.lists.growth") + "</td>";
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += "<td align=\"left\">" + getMessage("mdrtb.positiveShort") + "</td>";
								break;
							}
						}
					}
				}
				
				else {
					report += "<td></td>";
				}
			}
			
			else {
				report += "<td></td>";
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null)
				report += "<td align=\"left\">" + tf.getResistanceType().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
			report += "<td align=\"left\">" + getSensitiveDrugs(tf) + "</td>";
			
			if (tf.getHivStatus() != null)
				report += "<td align=\"left\">" + tf.getHivStatus().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tf.getTreatmentOutcome() != null)
				report += "<td align=\"left\">" + tf.getTreatmentOutcome().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tf.getTreatmentOutcomeDate() != null)
				report += "<td align=\"left\">" + sdf.format(tf.getTreatmentOutcomeDate()) + "</td>";
			else
				report += "<td></td>";
			
			report += "<td align=\"left\">" + TB03Util.getRegistrationNumber(tuf) + "</td>";
			report += "<td align=\"left\">" + sdf.format(tuf.getEncounterDatetime()) + "</td>";
			
			if (tuf.getPatientCategory() != null)
				report += "<td align=\"left\">" + tuf.getPatientCategory().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report += "<td align=\"left\">" + sdf.format(tuf.getMdrTreatmentStartDate()) + "</td>";
			else
				report += "<td></td>";
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatientProgramId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null)
					report += "<td align=\"left\">" + rf.getSldRegimenType().getName().getName() + "</td>";
				else
					report += "<td></td>";
				if (rf.getCouncilDate() != null)
					report += "<td align=\"left\">" + sdf.format(rf.getCouncilDate()) + "</td>";
				else
					report += "<td></td>";
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report += "<td align=\"left\">" + tuf.getTreatmentOutcome().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report += "<td align=\"left\">" + sdf.format(tuf.getTreatmentOutcomeDate()) + "</td>";
			else
				report += "<td></td>";
			
			report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
			report += "</tr>";
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* DR-TB Patients without Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsNoTreatment")
	public String drTbPatientsNoTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		model.addAttribute("listName", getMessage("mdrtb.drTbPatientsNoTreatment"));
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		
		String report = getDrTbPatientsWithoutTreatmentTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDrTbPatientsWithoutTreatmentTable(List<Location> locList, Integer year, Integer quarter,
	        Integer month) {
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		List<TB03Form> tb03s = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month);
		Collections.sort(tb03s);
		
		String report = "";
		//NEW CASES 
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.registrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.xpert") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.culture") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugResistance") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.resistantTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.sensitiveTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.hivStatus") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.noTxReason") + "</td>";
		/*report += "<td align=\"left\">" + getMessage("mdrtb.tb03uRegistrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03uDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03u.changeOfRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";*/
		report += "</tr>";
		
		report += "<tr>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.inhShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.rifShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.injectablesShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.quinShort") + "</td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		/*report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";*/
		
		report += "</tr>";
		
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
			if (tf.getPatient() == null || tf.getPatient().getVoided())
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
			List<MdrtbPatientProgram> mdrtbPrograms = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(
			    tf.getPatient());
			if (mdrtbPrograms != null && mdrtbPrograms.size() != 0) {
				for (MdrtbPatientProgram mpp : mdrtbPrograms) {
					TB03uForm tuf = mpp.getTb03u();
					if (tuf != null) {
						tutf = tuf.getTb03();
						if (tutf != null) {
							if (!tutf.getEncounter().getVoided()
							        && (tutf.getEncounter().getEncounterId().intValue() == tf.getEncounter()
							                .getEncounterId().intValue())) {
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
			report += "<tr>";
			report += "<td align=\"left\">" + i + "</td>";
			report += "<td align=\"left\">" + getRegistrationNumber(tf) + "</td>";
			report += "<td align=\"left\">" + p.getFamilyName() + "," + p.getGivenName() + "</td>";
			report += "<td align=\"left\">" + getGender(p) + "</td>";
			report += "<td align=\"left\">" + sdf.format(p.getBirthdate()) + "</td>";
			report += "<td align=\"left\">" + tf.getAgeAtTB03Registration() + "</td>";
			
			//XPERT
			List<XpertForm> xperts = tf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += "<td></td>";
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += "<td align=\"left\">" + xr + "</td>";
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += "<td align=\"left\">" + xr + "</td>";
							}
							
							else {
								report += "<td align=\"left\">" + xr + "</td>";
							}
						}
						
						else {
							report += "<td align=\"left\">" + xr + "</td>";
						}
					}
					
					else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					}
					
					else {
						report += "<td></td>";
					}
				}
				
			}
			
			else {
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (rh != null) {
					report += "<td align=\"left\">" + rh.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (fq != null) {
					report += "<td align=\"left\">" + fq.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
			}
			
			//CULTURE
			List<CultureForm> cultures = tf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.lists.growth") + "</td>";
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += "<td align=\"left\">" + getMessage("mdrtb.positiveShort") + "</td>";
								break;
							}
							
						}
					}
				}
				
				else {
					report += "<td></td>";
				}
			}
			
			else {
				report += "<td></td>";
			}
			
			//Drug Resistance
			if (tf.getResistanceType() != null) {
				report += "<td align=\"left\">" + tf.getResistanceType().getName().getName() + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			report += "<td align=\"left\">" + getResistantDrugs(tf) + "</td>";
			report += "<td align=\"left\">" + getSensitiveDrugs(tf) + "</td>";
			
			if (tf.getHivStatus() != null) {
				report += "<td align=\"left\">" + tf.getHivStatus().getName().getName() + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			if (tf.getTreatmentOutcome() != null) {
				report += "<td align=\"left\">" + tf.getTreatmentOutcome().getName().getName() + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			if (tf.getTreatmentOutcomeDate() != null) {
				report += "<td align=\"left\">" + sdf.format(tf.getTreatmentOutcomeDate()) + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			//////////////////////////////////////
			
			report += "<td></td>";
			
			report += "<td align=\"left\">" + getPatientLink(tf) + "</td>";
			report += "</tr>";
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	/* DR-TB Patients with Successful Treatment */
	
	@RequestMapping("/module/mdrtb/reporting/drTbPatientsSuccessfulTreatment")
	public String drTbPatientsSuccessfulTreatment(@RequestParam("district") Integer districtId,
	        @RequestParam("oblast") Integer oblastId, @RequestParam("facility") Integer facilityId,
	        @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) throws EvaluationException {
		
		MdrtbService ms = Context.getService(MdrtbService.class);
		
		String oName = "";
		if (oblastId != null) {
			oName = ms.getRegion(oblastId).getName();
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
		model.addAttribute("listName", getMessage("mdrtb.drTbPatientsSuccessfulTreatment"));
		
		List<Location> locList = null;
		if (oblastId.intValue() == 186) {
			locList = Context.getService(MdrtbService.class).getLocationListForDushanbe(oblastId, districtId, facilityId);
		} else {
			Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
			District district = Context.getService(MdrtbService.class).getDistrict(districtId);
			Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
			locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		String report = getDrTbCasesWithSuccessfulTreatmentTable(locList, year, quarterInt, monthInt);
		model.addAttribute("report", report);
		return "/module/mdrtb/reporting/patientListsResults";
		
	}
	
	public static String getDrTbCasesWithSuccessfulTreatmentTable(List<Location> locList, Integer year, Integer quarterInt,
	        Integer monthInt) {
		List<TB03uForm> tb03us = Context.getService(MdrtbService.class).getTB03uFormsFilled(locList, year, quarterInt,
		    monthInt);
		SimpleDateFormat sdf = Context.getDateFormat();
		sdf.setLenient(false);
		Collections.sort(tb03us);
		
		String report = "";
		//report += "<h4>" + getMessage("mdrtb.pulmonary") + "</h4>";
		report += "<table border=\"1\">";
		report += "<tr>";
		report += "<td align=\"left\">" + getMessage("mdrtb.serialNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03uRegistrationNumber") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03uDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.name") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.gender") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.dateOfBirth") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.ageAtRegistration") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.xpert") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain1") + "</td>";
		report += "<td align=\"center\" colspan=\"3\">" + getMessage("mdrtb.hain2") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.culture") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.drugResistance") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.resistantTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.sensitiveTo") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.hivStatus") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03u.changeOfRegimen") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.tb03.treatmentStartDate") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.outcome") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.endOfTreatmentDate") + "</td>";
		
		report += "</tr>";
		
		report += "<tr>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.inhShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.rifShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.result") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.injectablesShort") + "</td>";
		report += "<td align=\"left\">" + getMessage("mdrtb.lists.quinShort") + "</td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		report += "<td></td>";
		
		report += "</tr>";
		
		RegimenForm rf = null;
		int curedId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED).getConceptId().intValue();
		int txCompId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_COMPLETE).getConceptId()
		        .intValue();
		int i = 0;
		Person p = null;
		for (TB03uForm tuf : tb03us) {
			
			rf = null;
			if (tuf.getPatient() == null || tuf.getPatient().getVoided())
				continue;
			
			if (tuf.getTreatmentOutcome() == null
			        || (tuf.getTreatmentOutcome().getConceptId().intValue() != curedId && tuf.getTreatmentOutcome()
			                .getConceptId().intValue() != txCompId))
				continue;
			
			//tuf = Context.getService(MdrtbService.class).getTB03uFormForProgram(tf.getPatient(), tf.getPatientProgramId());
			
			i++;
			p = Context.getPersonService().getPerson(tuf.getPatient().getId());
			report += "<tr>";
			report += "<td align=\"left\">" + i + "</td>";
			report += "<td align=\"left\">" + getRegistrationNumber(tuf) + "</td>";
			report += "<td align=\"left\">" + sdf.format(tuf.getEncounterDatetime()) + "</td>";
			report += "<td align=\"left\">" + p.getFamilyName() + "," + p.getGivenName() + "</td>";
			report += "<td align=\"left\">" + getGender(p) + "</td>";
			report += "<td align=\"left\">" + sdf.format(p.getBirthdate()) + "</td>";
			report += "<td align=\"left\">" + tuf.getAgeAtMDRRegistration() + "</td>";
			
			//XPERT
			List<XpertForm> xperts = tuf.getXperts();
			if (xperts != null && xperts.size() != 0) {
				Collections.sort(xperts);
				
				XpertForm dx = xperts.get(0);
				Concept mtb = dx.getMtbResult();
				Concept res = dx.getRifResult();
				
				if (mtb == null) {
					report += "<td></td>";
				}
				
				else {
					if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.POSITIVE).getConceptId().intValue()
					        || mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					                .getConcept(MdrtbConcepts.MTB_POSITIVE).getConceptId().intValue()) {
						String xr = getMessage("mdrtb.positiveShort");
						
						if (res != null) {
							int resId = res.getConceptId().intValue();
							
							if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.resistantShort");
								report += "<td align=\"left\">" + xr + "</td>";
							}
							
							else if (resId == Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NOT_DETECTED)
							        .getConceptId().intValue()) {
								xr += "/" + getMessage("mdrtb.sensitiveShort");
								report += "<td align=\"left\">" + xr + "</td>";
							}
							
							else {
								report += "<td align=\"left\">" + xr + "</td>";
							}
						}
						
						else {
							report += "<td align=\"left\">" + xr + "</td>";
						}
					}
					
					else if (mtb.getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.MTB_NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					}
					
					else {
						report += "<td></td>";
					}
				}
				
			}
			
			else {
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (rh != null) {
					report += "<td align=\"left\">" + rh.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
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
					report += "<td align=\"left\">" + res.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (ih != null) {
					report += "<td align=\"left\">" + ih.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
				if (fq != null) {
					report += "<td align=\"left\">" + fq.getName().getName() + "</td>";
				} else {
					report += "<td></td>";
				}
				
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
				report += "<td></td>";
			}
			
			//CULTURE
			List<CultureForm> cultures = tuf.getCultures();
			if (cultures != null && cultures.size() != 0) {
				Collections.sort(cultures);
				
				CultureForm dc = cultures.get(0);
				
				if (dc.getCultureResult() != null) {
					
					if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.NEGATIVE).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.negativeShort") + "</td>";
					}
					
					else if (dc.getCultureResult().getConceptId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.CULTURE_GROWTH).getConceptId().intValue()) {
						report += "<td align=\"left\">" + getMessage("mdrtb.lists.growth") + "</td>";
					}
					
					else {
						Integer[] concs = MdrtbUtil.getPositiveResultConceptIds();
						for (int index = 0; index < concs.length; index++) {
							if (concs[index].intValue() == dc.getCultureResult().getConceptId().intValue()) {
								report += "<td align=\"left\">" + getMessage("mdrtb.positiveShort") + "</td>";
								break;
							}
							
						}
					}
				}
				
				else {
					report += "<td></td>";
				}
			}
			
			else {
				report += "<td></td>";
			}
			
			//Drug Resistance
			if (tuf.getResistanceType() != null) {
				report += "<td align=\"left\">" + tuf.getResistanceType().getName().getName() + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			report += "<td align=\"left\">" + getResistantDrugs(tuf) + "</td>";
			report += "<td align=\"left\">" + getSensitiveDrugs(tuf) + "</td>";
			
			if (tuf.getHivStatus() != null) {
				report += "<td align=\"left\">" + tuf.getHivStatus().getName().getName() + "</td>";
			}
			
			else {
				report += "<td></td>";
			}
			
			if (tuf.getPatientCategory() != null)
				report += "<td align=\"left\">" + tuf.getPatientCategory().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tuf.getMdrTreatmentStartDate() != null)
				report += "<td align=\"left\">" + sdf.format(tuf.getMdrTreatmentStartDate()) + "</td>";
			else
				report += "<td></td>";
			
			rf = getFirstRegimenChangeForPatient(tuf.getPatient(), tuf.getPatientProgramId());
			
			if (rf != null) {
				if (rf.getSldRegimenType() != null) {
					report += "<td align=\"left\">" + rf.getSldRegimenType().getName().getName() + "</td>";
				}
				
				else {
					report += "<td></td>";
				}
				
				if (rf.getCouncilDate() != null) {
					report += "<td align=\"left\">" + sdf.format(rf.getCouncilDate()) + "</td>";
				}
				
				else {
					report += "<td></td>";
				}
			}
			
			else {
				report += "<td></td>";
				report += "<td></td>";
			}
			
			if (tuf.getTreatmentOutcome() != null)
				report += "<td align=\"left\">" + tuf.getTreatmentOutcome().getName().getName() + "</td>";
			else
				report += "<td></td>";
			
			if (tuf.getTreatmentOutcomeDate() != null)
				report += "<td align=\"left\">" + sdf.format(tuf.getTreatmentOutcomeDate()) + "</td>";
			else
				report += "<td></td>";
			
			report += "<td align=\"left\">" + getPatientLink(tuf) + "</td>";
			report += "</tr>";
			
		}
		
		report += "</table>";
		report += getMessage("mdrtb.numberOfRecords") + ": " + i;
		return report;
	}
	
	////////////////////// UTILITY FUNCTIONS //////////////////////////
	
	private static String renderPerson(Person p, boolean gender) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		String ret = "";
		ret += "<td align=\"left\">" + p.getFamilyName() + "," + p.getGivenName() + "</td>";
		
		if (gender) {
			String g = p.getGender().equals("M") ? Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.male")
			        : Context.getMessageSourceService().getMessage("mdrtb.tb03.gender.female");
			ret += "<td align=\"left\">" + g + "</td>";
		}
		
		ret += "<td align=\"left\">" + dateFormat.format(p.getBirthdate()) + "</td>";
		
		return ret;
		
	}
	
	private static String renderDate(Date date) {
		if (date == null)
			return "";
		
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		
		return dateFormat.format(date);
	}
	
	private static String getRegistrationNumber(TB03Form form) {
		String val = "";
		val = form.getRegistrationNumber();
		if (val == null || val.length() == 0) {
			val = getMessage("mdrtb.unassigned");
		}
		return val;
	}
	
	private static String getRegistrationNumber(TB03uForm form) {
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
				pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
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
	
	public static String getPatientLink(TB03Form form) {
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public static String getPatientLink(TB03uForm form) {
		
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public static String getPatientLink(Form89 form) {
		
		String link = null;
		link = "../program/enrollment.form?patientId=" + form.getPatient().getId();
		link = "<a href=\"" + link + "\" target=\"_blank\">" + getMessage("mdrtb.view") + "</a>";
		return link;
	}
	
	public static String getGender(Person p) {
		String gender = p.getGender();
		return gender.equals("F") ? getMessage("mdrtb.tb03.gender.female") : getMessage("mdrtb.tb03.gender.male");
	}
	
	public static String getTransferFrom(TB03Form tf) {
		TransferInForm tif = getTransferInForm(tf);
		if (tif != null) {
			return tif.getLocation().toString();
		}
		return "";
	}
	
	public static String getTransferFromDate(TB03Form tf) {
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
	
	public static TransferInForm getTransferInForm(TB03Form tf) {
		TransferInForm tif = null;
		
		Integer ppid = tf.getPatientProgramId();
		TbPatientProgram tpp = Context.getService(MdrtbService.class).getTbPatientProgram(ppid);
		tpp.getDateEnrolled();
		Date endDate = tpp.getDateCompleted();
		
		List<TransferInForm> allTifs = Context.getService(MdrtbService.class).getTransferInFormsFilledForPatient(
		    tf.getPatient());
		
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
	
	public static String getSiteOfDisease(TB03Form tf) {
		
		if (tf.getAnatomicalSite() != null) {
			return tf.getAnatomicalSite().getName().getName();
		}
		
		else {
			return "";
		}
		
	}
	
	public static String getRegistrationGroup(TB03Form tf) {
		if (tf.getRegistrationGroup() != null) {
			return tf.getRegistrationGroup().getName().getName();
		}
		return "";
	}
	
	public static String getResistantDrugs(TB03Form tf) {
		String drugs = "";
		List<DSTForm> dsts = tf.getDsts();
		if (dsts == null || dsts.size() == 0) {
			drugs = "";
		} else {
			DSTForm latest = dsts.get(dsts.size() - 1);
			DstImpl dst = latest.getDi();
			return dst.getResistantDrugs();
		}
		return drugs;
	}
	
	public static String getSensitiveDrugs(TB03Form tf) {
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
	
	public static String getResistantDrugs(TB03uForm tf) {
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
	
	public static String getSensitiveDrugs(TB03uForm tf) {
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
	
	public static String getReRegistrationNumber(TB03Form tf) {
		String ret = "";
		Integer ppid = tf.getPatientProgramId();
		if (ppid == null)
			return ret;
		Patient p = tf.getPatient();
		MdrtbService ms = Context.getService(MdrtbService.class);
		List<TbPatientProgram> tpps = ms.getTbPatientPrograms(p);
		if (tpps == null || tpps.size() <= 1) {
			return ret;
		}
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
	
	private static RegimenForm getFirstRegimenChangeForPatient(Patient p, Integer patientProgramId) {
		
		p.getPatientId().intValue();
		List<RegimenForm> forms = Context.getService(MdrtbService.class).getRegimenFormsForProgram(p, patientProgramId);
		
		if (forms == null)
			return null;
		
		if (forms.size() >= 2) {
			return forms.get(1);
		}
		return null;
	}
	
	private static String getMessage(String code) {
		return Context.getMessageSourceService().getMessage(code);
	}
}
