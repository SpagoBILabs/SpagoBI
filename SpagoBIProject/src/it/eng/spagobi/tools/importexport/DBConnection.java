/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.importexport;

/** 
 * Bean that describes a SpagoBI database connection
 */
public class DBConnection {

	private String name;
	private String description;
	private boolean jdbcType = false;
	private boolean jndiType = false;
	
	/**
	 * Gets the description of the database connection.
	 * 
	 * @return description of the database connection
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description.
	 * 
	 * @param description Description for the connection
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the name of the database connection.
	 * 
	 * @return name of the databse connection
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name.
	 * 
	 * @param name Name for the connection
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Check if the connection is a jdbc connections.
	 * 
	 * @return True if the connection is a jdbc
	 */
	public boolean isJdbcType() {
		return jdbcType;
	}
	
	/**
	 * Set the flag which store if the connection is a jdbc kind of connection.
	 * 
	 * @param jdbcType boolean value to describe a jdbc kind of connection
	 */
	public void setJdbcType(boolean jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	/**
	 * Check if the connection is a jndi connections.
	 * 
	 * @return True if the connection is a jndi
	 */
	public boolean isJndiType() {
		return jndiType;
	}
	
	/**
	 * Set the flag which store if the connection is a jndi kind of connection.
	 * 
	 * @param jndiTypw boolean value to describe a jndi kind of connection
	 */
	public void setJndiType(boolean jndiTypw) {
		this.jndiType = jndiTypw;
	}
	
	
}
