/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.data.editor.relationship.RelationshipEditor', {
	extend: 'Ext.Panel'
    , layout: 'border'

	, config:{	
		  services: null		
		, usedDatasets: null
		, contextMenu: null		
//		, engineAlreadyInitialized : null
		, border: false
		, autoScroll: true
	}

	/**
	 * @property {Sbi.data.editor.relationship.RelationshipEditorDatasetContainer} dsContainerPanel
	 * The container of datasets
	 */
	, dsContainerPanel: null
	/**
	 * @property {Sbi.data.editor.relationship.RelationshipEditorList} relContainerPanel
	 * The container of relationships
	 */
	, relContainerPanel: null
	/**
	 * @property {Ext.Array} relationsList
	 * The list with all relations
	 */
	, relationsList: null 	

	, constructor : function(config) {
		Sbi.trace("[RelationshipEditor.constructor]: IN");
		this.initConfig(config);
		this.initPanels(config);
		this.callParent(arguments);		
		this.addEvents('addRelation','addRelationToList');
		Sbi.trace("[RelationshipEditor.constructor]: OUT");	
	}

	,  initComponent: function() {
	        Ext.apply(this, {
	            items:[{
						id: 'dsContainerPanel',
						region: 'center',
						layout: 'fit',
						autoScroll: true,
						items: [this.dsContainerPanel]
						},
						{
						id: 'relContainerPanel',	  
						region: 'south',
						autoScroll: true,
						items: [this.relContainerPanel]
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
		this.initDatasetPanel(config);
		this.initRelationshipPanel(config);
	}
	
	, initDatasetPanel: function(config) {
		this.dsContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorDatasetContainer',{usedDatasets: this.usedDatasets});
	}
	
	, initRelationshipPanel: function(config) {
		this.relContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorList',{height:200});
		this.relContainerPanel.addListener('addRelation', this.addRelation, this);
		this.relContainerPanel.addListener('modifyRelation', this.modifyRelation, this);
		this.relContainerPanel.addListener('removeRelation', this.removeRelation, this);
		this.relContainerPanel.addListener('selectRelation', this.selectRelation, this);
		this.relContainerPanel.addListener('updateIdentifier', this.updateIdentifier, this);
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method (fired)
	 * Adds a new relation with active selections to the relationsList and to the associations grid
	 * 
	 * @param {String} n The identifier (setted for update context)
	 */
	, addRelation: function(n){		
		var toReturn = true;
		
		var allDs = this.dsContainerPanel.getAllDatasets();
		var relToAdd = new Array();
		relToAdd.id = this.getRelationId(n);
		for (var i=0; i< allDs.length; i++){			
			var ds = allDs.get(i);
			var f = this.dsContainerPanel.getSelection(ds.dataset);
			if (f !== null){
				f.ds = ds.dataset;	
				relToAdd.push(f);
			}
		}
		
		toReturn = this.addRelationToList(relToAdd);
		
		return toReturn;
	}
	
	/**
	 * @method (fired)
	 * Remove the relation from the relationsList
	 * 
	 * @param {String} r The relation content to remove
	 */
	, removeRelation: function(r){
		for (var i=0; i<this.relationsList.length; i++){
			var obj = this.relationsList[i];
			if (obj && obj.rel == r){
				this.relationsList.splice(i,1);
				break;
			}
		}
		Sbi.trace("[RelationshipEditor.removeRelation]: Removed association ['"+ r +"']");
		Sbi.trace("[RelationshipEditor.removeRelation]: Associations List upgraded is  [ " +  Sbi.toSource(this.relationsList) + ']');
	}
	
	/**
	 * @method (fired)
	 * Update (with an add and remove of the element) the relation from the relationsList and grid
	 * 
	 */
	, modifyRelation: function(){
		var relToModify = this.relContainerPanel.getCurrentRel();		
		if (relToModify == null){
	   		  alert(LN('sbi.cockpit.relationship.editor.msg.modify'));
	   		  return;
		}
		var relToModifyRec = this.relContainerPanel.getRelationById(relToModify.id);
	    if (this.addRelation(relToModify.id)){
			this.relContainerPanel.removeRelationFromGrid(relToModifyRec);
			this.removeRelation(relToModify.rel);	    
	    }
		
	}
	
	/**
	 * @method (fired)
	 * Select the cells linked to the list grid
	 * 
	 * @param {String} r The relation content to use for the selection of elements
	 */
	, selectRelation: function(r){
		var lst = r.rel.split('=');
		for (var i=0; i<lst.length; i++){
			var el = lst[i].split('.');
			this.dsContainerPanel.setSelection(el);
		}
	}
	
	/**
	 * @method (fired)
	 * Update the identifier modified manually from the user
	 * 
	 * @param {Element} e The element (cell) modified.
	 */
	, updateIdentifier: function(e){
		var obj = this.getRelationById(e.originalValue);
		obj.id = e.value;
		
	}
	
	/**
	 * @method 
	 * Returns the relations list
	 * 
	 */
	, getRelationsList: function(){
		return this.relationsList;
	}

	/**
	 * @method 
	 * Set the relations list
	 * 
	 */
	, setRelationsList: function(r){
		this.relationsList = r;
	}
	
	/**
	 * @method 
	 * Reset the relations list
	 * 
	 */
	, removeAllRelations: function(){
		this.relationsList = new Array();
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * @method 
	 * Adds a new relation to the relationsList with the selected elements.
	 * 
	 * @param {Array} r The array of elements
	 */
	, addRelationToList: function(r){
		var toReturn = true;
		
		if (this.relationsList == null) 
			this.relationsList = new Array();
		
		var obj = '';
		var objType = '';
		var equal = '';
		var wrongTypes = false;
		
		for (var i=0; i< r.length; i++){			
			var el = r[i];			
			
			if (i==0){
				objType = el.colType;
				equal = '=';
			}else{
				//check consistency between type fields
				if (objType !== el.colType){
					wrongTypes = true;					
				}
			}
			//create association string (ex: tabA.colA=tabB.colB...)
			obj += el.ds + '.' + el.alias + ((i<r.length-1)?equal:'');				
		}
		
		if (this.existsRelation(obj)){
			alert(LN('sbi.cockpit.relationship.editor.msg.duplicate'));
			return false;
		}

		//adds only new associations
		if (wrongTypes){
			Ext.MessageBox.confirm(
					LN('sbi.generic.pleaseConfirm')
					, LN('sbi.cockpit.relationship.editor.msg.differentType')
		            , function(btn, text) {
		                if ( btn == 'yes' ) {
		                	this.relationsList.push({id: r.id, rel:obj});	
		                	this.relContainerPanel.addRelationToList({id: r.id, rel:obj});
		                	toReturn = true;
		                }else
		                	toReturn = false;
					}
					, this
				);
		}else{
			if (obj !== ''){
				this.relationsList.push({id: r.id, rel:obj});
				this.relContainerPanel.addRelationToList({id: r.id, rel:obj});
				toReturn = true;
			}else{
				alert(LN('sbi.cockpit.relationship.editor.msg.selectFields'));
				toReturn = false;
			}
		}
		Sbi.trace("[RelationshipEditor.addRelation]: Associations List updated with  [ " +  Sbi.toSource(this.relationsList) + ']');
		return toReturn;
	}
	
	, existsRelation: function(r){
		if (this.getRelationByRel(r)  != null)
			return true;				
		else
			return false;
	}
	
	/**
	 * @method 
	 * Returns the identifier for the relation (insert or update action)
	 * 
	 * @param {String} n The identifier. Setted only for update action
	 */
	, getRelationId: function(n){
		var newId = '';
		//parameter n is valorized only in modify context
		if (n !== null && n !== undefined) 
			newId += n;
		else{
			newId += '#';
			if (this.relationsList != null){
				//get max id already setted
				var maxId = 0;			
				for (var i=0; i<this.relationsList.length; i++ ){
					var currId = this.relationsList[i].id.substring(1);
					if (maxId < parseInt(currId))
						maxId = parseInt(currId);
				}
				newId += (maxId+1).toString();
			}
			else
				newId += '0';
		}	
		
		return newId;
	}
	
	/**
	 * @method 
	 * Returns the relation object getted from the relationsList throught the id. 
	 * Format: {id:xx, rel:yy}
	 * 
	 * @param {String} id The identifier.
	 */
	, getRelationById: function(id){
		if (this.relationsList == null) return null;
		for (var i=0; i<this.relationsList.length; i++){
			var obj = this.relationsList[i];
			if (obj && obj.id == id){
				return obj;
				break;
			}
		}		
		return null;
	}
	
	/**
	 * @method 
	 * Returns the relation object getted from the relationsList throught the relation content. 
	 * Format: {id:xx, rel:yy}
	 * 
	 * @param {String} rel The relation content.
	 */
	, getRelationByRel: function(r){
		if (this.relationsList == null) return null;
		for (var i=0; i<this.relationsList.length; i++){
			var obj = this.relationsList[i];
			if (obj && obj.rel == r){
				return obj;
				break;
			}
		}		
		return null;
	}
});
