COMMENT ON TABLE SBI_CHECKS IS 'Checks to validate paramenters data entry.';
COMMENT ON COLUMN SBI_CHECKS.VALUE_TYPE_ID IS 'Value type identifier.';
COMMENT ON COLUMN SBI_CHECKS.VALUE_TYPE_CD IS 'Value type code (denormalized form SBI_DOMAINS). Example: boolean, single value, range (double value).';
COMMENT ON COLUMN SBI_CHECKS.VALUE_1 IS 'Lower threshold of a range or punctual value for a comparison.';
COMMENT ON COLUMN SBI_CHECKS.VALUE_2 IS 'Superior threshold of a range.';
COMMENT ON COLUMN SBI_CHECKS.LABEL IS 'Parameter label (short textual identifier).';
COMMENT ON COLUMN SBI_CHECKS.DESCR IS 'Parameter description';
COMMENT ON TABLE SBI_DOMAINS IS 'Support domains for defined data values.
The table contains the possible values of: 1) STATE; 2) BIOBJ Type; 3) PARAMETER Type; 4) INPUT type; 4) VALUE Type; 5) ROLE type; 6) Functionality type; 7) BIObject execution mode; 8) BIObject state handling;';
COMMENT ON TABLE SBI_ENGINES IS 'Engine repository. The table contains all Engine information for its servlet invocation.';
COMMENT ON COLUMN SBI_ENGINES.ENGINE_ID IS 'Engine idenitifier';
COMMENT ON COLUMN SBI_ENGINES.ENCRYPT IS 'Encripting support';
COMMENT ON COLUMN SBI_ENGINES.NAME IS 'Engine name';
COMMENT ON COLUMN SBI_ENGINES.DESCR IS 'Engine description';
COMMENT ON COLUMN SBI_ENGINES.MAIN_URL IS 'Engine URL (main location/invocation)';
COMMENT ON COLUMN SBI_ENGINES.SECN_URL IS 'Secondary engine URL (location/invocation)';
COMMENT ON COLUMN SBI_ENGINES.OBJ_UPL_DIR IS 'Path for documents upload (before approval iteration)';
COMMENT ON COLUMN SBI_ENGINES.OBJ_USE_DIR IS 'Path for usable documents';
COMMENT ON COLUMN SBI_ENGINES.DRIVER_NM IS 'Driver''s name for specific engine interface.';
COMMENT ON TABLE SBI_EXT_ROLES IS 'User roles. The role defines user visibility and interaction modality with parameters values.
The roles are imported from outside systems, which maintains their management responsibility.
The set of the user''s roles realized his profile.';
COMMENT ON COLUMN SBI_EXT_ROLES.EXT_ROLE_ID IS 'User role identifier';
COMMENT ON COLUMN SBI_EXT_ROLES.ROLE_TYPE_ID IS 'Role type identifier';
COMMENT ON COLUMN SBI_EXT_ROLES.ROLE_TYPE_CD IS 'Role type (administrative role, functional role), if role domains isn''t sufficiently detailed.';
COMMENT ON COLUMN SBI_EXT_ROLES.CODE IS 'Role code.';
COMMENT ON COLUMN SBI_EXT_ROLES.NAME IS 'Role name';
COMMENT ON COLUMN SBI_EXT_ROLES.DESCR IS 'Role description';
COMMENT ON TABLE SBI_FUNC_ROLE IS 'Permissions to the use of the functionalities on the user''s roles basis.';
COMMENT ON COLUMN SBI_FUNC_ROLE.FUNCT_ID IS 'Functionality identifier';
COMMENT ON COLUMN SBI_FUNC_ROLE.ROLE_ID IS 'User role identifier';
COMMENT ON COLUMN SBI_FUNC_ROLE.STATE_CD IS 'State code. Initially hard-coded valued, in the future, managed by a states workflow with historical storage.';
COMMENT ON TABLE SBI_FUNCTIONS IS 'Functions in the mining of Business Intelligence Documents thematic group.';
COMMENT ON COLUMN SBI_FUNCTIONS.FUNCT_ID IS 'Functionality identifier';
COMMENT ON COLUMN SBI_FUNCTIONS.PARENT_FUNCT_ID IS 'Functionality identifier';
COMMENT ON COLUMN SBI_FUNCTIONS.FUNCT_TYPE_ID IS 'Functionality type identifier';
COMMENT ON COLUMN SBI_FUNCTIONS.FUNCT_TYPE_CD IS 'Functionality type code';
COMMENT ON COLUMN SBI_FUNCTIONS.CODE IS 'Functionality code';
COMMENT ON COLUMN SBI_FUNCTIONS.NAME IS 'Functionality name';
COMMENT ON COLUMN SBI_FUNCTIONS.DESCR IS 'Functionality description';
COMMENT ON COLUMN SBI_FUNCTIONS.PATH IS 'Functionality location for category link.';
COMMENT ON TABLE SBI_LOV IS 'Lists of values and inputation mode for parameters presentation and values entry.';
COMMENT ON COLUMN SBI_LOV.INPUT_TYPE_ID IS 'Input type identifier.';
COMMENT ON COLUMN SBI_LOV.INPUT_TYPE_CD IS 'Input type (ex. manual input, profile, values list, query statement). Denormalizated attribute from SBI_DOMAINS.';
COMMENT ON COLUMN SBI_LOV.DEFAULT_VAL IS 'Default value for the parameter.';
COMMENT ON COLUMN SBI_LOV.LABEL IS 'Parameter label (short textual identifier).';
COMMENT ON COLUMN SBI_LOV.DESCR IS 'Parameter description';
COMMENT ON COLUMN SBI_LOV.PROFILE_ATTR IS 'Name of the user''s profile attribute to use for value setting.';
COMMENT ON COLUMN SBI_LOV.LOV_PROVIDER IS 'List of values provider. According to INPUT_TYPE_ID and INPUT_TYPE_ID, the column contains hard-coded values, query statement, function load name and so on.';
COMMENT ON TABLE SBI_OBJ_FUNC IS 'Relationship between SBIObjects and Functionalities.';
COMMENT ON COLUMN SBI_OBJ_FUNC.BIOBJ_ID IS 'Business Intelligence Object identifier';
COMMENT ON COLUMN SBI_OBJ_FUNC.FUNCT_ID IS 'Functionality identifier';
COMMENT ON TABLE SBI_OBJ_PAR IS 'Parameters used by BIObjects.';
COMMENT ON COLUMN SBI_OBJ_PAR.BIOBJ_ID IS 'Business Intelligence Object identifier';
COMMENT ON COLUMN SBI_OBJ_PAR.PAR_ID IS 'Parameter identifier';
COMMENT ON COLUMN SBI_OBJ_PAR.PROG IS 'Ordinal number of BIObj and Parameter association and input form.';
COMMENT ON COLUMN SBI_OBJ_PAR.REQ_FL IS 'Parameter required flag.';
COMMENT ON COLUMN SBI_OBJ_PAR.MOD_FL IS 'Parameter modifiable flag.';
COMMENT ON COLUMN SBI_OBJ_PAR.VIEW_FL IS 'Paramenter visibility flag. For hidden parameters.';
COMMENT ON COLUMN SBI_OBJ_PAR.MULT_FL IS 'Multivalue parameter.';
COMMENT ON COLUMN SBI_OBJ_PAR.LABEL IS 'Parameter label in BIObj use (short textual identifier)';
COMMENT ON COLUMN SBI_OBJ_PAR.PARURL_NM IS 'Parameter name in HTTP request.';
COMMENT ON TABLE SBI_OBJ_STATE IS 'Historical state''s changes of every BI Objects.';
COMMENT ON COLUMN SBI_OBJ_STATE.BIOBJ_ID IS 'Business Intelligence Object identifier';
COMMENT ON COLUMN SBI_OBJ_STATE.START_DT IS 'State assumption date';
COMMENT ON COLUMN SBI_OBJ_STATE.END_DT IS 'End date for object in state.';
COMMENT ON TABLE SBI_OBJECTS IS 'Business Intelligence Objects. The table contains all BI Objects, also of different family (report, OLAP, DM, Dash), with their essential attributes.
';
COMMENT ON COLUMN SBI_OBJECTS.BIOBJ_ID IS 'Business Intelligence Object identifier';
COMMENT ON COLUMN SBI_OBJECTS.BIOBJ_TYPE_ID IS 'Business Intelligence Object Type identifier.';
COMMENT ON COLUMN SBI_OBJECTS.BIOBJ_TYPE_CD IS 'Business Intelligence Object Type code (ex. report, OLAP, Data mining, Dashboard). Denormalizated attribute from SBI_DOMAINS.';
COMMENT ON COLUMN SBI_OBJECTS.ENGINE_ID IS 'Engine idenitifier (FK)';
COMMENT ON COLUMN SBI_OBJECTS.ENCRYPT IS 'Parameter encryption request.';
COMMENT ON COLUMN SBI_OBJECTS.STATE_ID IS 'State identifier (actually not used)';
COMMENT ON COLUMN SBI_OBJECTS.STATE_CD IS 'State code. Initially hard-coded valued, in the future, managed by a states workflow with historical storage.';
COMMENT ON COLUMN SBI_OBJECTS.SCHED_FL IS 'Schedulable document.';
COMMENT ON COLUMN SBI_OBJECTS.EXEC_MODE_ID IS 'Execution mode identifier.';
COMMENT ON COLUMN SBI_OBJECTS.EXEC_MODE_CD IS 'Business Intelligence Object execution mode (ex. delegated, supervised, proprietary). Denormalizated attribute from SBI_DOMAINS.';
COMMENT ON COLUMN SBI_OBJECTS.STATE_CONS_ID IS 'State consideration identifier.';
COMMENT ON COLUMN SBI_OBJECTS.STATE_CONS_CD IS 'State consideration code (statefull, stateless).Denormalizated attribute from SBI_DOMAINS. It can be also inherited form BIObjectType';
COMMENT ON COLUMN SBI_OBJECTS.LABEL IS 'Engine label (short textual identifier)';
COMMENT ON COLUMN SBI_OBJECTS.DESCR IS 'BI Object description';
COMMENT ON COLUMN SBI_OBJECTS.REL_NAME IS 'Relative path + file object name';
COMMENT ON TABLE SBI_PARAMETERS IS 'The table contains all parameters usable by all the BI objects. Every parameter can have several inputation and validation mode, depending of users roles.';
COMMENT ON COLUMN SBI_PARAMETERS.PAR_ID IS 'Parameter identifier';
COMMENT ON COLUMN SBI_PARAMETERS.LENGTH IS 'Parameter dimension.';
COMMENT ON COLUMN SBI_PARAMETERS.PAR_TYPE_ID IS 'Parameter type identifier.';
COMMENT ON COLUMN SBI_PARAMETERS.PAR_TYPE_CD IS 'Parameter type (ex. date, number, string). Denormalizated attribute from SBI_DOMAINS.';
COMMENT ON COLUMN SBI_PARAMETERS.LABEL IS 'Parameter label (short textual identifier).';
COMMENT ON COLUMN SBI_PARAMETERS.DESCR IS 'Parameter description';
COMMENT ON COLUMN SBI_PARAMETERS.MASK IS 'Parameter input format.';
COMMENT ON TABLE SBI_PARUSE IS 'Parameters use mode. According to user''s role, every parameter can be work in a different way. It present itself and validate the particular input value according to user''s role. There are three different table to do that:
1) SBI_PARUSE lets you to choose how the parameter can present itsefl (only one mode for user''s role, but many for the same parameter)
2) SBI_PARUSE_DET binds presentation mode with user''s roles
3) SBI_PARUSE_CK contains all input value''s validation rules (many checks for one input value)
With this structure, if you whant to bind checks with roles, you have to instantiate different SBI_PARUSE rows.
';
COMMENT ON COLUMN SBI_PARUSE.USE_ID IS 'Use mode identifier. It''s an auxiliary identifier used for a simpler handling of the relation between parameters, lovs, checks and roles.';
COMMENT ON COLUMN SBI_PARUSE.PAR_ID IS 'Parameter identifier';
COMMENT ON COLUMN SBI_PARUSE.LOV_ID IS 'Identifier for input mode and predefined list of values or reading criteria.';
COMMENT ON COLUMN SBI_PARUSE.LABEL IS 'Label to identify the parameter use mode.';
COMMENT ON COLUMN SBI_PARUSE.DESCR IS 'Description of the parameter use mode.';
COMMENT ON TABLE SBI_PARUSE_CK IS 'SBI_PARUSE_CK contains all input value''s validation rules (many checks for one input value)';
COMMENT ON COLUMN SBI_PARUSE_CK.USE_ID IS 'Use mode identifier.';
COMMENT ON COLUMN SBI_PARUSE_CK.CHECK_ID IS 'Check identifier. For every parameter-presentation mode (use_id) you can have many check to apply sequentially.';
COMMENT ON TABLE SBI_PARUSE_DET IS 'SBI_PARUSE_DET binds presentation mode with user''s roles';
COMMENT ON COLUMN SBI_PARUSE_DET.USE_ID IS 'Use mode identifier. It''s an auxiliary identifier used for a simpler handling of the relation between parameters, lovs, checks and roles.';
COMMENT ON COLUMN SBI_PARUSE_DET.EXT_ROLE_ID IS 'User role identifier';
COMMENT ON COLUMN SBI_PARUSE_DET.PROG IS 'Progressive.';

COMMENT ON TABLE SBI_GEO_MAPS IS 'The table contains all maps usable by GEO DWH';
COMMENT ON COLUMN SBI_GEO_MAPS.NAME IS 'Name of a map (logic key)';
COMMENT ON COLUMN SBI_GEO_MAPS.DESCR IS 'Description of a map ';
COMMENT ON COLUMN SBI_GEO_MAPS.URL IS  'Relative url of a map ';
COMMENT ON COLUMN SBI_GEO_MAPS.FORMAT IS  'Format of a map (ie. SVG)';
COMMENT ON TABLE SBI_GEO_FEATURES IS 'The table contains all features usable by GEO DWH';
COMMENT ON COLUMN SBI_GEO_FEATURES.NAME IS 'Name of a feature (logic key)';
COMMENT ON COLUMN SBI_GEO_FEATURES.DESCR IS 'Description of a feature ';
COMMENT ON COLUMN SBI_GEO_FEATURES.TYPE IS  'Relative url of a map ';
COMMENT ON TABLE SBI_GEO_MAP_FEATURES IS  'The table contains all relations between maps and features';
COMMENT ON COLUMN SBI_GEO_MAP_FEATURES.MAP_ID IS 'Map identifier';
COMMENT ON COLUMN SBI_GEO_MAP_FEATURES.FEATURE_ID IS 'Feature identifier';

COMMENT ON TABLE SBI_VIEWPOINTS IS 'The table contains all viewpoints';
COMMENT ON TABLE SBI_DATA_SOURCE IS 'The table contains all data sources';

COMMENT ON TABLE SBI_USER_FUNC IS 'The table contains all user functionalities';
COMMENT ON TABLE SBI_ROLE_TYPE_USER_FUNC IS 'The table contains all relations between user role and user functionalities';

COMMENT ON TABLE SBI_MENU IS 'The table contains all menu voices';
COMMENT ON TABLE SBI_MENU_ROLE IS 'The table contains all relations between menu and roles';

COMMENT ON  TABLE SBI_UDP IS 'The table contains all generic attributes definition';
COMMENT ON  TABLE SBI_UDP_VALUE IS 'The table contains all generci attributes values';