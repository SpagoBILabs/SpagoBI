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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.chart");


Sbi.chart.OpenFlashChart = function(config) {	
	Sbi.chart.OpenFlashChart.superclass.constructor.call(this, config);
};

Ext.extend(Sbi.chart.OpenFlashChart, Ext.FlashComponent, {
    
   
    // -- public methods -------------------------------------------------------------------
    
   
    
    
    // -- private methods ------------------------------------------------------------------
    
	initComponent : function(){
		Sbi.chart.OpenFlashChart.superclass.initComponent.call(this);
    	if(!this.url){
        	this.url = Sbi.chart.OpenFlashChart.CHART_URL;
    	}
    	   	
    	this.autoScroll = true;
    	
    	this.flashVars = {
    		paramWidth: 100
    		, paramHeight: 100
    		, minValue: -100
    		, maxValue: 100
    		, lowValue: -50
    		, highValue: 50
    	};
    	
	}

	,  onRender : function(ct, position){
		
		this.flashVars.paramWidth = ct.getWidth();
		this.flashVars.paramHeight = ct.getHeight();
				
		Sbi.chart.OpenFlashChart.superclass.onRender.call(this, ct, position);
		
        //this.testFn.defer(2000, this);
	}
	
	, testFn: function() {
    	this.swf.load();
    }
    
});



/*
function open_flash_chart_data()
{
	var s = Ext.util.JSON.encode({
		"elements": [
		{
			"type": "bar",
			"values": [9, 8, 7, 6, 5, 4, 3, 2, 1]
		}
		]
		, "title": {
			"text": "Chart di prova"
		}
	});
	return s;
}
*/
function open_flash_chart_data() {
	
	var s = Ext.util.JSON.encode({ 
		"elements": [ { 
			"type": "bar_sketch", 
			"colour": "#81AC00", 
			"outline-colour": "#567300", 
			"offset": 5, 
			"values": [ { "top": 3, "tip": "Hello #val#" }, 
			            1, 2, 3, 
			            { "top": 3, "tip": "Hello #val#" }, 
			            { "top": 3, "tip": "Hello #val#" },
			            { "top": 3, "tip": "Hello #val#" }, 
			            { "top": 3, "tip": "Hello #val#" }, 
			            { "top": 3, "tip": "Hello #val#" }, 
			            { "top": 3, "tip": "Hello #val#" }, 
			            10, 11 ] 
		} ], 
		"title": { "text": "Open Flash Chart", "style": "{color: #567300; font-size: 14px}" } ,
		"bg_colour": "#FFFFFF"
	});
	return s;
}





Sbi.chart.OpenFlashChart.CHART_URL = '/SpagoBIConsoleEngine/swf/openflashchart/open-flash-chart.swf';