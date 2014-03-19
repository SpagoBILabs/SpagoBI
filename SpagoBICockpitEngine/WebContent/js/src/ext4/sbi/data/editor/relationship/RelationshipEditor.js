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
	
	, initializeEngineInstance : function (config) {

	}
	
	, initPanels: function(config){
		this.initDatasetPanel(config);
		this.initRelationshipPanel(config);
	}
	
	, initDatasetPanel: function(config) {
		this.dsContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorDatasetContainer',{usedDatasets: this.usedDatasets});
		this.dsContainerPanel.on('addRelationship',this.addRelationship, this);
	}
	
	, initRelationshipPanel: function(config) {
		this.relContainerPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditorList',{height:200});
		this.relContainerPanel.addListener('addRelation', this.addRelation, this);
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
//		this.dsContainerPanel.addRelation(relToAdd);
		this.addRelationToList(relToAdd);
		
//		this.fireEvent('addRelation');
//		this.fireEvent('addRelationToList');
	}
	
	, addRelationToList: function(r){
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
		
		if (wrongTypes){
			Ext.MessageBox.confirm(
					LN('sbi.generic.pleaseConfirm')
					, 'Non tutte le tipologie dei campi selezionati coincidono. Si intende proseguire con l\'aggiunta della relazione?'
		            , function(btn, text) {
		                if ( btn == 'yes' ) {
		                	this.relationsList.push({id: r.id, rel:obj});	
		                	this.relContainerPanel.addRelationToList({id: r.id, rel:obj});
//		                	this.fireEvent('addRelationToList');
		                }
					}
					, this
				);
		}else{
			if (obj !== ''){
				this.relationsList.push({id: r.id, rel:obj});
//				this.fireEvent('addRelationToList');
				this.relContainerPanel.addRelationToList({id: r.id, rel:obj});
			}else{
				alert('Please select fields for the association!');
			}
		}
		

		Sbi.trace("[RelationshipEditor.addRelation]: Associations List updated with  [ " +  Sbi.toSource(this.relationsList) + ']');
		
	}
	
});
