CREATE TABLE SBI_PROGRESS_THREAD (
       PROGRESS_THREAD_ID   INTEGER NOT NULL,
       USER_ID              VARCHAR(100) NOT NULL,
       PARTIAL              INTEGER,
       TOTAL        	      INTEGER,
       FUNCTION_CD         VARCHAR(200),
       STATUS              VARCHAR(4000),
       RANDOM_KEY			      VARCHAR(4000),	
       TYPE			          VARCHAR(200),	
              CONSTRAINT XAK1SBI_PROGRESS_THREAD UNIQUE  (PROGRESS_THREAD_ID),
       PRIMARY KEY (PROGRESS_THREAD_ID)
);\p\g

insert into hibernate_sequences(next_val,sequence_name) values (1, 'SBI_PROGRESS_THREAD');\p\g

ALTER TABLE SBI_EXT_ROLES ADD COLUMN DO_MASSIVE_EXPORT TINYINT DEFAULT 1;\p\g

insert into hibernate_sequences(next_val,sequence_name) values (ifnull((select max(PROGRESS_THREAD_ID)+1 from SBI_PROGRESS_THREAD) ,1),'SBI_PROGRESS_THREAD');\p\g

ALTER TABLE SBI_UDP_VALUE MODIFY COLUMN VALUE VARCHAR(1000) DEFAULT NULL;\p\g

INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, USER_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'JNDI_THREAD_MANAGER', 'JNDI_THREAD_MANAGER', 'Jndi to build work manager', true, 'java:/comp/env/wm/SpagoWorkManager',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'biadmin');\p\g

update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';\p\g

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
	VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'XLSX','XLSX','EXPORT_TYPE','Exporters type','Export type', 'biadmin', current_timestamp);\p\g
	
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_DOMAINS';\p\g

INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE)values((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL='WorksheetEngine'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'XLSX'), false);\p\g

commit;\p\g