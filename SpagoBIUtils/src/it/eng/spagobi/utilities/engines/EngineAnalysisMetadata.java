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
package it.eng.spagobi.utilities.engines;

import java.util.Date;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class EngineAnalysisMetadata {
	private Integer id;
	private String name;	
	private String description;
	private String owner;	
	private String scope;
	private Date firstSavingDate;
	private Date lastSavingDate;
	
	public static final String PUBLIC_SCOPE = "public";
	public static final String PRIVATE_SCOPE = "private";

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * Sets the owner.
	 * 
	 * @param owner the new owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * Gets the scope.
	 * 
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * Sets the scope.
	 * 
	 * @param scope the new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	/**
	 * Gets the first saving date.
	 * 
	 * @return the first saving date
	 */
	public Date getFirstSavingDate() {
		return firstSavingDate;
	}
	
	/**
	 * Sets the first saving date.
	 * 
	 * @param firstSavingDate the new first saving date
	 */
	public void setFirstSavingDate(Date firstSavingDate) {
		this.firstSavingDate = firstSavingDate;
	}
	
	/**
	 * Gets the last saving date.
	 * 
	 * @return the last saving date
	 */
	public Date getLastSavingDate() {
		return lastSavingDate;
	}
	
	/**
	 * Sets the last saving date.
	 * 
	 * @param lastSavingDate the new last saving date
	 */
	public void setLastSavingDate(Date lastSavingDate) {
		this.lastSavingDate = lastSavingDate;
	}
	
	/**
	 * Checks if is public.
	 * 
	 * @return true, if is public
	 */
	public boolean isPublic() {
		return scope!=null && scope.equalsIgnoreCase( PUBLIC_SCOPE );
	}
	
	/**
	 * Already saved.
	 * 
	 * @return true, if successful
	 */
	public boolean alreadySaved() {
		return firstSavingDate != null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
    	String str = "";
    	
    	str += "[";
    	str += "id:" + id + "; ";
    	str += "name:" + name + "; ";
    	str += "description:" + description + "; ";
    	str += "owner:" + owner + "; ";
    	str += "scope:" + scope + "; ";
    	str += "firstSavingDate:" + firstSavingDate;
    	str += "firstSavingDate:" + lastSavingDate;
    	str += "]";
    	
    	return str;
    }
}
