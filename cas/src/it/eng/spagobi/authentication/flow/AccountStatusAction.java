/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.authentication.flow;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This action checks the status of the account of the current user.
 * It will block further access when the account has been blocked.
 * It will display the appropriate warnings to the user to change the password.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public final class AccountStatusAction extends AbstractAction implements InitializingBean {
	
	protected static Logger logger = Logger.getLogger(AccountStatusAction.class);

    public static final String STATUS_ACTIVATE = "activate";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_CHANGEPWD = "changepwd";
    public static final String STATUS_LOCKED = "locked";
    public static final String STATUS_BLOCKED = "blocked";
    public static final String STATUS_ERROR = "error";

    private AccountStatusGetter accountStatusGetter;
    
    
    public final AccountStatusGetter getAccountStatusGetter() {
        return this.accountStatusGetter;
    }

    
    public final void setAccountStatusGetter(final AccountStatusGetter accountStatusGetter) {
        this.accountStatusGetter = accountStatusGetter;
    }

    protected Event doExecute(final RequestContext context) throws Exception {
        logger.debug("checking account status--");
        int status = AbstractAccountStatusGetter.STATUS_ERROR;
        String resultString = STATUS_ERROR;

        String userID = context.getExternalContext().getRequestParameterMap().get("username");
        logger.debug("userID='" + userID + "'");
        
        
        status = this.accountStatusGetter.getStatus(userID);
        logger.debug("translating return code status='" + status + "'");
        switch(status) {
            case AbstractAccountStatusGetter.STATUS_ACTIVATE:
                resultString = STATUS_ACTIVATE;
                logger.info("'" + userID + "' needs to activate");
                break;
            case AbstractAccountStatusGetter.STATUS_ACTIVE:
                resultString = STATUS_ACTIVE;
                logger.debug("'" + userID + "' account is active");
                break;
            case AbstractAccountStatusGetter.STATUS_CHANGEPWD:
                resultString = STATUS_CHANGEPWD;
                logger.info("'" + userID + "' needs to change password");
                break;
            case AbstractAccountStatusGetter.STATUS_LOCKED:
                resultString = STATUS_LOCKED;
                this.logger.info("'" + userID + "' account is locked");
                break;
            case AbstractAccountStatusGetter.STATUS_BLOCKED:
                resultString = STATUS_BLOCKED;
                logger.info("'" + userID + "' account is blocked");
                break;
            case AbstractAccountStatusGetter.STATUS_ERROR:
                resultString = STATUS_ERROR;
                logger.warn("'ERROR' was returned for '" + userID + "'");
                break;
        }
        logger.debug("--checking account status; result='" + resultString + "'");
        return result(resultString);
        
    }

    protected void initAction() throws Exception {
        Assert.notNull(this.accountStatusGetter, "accountStatusGetter cannot be null");
        logger.info("inited with accountStatusGetter='"
            + this.accountStatusGetter.getClass().getName() + "'");
    }

}
