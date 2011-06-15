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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.FormPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.formbuilder.formpanel.title')
	};
	
	this.services = new Array();
	var params = {};
	this.services['setFormBuilderState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SET_FORM_BUILDER_STATE_ACTION'
		, baseParams: params
	});
	this.services['getFormPreview'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_FORM_PREVIEW_ACTION'
		, baseParams: {'MODALITY' : 'SMARTFILTER_EDIT'}
	});
		
	if(Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.formPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.formPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.formBuilderPage = new Sbi.formbuilder.FormBuilderPage({
		closable: false,
		template: config.template
	});
	this.formPreviewPage = new Sbi.formbuilder.FormPreviewPage({closable: false});
	
	c = Ext.apply(c, {
		border: false
		, tabPosition: 'bottom'
  		, activeTab: 0
      	, items: [this.formBuilderPage, this.formPreviewPage]
	});

	// constructor
	Sbi.formbuilder.FormPanel.superclass.constructor.call(this, c);
	
	// if moving towards preview, send form builder state to server, and then update iframe content
	this.formPreviewPage.on('activate', function() {
		
		this.formBuilderPage.validateTemplate(
			function() {
				this.setFormBuilderState(this.refreshPreview, Sbi.exception.ExceptionHandler.handleFailure, this);
			}, function(errors) {
				var message = errors.join('<br>');
				Sbi.exception.ExceptionHandler.showErrorMessage(message, LN('sbi.formbuilder.formbuilderpage.validationerrors.title'));
		}, this);
		
	}, this);

};

Ext.extend(Sbi.formbuilder.FormPanel, Ext.TabPanel, {
	
	services: null

	, setFormBuilderState: function(successCallback, failureCallback, scope) {
		var state = this.formBuilderPage.templateEditorPanel.getContents();
		var params = {
				"FORM_STATE": Sbi.commons.JSON.encode(state)
		};
		Ext.Ajax.request({
		    url: this.services['setFormBuilderState'],
		    success: successCallback,
		    failure: failureCallback,	
		    scope: scope,
		    params: params
		});   
	}
	
	, refreshPreview: function() {
		this.formPreviewPage.getFrame().setSrc(this.services['getFormPreview']);
	}
    
});