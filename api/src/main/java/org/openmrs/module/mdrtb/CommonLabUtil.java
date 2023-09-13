package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.Order.Urgency;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.commonlabtest.LabTestAttributeType;
import org.openmrs.module.commonlabtest.LabTestGroup;
import org.openmrs.module.commonlabtest.LabTestSample;
import org.openmrs.module.commonlabtest.LabTestSampleStatus;
import org.openmrs.module.commonlabtest.LabTestType;
import org.openmrs.module.commonlabtest.api.CommonLabTestService;
import org.openmrs.module.mdrtb.specimen.DstTestType;

public class CommonLabUtil {
	
	protected static final Log log = LogFactory.getLog(CommonLabUtil.class);
	
	List<LabTestType> types = new ArrayList<>();
	
	List<LabTestAttributeType> commonAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> xpertAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> cultureAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> hainAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> hain2AttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> smearAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> dstMgitAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> dstLjAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> dst2AttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> dst2MgitAttributeTypes = new ArrayList<>();
	
	List<LabTestAttributeType> dst2LjAttributeTypes = new ArrayList<>();
	
	private CommonLabTestService service;
	
	public static CommonLabUtil getService() {
		return new CommonLabUtil();
	}
	
	public CommonLabTestService getCommonLabService() {
		return service;
	}
	
	public CommonLabUtil() {
		// Try to Initialize service
		if (service == null) {
			try {
				service = Context.getService(CommonLabTestService.class);
				if (service != null) {
					if (types.isEmpty()) {
						types = service.getAllLabTestTypes(false);
					}
					// If the prior does not work, fallback to SQL
					if (types == null) {
						types = getLabTestTypesViaSql();
					}
				}
			}
			catch (Exception e) {
				log.debug("Common Lab service may not have initialized!" + e.getMessage());
			}
			// Fetch all attribuet types for DST tests
			List<LabTestAttributeType> attributeTypes = service.getAllLabTestAttributeTypes(false);
			// Attach attributes for MDRTB test type
			for (LabTestAttributeType at : attributeTypes) {
				if (at.getLabTestType().getUuid().equals(MdrtbConstants.MDRTB_TEST_TYPE_UUID)) {
					if (at.getGroupName() == null) {
						commonAttributeTypes.add(at);
					} 
					else {
						switch (at.getGroupName().toUpperCase()) {
							case "XPERT":
								xpertAttributeTypes.add(at);
								break;
							case "HAIN":
								hainAttributeTypes.add(at);
								break;
							case "HAIN2":
								hain2AttributeTypes.add(at);
								break;
							case "CULTURE":
								cultureAttributeTypes.add(at);
								break;
							case "SMEAR":
								smearAttributeTypes.add(at);
								break;
						}
					}
				}
			}
			for (LabTestAttributeType at : attributeTypes) {
				if (at.getLabTestType().getName().equalsIgnoreCase(MdrtbConstants.DST2_TEST_NAME)) {
					dst2AttributeTypes.add(at);
				}
				if (at.getLabTestType().getName().equalsIgnoreCase(MdrtbConstants.DST2_MGIT_TEST_NAME)) {
					dst2MgitAttributeTypes.add(at);
				}
				if (at.getLabTestType().getName().equalsIgnoreCase(MdrtbConstants.DST2_LJ_TEST_NAME)) {
					dst2LjAttributeTypes.add(at);
				}
			}
		}
	}

	private List<LabTestType> getLabTestTypesViaSql() {
		String sql = "select test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from commonlabtest_type from ";
		List<List<Object>> list = Context.getAdministrationService().executeSQL(sql, true);
		List<LabTestType> testTypes = new ArrayList<>();
		for (List<Object> row : list) {
			LabTestType type = new LabTestType();
			type.setId((Integer) row.get(0));
			type.setName((String) row.get(1));
			type.setShortName((String) row.get(3));
			type.setTestGroup(LabTestGroup.valueOf((String) row.get(4)));
			type.setRequiresSpecimen((Boolean) row.get(5));
			testTypes.add(type);
		}
		return testTypes;
	}

	public List<LabTest> getLabTests(Patient patient) {
		if (patient == null) {
			return null;
		}
		return service.getLabTests(patient, false);
	}

	public List<LabTest> getLabTests(Patient patient, LabTestType labTestType) {
		if (patient == null) {
			return null;
		}
		return service.getLabTests(labTestType, patient, null, null, null, null, null, null, false);
	}

	public LabTestType getMdrtbTestType() {
		LabTestType testType = service.getLabTestTypeByUuid(MdrtbConstants.MDRTB_TEST_TYPE_UUID);
		return testType;
	}
	
	public LabTestType getDst2TestType() {
		List<LabTestType> testType = service.getLabTestTypes(MdrtbConstants.DST2_TEST_NAME, null, null, null, null, false);
		return testType.isEmpty() ? null : testType.get(0);
	}

	public LabTestType getDst2MgitTestType() {
		List<LabTestType> testType = service.getLabTestTypes(MdrtbConstants.DST2_MGIT_TEST_NAME, null, null, null, null, false);
		return testType.isEmpty() ? null : testType.get(0);
	}

	public LabTestType getDst2LjTestType() {
		List<LabTestType> testType = service.getLabTestTypes(MdrtbConstants.DST2_LJ_TEST_NAME, null, null, null, null, false);
		return testType.isEmpty() ? null : testType.get(0);
	}

	public LabTestSample getMostRecentAcceptedSample(Integer labTestId) {
		LabTest test = service.getLabTest(labTestId);
		return getMostRecentAcceptedSample(test);
	}
	
	public LabTestSample getMostRecentAcceptedSample(LabTest labTest) {
		List<LabTestSample> samples = service.getLabTestSamples(labTest, false);
		LabTestSample mostRecent = null;
		for (LabTestSample labTestSample : samples) {
			if (labTestSample.getStatus() == LabTestSampleStatus.ACCEPTED) {
				if (mostRecent == null) {
					mostRecent = labTestSample;					
				} else {
					if (mostRecent.getDateCreated().before(labTestSample.getDateCreated())) {
						mostRecent = labTestSample;					
					}
				}
			}
		}
		return mostRecent;
	}
	
	public LabTestSample getMostRecentProcessedSample(Integer labTestId) {
		LabTest test = service.getLabTest(labTestId);
		return getMostRecentAcceptedSample(test);
	}
	
	public LabTestSample getMostRecentProcessedSample(LabTest labTest) {
		LabTestSample sample = labTest.getLabTestSample(LabTestSampleStatus.PROCESSED);
		return sample;
	}
	
	public LabTestAttributeType getLabTestAttributeTypeByName(String name) {
		List<LabTestAttributeType> list = service.getLabTestAttributeTypes(name, null, false);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public LabTestAttribute getCommonAttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, commonAttributeTypes);
	}
	
	public LabTestAttribute getXpertAttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, xpertAttributeTypes);
	}
	
	public LabTestAttribute getCultureAttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, cultureAttributeTypes);
	}
	
	public LabTestAttribute getHainAttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, hainAttributeTypes);
	}
	
	public LabTestAttribute getHain2AttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, hain2AttributeTypes);
	}
	
	public LabTestAttribute getSmearAttributeByTestAndName(LabTest labTest, String name) {
		return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, smearAttributeTypes);
	}
	
	public LabTestAttribute getDstAttributeByTestAndName(LabTest labTest, String name, DstTestType dstTestType) {
		switch (dstTestType) {
			case DST1_LJ:
				return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, dstLjAttributeTypes);
			case DST1_MGIT:
				return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, dstMgitAttributeTypes);
			case DST1: //TODO: Check if there exists a DST already
			case DST2:
				return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, dst2AttributeTypes);
			case DST2_LJ:
				return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, dst2LjAttributeTypes);
			case DST2_MGIT:
				return getAttributeByTestAndNameFromAttributeTypeSubset(labTest, name, dst2MgitAttributeTypes);
		}
		return null;
	}
	
	private LabTestAttribute getAttributeByTestAndNameFromAttributeTypeSubset(LabTest labTest, String name,
	        List<LabTestAttributeType> attributeTypeSubset) {
		Collection<LabTestAttribute> attributes = labTest.getActiveAttributes();
		LabTestAttributeType targetAttributeType = null;
		for (LabTestAttributeType type : attributeTypeSubset) {
			if (type.getName().equalsIgnoreCase(name)) {
				targetAttributeType = type;
			}
		}
		for (LabTestAttribute attribute : attributes) {
			if (attribute.getAttributeType().equals(targetAttributeType)) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * Searches for a Lab Test order against given {@link Encounter} and creates if one doesn't
	 * exist, otherwise returns existing one.
	 * 
	 * @param encounter
	 * @return
	 */
	@SuppressWarnings("null")
	public LabTest createLabTestOrder(Encounter encounter, LabTestType labTestType) {
		// Check if an order already exists
		Set<Order> orders = encounter.getOrders();
		if (orders.isEmpty()) {
			Encounter target = null;
			List<Encounter> allEncounters = Context.getEncounterService().getEncountersByPatient(encounter.getPatient());
			for (Encounter enc : allEncounters) {
				if (enc.getEncounterType().equals(MdrtbConstants.ET_SPECIMEN_COLLECTION)) {
					if (target == null) {
						target = enc;
					} else {
						// Match the date, we're interested in the encounter closest to the function parameter
						long diff = Math.abs(encounter.getEncounterDatetime().getTime() - enc.getEncounterDatetime().getTime());
						if (diff < Math.abs(encounter.getEncounterDatetime().getTime() - target.getEncounterDatetime().getTime())) {
							target = enc;
						}
					}
				}
			}
			orders = target.getOrders();
		}
		CommonLabTestService labTestService = service;
		for (Order o : orders) {
			// Does this order have a LabTest object of given type?
			LabTest existing = labTestService.getLabTest(o.getOrderId());
			if (existing != null) {
				if (existing.getLabTestType().equals(labTestType)) {
					return existing;
				}
			}
		}
		// Otherwise create a new Order
		Order order = new Order();
		order.setEncounter(encounter);
		order.setAction(Action.NEW);
		OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID);
		order.setOrderType(orderType);
		order.setUrgency(Urgency.ROUTINE);
		LabTest labTest = new LabTest(order);
		labTest.setLabTestType(labTestType);
		return labTest;
	}
}
