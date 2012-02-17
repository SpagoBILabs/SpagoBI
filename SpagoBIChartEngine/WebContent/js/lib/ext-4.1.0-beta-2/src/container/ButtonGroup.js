/**
 * Provides a container for arranging a group of related Buttons in a tabular manner.
 *
 *     @example
 *     Ext.create('Ext.panel.Panel', {
 *         title: 'Panel with ButtonGroup',
 *         width: 300,
 *         height:200,
 *         renderTo: document.body,
 *         bodyPadding: 10,
 *         html: 'HTML Panel Content',
 *         tbar: [{
 *             xtype: 'buttongroup',
 *             columns: 3,
 *             title: 'Clipboard',
 *             items: [{
 *                 text: 'Paste',
 *                 scale: 'large',
 *                 rowspan: 3,
 *                 iconCls: 'add',
 *                 iconAlign: 'top',
 *                 cls: 'btn-as-arrow'
 *             },{
 *                 xtype:'splitbutton',
 *                 text: 'Menu Button',
 *                 scale: 'large',
 *                 rowspan: 3,
 *                 iconCls: 'add',
 *                 iconAlign: 'top',
 *                 arrowAlign:'bottom',
 *                 menu: [{ text: 'Menu Item 1' }]
 *             },{
 *                 xtype:'splitbutton', text: 'Cut', iconCls: 'add16', menu: [{text: 'Cut Menu Item'}]
 *             },{
 *                 text: 'Copy', iconCls: 'add16'
 *             },{
 *                 text: 'Format', iconCls: 'add16'
 *             }]
 *         }]
 *     });
 *
 */
Ext.define('Ext.container.ButtonGroup', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.buttongroup',
    alternateClassName: 'Ext.ButtonGroup',

    /**
     * @cfg {Number} columns
     * The `columns` configuration property passed to the {@link #layout configured layout manager}.
     * See {@link Ext.layout.container.Table#columns}.
     */

    /**
     * @cfg {String} baseCls
     * @inheritdoc
     */
    baseCls: Ext.baseCSSPrefix + 'btn-group',

    /**
     * @cfg {Object} layout
     * @inheritdoc
     */
    layout: {
        type: 'table'
    },

    defaultType: 'button',

    /**
     * @cfg {Boolean} frame
     * @inheritdoc
     */
    frame: true,

    frameHeader: false,

    internalDefaults: {removeMode: 'container', hideParent: true},

    initComponent : function(){
        // Copy the component's columns config to the layout if specified
        var me = this,
            cols = me.columns;

        me.noTitleCls = me.baseCls + '-notitle';
        if (cols) {
            me.layout = Ext.apply({}, {columns: cols}, me.layout);
        }

        if (!me.title) {
            me.addCls(me.noTitleCls);
        }
        me.callParent(arguments);
    },

    beforeRender: function() {
        var me = this;

        me.callParent();

        //we need to add an addition item in here so the ButtonGroup title is centered
        if (me.header) {
            // Header text cannot flex, but must be natural size if it's being centered
            delete me.header.items.items[0].flex;

            // For Centering, surround the text with two flex:1 spacers.
            me.header.insert(1, {
                xtype: 'component',
                ui   : me.ui,
                flex : 1
            });
            me.header.insert(0, {
                xtype: 'component',
                ui   : me.ui,
                flex : 1
            });
        }
        me.callParent(arguments);
    },

    // private
    onBeforeAdd: function(component) {
        if (component.is('button')) {
            component.ui = component.ui + '-toolbar';
        }
        this.callParent(arguments);
    },

    //private
    applyDefaults: function(c) {
        if (!Ext.isString(c)) {
            c = this.callParent(arguments);
            var d = this.internalDefaults;
            if (c.events) {
                Ext.applyIf(c.initialConfig, d);
                Ext.apply(c, d);
            } else {
                Ext.applyIf(c, d);
            }
        }
        return c;
    }

    /**
     * @cfg {Array} tools
     * @private
     */
    /**
     * @cfg {Boolean} collapsible
     * @private
     */
    /**
     * @cfg {Boolean} collapseMode
     * @private
     */
    /**
     * @cfg {Boolean} animCollapse
     * @private
     */
    /**
     * @cfg {Boolean} closable
     * @private
     */
});
