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
	    			handler: function(){
						thisPanel.fireEvent("previous",thisPanel, this)
					}
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
						cls: 'toolbar-doc-browser',
						text: 'Back',
						handler: function(){
							thisPanel.fireEvent("documentbrowserback",thisPanel, this);
						}
						
						});
	    	}else if(btnKey === 'title'){
	    		button = new Ext.Panel( {
    				hidden: true,
					title : 'title',
					html: ' '
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

	    	}else if(btnKey === 'navigation'){
	    		button = this.buildNavigationToolbar();
	    	}else if(btnKey.substring(0,4) === 'html'){
	    		var code = btnKey.substring(5); 
	    		button = new Ext.Panel( {
					title : 'Html',
					ui: 'plain',
					cls: 'toolbar-html',
					html: Sbi.settings.toolbar.html[code],
					autoEvent: 'html'
					});
	    	}
	    	if(button){
	    		button.btnKey = btnKey;
	    		this.add(button);
	    		this.visibleButtons.push(button);
	    	}
    	}
    }
    
    ,setViewModality: function(modality, submodality){
    	this.updateToolbar(this.toolbarConfiguration[modality],submodality);
    	this.modality = modality;
    }
        
    /**
     * Show the buttons contained in the visibleButtonsList and hide all the others.
     * Its important that visibleButtonsList is a proper subset of this.visibleButtons
     *  
     */
    ,updateToolbar: function(visibleButtonsList, submodality){
    	if(visibleButtonsList && visibleButtonsList.length>0){
    		this.show();
    		var j=0;
    		for(var i=0; i<visibleButtonsList.length; i++){
        		while(j<this.visibleButtons.length && this.visibleButtons[j].btnKey!=visibleButtonsList[i]){
        			this.visibleButtons[j].hide();
        			j++;
        		}
        		if(j<this.visibleButtons.length && this.visibleButtons[j].btnKey==visibleButtonsList[i]){
        			if(!this.visibleButtons[j].toHide){
        				this.visibleButtons[j].show();	
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
    },
    
    
    /**
     * Show the buttons contained in the visibleButtonsList and hide all the others.
     * Its important that visibleButtonsList is a proper subset of this.visibleButtons
     *  
     */
    hideItem: function(itemKey){
    	if(this.visibleButtons){
        	for(var j=0; j<this.visibleButtons.length; j++){
        		if(this.visibleButtons[j].btnKey==itemKey){
        			this.visibleButtons[j].hide();
        			break;
        		}
        	}
   		}
    },
    
    showItem: function(itemKey){
    	if(this.visibleButtons){
        	for(var j=0; j<this.visibleButtons.length; j++){
        		if(this.visibleButtons[j].btnKey==itemKey){
        			this.visibleButtons[j].show();
        			break;
        		}
        	}
   		}
    },
    
    getToolbarButtonByType: function(type){
    	for(var i=0; i<this.visibleButtons.length; i++){
    		if(this.visibleButtons[i].btnKey == type){
    			return this.visibleButtons[i];
    		}
    	}
    	return null;
    },
    
    buildNavigationToolbar: function(){
    	this.navigationToolbar = Ext.create('Ext.SegmentedButton', {
            allowMultiple: false,
            items: []
    	});
    	//this.navigationToolbar.documentNames = new Array();
    	return this.navigationToolbar;
    },
    
    addDocumentToNavigationToolbar: function(text,itemPos){
    	var thisPanel = this;
    	if(this.navigationToolbar){
    		//this.navigationToolbar.documentNames.push(text);
    		var button = Ext.create('Ext.Button', {
    		    text: text,
    		    height: 20,
    		    cls: 'toolbar-navigation',
				handler: function(){
					thisPanel.fireEvent("navigationbuttonclicked",thisPanel, itemPos);
				}
    		});
    		
    		this.navigationToolbar.add(button);
    		this.navigationToolbar.setPressedButtons([button]);
    	}
    },
    
    /**
     * Clean the toolbar on the right.
     * 
     * @param position
     * @param goForward if true we are navigating forward
     */
    cleanNavigationToolbarFromPosition: function(position, goForward){	
    	if(this.navigationToolbar){
    		var i=-1;
    		for(i=this.navigationToolbar.getItems().items.length-1; i>=position; i-- ){
    			this.navigationToolbar.remove(this.navigationToolbar.getItems().items[i]);
    		}
    		if(!goForward && i>=0){
    			this.navigationToolbar.getItems().items[i].addCls("x-button-pressed");
    		}else{
        		for(i=0; i<position; i++ ){
        			this.navigationToolbar.getItems().items[i].removeCls("x-button-pressed");
        		}
    		}
    	}
    }
});