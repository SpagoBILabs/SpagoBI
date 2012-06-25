/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.authentication.handler;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spagobi.authentication.utility.AuthenticationUtility;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;



/**
 * @author Giachino (antonella.giachino@eng.it)
 **/

/**
 * Authenticates where the presented password is valid. 
 */
public class BaseAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	protected static Logger logger = Logger.getLogger(BaseAuthenticationHandler.class);
	
    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) 
    	throws AuthenticationException {
        logger.debug("IN");

        
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        
        logger.debug("user  "+username);
        logger.debug("psw  "+password);
        
        String correctPassword = null;
        String encrPass = null;
        logger.debug("Start validating password for the user " + username);
        List lstResult = null;
        //define query to get pwd from database
        try{
        	encrPass = Password.encriptPassword(password);
        	AuthenticationUtility utility = new AuthenticationUtility();
        	List pars = new LinkedList();
        	// CASE INSENSITVE SEARCH ON USER ID
        	pars.add(username.toUpperCase());
        	lstResult = utility.executeQuery("SELECT PASSWORD FROM SBI_USER WHERE UPPER(USER_ID) = ?", pars);
        }catch(Exception e){
        	logger.error("Error while check pwd: " + e);
        	e.printStackTrace();
        }
        
      //gets the pwd presents in db
		Iterator iter_sb_pwd = lstResult.iterator();
		while(iter_sb_pwd.hasNext()) {
			SourceBeanAttribute tmp_attributeSB = (SourceBeanAttribute)iter_sb_pwd.next();
			SourceBean attributeSB = (SourceBean)tmp_attributeSB.getValue();
			String attribute = (String)attributeSB.getAttribute("PASSWORD");
			correctPassword = attribute;
		}
        
        logger.debug("OUT");
        return encrPass.equals(correctPassword);
    }
    
    

}