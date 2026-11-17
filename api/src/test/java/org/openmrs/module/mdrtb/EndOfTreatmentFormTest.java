package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm.FollowupXray;

/**
 * Tests for {@link EndOfTreatmentForm} getters and setters, on an encounter that is not saved. The
 * End of Treatment concepts are in the test datasets (test concept ids 828-845 without 834;
 * production 890-906 - the UUIDs match, not the ids).
 */
public class EndOfTreatmentFormTest extends MdrtbTestBase {
	
	private EndOfTreatmentForm form;
	
	private MdrtbService service;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
		Encounter encounter = new Encounter();
		encounter.setPatient(harry);
		encounter.setLocation(hogwarts);
		encounter.setEncounterDatetime(date("2023-09-01"));
		form = new EndOfTreatmentForm(encounter);
	}
	
	private static Date date(String yyyyMMdd) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd);
	}
	
	private Concept concept(String uuid) {
		Concept concept = service.getConcept(uuid);
		assertNotNull("concept missing from the test datasets: " + uuid, concept);
		return concept;
	}
	
	private int countObs(String conceptUuid, boolean voided) {
		Concept concept = concept(conceptUuid);
		int count = 0;
		for (Obs obs : form.getEncounter().getAllObs(true)) {
			if (obs.getConcept().equals(concept) && obs.getVoided() == voided) {
				count++;
			}
		}
		return count;
	}
	
	@Test
	public void newForm_shouldBeEmpty() {
		assertNull(form.getDispensaryGroup());
		assertTrue(form.getFollowupXrays().isEmpty());
		assertNull(form.getFollowupXray(6));
		assertNull(form.getTransferToGroup2Date());
		assertNull(form.getTransferToGroup3Date());
		assertNull(form.getTransferToGroup3Reason());
		assertNull(form.getRemovalFromObservationDate());
		assertNull(form.getRemovalFromObservationReason());
	}
	
	@Test
	public void setDispensaryGroup_shouldStoreAndChangeTheAnswer() {
		Concept ia = concept(MdrtbConcepts.DISPENSARY_GROUP_IA);
		Concept ib = concept(MdrtbConcepts.DISPENSARY_GROUP_IB);
		form.setDispensaryGroup(ia);
		assertEquals(ia, form.getDispensaryGroup());
		form.setDispensaryGroup(ib);
		assertEquals(ib, form.getDispensaryGroup());
		// the old answer is kept as a voided obs
		assertEquals(1, countObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP, false));
		assertEquals(1, countObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP, true));
	}
	
	@Test
	public void setDispensaryGroup_shouldDoNothingWhenTheAnswerIsUnchanged() {
		Concept ia = concept(MdrtbConcepts.DISPENSARY_GROUP_IA);
		form.setDispensaryGroup(ia);
		form.setDispensaryGroup(ia);
		assertEquals(1, countObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP, false));
		assertEquals(0, countObs(MdrtbConcepts.DISPENSARY_REGISTRATION_GROUP, true));
	}
	
	@Test
	public void setFollowupXray_shouldStoreXraysOrderedByMonth() throws Exception {
		Concept normal = concept(MdrtbConcepts.XRAY_NORMAL);
		Concept abnormal = concept(MdrtbConcepts.XRAY_ABNORMAL);
		form.setFollowupXray(12, date("2024-03-01"), abnormal);
		form.setFollowupXray(6, date("2023-09-01"), normal);
		List<FollowupXray> xrays = form.getFollowupXrays();
		assertEquals(2, xrays.size());
		assertEquals(Integer.valueOf(6), xrays.get(0).getMonthOfTreatment());
		assertEquals(date("2023-09-01"), xrays.get(0).getDate());
		assertEquals(normal, xrays.get(0).getResult());
		assertEquals(Integer.valueOf(12), xrays.get(1).getMonthOfTreatment());
		assertEquals(abnormal, form.getFollowupXray(12).getResult());
	}
	
	@Test
	public void setFollowupXray_shouldAllowMoreThanFiveXrays() throws Exception {
		for (int month = 1; month <= 8; month++) {
			form.setFollowupXray(month, date("2023-09-01"), null);
		}
		assertEquals(8, form.getFollowupXrays().size());
	}
	
	@Test
	public void setFollowupXray_shouldReplaceTheXrayOfTheSameMonthAndKeepTheOldOneVoided() throws Exception {
		form.setFollowupXray(6, date("2023-09-01"), concept(MdrtbConcepts.XRAY_NORMAL));
		form.setFollowupXray(6, date("2023-09-02"), concept(MdrtbConcepts.XRAY_INCONCLUSIVE));
		assertEquals(1, form.getFollowupXrays().size());
		assertEquals(date("2023-09-02"), form.getFollowupXray(6).getDate());
		assertEquals(concept(MdrtbConcepts.XRAY_INCONCLUSIVE), form.getFollowupXray(6).getResult());
		assertEquals(1, countObs(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT, false));
		assertEquals(1, countObs(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT, true));
	}
	
	@Test
	public void setFollowupXray_shouldRemoveTheXrayWhenDateAndResultAreEmpty() throws Exception {
		form.setFollowupXray(6, date("2023-09-01"), concept(MdrtbConcepts.XRAY_NORMAL));
		form.setFollowupXray(6, null, null);
		assertTrue(form.getFollowupXrays().isEmpty());
		assertEquals(1, countObs(MdrtbConcepts.FOLLOWUP_XRAY_CONSTRUCT, true));
	}
	
	@Test(expected = MdrtbAPIException.class)
	public void setFollowupXray_shouldRejectNegativeMonth() throws Exception {
		form.setFollowupXray(-1, date("2023-09-01"), null);
	}
	
	@Test
	public void setDates_shouldStoreAndChangeTheDates() throws Exception {
		form.setTransferToGroup2Date(date("2023-10-01"));
		form.setTransferToGroup3Date(date("2024-10-01"));
		form.setRemovalFromObservationDate(date("2025-01-15"));
		assertEquals(date("2023-10-01"), form.getTransferToGroup2Date());
		assertEquals(date("2024-10-01"), form.getTransferToGroup3Date());
		assertEquals(date("2025-01-15"), form.getRemovalFromObservationDate());
		form.setTransferToGroup2Date(date("2023-11-01"));
		assertEquals(date("2023-11-01"), form.getTransferToGroup2Date());
		assertEquals(1, countObs(MdrtbConcepts.DATE_OF_TRANSFER_TO_DISPENSARY_GROUP_2, true));
	}
	
	@Test
	public void setTransferToGroup3Reason_shouldStoreTheAnswer() {
		Concept disability = concept(MdrtbConcepts.DISABILITY);
		form.setTransferToGroup3Reason(disability);
		assertEquals(disability, form.getTransferToGroup3Reason());
		form.setTransferToGroup3Reason(null);
		assertNull(form.getTransferToGroup3Reason());
	}
	
	@Test
	public void setRemovalFromObservationReason_shouldStoreTextAndTreatBlankAsEmpty() {
		form.setRemovalFromObservationReason("Recovered");
		assertEquals("Recovered", form.getRemovalFromObservationReason());
		form.setRemovalFromObservationReason("   ");
		assertNull(form.getRemovalFromObservationReason());
		assertFalse(countObs(MdrtbConcepts.REASON_FOR_REMOVAL_FROM_DISPENSARY_OBSERVATION, false) > 0);
	}
	
	@Test
	public void getFollowupSmears_shouldBeEmptyWithoutPatientProgram() {
		assertTrue(form.getFollowupSmears().isEmpty());
	}
}
