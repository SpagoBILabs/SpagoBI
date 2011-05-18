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
  * Chiara Chiarelli
  */

Ext.ns("Sbi.execution");

Sbi.execution.SaveDocumentWindow = function(config) {

	this.services = new Array();
	var saveDocParams = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: 'DOC_SAVE'};
	this.services['saveDocumentService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SAVE_DOCUMENT_ACTION'
		, baseParams: saveDocParams
	});
	
	this.SBI_EXECUTION_ID = config.SBI_EXECUTION_ID;
	this.OBJECT_ID = config.OBJECT_ID;
	this.OBJECT_TYPE = config.OBJECT_TYPE;
	this.OBJECT_ENGINE = config.OBJECT_ENGINE;
	this.OBJECT_TEMPLATE = config.OBJECT_TEMPLATE;
	this.OBJECT_DATA_SOURCE = config.OBJECT_DATA_SOURCE;
	this.OBJECT_PARS = config.OBJECT_PARS;
	
	this.initFormPanel();
	
	var c = Ext.apply({}, config, {
		id:'popup_docSave',
		layout:'fit',
		width:640,
		height:350,
		closeAction: 'close',
		buttons:[{ 
			  iconCls: 'icon-save' 	
			, handler: this.saveDocument
			, scope: this
			, text: LN('sbi.generic.update')
           }],
		title: LN('sbi.execution.saveDocument'),
		items: this.saveDocumentForm
	});   
	
    Sbi.execution.SaveDocumentWindow.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.execution.SaveDocumentWindow, Ext.Window, {
	
	inputForm: null
	,saveDocumentForm: null
	,SBI_EXECUTION_ID: null
	,OBJECT_ID: null
	,OBJECT_TYPE: null
	,OBJECT_ENGINE: null
	,OBJECT_TEMPLATE: null
	,OBJECT_DATA_SOURCE: null
	,OBJECT_PARS: null
	
	,initFormPanel: function (){
		
		this.docName = new Ext.form.TextField({
			id: 'docName',
			name: 'docName',
			allowBlank: false, 
			inputType: 'text',
			maxLength: 200,
			width: 250,
			fieldLabel: LN('sbi.generic.name') 
		});
		
		this.docLabel = new Ext.form.TextField({
	        id:'docLabel',
	        name: 'docLabel',
	        allowBlank: false, 
	        inputType: 'text',
	        maxLength: 20,
	        width: 250,
			fieldLabel: LN('sbi.generic.label')  
	    });
		
		this.docDescr = new Ext.form.TextArea({
	        id:'docDescr',
	        name: 'docDescr',
	        inputType: 'text',
	        allowBlank: true, 
	        maxLength: 400,
	        width: 250,
	        height: 80,
			fieldLabel: LN('sbi.generic.descr')  
	    });
	    
	    this.inputForm = new Ext.Panel({
	         itemId: 'detail'
	        , width: 380
	        , items: {
		   		 id: 'items-detail',   	
	 		   	 itemId: 'items-detail',   	              
	 		   	 columnWidth: 0.4,
	             xtype: 'fieldset',
	             labelWidth: 80,
	             defaults: {width: 250, border:false},    
	             defaultType: 'textfield',
	             autoHeight: true,
	             autoScroll  : true,
	             bodyStyle: Ext.isIE ? 'padding:0 0 5px 5px;' : 'padding:0px 5px;',
	             border: false,
	             style: {
	                 "margin-left": "4px",
	                 "margin-top": "25px"
	             },
	             items: [this.docName,this.docLabel,this.docDescr]
	    	}
	    });
	    
	    this.treePanel = new Sbi.browser.DocumentsTree({
	          border: true,
	          collapsible: false,
	          title: '',
	          drawUncheckedChecks: true,
	          bodyStyle:'padding:6px 6px 6px 6px;',
	    });
	    
	    this.saveDocumentForm = new Ext.form.FormPanel({
		          frame: true,
		          autoScroll: true,
		          labelAlign: 'left',
		          autoWidth: true,
		          height: 650,
		          layout: 'column',
		          scope:this,
		          forceLayout: true,
		          trackResetOnLoad: true,
		          layoutConfig : {
		 				animate : true,
		 				activeOnTop : false

		 			},
		          items: [
		              this.inputForm
		              , this.treePanel           	  		
		          ]
		          
		      });
	}
	
	,saveDocument: function () {
		 
		var docName = this.docName.getValue();
		var docLabel = this.docLabel.getValue();
		var docDescr = this.docDescr.getValue();
		var functs = this.treePanel.returnCheckedIdNodesArray();
		
		if(docName == null || docName == undefined ||
		   docLabel == null || docLabel == undefined ||
		   functs == null || functs == undefined 
		   || functs.length == 0){
				Ext.MessageBox.show({
	                title: LN('sbi.generic.warning'),
	                msg:  LN('sbi.document.saveWarning'),
	                width: 180,
	                buttons: Ext.MessageBox.OK
	           });
		}else{	
			functs = Ext.util.JSON.encode(functs);
			var params = {
		        	name :  docName,
		        	label : docLabel,
		        	description : docDescr,
					typeid: this.OBJECT_TYPE,
					engineid: this.OBJECT_ENGINE,
					template: this.OBJECT_TEMPLATE,
					datasourceid: this.OBJECT_DATA_SOURCE,
					pars: this.OBJECT_PARS,
					functs: functs
		        };
			
			Ext.Ajax.request({
		        url: this.services['saveDocumentService'],
		        params: params,
		        callback : function(options , success, response) {
		  	  		if (success) {
			      		if(response !== undefined && response.responseText !== undefined) {
			      			var content = Ext.util.JSON.decode( response.responseText );
			      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.generic.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });              
				      		}else{			      			
				      			Ext.MessageBox.show({
				                        title: LN('sbi.generic.result'),
				                        msg: LN('sbi.generic.resultMsg'),
				                        width: 200,
				                        buttons: Ext.MessageBox.OK
				                });
				      			 this.close();
				      		}  
			      		} else {
			      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			      		}
		  	  		}
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
			});
		}
	}
	
});