/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.data.editor.relationship.RelationshipEditorPage', {
	  extend: 'Ext.Panel'
	, layout: 'fit'
			
	, config:{	
		    usedDatasets: null
		  , itemId: 0
		  , border: false
	}

	/**
	 * @property {Sbi.data.editor.relationship.RelationshipEditor} relationshipEditorPanel
	 *  Container of the editor component
	 */
	 , relationshipEditorPanel: null
	
	 , constructor : function(config) {
		Sbi.trace("[RelationshipEditorPage.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(arguments);
		Sbi.trace("[RelationshipEditorPage.constructor]: OUT");
	 }
	
	 , initComponent: function() {
	     Ext.apply(this, {
	         items: [this.relationshipEditorPanel]
	     });
	     this.callParent();
	 }	   

	
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, updateValues: function(values) {
		Sbi.trace("[RelationshipEditorPage.updateValues]: IN");
		
		Sbi.trace("[RelationshipEditorPage.updateValues]: Input parameter values is equal to [" + Sbi.toSource(values) + "]");
		this.relationshipEditorPanel.controlPanel.updateValues(values);
		Sbi.trace("[RelationshipEditorPage.updateValues]: OUT");
	}

	, getValidationErrorMessages: function() {
		Sbi.trace("[RelationshipEditorPage.getValidationErrorMessage]: IN");
		var msg = null;

		// TODO check if the designer is properly defined
		
		Sbi.trace("[RelationshipEditorPage.getValidationErrorMessage]: OUT");
		
		return msg;
	}
	
	, isValid: function() {
		Sbi.trace("[RelationshipEditorPage.isValid]: IN");
	
		var isValid = this.getValidationErrorMessages() === null;
		
		Sbi.trace("[RelationshipEditorPage.isValid]: OUT");
		
		return isValid;
	}

	, applyPageState: function(state) {
		Sbi.trace("[RelationshipEditorPage.applyPageState]: IN");
		state =  state || {};
		if(this.relationshipEditorPanel) {
			state.relationsList = this.relationshipEditorPanel.getRelationsList();
		}
		Sbi.trace("[RelationshipEditorPage.applyPageState]: OUT");
		return state;
	}	

	, setPageState: function(state) {
		Sbi.trace("[RelationshipEditorPage.setPageState]: IN");
		Sbi.trace("[RelationshipEditorPage.setPageState]: state parameter is equal to [" + Sbi.toSource(state, true) + "]");
		
		this.relationshipEditorPanel.setRelationsList(state);
		
		Sbi.trace("[RelationshipEditorPage.setPageState]: OUT");
	}
	
	, resetPageState: function() {
		Sbi.trace("[RelationshipEditorPage.resetPageState]: IN");
		this.relationshipEditorPanel.removeAllRelations();
		Sbi.trace("[RelationshipEditorPage.resetPageState]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, init: function(){
		this.relationshipEditorPanel = Ext.create('Sbi.data.editor.relationship.RelationshipEditor',{usedDatasets: this.usedDatasets}); 
		return this.relationshipEditorPanel;
	}

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
