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
