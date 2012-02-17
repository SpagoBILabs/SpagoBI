/**
 * @author Ed Spencer
 * TabBar is used internally by a {@link Ext.tab.Panel TabPanel} and typically should not need to be created manually.
 * The tab bar automatically removes the default title provided by {@link Ext.panel.Header}
 */
Ext.define('Ext.tab.Bar', {
    extend: 'Ext.panel.Header',
    alias: 'widget.tabbar',
    baseCls: Ext.baseCSSPrefix + 'tab-bar',

    requires: [
        'Ext.tab.Tab'
    ],

    isTabBar: true,
    
    /**
     * @cfg {String} title @hide
     */
    
    /**
     * @cfg {String} iconCls @hide
     */

    // @private
    defaultType: 'tab',

    /**
     * @cfg {Boolean} plain
     * True to not show the full background on the tabbar
     */
    plain: false,

    childEls: [
        'body', 'strip'
    ],

    // @private
    renderTpl: [
        '<div id="{id}-body" class="{baseCls}-body <tpl if="bodyCls"> {bodyCls}</tpl> <tpl if="ui"> {baseCls}-body-{ui}<tpl for="uiCls"> {parent.baseCls}-body-{parent.ui}-{.}</tpl></tpl>"<tpl if="bodyStyle"> style="{bodyStyle}"</tpl>>',
            '{%this.renderContainer(out,values)%}',
        '</div>',
        '<div id="{id}-strip" class="{baseCls}-strip<tpl if="ui"> {baseCls}-strip-{ui}<tpl for="uiCls"> {parent.baseCls}-strip-{parent.ui}-{.}</tpl></tpl>"></div>'
    ],

    /**
     * @cfg {Number} minTabWidth
     * The minimum width for a tab in this tab Bar. Defaults to the tab Panel's {@link Ext.tab.Panel#minTabWidth minTabWidth} value.
     * @deprecated This config is deprecated. It is much easier to use the {@link Ext.tab.Panel#minTabWidth minTabWidth} config on the TabPanel.
     */

    /**
     * @cfg {Number} maxTabWidth
     * The maximum width for a tab in this tab Bar. Defaults to the tab Panel's {@link Ext.tab.Panel#maxTabWidth maxTabWidth} value.
     * @deprecated This config is deprecated. It is much easier to use the {@link Ext.tab.Panel#maxTabWidth maxTabWidth} config on the TabPanel.
     */

    // @private
    initComponent: function() {
        var me = this,
            keys;

        if (me.plain) {
            me.setUI(me.ui + '-plain');
        }

        me.addClsWithUI(me.dock);

        me.addEvents(
            /**
             * @event change
             * Fired when the currently-active tab has changed
             * @param {Ext.tab.Bar} tabBar The TabBar
             * @param {Ext.tab.Tab} tab The new Tab
             * @param {Ext.Component} card The card that was just shown in the TabPanel
             */
            'change'
        );

        me.callParent(arguments);

        me.on({
            click: me.onClick,
            element: 'el',
            delegate: '.' + Ext.baseCSSPrefix + 'tab',
            scope: me
        });

        // TabBar must override the Header's align setting.
        me.layout.align = (me.orientation == 'vertical') ? 'left' : 'top';
        me.layout.overflowHandler = new Ext.layout.container.boxOverflow.Scroller(me.layout);

        me.remove(me.titleCmp);
        delete me.titleCmp;

        Ext.apply(me.renderData, {
            bodyCls: me.bodyCls
        });
    },

    getLayout: function() {
        var me = this;
        me.layout.type = (me.dock === 'top' || me.dock === 'bottom') ? 'hbox' : 'vbox';
        return me.callParent(arguments);
    },

    // @private
    onAdd: function(tab) {
        tab.position = this.dock;
        this.callParent(arguments);
    },
    
    onRemove: function(tab) {
        var me = this;
        
        if (tab === me.previousTab) {
            me.previousTab = null;
        }
        
        if (tab === me.activeTab) {
            me.activeTab = null;
        }
        
        if (me.items.getCount() === 0) {
            me.activeTab = null;
        }
        me.callParent(arguments);    
    },

    afterComponentLayout : function(width) {
        var me = this,
            tab = me.activeTab;
            
        me.callParent(arguments);
        me.strip.setWidth(width);
        
        if (tab && me.rendered) {
            me.layout.overflowHandler.scrollToItem(tab);
        }
    },

    // @private
    onClick: function(e, target) {
        // The target might not be a valid tab el.
        var tab = Ext.getCmp(target.id),
            tabPanel = this.tabPanel;

        target = e.getTarget();

        if (tab && tab.isDisabled && !tab.isDisabled()) {
            if (tab.closable && target === tab.closeEl.dom) {
                tab.onCloseClick();
            } else {
                if (tabPanel) {
                    // TabPanel will card setActiveTab of the TabBar
                    tabPanel.setActiveTab(tab.card);
                } else {
                    this.setActiveTab(tab);
                }
                tab.focus();
            }
        }
    },

    /**
     * @private
     * Closes the given tab by removing it from the TabBar and removing the corresponding card from the TabPanel
     * @param {Ext.tab.Tab} tab The tab to close
     */
    closeTab: function(tab) {
        var me = this,
            card = tab.card,
            tabPanel = me.tabPanel,
            active = tab.active,
            nextTab;

        if (card && card.fireEvent('beforeclose', card) === false) {
            return false;
        }
        
        if (tabPanel && card) {
            // Remove the ownerCt so the tab doesn't get destroyed if the remove is successful
            // We need this so we can have the tab fire it's own close event.
            delete tab.ownerCt;
            tabPanel.remove(card);
            // Remove succeeded
            if (!tabPanel.getComponent(card)) {
                /*
                 * force the close event to fire. By the time this function returns,
                 * the tab is already destroyed and all listeners have been purged
                 * so the tab can't fire itself.
                 */
                tab.fireClose();
                me.remove(tab);
                card.fireEvent('close', card);
            } else {
                // Restore the ownerCt from above
                tab.ownerCt = me;
                return false;
            }
        }

        if (me.items.getCount() >= 1) {
            nextTab = me.previousTab || tab.next('tab') || me.items.first();
            me.setActiveTab(nextTab);
            if (tabPanel) {
                tabPanel.setActiveTab(nextTab.card);
            }
        }

        if (nextTab) {
            nextTab.focus();
        }
    },

    /**
     * @private
     * Marks the given tab as active
     * @param {Ext.tab.Tab} tab The tab to mark active
     */
    setActiveTab: function(tab) {
        if (tab.disabled) {
            return;
        }
        var me = this;
        if (me.activeTab) {
            me.previousTab = me.activeTab;
            me.activeTab.deactivate();
        }
        tab.activate();

        me.activeTab = tab;
        me.fireEvent('change', me, tab, tab.card);
    }
});
