/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.authentication.flow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * Abstract class providing common functionality for getting an account status.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public abstract class AbstractAccountStatusGetter implements AccountStatusGetter, InitializingBean {

    /** Instance of logging for subclasses. */
    protected Log logger = LogFactory.getLog(this.getClass());

    public static final int STATUS_ACTIVATE = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_CHANGEPWD = 3;
    public static final int STATUS_LOCKED = 5;
    public static final int STATUS_BLOCKED = 7;
    public static final int STATUS_ERROR = 9;
    
}
