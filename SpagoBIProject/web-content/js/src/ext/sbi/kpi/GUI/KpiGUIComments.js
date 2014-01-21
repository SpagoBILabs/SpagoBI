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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIComments =  function(config) {
		
		var defaultSettings = { //autoScroll: true, 
								//height: 500,
								layout: 'fit',
								border:false,
								style:'padding: 5px;'
							};
		
		var execId = config.SBI_EXECUTION_ID;
		var paramsList = {SBI_EXECUTION_ID: execId, LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "COMMENTS_LIST"};
		var paramsSave = {SBI_EXECUTION_ID: execId, LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "SAVE_COMMENT"};
		var paramsDel = {SBI_EXECUTION_ID: execId, LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "DELETE_COMMENT"};
		
		var c = Ext.apply(defaultSettings, config || {});
		
		this.services = new Array();
		
		this.services['commentsList'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_COMMENTS'
			, baseParams: paramsList
		});
		this.services['commentSave'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_COMMENTS'
			, baseParams: paramsSave
		});
		this.services['commentDelete'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_COMMENTS'
			, baseParams: paramsDel
		});
		this.store = new Ext.data.JsonStore({
	    	autoLoad: false    	  
	    	, id : 'id'		
	        , fields: ['owner'
         	          , 'creationDate'
          	          , 'comment'
          	          , 'id'
          	          , 'binId'
          	          ]
	    	, root: 'comments'
			, url: this.services['commentsList']		
		});

		Ext.apply(this, c);
		
		this.initComments(c);
   
		Sbi.kpi.KpiGUIComments.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.kpi.KpiGUIComments , Ext.form.FormPanel, {
	editor: null,
	listPanel: null,
	kpiInstId: null,
	saveBtn: null,
	selectedField: null,
	commentId: null,
	owner: null,

	initComments: function(c){

		var h = c.height;
		
		this.rowselModel = new Ext.grid.RowSelectionModel({
	          singleSelect: true
	          
	    });
		
		
		var deleteButtonBaseConf = {
			header:  ' '
			, iconCls: 'icon-remove'
			, width: 25
			, scope: this
			, loggedUser: this.loggedUser
		};
		
		Sbi.debug("[KpiGUIComments.initComponents]: user [" + this.loggedUser + "] can delete all notes [" + c.canDelete + "]");
		Sbi.debug("[KpiGUIComments.initComponents]: user [" + this.loggedUser + "] can edit personal notes [" + c.canEditPersonal + "]");
		Sbi.debug("[KpiGUIComments.initComponents]: user [" + this.loggedUser + "] can edit all notes [" + c.canEditAll + "]");
		
		if(c.canDelete === true) {
			deleteButtonBaseConf.renderer = function(v, p, record){
				return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	  	    };
	  	    Sbi.debug("[KpiGUIComments.initComponents]: added delete button to all records");
		} else if(c.canEditPersonal === true || c.canEditAll === true) {
			deleteButtonBaseConf.renderer = function(v, p, record){
				Sbi.debug("[KpiGUIComments.initComponents]: [" + record.get('owner') +"] === [" + this.loggedUser + "] ? " + (record.get('owner') === this.loggedUser));
				if(record.get('owner') === this.loggedUser) {
					Sbi.debug("[KpiGUIComments.initComponents]: delete button added during rendering to record [" + record.get('comment') + "]");
					return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
				} else {
					Sbi.debug("[KpiGUIComments.initComponents]: delete button not added during rendering to record [" + record.get('comment') + "]");
					 return '&nbsp;';
				}
	  	    };
	  	    Sbi.debug("[KpiGUIComments.initComponents]: added delete button only to personal record");
		} else {
			deleteButtonBaseConf.renderer = function(v, p, record){
   	           return '&nbsp;';
   	       }
			Sbi.debug("[KpiGUIComments.initComponents]: delete button not added");
		}
        
		 this.deleteColumn = new Ext.grid.ButtonColumn(deleteButtonBaseConf);
       
 
       
	    this.listPanel = new Ext.grid.GridPanel({
	        store: this.store,
	        minWidth: 400,
	        //height: 200,
	        //maxHeight: 200,
	        //autoScroll: true,
	        viewConfig:{forceFit:true},
	        selModel: this.rowselModel,
	     	hideHeaders : true,
	     	layout: 'fit',
	     	scope: this,
	     	listeners: {
	        	'cellclick':{
	        		fn: function(grid, rowIndex, columnIndex, e) {
	        		    var record = grid.getStore().getAt(rowIndex);  // Get the Record
	        		    var fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
	        		    var data = record.get(fieldName);
	        		    if(fieldName != undefined){
		    				this.editor.setValue(record.data.comment);
		    				this.commentId=record.data.id;	
		    				this.owner= record.data.owner;
	        		    }else{
	        		    	//delete button
	        		    	this.deleteItem( record.data.id, columnIndex);
	        		    }

	        		},
	        		scope:this
	        	}
	        },
	        columns: [{
	            header: 'Owner',       
	            dataIndex: 'owner',
	            tooltip : 'Ownner'
	        },{
	            header: 'Creation Date',
	            dataIndex: 'creationDate',
	            tpl: '{lastmod:date("m-d h:i a")}',
	            tooltip : 'Creation Date'
	        }
/*	        ,{
	            header: 'Last Modified',
	            dataIndex: 'lastModificationDate',
	            tpl: '{lastmod:date("m-d h:i a")}'
	        }*/
	        ,{
	            header: 'Comment',
	            dataIndex: 'comment',
	            align: 'right',
	            tooltip : 'Text Comment'
	        },
	        this.deleteColumn
	        ]
	        ,fbar: [{
	            text: 'Save comment'
	            , handler: this.saveComment
	            , scope: this
	        }]
	    });
	
	  this.editor = new Ext.form.HtmlEditor({
		  	enableSourceEdit: false
		  	, width: 500
		  	, height: 200
		  	, autoScroll: true
		  	, style:'align: center;'
		  	, layout: 'fit'
    
	  }); 

	  this.setAutoScroll(true);
	  this.items =[this.listPanel,  this.editor];
	  
	}
	
	, loadComments: function (field) {
		if(field.attributes.kpiInstId != null && field.attributes.kpiInstId !== undefined){
			Ext.Ajax.request({
		        url: this.services['commentsList'],
		        params: {kpiInstId: field.attributes.kpiInstId},
		        success : function(response, options) {
		      		if(response !== undefined && response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if (content !== undefined) {
		      				this.listPanel.store.loadData(content);	      				
		      			} 
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
			});
		}
	}
	, saveComment: function () {
		var commId= this.commentId;
		Ext.Ajax.request({
	        url: this.services['commentSave'],
	        params: {'comment': this.editor.getValue(), 'kpiInstId': this.kpiInstId, 'commentId': this.commentId, 'owner': this.owner},
			success: function(response, options) {
	      		if(response !== undefined && response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText);
	      			if (content !== undefined) {

	      				if(content.text == 'Forbidden'){
			      			Ext.MessageBox.show({
			      				title: 'Status',
			      				msg: 'Operation forbidden',
			      				modal: false,
			      				buttons: Ext.MessageBox.OK,
			      				width:300,
			      				icon: Ext.MessageBox.INFO  			
			      			});
	      				}else{
			      			Ext.MessageBox.show({
			      				title: 'Status',
			      				msg: 'Success',
			      				modal: false,
			      				buttons: Ext.MessageBox.OK,
			      				width:300,
			      				icon: Ext.MessageBox.INFO  			
			      			});
				      		this.update(this.selectedField);
	      				}
				      		
	      			}
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
	        },
	        scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
		
	}
	, update:  function(field){	
		if(field !== undefined && field != null){
			this.kpiInstId = field.attributes.kpiInstId;
			this.selectedField= field;
			this.loadComments(field);
			
		}else{

			this.kpiInstId = null;
		}
		this.editor.setValue('');
		this.editor.show();

	}
	, deleteItem: function(id, index) {
		
		Ext.MessageBox.confirm(
			LN('sbi.generic.pleaseConfirm'),
			LN('sbi.generic.confirmDelete'),            
            function(btn, text) {
                if (btn=='yes') {
                	if (id != null) {
                		
						Ext.Ajax.request({
				            url: this.services['commentDelete'],
				            params: {'commentId': id},
				            method: 'GET',
				            success: function(response, options) {
				            	if(response !== undefined && response.responseText != undefined ){
				            		var res = Ext.decode(response.responseText);
				      				if(res.text == 'Forbidden'){
						      			Ext.MessageBox.show({
						      				title: 'Status',
						      				msg: 'Operation forbidden',
						      				modal: false,
						      				buttons: Ext.MessageBox.OK,
						      				width:300,
						      				icon: Ext.MessageBox.INFO  			
						      			});
				      				}else{
						      			Ext.MessageBox.show({
						      				title: 'Status',
						      				msg: 'Success',
						      				modal: false,
						      				buttons: Ext.MessageBox.OK,
						      				width:300,
						      				icon: Ext.MessageBox.INFO  			
						      			});
										var deleteRow = this.rowselModel.getSelected();
										this.store.remove(deleteRow);
										this.store.commitChanges();
										if(this.store.getCount()>0){
											this.rowselModel.selectRow(0);
										}
				      				}
				            	}
				            }
				            , failure: this.onDeleteItemFailure
				            , scope: this
			
						});
					} 
                }
            },
            this
		);
	}
});