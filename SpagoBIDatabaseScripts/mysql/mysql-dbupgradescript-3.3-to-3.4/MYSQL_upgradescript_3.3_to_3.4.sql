CREATE TABLE SBI_PROGRESS_THREAD (
       PROGRESS_THREAD_ID   INTEGER NOT NULL,
       USER_ID              VARCHAR(100) NOT NULL,
       PARTIAL              INTEGER,
       TOTAL        	      INTEGER,
       FUNCTION_CD         VARCHAR(200),
       STATUS              VARCHAR(4000),
       RANDOM_KEY			VARCHAR(4000),	
       TYPE           VARCHAR(200),
       PRIMARY KEY (PROGRESS_THREAD_ID)
);
 
insert into hibernate_sequences(next_val,sequence_name) values (1, 'SBI_PROGRESS_THREAD');

ALTER TABLE SBI_EXT_ROLES ADD COLUMN DO_MASSIVE_EXPORT BOOLEAN DEFAULT TRUE;

INSERT INTO SBI_CONFIG ( ID, LABEL, NAME, DESCRIPTION, IS_ACTIVE, VALUE_CHECK, VALUE_TYPE_ID, USER_IN, TIME_IN) VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_CONFIG'),'JNDI_THREAD_MANAGER', 'JNDI_THREAD_MANAGER', 'Jndi to build work manager', true, 'java:/comp/env/wm/SpagoWorkManager',(select VALUE_ID from SBI_DOMAINS where VALUE_CD = 'STRING' AND DOMAIN_CD = 'PAR_TYPE'), 'biadmin', current_timestamp);

update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_CONFIG';

ALTER TABLE sbi_udp_value MODIFY COLUMN VALUE VARCHAR(1000);

commit;


INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN) 
	VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'MOBILE_REPORT','sbidomains.nm.mobile.report','BIOBJ_TYPE','BI Object types','sbidomains.ds.mobile.report', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_DOMAINS';	
INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN) 
	VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'MOBILE_CHART','sbidomains.nm.mobile.chart','BIOBJ_TYPE','BI Object types','sbidomains.ds.mobile.chart', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_DOMAINS';		
INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN) 
	VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
	'MOBILE_COCKPIT','sbidomains.nm.mobile.cockpit','BIOBJ_TYPE','BI Object types','sbidomains.ds.mobile.cockpit', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_DOMAINS';	
commit;


INSERT INTO SBI_ENGINES (ENGINE_ID,NAME, ENCRYPT, LABEL,MAIN_URL, DRIVER_NM, ENGINE_TYPE,BIOBJ_TYPE,USE_DATASOURCE,USE_DATASET, USER_IN, TIME_IN) VALUES
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_ENGINES'),'Mobile Table Engine', 0, 'SpagoBITableMobileEn','/SpagoBIMobileEngine/servlet/AdapterHTTP?ACTION_NAME=MOBILE_ENGINE_START_ACTION','it.eng.spagobi.engines.drivers.mobile.report.MobileReportDriver',(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ENGINE_TYPE' AND VALUE_CD = 'EXT'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'BIOBJ_TYPE' AND VALUE_CD = 'MOBILE_REPORT'),false, true, 'biadmin', current_timestamp);
update HIBERNATE_SEQUENCES set next_val = next_val+1 where  sequence_name = 'SBI_ENGINES';
commit;

INSERT INTO SBI_ENGINES (ENGINE_ID, NAME, ENCRYPT, LABEL,MAIN_URL, DRIVER_NM, ENGINE_TYPE,BIOBJ_TYPE,USE_DATASOURCE,USE_DATASET, USER_IN, TIME_IN) VALUES
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_ENGINES'),'Mobile Chart Engine', 0, 'SpagoBIChartMobileEn','/SpagoBIMobileEngine/servlet/AdapterHTTP?ACTION_NAME=MOBILE_ENGINE_START_ACTION','it.eng.spagobi.engines.drivers.mobile.chart.MobileChartDriver',(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ENGINE_TYPE' AND VALUE_CD = 'EXT'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'BIOBJ_TYPE' AND VALUE_CD = 'MOBILE_CHART'),false, true, 'biadmin', current_timestamp);
update HIBERNATE_SEQUENCES set next_val = next_val+1 where  sequence_name = 'SBI_ENGINES';
commit;

INSERT INTO SBI_ENGINES (ENGINE_ID, NAME, ENCRYPT, LABEL,MAIN_URL, DRIVER_NM, ENGINE_TYPE,BIOBJ_TYPE,USE_DATASOURCE,USE_DATASET, USER_IN, TIME_IN) VALUES
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_ENGINES'),'Mobile Cockpit Engine', 0, 'SpagoBICockpitMobile','/SpagoBIMobileEngine/servlet/AdapterHTTP?ACTION_NAME=MOBILE_ENGINE_START_ACTION','it.eng.spagobi.engines.drivers.mobile.cockpit.MobileCockpitDriver',(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ENGINE_TYPE' AND VALUE_CD = 'EXT'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'BIOBJ_TYPE' AND VALUE_CD = 'MOBILE_COCKPIT'),false, true, 'biadmin', current_timestamp);
update HIBERNATE_SEQUENCES set next_val = next_val+1 where  sequence_name = 'SBI_ENGINES';
commit;


INSERT INTO SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),
    'CHART','Chart','BIOBJ_TYPE','BI Object types','sbidomains.ds.chart', 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where  sequence_name = 'SBI_DOMAINS';
commit;

INSERT INTO SBI_ENGINES (ENGINE_ID, NAME, ENCRYPT, LABEL,MAIN_URL, DRIVER_NM, ENGINE_TYPE,BIOBJ_TYPE,USE_DATASOURCE,USE_DATASET, USER_IN, TIME_IN) VALUES
((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_ENGINES'),'Chart External Engine', 0, 'SpagoBIJSChartEngine','/SpagoBIChartEngine/servlet/AdapterHTTP','it.eng.spagobi.engines.drivers.chart.ChartDriver',(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ENGINE_TYPE' AND VALUE_CD = 'EXT'),(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'BIOBJ_TYPE' AND VALUE_CD = 'CHART'),false, true, 'biadmin', current_timestamp);
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_ENGINES';
commit;

INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE) 
	VALUES ((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL = 'SpagoBIJSChartEngine'),
	(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'PDF'), 
	false);
commit;
INSERT INTO SBI_EXPORTERS (ENGINE_ID,DOMAIN_ID,DEFAULT_VALUE) 
	VALUES ((SELECT ENGINE_ID FROM SBI_ENGINES WHERE LABEL = 'SpagoBIJSChartEngine'),
	(SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'EXPORT_TYPE' AND VALUE_CD = 'JPG'), 
	true);
commit;
UPDATE SBI_ENGINES SET MAIN_URL = '/SpagoBIConsoleEngine/servlet/AdapterHTTP', DRIVER_NM = 'it.eng.spagobi.engines.drivers.console.ConsoleDriver' WHERE LABEL = 'ConsoleEngine';
COMMIT;



update SBI_DOMAINS set VALUE_CD = 'FREE_INQUIRY', VALUE_NM ='sbidomains.nm.freeinquiry', VALUE_DS = 'sbidomains.ds.freeinquiry'
where  VALUE_CD = 'DATAMART' AND DOMAIN_CD = 'BIOBJ_TYPE';

update SBI_DOMAINS set VALUE_CD = 'ADHOC_REPORTING', VALUE_NM ='sbidomains.nm.adhoc_reporting', VALUE_DS = 'sbidomains.ds.adhoc_reporting' 
where  VALUE_CD = 'WORKSHEET' AND DOMAIN_CD = 'BIOBJ_TYPE';

update SBI_DOMAINS set VALUE_CD = 'COCKPIT', VALUE_NM ='sbidomains.nm.cockpit', VALUE_DS = 'sbidomains.ds.cockpit'  
where  VALUE_CD = 'DOCUMENT_COMPOSITE' AND DOMAIN_CD = 'BIOBJ_TYPE';

update SBI_DOMAINS set VALUE_CD = 'COLLABORATION', VALUE_NM ='sbidomains.nm.collaboration', VALUE_DS = 'sbidomains.ds.collaboration'  
where  VALUE_CD = 'DOSSIER' AND DOMAIN_CD = 'BIOBJ_TYPE';
					
update SBI_DOMAINS set VALUE_CD = 'LOCATION_INTELLIGENCE', VALUE_NM ='sbidomains.nm.location_intelligence', VALUE_DS = 'sbidomains.ds.location_intelligence'  
where  VALUE_CD = 'MAP' AND DOMAIN_CD = 'BIOBJ_TYPE';

update SBI_DOMAINS set VALUE_CD = 'EXTERNAL_PROCESS', VALUE_NM ='sbidomains.nm.external_process' , VALUE_DS = 'sbidomains.ds.external_process'  
where  VALUE_CD = 'PROCESS' AND DOMAIN_CD = 'BIOBJ_TYPE';
					
					
INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM,VALUE_DS) values (  maxId  , 'REAL_TIME', 'sbidomains.nm.realtime','BIOBJ_TYPE','BI Object type','sbidomains.ds.realtime');
INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM,VALUE_DS) values (  maxId  , 'MOBILE_REPORT', 'sbidomains.nm.mobile.report','BIOBJ_TYPE','BI Object type','sbidomains.ds.mobile.report');
INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM,VALUE_DS) values (  maxId  , 'MOBILE_CHART', 'sbidomains.nm.mobile.chart','BIOBJ_TYPE','BI Object type','sbidomains.ds.mobile.chart');
INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM,VALUE_DS) values (  maxId  , 'MOBILE_COCKPIT', 'sbidomains.nm.mobile.cockpit','BIOBJ_TYPE','BI Object type','sbidomains.ds.mobile.cockpit');
INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD, VALUE_NM, DOMAIN_CD, DOMAIN_NM,VALUE_DS) values (  maxId  , 'CHART', 'sbidomains.nm.chart','BIOBJ_TYPE','BI Object type','sbidomains.ds.chart');

update SBI_ENGINES set BIOBJ_TYPE = (SELECT VALUE_ID FROM sbi_domains s WHERE VALUE_CD = 'REAL_TIME' AND DOMAIN_CD = 'BIOBJ_TYPE') WHERE CLASS_NM = 'it.eng.spagobi.engines.dashboard.SpagoBIDashboardInternalEngine';
update SBI_ENGINES set BIOBJ_TYPE = (SELECT VALUE_ID FROM sbi_domains s WHERE VALUE_CD = 'REPORT' AND DOMAIN_CD = 'BIOBJ_TYPE') WHERE DRIVER_NM = 'it.eng.spagobi.engines.drivers.accessibility.AccessibilityDriver';
update SBI_ENGINES set BIOBJ_TYPE = (SELECT VALUE_ID FROM sbi_domains s WHERE VALUE_CD = 'EXTERNAL_PROCESS' AND DOMAIN_CD = 'BIOBJ_TYPE') WHERE DRIVER_NM = 'it.eng.spagobi.engines.drivers.commonj.CommonjDriver';
update SBI_ENGINES set BIOBJ_TYPE = (SELECT VALUE_ID FROM sbi_domains s WHERE VALUE_CD = 'REAL_TIME' AND DOMAIN_CD = 'BIOBJ_TYPE') WHERE DRIVER_NM = 'it.eng.spagobi.engines.drivers.generic.GenericDriver' AND MAIN_URL='/SpagoBIConsoleEngine/servlet/AdapterHTTP?ACTION_NAME=CONSOLE_ENGINE_START_ACTION';
update SBI_ENGINES set BIOBJ_TYPE = (SELECT VALUE_ID FROM sbi_domains s WHERE VALUE_CD = 'FREE_INQUIRY' AND DOMAIN_CD = 'BIOBJ_TYPE') WHERE DRIVER_NM = 'it.eng.spagobi.engines.drivers.smartfilter.SmartFilterDriver';

update SBI_OBJECTS set BIOBJ_TYPE_CD = 'REAL_TIME' WHERE BIOBJ_TYPE_CD = 'DASH';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'FREE_INQUIRY' WHERE BIOBJ_TYPE_CD = 'DATAMART';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'ADHOC_REPORTING' WHERE BIOBJ_TYPE_CD = 'WORKSHEET';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'LOCATION_INTELLIGENCE' WHERE BIOBJ_TYPE_CD = 'GEO';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'COCKPIT' WHERE BIOBJ_TYPE_CD = 'DOCUMENT_COMPOSITE';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'COLLABORATION' WHERE BIOBJ_TYPE_CD = 'DOSSIER';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'LOCATION_INTELLIGENCE' WHERE BIOBJ_TYPE_CD = 'MAP';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'EXTERNAL_PROCESS' WHERE BIOBJ_TYPE_CD = 'PROCESS';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'REPORT' WHERE BIOBJ_TYPE_CD = 'ACCESSIBLE_HTML';
update SBI_OBJECTS set BIOBJ_TYPE_CD = 'REAL_TIME' WHERE BIOBJ_TYPE_CD = 'CONSOLE';

