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
	
	title: LN('sbi.social.analysis.continuousscanning'),
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
		            text: LN('sbi.social.analysis.label'),
//		            width: 100,
		            dataIndex: 'label'
		        },
		        {
		            text: LN('sbi.social.analysis.keywords'),
//		            width: 100,
		            flex: 1,
		            dataIndex: 'keywords'
		        },
		        {
		            text: LN('sbi.social.analysis.lastactivation'),
//		            width: 100,
		            dataIndex: 'lastActivationTime',
		            renderer : Ext.util.Format.dateRenderer('m/d/Y H:i')
		        },
		        {
		            text: LN('sbi.social.analysis.accountstomonitor'),
//		            width: 200,
		            flex: 1,
		            dataIndex: 'accounts',
		        },
		        {
		            text: LN('sbi.social.analysis.resourcestomonitor'),
//		            width: 200,
		            flex: 1,
		            dataIndex: 'links',
		        },
		        {
		            text: LN('sbi.social.analysis.documents'),
//		            width: 200,
		            flex: 1,
		            dataIndex: 'documents',
		        },
		        {
		        	xtype: 'actioncolumn',
		            text: LN('sbi.social.analysis.startstop'),
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
		            isDisabled: function(view, rowIndex, colIndex, item, record)
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
		        		
		        		if(isDisabled)
		        		{
		        			return true;
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
	                    	     msg: LN('sbi.social.analysis.stopstreammessage'),
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
	                    	     msg: LN('sbi.social.analysis.startstreammessage'),
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
		            text: LN('sbi.social.analysis.delete'),
		            icon: 'img/delete.png',
		            align: 'center',
		            isDisabled: function(view, rowIndex, colIndex, item, record)
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
		        		
		        		if(isDisabled)
		        		{
		        			return true;
		        		}
		            },
		            handler: function(grid, rowIndex, colIndex) {
	                    
		            	var rec = grid.getStore().getAt(rowIndex);
	              
                    	Ext.Msg.show({
                    	     title:'Confirm',
                    	     msg: LN('sbi.social.analysis.deletingmessage'),
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
		            text: LN('sbi.social.analysis.analyse'),
//		            width: 100,
		            dataIndex: 'loading',
		            align: 'center',
		            getClass: function(value, metadata, record)
		            {
		            	var searchLoading = record.get('loading');

		            	if(!searchLoading)
		            	{
		            	    return 'x-analysis-display'; 
		            	} else {
		            	    return 'x-analysis-loading';               
		            	}
		            },
		            isDisabled: function(view, rowIndex, colIndex, item, record)
		            {
		            	var searchLoading = record.get('loading');
		            	if(!searchLoading)
	            		{
		            		return false;	            		
	            		}
		            	else
	            		{
		            		return true;
	            		}
		            },
		            handler: function(grid, rowIndex, colIndex) {
		            	
	                    var rec = grid.getStore().getAt(rowIndex);
	                    
	                    var searchId = rec.get('searchID');
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

