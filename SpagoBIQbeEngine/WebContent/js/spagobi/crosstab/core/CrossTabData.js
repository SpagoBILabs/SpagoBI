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
 	, serializeEntries: function(rowsumO, columnsumO, misuresOnRow, percenton){
 				
 		var rowsum = null;
 		var columnsum = null;
 		var tempSerializedEntries = this.getEntries();
 		var serializedEntries = Sbi.crosstab.core.CrossTabShowHideUtility.cloneArray(tempSerializedEntries);
 		var superSumArray = new Array();
 		var visibleRows;
 		var visibleColumns;
 		
 		//Calculate the sum of sums
 		if(rowsumO!=null && rowsumO!=undefined && rowsumO.length>1){
 			for(var x=0; x<rowsumO.length; x++){
 				var freshSum=0;
 	 			for(var y=0; y<rowsumO[x].length; y++){
 	 				freshSum = freshSum+parseFloat(rowsumO[x][y]);
 	 			}
 	 			superSumArray.push(Sbi.qbe.commons.Format.number(freshSum,'float'));
 			}
 		}else if(rowsumO!=null && rowsumO!=undefined && columnsumO.length>1){
 			for(var x=0; x<columnsumO.length; x++){
 				var freshSum=0;
 	 			for(var y=0; y<columnsumO[x].length; y++){
 	 				freshSum = freshSum+parseFloat(columnsumO[x][y]);
 	 			}
 	 			superSumArray.push(Sbi.qbe.commons.Format.number(freshSum,'float'));
 			}
 		}
 		
		if(percenton=='column'){
			if(!this.misuresOnRow){
				for(var i=0; i< serializedEntries.length; i++){
		        	for(var j=0; j< serializedEntries[0].length; j++){
		        		serializedEntries[i][j] = Sbi.qbe.commons.Format.number(serializedEntries[i][j],'float') +" ("+ Sbi.qbe.commons.Format.number(100*parseFloat(serializedEntries[i][j])/parseFloat(columnsumO[0][j]), 'float')+"%)";
		        	}
		    	}
			}else{
				for(var i=0; i< serializedEntries.length; i++){
		        	for(var j=0; j< serializedEntries[0].length; j++){
		        		serializedEntries[i][j] = Sbi.qbe.commons.Format.number(serializedEntries[i][j],'float') +" ("+ Sbi.qbe.commons.Format.number(100*parseFloat(serializedEntries[i][j])/parseFloat(columnsumO[i%columnsumO.length][j]), 'float')+"%)";
		        	}
		    	}	
			}
		} else if(percenton=='row'){
			
			if(this.misuresOnRow){
				for(var i=0; i< serializedEntries.length; i++){
		        	for(var j=0; j< serializedEntries[0].length; j++){
		        		serializedEntries[i][j] = Sbi.qbe.commons.Format.number(serializedEntries[i][j],'float') +" ("+Sbi.qbe.commons.Format.number(100*parseFloat(serializedEntries[i][j])/parseFloat(rowsumO[0][i]), 'float')+"%)";
		        	}
		    	}	
			}else{
				for(var i=0; i< serializedEntries.length; i++){
		        	for(var j=0; j< serializedEntries[0].length; j++){
		        		serializedEntries[i][j] = Sbi.qbe.commons.Format.number(serializedEntries[i][j],'float') +" ("+Sbi.qbe.commons.Format.number(100*parseFloat(serializedEntries[i][j])/parseFloat(rowsumO[j%rowsumO.length][i]), 'float')+"%)";
		        	}
		    	}	
			}
		}
 		
    	if(columnsumO!=null && columnsumO!=undefined){
    		columnsum = Sbi.crosstab.core.CrossTabShowHideUtility.cloneArray(columnsumO);
    		this.addPrefix(columnsum,'[sum]');
    		for(var j=0; j<columnsum.length; j++){
 				serializedEntries.push(columnsum[j]);
 			}
    	}
    	
    	if(rowsumO!=null && rowsumO!=undefined){
    		rowsum = Sbi.crosstab.core.CrossTabShowHideUtility.cloneArray(rowsumO);
    	}
 		
    	//ADD the sum of sum
 		if(rowsumO!=null && rowsumO!=undefined && columnsumO!=null && columnsumO!=undefined){
    		if(!misuresOnRow){
    			for(var u=0; u<superSumArray.length; u++){
    				rowsum[u].push(superSumArray[u]);
    			}
    			
         		if(serializedEntries.length>rowsum[0].length){
         			var rowsumRowNumber = rowsum[0].length;
         			for(var u=0; u<superSumArray.length; u++){
            			for(var j=rowsumRowNumber; j<serializedEntries.length; j++){
            				rowsum[u].push(superSumArray[u]);
            			}
        			}  			
         		}
    		}else{
    			for(var u=0; u<superSumArray.length; u++){
    				rowsum[0].push(superSumArray[u]);
    			}
    			
    			var rowSize = serializedEntries[0].length;
    			
         		if(serializedEntries[serializedEntries.length-superSumArray.length].length<rowSize){

         			for(var u=0; u<superSumArray.length; u++){
         				serializedEntries[serializedEntries.length-superSumArray.length+u].push(superSumArray[u]);
        			}  	
         		}
    		}
    	}

 		if(rowsumO!=null){
 			this.addPrefix(rowsum,'[sum]');
 			for(var i=0; i<rowsum[0].length; i++){
 	 			for(var j=0; j<rowsum.length; j++){
 	 				serializedEntries[i].push(rowsum[j][i]);
 	 			}
 			}	
 		}
 		
		return serializedEntries;
	}
 	
 	, addPrefix: function(array, prefix){
 		for(var i=0; i<array.length; i++){
 			if(!(array[i] instanceof Array)){
 				array[i] = prefix+array[i];
 			}else{
 				for(var j=0; j<array[i].length; j++){
 					array[i][j] = prefix+array[i][j];
 				}
 			}
 		}
 	}
       
});