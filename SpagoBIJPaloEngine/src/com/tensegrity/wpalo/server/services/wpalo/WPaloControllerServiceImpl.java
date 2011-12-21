/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.server.services.wpalo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.BasePaloServiceServlet;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewController;
import com.tensegrity.wpalo.client.exceptions.AuthenticationFailedException;
import com.tensegrity.wpalo.client.exceptions.WPaloException;
import com.tensegrity.wpalo.client.services.wpalo.WPaloControllerService;
import com.tensegrity.wpalo.server.dbconnection.HSqlDbConnection;
import com.tensegrity.wpalo.server.dbconnection.MySqlDbConnection;

/**
 * <code>WPaloControllerServiceImpl</code> TODO DOCUMENT ME
 * 
 * @version $Id: WPaloControllerServiceImpl.java,v 1.6 2009/06/16 13:46:08
 *          ArndHouben Exp $
 **/
public class WPaloControllerServiceImpl extends BasePaloServiceServlet
		implements WPaloControllerService {
	/* manages applications life cycle */
	static private Logger logger = Logger.getLogger(WPaloControllerServiceImpl.class);
	/** generated serial number */
	private static final long serialVersionUID = -6287168959908304515L;

	public static boolean USE_MYSQL = false;

	private DbConnection dbConnection;
	private final SessionListener sessionListener;
	private volatile boolean initialised = false;
	
	public WPaloControllerServiceImpl() {
		sessionListener = new SessionListener(this);
	}

	public final boolean isRunning() {
		return initialised;
	}

	public final void start() throws WPaloException {
		if (!initialised) {
			initialised = true;
			try {
				// read in connection properties:
				if (ServiceProvider.getDbConnection() == null) {
					dbConnection = createConnection();				
					ServiceProvider.initialize(dbConnection, true);
				} else {
					dbConnection = ServiceProvider.getDbConnection();
				}
			} catch (Throwable e) {
				logger.error(e.getMessage());
//				initialised = false;
//				throw new WPaloException("Couldn't start the application!!", e);
			}
		}
	}

	public final void stop(String sessionId) throws WPaloException {
	}
	
	final void stopIgnoreException(UserSession userSession) {
		try {
			stop(userSession);
		} catch (WPaloException e) {
			e.printStackTrace();
		}
	}

	private final void stop(UserSession userSession) throws WPaloException {
		if (initialised) {
			initialised = false;
			try {
				logout(userSession.getUser());
//				ServiceProvider.release(dbConnection);
			} catch (RuntimeException e) {
				logger.error(e.getMessage());
				initialised = true;
			}
		}
	}

	private final void setLocale(String sessionId, String locale) {
		try {
			getUserSession(sessionId).setLocale(locale);
		} catch (SessionExpiredException e) {
			logger.error(e.getMessage());
		}
	}
	
	private final void configureNumberFormat(String sessionId, AuthUser usr) {
		Locale locale = getThreadLocalRequest().getLocale();
		
		NumberFormat format = NumberFormat.getInstance(locale);
		setNumberFormat(sessionId, usr, format);
		
//		String number = format.format(1.1);
//		result.setFloatSeparator(number.charAt(1));
//
//		number = format.format(1000);
//	    char c = number.charAt(1);
//		result.setDecimalSeparator(c + "");		
	}
	
	public final XUser login(String login, String password, String locale)
			throws AuthenticationFailedException {		
		// try to authenticate:
		try {
			AuthUser usr = ServiceProvider.getAuthenticationService().authenticate(login, password);
			//AuthUser usr = ServiceProvider.getAuthenticationService().authenticate("admin", "admin");
			registerListeners();
			UserSession userSession = setLoggedInUser(usr);
			configureNumberFormat(userSession.getSessionId(), usr);			
			setLocale(userSession.getSessionId(), locale);
			
//			try {
//				UserSession userSession = getUserSession();
////				CubeViewController.removeAllViewsAndShutdownConnectionPool(userSession);
//			} catch (Throwable t) {
//				t.printStackTrace();
//			}
			XUser user = (XUser) XConverter.createX(usr); 
			user.setSessionId(userSession.getSessionId());
			return user; 
		} catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new AuthenticationFailedException(e.getMessage(), e);
		}
	}

	public final XUser loginHash(String login, String password, String locale)
			throws AuthenticationFailedException {
		// try to authenticate:
		try {

			AuthUser usr = ServiceProvider.getAuthenticationService()
					.authenticateHash(login, password);

			registerListeners();
			UserSession userSession = setLoggedInUser(usr);
			configureNumberFormat(userSession.getSessionId(), usr);
			setLocale(userSession.getSessionId(), locale);
			
//			try {
//				UserSession userSession = getUserSession();
//				// CubeViewController.removeAllViewsAndShutdownConnectionPool(userSession);
//			} catch (Throwable t) {
//				t.printStackTrace();
//			}

			XUser user = (XUser) XConverter.createX(usr); 
			user.setSessionId(userSession.getSessionId());
			return user; 
		} catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new AuthenticationFailedException(e.getMessage(), e);
		}
	}

	private final void registerListeners() {
		register(sessionListener);
	}

	public void logout(String sessionId) {
		try {
			UserSession userSession = getUserSession(sessionId);
			try {
				CubeViewController.removeAllViewsAndShutdownConnectionPool(userSession);
			} catch (Throwable t) {
			}
			AuthUser user = getLoggedInUser(sessionId);			
			logout(user);
			terminateSession();
			try {
				CubeViewController.removeAllViewsAndShutdownConnectionPool(userSession);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
			stopIgnoreException(userSession);
		} catch (SessionExpiredException ex) {
			// already logged out...
			logger.error(ex.getMessage(), ex);
		} catch (RuntimeException ex) {
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}		
	}
	
	void logout(AuthUser user) {
		closeOpenConnections(user);
		unregisterListeners();
		invalidateSession();
	}

	private final void closeOpenConnections(AuthUser user) {
		List<Account> accounts = user.getAccounts();
		for (Account account : accounts) {
			try {
				if (account.isLoggedIn())
					account.logout();
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	private final void unregisterListeners() {
		// unregister(sessionListener);
	}

	private final void invalidateSession() {
		try {
			HttpSession session = getSession();
			session.invalidate();
		} catch (Exception ex) {
		}
	}
	
	protected void initDbConnection(ServletContext globalContext) {
		// HERE WE CREATE GLOBAL CONNECTIONs AND/OR GLOBAL CONNECTION POOLS
		try {
			DbConnection sqlConnection = (DbConnection) globalContext
					.getAttribute(SQL_CONNECTION);
			if (sqlConnection == null && ServiceProvider.getDbConnection() == null) {
				dbConnection = createConnection();
				ServiceProvider.initialize(dbConnection, true);
				initialised = true;
				globalContext.setAttribute(SQL_CONNECTION, dbConnection);
			} else {
				if (sqlConnection == null) {
					sqlConnection = ServiceProvider.getDbConnection();
					globalContext.setAttribute(SQL_CONNECTION, dbConnection);
				}
				dbConnection = sqlConnection;
				initialised = true;				
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			// TODO introduce an exception here...
			// throw new WPaloException("Couldn't start the application!!", e);
		}
	}

	private final DbConnection createConnection() {
		try{
			String useMySql = ResourceBundle.getBundle("deploy", Locale.ITALIAN).getString("use.mysql");
			if(useMySql != null){				
				USE_MYSQL = Boolean.valueOf(ResourceBundle.getBundle("deploy", Locale.ITALIAN).getString("use.mysql"));
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		if (USE_MYSQL)
			return MySqlDbConnection.newInstance();
		else
			return HSqlDbConnection.newInstance();
	}
}