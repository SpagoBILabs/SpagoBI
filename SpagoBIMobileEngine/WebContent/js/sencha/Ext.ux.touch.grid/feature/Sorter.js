Ext.define('Ext.ux.touch.grid.feature.Sorter', {
    extend   : 'Ext.ux.touch.grid.feature.Abstract',
    requires : 'Ext.ux.touch.grid.feature.Abstract',

    config : {
        events : {
            grid    : {
                sort : 'updateHeaderIcons'
            },
            headerEl : {
                tap : 'handleHeaderTap'
            }
        },

        extraCls : 'sorter',

        asc  : 'x-grid-sort-asc',
        desc : 'x-grid-sort-desc'
    },

    init : function(grid) {
        var store  = grid.getStore(),
            sorted = store.isSorted ? store.isSorted() : store.getData().sorted;

        if (sorted) {
            if (grid.rendered) {
                grid.fireEvent('sort');
            } else {
                grid.on('painted', function() {
                    grid.fireEvent('sort');
                }, null, { single : true });
            }
        }
    },

    onDestroy: function() {
        var me     = this,
            grid   = me.grid,
            header = grid.header,
            el     = header.el;

        el.un({
            scope : me,
            tap   : me.handleHeaderTap
        });

        grid.un({
            scope : me,
            sort  : me.updateHeaderIcons
        });
    },

    isSortable: function(grid, column) {
        return !(grid.stopSort || column.sortable == false);
    },

    handleHeaderTap: function(e, t) {
        e.isStopped = true;

        var me        = this,
            grid      = me.getGrid(),
            columns   = grid.getColumns(),
            c         = 0,
            cNum      = columns.length,
            store     = grid.getStore(),
            el        = Ext.get(t),
            dataIndex = el.getAttribute('dataindex'),
            sorters   = store.getSorters(),
            sorter    = sorters[0],
            dir       = (sorter && (sorter.getProperty() === dataIndex)) ? sorter.getDirection() : 'DESC',
            column;

        for (; c < cNum; c++) {
            column = columns[c];

            if (column.dataIndex === dataIndex) {
                break;
            }
        }

        if (grid.fireEvent('beforesort', grid, column) === false || !me.isSortable(grid, column)) {
            return;
        }

        store.sort(dataIndex, dir === 'DESC' ? 'ASC' : 'DESC');
        
        if (store.getRemoteSort() === true) {
            store.load();
        } else {
            grid.refresh();
        }

        grid.fireEvent('sort');
    },

    updateHeaderIcons: function() {
        var me       = this,
            grid     = me.getGrid(),
            store    = grid.getStore(),
            sorters  = store.getSorters(),
            header   = grid.getHeader(),
            headerEl = header.element,
            s        = 0,
            sNum     = sorters.length,
            asc      = this.getAsc(),
            desc     = this.getDesc(),
            column, dataIndex, colEl, sorter, dir;

        me.clearSort();

        for (; s < sNum; s++) {
            sorter    = sorters[s];
            dataIndex = sorter.getProperty();
            dir       = sorter.getDirection();
            column    = grid.getColumn(dataIndex);
            colEl     = column.element;

            if (!colEl) {
                colEl = column.element = Ext.get(headerEl.down('div.x-grid-cell-hd[dataindex=' + dataIndex + ']'));
            }

            colEl && colEl.addCls(dir === 'DESC' ? desc : asc);
        }
    },

    clearSort : function() {
        var grid     = this.getGrid(),
            columns  = grid.getColumns(),
            header   = grid.getHeader(),
            headerEl = header.element,
            c        = 0,
            cNum     = columns.length,
            asc      = this.getAsc(),
            desc     = this.getDesc(),
            column, dataIndex, colEl;

        for (; c < cNum; c++) {
            column    = columns[c];
            dataIndex = column.dataIndex;
            colEl     = column.element;

            if (!colEl) {
                colEl = column.element = Ext.get(headerEl.down('div.x-grid-cell-hd[dataindex=' + dataIndex + ']'));
            }

            //column is hidden?
            if (colEl) {
                colEl.removeCls(asc).removeCls(desc);
            }
        }
    }
});