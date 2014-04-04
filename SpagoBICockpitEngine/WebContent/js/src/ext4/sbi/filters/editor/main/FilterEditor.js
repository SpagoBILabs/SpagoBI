/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.filters.editor.main.FilterEditor', {
	extend: 'Ext.Panel'
    , layout: 'fit'

	, config:{	
		  services: null		
		, storesList: null	   
		, contextMenu: null		
		, border: false
//		, autoScroll: true
	}

	/**
	 * @property {Sbi.filters.editor.main.FilterEditorList} filterContainerPanel
	 * The container of datasets
	 */
	, filtersContainerPanel: null
	
	/**
	 * @property {Ext.Array} associationsList
	 * The list with all filters
	 */
	, filters: null 	

	, constructor : function(config) {
		Sbi.trace("[FilterEditor.constructor]: IN");
		this.initConfig(config);
		this.initPanels(config);
		this.callParent(arguments);		
//		this.addEvents('addAssociation','addAssociationToList');
		Sbi.trace("[FilterEditor.constructor]: OUT");	
	}

	,  initComponent: function() {
	        Ext.apply(this, {
	            items:[{
						id: 'filtersContainerPanel',
//						region: 'center',
						layout: 'fit',
						autoScroll: true,
						split: true,
						items: [this.filtersContainerPanel]
						}]
	        });
	        this.callParent();
	    }
	   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	, initializeEngineInstance : function (config) {

	}
	
	, initPanels: function(config){
		this.initFiltersPanel(config);
	}

	
	, initFiltersPanel: function(config) {
		this.filtersContainerPanel = Ext.create('Sbi.filters.editor.main.FilterEditorList',{storesList: this.storesList});
//		this.filtersContainerPanel.addListener('addAssociation', this.addAssociation, this);
//		this.filtersContainerPanel.addListener('modifyAssociation', this.modifyAssociation, this);
//		this.filtersContainerPanel.addListener('removeAssociation', this.removeAssociation, this);
//		this.filtersContainerPanel.addListener('selectAssociation', this.selectAssociation, this);
//		this.filtersContainerPanel.addListener('updateIdentifier', this.updateIdentifier, this);
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method (fired)
	 * Adds a new Association with active selections to the associationsList and to the associations grid
	 * 
	 * @param {String} n The identifier (setted for update context)
	 */
//	, addAssociation: function(n){		
//		var toReturn = true;
//		
//		var allDs = this.dsContainerPanel.getAllDatasets();
//		var assToAdd = new Array();
//		assToAdd.id = this.getAssociationId(n);
//		for (var i=0; i< allDs.length; i++){			
//			var ds = allDs.get(i);
//			var f = this.dsContainerPanel.getSelection(ds.dataset);
//			if (f !== null){
//				f.ds = ds.dataset;	
//				assToAdd.push(f);
//			}
//		}
//		
//		toReturn = this.addAssociationToList(assToAdd);
//		
//		return toReturn;
//	}
//	
//	/**
//	 * @method (fired)
//	 * Remove the Association from the AssociationsList
//	 * 
//	 * @param {String} r The Association content to remove
//	 */
//	, removeAssociation: function(r){
//		for (var i=0; i<this.associationsList.length; i++){
//			var obj = this.associationsList[i];
//			if (obj && obj.ass == r){
//				this.associationsList.splice(i,1);
//				break;
//			}
//		}
//		Sbi.trace("[AssociationEditor.removeAssociation]: Removed association ['"+ r +"']");
//		Sbi.trace("[AssociationEditor.removeAssociation]: Associations List upgraded is  [ " +  Sbi.toSource(this.associationsList) + ']');
//	}
//	
//	/**
//	 * @method (fired)
//	 * Update (with an add and remove of the element) the Association from the AssociationsList and grid
//	 * 
//	 */
//	, modifyAssociation: function(){
//		var assToModify = this.assContainerPanel.getCurrentAss();		
//		if (assToModify == null){
//	   		  alert(LN('sbi.cockpit.association.editor.msg.modify'));
//	   		  return;
//		}
//		var assToModifyRec = this.assContainerPanel.getAssociationById(assToModify.id);
//	    if (this.addAssociation(assToModify.id)){
//			this.assContainerPanel.removeAssociationFromGrid(assToModifyRec);
//			this.removeAssociation(assToModify.ass);	    
//	    }
//		
//	}
//	 
//	/**
//	 * @method (fired)
//	 * Select the cells linked to the list grid
//	 * 
//	 * @param {String} r The Association content to use for the selection of elements
//	 */
//	, selectAssociation: function(r){
//		this.dsContainerPanel.resetSelections();
//		var lst = r.ass.split('=');
//		for (var i=0; i<lst.length; i++){
//			var el = lst[i].split('.');
//			this.dsContainerPanel.setSelection(el);
//		}
//	}
//	
//	/**
//	 * @method (fired)
//	 * Update the identifier modified manually from the user
//	 * 
//	 * @param {Element} e The element (cell) modified.
//	 */
//	, updateIdentifier: function(e){
//		var obj = this.getAssociationById(e.originalValue);
//		obj.id = e.value;
//		
//	}
	
	/**
	 * @method 
	 * Returns the Associations list
	 * 
	 
	, getAssociationsList: function(){
		return this.associationsList;
	}
*/
	/**
	 * @method 
	 * Set the Associations list
	 * 
	 
	, setAssociationsList: function(r){
		this.associationsList = r;
	}
	*/
	/**
	 * @method 
	 * Reset the Associations list
	 * 
	 
	, removeAllAssociations: function(){
		this.associationsList = new Array();
	}
	*/
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
});
