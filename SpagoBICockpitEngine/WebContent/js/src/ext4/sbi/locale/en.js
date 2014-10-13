/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

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

//=====================================================================================================
//GENERIC
//=====================================================================================================

Sbi.locale.ln['sbi.generic.author'] = 'Author';
Sbi.locale.ln['sbi.generic.creationdate'] = 'Creation date';
Sbi.locale.ln['sbi.generic.owner']= 'Autor';
Sbi.locale.ln['sbi.generic.name']= 'Name';
Sbi.locale.ln['sbi.generic.save'] = 'Save ';
Sbi.locale.ln['sbi.generic.saveAndGoBack'] = 'Save and return ';
Sbi.locale.ln['sbi.generic.cancel'] = 'Cancel';
Sbi.locale.ln['sbi.generic.resultMsg'] = 'Operation succeded';

//===================================================================
//WIZARD
//===================================================================
Sbi.locale.ln['sbi.ds.wizard.general']= 'Save the dataset';
Sbi.locale.ln['sbi.ds.wizard.detail']= 'Load file';
Sbi.locale.ln['sbi.ds.wizard.metadata']= 'Definition data';
Sbi.locale.ln['sbi.ds.wizard.validation']= 'Validation data';
Sbi.locale.ln['sbi.ds.wizard.back']= '< Back';
Sbi.locale.ln['sbi.ds.wizard.next']= 'Next >';
Sbi.locale.ln['sbi.ds.wizard.confirm']= 'Confirm';
Sbi.locale.ln['sbi.ds.wizard.cancel']= 'Cancel';
Sbi.locale.ln['sbi.ds.wizard.close']= 'Close';
Sbi.locale.ln['sbi.ds.wizard.startMsg']= 'Select and load your file XLS or CSV ...';
Sbi.locale.ln['sbi.ds.wizard.file']= 'File';
Sbi.locale.ln['sbi.ds.wizard.successLoad']= ' loaded succesfully!';
Sbi.locale.ln['sbi.ds.wizard.selectedFile']='Selected file ';
Sbi.locale.ln['sbi.ds.wizard.loadedFile']='Loaded file ';
Sbi.locale.ln['sbi.ds.wizard.selectFile']='Select file';

Sbi.locale.ln['sbi.ds.orderComboLabel']= 'Order by...';
Sbi.locale.ln['sbi.ds.filterLabel']= 'Filter by ...';
Sbi.locale.ln['sbi.ds.moreRecent']= 'Recent';


//===================================================================
//BROWSER
//===================================================================
Sbi.locale.ln['sbi.browser.document.searchDatasets']  = 'Search for datasets';
Sbi.locale.ln['sbi.browser.document.searchKeyword']  = 'Search by keyword...';

//===================================================================
//MYDATA
//===================================================================
Sbi.locale.ln['sbi.mydata.useddataset'] = "Used Data Set";
Sbi.locale.ln['sbi.mydata.mydataset'] = "My Data Set";
Sbi.locale.ln['sbi.mydata.enterprisedataset'] = "Enterprise Data Set";
Sbi.locale.ln['sbi.mydata.shareddataset'] = "Shared Data Set";
Sbi.locale.ln['sbi.mydata.alldataset'] = "All Data Set";
Sbi.locale.ln['sbi.mydata.sharedataset'] = "Share Data Set";
Sbi.locale.ln['sbi.mydata.unsharedataset'] = "Unshare Data Set";

//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.savewin.title'] = 'Insert more details and save your document... ';
Sbi.locale.ln['sbi.savewin.name'] = 'Name';
Sbi.locale.ln['sbi.savewin.description'] = 'Description';
Sbi.locale.ln['sbi.savewin.previewfile'] = 'Preview file';
Sbi.locale.ln['sbi.savewin.saveWarning']  = 'Insert the name before save document';

//===================================================================
//COCKPIT
//===================================================================

Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorcustomconfpanel.emptymsg'] = 'Drag & drop here a widget from the palette';
Sbi.locale.ln['sbi.cockpit.core.WidgetDesigner.title'] = 'Flat table designer';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields.emptymsg'] = 'Visible fields';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields'] = 'Drag & drop here attribute as the series\' that you want view in the table';
Sbi.locale.ln['sbi.cockpit.queryfieldspanel.title'] = 'Selected fields';


//===================================================================
//Sbi.cockpit.widgets.ChartCategoryPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.title'] = 'Category';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.emptymsg'] = 'Drag & drop here a query attribute as a category';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.title'] = 'Drop not allowed';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.unknownsource'] = 'Unknown source';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.measures'] = 'You cannot drop measures here';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.postlinecalculated'] = 'You cannot drop script-based calculated fields here';

//===================================================================
//Sbi.cockpit.widgets.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.title'] = 'Series';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.emptymsg'] = 'Drag & drop here some query measures as series';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.queryfield'] = 'Field';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.seriename'] = 'Name';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.color'] = 'Color';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.showcomma'] = 'Show grouping separator';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.precision'] = 'Precision';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.suffix'] = 'Suffix';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.title'] = 'Drop not allowed';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.measurealreadypresent'] = 'The measure is already present';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.attributes'] = 'You cannot drag attributes into the chart\'s series';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.postlinecalculated'] = 'You cannot use script-based calculated fields into the chart\'s series';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.tools.tt.removeall'] = ['Remove all'];

//===================================================================
//Sbi.cockpit.widgets.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.seriesgroupingpanel.emptymsg'] = 'Drag & drop here a query attribute as the series\' grouping variable';
Sbi.locale.ln['sbi.cockpit.widgets.seriesgroupingpanel.title'] = 'Series\' grouping variable';


//column headers
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.visible'] = 'Visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.include'] = 'Include';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.filter'] = 'Filter';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.having'] = 'Group Filter';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.entity'] = 'Entity';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.alias'] = 'Alias';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.order'] = 'Order';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Function';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.field'] = 'Field';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete'] = 'Delete all';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete.column'] = 'Delete';

//aggregation functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'none';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'sum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'averege';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'maximum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'minimum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'count';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'count distinct';

Sbi.locale.ln['sbi.cockpit.mainpanel.btn.selections'] = 'Selections';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.clearselections'] = 'Clear selections';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.parameters'] = 'Parameters';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.associations'] = 'Associations';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.addWidget'] = 'Add Widget';


//ASSOCIATIONS
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.title'] = 'Associations editor';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.ds.columnColumn'] = 'Column';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.ds.columnType'] = 'Type';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.columnId'] = 'Identifier';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.columnAssociation'] = 'Association';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.title'] = 'Associations List';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.add'] = 'Add';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.add.tooltip'] = 'Add Association';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.modify'] = 'Modify';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.modify.tooltip'] = 'Modify Association';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.delete'] = 'Delete';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.delete.tooltip'] = 'Delete Association';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.autodetect'] = 'Auto Detect';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.autodetect.tooltip'] = 'Auto Detect Association';

Sbi.locale.ln['sbi.cockpit.association.editor.msg.modify'] = 'Please select the association that you want modify from the list!';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.duplicate'] = 'Association already exists!';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.differentType'] ='Not all fields type are consistent. Do you want to continue with the addition of the association?';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.selectFields'] ='Please select fields for the association';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.confirmDelete'] ='Confirm delete of association ';

//FILTERS
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.title'] = 'Filters editor';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.nameObj'] = 'Object Reference';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.typeObj'] = 'Type';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.nameObj'] = 'Parameter Name';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.typeObj'] = 'Parameter Type';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.initialValue'] = 'Initial Value';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.scope'] = 'Scope';

//SELECTIONS
Sbi.locale.ln['sbi.cockpit.core.selections.title'] = 'Active selections list';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnAssociation'] = 'Association';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnWidget'] = 'Widget';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnField'] = 'Selection field';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnValues'] = 'Values';
Sbi.locale.ln['sbi.cockpit.core.selections.list.items'] = 'Items';


//===================================================================
//Sbi.cockpit.core.WidgetContainerComponent
//===================================================================
Sbi.locale.ln['sbi.cockpit.window.toolbar.editor'] = 'Edit';
Sbi.locale.ln['sbi.cockpit.window.toolbar.refresh'] = 'Refresh';
Sbi.locale.ln['sbi.cockpit.window.toolbar.clone'] = 'Clone';


//===================================================================
//Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorgenericconfpanel.incomingeventsenabled.label'] = 'Updateable';
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorgenericconfpanel.incomingeventsenabled.description'] = 'If checked, the widget will update on user interactions with other widgets. Be aware that, if you change this setting on an existing document, you need to save it and re-execute it in order to apply changes.';
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorgenericconfpanel.outcomingeventsenabled.label'] = 'Clickable';
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorgenericconfpanel.outcomingeventsenabled.description'] = 'If checked, you\'ll be able to interact with the widget directly by clicking on its items. Be aware that, if you change this setting on an existing document, you need to save it and re-execute it in order to apply changes.';