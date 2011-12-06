CREATE TABLE SBI_I18N_MESSAGES (
  LANGUAGE_CD INTEGER NOT NULL,
  LABEL VARCHAR(200) NOT NULL,
  MESSAGE NVARCHAR,
  PRIMARY KEY (LANGUAGE_CD, LABEL)
);\p\g

ALTER TABLE SBI_I18N_MESSAGES ADD CONSTRAINT FK_SBI_I18N_MESSAGES FOREIGN KEY (LANGUAGE_CD) REFERENCES SBI_DOMAINS(VALUE_ID) ON DELETE RESTRICT;;


INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'ITA','Italian','LANG','language ISO Code','Italian', 'biadmin');
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'ENG','English','LANG','language ISO Code','English', 'biadmin');
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'FRA','French','LANG','language ISO Code','French', 'biadmin');
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN)
VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'SPA','Spanish','LANG','language ISO Code','Spanish', 'biadmin');
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;