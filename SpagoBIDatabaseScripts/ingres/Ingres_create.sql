/*
Created		13/10/2008
Modified		09/12/2008
Project		
Model		
Company		
Author		
Version		
Database		Ingres 
*/

CREATE SEQUENCE SBI_AUDIT_SEQ;\p\g

Create table SBI_AUDIT (
	ID Integer NOT NULL with default next value for  SBI_AUDIT_SEQ,
	USERNAME Varchar(40) NOT NULL,
	USERGROUP Varchar(100),
	DOC_REF Integer,
	DOC_ID Integer,
	DOC_LABEL Varchar(20) NOT NULL,
	DOC_NAME Varchar(40) NOT NULL,
	DOC_TYPE Varchar(20) NOT NULL,
	DOC_STATE Varchar(20) NOT NULL,
	DOC_PARAMETERS Varchar(30000),
	SUBOBJ_REF Integer,
	SUBOBJ_ID Integer,
	SUBOBJ_NAME Varchar(50),
	SUBOBJ_OWNER Varchar(50),
	SUBOBJ_ISPUBLIC Smallint,
	ENGINE_REF Integer,
	ENGINE_ID Integer,
	ENGINE_LABEL Varchar(40) NOT NULL,
	ENGINE_NAME Varchar(40) NOT NULL,
	ENGINE_TYPE Varchar(20) NOT NULL,
	ENGINE_URL Varchar(400),
	ENGINE_DRIVER Varchar(400),
	ENGINE_CLASS Varchar(400),
	REQUEST_TIME timestamp NOT NULL,
	EXECUTION_START timestamp,
	EXECUTION_END timestamp,
	EXECUTION_TIME Integer,
	EXECUTION_STATE Varchar(20),
	ERROR Smallint,
	ERROR_MESSAGE Varchar(400),
	ERROR_CODE Varchar(20),
	EXECUTION_MODALITY Varchar(40),
Primary Key (ID)
) ;\p\g

CREATE SEQUENCE SBI_ACTIVITY_SEQ;\p\g

CREATE TABLE SBI_ACTIVITY_MONITORING (
  ID INTEGER  NOT NULL with default next value for  SBI_ACTIVITY_SEQ,
  ACTION_TIME   timestamp,
  USERNAME 	 	VARCHAR(40) NOT NULL,
  USERGROUP		VARCHAR(400),
  LOG_LEVEL 	VARCHAR(10) ,
  ACTION_CODE 	VARCHAR(45) NOT NULL,
  INFO 			VARCHAR(400),
  PRIMARY KEY (ID)
) ;\p\g


CREATE SEQUENCE SBI_BINARY_CONTENTS_SEQ;\p\g
Create table SBI_BINARY_CONTENTS (
	BIN_ID Integer NOT NULL with default next value for SBI_BINARY_CONTENTS_SEQ,
	BIN_CONTENT Long byte NOT NULL,
Primary Key (BIN_ID)
) ;\p\g

CREATE SEQUENCE SBI_CHECKS_SEQ;\p\g
Create table SBI_CHECKS (
	CHECK_ID Integer NOT NULL with default next value for  SBI_CHECKS_SEQ,
	DESCR Varchar(160),
	LABEL Varchar(20) NOT NULL,
	VALUE_TYPE_CD Varchar(20) NOT NULL,
	VALUE_TYPE_ID Integer NOT NULL,
	VALUE_1 Varchar(400),
	VALUE_2 Varchar(400),
	NAME Varchar(40) NOT NULL,
UNIQUE(LABEL),	
Primary Key (CHECK_ID)
) ;\p\g

CREATE SEQUENCE SBI_DATA_SET_SEQ;\p\g
Create table SBI_DATA_SET (
	DS_ID Integer NOT NULL with default next value for SBI_DATA_SET_SEQ,
	DESCR Varchar(160),
	LABEL Varchar(50) NOT NULL,
	NAME Varchar(50) NOT NULL,
	FILE_NAME Varchar(300),
	QUERY Long nvarchar,
	ADRESS Varchar(250),
	EXECUTOR_CLASS Varchar(250),
	PARAMS Varchar(1000),
	DS_METADATA Varchar(2000),
	DATA_SOURCE_ID Integer,
	OBJECT_TYPE Varchar(50),
	OPERATION Varchar(250),
	JCLASS_NAME    VARCHAR(100),
	SCRIPT   	   Long nvarchar,
	TRANSFORMER_ID INTEGER,
	PIVOT_COLUMN   VARCHAR(50),
	PIVOT_ROW      VARCHAR(50),
	PIVOT_VALUE    VARCHAR(50),
	num_rows smallint default 0,
	LANGUAGE_SCRIPT Varchar(50), 
UNIQUE(LABEL),	
Primary Key (DS_ID)
) ;\p\g

CREATE SEQUENCE SBI_DATA_SOURCE_SEQ;\p\g
Create table SBI_DATA_SOURCE (
	DS_ID Integer NOT NULL with default next value for SBI_DATA_SOURCE_SEQ,
	DESCR Varchar(160),
	LABEL Varchar(50) NOT NULL,
	JNDI Varchar(50),
	URL_CONNECTION Varchar(500),
	USERNAME Varchar(50),
	PWD Varchar(50),
	DRIVER Varchar(160),
	DIALECT_ID Integer NOT NULL,
	MULTI_SCHEMA TINYINT(1) NOT NULL DEFAULT '0',
	ATTR_SCHEMA VARCHAR(45) DEFAULT NULL ,
UNIQUE(LABEL),		
Primary Key (DS_ID)
) ;\p\g

CREATE SEQUENCE SBI_DIST_LIST_SEQ;\p\g
Create table SBI_DIST_LIST (
	DL_ID Integer NOT NULL with default next value for SBI_DIST_LIST_SEQ,
	NAME Varchar(40) NOT NULL,
	DESCR Varchar(160),
Primary Key (DL_ID)
) ;\p\g
CREATE SEQUENCE SBI_DIST_LIST_OBJECTS_SEQ;\p\g
Create table SBI_DIST_LIST_OBJECTS (
	REL_ID Integer NOT NULL with default next value for SBI_DIST_LIST_OBJECTS_SEQ,
	DOC_ID Integer NOT NULL,
	DL_ID Integer NOT NULL,
	XML Varchar(2000) NOT NULL,		
Primary Key (REL_ID)
) ;\p\g

CREATE SEQUENCE SBI_DIST_LIST_USER_SEQ;\p\g
Create table SBI_DIST_LIST_USER (
	DLU_ID Integer NOT NULL with default next value for SBI_DIST_LIST_USER_SEQ,
	LIST_ID Integer NOT NULL,
	USER_ID Varchar(40) NOT NULL,
	E_MAIL Varchar(70) NOT NULL,
UNIQUE(	LIST_ID , USER_ID),
Primary Key (DLU_ID)
) ;\p\g

CREATE SEQUENCE SBI_DOMAINS_SEQ START WITH 200;\p\g
Create table SBI_DOMAINS (
	VALUE_ID Integer NOT NULL with default next value for SBI_DOMAINS_SEQ,
	VALUE_CD Varchar(100) NOT NULL,
	VALUE_NM Varchar(40),
	DOMAIN_CD Varchar(20) NOT NULL,
	DOMAIN_NM Varchar(40),
	VALUE_DS Varchar(160),
	PARENT_ID Integer,
UNIQUE(	VALUE_CD, DOMAIN_CD),
Primary Key (VALUE_ID)
) ;\p\g

CREATE SEQUENCE SBI_DOSSIER_BIN_TEMP_SEQ;\p\g
Create table SBI_DOSSIER_BIN_TEMP (
	BIN_ID Integer NOT NULL with default next value for SBI_DOSSIER_BIN_TEMP_SEQ,
	PART_ID Integer NOT NULL,
	NAME Varchar(20),
	BIN_CONTENT Long byte NOT NULL,
	TYPE Varchar(20) NOT NULL,
	CREATION_DATE TIMESTAMP NOT NULL,
Primary Key (BIN_ID)
) ;\p\g

CREATE SEQUENCE SBI_DOSSIER_PRES_SEQ;\p\g
Create table SBI_DOSSIER_PRES (
	PRESENTATION_ID Integer NOT NULL with default next value for SBI_DOSSIER_PRES_SEQ,
	WORKFLOW_PROCESS_ID Float NOT NULL,
	BIOBJ_ID Integer NOT NULL,
	BIN_ID Integer NOT NULL,
	NAME Varchar(40) NOT NULL,
	PROG Integer,
	CREATION_DATE TIMESTAMP NOT NULL,
	APPROVED Smallint,
  Primary Key (PRESENTATION_ID)
) ;\p\g

CREATE SEQUENCE SBI_DOSSIER_TEMP_SEQ;\p\g
Create table SBI_DOSSIER_TEMP (
	PART_ID Integer NOT NULL with default next value for SBI_DOSSIER_TEMP_SEQ,
	WORKFLOW_PROCESS_ID Float NOT NULL,
	BIOBJ_ID Integer NOT NULL,
	PAGE_ID Integer NOT NULL,
Primary Key (PART_ID)
) ;\p\g

CREATE SEQUENCE SBI_ENGINES_SEQ;\p\g
Create table SBI_ENGINES (
	ENGINE_ID Integer NOT NULL with default next value for SBI_ENGINES_SEQ,
	ENCRYPT Smallint,
	NAME Varchar(40) NOT NULL,
	DESCR Varchar(160),
	MAIN_URL Varchar(400),
	SECN_URL Varchar(400),
	OBJ_UPL_DIR Varchar(400),
	OBJ_USE_DIR Varchar(400),
	DRIVER_NM Varchar(400),
	LABEL Varchar(40) NOT NULL,
	ENGINE_TYPE Integer NOT NULL,
	CLASS_NM Varchar(400),
	BIOBJ_TYPE Integer NOT NULL,
	DEFAULT_DS_ID Integer,
	USE_DATASOURCE TINYINT Default 0,
	USE_DATASET TINYINT Default 0,
UNIQUE(LABEL),	
Primary Key (ENGINE_ID)
) ;\p\g

CREATE SEQUENCE SBI_EVENTS_SEQ;\p\g
Create table SBI_EVENTS (
	ID Integer NOT NULL with default next value for SBI_EVENTS_SEQ,
	USER_EVENT Varchar(40) NOT NULL,
Primary Key (ID)
) ;\p\g

CREATE SEQUENCE SBI_EVENTS_LOG_SEQ;\p\g
Create table SBI_EVENTS_LOG (
	ID Integer NOT NULL with default next value for SBI_EVENTS_LOG_SEQ,
	USER_EVENT Varchar(40) NOT NULL,
	EVENT_DATE TIMESTAMP NOT NULL,
	DESCR Varchar(4000) NOT NULL,
	PARAMS Varchar(1000),
	HANDLER Varchar(400) Default 'it.eng.spagobi.events.handlers.DefaultEventPresentationHandler' NOT NULL,
Primary Key (ID)
) ;\p\g

Create table SBI_EVENTS_ROLES (
	EVENT_ID Integer NOT NULL,
	ROLE_ID Integer NOT NULL,
Primary Key (EVENT_ID,ROLE_ID)
) ;\p\g

CREATE SEQUENCE SBI_EXT_ROLES_SEQ;\p\g
Create table SBI_EXT_ROLES (
	EXT_ROLE_ID Integer NOT NULL with default next value for SBI_EXT_ROLES_SEQ,
	NAME Varchar(100),
	DESCR Varchar(160),
	CODE Varchar(20),
	ROLE_TYPE_CD Varchar(20) NOT NULL,
	ROLE_TYPE_ID Integer NOT NULL,
	SEE_SUBOBJECTS TINYINT Default 1,
	SEE_VIEWPOINTS TINYINT Default 1,
	SEE_SNAPSHOTS TINYINT Default 1,
	SEE_NOTES TINYINT Default 1,
	SEND_MAIL TINYINT Default 1,
	SAVE_INTO_FOLDER TINYINT Default 1,
	SAVE_REMEMBER_ME TINYINT Default 1,
	SEE_METADATA TINYINT Default 1,
	SAVE_METADATA TINYINT Default 1,
	SAVE_SUBOBJECTS TINYINT Default 1,
	BUILD_QBE_QUERY TINYINT Default 1,
Primary Key (EXT_ROLE_ID)
) ;\p\g

CREATE SEQUENCE SBI_FUNCTIONS_SEQ;\p\g
Create table SBI_FUNCTIONS (
	FUNCT_ID Integer NOT NULL with default next value for SBI_FUNCTIONS_SEQ,
	FUNCT_TYPE_CD Varchar(20) NOT NULL,
	PARENT_FUNCT_ID Integer,
	NAME Varchar(40),
	DESCR Varchar(160),
	PATH Varchar(400),
	CODE Varchar(40) NOT NULL,
	PROG Integer NOT NULL,
	FUNCT_TYPE_ID Integer NOT NULL,
UNIQUE(CODE),
Primary Key (FUNCT_ID)
) ;\p\g

Create table SBI_FUNC_ROLE (
	FUNCT_ID Integer NOT NULL,
	STATE_ID Integer NOT NULL,
	ROLE_ID Integer NOT NULL,
	STATE_CD Varchar(20),
Primary Key (FUNCT_ID,STATE_ID,ROLE_ID)
) ;\p\g

CREATE SEQUENCE SBI_GEO_FEATURES_SEQ;\p\g
Create table SBI_GEO_FEATURES (
	FEATURE_ID Integer NOT NULL with default next value for SBI_GEO_FEATURES_SEQ,
	NAME Varchar(40) NOT NULL,
	DESCR Varchar(160),
	TYPE Varchar(40),
UNIQUE(NAME),	
Primary Key (FEATURE_ID)
) ;\p\g

CREATE SEQUENCE SBI_GEO_MAPS_SEQ;\p\g
Create table SBI_GEO_MAPS (
	MAP_ID Integer NOT NULL with default next value for SBI_GEO_MAPS_SEQ,
	NAME Varchar(40) NOT NULL,
	DESCR Varchar(160),
	URL Varchar(400) NULL,
	FORMAT Varchar(40),
	BIN_ID INTEGER NOT NULL,
UNIQUE(NAME),	
Primary Key (MAP_ID)
) ;\p\g

CREATE SEQUENCE SBI_GEO_MAP_FEATURES_SEQ;\p\g
Create table SBI_GEO_MAP_FEATURES (
	MAP_ID Integer NOT NULL with default next value for SBI_GEO_MAP_FEATURES_SEQ,
	FEATURE_ID Integer NOT NULL,
	SVG_GROUP Varchar(40),
	VISIBLE_FLAG Varchar(1),
Primary Key (MAP_ID,FEATURE_ID)
) ;\p\g

CREATE SEQUENCE SBI_LOV_SEQ;\p\g
Create table SBI_LOV (
	LOV_ID Integer NOT NULL with default next value for SBI_LOV_SEQ,
	DESCR Varchar(160),
	LABEL Varchar(20) NOT NULL,
	INPUT_TYPE_CD Varchar(20) NOT NULL,
	DEFAULT_VAL Varchar(40),
	LOV_PROVIDER TEXT,
	INPUT_TYPE_ID Integer NOT NULL,
	PROFILE_ATTR Varchar(20),
	NAME Varchar(40) NOT NULL,
UNIQUE(LABEL),	
Primary Key (LOV_ID)
) ;\p\g

CREATE SEQUENCE SBI_MENU_SEQ;\p\g
Create table SBI_MENU (
	MENU_ID Integer NOT NULL with default next value for SBI_MENU_SEQ,
	NAME Varchar(50),
	DESCR Varchar(2000),
	PARENT_ID Integer,
	BIOBJ_ID Integer,
	VIEW_ICONS Smallint,
	HIDE_TOOLBAR Smallint,
	HIDE_SLIDERS Smallint,
	STATIC_PAGE Varchar(45),
	BIOBJ_PARAMETERS Long nvarchar null,
	SUBOBJ_NAME Varchar(50),
	SNAPSHOT_NAME Varchar(50),
	SNAPSHOT_HISTORY Integer,
	FUNCTIONALITY Varchar(50),
	INITIAL_PATH Varchar(400),
	PROG Integer Default 1 NOT NULL,
Primary Key (MENU_ID)
) ;\p\g

Create table SBI_MENU_ROLE (
	MENU_ID Integer NOT NULL,
	EXT_ROLE_ID Integer NOT NULL,
Primary Key (MENU_ID,EXT_ROLE_ID)
) ;\p\g

CREATE SEQUENCE SBI_OBJECTS_SEQ;\p\g
Create table SBI_OBJECTS (
	BIOBJ_ID Integer NOT NULL with default next value for SBI_OBJECTS_SEQ,
	ENGINE_ID Integer NOT NULL,
	DESCR Varchar(400),
	LABEL Varchar(20) NOT NULL,
	ENCRYPT Smallint,
	PATH Varchar(400),
	REL_NAME Varchar(400),
	STATE_ID Integer NOT NULL,
	STATE_CD Varchar(20) NOT NULL,
	BIOBJ_TYPE_CD Varchar(20) NOT NULL,
	BIOBJ_TYPE_ID Integer NOT NULL,
	SCHED_FL Smallint,
	EXEC_MODE_ID Integer,
	STATE_CONS_ID Integer,
	EXEC_MODE_CD Varchar(20),
	STATE_CONS_CD Varchar(20),
	NAME Varchar(200) NOT NULL,
	VISIBLE Smallint NOT NULL,
	UUID Varchar(40) NOT NULL,
	DATA_SOURCE_ID Integer,
	DATA_SET_ID Integer,
	CREATION_DATE TIMESTAMP NULL,
	CREATION_USER Varchar(45) NOT NULL,
	REFRESH_SECONDS Integer,
	PROF_VISIBILITY Varchar(400),
UNIQUE(LABEL),	
Primary Key (BIOBJ_ID)
) ;\p\g

Create table SBI_OBJECTS_RATING (
	USER_ID Varchar(127) NOT NULL,
	OBJ_ID Integer NOT NULL,
	RATING Integer NOT NULL,
Primary Key (USER_ID,OBJ_ID)
) ;\p\g
CREATE SEQUENCE SBI_OBJECT_NOTES_SEQ;\p\g
Create table SBI_OBJECT_NOTES (
	OBJ_NOTE_ID Integer NOT NULL with default next value for SBI_OBJECT_NOTES_SEQ,
	BIOBJ_ID Integer NOT NULL,
	BIN_ID Integer,
	EXEC_REQ Varchar(500),
	OWNER Varchar(50),
	ISPUBLIC TINYINT,
	CREATION_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	LAST_CHANGE_DATE TIMESTAMP NOT NULL Default CURRENT_TIMESTAMP,
Primary Key (OBJ_NOTE_ID)
) ;\p\g

CREATE SEQUENCE SBI_OBJECT_TEMPLATES_SEQ;\p\g
Create table SBI_OBJECT_TEMPLATES (
	OBJ_TEMP_ID Integer NOT NULL with default next value for SBI_OBJECT_TEMPLATES_SEQ,
	BIOBJ_ID Integer NOT NULL,
	BIN_ID Integer,
	NAME Varchar(50),
	PROG Integer,
	DIMENSION Varchar(20),
	CREATION_DATE Date NULL,
	CREATION_USER Varchar(45) NOT NULL,
	ACTIVE TINYINT,
Primary Key (OBJ_TEMP_ID)
) ;\p\g

Create table SBI_OBJ_FUNC (
	BIOBJ_ID Integer NOT NULL,
	FUNCT_ID Integer NOT NULL,
	PROG Integer,
Primary Key (BIOBJ_ID,FUNCT_ID)
) ;\p\g

CREATE SEQUENCE SBI_OBJ_PAR_SEQ;\p\g
Create table SBI_OBJ_PAR (
	OBJ_PAR_ID Integer NOT NULL with default next value for SBI_OBJ_PAR_SEQ,
	PAR_ID Integer NOT NULL,
	BIOBJ_ID Integer NOT NULL,
	LABEL Varchar(20) NOT NULL,
	REQ_FL Smallint,
	MOD_FL Smallint,
	VIEW_FL Smallint,
	MULT_FL Smallint,
	PROG Integer NOT NULL,
	PARURL_NM Varchar(20),
	PRIORITY Integer,
Primary Key (OBJ_PAR_ID)
) ;\p\g

Create table SBI_OBJ_PARUSE (
	OBJ_PAR_ID Integer NOT NULL,
	USE_ID Integer NOT NULL,
	OBJ_PAR_FATHER_ID Integer NOT NULL,
	FILTER_OPERATION Varchar(20) NOT NULL,
	PROG Integer NOT NULL,
	FILTER_COLUMN Varchar(30) NOT NULL,
	PRE_CONDITION Varchar(10),
	POST_CONDITION Varchar(10),
	LOGIC_OPERATOR Varchar(10),
Primary Key (OBJ_PAR_ID,USE_ID,OBJ_PAR_FATHER_ID,FILTER_OPERATION)
) ;\p\g

Create table SBI_OBJ_STATE (
	BIOBJ_ID Integer NOT NULL,
	STATE_ID Integer NOT NULL,
	START_DT Date NOT NULL,
	END_DT Date,
	NOTE Varchar(300),
Primary Key (BIOBJ_ID,STATE_ID,START_DT)
) ;\p\g

CREATE SEQUENCE SBI_PARAMETERS_SEQ;\p\g
Create table SBI_PARAMETERS (
	PAR_ID Integer NOT NULL with default next value for SBI_PARAMETERS_SEQ,
	DESCR Varchar(160),
	LENGTH Smallint NOT NULL,
	LABEL Varchar(20) NOT NULL,
	PAR_TYPE_CD Varchar(20) NOT NULL,
	MASK Varchar(20),
	PAR_TYPE_ID Integer NOT NULL,
	NAME Varchar(40) NOT NULL,
	FUNCTIONAL_FLAG Smallint Default 1 NOT NULL,
	TEMPORAL_FLAG Smallint Default 0 NOT NULL,
UNIQUE(LABEL),	
Primary Key (PAR_ID)
) ;\p\g

CREATE SEQUENCE SBI_PARUSE_SEQ;\p\g
Create table SBI_PARUSE (
	USE_ID Integer NOT NULL with default next value for SBI_PARUSE_SEQ,
	LOV_ID Integer,
	LABEL Varchar(20) NOT NULL,
	DESCR Varchar(160),
	PAR_ID Integer NOT NULL,
	NAME Varchar(40) NOT NULL,
	MAN_IN Integer NOT NULL,
	SELECTION_TYPE Varchar(20) Default 'LIST',
	MULTIVALUE_FLAG Integer Default 0,
UNIQUE(PAR_ID,LABEL),	
Primary Key (USE_ID)
) ;\p\g

Create table SBI_PARUSE_CK (
	USE_ID Integer NOT NULL ,
	CHECK_ID Integer NOT NULL,
	PROG Integer,
Primary Key (USE_ID,CHECK_ID)
) ;\p\g

Create table SBI_PARUSE_DET (
	USE_ID Integer NOT NULL,
	EXT_ROLE_ID Integer NOT NULL,
	PROG Integer,
	HIDDEN_FL Smallint,
	DEFAULT_VAL Varchar(40),
Primary Key (USE_ID,EXT_ROLE_ID)
) ;\p\g

CREATE SEQUENCE SBI_REMEMBER_ME_SEQ;\p\g
Create table SBI_REMEMBER_ME (
	ID Integer NOT NULL with default next value for SBI_REMEMBER_ME_SEQ,
	USERNAME Varchar(40) NOT NULL,
	BIOBJ_ID Integer NOT NULL,
	SUBOBJ_ID Integer,
	PARAMETERS Varchar(30000),
	NAME Varchar(50) NOT NULL,
	DESCRIPTION Varchar(30000),
Primary Key (ID)
) ;\p\g

Create table SBI_ROLE_TYPE_USER_FUNC (
	ROLE_TYPE_ID Integer NOT NULL,
	USER_FUNCT_ID Integer NOT NULL,
Primary Key (ROLE_TYPE_ID,USER_FUNCT_ID)
) ;\p\g

CREATE SEQUENCE SBI_SNAPSHOTS_SEQ;\p\g
Create table SBI_SNAPSHOTS (
	SNAP_ID Integer NOT NULL with default next value for SBI_SNAPSHOTS_SEQ,
	BIOBJ_ID Integer NOT NULL,
	BIN_ID Integer,
	NAME Varchar(50),
	DESCRIPTION Varchar(100),
	CREATION_DATE TIMESTAMP NOT NULL,
Primary Key (SNAP_ID)
) ;\p\g

CREATE SEQUENCE SBI_SUBOBJECTS_SEQ;\p\g
Create table SBI_SUBOBJECTS (
	SUBOBJ_ID Integer NOT NULL with default next value for SBI_SUBOBJECTS_SEQ,
	BIOBJ_ID Integer NOT NULL,
	BIN_ID Integer,
	NAME Varchar(50) NOT NULL,
	DESCRIPTION Varchar(100),
	OWNER Varchar(50),
	ISPUBLIC TINYINT,
	CREATION_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	LAST_CHANGE_DATE TIMESTAMP NOT NULL Default CURRENT_TIMESTAMP,
Primary Key (SUBOBJ_ID)
) ;\p\g

Create table SBI_SUBREPORTS (
	MASTER_RPT_ID Integer NOT NULL,
	SUB_RPT_ID Integer NOT NULL,
Primary Key (MASTER_RPT_ID,SUB_RPT_ID)
) ;\p\g

CREATE SEQUENCE SBI_USER_FUNC_SEQ;\p\g
Create table SBI_USER_FUNC (
	USER_FUNCT_ID Integer NOT NULL with default next value for SBI_USER_FUNC_SEQ,
	NAME Varchar(50),
	DESCRIPTION Varchar(100),
Primary Key (USER_FUNCT_ID)
) ;\p\g

CREATE SEQUENCE SBI_VIEWPOINTS_SEQ;\p\g
Create table SBI_VIEWPOINTS (
	VP_ID Integer NOT NULL with default next value for SBI_VIEWPOINTS_SEQ,
	BIOBJ_ID Integer NOT NULL,
	VP_NAME Varchar(40) NOT NULL,
	VP_OWNER Varchar(40),
	VP_DESC Varchar(160),
	VP_SCOPE Varchar(20) NOT NULL,
	VP_VALUE_PARAMS Long nvarchar,
	VP_CREATION_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
Primary Key (VP_ID)
) ;\p\g

CREATE SEQUENCE SBI_KPI_INST_PERIOD_SEQ;\p\g
Create table SBI_KPI_INST_PERIOD (
	KPI_INST_PERIOD_ID Integer NOT NULL with default next value for SBI_KPI_INST_PERIOD_SEQ,
	KPI_INSTANCE_ID Integer NOT NULL,
	PERIODICITY_ID Integer NOT NULL,
	DEFAULT_VALUE smallint default 0,
Primary Key (KPI_INST_PERIOD_ID)
) ;\p\g

CREATE SEQUENCE SBI_KPI_ROLE_SEQ;\p\g
Create table SBI_KPI_ROLE (
	id_kpi_role Integer NOT NULL with default next value for SBI_KPI_ROLE_SEQ,
	KPI_ID Integer NOT NULL,
	EXT_ROLE_ID Integer NOT NULL,
Primary Key (id_kpi_role)
) ;\p\g

CREATE SEQUENCE SBI_KPI_SEQ;\p\g
Create table SBI_KPI (
	KPI_ID Integer NOT NULL with default next value for SBI_KPI_SEQ,
	ID_MEASURE_UNIT Integer,
	DS_ID Integer,
	THRESHOLD_ID Integer,
	ID_KPI_PARENT Integer,
	NAME Varchar(400) NOT NULL,
	CODE Varchar(40) NOT NULL,
	METRIC Varchar(1000),
	DESCRIPTION Varchar(1000),
	WEIGHT Float,
	FLG_IS_FATHER Char(1),
	KPI_TYPE Integer,
	METRIC_SCALE_TYPE Integer,
  MEASURE_TYPE Integer,
	INTERPRETATION Varchar(1000),
	INPUT_ATTRIBUTES Varchar(1000),
	MODEL_REFERENCE Varchar(255),
	TARGET_AUDIENCE Varchar(1000),
UNIQUE(CODE),	
Primary Key (KPI_ID)
) ;\p\g

CREATE SEQUENCE SBI_KPI_DOCUMENTS_SEQ;\p\g
Create table SBI_KPI_DOCUMENTS (
	ID_KPI_DOC INTEGER  NOT NULL with default next value for SBI_KPI_DOCUMENTS_SEQ,
	BIOBJ_ID INTEGER NOT NULL,
	KPI_ID INTEGER NOT NULL,
 Primary Key (ID_KPI_DOC)
);\p\g

CREATE SEQUENCE SBI_MEASURE_UNIT_SEQ;\p\g
Create table SBI_MEASURE_UNIT (
	ID_MEASURE_UNIT Integer NOT NULL with default next value for SBI_MEASURE_UNIT_SEQ,
	NAME Varchar(400),
	SCALE_TYPE_ID Integer NOT NULL,
	SCALE_CD Varchar(40),
	SCALE_NM Varchar(400),
Primary Key (id_measure_unit)
) ;\p\g

CREATE SEQUENCE SBI_THRESHOLD_SEQ;\p\g
Create table SBI_THRESHOLD (
	THRESHOLD_ID Integer NOT NULL with default next value for SBI_THRESHOLD_SEQ,
	THRESHOLD_TYPE_ID Integer NOT NULL,
	NAME Varchar(400),
	DESCRIPTION Varchar(1000),
	CODE Varchar(45) NOT NULL,
UNIQUE(CODE),	
Primary Key (THRESHOLD_ID)
) ;\p\g

CREATE SEQUENCE SBI_THRESHOLD_VALUE_SEQ;\p\g
Create table SBI_THRESHOLD_VALUE (
	ID_THRESHOLD_VALUE Integer NOT NULL with default next value for SBI_THRESHOLD_VALUE_SEQ,
	THRESHOLD_ID Integer NOT NULL,
	SEVERITY_ID Integer,
	POSITION Integer,
	MIN_VALUE Float,
	MAX_VALUE Float,
	LABEL Varchar(20) NOT NULL,
	COLOUR Varchar(20),
	min_closed Smallint,
	max_closed Smallint,
	th_value Float,
UNIQUE(LABEL, THRESHOLD_ID),	
Primary Key (id_threshold_value)
) ;\p\g

CREATE SEQUENCE SBI_KPI_MODEL_SEQ;\p\g
Create table SBI_KPI_MODEL (
	KPI_MODEL_ID Integer NOT NULL with default next value for SBI_KPI_MODEL_SEQ,
	KPI_ID Integer,
	KPI_MODEL_TYPE_ID Integer NOT NULL,
	KPI_PARENT_MODEL_ID Integer,
	KPI_MODEL_CD Varchar(40) NOT NULL,
	KPI_MODEL_NM Varchar(400),
	KPI_MODEL_DESC Varchar(1000),
	KPI_MODEL_LBL VARCHAR(100) NOT NULL,
UNIQUE(KPI_MODEL_LBL),	
Primary Key (KPI_MODEL_ID)
) ;\p\g

ALTER TABLE SBI_KPI_MODEL ADD UNIQUE INDEX UNIQUE_PAR_ID_CD(KPI_PARENT_MODEL_ID,
 KPI_MODEL_CD) ;\p\g

CREATE SEQUENCE SBI_KPI_MODEL_ATTR_SEQ;\p\g
Create table SBI_KPI_MODEL_ATTR (
	KPI_MODEL_ATTR_ID Integer NOT NULL with default next value for SBI_KPI_MODEL_ATTR_SEQ,
	KPI_MODEL_ATTR_TYPE_ID Integer NOT NULL,
	KPI_MODEL_ATTR_CD Varchar(40),
	KPI_MODEL_ATTR_NM Varchar(400),
	KPI_MODEL_ATTR_DESCR Varchar(1000),
Primary Key (KPI_MODEL_ATTR_ID)
) ;\p\g
CREATE SEQUENCE SBI_KPI_MODEL_ATTR_VAL_SEQ;\p\g
Create table SBI_KPI_MODEL_ATTR_VAL (
	KPI_MODEL_ATTR_VAL_ID Integer NOT NULL with default next value for SBI_KPI_MODEL_ATTR_VAL_SEQ,
	KPI_MODEL_ATTR_ID Integer NOT NULL,
	KPI_MODEL_ID Integer NOT NULL,
	VALUE Varchar(2000),
Primary Key (KPI_MODEL_ATTR_VAL_ID)
) ;\p\g
CREATE SEQUENCE SBI_KPI_PERIODICITY_SEQ;\p\g
Create table SBI_KPI_PERIODICITY (
	ID_KPI_PERIODICITY Integer NOT NULL with default next value for SBI_KPI_PERIODICITY_SEQ,
	NAME Varchar(400) NOT NULL,
	MONTHS Integer,
	DAYS Integer,
	HOURS Integer,
	MINUTES Integer,
	CHRON_STRING Varchar(20),
	START_DATE TIMESTAMP,
UNIQUE(NAME),	
Primary Key (ID_KPI_PERIODICITY)
) ;\p\g

CREATE SEQUENCE SBI_KPI_INSTANCE_SEQ;\p\g
Create table SBI_KPI_INSTANCE (
	id_kpi_instance Integer NOT NULL with default next value for SBI_KPI_INSTANCE_SEQ,
	KPI_ID Integer NOT NULL,
	THRESHOLD_ID Integer,
	CHART_TYPE_ID Integer,
	ID_MEASURE_UNIT Integer,
	WEIGHT Float,
	TARGET Float,
	BEGIN_DT Date,
Primary Key (id_kpi_instance)
) ;\p\g
CREATE SEQUENCE SBI_KPI_INSTANCE_HISTORY_SEQ;\p\g
Create table SBI_KPI_INSTANCE_HISTORY (
	ID_KPI_INSTANCE_HISTORY Integer NOT NULL with default next value for SBI_KPI_INSTANCE_HISTORY_SEQ,
	ID_KPI_INSTANCE Integer NOT NULL,
	THRESHOLD_ID Integer,
	CHART_TYPE_ID Integer,
	ID_MEASURE_UNIT Integer,
	WEIGHT Float,
	TARGET Float,
	BEGIN_DT Date,
	END_DT Date,
Primary Key (id_kpi_instance_history)
) ;\p\g
CREATE SEQUENCE SBI_KPI_VALUE_SEQ;\p\g
Create table SBI_KPI_VALUE (
	ID_KPI_INSTANCE_VALUE Integer NOT NULL with default next value for SBI_KPI_VALUE_SEQ,
	ID_KPI_INSTANCE Integer NOT NULL,
	RESOURCE_ID Integer NULL,
	VALUE Varchar(40),
	BEGIN_DT Date,
	END_DT Date,
	DESCRIPTION Varchar(100),
	XML_DATA Long nvarchar,
Primary Key (id_kpi_instance_value)
) ;\p\g
CREATE SEQUENCE SBI_KPI_MODEL_INST_SEQ;\p\g
Create table SBI_KPI_MODEL_INST (
	KPI_MODEL_INST Integer NOT NULL with default next value for SBI_KPI_MODEL_INST_SEQ,
	KPI_MODEL_INST_PARENT Integer,
	KPI_MODEL_ID Integer,
	ID_KPI_INSTANCE Integer,
	NAME Varchar(400),
	LABEL Varchar(100) NOT NULL,	
	DESCRIPTION Varchar(1000),
	START_DATE Date,
	END_DATE Date,
	modelUUID Varchar(400),
UNIQUE(LABEL),	
Primary Key (KPI_MODEL_INST)
) ;\p\g

CREATE SEQUENCE SBI_RESOURCES_SEQ;\p\g
Create table SBI_RESOURCES (
	RESOURCE_ID Integer NOT NULL with default next value for SBI_RESOURCES_SEQ,
	RESOURCE_TYPE_ID Integer NOT NULL,
	TABLE_NAME Varchar(40),
	COLUMN_NAME Varchar(40),
	RESOURCE_NAME Varchar(40) NOT NULL,
	RESOURCE_CODE VARCHAR(45) NOT NULL,
UNIQUE(RESOURCE_CODE),	
Primary Key (RESOURCE_ID)
) ;\p\g
CREATE SEQUENCE SBI_KPI_MODEL_RESOURCES_SEQ;\p\g 
Create table SBI_KPI_MODEL_RESOURCES (
	KPI_MODEL_RESOURCES_ID Integer NOT NULL with default next value for SBI_KPI_MODEL_RESOURCES_SEQ,
	RESOURCE_ID Integer NOT NULL,
	KPI_MODEL_INST Integer NOT NULL,
Primary Key (KPI_MODEL_RESOURCES_ID)
) ;\p\g

CREATE SEQUENCE SBI_ALARM_SEQ;\p\g
Create table SBI_ALARM (
	ALARM_ID Integer NOT NULL with default next value for SBI_ALARM_SEQ,
	id_kpi_instance Integer,
	MODALITY_ID Integer NOT NULL,
	DOCUMENT_ID Integer,
	LABEL Varchar(50) NOT NULL,
	NAME Varchar(50),
	DESCR Varchar(200),
	TEXT Varchar(1000),
	URL Varchar(20),
	SINGLE_EVENT Char(1),
	AUTO_DISABLED Char(1),
	id_threshold_value Integer,
UNIQUE(LABEL),	
Primary Key (ALARM_ID)
) ;\p\g

CREATE SEQUENCE SBI_ALARM_EVENT_SEQ;\p\g
Create table SBI_ALARM_EVENT (
	ALARM_EVENT_ID Integer NOT NULL with default next value for SBI_ALARM_EVENT_SEQ,
	ALARM_ID Integer NOT NULL,
	EVENT_TS Date,
	ACTIVE Char(1),
	KPI_VALUE Varchar(50),
	THRESHOLD_VALUE Varchar(50),
	KPI_NAME Varchar(100),
	RESOURCES Varchar(200),
	KPI_DESCRIPTION Varchar (100),
	RESOURCE_ID Integer,
  KPI_INSTANCE_ID Integer,	
Primary Key (ALARM_EVENT_ID)
) ;\p\g

CREATE SEQUENCE SBI_ALARM_CONTACT_SEQ;\p\g
Create table SBI_ALARM_CONTACT (
	ALARM_CONTACT_ID Integer NOT NULL with default next value for SBI_ALARM_CONTACT_SEQ,
	NAME Varchar(100) NOT NULL,
	EMAIL Varchar(100),
	MOBILE Varchar(50),
	RESOURCES Varchar(20),
UNIQUE(NAME),	
Primary Key (ALARM_CONTACT_ID)
) ;\p\g

Create table SBI_ALARM_DISTRIBUTION (
	ALARM_CONTACT_ID Integer NOT NULL,
	ALARM_ID Integer NOT NULL,
Primary Key (ALARM_CONTACT_ID,ALARM_ID)
) ;\p\g

Create table SBI_EXPORTERS (
	ENGINE_ID Integer NOT NULL,
	DOMAIN_ID Integer NOT NULL,
	DEFAULT_VALUE SMALLINT NULL,
Primary Key (ENGINE_ID,DOMAIN_ID)
) ;\p\g

CREATE SEQUENCE SBI_OBJ_METADATA_SEQ;\p\g
CREATE TABLE SBI_OBJ_METADATA (
	OBJ_META_ID 		INTEGER NOT NULL with default next value for SBI_OBJ_METADATA_SEQ,
    LABEL	 	        VARCHAR(20) NOT NULL,
    NAME 	            VARCHAR(40) NOT NULL,
    DESCRIPTION	        VARCHAR(100),  
    DATA_TYPE_ID	    INTEGER NOT NULL,
    CREATION_DATE 	    TIMESTAMP NOT NULL,    
    UNIQUE(LABEL),	
	PRIMARY KEY (OBJ_META_ID)
);\p\g

CREATE SEQUENCE SBI_OBJ_METACONTENTS_SEQ;\p\g
CREATE TABLE SBI_OBJ_METACONTENTS (
  OBJ_METACONTENT_ID INTEGER  NOT NULL with default next value for SBI_OBJ_METACONTENTS_SEQ,
  OBJMETA_ID 		 INTEGER  NOT NULL ,
  BIOBJ_ID 			 INTEGER  NOT NULL,
  SUBOBJ_ID 		 INTEGER,
  BIN_ID 			 INTEGER,
  CREATION_DATE 	 TIMESTAMP NOT NULL,   
  LAST_CHANGE_DATE   TIMESTAMP NOT NULL,   
    PRIMARY KEY (OBJ_METACONTENT_ID)
);\p\g


CREATE UNIQUE INDEX XAK1SBI_OBJ_METACONTENTS ON SBI_OBJ_METACONTENTS
(
        OBJMETA_ID,
        BIOBJ_ID,
        SUBOBJ_ID
);\p\g

CREATE SEQUENCE SBI_CONFIG_SEQ
\p\g
CREATE TABLE SBI_CONFIG (
	ID 				INTEGER NOT NULL with default next value for SBI_CONFIG_SEQ,
	LABEL			VARCHAR(100) NOT NULL,
	NAME			VARCHAR(100) NULL,
	DESCRIPTION 	VARCHAR(500) NULL,
	IS_ACTIVE 		TINYINT Default 1,
	VALUE_CHECK 	VARCHAR(1000) NULL,
	VALUE_TYPE_ID 	INTEGER NULL,    
 PRIMARY KEY (ID))
 \p\g
 CREATE TABLE SBI_USER (
	USER_ID VARCHAR(100) NOT NULL,
	PASSWORD VARCHAR(150),
	FULL_NAME VARCHAR(255),
	ID INTEGER NOT NULL,
 PRIMARY KEY (ID))
\p\g
CREATE TABLE SBI_ATTRIBUTE (
	ATTRIBUTE_NAME VARCHAR(255) NOT NULL,
	DESCRIPTION VARCHAR(500) NOT NULL,
	ATTRIBUTE_ID INTEGER NOT NULL,
 PRIMARY KEY (ATTRIBUTE_ID))
\p\g
CREATE TABLE SBI_USER_ATTRIBUTES (
	ID INTEGER NOT NULL,
	ATTRIBUTE_ID INTEGER NOT NULL,
	ATTRIBUTE_VALUE VARCHAR(500),
 PRIMARY KEY (ID,ATTRIBUTE_ID))
 \p\g
CREATE TABLE SBI_EXT_USER_ROLES (
	ID INTEGER NOT NULL,
	EXT_ROLE_ID INTEGER NOT NULL,
 PRIMARY KEY (ID,EXT_ROLE_ID))
\p\g
