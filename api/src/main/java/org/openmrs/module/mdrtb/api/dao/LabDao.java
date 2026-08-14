/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mdrtb.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author owais.hussain@ihsinformatics.com
 */
@Repository("mdrtb.LabDao")
public class LabDao {
	
	private static final int MAX_FETCH_LIMIT = 100;
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} objects
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestAttributeType> getAllLabTestAttributeTypes(boolean includeRetired) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttributeType.class);
		criteria.addOrder(Order.asc("name"));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	/**
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} objects
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestType> getAllLabTestTypes(boolean includeRetired) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestType.class);
		criteria.addOrder(Order.asc("name"));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	/**
	 * @param name the name of lab test type
	 * @param shortName the short name
	 * @param testGroup the {@link LabTestGroup} object
	 * @param referenceConcept the {@link Concept} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} objects
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestType> getLabTestTypes(String name, String shortName, LabTestGroup testGroup,
	        Concept referenceConcept, boolean includeRetired) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestType.class);
		if (name != null) {
			criteria.add(Restrictions.ilike("name", name, MatchMode.START));
		}
		if (shortName != null) {
			criteria.add(Restrictions.ilike("shortName", name, MatchMode.START));
		}
		if (testGroup != null) {
			criteria.add(Restrictions.eq("testGroup", testGroup));
		}
		if (referenceConcept != null) {
			criteria.add(Restrictions.ilike("referenceConcept", referenceConcept));
		}
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name")).addOrder(Order.asc("retired")).list();
		return criteria.list();
	}
	
	/**
	 * @param order the {@link org.openmrs.Order} object
	 * @return {@link LabTest} object by matching given {@link org.openmrs.Order} object
	 */
	public LabTest getLabTest(org.openmrs.Order order) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTest.class);
		criteria.add(Restrictions.eq("testOrderId", order.getId()));
		return (LabTest) criteria.uniqueResult();
	}
	
	/**
	 * @param labTestId the Id
	 * @return {@link LabTest} object
	 */
	public LabTest getLabTest(Integer labTestId) {
		return (LabTest) sessionFactory.getCurrentSession().get(LabTest.class, labTestId);
	}
	
	/**
	 * @param labTestAttributeId the Id
	 * @return {@link LabTestAttribute} object
	 */
	public LabTestAttribute getLabTestAttribute(Integer labTestAttributeId) {
		return (LabTestAttribute) sessionFactory.getCurrentSession().get(LabTestAttribute.class, labTestAttributeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestAttribute} object
	 */
	public LabTestAttribute getLabTestAttributeByUuid(String uuid) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttribute.class);
		criteria.add(Restrictions.eq("uuid", uuid.toLowerCase()));
		return (LabTestAttribute) criteria.uniqueResult();
	}
	
	/**
	 * @param testOrderId the Id
	 * @return {@link LabTestAttribute} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestAttribute> getLabTestAttributes(Integer testOrderId) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttribute.class);
		criteria.add(Restrictions.eq("labTest.testOrderId", testOrderId));
		return criteria.list();
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param valueReference the reference value
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestAttribute} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, String valueReference,
	        Date from, Date to, boolean includeVoided) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttribute.class);
		if (labTestAttributeType != null) {
			criteria.add(Restrictions.eqOrIsNull("attributeType.labTestAttributeTypeId", labTestAttributeType.getId()));
		}
		if (valueReference != null) {
			criteria.add(Restrictions.ilike("valueReference", valueReference, MatchMode.START));
		}
		if (from != null && to != null) {
			criteria.add(Restrictions.between("dateCreated", from, to));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		return criteria.addOrder(Order.asc("labTestAttributeId")).addOrder(Order.asc("voided")).list();
	}
	
	/**
	 * @param patient the {@link Patient} object
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestAttribute} object(s)
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, LabTestAttributeType labTestAttributeType,
	        boolean includeVoided) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("from LabTestAttribute lta where lta.labTest.order.patient.patientId = :patientId");
		queryString.append(labTestAttributeType == null ? ""
		        : " and lta.labTestAttributeType.labTestAttributeTypeId = :labTestAttributeType");
		queryString.append(includeVoided ? "" : " and lta.voided = :voided");
		Query query = sessionFactory.getCurrentSession().createQuery(queryString.toString());
		query.setInteger("patientId", patient.getPatientId());
		if (labTestAttributeType != null) {
			query.setInteger("labTestAttributeTypeId", labTestAttributeType.getId());
		}
		if (!includeVoided) {
			query.setBoolean("voided", false);
		}
		return query.list();
	}
	
	/**
	 * @param labTestAttributeTypeId the Id
	 * @return {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType getLabTestAttributeType(Integer labTestAttributeTypeId) {
		return (LabTestAttributeType) sessionFactory.getCurrentSession().get(LabTestAttributeType.class,
		    labTestAttributeTypeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType getLabTestAttributeTypeByUuid(String uuid) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttributeType.class);
		criteria.add(Restrictions.eq("uuid", uuid.toLowerCase()));
		return (LabTestAttributeType) criteria.uniqueResult();
	}
	
	/**
	 * @param name the name
	 * @param datatypeClassname the name of fully specified data type class
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestAttributeType> getLabTestAttributeTypes(String name, String datatypeClassname, boolean includeRetired) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttributeType.class);
		if (name != null) {
			criteria.add(Restrictions.ilike("name", name, MatchMode.START));
		}
		
		if (datatypeClassname != null) {
			criteria.add(Restrictions.eq("datatypeClassname", datatypeClassname));
		}
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name")).addOrder(Order.asc("retired")).list();
		
		return criteria.list();
	}
	
	/**
	 * @param labTestType {@link LabTestType} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestAttributeType> getLabTestAttributeTypes(LabTestType labTestType, boolean includeRetired) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestAttributeType.class);
		criteria.add(Restrictions.eq("labTestType", labTestType));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("sortWeight")).addOrder(Order.asc("retired")).list();
		return criteria.list();
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTest} object
	 */
	public LabTest getLabTestByUuid(String uuid) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTest.class);
		criteria.add(Restrictions.eq("uuid", uuid.toLowerCase()));
		return (LabTest) criteria.uniqueResult();
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object
	 * @param patient the {@link Patient} object
	 * @param orderNumber the order number
	 * @param referenceNumber the reference number
	 * @param orderConcept the {@link org.openmrs.Order} concept object
	 * @param orderer the {@link Provider} object
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTest} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTest> getLabTests(LabTestType labTestType, Patient patient, String orderNumber, String referenceNumber,
	        Concept orderConcept, Provider orderer, Date from, Date to, boolean includeVoided) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTest.class);
		criteria.createAlias("order", "o");
		if (labTestType != null) {
			criteria.add(Restrictions.eq("labTestType", labTestType));
		}
		if (patient != null) {
			criteria.add(Restrictions.eq("o.patient.id", patient.getPatientId()));
		}
		if (orderNumber != null) {
			criteria.add(Restrictions.ilike("o.orderReference", orderNumber, MatchMode.START));
		}
		if (orderConcept != null) {
			criteria.add(Restrictions.eq("o.concept.conceptId", orderConcept.getConceptId()));
		}
		if (orderer != null) {
			criteria.add(Restrictions.eq("o.orderer.providerId", orderer.getProviderId()));
		}
		if (referenceNumber != null) {
			criteria.add(Restrictions.ilike("labReferenceNumber", referenceNumber, MatchMode.START));
		}
		if (from != null && to != null) {
			criteria.add(Restrictions.between("dateCreated", from, to));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("o.voided", false));
		}
		criteria.addOrder(Order.asc("testOrderId")).addOrder(Order.asc("voided")).list();
		return criteria.list();
	}
	
	/**
	 * @param labTestSampleId the generated Id
	 * @return {@link LabTestSample} object
	 */
	public LabTestSample getLabTestSample(Integer labTestSampleId) {
		return (LabTestSample) sessionFactory.getCurrentSession().get(LabTestSample.class, labTestSampleId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestSample} object
	 */
	public LabTestSample getLabTestSampleByUuid(String uuid) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		criteria.add(Restrictions.eq("uuid", uuid.toLowerCase()));
		return (LabTestSample) criteria.uniqueResult();
	}
	
	/**
	 * @param labTest the {@link LabTest} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestSample> getLabTestSamples(LabTest labTest, boolean includeVoided) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		criteria.add(Restrictions.eq("labTest.testOrderId", labTest.getId()));
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		criteria.addOrder(Order.asc("sampleIdentifier")).addOrder(Order.asc("voided")).list();
		return criteria.list();
	}
	
	/**
	 * @param patient the {@link Patient} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LabTestSample> getLabTestSamples(Patient patient, boolean includeVoided) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		
		criteria.createAlias("labTest", "labTest", CriteriaSpecification.INNER_JOIN)
		        .setFetchMode("labTest", FetchMode.JOIN)
		        // .add(Restrictions.eq("labTest.order.patient.personId",
		        // patient.getPatientId()))
		        .createAlias("labTest.order", "order", CriteriaSpecification.INNER_JOIN)
		        .setFetchMode("order", FetchMode.JOIN)
		        .add(Restrictions.eq("order.patient.personId", patient.getPatientId()));
		// .createAlias("labTest", "labTest",
		// CriteriaSpecification.INNER_JOIN).setFetchMode("labTest", FetchMode.JOIN);
		// criteria.add(Restrictions.eq("order.patient.patientId",
		// patient.getPatientId()));
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		criteria.addOrder(Order.asc("sampleIdentifier")).addOrder(Order.asc("voided")).list();
		return criteria.list();
	}
	
	/**
	 * @param collector the {@link Provider} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	@SuppressWarnings("unchecked")
	public List<LabTestSample> getLabTestSamples(Provider collector, boolean includeVoided) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		criteria.add(Restrictions.eq("collector.providerId", collector.getProviderId()));
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		criteria.addOrder(Order.asc("sampleIdentifier")).addOrder(Order.asc("voided")).list();
		return criteria.list();
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
	@SuppressWarnings("unchecked")
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, String sampleIdentifier,
	        Concept specimenType, LabTestSampleStatus status, Provider collector, Date from, Date to, boolean includeVoided) {
		if (labTest == null && patient == null && sampleIdentifier == null) {
			return null;
		}
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		if (labTest != null) {
			criteria.add(Restrictions.eq("labTest.testOrderId", labTest.getTestOrderId()));
		}
		if (patient != null) {
			criteria.createAlias("labTest", "labTest").setFetchMode("labTest", FetchMode.JOIN)
			        .createAlias("labTest.order", "order").setFetchMode("order", FetchMode.JOIN)
			        .add(Restrictions.eq("order.patient.personId", patient.getPatientId()));
		}
		if (sampleIdentifier != null) {
			criteria.add(Restrictions.ilike("sampleIdentifier", sampleIdentifier, MatchMode.START));
		}
		if (specimenType != null) {
			criteria.add(Restrictions.eq("specimenType.conceptId", specimenType.getConceptId()));
		}
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		if (collector != null) {
			criteria.add(Restrictions.eq("collector.providerId", collector.getProviderId()));
		}
		if (from != null && to != null) {
			criteria.add(Restrictions.between("dateCreated", from, to));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		criteria.addOrder(Order.asc("sampleIdentifier"));
		return criteria.list();
	}
	
	/**
	 * @param labTestTypeId the generated Id
	 * @return {@link LabTestType} object
	 */
	public LabTestType getLabTestType(Integer labTestTypeId) {
		return (LabTestType) sessionFactory.getCurrentSession().get(LabTestType.class, labTestTypeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestType} object
	 */
	public LabTestType getLabTestTypeByUuid(String uuid) {
		@SuppressWarnings("deprecation")
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestType.class);
		criteria.add(Restrictions.eq("uuid", uuid.toLowerCase()));
		return (LabTestType) criteria.uniqueResult();
	}
	
	/**
	 * Returns a list of 'n' number of {@link LabTest} objects. If firstNObjects is true, then
	 * earliest 'n' objects are returned; if lastNObjects is true, then latest 'n' objects are
	 * returned. If both a true, then a union of both results is returned. Maximum number of objects
	 * to return is limited by MAX_FETCH_LIMIT
	 * 
	 * @param patient the {@link Patient} object
	 * @param n the number of objects to return
	 * @param firstNObjects whether to return initial n objects
	 * @param lastNObjects whether to return last n objects
	 * @param includeVoided include retired objects
	 * @return {@link LabTest} object
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LabTest> getNLabTests(Patient patient, int n, boolean firstNObjects, boolean lastNObjects,
	        boolean includeVoided) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTest.class);
		List<LabTest> firstN = null;
		List<LabTest> lastN = null;
		// Disallow fetching more than 100 records per query
		criteria.setMaxResults(n > MAX_FETCH_LIMIT ? MAX_FETCH_LIMIT : n);
		if (patient != null) {
			criteria.createAlias("order", "o", CriteriaSpecification.INNER_JOIN).setFetchMode("o", FetchMode.JOIN)
			        .add(Restrictions.eq("o.patient.personId", patient.getPatientId()));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		if (firstNObjects) {
			criteria.addOrder(Order.asc("dateCreated"));
			firstN = criteria.list();
		}
		if (lastNObjects) {
			criteria.addOrder(Order.desc("dateCreated"));
			lastN = criteria.list();
		}
		List<LabTest> list = new ArrayList<LabTest>();
		if (firstN != null) {
			list.addAll(firstN);
		}
		if (lastN != null) {
			list.addAll(lastN);
		}
		return list;
	}
	
	/**
	 * Returns a list of 'n' number of {@link LabTestSample} objects by matching {@link Patient} and
	 * {@link LabTestSampleStatus} (optional, pass null to ignore). If firstNObjects is true, then
	 * earliest 'n' objects are returned; if lastNObjects is true, then latest 'n' objects are
	 * returned. If both a true, then a union of both results is returned. Maximum number of objects
	 * to return is limited by MAX_FETCH_LIMIT
	 * 
	 * @param patient the {@link Patient} object
	 * @param status the {@link LabTestSampleStatus} object
	 * @param n the number of objects to return
	 * @param firstNObjects whether to return initial n objects
	 * @param lastNObjects whether to return last n objects
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LabTestSample> getNLabTestSamples(Patient patient, LabTestSampleStatus status, int n, boolean firstNObjects,
	        boolean lastNObjects, boolean includeVoided) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LabTestSample.class);
		List<LabTestSample> firstN = null;
		List<LabTestSample> lastN = null;
		// Disallow fetching more than 100 records per query
		criteria.setMaxResults(n > MAX_FETCH_LIMIT ? MAX_FETCH_LIMIT : n);
		criteria.createAlias("labTest", "labTest", CriteriaSpecification.INNER_JOIN).setFetchMode("labTest", FetchMode.JOIN)
		        .createAlias("labTest.order", "order", CriteriaSpecification.INNER_JOIN)
		        .setFetchMode("order", FetchMode.JOIN)
		        .add(Restrictions.eq("order.patient.personId", patient.getPatientId()));
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		if (firstNObjects) {
			criteria.addOrder(Order.asc("dateCreated"));
			firstN = criteria.list();
		}
		if (lastNObjects) {
			criteria.addOrder(Order.desc("dateCreated"));
			lastN = criteria.list();
		}
		criteria.setMaxResults(n);
		List<LabTestSample> list = new ArrayList<LabTestSample>();
		if (firstN != null) {
			list.addAll(firstN);
		}
		if (lastN != null) {
			list.addAll(lastN);
		}
		return list;
	}
	
	/**
	 * @param labTest the {@link LabTest} object to delete
	 */
	public void purgeLabTest(LabTest labTest) {
		sessionFactory.getCurrentSession().delete(labTest);
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to delete
	 */
	public void purgeLabTestAttribute(LabTestAttribute labTestAttribute) {
		sessionFactory.getCurrentSession().delete(labTestAttribute);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to delete
	 */
	public void purgeLabTestAttributeType(LabTestAttributeType labTestAttributeType) {
		sessionFactory.getCurrentSession().delete(labTestAttributeType);
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to delete
	 */
	public void purgeLabTestSample(LabTestSample labTestSample) {
		sessionFactory.getCurrentSession().delete(labTestSample);
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to delete
	 */
	public void purgeLabTestType(LabTestType labTestType) {
		sessionFactory.getCurrentSession().delete(labTestType);
	}
	
	/**
	 * Detects whether it's a new order or existing one. In case the order already exits, it is NOT
	 * overridden because Order objects are immutable
	 * 
	 * @param order the {@link org.openmrs.Order} object to save
	 * @return saved {@link org.openmrs.Order} object
	 */
	public org.openmrs.Order saveLabTestOrder(org.openmrs.Order order) {
		// NOTE: do not call order.getOrderType().setJavaClassName(...) here. That object is a managed
		// Hibernate entity, so writing to it rewrites the shared order_type row for the whole system.
		// The Test Order type is aligned with org.openmrs.Order by the
		// mdrtb-2026-08-13-test-order-java-class changeset in liquibase.xml instead.
		boolean createNew = order.getId() == null;
		if (!createNew) {
			// See if the given ID actually exists or not
			createNew = Context.getOrderService().getOrder(order.getId()) == null;
		}
		if (createNew) {
			order.setId(null);
			return Context.getOrderService().saveOrder(order, null);
		}
		// Do nothing
		return order;
	}
	
	/**
	 * Persists {@link LabTest} in database. This method also persists {@link org.openmrs.Order}
	 * entity, because unlike {@link org.openmrs.Order}, the {@link LabTest} is not hierarchical
	 * 
	 * @param labTest the {@link LabTest} object to save
	 * @return saved {@link LabTest} object
	 */
	public LabTest saveLabTest(LabTest labTest) {
		org.openmrs.Order savedOrder = saveLabTestOrder(labTest.getOrder());
		labTest.setOrder(savedOrder);
		labTest.setTestOrderId(savedOrder.getOrderId());
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(labTest);
		return labTest;
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to save
	 * @return saved {@link LabTestAttribute} object
	 */
	public LabTestAttribute saveLabTestAttribute(LabTestAttribute labTestAttribute) {
		
		sessionFactory.getCurrentSession().saveOrUpdate(labTestAttribute);
		return labTestAttribute;
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to save
	 * @return saved {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType saveLabTestAttributeType(LabTestAttributeType labTestAttributeType) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestAttributeType);
		return labTestAttributeType;
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to save
	 * @return saved {@link LabTestSample} object
	 */
	public LabTestSample saveLabTestSample(LabTestSample labTestSample) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestSample);
		return labTestSample;
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to save
	 * @return saved {@link LabTestType} object
	 */
	public LabTestType saveLabTestType(LabTestType labTestType) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestType);
		return labTestType;
	}
}
