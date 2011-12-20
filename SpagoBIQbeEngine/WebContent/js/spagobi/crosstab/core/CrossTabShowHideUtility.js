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

Sbi.crosstab.core.CrossTabShowHideUtility = function(){
    // public space
	return {

		//hide a measure
	    showHideMeasure: function(measure, hide, horizontal, crossTab){
	    	var headers;
	    	if(horizontal){
	    		headers = crossTab.columnHeader;
	    	}else{
	    		headers = crossTab.rowHeader;
	    	}
	    	for(var i=0; i<headers[headers.length-1].length; i++){
	    		if(headers[headers.length-1][i].name == measure){
	        		if(hide){
	        			this.hideLine(i, horizontal, crossTab, true);
	        		}else{
	        			this.showLine(i, horizontal, crossTab, true);
	        		}
	    		}
	    	}
	    	crossTab.reloadHeadersAndTable();
	    }
	
	    //show/hide a node and all its child
	    //node: the node to hide
	    //hide: true for hide, false for show
	    //lazy: if true the table is not updated
	    , showHideNode: function(node, hide, lazy, crossTab){
	    	var i=0;
	    	var startHeight, endHeight;
	    	var headers;  	
	    	
	    	if(node.horizontal){
	    		headers = crossTab.columnHeader;
	    	}else{
	    		headers = crossTab.rowHeader;
	    	}
	    	
	    	var leafs = node.getLeafs();
	    	if(leafs.length==0){//if the node is already a leaf
	    		leafs.push(node);
	    	}
	    	
	    	startHeight=headers[headers.length-1].indexOf(leafs[0]);
	    	endHeight=leafs.length+startHeight;
	
	    	for(var y=startHeight; y<endHeight; y++){
	    		if(hide){
	    			this.hideLine(y, node.horizontal, crossTab, true);
	    		}else{
	    			this.showLine(y, node.horizontal, crossTab, true);
	    		}
	    	}
	
	    	if(!lazy){
	    		crossTab.reloadHeadersAndTable();
	    	}
	    }
	
	    //show/hide the node and all its brothers with the same name
	    //node: the node to hide
	    //hide: true for hide, false for show  
	    , showHideAllNodes: function(node, hide, crossTab, lazy){
	    	var headers;
	    	
	    	if(node.horizontal){
	    		headers = crossTab.columnHeader;
	    	}else{
	    		headers = crossTab.rowHeader;
	    	}
	    	
	    	var header=headers[node.level];
	    	for(var y=0; y<header.length; y++){
	    		if(node.name == header[y].name){
	   				this.showHideNode(header[y], hide, true, crossTab);
	    		}
	    	}
	    	if(!lazy){
	    		crossTab.reloadHeadersAndTable();
	    	}
	    }
	    
	    //Hide a line
	    , hideLine : function(lineNumber, horizontal, crossTab, lazy){

	    	var headerEntry;
	    	if(horizontal){
	    		headerEntry = crossTab.columnHeader[crossTab.columnHeader.length-1][lineNumber];
	    	}else{
	    		headerEntry = crossTab.rowHeader[crossTab.rowHeader.length-1][lineNumber];
	    	}
	    	if(!headerEntry.hidden){
	    		headerEntry.hide();
	    		
		    	var father = headerEntry.father;
		    	while(father!=null && father!=undefined){
		    		father.thisDimension = father.thisDimension-1;
		        	if(father.thisDimension == 0){
		        		father.hide();
		        	}
		        	father.update();
		    		father = father.father;
		    	}
	    	}
	    	if(!lazy){
	    		crossTab.reloadHeadersAndTable();
	    	}
	    }
	    
	    //Show a line
	    , showLine : function(lineNumber, horizontal, crossTab, lazy){

	    	var headerEntry;
	    	if(horizontal){
	    		headerEntry = crossTab.columnHeader[crossTab.columnHeader.length-1][lineNumber];
	    	}else{
	    		headerEntry = crossTab.rowHeader[crossTab.rowHeader.length-1][lineNumber];
	    	}
	    	if(headerEntry.hidden){
		    	headerEntry.show();
		    	var father = headerEntry.father;
		    	while(father!=null && father!=undefined){
		    		father.thisDimension = father.thisDimension+1;
		        	if(father.thisDimension == 1){
		        		father.show();
		        	}
		        	father.update();
		    		father = father.father;
		    	}
	    	}
	    	if(!lazy){
	    		crossTab.reloadHeadersAndTable();
	    	}
	    }
	    
	 	, cloneArray: function(array){
	 		if(array.length>0 && (array[0] instanceof Array)){
	 			var newArray = new Array();
	 			for(var i=0; i<array.length; i++){
	 				newArray.push(this.cloneArray(array[i]));
	 	 		}
	 			return newArray;
	 		}else{
	 			return array.slice();
	 		}

	 	}
	};
}();