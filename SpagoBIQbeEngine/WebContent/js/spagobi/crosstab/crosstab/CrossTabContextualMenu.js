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
CrossTabContextualMenu = function(node, crossTab) {
	
	this.crossTab = crossTab;
	
	if(node.horizontal){
		this.headers = crossTab.columnHeader;
	}else{
		this.headers = crossTab.rowHeader;
	}

	var c = {
			id:'feeds-ctx',
			items: [
			        '-',
			        {
				       	text: LN('sbi.crosstab.menu.addcalculatedfield'),
				       	iconCls:'add',
				       	handler:function(){
			        		crossTab.crossTabCFWizard = new CrossTabCFWizard(node.level, node.horizontal); 
			        		crossTab.crossTabCFWizard.show(crossTab);  
			        		crossTab.crossTabCFWizard.on('applyCalculatedField', function(level, horizontal, op, CFName){
				       			CrossTabCalculatedFields.calculateCF(level, horizontal, op, CFName, crossTab);
				       		}, crossTab);    	 	
				       	},
				       	scope: this
			        },
			        '-', 
			        {
			        	text: LN('sbi.crosstab.menu.hideheader'),
			        	iconCls:'hide',
			        	handler:function(){
			        		this.showHideNode(node, true) ; 	
			        	},
			        	scope: this
			        },
			        {
			        	text: LN('sbi.crosstab.menu.hideheadertype'),
			        	iconCls:'hide',
			        	handler:function(){
			        		this.showHideAllNodes(node, true);
			        	},
			        	scope: this
			        },
			        {
			        	text: LN('sbi.crosstab.menu.hiddenheader'),
			        	iconCls:'show',
			        	menu:  new Ext.menu.Menu({
			        		items: this.getHiddenCheckboxes(node)
			        	})
			        },
			        {
			        	text: LN('sbi.crosstab.menu.hidemeasure'),
			        	iconCls:'show',
			        	menu:  new Ext.menu.Menu({
			        		items: this.getCheckboxesForMeasures(node.horizontal)
			        	})
			        }
			     ]
        }; 
		CrossTabContextualMenu.superclass.constructor.call(this, c);
};
		
Ext.extend(CrossTabContextualMenu, Ext.menu.Menu, {
	crossTab: null,
	headers: null
    
    //hide a measure
    , showHideMeasure: function(measure, hide, horizontal){
    	for(var i=0; i<this.headers[this.headers.length-1].length; i++){
    		if(this.headers[this.headers.length-1][i].name == measure){
        		if(hide){
        			this.crossTab.hideLine(i, horizontal, true);
        		}else{
        			this.crossTab.showLine(i, horizontal, true);
        		}
    		}
    	}
    	this.crossTab.reloadHeadersAndTable();
    }

    //show/hide a node and all its childs
    //node: the node to hide
    //hide: true for hide, false for show
    //lazy: if true the table is not updated
    , showHideNode: function(node, hide, lazy){
    	var i=0;
    	var startHeight, endHeight;
    	
    	var leafs = node.getLeafs();
    	if(leafs.length==0){//if the node is already a leaf
    		leafs.push(node);
    	}
    	
    	startHeight=this.headers[this.headers.length-1].indexOf(leafs[0]);
    	endHeight=leafs.length+startHeight;

    	for(var y=startHeight; y<endHeight; y++){
    		if(hide){
    			this.crossTab.hideLine(y, node.horizontal, true);
    		}else{
    			this.crossTab.showLine(y, node.horizontal, true);
    		}
    	}

    	if(!lazy){
    		this.crossTab.reloadHeadersAndTable();
    	}
    }

    //show/hide the node and all its brothers with the same name
    //node: the node to hide
    //hide: true for hide, false for show  
    , showHideAllNodes: function(node, hide){
    	var header=this.headers[node.level];
    	for(var y=0; y<header.length; y++){
    		if(node.name == header[y].name){
   				this.showHideNode(header[y], hide, true);
    		}
    	}
    	this.crossTab.reloadHeadersAndTable();
    }
    
    // For every hidden brother of the node, this method creates
    // a checkbox. If the user checks the checkbox the linked
    // header will be shown.
    , getHiddenCheckboxes: function(node){
    	var header=this.headers[node.level];
    	var checkBoxes= new Array();    	
    	for(var i=0; i<header.length; i++){
    		var text= header[i].name;   	
    		var father = header[i].father;
    		while(father.father!=null){//the node is not the root
    			text = father.name+" / "+ text;
    			father = father.father;
    		}

    		if(header[i].hidden){
	    		var freshCheck = new Ext.menu.CheckItem({
					checked: false,
					text: text,
					id : (i+1)//with 0 it doesn't work
				});
	    		freshCheck.on('checkchange', function(checkBox, checked){
	 				this.showHideNode(header[checkBox.id-1], false);
	    		}, this);
	    		checkBoxes.push(freshCheck);
	    		if(i<header.length-1 && (header[i].father.name != header[i+1].father.name)){
	    			checkBoxes.push('-');
	    		}
    		}
    	}
    	return checkBoxes;
    }

    //load the checkboxes for the show/hide menu
    , getCheckboxes : function(horizontal){
   	
    	var checkBoxes = new Array();
    	for(var i=0; i<this.headers[this.headers.length-1].length; i++){
    		var text= this.headers[this.headers.length-1][i].name;
    		var father = this.headers[this.headers.length-1][i].father;
    		while(father.father!=null){//the node is not the root
    			text = father.name+" / "+ text;
    			father = father.father;
    		}
    		
    		var freshCheck = new Ext.menu.CheckItem({
				checked: !(this.headers[this.headers.length-1][i].hidden),
				text: text,
				id : (i+1)//with 0 it doesn't work
			});
    		freshCheck.on('checkchange', function(checkBox, checked){
    									if(checked){
    										this.crossTab.showLine(checkBox.id-1, horizontal);
    									}else{
    										this.crossTab.hideLine(checkBox.id-1, horizontal);
    									}	
    					}, this);
    		checkBoxes.push(freshCheck);
    		if(i<this.headers[this.headers.length-1].length-1 && (this.headers[this.headers.length-1][i].father.name != this.headers[this.headers.length-1][i+1].father.name)){
    			checkBoxes.push('-');
    		}
    	}
    	return checkBoxes;
	}     
    
    
    //load the checkboxes for the measures
    , getCheckboxesForMeasures : function(horizontal){
    	var text;
    	var father;
    	var checkBoxes = new Array();
    	var visibleMesures = new Array();
    	var index, leafsLength=0;
   		this.leafs = new Array();
   		
   		//load the measures
       	for(var i=0; i<this.headers[this.headers.length-1].length; i++){
       		text= this.headers[this.headers.length-1][i].name;
       		index = this.leafs.indexOf(text);
       		if(index<0){
       			this.leafs.push(text);
       			//check if all the lines with this measure are visible
       			visibleMesures[leafsLength] = !(this.headers[this.headers.length-1][i].hidden);
       			leafsLength++;
        	}else{
        		visibleMesures[index] = visibleMesures[index] || !(this.headers[this.headers.length-1][i].hidden);
        	}
    	}
    	
    	for(var i=0; i<this.leafs.length; i++){
    		text= this.leafs[i];

    		var freshCheck = new Ext.menu.CheckItem({
				checked: visibleMesures[i],
				text: text,
				id : text
			});
    		freshCheck.on('checkchange', function(checkBox, checked){
    			this.showHideMeasure(checkBox.id, !checked, horizontal);
    		}, this);
    		checkBoxes.push(freshCheck);
    	}
    	return checkBoxes;
	}  
});
