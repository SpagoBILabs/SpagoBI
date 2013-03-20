Ext.define('Ext.ux.touch.grid.feature.Abstract', {
    config : {
        events   : {},
        extraCls : null,
        grid     : null
    },

    constructor : function(config) {
        this.initConfig(config);

        this.callParent([config]);
    },

    updateEvents : function(events) {
        var me   = this,
            grid = me.getGrid(),
            cls, clsEvents;

        for (cls in events) {
            if (events.hasOwnProperty(cls)) {
                clsEvents = events[cls];

                if (cls === 'grid') {
                    cls = grid;
                } else if (cls === 'header') {
                    cls = grid.getHeader();
                } else if (cls === 'headerEl') {
                    cls = grid.getHeader().element;
                } else if (cls === 'gridBody') {
                    cls = grid.element.down('div.x-body');
                } else if (cls === 'store') {
                    cls = grid.getStore();
                } else {
                    cls = grid[cls];
                }

                var eventName, eventFn, eventCfg;

                if (Ext.isObject(cls)) {
                    for (eventName in clsEvents) {
                        eventFn = clsEvents[eventName];

                        if (Ext.isObject(eventFn)) {
                            eventCfg = Ext.apply({}, eventFn);

                            delete eventCfg.fn;

                            eventFn = eventFn.fn;
                        }

                        cls.on(eventName, me[eventFn], me, eventCfg || {});
                    }
                } else {
                    console.warn('Class could not be found in config.events Object');
                }
            }
        }
    }
});