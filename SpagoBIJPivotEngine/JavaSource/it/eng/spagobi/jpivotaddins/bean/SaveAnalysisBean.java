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
package it.eng.spagobi.jpivotaddins.bean;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.jpivotaddins.bean.adapter.AnalysisAdapterUtil;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.utilities.messages.EngineMessageBundle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.thoughtworks.xstream.XStream;
import com.tonbeller.jpivot.chart.ChartComponent;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.navi.MdxQuery;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.wcf.component.ComponentSupport;
import com.tonbeller.wcf.controller.Dispatcher;
import com.tonbeller.wcf.controller.DispatcherSupport;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.format.FormatException;

public class SaveAnalysisBean extends ComponentSupport {
	
	public static final String PUBLIC_VISIBLITY = "Public";
	public static final String PRIVATE_VISIBLITY = "Private";
	
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	private PropertyChangeSupport propertySupport;
	
	Dispatcher dispatcher = new DispatcherSupport();
	
	private String analysisName;
	
	private String analysisDescription;

	private String analysisVisibility;

	private String recoveryAnalysisName;
	
	public String getAnalysisDescription() {
		return analysisDescription;
	}

	public void setAnalysisDescription(String analysisDescription) {
		RequestContext context = RequestContext.instance();
		Locale locale = context.getLocale();
		if (analysisDescription == null) {
			this.analysisDescription = null;
			return;
		}
		this.analysisDescription = analysisDescription;
	}

	public String getAnalysisName() {
		return analysisName;
	}
	
    public SaveAnalysisBean(String id,  RequestContext aContext) {
		super(id, null);
		analysisName = "";
		analysisDescription = "";
		analysisVisibility = PUBLIC_VISIBLITY;
		recoveryAnalysisName = "";
        propertySupport = new PropertyChangeSupport(this);
        getDispatcher().addRequestListener(null, null, dispatcher);
    }
	
	public void setAnalysisName(String analysisName) {
		RequestContext context = RequestContext.instance();
		Locale locale = context.getLocale();
	    if (analysisName == null || analysisName.trim().equals("")) {
	    	logger.error("Analysis name missing.");
	    	String msg = EngineMessageBundle.getMessage("error.analysis.name.missing", locale);
			throw new FormatException(msg);
		}
		// save the current analisys name if the recoveryAnalysisName variable
		this.recoveryAnalysisName = this.analysisName;
		this.analysisName = analysisName;
	}

	public String getAnalysisVisibility() {
		return analysisVisibility;
	}

	public void setAnalysisVisibility(String analysisVisibility) {
		if (PUBLIC_VISIBLITY.equalsIgnoreCase(analysisVisibility)) {
			this.analysisVisibility = PUBLIC_VISIBLITY;
		} else {
			this.analysisVisibility = PRIVATE_VISIBLITY;
		}
	}
	
	public void resetFields (){
		analysisName = null;
		analysisDescription = null;
		analysisVisibility = null;
	}
	
	public void saveSubObject(RequestContext reqContext){
		HttpSession session = reqContext.getSession();
		String documentId=(String)session.getAttribute("document");
		IEngUserProfile profile=(IEngUserProfile)session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		//String user = (String) profile.getUserUniqueIdentifier();
		String user = (String)((UserProfile) profile).getUserId();
		String userUniqueIdentifier = (String)((UserProfile) profile).getUserUniqueIdentifier();
		OlapModel olapModel = (OlapModel) session.getAttribute("query01");
		String query = null;
		MdxQuery mdxQuery = (MdxQuery) olapModel.getExtension("mdxQuery");
		if (mdxQuery != null) {
			query = mdxQuery.getMdxQuery();
		}
		if (query != null) {
			ChartComponent chart = (ChartComponent) session.getAttribute("chart01");
			TableComponent table = (TableComponent) session.getAttribute("table01");
			AnalysisBean analysis = (AnalysisBean) session.getAttribute("analysisBean");
			analysis = AnalysisAdapterUtil.createAnalysisBean(analysis.getConnectionName(), analysis.getCatalogUri(),
				chart, table, olapModel);
		    XStream dataBinder = new XStream();
		    String xmlString = dataBinder.toXML(analysis);
		    String visibilityBoolean = "false";
		    if (PUBLIC_VISIBLITY.equalsIgnoreCase(analysisVisibility)) 
				visibilityBoolean = "true";
		    
		   //ContentServiceProxy proxy=new ContentServiceProxy(user,session);
		   ContentServiceProxy proxy=new ContentServiceProxy(userUniqueIdentifier,session);
		    try {
                String result=proxy.saveSubObject(documentId, analysisName,analysisDescription, 
                									visibilityBoolean, xmlString);		
                logger.debug("result save subobject: " + result);
		        session.setAttribute("saveSubObjectMessage", result);
		        // if the saving operation has no success, the previous 
		        if (result.toUpperCase().startsWith("KO")) this.analysisName = recoveryAnalysisName;
		    } catch (Exception gse) {		
		    	logger.error("Error while saving analysis.", gse);
		    	session.setAttribute("saveSubObjectMessage", "KO - " + gse.getMessage());
		    }   
		}
	}

	public Document render(RequestContext arg0) throws Exception {
		return null;
	}
	
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * called once by the creating tag
     */
    public void initialize(RequestContext context) throws Exception {
    	super.initialize(context);
    }
	
}
