/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPIVOT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.jpivotaddins.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import sun.misc.BASE64Decoder;

import com.thoughtworks.xstream.XStream;

/**
 * @author Andrea Gioia
 *
 */
public class AnalysisBeanHandler {
	private static AnalysisBeanHandler instance;
	
	public static AnalysisBeanHandler getInstance() {
		if(instance == null) instance = new AnalysisBeanHandler();
		return instance;
	}
	
	private AnalysisBeanHandler() { }

	public void getAnalysisBean(HttpSession session, HttpServletRequest request) throws IOException, DocumentException {
		List parameters = null;
		InputStream is = null;
		String reference = null,name = null, nameConnection = null, query= null;
		AnalysisBean analysis = null;
		
		
		SaveAnalysisBean analysisBean = (SaveAnalysisBean) session.getAttribute("save01");
		String nameSubObject = request.getParameter("nameSubObject");
		
		// if into the request is defined the attribute "nameSubObject" the engine must run a subQuery
		if (nameSubObject != null) {
			String jcrPath = (String)session.getAttribute("templatePath");
			String spagoBIBaseUrl = (String)session.getAttribute("spagobiurl");
			String user = (String)session.getAttribute("user");
			// if subObject execution in the request there are the description and visibility
			String descrSO = request.getParameter("descriptionSubObject");
			if(descrSO == null){
				descrSO = "";
			}
			String visSO = request.getParameter("visibilitySubObject");
			if(visSO == null){
				visSO = "Private";
			}
			
			analysisBean.setAnalysisName(nameSubObject);
			analysisBean.setAnalysisDescription(descrSO);
			// the possible values of the visibility are (Private/Public)
			analysisBean.setAnalysisVisibility(visSO);
			// get content from cms
			
			String subobjdata64Coded = request.getParameter("subobjectdata");
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			byte[] subobjBytes = bASE64Decoder.decodeBuffer(subobjdata64Coded);
			is = new java.io.ByteArrayInputStream(subobjBytes);
			InputStreamReader isr = new InputStreamReader(is);
			XStream dataBinder = new XStream();
			try {
				analysis = (AnalysisBean) dataBinder.fromXML(isr, new AnalysisBean());
				isr.close();
				query = analysis.getMdxQuery();
				nameConnection = analysis.getConnectionName();
				reference = analysis.getCatalogUri();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		
		// normal execution (no subObject)	
		} else {
			String templateBase64Coded = request.getParameter("template");
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			byte[] template = bASE64Decoder.decodeBuffer(templateBase64Coded);
			is = new java.io.ByteArrayInputStream(template);
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
		    Document document = reader.read(is);
		    nameConnection = request.getParameter("connectionName");
			query = document.selectSingleNode("//olap/MDXquery").getStringValue();
			Node cube = document.selectSingleNode("//olap/cube");
			reference = cube.valueOf("@reference");
			name = cube.valueOf("@name");
			parameters = document.selectNodes("//olap/MDXquery/parameter");
			analysis = new AnalysisBean();
			analysis.setConnectionName(nameConnection);
			analysis.setCatalogUri(reference);
			session.setAttribute("analysisBean",analysis);
		}
	}

	
}
