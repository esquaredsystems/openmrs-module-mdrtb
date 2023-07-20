-- Add attributes - RIFAMPICIN RESISTANCE for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.name = 'RIFAMPICIN RESISTANCE' and cat.group_name = 'XPERT'
where o.concept_id = 317;

-- Add attributes - RIFAMPICIN RESISTANCE for HAIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.name = 'RIFAMPICIN RESISTANCE' and cat.group_name = 'HAIN'
where o.concept_id = 317;

-- Add attributes - RIFAMPICIN RESISTANCE for HAIN 2
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.name = 'RIFAMPICIN RESISTANCE' and cat.group_name = 'HAIN2'
where o.concept_id = 317;

-- Add attributes - ERROR CODE for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.name = 'ERROR CODE' and cat.group_name = 'XPERT'
where o.concept_id = 316;

-- Add attributes - ERROR CODE for HAIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.name = 'ERROR CODE' and cat.group_name = 'HAIN'
where o.concept_id = 316;

-- Add attributes - ERROR CODE for HAIN 2
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.name = 'ERROR CODE' and cat.group_name = 'HAIN2'
where o.concept_id = 316;

-- Add attributes - XPERT MTB BURDEN for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.name = 'XPERT MTB BURDEN' and cat.group_name = 'XPERT'
where o.concept_id = 318;

-- Add attributes - XPERT MTB BURDEN for HAIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.name = 'XPERT MTB BURDEN' and cat.group_name = 'HAIN'
where o.concept_id = 318;

-- Add attributes - XPERT MTB BURDEN for HAIN 2
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.name = 'XPERT MTB BURDEN' and cat.group_name = 'HAIN2'
where o.concept_id = 318;

-- Add attributes - APPEARANCE OF SPECIMEN for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311 
inner join commonlabtest_attribute_type cat on cat.name = 'APPEARANCE OF SPECIMEN' and cat.group_name = 'XPERT'
where o.concept_id = 174;

-- Add attributes - APPEARANCE OF SPECIMEN for HAIN
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 323 
inner join commonlabtest_attribute_type cat on cat.name = 'APPEARANCE OF SPECIMEN' and cat.group_name = 'HAIN'
where o.concept_id = 174;

-- Add attributes - APPEARANCE OF SPECIMEN for HAIN 2
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 414 
inner join commonlabtest_attribute_type cat on cat.name = 'APPEARANCE OF SPECIMEN' and cat.group_name = 'HAIN2'
where o.concept_id = 174;

-- Add attributes - DATE OF REQUEST FOR LABORATORY INVESTIGATION for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
inner join commonlabtest_attribute_type cat on cat.name = 'DATE OF REQUEST FOR LABORATORY INVESTIGATION' and cat.group_name = 'XPERT'
where o.concept_id = 589;

-- Add attributes - LAB SPECIALIST NAME for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
inner join commonlabtest_attribute_type cat on cat.name = 'LAB SPECIALIST NAME' and cat.group_name = 'XPERT'
where o.concept_id = 611;

-- Add attributes - LABORATORY INVESTIGATION NUMBER for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
inner join commonlabtest_attribute_type cat on cat.name = 'LABORATORY INVESTIGATION NUMBER' and cat.group_name = 'XPERT'
where o.concept_id = 428;

-- Add attributes - PURPOSE OF INVESTIGATION for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
inner join commonlabtest_attribute_type cat on cat.name = 'PURPOSE OF INVESTIGATION' and cat.group_name = 'XPERT'
where o.concept_id = 425;

-- Add attributes - TUBERCULOSIS SAMPLE SOURCE for XPERT
insert into commonlabtest_attribute (test_attribute_id,test_order_id,attribute_type_id,value_reference,creator,date_created,voided,voided_by,date_voided,void_reason,uuid)
select 0, ct.test_order_id, cat.test_attribute_type_id, c.uuid, o.creator, o.date_created, o.voided, o.voided_by, o.date_voided, o.void_reason, uuid() as uuid from obs as o 
inner join orders o2 ON o2.encounter_id = o.encounter_id 
inner join commonlabtest_test ct on ct.test_order_id = o2.order_id 
inner join concept c on c.concept_id = o.value_coded 
inner join obs o3 on o3.obs_id = o.obs_group_id and o3.concept_id = 311
inner join commonlabtest_attribute_type cat on cat.name = 'TUBERCULOSIS SAMPLE SOURCE' and cat.group_name = 'XPERT'
where o.concept_id = 67;



SELECT *  from commonlabtest_attribute_type cat 
where name = 'TUBERCULOSIS SAMPLE SOURCE'


select o.obs_id , o.encounter_id, o2.uuid as 'order_id' from obs o
inner join orders o2  on o.encounter_id  = o2.encounter_id
inner join commonlabtest_attribute ca on ca.test_order_id = o2.order_id
where ca.attribute_type_id = 4

select o.obs_id , o.encounter_id, ct.uuid as 'TEST ORDER ID' from obs o
inner join orders o2  on o.encounter_id  = o2.encounter_id
inner join commonlabtest_test ct on o2.order_id = ct.test_order_id
inner join commonlabtest_attribute ca on ca.test_order_id = o2.order_id
where ca.attribute_type_id = 7


SELECT ct.test_order_id ,ct.uuid, ca.attribute_type_id , ca.test_attribute_id  from commonlabtest_test ct
inner join commonlabtest_attribute ca on ct.test_order_id = ca.test_order_id
where ca.attribute_type_id = 7
