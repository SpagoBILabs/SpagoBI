Ext.ns("Sbi.settings");

Sbi.settings.execution = {
		parametersPanel: {
			columnNo: 3
			, columnWidth: 325
			, labelAlign: 'left'
			, fieldWidth: 190	
			, maskOnRender: false
			, fieldLabelWidth: 100
			, moveInMementoUsingCtrlKey: false
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
