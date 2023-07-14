set foreign_key_checks = 0;

-- Common Lab Module
-- Set the UUID for Specimen Type and Site
update global_property set property_value = '31bf065e-0370-102d-b0e3-001ec94a0cc1' where property = 'commonlabtest.specimenSiteConceptUuid';
update global_property set property_value = '2da61322-bcc5-4c32-b412-1b1ef37f4a25' where property = 'commonlabtest.specimenTypeConceptUuid';

/* CommonLabTest migration start */
truncate commonlabtest_type;
insert into commonlabtest_type (test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid)  
select test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _commonlabtest_type ;

truncate commonlabtest_attribute_type;
insert into commonlabtest_attribute_type (test_attribute_type_id, test_type_id, name, datatype, min_occurs, max_occurs, datatype_config, handler_config, sort_weight, description, preferred_handler, hint, group_name, multiset_name, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select test_attribute_type_id, test_type_id, name, datatype, min_occurs, max_occurs, datatype_config, handler_config, sort_weight, description, preferred_handler, hint, group_name, multiset_name, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _commonlabtest_attribute_type;
/* CommonLabTest migration end */

-- Create temporary tables to separate out the results
-- create table _temp 
select e.encounter_id, e.encounter_type, et.name as encounter_name, e.encounter_datetime, e.patient_id, o.obs_id, o.concept_id, cn.name as obs_question, o.value_coded, ocn.name as value_coded_name, o.value_numeric, o.value_text, o.value_datetime from encounter e 
inner join encounter_type et on et.encounter_type_id = e.encounter_type 
inner join obs o on o.encounter_id = e.encounter_id 
inner join concept_name cn on cn.concept_id = o.concept_id and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED'
left join concept_name ocn on ocn.concept_id = o.value_coded and ocn.locale = 'en' and ocn.concept_name_type = 'FULLY_SPECIFIED'
;

-- Have a look at what constructs exist
select distinct obs_question from _temp 
where value_coded is null and value_numeric is null and value_text is null and value_datetime is null;

create table if not exists _temp_xpert select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS XPERT TEST CONSTRUCT');
create table if not exists _temp_culture select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS CULTURE CONSTRUCT');
create table if not exists _temp_hain select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'TUBERCULOSIS HAIN TEST CONSTRUCT');
create table if not exists _temp_microscopy select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'MICROSCOPY TEST CONSTRUCT');
create table if not exists _temp_hain2 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'HAIN 2 TEST CONSTRUCT');
create table if not exists _temp_dst1 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 CONSTRUCT');
create table if not exists _temp_dst1_mgit select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 MGIT CONSTRUCT');
create table if not exists _temp_dst1_lj select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST1 LJ CONSTRUCT');
create table if not exists _temp_dst2 select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 CONSTRUCT');
create table if not exists _temp_dst2_mgit select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 MGIT CONSTRUCT');
create table if not exists _temp_dst2_lj select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'DST2 LJ CONSTRUCT');
create table if not exists _temp_mgit_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'MGIT RESULT TEMPLATE');
create table if not exists _temp_lj_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'L-J RESULT TEMPLATE');
create table if not exists _temp_contaminated_template select * from _temp where encounter_id in (select encounter_id from _temp where obs_question = 'CONTAMINATED TUBES RESULT TEMPLATE');

-- Create table _lab_data, a denormalized form for all lab test Enc-Obs data
-- create table if not exists _lab_data 
select t.encounter_id, t.encounter_datetime, t.patient_id, 
group_concat(if(t.concept_id = 153, 'TRUE', null)) as TUBERCULOSIS_CULTURE_CONSTRUCT, 
group_concat(if(t.concept_id = 311, 'TRUE', null)) as TUBERCULOSIS_XPERT_TEST_CONSTRUCT, 
group_concat(if(t.concept_id = 323, 'TRUE', null)) as TUBERCULOSIS_HAIN_TEST_CONSTRUCT, 
group_concat(if(t.concept_id = 410, 'TRUE', null)) as MICROSCOPY_TEST_CONSTRUCT, 
group_concat(if(t.concept_id = 414, 'TRUE', null)) as HAIN_2_TEST_CONSTRUCT, 
group_concat(if(t.concept_id = 524, 'TRUE', null)) as MGIT_RESULT_TEMPLATE, 
group_concat(if(t.concept_id = 535, 'TRUE', null)) as LJ_RESULT_TEMPLATE, 
group_concat(if(t.concept_id = 539, 'TRUE', null)) as CONTAMINATED_TUBES_RESULT_TEMPLATE, 
group_concat(if(t.concept_id = 542, 'TRUE', null)) as DST1_MGIT_CONSTRUCT, 
group_concat(if(t.concept_id = 549, 'TRUE', null)) as DST1_CONSTRUCT, 
group_concat(if(t.concept_id = 552, 'TRUE', null)) as DST1_LJ_CONSTRUCT, 
group_concat(if(t.concept_id = 571, 'TRUE', null)) as DST2_CONSTRUCT, 
group_concat(if(t.concept_id = 572, 'TRUE', null)) as DST2_MGIT_CONSTRUCT, 
group_concat(if(t.concept_id = 573, 'TRUE', null)) as DST2_LJ_CONSTRUCT, 
group_concat(if(t.concept_id = 67, t.value_coded, null) ) as TUBERCULOSIS_SAMPLE_SOURCE, 
group_concat(if(t.concept_id = 68, t.value_datetime, null) ) as TUBERCULOSIS_TEST_RESULT_DATE, 
group_concat(if(t.concept_id = 152, t.value_coded, null)) as TUBERCULOSIS_CULTURE_RESULT, 
group_concat(if(t.concept_id = 174, t.value_coded, null)) as APPEARANCE_OF_SPECIMEN, 
group_concat(if(t.concept_id = 188, t.value_datetime, null)) as SPUTUM_COLLECTION_DATE, 
group_concat(if(t.concept_id = 312, t.value_coded, null)) as MTB_RESULT, 
group_concat(if(t.concept_id = 316, t.value_text, null)) as ERROR_CODE, 
group_concat(if(t.concept_id = 317, t.value_coded, null)) as RIFAMPICIN_RESISTANCE, 
group_concat(if(t.concept_id = 318, t.value_coded, null)) as XPERT_MTB_BURDEN, 
group_concat(if(t.concept_id = 322, t.value_coded, null)) as ISONIAZID_RESISTANCE, 
group_concat(if(t.concept_id = 400, t.value_coded, null)) as MICROSCOPY_RESULT, 
group_concat(if(t.concept_id = 401, t.value_coded, null)) as MICROSCOPY_SAMPLE_1_RESULT, 
group_concat(if(t.concept_id = 402, t.value_coded, null)) as MICROSCOPY_SAMPLE_2_RESULT, 
group_concat(if(t.concept_id = 403, t.value_coded, null)) as MICROSCOPY_SAMPLE_3_RESULT, 
group_concat(if(t.concept_id = 404, t.value_coded, null)) as MICROSCOPY_SAMPLE_2_APPEARANCE_1, 
group_concat(if(t.concept_id = 405, t.value_coded, null)) as MICROSCOPY_SAMPLE_2_APPEARANCE_2, 
group_concat(if(t.concept_id = 406, t.value_coded, null)) as MICROSCOPY_SAMPLE_3_APPEARANCE, 
group_concat(if(t.concept_id = 407, t.value_datetime, null)) as MICROSCOPY_SAMPLE_1_RESULT_DATE, 
group_concat(if(t.concept_id = 408, t.value_datetime, null)) as MICROSCOPY_SAMPLE_2_RESULT_DATE, 
group_concat(if(t.concept_id = 409, t.value_datetime, null)) as MICROSCOPY_SAMPLE_3_RESULT_DATE, 
group_concat(if(t.concept_id = 411, t.value_coded, null)) as E, 
group_concat(if(t.concept_id = 412, t.value_coded, null)) as KM_AM_CM, 
group_concat(if(t.concept_id = 413, t.value_coded, null)) as MOX_OFX, 
group_concat(if(t.concept_id = 423, t.value_datetime, null)) as DATE_OF_RESULT, 
group_concat(if(t.concept_id = 424, t.value_text, null)) as PERIPHERAL_LABORATORY_INVESTIGATION_NUMBER, 
group_concat(if(t.concept_id = 425, t.value_coded, null)) as PURPOSE_OF_INVESTIGATION, 
group_concat(if(t.concept_id = 426, t.value_coded, null)) as REQUESTING_MEDICAL_FACILITY, 
group_concat(if(t.concept_id = 427, t.value_datetime, null)) as INVESTIGATION_DATE, 
group_concat(if(t.concept_id = 428, t.value_text, null)) as LABORATORY_INVESTIGATION_NUMBER_, 
group_concat(if(t.concept_id = 497, t.value_text, null)) as REFERRED_BY, 
group_concat(if(t.concept_id = 498, t.value_text, null)) as REFERRING_FACILITY, 
group_concat(if(t.concept_id = 499, t.value_datetime, null)) as YEAR, 
group_concat(if(t.concept_id = 500, t.value_numeric, null)) as TB03, 
group_concat(if(t.concept_id = 501, t.value_text, null)) as REGIONAL_LABORATORY_NO, 
group_concat(if(t.concept_id = 502, t.value_datetime, null)) as DATE_OF_OBSERVED_GROWTH, 
group_concat(if(t.concept_id = 505, t.value_coded, null)) as H, 
group_concat(if(t.concept_id = 506, t.value_coded, null)) as R, 
group_concat(if(t.concept_id = 507, t.value_coded, null)) as XPERT_MTB_RIF, 
group_concat(if(t.concept_id = 508, t.value_datetime, null)) as DATE_OF_INOCULATION_OF_CULTURE, 
group_concat(if(t.concept_id = 509, t.value_numeric, null)) as LABORATORY_NO, 
group_concat(if(t.concept_id = 517, t.value_coded, null)) as MT_ID_TEST, 
group_concat(if(t.concept_id = 519, t.value_coded, null)) as MGIT_CULTURE_RESULT, 
group_concat(if(t.concept_id = 521, t.value_coded, null)) as TYPE_OF_CULTURE_REPORTED, 
group_concat(if(t.concept_id = 522, t.value_datetime, null)) as DATE_OF_REPORTING_CULTURE_RESULT, 
group_concat(if(t.concept_id = 523, t.value_datetime, null)) as DATE_OF_MGIT_GROWTH_OCCURRENCE, 
group_concat(if(t.concept_id = 532, t.value_coded, null)) as PLACE_OF_CULTURE, 
group_concat(if(t.concept_id = 533, t.value_datetime, null)) as DATE_OF_LJ_GROWTH_OCCURRENCE, 
group_concat(if(t.concept_id = 534, t.value_coded, null)) as LJ_CULTURE_RESULT, 
group_concat(if(t.concept_id = 536, t.value_datetime, null)) as DATE_OF_REPEATED_DECONTAMINATION_AND_2ND_INOCULATION, 
group_concat(if(t.concept_id = 537, t.value_datetime, null)) as DATE_OF_GROWTH_OCCURRENCE_2ND_DECONT, 
group_concat(if(t.concept_id = 538, t.value_coded, null)) as CULTURE_RESULT_2ND_DECONTAMINATION, 
group_concat(if(t.concept_id = 540, t.value_datetime, null)) as DATE_OF_READING_1ST_LINE_DST_RESULTS_ON_MGIT, 
group_concat(if(t.concept_id = 541, t.value_datetime, null)) as DATE_1ST_LINE_DST_INOCULATION_ON_MGIT, 
group_concat(if(t.concept_id = 544, t.value_coded, null)) as STREPTOMYCIN, 
group_concat(if(t.concept_id = 548, t.value_coded, null)) as PYRAZINAMIDE, 
group_concat(if(t.concept_id = 550, t.value_coded, null)) as TYPE_OF_DST_METHOD_REPORTED, 
group_concat(if(t.concept_id = 551, t.value_datetime, null)) as DATE_OF_REPORTING_1ST_LINE_DST_RESULT, 
group_concat(if(t.concept_id = 553, t.value_datetime, null)) as DATE_OF_1ST_LINE_DST_INOCULATION_ON_LJ, 
group_concat(if(t.concept_id = 554, t.value_datetime, null)) as DATE_OF_READING_1ST_LINE_DST_RESULTS_ON_LJ, 
group_concat(if(t.concept_id = 556, t.value_datetime, null)) as DATE_OF_2ND_LINE_DST_INOCULATION, 
group_concat(if(t.concept_id = 557, t.value_datetime, null)) as DATE_OF_READING_2ND_LINE_DST_RESULTS, 
group_concat(if(t.concept_id = 558, t.value_coded, null)) as OFLOXACIN_RESISTANCE, 
group_concat(if(t.concept_id = 559, t.value_coded, null)) as MOXIFLOXACIN_RESISTANCE, 
group_concat(if(t.concept_id = 560, t.value_coded, null)) as LEVOFLOXACIN_RESISTANCE, 
group_concat(if(t.concept_id = 561, t.value_coded, null)) as PROTHIONAMIDE_RESISTANCE, 
group_concat(if(t.concept_id = 562, t.value_coded, null)) as LINEZOLID_RESISTANCE, 
group_concat(if(t.concept_id = 563, t.value_coded, null)) as CLOFAZAMINE_RESISTANCE, 
group_concat(if(t.concept_id = 564, t.value_coded, null)) as BEDAQUILINE_RESISTANCE, 
group_concat(if(t.concept_id = 565, t.value_coded, null)) as DELAMANID_RESISTANCE, 
group_concat(if(t.concept_id = 566, t.value_coded, null)) as P_AMINOSALICY_RESISTANCE, 
group_concat(if(t.concept_id = 567, t.value_coded, null)) as CAPREOMYCIN_RESISTANCE, 
group_concat(if(t.concept_id = 568, t.value_coded, null)) as KANAMYCIN_RESISTANCE, 
group_concat(if(t.concept_id = 569, t.value_coded, null)) as AMIKACIN_RESISTANCE, 
group_concat(if(t.concept_id = 570, t.value_datetime, null)) as DATE_OF_REPORTING_2ND_LINE_DST_RESULT, 
group_concat(if(t.concept_id = 574, t.value_coded, null)) as DST1_LAB_LOCATION, 
group_concat(if(t.concept_id = 575, t.value_coded, null)) as DST2_LAB_LOCATION, 
group_concat(if(t.concept_id = 587, t.value_coded, null)) as PERIPHERAL_LAB_BIOLOGICAL_SPECIMEN, 
group_concat(if(t.concept_id = 588, t.value_text, null)) as PERIPHERAL_LAB, 
group_concat(if(t.concept_id = 589, t.value_datetime, null)) as DATE_OF_REQUEST_FOR_LABORATORY_INVESTIGATION, 
group_concat(if(t.concept_id = 607, t.value_datetime, null)) as DATE_OF_SENDING_TO_CULTURE, 
group_concat(if(t.concept_id = 608, t.value_datetime, null)) as DATE_OF_SENDING_TO_DST, 
group_concat(if(t.concept_id = 609, t.value_coded, null)) as SENT_TO_DST, 
group_concat(if(t.concept_id = 610, t.value_coded, null)) as SENT_TO_CULTURE, 
group_concat(if(t.concept_id = 611, t.value_text, null)) as LAB_SPECIALIST_NAME, 
'' as BLANK from `_temp` t 
group by t.encounter_id, t.encounter_datetime, t.patient_id;

truncate orders;
INSERT INTO orders (order_type_id,concept_id,orderer,encounter_id,instructions,date_activated,auto_expire_date,date_stopped,order_reason,order_reason_non_coded,creator,date_created,voided,voided_by,date_voided,void_reason,patient_id,accession_number,uuid,urgency,order_number,previous_order_id,order_action,comment_to_fulfiller,care_setting,scheduled_date,order_group_id,sort_weight,fulfiller_comment,fulfiller_status) VALUES
(1,410,3,147521,NULL,'2017-05-04 00:00:00',NULL,NULL,NULL,NULL,1,'2023-04-09 07:33:26',0,NULL,NULL,NULL,32072,NULL,'90ef1459-8b33-4a18-936e-2cfdfbe99649','ROUTINE','ORD-1',NULL,'NEW',NULL,1,NULL,NULL,NULL,NULL,NULL);


-- For each patient with a Test, create an Order from respective Lab Encounter (for now set the concept to Microscopy test)
insert into orders (order_type_id, concept_id, orderer, encounter_id, date_activated, creator, date_created, voided, voided_by, date_voided, void_reason, patient_id, uuid, urgency, order_number, order_action, care_setting) 
select 3 as order_type_id, 410 as concept_id, e.creator, e.encounter_id, e.encounter_datetime, p.provider_id as orderer, e.date_created, e.voided, e.voided_by, e.date_voided, e.voided_by, e.patient_id, UUID(), 'ROUTINE' as urgency, concat('ORD-', e.encounter_id) as order_number, 'NEW' as order_action, 1 as care_setting from encounter as e 
inner join users u on u.user_id = e.creator 
inner join provider p on p.person_id = u.person_id 
where e.encounter_type = 11;


-- Update the order and set the creator where respective user does not exist
update orders set creator = 1 where creator not in (select user_id from users);
update orders set voided_by = 1 where voided_by is not null and voided_by not in (select user_id from users);

/* Create Lab Test records for each Order */
truncate commonlabtest_test ;
insert into commonlabtest_test (test_order_id, test_type_id, lab_reference_number, creator, date_created, voided, voided_by, date_voided, void_reason, uuid) 
select o.order_id, 5, concat(date_format(o.date_activated, '%Y%m%d'), '-', o.encounter_id) as lab_reference_number, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from orders as o 
inner join commonlabtest_type as ct on ct.reference_concept_id = o.concept_id 
where o.order_type_id = 3 and o.voided = 0;


/* Create a PROCESSED sample to ensure data integrity */
truncate commonlabtest_sample ;
insert into commonlabtest_sample (test_order_id, specimen_type, specimen_site, is_expirable, lab_sample_identifier, collector, status, creator, date_created, collection_date, processed_date, uuid) 
select order_id, 61 as specimen_type, 491 as specimen_site, 0, uuid() as lab_sample_identifier, creator, 'PROCESSED', creator, date_created, date_created, date_created, uuid() from orders 
where order_type_id = 3 and voided = 0;


truncate commonlabtest_attribute_type;
insert into commonlabtest_attribute_type (test_attribute_type_id,test_type_id,name,datatype,min_occurs,max_occurs,datatype_config,sort_weight,description,preferred_handler,hint,group_name,multiset_name,creator,date_created,retired,uuid) values 
('1','5','DATE COLLECTED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','e814ce0e-96fb-48ff-a116-c504613b931f','1','DATE COLLECTED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639b3f-d6cc-11ed-8fc9-e86a64440f18'),
('2','5','INVESTIGATION DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','2','INVESTIGATION DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639cfc-d6cc-11ed-8fc9-e86a64440f18'),
('3','5','LABORATORY INVESTIGATION NUMBER','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','3','LABORATORY INVESTIGATION NUMBER','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d25-d6cc-11ed-8fc9-e86a64440f18'),
('4','5','PURPOSE OF INVESTIGATION','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5a83a85a-8287-4ae6-8807-681a97b97667','4','PURPOSE OF INVESTIGATION','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d4b-d6cc-11ed-8fc9-e86a64440f18'),
('5','5','REFERRING FACILITY','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','','5','REFERRING FACILITY','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','XPERT','','1','2023-04-09','0','c9639b4e-d6cc-11ed-8fc9-e86a64440f18'),
('6','5','REQUESTING MEDICAL FACILITY','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','357ecc36-1f6f-40c7-9e7e-4105286002b0','6','REQUESTING MEDICAL FACILITY','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d00-d6cc-11ed-8fc9-e86a64440f18'),
('7','5','TUBERCULOSIS SAMPLE SOURCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf065e-0370-102d-b0e3-001ec94a0cc1','7','TUBERCULOSIS SAMPLE SOURCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d27-d6cc-11ed-8fc9-e86a64440f18'),
('8','5','DATE OF REQUEST FOR LABORATORY INVESTIGATION','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','8','DATE OF REQUEST FOR LABORATORY INVESTIGATION','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d4d-d6cc-11ed-8fc9-e86a64440f18'),
('9','5','LAB SPECIALIST NAME','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','9','LAB SPECIALIST NAME','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','XPERT','','1','2023-04-09','0','c9639b50-d6cc-11ed-8fc9-e86a64440f18'),
('10','5','REFERRED BY','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','10','REFERRED BY','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','XPERT','','1','2023-04-09','0','c9639d02-d6cc-11ed-8fc9-e86a64440f18'),
('11','5','APPEARANCE OF SPECIMEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bb563a-0370-102d-b0e3-001ec94a0cc1','11','APPEARANCE OF SPECIMEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d29-d6cc-11ed-8fc9-e86a64440f18'),
('12','5','MTB RESULT','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','731bdb67-f216-477f-85c2-8af92d999121','12','MTB RESULT','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d4f-d6cc-11ed-8fc9-e86a64440f18'),
('13','5','RIFAMPICIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','12235c33-e627-4636-8b85-8643fadc622e','13','RIFAMPICIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639b52-d6cc-11ed-8fc9-e86a64440f18'),
('14','5','ERROR CODE','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','14','ERROR CODE','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','XPERT','','1','2023-04-09','0','c9639d04-d6cc-11ed-8fc9-e86a64440f18'),
('15','5','XPERT MTB BURDEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','73ac78a7-6c49-490a-bb6e-ba94bc6d0479','15','XPERT MTB BURDEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d2b-d6cc-11ed-8fc9-e86a64440f18'),
('16','5','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','16','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d51-d6cc-11ed-8fc9-e86a64440f18'),
('17','5','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','17','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639b54-d6cc-11ed-8fc9-e86a64440f18'),
('18','5','TUBERCULOSIS TEST START DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','18','TUBERCULOSIS TEST START DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d06-d6cc-11ed-8fc9-e86a64440f18'),
('19','5','TUBERCULOSIS TEST RESULT DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','19','TUBERCULOSIS TEST RESULT DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','XPERT','','1','2023-04-09','0','c9639d2d-d6cc-11ed-8fc9-e86a64440f18'),
('20','5','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.customdatatype.datatype.LongFreeTextDatatype','0','1','Length=500','20','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','XPERT','','1','2023-04-09','0','c9639d52-d6cc-11ed-8fc9-e86a64440f18'),
('32','5','APPEARANCE OF SPECIMEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bb563a-0370-102d-b0e3-001ec94a0cc1','21','APPEARANCE OF SPECIMEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba17ba-d6ce-11ed-8fc9-e86a64440f18'),
('33','5','MTB RESULT','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','731bdb67-f216-477f-85c2-8af92d999121','22','MTB RESULT','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba17de-d6ce-11ed-8fc9-e86a64440f18'),
('34','5','RIFAMPICIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','12235c33-e627-4636-8b85-8643fadc622e','23','RIFAMPICIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba1804-d6ce-11ed-8fc9-e86a64440f18'),
('35','5','ISONIAZID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','9446085c-86ae-4e06-b571-f8a88217b472','24','ISONIAZID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba19d6-d6ce-11ed-8fc9-e86a64440f18'),
('36','5','ERROR CODE','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','25','ERROR CODE','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','HAIN','','1','2023-04-09','0','0dba182d-d6ce-11ed-8fc9-e86a64440f18'),
('37','5','XPERT MTB BURDEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','73ac78a7-6c49-490a-bb6e-ba94bc6d0479','26','XPERT MTB BURDEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba1857-d6ce-11ed-8fc9-e86a64440f18'),
('38','5','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','27','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba1885-d6ce-11ed-8fc9-e86a64440f18'),
('39','5','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','28','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba18b6-d6ce-11ed-8fc9-e86a64440f18'),
('40','5','TUBERCULOSIS TEST START DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','29','TUBERCULOSIS TEST START DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba18eb-d6ce-11ed-8fc9-e86a64440f18'),
('41','5','TUBERCULOSIS TEST RESULT DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','30','TUBERCULOSIS TEST RESULT DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN','','1','2023-04-09','0','0dba1923-d6ce-11ed-8fc9-e86a64440f18'),
('42','5','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.customdatatype.datatype.LongFreeTextDatatype','0','1','Length=500','31','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','HAIN','','1','2023-04-09','0','0dba195c-d6ce-11ed-8fc9-e86a64440f18'),
('54','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','32','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','CULTURE','','1','2023-04-09','0','3ecf9911-d6d7-11ed-8fc9-e86a64440f18'),
('55','5','DAYS TO POSITIVITY','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','33','DAYS TO POSITIVITY','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','CULTURE','','1','2023-04-09','0','3ecf9934-d6d7-11ed-8fc9-e86a64440f18'),
('56','5','TUBERCULOSIS CULTURE METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf0d84-0370-102d-b0e3-001ec94a0cc1','34','TUBERCULOSIS CULTURE METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','CULTURE','','1','2023-04-09','0','3ecf9959-d6d7-11ed-8fc9-e86a64440f18'),
('57','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','35','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','CULTURE','','1','2023-04-09','0','3ecf9981-d6d7-11ed-8fc9-e86a64440f18'),
('58','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','36','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','CULTURE','','1','2023-04-09','0','3ecf99ac-d6d7-11ed-8fc9-e86a64440f18'),
('69','5','TUBERCULOSIS SMEAR MICROSCOPY METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf128e-0370-102d-b0e3-001ec94a0cc1','37','TUBERCULOSIS SMEAR MICROSCOPY METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','SMEAR','','1','2023-04-09','0','3014c358-d8cc-11ed-8fc9-e86a64440f18'),
('70','5','BACILLI','org.openmrs.customdatatype.datatype.FloatDatatype','0','1','','38','BACILLI','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','SMEAR','','1','2023-04-09','0','3014c37b-d8cc-11ed-8fc9-e86a64440f18'),
('81','5','APPEARANCE OF SPECIMEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bb563a-0370-102d-b0e3-001ec94a0cc1','39','APPEARANCE OF SPECIMEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f563e-d8cf-11ed-8fc9-e86a64440f18'),
('82','5','MTB RESULT','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','731bdb67-f216-477f-85c2-8af92d999121','40','MTB RESULT','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f5660-d8cf-11ed-8fc9-e86a64440f18'),
('83','5','RIFAMPICIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','12235c33-e627-4636-8b85-8643fadc622e','41','RIFAMPICIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f5685-d8cf-11ed-8fc9-e86a64440f18'),
('84','5','ISONIAZID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','9446085c-86ae-4e06-b571-f8a88217b472','42','ISONIAZID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f56ae-d8cf-11ed-8fc9-e86a64440f18'),
('85','5','ERROR CODE','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','43','ERROR CODE','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','HAIN2','','1','2023-04-09','0','d35f56d9-d8cf-11ed-8fc9-e86a64440f18'),
('86','5','XPERT MTB BURDEN','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','73ac78a7-6c49-490a-bb6e-ba94bc6d0479','44','XPERT MTB BURDEN','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f5708-d8cf-11ed-8fc9-e86a64440f18'),
('87','5','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','45','TUBERCULOSIS TEST DATE ORDERED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f5739-d8cf-11ed-8fc9-e86a64440f18'),
('88','5','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','46','TUBERCULOSIS TEST DATE RECEIVED','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f576d-d8cf-11ed-8fc9-e86a64440f18'),
('89','5','TUBERCULOSIS TEST START DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','47','TUBERCULOSIS TEST START DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f57a5-d8cf-11ed-8fc9-e86a64440f18'),
('90','5','TUBERCULOSIS TEST RESULT DATE','org.openmrs.customdatatype.datatype.DateDatatype','0','1','','48','TUBERCULOSIS TEST RESULT DATE','org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler','','HAIN2','','1','2023-04-09','0','d35f57df-d8cf-11ed-8fc9-e86a64440f18'),
('91','5','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.customdatatype.datatype.LongFreeTextDatatype','0','1','Length=500','49','TUBERCULOSIS SPECIMEN COMMENTS','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','HAIN2','','1','2023-04-09','0','d35f581c-d8cf-11ed-8fc9-e86a64440f18'),
('103','5','DIRECT/INDIRECT','org.openmrs.customdatatype.datatype.BooleanDatatype','0','1','','50','DIRECT/INDIRECT','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0e4d-db60-11ed-aa25-e86a64440f18'),
('104','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','51','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0e70-db60-11ed-aa25-e86a64440f18'),
('105','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','52','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0e97-db60-11ed-aa25-e86a64440f18'),
('106','5','OFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','b1c139e2-fc0e-46f0-af5b-93fdfabc42d9','53','OFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0ec1-db60-11ed-aa25-e86a64440f18'),
('107','5','MOXIFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','a1930ed3-333f-4bdd-9c56-6b9791143e8a','54','MOXIFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0eff-db60-11ed-aa25-e86a64440f18'),
('108','5','LEVOFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','8bf73e32-f75a-4242-9bcf-92c387ff264e','55','LEVOFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0f30-db60-11ed-aa25-e86a64440f18'),
('109','5','PROTHIONAMIDE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','044afaa8-5e59-4aae-9845-3141544d9957','56','PROTHIONAMIDE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0f63-db60-11ed-aa25-e86a64440f18'),
('110','5','LINEZOLID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5d758f40-a683-4779-8a49-e7320a7b863e','57','LINEZOLID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0f99-db60-11ed-aa25-e86a64440f18'),
('111','5','CLOFAZAMINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','917e64f7-893a-4ef7-92e4-e6c1fa592300','58','CLOFAZAMINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e0fd0-db60-11ed-aa25-e86a64440f18'),
('112','5','BEDAQUILINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','7b41b1ae-410f-4b4a-adae-f7394311f49e','59','BEDAQUILINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e100c-db60-11ed-aa25-e86a64440f18'),
('113','5','DELAMANID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','92649f85-64d9-4ef0-b266-df583e3b6715','60','DELAMANID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e104a-db60-11ed-aa25-e86a64440f18'),
('114','5','P-AMINOSALICY RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','6371d4c7-b3c6-4d57-8d03-7a383b616fc7','61','P-AMINOSALICY RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e108a-db60-11ed-aa25-e86a64440f18'),
('115','5','CAPREOMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','85209eef-f575-4a5f-9357-e8e39a1785cf','62','CAPREOMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e10cd-db60-11ed-aa25-e86a64440f18'),
('116','5','KANAMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','988fa8f7-c67d-44c8-99e8-fdd20a4b0050','63','KANAMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e11e1-db60-11ed-aa25-e86a64440f18'),
('117','5','AMIKACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','68e27ec8-37da-4ab8-b992-f90374275043','64','AMIKACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e122c-db60-11ed-aa25-e86a64440f18'),
('118','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','65','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e1275-db60-11ed-aa25-e86a64440f18'),
('119','5','CONCENTRATION','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','66','CONCENTRATION','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e12c3-db60-11ed-aa25-e86a64440f18'),
('120','5','COLONIES IN CONTROL','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','67','COLONIES IN CONTROL','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e1313-db60-11ed-aa25-e86a64440f18'),
('121','5','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf023a-0370-102d-b0e3-001ec94a0cc1','68','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_MGIT','','1','2023-04-09','0','2f7e1367-db60-11ed-aa25-e86a64440f18'),
('132','5','DIRECT/INDIRECT','org.openmrs.customdatatype.datatype.BooleanDatatype','0','1','','69','DIRECT/INDIRECT','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafd7e-db7a-11ed-aa25-e86a64440f18'),
('133','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','70','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafda2-db7a-11ed-aa25-e86a64440f18'),
('134','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','71','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','DST2_LJ','','1','2023-04-09','0','a4cafdc9-db7a-11ed-aa25-e86a64440f18'),
('135','5','OFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','b1c139e2-fc0e-46f0-af5b-93fdfabc42d9','72','OFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafdf3-db7a-11ed-aa25-e86a64440f18'),
('136','5','MOXIFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','a1930ed3-333f-4bdd-9c56-6b9791143e8a','73','MOXIFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafe20-db7a-11ed-aa25-e86a64440f18'),
('137','5','LEVOFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','8bf73e32-f75a-4242-9bcf-92c387ff264e','74','LEVOFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafe50-db7a-11ed-aa25-e86a64440f18'),
('138','5','PROTHIONAMIDE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','044afaa8-5e59-4aae-9845-3141544d9957','75','PROTHIONAMIDE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafe82-db7a-11ed-aa25-e86a64440f18'),
('139','5','LINEZOLID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5d758f40-a683-4779-8a49-e7320a7b863e','76','LINEZOLID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafeb8-db7a-11ed-aa25-e86a64440f18'),
('140','5','CLOFAZAMINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','917e64f7-893a-4ef7-92e4-e6c1fa592300','77','CLOFAZAMINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cafef2-db7a-11ed-aa25-e86a64440f18'),
('141','5','BEDAQUILINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','7b41b1ae-410f-4b4a-adae-f7394311f49e','78','BEDAQUILINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4caff2e-db7a-11ed-aa25-e86a64440f18'),
('142','5','DELAMANID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','92649f85-64d9-4ef0-b266-df583e3b6715','79','DELAMANID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4caff6b-db7a-11ed-aa25-e86a64440f18'),
('143','5','P-AMINOSALICY RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','6371d4c7-b3c6-4d57-8d03-7a383b616fc7','80','P-AMINOSALICY RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4caffaa-db7a-11ed-aa25-e86a64440f18'),
('144','5','CAPREOMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','85209eef-f575-4a5f-9357-e8e39a1785cf','81','CAPREOMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4caffed-db7a-11ed-aa25-e86a64440f18'),
('145','5','KANAMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','988fa8f7-c67d-44c8-99e8-fdd20a4b0050','82','KANAMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb0033-db7a-11ed-aa25-e86a64440f18'),
('146','5','AMIKACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','68e27ec8-37da-4ab8-b992-f90374275043','83','AMIKACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb007a-db7a-11ed-aa25-e86a64440f18'),
('147','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','84','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb00c4-db7a-11ed-aa25-e86a64440f18'),
('148','5','CONCENTRATION','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','85','CONCENTRATION','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb0111-db7a-11ed-aa25-e86a64440f18'),
('149','5','COLONIES IN CONTROL','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','86','COLONIES IN CONTROL','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb015f-db7a-11ed-aa25-e86a64440f18'),
('150','5','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf023a-0370-102d-b0e3-001ec94a0cc1','87','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2_LJ','','1','2023-04-09','0','a4cb01b1-db7a-11ed-aa25-e86a64440f18'),
('161','5','DIRECT/INDIRECT','org.openmrs.customdatatype.datatype.BooleanDatatype','0','1','','88','DIRECT/INDIRECT','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44bfe-db7a-11ed-aa25-e86a64440f18'),
('162','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','89','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44c21-db7a-11ed-aa25-e86a64440f18'),
('163','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','90','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','DST2','','1','2023-04-09','0','c8d44c47-db7a-11ed-aa25-e86a64440f18'),
('164','5','OFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','b1c139e2-fc0e-46f0-af5b-93fdfabc42d9','91','OFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44c70-db7a-11ed-aa25-e86a64440f18'),
('165','5','MOXIFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','a1930ed3-333f-4bdd-9c56-6b9791143e8a','92','MOXIFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44c9b-db7a-11ed-aa25-e86a64440f18'),
('166','5','LEVOFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','8bf73e32-f75a-4242-9bcf-92c387ff264e','93','LEVOFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44cc9-db7a-11ed-aa25-e86a64440f18'),
('167','5','PROTHIONAMIDE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','044afaa8-5e59-4aae-9845-3141544d9957','94','PROTHIONAMIDE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44cfb-db7a-11ed-aa25-e86a64440f18'),
('168','5','LINEZOLID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5d758f40-a683-4779-8a49-e7320a7b863e','95','LINEZOLID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44d2f-db7a-11ed-aa25-e86a64440f18'),
('169','5','CLOFAZAMINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','917e64f7-893a-4ef7-92e4-e6c1fa592300','96','CLOFAZAMINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44d66-db7a-11ed-aa25-e86a64440f18'),
('170','5','BEDAQUILINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','7b41b1ae-410f-4b4a-adae-f7394311f49e','97','BEDAQUILINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44da0-db7a-11ed-aa25-e86a64440f18'),
('171','5','DELAMANID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','92649f85-64d9-4ef0-b266-df583e3b6715','98','DELAMANID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44ddc-db7a-11ed-aa25-e86a64440f18'),
('172','5','P-AMINOSALICY RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','6371d4c7-b3c6-4d57-8d03-7a383b616fc7','99','P-AMINOSALICY RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44e1b-db7a-11ed-aa25-e86a64440f18'),
('173','5','CAPREOMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','85209eef-f575-4a5f-9357-e8e39a1785cf','100','CAPREOMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44e5d-db7a-11ed-aa25-e86a64440f18'),
('174','5','KANAMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','988fa8f7-c67d-44c8-99e8-fdd20a4b0050','101','KANAMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44ea2-db7a-11ed-aa25-e86a64440f18'),
('175','5','AMIKACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','68e27ec8-37da-4ab8-b992-f90374275043','102','AMIKACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44eea-db7a-11ed-aa25-e86a64440f18'),
('176','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','103','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44f34-db7a-11ed-aa25-e86a64440f18'),
('177','5','CONCENTRATION','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','104','CONCENTRATION','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44f7f-db7a-11ed-aa25-e86a64440f18'),
('178','5','COLONIES IN CONTROL','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','105','COLONIES IN CONTROL','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST2','','1','2023-04-09','0','c8d44fce-db7a-11ed-aa25-e86a64440f18'),
('179','5','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf023a-0370-102d-b0e3-001ec94a0cc1','106','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST2','','1','2023-04-09','0','c8d45020-db7a-11ed-aa25-e86a64440f18'),
('190','5','DIRECT/INDIRECT','org.openmrs.customdatatype.datatype.BooleanDatatype','0','1','','107','DIRECT/INDIRECT','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea8167e9-db7a-11ed-aa25-e86a64440f18'),
('191','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','108','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816814-db7a-11ed-aa25-e86a64440f18'),
('192','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','109','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','DST1_MGIT','','1','2023-04-09','0','ea81684c-db7a-11ed-aa25-e86a64440f18'),
('193','5','OFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','b1c139e2-fc0e-46f0-af5b-93fdfabc42d9','110','OFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816888-db7a-11ed-aa25-e86a64440f18'),
('194','5','MOXIFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','a1930ed3-333f-4bdd-9c56-6b9791143e8a','111','MOXIFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea8168bb-db7a-11ed-aa25-e86a64440f18'),
('195','5','LEVOFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','8bf73e32-f75a-4242-9bcf-92c387ff264e','112','LEVOFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea8168e9-db7a-11ed-aa25-e86a64440f18'),
('196','5','PROTHIONAMIDE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','044afaa8-5e59-4aae-9845-3141544d9957','113','PROTHIONAMIDE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea81691a-db7a-11ed-aa25-e86a64440f18'),
('197','5','LINEZOLID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5d758f40-a683-4779-8a49-e7320a7b863e','114','LINEZOLID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea81694e-db7a-11ed-aa25-e86a64440f18'),
('198','5','CLOFAZAMINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','917e64f7-893a-4ef7-92e4-e6c1fa592300','115','CLOFAZAMINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816985-db7a-11ed-aa25-e86a64440f18'),
('199','5','BEDAQUILINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','7b41b1ae-410f-4b4a-adae-f7394311f49e','116','BEDAQUILINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea8169be-db7a-11ed-aa25-e86a64440f18'),
('200','5','DELAMANID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','92649f85-64d9-4ef0-b266-df583e3b6715','117','DELAMANID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea8169fa-db7a-11ed-aa25-e86a64440f18'),
('201','5','P-AMINOSALICY RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','6371d4c7-b3c6-4d57-8d03-7a383b616fc7','118','P-AMINOSALICY RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816a39-db7a-11ed-aa25-e86a64440f18'),
('202','5','CAPREOMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','85209eef-f575-4a5f-9357-e8e39a1785cf','119','CAPREOMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816a7a-db7a-11ed-aa25-e86a64440f18'),
('203','5','KANAMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','988fa8f7-c67d-44c8-99e8-fdd20a4b0050','120','KANAMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816abe-db7a-11ed-aa25-e86a64440f18'),
('204','5','AMIKACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','68e27ec8-37da-4ab8-b992-f90374275043','121','AMIKACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816b06-db7a-11ed-aa25-e86a64440f18'),
('205','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','122','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816b50-db7a-11ed-aa25-e86a64440f18'),
('206','5','CONCENTRATION','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','123','CONCENTRATION','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816b9d-db7a-11ed-aa25-e86a64440f18'),
('207','5','COLONIES IN CONTROL','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','124','COLONIES IN CONTROL','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816bed-db7a-11ed-aa25-e86a64440f18'),
('208','5','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf023a-0370-102d-b0e3-001ec94a0cc1','125','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_MGIT','','1','2023-04-09','0','ea816c41-db7a-11ed-aa25-e86a64440f18'),
('219','5','DIRECT/INDIRECT','org.openmrs.customdatatype.datatype.BooleanDatatype','0','1','','126','DIRECT/INDIRECT','org.openmrs.web.attribute.handler.BooleanFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90dca-db7b-11ed-aa25-e86a64440f18'),
('220','5','TYPE OF ORGANISM','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31befc22-0370-102d-b0e3-001ec94a0cc1','127','TYPE OF ORGANISM','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90dee-db7b-11ed-aa25-e86a64440f18'),
('221','5','TYPE OF ORGANISM NON-CODED','org.openmrs.customdatatype.datatype.FreeTextDatatype','0','1','Length=200','128','TYPE OF ORGANISM NON-CODED','org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler','','DST1_LJ','','1','2023-04-09','0','03c90e15-db7b-11ed-aa25-e86a64440f18'),
('222','5','OFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','b1c139e2-fc0e-46f0-af5b-93fdfabc42d9','129','OFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90e3e-db7b-11ed-aa25-e86a64440f18'),
('223','5','MOXIFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','a1930ed3-333f-4bdd-9c56-6b9791143e8a','130','MOXIFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90e6a-db7b-11ed-aa25-e86a64440f18'),
('224','5','LEVOFLOXACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','8bf73e32-f75a-4242-9bcf-92c387ff264e','131','LEVOFLOXACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90e98-db7b-11ed-aa25-e86a64440f18'),
('225','5','PROTHIONAMIDE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','044afaa8-5e59-4aae-9845-3141544d9957','132','PROTHIONAMIDE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90eca-db7b-11ed-aa25-e86a64440f18'),
('226','5','LINEZOLID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','5d758f40-a683-4779-8a49-e7320a7b863e','133','LINEZOLID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90eff-db7b-11ed-aa25-e86a64440f18'),
('227','5','CLOFAZAMINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','917e64f7-893a-4ef7-92e4-e6c1fa592300','134','CLOFAZAMINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90f38-db7b-11ed-aa25-e86a64440f18'),
('228','5','BEDAQUILINE RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','7b41b1ae-410f-4b4a-adae-f7394311f49e','135','BEDAQUILINE RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90f72-db7b-11ed-aa25-e86a64440f18'),
('229','5','DELAMANID RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','92649f85-64d9-4ef0-b266-df583e3b6715','136','DELAMANID RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90fae-db7b-11ed-aa25-e86a64440f18'),
('230','5','P-AMINOSALICY RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','6371d4c7-b3c6-4d57-8d03-7a383b616fc7','137','P-AMINOSALICY RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c90fee-db7b-11ed-aa25-e86a64440f18'),
('231','5','CAPREOMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','85209eef-f575-4a5f-9357-e8e39a1785cf','138','CAPREOMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c91031-db7b-11ed-aa25-e86a64440f18'),
('232','5','KANAMYCIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','988fa8f7-c67d-44c8-99e8-fdd20a4b0050','139','KANAMYCIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c91075-db7b-11ed-aa25-e86a64440f18'),
('233','5','AMIKACIN RESISTANCE','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','68e27ec8-37da-4ab8-b992-f90374275043','140','AMIKACIN RESISTANCE','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c910bc-db7b-11ed-aa25-e86a64440f18'),
('234','5','COLONIES','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','141','COLONIES','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c91106-db7b-11ed-aa25-e86a64440f18'),
('235','5','CONCENTRATION','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','142','CONCENTRATION','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c91153-db7b-11ed-aa25-e86a64440f18'),
('236','5','COLONIES IN CONTROL','org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype','0','1','Regex=[0-9]*','143','COLONIES IN CONTROL','org.openmrs.web.attribute.handler.RegexValidatedTextDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c911a3-db7b-11ed-aa25-e86a64440f18'),
('237','5','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.customdatatype.datatype.ConceptDatatype','0','1','31bf023a-0370-102d-b0e3-001ec94a0cc1','144','TUBERCULOSIS DRUG SENSITIVITY TEST METHOD','org.openmrs.web.attribute.handler.ConceptFieldGenDatatypeHandler','','DST1_LJ','','1','2023-04-09','0','03c911f5-db7b-11ed-aa25-e86a64440f18');


select * from commonlabtest_type;
select * from commonlabtest_attribute_type;
select * from commonlabtest_sample;
select * from commonlabtest_test;
select * from commonlabtest_attribute;
select * from orders o where o.patient_id = 32072;

set foreign_key_checks = 1;


/* Tasks
 * 1. commonlabtest_sample.collection_date = DATE COLLECTED
 * 2. commonlabtest_sample.comments = TUBERCULOSIS SPECIMEN COMMENTS
 * 3. Void TUBERCULOSIS TEST DATE ORDERED
 * 4. commonlabtest_sample.processed_date = TUBERCULOSIS TEST RESULT DATE
 */

-- Add attributes - DATE COLLECTED
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, 1, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
where o.concept_id = 188;

-- Add attributes - INVESTIGATION DATE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, 1, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
where o.concept_id = 427;

-- Add attributes - LABORATORY INVESTIGATION NUMBER
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, 1, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
where o.concept_id = 428;

-- Add attributes - PURPOSE OF INVESTIGATION
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, 1, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
where o.concept_id = 425;

-- Add attributes - REFERRING FACILITY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, 1, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
where o.concept_id = 498;





select * from commonlabtest_attribute_type cat ;

