package it.eng.spagobi.services.common;

import javax.servlet.http.HttpSession;

public abstract class AbstractSsoServiceInterface implements SsoServiceInterface{
	
	@Override
	public void invalidateSession(HttpSession session){
		session.invalidate();
	}
}
