/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Grid for the historical search
 * 
 *     
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */


Ext.define('Sbi.social.analysis.search.view.SocialAnalysisSearchHistoricalGrid', {
	extend: 'Ext.grid.Panel',
	
	title: LN('sbi.social.analysis.timelyscanning'),
	titleAlign: 'center',
	flex: 1,
	margin: '10 0 10 0',
	disableSelection: true,
	viewConfig: 
	{
        getRowClass: function(record, index) {
            var loading = record.get('loading');
        	
            if (loading) {
                return 'disabled-row';
            }
        }
    },

	config:
	{

	},	


	constructor : function(config) {
		this.initConfig(config||{});
		
		this.store = Ext.create('Sbi.social.analysis.search.store.HistoricalSearchStore', { });
		
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
//		            width: 200,
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
//		        {
//		            text: 'Frequency',
//		            width: 100,
//		            dataIndex: 'frequency',
//		        },
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
		        		else
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
         	                            url : 'restful-services/historicalSearch/deleteSearch',
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
	                    
//		            	window.location.href = "tabs/summary?searchID="+ searchId + "&documents=" + docArr;
	                }		            
		        },
		        {
		        	xtype: 'actioncolumn',
		            text: LN('sbi.social.analysis.scheduler'),
//		            width: 100,
		            align: 'center',
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
		            items: [
		            {
		            	//search scheduler
		            	getClass: function(value, metadata, record)
			            {
			            	var searchScheduler = record.get('hasSearchScheduler');

			            	if(searchScheduler)
			            	{
			            	    return 'x-scheduler-stop-enabled'; 
			            	} else {
			            	    return 'x-scheduler-stop-disabled';               
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
		                tooltip: LN('sbi.social.analysis.schedulertooltip'),
		                handler:  this.twitterStopSearchScheduler
		            }
//		            {
//		            	//monitor scheduler
//		            	getClass: function(value, metadata, record)
//			            {
//		            		
//			            	var monitorScheduler = record.get('hasMonitorScheduler');
//
//			            	if(monitorScheduler)
//			            	{
//			            	    return 'x-scheduler-stop-enabled'; 
//			            	} else {
//			            	    return 'x-scheduler-stop-disabled';               
//			            	}
//			            },
//		                tooltip: 'Stop Monitor Resources Scheduler',
////		                handler:  this.twitterStopSearchScheduler
//		                
//		            }
		            ],
		           
		        }],
		}),
		
		this.store.on( 'load', function(store, records, options) {
			
			if(store != null)
        		{
        			for(var i = 0; i < store.getCount(); i++)
    				{
        					
        				var record = store.getAt(i);

        				if(record.get('isFailed'))
    					{
        					var searchID = record.get('searchID');
        					var labelSearch = record.get('label');
        					
        					Ext.Msg.show({
        						title:'Alert',
        						msg: labelSearch + LN('sbi.social.analysis.searchfailedmessage'),
        						buttons: Ext.Msg.OK,
        						icon: Ext.Msg.WARNING,
        						fn: function(btn, text){
                                    if (btn == 'ok'){
                                    	Ext.Ajax.request({
            								url : 'restful-services/historicalSearch/updateFailedSearch',
            								method:'POST', 
            								params : {
            									searchID: Ext.encode(searchID)
            									},
            									scope : this,
            									success: function(response)
                  	                           {
                  	                        	    var text = response.responseText;
                  	                        	 	Ext.Msg.alert('Success', text);
            										store.load();
                 	                           }
            							});
                                    }
                               }
        						
        					});
        					break;
    					}
    				}		        		
        		}
        	}); 
		
		this.callParent();
	},
	
	
	twitterStopSearchScheduler: function(grid, rowIndex, colIndex)
	{

        var rec = grid.getStore().getAt(rowIndex);
        var searchSchedulerValue = rec.get('hasSearchScheduler');
        
        if(!searchSchedulerValue)
    	{
        	//stop code
    	}
        else
    	{
        	//stop scheduler code
        	Ext.Msg.show({
        	     title:'Confirm',
        	     msg: LN('sbi.social.analysis.stopsearchscheduler'),
        	     buttons: Ext.Msg.YESNO,
        	     icon: Ext.Msg.QUESTION,
        	     fn: function(btn, text){
                     if (btn == 'yes'){
                    	 Ext.Ajax.request({
	                            url : 'restful-services/historicalSearch/stopSearchScheduler',
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
	}

});

