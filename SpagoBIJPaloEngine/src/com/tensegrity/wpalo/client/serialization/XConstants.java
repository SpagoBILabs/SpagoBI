/*
*
* @file XConstants.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: XConstants.java,v 1.15 2009/12/17 16:14:20 PhilippBouillon Exp $
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
