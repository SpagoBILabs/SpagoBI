/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

//===================================================================
//MESSAGE BOX BUTTONS
//===================================================================
Ext.Msg.buttonText.yes = 'Yes'; 
Ext.Msg.buttonText.no = 'No';


//===================================================================
//MESSAGE GENERAL
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.yes'] = 'Yes';
Sbi.locale.ln['sbi.qbe.messagewin.no'] = 'No';
Sbi.locale.ln['sbi.qbe.messagewin.cancel'] = 'Cancel';

Sbi.locale.ln['sbi.generic.label'] = 'Label';
Sbi.locale.ln['sbi.generic.name'] = 'Name';
Sbi.locale.ln['sbi.generic.descr'] = 'Description';
Sbi.locale.ln['sbi.generic.scope'] = 'Scope';
Sbi.locale.ln['sbi.generic.scope.private'] = 'Private';
Sbi.locale.ln['sbi.generic.scope.public'] = 'Public';
Sbi.locale.ln['sbi.generic.actions.save'] = 'Save';

Sbi.locale.ln['sbi.generic.wait'] = "Please wait...";
Sbi.locale.ln['sbi.generic.error'] = "Error";
Sbi.locale.ln['sbi.generic.success'] = "Success";
Sbi.locale.ln['sbi.generic.operationSucceded'] = "Operation succeded";
Sbi.locale.ln['sbi.generic.query.SQL'] = "SQL Query";
Sbi.locale.ln['sbi.generic.query.JPQL'] = "JPLQ Query";

//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Warning message';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Error message';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Info message';


//===================================================================
//SESSION EXPIRED
//===================================================================
Sbi.locale.ln['sbi.qbe.sessionexpired.msg'] = 'Session has expired. Try re-executing the document';


//===================================================================
//DATASTORE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.title'] = 'Results';

Sbi.locale.ln['sbi.qbe.datastorepanel.grid.displaymsg'] = 'Displaying {0} - {1} of {2}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptymsg'] = 'No data to display';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptywarningmsg'] = 'Query returns no data. Check if there are some filters to define.';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforeoverflow'] = 'Max records number limit';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afteroverflow'] = 'exceeded';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforepagetext'] = 'Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afterpagetext'] = 'of {0}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.firsttext'] = 'First Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.prevtext'] = 'Previous Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.nexttext'] = 'Next Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.lasttext'] = 'Next Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.refreshtext'] = 'Refresh';

Sbi.locale.ln['sbi.qbe.datastorepanel.button.tt.exportto'] = 'Export to';

Sbi.locale.ln['sbi.qbe.savedatasetwindow.title'] = "Fill in the dataset details and click on Save";

//===================================================================
//Sbi.worksheet.designer.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.title'] = 'Series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.emptymsg'] = 'Drag & drop here some query measures as series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.queryfield'] = 'Field';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.seriename'] = 'Name';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.color'] = 'Color';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.showcomma'] = 'Show grouping separator';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.precision'] = 'Precision';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.suffix'] = 'Suffix';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.title'] = 'Drop not allowed';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.measurealreadypresent'] = 'The measure is already present';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.attributes'] = 'You cannot drag attributes into the chart\'s series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.postlinecalculated'] = 'You cannot use script-based calculated fields into the chart\'s series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.tools.tt.removeall'] = ['Remove all'];
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'none';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'sum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'averege';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'maximum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'minimum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'count';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'count distinct';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Function';


//===================================================================
//Sbi.worksheet.designer.SeriesGroupingPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.seriesgroupingpanel.title'] = 'Series\' grouping variable';

//===================================================================
//Sbi.worksheet.designer.ChartCategoryPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.title'] = 'Category';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.emptymsg'] = 'Drag & drop here a query attribute as a category';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.title'] = 'Drop not allowed';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.unknownsource'] = 'Unknown source';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.measures'] = 'You cannot drop measures here';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.postlinecalculated'] = 'You cannot drop script-based calculated fields here';

