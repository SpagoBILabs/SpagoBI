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
 * updateLayout(layout): update the layout of the active tab
 * 
 * updateActiveSheet(change) : update the sheet after tools value changed 
 * 
 * 
 * Public Events
 * 
 * tabChange(activePanel): the tab is changed
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetsContainerPanel = function(config) { 
	
	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetsContainerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetsContainerPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.index = 0;
	this.addEvents();
	this.addPanel = {
			id: 'addTab',
            title: '<br>',
            iconCls: 'newTabIcon',
        	};
	
	c ={
			tabPosition: 'bottom',        
	        enableTabScroll:true,
	        defaults: {autoScroll:true},
	        items: [this.addPanel]
	};
	
	this.on('render',function(){this.addTab();},this);
	
	this.initPanel();
	Sbi.worksheet.designer.SheetsContainerPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.SheetsContainerPanel, Ext.TabPanel, {
	index: null,
	
	initPanel: function(){
	    this.on('tabchange',function(tabPanel, tab){
	    	if(tab==null || tab.id=='addTab'){
	    		this.addTab();
	    		tabPanel.setActiveTab(tabPanel.items.length-2);
	    	}
	    	this.fireEvent('sheetchange',tab);
	    },this);
	}

	, addTab: function(sheetConf){
		this.suspendEvents();
		this.remove('addTab');
		
		if(sheetConf==null){
			sheetConf={};
		}
		
		//The title property is overridden: see setSheetsState
		var sheet = new Sbi.worksheet.designer.SheetPanel(Ext.apply(sheetConf,{
	        title: 'Sheet ' + (++this.index),
	        closable:true
	    })); 
		
	    var tab = this.add(sheet);
	    this.add(this.addPanel);
	    if(this.getActiveTab()==null){
	    	this.setActiveTab(0);
	    }
	    this.resumeEvents();
	    
	    tab.on('beforeClose',function(panel){
			Ext.MessageBox.confirm(
					LN('sbi.worksheet.designer.msg.deletetab.title'),
					LN('sbi.worksheet.designer.msg.deletetab.msg'),            
		            function(btn, text) {
		                if (btn=='yes') {
		                	this.remove(panel);
		                }
		            },
		            this
				);
			return false;
	    },this);
	}
	
	//Update the layout of the active panel
	, updateLayout: function (layout) {
		var activeTab = this.getActiveTab();
		if(activeTab==null){
			this.setActiveTab(0);
			activeTab = this.getActiveTab();
		}
		activeTab.updateLayout(layout);
	}
	
	//Update the sheet after tools value changed 
	, updateActiveSheet: function(change){
	
		
		if(change.layout!=null && change.layout!=undefined){
			this.updateLayout(change.layout);
		}
	}
	
	, getSheetsState: function(){
		var sheets = [];
		for(var i=0; i<this.items.length; i++){
			sheets.push(this.items[i].getSheetState());
		}
		return sheets;
	}
	
	/**
	 * Set the tabs..  THE PANEL SHOULD BE ALREADY RENDERED
	 * The title is overridden: if the saved panels has title [Sheet 1, Sheet 2, Sheet 7]
	 * the new titles are  [Sheet 1, Sheet 2, Sheet 3]
	 */
	, setSheetsState: function(sheets){
		if(this.items.length>1){
			this.remove(this.items[0]);//remove the first panel
		}
		
		//add the panels
		if(this.rendered){
			for(var i=0; i<sheets.length; i++){
				this.addTab(sheets[i]);
			}
		}else{
			this.on('render',function(){
				for(var i=0; i<sheets.length; i++){
					this.addTab(sheets[i]);
				}
			},this);
		}
	}

	
});