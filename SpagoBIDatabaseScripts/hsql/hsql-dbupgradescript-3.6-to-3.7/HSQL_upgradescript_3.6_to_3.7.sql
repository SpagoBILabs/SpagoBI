--inserts configuration for check of role in login module
INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'SPAGOBI.SECURITY.CHECK_ROLE_LOGIN', 'SPAGOBI.SECURITY.CHECK_ROLE_LOGIN', 'Check the correct role in login action', false, 'false',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SECURITY', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, CATEGORY, USER_IN, TIME_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'SPAGOBI.SECURITY.ROLE_LOGIN', 'SPAGOBI.SECURITY.ROLE_LOGIN', 'The value of the role to check at login module', false, '',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'SECURITY', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';
UPDATE SBI_DOMAINS SET VALUE_CD = 'org.hibernate.dialect.MySQLInnoDBDialect' WHERE VALUE_CD = 'org.hibernate.dialect.MySQLDialect';
COMMIT;
