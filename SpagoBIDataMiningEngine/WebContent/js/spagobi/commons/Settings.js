Ext.ns("Sbi.settings");

Sbi.settings.qbe = {
		queryBuilderPanel: {
			enableTreeToolbar: true,
			enableTreeTbPinBtn: true,
			enableTreeTbUnpinBtn: true,
			
			
			enableQueryTbExecuteBtn: true,
			enableQueryTbSaveBtn: true,
			enableQueryTbValidateBtn: false,
			
			enableCatalogueTbDeleteBtn: true,
			enableCatalogueTbAddBtn: false,
			enableCatalogueTbInsertBtn: true
		}

		, selectGridPanel: {
		
			enableTbHideNonvisibleBtn: true
			, enableTbAddCalculatedBtn: true
			, enableTbDeleteAllBtn: true
			, columns : {
				'entity': {hideable: true, hidden: false, sortable: false}
				, 'field': {hideable: true, hidden: false, sortable: false}
				, 'alias': {hideable: true, hidden: false, sortable: false}	
				, 'funct': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'group': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'order': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'visible': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'include': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'filter': {hideable: true, hidden: false, width: 50, sortable: false}
				, 'having': {hideable: true, hidden: false, width: 50, sortable: false}			
			}
		}
}; 