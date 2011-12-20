/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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

import it.eng.spago.base.SourceBean;

/**
 * @author Monica Franceschini
 *
 */
public class JPaloEngineTemplate {
	
	
	/**
	 * expected template structure:
	 *
	 * <olap connection="Mondrian" 
     *  	 account="admin" 
     *       view="Sales"
     *		 cube="Sales">
	 * </olap>
	 */
	private SourceBean templateSB;
	
	private static final String CONNECTION_NAME = "connection";
	private static final String ACCOUNT_NAME = "account";
	private static final String CUBE_ATTRIBUTE_NAME = "cube";
	private static final String VIEW_NAME = "view";
	
	
	
	public JPaloEngineTemplate(SourceBean template) {
		setTemplateSB(template);
	}

	protected SourceBean getTemplateSB() {
		return templateSB;
	}

	protected void setTemplateSB(SourceBean templateSB) {
		this.templateSB = templateSB;
	}
	public String getAccountName() {
		return (String)getTemplateSB().getAttribute( ACCOUNT_NAME );
	}
	
	public String getConnectionName() {	
		return (String)getTemplateSB().getAttribute( CONNECTION_NAME );
	}
	
	public String getCubeName() {		
		return (String)getTemplateSB().getAttribute( CUBE_ATTRIBUTE_NAME );
	}
	
	public String getViewName() {		
		return (String)getTemplateSB().getAttribute( VIEW_NAME );
	}
	
}
