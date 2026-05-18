package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;

public class MdrtbServiceTest extends MdrtbTestBase {
	
	MdrtbService service;
	
	LocalDate startDate = new LocalDate(2022, 8, 1);
	
	LocalDate endDate = new LocalDate(2022, 8, 31);
	
	LocalDate now = new LocalDate();
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
	}
	
	@Test
	public void testMdrtbService() {
		assertNotNull(service);
	}
	
	@Test
	public final void testFindMatchingConceptWithExactString() {
		Concept concept = service.getConcept("DATE OF MDR TREATMENT START");
		assertNotNull(concept);
	}
	
	@Test
	public final void testGetAllMdrtbPatientProgramsInDateRange() {
		List<MdrtbPatientProgram> list = service.getAllMdrtbPatientProgramsEnrolledInDateRange(startDate.toDate(),
		    endDate.toDate());
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}
	
	@Test
	public final void testGetMdrtbPatientPrograms() {
		List<MdrtbPatientProgram> list = service.getMdrtbPatientPrograms(harry);
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}
	
	@Test
	public final void testGetMostRecentMdrtbPatientProgram() {
		MdrtbPatientProgram program = service.getMostRecentMdrtbPatientProgram(harry);
		assertTrue(program.getId() == new MdrtbPatientProgram(harryMdrProgram).getId());
	}
	
	@Test
	public final void testGetMdrtbPatientProgramsInDateRange() {
		List<MdrtbPatientProgram> list = service.getMdrtbPatientProgramsInDateRange(harry, startDate.toDate(),
		    endDate.toDate());
		assertTrue(list.contains(new MdrtbPatientProgram(harryMdrProgram)));
	}
	
	@Test
	@Ignore
	public final void testGetMdrtbPatientProgramOnDate() {
		MdrtbPatientProgram program = service.getMdrtbPatientProgramOnDate(harry, startDate.toDate());
		assertTrue(program.equals(new MdrtbPatientProgram(harryMdrProgram)));
	}
	
	@Test
	public final void testGetMdrtbPatientProgram() {
		MdrtbPatientProgram program = service.getMdrtbPatientProgram(10001);
		assertTrue(program.equals(new MdrtbPatientProgram(harryMdrProgram)));
	}
	
	@Test
	public final void testCreateSpecimen() {
		Specimen specimen = service.createSpecimen(harry);
		assertNotNull(specimen);
	}
	
	@Test
	@Ignore
	public final void testGetSpecimensPatientDateDateLocation() {
		List<Specimen> list = service.getSpecimens(harry, startDate.toDate(), endDate.toDate(), hogwarts);
		assertTrue(list.contains(new SpecimenImpl(harrySpecimenEncounter)));
	}
	
	@Test
	public final void testSaveSpecimen() {
		now = new LocalDate();
		Encounter encounter = new Encounter();
		encounter.setPatient(hermione);
		encounter.setEncounterDatetime(now.toDate());
		encounter.setEncounterType(specimen);
		Specimen specimenEncounter = new SpecimenImpl(encounter);
		service.saveSpecimen(specimenEncounter);
		List<Encounter> list = Context.getEncounterService().getEncountersByPatient(hermione);
		boolean exists = false;
		for (Encounter e : list) {
			if (e.getEncounterType().equals(specimen) && e.getEncounterDatetime().equals(now.toDate())) {
				exists = true;
			}
		}
		assertTrue(exists);
	}
	
	@Test
	@Ignore
	public final void shouldNOTDeleteTest() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testProcessDeath() {
		Concept causeOfDeath = Context.getConceptService().getConcept(27);
		service.processDeath(harry, now.toDate(), causeOfDeath);
		MdrtbPatientProgram mdrtbPatientProgram = new MdrtbPatientProgram(Context.getProgramWorkflowService()
		        .getPatientProgram(harryMdrProgram.getId()));
		
		Concept died = Context.getConceptService().getConcept(71);
		assertEquals(died, mdrtbPatientProgram.getOutcome().getConcept());
		
		assertFalse(mdrtbPatientProgram.getActive());
		
	}
	
	@Test
	public final void testGetColorForConcept() {
		Concept contaminated = Context.getConceptService().getConcept(47);
		String color = service.getColorForConcept(contaminated);
		assertEquals(color, "lightgrey");
	}
	
	@Test
	@Ignore
	public final void testGetAllMdrtbPatientProgramsEnrolledInDateRange() {
		fail("Not yet implemented");
	}
	
	@Test
	public final void testGetGetXpert() {
		Obs obs = new Obs(harry.getPerson(), Context.getConceptService().getConcept(MdrtbConcepts.XPERT_CONSTRUCT),
		        new Date(), hogwarts);
		Context.getObsService().saveObs(obs, null);
		assertNotNull(service.getXpert(obs.getObsId()));
	}
	
	@Test(expected = RuntimeException.class)
	public final void testThrowExceptionGetXpert() {
		Integer obsId = -1;
		service.getXpert(obsId);
	}
	
	@Test
	public final void testGetHAIN() {
		Obs obs = new Obs(harry.getPerson(), Context.getConceptService().getConcept(MdrtbConcepts.HAIN_CONSTRUCT),
		        new Date(), hogwarts);
		Context.getObsService().saveObs(obs, null);
		assertNotNull(service.getHAIN(obs));
	}
	
	@Test(expected = RuntimeException.class)
	public final void testThrowExceptionGetHAIN() {
		service.getHAIN(null);
	}
	
	@Test
	public final void testGetHAIN2() {
		Obs obs = new Obs(harry.getPerson(), Context.getConceptService().getConcept(MdrtbConcepts.HAIN2_CONSTRUCT),
		        new Date(), hogwarts);
		Context.getObsService().saveObs(obs, null);
		assertNotNull(service.getHAIN2(obs));
	}
	
	@Test(expected = RuntimeException.class)
	public final void testThrowExceptionGetHAIN2() {
		service.getHAIN2(null);
	}
	
	@Test
	@Ignore
	public final void testSaveXpert() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testCreateXpert() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testSaveHAIN() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testCreateHAIN() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testCreateHAIN2() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testSaveHAIN2() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetEnrollmentLocations() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPatientProgramIdentifier() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetGenPatientProgramIdentifier() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsEnrolledInDateRange() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testAddIdentifierToProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleIPTreatmentSites() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleCPTreatmentSites() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleRegimens() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleHIVStatuses() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleResistanceTypes() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleConceptAnswers() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testCountPDFRows() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testCountPDFColumns() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testPDFRows() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testPDFColumns() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testUnlockReport() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testDoPDF() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testReadReportStatus() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testReadTableData() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetEncountersByEncounterTypesListOfString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetEncountersByEncounterTypesListOfStringDateDateDate() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetSmearForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetCultureForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetXpertForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetHAINForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetHAIN2Forms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetDstForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetDrdtForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetEncountersWithNoProgramId() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testAddProgramIdToEncounter() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03FormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03FormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetForm89FormsFilledLocationStringIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetForm89FormsFilledArrayListOfLocationIntegerStringString() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetForm89FormsFilledForPatientProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTransferOutFormsFilled() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTransferInFormsFilled() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTransferOutFormsFilledForPatient() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTransferInFormsFilledForPatient() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPossibleDOTSClassificationsAccordingToPreviousDrugUse() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetClosestTB03Form() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetCultureLocations() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetLocationList() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPatientIdentifierById() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03uFormsFilledWithTxStartDateDuring() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03FormsForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetForm89FormsForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testEvict() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03uFormForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegimenFormsForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegimenFormsFilled() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllPatientsWithRegimenForms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetPreviousRegimenFormForPatient() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetCurrentRegimenFormForPatient() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAEFormsFilled() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAEFormsForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsEnrolledInDateRangeAndLocations() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTB03uFormsForProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTbEncounters() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllTbPatientPrograms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetAllTbPatientProgramsInDateRange() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTbPatientPrograms() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetMostRecentTbPatientProgram() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTbPatientProgramsInDateRange() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTbPatientProgramOnDate() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetTbPatientProgram() {
		fail("Not yet implemented");
	}
}
