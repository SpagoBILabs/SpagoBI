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
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageUdpValues = function(config) { 
		
	// create the editor grid
	    var grid = {
	    	xtype: 'grid',
	        width: 380,
	        autoHeight: true,
	        source: config.udpEmptyList,
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
    Sbi.kpi.ManageUdpValues.superclass.constructor.call(this, c);
    
    this.on('beforeedit', function(e) {
    	var t = Ext.apply({}, e);
    	var col = t.column;
    	this.currentRowRecordEdited = t.row;	
    	
    }, this);
    
    this.on('afteredit', function(e) {
    	
    	var col = e.column;
    	var row = e.row;	
    	
    }, this);

}

Ext.extend(Sbi.kpi.ManageUdpValues, Ext.grid.PropertyGrid, {
  
	// fills value if present: if not present set defaults type in order to have right editors
	fillUdpValues:function(udpValues){

	//	remove preceding content
	this.store.removeAll();
		// get selected kpi udp values
	
		var udpEmptyList = this.udpEmptyList;
		var udpList = this.udpList;
		
		// if not defined give a default
		if(!udpValues){
			udpValues = new Array();
		}
		
		if(udpList){
		// fill udp st if present with values associated		
		for(i = 0; i<udpList.length;i++){
			var udp = udpList[i];
			// check if udp.label has a value associated for present KPI
			// check if udo.label is present among udpValues
			var valueToInsert = null;
			var stop = false;
			for ( var int = 0; int < udpValues.length && stop == false; int++) {
				var udpVal = udpValues[int];
				if(udpVal.label == udp.label || udpVal.name == udp.label){
					valueToInsert = udpVal.value;					
					stop = true;
				}
			}
		
		// get UDP type
		var typeData = udp.dataTypeCd;
		if(!typeData){
			typeData = 'TEXT'
		}
		else{
			typeData = typeData.toUpperCase();
		}
		//convert value to th right type
		if(typeData == 'INTEGER'){
			if(valueToInsert == null){
			valueToInsert = parseFloat(0);	
			}
			else{
				valueToInsert = parseFloat(valueToInsert);
			}
			}
		else if (typeData == 'BOOLEAN'){
			var boolToInsert = (valueToInsert === 'true' || valueToInsert === true)
			valueToInsert = boolToInsert;						
		}
		else {
			// case text
			if(valueToInsert == null){
				valueToInsert = '';
			}
			else{
				// keep valueToInsert
			}
		}

		var tempRecord = new Ext.data.Record({"name": udp.label,"value": valueToInsert});					
		this.store.add(tempRecord);
		}
	  }
	}
	,
	// return array with values in grid
	saveUdpValues:function(type){
	     var arrayUdps = new Array();
			var storeUdps = this.getStore();
			for(var i = 0;i< storeUdps.getCount();i++){
				var item = storeUdps.getAt(i);
				var data = item.data;
				// want to add type and family to this record
				data.familyId = type;
				var stop = false;
				for ( var int = 0; int < this.udpList.length && stop == false; int++) {
					var udpVal = config.udpList[int];
					if(udpVal.label == data.name){
						data.dataTypeCd = udpVal.dataTypeCd;
						stop = true;
					}
				}
				arrayUdps.push(data);
			}
			return arrayUdps;
	}
	
});

