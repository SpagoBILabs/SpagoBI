/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.drivers.worksheet;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.dao.IObjTemplateDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IBinContentDAO;
import it.eng.spagobi.commons.utilities.ParameterValuesEncoder;
import it.eng.spagobi.engines.drivers.AbstractDriver;
import it.eng.spagobi.engines.drivers.EngineURL;
import it.eng.spagobi.engines.drivers.IEngineDriver;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * Driver Implementation (IEngineDriver Interface) for Worksheet External Engine. 
 */
public class WorksheetDriver extends AbstractDriver implements IEngineDriver {
	
	static private Logger logger = Logger.getLogger(WorksheetDriver.class);
	
	public final static String PARAM_SERVICE_NAME = "ACTION_NAME";
    public final static String PARAM_NEW_SESSION = "NEW_SESSION";
    public final static String WORKSHEET_DEFINITION = "WORKSHEET_DEFINITION";
    public final static String QUERY = "QUERY";
    public final static String PARAM_ACTION_NAME = "WORKSHEET_ENGINE_START_ACTION";
    public final static String PARAM_MODALITY = "MODALITY";
    public final static String PARAM_MODALITY_VIEW = "VIEW";
    public final static String PARAM_MODALITY_EDIT = "EDIT";
    
    
		
	/**
	 * Returns a map of parameters which will be send in the request to the
	 * engine application.
	 * 
	 * @param profile Profile of the user
	 * @param roleName the name of the execution role
	 * @param analyticalDocument the biobject
	 * 
	 * @return Map The map of the execution call parameters
	 */
	public Map getParameterMap(Object analyticalDocument, IEngUserProfile profile, String roleName) {
		Map parameters;
		BIObject biObject;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(analyticalDocument, "Input parameter [analyticalDocument] cannot be null");
			Assert.assertTrue((analyticalDocument instanceof BIObject), "Input parameter [analyticalDocument] cannot be an instance of [" + analyticalDocument.getClass().getName()+ "]");
			
			biObject = (BIObject)analyticalDocument;
			
			parameters = new Hashtable();
			parameters = getRequestParameters(biObject);
			parameters = applySecurity(parameters, profile);
			//parameters = addDocumentParametersInfo(parameters, biObject);
			parameters = applyService(parameters, biObject);
		} finally {
			logger.debug("OUT");
		}
		
		return parameters;
	}
	
	/**
	 * Returns a map of parameters which will be send in the request to the
	 * engine application.
	 * 
	 * @param analyticalDocumentSubObject SubObject to execute
	 * @param profile Profile of the user
	 * @param roleName the name of the execution role
	 * @param analyticalDocument the object
	 * 
	 * @return Map The map of the execution call parameters
	 */
	public Map getParameterMap(Object analyticalDocument, Object analyticalDocumentSubObject, IEngUserProfile profile, String roleName) {
		
		Map parameters;
		BIObject biObject;
		SubObject subObject;
		
		logger.debug("IN");
		
		try{
			Assert.assertNotNull(analyticalDocument, "Input parameter [analyticalDocument] cannot be null");
			Assert.assertTrue((analyticalDocument instanceof BIObject), "Input parameter [analyticalDocument] cannot be an instance of [" + analyticalDocument.getClass().getName()+ "]");
			biObject = (BIObject)analyticalDocument;
			
			if(analyticalDocumentSubObject == null) {
				logger.warn("Input parameter [subObject] is null");
				return getParameterMap(analyticalDocument, profile, roleName);
			}				
			Assert.assertTrue((analyticalDocumentSubObject instanceof SubObject), "Input parameter [subObjectDetail] cannot be an instance of [" + analyticalDocumentSubObject.getClass().getName()+ "]");
			subObject = (SubObject) analyticalDocumentSubObject;
						
			parameters = getRequestParameters(biObject);
			
			parameters.put("nameSubObject",  subObject.getName() != null? subObject.getName(): "" );
			parameters.put("descriptionSubObject", subObject.getDescription() != null? subObject.getDescription(): "");
			parameters.put("visibilitySubObject", subObject.getIsPublic().booleanValue()?"Public":"Private" );
			parameters.put("subobjectId", subObject.getId());
			
			parameters = applySecurity(parameters, profile);
			//parameters = addDocumentParametersInfo(parameters, biObject);
			parameters = applyService(parameters, biObject);
			parameters.put("isFromCross", "false");
		
		} finally {
			logger.debug("OUT");
		}
		return parameters;
		
	}

	/**
     * Starting from a BIObject extracts from it the map of the paramaeters for the
     * execution call
     * @param biObject BIObject to execute
     * @return Map The map of the execution call parameters
     */    
	private Map getRequestParameters(BIObject biObject) {
		logger.debug("IN");
		
		Map parameters;
		ObjTemplate template;
		IBinContentDAO contentDAO;
		byte[] content;
		
		logger.debug("IN");
		
		parameters = null;
		
		try {		
			parameters = new Hashtable();
			template = this.getTemplate(biObject);
			
			try {
				contentDAO = DAOFactory.getBinContentDAO();
				Assert.assertNotNull(contentDAO, "Impossible to instantiate contentDAO");
				
				content = contentDAO.getBinContent(template.getBinId());		    
				Assert.assertNotNull(content, "Template content cannot be null");
			} catch (Throwable t){
				throw new RuntimeException("Impossible to load template content for document [" + biObject.getLabel()+ "]", t);
			}
					
			appendRequestParameter(parameters, "document", biObject.getId().toString());
			appendAnalyticalDriversToRequestParameters(biObject, parameters);
			addBIParameterDescriptions(biObject, parameters);
		} finally {
			logger.debug("OUT");
		}
		
		return parameters;
	} 
	
	
	
    /**
     * Add into the parameters map the BIObject's BIParameter names and values
     * @param biobj BIOBject to execute
     * @param pars Map of the parameters for the execution call  
     * @return Map The map of the execution call parameters
     */
	private Map appendAnalyticalDriversToRequestParameters(BIObject biobj, Map pars) {
		logger.debug("IN");
		
		if(biobj==null) {
			logger.warn("BIObject parameter null");	    
		    return pars;
		}
		
		ParameterValuesEncoder parValuesEncoder = new ParameterValuesEncoder();
		if(biobj.getBiObjectParameters() != null){
			BIObjectParameter biobjPar = null;
			for(Iterator it = biobj.getBiObjectParameters().iterator(); it.hasNext();){
				try {
					biobjPar = (BIObjectParameter)it.next();									
					String value = parValuesEncoder.encode(biobjPar);
					pars.put(biobjPar.getParameterUrlName(), value);
					logger.debug("Add parameter:"+biobjPar.getParameterUrlName()+"/"+value);
				} catch (Exception e) {
					logger.error("Error while processing a BIParameter",e);
				}
			}
		}
		
		logger.debug("OUT");
  		return pars;
	}
	
	 /**
 	 * Function not implemented. Thid method should not be called
 	 * 
 	 * @param biobject The BIOBject to edit
 	 * @param profile the profile
 	 * 
 	 * @return the edits the document template build url
 	 * 
 	 * @throws InvalidOperationRequest the invalid operation request
 	 */
    public EngineURL getEditDocumentTemplateBuildUrl(Object biobject, IEngUserProfile profile)
	throws InvalidOperationRequest {
    	logger.warn("Function not implemented");
    	throw new InvalidOperationRequest();
    }

    /**
     * Function not implemented. Thid method should not be called
     * 
     * @param biobject  The BIOBject to edit
     * @param profile the profile
     * 
     * @return the new document template build url
     * 
     * @throws InvalidOperationRequest the invalid operation request
     */
    public EngineURL getNewDocumentTemplateBuildUrl(Object biobject, IEngUserProfile profile)
	throws InvalidOperationRequest {
    	logger.warn("Function not implemented");
    	throw new InvalidOperationRequest();
    }

    
    

    public String composeWorksheetTemplate(String workSheetDef, String workSheetQuery, String originalQbeTempl) throws SourceBeanException{
    	SourceBean confSB = SourceBean.fromXMLString( originalQbeTempl );
		SourceBean wk_def_sb = new SourceBean(WORKSHEET_DEFINITION);
		wk_def_sb.setCharacters(workSheetDef);
		SourceBean query_sb = new SourceBean(QUERY);
		query_sb.setCharacters(workSheetQuery);
		confSB.setAttribute(wk_def_sb);
		confSB.setAttribute(query_sb);
		String template = confSB.toXML(false);	
		return template;
    }
    
	private Map applyService(Map parameters, BIObject biObject) {
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(parameters, "Input [parameters] cannot be null");
			
			parameters.put(PARAM_SERVICE_NAME, PARAM_ACTION_NAME);
			parameters.put(PARAM_MODALITY, PARAM_MODALITY_VIEW);
			parameters.put(PARAM_NEW_SESSION, "TRUE");
			
		} catch(Throwable t) {
			throw new RuntimeException("Impossible to guess from template extension the engine startup service to call");
		} finally {
			logger.debug("OUT");
		}
		
		return parameters;
	}
	
	private ObjTemplate getTemplate(BIObject biObject) {
		ObjTemplate template;
		IObjTemplateDAO templateDAO;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(biObject, "Input [biObject] cannot be null");
			
			templateDAO = DAOFactory.getObjTemplateDAO();
			Assert.assertNotNull(templateDAO, "Impossible to instantiate templateDAO");
		
			template = templateDAO.getBIObjectActiveTemplate( biObject.getId() );
			Assert.assertNotNull(template, "Loaded template cannot be null");	
			
			logger.debug("Active template [" + template.getName() + "] of document [" + biObject.getLabel() + "] loaded succesfully");
		} catch(Throwable t) {
			throw new RuntimeException("Impossible to load template for document [" + biObject.getLabel()+ "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return template;
	}
    
    private void appendRequestParameter(Map parameters, String pname, String pvalue) {
		parameters.put(pname, pvalue);
		logger.debug("Added parameter [" + pname + "] with value [" + pvalue + "] to request parameters list");
	}
}

