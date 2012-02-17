/**
 * Base Class for HBoxLayout and VBoxLayout Classes. Generally it should not need to be used directly.
 */
Ext.define('Ext.layout.container.Box', {

    /* Begin Definitions */

    alias: ['layout.box'],
    extend: 'Ext.layout.container.Container',
    alternateClassName: 'Ext.layout.BoxLayout',

    requires: [
        'Ext.layout.container.boxOverflow.None',
        'Ext.layout.container.boxOverflow.Menu',
        'Ext.layout.container.boxOverflow.Scroller',
        'Ext.util.Format',
        'Ext.dd.DragDropManager'
    ],

    /* End Definitions */

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
     */
    defaultMargins: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    },

    /**
     * @cfg {String} padding
     * Sets the padding to be applied to all child items managed by this layout.
     *
     * This property must be specified as a string containing space-separated, numeric padding values. The order of the
     * sides associated with each value matches the way CSS processes padding values:
     *
     *   - If there is only one value, it applies to all sides.
     *   - If there are two values, the top and bottom borders are set to the first value and the right and left are
     *     set to the second.
     *   - If there are three values, the top is set to the first value, the left and right are set to the second,
     *     and the bottom is set to the third.
     *   - If there are four values, they apply to the top, right, bottom, and left, respectively.
     */
    padding: 0,

    /**
     * @cfg {String} pack
     * Controls how the child items of the container are packed together. Acceptable configuration values for this
     * property are:
     *
     *   - **start** - child items are packed together at **left** side of container (*default**)
     *   - **center** - child items are packed together at **mid-width** of container
     *   - **end** - child items are packed together at **right** side of container
     */
    pack: 'start',

    /**
     * @cfg {Number} flex
     * This configuration option is to be applied to **child items** of the container managed by this layout. Each child
     * item with a flex property will be flexed **horizontally** according to each item's **relative** flex value
     * compared to the sum of all items with a flex value specified. Any child items that have either a flex = 0 or flex
     * = undefined will not be 'flexed' (the initial size will not be changed).
     */
    flex: undefined,
    
    /**
     * @cfg {String/Ext.Component} stretchMaxPartner
     * Allows stretchMax calculation to take into account the max perpendicular size (height for HBox layout and width
     * for VBox layout) of another Box layout when calculating its maximum perpendicular child size.
     *
     * If specified as a string, this may be either a known Container ID, or a ComponentQuery selector which is rooted
     * at this layout's Container (ie, to find a sibling, use `"^>#siblingItemId`).
     */
    stretchMaxPartner: undefined,

    type: 'box',
    scrollOffset: 0,
    itemCls: Ext.baseCSSPrefix + 'box-item',
    targetCls: Ext.baseCSSPrefix + 'box-layout-ct',
    innerCls: Ext.baseCSSPrefix + 'box-inner',

    // availableSpaceOffset is used to adjust the availableWidth, typically used
    // to reserve space for a scrollbar
    availableSpaceOffset: 0,

    // whether or not to reserve the availableSpaceOffset in layout calculations
    reserveOffset: true,

    manageMargins: true,

    childEls: [
        'innerCt'
    ],

    renderTpl: [
        '{%this.renderOverflowPrefix(out, values)%}',
        '<div id="{ownerId}-innerCt" class="{[values.$layout.innerCls]} {[values.$layout.overflowHandler.getOverflowCls()]}" role="presentation">',
            '{%this.renderBody(out, values)%}',
        '</div>',
        '{%this.renderOverflowSuffix(out, values)%}',
        {
            // Output the "before" scroller's markup into the output stream
            renderOverflowPrefix: function(out, values) {
                var overflowPrefixConfig = values.$comp.layout.overflowHandler.getPrefixConfig();

                if (overflowPrefixConfig) {
                    Ext.DomHelper.generateMarkup(overflowPrefixConfig, out);
                }
            },

            // Output the "after" scroller's markup into the output stream
            renderOverflowSuffix: function(out, values) {
                var overflowSuffixConfig = values.$comp.layout.overflowHandler.getSuffixConfig();

                if (overflowSuffixConfig) {
                    Ext.DomHelper.generateMarkup(overflowSuffixConfig, out);
                }
            },
            disableFormats: true
        }
    ],

    constructor: function(config) {
        var me = this,
            type;

        me.callParent(arguments);

        // The sort function needs access to properties in this, so must be bound.
        me.flexSortFn = Ext.Function.bind(me.flexSort, me);

        me.initOverflowHandler();

        type = typeof me.padding;
        if (type == 'string' || type == 'number') {
            me.padding = Ext.util.Format.parseBox(me.padding);
            me.padding.height = me.padding.top  + me.padding.bottom;
            me.padding.width  = me.padding.left + me.padding.right;
        }
    },

    getNames: function () {
        return this.names;
    },

    getItemSizePolicy: function (item) {
        var policy = this.sizePolicy,
            align = this.align,
            key = (align == 'stretchmax' || align == 'stretch') ? align : '';

        if (item.flex) {
            policy = policy.flex;
        }

        return policy[key];
    },

    flexSort: function (a, b) {
        var maxWidthName = 'max' + this.getNames().widthCap,
            infiniteValue = Infinity;

        a = a.target[maxWidthName] || infiniteValue;
        b = b.target[maxWidthName] || infiniteValue;

        // IE 6/7 Don't like Infinity - Infinity...
        if (!isFinite(a) && !isFinite(b)) {
            return 0;
        }

        return a - b;
    },

    isItemBoxParent: function (itemContext) {
        return true;
    },

    isItemShrinkWrap: function (item) {
        return true;
    },

    // Sort into *descending* order.
    minSizeSortFn: function(a, b) {
        return b.available - a.available;
    },

    roundFlex: function(width) {
        return Math.ceil(width);
    },

    /**
     * @private
     * Called by an owning Panel before the Panel begins its collapse process.
     * Most layouts will not need to override the default Ext.emptyFn implementation.
     */
    beginCollapse: function(child) {
        var me = this;

        if (me.direction === 'vertical' && child.collapsedVertical()) {
            child.collapseMemento.capture(['flex']);
            delete child.flex;
        } else if (me.direction === 'horizontal' && child.collapsedHorizontal()) {
            child.collapseMemento.capture(['flex']);
            delete child.flex;
        }
    },

    /**
     * @private
     * Called by an owning Panel before the Panel begins its expand process.
     * Most layouts will not need to override the default Ext.emptyFn implementation.
     */
    beginExpand: function(child) {

        // Restore's the flex if we used to be flexed before
        child.collapseMemento.restore(['flex']);
    },

    beginLayout: function (ownerContext) {
        var me = this,
            smp = me.owner.stretchMaxPartner,
            style = me.innerCt.dom.style,
            names = me.getNames(),
            state = ownerContext.state,
            plan = state.boxPlan || (state.boxPlan = {});

        // this must happen before callParent to allow the overflow handler to do its work
        // that can effect the childItems collection...
        me.overflowHandler.beginLayout(ownerContext);

        // get the contextItem for our stretchMax buddy:
        if (typeof smp === 'string') {
            smp = Ext.getCmp(smp) || me.owner.query(smp)[0];
        }

        ownerContext.stretchMaxPartner = smp && ownerContext.context.getCmp(smp);

        me.callParent(arguments);

        ownerContext.innerCtContext = ownerContext.getEl('innerCt', me);

        // Capture whether the owning Container is scrolling in the parallel direction
        plan.scrollParallel = (me.owner.autoScroll || me.owner['overflow' + names.x.toUpperCase()]);

        // If we *are* scrolling parallel, capture the scroll position
        if (plan.scrollParallel) {
            state.scrollPos = me.owner.getTargetEl().dom['scroll' + names.leftCap];
        }

        // don't allow sizes burned on to the innerCt to influence measurements:
        style.width = '';
        style.height = '';

        me.cacheFlexes(ownerContext);
    },

    beginLayoutCycle: function (ownerContext, firstCycle) {
        var me = this,
            align = me.align,
            names = me.getNames(),
            pack = me.pack,
            heightModelName = names.height + 'Model',
            childItems, childContext, i, length, shrinkWrap;

        // this must happen before callParent to allow the overflow handler to do its work
        // that can effect the childItems collection...
        me.overflowHandler.beginLayoutCycle(ownerContext, firstCycle);

        me.callParent(arguments);

        // Cache several of our string concat/compare results (since width/heightModel can
        // change if we are invalidated, we cannot do this in beginLayout)

        ownerContext.parallelSizeModel      = ownerContext[names.width + 'Model'];
        ownerContext.perpendicularSizeModel = ownerContext[heightModelName];

        ownerContext.boxOptions = {
            align: align = {
                stretch:    align == 'stretch',
                stretchmax: align == 'stretchmax',
                center:     align == names.center
            },
            pack: pack = {
                center: pack == 'center',
                end:    pack == 'end'
            }
        };

        // Consider an hbox w/stretch which means "assign all items the container's height".
        // The spirit of this request is make all items the same height, but when shrinkWrap
        // height is also requested, the height of the tallest item determines the height.
        // This is exactly what the stretchmax option does, so we jiggle the flags here to
        // act as if stretchmax were requested.

        if (align.stretch && ownerContext.perpendicularSizeModel.shrinkWrap) {
            align.stretchmax = true;
            align.stretch = false;
        }

        // In our example hbox, packing items to the right (end) or center can only work if
        // there is a container width. So, if we are shrinkWrap, we just turn off the pack
        // options for the run.

        if (ownerContext.parallelSizeModel.shrinkWrap) {
            pack.center = pack.end = false;
        }

        // StretchMax
        // ==========
        // This fellow is more interesting than just about any other layout. Consider an
        // hbox w/stretchmax. The idea is to first first allow the children to perform their
        // natural (shrinkWrap) layout and then to set the height of all children to the
        // the maximum height of any child. In terms of layout, this equates to starting
        // with all children as heightModel.shrinkWrap or heightModel.configured.
        //
        // Then down in calculateStretcMax we flip the shrinkWrap children to layout-sized
        // heightModel.calculated and set their height to the maxHeight. If we ever have to
        // start over due to invalidate, we must restart back at the beginning (which is
        // why this logic is not in beginLayout).

        if (align.stretchmax) {
            childItems = ownerContext.childItems;
            length = childItems.length;
            shrinkWrap = me.sizeModels.shrinkWrap;

            for (i = 0; i < length; ++i) {
                childContext = childItems[i];
                if (!childContext[heightModelName].configured) {
                    childContext[heightModelName] = shrinkWrap;
                }
            }
        }
    },

    /**
     * This method is called to (re)cache our understanding of flexes. This happens during beginLayout and may need to
     * be called again if the flexes are changed during the layout (e.g., like ColumnLayout).
     * @param {Object} ownerContext
     * @protected
     */
    cacheFlexes: function (ownerContext) {
        var names = this.getNames(),
            widthModelName = names.width + 'Model',
            totalFlex = 0,
            childItems = ownerContext.childItems,
            i = childItems.length,
            flexedItems = [],
            minWidth = 0,
            minWidthName = 'min' + names.widthCap,
            child, childContext, flex;

        while (i--) {
            childContext = childItems[i];

            // if the child has a flex it could be "accidental" (typically via "defaults"),
            // so just check widthModel to see if we are the sizing layout. If so, copy
            // the flex from the item to the contextItem and add it to totalFlex
            //
            if (childContext[widthModelName].calculated) {
                child = childContext.target;
                childContext.flex = flex = child.flex;
                if (flex) {
                    totalFlex += flex;
                    flexedItems.push(childContext);
                    minWidth += child[minWidthName] || 0;
                }
            }
            // the above means that "childContext.flex" is properly truthy/falsey, which is
            // often times quite convenient...
        }

        ownerContext.flexedItems = flexedItems;
        ownerContext.flexedMinSize = minWidth;
        ownerContext.totalFlex = totalFlex;

        // The flexed boxes need to be sorted in ascending order of maxSize to work properly
        // so that unallocated space caused by maxWidth being less than flexed width can be
        // reallocated to subsequent flexed boxes.
        Ext.Array.sort(flexedItems, this.flexSortFn);
    },

    calculate: function(ownerContext) {
        var me = this,
            targetSize = me.getContainerSize(ownerContext),
            names = me.getNames(),
            state = ownerContext.state,
            plan = state.boxPlan || (state.boxPlan = {});

        if (!ownerContext.parallelSizeModel.shrinkWrap && !targetSize['got' + names.widthCap]) {
            // If we are not widthModel.shrinkWrap, we need the width before we can lay out
            // boxes:
            me.done = false;
            return;
        }

        plan.targetSize = targetSize;

        if (!state.parallelDone) {
            state.parallelDone = me.calculateParallel(ownerContext, names, plan);
        }

        if (!state.perpendicularDone) {
            state.perpendicularDone = me.calculatePerpendicular(ownerContext, names, plan);
        }

        // Fix for left and right docked Components in a dock component layout. This is for docked Headers and docked Toolbars.
        // Older Microsoft browsers do not size a position:absolute element's width to match its content.
        // So in this case, in the publishInnerCtSize method we may need to adjust the size of the owning Container's element explicitly based upon
        // the discovered max width. So here we put a calculatedWidth property in the metadata to facilitate this.
        if (me.owner.dock && (Ext.isIE6 || Ext.isIE7 || Ext.isIEQuirks) && !me.owner.width && !me.horizontal) {
            plan.isIEVerticalDock = true;
            plan.calculatedWidth = plan.maxSize + ownerContext.getPaddingInfo().width + ownerContext.getFrameInfo().width;
        }

        if (!state.parallelDone || !state.perpendicularDone) {
            me.done = false;
        } else {
            me.publishInnerCtSize(ownerContext, me.reserveOffset ? me.availableSpaceOffset : 0);

            if (me.done && ownerContext.boxOptions.align.stretchmax && !state.stretchMaxDone) {
                me.calculateStretchMax(ownerContext, names, plan);
                state.stretchMaxDone = true;
            }
        }
    },

    calculateParallel: function(ownerContext, names, plan) {
        var me = this,
            widthName = names.width,
            widthNameCap = names.widthCap,
            childItems = ownerContext.childItems,
            leftName = names.left,
            rightName = names.right,
            setWidthName = 'set' + widthNameCap,
            childItemsLength = childItems.length,
            flexedItems = ownerContext.flexedItems,
            flexedItemsLength = flexedItems.length,
            pack = ownerContext.boxOptions.pack,
            padding = me.padding,
            left = padding[leftName],
            nonFlexWidth = left + padding[rightName] + me.scrollOffset +
                                    (me.reserveOffset ? me.availableSpaceOffset : 0),
            i, childMargins, remainingWidth, remainingFlex, childContext, flex, flexedWidth,
            contentWidth;

        // Gather the total size taken up by non-flexed items:
        for (i = 0; i < childItemsLength; ++i) {
            childContext = childItems[i];
            childMargins = childContext.marginInfo || childContext.getMarginInfo();

            nonFlexWidth += childMargins[widthName];

            if (!childContext.flex) {
                nonFlexWidth += childContext.getProp(widthName); // min/maxWidth safe
                if (isNaN(nonFlexWidth)) {
                    return false;
                }
            }
        }
        // if we get here, we have all the childWidths for non-flexed items...

        if (ownerContext.parallelSizeModel.shrinkWrap) {
            plan.availableSpace = 0;
            plan.tooNarrow = false;
        } else {
            plan.availableSpace = plan.targetSize[widthName] - nonFlexWidth;
            
            // If we're going to need space for a parallel scrollbar, then we need to redo the perpendicular measurements
            if ((plan.tooNarrow = plan.availableSpace < ownerContext.flexedMinSize) && plan.scrollParallel && ownerContext.state.perpendicularDone) {
                ownerContext.state.perpendicularDone = false;
                for (i = 0; i < childItemsLength; ++i) {
                    childItems[i].invalidate();
                }
            }
        }

        contentWidth = nonFlexWidth;
        remainingWidth = plan.availableSpace;
        remainingFlex = ownerContext.totalFlex;

        // Calculate flexed item sizes:
        for (i = 0; i < flexedItemsLength; i++) {
            childContext = flexedItems[i];
            flex         = childContext.flex;
            flexedWidth  = me.roundFlex((flex / remainingFlex) * remainingWidth);
            flexedWidth  = childContext[setWidthName](flexedWidth); // constrained
            // for shrinkWrap w/flex, the item will be reduced to minWidth (maybe 0)

            // due to minWidth constraints, it may be that flexedWidth > remainingWidth

            contentWidth   += flexedWidth;
            // Remaining space has already had margins subtracted, so just subtract size
            remainingWidth  = Math.max(0, remainingWidth - flexedWidth); // no negatives!
            remainingFlex  -= flex;
        }

        if (pack.center) {
            left += remainingWidth / 2;

            // If content is too wide to pack to center, do not allow the centering calculation to place it off the left edge.
            if (left < 0) {
                left = 0;
            }
        } else if (pack.end) {
            left += remainingWidth;
        }

        ownerContext['setContent' + widthNameCap](contentWidth + me.padding[widthName] + ownerContext.targetContext.getPaddingInfo()[widthName]);

        // Assign parallel position for the boxes:
        for (i = 0; i < childItemsLength; ++i) {
            childContext = childItems[i];
            childMargins = childContext.marginInfo; // already cached by first loop

            left += childMargins[leftName];

            childContext.setProp(names.x, left);

            // We can read directly from "props.width" because we have already properly
            // requested it in the calculation of nonFlexedWidths or we calculated it.
            // We cannot call getProp because that would be inappropriate for flexed items
            // and we don't need any extra function call overhead:
            left += childMargins[rightName] + childContext.props[widthName];
        }

        return true;
    },

    calculatePerpendicular: function(ownerContext, names, plan) {
        var me = this,
            heightShrinkWrap = ownerContext.perpendicularSizeModel.shrinkWrap,
            targetSize = plan.targetSize,
            childItems = ownerContext.childItems,
            childItemsLength = childItems.length,
            mmax = Math.max,
            heightName = names.height,
            heightNameCap = names.heightCap,
            setHeightName = 'set' + heightNameCap,
            topName = names.top,
            topPositionName = names.y,
            padding = me.padding,
            top = padding[topName],
            availHeight = targetSize[heightName] - top - padding[names.bottom],
            align = ownerContext.boxOptions.align,
            isStretch    = align.stretch, // never true if heightShrinkWrap (see beginLayoutCycle)
            isStretchMax = align.stretchmax,
            isCenter     = align.center,
            maxHeight = 0,
            childTop, i, childHeight, childMargins, diff, height, childContext,
            stretchMaxPartner,
            scrollbarHeight,
            stretchMaxChildren;

        if (isStretch || (isCenter && !heightShrinkWrap)) {
            if (isNaN(availHeight)) {
                return false;
            }
        }

        // If the intention is to horizontally scroll height-fitted child components, but the container is too narrow,
        // then we must allow for the parallel scrollbar to intrude into the perpendicular dimension
        if (isStretch && plan.scrollParallel && plan.tooNarrow) {
            scrollbarHeight = Ext.getScrollbarSize().height;
            availHeight -= scrollbarHeight;
            plan.targetSize[heightName] -= scrollbarHeight;
        }

        if (isStretch) {
            height = availHeight; // never heightShrinkWrap...
        } else {
            for (i = 0; i < childItemsLength; i++) {
                childContext = childItems[i];
                childMargins = childContext.marginInfo || childContext.getMarginInfo();
                childHeight  = childContext.getProp(heightName);

                // Max perpendicular measurement (used for stretchmax) must take the min perpendicular size of each child into account in case any fall short.
                if (isNaN(maxHeight = mmax(maxHeight, childHeight + childMargins[heightName], childContext.target['min' + heightNameCap]||0))) {
                    return false; // heightShrinkWrap || isCenter || isStretchMax ??
                }
            }

            // If we are associated with another box layout, grab its maxChildHeight
            stretchMaxPartner = ownerContext.stretchMaxPartner;
            if (stretchMaxPartner) {
                // Publish maxChildHeight as soon as it has been calculated for our partner:
                ownerContext.setProp('maxChildHeight', maxHeight);
                stretchMaxChildren = stretchMaxPartner.childItems;
                // Only wait for maxChildHeight if our partner has visible items:
                if (stretchMaxChildren && stretchMaxChildren.length) {
                    maxHeight = mmax(maxHeight, stretchMaxPartner.getProp('maxChildHeight'));
                    if (isNaN(maxHeight)) {
                        return false;
                    }
                }
            }

            plan.maxSize = maxHeight;
            ownerContext['setContent' + heightNameCap](maxHeight + me.padding[heightName] +
                ownerContext.targetContext.getPaddingInfo()[heightName]);

            if (isStretchMax) {
                height = maxHeight;
            } else if (isCenter) {
                height = heightShrinkWrap ? maxHeight : mmax(availHeight, maxHeight);

                // When calculating a centered position within the content box of the innerCt,
                // the width of the borders must be subtracted from the size to yield the
                // space available to center within. The publishInnerCtSize method explicitly
                // adds the border widths to the set size of the innerCt.
                height -= ownerContext.innerCtContext.getBorderInfo()[heightName];
            }
        }

        for (i = 0; i < childItemsLength; i++) {
            childContext = childItems[i];
            childMargins = childContext.marginInfo || childContext.getMarginInfo();

            childTop = top + childMargins[topName];

            if (isStretch) {
                childContext[setHeightName](height - childMargins[heightName]);
            } else if (isCenter) {
                diff = height - childContext.props[heightName];
                if (diff > 0) {
                    childTop = top + Math.round(diff / 2);
                }
            }

            childContext.setProp(topPositionName, childTop);
        }

        return true;
    },

    calculateStretchMax: function (ownerContext, names, plan) {
        var me = this,
            calculated = me.sizeModels.calculated,
            heightModelName = names.height + 'Model',
            heightName = names.height,
            widthName = names.width,
            childItems = ownerContext.childItems,
            length = childItems.length,
            height = plan.maxSize,
            onInvalidateChild = me.onInvalidateChild,
            childContext, props, i, childHeight;

        for (i = 0; i < length; ++i) {
            childContext = childItems[i];

            if (childContext[heightModelName].shrinkWrap) {
                props = childContext.props;
                childHeight = height - childContext.getMarginInfo()[heightName];

                if (childHeight != props[heightName]) { // if (wrong height)
                    // Change the childItem from shrinkWrap to "set by ownerCt". To allow
                    // the child's component layout to course-correct (like dock layout
                    // does for a collapsed panel), we must make these changes here before
                    // invalidating the child:
                    childContext[heightModelName] = calculated;

                    // When we invalidate a child, since we won't be around to size and
                    // position it, we include a callback that will be run following the
                    // invalidate that will (re)do that work. The good news here is that
                    // we can read the results of all that from the childContext props.
                    childContext.invalidate({
                        callback: onInvalidateChild,
                        layout: me,
                        // passing this data avoids a 'scope' and its Function.bind
                        childWidth: props[widthName],
                        // subtract margins from the maximum value
                        childHeight: childHeight,
                        childX: props.x,
                        childY: props.y,
                        names: names
                    });
                }
            }
        }
    },

    completeLayout: function(ownerContext) {
        var me = this,
            state = ownerContext.state;

        me.overflowHandler.completeLayout(ownerContext);

        // If we are scrolling parallel, restore the saved scroll position
        if (state.boxPlan.scrollParallel) {
            me.owner.getTargetEl().dom['scroll' + me.getNames().leftCap] = state.scrollPos;
        }

    },

    onInvalidateChild: function (options, childContext) {
        // NOTE: No "this" pointer in here...
        var names = options.names,
            heightCapName = names.heightCap;

        childContext.setProp('x', options.childX);
        childContext.setProp('y', options.childY);

        if (childContext[names.height + 'Model'].calculated) {
            // We need to respect a child that is still not calculated (such as a collapsed
            // panel)...
            childContext['set' + heightCapName](options.childHeight);
        }

        if (childContext.flex) {
            childContext['set' + names.widthCap](options.childWidth);
        }
    },

    publishInnerCtSize: function(ownerContext, reservedSpace) {
        var me = this,
            names = me.getNames(),
            heightName = names.height,
            widthName = names.width,
            align = ownerContext.boxOptions.align,
            dock = me.owner.dock,
            padding = me.padding,
            plan = ownerContext.state.boxPlan,
            targetSize = plan.targetSize,
            height = targetSize[heightName],
            innerCtContext = ownerContext.innerCtContext,
            innerCtWidth = (ownerContext.parallelSizeModel.shrinkWrap || (plan.tooNarrow && plan.scrollParallel)
                    ? ownerContext.props['content' + names.widthCap]
                    : targetSize[widthName]) - (reservedSpace || 0),
            innerCtHeight;

        if (align.stretch) {
            innerCtHeight = height;
        } else {
            innerCtHeight = plan.maxSize + padding[names.top] + padding[names.bottom] +
                                    innerCtContext.getBorderInfo()[heightName];

            if (!ownerContext.perpendicularSizeModel.shrinkWrap && align.center) {
                innerCtHeight = Math.max(height, innerCtHeight);
            }
        }

        innerCtContext['set' + names.widthCap](innerCtWidth);
        innerCtContext['set' + names.heightCap](innerCtHeight);

        // If unable to publish both dimensions, this layout needs to run again
        if (isNaN(innerCtWidth + innerCtHeight)) {
            me.done = false;
        }

        // If a calculated width has been found (this only happens for widthModel.shrinkWrap
        // vertical docked Components in old Microsoft browsers) then, if the Component has
        // not assumed the size of its content, set it to do so.
        // 
        // We MUST pass the dirty flag to get that into the DOM, and because we are a Container
        // layout, and not really supposed to perform sizing, we must also use the force flag.
        if (plan.calculatedWidth && (dock == 'left' || dock == 'right')) {
            ownerContext.setWidth(plan.calculatedWidth, true, true);
        }
    },

    onRemove: function(comp){
        var me = this;
        me.callParent(arguments);
        if (me.overflowHandler) {
            me.overflowHandler.onRemove(comp);
        }
        if (comp.layoutMarginCap == me.id) {
            delete comp.layoutMarginCap;
        }
    },

    /**
     * @private
     */
    initOverflowHandler: function() {
        var me = this,
            handler = me.overflowHandler,
            handlerType,
            constructor;

        if (typeof handler == 'string') {
            handler = {
                type: handler
            };
        }

        handlerType = 'None';
        if (handler && handler.type !== undefined) {
            handlerType = handler.type;
        }

        constructor = Ext.layout.container.boxOverflow[handlerType];
        if (constructor[me.type]) {
            constructor = constructor[me.type];
        }

        me.overflowHandler = Ext.create('Ext.layout.container.boxOverflow.' + handlerType, me, handler);
    },

    // private
    isValidParent : function(item, target, position) {
        // Note: Box layouts do not care about order within the innerCt element because it's an absolutely positioning layout
        // We only care whether the item is a direct child of the innerCt element.
        var itemEl = item.el ? item.el.dom : Ext.getDom(item);
        return (itemEl && this.innerCt && itemEl.parentNode === this.innerCt.dom) || false;
    },

    // Overridden method from AbstractContainer.
    // Used in the base AbstractLayout.beforeLayout method to render all items into.
    getRenderTarget: function() {
        return this.innerCt;
    },

    //<debug>
    calculateChildBox: Ext.deprecated(),
    calculateChildBoxes: Ext.deprecated(),
    updateChildBoxes: Ext.deprecated(),
    //</debug>

    /**
     * @private
     */
    destroy: function() {
        Ext.destroy(this.innerCt, this.overflowHandler);
        this.callParent(arguments);
    }
});
