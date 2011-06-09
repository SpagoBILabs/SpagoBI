/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
        	pars.add(username);
        	lstResult = utility.executeQuery("SELECT PASSWORD FROM SBI_USER WHERE USER_ID = ?", pars);
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