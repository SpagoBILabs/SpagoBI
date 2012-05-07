ALTER TABLE SBI_LOV ADD DATASET_ID INTEGER;\p\g 
ALTER TABLE SBI_LOV ADD CONSTRAINT SBI_LOV_2 FOREIGN KEY  (DATASET_ID) REFERENCES SBI_DATA_SET (DS_ID);\p\g

INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
    'DATASET','sbidomains.nm.dataset','INPUT_TYPE','Input mode and values','sbidomains.ds.dataset', 'server', current_timestamp);\p\g
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';\p\g
commit;\p\g