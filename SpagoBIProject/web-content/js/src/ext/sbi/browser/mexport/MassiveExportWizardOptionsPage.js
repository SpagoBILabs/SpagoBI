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
 * Authors
 *
 * - Giulio Gavardi (giulio.gavardi@eng.it)
 */

Ext.ns("Sbi.browser.mexport");

Sbi.browser.mexport.MassiveExportWizardOptionsPage = function(config) {

	var defaultSettings = {
			title: LN('sbi.browser.mexport.massiveExportWizardOptionsPage.title')
	};

	if (Sbi.settings && Sbi.settings.browser 
			&& Sbi.settings.browser.mexport && Sbi.settings.browser.mexport.massiveExportWizardOptionsPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.browser.mexport.massiveExportWizardOptionsPage);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);

	this.services = new Array();
	this.services['StartMassiveExportExecutionProcessAction'] = this.services['StartMassiveExportExecutionProcessAction'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_MASSIVE_EXPORT_EXECUTION_PROCESS_ACTION'
			, baseParams: new Object()
	});

	this.addEvents('noDocsEvent');

	this.initFormPanel();
	this.initRolesCombo(Sbi.user.roles);

	c = Ext.apply(c, {
		layout: 'fit',       
		items: [this.formPanel]
	});

	// constructor
	Sbi.browser.mexport.MassiveExportWizardOptionsPage.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.browser.mexport.MassiveExportWizardOptionsPage, Ext.Panel, {

	selectedRole : null
	, formPanel : null
	, functId : null
	, rolesCombo : null
	, docsPanel : null
	, checkBox : null
	// methods
	, initFormPanel: function(){
		this.formPanel = new Ext.form.FormPanel({
			id: 'formPanel',
			//layout: 'fit',
			
		});
	
	}
	, initRolesCombo: function (rolesArray) {
		//rolesArray (building with multidimensional array)	  
		var multiArray = new Array(); 
		var singleArray;
		for(i = 0; i < rolesArray.length; i ++){
			singleArray = new Array();
			singleArray[0] = rolesArray[i];
			multiArray[i] = singleArray;
		}
		var scopeComboBoxStore = new Ext.data.SimpleStore({
				 fields: ['role']
		         , data : multiArray
		         , autoLoad: true
		}); 
		if(!this.selectedRole){
			selectedRole = rolesArray[0];
		}

		// if there is only one role don't draw combo
		if(rolesArray.length==1){
			selectedRole = rolesArray[0];
			this.rolesCombo = null;
			this.retrieveDocuments();
		}
		else {
			// in the case of more roles
			this.rolesCombo = new Ext.form.ComboBox({
				  tpl: '<tpl for="."><div ext:qtip="{role}" class="x-combo-list-item">{role}</div></tpl>'
				, editable  : true
				, forceSelection : true
				, fieldLabel : LN('sbi.browser.mexport.massiveExportWizardOptionsPage.field.role.label')
				, labelAlign : 'left'
				, name : 'roles'
				, emptyText: LN('sbi.browser.mexport.massiveExportWizardOptionsPage.field.role.emptyText')
				, mode : 'local'
				, typeAhead: true
				, triggerAction: 'all'
				, store: scopeComboBoxStore
			    , displayField:'role'
				, valueField:'role'
			    , listeners: {
					'select': {
						fn: function(){ 
						selectedRole = this.rolesCombo.getValue();
						this.rolesCombo.setValue(selectedRole);
						}
				, scope: this
						}
					}
				});	
		
				this.formPanel.add(this.rolesCombo);

				// select first as default
				this.rolesCombo.on('render', function() {
						if(rolesArray.length>0){
							var sel = rolesArray[0];
							this.rolesCombo.setValue(sel);
						}
						this.retrieveDocuments();
					}, this);  
			} // close multi-role case

	}
	, retrieveDocuments: function (rolesArray) {
	
		//	calls ervice to get export documents list
		Ext.Ajax.request({
			url: this.services['StartMassiveExportExecutionProcessAction'],
	
			params: {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null, TYPE: 'WORKSHEET', MODALITY : 'RETRIEVE_DOCUMENTS_MODALITY', functId: this.functId},
	
			success : function(response, options){
			if(response !== undefined) {   
				if(response.responseText !== undefined) {
					var content = Ext.util.JSON.decode( response.responseText );
					if(content !== undefined) {
						// build documents list
						var docsArray = content.selectedDocuments;
						var list ='<ul>'
						if(docsArray.length==0){
							list = LN('sbi.browser.mexport.massiveExportWizardOptionsPage.msg.noDoc');
							this.fireEvent('noDocsEvent', this, this);	
						}
						else{
							for(i=0;i<docsArray.length;i++){
								var name = docsArray[i];
								list+='<li>'+name+'</li>';
							}
						}
						list+='</ul>';
						
						// checkbox
						this.buildCycleCheck(); 
						// draw documents list
						this.buildDocsPanel(list);
						this.doLayout();
					} 
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			}
			else{
				//clear preceding store if error happened
				for(p in this.parametersPanel.fields){
					var field = this.parametersPanel.fields[p];
					field.disable();
				}
				this.btnFinish.disable();
			}
		},
		scope: this,
		failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
	
	
	}
	, buildDocsPanel : function(list){
		
		this.docsPanel = new Ext.Panel({
	    	name : 'DocToExport'
	  	   	, title : LN('sbi.browser.mexport.massiveExportWizardOptionsPage.field.documents.label')
			, html : list
		});
		this.formPanel.add(this.docsPanel);
		
	}
	, buildCycleCheck : function(){
		this.checkBox = new Ext.form.Checkbox({
			name : 'cycle'
			, fieldLabel : LN('sbi.browser.mexport.massiveExportWizardOptionsPage.field.cycle.label')
		});
	
		this.formPanel.add(this.checkBox);
		this.formPanel.doLayout();
		this.doLayout();
	}
	
	, getSelectedRole : function(){
		return selectedRole;
	}
	, isCycleOnFilterSelected : function(){
		return this.checkBox.checked;
	}

});