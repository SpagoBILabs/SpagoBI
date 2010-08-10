/*
*
* @file WPaloConstants.java
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
* @version $Id: WPaloConstants.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client;


/**
 * Interface to represent the constants contained in resource  bundle:
 * 	'C:/Users/PhilippBouillon/workspace33/WPalo/src/com/tensegrity/wpalo/client/WPaloConstants.properties'.
 */
public interface WPaloConstants extends com.google.gwt.i18n.client.Constants {
  
  /**
   * Translated "Create new Folder".
   * 
   * @return translated "Create new Folder"
  
   */
  @DefaultStringValue("Create new Folder")
  String reportNavigatorView_addFolderToolTip();

  /**
   * Translated "Create new Workbook Template".
   * 
   * @return translated "Create new Workbook Template"
  
   */
  @DefaultStringValue("Create new Workbook Template")
  String reportNavigatorView_addWorkbookTemplateToolTip();

  /**
   * Translated "Report Templates".
   * 
   * @return translated "Report Templates"
  
   */
  @DefaultStringValue("Report Templates")
  String reportNavigatorView_heading();

  /**
   * Translated "Sheet Templates".
   * 
   * @return translated "Sheet Templates"
  
   */
  @DefaultStringValue("Sheet Templates")
  String reportNavigatorView_sheetTemplatesName();

  /**
   * Translated "Delete items".
   * 
   * @return translated "Delete items"
  
   */
  @DefaultStringValue("Delete items")
  String reportNavigatorView_deleteItemsToolTip();

  /**
   * Translated "Adhoc Templates".
   * 
   * @return translated "Adhoc Templates"
  
   */
  @DefaultStringValue("Adhoc Templates")
  String reportNavigatorView_adhocTemplatesName();

  /**
   * Translated "Create new AdHoc View Template".
   * 
   * @return translated "Create new AdHoc View Template"
  
   */
  @DefaultStringValue("Create new AdHoc View Template")
  String reportNavigatorView_addAdhocViewTemplateToolTip();
}
