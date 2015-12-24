/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.define('app.views.WidgetPanel',{
	extend:'Ext.Panel',
	overlay: null,
	config:{
		executionInstance : null
		,resp: null
		,slider: null
		,dockedItems: []		

	},

	constructor : function(config) {
		Ext.apply(this,config);
		this.callParent(arguments);

		if(this.executionInstance.TYPE_CODE != null && this.executionInstance.TYPE_CODE !== undefined 
				&& (this.executionInstance.TYPE_CODE != Sbi.constants.documenttype.cockpit)){
	
			var innerPanel =  new Ext.Panel({  
			            //the element where the panel is going to be inserted 
						style: 'text-align: center; vertical-align: middle;padding-top: 30px;',
						layout: 'fit',
			            html: 'Loading...' //panel's content  
			            });  
			var executionInstanceVar= this.executionInstance;

			this.overlay = Ext.Viewport.add({
			    xtype: 'panel',
			    scope: this,
			    isOverlay: true,
			    executionInstance: executionInstanceVar,
			    layout:'fit',
			    // We give it a left and top property to make it floating by default
			
			    // Make it modal so you can click the mask to hide the overlay
	            centered : true,
	            modal    : true,
	            hideOnMaskTap : true,
			
			    // Make it hidden by default
			    hidden: true,
			
			    // Set the width and height of the panel
	
	            width    : '100%',
	            height   : '100%',

			
			    // Style the content and make it scrollable
			    styleHtmlContent: true,
			    scrollable: true,
			
			    // Insert a title docked at the top with a title
			    items: [	
			        innerPanel
			    ]
	
			});
	    	this.element.on("doubletap",function(e) {
				var exInst = this.overlay.config.executionInstance;
				var typeCode= exInst.TYPE_CODE;
				if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.cockpit)){
					return;
					
				}
				var h = screen.availHeight;
				var w = screen.availWidth;
				this.overlay.setHeight(h*0.75);
				this.overlay.setWidth(w*0.75);

				this.overlay.show();

		        app.controllers.executionController.executeTemplate({executionInstance: exInst},this.overlay,false);
			},this);
		
	
		}
		}

	,
	plugins: [
	          {
	              xclass: 'Ext.ux.plugin.Pinchemu'
	          }
	]
	                
    ,initialize: function (options) {
    	if(this.resp && this.resp.documentProperties && this.resp.documentProperties.style){
    		this.setStyle(this.resp.documentProperties.style);
    	}else{
    		this.setStyle("");
    	}
		var h = this.buildHeader();
		if(h){
			this.add(h);
		}
		var f = this.buildFooter();
		if(f){
			this.add(f);
		}
		
    	this.callParent( arguments);
    	Ext.apply(this,{listeners:{
			doubletap: { 
				element : 'element',
				fn : function(e) {
					var exInst = this.overlay.config.executionInstance;
					var typeCode= exInst.TYPE_CODE;
					if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.cockpit)){
						return;
						
					}
					var h = this.screen.availHeight;
					var w = this.screen.availWidth;
					this.overlay.setHeight(h*0.75);
					this.overlay.setWidth(w*0.75);

					this.overlay.show();

			        app.controllers.executionController.executeTemplate({executionInstance: exInst},this.overlay,false);
				},
				scope : this
			}
		}});
	}

	,
	setExecutionInstance : function (executionInstance) {
		this.executionInstance = executionInstance;
	}

	,
	getExecutionInstance : function () {
		return this.executionInstance;
	}
	, setTargetDocument: function(resp){
		var drill;
		try{
			drill = resp.config.drill;
		}catch(err){
			//for table cross navigation
			drill = resp.features.drill;
		}
		if(!drill){
			drill = resp.features.drill;
		}
		var targetDoc = drill.document;
		return targetDoc;
	}
	
	, buildHeader: function(){
		if(this.resp && this.resp.documentProperties && this.resp.documentProperties.header){
			var header = new Ext.Panel({
				html: this.updateParameters(this.resp.documentProperties.header),
				docked: "top",
				style: {
					height: "20px",
					position: "relative"
				}
			});
			this.header = header;
			return header;
		}
		this.header = null;
		return null;
	}
	

	, buildFooter: function(){
		if(this.resp && this.resp.documentProperties && this.resp.documentProperties.footer){
			var footer = new Ext.Panel({
				docked: "bottom",
				html: this.updateParameters(this.resp.documentProperties.footer),
				style: {
					height: "20px",
					position: "relative"
				}
			});
			this.footer = footer;
			return footer;
		}
		this.footer =null;
		return null;
	}


	
	, updateTitle: function(){
		var button = null;
		
		if(app.views.customTopToolbar){
			button = app.views.customTopToolbar.getToolbarButtonByType('title');
		}
		if(button==null && app.views.customBottomToolbar){
			button = app.views.customBottomToolbar.getToolbarButtonByType('title');
		}
		
		if(button && this.resp && this.resp.documentProperties && this.resp.documentProperties.title){
			
			button.toHide = false;
			var titleText = this.resp.documentProperties.title.value;
			var titleStyle = this.resp.documentProperties.title.style;
			button.titleText = titleText;
			titleText = this.updateParameters(titleText);
			if(titleText){
				button.setHtml(titleText);				
			}else{
				c.hide();
				button.toHide=true;
			}
			if(titleStyle){
				button.setStyle(titleStyle); 
			}
			this.titleButton = button;
		}else{
			button.hide();
			button.toHide=true;
		}
	}
	
	, updateHeader: function(params){
		if(this.header){
			this.header.setHtml(this.updateParameters(this.resp.documentProperties.header,params));	
		}
	}
	, updateFooter: function(params){
		if(this.footer){
			this.footer.setHtml(this.updateParameters(this.resp.documentProperties.footer, params));	
		}
	}
	
	, updateTitlePanel: function(params){
		if(this.titleButton){
			this.titleButton.setHtml(this.updateParameters(this.titleButton.titleText, params));	
		}
	}
	
	, updateParameters: function(text,parameters){
		try{
			if(!parameters){
				parameters = this.executionInstance.PARAMETERS;			
			}
			for(p in parameters){
				text = text.replace("$P{"+p+"}",parameters[p]);
			}
			return text;
		}catch(e){
			console.error(e);
		}
		return text;
	}
	
	, updatePanel: function(params){
		this.updateHeader(params);
		this.updateFooter(params);
		this.updateTitlePanel(params);
	}
});