/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Search Form
 * 
 *     
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */


Ext.define('Sbi.social.analysis.search.view.SocialAnalysisSearchForm', {
	extend: 'Ext.form.Panel',
	
	bodyPadding: '10 0 10 20',
	frame: true,
	
	config:{
		
	},
	
	/**
     * @property {Sbi.social.analysis.search.store.StartingFromStore} startingFromStore
     *  Store useful for the startingFromComboBox
     */
	startingFromStore: null,
	
	/**
     * @property {Sbi.social.analysis.search.store.UpToStore} upToStore
     *  Store useful for the upToComboBox
     */
	upToStore: null,
	


	constructor : function(config) {
		this.initConfig(config||{});

		this.repeatTypeStore = Ext.create('Sbi.social.analysis.search.store.RepeatTypeStore',{});
		this.upToStore = Ext.create('Sbi.social.analysis.search.store.UpToStore',{});
		
		this.addEvents(
		        /**
		         * @event searchSubmit
		         * User submit a search
		         */
		        'searchSubmit',
		        
		        /**
		         * @event refreshGrids
		         * Refresh the search grids
		         */
		        'refreshGrids'
				);

		
		this.callParent(arguments);
	},

	initComponent: function() {
		
		var searchForm = this;
		
//		alert(Sbi.user.functionalities[Sbi.createsocialanalysis]);
		
		Ext.apply(this, {

	     // The fields
	        items: [
	        {
	        	//container for search type and options
	        	padding: "0 0 0 20",
	        	xtype      : 'fieldcontainer',
	            fieldLabel: LN('sbi.social.analysis.searchType'),
	            layout: 'hbox',
//	            disabled: this.noCreateSocialAnalysis,
	            items: [
	                {
	                	xtype	  :	'radiofield',
	                    boxLabel  : LN('sbi.social.analysis.onlinemonitoring'),
	                    name      : 'searchType',
	                    inputValue: 'streamingAPI',
	                    id        : 'radioStreaming',
	                    checked	  : true
	                }, 
	                {
						xtype	  :	'radiofield',
					    boxLabel  : LN('sbi.social.analysis.historicaldata'),
					    name      : 'searchType',
					    inputValue: 'searchAPI',
					    id        : 'radioHSearch',
					    margin	  : '0 0 0 20',
					    baseCls: 'css-checkbox',
					    listeners: 
					    {
					        change: function (field, newValue, oldValue) 
					        {
					        	var form = field.up('form');
					            var startingFromContainer = form.down('#startingFromID');
					            var repeatEveryContainer = form.down('#repeatEveryID');
					            var startingFromCheckBox = startingFromContainer.down('#checkboxStartingFromID');
					            var repeatEveryCheckBox = repeatEveryContainer.down('#checkboxRepeatID');
					        
					            if(newValue) {
					            	startingFromContainer.enable();
					            	repeatEveryContainer.enable();
					            }
					            else {
					            	startingFromCheckBox.setValue(false);
					            	repeatEveryCheckBox.setValue(false);                            	
					            	startingFromContainer.disable();
					            	repeatEveryContainer.disable();
					            }
					        }
				    	}
            	 	},
            	 	{
	                	padding: '0 0 0 20',
						xtype:'fieldcontainer',
					    layout: 
					    {
					    	type: 'hbox',
					    },
					    disabled: true,
					    id: 'startingFromID',
					    items:
					    [
						    {
						    	xtype: 'checkboxfield',
						    	boxLabel  : LN('sbi.social.analysis.startingfrom'),
						        name      : 'isStartingFrom',
						        inputValue: '1',
						        id        : 'checkboxStartingFromID',
						        listeners: 
						        {
						            change: function (field, newValue, oldValue, eOpts ) 
						            {
						            	var form = field.up('form');
						                var numberField = form.down('#numberStartingFromID');
						                var labelAgoStartingFrom = form.down('#labelAgoStartingFromID');
						                
						        
						                if(newValue) {
						                    numberField.enable();
						                    labelAgoStartingFrom.setVisible(true);
						                }
						                else {
						                    numberField.reset();
						                    numberField.disable();
						                    labelAgoStartingFrom.setVisible(false);
						                }
						            }
						        }
					    	},
						    {
						    	xtype: 'numberfield',
						    	width: 40,
						        name      : 'numberStartingFrom',
						        id: 'numberStartingFromID',
						        padding: '0 0 0 20',
						        minValue: 1,
						        value: 1,
						        maxValue: 6,
						        disabled: true
						    },                
							{
						    	xtype: 'displayfield',
						    	value: LN('sbi.social.analysis.dayago'),
						    	margin: '0 0 0 10',
						    	hidden: true,
						    	id: 'labelAgoStartingFromID'
							}
						]
				    },
					{
					   padding: '0 0 0 20',
					   xtype:'fieldcontainer',
					   layout: 'hbox',  
					   id: 'repeatEveryID',
					   disabled: true,
					   items :[
					   {
						   	xtype: 'checkboxfield',
						   	boxLabel  : LN('sbi.social.analysis.repeatevery'),
						   	name      : 'isRepeating',
						   	inputValue: '1',
						   	id        : 'checkboxRepeatID',
						   	listeners: {
					            change: function (field, newValue, oldValue, eOpts ) {
					            	var form = field.up('form');
					                var numberField = form.down('#numberRepeatEveryID');
					                var repeatEvery = form.down('#repeatTypeCombo');
					        
					                if(newValue) {
					                	numberField.enable();
					                	repeatEvery.enable();
					                }
					                else {
					                	numberField.reset();
					                	numberField.disable();
					                	repeatEvery.setValue(repeatEvery.store.first().data.type);
					                	repeatEvery.disable();
					
					                }
					            }
					        }
				   		},
					   	{
							xtype: 'numberfield',
					    	width: 40,
					        name      : 'numberRepeat',
					        padding: '0 0 0 19',
					        id : 'numberRepeatEveryID',
					        value: 1,
					        minValue: 1,
					        disabled: true
					   },
		        	   {
		            		xtype: 'combo',
		            		editable: false,
		            		store: this.repeatTypeStore, 
		            		queryMode: 'local', 
		            		displayField: 'name',  
		            		valueField: 'type',
		            		padding: '0 0 0 10',
		            		name: 'repeatType',
		            		id: 'repeatTypeCombo',
		            		disabled: true,
		            		margin: '0 0 0 10',
		            		listeners: {
				                render: function (field) {
				                    field.setValue(field.store.first().data.type);
				                }
				             }
		        	   }
					   ]
					}
	                	 	
            	]},                             
               {
	            xtype: 'fieldcontainer',
	        	defaultType: 'textfield',
	        	layout: 'anchor',
	        	padding: '0 0 0 20',
	        	fieldDefaults: {
	                msgTarget: 'side',
	                labelWidth: 75
	            },
//	            disabled: this.noCreateSocialAnalysis,
	        	items: [
	        	{
	        		fieldLabel: LN('sbi.social.analysis.logicalidentifier'),
	        		name: 'label',
	        		anchor: '30%',
	        		enforceMaxLength: true,
	        		maxLength: 100
	        	}, 
	        	{
	        		xtype:'fieldcontainer',
	        		anchor: '50%',
					layout: 'hbox',
					items: 
					[
					 	{
			        		fieldLabel: LN('sbi.social.analysis.keywords'),
			        		xtype: 'textfield',
			        		name: 'keywords',
			        		id: 'keywordsId',
			        		allowBlank: false,
			        		invalidCls: 'x-form-invalid-field',
			        		enforceMaxLength: true,
			        		flex: 1,
//			        		maxLength: 200,
			        		regexText: LN('sbi.social.analysis.keywordsfieldalertmessage'),
			        		regex: /^([#]?[a-zA-Z0-9_-]+)(,\s*[#]?[a-zA-Z0-9_-]+){0,4}$/
					 	},
					 	{
		                	xtype	  :	'radiofield',
		                    boxLabel  : LN('sbi.social.analysis.and'),
		                    name      : 'booleanOperator',
		                    inputValue: 'AND',
		                    id        : 'radioAND',
		                    margin	  : '0 0 0 20',
		                    checked	  : true
		                }, 
		                {
		                	xtype	  :	'radiofield',
		                    boxLabel  : LN('sbi.social.analysis.or'),
		                    name      : 'booleanOperator',
		                    inputValue: 'OR',
		                    id        : 'radioOR',
		                    margin	  : '0 0 0 20'		                    
		                },
		                {
		                	xtype	  :	'radiofield',
		                    boxLabel  : LN('sbi.social.analysis.free'),
		                    name      : 'booleanOperator',
		                    inputValue: 'FREE',
		                    id        : 'radioFree',
		                    margin	  : '0 0 0 20',
		                    listeners: 
		                    {
						        change: function (field, newValue, oldValue) 
						        {
						        	var form = field.up('form');
						            var keywordsfield = form.down('#keywordsId');
						            if(newValue)
						            {
						            	keywordsfield.regex = '';
						            }
						            else
						            {
						            	keywordsfield.regex = /^([#]?[a-zA-Z0-9_-]+)(,\s*[#]?[a-zA-Z0-9_-]+){0,4}$/;
						            }
						        }
		                    }
		                }
		        	]
	        	}],
	        },
	        {
                xtype: 'fieldset',
                title: LN('sbi.social.analysis.advancedoptions'),
                collapsible: true,
                collapsed: true,
                defaults: {
                    labelWidth: 89,
                    layout: {
                        type: 'anchor',
                        defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
                    }
                },
	        	anchor: '50%',
                items: [
                    {
                        xtype: 'fieldcontainer',
                        items: [
                            {xtype: 'checkboxfield',    name: 'ranalysis', boxLabel  : LN('sbi.social.analysis.ranalysis')},
                        ]
                    }]
            },
	        {
	        	xtype: 'fieldcontainer',
	            defaultType: 'checkboxfield',
	            layout: 'hbox',
	            padding: '0 0 0 20',
	            items: [
	                {
	                    boxLabel  : LN('sbi.social.analysis.twitter'),
	                    name      : 'socialType',
	                    inputValue: '1',
	                    disabled  : true,
	                    checked   : true,
	                    id        : 'checkboxTwitter',
	                    padding: '0 0 0 20',
	                }, {
	                    boxLabel  : LN('sbi.social.analysis.facebook'),
	                    name      : 'socialType',
	                    inputValue: '2',
	                    disabled   : true,
	                    id        : 'checkboxFacebook',
	                    padding: '0 0 0 20'
	                },
	                {
	                    boxLabel  : LN('sbi.social.analysis.linkedin'),
	                    name      : 'socialType',
	                    inputValue: '3',
	                    disabled   : true,
	                    id        : 'checkboxLinkedin',
	                    padding: '0 0 0 20'
	                },
//	                {
//	                	xtype: 'fieldcontainer',
//	                	fieldLabel: 'Advanced',
//	                    boxLabel  : LN('sbi.social.analysis.ranalysis'),
//	                    name      : 'rAnalysis',
//	                    inputValue: '1',
//	                    id        : 'checkboxRAnalysis',
//	                }
	            ]
	        },
	        {
	            xtype: 'fieldcontainer',
	        	defaultType: 'textfield',
	        	layout: 'anchor',
	        	defaults:
	        	{
	        		labelWidth: 150,
	        	},
	        	padding: '0 0 0 20',
	        	fieldDefaults: {
	                msgTarget: 'side',
	                labelWidth: 75
	            },
//	            disabled: this.noCreateSocialAnalysis,
	        	items: [
	        	{
	        		fieldLabel: LN('sbi.social.analysis.accountstomonitor'),
	        		name: 'accounts',
	        		anchor: '50%',
	        		invalidCls: 'x-form-invalid-field',
	        		enforceMaxLength: true,
	        		maxLength: 500,
	        		regexText: LN('sbi.social.analysis.accountstomonitorfieldalertmessage'),
	        		regex: /^([@][a-zA-Z0-9_-]+)(,\s*[@][a-zA-Z0-9_-]+){0,2}$/
	        	}, {
	        		fieldLabel: LN('sbi.social.analysis.resourcestomonitor'),
	        		name: 'links',
	        		anchor: '50%',
	        		invalidCls: 'x-form-invalid-field',
	        		enforceMaxLength: true,
	        		maxLength: 500,
	        		regexText: LN('sbi.social.analysis.resourcestomonitorfieldalertmessage'),
	        		regex: /^((http:\/\/bit.ly\/)[a-zA-Z0-9_-]+)(,\s*(http:\/\/bit.ly\/)[a-zA-Z0-9_-]+){0,2}$/
	        	}, {
	        		fieldLabel: LN('sbi.social.analysis.documentstomonitor'),
	        		name: 'documents',
	        		anchor: '50%',
	        		invalidCls: 'x-form-invalid-field',
	        		enforceMaxLength: true,
	        		maxLength: 500,
	        		regexText: LN('sbi.social.analysis.documentstomonitorfieldalertmessage'),
	        		regex: /^([a-zA-Z0-9_-]+)(,\s*[a-zA-Z0-9_-]+){0,2}$/
//	        		hidden: true
	        	}],
	        },
	        {
	        	xtype: 'fieldcontainer',
	        	layout: 'hbox',
	        	defaults:
	        	{
	        		labelWidth: 150,
	        	},
	        	padding: '0 0 0 20',
	        	items :[
                 {
                	xtype: 'numberfield',
                 	width: 40,
                 	value: 0,
                 	minValue: 0,
                	fieldLabel: LN('sbi.social.analysis.upto'),
                    name      : 'numberUpTo',
                    width: 200
                },                   
        	   {
            		xtype: 'combo',
            		editable: false,
            		store: this.upToStore, 
            		queryMode: 'local', 
            		displayField: 'name',  
            		valueField: 'type',
            		padding: '0 0 0 10',
            		name: 'typeUpTo',
            		id: 'typeUpToID',
            		width: 100,
            		listeners: {
		                render: function (field) {
		                    field.setValue(field.store.first().data.type);
		                }
		             }
        	   },
               {
        		   xtype: 'displayfield',
        		   value: 'later',
        		   margin: '0 0 0 10'
               },
	       	   {
					xtype: 'numberfield',
					fieldLabel: LN('sbi.social.analysis.frequency'),
			    	labelWidth: 50,
			        name: 'monitorFrequencyValue',
			        padding: '0 0 0 19',
			        value: 1,
			        minValue: 1,
			        width: 110,
			        labelPad: 20
			   },
		   	   {
		       		xtype: 'combo',
		       		editable: false,
		       		store: this.repeatTypeStore, 
		       		queryMode: 'local', 
		       		displayField: 'name',  
		       		valueField: 'type',
		       		padding: '0 0 0 10',
		       		name: 'monitorFrequencyType',
		       		margin: '0 0 0 10',
		       		width: 100,
		       		id: 'monitorFrequencyTypeID',
		       		listeners: {
		                render: function (field) {
		                    field.setValue(field.store.first().data.type);
		                }
		             }
		   	   },
		   	{
	        		xtype: 'button',
	        		text: LN('sbi.social.analysis.search'),
	        		margin: '0, 0, 0, 20',
	        		width: 55,
	        		handler: function()
	        		{
	        			var form = this.up('form').getForm() // get the basic form
	        	        var record = form.getRecord(),
	        	        values = form.getFieldValues();
	        	        form.updateRecord(record);
	        	        if(values.searchType == 'searchAPI')
	        	        {
	        	        	form.submit({
	        	        		//
	        	        		url: 'restful-services/historicalSearch',
	        	        			
	        	        		success: function(form, action) 
	        	        		{
	        	        			Ext.Msg.alert('Success', action.result.msg);
	        	        			searchForm.fireEvent('searchSubmit'); 
	        	        			form.reset();
	        	        			 
	        	        			var searchType = form.findField('repeatTypeCombo');
	        	        			searchType.setValue(searchType.store.first().data.type);
	        	        			
	        	        			var monitorType = form.findField('monitorFrequencyTypeID');
	        	        			monitorType.setValue(monitorType.store.first().data.type);
	        	        			
	        	        			var upToType = form.findField('typeUpToID');
	        	        			upToType.setValue(upToType.store.first().data.type);
	        	        			
	        	        	    },
	        	        	    failure: function(form, action) 
	        	        	    {
	        	        	    	Ext.Msg.alert('Failure', action.result.msg);
	        	        	    	searchForm.fireEvent('searchSubmit'); 
	        	        	    	form.reset();
	        	        	    	
	        	        	    	var searchType = form.findField('repeatTypeCombo');
	        	        			searchType.setValue(searchType.store.first().data.type);
	        	        			
	        	        			var monitorType = form.findField('monitorFrequencyTypeID');
	        	        			monitorType.setValue(monitorType.store.first().data.type);
	        	        			
	        	        			var upToType = form.findField('typeUpToID');
	        	        			upToType.setValue(upToType.store.first().data.type);
	        	        	    }
	        	        	
	        	        	});
	        	        	
	        	        }
	        	        else if(values.searchType == 'streamingAPI')
	        	        {
	        	        	Ext.Msg.show({
	        	        	     title:'Confirm',
	        	        	     msg: 'Would you like to enable now your stream? If you have another enabled stream, this will stop it',
	        	        	     buttons: Ext.Msg.YESNO,
	        	        	     icon: Ext.Msg.QUESTION,
	        	        	     fn: function(btn, text){
	        	                     if (btn == 'yes'){
	        	                    	 form.submit({
	        		                    		url: 'restful-services/streamingSearch/createEnabledStream',
	        		                    	
	        		                    		success: function(form, action) 
	        		                    		{
	        		                    			Ext.Msg.alert('Success', action.result.msg);
	        		                    			searchForm.fireEvent('searchSubmit'); 
	        		                    			
	        		                    			form.reset();
	        		                    			
	        		                    			var searchType = form.findField('repeatTypeCombo');
	        		                    			searchType.setValue(searchType.store.first().data.type);
	        		                    			
	        		                    			var monitorType = form.findField('monitorFrequencyTypeID');
	        		                    			monitorType.setValue(monitorType.store.first().data.type);
	        		                    			
	        		                    			var upToType = form.findField('typeUpToID');
	        		                    			upToType.setValue(upToType.store.first().data.type);
	        		                    	    },
	        		                    	    failure: function(form, action) 
	        		                    	    {
	        		                    	    	Ext.Msg.alert('Failure', action.result.msg);
	        		                    	    	searchForm.fireEvent('searchSubmit'); 
	        		                    	    	
	        		                    	    	form.reset();
	        	                    	    	
	        		                    	    	var searchType = form.findField('repeatTypeCombo');
	        		                    			searchType.setValue(searchType.store.first().data.type);
	        		                    			
	        		                    			var monitorType = form.findField('monitorFrequencyTypeID');
	        		                    			monitorType.setValue(monitorType.store.first().data.type);
	        		                    			
	        		                    			var upToType = form.findField('typeUpToID');
	        		                    			upToType.setValue(upToType.store.first().data.type);
	        		                    	    }
	        		                    	    
	        		                    	});
	        	                      
	        	                     }
	        	                     if (btn == 'no'){
	        	                    	 form.submit({
	        		                    		url: 'restful-services/streamingSearch/createDisabledStream',
	        		                    	
	        		                    		success: function(form, action) 
	        		                    		{
	        		                    			Ext.Msg.alert('Success', action.result.msg);   
	        		                    			searchForm.fireEvent('searchSubmit'); 
	        		                    			
	        		                 				form.reset();
	        		                    	    	
	        		                    	    	var searchType = form.findField('repeatTypeCombo');
	        		                    			searchType.setValue(searchType.store.first().data.type);
	        		                    			
	        		                    			var monitorType = form.findField('monitorFrequencyTypeID');
	        		                    			monitorType.setValue(monitorType.store.first().data.type);
	        		                    			
	        		                    			var upToType = form.findField('typeUpToID');
	        		                    			upToType.setValue(upToType.store.first().data.type);
	        		                    	    },
	        		                    	    failure: function(form, action) 
	        		                    	    {
	        		                    	    	Ext.Msg.alert('Failure', action.result.msg);
	        		                    	    	searchForm.fireEvent('searchSubmit'); 
	        		                    	    	
	        		                 	    		form.reset();
	        		                    	    	
	        		                    	    	var searchType = form.findField('repeatTypeCombo');
	        		                    			searchType.setValue(searchType.store.first().data.type);
	        		                    			
	        		                    			var monitorType = form.findField('monitorFrequencyTypeID');
	        		                    			monitorType.setValue(monitorType.store.first().data.type);
	        		                    			
	        		                    			var upToType = form.findField('typeUpToID');
	        		                    			upToType.setValue(upToType.store.first().data.type);
	        		                    	    }
	        		                    	    
	        		                    	});
	        	                     }
	        	                 },
	        	        	     icon: Ext.Msg.QUESTION
	        	        	});
	        	        	
	        	        }
	        			
	        		}
	           },
//	           {
//			   		xtype: 'button',
//			   		text: 'Refresh',
//			   		margin: '0, 0, 0, 20',
//			   		handler: function() {
//			               
//			              searchForm.fireEvent('refreshGrids');                   			
//			   
//			         }                   
//	       		}
               ]
	       }],
	    });
		
		this.noCreateSocialAnalysis();
		
		this.callParent();
		
		Ext.TaskManager.start({
			  run: function(){
				  searchForm.fireEvent('refreshGrids');
			  },
			  interval: 60*1000
			});
	},
	
	noCreateSocialAnalysis: function()
	{
		
		var isDisabled = true;
		
		for(var i = 0; i < Sbi.user.functionalities.length; i++)
		{
			if(Sbi.user.functionalities[i]==Sbi.createsocialanalysis)
			{
				isDisabled = false;
				break;
			}
		}
		
		this.disabled = isDisabled;
	}

});

