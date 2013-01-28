/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.widgets");

Sbi.widgets.Catalogue = function(config) {
	
	// apply defaults values
	config = Ext.apply({
		// no defaults
	}, config || {});
	
	this.configurationObject = Ext.apply({}, config.configurationObject || {});
	
	this.configurationObject.manageListService = config.mainListServices.manageListService;
	this.configurationObject.saveItemService = config.mainListServices.saveItemService;
	this.configurationObject.deleteItemService = config.mainListServices.deleteItemService;
	
	this.init(config);
	
	config.configurationObject = this.configurationObject;
	config.singleSelection = true;
	config.fileUpload = true;  // this is a multipart form!!
	
	var c = Ext.apply({}, config);

	Sbi.widgets.Catalogue.superclass.constructor.call(this, c);
	
	this.rowselModel.addListener('rowselect',function(sm, row, rec) { 
		this.getForm().loadRecord(rec);
		this.versionsGridPanel.getStore().load({ params : { id : rec.get('id') } });
		this.uploadField.reset();
     }, this);

};

Ext.extend(Sbi.widgets.Catalogue, Sbi.widgets.ListDetailForm, {
	
	configurationObject : null
	, detailPanel : null
	, uploadField : null

	,
	init : function(config) {
	    this.configurationObject.fields = [ 'id' , 'name' , 'description' ];
		
		this.configurationObject.emptyRecToAdd = new Ext.data.Record({ id : 0, name : '' , description : ''});
		
		this.configurationObject.gridColItems = 
			[
				{header: LN('sbi.generic.name'), width: 200, sortable: true, locked: true, dataIndex: 'name'},
				{header: LN('sbi.generic.descr'), width: 220, sortable: true, dataIndex: 'description'}
			];
		
		this.initDetailPanel(config);
		
		this.configurationObject.tabItems = [ this.detailPanel ];
    }

	,
	initDetailPanel : function(config) {
		
		//START list of detail fields
		var idField = {
			name				: 'id'
			, hidden			: true
			, value				: 0
		};
		
		var nameField = {
			maxLength 			: 100
			, minLength 		: 1
			, fieldLabel 		: LN('sbi.generic.name')
			, allowBlank 		: false
			, validationEvent	: true
			, name				: 'name'
		}; 
		
		var descrField = {
				maxLength 			: 500
				, fieldLabel 		: LN('sbi.generic.descr')
				, allowBlank 		: true
				, name				: 'description'
		};
		
		this.uploadField = new Ext.form.TextField({
			inputType				: 'file'
			, fieldLabel			: LN('sbi.generic.upload')
			, allowBlank			: true
		});
		
		this.versionsGridPanel = new Sbi.widgets.CatalogueVersionsGridPanel({
			services 	: {
				'getVersionsService' 		: config.singleItemServices.getVersionsService
				, 'deleteVersionsService' 	: config.singleItemServices.deleteVersionsService
				, 'downloadVersionService' 	: config.singleItemServices.downloadVersionService
			}
        	, height		: 200
		});
		
		//END list of detail fields
	 	   
		this.detailPanel = new Ext.Panel({
			title 		: LN('sbi.generic.details')
			, layout 	: 'fit'
			, items : [{
					columnWidth		: 0.4
					, xtype			: 'fieldset'
					, labelWidth	: 110
					, defaults		: { width: 220, border : false }  
					, defaultType	: 'textfield'
					, autoHeight	: true
					, autoScroll  	: true
					, bodyStyle		: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;'
					, border		: false
					, style			: {
								"margin-left" 	: "20px" 
								, "margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-20px" : "-23px") : "20"  
					}
					, items: [ idField , nameField , descrField , this.uploadField ]
				}
				, {
					style			: {
						"margin-left" 	: "20px" 
						, "margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-20px" : "-23px") : "20"  
					}
					//, height		: 400
					, items 		: [this.versionsGridPanel]
				}
				
			]
		});
		
	}
	
	,
	addNewItem : function() {
		Sbi.widgets.Catalogue.superclass.addNewItem.call(this);
		this.versionsGridPanel.getStore().removeAll();
		this.uploadField.reset();
	}
	
	,
	save : function() {
		this.validate(this.doSave, this.showValidationErrors, this);
    }
	
	,
	doSave : function () {
		
		// a multipart form cannot contain parameters on its main URL; they must POST parameters
		// therefore we have to split save service url into base url and parameters
		var completeUrl = this.services['saveItemService'];
		var baseUrl = completeUrl.substr(0, completeUrl.indexOf("?"));
		var queryStr = completeUrl.substr(completeUrl.indexOf("?") + 1);
		var params = Ext.urlDecode(queryStr);
		
		var activeVersionRecord = this.versionsGridPanel.getCurrentActiveRecord();
		if (activeVersionRecord != null) {
			params['active_content_id'] = activeVersionRecord.get('id');
		}
		
        var form = this.getForm();
	    form.submit({
	         url : baseUrl  // a multipart form cannot contain parameters on its main URL; they must POST parameters
	         , params : params
	         , waitMsg : LN('sbi.generic.wait')
	         , success : this.doSaveHandler
	         , failure : function (form, action) {
	        	 Sbi.exception.ExceptionHandler.showErrorMessage(action.result.msg, LN('sbi.generic.serviceError'));
	         }
	         , scope : this
	    });
	}
	
	,
	doSaveHandler : function (form, action) {
		var success = (action.result && action.result.success) ? (action.result.success == true) : false;
		if (success) {
			Ext.Msg.show({
				   title: LN('sbi.generic.ok')
				   , msg: LN('sbi.generic.result')
				   , buttons: Ext.Msg.OK
				   , icon: Ext.MessageBox.INFO
			});
			this.uploadField.reset();
			this.commitChangesInList(action.result.msg);
		} else {
			var message = (action.result && action.result.msg) ? action.result.msg : LN('sbi.generic.error.msg');
			Sbi.exception.ExceptionHandler.showErrorMessage(message, LN('sbi.generic.error'));
		}
	}
	
	,
	getFormState : function () {
		var state = this.getForm().getFieldValues();
		if (!state['id']) {
			state['id'] = 0;
		}
		return state;
	}
	
	,
	doValidate : function () {
		// returns an array of errors; if no error returns an empty array
		var toReturn = new Array();
		var values = this.getForm().getFieldValues();
		var name = values['name'];
		if (name.trim() == '') {
			toReturn.push(LN('sbi.generic.validation.missingName'));
		}
		return toReturn;
	}
	
	,
	commitChangesInList : function ( serverResponseText ) {
		var responseContent = Ext.util.JSON.decode( serverResponseText );
		if (this.isNewItem()) {
			var values = this.getFormState();
			var id = responseContent.id;
			values['id'] = id;
			var newRecord = new Ext.data.Record( values );
			this.mainElementsStore.add(newRecord);
			this.rowselModel.selectLastRow(true);
		} else {
			var values = this.getFormState();
			var record = this.getRecordById( values['id'] );
			record.set( 'name', values['name'] );
			record.set( 'description', values['description'] );
			this.mainElementsStore.commitChanges();
			this.versionsGridPanel.getStore().load({ params : { id : values['id'] } });
		}
	}
	
	,
	getRecordById : function ( id ) {
		var length = this.mainElementsStore.getCount();
		for ( var i = 0 ; i < length ; i++ ) {
   	        var aRecord = this.mainElementsStore.getAt(i);
   	        if (aRecord.data.id == id ){
   	        	return aRecord;
			}
   	    }
		return null;
	}
	
	,
	isNewItem : function () {
		var values = this.getFormState();
		return values['id'] == 0;
	}
	
	,
	validate: function (successHandler, failureHandler, scope) {
		var errorArray = this.doValidate();
		if ( errorArray.length > 0 ) {
			return failureHandler.call(scope || this, errorArray);
		} else {
			return successHandler.call(scope || this);	
		}
	}
	
    ,
    showValidationErrors : function(errorsArray) {
    	var errMessage = '';
    	
    	for (var i = 0; i < errorsArray.length; i++) {
    		var error = errorsArray[i];
    		errMessage += error + '<br>';
    	}
    	
    	Sbi.exception.ExceptionHandler.showErrorMessage(errMessage, LN('sbi.generic.error'));
    }

});
