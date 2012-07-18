package com.tensegrity.wpalo.client.ui.mvc.cubeview;

public class AliasFormat {
	public String name;
	public String id;
	
	public AliasFormat(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int hashCode() {
		return 7 + id.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AliasFormat)) {
			return false;
		}
		return ((AliasFormat) o).id.equals(id);
	}
}
