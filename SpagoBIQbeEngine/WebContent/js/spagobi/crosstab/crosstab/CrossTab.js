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

CrossTab = function(rowHeadersDefinition, columnHeadersDefinition, entries, withRowsSum, withColumnsSum) {
	this.fontSize = 12;
	this.entries = new CrossTabData(entries);
    this.withRowsSum = withRowsSum;
    this.withColumnsSum = withColumnsSum;
    this.rowHeader = new Array();
    this.build(rowHeadersDefinition, 0, this.rowHeader, false);
    this.setFathers(this.rowHeader);
    this.setDragAndDrop(this.rowHeader, false, this);
    this.rowHeader[0][0].hidden=true;//hide the fake root header
    this.rowHeaderPanel = this.buildHeaderGroup(this.rowHeader, false);
    
    this.columnHeader = new Array();
    this.build(columnHeadersDefinition, 0, this.columnHeader, true);
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
    
    CrossTab.superclass.constructor.call(this, c);
};
	
Ext.extend(CrossTab, Ext.Panel, {
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
    					var a = new Array();
    					a.push(entries[i][j]);
    					a.push('['+visiblei+','+visiblej+']');
	    				toReturn.push(a);
	    				visiblej++;
    				}   				
    			}

    			visiblei++;
        	}	
    	}
    	return toReturn;
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
    
    //color the background of a row of the tabel
    //i: the number of the row (visible)
    //columnForView: number of visible columns
    //color: the background color
    ,colorRowBackground: function(i, columnForView, color){
		for(var y = 0; y<columnForView; y++){
			Ext.get('['+i+','+y+']').setStyle('background-color', color);
		}
     }
    
    //color the background of a column of the tabel
    //j: the number of the column (visible)
    //rowForView: number of visible rows
    //columnForView: number of visible columns
    //color: the background color
     ,colorColumnBackground: function(j, rowForView, color){
		for(var y = 0; y<rowForView; y++){
			if(Ext.get('['+y+','+j+']')!=null){
				Ext.get('['+y+','+j+']').setStyle('background-color', color);
			}
		}
     }
     
     //set transparent the backgound of all the cell of the tabel
     ,clearTableBackground: function(){
    	var entries = this.entries.getEntries();
		for(var i = 0; i<entries.length; i++){
			for(var y = 0; y<entries[0].length; y++){
				var el = Ext.get('['+i+','+y+']');
				if(el == null){
					break;
				}
				el.setStyle('background-color', 'transparent');
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
    		 rowsum = this.rowSum();
    	 }
    	 var serializedCrossTab = '{data:' + this.entries.serializeEntries(rowsum, columnsum);
    	 serializedCrossTab = serializedCrossTab + ', \n columns:' +  this.serializeHeader(this.columnHeader[0][0]);
    	 serializedCrossTab = serializedCrossTab + ', \n rows:' +  this.serializeHeader(this.rowHeader[0][0])+'}';
    	 return serializedCrossTab;
     }
     
     //serialize a header and all his the subtree
 	 ,serializeHeader: function(header){
		if(header.childs.length==0){
			return '{node_key: \"'+header.name+'\"}';
		}else{
			var node = '{node_key: \"'+header.name+'\", node_childs:[';
			for(var i=0; i<header.childs.length; i++){
				node = node+this.serializeHeader(header.childs[i])+', ';
			}
			node = node.substr(0,node.length-2)+']}';
			return node;
		}
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
    	p = new HeaderEntry(name, thisDimension, horizontal, level);
    	this.setHeaderListener(p,horizontal);

    	if(headers[level]==null){
    		headers[level]= new Array();
    	}
    	headers[level].push(p);
    	return thisDimension;
    }

     //Adds the listeners to the header
    , setHeaderListener: function(header, horizontal){
     
    	header.addListener({render: function(c) {
			// open the show/hide dialog
			c.el.on('click', function(event,element,object) {
							if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible()){
								this.crossTabCFWizard.addField("field["+c.name+"]", c.level, c.horizontal);
							}else{
								if(this.clickMenu!=null){
									this.clickMenu.destroy();
								}
								this.clickMenu = new CrossTabContextualMenu(c, this);
								this.clickMenu.showAt([event.getPageX(), event.getPageY()]);
							}
						}
					,this);
			//color the rows/columns when the mouse enter in the header
			c.el.on('mouseenter', function() {
							if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible() && this.crossTabCFWizard.isActiveLevel(c.level, c.horizontal)){
								c.setWidth(c.getWidth()-2);
								c.setHeight(c.getHeight()-2); 
								c.addClass("crosstab-borderd");
							}

							var rowForView = this.getRowsForView();
					 		var columnForView = this.getColumnsForView();
							var start=0;
							var i=0;
							var headers;
							
							if(horizontal){
								headers = this.columnHeader;
							}else{
								headers = this.rowHeader
							}
							
							while(!this.isTheSameHeader(headers[c.level][i],c) && i<headers[c.level].length){
								if(!headers[c.level][i].hidden){
									start = start + headers[c.level][i].thisDimension;
								}
								i++;
							}
							if( i<headers[c.level].length){
								var end = start+headers[c.level][i].thisDimension-1;
	
								if(horizontal){
									for(i=start; i<=end; i++){
										this.colorColumnBackground(i, rowForView, '#EFEFEF');
									}
								}else{
									for(i=start; i<=end; i++){
										this.colorRowBackground(i, columnForView, '#EFEFEF');
									}
								}
							}
							}
						,this);
			c.el.on('mouseleave', function() {
						
						if(this.crossTabCFWizard!=null && this.crossTabCFWizard.isVisible() && this.crossTabCFWizard.isActiveLevel(c.level, c.horizontal)){
							c.removeClass("crosstab-borderd");
							c.setWidth(c.getWidth()+2);
							c.setHeight(c.getHeight()+2); 
						}
							this.clearTableBackground();
							
						}
							
						,this);
			}, scope: this
	  	});
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
    	
    	//var d1 = new Date();
    	
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
    	             {name: 'name', type: 'float'},
    	             'divId'
    	    ]
    	});

    	store.loadData(this.entriesPanel);
    	var columnsForView = this.getColumnsForView();
    	
    	var ieOffset =0;
    	if(Ext.isIE){
    		ieOffset = 2;
    	}
    	
    	var tpl = new Ext.XTemplate(
    	    '<tpl for=".">',
    	    '<div id="{divId}" class="x-panel crosstab-table-cells" style="height: '+(this.rowHeight-2+ieOffset)+'px; width:'+(this.columnWidth-2)+'px; float:left;" onMouseOver="cruxBackground({divId},'+rowForView+','+columnsForView+')"  onMouseOut="clearBackground({divId},'+rowForView+','+columnsForView+')"> <div class="x-panel-bwrap"> <div class=x-panel-body-crosstab-table-cells-x-panel-body-noheader" style="width:'+(this.columnWidth-2)+'px;  padding-top:'+(this.rowHeight-4-this.fontSize)/2+'">',
    	    '{name}',
    	    '</div> </div> </div>',
    	    '</tpl>'
    	);
    	
    	var tplsum = new Ext.XTemplate(
        	    '<tpl for=".">',
        	    '<div id="{divId}" class="x-panel crosstab-table-cells" style="width:'+(this.columnWidth-2+ieOffset)+'px; height: '+(this.rowHeight-2+ieOffset)+'px; float:left;"> <div class="x-panel-bwrap"> <div class=x-panel-body-crosstab-table-cells-x-panel-body-noheader"  padding-top:'+(this.rowHeight-4-this.fontSize)/2+'">',
        	    '{name}',
        	    '</div> </div> </div>',
        	    '</tpl>'
        	);
    	
    	this.datapanel = new Ext.Panel({
            width: (columnsForView)*(this.columnWidth),
            height: (rowForView)*(this.rowHeight)+1,
            cellCls: dataPanelStyle,
            border: false,
    	    layout:'fit',
    	    items: new Ext.DataView({
    	        store: store,
    	        tpl: tpl,
    	        itemSelector: 'div.crosstab-table-cells'
    	    })
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

//    	var d2 = new Date();
    	
   		this.add(this.table);
   		this.doLayout();

//   		var d3 = new Date();
   		
//   		alert("A: "+(d2-d1));
//   		alert("B: "+(d3-d2));
   		
   		if(Ext.get('loading')!=null){
	   		setTimeout(function(){
	   			Ext.get('loading').remove();
	   			Ext.get('loading-mask').fadeOut({remove:true});
	   			}, 250);
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

    	if(!lazy){
	    	this.setFathers(headers);
			this.setDragAndDrop(headers, horizontal, this);
		
			
	    	if(horizontal){
	    		this.columnHeader = headers;
	    	}else{
	    		this.rowHeader = headers;
	    	}
	  
//	    	this.reloadTable();
    	}
    }  

    //Hide a line
    , hideLine : function(lineNumber, horizontal, lazy){
    	var headerEntry;
    	if(horizontal){
    		headerEntry = this.columnHeader[this.columnHeader.length-1][lineNumber];
    	}else{
    		headerEntry = this.rowHeader[this.rowHeader.length-1][lineNumber];
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
    		this.reloadHeadersAndTable();
    	}
    }
    
    //Show a line
    , showLine : function(lineNumber, horizontal, lazy){
    	var headerEntry;
    	if(horizontal){
    		headerEntry = this.columnHeader[this.columnHeader.length-1][lineNumber];
    	}else{
    		headerEntry = this.rowHeader[this.rowHeader.length-1][lineNumber];
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
    		this.reloadHeadersAndTable();
    	}
    }
    
    
  //============================
  //Partial Sum
  //============================
    
    //Calculate the partial sum of the rows
    , rowsSum : function(){
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
    	for(var i=0; i<entries.length; i++){
    		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
	    		partialSum =0;
	        	for(var j=0; j<entries[0].length; j++){
	        		if(!this.columnHeader[this.columnHeader.length-1][j].hidden){
	        			partialSum = partialSum + parseInt(entries[i][j]);
	        		}
	        	}
	        	sum.push(partialSum);
    		}
    	}
    	return sum;
    }
    
    //Calculate the partial sum of the columns
    , columnsSum : function(){
    	var entries = this.entries.getEntries();
    	var sum = new Array();
    	var partialSum;
       	for(var j=0; j<entries[0].length; j++){
       		if(!this.columnHeader[this.columnHeader.length-1][j].hidden){
	       		partialSum =0;
	        	for(var i=0; i<entries.length; i++){
	        		if(!this.rowHeader[this.rowHeader.length-1][i].hidden){
	        			partialSum = partialSum + parseInt(entries[i][j]);
	        		}
	        	}
	        	sum.push(partialSum);
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
    
});

function cruxBackground(id,rowsNumber,columnsNumber){

   	var row = id[0];
   	var column = id[1];
   	
   	var cel;
   	for(var i=0; i<rowsNumber; i++){
   		cel = document.getElementById("["+i+","+column+"]");
   		cel.style.backgroundColor = '#EFEFEF';
   	}
   	for(var i=0; i<columnsNumber; i++){
   		cel = document.getElementById("["+row+","+i+"]");
   		cel.style.backgroundColor = '#EFEFEF';
   	}
}

function clearBackground(id,rowsNumber,columnsNumber){

   	var row = id[0];
   	var column = id[1];

   	var cel;
   	for(var i=0; i<rowsNumber; i++){
   		cel = document.getElementById("["+i+","+column+"]");
   		cel.style.backgroundColor = '#FFFFFF';
   	}
   	for(var i=0; i<columnsNumber; i++){
   		cel = document.getElementById("["+row+","+i+"]");
   		cel.style.backgroundColor = '#FFFFFF';
   	}
}