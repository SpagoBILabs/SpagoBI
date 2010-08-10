
            ===========================================================
                  Migration Job from SpagoBI 1.9.4 to SpagoBI 2.1.0
            ===========================================================

The aim of this package is to provide a tool in order to migrate data from a SpagoBI 1.9.4 metadata database to a SpagoBI 2.1.0 metadata database.

workspace.zip file contains the workspace for talend Open Studio 2.4, it contains a Job named MIGRATION_JOB, and some sub-jobs.

The target database must be empty, with the exception of the table SBI_DOMAINS that must already have been filled.
In order to achieve this result, you have to start SpagoBI server, it will automatically populate some tables; then stop the server and delete content of tables:
SBI_ENGINES
SBI_CHECKS
SBI_LOV
SBI_EXT_ROLES
SBI_FUNCTIONS

Metadata that are migrated are about engines, lovs, analytical drivers, checks, functionalities and roles, documents, audit, viewpoints and subreports.
Pay attention that documents' templates, customized views (subobjects) and scheduled execution results (snapshots) are not migrated; for what concern templates, user will likely have to modify them according
to new platform needs, exspecially for OLAP and QBE documents.


======== How to execute Migration_Job: =====================

Unzip the "workspace.zip" file and run talend by selecting it as workspace.

open the job named MIGRATION_JOB

click on the first two components of migration_job, named "driver" and "driver2" and choose the jar files
containing the jdbc drivers for your source and target database.

When starting the job you'll have to fill the connection parameters for both your source and target database; such as
url, username, password, driver, driver jar file (put driver file name between quotes, for example if you are using Oracle database: "ojdbc5.jar").
Parameters with "Exp" suffix are referring to source database, parameters with "Imp" suffix are referring to target database.

In the fileLog parameter you can place the path of a file where informations about migration will be placed.
 
