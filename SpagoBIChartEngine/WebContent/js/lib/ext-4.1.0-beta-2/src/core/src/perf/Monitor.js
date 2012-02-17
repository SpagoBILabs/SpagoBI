/**
 * @class Ext.perf.Monitor
 * @singleton
 * @private
 */
Ext.define('Ext.perf.Monitor', {
    singleton: true,
    alternateClassName: 'Ext.Perf',

    requires: [
        'Ext.perf.Accumulator'
    ],

    constructor: function () {
        this.accumulators = [];
        this.accumulatorsByName = {};
    },

    calibrate: function () {
        var accum = new Ext.perf.Accumulator('$'),
            total = accum.total,
            getTimestamp = Ext.perf.Accumulator.getTimestamp,
            count = 0,
            frame,
            endTime,
            startTime;

        startTime = getTimestamp();

        do {
            frame = accum.enter();
            frame.leave();
            ++count;
        } while (total.sum < 100);

        endTime = getTimestamp();

        return (endTime - startTime) / count;
    },

    get: function (name) {
        var me = this,
            accum = me.accumulatorsByName[name];

        if (!accum) {
            me.accumulatorsByName[name] = accum = new Ext.perf.Accumulator(name);
            me.accumulators.push(accum);
        }

        return accum;
    },

    enter: function (name) {
        return this.get(name).enter();
    },

    monitor: function (name, fn, scope) {
        this.get(name).monitor(fn, scope);
    },

    report: function () {
        var me = this,
            accumulators = me.accumulators,
            calibration = me.calibrate(),
            report = ['Calibration: ' + Math.round(calibration * 100) / 100 + ' msec/sample'];

        accumulators.sort(function (a, b) {
            return (a.name < b.name) ? -1 : ((b.name < a.name) ? 1 : 0);
        });

        Ext.each(accumulators, function (accum) {
            report.push(accum.format(calibration));
        });
        Ext.log(report.join('\n'));
    },

    getData: function (all) {
        var ret = {},
            accumulators = this.accumulators;

        Ext.each(accumulators, function (accum) {
            if (all || accum.count) {
                ret[accum.name] = accum.getData();
            }
        });

        return ret;
    },

    setup: function (config) {
        if (!config) {
            config = {
                /*insertHtml: {
                    'Ext.dom.Helper': 'insertHtml'
                },*/
                /*xtplCompile: {
                    'Ext.XTemplateCompiler': 'compile'
                },*/
//                doInsert: {
//                    'Ext.Template': 'doInsert'
//                },
//                applyOut: {
//                    'Ext.XTemplate': 'applyOut'
//                },
                render: {
                    'Ext.AbstractComponent': 'render'
                },
//                fnishRender: {
//                    'Ext.AbstractComponent': 'finishRender'
//                },
//                renderSelectors: {
//                    'Ext.AbstractComponent': 'applyRenderSelectors'
//                },
//                compAddCls: {
//                    'Ext.AbstractComponent': 'addCls'
//                },
//                compRemoveCls: {
//                    'Ext.AbstractComponent': 'removeCls'
//                },
//                getStyle: {
//                    'Ext.core.Element': 'getStyle'
//                },
//                setStyle: {
//                    'Ext.core.Element': 'setStyle'
//                },
//                addCls: {
//                    'Ext.core.Element': 'addCls'
//                },
//                removeCls: {
//                    'Ext.core.Element': 'removeCls'
//                },
//                measure: {
//                    'Ext.layout.component.Component': 'measureAutoDimensions'
//                },
//                moveItem: {
//                    'Ext.layout.Layout': 'moveItem'
//                },
//                layoutFlush: {
//                    'Ext.layout.Context': 'flush'
//                },
                layout: {
                    'Ext.layout.Context': 'run'
                }
            };
        }

        this.currentConfig = config;

        Ext.Object.each(config, function (accumName, taps) {
            var accum = Ext.Perf.get(accumName);

            Ext.Object.each(taps, function (className, methods) {
                accum.tap(className, methods);
            });
        });
    }
});
