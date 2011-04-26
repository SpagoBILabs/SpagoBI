package it.eng.spagobi.commons.dao;

import it.eng.spago.security.IEngUserProfile;

public interface ISpagoBIDao {
	void setUserProfile(IEngUserProfile profile);
	void setUserID(String user);
	IEngUserProfile getUserProfile();
}
