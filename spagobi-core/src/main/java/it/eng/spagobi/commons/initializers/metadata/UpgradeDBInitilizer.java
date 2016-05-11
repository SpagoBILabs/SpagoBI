package it.eng.spagobi.commons.initializers.metadata;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.dao.DAOConfig;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.commons.upgrade.db.IUpgraderDB;
import it.eng.spagobi.commons.upgrade.db.UpgradeDBFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

public class UpgradeDBInitilizer implements InitializerIFace {

	private static Logger logger = Logger.getLogger(UpgradeDBInitilizer.class);

	@Override
	public void init(SourceBean config) {
		logger.debug("Init for Upgrade DB");

		String version = null;
		IConfigDAO configsDao = null;

		String hibernateFile = DAOConfig.getHibernateConfigurationFile();
		Configuration conf = new Configuration();
		conf = conf.configure(hibernateFile);
		Connection connection = getJNDIConnection(conf.getProperty("hibernate.connection.datasource"));
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT value_check from sbi_config where label='SPAGOBI.SPAGOBI_VERSION_NUMBER'");
			if (resultSet.first()){
				version = resultSet.getString("value_check");
			}
		} catch (Exception e) {
			logger.debug("Version is not present in sbi_config");
		}

//		String dialect = conf.getProperty("hibernate.dialect");

		IUpgraderDB upgraderDB = UpgradeDBFactory.getUpgraderDB();
		upgraderDB.upgrade(connection,version);

	}

	@Override
	public SourceBean getConfig() {
		return null;
	}

	private Connection getJNDIConnection(String jndiDatasource) {
			Connection result = null;
			try {
				Context initialContext = new InitialContext();
				DataSource datasource = (DataSource) initialContext
						.lookup(jndiDatasource);
				if (datasource != null) {
					result = datasource.getConnection();
				} else {
					logger.error("Failed to lookup datasource.");
				}
			} catch (NamingException ex) {
				logger.error("Cannot get connection: " + ex.getMessage());
			} catch (SQLException ex) {
				logger.error("Cannot get connection: " + ex.getMessage());
			}
			return result;
		}

}
