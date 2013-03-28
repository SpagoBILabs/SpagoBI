/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  

Ext.define('app.views.CustomToolbar', {
	extend: 'Ext.Toolbar',
	config: {
		toolbarConfiguration: {},
        padding: 5,
    	defaults : {
    		ui : 'plain',
    		iconMask : true
    	},
    	scroll : 'horizontal',
    	layout : {
    		pack : 'center'
    	},
        height: 30
    },

    constructor: function(config){
    	Ext.apply(this,config||{});
    	this.callParent(arguments);
    },
    
	initialize : function() {
		this.callParent(arguments);
		
		console.log('initialize hidden custom toolbar');
		this.setButtons();	

		this.hideToolbar();

	}
    ,hideToolbar: function(){
    	this.hide();
    }
    ,showToolbar: function(){
    	this.show();
    }
    ,setButtons: function(){
    	var buttons = this.toolbarConfiguration.buttons;
    	this.visibleButtons=new Array();
    	var thisPanel = this;
    	for(var i =0; i< buttons.length; i++){
    		var button = null;
    		var btnKey = buttons[i];
	    	if(btnKey === 'home'){
	    		button = new Ext.Button( {
						title : 'Home',
						iconCls : 'home',
						text : 'Home',
						ui: 'plain',
						iconMask: true,
						handler: function(){
							thisPanel.fireEvent("gohome",thisPanel, this);
						}
						});
	    	}else if(btnKey === 'prec'){
	    		button = new Ext.Button( {
	    			text : 'Previous',
	    			iconCls : 'reply',
	    			ui: 'plain',
					autoEvent: 'previous'
	    		});
	    	}else if(btnKey === 'refresh'){
	    		button = new Ext.Button( {
	    			text : 'Refresh',
	    			iconCls : 'refresh',
	    			ui: 'plain',
	    			handler: function(){
						thisPanel.fireEvent("refreshDoc",thisPanel, this);
					}
		    		});
	    	}else if(btnKey === 'params'){
	    		button = new Ext.Button( {
						title : 'Parameters',
						iconCls : 'compose',
						text : 'Parameters',
						ui: 'plain',
						handler: function(){
							thisPanel.fireEvent("gotoparameters",thisPanel, this);
						}
						});
	    	}else if(btnKey === 'html'){
	    		button = new Ext.Button( {
						title : 'Html',
						ui: 'plain',
						html: Sbi.settings.toolbar.html.code,
						autoEvent: 'html'
						
						});
	    	}else if(btnKey === 'back'){
	    		button = new Ext.Button( {
						title : 'back',
						ui: 'back',
						text: 'Back',
						handler: function(){
							thisPanel.fireEvent("back",thisPanel);
						}
						
						});
	    	}else if(btnKey === 'documentbrowser'){
	    		button = new Ext.Button( {
	    				hidden: true,
						title : 'back',
						ui: 'back',
						text: 'Back',
						handler: function(){
							thisPanel.fireEvent("documentbrowserback",thisPanel, this);
						}
						
						});
	    	}else if(btnKey === 'spacer'){
	    		button = new Ext.Spacer( {
	    			xtype: 'spacer'
	    		});
	    	}else if(btnKey === 'logout'){
	    		button = new Ext.Button( {
	    			iconCls : 'logout',
	    			text : 'Logout',
	    			ui: 'round',
	    			handler: function(){
						thisPanel.fireEvent("logout",thisPanel, this);
					}
	    		});

	    	}
	    	if(button){
	    		button.btnKey = btnKey;
	    		this.add(button);
	    		this.visibleButtons.push(button);
	    	}
    	}
    }
    
    ,setViewModality: function(modality){
    	this.updateToolbar(this.toolbarConfiguration[modality]);
    	this.modality = modality;
    }
        
    /**
     * Show the buttons contained in the visibleButtonsList and hide all the others.
     * Its important that visibleButtonsList is a proper subset of this.visibleButtons
     *  
     */
    ,updateToolbar: function(visibleButtonsList){
    	if(visibleButtonsList && visibleButtonsList.length>0){
    		this.show();
    		var j=0;
    		for(var i=0; i<visibleButtonsList.length; i++){
        		while(j<this.visibleButtons.length && this.visibleButtons[j].btnKey!=visibleButtonsList[i]){
        			this.visibleButtons[j].hide();
        			j++;
        		}
        		if(j<this.visibleButtons.length && this.visibleButtons[j].btnKey==visibleButtonsList[i]){
        			this.visibleButtons[j].show();
        			if(this.visibleButtons[j].btnKey=="documentbrowser"){
        				this.visibleButtons[j].hide();
        			}
        			j++;
        		}
    		}
    		while(j<this.visibleButtons.length){
    			this.visibleButtons[j].hide();
    			j++;
    		}
    	}else{//no button to show, so we hide the toolbar
    		this.hide();
    	}
    }

});