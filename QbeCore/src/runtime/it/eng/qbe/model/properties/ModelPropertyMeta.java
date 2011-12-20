/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.model.properties;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelPropertyMeta {
	protected String name;
	protected boolean inherited;   
	protected boolean optional;   
	protected String defaultValue; 
	
	public ModelPropertyMeta(String name, boolean inherited, boolean optional, String defaultValue) {
		this.name = name;
		this.inherited = inherited;
		this.optional = optional;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public boolean isInherited() {
		return inherited;
	}

	public boolean isOptional() {
		return optional;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}
