/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.datasource;

// TODO: Auto-generated Javadoc
/**
 * The Class DBConnection.
 * 
 * @author Andrea Gioia
 */
public class DBConnection {
	
		// GENERAL
	 	
		/** The name. */
		private String name;
		
		/** The dialect. */
		private String dialect;
		
		// JNDI

	    /** The jndi name. */
		private String jndiName;
	    
	    // STATIC
	    
	    /** The url. */
    	private String url;	  
	    
	    /** The driver class. */
    	private String driverClass;

	    /** The password. */
    	private String password;
	    
	    /** The username. */
    	private String username;

	    
	    
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 * 
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the jndi name.
		 * 
		 * @return the jndi name
		 */
		public String getJndiName() {
			return jndiName;
		}

		/**
		 * Sets the jndi name.
		 * 
		 * @param jndiName the new jndi name
		 */
		public void setJndiName(String jndiName) {
			this.jndiName = jndiName;
		}

		/**
		 * Gets the url.
		 * 
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the url.
		 * 
		 * @param url the new url
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Gets the password.
		 * 
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * Sets the password.
		 * 
		 * @param password the new password
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * Gets the driver class.
		 * 
		 * @return the driver class
		 */
		public String getDriverClass() {
			return driverClass;
		}

		/**
		 * Sets the driver class.
		 * 
		 * @param driverClass the new driver class
		 */
		public void setDriverClass(String driverClass) {
			this.driverClass = driverClass;
		}

		/**
		 * Gets the username.
		 * 
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * Sets the username.
		 * 
		 * @param username the new username
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * Gets the dialect.
		 * 
		 * @return the dialect
		 */
		public String getDialect() {
			return dialect;
		}

		/**
		 * Sets the dialect.
		 * 
		 * @param dialect the new dialect
		 */
		public void setDialect(String dialect) {
			this.dialect = dialect;
		}
		
		/**
		 * Checks if is jndi conncetion.
		 * 
		 * @return true, if is jndi conncetion
		 */
		public boolean isJndiConncetion() {
			return (jndiName != null);
		}
	    
}
