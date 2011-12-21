/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.serialization;

/**
 * <code>XConstants</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XConstants.java,v 1.15 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class XConstants {

	public static final String TYPE_ROOT_NODE				= "rootnode";
	public static final String TYPE_ELEMENT_NODE       	   = "elementnode";
	
	//report administration
	public static final String TYPE_SHEET_TEMPLATES_NODE = "sheettemplatesnode";
	public static final String TYPE_ADHOC_TEMPLATES_NODE = "adhoctemplatesnode";
	
	//administration
	public static final String TYPE_USERS_NODE			= "usersnode";
	public static final String TYPE_GROUPS_NODE			= "groupsnode";
	public static final String TYPE_ROLES_NODE			= "rolesnode";
	public static final String TYPE_ACCOUNTS_NODE		= "accountsnode";
	public static final String TYPE_CONNECTIONS_NODE	= "connectionsnode";
	
	//WSS
    public static final String TYPE_WSS_ACCOUNTS_NODE   = "wssaccountsnode";

	//cube view browser
	public static final String TYPE_VIEWBROWSER_ROOT	= "viewbrowserrootnode";
	public static final String TYPE_VIEWBROWSER_ACCOUNT_NODE	= "viewbrowseraccountnode";
	public static final String TYPE_VIEWBROWSER_DATABASE_NODE	= "viewbrowserdatabasenode";
	public static final String TYPE_VIEWBROWSER_CUBE_NODE	= "viewbrowsercubenode";
	public static final String TYPE_VIEWBROWSER_VIEW_NODE	= "viewbrowserviewnode";
	
	//folders
	public static final String TYPE_ROOT_REPORT_STRUCTURE_NODE = "reportstructureroot";
	public static final String TYPE_ACCOUNT_VIEWS_NODE	= "accountviewsnode";
	public static final String TYPE_FOLDER_ELEMENT_VIEW        = "view";
	public static final String TYPE_FOLDER_ELEMENT_SHEET       = "sheet";
	
	//modeller
	public static final String TYPE_DIMENSION_WITH_SUBSETS = "dimensionwithsubsets";
	public static final String TYPE_DATABASE_NO_CUBES      = "databasenocubes";
}
