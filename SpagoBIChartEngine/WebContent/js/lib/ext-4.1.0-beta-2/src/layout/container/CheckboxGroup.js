/**
 * This layout implements the column arrangement for {@link Ext.form.CheckboxGroup} and {@link Ext.form.RadioGroup}.
 * It groups the component's sub-items into columns based on the component's
 * {@link Ext.form.CheckboxGroup#columns columns} and {@link Ext.form.CheckboxGroup#vertical} config properties.
 */
Ext.define('Ext.layout.container.CheckboxGroup', {
    extend: 'Ext.layout.container.Container',
    alias: ['layout.checkboxgroup'],
    requires: ['Ext.layout.container.HBox'],

    childGeneration: 0,

    /**
     * @cfg {Boolean} [autoFlex=true]
     * By default,  CheckboxGroup allocates all available space to the configured columns meaning that
     * column are evenly spaced across the contaioner.
     *
     * To have each column only be wide enough to fit the container Checkboxes (or Radios), set `autoFlex` to `false`
     */
    autoFlex: true,

    type: 'checkboxgroup',

    childEls: [
        'innerCt'
    ],

    renderTpl: [
        '<table id="{ownerId}-innerCt" role="presentation" style="{tableStyle}"><tbody><tr>',
            '<tpl for="columns">',
                '<td class="{parent.colCls}" valign="top" style="{style}">',
                    '{% this.renderColumn(out,parent,xindex-1) %}',
                '</td>',
            '</tpl>',
        '</tr></tbody></table>'
    ],

    beginLayout: function(ownerContext) {
        var me = this,
            columns = me.columnEls,
            numCols,
            childItems,
            i, width,
            autoFlex = me.autoFlex,
            percentWidthColumns = false;

        me.callParent(arguments);
        childItems = ownerContext.childItems;

        // If the child items have changed since the last layout then we need to fixup
        // the association of items to columns:
        if (me.lastChildGeneration != me.childGeneration) {
            me.lastChildGeneration = me.childGeneration;
            me.fixColumns();
        }

        ownerContext.innerCtContext = ownerContext.getEl('innerCt', me);

        // Child items can grab a flex of the available width, so collect flexes
        Ext.layout.container.Box.prototype.cacheFlexes.call(me, ownerContext);

        // The columns config may be an array of widths. Any value < 1 is taken to be a fraction:
        if (!ownerContext.widthModel.shrinkWrap) {
            numCols = columns.length;

            // If columns is an array of numeric widths
            if (me.columnsArray) {
                for (i = 0; i < numCols; i++) {
                    width = me.owner.columns[i];
                    columns[i].style.width = (percentWidthColumns |= (width < 1)) ? (width * 100) + '%' : width + 'px';
                }
            }

            // Otherwise it's the *number* of columns, so use child item width settings
            else {
                // this won't run unless we force percentWidthColumns to false
                for (i = 0; i < numCols; i++) {
                    if (childItems[i].flex) {
                        columns[i].style.width = (childItems[i].flex / ownerContext.totalFlex * 100) + '%';
                        percentWidthColumns = true;
                    } else if (childItems[i].width) {
                        columns[i].style.width = childItems[i].width + 'px';
                        autoFlex = false;
                    } else if (me.evenColumns) {
                        columns[i].style.width = (1 / numCols * 100) + '%';
                        percentWidthColumns = true;
                    }
                }
            }

            // If the columns config was an array of column widths, allow table to auto width
            if (percentWidthColumns || autoFlex) {
                me.innerCt.dom.style.tableLayout = 'fixed';
                me.innerCt.dom.style.width = '100%';
            } else {
                me.innerCt.dom.style.tableLayout = 'auto';
                me.innerCt.dom.style.width = 'auto';
            }
        }
    },

    cacheElements: function () {
        var me = this;

        // Grab defined childEls
        me.callParent();

        // Grab columns TDs
        me.columnEls = me.innerCt.query('td.' + me.owner.groupCls);

        // we just rendered so the items are in the correct columns:
        me.lastChildGeneration = me.childGeneration;
    },

    /*
     * Just wait for the child items to all lay themselves out in the width we are configured
     * to make available to them. Then we can measure our height.
     */
    calculate: function(ownerContext) {
        var me = this;

        // The columnEls are widthed using their own width attributes, we just need to wait
        // for all children to have arranged themselves in that width, and then collect our height.
        if (!ownerContext.getDomProp('containerChildrenDone')) {
            me.done = false;
        } else {
            var targetContext = ownerContext.innerCtContext,
                widthShrinkWrap = ownerContext.widthModel.shrinkWrap,
                heightShrinkWrap = ownerContext.heightModel.shrinkWrap,
                shrinkWrap = heightShrinkWrap || widthShrinkWrap,
                table = targetContext.el.dom,
                targetPadding = shrinkWrap && targetContext.getPaddingInfo();

            if (widthShrinkWrap) {
                ownerContext.setContentWidth(table.offsetWidth + targetPadding.width, true);
            }

            if (heightShrinkWrap) {
                ownerContext.setContentHeight(table.offsetHeight + targetPadding.height, true);
            }
        }
    },

    doRenderColumn: function (out, renderData, columnIndex) {
        // Careful! This method is bolted on to the renderTpl so all we get for context is
        // the renderData! The "this" pointer is the renderTpl instance!

        var me = renderData.$layout,
            owner = me.owner,
            columnCount = renderData.columnCount,
            items = owner.items.items,
            itemCount = items.length,
            item, itemIndex, rowCount, increment, tree;

        // Example:
        //      columnCount = 3
        //      items.length = 10

        if (owner.vertical) {
            //        0   1   2
            //      +---+---+---+
            //    0 | 0 | 4 | 8 |
            //      +---+---+---+
            //    1 | 1 | 5 | 9 |
            //      +---+---+---+
            //    2 | 2 | 6 |   |
            //      +---+---+---+
            //    3 | 3 | 7 |   |
            //      +---+---+---+

            rowCount = Math.ceil(itemCount / columnCount); // = 4
            itemIndex = columnIndex * rowCount;
            itemCount = Math.min(itemCount, itemIndex + rowCount);
            increment = 1;
        } else {
            //        0   1   2
            //      +---+---+---+
            //    0 | 0 | 1 | 2 |
            //      +---+---+---+
            //    1 | 3 | 4 | 5 |
            //      +---+---+---+
            //    2 | 6 | 7 | 8 |
            //      +---+---+---+
            //    3 | 9 |   |   |
            //      +---+---+---+

            itemIndex = columnIndex;
            increment = columnCount;
        }

        for ( ; itemIndex < itemCount; itemIndex += increment) {
            item = items[itemIndex];
            me.configureItem(item);
            tree = item.getRenderTree();
            Ext.DomHelper.generateMarkup(tree, out);
        }
    },

    // Distribute child items between column elements according to row first or
    // column first order
    fixColumns: function () {
        var me = this,
            owner = me.owner,
            columns = me.columns,
            items = owner.items.items,
            columnCount = columns.length,
            itemCount = items.length,
            columnIndex, i, rowCount;

        if (owner.vertical) {
            columnIndex = -1; // first loop will increment this to 0
            rowCount = Math.ceil(itemCount / columnCount);

            for (i = 0; i < itemCount; ++i) {
                if (i % rowCount === 0) {
                    ++columnIndex;
                }
                columns[columnIndex].appendChild(items[i].el.dom);
            }
        } else {
            for (i = 0; i < itemCount; ++i) {
                columnIndex = i % columnCount;
                columns[columnIndex].appendChild(items[i].el.dom);
            }
        }
    },

    /**
     * Returns the number of columns in the checkbox group.
     * @private
     */
    getColumnCount: function() {
        var me = this,
            owner = me.owner,
            ownerColumns = owner.columns;

        // Our columns config is an array of numeric widths.
        // Calculate our total width
        if (me.columnsArray) {
            return ownerColumns.length;
        }

        if (Ext.isNumber(ownerColumns)) {
            return ownerColumns;
        }
        return owner.items.length;
    },

    getItemSizePolicy: function (item) {
        return this.autoSizePolicy;
    },

    getRenderData: function () {
        var me = this,
            data = me.callParent(),
            owner = me.owner,
            i = 0, columns = me.getColumnCount(),
            items = owner.items.items,
            width,
            childItem, column, totalFlex,
            autoFlex = me.autoFlex,
            percentWidthColumns = false;

        // Calculate total flex
        if (!me.columnsArray) {
            for (; i < columns; i++) {
                if (items[i].flex) {
                    totalFlex += items[i].flex;
                }
            }
        }

        data.colCls = owner.groupCls;
        data.columnCount = me.getColumnCount();

        data.columns = [];
        for (i = 0; i < columns; i++) {
            column = (data.columns[i] = {});

            if (me.columnsArray) {
                width = me.owner.columns[i];
                column.style = 'width:' + ((percentWidthColumns |= (width < 1)) ? (width * 100) + '%' : width + 'px');
            } else {
                childItem = items[i];
                if (childItem.flex) {
                    column.style = 'width:' + (childItem.flex / totalFlex * 100) + '%';
                    percentWidthColumns = true;
                } else if (childItem.width) {
                    column.style = 'width:' + childItem.width + 'px';
                    autoFlex = false;
                } else if (me.evenColumns) {
                    column.style = 'width:' + (1 / columns * 100) + '%';
                    percentWidthColumns = true;
                }
            }
        }

        // If the columns config was an array of column widths, allow table to auto width
        data.tableStyle = percentWidthColumns || autoFlex ? 'table-layout:fixed;width:100%' : '';

        return data;
    },

    // Overridden method from AbstractContainer.
    getRenderTarget: function() {
        return this.innerCt;
    },

    initLayout: function () {
        var me = this,
            owner = me.owner;

        me.columnsArray = Ext.isArray(owner.columns);
        me.evenColumns = Ext.isNumber(owner.columns);

        me.callParent();
    },

    // Always valid. beginLayout ensures the encapsulating elements of all children are in the correct place
    isValidParent: function() {
        return true;
    },

    onAdd: function () {
        this.callParent(arguments);
        ++this.childGeneration;
    },

    onRemove: function () {
        this.callParent(arguments);
        ++this.childGeneration;
    },

    setupRenderTpl: function (renderTpl) {
        this.callParent(arguments);

        renderTpl.renderColumn = this.doRenderColumn;
    }
}, function() {
    this.prototype.names = Ext.layout.container.HBox.prototype.names;
    this.prototype.getNames = Ext.layout.container.HBox.prototype.getNames;
});