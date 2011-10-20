Ext.ns("Sbi.settings");

Sbi.settings.qbe = {
		
		
		// TODO move this to a dedicated file
		/*
		constants : {
			// select clause field types (twins java constants are defined in class ISelectField)
			FIELD_TYPE_SIMPLE: 'simple.field'
			, FIELD_TYPE_CALCULATED: 'calculated.field'
			, FIELD_TYPE_INLINE_CALCULATED: 'inline.calculated.field'
			
			// where clause operand types (twins java constants are defined in class AbstractStatement)
			, OPERAND_TYPE_STATIC_VALUE: 'static.value'
			, OPERAND_TYPE_SUBQUERY: 'subquery'
			, OPERAND_TYPE_PARENT_FIELD: 'parent.query.field'
			, OPERAND_TYPE_SIMPLE_FIELD: 'simple.field'
			, OPERAND_TYPE_CALCULATED_FIELD: 'calculated.field'
			, OPERAND_TYPE_INLINE_CALCULATED_FIELD: 'inline.calculated.field'
				
			// tree field types (twins java constants are defined in class ExtJsQbeTreeBuilder)
			, NODE_TYPE_ENTITY: 'entity'
			, NODE_TYPE_SIMPLE_FIELD: 'field'
			, NODE_TYPE_CALCULATED_FIELD: 'calculatedField'
			, NODE_TYPE_INLINE_CALCULATED_FIELD: 'inLineCalculatedField'
			
		}

		*/

		
		queryBuilderPanel: {
			enableTreeToolbar: true,
			enableTreeTbPinBtn: true,
			enableTreeTbUnpinBtn: true,
			enableTreeTbSaveBtn: true,
			
			enableQueryTbExecuteBtn: true,
			enableQueryTbSaveBtn: false,
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
			, enableRowRemoveBtn: false
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
		
		, crossTab: {
			 columnWidth: 80
			, columnWidthPercent: 105
			, rowHeight: 25
			, fontSize: 10
			, percentageFontSize: 9
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

Sbi.settings.worksheet = {
		runtime : {
			table : {
				height: 400
			}
		}
};