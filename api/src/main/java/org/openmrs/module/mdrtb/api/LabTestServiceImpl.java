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
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.mdrtb.LabConfig;
import org.openmrs.module.mdrtb.api.dao.LabDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("labtest.LabTestService")
public class LabTestServiceImpl extends BaseOpenmrsService implements LabTestService {
	
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
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getAllLabTestAttributeTypes(boolean includeRetired) throws APIException {
		return dao.getAllLabTestAttributeTypes(includeRetired);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestType> getAllLabTestTypes(boolean includeRetired) throws APIException {
		return dao.getAllLabTestTypes(includeRetired);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getEarliestLabTest(Patient patient) throws APIException {
		List<LabTest> labTests = dao.getNLabTests(patient, 1, true, false, false);
		if (!labTests.isEmpty()) {
			return labTests.get(0);
		}
		return null;
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getEarliestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException {
		List<LabTestSample> labTestSamples = dao.getNLabTestSamples(patient, status, 1, true, false, false);
		if (!labTestSamples.isEmpty()) {
			return labTestSamples.get(0);
		}
		return null;
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTest(Integer labTestId) throws APIException {
		return dao.getLabTest(labTestId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttribute getLabTestAttribute(Integer labTestAttributeId) throws APIException {
		return dao.getLabTestAttribute(labTestAttributeId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttribute getLabTestAttributeByUuid(String uuid) throws APIException {
		return dao.getLabTestAttributeByUuid(uuid);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, String valueReference,
	        Date from, Date to, boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(labTestAttributeType, valueReference, from, to, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, boolean includeVoided)
	        throws APIException {
		return getLabTestAttributes(labTestAttributeType, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Integer testOrderId) throws APIException {
		return dao.getLabTestAttributes(testOrderId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(patient, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, LabTestAttributeType labTestAttributeType,
	        boolean includeVoided) throws APIException {
		return dao.getLabTestAttributes(patient, labTestAttributeType, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttributeType getLabTestAttributeType(Integer labTestAttributeTypeId) throws APIException {
		return dao.getLabTestAttributeType(labTestAttributeTypeId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestAttributeType getLabTestAttributeTypeByUuid(String uuid) throws APIException {
		return dao.getLabTestAttributeTypeByUuid(uuid);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getLabTestAttributeTypes(String name, String datatypeClassname, boolean includeRetired)
	        throws APIException {
		return dao.getLabTestAttributeTypes(name, datatypeClassname, includeRetired);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestAttributeType> getLabTestAttributeTypes(LabTestType labTestType, boolean includeRetired)
	        throws APIException {
		return dao.getLabTestAttributeTypes(labTestType, includeRetired);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTestByUuid(String uuid) throws APIException {
		return dao.getLabTestByUuid(uuid);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLabTest(Order order) throws APIException {
		return dao.getLabTest(order);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLabTestSample(Integer labTestSampleId) throws APIException {
		return dao.getLabTestSample(labTestSampleId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLabTestSampleByUuid(String uuid) throws APIException {
		return dao.getLabTestSampleByUuid(uuid);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, LabTestSampleStatus status,
	        String labSampleIdentifier, Provider collector, Date from, Date to, boolean includeVoided) throws APIException {
		return getLabTestSamples(labTest, patient, labSampleIdentifier, null, status, collector, from, to, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(String labSampleIdentifier, String orderNumber, String labReferenceNumber,
	        boolean includeVoided) throws APIException {
		return getLabTestSamples(null, null, null, labSampleIdentifier, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(labTest, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(Patient patient, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(patient, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(Provider collector, boolean includeVoided) throws APIException {
		return dao.getLabTestSamples(collector, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTestSampleStatus status, Date from, Date to, boolean includeVoided)
	        throws APIException {
		return getLabTestSamples(null, null, status, null, null, from, to, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, String sampleIdentifier,
	        Concept specimenType, LabTestSampleStatus status, Provider collector, Date from, Date to, boolean includeVoided) {
		return dao.getLabTestSamples(labTest, patient, sampleIdentifier, specimenType, status, collector, from, to,
		    includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestType getLabTestType(Integer labTestTypeId) throws APIException {
		return dao.getLabTestType(labTestTypeId);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestType getLabTestTypeByUuid(String uuid) throws APIException {
		return dao.getLabTestTypeByUuid(uuid);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_METADATA_PRIVILEGE)
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
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(LabTestType labTestType, Patient patient, String orderNumber, String referenceNumber,
	        Concept orderConcept, Provider orderer, Date from, Date to, boolean includeVoided) throws APIException {
		return dao.getLabTests(labTestType, patient, orderNumber, referenceNumber, orderConcept, orderer, from, to,
		    includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(LabTestType labTestType, boolean includeVoided) throws APIException {
		return getLabTests(labTestType, null, null, null, null, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Concept orderConcept, boolean includeVoided) throws APIException {
		return getLabTests(null, null, null, null, orderConcept, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Provider orderer, boolean includeVoided) throws APIException {
		return getLabTests(null, null, null, null, null, orderer, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(Patient patient, boolean includeVoided) throws APIException {
		return getLabTests(null, patient, null, null, null, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public List<LabTest> getLabTests(String referenceNumber, boolean includeVoided) throws APIException {
		if (referenceNumber.length() < 4) {
			throw new APIException("Reference number to search should at least be 4 character long.");
		}
		return getLabTests(null, null, null, referenceNumber, null, null, null, null, includeVoided);
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTest getLatestLabTest(Patient patient) throws APIException {
		List<LabTest> labTests = dao.getNLabTests(patient, 1, false, true, false);
		if (!labTests.isEmpty()) {
			return labTests.get(0);
		}
		return null;
	}
	
	@Authorized(LabConfig.VIEW_LAB_TEST_PRIVILEGE)
	@Transactional(readOnly = true)
	public LabTestSample getLatestLabTestSample(Patient patient, LabTestSampleStatus status) throws APIException {
		List<LabTestSample> labTestSamples = dao.getNLabTestSamples(patient, status, 1, false, true, false);
		if (!labTestSamples.isEmpty()) {
			return labTestSamples.get(0);
		}
		return null;
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTest saveLabTest(LabTest labTest) throws APIException {
		return saveLabTest(labTest, null, null);
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTest saveLabTest(LabTest labTest, LabTestSample labTestSample, Collection<LabTestAttribute> labTestAttributes)
	        throws APIException {
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
		LabTest savedLabTest = dao.saveLabTest(labTest);
		if (labTestSample != null) {
			LabTestSample saveLabTestSample = saveLabTestSample(labTestSample);
			labTest.removeLabTestSample(labTestSample);
			labTest.addLabTestSample(saveLabTestSample);
		}
		if (labTestAttributes != null) {
			Set<LabTestAttribute> attributes = new HashSet<LabTestAttribute>();
			for (LabTestAttribute labTestAttribute : labTestAttributes) {
				attributes.add(saveLabTestAttribute(labTestAttribute));
			}
			savedLabTest.setAttributes(attributes);
		}
		return savedLabTest;
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public LabTestAttribute saveLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		return dao.saveLabTestAttribute(labTestAttribute);
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_PRIVILEGE)
	@Transactional
	public List<LabTestAttribute> saveLabTestAttributes(List<LabTestAttribute> labTestAttributes) throws APIException {
		for (LabTestAttribute labTestAttribute : labTestAttributes) {
			saveLabTestAttribute(labTestAttribute);
		}
		return labTestAttributes;
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public LabTestAttributeType saveLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		return dao.saveLabTestAttributeType(labTestAttributeType);
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public LabTestSample saveLabTestSample(LabTestSample labTestSample) throws APIException {
		return dao.saveLabTestSample(labTestSample);
	}
	
	@Authorized(LabConfig.ADD_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public LabTestType saveLabTestType(LabTestType labTestType) throws APIException {
		handleUnknownTestTypeOperation(labTestType);
		return dao.saveLabTestType(labTestType);
	}
	
	private void handleUnknownTestTypeOperation(LabTestType labTestType) {
		if (labTestType.getUuid().equals(LabTestType.UNKNOWN_TEST_UUID)) {
			throw new UnchangeablePropertyException("The LabTestType: UNKNOWN " + LabTestType.UNKNOWN_TEST_UUID
			        + " is mandatory, and cannot be altered.");
		}
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void unretireLabTestType(LabTestType labTestType) throws APIException {
		labTestType.setRetired(Boolean.FALSE);
		labTestType.setRetireReason("Previously retired for reason: " + labTestType.getRetireReason());
		handleUnknownTestTypeOperation(labTestType);
		dao.saveLabTestType(labTestType);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void unretireLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		labTestAttributeType.setRetired(Boolean.FALSE);
		labTestAttributeType.setRetireReason("Previously retired for reason: " + labTestAttributeType.getRetireReason());
		dao.saveLabTestAttributeType(labTestAttributeType);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void voidLabTestAttribute(LabTestAttribute labTestAttribute, String voidReason) throws APIException {
		labTestAttribute.setVoided(true);
		labTestAttribute.setVoidedBy(Context.getAuthenticatedUser());
		labTestAttribute.setVoidReason(voidReason);
		labTestAttribute.setDateVoided(new Date());
		dao.saveLabTestAttribute(labTestAttribute);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void voidLabTestAttributes(LabTest labTest, String voidReason) throws APIException {
		List<LabTestSample> labTestSamples = getLabTestSamples(labTest, true);
		for (LabTestSample labTestSample : labTestSamples) {
			if (labTestSample.getStatus() == LabTestSampleStatus.PROCESSED) {
				labTestSample.setStatus(LabTestSampleStatus.COLLECTED);
				labTestSample = dao.saveLabTestSample(labTestSample);
			}
		}
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public void voidLabTestSample(LabTestSample labTestSample, String voidReason) throws APIException {
		labTestSample.setVoided(true);
		labTestSample.setVoidedBy(Context.getAuthenticatedUser());
		labTestSample.setVoidReason(voidReason);
		labTestSample.setDateVoided(new Date());
		dao.saveLabTestSample(labTestSample);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void unvoidLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		labTestAttribute.setVoided(false);
		labTestAttribute.setVoidReason("Previously voided for reason: " + labTestAttribute.getVoidReason());
		dao.saveLabTestAttribute(labTestAttribute);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_SAMPLE_PRIVILEGE)
	@Transactional
	public void unvoidLabTestSample(LabTestSample labTestSample) throws APIException {
		labTestSample.setVoided(false);
		labTestSample.setVoidReason("Previously voided for reason: " + labTestSample.getVoidReason());
		dao.saveLabTestSample(labTestSample);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTest(LabTest labTest) throws APIException {
		dao.purgeLabTest(labTest);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTestAttribute(LabTestAttribute labTestAttribute) throws APIException {
		dao.purgeLabTestAttribute(labTestAttribute);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestAttributeType(LabTestAttributeType labTestAttributeType) throws APIException {
		deleteLabTestAttributeType(labTestAttributeType, false);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_PRIVILEGE)
	@Transactional
	public void deleteLabTestSample(LabTestSample labTestSample) throws APIException {
		dao.purgeLabTestSample(labTestSample);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestType(LabTestType labTestType) throws APIException {
		deleteLabTestType(labTestType, null);
	}
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
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
	
	@Authorized(LabConfig.DELETE_LAB_TEST_METADATA_PRIVILEGE)
	@Transactional
	public void deleteLabTestType(LabTestType labTestType, LabTestType newObjectForCascade) throws APIException {
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
		handleLabTestTypeDependencies(labTestType, newObjectForCascade, message.toString());
		dao.purgeLabTestType(labTestType);
	}
	
	private void handleLabTestTypeDependencies(LabTestType labTestType, LabTestType newObjectForCascade, String voidMessage) {
		List<LabTest> labTests = getLabTests(labTestType, true);
		if (labTests != null) {
			for (LabTest labTest : labTests) {
				labTest.setLabTestType(newObjectForCascade);
				saveLabTest(labTest);
				voidLabTest(labTest, voidMessage);
			}
		}
		List<LabTestAttributeType> testAttributeTypes = getLabTestAttributeTypes(labTestType, true);
		if (testAttributeTypes != null) {
			for (LabTestAttributeType attributeType : testAttributeTypes) {
				attributeType.setLabTestType(newObjectForCascade);
				saveLabTestAttributeType(attributeType);
				retireLabTestAttributeType(attributeType, voidMessage);
			}
		}
	}
}
