/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.qbe.services.formbuilder;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SaveFormAction extends AbstractQbeEngineAction {	

	// INPUT PARAMETERS
	public static final String FORM_STATE = "FORM_STATE";
	public static final String TEMPLATE_NAME = "TEMPLATE_NAME";
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(SaveFormAction.class);
   
	public void service(SourceBean request, SourceBean response)  {
		
		JSONObject formState;
		String templateName;
		SourceBean template;
		SourceBean formBlock;
		SourceBean queryBlock;
		
		QbeEngineInstance qbeEngineInstance;
		ContentServiceProxy contentServiceProxy;
		
		logger.debug("IN");
		
		try {		
			super.service(request, response);
			
			qbeEngineInstance = getEngineInstance();
			Assert.assertNotNull(qbeEngineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			
			formState = this.getAttributeAsJSONObject(FORM_STATE);
			logger.debug("Parameter [" + FORM_STATE + "] is equals to [" + formState + "]");
			Assert.assertNotNull(formState, "Parameter [" + FORM_STATE + "] cannot be null");
			
			qbeEngineInstance.getFormState().setConf(formState);
			
			templateName = this.getAttributeAsString(TEMPLATE_NAME);
			logger.debug("Parameter [" + TEMPLATE_NAME + "] is equals to [" + templateName + "]");
			if(StringUtilities.isEmpty(templateName)) {
				templateName = "template.sbiform";
				logger.debug("The default template name [" + templateName + "] will be used");
			}
			
			template = (SourceBean)qbeEngineInstance.getEnv().get("TEMPLATE");
			
			StringBuffer dataDefinition = new StringBuffer();
			String formStateString = new String(getEngineInstance().getFormState().store());
			JSONObject formStateJSON = new JSONObject(formStateString);
			dataDefinition.append("<FORM>");
			dataDefinition.append("<![CDATA[\n" + formStateJSON.toString(3) + "\n]]>");
			dataDefinition.append("</FORM>");
			formBlock = SourceBean.fromXMLString(dataDefinition.toString());
			template.updAttribute(formBlock);
			
			dataDefinition = new StringBuffer();
			String analysisState = new String(getEngineInstance().getAnalysisState().store());
			JSONObject queryJSON = new JSONObject(analysisState);
			dataDefinition.append("<QUERY>");
			dataDefinition.append("<![CDATA[\n" + queryJSON.toString(3) + "\n]]>");
			dataDefinition.append("</QUERY>");
			queryBlock = SourceBean.fromXMLString(dataDefinition.toString());
			template.updAttribute(queryBlock);
			
			logger.debug(template.toString());
			
			contentServiceProxy = (ContentServiceProxy)qbeEngineInstance.getEnv().get(EngineConstants.ENV_CONTENT_SERVICE_PROXY);
			Assert.assertNotNull(formState, "Parameter [" + FORM_STATE + "] cannot be null");
			
			String docId = (String)qbeEngineInstance.getEnv().get("DOCUMENT");
			String result = contentServiceProxy.saveObjectTemplate(docId, templateName, template.toString());
			
			if (result == null || !result.trim().equals("OK")) {
				throw new Exception("Error while saving document's template");
			}
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				throw new SpagoBIEngineServiceException(getActionName(), "Impossible to write back the responce to the client", e);
			}
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {			
			logger.debug("OUT");
		}	
	}
}
