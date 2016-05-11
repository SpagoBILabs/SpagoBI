package it.eng.spagobi.commons.upgrade.db;

import it.eng.spagobi.commons.metadata.SbiCommonInfo;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.ValidationFailedException;
import liquibase.resource.FileSystemResourceAccessor;

import org.apache.log4j.Logger;

public class LiquibaseUpgradeDB implements IUpgraderDB{
	private static Logger logger = Logger.getLogger(LiquibaseUpgradeDB.class);
	String changelogFileName = null;

	public LiquibaseUpgradeDB(){
		changelogFileName = "it/eng/spagobi/commons/upgrade/db/db.changelog-master.xml";
	}

	@Override
	public void upgrade(Connection connection, String version) {
		logger.debug("Start Upgrade DB");

		Thread curThread = Thread.currentThread();
		ClassLoader classLoad = curThread.getContextClassLoader();
		String pathFileName = classLoad.getResource(changelogFileName).getFile();
		File fileChangelog = new File(pathFileName);
		Liquibase liquibase = null;
		try {
			String parentPath = fileChangelog.getParentFile().getAbsolutePath().replace("\\", "/").replace("%20", " ");
			FileSystemResourceAccessor fileSystemResourceAccessor = new FileSystemResourceAccessor(parentPath);
			JdbcConnection jdbcConnection= new JdbcConnection(connection);
			Database  db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
			liquibase = new Liquibase(fileChangelog.getName(), fileSystemResourceAccessor , db);
			if (version != null && version.equalsIgnoreCase(SbiCommonInfo.SBI_VERSION)){
				//upgrade tables only of the current version
				liquibase.update(new Contexts(), new LabelExpression("upgrade_" + SbiCommonInfo.SBI_VERSION));
			}else{
				DatabaseMetaData databaseMetaData = connection.getMetaData();
				ResultSet resultSetUpperCase = databaseMetaData.getTables(jdbcConnection.getCatalog(),null,"SBI_CONFIG",null);
				ResultSet resultSetLowerCase = databaseMetaData.getTables(jdbcConnection.getCatalog(),null,"sbi_config",null);
				//check if the table SBI_CONFIG (lower case o upper case) is present in db. If it is present upgrade the tables, else create them
				if ((resultSetUpperCase.next() && resultSetUpperCase.getRow() > 0) || (resultSetLowerCase.next() && resultSetLowerCase.getRow() > 0)){
					//upgrade tables, run all changeSets excluding the changeSets used to create DB the first time
					liquibase.update(new Contexts(), new LabelExpression("!create_tables"));
				}else{
					//create tables of the current version
					liquibase.update(new Contexts(), new LabelExpression("create_tables and create_" + SbiCommonInfo.SBI_VERSION));
				}
			}
		} catch (DatabaseException e1) {
			logger.error("Impossible to instance database for Liquibase " + e1);
			return;
		}catch (ValidationFailedException ve) {
			logger.error("Error during changelog(s) validation for liquibase" + ve);
		}catch(LiquibaseException  e){
			logger.error("Error during Liquibase updating " + e);
		} catch (SQLException e) {
			logger.error("Impossible to retrive DB metadata " + e);
		}

		logger.debug("End Upgrade DB");
	}

}
