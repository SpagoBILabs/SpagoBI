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

Sbi.crosstab.core.CrossTabContextualMenu = function(node, crossTab) {
	
	this.crossTab = crossTab;
	
	if(node.horizontal){
		this.headers = this.crossTab.columnHeader;
	}else{
		this.headers = this.crossTab.rowHeader;
	}

	var c = {
			id:'feeds-ctx',
			items: [
			        '-',
			        {
				       	text: LN('sbi.crosstab.menu.addcalculatedfield'),
				       	iconCls:'add',
				       	handler:function(){
				       		this.crossTab.crossTabCFWizard = new Sbi.crosstab.core.CrossTabCFWizard(node.level, node.horizontal); 
				       		this.crossTab.crossTabCFWizard.show(this.crossTab);  
				       		this.crossTab.crossTabCFWizard.on('applyCalculatedField', function(level, horizontal, op, CFName){
				        		Sbi.crosstab.core.CrossTabCalculatedFields.calculateCF(level, horizontal, op, CFName, this.crossTab);
				        		this.crossTab.addCalculatedField(level, horizontal, op, CFName);
				       		}, this); 
				       	},
				       	scope: this
			        },
			        '-', 
			        {
			        	text: LN('sbi.crosstab.menu.hideheader'),
			        	iconCls:'hide',
			        	handler:function(){
			        		Sbi.crosstab.core.CrossTabShowHideUtility.showHideNode(node, true, false, this.crossTab) ; 	
			        	},
			        	scope: this
			        },
			        {
			        	text: LN('sbi.crosstab.menu.hideheadertype'),
			        	iconCls:'hide',
			        	handler:function(){
			        		Sbi.crosstab.core.CrossTabShowHideUtility.showHideAllNodes(node, true, false, this.crossTab);
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
		Sbi.crosstab.core.CrossTabContextualMenu.superclass.constructor.call(this, c);
};
		
Ext.extend(Sbi.crosstab.core.CrossTabContextualMenu, Ext.menu.Menu, {
	crossTab: null,
	headers: null
        
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
	    			Sbi.crosstab.core.CrossTabShowHideUtility.showHideNode(header[checkBox.id-1], false, false, this.crossTab);
	    		}, this);
	    		checkBoxes.push(freshCheck);
	    		if(i<header.length-1 && (header[i].father.name != header[i+1].father.name)){
	    			checkBoxes.push('-');
	    		}
    		}
    	}
    	return checkBoxes;
    }

    //load the checkboxes for the show/hide menu (IT WORKS BUT IS NOT USED)
//    , getCheckboxes : function(horizontal){
//   	
//    	var checkBoxes = new Array();
//    	for(var i=0; i<this.headers[this.headers.length-1].length; i++){
//    		var text= this.headers[this.headers.length-1][i].name;
//    		var father = this.headers[this.headers.length-1][i].father;
//    		while(father.father!=null){//the node is not the root
//    			text = father.name+" / "+ text;
//    			father = father.father;
//    		}
//    		
//    		var freshCheck = new Ext.menu.CheckItem({
//				checked: !(this.headers[this.headers.length-1][i].hidden),
//				text: text,
//				id : (i+1)//with 0 it doesn't work
//			});
//    		freshCheck.on('checkchange', function(checkBox, checked){
//    									if(checked){
//    										Sbi.crosstab.core.CrossTabShowHideUtility.showLine(checkBox.id-1, horizontal, this.crossTab, false);
//    									}else{
//    										Sbi.crosstab.core.CrossTabShowHideUtility.hideLine(checkBox.id-1, horizontal, this.crossTab, false);
//    									}	
//    					}, this);
//    		checkBoxes.push(freshCheck);
//    		if(i<this.headers[this.headers.length-1].length-1 && (this.headers[this.headers.length-1][i].father.name != this.headers[this.headers.length-1][i+1].father.name)){
//    			checkBoxes.push('-');
//    		}
//    	}
//    	return checkBoxes;
//	}     
    
    
    //load the checkboxes for the measures
    , getCheckboxesForMeasures : function(horizontal){
    	
    	if(this.crossTab.misuresOnRow == horizontal){//if the measures live in the other axe
    		return new Array();
    	}
    	
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
    			Sbi.crosstab.core.CrossTabShowHideUtility.showHideMeasure(checkBox.id, !checked, horizontal, this.crossTab);
    		}, this);
    		checkBoxes.push(freshCheck);
    	}
    	return checkBoxes;
	}  
});
