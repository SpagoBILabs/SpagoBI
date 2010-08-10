/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
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
