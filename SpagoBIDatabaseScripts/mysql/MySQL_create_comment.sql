ALTER TABLE SBI_CHECKS COMMENT = 'Checks to validate paramenters data entry.';
ALTER TABLE SBI_DOMAINS COMMENT = 'Support domains for defined data values. The table contains the possible values of: 1) STATE; 2) BIOBJ Type; 3) PARAMETER Type; 4) INPUT type; 4) VALUE Type; 5) ROLE type; 6) Functionality type; 7) BIObject execution mode; 8) BIObject state handling;';
ALTER TABLE SBI_ENGINES COMMENT = 'Engine repository. The table contains all Engine information for its servlet invocation.';
ALTER TABLE SBI_EXT_ROLES COMMENT = 'User roles. The role defines user visibility and interaction modality with parameters values. The roles are imported from outside systems, which maintains their management responsibility. The set of the user''s roles realized his profile.';
ALTER TABLE SBI_FUNC_ROLE COMMENT = 'Permissions to the use of the functionalities on the user''s roles basis.';
ALTER TABLE SBI_FUNCTIONS COMMENT = 'Functions in the mining of Business Intelligence Documents thematic group.';
ALTER TABLE SBI_LOV COMMENT = 'Lists of values and inputation mode for parameters presentation and values entry.';
ALTER TABLE SBI_OBJ_FUNC COMMENT = 'Relationship between SBIObjects and Functionalities.';
ALTER TABLE SBI_OBJ_PAR COMMENT = 'Parameters used by BIObjects.';
ALTER TABLE SBI_OBJ_STATE COMMENT = 'Historical state''s changes of every BI Objects.';
ALTER TABLE SBI_OBJECTS COMMENT = 'Business Intelligence Objects. The table contains all BI Objects, also of different family (report, OLAP, DM, Dash), with their essential attributes.';
ALTER TABLE SBI_PARAMETERS COMMENT = 'The table contains all parameters usable by all the BI objects. Every parameter can have several inputation and validation mode, depending of users roles.';
ALTER TABLE SBI_PARUSE COMMENT = 'Parameters use mode. According to user''s role, every parameter can be work in a different way. It present itself and validate the particular input value according to user''s role. There are three different table to do that: 1) SBI_PARUSE lets you to choose how the parameter can present itsefl (only one mode for user''s role, but many for the same parameter) 2) SBI_PARUSE_DET binds presentation mode with user''s roles 3) SBI_PARUSE_CK contains all input value''s validation rules (many checks for one input value) With this structure, if you whant to bind checks with roles, you have to instantiate different SBI_PARUSE rows.';
ALTER TABLE SBI_PARUSE_CK COMMENT = 'SBI_PARUSE_CK contains all input value''s validation rules (many checks for one input value)';
ALTER TABLE SBI_PARUSE_DET COMMENT = 'SBI_PARUSE_DET binds presentation mode with user''s roles';

ALTER TABLE SBI_GEO_MAPS COMMENT = 'The table contains all maps usable by GEO DWH';
ALTER TABLE SBI_GEO_FEATURES COMMENT = 'The table contains all features usable by GEO DWH';
ALTER TABLE SBI_GEO_MAP_FEATURES COMMENT = 'The table contains all relations between maps and features';
ALTER TABLE SBI_VIEWPOINTS COMMENT = 'The table contains all viewpoints';
ALTER TABLE SBI_DATA_SOURCE COMMENT = 'The table contains all data sources';
ALTER TABLE SBI_MENU COMMENT = 'The table contains all menu voices';
ALTER TABLE SBI_MENU_ROLE COMMENT = 'The table contains all relations between menu and roles';

ALTER TABLE SBI_UDP = 'The table contains all generic attributes definition';
ALTER TABLE SBI_UDP_VALUE = 'The table contains all generci attributes values';