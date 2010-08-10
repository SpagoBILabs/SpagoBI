package it.eng.spagobi.engines.config.service;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.tools.datasource.service.TestConnectionAction;

public class TestInternalEngineAction extends AbstractHttpAction{
	
	static private Logger logger = Logger.getLogger(TestInternalEngineAction.class);
	
	public void service(SourceBean serviceRequest, SourceBean serviceResponse)
	throws Exception {
		
		logger.debug("IN");
		String message = null;
		freezeHttpResponse();
		HttpServletResponse httResponse = getHttpResponse();

    	String driverName = (String)serviceRequest.getAttribute("driverName");	
		String className = (String) serviceRequest.getAttribute("className");
		
		try {
			if(driverName!=null){
				Class.forName(driverName);
			}else if(className!=null){
				Class.forName(className);
			}else{
				message = "ClassNameError";
			}
			message = "sbi.connTestOk";
			
		} catch (ClassNotFoundException e) {
			 message = "ClassNameError";
			e.printStackTrace();
		}
		finally {
			httResponse.getOutputStream().write(message.getBytes());
			httResponse.getOutputStream().flush();
	    	logger.debug("OUT");  	
		}  	
	}

}
