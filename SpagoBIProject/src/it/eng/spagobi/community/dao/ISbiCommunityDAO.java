/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.community.mapping.SbiCommunity;

import java.util.List;

public interface ISbiCommunityDAO extends ISpagoBIDao {
	
	public SbiCommunity loadSbiCommunityByName(String name) throws EMFUserError;

	public void saveSbiComunityUsers(SbiCommunity community, String userID) throws EMFUserError;
	
	public List<SbiCommunity> loadSbiCommunityByUser(String userID) throws EMFUserError;
	
	public List<SbiCommunity> loadSbiCommunityByOwner(String userID) throws EMFUserError;
	
	public void addCommunityMember(SbiCommunity community, String userID) throws EMFUserError;
}
