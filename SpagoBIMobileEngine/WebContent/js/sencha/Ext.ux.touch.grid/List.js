Ext.define('Ext.ux.touch.grid.List', {
    extend : 'Ext.dataview.List',
    xtype  : 'touchgridpanel',

    requires : [
        'Ext.ux.touch.grid.feature.Feature',
        'Ext.Toolbar'
    ],
    mixins   : ['Ext.ux.touch.grid.feature.Feature'],

    config : {
        /*
         * @property {String|Function} [rowCls=null]
         *  Either a string (or a Function that returns a string) designating the class
         *  string to be applied to row.  Current record values are passed as the first argument.
         */
        rowCls : null,

        /*
         * @property {String|Function} [rowStyle=null]
         *  Either a string (or a Function that returns a string) designating the style
         *  to be applied to each row. Current record values are passed as the first argument.
         */
        rowStyle : null,

        columns : [
            {}
        ],
        cls     : 'touchgridpanel',
        header  : {
            xtype  : 'toolbar',
            docked : 'top',
            cls    : 'x-grid-header'
        },
        itemTpl : false,
        itemCls : 'x-touchgrid-item'
    },

    
    _getRowClsFn : null,
    _getRowStyleFn : null,
    
    
    constructor : function (config) {
        var me = this,
            features = me.features = config.features || me.config.features || me.features;
        
        me._getRowClsFn = Ext.bind(me.getRowCls, me);
        me._getRowStyleFn = Ext.bind(me.getRowStyle, me);

        me.callParent([config]);

        if (typeof me.initFeatures === 'function' && typeof config.features === 'object') {
            me.initFeatures(features, 'constructor');
        }

        me.setWidth(me._buildWidth());
    },

    initialize : function () {
        var me = this;

        me.callParent();

        if (typeof me.initFeatures === 'function' && typeof me.features === 'object') {
            me.initFeatures(me.features, 'initialize');
        }
    },

    applyColumns : function (columns) {
        var c = 0,
            cLen = columns.length,
            newColumns = [];

        for (; c < cLen; c++) {
            newColumns.push(
                Ext.merge({}, columns[c])
            );
        }

        return newColumns;
    },

    updateColumns : function () {
        if (this._itemTpl) {
            this.setItemTpl(null);
        }
    },

    refreshScroller : function () {
        var scroller = this.getScrollable().getScroller();

        scroller.refresh();
    },

    applyHeader : function (config) {
        Ext.apply(config, {
            docked : 'top',
            cls    : 'x-grid-header'
        });

        return Ext.factory(config, Ext.Toolbar);
    },

    updateHeader : function (header) {
        this.insert(0, header);
    },

    _buildWidth : function () {
        var me = this,
            columns = me.getColumns(),
            c = 0,
            cNum = columns.length,
            retWidth = 0,
            stop = false,
            defaults = this.getDefaults() || {},
            column, width;

        for (; c < cNum; c++) {
            column = columns[c];
            width = column.width || defaults.column_width;

            if (!Ext.isNumber(width)) {
                stop = true;
                break;
            }

            retWidth += width;
        }

        return stop ? undefined : retWidth;
    },

    _defaultRenderer : function (value) {
        return Ext.isEmpty(value) ? '&nbsp;' : value;
    },

    applyItemTpl : function (tpl) {
        if (!tpl) {
            tpl = this._buildTpl(this.getColumns(), false);
        }

        if (!(tpl instanceof Ext.XTemplate)) {
            tpl = Ext.create('Ext.XTemplate', tpl.tpl, tpl.renderers);
        }

        return tpl;
    },

    _updateItemTpl: function(newTpl) {
        var listItems = this.listItems,
            ln = listItems.length || 0,
            store = this.getStore(),
            i, listItem;

        for (i = 0; i < ln; i++) {
            listItem = listItems[i];
            listItem.setTpl(newTpl);
        }

        if (store && store.getCount()) {
            this.doRefresh();
        }
    },

    updateItemTpl : function () {
        this._updateItemTpl(this.getItemTpl());

        var header = this.getHeader(),
            html = this._buildTpl(this.getColumns(), true);
        
        header.setHtml(html.tpl);

        this.refresh();
    },
    
    _buildTpl : function (columns, header) {
        var me = this,
            tpl = [],
            c = 0,
            cNum = columns.length,
            basePrefix = Ext.baseCSSPrefix,
            renderers = {},
            defaults = me.getDefaults() || {},
            rowCls = me.getRowCls(),
            rowStyle = me.getRowStyle(),
            column, hidden, css, styles, attributes, width, renderer, rendererName, innerText;

        for (; c < cNum; c++) {
            column = columns[c];
            hidden = column.hidden;

            if (hidden) {
                continue;
            }

            css = [basePrefix + 'grid-cell'];
            styles = [];
            attributes = ['dataindex="' + column.dataIndex + '"'];
            width = column.width || defaults.column_width;
            renderer = column[header ? 'headerRenderer' : 'renderer'] || this._defaultRenderer;
            rendererName = column.dataIndex + '_renderer';

            styles.push('line-height: '+this.getItemHeight()+'px;');

            if (header) {
                css.push(basePrefix + 'grid-cell-hd');
                innerText = renderer.call(this, column.header);
            } else {
                innerText = '{[this.' + rendererName + '(values.' + column.dataIndex + ', values)]}';

                if (column.style) {
                    styles.push(column.style);
                }

                renderers[rendererName] = renderer;
            }

            if (column.cls) {
                css.push(column.cls);
            }

            if (width) {
                styles.push('width: ' + width + (Ext.isString(width) ? '' : 'px') + ';');
            }

            if (styles.length > 0) {
                attributes.push('style="' + styles.join(' ') + '"');
            }

            tpl.push('<div class="' + css.join(' ') + '" ' + attributes.join(' ') + '>' + innerText + '</div>');
        }

        tpl = tpl.join('');
        
        if (!header) {
            var rcls = null,
                rstl = null;
            
            if (Ext.isFunction(rowCls) || Ext.isString(rowCls)) {
                renderers._getRowCls = me._getRowClsFn;
                rcls = 'class="' + basePrefix + 'grid-row {[this._getRowCls(values) || \'\']}"';
            }
        
            if (Ext.isFunction(rowStyle) || Ext.isString(rowStyle)) {
                renderers._getRowStyle = me._getRowStyleFn;
                rstl = 'style="{[this._getRowStyle(values) || \'\']}"';
            }
        
            if (rcls || rstl) {
                tpl = '<div' + (rcls ? ' ' + rcls : '') + (rstl ? ' ' + rstl : '') + '>' + tpl + '</div>';
            }
        }

        return {
            tpl       : tpl,
            renderers : renderers
        };
    },

    getRowCls : function (data) {
        var me = this,
            rowCls = me._rowCls;

        if (typeof rowCls === 'function') {
            return rowCls.call(me, data);
        }

        return rowCls;
    },

    getRowStyle : function (data) {
        var me = this,
            rowStyle = me._rowStyle;

        if (typeof rowStyle === 'function') {
            return rowStyle.call(me, data);
        }

        return rowStyle;
    },

    getColumn : function (dataIndex) {
        var me = this,
            columns = me.getColumns(),
            c = 0,
            cNum = columns.length,
            column;

        for (; c < cNum; c++) {
            column = columns[c];

            if (column.dataIndex === dataIndex) {
                return column;
            }
        }

        return null;
    },

    toggleColumn : function (index, hide) {
        var columns = this.getColumns(),
            column = columns[index];

        if (!Ext.isDefined(hide)) {
            hide = !column.hidden;
        }

        column.hidden = hide;

        this.setItemTpl(null); //trigger new tpl on items and header
        this.refresh();
    },

    hideColumn : function (index) {
        this.toggleColumn(index, true);
    },

    showColumn : function (index) {
        this.toggleColumn(index, false);
    }
});