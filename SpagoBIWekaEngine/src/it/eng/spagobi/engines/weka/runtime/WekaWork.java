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

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Arrays;

import org.apache.log4j.Logger;

import commonj.work.Work;

import it.eng.spagobi.engines.weka.ParametersFiller;
import it.eng.spagobi.engines.weka.Utils;
import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.WekaEngineInstanceMonitor;
import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;
import it.eng.spagobi.engines.weka.WekaKnowledgeFlowRunner;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaWork implements Work {

	
	
	private static transient Logger logger = Logger.getLogger(WekaWork.class);
	
	File file;	
	WekaEngineInstance engineInstance;
	WekaEngineInstanceMonitor knowledgeFlowStartupMonitor; 
	
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
}
