/**
 * This is a layout that will render form Fields, one under the other all stretched to the Container width.
 *
 *     @example
 *     Ext.create('Ext.Panel', {
 *         width: 500,
 *         height: 300,
 *         title: "FormLayout Panel",
 *         layout: 'form',
 *         renderTo: Ext.getBody(),
 *         bodyPadding: 5,
 *         defaultType: 'textfield',
 *         items: [{
 *            fieldLabel: 'First Name',
 *             name: 'first',
 *             allowBlank:false
 *         },{
 *             fieldLabel: 'Last Name',
 *             name: 'last'
 *         },{
 *             fieldLabel: 'Company',
 *             name: 'company'
 *         }, {
 *             fieldLabel: 'Email',
 *             name: 'email',
 *             vtype:'email'
 *         }, {
 *             fieldLabel: 'DOB',
 *             name: 'dob',
 *             xtype: 'datefield'
 *         }, {
 *             fieldLabel: 'Age',
 *             name: 'age',
 *             xtype: 'numberfield',
 *             minValue: 0,
 *             maxValue: 100
 *         }, {
 *             xtype: 'timefield',
 *             fieldLabel: 'Time',
 *             name: 'time',
 *             minValue: '8:00am',
 *             maxValue: '6:00pm'
 *         }],
 *     });
 *
 * Note that any configured {@link Ext.Component#padding padding} will be ignored on items within a Form layout.
 */
Ext.define('Ext.layout.container.Form', {

    /* Begin Definitions */

    alias: 'layout.form',
    extend: 'Ext.layout.container.Auto',
    alternateClassName: 'Ext.layout.FormLayout',

    /* End Definitions */

    type: 'form',

    manageOverflow: 2,

    childEls: ['formTable'],

    renderTpl: [
        '<table id="{ownerId}-formTable" style="width:100%" cellspacing="0" cellpadding="0">',
            '{%this.renderBody(out,values)%}',
        '</table>',
        '{%this.renderPadder(out,values)%}'
    ],

    calculate : function (ownerContext) {
        var me = this,
            containerSize = me.getContainerSize(ownerContext, true),
            tableWidth,
            childItems,
            i = 0, length;

        // Once we have been widthed, we can impose that width (in a non-dirty setting) upon all children at once
        if (containerSize.gotWidth) {
            this.callParent(arguments);
            tableWidth = me.formTable.dom.offsetWidth;
            childItems = ownerContext.childItems;

            for (length = childItems.length; i < length; ++i) {
                childItems[i].setWidth(tableWidth, false);
            }
        } else {
            me.done = false;
        }
    },

    getRenderTarget: function() {
        return this.formTable;
    },

    getRenderTree: function() {
        var result = this.callParent(arguments),
            i = 0, len = result.length,
            item;

        for (; i < len; i++) {
            item = result[i];
            if (item.tag && item.tag == 'table') {
                item.tag = 'tbody';
                delete item.cellspacing;
                delete item.cellpadding;
                item.cn = '<tr><td class="x-form-item-pad" colspan="3"></td></tr>';
            } else {
                result[i] = {
                    tag: 'tbody',
                    cn: {
                        tag: 'tr',
                        cn: {
                            tag: 'td',
                            colspan: 3,
                            style: 'width:100%',
                            cn: item
                        }
                    }
                };
            }
        }
        return result;
    },

    isValidParent: function(item, target, position) {
        return true;
    },

    isItemShrinkWrap: function(item) {
        return ((item.shrinkWrap === true) ? 3 : item.shrinkWrap||0) & 2;
    },

    getItemSizePolicy: function(item) {
        return {
            setsWidth: 1,
            setsHeight: 0
        }
    }
});
