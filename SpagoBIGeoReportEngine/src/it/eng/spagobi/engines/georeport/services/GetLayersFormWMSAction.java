/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.georeport.services;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.spagobi.utilities.engines.BaseServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.service.AbstractBaseServlet;


/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class GetLayersFormWMSAction extends AbstractBaseServlet {
	
	public static final String WMS_URL = "urlWms";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GetLayersFormWMSAction.class);
    
    
	public void doService( BaseServletIOManager servletIOManager ) throws SpagoBIEngineException {
		 
		String wmsUrl;
		
		logger.debug("IN");
		
		try {
			
			wmsUrl = servletIOManager.getParameterAsString(WMS_URL);
			logger.debug("Parameter [" + WMS_URL + "] is equal to [" + wmsUrl + "]");
			
			wmsUrl = wmsUrl  + "?"+ "request=getCapabilities";
			String resultStr = "";	
			JSONArray results;
			
			URL url = new URL(wmsUrl);
			URLConnection conn = url.openConnection ();
			  
			// DOM way:
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("Layer");
			  
			int n = (nodeLst.getLength());
			
			results = new JSONArray();
			
			for (int s = 0; s < n; s++) {
			  
				Node fstNode = nodeLst.item(s);

			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    
			      Element fstElmnt = (Element) fstNode;
			           
			      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("Name");
			      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);            
			      NodeList fstNm = fstNmElmnt.getChildNodes();
			           
			      NodeList srsNmElmntLst = fstElmnt.getElementsByTagName("SRS");
			      Element srsNmElmnt = (Element) srsNmElmntLst.item(0);     
			      NodeList srsNm = srsNmElmnt.getChildNodes();
			      
			      /*
			      NodeList titleNmElmntLst = fstElmnt.getElementsByTagName("Title");
			      Element titleNmElmnt = (Element) titleNmElmntLst.item(0);     
			      NodeList titleNm = titleNmElmnt.getChildNodes();
			      */

			      JSONObject layerJSON = new JSONObject();
			      String layername = ((Node) fstNm.item(0)).getNodeValue();
			      String srs = ((Node) srsNm.item(0)).getNodeValue();
			      layerJSON.put("id", s);
			      layerJSON.put("layername", layername);
			      layerJSON.put("srs", srs);
			     
			      results.put(layerJSON);
			      			      
			      /*
			      if(s == n-1) {
			    	  resultStr = "{id:"+'\"'+s+'\"'+", layername:"+'\"'+((Node) fstNm.item(0)).getNodeValue()+'\"'+", srs:"+'\"'+((Node) srsNm.item(0)).getNodeValue()+'\"'+"}]" ;
			      } else if (s == 1) {
			    	  resultStr = "[{id:"+'\"'+s+'\"'+", layername:"+'\"'+((Node) fstNm.item(0)).getNodeValue()+'\"'+", srs:"+'\"'+((Node) srsNm.item(0)).getNodeValue()+'\"'+"}"+ ",";
			      } else {
			    	  resultStr = "{id:"+'\"'+s+'\"'+", layername:"+'\"'+((Node) fstNm.item(0)).getNodeValue()+'\"'+", srs:"+'\"'+((Node) srsNm.item(0)).getNodeValue()+'\"'+"}"+ ",";
			      } 
			      */           
	
			    }
			    
			}
			
			servletIOManager.tryToWriteBackToClient(results.toString());
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			logger.debug("OUT");
		}
	}

	public void handleException(BaseServletIOManager servletIOManager,
			Throwable t) {
		t.printStackTrace();		
	}

}
