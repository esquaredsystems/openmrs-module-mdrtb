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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.UnchangeablePropertyException;
import org.openmrs.api.context.Context;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.mdrtb.LabTConfig;
import org.openmrs.module.mdrtb.api.dao.LabDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("commonlabtest.CommonLabTestService")
public class LabServiceImpl extends BaseOpenmrsService implements OpenmrsService {
	
	@Autowired
	LabDao dao;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 * 
	 * @param dao the {@link LabDao} object
	 */
	public void setDao(LabDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Returns list of all {@link LabTestAttributeType} objects
	 * 
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getAllLabTestAttributeTypes(boolean includeRetired) throws APIException {
		return dao.getAllLabTestAttributeTypes(includeRetired);
	}
	
	/**
	 * Returns list of all objects of {@link LabTestType}
	 * 
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestType> getAllLabTestTypes(boolean includeRetired) throws APIException {
		return dao.getAllLabTestTypes(includeRetired);
	}
	
	/**
	 * Returns first {@link LabTest} object by given {@link Patient}
	 * 
	 * @param patient the {@link Patient} object
	 * @return {@link LabTestType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getEarliestLabTest(Patient patient) throws APIException {
		List<LabTest> labTests = dao.getNLabTests(patient, 1, true, false, false);
		if (!labTests.isEmpty()) {
			return labTests.get(0);
		}
		return null;
	}
	
	/**
	 * Returns first {@link LabTestSample} object by given {@link Patient} and
	 * {@link LabTestSampleStatus}
	 * 
	 * @param patient the {@link Patient} object
	 * @param status the {@link LabTestSampleStatus} object
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getEarliestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException {
		List<LabTestSample> labTestSamples = dao.getNLabTestSamples(patient, status, 1, true, false, false);
		if (!labTestSamples.isEmpty()) {
			return labTestSamples.get(0);
		}
		return null;
	}
	
	/**
	 * Returns a {@link LabTest} object by generated ID
	 * 
	 * @param labTestId the Id
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTest(Integer labTestId) throws APIException {
		return dao.getLabTest(labTestId);
	}
	
	/**
	 * Returns a {@link LabTestAttribute} object by its generated ID
	 * 
	 * @param labTestAttributeId the Id
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttribute getLabTestAttribute(Integer labTestAttributeId) throws APIException {
		return dao.getLabTestAttribute(labTestAttributeId);
	}
	
	/**
	 * Returns a {@link LabTestAttribute} object by uuid. It can be called by any authenticated
	 * user. It is fetched in read only transaction.
	 * 
	 * @param uuid the UUID
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttribute getLabTestAttributeByUuid(String uuid) throws APIException {
		return dao.getLabTestAttributeByUuid(uuid);
	}
	
	/**
	 * Get a list of {@link LabTestAttribute} objects using various parameters available. This is
	 * similar to getByExample(...) methods, except that instead of passing similar object,
	 * properties are passed as parameters
	 * 
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param valueReference the value of reference
	 * @param from the {@link Date} object
	 * @param to the {@link Date} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, String valueReference,
	        Date from, Date to, boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(labTestAttributeType, valueReference, from, to, includeVoided);
	}
	
	/**
	 * @see #getLabTestAttributes(LabTestAttributeType, String, Date, Date, boolean)
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, boolean includeVoided)
	        throws APIException {
		return getLabTestAttributes(labTestAttributeType, null, null, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestAttributes(Integer)
	 * @param testOrderId the order Id
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Integer testOrderId) throws APIException {
		return dao.getLabTestAttributes(testOrderId);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestAttributes(Patient,
	 *      LabTestAttributeType, boolean)
	 * @param patient the {@link Patient} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(patient, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestAttributes(Patient,
	 *      LabTestAttributeType, boolean)
	 * @param patient the {@link Patient} object
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestAttribute} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, LabTestAttributeType labTestAttributeType,
	        boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(patient, labTestAttributeType, includeVoided);
	}
	
	/**
	 * Returns a {@link LabTestAttributeType} object by its generated ID
	 * 
	 * @param labTestAttributeTypeId the generated Id
	 * @return {@link LabTestAttributeType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttributeType getLabTestAttributeType(Integer labTestAttributeTypeId) throws APIException {
		return dao.getLabTestAttributeType(labTestAttributeTypeId);
	}
	
	/**
	 * Returns a {@link LabTestAttributeType} object by uuid. It can be called by any authenticated
	 * user. It is fetched in read only transaction.
	 * 
	 * @param uuid the unique Id
	 * @return {@link LabTestAttributeType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttributeType getLabTestAttributeTypeByUuid(String uuid) throws APIException {
		return dao.getLabTestAttributeTypeByUuid(uuid);
	}
	
	/**
	 * Get a list of {@link LabTestAttributeType} objects using various parameters available. This
	 * is similar to getByExample(...) methods, except that instead of passing similar object,
	 * properties are passed as parameters
	 * 
	 * @param name the name of attribute type
	 * @param datatypeClassname fully specified data type class name
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getLabTestAttributeTypes(String name, String datatypeClassname, boolean includeRetired)
	        throws APIException {
		return dao.getLabTestAttributeTypes(name, datatypeClassname, includeRetired);
	}
	
	/**
	 * Get a list of {@link LabTestAttributeType} objects against {@link LabTestType} object
	 * 
	 * @param labTestType the {@link LabTestType} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getLabTestAttributeTypes(LabTestType labTestType, boolean includeRetired)
	        throws APIException {
		return dao.getLabTestAttributeTypes(labTestType, includeRetired);
	}
	
	/**
	 * Returns a {@link LabTest} object by uuid. It can be called by any authenticated user. It is
	 * fetched in read only transaction.
	 * 
	 * @param uuid the unique Id
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTestByUuid(String uuid) throws APIException {
		return dao.getLabTestByUuid(uuid);
	}
	
	/**
	 * @param order the {@link Order} object
	 * @return {@link LabTest} object by matching given {@link Order} object
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTest(Order order) throws APIException {
		return dao.getLabTest(order);
	}
	
	/**
	 * Returns a {@link LabTestSample} object by its generated ID
	 * 
	 * @param labTestSampleId the Id
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLabTestSample(Integer labTestSampleId) throws APIException {
		return dao.getLabTestSample(labTestSampleId);
	}
	
	/**
	 * Returns a {@link LabTestSample} object by uuid. It can be called by any authenticated user.
	 * It is fetched in read only transaction.
	 * 
	 * @param uuid the unique Id
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLabTestSampleByUuid(String uuid) throws APIException {
		return dao.getLabTestSampleByUuid(uuid);
	}
	
	/**
	 * Get a list of {@link LabTestSample} objects using various parameters available. This is
	 * similar to getByExample(...) methods, except that instead of passing similar object,
	 * properties are passed as parameters
	 * 
	 * @param labTest the {@link LabTest} object
	 * @param patient the {@link Patient} object
	 * @param status the {@link LabTestSampleStatus} object
	 * @param labSampleIdentifier the lab sample Id
	 * @param collector the {@link Provider} object
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, LabTestSampleStatus status,
	        String labSampleIdentifier, Provider collector, Date from, Date to, boolean includeVoided) throws APIException {
		return getLabTestSamples(labTest, patient, labSampleIdentifier, null, status, collector, from, to, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTestSample} objects by matching given identifier and/or order
	 * number and/or lab reference number
	 * 
	 * @param labSampleIdentifier the lab sample Id
	 * @param orderNumber the order number
	 * @param labReferenceNumber the reference number
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(String labSampleIdentifier, String orderNumber, String labReferenceNumber,
	        boolean includeVoided) throws APIException {
		return getLabTestSamples(null, null, null, labSampleIdentifier, null, null, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestSamples(LabTest, boolean)
	 * @param labTest the {@link LabTest} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(labTest, includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestSamples(Patient, boolean)
	 * @param patient the {@link Patient} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(Patient patient, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(patient, includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.mdrtb.api.dao.LabDao#getLabTestSamples(Provider, boolean)
	 * @param collector the {@link Provider} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(Provider collector, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(collector, includeVoided);
	}
	
	/**
	 * Returns list of {@link LabTestSample} objects by matching status property and date range.
	 * This can be used to get samples which are yet to be processed
	 * 
	 * @param status the {@link LabTestSampleStatus} object
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTestSampleStatus status, Date from, Date to, boolean includeVoided)
	        throws APIException {
		return getLabTestSamples(null, null, status, null, null, from, to, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTestSample} objects by matching the given criteria. At least one
	 * of the first three parameters must be provided, the rest are optional.
	 * 
	 * @param labTest the {@link LabTest} object
	 * @param patient the {@link Patient} object
	 * @param sampleIdentifier the identifier of specimen sample
	 * @param specimenType the {@link Concept} object representing type of specimen
	 * @param status the {@link LabTestSampleStatus} enumerated type
	 * @param collector the {@link Provider} object
	 * @param from the start {@link Date} object representing start of date of creation
	 * @param to the end {@link Date} object representing end of date of creation
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, String sampleIdentifier,
	        Concept specimenType, LabTestSampleStatus status, Provider collector, Date from, Date to, boolean includeVoided) {
		return dao.getLabTestSamples(labTest, patient, sampleIdentifier, specimenType, status, collector, from, to,
		    includeVoided);
	}
	
	/**
	 * @param labTestTypeId the generated Id
	 * @return {@link LabTestType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestType getLabTestType(Integer labTestTypeId) throws APIException {
		return dao.getLabTestType(labTestTypeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestType getLabTestTypeByUuid(String uuid) throws APIException {
		return dao.getLabTestTypeByUuid(uuid);
	}
	
	/**
	 * Get a list of {@link LabTestType} objects using various parameters available. This is similar
	 * to getByExample(...) methods, except that instead of passing similar object, properties are
	 * passed as parameters
	 * 
	 * @param name the name
	 * @param shortName the short name
	 * @param testGroup the {@link LabTestGroup} object
	 * @param isSpecimenRequired whether specimen is required
	 * @param referenceConcept the {@link Concept} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestType> getLabTestTypes(String name, String shortName, LabTestGroup testGroup,
	        final Boolean isSpecimenRequired, Concept referenceConcept, boolean includeRetired) throws APIException {
		List<LabTestType> labTestTypes = dao.getLabTestTypes(name, shortName, testGroup, referenceConcept, includeRetired);
		if (isSpecimenRequired != null) {
			for (Iterator<LabTestType> iterator = labTestTypes.iterator(); iterator.hasNext();) {
				LabTestType labTestType = iterator.next();
				if (!labTestType.getRequiresSpecimen().equals(isSpecimenRequired)) {
					iterator.remove();
				}
			}
		}
		return labTestTypes;
	}
	
	/**
	 * Get a list of {@link LabTest} objects using various parameters available. This is similar to
	 * getByExample(...) methods, except that instead of passing similar object, properties are
	 * passed as parameters
	 * 
	 * @param labTestType the {@link LabTestType} object
	 * @param patient the {@link Patient} object
	 * @param orderNumber the order number
	 * @param referenceNumber the reference number
	 * @param orderConcept the {@link Concept} object
	 * @param orderer the {@link Provider} object
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(LabTestType labTestType, Patient patient, String orderNumber, String referenceNumber,
	        Concept orderConcept, Provider orderer, Date from, Date to, boolean includeVoided) throws APIException {
		return dao.getLabTests(labTestType, patient, orderNumber, referenceNumber, orderConcept, orderer, from, to,
		    includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTest} objects by {@link LabTestType}
	 * 
	 * @param labTestType the {@link LabTestType} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(LabTestType labTestType, boolean includeVoided) throws APIException {
		return getLabTests(labTestType, null, null, null, null, null, null, null, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTest} objects by {@link Concept} object in {@link LabTest} Order
	 * 
	 * @param orderConcept the {@link Concept} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Concept orderConcept, boolean includeVoided) throws APIException {
		return getLabTests(null, null, null, null, orderConcept, null, null, null, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTest} objects by {@link LabTest} Order {@link Provider} object
	 * 
	 * @param orderer the {@link Provider} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Provider orderer, boolean includeVoided) throws APIException {
		return getLabTests(null, null, null, null, null, orderer, null, null, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTest} objects by {@link Patient}
	 * 
	 * @param patient the {@link Patient} object
	 * @param includeVoided include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Patient patient, boolean includeVoided) throws APIException {
		return getLabTests(null, patient, null, null, null, null, null, null, includeVoided);
	}
	
	/**
	 * Returns a list of {@link LabTest} objects by matching reference number
	 * 
	 * @param referenceNumber the reference number
	 * @param includeVoided to include voided objects
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(String referenceNumber, boolean includeVoided) throws APIException {
		if (referenceNumber.length() < 4) {
			throw new APIException("Reference number to search should at least be 4 character long.");
		}
		return getLabTests(null, null, null, referenceNumber, null, null, null, null, includeVoided);
	}
	
	/**
	 * Returns most recent {@link LabTest} object by given {@link Patient}
	 * 
	 * @param patient the {@link Patient} object
	 * @return {@link LabTest} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLatestLabTest(Patient patient) throws APIException {
		List<LabTest> labTests = dao.getNLabTests(patient, 1, false, true, false);
		if (!labTests.isEmpty()) {
			return labTests.get(0);
		}
		return null;
	}
	
	/**
	 * Returns most recent {@link LabTestSample} object by given {@link Patient} and
	 * {@link LabTestSampleStatus}
	 * 
	 * @param patient the {@link Patient} object
	 * @param status the {@link LabTestSampleStatus} object (optional, set null to skip)
	 * @return {@link LabTestSample} object(s)
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLatestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException {
		List<LabTestSample> labTestSamples = dao.getNLabTestSamples(patient, status, 1, false, true, false);
		if (!labTestSamples.isEmpty()) {
			return labTestSamples.get(0);
		}
		return null;
	}
	
	/**
	 * @param labTest the {@link LabTest} object to save
	 * @return saved {@link LabTest} object
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTest saveLabTest(LabTest labTest) throws APIException {
		return saveLabTest(labTest, null, null);
	}
	
	/**
	 * Saves a {@link LabTest} object along with its properties {@link LabTestSample} and
	 * {@link LabTestAttribute} object(s)
	 * 
	 * @param labTest the {@link LabTest} object to save
	 * @param labTestSample the {@link LabTestSample} object to save
	 * @param labTestAttributes tye {@link LabTestAttribute} object(s) to save
	 * @return saved {@link LabTest} object
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTest saveLabTest(LabTest labTest, LabTestSample labTestSample, Collection<LabTestAttribute> labTestAttributes)
	        throws APIException {
		// Check mandatory fields
		if (labTest.getOrder() == null) {
			throw new APIException("org.openmrs.Order", (Object[]) null);
		}
		if (labTest.getOrder().getEncounter() == null) {
			throw new APIException("org.openmrs.Encounter", (Object[]) null);
		}
		if (labTest.getOrder().getConcept() == null) {
			throw new APIException("org.openmrs.Concept", (Object[]) null);
		}
		if (labTest.getOrder().getOrderer() == null) {
			throw new APIException("org.openmrs.Orderer", (Object[]) null);
		}
		// Order object is saved via DAO object
		// Save Order
		LabTest savedLabTest = dao.saveLabTest(labTest);
		// Save sample if present
		if (labTestSample != null) {
			LabTestSample saveLabTestSample = saveLabTestSample(labTestSample);
			labTest.removeLabTestSample(labTestSample);
			labTest.addLabTestSample(saveLabTestSample);
		}
		// Save attributes if present
		if (labTestAttributes != null) {
			Set<LabTestAttribute> attributes = new HashSet<LabTestAttribute>();
			for (LabTestAttribute labTestAttribute : labTestAttributes) {
				attributes.add(saveLabTestAttribute(labTestAttribute));
			}
			savedLabTest.setAttributes(attributes);
		}
		return savedLabTest;
	}
	
	/*
	 * @see CommonLabTestService#saveLabTestAttribute(LabTestAttribute)
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTestAttribute saveLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		return dao.saveLabTestAttribute(labTestAttribute);
	}
	
	/*
	 * @see CommonLabTestService#saveLabTestAttributes(List)
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public List<LabTestAttribute> saveLabTestAttributes(List<LabTestAttribute> labTestAttributes) throws APIException {
		for (LabTestAttribute labTestAttribute : labTestAttributes) {
			saveLabTestAttribute(labTestAttribute);
		}
		return labTestAttributes;
	}
	
	/*
	 * @see CommonLabTestService#saveLabTestAttributeType(LabTestAttributeType)
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public LabTestAttributeType saveLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		return dao.saveLabTestAttributeType(labTestAttributeType);
	}
	
	/*
	 * @see CommonLabTestService#saveLabTestSample(LabTestSample)
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public LabTestSample saveLabTestSample(LabTestSample labTestSample) throws APIException {
		return dao.saveLabTestSample(labTestSample);
	}
	
	/**
	 * Saves a {@link LabTestType} object (except mandatory Unknown record). Sets the owner to
	 * superuser, if it is not set. It can be called by users with this module's privilege. It is
	 * executed in a transaction.
	 * 
	 * @param labTestType the {@link LabTestType} object to save
	 * @return saved {@link LabTestType} object
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public LabTestType saveLabTestType(LabTestType labTestType) throws APIException {
		handleUnknownTestTypeOperation(labTestType);
		return dao.saveLabTestType(labTestType);
	}
	
	/**
	 * This method disallows any modification in Unknown {@link LabTestType}. This method must be
	 * called in before DML operations in any {@link LabTestType} object
	 * 
	 * @param labTestType
	 */
	private void handleUnknownTestTypeOperation(LabTestType labTestType) {
		if (labTestType.getUuid().equals(LabTestType.UNKNOWN_TEST_UUID)) {
			throw new UnchangeablePropertyException("The LabTestType: UNKNOWN " + LabTestType.UNKNOWN_TEST_UUID
			        + " is mandatory, and cannot be altered.");
		}
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to retire
	 * @param retireReason the reason to retire
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void retireLabTestType(LabTestType labTestType, String retireReason) throws APIException {
		handleUnknownTestTypeOperation(labTestType);
		if (labTestType.getRetired()) {
			throw new APIException("Object has alread been retired.");
		}
		labTestType.setRetired(Boolean.TRUE);
		labTestType.setRetiredBy(Context.getAuthenticatedUser());
		labTestType.setRetireReason(retireReason);
		dao.saveLabTestType(labTestType);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to retire
	 * @param retireReason the reason to retire
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void retireLabTestAttributeType(LabTestAttributeType labTestAttributeType, String retireReason)
	        throws APIException {
		if (labTestAttributeType.getRetired()) {
			throw new APIException("Object has alread been retired.");
		}
		labTestAttributeType.setRetired(Boolean.TRUE);
		labTestAttributeType.setRetiredBy(Context.getAuthenticatedUser());
		labTestAttributeType.setRetireReason(retireReason);
		dao.saveLabTestAttributeType(labTestAttributeType);
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to unretire
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void unretireLabTestType(LabTestType labTestType) throws APIException {
		labTestType.setRetired(Boolean.FALSE);
		labTestType.setRetireReason("Previously retired for reason: " + labTestType.getRetireReason());
		handleUnknownTestTypeOperation(labTestType);
		dao.saveLabTestType(labTestType);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to unretire
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void unretireLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		labTestAttributeType.setRetired(Boolean.FALSE);
		labTestAttributeType.setRetireReason("Previously retired for reason: " + labTestAttributeType.getRetireReason());
		dao.saveLabTestAttributeType(labTestAttributeType);
	}
	
	/**
	 * Voids a {@link LabTest} object and all {@link LabTestSample} and {@link LabTestAttribute}
	 * objects associated with it
	 * 
	 * @param labTest the {@link LabTest} object to void
	 * @param voidReason the reason to void
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void voidLabTest(LabTest labTest, String voidReason) throws APIException {
		List<LabTestSample> labTestSamples = dao.getLabTestSamples(labTest, Boolean.FALSE);
		if (labTestSamples != null) {
			for (LabTestSample sample : labTestSamples) {
				voidLabTestSample(sample, voidReason);
			}
		}
		List<LabTestAttribute> labTestAttributes = dao.getLabTestAttributes(labTest.getId());
		if (labTestAttributes != null) {
			for (LabTestAttribute attribute : labTestAttributes) {
				voidLabTestAttribute(attribute, voidReason);
			}
		}
		labTest.setVoided(true);
		labTest.setVoidedBy(Context.getAuthenticatedUser());
		labTest.setVoidReason(voidReason);
		Context.getOrderService().voidOrder(labTest.getOrder(), voidReason);
		dao.saveLabTest(labTest);
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to void
	 * @param voidReason the reason to void
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void voidLabTestAttribute(LabTestAttribute labTestAttribute, String voidReason) throws APIException {
		labTestAttribute.setVoided(true);
		labTestAttribute.setVoidedBy(Context.getAuthenticatedUser());
		labTestAttribute.setVoidReason(voidReason);
		labTestAttribute.setDateVoided(new Date());
		dao.saveLabTestAttribute(labTestAttribute);
	}
	
	/**
	 * Voids list of {@link LabTestAttribute} objects by {@link LabTest} and set the
	 * {@link LabTestSampleStatus} of PROCESSED {@link LabTestSample} objects to COLLECTED
	 * 
	 * @param labTest the {@link LabTest} object to void
	 * @param voidReason the reason to void
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void voidLabTestAttributes(LabTest labTest, String voidReason) throws APIException {
		// Set the status of PROCESSED LabTestSample objects back to COLLECTED
		List<LabTestSample> labTestSamples = getLabTestSamples(labTest, true);
		for (LabTestSample labTestSample : labTestSamples) {
			if (labTestSample.getStatus() == LabTestSampleStatus.PROCESSED) {
				labTestSample.setStatus(LabTestSampleStatus.COLLECTED);
				labTestSample = dao.saveLabTestSample(labTestSample);
			}
		}
		// Void all attributes in the list
		List<LabTestAttribute> labTestAttributes = getLabTestAttributes(labTest.getTestOrderId());
		if (labTestAttributes != null) {
			for (LabTestAttribute labTestAttribute : labTestAttributes) {
				if (labTestAttribute.getVoided()) {
					continue;
				}
				voidLabTestAttribute(labTestAttribute, voidReason);
			}
		}
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to void
	 * @param voidReason the reason to void
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public void voidLabTestSample(LabTestSample labTestSample, String voidReason) throws APIException {
		labTestSample.setVoided(true);
		labTestSample.setVoidedBy(Context.getAuthenticatedUser());
		labTestSample.setVoidReason(voidReason);
		labTestSample.setDateVoided(new Date());
		dao.saveLabTestSample(labTestSample);
	}
	
	/**
	 * Unvoids a {@link LabTest} object along with all {@link LabTestSample} and
	 * {@link LabTestAttribute} objects, which were voided with the same reason on same time. This
	 * way, the dependent objects which were manually void will stay voided.
	 * 
	 * @param labTest the {@link LabTest} object to unvoid
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void unvoidLabTest(LabTest labTest) throws APIException {
		
		List<LabTestSample> labTestSamples = dao.getLabTestSamples(labTest, Boolean.TRUE);
		for (LabTestSample sample : labTestSamples) {
			unvoidLabTestSample(sample);
		}
		
		List<LabTestAttribute> labTestAttributes = dao.getLabTestAttributes(labTest.getId());
		for (LabTestAttribute attribute : labTestAttributes) {
			unvoidLabTestAttribute(attribute);
		}
		Context.getOrderService().unvoidOrder(labTest.getOrder());
		dao.saveLabTest(labTest);
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to unvoid
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void unvoidLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		labTestAttribute.setVoided(false);
		labTestAttribute.setVoidReason("Previously voided for reason: " + labTestAttribute.getVoidReason());
		dao.saveLabTestAttribute(labTestAttribute);
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to unvoid
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public void unvoidLabTestSample(LabTestSample labTestSample) throws APIException {
		labTestSample.setVoided(false);
		labTestSample.setVoidReason("Previously voided for reason: " + labTestSample.getVoidReason());
		dao.saveLabTestSample(labTestSample);
	}
	
	/**
	 * @param labTest the {@link LabTest} object to delete
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTest(LabTest labTest) throws APIException {
		dao.purgeLabTest(labTest);
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to delete
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		dao.purgeLabTestAttribute(labTestAttribute);
	}
	
	/**
	 * @see #deleteLabTestAttributeType(LabTestAttributeType, boolean)
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		deleteLabTestAttributeType(labTestAttributeType, false);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to delete
	 * @param cascade whether to cascade delete dependent objects or not
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestAttributeType(LabTestAttributeType labTestAttributeType, boolean cascade) throws APIException {
		if (cascade) {
			List<LabTestAttribute> labTestAttributes = getLabTestAttributes(labTestAttributeType, true);
			for (LabTestAttribute labTestAttribute : labTestAttributes) {
				try {
					dao.purgeLabTestAttribute(labTestAttribute);
				}
				catch (Exception e) {
					throw new APIException(e.getMessage());
				}
			}
		}
		dao.purgeLabTestAttributeType(labTestAttributeType);
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to delete
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTestSample(LabTestSample labTestSample) throws APIException {
		dao.purgeLabTestSample(labTestSample);
	}
	
	/**
	 * @see #deleteLabTestType(LabTestType, LabTestType)
	 * @param labTestType the {@link LabTestType} object to delete
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestType(LabTestType labTestType) throws APIException {
		deleteLabTestType(labTestType, null);
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to delete
	 * @param cascade whether to cascade delete dependent objects or not
	 * @throws APIException on Exception
	 * @deprecated because deleting dependent objects is a risk. Use the alternate overloaded method
	 *             <code>deleteLabTestType(LabTestType, LabTestType)</code>
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	@Deprecated
	public void deleteLabTestType(LabTestType labTestType, boolean cascade) throws APIException {
		List<LabTest> labTests = getLabTests(labTestType, true);
		if (labTests == null) {
			dao.purgeLabTestType(labTestType);
		} else if (cascade) {
			for (LabTest labTest : labTests) {
				dao.purgeLabTest(labTest);
			}
			dao.purgeLabTestType(labTestType);
		} else {
			throw new APIException("Cannot delete LabTestType because of Foreign Key Violation.");
		}
	}
	
	/**
	 * Deletes a LabTestType object and sets {@link LabTestType} of dependent objects to second
	 * parameter
	 * 
	 * @param labTestType the {@link LabTestType} object to delete
	 * @param newObjectForCascade the {@link LabTestType} set to cascaded dependent objects
	 * @throws APIException on Exception
	 */
	@Authorized(LabTConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestType(LabTestType labTestType, LabTestType newObjectForCascade) throws APIException {
		// Replace with Unknown Test Type object if null
		if (newObjectForCascade == null) {
			newObjectForCascade = getLabTestTypeByUuid(LabTestType.UNKNOWN_TEST_UUID);
		}
		StringBuilder message = new StringBuilder();
		message.append("Associated LabTestType: ");
		message.append(labTestType.getName());
		message.append("(");
		message.append(labTestType.getUuid());
		message.append(") was deleted on ");
		message.append(Context.getDateFormat().format(new Date()));
		// Replace LabTestType in dependencies
		handleLabTestTypeDependencies(labTestType, newObjectForCascade, message.toString());
		dao.purgeLabTestType(labTestType);
	}
	
	/**
	 * This method changes the {@link LabTestType} object in respective {@link LabTest} and
	 * {@link LabTestAttributeType} dependencies, and voides/retires them afterwards with given
	 * message
	 * 
	 * @param labTestType
	 * @param newObjectForCascade
	 * @param voidMessage
	 */
	private void handleLabTestTypeDependencies(LabTestType labTestType, LabTestType newObjectForCascade, String voidMessage) {
		List<LabTest> labTests = getLabTests(labTestType, true);
		if (labTests != null) {
			for (LabTest labTest : labTests) {
				// Set new LabTestType
				labTest.setLabTestType(newObjectForCascade);
				saveLabTest(labTest);
				// Void with reason
				voidLabTest(labTest, voidMessage);
			}
		}
		List<LabTestAttributeType> testAttributeTypes = getLabTestAttributeTypes(labTestType, true);
		if (testAttributeTypes != null) {
			for (LabTestAttributeType attributeType : testAttributeTypes) {
				// Set new LabTestType
				attributeType.setLabTestType(newObjectForCascade);
				saveLabTestAttributeType(attributeType);
				// Retired with reason
				retireLabTestAttributeType(attributeType, voidMessage);
			}
		}
	}
}
