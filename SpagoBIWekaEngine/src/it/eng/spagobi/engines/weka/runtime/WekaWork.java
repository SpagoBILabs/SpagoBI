/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.weka.runtime;

import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
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

import commonj.work.Work;

import it.eng.spagobi.engines.weka.ParametersFiller;
import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.WekaEngineInstanceMonitor;
import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;
import it.eng.spagobi.engines.weka.configurators.WekaBeanConfiguratorFactory;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaWork implements Work {

	File file;	
	WekaEngineInstance engineInstance;
	WekaEngineInstanceMonitor knowledgeFlowStartupMonitor; 
	
	
	private static transient Logger logger = Logger.getLogger(WekaWork.class);
	
	
	public WekaWork(WekaEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
		this.knowledgeFlowStartupMonitor = new WekaEngineInstanceMonitor(engineInstance.getEnv());
		try {
			this.file = File.createTempFile("weka", null);
			ParametersFiller.fill(new StringReader(engineInstance.getTemplate()), new FileWriter(file), engineInstance.getEnv());
		} catch (Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to replace parameters in template", t);
		}		
	}
	
	public boolean isDaemon() {
		// TODO Auto-generated method stub
		return false;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

	
	public void run() {
		run(true, true);
	}
	
	public void run(boolean forceSetup, boolean forceBlocking) {
		logger.debug("IN");
		
		loadKnowledgeFlowTemplate();
		
		if(forceSetup) {
			logger.debug("Configuring loaders & savers ...");
			setupLoaders();
			setupSavers();
		}		
				
		for(int i = 0; i < loaders.size(); i++) {
			Loader loader = (Loader)loaders.get(i);
			logger.debug("Start loading: " + loader );			
			loader.startLoading();			
		}		
		
		if(forceBlocking) {
			for(int i = 0; i < savers.size(); i++) {
				Saver saver = (Saver)savers.get(i);
				logger.debug("Start blocking on: " + savers );			
				saver.waitUntilFinish();			
			}	
		}
		logger.debug("OUT");
	}
	
	// ----------------------------------------------------
	// TODO move to a dedicated class - START
	// ----------------------------------------------------
	
	BeanContextSupport beanContextSupport;
	Vector beans;
	Vector connections;
	Vector loaders;
	Vector savers;
	
	public void loadKnowledgeFlowTemplate() {
		logger.debug("IN");
		
		try {
			reset();
			XMLBeans xml = new XMLBeans(null, beanContextSupport); 
			Vector v     = (Vector) xml.read(file);
			beans        = (Vector) v.get(XMLBeans.INDEX_BEANINSTANCES);
			connections  = (Vector) v.get(XMLBeans.INDEX_BEANCONNECTIONS);
			
			beanContextSupport = new BeanContextSupport(); // why?
			beanContextSupport.setDesignTime(true);
					
			for(int i = 0; i < beans.size(); i++) {
				BeanInstance bean = (BeanInstance)beans.get(i);
				logger.debug("   " + (i+1) + ". " + bean.getBean().getClass().getName());
			
				// register loaders
				if (bean.getBean() instanceof Loader) {
					logger.debug("    - Loader [" + 
						((Loader)bean.getBean()).getLoader().getClass() + "]");
									
					loaders.add(bean.getBean());
				}	
				
				// register savers
				if (bean.getBean() instanceof Saver) {
					logger.debug("    - Saver [" + 
						((Saver)bean.getBean()).getSaver().getClass() + "]");
									
					savers.add(bean.getBean());
				}	
				
				WekaBeanConfiguratorFactory.setup(bean.getBean());
				
				if (bean.getBean() instanceof BeanContextChild) {
					logger.debug("    - BeanContextChild" );
					beanContextSupport.add(bean.getBean());
				}		
				
			}
		
		
			for(int i = 0; i < connections.size(); i++) {
				BeanConnection connection = (BeanConnection)connections.get(i);
				logger.debug("   " + (i+1) + ". " + connection.getClass().getName());
			}	
			
			BeanInstance.setBeanInstances(beans, null);
			BeanConnection.setConnections(connections);
			
		
		} catch(Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to parse template", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	public void reset() {
		this.beanContextSupport = new BeanContextSupport();
		this.beans = new Vector();
		this.connections = new Vector();
		this.loaders = new Vector();
		this.savers = new Vector();
	}
	
	// ----------------------------------------------------
	// TODO move to a dedicated class - END
	// ----------------------------------------------------
	
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
				
				if(engineInstance.getOutConnection() != null) {
					databaseLoader.setSource(engineInstance.getInConnection());
				}
				else {
					// l'url del db, il nome utente e la password non sono 
					// memorizzati nel tempalte file quindi è necessario riinserirli a
					// mano al termine del processo di parsing
					/*
					databaseLoader.setUrl(dbUrl);
					databaseLoader.setUser(dbUser);
					databaseLoader.setPassword(dbPassword);	
					*/
					// NOTE if connection is not defined -> Fail Fast!
					// TODO throw an exception here
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
				
				databaseSaver.setDbWriteMode(engineInstance.getWriteMode());
				databaseSaver.setKeyColumnNames(engineInstance.getKeyColumnNames());
				
				if( engineInstance.isVersioningEnabled() ) {
					databaseSaver.setVersioning(true);
					databaseSaver.setVersionColumnName( engineInstance.getVersionColumnName());
					databaseSaver.setVersion( engineInstance.getVersion());
				}
				
				if( engineInstance.getOutConnection() != null ) {
					databaseSaver.setDestination(engineInstance.getOutConnection());
				}
				else {				
					// l'url del db, il nome utente e la password non sono 
					// memorizzati nel tempalte file quindi è necessario riinserirli a
					// mano al termine del processo di parsing
					/*
					databaseSaver.setUrl(dbUrl);
					databaseSaver.setUser(dbUser);
					databaseSaver.setPassword(dbPassword);	
					*/	
					// NOTE if connection is not defined -> Fail Fast!
					// TODO throw an exception here
				}
			}			
			else if(className.equalsIgnoreCase(ArffSaver.class.getName())) {
				ArffSaver arffSaver = (ArffSaver)saver.getSaver();				
				// setup operation goes here			
			}
		}
		logger.debug("OUT");
	}
	
	
	
	/*
	public void run() {
		WekaKnowledgeFlowRunner knowledgeFlowRunner;
		
		logger.debug("IN");
				
		try {
			knowledgeFlowStartupMonitor.start();
			
			
			knowledgeFlowRunner = buildKnowledgeFlowRunner();
			
			
			try {
				knowledgeFlowRunner.run(false, true);
			} catch (Throwable t2) {
				throw new WekaEngineRuntimeException("Impossible to run the Knowledge-Flow", t2);
			}
			
		} catch(Throwable error) {
			knowledgeFlowStartupMonitor.setError(error);
		} finally {
			file.delete();
			knowledgeFlowStartupMonitor.stop();
			logger.debug("OUT");
		}
	}
	*/
	
	
	/*
	private WekaKnowledgeFlowRunner buildKnowledgeFlowRunner() {
		Connection conn;
		WekaKnowledgeFlowRunner knowledgeFlowRunner;
		
		logger.debug("IN");
		
		knowledgeFlowRunner = null;
		
		try {
			conn = engineInstance.getConnection();
			knowledgeFlowRunner = new WekaKnowledgeFlowRunner(conn, conn);
			logger.debug("Knowledge-Flow Runner successfully created");
			
			knowledgeFlowRunner.loadKnowledgeFlowTemplate(file);
			knowledgeFlowRunner.setWriteMode( engineInstance.getWriteMode() );
			logger.debug("Write mode is equal to [" + knowledgeFlowRunner.getWriteMode() + "]");
			
			knowledgeFlowRunner.setKeyColumnNames( engineInstance.getKeys() );
			logger.debug("Key column names are " + Arrays.toString( knowledgeFlowRunner.getKeyColumnNames() ) + "");
			
			if( engineInstance.isVerioningEnabled() ){
				knowledgeFlowRunner.setVersioning(true);
				logger.debug("Versioning is enabled");
				
				if( engineInstance.getVerionColumnName() != null) {
					knowledgeFlowRunner.setVersionColumnName( engineInstance.getVerionColumnName() );
					logger.debug("Version column name is equal to [" + knowledgeFlowRunner.getVersionColumnName() + "]");
				}
				
				if( engineInstance.getVerion() != null) {
					knowledgeFlowRunner.setVersion(engineInstance.getVerion());
					logger.debug("Version is equal to [" + knowledgeFlowRunner.getVersion() + "]");
				}
			} else {
				logger.debug("Versioning is not enabled");
			}
			
			knowledgeFlowRunner.setupSavers();
			logger.debug("Savers successfully initializated");
			knowledgeFlowRunner.setupLoaders();
			logger.debug("Loaders successfully initializated");
			
			logger.debug( "\n" + Utils.getLoderDesc(knowledgeFlowRunner.getLoaders()) );
			logger.debug( "\n" + Utils.getSaverDesc(knowledgeFlowRunner.getSavers()) );
			
		} catch(Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to instatiate the Knowledge-Flow Runner]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return knowledgeFlowRunner;	
	}
	*/
}
