Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

Sbi.locale.formats = {

		float: {
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		int: {
			decimalSeparator: '.',
			decimalPrecision: 0,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},

		string: {
			trim: true,
    		maxLength: null,
    		ellipsis: true,
    		changeCase: null, // null | 'capitalize' | 'uppercase' | 'lowercase'
    		//prefix: '',
    		//suffix: '',
    		nullValue: ''
		},

		date: {
			dateFormat: 'm/Y/d',
    		nullValue: ''
		},

		boolean: {
			trueSymbol: 'true',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};


// == == == == == == == == == == == == == == == == == == == == == == == == ==
//HELP
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.help.content'] = 'Select the visible members for the selected dimanesion. These members will be included in the select staement of the MDX query';
Sbi.locale.ln['sbi.olap.help.title'] = 'Help';
Sbi.locale.ln['sbi.olap.execution.table.dimension.cannotchangehierarchy'] = 'You cannot change the hierarchy since there are some pending modifications on the data; you must firstly persist the modifications.';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//COMMONS
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.common.cancel'] = 'Cancel';
Sbi.locale.ln['sbi.common.close'] = 'Close';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Select';
Sbi.locale.ln['sbi.common.warning'] = 'Warning';
Sbi.locale.ln['sbi.common.next'] = 'Next';
Sbi.locale.ln['sbi.common.prev'] = 'Back';
Sbi.locale.ln['sbi.common.wait'] = 'Please wait...';
Sbi.locale.ln['sbi.common.wait.long'] = 'This operation can take some minutes. Please wait...';
Sbi.locale.ln['sbi.common.name'] = 'Name';
Sbi.locale.ln['sbi.common.description'] = 'Description';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
// TOOLBAR
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_MDX'] = 'Mdx Query';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Drill mode';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_UNDO'] = 'Undo';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FLUSH_CACHE'] = 'Reload model';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FATHER_MEMBERS'] = 'Show parent members';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_SPANS'] = 'Hide spans';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SHOW_PROPERTIES'] = 'Show properties';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_EMPTY'] = 'Suppress empty rows/columns';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE'] = 'Save';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE_NEW'] = "Save as new version";
Sbi.locale.ln['sbi.olap.toolbar.lock'] = "Lock model";
Sbi.locale.ln['sbi.olap.toolbar.unlock'] = "Unlock model";
Sbi.locale.ln['sbi.olap.toolbar.lock_other'] = "Model locked by other user";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_VERSION_MANAGER'] = "Delete versions";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_EXPORT_OUTPUT'] = "Output wizard";
Sbi.locale.ln['sbi.olap.toolbar.save.as.description'] = "Type the name of the new version. If you leave it blank the name and the description assume the version number";
Sbi.locale.ln['sbi.olap.toolbar.save.as.version.name'] = "Version name";
Sbi.locale.ln['sbi.olap.toolbar.save.as.version.description'] = "Version description";

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//FILTERS
// == == == == == == == == == == == == == == == == == == == == == == == == ==

Sbi.locale.ln['sbi.olap.execution.table.filter.collapse'] = 'Collapse all';
Sbi.locale.ln['sbi.olap.execution.table.filter.expand'] = 'Expand all';
Sbi.locale.ln['sbi.olap.execution.table.filter.filter.title'] = 'Select a slicer';
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.title'] = 'Select the visible members';
Sbi.locale.ln['sbi.olap.execution.table.filter.no.measure'] = 'A measure can not be used as a filter';
Sbi.locale.ln['sbi.olap.execution.table.filter.empty'] = 'Drag the member here if you want to use it as a slicer';


// == == == == == == == == == == == == == == == == == == == == == == == == ==
//DIMENSIONS
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy'] = 'The selected hierarchy is ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy.2'] = ' You can chage it acting on the form below.';
Sbi.locale.ln['sbi.olap.execution.table.dimension.available.hierarchies'] = 'Available hierarchies: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.no.enough'] = 'There must be at least one dimension in the columns and in the rows';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//WRITEBACK
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.writeback.persist.error'] = 'Error saving the modifications';
Sbi.locale.ln['sbi.olap.writeback.edit.no.zero'] =  'It\'s not possible to edit this cell!! The current propagation algorithm preserves the weight between siblings cells; when editing a blank or zero-value cell, this constraint will be broken on children cells. In the next release we\'ll provide more propagation algorithms.';
Sbi.locale.ln['sbi.olap.writeback.edit.no.locked'] =  'It\'s not possible to edit a model if you have not set a lock on it.';
Sbi.locale.ln['sbi.olap.writeback.edit.lock.export.output'] =  'It\'s not possible to edit a model while an export process is executing.';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//LOCK
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.artifact.lock.error'] = 'The lock operation did not work, the model is still unlocked';
Sbi.locale.ln['sbi.olap.artifact.unlock.error'] = 'The unlock operation did not work, the model is still locked';
Sbi.locale.ln['sbi.olap.artifact.unlock.errorOther'] = 'The unlock operation did not work, the model is locked by user ';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//MENU
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.execution.menu.buttonMenu'] = 'Buttons Menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToMenu'] = 'Add to menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToToolbar'] = 'Add to toolbar';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//VERSION MANAGER
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all'] = 'You can\'t delete all the versions';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current'] = 'You can\'t delete the current versions';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.title'] = 'Select the version to delete';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.select.warning'] = 'Warning: this operation could take some minutes.';
Sbi.locale.ln['sbi.olap.control.controller.delete.version.ok'] = 'Versions correctly deleted.';

// == == == == == == == == == == == == == == == == == == == == == == == == ==
//OUTPUT
// == == == == == == == == == == == == == == == == == == == == == == == == ==
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.name'] = "Table name";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.description'] = "What is the name of the table for the output process?";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.row.delimiter'] = "Row delimiter";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.filter.delimiter'] = "Field delimiter";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.description'] = "Please fill the CSV options";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table'] = "table";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv'] = "file";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type'] = "Output type";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.description'] = "Select the output type for the analysis. This operation will take some minutes. In the meanwile you can continue to work on your cube";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.title'] = "Output wizard";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.version'] = "Version to export";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.ok'] = "Analysis exported";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.error'] = "An error occurred  exporting the data";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.csv.window'] = "<h1>Export the output in CSV</h1><p>This operation can take some minutes. You can continue to work on your cube</p>";



