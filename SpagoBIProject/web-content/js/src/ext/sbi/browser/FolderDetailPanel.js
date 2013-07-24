/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */

function myAdd(){
	alert("myAdd");
	var io = Ext.get("ciccio");
//	Ext.get("doc-details").addNewDocument();
	io.addNewDocument;
}

Ext.ns("Sbi.browser");

Sbi.browser.FolderDetailPanel = function(config) {    
    
	// always declare exploited services first!
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	this.services = new Array();
	this.services['loadFolderContentService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_FOLDER_CONTENT_ACTION'
		, baseParams: params
	});
	this.services['searchContentService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SEARCH_CONTENT_ACTION'
		, baseParams: params
	});
	this.services['loadFolderPathService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_FOLDER_PATH_ACTION'
		, baseParams: params
	});
	this.services['deleteDocument'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'DELETE_OBJECT_ACTION'
		, baseParams: params
	});
	this.services['detailDocument'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceType: 'PAGE'
		, serviceName: 'DetailBIObjectPage'
		, baseParams: {LIGHT_NAVIGATOR_DISABLED: 'FALSE', MESSAGEDET: 'DETAIL_SELECT'}
	});
  
	this.services['newDocument'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceType: 'PAGE'
		, serviceName: 'DetailBIObjectPage'
		, baseParams: {LIGHT_NAVIGATOR_DISABLED: 'FALSE', MESSAGEDET: 'DETAIL_NEW'}
	});
	
	// -- store -------------------------------------------------------
	this.store = new Ext.data.JsonStore({
		 url: this.services['loadFolderContentService']
		 , browseUrl: this.services['loadFolderContentService']
		 , searchUrl: this.services['searchContentService']
		 , root: 'folderContent'
		 , fields: ['title', 'icon', 'samples']
	});	

	this.store.on('loadexception', Sbi.exception.ExceptionHandler.handleFailure);
	this.store.on('beforeload', 
			function(){
				if(this.loadingMask) {
					this.loadingMask.show();
				}
			}, this);
	this.store.on('load', function(){if(this.loadingMask) {this.loadingMask.hide();}}, this);
	
	// -- folderView ----------------------------------------------------
    this.folderView = new Sbi.browser.FolderView({
    	store: this.store
        , listeners: {
        	'click': {
    			fn: this.onClick
    			, scope: this
    		}
    	}
    	, emptyText: LN('sbi.browser.folderdetailpanel.emptytext')
    	, metaFolder: config.metaFolder
        , metaDocument: config.metaDocument	
    });
    
    // -- toolbar -----------------------------------------------------------
    var toolbarItems = [];
    
    var ttbarTextItem = null;
    if (Sbi.settings.browser.showBreadCrumbs !== undefined && Sbi.settings.browser.showBreadCrumbs){
    	ttbarTextItem = new Ext.Toolbar.TextItem('> ?');  
	    ttbarTextItem.isBreadcrumb = true;	   
	    toolbarItems.push(ttbarTextItem);
    }
    
    var ttbarToggleViewButton = new Ext.Toolbar.Button({
    	tooltip: LN('sbi.browser.folderdetailpanel.listviewTT'),
		iconCls:'icon-list-view',
		listeners: {
			'click': {
          		fn: this.toggleDisplayModality,
          		scope: this
        	} 
		}
    });
    var newDocumentButton = new Ext.Toolbar.Button({
    	tooltip: LN('sbi.generic.add'),
		iconCls:'icon-add',
		listeners: {
			'click': {
          		fn: this.addNewDocument,
          		scope: this
        	} 
		}
    });
    
    var bannerHTML = ''+
 		'<div class="aux"> '+
		'    <div class="list-actions-container"> '+
		'		<ul class="list-tab"> '+
		'	    	<li class="active first"><a href="#">Tutte</a></li> '+
		'	        <li><a href="#">ASTAT</a></li> '+
		'	        <li class="favourite last"><a href="#">Favoriti</a></li> '+
		'		</ul> '+
		'	    <div class="list-actions"> '+
//		'	        <a href="#" class="btn-add" onClick="addNewDocument()"><span class="highlighted">Carica</span> mappa<span class="plus">+</span></a> '+
		'	        <a id="newDocument" href="#" onclick="myAdd()" class="btn-add"><span class="highlighted">Carica</span> mappa<span class="plus">+</span></a> '+
//		'	        <a id="newDocument" href="#"  class="btn-add"><span class="highlighted">Carica</span> mappa<span class="plus">+</span></a> '+
		'	        <form action="#" method="get" class="search-form"> '+
		'	            <fieldset> '+
		'	                <div class="field"> '+
		'	                    <label for="search">Cerca fra i dataset</label> '+
		'	                    <input type="text" name="search" id="search" value="Cerca per parola chiave..." /> '+
		'	                </div> '+
		'	                <div class="submit"> '+
		'	                    <input type="submit" value="Cerca" /> '+
		'	                </div> '+
		'	            </fieldset> '+
		'	        </form> '+
		'	        <ul class="order"> '+
		'	            <li class="active"><a href="#">Recenti<span class="arrow"></span></a></li> '+
		'	            <li><a href="#">Valore 2</a></li> '+
		'	            <li><a href="#">Altro valore</a></li> '+
		'	        </ul> '+
		'	    </div> '+
		'</div>' ;
    
       
	
	var bannerPanel = new Ext.Panel({
		id:'banner-doc',
	   	autoScroll: false,
	   	layout:'fit',
//		   	margin:'30',
//		   	height: 100,
	   	html: bannerHTML   
	});
//	bannerPanel.on('onclick',this.addNewDocument,this);
	
	
	
   
    toolbarItems.push('->');	 
    if (this.isAbleToCreateDocument()){
    	toolbarItems.push(newDocumentButton);
    }
    toolbarItems.push(ttbarToggleViewButton);

    this.toolbar = new Ext.Toolbar({     
      items: toolbarItems
    });


    
    if (Sbi.settings.browser.showBreadCrumbs !== undefined && Sbi.settings.browser.showBreadCrumbs){
    	this.toolbar.breadcrumbs = new Array();
	    this.toolbar.breadcrumbs.push(ttbarTextItem);
    }
    //this.toolbar.text = ttbarTextItem;
    this.toolbar.buttonsL = new Array();
    this.toolbar.buttonsL['toggleView'] = ttbarToggleViewButton;
    if (this.isAbleToCreateDocument()){
    	this.toolbar.buttonsL['newDocument'] = newDocumentButton;
    }
    
    
    if(config.modality && (
      config.modality === 'list-view' || 'group-view'
    )) {
      this.modality = config.modality;
    } else {
      this.modality = 'group-view';
    }
    
    // -- mainPanel -----------------------------------------------------------
    this.mainPanel = new Ext.Panel({
//    	layout:'border',
         id:'doc-details'
        , cls: this.modality
//        , autoHeight: true
        , collapsible: true
        , frame: true
        , autoHeight: false
        , autoScroll:true
               
        , items:[bannerPanel, this.folderView]        
        , tbar: this.toolbar 
        
        , listeners: {
		    'render': {
            	fn: function() {
            		this.loadingMask = new Sbi.decorator.LoadMask(this.mainPanel.body, {msg:LN('sbi.browser.folderdetailpanel.waitmsg')}); 
            	},
            	scope: this
          	}
        }        
    });
    
    var c = Ext.apply({}, config, {
      items: [this.mainPanel]
    });   
    
    Sbi.browser.FolderDetailPanel.superclass.constructor.call(this, c);   
    
    this.addEvents("onfolderload", "ondocumentclick", "beforeperformactionondocument", "onfolderclick", "beforeperformactiononfolder");
    if (Sbi.settings.browser.showBreadCrumbs !== undefined && Sbi.settings.browser.showBreadCrumbs){
    	 this.addEvents("onbreadcrumbclick");
    }
    //this.store.load();  
    
	//for "custom" Document Browser we have a defaultFolder id
    if (config.defaultFolderId != null){
        this.loadFolder(config.defaultFolderId, null);

    } else {
        this.loadFolder(config.folderId, config.folderId);
    }
    
    var newDoc =  Ext.select("#newDocument");
//    var newDoc =  Ext.get("newDocument");
	newDoc.on('onclick', this.addNewDocument, this);
};




Ext.extend(Sbi.browser.FolderDetailPanel, Ext.Panel, {
	//constants
      DETAIL_DOCUMENT: 'DocumentDetailManagement'
    //variables
	, services: null
    , modality: null // list-view || group-view
    , store: null
    , toolbar: null
    , folderView: null
    , loadingMask: null
    , folderId: null
    
    , id:'ciccio'

    
    , toggleDisplayModality: function() {
      this.loadingMask.show();
      var b = this.toolbar.buttonsL['toggleView'];
      if(this.modality === 'list-view') {
        this.mainPanel.getEl().removeClass('list-view');
        this.mainPanel.addClass('group-view');
        this.modality = 'group-view';
        
        b.tooltip = 'List view';
        b.setIconClass('icon-list-view');
      } else {
        this.mainPanel.getEl().removeClass('group-view');
        this.mainPanel.addClass('list-view');
        this.modality = 'list-view';
        
        b.tooltip = LN('sbi.browser.folderdetailpanel.groupviewTT');
        b.setIconClass('icon-group-view');        
      }
      
       var btnEl = b.el.child(b.buttonSelector);
       if(b.tooltip){
            if(typeof b.tooltip == 'object'){
                Ext.QuickTips.register(Ext.apply({
                      target: btnEl.id
                }, b.tooltip));
            } else {
                btnEl.dom[b.tooltipType] = b.tooltip;
            }
        }   
      
      this.loadingMask.hide();
    }
	, addNewDocument: function() {
		alert("addNewDocument");
		var urlToCall = this.services['newDocument'];
		
		if(this.folderId != null){
			urlToCall += '&FUNCT_ID='+this.folderId;
		}
		
		window.location.href=urlToCall;	
	}
    , loadFolder: function(folderId, rootFolderId) {
      
      this.folderId = folderId;	
      
      var p = {};
      
      if(folderId) {
    	  p.folderId = folderId;
      }
      
      if(rootFolderId) {
    	  p.rootFolderId = rootFolderId;
      }
     
      
      this.store.proxy.conn.url = this.store.browseUrl;
      this.store.baseParams = p || {};
      this.store.load();
     
     
      Ext.Ajax.request({
          url: this.services['loadFolderPathService'],
          params: p,
          callback : function(options , success, response){
    	  	if(success && response !== undefined) {   
	      		if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined) {
	      				if (Sbi.settings.browser.showBreadCrumbs !== undefined && Sbi.settings.browser.showBreadCrumbs){
	      					this.setBreadcrumbs(content);
	      				}
	      				this.fireEvent('onfolderload', this);
	      			} 
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
    	  	}
          },
          scope: this,
  		  failure: Sbi.exception.ExceptionHandler.handleFailure      
       });
    }
    
    , searchFolder: function(params) {       
    	this.store.proxy.conn.url = this.store.searchUrl;
        this.store.baseParams = params || {};
        this.store.load();  
        this.setTitle('Query results ...');
    }
    
   
    , setTitle: function(title) {
    	this.resetToolbar();
    	this.toolbar.add({
    		xtype: 'tbtext'
    		, text: title || ''
    	});
    	this.reinitToolbar();
    }
    
    , setBreadcrumbs: function(breadcrumbs) {
    	this.resetToolbar();
    	
    	this.toolbar.addSpacer();
    	this.toolbar.addDom('<image width="12" height="12" src="../themes/sbi_default/img/analiticalmodel/browser/server16x16.png"></image>');
    	this.toolbar.addSpacer();
    	
    	
    	var titleLn = LN('sbi.browser.document.functionalities');
    	if(breadcrumbs[0]){
    			breadcrumbs[0].name = titleLn;
    	}
    	
    	
        for (var i = 0; i < breadcrumbs.length - 1; i++) {
        	this.toolbar.add({
        		text: breadcrumbs[i].name
        		, breadcrumb: breadcrumbs[i]
        		, listeners: {
        			'click': {
                  		fn: this.onBreadCrumbClick,
                  		scope: this
                	} 
        	}
        	});
        	 
        	this.toolbar.addSpacer();
        	this.toolbar.addDom('<image width="3" height="6" src="../themes/sbi_default/img/analiticalmodel/execution/c-sep.gif"></image>');
        	this.toolbar.addSpacer();
        }

        this.toolbar.add({
    		text: breadcrumbs[breadcrumbs.length-1].name
    		, breadcrumb: breadcrumbs[breadcrumbs.length-1]
    		, disabled: true
    		, cls: 'sbi-last-folder'
    	});
        
       this.reinitToolbar();
    }
    
    , resetToolbar: function() {
    	this.toolbar.items.each(function(item){            
            this.items.remove(item);
            item.destroy();           
        }, this.toolbar.items); 
    }
    
    , reinitToolbar: function() {
    	var tt;
        var cls;
        if(this.modality === 'list-view') {
        	tt = LN('sbi.browser.folderdetailpanel.groupviewTT');
        	cls = 'icon-group-view';
        } else {
        	tt = LN('sbi.browser.folderdetailpanel.listviewTT');
        	cls = 'icon-list-view';
        }
        this.toolbar.buttonsL['toggleView'] = new Ext.Toolbar.Button({
        	tooltip: tt,
    		iconCls: cls,
    		listeners: {
    			'click': {
              		fn: this.toggleDisplayModality,
              		scope: this
            	} 
    		}
        });
        if (this.isAbleToCreateDocument()){
	        this.toolbar.buttonsL['newDocument'] = new Ext.Toolbar.Button({
	        	tooltip: LN('sbi.generic.add'),
	    		iconCls:'icon-add',
	    		listeners: {
	    			'click': {
	              		fn: this.addNewDocument,
	              		scope: this
	            	} 
	    		}
	        });
	        this.toolbar.add('->', this.toolbar.buttonsL['toggleView'], this.toolbar.buttonsL['newDocument']);
        }else{
        	this.toolbar.add('->', this.toolbar.buttonsL['toggleView']);
        }
    }
   
    
    , onBreadCrumbClick: function(b, e) {    	
    	this.fireEvent('onbreadcrumbclick', this, b.breadcrumb, e);
    }
    
    
    
    // private methods 
    
    , onClick: function(dataview, i, node, e) {
    	
//    	var button = e.getTarget('div[class=button]', 10, true);
//    	if(button) {
//		var buttonImg = button.down('img');
//		var startIndex = (' '+buttonImg.dom.className+' ').indexOf(' action-');
//		if(startIndex != -1) {
//			action = buttonImg.dom.className.substring(startIndex).trim().split(' ')[0];
//			action = action.split('-')[1];
//		}    		
//	}
    	
    	var actionDetail = e.getTarget('li[class=detail]', 10, true);
    	var actionMetaData = e.getTarget('li[class=showmetadata]', 10, true);
        var actionDelete = e.getTarget('a[class=delete]', 10, true);
        var actionFavourite = e.getTarget('span.icon', 10, true); //TBD
      
    	var action = null;

    	 if (actionDetail != null){
         	Sbi.debug('view detail ');        	
         	action = actionDetail.dom.className;
    	 }else if (actionDelete != null){
         	Sbi.debug('delete');        	
         	action = actionDelete.dom.className;
    	 }else if (actionMetaData != null){
         	Sbi.debug('showMetadata');        	
         	action = actionMetaData.dom.className;
    	 }         	
    	
    	var r = this.folderView.getRecord(i);
    	if(r.engine) {
    		if(action !== null) {
    			this.performActionOnDocument(r, action);
    		} else {
    			this.fireEvent('ondocumentclick', this, r, e);
    		}
    	} else{
    		if(action !== null) {
    			this.performActionOnFolder(r, action);
    		} else {
    			this.fireEvent('onfolderclick', this, r, e);
    		}
    	}      
    }
   
    , sort : function(groupName, attributeName) {
    	if(this.loadingMask) this.loadingMask.show();
    	//alert('sort: ' + groupName + ' - ' + attributeName);
    	this.folderView.sort(groupName, attributeName);
    	if(this.loadingMask) this.loadingMask.hide();
    }
    
    , group : function(groupName, attributeName) {
    	if(this.loadingMask) this.loadingMask.show();
    	//alert('group: ' + groupName + ' - ' + attributeName);
    	this.folderView.group(groupName, attributeName);
    	if(this.loadingMask) this.loadingMask.hide();
    }
    
    , filter : function(type) {
    	if(this.loadingMask) this.loadingMask.show();
    	//alert('filter: ' + type);
    	this.folderView.filter(type);
    	if(this.loadingMask) this.loadingMask.hide();
    }
    
    , deleteDocument: function(docId) {
    	
    	var p = {};
        
        if(docId) {
      	  p.docId = docId;
      	  p.folderId = this.folderId;
        }
        
    	Ext.Ajax.request({
             url: this.services['deleteDocument'],
             params: p,
             callback : function(options , success, response){
    			 //alert(options.params.docId));
	       	  	 if(success && response !== undefined) {   
	   	      		if(response.responseText !== undefined) {
	   	      			Ext.MessageBox.show({
		      				title: 'Status',
		      				msg: 'Documnt/s deleted succesfully',
		      				modal: false,
		      				buttons: Ext.MessageBox.OK,
		      				width:300,
		      				icon: Ext.MessageBox.INFO 			
		      			});
	   	      			this.loadFolder(this.folderId);
	   	      		} else {
	   	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	   	      		}
	       	  	}
             },
             scope: this,
     		 failure: Sbi.exception.ExceptionHandler.handleFailure      
       });
    }
    
     , showDocumentMetadata: function(docId) {
        this.win_metadata = new Sbi.execution.toolbar.MetadataWindow({'OBJECT_ID': docId});
		this.win_metadata.show();
    }
    
    
    , performActionOnDocument: function(docRecord, action) {
    	if(this.fireEvent('beforeperformactionondocument', this, docRecord, action) !== false){
    		if(action === 'delete') {
    			Ext.MessageBox.confirm(
    					LN('sbi.generic.pleaseConfirm')
    					, LN('sbi.generic.confirmDelete')
    		            , function(btn, text) {
    		                if ( btn == 'yes' ) {
    		                	this.deleteDocument(docRecord.id);
    		                }
    					}
    					, this
    				);
    			
    		} else if(action === 'showmetadata' && Sbi.user.functionalities.contains('SeeMetadataFunctionality')) {
    			//alert(docRecord.id + '; ' + this.folderId);
    			this.showDocumentMetadata(docRecord.id);
    		} else if(action === 'detail') {

				var urlToCall = this.services['detailDocument'];
				urlToCall += '&OBJECT_ID='+docRecord.id;
				window.location.href=urlToCall;	
    		}
    	}
    }
    
    , performActionOnFolder: function(dirRecord, action) {
    	if(this.fireEvent('beforeperformactiononfolder', this, dirRecord, action) !== false){
    	
    		var services = [];
    		var serviceConf = {
    			baseParams: new Object()
    		};
    		
    		if(action === 'export' && Sbi.user.functionalities.contains('SeeMetadataFunctionality')) {
    			serviceConf.serviceName = 'START_MASSIVE_EXPORT_THREAD_ACTION';
    			services['performFinishAction'] = Sbi.config.serviceRegistry.getServiceUrl(serviceConf);
    			var popupWin = new Sbi.browser.mexport.MassiveExportWizard({
    				functId : dirRecord.id
    				, functCd : dirRecord.code
    				, wizardType: 'export'
    				, services: services
    			});
    			popupWin.show();
    			popupWin.doLayout();
    		
    		} else if(action === 'schedule' && Sbi.user.functionalities.contains('SeeMetadataFunctionality')) {
    			serviceConf.serviceName = 'START_MASSIVE_SCHEDULE_ACTION';
    			services['performFinishAction'] = Sbi.config.serviceRegistry.getServiceUrl(serviceConf);
    			var popupWin = new Sbi.browser.mexport.MassiveExportWizard({
    				functId : dirRecord.id
    				, functCd : dirRecord.code
    				, wizardType: 'schedule'
    				, services: services
    			});
    			popupWin.show();
    			popupWin.doLayout();
    		}
    	}
    }
    
    , isAbleToCreateDocument: function(){
    	var funcs = Sbi.user.functionalities;
    	if (funcs == null || funcs == undefined) return false;
    	
    	for (f in funcs){
    		if (funcs[f] == this.DETAIL_DOCUMENT){
    			return true;
    			break;
    		}
    	}
    	
    	return false;
    }
    
    
   
});


