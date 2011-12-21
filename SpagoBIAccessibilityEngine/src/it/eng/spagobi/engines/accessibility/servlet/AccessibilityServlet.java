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
package it.eng.spagobi.engines.accessibility.servlet;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.accessibility.dao.QueryExecutor;
import it.eng.spagobi.engines.accessibility.xslt.Transformation;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.services.proxy.DataSourceServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

public class AccessibilityServlet extends HttpServlet {
	
	private static transient Logger logger = Logger.getLogger(AccessibilityServlet.class);
	private static String CONNECTION_NAME="connectionName";
	private static String QUERY="query";
	private static String DOCUMENT_ID="document";
	private static String USER_ID="user_id";
	
	
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	logger.debug("Initializing SpagoBI Accessibility Engine...");
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	logger.debug("IN");
    	//get the document
    	HttpSession session = request.getSession();
    	logger.debug("documentId IN Session:"+(String)session.getAttribute(DOCUMENT_ID));
    	// USER PROFILE
    	String documentId = (String) request.getParameter(DOCUMENT_ID);
    	if (documentId==null){
    	    documentId=(String)session.getAttribute(DOCUMENT_ID);
    	    logger.debug("documentId From Session:"+documentId);
    	}
    	logger.debug("documentId:"+documentId);
    	
    	//get userprofile
    	IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
    	logger.debug("profile from session: " + profile);
    	
    	// AUDIT UPDATE
    	String auditId = request.getParameter("SPAGOBI_AUDIT_ID");
    	AuditAccessUtils auditAccessUtils = (AuditAccessUtils) request.getSession().getAttribute("SPAGOBI_AUDIT_UTILS");
    	if (auditAccessUtils != null)
    	    auditAccessUtils.updateAudit(session,(String) profile.getUserUniqueIdentifier(), auditId, new Long(System
    		    .currentTimeMillis()), null, "EXECUTION_STARTED", null, null);
    	
    	
    	//read connection from request	
    	String requestConnectionName = (String) request.getParameter(CONNECTION_NAME);
    	if (requestConnectionName==null) logger.debug("requestConnectionName is NULL");
    	else logger.debug("requestConnectionName:"+requestConnectionName);
    	
    	Connection con = null;
    	String query= null;

    	SpagoBiDataSet dataset = getDataSet(requestConnectionName, session, profile, documentId);
    	if (dataset == null) {
    	    logger.debug("No dataset query associated to this document");
    	    logger.debug("Try to get datasource");
    	    con = getConnection(requestConnectionName,session,profile,documentId);
    	    if(con == null){
    	    	logger.error("Document "+documentId+" has no dataset query neither datasource associated!");
    		    // AUDIT UPDATE
    		    if (auditAccessUtils != null)
    			auditAccessUtils.updateAudit(session,(String) profile.getUserUniqueIdentifier(), auditId, null, new Long(System
    				.currentTimeMillis()), "EXECUTION_FAILED", "No connection available", null);
    		    return;
    	    } else{
    	    	//get the request query parameter name
    	    	query = (String) request.getParameter(QUERY);
    	    	
    	    }   	    
    	} else{
    		//get query
    		query = dataset.getQuery();
    		try {
				con = dataset.getDataSource().readConnection(dataset.getDataSource().getSchemaAttribute());
			} catch (Exception e) {
				logger.error("Unable to get connection", e);
			    if (auditAccessUtils != null)
					auditAccessUtils.updateAudit(session,(String) profile.getUserUniqueIdentifier(), auditId, null, new Long(System
						.currentTimeMillis()), "EXECUTION_FAILED", e.getMessage(), null);
			    return;
			}  		
    		
    	}   
    	//call dao to execute query
    	try {
    		//gets request parameters to execute query
    		HashMap<String, String> parameters = cleanParameters(request);
    		
			String xmlResult = QueryExecutor.executeQuery(con, query, parameters);

			byte[] xsl = getDocumentXSL(requestConnectionName, session, profile, documentId);

	    	byte[] html = Transformation.tarnsformXSLT(xmlResult, xsl);

		    String outputType = "html";
		    response.setContentType("text/html");
			response.getOutputStream().write(html);

		    response.getOutputStream().flush();
		    

		} catch (Exception e1) {
			logger.error("Unable to output result", e1);
		    if (auditAccessUtils != null)
				auditAccessUtils.updateAudit(session,(String) profile.getUserUniqueIdentifier(), auditId, null, new Long(System
					.currentTimeMillis()), "EXECUTION_FAILED", e1.getMessage(), null);
		    return;
		}

    	logger.debug("OUT");
    }
    
    private HashMap<String, String> cleanParameters(HttpServletRequest request){
		//gets request parameters to execute query
		HashMap<String, String> parameters = new HashMap<String, String>((HashMap<String, String>)request.getParameterMap());
		HashMap<String, String> parametersCleaned = new HashMap<String, String>();
		if(parameters.containsKey(QUERY)){    			
			parameters.remove(QUERY);
		}
		if(parameters.containsKey(DOCUMENT_ID)){    			
			parameters.remove(DOCUMENT_ID);
		}
		if(parameters.containsKey(CONNECTION_NAME)){    			
			parameters.remove(CONNECTION_NAME);
		}
		if(parameters.containsKey(USER_ID)){    			
			parameters.remove(USER_ID);
		}
        if(!parameters.isEmpty()){
        	for (Iterator it = parameters.keySet().iterator(); it.hasNext(); ) {
        		String key= (String)it.next();
        		parametersCleaned.put(key, request.getParameter(key));
        	}	        	
        }
    	return parametersCleaned;
    }
    /**
     * This method, based on the data sources table, gets a database connection
     * and return it
     * 
     * @return the database connection
     */
    private Connection getConnection(String requestConnectionName,HttpSession session,IEngUserProfile profile,String documentId) {
    	logger.debug("IN");
    	IDataSource ds = null;

		DataSourceServiceProxy proxyDS = new DataSourceServiceProxy((String)profile.getUserUniqueIdentifier(),session);
		if(requestConnectionName == null){
			//get document's datasource
			ds = proxyDS.getDataSource(documentId);
		}else{
			//get datasource by label
			ds = proxyDS.getDataSourceByLabel(requestConnectionName);
		}
		if(ds != null){
			String schema=null;
			try {
				if (ds.checkIsMultiSchema()){
					String attrname=ds.getSchemaAttribute();
					if (attrname!=null) 
						schema = (String)profile.getUserAttribute(attrname);
				}
			} catch (EMFInternalError e) {
				logger.error("Cannot retrive ENTE", e);
			}
		
			if (ds==null) {
			    logger.warn("Data Source IS NULL. There are problems reading DataSource informations");
			    return null;
			}
			// get connection
			Connection conn = null;
			
			try {
				conn = ds.toSpagoBiDataSource().readConnection(schema);
				return conn;
			} catch (Exception e) {
				logger.error("Cannot retrive connection", e);
			}
		}
		logger.debug("OUT");
		return null;

    }

    private SpagoBiDataSet getDataSet(String requestConnectionName,HttpSession session,IEngUserProfile profile,String documentId) {
    	logger.debug("IN");
		logger.debug("IN.documentId:"+documentId);
		DataSetServiceProxy proxyDataset =new DataSetServiceProxy((String)profile.getUserUniqueIdentifier(),session);
		//get document's dataset
		IDataSet dataset = proxyDataset.getDataSet(documentId);
		
		if (dataset==null) {
		    logger.warn("Data Set IS NULL. There are problems reading DataSet informations");
		    return null;
		}
		SpagoBiDataSet biDataset = null;
		try {
			biDataset= dataset.toSpagoBiDataSet();
		} catch (Exception e) {
			logger.error("Cannot retrive SpagoBi DataSet", e);
		} 
		logger.debug("OUT");
		return biDataset;

    }
    
    private byte[] getDocumentXSL(String requestConnectionName,HttpSession session,IEngUserProfile profile,String documentId) {
    	logger.debug("IN");

		ContentServiceProxy contentProxy = new ContentServiceProxy((String)profile.getUserUniqueIdentifier(),session);

		Content templateContent = contentProxy.readTemplate(documentId,new HashMap());
		
		InputStream is = null;
		byte[] byteContent = null;
		try {
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			byteContent = bASE64Decoder.decodeBuffer(templateContent.getContent());
			is = new java.io.ByteArrayInputStream(byteContent);
		}catch (Throwable t){
			logger.warn("Error on decompile",t); 
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				logger.warn("Error on closing inputstream",e); 
			}
		}

		logger.debug("OUT");
		return byteContent;

    }
}
