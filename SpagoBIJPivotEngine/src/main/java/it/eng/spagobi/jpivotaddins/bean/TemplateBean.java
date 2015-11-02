/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.bean;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.utilities.messages.EngineMessageBundle;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.navi.MdxQuery;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.format.FormatException;

public class TemplateBean implements Serializable {
	
	private String templateName;
	
	private transient Logger logger = Logger.getLogger(this.getClass());

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		RequestContext context = RequestContext.instance();
		Locale locale = context.getLocale();
		if (templateName == null || templateName.trim().equals("")) {
	    	logger.error("Template name missing.");
	    	String msg = EngineMessageBundle.getMessage("error.template.name.missing", locale);
			throw new FormatException(msg);
		}
		if (templateName.indexOf("/") != -1 || templateName.indexOf("\\") != -1) {
			logger.error("Template name contains file path separators.");
	    	String msg = EngineMessageBundle.getMessage("error.template.name.contains.separators", locale);
			throw new FormatException(msg);
		}
		if (templateName.indexOf("<") != -1 || templateName.indexOf(">") != -1) {
			logger.error("Template name contains invalid characters.");
	    	String msg = EngineMessageBundle.getMessage("error.template.name.invalid.characters", locale);
			throw new FormatException(msg);
		}
		this.templateName = templateName;
	}
	
	public void resetFields (){
		templateName = null;
	}

	public void saveTemplate(RequestContext reqContext){
		Logger logger = Logger.getLogger(this.getClass());
		HttpSession session = reqContext.getSession();
		IEngUserProfile profile=(IEngUserProfile)session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		String userUniqueIdentifier = (String) profile.getUserUniqueIdentifier();		
		String reference = (String)session.getAttribute("reference");
		String documentId=(String)session.getAttribute("document");
		OlapModel olapModel = (OlapModel) session.getAttribute("query01");
		MdxQuery mdxQuery = (MdxQuery) olapModel.getExtension("mdxQuery");
		String query = mdxQuery.getMdxQuery();
		// the queryWithParameters is added by the addParameter.jsp
		String queryWithParameters = (String) session.getAttribute("queryWithParameters");
		String initialQueryWithParameters = (String) session.getAttribute("initialQueryWithParameters");
		String initialMondrianQuery = (String) session.getAttribute("initialMondrianQuery");
		if (initialQueryWithParameters != null && initialMondrianQuery != null) {
			if (query.trim().equalsIgnoreCase(initialMondrianQuery.trim())) {
				// the initial Mondrian query was not modified
				if (queryWithParameters == null) {
					// if the queryWithParameters is not null it means that the user modified manually the query;
					// if it is null instead it means that the user did not modify manually the query.
					queryWithParameters = initialQueryWithParameters;
				}
			}
		}
		if (queryWithParameters == null) queryWithParameters = query;
		HashMap parameters = (HashMap) session.getAttribute("parameters");
		if (query != null) {
			String xmlString = "<olap>\n";
			//xmlString += "	<cube reference='" + catalogUri + "' />\n";
			xmlString += "	<cube reference='" + reference + "' />\n";
			xmlString += "	<MDXquery>\n";
			xmlString += queryWithParameters;
			if (parameters != null && parameters.size() > 0) {
				Set keys = parameters.keySet();
				Iterator keyIt = keys.iterator();
				while (keyIt.hasNext()) {
					String parameterName = (String) keyIt.next();
					String parameterUrlName = (String) parameters.get(parameterName);
					xmlString += "<parameter name='" + parameterUrlName + "' as='" + parameterName + "' />";
				}
			}
			xmlString += "	</MDXquery>\n";
			xmlString += "	<MDXMondrianQuery>\n";
			xmlString += query;
			xmlString += "	</MDXMondrianQuery>\n";
			xmlString += "</olap>";
			// controls that the produced String is a valid xml format
			Document document = null;
			try {
				SAXReader reader = new SAXReader();
				byte[] templateContent = xmlString.getBytes();
				ByteArrayInputStream is = new ByteArrayInputStream(templateContent);
				document = reader.read(is);
			} catch (Exception e) {
				logger.error("Error while parsing xml template " + xmlString, e);
				return;
			}
			xmlString = document.asXML();
		    try {
				ContentServiceProxy proxy = new ContentServiceProxy(userUniqueIdentifier, session);
				String result = proxy.saveObjectTemplate( documentId, templateName, xmlString);
		    } catch (Exception gse) {		
		    	logger.error("Error while saving template", gse);
		    }   
		} else {
			logger.error("Could not retrieve MDX query");
		}
	}
}
