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
		}

		, shortcutsPanel: {
			panelsOrder: {
				subobjects: 1
				, viewpoints: 2
				, snapshots: 3
			}
			, height: 205
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




// Specific IE settings
Ext.ns("Sbi.settings.IE");

// Workaround: on IE, it takes a long time to destroy the stacked execution wizards.
// If the Sbi.settings.IE.destroyExecutionWizardWhenClosed is false, stacked execution wizards are not destroyed but only hidden;
// if the Sbi.settings.IE.destroyExecutionWizardWhenClosed is true, stacked execution wizards are destroyed instead (this may cause the IE 
// warning message "A script on this page is causing Internet Explorer to run slowly")
Sbi.settings.IE.destroyExecutionWizardWhenClosed = false;
