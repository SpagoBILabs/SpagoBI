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
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.crosstab.core");

Sbi.crosstab.core.CrossTabData = function(entries) {
    this.entries =entries;
};
	
Ext.extend(Sbi.crosstab.core.CrossTabData , Object, {
	entries: null // matrix with the data 

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

	 //serialize the data (it ads also the sums)
 	, serializeEntries: function(rowsum, columnsum ){

 		var serializedEntries = this.getEntries();
 		
 		if(rowsum!=null){
 			for(var i=0; i<rowsum.length; i++){
 				serializedEntries[i].push(rowsum[i]);
 			}	
 		}
    	if(columnsum!=null){
    		serializedEntries.push(columnsum);
    	}
		return serializedEntries;
	}
  
       
});