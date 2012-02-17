/**
 * This feature allows to display the grid rows aggregated into groups as specified by the {@link Ext.data.Store#groupers}
 * specified on the Store. The group will show the title for the group name and then the appropriate records for the group
 * underneath. The groups can also be expanded and collapsed.
 * 
 * ## Extra Events
 * This feature adds several extra events that will be fired on the grid to interact with the groups:
 *
 *  - {@link #groupclick}
 *  - {@link #groupdblclick}
 *  - {@link #groupcontextmenu}
 *  - {@link #groupexpand}
 *  - {@link #groupcollapse}
 * 
 * ## Menu Augmentation
 * This feature adds extra options to the grid column menu to provide the user with functionality to modify the grouping.
 * This can be disabled by setting the {@link #enableGroupingMenu} option. The option to disallow grouping from being turned off
 * by thew user is {@link #enableNoGroups}.
 * 
 * ## Controlling Group Text
 * The {@link #groupHeaderTpl} is used to control the rendered title for each group. It can modified to customized
 * the default display.
 * 
 * ## Example Usage
 * 
 *     var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
 *         groupHeaderTpl: 'Group: {name} ({rows.length})', //print the number of items in the group
 *         startCollapsed: true // start all groups collapsed
 *     });
 * 
 * @author Nicolas Ferrero
 */
Ext.define('Ext.grid.feature.Grouping', {
    extend: 'Ext.grid.feature.Feature',
    alias: 'feature.grouping',

    eventPrefix: 'group',
    eventSelector: '.' + Ext.baseCSSPrefix + 'grid-group-hd',

    constructor: function() {
        var me = this;
        
        me.collapsedState = {};
        me.callParent(arguments);
    },
    
    /**
     * @event groupclick
     * @param {Ext.view.Table} view
     * @param {HTMLElement} node
     * @param {String} group The name of the group
     * @param {Ext.EventObject} e
     */

    /**
     * @event groupdblclick
     * @param {Ext.view.Table} view
     * @param {HTMLElement} node
     * @param {String} group The name of the group
     * @param {Ext.EventObject} e
     */

    /**
     * @event groupcontextmenu
     * @param {Ext.view.Table} view
     * @param {HTMLElement} node
     * @param {String} group The name of the group
     * @param {Ext.EventObject} e
     */

    /**
     * @event groupcollapse
     * @param {Ext.view.Table} view
     * @param {HTMLElement} node
     * @param {String} group The name of the group
     * @param {Ext.EventObject} e
     */

    /**
     * @event groupexpand
     * @param {Ext.view.Table} view
     * @param {HTMLElement} node
     * @param {String} group The name of the group
     * @param {Ext.EventObject} e
     */

    /**
     * @cfg {String/Array/Ext.Template} groupHeaderTpl
     * A string Template snippet, an array of strings (optionally followed by an object containing Template methods) to be used to construct a Template, or a Template instance.
     * 
     * - Example 1 (Template snippet):
     * 
     *       groupHeaderTpl: 'Group: {name}'
     *     
     * - Example 2 (Array):
     * 
     *       groupHeaderTpl: [
     *           'Group: ',
     *           '<div>{name:this.formatName}</div>',
     *           {
     *               formatName: function(name) {
     *                   return Ext.String.trim(name);
     *               }
     *           }
     *       ]
     *     
     * - Example 3 (Template Instance):
     * 
     *       groupHeaderTpl: Ext.create('Ext.XTemplate',
     *           'Group: ',
     *           '<div>{name:this.formatName}</div>',
     *           {
     *               formatName: function(name) {
     *                   return Ext.String.trim(name);
     *               }
     *           }
     *       )
     */
    groupHeaderTpl: '{columnName}: {name}',

    /**
     * @cfg {Number} depthToIndent
     * Number of pixels to indent per grouping level
     */
    depthToIndent: 17,

    collapsedCls: Ext.baseCSSPrefix + 'grid-group-collapsed',
    hdCollapsedCls: Ext.baseCSSPrefix + 'grid-group-hd-collapsed',

    /**
     * @cfg
     * Text displayed in the grid header menu for grouping by header.
     */
    //<locale>
    groupByText : 'Group By This Field',
    //</locale>
    /**
     * @cfg
     * Text displayed in the grid header for enabling/disabling grouping.
     */
    //<locale>
    showGroupsText : 'Show in Groups',
    //</locale>

    /**
     * @cfg
     * True to hide the header that is currently grouped.
     */
    hideGroupedHeader : false,

    /**
     * @cfg
     * True to start all groups collapsed.
     */
    startCollapsed : false,

    /**
     * @cfg
     * True to enable the grouping control in the header menu.
     */
    enableGroupingMenu : true,

    /**
     * @cfg
     * True  to allow the user to turn off grouping.
     */
    enableNoGroups : true,
    
    enable: function() {
        var me    = this,
            view  = me.view,
            store = view.store,
            groupToggleMenuItem;
            
        me.lastGroupField = me.getGroupField();

        if (me.lastGroupIndex) {
            store.group(me.lastGroupIndex);
        }
        me.callParent();
        groupToggleMenuItem = me.view.headerCt.getMenu().down('#groupToggleMenuItem');
        groupToggleMenuItem.setChecked(true, true);
        me.refreshIf();
    },

    disable: function() {
        var me    = this,
            view  = me.view,
            store = view.store,
            remote = store.remoteGroup,
            groupToggleMenuItem,
            lastGroup;
            
        lastGroup = store.groupers.first();
        if (lastGroup) {
            me.lastGroupIndex = lastGroup.property;
            me.block();
            store.clearGrouping();
            me.unblock();
        }
        
        me.callParent();
        groupToggleMenuItem = me.view.headerCt.getMenu().down('#groupToggleMenuItem');
        groupToggleMenuItem.setChecked(true, true);
        groupToggleMenuItem.setChecked(false, true);
        if (!remote) {
            view.refresh();
        }
    },
    
    refreshIf: function() {
        if (this.blockRefresh !== true) {
            this.view.refresh();
        }    
    },

    getFeatureTpl: function(values, parent, x, xcount) {
        var me = this,
            tpl = me.groupHeaderTpl;

        // if the groupHeaderTpl is a string or array, lets turn it into an XTemplate before proceeding
        if(Ext.isString(tpl)) {
            tpl = new Ext.XTemplate(tpl);
        } else if(Ext.isArray(tpl)) {
            tpl = Ext.Array.clone(tpl);
            Ext.Array.splice(tpl, 0, 0, 'Ext.XTemplate');
            tpl = Ext.create.apply(Ext, tpl);
        }
        me.groupHeaderTpl = tpl;
   
        return [
            '<tpl if="typeof rows !== \'undefined\'">',
                // group row tpl
                '<tr class="' + Ext.baseCSSPrefix + 'grid-group-hd ' + (me.startCollapsed ? me.hdCollapsedCls : '') + ' {hdCollapsedCls}"><td class="' + Ext.baseCSSPrefix + 'grid-cell" colspan="' + parent.columns.length + '" {[this.indentByDepth(values)]}><div class="' + Ext.baseCSSPrefix + 'grid-cell-inner"><div class="' + Ext.baseCSSPrefix + 'grid-group-title">{collapsed}{[this.renderGroupHeaderTpl(values)]}</div></div></td></tr>',
                // this is the rowbody
                '<tr id="{viewId}-gp-{name}" class="' + Ext.baseCSSPrefix + 'grid-group-body ' + (me.startCollapsed ? me.collapsedCls : '') + ' {collapsedCls}"><td colspan="' + parent.columns.length + '">{[this.recurse(values)]}</td></tr>',
            '</tpl>'
        ].join('');
    },

    getFragmentTpl: function() {
        var me = this;
        return {
            indentByDepth: me.indentByDepth,
            depthToIndent: me.depthToIndent,
            renderGroupHeaderTpl: function(values) {
                return me.groupHeaderTpl.apply(values);
            }
        };
    },

    indentByDepth: function(values) {
        var depth = values.depth || 0;
        return 'style="padding-left:'+ depth * this.depthToIndent + 'px;"';
    },

    // Containers holding these components are responsible for
    // destroying them, we are just deleting references.
    destroy: function() {
        var me = this;
        
        delete me.view;
        delete me.prunedHeader;
    },

    // perhaps rename to afterViewRender
    attachEvents: function() {
        var me = this,
            view = me.view;

        view.on({
            scope: me,
            groupclick: me.onGroupClick,
            rowfocus: me.onRowFocus
        });
        view.mon(view.store, 'groupchange', me.onGroupChange, me);

        if (me.enableGroupingMenu) {
            me.injectGroupingMenu();
        }

        me.pruneGroupedHeader();

        me.lastGroupField = me.getGroupField();
        me.block();
        me.onGroupChange();
        me.unblock();
    },
    
    injectGroupingMenu: function() {
        var me       = this,
            view     = me.view,
            headerCt = view.headerCt;
        headerCt.showMenuBy = me.showMenuBy;
        headerCt.getMenuItems = me.getMenuItems();
    },
    
    showMenuBy: function(t, header) {
        var menu = this.getMenu(),
            groupMenuItem  = menu.down('#groupMenuItem'),
            groupableMth = header.groupable === false ?  'disable' : 'enable';
            
        groupMenuItem[groupableMth]();
        Ext.grid.header.Container.prototype.showMenuBy.apply(this, arguments);
    },
    
    getMenuItems: function() {
        var me                 = this,
            groupByText        = me.groupByText,
            disabled           = me.disabled,
            showGroupsText     = me.showGroupsText,
            enableNoGroups     = me.enableNoGroups,
            groupMenuItemClick = Ext.Function.bind(me.onGroupMenuItemClick, me),
            groupToggleMenuItemClick = Ext.Function.bind(me.onGroupToggleMenuItemClick, me);
        
        // runs in the scope of headerCt
        return function() {
            var o = Ext.grid.header.Container.prototype.getMenuItems.call(this);
            o.push('-', {
                iconCls: Ext.baseCSSPrefix + 'group-by-icon',
                itemId: 'groupMenuItem',
                text: groupByText,
                handler: groupMenuItemClick
            });
            if (enableNoGroups) {
                o.push({
                    itemId: 'groupToggleMenuItem',
                    text: showGroupsText,
                    checked: !disabled,
                    checkHandler: groupToggleMenuItemClick
                });
            }
            return o;
        };
    },


    /**
     * Group by the header the user has clicked on.
     * @private
     */
    onGroupMenuItemClick: function(menuItem, e) {
        var me = this,
            menu = menuItem.parentMenu,
            hdr  = menu.activeHeader,
            view = me.view,
            store = view.store,
            remote = store.remoteGroup;

        delete me.lastGroupIndex;
        me.block();
        me.enable();
        store.group(hdr.dataIndex);
        me.pruneGroupedHeader();
        me.unblock();
        if (!remote) {
            view.refresh();
        }  
    },
    
    block: function(){
        this.blockRefresh = this.view.blockRefresh = true;
    },
    
    unblock: function(){
        this.blockRefresh = this.view.blockRefresh = false;
    },

    /**
     * Turn on and off grouping via the menu
     * @private
     */
    onGroupToggleMenuItemClick: function(menuItem, checked) {
        this[checked ? 'enable' : 'disable']();
    },

    /**
     * Prunes the grouped header from the header container
     * @private
     */
    pruneGroupedHeader: function() {
        var me = this,
            header = me.getGroupedHeader();

        if (me.hideGroupedHeader && header) {
            if (me.prunedHeader) {
                me.prunedHeader.show();
            }
            me.prunedHeader = header;
            header.hide();
        }
    },
    
    getGroupedHeader: function(){
        var groupField = this.getGroupField(),
            headerCt = this.view.headerCt;
            
        return groupField ? headerCt.down('[dataIndex=' + groupField + ']') : null;
    },

    getGroupField: function(){
        var group = this.view.store.groupers.first();
        if (group) {
            return group.property;    
        }
        return ''; 
    },

    /**
     * When a row gains focus, expand the groups above it
     * @private
     */
    onRowFocus: function(rowIdx) {
        var node    = this.view.getNode(rowIdx),
            groupBd = Ext.fly(node).up('.' + this.collapsedCls);

        if (groupBd) {
            // for multiple level groups, should expand every groupBd
            // above
            this.expand(groupBd);
        }
    },

    /**
     * Expand a group
     * @param {String/Ext.Element} group The group name, or the element that contains
     * the group body
     */
    expand: function(group, /*private*/ preventSizeCalculation) {
        var me = this,
            view = me.view,
            grid = view.up('gridpanel'),
            groupBdDom;
            
        
        if (Ext.isString(group)) {
            group = Ext.get(view.id + '-gp-' + group);
        }    
        groupBdDom = Ext.getDom(group);
            
        me.collapsedState[groupBdDom.id] = false;

        group.removeCls(me.collapsedCls);
        group.prev().removeCls(me.hdCollapsedCls);

        if (preventSizeCalculation !== true) {
            view.refreshHeight();
        }
        view.fireEvent('groupexpand');
    },
    
    /**
     * Expand all groups
     */
    expandAll: function(){
        var me = this,
            view = me.view;
            
        view.el.select(me.eventSelector).each(function(group){
            me.expand(group.next(), true);
        });
        view.refreshHeight();
    },

    /**
     * Collapse a group
     * @param {String/Ext.Element} group The group name, or the element that contains
     * group body
     * @private
     */
    collapse: function(group, /*private*/ preventSizeCalculation) {
        var me = this,
            view = me.view,
            grid = view.up('gridpanel'),
            groupBdDom;
            
        if (Ext.isString(group)) {
            group = Ext.get(view.id + '-gp-' + group);    
        }
        groupBdDom = Ext.getDom(group);
            
        me.collapsedState[groupBdDom.id] = true;

        group.addCls(me.collapsedCls);
        group.prev().addCls(me.hdCollapsedCls);
        
        if (preventSizeCalculation !== true) {
            view.refreshHeight();
        }
        view.fireEvent('groupcollapse');
    },
    
    /**
     * Collapse all groups
     */
    collapseAll: function(){
        var me = this,
            view = me.view;
            
        view.el.select(me.eventSelector).each(function(group){
            me.collapse(group.next(), true);
        });
        view.refreshHeight();
    },
    
    onGroupChange: function(){
        var me = this,
            field = me.getGroupField(),
            menuItem,
            visibleGridColumns,
            groupingByLastVisibleColumn;
            
        if (me.hideGroupedHeader) {
            if (me.lastGroupField) {
                menuItem = me.getMenuItem(me.lastGroupField);
                if (menuItem) {
                    menuItem.setChecked(true);
                }
            }
            if (field) {
                visibleGridColumns = me.view.headerCt.getVisibleGridColumns();

                // See if we are being asked to group by the sole remaining visible column.
                // If so, then do not hide that column.
                groupingByLastVisibleColumn = ((visibleGridColumns.length === 1) && (visibleGridColumns[0].dataIndex == field));
                menuItem = me.getMenuItem(field);
                if (menuItem && !groupingByLastVisibleColumn) {
                    menuItem.setChecked(false);
                }
            }
        }
        if (me.blockRefresh !== true) {
            me.view.refresh();
        }
        me.lastGroupField = field;
    },
    
    /**
     * Gets the related menu item for a dataIndex
     * @private
     * @return {Ext.grid.header.Container} The header
     */
    getMenuItem: function(dataIndex){
        var view = this.view,
            header = view.headerCt.down('gridcolumn[dataIndex=' + dataIndex + ']'),
            menu = view.headerCt.getMenu();
            
        return header ? menu.down('menuitem[headerId='+ header.id +']') : null;
    },

    /**
     * Toggle between expanded/collapsed state when clicking on
     * the group.
     * @private
     */
    onGroupClick: function(view, group, idx, foo, e) {
        var me = this,
            toggleCls = me.toggleCls,
            groupBd = Ext.fly(group.nextSibling, '_grouping');

        if (groupBd.hasCls(me.collapsedCls)) {
            me.expand(groupBd);
        } else {
            me.collapse(groupBd);
        }
    },

    // Injects isRow and closeRow into the metaRowTpl.
    getMetaRowTplFragments: function() {
        return {
            isRow: this.isRow,
            closeRow: this.closeRow
        };
    },

    // injected into rowtpl and wrapped around metaRowTpl
    // becomes part of the standard tpl
    isRow: function() {
        return '<tpl if="typeof rows === \'undefined\'">';
    },

    // injected into rowtpl and wrapped around metaRowTpl
    // becomes part of the standard tpl
    closeRow: function() {
        return '</tpl>';
    },

    // isRow and closeRow are injected via getMetaRowTplFragments
    mutateMetaRowTpl: function(metaRowTpl) {
        metaRowTpl.unshift('{[this.isRow()]}');
        metaRowTpl.push('{[this.closeRow()]}');
    },

    // injects an additional style attribute via tdAttrKey with the proper
    // amount of padding
    getAdditionalData: function(data, idx, record, orig) {
        var view = this.view,
            hCt  = view.headerCt,
            col  = hCt.items.getAt(0),
            o = {},
            tdAttrKey = col.id + '-tdAttr';

        // maintain the current tdAttr that a user may ahve set.
        o[tdAttrKey] = this.indentByDepth(data) + " " + (orig[tdAttrKey] ? orig[tdAttrKey] : '');
        o.collapsed = 'true';
        o.data = record.getData();
        return o;
    },

    // return matching preppedRecords
    getGroupRows: function(group, records, preppedRecords, fullWidth) {
        var me = this,
            children = group.children,
            rows = group.rows = [],
            view = me.view,
            header = me.getGroupedHeader(),
            groupField = me.getGroupField(),
            index = -1,
            rec;
            
        group.viewId = view.id;

        Ext.Array.each(records, function(record, idx) {
            if (record.get(groupField) == group.name) {
                index = idx;
            }
            if (Ext.Array.indexOf(children, record) != -1) {
                rows.push(Ext.apply(preppedRecords[idx], {
                    depth: 1
                }));
            }
        });
        delete group.children;
        group.fullWidth = fullWidth;
        group.columnName = header ? header.text : groupField;
        group.rawName = group.name;
        // Here we get the rendered value of the column
        if (header && index > -1) {
            group.name = preppedRecords[index][header.id];
        }
        if (me.collapsedState[view.id + '-gp-' + group.name]) {
            group.collapsedCls = me.collapsedCls;
            group.hdCollapsedCls = me.hdCollapsedCls;
        }

        return group;
    },

    // return the data in a grouped format.
    collectData: function(records, preppedRecords, startIndex, fullWidth, o) {
        var me    = this,
            store = me.view.store,
            groups;
            
        if (!me.disabled && store.isGrouped()) {
            groups = store.getGroups();
            Ext.Array.each(groups, function(group, idx){
                me.getGroupRows(group, records, preppedRecords, fullWidth);
            }, me);
            return {
                rows: groups,
                fullWidth: fullWidth
            };
        }
        return o;
    },
    
    // adds the groupName to the groupclick, groupdblclick, groupcontextmenu
    // events that are fired on the view. Chose not to return the actual
    // group itself because of its expense and because developers can simply
    // grab the group via store.getGroups(groupName)
    getFireEventArgs: function(type, view, featureTarget, e) {
        var returnArray = [type, view, featureTarget],
            groupBd     = Ext.fly(featureTarget.nextSibling, '_grouping'),
            groupBdId   = Ext.getDom(groupBd).id,
            prefix      = view.id + '-gp-',
            groupName   = groupBdId.substr(prefix.length);
        
        returnArray.push(groupName, e);
        
        return returnArray;
    }
});
