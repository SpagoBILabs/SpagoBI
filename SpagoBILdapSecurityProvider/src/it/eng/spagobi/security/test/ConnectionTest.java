/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class ConnectionTest {

	/**
	 * Creates the connection.
	 * 
	 * @return the lDAP connection
	 * 
	 * @throws Exception the exception
	 */
	public LDAPConnection createConnection () throws Exception {

	    LDAPConnection connection = null;

	  		try {
	  			connection = new LDAPConnection();
	  			// connessione
	  			connection.connect("localhost",389);
	        if (connection.isConnected()) {
	        	// login
	        	connection.bind(LDAPConnection.LDAP_V3,"cn=root,dc=spagobi,dc=com","root".getBytes("UTF8"));
	        	//connection.bind(3,"cn=biadmin,ou=People,dc=spagobi,dc=com","biadmin".getBytes("UTF8"));
	        }
	    		if (connection.isBound()) {
	    			// valore della radice e dello scope di ricerca
	    			return connection;
	    		}
	  		}catch (LDAPException e) {
	  			System.out.println("errore in createConnection:: createConnection " + e);  			
	  		}
	      catch(UnsupportedEncodingException e) {
	  			System.out.println("errore in createConnection:: createConnection:  " + e);  			
	      }  		
	    
			if (!connection.isConnected() || !connection.isBound()) {
				System.out.println("errore in UserContextHandling:: createConnection: connessione fallita");  			
				throw new Exception("UserContextHandling:: createConnection: connessione fallita");
			}
			return null;
			
		}

	/**
	 * Autenticate user.
	 * 
	 * @param userId the user id
	 * @param psw the psw
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public boolean autenticateUser (String userId,String psw) throws Exception {

	    LDAPConnection connection = null;

	  		try {
	  			connection = new LDAPConnection();
	  			// connessione
	  			connection.connect("localhost",389);
	        if (connection.isConnected()) {
	        	// login
	        	connection.bind(LDAPConnection.LDAP_V3,"cn="+userId+",ou=People,dc=spagobi,dc=com",psw.getBytes("UTF8"));
	        }
	    		if (connection.isBound()) {
	    			return true;
	    		}
	  		}catch (LDAPException e) {
	  			System.out.println("errore in createConnection:: createConnection " + e);  			
	  		}
	      catch(UnsupportedEncodingException e) {
	  			System.out.println("errore in createConnection:: createConnection:  " + e);  			
	      }  		
	    
			if (!connection.isConnected() || !connection.isBound()) {
				System.out.println("errore in UserContextHandling:: createConnection: connessione fallita");  			
				return false;
			}
			return false;
			
		}
	
	private HashMap getUserAttributes(String userId) throws Exception {
		HashMap userAttributes = new HashMap();
			
	    LDAPConnection connection = createConnection();
	    if (connection != null) {
			try {
				String[] attrIDs = {"description","sn"};
		    	LDAPSearchResults searchResults = connection.search("ou=People,dc=spagobi,dc=com",
		    			LDAPConnection.SCOPE_SUB,
		    			"(&(objectclass=person)(cn=biadmin))",
		    			attrIDs,false);
		    	
			    // popolamento userAttributes con attributeName e attributeValue
		    	LDAPEntry entry = null;
		    	LDAPAttributeSet attributeSet = null;
			    if (searchResults.hasMore()) {
		            try {
		                entry = searchResults.next();
		            }catch(LDAPException e) {
		            	e.printStackTrace();
		                System.out.println("errore in UserContext:: getUserAttributes: " + e.getMessage());
		            }			    	
					}
				    
				    if (entry != null) {
				    	attributeSet = entry.getAttributeSet();
						userAttributes.put("dn", entry.getDN());							
						userAttributes.put("description", entry.getAttribute("description"));
						userAttributes.put("sn", entry.getAttribute("sn"));
									    	
				    }

		 }catch (LDAPException e) {
		 		System.out.println("errore in UserContext:: getUserAttributes: " + e);					 	
		 		throw e;
		 }finally {
		 	if (connection != null)
		 		connection.disconnect();
		 }
	    
	    }
	    
	    return userAttributes;

	
	}

	private List getUserGroup(String userId) throws Exception {
		List userAttributes = new ArrayList();
			
	    LDAPConnection connection = createConnection();
	    if (connection != null) {
			try {
				String[] attrIDs = {"description","sn","ou"};
		    	LDAPSearchResults searchResults = connection.search("ou=People,dc=spagobi,dc=com",
		    			LDAPConnection.SCOPE_SUB,
		    			"(&(objectclass=person)(cn=biadmin))",
		    			attrIDs,false);
		    	
			    // popolamento userAttributes con attributeName e attributeValue
		    	LDAPEntry entry = null;
		    	LDAPAttributeSet attributeSet = null;
			    if (searchResults.hasMore()) {
		            try {
		                entry = searchResults.next();
		            }catch(LDAPException e) {
		            	e.printStackTrace();
		                System.out.println("errore in UserContext:: getUserAttributes: " + e.getMessage());
		            }			    	
					}
				    
				    if (entry != null) {
				    	attributeSet = entry.getAttributeSet();
				    	String[] ou=entry.getAttribute("ou").getStringValueArray();
				    	
						userAttributes.add(ou[0]);							
						userAttributes.add(ou[1]);
									    	
				    }

		 }catch (LDAPException e) {
		 		System.out.println("errore in UserContext:: getUserAttributes: " + e);					 	
		 		throw e;
		 }finally {
		 	if (connection != null)
		 		connection.disconnect();
		 }
	    
	    }
	    
	    return userAttributes;

	
	}

	
	private List getAllGroups() throws Exception {
		List groups = new ArrayList();
			
	    LDAPConnection connection = createConnection();
	    if (connection != null) {
			try {
				String[] attrIDs = {"description","ou"};
		    	LDAPSearchResults searchResults = connection.search("ou=Group,dc=spagobi,dc=com",
		    			LDAPConnection.SCOPE_SUB,
		    			"(objectclass=organizationalUnit)",
		    			attrIDs,false);
		    	
			    // popolamento userAttributes con attributeName e attributeValue
		    	LDAPEntry entry = null;
		    	LDAPAttributeSet attributeSet = null;
		    	while (searchResults.hasMore()){

		            try {
		                entry = searchResults.next();
		                if (entry != null) {
					    	attributeSet = entry.getAttributeSet();
							groups.add(attributeSet.getAttribute("ou").getStringValue());
		                }
		            }catch(LDAPException e) {
		            	e.printStackTrace();
		                System.out.println("errore in UserContext:: getUserAttributes: " + e.getMessage());
		            }			    	
				}


		 }catch (LDAPException e) {
		 		System.out.println("errore in UserContext:: getUserAttributes: " + e);					 	
		 		throw e;
		 }finally {
		 	if (connection != null)
		 		connection.disconnect();
		 }
	    
	    }
	    
	    return groups;

	
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String[] args) {
		ConnectionTest t=new ConnectionTest();
		try {
			//HashMap attr=t.getUserAttributes ("biadmin");
			//List attr=t.getUserGroup("biadmin");
			boolean attr=t.autenticateUser("biadmin","biadmin");
			System.out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
