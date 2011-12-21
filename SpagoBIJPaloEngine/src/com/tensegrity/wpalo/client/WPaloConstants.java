/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
