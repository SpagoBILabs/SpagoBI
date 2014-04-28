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


//===================================================================
//COMMONS
//===================================================================
Sbi.locale.ln['sbi.common.cancel'] = 'Cancel';
Sbi.locale.ln['sbi.common.close'] = 'Close';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Select';

//===================================================================
// TOOLBAR
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.mdx'] = 'Mdx Query';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Drill mode';
Sbi.locale.ln['sbi.olap.toolbar.undo'] = 'Undo';
Sbi.locale.ln['sbi.olap.toolbar.clean'] = 'Clean cache';
Sbi.locale.ln['sbi.olap.toolbar.showParentMembers'] = 'Show parent members';
Sbi.locale.ln['sbi.olap.toolbar.hideSpans'] = 'Hide spans';
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
