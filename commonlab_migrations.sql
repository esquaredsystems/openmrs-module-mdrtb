set foreign_key_checks = 0;

-- Common Lab Module
-- Set the UUID for Specimen Type and Site
update global_property set property_value = '31bf065e-0370-102d-b0e3-001ec94a0cc1' where property = 'commonlabtest.specimenSiteConceptUuid';
update global_property set property_value = '2da61322-bcc5-4c32-b412-1b1ef37f4a25' where property = 'commonlabtest.specimenTypeConceptUuid';

select * from global_property gp where property like 'commonlab%';

truncate commonlabtest_type;
insert into commonlabtest_type (test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) 
select test_type_id, name, short_name, test_group, requires_specimen, reference_concept_id, description, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid from _commonlabtest_type ;


-- Create temporary tables to separate out the results
drop table if exists _temp;
create table _temp 
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


-- First, copy a placeholder order to be used when none other is present
truncate orders;
INSERT INTO orders (order_type_id,concept_id,orderer,encounter_id,instructions,date_activated,auto_expire_date,date_stopped,order_reason,order_reason_non_coded,creator,date_created,voided,voided_by,date_voided,void_reason,patient_id,accession_number,uuid,urgency,order_number,previous_order_id,order_action,comment_to_fulfiller,care_setting,scheduled_date,order_group_id,sort_weight,fulfiller_comment,fulfiller_status) VALUES
(1,410,3,147521,NULL,'2017-05-04 00:00:00',NULL,NULL,NULL,NULL,1,'2023-04-09 07:33:26',0,NULL,NULL,NULL,32072,NULL,'90ef1459-8b33-4a18-936e-2cfdfbe99649','ROUTINE','ORD-1',NULL,'NEW',NULL,1,NULL,NULL,NULL,NULL,NULL);

-- For each patient with a Test, create an Order from respective Lab Encounter (for now set the concept to Microscopy test)
-- First, store orders against all Lab Result encounters
insert into orders (order_type_id, concept_id, orderer, encounter_id, date_activated, creator, date_created, voided, voided_by, date_voided, void_reason, patient_id, uuid, urgency, order_number, order_action, care_setting) 
select 3 as order_type_id, 410 as concept_id, e.creator, e.encounter_id, e.encounter_datetime, p.provider_id as orderer, e.date_created, e.voided, e.voided_by, e.date_voided, e.voided_by, e.patient_id, UUID(), 'ROUTINE' as urgency, concat('ORD-', e.encounter_id) as order_number, 'NEW' as order_action, 1 as care_setting from encounter as e 
inner join users u on u.user_id = e.creator 
inner join provider p on p.person_id = u.person_id 
where e.encounter_type = 11;
-- Now store orders against Specimen Collection encounter
insert into orders (order_type_id, concept_id, orderer, encounter_id, date_activated, creator, date_created, voided, voided_by, date_voided, void_reason, patient_id, uuid, urgency, order_number, order_action, care_setting) 
select 3 as order_type_id, 571 as concept_id, e.creator, e.encounter_id, e.encounter_datetime, p.provider_id as orderer, e.date_created, e.voided, e.voided_by, e.date_voided, e.voided_by, e.patient_id, UUID(), 'ROUTINE' as urgency, concat('ORD-', e.encounter_id) as order_number, 'NEW' as order_action, 1 as care_setting from encounter as e 
inner join users u on u.user_id = e.creator 
inner join provider p on p.person_id = u.person_id 
where e.encounter_type = 5;


-- Update the order and set the creator where respective user does not exist
update orders set creator = 1 where creator not in (select user_id from users);
update orders set voided_by = 1 where voided_by is not null and voided_by not in (select user_id from users);

/* Create Lab Test records for each Order */
truncate commonlabtest_test;
insert into commonlabtest_test (test_order_id, test_type_id, lab_reference_number, creator, date_created, voided, voided_by, date_voided, void_reason, uuid) 
select o.order_id, (case o.concept_id when 410 then 5 else 9 end) as test_type_id, concat(date_format(o.date_activated, '%Y%m%d'), '-', o.encounter_id) as lab_reference_number, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from orders as o 
inner join commonlabtest_type as ct on ct.reference_concept_id = o.concept_id 
where o.order_type_id = 3 and o.voided = 0;

/* Create a PROCESSED sample to ensure data integrity */
truncate commonlabtest_sample ;
insert into commonlabtest_sample (test_order_id, specimen_type, specimen_site, is_expirable, lab_sample_identifier, collector, status, creator, date_created, collection_date, processed_date, uuid) 
select order_id, 61 as specimen_type, 491 as specimen_site, 0, uuid() as lab_sample_identifier, creator, 'PROCESSED', creator, date_created, date_created, date_created, uuid() from orders 
where order_type_id = 3 and voided = 0;


truncate commonlabtest_attribute_type;
insert into commonlabtest_attribute_type (test_attribute_type_id,test_type_id,name,datatype,min_occurs,max_occurs,datatype_config,sort_weight,description,preferred_handler,hint,group_name,multiset_name,creator,date_created,retired,uuid) 
select test_attribute_type_id,test_type_id,name,datatype,min_occurs,max_occurs,datatype_config,sort_weight,description,preferred_handler,hint,group_name,multiset_name,creator,date_created,retired,uuid from _commonlabtest_attribute_type;

-- Update description to include group name
update commonlabtest_attribute_type set description = concat(description, ' - ', group_name) where group_name is not null and description is null;


/* Tasks
 * 1. commonlabtest_sample.collection_date = DATE COLLECTED
 * 2. commonlabtest_sample.comments = TUBERCULOSIS SPECIMEN COMMENTS
 * 3. Void TUBERCULOSIS TEST DATE ORDERED
 * 4. commonlabtest_sample.processed_date = TUBERCULOSIS TEST RESULT DATE
 */

truncate commonlabtest_attribute;
-- Add attributes - DATE COLLECTED (will be repeated for each test time if there are multiple attributes matching)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'DATE COLLECTED' 
where o.concept_id = 188;

-- Add attributes - INVESTIGATION DATE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'INVESTIGATION DATE' 
where o.concept_id = 427;

-- Add attributes - LABORATORY INVESTIGATION NUMBER
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'LABORATORY INVESTIGATION NUMBER' 
where o.concept_id = 428;

-- Add attributes - PURPOSE OF INVESTIGATION
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.name = 'PURPOSE OF INVESTIGATION' 
where o.concept_id = 425;

-- Add attributes - REFERRING FACILITY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'REFERRING FACILITY' 
where o.concept_id = 498 and length(o.value_text) < 255;

-- Add attributes - REQUESTING MEDICAL FACILITY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.name = 'REQUESTING MEDICAL FACILITY' 
where o.concept_id = 426;

-- Add attributes - TUBERCULOSIS SAMPLE SOURCE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type as tat on tat.name = 'TUBERCULOSIS SAMPLE SOURCE' 
where o.concept_id = 67;

-- Add attributes - DATE OF REQUEST FOR LABORATORY INVESTIGATION
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'DATE OF REQUEST FOR LABORATORY INVESTIGATION' 
where o.concept_id = 589;

-- Add attributes - LAB SPECIALIST NAME
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'LAB SPECIALIST NAME' 
where o.concept_id = 611;

-- Add attributes - REFERRED BY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, tat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join commonlabtest_attribute_type as tat on tat.name = 'REFERRED BY' 
where o.concept_id = 497;


-- Add attributes for XPERT test
-- Add attributes - APPEARANCE OF SPECIMEN
-- insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'APPEARANCE OF SPECIMEN' 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
where o.concept_id = 174;
-- Add attributes - MTB RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'MTB RESULT' 
where o.concept_id = 312;
-- Add attributes - ERROR CODE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_text, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'ERROR CODE' 
where o.concept_id = 316 and o.value_text regexp '^[0-9]*$';
-- Add attributes - XPERT MTB BURDEN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'XPERT MTB BURDEN' 
where o.concept_id = 318;
-- Add attributes - ISONIAZID RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'ISONIAZID RESULT' 
where o.concept_id = 322;
-- Add attributes - RIFAMPICIN RESULT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'RIFAMPICIN RESULT' 
where o.concept_id = 317;
-- Add attributes - TUBERCULOSIS TEST DATE ORDERED
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'TUBERCULOSIS TEST RESULT DATE' 
where o.concept_id = 68;
-- Add attributes - TUBERCULOSIS SPECIMEN COMMENTS
-- insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, o.value_datetime, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.group_name = 'XPERT' and cat.name = 'TUBERCULOSIS SPECIMEN COMMENTS' 
where o.concept_id = 149;

select * from obs where concept_id = 149;



-- Add attributes - MTB RESULT (for HAIN)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.name = 'MTB RESULT' and cat.group_name = 'HAIN'
where o.concept_id = 312;
-- Add attributes - MTB RESULT (for HAIN2)
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.name = 'MTB RESULT' and cat.group_name = 'HAIN2'
where o.concept_id = 312;


-- Add attributes - For DST2 (All Susceptible Drugs)
-- If Resistance exists in Susceptible drugs, then insert as Drug name with answer as Susceptible
-- AMIKACIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'AMIKACIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 115; -- (The question concept should be SUSCEPTIBLE and value coded be AMIKACIN)
-- BEDAQUILINE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'BEDAQUILINE RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 447; -- (The question concept should be SUSCEPTIBLE and value coded be BEDAQUILINE)
-- CAPREOMYCIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'CAPREOMYCIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 107; -- (The question concept should be SUSCEPTIBLE and value coded be CAPREOMYCIN)
-- CIPROFLOXACIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'CIPROFLOXACIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 79; -- (The question concept should be SUSCEPTIBLE and value coded be CIPROFLOXACIN)
-- CLOFAZAMINE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'CLOFAZAMINE RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 72; -- (The question concept should be SUSCEPTIBLE and value coded be CLOFAZAMINE)
-- KANAMYCIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'KANAMYCIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 112; -- (The question concept should be SUSCEPTIBLE and value coded be KANAMYCIN)
-- LEVOFLOXACIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'LEVOFLOXACIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 101; -- (The question concept should be SUSCEPTIBLE and value coded be LEVOFLOXACIN)
-- MOXIFLOXACIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'MOXIFLOXACIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 109; -- (The question concept should be SUSCEPTIBLE and value coded be MOXIFLOXACIN)
-- OFLOXACIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'OFLOXACIN RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 108; -- (The question concept should be SUSCEPTIBLE and value coded be OFLOXACIN)
-- P-AMINOSALICY
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'P-AMINOSALICY RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 98; -- (The question concept should be SUSCEPTIBLE and value coded be P-AMINOSALICY)
-- PROTHIONAMIDE
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c2.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join commonlabtest_attribute_type cat on cat.name = 'PROTHIONAMIDE RESISTANCE' and cat.group_name = 'DST2'
inner join concept c2 on c2.concept_id = 543 -- (Concept for Answer as SUSCEPTIBLE)
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 138 -- (Concept For TB DRUG SENSITIVITY TEST)
where o.concept_id = 118 and o.value_coded = 42; -- (The question concept should be SUSCEPTIBLE and value coded be PROTHIONAMIDE)
-- TODO: CIPROFLOXACIN RESISTANCE
-- TODO: CLARITHROMYCIN RESISTANCE
-- TODO: CYCLOSERINE RESISTANCE
-- TODO: DELAMANID RESISTANCE
-- TODO: ETHAMBUTOL RESISTANCE
-- TODO: ETHIONAMIDE RESISTANCE
-- TODO: GATIFLOXACIN RESISTANCE
-- TODO: ISONIAZID RESISTANCE
-- TODO: LINEZOLID RESISTANCE
-- TODO: PYRAZINAMIDE RESISTANCE
-- TODO: PYRIDOXINE RESISTANCE
-- TODO: RIFABUTIN RESISTANCE
-- TODO: RIFAMPICIN RESISTANCE
-- TODO: STREPTOMYCIN RESISTANCE
-- TODO: TERIZIDONE RESISTANCE
-- TODO: THIOACETAZONE RESISTANCE
-- TODO: VIOMYCIN RESISTANCE
-- TODO: OTHER RESISTANCE

select * from commonlabtest_attribute ca where attribute_type_id = 169;
select * from commonlabtest_attribute_type cat where test_attribute_type_id = 169;

select * from commonlabtest_attribute_type cat where group_name = 'DST2' and name like '%RESIST%' order by name;

set foreign_key_checks = 1;


select * from commonlabtest_type;
select * from commonlabtest_attribute_type;
select * from commonlabtest_sample;
select * from commonlabtest_test;
select * from commonlabtest_attribute;
select * from orders o where o.patient_id = 32072;

select distinct obs_question, value_coded_name from _temp 
where encounter_type = 5 and patient_id = 259059;


select * from concept_name cn 
where cn.concept_id between 745 and 746;
