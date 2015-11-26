Ext.define('Ext.ux.touch.grid.feature.CheckboxSelection', {
    extend   : 'Ext.ux.touch.grid.feature.Abstract',
    requires : 'Ext.ux.touch.grid.feature.Abstract',

    config : {
        events : {
            headerEl : {
                tap : 'onHeaderTap'
            }
        },

        checkboxCls     : 'grid-checkbox',
        checkboxCellCls : 'checkbox-cell'
    },

    init : function(grid) {
        var columns = grid.getColumns(),
            cls     = this.getCheckboxCls(),
            cellCls = this.getCheckboxCellCls();

        columns = [
            {
                width     : 100,
                cls       : cellCls,
                dataIndex : 'checkbox_selection',
                headerRenderer : function() {
                    return '<div class="' + cls + '">&nbsp;</div>';
                },
                renderer  : function() {
                    return '<div class="' + cls + '">&nbsp;</div>';
                }
            }
        ].concat(columns);

        grid.setMode('MULTI');
        grid.setColumns(columns);
    },

    onHeaderTap : function(e) {
        var grid       = this.getGrid(),
            cls        = this.getCheckboxCellCls(),
            isCheckbox = !!e.getTarget('.' + cls);

        if (isCheckbox) {
            if (this.isAllSelected(grid)) {
                grid.deselectAll();
            } else {
                grid.selectAll();
            }
        }
    },

    isAllSelected : function(grid) {
        if (!grid) {
            grid = this.getGrid();
        }

        var store         = grid.getStore(),
            isAllSelected = true;

        store.each(function(record) {
            if (!grid.isSelected(record)) {
                isAllSelected = false;
            }
        });

        return isAllSelected;
    }
});