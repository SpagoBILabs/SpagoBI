/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.locale");

Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//MESSAGE BOX BUTTONS
//===================================================================
Ext.Msg.buttonText.yes = 'SÃ­';
Ext.Msg.buttonText.no = 'No';


//===================================================================
//MESSAGE GENERAL
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.yes'] = 'SÃ­';
Sbi.locale.ln['sbi.qbe.messagewin.no'] = 'No';
Sbi.locale.ln['sbi.qbe.messagewin.cancel'] = 'Cancelar';


//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Mensaje de advertencia';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Mensaje de error';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Mensaje de informaciÃ³n';


//===================================================================
//SESSION EXPIRED
//===================================================================
Sbi.locale.ln['sbi.qbe.sessionexpired.msg'] = 'La sesiÃ³n ha caducado. Intente volver a ejecutar el documento';
//===================================================================
//DATASTORE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.title'] = 'Resultados';

Sbi.locale.ln['sbi.qbe.datastorepanel.grid.displaymsg'] = 'Mostrando {0} - {1} de {2}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptymsg'] = 'No hay datos para mostrar';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptywarningmsg'] = 'La query no ha devuelto valores. Verificar si hay filtros por configurar.';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforeoverflow'] = 'LÃ­mite mÃ¡ximo nÃºmero de registros';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afteroverflow'] = 'excedido';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforepagetext'] = 'PÃ¡gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afterpagetext'] = 'de {0}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.firsttext'] = 'Primera PÃ¡gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.prevtext'] = 'PÃ¡gina anterior';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.nexttext'] = 'PÃ¡gina siguiente';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.lasttext'] = 'Ãºltima PÃ¡gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.refreshtext'] = 'Refrescar';

Sbi.locale.ln['sbi.qbe.datastorepanel.button.tt.exportto'] = 'Exportar a';


//===================================================================
//Sbi.worksheet.designer.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.title'] = 'Series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.emptymsg'] = 'Arrastre y suelte aquí algunas medidas de la consulta como series';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.queryfield'] = 'Campo';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.seriename'] = 'Nombre';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.color'] = 'Color';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.showcomma'] = 'Mostrar separador de agrupación';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.precision'] = 'Precisión';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.columns.suffix'] = 'Sufijo';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.title'] = 'Drop no permitido';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.measurealreadypresent'] = 'La medida ya está presente';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.attributes'] = 'No puede arrastrar dentro de las series del chart';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.cannotdrophere.postlinecalculated'] = 'No se puede utilizar campos calculados basados en scripts dentro de las series del chart';
Sbi.locale.ln['sbi.worksheet.designer.chartseriespanel.tools.tt.removeall'] = ['Remove all'];
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'suma';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'media';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'máximo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'mínimo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'count';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'count distinct';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Function';

//===================================================================
//Sbi.worksheet.designer.SeriesGroupingPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.seriesgroupingpanel.title'] = 'Variable de agrupación de las series';


//===================================================================
//Sbi.worksheet.designer.ChartCategoryPanel
//===================================================================
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.title'] = 'Categoría';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.emptymsg'] = 'Arrastre y suelte aquí un atributo de consulta como una categoría';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.title'] = 'Drop no permitido';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.unknownsource'] = 'Fuente desconocida';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.measures'] = 'No es posible soltar las medidas aquí';
Sbi.locale.ln['sbi.worksheet.designer.chartcategorypanel.cannotdrophere.postlinecalculated'] = 'No puedes soltar los campos calculados basados en script aquí';

//===================================================================
//Sbi.cockpit.widgets.table.AggregationChooserWindow
//===================================================================
Sbi.locale.ln['sbi.cockpit.aggregationwindow.title'] = 'Select aggregation function';
Sbi.locale.ln['sbi.cockpit.aggregationwindow.selectAggregation'] = 'Aggregation function';

//===================================================================
//Sbi.cockpit.widgets.table.QueryFieldsContainerPanel
//===================================================================

Sbi.locale.ln['Sbi.cockpit.widgets.table.QueryFieldsContainerPanel.infoTitle'] = 'Info';
Sbi.locale.ln['Sbi.cockpit.widgets.table.QueryFieldsContainerPanel.info'] = 'Define with mouse fields order of appearance. \n Double click on measure to define aggregation. \n Click cancel button to delete.';
