package it.eng.spagobi.utilities.themes;

import it.eng.spago.base.SourceBean;

public class Theme {

	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Theme(String name) {
		super();
		this.name = name;
	}
	public Theme(SourceBean def) {
		String name = (String)def.getAttribute("name");
		this.name = name;
	}
	
	
}
