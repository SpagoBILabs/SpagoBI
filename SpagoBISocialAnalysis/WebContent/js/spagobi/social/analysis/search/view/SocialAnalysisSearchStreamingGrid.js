/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Grid of the streaming search
 * 
 *     
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */


Ext.define('Sbi.social.analysis.search.view.SocialAnalysisSearchStreamingGrid', {
	extend: 'Ext.grid.Panel',
	
	title: 'Continuos scanning',
	titleAlign: 'center',
	flex: 1,

	config:
	{

	},
	
	

	constructor : function(config) {
		this.initConfig(config||{});
		
		this.store = Ext.create('Sbi.social.analysis.search.store.StreamingSearchStore', { });
		
		this.callParent(arguments);
	},

	initComponent: function() {
		Ext.apply(this, {
			columns: [
//		        {
//		            text: 'ID',
//		            width: 40,
//		            dataIndex: 'searchID'
//		        },
		        {
		            text: 'Label',
//		            width: 100,
		            dataIndex: 'label'
		        },
		        {
		            text: 'Keywords',
//		            width: 100,
		            flex: 1,
		            dataIndex: 'keywords'
		        },
		        {
		            text: 'Last Activation',
//		            width: 100,
		            dataIndex: 'lastActivationTime',
		            renderer : Ext.util.Format.dateRenderer('m/d/Y H:i')
		        },
		        {
		            text: 'Accounts to monitor',
//		            width: 200,
		            flex: 1,
		            dataIndex: 'accounts',
		        },
		        {
		            text: 'Resources to monitor',
//		            width: 200,
		            flex: 1,
		            dataIndex: 'links',
		        },
		        {
		            text: 'Documents',
//		            width: 200,
		            flex: 1,
		            dataIndex: 'documents',
		        },
		        {
		        	xtype: 'actioncolumn',
		            text: 'Start/Stop',
		            width: 100,
		            dataIndex: 'loading',
		            align: 'center',
		            getClass: function(value, metadata, record)
		            {
		            	var searchLoading = record.get('loading');

		            	if(!searchLoading)
		            	{
		            	    return 'x-streaming-start'; 
		            	} else {
		            	    return 'x-streaming-stop';               
		            	}
		            },
		            handler: function(grid, rowIndex, colIndex) {
	                    var rec = grid.getStore().getAt(rowIndex);
	                    var loadingValue = rec.get('loading');
	                    
	                    if(loadingValue)
                    	{
	                    	//stop code
	                    	Ext.Msg.show({
	                    	     title:'Confirm',
	                    	     msg: 'Your are stopping the streaming search. Are you sure?',
	                    	     buttons: Ext.Msg.YESNO,
	                    	     icon: Ext.Msg.QUESTION,
	                    	     fn: function(btn, text){
                                    if (btn == 'yes'){
                                   	 Ext.Ajax.request({
            	                            url : 'restful-services/streamingSearch/stopStreamingSearch',
            	                            method:'POST', 
            	                            params : {
            	                                searchID: Ext.encode(rec.get('searchID'))
            	                            },
            	                            scope : this,
            	                            success: function(response)
               	                           {
               	                        	  var text = response.responseText;
               	                        	  Ext.Msg.alert('Success', text);
              	                        	  grid.getStore().load();
              	                           }
                                   	 }); 
                                    
                                    }
                                    if (btn == 'no'){
                                   	 //do nothing
                                    }
                                },
	                    	     icon: Ext.Msg.QUESTION
	                    	});
                    	}
	                    else
                    	{
	                    	//start code
	                    	Ext.Msg.show({
	                    	     title:'Confirm',
	                    	     msg: 'Starting this Stream will stop the other one active. Are you sure?',
	                    	     buttons: Ext.Msg.YESNO,
	                    	     icon: Ext.Msg.QUESTION,
	                    	     fn: function(btn, text){
                                     if (btn == 'yes'){
                                    	 Ext.Ajax.request({
             	                            url : 'restful-services/streamingSearch',
             	                            method:'POST', 
             	                            params : {
             	                                searchID: Ext.encode(rec.get('searchID')),
             	                                keywords: Ext.encode(rec.get('keywords'))
             	                            },
             	                            scope : this,
             	                           success: function(response)
              	                           {
              	                        	  var text = response.responseText;
              	                        	  Ext.Msg.alert('Success', text);
             	                        	  grid.getStore().load();
             	                           }
                                    	 }); 
                                     
                                     }
                                     if (btn == 'no'){
                                    	 //do nothing
                                     }
                                 },
	                    	     icon: Ext.Msg.QUESTION
	                    	});
//	                    	
                    	}
		            }
	            },
		        {
		            xtype: 'actioncolumn',
		            text: 'Delete',
		            icon: 'img/delete.png',
		            align: 'center',
		            handler: function(grid, rowIndex, colIndex) {
	                    
		            	var rec = grid.getStore().getAt(rowIndex);
	              
                    	Ext.Msg.show({
                    	     title:'Confirm',
                    	     msg: 'You are deleting this search. Are you sure?',
                    	     buttons: Ext.Msg.YESNO,
                    	     icon: Ext.Msg.QUESTION,
                    	     fn: function(btn, text){
                                 if (btn == 'yes'){
                                	 Ext.Ajax.request({
         	                            url : 'restful-services/streamingSearch/deleteSearch',
         	                            method:'POST', 
         	                            params : {
         	                                searchID: Ext.encode(rec.get('searchID')),
         	                                loading: Ext.encode(rec.get('loading'))
         	                            },
         	                            scope : this,
         	                           success: function(response)
          	                           {
          	                        	    var text = response.responseText;
          	                        	 	Ext.Msg.alert('Success', text);
          	                        	 	grid.getStore().load();
         	                           }
                                	 }); 
                                 
                                 }
                                 if (btn == 'no'){
                                	 //do nothing
                                 }
                             },
                    	     icon: Ext.Msg.QUESTION
                    	});
//	                    	
                    }
		        },
		        {
		        	xtype: 'actioncolumn',
		            text: 'Analyse',
//		            width: 100,
		            dataIndex: 'loading',
		            align: 'center',
		            icon: 'img/show.png',
		            handler: function(grid, rowIndex, colIndex) {
		            	
	                    var rec = grid.getStore().getAt(rowIndex);
	                    
	                    var documents = rec.get('documents');    
	                    
	                    if(documents != null && documents != "")
	                    {
	                    	
	                    	window.location.href = "tabs/summary?searchID="+ searchId + "&withDocs=true";
	                    	
	                    }
	                    else
	                    {
	                    	window.location.href = "tabs/summary?searchID="+ searchId + "&withDocs=false";
	                    }	  
	                }
		        }
		    ]}),
		
		this.callParent();
	}

});

