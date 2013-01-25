--inserts configuration for check of role in login module
INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'SPAGOBI.SECURITY.CHECK_ROLE_LOGIN', 'SPAGOBI.SECURITY.CHECK_ROLE_LOGIN', 'Check the correct role in login action', false, 'false',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SECURITY', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'SPAGOBI.SECURITY.ROLE_LOGIN', 'SPAGOBI.SECURITY.ROLE_LOGIN', 'The value of the role to check at login module', false, '',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SECURITY', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';

COMMIT;
-- NETWORK ENGINE
INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
    'PNG','PNG','EXPORT_TYPE','Exporters type','Exporters type', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
    'GRAPHML','GRAPHML','EXPORT_TYPE','Exporters type','Exporters type', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
    'NETWORK','sbidomains.nm.network','BIOBJ_TYPE','BI Object types','sbidomains.ds.network', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

SELECT @sequence:=(SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_ENGINES' )-1;
INSERT INTO SBI_ENGINES (ENGINE_ID, NAME,DESCR, ENCRYPT, LABEL,MAIN_URL, DRIVER_NM, ENGINE_TYPE,BIOBJ_TYPE,USE_DATASOURCE,USE_DATASET, USER_IN, TIME_IN, SBI_VERSION_IN,ORGANIZATION)
SELECT @sequence:=@sequence+1, 'Network Analysis Engine','Network Analysis Engine' , 0, 'SpagoBINetworkEngine','/SpagoBINetworkEngine/servlet/AdapterHTTP','it.eng.spagobi.engines.drivers.network.NetworkDriver',(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ENGINE_TYPE' AND VALUE_CD = 'EXT'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'BIOBJ_TYPE' AND VALUE_CD = 'NETWORK'),false, true, 'biadmin', current_timestamp, '3.6.0', ORGANIZATION.NAME
FROM sbi_organizations ORGANIZATION;

INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE) 
	VALUES ((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL = 'SpagoBINetworkEngine'),
	(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'PDF'), 
	false);
commit;
INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE) 
	VALUES ((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL = 'SpagoBINetworkEngine'),
	(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'PNG'), 
	true);
commit;
INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE) 
	VALUES ((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL = 'SpagoBINetworkEngine'),
	(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'GRAPHML'), 
	true);
commit;

DELETE FROM sbi_domains WHERE domain_cd = 'SELECTION_TYPE';

INSERT INTO sbi_domains (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM, VALUE_DS, USER_IN, ORGANIZATION, TIME_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'), 'LIST', 'sbidomains.nm.list', 'SELECTION_TYPE', 'Selection modality of parameter values', 'sbidomains.ds.list', 'SPAGOBI', 'SPAGOBI', current_timestamp);
UPDATE hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
COMMIT;

INSERT INTO sbi_domains (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM, VALUE_DS, USER_IN, ORGANIZATION, TIME_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'), 'LOOKUP', 'sbidomains.nm.lookup', 'SELECTION_TYPE', 'Selection modality of parameter values', 'sbidomains.ds.lookup', 'SPAGOBI', 'SPAGOBI', current_timestamp);
UPDATE hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
COMMIT;

INSERT INTO sbi_domains (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM, VALUE_DS, USER_IN, ORGANIZATION, TIME_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'), 'SLIDER', 'sbidomains.nm.slider', 'SELECTION_TYPE', 'Selection modality of parameter values', 'sbidomains.ds.slider', 'SPAGOBI', 'SPAGOBI', current_timestamp);
UPDATE hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
COMMIT;

INSERT INTO sbi_domains (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM, VALUE_DS, USER_IN, ORGANIZATION, TIME_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'), 'TREE', 'sbidomains.nm.tree', 'SELECTION_TYPE', 'Selection modality of parameter values', 'sbidomains.ds.tree', 'SPAGOBI', 'SPAGOBI', current_timestamp);
UPDATE hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
COMMIT;

UPDATE sbi_obj_par SET MULT_FL=0;
UPDATE sbi_obj_par SET MULT_FL=1 WHERE par_id IN (SELECT a.par_id FROM   sbi_parameters a, sbi_paruse m
WHERE a.par_id = m.par_id and selection_type = 'CHECK_LIST');
UPDATE sbi_paruse SET selection_type='LOOKUP' WHERE selection_type = 'CHECK_LIST'OR selection_type = 'LIST'

COMMIT;

UPDATE sbi_obj_par SET REQ_FL=0;
UPDATE sbi_obj_par SET REQ_FL=1 WHERE par_id IN (SELECT a.par_id FROM   sbi_parameters a, sbi_paruse m, sbi_paruse_ck r, sbi_checks c
WHERE a.par_id = m.par_id and m.use_id = r.use_id and r.check_id = c.check_id and c.value_type_cd = 'MANDATORY')

