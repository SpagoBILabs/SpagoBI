package it.eng.spagobi.utilities.engines;

import it.eng.spago.base.SourceBean;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class EngineTestServlet extends HttpServlet {
	/**
     * Logger component
     */
    private static transient Logger logger = Logger.getLogger(EngineTestServlet.class);
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    	
    	logger.debug("IN");

    		String message = "sbi.connTestOk";

			response.getOutputStream().write(message.getBytes());
	    	response.getOutputStream().flush();
	    	logger.debug("OUT");  		
    }    
}
