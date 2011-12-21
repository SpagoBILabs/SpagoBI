/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.dialog;

public class LanguageData {
	String name;
	String id;
	
	LanguageData(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public String toString() {
		return name;
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LanguageData)) {
			return false;
		}
		return ((LanguageData) o).id.equals(id);
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
}
