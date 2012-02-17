/**
 * @private
 *
 * This class is used only by the grid's HeaderContainer docked child.
 *
 * It adds the ability to shrink the vertical size of the inner container element back if a grouped
 * column header has all its child columns dragged out, and the whole HeaderContainer needs to shrink back down.
 *
 * Also, after every layout, after all headers have attained their 'stretchmax' height, it goes through and calls
 * `setPadding` on the columns so that they lay out correctly.
 */
Ext.define('Ext.grid.ColumnLayout', {
    extend: 'Ext.layout.container.HBox',
    alias: 'layout.gridcolumn',
    type : 'gridcolumn',

    reserveOffset: false,

    shrinkToFit: false,

    firstHeaderCls: Ext.baseCSSPrefix + 'column-header-first',
    lastHeaderCls: Ext.baseCSSPrefix + 'column-header-last',

    // Collect the height of the table of data upon layout begin
    beginLayout: function (ownerContext) {
        var me = this,
            owner = me.owner,
            grid = owner.up('[scrollerOwner]'),
            view = grid.view,
            i = 0,
            items = me.getVisibleItems(),
            len = items.length,
            item;

        me.callParent(arguments);

        // Unstretch child items before the layout which stretches them.
        for (; i < len; i++) {
            item = items[i];
            item.removeCls([me.firstHeaderCls, me.lastHeaderCls]);
            item.el.setStyle({
                height: 'auto'
            });
            item.titleEl.setStyle({
                height: 'auto',
                paddingTop: '0'
            });
        }

        // Add special first/last classes
        if (len > 0) {
            items[0].addCls(me.firstHeaderCls);
            items[len - 1].addCls(me.lastHeaderCls);
        }

        // If the owner is the grid's HeaderContainer, and the UI displays old fashioned scrollbars and there is a rendered View with data in it,
        // collect the View context to interrogate it for overflow, and possibly invalidate it if there is overflow
        if (!me.owner.isHeader && Ext.getScrollbarSize().width && !grid.collapsed && view && view.rendered && (ownerContext.viewTable = view.el.child('table', true))) {
            ownerContext.viewContext = ownerContext.context.getCmp(view);
        }
    },

    roundFlex: function(width) {
        return Math.floor(width);
    },
    
    /**
     * @private
     * Local getContainerSize implementation accounts for vertical scrollbar in the view.
     */
    getContainerSize: function(ownerContext) {
        var me = this,
            result = me.callParent(arguments),
            viewContext = ownerContext.viewContext,
            viewHeight;

        // If we've collected a viewContext, we will also have the table height
        // If there's overflow, the View must be narrower to accomodate the scrollbar
        if (viewContext && !viewContext.heightModel.shrinkWrap) {
            viewHeight = viewContext.getProp('height');
            if (isNaN(viewHeight)) {
                me.done = false;
            } else if (ownerContext.state.tableHeight > viewHeight) {
                result.width -= Ext.getScrollbarSize().width;
                ownerContext.state.parallelDone = false;
                viewContext.invalidate();
            }
        }

// TODO - flip the initial assumption to "we have a vscroll" to avoid the invalidate in most
// cases (and the expensive ones to boot)

        return result;
    },

    calculate: function(ownerContext) {
        var me = this,
            viewContext = ownerContext.viewContext;

        // Collect the height of the data table if we need it to determine overflow
        if (viewContext && !ownerContext.state.tableHeight) {
            ownerContext.state.tableHeight = ownerContext.viewTable.offsetHeight;
        }
        me.callParent(arguments);
    },
 
    completeLayout: function(ownerContext) {
        var me = this,
            owner = me.owner,
            state = ownerContext.state,
            needsInvalidate = false,
            calculated = me.sizeModels.calculated,
            childItems, len, i, childContext, item;

        me.callParent(arguments);

        // If we have not been through this already, and the owning Container is configured
        // forceFit, is not a group column and and there is a valid width, then convert
        // widths to flexes, and loop back.
        if (!state.flexesCalculated && owner.forceFit && !owner.isHeader) {
            childItems = ownerContext.childItems;
            len = childItems.length;

            for (i = 0; i < len; i++) {
                childContext = childItems[i];
                item = childContext.target;

                // For forceFit, just use allocated width as the flex value, and the proportions
                // will end up the same whatever HeaderContainer width they are being forced into.
                if (item.width) {
                    item.flex = ownerContext.childItems[i].flex = item.width;
                    delete item.width;
                    childContext.widthModel = calculated;
                    needsInvalidate = true;
                }
            }

            // Recalculate based upon all columns now being flexed instead of sized.
            // Set flag, so that we do not do this infinitely
            if (needsInvalidate) {
                me.cacheFlexes(ownerContext);
                ownerContext.invalidate({
                    state: {
                        flexesCalculated: true
                    }
                });
            }
        }
    },

    finalizeLayout: function() {
        var me = this,
            i = 0,
            items,
            len,
            headerHeight;

        // Set up padding in items
        items = me.getVisibleItems();
        len = items.length;
        headerHeight = me.getRenderTarget().getViewSize().height;
        for (; i < len; i++) {
            items[i].setPadding(headerHeight);
        }
    },

    // FIX: when flexing we actually don't have enough space as we would
    // typically because of the scrollOffset on the GridView, must reserve this
    publishInnerCtSize: function(ownerContext) {
        var me = this,
            plan = ownerContext.state.boxPlan,
            size = plan.targetSize;

        // InnerCt MUST stretch to accommodate all columns so that left/right scrolling is enabled in the header container.
        if (!me.owner.isHeader) {
            size.width = ownerContext.getProp('contentWidth') + Ext.getScrollbarSize().width;
        }

        return me.callParent(arguments);
    }
});