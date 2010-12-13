/**
 *
 *	LICENSE: see COPYING file
 *
**/
package it.eng.spagobi.engines.weka;

import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Logger;

import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;
import weka.gui.beans.BeanConnection;
import weka.gui.beans.BeanInstance;
import weka.gui.beans.Loader;
import weka.gui.beans.Saver;
import weka.gui.beans.xml.XMLBeans;

import it.eng.spagobi.engines.weka.configurators.WekaBeanConfiguratorFactory;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class WekaKnowledgeFlowRunner {
	
	protected BeanContextSupport beanContextSupport;
	protected Vector beans;
	protected Vector connections;
	protected Vector loaders;
	protected Vector savers;
	
	// Write modality options
	protected String writeMode; 
	protected String[] keyColumnNames = null;
	protected boolean versioning;
	protected String versionColumnName;
	protected String version;
	
	// Connections to db for Loaders and Savers
	protected Connection inConnection;
	protected Connection outConnection;
		
	//	Connection parameters for db Loaders and Savers
	protected String dbUrl = DEFAULT_DB_URL;
	protected String dbUser = DEFAULT_DB_USER;
	protected String dbPassword = DEFAULT_DB_PASSWORD;
	
	
	private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost/foodmart";
	private static final String DEFAULT_DB_USER = "root";
	private static final String DEFAULT_DB_PASSWORD = "xxx";
	/*
	private static final String DEFAULT_DB_URL = "jdbc:dbname://localhost/foodmart";
	private static final String DEFAULT_DB_USER = "root";
	private static final String DEFAULT_DB_PASSWORD = "admin";
	*/
	private static transient Logger logger = Logger.getLogger(WekaKnowledgeFlowRunner.class);
	
	static private void log(String msg) {
		logger.debug("WekaKFRunner:" + msg);
	}
	
	
	public WekaKnowledgeFlowRunner() { 
		this(null, null);
	}
	
	public WekaKnowledgeFlowRunner(Connection inConnection, Connection outConnection) { 
		versioning = false;
		versionColumnName = "version";
		version = "base-version";
		this.inConnection = inConnection;
		this.outConnection = outConnection;		
	}
	
	public Vector getLoaders() {
		return loaders;
	}

	public Vector getSavers() {
		return savers;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	public void reset() {
		this.beanContextSupport = new BeanContextSupport();
		this.beans = new Vector();
		this.connections = new Vector();
		this.loaders = new Vector();
		this.savers = new Vector();
	}
	
	public Connection getInConnection() {
		return inConnection;
	}


	public void setInConnection(Connection inConnection) {
		this.inConnection = inConnection;
	}


	public Connection getOutConnection() {
		return outConnection;
	}


	public void setOutConnection(Connection outConnection) {
		this.outConnection = outConnection;
	}


	public String getWriteMode() {
		return writeMode;
	}


	public void setWriteMode(String writeMode) {
		this.writeMode = writeMode;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getVersionColumnName() {
		return versionColumnName;
	}


	public void setVersionColumnName(String versionColumnName) {
		this.versionColumnName = versionColumnName;
	}


	public boolean isVersioning() {
		return versioning;
	}


	public void setVersioning(boolean versioning) {
		this.versioning = versioning;
	}	
	
	public void loadKnowledgeFlowTemplate(File template) throws Exception {
		logger.debug("IN");
		reset();
		XMLBeans xml = new XMLBeans(null, beanContextSupport); 
		Vector v     = (Vector) xml.read(template);
		beans        = (Vector) v.get(XMLBeans.INDEX_BEANINSTANCES);
		connections  = (Vector) v.get(XMLBeans.INDEX_BEANCONNECTIONS);
		
		beanContextSupport = new BeanContextSupport(); // why?
		beanContextSupport.setDesignTime(true);
				
		for(int i = 0; i < beans.size(); i++) {
			BeanInstance bean = (BeanInstance)beans.get(i);
			log("   " + (i+1) + ". " + bean.getBean().getClass().getName());
		
			// register loaders
			if (bean.getBean() instanceof Loader) {
				log("    - Loader [" + 
					((Loader)bean.getBean()).getLoader().getClass() + "]");
								
				loaders.add(bean.getBean());
			}	
			
			// register savers
			if (bean.getBean() instanceof Saver) {
				log("    - Saver [" + 
					((Saver)bean.getBean()).getSaver().getClass() + "]");
								
				savers.add(bean.getBean());
			}	
			
			WekaBeanConfiguratorFactory.setup(bean.getBean());
			
			if (bean.getBean() instanceof BeanContextChild) {
				log("    - BeanContextChild" );
				beanContextSupport.add(bean.getBean());
			}			
		}
		
		for(int i = 0; i < connections.size(); i++) {
			BeanConnection connection = (BeanConnection)connections.get(i);
			log("   " + (i+1) + ". " + connection.getClass().getName());
		}	
		
		BeanInstance.setBeanInstances(beans, null);
		BeanConnection.setConnections(connections);
		logger.debug("OUT");
	}
	
	/**
	 *  Setup Loader filling missing parameter values	 *
	 */
	public void setupLoaders() {	
		logger.debug("IN");
		for(int i = 0; i < loaders.size(); i++) {
			Loader loader = (Loader)loaders.get(i);						
			String className = loader.getLoader().getClass().getName();
			
			if(className.equalsIgnoreCase(DatabaseLoader.class.getName())) {
				DatabaseLoader databaseLoader = (DatabaseLoader)loader.getLoader();
				
				if(inConnection != null) {
					databaseLoader.setSource(inConnection);
				}
				else {
					// l'url del db, il nome utente e la password non sono 
					// memorizzati nel tempalte file quindi è necessario riinserirli a
					// mano al termine del processo di parsing
					databaseLoader.setUrl(dbUrl);
					databaseLoader.setUser(dbUser);
					databaseLoader.setPassword(dbPassword);	
				}
			}
			else if(className.equalsIgnoreCase(ArffLoader.class.getName())) {
				ArffLoader arffLoader = (ArffLoader)loader.getLoader();
				// setup operation goes here
			}
			else if(className.equalsIgnoreCase(CSVLoader.class.getName())) {
				CSVLoader csvLoader = (CSVLoader)loader.getLoader();
				// setup operation goes here				
			}		
		}
		logger.debug("OUT");
	}
	
	/**
	 *  Setup Saver filling missing parameter values	 *
	 */
	public void setupSavers() {
		logger.debug("IN");
		for(int i = 0; i < savers.size(); i++) {
			Saver saver = (Saver)savers.get(i);
			
			
			
			String className = saver.getSaver().getClass().getName();
			if(className.equalsIgnoreCase(DatabaseSaver.class.getName())) {
				DatabaseSaver databaseSaver = (DatabaseSaver)saver.getSaver();
				
				databaseSaver.setDbWriteMode(writeMode);
				databaseSaver.setKeyColumnNames(this.keyColumnNames);
				
				if(versioning) {
					databaseSaver.setVersioning(true);
					databaseSaver.setVersionColumnName(versionColumnName);
					databaseSaver.setVersion(version);
				}
				
				if(outConnection != null) {
					databaseSaver.setDestination(outConnection);
				}
				else {				
					// l'url del db, il nome utente e la password non sono 
					// memorizzati nel tempalte file quindi è necessario riinserirli a
					// mano al termine del processo di parsing
					databaseSaver.setUrl(dbUrl);
					databaseSaver.setUser(dbUser);
					databaseSaver.setPassword(dbPassword);		
				}
			}			
			else if(className.equalsIgnoreCase(ArffSaver.class.getName())) {
				ArffSaver arffSaver = (ArffSaver)saver.getSaver();				
				// setup operation goes here			
			}
		}
		logger.debug("OUT");
	}
		
	public void run() {
		run(true, true);
	}
	
	public void run(boolean forceSetup, boolean forceBlocking) {
		logger.debug("IN");
		if(forceSetup) {
			log("Configuring loaders & savers ...");
			setupLoaders();
			setupSavers();
		}		
				
		for(int i = 0; i < loaders.size(); i++) {
			Loader loader = (Loader)loaders.get(i);
			log("Start loading: " + loader );			
			loader.startLoading();			
		}		
		
		if(forceBlocking) {
			for(int i = 0; i < savers.size(); i++) {
				Saver saver = (Saver)savers.get(i);
				log("Start blocking on: " + savers );			
				saver.waitUntilFinish();			
			}	
		}
		logger.debug("OUT");
	}


	public String[] getKeyColumnNames() {
		return keyColumnNames;
	}


	public void setKeyColumnNames(String[] keyColumnNames) {
		this.keyColumnNames = keyColumnNames;
	}	
}
