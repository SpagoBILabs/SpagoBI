alter table SBI_OBJ_METADATA CHANGE DESCRIPTION  DESCR  VARCHAR(100) ;

INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN, SBI_VERSION_IN) VALUES 
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'), 
'SPAGOBI.SOCIAL_ANALYSIS_URL', 'SPAGOBI SOCIAL ANALYSIS URL', 'SPAGOBI SOCIAL ANALYSIS URL', true, '/SpagoBISocialAnalysis',
(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SOCIAL_CONFIGURATION', 'server', current_timestamp, '5.1');
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
commit;

INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN, SBI_VERSION_IN) VALUES 
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'), 
'SPAGOBI.SOCIAL_ANALYSIS_IS_ACTIVE', 'SPAGOBI SOCIAL ANALYSIS STATUS', 'SPAGOBI SOCIAL ANALYSIS STATUS', true, 'true',
(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SOCIAL_CONFIGURATION', 'server', current_timestamp, '5.1');
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
commit;