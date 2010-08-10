Ext.ns("Sbi.settings");

Sbi.settings.qbe = {
		queryBuilderPanel: {
			enableTreeToolbar: true,
			enableTreeTbPinBtn: true,
			enableTreeTbUnpinBtn: true,
			enableTreeTbSaveBtn: true,
			
			enableQueryTbExecuteBtn: true,
			enableQueryTbSaveBtn: true,
			enableQueryTbValidateBtn: false,
			
			enableCatalogueTbDeleteBtn: true,
			enableCatalogueTbAddBtn: true,
			enableCatalogueTbInsertBtn: true
		}

		, selectGridPanel: {
			gridHeight: 250
			, enableTbAddCalculatedBtn: true 
			, enableTbHideNonvisibleBtn: true
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
		
		, filterGridPanel: {
			gridHeight: 250
			, enableTbExpWizardBtn: true
			, enableTbRemoveAllFilterBtn: true
			, enableTbAddFilterBtn: true
			, columns : {
				'filterId': {hideable: true, hidden: false, sortable: false, editable: true}
				, 'filterDescripion': {hideable: true, hidden: true, sortable: false, editable: true}
				, 'leftOperandDescription': {hideable: false, hidden: false, sortable: false, editable: true}
				, 'leftOperandType': {hideable: true, hidden: true, sortable: false, editable: true}
				, 'operator': {hideable: false, hidden: false, sortable: false, editable: true}
				, 'rightOperandDescription': {hideable: false, hidden: false, sortable: false, editable: true}				
				, 'rightOperandType': {hideable: true, hidden: true, sortable: false, editable: true}
				, 'booleanConnector': {hideable: true, hidden: false, sortable: false, editable: true}
				, 'deleteButton': {hideable: true, hidden: true, sortable: false, editable: true}
				, 'promptable': {hideable: true, hidden: false, sortable: false, editable: true}				
			}
			, lookupValuesSeparator: ' ---- '
		}
		
		, havingGridPanel: {
			gridHeight: 250
		}
		
		, dataMartStructurePanel: {
			enableTreeContextMenu: true
		}
};

Sbi.settings.formviewer = {
		staticClosedXORFiltersPanel: {
			width: 300
			//, height: 150
		}
		, staticClosedOnOffFiltersPanel: {
			width: 300
			//, height: 150
		}
		, staticOpenFiltersPanel: {
			valueDelimiter: '--!;;;;;!--'
		}
};