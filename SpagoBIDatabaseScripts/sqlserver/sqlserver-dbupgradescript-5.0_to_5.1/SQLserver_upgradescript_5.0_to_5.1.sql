EXEC sp_rename 'SBI_OBJ_METADATA.DESCRIPTION', 'DESCR', 'COLUMN';
--if previous stmt doesn't work try with nexts:
--ALTER TABLE SBI_OBJ_METADATA ADD COLUMN DESCR VARCHAR(100) NULL;
--UPDATE SBI_OBJ_METADATA SET DESCR = DESCRIPTION;
--COMMIT;
--ALTER TABLE SBI_OBJ_METADATA DROP COLUMN DESCRIPTION;

-- 10/09/2014 Marco: added HIERARCHIES_MANAGEMENT authorization
INSERT INTO SBI_AUTHORIZATIONS
(ID, NAME, USER_IN, TIME_IN)
values ((SELECT NEXT_VAL FROM hibernate_sequences WHERE SEQUENCE_NAME='SBI_AUTHORIZATIONS'),
'HIERARCHIES_MANAGEMENT',
'server', current_timestamp) ;
commit;
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_AUTHORIZATIONS';
commit; 

INSERT INTO SBI_AUTHORIZATIONS
(ID, NAME, USER_IN, TIME_IN) 
values ((SELECT NEXT_VAL FROM hibernate_sequences WHERE SEQUENCE_NAME='SBI_AUTHORIZATIONS'), 
'VIEW_SOCIAL_ANALYSIS', 
'server', current_timestamp) ;
commit;
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_AUTHORIZATIONS';
commit;