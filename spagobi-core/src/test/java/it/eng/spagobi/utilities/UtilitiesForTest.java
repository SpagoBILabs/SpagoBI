package it.eng.spagobi.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.sql.DataSource;

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spagobi.commons.SimpleSingletonConfigCache;
import it.eng.spagobi.commons.SingletonConfig;

public class UtilitiesForTest {

	public static void setUpEmptyMasterConfiguration() {
		System.setProperty("AF_CONFIG_FILE", "resources-test/master.xml");
		ConfigSingleton.setConfigurationCreation(new FileCreatorConfiguration("./"));
	}

	public static void setUpMasterConfiguration() throws FileNotFoundException, IOException {
		ConfigSingleton.setConfigurationCreation(new FileCreatorConfiguration(getTestProperty("trunk.root") + "/SpagoBIProject/web-content"));
		ConfigSingleton.setConfigFileName("/WEB-INF/conf/master.xml");
	}

	private static String getTestProperty(String name) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(UtilitiesForTest.class.getClassLoader().getResourceAsStream("test.properties"));
		return props.getProperty(name);
	}

	public static void setUpTestJNDI() throws Exception {

		// Create initial context
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockFactory.class.getName());

		Context ic = new MockContext();
		MockFactory.context = ic;
		ic.bind("java:/comp/env/jdbc/spagobi", getSpagoBIDataSource());
		ic.bind("java:/comp/env/spagobi_service_url", "http://localhost:8080/spagobi");
		ic.bind("java:/comp/env/spagobi_host_url", "http://localhost:8080");
		ic.bind("java:/comp/env/spagobi_sso_class", "it.eng.spagobi.services.common.FakeSsoService");

		ic.bind("java://comp/env/spagobi_service_url", "http://localhost:8080/spagobi");
		ic.bind("java://comp/env/spagobi_host_url", "http://localhost:8080");
		ic.bind("java://comp/env/spagobi_sso_class", "it.eng.spagobi.services.common.FakeSsoService");

		SimpleSingletonConfigCache cache = new SimpleSingletonConfigCache();
		cache.setProperty("SPAGOBI_SSO.INTEGRATION_CLASS_JNDI", "java:/comp/env/spagobi_sso_class");
		SingletonConfig.getInstance().setCache(cache);
	}

	private static DataSource getSpagoBIDataSource() {
		return new DataSource() {

			@Override
			public <T> T unwrap(Class<T> iface) throws SQLException {

				return null;
			}

			@Override
			public boolean isWrapperFor(Class<?> iface) throws SQLException {

				return false;
			}

			@Override
			public void setLoginTimeout(int seconds) throws SQLException {

			}

			@Override
			public void setLogWriter(PrintWriter out) throws SQLException {

			}

			@Override
			public Logger getParentLogger() throws SQLFeatureNotSupportedException {

				return null;
			}

			@Override
			public int getLoginTimeout() throws SQLException {

				return 0;
			}

			@Override
			public PrintWriter getLogWriter() throws SQLException {

				return null;
			}

			@Override
			public Connection getConnection(String username, String password) throws SQLException {

				return null;
			}

			@Override
			public Connection getConnection() throws SQLException {
				try {
					return DriverManager.getConnection(getTestProperty("db.url"), getTestProperty("db.user"), getTestProperty("db.password"));
				} catch (IOException e) {
					throw new SQLException(e);
				}
			}
		};
	}

}
