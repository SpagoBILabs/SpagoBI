/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
