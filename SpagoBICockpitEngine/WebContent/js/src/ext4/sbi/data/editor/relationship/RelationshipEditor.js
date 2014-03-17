/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.data.editor.relationship.RelationshipEditor', {
	extend: 'Ext.Panel'

	, config:{	
		  services: null		
		, dsContainerPanel: null
		, relContainerPanel: null
		, contextMenu: null		
		, engineAlreadyInitialized : null
		, layout: 'border'
		, border: false
		, autoScroll: true
	}

	, constructor : function(config) {

		Sbi.trace("[RelationshipEditor.constructor]: IN");
		
		var defaultSettings = {
			engineAlreadyInitialized : false
			, border : false
			, autoScroll: true
		};
	
		var settings = Sbi.getObjectSettings('Sbi.data.editor.relationship.RelationshipEditor', defaultSettings);
		
		var c = Ext.apply(settings, config || {});
		Ext.apply(this, c);
	
		this.addEvents('addRelation');
		
		this.init(config);
		
		this.items = [
			        {
			        	id: 'dsContainerPanel',
			        	region: 'center',
	//		        	collapseMode:'mini',
	//		        	split: true,
			        	layout: 'fit',
			        	autoScroll: true,
			        	items: [this.dsContainerPanel]
			        },
			        {
			        	id: 'relContainerPanel',	  
			        	region: 'south',
	//		        	split: true,
	//		        	collapseMode:'mini',
			        	autoScroll: true,
			        	items: [this.relContainerPanel]
			        }
				];
		this.callParent(c);
		Sbi.trace("[RelationshipEditor.constructor]: OUT");
		
	}

	
	

	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	, initializeEngineInstance : function (config) {

	}
	
	, init : function (config) {
		this.initPanels(config);
	}
	
	, initPanels: function(config){
		this.initDatasetPanel(config);
		this.initRelationshipPanel(config);
	}
	
	, initDatasetPanel: function(config) {
		this.dsContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorDatasetContainer',{usedDatasets: this.usedDatasets});
//		this.dsContainerPanel.on('addRelationship',this.addRelationship, this);
	}
	
	, initRelationshipPanel: function(config) {
		this.relContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorList',{height:200});
//		this.relContainerPanel.addListener('addRelation', this.addRelation, this);

	}
	
	, addRelation: function(){		
		var allDs = this.dsContainerPanel.getAllDatasets();
		var relToAdd = new Array();
		relToAdd.id = '__';
		for (var i=0; i< allDs.length; i++){
			var ds = allDs.get(i);
			var f = this.dsContainerPanel.getSelection(ds.dataset);
			if (f !== null){
				f.ds = ds.dataset;	
				relToAdd.push(f);
			}
		}
		this.dsContainerPanel.addRelation(relToAdd);
		
		this.fireEvent('addRelation');
	}
	
});
