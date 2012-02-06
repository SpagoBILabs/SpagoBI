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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.crosstab.core");

Sbi.crosstab.core.CrossTabData = function(entries) {
    this.entries =entries;
};
	
Ext.extend(Sbi.crosstab.core.CrossTabData , Object, {
	entries: null // matrix with the data 
	
	, getColumnsNumber: function(){
		return this.entries[0].length;
	}

	, getRowsNumber: function(){
		return this.entries.length;
	}

	,getEntries: function(){
		return this.entries;
	}
    
	,setEntries: function(entries){
		this.entries =entries;
	}
    
    //returns the i-th column
    , getColumn : function(columnId){
    	var column = new Array();
    	var rows = this.entries.length;
    	for(var i=0; i<rows; i++){
    		column.push(this.entries[i][columnId]);
    	}
    	return column;
    }
    
    //returns the i-th row
    , getRow : function(rowId){
    	return this.entries[rowId];
    }

    //add the columns starting from the i-th position
    , addColumns : function(columnId, columns){
    
    	var rows = this.entries.length;
    	for(var k=0; k<rows; k++){
        	for(var i=this.entries[k].length-1; i>=columnId; i--){
        		this.entries[k][columns.length+i] = this.entries[k][i];
        	}
    
        	for(var i=0; i<columns.length; i++){
        		this.entries[k][i+columnId] = columns[i][k];
        	}
    	}
    }
    
    //add the rows starting from the i-th position
    , addRows : function(rowId, rows){
    	for(var i=this.entries.length-1; i>=rowId; i--){
    		this.entries[rows.length+i] = this.entries[i];
    		this.entries[i]=null;
    	}
    	for(var i=0; i<rows.length; i++){
    		this.entries[i+rowId] = rows[i];
    	}
    }
    
    //remove the columns between the startId and the endId
    , removeColumns : function(startId, endId){
    	for(var k=0; k<this.entries.length; k++){
        	for(var i=endId; i>=startId && i>=0; i--){
        		this.entries[k][i] = null;
        	}
        	this.entries[k] = this.arrayCompression(this.entries[k]);
    	}
    }
    
    //remove the columns between the startId and the endId
    , removeRows : function(startId, endId){
    	for(var i=endId; i>=startId && i>=0; i--){
    		this.entries[i]=null;
    	}
    	this.entries = this.arrayCompression(this.entries);
    }
    
    //remove the columns between the startId and the endId
    , arrayCompression : function(array){
    	var freshArray = new Array();	
    	for(var i=0; i<array.length; i++){
    		if(array[i]!=null){
    			freshArray.push(array[i]);
    		}
    	}
    	return freshArray;
    }

	 //serialize the data (it ads also the sums)
 	, serializeEntries: function(crosstab){

 		var tempSerializedEntries = this.getEntries();
 		var serializedEntries = Sbi.crosstab.core.CrossTabShowHideUtility.cloneArray(tempSerializedEntries);

		if(crosstab.percenton=='column' || crosstab.percenton=='row'){
			for(var i=0; i< serializedEntries.length; i++){
		       	for(var j=0; j< serializedEntries[0].length; j++){
		       		var measureName = null;
					if (crosstab.misuresOnRow) {
						measureName = crosstab.rowHeader[crosstab.rowHeader.length-1][i].name;
					} else {
						measureName = crosstab.columnHeader[crosstab.columnHeader.length-1][j].name;
					}
					var measureMetadata = crosstab.getMeasureMetadata(measureName);
					var percentValue = crosstab.calculatePercent(serializedEntries[i][j], i, j, measureMetadata, this.entries)
	        		serializedEntries[i][j] = Sbi.qbe.commons.Format.number(serializedEntries[i][j],'float') +" ("+ Sbi.qbe.commons.Format.number(percentValue, 'float')+"%)";
		       	}
		   	}
		}

 		
		return serializedEntries;
	}

       
});