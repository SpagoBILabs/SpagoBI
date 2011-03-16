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
package it.eng.spagobi.engines.qbe.template;

import it.eng.qbe.model.accessmodality.IModelAccessModality;
import it.eng.spagobi.engines.qbe.externalservices.ExternalServiceConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * The Class QbeTemplate.
 * 
 * @author Andrea Gioia
 */
public class QbeTemplate {
	private boolean composite;	
	private Map dblinkMap;	
	private List datamartNames;	
	private IModelAccessModality datamartModelAccessModality;
	private String dialect;
	
	private Map properties;
	
	private Object rawData;	
	
	private List<ExternalServiceConfiguration> externalServicesConfiguration;
		
	public QbeTemplate() {
		datamartNames = new ArrayList();
		dblinkMap = new HashMap();
		properties = new HashMap();
		externalServicesConfiguration = new ArrayList();
	}
	
	public void addDatamartName(String name) {
		datamartNames.add(name);
	}
	
	public List getDatamartNames() {
		return datamartNames;
	}
	
	public void addExternalServiceConfiguration(ExternalServiceConfiguration c) {
		externalServicesConfiguration.add(c);
	}
	
	public List<ExternalServiceConfiguration> getExternalServiceConfigurations() {
		return externalServicesConfiguration;
	}
	
	public JSONArray getExternalServiceConfigurationsAsJSONArray() throws JSONException {
		JSONArray toReturn = new JSONArray();
		Iterator<ExternalServiceConfiguration> it = externalServicesConfiguration.iterator();
		while (it.hasNext()) {
			ExternalServiceConfiguration aServiceConfig = it.next();
			JSONObject obj = new JSONObject();
			obj.put("id", aServiceConfig.getId());
			obj.put("description", aServiceConfig.getDescription());
			toReturn.put(obj);
		}
		return toReturn;
	}
	
	public void setDbLink(String datamartName, String dblink) {
		dblinkMap.put(datamartName, dblink);
	}
	
	public Map getDbLinkMap() {
		return dblinkMap;
	}
	
	public void setDatamartModelAccessModality(QbeXMLModelAccessModality datamartModelAccessModality) {
		this.datamartModelAccessModality = datamartModelAccessModality;
	}
	
	public IModelAccessModality getDatamartModelAccessModality() {
		return datamartModelAccessModality;
	}
	
	public boolean isComposite() {
		return composite;
	}

	public void setComposite(boolean composite) {
		this.composite = composite;
	}
	
	public void setProperty(String pName, Object pValue) {
		properties.put(pName, pValue);
	}
	
	public Object getProperty(String pName) {
		return properties.get(pName);
	}

}
