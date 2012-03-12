/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * Authors - Antonella Giachino (antonella.giachino@eng.it)
 */
Ext.define('Sbi.extjs.chart.ExtJSChartPanel', {
    alias: 'widget.ExtJSChartPanel',
    extend: 'Sbi.extjs.chart.ExtJSGenericChartPanel',
    chart: null,
    
    constructor: function(config) {
    	var defaultSettings = {
    	};

    	var c = Ext.apply(defaultSettings, config || {});
    	
    	c = Ext.apply(c, {id: 'ExtJSGenericChartPanel'});
    	
    	c.storeId = c.dsLabel;
    	
    	Ext.apply(this, c);
    	
    	this.template = c.template || {};
    	this.template.divId = c.divId || {};
    	delete c.template;
    	this.init(c);
        this.callParent(arguments);
    }

  , createChart: function(){
	  // gets JSON template
	  var config =  Ext.apply(this.template || {});	    
	  config.renderTo = config.divId;
	  config.store = this.chartStore;
	  config.animate = (!config.animate)?true:config.animate;	
	   	
	   	//defines dimensions 
	   	config.width = (!config.width)?500:parseInt(config.width);
	   	config.height = ((!config.height)?500:parseInt(config.height));
	   	
	   	//updates theme
	   	var themeConfig = this.getThemeConfiguration(config);
	   	if (themeConfig !== null){
		   	Ext.define('Ext.chart.theme.ExtJSChartTheme', {
		   	    extend: 'Ext.chart.theme.Base',	       
		   	    constructor: function(config) {
		   	        this.callParent([Ext.apply(themeConfig, config)]);
		   	    }});		
		    config.theme = 'ExtJSChartTheme';
	   	}
	   	
	   	//defines tips	   	
	   	for(var j = 0; j< config.series.length; j++){
	        if (config.series[j].tips !== undefined){
	        	config.series[j].tips.renderer = function(storeItem, item) {
	        		var cat = "";
	        		var value = "";
	        		
	        		 var type = item.series.alias[0] || "";	        		
	        		
	        		 switch (type) {
	        		     case 'series.bar':
	        		    	cat = item.value[0]; 
	        		    	value = item.value[1];     
	        		     	break;
	        		     case 'series.line':
	        		    	cat = storeItem.get(item.series.xField);
	        		    	value = storeItem.get(item.series.yField);        
	        		     	break;	 
	        		     case 'series.pie': 
		        		    	cat = storeItem.get(item.series.label.field);
		        		    	value = storeItem.get(item.series.field);        
		        		     	break;	
	        		     case 'series.gauge':
		        		    	cat = storeItem.get(item.series.label.field);
		        		    	value = storeItem.get(item.series.field);        
		        		     	break;	
	        		     case 'series.radar':
	        		    	 	cat = storeItem.get(item.series.xField);
		        		    	value = storeItem.get(item.series.yField);       
		        		     	break;	
	        		     case 'series.scatter':
	        		    	 	cat = storeItem.get(item.series.xField);
		        		    	value = storeItem.get(item.series.yField);       
		        		     	break;	
	        		     case 'series.area':
	        		    	 	cat = storeItem.get(item.series.xField);
		        		    	value = storeItem.get(item.storeField);       
		        		     	break;	
	        		     default: 
	        		    	cat = undefined;
	        		     	value = undefined;
	        		     	break;      
	        		 }
	        		 if (cat && value){
	        			 var text = cat + ': ' + value ; //default
	        			 var tip = item.series.tips.text;	        			 
	        			 if (tip){
	        				 tip = tip.replace('{CATEGORY}', cat);	        						
	        				 tip = tip.replace('{SERIE}', value);
	        				 text = tip;
	        			 }
	        			 this.setTitle(text);	 
	        		 }	        	
	        	};
	        }
	   	}
	  	
	  	var docLabel = this.documentLabel;
	  	var docParameters = config.DOCUMENT_PARAMETERS;
	  	
	  	//Adding click listener for Cross Navigation
	  	for(var j = 0; j< config.series.length; j++){
		  	config.series[j].listeners = {
		  			itemmousedown:function(obj) {
		  				var categoryField ;
		  				var valueField ;
		  				
		  				if (obj.series.type == 'bar'){
		  					categoryField = obj.storeItem.data[obj.series.xField];
			  				valueField = obj.storeItem.data[obj.yField];
		  				}
		  				else if (obj.series.type == 'pie'){
		  					categoryField = obj.storeItem.data[obj.series.label.field];
		  					valueField = obj.slice.value;	
		  				} else if (obj.series.type == 'gauge'){
		  					categoryField = obj.storeItem.data[obj.series.label.field];
		  					valueField = obj.slice.value;	
		  				} else if (obj.series.type == 'area'){
		  					categoryField = obj.storeItem.data[obj.series.xField];
		  					valueField = obj.storeItem.data[obj.storeField];	
		  				} else if (obj.series.type == 'line'){
		  					categoryField = obj.storeItem.data[obj.series.xField];
		  					valueField = obj.storeItem.data[obj.series.yField];	
		  				}  else if (obj.series.type == 'radar'){
		  					categoryField = obj.storeItem.data[obj.series.xField];
		  					valueField = obj.storeItem.data[obj.series.yField];	
		  				} else if (obj.series.type == 'scatter'){
		  					categoryField = obj.storeItem.data[obj.series.xField];
		  					valueField = obj.storeItem.data[obj.series.yField];	
		  				}

		  				// alert(categoryField + ' &' + valueField);
		  				 
		  				//Cross Navigation
		  			  	var drill = config.drill;
		  		    	if(drill != null && drill !== undefined){
		  		    		var doc = drill.document;
		  		    		
		  		    		var params = "";
		  		    		for(var i = 0; i< drill.param.length; i++){
		  		    			if(drill.param[i].type == 'ABSOLUTE'){
		  		    				if(params !== ""){
		  			    				params+="&";
		  			    			}
		  		    				params+= drill.param[i].name +"="+drill.param[i].value;
		  		    			}
		  		    		}
		  		    		
		  			    		for(var i = 0; i< drill.param.length; i++){
		  			    			if(drill.param[i].type == 'CATEGORY'){
		  			    				if(params !== ""){
		  		    	    				params+="&";
		  		    	    			}
		  			    				if(categoryField !== undefined){
		  			    					params+= drill.param[i].name +"="+categoryField;
		  			    				}	    				
		  			    			}
		  			    			
		  			    		}
		  			    		
		  			    		var relParams = docParameters[0];
				        		for(var i = 0; i< drill.param.length; i++){
				        			if(drill.param[i].type == 'RELATIVE'){
				        				for(var y =0; y<relParams.length; y++){		        					
				        					if(relParams[y].name == drill.param[i].name){
				        						if(params !== ""){
						    	    				params+="&";
						    	    			}
						        				params+= drill.param[i].name +"="+relParams[y].value+"";
					    	    				params+="&";
				        					}
				        				}
				        			}
				        		}	
		  			    		for(var i = 0; i< drill.param.length; i++){
		  			    			if(drill.param[i].type == 'SERIE'){
		  			    				if(params !== ""){
		  		    	    				params+="&";
		  		    	    			}
		  			    				if (valueField !== undefined){
			  			    				params+= drill.param[i].name +"="+valueField;
		  			    				} 
		  			    					
		  			    			}
		  			    			
		  			    		}    		
		  		    		
		  			    		//execute Cross navigation
		  			    		//alert("Document Label: "+docLabel);
		  						parent.execCrossNavigation("iframe_"+docLabel, doc, params);

		  		    	}	 
		  				 
		  		    }
		  	};
	  	}

        if (this.chart){
        	//update the store and redraw the chart
        	this.chart.store = this.chartStore;
        	this.chart.redraw();
        }else{
        	//Creates the new (initial) instance of chart
        	this.chart = Ext.create('Ext.chart.Chart',config);
	    	
        	//Adds title and subtitle        	
        	var configTitle = this.getConfigStyle(config.title);
        	configTitle.renderTo = config.divId + '_title';
        	var title = this.createTextObject(configTitle);        	        	
        	var configSubtitle = this.getConfigStyle(config.subtitle, 2);
        	configSubtitle.renderTo = config.divId + '_subtitle';
			var subtitle = this.createTextObject(configSubtitle);                	
        }
		this.hideMask();

  }

});