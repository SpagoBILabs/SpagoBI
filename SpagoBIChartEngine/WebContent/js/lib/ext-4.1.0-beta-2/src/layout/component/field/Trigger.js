/**
 * Layout class for {@link Ext.form.field.Trigger} fields. Adjusts the input field size to accommodate
 * the trigger button(s).
 * @private
 */
Ext.define('Ext.layout.component.field.Trigger', {

    /* Begin Definitions */

    alias: 'layout.triggerfield',

    extend: 'Ext.layout.component.field.Field',

    /* End Definitions */

    type: 'triggerfield',

    beginLayout: function(ownerContext) {
        var me = this,
            owner = me.owner,
            flags;

        ownerContext.triggerWrap = ownerContext.getEl('triggerWrap');

        me.callParent(arguments);

        // if any of these important states have changed, sync them now:
        flags = owner.getTriggerStateFlags();
        if (flags != owner.lastTriggerStateFlags) {
            owner.lastTriggerStateFlags = flags;
            me.updateEditState();
        }
    },

    beginLayoutFixed: function (ownerContext, width) {
        var owner = ownerContext.target;

        this.callParent(arguments);

        owner.inputCell.setStyle('width', '100%');
        owner.inputEl.setStyle('width', '100%');
        owner.triggerWrap.setStyle('width', width);
        owner.triggerWrap.setStyle('table-layout', 'fixed');
    },

    beginLayoutShrinkWrap: function (ownerContext) {
        var owner = ownerContext.target,
            emptyString = '';

        this.callParent(arguments);

        owner.triggerWrap.setStyle('width', emptyString);
        owner.inputCell.setStyle('width', emptyString);
        owner.inputEl.setStyle('width', emptyString);
        owner.triggerWrap.setStyle('table-layout', 'auto');
    },

    updateEditState: function() {
        var me = this,
            owner = me.owner,
            inputEl = owner.inputEl,
            noeditCls = Ext.baseCSSPrefix + 'trigger-noedit',
            displayed,
            readOnly;

        if (me.owner.readOnly) {
            inputEl.addCls(noeditCls);
            readOnly = true;
            displayed = false;
        } else {
            if (me.owner.editable) {
                inputEl.removeCls(noeditCls);
                readOnly = false;
            } else {
                inputEl.addCls(noeditCls);
                readOnly = true;
            }
            displayed = !me.owner.hideTrigger;
        }

        owner.triggerCell.setDisplayed(displayed);
        inputEl.dom.readOnly = readOnly;
    }
});