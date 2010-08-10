/**
 * 
 */
package it.eng.spagobi.chiron;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;


/**
 * @author Andtra Gioia (andrea.gioia@eng.it)
 *
 */
public class SaveEngineAction extends AbstractBaseHttpAction{
	
	// logger component
	private static Logger logger = Logger.getLogger(SaveEngineAction.class);
	
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		
		
		logger.debug("IN");
		
		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			JSONObject engineJSON = this.getAttributeAsJSONObject("ENGINE");
			logger.debug(engineJSON);
		} catch (Throwable t) {
			throw new SpagoBIException("An unexpected error occured while executing SAVE_ENGINES_ACTION", t);
		} finally {
			logger.debug("OUT");
		}
	}

}
