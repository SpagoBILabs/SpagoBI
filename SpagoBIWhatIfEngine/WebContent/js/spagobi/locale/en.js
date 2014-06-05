Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

Sbi.locale.formats = {
		/*
		number: {
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		*/
		
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


//===================================================================
//HELP
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.help.content'] = 'Select the visible members for the selected dimanesion. These members will be included in the select staement of the MDX query';
Sbi.locale.ln['sbi.olap.help.title'] = 'Help';
Sbi.locale.ln['sbi.olap.execution.table.dimension.cannotchangehierarchy'] = 'You cannot change the hierarchy since there are some pending modifications on the data; you must firstly persist the modifications.';

//===================================================================
//COMMONS
//===================================================================
Sbi.locale.ln['sbi.common.cancel'] = 'Cancel';
Sbi.locale.ln['sbi.common.close'] = 'Close';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Select';
Sbi.locale.ln['sbi.common.warning'] = 'Warning';

//===================================================================
// TOOLBAR
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.mdx'] = 'Mdx Query';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Drill mode';
Sbi.locale.ln['sbi.olap.toolbar.undo'] = 'Undo';
Sbi.locale.ln['sbi.olap.toolbar.clean'] = 'Clear_cache';
Sbi.locale.ln['sbi.olap.toolbar.showParentMembers'] = 'Show parent members';
Sbi.locale.ln['sbi.olap.toolbar.hideSpans'] = 'Hide spans';
Sbi.locale.ln['sbi.olap.toolbar.showProperties'] = 'Show properties';
Sbi.locale.ln['sbi.olap.toolbar.suppressEmpty'] = 'Suppress empty rows/columns';
Sbi.locale.ln['sbi.olap.toolbar.save'] = 'Save';
Sbi.locale.ln['sbi.olap.toolbar.save.new'] = "Save as new version";
Sbi.locale.ln['sbi.olap.toolbar.lock'] = "Lock model";
Sbi.locale.ln['sbi.olap.toolbar.unlock'] = "Unlock model";
Sbi.locale.ln['sbi.olap.toolbar.lock_other'] = "Model locked by other user";
//===================================================================
//FILTERS
//===================================================================

Sbi.locale.ln['sbi.olap.execution.table.filter.collapse'] = 'Collapse all';
Sbi.locale.ln['sbi.olap.execution.table.filter.expand'] = 'Expand all';
Sbi.locale.ln['sbi.olap.execution.table.filter.filter.title'] = 'Select a slicer';
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.title'] = 'Select the visible members';
Sbi.locale.ln['sbi.olap.execution.table.filter.no.measure'] = 'A measure can not be used as a filter';
Sbi.locale.ln['sbi.olap.execution.table.filter.empty'] = 'Drag the member here if you want to use it as a slicer';


//===================================================================
//DIMENSIONS
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy'] = 'The selected hierarchy is ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy.2'] = ' You can chage it acting on the form below.';
Sbi.locale.ln['sbi.olap.execution.table.dimension.available.hierarchies'] = 'Available hierarchies: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.no.enough'] = 'There must be at least one dimension in the columns and in the rows';

//===================================================================
//WRITEBACK
//===================================================================
Sbi.locale.ln['sbi.olap.weiteback.persist.error'] = 'Error persisting the transformations';
Sbi.locale.ln['sbi.olap.weiteback.edit.no.zero'] =  'With the proportional algorithm it\'s not possible to edit a cell with the value 0. This algorithm preserves the weight between siblings cells and, editing the 0 value, this constraint will be broken. In the next release we\'ll provide more propagation algorithms.';

//===================================================================
//LOCK
//===================================================================
Sbi.locale.ln['sbi.olap.artifact.lock.error'] = 'The lock operation did not work, the model is still unlocked';
Sbi.locale.ln['sbi.olap.artifact.unlock.error'] = 'The unlock operation did not work, the model is still locked';
Sbi.locale.ln['sbi.olap.artifact.unlock.errorOther'] = 'The unlock operation did not work, the model is locked by user ';

//===================================================================
//MENU
//===================================================================
Sbi.locale.ln['sbi.olap.execution.menu.buttonMenu'] = 'Buttons Menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToMenu'] = 'Add to menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToToolbar'] = 'Add to toolbar';

//===================================================================
//VERSION MANAGER
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all'] = 'You can\'t delete all the versions';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current'] = 'You can\'t delete the current versions';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.title'] = 'Select the version to delete';>>>>>>> .r22906
