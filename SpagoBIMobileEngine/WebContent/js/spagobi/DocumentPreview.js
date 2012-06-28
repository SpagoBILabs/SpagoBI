/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 app.views.DocumentPreview = Ext.extend(Ext.Panel,

		{
	    dockedItems: [],
	    flex:1.5,
	    cls: 'spagobi-bg',
	    layout: 'vbox',
		initComponent: function ()	{
			this.title = 'Document preview';
			this.items = [];
			console.log('init document preview');
			
			app.views.DocumentPreview.superclass.initComponent.apply(this, arguments);
			
		}
		, showPreview: function (imageClass, rec){
			
			this.removeAll();

			var documentTpl = new Ext.XTemplate(
					'<tpl for=".">',
					'<div class="preview-item">',
					'<div class="'+imageClass+'">' ,		
					'<img src="' + Ext.BLANK_IMAGE_URL + '" ></img>' ,
					'</div>' ,
				    '<div class="item-desc">{name}</div>',
				    '<div class="item-desc"><b>engine: </b>{engine}</div>',
				    '<div class="item-desc"><b>description: </b>{description}</div>',
				    '<div class="item-desc">{creationDate}</div>',
				    '</div>',
				    '</tpl>'
				 );

			
			var html = documentTpl.applyTemplate( rec ) ;
			
		    app.views.previewItem = new Ext.Panel({
		    	html: html,
		    	listeners: { el:{ tap:function(e){ 
		  			  Ext.dispatch({
						  controller: app.controllers.mobileController,
						  action: 'getRoles',
						  id: rec.id,
						  label: rec.label, 
						  engine: rec.engine, 
						  typeCode: rec.typeCode
					  });
		    		} } }
		    });			
		    this.add(app.views.previewItem);
		    
//			DO NOT REMOVE; WE CAN USE THIS SNIPPET IF THE PREVIEW PANEL SHOULD CONTAIN MORE THAN ONE DOCUMENT PREVIW 
//			var data = [rec];
//			//this store can be used several times
//			new Ext.data.Store({
//			    model: 'browserItems',
//			    storeId: 'previewStore',
//			    type: 'memory',
//			    data: data
//			});		
//		    app.views.previewItem = new Ext.DataView({
//			    overItemCls: 'over'
//			    , itemSelector: 'dd'
//			    , groups: null
//			    , lookup: null
//			    , viewState: null
//			    , ready: false				    
//			    , tpl : documentTpl	
//			    , store: 'previewStore'
//			    , listeners: { el:{ tap:function(e){ alert('click'); } } }
//		    });
//		    app.views.previewItem.on('tap', alert('on container tap'));
			
			
/*			var docTpl = new Ext.XTemplate(
					'<div class="preview-item">',
					'<div class="'+imageClass+'">' ,		
					'<img src="' + Ext.BLANK_IMAGE_URL + '" ></img>' ,
					'</div>' ,
				    '<div class="item-desc">{name}</div>',
				    '<div class="item-desc"><b>engine: </b>{engine}</div>',
				    '<div class="item-desc"><b>description: </b>{description}</div>',
				    '<div class="item-desc">{creationDate}</div>',
				    '</div>'
				 );
		    var button = new Ext.Button({
		        tpl: docTpl,
		        data: rec,
		        ui  : 'plain',
                text: 'Drastic',
                flex: 1,
		        listeners:{
		    		'tap': function(btn, e ){
		    			  alert('tap');
		    			  Ext.dispatch({
		    				  controller: app.controllers.mobileController,
		    				  action: 'executeDocument',
		    				  id: id,
		    				  label: label, 
		    				  engine: engine, 
		    				  typeCode: typeCode
		    			  });
		    		}
		    	}
		    });
		    //p.on('tap', alert('on tap 1'));
		    this.add(button);*/
		    //this.update(tpl);
		    this.doLayout();
		    console.log('preview items: '+this.items.length);
		}

	});