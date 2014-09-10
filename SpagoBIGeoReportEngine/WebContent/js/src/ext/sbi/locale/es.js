/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

// once all the labels have been translated remove the following block

// < block START
Sbi.locale.unsupportedmsg = 'Sorry, spanish localization is not yet supported by SpagoBIGeoReportEngine.'
	+ 'In order to add spanish localization you can modify properly the file ' 
	+ 'SpagoBIGeoReportEngine/js/spagobi/locale/es.js translating text from english to spanish.'
	+ 'Once done please contribute it back to the project.';

Ext.Msg.show({
	   title:'Unimplemented functionality',
	   msg: Sbi.locale.unsupportedmsg,
	   buttons: Ext.Msg.OK,
	   icon: Ext.MessageBox.INFO
});
// block END >

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
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Navegaci\u00f3n';
Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Niveles';

Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'An\u00e1lisis';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'A\u00f1adir indicadores';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indicador';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 	'Seleccionar un indicador';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'M\u00e9todo';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'N\u00famero de clases';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'desde color';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'a color';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'establecer por defecto';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'valores por defecto correctamente establecidos';


Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'Leyenda';
Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Cambio de estilo'; 
Sbi.locale.ln['sbi.geo.earthpanel.title'] = 'Navegaci\u00f3n 3D';

//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.savewin.title'] = 'Insertar m\u00e1s detalles y salvar tus documentos...';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.name'] = 'Nombre';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.description'] = 'Descripci\u00f3n';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.visibility'] = 'Visibilidad documento';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.previewfile'] = 'Vista previa de archivos';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.community'] = 'Communidad';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.scope'] = 'Alcance';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.saveWarning']  = 'Antes de guardar el documento, es necesario insertar el nombre del mapa y seleccionar al menos una carpeta.';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = ' Seleccionar la carpeta en a cual pulicar el mapa. Guardar como [public] si todo el mundo verá el mapa. Guardar como [private] si sólo los usuarios registrados podrán ver el mapa.';

//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Mapa';

//===================================================================
//OPENLAYERS
//===================================================================
Sbi.locale.ln['mf.print.mapTitle'] =  'T\u00edtulo';
Sbi.locale.ln['mf.print.comment'] =  'Commentarios';
Sbi.locale.ln['mf.print.loadingConfig'] =  'Cargando la configuraci\u00f3n...';
Sbi.locale.ln['mf.print.serverDown'] =  'El servicio de impresi\u00f3n no est\u00e1 funcionando';
Sbi.locale.ln['mf.print.unableToPrint'] =  "No se puede imprimir";
Sbi.locale.ln['mf.print.generatingPDF'] =  "Generando PDF...";
Sbi.locale.ln['mf.print.dpi'] =  'DPI';
Sbi.locale.ln['mf.print.scale'] =  'Escala';
Sbi.locale.ln['mf.print.rotation'] =  'Rotaci\u00f3n';
Sbi.locale.ln['mf.print.print'] =  'Imprimir';
Sbi.locale.ln['mf.print.resetPos'] =  'Restablecer Pos.';
Sbi.locale.ln['mf.print.layout'] =  'Disposici\u00f3n de la pantalla';
Sbi.locale.ln['mf.print.addPage'] =  'A\u00f1adir p\u00e1gina';
Sbi.locale.ln['mf.print.remove'] =  'Eliminar p\u00e1gina';
Sbi.locale.ln['mf.print.clearAll'] =  'Limpiar todo';
Sbi.locale.ln['mf.print.popupBlocked'] =  'Las ventanas emergentes est\u00e1n bloqueadas por su navegador. <br />' +
                         '<br />Use este url para descargar su documento] = ';
Sbi.locale.ln['mf.print.noPage'] =  'No p\u00e1gina seleccionada; click en el bot\u00f3n "A\u00f1adir p\u00e1gina" para a\u00f1adir una.';
Sbi.locale.ln['mf.error'] =  'Error';
Sbi.locale.ln['mf.warning'] =  'Atenci\u00f3n';
Sbi.locale.ln['mf.information'] =  'Informaci\u00f3n';
Sbi.locale.ln['sbi.tools.catalogue.measures.measure.properties'] = 'Propiedades de medida';
Sbi.locale.ln['sbi.tools.catalogue.measures.dataset.properties'] = 'Propiedades del Data Set';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Join';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Ejecutar la combinaci\u00f3n de medidas seleccionadas';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.tooltip'] = 'Abra el cuadro de selecci\u00f3n';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.btn'] = 'Selecci\u00f3n';
Sbi.locale.ln['sbi.tools.catalogue.measures.window.title'] =  'Cat\u00e1logo de medidas';
Sbi.locale.ln['error.mesage.description.measure.join.no.common.dimension'] = 'Imposible ejecutar la combinaci\u00f3n de medidas seleccionadas. Los conjuntos de datos asociados no tienen ninguna dimensi\u00f3n en com\u00fan.';
Sbi.locale.ln['error.mesage.description.measure.join.no.complete.common.dimension'] = 'Imposible ejecutar la combinaci\u00f3n de medidas seleccionadas. Los data Los datasets asociados no tienen ninguna dimensi\u00f3n en com\u00fan';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.alias'] = 'Alias';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsName'] = 'Nombre';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsLabel'] = 'Etiqueta';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsCategory'] = 'Categor\u00eda';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsType'] = 'Tipo';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.label'] = 'Etiqueta';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.descr'] = 'Descripci\u00f3n';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.type'] = 'Tipo';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.baseLayer'] = 'Base capa';

