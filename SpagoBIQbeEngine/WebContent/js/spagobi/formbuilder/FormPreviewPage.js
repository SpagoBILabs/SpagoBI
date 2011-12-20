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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.FormPreviewPage = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.formbuilder.formpreviewpage.title')
		, defaultSrc: 'about:blank'
		, autoLoad: true
        , loadMask: {msg: 'Loading...'}
        , fitToParent: true  // not valid in a layout
        , disableMessaging: true
	};
	
	if(Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.formPreviewPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.formPreviewPage);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	
	// constructor
	Sbi.formbuilder.FormPreviewPage.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.formbuilder.FormPreviewPage, Ext.ux.ManagedIFramePanel, {
    
});