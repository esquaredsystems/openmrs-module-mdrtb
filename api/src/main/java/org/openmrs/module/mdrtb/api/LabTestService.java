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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.mdrtb.specimen.DstTestType;
import org.openmrs.module.mdrtb.LabConfig;

public interface LabTestService extends OpenmrsService {
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	List<LabTestAttributeType> getAllLabTestAttributeTypes(boolean includeRetired) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	List<LabTestType> getAllLabTestTypes(boolean includeRetired) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getEarliestLabTest(Patient patient) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	LabTestSample getEarliestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getLabTest(Integer labTestId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getLabTestAttribute(Integer labTestAttributeId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getLabTestAttributeByUuid(String uuid) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, String valueReference, Date from,
	        Date to, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, boolean includeVoided)
	        throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> getLabTestAttributes(Integer testOrderId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> getLabTestAttributes(Patient patient, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> getLabTestAttributes(Patient patient, LabTestAttributeType labTestAttributeType,
	        boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestAttributeType getLabTestAttributeType(Integer labTestAttributeTypeId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestAttributeType getLabTestAttributeTypeByUuid(String uuid) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	List<LabTestAttributeType> getLabTestAttributeTypes(String name, String datatypeClassname, boolean includeRetired)
	        throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	List<LabTestAttributeType> getLabTestAttributeTypes(LabTestType labTestType, boolean includeRetired) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getLabTestByUuid(String uuid) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getLabTest(Order order) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	LabTestSample getLabTestSample(Integer labTestSampleId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	LabTestSample getLabTestSampleByUuid(String uuid) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, LabTestSampleStatus status,
	        String labSampleIdentifier, Provider collector, Date from, Date to, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(String labSampleIdentifier, String orderNumber, String labReferenceNumber,
	        boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(LabTest labTest, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(Patient patient, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(Provider collector, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(LabTestSampleStatus status, Date from, Date to, boolean includeVoided)
	        throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, String sampleIdentifier, Concept specimenType,
	        LabTestSampleStatus status, Provider collector, Date from, Date to, boolean includeVoided);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType getLabTestType(Integer labTestTypeId) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType getLabTestTypeByUuid(String uuid) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	List<LabTestType> getLabTestTypes(String name, String shortName, LabTestGroup testGroup, Boolean isSpecimenRequired,
	        Concept referenceConcept, boolean includeRetired) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(LabTestType labTestType, Patient patient, String orderNumber, String referenceNumber,
	        Concept orderConcept, Provider orderer, Date from, Date to, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(LabTestType labTestType, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(Concept orderConcept, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(Provider orderer, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(Patient patient, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(String referenceNumber, boolean includeVoided) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getLatestLabTest(Patient patient) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestSample getLatestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	LabTest saveLabTest(LabTest labTest) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	LabTest saveLabTest(LabTest labTest, LabTestSample labTestSample, Collection<LabTestAttribute> labTestAttributes)
	        throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	LabTestAttribute saveLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	List<LabTestAttribute> saveLabTestAttributes(List<LabTestAttribute> labTestAttributes) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	LabTestAttributeType saveLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_SAMPLE_PRIVILEGE)
	LabTestSample saveLabTestSample(LabTestSample labTestSample) throws APIException;
	
	@Authorized(LabConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType saveLabTestType(LabTestType labTestType) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void retireLabTestType(LabTestType labTestType, String retireReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void retireLabTestAttributeType(LabTestAttributeType labTestAttributeType, String retireReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void unretireLabTestType(LabTestType labTestType) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void unretireLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void voidLabTest(LabTest labTest, String voidReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void voidLabTestAttribute(LabTestAttribute labTestAttribute, String voidReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void voidLabTestAttributes(LabTest labTest, String voidReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	void voidLabTestSample(LabTestSample labTestSample, String voidReason) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void unvoidLabTest(LabTest labTest) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void unvoidLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	void unvoidLabTestSample(LabTestSample labTestSample) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void deleteLabTest(LabTest labTest) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void deleteLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void deleteLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void deleteLabTestAttributeType(LabTestAttributeType labTestAttributeType, boolean cascade) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	void deleteLabTestSample(LabTestSample labTestSample) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void deleteLabTestType(LabTestType labTestType) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Deprecated
	void deleteLabTestType(LabTestType labTestType, boolean cascade) throws APIException;
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	void deleteLabTestType(LabTestType labTestType, LabTestType newObjectForCascade) throws APIException;
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(Patient patient);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	List<LabTest> getLabTests(Patient patient, LabTestType labTestType);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType getCommonTestType();
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType getDstMgitTestType();
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestType getDstLjTestType();
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	LabTestSample getMostRecentAcceptedSample(LabTest labTest);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestAttributeType getLabTestAttributeTypeByName(String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	LabTestAttributeType getLabTestAttributeTypeByTestTypeAndName(LabTestType testType, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getCommonAttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getXpertAttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getCultureAttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getHainAttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getHain2AttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getSmearAttributeByTestAndName(LabTest labTest, String name);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTestAttribute getDstAttributeByTestAndName(LabTest labTest, String name, DstTestType dstTestType);
	
	/**
	 * Searches for a Lab Test order against given {@link Encounter} and creates if one doesn't
	 * exist, otherwise returns existing one.
	 */
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getMdrtbLabTestOrder(Encounter encounter, LabTestType labTestType);
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	LabTest createMdrtbLabTestOrder(Encounter encounter, LabTestType labTestType);
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	LabTest getDstLabTestOrder(Encounter encounter);
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	LabTest createDstLabTestOrder(Encounter encounter);
}
