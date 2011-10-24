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
 * 
 * Public Events
 *
 * 
 * Authors - Davide Zerbetto
 */
Sbi.worksheet.designer.SheetTabMenu = Ext.extend(Object, {

    constructor : function(config){
        Ext.apply(this, config || {});
    },

    //public
    init : function(tabs){
        this.tabs = tabs;
        tabs.on({
            scope: this,
            contextmenu: this.onContextMenu,
            destroy: this.destroy
        });
    }
    
    ,
    destroy : function(){
        Ext.destroy(this.menu);
        delete this.menu;
        delete this.tabs;
        delete this.active;    
    }

    // private
    ,
    onContextMenu : function(tabs, item, e){
        this.active = item;
        if (this.active.id != 'addTab') {
	        var menu = this.createMenu();
	        e.stopEvent();
	        menu.showAt(e.getPoint());
    	}
    }
    
    ,
    createMenu : function(){
        if ( !this.menu ) {
            var items = [{
                itemId: 'close',
                text: LN('sbi.worksheet.designer.sheettabmenu.menu.close'),
                scope: this,
                handler: this.onClose
            }, {
                itemId: 'rename',
                text: LN('sbi.worksheet.designer.sheettabmenu.menu.rename'),
                scope: this,
                handler: this.onRename
            }];
            this.menu = new Ext.menu.Menu({
                items: items
            });
        }
        return this.menu;
    }    
    
    ,
    onClose : function(){
        this.tabs.remove(this.active);
    }
    
    ,
    onRename : function() {
    	this.sheetNameInput = new Ext.form.TextField({
			allowBlank: false
			, fieldLabel: LN('sbi.worksheet.designer.sheettabmenu.rename.sheetname')
    	});
		var form = new Ext.form.FormPanel({
			frame: true
			, items: [this.sheetNameInput]
			, buttons: [{
    			text: LN('sbi.worksheet.designer.sheettabmenu.buttons.apply')
    		    , handler: function() {
    		    	var sheetName = this.sheetNameInput.getValue();
    		    	var valid = this.validate(sheetName);
    		    	if (valid) {
	    		    	this.active.setTitle( sheetName );
	                	this.renameWindow.close();
    		    	}
            	}
            	, scope: this
    	    },{
    		    text: LN('sbi.worksheet.designer.sheettabmenu.buttons.cancel')
    		    , handler: function(){ 
    		    	this.renameWindow.close(); 
    		    }
            	, scope: this
    		}]
		});
    	this.renameWindow = new Ext.Window({
    		closeAction : 'close'
    		, items : [form]
    	});
        this.sheetNameInput.setValue( this.active.title );
        this.renameWindow.show();
        
    }
    
    ,
    validate : function (sheetName) {
    	var success = true;
    	if (sheetName == undefined || sheetName == null || sheetName.trim() == '') {
        	Ext.MessageBox.show({
           		title: LN('sbi.worksheet.designer.sheettabmenu.error')
           		, msg: LN('sbi.worksheet.designer.sheettabmenu.error.nosheetname')
           		, buttons: Ext.MessageBox.OK     
           		, icon: Ext.MessageBox.ERROR
           		, modal: true
       		});
        	return false;
    	}
    	for (var i = 0 ; i < this.tabs.items.items.length; i++) {
    		var aTab = this.tabs.items.items[i];
    		if ( aTab != this.active && aTab.title == sheetName ) {
    			success = false;
	        	Ext.MessageBox.show({
	           		title: LN('sbi.worksheet.designer.sheettabmenu.error')
	           		, msg: LN('sbi.worksheet.designer.sheettabmenu.error.sheetnamealreadyinuse')
	           		, buttons: Ext.MessageBox.OK     
	           		, icon: Ext.MessageBox.ERROR
	           		, modal: true
	       		});
    			break;
    		}
    	}
    	return success;
    }
    
});