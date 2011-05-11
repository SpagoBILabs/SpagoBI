/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.WorkSheetPreviewPage = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.worksheet.runtime.worksheetpreviewpage.title')
		, defaultSrc: 'about:blank'
		, autoLoad: true
        , loadMask: {msg: 'Loading...'}
        , fitToParent: true  // not valid in a layout
        , disableMessaging: true
	};
	
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime && Sbi.settings.worksheet.runtime.workSheetPreviewPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.workSheetPreviewPage);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	
	// constructor
	Sbi.worksheet.runtime.WorkSheetPreviewPage.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.runtime.WorkSheetPreviewPage, Ext.ux.ManagedIFramePanel, {
    
});