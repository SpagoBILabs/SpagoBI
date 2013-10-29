/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.ns("Sbi.settings");

/**
 * Execution  panel settings
 */
Sbi.settings.execution = {
		parametersPanel: {
			columnNo: 1
			, mandatoryFieldAdditionalString: '*' // a String that will be added in the label of the mandatory fields
			, columnWidth: 290
			, labelAlign: 'left'
			, fieldWidth: 180	
			, maskOnRender: false
			, fieldLabelWidth: 100
			, moveInMementoUsingCtrlKey: false
			, width: 295
			, viewportWindowWidth: 500
			, viewportWindowHeight: 500
		}

		, shortcutsPanel: {
			panelsOrder: {
				subobjects: 1
				, snapshots: 2
			}
			, height: 205
		}
		
		, toolbar:{
			hideForEngineLabels:[]	//list of engines without toolbar 
		}
};

/**
 * Document browser settings
 */
Sbi.settings.browser = {
		mexport: {
			massiveExportWizard: {
				resizable: true
			}
			, massiveExportWizardOptionsPage: {
				
			}, massiveExportWizardParametersPage: {
				
			}
			, massiveExportWizardTriggerPage: {
				showJobDetails: false
			}
		}
	  , showLeftPanels: true
	  , showBreadCrumbs: true
//	  , maxNumberOfExecutionTabs: 1 	 //the maximum number of tabs to open on execution of documents if valorized
	  , typeLayout: 'tab'				 //possible values: 'tab' or 'card'
	  , showTitle: true 
	  , showCreateButton: true
} 

Sbi.settings.invisibleParameters = {
	remove : true
};

/**
 * KPI
 */
Sbi.settings.kpi = {
		goalModelInstanceTreeUI: {
			goalCustom: false
		}
};


/**
 * WIDGETS
 */
Sbi.settings.widgets = {
		TreeLookUpField : {
			//true to allow the selection of the internal node of the tree driver
			//false to allow the selection only foe the leafs
			allowInternalNodeSelection: true
		}
		//Details for specific file upload management (ex: img for document preview,...)
	  , FileUploadPanel: {
			imgUpload: {
				maxSizeFile: 10485760
			  , directory: '/preview/images' //starting from /resources directory
			  , extFiles: ['BMP', 'IMG', 'JPG', 'PNG', 'GIF']
			}
		}
};

// Specific IE settings
Ext.ns("Sbi.settings.IE");

// Workaround: on IE, it takes a long time to destroy the stacked execution wizards.
// If the Sbi.settings.IE.destroyExecutionWizardWhenClosed is false, stacked execution wizards are not destroyed but only hidden;
// if the Sbi.settings.IE.destroyExecutionWizardWhenClosed is true, stacked execution wizards are destroyed instead (this may cause the IE 
// warning message "A script on this page is causing Internet Explorer to run slowly")
Sbi.settings.IE.destroyExecutionWizardWhenClosed = false;



