/*
*
* @file WPaloEvent.java
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
* @version $Id: WPaloEvent.java,v 1.30 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client;

/**
 * <code>WPaloEvent</code>
 * <p>Defines all event types which can occur within WPalo. Interested 
 * controllers can register for certain types.</p>
 *
 * @version $Id: WPaloEvent.java,v 1.30 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public interface WPaloEvent {

	//general:
	public static final int APP_START = 0;
	public static final int APP_STOP = 1;
	public static final int INIT = 2;
	public static final int LOGIN = 3;
//	public static final int LOGGED_IN = 4;
	public static final int LOGOUT = 4;
	
//	public static final int SAVED_ITEM = 5;
	public static final int DELETED_ITEM = 6;
	public static final int LOGOUT_CLICKED = 7;
	
	//Update:
	public static final int UPDATE_WORKBOOKS = 50;
	
	//user, group and role administration:
	public static final int EXPANDED_ADMIN_SECTION = 100;

	public static final int SELECTED_USERS = 101;
	public static final int EDIT_USER_ITEM = 102;
	public static final int SELECTED_GROUPS = 103;
	public static final int EDIT_GROUP_ITEM = 104;	
	public static final int SELECTED_ROLES = 105;
	public static final int EDIT_ROLE_ITEM = 106;
		
	public static final int ADD_USER_ITEM = 110;
	public static final int ADD_GROUP_ITEM = 111;
	public static final int ADD_ROLE_ITEM = 112;
	
	public static final int SAVED_USER_ITEM = 113;
	public static final int SAVED_GROUP_ITEM = 114;
	public static final int SAVED_ROLE_ITEM = 115;
	
	//account and connections:
	public static final int EXPANDED_ACCOUNT_SECTION = 200;
	
	public static final int SELECTED_ACCOUNTS = 201;
	public static final int EDIT_ACCOUNT_ITEM = 202;	
	public static final int SELECTED_CONNECTIONS = 203;
	public static final int EDIT_CONNECTION_ITEM = 204;
	
	public static final int SAVED_ACCOUNT_ITEM = 205;
	public static final int SAVED_CONNECTION_ITEM = 206;
	
	public static final int ADD_ACCOUNT_ITEM = 207;
	public static final int ADD_CONNECTION_ITEM = 208;
	
	
	//report view:
	public static final int EXPANDED_REPORT_SECTION = 300;	
	public static final int EDIT_TEMPLATE_ITEM = 301;
	public static final int EDIT_TEMPLATE_VIEW = 302;
	public static final int SHOW_TEMPLATE_VIEW = 303;
	
	//report structure view:	
	public static final int EXPANDED_REPORT_STRUCTURE_SECTION = 400;
	public static final int EDIT_REPORT_STRUCTURE = 401;
	public static final int SET_EDITOR_INPUT = 402;
	
	//modeller view:
	public static final int EXPANDED_SERVER_SECTION = 500;	
	public static final int SELECTED_DIMENSIONS = 501;
	public static final int EDIT_DIMENSION_ITEM = 502;
	public static final int SELECTED_SERVERS = 503;
	public static final int EDIT_SERVER_ITEM = 504;
	
	//view mode viewer:	
	public static final int OPEN_VIEW_MODE = 600;
	public static final int INIT_VIEW_MODE = 601;
	public static final int VIEW_MODE_LOGIN = 602;
	public static final int VIEW_MODE_LOGOUT = 603;
	
	public static final int EXPANDED_VIEW_REPORT_STRUCTURE_SECTION = 604;
	public static final int VIEW_REPORT_EVENT = 605;
	
	//view browser:
	public static final int EXPANDED_VIEWBROWSER_SECTION = 700;
	public static final int EDIT_VIEWBROWSER_VIEW = 701;	
	public static final int DELETED_VIEWBROWSER_VIEW = 702;	
	public static final int SHOW_VIEWBROWSER_VIEW = 703;
	public static final int RENAMED_VIEWBROWSER_VIEW = 704;
	public static final int WILL_DELETE_VIEWBROWSER_VIEW = 705;
	public static final int VIEW_LOADED = 706;
}
