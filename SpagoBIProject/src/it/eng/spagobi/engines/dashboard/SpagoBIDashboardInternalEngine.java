/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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

package it.eng.spagobi.engines.dashboard;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.InternalEngineIFace;
import it.eng.spagobi.engines.chart.utils.DataSetAccessFunctions;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;
import it.eng.spagobi.utilities.ParametersDecoder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SpagoBIDashboardInternalEngine implements InternalEngineIFace {

    private static transient Logger logger = Logger.getLogger(SpagoBIDashboardInternalEngine.class);

    public static final String messageBundle = "MessageFiles.component_spagobidashboardIE_messages";
    
    Map confParameters;
    Map dataParameters;
    Map drillParameters;

    /**
     * Executes the document and populates the response.
     * 
     * @param requestContainer The <code>RequestContainer</code> object (the session
     * can be retrieved from this object)
     * @param obj The <code>BIObject</code> representing the document to
     * be executed
     * @param response The response <code>SourceBean</code> to be populated
     * 
     * @throws EMFUserError the EMF user error
     */
    public void execute(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError {

		logger.debug("IN");
	
		try {
	
		    if (obj == null) {
				logger.error("The input object is null.");
				throw new EMFUserError(EMFErrorSeverity.ERROR, "100", messageBundle);
		    }
	
		    if (!obj.getBiObjectTypeCode().equalsIgnoreCase("DASH")) {
				logger.error("The input object is not a dashboard.");
				throw new EMFUserError(EMFErrorSeverity.ERROR, "1001", messageBundle);
		    }
	
		    byte[] contentBytes = null;
		    try {
			ObjTemplate template = DAOFactory.getObjTemplateDAO().getBIObjectActiveTemplate(obj.getId());
			if (template == null)
			    throw new Exception("Active Template null");
			contentBytes = template.getContent();
			if (contentBytes == null)
			    throw new Exception("Content of the Active template null");
		    } catch (Exception e) {
				logger.error("Error while recovering template content: \n" , e);
				throw new EMFUserError(EMFErrorSeverity.ERROR, "1002", messageBundle);
		    }
		    // get bytes of template and transform them into a SourceBean
		    SourceBean content = null;
		    try {
				String contentStr = new String(contentBytes);
				content = SourceBean.fromXMLString(contentStr);
		    } catch (Exception e) {
				logger.error("Error while converting the Template bytes into a SourceBean object");
				throw new EMFUserError(EMFErrorSeverity.ERROR, "1003", messageBundle);
		    }
		    // get information from the conf SourceBean and pass them into the
		    // response
		    String movie = (String) content.getAttribute("movie");
		    String width = (String) content.getAttribute("DIMENSION.width");
		    String height = (String) content.getAttribute("DIMENSION.height");
	
		    String dataurl = (String) content.getAttribute("DATA.url");
		    
		    // get all the parameters for data url
		    dataParameters = new LinkedHashMap();
		    confParameters = new LinkedHashMap();
		    drillParameters = new LinkedHashMap();
		    
		    SessionContainer session = requestContainer.getSessionContainer();
		    IEngUserProfile profile = (IEngUserProfile) session.getPermanentContainer().getAttribute(
			    IEngUserProfile.ENG_USER_PROFILE);
		    
		    SourceBean serviceRequest=requestContainer.getServiceRequest();
		    
			 // get all the parameters for dash configuration		    
		    defineDataParameters(content, obj, profile);
		    defineConfParameters(content, profile);
		    defineLinkParameters(content, serviceRequest);
		    
		    // set information into reponse
		    response.setAttribute(ObjectsTreeConstants.SESSION_OBJ_ATTR, obj);
		    response.setAttribute("movie", movie);
		    response.setAttribute("dataurl", dataurl);
		    response.setAttribute("width", width);
		    response.setAttribute("height", height);
		    
		    response.delAttribute("confParameters");
		    response.setAttribute("confParameters", getConfParameters());
		    response.delAttribute("dataParameters");
		    response.setAttribute("dataParameters", getDataParameters());	
		    response.delAttribute("drillParameters");
		    response.setAttribute("drillParameters", getDrillParameters());	

		    // set information for the publisher
		    response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "DASHBOARD");
	
		} catch (EMFUserError error) {
		    logger.error("Cannot exec the dashboard", error);
		    throw error;
		} catch (Exception e) {
		    logger.error("Cannot exec the dashboard", e);
		    throw new EMFUserError(EMFErrorSeverity.ERROR, "100", messageBundle);
		} finally {
		    logger.debug("OUT");
		}
    }

    /**
     * The <code>SpagoBIDashboardInternalEngine</code> cannot manage
     * subobjects so this method must not be invoked.
     * 
     * @param requestContainer The <code>RequestContainer</code> object (the session
     * can be retrieved from this object)
     * @param obj The <code>BIObject</code> representing the document
     * @param response The response <code>SourceBean</code> to be populated
     * @param subObjectInfo An object describing the subobject to be executed
     * 
     * @throws EMFUserError the EMF user error
     */
    public void executeSubObject(RequestContainer requestContainer, BIObject obj, SourceBean response,
	    Object subObjectInfo) throws EMFUserError {
    	// it cannot be invoked
		logger.error("SpagoBIDashboardInternalEngine cannot exec subobjects.");
		throw new EMFUserError(EMFErrorSeverity.ERROR, "101", messageBundle);
    }

    /**
     * Function not implemented. Thid method should not be called
     * 
     * @param requestContainer The <code>RequestContainer</code> object (the session
     * can be retrieved from this object)
     * @param response The response <code>SourceBean</code> to be populated
     * @param obj the obj
     * 
     * @throws InvalidOperationRequest the invalid operation request
     * @throws EMFUserError the EMF user error
     */
    public void handleNewDocumentTemplateCreation(RequestContainer requestContainer, BIObject obj, SourceBean response)
	    throws EMFUserError, InvalidOperationRequest {
		logger.error("SpagoBIDashboardInternalEngine cannot build document template.");
		throw new InvalidOperationRequest();

    }

    /**
     * Function not implemented. Thid method should not be called
     * 
     * @param requestContainer The <code>RequestContainer</code> object (the session
     * can be retrieved from this object)
     * @param response The response <code>SourceBean</code> to be populated
     * @param obj the obj
     * 
     * @throws InvalidOperationRequest the invalid operation request
     * @throws EMFUserError the EMF user error
     */
    public void handleDocumentTemplateEdit(RequestContainer requestContainer, BIObject obj, SourceBean response)
	    throws EMFUserError, InvalidOperationRequest {
		logger.error("SpagoBIDashboardInternalEngine cannot build document template.");
		throw new InvalidOperationRequest();
    }
    
    /**
	 * set parameters configuration for the creation of the dashboard getting them from template or from Dataset/LOV.
	 * 
	 * @param content the content of the template.
	 * @param profile the user's profile
	 * 
	 * @return A chart that displays a value as a dial.
	 */
	private void defineConfParameters(SourceBean content, IEngUserProfile profile) throws Exception {
		logger.debug("IN");
		
		boolean isDsConfDefined = false;
		String confDataset = "";
	
		// get all the parameters for dash configuration
	    confParameters = new LinkedHashMap();
	    SourceBean confSB = (SourceBean) content.getAttribute("CONF");
	    List confAttrsList = confSB.getContainedSourceBeanAttributes();
	    Iterator confAttrsIter = confAttrsList.iterator();
	    while (confAttrsIter.hasNext()) {
			SourceBeanAttribute paramSBA = (SourceBeanAttribute) confAttrsIter.next();
			SourceBean param = (SourceBean) paramSBA.getValue();
			String nameParam = (String) param.getAttribute("name");
			String valueParam = replaceParsInString((String) param.getAttribute("value"));	
			
			confParameters.put(nameParam, valueParam);
	    }		
	    //defines if configuration is by dataset
	    if(confParameters.get("confdataset")!=null && !(((String)confParameters.get("confdataset")).equalsIgnoreCase("") )){	
			confDataset=(String)confParameters.get("confdataset");
			isDsConfDefined=true;
		}
		else {
			isDsConfDefined=false;
		}
	    //if the configuration is by dataset reading it and compiles attributes with its values.
		if(isDsConfDefined){
			logger.debug("configuration defined in dataset "+confDataset);
			
			String parameters=DataSetAccessFunctions.getDataSetResultFromLabel(profile, confDataset, dataParameters);
			SourceBean sourceBeanResult=null;
			try {
				sourceBeanResult = SourceBean.fromXMLString(parameters);
			} catch (SourceBeanException e) {
				logger.error("error in reading configuration lov");
				throw new Exception("error in reading configuration lov");
			}
			confParameters = new LinkedHashMap();
			SourceBean sbRow=(SourceBean)sourceBeanResult.getAttribute("ROW");
			String parValue = "";
			parValue = (sbRow.getAttribute("minValue")!=null)?(String)sbRow.getAttribute("minValue"):(String)sbRow.getAttribute("MINVALUE");
			confParameters.put("minValue", parValue);
			parValue = (sbRow.getAttribute("maxValue")!=null)?(String)sbRow.getAttribute("maxValue"):(String)sbRow.getAttribute("MAXVALUE");
			confParameters.put("maxValue", parValue);
			parValue = (sbRow.getAttribute("lowValue")!=null)?(String)sbRow.getAttribute("lowValue"):(String)sbRow.getAttribute("LOWVALUE");
			confParameters.put("lowValue", parValue);
			parValue = (sbRow.getAttribute("highValue")!=null)?(String)sbRow.getAttribute("highValue"):(String)sbRow.getAttribute("HIGHVALUE");
			confParameters.put("highValue", parValue);
			parValue = (sbRow.getAttribute("refreshRate")!=null)?(String)sbRow.getAttribute("refreshRate"):(String)sbRow.getAttribute("REFRESHRATE");
			confParameters.put("refreshRate", parValue);
			parValue = (sbRow.getAttribute("multichart")!=null)?(String)sbRow.getAttribute("multichart"):(String)sbRow.getAttribute("MULTICHART");
			confParameters.put("multichart", parValue);
			parValue = (sbRow.getAttribute("numCharts")!=null)?(String)sbRow.getAttribute("numCharts"):(String)sbRow.getAttribute("NUMCHARTS");
			confParameters.put("numCharts", parValue);
			parValue = (sbRow.getAttribute("orientation_multichart")!=null)?(String)sbRow.getAttribute("orientation_multichart"):(String)sbRow.getAttribute("ORIENTATION_MULTICHART");
			confParameters.put("orientation_multichart", parValue);
			//defining title and legend variables			
			parValue = (sbRow.getAttribute("displayTitleBar")!=null)?(String)sbRow.getAttribute("displayTitleBar"):(String)sbRow.getAttribute("DISPLAYTITLEBAR");
			confParameters.put("displayTitleBar", parValue);
			parValue = (sbRow.getAttribute("title")!=null)?(String)sbRow.getAttribute("title"):(String)sbRow.getAttribute("TITLE");
			confParameters.put("title", parValue);
			parValue = (sbRow.getAttribute("colorTitle")!=null)?(String)sbRow.getAttribute("colorTitle"):(String)sbRow.getAttribute("COLORTITLE");
			confParameters.put("colorTitle", parValue);
			parValue = (sbRow.getAttribute("sizeTitle")!=null)?(String)sbRow.getAttribute("sizeTitle"):(String)sbRow.getAttribute("SIZETITLE");
			confParameters.put("sizeTitle", parValue);
			parValue = (sbRow.getAttribute("fontTitle")!=null)?(String)sbRow.getAttribute("fontTitle"):(String)sbRow.getAttribute("FONTTITLE");
			confParameters.put("fontTitle", parValue);
			parValue = (sbRow.getAttribute("colorTitleSerie")!=null)?(String)sbRow.getAttribute("colorTitleSerie"):(String)sbRow.getAttribute("COLORTITLESERIE");
			confParameters.put("colocolorTitleSerierTitle", parValue);
			parValue = (sbRow.getAttribute("sizeTitleSerie")!=null)?(String)sbRow.getAttribute("sizeTitleSerie"):(String)sbRow.getAttribute("SIZETITLESERIE");
			confParameters.put("sizeTitleSerie", parValue);
			parValue = (sbRow.getAttribute("fontTitleSerie")!=null)?(String)sbRow.getAttribute("fontTitleSerie"):(String)sbRow.getAttribute("FONTTITLESERIE");
			confParameters.put("fontTitleSerie", parValue);
			parValue = (sbRow.getAttribute("legend")!=null)?(String)sbRow.getAttribute("legend"):(String)sbRow.getAttribute("LEGEND");
			confParameters.put("legend", parValue);
			
			parValue = (sbRow.getAttribute("numNeedles")!=null)?(String)sbRow.getAttribute("numNeedles"):(String)sbRow.getAttribute("NUMNEEDLES");
			confParameters.put("numNeedles",parValue);
			int numNeedles = 0;
			try{
				numNeedles = Integer.parseInt(parValue);
			}catch(Exception e){
				logger.error("error in reading configuration dataset. Number of needles is invalid." );
				throw new Exception("error in reading configuration dataset. Number of needles is invalid.");
			}
			
			for (int i=0 ; i < numNeedles; i++){	
				parValue = (sbRow.getAttribute("colorNeedle"+(i+1))!=null)?(String)sbRow.getAttribute("colorNeedle"+(i+1)):(String)sbRow.getAttribute("COLORNEEDLE"+(i+1));
				confParameters.put("colorNeedle"+(i+1),parValue);
				parValue = (sbRow.getAttribute("value"+(i+1))!=null)?(String)sbRow.getAttribute("value"+(i+1)):(String)sbRow.getAttribute("VALUE"+(i+1));
				confParameters.put("value"+(i+1), parValue);
			}
		}
		else
			logger.debug("Configuration set in template");
		
		logger.debug("out");
	}
	
    /**
	 * set parameters for getting the data 
	 * 
	 * @param content the content of the template.
	 * @param obj the object document
	 * @param profile the user's profile
	 * 
	 * @return A chart that displays a value as a dial.
	 */
	private void defineDataParameters(SourceBean content, BIObject obj, IEngUserProfile profile) throws Exception {
		SourceBean dataSB = (SourceBean) content.getAttribute("DATA");
	    List dataAttrsList = dataSB.getContainedSourceBeanAttributes();
	    Iterator dataAttrsIter = dataAttrsList.iterator();
	    
	    if(obj.getDataSetId()!=null){
		    String dataSetId=obj.getDataSetId().toString();
		    dataParameters.put("datasetid", dataSetId);
	    }
	    while (dataAttrsIter.hasNext()) {
			SourceBeanAttribute paramSBA = (SourceBeanAttribute) dataAttrsIter.next();
			SourceBean param = (SourceBean) paramSBA.getValue();
			String nameParam = (String) param.getAttribute("name");
			String valueParam = (String) param.getAttribute("value");
	
			dataParameters.put(nameParam, valueParam);
	    }
	    
	    // puts the document id
	    dataParameters.put("documentId", obj.getId().toString());
	    // puts the userId into parameters for data recovery
	    
	    dataParameters.put("userid", ((UserProfile)profile).getUserUniqueIdentifier());

	    // create the title
	    String title = "";
	    title += obj.getName();
	    String objDescr = obj.getDescription();
	    if ((objDescr != null) && !objDescr.trim().equals("")) {
	    	title += ": " + objDescr;
	    }
	    
		String parameters="";
	    //Search if the chart has parameters
		List parametersList=obj.getBiObjectParameters();
		logger.debug("Check for BIparameters and relative values");
		if(parametersList!=null){
			ParametersDecoder decoder = new ParametersDecoder();
			for (Iterator iterator = parametersList.iterator(); iterator.hasNext();) {
				BIObjectParameter par= (BIObjectParameter) iterator.next();
				String url=par.getParameterUrlName();
				List values=par.getParameterValues();
				
				
				if(values!=null){
					/*
					if ((values.size() >=1)) {
					    List values = decoder.decode(parValue);
					    newParValue = "";
					    for (int i = 0; i < values.size(); i++) {
							newParValue += (i > 0 ? "," : "");
							newParValue += values.get(i);
					    }

					} else {
					    newParValue = parValue;
					}
					*/
					if(values.size()==1){
						String value=(String)values.get(0);
						if (value.equals("%")) value = "%25";
					    else if (value.equals(";%")) value = ";%25";
						dataParameters.put(url, value);
					}else if(values.size() >=1){
						String value = "'"+(String)values.get(0)+"'";					
						for(int k = 1; k< values.size() ; k++){
							value = value + ",'" + (String)values.get(k)+"'";
						}
						dataParameters.put(url, value);
					}
				}
			}	
		}
	}
	
    /**
	 * set parameters for the drill action.
	 * 
	 * @param content the content of the template.
	 * 
	 * @return A chart that displays a value as a dial.
	 */
	private void defineLinkParameters(SourceBean content, SourceBean serviceRequest) throws Exception {
		logger.debug("IN");
		
		SourceBean drillSB = (SourceBean)content.getAttribute("DRILL");
		String drillLabel="";
		Map tmpDrillParameters= new LinkedHashMap();
		
		if(drillSB!=null){
			String lab=(String)drillSB.getAttribute("document");
			if(lab!=null) drillLabel=lab;
			else{
				logger.info("Drill label not found");
			}

			List parameters =drillSB.getAttributeAsList("PARAM");
			if(parameters!=null){
				for (Iterator iterator = parameters.iterator(); iterator.hasNext();) {
					SourceBean att = (SourceBean) iterator.next();
					String name=(String)att.getAttribute("name");
					String type=(String)att.getAttribute("type");
					String value=replaceParsInString((String)att.getAttribute("value"));

					//looking for the parameter before into the request, then into data parameters.
					//if the value is a dataset value it leaves the tag field $F{...}. The swf file will replace the value.
					if (!value.startsWith("$F{")){
						String reqValue = (String)serviceRequest.getAttribute(name);
					
						if(reqValue == null) {
							if (getDataParameters().get(name)!=null)
								value=(String)getDataParameters().get(name);
						}
						else
							value = reqValue;
					}
					tmpDrillParameters.put(name, value);
				}
			}
		}
		
		//creates the drill url
		int i=0;
		String drillUrl  = "javascript:execCrossNavigation(this.name, '"+drillLabel+"','";

		if (serviceRequest.getAttribute(ObjectsTreeConstants.MODALITY) != null &&
				((String)serviceRequest.getAttribute(ObjectsTreeConstants.MODALITY)).equals(SpagoBIConstants.DOCUMENT_COMPOSITION) )
			drillUrl  = "javascript:parent.execCrossNavigation(this.name, '"+drillLabel+"','";

		for (Iterator iterator = tmpDrillParameters.keySet().iterator(); iterator.hasNext();) {
			String tmpName = (String) iterator.next();			
			String tmpValue=(String)tmpDrillParameters.get(tmpName);
			
			if (i>0)
				drillUrl += "%26";
				
			drillUrl += tmpName + "%3D" + tmpValue;
			i++;
		}
		drillUrl += "');";
		if (i>0)
			getDrillParameters().put("drillUrl", drillUrl);
		
		logger.debug("drillUrl: " + drillUrl);
		
		logger.debug("out");
	}

	/**
	 * set parameters for the drill action.
	 * 
	 * @param content the content of the template.
	 * 
	 * @return A chart that displays a value as a dial.
	 */
	private String replaceParsInString(String strToRep) throws Exception {
		logger.debug("IN");
		
		if (strToRep == null) return "";
		
		String strRet = strToRep;
		
		logger.debug("String to replace: " + strToRep);
		int startIdx = strToRep.indexOf("$P{");
		int endIdx = strToRep.indexOf("}");
		
		if (startIdx > -1 && endIdx > -1){
			String namePar = strToRep.substring(startIdx+3, endIdx);
			String valuePar = (String)getDataParameters().get(namePar);
			if (valuePar != null)
				strRet = strRet.replace("$P{"+namePar+"}", valuePar);
		}
		
		logger.debug("String replaced: " + strRet);
		
		logger.debug("OUT");
		return strRet;
	}
	
	/**
	 * @return the confParameters
	 */
	public Map getConfParameters() {
		return confParameters;
	}

	/**
	 * @param confParameters the confParameters to set
	 */
	public void setConfParameters(Map confParameters) {
		this.confParameters = confParameters;
	}

	/**
	 * @return the dataParameters
	 */
	public Map getDataParameters() {
		return dataParameters;
	}

	/**
	 * @param dataParameters the dataParameters to set
	 */
	public void setDataParameters(Map dataParameters) {
		this.dataParameters = dataParameters;
	}

	/**
	 * @return the drillParameters
	 */
	public Map getDrillParameters() {
		return drillParameters;
	}

	/**
	 * @param drillParameters the drillParameters to set
	 */
	public void setDrillParameters(Map drillParameters) {
		this.drillParameters = drillParameters;
	}
}
