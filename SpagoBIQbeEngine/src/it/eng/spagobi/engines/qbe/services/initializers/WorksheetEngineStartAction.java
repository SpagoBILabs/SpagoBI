/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.services.initializers;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.QbeEngine;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.template.QbeTemplateParseException;
import it.eng.spagobi.engines.qbe.worksheet.Sheet;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.json.JSONObject;



/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class WorksheetEngineStartAction extends AbstractEngineStartAction {	
	
	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(WorksheetEngineStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIWorksheetEngine";
	
    public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
    	QbeEngineInstance qbeEngineInstance = null;
    	Locale locale;
    	
    	
    	logger.debug("IN");
       
    	try {
    		setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			logger.debug("Document Id: " + getDocumentId());
			logger.debug("Template: " + getTemplateAsSourceBean());
						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			try {
				qbeEngineInstance = QbeEngine.createInstance(getTemplateAsSourceBean(), getEnv() );
			} catch(Throwable t) {
				SpagoBIEngineStartupException serviceException;
				String msg = "Impossible to create engine instance for document [" + getDocumentId() + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
				
				if(rootException instanceof QbeTemplateParseException) {
					QbeTemplateParseException e = (QbeTemplateParseException)rootException;
					serviceException.setDescription( e.getDescription());
					serviceException.setHints( e.getHints() );
				} 
				
				throw serviceException;
			}
			logger.debug("Engine instance succesfully created");
			
			qbeEngineInstance.getEnv().put("TEMPLATE", getTemplateAsSourceBean());
			
			locale = (Locale)qbeEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			
			setAttributeInSession( ENGINE_INSTANCE, qbeEngineInstance);		
			setAttribute(ENGINE_INSTANCE, qbeEngineInstance);
			
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());
			
			if(qbeEngineInstance!= null && qbeEngineInstance.getWorkSheetDefinition()!=null && qbeEngineInstance.getWorkSheetDefinition().getWorkSheet()!=null){
				List<Sheet> ws = qbeEngineInstance.getWorkSheetDefinition().getWorkSheet();
				for(int i=0; i<ws.size();i++){
					setImageWidth((ws.get(i)).getHeader());
					setImageWidth((ws.get(i)).getFooter());
				}		
			}

			
			
		} catch (Throwable e) {
			SpagoBIEngineStartupException serviceException = null;
			
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			logger.debug("OUT");
		}		
		
	}
    
    /**
     * Set the with of the image in the template
     * @param title The JSONObject rapresentation of the header/footer
     * @throws Exception
     */
	public static void setImageWidth(JSONObject title) throws Exception {
		logger.debug("IN");
		
		if(title!=null){
			String s = title.optString("img");
			if(s!=null && !s.equals("") && !s.equals("null")){
				try {
					logger.debug("Image file = "+s);
					File toReturn = null;
					File imagesDir = QbeEngineConfig.getInstance().getWorksheetImagesDir();
					toReturn = new File(imagesDir, s);

					BufferedImage img = ImageIO.read(toReturn);
				    int width= img.getWidth();
					
					title.put("width", width);	
				} catch (Exception e) {
					logger.error("Error loading the image "+s+":  "+e);
				}

			}
		}
		logger.debug("OUT");
	}
    
}
