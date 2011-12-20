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
package it.eng.qbe.model.structure;

import it.eng.spagobi.utilities.assertion.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractModelObject implements IModelObject {
	
	protected long id;	
	protected String name;
	protected Map<String,Object> properties;
	
	public long getId() {
		return id;
	}
	
	protected void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected void initProperties() {
		if(properties == null) {
			properties = new HashMap<String,Object>();
		}
	}
	
	public Map<String,Object> getProperties() {
		return properties;
	}
	
	public Object getProperty(String name) {
		return properties.get(name);
	}
	
	public String getPropertyAsString(String name) {
		return (String)getProperty(name);
	}
	
	public boolean getPropertyAsBoolean(String name) {
		boolean booleanValue = true;
		String stringValue = getPropertyAsString(name);
		//Assert.assertNotNull(stringValue, "Property [" + name + "] is not defined for item [" + this.name + "]");
		if(stringValue == null) {
			Assert.assertUnreachable("Property [" + name + "] is not defined for item [" + this.name + "]");
		}
		
		if( "TRUE".equalsIgnoreCase( stringValue ) ) {
			booleanValue = true;
		} else if( "FALSE".equalsIgnoreCase( stringValue ) ) {
			booleanValue = false;
		} else {
			Assert.assertUnreachable("Value [" + stringValue + "] is not vaid for the boolean property [" + this.name + "] of item [" + name + "]");
		}	
		
		return booleanValue;
	}
	
	public int getPropertyAsInt(String name) {
		int intValue = 0;
		String stringValue = getPropertyAsString(name);
		//Assert.assertNotNull(stringValue, "Property [" + name + "] is not defined for item [" + this.name + "]");
		if(stringValue == null) {
			Assert.assertUnreachable("Property [" + name + "] is not defined for item [" + this.name + "]");
		}
		
		intValue = Integer.parseInt(stringValue);
		
		return intValue;
	}
	
	public void setProperties(Map<String,Object> properties) {
		this.properties = properties;
	}
	
	
}
