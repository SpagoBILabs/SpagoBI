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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIComments =  function(config) {
		
		var defaultSettings = {autoScroll: true, 
								height: 350,
								layout: 'column',
								border:false,
								style:'padding: 5px;'};
		
		var execId = config.SBI_EXECUTION_ID;
		var paramsList = {SBI_EXECUTION_ID: execId, LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "COMMENTS_LIST"};
		var paramsSave = {SBI_EXECUTION_ID: execId, LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "SAVE_COMMENT"};
		
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
		this.store = new Ext.data.JsonStore({
	    	autoLoad: false    	  
	    	, id : 'id'		
	        , fields: ['owner'
         	          , 'creationDate'
          	          //, 'lastModificationDate'
          	          , 'comment'
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
	
	
	initComments: function(c){
		this.rowselModel = new Ext.grid.RowSelectionModel({
	          singleSelect: true
	          
	    });
	    this.listPanel = new Ext.grid.GridPanel({
	        store: this.store,
	        width: 400,
	        height: 150,
	        autoScroll: true,
	        viewConfig:{forceFit:true},
	        selModel: this.rowselModel,
	     	hideHeaders : true,
	     	listeners: {
	        	'click': {
					fn: function(){
	    				var row = this.rowselModel.getSelected();		
	    				this.editor.setValue(row.data.comment);
	     			}
					, scope: this
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
	        }]
	        ,fbar: [{
	            text: 'Save comment'
	            , handler: this.saveComment
	            , scope: this
	        }]
	    });
	
	  this.editor = new Ext.form.HtmlEditor({
		  	enableSourceEdit: false
		  	, width: 400
		  	, height: 140
		  	, autoScroll: true
		  	, style:'padding-left: 5px; margin: 5px;'
		  	, layout: 'fit'
    
	  }); 

	  this.setAutoScroll(true);
	  this.items =[this.listPanel,  this.editor];
	  
	}
	
	, loadComments: function (field) {
		
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
	, saveComment: function () {
		Ext.Ajax.request({
	        url: this.services['commentSave'],
	        params: {'comment': this.editor.getValue(), 'kpiInstId': this.kpiInstId},
			success: function(response, options) {
	      		if(response !== undefined && response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if (content !== undefined) {
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
		this.doLayout();
        this.render();
	}
});