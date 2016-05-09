Ext.define('Ext.ux.touch.grid.View', {
    extend : 'Ext.dataview.DataView',

    requires : [
        'Ext.ux.touch.grid.List'
    ],

    constructor: function(config) {
        //<debug>
        if (Ext.Logger) {
            Ext.Logger.deprecate('Ext.ux.touch.grid.View is now deprecated, Ext.ux.touch.grid.List should be used and now extends Ext.dataview.List');
        }
        //</debug>

        return new Ext.ux.touch.grid.List(config);
    }
});