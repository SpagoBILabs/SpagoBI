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
    public final static String QUERY = "QUERY";
    public final static String PARAM_ACTION_NAME = "WORKSHEET_ENGINE_START_ACTION";
    public final static String EXPORT_ACTION_NAME = "MASSIVE_EXPORT_WORKSHEET_ENGINE_START_ACTION";
    
    public final static String FORM_VALUES = "FORM_VALUES";
    
	public final static String CURRENT_VERSION = "1";
	public final static String ATTRIBUTE_VERSION = "version";
	public final static String TAG_WORKSHEET_DEFINITION = "WORKSHEET_DEFINITION";
	public final static String TAG_WORKSHEET = "WORKSHEET";
	public final static String TAG_QBE = "QBE";
		
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

    
    
    public String updateWorksheetTemplate(String workSheetDef, String workSheetQuery, String smartFilterValues, String originalWorksheetTempl) throws SourceBeanException{
    	SourceBean templateSB = new SourceBean(TAG_WORKSHEET);
    	templateSB.setAttribute(ATTRIBUTE_VERSION, CURRENT_VERSION);
    	SourceBean previous = SourceBean.fromXMLString( originalWorksheetTempl );
    	
    	// from version 0 to version 1 worksheet change compensation: on version 0 the 
    	// worksheet definition was inside QBE tag; on version 1 the QBE tag is inside 
    	// WORKSHEET tag
    	if (previous.getName().equalsIgnoreCase(TAG_QBE)) {
    		
	    	if (previous.containsAttribute(TAG_WORKSHEET_DEFINITION)) {
	    		previous.delAttribute(TAG_WORKSHEET_DEFINITION);
	    	}
	    	templateSB.setAttribute(previous);
			SourceBean wk_def_sb = new SourceBean(TAG_WORKSHEET_DEFINITION);
			wk_def_sb.setCharacters(workSheetDef);
			templateSB.setAttribute(wk_def_sb);
			
			if(workSheetQuery!=null && !workSheetQuery.equals("") ){
				SourceBean query_sb = new SourceBean(QUERY);
				query_sb.setCharacters(workSheetQuery);
				previous.updAttribute(query_sb);
			}
			
			if(smartFilterValues!=null && !smartFilterValues.equals("")){
				SourceBean smartFilterValuesSB = new SourceBean(FORM_VALUES);
				smartFilterValuesSB.setCharacters(smartFilterValues);
				previous.updAttribute(smartFilterValuesSB);
			}
    		
    	} else {
    		
        	SourceBean qbeSB = (SourceBean) previous.getAttribute(TAG_QBE);
        	if (qbeSB != null) {
        		templateSB.setAttribute(qbeSB);
        	}
        	
    		SourceBean wk_def_sb = new SourceBean(TAG_WORKSHEET_DEFINITION);
    		wk_def_sb.setCharacters(workSheetDef);
    		templateSB.setAttribute(wk_def_sb);
    		
    		if(qbeSB != null && workSheetQuery!=null && !workSheetQuery.equals("") ){
    			SourceBean query_sb = new SourceBean(QUERY);
    			query_sb.setCharacters(workSheetQuery);
    			qbeSB.updAttribute(query_sb);
    		}
    		
    		if(qbeSB != null && smartFilterValues!=null && !smartFilterValues.equals("")){
    			SourceBean smartFilterValuesSB = new SourceBean(FORM_VALUES);
    			smartFilterValuesSB.setCharacters(smartFilterValues);
    			qbeSB.updAttribute(smartFilterValuesSB);
    		}
    		
    	}
    	


		String template = templateSB.toXML(false);	
		return template;
    }

    public String composeWorksheetTemplate(String workSheetDef, String workSheetQuery, String smartFilterValues, String originalQbeTempl) throws SourceBeanException{
    	SourceBean templateSB = new SourceBean(TAG_WORKSHEET);
    	templateSB.setAttribute(ATTRIBUTE_VERSION, CURRENT_VERSION);
    	SourceBean confSB = SourceBean.fromXMLString( originalQbeTempl );
    	// from version 0 to version 1 worksheet change compensation: on version 0 the 
    	// worksheet definition was inside QBE tag; on version 1 the QBE tag is inside 
    	// WORKSHEET tag
    	if (confSB.getName().equalsIgnoreCase(TAG_QBE)) {
    		
	    	if (confSB.containsAttribute(TAG_WORKSHEET_DEFINITION)) {
	    		confSB.delAttribute(TAG_WORKSHEET_DEFINITION);
	    	}
	    	templateSB.setAttribute(confSB);
			SourceBean wk_def_sb = new SourceBean(TAG_WORKSHEET_DEFINITION);
			wk_def_sb.setCharacters(workSheetDef);
			templateSB.setAttribute(wk_def_sb);
			
			if(workSheetQuery!=null && !workSheetQuery.equals("") ){
				SourceBean query_sb = new SourceBean(QUERY);
				query_sb.setCharacters(workSheetQuery);
				confSB.updAttribute(query_sb);
			}
			
			if(smartFilterValues!=null && !smartFilterValues.equals("")){
				SourceBean smartFilterValuesSB = new SourceBean(FORM_VALUES);
				smartFilterValuesSB.setCharacters(smartFilterValues);
				confSB.updAttribute(smartFilterValuesSB);
			}
		
    	} else {

    		SourceBean qbeSB = (SourceBean) confSB.getAttribute(TAG_QBE);
    		if (qbeSB != null) {
    			templateSB.setAttribute(qbeSB);
    			if(workSheetQuery!=null && !workSheetQuery.equals("") ){
    				SourceBean query_sb = new SourceBean(QUERY);
    				query_sb.setCharacters(workSheetQuery);
    				qbeSB.updAttribute(query_sb);
    			}
    			
    			if(smartFilterValues!=null && !smartFilterValues.equals("")){
    				SourceBean smartFilterValuesSB = new SourceBean(FORM_VALUES);
    				smartFilterValuesSB.setCharacters(smartFilterValues);
    				qbeSB.updAttribute(smartFilterValuesSB);
    			}
    		}
	    	
			SourceBean wk_def_sb = new SourceBean(TAG_WORKSHEET_DEFINITION);
			wk_def_sb.setCharacters(workSheetDef);
			templateSB.setAttribute(wk_def_sb);
    	}

		String template = templateSB.toXML(false);	
		return template;
    }
    
    public String createNewWorksheetTemplate(String worksheetDefinition) throws SourceBeanException {
    	SourceBean templateSB = new SourceBean(TAG_WORKSHEET);
    	templateSB.setAttribute(ATTRIBUTE_VERSION, CURRENT_VERSION);
		SourceBean worksheetDefinitionSB = new SourceBean(TAG_WORKSHEET_DEFINITION);
		worksheetDefinitionSB.setCharacters(worksheetDefinition);
		templateSB.setAttribute(worksheetDefinitionSB);
		String template = templateSB.toXML(false);	
		return template;
    }
    
	private Map applyService(Map parameters, BIObject biObject) {
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(parameters, "Input [parameters] cannot be null");
			
			parameters.put(PARAM_SERVICE_NAME, PARAM_ACTION_NAME);
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

