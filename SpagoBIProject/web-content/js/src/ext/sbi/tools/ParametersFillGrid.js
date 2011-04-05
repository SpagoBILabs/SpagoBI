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
 * Authors - Giulio Gavardi
 */
Ext.ns("Sbi.tools");

Sbi.tools.ParametersFillGrid = function(config) { 
		
	// create the editor grid
	    var grid = {
	    	xtype: 'grid',
	    	title:  LN('sbi.ds.fillPars'),
	        //width: 380,
	        autoHeight: true,
	        source: config.pars,
	        forceLayout: true,
	        frame: true,
	        deferRowRender : false,
	        propertyNames: {
	            tested: 'QA',
	            borderWidth: 'Border Width'
	        },
	        viewConfig : {
	            forceFit: true,
	            scrollOffset: 2 // the grid will never have scrollbars
	        }
	        };

    var c = Ext.apply( {}, config, grid);
    
    // constructor
    Sbi.tools.ParametersFillGrid.superclass.constructor.call(this, c);
    
    this.on('beforeedit', function(e) {
    	var t = Ext.apply({}, e);
    	var col = t.column;
    	this.currentRowRecordEdited = t.row;	
    	
    }, this);
    
    this.on('afteredit', function(e) {
    	
    	var col = e.column;
    	var row = e.row;	
    	
    }, this);

};

Ext.extend(Sbi.tools.ParametersFillGrid, Ext.grid.PropertyGrid, {
  
	// fills value if present: if not present set defaults type in order to have right editors
	fillParameters: function(parsList){

		//	remove preceding content
		this.store.removeAll(true);
		// get selected kpi udp values 
	
		if(parsList){
		// fill udp st if present with values associated		
			for(i = 0; i<parsList.length;i++){
				var singlePar = parsList[i];
				// check if singlePar.label has a value associated for present KPI
				// check if udo.label is present among singleParValues
				var valueToInsert = null;
			
				// get UDP type
				var typeData = singlePar.type;
				if(!typeData){
					typeData = 'STRING';
				}
				else{
					typeData = typeData.toUpperCase();
				}
				//convert value to th right type
				if(typeData == 'STRING'){
					if(valueToInsert == null){
						valueToInsert = '';
					}
				}else if (typeData == 'NUMBER'){
					if(valueToInsert == null){
						valueToInsert = parseFloat(0);	
					}
					else{
						valueToInsert = parseFloat(valueToInsert);
					}						
				} else {
					// case text
					if(valueToInsert == null){
						valueToInsert = '';
					}
				}
		
				var tempRecord = new Ext.data.Record({"name": singlePar.name,"value": valueToInsert});		
				this.store.add(tempRecord);
			}
		}
	  this.getView().refresh();
	}
	,
	// return array with values in grid
	saveUdpValues:function(type){
	     /*var arrayUdps = new Array();
			var storeUdps = this.getStore();
			for(var i = 0;i< storeUdps.getCount();i++){
				var item = storeUdps.getAt(i);
				var data = item.data;
				// want to add type and family to this record
				data.familyId = type;
				var stop = false;
				for ( var j = 0; j < this.parsList.length && stop == false; j++) {
					var udpVal = config.parsList[j];
					if(udpVal.label == data.name){
						data.dataTypeCd = udpVal.dataTypeCd;
						stop = true;
					}
				}
				arrayUdps.push(data);
			}
		return arrayUdps;*/
	}
	
});

