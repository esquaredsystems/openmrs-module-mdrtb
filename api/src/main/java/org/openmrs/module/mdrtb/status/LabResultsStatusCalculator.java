package org.openmrs.module.mdrtb.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;

public class LabResultsStatusCalculator {
	
	protected static final Log log = LogFactory.getLog(LabResultsStatusCalculator.class);
	
	// TODO: if there are performance issues, we can combine some of the calculation
	// methods, to avoid iterating over specimens multiple times	
	private LabResultsStatusRenderer renderer;
	
	public LabResultsStatusCalculator(LabResultsStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
	@SuppressWarnings("unchecked")
	public Status calculate(MdrtbPatientProgram mdrtbProgram) {
		
		// create the Status
		LabResultsStatus status = new LabResultsStatus(mdrtbProgram);
		
		// get the specimens for this patient program, because these will be used for
		// multiple calculations
		List<Specimen> specimens = mdrtbProgram.getSpecimensDuringProgram();
		
		// just create an empty list of specimens if no specimens during the program
		if (specimens == null) {
			specimens = new LinkedList<Specimen>();
		}
		
		// get the control smear and diagnostic culture
		findDiagnosticSmearAndCulture(specimens, status);
		
		// determine any pending lab results
		findPendingLabResults(specimens, status);
		
		// determine the resistance profile
		StatusItem resistanceProfile = calculateResistanceProfile(specimens);
		status.addItem("drugResistanceProfile", resistanceProfile);
		
		// now use the resistance profile to determine the mdr-tb classification
		status.addItem("tbClassification", calculateTbClassication((List<Concept>) resistanceProfile.getValue()));
		
		// we want to to reverse the order of the specimens here so that first=most
		// recent
		// NOTE: the find most recent smear/culture and the calculate conversions
		// methods
		// both rely on the specimens being in reverse order
		Collections.reverse(specimens);
		
		// find the most recent smear and culture
		findMostRecentSmear(specimens, status);
		findMostRecentCulture(specimens, status);
		findMostRecentXpert(specimens, status);
		findMostRecentHAIN(specimens, status);
		findMostRecentHAIN2(specimens, status);
		findMostRecentDst(specimens, status);
		
		// calculate whether or not the culture has been converted
		status.addItem("smearConversion", calculateConversion(specimens, "smear"));
		status.addItem("cultureConversion", calculateConversion(specimens, "culture"));
		
		// figure out the anatomical site, if know
		status.addItem("anatomicalSite", findAnatomicalSite(mdrtbProgram));
		
		return status;
		
	}
	
	public void setRenderer(LabResultsStatusRenderer renderer) {
		this.renderer = renderer;
	}
	
	public LabResultsStatusRenderer getRenderer() {
		return renderer;
	}
	
	/**
	 * Utility methods
	 */
	
	private StatusItem calculateResistanceProfile(List<Specimen> specimens) {
		StatusItem resistanceProfile = new StatusItem();
		
		List<Concept> drugs = new LinkedList<Concept>();
		
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		
		if (specimens != null) {
			for (Specimen specimen : specimens) {
				for (Dst dst : specimen.getDsts()) {
					for (DstImpl result : dst.getResults()) {
						if (resistant.equals(result.getResult())) {
							if (!drugs.contains(result.getDrug())) {
								drugs.add(result.getDrug());
							}
						}
					}
				}
			}
		}
		
		// sort the drugs in the standard order
		drugs = MdrtbUtil.sortMdrtbDrugs(drugs);
		
		resistanceProfile.setValue(drugs);
		resistanceProfile.setDisplayString(renderer.renderDrugResistanceProfile(drugs));
		
		return resistanceProfile;
	}
	
	// this calculation is based upon guidelines in the WHO publication "Guidelines
	// for the
	// programmatic management of drug-resistant tuberculosis, Emergency update
	// 2008" (see section 4.2, p.20)
	private StatusItem calculateTbClassication(List<Concept> resistanceProfile) {
		
		MdrtbService mdrtbService = Context.getService(MdrtbService.class);
		
		StatusItem tbClassification = new StatusItem();
		TbClassification classification = null;
		
		// if there are no drugs in the resistance profile, we can't make any
		// classications yet
		if (resistanceProfile.size() > 0) {
			if (resistanceProfile.size() == 1) {
				// if there's only one, it's mono resistance
				classification = TbClassification.MONO_RESISTANT_TB;
			} else {
				// if patient is resistance to 2 or more drugs, patient is at least
				// poly-resistant
				classification = TbClassification.POLY_RESISTANT_TB;
				
				// if they are resistant to isoniazid and rifampicin, at least MDR-TB
				if (resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.ISONIAZID))
				        && resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.RIFAMPICIN))) {
					classification = TbClassification.MDR_TB;
					
					// if resistant to capreomycin, kanamycin, or amikacin, and any one
					// fluorquinolone, the patient is XDR
					if ((resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.CAPREOMYCIN))
					        || resistanceProfile.contains(mdrtbService.getConcept(MdrtbConcepts.KANAMYCIN)) || resistanceProfile
					            .contains(mdrtbService.getConcept(MdrtbConcepts.AMIKACIN)))) {
						
						for (ConceptSet fluoroquinolone : mdrtbService.getConcept(MdrtbConcepts.QUINOLONES).getConceptSets()) {
							if (resistanceProfile.contains(fluoroquinolone.getConcept())) {
								classification = TbClassification.XDR_TB;
							}
						}
					}
				}
			}
		}
		
		tbClassification.setValue(classification);
		tbClassification.setDisplayString(renderer.renderTbClassification(classification));
		
		return tbClassification;
	}
	
	// defined as two consecutive negative smears/cultures more than 30 days apart
	// (with no positives in the meantime)
	// note that this method expects to work on a specimen list in reverse
	// chronological order
	private StatusItem calculateConversion(List<Specimen> specimens, String type) {
		
		// get a set of all concepts that represent positive results
		Set<Concept> positiveResults = MdrtbUtil.getPositiveResultConcepts();
		
		StatusItem conversion = new StatusItem();
		List<Date> negativeDates = new LinkedList<Date>();
		
		// loop through all the specimens until we hit one with a positive smear or
		// culture
		for (Specimen specimen : specimens) {
			Boolean possibleConversion;
			
			if (type.matches("smear")) {
				possibleConversion = calculateSmearConversionHelper(specimen, positiveResults);
			} else if (type.matches("culture")) {
				possibleConversion = calculateCultureConversionHelper(specimen, positiveResults);
			} else {
				throw new MdrtbAPIException("Can't calculate conversion on invalid test type " + type);
			}
			
			if (possibleConversion != null) {
				if (!possibleConversion) {
					break;
				} else if (possibleConversion) {
					negativeDates.add(specimen.getDateCollected());
				}
			}
		}
		
		// there need to be at least two negative cultures for this to be a conversion
		if (negativeDates.size() > 1) {
			
			// now compare to make sure that the conversions are at least 30 days apart
			Calendar lastNegative = Calendar.getInstance();
			Calendar firstNegative = Calendar.getInstance();
			
			lastNegative.setTime(negativeDates.get(0));
			firstNegative.setTime(negativeDates.get(negativeDates.size() - 1));
			
			firstNegative.add(Calendar.DAY_OF_MONTH, 29);
			
			if (firstNegative.before(lastNegative)) {
				// we have a successful conversion
				conversion.setValue(new Boolean(true));
				
				// determine what the conversion date should be reported as
				Collections.sort(negativeDates, Collections.reverseOrder());
				for (Date date : negativeDates) {
					if (date.after(firstNegative.getTime())) {
						conversion.setDate(date);
					}
				}
				conversion.setDisplayString(renderer.renderConversion(conversion));
				return conversion;
			}
		}
		
		// if we've got here, not converted
		conversion.setValue(new Boolean(false));
		conversion.setDisplayString(renderer.renderConversion(conversion));
		
		return conversion;
	}
	
	// if this specimen has any positive smears, returns null
	// if not, and it has a negative smear, return true
	private Boolean calculateSmearConversionHelper(Specimen specimen, Set<Concept> positiveResults) {
		
		Boolean result = null;
		
		for (Smear smear : specimen.getSmears()) {
			if (positiveResults.contains(smear.getResult())) {
				result = false;
				break;
			} else if (smear.getResult() != null
			        && smear.getResult().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE))) {
				result = true;
			}
		}
		
		// note that if there are no cultures for this specimen, this will return null
		return result;
	}
	
	// if this specimen has any positive cultures, returns null
	// if not, and it has a negative culture, return true
	private Boolean calculateCultureConversionHelper(Specimen specimen, Set<Concept> positiveResults) {
		
		Boolean result = null;
		
		for (Culture culture : specimen.getCultures()) {
			if (positiveResults.contains(culture.getResult())) {
				result = false;
				break;
			} else if (culture.getResult() != null
			        && culture.getResult().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE))) {
				result = true;
			}
		}
		
		// note that if there are no cultures for this specimen, this will return null
		return result;
	}
	
	private StatusItem findAnatomicalSite(MdrtbPatientProgram program) {
		StatusItem anatomicalSite = new StatusItem();
		
		anatomicalSite.setValue(program.getCurrentAnatomicalSiteDuringProgram());
		anatomicalSite.setDisplayString(renderer.renderAnatomicalSite(anatomicalSite));
		
		return anatomicalSite;
	}
	
	private void findPendingLabResults(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem pendingLabResults = new StatusItem();
		
		List<StatusItem> tests = new LinkedList<StatusItem>();
		
		if (specimens != null) {
			for (Specimen specimen : specimens) {
				for (Test test : specimen.getTests()) {
					if (test.getStatus() != TestStatus.COMPLETED) {
						// if a test does not have a status of complete, also verify that there are no
						// results before we decide to display it as "pending"
						if (test.getResult() == null || test instanceof Dst && ((Dst) test).getResults().size() == 0) {
							tests.add(new StatusItem(test));
						}
					}
				}
			}
		}
		
		pendingLabResults.setValue(tests);
		renderer.renderPendingLabResults(pendingLabResults, status);
		
		status.addItem("pendingLabResults", pendingLabResults);
	}
	
	// diagnostic smear and culture defined as first smear and culture results from
	// the specimens associated with the program
	private void findDiagnosticSmearAndCulture(List<Specimen> specimens, LabResultsStatus status) {
		
		Smear smear = findFirstCompletedSmearInList(specimens);
		StatusItem diagnosticSmear = new StatusItem();
		diagnosticSmear.setValue(smear);
		renderer.renderSmear(diagnosticSmear, status);
		
		Culture culture = findFirstCompletedCultureInList(specimens);
		StatusItem diagnosticCulture = new StatusItem();
		diagnosticCulture.setValue(culture);
		renderer.renderCulture(diagnosticCulture, status);
		
		Xpert xpert = findFirstCompletedXpertInList(specimens);
		StatusItem diagnosticXpert = new StatusItem();
		diagnosticXpert.setValue(xpert);
		renderer.renderXpert(diagnosticXpert, status);
		
		HAIN hain = findFirstCompletedHAINInList(specimens);
		StatusItem diagnosticHAIN = new StatusItem();
		diagnosticHAIN.setValue(hain);
		renderer.renderHAIN(diagnosticHAIN, status);
		
		status.addItem("diagnosticSmear", diagnosticSmear);
		status.addItem("diagnosticCulture", diagnosticCulture);
		status.addItem("diagnosticXpert", diagnosticXpert);
		status.addItem("diagnosticHAIN", diagnosticHAIN);
	}
	
	// note that this method expects the specimen list to be in reverse
	// chronilogical order
	private void findMostRecentSmear(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedSmear = new StatusItem();
		
		Smear smear = findFirstCompletedSmearInList(specimens);
		mostRecentCompletedSmear.setValue(smear);
		renderer.renderSmear(mostRecentCompletedSmear, status);
		
		status.addItem("mostRecentSmear", mostRecentCompletedSmear);
		
		/**
		 * if (smear == null) { mostRecentCompletedSmear.addFlag(renderer.createNoSmearsFlag()); }
		 */
		
	}
	
	// note that this method expects the specimen list to be in reverse
	// chronilogical order
	private void findMostRecentCulture(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedCulture = new StatusItem();
		
		Culture culture = findFirstCompletedCultureInList(specimens);
		mostRecentCompletedCulture.setValue(culture);
		renderer.renderCulture(mostRecentCompletedCulture, status);
		
		status.addItem("mostRecentCulture", mostRecentCompletedCulture);
		
		/**
		 * if (culture == null) {
		 * mostRecentCompletedCulture.addFlag(renderer.createNoCulturesFlag()); }
		 */
	}
	
	private Smear findFirstCompletedSmearInList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<Smear> smears = specimen.getSmears();
			
			if (smears != null && !smears.isEmpty()) {
				Collections.reverse(smears);
				for (Smear smear : smears) {
					if (smear.getResult() != null) {
						return smear;
					}
				}
			}
		}
		
		// if we've got here, no matching smear
		return null;
	}
	
	private Culture findFirstCompletedCultureInList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<Culture> cultures = specimen.getCultures();
			
			if (cultures != null && !cultures.isEmpty()) {
				Collections.reverse(cultures);
				for (Culture culture : cultures) {
					if (culture.getResult() != null) {
						return culture;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed culture for this patient
		return null;
	}
	
	/****** CUSTOM METHODS ******/
	@SuppressWarnings("unchecked")
	public Status calculateTb(TbPatientProgram tbProgram) {
		
		// create the Status
		LabResultsStatus status = new LabResultsStatus(tbProgram);
		
		// get the specimens for this patient program, because these will be used for
		// multiple calculations
		List<Specimen> specimens = tbProgram.getSpecimensDuringProgramObs();
		
		// just create an empty list of specimens if no specimens during the program
		if (specimens == null) {
			specimens = new LinkedList<Specimen>();
		}
		
		// get the control smear and diagnostic culture
		// findDiagnosticSmearAndCulture(specimens, status);
		// findDiagnosticTests(specimens, status);
		
		// determine any pending lab results
		findPendingLabResults(specimens, status);
		
		// determine the resistance profile
		StatusItem resistanceProfile = calculateResistanceProfile(specimens);
		status.addItem("drugResistanceProfile", resistanceProfile);
		
		// now use the resistance profile to determine the mdr-tb classification
		status.addItem("tbClassification", calculateTbClassication((List<Concept>) resistanceProfile.getValue()));
		
		// we want to to reverse the order of the specimens here so that first=most
		// recent
		// NOTE: the find most recent smear/culture and the calculate conversions
		// methods
		// both rely on the specimens being in reverse order
		Collections.reverse(specimens);
		
		// find the most recent smear and culture
		findMostRecentSmear(specimens, status);
		findMostRecentCulture(specimens, status);
		findMostRecentXpert(specimens, status);
		findMostRecentHAIN(specimens, status);
		findMostRecentHAIN2(specimens, status);
		findMostRecentDst(specimens, status);
		
		// calculate whether or not the culture has been converted
		status.addItem("smearConversion", calculateConversion(specimens, "smear"));
		status.addItem("cultureConversion", calculateConversion(specimens, "culture"));
		
		// figure out the anatomical site, if know
		status.addItem("anatomicalSite", findAnatomicalSiteTb(tbProgram));
		
		return status;
		
	}
	
	public StatusItem calculateMostRecentResistanceProfile(List<Specimen> specimens) {
		StatusItem resistanceProfile = new StatusItem();
		
		List<Concept> drugs = new LinkedList<Concept>();
		
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		
		if (specimens != null) {
			for (Specimen specimen : specimens) {
				for (Dst dst : specimen.getDsts()) {
					for (DstImpl result : dst.getResults()) {
						if (resistant.equals(result.getResult())) {
							if (!drugs.contains(result.getDrug())) {
								drugs.add(result.getDrug());
							}
						}
					}
				}
			}
		}
		
		// sort the drugs in the standard order
		drugs = MdrtbUtil.sortMdrtbDrugs(drugs);
		
		resistanceProfile.setValue(drugs);
		resistanceProfile.setDisplayString(renderer.renderDrugResistanceProfile(drugs));
		
		return resistanceProfile;
	}
	
	private StatusItem findAnatomicalSiteTb(TbPatientProgram program) {
		StatusItem anatomicalSite = new StatusItem();
		
		anatomicalSite.setValue(program.getCurrentAnatomicalSiteDuringProgram());
		anatomicalSite.setDisplayString(renderer.renderAnatomicalSite(anatomicalSite));
		
		return anatomicalSite;
	}
	
	private Xpert findFirstCompletedXpertInList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<Xpert> xperts = specimen.getXperts();
			
			if (xperts != null && !xperts.isEmpty()) {
				Collections.reverse(xperts);
				for (Xpert xpert : xperts) {
					if (xpert.getResult() != null) {
						return xpert;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed xpert for this patient
		return null;
	}
	
	private HAIN findFirstCompletedHAINInList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<HAIN> hains = specimen.getHAINs();
			
			if (hains != null && !hains.isEmpty()) {
				Collections.reverse(hains);
				for (HAIN hain : hains) {
					if (hain.getResult() != null) {
						return hain;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed hain for this patient
		return null;
	}
	
	private HAIN2 findFirstCompletedHAIN2InList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<HAIN2> hains = specimen.getHAIN2s();
			
			if (hains != null && !hains.isEmpty()) {
				Collections.reverse(hains);
				for (HAIN2 hain : hains) {
					if (hain.getResult() != null) {
						return hain;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed hain for this patient
		return null;
	}
	
	private Dst findFirstCompletedDstnList(List<Specimen> specimens) {
		
		if (specimens == null) {
			return null;
		}
		
		for (Specimen specimen : specimens) {
			List<Dst> dsts = specimen.getDsts();
			
			if (dsts != null && !dsts.isEmpty()) {
				Collections.reverse(dsts);
				for (Dst dst : dsts) {
					if (dst.getResults() != null) {
						log.debug("MOST RECENT DST: " + dst.getId());
						return dst;
					}
				}
			}
		}
		
		// if we've got to here, there is no completed Dst for this patient
		return null;
	}
	
	private void findMostRecentXpert(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedXpert = new StatusItem();
		
		Xpert xpert = findFirstCompletedXpertInList(specimens);
		mostRecentCompletedXpert.setValue(xpert);
		renderer.renderXpert(mostRecentCompletedXpert, status);
		
		status.addItem("mostRecentXpert", mostRecentCompletedXpert);
		
	}
	
	private void findMostRecentHAIN(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedHAIN = new StatusItem();
		
		HAIN hain = findFirstCompletedHAINInList(specimens);
		mostRecentCompletedHAIN.setValue(hain);
		renderer.renderHAIN(mostRecentCompletedHAIN, status);
		
		status.addItem("mostRecentHAIN", mostRecentCompletedHAIN);
		
	}
	
	private void findMostRecentHAIN2(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedHAIN2 = new StatusItem();
		
		HAIN2 hain2 = findFirstCompletedHAIN2InList(specimens);
		mostRecentCompletedHAIN2.setValue(hain2);
		renderer.renderHAIN2(mostRecentCompletedHAIN2, status);
		
		status.addItem("mostRecentHAIN2", mostRecentCompletedHAIN2);
		
	}
	
	private void findMostRecentDst(List<Specimen> specimens, LabResultsStatus status) {
		StatusItem mostRecentCompletedDst = new StatusItem();
		
		Dst dst = findFirstCompletedDstnList(specimens);
		mostRecentCompletedDst.setValue(dst);
		renderer.renderDst(mostRecentCompletedDst, status);
		
		status.addItem("mostRecentDst", mostRecentCompletedDst);
	}
	
}
