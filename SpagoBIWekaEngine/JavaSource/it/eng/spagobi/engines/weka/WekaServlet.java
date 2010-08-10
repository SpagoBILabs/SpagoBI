/**
 *
 *	LICENSE: see COPYING file
 *
**/
package it.eng.spagobi.engines.weka;

import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Process weka execution requests and returns bytes of the filled
 * reports
 */
public class WekaServlet extends AbstractEngineStartServlet {
	
	/**
	 * Logger component
	 */
	private static transient Logger logger = Logger.getLogger(WekaServlet.class);

	/**
	 * Input parameters map
	 */
	private Map params = null;
	
	public static final String TALEND_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendRolesHandler";
	public static final String TALEND_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendEventPresentationHandler";
	public static final String START_EVENT_ID = "startEventId";
	public static final String BIOBJECT_ID = "biobjectId";
	public static final String EVENTS_MANAGER_URL = "events_manager_url";
	public static final String EVENT_TYPE = "event-type";
	public static final String DOCUMENT_EXECUTION_START = "biobj-start-execution";
	public static final String DOCUMENT_EXECUTION_END = "biobj-end-execution";
	public static final String PROCESS_ACTIVATED_MSG = "processActivatedMsg";
	public static final String PROCESS_NOT_ACTIVATED_MSG = "processNotActivatedMsg";
	
	protected AuditAccessUtils auditAccessUtils;
	
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		logger.debug("IN");
		WekaEngineInstance engineInstance = null;
		
		Map env = servletIOManager.getEnv();
		String template = servletIOManager.getTemplateAsString();
				
		String message = null;
				
		try {
			
			engineInstance = WekaEngine.createInstance(template, env);
			logger.debug("Engine Instance Created");
			engineInstance.start();
			logger.debug("Engine Started");
			message = servletIOManager.getLocalizedMessage("weka.correct.execution");
			//message = (String) params.get(PROCESS_ACTIVATED_MSG);
			logger.info(":service: Return the default waiting message");
			/*Map startEventParams = new HashMap();
			startEventParams.put(EVENT_TYPE, DOCUMENT_EXECUTION_START);
			startEventParams.put(BIOBJECT_ID, _parameters.get(EngineConstants.ENV_DOCUMENT_ID));

			Integer startEventId = null;
			EventServiceProxy eventServiceProxy = (EventServiceProxy)_parameters.get( EngineConstants.ENV_EVENT_SERVICE_PROXY);
			

			try {

			    String startEventParamsStr = getParamsStr(startEventParams);

			    eventServiceProxy.fireEvent(startExecutionEventDescription + parametersList, startEventParamsStr,
				    TALEND_ROLES_HANDLER_CLASS_NAME, TALEND_PRESENTAION_HANDLER_CLASS_NAME);
			    logger.debug("Start Fire Event");

			} catch (Exception e) {
			    logger.error("problems while registering the start process event", e);
			}*/

		} catch (Exception e) {
			logger.error(":service: error while process startup", e);
			//message = (String) params.get(PROCESS_NOT_ACTIVATED_MSG);
			message = servletIOManager.getLocalizedMessage("an.unpredicted.error.occured");
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("<html>\n");
		buffer.append("<head><title>Service Response</title></head>\n");
		buffer.append("<body>");
		buffer
				.append("<p style=\"text-align:center;font-size:13pt;font-weight:bold;color:#000033;\">");
		buffer.append(message);
		buffer.append("</p>");
		buffer.append("</body>\n");
		buffer.append("</html>\n");

		servletIOManager.getResponse().setContentLength(buffer.length());
		servletIOManager.getResponse().setContentType("text/html");
		PrintWriter writer;
		try {
			writer = servletIOManager.getResponse().getWriter();
			writer.print(buffer.toString());
			writer.flush();
		} catch (IOException e) {
			throw new SpagoBIRuntimeException("Impossible to write back response to client", e);
		}
		

		logger.info("service:Request processed");
	}

	
	/**
	 * @param params
	 * @param parName
	 * @param parValue
	 */
	private void addParToParMap(Map params, String parName, String parValue) {
		logger.debug("IN");
		String newParValue;
		
		ParametersDecoder decoder = new ParametersDecoder();
		if(decoder.isMultiValues(parValue)) {			
			List values = decoder.decode(parValue);
			newParValue = "";
			newParValue = (String)values.get(0);
			
		} else {
			newParValue = parValue;
		}
		
		params.put(parName, newParValue);
		logger.debug("OUT");
	}



	
	
	


}
