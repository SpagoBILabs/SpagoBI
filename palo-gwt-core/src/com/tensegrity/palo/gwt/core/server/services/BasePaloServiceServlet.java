/*
*
* @file BasePaloServiceServlet.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: BasePaloServiceServlet.java,v 1.27 2010/04/13 09:45:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DbConnection;
import org.palo.viewapi.services.ServiceProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.server.services.cubeview.CubeViewController;


/**
 * <code>CorePaloService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: BasePaloServiceServlet.java,v 1.27 2010/04/13 09:45:58 PhilippBouillon Exp $
 **/
public abstract class BasePaloServiceServlet extends RemoteServiceServlet {
//MAIN SERVLET. PROVIDES PER SESSION DATA AND GLOBAL CONTEXT DATA, LIKE CONNECTION POOLS
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	/** generated default serial id */
	private static final long serialVersionUID = 5835337002347127643L;

	//SESSION DATA ATTRIBUTES:
	static final String SESSION_USER = BasePaloServiceServlet.class.getName() + "authuser";
	//GLOBAL DATA ATTRIBUTES:
	protected static final String SQL_CONNECTION = BasePaloServiceServlet.class.getName() + "sqlconnection";
	
	private ISessionListener sessionListener;
	private static final HashMap <String, String> userBinding =
		new HashMap<String, String>();
	
	private static final HashMap <String, Long> lastAccessTime =
		new HashMap<String, Long>();
	
	private static final HashMap <String, Integer> maxInactiveTime =
		new HashMap<String, Integer>();
	
	private static final HashMap <String, UserSession> sessionMap =
		new HashMap<String, UserSession>();
	
	public void init(ServletConfig config) throws ServletException {

		try{
			super.init(config);
			initDbConnection(config.getServletContext());
		}catch(Exception e){
			e.printStackTrace();
			if(e instanceof ServletException){
				throw (ServletException)e;
			}
		}

	}

	public final void register(ISessionListener listener) {
		this.sessionListener = listener;
//		SessionListener sessionListener = new SessionListener(listener);
//		sessionListener.registerTo(getSession());
	}
	
//	public final void unregister(HttpSessionListener listener) {
//		SessionListener sessionListener = new SessionListener(listener);
//		sessionListener.unregisterFrom(getSession());		
//	}
	
	private final synchronized void checkUserSessionTimeout(String sessionId, HttpSession session, UserSession userSession) throws SessionExpiredException {
		if (!lastAccessTime.containsKey(sessionId)) {
			lastAccessTime.put(sessionId, System.currentTimeMillis());
		}
		if (!maxInactiveTime.containsKey(sessionId)) {
			maxInactiveTime.put(sessionId, session.getMaxInactiveInterval());
//			System.err.println("Setting session timeout for session [" + sessionId + "]: " + session.getMaxInactiveInterval() + "s.");
		}
		int maxInactive = maxInactiveTime.get(sessionId);
		long deltaTime = (System.currentTimeMillis() - lastAccessTime.get(sessionId)) / 1000;
		if (deltaTime > maxInactive) {
			System.err.println("Session [" + sessionId + "] timed out after " + deltaTime + " inactive seconds. (Timeout was: " + maxInactive + "s)");
			CubeViewController.removeAllViews(userSession);
			if (sessionListener != null) {
				sessionListener.sessionEnded(userSession);
			}
			// TODO maybe not (yet) remove here?!
			sessionMap.remove(sessionId);
			throw new SessionExpiredException("Session expired!");
		}		
	}
	
	public final synchronized UserSession getUserSession(String id) throws SessionExpiredException {
		UserSession userSession = sessionMap.get(id);
		if (userSession == null) {
			throw new SessionExpiredException("Session " + id + " has expired.");
		}
		HttpSession session = getSession();
		checkUserSessionTimeout(id, session, userSession);
		lastAccessTime.put(id, System.currentTimeMillis());
		return userSession;
	}
		
//	public final synchronized UserSession getUserSession() throws SessionExpiredException {
//		try {
//		HttpSession session = getSession();
//		synchronized (session) {
//			UserBinding userBinding = (UserBinding)session.getAttribute(SESSION_USER);
//			if(userBinding == null) {
////				System.err.println("Browser session expired, re-authenticating user.");
//				Object [] result = new Object [] {null, null};
//				try {
//					result = getAuthUserAndIdFromCookies();
//				} catch (Throwable t) {					
//				}
//				AuthUser user = (AuthUser) result[0];
//				String sessionId = (String) result[1];
//				if (user != null) {
//					UserSession userSession = new UserSession(user, sessionId);
//					checkUserSessionTimeout(session, userSession);
//					bindUserToSession(userSession, session);
//					updateCookies(user.getLoginName(), user.getPassword(), sessionId);
//				}
//				userBinding = (UserBinding)session.getAttribute(SESSION_USER);		
//				if (userBinding == null) {					
//					throw new SessionExpiredException("Session expired!");
//				}
//			}
//			checkUserSessionTimeout(session, userBinding.getUserSession());
//			validateCookie(userBinding.getUserSession().getUser(), userBinding.getUserSession().getId());
//			lastAccessTime.put(userBinding.getUserSession().getId(), System.currentTimeMillis());
//			return userBinding.getUserSession();
//		}
//		} catch (Throwable t) {
//			throw new SessionExpiredException("Session expired!");
//		}
//		
//	}
	
	public final AuthUser getLoggedInUser(String sessionId) throws SessionExpiredException {
		UserSession userSession = getUserSession(sessionId);		
		return userSession.getUser();
	}
	protected final void overrideLoggedInUser(String sessionId, AuthUser user) throws SessionExpiredException {
		UserSession userSession = getUserSession(sessionId);		
		userSession.setUser(user); 
	}
	public final NumberFormat getNumberFormat(String sessionId) throws SessionExpiredException {
		UserSession userSession = getUserSession(sessionId);
		return userSession.getNumberFormat();		
	}
	protected final UserSession setLoggedInUser(AuthUser user) {
		HttpSession session = startNewSession();
		UserSession userSession = new UserSession(user);
		updateCookies(user.getLoginName(), "", userSession.getSessionId());
//		session.setMaxInactiveInterval(30);
//		System.err.println("!!!IMPORTANT -- REMOVE BEFORE BUILD (BasePaloServiceServlet) Session Timeout: " + session.getMaxInactiveInterval() + "s");
		bindUserToSession(userSession, session);
		sessionMap.put(userSession.getSessionId(), userSession);
//		displayCookie("setLoggedInUser");
		return userSession;
	}
	
	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());
	}
	
	public final void terminateSession() {
		String loginName = getLoginNameFromCookies();
		try {
			updateCookies("", "", "");
		} catch(Throwable t) {			
		}
		if (loginName != null) {
			userBinding.remove(loginName);
		}
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession(false); 
		if (session == null) {
			System.err.println("[palo pivot - " + now() + "] session destroyed.");
		} else {
			session.invalidate();
		}
	}
	
	protected final void setNumberFormat(String sessionId, AuthUser user, NumberFormat format) {
		try {
			getUserSession(sessionId).setNumberFormat(format);
		} catch (SessionExpiredException e) {
		}
	}

	private final String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	public byte[] convertFromHex(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	private final String encrypt(String password) throws Exception {
		System.out.println("Encrypting " + password);
		String key = "trqHtG623NBXX3zP";
		byte [] pass_dec = new BASE64Decoder().decodeBuffer(password);

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(("flip" + "grt62-tts-2").getBytes("UTF-8"));
		byte[] iv = md.digest();		

		Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv));

		String pass = convertToHex(cipher.doFinal(pass_dec)).trim();

		System.out.println("Result: " + pass);
		return pass;		
	}
	
	private final String decrypt(String password) throws Exception {
		System.out.println("Decrypting " + password);
		
		String key = "trqHtG623NBXX3zP";
		byte[] pass_enc = convertFromHex(password); 
//			new BASE64Decoder().decodeBuffer(password);
		System.out.println("Pass_Enc = " + new String(pass_enc));

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(("flip" + "grt62-tts-2").getBytes("UTF-8"));
		byte[] iv = md.digest();		

		Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv));

		String pass = new String(cipher.doFinal(pass_enc), "UTF-8").trim();

		System.out.println("Result: " + pass);
		return pass;
	}
	
	private final synchronized void updateCookies(String login, String hash, String id) {
		Cookie cLogin = new Cookie("userName", login);
//		Cookie cPwd = new Cookie("userPassword", hash);
		Cookie cId = new Cookie("sessionID", id);
		int maxAge;
		String path = getThreadLocalRequest().getContextPath();
		maxAge = 60 * 60 * 24 * 30; // 1 month

		cLogin.setMaxAge(maxAge);
//		cPwd.setMaxAge(maxAge);
		cId.setMaxAge(maxAge);

		cLogin.setPath(path);
//		cPwd.setPath(path);
		cId.setPath(path);

		try {
			getThreadLocalResponse().addCookie(cLogin);
		} catch (Throwable t) {			
			t.printStackTrace();
		}
//		try {
//			getThreadLocalResponse().addCookie(cPwd);
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
		try {
			getThreadLocalResponse().addCookie(cId);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
//		displayCookie("updateCookies");
	}
	
//	private final void validateCookie(AuthUser user, String sessionId) {
//		Cookie[] cookies = getThreadLocalRequest().getCookies();
//		String loginName = null;
//		String password = null;
//		String id = null;
//		if (cookies != null) {
//			for (int i = 0; i < cookies.length; i++) {
//				Cookie cookie = cookies[i];
//				String name = cookie.getName();
//				String value = cookie.getValue();
//				if (name.equalsIgnoreCase("userName")) {
//					loginName = value;
//				} else if (name.equalsIgnoreCase("userPassword")) {
//					password = value;
//				} else if (name.equalsIgnoreCase("sessionID")) {
//					id = value;
//				}
//			}
//		}
//		
//		if (loginName == null || password == null || id == null) {
////			System.err.println("Resetting cookie");
//			updateCookies(user.getLoginName(), user.getPassword(), sessionId);
//		}
//	}
	
//	private final void displayCookie(String msg) {
//		Cookie[] cookies = getThreadLocalRequest().getCookies();
//		String loginName = null;
//		String password = null;
//		String id = null;
//		if (cookies != null) {
//			for (int i = 0; i < cookies.length; i++) {
//				Cookie cookie = cookies[i];
//				String name = cookie.getName();
//				String value = cookie.getValue();
//				if (name.equalsIgnoreCase("userName")) {
//					loginName = value;
//				} else if (name.equalsIgnoreCase("userPassword")) {
//					password = value;
//				} else if (name.equalsIgnoreCase("sessionID")) {
//					id = value;
//				}
//			}
//		}
//		
////		System.err.println("[" + msg + "]" + " cookie: " + loginName + ", " + password + ", " + id);		
//	}
	
//	protected Object [] getAuthUserAndIdFromCookies () {
//		Cookie[] cookies = getThreadLocalRequest().getCookies();
//		String loginName = null;
//		String password = null;
//		String id = null;
//		if (cookies != null) {
//			for (int i = 0; i < cookies.length; i++) {
//				Cookie cookie = cookies[i];
//				String name = cookie.getName();
//				String value = cookie.getValue();
//				if (name.equalsIgnoreCase("userName")) {
//					loginName = value;
//				} else if (name.equalsIgnoreCase("userPassword")) {
//					try {
//						password = decrypt(value);
//					} catch (Exception e) {
//						e.printStackTrace();
//						password = value;
//					}
//				} else if (name.equalsIgnoreCase("sessionID")) {
//					id = value;
//				}
//			}
//		}
//		
////		System.err.println("Examining cookie: " + loginName + ", " + password + ", " + id);
//		
//		try {
//			return new Object [] {ServiceProvider.getAuthenticationService().authenticateHash(
//				loginName, password), id};
//		} catch (org.palo.viewapi.exceptions.AuthenticationFailedException e) {
////			System.err.println("Could not authenticate user from cookie.");
//		}
//		return null;
//	}
		
	protected String getLoginNameFromCookies () {
		try {
			Cookie[] cookies = getThreadLocalRequest().getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie cookie = cookies[i];
					String name = cookie.getName();
					String value = cookie.getValue();
					if (name.equalsIgnoreCase("userName")) {
						return value;
					} 
				}
			}
		} catch (Throwable t) {			
		}
		return null;
	}

	private final HttpSession startNewSession() {		
		HttpSession session = getSession();
		//SpagoBI informations
		String documentId=(String)session.getAttribute("spagobidocument");
		String profile = (String) session.getAttribute("spagobiuser");
		String subobjId=(String)session.getAttribute("spagobisubobj");
		String spagobiState=(String)session.getAttribute("spagobi_state");
		//System.out.println("startNewSession .. spagoBIState:: "+spagobiState);
		String spagobiDev=(String)session.getAttribute("isdeveloper");
		if(session != null) {
//			return session; //session.invalidate();
			session.invalidate();
		}
				
		session = getThreadLocalRequest().getSession(true);
		if(documentId != null)
			session.setAttribute("spagobidocument", documentId);
		if(profile != null)
			session.setAttribute("spagobiuser", profile);
		if(subobjId != null)
			session.setAttribute("spagobisubobj", subobjId);
		if(spagobiState != null)
			session.setAttribute("spagobi_state", spagobiState);
		if(spagobiDev != null){			
			session.setAttribute("isdeveloper", spagobiDev);
		}
//		System.out.println("SessionID: " + session.getId());
		return session;
	}
	private final void bindUserToSession(UserSession userSession, HttpSession session) {
		UserBinding binding = new UserBinding(userSession, sessionListener);
		synchronized(session) {
			session.setAttribute(SESSION_USER, binding);
		}
		String loginName = binding.getUserSession().getUser().getLoginName();
		String pw = binding.getUserSession().getUser().getPassword();
		userBinding.put(loginName, pw);
//		System.err.println(session.getId());
	}
		
	protected void initDbConnection(ServletContext globalContext) {		
	}
	
	protected final synchronized HttpSession getSession() {		
		HttpServletRequest request = getThreadLocalRequest();
		return request.getSession();
	}
}
class UserBinding implements HttpSessionBindingListener {

	private final UserSession userSession;
	private final ISessionListener sessionListener;
	
	public UserBinding(UserSession userSession, ISessionListener sessionListener) {
		this.userSession = userSession;
		this.sessionListener = sessionListener;	
	}
	
	public final UserSession getUserSession() {
		return userSession;
	}
	
	public void valueBound(HttpSessionBindingEvent be) {
		sessionListener.sessionStarted(be.getSession());
	}

	public void valueUnbound(HttpSessionBindingEvent be) {
		try {
			UserBinding binding = (UserBinding)be.getValue();
			UserSession userSession = binding.getUserSession();
		//	remove all controllers:
		} catch (Throwable t) {
		}
	}
	
}
