/**
 * This class monitors scrolling of the {@link Ext.view.Table TableView} within a
 * {@link Ext.grid.Panel GridPanel} which is using a buffered store to only cache
 * and render a small section of a very large dataset.
 *
 * The GridPanel will instantiate this to perform monitoring, this class should
 * never be instantiated by user code.
 */
Ext.define('Ext.grid.PagingScroller', {

    /**
     * @cfg
     * @deprecated This config is now ignored.
     */
    percentageFromEdge: 0.35,

    /**
     * @cfg
     * The zone which causes a refresh of the rendered viewport. As soon as the edge
     * of the rendered grid is this number of rows from the edge of the viewport, the view is moved.
     */
    numFromEdge: 2,

    /**
     * @cfg
     * The number of extra rows to render on the trailing side of scrolling
     * **outside the {@link #numFromEdge}** buffer as scrolling proceeds.
     */
    trailingBufferZone: 5,
    
    /**
     * @cfg
     * The number of extra rows to render on the leading side of scrolling
     * **outside the {@link #numFromEdge}** buffer as scrolling proceeds.
     */
    leadingBufferZone: 15,

    /**
     * @cfg
     * This is the time in milliseconds to buffer load requests when scrolling the PagingScrollbar.
     */
    scrollToLoadBuffer: 200,

    constructor: function(config) {
        var me = this,
            listeners = {
                scroll: {
                    fn: me.onViewScroll,
                    element: 'el',
                    scope: me
                },
                render: me.onViewRender,
                resize: me.onViewResize,
                refresh: me.onViewRefresh,
                scope: me
            };
        Ext.apply(me, config);

        // Prepare for the most common scnerio: An initial load of page one, followed by controlled scrolling downwards
        if (me.store) {
            if (me.store.loaded) {
                if (me.store.getTotalCount()) {
                    me.store.prefetchPage(2);
                }
            }
        } else {
            me.store.on({
                load: function() {
                    me.store.prefetchPage(2);
                },
                single: true
            });
        }

        /**
         * @property {Number} position
         * Current pixel scroll position of the associated {@link Ext.view.Table View}.
         */
        me.position = 0;

        if (me.variableRowHeight) {
            listeners.beforerefresh = me.beforeViewRefresh;
        }
        me.view.on(listeners);
        me.store.on({
            guaranteedrange: me.onGuaranteedRange,
            scope: me
        });
        me.callParent(arguments);
    },

    // Ensure that the stretcher element is inserted into the View as the first element.
    onViewRender: function() {
        var me = this,
            el = me.view.el;

        el.setStyle('position', 'relative');
        me.stretcher = el.createChild({
            style:{
                position: 'absolute',
                width: '1px',
                height: 0,
                top: 0,
                left: 0
            }
        }, el.dom.firstChild);
    },

    onViewResize: function(view, width, height) {
        var me = this,
            store = me.store,
            calcPageSize;
        
        me.minStoreSize = (height / 21) + (me.numFromEdge * 2) + me.trailingBufferZone + me.leadingBufferZone;
        calcPageSize = Math.max(me.store.pageSize||0, Math.floor(me.minStoreSize + (store.numFromEdge * 2) + store.trailingBufferZone + store.leadingBufferZone));

        // calculate a sensible Store page size
        if (store.pageSize) {
            // <debug>
            if (store.pageSize < calcPageSize) {
                Ext.log("Store " + store.storeId + "'s pageSize (" + store.pageSize + ") is smaller than optimal page size of " + calcPageSize);
            }
            // </debug>
        } else {
            store.pageSize = calcPageSize;
        }
    },

    // Used for variable row heights. Try to find the offset from scrollTop of a common row
    beforeViewRefresh: function() {
        var me = this,
            view = me.view,
            rows,
            store = me.store,
            direction = me.lastScrollDirection;

        me.commonRecordIndex = undefined;
        if (me.variableRowHeight && (me.previousStart !== undefined) && (me.scrollProportion === undefined)) {
            rows = view.getNodes();

            // We have scrolled downwards
            if (direction === 1) {

                // If the ranges overlap, we are going to be able to position the table exactly
                if (store.guaranteedStart <= me.previousEnd) {
                    me.commonRecordIndex = rows.length - 1;

                }
            }
            // We have scrolled upwards
            else if (direction === -1) {

                // If the ranges overlap, we are going to be able to position the table exactly
                if (store.guaranteedEnd >= me.previousStart) {
                    me.commonRecordIndex = 0;
                }
            }
            // Cache the old offset of the common row from the scrollTop
            me.scrollOffset = -view.el.getOffsetsTo(rows[me.commonRecordIndex])[1];

            // In the new table the common row is at a different index
            me.commonRecordIndex -= (store.guaranteedStart - me.previousStart)
        } else {
            me.scrollOffset = undefined;
        }
    },

    // Ensure, upon each refresh, that the stretcher element is the correct height
    onViewRefresh: function() {
        var me = this,
            newScrollHeight = me.getScrollHeight(),
            view = me.view,
            viewEl = view.el.dom,
            store = me.store,
            rows,
            newScrollOffset,
            scrollDelta,
            table,
            tableTop;

        me.stretcher.setHeight(newScrollHeight);

        // If we have had to calculate the store position from the pure scroll bar position,
        // then we must calculate the table's vertical position from the scrollProportion 
        if (me.scrollProportion !== undefined) {
            table = me.view.el.child('table', true);
            me.scrollProportion = view.el.dom.scrollTop / (newScrollHeight - table.offsetHeight);
            table = me.view.el.child('table', true);
            table.style.position = 'absolute';
            table.style.top = (me.scrollProportion ? (newScrollHeight * me.scrollProportion) - (table.offsetHeight * me.scrollProportion) : 0) + 'px';
        }
        else {
            table = me.view.el.child('table', true);
            table.style.position = 'absolute';
            table.style.top = (tableTop = (store.guaranteedStart||0) * me.rowHeight) + 'px';

            // ScrollOffset to a common row was calculated in beforeViewRefresh, so we can synch table position with how it was before
            if (me.scrollOffset) {
                rows = view.getNodes();
                newScrollOffset = -view.el.getOffsetsTo(rows[me.commonRecordIndex])[1];
                scrollDelta = newScrollOffset - me.scrollOffset;
                me.position = (view.el.dom.scrollTop += scrollDelta);
            }

            // If the table is not fully in view view, scroll to where it is in view.
            // This will happen when the page goes out of view undepectedly, outside the
            // control of the PagingScroller. For example, a refresh caused by a remote sort reverting
            // back to page 1.
            // Note that with buffered Stores, only remote paging is allowed, otherwise the locally
            // sorted page will be out of order with the whole dataset.
            else if ((tableTop > viewEl.scrollTop) || ((tableTop + table.offsetHeight) < viewEl.scrollTop + viewEl.clientHeight)) {
                me.position = viewEl.scrollTop = tableTop;
            }
        }
    },
    
    onGuaranteedRange: function(range, start, end) {
        var me = this,
            ds = me.store;

        // this should never happen
        if (range.length && me.visibleStart < range[0].index) {
            return;
        }

        ds.loadRecords(range);
    },

    onViewScroll: function(e, t) {
        var me = this,
            view = me.view,
            lastPosition = me.position;

        me.position = view.el.dom.scrollTop;
        me.lastScrollDirection = me.position > lastPosition ? 1 : -1;
        me.handleViewScroll(e, me.lastScrollDirection);
    },

    handleViewScroll: function(e, direction) {
        var me                = this,
            store             = me.store,
            view              = me.view,
            guaranteedStart   = me.previousStart = store.guaranteedStart,
            guaranteedEnd     = me.previousEnd = store.guaranteedEnd,
            renderedSize      = store.getCount(),
            totalCount        = store.getTotalCount(),
            highestStartPoint = totalCount - renderedSize,
            visibleStart      = me.getFirstVisibleRowIndex(),
            visibleEnd        = me.getLastVisibleRowIndex(),
            requestStart,
            requestEnd;

        // Only process if the total rows is larger than the visible page size
        if (totalCount >= renderedSize) {

            // This is only set if we are using variable row height, and the thumb is dragged so that
            // There are no remaining visible rows to vertically anchor the new table to.
            // In this case we use the scrollProprtion to anchor the table to the correct relative
            // position on the vertical axis.
            me.scrollProportion = undefined;

            // We're scrolling up
            if (direction == -1) {
                if (visibleStart !== undefined) {
                    if (visibleStart < (guaranteedStart + me.numFromEdge)) {
                        requestStart = Math.max(0, visibleEnd + me.numFromEdge + me.trailingBufferZone - renderedSize);
                    }
                }

                // The only way we can end up without a visible start is if, in variableRowHeight mode, the user drags
                // the thumb up out of the visible range. In this case, we have to estimate the start row index
                else {
                    // If we have no visible rows to orientate with, then use the scroll proportion
                    me.scrollProportion = view.el.dom.scrollTop / (view.el.dom.scrollHeight - view.el.dom.clientHeight);
                    requestStart = Math.max(0, totalCount * me.scrollProportion - (renderedSize / 2) - me.numFromEdge - ((me.leadingBufferZone + me.trailingBufferZone) / 2));
                }
            }
            // We're scrolling down
            else {
                if (visibleStart !== undefined) {
                    if (visibleEnd > (guaranteedEnd - me.numFromEdge)) {
                        requestStart = visibleStart - me.numFromEdge - me.trailingBufferZone;
                    }
                }
                
                // The only way we can end up without a visible end is if, in variableRowHeight mode, the user drags
                // the thumb down out of the visible range. In this case, we have to estimate the start row index
                else {
                    // If we have no visible rows to orientate with, then use the scroll proportion
                    me.scrollProportion = view.el.dom.scrollTop / (view.el.dom.scrollHeight - view.el.dom.clientHeight);
                    requestStart = totalCount * me.scrollProportion - (renderedSize / 2) - me.numFromEdge - ((me.leadingBufferZone + me.trailingBufferZone) / 2);
                }
            }
            

            // We scrolled close to the edge and the Store needs reloading
            if (requestStart !== undefined) {
                // The calculation walked off the end; Request the highest possible chunk which starts on an even row count (Because of row striping)
                if (requestStart > highestStartPoint) {
                    requestStart = highestStartPoint & ~1;
                    requestEnd = totalCount - 1;
                }
                // Make sure first row is even to ensure correct even/odd row striping
                else {
                    requestStart = requestStart & ~1;
                    requestEnd = requestStart + renderedSize - 1;
                }

                // If range is satsfied within the prefetch buffer, then just draw it from the prefetch buffer
                if (store.rangeSatisfied(requestStart, requestEnd)) {
                    me.cancelLoad();
                    store.guaranteeRange(requestStart, requestEnd);
                } else {
                    me.attemptLoad(requestStart, requestEnd);
                }
            }
        }
    },

    getFirstVisibleRowIndex: function() {
        var me = this,
            store = me.store,
            view = me.view,
            scrollTop = view.el.dom.scrollTop,
            rows,
            count,
            i,
            rowBottom;

        if (me.variableRowHeight) {
            rows = view.getNodes();
            count = store.getCount();
            for (i = 0; i < count; i++) {
                rowBottom = Ext.fly(rows[i]).getOffsetsTo(view.el)[1] + rows[i].offsetHeight;
                
                // Searching for the first visible row, and off the bottom of the clientArea, then there's no visible first row!
                if (rowBottom > view.el.dom.clientHeight) {
                    return;
                }

                if (rowBottom > 0) {
                    return i + store.guaranteedStart;
                }
            }
        } else {
            return Math.floor(scrollTop / me.rowHeight);
        }
    },

    getLastVisibleRowIndex: function() {
        var me = this,
            store = me.store,
            view = me.view,
            clientHeight = view.el.dom.clientHeight,
            rows,
            count,
            i,
            rowTop;

        if (me.variableRowHeight) {
            rows = view.getNodes();
            count = store.getCount();
            for (i = count - 1; i >= 0; i--) {
                rowTop = Ext.fly(rows[i]).getOffsetsTo(view.el)[1];

                // Searching for the last visible row, and off the top of the clientArea, then there's no visible last row!
                if (rowTop < 0) {
                    return;
                }
                if (rowTop < clientHeight) {
                    return i + store.guaranteedStart;
                }
            }
        } else {
            return me.getFirstVisibleRowIndex() + Math.ceil(clientHeight / me.rowHeight);
        }
    },

    getScrollHeight: function() {
        var me = this,
            view   = me.view,
            table,
            firstRow,
            store  = me.store,
            rowCount,
            deltaHeight = 0;

        if (me.variableRowHeight) {
            table = me.view.el.down('table', true);
            if (me.rowHeight) {
                deltaHeight = table.offsetHeight - me.initialTableHeight;
            } else {
                me.initialTableHeight = table.offsetHeight;
                me.rowHeight = me.initialTableHeight / me.store.pageSize;
            }
        } else if (!me.rowHeight) {
            firstRow = view.el.down(view.getItemSelector());
            me.rowHeight = firstRow ? firstRow.getHeight(false, true) : 0;
        }

        // If the Store is *locally* filtered, use the filtered count from getCount.
        rowCount = store[(!store.remoteFilter && store.isFiltered()) ? 'getCount' : 'getTotalCount']() || 0;
        return Math.floor(rowCount * me.rowHeight) + deltaHeight;
    },

    attemptLoad: function(start, end) {
        var me = this;
        if (!me.loadTask) {
            me.loadTask = new Ext.util.DelayedTask(me.doAttemptLoad, me, []);
        }
        me.loadTask.delay(me.scrollToLoadBuffer, me.doAttemptLoad, me, [start, end]);
    },

    cancelLoad: function() {
        if (this.loadTask) {
            this.loadTask.cancel();
        }
    },

    doAttemptLoad:  function(start, end) {
        this.store.mask();
        this.store.guaranteeRange(start, end);
    }
});
