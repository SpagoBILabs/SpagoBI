/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one ats http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.data.editor.relationship.RelationshipEditorDatasetContainer', {
	extend: 'Ext.Panel'

	, config:{	
		  services: null		
		, relationsList: null 		
		, dsContainerPanel: null
	}

	, constructor : function(config) { 
		Sbi.trace("[RelationshipEditorDatasetContainer.constructor]: IN");
		
		var defaultSettings = {
			engineAlreadyInitialized : false
			, border : false
			, autoScroll: true
			, layout: 'column'
		};
	
		var settings = Sbi.getObjectSettings('Sbi.data.editor.relationship.RelationshipEditorDatasetContainer', defaultSettings);
		
		var c = Ext.apply(settings, config || {});
		Ext.apply(this, c);
		
		this.init(config);	 		
		this.callParent(c);
		Sbi.trace("[RelationshipEditorDatasetContainer.constructor]: OUT");
	}


	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	, init: function(config) {
		var items = new Array();
		
		// ONLY FOR TEST
		this.usedDatasets.push('ds__7525356');
		this.usedDatasets.push('dsTestForAdmin');
		//
		for (var i=0; i < this.usedDatasets.length; i++){
				items.push(new Sbi.data.editor.relationship.RelationshipEditorDataset({
				border: false,
		        gridConfig: {
		        	title: this.usedDatasets[i],
		        	margins: '5 5 5 0'
		        },
				height : 225,
				width : 180,
				dataset: this.usedDatasets[i],
				services : this.services
			}));
		}
		
		this.items = items;
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
		if (ds !== null && ds.grid !== null && ds.grid.getSelectionModel().getSelections().length >= 0 )
			toReturn = ds.grid.getSelectionModel().getSelections()[0].data;
			
		return toReturn;
	}
	
	, addRelation: function(r){
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
		                	alert('Added association!');
		                }
					}
					, this
				);
//			Ext.MessageBox.confirm(
//					LN('sbi.generic.pleaseConfirm')
//					, 'Non tutte le tipologie dei campi selezionati coincidono. Si intende proseguire con l\'aggiunta della relazione?'
//					, this.test 
//					, this
//				);
		}else{
			this.relationsList.push({id: r.id, rel:obj});		
			alert('Added association!');
		}
		

		Sbi.trace("[RelationshipEditor.addRelation]: Associations List updated with  [ " +  Sbi.toSource(this.relationsList) + ']');
		
	}
	
	, test: function(){
		//this.relationsList.push({id: r.id, rel:obj});
		alert('Added association!');
	}
});
