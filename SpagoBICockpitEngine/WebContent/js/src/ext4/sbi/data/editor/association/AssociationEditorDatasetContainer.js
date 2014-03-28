/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one ats http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.data.editor.association.AssociationEditorDatasetContainer', {
	extend: 'Ext.Panel'
	, layout: 'column'
	, config:{	
		  services: null			
		, dsContainerPanel: null
		, usedDatasets: null
		, engineAlreadyInitialized : false
		, border : false
		, autoScroll: true
		
	}

	, constructor : function(config) { 
		Sbi.trace("[AssociationEditorDatasetContainer.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(config);	
		Sbi.trace("[AssociationEditorDatasetContainer.constructor]: OUT");
	}


	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	, init: function() {
		var items = new Array();
		
		for (var i=0; i < this.usedDatasets.length; i++){
				var item = new Sbi.data.editor.association.AssociationEditorDataset({
					border: false,
					height : 225,
					width : 180,
					dataset: this.usedDatasets[i]
				});
				items.push(item);
		}
		
		this.items = items;
	}
	
	, resetSelections: function(){
		for (var i=0; i<this.items.length; i++){
			var item = this.items.get(i);
			var sm = item.grid.getSelectionModel();
			if (sm != null && sm !== undefined){
				sm.deselectAll(true);
			}
		}
	}
	
	// PUBLIC METHODS
	, getDatasetItem: function(idx){
		return this.items.get(idx);
	}
	
	, getDatasetItemByLabel: function(l){
		var toReturn = null;
		
		for (var i=0; i<this.items.length; i++){
			if (this.items.get(i).dataset === l){
				toReturn = this.items.get(i);
				break;
			}
		}
		return toReturn;
	}
	
	
	, getAllDatasets: function(){
		return this.items;
	}
	
	, getSelection: function(l){
		var toReturn = null;
		
		var ds = this.getDatasetItemByLabel(l);
		if (ds !== null && ds.grid !== null && 
				ds.grid.getSelectionModel().getSelections().length > 0 &&
				ds.grid.getSelectionModel().getSelections()[0] !== undefined)
			toReturn = ds.grid.getSelectionModel().getSelections()[0].data;
		return toReturn;
	}
		
	, setSelection: function(el){
		var dsLabel = el[0];
		var dsField = el[1];
		
		var ds = this.getDatasetItemByLabel(dsLabel);
		if (ds !== null && ds !== undefined){
			var recId = ds.grid.store.find('alias', dsField);
			ds.grid.getSelectionModel().select(recId,true,true);
		}
	}

});
