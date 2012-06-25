/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.authentication.flow;

/**
 * Interface for a class that fetches an account status.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public interface AccountStatusGetter {
    /**
     * @param userID The unique ID of the user
     * @return Code for this status
     */
    public int getStatus(String userID);
    
}
