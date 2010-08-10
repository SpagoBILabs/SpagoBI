/**
 * 
 */
package it.eng.spagobi.engines.drivers;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.services.common.SsoServiceInterface;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class AbstractDriver {

    static Logger logger = Logger.getLogger(AbstractDriver.class);
    /**
     * 
     */
    public AbstractDriver() {
	super();
    }

    /**
     * Applys changes for security reason if necessary
     * 
     * @param pars
     *                The map of parameters
     * @return The map of parameters to send to the engine
     */
    protected Map applySecurity(Map pars, IEngUserProfile profile) {
        logger.debug("IN");
        ConfigSingleton config = ConfigSingleton.getInstance();
        SourceBean configSB = (SourceBean) config.getAttribute("SPAGOBI_SSO.ACTIVE");
		String active = (String) configSB.getCharacters();
		String userId=(String)profile.getUserUniqueIdentifier();
		if (active != null && active.equalsIgnoreCase("true") && !((UserProfile)profile).isSchedulerUser(userId)){
		    logger.debug("I don't put the UserId information in the URL");
		}else {
		    if (((UserProfile) profile).getUserUniqueIdentifier() != null) {
			pars.put(SsoServiceInterface.USER_ID, ((UserProfile) profile).getUserUniqueIdentifier()); 
		    }
		}
	    
        
        logger.debug("Add parameter: "+SsoServiceInterface.USER_ID+" / " + ((UserProfile) profile).getUserUniqueIdentifier());
        logger.debug("OUT");
        return pars;
    }

}