/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('Sbi.data.RelationshipEditorWizard', {
	extend: 'Ext.Window'
	, layout:'fit'
	, config:{title: LN('sbi.cockpit.relationship.editor.wizard.title')				   
			  , width: 1000
			  , height: 510
			  , closable: false
			  , plain: true
			  , modal: true	
	}
	

	/**
	 * @property {Sbi.data.RelationshipEditorWizardPanel} editorMainPanel
	 *  Container of the wizard panel
	 */
	, editorMainPanel: null

	, constructor : function(config) {
		Sbi.trace("[RelationshipEditorWizard.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.initEvents();
		this.callParent(arguments);
		Sbi.trace("[RelationshipEditorWizard.constructor]: OUT");
	}
	
	, initComponent: function() {
  
        Ext.apply(this, {
            items: [this.editorMainPanel]
        });
        
        this.callParent();
    }
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	, getRelationshipEditorPage: function() {
		return this.editorMainPanel.getRelationshipEditorPage();
	}
	

	, getWizardState: function() {
		return this.editorMainPanel.getWizardState();
	}

	, setWizardState: function(editorState) {
		Sbi.trace("[RelationshipEditorWizard.setWizardState]: IN");
		Sbi.trace("[RelationshipEditorWizard.setWizardState]: wizard new configuration is equal to [" + Sbi.toSource(editorState) + "]");
		this.editorMainPanel.setWizardState(editorState);
		Sbi.trace("[RelationshipEditorWizard.setWizardState]: OUT");
	}
	
	, resetWizardState: function() {
		Sbi.trace("[RelationshipEditorWizard.resetWizardState]: IN");
		this.editorMainPanel.resetWizardState();
		Sbi.trace("[RelationshipEditorWizard.resetWizardState]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, init: function(){
		Sbi.trace("[RelationshipEditorWizard.init]: IN");
		// ONLY FOR TEST
		this.usedDatasets = [];
		this.usedDatasets.push('Pernottamenti');
		this.usedDatasets.push('dsTestForAdmin');
		this.usedDatasets.push('POP_STATOCIVILE');
		//
		this.editorMainPanel = Ext.create('Sbi.data.RelationshipEditorWizardPanel',{
			usedDatasets: this.usedDatasets
		});
		this.editorMainPanel.on('cancel', this.onCancel, this);
		this.editorMainPanel.on('submit', this.onSubmit, this);
		
		Sbi.trace("[RelationshipEditorWizard.init]: OUT");
	}
	
	, initEvents: function() {
		this.addEvents(
			/**
			* @event indicatorsChanged
			* Fires when data inserted in the wizard is canceled by the user
			* @param {RelationshipEditorWizard} this
			*/
			'cancel'
			/**
			* @event apply
			* Fires when data inserted in the wizard is applied by the user
			* @param {RelationshipEditorWizard} this
			*/
			, 'apply'
			/**
			* @event submit
			* Fires when data inserted in the wizard is submitted by the user
			* @param {RelationshipEditorWizard} this
			*/
			, 'submit'
		);
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onCancel: function(){
		this.fireEvent("cancel", this);
	}
	
	, onApply: function(){
		this.fireEvent("apply", this);
	}
	
	, onSubmit: function(editorPanel){
		this.fireEvent("submit", this);
	}

});
