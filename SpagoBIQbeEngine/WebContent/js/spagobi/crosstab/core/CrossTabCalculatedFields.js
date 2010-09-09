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

Sbi.crosstab.core.CrossTabCalculatedFields = function(){
	
    // public space
	return {

	//execute the calculations 
    calculateCF: function(level, horizontal, op, CFName, crossTab){

    	var headers;
    	if(horizontal){
    		headers = crossTab.columnHeader;
    	}else{
    		headers = crossTab.rowHeader;
    	}
    	var operation = new Array();
    	var operationExpsNames = new Array();
    	var operationExpsIds = new Array();
    	var attributes = new Array();
    	var freshOp = " "+op;
    	var index;
    	var hiddenPannels = new Array();
    	
    	//show all the hidden panels
    	for(var y=0; y<headers[headers.length-1].length; y++){
    		if(headers[headers.length-1][y].hidden){
    			headerEntry = headers[headers.length-1][y];
    			hiddenPannels.push(headerEntry);
    		    headerEntry.show();
    		    var father = headerEntry.father;
    		    while(father!=null && father!=undefined){
    		    	father.thisDimension = father.thisDimension+1;
    		       	if(father.thisDimension == 1){
    		       		father.show();
    		       	}
    		    	father = father.father;
    		    }
    		}
    	}

    	
    	//parse the operation
    	while(freshOp.indexOf("field[")>=0){
    		index =  freshOp.indexOf("field[")+6;
    		operation.push(freshOp.substring(0,index-6));
    		freshOp = freshOp.substring(index);
    		index = freshOp.indexOf("]");
    		operationExpsNames.push(freshOp.substring(0, index));
    		freshOp = freshOp.substring(index+1);
    	}
    	operation.push(freshOp);


    	//operationExpsNames: the name of the elements of the operation, for example z1,z2
    	//operationExpsIds: The ids of the elements inside the list of header
    	//                  es: z1-->[0,2,4,6]  z2-->[1,3,5,7]
    	//					operationExpsIds = [[0,1][2,3],[4,5],[6,7]]
    	var pos =0;
    	var operationExpsIdsItem;
    	for(var j=0; j<headers[level-1].length; j++){
    		operationExpsIdsItem = new Array();
    		for(var y=0; y<headers[level-1][j].childs.length; y++){
	    		for(var i=0; i<operationExpsNames.length; i++){
	    			if(headers[level-1][j].childs[y].name==operationExpsNames[i]){
	    				operationExpsIdsItem.push(pos+y);
	        		}
	    		}
    		}
    		pos = headers[level-1][j].childs.length+ pos;
			if(operationExpsIdsItem.length==operationExpsNames.length){
				operationExpsIds.push(operationExpsIdsItem);
			}
    	}
        	
    	var linesValueForHeader = this.getLinesForCF(level, headers);
    	
    	//For every appearance of the elements of the operation inside the header we calculate the calculated field
    	for(var j=0; j<operationExpsIds.length; j++){
        	var expIds = operationExpsIds[j];
        	
        	//execute the operation
	    	var entries = this.executeOp(operation,horizontal,linesValueForHeader,expIds,crossTab);
	    	var panel1 = headers[level][operationExpsIds[j][0]];
	    	
	    	if(CFName==null){
	    		CFName = "CF";
	    	}
	    	//build the structure of the subtree
	    	var cfNode = this.buildHeadersStructure(CFName, panel1, crossTab);

	    	cfNode.father = panel1.father;
	    	for(var y=0; y<cfNode.childs.length; y++){
	    		cfNode.childs[y].father = cfNode;
	    	}

	    	attributes.push([cfNode, entries]);   
    	}
    	
    	//add the entries in the table
    	for(var j=0; j<attributes.length-1; j++){
    		crossTab.addNewEntries(level,attributes[j][0],headers, attributes[j][1], horizontal,true);
    		//alert(attributes[j][1].length);
    	}
    	if(attributes.length>0){
    		crossTab.addNewEntries(level,attributes[attributes.length-1][0],headers, attributes[attributes.length-1][1], horizontal,false);
    		//alert(attributes[j][1].length);
    	}
    	
    	//hide the hidden panels
    	for(var y=0; y<hiddenPannels.length; y++){
    		var headerEntry=hiddenPannels[y];
    		headerEntry.hide();
	    	var father = headerEntry.father;
	    	while(father!=null && father!=undefined){
	    		father.thisDimension = (father.thisDimension-1);
	        	if(father.thisDimension == 0){
	        		father.hide();
	        	}
	    		father = father.father;
	    	}
    	}

    	for(var x=0; x<headers.length;x++){
    		for(var y=0; y<headers[x].length;y++){
    			if(headers[x][y].horizontal){
    				headers[x][y].updateStaticDimension(headers[x][y].height);
    			}else{
    				headers[x][y].update();
    			}
    		}
    	}
    	
    	crossTab.reloadHeadersAndTable();
    	
    }
    
    //Execute the operation: extracts the columns/rows 
    //op: the skeleton of the operation
    //horizontal: as usual
    //linesValueForHeader
    //expIds: the ids of the columns or rows that stay for the elements of the operation 
    ,executeOp: function(op, horizontal, linesValueForHeader, expIds, crossTab){
    	var exps = new Array();
    	var CF = new Array();
    	var lineLength;
    	var listOfExp  = new Array();

    	if(horizontal){
    		lineLength = crossTab.entries.getEntries().length;
    	}else{
    		lineLength = crossTab.entries.getEntries()[0].length;
    	}

  	   	for(var j=0; j<linesValueForHeader.length; j++){
  	   		var CFFresh = new Array();
  	   		if(linesValueForHeader[j].length>0){
  	   			var y;
  	   			exps = new Array();
  	   			//get the lines and the columns
  	   			for(y=0; y<expIds.length; y++){
  	   				if(linesValueForHeader[j][expIds[y]]==null || linesValueForHeader[j][expIds[y]]==undefined ){
  				    	break;
  	   				}else{
  	   					if(horizontal){
  	   						exps.push(crossTab.entries.getColumn(linesValueForHeader[j][expIds[y]]));
  	   					}else{
  	   						exps.push(crossTab.entries.getRow(linesValueForHeader[j][expIds[y]]));	
  	   					}
  	   				}
  	   			}
  	   			if(y<expIds.length){
  	   				for(var i=0; i<lineLength; i++){
			    		CFFresh.push("NA");
			    	}
  	   			}else{
			    	for(var m=0; m<exps[0].length; m++){//per ogni riga/colonna
			    		listOfExp  = new Array();
			    		for(var i=0; i<exps.length; i++){//estraggo gli indici delle colonne/righe
				    		listOfExp.push(exps[i][m]);
					    }
			    		CFFresh.push(""+this.executeSingleOp(listOfExp,op));
				    }
  	   			}
  	   			CF.push(CFFresh);
  	   		}
    	}
    	return CF;
    }
    
    //This methot perform the operation
    //the input is the skeleton of the operation and the values of the variables
    , executeSingleOp: function(listOfExp, op){
    	var maxDigitAfterComma = Sbi.settings.qbe.crosstab.calculatedfield.maxDigitAfterComma;
    	var indexComma = 0;
    	var operation ="";
    	var i=0;
    	for(i=0; i<op.length-1; i++){
    		operation = operation+op[i];
//    		indexComma = listOfExp[i].indexOf('.');
//    		if(indexComma > 0 && (listOfExp[i].length-indexComma-1)>maxDigitAfterComma){
//    			maxDigitAfterComma = (listOfExp[i].length-indexComma-1);
//    		}
    		
    		operation = operation+listOfExp[i];
    		if(listOfExp[i]=="NA" || listOfExp[i]=="null"){
    			return "NA";
    		}
    	}
    	operation = operation + op[i];
    	var evalued = eval(operation);
    	return evalued;  
//    	return Math.round(evalued*Math.pow(10,maxDigitAfterComma))/Math.pow(10,maxDigitAfterComma);  
    }
    
    //Lista i cui indici sono le foglie.
    //esempio:
	//		 ----------------
	//		 |     k        |
	//		 ----------------
	//		 |  y  |  x     |
	//   	 ----------------
	//	 	 |y1|y2|x1|x2|x3|
	//-----------------------
	//| | |x2|  |  |  |  |  |
	//| | |------------------
	//| |x|x3|  |  |  |  |  |
	//| |--------------------
	//|k| |x1|  |  |  |  |  |
	//| |y|------------------
	//| | |x2|  |  |  |  |  |
	//| | |------------------
	//| | |x3|  |  |  |  |  |
	//-----------------------
    //x+y-->  linesValueForHeader= [[0,3],[1,4],[,2]] where [0,3] is the position of x2 in the headers, [1,4] of x3 and [,2] of x1. 
    // The first value of x1 is null because in the first group x1 doesn't appear
    , getLinesForCF: function(level, headers){
    	var headersNames= new Array();
    	var linesValueForHeader = new Array();
    	var nodeColumns;
    	
    	if(level==headers.length-1){
    		linesValueForHeader.push(new Array());
        	for(var i=0; i<headers[level].length; i++){
        		linesValueForHeader[0].push(i);
        	}  	
        	return linesValueForHeader;
    	}
    	
		this.leafs = new Array();
    	
    	nodeColumns = this.breadthFirstSearch(headers[level][0],"", true);
    	
    	for(var i=0; i<headers[headers.length-1].length; i++){
    		linesValueForHeader[i] = new Array();
    	}
    	
    	for(var i=0; i<nodeColumns.length; i++){
        	headersNames[i]=nodeColumns[i];
        	linesValueForHeader[i][0]=(i);
    	}
    	
    	var depth=headers[level][0].thisDimension;
    	for(var i=1; i<headers[level].length; i++){
    		nodeColumns = this.breadthFirstSearch(headers[level][i],"",true);
    		this.checkHeaders(depth, nodeColumns, headersNames, linesValueForHeader,i);
    		depth = depth+ headers[level][i].thisDimension;
    	}   
    	return this.sort(linesValueForHeader, headersNames);
    }
    
    
    ,checkHeaders: function(startColon, nodeColumns, headersNames, linesValueForHeader,pos){
    	var i;
    	for(var j=0; j<nodeColumns.length; j++){
        	for(i=0; i<headersNames.length; i++){
        		if((nodeColumns[j]==headersNames[i])){
        			linesValueForHeader[i][pos]=(startColon+j);
        			break;
        		}
        	}
        	if(i==headersNames.length){
        		headersNames.push(nodeColumns[j]);
        		linesValueForHeader[i][pos]=(startColon+j);
        	}
    	}
    }
  
    //sort the linesValueForHeader
    //[[0,6,12,18],		[[,5,11,17],
    //[1,7,13,19],  =>  [0,6,12,18],
    //[,5,11,17]]		[1,7,13,19]]
    ,sort: function(linesValueForHeader, headersNames){
    	var min;
    	for(var pos=0; pos<linesValueForHeader.length; pos++){
    		if(linesValueForHeader[pos].length==0){
    			linesValueForHeader.length=pos;
    			break;
    		}
    	}
    	
    	for(var pos=1; pos<linesValueForHeader[0].length; pos++){
		    for(var y=0; y<linesValueForHeader.length; y++){
		    	if(linesValueForHeader[y][pos]==null){
		    		min = y;
		    	}else{
		    		min = this.findMin(linesValueForHeader, pos, y);
		    	}
	    		var fresh = linesValueForHeader[y];
	    		var freshN = this.leafs[y];
		    	linesValueForHeader[y] = linesValueForHeader[min];
		    	linesValueForHeader[min] = fresh;
		    	this.leafs[y] = this.leafs[min];
		    	this.leafs[min]= freshN;
    		}
    	}
    	var freshLeafs = new Array();
    	for(var y=0; y<this.leafs.length; y++ ){//cerco la prima ripetizione. se ho x1 x2 x3 x2 x1 x3 allora quando analizzo il secondo x2 mi fermo e setto quella come dimensione dell'array, in modo da non avere doppioni
     		if(freshLeafs.indexOf(this.leafs[y])<0){
	    		freshLeafs.push(this.leafs[y]);
    		}
    	}
    	
    	this.leafs = freshLeafs;
    	return linesValueForHeader;
    }
    
    
    ,findMin: function(linesValueForHeader, pos, start){
    	var min=999;
    	var minPos;
    	for(var y=start; y<linesValueForHeader.length; y++){
    		if(linesValueForHeader[y][pos]<min){
    			min = linesValueForHeader[y][pos];
    			minPos = y;
    		}
    	}
    	return minPos;
    }
    
    //Starting from the node "node" visits all the childs and links the name of the father to the name of the childs: kz1x1, kz1x2,kz1x3,kz2x1...
    ,breadthFirstSearch: function(node, familyPrefix, first){
    	
    	var arrayToReturn = new Array(); 
    	var childs =node.childs;

    	if(childs.length==0){
   			this.leafs.push(node.name);  		
    		arrayToReturn.push(familyPrefix+node.name);
    	}else{
    		if(!first){
    			familyPrefix = familyPrefix+node.name;
    		}
    		for(var i=0; i<childs.length; i++){
    			arrayToReturn = arrayToReturn.concat(this.breadthFirstSearch(childs[i], familyPrefix));
    		}
    	}
    	return arrayToReturn;
    }
    
    //build the structure of the subtree to add
    ,buildHeadersStructure: function(name, node, crossTab){
    	//alert(node.name);
    	if(name!=null){
    		var clonedNode = new Sbi.crosstab.core.HeaderEntry(name, node.thisDimension, node.horizontal, node.level, node.width, node.height);
    	}else{
    		var clonedNode = new Sbi.crosstab.core.HeaderEntry(node.name, node.thisDimension, node.horizontal, node.level, node.width, node.height);
    	}
    	clonedNode.type='CF';
    	crossTab.setHeaderListener(clonedNode,node.horizontal); 
    	var childs =node.childs;
    	var newDimension=0;
    	//if the node is a leaf
    	if(node.childs.length==0){
    		return clonedNode;
    	}
    	if(node.childs[0].childs.length>0){
    		for(var i=0; i<childs.length; i++){
    			clonedNode.childs.push(this.buildHeadersStructure(childs[i].name, childs[i], crossTab));
    			clonedNode.childs[i].father = clonedNode;
    			newDimension = newDimension+clonedNode.childs[i].thisDimension;
    		}
    		clonedNode.thisDimension = newDimension;
    	}else{
        	//In a calculated field the leafs are the set of all possible leafs. 
        	//So when the node is the father of a leaf it sets as childs the set of all possible leafs
    		clonedNode.childs = new Array();
    		for(var t=0; t<this.leafs.length; t++){
            	var clonedNodeF = new Sbi.crosstab.core.HeaderEntry(this.leafs[t], 1, clonedNode.horizontal, clonedNode.level+1,node.childs[0].width, node.childs[0].height);
            	clonedNodeF.type='CF';
            	crossTab.setHeaderListener(clonedNodeF,clonedNode.horizontal); 
            	clonedNodeF.father = clonedNode;
            	clonedNode.childs.push(clonedNodeF);
    		}
    		clonedNode.thisDimension = this.leafs.length;
    	}
    	return clonedNode;
    }
    
    //utility function for print a subtree
    ,printNode: function(node){
    	var leafsName = [node.name];
    	if(node.childs.length>0){
    		for(var p=0; p< node.childs.length; p++){
    			leafsName = leafsName.concat(this.printNode(node.childs[p]));
    		}
    	}
    	return leafsName;
    }
	};
}();
