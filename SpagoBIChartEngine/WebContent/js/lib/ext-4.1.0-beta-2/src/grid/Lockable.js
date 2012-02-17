/**
 * @private
 *
 * Lockable is a private mixin which injects lockable behavior into any
 * TablePanel subclass such as GridPanel or TreePanel. TablePanel will
 * automatically inject the Ext.grid.Lockable mixin in when one of the
 * these conditions are met:
 *
 *  - The TablePanel has the lockable configuration set to true
 *  - One of the columns in the TablePanel has locked set to true/false
 *
 * Each TablePanel subclass must register an alias. It should have an array
 * of configurations to copy to the 2 separate tablepanel's that will be generated
 * to note what configurations should be copied. These are named normalCfgCopy and
 * lockedCfgCopy respectively.
 *
 * Columns which are locked must specify a fixed width. They do NOT support a
 * flex width.
 *
 * Configurations which are specified in this class will be available on any grid or
 * tree which is using the lockable functionality.
 */
Ext.define('Ext.grid.Lockable', {

    requires: ['Ext.grid.LockingView'],

    /**
     * @cfg {Boolean} syncRowHeight Synchronize rowHeight between the normal and
     * locked grid view. This is turned on by default. If your grid is guaranteed
     * to have rows of all the same height, you should set this to false to
     * optimize performance.
     */
    syncRowHeight: true,

    /**
     * @cfg {String} subGridXType The xtype of the subgrid to specify. If this is
     * not specified lockable will determine the subgrid xtype to create by the
     * following rule. Use the superclasses xtype if the superclass is NOT
     * tablepanel, otherwise use the xtype itself.
     */

    /**
     * @cfg {Object} lockedViewConfig A view configuration to be applied to the
     * locked side of the grid. Any conflicting configurations between lockedViewConfig
     * and viewConfig will be overwritten by the lockedViewConfig.
     */

    /**
     * @cfg {Object} normalViewConfig A view configuration to be applied to the
     * normal/unlocked side of the grid. Any conflicting configurations between normalViewConfig
     * and viewConfig will be overwritten by the normalViewConfig.
     */

    headerCounter: 0,

    /**
     * @cfg {Number} scrollDelta
     * Number of pixels to scroll when scrolling the locked section with mousewheel.
     */
    scrollDelta: 40,

    // i8n text
    //<locale>
    unlockText: 'Unlock',
    //</locale>
    //<locale>
    lockText: 'Lock',
    //</locale>

    determineXTypeToCreate: function() {
        var me = this,
            typeToCreate;

        if (me.subGridXType) {
            typeToCreate = me.subGridXType;
        } else {
            var xtypes     = this.getXTypes().split('/'),
                xtypesLn   = xtypes.length,
                xtype      = xtypes[xtypesLn - 1],
                superxtype = xtypes[xtypesLn - 2];

            if (superxtype !== 'tablepanel') {
                typeToCreate = superxtype;
            } else {
                typeToCreate = xtype;
            }
        }

        return typeToCreate;
    },

    // injectLockable will be invoked before initComponent's parent class implementation
    // is called, so throughout this method this. are configurations
    injectLockable: function() {
        // ensure lockable is set to true in the TablePanel
        this.lockable = true;
        // Instruct the TablePanel it already has a view and not to create one.
        // We are going to aggregate 2 copies of whatever TablePanel we are using
        this.hasView = true;

        var me = this,
            store = me.store = Ext.StoreManager.lookup(me.store),
            // xtype of this class, 'treepanel' or 'gridpanel'
            // (Note: this makes it a requirement that any subclass that wants to use lockable functionality needs to register an
            // alias.)
            xtype = me.determineXTypeToCreate(),
            // share the selection model
            selModel = me.getSelectionModel(),
            lockedGrid = {
                xtype: xtype,
                store: store,
                scrollerOwner: false,
                // Lockable does NOT support animations for Tree
                enableAnimations: false,
                scroll: false,
                selModel: selModel,
                border: false,
                cls: Ext.baseCSSPrefix + 'grid-inner-locked',
                isLayoutRoot: function() {
                    return false;
                }
            },
            normalGrid = {
                xtype: xtype,
                store: store,
                scrollerOwner: false,
                enableAnimations: false,
                selModel: selModel,
                border: false,
                isLayoutRoot: function() {
                    return false;
                }
            },
            i = 0,
            columns,
            lockedHeaderCt,
            normalHeaderCt,
            lockedView,
            normalView;
        
        me.addCls(Ext.baseCSSPrefix + 'grid-locked');

        // copy appropriate configurations to the respective
        // aggregated tablepanel instances and then delete them
        // from the master tablepanel.
        Ext.copyTo(normalGrid, me, me.bothCfgCopy);
        Ext.copyTo(lockedGrid, me, me.bothCfgCopy);
        Ext.copyTo(normalGrid, me, me.normalCfgCopy);
        Ext.copyTo(lockedGrid, me, me.lockedCfgCopy);
        for (; i < me.normalCfgCopy.length; i++) {
            delete me[me.normalCfgCopy[i]];
        }
        for (i = 0; i < me.lockedCfgCopy.length; i++) {
            delete me[me.lockedCfgCopy[i]];
        }

        me.addEvents(
            /**
             * @event lockcolumn
             * Fires when a column is locked.
             * @param {Ext.grid.Panel} this The gridpanel.
             * @param {Ext.grid.column.Column} column The column being locked.
             */
            'lockcolumn',

            /**
             * @event unlockcolumn
             * Fires when a column is unlocked.
             * @param {Ext.grid.Panel} this The gridpanel.
             * @param {Ext.grid.column.Column} column The column being unlocked.
             */
            'unlockcolumn'
        );

        me.addStateEvents(['lockcolumn', 'unlockcolumn']);

        me.lockedHeights = [];
        me.normalHeights = [];

        columns = me.processColumns(me.columns);

        lockedGrid.width = columns.lockedWidth + Ext.num(selModel.headerWidth, 0);
        lockedGrid.columns = columns.locked;
        normalGrid.columns = columns.normal;
        
        // normal grid should flex the rest of the width
        normalGrid.flex = 1;
        lockedGrid.viewConfig = me.lockedViewConfig || {};
        lockedGrid.viewConfig.loadingUseMsg = false;
        normalGrid.viewConfig = me.normalViewConfig || {};

        Ext.applyIf(lockedGrid.viewConfig, me.viewConfig);
        Ext.applyIf(normalGrid.viewConfig, me.viewConfig);

        me.normalGrid = Ext.ComponentManager.create(normalGrid);
        me.lockedGrid = Ext.ComponentManager.create(lockedGrid);
        
        me.view = new Ext.grid.LockingView({
            locked: me.lockedGrid,
            normal: me.normalGrid,
            panel: me
        });

        lockedView = me.lockedGrid.getView();
        normalView = me.normalGrid.getView();

        // Synchronize the scrollTops of the two views
        lockedView.on({
            scroll: {
                fn: me.onLockedViewScroll,
                element: 'el',
                scope: me
            },
            mousewheel: {
                fn: me.onLockedViewMouseWheel,
                element: 'el',
                scope: me
            }
        });
        normalView.on({
            scroll: {
                fn: me.onNormalViewScroll,
                element: 'el',
                scope: me
            },
            refresh: me.createSpacer,
            beforerefresh: me.destroySpacer,
            scope: me
        });

        if (me.syncRowHeight) {
            lockedView.on({
                refresh: me.onLockedViewRefresh,
                itemupdate: me.onLockedViewItemUpdate,
                scope: me
            });
            
            normalView.on({
                refresh: me.onNormalViewRefresh,
                itemupdate: me.onNormalViewItemUpdate,
                scope: me
            });
        }

        lockedHeaderCt = me.lockedGrid.headerCt;
        normalHeaderCt = me.normalGrid.headerCt;

        lockedHeaderCt.lockedCt = true;
        lockedHeaderCt.lockableInjected = true;
        normalHeaderCt.lockableInjected = true;

        lockedHeaderCt.on({
            columnshow: me.onLockedHeaderShow,
            columnhide: me.onLockedHeaderHide,
            columnmove: me.onLockedHeaderMove,
            sortchange: me.onLockedHeaderSortChange,
            columnresize: me.onLockedHeaderResize,
            scope: me
        });

        normalHeaderCt.on({
            columnmove: me.onNormalHeaderMove,
            sortchange: me.onNormalHeaderSortChange,
            scope: me
        });
        
        me.modifyHeaderCt();
        me.items = [me.lockedGrid, me.normalGrid];

        me.relayHeaderCtEvents(lockedHeaderCt);
        me.relayHeaderCtEvents(normalHeaderCt);

        me.layout = {
            type: 'hbox',
            align: 'stretch'
        };
    },

    processColumns: function(columns){
        // split apart normal and lockedWidths
        var i = 0,
            len = columns.length,
            lockedWidth = 0,
            lockedHeaders = [],
            normalHeaders = [],
            column;

        for (; i < len; ++i) {
            column = columns[i];
            // mark the column as processed so that the locked attribute does not
            // trigger trying to aggregate the columns again.
            column.processed = true;
            if (column.locked) {
                // <debug>
                if (column.flex) {
                    Ext.Error.raise("Columns which are locked do NOT support a flex width. You must set a width on the " + columns[i].text + "column.");
                }
                // </debug>
                if (!column.hidden) {
                    lockedWidth += column.width || Ext.grid.header.Container.prototype.defaultWidth;
                }
                lockedHeaders.push(column);
            } else {
                normalHeaders.push(column);
            }
            if (!column.headerId) {
                column.headerId = (column.initialConfig || column).id || ('L' + (++this.headerCounter));
            }
        }
        return {
            lockedWidth: lockedWidth,
            locked: {
                items: lockedHeaders,
                itemId: 'lockedHeaderCt',
                stretchMaxPartner: '^^>>#normalHeaderCt'
            },
            normal: {
                items: normalHeaders,
                itemId: 'normalHeaderCt',
                stretchMaxPartner: '^^>>#lockedHeaderCt'
            }
        };
    },

    /**
     * @private
     * Listen for mousewheel events on the locked section which does not scroll.
     * Scroll it in response, and the other section will automatically sync.
     */
    onLockedViewMouseWheel: function(e) {
        var me = this,
            scrollDelta = -me.scrollDelta,
            deltaY = scrollDelta * e.getWheelDeltas().y,
            vertScrollerEl = me.lockedGrid.getView().el.dom,
            verticalCanScrollDown, verticalCanScrollUp;

        if (vertScrollerEl) {
            verticalCanScrollDown = vertScrollerEl.scrollTop !== vertScrollerEl.scrollHeight - vertScrollerEl.clientHeight;
            verticalCanScrollUp   = vertScrollerEl.scrollTop !== 0;
        }

        if ((deltaY < 0 && verticalCanScrollUp) || (deltaY > 0 && verticalCanScrollDown)) {
            e.stopEvent();
            vertScrollerEl.scrollTop += deltaY;
        }
    },

    onLockedViewScroll: function() {
        var me = this,
            lockedView = me.lockedGrid.getView(),
            normalView = me.normalGrid.getView(),
            normalTable,
            lockedTable;

        normalView.el.dom.scrollTop = lockedView.el.dom.scrollTop;

        // For buffered views, the absolute position is important as well as scrollTop
        if (me.store.buffered) {
            lockedTable = lockedView.child('table', true);
            normalTable = normalView.child('table', true);
            normalTable.dom.style.top = lockedTable.dom.style.top;
        }
    },
    
    onNormalViewScroll: function() {
        var me = this,
            lockedView = me.lockedGrid.getView(),
            normalView = me.normalGrid.getView(),
            normalTable,
            lockedTable;

        lockedView.el.dom.scrollTop = normalView.el.dom.scrollTop;

        // For buffered views, the absolute position is important as well as scrollTop
        if (me.store.buffered) {
            lockedTable = lockedView.child('table', true);
            normalTable = normalView.child('table', true);
            lockedTable.dom.style.top = normalTable.dom.style.top;
        }
    },

    // trigger a pseudo refresh on the normal side
    onLockedHeaderMove: function() {
        if (this.syncRowHeight) {
            this.onNormalViewRefresh();
        }
    },

    // trigger a pseudo refresh on the locked side
    onNormalHeaderMove: function() {
        if (this.syncRowHeight) {
            this.onLockedViewRefresh();
        }
    },
    
    // Create a spacer in lockedsection and store a reference.
    // This is to allow the locked section to scroll past the bottom to
    // take the mormal section's horizontal scrollbar into account
    // TODO: Should destroy before refreshing content
    createSpacer: function() {
        var me   = this,
            // This affects scrolling all the way to the bottom of a locked grid
            // additional test, sort a column and make sure it synchronizes
            lockedViewEl   = me.lockedGrid.getView().el,
            normalViewEl = me.normalGrid.getView().el.dom;

        me.spacerEl = Ext.core.DomHelper.append(lockedViewEl, {
            style: 'height: ' + (normalViewEl.offsetHeight - normalViewEl.clientHeight) + 'px;'
        }, true);
    },

    destroySpacer: function() {
        var me = this;
        if (me.spacerEl) {
            me.spacerEl.destroy();
            delete me.spacerEl;
        }
    },

    // cache the heights of all locked rows and sync rowheights
    onLockedViewRefresh: function() {
        var me     = this,
            view   = me.lockedGrid.getView(),
            el     = view.el,
            rowEls = el.query(view.getItemSelector()),
            ln     = rowEls.length,
            i = 0;

        // reset heights each time.
        me.lockedHeights = [];

        for (; i < ln; i++) {
            me.lockedHeights[i] = rowEls[i].clientHeight;
        }
        me.syncRowHeights();
    },

    // cache the heights of all normal rows and sync rowheights
    onNormalViewRefresh: function() {
        var me     = this,
            view   = me.normalGrid.getView(),
            el     = view.el,
            rowEls = el.query(view.getItemSelector()),
            ln     = rowEls.length,
            i = 0;

        // reset heights each time.
        me.normalHeights = [];

        for (; i < ln; i++) {
            me.normalHeights[i] = rowEls[i].clientHeight;
        }
        me.syncRowHeights();
    },

    // rows can get bigger/smaller
    onLockedViewItemUpdate: function(record, index, node) {
        this.lockedHeights[index] = node.clientHeight;
        this.syncRowHeights();
    },

    // rows can get bigger/smaller
    onNormalViewItemUpdate: function(record, index, node) {
        this.normalHeights[index] = node.clientHeight;
        this.syncRowHeights();
    },

    /**
     * Synchronizes the row heights between the locked and non locked portion of the grid for each
     * row. If one row is smaller than the other, the height will be increased to match the larger one.
     */
    syncRowHeights: function() {
        var me = this,
            lockedHeights = me.lockedHeights,
            normalHeights = me.normalHeights,
            calcHeights   = [],
            ln = lockedHeights.length,
            i  = 0,
            lockedView, normalView,
            lockedRowEls, normalRowEls,
            scrollTop;

        // ensure there are an equal num of locked and normal
        // rows before synchronization
        if (lockedHeights.length && normalHeights.length) {
            lockedView = me.lockedGrid.getView();
            normalView = me.normalGrid.getView();
            lockedRowEls = lockedView.el.query(lockedView.getItemSelector());
            normalRowEls = normalView.el.query(normalView.getItemSelector());

            // loop thru all of the heights and sync to the other side
            for (; i < ln; i++) {
                // ensure both are numbers
                if (!isNaN(lockedHeights[i]) && !isNaN(normalHeights[i])) {
                    if (lockedHeights[i] > normalHeights[i]) {
                        Ext.fly(normalRowEls[i]).setHeight(lockedHeights[i]);
                    } else if (lockedHeights[i] < normalHeights[i]) {
                        Ext.fly(lockedRowEls[i]).setHeight(normalHeights[i]);
                    }
                }
            }

            // Synchronize the scrollTop positions of the two views
            // We don't use setScrollTop here because if the scrollTop is
            // set to the exact same value some browsers won't fire the scroll
            // event. Instead, we directly set the scrollTop.
            scrollTop = normalView.el.dom.scrollTop;
            normalView.el.dom.scrollTop = scrollTop;
            lockedView.el.dom.scrollTop = scrollTop;
            
            // reset the heights
            me.lockedHeights = [];
            me.normalHeights = [];
        }
    },

    // inject Lock and Unlock text
    modifyHeaderCt: function() {
        var me = this;
        me.lockedGrid.headerCt.getMenuItems = me.getMenuItems(true);
        me.normalGrid.headerCt.getMenuItems = me.getMenuItems(false);
    },

    onUnlockMenuClick: function() {
        this.unlock();
    },

    onLockMenuClick: function() {
        this.lock();
    },

    getMenuItems: function(locked) {
        var me            = this,
            unlockText    = me.unlockText,
            lockText      = me.lockText,
            unlockCls     = Ext.baseCSSPrefix + 'hmenu-unlock',
            lockCls       = Ext.baseCSSPrefix + 'hmenu-lock',
            unlockHandler = Ext.Function.bind(me.onUnlockMenuClick, me),
            lockHandler   = Ext.Function.bind(me.onLockMenuClick, me);

        // runs in the scope of headerCt
        return function() {
            var o = Ext.grid.header.Container.prototype.getMenuItems.call(this);
            o.push('-',{
                cls: unlockCls,
                text: unlockText,
                handler: unlockHandler,
                disabled: !locked
            });
            o.push({
                cls: lockCls,
                text: lockText,
                handler: lockHandler,
                disabled: locked
            });
            return o;
        };
    },

    // going from unlocked section to locked
    /**
     * Locks the activeHeader as determined by which menu is open OR a header
     * as specified.
     * @param {Ext.grid.column.Column} [header] Header to unlock from the locked section.
     * Defaults to the header which has the menu open currently.
     * @param {Number} [toIdx] The index to move the unlocked header to.
     * Defaults to appending as the last item.
     * @private
     */
    lock: function(activeHd, toIdx) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalHCt  = normalGrid.headerCt,
            lockedHCt  = lockedGrid.headerCt;

        activeHd = activeHd || normalHCt.getMenu().activeHeader;

        // if column was previously flexed, get/set current width
        // and remove the flex
        if (activeHd.flex) {
            activeHd.width = activeHd.getWidth();
            delete activeHd.flex;
        }

        Ext.suspendLayouts();
        normalHCt.remove(activeHd, false);
        activeHd.locked = true;
        if (Ext.isDefined(toIdx)) {
            lockedHCt.insert(toIdx, activeHd);
        } else {
            lockedHCt.add(activeHd);
        }
        me.syncLockedSection();
        Ext.resumeLayouts(true);

        me.fireEvent('lockcolumn', me, activeHd);
    },

    syncLockedSection: function() {
        var me = this;
        me.syncLockedWidth();
        me.lockedGrid.getView().refresh();
        me.normalGrid.getView().refresh();
    },

    // adjust the locked section to the width of its respective
    // headerCt
    syncLockedWidth: function() {
        var me = this,
            locked = me.lockedGrid,
            width = locked.headerCt.getFullWidth(true);
            
        me.suspendLayouts();
        if (width > 0) {
            locked.setWidth(width);
            locked.show();
        } else {
            locked.hide();
        }
        me.resumeLayouts(true);
        
        return width > 0;
    },

    onLockedHeaderResize: function() {
        this.syncLockedWidth();
    },

    onLockedHeaderHide: function() {
        this.syncLockedWidth();
    },

    onLockedHeaderShow: function() {
        this.syncLockedWidth();
    },

    onLockedHeaderSortChange: function(headerCt, header, sortState) {
        if (sortState) {
            // no real header, and silence the event so we dont get into an
            // infinite loop
            this.normalGrid.headerCt.clearOtherSortStates(null, true);
        }
    },

    onNormalHeaderSortChange: function(headerCt, header, sortState) {
        if (sortState) {
            // no real header, and silence the event so we dont get into an
            // infinite loop
            this.lockedGrid.headerCt.clearOtherSortStates(null, true);
        }
    },

    // going from locked section to unlocked
    /**
     * Unlocks the activeHeader as determined by which menu is open OR a header
     * as specified.
     * @param {Ext.grid.column.Column} [header] Header to unlock from the locked section.
     * Defaults to the header which has the menu open currently.
     * @param {Number} [toIdx=0] The index to move the unlocked header to.
     * @private
     */
    unlock: function(activeHd, toIdx) {
        var me         = this,
            normalGrid = me.normalGrid,
            lockedGrid = me.lockedGrid,
            normalHCt  = normalGrid.headerCt,
            lockedHCt  = lockedGrid.headerCt,
            refreshLocked = false;

        if (!Ext.isDefined(toIdx)) {
            toIdx = 0;
        }
        activeHd = activeHd || lockedHCt.getMenu().activeHeader;

        Ext.suspendLayouts();
        lockedHCt.remove(activeHd, false);
        if (me.syncLockedWidth()) {
            refreshLocked = true;
        }
        activeHd.locked = false;
        normalHCt.insert(toIdx, activeHd);
        if (refreshLocked) {
            me.lockedGrid.getView().refresh();
        }
        me.normalGrid.getView().refresh();
        Ext.resumeLayouts(true);

        me.fireEvent('unlockcolumn', me, activeHd);
    },

    applyColumnsState: function (columns) {
        var me = this,
            lockedGrid = me.lockedGrid,
            lockedHeaderCt = lockedGrid.headerCt,
            normalHeaderCt = me.normalGrid.headerCt,
            lockedCols = lockedHeaderCt.items,
            normalCols = normalHeaderCt.items,
            existing,
            locked = [],
            normal = [],
            lockedDefault,
            lockedWidth = 1;

        Ext.each(columns, function (col) {
            function matches (item) {
                return item.headerId == col.id;
            }

            lockedDefault = true;
            if (!(existing = lockedCols.findBy(matches))) {
                existing = normalCols.findBy(matches);
                lockedDefault = false;
            }

            if (existing) {
                if (existing.applyColumnState) {
                    existing.applyColumnState(col);
                }
                if (!Ext.isDefined(existing.locked)) {
                    existing.locked = lockedDefault;
                }
                if (existing.locked) {
                    locked.push(existing);
                    if (!existing.hidden && Ext.isNumber(existing.width)) {
                        lockedWidth += existing.width;
                    }
                } else {
                    normal.push(existing);
                }
            }
        });

        // state and config must have the same columns (compare counts for now):
        if (locked.length + normal.length == lockedCols.getCount() + normalCols.getCount()) {
            lockedHeaderCt.removeAll(false);
            normalHeaderCt.removeAll(false);

            lockedHeaderCt.add(locked);
            normalHeaderCt.add(normal);

            lockedGrid.setWidth(lockedWidth);
        }
    },

    getColumnsState: function () {
        var me = this,
            locked = me.lockedGrid.headerCt.getColumnsState(),
            normal = me.normalGrid.headerCt.getColumnsState();

        return locked.concat(normal);
    },

    // we want to totally override the reconfigure behaviour here, since we're creating 2 sub-grids
    reconfigureLockable: function(store, columns) {
        var me = this,
            lockedGrid = me.lockedGrid,
            normalGrid = me.normalGrid;

        if (columns) {
            Ext.suspendLayouts();
            lockedGrid.headerCt.removeAll();
            normalGrid.headerCt.removeAll();

            columns = me.processColumns(columns);
            lockedGrid.setWidth(columns.lockedWidth);
            lockedGrid.headerCt.add(columns.locked.items);
            normalGrid.headerCt.add(columns.normal.items);
            Ext.resumeLayouts(true);
        }

        if (store) {
            store = Ext.data.StoreManager.lookup(store);
            me.store = store;
            lockedGrid.bindStore(store);
            normalGrid.bindStore(store);
        } else {
            lockedGrid.getView().refresh();
            normalGrid.getView().refresh();
        }
    }
});
