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
 * isValid(): return true if the panel is valid
 * setSheetsState(state): set the state of the panels
 * getSheetsState(): get the state of the panel
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
            iconCls: 'newTabIcon'
        	};
	
	c ={
			tabPosition: 'bottom',        
	        enableTabScroll:true,
	        defaults: {autoScroll:true},
	        items: [this.addPanel],
	        frame: true,
	        tools:[{
	        	id:'save',
	        	qtip: LN('sbi.qbe.queryeditor.centerregion.tools.save'),
	        	handler:this.saveWorkSheet,
	        	scope: this
	        }]
	};
	
	this.on('render',function(){this.addTab();},this);
	
	this.initPanel();
	Sbi.worksheet.designer.SheetsContainerPanel.superclass.constructor.call(this, c);	 	
	this.addEvents('saveworkheet');

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
		sheet.contentPanel.on('addDesigner', this.addDesignerHandler, this);
		
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
	    
	    return sheet;
	}
	
	, addDesignerHandler: function (sheet, state) {
		var newSheet = this.addTab({});
		newSheet.contentPanel.addDesigner(state);
		this.activate(newSheet);
		this.notifySheetAdded.defer(500, this, [LN('sbi.worksheet.designer.msg.newsheet.title'), LN('sbi.worksheet.designer.msg.newsheet.msg')]);
	}
	
    , createBox: function (t, s) {
        return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
    }
    
    , notifySheetAdded : function(title, format) {
        if(!this.msgCt){
        	this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        }
        var s = String.format.apply(String, Array.prototype.slice.call(arguments, 1));
        var m = Ext.DomHelper.append(this.msgCt, this.createBox(title, s), true);
        m.hide();
        m.slideIn('b', { duration: 1 }).pause( 1 ).ghost("b", { duration: 2, remove: true});
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
		if(change.sheetLayout!=null && change.sheetLayout!=undefined){
			this.updateLayout(change.sheetLayout);
		}
	}
	
	, getSheetsState: function(){
		var sheets = [];
		if(this.items.items.length>1){
			var i=0;
			for(; i<this.items.items.length-1; i++){//-1 because of the add panel teb
				sheets.push(this.items.items[i].getSheetState());
			}
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
			var i=0;
			for(; i<sheets.length; i++){
				this.addTab(sheets[i]);
			}
		}else{
			this.on('render',function(){
				var i=0;
				for(; i<sheets.length; i++){
					this.addTab(sheets[i]);
				}
			},this);
		}
	}
	
	, isValid: function(){
		var valid = true;
		if(this.items.items.length>1){
			var i=0;
			for(; i<this.items.items.length-1; i++){//-1 because of the add panel teb
				valid = valid && this.items.items[i].isValid();
				if(!valid){
					break;
				}
			}
		}
		return valid;
	}

	, showState: function(event, toolEl, panel) {
		Ext.MessageBox.confirm(
				LN('sbi.worksheet.designer.msg.deletetab.title'),
				Ext.encode(this.getSheetsState()),            
	            function(btn, text) {
	                if (btn=='yes') {
	                	this.remove(panel);
	                }
	            },
	            this
			);
  	}
	
	, saveWorkSheet: function(){
		var	wk_definition = this.getSheetsState();
		this.fireEvent('saveworkheet', this, {'OBJECT_TYPE': 'WORKSHEET','OBJECT_WK_DEFINITION': wk_definition });	
	}

	
});