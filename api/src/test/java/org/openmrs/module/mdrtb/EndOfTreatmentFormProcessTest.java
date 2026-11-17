package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;

/**
 * Seed data (MdrtbTestService-initialData.xml): hermione MDR-TB 10003 TB03u with outcome date
 * 2023-06-30; hermione DOTS 10004 TB03 with outcome date 2023-02-28; hermione DOTS 10005 TB03
 * without outcome date; harry MDR-TB 10001 / DOTS 10002 without TB03u / TB03.
 */
public class EndOfTreatmentFormProcessTest extends MdrtbTestBase {
	
	private MdrtbFormServiceImpl formService;
	
	private MdrtbService service;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		formService = new MdrtbFormServiceImpl();
		service = Context.getService(MdrtbService.class);
	}
	
	private static Date date(String yyyyMMdd) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd);
	}
	
	private static Date tomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
	
	private EndOfTreatmentForm newForm(Patient patient, Integer patientProgramId) throws Exception {
		Encounter encounter = new Encounter();
		encounter.setEncounterType(MdrtbConstants.ET_END_OF_TREATMENT);
		encounter.setPatient(patient);
		encounter.setLocation(hogwarts);
		encounter.setEncounterDatetime(date("2023-09-01"));
		EndOfTreatmentForm form = new EndOfTreatmentForm(encounter);
		if (patientProgramId != null) {
			form.setPatientProgramId(patientProgramId);
		}
		return form;
	}
	
	private EndOfTreatmentForm newHermioneDotsForm() throws Exception {
		return newForm(hermione, hermioneDotsProgram.getPatientProgramId());
	}
	
	private void addXrayGroup(EndOfTreatmentForm form, int month, Date date) {
		Encounter encounter = form.getEncounter();
		Obs group = new Obs(encounter.getPatient(), service.getConcept(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT),
		        encounter.getEncounterDatetime(), encounter.getLocation());
		encounter.addObs(group);
		Obs monthObs = new Obs(encounter.getPatient(), service.getConcept(MdrtbConcepts.MONTH_OF_TREATMENT),
		        encounter.getEncounterDatetime(), encounter.getLocation());
		monthObs.setValueNumeric((double) month);
		group.addGroupMember(monthObs);
		encounter.addObs(monthObs);
		Obs dateObs = new Obs(encounter.getPatient(), service.getConcept(MdrtbConcepts.XRAY_DATE),
		        encounter.getEncounterDatetime(), encounter.getLocation());
		dateObs.setValueDatetime(date);
		group.addGroupMember(dateObs);
		encounter.addObs(dateObs);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenEncounterHasNoLocation() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.getEncounter().setLocation(null);
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenPatientProgramIsNotAttached() throws Exception {
		formService.processEndOfTreatmentForm(newForm(harry, null));
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenPatientProgramDoesNotExist() throws Exception {
		formService.processEndOfTreatmentForm(newForm(harry, 999999));
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenPatientProgramBelongsToAnotherPatient() throws Exception {
		formService.processEndOfTreatmentForm(newForm(harry, hermioneMdrProgram.getPatientProgramId()));
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailForDotsProgramWithoutTB03() throws Exception {
		formService.processEndOfTreatmentForm(newForm(harry, harryDotsProgram.getPatientProgramId()));
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailForMdrtbProgramWithoutTB03u() throws Exception {
		formService.processEndOfTreatmentForm(newForm(harry, harryMdrProgram.getPatientProgramId()));
	}
	
	@Test
	public void process_shouldSaveForDotsProgramWithTB03() throws Exception {
		EndOfTreatmentForm form = formService.processEndOfTreatmentForm(newHermioneDotsForm());
		assertNotNull(form.getEncounter().getEncounterId());
		assertEquals(1, service.getEndOfTreatmentFormsForProgram(hermione, hermioneDotsProgram.getPatientProgramId()).size());
	}
	
	@Test
	public void process_shouldSaveForMdrtbProgramWithTB03u() throws Exception {
		EndOfTreatmentForm form = formService.processEndOfTreatmentForm(newForm(hermione,
		    hermioneMdrProgram.getPatientProgramId()));
		assertNotNull(form.getEncounter().getEncounterId());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenProgramAlreadyHasAnotherEndOfTreatmentForm() throws Exception {
		formService.processEndOfTreatmentForm(newHermioneDotsForm());
		formService.processEndOfTreatmentForm(newHermioneDotsForm());
	}
	
	@Test
	public void process_shouldAllowEditingTheExistingEndOfTreatmentForm() throws Exception {
		EndOfTreatmentForm form = formService.processEndOfTreatmentForm(newHermioneDotsForm());
		form.setDispensaryGroup(service.getConcept(MdrtbConcepts.DISPENSARY_GROUP_IA));
		formService.processEndOfTreatmentForm(form);
		assertEquals(service.getConcept(MdrtbConcepts.DISPENSARY_GROUP_IA), form.getDispensaryGroup());
	}
	
	@Test
	public void process_shouldNotChangePatientProgramOrPatient() throws Exception {
		Date dateCompleted = hermioneDotsProgram.getDateCompleted();
		formService.processEndOfTreatmentForm(newHermioneDotsForm());
		PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(
		    hermioneDotsProgram.getPatientProgramId());
		assertEquals(dateCompleted, program.getDateCompleted());
		assertFalse(Context.getPatientService().getPatient(hermione.getPatientId()).getDead());
	}
	
	@Test
	public void process_shouldSaveXraysOfDifferentMonths() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		for (int month = 1; month <= 7; month++) {
			form.setFollowupXray(month, date("2023-03-01"), service.getConcept(MdrtbConcepts.XRAY_NORMAL));
		}
		formService.processEndOfTreatmentForm(form);
		assertEquals(7, form.getFollowupXrays().size());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenFollowupXrayMonthIsRepeated() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		addXrayGroup(form, 6, date("2023-09-01"));
		addXrayGroup(form, 6, date("2023-09-02"));
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenFollowupXrayIsOnTheDayOfEndOfTreatment() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setFollowupXray(1, date("2023-02-28"), null); // TB03 outcome date
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenFollowupXrayIsBeforeEndOfTreatment() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setFollowupXray(1, date("2023-01-15"), null);
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test
	public void process_shouldSaveFollowupXrayOnTheDayAfterEndOfTreatment() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setFollowupXray(1, date("2023-03-01"), null);
		formService.processEndOfTreatmentForm(form);
		assertNotNull(form.getEncounter().getEncounterId());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldUseTheTB03uOutcomeDateForMdrtbProgram() throws Exception {
		// hermione's TB03u outcome date is 2023-06-30: an X-ray in March is before it
		EndOfTreatmentForm form = newForm(hermione, hermioneMdrProgram.getPatientProgramId());
		form.setFollowupXray(1, date("2023-03-01"), null);
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test
	public void process_shouldSaveWithoutCheckingXrayDatesWhenTB03HasNoOutcomeDate() throws Exception {
		EndOfTreatmentForm form = newForm(hermione, 10005);
		form.setFollowupXray(1, date("2020-01-01"), null);
		formService.processEndOfTreatmentForm(form);
		assertNotNull(form.getEncounter().getEncounterId());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenGroup3DateIsOnTheGroup2Date() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup2Date(date("2023-10-01"));
		form.setTransferToGroup3Date(date("2023-10-01"));
		form.setTransferToGroup3Reason(service.getConcept(MdrtbConcepts.DISABILITY));
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenGroup3DateIsBeforeGroup2Date() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup2Date(date("2023-10-01"));
		form.setTransferToGroup3Date(date("2023-09-01"));
		form.setTransferToGroup3Reason(service.getConcept(MdrtbConcepts.DISABILITY));
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test
	public void process_shouldSaveGroup3AfterGroup2WithReason() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup2Date(date("2023-10-01"));
		form.setTransferToGroup3Date(date("2024-10-01"));
		form.setTransferToGroup3Reason(service.getConcept(MdrtbConcepts.MAJOR_RESIDUAL_CHANGES));
		formService.processEndOfTreatmentForm(form);
		assertEquals(service.getConcept(MdrtbConcepts.MAJOR_RESIDUAL_CHANGES), form.getTransferToGroup3Reason());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenGroup3DateHasNoReason() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup3Date(date("2024-10-01"));
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test
	public void process_shouldClearGroup3ReasonWhenThereIsNoGroup3Date() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup3Reason(service.getConcept(MdrtbConcepts.TB_HIV));
		formService.processEndOfTreatmentForm(form);
		assertNull(form.getTransferToGroup3Reason());
	}
	
	@Test
	public void process_shouldAcceptRemovalDateWithoutReason() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setRemovalFromObservationDate(date("2025-01-15"));
		formService.processEndOfTreatmentForm(form);
		assertEquals(date("2025-01-15"), form.getRemovalFromObservationDate());
		assertNull(form.getRemovalFromObservationReason());
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenXrayDateIsInTheFuture() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setFollowupXray(1, tomorrow(), null);
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenGroup2DateIsInTheFuture() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setTransferToGroup2Date(tomorrow());
		formService.processEndOfTreatmentForm(form);
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void process_shouldFailWhenRemovalDateIsInTheFuture() throws Exception {
		EndOfTreatmentForm form = newHermioneDotsForm();
		form.setRemovalFromObservationDate(tomorrow());
		formService.processEndOfTreatmentForm(form);
	}
}
