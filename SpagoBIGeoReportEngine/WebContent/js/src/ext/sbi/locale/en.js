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

//===================================================================
//LABELS
//===================================================================


Sbi.locale.ln['sbi.dataset.no.visible'] = 'Sorry but it is imposible to execute document [%1] because the dataset [%0] associted to it is not visible anymore';

//===================================================================
//GENERIC
//===================================================================
Sbi.locale.ln['sbi.generic.add'] = 'Add indicators from catalogue';//'Add';
Sbi.locale.ln['sbi.generic.select'] = 'Select indicators from catalogue';//'Select';
Sbi.locale.ln['sbi.generic.delete'] = 'Delete';
Sbi.locale.ln['sbi.generic.cancel'] = 'Cancel';
Sbi.locale.ln['sbi.generic.modify'] = 'Modify';
Sbi.locale.ln['sbi.generic.save'] = 'Save ';
Sbi.locale.ln['sbi.generic.newmap'] = 'new map';
Sbi.locale.ln['sbi.generic.savenewmap'] = 'Save new map';
Sbi.locale.ln['sbi.generic.wait'] = 'Please wait...';
Sbi.locale.ln['sbi.generic.info'] = 'Info';
Sbi.locale.ln['sbi.generic.error'] = 'Error';
Sbi.locale.ln['sbi.generic.error.msg'] = 'Operation failed';
Sbi.locale.ln['sbi.generic.ok'] = 'Information';
Sbi.locale.ln['sbi.generic.ok.msg'] = 'Operation succesfully ended';
Sbi.locale.ln['sbi.generic.resultMsg'] = 'Operation succeded';
Sbi.locale.ln['sbi.generic.result'] = 'Updates saved';
Sbi.locale.ln['sbi.generic.serviceError'] = 'Service Error';
Sbi.locale.ln['sbi.generic.serviceResponseEmpty'] = 'Server response is empty';
Sbi.locale.ln['sbi.generic.savingItemError'] = 'Error while saving item';
Sbi.locale.ln['not-enabled-to-call-service'] = 'The user is not allowed to do this operation';
Sbi.locale.ln['sbi.generic.deletingItemError'] = 'Error while deleting item. Control eventual items to which it is associated and then try to delete it again!';
Sbi.locale.ln['sbi.generic.warning'] = 'Warning';
Sbi.locale.ln['sbi.generic.pleaseConfirm'] = 'Please confirm';


//===================================================================
// CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Control Panel';
Sbi.locale.ln['sbi.geo.controlpanel.defaultname'] = 'New Map name...';
Sbi.locale.ln['sbi.geo.controlpanel.defaultdescr'] = 'New Map description...';
Sbi.locale.ln['sbi.geo.controlpanel.publishedby'] = 'Published by ';
Sbi.locale.ln['sbi.geo.controlpanel.sendfeedback'] = ' Send feedback ';
Sbi.locale.ln['sbi.geo.controlpanel.indicators'] = ' Indicators ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionlabel'] = 'This map is: ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionprivate'] = 'Private ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionpublic'] = 'Public ';
Sbi.locale.ln['sbi.geo.controlpanel.map'] = 'Map ';
Sbi.locale.ln['sbi.geo.controlpanel.zone'] = 'zone ';
Sbi.locale.ln['sbi.geo.controlpanel.point'] = 'point ';


Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Layers';
Sbi.locale.ln['sbi.geo.layerpanel.layer'] = ' layer';
Sbi.locale.ln['sbi.geo.layerpanel.add'] = 'Add layers';
Sbi.locale.ln['sbi.geo.layerpanel.catalogue'] = 'Layers Catalogue';
Sbi.locale.ln['sbi.geo.layerpanel.addremove'] = 'Add/Remove Layer';

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

Sbi.locale.ln['sbi.geo.controlpanel.filters'] = 'Filters';

Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'Legend';
Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Change style'; 
Sbi.locale.ln['sbi.geo.earthpanel.title'] = '3D Navigation';

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
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = 'Select the folder in which to publish the map. Saving in the [public] everyone will see the map. Saving in the [private] only logged in users will be able to see the map.';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.title']  = 'Send Feedback';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.label']  = 'Message text';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.btn.send']  = 'Send';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.sendOK']  = 'Feedback sent to document\'s creator';

Sbi.locale.ln['sbi.geo.controlpanel.control.share.title']  = 'Share map';


//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Map';

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
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Show on map';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Generate a thematic map using the selected indicators';
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
