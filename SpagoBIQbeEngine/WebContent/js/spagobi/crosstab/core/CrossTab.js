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

//================================================================
//CrossTab
//================================================================
//
//The cross tab is a grid with headers and for the x and for the y. 
//it's look like this:
//       ----------------
//       |     k        |
//       ----------------
//       |  y  |  x     |
//       ----------------
//       |y1|y2|x1|x2|x3|
//-----------------------
//| | |x1|  |  |  |  |  |
//| | |------------------
//| |x|x2|  |  |  |  |  |
//| | |------------------
//|k| |x3|  |  |  |  |  |
//| |--------------------
//| | |y1|  |  |  |  |  |
//| |y|------------------
//| | |y2|  |  |  |  |  |
//-----------------------
//
//The grid is structured in 4 panels:
//         -----------------------------------------
//         |emptypanelTopLeft|    columnHeaderPanel|
// table=  -----------------------------------------
//         |rowHeaderPanel   |    datapanel        | 
//         -----------------------------------------

Ext.ns("Sbi.crosstab.core");

Sbi.crosstab.core.CrossTab = function(config) {
    this.calculatedFields = new Array();
    
	Ext.apply(this, config);
	
	this.manageDegenerateCrosstab(this.rowHeadersDefinition, this.columnHeadersDefinition);
	this.fontSize = 12;
	this.entries = new Sbi.crosstab.core.CrossTabData(this.entries);
    this.rowHeader = new Array();
    this.build(this.rowHeadersDefinition, 0, this.rowHeader, false);
    this.setFathers(this.rowHeader);
    this.setDragAndDrop(this.rowHeader, false, this);
    this.rowHeader[0][0].hidden=true;//hide the fake root header
    this.rowHeaderPanel = this.buildHeaderGroup(this.rowHeader, false);
    
    this.columnHeader = new Array();
    this.build(this.columnHeadersDefinition, 0, this.columnHeader, true);
    this.setFathers(this.columnHeader);
    this.setDragAndDrop(this.columnHeader, true, this);
    this.columnHeader[0][0].hidden=true;//hide the fake root header
    this.columnHeaderPanel = this.buildHeaderGroup(this.columnHeader, true);

    this.addDDArrowsToPage();
    
    var c = {
  		layout:'fit',
  		border: false,
  		defaults: {autoScroll: true},
		padding : 10
	};

    this.addEvents();
	this.on('afterrender', function(){
		this.calculatePartialSum();
	}, this);
    
    
    if(this.calculatedFields!=null && this.calculatedFields.size>0){
    	
    	this.calculatedFields = calculatedFields;
    	this.on('afterrender', function(){
    		for(var i=0; i<this.calculatedFields.length; i++){
    			Sbi.crosstab.core.CrossTabCalculatedFields.calculateCF(this.calculatedFields[i].level, this.calculatedFields[i].horizontal, this.calculatedFields[i].operation, this.calculatedFields[i].name, this);
    		}
    	}, this);
    }
    
    Sbi.crosstab.core.CrossTab.superclass.constructor.call(this, c);
};
	
Ext.extend(Sbi.crosstab.core.CrossTab, Ext.Panel, {
	entries: null // matrix with the data 
    ,rowHeaderPanelContainer: null //Panel with the header for the rows
    ,columnHeaderPanelContainer: null //Panel with the header for the columns
    ,rowHeader: null // Array. Every entry contains an array of HeaderEntry. At position 0 there is the external headers.
    ,columnHeader: null // Array. Every entry contains an array of HeaderEntry. At position 0 there is the external headers.
    ,emptypanelTopLeft: null // The top-left corner of the table
    ,datapanel: null // The panel with the table of data
    ,rowHeaderPanel:null // An array. Every entry contains a Panel wich items are the rowHeader. i.e: rowHeaderPanel[0]= new Ext.Panel(...items :  rowHeader[0]), rowHeaderPanel[1]= new Ext.Panel(...items :  rowHeader[1])
    ,columnHeaderPanel: null // An array. Every entry contains a Panel wich items are the columnHeader. i.e: columnHeaderPanel[0]= new Ext.Panel(...items :  columnHeader[0]), columnHeaderPanel[1]= new Ext.Panel(...items :  columnHeader[1])
    ,table: null //the external table with 2 rows and 2 columns. It contains emptypanelTopLeft, columnHeaderPanel, rowHeaderPanel, datapanel
    ,checkBoxWindow: null //window with the checkBoxs for hide or show a column/line
	,columnWidth: 100
	,rowHeight: 25
	,entriesPanel : null
	,crossTabCFWizard: null
	,clickMenu: null
	,withRowsSum: null
	,withColumnsSum: null
	,withRowsPartialSum: null
	,withColumnsPartialSum: null
	,calculatedFields: null
	,misuresOnRow: null
	,measuresMetadata: null // metadata on measures: it is an Array, each entry is a json object with name, type and (in case of date/timestamp) format of the measure
	
	, manageDegenerateCrosstab: function(rowHeadersDefinition, columnHeadersDefinition) {
		if (rowHeadersDefinition.length == 1) { // degenerate crosstab (everything on columns)
			var array = ["Data"];
			var wrapper = [array];
			rowHeadersDefinition.push(wrapper);
		}
		if (columnHeadersDefinition.length == 1) { // degenerate crosstab (everything on rows)
			var array = ["Data"];
			var wrapper = [array];
			columnHeadersDefinition.push(wrapper);
		}
	}
	
    //================================================================
    // Loads and prepare the table with the data
    //================================================================
    
    // takes the data definition and prepare an ordered array with a panel for every tab cell
    // (in position 0 there is the cell at position (0,0) in the table, 
    // in positin 1 the cell (0,1) ecc..) 
    ,getEntries : function(){
    	var entries = this.entries.getEntries();
    	var toReturn = new Array();
    	var visiblei=0;//the visible row index. If i=2 and row[0].hidden=true, row[1].hidden=false  then  visiblei=i-1 = 1
    	
    	for(var i=0; i<entries.length; i++){
    		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
    			var visiblej=0;
    			partialSum =0;
    			for(var j=0; j<entries[i].length; j++){
    				if(!this.columnHeader[this.columnHeader.length-1][j].hidden){
    					// get measure metadata (name, type and format)
    					var measureName = null;
    					if (this.misuresOnRow) {
    						measureName = this.rowHeader[this.rowHeader.length-1][i].name;
    					} else {
    						measureName = this.columnHeader[this.columnHeader.length-1][j].name;
    					}
    					var measureMetadata = this.getMeasureMetadata(measureName);
    					// in case of calculated fields made with measures, measureMetadata is null!!!
    					var datatype = measureMetadata !== null ? measureMetadata.type : 'float';
    					var format = (measureMetadata !== null && measureMetadata.format !== null && measureMetadata.format !== '') ? measureMetadata.format : null;
    					// get also type of the cell (data, CF = calculated fields, partialsum)
    					var celltype = this.getCellType(this.rowHeader[this.rowHeader.length-1][i], this.columnHeader[this.columnHeader.length-1][j]);
    					// put measure value and metadata into an array
    					var a = new Array();
    					a.push(entries[i][j]);
    					a.push('['+visiblei+','+visiblej+']');
    					a.push(datatype);
    					a.push(format);
    					a.push(celltype);
	    				toReturn.push(a);
	    				visiblej++;
    				}   				
    			}
    			visiblei++;
        	}	
    	}
    	return toReturn;
    }

	// returns the type of the cell (data, CF = calculated fields, partialsum) by the cell headers
	, getCellType: function(rowHeader, columnHeader) {
		if (rowHeader.type == 'CF' || columnHeader.type == 'CF') {
			return 'CF';
		}
		if (rowHeader.type == 'partialsum' || columnHeader.type == 'partialsum') {
			return 'partialsum';
		}
		return 'data';
	}

	, getMeasureMetadata: function (measureName) {
		for (var i = 0; i < this.measuresMetadata.length; i++) {
			if (this.measuresMetadata[i].name === measureName) {
				return this.measuresMetadata[i];
			}
		}
		return null;
	}
	    	
	//returns the number of the visible (not hidden) rows		
    ,getRowsForView : function(){
    	var count =0;
    	for(var i=0; i<this.rowHeader[this.rowHeader.length-1].length; i++){
    		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
    			count++
    		}
    	}
    	return count;
    }
    
    //returns the number of the visible (not hidden) columns
    , getColumnsForView : function(){
    	var count =0;
    	for(var i=0; i<this.columnHeader[this.columnHeader.length-1].length; i++){
    		if(!this.columnHeader[this.columnHeader.length-1][i].hidden){
    			count++
    		}
    	}
    	return count;
    }
    
    //highlight a row of the table by adding a class to the cell elements (the additional class sets a background color)
    //i: the number of the row (visible)
    ,highlightRow: function(i){
		for(var y = 0; ; y++){
			//var el = Ext.get('['+i+','+y+']');
			//if (el == null) return;
			//el.addClass('crosstab-table-cells-highlight');
	   		var cel = document.getElementById('['+i+','+y+']');
	   		if (cel == null) return;
	   		cel.className += ' crosstab-table-cells-highlight'; // adding class crosstab-table-cells-highlight
		}
    }
    
    //remove highlight of a row of the table by removing an additional class 
    //i: the number of the row (visible)
    ,removeHighlightOnRow: function(i){
		for(var y = 0; ; y++){
			//var el = Ext.get('['+i+','+y+']');
			//if (el == null) return;
			//el.removeClass('crosstab-table-cells-highlight');
	   		var cel = document.getElementById('['+i+','+y+']');
	   		if (cel == null) return;
	   		cel.className = cel.className.replace(/\bcrosstab-table-cells-highlight\b/,''); // removing class crosstab-table-cells-highlight
		}
    }
    
    //highlight a column of the table by adding a class to the cell elements (the additional class sets a background color)
    //j: the number of the column (visible)
    ,highlightColumn: function(j){
		for (var y = 0; ; y++) {
			//var el = Ext.get('['+y+','+j+']');
			//if (el == null) return;
			//Ext.get('['+y+','+j+']').addClass('crosstab-table-cells-highlight');
	   		var cel = document.getElementById('['+y+','+j+']');
	   		if (cel == null) return;
	   		cel.className += ' crosstab-table-cells-highlight'; // adding class crosstab-table-cells-highlight
		}
    }
     
    //remove highlight of a column of the table by removing an additional class 
    //j: the number of the column (visible)
    ,removeHighlightOnColumn: function(j){
 		for (var y = 0; ; y++) {
			//var el = Ext.get('['+y+','+j+']');
			//if (el == null) return;
			//Ext.get('['+y+','+j+']').removeClass('crosstab-table-cells-highlight');
	   		var cel = document.getElementById('['+y+','+j+']');
	   		if (cel == null) return;
	   		cel.className = cel.className.replace(/\bcrosstab-table-cells-highlight\b/,''); // removing class crosstab-table-cells-highlight
 		}
    }
     
     //remove highlight from all the cell of the table by removing an additional class 
     ,removeHighlightOnTable: function(){
    	var entries = this.entries.getEntries();
		for(var i = 0; i<entries.length; i++){
			for(var y = 0; y<entries[0].length; y++){
				//var el = Ext.get('['+i+','+y+']');
				//if(el == null){
				//	break;
				//}
				//el.removeClass('crosstab-table-cells-highlight');
		   		var cel = document.getElementById('['+i+','+y+']');
		   		if (cel == null) break;
		   		cel.className = cel.className.replace(/\bcrosstab-table-cells-highlight\b/,''); // removing class crosstab-table-cells-highlight
			}
		}
     }
     
     //serialize the crossTab: 
     //Create a JSONObject with the properties: data, columns, rows
     ,serializeCrossTab: function(){
    	 var columnsum = null;
    	 var rowsum = null;
    	 if(this.withColumnsSum){
    		 columnsum = this.columnsSum();
    	 }
    	 if(this.withRowsSum){
    		 rowsum = this.rowsSum();
    	 }
    	 var serializedCrossTab = {}; 
    	 serializedCrossTab.data= this.entries.serializeEntries(rowsum, columnsum);
    	 serializedCrossTab.columns=  this.serializeHeader(this.columnHeader[0][0]);
    	 serializedCrossTab.rows=  this.serializeHeader(this.rowHeader[0][0]);
    	 return serializedCrossTab;
     }
     
   //serialize a header and all his the subtree
 	 ,serializeHeader: function(header){
  		var node = {};
  		node.node_key =  header.name;
 		if(header.childs.length>0){
 			var nodeChilds = new Array();
 			for(var i=0; i<header.childs.length; i++){
 				nodeChilds.push(this.serializeHeader(header.childs[i]));
 			}
 			node.node_childs = nodeChilds;
 		}
 		return node;
 	}
 	    
    
    //================================================================
    // Build the headers
    //================================================================
	//    ----------------
	//    |     k        |
	//    ----------------
	//    |  y  |  x     |
	//    ----------------
	//    |y1|y2|x1|x2|x3|
	//    ----------------
    //Recursive function that builds the header panels (i.e. columnHeader and rowHeader)  
    //line: the definition of a subtree (for example ["x",[["x1"],["x2"],["x3"]] or ["y",[["y1"],["y2"]]])
    //level: the header level. For example the level of x is 1, for x1 is 2
    //headers: Or columnHeader or rowHeader
    //horizontal: true for columnHeader and false for rowHeader 
     , build : function(line, level, headers, horizontal){
		var name = line[0];
		var thisDimension;
    	if(line.length==1){
    		thisDimension = 1;
    	}else{
    		var t=0;
    		var items = line[1];
    		for(var i=0; i<items.length; i++){
    			t= t+this.build(items[i], level+1, headers, horizontal);
    		}
    		thisDimension =t;
    	}
    	p = new Sbi.crosstab.core.HeaderEntry(name, thisDimension, horizontal, level);
    	this.setHeaderListener(p);

    	if(headers[level]==null){
    		headers[level]= new Array();
    	}
    	headers[level].push(p);
    	return thisDimension;
    }

     //Adds the listeners to the header
    , setHeaderListener: function(header){
     
    	header.addListener({render: function(theHeader) {
			// open the show/hide dialog
    		theHeader.el.on('click', this.headerClickHandler.createDelegate(this, [theHeader], true), this);
			//color the rows/columns when the mouse enter in the header
    		theHeader.el.on('mouseenter', this.headerMouseenterHandler.createDelegate(this, [theHeader, header.horizontal], true), this);
    		theHeader.el.on('mouseleave', this.headerMouseleaveHandler.createDelegate(this, [theHeader], true), this);
			}, scope: this
	  	});
    	
    }
    
    , headerClickHandler: function(event, element, object, theHeader) {
    	var theHandler = function(event, element, object, theHeader) {
			if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible()){
				this.crossTabCFWizard.addField("field["+theHeader.name+"]", theHeader.level, theHeader.horizontal);
			}else{
				if(this.clickMenu!=null){
					this.clickMenu.destroy();
				}
				this.clickMenu = new Sbi.crosstab.core.CrossTabContextualMenu(theHeader, this);
				this.clickMenu.showAt([event.getPageX(), event.getPageY()]);
			}
		}
    	theHandler.defer(100, this, [event, element, object, theHeader]); 
    	// This is a work-around (workaround, work around): without deferring the function call, if you open the calculated fields wizard, 
    	// then close it, and click quickly on a header, the context menu appears for a moment but it immediately disappears.
	}
    
    , headerMouseenterHandler: function(event, htmlElement, o, theHeader, horizontal) {
		if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible() && this.crossTabCFWizard.isActiveLevel(theHeader.level, theHeader.horizontal)){
			theHeader.setWidth(theHeader.getWidth()-2);
			theHeader.setHeight(theHeader.getHeight()-2); 
			theHeader.addClass("crosstab-borderd");
		}

		var start=0;
		var i=0;
		var headers;
		
		if(horizontal){
			headers = this.columnHeader;
		}else{
			headers = this.rowHeader
		}
		
		while(!this.isTheSameHeader(headers[theHeader.level][i],theHeader) && i<headers[theHeader.level].length){
			if(!headers[theHeader.level][i].hidden){
				start = start + headers[theHeader.level][i].thisDimension;
			}
			i++;
		}
		
		if( i<headers[theHeader.level].length){
			var end = start+headers[theHeader.level][i].thisDimension-1;
			if(horizontal){
				for(i=start; i<=end; i++){
					this.highlightColumn(i);
				}
			}else{
				for(i=start; i<=end; i++){
					this.highlightRow(i);
				}
			}
		}
	}
    
    , headerMouseleaveHandler: function(event, htmlElement, o, theHeader) {
		
		if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible() && this.crossTabCFWizard.isActiveLevel(theHeader.level, theHeader.horizontal)){
			theHeader.removeClass("crosstab-borderd");
			theHeader.setWidth(theHeader.getWidth()+2);
			theHeader.setHeight(theHeader.getHeight()+2); 
		}
		this.removeHighlightOnTable();
			
	}
     
     //Sets the father of every HeaderEntry
    , setFathers : function(headers){
    	var heigth;	
    	for(var k=0; k<headers.length-1; k++){
    		var pannels = headers[k];
    		//index of the first child in the headers array
    		var heigthCount=0;
    		var i=0; 
	    	for(var y=0; y<pannels.length; y++){
	    		//index of the last child in the headers array
	    		heigth = pannels[y].thisDimension+heigthCount;
	    		pannels[y].childs = new Array();
	    		while(heigthCount<heigth){
	    			pannels[y].childs.push(headers[k+1][i]);
	    			headers[k+1][i].father = pannels[y];
	    			heigthCount = heigthCount+headers[k+1][i].thisDimension;
	    			i++;
	    		}
	    	}
    	}
    }
    
    //Sets the drag and drop function of every HeaderEntry
    , setDragAndDrop : function(headers, horizontal, myPanel){
	    for(var k=1; k<headers.length; k++){
			var pannels = headers[k];
	    	for(var y=0; y<pannels.length; y++){
	    		pannels[y].draggable = {

	    			// for the shadow of the panel to drag
	    			// draw the arrows
		    	    onDrag : function(e){
	    	            var pel = this.proxy.getEl();
	    	            this.x = pel.getLeft(true);
	    	            this.y = pel.getTop(true);
	    	            var s = this.panel.getEl().shadow;
	    	            if (s) {
	    	                s.realign(this.x, this.y, pel.getWidth(), pel.getHeight());
	    	            }
	    	            
	    	            var arrowTop = document.getElementById('ext-arrow-dd-top');  	            
	    	            arrowTop.style.visibility = "visible";
	    	            
	    	            var brothers =this.getDragData().panel.father.childs;
	    	            var i=0;
	    	        	var father = this.getDragData().panel.father;
	    	        	
	    	            if(horizontal){
	    	            	var arrowBottom = document.getElementById('ext-arrow-dd-bottom');
		    	            arrowBottom.style.visibility = "visible";
		    	            
		    	            //try to find the panel the cursor is over
		    	            for(i=0; i<brothers.length; i++){
			    	            if((brothers[i].getEl().getLeft(false)<e.xy[0] && brothers[i].getEl().getRight(false)>e.xy[0])){ 
			    	            	 //the cursor is over a brother panel
		    	            		arrowTop.style.left = brothers[i].getEl().getLeft(false)-5;
			    	           		arrowTop.style.top =  brothers[i].getEl().getTop(false)-10;
			    	           		arrowBottom.style.left = brothers[i].getEl().getLeft(false)-5;
			    	           		arrowBottom.style.top =  brothers[i].getEl().getBottom(false);
			    	            	return;
			    	            }
		    	            }  
		    	            //the cursor is out of the list of panels
		    	            if (father.getEl().getRight(false)<e.xy[0] ){
	    	            		arrowTop.style.left = father.getEl().getRight(false)-5;
		    	           		arrowTop.style.top =  father.getEl().getBottom(false)-10;
		    	           		arrowBottom.style.left = father.getEl().getRight(false)-5;
		    	           		arrowBottom.style.top =  father.getEl().getBottom(false)+pel.getHeight();
		    	            }else if (father.getEl().getLeft(false)>e.xy[0]){
	    	            		arrowTop.style.left = father.getEl().getLeft(false)-5;
		    	           		arrowTop.style.top =  father.getEl().getBottom(false)-10;
		    	           		arrowBottom.style.left = father.getEl().getLeft(false)-5;
		    	           		arrowBottom.style.top =  father.getEl().getBottom(false)+pel.getHeight();;
		    	            }else{//the cursor is over the panel we try to dd
		    	            	
		    	            	var visibleBrother = this.getDragData().panel.getPreviousSibling(true);
		    	            	if (visibleBrother.name != this.getDragData().panel.name){
		    	            		arrowTop.style.left = visibleBrother.getEl().getRight(false)-5;
			    	           		arrowTop.style.top =  visibleBrother.getEl().getTop(false)-10;	
			    	           		arrowBottom.style.left = visibleBrother.getEl().getRight(false)-5;
		    	            		arrowBottom.style.top =  visibleBrother.getEl().getBottom(false);	
		    	            	}else{//there are no previous visible pannel
		    	            	
		    	            		if(!father.hidden){
		    	            			//not father is not the root, so we can take the father cordinates
		    	            			arrowTop.style.left = father.getEl().getLeft(false)-5;
		    	            			arrowTop.style.top =  father.getEl().getBottom(false)-10;
		    	            			arrowBottom.style.left = father.getEl().getLeft(false)-5;
		    	            			arrowBottom.style.top =  father.getEl().getBottom(false)+pel.getHeight();
		    	            		}else{
		    	            			//the father is the root, so we have to take the cordinates of the next brother
		    	            			visibleBrother = this.getDragData().panel.getNextSibling(true);
		    	            			if (visibleBrother.name != this.getDragData().panel.name){
				    	            		arrowTop.style.left = visibleBrother.getEl().getLeft(false)-5-pel.getWidth();
					    	           		arrowTop.style.top =  visibleBrother.getEl().getTop(false)-10;	
					    	           		arrowBottom.style.left = visibleBrother.getEl().getLeft(false)-5-pel.getWidth();
				    	            		arrowBottom.style.top =  visibleBrother.getEl().getBottom(false);	
		    	            			}else{//there is only one panel, the one we dd 
		    	            				arrowBottom.style.visibility = "hidden";
				    	    	            arrowTop.style.visibility = "hidden";	
				    	            	}
		    	            		}
		    	            	}
		    	            }
	    	            }else{
	    	            	var arrowTop2 = document.getElementById('ext-arrow-dd-top2');
		    	            arrowTop2.style.visibility = "visible";
		    	            for(i=0; i<brothers.length; i++){
			    	            //the cursor is over another panel
		    	            	if((brothers[i].getEl().getTop(false)<e.xy[1] && brothers[i].getEl().getBottom(false)>e.xy[1])){
		    	            		arrowTop.style.left = brothers[i].getEl().getLeft(false)-5;
			    	           		arrowTop.style.top =  brothers[i].getEl().getTop(false)-10;
			    	           		arrowTop2.style.left = brothers[i].getEl().getRight(false)-5;
			    	           		arrowTop2.style.top =  brothers[i].getEl().getTop(false)-10;
		    	    	            return;
		    	            	}
		    	            }
		    	            //the cursor is out of the list of panels
		    	            if (father.getEl().getBottom(false)<e.xy[1] ){
	    	            		arrowTop.style.left = father.getEl().getRight(false)-5;
		    	           		arrowTop.style.top =  father.getEl().getBottom(false)-10-pel.getHeight();
		    	           		arrowTop2.style.left = father.getEl().getRight(false)-5+pel.getWidth();
		    	           		arrowTop2.style.top =  father.getEl().getBottom(false)-10-pel.getHeight();
		    	            }else if (father.getEl().getTop(false)>e.xy[1]){
	    	            		arrowTop.style.left = father.getEl().getRight(false)-5;
		    	           		arrowTop.style.top =  father.getEl().getTop(false)-10;
		    	           		arrowTop2.style.left = father.getEl().getRight(false)-5+pel.getWidth();
		    	           		arrowTop2.style.top =  father.getEl().getTop(false)-10;
		    	            }else{//the cursor is over the panel we try to dd
		    	            	var visibleBrother = this.getDragData().panel.getPreviousSibling(true);
		    	            	if (visibleBrother.name != this.getDragData().panel.name){
		    	            		arrowTop.style.left = visibleBrother.getEl().getLeft(false)-5;
			    	           		arrowTop.style.top =  visibleBrother.getEl().getTop(false)-10+brothers[i-1].getEl().getHeight();
			    	           		arrowTop2.style.left = visibleBrother.getEl().getRight(false)-5;
			    	           		arrowTop2.style.top =  visibleBrother.getEl().getTop(false)-10+brothers[i-1].getEl().getHeight()
		    	            	}else{//there are no previous visible pannel
		    	            		if(!father.hidden){
		    	            			//not father is not the root, so we can take the father cordinates
		    	            			arrowTop.style.left = father.getEl().getRight(false)-5;
		    	            			arrowTop.style.top =  father.getEl().getTop(false)-10;
		    	            			arrowTop2.style.left = father.getEl().getRight(false)-5+pel.getWidth();
		    	            			arrowTop2.style.top =  father.getEl().getTop(false)-10;
		    	            		}else{
		    	            			//the father is the root, so we have to take the cordinates of the next brother
		    	            			visibleBrother = this.getDragData().panel.getNextSibling(true);
		    	            			if (visibleBrother.name != this.getDragData().panel.name){
				    	            		arrowTop.style.left = visibleBrother.getEl().getLeft(false)-5;
				    	            		arrowTop.style.top =  visibleBrother.getEl().getTop(false)-10-pel.getHeight();	
				    	            		arrowTop2.style.left = visibleBrother.getEl().getRight(false)-5;
				    	            		arrowTop2.style.top =  visibleBrother.getEl().getTop(false)-10-pel.getHeight();	
		    	            			}else{//there is only one panel, the one we dd 
		    	            				arrowBottom.style.visibility = "hidden";
				    	    	            arrowTop.style.visibility = "hidden";	
				    	            	}
		    	            		}
		    	            	}
		    	            }		    	            
	    	            }
			        },	    	       
			        //Upadate the positions of the entries in the line with the element dropped
			        endDrag : function(e){  
			        	var myXY;
			        	var exy;
			        	if(horizontal){
			        		myXY = this.getDragData().panel.getEl().getX();
			        		exy = 0;
			        	}else{
			        		myXY = this.getDragData().panel.getEl().getY();
			        		exy = 1;
			        	}
			        	
			        	var father =this.getDragData().panel.father;
			        	var tempChilds = new Array();
			        	var xy;
			        	
			        	var hiddenPanels = new Array();
			        	var notHiddenPanels = new Array();
			        	for(var j=0; j<father.childs.length; j++){
			        		if(father.childs[j].hidden){
			        			hiddenPanels.push(father.childs[j]);
			        		}else{
			        			notHiddenPanels.push(father.childs[j]);
			        		}
			        	}

			        	father.childs =notHiddenPanels.concat(hiddenPanels);
			        	
			        	if(myXY<e.xy[exy]){
	    	        		var startPosition = father.childs.length-1;
	    	        		var endPosition = father.childs.length-1;
	    	        		
	    	        		for(var j=0; j<father.childs.length; j++){
	    	        			if(horizontal){
	    	        				xy = (father.childs[j]).getEl().getX();
	    	        			}else{
	    	        				xy = (father.childs[j]).getEl().getY();
	    	        			}
	    	        			if(xy>myXY || xy==0){
	    	        				startPosition = j-1;
	    	        				break;
	    	        			}
	    	        		}
	    	        		for(var j=startPosition; j<father.childs.length; j++){
	    	        			if(horizontal){
	    	        				xy = (father.childs[j]).getEl().getX();
	    	        			}else{
	    	        				xy = (father.childs[j]).getEl().getY();
	    	        			}

	    	        			if(xy>e.xy[exy] || xy==0){
	    	        				endPosition = j-1;
	    	        				break;
	    	        			}
	    	        		}

	    	        		var me = father.childs[startPosition];
	    	        		for(var j=startPosition; j<endPosition; j++){
	    	        			father.childs[j]=father.childs[j+1];
	    	        		}
	    	        		father.childs[endPosition] = me;
	    	        	}else{
	    	        		var startPosition = father.childs.length-1;
	    	        		var endPosition = 0;
	    	        		
	    	        		for(var j=0; j<father.childs.length; j++){
	    	        			if(horizontal){
	    	        				xy = (father.childs[j]).getEl().getX();
	    	        			}else{
	    	        				xy = (father.childs[j]).getEl().getY();
	    	        			}
	    	        			
	    	        			if(xy>e.xy[exy]){
	    	        				endPosition = j-1;
	    	        				break;
	    	        			}
	    	        		}
	    	        		if(endPosition <0){
	    	        			endPosition = 0;
	    	        		}
	    	        		
	    	        		for(var j=endPosition; j<father.childs.length; j++){
	    	        			if(horizontal){
	    	        				xy = (father.childs[j]).getEl().getX();
	    	        			}else{
	    	        				xy = (father.childs[j]).getEl().getY();
	    	        			}
	    	        			if(xy>myXY || xy==0){
	    	        				startPosition = j-1;
	    	        				break;
	    	        			}
	    	        		}

	    	        		var me = father.childs[startPosition];
	    	        		for(var j=startPosition; j>endPosition; j--){
	    	        			father.childs[j]=father.childs[j-1];
	    	        		}
	    	        		father.childs[endPosition] = me;
	    	        	}
			        	var arrowBottom = document.getElementById('ext-arrow-dd-bottom');
	    	            var arrowTop = document.getElementById('ext-arrow-dd-top');
	    	            var arrowTop2 = document.getElementById('ext-arrow-dd-top2');
	    	            arrowBottom.style.visibility = "hidden";
	    	            arrowTop.style.visibility = "hidden";
	    	            arrowTop2.style.visibility = "hidden";
	    	            myPanel.reload(headers, horizontal);
	    	        }
	    	    }
	    	}
		}
    }
    
    //Adds the arrows for the drag and drop to the page
    , addDDArrowsToPage : function(){
		var dh = Ext.DomHelper; 
		var bottomArrowDOM = {
			id: 'ext-arrow-dd-bottom',
			tag: 'div',
			cls: 'col-move-bottom',
			html: '&nbsp;',
			style: 'left: -100px; top: -100px; visibility: hidden;'
		};
		
		var topArrowDOM = {
			id: 'ext-arrow-dd-top',
			tag: 'div',
			cls: 'col-move-top',
			html: '&nbsp;',
			style: 'left: -100px; top: -100px; visibility: hidden;'
		};
		
		var topArrowDOM2 = {
			id: 'ext-arrow-dd-top2',
			tag: 'div',
			cls: 'col-move-top',
			html: '&nbsp;',
			style: 'left: -100px; top: -100px; visibility: hidden;'
		};
		
		var bodyElement = document.getElementsByTagName('body');

		dh.append(bodyElement[0].id, bottomArrowDOM);
		dh.append(bodyElement[0].id, topArrowDOM);
		dh.append(bodyElement[0].id, topArrowDOM2);
		
    }

    //reload the panels after a change (for example DD)
    , reload : function(headers, horizontal){
    	
    	var headersFresh=new Array();
    	headersFresh.push(headers[0]);
    	for(var k=1; k<headers.length; k++){
	    	var rowHeaderFresh=new Array();
	    	for(var y=0; y<headersFresh[k-1].length; y++){
	    		for(var i=0; i<headersFresh[k-1][y].childs.length; i++){
	    			rowHeaderFresh.push(headersFresh[k-1][y].childs[i]);
	    		}
	    	}
	    	headersFresh.push(rowHeaderFresh);   
    	}
    	
    	this.upadateAndReloadTable(headersFresh, horizontal);
    }
    
    // Build the columnHeaderPanel or the rowHeaderPanel
	, buildHeaderGroup : function(headers, horizontal) {
	
		var headerGroup = new Array();
		this.headersPanelHidden = new Array();
		var resizeHeandles = 'e';
		if (horizontal){
			resizeHeandles = 's';
		}
		var c;
		if(horizontal){
			c = {
					boxMinHeight: this.rowHeight
				};
		}else{
			c = {
					boxMinWidth: this.columnWidth
				};	
		}
		
		for(var y=1; y<headers.length; y++){
			var layoutConfig;
			if(horizontal){
				layoutConfig= {rows: 1};
			}else{
				layoutConfig= {columns: 1};
			}
			
			var headersPanel = new Array();
			var headersPanelHidden = new Array();
			for(var i=0; i<headers[y].length; i++){
				if(!headers[y][i].hidden){
					headersPanel.push(headers[y][i]);
				}else{
					//we put the hidden panels in the tail of the table.
					//this because a bug of ie
					headersPanelHidden.push(headers[y][i]);
				}
			}
			headersPanel = headersPanel.concat(headersPanelHidden);
			c = Ext.apply(c,{
		    	cellCls: 'crosstab-header-panel-cell',
		    	layout:'table',
		    	border: false,
	            layoutConfig: layoutConfig,
				items:  headersPanel
		    });
		    var p = new Ext.Panel(c);  
		   	    
		    this.createResizable(p,resizeHeandles,headersPanel, horizontal);
		    headerGroup.push(p);

		}
		return headerGroup;
	}
	
	//Upadate the container table 
    , upadateAndReloadTable : function(headerGroup, horizontal){
    	if(horizontal){
    		this.updateTableY(this.getNewPositions(this.columnHeader[this.columnHeader.length-1], headerGroup[this.columnHeader.length-1]));
    		this.columnHeader = headerGroup;
    	}else{
    		this.updateTableX(this.getNewPositions(this.rowHeader[this.rowHeader.length-1], headerGroup[this.rowHeader.length-1]));
    		this.rowHeader = headerGroup;
    	}
    	
    	this.reloadHeadersAndTable(horizontal);
    }

    //Calculate the translation vector after a transformation (for example DD)
    //headerLine2: the old headerLine
    //headerLine1: the new headerLine
    //For example:
    //headerLine2 = x y
    //headerLine1 = y x
    //returns 1 0 
    , getNewPositions : function(headerLine2, headerLine1){
    	var newPositions = new Array();
    	for(var y=0; y<headerLine1.length; y++){
        	for(var i=0; i<headerLine2.length; i++){
        		if(this.isTheSameHeader(headerLine2[i], headerLine1[y])){
        			if(headerLine2[i].hidden){
        				newPositions.push(i);
        			}else{
        				newPositions.push(i);
        			}
        			break;
        		}
        	}
    	}
    	return newPositions;
    }
    
    //check if header1.equals(header2)
    ,isTheSameHeader : function(header1, header2, debug){
    	var loop1 = header1; 
    	var loop2 = header2;
    	do{
    		if(debug){
    			alert(loop1.name+" "+loop2.name );
    		}
    		if(loop1.name!= loop2.name){
    			return false;
    		}
    		loop1 = loop1.father;
    		loop2 = loop2.father;
    	} while(loop1!=null);
    	return loop2==null;
    }
    
    //Update the order of the cells after a change in the column headers (Dd or hide/show)
    , updateTableY : function(newPositions){
    	var newEntries = new Array();
    	var entries = this.entries.getEntries();
    	for(var i=0; i<entries.length; i++){
    		var templine = new Array();
    		for(var y=0; y<entries[i].length; y++){
    		//	if(newPositions[y]!=null){
	        		templine.push(entries[i][newPositions[y]]);
	       // 	}
    		}
    		newEntries.push(templine);
    	}
    	this.entries.setEntries(newEntries);
    }

    //Update the order of the cells after a change in the row headers (Dd or hide/show)
    , updateTableX : function(newPositions){
    	var entries = this.entries.getEntries();
    	var newEntries = new Array();
    	for(var i=0; i<entries.length; i++){
    		//if(newPositions[i]!=null){
    		newEntries.push(entries[newPositions[i]]);
    		//}
    	}
    	this.entries.setEntries(newEntries);
    }
    
    //reload the container table
    , reloadTable : function(horizontal){
    	
    	var d1 = new Date();
    	
    	var tableRows = 2;
    	var tableColumns = 2;
    	var dataPanelStyle = "crosstab-table-data-panel";
    	var classEmptyBottomRight = 'crosstab-table-empty-bottom-right-panel';
    	
    	if(this.table!=null && this.datapanel!=null){
    		this.datapanel.destroy();
    		if(this.withRowsSum && this.datapanelRowSum!=null){
    			this.datapanelRowSum.destroy();
    		}
    		if(this.withColumnsSum  && this.datapanelColumnSum!=null){
    			this.datapanelColumnSum.destroy();
    		}
    		this.remove(this.table, false);
    	}
    	
		if(this.withRowsSum){
			tableColumns = 3;
			dataPanelStyle = dataPanelStyle+ " crosstab-none-right-border-panel";
		}else{
			classEmptyBottomRight = classEmptyBottomRight+' crosstab-none-top-border-panel';
		}
    	if(this.withColumnsSum){
    		tableRows = 3;
    		dataPanelStyle = dataPanelStyle+" crosstab-none-bottom-border-panel";
    	}else{
    		classEmptyBottomRight = classEmptyBottomRight+' crosstab-none-left-border-panel';
    	}
    	   	
    	this.table = new Ext.Panel({    	
            layout:'table',
            border: false,
            border: false,
            layoutConfig: {
                columns: tableColumns,
                rows: tableRows
            }
        });
    	
   	    this.entriesPanel = this.getEntries(true, true);
   		var rowForView = this.getRowsForView();
   		var columnsForView = this.getColumnsForView();
   		
   		if(this.emptypanelTopLeft==null){
	   		this.emptypanelTopLeft = new Ext.Panel({
	   			//height: (this.columnHeader.length-1)*this.rowHeight,
	   	        //width: (this.rowHeader.length-1)*this.columnWidth,
	   	        cellCls: 'crosstab-table-empty-top-left-panel',
	   	        border: false,
	   	        html: ""
	   	    });
   		} 	
   		
   		if(this.withRowsSum && this.emptypanelTopRight==null){
	   		this.emptypanelTopRight = new Ext.Panel({
	   			//height: (this.columnHeader.length-1)*this.rowHeight,
	   	        //width: this.columnWidth,
	   	        cellCls: 'crosstab-table-empty-top-right-panel',
	   	        border: false,
	   	        html: ""
	   	    });
   		} 
   		
   		if(this.withColumnsSum && this.emptypanelBottomLeft==null){
	   		this.emptypanelBottomLeft = new Ext.Panel({
	   			//height: this.rowHeight,
	   	        //width: this.columnWidth,
	   	        cellCls: 'crosstab-table-empty-bottom-left-panel',
	   	        border: false,
	   	        html: ""
	   	    });
   		} 
   		
   		if((this.withColumnsSum || this.withRowsSum) && this.emptypanelBottomRight==null){
	   		this.emptypanelBottomRight = new Ext.Panel({
	   			//height: this.rowHeight,
	   	        //width: this.columnWidth,
	   	        cellCls: classEmptyBottomRight,
	   	        border: false,
	   	        html: ""
	   	    });
   		} 
   		   		
    	var store = new Ext.data.ArrayStore({
    	    autoDestroy: true,
    	    storeId: 'myStore',
    	    fields: [
    	             {name: 'name'},
    	             'divId',
    	             {name: 'datatype'},
    	             {name: 'format'},
    	             {name: 'celltype'}
    	    ]
    	});

    	store.loadData(this.entriesPanel);
    	var columnsForView = this.getColumnsForView();
    	
    	var ieOffset =0;
    	if(Ext.isIE){
    		ieOffset = 2;
    	}
    	
    	var tpl = new Ext.XTemplate(
    	    '<tpl for=".">'
    	    , '<div id="{divId}" class="x-panel crosstab-table-cells crosstab-table-cells-{celltype}" ' // the crosstab-table-cells class is needed as itemSelector
    	    , ' style="height: '+(this.rowHeight-2+ieOffset)+'px; width:'+(this.columnWidth-2)+'px; float:left;" >'
    	    , '  <div class="x-panel-bwrap"> '
    	    , '    <div style="width:'+(this.columnWidth-2)+'px; overflow:hidden; padding-top:'+(this.rowHeight-4-this.fontSize)/2+'">'
    	    , '    {[this.format(values.name, values.datatype, values.format)]}'
    	    , '    </div> '
    	    , '  </div>'
    	    , '</div>'
    	    , '</tpl>'
    	    , {
    	    	format: this.format
    	    }
    	);
    	
    	var tplsum = new Ext.XTemplate(
        	    '<tpl for=".">',
        	    '<div id="{divId}" class="x-panel crosstab-table-cells crosstab-table-cells-totals" style="width:'+(this.columnWidth-2+ieOffset)+'px; height: '+(this.rowHeight-2+ieOffset)+'px; float:left;"> <div class="x-panel-bwrap"> <div padding-top:'+(this.rowHeight-4-this.fontSize)/2+'">',
        	    '{[this.format(values.name, values.datatype, values.format)]}',
        	    '</div> </div> </div>',
        	    '</tpl>',
        	    {
        	    	format: this.format
        	    }
        );
    	
    	var dataView = new Ext.DataView({
	        store: store,
	        tpl: tpl,
	        itemSelector: 'div.crosstab-table-cells',
	        trackOver:true
	    });
    	dataView.on('mouseleave', function(dataView, index, htmlNode, event) {
            var divId = eval(htmlNode.id);
           	var row = divId[0];
           	var column = divId[1];
            this.removeHighlightOnColumn(column);
            this.removeHighlightOnRow(row);
        }, this);
    	dataView.on('mouseenter', function(dataView, index, htmlNode, event) {
    		var divId = eval(htmlNode.id);
           	var row = divId[0];
           	var column = divId[1];
            this.highlightColumn(column);
            this.highlightRow(row);
        }, this);
    	
    	this.datapanel = new Ext.Panel({
            width: (columnsForView)*(this.columnWidth),
            height: (rowForView)*(this.rowHeight)+1,
            cellCls: dataPanelStyle,
            border: false,
    	    layout:'fit',
    	    items: dataView
    	});

   		this.table.add(this.emptypanelTopLeft);
   		this.table.add(this.columnHeaderPanelContainer);
		if(this.withRowsSum){
			this.table.add(this.emptypanelTopRight);
		}
   		
   		this.table.add(this.rowHeaderPanelContainer);
   		this.table.add(this.datapanel);
		if(this.withRowsSum){
			this.datapanelRowSum = this.getRowsSumPanel(tplsum, rowForView, this.withColumnsSum);
			this.table.add(this.datapanelRowSum);
		}
    	if(this.withColumnsSum){
    		this.datapanelColumnSum = this.getColumnsSumPanel(tplsum, columnsForView, this.withRowsSum);
	   		this.table.add(this.emptypanelBottomLeft);
	   		this.table.add(this.datapanelColumnSum);
    		this.table.add(this.emptypanelBottomRight);
    	}

    	
    	
   		this.add(this.table);
   		var d22 = new Date();
   		this.rowHeaderPanelContainer.doLayout();
   		var d3 = new Date();
   		this.columnHeaderPanelContainer.doLayout();
   		var d4 = new Date();
   		this.datapanel.doLayout();
   		var d5 = new Date();
   		this.emptypanelTopLeft.doLayout();
   		var d6 = new Date();

   		this.table.doLayout(false);
   		this.doLayout(false);
   		
   		var d7 = new Date();

//   		alert("B: "+(d3-d22));
//   		alert("C: "+(d4-d3));
//   		alert("D: "+(d5-d4));
//   		alert("E: "+(d6-d5));
//   		alert("F: "+(d7-d6));
   		
   		
    }
    
    
    , format: function(value, type, format) {
		try {
			var valueObj = value;
			if (type == 'int') {
				valueObj = parseInt(value);
			} else if (type == 'float') {
				valueObj = parseFloat(value);
			} else if (type == 'date') {
				valueObj = Date.parseDate(value, format);
			} else if (type == 'timestamp') {
				valueObj = Date.parseDate(value, format);
			}
			var str = Sbi.locale.formatters[type].call(this, valueObj); // formats the value
			return str;
		} catch (err) {
			return value;
		}
	}
    
    
    , reloadHeadersAndTable: function(horizontal){
   // 	if(horizontal || horizontal==null){
        	
        	this.columnHeaderPanel = this.buildHeaderGroup(this.columnHeader, true);
        	if(this.columnHeaderPanelContainer!=null){
        		this.columnHeaderPanelContainer.destroy();
        	}
        	this.columnHeaderPanelContainer = new Ext.Panel({
	   			style: 'margin: 0px; padding: 0px;',
	   			cellCls: 'crosstab-column-header-panel-container',
	   			height: 'auto',
	   	        width: 'auto',
	   	        border: false,
	   	        layout:'table',
	   	     
	   	        layoutConfig: {
	   	            columns: 1
	   	        },
	   	        items: this.columnHeaderPanel,
	   	        colspan: 1
	   	    });
//    	}
//    	
//    	if(!horizontal || horizontal==null){
        	
	    	this.rowHeaderPanel = this.buildHeaderGroup(this.rowHeader, false);
	    	if(this.rowHeaderPanelContainer!=null){
	    		this.rowHeaderPanelContainer.destroy();
	    	}
	   		this.rowHeaderPanelContainer = new Ext.Panel({
	   			style: 'margin: 0px; padding: 0px; ',
	   			cellCls: 'crosstab-row-header-panel-container',
	   			height: 'auto',
	   	        width: 'auto',
	   	        layout:'table',
	   	        border: false,
	   	        layoutConfig: {
	   	    		
	   	            columns: this.rowHeader.length
	   	        },
	   	        items: this.rowHeaderPanel,
	   	        colspan: 1
	   	    });
 //   	}
		this.reloadTable();
    }
    
	, createResizable: function(aPanel, heandles, items, horizontal) {
		
    	var iePinned = false;
    	if(Ext.isIE){
    		iePinned = true;
    	}
		
		aPanel.on('render', function() {
			var resizer = new Ext.Resizable(this.id, {
			    handles: heandles,
			    pinned: iePinned
			});
			resizer.on('resize', function(resizable, width, height, event) {
				
				for(var i=0; i<items.length; i++){
					if(horizontal){
						items[i].updateStaticDimension(height);
					}else{
						items[i].updateStaticDimension(width);
					}
				}
			}, this);
		}, aPanel);
	}
 
    
    //Add a new block in the table (a subtree with the headhers and a set of columns or rows)
    //level: the level in witch put the root of the subtree 
    //node: the root of the subtree of headers to add
    //headers: the headers (columnHeader or rowheader)
    //entries: the rows or columns with the data to add
    //horizontal: true if the entries are columns, false otherwise
    //lazy: true if we only want to insert the row/columns in the data structures, but not in spread the data in the GUI.
    //      it's usefull if we call this method more than one time: we call it with lazy=true far all the iteration and with lazy=false in the last one +
    //       (take a look at the method calculateCF)
    , addNewEntries: function(level,node,headers,entries, horizontal, lazy){
    	    	
    	var father = node.father;
    	var dimensionToAdd= node.thisDimension;
    	//update the father
    	father.childs.push(node);
    	
    	//update the fathers dimension of the subtree of headers where we put the node..
    	while(father!=null){
    		father.thisDimension= father.thisDimension+dimensionToAdd;
    		father=father.father;
    	}
    	
    	var nodeToAddList = new Array();
    	var freshNodeToAddList;
    	var nodePosition;
    	var startPos;
    	var endPos;
    	var nodeS;
    	nodeToAddList.push(node);

    	//Find the index in the headers[level] where put the node
    	var startDimension=0;
		for(var i=0; i<headers[level].length; i++){
			nodeS =  headers[level][i];
			if(nodeS.father == node.father){
				startDimension=startDimension+node.father.thisDimension-node.thisDimension;
				startPos = i+nodeS.father.childs.length-1;
				break;
			}
			startDimension = startDimension+nodeS.thisDimension;
		}
		nodePosition=startDimension;
		
		//when we add a new node, we have to move forward all the
		//pannels that live after the nodePosition, and we have
		//to add the node and all its childs
    	for(var y=level; y<headers.length;y++){

			//move the pannels that live after the position of the pannels
			for(var j=headers[y].length-1; j>=startPos; j--){
				headers[y][j+nodeToAddList.length] = headers[y][j];
			}

			//add the node and all its childs
			for(var j=0; j<nodeToAddList.length; j++){
				headers[y][startPos+j] = nodeToAddList[j];
			}
			
			if(y<headers.length-1){
				//prepares the fresh variable for the next iteration with all the childs 
				freshNodeToAddList = new Array();
				for(var j=0; j<nodeToAddList.length; j++){
					freshNodeToAddList = freshNodeToAddList.concat(nodeToAddList[j].childs);
				}
				nodeToAddList = freshNodeToAddList;
				var freshStartDimension=0;
				
				for(var i=0; i<=headers[y+1].length; i++){
					if(startDimension == freshStartDimension){
						startPos = i;
						break;
					}
					freshStartDimension=freshStartDimension+headers[y+1][i].thisDimension;
				}
			}
    	}
    	
    	//add the columns or the rows
    	if(horizontal){
    		this.entries.addColumns(nodePosition,entries);
    	}else{
    		this.entries.addRows(nodePosition,entries);
    	}
    	
    	if(horizontal){
    		this.columnHeader = headers;
    	}else{
    		this.rowHeader = headers;
    	}
    	
    	if(!lazy){
	    	this.setFathers(headers);
			this.setDragAndDrop(headers, horizontal, this);
		
			

	  
	    	this.reloadTable();
    	}
    }   

    , addCalculatedField: function(level, horizontal, op, CFName){
    	var calculatedField = new Sbi.crosstab.core.CrossTabCalculatedField(CFName, level, horizontal, op); 
    	this.calculatedFields.push(calculatedField);
    }

    , getCalculatedFields: function() {
    	return this.calculatedFields;
    }
    
    , getHeaderBounds: function(header, horizontal, level){
    	var headers;
    	var bounds = new Array();
    	var dimension=0;
    	
    	if(header != null){
    		horizontal = header.horizontal;
    		level = header.level;
    	}
    	if(level == null){
    		level =0;
    	}
    	if(horizontal){
    		headers = this.columnHeader[level];
    	}else{
    		headers = this.rowHeader[level];
    	}
    	if(header == null){
    		header = headers[0];
    	}
    	
    	for(var i=0; i<headers.length; i++){
    		if(headers[i]==header){
    			bounds[0] = dimension;
    			bounds[1] = dimension+headers[i].thisDimension;
    			break; 
    		}else{
    			dimension = dimension+headers[i].thisDimension;
    		}
    	}
    	return bounds;
    }
    
    
    
  //============================
  //Partial Sum
  //============================
    
    //Calculate the partial sum of the rows
    , rowsSum : function(){
    	return this.rowsHeaderSum(0, this.entries.getEntries()[0].length);
    }
    
    //Calculate the partial sum of the columns
    , columnsSum : function(){
    	return this.columnsHeaderSum(0, this.entries.getEntries().length);
    }  

    , addHeaderSum: function(header, type){
    	if(header.type=='data'){
	    	var bounds = this.getHeaderBounds(header);
	    	var sum; 
	    	var headers;
	    	if(header.horizontal){
	    		sum = this.rowsHeaderSum(bounds[0],bounds[1]);
	    		headers = this.columnHeader;
	    	}else{
	    		sum = this.columnsHeaderSum(bounds[0],bounds[1]);
	    		headers = this.rowHeader;
	    	}
	    	var sums = new Array();
	    	
	    	var totalNode = new Sbi.crosstab.core.HeaderEntry('Total', 1, header.horizontal, header.level+1, header.childs[0].width, header.childs[0].height);
	    	totalNode.type=type;
	    	totalNode.father = header;
	    	this.setHeaderListener(totalNode);
	    	
	    	if (header.childs[0].childs.length>0){
	    	
		    	var cousinNode = header.childs[0];
		    	var freshTotalChildNode;
		    	var freshFatherNode = totalNode;
		    	
		    	while(cousinNode.childs.length>0){
		    		freshTotalChildNode = new Sbi.crosstab.core.HeaderEntry('Total', 1, freshFatherNode.horizontal, freshFatherNode.level+1, cousinNode.childs[0].width, cousinNode.childs[0].height);
		    		freshTotalChildNode.type=type;
		    		freshTotalChildNode.father = freshFatherNode;
		    		freshFatherNode.childs.push(freshTotalChildNode);
		    		freshFatherNode = freshTotalChildNode;
		    		cousinNode = cousinNode.childs[0];
		    		this.setHeaderListener(freshTotalChildNode);
		    	}
	    	}
	    	sums.push(sum);
	    	this.addNewEntries(header.level+1,totalNode,headers,sums, header.horizontal, true);
    	}
    }
    
    ,printHeader: function(header){
    	var printed = new Array();
    	var length;
    	if(header.horizontal){
    		length = this.columnHeader.length;
    	}else{
    		length = this.rowHeader.length;
    	}
    	
    	for(var i= header.level; i<length; i++){
    		printed.push(this.findLevelHeaders(header, i));
    	}
    	return printed;
    }
    
    ,findLevelHeaders: function(header, level){
    	var a = new Array();
    	if (header.level == level){
    		a.push(header.name);
    		return a;
    	}else if (header.childs.length==0){
    		return a;
    	}else{
    		for(var i=0; i<header.childs.length; i++){
    			a = a.concat(this.findLevelHeaders(header.childs[i],level));
    		}
    		return a;
    	}
    }
    
    //Calculate the partial sum of the rows
    , rowsHeaderSum : function(start, end){
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
    	for(var i=0; i<entries.length; i++){
    		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
	    		partialSum =0;
	        	for(var j=start; j<entries[0].length && j<end; j++){
	        		if(!this.columnHeader[this.columnHeader.length-1][j].hidden && this.columnHeader[this.columnHeader.length-1][j].type=='data'){
	        			partialSum = partialSum + parseInt(entries[i][j]);
	        		}
	        	}
	        	sum.push(''+partialSum);
    		}
    	}
    	return sum;
    }
    
    //Calculate the partial sum of the columns
    , columnsHeaderSum : function(start, end){
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
       	for(var j=0; j<entries[0].length; j++){
       		if(!this.columnHeader[this.columnHeader.length-1][j].hidden){
	       		partialSum =0;
	        	for(var i=start; i<entries.length && i<end; i++){
	        		if(!this.rowHeader[this.rowHeader.length-1][i].hidden && this.rowHeader[this.rowHeader.length-1][i].type=='data'){
	        			partialSum = partialSum + parseInt(entries[i][j]);
	        		}
	        	}
	        	sum.push(''+partialSum);
       		}
    	}
    	return sum;
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Calculate the partial sum of the rows
    , rowsHeaderListSum : function(lines){
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
    	for(var i=0; i<entries.length; i++){
    		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
	    		partialSum =0;
	        	for(var j=0; j<lines.length; j++){
	        		
	        		if(!this.columnHeader[this.columnHeader.length-1][lines[j]].hidden ){//}&& this.columnHeader[this.columnHeader.length-1][lines[j]].type=='data'){
	        			partialSum = partialSum + parseInt(entries[i][lines[j]]);
	        		}
	        	}
	        	sum.push(''+partialSum);
    		}
    	}
    	return sum;
    }
    
    //Calculate the partial sum of the columns
    , columnsHeaderListSum : function(lines){
    	
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
       	for(var j=0; j<entries[0].length; j++){
       		if(!this.columnHeader[this.columnHeader.length-1][j].hidden){
	       		partialSum =0;
	        	for(var i=0; i<lines.length; i++){
	        		if(!this.rowHeader[this.rowHeader.length-1][lines[i]].hidden ){//&& this.rowHeader[this.rowHeader.length-1][lines[i]].type=='data'){
	        			partialSum = partialSum + parseInt(entries[lines[i]][j]);
	        		}
	        	}
	        	sum.push(''+partialSum);
       		}
    	}
    	return sum;
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    //Build the panel with the partial sum of the columns
    , getColumnsSumPanel : function(tpl, columnsForView, withRowsSum){
    	var storeColumns = new Ext.data.ArrayStore({
    	    autoDestroy: true,
    	    storeId: 'myStore',
    	    fields: [
    	             {name: 'name', type: 'float'},
    	             'divId'
    	    ]
    	});
    	var sumColumnsStore = new Array();
   		var sumColumns = this.columnsSum();
   		for(var j=0; j<sumColumns.length; j++){
   			var a = new Array();
   			a.push(sumColumns[j]);
   			a.push('[partialSumC'+j+']');
   			sumColumnsStore.push(a);
   		}
    	storeColumns.loadData(sumColumnsStore);
    	
		var cellCls = 'crosstab-column-sum-panel-container';
		if(withRowsSum){
			cellCls = cellCls + ' crosstab-none-right-border-panel';
		}
    	var datapanelColumnSum = new Ext.Panel({
    		cellCls: cellCls,
            width: (columnsForView)*(this.columnWidth),
            height: (this.rowHeight),
            border: false,
    	    layout:'fit',
    	    items: new Ext.DataView({
    	        store: storeColumns,
    	        tpl: tpl,
    	        itemSelector: 'div.crosstab-table-cells'
    	    })
    	});
    	return datapanelColumnSum;
    }
    
    //Build the panel with the partial sum of the rows
    , getRowsSumPanel : function(tpl,rowForView, withColumnsSum){
    	var storeRows = new Ext.data.ArrayStore({
    	    autoDestroy: true,
    	    storeId: 'myStore',
    	    fields: [
    	             {name: 'name', type: 'float'},
    	             'divId'
    	    ]
    	});
		var sumRowsStore = new Array();
   		var sumRows = this.rowsSum();
   		for(var j=0; j<sumRows.length; j++){
			var a = new Array();
			a.push(sumRows[j]);
			a.push('[partialSumR'+j+']');
			sumRowsStore.push(a);
   		}
		storeRows.loadData(sumRowsStore);	
		
		var cellCls = 'crosstab-row-sum-panel-container';
		if(withColumnsSum){
			cellCls = cellCls + ' crosstab-none-bottom-border-panel';
		}
		
    	var datapanelRowSum = new Ext.Panel({
    		cellCls: cellCls,
            width: (this.columnWidth),
            height: (rowForView)*(this.rowHeight),
            border: false,
    	    layout:'fit',
    	    items: new Ext.DataView({
    	        store: storeRows,
    	        tpl: tpl,
    	        itemSelector: 'div.crosstab-table-cells'
    	    })
    	});
    	return datapanelRowSum;
    }

    
    , calculatePartialSum: function(horizontal){
    	
	    var headers;
	    	
		if(this.withRowsPartialSum && !this.misuresOnRow){
		    for(var i=0; i<this.rowHeader.length-1; i++){
		        for(var j=0; j<this.rowHeader[i].length; j++){
		        	this.addHeaderSum(this.rowHeader[i][j], 'subtotal');
		        }
		    }
		}
		if(this.withColumnsPartialSum && this.misuresOnRow){
			for(var i=0; i<this.columnHeader.length-1; i++){
		        for(var j=0; j<this.columnHeader[i].length; j++){
		        	this.addHeaderSum(this.columnHeader[i][j], 'subtotal');
		        }
		    }
		}
	    
	    if((this.withRowsPartialSum && this.misuresOnRow) || (this.withColumnsPartialSum && !this.misuresOnRow)){
		
    		if(!this.misuresOnRow){
    			headers = this.columnHeader;
    		}else{
    			headers = this.rowHeader;
    		}
		    var check = false;
		    var operations = new Array();//Lista di operazioni, per non ripetere 2 volte la stessa (I campi calcolati gia da soli ripetono la stessa operazione in ogni sottoalbero)
		    
		    if(headers.length>=3){//if there are more than one level
		    	var i = headers.length-3;
		        for(var j=0; j<headers[i].length; j++){
	
	    	       	if(headers[i][j].type=='data'){
	    	        	var op ='0';
	    	        	for(var y=0; y<headers[i][j].childs.length; y++){
	    	        		if(headers[i][j].childs[y].type=='data'){
	    	        			op = op+' + field['+headers[i][j].childs[y].name+']';
	    	        		}
	    	        	}
	    	        	check = true;
	    	        	for(var y=0; y<operations.length; y++){
	    	        		if(operations[y]==op){
	    	        			check = false;
	    	        			break;
	    	        		}
	    	        	}
	    	        	if(check){
	    	        		operations.push(op);
	    	        		Sbi.crosstab.core.CrossTabCalculatedFields.calculateCF(headers[i][j].level+1, headers[i][j].horizontal, op, 'Total', this, true, 'partialsum');
	    	        	}
	    	       	}
		        }
		        
		        //this.crossTab.reloadHeadersAndTable();
		        
		        var measuresSum = new Array();
		        var lineCount=0;
		        for(var k=i; k>0; k--){
		        	
		        	if(!this.misuresOnRow){
	        			headers = this.columnHeader;
	        		}else{
	        			headers = this.rowHeader;
	        		}
		        	
		        	lineCount=0;
		        	var partialSumNode;
		        	var grandFather = headers[k][0].father;
		        	var j=0;
		        	var headers_k1_length = headers[k+1].length;
		        	while(j<headers[k+1].length){
		        		var measuresPosition = new Array();
	    	        	while( j<headers[k+1].length && grandFather.name == headers[k+1][j].father.father.name){
		        	       	if(headers[k+1][j].type=='partialsum'){
		        	       		measuresPosition.push(lineCount);
		        	       		partialSumNode=headers[k+1][j];
		        	       	}
		        	       	lineCount = lineCount+headers[k+1][j].thisDimension;
		        	       	j++;
	        	        }
	
	    	        	if(partialSumNode!=null){
		        	        //prepare the total node
		        	        var totalNode = new Sbi.crosstab.core.HeaderEntry('Total', partialSumNode.thisDimension, partialSumNode.horizontal, k, headers[k][0].width, headers[k][0].height);
		        	        totalNode.father = partialSumNode.father.father;
		        	        totalNode.type = 'partialsum';
		        	        var totalNodeChild = this.cloneNode(partialSumNode,totalNode);
		        	        totalNode.childs.push(totalNodeChild);
		        	        
		        	        this.setHeaderListener(totalNode);
		        	       
		        	        var totalSum = new Array();
		        	        for(var y=0; y<partialSumNode.thisDimension; y++){//the number of the leafs(measures)
		        	        	var lineToSum = new Array();
		        	        	var sum;
			        	        for(var x=0; x<measuresPosition.length; x++){
			        	        	lineToSum.push(measuresPosition[x]+y);
			        	        }	
			        	        if(!totalNode.horizontal){
			        	        	totalSum.push(this.columnsHeaderListSum(lineToSum));
			        	        }else{
			        	        	totalSum.push(this.rowsHeaderListSum(lineToSum));
			        	        }
		        	        }
	
		        	        this.addNewEntries( k,totalNode,headers,totalSum, totalNode.horizontal, true);
		        	        //prepare for the next step
		        	        if(j+2<headers[k+1].length){//2 perchè bisogna aggiunge i per il subtotale del livello k+1 piu il totale che sto aggiungendo ora
	        	        		j=j+1; //the totalNode entry
	        	        		grandFather = headers[k+1][j].father.father;
	        	        		lineCount = lineCount+partialSumNode.thisDimension;
	        	        		partialSumNode = null;
	        	        	}else{
	        	        		break;
	        	        	}
	    	        	}else{
	    	        		break;
	    	        	}
		        	}
		        }
		    }
	    }
	    
	    if(this.withRowsPartialSum || this.withColumnsPartialSum){
	    	this.reloadHeadersAndTable();
	    }
    }
    
	, cloneNode: function(node,father){
		var clonedNode = new Sbi.crosstab.core.HeaderEntry(node.name, node.thisDimension, node.horizontal, node.level, node.width, node.height);
		clonedNode.type = node.type;
		clonedNode.father = father;
		for(var i=0; i<node.childs.length;i++){
			clonedNode.childs.push(this.cloneNode(node.childs[i],clonedNode));
		}
		this.setHeaderListener(clonedNode);
		return clonedNode;
	}
    
    , showCFWizard: function(node, modality) {
   		this.crossTabCFWizard = new Sbi.crosstab.core.CrossTabCFWizard({
   			'baseNode' : node, 
   			'modality' : modality
   		}); 
   		this.crossTabCFWizard.show(this);  
   		this.crossTabCFWizard.on('applyCalculatedField', function(theNode, level, horizontal, op, CFName){
    		Sbi.crosstab.core.CrossTabCalculatedFields.calculateCF(level, horizontal, op, CFName, this);
    		this.addCalculatedField(level, horizontal, op, CFName);
   		}, this); 
    }
    
});

/*
function cruxBackground(id,rowsNumber,columnsNumber){

   	var row = id[0];
   	var column = id[1];
   	
   	var cel;
   	for(var i=0; i<rowsNumber; i++){
   		cel = document.getElementById("["+i+","+column+"]");
   		cel.className += ' crosstab-table-cells-highlight'; // adding class crosstab-table-cells-highlight
   	}
   	for(var i=0; i<columnsNumber; i++){
   		cel = document.getElementById("["+row+","+i+"]");
   		cel.className += ' crosstab-table-cells-highlight'; // adding class crosstab-table-cells-highlight
   	}
}

function clearBackground(id,rowsNumber,columnsNumber){

   	var row = id[0];
   	var column = id[1];

   	var cel;
   	for(var i=0; i<rowsNumber; i++){
   		cel = document.getElementById("["+i+","+column+"]");
   		cel.className = cel.className.replace(/\bcrosstab-table-cells-highlight\b/,''); // removing class crosstab-table-cells-highlight
   	}
   	for(var i=0; i<columnsNumber; i++){
   		cel = document.getElementById("["+row+","+i+"]");
   		cel.className = cel.className.replace(/\bcrosstab-table-cells-highlight\b/,''); // removing class crosstab-table-cells-highlight
   	}
}
*/