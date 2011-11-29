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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGridPanel =  function(config) {
		
		var json = config.json;

		var defaultSettings = {
	        title: config.title,
	        region: 'center',
	        enableDD: true
			,loader: new Ext.tree.TreeLoader() // Note: no dataurl, register a TreeLoader to make use of createNode()
			,root: new Ext.tree.AsyncTreeNode({
				text: 'KPI root',
				id:'name',
				children: json
				
			}),
			rootVisible:false
				
		};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiGridPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiGridPanel);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		this.initGrid();
		this.addEvents('updateAccordion');
		
		Sbi.kpi.KpiGridPanel.superclass.constructor.call(this, c);
		this.initGridListeners();
};

Ext.extend(Sbi.kpi.KpiGridPanel ,Ext.ux.tree.TreeGrid, {
	columns: null,
	ids : new Array()
	
	, initGrid: function(){
		var kpiColumns = new Array();

		var ids = this.ids;
	    var tpl = new Ext.XTemplate(
			      '<tpl for=".">'
	    		  ,'<tpl if="values.status">'
			      ,'<canvas id="{values.name}" width="15px" height="15px" onLoad="{[this.draw(values.name, values.status)]}" style="align: center;"/>'	     
			      ,'</tpl>'
			      ,'</tpl>'
			      ,{
			          ids: this.ids,
			          draw: function(val, color) {
			    	  	  //console.log(val);
			    	  	  var status = {val : val, color: color};
			              ids.push(status);
			          }
			      }
			);
	    
	    var tplTrend = new Ext.XTemplate(
			      '<tpl for=".">'
	    		  ,'<tpl if="values.trend !== undefined && values.trend === 1">'//positive
			      ,'<div style="align: center;" class="trend-up">&nbsp;</div>'	     
			      ,'</tpl>'
	    		  ,'<tpl if="values.trend !== undefined && values.trend === -1">'//negative
			      ,'<div style="align: center;" class="trend-down">&nbsp;</div>'	     
			      ,'</tpl>'
	    		  ,'<tpl if="values.trend !== undefined && values.trend === 0">'//equal
			      ,'<div style="align: center;" class="trend-equal">&nbsp;</div>'	     
			      ,'</tpl>'
			      ,'</tpl>'

			);
		var col = {header:'Model Instance',
		dataIndex:'name',
		width:200};
		kpiColumns.push(col);
		
		var col1 = {header:'Actual',
		dataIndex: 'actual',
		width: 100};
		kpiColumns.push(col1);	
		
		var col2 = {header:'Target',
		dataIndex:'target',
		width: 70};
		kpiColumns.push(col2);
		
		var col3 = {header:'Status',
		dataIndex:'status',
		tpl: tpl,
		width:70};
		kpiColumns.push(col3);
		
		var col4 = {header:'Trend',
		dataIndex:'trend',
		tpl: tplTrend,
		width:70};
		kpiColumns.push(col4);	
		
		this.columns = kpiColumns;


	}
	, initGridListeners: function() {
		this.on("afterrender", function(grid){
	    	for(i=0; i< this.ids.length; i++){
	    		var status = this.ids[i];
	    		var canvas = document.getElementById(status.val);
	    		try{
		    		if(canvas.getContext("2d")){
		    			drawCanvasCircle(canvas, status.color);
		    		}
	    		}catch(error){
	    			var canvasEl = new Ext.Element(canvas);
	    			canvasEl.setHeight(0);
	    			Ext.DomHelper.insertHtml('beforeBegin', canvas, '<div width="10px" height="10px" style="display:inline; border: 1px solid black; background-color:'+status.color+'">&nbsp;&nbsp;&nbsp;</div>' )
	    		
	    		}
	    	}
	    	this.show();
	     }, this);
		
		this.addListener('click', this.selectNode, this);
		
	}
	,selectNode : function(field) {

		if(field !== null){
			if(field.attributes != undefined ){
//				var parent = this.ownerCt;
//				var accordion = parent.items[1];
				this.fireEvent('updateAccordion',field);				
			}
		}
	}
	
});

function drawCanvasCircle(canvas, color){
	
	    var context = canvas.getContext("2d");
	 
	    var centerX = 7;
	    var centerY = 7;
	    var radius = 7;
	 
	    context.beginPath();
	    context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
	 
	    context.fillStyle = color;
	    context.fill();
	    context.lineWidth = 1;
	    context.strokeStyle = "black";
	    context.stroke();
}