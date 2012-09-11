/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//MESSAGE BOX BUTTONS
//===================================================================
Ext.Msg.buttonText.yes = 'Si'; 
Ext.Msg.buttonText.no = 'No';


//===================================================================
//MESSAGE GENERAL
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.yes'] = 'Si';
Sbi.locale.ln['sbi.qbe.messagewin.no'] = 'No';
Sbi.locale.ln['sbi.qbe.messagewin.cancel'] = 'Cancelar';


//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Mensaje de advertencia';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Mensaje de error';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Mensaje de informaci\u00F2n';


//===================================================================
//SESSION EXPIRED
//===================================================================
Sbi.locale.ln['sbi.qbe.sessionexpired.msg'] = 'Sesi\u00F3n ha caducado. Intente volver a ejecutar el documento';


//===================================================================
//QBE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.qbepanel.worksheetdesignerpanel.tools.preview'] = 'Mostrar vista previa de hoja de c\u00E1lculo';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerytitle'] = 'Consulta est\u00E1 vac\u00EDa';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerymessage'] = 'Consulta est\u00E1 vac\u00EDa y no tiene permiso para crear nuevas consultas. Seleccione una consulta guardada de la lista de las vistas personalizadas.';


//===================================================================
//QUERY EDITOR PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.queryeditor.title'] = 'Pregunta';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.title'] = 'Esquema';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.expand'] = 'Expandir todo'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.collapse'] = 'Contraer todo'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.flat'] = 'Vista plana'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.addcalculated'] = 'A\u00F1adir campo calulated'; 

Sbi.locale.ln['sbi.qbe.queryeditor.savequery'] = 'Guardar consulta ...';
Sbi.locale.ln['sbi.qbe.queryeditor.querysaved'] = 'Consulta guardada';
Sbi.locale.ln['sbi.qbe.queryeditor.querysavedsucc'] = 'Consulta guardada con \u00E9xito';
Sbi.locale.ln['sbi.qbe.queryeditor.msgwarning'] = 'La consulta no es correcta, ¿quieres guardar de todos modos?';
Sbi.locale.ln['sbi.qbe.queryeditor.saveqasview'] = 'Save query as view...';

Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.title'] = 'Editor de consultas';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.save'] = 'Guarde la consulta como subobjeto';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.view'] = 'Guardar consulta como una vista';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.execute'] = 'Ejecutar la consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.validate'] = 'Validar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.help'] = 'Ayuda por favor';

Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.title'] = 'Cat\u00E1logo de consultas';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.delete'] = 'Eliminar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.add'] = 'Agregar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.insert'] = 'Insertar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.wanringEraseRoot'] = 'No se puede borrar la ra\u00EDz de consultas';


//===================================================================
//EXPRESSION EDITOR
//===================================================================
Sbi.locale.ln['sbi.qbe.expreditor.title'] = 'Expresi\u00F3n del Editor';
Sbi.locale.ln['sbi.qbe.expreditor.items'] = 'Exp. Art\u00EDculos';
Sbi.locale.ln['sbi.qbe.expreditor.operands'] = 'Operandos';
Sbi.locale.ln['sbi.qbe.expreditor.operators'] = 'Operadores';
Sbi.locale.ln['sbi.qbe.expreditor.structure'] = 'Exp. estructura';
Sbi.locale.ln['sbi.qbe.expreditor.clear'] = 'Borrar todo';
Sbi.locale.ln['sbi.qbe.expreditor.expression'] = 'Expresi\u00F3n';
Sbi.locale.ln['sbi.qbe.expreditor.log'] =  'Log';
Sbi.locale.ln['sbi.qbe.expreditor.refresh'] =  'Actualizar la estructura de la expresi\u00F3n';
Sbi.locale.ln['sbi.qbe.expreditor.clearttp'] =  'Borrar todos los campos seleccionados';
Sbi.locale.ln['sbi.qbe.expreditor.filterdesc'] = 'Descripci\u00F3n del filtro va aqu\u00ED';
Sbi.locale.ln['sbi.qbe.expreditor.operatordesc'] =  'Descripci\u00F3n del operador va aqu\u00ED';


//===================================================================
//DATASTORE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.title'] = 'Resultados';

Sbi.locale.ln['sbi.qbe.datastorepanel.grid.displaymsg'] = 'Mostrando {0} - {1} de {2}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptymsg'] = 'No hay datos para mostrar';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptywarningmsg'] = 'Consulta no devuelve datos';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforeoverflow'] = 'Max l\u00EDmite de n\u00FAmero de registros';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afteroverflow'] = 'excedido';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforepagetext'] = 'P\u00e1gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afterpagetext'] = 'de {0}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.firsttext'] = 'Primera P\u00E1gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.prevtext'] = 'P\u00E1gina anterior';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.nexttext'] = 'P\u00E1gina siguiente';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.lasttext'] = '\u00DAltima P\u00E1gina';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.refreshtext'] = 'Refrescar';

Sbi.locale.ln['sbi.qbe.datastorepanel.button.tt.exportto'] = 'Exportar a';


//===================================================================
//SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.savewindow.desc'] = 'Descripci\u00F3n';
Sbi.locale.ln['sbi.qbe.savewindow.name'] = 'Nombre' ;
Sbi.locale.ln['sbi.qbe.savewindow.saveas'] = 'Guardar como ...' ;
Sbi.locale.ln['sbi.qbe.savewindow.selectscope'] = 'Seleccione el alcance...' ;
Sbi.locale.ln['sbi.qbe.savewindow.scope'] = 'Alcance';
Sbi.locale.ln['sbi.qbe.savewindow.save'] = 'Salvar';
Sbi.locale.ln['sbi.qbe.savewindow.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.savewindow.public'] = 'P\u00FAblico';
Sbi.locale.ln['sbi.qbe.savewindow.private'] = 'Privado';
Sbi.locale.ln['sbi.qbe.savewindow.publicdesc'] = 'Todo el mundo que se puede ejecutar este documento se ver\u00E1 tambi\u00E9n el subobjeto salvado';
Sbi.locale.ln['sbi.qbe.savewindow.privatedesc'] = 'La consulta guardada ser\u00E1 visible sólo a';
Sbi.locale.ln['sbi.qbe.savewindow.selectmetadata'] = 'Introduzca los metadatos';


//===================================================================
//FILTER GRID
//===================================================================
Sbi.locale.ln['sbi.qbe.filtergridpanel.title'] = 'Caso de la cl\u00E1usula';

Sbi.locale.ln['sbi.qbe.filtergridpanel.namePrefix'] = 'Filtrar';

//column headers
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.name'] = 'Nombre del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.desc'] = 'Desc. del filtro';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.loval'] = 'Izquierda valor del operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodesc'] = 'Izquierda operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lotype'] = 'Izquierda tipo de operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodef'] = 'Izquierda operando valor por defecto';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lolast'] = 'Izquierda valor del operando \u00FAltima';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.operator'] = 'Operador';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.roval'] = 'Valor del operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodesc'] = 'Operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rotype'] = 'Derecho de tipo de operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodef'] = 'Derecho operando valor por defecto';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rolast'] = '\u00DAltimo valor del operando derecho';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.isfree'] = 'Es de sistema';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.boperator'] = 'Bol. Conector';

//column tooltip
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.notdef'] = 'Ayuda sobre herramientas no definido a\u00FAn';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.name'] = 'Nombre \u00FAnico filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.desc'] = 'Filtro de prop\u00F3sito descripci\u00F3n';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.loval'] = 'Izquierda valor del operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodesc'] = 'Izquierda operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lotype'] = 'Izquierda tipo de operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodef'] = 'Izquierda operando valor por defecto';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lolast'] = 'Izquierda valor del operando \u00FAltima';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.operator'] = 'Operador';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.roval'] = 'Valor del operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodesc'] = 'Operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rotype'] = 'Derecho de tipo de operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodef'] = 'Derecho operando valor por defecto';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rolast'] = 'u00DAltimo valor del operando derecho';


//boolean operators
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.and'] = 'Y';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.or'] = 'O';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.and'] = 'Conecte el filtro y el siguiente con los operadores booleanos Y';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.or'] = 'Conecte el filtro y el siguiente con el operador booleano OR';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.editor.emptymsg'] = 'Seleccione un operador...';


//filter operators

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.none'] = 'el noveno';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eq'] = 'es igual a';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.noteq'] = 'no es igual a';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.gt'] = 'mayor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqgt'] = 'es igual o mayor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.lt'] = 'menor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqlt'] = 'es igual o menor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.starts'] = 'comienza con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notstarts'] = 'No se comienza con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.ends'] = 'termina con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notends'] = 'No se termina con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.contains'] = 'contiene';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notcontains'] = 'no contiene';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.between'] = 'entre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notbetween'] = 'no entre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.in'] = 'en';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notin'] = 'no in';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notnull'] = 'no es nulo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.isnull'] = 'es nulo';

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.none'] = 'no se aplica un filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eq'] = 'verdadero si el valor del campo es igual al valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.noteq'] = 'verdadero si el valor del campo no es igual al valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.gt'] = 'verdadero si el valor del campo es mayor que el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eqgt'] = 'verdadero si el valor del campo es igual o mayor que el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.lt'] = 'verdadero si el valor del campo es inferior al valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eqlt'] = 'verdadero si el valor del campo comienza con el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.starts'] = 'verdadero si el valor del campo es igual o menor que el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notstarts'] = 'verdadero si el valor del campo no se inicia con el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.ends'] = 'verdadero si el valor del campo termina con el valor de filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notends'] = 'verdadero si el valor del campo no termina con el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.contains'] = 'verdadero si el valor del campo contiene el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notcontains'] = 'verdadero si el valor del campo no contiene el valor del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.between'] = 'verdadero si el valor del campo es entre el rango especificado en el valor de filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notbetween'] = 'verdadero si el valor del campo no es entre el rango especificado en el valor de filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.in'] = 'verdadero si el valor del campo es igual a uno de los valores especificados en el valor de filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notin'] = 'verdadero si el valor del campo no es igual a cualquiera de los valores especificados en el valor de filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notnull'] = 'verdadero   si el valor del campo no es nulo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.isnull'] = 'verdadero si el valor del campo es nulo';

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.editor.emptymsg'] = 'Seleccione un operador...';

//buttons 
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.add'] = 'Nuevo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.add'] = 'Crear un filtro nuevo vac\u00EDo';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.delete'] = 'Eliminar todos los filtros';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.wizard'] = 'Exp Wizard';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.wizard'] = 'Exp Wizard';

// warnings
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.title'] = 'Quite el filtro?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.msg'] = 'Su est\u00E1 quitando un filtro que se utiliza en una expresi\u00F3n neasted (v\u00E9ase asistente expresi\00F3n). Si lo quita, se restablecer\u00E1 la expresi\u00F3n. ¿Le gustar\u00EDa ir de todos modos?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.title'] = 'Eliminar todos los filtros?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.msg'] = 'Usted va a eliminar todos los usa el filtro. ¿Quieres que siga?';


Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.title'] = 'Cambiar conector booleano?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.msg'] = 'Cambiar el conector booleano de este filtro, se restablecer\u00E1 la expresi\u00F3n neasted asociados (v\u00E9ase el asistente de expresi\u00F3n). ¿Le gustar\u00EDa ir de todos modos?';

// ===================================================================
//	SELECT GRID
// ===================================================================
Sbi.locale.ln['sbi.qbe.selectgridpanel.title'] = 'Seleccione los campos';

// column headers
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.visible'] = 'Visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.include'] = 'Incluir';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.filter'] = 'Filtrar';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.having'] = 'Tener';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.entity'] = 'Entidad';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.alias'] = 'Alias';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.order'] = 'Orden';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Funci\u00F3n';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.field'] = 'Campo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete'] = 'Eliminar todo';

//aggregation functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'suma';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'promedio';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'm\u00E1ximo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'm\u00EDnimo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'contar';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'contar con distintas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpSum'] = 'Atributo a usar en funci\u00F3n SUM';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMin'] = 'Atributo a usar en funci\u00F3n de MIN';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMax'] = 'Atributo a usar en funci\u00F3n MAX';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCount'] = 'Atributo a usar en funciu00F3n de Conde';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCountDist'] = 'Atributo a usar en funciu\00F3n Distinct Count';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpAVG'] = 'Atributo a usar en funciu\00F3n de AVG';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate'] = 'Fecha  a utilizar en la funci\00F3n';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1'] = 'Fecha de inicio para utilizar en la funci\00F3n';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2'] = 'Fin fecha para utilizar en la funci\00F3n';

Sbi.locale.ln['sbi.qbe.selectgridpanel.func.link.tip'] = 'create a link to external web page';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.image.tip'] = 'include an external image';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.cross.tip'] = 'create a cross navigation link';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.sum.tip'] = 'binary sum function';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.difference.tip'] = 'binary difference function';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.multiplication.tip'] = 'binary multiplication function';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.division.tip'] = 'binary division function';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.pipe.tip'] = 'pipe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.openpar.tip'] = 'open parenthesis';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.closepar.tip'] = 'closed parenthesis';

Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.none'] = 'No aggregation function applied';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.sum'] = 'Return the sum of all values in group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.avg'] = 'Return the average of all values in group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.max'] = 'Return the max of all values in group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.min'] = 'Return the min of all values in group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.count'] = 'Return the count of all values in group';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct'] = 'Return the count of distinct values in group';

Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.ggbetweendates'] = 'Volver la diferencia en d\u00EDas entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.mmbetweendates'] = 'Volver la diferencia en meses entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.aabetweendates'] = 'Volver la diferencia en años entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.gguptoday'] = 'Volver la diferencia en días entre ahora y un campo de fecha';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.mmuptoday'] = 'Volver la diferencia en meses entre el momento actual y un campo de fecha';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.aauptoday'] = 'Volver la diferencia en años entre ahora y un campo de fecha';
	
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.editor.emptymsg'] = 'Seleccione una funci\u00F3n...';


// sorting functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.asc'] = 'ascendente';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.desc'] = 'descendente';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.none'] = 'Sin ordenar aplicada a la colunm dado';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.asc'] = 'Los valores de orden de la columna dada en forma de asecnding';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.desc'] = 'Los valores de orden de la columna dada en forma descendente';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.editor.emptymsg'] = 'Seleccione la direcci\u00F3n del pedido...';

//buttons 
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.hide'] = 'Ocultar no visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.hide'] = 'Ocultar todos los campos no visibles';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.group'] = 'Grupo por la entidad';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.group'] = 'Los campos de entidad matriz del grupo';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.add'] = 'A\u00F1adir calcula';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.add'] = 'Agregar un campo calculado ad-hoc (es decir, v\u00E1lido s\u00F3lo para esta consulta)';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.expert'] = 'Expertos de usuario';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.delete'] = 'Eliminar seleccionados presentaron	';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.deleteall'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.deleteall'] = 'Eliminar todos los campos seleccionados';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.distinct'] = 'Aplicar la cl\u00E1usula distinta';

Sbi.locale.ln['sbi.qbe.freeconditionswindow.title'] = 'Llene las condiciones de libre';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restoredefaults'] = 'Restaurar valores predeterminados';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.saveasdefaults'] = 'Establecer como predeterminado';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restorelast'] = 'Restaurar \u00FAltima';

//===================================================================
//QUERY CATALOGUE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.cataloguepanel.title'] = 'Cat\u00E1logo de consultas';

//===================================================================
//HAVING CLAUSE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.havinggridpanel.title'] = 'Having cl\u00E1usula';

//===================================================================
//DOCUMENT PARAMETERS PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.title'] = 'Los conductores de an\u00E1lisis de documentos';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.emptytext'] = 'Este documento no tiene controladores de an\u00E1lisis';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.headers.label'] = 'T\u00EDtulo';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.parameterreference'] = 'Conductor anal\u00EDtica';
Sbi.locale.ln['sbi.qbe.parametersgridpanel.parameterreference'] = 'Par\u00E1metro';


//===================================================================
//DATA STORE PANEL AND EXTERNAL SERVICES
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.title'] = 'El servicio se ha invocado correctamente';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.serviceresponse'] = 'El servicio ha devuelto el siguiente mensaje:';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.title'] = 'Error';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.missingcolumns'] = 'El servicio requerido tiene las siguientes columnas:';
Sbi.locale.ln['sbi.qbe.datastore.refreshgrid'] = 'Restaurar estilo de la red'

//===================================================================
//CALCULATED FIELD WIZARD
//===================================================================
Sbi.locale.ln['sbi.qbe.calculatedFields.title'] = 'Asistente de Campo calculado (Modo experto)';
Sbi.locale.ln['sbi.qbe.inlineCalculatedFields.title'] = 'Asistente de Campo calculado (modo simple)';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.title'] = 'Validaci\u00F3n';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.text'] = 'Validaci\u00F3n Aceptar';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.fail.title'] = 'Validaci\u00F3n de Falla';
Sbi.locale.ln['sbi.qbe.calculatedFields.expert.nofilterwindow.title'] = 'Advertencia: con este tipo de campos calculados no se puede usar filtros';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.ok'] = 'Aceptar';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.calculatedFields.fields'] = 'Campos';
Sbi.locale.ln['sbi.qbe.calculatedFields.attributes'] = 'Atributos';
Sbi.locale.ln['sbi.qbe.calculatedFields.parameters'] = 'Par\u00E1metros';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions'] = 'Funciones';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.arithmentic'] = 'Funciones de c\u00E1lculo';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.script'] = 'Groovy funciones';
Sbi.locale.ln['sbi.qbe.calculatedFields.aggrfunctions'] = 'Funciones de agregaci\u00F3n';
Sbi.locale.ln['sbi.qbe.calculatedFields.datefunctions'] = 'Funciones de fecha';
Sbi.locale.ln['sbi.qbe.calculatedFields.string.type'] = 'Si el gui\u00F3n expresi\u00F3n devuelve una cadena de texto sin formato';
Sbi.locale.ln['sbi.qbe.calculatedFields.html.type'] = 'Si el gui\u00F3n expresión devuelve un fragmento de c\u00F3digo HTML v\u00E1lido';
Sbi.locale.ln['sbi.qbe.calculatedFields.num.type'] = 'Si la secuencia de comandos de la expresi\u00F3n devuelve un n\u00FAmero';
Sbi.locale.ln['sbi.qbe.calculatedFields.date.type'] = 'Si el gui\u00F3n expresi\u00F3n devuelve una fecha';
Sbi.locale.ln['sbi.qbe.calculatedFields.add'] = 'Agregar campo calculado';
Sbi.locale.ln['sbi.qbe.calculatedFields.remove'] = 'Quitar campo calculado';
Sbi.locale.ln['sbi.qbe.calculatedFields.edit'] = 'Editar campo';
Sbi.locale.ln['sbi.qbe.calculatedFields.add.error'] = 'Imposible agregar campo calculado a un nodo de tipo [{0}]';
Sbi.locale.ln['sbi.qbe.calculatedFields.operands.title.text'] = 'Seleccionar operativo';

//===================================================================
//BANDS WIZARD
//===================================================================
Sbi.locale.ln['sbi.qbe.menu.bands.add'] = 'A\u00F1adir Rango';
Sbi.locale.ln['sbi.qbe.menu.bands.edit'] = 'Editar rango';
Sbi.locale.ln['sbi.qbe.bands.title'] = 'Asistente de bandas ...';
Sbi.locale.ln['sbi.qbe.bands.noteditable'] = 'No se puede cargar la definici\u00F3n amplia';
Sbi.locale.ln['sbi.qbe.bands.back.btn'] = 'Espalda';
Sbi.locale.ln['sbi.qbe.bands.next.btn'] = 'Pr\u00F3ximo';
Sbi.locale.ln['sbi.qbe.bands.finish.btn'] = 'Terminar';
Sbi.locale.ln['sbi.qbe.bands.save.btn'] = 'Ahorrar';
Sbi.locale.ln['sbi.qbe.bands.addband.btn'] = 'A\u00F1adir Band';
Sbi.locale.ln['sbi.qbe.bands.adddefault.btn'] = 'A\u00F1adir por defecto';
Sbi.locale.ln['sbi.qbe.bands.delete.btn'] = 'Borrar';
Sbi.locale.ln['sbi.qbe.bands.col.name'] = 'Nombre';
Sbi.locale.ln['sbi.qbe.bands.col.values'] = 'Valores';
Sbi.locale.ln['sbi.qbe.bands.col.limits'] = 'L\u00EDmites';
Sbi.locale.ln['sbi.qbe.bands.col.vallist'] = 'Lista de Valores';
Sbi.locale.ln['sbi.qbe.bands.alert.default'] = 'Operaci\u00F3n denegada por la banda por defectod';
Sbi.locale.ln['sbi.qbe.bands.delete.alert.title'] = 'Banda eliminaci\u00F3ón tema';
Sbi.locale.ln['sbi.qbe.bands.delete.alert.msg'] = 'Confirmar el punto eliminarlo?';
Sbi.locale.ln['sbi.qbe.bands.new.name'] = 'Nueva Banda';
Sbi.locale.ln['sbi.qbe.bands.default.name'] = 'Otro';
Sbi.locale.ln['sbi.qbe.bands.default.alert'] = 'Deafult ya est\u00E1 definido';
Sbi.locale.ln['sbi.qbe.bands.prefix'] = 'Band-';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.definition'] = 'definición inv\u00E1lida';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.definition.msg'] = 'Imposible de a\u00F1adir la ranura para un nodo de tipo [{0}]';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation'] = 'Operaci\u00F3n no v\u00E1lida';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.msg'] = 'Nodo de tipo [{0}] no se ha modificado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.delete.msg'] = 'Nodo de tipo [{0}] no se ha eliminado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.edit.msg'] = 'Nodo de tipo [{0}] no se ha editado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.node'] = 'El nodo no es una banda';
Sbi.locale.ln['sbi.qbe.bands.range.title'] = 'Limitar la definici\u00F3n';
Sbi.locale.ln['sbi.qbe.bands.range.invalid'] = 'El alcance no ajustado correctamente';