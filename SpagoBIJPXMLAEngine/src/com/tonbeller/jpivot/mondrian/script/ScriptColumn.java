/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPIVOT.LICENSE.txt file
 * 
 */
package com.tonbeller.jpivot.mondrian.script;

/**
 * @author Engineering Ingegneria Informatica S.p.A. - Luca Barozzi
 */
public class ScriptColumn {
	private String title;
	private int position;
	private String file;
	
	public ScriptColumn() {
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
