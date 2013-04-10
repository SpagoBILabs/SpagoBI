/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */

Ext.ns("Sbi.browser");

Sbi.browser.DocumentsBrowser = function(config) {    
   
	// sub-components   
	
	this.tabbedBrowser = config.parentTab;
	this.rootFolderId = config.rootFolderId || null;
	this.selectedFolderId = this.rootFolderId;
	
	this.treePanel = new Sbi.browser.DocumentsTree({
        border: true
        , rootNodeId: this.selectedFolderId 
    });
	
	this.filterPanel = new Sbi.browser.FilterPanel({
        title: LN('sbi.browser.filtrpanel.title')
        , border:true
        , metaFolder: config.metaFolder
        , metaDocument: config.metaDocument	
    });
	
	if (Sbi.user.functionalities.contains('DoMassiveExportFunctionality')) {
		this.progressPanel = new Sbi.browser.ProgressPanel({
			title: LN('sbi.browser.progresspanel.title')
			, border:true
			, metaFolder: config.metaFolder
			, metaDocument: config.metaDocument	
		});
	}
	
	
	this.searchPanel = new Sbi.browser.SearchPanel({
        title: LN('sbi.browser.searchpanel.title')
        , border:true
        , metaDocument: config.metaDocument	
    });
		
	this.westRegionContainer = new Ext.Panel({
	       id:'westRegionContainer',
	       split:true,
	       border:true,
	       frame:true,
	       collapsible: true,
	       //margins:'0 0 0 15',
	       layout:'accordion',
	       layoutConfig:{
	          animate:true
	       },
	       items: [
	               this.treePanel
	               , this.filterPanel
	               , this.searchPanel
	       ]
	});

	if(this.progressPanel){
	// defined and added only if user has massive export functionality	
		this.westRegionContainer.add(this.progressPanel);
	}
	
	
	this.detailPanel = new Sbi.browser.FolderDetailPanel({ 
		layout: 'fit'
        , metaFolder: config.metaFolder
        , metaDocument: config.metaDocument	
        , folderId: this.selectedFolderId
    });

	this.centerContainerPanel = new Ext.Panel({
		 region: 'center'
//		 , enableTabScroll:true
//		 , defaults: {autoScroll:true}	

		 , items: [this.detailPanel]
		,layout: 'fit'
	});
	config.baseLayout = config.baseLayout || {}; 	
	var c = Ext.apply({}, config.baseLayout, {
		layout: 'border',
	    border: false,
	    title:'Document browser',
	    items: [ 
	            // CENTER REGION ---------------------------------------------------------
	            this.centerContainerPanel, 
	            // WEST REGION -----------------------------------------------------------
	            new Ext.Panel({               
	                region: 'west',
	                border: false,
	                frame: false,
	                //margins: '0 0 3 3',
	                collapsible: true,
	                collapsed: false,
	                hideCollapseTool: true,
	                titleCollapse: true,
	                collapseMode: 'mini',
	                split: true,
	                autoScroll: false,
	                width: 280,
	                minWidth: 280,
	                layout: 'fit',
	                items: [this.westRegionContainer]
	              })
	            // NORTH HREGION -----------------------------------------------------------
	            /*
	          	,new Sbi.browser.Toolbar({
	            	region: 'north',
	            	margins: '3 3 3 3',
	            	autoScroll: false,
	            	height: 30,
	            	layout: 'fit'
	          	})
	          	*/
	        ]
	});   
	config.baseLayout = config.baseLayout || {}; 	
   
    Sbi.browser.DocumentsBrowser.superclass.constructor.call(this, c);
	
	// if browser is IE, re-inject parent.execCrossNavigation function in order to solve parent variable conflict that occurs when 
	// more iframes are built and the same function in injected: it is a workaround that let cross navigation work properly
	if (Ext.isIE) {
		this.on(
				'tabchange',
				function () {
					var anActiveTab = this.getActiveTab();
					if (anActiveTab.tabType == 'document' && anActiveTab.getActiveDocument() !== undefined) {
						try {
							var documentPage = anActiveTab.getActiveDocument().getDocumentExecutionPage();
							if (documentPage.isVisible()) {
								documentPage.injectCrossNavigationFunction();
							}
						//} catch (e) {alert(e);}
						} catch (e) {}
					}
				}
				, this
		);
	}
	//send messages about enable or disable datastore refresh action (for console engine) 
	this.on(
	   'beforetabchange',
	   function (tabPanel, newTab, currentTab ) {
		   if(currentTab && currentTab.tabType === 'document' && currentTab.getActiveDocument() && currentTab.getActiveDocument().getDocumentExecutionPage()) {
			   currentTab.getActiveDocument().getDocumentExecutionPage().getDocumentPage().sendMessage('Disable datastore', 'hide');
		   }
		   if(newTab.tabType === 'document' && newTab.getActiveDocument() && newTab.getActiveDocument().getDocumentExecutionPage()){
			   newTab.getActiveDocument().getDocumentExecutionPage().getDocumentPage().sendMessage('Enable datastore', 'show');
		   }
	   }
	   , this
	);
	
    this.treePanel.addListener('click', this.onTreeNodeClick, this);
 
    this.detailPanel.addListener('onfolderload', this.onFolderLoad, this);
    this.detailPanel.addListener('ondocumentclick', this.onDocumentClick, this);
    
    this.detailPanel.addListener('onfolderclick', this.onFolderClick, this);
    this.detailPanel.addListener('onbreadcrumbclick', this.onBreadCrumbClick, this);
    
    this.searchPanel.addListener('onsearch', this.onSearch, this);
    this.searchPanel.addListener('onreset', this.onReset, this);
    
    this.filterPanel.addListener('onsort', this.onSort, this);
    this.filterPanel.addListener('ongroup', this.onGroup, this);
    this.filterPanel.addListener('onfilter', this.onFilter, this);
   
    if(this.progressPanel){ 
    	this.progressPanel.addListener('click', this.onTreeNodeClick, this);
    }
    
    
}


Ext.extend(Sbi.browser.DocumentsBrowser, Ext.Panel, {
    	
	rootFolderId: null
    , selectedFolderId: null
    
	, westRegionContainer: null
    , treePanel: null
    , filterPanel: null
    , searchPanel: null
    , progressPanel: null
    
    , centerRegionContainer: null
    , detailPanel: null
    , executionPanel: null

    
    , selectFolder: function(folderId) {
		this.detailPanel.loadFolder(folderId, this.rootFolderId);
		this.selectedFolderId = folderId;
		this.searchPanel.selectedFolderId = folderId;
	}
	
	, onFolderLoad: function(panel) {
//		if(this.brTab.getActiveTab() != this.detailPanel) {
//			this.brTab.setActiveTab(this.detailPanel);
//			
//		}
//		this.detailPanel.show();
	}
    
    
    , onTreeNodeClick: function(node, e) {
		this.selectFolder(node.id);
	}
    
    , onOpenFavourite: function(doc){
    	var executionPanel = new Sbi.execution.ExecutionPanel({
			title: doc.title !== undefined ? doc.title : doc.name
			, closable: true
		}, doc);
		executionPanel.tabType = 'document';
		
		executionPanel.addListener('crossnavigationonothertab', this.onCrossNavigation, this);
		executionPanel.addListener('openfavourite', this.onOpenFavourite, this);
		
		
		this.tabbedBrowser.brTab.add(executionPanel).show();
		
		executionPanel.execute();
	}
    
	, onCrossNavigation: function(config){
		this.onCrossNavigationDocumentClick(config);
		return false;
	}
	
	, onCrossNavigationDocumentClick: function(r) {

		var config = Ext.apply({
			//title: (r.document.title !== undefined || r.document.title != null)? r.document.title : r.document.name, 
			closable: true
		}, r);
		
		var name = r.document.name;
		var title = r.document.title;
		if(title !== undefined){
			config.title = title;
		}else{
			config.title = name;
		}
		
		var executionPanel = new Sbi.execution.ExecutionPanel(config, r.document);
		executionPanel.tabType = 'document';
		
		executionPanel.addListener('crossnavigationonothertab', this.onCrossNavigation, this);
		executionPanel.addListener('openfavourite', this.onOpenFavourite, this);
		
		this.tabbedBrowser.brTab.add(executionPanel).show();
		
		executionPanel.execute();
	}

	, onDocumentClick: function(panel, doc) {
		
		var executionPanel = new Sbi.execution.ExecutionPanel({
			title: doc.title !== undefined ? doc.title : doc.name
			, closable: true
		}, doc);
		executionPanel.tabType = 'document';
		
		executionPanel.addListener('crossnavigationonothertab', this.onCrossNavigation, this);
		executionPanel.addListener('openfavourite', this.onOpenFavourite, this);
		
		
		this.tabbedBrowser.brTab.add(executionPanel).show();
		
		executionPanel.execute();
	}
	
	, onFolderClick: function(panel, r) {
		this.selectFolder(r.id);
	}
	
	
	, onBreadCrumbClick: function(panel, b) {
		this.selectFolder(b.id);
	}
	
	, onSearch: function(panel, q) {
		if(this.rootFolderId) q.rootFolderId = this.rootFolderId;
		this.detailPanel.searchFolder(q);
	}
	
	, onSort: function(panel, cb) {
		this.detailPanel.sort('Documents', cb.inputValue);
	}
	
	, onReset: function(panel, cb) {
		this.selectFolder(this.selectedFolderId);
	}
	
	, onGroup: function(panel, cb) {
		this.detailPanel.group('Documents', cb.inputValue);
	}
	
	, onFilter: function(panel, cb) {
		this.detailPanel.filter(cb.inputValue);
	}
});