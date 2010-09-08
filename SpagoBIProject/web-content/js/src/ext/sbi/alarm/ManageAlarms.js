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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Chiara Chiarelli
 */
Ext.ns("Sbi.alarms");

Sbi.alarms.ManageAlarms = function(config) { 
	var paramsList = { MESSAGE_DET: "ALARMS_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "ALARM_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "ALARM_DELETE"};
	var paramsTresholds = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "TRESHOLDS_LIST"};
	
	this.services = new Array();
	this.services['manageAlarmsList'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ALARMS_ACTION'
		, baseParams: paramsList
	});
	this.services['saveAlarmService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ALARMS_ACTION'
		, baseParams: paramsSave
	});
	
	this.services['deleteAlarmService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ALARMS_ACTION'
		, baseParams: paramsDel
	});	
	
	this.services['loadTresholdsService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ALARMS_ACTION'
		, baseParams: paramsTresholds
	});	
	this.initStores(config);

	this.initManageAlarms();
	
	Ext.getCmp('alarmsgrid').store.on('load', function(){
		 var grid = Ext.getCmp('alarmsgrid');
		 
		 if(this.alarmsStore.getTotalCount()>0){
			 grid.fireEvent('rowclick', grid, 0);
			 grid.getSelectionModel().selectRow(0);
		 }
	 }, this, {
	 single: true
    });
	
	this.alarmsStore.load();
 
    Ext.getCmp('alarmsgrid').on('delete', this.deleteSelectedAlarm, this);	
};

Ext.extend(Sbi.alarms.ManageAlarms, Ext.FormPanel, {
	  gridForm:null
	, alarmsStore:null
	, kpiStore: null
	, thresholdsStore: null
	, contactsStore: null
	
	, colModel:null
	, typeData: null
	, buttons: null
	
	, detailItems: null
	, detailTab: null
    , kpiTab: null
    , contactsTab: null
	, tabs: null
	
	, contactsGridPanel: null
	, contactsGrid: null
	, smContacts: null
	
	, alarmsEmptyStore: null
	, kpisEmptyStore: null
	, contactsEmptyStore: null

	, initStores: function (config) {
	
		this.alarmsStore = new Ext.data.JsonStore({
	    	autoLoad: false  
	    	,fields: ['id'
	    			  , 'name'
	    	          , 'description'
	    	          , 'label'
	    	          , 'modality'
	    	          , 'singleEvent'
	    	          , 'autoDisabled'
	    	          , 'text'
	    	          , 'url'
	    	          , 'contacts'
	    	          , 'kpi'
	    	          , 'threshold'
	    	          ]
	    	, root: 'samples'
			, url: this.services['manageAlarmsList']			
		});	
		
		this.kpiStore = new Ext.data.SimpleStore({
			autoLoad: false 
			, data: config.kpisEmptyList
	        , fields : [ 'id', 'kpiName', 'kpiModel' ]
	    });
	    
	    this.thresholdsStore = new Ext.data.JsonStore({
	    	id: 'id',
	    	root: 'samples',
	        fields : ['id', 'label']
	    });
	    
	    this.contactsStore = new Ext.data.SimpleStore({
	    	id: 'id',
	        fields : [ 'id', 'name', 'email', 'mobile', 'resources' ]
	    });
	    
	    this.kpisEmptyStore = config.kpisEmptyList;
	    this.tresholdsEmptyStore = config.tresholdsList;
	    this.contactsEmptyStore = config.contactsEmpyList;
  
    }
    
  ,initTabs: function(){
  
      this.detailTab = new Ext.Panel({
		        title: LN('sbi.alarms.details')
		        , id: 'detail'
		        , layout: 'fit'
		        , items: {
		 		   	     id: 'alarm-detail',   	              
		 		   	     columnWidth: 0.4,
			             xtype: 'fieldset',
			             labelWidth: 90,
			             defaults: {width: 140, border:false},    
			             defaultType: 'textfield',
			             autoHeight: true,
			             autoScroll  : true,
			             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
			             border: false,
			             style: {
			                 "margin-left": "10px", 
			                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
			             },
			             items: [{
			                 name: 'id',
			                 hidden: true
			             },{
			                 fieldLabel: LN('sbi.alarms.alarmLabel'),
			                 name: 'label',
			                 width : 250,
			                 allowBlank: false,
			                 validationEvent:true,
	  		            	 maxLength:50,
	  		            	 minLength:1,
	  		            	 //regex : new RegExp("^([a-zA-Z1-9_\x2D])+$", "g"),
	  		            	 regexText : LN('sbi.users.wrongFormat')
			             },{
			                 fieldLabel:  LN('sbi.alarms.alarmName'),
			                 name: 'name',
			                 width : 250,
			                 allowBlank: false,
			                 validationEvent:true,
	  		            	 maxLength:50,
	  		            	 minLength:1,
	  		            	 //regex : new RegExp("^([a-zA-Z0-9_\x2D\s\x2F])+$", "g"),
	  		            	 regexText : LN('sbi.users.wrongFormat')
			             },{
			                 fieldLabel: LN('sbi.alarms.alarmDescr'),
			                 width : 250,
			                 name: 'description',
			                 allowBlank: true,
			                 validationEvent:true,
	  		            	 maxLength:200
			             },{
				            xtype: 'radiogroup',
				            itemId: 'modality',
				            name: 'mod',
				            boxMinWidth  : 50,
				            boxMinHeight  : 100,
				            fieldLabel: LN('sbi.alarms.alarmModality'),
				            items: [
				             		{boxLabel: LN('sbi.alarms.MAIL'),id:'mail',name: 'modality', inputValue: 1, checked: true},
							        {boxLabel: LN('sbi.alarms.SMS'),id:'sms',name: 'modality', inputValue: 2}	
				            ]
				         },new Ext.form.CheckboxGroup({
				            xtype: 'checkboxgroup',
				            itemId: 'options',
				            columns: 2,
				            boxMinWidth  : 200,
				            boxMinHeight  : 100,
				            hideLabel  : false,
				            fieldLabel: LN('sbi.alarms.options'),
				            items: [
				                {boxLabel: LN('sbi.alarms.alarmSingleEvent'), name: 'singleEvent', checked:false},
				                {boxLabel: LN('sbi.alarms.alarmAutoDisabled'), name: 'autoDisabled', checked:false}
				            ]
			             }),{
			                 fieldLabel:  LN('sbi.alarms.alarmMailUrl'),
			                 width : 250,
			                 name: 'url',
			                 allowBlank: true,
			                 validationEvent:true,
	  		            	 maxLength:20
			             },{
			                 fieldLabel:  LN('sbi.alarms.alarmMailText'),
			                 xtype: 'textarea',
			                 width : 250,
			                 height : 80,
			                 name: 'text',
			                 allowBlank: true,
			                 validationEvent:true,
	  		            	 maxLength:1000
		             }]
		    	}
		    });
		    
		    
      	 this.kpiCheckColumn = new Ext.grid.CheckboxSelectionModel( {header: ' ',singleSelect: true, scope:this, dataIndex: 'id'} );
		 this.kpiCheckColumn.on('rowselect', this.onKpiSelect, this);
		 
	     this.kpiCm = new Ext.grid.ColumnModel({
	         // specify any defaults for each column
	         defaults: {
	             sortable: true // columns are not sortable by default           
	         },
	         columns: [
	             {
	                 id: 'id',
	                 header: LN('sbi.alarms.kpiInstanceIdHeader'),
	                 dataIndex: 'id',
	                 width: 120
	             }, {
	                 header: LN('sbi.alarms.kpiModelHeader'),
	                 dataIndex: 'kpiModel',
	                 width: 130
	             }, {
	                 header: LN('sbi.alarms.kpiNameHeader'),
	                 dataIndex: 'kpiName',
	                 width: 120
	             },
	             this.kpiCheckColumn // the plugin instance
	         ]
	     });
		this.kpiGrid = new Ext.grid.EditorGridPanel ({
			id: 'kpi-grid',
			store: this.kpiStore,
			autoHeight : true,
			cm: this.kpiCm,
			sm: this.kpiCheckColumn,
			frame: true,
			//plugins: this.kpiCheckColumn,
            viewConfig : {
	            forceFit : true,
	            scrollOffset : 2
	        // the grid will never have scrollbars
	        },
	        singleSelect : true,
	        scope:this,
	        clicksToEdit : 2

		}); 
		this.tresholdsCombo = new Ext.form.ComboBox(
			{
				 id: 'tresholds-combo',
                 fieldLabel:  LN('sbi.alarms.alarmKpiThreshold'),                 
                 name: 'alarmKpiThreshold',
                 width: 200,
	             store: this.thresholdsStore,
	             forceReload:true,
	             displayField:'label',
	             valueField: 'id',
	             typeAhead: true,
	             mode: 'local',
	             triggerAction: 'all',
	             emptyText:'Select a treshold...',
	             selectOnFocus:true
             }
		);
		
      	this.kpiTab = new Ext.Panel({
		        title: LN('sbi.alarms.kpis')
		        , id : 'alarmKpi'
		        , layout: 'form'
		        , autoScroll: true
		        , itemId: 'kpis'
		        , scope: this
	            , bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;'
			    , border: false
		        , items: [
		            this.kpiGrid
		        	,this.tresholdsCombo
		        	]
		    });
		
		this.initContactsGridPanel();
		    
        this.contactsTab = new Ext.Panel({
		        title: LN('sbi.alarms.contacts')
		        , autoScroll: true
		        , id : 'contactsList'
	            , items : [ this.contactsGridPanel ]
		        , itemId: 'contacts'
		        , layout: 'fit'
				, autoWidth: true
		    });
		    
    }
    
     , initContactsGridPanel : function() {
       
    	this.smContacts = new Ext.grid.CheckboxSelectionModel( {header: ' ',singleSelect: false, scope:this, dataIndex: 'id'} );
		
        this.cmContacts = new Ext.grid.ColumnModel([
	         //{id:'id',header: "id", dataIndex: 'id'},
	         {header: LN('sbi.alarmcontact.name'), width: 45, sortable: true, dataIndex: 'name'},
	         {header: LN('sbi.alarmcontact.email'), width: 45, sortable: true, dataIndex: 'email'},
	         {header: LN('sbi.alarmcontact.mobile'), width: 45, sortable: true, dataIndex: 'mobile'},
	         {header: LN('sbi.alarmcontact.resources'), width: 45, sortable: true, dataIndex: 'resources'},
	         this.smContacts 
	    ]);

		this.contactsGridPanel = new Ext.grid.GridPanel({
			  store: this.contactsStore
			, id: 'contacts-form'
			//NB: Important trick!!!to render the grid with activeTab=0	
			, renderTo: Ext.getBody()
   	     	, cm: this.cmContacts
   	     	, sm: this.smContacts
   	     	, frame: false
   	     	, border:false 
   	     	, height: 450
   	     	, collapsible:false
   	     	, loadMask: true
   	     	, viewConfig: {
   	        	forceFit:true
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	}
			, scope: this
		});
		
		Ext.getCmp("contacts-form").on('recToSelect', function(roleId, index){		
			Ext.getCmp("contacts-form").selModel.selectRow(index,true);
		});

		this.doLayout();
	}
    
	,onKpiSelect: function(){
		//loads tresholds
		var sm = this.kpiGrid.getSelectionModel();
		var row = sm.getSelected();
	
		this.kpiInstId = row.data.id;
		this.thresholdsStore.removeAll();
		this.tresholdsCombo.clearValue();
		
		Ext.Ajax.request({
	          url: this.services['loadTresholdsService'],
	          params: {id: this.kpiInstId},
	          method: 'GET',
	          success: function(response, options) {
	          	
				if (response !== undefined) {		
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined) {	     				
	      				this.tresholdsCombo.getStore().loadData(content);
	      				this.tresholdsCombo.getStore().commitChanges();
	      			}
				 } 	
	          }
	          ,scope: this
	    });
	}
	
	,initManageAlarms: function(){

	    this.deleteColumn = new Ext.grid.ButtonColumn({
	       header:  ' ',
	       dataIndex: 'id',
	       iconCls: 'icon-remove',
	       clickHandler: function(e, t) {
	          var index = Ext.getCmp("alarmsgrid").getView().findRowIndex(t);
	          
	          var selectedRecord = Ext.getCmp("alarmsgrid").store.getAt(index);
	          var userId = selectedRecord.get('id');
	          Ext.getCmp("alarmsgrid").fireEvent('delete', userId, index);
	       }
	       ,width: 25
	       ,renderer : function(v, p, record){
	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	       }
        });
       
        this.colModel = new Ext.grid.ColumnModel([
         {header: LN('sbi.alarms.alarmLabel'), width: 150, sortable: true, dataIndex: 'label'},
         {header: LN('sbi.alarms.alarmName'), width: 150, sortable: true, dataIndex: 'name'},
         this.deleteColumn
        ]);    	   

	    this.tbSave = new Ext.Toolbar({
 	    	buttonAlign : 'right', 	    	
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.alarms.update'),
 	            iconCls: 'icon-save',
 	            handler: this.save,
 	            width: 30,
 	            id: 'save-btn',
 	            scope: this
 	        })
 	    	]
 	    });	    

 	   this.initTabs();	  
 	    
 	   this.tabs = new Ext.TabPanel({
           enableTabScroll : true
           , activeTab : 0
           , autoScroll : true
           , width: 450
           , height: 450
           , deferredRender: false
           , itemId: 'tabs'
           , tbar: this.tbSave 
		   , items: [ this.detailTab
		   , this.kpiTab
		   , this.contactsTab
		   ]
		 });

	    this.tb = new Ext.Toolbar({
 	    	buttonAlign : 'right',
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.alarms.add'),
 	            iconCls: 'icon-add',
 	            handler: this.addNewAlarm,
 	            width: 30,
 	            scope: this
 	        })
 	    	]
 	    });
 	
 	    
   	   this.gridForm = new Ext.FormPanel({
   	          id: 'alarm-form',
   	          frame: true,
   	          labelAlign: 'left',
   	          title: LN('sbi.alarms.manageAlarms'),
   	          bodyStyle:'padding:5px',
   	          width: 850,
   	          layout: 'column',
   	          scope: this,
   	          items: [{
   	              columnWidth: 0.90,
   	              scope: this,
   	              layout: 'fit',
   	              items: {
   	        	  	  id: 'alarmsgrid',
   	                  xtype: 'grid',
   	                  ds: this.alarmsStore,   	                  
   	                  cm: this.colModel,
   	                  scope:this,
   	                  plugins: this.deleteColumn,
   	                  sm: new Ext.grid.RowSelectionModel({   	                  	  
   	                      singleSelect: true,
   	                      scope: this,   	                   
	   	                  fillContacts : function(row, rec) {	 
							Ext.getCmp("contacts-form").store.removeAll();
	   	                   	var tempArr = rec.data.contacts;
	   	                  	var length = rec.data.contacts.length;
	   	                  	for(var i=0;i<length;i++){
	   	                  		var tempRecord = new Ext.data.Record({"id":tempArr[i].id, "name":tempArr[i].name, 
	   	                  											  "email":tempArr[i].email, "mobile":tempArr[i].mobile, "resources":tempArr[i].resources});
							    Ext.getCmp("contacts-form").store.addSorted(tempRecord);
							    Ext.getCmp("contacts-form").store.commitChanges();
							    Ext.getCmp("contacts-form").selModel.unlock();
							    if(tempArr[i].checked){
							    	var contactId = tempRecord.get('id');				    	
							    	Ext.getCmp("contacts-form").fireEvent('recToSelect', contactId, i);
							    }
	   	                  	}		   	                  	
	   	                  },
	   	                  fillOptions : function(row, rec) {	 
	   	                  	var singleEvent = rec.get('singleEvent');
	   	                  	var autoDisabled = rec.get('autoDisabled');
	   	                  	var modality = rec.get('modality');
	   	                  	
	   	                  	Ext.getCmp("detail").items.each(function(item){	  
	   	                  		if(item.getItemId() == 'alarm-detail'){ 
	   	                  		  item.items.each(function(item){	  
		   	                  		   if(item.getItemId() == 'options'){
		   	                  		   		item.setValue('singleEvent', singleEvent);
		   	                  		   		item.setValue('autoDisabled', autoDisabled);
		   	                   		   }else if(item.getItemId() == 'modality'){
			   	                   		  	if(modality =='SMS'){
					   	                  		item.onSetValue( 'sms',true);
					   	                  	}else{
					   	                  		item.onSetValue( 'mail',true);
					   	                  	}
		   	                   		   }
		   	                   	   });
	   	                  		}  
	   	                   	});	      
	   	                  },

	   	                  fillKpis : function(row, rec) {	
		   	         		Ext.getCmp("kpi-grid").store.removeAll();
		   	         		var tempAttrArr = config.kpisEmptyList;
		   	         		var length = tempAttrArr.length;
		   	         		for(var i=0;i<length;i++){
		   	         			var tempRecord = new Ext.data.Record({"kpiName":tempAttrArr[i].kpiName, "kpiModel":tempAttrArr[i].kpiModel,"id":tempAttrArr[i].id });
		   	         			Ext.getCmp("kpi-grid").store.add(tempRecord);
							    Ext.getCmp("kpi-grid").store.commitChanges();
							    Ext.getCmp("kpi-grid").selModel.unlock();
		   	         			if(tempAttrArr[i].id === rec.data.kpi){
		   	         				var checkedArr = [];
		   	         				checkedArr.push(tempRecord);		   	         				
		   	         				Ext.getCmp("kpi-grid").getSelectionModel().selectRecords(checkedArr, false);	
		   	         			}
		   	         		 }
		   	         		 var paramsTresholds = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "TRESHOLDS_LIST"};
		   	         		 var loadThr = Sbi.config.serviceRegistry.getServiceUrl({
								serviceName: 'MANAGE_ALARMS_ACTION'
								, baseParams: paramsTresholds
							 });	
		   	         				
							var sm = Ext.getCmp("kpi-grid").getSelectionModel();
							var row = sm.getSelected();
							if(row){
								var kpiInstId = row.data.id;
								Ext.getCmp("tresholds-combo").store.removeAll();
								Ext.getCmp("tresholds-combo").clearValue();
								
								Ext.Ajax.request({
							          url: loadThr,
							          params: {id: kpiInstId},
							          method: 'GET',
							          success: function(response, options) {   	
										if (response !== undefined) {		
							      			var content = Ext.util.JSON.decode( response.responseText );
							      			if(content !== undefined) {	     				
							      				Ext.getCmp("tresholds-combo").store.loadData(content);
							      				Ext.getCmp("tresholds-combo").store.commitChanges();
										   	    Ext.getCmp("tresholds-combo").setValue(rec.data.threshold);	
							      			}
										 } 	
							          }
							          ,scope: this
							    });
						    }
	   	                  },

   	                      listeners: {
   	                          rowselect: function(sm, row, rec) {   
   	                          	  Ext.getCmp('save-btn').enable();
   	                              Ext.getCmp("alarm-form").getForm().loadRecord(rec);  	
   	                              this.fillOptions(row, rec);   	                              	 
   	                	  		  this.fillKpis(row, rec);                                 	                              
   	                              this.fillContacts(row, rec); 	                                    	                              
   	                          }
   	                      }
   	                  }),
   	                  height: 450,
   	                  width: 400,
   	                  title:LN('sbi.alarms.alarmsList'),
   	                  tbar: this.tb,
   	                  border: true
  	                 ,listeners: {
   	                      viewready: function(g) {
   	                      	  g.getView().refresh();
	   	                      g.fireEvent('rowclick', g, 0);
							  g.getSelectionModel().selectRow(0); 	                      
   	                      }  
   	                  }
   	              }
   	          }, this.tabs
   	          ],
   	          renderTo: Ext.getBody()
   	      });

	}
	
	,save : function() {
		   
	   var values = this.gridForm.getForm().getValues();

       var newRec = null;
       var idRec = values['id'];

	   var params = {
	      	name : values['name'],
	      	description : values['description'],
	      	text : values['text'],
	      	url : values['url'],
	      	label : values['label'] 
	   }
       params.id = values['id'];
       
       var mod = 'MAIL';
       if(values['modality']==2){
          params.modality ='SMS';
          mod =	'SMS';       
       }else{
       	  params.modality ='MAIL';
       }
       
       var autoDis = false;
       if(values['autoDisabled']=='on'){
          params.autoDisabled = true;	 
          autoDis = true;         
       }else{
       	  params.autoDisabled = false;
       }
       
       var singleEv = false;
       if(values['singleEvent']=='on'){
          params.singleEvent = true;     
          singleEv = true;         
       }else{
       	  params.singleEvent = false;
       }

      var contactsSelected = Ext.getCmp("contacts-form").selModel.getSelections();
      var lengthR = contactsSelected.length;
      var contacts =new Array();
      for(var i=0;i<lengthR;i++){
        var contact ={'name':contactsSelected[i].get("name"),'id':contactsSelected[i].get("id"),'mobile':contactsSelected[i].get("mobile"),'resources':contactsSelected[i].get("resources")
        			 ,'email':contactsSelected[i].get("email"),'checked':true};
		contacts.push(contact);
      }
      params.contacts =  Ext.util.JSON.encode(contacts);

      var alarmContacts = new Array();
      var tempArr = Ext.getCmp("contacts-form").store;
      var length = Ext.getCmp("contacts-form").store.data.length;

      for(var i=0;i<length;i++){
        var selected = false;
        for(var j=0;j<lengthR;j++){
        	if(contactsSelected[j].get("id")===tempArr.getAt(i).get("id")){
        		selected = true;
        		var contact ={'name':tempArr.getAt(i).get("name"),'id':tempArr.getAt(i).get("id"),'mobile':tempArr.getAt(i).get("mobile"),'resources':tempArr.getAt(i).get("resources")
        						,'email':tempArr.getAt(i).get("email"),'checked':true};
				alarmContacts.push(contact);
        		break;
        	}
        }
        if(!selected){
        	var contact ={'name':tempArr.getAt(i).get("name"),'id':tempArr.getAt(i).get("id"),'mobile':tempArr.getAt(i).get("mobile"),'resources':tempArr.getAt(i).get("resources")
        						,'email':tempArr.getAt(i).get("email"),'checked':false};
			alarmContacts.push(contact);
		}
       }	
        
      //kpi
       var kpiSelected = Ext.getCmp("kpi-grid").getSelectionModel().getSelected();
	   var noKpi = true;
       var kpiId;
       if(kpiSelected == undefined || kpiSelected == null ){
       		noKpi = true;
       }else{
       	   noKpi = false;
	       kpiId = kpiSelected.get("id");
	       params.kpi = kpiId;
	   }
	  
	   var noThr = true;
	   var thrId = Ext.getCmp("tresholds-combo").value;
	   if(thrId == undefined || thrId == null || thrId ==''){
	   		noThr = true;
       }else{
       	  noThr = false;
	       //threshold       	      
	      params.threshold = thrId;  
	   }

   	   if(idRec ==0 || idRec == null || idRec === ''){
	       newRec =new Ext.data.Record({'name': values['name'],'description': values['description'],'text':values['text']
	       							   ,'url': values['url'],'label': values['label'],'modality':mod,'autoDisabled':autoDis,'singleEvent':singleEv
	       							   ,'contacts': alarmContacts,'kpi': kpiId,'threshold': thrId});	       
	   }else{
			var record;
			var length = this.alarmsStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.alarmsStore.getAt(i);
	   	        if(tempRecord.data.id==idRec ){
	   	        	record = tempRecord;
				}			   
	   	    }	
			record.set('name',values['name']);
			record.set('description',values['description']);
			record.set('text',values['text']);
			record.set('url',values['url']);
			record.set('modality',mod);
			record.set('autoDisabled',autoDis);
			record.set('singleEvent',singleEv);			
			record.set('contacts', alarmContacts);
			record.set('kpi', kpiId);
			record.set('threshold', thrId);         
	  }
	  
	  if(noKpi || noThr){
	    Ext.MessageBox.confirm(
		     	LN('sbi.alarms.confirm'),
	            LN('sbi.alarms.noThrOrKpiI'),            
	            function(btn, text) {
	                if (btn=='yes') {
	                	Ext.Ajax.request({
				          url: this.services['saveAlarmService'],
				          params: params,
				          method: 'GET',
				          success: function(response, options) {
							if (response !== undefined) {		
					      		if(response.responseText !== undefined) {
					      			var content = Ext.util.JSON.decode( response.responseText );
					      			if(content.responseText !== 'Operation succeded') {
					                    Ext.MessageBox.show({
					                        title: LN('sbi.alarms.error'),
					                        msg: content,
					                        width: 150,
					                        buttons: Ext.MessageBox.OK
					                   });
					      			}else{
					      			    
										var idTemp = content.id;
										if(newRec!==null){
											newRec.set('id', idTemp);
											this.alarmsStore.add(newRec);
										}
										this.contactsStore.commitChanges();
										this.alarmsStore.commitChanges();		
										if(newRec!==null){
											var grid = Ext.getCmp('alarmsgrid');
								            grid.getSelectionModel().selectLastRow(true);
							            }
										Ext.MessageBox.show({
							                        title: LN('sbi.attributes.result'),
							                        msg: 'Operation succeded',
							                        width: 200,
							                        buttons: Ext.MessageBox.OK
							                });	
					      			 }
						      	}else{
						      		Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
						      	}
							}else{
								Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving User', 'Service Error');
							}
					      },
				          failure: function(response) {
				     		if(response.responseText !== undefined) {
				     			var content = Ext.util.JSON.decode( response.responseText );
				     			var errMessage ='';
							for (var count = 0; count < content.errors.length; count++) {
								var anError = content.errors[count];
				       			if (anError.localizedMessage !== undefined && anError.localizedMessage !== '') {
				       				errMessage += anError.localizedMessage;
				       			} else if (anError.message !== undefined && anError.message !== '') {
				       				errMessage += anError.message;
				       			}
				       			if (count < content.errors.length - 1) {
				       				errMessage += '<br/>';
				       			}
							}
				               Ext.MessageBox.show({
				                   title: LN('sbi.alarms.validationError'),
				                   msg: errMessage,
				                   width: 400,
				                   buttons: Ext.MessageBox.OK
				              });
				     		}else{
				               Ext.MessageBox.show({
				                   title:LN('sbi.alarms.error'),
				                   msg: 'Error in Saving User',
				                   width: 150,
				                   buttons: Ext.MessageBox.OK
				              });
				     		}
				          }
				          ,scope: this
				       });	
	                }
	            },
	            this
			);  
		}else{
			Ext.Ajax.request({
	          url: this.services['saveAlarmService'],
	          params: params,
	          method: 'GET',
	          success: function(response, options) {
				if (response !== undefined) {		
		      		if(response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content.responseText !== 'Operation succeded') {
		                    Ext.MessageBox.show({
		                        title: LN('sbi.alarms.error'),
		                        msg: content,
		                        width: 150,
		                        buttons: Ext.MessageBox.OK
		                   });
		      			}else{
		      			    
							var idTemp = content.id;
							if(newRec!==null){
								newRec.set('id', idTemp);
								this.alarmsStore.add(newRec);
							}
							this.contactsStore.commitChanges();
							this.alarmsStore.commitChanges();	
							if(newRec!==null){
								var grid = Ext.getCmp('alarmsgrid');
					            grid.getSelectionModel().selectLastRow(true);
				            }
				            	
							Ext.MessageBox.show({
				                        title: LN('sbi.attributes.result'),
				                        msg: 'Operation succeded',
				                        width: 200,
				                        buttons: Ext.MessageBox.OK
				                });		
		      			 }
			      	}else{
			      		Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			      	}
				}else{
					Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving User', 'Service Error');
				}
		      },
	          failure: function(response) {
	     		if(response.responseText !== undefined) {
	     			var content = Ext.util.JSON.decode( response.responseText );
	     			var errMessage ='';
				for (var count = 0; count < content.errors.length; count++) {
					var anError = content.errors[count];
	       			if (anError.localizedMessage !== undefined && anError.localizedMessage !== '') {
	       				errMessage += anError.localizedMessage;
	       			} else if (anError.message !== undefined && anError.message !== '') {
	       				errMessage += anError.message;
	       			}
	       			if (count < content.errors.length - 1) {
	       				errMessage += '<br/>';
	       			}
				}
	               Ext.MessageBox.show({
	                   title: LN('sbi.alarms.validationError'),
	                   msg: errMessage,
	                   width: 400,
	                   buttons: Ext.MessageBox.OK
	              });
	     		}else{
	               Ext.MessageBox.show({
	                   title:LN('sbi.alarms.error'),
	                   msg: 'Error in Saving User',
	                   width: 150,
	                   buttons: Ext.MessageBox.OK
	              });
	     		}
	          }
	          ,scope: this
	       });	
		}   
    }
	, addNewAlarm : function(){
		Ext.getCmp('save-btn').enable();

		var emptyRecToAdd =new Ext.data.Record({id:0, 
											name:'', 
											description:'',
											label:'',
											modality:'MAIL',
											singleEvent: false,
											autoDisabled: false,
											text:'',
											url: '',
											contacts: []
											// 'kpi'
							    	        //, 'thr'
							    	        //, 'doc'
											});
	
		Ext.getCmp('alarm-form').getForm().loadRecord(emptyRecToAdd);
		Ext.getCmp("detail").items.each(function(item){	  
	   		if(item.getItemId() == 'alarm-detail'){ 
	   		  item.items.each(function(item){	  
	    		   if(item.getItemId() == 'options'){
	    		   		item.setValue('singleEvent', false);
	    		   		item.setValue('autoDisabled', false);
	     		   }else if(item.getItemId() == 'modality'){
		      		  	item.onSetValue( 'mail',true);
	     		   }
	     	   });
	   		}  
    	});	
		
		Ext.getCmp("kpi-grid").store.removeAll();
	   	var tempAttrArr = this.kpisEmptyStore;
	   	var length = this.kpisEmptyStore.length;
        for(var i=0;i<length;i++){
        	var tempRecord = new Ext.data.Record({"kpiName":tempAttrArr[i].kpiName, "kpiModel":tempAttrArr[i].kpiModel,"id":tempAttrArr[i].id });
    		Ext.getCmp("kpi-grid").store.add(tempRecord);	
        }		
        Ext.getCmp("kpi-grid").store.commitChanges();
        this.tresholdsCombo.clearValue();
        
        
        Ext.getCmp("contacts-form").store.removeAll();
        var tempContactsArr = this.contactsEmptyStore;
        var length2 = this.contactsEmptyStore.length;
        for(var i=0;i<length2;i++){
          	var tempRecord = new Ext.data.Record({"id":tempContactsArr[i].id, "name":tempContactsArr[i].name, 
	   	                  						  "email":tempContactsArr[i].email, "mobile":tempContactsArr[i].mobile, "resources":tempContactsArr[i].resources});
			Ext.getCmp("contacts-form").store.add(tempRecord);								   
        }	
		
		Ext.getCmp('alarm-form').doLayout();
	}
	
	, deleteSelectedAlarm: function(userId, index) {
		Ext.MessageBox.confirm(
		     LN('sbi.alarms.confirm'),
            LN('sbi.alarms.confirmDelete'),            
            function(btn, text) {
                if (btn=='yes') {
                	if (userId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteAlarmService'],
				            params: {'ID': userId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {
									//this.thresholdsStore.load();
									var sm = Ext.getCmp('alarmsgrid').getSelectionModel();
									var deleteRow = sm.getSelected();
									this.alarmsStore.remove(deleteRow);
									this.alarmsStore.commitChanges();
									var grid = Ext.getCmp('alarmsgrid');
									if(this.alarmsStore.getCount()>0){
										grid.getSelectionModel().selectRow(0);
										grid.fireEvent('rowclick', grid, 0);
									}else{
										this.addNewAlarm();
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage('Error while deleting Alarm', 'Service Error');
								}
				            },
				            failure: function() {
				                Ext.MessageBox.show({
				                    title: LN('sbi.alarms.error'),
				                    msg: 'Error in deleting Alarm',
				                    width: 150,
				                    buttons: Ext.MessageBox.OK
				               });
				            }
				            ,scope: this
			
						});
					} else {
						Sbi.exception.ExceptionHandler.showWarningMessage('Operation failed', 'Warning');
					}
                }
            },
            this
		);
	}

});

Ext.reg('managealarms', Sbi.alarms.ManageAlarms);
