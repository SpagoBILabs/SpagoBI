/* =============================================================================
* Added by Ghedin Alberto (March 2012)
* In Sencha 2.1.1 there are some issue with the height of the toolbar
============================================================================= */
Ext.override(Ext.MessageBox, {
	updateButtons: function(newButtons) {
        var me = this;

        // If there are no new buttons or it is an empty array, set newButtons
        // to false
        newButtons = (!newButtons || newButtons.length === 0) ? false : newButtons;

        if (newButtons) {
            if (me.buttonsToolbar) {
                me.buttonsToolbar.show();
                me.buttonsToolbar.removeAll();
                me.buttonsToolbar.setItems(newButtons);
            } else {
                me.buttonsToolbar = Ext.create('Ext.Toolbar', {
                	height: 30,//THIS IS THE CHANGE
                    docked     : 'bottom',
                    defaultType: 'button',
                    layout     : {
                        type: 'hbox',
                        pack: 'center'
                    },
                    ui         : me.getUi(),
                    cls        : me.getBaseCls() + '-buttons',
                    items      : newButtons
                });

                me.add(me.buttonsToolbar);
            }
        } else if (me.buttonsToolbar) {
            me.buttonsToolbar.hide();
        }
    }
});