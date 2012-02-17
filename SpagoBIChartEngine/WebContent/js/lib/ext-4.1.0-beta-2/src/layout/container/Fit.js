/**
 * This is a base class for layouts that contain a single item that automatically expands to fill the layout's
 * container. This class is intended to be extended or created via the layout:'fit'
 * {@link Ext.container.Container#layout} config, and should generally not need to be created directly via the new keyword.
 *
 * Fit layout does not have any direct config options (other than inherited ones). To fit a panel to a container using
 * Fit layout, simply set `layout: 'fit'` on the container and add a single panel to it.
 *
 *     @example
 *     Ext.create('Ext.panel.Panel', {
 *         title: 'Fit Layout',
 *         width: 300,
 *         height: 150,
 *         layout:'fit',
 *         items: {
 *             title: 'Inner Panel',
 *             html: 'This is the inner panel content',
 *             bodyPadding: 20,
 *             border: false
 *         },
 *         renderTo: Ext.getBody()
 *     });
 *
 * If the container has multiple items, all of the items will all be equally sized. This is usually not
 * desired, so to avoid this, place only a **single** item in the container. This sizing of all items
 * can be used to provide a background {@link Ext.Img image} that is "behind" another item
 * such as a {@link Ext.view.View dataview} if you also absolutely position the items.
 */
Ext.define('Ext.layout.container.Fit', {

    /* Begin Definitions */
    extend: 'Ext.layout.container.Container',
    alternateClassName: 'Ext.layout.FitLayout',

    alias: 'layout.fit',

    /* End Definitions */

    itemCls: Ext.baseCSSPrefix + 'fit-item',
    targetCls: Ext.baseCSSPrefix + 'layout-fit',
    type: 'fit',
   
    /**
     * @cfg {Object} defaultMargins
     * If the individual contained items do not have a margins property specified or margin specified via CSS, the
     * default margins from this property will be applied to each item.
     *
     * This property may be specified as an object containing margins to apply in the format:
     *
     *     {
     *         top: (top margin),
     *         right: (right margin),
     *         bottom: (bottom margin),
     *         left: (left margin)
     *     }
     *
     * This property may also be specified as a string containing space-separated, numeric margin values. The order of
     * the sides associated with each value matches the way CSS processes margin values:
     *
     *   - If there is only one value, it applies to all sides.
     *   - If there are two values, the top and bottom borders are set to the first value and the right and left are
     *     set to the second.
     *   - If there are three values, the top is set to the first value, the left and right are set to the second,
     *     and the bottom is set to the third.
     *   - If there are four values, they apply to the top, right, bottom, and left, respectively.
     *
     */
    defaultMargins: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    },

    manageMargins: true,

    sizePolicies: {
        0: { setsWidth: 0, setsHeight: 0 },
        1: { setsWidth: 1, setsHeight: 0 },
        2: { setsWidth: 0, setsHeight: 1 },
        3: { setsWidth: 1, setsHeight: 1 }
    },

    getItemSizePolicy: function (item) {
        // this layout's sizePolicy is derived from its owner's sizeModel:
        var sizeModel = this.owner.getSizeModel(),
            mode = (sizeModel.width.shrinkWrap ? 0 : 1) |
                   (sizeModel.height.shrinkWrap ? 0 : 2);

       return this.sizePolicies[mode];
    },

    beginLayout: function(ownerContext) {
        var me = this,
            childItems,
            length,
            i = 0, itemContext;

        me.callParent(arguments);

        // Clear any dimensions which we set before calculation, in case the current settings affect the available size.
        // This particularly effects self-sizing containers such as fields, in which the target element is naturally sized,
        // and should not be stretched by a sized child item.
        if (ownerContext.targetContext.el.dom.tagName === 'TD') {
            childItems = ownerContext.childItems;
            length = childItems.length;
            for (i = 0; i < length; ++i) {
                itemContext = childItems[i];
                if (itemContext.heightModel.calculated) {
                    itemContext.target.el.dom.style.height = '';
                }
                if (itemContext.widthModel.calculated) {
                    itemContext.target.el.dom.style.width = '';
                }
            }
        }
    },

    // @private
    calculate : function (ownerContext) {
        var me = this,
            childItems = ownerContext.childItems,
            length = childItems.length,
            info = {
                contentWidth: 0,
                contentHeight: 0,
                length: length,
                ownerContext: ownerContext,
                targetSize: me.getContainerSize(ownerContext)
            },
            calcWidth = ownerContext.widthModel.shrinkWrap,
            calcHeight = ownerContext.heightModel.shrinkWrap,
            padWidth = 0,
            padHeight = 0,
            padding,
            i;

        for (i = 0; i < length; ++i) {
            info.index = i;
            me.fitItem(childItems[i], info);
        }
        
        if (calcHeight || calcWidth) {
            padding = ownerContext.targetContext.getPaddingInfo();
            
            if (calcWidth) {
                padWidth = padding.width;
            }
            
            if (calcHeight) {
                padHeight = padding.height;
            }
            
        }

        if (!ownerContext.setContentSize(info.contentWidth + padWidth, info.contentHeight + padHeight)) {
            me.done = false;
        }
    },

    fitItem: function (itemContext, info) {
        var me = this;

        info.margins = itemContext.getMarginInfo();
        info.needed = info.got = 0;

        me.fitItemWidth(itemContext, info);
        me.fitItemHeight(itemContext, info);

        // If not all required dimensions have been satisfied, we're not done.
        if (info.got != info.needed) {
            me.done = false;
        }
    },

    fitItemWidth: function (itemContext, info) {
        // Attempt to set only dimensions that are being controlled, not shrinkWrap dimensions
        if (info.ownerContext.widthModel.shrinkWrap) {
            info.contentWidth = Math.max(info.contentWidth, itemContext.getProp('width'));
        } else if (itemContext.widthModel.calculated) {
            ++info.needed;
            if (info.targetSize.gotWidth) {
                ++info.got;
                this.setItemWidth(itemContext, info);
            }
        }

        this.positionItemX(itemContext, info);
    },

    fitItemHeight: function (itemContext, info) {
        if (info.ownerContext.heightModel.shrinkWrap) {
            info.contentHeight = Math.max(info.contentHeight, itemContext.getProp('height'));
        } else if (itemContext.heightModel.calculated) {
            ++info.needed;
            if (info.targetSize.gotHeight) {
                ++info.got;
                this.setItemHeight(itemContext, info);
            }
        }

        this.positionItemY(itemContext, info);
    },

    positionItemX: function (itemContext, info) {
        var margins = info.margins;

        // Adjust position to account for configured margins or if we have multiple items
        // (all items should overlap):
        if (info.index || margins.left) {
            itemContext.setProp('x', margins.left);
        }
    },

    positionItemY: function (itemContext, info) {
        var margins = info.margins;

        if (info.index || margins.top) {
            itemContext.setProp('y', margins.top);
        }
    },

    setItemHeight: function (itemContext, info) {
        itemContext.setHeight(info.targetSize.height - info.margins.height);
    },

    setItemWidth: function (itemContext, info) {
        itemContext.setWidth(info.targetSize.width - info.margins.width);
    }
});

