package it.eng.spagobi.engine.mobile.template;

import java.util.List;
import java.util.Vector;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ComposedTemplateInstance implements IMobileTemplateInstance{


	private static transient Logger logger = Logger.getLogger(ComposedTemplateInstance.class);
	
	private SourceBean template;
	private JSONObject title = new JSONObject();
	private JSONObject documents = new JSONObject();
	private JSONObject features = new JSONObject();
	
	public ComposedTemplateInstance(SourceBean template) {
		this.template = template;
	}

	@Override
	public void loadTemplateFeatures() throws Exception {
		buildTitleJSON();
		buildDocumentsJSON();
		setFeatures();
		
	}
	
	public void setFeatures() {
		try {
			features.put("title", title);
			features.put("documents", documents);

		} catch (JSONException e) {
			logger.error("Unable to set features");
		}		 
	}
	private void buildTitleJSON() throws Exception {
		
		SourceBean confSB = null;
		String titleName = null;
		
		logger.debug("IN");
		confSB = (SourceBean)template.getAttribute(MobileConstants.TITLE_TAG);
		if(confSB == null) {
			logger.warn("Cannot find title configuration settings: tag name " + MobileConstants.TITLE_TAG);
		}
		titleName = (String)confSB.getAttribute(MobileConstants.TITLE_VALUE_ATTR);
		String titleStyle = (String)confSB.getAttribute(MobileConstants.TITLE_STYLE_ATTR);
		
		title.put("value", titleName);
		title.put("style", titleStyle);

		logger.debug("OUT");		

	}

	private void buildDocumentsJSON() throws Exception {

		logger.debug("IN");
		List docs = (List)template.getAttributeAsList(MobileConstants.DOCUMENTS_TAG+"."+MobileConstants.DOCUMENT_TAG);
		if(docs == null) {
			logger.warn("Cannot find columns configuration settings: tag name " + MobileConstants.DOCUMENTS_TAG+"."+MobileConstants.DOCUMENT_TAG);
		}else{
			//get total dimensions
			SourceBean docsTag = (SourceBean)template.getAttribute(MobileConstants.DOCUMENTS_TAG);
			String totWidth = (String)docsTag.getAttribute(MobileConstants.DOCUMENTS_WIDTH_ATTR);
			String totHeight = (String)docsTag.getAttribute(MobileConstants.DOCUMENTS_HEIGHT_ATTR);
			documents.put("totWidth", totWidth);
			documents.put("totHeight", totWidth);
			
			
			JSONArray docsArray = new JSONArray();
			for(int i=0; i<docs.size(); i++){
				SourceBean doc = (SourceBean)docs.get(i);
				JSONObject docJSON = new JSONObject();
				String label = (String)doc.getAttribute(MobileConstants.DOCUMENT_LABEL_ATTR);
				docJSON.put("label", label);
				
				String width = (String)doc.getAttribute(MobileConstants.DOCUMENT_WIDTH_ATTR);
				docJSON.put("width", width);
	
				String height = (String)doc.getAttribute(MobileConstants.DOCUMENT_HEIGHT_ATTR);
				docJSON.put("height", height);

				docsArray.put(docJSON);
			}
			documents.put("docs", docsArray);
		}

		logger.debug("OUT");		

	}
	@Override
	public String getDocumentType() {
		// TODO Auto-generated method stub
		return MobileConstants.COMPOSED_TYPE;
	}


	@Override
	public JSONObject getFeatures() {
		return features;
	}

}
