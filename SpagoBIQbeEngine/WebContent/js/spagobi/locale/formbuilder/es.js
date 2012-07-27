/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

//===================================================================
//FORM BUILDER PAGE
//===================================================================
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.title'] = 'Dise\u00F1ador';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.toolbar.save'] = 'Guardar plantilla';

Sbi.locale.ln['sbi.formbuilder.formbuilderpage.templatesaved.title'] = 'Plantilla guarda';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.templatesaved.msg'] = 'Plantilla guardada con \u00E9xito!';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.validationerrors.title'] = 'Error';

Sbi.locale.ln['sbi.formbuilder.formpanel.title'] = 'Dise\u00F1ador de formularios';

Sbi.locale.ln['sbi.formbuilder.formpreviewpage.title'] = 'Vista previa';

Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.title'] = 'Los campos seleccionados';
Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.tools.refresh'] = 'Actualizar campos de consulta';
Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.fieldname'] = 'Nombre del campo';

Sbi.locale.ln['sbi.formbuilder.templateeditorpanel.title'] = 'Dise\u00F1ador de formularios';

Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.documentfield.label'] = 'Documento';
Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.documentfield.emptytext'] = 'Seleccione un documento...';
Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.startediting'] = 'Iniciar el dise\u00F1ador...';

Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.title'] = 'Los filtros din\u00E1micos';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.emptymsg'] = 'Haga clic en el bot\u00F3n en la esquina superior derecha con el fin de agregar un filtro nuevo y din\u00E1mico';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.grouptitle'] = 'Grupo de filtros din\u00E1mica';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.filteritemname'] = 'Grupo de filtros din\u00E1mica';

Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.grouptitle'] = 'Filtro de grupo din\u00E1mico';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.emptymsg'] = 'Arrastre un campo aqu\u00ED para agregar una opci\u00F3n para el filtro din\u00E1mico';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.edit'] = 'Editar';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.remove'] = 'Quitar';


Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.title'] = 'Din\u00E1mica de filtro de grupo de la definici\u00F3n';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.fields.filtername.label'] = 'Nombre';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.fields.operatorfield.label'] = 'Operador';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.buttons.cancel'] = 'Cancelar';


Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.title'] = 'Variables de agrupaci\u00F3n';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.emptymsg'] = ' Arrastre un campo aqu\u00ED para agregar una variable nuevo grupo';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.grouptitle'] = 'Variable';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.validationerrors.missingadmissiblefields'] = 'Falta campos admisibles para la agrupaci\u00F3n de variables';

Sbi.locale.ln['sbi.formbuilder.variablegroupeditor.grouptitle'] = 'Grupo de variables';
Sbi.locale.ln['sbi.formbuilder.variablegroupeditor.emptymsg'] = 'Arrastre un campo aqu\u00ED para a\u00F1adir un nuevo valor admisible a la variable';

Sbi.locale.ln['sbi.formbuilder.editordroptarget.wrongdropmsg.title'] = 'Fuente equivocada arrastrado';
Sbi.locale.ln['sbi.formbuilder.editordroptarget.wrongdropmsg.msg'] = 'Seleccione un solo campo por favor';


Sbi.locale.ln['sbi.formbuilder.editorpanel.add'] = 'A\u00F1adir';
Sbi.locale.ln['sbi.formbuilder.editorpanel.clearall'] = 'Borrar todos';

Sbi.locale.ln['sbi.formbuilder.inlineeditor.edit'] = 'Editar';
Sbi.locale.ln['sbi.formbuilder.inlineeditor.remove'] = 'Eliminar';


Sbi.locale.ln['sbi.formbuilder.staticopenfiltereditorpanel.title'] = 'Los filtros est\u00E1ticos abiertos';
Sbi.locale.ln['sbi.formbuilder.staticopenfiltereditorpanel.emptymsg'] = 'Arrastre un campo aqu\u00ED para crear un nuevo filtro de est\u00E1tica abierta';

Sbi.locale.ln['sbi.formbuilder.staticopenfiltergroupeditor.edit'] = 'Editar';
Sbi.locale.ln['sbi.formbuilder.staticopenfiltergroupeditor.remove'] = 'Eliminar';


Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.title'] = 'Static definici\u00F3n abierta del filtro';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filtername.label'] = 'Nombre';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filterentity.label'] = 'Campo';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filteroperator.label'] = 'Operador';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.maxselectionnumber.label'] = 'Max selecci\u00F3n';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.orderbyfield.label'] = 'Ordenar los resultados por los';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.ordertype.label'] = 'Orden de tipo';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.title'] = 'B\u00FAsqueda de los detalles de la consulta';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.customquerydetailssection.title'] = 'Consulta personalizada';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.standardquerydetailssection.title'] = 'Consulta normalizada';

Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.customquerydetailssection.lookupquery'] = 'B\u00FAsqueda de la consulta';

Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.promptvalues'] = 'valores de la solicitud';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.donotqueryrootentity'] = 'que son admisibles para el campo de filtro';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.queryrootentity'] = 'que son admisibles para el campo correspondiente en la entidad ra\u00EDz';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.buttons.cancel'] = 'Cancelar';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.title'] = 'Est\u00E1ticos filtros cerrados';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.emptymsg'] = 'Haga clic en el bot\u00F3n en la esquina superior derecha con el fin de agregar un grupo nuevo filtro';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.filteritemname'] = 'grupo est\u00E1tico filtros cerrado';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.add'] = 'A\u00F1adir';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.edit'] = 'Editar';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.remove'] = 'Quitar';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.title'] = 'Definici\u00F3n est\u00E1tica filtros cerca';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.noselectiontext'] = 'Todo';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.grouptitle.label'] = 'Nombre';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.label'] = 'Permitir la selecci\u00F3n \u00FAnica';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.no'] = 'No';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.yes'] = 'S\u00ED';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.label'] = 'Permita que "no selecci\u00F3n"';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.no'] = 'No';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.yes'] = 'S\u00ED';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.noselectionoptionlabel.label'] = 'No hay ninguna opci\u00F3n de selecci\u00F3n de la etiqueta';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.label'] = 'Boolean conector';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.and'] = 'Y';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.or'] = 'O';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.options'] = 'Opciones';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.buttons.cancel'] = 'Cancelar';


Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.title'] = 'Est\u00E1tica definici\u00F3n del filtro cerca';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.filtertitle.label'] = 'Nombre';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.leftoperand.label'] = 'Campo';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.operator.label'] = 'Operador';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.rightoperand.label'] = 'Valor';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.buttons.apply'] = 'Aplicar';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.buttons.cancel'] = 'Cancelar';


