/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.data.editor.association.AssociationEditorPage', {
	  extend: 'Ext.Panel'
	, layout: 'fit'
			
	, config:{	
			storeList: null
		  , associationsList: null
		  , itemId: 0
		  , border: false
	}

	/**
	 * @property {Sbi.data.editor.association.AssociationEditor} associationEditorPanel
	 *  Container of the editor component
	 */
	 , associationEditorPanel: null
	
	 , constructor : function(config) {
		Sbi.trace("[AssociationEditorPage.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(arguments);
		Sbi.trace("[AssociationEditorPage.constructor]: OUT");
	 }
	
	 , initComponent: function() {
	     Ext.apply(this, {
	         items: [this.associationEditorPanel]
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
		Sbi.trace("[AssociationEditorPage.updateValues]: IN");
		
		Sbi.trace("[AssociationEditorPage.updateValues]: Input parameter values is equal to [" + Sbi.toSource(values) + "]");
		this.associationEditorPanel.controlPanel.updateValues(values);
		Sbi.trace("[AssociationEditorPage.updateValues]: OUT");
	}

	, getValidationErrorMessages: function() {
		Sbi.trace("[AssociationEditorPage.getValidationErrorMessage]: IN");
		var msg = null;

		// TODO check if the designer is properly defined
		
		Sbi.trace("[AssociationEditorPage.getValidationErrorMessage]: OUT");
		
		return msg;
	}
	
	, isValid: function() {
		Sbi.trace("[AssociationEditorPage.isValid]: IN");
	
		var isValid = this.getValidationErrorMessages() === null;
		
		Sbi.trace("[AssociationEditorPage.isValid]: OUT");
		
		return isValid;
	}

	, applyPageState: function(state) {
		Sbi.trace("[AssociationEditorPage.applyPageState]: IN");
		state =  state || {};
		if(this.associationEditorPanel) {
			state.associationsList = this.associationEditorPanel.getAssociationsList();
		}
		Sbi.trace("[AssociationEditorPage.applyPageState]: OUT");
		return state;
	}	

	, setPageState: function(state) {
		Sbi.trace("[AssociationEditorPage.setPageState]: IN");
		Sbi.trace("[AssociationEditorPage.setPageState]: state parameter is equal to [" + Sbi.toSource(state, true) + "]");
		
		this.associationEditorPanel.setAssociationsList(state);
		
		Sbi.trace("[AssociationEditorPage.setPageState]: OUT");
	}
	
	, resetPageState: function() {
		Sbi.trace("[AssociationEditorPage.resetPageState]: IN");
		this.associationEditorPanel.removeAllAssociations();
		Sbi.trace("[AssociationEditorPage.resetPageState]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, init: function(){
		this.associationEditorPanel = Ext.create('Sbi.data.editor.association.AssociationEditor',{storeList: this.storeList
																								, associationsList: this.associationsList}); 
		return this.associationEditorPanel;
	}

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
