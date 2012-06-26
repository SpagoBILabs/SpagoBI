/*
*
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
*
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
