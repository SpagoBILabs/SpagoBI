ALTER TABLE SBI_OBJ_METADATA ALTER COLUMN DESCRIPTION RENAME TO DESCR;

INSERT INTO SBI_AUTHORIZATIONS
(ID, NAME, USER_IN, TIME_IN)
values ((SELECT NEXT_VAL FROM hibernate_sequences WHERE SEQUENCE_NAME='SBI_AUTHORIZATIONS'),
'HIERARCHIES_MANAGEMENT',
'server', current_timestamp) ;
commit;
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_AUTHORIZATIONS';
commit; 