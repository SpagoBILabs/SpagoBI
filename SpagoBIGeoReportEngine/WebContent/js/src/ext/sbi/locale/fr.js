/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

//once all the labels have been translated remove the following block

//< block START
Sbi.locale.unsupportedmsg = 'Sorry, french localization is not yet supported by SpagoBIGeoReportEngine.'
	+ 'In order to add fench localization you can modify properly the file ' 
	+ 'SpagoBIGeoReportEngine/js/spagobi/locale/fr.js translating text from english to french.'
	+ 'Once done please contribute it back to the project.';

Ext.Msg.show({
	   title:'Unimplemented functionality',
	   msg: Sbi.locale.unsupportedmsg,
	   buttons: Ext.Msg.OK,
	   icon: Ext.MessageBox.INFO
});
//block END >

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
// CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Navigation';
Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Layers';

Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'Analysis';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'Add indicators';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indicator';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 	'Select an indicator';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'Method';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'Number of classes';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'From color';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'To color';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'Set Default';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'Default values correctly set';

Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'L\u00E9gende';
Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Change style'; 
Sbi.locale.ln['sbi.geo.earthpanel.title'] = 'Navigation 3D';

//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.savewin.title'] = 'Insert more details and save your document... ';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.name'] = 'Name';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.description'] = 'Description';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.visibility'] = 'Document visibility';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.previewfile'] = 'Preview file';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.community'] = 'Community';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.scope'] = 'Scope';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.saveWarning']  = 'Before save document, is necessary to insert the name of the map and select almost a folder.';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = ' ';

//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Carte';

//===================================================================
//OPENLAYERS
//===================================================================
Sbi.locale.ln['mf.print.mapTitle'] =  'Title';
Sbi.locale.ln['mf.print.comment'] =  'Comments';
Sbi.locale.ln['mf.print.loadingConfig'] =  'Loading the configuration...';
Sbi.locale.ln['mf.print.serverDown'] =  'The print service is not working';
Sbi.locale.ln['mf.print.unableToPrint'] =  "Unable to print";
Sbi.locale.ln['mf.print.generatingPDF'] =  "Generating PDF...";
Sbi.locale.ln['mf.print.dpi'] =  'DPI';
Sbi.locale.ln['mf.print.scale'] =  'Scale';
Sbi.locale.ln['mf.print.rotation'] =  'Rotation';
Sbi.locale.ln['mf.print.print'] =  'Print';
Sbi.locale.ln['mf.print.resetPos'] =  'Reset Pos.';
Sbi.locale.ln['mf.print.layout'] =  'Layout';
Sbi.locale.ln['mf.print.addPage'] =  'Add page';
Sbi.locale.ln['mf.print.remove'] =  'Remove page';
Sbi.locale.ln['mf.print.clearAll'] =  'Clear all';
Sbi.locale.ln['mf.print.popupBlocked'] =  'Popup windows are blocked by your browser.<br />' +
                       '<br />Use this url to download your document] = ';
Sbi.locale.ln['mf.print.noPage'] =  'No page selected; click on the "Add page" button to add one.';
Sbi.locale.ln['mf.error'] =  'Error';
Sbi.locale.ln['mf.warning'] =  'Warning';
Sbi.locale.ln['mf.information'] =  'Information';
Sbi.locale.ln['sbi.tools.catalogue.measures.measure.properties'] = 'Measure Properties';
Sbi.locale.ln['sbi.tools.catalogue.measures.dataset.properties'] = 'Data Set Properties';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Join';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Execute join between the selected measures';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.tooltip'] = 'Open the selection frame';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.btn'] = 'Selection';
Sbi.locale.ln['sbi.tools.catalogue.measures.window.title'] =  'Measures Catalogue';
Sbi.locale.ln['error.mesage.description.measure.join.no.common.dimension'] = 'Impossible to execute the join between measures. The associated datasets haven\'t any dimension in common.';
Sbi.locale.ln['error.mesage.description.measure.join.no.complete.common.dimension'] = 'Impossible to execute the join between measures. The associated datasets haven\'t any complete dimension in common.';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.alias'] = 'Alias';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsName'] = 'Name';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsLabel'] = 'Label';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsCategory'] = 'Category';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsType'] = 'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.label'] = 'Label';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.descr'] = 'Description';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.type'] = 'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.baseLayer'] = 'Base Layer';

