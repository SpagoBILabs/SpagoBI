/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.data.RelationshipEditorWizardPanel', {
		extend: 'Sbi.widgets.WizardPanel'
	
	, config:{ 
		/**
	     * @property {String[]} usedDatasets
	     * The list of the labels of used datasets. It is used only for the initialization of #datasetsBrowserPage
	     * After the inizialization it is removed. In order to get this list after initialization
	     * use #getDatasetBrowserPage method as shown in the following example:
	     * <pre><code>wizardPanel.getDatasetBrowserPage().getUsedDatasets();</code></pre>
	     */
		usedDatasets: null
	  , associationsList: null
	  , frame: false
	  , border: false
	}

	/**
	 * @property {Sbi.cockpit.editor.relationship.RelationshipEditorPage} relationshipEditorPage
	 * The page that manages relationship editing
	 */
	, relationshipEditorPage: null
	
	, constructor : function(config) {
		Sbi.trace("[RelationshipEditorWizardPanel.constructor]: IN");
		this.initConfig(config);
		this.callParent(config);
		Sbi.trace("[RelationshipEditorWizardPanel.constructor]: OUT");
	}

	
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	, getRelationshipEditorPage: function() {
		return this.relationshipEditorPage;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, initPages: function(){
		Sbi.trace("[RelationshipEditorWizardPanel.initPages]: IN");
		
		this.pages = new Array();
		
		this.initRelationshipEditorPage();
		this.pages.push(this.relationshipEditorPage);

		Sbi.trace("[RelationshipEditorWizardPanel.initPages]: relationship editor page succesfully adedd");
		
		Sbi.trace("[RelationshipEditorWizardPanel.initPages]: OUT");

		return this.pages;
	}
		
	, initButtons: function(){
		Sbi.trace("[RelationshipEditorWizardPanel.initButtons]: IN");
		
		this.buttons = new Array();
		
		this.buttons.push('->');
		
		this.buttons.push({
			id: 'submit'
			, hidden: false
	        , text:  LN('sbi.ds.wizard.confirm')
	        , handler: this.onSubmit
	        , scope: this
//	        , disabled: (this.activeItem == 0)?false:true
//	        , disabled: true
	    });
		
		this.buttons.push({
			id: 'cancel'
	        , text:  LN('sbi.ds.wizard.cancel')
	        , handler: this.onCancel
	        , scope: this
	    });
		
		Sbi.trace("[RelationshipEditorWizardPanel.initButtons]: relationship editor buttons succesfully adedd");
		
		Sbi.trace("[RelationshipEditorWizardPanel.initButtons]: OUT");
		
		return this.buttons;
	}
	
	, initRelationshipEditorPage: function() {
		Sbi.trace("[RelationshipEditorWizardPanel.initRelationshipEditorPage]: IN");
		this.relationshipEditorPage = Ext.create('Sbi.data.editor.relationship.RelationshipEditorPage',{
			usedDatasets: this.usedDatasets
		  , associationsList: this.associationsList
		});
		Sbi.trace("[RelationshipEditorWizardPanel.initRelationshipEditorPage]: IN");
		return this.relationshipEditorPage;
	}
	

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
