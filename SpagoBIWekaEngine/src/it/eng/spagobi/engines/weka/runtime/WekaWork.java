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

import weka.core.Drawable;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;
import weka.gui.beans.Associator;
import weka.gui.beans.BeanConnection;
import weka.gui.beans.BeanInstance;
import weka.gui.beans.Loader;
import weka.gui.beans.Saver;
import weka.gui.beans.TextEvent;
import weka.gui.beans.TextListener;
import weka.gui.beans.xml.XMLBeans;

import commonj.work.Work;

import it.eng.spagobi.engines.weka.ParametersFiller;
import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;
import it.eng.spagobi.engines.weka.configurators.WekaBeanConfiguratorFactory;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaWork implements Work {

	File file;	
	WekaEngineInstance engineInstance;
	
	private static transient Logger logger = Logger.getLogger(WekaWork.class);
	
	
	public WekaWork(WekaEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
		try {
			this.file = File.createTempFile("weka", null);
			ParametersFiller.fill(new StringReader(engineInstance.getTemplate()), new FileWriter(file), engineInstance.getEnv());
		} catch (Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to replace parameters in template", t);
		}		
	}
	
	public boolean isDaemon() {
		return false;
	}

	public void release() {
		logger.debug("IN");
		logger.debug("OUT");
	}

	
	public void run() {
		run(true, true);
	}
	
	public void run(boolean forceSetup, boolean forceBlocking) {
		logger.debug("IN");
		
		try {
			loadKnowledgeFlowTemplate();
			
			if(forceSetup) {
				logger.debug("Configuring loaders & savers ...");
				setupLoaders();
				setupSavers();
				setupAssociators();
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
			
			
		} catch (Throwable t) {
			throw new WekaEngineRuntimeException("An error occurred while running work", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	// ----------------------------------------------------
	// TODO move to a dedicated class - START
	// ----------------------------------------------------
	
	BeanContextSupport beanContextSupport;
	Vector beans;
	Vector connections;
	Vector loaders;
	Vector savers;
	Vector associators;
	
	public void loadKnowledgeFlowTemplate() {
		Vector v ;
		XMLBeans xml;
		
		logger.debug("IN");
		
		try {
			reset();
			
			xml = new XMLBeans(null, beanContextSupport); 
			v     = (Vector) xml.read(file);
			
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
				
				if (bean.getBean() instanceof Associator) {
					weka.associations.Associator associator;
					associator = ((Associator)bean.getBean()).getAssociator();
					logger.debug("    - Associator [" + associator.getClass() + "]");
					logger.debug("    - Associator is instance of drowable [" + (associator instanceof Drawable) + "]");
						
					associators.add(bean.getBean());
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
			throw new WekaEngineRuntimeException("Impossible to parse template from file [" + file.getName()+ "]", t);
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
		this.associators = new Vector();
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
					// memorizzati nel tempalte file quindi è necessario reinserirli a
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
	
	/**
	 *  Setup Associator filling missing parameter values	 *
	 */
	
	public void setupAssociators() {
		logger.debug("IN");
		for(int i = 0; i < associators.size(); i++) {
			Associator associator = (Associator)associators.get(i);
			TextListener listener = new TextListener() {
				public void acceptText(TextEvent e) {
					logger.debug(e.getText());
				}
			};
			associator.addTextListener(listener);
		}
		
		logger.debug("OUT");
	}
	
	

}
