package it.eng.spagobi.util.spagobi;

import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.proxy.ContentServiceProxy;

import javax.servlet.http.HttpSession;

import org.palo.viewapi.View;

import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;

import sun.misc.BASE64Decoder;

public class JPaloSavingUtil {
	
	private static final BASE64Decoder DECODER = new BASE64Decoder();
	
	public String saveSubobjectForJPalo (HttpSession session,  String name, String descr, String xml){
		String documentId=(String)session.getAttribute("spagobidocument");
		String profile = (String) session.getAttribute("spagobiuser");
		ContentServiceProxy contentProxy = new ContentServiceProxy(profile, session);
		String result = null;
	    try {
            result = contentProxy.saveSubObject(documentId, 
            										 name,
            										 descr,             									
            										 "true", 
            										 xml);		
	        session.setAttribute("saveSubObjectId", getSubobjectId(result));
	        
	        
	    } catch (Exception gse) {		
	    	gse.printStackTrace();
	    }
	    return result; 
	}
	
	public String getSubobjectForJPalo (HttpSession session,  String name){
		String xml = null;
		String profile = (String) session.getAttribute("spagobiuser");
		String subobjId=(String)session.getAttribute("spagobisubobj");
		ContentServiceProxy contentProxy = new ContentServiceProxy(profile, session);
		
	    try {
            Content content = contentProxy.readSubObjectContent(subobjId);
            if(content != null){            	
            	xml = new String(DECODER.decodeBuffer( content.getContent() ));
            }
    		//The set definition is made in CubeViewController. Here we set session attribute
    		session.setAttribute("spagobi_state", xml);
    		return xml;

	    } catch (Exception gse) {		
	    	gse.printStackTrace();
	    	return null;
	    }

	}
	public void saveTemplateForJPalo (HttpSession session,  View view){
		String documentId=(String)session.getAttribute("spagobidocument");
		String profile = (String) session.getAttribute("spagobiuser");
		ContentServiceProxy contentProxy = new ContentServiceProxy(profile, session);
		String template = null;
	    try {
	    	template = writeTemplate(view);
	    	//System.out.println(template);
            String result= contentProxy.saveObjectTemplate(documentId, 
            										 view.getName(),
            										 template);	
            //System.out.println(result);
	        

	    } catch (Exception gse) {		
	    	gse.printStackTrace();
	    } 
	}
	
	private String writeTemplate(View view){
		StringBuffer template = new StringBuffer();
    	String viewName = view.getName();
    	String connectionName = view.getAccount().getConnection().getName();
    	String accountLogin = view.getAccount().getLoginName();
    	String cubeName ="";
    	if(view.getCubeView() != null){
    		cubeName = view.getCubeView().getCube().getName();
    	}
    	template.append("<olap ");
    	template.append("connection=\"");
    	template.append(connectionName);
    	template.append("\" ");
    	template.append("account=\"");
    	template.append(accountLogin);
    	template.append("\" ");
    	template.append("view=\"");
    	template.append(viewName);
    	template.append("\" ");
    	template.append("cube=\"");
    	template.append(cubeName);
    	template.append("\" >");
    	template.append("</olap>");
    	
    	return template.toString();
	
	}
	
	private String getSubobjectId(String id){
		String onlyId = id;
		if(id != null && id.indexOf("OK - ") != -1){
			onlyId = id.substring("OK - ".length());
		}
		return onlyId;
	}
}
