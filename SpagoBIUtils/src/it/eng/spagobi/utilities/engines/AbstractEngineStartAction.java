/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
package it.eng.spagobi.utilities.engines;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.container.ContextManager;
import it.eng.spagobi.container.IBeanContainer;
import it.eng.spagobi.container.IContainer;
import it.eng.spagobi.container.SpagoBIRequestContainer;
import it.eng.spagobi.container.strategy.ExecutionContextRetrieverStrategy;
import it.eng.spagobi.container.strategy.IContextRetrieverStrategy;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.services.proxy.DataSourceServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class AbstractEngineStartAction extends AbstractBaseHttpAction {
	
	private String engineName;

	private ContextManager conetxtManager;
	
	private String userId;
	private String userUniqueIdentifier;
	private String auditId;
	private String documentId;
	private IDataSource dataSource;
	private IDataSet dataSet;
	private Locale locale;	
	private EngineAnalysisMetadata analysisMetadata;
	private byte[] analysisStateRowData;
	
	private Content template;
	
	private ContentServiceProxy contentProxy;
	private AuditServiceProxy auditProxy;
	private DataSourceServiceProxy datasourceProxy;
	private DataSetServiceProxy datasetProxy;
	
	
	private static final BASE64Decoder DECODER = new BASE64Decoder();
	
	public static final String AUDIT_ID = "SPAGOBI_AUDIT_ID";
	public static final String DOCUMENT_ID = "document";
	public static final String SBI_EXECUTION_ID = "SBI_EXECUTION_ID";
	
	public static final String COUNTRY = "SBI_COUNTRY";
	public static final String LANGUAGE = "SBI_LANGUAGE";
	
	public static final String SUBOBJ_ID = "subobjectId";
	public static final String SUBOBJ_NAME = "nameSubObject";
	public static final String SUBOBJ_DESCRIPTION = "descriptionSubObject";
	public static final String SUBOBJ_VISIBILITY = "visibilitySubObject";
		
	/**
     * Logger component
     */
    public static transient Logger logger = Logger.getLogger(AbstractEngineStartAction.class);
	
    
    
    
    public void init(SourceBean config) {
        super.init(config);
    } 
	

	public void service(SourceBean request, SourceBean response) throws SpagoBIEngineException {
		setSpagoBIRequestContainer(request);
		setSpagoBIResponseContainer(response);				
	}
	
	public String getEngineName() {
		return engineName;
	}


	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	
	
	// all accesses to session into the engine's scope refer to HttpSession and not to Spago's SessionContainer
	
	public ContextManager getConetxtManager() {
		if(conetxtManager == null) {
			IContextRetrieverStrategy contextRetriveStrategy;
			contextRetriveStrategy = new ExecutionContextRetrieverStrategy( getSpagoBIRequestContainer() );
			conetxtManager = new ContextManager(super.getSpagoBIHttpSessionContainer(), contextRetriveStrategy);
		}
		
		List list = conetxtManager.getKeys();
		
		return conetxtManager;
	}
	
	
	public IBeanContainer getSpagoBISessionContainer() {
		return getSpagoBIHttpSessionContainer();
	}
	
	
	public IBeanContainer getSpagoBIHttpSessionContainer() {
		return getConetxtManager();
	}
	
	
	public UserProfile getUserProfile() {
		return (UserProfile) getAttributeFromSession( IEngUserProfile.ENG_USER_PROFILE ); 
	}

    public String getUserId() {
    	UserProfile profile = null;
    	
    	if(userId == null) {  	
        	userId = (String)getUserProfile().getUserId();
    	}
    	
    	return userId;
    }
    
    public String getUserIdentifier() {
    	IEngUserProfile profile = null;
    	
    	if(userUniqueIdentifier == null) {	
    		userUniqueIdentifier = (String)getUserProfile().getUserUniqueIdentifier();
    	}
    	
    	return userUniqueIdentifier;
    }
    
    /**
     * Gets the audit id.
     * 
     * @return the audit id
     */
    public String getAuditId() {    	
    			
    	logger.debug("IN");	
    	
    	try {	    
    		if(auditId == null) {
        		auditId = getHttpRequest().getParameter( AUDIT_ID );
        	}
    	} finally {
			logger.debug("OUT");	
		}
		
		return auditId;
    }
    
    /**
     * Gets the document id.
     * 
     * @return the document id
     */
    public String getDocumentId() {
    	String documentIdInSection = null;
    
    	logger.debug("IN");	
    	
    	try {	    
	    	if(documentId == null) {
		    	documentIdInSection = getAttributeFromSessionAsString( DOCUMENT_ID );
		    	logger.debug("documentId in Session:" + documentIdInSection);
		    	
		    	if( requestContainsAttribute( DOCUMENT_ID ) ) {
		    		documentId = getAttributeAsString( DOCUMENT_ID );
		    	} else {
		    		documentId = documentIdInSection;
		    		logger.debug("documentId has been taken from session");
		    	}
	    	}
	    	
	    	if(documentId == null) {
	    		SpagoBIEngineStartupException e = new SpagoBIEngineStartupException(getEngineName(), "Impossible to retrive document id");
	    		e.setDescription("The engine is unable to retrive the id of the document to execute from request");
	    		e.addHint("Check on SpagoBI Server if the analytical document you want to execute have a valid template associated. Maybe you have saved the analytical document without " +
	    				"uploading a valid template file");
	    		throw e;
	    	}
    	} finally {
			logger.debug("OUT");	
		}
		
    	return documentId;   	
    }
    
    
    
   
    public JSONObject getTemplateAsJSONObject() {
    	JSONObject templateSB = null;
		try {
		  templateSB = new JSONObject(getTemplateAsString());
		} catch (JSONException e) {
			SpagoBIEngineStartupException engineException = new SpagoBIEngineStartupException(getEngineName(), "Impossible to parse template's content", e);
			engineException.setDescription("Impossible to parse template's content:  " + e.getMessage());
			engineException.addHint("Check if the document's template is a well formed json file");
			throw engineException;
		}		
		
		return templateSB;
	}
  
    public SourceBean getTemplateAsSourceBean() {
    	SourceBean templateSB = null;
		try {
		  templateSB = SourceBean.fromXMLString(getTemplateAsString());
		} catch (SourceBeanException e) {
			SpagoBIEngineStartupException engineException = new SpagoBIEngineStartupException(getEngineName(), "Impossible to parse template's content", e);
			engineException.setDescription("Impossible to parse template's content:  " + e.getMessage());
			engineException.addHint("Check if the document's template is a well formed xml file");
			throw engineException;
		}		
		
		return templateSB;
	}
    
    public String getTemplateAsString() {
	  return new String(getTemplate());
	}
    
    private byte[] getTemplate() {
		byte[] templateContent = null;
		HashMap requestParameters;
		
		if(template == null) {
			contentProxy = getContentServiceProxy();
			if(contentProxy == null) {
				throw new SpagoBIEngineStartupException("SpagoBIQbeEngine", 
						"Impossible to instatiate proxy class [" + ContentServiceProxy.class.getName() + "] " +
						"in order to retrive the template of document [" + documentId + "]");
			}
				
			requestParameters = ParametersDecoder.getDecodedRequestParameters(this.getHttpRequest());
			template = contentProxy.readTemplate(getDocumentId(), requestParameters);
		}	
		try {
			if(template == null)throw new SpagoBIEngineRuntimeException("There are no template associated to document [" + documentId + "]");
			templateContent = DECODER.decodeBuffer(template.getContent());
		} catch (Throwable e) {
			SpagoBIEngineStartupException engineException = new SpagoBIEngineStartupException(getEngineName(), "Impossible to get template's content", e);
			engineException.setDescription("Impossible to get template's content:  " + e.getMessage());
			engineException.addHint("Check the document's template");
			throw engineException;
		}
		
		return templateContent;
	}
    
    
    /**
     * Gets the data source.
     * 
     * @return the data source
     */
    
    public IDataSource getDataSource() {
    	String schema=null;
        String attrname=null;
    
    	if(dataSource == null) {
    		dataSource = getDataSourceServiceProxy().getDataSource( getDocumentId() );      	
            if (dataSource.checkIsMultiSchema()){
                logger.debug("Datasource [" + dataSource.getLabel() + "] is defined on multi schema");
                try {            
                    logger.debug("Retriving target schema for datasource [" + dataSource.getLabel() + "]");
                    attrname = dataSource.getSchemaAttribute();
                    logger.debug("Datasource's schema attribute name is equals to [" + attrname + "]");                                 
                    Assert.assertNotNull(attrname, "Datasource's schema attribute name cannot be null in order to retrive the target schema");
                    schema = (String)getUserProfile().getUserAttribute(attrname);
                    Assert.assertNotNull(schema, "Impossible to retrive the value of attribute [" + attrname + "] form user profile");
                    dataSource.setJndi( dataSource.getJndi() + schema);
                    logger.debug("Target schema for datasource  [" + dataSource.getLabel() + "] is [" + dataSource.getJndi()+ "]");
                } catch (Throwable t) {
                    throw new SpagoBIEngineRuntimeException("Impossible to retrive target schema for datasource [" + dataSource.getLabel() + "]", t);
                }
                logger.debug("Target schema for datasource  [" + dataSource.getLabel() + "] retrieved succesfully"); 
            }            
    	}

		return dataSource;
    }
    
    /*
    public IDataSource getDataSource() {
    	String schema;
    	String attrname;
    	
    	if(dataSource == null) {
            dataSource = getDataSourceServiceProxy().getDataSource( getDocumentId() );
        }
        
        if (dataSource.checkIsMultiSchema()){
	        logger.debug("Datasource [" + dataSource.getLabel() + "] is defined on multi schema");
        	
	        try {       
	        	logger.debug("Retriving target schema for datasource [" + dataSource.getLabel() + "]");
	         	attrname = dataSource.getSchemaAttribute();
	         	logger.debug("Datasource's schema attribute name is equals to [" + attrname + "]");	         	
	         	Assert.assertNotNull(attrname, "Datasource's schema attribute name cannot be null in order to retrive the target schema");
	            
	            schema = (String)getUserProfile().getUserAttribute(attrname);
	         	Assert.assertNotNull(schema, "Impossible to retrive the value of attribute [" + attrname + "] form user profile");
	            
	         	dataSource.setJndi( dataSource.getJndi() + schema);
	         	logger.debug("Target schema for datasource  [" + dataSource.getLabel() + "] is [" + dataSource.getJndi()+ "]");
	        } catch (Throwable t) {
	        	throw new SpagoBIEngineRuntimeException("Impossible to retrive target schema for datasource [" + dataSource.getLabel() + "]", t);
	        }
	        
	        logger.debug("Target schema for datasource  [" + dataSource.getLabel() + "] retrieved succesfully");
        }
        
        return dataSource;
    } 
    */
    
    public IDataSet getDataSet() {
    	if(dataSet == null) {
    		dataSet = getDataSetServiceProxy().getDataSet( getDocumentId() );
    	}
		
		return dataSet;    	
    }
    
    
    /**
     * Gets the locale.
     * 
     * @return the locale
     */
    public Locale getLocale() {
    	String language;
		String country;
		
    	if(locale == null) {
    		
    		logger.debug("IN");
    		
    		language = getAttributeAsString( LANGUAGE );
    		country = getAttributeAsString( COUNTRY );
    		logger.debug("Locale parameters received: language = [" + language + "] ; country = [" + country + "]");

    		try {
    		    locale = new Locale(language, country); 
    		} catch (Exception e) {
    		    logger.debug("Error while creating Locale object from input parameters: language = [" + language
    			    + "] ; country = [" + country + "]");
    		    logger.debug("Creating default locale [en,US].");
    		    locale = new Locale("en", "US");
    		}
    		
    		logger.debug("IN");
    	}
    	
    	return locale;
    }
    
    /**
     * Gets the analysis metadata.
     * 
     * @return the analysis metadata
     */
    public EngineAnalysisMetadata getAnalysisMetadata() {
		if( analysisMetadata != null) {
			return analysisMetadata;
		}
    	
		logger.debug("IN");
		
		analysisMetadata = new EngineAnalysisMetadata();
		
    	if( requestContainsAttribute(SUBOBJ_ID) ) {
			
    		Integer id = getAttributeAsInteger( SUBOBJ_ID );
    		if(id == null) {
    			logger.warn( "Value [" + getAttribute( SUBOBJ_ID ).toString() + "] is not a valid subobject id");
    		} 
    		analysisMetadata.setId(id);
    		
			if( requestContainsAttribute( SUBOBJ_NAME ) ) {
				analysisMetadata.setName( getAttributeAsString( SUBOBJ_NAME ) );
			} else {
				logger.warn("No name attribute available in request for subobject [" + getAttributeAsString( SUBOBJ_ID ) + "]");
				analysisMetadata.setName(  getAttributeAsString( SUBOBJ_ID ) );
			}
    		
			if( requestContainsAttribute( SUBOBJ_DESCRIPTION ) ) {
				analysisMetadata.setDescription( getAttributeAsString( SUBOBJ_DESCRIPTION ) );
			} else {
				logger.warn("No description attribute available in request for subobject [" + getAttributeAsString( SUBOBJ_ID ) + "]");
				analysisMetadata.setDescription( "" );
			}
			
			if( requestContainsAttribute( SUBOBJ_VISIBILITY ) ) {
				if( requestContainsAttribute(SUBOBJ_VISIBILITY, "Public") ) {
					analysisMetadata.setScope( EngineAnalysisMetadata.PUBLIC_SCOPE );
				} else {
					logger.warn("No visibility attribute available in request for subobject [" + getAttributeAsString( SUBOBJ_ID ) + "]");
					analysisMetadata.setScope( EngineAnalysisMetadata.PRIVATE_SCOPE );
				}			
			}
    	}
    	
    	logger.debug("OUT");
    	
    	return analysisMetadata;
	}

	/**
	 * Gets the analysis state row data.
	 * 
	 * @return the analysis state row data
	 */
	public byte[] getAnalysisStateRowData() {
		Content spagoBISubObject;
    	byte[] rowData;
    	
		if ( analysisStateRowData == null && getAnalysisMetadata().getId() != null ) {
			
			logger.debug("IN");
			
			spagoBISubObject = getContentServiceProxy().readSubObjectContent( getAnalysisMetadata().getId().toString() );	
			try {
				rowData = DECODER.decodeBuffer( spagoBISubObject.getContent() );
				analysisStateRowData = rowData;
			} catch (IOException e) {
				logger.warn( "Impossible to decode the content of " + getAnalysisMetadata().getId().toString() + " subobject");
    			return null;
			}
			
			logger.debug("OUT");
		}
		
		return analysisStateRowData;
	}
    	
	private ContentServiceProxy getContentServiceProxy() {
	   if(contentProxy == null) {
		   contentProxy = new ContentServiceProxy(getUserIdentifier(), getHttpSession());
	   }	   
	    
	   return contentProxy;
   }
   
   public AuditServiceProxy getAuditServiceProxy() {
	   if(auditProxy == null && getAuditId() != null) {
		   auditProxy = new AuditServiceProxy(getAuditId(), getUserIdentifier(), getHttpSession());
	   }	   
	    
	   return auditProxy;
   }
   
   private DataSourceServiceProxy getDataSourceServiceProxy() {
	   if(datasourceProxy == null) {
		   datasourceProxy = new DataSourceServiceProxy( getUserIdentifier() , getHttpSession() );
	   }	   
	    
	   return datasourceProxy;
   }
   
   public DataSetServiceProxy getDataSetServiceProxy() {
	   if(datasetProxy == null) {
		   datasetProxy = new DataSetServiceProxy(getUserIdentifier() , getHttpSession());
	   }	   
	    
	   return datasetProxy;
   }
   
   
   
   public Map getEnv() {
	   Map env = new HashMap();
	   
	   copyRequestParametersIntoEnv(env, getSpagoBIRequestContainer());
	   env.put(EngineConstants.ENV_DATASOURCE, getDataSource());
	   env.put(EngineConstants.ENV_DOCUMENT_ID, getDocumentId());
	   env.put(EngineConstants.ENV_USER_PROFILE, getUserProfile());
	   env.put(EngineConstants.ENV_CONTENT_SERVICE_PROXY, getContentServiceProxy());
	   env.put(EngineConstants.ENV_AUDIT_SERVICE_PROXY, getAuditServiceProxy() );
	   env.put(EngineConstants.ENV_DATASET_PROXY, getDataSetServiceProxy()); 
	   env.put(EngineConstants.ENV_LOCALE, getLocale()); 
		
	   return env;
   }
    
   /**
	 * Copy request parameters into env.
	 * 
	 * @param env the env
	 * @param serviceRequest the service request
	 */
	public void copyRequestParametersIntoEnv(Map env, IContainer request) {
		Set parameterStopList = null;
		List requestParameters = null;
		
		logger.debug("IN");
		
		parameterStopList = new HashSet();
		parameterStopList.add("template");
		parameterStopList.add("ACTION_NAME");
		parameterStopList.add("NEW_SESSION");
		parameterStopList.add("document");
		parameterStopList.add("spagobicontext");
		parameterStopList.add("BACK_END_SPAGOBI_CONTEXT");
		parameterStopList.add("userId");
		parameterStopList.add("auditId");
		
		
		requestParameters = ((SpagoBIRequestContainer)request).getRequest().getContainedAttributes();
		for(int i = 0; i < requestParameters.size(); i++) {
			SourceBeanAttribute attrSB = (SourceBeanAttribute)requestParameters.get(i);
			logger.debug("Parameter [" + attrSB.getKey() + "] has been read from request");
			logger.debug("Parameter [" + attrSB.getKey() + "] is of type  " + attrSB.getValue().getClass().getName());
			logger.debug("Parameter [" + attrSB.getKey() + "] is equal to " + attrSB.getValue().toString());
			
			if(parameterStopList.contains(attrSB.getKey())) {
				logger.debug("Parameter [" + attrSB.getKey() + "] copyed into environment parameters list: FALSE");
				continue;
			}
			
			env.put(attrSB.getKey(), decodeParameterValue(attrSB.getValue().toString()) );
			logger.debug("Parameter [" + attrSB.getKey() + "] copyed into environment parameters list: TRUE");
		}

		logger.debug("OUT");
	}
    
   
	/**
	 * Decode parameter value.
	 * 
	 * @param parValue the par value
	 * 
	 * @return the string
	 */
	private String decodeParameterValue(String parValue) {
		String newParValue;
			
		ParametersDecoder decoder = new ParametersDecoder();
		if(decoder.isMultiValues(parValue)) {			
			List values = decoder.decode(parValue);
			newParValue = "";
			for(int i = 0; i < values.size(); i++) {
				newParValue += (i>0?",":"");
				newParValue += values.get(i);
			}
		} else {
			newParValue = parValue;
		}
			
		return newParValue;
	}
	
}
