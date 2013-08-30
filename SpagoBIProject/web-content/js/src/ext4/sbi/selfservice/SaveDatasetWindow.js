/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.selfservice.SaveDatasetWindow', {
	extend: 'Ext.Window'

	,
	config: {	
		queryDefinition : {}
		, categoriesStore: null
		, scopeStore: null
		, title : LN('sbi.execution.toolbar.savedatasetwindow.title')
		, bodyPadding : 10
		, layout : 'fit'
	}
	

	,
	constructor: function(config) {
		
		//this.initConfig(config);
		
		this.initServices();
		this.categoriesStore = this.createCategoriesStore();
		this.scopeStore = this.createScopeStore();

		config.items = this.initItems();
		//config.buttons = this.initWizardBar();
		
		this.callParent(arguments);
	}
	
//	,
//	initConfig : function (config) {
//
//	}
	
	
	,
	initServices : function() {
		this.services = [];

		this.services["getCategories"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'domainsforfinaluser/listValueDescriptionByType',
			baseParams: {}
		});
	}
	
	,
	initItems : function () {
		
		var form = Ext.create('Ext.form.Panel', {
		    width: 350,

		    // The form will submit an AJAX request to this URL when submitted
		    //url: 'save-form.php',

		    // Fields will be arranged vertically, stretched to full width
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'
		    },

		    // The fields
		    defaultType: 'textfield',
		    items: [{
		        fieldLabel: LN('sbi.generic.label'),
		        name: 'label',
		        allowBlank: false
		    },{
		        fieldLabel: LN('sbi.generic.name'),
		        name: 'name',
		        allowBlank: false
		    },{
		        fieldLabel: LN('sbi.generic.description'),
		        name: 'description'
		    }]

		    // Reset and Submit buttons
//		    buttons: [{
//		        text: 'Reset',
//		        handler: function() {
//		            this.up('form').getForm().reset();
//		        }
//		    }, {
//		        text: 'Submit',
//		        formBind: true, //only enabled once the form is valid
//		        disabled: true,
//		        handler: function() {
//		            var form = this.up('form').getForm();
//		            if (form.isValid()) {
//		                form.submit({
//		                    success: function(form, action) {
//		                       Ext.Msg.alert('Success', action.result.msg);
//		                    },
//		                    failure: function(form, action) {
//		                        Ext.Msg.alert('Failed', action.result.msg);
//		                    }
//		                });
//		            }
//		        }
//		    }],
//		    renderTo: Ext.getBody()
		});
		
		return form;
//		var fields = this.initFields();
//		var widget = Ext.create("Sbi.widgets.wizard.WizardWindow", {});
//		return widget.createStepFieldsGUI(this.fields);
	}
	
	,
	initFields : function () {
		var toReturn = [];
		
		toReturn = [{label:"Id", name:"id",type:"text",hidden:"true"},
	     {label: LN('sbi.generic.label'), name:"label", type:"text", mandatory:true}, 
	     {label: LN('sbi.generic.name'), name:"name", type:"text", mandatory:true},
	     {label: LN('sbi.generic.description'), name:"description", type:"textarea"},
		 {label: LN('sbi.generic.catType'), name:"catTypeVn", type:"combo", valueCol:"VALUE_ID", descCol:"VALUE_DS", data:this.categoriesStore},
		 {label: LN('sbi.generic.scope'), name:"isPublic", type:"combo", valueCol:"field", descCol:"value", data:this.scopeStore}];
		
		return toReturn;
	}
	
	,
	createCategoriesStore: function(){
		Ext.define("CategoriesModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var categoriesStore=  Ext.create('Ext.data.Store',{
    		model: "CategoriesModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"CATEGORY_TYPE"},
    			url:  this.services['getCategories'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	categoriesStore.load();
    	
    	return categoriesStore;
	}
	
	,
	createScopeStore : function () {
		var store = Ext.create('Ext.data.Store', {
		    fields: ['field', 'value'],
		    data : [
		        {"field":"true", "value":"Public"},
		        {"field":"false", "value":"Private"}
		    ]
		});
		return store;
	}
	
});

/*
Sbi.execution.toolbar.SaveDatasetWindow = function(config) {

	// init properties...
	var defaultSettings = {
		// public
		title : LN('sbi.execution.toolbar.savedatasetwindow.title')
		, layout : 'fit'
		, width : 640
		, height : 450
		, closeAction : 'close'
		, frame : true
		// private
	};

	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.toolbar && Sbi.settings.execution.toolbar.documentexecutionpagetoolbar) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.toolbar.documentexecutionpagetoolbar);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	this.addEvents('save');
	
	this.initServices();
	
	this.initForm();
	
	var c = Ext.apply({}, config, {
		buttons : [{ 
			  iconCls: 'icon-save' 	
			, handler: this.saveDatasetHandler
			, scope: this
			, text: LN('sbi.generic.update')
           }]
		, items : this.datasetForm
	});   
	
    Sbi.execution.toolbar.SaveDatasetWindow.superclass.constructor.call(this, c);
    
};


Ext.extend(Sbi.execution.toolbar.SaveDatasetWindow, Ext.Window, {
	
	datasetForm : null
	, queryDefinition : null
	, services: null
    , mainPanel: null
    , generalConfFields: null
	, cronConfFields: null
    , generalInfoFieldSet: null
    , hourlyOptionsFieldSet: null
    , dailyOptionsFieldSet: null
    , weeklyOptionsFieldSet: null
    , monthlyOptionsFieldSet: null
	
	,
	initServices: function() {
		this.services = new Array();
		
		var params = {
			LIGHT_NAVIGATOR_DISABLED: 'TRUE'
		};

		this.services['saveDatasetService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_DATASET_USER_ACTION'
			, baseParams: params
		});
		
		this.services['getDatasourcesService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_DATASOURCES_LIST_USER_ACTION'
			, baseParams: params
		});
	}
	
	,
	initForm: function () {
		
		this.labelField = new Ext.form.TextField({
	        name : 'label'
	        , allowBlank : false
	        , inputType : 'text'
	        , maxLength : 50
	        , anchor : '95%'
			, fieldLabel : LN('sbi.generic.label')  
	    });
		
		this.nameField = new Ext.form.TextField({
	        name : 'label'
	        , allowBlank : false
	        , inputType : 'text'
	        , maxLength : 50
	        , anchor : '95%'
			, fieldLabel : LN('sbi.generic.name')  
	    });
		
		this.descriptionField = new Ext.form.TextArea({
	        name : 'description'
	        , allowBlank : true
	        , inputType : 'text'
	        , maxLength : 160
	        , height : 80
	        , anchor : '95%'
			, fieldLabel : LN('sbi.generic.descr')  
	    });
		
		this.datasourceField = new Ext.form.ComboBox({
			name : 'datasource'
			, store : new Ext.data.JsonStore({
			    url: this.services['getDatasourcesService']
			    , root: 'rows'
			    , fields: ['id', 'label', 'description']
			})
			, width : 160
			, fieldLabel : LN('sbi.execution.toolbar.savedatasetwindow.fields.datasource')
			, displayField : 'label'
			, valueField : 'label'
			, forceSelection : true
			, triggerAction : 'all'
			, selectOnFocus : true
			, editable : false
			, allowBlank : false
			, validationEvent : true
			, frame : true
		});
	    
    	this.generalInfoFieldSet = new Ext.form.FieldSet({
            collapsible: true,
            collapsed: false,
            title: 'General info',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            items : [this.nameField, this.descriptionField, this.datasourceField] // removed this.labelField
    	});
		
		this.initCronConfFiledSet();
		
	    this.datasetForm = new Ext.FormPanel({
	        columnWidth: 0.6
	        , frame : true
	        , autoScroll : true
	        , items: {
	 		   	 columnWidth : 0.4
	             , xtype : 'fieldset'
	             , labelWidth : 80
	             //, defaults : { border : false }
	             , defaultType : 'textfield'
	             , autoHeight : true
	             , autoScroll : true
	             , bodyStyle : Ext.isIE ? 'padding:0 0 5px 5px;' : 'padding:0px 5px;'
	             , border : false
	             , style : {
	                 //"margin-left": "4px",
	                 //"margin-top": "10px"
	             }
	             , items : [this.generalInfoFieldSet
	                        , new Ext.Panel({html : '<label class="x-form-item" style="margin-bottom:10px;">Settings for data refreshing:</label>'})
	        				, this.hourlyOptionsFieldSet
	        				, this.dailyOptionsFieldSet
	        				, this.weeklyOptionsFieldSet
	        				, this.monthlyOptionsFieldSet]
	    	}
	    });
	    
	}
	
	,
	getFormState : function() {      
      	var formState = {};
      	formState.label = ''; //this.labelField.getValue();
      	formState.name = this.nameField.getValue();
      	formState.description = this.descriptionField.getValue();
      	formState.dataSourcePersist = this.datasourceField.getValue();
      	return formState;
    }
	
	,
	saveDatasetHandler: function () {
		
		var params = this.getInfoToBeSentToServer();
		Ext.MessageBox.wait(LN('sbi.generic.wait'));
		Ext.Ajax.request({
	        url : this.services['saveDatasetService']
	        , params : params
	        , success : this.datasetSavedSuccessHandler
	        , scope : this
			, failure : Sbi.exception.ExceptionHandler.handleFailure      
		});

	}
	
	,
	getInfoToBeSentToServer : function () {
		var formState = this.getFormState();
		formState.qbeJSONQuery = Ext.util.JSON.encode(this.queryDefinition.queries);
		formState.qbeDataSource = this.queryDefinition.datasourceLabel;
		formState.isPersisted = true;
		formState.isFlatDataset = false;
		formState.sourceDatasetLabel = this.queryDefinition.sourceDatasetLabel;
		return formState;
	}
	
	,
	datasetSavedSuccessHandler : function (response , options) {
  		if (response !== undefined && response.responseText !== undefined) {
  			var content = Ext.util.JSON.decode( response.responseText );
  			if (content.responseText !== 'Operation succeded') {
                Ext.MessageBox.show({
                    title: LN('sbi.generic.error'),
                    msg: content,
                    width: 150,
                    buttons: Ext.MessageBox.OK
               });
      		} else {			      			
      			Ext.MessageBox.show({
                        title: LN('sbi.generic.result'),
                        msg: LN('sbi.generic.resultMsg'),
                        width: 200,
                        buttons: Ext.MessageBox.OK
                });
      			this.fireEvent('save', this, this.getFormState());
      		}  
  		} else {
  			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
  		}  	
	}
	
    ,
    initCronConfFiledSet: function() {
    	this.cronConfFields = {};
    	this.initHourlyConfFieldSet();
    	this.initDailyConfFieldSet();
    	this.initWeeklyConfFieldSet();
    	this.initMonthlyConfFieldSet();
    }
	
    ,
    initHourlyConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['hourly'] = [];
    	
    	field = new Ext.form.NumberField({
    		fieldLabel: 'Every n houres',
            name: 'houres',
            allowBlank:false
    	});
    	this.cronConfFields['hourly'].push(field);
    	
    	this.hourlyOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Hourly',
            name: 'hourly',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['hourly']
    	});
    	this.hourlyOptionsFieldSet.on('expand', this.onExpand, this);
    }
    
    ,
    initDailyConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['daily'] = [];
    	
    	field = new Ext.form.NumberField({
    		fieldLabel: 'Every n days',
            name: 'days',
            allowBlank:false
    	});
    	this.cronConfFields['daily'].push(field);
    	
    	this.dailyOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Daily',
            name: 'daily',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['daily']
    	});	
    	this.dailyOptionsFieldSet.on('expand', this.onExpand, this);
    }
    
    ,
    initWeeklyConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['weekly'] = [];
    	
    	var dayOfTheWeekName = ['Monday', 'Tuesday', 'Wednesday'
    	                    , 'Thursday', 'Friday', 'Saturday'
    	                    , 'Sunday'];
    	
    	var dayOfTheWeekId = ['MON', 'TUE', 'WED'
        	                  , 'THU', 'FRI', 'SAT'
        	                  , 'SUN'];
    	
    	field = new Ext.form.Checkbox({
    		fieldLabel: 'In day',
    		boxLabel: dayOfTheWeekName[0],
    		data: dayOfTheWeekId[0],
            name: 'inDays',
            allowBlank:false
    	});
    	this.cronConfFields['weekly'].push(field);
    	
    	for(var i = 1; i < dayOfTheWeekName.length; i++)  {
	    	field = new Ext.form.Checkbox({
	    		boxLabel: dayOfTheWeekName[i],
	    		data: dayOfTheWeekId[i],
	    		fieldLabel: '',
	            labelSeparator: '',
	            name: 'inDays',
	            allowBlank:false
	    	});
	    	this.cronConfFields['weekly'].push(field);
    	}
    	
    	
    	this.weeklyOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Weekly',
            name: 'weekly',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['weekly']
    	});	
    	this.weeklyOptionsFieldSet.on('expand', this.onExpand, this);
    }
    
    ,
    initMonthlyConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['monthly'] = [];
    	
    	field = new Ext.form.NumberField({
    		fieldLabel: 'In day [1-31]',
    		minValue: 0,
    		maxValue: 31,    		
            name: 'inDay',
            allowBlank:false
    	});
    	this.cronConfFields['monthly'].push(field);
    	
    	this.monthlyOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Monthly',
            name: 'monthly',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['monthly']
    	});	
    	this.monthlyOptionsFieldSet.on('expand', this.onExpand, this);
    }
    
    ,
    onExpand: function(fieldSet) {
    	if(this.activeFieldSet) this.activeFieldSet.collapse();
    	this.activeFieldSet = fieldSet;
    }
	
});
 */