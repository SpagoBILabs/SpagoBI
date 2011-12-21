/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
