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
package it.eng.spagobi.engines.weka.knowledgeflow;

import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextSupport;
import java.io.FileWriter;
import java.io.Writer;
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

import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaKnowledgeFlow {
	BeanContextSupport beanContextSupport;
	Vector beans;
	Vector connections;
	Vector loaders;
	Vector savers;
	Vector associators;
	
	private static transient Logger logger = Logger.getLogger(WekaKnowledgeFlow.class);
	
	WekaKnowledgeFlowEnv env;
	
	public WekaKnowledgeFlow(String template, WekaKnowledgeFlowEnv env) {
		this.env = env;
		template = template.replaceAll("<object class=\"weka.core.SelectedTag\" name=\"metricType\">0</object>", "");
		template = template.replaceAll("<object class=\"weka.core.SelectedTag\" name=\"missingValues\">0</object>", "");
		template = template.replaceAll("<object class=\"weka.core.SelectedTag\" name=\"negation\">0</object>", "");
		template = template.replaceAll("<object class=\"weka.core.SelectedTag\" name=\"valuesOutput\">0</object>", "");

		this.load(template);
	}
	
	public void run(boolean forceSetup, boolean forceBlocking) {
		logger.debug("IN");
		
		try {
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
				
				if(savers.size() == 0) {
					for(int i = 0; i < associators.size(); i++) {
						Associator associator = (Associator)associators.get(i);
						logger.debug("Start blocking on: " + associator );			
						associator.waitUntilFinish();			
					}	
				}
			}
			
			
		} catch (Throwable t) {
			throw new WekaEngineRuntimeException("An error occurred while running work", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	// ===================================
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
				if(env.getOutConnection() != null) {
					databaseLoader.setSource(env.getInConnection());
				}
			} else if(className.equalsIgnoreCase(ArffLoader.class.getName())) {
				ArffLoader arffLoader = (ArffLoader)loader.getLoader();
				// setup operation goes here
			} else if(className.equalsIgnoreCase(CSVLoader.class.getName())) {
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
				
				databaseSaver.setDbWriteMode(env.getWriteMode());
				databaseSaver.setKeyColumnNames(env.getKeyColumnNames());
				
				if( env.isVersioningEnabled() ) {
					databaseSaver.setVersioning(true);
					databaseSaver.setVersionColumnName( env.getVersionColumnName());
					databaseSaver.setVersion( env.getVersion());
				}
				
				if( env.getOutConnection() != null ) {
					databaseSaver.setDestination(env.getOutConnection());
				}
			} else if(className.equalsIgnoreCase(ArffSaver.class.getName())) {
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
		env.setParameter("operation-output", env.getOutputFile());
		for(int i = 0; i < associators.size(); i++) {
			Associator associator = (Associator)associators.get(i);
			TextListener listener = new TextListener() {
				public void acceptText(TextEvent e) {
					try {
						synchronized(this) {
							
							Writer writer = new FileWriter(env.getOutputFile(), true);
							writer.write( e.getText().replaceAll("\n", "\r\n"));							
							writer.flush();
							writer.close();
							/*
							FileOutputStream outputStream = new FileOutputStream(env.getOutputFile(), true);
							//outputStream.write(e.getText().getBytes("US-ASCII"));
							outputStream.write(e.getText().getBytes("UTF-8"));
							outputStream.flush();
							outputStream.close();
							*/
						}
					} catch (Throwable t) {
						logger.error(t);
						throw new RuntimeException("Impossible to save output text on file [" + env.getOutputFile() + "]", t);
					}
				}
			};
			associator.addTextListener(listener);
		}
		
		logger.debug("OUT");
	}
	// ===================================
	
	private void reset() {
		this.beanContextSupport = new BeanContextSupport();
		this.beans = new Vector();
		this.connections = new Vector();
		this.loaders = new Vector();
		this.savers = new Vector();
		this.associators = new Vector();
	}
	
	private void load(String template) {
		Vector v ;
		XMLBeans xml;
		
		logger.debug("IN");
		
		try {
			reset();
			
			xml = new XMLBeans(null, beanContextSupport); 
			v     = (Vector) xml.read(template);
			
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
				
				
				
				//WekaBeanConfiguratorFactory.setup(bean.getBean());
				
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
			throw new WekaEngineRuntimeException("Impossible to parse template [" + template + "]", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	public static WekaKnowledgeFlow load(String template, WekaKnowledgeFlowEnv env) {
		return new WekaKnowledgeFlow(template, env);
	}
}
