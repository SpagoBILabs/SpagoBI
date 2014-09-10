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
//HELP
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.help.content'] = 'Seleccione los miembros visibles de la dimensión seleccionada. Estos miembros serán incluidas en la instrucción select de la consulta MDX ';
Sbi.locale.ln['sbi.olap.help.title'] = 'Ayuda';
Sbi.locale.ln['sbi.olap.execution.table.dimension.cannotchangehierarchy'] = 'No se puede cambiar la jerarquía, ya que hay algunas modificaciones pendientes en los datos; debe guardar en primer lugar las modificaciones .';

//===================================================================
//COMMONS
//===================================================================
Sbi.locale.ln['sbi.common.cancel'] = 'Cancelar';
Sbi.locale.ln['sbi.common.close'] = 'Cerrar';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Seleccionar';
Sbi.locale.ln['sbi.common.warning'] = 'Advertencia ';
Sbi.locale.ln['sbi.common.next'] = 'Siguiente';
Sbi.locale.ln['sbi.common.prev'] = 'Regresar';
Sbi.locale.ln['sbi.common.wait'] = 'Por favor espere...';
Sbi.locale.ln['sbi.common.wait.long'] = 'Esta operación puede tomar algunos minutos. Por favor espere...';


//===================================================================
// TOOLBAR
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_MDX'] = 'Mdx Query';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Modo Drill';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_UNDO'] = 'deshacer';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FLUSH_CACHE'] = 'Modelo Actualizar;
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FATHER_MEMBERS'] = 'Mostrar miembros padres';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_SPANS'] = 'Ocultar spans';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SHOW_PROPERTIES'] = 'Mostrar propiedades';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_EMPTY'] = 'Suprimir filas / columnas vacías';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE'] = 'Guardar';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE_NEW'] = "guardar como nueva versión";
Sbi.locale.ln['sbi.olap.toolbar.lock'] = "Modelo de bloqueo";
Sbi.locale.ln['sbi.olap.toolbar.unlock'] = "Modelo de desbloqueo";
Sbi.locale.ln['sbi.olap.toolbar.lock_other'] = "Modelo desbloqueado por otro usuario";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_VERSION_MANAGER'] = "Eliminar versiones";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_EXPORT_OUTPUT'] = "Asistente de salida";

//===================================================================
//FILTERS
//===================================================================

Sbi.locale.ln['sbi.olap.execution.table.filter.collapse'] = 'Contraer todo';
Sbi.locale.ln['sbi.olap.execution.table.filter.expand'] = 'Expander todo';
Sbi.locale.ln['sbi.olap.execution.table.filter.filter.title'] = 'Seleccionar un slicer';
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.title'] = 'Seleccionar los miembros visibles';
Sbi.locale.ln['sbi.olap.execution.table.filter.no.measure'] = 'Una medida no puede ser utilizado como un filtro';
Sbi.locale.ln['sbi.olap.execution.table.filter.empty'] = 'Arrastrar el miembro aquì si tu quieres usarlo como un slicer';

//===================================================================
//DIMENSIONS
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy'] = 'La jerarquía seleccionada es';
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy.2'] = 'Puedes cambiarlo en el siguiente formulario.';
Sbi.locale.ln['sbi.olap.execution.table.dimension.available.hierarchies'] = 'Jerarquías disponibles: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.no.enough'] = 'Debe haber al menos una dimensiones en las columnas y filas';

//===================================================================
//WRITEBACK
//===================================================================
Sbi.locale.ln['sbi.olap.writeback.persist.error'] = 'Error salvando las modificaciones';
Sbi.locale.ln['sbi.olap.writeback.edit.no.zero'] =  '¡No es posible editar esta celda! El algoritmo de propagación actual conserva el peso entre las células adyacentes; al editar una celda en blanco o con valor nulo, esta restricción se romperá en las celdas hijas. En el próximo lanzamiento proporcionaremos más algoritmos de propagación. ';
Sbi.locale.ln['sbi.olap.writeback.edit.no.locked'] =  'No es posible editar un modelo si no se ha establecido un bloqueo en ella';

//===================================================================
//LOCK
//===================================================================
Sbi.locale.ln['sbi.olap.artifact.lock.error'] = 'La operación de bloqueo no trabaja, el modelo está todavía bloqueado';
Sbi.locale.ln['sbi.olap.artifact.unlock.error'] = 'La operación de desbloqueo no funciona, el modelo sigue bloqueado';
Sbi.locale.ln['sbi.olap.artifact.unlock.errorOther'] = 'La operación de desbloqueo no funciona, el modelo está bloqueado por el usuario';

//===================================================================
//MENU
//===================================================================
Sbi.locale.ln['sbi.olap.execution.menu.buttonMenu'] = 'Botones de menú ';
Sbi.locale.ln['sbi.olap.execution.menu.addToMenu'] = 'Añadir a menú';
Sbi.locale.ln['sbi.olap.execution.menu.addToToolbar'] = 'Añadir a la barra de herramientas ';

//===================================================================
//VERSION MANAGER
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all'] = 'No es posible eliminar todas las versiones ';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current'] = 'Puedes eliminar la versión actual';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.title'] = 'Seleccionar la versión a eliminar;
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.select.warning'] = 'Advertencia: esta operación podría durar algunos minutos. ';
Sbi.locale.ln['sbi.olap.control.controller.delete.version.ok'] = 'Versiones eliminadas correctamente.';
//Sbi.locale.ln['sbi.olap.control.controller.delete.version.error'] = 'Error al eliminar las versiones .';

//===================================================================
//OUTPUT
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.name'] = "Nombre de la tabla ";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.description'] = "¿Cuál es el nombre de la tabla para el proceso de salida?";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.row.delimiter'] = "Delimitador fila";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.filter.delimiter'] = "Delimitador campo";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.description'] = "Por favor, complete las opciones CSV";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table'] = "tabla";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv'] = "archivo";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type'] = "Tipo de salida";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.description'] = "Seleccionar el tipo de salida para el análisis. Esta operación llevará algunos minutos. Mientras tanto puede seguir trabajando en el cubo.";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.title'] = "Asistente de salida";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.version'] = "Versión para exportar ";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.ok'] = "Análisi exportado";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.error'] = "Se produjo un error exportando los datos";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.csv.window'] = "<h1> Exportar la salida en CSV </ h1> <p> Esta operación puede tardar algunos minutos. Puede continuar trabajando en el cubo </ p> ";




