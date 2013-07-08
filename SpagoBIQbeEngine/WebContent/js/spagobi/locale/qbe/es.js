/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//MESSAGE BOX BUTTONS
//===================================================================
Ext.Msg.buttonText.yes = 'Sí'; 
Ext.Msg.buttonText.no = 'No';


//===================================================================
//MESSAGE GENERAL
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.yes'] = 'Sí';
Sbi.locale.ln['sbi.qbe.messagewin.no'] = 'No';
Sbi.locale.ln['sbi.qbe.messagewin.cancel'] = 'Cancelar';


//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Mensaje de advertencia';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Mensaje de error';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Mensaje de información';


//===================================================================
//SESSION EXPIRED
//===================================================================
Sbi.locale.ln['sbi.qbe.sessionexpired.msg'] = 'La sesión ha caducado. Intente volver a ejecutar el documento';


//===================================================================
//QBE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.qbepanel.worksheetdesignerpanel.tools.preview'] = 'Mostrar vista previa deworksheet';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerytitle'] = 'Query está vacía';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerymessage'] = 'Query está vacía y no tiene permiso para crear nuevas consultas. Seleccione una query guardada de la lista de las vistas personalizadas.';


//===================================================================
//QUERY EDITOR PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.queryeditor.title'] = 'Query';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.title'] = 'Esquema';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.expand'] = 'Expandir todo'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.collapse'] = 'Contraer todo'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.flat'] = 'Vista plana'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.addcalculated'] = 'Agregar campo calculado'; 

Sbi.locale.ln['sbi.qbe.queryeditor.savequery'] = 'Guardar query ...';
Sbi.locale.ln['sbi.qbe.queryeditor.querysaved'] = 'Query guardada';
Sbi.locale.ln['sbi.qbe.queryeditor.querysavedsucc'] = 'Query guardada con éxito';
Sbi.locale.ln['sbi.qbe.queryeditor.msgwarning'] = 'La query no es correcta, ¿quieres guardar de todos modos?';
Sbi.locale.ln['sbi.qbe.queryeditor.saveqasview'] = 'Guardar query como vista...';

Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.title'] = 'Editor de query';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.save'] = 'Guarde la query como subobjeto';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.view'] = 'Guardar query como una vista';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.execute'] = 'Ejecutar la query';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.validate'] = 'Validar query';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.help'] = 'Ayuda por favor';

Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.title'] = 'Catálogo Query';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.delete'] = 'Eliminar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.add'] = 'Agregar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.insert'] = 'Insertar consulta';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.wanringEraseRoot'] = 'No se puede borrar la raíz query';


//===================================================================
//EXPRESSION EDITOR
//===================================================================
Sbi.locale.ln['sbi.qbe.expreditor.title'] = 'Editor de expresiones';
Sbi.locale.ln['sbi.qbe.expreditor.items'] = 'Exp. elementos';
Sbi.locale.ln['sbi.qbe.expreditor.operands'] = 'Operandos';
Sbi.locale.ln['sbi.qbe.expreditor.operators'] = 'Operadores';
Sbi.locale.ln['sbi.qbe.expreditor.structure'] = 'Exp. estructura';
Sbi.locale.ln['sbi.qbe.expreditor.clear'] = 'Borrar todo';
Sbi.locale.ln['sbi.qbe.expreditor.expression'] = 'Expresión';
Sbi.locale.ln['sbi.qbe.expreditor.log'] =  'Log';
Sbi.locale.ln['sbi.qbe.expreditor.refresh'] =  'Actualizar la estructura de la expresión';
Sbi.locale.ln['sbi.qbe.expreditor.clearttp'] =  'Borrar todos los campos seleccionados';
Sbi.locale.ln['sbi.qbe.expreditor.filterdesc'] = 'Descripción del filtro va aquí';
Sbi.locale.ln['sbi.qbe.expreditor.operatordesc'] =  'Descripción del operador va aquí';


//===================================================================
//DATASTORE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.title'] = 'Resultados';

Sbi.locale.ln['sbi.qbe.datastorepanel.grid.displaymsg'] = 'Mostrando {0} - {1} de {2}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptymsg'] = 'No hay datos para mostrar';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptywarningmsg'] = 'Query no devuelve datos';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforeoverflow'] = 'Límite máximo número de registros';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afteroverflow'] = 'excedido';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforepagetext'] = 'Página';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afterpagetext'] = 'de {0}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.firsttext'] = 'Primera Página';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.prevtext'] = 'Página anterior';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.nexttext'] = 'Página siguiente';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.lasttext'] = 'última Página';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.refreshtext'] = 'Refrescar';

Sbi.locale.ln['sbi.qbe.datastorepanel.button.tt.exportto'] = 'Exportar a';


//===================================================================
//SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.savewindow.desc'] = 'Descripción';
Sbi.locale.ln['sbi.qbe.savewindow.name'] = 'Nombre' ;
Sbi.locale.ln['sbi.qbe.savewindow.saveas'] = 'Guardar como ...' ;
Sbi.locale.ln['sbi.qbe.savewindow.selectscope'] = 'Seleccione el alcance...' ;
Sbi.locale.ln['sbi.qbe.savewindow.scope'] = 'Alcance';
Sbi.locale.ln['sbi.qbe.savewindow.save'] = 'Salvar';
Sbi.locale.ln['sbi.qbe.savewindow.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.savewindow.public'] = 'Público';
Sbi.locale.ln['sbi.qbe.savewindow.private'] = 'Privado';
Sbi.locale.ln['sbi.qbe.savewindow.publicdesc'] = 'Todo el mundo que puede ejecutar este documento verá también el subobjeto guardado';
Sbi.locale.ln['sbi.qbe.savewindow.privatedesc'] = 'La query guardada será visible sólo para ti';
Sbi.locale.ln['sbi.qbe.savewindow.selectmetadata'] = 'Introduzca los metadatos';


//===================================================================
//FILTER GRID
//===================================================================
Sbi.locale.ln['sbi.qbe.filtergridpanel.title'] = 'Cláusula Where';

Sbi.locale.ln['sbi.qbe.filtergridpanel.namePrefix'] = 'Filtrar';

//column headers
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.name'] = 'Nombre del filtro';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.desc'] = 'Desc. del filtro';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.loval'] = 'Valor del operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodesc'] = 'operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lotype'] = 'Tipo de operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodef'] = 'Valor predeterminado operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lolast'] = 'último valor del operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.operator'] = 'Operador';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.roval'] = 'Valor del operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodesc'] = 'Operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rotype'] = 'Tipo de operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodef'] = 'Valor predeterminado operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rolast'] = 'último valor del operando derecho';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.isfree'] = 'Es por Prompt';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.boperator'] = 'Bol. Conector';

//column tooltip
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.notdef'] = 'Heramienta de Ayuda no definida todavía';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.name'] = 'Nombre de filtro único';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.desc'] = 'Descripción del propósito Filtro';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.loval'] = 'Valor del operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodesc'] = 'Operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lotype'] = 'Tipo de operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodef'] = 'Valor predeterminado del operando izquierdo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lolast'] = 'último valor del operando izquierdo';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.operator'] = 'Operador';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.roval'] = 'Valor del operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodesc'] = 'Operando derecho';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rotype'] = 'Derecho de tipo de operando';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodef'] = 'Derecho operando valor por defecto';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rolast'] = 'último valor del operando derecho';


//boolean operators
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.and'] = 'AND';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.or'] = 'OR';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.and'] = 'Conectar este filtro y el siguiente usando el operador booleano AND';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.or'] = 'Conectar este filtro y el siguiente usando el operador booleano OR';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.editor.emptymsg'] = 'Seleccione un operador...';


//filter operators

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eq'] = 'es igual a';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.noteq'] = 'no es igual a';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.gt'] = 'mayor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqgt'] = 'es igual o mayor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.lt'] = 'menor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqlt'] = 'es igual o menor que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.starts'] = 'comienza con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notstarts'] = 'No comienza con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.ends'] = 'termina con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notends'] = 'No termina con';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.contains'] = 'contiene';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notcontains'] = 'no contiene';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.between'] = 'entre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notbetween'] = 'no entre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.in'] = 'en';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notin'] = 'no en';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notnull'] = 'no es nulo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.isnull'] = 'es nulo';

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.none'] = 'Ningún filtro aplicado';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eq'] = 'Verdadero si el valor del campo es igual al valor del filtro';
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
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.add'] = 'Crear un nuevo filtro vacío';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.delete'] = 'Eliminar todos los filtros';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.wizard'] = 'Exp Wizard';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.wizard'] = 'Exp Wizard';

// warnings
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.title'] = '¿Borrar filtro?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.msg'] = 'Estás suprimiendo un filtro que se utiliza en una expresión anidada (véase asistente expresión). Si lo quita, se reinicializarà la expresión. ¿Desea continuar?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.title'] = '¿Eliminar todos los filtros?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.msg'] = 'Usted va a eliminar todos los filtro. ¿Desea continuar?';


Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.title'] = '¿Cambiar conector booleano?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.msg'] = 'Cambiando el conector booleano de este filtro se restablecerá la expresión anidada asociada (véase asistente expresión). ¿Desea continuar?';

// ===================================================================
//	SELECT GRID
// ===================================================================
Sbi.locale.ln['sbi.qbe.selectgridpanel.title'] = 'Seleccione los campos';

// column headers
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.visible'] = 'Visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.include'] = 'Incluir';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.filter'] = 'Filtrar';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.having'] = 'Filtrar grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.entity'] = 'Entidad';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.alias'] = 'Alias';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.order'] = 'Orden';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Función';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.field'] = 'Campo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete'] = 'Eliminar todo';

//aggregation functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'suma';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'promedio';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'Máximo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'Mínimo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'contar (count)';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'contar valores distintos (distinct count)';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpSum'] = 'Atributo a usar en función SUM';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMin'] = 'Atributo a usar en función de MIN';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMax'] = 'Atributo a usar en función MAX';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCount'] = 'Atributo a usar en función de Contar (count)';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCountDist'] = 'Atributo a usar en función Distinct Count';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpAVG'] = 'Atributo a usar en función de AVG';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate'] = 'Fecha  a utilizar en la función';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1'] = 'Fecha de inicio para utilizar en la función';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2'] = 'Fin fecha para utilizar en la función';

Sbi.locale.ln['sbi.qbe.selectgridpanel.func.link.tip'] = 'Crear un vínculo a la página web externa';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.image.tip'] = 'Incluir una imagen externa';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.cross.tip'] = 'crear un vínculo de navegación cruzada (cross navigation)';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.sum.tip'] = 'Función de suma binaria';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.difference.tip'] = 'Función de diferencia binaria';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.multiplication.tip'] = 'Función de multiplicación binaria';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.division.tip'] = 'Función de división binaria';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.pipe.tip'] = 'conducto';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.openpar.tip'] = 'paréntesis de apertura';
Sbi.locale.ln['sbi.qbe.selectgridpanel.func.closepar.tip'] = 'cierre de paréntesis';

Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.none'] = 'Sin función de agregación aplicado';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.sum'] = 'Calcular la suma de todos los valores del grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.avg'] = 'Calcular la media de todos los valores del grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.max'] = 'Calcular el max de todos los valores del grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.min'] = 'Calcular el min de todos los valores del grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.count'] = 'Calcular la count de todos los valores del grupo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct'] = 'Calcular distinct count de todos los valores del grupo';

Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.ggbetweendates'] = 'Calcular la diferencia en días entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.mmbetweendates'] = 'Calcular la diferencia en meses entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.aabetweendates'] = 'Calcular la diferencia en años entre dos campos de fechas';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.gguptoday'] = 'Calcular la diferencia en días entre ahora y un campo de fecha';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.mmuptoday'] = 'Calcular la diferencia en meses entre el momento actual y un campo de fecha';
Sbi.locale.ln['sbi.qbe.selectgridpanel.datefunc.desc.aauptoday'] = 'Calcular la diferencia en años entre ahora y un campo de fecha';
	
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.editor.emptymsg'] = 'Seleccione una función...';


// sorting functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.none'] = 'ninguno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.asc'] = 'ascendente';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.desc'] = 'descendente';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.none'] = 'No ordenación aplicada a la columna dada';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.asc'] = 'Ordenar los valores dados de manera ascendente';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.desc'] = 'Ordenar los valores dados de manera descendente';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.editor.emptymsg'] = 'Seleccione la dirección del pedido...';

//buttons 
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.hide'] = 'Ocultar no visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.hide'] = 'Ocultar todos los campos no visibles';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.group'] = 'Agrupar por entidad';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.group'] = 'Agrupar campos por entidad matriz';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.add'] = 'Añadir calculado';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.add'] = 'Agregar un campo calculado ad-hoc (es decir, válido sólo para esta query)';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.expert'] = 'Usuario experto';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.delete'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.delete'] = 'Eliminar archivos seleccionados';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.deleteall'] = 'Eliminar todo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.deleteall'] = 'Eliminar todos los campos seleccionados';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.distinct'] = 'Aplicar la Cláusula distinct';

Sbi.locale.ln['sbi.qbe.freeconditionswindow.title'] = 'Fill free conditionse';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restoredefaults'] = 'Restaurar valores predeterminados';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.saveasdefaults'] = 'Establecer como predeterminado';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restorelast'] = 'Restaurar última';

//===================================================================
//QUERY CATALOGUE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.cataloguepanel.title'] = 'Catálogo Query';

//===================================================================
//HAVING CLAUSE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.havinggridpanel.title'] = 'Cláusula Having';

//===================================================================
//DOCUMENT PARAMETERS PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.title'] = 'Conductores analíticos del Documento';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.emptytext'] = 'Este documento no tiene controladores analíticos';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.headers.label'] = 'Títul';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.parameterreference'] = 'Conductor aanalítico';
Sbi.locale.ln['sbi.qbe.parametersgridpanel.parameterreference'] = 'Parámetro';


//===================================================================
//DATA STORE PANEL AND EXTERNAL SERVICES
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.title'] = 'El servicio fue invocado correctamente';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.serviceresponse'] = 'El servicio ha devuelto el siguiente mensaje:';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.title'] = 'Error';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.missingcolumns'] = 'El servicio requerido necesita las siguientes columnas:';
Sbi.locale.ln['sbi.qbe.datastore.refreshgrid'] = 'Restaurar estilo de la cuadrícula'

//===================================================================
//CALCULATED FIELD WIZARD
//===================================================================
Sbi.locale.ln['sbi.qbe.calculatedFields.title'] = 'Asistente de Campo calculado (Modo experto)';
Sbi.locale.ln['sbi.qbe.inlineCalculatedFields.title'] = 'Asistente de Campo calculado (modo simple)';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.title'] = 'Validación';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.text'] = 'Aceptar Validación';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.fail.title'] = 'Validación Fallida';
Sbi.locale.ln['sbi.qbe.calculatedFields.expert.nofilterwindow.title'] = 'Advertencia: con este tipo de campos calculados no se puede usar filtros';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.ok'] = 'Aceptar';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.qbe.calculatedFields.fields'] = 'Campos';
Sbi.locale.ln['sbi.qbe.calculatedFields.attributes'] = 'Atributos';
Sbi.locale.ln['sbi.qbe.calculatedFields.parameters'] = 'Parámetro';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions'] = 'Funciones';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.arithmentic'] = 'Funciones aritméticas';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.script'] = 'Groovy funciones';
Sbi.locale.ln['sbi.qbe.calculatedFields.aggrfunctions'] = 'Funciones de agregación';
Sbi.locale.ln['sbi.qbe.calculatedFields.datefunctions'] = 'Funciones de fecha';
Sbi.locale.ln['sbi.qbe.calculatedFields.string.type'] = 'Si la expresión script devuelve una cadena de texto sin formato';
Sbi.locale.ln['sbi.qbe.calculatedFields.html.type'] = 'Si la expresión script devuelve un fragmento de código HTML válido';
Sbi.locale.ln['sbi.qbe.calculatedFields.num.type'] = 'Si la expresión script devuelve un número';
Sbi.locale.ln['sbi.qbe.calculatedFields.date.type'] = 'Si la expresión script devuelve una fecha';
Sbi.locale.ln['sbi.qbe.calculatedFields.add'] = 'Agregar campo calculado';
Sbi.locale.ln['sbi.qbe.calculatedFields.remove'] = 'Quitar campo calculado';
Sbi.locale.ln['sbi.qbe.calculatedFields.edit'] = 'Editar campo';
Sbi.locale.ln['sbi.qbe.calculatedFields.add.error'] = 'Imposible agregar campo calculado a un nodo de tipo [{0}]';
Sbi.locale.ln['sbi.qbe.calculatedFields.operands.title.text'] = 'Seleccionar operativo';

//===================================================================
//BANDS WIZARD
//===================================================================
Sbi.locale.ln['sbi.qbe.menu.bands.add'] = 'Añadir Rango';
Sbi.locale.ln['sbi.qbe.menu.bands.edit'] = 'Editar rango';
Sbi.locale.ln['sbi.qbe.bands.title'] = 'Asistente de bandas ...';
Sbi.locale.ln['sbi.qbe.bands.noteditable'] = 'No se puede cargar el rango de definición';
Sbi.locale.ln['sbi.qbe.bands.back.btn'] = 'Anterior';
Sbi.locale.ln['sbi.qbe.bands.next.btn'] = 'Siguiente';
Sbi.locale.ln['sbi.qbe.bands.finish.btn'] = 'Terminar';
Sbi.locale.ln['sbi.qbe.bands.save.btn'] = 'Guardar';
Sbi.locale.ln['sbi.qbe.bands.addband.btn'] = 'Añadir Banda';
Sbi.locale.ln['sbi.qbe.bands.adddefault.btn'] = 'Añadir por defecto';
Sbi.locale.ln['sbi.qbe.bands.delete.btn'] = 'Borrar';
Sbi.locale.ln['sbi.qbe.bands.col.name'] = 'Nombre';
Sbi.locale.ln['sbi.qbe.bands.col.values'] = 'Valores';
Sbi.locale.ln['sbi.qbe.bands.col.limits'] = 'Límites';
Sbi.locale.ln['sbi.qbe.bands.col.vallist'] = 'Lista de Valores';
Sbi.locale.ln['sbi.qbe.bands.alert.default'] = 'Operación denegada por la predeterminada banda';
Sbi.locale.ln['sbi.qbe.bands.delete.alert.title'] = 'Eliminación del elemento Banda';
Sbi.locale.ln['sbi.qbe.bands.delete.alert.msg'] = 'Confirmar eliminar elemento?';
Sbi.locale.ln['sbi.qbe.bands.new.name'] = 'Nueva Banda';
Sbi.locale.ln['sbi.qbe.bands.default.name'] = 'Otro';
Sbi.locale.ln['sbi.qbe.bands.default.alert'] = 'Por defecto ya definido';
Sbi.locale.ln['sbi.qbe.bands.prefix'] = 'Banda-';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.definition'] = 'Definición no válida';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.definition.msg'] = 'Imposible de añadir la ranura para un nodo de tipo [{0}]';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation'] = 'Operación no válida';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.msg'] = 'Nodo de tipo [{0}] no se ha modificado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.delete.msg'] = 'Nodo de tipo [{0}] no se ha eliminado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.operation.edit.msg'] = 'Nodo de tipo [{0}] no se ha editado';
Sbi.locale.ln['sbi.qbe.bands.wizard.invalid.node'] = 'El nodo no es una banda';
Sbi.locale.ln['sbi.qbe.bands.range.title'] = 'Limitar la definino válida';
Sbi.locale.ln['sbi.qbe.bands.range.invalid'] = 'El alcance no ha sido ajustado correctamente';




Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.boperator.desc'] = 'Bol.Connector entre filtros';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.isfree.desc'] = 'El usuario puede seleccionar el valor en un ventana emergente cuando la query es ejecutada';
