/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//CROSSTAB DESIGNER
//===================================================================
Sbi.locale.ln['sbi.crosstab.crosstabdesignerpanel.title'] = 'Dise\u00F1ador de la tabla de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.title'] = 'Definici\u00F3nn de la tabla de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.tools.preview'] = 'Mostrar vista previa de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.rows'] = 'Filas';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.columns'] = 'Columnas';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.measures'] = 'Medidas';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.title'] = 'Ca\u00EDda no se les permite';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresent'] = 'El atributo ya est\u00E1 presente';
//Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresentfilters'] = 'The attribute is already present in filters';

Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.measures'] = 'No se puede arrastrar las medidas en las filas o columnas: lo que tienes que arrastrarlos a la secci\u00F3n central de la tabla de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.tools.tt.removeall'] = ['Eliminar todosl'];

Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.title'] = 'Ca|u00EDda no se les permite';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.measurealreadypresent'] = 'La medida ya est\u00E1 presente';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.attributes'] = 'No se puede arrastrar los atributos en la secci\u00F3n central de la tabla de referencias cruzadas: usted tiene que arrastrar en filas o columnas';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.postlinecalculated'] = 'No se puede utilizar secuencias de comandos basados en los campos calculados en la tabla de referencias cruzadas';

Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.showdetailswizard'] = 'Mostrar detalles';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.removeall'] = ['Eliminar todos'];

Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.title'] = 'Detalles de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.measureson'] = 'Las medidas relativas a';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.rows'] = 'filas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.columns'] = 'columnas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.onrows'] = 'En filas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.oncolumns'] = 'En las columnas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsonrows'] = 'mostrar los totales';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsonrows'] = 'mostrar los subtotales';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsoncolumns'] = 'mostrar los totales';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsoncolumns'] = 'mostrar los subtotales';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.percenton'] = 'Porcentaje calculado sobre';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.row'] = 'filas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.column'] = 'columnas';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.nopercent'] = 'no';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.maxcellnumber'] = 'Max células n\u00FAmero de';

Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.title'] = 'Elija la funci\u00F3n de agregaci\u00F3n para la medida';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.cancel'] = 'Cancelar';

Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.title'] = 'Mostrar vista previa de referencias cruzadas';
Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.overflow.warning'] = 'El n\u00FAmero de c\u00E9lulas es highter sea el predeterminado. Usted puede encontrar todos los datos con la exportaci\u00F3n XLS.';

Sbi.locale.ln['sbi.crosstab.crossTabValidation.title'] = 'La validaci\u00F3n de referencias cruzadas no';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMeasure'] = 'No se han incluido ninguna medida de tabla din\u00E1mica';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noAttribute'] = 'No se han incluido todos los atributos de la tabla din\u00E1mica';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noSegmentAttribute'] = 'Caracter\u00EDsticas del segmento debe ser incluido';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMandatoryMeasure'] = 'Medida obligatoria debe ser incluido';


//===================================================================
//CROSSTAB 
//===================================================================

Sbi.locale.ln['sbi.crosstab.header.total.text'] = 'Total';

Sbi.locale.ln['sbi.crosstab.menu.addcalculatedfield'] = 'Agregar campo calculado';
Sbi.locale.ln['sbi.crosstab.menu.removecalculatedfield'] = 'Quitar campo calculado';
Sbi.locale.ln['sbi.crosstab.menu.modifycalculatedfield'] = 'Modificar campo calculado';
Sbi.locale.ln['sbi.crosstab.menu.hideheader'] = 'Ocultar esta cabecera';
Sbi.locale.ln['sbi.crosstab.menu.hideheadertype'] = 'Ocultar todas las cabeceras de este tipo';
Sbi.locale.ln['sbi.crosstab.menu.hidemeasure'] = 'Medidas';
Sbi.locale.ln['sbi.crosstab.menu.hiddenheader'] = 'Encabezados ocultos';

//===================================================================
//CROSSTAB CALCULATED FIELDS WIZARD
//===================================================================
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.title'] = 'Campo calculado';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.validate'] = 'Validar';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.clear'] = 'Borrar';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.ok'] = 'Aceptar';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.info'] = 'Un campo de calculo es una expresi\u00F3n compuesta por operadores matem\u00E1ticos, constantes y variables. Las variables son los identificadores de las columnas o filas. Con el fin de insertar una columna / fila, debe hacer clic en la cabecera correspondiente. Puede hacer clic en los encabezados de nivel en la bruja de los campos calculados se a\u00F1adirán.';
	