/**
 * @class Ext.fx.Queue
 * Animation Queue mixin to handle chaining and queueing by target.
 * @private
 */

Ext.ns('Ext.fx.target');

Ext.fx.Queue = {

    constructor: function() {
        this.targets = new Ext.util.HashMap();
        this.fxQueue = {};
    },

    // @private
    getFxDefaults: function(targetId) {
        var target = this.targets.get(targetId);
        if (target) {
            return target.fxDefaults;
        }
        return {};
    },

    // @private
    setFxDefaults: function(targetId, obj) {
        var target = this.targets.get(targetId);
        if (target) {
            target.fxDefaults = Ext.apply(target.fxDefaults || {}, obj);
        }
    },

    // @private
    stopAnimation: function(targetId) {
        var me = this,
            queue = me.getFxQueue(targetId),
            ln = queue.length;
        while (ln) {
            queue[ln - 1].end();
            ln--;
        }
    },

    /**
     * @private
     * Returns current animation object if the element has any effects actively running or queued, else returns false.
     */
    getActiveAnimation: function(targetId) {
        var queue = this.getFxQueue(targetId);
        return (queue && !!queue.length) ? queue[0] : false;
    },

    // @private
    hasFxBlock: function(targetId) {
        var queue = this.getFxQueue(targetId);
        return queue && queue[0] && queue[0].block;
    },

    // @private get fx queue for passed target, create if needed.
    getFxQueue: function(targetId) {
        if (!targetId) {
            return false;
        }
        var me = this,
            queue = me.fxQueue[targetId],
            target = me.targets.get(targetId);

        if (!target) {
            return false;
        }

        if (!queue) {
            me.fxQueue[targetId] = [];
            // GarbageCollector will need to clean up Elements since they aren't currently observable
            if (target.type != 'element') {
                target.target.on('destroy', function() {
                    me.fxQueue[targetId] = [];
                });
            }
        }
        return me.fxQueue[targetId];
    },

    // @private
    queueFx: function(anim) {
        var me = this,
            target = anim.target,
            queue, ln;

        if (!target) {
            return;
        }

        queue = me.getFxQueue(target.getId());
        ln = queue.length;

        if (ln) {
            if (anim.concurrent) {
                anim.paused = false;
            }
            else {
                queue[ln - 1].on('afteranimate', function() {
                    anim.paused = false;
                });
            }
        }
        else {
            anim.paused = false;
        }
        anim.on('afteranimate', function() {
            // COMPAT 
            queue.remove(anim);
            if (anim.remove) {
                if (target.type == 'element') {
                    var el = Ext.get(target.id);
                    if (el) {
                        el.remove();
                    }
                }
            }
        }, this);
        queue.push(anim);
    }
};
/**
 * @class Ext.fx.Manager
 * Animation Manager which keeps track of all current animations and manages them on a frame by frame basis.
 * @private
 * @singleton
 */

Ext.fx.Manager = {

    constructor: function() {
        var me = this;
        me.items = new Ext.util.MixedCollection();
        me.addEvents(
            /**
             * @event framebegin
             * Fires before the frame starts.
             */
            'framebegin',
             /**
              * @event frameend
              * Fires when the frame ends.
              */
            'frameend'
        );
        Ext.util.Observable.constructor.call(me);
        Ext.fx.Queue.constructor.call(me);
    },

    /**
     * @cfg {Number} interval Default interval in miliseconds to calculate each frame.  Defaults to 16ms (~60fps)
     */
    interval: 32,

    // @private Target factory
    createTarget: function(target) {
        var me = this,
            targetObj;

        if (Ext.isObject(target)) {
            // Draw Sprite
            if (target.isSprite) {
                targetObj = new Ext.fx.target.Sprite(target);
            }
            // Draw Sprite Composite
            else if (target.isCompositeSprite) {
                targetObj = new Ext.fx.target.CompositeSprite(target);
            }
            else if (target.isAnimTarget) {
                return target;
            }
            else {
                return null;
            }
            me.targets.add(targetObj);
            return targetObj;
        }
        else {
            return null;
        }
    },

    /**
     * Add an Anim to the manager. This is done automatically when an Anim instance is created.
     * @param {Ext.fx.Anim} anim
     */
    addAnim: function(anim) {
        var me = this,
            items = me.items,
            task = me.task;

        items.add(anim);

        // Start the timer if not already running
        if (!task && items.length) {
            me.task = setInterval(me.runner, me.interval);
        }
    },

    /**
     * Remove an Anim from the manager. This is done automatically when an Anim ends.
     * @param {Ext.fx.Anim} anim
     */
    removeAnim: function(anim) {
        var me = this,
            items = me.items,
            task = me.task;

        items.remove(anim);

        // Stop the timer if there are no more managed Anims
        if (task && !items.length) {
            clearInterval(task);
            delete me.task;
        }
    },

    /**
     * @private
     * Filter function to determine which animations need to be started
     */
    startingFilter: function(o) {
        return o.paused === false && o.running === false && o.iterations > 0;
    },

    /**
     * @private
     * Filter function to determine which animations are still running
     */
    runningFilter: function(o) {
        return o.paused === false && o.running === true && o.isAnimator !== true;
    },

    /**
     * @private
     * Runner function being called each frame
     */
    runner: function() {
        var me = Ext.fx.Manager,
            items = me.items;
        if (!me.running) {
            me.fireEvent('framebegin');
            me.running = true;
            me.targetData = {};
            me.targetArr = {};

            // Single timestamp for all animations this interval
            me.timestamp = new Date();

            // Start any items not current running
            items.filterBy(me.startingFilter).each(me.startAnim, me);

            // Build the new attributes to be applied for all targets in this frame
            items.filterBy(me.runningFilter).each(me.runAnim, me);

            // Apply all the pending changes to their targets
            me.applyPendingAttrs();
            me.fireEvent('frameend');
            me.running = false;
        }
    },

    /**
     * @private
     * Start the individual animation (initialization)
     */
    startAnim: function(anim) {
        anim.start(this.timestamp);
    },

    /**
     * @private
     * Run the individual animation for this frame
     */
    runAnim: function(anim) {
        if (!anim) {
            return;
        }
        var me = this,
            targetId = anim.target.getId(),
            elapsedTime = me.timestamp - anim.startTime;

        this.collectTargetData(anim, elapsedTime);

        // For JS animation, trigger the lastFrame handler if this is the final frame
        if (elapsedTime >= anim.duration) {
            me.applyPendingAttrs(true);
            delete me.targetData[targetId];
            delete me.targetArr[targetId];
            anim.lastFrame();
        }
    },

    /**
     * Collect target attributes for the given Anim object at the given timestamp
     * @param {Ext.fx.Anim} anim The Anim instance
     * @param {Number} elapsedTime Time after the anim's start time
     */
    collectTargetData: function(anim, elapsedTime) {
        var me = this,
            targetId = anim.target.getId(),
            targetData = me.targetData[targetId],
            data;

        if (!targetData) {
            targetData = me.targetData[targetId] = [];
            me.targetArr[targetId] = anim.target;
        }

        data = {
            duration: anim.duration,
            easing: anim.easing,
            attrs: {}
        };
        Ext.apply(data.attrs, anim.runAnim(elapsedTime));
        targetData.push(data);
    },

    /**
     * @private
     * Apply all pending attribute changes to their targets
     */
    applyPendingAttrs: function(isLastFrame) {
        var targetData = this.targetData,
            targetArr = this.targetArr,
            targetId;
        for (targetId in targetData) {
            if (targetData.hasOwnProperty(targetId)) {
                targetArr[targetId].setAttr(targetData[targetId], false, isLastFrame);
            }
        }
    }
};

Ext.applyIf(Ext.fx.Manager, Ext.fx.Queue);
Ext.applyIf(Ext.fx.Manager, Ext.util.Observable.prototype);
Ext.fx.Manager.constructor();

/**
 * @class Ext.fx.CubicBezier
 * @ignore
 */
Ext.fx.CubicBezier = {

    cubicBezierAtTime: function(t, p1x, p1y, p2x, p2y, duration) {
        var cx = 3 * p1x,
            bx = 3 * (p2x - p1x) - cx,
            ax = 1 - cx - bx,
            cy = 3 * p1y,
            by = 3 * (p2y - p1y) - cy,
            ay = 1 - cy - by;
        function sampleCurveX(t) {
            return ((ax * t + bx) * t + cx) * t;
        }
        function solve(x, epsilon) {
            var t = solveCurveX(x, epsilon);
            return ((ay * t + by) * t + cy) * t;
        }
        function solveCurveX(x, epsilon) {
            var t0, t1, t2, x2, d2, i;
            for (t2 = x, i = 0; i < 8; i++) {
                x2 = sampleCurveX(t2) - x;
                if (Math.abs(x2) < epsilon) {
                    return t2;
                }
                d2 = (3 * ax * t2 + 2 * bx) * t2 + cx;
                if (Math.abs(d2) < 1e-6) {
                    break;
                }
                t2 = t2 - x2 / d2;
            }
            t0 = 0;
            t1 = 1;
            t2 = x;
            if (t2 < t0) {
                return t0;
            }
            if (t2 > t1) {
                return t1;
            }
            while (t0 < t1) {
                x2 = sampleCurveX(t2);
                if (Math.abs(x2 - x) < epsilon) {
                    return t2;
                }
                if (x > x2) {
                    t0 = t2;
                } else {
                    t1 = t2;
                }
                t2 = (t1 - t0) / 2 + t0;
            }
            return t2;
        }
        return solve(t, 1 / (200 * duration));
    },

    cubicBezier: function(x1, y1, x2, y2) {
        var fn = function(pos) {
            return Ext.fx.CubicBezier.cubicBezierAtTime(pos, x1, y1, x2, y2, 1);
        };
        fn.toCSS3 = function() {
            return 'cubic-bezier(' + [x1, y1, x2, y2].join(',') + ')';
        };
        fn.reverse = function() {
            return Ext.fx.CubicBezier.cubicBezier(1 - x2, 1 - y2, 1 - x1, 1 - y1);
        };
        return fn;
    }
};
/**
 * @class Ext.fx.Easing
 *
This class contains a series of function definitions used to modify values during an animation.
They describe how the intermediate values used during a transition will be calculated. It allows for a transition to change
speed over its duration. The following options are available:

- linear The default easing type
- backIn
- backOut
- bounceIn
- bounceOut
- ease
- easeIn
- easeOut
- easeInOut
- elasticIn
- elasticOut
- cubic-bezier(x1, y1, x2, y2)

Note that cubic-bezier will create a custom easing curve following the CSS3 transition-timing-function specification `{@link http://www.w3.org/TR/css3-transitions/#transition-timing-function_tag}`. The four values specify points P1 and P2 of the curve
as (x1, y1, x2, y2). All values must be in the range [0, 1] or the definition is invalid.
 * @markdown
 * @singleton
 */

(Ext.fx.Easing = function() {
    var math = Math,
        pi = math.PI,
        pow = math.pow,
        sin = math.sin,
        sqrt = math.sqrt,
        abs = math.abs,
        backInSeed = 1.70158;

    Ext.apply(Ext.fx.Easing, {
        linear: function(n) {
            return n;
        },
        ease: function(n) {
            var q = 0.07813 - n / 2,
                Q = sqrt(0.0066 + q * q),
                x = Q - q,
                X = pow(abs(x), 1/3) * (x < 0 ? -1 : 1),
                y = -Q - q,
                Y = pow(abs(y), 1/3) * (y < 0 ? -1 : 1),
                t = X + Y + 0.25;
            return pow(1 - t, 2) * 3 * t * 0.1 + (1 - t) * 3 * t * t + t * t * t;
        },
        easeIn: function (n) {
            return pow(n, 1.7);
        },
        easeOut: function (n) {
            return pow(n, 0.48);
        },
        easeInOut: function(n) {
            var q = 0.48 - n / 1.04,
                Q = sqrt(0.1734 + q * q),
                x = Q - q,
                X = pow(abs(x), 1/3) * (x < 0 ? -1 : 1),
                y = -Q - q,
                Y = pow(abs(y), 1/3) * (y < 0 ? -1 : 1),
                t = X + Y + 0.5;
            return (1 - t) * 3 * t * t + t * t * t;
        },
        backIn: function (n) {
            return n * n * ((backInSeed + 1) * n - backInSeed);
        },
        backOut: function (n) {
            n = n - 1;
            return n * n * ((backInSeed + 1) * n + backInSeed) + 1;
        },
        elasticIn: function (n) {
            if (n === 0 || n === 1) {
                return n;
            }
            var p = 0.3,
                s = p / 4;
            return pow(2, -10 * n) * sin((n - s) * (2 * pi) / p) + 1;
        },
        elasticOut: function (n) {
            return 1 - Ext.fx.Easing.elasticIn(1 - n);
        },
        bounceIn: function (n) {
            return 1 - Ext.fx.Easing.bounceOut(1 - n);
        },
        bounceOut: function (n) {
            var s = 7.5625,
                p = 2.75,
                l;
            if (n < (1 / p)) {
                l = s * n * n;
            } else {
                if (n < (2 / p)) {
                    n -= (1.5 / p);
                    l = s * n * n + 0.75;
                } else {
                    if (n < (2.5 / p)) {
                        n -= (2.25 / p);
                        l = s * n * n + 0.9375;
                    } else {
                        n -= (2.625 / p);
                        l = s * n * n + 0.984375;
                    }
                }
            }
            return l;
        }
    });
    Ext.apply(Ext.fx.Easing, {
        'back-in': Ext.fx.Easing.backIn.prototype,
        'back-out': Ext.fx.Easing.backOut.prototype,
        'ease-in': Ext.fx.Easing.easeIn.prototype,
        'ease-out': Ext.fx.Easing.easeOut.prototype,
        'elastic-in': Ext.fx.Easing.elasticIn.prototype,
        'elastic-out': Ext.fx.Easing.elasticIn.prototype,
        'bounce-in': Ext.fx.Easing.bounceIn.prototype,
        'bounce-out': Ext.fx.Easing.bounceOut.prototype,
        'ease-in-out': Ext.fx.Easing.easeInOut.prototype
    });
})();
/**
 * @class Ext.fx.PropertyHandler
 * @ignore
 */
Ext.fx.PropertyHandler = {

    defaultHandler: {
        pixelDefaults: ['width', 'height', 'top', 'left'],
        unitRE: /^(-?\d*\.?\d*){1}(em|ex|px|in|cm|mm|pt|pc|%)*$/,

        computeDelta: function(from, end, damper, initial, attr) {
            damper = (typeof damper == 'number') ? damper : 1;
            var match = this.unitRE.exec(from),
                start, units;
            if (match) {
                from = match[1];
                units = match[2];
                // COMPAT Ext.Array.contains
                if (!units && this.pixelDefaults.indexOf(attr) !== -1) {
                    units = 'px';
                }
            }
            from = +from || 0;

            match = this.unitRE.exec(end);
            if (match) {
                end = match[1];
                units = match[2] || units;
            }
            end = +end || 0;
            start = (initial != null) ? initial : from;
            return {
                from: from,
                delta: (end - start) * damper,
                units: units
            };
        },

        get: function(from, end, damper, initialFrom, attr) {
            var ln = from.length,
                out = [],
                i, initial, res, j, len;
            for (i = 0; i < ln; i++) {
                if (initialFrom) {
                    initial = initialFrom[i][1].from;
                }
                if (Ext.isArray(from[i][1]) && Ext.isArray(end)) {
                    res = [];
                    j = 0;
                    len = from[i][1].length;
                    for (; j < len; j++) {
                        res.push(this.computeDelta(from[i][1][j], end[j], damper, initial, attr));
                    }
                    out.push([from[i][0], res]);
                }
                else {
                    out.push([from[i][0], this.computeDelta(from[i][1], end, damper, initial, attr)]);
                }
            }
            return out;
        },

        set: function(values, easing) {
            var ln = values.length,
                out = [],
                i, val, res, len, j;
            for (i = 0; i < ln; i++) {
                val  = values[i][1];
                if (Ext.isArray(val)) {
                    res = [];
                    j = 0;
                    len = val.length;
                    for (; j < len; j++) {
                        res.push(val[j].from + (val[j].delta * easing) + (val[j].units || 0));
                    }
                    out.push([values[i][0], res]);
                } else {
                    out.push([values[i][0], val.from + (val.delta * easing) + (val.units || 0)]);
                }
            }
            return out;
        }
    },
    color: {
        rgbRE: /^rgb\(([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\)$/i,
        hexRE: /^#?([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$/i,
        hex3RE: /^#?([0-9A-F]{1})([0-9A-F]{1})([0-9A-F]{1})$/i,

        parseColor : function(color, damper) {
            damper = (typeof damper == 'number') ? damper : 1;
            var base,
                out = false,
                match;

            Ext.each([this.hexRE, this.rgbRE, this.hex3RE], function(re, idx) {
                base = (idx % 2 == 0) ? 16 : 10;
                match = re.exec(color);
                if (match && match.length == 4) {
                    if (idx == 2) {
                        match[1] += match[1];
                        match[2] += match[2];
                        match[3] += match[3];
                    }
                    out = {
                        red: parseInt(match[1], base),
                        green: parseInt(match[2], base),
                        blue: parseInt(match[3], base)
                    };
                    return false;
                }
            });
            return out || color;
        },

        computeDelta: function(from, end, damper, initial) {
            from = this.parseColor(from);
            end = this.parseColor(end, damper);
            var start = initial ? initial : from,
                tfrom = typeof start,
                tend = typeof end;
            //Extra check for when the color string is not recognized.
            if (tfrom == 'string' ||  tfrom == 'undefined'
              || tend == 'string' || tend == 'undefined') {
                return end || start;
            }
            return {
                from:  from,
                delta: {
                    red: Math.round((end.red - start.red) * damper),
                    green: Math.round((end.green - start.green) * damper),
                    blue: Math.round((end.blue - start.blue) * damper)
                }
            };
        },

        get: function(start, end, damper, initialFrom) {
            var ln = start.length,
                out = [],
                i, initial;
            for (i = 0; i < ln; i++) {
                if (initialFrom) {
                    initial = initialFrom[i][1].from;
                }
                out.push([start[i][0], this.computeDelta(start[i][1], end, damper, initial)]);
            }
            return out;
        },

        set: function(values, easing) {
            var ln = values.length,
                out = [],
                i, val, parsedString, from, delta;
            for (i = 0; i < ln; i++) {
                val = values[i][1];
                if (val) {
                    from = val.from;
                    delta = val.delta;
                    //multiple checks to reformat the color if it can't recognized by computeDelta.
                    val = (typeof val == 'object' && 'red' in val)?
                            'rgb(' + val.red + ', ' + val.green + ', ' + val.blue + ')' : val;
                    val = (typeof val == 'object' && val.length)? val[0] : val;
                    if (typeof val == 'undefined') {
                        return [];
                    }
                    parsedString = typeof val == 'string'? val :
                        'rgb(' + [
                              (from.red + Math.round(delta.red * easing)) % 256,
                              (from.green + Math.round(delta.green * easing)) % 256,
                              (from.blue + Math.round(delta.blue * easing)) % 256
                          ].join(',') + ')';
                    out.push([
                        values[i][0],
                        parsedString
                    ]);
                }
            }
            return out;
        }
    },
    object: {
        interpolate: function(prop, damper) {
            damper = (typeof damper == 'number') ? damper : 1;
            var out = {},
                p;
            for(p in prop) {
                out[p] = parseFloat(prop[p], 10) * damper;
            }
            return out;
        },

        computeDelta: function(from, end, damper, initial) {
            from = this.interpolate(from);
            end = this.interpolate(end, damper);
            var start = initial ? initial : from,
                delta = {},
                p;

            for(p in end) {
                delta[p] = end[p] - start[p];
            }
            return {
                from:  from,
                delta: delta
            };
        },

        get: function(start, end, damper, initialFrom) {
            var ln = start.length,
                out = [],
                i, initial;
            for (i = 0; i < ln; i++) {
                if (initialFrom) {
                    initial = initialFrom[i][1].from;
                }
                out.push([start[i][0], this.computeDelta(start[i][1], end, damper, initial)]);
            }
            return out;
        },

        set: function(values, easing) {
            var ln = values.length,
                out = [],
                outObject = {},
                i, from, delta, val, p;
            for (i = 0; i < ln; i++) {
                val  = values[i][1];
                from = val.from;
                delta = val.delta;
                for (p in from) {
                    outObject[p] = Math.round(from[p] + delta[p] * easing);
                }
                out.push([
                    values[i][0],
                    outObject
                ]);
            }
            return out;
        }
    },

    path: {
        computeDelta: function(from, end, damper, initial) {
            damper = (typeof damper == 'number') ? damper : 1;
            var start;
            from = +from || 0;
            end = +end || 0;
            start = (initial != null) ? initial : from;
            return {
                from: from,
                delta: (end - start) * damper
            };
        },

        forcePath: function(path) {
            if (!Ext.isArray(path) && !Ext.isArray(path[0])) {
                path = Ext.draw.Draw.parsePathString(path);
            }
            return path;
        },

        get: function(start, end, damper, initialFrom) {
            var endPath = this.forcePath(end),
                out = [],
                startLn = start.length,
                startPathLn, pointsLn, i, deltaPath, initial, j, k, path, startPath;
            for (i = 0; i < startLn; i++) {
                startPath = this.forcePath(start[i][1]);

                deltaPath = Ext.draw.Draw.interpolatePaths(startPath, endPath);
                startPath = deltaPath[0];
                endPath = deltaPath[1];

                startPathLn = startPath.length;
                path = [];
                for (j = 0; j < startPathLn; j++) {
                    deltaPath = [startPath[j][0]];
                    pointsLn = startPath[j].length;
                    for (k = 1; k < pointsLn; k++) {
                        initial = initialFrom && initialFrom[0][1][j][k].from;
                        deltaPath.push(this.computeDelta(startPath[j][k], endPath[j][k], damper, initial));
                    }
                    path.push(deltaPath);
                }
                out.push([start[i][0], path]);
            }
            return out;
        },

        set: function(values, easing) {
            var ln = values.length,
                out = [],
                i, j, k, newPath, calcPath, deltaPath, deltaPathLn, pointsLn;
            for (i = 0; i < ln; i++) {
                deltaPath = values[i][1];
                newPath = [];
                deltaPathLn = deltaPath.length;
                for (j = 0; j < deltaPathLn; j++) {
                    calcPath = [deltaPath[j][0]];
                    pointsLn = deltaPath[j].length;
                    for (k = 1; k < pointsLn; k++) {
                        calcPath.push(deltaPath[j][k].from + deltaPath[j][k].delta * easing);
                    }
                    newPath.push(calcPath.join(','));
                }
                out.push([values[i][0], newPath.join(',')]);
            }
            return out;
        }
    }
};

Ext.each([
    'outlineColor',
    'backgroundColor',
    'borderColor',
    'borderTopColor',
    'borderRightColor',
    'borderBottomColor',
    'borderLeftColor',
    'fill',
    'stroke'
], function(prop) {
    Ext.fx.PropertyHandler[prop] = Ext.fx.PropertyHandler.color;
}, Ext.fx.PropertyHandler);
/**
 * @class Ext.fx.Animator
 * Animation instance

This class is used to run keyframe based animations, which follows the CSS3 based animation structure.
Keyframe animations differ from typical from/to animations in that they offer the ability to specify values
at various points throughout the animation.

__Using Keyframes__
The {@link #keyframes} option is the most important part of specifying an animation when using this
class. A key frame is a point in a particular animation. We represent this as a percentage of the
total animation duration. At each key frame, we can specify the target values at that time. Note that
you *must* specify the values at 0% and 100%, the start and ending values. There is also a {@link keyframe}
event that fires after each key frame is reached.

__Example Usage__
In the example below, we modify the values of the element at each fifth throughout the animation.

    Ext.create('Ext.fx.Animator', {
        target: Ext.getBody().createChild({
            style: {
                width: '100px',
                height: '100px',
                'background-color': 'red'
            }
        }),
        duration: 10000, // 10 seconds
        keyframes: {
            0: {
                opacity: 1,
                backgroundColor: 'FF0000'
            },
            20: {
                x: 30,
                opacity: 0.5
            },
            40: {
                x: 130,
                backgroundColor: '0000FF'
            },
            60: {
                y: 80,
                opacity: 0.3
            },
            80: {
                width: 200,
                y: 200
            },
            100: {
                opacity: 1,
                backgroundColor: '00FF00'
            }
        }
    });

 * @markdown
 */
Ext.fx.Animator = Ext.extend(Ext.util.Observable, {

    isAnimator: true,

    /**
     * @cfg {Number} duration
     * Time in milliseconds for the animation to last. Defaults to 250.
     */
    duration: 250,

    /**
     * @cfg {Number} delay
     * Time to delay before starting the animation. Defaults to 0.
     */
    delay: 0,

    /* private used to track a delayed starting time */
    delayStart: 0,

    /**
     * @cfg {Boolean} dynamic
     * Currently only for Component Animation: Only set a component's outer element size bypassing layouts.  Set to true to do full layouts for every frame of the animation.  Defaults to false.
     */
    dynamic: false,

    /**
     * @cfg {String} easing

This describes how the intermediate values used during a transition will be calculated. It allows for a transition to change
speed over its duration.

- backIn
- backOut
- bounceIn
- bounceOut
- ease
- easeIn
- easeOut
- easeInOut
- elasticIn
- elasticOut
- cubic-bezier(x1, y1, x2, y2)

Note that cubic-bezier will create a custom easing curve following the CSS3 transition-timing-function specification `{@link http://www.w3.org/TR/css3-transitions/#transition-timing-function_tag}`. The four values specify points P1 and P2 of the curve
as (x1, y1, x2, y2). All values must be in the range [0, 1] or the definition is invalid.

     * @markdown
     */
    easing: 'ease',

    /**
     * Flag to determine if the animation has started
     * @property running
     * @type boolean
     */
    running: false,

    /**
     * Flag to determine if the animation is paused. Only set this to true if you need to
     * keep the Anim instance around to be unpaused later; otherwise call {@link #end}.
     * @property paused
     * @type boolean
     */
    paused: false,

    /**
     * @private
     */
    damper: 1,

    /**
     * @cfg {Number} iterations
     * Number of times to execute the animation. Defaults to 1.
     */
    iterations: 1,

    /**
     * Current iteration the animation is running.
     * @property currentIteration
     * @type int
     */
    currentIteration: 0,

    /**
     * Current keyframe step of the animation.
     * @property keyframeStep
     * @type Number
     */
    keyframeStep: 0,

    /**
     * @private
     */
    animKeyFramesRE: /^(from|to|\d+%?)$/,

    /**
     * @cfg {Ext.fx.target} target
     * The Ext.fx.target to apply the animation to.  If not specified during initialization, this can be passed to the applyAnimator
     * method to apply the same animation to many targets.
     */

     /**
      * @cfg {Object} keyframes
      * Animation keyframes follow the CSS3 Animation configuration pattern. 'from' is always considered '0%' and 'to'
      * is considered '100%'.<b>Every keyframe declaration must have a keyframe rule for 0% and 100%, possibly defined using
      * "from" or "to"</b>.  A keyframe declaration without these keyframe selectors is invalid and will not be available for
      * animation.  The keyframe declaration for a keyframe rule consists of properties and values. Properties that are unable to
      * be animated are ignored in these rules, with the exception of 'easing' which can be changed at each keyframe. For example:
 <pre><code>
keyframes : {
    '0%': {
        left: 100
    },
    '40%': {
        left: 150
    },
    '60%': {
        left: 75
    },
    '100%': {
        left: 100
    }
}
 </code></pre>
      */
    constructor: function(config) {
        var me = this;
        config = Ext.apply(me, config || {});
        me.config = config;
        me.id = Ext.id(null, 'ext-animator-');
        me.addEvents(
            /**
             * @event beforeanimate
             * Fires before the animation starts. A handler can return false to cancel the animation.
             * @param {Ext.fx.Animator} this
             */
            'beforeanimate',
            /**
              * @event keyframe
              * Fires at each keyframe.
              * @param {Ext.fx.Animator} this
              * @param {Number} keyframe step number
              */
            'keyframe',
            /**
             * @event afteranimate
             * Fires when the animation is complete.
             * @param {Ext.fx.Animator} this
             * @param {Date} startTime
             */
            'afteranimate'
        );
        Ext.fx.Animator.superclass.constructor.call(me, config);
        me.timeline = [];
        me.createTimeline(me.keyframes);
        if (me.target) {
            me.applyAnimator(me.target);
            Ext.fx.Manager.addAnim(me);
        }
    },

    /**
     * @private
     */
    sorter: function (a, b) {
        return a.pct - b.pct;
    },

    /**
     * @private
     * Takes the given keyframe configuration object and converts it into an ordered array with the passed attributes per keyframe
     * or applying the 'to' configuration to all keyframes.  Also calculates the proper animation duration per keyframe.
     */
    createTimeline: function(keyframes) {
        var me = this,
            attrs = [],
            to = me.to || {},
            duration = me.duration,
            prevMs, ms, i, ln, pct, attr;

        for (pct in keyframes) {
            if (keyframes.hasOwnProperty(pct) && me.animKeyFramesRE.test(pct)) {
                attr = {attrs: Ext.apply(keyframes[pct], to)};
                // CSS3 spec allow for from/to to be specified.
                if (pct == "from") {
                    pct = 0;
                }
                else if (pct == "to") {
                    pct = 100;
                }
                // convert % values into integers
                attr.pct = parseInt(pct, 10);
                attrs.push(attr);
            }
        }
        // Sort by pct property
        // COMPAT Array
        attrs.sort(me.sorter);
        // Only an end
        //if (attrs[0].pct) {
        //    attrs.unshift({pct: 0, attrs: element.attrs});
        //}

        ln = attrs.length;
        for (i = 0; i < ln; i++) {
            prevMs = (attrs[i - 1]) ? duration * (attrs[i - 1].pct / 100) : 0;
            ms = duration * (attrs[i].pct / 100);
            me.timeline.push({
                duration: ms - prevMs,
                attrs: attrs[i].attrs
            });
        }
    },

    /**
     * Applies animation to the Ext.fx.target
     * @private
     * @param target
     * @type string/object
     */
    applyAnimator: function(target) {
        var me = this,
            anims = [],
            timeline = me.timeline,
            ln = timeline.length,
            anim, easing, damper, attrs, i;

        if (me.fireEvent('beforeanimate', me) !== false) {
            for (i = 0; i < ln; i++) {
                anim = timeline[i];
                attrs = anim.attrs;
                easing = attrs.easing || me.easing;
                damper = attrs.damper || me.damper;
                delete attrs.easing;
                delete attrs.damper;
                anim = new Ext.fx.Anim({
                    target: target,
                    easing: easing,
                    damper: damper,
                    duration: anim.duration,
                    paused: true,
                    to: attrs
                });
                anims.push(anim);
            }
            me.animations = anims;
            me.target = anim.target;
            for (i = 0; i < ln - 1; i++) {
                anim = anims[i];
                anim.nextAnim = anims[i + 1];
                anim.on('afteranimate', function() {
                    this.nextAnim.paused = false;
                });
                anim.on('afteranimate', function() {
                    this.fireEvent('keyframe', this, ++this.keyframeStep);
                }, me);
            }
            anims[ln - 1].on('afteranimate', function() {
                this.lastFrame();
            }, me);
        }
    },

    /*
     * @private
     * Fires beforeanimate and sets the running flag.
     */
    start: function(startTime) {
        var me = this,
            delay = me.delay,
            delayStart = me.delayStart,
            delayDelta;
        if (delay) {
            if (!delayStart) {
                me.delayStart = startTime;
                return;
            }
            else {
                delayDelta = startTime - delayStart;
                if (delayDelta < delay) {
                    return;
                }
                else {
                    // Compensate for frame delay;
                    startTime = new Date(delayStart.getTime() + delay);
                }
            }
        }
        if (me.fireEvent('beforeanimate', me) !== false) {
            me.startTime = startTime;
            me.running = true;
            me.animations[me.keyframeStep].paused = false;
        }
    },

    /*
     * @private
     * Perform lastFrame cleanup and handle iterations
     * @returns a hash of the new attributes.
     */
    lastFrame: function() {
        var me = this,
            iter = me.iterations,
            iterCount = me.currentIteration;

        iterCount++;
        if (iterCount < iter) {
            me.startTime = new Date();
            me.currentIteration = iterCount;
            me.keyframeStep = 0;
            me.applyAnimator(me.target);
            me.animations[me.keyframeStep].paused = false;
        }
        else {
            me.currentIteration = 0;
            me.end();
        }
    },

    /*
     * Fire afteranimate event and end the animation. Usually called automatically when the
     * animation reaches its final frame, but can also be called manually to pre-emptively
     * stop and destroy the running animation.
     */
    end: function() {
        var me = this;
        me.fireEvent('afteranimate', me, me.startTime, new Date() - me.startTime);
    }
});
/**
 * @class Ext.fx.Anim
 *
 * This class manages animation for a specific {@link #target}. The animation allows
 * animation of various properties on the target, such as size, position, color and others.
 *
 * ## Starting Conditions
 * The starting conditions for the animation are provided by the {@link #from} configuration.
 * Any/all of the properties in the {@link #from} configuration can be specified. If a particular
 * property is not defined, the starting value for that property will be read directly from the target.
 *
 * ## End Conditions
 * The ending conditions for the animation are provided by the {@link #to} configuration. These mark
 * the final values once the animations has finished. The values in the {@link #from} can mirror
 * those in the {@link #to} configuration to provide a starting point.
 *
 * ## Other Options
 *  - {@link #duration}: Specifies the time period of the animation.
 *  - {@link #easing}: Specifies the easing of the animation.
 *  - {@link #iterations}: Allows the animation to repeat a number of times.
 *  - {@link #alternate}: Used in conjunction with {@link #iterations}, reverses the direction every second iteration.
 *
 * ## Example Code
 *
 *     var myComponent = Ext.create('Ext.Component', {
 *         renderTo: document.body,
 *         width: 200,
 *         height: 200,
 *         style: 'border: 1px solid red;'
 *     });
 *
 *     new Ext.fx.Anim({
 *         target: myComponent,
 *         duration: 1000,
 *         from: {
 *             width: 400 //starting width 400
 *         },
 *         to: {
 *             width: 300, //end width 300
 *             height: 300 // end width 300
 *         }
 *     });
 */
Ext.fx.Anim = Ext.extend(Ext.util.Observable, {

    isAnimation: true,
    /**
     * @cfg {Number} duration
     * Time in milliseconds for a single animation to last. Defaults to 250. If the {@link #iterations} property is
     * specified, then each animate will take the same duration for each iteration.
     */
    duration: 250,

    /**
     * @cfg {Number} delay
     * Time to delay before starting the animation. Defaults to 0.
     */
    delay: 0,

    /* private used to track a delayed starting time */
    delayStart: 0,

    /**
     * @cfg {Boolean} dynamic
     * Currently only for Component Animation: Only set a component's outer element size bypassing layouts.  Set to true to do full layouts for every frame of the animation.  Defaults to false.
     */
    dynamic: false,

    /**
     * @cfg {String} easing
This describes how the intermediate values used during a transition will be calculated. It allows for a transition to change
speed over its duration.

         -backIn
         -backOut
         -bounceIn
         -bounceOut
         -ease
         -easeIn
         -easeOut
         -easeInOut
         -elasticIn
         -elasticOut
         -cubic-bezier(x1, y1, x2, y2)

Note that cubic-bezier will create a custom easing curve following the CSS3 transition-timing-function specification `{@link http://www.w3.org/TR/css3-transitions/#transition-timing-function_tag}`. The four values specify points P1 and P2 of the curve
as (x1, y1, x2, y2). All values must be in the range [0, 1] or the definition is invalid.
     * @markdown
     */
    easing: 'ease',

     /**
      * @cfg {Object} keyframes
      * Animation keyframes follow the CSS3 Animation configuration pattern. 'from' is always considered '0%' and 'to'
      * is considered '100%'.<b>Every keyframe declaration must have a keyframe rule for 0% and 100%, possibly defined using
      * "from" or "to"</b>.  A keyframe declaration without these keyframe selectors is invalid and will not be available for
      * animation.  The keyframe declaration for a keyframe rule consists of properties and values. Properties that are unable to
      * be animated are ignored in these rules, with the exception of 'easing' which can be changed at each keyframe. For example:
 <pre><code>
keyframes : {
    '0%': {
        left: 100
    },
    '40%': {
        left: 150
    },
    '60%': {
        left: 75
    },
    '100%': {
        left: 100
    }
}
 </code></pre>
      */

    /**
     * @private
     */
    damper: 1,

    /**
     * @private
     */
    bezierRE: /^(?:cubic-)?bezier\(([^,]+),([^,]+),([^,]+),([^\)]+)\)/,

    /**
     * Run the animation from the end to the beginning
     * Defaults to false.
     * @cfg {Boolean} reverse
     */
    reverse: false,

    /**
     * Flag to determine if the animation has started
     * @property running
     * @type boolean
     */
    running: false,

    /**
     * Flag to determine if the animation is paused. Only set this to true if you need to
     * keep the Anim instance around to be unpaused later; otherwise call {@link #end}.
     * @property paused
     * @type boolean
     */
    paused: false,

    /**
     * Number of times to execute the animation. Defaults to 1.
     * @cfg {int} iterations
     */
    iterations: 1,

    /**
     * Used in conjunction with iterations to reverse the animation each time an iteration completes.
     * @cfg {Boolean} alternate
     * Defaults to false.
     */
    alternate: false,

    /**
     * Current iteration the animation is running.
     * @property currentIteration
     * @type int
     */
    currentIteration: 0,

    /**
     * Starting time of the animation.
     * @property startTime
     * @type Date
     */
    startTime: 0,

    /**
     * Contains a cache of the interpolators to be used.
     * @private
     * @property propHandlers
     * @type Object
     */

    /**
     * @cfg {String/Object} target
     * The {@link Ext.fx.target.Target} to apply the animation to.  This should only be specified when creating an Ext.fx.Anim directly.
     * The target does not need to be a {@link Ext.fx.target.Target} instance, it can be the underlying object. For example, you can
     * pass a Component, Element or Sprite as the target and the Anim will create the appropriate {@link Ext.fx.target.Target} object
     * automatically.
     */

    /**
     * @cfg {Object} from
     * An object containing property/value pairs for the beginning of the animation.  If not specified, the current state of the
     * Ext.fx.target will be used. For example:
<pre><code>
from : {
    opacity: 0,       // Transparent
    color: '#ffffff', // White
    left: 0
}
</code></pre>
     */

    /**
     * @cfg {Object} to
     * An object containing property/value pairs for the end of the animation. For example:
 <pre><code>
 to : {
     opacity: 1,       // Opaque
     color: '#00ff00', // Green
     left: 500
 }
 </code></pre>
     */

    // @private
    constructor: function(config) {
        var me = this;
        config = config || {};
        // If keyframes are passed, they really want an Animator instead.
        if (config.keyframes) {
            return new Ext.fx.Animator(config);
        }
        config = Ext.apply(me, config);
        if (me.from === undefined) {
            me.from = {};
        }
        me.propHandlers = {};
        me.config = config;
        me.target = Ext.fx.Manager.createTarget(me.target);
        me.easingFn = Ext.fx.Easing[me.easing];
        me.target.dynamic = me.dynamic;

        // If not a pre-defined curve, try a cubic-bezier
        if (!me.easingFn) {
            me.easingFn = String(me.easing).match(me.bezierRE);
            if (me.easingFn && me.easingFn.length == 5) {
                var curve = me.easingFn;
                me.easingFn = Ext.fx.cubicBezier(+curve[1], +curve[2], +curve[3], +curve[4]);
            }
        }
        me.id = Ext.id(null, 'ext-anim-');
        Ext.fx.Manager.addAnim(me);
        me.addEvents(
            /**
             * @event beforeanimate
             * Fires before the animation starts. A handler can return false to cancel the animation.
             * @param {Ext.fx.Anim} this
             */
            'beforeanimate',
             /**
              * @event afteranimate
              * Fires when the animation is complete.
              * @param {Ext.fx.Anim} this
              * @param {Date} startTime
              */
            'afteranimate',
             /**
              * @event lastframe
              * Fires when the animation's last frame has been set.
              * @param {Ext.fx.Anim} this
              * @param {Date} startTime
              */
            'lastframe'
        );
        Ext.fx.Anim.superclass.constructor.call(me, config);
        if (config.callback) {
            me.on('afteranimate', config.callback, config.scope);
        }
        return me;
    },

    /**
     * @private
     * Helper to the target
     */
    setAttr: function(attr, value) {
        return Ext.fx.Manager.items.get(this.id).setAttr(this.target, attr, value);
    },

    /*
     * @private
     * Set up the initial currentAttrs hash.
     */
    initAttrs: function() {
        var me = this,
            from = me.from,
            to = me.to,
            initialFrom = me.initialFrom || {},
            out = {},
            start, end, propHandler, attr;

        for (attr in to) {
            if (to.hasOwnProperty(attr)) {
                start = me.target.getAttr(attr, from[attr]);
                end = to[attr];
                // Use default (numeric) property handler
                if (!Ext.fx.PropertyHandler[attr]) {
                    if (Ext.isObject(end)) {
                        propHandler = me.propHandlers[attr] = Ext.fx.PropertyHandler.object;
                    } else {
                        propHandler = me.propHandlers[attr] = Ext.fx.PropertyHandler.defaultHandler;
                    }
                }
                // Use custom handler
                else {
                    propHandler = me.propHandlers[attr] = Ext.fx.PropertyHandler[attr];
                }
                out[attr] = propHandler.get(start, end, me.damper, initialFrom[attr], attr);
            }
        }
        me.currentAttrs = out;
    },

    /*
     * @private
     * Fires beforeanimate and sets the running flag.
     */
    start: function(startTime) {
        var me = this,
            delay = me.delay,
            delayStart = me.delayStart,
            delayDelta;
        if (delay) {
            if (!delayStart) {
                me.delayStart = startTime;
                return;
            }
            else {
                delayDelta = startTime - delayStart;
                if (delayDelta < delay) {
                    return;
                }
                else {
                    // Compensate for frame delay;
                    startTime = new Date(delayStart.getTime() + delay);
                }
            }
        }
        if (me.fireEvent('beforeanimate', me) !== false) {
            me.startTime = startTime;
            if (!me.paused && !me.currentAttrs) {
                me.initAttrs();
            }
            me.running = true;
        }
    },

    /*
     * @private
     * Calculate attribute value at the passed timestamp.
     * @returns a hash of the new attributes.
     */
    runAnim: function(elapsedTime) {
        var me = this,
            attrs = me.currentAttrs,
            duration = me.duration,
            easingFn = me.easingFn,
            propHandlers = me.propHandlers,
            ret = {},
            easing, values, attr, lastFrame;

        if (elapsedTime >= duration) {
            elapsedTime = duration;
            lastFrame = true;
        }
        if (me.reverse) {
            elapsedTime = duration - elapsedTime;
        }

        for (attr in attrs) {
            if (attrs.hasOwnProperty(attr)) {
                values = attrs[attr];
                easing = lastFrame ? 1 : easingFn(elapsedTime / duration);
                ret[attr] = propHandlers[attr].set(values, easing);
            }
        }
        return ret;
    },

    /*
     * @private
     * Perform lastFrame cleanup and handle iterations
     * @returns a hash of the new attributes.
     */
    lastFrame: function() {
        var me = this,
            iter = me.iterations,
            iterCount = me.currentIteration;

        iterCount++;
        if (iterCount < iter) {
            if (me.alternate) {
                me.reverse = !me.reverse;
            }
            me.startTime = new Date();
            me.currentIteration = iterCount;
            // Turn off paused for CSS3 Transitions
            me.paused = false;
        }
        else {
            me.currentIteration = 0;
            me.end();
            me.fireEvent('lastframe', me, me.startTime);
        }
    },

    /*
     * Fire afteranimate event and end the animation. Usually called automatically when the
     * animation reaches its final frame, but can also be called manually to pre-emptively
     * stop and destroy the running animation.
     */
    end: function() {
        var me = this;
        me.startTime = 0;
        me.paused = false;
        me.running = false;
        Ext.fx.Manager.removeAnim(me);
        me.fireEvent('afteranimate', me, me.startTime);
    }
});
// Set flag to indicate that Fx is available. Class might not be available immediately.
Ext.enableFx = true;

/**
 * @class Ext.fx.target.Target

This class specifies a generic target for an animation. It provides a wrapper around a
series of different types of objects to allow for a generic animation API.
A target can be a single object or a Composite object containing other objects that are
to be animated. This class and it's subclasses are generally not created directly, the
underlying animation will create the appropriate Ext.fx.target.Target object by passing
the instance to be animated.

The following types of objects can be animated:
- {@link #Ext.fx.target.Component Components}
- {@link #Ext.fx.target.Element Elements}
- {@link #Ext.fx.target.Sprite Sprites}

 * @markdown
 * @abstract
 * @constructor
 * @param {Mixed} target The object to be animated
 */

Ext.fx.target.Target = Ext.extend(Object, {

    isAnimTarget: true,

    constructor: function(target) {
        this.target = target;
        this.id = this.getId();
    },

    getId: function() {
        return this.target.id;
    }
});

/**
 * @class Ext.fx.target.Sprite
 * @extends Ext.fx.target.Target

This class represents a animation target for a {@link Ext.draw.Sprite}. In general this class will not be
created directly, the {@link Ext.draw.Sprite} will be passed to the animation and
and the appropriate target will be created.

 * @markdown
 */

Ext.fx.target.Sprite = Ext.extend(Ext.fx.target.Target, {

    type: 'draw',

    getFromPrim: function(sprite, attr) {
        var o;
        if (attr == 'translate') {
            o = {
                x: sprite.attr.translation.x || 0,
                y: sprite.attr.translation.y || 0
            };
        }
        else if (attr == 'rotate') {
            o = {
                degrees: sprite.attr.rotation.degrees || 0,
                x: sprite.attr.rotation.x,
                y: sprite.attr.rotation.y
            };
        }
        else {
            o = sprite.attr[attr];
        }
        return o;
    },

    getAttr: function(attr, val) {
        return [[this.target, val != undefined ? val : this.getFromPrim(this.target, attr)]];
    },

    setAttr: function(targetData) {
        var ln = targetData.length,
            spriteArr = [],
            attrs, attr, attrArr, attPtr, spritePtr, idx, value, i, j, x, y, ln2;
        for (i = 0; i < ln; i++) {
            attrs = targetData[i].attrs;
            for (attr in attrs) {
                attrArr = attrs[attr];
                ln2 = attrArr.length;
                for (j = 0; j < ln2; j++) {
                    spritePtr = attrArr[j][0];
                    attPtr = attrArr[j][1];
                    if (attr === 'translate') {
                        value = {
                            x: attPtr.x,
                            y: attPtr.y
                        };
                    }
                    else if (attr === 'rotate') {
                        x = attPtr.x;
                        if (isNaN(x)) {
                            x = null;
                        }
                        y = attPtr.y;
                        if (isNaN(y)) {
                            y = null;
                        }
                        value = {
                            degrees: attPtr.degrees,
                            x: x,
                            y: y
                        };
                    }
                    else if (attr === 'width' || attr === 'height' || attr === 'x' || attr === 'y') {
                        value = parseFloat(attPtr);
                    }
                    else {
                        value = attPtr;
                    }
                    // COMPAT indexOf
                    idx = spriteArr.indexOf(spritePtr);
                    if (idx == -1) {
                        spriteArr.push([spritePtr, {}]);
                        idx = spriteArr.length - 1;
                    }
                    spriteArr[idx][1][attr] = value;
                }
            }
        }
        ln = spriteArr.length;
        for (i = 0; i < ln; i++) {
            spritePtr = spriteArr[i];
            spritePtr[0].setAttributes(spritePtr[1]);
        }
        this.target.tween();
    }
});

/**
 * @class Ext.fx.target.CompositeSprite
 * @extends Ext.fx.target.Sprite

This class represents a animation target for a {@link Ext.draw.CompositeSprite}. It allows
each {@link Ext.draw.Sprite} in the group to be animated as a whole. In general this class will not be
created directly, the {@link Ext.draw.CompositeSprite} will be passed to the animation and
and the appropriate target will be created.

 * @markdown
 */

Ext.fx.target.CompositeSprite = Ext.extend(Ext.fx.target.Sprite, {

    getAttr: function(attr, val) {
        var out = [],
            target = this.target;
        target.each(function(sprite) {
            out.push([sprite, val != undefined ? val : this.getFromPrim(sprite, attr)]);
        }, this);
        return out;
    }
});

/**
 * @class Ext.util.Animate
 * This animation class is a mixin.
 *
 * Ext.util.Animate provides an API for the creation of animated transitions of properties and styles.
 * This class is used as a mixin and currently applied to {@link Ext.core.Element}, {@link Ext.CompositeElement},
 * {@link Ext.draw.Sprite}, {@link Ext.draw.CompositeSprite}, and {@link Ext.Component}.  Note that Components
 * have a limited subset of what attributes can be animated such as top, left, x, y, height, width, and
 * opacity (color, paddings, and margins can not be animated).
 *
 * ## Animation Basics
 *
 * All animations require three things - `easing`, `duration`, and `to` (the final end value for each property)
 * you wish to animate. Easing and duration are defaulted values specified below.
 * Easing describes how the intermediate values used during a transition will be calculated.
 * {@link Ext.fx.Anim#easing Easing} allows for a transition to change speed over its duration.
 * You may use the defaults for easing and duration, but you must always set a
 * {@link Ext.fx.Anim#to to} property which is the end value for all animations.
 *
 * Popular element 'to' configurations are:
 *
 *  - opacity
 *  - x
 *  - y
 *  - color
 *  - height
 *  - width
 *
 * Popular sprite 'to' configurations are:
 *
 *  - translation
 *  - path
 *  - scale
 *  - stroke
 *  - rotation
 *
 * The default duration for animations is 250 (which is a 1/4 of a second).  Duration is denoted in
 * milliseconds.  Therefore 1 second is 1000, 1 minute would be 60000, and so on. The default easing curve
 * used for all animations is 'ease'.  Popular easing functions are included and can be found in {@link Ext.fx.Anim#easing Easing}.
 *
 * For example, a simple animation to fade out an element with a default easing and duration:
 *
 *     var p1 = Ext.get('myElementId');
 *
 *     p1.animate({
 *         to: {
 *             opacity: 0
 *         }
 *     });
 *
 * To make this animation fade out in a tenth of a second:
 *
 *     var p1 = Ext.get('myElementId');
 *
 *     p1.animate({
 *        duration: 100,
 *         to: {
 *             opacity: 0
 *         }
 *     });
 *
 * ## Animation Queues
 *
 * By default all animations are added to a queue which allows for animation via a chain-style API.
 * For example, the following code will queue 4 animations which occur sequentially (one right after the other):
 *
 *     p1.animate({
 *         to: {
 *             x: 500
 *         }
 *     }).animate({
 *         to: {
 *             y: 150
 *         }
 *     }).animate({
 *         to: {
 *             backgroundColor: '#f00'  //red
 *         }
 *     }).animate({
 *         to: {
 *             opacity: 0
 *         }
 *     });
 *
 * You can change this behavior by calling the {@link Ext.util.Animate#syncFx syncFx} method and all
 * subsequent animations for the specified target will be run concurrently (at the same time).
 *
 *     p1.syncFx();  //this will make all animations run at the same time
 *
 *     p1.animate({
 *         to: {
 *             x: 500
 *         }
 *     }).animate({
 *         to: {
 *             y: 150
 *         }
 *     }).animate({
 *         to: {
 *             backgroundColor: '#f00'  //red
 *         }
 *     }).animate({
 *         to: {
 *             opacity: 0
 *         }
 *     });
 *
 * This works the same as:
 *
 *     p1.animate({
 *         to: {
 *             x: 500,
 *             y: 150,
 *             backgroundColor: '#f00'  //red
 *             opacity: 0
 *         }
 *     });
 *
 * The {@link Ext.util.Animate#stopAnimation stopAnimation} method can be used to stop any
 * currently running animations and clear any queued animations.
 *
 * ## Animation Keyframes
 *
 * You can also set up complex animations with {@link Ext.fx.Anim#keyframe keyframe} which follows the
 * CSS3 Animation configuration pattern. Note rotation, translation, and scaling can only be done for sprites.
 * The previous example can be written with the following syntax:
 *
 *     p1.animate({
 *         duration: 1000,  //one second total
 *         keyframes: {
 *             25: {     //from 0 to 250ms (25%)
 *                 x: 0
 *             },
 *             50: {   //from 250ms to 500ms (50%)
 *                 y: 0
 *             },
 *             75: {  //from 500ms to 750ms (75%)
 *                 backgroundColor: '#f00'  //red
 *             },
 *             100: {  //from 750ms to 1sec
 *                 opacity: 0
 *             }
 *         }
 *     });
 *
 * ## Animation Events
 *
 * Each animation you create has events for {@link Ext.fx.Anim#beforeanimation beforeanimation},
 * {@link Ext.fx.Anim#afteranimate afteranimate}, and {@link Ext.fx.Anim#lastframe lastframe}.
 * Keyframed animations adds an additional {@link Ext.fx.Animator#keyframe keyframe} event which
 * fires for each keyframe in your animation.
 *
 * All animations support the {@link Ext.util.Observable#listeners listeners} configuration to attact functions to these events.
 *
 *     startAnimate: function() {
 *         var p1 = Ext.get('myElementId');
 *         p1.animate({
 *            duration: 100,
 *             to: {
 *                 opacity: 0
 *             },
 *             listeners: {
 *                 beforeanimate:  function() {
 *                     // Execute my custom method before the animation
 *                     this.myBeforeAnimateFn();
 *                 },
 *                 afteranimate: function() {
 *                     // Execute my custom method after the animation
 *                     this.myAfterAnimateFn();
 *                 },
 *                 scope: this
 *         });
 *     },
 *     myBeforeAnimateFn: function() {
 *       // My custom logic
 *     },
 *     myAfterAnimateFn: function() {
 *       // My custom logic
 *     }
 *
 * Due to the fact that animations run asynchronously, you can determine if an animation is currently
 * running on any target by using the {@link Ext.util.Animate#getActiveAnimation getActiveAnimation}
 * method.  This method will return false if there are no active animations or return the currently
 * running {@link Ext.fx.Anim} instance.
 *
 * In this example, we're going to wait for the current animation to finish, then stop any other
 * queued animations before we fade our element's opacity to 0:
 *
 *     var curAnim = p1.getActiveAnimation();
 *     if (curAnim) {
 *         curAnim.on('afteranimate', function() {
 *             p1.stopAnimation();
 *             p1.animate({
 *                 to: {
 *                     opacity: 0
 *                 }
 *             });
 *         });
 *     }
 *
 * @docauthor Jamie Avins <jamie@sencha.com>
 */
Ext.util.Animate = {

    /**
     * <p>Perform custom animation on this object.<p>
     * <p>This method is applicable to both the the {@link Ext.Component Component} class and the {@link Ext.core.Element Element} class.
     * It performs animated transitions of certain properties of this object over a specified timeline.</p>
     * <p>The sole parameter is an object which specifies start property values, end property values, and properties which
     * describe the timeline. Of the properties listed below, only <b><code>to</code></b> is mandatory.</p>
     * <p>Properties include<ul>
     * <li><code>from</code> <div class="sub-desc">An object which specifies start values for the properties being animated.
     * If not supplied, properties are animated from current settings. The actual properties which may be animated depend upon
     * ths object being animated. See the sections below on Element and Component animation.<div></li>
     * <li><code>to</code> <div class="sub-desc">An object which specifies end values for the properties being animated.</div></li>
     * <li><code>duration</code><div class="sub-desc">The duration <b>in milliseconds</b> for which the animation will run.</div></li>
     * <li><code>easing</code> <div class="sub-desc">A string value describing an easing type to modify the rate of change from the default linear to non-linear. Values may be one of:<code><ul>
     * <li>ease</li>
     * <li>easeIn</li>
     * <li>easeOut</li>
     * <li>easeInOut</li>
     * <li>backIn</li>
     * <li>backOut</li>
     * <li>elasticIn</li>
     * <li>elasticOut</li>
     * <li>bounceIn</li>
     * <li>bounceOut</li>
     * </ul></code></div></li>
     * <li><code>keyframes</code> <div class="sub-desc">This is an object which describes the state of animated properties at certain points along the timeline.
     * it is an object containing properties who's names are the percentage along the timeline being described and who's values specify the animation state at that point.</div></li>
     * <li><code>listeners</code> <div class="sub-desc">This is a standard {@link Ext.util.Observable#listeners listeners} configuration object which may be used
     * to inject behaviour at either the <code>beforeanimate</code> event or the <code>afteranimate</code> event.</div></li>
     * </ul></p>
     * <h3>Animating an {@link Ext.core.Element Element}</h3>
     * When animating an Element, the following properties may be specified in <code>from</code>, <code>to</code>, and <code>keyframe</code> objects:<ul>
     * <li><code>x</code> <div class="sub-desc">The page X position in pixels.</div></li>
     * <li><code>y</code> <div class="sub-desc">The page Y position in pixels</div></li>
     * <li><code>left</code> <div class="sub-desc">The element's CSS <code>left</code> value. Units must be supplied.</div></li>
     * <li><code>top</code> <div class="sub-desc">The element's CSS <code>top</code> value. Units must be supplied.</div></li>
     * <li><code>width</code> <div class="sub-desc">The element's CSS <code>width</code> value. Units must be supplied.</div></li>
     * <li><code>height</code> <div class="sub-desc">The element's CSS <code>height</code> value. Units must be supplied.</div></li>
     * <li><code>scrollLeft</code> <div class="sub-desc">The element's <code>scrollLeft</code> value.</div></li>
     * <li><code>scrollTop</code> <div class="sub-desc">The element's <code>scrollLeft</code> value.</div></li>
     * <li><code>opacity</code> <div class="sub-desc">The element's <code>opacity</code> value. This must be a value between <code>0</code> and <code>1</code>.</div></li>
     * </ul>
     * <p><b>Be aware than animating an Element which is being used by an Ext Component without in some way informing the Component about the changed element state
     * will result in incorrect Component behaviour. This is because the Component will be using the old state of the element. To avoid this problem, it is now possible to
     * directly animate certain properties of Components.</b></p>
     * <h3>Animating a {@link Ext.Component Component}</h3>
     * When animating an Element, the following properties may be specified in <code>from</code>, <code>to</code>, and <code>keyframe</code> objects:<ul>
     * <li><code>x</code> <div class="sub-desc">The Component's page X position in pixels.</div></li>
     * <li><code>y</code> <div class="sub-desc">The Component's page Y position in pixels</div></li>
     * <li><code>left</code> <div class="sub-desc">The Component's <code>left</code> value in pixels.</div></li>
     * <li><code>top</code> <div class="sub-desc">The Component's <code>top</code> value in pixels.</div></li>
     * <li><code>width</code> <div class="sub-desc">The Component's <code>width</code> value in pixels.</div></li>
     * <li><code>width</code> <div class="sub-desc">The Component's <code>width</code> value in pixels.</div></li>
     * <li><code>dynamic</code> <div class="sub-desc">Specify as true to update the Component's layout (if it is a Container) at every frame
     * of the animation. <i>Use sparingly as laying out on every intermediate size change is an expensive operation</i>.</div></li>
     * </ul>
     * <p>For example, to animate a Window to a new size, ensuring that its internal layout, and any shadow is correct:</p>
     * <pre><code>
myWindow = Ext.create('Ext.window.Window', {
    title: 'Test Component animation',
    width: 500,
    height: 300,
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    items: [{
        title: 'Left: 33%',
        margins: '5 0 5 5',
        flex: 1
    }, {
        title: 'Left: 66%',
        margins: '5 5 5 5',
        flex: 2
    }]
});
myWindow.show();
myWindow.header.el.on('click', function() {
    myWindow.animate({
        to: {
            width: (myWindow.getWidth() == 500) ? 700 : 500,
            height: (myWindow.getHeight() == 300) ? 400 : 300,
        }
    });
});
</code></pre>
     * <p>For performance reasons, by default, the internal layout is only updated when the Window reaches its final <code>"to"</code> size. If dynamic updating of the Window's child
     * Components is required, then configure the animation with <code>dynamic: true</code> and the two child items will maintain their proportions during the animation.</p>
     * @param {Object} config An object containing properties which describe the animation's start and end states, and the timeline of the animation.
     * @return {Object} this
     */
    animate: function(animObj) {
        var me = this;
        if (Ext.fx.Manager.hasFxBlock(me.id)) {
            return me;
        }
        Ext.fx.Manager.queueFx(new Ext.fx.Anim(me.anim(animObj)));
        return this;
    },

    // @private - process the passed fx configuration.
    anim: function(config) {
        if (!Ext.isObject(config)) {
            return (config) ? {} : false;
        }

        var me = this;

        if (config.stopAnimation) {
            me.stopAnimation();
        }

        Ext.applyIf(config, Ext.fx.Manager.getFxDefaults(me.id));

        return Ext.apply({
            target: me,
            paused: true
        }, config);
    },

    /**
     * Stops any running effects and clears this object's internal effects queue if it contains
     * any additional effects that haven't started yet.
     * @return {Ext.core.Element} The Element
     */
    stopAnimation: function() {
        Ext.fx.Manager.stopAnimation(this.id);
        return this;
    },

    /**
     * Ensures that all effects queued after syncFx is called on this object are
     * run concurrently.  This is the opposite of {@link #sequenceFx}.
     * @return {Object} this
     */
    syncFx: function() {
        Ext.fx.Manager.setFxDefaults(this.id, {
            concurrent: true
        });
        return this;
    },

    /**
     * Ensures that all effects queued after sequenceFx is called on this object are
     * run in sequence.  This is the opposite of {@link #syncFx}.
     * @return {Object} this
     */
    sequenceFx: function() {
        Ext.fx.Manager.setFxDefaults(this.id, {
            concurrent: false
        });
        return this;
    },

    /**
     * Returns thq current animation if this object has any effects actively running or queued, else returns false.
     * @return {Mixed} anim if element has active effects, else false
     */
    getActiveAnimation: function() {
        return Ext.fx.Manager.getActiveAnimation(this.id);
    }
};

/**
 * @class Ext.layout.component.Draw
 * @extends Ext.layout.component.Component
 * @private
 *
 */

Ext.layout.DrawLayout = Ext.extend(Ext.layout.AutoComponentLayout, {
    type: 'draw',

    onLayout : function(width, height) {
        this.owner.surface.setSize(width, height);
        Ext.layout.DrawLayout.superclass.onLayout.apply(this, arguments);
    }
});
Ext.regLayout('draw', Ext.layout.DrawLayout);

/**
 * @class Ext.draw.Color
 * @extends Object
 *
 * Represents an RGB color and provides helper functions get
 * color components in HSL color space.
 */
Ext.ns('Ext.draw.engine');

Ext.draw.Color = Ext.extend(Object, {

    colorToHexRe: /(.*?)rgb\((\d+),\s*(\d+),\s*(\d+)\)/,
    rgbRe: /\s*rgba?\s*\(\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*(,\s*[0-9\.]+\s*)?\)\s*/,
    hexRe: /\s*#([0-9a-fA-F][0-9a-fA-F]?)([0-9a-fA-F][0-9a-fA-F]?)([0-9a-fA-F][0-9a-fA-F]?)\s*/,

    /**
     * @cfg {Number} lightnessFactor
     *
     * The default factor to compute the lighter or darker color. Defaults to 0.2.
     */
    lightnessFactor: 0.2,

    /**
     * @constructor
     * @param {Number} red Red component (0..255)
     * @param {Number} green Green component (0..255)
     * @param {Number} blue Blue component (0..255)
     */
    // COMPAT Ext.util.Numbers -> Ext.Number
    constructor : function(red, green, blue) {
        var me = this,
            clamp = Ext.util.Numbers.constrain;
        me.r = clamp(red, 0, 255);
        me.g = clamp(green, 0, 255);
        me.b = clamp(blue, 0, 255);
    },

    /**
     * Get the red component of the color, in the range 0..255.
     * @return {Number}
     */
    getRed: function() {
        return this.r;
    },

    /**
     * Get the green component of the color, in the range 0..255.
     * @return {Number}
     */
    getGreen: function() {
        return this.g;
    },

    /**
     * Get the blue component of the color, in the range 0..255.
     * @return {Number}
     */
    getBlue: function() {
        return this.b;
    },

    /**
     * Get the RGB values.
     * @return {Array}
     */
    getRGB: function() {
        var me = this;
        return [me.r, me.g, me.b];
    },

    /**
     * Get the equivalent HSL components of the color.
     * @return {Array}
     */
    getHSL: function() {
        var me = this,
            r = me.r / 255,
            g = me.g / 255,
            b = me.b / 255,
            max = Math.max(r, g, b),
            min = Math.min(r, g, b),
            delta = max - min,
            h,
            s = 0,
            l = 0.5 * (max + min);

        // min==max means achromatic (hue is undefined)
        if (min != max) {
            s = (l < 0.5) ? delta / (max + min) : delta / (2 - max - min);
            if (r == max) {
                h = 60 * (g - b) / delta;
            } else if (g == max) {
                h = 120 + 60 * (b - r) / delta;
            } else {
                h = 240 + 60 * (r - g) / delta;
            }
            if (h < 0) {
                h += 360;
            }
            if (h >= 360) {
                h -= 360;
            }
        }
        return [h, s, l];
    },

    /**
     * Return a new color that is lighter than this color.
     * @param {Number} factor Lighter factor (0..1), default to 0.2
     * @return Ext.draw.Color
     */
    getLighter: function(factor) {
        var hsl = this.getHSL();
        factor = factor || this.lightnessFactor;
        // COMPAT Ext.util.Numbers -> Ext.Number
        hsl[2] = Ext.util.Numbers.constrain(hsl[2] + factor, 0, 1);
        return this.fromHSL(hsl[0], hsl[1], hsl[2]);
    },

    /**
     * Return a new color that is darker than this color.
     * @param {Number} factor Darker factor (0..1), default to 0.2
     * @return Ext.draw.Color
     */
    getDarker: function(factor) {
        factor = factor || this.lightnessFactor;
        return this.getLighter(-factor);
    },

    /**
     * Return the color in the hex format, i.e. '#rrggbb'.
     * @return {String}
     */
    toString: function() {
        var me = this,
            round = Math.round,
            r = round(me.r).toString(16),
            g = round(me.g).toString(16),
            b = round(me.b).toString(16);
        r = (r.length == 1) ? '0' + r : r;
        g = (g.length == 1) ? '0' + g : g;
        b = (b.length == 1) ? '0' + b : b;
        return ['#', r, g, b].join('');
    },

    /**
     * Convert a color to hexadecimal format.
     *
     * @param {String|Array} color The color value (i.e 'rgb(255, 255, 255)', 'color: #ffffff').
     * Can also be an Array, in this case the function handles the first member.
     * @returns {String} The color in hexadecimal format.
     */
    toHex: function(color) {
        if (Ext.isArray(color)) {
            color = color[0];
        }
        if (!Ext.isString(color)) {
            return '';
        }
        if (color.substr(0, 1) === '#') {
            return color;
        }
        var digits = this.colorToHexRe.exec(color);

        if (Ext.isArray(digits)) {
            var red = parseInt(digits[2], 10),
                green = parseInt(digits[3], 10),
                blue = parseInt(digits[4], 10),
                rgb = blue | (green << 8) | (red << 16);
            return digits[1] + '#' + ("000000" + rgb.toString(16)).slice(-6);
        }
        else {
            return '';
        }
    },

    /**
     * Parse the string and create a new color.
     *
     * Supported formats: '#rrggbb', '#rgb', and 'rgb(r,g,b)'.
     *
     * If the string is not recognized, an undefined will be returned instead.
     *
     * @param {String} str Color in string.
     * @returns Ext.draw.Color
     */
    fromString: function(str) {
        var values, r, g, b,
            parse = parseInt;

        if ((str.length == 4 || str.length == 7) && str.substr(0, 1) === '#') {
            values = str.match(this.hexRe);
            if (values) {
                r = parse(values[1], 16) >> 0;
                g = parse(values[2], 16) >> 0;
                b = parse(values[3], 16) >> 0;
                if (str.length == 4) {
                    r += (r * 16);
                    g += (g * 16);
                    b += (b * 16);
                }
            }
        }
        else {
            values = str.match(this.rgbRe);
            if (values) {
                r = values[1];
                g = values[2];
                b = values[3];
            }
        }

        return (typeof r == 'undefined') ? undefined : new Ext.draw.Color(r, g, b);
    },

    /**
     * Returns the gray value (0 to 255) of the color.
     *
     * The gray value is calculated using the formula r*0.3 + g*0.59 + b*0.11.
     *
     * @returns {Number}
     */
    getGrayscale: function() {
        // http://en.wikipedia.org/wiki/Grayscale#Converting_color_to_grayscale
        return this.r * 0.3 + this.g * 0.59 + this.b * 0.11;
    },

    /**
     * Create a new color based on the specified HSL values.
     *
     * @param {Number} h Hue component (0..359)
     * @param {Number} s Saturation component (0..1)
     * @param {Number} l Lightness component (0..1)
     * @returns Ext.draw.Color
     */
    fromHSL: function(h, s, l) {
        var C, X, m, rgb = [],
            abs = Math.abs,
            floor = Math.floor;

        if (s == 0 || h == null) {
            // achromatic
            rgb = [l, l, l];
        }
        else {
            // http://en.wikipedia.org/wiki/HSL_and_HSV#From_HSL
            // C is the chroma
            // X is the second largest component
            // m is the lightness adjustment
            h /= 60;
            C = s * (1 - abs(2 * l - 1));
            X = C * (1 - abs(h - 2 * floor(h / 2) - 1));
            m = l - C / 2;
            switch (floor(h)) {
                case 0:
                    rgb = [C, X, 0];
                    break;
                case 1:
                    rgb = [X, C, 0];
                    break;
                case 2:
                    rgb = [0, C, X];
                    break;
                case 3:
                    rgb = [0, X, C];
                    break;
                case 4:
                    rgb = [X, 0, C];
                    break;
                case 5:
                    rgb = [C, 0, X];
                    break;
            }
            rgb = [rgb[0] + m, rgb[1] + m, rgb[2] + m];
        }
        return new Ext.draw.Color(rgb[0] * 255, rgb[1] * 255, rgb[2] * 255);
    }
});

(function() {
    var prototype = Ext.draw.Color.prototype;
    Ext.draw.Color.fromHSL = function() {
        return prototype.fromHSL.apply(prototype, arguments);
    };
    Ext.draw.Color.fromString = function() {
        return prototype.fromString.apply(prototype, arguments);
    };
    Ext.draw.Color.toHex = function() {
        return prototype.toHex.apply(prototype, arguments);
    };
})();

/**
 * @class Ext.draw.Draw
 * Base Drawing class.  Provides base drawing functions.
 */

Ext.draw.Draw = {

    pathToStringRE: /,?([achlmqrstvxz]),?/gi,
    pathCommandRE: /([achlmqstvz])[\s,]*((-?\d*\.?\d*(?:e[-+]?\d+)?\s*,?\s*)+)/ig,
    pathValuesRE: /(-?\d*\.?\d*(?:e[-+]?\d+)?)\s*,?\s*/ig,
    stopsRE: /^(\d+%?)$/,
    radian: Math.PI / 180,
    pi2: Math.PI * 2,
    snapEndsIntervalWeights: [[0, 15], [20, 4], [30, 2], [40, 4], [50, 9], [60, 4], [70, 2], [80, 4], [100, 15]],

    is: function(o, type) {
        type = String(type).toLowerCase();
        return (type == "object" && o === Object(o)) ||
            (type == "undefined" && typeof o == type) ||
            (type == "null" && o === null) ||
            (type == "array" && Array.isArray && Array.isArray(o)) ||
            (Object.prototype.toString.call(o).toLowerCase().slice(8, -1)) == type;
    },

    ellipsePath: function(sprite) {
        var attr = sprite.attr;
        return Ext.String.format("M{0},{1}A{2},{3},0,1,1,{0},{4}A{2},{3},0,1,1,{0},{1}z", attr.x, attr.y - attr.ry, attr.rx, attr.ry, attr.y + attr.ry);
    },

    rectPath: function(sprite) {
        var attr = sprite.attr;
        if (attr.radius) {
            return Ext.String.format("M{0},{1}l{2},0a{3},{3},0,0,1,{3},{3}l0,{5}a{3},{3},0,0,1,{4},{3}l{6},0a{3},{3},0,0,1,{4},{4}l0,{7}a{3},{3},0,0,1,{3},{4}z", attr.x + attr.radius, attr.y, attr.width - attr.radius * 2, attr.radius, -attr.radius, attr.height - attr.radius * 2, attr.radius * 2 - attr.width, attr.radius * 2 - attr.height);
        }
        else {
            return Ext.String.format("M{0},{1}l{2},0,0,{3},{4},0z", attr.x, attr.y, attr.width, attr.height, -attr.width);
        }
    },

    // Convert the passed arrayPath to a proper SVG path string (d attribute)
    pathToString: function(arrayPath) {
        if (Ext.isArray(arrayPath)) {
            arrayPath = arrayPath.join(',');
        }
        return arrayPath.replace(Ext.draw.Draw.pathToStringRE, "$1");
    },

    parsePathString: function (pathString) {
        if (!pathString) {
            return null;
        }
        var paramCounts = {a: 7, c: 6, h: 1, l: 2, m: 2, q: 4, s: 4, t: 2, v: 1, z: 0},
            data = [],
            me = this;
        if (me.is(pathString, "array") && me.is(pathString[0], "array")) { // rough assumption
            data = me.pathClone(pathString);
        }
        if (!data.length) {
            Ext.draw.Draw.pathToString(pathString).replace(me.pathCommandRE, function (a, b, c) {
                var params = [],
                    name = b.toLowerCase();
                c.replace(me.pathValuesRE, function (a, b) {
                    b && params.push(+b);
                });
                if (name == "m" && params.length > 2) {
                    data.push([b].concat(params.splice(0, 2)));
                    name = "l";
                    b = (b == "m") ? "l" : "L";
                }
                while (params.length >= paramCounts[name]) {
                    data.push([b].concat(params.splice(0, paramCounts[name])));
                    if (!paramCounts[name]) {
                        break;
                    }
                }
            });
        }
        return data;
    },

    mapPath: function (path, matrix) {
        if (!matrix) {
            return path;
        }
        var x, y, i, ii, j, jj, pathi;
        path = this.path2curve(path);
        for (i = 0, ii = path.length; i < ii; i++) {
            pathi = path[i];
            for (j = 1, jj = pathi.length; j < jj-1; j += 2) {
                x = matrix.x(pathi[j], pathi[j + 1]);
                y = matrix.y(pathi[j], pathi[j + 1]);
                pathi[j] = x;
                pathi[j + 1] = y;
            }
        }
        return path;
    },

    pathClone: function(pathArray) {
        var res = [],
            j, jj, i, ii;
        if (!this.is(pathArray, "array") || !this.is(pathArray && pathArray[0], "array")) { // rough assumption
            pathArray = this.parsePathString(pathArray);
        }
        for (i = 0, ii = pathArray.length; i < ii; i++) {
            res[i] = [];
            for (j = 0, jj = pathArray[i].length; j < jj; j++) {
                res[i][j] = pathArray[i][j];
            }
        }
        return res;
    },

    pathToAbsolute: function (pathArray) {
        if (!this.is(pathArray, "array") || !this.is(pathArray && pathArray[0], "array")) { // rough assumption
            pathArray = this.parsePathString(pathArray);
        }
        var res = [],
            x = 0,
            y = 0,
            mx = 0,
            my = 0,
            i = 0,
            ln = pathArray.length,
            r, pathSegment, j, ln2;
        // MoveTo initial x/y position
        if (pathArray[0][0] == "M") {
            x = +pathArray[0][1];
            y = +pathArray[0][2];
            mx = x;
            my = y;
            i++;
            res[0] = ["M", x, y];
        }
        for (; i < ln; i++) {
            r = res[i] = [];
            pathSegment = pathArray[i];
            if (pathSegment[0] != pathSegment[0].toUpperCase()) {
                r[0] = pathSegment[0].toUpperCase();
                switch (r[0]) {
                    // Elliptical Arc
                    case "A":
                        r[1] = pathSegment[1];
                        r[2] = pathSegment[2];
                        r[3] = pathSegment[3];
                        r[4] = pathSegment[4];
                        r[5] = pathSegment[5];
                        r[6] = +(pathSegment[6] + x);
                        r[7] = +(pathSegment[7] + y);
                        break;
                    // Vertical LineTo
                    case "V":
                        r[1] = +pathSegment[1] + y;
                        break;
                    // Horizontal LineTo
                    case "H":
                        r[1] = +pathSegment[1] + x;
                        break;
                    case "M":
                    // MoveTo
                        mx = +pathSegment[1] + x;
                        my = +pathSegment[2] + y;
                    default:
                        j = 1;
                        ln2 = pathSegment.length;
                        for (; j < ln2; j++) {
                            r[j] = +pathSegment[j] + ((j % 2) ? x : y);
                        }
                }
            }
            else {
                j = 0;
                ln2 = pathSegment.length;
                for (; j < ln2; j++) {
                    res[i][j] = pathSegment[j];
                }
            }
            switch (r[0]) {
                // ClosePath
                case "Z":
                    x = mx;
                    y = my;
                    break;
                // Horizontal LineTo
                case "H":
                    x = r[1];
                    break;
                // Vertical LineTo
                case "V":
                    y = r[1];
                    break;
                // MoveTo
                case "M":
                    pathSegment = res[i];
                    ln2 = pathSegment.length;
                    mx = pathSegment[ln2 - 2];
                    my = pathSegment[ln2 - 1];
                default:
                    pathSegment = res[i];
                    ln2 = pathSegment.length;
                    x = pathSegment[ln2 - 2];
                    y = pathSegment[ln2 - 1];
            }
        }
        return res;
    },

    // Returns a path converted to a set of curveto commands
    path2curve: function (path) {
        var me = this,
            points = me.pathToAbsolute(path),
            ln = points.length,
            attrs = {x: 0, y: 0, bx: 0, by: 0, X: 0, Y: 0, qx: null, qy: null},
            i, seg, segLn, point;

        for (i = 0; i < ln; i++) {
            points[i] = me.command2curve(points[i], attrs);
            if (points[i].length > 7) {
                    points[i].shift();
                    point = points[i];
                    while (point.length) {
                        points.splice(i++, 0, ["C"].concat(point.splice(0, 6)));
                    }
                    points.splice(i, 1);
                    ln = points.length;
                }
            seg = points[i];
            segLn = seg.length;
            attrs.x = seg[segLn - 2];
            attrs.y = seg[segLn - 1];
            attrs.bx = parseFloat(seg[segLn - 4]) || attrs.x;
            attrs.by = parseFloat(seg[segLn - 3]) || attrs.y;
        }
        return points;
    },

    interpolatePaths: function (path, path2) {
        var me = this,
            p = me.pathToAbsolute(path),
            p2 = me.pathToAbsolute(path2),
            attrs = {x: 0, y: 0, bx: 0, by: 0, X: 0, Y: 0, qx: null, qy: null},
            attrs2 = {x: 0, y: 0, bx: 0, by: 0, X: 0, Y: 0, qx: null, qy: null},
            fixArc = function (pp, i) {
                if (pp[i].length > 7) {
                    pp[i].shift();
                    var pi = pp[i];
                    while (pi.length) {
                        pp.splice(i++, 0, ["C"].concat(pi.splice(0, 6)));
                    }
                    pp.splice(i, 1);
                    ii = Math.max(p.length, p2.length || 0);
                }
            },
            fixM = function (path1, path2, a1, a2, i) {
                if (path1 && path2 && path1[i][0] == "M" && path2[i][0] != "M") {
                    path2.splice(i, 0, ["M", a2.x, a2.y]);
                    a1.bx = 0;
                    a1.by = 0;
                    a1.x = path1[i][1];
                    a1.y = path1[i][2];
                    ii = Math.max(p.length, p2.length || 0);
                }
            };
        for (var i = 0, ii = Math.max(p.length, p2.length || 0); i < ii; i++) {
            p[i] = me.command2curve(p[i], attrs);
            fixArc(p, i);
            (p2[i] = me.command2curve(p2[i], attrs2));
            fixArc(p2, i);
            fixM(p, p2, attrs, attrs2, i);
            fixM(p2, p, attrs2, attrs, i);
            var seg = p[i],
                seg2 = p2[i],
                seglen = seg.length,
                seg2len = seg2.length;
            attrs.x = seg[seglen - 2];
            attrs.y = seg[seglen - 1];
            attrs.bx = parseFloat(seg[seglen - 4]) || attrs.x;
            attrs.by = parseFloat(seg[seglen - 3]) || attrs.y;
            attrs2.bx = (parseFloat(seg2[seg2len - 4]) || attrs2.x);
            attrs2.by = (parseFloat(seg2[seg2len - 3]) || attrs2.y);
            attrs2.x = seg2[seg2len - 2];
            attrs2.y = seg2[seg2len - 1];
        }
        return [p, p2];
    },

    //Returns any path command as a curveto command based on the attrs passed
    command2curve: function (pathCommand, d) {
        var me = this;
        if (!pathCommand) {
            return ["C", d.x, d.y, d.x, d.y, d.x, d.y];
        }
        if (pathCommand[0] != "T" && pathCommand[0] != "Q") {
            d.qx = d.qy = null;
        }
        switch (pathCommand[0]) {
            case "M":
                d.X = pathCommand[1];
                d.Y = pathCommand[2];
                break;
            case "A":
                pathCommand = ["C"].concat(me.arc2curve.apply(me, [d.x, d.y].concat(pathCommand.slice(1))));
                break;
            case "S":
                pathCommand = ["C", d.x + (d.x - (d.bx || d.x)), d.y + (d.y - (d.by || d.y))].concat(pathCommand.slice(1));
                break;
            case "T":
                d.qx = d.x + (d.x - (d.qx || d.x));
                d.qy = d.y + (d.y - (d.qy || d.y));
                pathCommand = ["C"].concat(me.quadratic2curve(d.x, d.y, d.qx, d.qy, pathCommand[1], pathCommand[2]));
                break;
            case "Q":
                d.qx = pathCommand[1];
                d.qy = pathCommand[2];
                pathCommand = ["C"].concat(me.quadratic2curve(d.x, d.y, pathCommand[1], pathCommand[2], pathCommand[3], pathCommand[4]));
                break;
            case "L":
                pathCommand = ["C"].concat(d.x, d.y, pathCommand[1], pathCommand[2], pathCommand[1], pathCommand[2]);
                break;
            case "H":
                pathCommand = ["C"].concat(d.x, d.y, pathCommand[1], d.y, pathCommand[1], d.y);
                break;
            case "V":
                pathCommand = ["C"].concat(d.x, d.y, d.x, pathCommand[1], d.x, pathCommand[1]);
                break;
            case "Z":
                pathCommand = ["C"].concat(d.x, d.y, d.X, d.Y, d.X, d.Y);
                break;
        }
        return pathCommand;
    },

    quadratic2curve: function (x1, y1, ax, ay, x2, y2) {
        var _13 = 1 / 3,
            _23 = 2 / 3;
        return [
                _13 * x1 + _23 * ax,
                _13 * y1 + _23 * ay,
                _13 * x2 + _23 * ax,
                _13 * y2 + _23 * ay,
                x2,
                y2
            ];
    },

    rotate: function (x, y, rad) {
        var cos = Math.cos(rad),
            sin = Math.sin(rad),
            X = x * cos - y * sin,
            Y = x * sin + y * cos;
        return {x: X, y: Y};
    },

    arc2curve: function (x1, y1, rx, ry, angle, large_arc_flag, sweep_flag, x2, y2, recursive) {
        // for more information of where this Math came from visit:
        // http://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes
        var me = this,
            PI = Math.PI,
            radian = me.radian,
            _120 = PI * 120 / 180,
            rad = radian * (+angle || 0),
            res = [],
            math = Math,
            mcos = math.cos,
            msin = math.sin,
            msqrt = math.sqrt,
            mabs = math.abs,
            masin = math.asin,
            xy, cos, sin, x, y, h, rx2, ry2, k, cx, cy, f1, f2, df, c1, s1, c2, s2,
            t, hx, hy, m1, m2, m3, m4, newres, i, ln, f2old, x2old, y2old;
        if (!recursive) {
            xy = me.rotate(x1, y1, -rad);
            x1 = xy.x;
            y1 = xy.y;
            xy = me.rotate(x2, y2, -rad);
            x2 = xy.x;
            y2 = xy.y;
            cos = mcos(radian * angle);
            sin = msin(radian * angle);
            x = (x1 - x2) / 2;
            y = (y1 - y2) / 2;
            h = (x * x) / (rx * rx) + (y * y) / (ry * ry);
            if (h > 1) {
                h = msqrt(h);
                rx = h * rx;
                ry = h * ry;
            }
            rx2 = rx * rx;
            ry2 = ry * ry;
            k = (large_arc_flag == sweep_flag ? -1 : 1) *
                    msqrt(mabs((rx2 * ry2 - rx2 * y * y - ry2 * x * x) / (rx2 * y * y + ry2 * x * x)));
            cx = k * rx * y / ry + (x1 + x2) / 2;
            cy = k * -ry * x / rx + (y1 + y2) / 2;
            f1 = masin(((y1 - cy) / ry).toFixed(7));
            f2 = masin(((y2 - cy) / ry).toFixed(7));

            f1 = x1 < cx ? PI - f1 : f1;
            f2 = x2 < cx ? PI - f2 : f2;
            if (f1 < 0) {
                f1 = PI * 2 + f1;
            }
            if (f2 < 0) {
                f2 = PI * 2 + f2;
            }
            if (sweep_flag && f1 > f2) {
                f1 = f1 - PI * 2;
            }
            if (!sweep_flag && f2 > f1) {
                f2 = f2 - PI * 2;
            }
        }
        else {
            f1 = recursive[0];
            f2 = recursive[1];
            cx = recursive[2];
            cy = recursive[3];
        }
        df = f2 - f1;
        if (mabs(df) > _120) {
            f2old = f2;
            x2old = x2;
            y2old = y2;
            f2 = f1 + _120 * (sweep_flag && f2 > f1 ? 1 : -1);
            x2 = cx + rx * mcos(f2);
            y2 = cy + ry * msin(f2);
            res = me.arc2curve(x2, y2, rx, ry, angle, 0, sweep_flag, x2old, y2old, [f2, f2old, cx, cy]);
        }
        df = f2 - f1;
        c1 = mcos(f1);
        s1 = msin(f1);
        c2 = mcos(f2);
        s2 = msin(f2);
        t = math.tan(df / 4);
        hx = 4 / 3 * rx * t;
        hy = 4 / 3 * ry * t;
        m1 = [x1, y1];
        m2 = [x1 + hx * s1, y1 - hy * c1];
        m3 = [x2 + hx * s2, y2 - hy * c2];
        m4 = [x2, y2];
        m2[0] = 2 * m1[0] - m2[0];
        m2[1] = 2 * m1[1] - m2[1];
        if (recursive) {
            return [m2, m3, m4].concat(res);
        }
        else {
            res = [m2, m3, m4].concat(res).join().split(",");
            newres = [];
            ln = res.length;
            for (i = 0;  i < ln; i++) {
                newres[i] = i % 2 ? me.rotate(res[i - 1], res[i], rad).y : me.rotate(res[i], res[i + 1], rad).x;
            }
            return newres;
        }
    },

    pathDimensions: function (path) {
        if (!path || !path.length) {
            return {x: 0, y: 0, width: 0, height: 0};
        }
        path = this.path2curve(path);
        var x = 0,
            y = 0,
            X = [],
            Y = [],
            i = 0,
            ln = path.length,
            p, xmin, ymin, dim;
        for (; i < ln; i++) {
            p = path[i];
            if (p[0] == "M") {
                x = p[1];
                y = p[2];
                X.push(x);
                Y.push(y);
            }
            else {
                dim = this.curveDim(x, y, p[1], p[2], p[3], p[4], p[5], p[6]);
                X = X.concat(dim.min.x, dim.max.x);
                Y = Y.concat(dim.min.y, dim.max.y);
                x = p[5];
                y = p[6];
            }
        }
        xmin = Math.min.apply(0, X);
        ymin = Math.min.apply(0, Y);
        return {
            x: xmin,
            y: ymin,
            path: path,
            width: Math.max.apply(0, X) - xmin,
            height: Math.max.apply(0, Y) - ymin
        };
    },

    intersectInside: function(path, cp1, cp2) {
        return (cp2[0] - cp1[0]) * (path[1] - cp1[1]) > (cp2[1] - cp1[1]) * (path[0] - cp1[0]);
    },

    intersectIntersection: function(s, e, cp1, cp2) {
        var p = [],
            dcx = cp1[0] - cp2[0],
            dcy = cp1[1] - cp2[1],
            dpx = s[0] - e[0],
            dpy = s[1] - e[1],
            n1 = cp1[0] * cp2[1] - cp1[1] * cp2[0],
            n2 = s[0] * e[1] - s[1] * e[0],
            n3 = 1 / (dcx * dpy - dcy * dpx);

        p[0] = (n1 * dpx - n2 * dcx) * n3;
        p[1] = (n1 * dpy - n2 * dcy) * n3;
        return p;
    },

    intersect: function(subjectPolygon, clipPolygon) {
        var me = this,
            i = 0,
            ln = clipPolygon.length,
            cp1 = clipPolygon[ln - 1],
            outputList = subjectPolygon,
            cp2, s, e, ln2, inputList, j;
        for (; i < ln; ++i) {
            cp2 = clipPolygon[i];
            inputList = outputList;
            outputList = [];
            s = inputList[inputList.length - 1];
            j = 0;
            ln2 = inputList.length;
            for (; j < ln2; j++) {
                e = inputList[j];
                if (me.intersectInside(e, cp1, cp2)) {
                    if (!me.intersectInside(s, cp1, cp2)) {
                        outputList.push(me.intersectIntersection(s, e, cp1, cp2));
                    }
                    outputList.push(e);
                }
                else if (me.intersectInside(s, cp1, cp2)) {
                    outputList.push(me.intersectIntersection(s, e, cp1, cp2));
                }
                s = e;
            }
            cp1 = cp2;
        }
        return outputList;
    },

    curveDim: function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y) {
        var a = (c2x - 2 * c1x + p1x) - (p2x - 2 * c2x + c1x),
            b = 2 * (c1x - p1x) - 2 * (c2x - c1x),
            c = p1x - c1x,
            t1 = (-b + Math.sqrt(b * b - 4 * a * c)) / 2 / a,
            t2 = (-b - Math.sqrt(b * b - 4 * a * c)) / 2 / a,
            y = [p1y, p2y],
            x = [p1x, p2x],
            dot;
        if (Math.abs(t1) > 1e12) {
            t1 = 0.5;
        }
        if (Math.abs(t2) > 1e12) {
            t2 = 0.5;
        }
        if (t1 > 0 && t1 < 1) {
            dot = this.findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t1);
            x.push(dot.x);
            y.push(dot.y);
        }
        if (t2 > 0 && t2 < 1) {
            dot = this.findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t2);
            x.push(dot.x);
            y.push(dot.y);
        }
        a = (c2y - 2 * c1y + p1y) - (p2y - 2 * c2y + c1y);
        b = 2 * (c1y - p1y) - 2 * (c2y - c1y);
        c = p1y - c1y;
        t1 = (-b + Math.sqrt(b * b - 4 * a * c)) / 2 / a;
        t2 = (-b - Math.sqrt(b * b - 4 * a * c)) / 2 / a;
        if (Math.abs(t1) > 1e12) {
            t1 = 0.5;
        }
        if (Math.abs(t2) > 1e12) {
            t2 = 0.5;
        }
        if (t1 > 0 && t1 < 1) {
            dot = this.findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t1);
            x.push(dot.x);
            y.push(dot.y);
        }
        if (t2 > 0 && t2 < 1) {
            dot = this.findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t2);
            x.push(dot.x);
            y.push(dot.y);
        }
        return {
            min: {x: Math.min.apply(0, x), y: Math.min.apply(0, y)},
            max: {x: Math.max.apply(0, x), y: Math.max.apply(0, y)}
        };
    },

    /**
     * @private
     *
     * Calculates bezier curve control anchor points for a particular point in a path, with a
     * smoothing curve applied. The smoothness of the curve is controlled by the 'value' parameter.
     * Note that this algorithm assumes that the line being smoothed is normalized going from left
     * to right; it makes special adjustments assuming this orientation.
     *
     * @param {Number} prevX X coordinate of the previous point in the path
     * @param {Number} prevY Y coordinate of the previous point in the path
     * @param {Number} curX X coordinate of the current point in the path
     * @param {Number} curY Y coordinate of the current point in the path
     * @param {Number} nextX X coordinate of the next point in the path
     * @param {Number} nextY Y coordinate of the next point in the path
     * @param {Number} value A value to control the smoothness of the curve; this is used to
     *                 divide the distance between points, so a value of 2 corresponds to
     *                 half the distance between points (a very smooth line) while higher values
     *                 result in less smooth curves. Defaults to 4.
     * @return {Object} Object containing x1, y1, x2, y2 bezier control anchor points; x1 and y1
     *                  are the control point for the curve toward the previous path point, and
     *                  x2 and y2 are the control point for the curve toward the next path point.
     */
    getAnchors: function (prevX, prevY, curX, curY, nextX, nextY, value) {
        value = value || 4;
        var math = Math,
            PI = math.PI,
            halfPI = PI / 2,
            abs = math.abs,
            sin = math.sin,
            cos = math.cos,
            atan = math.atan,
            control1Length, control2Length, control1Angle, control2Angle,
            control1X, control1Y, control2X, control2Y, alpha;

        // Find the length of each control anchor line, by dividing the horizontal distance
        // between points by the value parameter.
        control1Length = (curX - prevX) / value;
        control2Length = (nextX - curX) / value;

        // Determine the angle of each control anchor line. If the middle point is a vertical
        // turnaround then we force it to a flat horizontal angle to prevent the curve from
        // dipping above or below the middle point. Otherwise we use an angle that points
        // toward the previous/next target point.
        if ((curY >= prevY && curY >= nextY) || (curY <= prevY && curY <= nextY)) {
            control1Angle = control2Angle = halfPI;
        } else {
            control1Angle = atan((curX - prevX) / abs(curY - prevY));
            if (prevY < curY) {
                control1Angle = PI - control1Angle;
            }
            control2Angle = atan((nextX - curX) / abs(curY - nextY));
            if (nextY < curY) {
                control2Angle = PI - control2Angle;
            }
        }

        // Adjust the calculated angles so they point away from each other on the same line
        alpha = halfPI - ((control1Angle + control2Angle) % (PI * 2)) / 2;
        if (alpha > halfPI) {
            alpha -= PI;
        }
        control1Angle += alpha;
        control2Angle += alpha;

        // Find the control anchor points from the angles and length
        control1X = curX - control1Length * sin(control1Angle);
        control1Y = curY + control1Length * cos(control1Angle);
        control2X = curX + control2Length * sin(control2Angle);
        control2Y = curY + control2Length * cos(control2Angle);

        // One last adjustment, make sure that no control anchor point extends vertically past
        // its target prev/next point, as that results in curves dipping above or below and
        // bending back strangely. If we find this happening we keep the control angle but
        // reduce the length of the control line so it stays within bounds.
        if ((curY > prevY && control1Y < prevY) || (curY < prevY && control1Y > prevY)) {
            control1X += abs(prevY - control1Y) * (control1X - curX) / (control1Y - curY);
            control1Y = prevY;
        }
        if ((curY > nextY && control2Y < nextY) || (curY < nextY && control2Y > nextY)) {
            control2X -= abs(nextY - control2Y) * (control2X - curX) / (control2Y - curY);
            control2Y = nextY;
        }

        return {
            x1: control1X,
            y1: control1Y,
            x2: control2X,
            y2: control2Y
        };
    },

    /* Smoothing function for a path.  Converts a path into cubic beziers.  Value defines the divider of the distance between points.
     * Defaults to a value of 4.
     */
    smooth: function (originalPath, value) {
        var path = this.path2curve(originalPath),
            newp = [path[0]],
            x = path[0][1],
            y = path[0][2],
            j,
            points,
            i = 1,
            ii = path.length,
            beg = 1,
            mx = x,
            my = y,
            cx = 0,
            cy = 0;
        for (; i < ii; i++) {
            var pathi = path[i],
                pathil = pathi.length,
                pathim = path[i - 1],
                pathiml = pathim.length,
                pathip = path[i + 1],
                pathipl = pathip && pathip.length;
            if (pathi[0] == "M") {
                mx = pathi[1];
                my = pathi[2];
                j = i + 1;
                while (path[j][0] != "C") {
                    j++;
                }
                cx = path[j][5];
                cy = path[j][6];
                newp.push(["M", mx, my]);
                beg = newp.length;
                x = mx;
                y = my;
                continue;
            }
            if (pathi[pathil - 2] == mx && pathi[pathil - 1] == my && (!pathip || pathip[0] == "M")) {
                var begl = newp[beg].length;
                points = this.getAnchors(pathim[pathiml - 2], pathim[pathiml - 1], mx, my, newp[beg][begl - 2], newp[beg][begl - 1], value);
                newp[beg][1] = points.x2;
                newp[beg][2] = points.y2;
            }
            else if (!pathip || pathip[0] == "M") {
                points = {
                    x1: pathi[pathil - 2],
                    y1: pathi[pathil - 1]
                };
            } else {
                points = this.getAnchors(pathim[pathiml - 2], pathim[pathiml - 1], pathi[pathil - 2], pathi[pathil - 1], pathip[pathipl - 2], pathip[pathipl - 1], value);
            }
            newp.push(["C", x, y, points.x1, points.y1, pathi[pathil - 2], pathi[pathil - 1]]);
            x = points.x2;
            y = points.y2;
        }
        return newp;
    },

    findDotAtSegment: function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t) {
        var t1 = 1 - t;
        return {
            x: Math.pow(t1, 3) * p1x + Math.pow(t1, 2) * 3 * t * c1x + t1 * 3 * t * t * c2x + Math.pow(t, 3) * p2x,
            y: Math.pow(t1, 3) * p1y + Math.pow(t1, 2) * 3 * t * c1y + t1 * 3 * t * t * c2y + Math.pow(t, 3) * p2y
        };
    },


    /**
     * snapEnds is a utility function that gives you axis ticks information based on start, end
     * and preferred number of steps. It happens quite often that you have just a dataset and need to
     * build an axis. If you simply take min and max and divide delta to number of steps you could get
     * very ugly numbers. Lets say you have min = 0.532 and max = 0.823 and you want to draw axis
     * across 20 steps. Simple calculation like (max - min) / steps will give us: 0.014549(9), so
     * your axis will look like this:
     *
     *     0.532, 0.5465499, 0.5610998, 0.5756497, etc
     *
     * Not pretty at all. snapEnds will give different set of numbers for the same values:
     *
     *     0.5, 0.52, 0.54, 0.56, 0.58, 0.6, 0.62, ... 0.8, 0.82, 0.84
     *
     * It starts a bit earlier and ends a bit later and trying to find a step which will look nice.
     *
     * @param {Number} from The minimum value in the data
     * @param {Number} to The maximum value in the data
     * @param {Number} stepsMax The maximum number of ticks
     * @param {Number} endsLocked If true, the 'from' and 'to' parameters will be used as fixed end values
     *        and will not be adjusted
     * @return {Object} The calculated step and ends info; properties are:
     *     - from: The result start value, which may be lower than the original start value
     *     - to: The result end value, which may be higher than the original end value
     *     - power: The power of 10 used in the step calculation
     *     - step: The value size of each step
     *     - steps: The number of steps. NOTE: the steps may not divide the from/to range perfectly evenly;
     *              there may be a smaller distance between the last step and the end value than between prior
     *              steps, particularly when the `endsLocked` param is true. Therefore it is best to not use
     *              the `steps` result when finding the axis tick points, instead use the `step`, `to`, and
     *              `from` to find the correct point for each tick.
     */
    snapEnds: function (from, to, stepsMax, endsLocked) {
        var math = Math,
            pow = math.pow,
            floor = math.floor,

            // start with a precise step size
            step = (to - from) / stepsMax,

            // power is a power of 10 of the step. For axis 1, 2, 3 or 10, 20, 30 or
            // 0.1, 0.2, 0.3 power will be 0, 1 and -1 respectively.
            power = floor(math.log(step) / math.LN10) + 1,
            tenToPower = pow(10, power),

            // modulo will translate rounded value of the step to the 0 - 100 range. We will need it later.
            modulo = math.round((step % tenToPower) * pow(10, 2 - power)),

            // interval is an array of value/weight pairs
            interval = Ext.draw.Draw.snapEndsIntervalWeights,
            ln = interval.length,
            stepCount = 0,
            topWeight = 1e9,
            cur, value, weight, i, topValue;

        // round the start value by the power, so e.g. 0.532 will become 0.5.
        if (!endsLocked) {
            from = floor(from / tenToPower) * tenToPower;
        }
        cur = from;

        // find what is our step going to be to be closer to "pretty" numbers. This is done taking into
        // account the interval weights. This way we figure out topValue.
        for (i = 0; i < ln; i++) {
            value = interval[i][0];
            weight = (value - modulo) < 0 ? 1e6 : (value - modulo) / interval[i][1];
            if (weight < topWeight) {
                topValue = value;
                topWeight = weight;
            }
        }

        // with the new topValue, calculate the final step size
        step = floor(step * pow(10, -power)) * pow(10, power) + topValue * pow(10, power - 2);
        while (cur < to) {
            cur += step;
            stepCount++;
        }

        // Cut everything that is after tenth digit after floating point. This is to get rid of
        // rounding errors, i.e. 12.00000000000121212.
        if (!endsLocked) {
            to = +cur.toFixed(10);
        }

        return {
            from: from,
            to: to,
            power: power,
            step: step,
            steps: stepCount
        };
    },

    sorter: function (a, b) {
        return a.offset - b.offset;
    },

    rad: function(degrees) {
        return degrees % 360 * Math.PI / 180;
    },

    degrees: function(radian) {
        return radian * 180 / Math.PI % 360;
    },

    withinBox: function(x, y, bbox) {
        bbox = bbox || {};
        return (x >= bbox.x && x <= (bbox.x + bbox.width) && y >= bbox.y && y <= (bbox.y + bbox.height));
    },

    parseGradient: function(gradient) {
        var me = this,
            type = gradient.type || 'linear',
            angle = gradient.angle || 0,
            radian = me.radian,
            stops = gradient.stops,
            stopsArr = [],
            stop,
            vector,
            max,
            stopObj;

        if (type == 'linear') {
            vector = [0, 0, Math.cos(angle * radian), Math.sin(angle * radian)];
            max = 1 / (Math.max(Math.abs(vector[2]), Math.abs(vector[3])) || 1);
            vector[2] *= max;
            vector[3] *= max;
            if (vector[2] < 0) {
                vector[0] = -vector[2];
                vector[2] = 0;
            }
            if (vector[3] < 0) {
                vector[1] = -vector[3];
                vector[3] = 0;
            }
        }

        for (stop in stops) {
            if (stops.hasOwnProperty(stop) && me.stopsRE.test(stop)) {
                stopObj = {
                    offset: parseInt(stop, 10),
                    color: Ext.draw.Color.toHex(stops[stop].color) || '#ffffff',
                    opacity: stops[stop].opacity || 1
                };
                stopsArr.push(stopObj);
            }
        }
        // Sort by pct property
        stopsArr.sort(me.sorter);
        if (type == 'linear') {
            return {
                id: gradient.id,
                type: type,
                vector: vector,
                stops: stopsArr
            };
        }
        else {
            return {
                id: gradient.id,
                type: type,
                centerX: gradient.centerX,
                centerY: gradient.centerY,
                focalX: gradient.focalX,
                focalY: gradient.focalY,
                radius: gradient.radius,
                vector: vector,
                stops: stopsArr
            };
        }
    }
};

/**
 * @class Ext.draw.CompositeSprite
 * @extends Ext.util.MixedCollection
 *
 * A composite Sprite handles a group of sprites with common methods to a sprite
 * such as `hide`, `show`, `setAttributes`. These methods are applied to the set of sprites
 * added to the group.
 *
 * CompositeSprite extends {@link Ext.util.MixedCollection} so you can use the same methods
 * in `MixedCollection` to iterate through sprites, add and remove elements, etc.
 *
 * In order to create a CompositeSprite, one has to provide a handle to the surface where it is
 * rendered:
 *
 *     var group = Ext.create('Ext.draw.CompositeSprite', {
 *         surface: drawComponent.surface
 *     });
 *
 * Then just by using `MixedCollection` methods it's possible to add {@link Ext.draw.Sprite}s:
 *
 *     group.add(sprite1);
 *     group.add(sprite2);
 *     group.add(sprite3);
 *
 * And then apply common Sprite methods to them:
 *
 *     group.setAttributes({
 *         fill: '#f00'
 *     }, true);
 */
Ext.draw.CompositeSprite = Ext.extend(Ext.util.MixedCollection, {

    /* End Definitions */
    isCompositeSprite: true,
    constructor: function(config) {
        var me = this;

        config = config || {};
        Ext.apply(me, config);

        me.addEvents(
            'mousedown',
            'mouseup',
            'mouseover',
            'mouseout',
            'click'
        );
        me.id = Ext.id(null, 'ext-sprite-group-');
        Ext.draw.CompositeSprite.superclass.constructor.apply(this, arguments);
    },

    // @private
    onClick: function(e) {
        this.fireEvent('click', e);
    },

    // @private
    onMouseUp: function(e) {
        this.fireEvent('mouseup', e);
    },

    // @private
    onMouseDown: function(e) {
        this.fireEvent('mousedown', e);
    },

    // @private
    onMouseOver: function(e) {
        this.fireEvent('mouseover', e);
    },

    // @private
    onMouseOut: function(e) {
        this.fireEvent('mouseout', e);
    },

    attachEvents: function(o) {
        var me = this;

        o.on({
            scope: me,
            mousedown: me.onMouseDown,
            mouseup: me.onMouseUp,
            mouseover: me.onMouseOver,
            mouseout: me.onMouseOut,
            click: me.onClick
        });
    },

    /** Add a Sprite to the Group */
    add: function(key, o) {
        var result = Ext.draw.CompositeSprite.superclass.add.apply(this, arguments);
        this.attachEvents(result);
        return result;
    },

    insert: function(index, key, o) {
        return Ext.draw.CompositeSprite.superclass.insert.apply(this, arguments);
    },

    /** Remove a Sprite from the Group */
    remove: function(o) {
        var me = this;

        o.un({
            scope: me,
            mousedown: me.onMouseDown,
            mouseup: me.onMouseUp,
            mouseover: me.onMouseOver,
            mouseout: me.onMouseOut,
            click: me.onClick
        });
        Ext.draw.CompositeSprite.superclass.remove.apply(this, arguments);
    },

    /**
     * Returns the group bounding box.
     * Behaves like {@link Ext.draw.Sprite} getBBox method.
    */
    getBBox: function() {
        var i = 0,
            sprite,
            bb,
            items = this.items,
            len = this.length,
            infinity = Infinity,
            minX = infinity,
            maxHeight = -infinity,
            minY = infinity,
            maxWidth = -infinity;

        for (; i < len; i++) {
            sprite = items[i];
            bb = sprite.getBBox();
            minX = Math.min(minX, bb.x);
            minY = Math.min(minY, bb.y);
            maxHeight = Math.max(maxHeight, bb.height + bb.y);
            maxWidth = Math.max(maxWidth, bb.width + bb.x);
        }

        return {
            x: minX,
            y: minY,
            height: maxHeight - minY,
            width: maxWidth - minX
        };
    },

    /**
     *  Iterates through all sprites calling
     *  `setAttributes` on each one. For more information
     *  {@link Ext.draw.Sprite} provides a description of the
     *  attributes that can be set with this method.
     */
    setAttributes: function(attrs, redraw) {
        var i = 0,
            items = this.items,
            len = this.length;

        for (; i < len; i++) {
            items[i].setAttributes(attrs, redraw);
        }
        return this;
    },

    /**
     * Hides all sprites. If the first parameter of the method is true
     * then a redraw will be forced for each sprite.
     */
    hide: function(redraw) {
        var i = 0,
            items = this.items,
            len = this.length;

        for (; i < len; i++) {
            items[i].hide(redraw);
        }
        return this;
    },

    /**
     * Shows all sprites. If the first parameter of the method is true
     * then a redraw will be forced for each sprite.
     */
    show: function(redraw) {
        var i = 0,
            items = this.items,
            len = this.length;

        for (; i < len; i++) {
            items[i].show(redraw);
        }
        return this;
    },

    redraw: function() {
        var me = this,
            i = 0,
            items = me.items,
            surface = me.getSurface(),
            len = me.length;

        if (surface) {
            for (; i < len; i++) {
                surface.renderItem(items[i]);
            }
        }
        return me;
    },

    setStyle: function(obj) {
        var i = 0,
            items = this.items,
            len = this.length,
            item, el;

        for (; i < len; i++) {
            item = items[i];
            el = item.el;
            if (el) {
                el.setStyle(obj);
            }
        }
    },

    addCls: function(obj) {
        var i = 0,
            items = this.items,
            surface = this.getSurface(),
            len = this.length;

        if (surface) {
            for (; i < len; i++) {
                surface.addCls(items[i], obj);
            }
        }
    },

    removeCls: function(obj) {
        var i = 0,
            items = this.items,
            surface = this.getSurface(),
            len = this.length;

        if (surface) {
            for (; i < len; i++) {
                surface.removeCls(items[i], obj);
            }
        }
    },

    /**
     * Grab the surface from the items
     * @private
     * @return {Ext.draw.Surface} The surface, null if not found
     */
    getSurface: function(){
        var first = this.first();
        if (first) {
            return first.surface;
        }
        return null;
    },

    /**
     * Destroys the SpriteGroup
     */
    destroy: function(){
        var me = this,
            surface = me.getSurface(),
            item;

        if (surface) {
            while (me.getCount() > 0) {
                item = me.first();
                me.remove(item);
                surface.remove(item);
            }
        }
        me.clearListeners();
    }
});

Ext.applyIf(Ext.draw.CompositeSprite.prototype, Ext.util.Animate.prototype);
/**
 * @class Ext.draw.Sprite
 * @extends Object
 *
 * A Sprite is an object rendered in a Drawing surface. There are different options and types of sprites.
 * The configuration of a Sprite is an object with the following properties:
 *
 * - **type** - (String) The type of the sprite. Possible options are 'circle', 'path', 'rect', 'text', 'square', 'image'. 
 * - **width** - (Number) Used in rectangle sprites, the width of the rectangle.
 * - **height** - (Number) Used in rectangle sprites, the height of the rectangle.
 * - **size** - (Number) Used in square sprites, the dimension of the square.
 * - **radius** - (Number) Used in circle sprites, the radius of the circle.
 * - **x** - (Number) The position along the x-axis.
 * - **y** - (Number) The position along the y-axis.
 * - **path** - (Array) Used in path sprites, the path of the sprite written in SVG-like path syntax.
 * - **opacity** - (Number) The opacity of the sprite.
 * - **fill** - (String) The fill color.
 * - **stroke** - (String) The stroke color.
 * - **stroke-width** - (Number) The width of the stroke.
 * - **font** - (String) Used with text type sprites. The full font description. Uses the same syntax as the CSS `font` parameter.
 * - **text** - (String) Used with text type sprites. The text itself.
 * 
 * Additionally there are three transform objects that can be set with `setAttributes` which are `translate`, `rotate` and
 * `scale`.
 * 
 * For translate, the configuration object contains x and y attributes that indicate where to
 * translate the object. For example:
 * 
 *     sprite.setAttributes({
 *       translate: {
 *        x: 10,
 *        y: 10
 *       }
 *     }, true);
 * 
 * For rotation, the configuration object contains x and y attributes for the center of the rotation (which are optional),
 * and a `degrees` attribute that specifies the rotation in degrees. For example:
 * 
 *     sprite.setAttributes({
 *       rotate: {
 *        degrees: 90
 *       }
 *     }, true);
 * 
 * For scaling, the configuration object contains x and y attributes for the x-axis and y-axis scaling. For example:
 * 
 *     sprite.setAttributes({
 *       scale: {
 *        x: 10,
 *        y: 3
 *       }
 *     }, true);
 *
 * Sprites can be created with a reference to a {@link Ext.draw.Surface}
 *
 *      var drawComponent = Ext.create('Ext.draw.Component', options here...);
 *
 *      var sprite = Ext.create('Ext.draw.Sprite', {
 *          type: 'circle',
 *          fill: '#ff0',
 *          surface: drawComponent.surface,
 *          radius: 5
 *      });
 *
 * Sprites can also be added to the surface as a configuration object:
 *
 *      var sprite = drawComponent.surface.add({
 *          type: 'circle',
 *          fill: '#ff0',
 *          radius: 5
 *      });
 *
 * In order to properly apply properties and render the sprite we have to
 * `show` the sprite setting the option `redraw` to `true`:
 *
 *      sprite.show(true);
 *
 * The constructor configuration object of the Sprite can also be used and passed into the {@link Ext.draw.Surface}
 * add method to append a new sprite to the canvas. For example:
 *
 *     drawComponent.surface.add({
 *         type: 'circle',
 *         fill: '#ffc',
 *         radius: 100,
 *         x: 100,
 *         y: 100
 *     });
 */
Ext.draw.Sprite = Ext.extend(Ext.util.Observable, {

    dirty: false,
    dirtyHidden: false,
    dirtyTransform: false,
    dirtyPath: true,
    dirtyFont: true,
    zIndexDirty: true,
    isSprite: true,
    zIndex: 0,
    fontProperties: [
        'font',
        'font-size',
        'font-weight',
        'font-style',
        'font-family',
        'text-anchor',
        'text'
    ],
    pathProperties: [
        'x',
        'y',
        'd',
        'path',
        'height',
        'width',
        'radius',
        'r',
        'rx',
        'ry',
        'cx',
        'cy'
    ],

    minDefaults: {
        circle: {
            cx: 0,
            cy: 0,
            r: 0,
            fill: "none"
        },
        ellipse: {
            cx: 0,
            cy: 0,
            rx: 0,
            ry: 0,
            fill: "none"
        },
        rect: {
            x: 0,
            y: 0,
            width: 0,
            height: 0,
            rx: 0,
            ry: 0,
            fill: "none"
        },
        text: {
            x: 0,
            y: 0,
            "text-anchor": "start",
            fill: "#000"
        },
        path: {
            d: "M0,0",
            fill: "none"
        },
        image: {
            x: 0,
            y: 0,
            width: 0,
            height: 0,
            preserveAspectRatio: "none"
        }
    },

    constructor: function(config) {
        var me = this;
        config = config || {};
        me.id = Ext.id(null, 'ext-sprite-');
        me.transformations = [];
        me.surface = config.surface;
        me.group = config.group;
        me.type = config.type;
        //attribute bucket
        me.bbox = {};
        me.attr = {
            zIndex: 0,
            translation: {
                x: null,
                y: null
            },
            rotation: {
                degrees: null,
                x: null,
                y: null
            },
            scaling: {
                x: null,
                y: null,
                cx: null,
                cy: null
            }
        };
        //delete not bucket attributes
        delete config.surface;
        delete config.group;
        delete config.type;
        Ext.applyIf(config, me.minDefaults[me.type]);
        me.setAttributes(config);
        me.addEvents(
            'beforedestroy',
            'destroy',
            'render',
            'mousedown',
            'mouseup',
            'mouseover',
            'mouseout',
            'mousemove',
            'click',
            'rightclick',
            'mouseenter',
            'mouseleave',
            'touchstart',
            'touchmove',
            'touchend'
        );
        Ext.draw.Sprite.superclass.constructor.apply(this, arguments);
    },

    /**
     * Change the attributes of the sprite.
     * @param {Object} attrs attributes to be changed on the sprite.
     * @param {Boolean} redraw Flag to immediatly draw the change.
     * @return {Ext.draw.Sprite} this
     */
    setAttributes: function(attrs, redraw) {
        var me = this,
            fontProps = me.fontProperties,
            fontPropsLength = fontProps.length,
            pathProps = me.pathProperties,
            pathPropsLength = pathProps.length,
            hasSurface = !!me.surface,
            custom = hasSurface && me.surface.customAttributes || {},
            spriteAttrs = me.attr,
            attr, i, translate, translation, rotate, rotation, scale, scaling;

        attrs = Ext.apply({}, attrs);
        for (attr in custom) {
            if (attrs.hasOwnProperty(attr) && typeof custom[attr] == "function") {
                Ext.apply(attrs, custom[attr].apply(me, [].concat(attrs[attr])));
            }
        }

        // Flag a change in hidden
        if (!!attrs.hidden !== !!spriteAttrs.hidden) {
            me.dirtyHidden = true;
        }

        // Flag path change
        for (i = 0; i < pathPropsLength; i++) {
            attr = pathProps[i];
            if (attr in attrs && attrs[attr] !== spriteAttrs[attr]) {
                me.dirtyPath = true;
                break;
            }
        }

        // Flag zIndex change
        if ('zIndex' in attrs) {
            me.zIndexDirty = true;
        }

        // Flag font/text change
        for (i = 0; i < fontPropsLength; i++) {
            attr = fontProps[i];
            if (attr in attrs && attrs[attr] !== spriteAttrs[attr]) {
                me.dirtyFont = true;
                break;
            }
        }

        translate = attrs.translate;
        translation = spriteAttrs.translation;
        if (translate) {
            if ((translate.x && translate.x !== translation.x) ||
                (translate.y && translate.y !== translation.y)) {
                Ext.apply(translation, translate);
                me.dirtyTransform = true;
            }
            delete attrs.translate;
        }

        rotate = attrs.rotate;
        rotation = spriteAttrs.rotation;
        if (rotate) {
            if ((!rotate.x || rotate.x !== rotation.x) || 
                (!rotate.y || rotate.y !== rotation.y) ||
                (rotate.degrees && rotate.degrees !== rotation.degrees)) {
                Ext.apply(rotation, rotate);
                me.dirtyTransform = true;
            }
            delete attrs.rotate;
        }

        scale = attrs.scale;
        scaling = spriteAttrs.scaling;
        if (scale) {
            if ((scale.x && scale.x !== scaling.x) || 
                (scale.y && scale.y !== scaling.y) ||
                (scale.cx && scale.cx !== scaling.cx) ||
                (scale.cy && scale.cy !== scaling.cy)) {
                Ext.apply(scaling, scale);
                me.dirtyTransform = true;
            }
            delete attrs.scale;
        }

        Ext.apply(spriteAttrs, attrs);
        me.dirty = true;

        if (redraw === true && hasSurface) {
            me.redraw();
        }
        return this;
    },

    /**
     * Retrieve the bounding box of the sprite. This will be returned as an object with x, y, width, and height properties.
     * @return {Object} bbox
     */
    getBBox: function(isWithoutTransform) {
        return this.surface.getBBox(this, isWithoutTransform);
    },
    
    setText: function(text) {
        return this.surface.setText(this, text);
    },

    /**
     * Hide the sprite.
     * @param {Boolean} redraw Flag to immediatly draw the change.
     * @return {Ext.draw.Sprite} this
     */
    hide: function(redraw) {
        this.setAttributes({
            hidden: true
        }, redraw);
        return this;
    },

    /**
     * Show the sprite.
     * @param {Boolean} redraw Flag to immediatly draw the change.
     * @return {Ext.draw.Sprite} this
     */
    show: function(redraw) {
        this.setAttributes({
            hidden: false
        }, redraw);
        return this;
    },

    /**
     * Remove the sprite.
     */
    remove: function() {
        if (this.surface) {
            this.surface.remove(this);
            return true;
        }
        return false;
    },

    onRemove: function() {
        this.surface.onRemove(this);
    },

    /**
     * Removes the sprite and clears all listeners.
     */
    destroy: function() {
        var me = this;
        if (me.fireEvent('beforedestroy', me) !== false) {
            me.remove();
            me.surface.onDestroy(me);
            me.clearListeners();
            me.fireEvent('destroy');
        }
    },

    /**
     * Redraw the sprite.
     * @return {Ext.draw.Sprite} this
     */
    redraw: function() {
        this.surface.renderItem(this);
        return this;
    },

    /**
     * Draw a sprite Tween (animation interpolation).
     * @return {Ext.draw.Sprite} this
     */
    tween: function() {
        this.surface.tween(this);
        return this;
    },

    /**
     * Wrapper for setting style properties, also takes single object parameter of multiple styles.
     * @param {String/Object} property The style property to be set, or an object of multiple styles.
     * @param {String} value (optional) The value to apply to the given property, or null if an object was passed.
     * @return {Ext.draw.Sprite} this
     */
    setStyle: function() {
        this.el.setStyle.apply(this.el, arguments);
        return this;
    },

    /**
     * Adds one or more CSS classes to the element. Duplicate classes are automatically filtered out.  Note this method
     * is severly limited in VML.
     * @param {String/Array} className The CSS class to add, or an array of classes
     * @return {Ext.draw.Sprite} this
     */
    addCls: function(obj) {
        this.surface.addCls(this, obj);
        return this;
    },

    /**
     * Removes one or more CSS classes from the element.
     * @param {String/Array} className The CSS class to remove, or an array of classes.  Note this method
     * is severly limited in VML.
     * @return {Ext.draw.Sprite} this
     */
    removeCls: function(obj) {
        this.surface.removeCls(this, obj);
        return this;
    }
});

Ext.applyIf(Ext.draw.Sprite.prototype, Ext.util.Animate);

/**
 * @class Ext.draw.Matrix
 * @private
 */
Ext.draw.Matrix = Ext.extend(Object, {

    constructor: function(a, b, c, d, e, f) {
        if (a != null) {
            this.matrix = [[a, c, e], [b, d, f], [0, 0, 1]];
        }
        else {
            this.matrix = [[1, 0, 0], [0, 1, 0], [0, 0, 1]];
        }
    },

    add: function(a, b, c, d, e, f) {
        var me = this,
            out = [[], [], []],
            matrix = [[a, c, e], [b, d, f], [0, 0, 1]],
            x, y, z, res;

        for (x = 0; x < 3; x++) {
            for (y = 0; y < 3; y++) {
                res = 0;
                for (z = 0; z < 3; z++) {
                    res += me.matrix[x][z] * matrix[z][y];
                }
                out[x][y] = res;
            }
        }
        me.matrix = out;
        return me;
    },

    prepend: function(a, b, c, d, e, f) {
        var me = this,
            out = [[], [], []],
            matrix = [[a, c, e], [b, d, f], [0, 0, 1]],
            x, y, z, res;

        for (x = 0; x < 3; x++) {
            for (y = 0; y < 3; y++) {
                res = 0;
                for (z = 0; z < 3; z++) {
                    res += matrix[x][z] * me.matrix[z][y];
                }
                out[x][y] = res;
            }
        }
        me.matrix = out;
        return me;
    },

    invert: function() {
        var matrix = this.matrix,
            a = matrix[0][0],
            b = matrix[1][0],
            c = matrix[0][1],
            d = matrix[1][1],
            e = matrix[0][2],
            f = matrix[1][2],
            x = a * d - b * c;
        return new Ext.draw.Matrix(d / x, -b / x, -c / x, a / x, (c * f - d * e) / x, (b * e - a * f) / x);
    },

    clone: function() {
        var matrix = this.matrix;
        return new Ext.draw.Matrix(matrix[0][0], matrix[1][0], matrix[0][1], matrix[1][1], matrix[0][2], matrix[1][2]);
    },

    translate: function(x, y) {
        this.prepend(1, 0, 0, 1, x, y);
        return this;
    },

    scale: function(x, y, cx, cy) {
        var me = this;
        if (y == null) {
            y = x;
        }
        me.add(1, 0, 0, 1, cx, cy);
        me.add(x, 0, 0, y, 0, 0);
        me.add(1, 0, 0, 1, -cx, -cy);
        return me;
    },

    rotate: function(a, x, y) {
        a = Ext.draw.Draw.rad(a);
        var me = this,
            cos = +Math.cos(a).toFixed(9),
            sin = +Math.sin(a).toFixed(9);
        me.add(cos, sin, -sin, cos, x, y);
        me.add(1, 0, 0, 1, -x, -y);
        return me;
    },

    x: function(x, y) {
        var matrix = this.matrix;
        return x * matrix[0][0] + y * matrix[0][1] + matrix[0][2];
    },

    y: function(x, y) {
        var matrix = this.matrix;
        return x * matrix[1][0] + y * matrix[1][1] + matrix[1][2];
    },

    get: function(i, j) {
        return + this.matrix[i][j].toFixed(4);
    },

    /**
     * Determines whether this matrix is an identity matrix (no transform)
     * @return {Boolean}
     */
    isIdentity: function() {
        return this.equals(new Ext.draw.Matrix());
    },

    /**
     * Determines if this matrix has the same values as another matrix
     * @param {Ext.draw.Matrix} matrix
     * @return {Boolean}
     */
    equals: function(matrix) {
        var thisMatrix = this.matrix,
            otherMatrix = matrix.matrix;
        return thisMatrix[0][0] === otherMatrix[0][0] &&
               thisMatrix[0][1] === otherMatrix[0][1] &&
               thisMatrix[0][2] === otherMatrix[0][2] &&
               thisMatrix[1][0] === otherMatrix[1][0] &&
               thisMatrix[1][1] === otherMatrix[1][1] &&
               thisMatrix[1][2] === otherMatrix[1][2];
    },

    toString: function() {
        var me = this;
        return [me.get(0, 0), me.get(0, 1), me.get(1, 0), me.get(1, 1), 0, 0].join();
    },

    toCanvas: function(ctx) {
        var matrix = this.matrix;
        ctx.transform(matrix[0][0], matrix[1][0], matrix[0][1], matrix[1][1], matrix[0][2], matrix[1][2]);
    },

    toSvg: function() {
        var matrix = this.matrix;
        return "matrix(" + [matrix[0][0], matrix[1][0], matrix[0][1], matrix[1][1], matrix[0][2], matrix[1][2]].join() + ")";
    },

    toFilter: function() {
        var me = this;
        return "progid:DXImageTransform.Microsoft.Matrix(M11=" + me.get(0, 0) +
            ", M12=" + me.get(0, 1) + ", M21=" + me.get(1, 0) + ", M22=" + me.get(1, 1) +
            ", Dx=" + me.get(0, 2) + ", Dy=" + me.get(1, 2) + ")";
    },

    offset: function() {
        var matrix = this.matrix;
        return [matrix[0][2].toFixed(4), matrix[1][2].toFixed(4)];
    },

    // Split matrix into Translate Scale, Shear, and Rotate
    split: function () {
        function norm(a) {
            return a[0] * a[0] + a[1] * a[1];
        }
        function normalize(a) {
            var mag = Math.sqrt(norm(a));
            a[0] /= mag;
            a[1] /= mag;
        }
        var matrix = this.matrix,
            out = {
                translateX: matrix[0][2],
                translateY: matrix[1][2]
            },
            row;

        // scale and shear
        row = [[matrix[0][0], matrix[0][1]], [matrix[1][1], matrix[1][1]]];
        out.scaleX = Math.sqrt(norm(row[0]));
        normalize(row[0]);

        out.shear = row[0][0] * row[1][0] + row[0][1] * row[1][1];
        row[1] = [row[1][0] - row[0][0] * out.shear, row[1][1] - row[0][1] * out.shear];

        out.scaleY = Math.sqrt(norm(row[1]));
        normalize(row[1]);
        out.shear /= out.scaleY;

        // rotation
        out.rotate = Math.asin(-row[0][1]);

        out.isSimple = !+out.shear.toFixed(9) && (out.scaleX.toFixed(9) == out.scaleY.toFixed(9) || !out.rotate);

        return out;
    }
});
/**
 * @class Ext.draw.Surface
 * @extends Object
 *
 * A Surface is an interface to render methods inside a draw {@link Ext.draw.Component}.
 * A Surface contains methods to render sprites, get bounding boxes of sprites, add
 * sprites to the canvas, initialize other graphic components, etc. One of the most used
 * methods for this class is the `add` method, to add Sprites to the surface.
 *
 * Most of the Surface methods are abstract and they have a concrete implementation
 * in VML or SVG engines.
 *
 * A Surface instance can be accessed as a property of a draw component. For example:
 *
 *     drawComponent.surface.add({
 *         type: 'circle',
 *         fill: '#ffc',
 *         radius: 100,
 *         x: 100,
 *         y: 100
 *     });
 *
 * The configuration object passed in the `add` method is the same as described in the {@link Ext.draw.Sprite}
 * class documentation.
 *
 * ### Listeners
 *
 * You can also add event listeners to the surface using the `Observable` listener syntax. Supported events are:
 *
 * - 'mouseup'
 * - 'mousedown'
 * - 'mouseover'
 * - 'mouseout'
 * - 'mousemove'
 * - 'mouseenter'
 * - 'mouseleave'
 * - 'click'
 * - 'dblclick'
 * - 'tap'
 * - 'tapstart'
 * - 'tapend'
 * - 'tapcancel'
 * - 'taphold'
 * - 'doubletap'
 * - 'singletap'
 * - 'touchstart'
 * - 'touchmove'
 * - 'touchend'
 * - 'drag'
 * - 'dragstart'
 * - 'dragend'
 * - 'pinch'
 * - 'pinchstart'
 * - 'pinchend'
 * - 'swipe'
 *
 * For example:
 *
 *     drawComponent.surface.on({
 *        'mousemove': function() {
 *             console.log('moving the mouse over the surface');
 *         }
 *     });
 *
 * ## Example
 *
 *     drawComponent.surface.add([
 *         {
 *             type: 'circle',
 *             radius: 10,
 *             fill: '#f00',
 *             x: 10,
 *             y: 10,
 *             group: 'circles'
 *         },
 *         {
 *             type: 'circle',
 *             radius: 10,
 *             fill: '#0f0',
 *             x: 50,
 *             y: 50,
 *             group: 'circles'
 *         },
 *         {
 *             type: 'circle',
 *             radius: 10,
 *             fill: '#00f',
 *             x: 100,
 *             y: 100,
 *             group: 'circles'
 *         },
 *         {
 *             type: 'rect',
 *             radius: 10,
 *             x: 10,
 *             y: 10,
 *             group: 'rectangles'
 *         },
 *         {
 *             type: 'rect',
 *             radius: 10,
 *             x: 50,
 *             y: 50,
 *             group: 'rectangles'
 *         },
 *         {
 *             type: 'rect',
 *             radius: 10,
 *             x: 100,
 *             y: 100,
 *             group: 'rectangles'
 *         }
 *     ]);
 *
 *     // Get references to my groups
 *     my circles = surface.getGroup('circles');
 *     my rectangles = surface.getGroup('rectangles');
 *
 *     // Animate the circles down
 *     circles.animate({
 *         duration: 1000,
 *         translate: {
 *             y: 200
 *         }
 *     });
 *
 *     // Animate the rectangles across
 *     rectangles.animate({
 *         duration: 1000,
 *         translate: {
 *             x: 200
 *         }
 *     });
 */
// COMPAT set Ext.baseCSSPrefix
Ext.baseCSSPrefix = 'x-';

(function() {

function createProcessEventMethod(name) {
    return function(e) {
        this.processEvent(name, e);
    }
}

Ext.draw.Surface = Ext.extend(Ext.util.Observable, {
    // @private
    zoomX: 1,
    zoomY: 1,
    panX: 0,
    panY: 0,

    // @private
    availableAttrs: {
        blur: 0,
        "clip-rect": "0 0 1e9 1e9",
        cursor: "default",
        cx: 0,
        cy: 0,
        'dominant-baseline': 'auto',
        fill: "none",
        "fill-opacity": 1,
        font: '10px "Arial"',
        "font-family": '"Arial"',
        "font-size": "10",
        "font-style": "normal",
        "font-weight": 400,
        gradient: "",
        height: 0,
        hidden: false,
        href: "http://sencha.com/",
        opacity: 1,
        path: "M0,0",
        radius: 0,
        rx: 0,
        ry: 0,
        scale: "1 1",
        src: "",
        stroke: "#000",
        "stroke-dasharray": "",
        "stroke-linecap": "butt",
        "stroke-linejoin": "butt",
        "stroke-miterlimit": 0,
        "stroke-opacity": 1,
        "stroke-width": 1,
        target: "_blank",
        text: "",
        "text-anchor": "middle",
        title: "Ext Draw",
        width: 0,
        x: 0,
        y: 0,
        zIndex: 0
    },

 /**
  * @cfg {Number} height
  * The height of this component in pixels (defaults to auto).
  * <b>Note</b> to express this dimension as a percentage or offset see {@link Ext.Component#anchor}.
  */
 /**
  * @cfg {Number} width
  * The width of this component in pixels (defaults to auto).
  * <b>Note</b> to express this dimension as a percentage or offset see {@link Ext.Component#anchor}.
  */
    container: undefined,
    height: 352,
    width: 512,
    x: 0,
    y: 0,

    constructor: function(config) {
        var me = this;
        config = config || {};
        Ext.apply(me, config);

        me.domRef = Ext.getDoc().dom;

        me.customAttributes = {};

        me.addEvents.apply(me, Ext.draw.Surface.eventNames);

        Ext.draw.Surface.superclass.constructor.apply(me, arguments);

        me.getId();
        me.initGradients();
        me.initItems();
        if (me.renderTo) {
            me.render(me.renderTo);
            delete me.renderTo;
        }
        me.initBackground(config.background);
    },

    /**
     * @private initializes surface events. Should be called after render.
     */
    initializeEvents: function() {
        //NOTE: drag events have been moved to a deferred function.
      
        var me = this;
        me.mon(me.el, {
            scope: me,
            mouseover: me.onMouseOver,
            mouseout: me.onMouseOut,
            mouseenter: me.onMouseEnter,
            mouseleave: me.onMouseLeave,
            mousemove: me.onMouseMove,
            mouseup: me.onMouseUp,
            mousedown: me.onMouseDown,
            click: me.onClick,
            doubleclick: me.onDoubleClick,
            tap: me.onTap,
            tapstart: me.onTapStart,
            tapend: me.onTapEnd,
            tapcancel: me.onTapCancel,
            taphold: me.onTapHold,
            doubletap: me.onDoubleTap,
            singletap: me.onSingleTap,
            touchstart: me.onTouchStart,
            touchmove: me.onTouchMove,
            touchend: me.onTouchEnd,
            pinchstart: me.onPinchStart,
            pinch: me.onPinch,
            pinchend: me.onPinchEnd,
            swipe: me.onSwipe
        });
    },

    initializeDragEvents: function() {
        var me = this;

        if (me.dragEventsInitialized) {
            return;
        }

        me.dragEventsInitialized = true;

        me.mon(me.el, {
            scope: me,
            dragstart: me.onDragStart,
            drag: me.onDrag,
            dragend: me.onDragEnd
        });
    },

    // @private called to initialize components in the surface
    // this is dependent on the underlying implementation.
    initSurface: Ext.emptyFn,

    // @private called to setup the surface to render an item
    //this is dependent on the underlying implementation.
    renderItem: Ext.emptyFn,

    // @private
    renderItems: Ext.emptyFn,

    renderFrame: Ext.emptyFn,

    // @private
    setViewBox: Ext.emptyFn,

    // @private
    tween: Ext.emptyFn,

    /**
     * Adds one or more CSS classes to the element. Duplicate classes are automatically filtered out.
     *
     * For example:
     *
     *          drawComponent.surface.addCls(sprite, 'x-visible');
     *
     * @param {Object} sprite The sprite to add the class to.
     * @param {String/Array} className The CSS class to add, or an array of classes
     */
    addCls: Ext.emptyFn,

    /**
     * Removes one or more CSS classes from the element.
     *
     * For example:
     *
     *      drawComponent.surface.removeCls(sprite, 'x-visible');
     *
     * @param {Object} sprite The sprite to remove the class from.
     * @param {String/Array} className The CSS class to remove, or an array of classes
     */
    removeCls: Ext.emptyFn,

    /**
     * Sets CSS style attributes to an element.
     *
     * For example:
     *
     *      drawComponent.surface.setStyle(sprite, {
     *          'cursor': 'pointer'
     *      });
     *
     * @param {Object} sprite The sprite to add, or an array of classes to
     * @param {Object} styles An Object with CSS styles.
     */
    setStyle: Ext.emptyFn,

    // @private
    createWrapEl: function(container) {
        return Ext.fly(container).createChild({tag: 'div', cls: Ext.baseCSSPrefix + 'surface-wrap', style: 'overflow:hidden'});
    },

    // @private
    initGradients: function() {
        var gradients = this.gradients;
        if (gradients) {
            Ext.each(gradients, this.addGradient, this);
        }
    },

    // @private
    initItems: function() {
        var me = this,
            items = me.items;
        me.items = new Ext.draw.CompositeSprite();
        me.groups = new Ext.draw.CompositeSprite();
        if (items) {
            me.add(items);
        }
    },

    // @private
    initBackground: function(config) {
        var me = this,
            gradientId,
            gradient,
            width = me.width,
            height = me.height;
        if (config) {
            if (config.gradient) {
                gradient = config.gradient;
                gradientId = gradient.id;
                me.addGradient(gradient);
                me.background = me.add({
                    type: 'rect',
                    x: 0,
                    y: 0,
                    width: width,
                    height: height,
                    fill: 'url(#' + gradientId + ')',
                    zIndex: -100
                });
            } else if (config.fill) {
                me.background = me.add({
                    type: 'rect',
                    x: 0,
                    y: 0,
                    width: width,
                    height: height,
                    fill: config.fill,
                    zIndex: -100
                });
            } else if (config.image) {
                me.background = me.add({
                    type: 'image',
                    x: 0,
                    y: 0,
                    width: width,
                    height: height,
                    src: config.image,
                    zIndex: -100
                });
            }
        }
    },

    /**
     * Sets the size of the surface. Accomodates the background (if any) to fit the new size too.
     *
     * For example:
     *
     *      drawComponent.surface.setSize(500, 500);
     *
     * This method is generally called when also setting the size of the draw Component.
     *
     * @param {Number} w The new width of the canvas.
     * @param {Number} h The new height of the canvas.
     */
    setSize: function(w, h) {
        if (this.background) {
            this.background.setAttributes({
                width: w,
                height: h,
                hidden: false
            }, true);
        }
        this.width = w;
        this.height = h;
        this.updateSurfaceElBox();
    },

    // @private
    scrubAttrs: function(sprite) {
        var me = this,
            attrs = {},
            exclude = {},
            sattr = sprite.attr,
            i;
        for (i in sattr) {
            // Narrow down attributes to the main set
            if (me.translateAttrs.hasOwnProperty(i)) {
                // Translated attr
                attrs[me.translateAttrs[i]] = sattr[i];
                exclude[me.translateAttrs[i]] = true;
            }
            else if (me.availableAttrs.hasOwnProperty(i) && !exclude[i]) {
                // Passtrhough attr
                attrs[i] = sattr[i];
            }
        }
        return attrs;
    },

    onMouseMove: createProcessEventMethod('mousemove'),
    onMouseOver: createProcessEventMethod('mouseover'),
    onMouseOut: createProcessEventMethod('mouseout'),
    onMouseEnter: createProcessEventMethod('mouseenter'),
    onMouseLeave: createProcessEventMethod('mouseleave'),
    onMouseUp: createProcessEventMethod('mouseup'),
    onMouseDown: createProcessEventMethod('mousedown'),
    onClick: createProcessEventMethod('click'),
    onDoubleClick: createProcessEventMethod('doubleclick'),
    onTap: createProcessEventMethod('tap'),
    onTapStart: createProcessEventMethod('tapstart'),
    onTapEnd: createProcessEventMethod('tapend'),
    onTapCancel: createProcessEventMethod('tapcancel'),
    onTapHold: createProcessEventMethod('taphold'),
    onDoubleTap: createProcessEventMethod('doubletap'),
    onSingleTap: createProcessEventMethod('singletap'),
    onTouchStart: createProcessEventMethod('touchstart'),
    onTouchMove: createProcessEventMethod('touchmove'),
    onTouchEnd: createProcessEventMethod('touchend'),
    onDragStart: createProcessEventMethod('dragstart'),
    onDrag: createProcessEventMethod('drag'),
    onDragEnd: createProcessEventMethod('dragend'),
    onPinchStart: createProcessEventMethod('pinchstart'),
    onPinch: createProcessEventMethod('pinch'),
    onPinchEnd: createProcessEventMethod('pinchend'),
    onSwipe: createProcessEventMethod('swipe'),

    // @private - Normalize a delegated single event from the main container to each sprite and sprite group
    processEvent: function(name, e) {
        var me = this,
            sprite = me.getSpriteForEvent(e);
        if (sprite) {
            sprite.fireEvent(name, sprite, e);
        }
        me.fireEvent(name, e);
    },

    /**
     * @protected - For a given event, find the Sprite corresponding to it if any.
     * @return {Ext.draw.Sprite} The sprite instance, or null if none found.
     */
    getSpriteForEvent: function(e) {
        return null;
    },

    /**
     * Add a gradient definition to the Surface. Note that in some surface engines, adding
     * a gradient via this method will not take effect if the surface has already been rendered.
     * Therefore, it is preferred to pass the gradients as an item to the surface config, rather
     * than calling this method, especially if the surface is rendered immediately (e.g. due to
     * 'renderTo' in its config). For more information on how to create gradients in the Chart
     * configuration object please refer to {@link Ext.chart.Chart}.
     *
     * The gradient object to be passed into this method is composed by:
     *
     *
     *  - **id** - string - The unique name of the gradient.
     *  - **angle** - number, optional - The angle of the gradient in degrees.
     *  - **stops** - object - An object with numbers as keys (from 0 to 100) and style objects as values.
     *
     *
     For example:
                drawComponent.surface.addGradient({
                    id: 'gradientId',
                    angle: 45,
                    stops: {
                        0: {
                            color: '#555'
                        },
                        100: {
                            color: '#ddd'
                        }
                    }
                });
     */
    addGradient: Ext.emptyFn,

    /**
     * Add a Sprite to the surface. See {@link Ext.draw.Sprite} for the configuration object to be passed into this method.
     *
     * For example:
     *
     *     drawComponent.surface.add({
     *         type: 'circle',
     *         fill: '#ffc',
     *         radius: 100,
     *         x: 100,
     *         y: 100
     *     });
     *
    */
    add: function() {
        var me = this,
            args = Array.prototype.slice.call(arguments),
            hasMultipleArgs = args.length > 1,
            sprite, items, i, ln, item, results;

        if (hasMultipleArgs || Ext.isArray(args[0])) {
            items = hasMultipleArgs ? args : args[0];
            results = [];

            for (i = 0, ln = items.length; i < ln; i++) {
                item = items[i];
                item = me.add(item);
                results.push(item);
            }

            return results;
        }
        sprite = me.prepareItems(args[0], true)[0];
        me.normalizeSpriteCollection(sprite);
        me.onAdd(sprite);
        return sprite;
    },

    /**
     * @private
     * Insert or move a given sprite into the correct position in the items
     * MixedCollection, according to its zIndex. Will be inserted at the end of
     * an existing series of sprites with the same or lower zIndex. If the sprite
     * is already positioned within an appropriate zIndex group, it will not be moved.
     * This ordering can be used by subclasses to assist in rendering the sprites in
     * the correct order for proper z-index stacking.
     * @param {Ext.draw.Sprite} sprite
     * @return {Number} the sprite's new index in the list
     */
    normalizeSpriteCollection: function(sprite) {
        var items = this.items,
            zIndex = sprite.attr.zIndex,
            idx = items.indexOf(sprite);

        if (idx < 0 || (idx > 0 && items.getAt(idx - 1).attr.zIndex > zIndex) ||
                (idx < items.length - 1 && items.getAt(idx + 1).attr.zIndex < zIndex)) {
            items.removeAt(idx);
            idx = items.findIndexBy(function(otherSprite) {
                return otherSprite.attr.zIndex > zIndex;
            });
            if (idx < 0) {
                idx = items.length;
            }
            items.insert(idx, sprite);
        }
        return idx;
    },

    onAdd: function(sprite) {
        var group = sprite.group,
            draggable = sprite.draggable,
            groups, ln, i;
        if (group) {
            groups = [].concat(group);
            ln = groups.length;
            for (i = 0; i < ln; i++) {
                group = groups[i];
                this.getGroup(group).add(sprite);
            }
            delete sprite.group;
        }
        if (draggable) {
            sprite.initDraggable();
        }
    },

    /**
     * Remove a given sprite from the surface, optionally destroying the sprite in the process.
     * You can also call the sprite own `remove` method.
     *
     * For example:
     *
     *      drawComponent.surface.remove(sprite);
     *      //or...
     *      sprite.remove();
     *
     * @param {Ext.draw.Sprite} sprite
     * @param {Boolean} destroySprite
     * @return {Number} the sprite's new index in the list
     */
    remove: function(sprite, destroySprite) {
        if (sprite) {
            this.items.remove(sprite);
            this.groups.each(function(item) {
                item.remove(sprite);
            });
            sprite.onRemove();
            if (destroySprite === true) {
                sprite.destroy();
            }
        }
    },

    /**
     * Remove all sprites from the surface, optionally destroying the sprites in the process.
     *
     * For example:
     *
     *      drawComponent.surface.removeAll();
     *
     * @param {Boolean} destroySprites Whether to destroy all sprites when removing them.
     * @return {Number} The sprite's new index in the list.
     */
    removeAll: function(destroySprites) {
        var items = this.items.items,
            ln = items.length,
            i;
        for (i = ln - 1; i > -1; i--) {
            this.remove(items[i], destroySprites);
        }
    },

    onRemove: Ext.emptyFn,

    onDestroy: Ext.emptyFn,

    // @private
    applyTransformations: function(sprite) {
        sprite.bbox.transform = 0;
        sprite.dirtyTransform = false;

        var me = this,
            dirty = false,
            attr = sprite.attr;

        if (attr.translation.x != null || attr.translation.y != null) {
            me.translate(sprite);
            dirty = true;
        }
        if (attr.scaling.x != null || attr.scaling.y != null) {
            me.scale(sprite);
            dirty = true;
        }
        if (attr.rotation.degrees != null) {
            me.rotate(sprite);
            dirty = true;
        }
        if (dirty) {
            sprite.bbox.transform = 0;
            me.transform(sprite);
            sprite.transformations = [];
        }
    },

    // @private
    rotate: function (sprite) {
        var bbox,
            deg = sprite.attr.rotation.degrees,
            centerX = sprite.attr.rotation.x,
            centerY = sprite.attr.rotation.y,
            trans = sprite.attr.translation,
            dx = trans && trans.x || 0,
            dy = trans && trans.y || 0;
        if (!Ext.isNumber(centerX) || !Ext.isNumber(centerY)) {
            bbox = this.getBBox(sprite, true); //isWithoutTransform=true
            centerX = !Ext.isNumber(centerX) ? (bbox.x + dx) + bbox.width / 2 : centerX;
            centerY = !Ext.isNumber(centerY) ? (bbox.y + dy) + bbox.height / 2 : centerY;
        }
        sprite.transformations.push({
            type: "rotate",
            degrees: deg,
            x: centerX,
            y: centerY
        });
    },

    // @private
    translate: function(sprite) {
        var x = sprite.attr.translation.x || 0,
            y = sprite.attr.translation.y || 0;
        sprite.transformations.push({
            type: "translate",
            x: x,
            y: y
        });
    },

    // @private
    scale: function(sprite) {
        var bbox,
            x = sprite.attr.scaling.x || 1,
            y = sprite.attr.scaling.y || 1,
            centerX = sprite.attr.scaling.centerX,
            centerY = sprite.attr.scaling.centerY;

        if (!Ext.isNumber(centerX) || !Ext.isNumber(centerY)) {
            bbox = this.getBBox(sprite);
            centerX = !Ext.isNumber(centerX) ? bbox.x + bbox.width / 2 : centerX;
            centerY = !Ext.isNumber(centerY) ? bbox.y + bbox.height / 2 : centerY;
        }
        sprite.transformations.push({
            type: "scale",
            x: x,
            y: y,
            centerX: centerX,
            centerY: centerY
        });
    },

    // @private
    rectPath: function (x, y, w, h, r) {
        if (r) {
            return [["M", x + r, y], ["l", w - r * 2, 0], ["a", r, r, 0, 0, 1, r, r], ["l", 0, h - r * 2], ["a", r, r, 0, 0, 1, -r, r], ["l", r * 2 - w, 0], ["a", r, r, 0, 0, 1, -r, -r], ["l", 0, r * 2 - h], ["a", r, r, 0, 0, 1, r, -r], ["z"]];
        }
        return [["M", x, y], ["l", w, 0], ["l", 0, h], ["l", -w, 0], ["z"]];
    },

    // @private
    ellipsePath: function (x, y, rx, ry) {
        if (ry == null) {
            ry = rx;
        }
        return [["M", x, y], ["m", 0, -ry], ["a", rx, ry, 0, 1, 1, 0, 2 * ry], ["a", rx, ry, 0, 1, 1, 0, -2 * ry], ["z"]];
    },

    // @private
    getPathpath: function (el) {
        return el.attr.path;
    },

    // @private
    getPathcircle: function (el) {
        var a = el.attr;
        return this.ellipsePath(a.x, a.y, a.radius, a.radius);
    },

    // @private
    getPathellipse: function (el) {
        var a = el.attr;
        return this.ellipsePath(a.x, a.y, a.radiusX, a.radiusY);
    },

    // @private
    getPathrect: function (el) {
        var a = el.attr;
        return this.rectPath(a.x, a.y, a.width, a.height, a.r);
    },

    // @private
    getPathimage: function (el) {
        var a = el.attr;
        return this.rectPath(a.x || 0, a.y || 0, a.width, a.height);
    },

    // @private
    getPathtext: function (el) {
        var bbox = this.getBBoxText(el);
        return this.rectPath(bbox.x, bbox.y, bbox.width, bbox.height);
    },

    createGroup: function(id) {
        var group = this.groups.get(id);
        if (!group) {
            group = new Ext.draw.CompositeSprite({
                surface: this
            });
            group.id = id || Ext.id(null, 'ext-surface-group-');
            this.groups.add(group);
        }
        return group;
    },

    /**
     * Returns a new group or an existent group associated with the current surface.
     * The group returned is a {@link Ext.draw.CompositeSprite} group.
     *
     * For example:
     *
     *      var spriteGroup = drawComponent.surface.getGroup('someGroupId');
     *
     * @param {String} id The unique identifier of the group.
     * @return {Object} The {@link Ext.draw.CompositeSprite}.
     */
    getGroup: function(id) {
        if (typeof id == "string") {
            var group = this.groups.get(id);
            if (!group) {
                group = this.createGroup(id);
            }
        } else {
            group = id;
        }
        return group;
    },

    // @private
    prepareItems: function(items, applyDefaults) {
        items = [].concat(items);
        // Make sure defaults are applied and item is initialized
        var item, i, ln;
        for (i = 0, ln = items.length; i < ln; i++) {
            item = items[i];
            if (!(item instanceof Ext.draw.Sprite)) {
                // Temporary, just take in configs...
                item.surface = this;
                items[i] = this.createItem(item);
            } else {
                item.surface = this;
            }
        }
        return items;
    },

    /**
     * Changes the text in the sprite element. The sprite must be a `text` sprite.
     * This method can also be called from {@link Ext.draw.Sprite}.
     *
     * For example:
     *
     *      var spriteGroup = drawComponent.surface.setText(sprite, 'my new text');
     *
     * @param {Object} sprite The Sprite to change the text.
     * @param {String} text The new text to be set.
     */
    setText: Ext.emptyFn,

    //@private Creates an item and appends it to the surface. Called
    //as an internal method when calling `add`.
    createItem: Ext.emptyFn,

    /**
     * Retrieves the id of this component.
     * Will autogenerate an id if one has not already been set.
     */
    getId: function() {
        return this.id || (this.id = Ext.id(null, 'ext-surface-'));
    },

    /**
     * Destroys the surface. This is done by removing all components from it and
     * also removing its reference to a DOM element.
     *
     * For example:
     *
     *      drawComponent.surface.destroy();
     */
    destroy: function() {
        delete this.domRef;
        this.removeAll();
    },

    //Empty the surface (without destroying it)
    clear: Ext.emtpyFn,


    /**
     * @private update the position/size/clipping of the series surface to match the current
     * chartBBox and the stored zoom/pan properties.
     */
    updateSurfaceElBox: function() {
        var me = this,
            floor = Math.floor,
            width = floor(me.width * me.zoomX),
            height = floor(me.height * me.zoomY),
            panX = me.panX,
            panY = me.panY,
            maxWidth = 2000,
            maxHeight = 1500,
            surfaceEl = me.surfaceEl,
            surfaceDom = surfaceEl.dom,
            surfaceWidth = me.width,
            surfaceHeight = me.height,
            ctx = me.surfaceEl.dom.getContext('2d'),
            setTranslation = false,
            newWidth, newHeight,
            diffX, diffY;

        if (width * height > maxWidth * maxHeight) {

            setTranslation = true;

            //maintain aspect ratio.
            newHeight = height * maxWidth / width;

            if (newHeight > height) {
                newHeight = maxHeight;
            }

            newWidth = width * newHeight / height;

            panX = (surfaceWidth - newWidth) / 2;
            panY = (surfaceHeight - newHeight) / 2;

            diffX = me.panX - panX;
            diffY = me.panY - panY;

            width = newWidth;
            height = newHeight;
        }

        // adjust the surfaceEl to match current zoom/pan; only if the size is changing to prevent
        // the canvas from getting cleared as happens when width/height are set.
        if (surfaceDom.width != width || surfaceDom.height != height) {
            surfaceEl.setSize(width, height);
            surfaceDom.width = width;
            surfaceDom.height = height;

            //TODO(nico): this is canvas specific.
            //this with the pixel check should be moved to
            //Canvas.js.
            if (setTranslation) {
                ctx.translate(diffX, diffY);
            }
        }

        surfaceEl.setTopLeft(panY, panX);
    },

    /**
     * Sets the persistent transform and updates the surfaceEl's size and position to match.
     * @param {Number} panX
     * @param {Number} panY
     * @param {Number} zoomX
     * @param {Number} zoomY
     */
    setSurfaceTransform: function(panX, panY, zoomX, zoomY) {
        var me = this;
        me.panX = panX;
        me.panY = panY;
        me.zoomX = zoomX;
        me.zoomY = zoomY;
        me.setSurfaceFastTransform(null);
        me.updateSurfaceElBox();
    },

    /**
     * Sets a fast CSS3 transform on the surfaceEl.
     * @param {Ext.draw.Matrix} matrix
     */
    setSurfaceFastTransform: function(matrix) {
        this.transformMatrix = matrix;
        this.surfaceEl.setStyle({
            webkitTransformOrigin: '0 0',
            webkitTransform: matrix ? matrix.toSvg() : ''
        });
    }

});
})();

/**
 * Create and return a new concrete Surface instance appropriate for the current environment.
 * @param {Object} config Initial configuration for the Surface instance
 * @param {Array} enginePriority Optional order of implementations to use; the first one that is
 *                available in the current environment will be used. Defaults to
 *                <code>['Svg', 'Vml']</code>.
 */
Ext.draw.Surface.create = function(config, enginePriority) {
    return new Ext.draw.engine.Canvas(config);
    enginePriority = enginePriority || ['Canvas', 'Svg'];

    var i = 0,
        len = enginePriority.length;

    for (; i < len; i++) {
        //if (Ext.supports[enginePriority[i]]) {
            //return new Ext.draw.engine[enginePriority[i]](config);
            //return new Ext.draw.engine.Svg(config);
        //}
    }
    return false;
};

/**
 * A list of all event names that should be relayed by a Surface object from its inner surfaceEl.
 */
Ext.draw.Surface.eventNames = [
    'mouseup',
    'mousedown',
    'mouseover',
    'mouseout',
    'mousemove',
    'mouseenter',
    'mouseleave',
    'click',
    'dblclick',
    'tap',
    'tapstart',
    'tapend',
    'tapcancel',
    'taphold',
    'doubletap',
    'singletap',
    'touchstart',
    'touchmove',
    'touchend',
    'drag',
    'dragstart',
    'dragend',
    'pinch',
    'pinchstart',
    'pinchend',
    'swipe'
];

/**
 * @class Ext.draw.Canvas
 * @extends Ext.draw.Surface
 *<p>Provides specific methods to draw with Canvas.</p>
 */

Ext.draw.engine.Canvas = Ext.extend(Ext.draw.Surface, {

    //read only style attribute canvas property mapping.
    attributeMap: {
        rotate: "rotation",
        stroke: "strokeStyle",
        fill: "fillStyle",
        lineWidth: "lineWidth",
        "text-anchor": "textAlign",
        "stroke-width": "lineWidth",
        "stroke-linecap": "lineCap",
        "stroke-linejoin": "lineJoin",
        "stroke-miterlimit": "miterLimit",
        opacity: "globalAlpha",
        font: 'font',
        shadowColor: "shadowColor",
        shadowOffsetX: "shadowOffsetX",
        shadowOffsetY: "shadowOffsetY",
        shadowBlur: "shadowBlur"
    },

    //read only default canvas property value map.
    attributeDefaults: {
        strokeStyle: "rgba(0, 0, 0, 0)",
        fillStyle: "rgba(0, 0, 0, 0)",
        lineWidth: 1,
        lineCap: "square",
        lineJoin: "miter",
        miterLimit: 1,
        shadowColor: "none",
        shadowOffsetX: 0,
        shadowOffsetY: 0,
        shadowBlur: 0,
        font: "10px Helvetica, sans-serif",
        textAlign: "start",
        globalAlpha: 1
    },

    gradientRe: /\s*url\s*\(#([^\)]+)\)\s*/,

    //read-only map of value convertions
    //used to convert a gradient id string into a gradient object
    //in a generic way
    attributeParsers: {
        fillStyle: function(value, sprite, me) {
            if (!value) {
                return value;
            }
            //is a gradient object
            if (Ext.isObject(value)) {
                me.addGradient(value);
                value = 'url(#' + value.id + ')';
            }
            var id = value.match(me.gradientRe);
            if (id) {
                return me.createGradient(me._gradients[id[1]], sprite);
            } else {
                return value == 'none'? 'rgba(0, 0, 0, 0)' : value;
            }
        },
        strokeStyle: function(value, sprite, me) {
            if (!value) {
                return value;
            }
            //is a gradient object
            if (Ext.isObject(value)) {
                me.addGradient(value);
                value = 'url(#' + value.id + ')';
            }
            var id = value.match(me.gradientRe);
            if (id) {
                return me.createGradient(me._gradients[id[1]], sprite);
            } else {
                return value == 'none'? 'rgba(0, 0, 0, 0)' : value;
            }
        },
        textAlign: function(value, sprite) {
            if (value === 'middle') {
                return 'center';
            }
            return value;
        }
    },

    constructor: function(config) {
        var me = this;
        //whether to add an event system to the canvas or not
        me.initEvents = 'initEvents' in config ? config.initEvents : true;
        //store a hash of gradient configurations
        me._gradients = {};
        Ext.draw.engine.Canvas.superclass.constructor.apply(this, arguments);
        me.initCanvas(config.renderTo);

        // Redraw after each animation frame event
        Ext.fx.Manager.addListener('frameend', function() {
            // Only render a frame on frameend if we were changed via tween
            if (me.animatedFrame) {
                me.animatedFrame = false;
                me.renderFrame();
            }
        });

        //disable context menu
        //TODO(nico): This should be configurable.
        this.canvas.oncontextmenu = function() { return false; };
    },

    //initializes the only canvas instance to draw the shapes to.
    initCanvas: function(container) {
        if (this.ctx) {
            return;
        }

        var me = this,
            domContainer = Ext.get(container),
            width = domContainer.getWidth(),
            height = domContainer.getHeight(),
            div = me.createWrapEl(container),
            canvas = document.createElement('canvas'),
            ctx = canvas.getContext('2d');

        div.setSize(width, height);
        //add an id to the dom div element.
        div.dom.id = me.id + '-wrap';
        canvas.id = me.id + '-canvas';
        canvas.width = width;
        canvas.height = height;

        div.appendChild(canvas);

        me.el = div;
        me.surfaceEl = Ext.get(canvas);
        me.canvas = canvas;
        me.ctx = ctx;

        //Add event manager for canvas class
        if (me.initEvents) {
            me.initializeEvents();
        }

    },

    getSpriteForEvent: function() {
        return null; //TODO!!!
    },

    //stores the gradient configuration into a hashmap
    addGradient: function(gradient) {
        var me = this;
        gradient = Ext.draw.Draw.parseGradient(gradient);
        me._gradients[gradient.id] = gradient;
    },

    //applies the current transformations to the element's matrix
    //TODO(nico): similar to what's found in Svg engine
    transform: function(sprite) {
        var matrix = new Ext.draw.Matrix,
            transforms = sprite.transformations,
            transformsLength = transforms.length,
            i = 0,
            transform, type;

        for (; i < transformsLength; i++) {
            transform = transforms[i];
            type = transform.type;
            if (type == "translate") {
                matrix.translate(transform.x, transform.y);
            }
            else if (type == "rotate") {
                matrix.rotate(transform.degrees, transform.x, transform.y);
            }
            else if (type == "scale") {
                matrix.scale(transform.x, transform.y, transform.centerX, transform.centerY);
            }
        }
        sprite.matrix = matrix;
    },

    setSize: function(w, h) {
        var width, height,
            me = this,
            canvas = me.canvas;
        if (typeof w == 'object') {
            width = w.width;
            height = w.height;
        } else {
            width = w;
            height = h;
        }
        if (width !== canvas.width || height !== canvas.height) {
            me.el.setSize(width, height);
            me.surfaceEl.setSize(width, height);
            canvas.width = width;
            canvas.height = height;

            me.width = width;
            me.height = height;
        }

        Ext.draw.engine.Canvas.superclass.setSize.call(this, w, h);
    },

    tween: function() {
        this.animatedFrame = true;
        Ext.draw.engine.Canvas.superclass.tween.apply(this);
    },

    //Rendering
    renderFrame: function() {
        this.render();
    },

    render: function(container) {
        var me = this;
        if (!me.canvas) {
            me.initCanvas(container);
        }
        me.renderAll();
    },

    createItem: function (config) {
        var sprite = new Ext.draw.Sprite(config);
        sprite.surface = this;
        sprite.matrix = new Ext.draw.Matrix;
        sprite.bbox = {
            plain: 0,
            transform: 0
        };
        return sprite;
    },

    // @private
    //TODO(nico): should sort also by abstract concept: "priority"
    zIndexSort: function(a, b) {
        var aAttr = a.attr,
            bAttr = b.attr,
            aIndex = aAttr && aAttr.zIndex || -1,
            bIndex = bAttr && bAttr.zIndex || -1,
            val = aIndex - bIndex;
        if (!val) {
            return (a.id > b.id) ? 1 :  -1;
        }
        else {
            return val;
        }
    },

    renderAll: function() {
        var me = this;
        me.clear();
        //sort by zIndex
        me.items.items.sort(me.zIndexSort);
        me.items.each(me.renderSprite, me);
    },

    renderSprite: function (sprite) {
        // Clear dirty flags that aren't used by the Canvas renderer
        sprite.dirtyHidden = sprite.dirtyPath = sprite.zIndexDirty = sprite.dirtyFont = sprite.dirty = false;

        if (sprite.attr.hidden) {
            return;
        }
        if (!sprite.matrix) {
            sprite.matrix = new Ext.draw.Matrix();
        }
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            attributeMap = me.attributeMap,
            attributeDefaults = me.attributeDefaults,
            attributeParsers = me.attributeParsers,
            prop, val, propertyValue;

        if (sprite.dirtyTransform) {
            me.applyTransformations(sprite);
        }
        ctx.save();

        //set matrix state
        sprite.matrix.toCanvas(ctx);

        //set styles
        for (prop in attributeMap) {
            val = attributeMap[prop];
            if (val in attributeParsers) {
                propertyValue = attributeParsers[val](attr[prop], sprite, me);
                if (propertyValue !== undefined) {
                    ctx[val] = propertyValue;
                } else {
                    ctx[val] = attributeDefaults[val];
                }
            }
            else {
                propertyValue = attr[prop];
                if (propertyValue !== undefined) {
                    ctx[val] = propertyValue;
                } else {
                    ctx[val] = attributeDefaults[val];
                }
            }
        }

        //render shape
        me[sprite.type + 'Render'](sprite);
        ctx.restore();
    },

    circleRender: function(sprite) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            x = +(attr.x || 0),
            y = +(attr.y || 0),
            radius = attr.radius,
            pi2 = Ext.draw.Draw.pi2;

        //draw fill circle
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, pi2, true);
		ctx.closePath();
        ctx.fill();

        //draw stroke circle
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, pi2, true);
		ctx.closePath();
        ctx.stroke();
    },

    ellipseRender: function(sprite) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            width = attr.width,
            height = attr.height,
            x = +(attr.x || 0),
            y = +(attr.y || 0),
            scaleX = 1,
            scaleY = 1,
            scalePosX = 1,
            scalePosY = 1,
            radius = 0,
            pi2 = Ext.draw.Draw.pi2;

        if (width > height) {
            radius = width / 2;
            scaleY = height / width;
            scalePosY = width / height;
        }
        else {
            radius = height / 2;
            scaleX = width / height;
            scalePosX = height / width;
        }
        ctx.scale(scaleX, scaleY);

        //make fill ellipse
        ctx.beginPath();
        ctx.arc(x * scalePosX, y * scalePosY, radius, 0, pi2, true);
        ctx.closePath();
        ctx.fill();

        //make stroke ellipse
        ctx.beginPath();
        ctx.arc(x * scalePosX, y * scalePosY, radius, 0, pi2, true);
        ctx.closePath();
        ctx.stroke();
    },

    imageRender: function(sprite) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            width = attr.width,
            height = attr.height,

            x = +(attr.x || 0),
            y = +(attr.y || 0),
            src = attr.src,
            img;

        if (sprite._img) {
            img = sprite._img;
        }
        else {
            sprite._img = img = new Image();
            img.height = height;
            img.width = width;
            img._loading = true;
            img.onload = function() {
                img._loading = false;
                me.renderFrame();
            };
            img.src = src.slice(1, src.length -1);
        }

        if (!img._loading) {
            ctx.drawImage(img, x - width / 2, y - height / 2, width, height);
        }
    },

    rectRender: function(sprite) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            width = attr.width,
            height = attr.height,
            x = +(attr.x || 0),
            y = +(attr.y || 0);

        if (isFinite(x) && isFinite(y) && isFinite(width) && isFinite(height)) {
            ctx.fillRect(x, y, width, height);
            ctx.strokeRect(x, y, width, height);
        }
    },

    textRender: function(sprite) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            x = +(attr.x || 0),
            y = +(attr.y || 0),
            text = attr.text;
        if (isFinite(x) && isFinite(y)) {
            ctx.textBaseline = 'middle';
            ctx.fillText(text, x, y);
        }
    },

    pathRender: function(sprite) {
        if (!sprite.attr.path) {
            return;
        }

        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            path = Ext.draw.Draw.path2curve(attr.path),
            ln = path.length,
            x, y, i;

        ctx.beginPath();
        for (i = 0; i < ln; i++) {
            switch (path[i][0]) {
                case "M":
                    ctx.moveTo(path[i][1], path[i][2]);
                    if (x == null) {
                        x = path[i][1];
                    }
                    if (y == null) {
                        y = path[i][2];
                    }
                break;
                case "C":
                    ctx.bezierCurveTo(path[i][1],
                                      path[i][2],
                                      path[i][3],
                                      path[i][4],
                                      path[i][5],
                                      path[i][6]);
                break;
                case "Z":
                    ctx.lineTo(x, y);
                break;
            }
        }
        //if stroke is not transparent then draw it
        if (attr.stroke && attr.stroke != 'none' && attr.stroke != 'rgba(0, 0, 0, 0)') {
            ctx.stroke();
        }
        //if fill is not transparent then draw it
        if (attr.fill && attr.fill != 'none' && attr.fill != 'rgba(0, 0, 0, 0)') {
            ctx.fill();
        }
        ctx.closePath();
    },

    //Contains method used for event handling.
    //Returns the target pointed by the mouse or
    //false otherwise.
    contains: function(x, y) {
        var me = this,
            items = me.items.items,
            l = items.length,
            sprite;

        while (l--) {
            sprite = items[l];
            if (me.bboxContains(x, y, sprite)) {
                if (me[sprite.type + 'Contains'](x, y, sprite)) {
                    //TODO(nico): not returning just the sprite because a
                    //more complex object with more informaiton on the event
                    //may be returned.
                    return {
                        target: sprite
                    };
                }
            }
        }

        return false;
    },

    //Whether the point is in the BBox of the shape
    bboxContains: function(x, y, sprite) {
        var bbox = sprite.getBBox();

        return (x >= bbox.x && x <= (bbox.x + bbox.width)
            && (y >= bbox.y && y <= (bbox.y + bbox.height)));
    },

    //Whether the point is in the shape
    circleContains: function(x, y, sprite) {
        var attr = sprite.attr,
            trans = attr.translation,
            cx = (attr.x || 0) + (trans && trans.x || 0),
            cy = (attr.y || 0) + (trans && trans.y || 0),
            dx = x - cx,
            dy = y - cy,
            radius = attr.radius;

        return (dx * dx + dy * dy) <= (radius * radius);
    },

    //Whether the point is in the shape
    ellipseContains: function(x, y, sprite) {
        var attr = sprite.attr,
            trans = attr.translation,
            cx = (attr.x || 0) + (trans && trans.x || 0),
            cy = (attr.y || 0) + (trans && trans.y || 0),
            radiusX = attr.radiusX || (attr.width  / 2) || 0,
            radiusY = attr.radiusY || (attr.height / 2) || 0,
            radius = 0,
            scaleX = 1,
            scaleY = 1,
            dx, dy;

        if (radiusX > radiusY) {
                radius = radiusX;
                scaleY = radiusY / radiusX;
        } else {
            radius = radiusY;
            scaleY = radiusX / radiusY;
        }

        dx = (x - cx) / scaleX;
        dy = (y - cy) / scaleY;

        return (dx * dx + dy * dy) <= (radius * radius);
    },

    //Same behavior as the BBox check, so return true.
    imageContains: function(x, y, sprite) {
        return true;
    },

    //Same behavior as the BBox check, so return true.
    rectContains: function(x, y, sprite) {
        return true;
    },

    //Same behavior as the BBox check, so return true.
    textContains: function(x, y, sprite) {
        return true;
    },

    //TODO(nico): to be implemented later.
    pathContains: function(x, y, sprite) {
        return false;
    },

    createGradient: function(gradient, sprite) {
        var ctx = this.ctx,
            bbox = sprite.getBBox(),
            x1 = bbox.x,
            y1 = bbox.y,
            width = bbox.width,
            height = bbox.height,
            x2 = x1 + width,
            y2 = y1 + height,
            a = Math.round(Math.abs(gradient.degrees || gradient.angle || 0) % 360),
            stops = gradient.stops,
            stop, canvasGradient;

        if (a <= 0) {
            canvasGradient = ctx.createLinearGradient(x1, y1, x1, y2);
        } else if (a <= 45) {
            canvasGradient = ctx.createLinearGradient(x1, y1, x2, y2);
        } else if (a <= 90) {
            canvasGradient = ctx.createLinearGradient(x1, y1, x2, y1);
        } else if (a <= 135) {
            canvasGradient = ctx.createLinearGradient(x2, y1, x1, y2);
        } else if (a <= 180) {
            canvasGradient = ctx.createLinearGradient(x1, y2, x1, y1);
        } else if (a <= 225) {
            canvasGradient = ctx.createLinearGradient(x2, y2, x1, y1);
        } else if (a <= 270) {
            canvasGradient = ctx.createLinearGradient(x2, y1, x1, y1);
        } else if (a <= 315) {
            canvasGradient = ctx.createLinearGradient(x1, y2, x2, y1);
        } else {
            canvasGradient = ctx.createLinearGradient(x1, y1, x2, y2);
        }

        for (stop in stops) {
            if (stops.hasOwnProperty(stop)) {
                canvasGradient.addColorStop(stop, stops[stop].color || '#000');
            }
        }

        return canvasGradient;
    },

    //getBBox
    getBBox: function (sprite, isWithoutTransform) {
        if (sprite.type == 'text') {
            return this.getBBoxText(sprite, isWithoutTransform);
        }
        var realPath = this["getPath" + sprite.type](sprite);
        if (isWithoutTransform) {
            sprite.bbox.plain = sprite.bbox.plain || Ext.draw.Draw.pathDimensions(realPath);
            return sprite.bbox.plain;
        }
        //sprite.bbox.transform = sprite.bbox.transform || Ext.draw.Draw.pathDimensions(Ext.draw.Draw.mapPath(realPath, sprite.matrix));
        //caching the bounding box causes problems :(
        sprite.bbox.transform = Ext.draw.Draw.pathDimensions(Ext.draw.Draw.mapPath(realPath, sprite.matrix));
        return sprite.bbox.transform;
    },

    getBBoxText: function(sprite, isWithoutTransform) {
        var me = this,
            ctx = me.ctx,
            attr = sprite.attr,
            matrix,
            x = attr.x || 0,
            y = attr.y || 0,
            x1, x2, y1, y2,
            x1t, x2t, x3t, x4t,
            y1t, y2t, y3t, y4t,
            width, height,
            trans = sprite.attr.translation,
            dx = trans && trans.x || 0,
            dy = trans && trans.y || 0,
            font = attr.font,
            fontSize = +(font && font.match(/[0-9]+/)[0]) || 10,
            text = attr.text,
            measure;

        ctx.save();
        if (font) {
            ctx.font = font;
        }
        measure = ctx.measureText(text);
        ctx.restore();

        if (sprite.dirtyTransform) {
            me.applyTransformations(sprite);
        }
        matrix = sprite.matrix;

        x1 = x + dx;
        y1 = y + dy;
        x2 = x1 + (measure.width || fontSize);
        y2 = y1 + (measure.height || fontSize);

        if (isWithoutTransform) {
            return {
                x: x,
                y: y,
                width: (measure.width || fontSize),
                height: (measure.height || fontSize)
            };
        }

        x1t = matrix.x(x1, y1);
        y1t = matrix.y(x1, y1);

        x2t = matrix.x(x1, y2);
        y2t = matrix.y(x1, y2);

        x3t = matrix.x(x2, y1);
        y3t = matrix.y(x2, y1);

        x4t = matrix.x(x2, y2);
        y4t = matrix.y(x2, y2);

        x = Math.min(x1t, x2t, x3t, x4t);
        y = Math.min(y1t, y2t, y3t, y4t);

        width = Math.abs(x - Math.max(x1t, x2t, x3t, x4t));
        height = Math.abs(y - Math.max(y1t, y2t, y3t, y4t));

        return {
            x: x,
            y: y,
            width: width,
            height: height
        };
    },

    getRegion: function() {
        var canvas = this.canvas,
            xy = Ext.get(canvas).getXY();

        return {
            left: xy[0],
            top: xy[1],
            right: xy[0] + canvas.width,
            bottom: xy[1] + canvas.height
        };
    },

    //force will force the method to return a value.
    getShadowAttributesArray: function(force) {
        if (force) {
            return [{
                    "stroke-width": 6,
                    "stroke-opacity": 1,
                    stroke: 'rgba(200, 200, 200, 0.5)',
                    translate: {
                        x: 1.2,
                        y: 2
                    }
                },
                {
                    "stroke-width": 4,
                    "stroke-opacity": 1,
                    stroke: 'rgba(150, 150, 150, 0.5)',
                    translate: {
                        x: 0.9,
                        y: 1.5
                    }
                },
                {
                    "stroke-width": 2,
                    "stroke-opacity": 1,
                    stroke: 'rgba(100, 100, 100, 0.5)',
                    translate: {
                        x: 0.6,
                        y: 1
                    }
                }];
        } else {
            return [];
        }
    },

    //force will force the method to return a value.
    getShadowOptions: function(force) {
        return {
            shadowOffsetX: 2,
            //http://code.google.com/p/android/issues/detail?id=16025
            shadowOffsetY: Ext.is.Android ? -2 : 2,
            shadowBlur: 3,
            shadowColor: '#444'
        };
    },

    clear: function() {
        var me = this,
            canvas = me.canvas,
            ctx = me.ctx,
            width = canvas.width,
            height = canvas.height;

        ctx.clearRect(0, 0, width, height);
    }
});



/**
 * @class Ext.draw.Component
 * @extends Ext.Component
 *
 * The Draw Component is a surface in which sprites can be rendered. The Draw Component
 * manages and holds a `Surface` instance: an interface that has
 * an SVG or VML implementation depending on the browser capabilities and where
 * Sprites can be appended.
 * {@img Ext.draw.Component/Ext.draw.Component.png Ext.draw.Component component}
 * One way to create a draw component is:
 *
 *     var drawComponent = Ext.create('Ext.draw.Component', {
 *         viewBox: false,
 *         items: [{
 *             type: 'circle',
 *             fill: '#79BB3F',
 *             radius: 100,
 *             x: 100,
 *             y: 100
 *         }]
 *     });
 *
 *     Ext.create('Ext.Window', {
 *         width: 215,
 *         height: 235,
 *         layout: 'fit',
 *         items: [drawComponent]
 *     }).show();
 *
 * In this case we created a draw component and added a sprite to it.
 * The *type* of the sprite is *circle* so if you run this code you'll see a yellow-ish
 * circle in a Window. When setting `viewBox` to `false` we are responsible for setting the object's position and
 * dimensions accordingly.
 *
 * You can also add sprites by using the surface's add method:
 *
 *     drawComponent.surface.add({
 *         type: 'circle',
 *         fill: '#79BB3F',
 *         radius: 100,
 *         x: 100,
 *         y: 100
 *     });
 *
 * For more information on Sprites, the core elements added to a draw component's surface,
 * refer to the Ext.draw.Sprite documentation.
 */
Ext.draw.Component = Ext.extend(Ext.Component, {

    /**
     * @cfg {Array} enginePriority
     * Defines the priority order for which Surface implementation to use. The first
     * one supported by the current environment will be used.
     */
    enginePriority: ['Canvas'],

    baseCls: 'ext-surface',

    componentLayout: 'draw',

    /**
     * @cfg {Boolean} viewBox
     * Turn on view box support which will scale and position items in the draw component to fit to the component while
     * maintaining aspect ratio. Note that this scaling can override other sizing settings on yor items. Defaults to true.
     */
    viewBox: true,

    /**
     * @cfg {Boolean} autoSize
     * Turn on autoSize support which will set the bounding div's size to the natural size of the contents. Defaults to false.
     */
    autoSize: false,

    /**
     * @cfg {Array} gradients (optional) Define a set of gradients that can be used as `fill` property in sprites.
     * The gradients array is an array of objects with the following properties:
     *
     * <ul>
     * <li><strong>id</strong> - string - The unique name of the gradient.</li>
     * <li><strong>angle</strong> - number, optional - The angle of the gradient in degrees.</li>
     * <li><strong>stops</strong> - object - An object with numbers as keys (from 0 to 100) and style objects
     * as values</li>
     * </ul>
     *

     For example:

     <pre><code>
        gradients: [{
            id: 'gradientId',
            angle: 45,
            stops: {
                0: {
                    color: '#555'
                },
                100: {
                    color: '#ddd'
                }
            }
        },  {
            id: 'gradientId2',
            angle: 0,
            stops: {
                0: {
                    color: '#590'
                },
                20: {
                    color: '#599'
                },
                100: {
                    color: '#ddd'
                }
            }
        }]
     </code></pre>

     Then the sprites can use `gradientId` and `gradientId2` by setting the fill attributes to those ids, for example:

     <pre><code>
        sprite.setAttributes({
            fill: 'url(#gradientId)'
        }, true);
     </code></pre>

     */

    cls: 'x-draw-component',

    initComponent: function() {
        var me = this;
        Ext.draw.Component.superclass.initComponent.call(me);

        // Expose all mouse/touch events fired by the Surface
        me.addEvents.apply(me, Ext.draw.Surface.eventNames);
    },

    /**
     * @private
     *
     * Create the Surface on initial render
     */
    onRender: function() {
        var me = this,
            viewBox = me.viewBox,
            autoSize = me.autoSize,
            bbox, items, width, height, x, y;
        Ext.draw.Component.superclass.onRender.apply(this, arguments);

        me.surface = me.createSurface();

        items = me.surface.items;

        if (viewBox || autoSize) {
            bbox = items.getBBox();
            width = bbox.width;
            height = bbox.height;
            x = bbox.x;
            y = bbox.y;
            if (me.viewBox) {
                me.surface.setViewBox(x, y, width, height);
            }
            else {
                // AutoSized
                me.autoSizeSurface();
            }
        }
    },

    /**
     * @private Return a reference to the {@link Ext.draw.Surface} instance from which events
     * should be relayed.
     */
    getEventsSurface: function() {
        return this.surface;
    },

    initEvents: function() {
        var me = this;
        Ext.draw.Component.superclass.initEvents.call(me);

        // Relay all mouse/touch events from the surface
        me.relayEvents(me.getEventsSurface(), Ext.draw.Surface.eventNames);
    },

    //@private
    autoSizeSurface: function() {
        var me = this,
            items = me.surface.items,
            bbox = items.getBBox(),
            width = bbox.width,
            height = bbox.height;
        items.setAttributes({
            translate: {
                x: -bbox.x,
                //Opera has a slight offset in the y axis.
                y: -bbox.y + (+Ext.isOpera)
            }
        }, true);
        if (me.rendered) {
            me.setSize(width, height);
        }
        else {
            me.surface.setSize(width, height);
        }
        me.el.setSize(width, height);
    },

    /**
     * Create the Surface instance. Resolves the correct Surface implementation to
     * instantiate based on the 'enginePriority' config. Once the Surface instance is
     * created you can use the handle to that instance to add sprites. For example:
     *
     <pre><code>
        drawComponent.surface.add(sprite);
     </code></pre>
     */
    createSurface: function(config) {
        var me = this,
            apply = Ext.apply;

        return Ext.draw.Surface.create(apply({}, apply({
                width: me.width,
                height: me.height,
                renderTo: me.el,
                id: Ext.id()
            }, config), me.initialConfig));
    },


    /**
     * @private
     *
     * Clean up the Surface instance on component destruction
     */
    onDestroy: function() {
        var surface = this.surface;
        if (surface) {
            surface.destroy();
        }
        Ext.draw.Component.superclass.onDestroy.call(this);
    }

});

Ext.reg('draw', Ext.draw.Component);


/**
 * @class Ext.chart.Shape
 * @ignore
 */
Ext.ns('Ext.chart');
Ext.chart.Shape = {
    image: function (surface, opts) {
        opts.height = opts.height || 16;
        opts.width = opts.width || 16;
        return surface.add(Ext.applyIf({
            type: 'image',
            x: opts.x,
            y: opts.y,
            height: opts.height,
            width: opts.width,
            src: opts.src
        }, opts));
    },
    circle: function (surface, opts) {
        return surface.add(Ext.apply({
            type: 'circle',
            x: opts.x,
            y: opts.y,
            stroke: null,
            radius: opts.radius
        }, opts));
    },
    line: function (surface, opts) {
        return surface.add(Ext.apply({
            type: 'rect',
            x: opts.x - opts.radius,
            y: opts.y - opts.radius,
            height: 2 * opts.radius,
            width: 2 * opts.radius / 5
        }, opts));
    },
    square: function (surface, opts) {
        return surface.add(Ext.applyIf({
            type: 'rect',
            x: opts.x - opts.radius,
            y: opts.y - opts.radius,
            height: 2 * opts.radius,
            width: 2 * opts.radius,
            radius: null
        }, opts));
    },
    triangle: function (surface, opts) {
        opts.radius *= 1.75;
        return surface.add(Ext.apply({
            type: 'path',
            stroke: null,
            path: "M".concat(opts.x, ",", opts.y, "m0-", opts.radius * 0.58, "l", opts.radius * 0.5, ",", opts.radius * 0.87, "-", opts.radius, ",0z")
        }, opts));
    },
    diamond: function (surface, opts) {
        var r = opts.radius;
        r *= 1.5;
        return surface.add(Ext.apply({
            type: 'path',
            stroke: null,
            path: ["M", opts.x, opts.y - r, "l", r, r, -r, r, -r, -r, r, -r, "z"]
        }, opts));
    },
    cross: function (surface, opts) {
        var r = opts.radius;
        r = r / 1.7;
        return surface.add(Ext.apply({
            type: 'path',
            stroke: null,
            path: "M".concat(opts.x - r, ",", opts.y, "l", [-r, -r, r, -r, r, r, r, -r, r, r, -r, r, r, r, -r, r, -r, -r, -r, r, -r, -r, "z"])
        }, opts));
    },
    plus: function (surface, opts) {
        var r = opts.radius / 1.3;
        return surface.add(Ext.apply({
            type: 'path',
            stroke: null,
            path: "M".concat(opts.x - r / 2, ",", opts.y - r / 2, "l", [0, -r, r, 0, 0, r, r, 0, 0, r, -r, 0, 0, r, -r, 0, 0, -r, -r, 0, 0, -r, "z"])
        }, opts));
    },
    arrow: function (surface, opts) {
        var r = opts.radius;
        return surface.add(Ext.apply({
            type: 'path',
            path: "M".concat(opts.x - r * 0.7, ",", opts.y - r * 0.4, "l", [r * 0.6, 0, 0, -r * 0.4, r, r * 0.8, -r, r * 0.8, 0, -r * 0.4, -r * 0.6, 0], "z")
        }, opts));
    },
    drop: function (surface, x, y, text, size, angle) {
        size = size || 30;
        angle = angle || 0;
        surface.add({
            type: 'path',
            path: ['M', x, y, 'l', size, 0, 'A', size * 0.4, size * 0.4, 0, 1, 0, x + size * 0.7, y - size * 0.7, 'z'],
            fill: '#000',
            stroke: 'none',
            rotate: {
                degrees: 22.5 - angle,
                x: x,
                y: y
            }
        });
        angle = (angle + 90) * Math.PI / 180;
        surface.add({
            type: 'text',
            x: x + size * Math.sin(angle) - 10, // Shift here, Not sure why.
            y: y + size * Math.cos(angle) + 5,
            text:  text,
            'font-size': size * 12 / 40,
            stroke: 'none',
            fill: '#fff'
        });
    }
};
/**
 * @class Ext.chart.Toolbar
 * @extends Ext.Container
 *
 * The chart toolbar is a container that is docked to one side of the chart, that is intended
 * to hold buttons for performing user actions without taking up valuable screen real estate
 * from the chart. This is used internally for things like the button for showing the legend
 * when the legend is {@link Ext.chart.Legend#dock docked}, or the
 * {@link Ext.chart.interactions.PanZoom pan/zoom interaction}'s button for switching between
 * pan and zoom mode in non-multi-touch environments.
 *
 * An instance of this class is created automatically by the chart when it is needed; authors
 * should not need to instantiate it directly. To customize the configuration of the toolbar,
 * specify the chart's {@link Ext.chart.Chart#toolbar toolbar} config.
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.Toolbar = Ext.extend(Ext.Container, {
    isChartToolbar: true,

    defaultType: 'button',

    baseCls: Ext.baseCSSPrefix + 'chart-toolbar',

    /**
     * @cfg {String} position
     * The position at which the toolbar should be docked in relation to the chart. Can be one of:
     *
     * -  "top" - positions the legend centered at the top of the chart
     * -  "bottom" - positions the legend centered at the bottom of the chart
     * -  "left" - positions the legend centered on the left side of the chart
     * -  "right" - positions the legend centered on the right side of the chart
     *
     *     toolbar: {
     *         position: 'right'
     *     }
     *
     * In addition, you can specify different positionss based on the orientation of the browser viewport,
     * for instance you might want to put the toolbar on the right in landscape orientation but on the bottom in
     * portrait orientation. To achieve this, you can set the `position` config to an Object with `portrait` and
     * `landscape` properties, and set the value of those properties to one of the recognized value types described
     * above. For example, the following config will put the toolbar on the right in landscape and on the bottom
     * in portrait:
     *
     *     toolbar:
     *         position: {
     *             landscape: 'right',
     *             portrait: 'bottom'
     *         }
     *     }
     *
     * If not specified, the position will default to the configured position of the chart legend (if
     * a legend is configured), or 'bottom' otherwise.
     */


    /**
     * Returns whether the toolbar is configured with orientation-specific positions.
     * @return {Boolean}
     */
    isOrientationSpecific: function() {
        var position = this.position;
        return (position && Ext.isObject(position) && 'portrait' in position);
    },

    /**
     * Get the target position of the toolbar, after resolving any orientation-specific configs.
     * In most cases this method should be used rather than reading the `position` property directly.
     * @return {String} The position config value
     */
    getPosition: function() {
        var me = this,
            position = me.position,
            legend = me.chart.legend;

        if (!position && legend) {
            // Fall back to legend position if legend is present
            position = legend.getPosition();
        }
        else if (me.isOrientationSpecific()) {
            // Grab orientation-specific config if specified
            position = position[Ext.getOrientation()];
        }
        if (!position || !Ext.isString(position)) {
            // Catchall fallback
            position = 'bottom';
        }
        return position;
    },

    /**
     * @protected
     * Updates the toolbar to match the current viewport orientation.
     */
    orient: function() {
        var me = this,
            orientation = Ext.getOrientation();
        if (!me.rendered) {
            me.render(me.chart.el);
        }
        if (orientation !== me.lastOrientation) {
            me.el.dom.setAttribute('data-side', me.getPosition());
            me.lastOrientation = orientation;
        }
    }

});
/**
 * @class Ext.chart.Legend
 *
 * Defines a legend for a chart's series.
 * The 'chart' member must be set prior to rendering.
 * The legend class displays a list of legend items each of them related with a
 * series being rendered. In order to render the legend item of the proper series
 * the series configuration object must have {@link Ext.chart.Series#showInLegend showInLegend}
 * set to true.
 *
 * The legend configuration object accepts a {@link #position} as parameter, which allows
 * control over where the legend appears in relation to the chart. The position can be
 * confiured with different values for portrait vs. landscape orientations. Also, the {@link #dock}
 * config can be used to hide the legend in a sheet docked to one of the sides.
 *
 * Full example:
    <pre><code>
    var store = new Ext.data.JsonStore({
        fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
        data: [
            {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
            {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
            {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
            {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
            {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
        ]
    });

    new Ext.chart.Chart({
        renderTo: Ext.getBody(),
        width: 500,
        height: 300,
        animate: true,
        store: store,
        shadow: true,
        theme: 'Category1',
        legend: {
            position: 'top'
        },
         axes: [{
                type: 'Numeric',
                grid: true,
                position: 'left',
                fields: ['data1', 'data2', 'data3', 'data4', 'data5'],
                title: 'Sample Values',
                grid: {
                    odd: {
                        opacity: 1,
                        fill: '#ddd',
                        stroke: '#bbb',
                        'stroke-width': 1
                    }
                },
                minimum: 0,
                adjustMinimumByMajorUnit: 0
            }, {
                type: 'Category',
                position: 'bottom',
                fields: ['name'],
                title: 'Sample Metrics',
                grid: true,
                label: {
                    rotate: {
                        degrees: 315
                    }
                }
        }],
        series: [{
            type: 'area',
            highlight: false,
            axis: 'left',
            xField: 'name',
            yField: ['data1', 'data2', 'data3', 'data4', 'data5'],
            style: {
                opacity: 0.93
            }
        }]
    });
    </code></pre>
 *
 * @constructor
 */
Ext.chart.Legend = Ext.extend(Ext.util.Observable, {

    /**
     * @cfg {Boolean} visible
     * Whether or not the legend should be displayed.
     */
    visible: true,

    /**
     * @cfg {String} position
     * The position of the legend in relation to the chart. Can be one of:
     *
     * -  "top" - positions the legend centered at the top of the chart
     * -  "bottom" - positions the legend centered at the bottom of the chart
     * -  "left" - positions the legend centered on the left side of the chart
     * -  "right" - positions the legend centered on the right side of the chart
     * -  an Object with numeric properties `x` and `y`, and boolean property `vertical` - displays the legend
     *    floating on top of the chart at the given x/y coordinates. If `vertical:true` the legend items will
     *    be arranged stacked vertically, otherwise they will be arranged side-by-side. If {@link #dock} is
     *    set to `true` then this position config will be ignored and will dock to the bottom.
     *
     * In addition, you can specify different legend alignments based on the orientation of the browser viewport,
     * for instance you might want to put the legend on the right in landscape orientation but on the bottom in
     * portrait orientation. To achieve this, you can set the `position` config to an Object with `portrait` and
     * `landscape` properties, and set the value of those properties to one of the recognized value types described
     * above. For example, the following config will put the legend on the right in landscape but float it on top
     * of the chart at position 10,10 in portrait:
     *
     *     legend: {
     *         position: {
     *             landscape: 'right',
     *             portrait: {
     *                 x: 10,
     *                 y: 10,
     *                 vertical: true
     *             }
     *         }
     *     }
     */
    position: 'bottom',

    /**
     * @cfg {Boolean} dock
     * If set to `true`, then rather than rendering within the chart area the legend will be docked to the
     * {@link #position configured edge position} within a {@link Ext.Sheet}. The sheet will be initially
     * hidden and can be opened by tapping on a tab along the configured edge. This prevents screen real
     * estate from being taken up by the legend, which is especially important on small screen devices.
     *
     * Defaults to `true` for phone-sized screens, `false` for larger screens.
     */
    dock: Ext.is.Phone,

    /**
     * @cfg {Number} doubleTapThreshold
     * The duration in milliseconds in which two consecutive taps will be considered a doubletap.
     * Defaults to `250`.
     */
    doubleTapThreshold: 250,

    /**
     * @constructor
     * @param {Object} config
     */
    constructor: function(config) {
        var me = this,
            chart = config.chart,
            chartEl = chart.el,
            button, sheet, view, transitions, sheetAnim;

        me.addEvents(
            /**
             * @event combine
             * Fired when two legend items are combined together via drag-drop.
             * @param {Ext.chart.Legend} legend
             * @param {Ext.chart.series.Series} series The series owning the items being combined
             * @param {Number} index1 The index of the first legend item
             * @param {Number} index2 The index of the second legend item
             */
            'combine',

            /**
             * @event split
             * Fired when a previously-combined legend item is split into its original constituent items.
             * @param {Ext.chart.Legend} legend
             * @param {Ext.chart.series.Series} series The series owning the item being split
             * @param {Number} index The index of the legend item being split
             */
            'split'
        );

        Ext.chart.Legend.superclass.constructor.call(me, config);

        view = me.getView();
        if (me.dock) {
            // Legend is docked; create the sheet and trigger button
            button = me.button = chart.getToolbar().add({
                showAnimation: 'fade',
                cls: Ext.baseCSSPrefix + 'legend-button',
                iconCls: Ext.baseCSSPrefix + 'legend-button-icon',
                iconMask: true,
                handler: function() {
                    me.sheet.show();
                }
            });
            button.show();

            transitions = {
                bottom : 'up',
                top    : 'down',
                right  : 'left',
                left   : 'right'
            };

            sheetAnim = {
                type: 'slide',
                duration: 150,
                direction: transitions[me.getPosition()]
            };

            sheet = me.sheet = new Ext.Sheet({
                enter: me.getPosition(),
                stretchY: true,
                stretchX: true,
                ui: 'legend',
                hideOnMaskTap: true,
                enterAnimation: sheetAnim,
                exitAnimation: sheetAnim,
                width: 200,
                height: 260,
                renderTo: chartEl,
                layout: 'fit',
                items: view,
                listeners: {
                    // If user swipes in direction sheet came from, close it
                    // Only works for side-positioned labels (otherwise could just be scrolling legend list)
                    swipe: {
                        element: 'el',
                        fn: function(gesture){
                            if (gesture.direction == me.getPosition()) {
                                me.sheet.hide();
                            }
                        }
                    }
                }
            });
        } else {
            // Not docked; render view directly into chart container
            view.render(chartEl);
        }

        if (me.isDisplayed()) {
            me.show();
        }
    },

    /**
     * Retrieves the view component for this legend, creating it first if needed.
     * @return {@link Ext.chart.Legend.View}
     */
    getView: function() {
        var me = this;
        return me.view || (me.view = new Ext.chart.Legend.View({
            legend: me,
            floating: !me.dock
        }));
    },

    /**
     * @private Determine whether the legend should be displayed. Looks at the legend's 'visible' config,
     * and also the 'showInLegend' config for each of the series.
     * @return {Boolean}
     */
    isDisplayed: function() {
        return this.visible && this.chart.series.findIndex('showInLegend', true) !== -1;
    },

    /**
     * Returns whether the legend is configured with orientation-specific positions.
     * @return {Boolean}
     */
    isOrientationSpecific: function() {
        var position = this.position;
        return (Ext.isObject(position) && 'portrait' in position);
    },

    /**
     * Get the target position of the legend, after resolving any orientation-specific configs.
     * In most cases this method should be used rather than reading the `position` property directly.
     * @return {String/Object} The position config value
     */
    getPosition: function() {
        var me = this,
            position = me.position;
        // Grab orientation-specific config if specified
        if (me.isOrientationSpecific()) {
            position = position[Ext.getOrientation()];
        }
        // If legend is docked, default non-String values to 'bottom'
        if (me.dock && !Ext.isString(position)) {
            position = 'bottom';
        }
        return position;
    },

    /**
     * Returns whether the orientation of the legend items is vertical.
     * @return {Boolean} `true` if the legend items are to be arranged stacked vertically, `false` if they
     * are to be arranged side-by-side.
     */
    isVertical: function() {
        var position = this.getPosition();
        return this.dock || (Ext.isObject(position) ? position.vertical : "left|right|float".indexOf('' + position) !== -1);
    },

    /**
     * Update the legend component to match the current viewport orientation.
     */
    orient: function() {
        var me = this,
            sheet = me.sheet,
            position = me.getPosition(),
            orientation = Ext.getOrientation(),
            auto = 'auto';

        me.getView().orient();

        if (me.lastOrientation !== orientation) {
            if (sheet) {
                sheet.hide();
                sheet.enter = sheet.exit = position;
                sheet.setSize(null, null);
                sheet.orient();
            }

            me.lastOrientation = orientation;
        }
    },

    /**
     * @private Update the position of the legend if it is displayed and not docked.
     */
    updatePosition: function() {
        if (!this.dock) {
            var me = this,
                chart = me.chart,
                chartBBox = chart.chartBBox,
                insets = chart.insetPadding,
                isObject = Ext.isObject(insets),
                insetLeft = (isObject ? insets.left : insets) || 0,
                insetRight = (isObject ? insets.right : insets) || 0,
                insetBottom = (isObject ? insets.bottom : insets) || 0,
                insetTop = (isObject ? insets.top : insets) || 0,
                chartWidth = chart.curWidth,
                chartHeight = chart.curHeight,
                seriesWidth = chartBBox.width - (insetLeft + insetRight),
                seriesHeight = chartBBox.height - (insetTop + insetBottom),
                chartX = chartBBox.x + insetLeft,
                chartY = chartBBox.y + insetTop,
                isVertical = me.isVertical(),
                view = me.getView(),
                math = Math,
                mfloor = math.floor,
                mmin = math.min,
                mmax = math.max,
                x, y, legendWidth, legendHeight, maxWidth, maxHeight, position, undef;

            if (me.sheet) {
                return; //only set position if view is directly floated
            }

            if (me.isDisplayed()) {
                // Calculate the natural size
                view.show();
                view.setCalculatedSize(isVertical ? undef : null, isVertical ? null : undef); //clear fixed scroller length
                legendWidth = view.getWidth();
                legendHeight = view.getHeight();

                position = me.getPosition();
                if (Ext.isObject(position)) {
                    // Object with x/y properties: use them directly
                    x = position.x;
                    y = position.y;
                } else {
                    // Named positions - calculate x/y based on chart dimensions
                    switch(position) {
                        case "left":
                            x = insetLeft;
                            y = mfloor(chartY + seriesHeight / 2 - legendHeight / 2);
                            break;
                        case "right":
                            x = mfloor(chartWidth - legendWidth) - insetRight;
                            y = mfloor(chartY + seriesHeight / 2 - legendHeight / 2);
                            break;
                        case "top":
                            x = mfloor(chartX + seriesWidth / 2 - legendWidth / 2);
                            y = insetTop;
                            break;
                        default:
                            x = mfloor(chartX + seriesWidth / 2 - legendWidth / 2);
                            y = mfloor(chartHeight - legendHeight) - insetBottom;
                    }
                    x = mmax(x, insetLeft);
                    y = mmax(y, insetTop);
                }

                maxWidth = chartWidth - x - insetRight;
                maxHeight = chartHeight - y - insetBottom;

                view.setPosition(x, y);
                if (legendWidth > maxWidth || legendHeight > maxHeight) {
                    view.setCalculatedSize(mmin(legendWidth, maxWidth), mmin(legendHeight, maxHeight));
                }
            } else {
                view.hide();
            }
        }
    },

    /**
     * Calculate and return the number of pixels that should be reserved for the legend along
     * its edge. Only returns a non-zero value if the legend is positioned to one of the four
     * named edges, and if it is not {@link #dock docked}.
     */
    getInsetSize: function() {
        var me = this,
            pos = me.getPosition(),
            chartPadding = me.chart.insets,
            left = chartPadding.left,
            bottom = chartPadding.bottom,
            top = chartPadding.top,
            right = chartPadding.right,
            size = 0,
            view;

        if (!me.dock && me.isDisplayed()) {
            view = me.getView();
            view.show();
            if (pos === 'left' || pos === 'right') {
                size = view.getWidth() + left;
            }
            else if (pos === 'top' || pos === 'bottom') {
                size = view.getHeight() + top;
            }
        }
        return size;
    },

    /**
     * Shows the legend if it is currently hidden.
     */
    show: function() {
        (this.sheet || this.getView()).show();
    },

    /**
     * Hides the legend if it is currently shown.
     */
    hide: function() {
        (this.sheet || this.getView()).hide();
    },

    /**
     * @protected Fired when two legend items are combined via drag-drop in the legend view.
     * @param {Ext.chart.series.Series} series The series for the combined items
     * @param {Ext.chart.series.Series} index1 The series for the combined items
     * @param {Ext.chart.series.Series} index2 The series for the combined items
     */
    onCombine: function(series, index1, index2) {
        var me = this;
        series.combine(index1, index2);
        me.getView().updateStore();
        me.fireEvent('combine', me, series, index1, index2);
    },

    onSplit: function(series, index) {
        var me = this;
        series.split(index);
        me.getView().updateStore();
        me.fireEvent('split', me, series, index);
    },

    /**
     * Reset the legend back to its initial state before any user interactions.
     */
    reset: function() {
        this.getView().reset();
    }
});


/**
 * @class Ext.chart.Legend.View
 * @extends Ext.DataView
 *
 * A DataView specialized for displaying the legend items for a chart. This class is only
 * used internally by {@link Ext.chart.Legend} and should not need to be instantiated directly.
 */
Ext.chart.Legend.View = Ext.extend(Ext.DataView, {
    tpl: [
        '<ul class="' + Ext.baseCSSPrefix + 'legend-items">',
            '<tpl for=".">',
                '<li class="' + Ext.baseCSSPrefix + 'legend-item <tpl if="disabled">' + Ext.baseCSSPrefix + 'legend-inactive' + '</tpl>">',
                    '<span class="' + Ext.baseCSSPrefix + 'legend-item-marker" style="background-color:{markerColor};"></span>{label}',
                '</li>',
            '</tpl>',
        '</ul>'
    ],

    disableSelection: true,
    componentCls: Ext.baseCSSPrefix + 'legend',
    horizontalCls: Ext.baseCSSPrefix + 'legend-horizontal',
    inactiveItemCls: Ext.baseCSSPrefix + 'legend-inactive',
    itemSelector: '.' + Ext.baseCSSPrefix + 'legend-item',
    hideOnMaskTap: false,
    triggerEvent: 'tap',

    initComponent: function() {
        var me = this;
        me.createStore();
        Ext.chart.Legend.View.superclass.initComponent.call(me);
        me.on('refresh', me.updateDroppables, me);
    },

    initEvents: function() {
        var me = this;
        Ext.chart.Legend.View.superclass.initEvents.call(me);
        me.el.on('taphold', me.onTapHold, me, {delegate: me.itemSelector});
},

    /**
     * @private Fired when a legend item is tap-held. Initializes a draggable for the
     * held item.
     */
    onTapHold: function(e, target) {
        var me = this,
            draggable, record, seriesId, combinable;

        if (!Ext.fly(target).hasCls(me.inactiveItemCls)) {
            record = me.getRecord(target);
            seriesId = record.get('seriesId');
            combinable = me.store.findBy(function(record2) {
                return record2 !== record && record2.get('seriesId') === seriesId;
            });
            if (combinable > -1) {
                draggable = new Ext.util.Draggable(target, {
                    threshold: 0,
                    revert: true,
                    direction: me.legend.isVertical() ? 'vertical' : 'horizontal',
                    group: seriesId
                });

                draggable.on('dragend', me.onDragEnd, me);

                if (!draggable.dragging) {
                    draggable.onStart(e);
                }
            }
        }
    },

    /**
     * @private Updates the droppable objects for each list item. Should be called whenever
     * the list view is re-rendered.
     */
    updateDroppables: function() {
        var me = this,
            droppables = me.droppables,
            droppable;

        Ext.destroy(droppables);

        droppables = me.droppables = [];
        me.store.each(function(record) {
            droppable = new Ext.chart.Legend.Droppable(me.getNode(record), {
                group: record.get('seriesId'),
                disabled: record.get('disabled')
            });

            droppable.on('drop', me.onDrop, me);

            droppables.push(droppable);
        });
    },

    /**
     * @private Handles dropping one legend item on another.
     */
    onDrop: function(droppable, draggable) {
        var me = this,
            dragRecord = me.getRecord(draggable.el.dom),
            dropRecord = me.getRecord(droppable.el.dom);
        me.legend.onCombine(dragRecord.get('series'), dragRecord.get('index'), dropRecord.get('index'));
    },

    onDragEnd : function(draggable, e) {
        draggable.destroy();
    },

    /**
     * @private Create the internal data store for the view
     */
    createStore: function() {
        var me = this;

        me.store = new Ext.data.Store({
            fields: ['markerColor', 'label', 'series', 'seriesId', 'index', 'disabled'],
            data: me.getStoreData()
        });

        me.legend.chart.series.each(function(series) {
            series.on('titlechange', me.updateStore, me);
        });
    },

    /**
     * @private Create and return the JSON data for the legend's internal data store
     */
    getStoreData: function() {
        var data = [];

        this.legend.chart.series.each(function(series) {
            if (series.showInLegend) {
                Ext.each(series.getLegendLabels(), function(label, i) {
                    data.push({
                        label: label,
                        markerColor: series.getLegendColor(i),
                        series: series,
                        seriesId: Ext.id(series, 'legend-series-'),
                        index: i,
                        disabled: !series.visibleInLegend(i)
                    });
                });
            }
        });

        return data;
    },

    /**
     * Updates the internal store to match the current legend info supplied by all the series.
     */
    updateStore: function() {
        var store = this.store;
        store.suspendEvents(true);
        store.removeAll();
        store.add(this.getStoreData());
        store.resumeEvents();
    },

    /**
     * Update the legend component to match its current vertical/horizontal orientation
     */
    orient: function() {
        var me = this,
            legend = me.legend,
            horizontalCls = me.horizontalCls,
            isVertical = legend.isVertical(),
            orientation = Ext.getOrientation();

        if (isVertical) {
            me.removeCls(horizontalCls);
        } else {
            me.addCls(horizontalCls);
        }

        if (me.lastOrientation !== orientation) {
            me.setCalculatedSize(null, null);

            // Clean up things set by previous scroller -- Component#setScrollable should be fixed to do this
            me.scrollEl.setStyle({
                width: '',
                height: '',
                minWidth: '',
                minHeight: ''
            });
            Ext.iterate(me.scroller.scrollView.indicators, function(axis, indicator) {
                clearTimeout(indicator.hideTimer);
                Ext.destroy(indicator.el);
                delete indicator.el;
            }, this);
            me.scroller.destroy();

            // Re-init scrolling in the correct direction
            me.setScrollable(isVertical ? 'vertical' : 'horizontal');

            if (isVertical) {
                // Fix to the initial natural width so it doesn't expand when items are combined
                me.setCalculatedSize(me.getWidth());
            }
            if (me.scroller) {
                me.scroller.scrollTo({x: 0, y: 0});
            }
            me.lastOrientation = orientation;
        }
    },

    afterComponentLayout: function() {
        var me = this,
            scroller = me.scroller,
            innerSize, outerSize;

        Ext.chart.Legend.View.superclass.afterComponentLayout.apply(me, arguments);

        // Enable or disable scrolling depending on if the legend needs to be scrollable
        if (scroller) {
            innerSize = scroller.size;
            outerSize = scroller.containerBox;
            if (innerSize.width > outerSize.width || innerSize.height > outerSize.height) {
                scroller.enable();
            } else {
                scroller.disable();
            }
        }
    },

    refresh: function() {
        Ext.chart.Legend.View.superclass.refresh.apply(this, arguments);

        // Refresh may decrease the size of the scrollable content; we need to clear minWidth/Height
        // on the scrollEl so it doesn't force the floated view el to keep its old size.
        this.scrollEl.setStyle({
            minWidth: '',
            minHeight: ''
        });
    },

    onItemTap: function(item, i, e) {
        Ext.chart.Legend.View.superclass.onItemTap.apply(this, arguments);

        var me = this,
            record = me.store.getAt(i),
            series = record.get('series'),
            index = record.get('index'),
            threshold = me.legend.doubleTapThreshold,
            tapTask = me.tapTask || (me.tapTask = new Ext.util.DelayedTask()),
            now = +new Date();
        tapTask.cancel();

        // If the tapped item is a combined item, we need to distinguish between single and
        // double taps by waiting a bit; otherwise trigger the single tap handler immediately.
        if (series.isCombinedItem(index)) {
            if (now - (me.lastTapTime || 0) < threshold) {
                me.doItemDoubleTap(item, i);
            }
            else {
                tapTask.delay(threshold, me.doItemTap, me, [item, i]);
            }
            me.lastTapTime = now;
        } else {
            me.doItemTap(item, i);
        }
    },

    /**
     * @private
     * Handle single taps on legend items; toggles the corresponding series items on and off.
     */
    doItemTap: function(item, i) {
        var me = this,
            record = me.store.getAt(i),
            series = record.get('series'),
            index = record.get('index'),
            active = series.visibleInLegend(index),
            droppable = me.droppables[i],
            inactiveCls = me.inactiveItemCls;

        // Set the _index property on the series, this is used by the hideAll and
        // showAll methods for some series to know which legend item to hide/show.
        // This would be cleaner if it were just a passed argument.
        series._index = index;
        if (active) {
            series.hideAll();
            Ext.fly(item).addCls(inactiveCls);
            droppable.disable();
        } else {
            series.showAll();
            Ext.fly(item).removeCls(inactiveCls);
            droppable.enable();
        }

        // Flush rendering of affected surfaces
        series.getSurface().renderFrame();
        series.getOverlaySurface().renderFrame();
        me.legend.chart.axes.each(function(axis) {
            axis.renderFrame();
        });
    },

    /**
     * @private
     * Handle double-taps on legend items; splits items that are a result of item combination
     */
    doItemDoubleTap: function(item, i) {
        var me = this,
            record = me.getRecord(item);
        if (record) {
            me.legend.onSplit(record.get('series'), record.get('index'));
        }
    },

    /**
     * Reset the legend view back to its initial state before any user interactions.
     */
    reset: function() {
        var me = this;
        me.store.each(function(record) {
            var series = record.get('series');
            series._index = record.get('index');
            series.showAll();
            Ext.fly(me.getNode(record)).removeCls(me.inactiveItemCls);
            series.clearCombinations();
        });

        me.updateStore();
    }

});


/**
 * @private
 * @class Ext.chart.Legend.Droppable
 * @extends Ext.util.Droppable
 * Custom Droppable implementation for legend items. Only lets one legend item be active as a
 * drop target at once, using the center point of the draggable.
 */
Ext.chart.Legend.Droppable = Ext.extend(Ext.util.Droppable, {
    isDragOver : function(draggable) {
        var draggableRegion = draggable.region,
            round = Math.round,
            draggableCenter = {
                x: round((draggableRegion.right - draggableRegion.left) / 2 + draggableRegion.left) + 0.5,
                y: round((draggableRegion.bottom - draggableRegion.top) / 2 + draggableRegion.top) + 0.5
            };

        return draggable.el !== this.el && !this.region.isOutOfBound(draggableCenter);
    }
});

/**
 * @class Ext.chart.theme.Theme
 * @ignore
 */
Ext.ns('Ext.chart.theme');

//TODO(nico): I'm pretty sure this shouldn't be here.
Ext.ComponentQuery.pseudos['nth-child'] = function(items, value) {
    var index = +value -1;
    if (items[index]) {
        return [items[index]];
    }
    return [];
};

Ext.ComponentQuery.pseudos.highlight = function(items, value) {
    var i = 0, 
        j = 0,
        l = items.length,
        ans = [],
        item, refItems, refItem, lRefItems;

    for (; i < l; ++i) {
        item = items[i];
        if (item.isXType && item.isXType('highlight')) {
            ans.push(item);
        }
        if (item.getRefItems) {
            refItems = item.getRefItems(true);
            for (j = 0, lRefItems = refItems.length; j < lRefItems; ++j) {
                refItem = refItems[j];
                if (refItem.isXType && refItem.isXType('highlight')) {
                    ans.push(refItem);
                }
            }
        }
        
    }
    return ans;
};

Ext.chart.theme.Theme = Ext.extend(Object, {
    theme: 'Base',
    themeInitialized: false,

    applyStyles: function(themeName) {
        if (this.themeInitialized) {
            return;
        }
        //http://www.w3.org/TR/css3-selectors/#specificity.
        var me = this,
            root = {

                getRefItems: function() {
                    return [me];
                },

                isXType: function() {
                    return false;
                },

                initCls: function() {
                    return [];
                },

                getItemId: function() {
                    return '';
                }
            },
            themes = [Ext.chart.theme.Base.slice()],
            i = 0,
            n = 0,
            results = [],
            res, selector, style, rule, j, matches, lmatches, ln, l, configs;

        if (themeName || me.theme != 'Base') {
            themes.push(Ext.chart.theme[themeName || me.theme].slice());
        }

        for (ln = themes.length; n < ln; ++n) {
            configs = themes[n];
            l = configs.length;

            //sort by specificity
            configs.sort(function(a, b) {
                var sa = a.specificity,
                    sb = b.specificity;

                return sb[0] < sa[0] || (sb[0] == sa[0] && sb[1] < sa[1])
                    || (sb[0] == sa[0] && sb[1] == sa[1] && sb[2] < sa[2]);
            });

            for (i = 0; i < l; ++i) {
                rule = configs[i];
                selector = rule.selector;
                style = rule.style;
                matches = Ext.ComponentQuery.query(selector, root);
                results.push.apply(results, matches);
                for (j = 0, lmatches = matches.length; j < lmatches; ++j) {
                    matches[j].themeStyle = Ext.apply(matches[j].themeStyle || {}, style);
                }
            }
        }

        //Now get all themable elements and apply the themed styles to their style objects.
        //This way we can get the resulting cascaded style `themeStyle` (calculated above) and apply it to the
        //`style` property without overriding the options made by the user.
        for (j = 0, lmatches = results.length; j < lmatches; ++j) {
            res = results[j];
            res.style = Ext.applyIf(res.style || {}, res.themeStyle || {});
        }

        me.themeInitialized = true;
    }
});

;Ext.chart.theme.Base = [
    {
        "selector": "chart",
        "style": {
            "padding": 10,
            "colors": [
                "#115fa6",
                "#94ae0a",
                "#a61120",
                "#ff8809",
                "#ffd13e",
                "#a61187",
                "#24ad9a",
                "#7c7474",
                "#a66111"
            ]
        },
        "specificity": [
            0,
            0,
            1
        ]
    },
    {
        "selector": "chart axis",
        "style": {
            "color": "#354f6e",
            "fill": "#354f6e",
            "stroke": "#cccccc",
            "stroke-width": 1
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart axis label",
        "style": {
            "color": "#354f6e",
            "fill": "#354f6e",
            "font": "12px Helvetica, Arial, sans-serif",
            "font-weight": "bold",
            "spacing": 2,
            "padding": 5
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart axis title",
        "style": {
            "font": "18px Helvetica, Arial, sans-serif",
            "color": "#354f6e",
            "fill": "#354f6e",
            "padding": 5
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart axis[position=\"left\"] title",
        "style": {
            "rotate": {
                "x": 0,
                "y": 0,
                "degrees": 270
            }
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart axis[position=\"right\"] title",
        "style": {
            "rotate": {
                "x": 0,
                "y": 0,
                "degrees": 270
            }
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart axis[position=\"radial\"]",
        "style": {
            "fill": "none"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart axis[position=\"radial\"] label",
        "style": {
            "font": "10px Helvetica, Arial, sans-serif",
            "text-anchor": "middle"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart axis[position=\"gauge\"]",
        "style": {
            "fill": "none"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart axis[position=\"gauge\"] label",
        "style": {
            "font": "10px Helvetica, Arial, sans-serif",
            "text-anchor": "middle"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart series",
        "style": {
            "stroke-width": 1
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart series label",
        "style": {
            "font": "12px Helvetica, Arial, sans-serif",
            "fill": "#333333",
            "display": "none",
            "field": "name",
            "minMargin": "50",
            "orientation": "horizontal"
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart series:nth-child(1)",
        "style": {
            "fill": "#115fa6"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(2)",
        "style": {
            "fill": "#94ae0a"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(3)",
        "style": {
            "fill": "#a61120"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(4)",
        "style": {
            "fill": "#ff8809"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(5)",
        "style": {
            "fill": "#ffd13e"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(6)",
        "style": {
            "fill": "#a61187"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(7)",
        "style": {
            "fill": "#24ad9a"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(8)",
        "style": {
            "fill": "#7c7474"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:nth-child(9)",
        "style": {
            "fill": "#a66111"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series:highlight",
        "style": {
            "radius": 20,
            "stroke-width": 5,
            "stroke": "#ff5555"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart series[type=\"line\"]:highlight",
        "style": {
            "stroke-width": 3
        },
        "specificity": [
            0,
            2,
            2
        ]
    },
    {
        "selector": "chart series[type=\"bar\"]:highlight",
        "style": {
            "stroke-width": 3,
            "stroke": "#5555cc",
            "opacity": 0.8
        },
        "specificity": [
            0,
            2,
            2
        ]
    },
    {
        "selector": "chart series[type=\"area\"]:highlight",
        "style": {
            "stroke-width": 3,
            "stroke": "#111111"
        },
        "specificity": [
            0,
            2,
            2
        ]
    },
    {
        "selector": "chart series[type=\"pie\"]:highlight",
        "style": {
            "stroke": "none",
            "stroke-width": 0
        },
        "specificity": [
            0,
            2,
            2
        ]
    },
    {
        "selector": "chart series[type=\"scatter\"]:highlight",
        "style": {
            "stroke": "none",
            "stroke-width": 0
        },
        "specificity": [
            0,
            2,
            2
        ]
    },
    {
        "selector": "chart marker",
        "style": {
            "stroke": "#ffffff",
            "stroke-width": 1,
            "type": "circle",
            "fill": "#000000",
            "radius": 5,
            "size": 5
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(1)",
        "style": {
            "fill": "#115fa6",
            "type": "circle"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(2)",
        "style": {
            "fill": "#94ae0a"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(3)",
        "style": {
            "fill": "#a61120"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(3)",
        "style": {
            "fill": "#a61120"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(4)",
        "style": {
            "fill": "#ff8809"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(5)",
        "style": {
            "fill": "#ffd13e"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(6)",
        "style": {
            "fill": "#a61187"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(7)",
        "style": {
            "fill": "#24ad9a"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(8)",
        "style": {
            "fill": "#7c7474"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart marker:nth-child(9)",
        "style": {
            "fill": "#a66111"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart interaction[type=\"itemcompare\"] circle",
        "style": {
            "fill": "rgba(0, 0, 0, 0)",
            "stroke": "#0d75f2",
            "radius": 5
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart interaction[type=\"itemcompare\"] line",
        "style": {
            "stroke": "#0d75f2",
            "stroke-width": 3
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart interaction[type=\"itemcompare\"] arrow",
        "style": {
            "fill": "#0d75f2",
            "radius": 8
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart interaction[type=\"piegrouping\"] slice",
        "style": {
            "stroke": "#0d75f2",
            "stroke-width": 2,
            "fill": "#0d75f2",
            "opacity": 0.5
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart interaction[type=\"piegrouping\"] handle",
        "style": {
            "stroke": "#0d75f2",
            "stroke-width": 2,
            "fill": "#0d75f2"
        },
        "specificity": [
            0,
            1,
            3
        ]
    }
];;Ext.chart.theme.Demo = [
    {
        "selector": "chart[cls=\"area1\"] axis[position=\"left\"] grid even",
        "style": {
            "opacity": 1,
            "fill": "#dddddd",
            "stroke": "#bbbbbb",
            "stroke-width": 1
        },
        "specificity": [
            0,
            2,
            4
        ]
    },
    {
        "selector": "chart[cls=\"area1\"] axis[position=\"bottom\"] label",
        "style": {
            "rotate": {
                "degrees": 45
            }
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"area1\"] series",
        "style": {
            "opaciy": "0.93"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"bar1\"] axis[position=\"bottom\"] grid",
        "style": {
            "stroke": "#cccccc"
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"column1\"]",
        "style": {
            "background": "#111111"
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] axis",
        "style": {
            "stroke": "#eeeeee",
            "fill": "#eeeeee"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] axis label",
        "style": {
            "fill": "#ffffff"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] axis title",
        "style": {
            "fill": "#ffffff"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] axis[position=\"left\"] grid odd",
        "style": {
            "stroke": "#555555"
        },
        "specificity": [
            0,
            2,
            4
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] axis[position=\"left\"] grid even",
        "style": {
            "stroke": "#555555"
        },
        "specificity": [
            0,
            2,
            4
        ]
    },
    {
        "selector": "chart[cls=\"column1\"] series label",
        "style": {
            "fill": "#ffffff",
            "font": "17px Arial",
            "display": "insideEnd",
            "text-anchor": "middle",
            "orientation": "horizontal"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"barcombo1\"] axis[position=\"bottom\"] grid",
        "style": {
            "stroke": "#cccccc"
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"piecombo1\"]",
        "style": {
            "padding": 20
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"piecombo1\"] series label",
        "style": {
            "display": "rotate",
            "contrast": true,
            "font": "14px Arial"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"gaugecombo1\"]",
        "style": {
            "padding": 30
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"gaugecombo1\"] axis",
        "style": {
            "stroke": "#cccccc"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"gaugecombo1\"] axis label",
        "style": {
            "font": "15px Arial"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"radarcombo1\"]",
        "style": {
            "padding": 20
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"radarcombo1\"] axis",
        "style": {
            "stroke": "#cccccc",
            "fill": "none"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"radarcombo1\"] axis label",
        "style": {
            "font": "11px Arial",
            "text-anchor": "middle"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"radarcombo1\"] series",
        "style": {
            "opacity": 0.4
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"line1\"] axis[position=\"left\"] grid odd",
        "style": {
            "opacity": 1,
            "fill": "#dddddd",
            "stroke": "#bbbbbb",
            "stroke-width": 0.5
        },
        "specificity": [
            0,
            2,
            4
        ]
    },
    {
        "selector": "chart[cls=\"line1\"] marker",
        "style": {
            "size": 4,
            "radius": 4,
            "stroke-width": 0
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"line1\"] series:nth-child(1) marker",
        "style": {
            "type": "image",
            "height": "46",
            "width": "46",
            "src": "\"../resources/shared/img/iphone.png\""
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"line1\"] series:nth-child(2) marker",
        "style": {
            "type": "image",
            "height": "46",
            "width": "46",
            "src": "\"../resources/shared/img/android.png\""
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"line1\"] series:nth-child(3) marker",
        "style": {
            "type": "image",
            "height": "46",
            "width": "46",
            "src": "\"../resources/shared/img/ipad.png\""
        },
        "specificity": [
            0,
            2,
            3
        ]
    },
    {
        "selector": "chart[cls=\"pie1\"]",
        "style": {
            "padding": 10
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"pie1\"] series label",
        "style": {
            "display": "rotate",
            "contrast": true,
            "font": "18px Helvetica, Arial, sans-serif"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"radar1\"]",
        "style": {
            "padding": 20
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"radar1\"] axis",
        "style": {
            "stroke": "#cccccc",
            "fill": "none"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"radar1\"] axis label",
        "style": {
            "font": "11px Arial",
            "text-anchor": "middle"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"radar1\"] series",
        "style": {
            "opacity": 0.4
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"scatter1\"]",
        "style": {
            "padding": 40
        },
        "specificity": [
            0,
            1,
            1
        ]
    },
    {
        "selector": "chart[cls=\"scatter1\"] axis[position=\"left\"] grid odd",
        "style": {
            "opacity": 1,
            "fill": "#dddddd",
            "stroke": "#bbbbbb",
            "stroke-width": 0.5
        },
        "specificity": [
            0,
            2,
            4
        ]
    },
    {
        "selector": "chart[cls=\"scatter1\"] marker",
        "style": {
            "size": 8,
            "radius": 8
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart[cls=\"stock1\"] axis label",
        "style": {
            "font": "12px Arial"
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"stock1\"] axis[position=\"left\"] grid",
        "style": {
            "stroke": "#cccccc"
        },
        "specificity": [
            0,
            2,
            3
        ]
    }
];;Ext.chart.theme.Energy = [
    {
        "selector": "chart",
        "style": {
            "colors": [
                "rgba(17, 95, 166, 0.85)",
                "rgba(148, 174, 10, 0.85)",
                "rgba(166, 17, 32, 0.85)",
                "rgba(255, 136, 9, 0.85)",
                "rgba(255, 209, 62, 0.85)",
                "rgba(166, 17, 135, 0.85)",
                "rgba(36, 173, 154, 0.85)",
                "rgba(124, 116, 116, 0.85)",
                "rgba(166, 97, 17, 0.85)"
            ]
        },
        "specificity": [
            0,
            0,
            1
        ]
    },
    {
        "selector": "chart series",
        "style": {
            "stroke-width": 2
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart series grid odd",
        "style": {
            "stroke": "#333333"
        },
        "specificity": [
            0,
            0,
            4
        ]
    },
    {
        "selector": "chart series grid even",
        "style": {
            "stroke": "#222222"
        },
        "specificity": [
            0,
            0,
            4
        ]
    },
    {
        "selector": "chart axis",
        "style": {
            "stroke": "#555555",
            "fill": "#555555"
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart axis label",
        "style": {
            "fill": "#666666"
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart axis title",
        "style": {
            "fill": "#cccccc"
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart axis[position=\"radial\"]",
        "style": {
            "fill": "none"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart axis[position=\"radial\"] label",
        "style": {
            "fill": "#ffffff",
            "text-anchor": "center",
            "translate": {
                "x": 0,
                "y": -10
            }
        },
        "specificity": [
            0,
            1,
            3
        ]
    },
    {
        "selector": "chart[cls=\"radar\"]",
        "style": {
            "padding": 40
        },
        "specificity": [
            0,
            1,
            1
        ]
    }
];;Ext.chart.theme.WorldData = [
    {
        "selector": "chart",
        "style": {
            "colors": [
                "#49080e",
                "#49080e",
                "#d7a400"
            ],
            "background": "#dbddd8"
        },
        "specificity": [
            0,
            0,
            1
        ]
    },
    {
        "selector": "chart series:highlight",
        "style": {
            "radius": 5,
            "stroke-width": 3,
            "stroke": "#ffffff"
        },
        "specificity": [
            0,
            1,
            2
        ]
    },
    {
        "selector": "chart axis",
        "style": {
            "stroke": "#c2c4be",
            "fill": "#c2c4be"
        },
        "specificity": [
            0,
            0,
            2
        ]
    },
    {
        "selector": "chart axis label",
        "style": {
            "fill": "#909488"
        },
        "specificity": [
            0,
            0,
            3
        ]
    },
    {
        "selector": "chart axis title",
        "style": {
            "fill": "#43453e"
        },
        "specificity": [
            0,
            0,
            3
        ]
    }
];


/**
 * @class Ext.chart.theme.Style
 * @ignore
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.Style = Ext.extend(Object, {
    
    constructor: function(config) {
        this.style = {};
        this.themeStyle = {};
        
        Ext.apply(this.style, config); 
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    //filled by the constructor.
    ownerCt: null,
    
    getItemId: function() {
        return this.el && this.el.id || this.id || null;
    },
    
    initCls: function() {
        return (this.cls || '').split(' ');
    },
    
    isXType: function(xtype) {
        return xtype === '';
    },
    
    getRefItems: function(deep) {
        return [];
    }    
});

/**
 * @class Ext.chart.theme.LabelStyle
 * @ignore
 *
 * @xtype label
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.LabelStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.LabelStyle.superclass.constructor.call(this, config);    
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'label';
    }
});

/**
 * @class Ext.chart.theme.HighlightStyle
 * @ignore
 *
 * @xtype marker
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.HighlightStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.HighlightStyle.superclass.constructor.call(this, config);
        this.style  = false;
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'highlight';
    },
    
    getRefItems: function(deep) {
        return [];
    }
});




/**
 * @class Ext.chart.theme.MarkerStyle
 * @ignore
 *
 * @xtype marker
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.MarkerStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.MarkerStyle.superclass.constructor.call(this, config);    
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'marker';
    }
});

/**
 * @class Ext.chart.theme.TitleStyle
 * @ignore
 *
 * @xtype title
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.TitleStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.TitleStyle.superclass.constructor.call(this, config);
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'title';
    }
});

/**
 * @class Ext.chart.theme.CalloutStyle
 * @ignore
 *
 * @xtype marker
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.CalloutStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.CalloutStyle.superclass.constructor.call(this, config);
        this.style  = false;
        this.oddStyle = new Ext.chart.theme.OddStyle();
        this.evenStyle = new Ext.chart.theme.EvenStyle();
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'callout';
    },
    
    getRefItems: function(deep) {
        return [];
    }
});



/**
 * @class Ext.chart.theme.GridStyle
 * @ignore
 *
 * @xtype marker
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.GridStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.GridStyle.superclass.constructor.call(this, config);
        this.style  = false;
        this.oddStyle = new Ext.chart.theme.OddStyle();
        this.evenStyle = new Ext.chart.theme.EvenStyle();
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'grid';
    },
    
    getRefItems: function(deep) {
        return [ this.oddStyle, this.evenStyle ];
    }
});


/**
 * @class Ext.chart.theme.EvenStyle
 * @ignore
 *
 * @xtype even
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.EvenStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.EvenStyle.superclass.constructor.call(this, config);
        this.style = false;
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'even';
    }
});



/**
 * @class Ext.chart.theme.OddStyle
 * @ignore
 *
 * @xtype odd
 */
Ext.ns('Ext.chart.theme');

Ext.chart.theme.OddStyle = Ext.extend(Ext.chart.theme.Style, {
    
    constructor: function(config) {
        Ext.chart.theme.OddStyle.superclass.constructor.call(this, config);
        this.style = false;
    },
    
    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/
    
    isXType: function(xtype) {
        return xtype === 'odd';
    }
});


/**
 * @class Ext.chart.Chart
 * @extends Ext.draw.Component
 *
 * The Ext.chart package provides the capability to visualize data.
 * Each chart binds directly to an Ext.data.Store enabling automatic updates of the chart.
 * A chart configuration object has some overall styling options as well as an array of axes
 * and series. A chart instance example could look like:
 *
  <pre><code>
    new Ext.chart.Chart({
        renderTo: Ext.getBody(),
        width: 800,
        height: 600,
        animate: true,
        store: store1,
        shadow: true,
        theme: 'Category1',
        legend: {
            position: 'right'
        },
        axes: [ ...some axes options... ],
        series: [ ...some series options... ]
    });
  </code></pre>
 *
 * In this example we set the `width` and `height` of the chart, we decide whether our series are
 * animated or not and we select a store to be bound to the chart. We also turn on shadows for all series,
 * select a color theme `Category1` for coloring the series, set the legend to the right part of the chart and
 * then tell the chart to render itself in the body element of the document. For more information about the axes and
 * series configurations please check the documentation of each series (Line, Bar, Pie, etc).
 *
 * @xtype chart
 */

Ext.chart.Chart = Ext.extend(Ext.draw.Component, {

    /**
     * @property version Current Version of Touch Charts
     * @type {String}
     */
    version : '1.0.0',

    // @private
    viewBox: false,

    /**
     * @cfg {String} theme (optional) The name of the theme to be used. A theme defines the colors and
     * other visual displays of tick marks on axis, text, title text, line colors, marker colors and styles, etc.
     * Possible theme values are 'Base', 'Green', 'Sky', 'Red', 'Purple', 'Blue', 'Yellow' and also six category themes
     * 'Category1' to 'Category6'. Default value is 'Base'.
     */

    /**
     * @cfg {Boolean/Object} shadow (optional) true for the default shadow configuration (shadowOffsetX: 2, shadowOffsetY: 2, shadowBlur: 3, shadowColor: '#444')
     * or a standard shadow config object to be used for default chart shadows. Defaults to false.
     */

    /**
     * @cfg {Boolean/Object} animate (optional) true for the default animation (easing: 'ease' and duration: 500)
     * or a standard animation config object to be used for default chart animations. Defaults to false.
     */
    animate: false,

    /**
     * @cfg {Boolean/Object} legend (optional) true for the default legend display or a legend config object. Defaults to false.
     */
    legend: false,

    /**
     * @cfg {integer} insetPadding (optional) Set the amount of inset padding in pixels for the chart. Defaults to 10.
     */

    /**
     * @cfg {Object|Boolean} background (optional) Set the chart background. This can be a gradient object, image, or color.
     * Defaults to false for no background.
     *
     * For example, if `background` were to be a color we could set the object as
     *
     <pre><code>
        background: {
            //color string
            fill: '#ccc'
        }
     </code></pre>

     You can specify an image by using:

     <pre><code>
        background: {
            image: 'http://path.to.image/'
        }
     </code></pre>

     Also you can specify a gradient by using the gradient object syntax:

     <pre><code>
        background: {
            gradient: {
                id: 'gradientId',
                angle: 45,
                stops: {
                    0: {
                        color: '#555'
                    }
                    100: {
                        color: '#ddd'
                    }
                }
            }
        }
     </code></pre>
     */
    background: false,


    /**
     * @cfg {Array} interactions
     * Interactions are optional modules that can be plugged in to a chart to allow the user to interact
     * with the chart and its data in special ways. The `interactions` config takes an Array of Object
     * configurations, each one corresponding to a particular interaction class identified by a `type` property:
     *
     *     new Ext.chart.Chart({
     *         renderTo: Ext.getBody(),
     *         width: 800,
     *         height: 600,
     *         store: store1,
     *         axes: [ ...some axes options... ],
     *         series: [ ...some series options... ],
     *         interactions: [{
     *             type: 'interactiontype'
     *             // ...additional configs for the interaction...
     *         }]
     *     });
     *
     * When adding an interaction which uses only its default configuration (no extra properties other than `type`),
     * you can alternately specify only the type as a String rather than the full Object:
     *
     *     interactions: ['reset', 'rotate']
     *
     * The current supported interaction types include:
     *
     * - {@link Ext.chart.interactions.PanZoom panzoom} - allows pan and zoom of axes
     * - {@link Ext.chart.interactions.ItemCompare itemcompare} - allows selection and comparison of two data points
     * - {@link Ext.chart.interactions.ItemHighlight itemhighlight} - allows highlighting of series data points
     * - {@link Ext.chart.interactions.ItemInfo iteminfo} - allows displaying details of a data point in a popup panel
     * - {@link Ext.chart.interactions.PieGrouping piegrouping} - allows selection of multiple consecutive pie slices
     * - {@link Ext.chart.interactions.Rotate rotate} - allows rotation of pie and radar series
     * - {@link Ext.chart.interactions.Reset reset} - allows resetting of all user interactions to the default state
     * - {@link Ext.chart.interactions.ToggleStacked togglestacked} - allows toggling a multi-yField bar/column chart between stacked and grouped
     *
     * See the documentation for each of those interaction classes to see how they can be configured.
     *
     * Additional custom interactions can be registered with the {@link Ext.chart.interactions.Manager interaction manager}.
     */



    /**
     * @cfg {Object} toolbar
     * Optional configuration for this chart's toolbar. The toolbar docks itself to one side of the chart
     * and can contain buttons for handling certain actions. For example, if the chart legend is configured
     * with {@link Ext.chart.Legend#dock dock:true} then a button for bringing up the legend will be placed
     * in this toolbar. Custom may also be added to the toolbar if desired.
     *
     * See the {@link Ext.chart.Toolbar} docs for the recognized config properties.
     */

    /**
     * @private The z-indexes to use for the various surfaces
     */
    surfaceZIndexes: {
        main: 0,
        axis: 10,
        series: 20,
        overlay: 30,
        events: 40
    },

    /**
     * @cfg {Ext.data.Store} store
     * The store that supplies data to this chart.
     */

    /**
     * @cfg {[Ext.chart.series.Series]} series
     * Array of {@link Ext.chart.series.Series Series} instances or config objects. For example:
     *
     * series: [{
     *      type: 'column',
     *      axis: 'left',
     *      listeners: {
     *          'afterrender': function() {
     *              console('afterrender');
     *          }
     *      },
     *      xField: 'category',
     *      yField: 'data1'
     * }]
     */

    /**
     * @cfg {[Ext.chart.axis.Axis]} axes
     * Array of {@link Ext.chart.axis.Axis Axis} instances or config objects. For example:
     *
     * axes: [{
     *      type: 'Numeric',
     *      position: 'left',
     *      fields: ['data1'],
     *      title: 'Number of Hits',
     *      minimum: 0,
     *      //one minor tick between two major ticks
     *      minorTickSteps: 1
     * }, {
     *      type: 'Category',
     *      position: 'bottom',
     *      fields: ['name'],
     *      title: 'Month of the Year'
     * }]
     */


    constructor: function(config) {
        var me = this,
            defaultAnim;

        config = Ext.apply({}, config);
        if (me.gradients) {
            Ext.apply(config, { gradients: me.gradients });
        }
        if (me.background) {
            Ext.apply(config, { background: me.background });
        }
        if (config.animate) {
            defaultAnim = {
                easing: 'ease',
                duration: 500
            };
            if (Ext.isObject(config.animate)) {
                config.animate = Ext.applyIf(config.animate, defaultAnim);
            }
            else {
                config.animate = defaultAnim;
            }
        }
        Ext.chart.Chart.superclass.constructor.apply(this, [config]);
    },

    initComponent: function() {
        var me = this,
            axes, series, interactions;

        delete me.legend; //remove legend config from chart

        Ext.chart.Chart.superclass.initComponent.call(this);

        me.addEvents(
            /**
             * @event beforerefresh
             * Fires before a refresh to the chart data is called.  If the beforerefresh handler returns
             * <tt>false</tt> the {@link #refresh} action will be cancelled.
             * @param {Ext.chart.Chart} this
             */
            'beforerefresh',
            /**
             * @event refresh
             * Fires after the chart data has been refreshed.
             * @param {Ext.chart.Chart} this
             */
            'refresh',
            /**
             * @event redraw
             * Fires after the chart is redrawn
             * @param {Ext.chart.Chart} this
             */
            'redraw'

            /**
             * @event itemmousemove
             * Fires when the mouse is moved on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemmouseup
             * Fires when a mouseup event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemmousedown
             * Fires when a mousedown event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemmouseover
             * Fires when the mouse enters a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemmouseout
             * Fires when the mouse exits a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemclick
             * Fires when a click event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemdoubleclick
             * Fires when a doubleclick event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtap
             * Fires when a tap event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtapstart
             * Fires when a tapstart event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtapend
             * Fires when a tapend event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtapcancel
             * Fires when a tapcancel event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtaphold
             * Fires when a taphold event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemdoubletap
             * Fires when a doubletap event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemsingletap
             * Fires when a singletap event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtouchstart
             * Fires when a touchstart event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtouchmove
             * Fires when a touchmove event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemtouchend
             * Fires when a touchend event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemdragstart
             * Fires when a dragstart event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemdrag
             * Fires when a drag event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemdragend
             * Fires when a dragend event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itempinchstart
             * Fires when a pinchstart event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itempinch
             * Fires when a pinch event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itempinchend
             * Fires when a pinchend event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
            /**
             * @event itemswipe
             * Fires when a swipe event occurs on a series item.
             * @param {Ext.chart.series.Series} series
             * @param {Object} item
             * @param {Event} event
             */
        );

        // Borrow events from Series/ItemEvents so they can bubble up to the chart (doc'd above):
        me.addEvents.apply(me, Ext.chart.series.ItemEvents.itemEventNames);

        Ext.applyIf(me, {
            zoom: {
                width: 1,
                height: 1,
                x: 0,
                y: 0
            }
        });
        me.maxGutter = [0, 0];
        axes = me.axes;
        me.on('activate', me.onActivate, me);
        me.axes = new Ext.util.MixedCollection(false, function(a) { return a.position; });
        if (axes) {
            me.axes.addAll(axes);
        }
        series = me.series;
        me.series = new Ext.util.MixedCollection(false, function(a) { return a.seriesId || (a.seriesId = Ext.id(null, 'ext-chart-series-')); });
        if (series) {
            me.series.addAll(series);
        }
        interactions = me.interactions;
        me.interactions = new Ext.util.MixedCollection(false, function(a) { return a.type; });
        if (interactions) {
            Ext.each(interactions, me.addInteraction, me);
        }
    },

    onActivate: function() {
        if (this.dirtyStore) {
            this.redraw();
        }
    },

    getEventsSurface: function() {
        return this.getSurface('events');
    },

    initEvents: function() {
        Ext.chart.Chart.superclass.initEvents.call(this);
        this.interactions.each(function(interaction) {
            interaction.initEvents();
        });
    },

    getSurface: function(name) {
        var me = this,
            surfaces = me.surfaces || (me.surfaces = {main: me.surface}),
            surface = surfaces[name],
            zIndexes = me.surfaceZIndexes,
            el;
        if (!surface) {
            surface = surfaces[name] = me.createSurface({
                background: null,
                initEvents: (name == 'events')
            });
            el = surface.el;
            el.setStyle('position', 'absolute');
            el.setStyle('zIndex', 10);

            // Apply z-index if surface name is in the surfaceZIndexes mapping
            if (name in zIndexes) {
                el.setStyle('zIndex', zIndexes[name]);
            }
        }
        return surface;
    },

    /**
     * Retrieves a reference to the {@link Ext.chart.Toolbar} for this chart, creating it first
     * if necessary.
     * @return {Ext.chart.Toolbar}
     */
    getToolbar: function() {
        var me = this,
            toolbar = me.toolbar;
        if (!toolbar || !toolbar.isChartToolbar) {
            toolbar = me.toolbar = new Ext.chart.Toolbar(Ext.applyIf({chart: me}, toolbar));
        }
        return toolbar;
    },

    // @private overrides the component method to set the correct dimensions to the chart.
    doComponentLayout: function(width, height) {
        var me = this,
            eventSurface;
        if (Ext.isNumber(width) && Ext.isNumber(height) && (me.dirtyStore ||
            (width !== me.curWidth || height !== me.curHeight))) {
            // Layouts in Touch 1.x aren't optimal, cache the previous size so we don't redraw so much.
            me.curWidth = width;
            me.curHeight = height;

            // Update surfaces to match size
            me.getSurface('main').setSize(width, height);
            eventSurface = me.getEventsSurface();
            eventSurface.setSize(width, height);
            eventSurface.el.setTopLeft(0, 0);

            if (me.store) {
                me.redraw(true);
            }
        }
        Ext.chart.Chart.superclass.doComponentLayout.apply(this, arguments);
    },

    /**
     * Redraw the chart. If animations are set this will animate the chart too.
     * @param {Boolean} resize (optional) flag which changes the default origin points of the chart for animations.
     */
    redraw: function(resize) {
        var me = this,
            p, legend, toolbar, i, l, colors, color, colorArrayStyle, callback;

        me.dirtyStore = false;
        me.chartBBox = {
            x: 0,
            y: 0,
            height: me.curHeight,
            width: me.curWidth
        };
        me.colorArrayStyle = me.colorArrayStyle || [];

        me.series.each(me.initializeSeries, me);
        me.axes.each(me.initializeAxis, me);

        if (!me.themeInitialized) {
            // Apply styles from stylesheet.
            me.applyStyles();
            if (me.style && me.style.colors) {
                colors = me.style.colors;
                colorArrayStyle = me.colorArrayStyle;
                for (i = 0, l = colors.length; i < l; ++i) {
                    color = colors[i];
                    if (Ext.isObject(color)) {
                        for (p in me.surfaces) {
                            me.surfaces[p].addGradient(color);
                        }
                        colorArrayStyle.push('url(#' + color.id + ')');
                    } else {
                        colorArrayStyle.push(color);
                    }
                }
            } else {
                me.series.each(function(series, idx) {
                    me.colorArrayStyle[idx] = (series.style.fill || series.style.stroke || '#000');
                });
            }
            me.series.each(function(series) {
                series.colorArrayStyle = me.colorArrayStyle;
            });
            if (me.style && me.style.background) {
                colors = me.style.background;
                //a gradient object
                if (Ext.isObject(colors)) {
                    me.background = { gradient: colors };
                    me.surfaces.main.addGradient(colors);
                } else {
                    //an image
                    if (colors.indexOf('url') > -1) {
                        me.background = { image: colors };
                    //just a color
                    } else {
                        me.background = { fill: colors };
                    }
                }
                me.surfaces.main.initBackground(me.background);
            }
        }

        me.initializeLegend();
        legend = me.legend;
        if (legend) {
            legend.orient();
        }

        toolbar = me.toolbar;
        if (toolbar && toolbar.isChartToolbar) {
            toolbar.orient();
        }

        //process all views (aggregated data etc) on stores before rendering.
        me.axes.each(function(axis) {
            axis.processView();
        });
        me.axes.each(function(axis) {
            axis.drawAxis(true);
        });

        // Place axes properly, including influence from each other
        me.alignAxes();

        // Reposition legend based on new axis alignment
        if (legend) {
            legend.updatePosition();
        }

        // Find the max gutter
        me.getMaxGutter();

        // Draw axes and series
        me.resizing = !!resize;

        me.axes.each(me.drawAxis, me);
        me.series.each(me.drawCharts, me);

        Ext.iterate(me.surfaces, function(name, surface) {
            surface.renderFrame();
        });

        me.resizing = false;

        if (Ext.is.iPad) {
            Ext.repaint();
        }

        if (!me.interactionsInitialized) {
            me.interactionsInitialized = true;
            if (me.animate) {
                me.interactions.each(function(interaction) {
                    interaction.initializeDefaults({
                        type: 'beforerender'
                    });
                });

                //on after render callback should remove itself since it's
                //only called once.
                callback = function() {
                    me.interactions.each(function(interaction) {
                        interaction.initializeDefaults({
                            type: 'afterrender'
                        });
                    });
                    me.series.get(0).removeListener('afterrender', callback);
                };

                me.series.get(0).addListener('afterrender', callback);
            } else {
                me.interactions.each(function(interaction) {
                    interaction.initializeDefaults();
                });
            }
        }

        me.fireEvent('redraw', me);
    },

    // @private set the store after rendering the chart.
    afterRender: function() {
        var ref,
            me = this;
        Ext.chart.Chart.superclass.afterRender.call(this);

        if (me.categoryNames) {
            me.setCategoryNames(me.categoryNames);
        }

        if (me.tipRenderer) {
            ref = me.getFunctionRef(me.tipRenderer);
            me.setTipRenderer(ref.fn, ref.scope);
        }

        me.bindStore(me.store);
        me.refresh();
    },

    /**
     * @private
     * Return the x and y position of the given event relative to the chart's series area.
     */
    getEventXY: function(e) {
        e = (e.changedTouches && e.changedTouches[0]) || e.event || e.browserEvent || e;

        var me = this,
            chartXY = me.el.getXY(),
            chartBBox = me.chartBBox,
            x = e.pageX - chartXY[0] - chartBBox.x,
            y = e.pageY - chartXY[1] - chartBBox.y;

        return [x, y];
    },

    /**
     * Given an x/y point relative to the chart, find and return the first series item that
     * matches that point.
     * @param {Number} x
     * @param {Number} y
     * @return {Object} an object with `series` and `item` properties, or `false` if no item found
     */
    getItemForPoint: function(x, y) {
        var me = this,
            i = 0,
            items = me.series.items,
            l = items.length,
            series, item;

        for (; i < l; i++) {
            series = items[i];
            item = series.getItemForPoint(x, y);
            if (item) {
                return item;
            }
        }

        return false;
    },

    /**
     * Given an x/y point relative to the chart, find and return all series items that match that point.
     * @param {Number} x
     * @param {Number} y
     * @return {Array} an array of objects with `series` and `item` properties
     */
    getItemsForPoint: function(x, y) {
        var me = this,
            items = [];

        me.series.each(function(series) {
            var item = series.getItemForPoint(x, y);
            if (item) {
                items.push(item);
            }
        });

        return items;
    },

    capitalize: function(string) {
        return string.charAt(0).toUpperCase() + string.substr(1);
    },

    // @private buffered refresh for when we update the store
    delayRefresh: function() {
        var me = this;
        if (!me.refreshTask) {
            me.refreshTask = new Ext.util.DelayedTask(me.refresh, me);
        }
        me.refreshTask.delay(10);
    },

    // @private
    refresh: function() {
        var me = this,
            undef;

        me.dirtyStore = true;
        if (me.rendered && me.curWidth != undef && me.curHeight != undef && me.fireEvent('beforerefresh', me) !== false) {
            me.redraw();
            me.fireEvent('refresh', me);
        }
    },

    /**
     * Changes the data store bound to this chart and refreshes it.
     * @param {Ext.data.Store} store The store to bind to this chart
     */
    bindStore: function(store) {
        var me = this,
            currentStore = me.store,
            initial = !me.storeIsBound;

        store = Ext.StoreMgr.lookup(store);
        if (!initial && currentStore && store !== currentStore) {
            if (currentStore.autoDestroy) {
                currentStore.destroy();
            }
            else {
                currentStore.un({
                    scope: me,
                    datachanged: me.refresh,
                    add: me.delayRefresh,
                    remove: me.delayRefresh,
                    update: me.delayRefresh
                    //clear: me.refresh
                });
            }
        }
        if (store && (initial || store !== currentStore)) {
            store.on({
                scope: me,
                datachanged: me.refresh,
                add: me.delayRefresh,
                remove: me.delayRefresh,
                update: me.delayRefresh
                //clear: me.refresh
            });
        }
        me.store = store;
        me.storeIsBound = true;
        if (store && !initial) {
            me.refresh();
        }
    },

    /**
     * Adds an interaction to the chart.
     * @param {Object/String} interaction Either an instantiated {@link Ext.chart.interactions.Abstract}
     * instance, a configuration object for an interaction, or the interaction type as a String.
     */
    addInteraction: function(interaction) {
        if (Ext.isString(interaction)) {
            interaction = {type: interaction};
        }
        if (!interaction.chart) {
            interaction.chart = this;
            interaction = Ext.chart.interactions.Manager.create(interaction);
        }
        this.interactions.add(interaction);
    },

    // @private initialize the series.
    initializeLegend: function() {
        var me = this,
            legend = me.legend,
            legendConfig = me.initialConfig.legend;
        if (!legend && legendConfig) {
            legend = me.legend = new Ext.chart.Legend(Ext.apply({chart: me}, legendConfig));
            legend.on('combine', me.redraw, me);
            legend.on('split', me.redraw, me);
        }
    },

    // @private Create Axis
    initializeAxis: function(axis) {
        var me = this,
            chartBBox = me.chartBBox,
            w = chartBBox.width,
            h = chartBBox.height,
            x = chartBBox.x,
            y = chartBBox.y,
            config = {
                chart: me,
                ownerCt: me,
                x: 0,
                y: 0
            };

        switch (axis.position) {
            case 'top':
                Ext.apply(config, {
                    length: w,
                    width: h,
                    startX: x,
                    startY: y
                });
            break;
            case 'bottom':
                Ext.apply(config, {
                    length: w,
                    width: h,
                    startX: x,
                    startY: h
                });
            break;
            case 'left':
                Ext.apply(config, {
                    length: h,
                    width: w,
                    startX: x,
                    startY: h
                });
            break;
            case 'right':
                Ext.apply(config, {
                    length: h,
                    width: w,
                    startX: w,
                    startY: h
                });
            break;
        }
        if (!axis.chart) {
            Ext.apply(config, axis);
            axis = me.axes.replace(new Ext.chart.axis[this.capitalize(axis.type)](config));
        }
        else {
            Ext.apply(axis, config);
        }
    },


    /**
     * @private Adjust the dimensions and positions of each axis and the chart body area after accounting
     * for the space taken up on each side by the axes and legend.
     */
    alignAxes: function() {
        var me = this,
            axes = me.axes,
            legend = me.legend,
            edges = ['top', 'right', 'bottom', 'left'],
            chartBBox,
            //get padding from sass styling or property setting.
            insetPadding = me.insetPadding || +me.style.padding || 10,
            insets;

        //store the original configuration for insetPadding.
        if (Ext.isObject(insetPadding)) {
            me.insetPadding = Ext.apply({} ,insetPadding);
            insets = {
                top: insetPadding.top || 0,
                right: insetPadding.right || 0,
                bottom: insetPadding.bottom || 0,
                left: insetPadding.left || 0
            };
        } else {
            me.insetPadding = insetPadding;
            insets = {
                top: insetPadding,
                right: insetPadding,
                bottom: insetPadding,
                left: insetPadding
            };
        }

        me.insets = insets;

        function getAxis(edge) {
            var i = axes.findIndex('position', edge);
            return (i < 0) ? null : axes.getAt(i);
        }

        // Find the space needed by axes and legend as a positive inset from each edge
        Ext.each(edges, function(edge) {
            var isVertical = (edge === 'left' || edge === 'right'),
                axis = getAxis(edge),
                bbox;

            // Add legend size if it's on this edge
            if (legend !== false) {
                if (legend.getPosition() === edge) {
                    insets[edge] += legend.getInsetSize();
                }
            }

            // Add axis size if there's one on this edge only if it has been
            //drawn before.
            if (axis && axis.bbox) {
                bbox = axis.bbox;
                insets[edge] += (isVertical ? bbox.width : bbox.height);
            }
        });
        // Build the chart bbox based on the collected inset values
        chartBBox = {
            x: insets.left,
            y: insets.top,
            width: me.curWidth - insets.left - insets.right,
            height: me.curHeight - insets.top - insets.bottom
        };
        me.chartBBox = chartBBox;

        // Go back through each axis and set its size, position, and relative start point based on the
        // corresponding edge of the chartBBox
        axes.each(function(axis) {
            var pos = axis.position,
                axisBBox = axis.bbox || {width: 0, height: 0},
                isVertical = (pos === 'left' || pos === 'right');

            axis.x = (pos === 'left' ? chartBBox.x - axisBBox.width : chartBBox.x);
            axis.y = (pos === 'top' ? chartBBox.y - axisBBox.height : chartBBox.y);
            axis.width = (isVertical ? axisBBox.width + chartBBox.width: axisBBox.height + chartBBox.height);
            axis.length = (isVertical ? chartBBox.height : chartBBox.width);
            axis.startX = (isVertical ? (pos === 'left' ? axisBBox.width : chartBBox.width) : 0);
            axis.startY = (pos === 'top' ? axisBBox.height : chartBBox.height);
        });
    },

    // @private initialize the series.
    initializeSeries: function(series, idx) {
        var me = this,
            config = {
                chart: me,
                ownerCt: me,
                seriesId: series.seriesId,
                index: idx
            };

        if (series instanceof Ext.chart.series.Series) {
            Ext.apply(series, config);
        } else {
            Ext.applyIf(config, series);
            series = me.series.replace(new Ext.chart.series[me.capitalize(series.type)](config));
        }
        if (series.initialize) {
            series.initialize();
        }
    },

    // @private
    getMaxGutter: function() {
        var me = this,
            maxGutter = [0, 0];
        me.series.each(function(s) {
            var gutter = s.getGutters && s.getGutters() || [0, 0];
            maxGutter[0] = Math.max(maxGutter[0], gutter[0]);
            maxGutter[1] = Math.max(maxGutter[1], gutter[1]);
        });
        me.maxGutter = maxGutter;
    },

    // @private draw axis.
    drawAxis: function(axis) {
        axis.drawAxis();
    },

    // @private draw series.
    drawCharts: function(series) {
        series.drawSeries();
        if (!this.animate) {
            series.fireEvent('afterrender');
        }
    },

    /**
     * Reset the chart back to its initial state, before any user interaction.
     * @param {Boolean} skipRedraw if `true`, redrawing of the chart will be skipped.
     */
    reset: function(skipRedraw) {
        var me = this,
            legend = me.legend;

        me.axes.each(function(axis) {
            if (axis.reset) {
                axis.reset();
            }
        });

        me.series.each(function(series) {
            if (series.reset) {
                series.reset();
            }
        });

        if (legend && legend.reset) {
            legend.reset();
        }

        if (!skipRedraw) {
            me.redraw();
        }
    },

    // @private remove gently.
    destroy: function() {
        Ext.iterate(this.surfaces, function(name, surface) {
            surface.destroy();
        });
        this.bindStore(null);
        Ext.chart.Chart.superclass.destroy.apply(this, arguments);
    },

    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    ownerCt: null,

    getItemId: function() {
        return this.el && this.el.id || this.id || null;
    },

    initCls: function() {
        return (this.cls || '').split(' ');
    },

    isXType: function(xtype) {
        return xtype === 'chart';
    },

    getRefItems: function(deep) {
        var me = this,
            ans = [];

        me.series.each(function(series) {
            ans.push(series);
            if (deep) {
                if (series.markerStyle) {
                    ans.push(series.markerStyle);
                }
                if (series.labelStyle) {
                    ans.push(series.labelStyle);
                }
                if (series.calloutStyle) {
                    ans.push(series.calloutStyle);
                }
            }
        });

        me.axes.each(function(axis) {
            ans.push(axis);
            if (deep && axis.labelStyle) {
                ans.push(axis.labelStyle);
            }
            if (deep && axis.gridStyle) {
                ans.push(axis.gridStyle);
                ans.push(axis.gridStyle.oddStyle);
                ans.push(axis.gridStyle.evenStyle);
            }
        });

        me.interactions.each(function(interaction) {
            ans.push(interaction);
            if (deep) {
                ans = ans.concat(interaction.getRefItems(deep));
            }
        });

        return ans;
    }
});
Ext.applyIf(Ext.chart.Chart.prototype, Ext.chart.theme.Theme.prototype);
Ext.reg('chart', Ext.chart.Chart);

Ext.chart.Panel = Ext.extend(Ext.Panel, {

    defaultType: 'chart',
    layout: 'fit',

    constructor: function(config) {
        config.dockedItems = {
            xtype: 'panel',
            height: '2.6em',
            dock: 'top',
            layout: {
                type: 'card',
                align: 'stretch'
            },
            activeItem: 0,
            dockedItems: {
                dock: 'right',
                xtype: 'toolbar',
                ui: 'light',
                items: config.dockedItems
            },
            items: [{
                dock: 'top',
                xtype: 'toolbar',
                ui: 'light',
                title: config.title || ''
            }, {
                dock: 'top',
                xtype: 'toolbar',
                ui: 'light',
                title: ''
            }]
        };

        Ext.chart.Panel.superclass.constructor.call(this, config);
    },

    onRender: function() {
        var me = this,
            headerPanel;
        Ext.chart.Panel.superclass.onRender.apply(me, arguments);
        headerPanel = me.headerPanel = me.dockedItems.get(0);
        me.descriptionPanel = headerPanel.items.get(1);
    }
});

/**
 * @class Ext.chart.Callout
 * @ignore
 */
Ext.chart.Callout = Ext.extend(Object, {

    constructor: function(config) {
        var me = this;
        if (config.callouts) {
            config.callouts.styles = Ext.apply({}, config.callouts.styles || {});
            me.callouts = Ext.apply(me.callouts || {}, config.callouts);
            me.calloutsArray = [];
        }
        me.calloutStyle = new Ext.chart.theme.CalloutStyle();
    },

    renderCallouts: function() {
        if (!this.callouts) {
            return;
        }

        var me = this,
            items = me.items,
            animate = me.chart.animate,
            config = me.callouts,
            styles = config.styles,
            group = me.calloutsArray,
            store = me.chart.store,
            len = store.getCount(),
            ratio = items.length / len,
            previouslyPlacedCallouts = [],
            i, count, j, p;

        for (i = 0, count = 0; i < len; i++) {
            for (j = 0; j < ratio; j++) {
                var item = items[count],
                    label = group[count],
                    storeItem = store.getAt(i),
                    display;

                display = ((item && item.useCallout) || config.filter(storeItem, item, i, display, j, count)) && (Math.abs(item.endAngle - item.startAngle) > 0.8);

                if (!display && !label) {
                    count++;
                    continue;
                }

                if (!label) {
                    group[count] = label = me.onCreateCallout(storeItem, item, i, display, j, count);
                }
                for (p in label) {
                    if (label[p] && label[p].setAttributes) {
                        label[p].setAttributes(styles, true);
                    }
                }
                if (!display) {
                    for (p in label) {
                        if (label[p]) {
                            if (label[p].setAttributes) {
                                label[p].setAttributes({
                                    hidden: true
                                }, true);
                            } else if(label[p].setVisible) {
                                label[p].setVisible(false);
                            }
                        }
                    }
                }
                config.renderer(label, storeItem);

                if (display) {
                    me.onPlaceCallout(label, storeItem, item, i, display, animate,
                                      j, count, previouslyPlacedCallouts);
                }
                previouslyPlacedCallouts.push(label);
                count++;
            }
        }
        this.hideCallouts(count);
    },

    onCreateCallout: function(storeItem, item, i, display) {
        var me = this,
            config = me.callouts,
            styles = config.styles,
            width = styles.width || 100,
            height = styles.height || 100,
            surface = me.getSurface(),
            calloutObj = {
                label: false,
                box: false,
                lines: false
            };

        calloutObj.lines = surface.add(Ext.apply({}, {
            type: 'path',
            path: 'M0,0',
            stroke: me.getLegendColor(i) || '#555'
        }, config.lines || {}));

        calloutObj.box = surface.add(Ext.apply({
            type: 'rect',
            width: width,
            height: height
        }, config.box || {}));

        calloutObj.label = surface.add(Ext.apply({
            type: 'text',
            text: 'some text'
        }, config.label || {}));

        return calloutObj;
    },

    hideCallouts: function(index) {
        var calloutsArray = this.calloutsArray,
            len = calloutsArray.length,
            co, p;
        while (len-->index) {
            co = calloutsArray[len];
            for (p in co) {
                if (co[p]) {
                    co[p].hide(true);
                }
            }
        }
    },

    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    //filled by the constructor.
    ownerCt: null,

    getItemId: function() {
        return this.el && this.el.id || this.id || null;
    },

    initCls: function() {
        return (this.cls || '').split(' ');
    },

    isXType: function(xtype) {
        return xtype === 'callout';
    },

    getRefItems: function(deep) {
        return [];
    }
});

/**
 * @class Ext.chart.Highlight
 * @ignore
 */
Ext.chart.Highlight = Ext.extend(Object, {

    /**
     * Specifies whether this series should respond to highlighting, and optionally specifies custom
     * attributes for the highlighting effect. Only used if the
     * {@link Ext.chart.interactions.ItemHighlight itemhighlight} interaction is configured.
     * Defaults to `true` which uses a default highlighting effect for the series items; set it to
     * `false` to disable highlighting entirely on this series, or to an object with style properties
     * (i.e fill, stroke, radius) to customize the highlighting effect.
     */
    highlight: true,

    /**
     * @cfg {Number} highlightDuration
     * The duration for the highlight effect in milliseconds. Default's 150
     */
    highlightDuration: 150,

    highlightCfg : null,

    constructor: function(config) {
        if (config.highlight !== false) {
            if (config.highlight !== true) { //is an object
                this.highlightCfg = Ext.apply({}, config.highlight);
            }
            else {
                this.highlightCfg = {};
            }
            this.addEvents('highlight', 'unhighlight');
            this.highlightStyle = new Ext.chart.theme.HighlightStyle();
        }
    },

    /**
     * Highlight the given series item.
     * @param {Object} item Info about the item; same format as returned by #getItemForPoint.
     */
    highlightItem: function(item) {
        if (!item) {
            return;
        }

        var me = this,
            sprite = item.sprite,
            opts = me.highlightCfg,
            surface = me.chart.surface,
            animate = me.chart.animate,
            p, from, to, pi;

        if (me.highlight === false || !sprite || sprite._highlighted) {
            return;
        }
        //make sure we apply the stylesheet styles.
        Ext.applyIf(me.highlightCfg, me.highlightStyle.style || {});
        
        if (sprite._anim) {
            sprite._anim.paused = true;
        }
        sprite._highlighted = true;
        if (!sprite._defaults) {
            sprite._defaults = Ext.apply({}, sprite.attr);
            from = {};
            to = {};
            for (p in opts) {
                if (! (p in sprite._defaults)) {
                    sprite._defaults[p] = surface.attributeDefaults[surface.attributeMap[p]];
                }
                from[p] = sprite._defaults[p];
                to[p] = opts[p];
                if (Ext.isObject(opts[p])) {
                    from[p] = {};
                    to[p] = {};
                    Ext.apply(sprite._defaults[p], sprite.attr[p]);
                    Ext.apply(from[p], sprite._defaults[p]);
                    for (pi in sprite._defaults[p]) {
                        if (! (pi in opts[p])) {
                            to[p][pi] = from[p][pi];
                        } else {
                            to[p][pi] = opts[p][pi];
                        }
                    }
                    for (pi in opts[p]) {
                        if (! (pi in to[p])) {
                            to[p][pi] = opts[p][pi];
                        }
                    }
                }
            }
            sprite._from = from;
            sprite._to = to;
            sprite._endStyle = to;
        }
        if (animate) {
            sprite._anim = new Ext.fx.Anim({
                target: sprite,
                from: sprite._from,
                to: sprite._to,
                duration: me.highlightDuration || 150
            });
        } else {
            sprite.setAttributes(sprite._to, true);
        }

        me.fireEvent('highlight', item);
    },

    /**
     * Un-highlight any existing highlights
     */
    unHighlightItem: function() {
        if (this.highlight === false || !this.items) {
            return;
        }

        var me = this,
            items = me.items,
            len = items.length,
            opts = me.highlightCfg,
            animate = me.chart.animate,
            i = 0,
            obj, p, sprite;

        for (; i < len; i++) {
            if (!items[i]) {
                continue;
            }
            sprite = items[i].sprite;
            if (sprite && sprite._highlighted) {
                if (sprite._anim) {
                    sprite._anim.paused = true;
                }
                obj = {};
                for (p in opts) {
                    if (Ext.isObject(sprite._defaults[p])) {
                        obj[p] = {};
                        Ext.apply(obj[p], sprite._defaults[p]);
                    }
                    else {
                        obj[p] = sprite._defaults[p];
                    }
                }
                if (animate) {
                    //sprite._to = obj;
                    sprite._endStyle = obj;
                    sprite._anim = new Ext.fx.Anim({
                        target: sprite,
                        to: obj,
                        duration: me.highlightDuration || 150
                    });
                }
                else {
                    sprite.setAttributes(obj, true);
                }
                delete sprite._highlighted;
                //delete sprite._defaults;
            }
        }

        me.fireEvent('unhighlight');
    },

    cleanHighlights: function() {
        if (this.highlight === false) {
            return;
        }

        var group = this.group,
            markerGroup = this.markerGroup,
            i = 0,
            l;
        for (l = group.getCount(); i < l; i++) {
            delete group.getAt(i)._defaults;
        }
        if (markerGroup) {
            for (l = markerGroup.getCount(); i < l; i++) {
                delete markerGroup.getAt(i)._defaults;
            }
        }
    }
});

/**
 * @class Ext.chart.Label
 *
 * Labels is a mixin whose methods are appended onto the Series class. Labels is an interface with methods implemented
 * in each of the Series (Pie, Bar, etc) for label creation and label placement.
 *
 * The methods implemented by the Series are:
 *
 * - **`onCreateLabel(storeItem, item, i, display)`** Called each time a new label is created.
 *   The arguments of the method are:
 *   - *`storeItem`* The element of the store that is related to the label sprite.
 *   - *`item`* The item related to the label sprite. An item is an object containing the position of the shape
 *     used to describe the visualization and also pointing to the actual shape (circle, rectangle, path, etc).
 *   - *`i`* The index of the element created (i.e the first created label, second created label, etc)
 *   - *`display`* The display type. May be <b>false</b> if the label is hidden
 *
 *  - **`onPlaceLabel(label, storeItem, item, i, display, animate)`** Called for updating the position of the label.
 *    The arguments of the method are:
 *    - *`label`* The sprite label.</li>
 *    - *`storeItem`* The element of the store that is related to the label sprite</li>
 *    - *`item`* The item related to the label sprite. An item is an object containing the position of the shape
 *      used to describe the visualization and also pointing to the actual shape (circle, rectangle, path, etc).
 *    - *`i`* The index of the element to be updated (i.e. whether it is the first, second, third from the labelGroup)
 *    - *`display`* The display type. May be <b>false</b> if the label is hidden.
 *    - *`animate`* A boolean value to set or unset animations for the labels.
 */
Ext.chart.Label = Ext.extend(Object, {

    /**
     * @cfg {Object} label
     * Object with the following properties:
     *
     * - **display** : String
     *
     * Specifies the presence and position of labels for each pie slice. Either "rotate", "middle", "insideStart",
     * "insideEnd", "outside", "over", "under", or "none" to prevent label rendering.
     * Default value: 'none'.
     *
     * - **color** : String
     *
     * The color of the label text.
     * Default value: '#000' (black).
     *
     * - **contrast** : Boolean
     *
     * True to render the label in contrasting color with the backround.
     * Default value: false.
     *
     * - **field** : String
     *
     * The name of the field to be displayed in the label.
     * Default value: 'name'.
     *
     * - **minMargin** : Number
     *
     * Specifies the minimum distance from a label to the origin of the visualization.
     * This parameter is useful when using PieSeries width variable pie slice lengths.
     * Default value: 50.
     *
     * - **font** : String
     *
     * The font used for the labels.
     * Default value: "11px Helvetica, sans-serif".
     *
     * - **orientation** : String
     *
     * Either "horizontal" or "vertical".
     * Default value: "horizontal".
     *
     * - **renderer** : Function
     *
     * Optional function for formatting the label into a displayable value.
     * Default value: function(v) { return v; }
     */


    //@private a regex to parse url type colors.
    colorStringRe: /url\s*\(\s*#([^\/)]+)\s*\)/,

    //@private the mixin constructor. Used internally by Series.
    constructor: function(config) {
        var me = this;
        me.label = Ext.applyIf(config.label || {}, {
            renderer: function(v) {
                return v;
            }
        });

        if (me.label.display !== 'none') {
            me.labelsGroup = me.chart.surface.getGroup(me.seriesId + '-labels');
        }
    },

    //@private a method to render all labels in the labelGroup
    renderLabels: function() {
        var me = this,
            chart = me.chart,
            gradients = chart.gradients,
            items = me.items,
            animate = chart.animate,
            config = Ext.apply(me.labelStyle.style || {}, me.label || {}),
            display = config.display,
            field = [].concat(config.field),
            group = me.labelsGroup,
            len = me.getRecordCount(),
            itemLength = (items || 0) && items.length,
            ratio = itemLength / len,
            gradientsCount = (gradients || 0) && gradients.length,
            Color = Ext.draw.Color,
            gradient, count = 0, index, j, k, colorStopTotal, colorStopIndex, colorStop, item, label,
            sprite, spriteColor, spriteBrightness, labelColor, colorString;

        if (display == 'none') {
            return;
        }

        me.eachRecord(function(storeItem, i) {
            index = 0;
            for (j = 0; j < ratio; j++) {
                item = items[count];
                label = group.getAt(count);

                //check the excludes
                while (this.__excludes && this.__excludes[index]) {
                    index++;
                }

                if (!item && label) {
                    label.hide(true);
                }

                if (item && field[j]) {
                    if (!label) {
                        label = me.onCreateLabel(storeItem, item, i, display, j, index);
                    }
                    label.show(true);
                    me.onPlaceLabel(label, storeItem, item, i, display, animate, j, index);

                    //set contrast
                    if (config.contrast && item.sprite) {
                        sprite = item.sprite;
                        //set the color string to the color to be set.
                        if (sprite._endStyle) {
                            colorString = sprite._endStyle.fill;
                        }
                        else if (sprite._to) {
                            colorString = sprite._to.fill;
                        }
                        else {
                            colorString = sprite.attr.fill;
                        }
                        colorString = colorString || sprite.attr.fill;
                        spriteColor = Color.fromString(colorString);
                        //color wasn't parsed property maybe because it's a gradient id
                        if (colorString && !spriteColor) {
                            colorString = colorString.match(me.colorStringRe)[1];
                            for (k = 0; k < gradientsCount; k++) {
                                gradient = gradients[k];
                                if (gradient.id == colorString) {
                                    //avg color stops
                                    colorStop = 0;
                                    colorStopTotal = 0;
                                    for (colorStopIndex in gradient.stops) {
                                        colorStop++;
                                        colorStopTotal += Color.fromString(gradient.stops[colorStopIndex].color).getGrayscale();
                                    }
                                    spriteBrightness = (colorStopTotal / colorStop) / 255;
                                    break;
                                }
                            }
                        } else {
                            spriteBrightness = spriteColor.getGrayscale() / 255;
                        }
                        if (label.isOutside) {
                            spriteBrightness = 1;
                        }
                        labelColor = Color.fromString(label.attr.color || label.attr.fill).getHSL();

                        labelColor[2] = spriteBrightness > 0.5 ? 0.2 : 0.8;
                        label.setAttributes({
                            fill: String(Color.fromHSL.apply({},
                            labelColor))
                        },
                        true);
                    }
                }
                count++;
                index++;
            }
        });
        me.hideLabels(count);
    },

    //@private a method to hide labels.
    hideLabels: function(index) {
        var labelsGroup = this.labelsGroup,
            len;
        if (labelsGroup) {
            len = labelsGroup.getCount();
            while (len-->index) {
                labelsGroup.getAt(len).hide(true);
            }
        }
    }
});

/**
 * @class Ext.chart.Transformable
 *
 * Transformable is a mixin for chart items (axes, series, etc.) which makes them capable
 * of having their surfaces panned and zoomed via transformations.
 *
 * There are two modes of transformation that this mixin supports:
 *
 * - **Persistent transform** - This is a logical transformation, saved to the item as properties
 *   {@link #panX}, {@link #panY}, {@link #zoomX}, and {@link #zoomY}. The item's drawing logic must
 *   honor these properties and should be explicitly re-run after updating the persistent transform.
 * - **Fast transform** - This is a pixel-wise transform applied (via CSS3) to the {@link Ext.draw.Surface}
 *   element itself. As this does not perform a redraw of the surface, vector shapes currently
 *   rendered to the surface will be deformed by this transform. This is meant to only be transient,
 *   and to have {@link #syncToFastTransform} called once the speed is no longer required to apply
 *   the fast transform parameters into the persistent transform properties.
 */
Ext.chart.Transformable = Ext.extend(Object, {

    /**
     * @property zoomX
     * @type {Number}
     * The horizontal zoom transformation factor for this chart item. Defaults to 1.
     */
    zoomX: 1,
    /**
     * @property zoomY
     * @type {Number}
     * The vertical zoom transformation factor for this chart item. Defaults to 1.
     */
    zoomY: 1,
    /**
     * @property panX
     * @type {Number}
     * The horizontal pan transformation offset for this chart item. Defaults to 0.
     */
    panX: 0,
    /**
     * @property panY
     * @type {Number}
     * The vertical pan transformation offset for this chart item. Defaults to 0.
     */
    panY: 0,

    constructor: function() {
        this.addEvents(
            /**
             * @event transform
             * Fired after a transformation has been applied to this chart item.
             * @param {Object} this
             * @param {Boolean} fast True if it is a CSS3 fast transform, false if a persistent transform
             */
            'transform'
        );
    },

    /**
     * Directly sets the persistent pan/zoom transform properties for this chart item. Removes any
     * active fast transform and updates the {@link #panX}, {@link #panY}, {@link #zoomX}, and
     * {@link #zoomY} properties to match the supplied arguments.
     * @param {Number} panX
     * @param {Number} panY
     * @param {Number} zoomX
     * @param {Number} zoomY
     */
    setTransform: function(panX, panY, zoomX, zoomY) {
        var me = this;
        me.panX = panX;
        me.panY = panY;
        me.zoomX = zoomX;
        me.zoomY = zoomY;
        me.clearFastTransform();
        Ext.each(me.getTransformableSurfaces(), function(surface) {
            surface.setSurfaceTransform(panX, panY, zoomX, zoomY);
        });
        me.fireEvent('transform', me, false);
    },

    /**
     * Adjusts the persistent pan/zoom transform properties for this chart item. Removes any
     * active fast transform and adjusts the existing {@link #panX}, {@link #panY}, {@link #zoomX}, and
     * {@link #zoomY} properties by the supplied arguments.
     * @param {Number} panX
     * @param {Number} panY
     * @param {Number} zoomX
     * @param {Number} zoomY
     */
    transformBy: function(panX, panY, zoomX, zoomY) {
        var me = this;
        me.setTransform(me.panX + panX, me.panY + panY, me.zoomX * zoomX, me.zoomY * zoomY);
    },

    /**
     * Sets the pan/zoom transformation for this chart item, using CSS3 for fast hardware-accelerated
     * transformation. The existing persistent {@link #panX}, {@link #panY}, {@link #zoomX}, and
     * {@link #zoomY} properties will be left alone and the remaining transform required to reach
     * the supplied arguments will be applied using a CSS3 transform.
     * @param {Number} panX
     * @param {Number} panY
     * @param {Number} zoomX
     * @param {Number} zoomY
     */
    setTransformFast: function(panX, panY, zoomX, zoomY) {
        var me = this;
        panX -= me.panX;
        panY -= me.panY;
        zoomX /= me.zoomX;
        zoomY /= me.zoomY;
        me.clearFastTransform();
        me.transformByFast(panX, panY, zoomX, zoomY);
    },

    /**
     * Adjusts the pan/zoom transformation for this chart item, using CSS3 for fast hardware-accelerated
     * transformation. The existing persistent {@link #panX}/{@link #panY}/{@link #zoomX}/{@link #zoomY}
     * properties will be left alone and the supplied arguments will be added to the existing transform
     * using CSS3.
     * @param {Number} panX
     * @param {Number} panY
     * @param {Number} zoomX
     * @param {Number} zoomY
     */
    transformByFast: function(panX, panY, zoomX, zoomY) {
        this.setFastTransformMatrix(this.getFastTransformMatrix().translate(panX, panY).scale(zoomX, zoomY, 0, 0));
    },

    /**
     * Returns a {@link Ext.draw.Matrix} representing the total current transformation for this chart
     * item, including both the persistent {@link #panX}/{@link #panY}/{@link #zoomX}/{@link #zoomY}
     * and any additional CSS3 fast transform that is currently applied.
     * @return {Ext.draw.Matrix}
     */
    getTransformMatrix: function() {
        var me = this;
        return me.getFastTransformMatrix().clone().translate(me.panX, me.panY).scale(me.zoomX, me.zoomY, 0, 0);
    },

    /**
     * Returns a {@link Ext.draw.Matrix} representing the CSS3 fast transform currently applied to this
     * chart item. If no fast transform is applied a Matrix in its default state will be returned. This
     * matrix does *not* include the persistent {@link #panX}/{@link #panY}/{@link #zoomX}/{@link #zoomY}
     * transformation properties.
     * @return {Ext.draw.Matrix}
     */
    getFastTransformMatrix: function() {
        return this.fastTransformMatrix || new Ext.draw.Matrix();
    },

    /**
     * Sets the pan/zoom transformation for this chart item, using CSS3 for fast hardware-accelerated
     * transformation. The existing persistent {@link #panX}, {@link #panY}, {@link #zoomX}, and
     * {@link #zoomY} properties will be left alone and the remaining transform required to reach
     * the supplied matrix argument will be applied using a CSS3 transform.
     * @param {Ext.draw.Matrix} matrix
     */
    setTransformMatrixFast: function(matrix) {
        var parts = matrix.split();
        this.setTransformFast(parts.translateX, parts.translateY, parts.scaleX, parts.scaleY);
    },

    /**
     * @private
     * Sets only the CSS3 fast transform to match the given {@link Ext.draw.Matrix}, overwriting
     * any existing fast transform.
     * @param {Ext.draw.Matrix} matrix
     */
    setFastTransformMatrix: function(matrix) {
        var me = this;
        me.fastTransformMatrix = matrix;
        Ext.each(me.getTransformableSurfaces(), function(surface) {
            surface.setSurfaceFastTransform(matrix);
        });
        if (matrix) {
            me.fireEvent('transform', me, true);
        }
    },

    /**
     * @private
     * Removes any CSS3 fast transform currently applied to this chart item.
     */
    clearFastTransform: function() {
        this.setFastTransformMatrix(null);
    },

    /**
     * Returns `true` if this chart item currently has a CSS3 fast transform applied, `false` if not.
     * @return {Boolean}
     */
    hasFastTransform: function() {
        var matrix = this.fastTransformMatrix;
        return matrix && !matrix.isIdentity();
    },

    /**
     * Clears all transforms from this chart item.
     */
    clearTransform: function() {
        this.setTransform(0, 0, 1, 1);
    },

    /**
     * If this chart item has a CSS3 fast transform applied, this method will apply that transform
     * to the persistent {@link #panX}/{@link #panY}/{@link #zoomX}/{@link #zoomY} transform properties
     * and remove the fast transform.
     */
    syncToFastTransform: function() {
        // decompose the fast transform matrix and adjust the persistent pan/zoom by its values
        var me = this,
            fastMatrix = me.getFastTransformMatrix(),
            parts = fastMatrix.split();
        delete me.fastTransformMatrix;
        me.transformBy(parts.translateX, parts.translateY, parts.scaleX, parts.scaleY);
    },

    /**
     * Return a list of the {@link Ext.draw.Surface surfaces} that should be kept in sync
     * with this chart item's transformations.
     */
    getTransformableSurfaces: function() {
        return [];
    }

});
/**
 * @class Ext.chart.axis.Abstract
 */
Ext.ns('Ext.chart.axis');
Ext.chart.axis.Abstract = Ext.extend(Ext.util.Observable, {

    constructor: function(config) {
        config = config || {};

        var me = this,
            pos = config.position || 'left';

        pos = pos.charAt(0).toUpperCase() + pos.substring(1);
        Ext.apply(me, config);
        me.fields = [].concat(me.fields);
        me.labels = [];
        me.getId();
        me.labelGroup = me.getSurface().getGroup(me.axisId + "-labels");

        me.titleStyle = new Ext.chart.theme.TitleStyle();
        Ext.apply(me.titleStyle.style, config.labelTitle || {});

        me.labelStyle = new Ext.chart.theme.LabelStyle();
        Ext.apply(me.labelStyle.style, config.label || {});

        me.gridStyle = new Ext.chart.theme.GridStyle();
        Ext.apply(me.gridStyle.style, config.grid || {});

        if (config.grid && config.grid.odd) {
            me.gridStyle.oddStyle.style = Ext.apply(me.gridStyle.oddStyle.style || {},
            config.grid.odd);
        }

        if (config.grid && config.grid.even) {
            me.gridStyle.evenStyle.style = Ext.apply(me.gridStyle.evenStyle.style || {},
            config.grid.even);
        }

        Ext.chart.Transformable.prototype.constructor.call(me);
    },

    grid: false,
    steps: 10,
    x: 0,
    y: 0,
    minValue: 0,
    maxValue: 0,

    getId: function() {
        return this.axisId || (this.axisId = Ext.id(null, 'ext-axis-'));
    },

    /*
      Called to process a view i.e to make aggregation and filtering over
      a store creating a substore to be used to render the axis. Since many axes
      may do different things on the data and we want the final result of all these
      operations to be rendered we need to call processView on all axes before drawing
      them.
    */
    processView: Ext.emptyFn,

    drawAxis: Ext.emptyFn,

    /**
     * Get the {@link Ext.draw.Surface} instance for this axis.
     * @return {Ext.draw.Surface}
     */
    getSurface: function() {
        var me = this,
            surface = me.surface,
            chart = me.chart;
        if (!surface) {
            surface = me.surface = chart.getSurface(me.position + 'Axis');
            surface.el.setStyle('zIndex', chart.surfaceZIndexes.axis);
        }
        return surface;
    },

    /**
     * Hides all axis labels.
     */
    hideLabels: function() {
        this.labelGroup.hide();
    },

    /**
     * @private update the position/size of the axis surface. By default we set it to the
     * full chart size; subclasses can change this for custom clipping size.
     */
    updateSurfaceBox: function() {
        var me = this,
            surface = me.getSurface(),
            chart = me.chart;
        surface.el.setTopLeft(0, 0);
        surface.setSize(chart.curWidth, chart.curHeight);
    },

    getTransformableSurfaces: function() {
        return [this.getSurface()];
    },

    /**
     * Reset the axis to its original state, before any user interaction.
     */
    reset: function() {
        this.clearTransform();
    },

    /**
     * Invokes renderFrame on this axis's surface(s)
     */
    renderFrame: function() {
        this.getSurface().renderFrame();
    },


    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    //filled by the constructor.
    ownerCt: null,

    getItemId: function() {
        return this.el && this.el.id || this.id || null;
    },

    initCls: function() {
        return (this.cls || '').split(' ');
    },

    isXType: function(xtype) {
        return xtype === 'axis';
    },

    getRefItems: function(deep) {
        var me = this,
            ans = [];

        if (me.labelStyle) {
            ans.push(me.labelStyle);
        }

        if (me.titleStyle) {
            ans.push(me.titleStyle);
        }

        if (me.gridStyle) {
            ans.push(me.gridStyle);
            ans.push(me.gridStyle.oddStyle);
            ans.push(me.gridStyle.evenStyle);
        }

        return ans;
    }
});

Ext.applyIf(Ext.chart.axis.Abstract.prototype, Ext.chart.Transformable.prototype);


/**
 * @class Ext.chart.axis.Axis
 * @extends Ext.chart.axis.Abstract
 *
 * Defines axis for charts. The axis position, type, style can be configured.
 * The axes are defined in an axes array of configuration objects where the type,
 * field, grid and other configuration options can be set. To know more about how
 * to create a Chart please check the Chart class documentation. Here's an example for the axes part:
 * An example of axis for a series (in this case for an area chart that has multiple layers of yFields) could be:
 *
 *     axes: [{
 *         type: 'Numeric',
 *         grid: true,
 *         position: 'left',
 *         fields: ['data1', 'data2', 'data3'],
 *         title: 'Number of Hits',
 *         grid: {
 *             odd: {
 *                 opacity: 1,
 *                 fill: '#ddd',
 *                 stroke: '#bbb',
 *                 'stroke-width': 1
 *             }
 *         },
 *         minimum: 0
 *     }, {
 *         type: 'Category',
 *         position: 'bottom',
 *         fields: ['name'],
 *         title: 'Month of the Year',
 *         grid: true,
 *         label: {
 *             rotate: {
 *                 degrees: 315
 *             }
 *         }
 *     }]
 *
 * In this case we use a `Numeric` axis for displaying the values of the Area series and a `Category` axis for displaying the names of
 * the store elements. The numeric axis is placed on the left of the screen, while the category axis is placed at the bottom of the chart.
 * Both the category and numeric axes have `grid` set, which means that horizontal and vertical lines will cover the chart background. In the
 * category axis the labels will be rotated so they can fit the space better.
 */
Ext.chart.axis.Axis = Ext.extend(Ext.chart.axis.Abstract, {

    /**
     * @cfg {Number} majorTickSteps
     * If `minimum` and `maximum` are specified it forces the number of major ticks to the specified value.
     */

    /**
     * @cfg {Number} minorTickSteps
     * The number of small ticks between two major ticks. Default is zero.
     */

    /**
     * @cfg {Array} fields
     * An array containing the names of record fields which should be mapped along the axis
     */

    /**
     * @cfg {String} title
     * The title for the Axis
     */

    /**
     * @cfg {Object} label
     * The label configuration object for the Axis
     */

    /**
     * @cfg {Number} dashSize
     * The size of the dash marker. Default's 3.
     */
    dashSize: 3,

    /**
     * @cfg {String} position
     * Where to set the axis. Available options are `left`, `bottom`, `right`, `top`. Default's `bottom`.
     */
    position: 'bottom',

    /**
     * Offset axis position. Default's 0.
     * @property length
     * @type {Number}
     */
    length: 0,

    /**
     * Offset axis width. Default's 0.
     * @property width
     * @type {Number}
     */
    width: 0,

    majorTickSteps: false,

    // @private
    applyData: Ext.emptyFn,

    /**
     * @private If true, label values will be calculated during each axis draw; useful for numeric axes.
     */
    calcLabels: false,

    /**
     * @private Size of the buffer area on either side of the viewport to provide seamless zoom/pan
     * transforms. Expressed as a multiple of the viewport length, e.g. 1 will make the buffer on
     * each side equal to the length of the visible axis viewport.
     */
    overflowBuffer: 1.25,

    renderFrame: function() {
        var me = this,
            surface = me.getSurface(),
            labelSurface = me.getLabelSurface();
        surface.renderFrame();
        if (labelSurface !== surface) {
            labelSurface.renderFrame();
        }
    },

    // @private returns whether this axis is on the left or right side
    isSide: function() {
        var pos = this.position;
        return pos === 'left' || pos === 'right';
    },

    /**
     * @private update the position, size, and clipping of the axis surface to match the
     * current bbox and zoom/pan properties.
     */
    updateSurfaceBox: function() {
        var me = this,
            isSide = me.isSide(),
            length = me.length,
            viewLength = length + 1, //add 1px to viewable area so the last tick lines up with main line of adjacent axis
            width = me.width,
            surface = me.getSurface();

        surface.el.setTopLeft(me.y, me.x);
        surface.setSize(isSide ? width : viewLength, isSide ? viewLength : width);
    },

    // @private creates a structure with start, end and step points.
    calcEnds: function() {
        var me = this,
            boundSeries = me.getBoundSeries(),
            min = isNaN(me.minimum) ? Infinity : me.minimum,
            max = isNaN(me.maximum) ? -Infinity : me.maximum,
            zoom = me['zoom' + (me.isSide() ? 'Y' : 'X')],
            endsLocked = me.chart.endsLocked,
            outfrom, outto, out;

        if (endsLocked) {
            min = me.prevFrom;
            max = me.prevTo;
        } else {
            // For each series bound to this axis, ask the series for its min/max values
            // and use them to find the overall min/max.
            boundSeries.each(function(series) {
                var minMax = me.isBoundToField(series.xField) ? series.getMinMaxXValues() : series.getMinMaxYValues();
                if (minMax[0] < min) {
                    min = minMax[0];
                }
                if (minMax[1] > max) {
                    max = minMax[1];
                }
            });
            if (!isFinite(max)) {
                max = me.prevMax || 0;
            }
            if (!isFinite(min)) {
                min = me.prevMin || 0;
            }

            // If the max isn't on the floor, we want to ceil the max for a better endpoint.
            if (min != max && (max != (Math.floor(max)))) {
                max = Math.ceil(max);
            }
        }

        // if minimum and maximum are the same in a numeric axis then change the minimum bound.
        if (me.type == 'Numeric' && min === max) {
            if (max !== 0) {
                min = max / 2;
            } else {
                min = -1;
            }
        }

        out = Ext.draw.Draw.snapEnds(min, max, (me.majorTickSteps !== false ?  (me.majorTickSteps +1) : me.steps) * zoom, endsLocked);
        outfrom = out.from;
        outto = out.to;

        if (!endsLocked) {
            if (!isNaN(me.maximum)) {
                //TODO(nico) users are responsible for their own minimum/maximum values set.
                //Clipping should be added to remove lines in the chart which are below the axis.
                out.to = me.maximum;
            }
            if (!isNaN(me.minimum)) {
                //TODO(nico) users are responsible for their own minimum/maximum values set.
                //Clipping should be added to remove lines in the chart which are below the axis.
                out.from = me.minimum;
            }
        }

        //Adjust after adjusting minimum and maximum
        out.step = (out.to - out.from) / (outto - outfrom) * out.step;

        if (me.adjustMaximumByMajorUnit) {
            out.to += out.step;
        }
        if (me.adjustMinimumByMajorUnit) {
            out.from -= out.step;
        }
        me.prevTo = out.to;
        me.prevFrom = out.from;
        me.prevMin = min == max? 0 : min;
        me.prevMax = max;
        return out;
    },

    /**
     * Renders the axis into the screen and updates it's position.
     */
    drawAxis: function (init) {
        var me = this,
            zoomX = me.zoomX,
            zoomY = me.zoomY,
            x = me.startX * zoomX,
            y = me.startY * zoomY,
            gutterX = me.chart.maxGutter[0] * zoomX,
            gutterY = me.chart.maxGutter[1] * zoomY,
            dashSize = me.dashSize,
            subDashesX = me.minorTickSteps || 0,
            subDashesY = me.minorTickSteps || 0,
            isSide = me.isSide(),
            viewLength = me.length,
            bufferLength = viewLength * me.overflowBuffer,
            totalLength = viewLength * (isSide ? zoomY : zoomX),
            position = me.position,
            inflections = [],
            calcLabels = me.calcLabels,
            stepCalcs = me.applyData(),
            step = stepCalcs.step,
            from = stepCalcs.from,
            to = stepCalcs.to,
            math = Math,
            mfloor = math.floor,
            mmax = math.max,
            mmin = math.min,
            mround = math.round,
            trueLength, currentX, currentY, startX, startY, path, dashesX, dashesY, delta, skipTicks, i;

        me.updateSurfaceBox();

        //If no steps are specified
        //then don't draw the axis. This generally happens
        //when an empty store.
        if (me.hidden || me.chart.store.getCount() < 1 || stepCalcs.steps <= 0) {
            me.getSurface().items.hide(true);
            if (me.displaySprite) {
                me.displaySprite.hide(true);
            }
            return;
        }

        me.from = stepCalcs.from;
        me.to = stepCalcs.to;
        if (isSide) {
            currentX = mfloor(x) + 0.5;
            path = ["M", currentX, y, "l", 0, -totalLength];
            trueLength = totalLength - (gutterY * 2);
        }
        else {
            currentY = mfloor(y) + 0.5;
            path = ["M", x, currentY, "l", totalLength, 0];
            trueLength = totalLength - (gutterX * 2);
        }

        delta = trueLength * step / (to - from);
        skipTicks = me.skipTicks = mfloor(mmax(0, (isSide ? totalLength + me.panY - viewLength - bufferLength : -me.panX - bufferLength)) / delta);
        dashesX = mmax(subDashesX +1, 0);
        dashesY = mmax(subDashesY +1, 0);
        if (calcLabels) {
            me.labels = [stepCalcs.from + skipTicks * step];
        }
        if (isSide) {
            currentY = startY = y - gutterY - delta * skipTicks;
            currentX = x - ((position == 'left') * dashSize * 2);
            while (currentY >= startY - mmin(trueLength, viewLength + bufferLength * 2)) {
                path.push("M", currentX, mfloor(currentY) + 0.5, "l", dashSize * 2 + 1, 0);
                if (currentY != startY) {
                    for (i = 1; i < dashesY; i++) {
                        path.push("M", currentX + dashSize, mfloor(currentY + delta * i / dashesY) + 0.5, "l", dashSize + 1, 0);
                    }
                }
                inflections.push([ mfloor(x), mfloor(currentY) ]);
                currentY -= delta;
                if (calcLabels) {
                    // Cut everything that is after tenth digit after floating point. This is to get rid of
                    // rounding errors, i.e. 12.00000000000121212.
                    me.labels.push(+(me.labels[me.labels.length - 1] + step).toFixed(10));
                }
                if (delta === 0) {
                    break;
                }
            }
            if (mround(currentY + delta - (y - gutterY - trueLength))) {
                path.push("M", currentX, mfloor(y - totalLength + gutterY) + 0.5, "l", dashSize * 2 + 1, 0);
                for (i = 1; i < dashesY; i++) {
                    path.push("M", currentX + dashSize, mfloor(y - totalLength + gutterY + delta * i / dashesY) + 0.5, "l", dashSize + 1, 0);
                }
                inflections.push([ mfloor(x), mfloor(currentY) ]);
                if (calcLabels) {
                    // Cut everything that is after tenth digit after floating point. This is to get rid of
                    // rounding errors, i.e. 12.00000000000121212.
                    me.labels.push(+(me.labels[me.labels.length - 1] + step).toFixed(10));
                }
            }
        } else {
            currentX = startX = x + gutterX + delta * skipTicks;
            currentY = y - ((position == 'top') * dashSize * 2);
            while (currentX <= startX + mmin(trueLength, viewLength + bufferLength * 2)) {
                path.push("M", mfloor(currentX) + 0.5, currentY, "l", 0, dashSize * 2 + 1);
                if (currentX != startX) {
                    for (i = 1; i < dashesX; i++) {
                        path.push("M", mfloor(currentX - delta * i / dashesX) + 0.5, currentY, "l", 0, dashSize + 1);
                    }
                }
                inflections.push([ mfloor(currentX), mfloor(y) ]);
                currentX += delta;
                if (calcLabels) {
                    // Cut everything that is after tenth digit after floating point. This is to get rid of
                    // rounding errors, i.e. 12.00000000000121212.
                    me.labels.push(+(me.labels[me.labels.length - 1] + step).toFixed(10));
                }
                if (delta === 0) {
                    break;
                }
            }
            if (mround(currentX - delta - (x + gutterX + trueLength))) {
                path.push("M", mfloor(x + totalLength - gutterX) + 0.5, currentY, "l", 0, dashSize * 2 + 1);
                for (i = 1; i < dashesX; i++) {
                    path.push("M", mfloor(x + totalLength - gutterX - delta * i / dashesX) + 0.5, currentY, "l", 0, dashSize + 1);
                }
                inflections.push([mfloor(currentX), mfloor(y) ]);
                if (calcLabels) {
                    // Cut everything that is after tenth digit after floating point. This is to get rid of
                    // rounding errors, i.e. 12.00000000000121212.
                    me.labels.push(+(me.labels[me.labels.length - 1] + step).toFixed(10));
                }
            }
        }
        if (!me.axis) {
            me.axis = me.getSurface().add(Ext.apply({
                type: 'path',
                path: path
            }, me.style));
        }
        me.axis.setAttributes({
            path: path,
            hidden: false
        }, true);
        me.inflections = inflections;
        if (!init) {
            //if grids have been styled in some way
            if ( me.grid ||
                 me.gridStyle.style ||
                 me.gridStyle.oddStyle.style ||
                 me.gridStyle.evenStyle.style ) {
              me.drawGrid();
            }
        }
        me.axisBBox = me.axis.getBBox();
        me.drawLabel();
    },

    /**
     * Renders an horizontal and/or vertical grid into the Surface.
     */
    drawGrid: function() {
        var me = this,
            surface = me.getSurface(),
            grid = me.gridStyle.style || me.grid,
            odd = me.gridStyle.oddStyle.style || grid.odd,
            even = me.gridStyle.evenStyle.style || grid.even,
            inflections = me.inflections,
            ln = inflections.length - ((odd || even)? 0 : 1),
            position = me.position,
            gutter = me.chart.maxGutter,
            width = me.width - 2,
            point, prevPoint,
            i = 1,
            isSide = me.isSide(),
            path = [], styles, lineWidth, dlineWidth,
            oddPath = [], evenPath = [];

        if ((gutter[1] !== 0 && isSide) ||
            (gutter[0] !== 0 && !isSide)) {
            i = 0;
            ln++;
        }
        for (; i < ln; i++) {
            point = inflections[i];
            prevPoint = inflections[i - 1];
            if (odd || even) {
                path = (i % 2)? oddPath : evenPath;
                styles = ((i % 2)? odd : even) || {};
                lineWidth = (styles.lineWidth || styles['stroke-width'] || 0) / 2;
                dlineWidth = 2 * lineWidth;
                if (position == 'left') {
                    path.push("M", prevPoint[0] + 1 + lineWidth, prevPoint[1] + 0.5 - lineWidth,
                              "L", prevPoint[0] + 1 + width - lineWidth, prevPoint[1] + 0.5 - lineWidth,
                              "L", point[0] + 1 + width - lineWidth, point[1] + 0.5 + lineWidth,
                              "L", point[0] + 1 + lineWidth, point[1] + 0.5 + lineWidth, "Z");
                }
                else if (position == 'right') {
                    path.push("M", prevPoint[0] - lineWidth, prevPoint[1] + 0.5 - lineWidth,
                              "L", prevPoint[0] - width + lineWidth, prevPoint[1] + 0.5 - lineWidth,
                              "L", point[0] - width + lineWidth, point[1] + 0.5 + lineWidth,
                              "L", point[0] - lineWidth, point[1] + 0.5 + lineWidth, "Z");
                }
                else if (position == 'top') {
                    path.push("M", prevPoint[0] + 0.5 + lineWidth, prevPoint[1] + 1 + lineWidth,
                              "L", prevPoint[0] + 0.5 + lineWidth, prevPoint[1] + 1 + width - lineWidth,
                              "L", point[0] + 0.5 - lineWidth, point[1] + 1 + width - lineWidth,
                              "L", point[0] + 0.5 - lineWidth, point[1] + 1 + lineWidth, "Z");
                }
                else {
                    path.push("M", prevPoint[0] + 0.5 + lineWidth, prevPoint[1] - lineWidth,
                            "L", prevPoint[0] + 0.5 + lineWidth, prevPoint[1] - width + lineWidth,
                            "L", point[0] + 0.5 - lineWidth, point[1] - width + lineWidth,
                            "L", point[0] + 0.5 - lineWidth, point[1] - lineWidth, "Z");
                }
            } else {
                if (position == 'left') {
                    path = path.concat(["M", point[0] + 0.5, point[1] + 0.5, "l", width, 0]);
                }
                else if (position == 'right') {
                    path = path.concat(["M", point[0] - 0.5, point[1] + 0.5, "l", -width, 0]);
                }
                else if (position == 'top') {
                    path = path.concat(["M", point[0] + 0.5, point[1] + 0.5, "l", 0, width]);
                }
                else {
                    path = path.concat(["M", point[0] + 0.5, point[1] - 0.5, "l", 0, -width]);
                }
            }
        }
        if (odd || even) {
            if (oddPath.length) {
                if (!me.gridOdd && oddPath.length) {
                    me.gridOdd = surface.add({
                        type: 'path',
                        path: oddPath
                    });
                }
                me.gridOdd.setAttributes(Ext.apply({
                    path: oddPath,
                    hidden: false
                }, odd || {}), true);
            }
            if (evenPath.length) {
                if (!me.gridEven) {
                    me.gridEven = surface.add({
                        type: 'path',
                        path: evenPath
                    });
                }
                me.gridEven.setAttributes(Ext.apply({
                    path: evenPath,
                    hidden: false
                }, even || {}), true);
            }
        }
        else {
            if (path.length) {
                if (!me.gridLines) {
                    me.gridLines = me.getSurface().add({
                        type: 'path',
                        path: path,
                        "stroke-width": me.lineWidth || 1,
                        stroke: me.gridColor || '#ccc'
                    });
                }
                me.gridLines.setAttributes({
                    hidden: false,
                    path: path
                }, true);
            }
            else if (me.gridLines) {
                me.gridLines.hide(true);
            }
        }
    },

    isPannable: function() {
        var me = this,
            length = me.length,
            isSide = me.isSide(),
            math = Math,
            ceil = math.ceil,
            floor = math.floor,
            matrix = me.getTransformMatrix();
        return matrix && (
            (isSide ? ceil(matrix.y(0, 0)) < 0 : floor(matrix.x(length, 0)) > length) ||
            (isSide ? floor(matrix.y(0, length)) > length : ceil(matrix.x(0, 0)) < 0)
        );
    },
	
		// @private
	getOrCreateLabel: function (i, text) {
		if(this.image){
			return this.getOrCreateImageLabel(i, text);
		}else{
			return this.getOrCreateTextLabel(i, text);
		}
	},

    //@private
    getOrCreateTextLabel: function(i, text) {
        var me = this,
            labelGroup = me.labelGroup,
            textLabel = labelGroup.getAt(i),
            surface,
            labelStyle = me.labelStyle.style;
        if (textLabel) {
            if (text != textLabel.attr.text) {
                textLabel.setAttributes(Ext.apply({
                    text: text
                }, labelStyle), true);
                textLabel._bbox = textLabel.getBBox();
            }
        }
        else {
            surface = me.getLabelSurface();
            textLabel = surface.add(Ext.apply({
                group: labelGroup,
                type: 'text',
                x: 0,
                y: 0,
                text: text
            }, labelStyle));
            surface.renderItem(textLabel);
            textLabel._bbox = textLabel.getBBox();
        }
        //get untransformed bounding box
        if (labelStyle.rotation) {
            textLabel.setAttributes({
                rotation: {
                    degrees: 0
                }
            }, true);
            textLabel._ubbox = textLabel.getBBox();
            textLabel.setAttributes(labelStyle, true);
        } else {
            textLabel._ubbox = textLabel._bbox;
        }
        return textLabel;
    },
	
	    // @private
    getOrCreateImageLabel: function (i, text) {
         var me = this,
            labelGroup = me.labelGroup,
            textLabel = labelGroup.getAt(i),
            surface,
            labelStyle = me.labelStyle.style;
        if (textLabel) {
            if (text != textLabel.attr.text) {
                textLabel.setAttributes(Ext.apply({
                    src: text
                }, labelStyle), true);
                textLabel._bbox = textLabel.getBBox();
            }
        }
        else {
            surface = me.getLabelSurface();
            textLabel = surface.add(Ext.apply({
                group: labelGroup,
                type: 'image',
                x: 0,
                y: 0,
				width: 20,
				height: 20,
                src: text
            }, labelStyle));
            surface.renderItem(textLabel);
            textLabel._bbox = textLabel.getBBox();
        }
        //get untransformed bounding box
        if (labelStyle.rotation) {
            textLabel.setAttributes({
                rotation: {
                    degrees: 0
                }
            }, true);
            textLabel._ubbox = textLabel.getBBox();
            textLabel.setAttributes(labelStyle, true);
        } else {
            textLabel._ubbox = textLabel._bbox;
        }
        return textLabel;
    },


    rect2pointArray: function(sprite) {
        var surface = this.getSurface(),
            rect = surface.getBBox(sprite, true),
            p1 = [rect.x, rect.y],
            p1p = p1.slice(),
            p2 = [rect.x + rect.width, rect.y],
            p2p = p2.slice(),
            p3 = [rect.x + rect.width, rect.y + rect.height],
            p3p = p3.slice(),
            p4 = [rect.x, rect.y + rect.height],
            p4p = p4.slice(),
            matrix = sprite.matrix;
        //transform the points
        p1[0] = matrix.x.apply(matrix, p1p);
        p1[1] = matrix.y.apply(matrix, p1p);

        p2[0] = matrix.x.apply(matrix, p2p);
        p2[1] = matrix.y.apply(matrix, p2p);

        p3[0] = matrix.x.apply(matrix, p3p);
        p3[1] = matrix.y.apply(matrix, p3p);

        p4[0] = matrix.x.apply(matrix, p4p);
        p4[1] = matrix.y.apply(matrix, p4p);
        return [p1, p2, p3, p4];
    },

    intersect: function(l1, l2) {
        var r1 = this.rect2pointArray(l1),
            r2 = this.rect2pointArray(l2);
        return !!Ext.draw.Draw.intersect(r1, r2).length;
    },

    drawHorizontalLabels: function() {
        var me = this,
            labelConf = me.labelStyle.style,
            renderer = labelConf.renderer || function(v) {
                return v;
            },
            math = Math,
            floor = math.floor,
            max = math.max,
            axes = me.chart.axes,
            position = me.position,
            inflections = me.inflections,
            ln = inflections.length,
            labels = me.labels,
            skipTicks = me.skipTicks,
            maxHeight = 0,
            ratio,
            bbox, point, prevLabel,
            textLabel, text,
            last, x, y, i, firstLabel;

        if (!me.calcLabels && skipTicks) {
            labels = labels.slice(skipTicks);
            ln -= skipTicks;
        }

        last = ln - 1;
        //get a reference to the first text label dimensions
        point = inflections[0];
        firstLabel = me.getOrCreateLabel(0, renderer(labels[0]));
        ratio = math.abs(math.sin(labelConf.rotate && (labelConf.rotate.degrees * math.PI / 180) || 0)) >> 0;

        for (i = 0; i < ln; i++) {
            point = inflections[i];
            text = renderer(labels[i]);
            textLabel = me.getOrCreateLabel(i, text);
            bbox = textLabel._bbox;
            maxHeight = max(maxHeight, bbox.height + me.dashSize + (labelConf.padding || 0));
            x = floor(point[0] - (ratio? bbox.height : bbox.width) / 2);
            if (me.chart.maxGutter[0] == 0) {
                if (i == 0 && axes.findIndex('position', 'left') == -1) {
                    x = point[0];
                }
                else if (i == last && axes.findIndex('position', 'right') == -1) {
                    x = point[0] - bbox.width;
                }
            }
            if (position == 'top') {
                y = point[1] - (me.dashSize * 2) - labelConf.padding - (bbox.height / 2);
            }
            else {
                y = point[1] + (me.dashSize * 2) + labelConf.padding + (bbox.height / 2);
            }
            if (!me.isPannable()) {
                x += me.x;
                y += me.y;
            }
            textLabel.setAttributes({
                hidden: false,
                x: x,
                y: y
            }, true);
            if (labelConf.rotate) {
                textLabel.setAttributes(labelConf, true);
            }
            // Skip label if there isn't available minimum space
            if (i != 0 && (me.intersect(textLabel, prevLabel)
                || me.intersect(textLabel, firstLabel))) {
                textLabel.hide(true);
                continue;
            }
            prevLabel = textLabel;
        }

        return maxHeight;
    },

    drawVerticalLabels: function() {
        var me = this,
            labelConf = me.labelStyle.style,
            renderer = labelConf.renderer || function(v) {
                return v;
            },
            inflections = me.inflections,
            position = me.position,
            ln = inflections.length,
            labels = me.labels,
            skipTicks = me.skipTicks,
            maxWidth = 0,
            math = Math,
            max = math.max,
            floor = math.floor,
            ceil = math.ceil,
            axes = me.chart.axes,
            gutterY = me.chart.maxGutter[1],
            bbox, point, prevLabel,
            textLabel, text,
            last, x, y, i;

        if (!me.calcLabels && skipTicks) {
            labels = labels.slice(skipTicks);
            ln -= skipTicks;
        }

        last = ln;
        for (i = 0; i < last; i++) {
            point = inflections[i];
            text = renderer(labels[i]);
            textLabel = me.getOrCreateLabel(i, text);
            bbox = textLabel._bbox;
            maxWidth = max(maxWidth, bbox.width + me.dashSize + (labelConf.padding || 0));
            y = point[1];
            if (gutterY < bbox.height / 2) {
                if (i == last - 1 && axes.findIndex('position', 'top') == -1) {
                    y += ceil(bbox.height / 2);
                }
                else if (i == 0 && axes.findIndex('position', 'bottom') == -1) {
                    y -= floor(bbox.height / 2);
                }
            }
            if (position == 'left') {
                x = point[0] - bbox.width - me.dashSize - (labelConf.padding || 0) - 2;
            }
            else {
                x = point[0] + me.dashSize + (labelConf.padding || 0) + 2;
            }
            if (!me.isPannable()) {
                x += me.x;
                y += me.y + me.panY;
            }
            textLabel.setAttributes(Ext.apply({
                hidden: false,
                x: x,
                y: y
            }, labelConf), true);
            // Skip label if there isn't available minimum space
            if (i != 0 && me.intersect(textLabel, prevLabel)) {
                textLabel.hide(true);
                continue;
            }
            prevLabel = textLabel;
        }

        return maxWidth;
    },

    /**
     * Renders the labels in the axes.
     */
    drawLabel: function() {
        if (!this.inflections) {
            return 0;
        }
      
        var me = this,
            labelGroup = me.labelGroup,
            inflections = me.inflections,
            surface = me.getLabelSurface(),
            maxWidth = 0,
            maxHeight = 0,
            ln, i;

        // If we are switching between rendering labels to the axis surface and the main
        // chart surface, then we need to blow away all existing labels and let them get
        // re-created on the new surface
        if (me.lastLabelSurface !== surface) {
            labelGroup.each(function(sprite) {
                sprite.destroy();
            });
            labelGroup.clear();
            me.lastLabelSurface = surface;
        }

        if (me.isSide()) {
            maxWidth = me.drawVerticalLabels();
        } else {
            maxHeight = me.drawHorizontalLabels();
        }

        // Hide unused label sprites
        ln = labelGroup.getCount();
        i = inflections.length;
        for (; i < ln; i++) {
            labelGroup.getAt(i).hide(true);
        }

        me.bbox = {};
        Ext.apply(me.bbox, me.axisBBox);
        me.bbox.height = maxHeight;
        me.bbox.width = maxWidth;
        if (Ext.isString(me.title)) {
            me.drawTitle(maxWidth, maxHeight);
        }
    },

    /**
     * @private
     * Returns the surface onto which axis tick labels should be rendered. Differs between
     * when the axis is in its initial non-zoomed state (uses the main chart surface so the
     * labels can display outside the axis clipping area) and when it is zoomed so it overflows
     * (uses the axis surface so the labels are clipped and panned along with the axis grid).
     * @return {Ext.draw.Surface}
     */
    getLabelSurface: function() {
        var me = this;
        return me.isPannable() ? me.getSurface() : me.chart.getSurface('main');
    },

    /**
     * Updates the {@link #title} of this axis.
     * @param {String} title
     */
    setTitle: function(title) {
        this.title = title;
        this.drawLabel();
    },

    // @private draws the title for the axis.
    drawTitle: function(maxWidth, maxHeight) {
        var me = this,
            position = me.position,
            surface = me.chart.getSurface('main'), //title is drawn on main surface so it doesn't get transformed
            displaySprite = me.displaySprite,
            title = me.title,
            rotate = me.isSide(),
            x = me.startX + me.x,
            y = me.startY + me.y,
            base, bbox, pad;

        if (displaySprite) {
            displaySprite.setAttributes({text: title}, true);
        } else {
            base = {
                type: 'text',
                x: 0,
                y: 0,
                text: title
            };
            displaySprite = me.displaySprite = surface.add(Ext.apply(base, me.titleStyle.style, me.labelTitle));
            surface.renderItem(displaySprite);
        }
        bbox = displaySprite.getBBox();
        pad = me.dashSize + (me.titleStyle.style.padding || 0);

        if (rotate) {
            y -= ((me.length / 2) - (bbox.height / 2));
            if (position == 'left') {
                x -= (maxWidth + pad + (bbox.width / 2));
            }
            else {
                x += (maxWidth + pad + bbox.width - (bbox.width / 2));
            }
            me.bbox.width += bbox.width + 10;
        }
        else {
            x += (me.length / 2) - (bbox.width * 0.5);
            if (position == 'top') {
                y -= (maxHeight + pad + (bbox.height * 0.3));
            }
            else {
                y += (maxHeight + pad + (bbox.height * 0.8));
            }
            me.bbox.height += bbox.height + 10;
        }
        displaySprite.setAttributes({
            hidden: false,
            translate: {
                x: x,
                y: y
            }
        }, true);
    },

    /**
     * Return the Series object(s) that are bound to this axis.
     * @return Ext.util.MixedCollection
     */
    getBoundSeries: function() {
        var me = this,
            series = me.chart.series;
        return series.filterBy(function(s) {
            var seriesFields = [].concat(s.xField, s.yField),
                i = seriesFields.length;
            while (i--) {
                if (me.isBoundToField(seriesFields[i])) {
                    return true;
                }
            }
            return false;
        });
    },

    /**
     * Determine whether this axis is bound to the given field name.
     * @param {String} field
     * @return {Boolean}
     */
    isBoundToField: function(field) {
        var fields = this.fields,
            i = fields.length;
        while(i--) {
            if (fields[i] === field) {
                return true;
            }
        }
        return false;
    }

});


/**
 * @class Ext.chart.axis.Category
 * @extends Ext.chart.axis.Axis
 *
 * A type of axis that displays items in categories. This axis is generally used to
 * display categorical information like names of items, month names, quarters, etc.
 * but no quantitative values. For that other type of information <em>Number</em>
 * axis are more suitable.
 *
 * As with other axis you can set the position of the axis and its title. For example:
 *
 * {@img Ext.chart.axis.Category/Ext.chart.axis.Category.png Ext.chart.axis.Category chart axis}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}                                                
 *         ]
 *     });
 *  
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             grid: true,
 *             position: 'left',
 *             fields: ['data1', 'data2', 'data3', 'data4', 'data5'],
 *             title: 'Sample Values',
 *             grid: {
 *                 odd: {
 *                     opacity: 1,
 *                     fill: '#ddd',
 *                     stroke: '#bbb',
 *                     'stroke-width': 1
 *                 }
 *             },
 *             minimum: 0,
 *             adjustMinimumByMajorUnit: false
 *         }, {
 *             type: 'Category',
 *             position: 'bottom',
 *             fields: ['name'],
 *             title: 'Sample Metrics',
 *             grid: true,
 *             label: {
 *                 rotate: {
 *                     degrees: 315
 *                 }
 *             }
 *         }],
 *         series: [{
 *             type: 'area',
 *             highlight: false,
 *             axis: 'left',
 *             xField: 'name',
 *             yField: ['data1', 'data2', 'data3', 'data4', 'data5'],
 *             style: {
 *                 opacity: 0.93
 *             }
 *         }]
 *     });
 *
 * In this example with set the category axis to the bottom of the surface, bound the axis to
 * the <em>name</em> property and set as title <em>Month of the Year</em>.
 */

Ext.chart.axis.Category = Ext.extend(Ext.chart.axis.Axis, {

    /**
     * @cfg {Array} categoryNames
     * A list of category names to display along this axis.
     */
    categoryNames: null,

    /**
     * @cfg {Boolean} calculateCategoryCount
     * Indicates whether or not to calculate the number of categories (ticks and
     * labels) when there is not enough room to display all labels on the axis.
     * If set to true, the axis will determine the number of categories to plot.
     * If not, all categories will be plotted.
     */
    calculateCategoryCount: false,

    // @private creates an array of labels to be used when rendering.
    setLabels: function() {
        var store = this.chart.store,
            fields = this.fields,
            ln = fields.length,
            i;

        this.labels = [];
        store.each(function(record) {
            for (i = 0; i < ln; i++) {
                this.labels.push(record.get(fields[i]));
            }
        }, this);
    },

    // @private calculates labels positions and marker positions for rendering.
    applyData: function() {
        Ext.chart.axis.Category.superclass.applyData.call(this);
        this.setLabels();
        var count = this.chart.store.getCount();
        return {
            from: 0,
            to: count - 1,
            power: 1,
            step: 1,
            steps: count - 1
        };
    }
});

/**
 * @class Ext.chart.axis.Gauge
 * @extends Ext.chart.axis.Abstract
 *
 * Gauge Axis is the axis to be used with a Gauge series. The Gauge axis
 * displays numeric data from an interval defined by the `minimum`, `maximum` and
 * `step` configuration properties. The placement of the numeric data can be changed
 * by altering the `margin` option that is set to `10` by default.
 *
 * A possible configuration for this axis would look like:
 *
 *     axes: [{
 *         type: 'gauge',
 *         position: 'gauge',
 *         minimum: 0,
 *         maximum: 100,
 *         steps: 10,
 *         margin: 7
 *     }],
 */
Ext.chart.axis.Gauge = Ext.extend(Ext.chart.axis.Abstract, {

    /**
     * @cfg {Number} minimum (required) the minimum value of the interval to be displayed in the axis.
     */

    /**
     * @cfg {Number} maximum (required) the maximum value of the interval to be displayed in the axis.
     */

    /**
     * @cfg {Number} steps (optional) the number of steps and tick marks to add to the interval. Default's 10.
     */

    /**
     * @cfg {Number} margin (optional) the offset positioning of the tick marks and labels in pixels. Default's 10.
     */

    position: 'gauge',

    drawAxis: function(init) {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            bbox = chart.chartBBox,
            centerX = bbox.x + (bbox.width / 2),
            centerY = bbox.y + bbox.height,
            margin = me.margin || 10,
            rho = Math.min(bbox.width, 2 * bbox.height) /2 + margin,
            sprites = [], sprite,
            steps = me.steps,
            i, pi = Math.PI,
            cos = Math.cos,
            sin = Math.sin;

        if (me.sprites && !chart.resizing) {
            me.drawLabel();
            return;
        }

        me.updateSurfaceBox();

        if (me.margin >= 0) {
            if (!me.sprites) {
                //draw circles
                for (i = 0; i <= steps; i++) {
                    sprite = surface.add({
                        type: 'path',
                        path: ['M', centerX + (rho - margin) * cos(i / steps * pi - pi),
                                    centerY + (rho - margin) * sin(i / steps * pi - pi),
                                    'L', centerX + rho * cos(i / steps * pi - pi),
                                    centerY + rho * sin(i / steps * pi - pi), 'Z'],
                        stroke: '#ccc'
                    });
                    sprite.setAttributes(Ext.apply(me.style || {}, {
                        hidden: false
                    }), true);
                    sprites.push(sprite);
                }
            } else {
                sprites = me.sprites;
                //draw circles
                for (i = 0; i <= steps; i++) {
                    sprites[i].setAttributes({
                        path: ['M', centerX + (rho - margin) * cos(i / steps * pi - pi),
                                    centerY + (rho - margin) * sin(i / steps * pi - pi),
                               'L', centerX + rho * cos(i / steps * pi - pi),
                                    centerY + rho * sin(i / steps * pi - pi), 'Z']
                    }, true);
                }
            }
        }
        me.sprites = sprites;
        me.drawLabel();
        if (me.title) {
            me.drawTitle();
        }
    },

    drawTitle: function() {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            bbox = chart.chartBBox,
            labelSprite = me.titleSprite,
            labelBBox;

        if (!labelSprite) {
            me.titleSprite = labelSprite = surface.add({
                type: 'text',
                zIndex: 2
            });
        }
        labelSprite.setAttributes(Ext.apply({
            text: me.title
        }, Ext.apply(me.titleStyle.style || {}, me.label || {})), true);
        labelBBox = labelSprite.getBBox();
        labelSprite.setAttributes({
            x: bbox.x + (bbox.width / 2) - (labelBBox.width / 2),
            y: bbox.y + bbox.height - (labelBBox.height / 2) - 4
        }, true);
    },

    /**
     * Updates the {@link #title} of this axis.
     * @param {String} title
     */
    setTitle: function(title) {
        this.title = title;
        this.drawTitle();
    },

    drawLabel: function() {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            bbox = chart.chartBBox,
            centerX = bbox.x + (bbox.width / 2),
            centerY = bbox.y + bbox.height,
            margin = me.margin || 10,
            rho = Math.min(bbox.width, 2 * bbox.height) /2 + 2 * margin,
            round = Math.round,
            labelArray = [], label,
            maxValue = me.maximum || 0,
            steps = me.steps, i = 0,
            adjY,
            pi = Math.PI,
            cos = Math.cos,
            sin = Math.sin,
            labelConf = me.labelStyle.style,
            renderer = labelConf.renderer || function(v) { return v; };

        if (!me.labelArray) {
            //draw scale
            for (i = 0; i <= steps; i++) {
                // TODO Adjust for height of text / 2 instead
                adjY = (i === 0 || i === steps) ? 7 : 0;
                label = surface.add({
                    type: 'text',
                    text: renderer(round(i / steps * maxValue)),
                    x: centerX + rho * cos(i / steps * pi - pi),
                    y: centerY + rho * sin(i / steps * pi - pi) - adjY,
                    'text-anchor': 'middle',
                    'stroke-width': 0.2,
                    zIndex: 10,
                    stroke: '#333'
                });
                label.setAttributes(Ext.apply(me.labelStyle.style || {}, {
                    hidden: false
                }), true);
                labelArray.push(label);
            }
        }
        else {
            labelArray = me.labelArray;
            //draw values
            for (i = 0; i <= steps; i++) {
                // TODO Adjust for height of text / 2 instead
                adjY = (i === 0 || i === steps) ? 7 : 0;
                labelArray[i].setAttributes({
                    text: renderer(round(i / steps * maxValue)),
                    x: centerX + rho * cos(i / steps * pi - pi),
                    y: centerY + rho * sin(i / steps * pi - pi) - adjY
                }, true);
            }
        }
        me.labelArray = labelArray;
    }
});

/**
 * @class Ext.chart.axis.Numeric
 * @extends Ext.chart.axis.Axis
 *
 * An axis to handle numeric values. This axis is used for quantitative data as
 * opposed to the category axis. You can set mininum and maximum values to the
 * axis so that the values are bound to that. If no values are set, then the
 * scale will auto-adjust to the values.
 *
 * {@img Ext.chart.axis.Numeric/Ext.chart.axis.Numeric.png Ext.chart.axis.Numeric chart axis}
 *
 * For example:
 *
 *     var store = new Ext.data.JsonStore({
 *          fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *          data: [
 *              {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *              {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *              {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *              {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *              {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *          ]
 *     });
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             grid: true,
 *             position: 'left',
 *             fields: ['data1', 'data2', 'data3', 'data4', 'data5'],
 *             title: 'Sample Values',
 *             grid: {
 *                 odd: {
 *                     opacity: 1,
 *                     fill: '#ddd',
 *                     stroke: '#bbb',
 *                     'stroke-width': 1
 *                 }
 *             },
 *             minimum: 0,
 *             adjustMinimumByMajorUnit: 0
 *         }, {
 *             type: 'Category',
 *             position: 'bottom',
 *             fields: ['name'],
 *             title: 'Sample Metrics',
 *             grid: true,
 *             label: {
 *                 rotate: {
 *                     degrees: 315
 *                 }
 *             }
 *         }],
 *         series: [{
 *             type: 'area',
 *             highlight: false,
 *             axis: 'left',
 *             xField: 'name',
 *             yField: ['data1', 'data2', 'data3', 'data4', 'data5'],
 *             style: {
 *                 opacity: 0.93
 *             }
 *         }]
 *     });
 *
 * In this example we create an axis of Numeric type. We set a minimum value so that
 * even if all series have values greater than zero, the grid starts at zero. We bind
 * the axis onto the left part of the surface by setting <em>position</em> to <em>left</em>.
 * We bind three different store fields to this axis by setting <em>fields</em> to an array.
 * We set the title of the axis to <em>Number of Hits</em> by using the <em>title</em> property.
 * We use a <em>grid</em> configuration to set odd background rows to a certain style and even rows
 * to be transparent/ignored.
 *
 * @constructor
 */
Ext.chart.axis.Numeric = Ext.extend(Ext.chart.axis.Axis, {

    type: 'numeric',

    calcLabels: true,

    constructor: function(config) {
        var me = this,
            label, f;
        Ext.chart.axis.Numeric.superclass.constructor.apply(me, [config]);
        label = me.label || {};
        if (me.roundToDecimal === false) {
            return;
        }
        if (label.renderer) {
            f = label.renderer;
            label.renderer = function(v) {
                return me.roundToDecimal( f(v), me.decimals );
            };
        } else {
            label.renderer = function(v) {
                return me.roundToDecimal(v, me.decimals);
            };
        }
    },

    roundToDecimal: function(v, dec) {
        var val = Math.pow(10, dec || 0);
        return ((v * val) >> 0) / val;
    },

    /**
     * @cfg {Number} minimum
     * The minimum value drawn by the axis. If not set explicitly, the axis
     * minimum will be calculated automatically.
     */
    minimum: NaN,

    /**
     * @cfg {Number} maximum
     * The maximum value drawn by the axis. If not set explicitly, the axis
     * maximum will be calculated automatically.
     */
    maximum: NaN,

    /**
     * @cfg {Number} decimals
     * The number of decimals to round the value to.
     * Default's 2.
     */
    decimals: 2,

    /**
     * @cfg {String} scale
     * The scaling algorithm to use on this axis. May be "linear" or
     * "logarithmic".
     */
    scale: "linear",

    /**
     * @cfg {String} position
     * Indicates the position of the axis relative to the chart
     */
    position: 'left',

    /**
     * @cfg {Boolean} adjustMaximumByMajorUnit
     * Indicates whether to extend maximum beyond data's maximum to the nearest
     * majorUnit.
     */
    adjustMaximumByMajorUnit: false,

    /**
     * @cfg {Boolean} adjustMinimumByMajorUnit
     * Indicates whether to extend the minimum beyond data's minimum to the
     * nearest majorUnit.
     */
    adjustMinimumByMajorUnit: false,

    // @private apply data.
    applyData: function() {
        Ext.chart.axis.Numeric.superclass.applyData.apply(this, arguments);
        return this.calcEnds();
    }
});

/**
 * @class Ext.chart.axis.Radial
 * @extends Ext.chart.axis.Abstract
 *
 * Radial Axis is the axis to be used with a Radar Series. The Radial axis
 * is a circular display of numerical data by steps, with the number of circles
 * equivalent to the defined number of `steps`. Given the maximum data value,
 * the axis will compute step values depending on the number of defined `steps`.
 *
 * A possible configuration for this axis would look like:
 *
 *  axes: [{
 *      steps: 5,
 *      type: 'Radial',
 *      position: 'radial',
 *      label: {
 *          display: 'none'
 *      }
 *  }]
 */
Ext.chart.axis.Radial = Ext.extend(Ext.chart.axis.Abstract, {

    /**
     * @cfg {Number} maximum (optional) the maximum value to be displayed in the axis.
     */

    /**
     * @cfg {Number} steps (required) the number of steps to add to the radial axis.
     */

    position: 'radial',
    rotation: 0,

    drawAxis: function(init) {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            bbox = chart.chartBBox,
            store = chart.store,
            l = store.getCount(),
            centerX = bbox.x + (bbox.width / 2),
            centerY = bbox.y + (bbox.height / 2),
            math = Math,
            mmax = math.max,
            rho = math.min(bbox.width, bbox.height) /2,
            sprites = [], sprite,
            steps = me.steps,
            rotation = -me.rotation,
            rad = Ext.draw.Draw.rad,
            cos = math.cos,
            sin = math.sin,
            i, angle;

        if (!l) {
            surface.items.hide(true);
            return;
        }

        me.updateSurfaceBox();

        me.centerX = centerX;
        me.centerY = centerY;

        if (!me.sprites) {
            //draw circles
            for (i = 1; i <= steps; i++) {
                sprite = surface.add(Ext.apply(me.style || {}, {
                    type: 'circle',
                    x: centerX,
                    y: centerY,
                    'stroke-width': 1.5,
                    radius: mmax(rho * i / steps, 0),
                    stroke: '#ccc'
                }));
                sprite.setAttributes({
                    hidden: false
                }, true);
                sprites.push(sprite);
            }
            //draw lines
            store.each(function(rec, i) {
                angle = rad(rotation + i / l * 360);
                sprite = surface.add(Ext.apply(me.style || {}, {
                    type: 'path',
                    path: ['M', centerX, centerY, 'L', centerX + rho * cos(angle), centerY + rho * sin(angle), 'Z']
                }));
                sprite.setAttributes({
                    hidden: false
                }, true);
                sprites.push(sprite);
            });
        } else {
            sprites = me.sprites;
            //draw circles
            for (i = 0; i < steps; i++) {
                sprites[i].setAttributes({
                    hidden: false,
                    x: centerX,
                    y: centerY,
                    radius: mmax(rho * (i + 1) / steps, 0)
                }, true);
            }
            //draw lines
            store.each(function(rec, j) {
                angle = rad(rotation + j / l * 360);
                sprites[i + j].setAttributes(Ext.apply(me.style || {}, {
                    hidden: false,
                    path: ['M', centerX, centerY, 'L', centerX + rho * cos(angle), centerY + rho * sin(angle), 'Z']
                }), true);
            });
        }
        me.sprites = sprites;

        me.drawLabel();
    },

    drawLabel: function() {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            bbox = chart.chartBBox,
            store = chart.store,
            centerX = me.centerX,
            centerY = me.centerY,
            rho = Math.min(bbox.width, bbox.height) /2,
            max = Math.max, round = Math.round,
            labelArray = [], label,
            fields = [], nfields,
            categories = [], xField,
            aggregate = !me.maximum,
            maxValue = me.maximum || 0,
            steps = me.steps, i = 0, j, dx, dy,
            rotation = -me.rotation,
            rad = Ext.draw.Draw.rad,
            cos = Math.cos, sin = Math.sin,
            display = me.label.display,
            draw = display !== 'none',
            labelGroup = me.labelGroup,
            labelStyle = me.labelStyle.style,
            categoriesStyle = Ext.apply({}, labelStyle),
            margin = 10,
            angle;

        if (!draw) {
            return;
        }

        //get all rendered fields
        chart.series.each(function(series) {
            fields.push(series.yField);
            xField = series.xField;
        });

        //get maxValue to interpolate
        store.each(function(record, i) {
            if (aggregate) {
                for (i = 0, nfields = fields.length; i < nfields; i++) {
                    maxValue = max(+record.get(fields[i]), maxValue);
                }
            }
            categories.push(record.get(xField));
        });
        if (!me.labelArray) {
            if (display != 'categories') {
                //draw scale
                for (i = 1; i <= steps; i++) {
                    label = surface.add({
                        group: labelGroup,
                        type: 'text',
                        text: round(i / steps * maxValue),
                        x: centerX,
                        y: centerY - rho * i / steps
                    });
                    if (labelStyle) {
                        label.setAttributes(labelStyle, true);
                    }
                    labelArray.push(label);
                }
            }
            if (display != 'scale') {
                //TODO(nico): ignore translate property since positioning is radial.
                delete categoriesStyle.translate;
                //draw text
                for (j = 0, steps = categories.length; j < steps; j++) {
                    angle = rad(rotation + j / steps * 360);
                    dx = cos(angle) * (rho + margin);
                    dy = sin(angle) * (rho + margin);
                    label = surface.add({
                        group: labelGroup,
                        type: 'text',
                        text: categories[j],
                        x: centerX + dx,
                        y: centerY + dy,
                        'text-anchor': dx * dx <= 0.001? 'middle' : (dx < 0? 'end' : 'start')
                    });
                    if (labelStyle) {
                        label.setAttributes(categoriesStyle, true);
                    }
                    labelArray.push(label);
                }
            }
        }
        else {
            labelArray = me.labelArray;
            if (display != 'categories') {
                //draw values
                for (i = 0; i < steps; i++) {
                    labelArray[i].setAttributes({
                        text: round((i + 1) / steps * maxValue),
                        x: centerX,
                        y: centerY - rho * (i + 1) / steps,
                        hidden: false
                    }, true);
                }
            }
            if (display != 'scale') {
                //draw text
                for (j = 0, steps = categories.length; j < steps; j++) {
                    angle = rad(rotation + j / steps * 360);
                    dx = cos(angle) * (rho + margin);
                    dy = sin(angle) * (rho + margin);
                    if (labelArray[i + j]) {
                        labelArray[i + j].setAttributes({
                            type: 'text',
                            text: categories[j],
                            x: centerX + dx,
                            y: centerY + dy,
                            'text-anchor': dx * dx <= 0.001? 'middle' : (dx < 0? 'end' : 'start'),
                            hidden: false
                        }, true);
                    }
                }
            }
        }
        me.labelArray = labelArray;
    },

    getSurface: function() {
        return this.chart.getSurface('main');
    },

    reset: function() {
        this.rotation = 0;
        Ext.chart.axis.Radial.superclass.reset.call(this);
    }

});

/**
 * @class Ext.chart.axis.Time
 * @extends Ext.chart.axis.Axis
 *
 * A type of axis whose units are measured in time values. Use this axis
 * for listing dates that you will want to group or dynamically change.
 * If you just want to display dates as categories then use the
 * Category class for axis instead.
 *
 * For example:
 *
 *     axes: [{
 *         type: 'Time',
 *         position: 'bottom',
 *         fields: 'date',
 *         title: 'Day',
 *         dateFormat: 'M d',
 *         groupBy: 'year,month,day',
 *         aggregateOp: 'sum',
 *
 *         constrain: true,
 *         fromDate: new Date('1/1/11'),
 *         toDate: new Date('1/7/11')
 *     }]
 *
 * In this example we're creating a time axis that has as title *Day*.
 * The field the axis is bound to is `date`.
 * The date format to use to display the text for the axis labels is `M d`
 * which is a three letter month abbreviation followed by the day number.
 * The time axis will show values for dates between `fromDate` and `toDate`.
 * Since `constrain` is set to true all other values for other dates not between
 * the fromDate and toDate will not be displayed.
 *
 * @constructor
 */
Ext.chart.axis.Time = Ext.extend(Ext.chart.axis.Category, {

    /**
     * @cfg {Boolean} calculateByLabelSize
     * The minimum value drawn by the axis. If not set explicitly, the axis
     * minimum will be calculated automatically.
     */
    calculateByLabelSize: true,

    /**
     * @cfg {String/Boolean} dateFormat
     * Indicates the format the date will be rendered on.
     * For example: 'M d' will render the dates as 'Jan 30', etc.
     */
    dateFormat: false,

    /**
     * Indicates the time unit to use for each step. Can be 'day', 'month', 'year' or a comma-separated combination of all of them.
     * Default's 'year,month,day'.
     *
     * @cfg groupBy
     * @type {String}
     */
    groupBy: 'year,month,day',

    /**
     * Aggregation operation when grouping. Possible options are 'sum', 'avg', 'max', 'min'. Default's 'sum'.
     *
     * @cfg aggregateOp
     * @type {String}
     */
    aggregateOp: 'sum',

    /**
     * The starting date for the time axis.
     * @cfg fromDate
     * @type Date
     */
    fromDate: false,

    /**
     * The ending date for the time axis.
     * @cfg toDate
     * @type Date
     */
    toDate: false,

    /**
     * An array with two components: The first is the unit of the step (day, month, year, etc). The second one is the number of units for the step (1, 2, etc.).
     * Default's [Ext.Date.DAY, 1].
     *
     * @cfg step
     * @type Array
     */
    step: [Date.DAY, 1],

    /**
     * If true, the values of the chart will be rendered only if they belong between the fromDate and toDate.
     * If false, the time axis will adapt to the new values by adding/removing steps.
     * Default's [Ext.Date.DAY, 1].
     *
     * @cfg constrain
     * @type Boolean
     */
    constrain: false,

    // @private a wrapper for date methods.
    dateMethods: {
        'year': function(date) {
            return date.getFullYear();
        },
        'month': function(date) {
            return date.getMonth() + 1;
        },
        'day': function(date) {
            return date.getDate();
        },
        'hour': function(date) {
            return date.getHours();
        },
        'minute': function(date) {
            return date.getMinutes();
        },
        'second': function(date) {
            return date.getSeconds();
        },
        'millisecond': function(date) {
            return date.getMilliseconds();
        }
    },

    // @private holds aggregate functions.
    aggregateFn: (function() {
        var etype = (function() {
            var rgxp = /^\[object\s(.*)\]$/,
                toString = Object.prototype.toString;
            return function(e) {
                return toString.call(e).match(rgxp)[1];
            };
        })();
        return {
            'sum': function(list) {
                var i = 0,
                    l = list.length,
                    acum = 0;
                if (!list.length || etype(list[0]) != 'Number') {
                    return list[0];
                }
                for (; i < l; i++) {
                    acum += list[i];
                }
                return acum;
            },
            'max': function(list) {
                if (!list.length || etype(list[0]) != 'Number') {
                    return list[0];
                }
                return Math.max.apply(Math, list);
            },
            'min': function(list) {
                if (!list.length || etype(list[0]) != 'Number') {
                    return list[0];
                }
                return Math.min.apply(Math, list);
            },
            'avg': function(list) {
                var i = 0,
                    l = list.length,
                    acum = 0;
                if (!list.length || etype(list[0]) != 'Number') {
                    return list[0];
                }
                for (; i < l; i++) {
                    acum += list[i];
                }
                return acum / l;
            }
        };
    })(),

    // @private normalized the store to fill date gaps in the time interval.
    constrainDates: function() {
        var fromDate = Ext.Date.clone(this.fromDate),
            toDate = Ext.Date.clone(this.toDate),
            step = this.step,
            fields = this.fields,
            field = fields.length ?  fields[0] : fields,
            store = this.chart.store,
            newStore = new Ext.data.Store({
                model: store.model
            }),
            record, recObj;

        var getRecordByDate = (function() {
            var index = 0,
                l = store.getCount();
            return function(date) {
                var rec, recDate;
                for (; index < l; index++) {
                    rec = store.getAt(index);
                    recDate = rec.get(field);
                    if (+recDate > +date) {
                        return false;
                    } else if (+recDate == +date) {
                        return rec;
                    }
                }
                return false;
            };
        })();

        if (!this.constrain) {
            this.chart.filteredStore = this.chart.store;
            return;
        }

        while (+fromDate <= +toDate) {
            record = getRecordByDate(fromDate);
            recObj = {};
            if (record) {
                newStore.add(record.data);
            } else {
                newStore.model.prototype.fields.each(function(f) {
                    recObj[f.name] = false;
                });
                recObj.date = fromDate;
                newStore.add(recObj);
            }
            fromDate = Ext.Date.add(fromDate, step[0], step[1]);
        }

        this.chart.filteredStore = newStore;
    },

    // @private aggregates values if multiple store elements belong to the same time step.
    aggregate: function() {
        var aggStore = {},
            aggKeys = [],
            key, value, op = this.aggregateOp,
            field = this.fields,
            i, fields = this.groupBy.split(','),
            curField, recFields = [],
            recFieldsLen = 0,
            obj, dates = [],
            json = [],
            l = fields.length,
            dateMethods = this.dateMethods,
            aggregateFn = this.aggregateFn,
            store = this.chart.filteredStore || this.chart.store;

        //make sure we have a single field.
        field = field.length ? field[0] : field;

        store.each(function(rec) {
            //get all record field names in a simple array
            if (!recFields.length) {
                rec.fields.each(function(f) {
                    recFields.push(f.name);
                });
                recFieldsLen = recFields.length;
            }
            //get record date value
            value = rec.get(field);
            //generate key for grouping records
            for (i = 0; i < l; i++) {
                if (i == 0) {
                    key = String(dateMethods[fields[i]](value));
                } else {
                    key += '||' + dateMethods[fields[i]](value);
                }
            }
            //get aggregation record from hash
            if (key in aggStore) {
                obj = aggStore[key];
            } else {
                obj = aggStore[key] = {};
                aggKeys.push(key);
                dates.push(value);
            }
            //append record values to an aggregation record
            for (i = 0; i < recFieldsLen; i++) {
                curField = recFields[i];
                if (!obj[curField]) {
                    obj[curField] = [];
                }
                if (rec.get(curField) !== undefined) {
                    obj[curField].push(rec.get(curField));
                }
            }
        });
        //perform aggregation operations on fields
        for (key in aggStore) {
            obj = aggStore[key];
            for (i = 0; i < recFieldsLen; i++) {
                curField = recFields[i];
                obj[curField] = aggregateFn[op](obj[curField]);
            }
            json.push(obj);
        }
        this.chart.substore = new Ext.data.JsonStore({
            fields: recFields,
            data: json
        });

        this.dates = dates;
    },

    // JCA Compatibility with Date
    // @private creates a label array to be used as the axis labels.
    setLabels: function() {
        var me = this,
            store = me.chart.substore,
            fields = me.fields,
            format = me.dateFormat,
            dates = me.dates,
            labels;
        me.labels = labels = [];
        store.each(function(record, i) {
            if (!format) {
                labels.push(record.get(fields));
            } else {
                labels.push(dates[i].format(format));
            }
        },
        me);
    },

    processView: function() {
        //TODO(nico): fix this eventually...
        if (this.constrain) {
            this.constrainDates();
            this.aggregate();
            this.chart.substore = this.chart.filteredStore;
        } else {
            this.aggregate();
        }
    },

    // @private modifies the store and creates the labels for the axes.
    applyData: function() {
        this.setLabels();
        var count = this.chart.substore.getCount();
        return {
            from: 0,
            to: Math.max(count - 1, 0),
            steps: count - 1,
            step: 1
        };
    }
});

Ext.ns('Ext.chart.series');

/**
 * @class Ext.chart.series.ItemEvents
 *
 * This series mixin defines events that occur on a particular series item, and adds default
 * event handlers for detecting and firing those item interaction events.
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
(function() {
    function createEventRelayMethod(name) {
        return function(e) {
            var me = this,
                item = me.itemForEvent(e);
            if (item) {
                me.fireEvent(name, me, item, e);
            }
        }
    }

    Ext.chart.series.ItemEvents = Ext.extend(Object, {

        constructor: function() {
            var me = this,
                itemEventNames = Ext.chart.series.ItemEvents.itemEventNames;
            me.addEvents.apply(me, itemEventNames);
            me.enableBubble(itemEventNames);
        },

        initEvents: function() {
            var me = this;
            me.chart.on({
                scope: me,
                mousemove: me.onMouseMove,
                mouseup: me.onMouseUp,
                mousedown: me.onMouseDown,
                click: me.onClick,
                doubleclick: me.onDoubleClick,
                tap: me.onTap,
                tapstart: me.onTapStart,
                tapend: me.onTapEnd,
                tapcancel: me.onTapCancel,
                taphold: me.onTapHold,
                doubletap: me.onDoubleTap,
                singletap: me.onSingleTap,
                touchstart: me.onTouchStart,
                touchmove: me.onTouchMove,
                touchend: me.onTouchEnd,
                dragstart: me.onDragStart,
                drag: me.onDrag,
                dragend: me.onDragEnd,
                pinchstart: me.onPinchStart,
                pinch: me.onPinch,
                pinchend: me.onPinchEnd,
                swipe: me.onSwipe
            });
        },

        itemForEvent: function(e) {
            var me = this,
                chartXY = me.chart.getEventXY(e);
            return me.getItemForPoint(chartXY[0], chartXY[1]);
        },

        getBubbleTarget: function() {
            return this.chart;
        },

        onMouseMove: function(e) {
            var me = this,
                lastItem = me.lastOverItem,
                item = me.itemForEvent(e);
            if (lastItem && item !== lastItem) {
                me.fireEvent('itemmouseout', me, lastItem, e);
                delete me.lastOverItem;
            }
            if (item) {
                me.fireEvent('itemmousemove', me, item, e);
            }
            if (item && item !== lastItem) {
                me.fireEvent('itemmouseover', me, item, e);
                me.lastOverItem = item;
            }
        },

        // Events directly relayed when on an item:
        onMouseUp: createEventRelayMethod('itemmouseup'),
        onMouseDown: createEventRelayMethod('itemmousedown'),
        onClick: createEventRelayMethod('itemclick'),
        onDoubleClick: createEventRelayMethod('itemdoubleclick'),
        onTap: createEventRelayMethod('itemtap'),
        onTapStart: createEventRelayMethod('itemtapstart'),
        onTapEnd: createEventRelayMethod('itemtapend'),
        onTapCancel: createEventRelayMethod('itemtapcancel'),
        onTapHold: createEventRelayMethod('itemtaphold'),
        onDoubleTap: createEventRelayMethod('itemdoubletap'),
        onSingleTap: createEventRelayMethod('itemsingletap'),
        onTouchStart: createEventRelayMethod('itemtouchstart'),
        onTouchMove: createEventRelayMethod('itemtouchmove'),
        onTouchEnd: createEventRelayMethod('itemtouchend'),
        onDragStart: createEventRelayMethod('itemdragstart'),
        onDrag: createEventRelayMethod('itemdrag'),
        onDragEnd: createEventRelayMethod('itemdragend'),
        onPinchStart: createEventRelayMethod('itempinchstart'),
        onPinch: createEventRelayMethod('itempinch'),
        onPinchEnd: createEventRelayMethod('itempinchend'),
        onSwipe: createEventRelayMethod('itemswipe')

    });


    Ext.chart.series.ItemEvents.itemEventNames = [
        /**
         * @event itemmousemove
         * Fires when the mouse is moved on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemmousemove',
        /**
         * @event itemmouseup
         * Fires when a mouseup event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemmouseup',
        /**
         * @event itemmousedown
         * Fires when a mousedown event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemmousedown',
        /**
         * @event itemmouseover
         * Fires when the mouse enters a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemmouseover',
        /**
         * @event itemmouseout
         * Fires when the mouse exits a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemmouseout',
        /**
         * @event itemclick
         * Fires when a click event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemclick',
        /**
         * @event itemdoubleclick
         * Fires when a doubleclick event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemdoubleclick',
        /**
         * @event itemtap
         * Fires when a tap event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtap',
        /**
         * @event itemtapstart
         * Fires when a tapstart event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtapstart',
        /**
         * @event itemtapend
         * Fires when a tapend event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtapend',
        /**
         * @event itemtapcancel
         * Fires when a tapcancel event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtapcancel',
        /**
         * @event itemtaphold
         * Fires when a taphold event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtaphold',
        /**
         * @event itemdoubletap
         * Fires when a doubletap event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemdoubletap',
        /**
         * @event itemsingletap
         * Fires when a singletap event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemsingletap',
        /**
         * @event itemtouchstart
         * Fires when a touchstart event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtouchstart',
        /**
         * @event itemtouchmove
         * Fires when a touchmove event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtouchmove',
        /**
         * @event itemtouchend
         * Fires when a touchend event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemtouchend',
        /**
         * @event itemdragstart
         * Fires when a dragstart event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemdragstart',
        /**
         * @event itemdrag
         * Fires when a drag event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemdrag',
        /**
         * @event itemdragend
         * Fires when a dragend event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemdragend',
        /**
         * @event itempinchstart
         * Fires when a pinchstart event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itempinchstart',
        /**
         * @event itempinch
         * Fires when a pinch event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itempinch',
        /**
         * @event itempinchend
         * Fires when a pinchend event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itempinchend',
        /**
         * @event itemswipe
         * Fires when a swipe event occurs on a series item.
         * @param {Ext.chart.series.Series} series
         * @param {Object} item
         * @param {Event} event
         */
        'itemswipe'

        // TODO itemtouchenter, itemtouchleave?
    ];

})();
/**
 * @class Ext.chart.series.Series
 *
 * Series is the abstract class containing the common logic to all chart series. Series includes
 * methods from Labels, Highlights, Tips and Callouts mixins. This class implements the logic of
 * animating, hiding, showing all elements and returning the color of the series to be used as a legend item.
 *
 * ## Listeners
 *
 * The series class supports listeners via the Observable syntax. Some of these listeners are:
 *
 *  - `itemmouseup` When the user interacts with a marker.
 *  - `itemmousedown` When the user interacts with a marker.
 *  - `itemmousemove` When the user iteracts with a marker.
 *  - (similar `item*` events occur for many raw mouse and touch events)
 *  - `afterrender` Will be triggered when the animation ends or when the series has been rendered completely.
 *
 * For example:
 *
 *     series: [{
 *             type: 'column',
 *             axis: 'left',
 *             listeners: {
 *                     'afterrender': function() {
 *                             console('afterrender');
 *                     }
 *             },
 *             xField: 'category',
 *             yField: 'data1'
 *     }]
 *
 */
Ext.ns('Ext.chart.series');
Ext.chart.series.Series = Ext.extend(Ext.util.Observable, {

    // TODO make into interaction:
    /**
     * @cfg {Object} tips
     * Add tooltips to the visualization's markers. The options for the tips are the
     * same configuration used with {@link Ext.tip.ToolTip}. For example:
     *
     *     tips: {
     *       trackMouse: true,
     *       width: 140,
     *       height: 28,
     *       renderer: function(storeItem, item) {
     *         this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' views');
     *       }
     *     },
     */

    /**
     * @protected {String} type
     * The type of series. Set in subclasses.
     */
    type: null,

    /**
     * @cfg {String} title
     * The human-readable name of the series.
     */
    title: null,

    /**
     * @cfg {Boolean} showInLegend
     * Whether to show this series in the legend.
     */
    showInLegend: true,

    /**
     * @cfg {Function} renderer
     * A function that can be overridden to set custom styling properties to each rendered element.
     * Passes in (sprite, record, attributes, index, store) to the function.
     */
    renderer: function(sprite, record, attributes, index, store) {
        return attributes;
    },

    /**
     * @cfg {Array} shadowAttributes
     * An array with shadow attributes
     */
    shadowAttributes: null,

    //@private triggerdrawlistener flag
    triggerAfterDraw: false,

    constructor: function(config) {
        var me = this;

        //new fresh object as own property.
        me.style = {};
        me.themeStyle = {};

        if (config) {
            Ext.apply(me, config);
        }

        me.shadowGroups = [];
        me.markerStyle = new Ext.chart.theme.MarkerStyle();
        me.labelStyle = new Ext.chart.theme.LabelStyle();

        Ext.chart.Label.prototype.constructor.call(me, config);
        Ext.chart.Highlight.prototype.constructor.call(me, config);
        Ext.chart.Callout.prototype.constructor.call(me, config);
        Ext.chart.Transformable.prototype.constructor.call(me);
        Ext.chart.series.ItemEvents.prototype.constructor.call(me);

        me.addEvents({
            beforedraw: true,
            draw: true,
            afterdraw: true,

            /**
             * @event titlechange
             * Fires when the series title is changed via {@link #setTitle}.
             * @param {String} title The new title value
             * @param {Number} index The index in the collection of titles
             */
            titlechange: true
        });

        me.initEvents();

        Ext.chart.series.Series.superclass.constructor.call(me, config);

    },

    /**
     * @private get the surface for drawing the series sprites
     */
    getSurface: function() {
        var me = this,
            surface = me.surface;
        if (!surface) {
            surface = me.surface = me.chart.getSurface('series' + me.index);
            surface.el.setStyle('zIndex', me.chart.surfaceZIndexes.series);
        }
        return surface;
    },

    /**
     * @private get the surface for drawing the series overlay sprites
     */
    getOverlaySurface: function() {
        var me = this,
            surface = me.overlaySurface;
        if (!surface) {
            surface = me.overlaySurface = me.chart.getSurface('seriesOverlay' + me.index);
            surface.el.setStyle('zIndex', me.chart.surfaceZIndexes.overlay);
        }
        return surface;
    },

    // @private set the bbox and clipBox for the series
    setBBox: function(noGutter) {
        var me = this,
            chart = me.chart,
            chartBBox = chart.chartBBox,
            gutterX = noGutter ? 0 : chart.maxGutter[0],
            gutterY = noGutter ? 0 : chart.maxGutter[1],
            clipBox, bbox;

        clipBox = {
            x: 0,
            y: 0,
            width: chartBBox.width,
            height: chartBBox.height
        };
        me.clipBox = clipBox;

        bbox = {
            x: ((clipBox.x + gutterX) - (chart.zoom.x * chart.zoom.width)) * me.zoomX,
            y: ((clipBox.y + gutterY) - (chart.zoom.y * chart.zoom.height)) * me.zoomY,
            width: (clipBox.width - (gutterX * 2)) * chart.zoom.width * me.zoomX,
            height: (clipBox.height - (gutterY * 2)) * chart.zoom.height * me.zoomY
        };
        me.bbox = bbox;
    },

    // @private set the animation for the sprite
    onAnimate: function(sprite, attr) {
        var me = this;
        sprite.stopAnimation();
        if (me.triggerAfterDraw) {
            return sprite.animate(Ext.applyIf(attr, me.chart.animate));
        } else {
            me.triggerAfterDraw = true;
            return sprite.animate(Ext.apply(Ext.applyIf(attr, me.chart.animate), {
                listeners: {
                    'afteranimate': function() {
                        me.triggerAfterDraw = false;
                        me.fireEvent('afterrender');
                    }
                }
            }));
        }
    },

    // @private return the gutter.
    getGutters: function() {
        return [0, 0];
    },

    /**
     * For a given x/y point relative to the Surface, find a corresponding item from this
     * series, if any.
     * @param {Number} x
     * @param {Number} y
     * @return {Object} An object describing the item, or null if there is no matching item. The exact contents of
     *                  this object will vary by series type, but should always contain at least the following:
     *                  <ul>
     *                    <li>{Ext.chart.series.Series} series - the Series object to which the item belongs</li>
     *                    <li>{Object} value - the value(s) of the item's data point</li>
     *                    <li>{Array} point - the x/y coordinates relative to the chart box of a single point
     *                        for this data item, which can be used as e.g. a tooltip anchor point.</li>
     *                    <li>{Ext.draw.Sprite} sprite - the item's rendering Sprite.
     *                  </ul>
     */
    getItemForPoint: function(x, y) {
        var me = this,
            items = me.items,
            bbox = me.bbox,
            i, ln;

        if (items && items.length && !me.seriesIsHidden && Ext.draw.Draw.withinBox(x, y, bbox)) {
            // Adjust for series pan
            x -= me.panX;
            y -= me.panY;

            // Check bounds
            for (i = 0, ln = items.length; i < ln; i++) {
                if (items[i] && me.isItemInPoint(x, y, items[i], i)) {
                    return items[i];
                }
            }
        }

        return null;
    },

    isItemInPoint: function() {
        return false;
    },

    /**
     * Hides all the elements in the series.
     */
    hideAll: function() {
        var me = this,
            items = me.items,
            item, len, i, j, l, sprite, shadows;

        me.seriesIsHidden = true;
        me._prevShowMarkers = me.showMarkers;

        me.showMarkers = false;
        //hide all labels
        me.hideLabels(0);
        //hide all sprites
        for (i = 0, len = items.length; i < len; i++) {
            item = items[i];
            sprite = item.sprite;
            if (sprite) {
                sprite.setAttributes({
                    hidden: true
                }, true);
                
                if (sprite.shadows) {
                    shadows = sprite.shadows;
                    for (j = 0, l = shadows.length; j < l; ++j) {
                        shadows[j].hide(true);
                    }
                }
            }

        }
    },

    /**
     * Shows all the elements in the series.
     */
    showAll: function() {
        var me = this,
            prevAnimate = me.chart.animate;
        me.chart.animate = false;
        me.seriesIsHidden = false;
        me.showMarkers = me._prevShowMarkers;
        me.drawSeries();
        me.chart.animate = prevAnimate;
    },

    /**
     * Performs drawing of this series.
     */
    drawSeries: function() {
        this.updateSurfaceBox();
    },

    /**
     * Returns an array of labels to be displayed as items in the legend. Only relevant if
     * {@link #showInLegend} is true.
     */
    getLegendLabels: function() {
        var title = this.title;
        return title ? [title] : [];
    },

    getColorFromStyle: function(style) {
        if (Ext.isObject(style)) {
            return style.stops[0].color;
        }
        //if it's a gradient just return the first color stop.
        return style.indexOf('url') == -1 ? style : me.getSurface('main')._gradients[style.match(/url\(#([^\)]+)\)/)[1]].stops[0].color;
    },

    /**
     * Returns a string with the color to be used for the series legend item.
     */
    getLegendColor: function(index) {
        var me = this, fill, stroke;

        if (me.style) {
            fill = me.style.fill;
            stroke = me.style.stroke;
            if (fill && fill != 'none') {
                return me.getColorFromStyle(fill);
            }
            return me.getColorFromStyle(stroke);
        }

        return '#000';
    },

    /**
     * Checks whether the data field should be visible in the legend
     * @private
     * @param {Number} index The index of the current item
     */
    visibleInLegend: function(index){
        return !this.seriesIsHidden && !this.isExcluded(index);
    },

    /**
     * Changes the value of the {@link #title} for the series.
     * Arguments can take two forms:
     * <ul>
     * <li>A single String value: this will be used as the new single title for the series (applies
     * to series with only one yField)</li>
     * <li>A numeric index and a String value: this will set the title for a single indexed yField.</li>
     * </ul>
     * @param {Number} index
     * @param {String} title
     */
    setTitle: function(index, title) {
        var me = this,
            oldTitle = me.title;

        if (Ext.isString(index)) {
            title = index;
            index = 0;
        }

        if (Ext.isArray(oldTitle)) {
            oldTitle[index] = title;
        } else {
            me.title = title;
        }

        me.fireEvent('titlechange', title, index);
    },

    /**
     * @private update the position/size of the series surface
     */
    updateSurfaceBox: function() {
        var me = this,
            surface = me.getSurface(),
            overlaySurface = me.getOverlaySurface(),
            chartBBox = me.chart.chartBBox;

        surface.el.setTopLeft(chartBBox.y, chartBBox.x);
        surface.setSize(chartBBox.width, chartBBox.height);

        overlaySurface.el.setTopLeft(chartBBox.y, chartBBox.x);
        overlaySurface.setSize(chartBBox.width, chartBBox.height);
    },

    getTransformableSurfaces: function() {
        // Need to transform the overlay surface along with the normal surface
        // TODO might be good to skip transforming the overlay surface if there is nothing in it
        return [this.getSurface(), this.getOverlaySurface()];
    },

    /**
     * Iterate over each of the records for this series. The default implementation simply iterates
     * through the entire data store, but individual series implementations can override this to
     * provide custom handling, e.g. adding/removing records.
     * @param {Function} fn The function to execute for each record.
     * @param {Object} scope Scope for the fn.
     */
    eachRecord: function(fn, scope) {
        var chart = this.chart;
        (chart.substore || chart.store).each(fn, scope);
    },

    /**
     * Return the number of records being displayed in this series. Defaults to the number of
     * records in the store; individual series implementations can override to provide custom handling.
     */
    getRecordCount: function() {
        var chart = this.chart,
            store = chart.substore || chart.store;
        return store ? store.getCount() : 0;
    },

    /**
     * Determines whether the series item at the given index has been excluded, i.e. toggled off in the legend.
     * @param index
     */
    isExcluded: function(index) {
        var excludes = this.__excludes;
        return !!(excludes && excludes[index]);
    },

    /**
     * Combine two of this series's indexed items into one. This is done via drag-drop on the
     * legend for series that render more than one legend item. The data store is not modified,
     * but the series uses the cumulative list of combinations in its rendering.
     * @param {Number} index1 Index of the first item
     * @param {Number} index2 Index of the second item
     */
    combine: function(index1, index2) {
        var me = this,
            combinations = me.combinations || (me.combinations = []),
            excludes = me.__excludes;
        combinations.push([index1, index2]);
        if (excludes && index1 < excludes.length) {
            excludes.splice(index1, 1);
        }
    },

    /**
     * Determines whether the item at the given index is the result of item combination.
     * @param {Number} index
     * @return {Boolean}
     */
    isCombinedItem: function(index) {
        return this.getCombinationIndexesForItem(index).length > 0;
    },

    getCombinationIndexesForItem: function(index) {
        var me = this,
            combinations = me.combinations,
            provenances = [],
            i, len, combo, comboIndexA, comboIndexB;
        if (combinations) {
            // Step through the combinations to determine which combination step(s) contribute
            // to the item at the given index, if any
            for (i = 0, len = combinations.length; i < len; i++) {
                combo = combinations[i];
                comboIndexA = combo[0];
                comboIndexB = combo[1];
                if (!provenances[comboIndexB]) {
                    provenances[comboIndexB] = [];
                }
                if (provenances[comboIndexA]) {
                    provenances[comboIndexB] = provenances[comboIndexB].concat(provenances[comboIndexA]);
                }
                provenances[comboIndexB].push(i);
                provenances.splice(comboIndexA, 1);
            }
        }
        return provenances[index] || [];
    },

    split: function(index) {
        var me = this,
            combinations = me.combinations,
            excludes = me.__excludes,
            i, j, len, comboIndexes, combo, movedItemIndex;

        if (combinations) {
            comboIndexes = me.getCombinationIndexesForItem(index);

            // For each contributing combination, remove it from the list and adjust the indexes
            // of all subsequent combinations and excludes to account for it
            if (comboIndexes) {
                for (i = comboIndexes.length; i--;) {
                    movedItemIndex = combinations[comboIndexes[i]][0];
                    for (j = comboIndexes[i] + 1, len = combinations.length; j < len; j++) {
                        if (movedItemIndex <= combinations[j][0]) {
                            combinations[j][0]++;
                        }
                        if (movedItemIndex <= combinations[j][1]) {
                            combinations[j][1]++;
                        }
                    }
                    combinations.splice(comboIndexes[i], 1);

                    if (excludes) {
                        excludes.splice(movedItemIndex, 0, false);
                    }
                }
            }

            // Now that the combinations list is updated, reset and replay them all
            me.clearCombinations();
            for (i = 0, len = combinations.length; i < len; i++) {
                combo = combinations[i];
                me.combine(combo[0], combo[1]);
            }
        }
    },

    /**
     * Split any series items that were combined via {@link #combine} into their original items.
     */
    clearCombinations: function() {
        delete this.combinations;
    },

    /**
     * Reset the series to its original state, before any user interaction.
     */
    reset: function() {
        var me = this;
        me.unHighlightItem();
        me.cleanHighlights();
        me.clearTransform();
    },

    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    //filled by the constructor.
    ownerCt: null,

    getItemId: function() {
        return this.el && this.el.id || this.id || null;
    },

    initCls: function() {
        return (this.cls || '').split(' ');
    },

    isXType: function(xtype) {
        return xtype === 'series';
    },

    getRefItems: function(deep) {
        var me = this,
            ans = [];

        if (me.markerStyle) {
            ans.push(me.markerStyle);
        }

        if (me.labelStyle) {
            ans.push(me.labelStyle);
        }

        if (me.calloutStyle) {
            ans.push(me.calloutStyle);
        }
        
        if (me.highlightStyle) {
            ans.push(me.highlightStyle);
        }

        return ans;
    }
});

Ext.applyIf(Ext.chart.series.Series.prototype, Ext.chart.Label.prototype);
Ext.applyIf(Ext.chart.series.Series.prototype, Ext.chart.Highlight.prototype);
Ext.applyIf(Ext.chart.series.Series.prototype, Ext.chart.Callout.prototype);
Ext.applyIf(Ext.chart.series.Series.prototype, Ext.chart.Transformable.prototype);
Ext.applyIf(Ext.chart.series.Series.prototype, Ext.chart.series.ItemEvents.prototype);

/**
 * @class Ext.chart.series.Cartesian
 * @extends Ext.chart.series.Series
 *
 * Common base class for series implementations which plot values using x/y coordinates.
 *
 * @constructor
 */
Ext.chart.series.Cartesian = Ext.extend(Ext.chart.series.Series, {

    /**
     * The field used to access the x axis value from the items from the data
     * source.
     *
     * @cfg xField
     * @type String
     */
    xField: null,

    /**
     * The field used to access the y-axis value from the items from the data
     * source.
     *
     * @cfg yField
     * @type String
     */
    yField: null,

    /**
     * @cfg {String} axis
     * The position of the axis to bind the values to. Possible values are 'left', 'bottom', 'top' and 'right'.
     * You must explicitly set this value to bind the values of the line series to the ones in the axis, otherwise a
     * relative scale will be used.
     */
    axis: 'left',

    getLegendLabels: function() {
        var me = this,
            labels = [],
            combinations = me.combinations;

        Ext.each([].concat(me.yField), function(yField, i) {
            var title = me.title;
            // Use the 'title' config if present, otherwise use the raw yField name
            labels.push((Ext.isArray(title) ? title[i] : title) || yField);
        });

        // Handle yFields combined via legend drag-drop
        if (combinations) {
            Ext.each(combinations, function(combo) {
                var label0 = labels[combo[0]],
                    label1 = labels[combo[1]];
                labels[combo[1]] = label0 + ' & ' + label1;
                labels.splice(combo[0], 1);
            });
        }

        return labels;
    },

    /**
     * @protected Iterates over a given record's values for each of this series's yFields,
     * executing a given function for each value. Any yFields that have been combined
     * via legend drag-drop will be treated as a single value.
     * @param {Ext.data.Model} record
     * @param {Function} fn
     * @param {Object} scope
     */
    eachYValue: function(record, fn, scope) {
        Ext.each(this.getYValueAccessors(), function(accessor, i) {
            fn.call(scope, accessor(record), i);
        });
    },

    /**
     * @protected Returns the number of yField values, taking into account fields combined
     * via legend drag-drop.
     * @return {Number}
     */
    getYValueCount: function() {
        return this.getYValueAccessors().length;
    },

    combine: function(index1, index2) {
        var me = this,
            accessors = me.getYValueAccessors(),
            accessor1 = accessors[index1],
            accessor2 = accessors[index2];

        // Combine the yValue accessors for the two indexes into a single accessor that returns their sum
        accessors[index2] = function(record) {
            return accessor1(record) + accessor2(record);
        };
        accessors.splice(index1, 1);

        Ext.chart.series.Cartesian.superclass.combine.call(me, index1, index2);
    },

    clearCombinations: function() {
        // Clear combined accessors, they'll get regenerated on next call to getYValueAccessors
        delete this.yValueAccessors;
        Ext.chart.series.Cartesian.superclass.clearCombinations.call(this);
    },

    /**
     * @protected Returns an array of functions, each of which returns the value of the yField
     * corresponding to function's index in the array, for a given record (each function takes the
     * record as its only argument.) If yFields have been combined by the user via legend drag-drop,
     * this list of accessors will be kept in sync with those combinations.
     * @return {Array} array of accessor functions
     */
    getYValueAccessors: function() {
        var me = this,
            accessors = me.yValueAccessors;
        if (!accessors) {
            accessors = me.yValueAccessors = [];
            Ext.each([].concat(me.yField), function(yField) {
                accessors.push(function(record) {
                    return record.get(yField);
                });
            });
        }
        return accessors;
    },

    /**
     * Calculate the min and max values for this series's xField.
     * @return {Array} [min, max]
     */
    getMinMaxXValues: function() {
        var me = this,
            min, max,
            xField = me.xField;

        if (me.getRecordCount() > 0) {
            min = Infinity;
            max = -min;
            me.eachRecord(function(record) {
                var xValue = record.get(xField);
                if (xValue > max) {
                    max = xValue;
                }
                if (xValue < min) {
                    min = xValue;
                }
            });
        } else {
            min = max = 0;
        }
        return [min, max];
    },

    /**
     * Calculate the min and max values for this series's yField(s). Takes into account yField
     * combinations, exclusions, and stacking.
     * @return {Array} [min, max]
     */
    getMinMaxYValues: function() {
        var me = this,
            stacked = me.stacked,
            min, max,
            positiveTotal, negativeTotal;

        function eachYValueStacked(yValue, i) {
            if (!me.isExcluded(i)) {
                if (yValue < 0) {
                    negativeTotal += yValue;
                } else {
                    positiveTotal += yValue;
                }
            }
        }

        function eachYValue(yValue, i) {
            if (!me.isExcluded(i)) {
                if (yValue > max) {
                    max = yValue;
                }
                if (yValue < min) {
                    min = yValue;
                }
            }
        }

        if (me.getRecordCount() > 0) {
            min = Infinity;
            max = -min;
            me.eachRecord(function(record) {
                if (stacked) {
                    positiveTotal = 0;
                    negativeTotal = 0;
                    me.eachYValue(record, eachYValueStacked);
                    if (positiveTotal > max) {
                        max = positiveTotal;
                    }
                    if (negativeTotal < min) {
                        min = negativeTotal;
                    }
                } else {
                    me.eachYValue(record, eachYValue);
                }
            });
        } else {
            min = max = 0;
        }
        return [min, max];
    }

});

/**
 * Creates a Bar Chart. A Bar Chart is a useful visualization technique to display quantitative information for
 * different categories that can show some progression (or regression) in the dataset. As with all other series, the Bar
 * Series must be appended in the *series* Chart array configuration. See the Chart documentation for more information.
 * A typical configuration object for the bar series could be:
 *
 * {@img Ext.chart.series.Bar/Ext.chart.series.Bar.png Ext.chart.series.Bar chart series}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *         ]
 *     });
 *     
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             position: 'bottom',
 *             fields: ['data1'],
 *             label: {
 *                 renderer: Ext.util.Format.numberRenderer('0,0')
 *             },
 *             title: 'Sample Values',
 *             grid: true,
 *             minimum: 0
 *         }, {
 *             type: 'Category',
 *             position: 'left',
 *             fields: ['name'],
 *             title: 'Sample Metrics'
 *         }],
 *         series: [{
 *             type: 'bar',
 *             axis: 'bottom',
 *             highlight: true,
 *             tips: {
 *               trackMouse: true,
 *               width: 140,
 *               height: 28,
 *               renderer: function(storeItem, item) {
 *                 this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' views');
 *               }
 *             },
 *             label: {
 *               display: 'insideEnd',
 *                 field: 'data1',
 *                 renderer: Ext.util.Format.numberRenderer('0'),
 *                 orientation: 'horizontal',
 *                 color: '#333',
 *                 'text-anchor': 'middle'
 *             },
 *             xField: 'name',
 *             yField: ['data1']
 *         }]
 *     });
 *
 * In this configuration we set `bar` as the series type, bind the values of the bar to the bottom axis and set the
 * xField or category field to the `name` parameter of the store. We also set `highlight` to true which enables smooth
 * animations when bars are hovered. We also set some configuration for the bar labels to be displayed inside the bar,
 * to display the information found in the `data1` property of each element store, to render a formated text with the
 * `Ext.util.Format` we pass in, to have an `horizontal` orientation (as opposed to a vertical one) and we also set
 * other styles like `color`, `text-anchor`, etc.
 */
Ext.chart.series.Bar = Ext.extend(Ext.chart.series.Cartesian, {

    type: 'bar',

    /**
     * @private {Boolean} column Whether to set the visualization as column chart or horizontal bar chart.
     */
    column: false,
    
    axis: 'bottom',
    /**
     * @cfg style Style properties that will override the theming series styles.
     */
    
    /**
     * @cfg {Number} gutter The gutter space between single bars, as a percentage of the bar width
     */
    gutter: 38.2,

    /**
     * @cfg {Number} groupGutter The gutter space between groups of bars, as a percentage of the bar width
     */
    groupGutter: 38.2,

    /**
     * @cfg {Number} xPadding Padding between the left/right axes and the bars
     */
    xPadding: 0,

    /**
     * @cfg {Number} yPadding Padding between the top/bottom axes and the bars
     */
    yPadding: 10,
	
	xJustify: false,

    /**
     * @private
     * @property disjointStacked
     * @type Boolean
     * If set to `true`, then if `stacked:true` the bars will be drawn stacked in terms of their
     * y-values but remain side-by-side in the x-direction, basically a hybrid between stacked and
     * grouped. This is only used internally to support an intermediate animation state when
     * toggling between stacked and grouped (see the ToggleStacked interaction).
     */

    constructor: function(config) {
        Ext.chart.series.Bar.superclass.constructor.apply(this, arguments);
        var me = this,
            surface = me.getSurface(),
            shadow = me.chart.shadow,
            i, l;
        Ext.apply(me, config, {
            shadowAttributes: surface.getShadowAttributesArray(),
            shadowOptions: Ext.apply(surface.getShadowOptions(), shadow === true ? {} : (shadow || {}))
        });
        me.group = surface.getGroup(me.seriesId + '-bars');
        if (shadow) {
            for (i = 0, l = me.shadowAttributes.length; i < l; i++) {
                me.shadowGroups.push(surface.getGroup(me.seriesId + '-shadows' + i));
            }
        }
        me.initialStacked = me.stacked;
    },

    // @private sets the bar girth.
    getBarGirth: function() {
        var me = this,
            column = me.column,
            ln = me.getRecordCount(),
            gutter = me.gutter / 100;
        
        return (me.chart.chartBBox[column ? 'width' : 'height'] - me[column ? 'xPadding' : 'yPadding'] * 2) / (ln * (gutter + 1) - gutter);
    },

    // @private returns the gutters.
    getGutters: function() {
        var me = this,
            column = me.column,
            gutter = Math.ceil((column ? me.xPadding : me.yPadding) + me.getBarGirth() / 2);
        return column ? [gutter, 0] : [0, gutter];
    },

    // @private Get chart and data boundaries
    getBounds: function() {
        var me = this,
            chart = me.chart,
            barsLen = me.getYValueCount(),
            visibleBarsLen = barsLen,
            groupGutter = me.groupGutter / 100,
            column = me.column,
            zoomX = me.zoomX,
            zoomY = me.zoomY,
            xPadding = me.xPadding * zoomX,
            yPadding = me.yPadding * zoomY,
            stacked = me.stacked,
            disjointStacked = me.disjointStacked,
            barWidth = me.getBarGirth() * (column ? zoomX : zoomY),
            math = Math,
            mmax = math.max,
            mabs = math.abs,
            groupBarWidth, bbox, minY, maxY, axis, out,
            scale, zero, total, j, plus, minus, recordIndex;

        me.setBBox(true);
        bbox = me.bbox;

        //Skip excluded yFields
        for (j = 0, total = barsLen; j < total; j++) {
            if (me.isExcluded(j)) {
                visibleBarsLen--;
            }
        }

        if (me.axis) {
            axis = chart.axes.get(me.axis);
            if (axis) {
                out = axis.calcEnds();
                minY = out.from;
                maxY = out.to;
            }
        }

        if (me.yField && !Ext.isNumber(minY)) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.yField)
            });
            out = axis.calcEnds();
            minY = out.from;
            maxY = out.to;
        }

        if (!Ext.isNumber(minY)) {
            minY = 0;
        }
        if (!Ext.isNumber(maxY)) {
            maxY = 0;
        }
        scale = (column ? bbox.height - yPadding * 2 : bbox.width - xPadding * 2) / (maxY - minY);
        groupBarWidth = barWidth / ((stacked && !disjointStacked ? 1 : visibleBarsLen) * (groupGutter + 1) - groupGutter);
        zero = (column) ? bbox.y + bbox.height - yPadding : bbox.x + xPadding;


        function eachYValue(yValue, i) {
            if (!me.isExcluded(i)) {
                total[yValue > 0 ? 1 : 0][recordIndex] += mabs(yValue);
            }
        }

        if (stacked) {
            total = [[], []];
            me.eachRecord(function(record, i) {
                total[0][i] = total[0][i] || 0;
                total[1][i] = total[1][i] || 0;
                recordIndex = i;
                me.eachYValue(record, eachYValue);
            });
            total[+(maxY > 0)].push(mabs(maxY));
            total[+(minY > 0)].push(mabs(minY));
            minus = mmax.apply(math, total[0]);
            plus = mmax.apply(math, total[1]);
            scale = (column ? bbox.height - yPadding * 2 : bbox.width - xPadding * 2) / (plus + minus);
            zero = zero + minus * scale * (column ? -1 : 1);
        }
        else if (minY / maxY < 0) {
            zero = zero - minY * scale * (column ? -1 : 1);
        }
		
		if(me.xJustify){
			bbox.x = bbox.x-barWidth/2;
		}
		
		
        return {
            bbox: bbox,
            barsLen: barsLen,
            visibleBarsLen: visibleBarsLen,
            barWidth: barWidth,
            groupBarWidth: groupBarWidth,
            scale: scale,
            zero: zero,
            xPadding: xPadding,
            yPadding: yPadding,
            signed: minY / maxY < 0,
            minY: minY,
            maxY: maxY
        };
    },

    // @private Build an array of paths for the chart
    getPaths: function() {
        var me = this,
            chart = me.chart,
            bounds = me.bounds = me.getBounds(),
            items = me.items = [],
            gutter = me.gutter / 100,
            groupGutter = me.groupGutter / 100,
            animate = chart.animate,
            column = me.column,
            group = me.group,
            enableShadows = chart.shadow,
            shadowGroups = me.shadowGroups,
            shadowGroupsLn = shadowGroups.length,
            bbox = bounds.bbox,
            xPadding = me.xPadding * me.zoomX,
            yPadding = me.yPadding * me.zoomY,
            stacked = me.stacked,
            disjointStacked = me.disjointStacked,
            barsLen = bounds.barsLen,
            colors = me.colorArrayStyle,
            colorLength = colors && colors.length || 0,
            math = Math,
            mmax = math.max,
            mabs = math.abs,
            total = me.getRecordCount(),
            height, totalDim, totalNegDim, bottom, top, hasShadow, barAttr, attrs, counter,
            shadowIndex, shadow, sprite, offset, floorY, recordIndex, currentRecord;

        function eachYValue(yValue, i) {
            // Excluded series
            if (me.isExcluded(i)) {
                return;
            }

            height = Math.round(yValue * bounds.scale);
            barAttr = {
                fill: colors[(barsLen > 1 ? i : 0) % colorLength]
            };
            if (column) {
                Ext.apply(barAttr, {
                    height: height,
                    width: mmax(bounds.groupBarWidth, 0),
                    x: (bbox.x + xPadding + recordIndex * bounds.barWidth * (1 + gutter) + counter * bounds.groupBarWidth * (1 + groupGutter) * (!stacked || disjointStacked ? 1 : 0)),
                    y: bottom - height
                });
            }
            else {
                // draw in reverse order
                offset = (total - 1) - recordIndex;
                Ext.apply(barAttr, {
                    height: mmax(bounds.groupBarWidth, 0),
                    width: height + (bottom == bounds.zero),
                    x: bottom + (bottom != bounds.zero),
                    y: (bbox.y + yPadding + offset * bounds.barWidth * (1 + gutter) + counter * bounds.groupBarWidth * (1 + groupGutter) * (!stacked || disjointStacked ? 1 : 0) + 1)
                });
            }
            if (height < 0) {
                if (column) {
                    barAttr.y = top;
                    barAttr.height = mabs(height);
                } else {
                    barAttr.x = top + height;
                    barAttr.width = mabs(height);
                }
            }
            if (stacked) {
                if (height < 0) {
                    top += height * (column ? -1 : 1);
                } else {
                    bottom += height * (column ? -1 : 1);
                }
                totalDim += mabs(height);
                if (height < 0) {
                    totalNegDim += mabs(height);
                }
            }
            barAttr.x = Math.floor(barAttr.x) + 1;
            floorY = Math.floor(barAttr.y);
            if (!Ext.isIE9 && barAttr.y > floorY) {
                floorY--;
            }
            barAttr.y = floorY;
			
                var br = Ext.apply({},barAttr);
				if(me.xJustify && i==0){
					br.width =Math.floor(barAttr.width/2);
					br.x = br.x+br.width;
				}else{
					br.width =Math.floor(barAttr.width);
				}
                br.height = Math.floor(barAttr.height);

            items.push({
                series: me,
                storeItem: currentRecord,
                value: [currentRecord.get(me.xField), yValue],
                attr: br,
                point: column ? [br.x + br.width / 2, yValue >= 0 ? br.y : br.y + br.height] :
                                [yValue >= 0 ? br.x + br.width : br.x, br.y + br.height / 2]
            });
            // When resizing, reset before animating
            if (animate && chart.resizing) {
                attrs = column ? {
                    x: br.x,
                    y: bounds.zero,
                    width: br.width,
                    height: 0
                } : {
                    x: bounds.zero,
                    y: br.y,
                    width: 0,
                    height: br.height
                };
                if (enableShadows && (stacked && !hasShadow || !stacked)) {
                    hasShadow = true;
                    //update shadows
                    for (shadowIndex = 0; shadowIndex < shadowGroupsLn; shadowIndex++) {
                        shadow = shadowGroups[shadowIndex].getAt(stacked ? recordIndex : (recordIndex * barsLen + i));
                        if (shadow) {
                            shadow.setAttributes(attrs, true);
                        }
                    }
                }
                //update sprite position and width/height
                sprite = group.getAt(recordIndex * barsLen + i);
                if (sprite) {
                    sprite.setAttributes(attrs, true);
                }
            }
            counter++;
        }

        me.eachRecord(function(record, i) {
            bottom = top = bounds.zero;
            totalDim = 0;
            totalNegDim = 0;
            hasShadow = false;
            counter = 0;
            currentRecord = record;
            recordIndex = i;
            me.eachYValue(record, eachYValue);
            if (stacked && items.length) {
                items[i * counter].totalDim = totalDim;
                items[i * counter].totalNegDim = totalNegDim;
            }
        }, me);
    },

    // @private render/setAttributes on the shadows
    renderShadows: function(i, barAttr, baseAttrs, bounds) {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            animate = chart.animate,
            stacked = me.stacked,
            shadowGroups = me.shadowGroups,
            shadowAttributes = me.shadowAttributes,
            shadowGroupsLn = shadowGroups.length,
            store = chart.substore || chart.store,
            column = me.column,
            items = me.items,
            shadows = [],
            zero = bounds.zero,
            shadowIndex, shadowBarAttr, shadow, totalDim, totalNegDim, j, rendererAttributes;

        if ((stacked && (i % bounds.visibleBarsLen === 0)) || !stacked) {
            j = i / bounds.visibleBarsLen;
            //create shadows
            for (shadowIndex = 0; shadowIndex < shadowGroupsLn; shadowIndex++) {
                shadowBarAttr = Ext.apply({}, shadowAttributes[shadowIndex]);
                shadow = shadowGroups[shadowIndex].getAt(stacked ? j : i);
                shadowBarAttr.x = barAttr.x;
                shadowBarAttr.y = barAttr.y;
                shadowBarAttr.width = barAttr.width;
                shadowBarAttr.height = barAttr.height;
                if (!shadow) {
                    shadow = surface.add(Ext.apply({
                        type: 'rect',
                        group: shadowGroups[shadowIndex]
                    }, Ext.apply({}, baseAttrs, shadowBarAttr)));
                }
                if (stacked) {
                    totalDim = items[i].totalDim;
                    totalNegDim = items[i].totalNegDim;
                    if (column) {
                        shadowBarAttr.y = zero - totalNegDim;
                        shadowBarAttr.height = totalDim;
                    }
                    else {
                        shadowBarAttr.x = zero - totalNegDim;
                        shadowBarAttr.width = totalDim;
                    }
                }
                if (animate) {
                    if (!stacked) {
                        rendererAttributes = me.renderer(shadow, store.getAt(j), shadowBarAttr, i, store);
                        me.onAnimate(shadow, { to: rendererAttributes });
                    }
                    else {
                        rendererAttributes = me.renderer(shadow, store.getAt(j), Ext.apply(shadowBarAttr, { hidden: true }), i, store);
                        shadow.setAttributes(rendererAttributes, true);
                    }
                }
                else {
                    rendererAttributes = me.renderer(shadow, store.getAt(j), Ext.apply(shadowBarAttr, { hidden: false }), i, store);
                    shadow.setAttributes(rendererAttributes, true);
                }
                shadows.push(shadow);
            }
        }
        return shadows;
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            chart = me.chart,
            store = chart.substore || chart.store,
            surface = me.getSurface(),
            animate = chart.animate,
            stacked = me.stacked,
            column = me.column,
            enableShadows = chart.shadow,
            shadowGroups = me.shadowGroups,
            shadowGroupsLn = shadowGroups.length,
            group = me.group,
            seriesStyle = me.style,
            items, ln, i, j, baseAttrs, sprite, rendererAttributes, shadowIndex, shadowGroup,
            bounds, endSeriesStyle, barAttr, attrs, anim, item;
        
        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Bar.superclass.drawSeries.call(this);

        if (!me.getRecordCount()) {
            surface.items.hide(true);
            return;
        }

        //fill colors are taken from the colors array.
        delete seriesStyle.fill;
        me.unHighlightItem();
        me.cleanHighlights();

        me.getPaths();
        bounds = me.bounds;
        items = me.items;

        baseAttrs = column ? {
            y: bounds.zero,
            height: 0
        } : {
            x: bounds.zero,
            width: 0
        };
        ln = items.length;
        // Create new or reuse sprites and animate/display
        for (i = 0; i < ln; i++) {
            item = items[i];
            sprite = group.getAt(i);
            barAttr = item.attr;

            if (enableShadows) {
                item.shadows = me.renderShadows(i, barAttr, baseAttrs, bounds);
            }

            // Create a new sprite if needed (no height)
            if (!sprite) {
                attrs = Ext.apply({}, baseAttrs, barAttr);
                attrs = Ext.apply(attrs, endSeriesStyle || {});
                if (enableShadows) {
                    Ext.apply(attrs, me.shadowOptions);
                }
                sprite = surface.add(Ext.apply({}, {
                    type: 'rect',
                    group: group
                }, attrs));
            }
            if (animate) {
                rendererAttributes = me.renderer(sprite, store.getAt(i), barAttr, i, store);
                sprite._to = rendererAttributes;
                anim = me.onAnimate(sprite, { to: Ext.apply(rendererAttributes, endSeriesStyle) });
                if (enableShadows && stacked && (i % bounds.barsLen === 0)) {
                    j = i / bounds.barsLen;
                    for (shadowIndex = 0; shadowIndex < shadowGroupsLn; shadowIndex++) {
                        anim.on('afteranimate', function() {
                            this.show(true);
                        }, shadowGroups[shadowIndex].getAt(j));
                    }
                }
            }
            else {
                rendererAttributes = me.renderer(sprite, store.getAt(i), Ext.apply(barAttr, { hidden: false }), i, store);
                sprite.setAttributes(Ext.apply(rendererAttributes, endSeriesStyle), true);
            }
            item.sprite = sprite;
        }

        // Hide unused sprites
        ln = group.getCount();
        for (j = i; j < ln; j++) {
            group.getAt(j).hide(true);
        }
        // Hide unused shadows
        if (enableShadows) {
            for (shadowIndex = 0; shadowIndex < shadowGroupsLn; shadowIndex++) {
                shadowGroup = shadowGroups[shadowIndex];
                ln = shadowGroup.getCount();
                for (j = i; j < ln; j++) {
                    shadowGroup.getAt(j).hide(true);
                }
            }
        }
        me.renderLabels();
        me.fireEvent('draw', me);
    },
    
    // @private handled when creating a label.
    onCreateLabel: function(storeItem, item, i, display) {
        var me = this,
            surface = me.getSurface(),
            group = me.labelsGroup,
            config = me.label,
            endLabelStyle = Ext.apply({}, config, me.labelStyle.style || {}),
            sprite;
       
        return surface.add(Ext.apply({
            type: 'text',
            group: group
        }, endLabelStyle || {}));
    },
    
    // @private callback used when placing a label.
    onPlaceLabel: function(label, storeItem, item, i, display, animate, j, index) {
        // Determine the label's final position. Starts with the configured preferred value but
        // may get flipped from inside to outside or vice-versa depending on space.
        var me = this,
            opt = me.bounds,
            groupBarWidth = opt.groupBarWidth,
            column = me.column,
            chart = me.chart,
            chartBBox = chart.chartBBox,
            resizing = chart.resizing,
            xValue = item.value[0],
            yValue = item.value[1],
            attr = item.attr,
            config = Ext.apply(me.labelStyle.style || {},  me.label || {}),
            rotate = config.orientation == 'vertical',
            field = [].concat(config.field),
            format = config.renderer,
            text = format(storeItem.get(field[index])),
            size = me.getLabelSize(text),
            width = size.width,
            height = size.height,
            zero = opt.zero,
            outside = 'outside',
            insideStart = 'insideStart',
            insideEnd = 'insideEnd',
            offsetX = 10,
            offsetY = 6,
            signed = opt.signed,
            x, y, finalAttr;
        
        label.setAttributes({
            text: text
        });

        label.isOutside = false;
        if (column) {
            if (display == outside) {
                if (height + offsetY + attr.height > (yValue >= 0 ? zero: chartBBox.height - zero)) {
                    display = insideEnd;
                }
            } else {
                if (height + offsetY > attr.height) {
                    display = outside;
                    label.isOutside = true;
                }
            }
            x = attr.x + groupBarWidth / 2;
            y = display == insideStart ?
                    (zero + ((height / 2 + 3) * (yValue >= 0 ? -1 : 1))) :
                    (yValue >= 0 ? (attr.y + ((height / 2 + 3) * (display == outside ? -1 : 1))) :
                                   (attr.y + attr.height + ((height / 2 + 3) * (display === outside ? 1 : -1))));
        }
        else {
            if (display == outside) {
                if (width + offsetX + attr.width > (yValue >= 0 ? chartBBox.width - zero : zero)) {
                    display = insideEnd;
                }
            }
            else {
                if (width + offsetX > attr.width) {
                    display = outside;
                    label.isOutside = true;
                }
            }
            x = display == insideStart ?
                (zero + ((width / 2 + 5) * (yValue >= 0 ? 1 : -1))) :
                (yValue >= 0 ? (attr.x + attr.width + ((width / 2 + 5) * (display === outside ? 1 : -1))) :
                (attr.x + ((width / 2 + 5) * (display === outside ? -1 : 1))));
            y = attr.y + groupBarWidth / 2;
        }
        //set position
        finalAttr = {
            x: x,
            y: y
        };
        //rotate
        if (rotate) {
            finalAttr.rotate = {
                x: x,
                y: y,
                degrees: 270
            };
        }
        //check for resizing
        if (animate && resizing) {
            if (column) {
                x = attr.x + attr.width / 2;
                y = zero;
            } else {
                x = zero;
                y = attr.y + attr.height / 2;
            }
            label.setAttributes({
                x: x,
                y: y
            }, true);
            if (rotate) {
                label.setAttributes({
                    rotate: {
                        x: x,
                        y: y,
                        degrees: 270
                    }
                }, true);
            }
        }
        //handle animation
        if (animate) {
            me.onAnimate(label, { to: finalAttr });
        }
        else {
            label.setAttributes(Ext.apply(finalAttr, {
                hidden: false
            }), true);
        }
    },

    /* @private
     * Gets the dimensions of a given bar label. Uses a single hidden sprite to avoid
     * changing visible sprites.
     * @param value
     */
    getLabelSize: function(value) {
        var tester = this.testerLabel,
            config = this.label,
            endLabelStyle = Ext.apply({}, config, this.labelStyle.style || {}),
            rotated = config.orientation === 'vertical',
            bbox, w, h,
            undef;
        if (!tester) {
            tester = this.testerLabel = this.getSurface().add(Ext.apply({
                type: 'text',
                opacity: 0
            }, endLabelStyle));
        }
        tester.setAttributes({
            text: value
        }, true);

        // Flip the width/height if rotated, as getBBox returns the pre-rotated dimensions
        bbox = tester.getBBox();
        w = bbox.width;
        h = bbox.height;
        return {
            width: rotated ? h : w,
            height: rotated ? w : h
        };
    },

    // @private used to animate label, markers and other sprites.
    onAnimate: function(sprite, attr) {
        sprite.show();
        Ext.chart.series.Bar.superclass.onAnimate.apply(this, arguments);
    },
    
    isItemInPoint: function(x, y, item) {
        var bbox = item.sprite.getBBox();
        return bbox.x <= x && bbox.y <= y
            && (bbox.x + bbox.width) >= x
            && (bbox.y + bbox.height) >= y;
    },
    
    // @private hide all markers
    hideAll: function() {
        var axes = this.chart.axes;
        if (!isNaN(this._index)) {
            if (!this.__excludes) {
                this.__excludes = [];
            }
            this.__excludes[this._index] = true;
            this.drawSeries();
            axes.each(function(axis) {
                axis.drawAxis();
            });
        }
    },

    // @private show all markers
    showAll: function() {
        var axes = this.chart.axes;
        if (!isNaN(this._index)) {
            if (!this.__excludes) {
                this.__excludes = [];
            }
            this.__excludes[this._index] = false;
            this.drawSeries();
            axes.each(function(axis) {
                axis.drawAxis();
            });
        }
    },
    
    /**
     * Returns a string with the color to be used for the series legend item.
     * @param index
     */
    getLegendColor: function(index) {
        var me = this,
            colorArrayStyle = me.colorArrayStyle;
        return me.getColorFromStyle(colorArrayStyle[index % colorArrayStyle.length]);
    },

    highlightItem: function(item) {
        Ext.chart.series.Bar.superclass.highlightItem.apply(this, arguments);
        this.renderLabels();
    },

    unHighlightItem: function() {
        Ext.chart.series.Bar.superclass.unHighlightItem.apply(this, arguments);
        this.renderLabels();
    },

    cleanHighlights: function() {
        Ext.chart.series.Bar.superclass.cleanHighlights.apply(this, arguments);
        this.renderLabels();
    },

    reset: function() {
        var me = this;
        me.stacked = me.initialStacked;
        Ext.chart.series.Bar.superclass.reset.call(me);
    }
});

/**
 * @class Ext.chart.series.Column
 * @extends Ext.chart.series.Bar
 * 
 * Creates a Column Chart. Much of the methods are inherited from Bar. A Column Chart is a useful visualization technique to display quantitative information for different 
 * categories that can show some progression (or regression) in the data set.
 * As with all other series, the Column Series must be appended in the *series* Chart array configuration. See the Chart 
 * documentation for more information. A typical configuration object for the column series could be:
 *
 * {@img Ext.chart.series.Column/Ext.chart.series.Column.png Ext.chart.series.Column chart series  
 *
 * ## Example
 * 
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}                                                
 *         ]
 *     });
 *     
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             position: 'bottom',
 *             fields: ['data1'],
 *             label: {
 *                 renderer: Ext.util.Format.numberRenderer('0,0')
 *             },
 *             title: 'Sample Values',
 *             grid: true,
 *             minimum: 0
 *         }, {
 *             type: 'Category',
 *             position: 'left',
 *             fields: ['name'],
 *             title: 'Sample Metrics'
 *         }],
 *             axes: [{
 *                 type: 'Numeric',
 *                 position: 'left',
 *                 fields: ['data1'],
 *                 label: {
 *                     renderer: Ext.util.Format.numberRenderer('0,0')
 *                 },
 *                 title: 'Sample Values',
 *                 grid: true,
 *                 minimum: 0
 *             }, {
 *                 type: 'Category',
 *                 position: 'bottom',
 *                 fields: ['name'],
 *                 title: 'Sample Metrics'
 *             }],
 *             series: [{
 *                 type: 'column',
 *                 axis: 'left',
 *                 highlight: true,
 *                 tips: {
 *                   trackMouse: true,
 *                   width: 140,
 *                   height: 28,
 *                   renderer: function(storeItem, item) {
 *                     this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' $');
 *                   }
 *                 },
 *                 label: {
 *                   display: 'insideEnd',
 *                   'text-anchor': 'middle',
 *                     field: 'data1',
 *                     renderer: Ext.util.Format.numberRenderer('0'),
 *                     orientation: 'vertical',
 *                     color: '#333'
 *                 },
 *                 xField: 'name',
 *                 yField: 'data1'
 *             }]
 *     });
 *  
 * In this configuration we set `column` as the series type, bind the values of the bars to the bottom axis, set `highlight` to true so that bars are smoothly highlighted
 * when hovered and bind the `xField` or category field to the data store `name` property and the `yField` as the data1 property of a store element. 
 */
Ext.chart.series.Column = Ext.extend(Ext.chart.series.Bar, {

    type: 'column',

    column: true,
    
    axis: 'left',

    /**
     * @cfg {Number} xPadding
     * Padding between the left/right axes and the bars
     */
    xPadding: 10,

    /**
     * @cfg {Number} yPadding
     * Padding between the top/bottom axes and the bars
     */
    yPadding: 0
});
/**
 * @class Ext.chart.series.Gauge
 * @extends Ext.chart.series.Series
 *
 * Creates a Gauge Chart. Gauge Charts are used to show progress in a certain variable. There are two ways of using the Gauge chart.
 * One is setting a store element into the Gauge and selecting the field to be used from that store. Another one is instanciating the
 * visualization and using the `setValue` method to adjust the value you want.
 *
 * A chart/series configuration for the Gauge visualization could look like this:
 *
 *     {
 *         xtype: 'chart',
 *         store: store,
 *         axes: [{
 *             type: 'gauge',
 *             position: 'gauge',
 *             minimum: 0,
 *             maximum: 100,
 *             steps: 10,
 *             margin: -10
 *         }],
 *         series: [{
 *             type: 'gauge',
 *             angleField: 'data1',
 *             donut: false,
 *             colorSet: ['#F49D10', '#ddd']
 *         }]
 *     }
 *
 * In this configuration we create a special Gauge axis to be used with the gauge visualization (describing half-circle markers), and also we're
 * setting a maximum, minimum and steps configuration options into the axis. The Gauge series configuration contains the store field to be bound to
 * the visual display and the color set to be used with the visualization.
 *
 * @xtype gauge
 */
Ext.chart.series.Gauge = Ext.extend(Ext.chart.series.Series, {

    type: "gauge",

    rad: Math.PI / 180,

    /**
     * @cfg {String} angleField
     * The store record field name to be used for the gauge angles.
     * The values bound to this field name must be positive real numbers.
     * This parameter is required.
     */
    angleField: false,

    /**
     * @cfg {Boolean} needle
     * Use the Gauge Series as an area series or add a needle to it. Default's false.
     */
    needle: false,

    /**
     * @cfg {Boolean|Number} donut
     * Use the entire disk or just a fraction of it for the gauge. Default's false.
     */
    donut: false,

    /**
     * @cfg {Boolean} showInLegend
     * Whether to add the gauge chart elements as legend items. Default's false.
     */
    showInLegend: false,

    /**
     * @cfg {Object} style
     * An object containing styles for overriding series styles from Theming.
     */

    constructor: function(config) {
        Ext.chart.series.Gauge.superclass.constructor.apply(this, arguments);
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            shadow = chart.shadow, i, l;
        Ext.apply(me, config, {
            shadowAttributes: surface.getShadowAttributesArray(),
            shadowOptions: surface.getShadowOptions()
        });
        me.group = surface.getGroup(me.seriesId);
        if (shadow) {
            for (i = 0, l = me.shadowAttributes.length; i < l; i++) {
                me.shadowGroups.push(surface.getGroup(me.seriesId + '-shadows' + i));
            }
        }
        surface.customAttributes.segment = function(opt) {
            return me.getSegment(opt);
        };
    },

    //@private updates some onbefore render parameters.
    initialize: function() {
        var me = this;
        //Add yFields to be used in Legend.js
        me.yField = [];
        if (me.label.field) {
            me.eachRecord(function(rec) {
                me.yField.push(rec.get(me.label.field));
            });
        }
    },

    // @private returns an object with properties for a PieSlice.
    getSegment: function(opt) {
        var me = this,
            rad = me.rad,
            cos = Math.cos,
            sin = Math.sin,
            abs = Math.abs,
            x = me.centerX,
            y = me.centerY,
            x1 = 0, x2 = 0, x3 = 0, x4 = 0,
            y1 = 0, y2 = 0, y3 = 0, y4 = 0,
            delta = 1e-2,
            startAngle = opt.startAngle,
            endAngle = opt.endAngle,
            midAngle = (startAngle + endAngle) / 2 * rad,
            margin = opt.margin || 0,
            flag = abs(endAngle - startAngle) > 180,
            auxValue = abs(endAngle % 360),
            flag2 = auxValue > 90  && auxValue < 270,
            a1 = Math.min(startAngle, endAngle) * rad,
            a2 = Math.max(startAngle, endAngle) * rad,
            singleSlice = false,
            fullCircle = false;

        x += margin * cos(midAngle);
        y += margin * sin(midAngle);

        x1 = x + opt.startRho * cos(a1);
        y1 = y + opt.startRho * sin(a1);

        x2 = x + opt.endRho * cos(a1);
        y2 = y + opt.endRho * sin(a1);

        x3 = x + opt.startRho * cos(a2);
        y3 = y + opt.startRho * sin(a2);

        x4 = x + opt.endRho * cos(a2);
        y4 = y + opt.endRho * sin(a2);

        if (abs(x1 - x3) <= delta && abs(y1 - y3) <= delta) {
            singleSlice = true;
        }

        fullCircle = singleSlice && (abs(x2 - x4) <= delta && abs(y2 - y4) <= delta);
        //Solves mysterious clipping bug with IE
        if (fullCircle) {
            return {
                path: [
                ["M", x4, y4 - 1e-4],
                ["A", opt.endRho, opt.endRho, 0, +flag, +flag2, x4, y4],
                ["Z"]]
            };
        } else if (singleSlice) {
            return {
                path: [
                ["M", x1, y1],
                ["L", x2, y2],
                ["A", opt.endRho, opt.endRho, 0, +flag, 1, x4, y4],
                ["Z"]]
            };
        } else {
            return {
                path: [
                ["M", x1, y1],
                ["L", x2, y2],
                ["A", opt.endRho, opt.endRho, 0, +flag, 1, x4, y4],
                ["M", x4, y4],
                ["L", x3, y3],
                ["A", opt.startRho, opt.startRho, 0, +flag, 0, x1, y1],
                ["Z"]]
            };
        }
    },

    // @private utility function to calculate the middle point of a pie slice.
    calcMiddle: function(item) {
        var me = this,
            rad = me.rad,
            slice = item.slice,
            x = me.centerX,
            y = me.centerY,
            startAngle = slice.startAngle,
            endAngle = slice.endAngle,
            a1 = Math.min(startAngle, endAngle) * rad,
            a2 = Math.max(startAngle, endAngle) * rad,
            midAngle = -(a1 + (a2 - a1) / 2),
            xm = x + (item.endRho + item.startRho) / 2 * Math.cos(midAngle),
            ym = y - (item.endRho + item.startRho) / 2 * Math.sin(midAngle);

        item.middle = {
            x: xm,
            y: ym
        };
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            chart = me.chart,
            store = chart.substore || chart.store,
            group = me.group,
            animate = me.chart.animate,
            axis = me.chart.axes.get(0),
            minimum = axis && axis.minimum || me.minimum || 0,
            maximum = axis && axis.maximum || me.maximum || 0,
            field = me.angleField || me.field || me.xField,
            surface = me.getSurface(),
            chartBBox = chart.chartBBox,
            donut = +me.donut,
            items = [],
            seriesStyle = me.style,
            colorArrayStyle = me.colorArrayStyle,
            colorArrayLength = colorArrayStyle && colorArrayStyle.length || 0,
            cos = Math.cos,
            sin = Math.sin,
            enableShadows = !!chart.shadow,
            rendererAttributes, centerX, centerY, slice, slices, sprite, value,
            item, ln, record, i, path,
            p, spriteOptions, bbox, splitAngle, sliceA, sliceB;

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Gauge.superclass.drawSeries.call(this);

        me.setBBox();
        bbox = me.bbox;

        //override theme colors
        if (me.colorSet) {
            colorArrayStyle = me.colorSet;
            colorArrayLength = colorArrayStyle.length;
        }

        //if not store or store is empty then there's nothing to draw
        if (!me.getRecordCount()) {
            surface.items.hide(true);
            return;
        }

        centerX = me.centerX = (chartBBox.width / 2);
        centerY = me.centerY = chartBBox.height;
        me.radius = Math.min(centerX, centerY);
        me.slices = slices = [];
        me.items = items = [];

        if (!me.value) {
            record = store.getAt(0);
            me.value = record.get(field);
        }

        value = me.value;
        if (me.needle) {
            sliceA = {
                series: me,
                value: value,
                startAngle: -180,
                endAngle: 0,
                rho: me.radius
            };
            splitAngle = -180 * (1 - (value - minimum) / (maximum - minimum));
            slices.push(sliceA);
        } else {
            splitAngle = -180 * (1 - (value - minimum) / (maximum - minimum));
            sliceA = {
                series: me,
                value: value,
                startAngle: -180,
                endAngle: splitAngle,
                rho: me.radius
            };
            sliceB = {
                series: me,
                value: me.maximum - value,
                startAngle: splitAngle,
                endAngle: 0,
                rho: me.radius
            };
            slices.push(sliceA, sliceB);
        }

        //do pie slices after.
        for (i = 0, ln = slices.length; i < ln; i++) {
            slice = slices[i];
            sprite = group.getAt(i);
            //set pie slice properties
            rendererAttributes = Ext.apply({
                segment: {
                    startAngle: slice.startAngle,
                    endAngle: slice.endAngle,
                    margin: 0,
                    rho: slice.rho,
                    startRho: slice.rho * +donut / 100,
                    endRho: slice.rho
                }
            }, Ext.apply(seriesStyle, colorArrayStyle && { fill: colorArrayStyle[i % colorArrayLength] } || {}));

            item = Ext.apply({},
            rendererAttributes.segment, {
                slice: slice,
                series: me,
                storeItem: record,
                index: i
            });
            items[i] = item;
            // Create a new sprite if needed (no height)
            if (!sprite) {
                spriteOptions = Ext.apply({
                    type: "path",
                    group: group
                }, Ext.apply(seriesStyle, colorArrayStyle && { fill: colorArrayStyle[i % colorArrayLength] } || {}));

                if (enableShadows) {
                    Ext.apply(spriteOptions, me.shadowOptions);
                }

                sprite = surface.add(Ext.apply(spriteOptions, rendererAttributes));
            }
            slice.sprite = slice.sprite || [];
            item.sprite = sprite;
            slice.sprite.push(sprite);
            if (animate) {
                rendererAttributes = me.renderer(sprite, record, rendererAttributes, i, store);
                sprite._to = rendererAttributes;
                me.onAnimate(sprite, {
                    to: rendererAttributes
                });
            } else {
                rendererAttributes = me.renderer(sprite, record, Ext.apply(rendererAttributes, {
                    hidden: false
                }), i, store);
                sprite.setAttributes(rendererAttributes, true);
            }
        }

        if (me.needle) {
            splitAngle = splitAngle * Math.PI / 180;

            if (!me.needleSprite) {
                me.needleSprite = me.getSurface().add({
                    type: 'path',
                    path: ['M', centerX + (me.radius * +donut / 100) * cos(splitAngle),
                                centerY + -Math.abs((me.radius * +donut / 100) * sin(splitAngle)),
                           'L', centerX + me.radius * cos(splitAngle),
                                centerY + -Math.abs(me.radius * sin(splitAngle))],
                    'stroke-width': 4,
                    'stroke': '#222'
                });
            } else {
                if (animate) {
                    me.onAnimate(me.needleSprite, {
                        to: {
                        path: ['M', centerX + (me.radius * +donut / 100) * cos(splitAngle),
                                    centerY + -Math.abs((me.radius * +donut / 100) * sin(splitAngle)),
                               'L', centerX + me.radius * cos(splitAngle),
                                    centerY + -Math.abs(me.radius * sin(splitAngle))]
                        }
                    });
                } else {
                    me.needleSprite.setAttributes({
                        type: 'path',
                        path: ['M', centerX + (me.radius * +donut / 100) * cos(splitAngle),
                                    centerY + -Math.abs((me.radius * +donut / 100) * sin(splitAngle)),
                               'L', centerX + me.radius * cos(splitAngle),
                                    centerY + -Math.abs(me.radius * sin(splitAngle))]
                    });
                }
            }
            me.needleSprite.setAttributes({
                hidden: false
            }, true);
        }

        delete me.value;

        me.fireEvent('draw', me);
    },

    /**
     * Sets the Gauge chart to the current specified value.
    */
    setValue: function (value) {
        this.value = value;
        this.drawSeries();
    },

    // @private callback for when creating a label sprite.
    onCreateLabel: Ext.emptyFn,

    // @private callback for when placing a label sprite.
    onPlaceLabel: Ext.emptyFn,

    // @private callback for when placing a callout.
    onPlaceCallout: Ext.emptyFn,

    // @private handles sprite animation for the series.
    onAnimate: function(sprite, attr) {
        sprite.show();
        Ext.chart.series.Gauge.superclass.onAnimate.apply(this, arguments);
    },

    isItemInPoint: function() {
        return false;
    },

    // @private shows all elements in the series.
    showAll: function() {
        if (!isNaN(this._index)) {
            this.__excludes[this._index] = false;
            this.drawSeries();
        }
    },

    /**
     * Returns the color of the series (to be displayed as color for the series legend item).
     * @param index {Number} Info about the item; same format as returned by #getItemForPoint
     */
    getLegendColor: function(index) {
        var me = this,
            colorArrayStyle = me.colorArrayStyle;
        return me.getColorFromStyle(colorArrayStyle[index % colorArrayStyle.length]);
    }
});


/**
 * @class Ext.chart.series.Line
 * @extends Ext.chart.series.Cartesian
 *
 * Creates a Line Chart. A Line Chart is a useful visualization technique to display quantitative information for different
 * categories or other real values (as opposed to the bar chart), that can show some progression (or regression) in the dataset.
 * As with all other series, the Line Series must be appended in the *series* Chart array configuration. See the Chart
 * documentation for more information. A typical configuration object for the line series could be:
 *
 * {@img Ext.chart.series.Line/Ext.chart.series.Line.png Ext.chart.series.Line chart series}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *         ]
 *     });
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             position: 'bottom',
 *             fields: ['data1'],
 *             label: {
 *                 renderer: Ext.util.Format.numberRenderer('0,0')
 *             },
 *             title: 'Sample Values',
 *             grid: true,
 *             minimum: 0
 *         }, {
 *             type: 'Category',
 *             position: 'left',
 *             fields: ['name'],
 *             title: 'Sample Metrics'
 *         }],
 *         series: [{
 *             type: 'line',
 *             highlight: {
 *                 size: 7,
 *                 radius: 7
 *             },
 *             axis: 'left',
 *             xField: 'name',
 *             yField: 'data1',
 *             markerCfg: {
 *                 type: 'cross',
 *                 size: 4,
 *                 radius: 4,
 *                 'stroke-width': 0
 *             }
 *         }, {
 *             type: 'line',
 *             highlight: {
 *                 size: 7,
 *                 radius: 7
 *             },
 *             axis: 'left',
 *             fill: true,
 *             xField: 'name',
 *             yField: 'data3',
 *             markerCfg: {
 *                 type: 'circle',
 *                 size: 4,
 *                 radius: 4,
 *                 'stroke-width': 0
 *             }
 *         }]
 *     });
 *
 * In this configuration we're adding two series (or lines), one bound to the `data1`
 * property of the store and the other to `data3`. The type for both configurations is
 * `line`. The `xField` for both series is the same, the name propert of the store.
 * Both line series share the same axis, the left axis. You can set particular marker
 * configuration by adding properties onto the markerConfig object. Both series have
 * an object as highlight so that markers animate smoothly to the properties in highlight
 * when hovered. The second series has `fill=true` which means that the line will also
 * have an area below it of the same color.
 *
 * **Note:** In the series definition remember to explicitly set the axis to bind the
 * values of the line series to. This can be done by using the `axis` configuration property.
 */
Ext.chart.series.Line = Ext.extend(Ext.chart.series.Cartesian, {

    type: 'line',

    /**
     * @cfg {String} axis
     * The position of the axis to bind the values to. Possible values are 'left', 'bottom', 'top' and 'right'.
     * You must explicitly set this value to bind the values of the line series to the ones in the axis, otherwise a
     * relative scale will be used.
     */

    /**
     * @cfg {Number} selectionTolerance
     * The offset distance from the cursor position to the line series to trigger events (then used for highlighting series, etc).
     */
    selectionTolerance: 20,

    /**
     * @cfg {Boolean} showMarkers
     * Whether markers should be displayed at the data points along the line. If true,
     * then the {@link #markerConfig} config item will determine the markers' styling.
     */
    showMarkers: true,

    /**
     * @cfg {Object} markerConfig
     * The display style for the markers. Only used if {@link #showMarkers} is true.
     * The markerConfig is a configuration object containing the same set of properties defined in
     * the Sprite class. For example, if we were to set red circles as markers to the line series we could
     * pass the object:
     *
     <pre><code>
        markerConfig: {
            type: 'circle',
            radius: 4,
            'fill': '#f00'
        }
     </code></pre>

     */
    markerConfig: {},

    /**
     * @cfg {Object} style
     * An object containing styles for the visualization lines. These styles will override the theme styles.
     * Some options contained within the style object will are described next.
     */

    /**
     * @cfg {Boolean/Number} smooth
     * If set to `true` or a non-zero number, the line will be smoothed/rounded around its points; otherwise
     * straight line segments will be drawn.
     *
     * A numeric value is interpreted as a divisor of the horizontal distance between consecutive points in
     * the line; larger numbers result in sharper curves while smaller numbers result in smoother curves.
     *
     * If set to `true` then a default numeric value of 3 will be used. Defaults to `false`.
     */
    smooth: false,

    /**
     * @private Default numeric smoothing value to be used when {@link #smooth} = true.
     */
    defaultSmoothness: 3,

    /**
     * @cfg {Boolean} fill
     * If true, the area below the line will be filled using either the styles defined with sass or
     * {@link #style.eefill} and {@link #style.opacity} config properties from {@link style}. Defaults to false.
     */
    fill: false,

    /**
     * @private Size of the buffer area on either side of the viewport to provide seamless zoom/pan
     * transforms. Expressed as a multiple of the viewport length, e.g. 1 will make the buffer on
     * each side equal to the length of the visible axis viewport.
     */
    overflowBuffer: 1,

    constructor: function(config) {
        Ext.chart.series.Line.superclass.constructor.apply(this, arguments);
        var me = this,
            surface = me.getSurface(),
            shadow = me.chart.shadow,
            //get android version (if any)
            force = true,
            i, l;

        Ext.apply(me, config, {
            //get shadow as sprites (if force is true --for android > 2)
            shadowAttributes: surface.getShadowAttributesArray(force),
            //get canvas shadow options.
            shadowOptions: force && {} || Ext.apply(surface.getShadowOptions(), shadow === true ? {} : (shadow || {}))
        });

        me.group = surface.getGroup(me.seriesId);
        if (me.showMarkers) {
            me.markerGroup = surface.getGroup(me.seriesId + '-markers');
        }
        if (shadow) {
            for (i = 0, l = this.shadowAttributes.length; i < l; i++) {
                me.shadowGroups.push(surface.getGroup(me.seriesId + '-shadows' + i));
            }
        }
    },

    // @private makes an average of points when there are more data points than pixels to be rendered.
    shrink: function(xValues, yValues, size) {
        // Start at the 2nd point...
        var len = xValues.length,
            ratio = Math.floor(len / size),
            i = 1,
            xSum = 0,
            ySum = 0,
            xRes = [xValues[0]],
            yRes = [yValues[0]];

        for (; i < len; ++i) {
            xSum += xValues[i] || 0;
            ySum += yValues[i] || 0;
            if (i % ratio == 0) {
                xRes.push(xSum/ratio);
                yRes.push(ySum/ratio);
                xSum = 0;
                ySum = 0;
            }
        }
        return {
            x: xRes,
            y: yRes
        };
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            chart = me.chart,
            store = chart.substore || chart.store,
            storeCount = me.getRecordCount(),
            bufferWidth = chart.chartBBox.width * me.overflowBuffer,
            surface = me.getSurface(),
            bbox = {},
            group = me.group,
            showMarkers = me.showMarkers,
            markerGroup = me.markerGroup,
            enableShadows = chart.shadow,
            shadowGroups = me.shadowGroups,
            shadowAttributes = me.shadowAttributes,
            smooth = me.smooth,
            lnsh = shadowGroups.length,
            dummyPath = ["M"],
            path = ["M"],
            markerIndex = chart.markerIndex,
            axes = [].concat(me.axis),
            shadowBarAttr,
            xValues = [],
            yValues = [],
            onbreak = false,
            markerStyle = me.markerStyle.style,
            seriesStyle = me.style,
            colorArrayStyle = me.colorArrayStyle,
            colorArrayLength = colorArrayStyle && colorArrayStyle.length || 0,
            isNumber = Ext.isNumber,
            seriesIdx = me.seriesIdx, shadows, shadow, shindex, fromPath, fill, fillPath, rendererAttributes,
            x, y, prevX, prevY, firstX, firstY, markerCount, i, j, ln, axis, ends, marker, markerAux, item, xValue,
            yValue, coords, xScale, yScale, minX, maxX, minY, maxY, line, animation, endMarkerStyle,
            endLineStyle, type, count, bufferMinX, bufferMaxX;

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Line.superclass.drawSeries.call(this);

        //if store is empty or the series is excluded in the legend then there's nothing to draw.
        if (!storeCount || me.seriesIsHidden) {
            surface.items.hide(true);
            return;
        }

        //prepare style objects for line and markers
        endMarkerStyle = Ext.apply({}, markerStyle, me.markerConfig);
        type = endMarkerStyle.type;
        delete endMarkerStyle.type;
        endLineStyle = seriesStyle;
        //if no stroke with is specified force it to 0.5 because this is
        //about making *lines*
        if (!endLineStyle['stroke-width']) {
            endLineStyle['stroke-width'] = 0.5;
        }
        //If we're using a time axis and we need to translate the points,
        //then reuse the first markers as the last markers.
        if (markerIndex && markerGroup && markerGroup.getCount()) {
            for (i = 0; i < markerIndex; i++) {
                marker = markerGroup.getAt(i);
                markerGroup.remove(marker);
                markerGroup.add(marker);
                markerAux = markerGroup.getAt(markerGroup.getCount() - 2);
                marker.setAttributes({
                    x: 0,
                    y: 0,
                    translate: {
                        x: markerAux.attr.translation.x,
                        y: markerAux.attr.translation.y
                    }
                }, true);
            }
        }

        me.unHighlightItem();
        me.cleanHighlights();

        me.setBBox();
        bbox = me.bbox;

        me.clipRect = [bbox.x, bbox.y, bbox.width, bbox.height];

        for (i = 0, ln = axes.length; i < ln; i++) {
            axis = chart.axes.get(axes[i]);
            if (axis) {
                ends = axis.calcEnds();
                if (axis.position == 'top' || axis.position == 'bottom') {
                    minX = ends.from;
                    maxX = ends.to;
                }
                else {
                    minY = ends.from;
                    maxY = ends.to;
                }
            }
        }
        // If a field was specified without a corresponding axis, create one to get bounds
        //only do this for the axis where real values are bound (that's why we check for
        //me.axis)
        if (me.xField && !isNumber(minX)
            && (me.axis == 'bottom' || me.axis == 'top')) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.xField)
            }).calcEnds();
            minX = axis.from;
            maxX = axis.to;
        }
        if (me.yField && !isNumber(minY)
            && (me.axis == 'right' || me.axis == 'left')) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.yField)
            }).calcEnds();
            minY = axis.from;
            maxY = axis.to;
        }

        if (isNaN(minX)) {
            minX = 0;
            xScale = bbox.width / ((storeCount - 1) || 1);
        }
        else {
            xScale = bbox.width / ((maxX - minX) || (storeCount -1) || 1);
        }

        if (isNaN(minY)) {
            minY = 0;
            yScale = bbox.height / ((storeCount - 1) || 1);
        }
        else {
            yScale = bbox.height / ((maxY - minY) || 1);
        }

        // Find the min and max x values that fit within the zoom/pan buffer area
        bufferMinX = minX - (bufferWidth + me.panX) / xScale;
        bufferMaxX = bufferMinX + (bufferWidth * 2 + chart.chartBBox.width) / xScale;

        // Extract all x and y values from the store
        me.eachRecord(function(record, i) {
            xValue = record.get(me.xField);

            // Ensure a value
            if (typeof xValue == 'string' || typeof xValue == 'object'
                //set as uniform distribution if the axis is a category axis.
                || (me.axis != 'top' && me.axis != 'bottom')) {
                xValue = i;
            }

            // Filter out values that don't fit within the pan/zoom buffer area
            if (xValue >= bufferMinX && xValue <= bufferMaxX) {
                yValue = record.get(me.yField);
                //skip undefined values
                if (typeof yValue == 'undefined' || (typeof yValue == 'string' && !yValue)) {
                    return;
                }
                // Ensure a value
                if (typeof yValue == 'string' || typeof yValue == 'object'
                    //set as uniform distribution if the axis is a category axis.
                    || (me.axis != 'left' && me.axis != 'right')) {
                    yValue = i;
                }
                xValues.push(xValue);
                yValues.push(yValue);
            }
        });

        ln = xValues.length;
        if (ln > bbox.width) {
            coords = me.shrink(xValues, yValues, bbox.width);
            xValues = coords.x;
            yValues = coords.y;
        }

        me.items = [];

        count = 0;
        ln = xValues.length;
        for (i = 0; i < ln; i++) {
            xValue = xValues[i];
            yValue = yValues[i];
            if (yValue === false) {
                if (path.length == 1) {
                    path = [];
                }
                onbreak = true;
                me.items.push(false);
                continue;
            } else {
                x = (bbox.x + (xValue - minX) * xScale).toFixed(2);
                y = ((bbox.y + bbox.height) - (yValue - minY) * yScale).toFixed(2);
                if (onbreak) {
                    onbreak = false;
                    path.push('M');
                }
                path = path.concat([x, y]);
            }
            if ((typeof firstY == 'undefined') && (typeof y != 'undefined')) {
                firstY = y;
                firstX = x;
            }
            // If this is the first line, create a dummypath to animate in from.
            if (!me.line || chart.resizing) {
                dummyPath = dummyPath.concat([x, bbox.y + bbox.height / 2]);
            }

            // When resizing, reset before animating
            if (chart.animate && chart.resizing && me.line) {
                me.line.setAttributes({
                    path: dummyPath
                }, true);
                if (me.fillPath) {
                    me.fillPath.setAttributes({
                        path: dummyPath,
                        opacity: 0.2
                    }, true);
                }
                if (me.line.shadows) {
                    shadows = me.line.shadows;
                    for (j = 0, lnsh = shadows.length; j < lnsh; j++) {
                        shadow = shadows[j];
                        shadow.setAttributes({
                            path: dummyPath
                        }, true);
                    }
                }
            }
            if (showMarkers) {
                marker = markerGroup.getAt(count++);
                if (!marker) {
                    marker = Ext.chart.Shape[type](surface, Ext.apply({
                        group: [group, markerGroup],
                        x: 0, y: 0,
                        translate: {
                            x: prevX || x,
                            y: prevY || (bbox.y + bbox.height / 2)
                        },
                        value: '"' + xValue + ', ' + yValue + '"',
                        zIndex: 4000
                    }, endMarkerStyle));
                    marker._to = {
                        translate: {
                            x: x,
                            y: y
                        }
                    };
                } else {
                    marker.setAttributes({
                        value: '"' + xValue + ', ' + yValue + '"',
                        x: 0, y: 0,
                        hidden: false
                    }, true);
                    marker._to = {
                        translate: {
                            x: x, y: y
                        }
                    };
                }
            }
            me.items.push({
                series: me,
                value: [xValue, yValue],
                point: [x, y],
                sprite: marker,
                storeItem: store.getAt(i)
            });
            prevX = x;
            prevY = y;
        }

        if (path.length <= 1) {
            //nothing to be rendered
            return;
        }

        if (me.smooth) {
            path = Ext.draw.Draw.smooth(path, isNumber(smooth) ? smooth : me.defaultSmoothness);
        }

        //Correct path if we're animating timeAxis intervals
        if (chart.markerIndex && me.previousPath) {
            fromPath = me.previousPath;
            fromPath.splice(1, 2);
        } else {
            fromPath = path;
        }

        // Only create a line if one doesn't exist.
        if (!me.line) {
            me.line = surface.add(Ext.apply({
                type: 'path',
                group: group,
                path: dummyPath,
                stroke: endLineStyle.stroke || endLineStyle.fill
            }, endLineStyle || {}));

            if (enableShadows) {
                me.line.setAttributes(Ext.apply({}, me.shadowOptions), true);
            }

            //unset fill here (there's always a default fill withing the themes).
            me.line.setAttributes({
                fill: 'none',
                zIndex: 3000
            });
            if (!endLineStyle.stroke && colorArrayLength) {
                me.line.setAttributes({
                    stroke: colorArrayStyle[seriesIdx % colorArrayLength]
                }, true);
            }
            if (enableShadows) {
                //create shadows
                shadows = me.line.shadows = [];
                for (shindex = 0; shindex < lnsh; shindex++) {
                    shadowBarAttr = shadowAttributes[shindex];
                    shadowBarAttr = Ext.apply({}, shadowBarAttr, { path: dummyPath });
                    shadow = surface.add(Ext.apply({}, {
                        type: 'path',
                        group: shadowGroups[shindex]
                    }, shadowBarAttr));
                    shadows.push(shadow);
                }
            }
        }
        if (me.fill) {
            fillPath = path.concat([
                ["L", x, bbox.y + bbox.height],
                ["L", firstX, bbox.y + bbox.height],
                ["L", firstX, firstY]
            ]);
            if (!me.fillPath) {
                me.fillPath = surface.add({
                    group: group,
                    type: 'path',
                    opacity: endLineStyle.opacity || 0.3,
                    fill: endLineStyle.fill || colorArrayStyle[seriesIdx % colorArrayLength],
                    path: dummyPath
                });
            }
        }
        markerCount = showMarkers && markerGroup.getCount();
        if (chart.animate) {
            fill = me.fill;
            line = me.line;
            //Add renderer to line. There is not unique record associated with this.
            rendererAttributes = me.renderer(line, false, { path: path }, i, store);
            Ext.apply(rendererAttributes, endLineStyle || {}, {
                stroke: endLineStyle.stroke || endLineStyle.fill
            });
            //fill should not be used here but when drawing the special fill path object
            delete rendererAttributes.fill;
            line.show(true);
            if (chart.markerIndex && me.previousPath) {
                me.animation = animation = me.onAnimate(line, {
                    to: rendererAttributes,
                    from: {
                        path: fromPath
                    }
                });
            } else {
                me.animation = animation = me.onAnimate(line, {
                    to: rendererAttributes
                });
            }
            //animate shadows
            if (enableShadows) {
                shadows = line.shadows;
                for(j = 0; j < lnsh; j++) {
                    shadows[j].show(true);
                    if (chart.markerIndex && me.previousPath) {
                        me.onAnimate(shadows[j], {
                            to: { path: path },
                            from: { path: fromPath }
                        });
                    } else {
                        me.onAnimate(shadows[j], {
                            to: { path: path }
                        });
                    }
                }
            }
            //animate fill path
            if (fill) {
                me.fillPath.show(true);
                me.onAnimate(me.fillPath, {
                    to: Ext.apply({}, {
                        path: fillPath,
                        fill: endLineStyle.fill || colorArrayStyle[seriesIdx % colorArrayLength],
                        'stroke-width': 0
                    }, endLineStyle || {})
                });
            }
            //animate markers
            if (showMarkers) {
                count = 0;
                for(i = 0; i < ln; i++) {
                    if (me.items[i]) {
                        item = markerGroup.getAt(count++);
                        if (item) {
                            rendererAttributes = me.renderer(item, store.getAt(i), item._to, i, store);
                            me.onAnimate(item, {
                                to: Ext.apply(rendererAttributes, endMarkerStyle || {})
                            });
                            item.show(true);
                        }
                    }
                }
                for(; count < markerCount; count++) {
                    item = markerGroup.getAt(count);
                    item.hide(true);
                }
//                for(i = 0; i < (chart.markerIndex || 0)-1; i++) {
//                    item = markerGroup.getAt(i);
//                    item.hide(true);
//                }
            }
        } else {
            rendererAttributes = me.renderer(me.line, false, { path: path, hidden: false }, i, store);
            Ext.apply(rendererAttributes, endLineStyle || {}, {
                stroke: endLineStyle.stroke || endLineStyle.fill
            });
            //fill should not be used here but when drawing the special fill path object
            delete rendererAttributes.fill;
            me.line.setAttributes(rendererAttributes, true);
            //set path for shadows
            if (enableShadows) {
                shadows = me.line.shadows;
                for(j = 0; j < lnsh; j++) {
                    shadows[j].setAttributes({
                        path: path,
                        hidden: false
                    }, true);
                }
            }
            if (me.fill) {
                me.fillPath.setAttributes({
                    path: fillPath,
                    hidden: false
                }, true);
            }
            if (showMarkers) {
                count = 0;
                for(i = 0; i < ln; i++) {
                    if (me.items[i]) {
                        item = markerGroup.getAt(count++);
                        if (item) {
                            rendererAttributes = me.renderer(item, store.getAt(i), item._to, i, store);
                            item.setAttributes(Ext.apply(endMarkerStyle || {}, rendererAttributes || {}), true);
                            item.show(true);
                        }
                    }
                }
                for(; count < markerCount; count++) {
                    item = markerGroup.getAt(count);
                    item.hide(true);
                }
            }
        }

        if (chart.markerIndex) {
            path.splice(1, 0, path[1], path[2]);
            me.previousPath = path;
        }
        me.renderLabels();
        me.renderCallouts();

        me.fireEvent('draw', me);
    },

    // @private called when a label is to be created.
    onCreateLabel: function(storeItem, item, i, display) {
        var me = this,
            group = me.labelsGroup,
            config = me.label,
            bbox = me.bbox,
            endLabelStyle = Ext.apply(config, me.labelStyle.style);

        return me.getSurface().add(Ext.apply({
            'type': 'text',
            'text-anchor': 'middle',
            'group': group,
            'x': item.point[0],
            'y': bbox.y + bbox.height / 2,
            zIndex: 200
        }, endLabelStyle || {}));
    },

    // @private called when a label is to be created.
    onPlaceLabel: function(label, storeItem, item, i, display, animate) {
        var me = this,
            chart = me.chart,
            resizing = chart.resizing,
            config = me.label,
            format = config.renderer,
            field = config.field,
            bbox = me.bbox,
            x = item.point[0],
            y = item.point[1],
            radius = item.sprite.attr.radius,
            bb, width, height;

        label.setAttributes({
            text: format(storeItem.get(field)),
            hidden: true
        }, true);

        if (display == 'rotate') {
            label.setAttributes({
                'text-anchor': 'start',
                'rotation': {
                    x: x,
                    y: y,
                    degrees: -45
                }
            }, true);
            //correct label position to fit into the box
            bb = label.getBBox();
            width = bb.width;
            height = bb.height;
            x = x < bbox.x? bbox.x : x;
            x = (x + width > bbox.x + bbox.width)? (x - (x + width - bbox.x - bbox.width)) : x;
            y = (y - height < bbox.y)? bbox.y + height : y;

        } else if (display == 'under' || display == 'over') {
            //TODO(nicolas): find out why width/height values in circle bounding boxes are undefined.
            bb = item.sprite.getBBox();
            bb.width = bb.width || (radius * 2);
            bb.height = bb.height || (radius * 2);
            y = y + (display == 'over'? -bb.height : bb.height);
            //correct label position to fit into the box
            bb = label.getBBox();
            width = bb.width/2;
            height = bb.height/2;
            x = x - width < bbox.x? bbox.x + width : x;
            x = (x + width > bbox.x + bbox.width) ? (x - (x + width - bbox.x - bbox.width)) : x;
            y = y - height < bbox.y? bbox.y + height : y;
            y = (y + height > bbox.y + bbox.height) ? (y - (y + height - bbox.y - bbox.height)) : y;
        }

        if (me.chart.animate && !me.chart.resizing) {
            label.show(true);
            me.onAnimate(label, {
                to: {
                    x: x,
                    y: y
                }
            });
        } else {
            label.setAttributes({
                x: x,
                y: y
            }, true);
            if (resizing) {
                me.animation.on('afteranimate', function() {
                    label.show(true);
                });
            } else {
                label.show(true);
            }
        }
    },

    //@private Overriding highlights.js highlightItem method.
    highlightItem: function(item) {
        var me = this,
            line = me.line,
            marker, markerStyle, markerType;

        Ext.chart.series.Line.superclass.highlightItem.call(me, item);

        if (line && !me.highlighted) {
            if (!('__strokeWidth' in line)) {
                line.__strokeWidth = line.attr['stroke-width'] || 0;
            }
            if (line.__anim) {
                line.__anim.paused = true;
            }
            line.__anim = new Ext.fx.Anim({
                target: line,
                to: {
                    'stroke-width': line.__strokeWidth + 3
                }
            });
            me.highlighted = true;
        }

        // If no markers are configured, we still want to display one at the highlighted point
        // so the user can see what was highlighted.
        if (!me.showMarkers) {
            marker = me.highlightMarker;
            if (!marker) {
                markerStyle = Ext.apply({}, me.markerStyle.style, me.markerConfig);
                markerType = markerStyle.type;
                delete markerStyle.type;
                marker = me.highlightMarker = Ext.chart.Shape[markerType](me.getSurface(), Ext.apply({x: 0, y: 0}, markerStyle));
            }
            marker.setAttributes({
                translate: {
                    x: item.point[0],
                    y: item.point[1]
                },
                hidden: false
            }, true);
        }
    },

    //@private Overriding highlights.js unHighlightItem method.
    unHighlightItem: function() {
        var me = this,
            line = me.line,
            marker = me.highlightMarker;

        Ext.chart.series.Line.superclass.unHighlightItem.call(me);

        if (line && me.highlighted) {
            line.__anim = new Ext.fx.Anim({
                target: line,
                to: {
                    'stroke-width': line.__strokeWidth
                }
            });
            me.highlighted = false;
        }

        if (marker) {
            marker.hide(true);
        }
    },

    //@private called when a callout needs to be placed.
    onPlaceCallout : function(callout, storeItem, item, i, display, animate, index) {
        if (!display) {
            return;
        }

        var me = this,
            chart = me.chart,
            config = me.callouts,
            items = me.items,
            prev = i == 0? false : items[i -1].point,
            next = (i == items.length -1)? false : items[i +1].point,
            cur = [+item.point[0], +item.point[1]],
            dir, norm, normal, a, aprev, anext,
            offsetFromViz = config.offsetFromViz || 30,
            offsetBox = config.offsetBox || 3,
            boxx, boxy, boxw, boxh,
            p, clipRect = me.clipRect,
            bbox = {
                width: config.styles.width || 10,
                height: config.styles.height || 10
            },
            x, y;

        //get the right two points
        if (!prev) {
            prev = cur;
        }
        if (!next) {
            next = cur;
        }
        a = (next[1] - prev[1]) / (next[0] - prev[0]);
        aprev = (cur[1] - prev[1]) / (cur[0] - prev[0]);
        anext = (next[1] - cur[1]) / (next[0] - cur[0]);

        norm = Math.sqrt(1 + a * a);
        dir = [1 / norm, a / norm];
        normal = [-dir[1], dir[0]];

        //keep the label always on the outer part of the "elbow"
        if (aprev > 0 && anext < 0 && normal[1] < 0
            || aprev < 0 && anext > 0 && normal[1] > 0) {
            normal[0] *= -1;
            normal[1] *= -1;
        } else if (Math.abs(aprev) < Math.abs(anext) && normal[0] < 0
                   || Math.abs(aprev) > Math.abs(anext) && normal[0] > 0) {
            normal[0] *= -1;
            normal[1] *= -1;
        }
        //position
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;

        //box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;

        //now check if we're out of bounds and invert the normal vector correspondingly
        //this may add new overlaps between labels (but labels won't be out of bounds).
        if (boxx < clipRect[0] || (boxx + boxw) > (clipRect[0] + clipRect[2])) {
            normal[0] *= -1;
        }
        if (boxy < clipRect[1] || (boxy + boxh) > (clipRect[1] + clipRect[3])) {
            normal[1] *= -1;
        }

        //update positions
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;

        //update box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;

        if (chart.animate) {
            //set the line from the middle of the pie to the box.
            me.onAnimate(callout.lines, {
                to: {
                    path: ["M", cur[0], cur[1], "L", x, y, "Z"]
                }
            });
            //set component position
            if (callout.panel) {
                callout.panel.setPosition(boxx, boxy, true);
            }
        }
        else {
            //set the line from the middle of the pie to the box.
            callout.lines.setAttributes({
                path: ["M", cur[0], cur[1], "L", x, y, "Z"]
            }, true);
            //set component position
            if (callout.panel) {
                callout.panel.setPosition(boxx, boxy);
            }
        }
        for (p in callout) {
            callout[p].show(true);
        }
    },

    isItemInPoint: function(x, y, item, i) {
        var me = this,
            items = me.items,
            tolerance = me.selectionTolerance,
            point, diffX, diffY, dist,
            sqrt = Math.sqrt;

        // See if the target item is within the selectionTolerance distance from the x/y point
        point = item.point;
        diffX = x - point[0];
        diffY = y - point[1];
        dist = sqrt(diffX * diffX + diffY * diffY);

        if (dist <= tolerance) {
            // We have a match, but it's possible the previous or next item are even closer, so check them
            if (i > 0 && items[i - 1]) {
                point = items[i - 1].point;
                diffX = x - point[0];
                diffY = y - point[1];
                if (sqrt(diffX * diffX + diffY * diffY) < dist) {
                    return false;
                }
            }
            if (items[i + 1]) {
                point = items[i + 1].point;
                diffX = x - point[0];
                diffY = y - point[1];
                if (sqrt(diffX * diffX + diffY * diffY) < dist) {
                    return false;
                }
            }

            return true;
        }
        return false;
    },

    // @private toggle visibility of all series elements (markers, sprites).
    toggleAll: function(show) {
        var me = this,
            i, ln, shadow, shadows;
        if (!show) {
            Ext.chart.series.Line.superclass.hideAll.call(me);
        }
        else {
            Ext.chart.series.Line.superclass.showAll.call(me);
        }
        if (me.line) {
            me.line.setAttributes({
                hidden: !show
            }, true);
            //hide shadows too
            if (me.line.shadows) {
                for (i = 0, shadows = me.line.shadows, ln = shadows.length; i < ln; i++) {
                    shadow = shadows[i];
                    shadow.setAttributes({
                        hidden: !show
                    }, true);
                }
            }
        }
        if (me.fillPath) {
            me.fillPath.setAttributes({
                hidden: !show
            }, true);
        }
    },

    // @private hide all series elements (markers, sprites).
    hideAll: function() {
        this.toggleAll(false);
    },

    // @private hide all series elements (markers, sprites).
    showAll: function() {
        this.toggleAll(true);
    }
});

/**
 * @class Ext.chart.series.Pie
 * @extends Ext.chart.series.Series
 *
 * Creates a Pie Chart. A Pie Chart is a useful visualization technique to display quantitative information for different
 * categories that also have a meaning as a whole.
 * As with all other series, the Pie Series must be appended in the *series* Chart array configuration. See the Chart
 * documentation for more information. A typical configuration object for the pie series could be:
 *
 * {@img Ext.chart.series.Pie/Ext.chart.series.Pie.png Ext.chart.series.Pie chart series}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *         ]
 *     });
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         store: store,
 *         theme: 'Base:gradients',
 *         series: [{
 *             type: 'pie',
 *             angleField: 'data1',
 *             showInLegend: true,
 *             tips: {
 *               trackMouse: true,
 *               width: 140,
 *               height: 28,
 *               renderer: function(storeItem, item) {
 *                 //calculate and display percentage on hover
 *                 var total = 0;
 *                 store.each(function(rec) {
 *                     total += rec.get('data1');
 *                 });
 *                 this.setTitle(storeItem.get('name') + ': ' + Math.round(storeItem.get('data1') / total * 100) + '%');
 *               }
 *             },
 *             highlight: {
 *               segment: {
 *                 margin: 20
 *               }
 *             },
 *             label: {
 *                 field: 'name',
 *                 display: 'rotate',
 *                 contrast: true,
 *                 font: '18px Arial'
 *             }
 *         }]
 *     });
 *
 * In this configuration we set `pie` as the type for the series, set an object with specific style properties for highlighting options
 * (triggered when hovering elements). We also set true to `showInLegend` so all the pie slices can be represented by a legend item.
 * We set `data1` as the value of the field to determine the angle span for each pie slice. We also set a label configuration object
 * where we set the field name of the store field to be renderer as text for the label. The labels will also be displayed rotated.
 * We set `contrast` to `true` to flip the color of the label if it is to similar to the background color. Finally, we set the font family
 * and size through the `font` parameter.
 *
 * @xtype pie
 */
Ext.chart.series.Pie = Ext.extend(Ext.chart.series.Series, {

    type: 'pie',

    rad: Math.PI / 180,

    /**
     * @cfg {Number} rotation
     * The angle in degrees at which the first pie slice should start. Defaults to `0`.
     */
    rotation: 0,

    /**
     * @cfg {String} angleField
     * The store record field name to be used for the pie angles.
     * The values bound to this field name must be positive real numbers.
     * This parameter is required.
     */
    angleField: false,

    /**
     * @cfg {String} lengthField
     * The store record field name to be used for the pie slice lengths.
     * The values bound to this field name must be positive real numbers.
     * This parameter is optional.
     */
    lengthField: false,

    /**
     * @cfg {Boolean|Number} donut
     * Whether to set the pie chart as donut chart.
     * Default's false. Can be set to a particular percentage to set the radius
     * of the donut chart.
     */
    donut: false,

    /**
     * @cfg {Boolean} showInLegend
     * Whether to add the pie chart elements as legend items. Default's false.
     */
    showInLegend: false,

    /**
     * @cfg {Boolean} labelOverflowPadding
     * Extra distance value for which the labelOverflow listener is triggered. Default to 20.
     */
    labelOverflowPadding: 20,

    /**
     * @cfg {Array} colorSet
     * An array of color values which will be used, in order, as the pie slice fill colors.
     */

    /**
     * @cfg {Object} style
     * An object containing styles for overriding series styles from Theming.
     */

    constructor: function(config) {
        Ext.applyIf(config, {
            highlightCfg: {
                segment: {
                    margin: 20
                }
            }
        });

        Ext.chart.series.Pie.superclass.constructor.apply(this, arguments);
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            shadow = chart.shadow, i, l;
        Ext.apply(me, config, {
            shadowAttributes: surface.getShadowAttributesArray(),
            shadowOptions: Ext.apply(surface.getShadowOptions(), shadow === true ? {} : (shadow || {}))
        });
        me.group = surface.getGroup(me.seriesId);
        if (shadow) {
            for (i = 0, l = me.shadowAttributes.length; i < l; i++) {
                me.shadowGroups.push(surface.getGroup(me.seriesId + '-shadows' + i));
            }
        }
        surface.customAttributes.segment = function(opt) {
            return me.getSegment(opt);
        };
        me.__excludes = me.__excludes || [];

        //add labelOverflows as managed events.
        me.addEvents('labelOverflow');

        //add default label overflow listener which hides the label.
        me.addListener('labelOverflow', me.onLabelOverflow);
    },

    //default configuration for label overflowing the pie slice shape.
    onLabelOverflow: function(label) {
        label.hide(true);
    },

    // @private returns an object with properties for a PieSlice.
    getSegment: function(opt) {
        var me = this,
            rad = me.rad,
            cos = Math.cos,
            sin = Math.sin,
            abs = Math.abs,
            x = me.centerX,
            y = me.centerY,
            x1 = 0, x2 = 0, x3 = 0, x4 = 0,
            y1 = 0, y2 = 0, y3 = 0, y4 = 0,
            delta = 1e-2,
            startAngle = opt.startAngle,
            endAngle = opt.endAngle,
            midAngle = (startAngle + endAngle) / 2 * rad,
            margin = opt.margin || 0,
            flag = abs(endAngle - startAngle) > 180,
            a1 = Math.min(startAngle, endAngle) * rad,
            a2 = Math.max(startAngle, endAngle) * rad,
            singleSlice = false,
            fullCircle = false;

        x += margin * cos(midAngle);
        y += margin * sin(midAngle);

        x1 = x + opt.startRho * cos(a1);
        y1 = y + opt.startRho * sin(a1);

        x2 = x + opt.endRho * cos(a1);
        y2 = y + opt.endRho * sin(a1);

        x3 = x + opt.startRho * cos(a2);
        y3 = y + opt.startRho * sin(a2);

        x4 = x + opt.endRho * cos(a2);
        y4 = y + opt.endRho * sin(a2);

        if (abs(x1 - x3) <= delta && abs(y1 - y3) <= delta) {
            singleSlice = true;
        }

        fullCircle = singleSlice && (abs(x2 - x4) <= delta && abs(y2 - y4) <= delta);

        if (startAngle === endAngle) {
            return {path: ''}
        }
        //Solves mysterious clipping bug with IE
        else if (fullCircle) {
            return {
                path: [
                ["M", x + opt.endRho, y - 1e-4],
                ["A", opt.endRho, opt.endRho, 0, 1, 0, x + opt.endRho, y],
                ["Z"]]
            };
        } else if (singleSlice) {
            return {
                path: [
                ["M", x1, y1],
                ["L", x2, y2],
                ["A", opt.endRho, opt.endRho, 0, +flag, 1, x4, y4],
                ["Z"]]
            };
        } else {
            return {
                path: [
                ["M", x1, y1],
                ["L", x2, y2],
                ["A", opt.endRho, opt.endRho, 0, +flag, 1, x4, y4],
                ["L", x4, y4],
                ["L", x3, y3],
                ["A", opt.startRho, opt.startRho, 0, +flag, 0, x1, y1],
                ["Z"]]
            };
        }
    },

    // @private utility function to calculate the middle point of a pie slice.
    calcMiddle: function(item) {
        var me = this,
            rad = me.rad,
            slice = item.slice,
            x = me.centerX,
            y = me.centerY,
            startAngle = slice.startAngle,
            endAngle = slice.endAngle,
            a1 = Math.min(startAngle, endAngle) * rad,
            a2 = Math.max(startAngle, endAngle) * rad,
            midAngle = -(a1 + (a2 - a1) / 2),
            xm = x + (item.endRho + item.startRho) / 2 * Math.cos(midAngle),
            ym = y - (item.endRho + item.startRho) / 2 * Math.sin(midAngle);

        item.middle = {
            x: xm,
            y: ym
        };
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            store = me.chart.substore || me.chart.store,
            group = me.group,
            animate = me.chart.animate,
            field = me.angleField || me.field || me.xField,
            lenField = [].concat(me.lengthField),
            totalLenField = 0,
            chart = me.chart,
            surface = me.getSurface(),
            chartBBox = chart.chartBBox,
            enableShadows = chart.shadow,
            shadowGroups = me.shadowGroups,
            shadowAttributes = me.shadowAttributes,
            lnsh = shadowGroups.length,
            layers = lenField.length,
            rhoAcum = 0,
            donut = +me.donut,
            layerTotals = [],
            items = [],
            totalField = 0,
            maxLenField = 0,
            angle = me.rotation,
            seriesStyle = me.style,
            colorArrayStyle = me.colorArrayStyle,
            colorArrayLength = colorArrayStyle && colorArrayStyle.length || 0,
            rendererAttributes,
            shadowAttr,
            shadows,
            shadow,
            shindex,
            centerX,
            centerY,
            deltaRho,
            first = 0,
            slice,
            slices,
            sprite,
            value,
            item,
            lenValue,
            ln,
            i,
            j,
            endAngle,
            middleAngle,
            path,
            p,
            spriteOptions, bbox;

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Pie.superclass.drawSeries.call(this);

        me.setBBox();
        bbox = me.bbox;

        //override theme colors
        if (me.colorSet) {
            colorArrayStyle = me.colorSet;
            colorArrayLength = colorArrayStyle.length;
        }

        me.unHighlightItem();
        me.cleanHighlights();

        centerX = me.centerX = chartBBox.x + (chartBBox.width / 2);
        centerY = me.centerY = chartBBox.y + (chartBBox.height / 2);
        me.radius = Math.min(centerX - chartBBox.x, centerY - chartBBox.y);
        me.slices = slices = [];
        me.items = items = [];

        me.eachRecord(function(record, i) {
            if (me.isExcluded(i)) {
                //hidden series
                return;
            }
            totalField += +record.get(field);
            if (lenField[0]) {
                for (j = 0, totalLenField = 0; j < layers; j++) {
                    totalLenField += +record.get(lenField[j]);
                }
                layerTotals[i] = totalLenField;
                maxLenField = Math.max(maxLenField, totalLenField);
            }
        }, this);

        totalField = totalField || 1;
        me.eachRecord(function(record, i) {
            if (me.isExcluded(i)) {
                //hidden series
                return;
            }
            value = record.get(field);
            middleAngle = angle - 360 * value / totalField / 2;
            // TODO - Put up an empty circle
            if (isNaN(middleAngle)) {
                middleAngle = 360;
                value = 1;
                totalField = 1;
            }
            // First slice
            if (!i || first === 0) {
                angle = 360 - middleAngle;
                me.firstAngle = angle;
                middleAngle = angle - 360 * value / totalField / 2;
            }
            endAngle = angle - 360 * value / totalField;
            slice = {
                series: me,
                value: value,
                startAngle: angle,
                endAngle: endAngle,
                storeItem: record
            };
            if (lenField[0]) {
                lenValue = layerTotals[i];
                slice.rho = me.radius * (lenValue / maxLenField);
            } else {
                slice.rho = me.radius;
            }
            slices[i] = slice;
            angle = endAngle;
            first++;
        }, me);

        //do all shadows first.
        if (enableShadows) {
            for (i = 0, ln = slices.length; i < ln; i++) {
                if (me.isExcluded(i)) {
                    //hidden series
                    continue;
                }
                slice = slices[i];
                slice.shadowAttrs = [];
                for (j = 0, rhoAcum = 0, shadows = []; j < layers; j++) {
                    sprite = group.getAt(i * layers + j);
                    deltaRho = lenField[j] ? store.getAt(i).get(lenField[j]) / layerTotals[i] * slice.rho: slice.rho;
                    //set pie slice properties
                    rendererAttributes = {
                        segment: {
                            startAngle: slice.startAngle,
                            endAngle: slice.endAngle,
                            margin: 0,
                            rho: slice.rho,
                            startRho: rhoAcum + (deltaRho * donut / 100),
                            endRho: rhoAcum + deltaRho
                        },
                        hidden: !slice.value && (slice.startAngle % 360) == (slice.endAngle % 360)
                    };
                    //create shadows
                    for (shindex = 0, shadows = []; shindex < lnsh; shindex++) {
                        shadowAttr = shadowAttributes[shindex];
                        shadow = shadowGroups[shindex].getAt(i);
                        if (!shadow) {
                            shadow = me.getSurface().add(Ext.apply({}, {
                                type: 'path',
                                group: shadowGroups[shindex],
                                strokeLinejoin: "round"
                            }, rendererAttributes, shadowAttr));
                        }
                        if (animate) {
                            shadowAttr = me.renderer(shadow, store.getAt(i), Ext.apply({}, rendererAttributes, shadowAttr), i, store);
                            me.onAnimate(shadow, {
                                to: shadowAttr
                            });
                        } else {
                            shadowAttr = me.renderer(shadow, store.getAt(i), Ext.apply(shadowAttr, {
                                hidden: false
                            }), i, store);
                            shadow.setAttributes(shadowAttr, true);
                        }
                        shadows.push(shadow);
                    }
                    slice.shadowAttrs[j] = shadows;
                }
            }
        }
        //do pie slices after.
        for (i = 0, ln = slices.length; i < ln; i++) {
            if (me.isExcluded(i)) {
                //hidden series
                continue;
            }
            slice = slices[i];
            for (j = 0, rhoAcum = 0; j < layers; j++) {
                sprite = group.getAt(i * layers + j);
                deltaRho = lenField[j] ? store.getAt(i).get(lenField[j]) / layerTotals[i] * slice.rho: slice.rho;
                //set pie slice properties
                rendererAttributes = Ext.apply({
                    segment: {
                        startAngle: slice.startAngle,
                        endAngle: slice.endAngle,
                        margin: 0,
                        rho: slice.rho,
                        startRho: rhoAcum + (deltaRho * donut / 100),
                        endRho: rhoAcum + deltaRho
                    },
                    hidden: !slice.value && (slice.startAngle % 360) == (slice.endAngle % 360)
                }, Ext.apply(seriesStyle, colorArrayStyle && { fill: colorArrayStyle[(layers > 1? j : i) % colorArrayLength] } || {}));
                item = Ext.apply({},
                rendererAttributes.segment, {
                    slice: slice,
                    series: me,
                    storeItem: slice.storeItem,
                    index: i
                });
                me.calcMiddle(item);
                if (enableShadows) {
                    item.shadows = slice.shadowAttrs[j];
                }
                items[i] = item;
                // Create a new sprite if needed (no height)
                if (!sprite) {
                    spriteOptions = Ext.apply({
                        type: "path",
                        group: group,
                        middle: item.middle
                    }, Ext.apply(seriesStyle, colorArrayStyle && { fill: colorArrayStyle[(layers > 1? j : i) % colorArrayLength] } || {}));

                    if (enableShadows) {
                        Ext.apply(spriteOptions, me.shadowOptions);
                    }

                    sprite = surface.add(Ext.apply(spriteOptions, rendererAttributes));
                }
                slice.sprite = slice.sprite || [];
                item.sprite = sprite;
                slice.sprite.push(sprite);
                slice.point = [item.middle.x, item.middle.y];
                rendererAttributes = me.renderer(sprite, store.getAt(i), rendererAttributes, i, store);
                if (animate) {
                    sprite._to = rendererAttributes;
                    me.onAnimate(sprite, {
                        to: rendererAttributes
                    });
                } else {
                    sprite.setAttributes(rendererAttributes, true);
                }
                rhoAcum += deltaRho;
            }
        }

        // Hide unused bars
        ln = group.getCount();
        for (i = 0; i < ln; i++) {
            if (!slices[(i / layers) >> 0] && group.getAt(i)) {
                group.getAt(i).hide(true);
            }
        }
        if (enableShadows) {
            lnsh = shadowGroups.length;
            for (shindex = 0; shindex < ln; shindex++) {
                if (!slices[(shindex / layers) >> 0]) {
                    for (j = 0; j < lnsh; j++) {
                        if (shadowGroups[j].getAt(shindex)) {
                            shadowGroups[j].getAt(shindex).hide(true);
                        }
                    }
                }
            }
        }
        me.renderLabels();
        me.renderCallouts();

        me.fireEvent('draw', me);
    },

    // @private callback for when creating a label sprite.
    onCreateLabel: function(storeItem, item) {
        var me = this,
            group = me.labelsGroup,
            config = me.label,
            middle = item.middle,
            endLabelStyle = Ext.apply(me.labelStyle.style || {}, config || {});

        return me.getSurface().add(Ext.apply({
            'type': 'text',
            'text-anchor': 'middle',
            'group': group,
            'x': middle.x,
            'y': middle.y
        }, endLabelStyle));
    },

    // @private callback for when placing a label sprite.
    onPlaceLabel: function(label, storeItem, item, i, display, animate, index) {
        var me = this,
            chart = me.chart,
            resizing = chart.resizing,
            config = me.label,
            format = config.renderer,
            field = [].concat(config.field),
            centerX = me.centerX,
            centerY = me.centerY,
            middle = item.middle,
            opt = {
                x: middle.x,
                y: middle.y
            },
            x = middle.x - centerX,
            y = middle.y - centerY,
            from = {},
            rho = 1,
            theta = Math.atan2(y, x || 1),
            dg = theta * 180 / Math.PI,
            prevDg,
            sliceContainsLabel;

        function fixAngle(a) {
            if (a < 0) a += 360;
            return a % 360;
        }

        label.setAttributes({
            text: format(storeItem.get(field[index]))
        }, true);

        switch (display) {
        case 'outside':
            rho = Math.sqrt(x * x + y * y) * 2;
            //update positions
            opt.x = rho * Math.cos(theta) + centerX;
            opt.y = rho * Math.sin(theta) + centerY;
            break;

        case 'rotate':
            dg = fixAngle(dg);
            dg = (dg > 90 && dg < 270) ? dg + 180: dg;

            prevDg = label.attr.rotation.degrees;
            if (prevDg != null && Math.abs(prevDg - dg) > 180) {
                if (dg > prevDg) {
                    dg -= 360;
                } else {
                    dg += 360;
                }
                dg = dg % 360;
            } else {
                dg = fixAngle(dg);
            }
            //update rotation angle
            opt.rotate = {
                degrees: dg,
                x: opt.x,
                y: opt.y
            };
            break;

        default:
            break;
        }
        //ensure the object has zero translation
        opt.translate = {
            x: 0, y: 0
        };
        if (animate && !resizing && (display != 'rotate' || prevDg !== null)) {
            me.onAnimate(label, {
                to: opt
            });
        } else {
            label.setAttributes(opt, true);
        }
        label._from = from;


        sliceContainsLabel = me.sliceContainsLabel(item.slice, label);

        if (!sliceContainsLabel) {
            me.fireEvent('labelOverflow', label, item);
        }
    },

    onCreateCallout: function() {
        var me = this, ans;

        ans = Ext.chart.series.Pie.superclass.onCreateCallout.apply(this, arguments);

        ans.lines.setAttributes({
            path: ['M', me.centerX, me.centerY]
        });

        ans.box.setAttributes({
            x: me.centerX,
            y: me.centerY
        });

        ans.label.setAttributes({
            x: me.centerX,
            y: me.centerY
        });

        return ans;
    },

    // @private callback for when placing a callout sprite.
    onPlaceCallout: function(callout, storeItem, item) {
        var me = this,
            chart = me.chart,
            centerX = me.centerX,
            centerY = me.centerY,
            middle = item.middle,
            opt = {
                x: middle.x,
                y: middle.y
            },
            x = middle.x - centerX,
            y = middle.y - centerY,
            rho = 1,
            rhoCenter,
            theta = Math.atan2(y, x || 1),
            label = callout.label,
            box = callout.box,
            lines = callout.lines,
            lattr = lines.attr,
            bbox = label.getBBox(),
            offsetFromViz = lattr.offsetFromViz || 20,
            offsetToSide = lattr.offsetToSide || 10,
            offsetBox = box.attr.offsetBox || 10,
            p;
        //should be able to config this.
        rho = item.endRho + offsetFromViz;
        rhoCenter = (item.endRho + item.startRho) / 2 + (item.endRho - item.startRho) / 3;
        //update positions
        opt.x = rho * Math.cos(theta) + centerX;
        opt.y = rho * Math.sin(theta) + centerY;

        x = rhoCenter * Math.cos(theta);
        y = rhoCenter * Math.sin(theta);

        if (chart.animate) {
            //set the line from the middle of the pie to the box.
            me.onAnimate(callout.lines, {
              to: {
                    path: ["M", x + centerX, y + centerY, "L", opt.x, opt.y, "Z", "M", opt.x, opt.y, "l", x > 0 ? offsetToSide: -offsetToSide, 0, "z"]
                }
            });
            //set box position
            me.onAnimate(callout.box, {
                to: {
                    x: opt.x + (x > 0 ? offsetToSide: -(offsetToSide + bbox.width + 2 * offsetBox)),
                    y: opt.y + (y > 0 ? ( - bbox.height - offsetBox / 2) : ( - bbox.height - offsetBox / 2)),
                    width: bbox.width + 2 * offsetBox,
                    height: bbox.height + 2 * offsetBox
                }
            });
            //set text position
            me.onAnimate(callout.label, {
                to: {
                    x: opt.x + (x > 0 ? (offsetToSide + offsetBox) : -(offsetToSide + bbox.width + offsetBox)),
                    y: opt.y + (y > 0 ? -bbox.height / 4: -bbox.height / 4)
                }
            });
        } else {
            //set the line from the middle of the pie to the box.
            callout.lines.setAttributes({
                path: ["M", x + centerX, y + centerY, "L", opt.x, opt.y, "Z", "M", opt.x, opt.y, "l", x > 0 ? offsetToSide: -offsetToSide, 0, "z"]
            },
            true);
            //set box position
            callout.box.setAttributes({
                x: opt.x + (x > 0 ? offsetToSide: -(offsetToSide + bbox.width + 2 * offsetBox)),
                y: opt.y + (y > 0 ? ( - bbox.height - offsetBox / 2) : ( - bbox.height - offsetBox / 2)),
                width: bbox.width + 2 * offsetBox,
                height: bbox.height + 2 * offsetBox
            },
            true);
            //set text position
            callout.label.setAttributes({
                x: opt.x + (x > 0 ? (offsetToSide + offsetBox) : -(offsetToSide + bbox.width + offsetBox)),
                y: opt.y + (y > 0 ? -bbox.height / 4: -bbox.height / 4)
            },
            true);
        }
        for (p in callout) {
            callout[p].show(true);
        }
    },

    // @private handles sprite animation for the series.
    onAnimate: function(sprite, attr) {
        sprite.show();
        return Ext.chart.series.Pie.superclass.onAnimate.apply(this, arguments);
    },

    isItemInPoint: function(x, y, item) {
        var me = this,
            chartBBox = me.chart.chartBBox,
            cx = me.centerX - chartBBox.x,
            cy = me.centerY - chartBBox.y,
            abs = Math.abs,
            dx = abs(x - cx),
            dy = abs(y - cy),
            startAngle = item.startAngle,
            endAngle = item.endAngle,
            rho = Math.sqrt(dx * dx + dy * dy),
            angle = Math.atan2(y - cy, x - cx) / me.rad;

        while (angle < endAngle) {
            angle += 360;
        }
        while (angle > startAngle) {
            angle -= 360;
        }
        return (angle <= startAngle && angle > endAngle
                && rho >= item.startRho && rho <= item.endRho);
    },

    getItemForAngle: function(angle) {
        var me = this,
            items = me.items,
            i = items.length;
        while (i--) {
            if (items[i] && me.isAngleInItem(angle, items[i])) {
                return items[i];
            }
        }
    },

    isAngleInItem: function(angle, item) {
        var startAngle = item.startAngle,
            endAngle = item.endAngle;

        while (angle < endAngle) {
            angle += 360;
        }
        while (angle > startAngle) {
            angle -= 360;
        }
        return (angle <= startAngle && angle > endAngle);
    },

    sliceContainsLabel: function(slice, label) {
        var me = this,
            PI = Math.PI,
            startAngle = slice.startAngle,
            endAngle = slice.endAngle,
            diffAngle =  Math.abs(endAngle - startAngle) * PI / 180,
            bbox = label.getBBox(true), //isWithoutTransform == true
            dist = me.radius,
            height = bbox.height + (me.labelOverflowPadding || 0),
            angleHeight;

        if (diffAngle >= PI) {
            return true;
        }

        angleHeight = Math.abs(Math.tan(diffAngle / 2)) * dist * 2;

        return angleHeight >= height;
    },

    // @private hides all elements in the series.
    hideAll: function() {
        var i, l, shadow, shadows, sh, lsh, sprite;
        if (!isNaN(this._index)) {
            this.__excludes = this.__excludes || [];
            this.__excludes[this._index] = true;
            sprite = this.slices[this._index].sprite;
            for (sh = 0, lsh = sprite.length; sh < lsh; sh++) {
                sprite[sh].setAttributes({
                    hidden: true
                }, true);
            }
            if (this.slices[this._index].shadowAttrs) {
                for (i = 0, shadows = this.slices[this._index].shadowAttrs, l = shadows.length; i < l; i++) {
                    shadow = shadows[i];
                    for (sh = 0, lsh = shadow.length; sh < lsh; sh++) {
                        shadow[sh].setAttributes({
                            hidden: true
                        }, true);
                    }
                }
            }
            this.drawSeries();
        }
    },

    // @private shows all elements in the series.
    showAll: function() {
        var me = this,
            excludes = me.__excludes,
            index = me._index;
        if (!isNaN(index) && excludes && excludes[index]) {
            excludes[index] = false;
            me.drawSeries();
        }
    },

    /**
     * Highlight the specified item. If no item is provided the whole series will be highlighted.
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    highlightItem: function(item) {
        var me = this,
            rad = me.rad;
        item = item || this.items[this._index];
        if (!item || item.sprite && item.sprite._animating) {
            return;
        }

        Ext.chart.series.Pie.superclass.highlightItem.apply(this, [item]);

        if (me.highlight === false) {
            return;
        }
        if ('segment' in me.highlightCfg) {
            var highlightSegment = me.highlightCfg.segment,
                animate = me.chart.animate,
                attrs, i, shadows, shadow, ln, to, itemHighlightSegment, prop;
            //animate labels
            if (me.labelsGroup) {
                var group = me.labelsGroup,
                    label = group.getAt(item.index),
                    middle = (item.startAngle + item.endAngle) / 2 * rad,
                    r = highlightSegment.margin || 0,
                    x = r * Math.cos(middle),
                    y = r * Math.sin(middle);

                //TODO(nico): rounding to 1e-10
                //gives the right translation. Translation
                //was buggy for very small numbers. In this
                //case we're not looking to translate to very small
                //numbers but not to translate at all.
                if (Math.abs(x) < 1e-10) {
                    x = 0;
                }
                if (Math.abs(y) < 1e-10) {
                    y = 0;
                }

                if (animate) {
                    label.stopAnimation();
                    label.animate({
                        to: {
                            translate: {
                                x: x,
                                y: y
                            }
                        },
                        duration: me.highlightDuration
                    });
                }
                else {
                    label.setAttributes({
                        translate: {
                            x: x,
                            y: y
                        }
                    }, true);
                }
            }
            //animate shadows
            if (me.chart.shadow && item.shadows) {
                i = 0;
                shadows = item.shadows;
                ln = shadows.length;
                for (; i < ln; i++) {
                    shadow = shadows[i];
                    to = {};
                    itemHighlightSegment = item.sprite._from.segment;
                    for (prop in itemHighlightSegment) {
                        if (! (prop in highlightSegment)) {
                            to[prop] = itemHighlightSegment[prop];
                        }
                    }
                    attrs = {
                        segment: Ext.applyIf(to, me.highlightCfg.segment)
                    };
                    if (animate) {
                        shadow.stopAnimation();
                        shadow.animate({
                            to: attrs,
                            duration: me.highlightDuration
                        });
                    }
                    else {
                        shadow.setAttributes(attrs, true);
                    }
                }
            }
        }
    },

    /**
     * un-highlights the specified item. If no item is provided it will un-highlight the entire series.
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    unHighlightItem: function() {
        var me = this;
        if (me.highlight === false) {
            return;
        }

        if (('segment' in me.highlightCfg) && me.items) {
            var items = me.items,
                animate = me.chart.animate,
                shadowsEnabled = !!me.chart.shadow,
                group = me.labelsGroup,
                len = items.length,
                i = 0,
                j = 0,
                display = me.label.display,
                shadowLen, p, to, ihs, hs, sprite, shadows, shadow, item, label, attrs;

            for (; i < len; i++) {
                item = items[i];
                if (!item) {
                    continue;
                }
                sprite = item.sprite;
                if (sprite && sprite._highlighted) {
                    //animate labels
                    if (group) {
                        label = group.getAt(item.index);
                        attrs = Ext.apply({
                            translate: {
                                x: 0,
                                y: 0
                            }
                        },
                        display == 'rotate' ? {
                            rotate: {
                                x: label.attr.x,
                                y: label.attr.y,
                                degrees: label.attr.rotation.degrees
                            }
                        }: {});
                        if (animate) {
                            label.stopAnimation();
                            label.animate({
                                to: attrs,
                                duration: me.highlightDuration
                            });
                        }
                        else {
                            label.setAttributes(attrs, true);
                        }
                    }
                    if (shadowsEnabled) {
                        shadows = item.shadows;
                        shadowLen = shadows.length;
                        for (; j < shadowLen; j++) {
                            to = {};
                            ihs = item.sprite._to.segment;
                            hs = item.sprite._from.segment;
                            Ext.apply(to, hs);
                            for (p in ihs) {
                                if (! (p in hs)) {
                                    to[p] = ihs[p];
                                }
                            }
                            shadow = shadows[j];
                            if (animate) {
                                shadow.stopAnimation();
                                shadow.animate({
                                    to: {
                                        segment: to
                                    },
                                    duration: me.highlightDuration
                                });
                            }
                            else {
                                shadow.setAttributes({ segment: to }, true);
                            }
                        }
                    }
                }
            }
        }
        Ext.chart.series.Pie.superclass.unHighlightItem.apply(me, arguments);
    },

    getLegendLabels: function() {
        var me = this,
            labelField = me.label.field,
            labels = [];

        if (labelField) {
            me.eachRecord(function(rec) {
                labels.push(rec.get(labelField));
            });
        }

        return labels;
    },

    /**
     * Returns the color of the series (to be displayed as color for the series legend item).
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    getLegendColor: function(index) {
        var me = this,
            colorSet = me.colorSet,
            colorArrayStyle = me.colorArrayStyle;
        return me.getColorFromStyle(
            (colorSet && colorSet[index % colorSet.length]) || colorArrayStyle[index % colorArrayStyle.length]
        );
    },

    /**
     * Iterate over each of the displayed records for this pie series, taking into account
     * records that have been combined into one by the user.
     * @param {Function} fn The function to execute for each record.
     * @param {Object} scope Scope for the fn.
     */
    eachRecord: function(fn, scope) {
        var me = this,
            store = me.chart.substore || me.chart.store,
            combinations = me.combinations,
            labelField = me.label.field,
            angleField = me.angleField || me.field || me.xField,
            lengthFields = [].concat(me.lengthField),
            records;

        // If we have combined records, take a snapshot of the store data and apply the combinations
        if (combinations) {
            records = store.data.clone();
            Ext.each(combinations, function(combo) {
                var record1 = records.getAt(combo[0]),
                    record2 = records.getAt(combo[1]),
                    comboData = {};

                // Build a combination data model object from the two target records
                comboData[labelField] = record1.get(labelField) + ' & ' + record2.get(labelField);
                comboData[angleField] = +record1.get(angleField) + record2.get(angleField);
                if (lengthFields[0]) {
                    Ext.each(lengthFields, function(lengthField) {
                        comboData[lengthField] = +record1.get(lengthField) + record2.get(lengthField);
                    });
                }

                // Insert the new combination record in place of the second original record, and remove both originals
                records.insert(combo[1], Ext.ModelMgr.create(comboData, store.model));
                records.remove(record1);
                records.remove(record2);
            });
            records.each(fn, scope);
        } else {
            // No combinations - just iterate the store directly
            store.each(fn, scope);
        }
    },

    getRecordCount: function() {
        var me = this,
            combinations = me.combinations;
        return Ext.chart.series.Pie.superclass.getRecordCount.call(me) - (combinations ? combinations.length : 0);
    },

    /**
     * @private update the position/size of the series surface. For pie series we set it to the
     * full chart size so it doesn't get clipped when slices slide out.
     */
    updateSurfaceBox: function() {
        var me = this,
            surface = me.getSurface(),
            overlaySurface = me.getOverlaySurface(),
            chart = me.chart,
            width = chart.curWidth,
            height = chart.curHeight;

        surface.el.setTopLeft(0, 0);
        surface.setSize(width, height);

        overlaySurface.el.setTopLeft(0, 0);
        overlaySurface.setSize(width, height);
    },

    reset: function() {
        this.rotation = 0;
        Ext.chart.series.Pie.superclass.reset.call(this);
    }

});


/**
 * @class Ext.chart.series.Radar
 * @extends Ext.chart.series.Series
 *
 * Creates a Radar Chart. A Radar Chart is a useful visualization technique for comparing different quantitative values for
 * a constrained number of categories.
 * As with all other series, the Radar series must be appended in the *series* Chart array configuration. See the Chart
 * documentation for more information. A typical configuration object for the radar series could be:
 *
 * {@img Ext.chart.series.Radar/Ext.chart.series.Radar.png Ext.chart.series.Radar chart series}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *         ]
 *     });
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         theme:'Category2',
 *         store: store,
 *         axes: [{
 *             type: 'Radial',
 *             position: 'radial',
 *             label: {
 *                 display: true
 *             }
 *         }],
 *         series: [{
 *             type: 'radar',
 *             xField: 'name',
 *             yField: 'data3',
 *             showInLegend: true,
 *             showMarkers: true,
 *             markerConfig: {
 *                 radius: 5,
 *                 size: 5
 *             },
 *             style: {
 *                 'stroke-width': 2,
 *                 fill: 'none'
 *             }
 *         },{
 *             type: 'radar',
 *             xField: 'name',
 *             yField: 'data2',
 *             showMarkers: true,
 *             showInLegend: true,
 *             markerConfig: {
 *                 radius: 5,
 *                 size: 5
 *             },
 *             style: {
 *                 'stroke-width': 2,
 *                 fill: 'none'
 *             }
 *         },{
 *             type: 'radar',
 *             xField: 'name',
 *             yField: 'data5',
 *             showMarkers: true,
 *             showInLegend: true,
 *             markerConfig: {
 *                 radius: 5,
 *                 size: 5
 *             },
 *             style: {
 *                 'stroke-width': 2,
 *                 fill: 'none'
 *             }
 *         }]
 *     });
 *
 * In this configuration we add three series to the chart. Each of these series is bound to the same categories field, `name` but bound to different properties for each category,
 * `data1`, `data2` and `data3` respectively. All series display markers by having `showMarkers` enabled. The configuration for the markers of each series can be set by adding properties onto
 * the markerConfig object. Finally we override some theme styling properties by adding properties to the `style` object.
 *
 * @xtype radar
 */
Ext.chart.series.Radar = Ext.extend(Ext.chart.series.Series, {

    type: "radar",

    rad: Math.PI / 180,

    rotation: 0,

    showInLegend: false,

    /**
     * @cfg {Object} style
     * An object containing styles for overriding series styles from Theming.
     */

    /**
     * @cfg {Boolean} showMarkers
     * Whether markers should be displayed at the data points along the line. If true,
     * then the {@link #markerConfig} config item will determine the markers' styling.
     */

    /**
     * @cfg {Object} markerConfig
     * The display style for the markers. Only used if {@link #showMarkers} is true.
     * The markerConfig is a configuration object containing the same set of properties defined in
     * the Sprite class. For example, if we were to set red circles as markers to the line series we could
     * pass the object:
     *
     <pre><code>
        markerConfig: {
            type: 'circle',
            radius: 4,
            'fill': '#f00'
        }
     </code></pre>

     */

     /**
     * @cfg {String} xField
     * The store record field name for the labels used in the radar series.
     */

     /**
     * @cfg {Object} yField
     * The store record field name for the deflection of the graph in the radar series.
     */

    constructor: function(config) {
        Ext.chart.series.Radar.superclass.constructor.apply(this, arguments);
        var me = this,
            surface = me.getSurface();
        me.group = surface.getGroup(me.seriesId);
        if (me.showMarkers) {
            me.markerGroup = surface.getGroup(me.seriesId + '-markers');
        }
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            group = me.group,
            chart = me.chart,
            field = me.field || me.yField,
            surface = me.getSurface(),
            chartBBox = chart.chartBBox,
            centerX, centerY,
            items,
            radius,
            maxValue = 0,
            fields = [],
            max = Math.max,
            cos = Math.cos,
            sin = Math.sin,
            rotation = -me.rotation,
            rad = Ext.draw.Draw.rad,
            angle,
            l = me.getRecordCount(),
            startPath, path, x, y, rho,
            nfields,
            seriesStyle = me.style,
            axis = chart.axes && chart.axes.get(0),
            aggregate = !(axis && axis.maximum);

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Radar.superclass.drawSeries.call(this);

        me.setBBox();

        maxValue = aggregate? 0 : (axis.maximum || 0);

        //if the store is empty then there's nothing to draw
        if (!l || me.seriesIsHidden) {
            surface.items.hide(true);
            return;
        }

        me.unHighlightItem();
        me.cleanHighlights();

        centerX = me.centerX = (chartBBox.width / 2);
        centerY = me.centerY = (chartBBox.height / 2);
        me.radius = radius = Math.min(chartBBox.width, chartBBox.height) /2;
        me.items = items = [];

        if (aggregate) {
            //get all renderer fields
            chart.series.each(function(series) {
                fields.push(series.yField);
            });
            //get maxValue to interpolate
            me.eachRecord(function(record, i) {
                for (i = 0, nfields = fields.length; i < nfields; i++) {
                    maxValue = max(+record.get(fields[i]), maxValue);
                }
            });
        }
        //ensure non-zero value.
        maxValue = maxValue || 1;
        //create path and items
        startPath = []; path = [];
        me.eachRecord(function(record, i) {
            rho = radius * record.get(field) / maxValue;
            angle = rad(rotation + i / l * 360);
            x = rho * cos(angle);
            y = rho * sin(angle);
            if (i == 0) {
                path.push('M', x + centerX, y + centerY);
                startPath.push('M', 0.01 * x + centerX, 0.01 * y + centerY);
            } else {
                path.push('L', x + centerX, y + centerY);
                startPath.push('L', 0.01 * x + centerX, 0.01 * y + centerY);
            }
            items.push({
                sprite: false, //TODO(nico): add markers
                point: [centerX + x, centerY + y],
                series: me
            });
        });
        path.push('Z');
        //create path sprite
        if (!me.radar) {
            me.radar = surface.add(Ext.apply({
                type: 'path',
                group: group,
                path: startPath
            }, seriesStyle || {}));
        }
        //reset on resizing
        if (chart.resizing) {
            me.radar.setAttributes({
                path: startPath
            }, true);
        }
        //render/animate
        me.radar.show(true);
        if (chart.animate) {
            me.onAnimate(me.radar, {
                to: Ext.apply({
                    path: path
                }, seriesStyle || {})
            });
        } else {
            me.radar.setAttributes(Ext.apply({
                path: path
            }, seriesStyle || {}), true);
        }
        //render markers, labels and callouts
        if (me.showMarkers) {
            me.drawMarkers();
        }
        me.renderLabels();
        me.renderCallouts();

        me.fireEvent('draw', me);
    },

    // @private draws the markers for the lines (if any).
    drawMarkers: function() {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            markerStyle = Ext.apply({}, me.markerStyle.style || {}),
            endMarkerStyle = Ext.apply(markerStyle, me.markerConfig),
            items = me.items,
            type = endMarkerStyle.type,
            markerGroup = me.markerGroup,
            centerX = me.centerX,
            centerY = me.centerY,
            item, i, l, marker;

        delete endMarkerStyle.type;

        for (i = 0, l = items.length; i < l; i++) {
            item = items[i];
            marker = markerGroup.getAt(i);
            if (!marker) {
                marker = Ext.chart.Shape[type](surface, Ext.apply({
                    group: markerGroup,
                    x: 0,
                    y: 0,
                    translate: {
                        x: centerX,
                        y: centerY
                    }
                }, endMarkerStyle));
            }
            else {
                marker.show();
            }
            if (chart.resizing) {
                marker.setAttributes({
                    x: 0,
                    y: 0,
                    translate: {
                        x: centerX,
                        y: centerY
                    }
                }, true);
            }
            marker._to = {
                translate: {
                    x: item.point[0],
                    y: item.point[1]
                }
            };
            //render/animate
            if (chart.animate) {
                me.onAnimate(marker, {
                    to: marker._to
                });
            }
            else {
                marker.setAttributes(Ext.apply(marker._to, endMarkerStyle || {}), true);
            }
        }
    },

    isItemInPoint: function(x, y, item) {
        var point,
            tolerance = 10,
            abs = Math.abs;
        point = item.point;
        return (abs(point[0] - x) <= tolerance &&
                abs(point[1] - y) <= tolerance);
    },

    // @private callback for when creating a label sprite.
    onCreateLabel: function(storeItem, item) {
        var me = this,
            group = me.labelsGroup,
            config = me.label,
            centerX = me.centerX,
            centerY = me.centerY;

        return me.getSurface().add(Ext.apply({
            'type': 'text',
            'text-anchor': 'middle',
            'group': group,
            'x': centerX,
            'y': centerY
        }, config || {}));
    },

    // @private callback for when placing a label sprite.
    onPlaceLabel: function(label, storeItem, item, i, display, animate) {
        var me = this,
            chart = me.chart,
            resizing = chart.resizing,
            config = me.label,
            format = config.renderer,
            field = config.field,
            centerX = me.centerX,
            centerY = me.centerY,
            opt = {
                x: item.point[0],
                y: item.point[1]
            };

        label.setAttributes({
            text: format(storeItem.get(field)),
            hidden: true
        },
        true);

        if (resizing) {
            label.setAttributes({
                x: centerX,
                y: centerY
            }, true);
        }

        if (animate) {
            label.show(true);
            me.onAnimate(label, {
                to: opt
            });
        } else {
            label.setAttributes(opt, true);
            label.show(true);
        }
    },

    // @private for toggling (show/hide) series.
    toggleAll: function(show) {
        var me = this,
            i, ln, shadow, shadows;
        if (!show) {
            Ext.chart.series.Radar.superclass.hideAll.call(me);
        }
        else {
            Ext.chart.series.Radar.superclass.showAll.call(me);
        }
        if (me.radar) {
            me.radar.setAttributes({
                hidden: !show
            }, true);
            //hide shadows too
            if (me.radar.shadows) {
                for (i = 0, shadows = me.radar.shadows, ln = shadows.length; i < ln; i++) {
                    shadow = shadows[i];
                    shadow.setAttributes({
                        hidden: !show
                    }, true);
                }
            }
        }
    },

    // @private hide all elements in the series.
    hideAll: function() {
        this.toggleAll(false);
        this.hideMarkers(0);
    },

    // @private show all elements in the series.
    showAll: function() {
        this.toggleAll(true);
    },

    // @private hide all markers that belong to `markerGroup`
    hideMarkers: function(index) {
        var me = this,
            count = me.markerGroup && me.markerGroup.getCount() || 0,
            i = index || 0;
        for (; i < count; i++) {
            me.markerGroup.getAt(i).hide(true);
        }
    },

    getLegendLabels: function() {
        var label = this.title || this.yField;
        return label ? [label] : [];
    },

    reset: function() {
        this.rotation = 0;
        Ext.chart.series.Radar.superclass.reset.call(this);
    }

});


/**
 * @class Ext.chart.series.Scatter
 * @extends Ext.chart.series.Cartesian
 *
 * Creates a Scatter Chart. The scatter plot is useful when trying to display more than two variables in the same visualization.
 * These variables can be mapped into x, y coordinates and also to an element's radius/size, color, etc.
 * As with all other series, the Scatter Series must be appended in the *series* Chart array configuration. See the Chart
 * documentation for more information on creating charts. A typical configuration object for the scatter could be:
 *
 * {@img Ext.chart.series.Scatter/Ext.chart.series.Scatter.png Ext.chart.series.Scatter chart series}
 *
 *     var store = new Ext.data.JsonStore({
 *         fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
 *         data: [
 *             {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
 *             {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
 *             {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
 *             {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
 *             {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}
 *         ]
 *     });
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 500,
 *         height: 300,
 *         animate: true,
 *         theme:'Category2',
 *         store: store,
 *         axes: [{
 *             type: 'Numeric',
 *             position: 'bottom',
 *             fields: ['data1', 'data2', 'data3'],
 *             title: 'Sample Values',
 *             grid: true,
 *             minimum: 0
 *         }, {
 *             type: 'Category',
 *             position: 'left',
 *             fields: ['name'],
 *             title: 'Sample Metrics'
 *         }],
 *         series: [{
 *             type: 'scatter',
 *             markerConfig: {
 *                 radius: 5,
 *                 size: 5
 *             },
 *             axis: 'left',
 *             xField: 'name',
 *             yField: 'data2'
 *         }, {
 *             type: 'scatter',
 *             markerConfig: {
 *                 radius: 5,
 *                 size: 5
 *             },
 *             axis: 'left',
 *             xField: 'name',
 *             yField: 'data3'
 *         }]
 *     });
 *
 * In this configuration we add three different categories of scatter series. Each of them is bound to a different field of the same data store,
 * `data1`, `data2` and `data3` respectively. All x-fields for the series must be the same field, in this case `name`.
 * Each scatter series has a different styling configuration for markers, specified by the `markerConfig` object. Finally we set the left axis as
 * axis to show the current values of the elements.
 *
 * @xtype scatter
 */
Ext.chart.series.Scatter = Ext.extend(Ext.chart.series.Cartesian, {

    type: 'scatter',

    /**
     * @cfg {Object} markerConfig
     * The display style for the scatter series markers.
     */

    /**
     * @cfg {Object} style
     * Append styling properties to this object for it to override theme properties.
     */

    /**
     * @cfg {String/Array} axis
     * The position of the axis to bind the values to. Possible values are 'left', 'bottom', 'top' and 'right'.
     * You must explicitly set this value to bind the values of the line series to the ones in the axis, otherwise a
     * relative scale will be used. If multiple axes are being used, they should both be specified in in the configuration.
     */

    constructor: function(config) {
        Ext.chart.series.Scatter.superclass.constructor.apply(this, arguments);
        var me = this,
            shadow = me.chart.shadow,
            surface = me.getSurface(), i, l;
        Ext.apply(me, config, {
            style: {},
            markerConfig: {},
            shadowAttributes: surface.getShadowAttributesArray(),
            shadowOptions: Ext.apply(surface.getShadowOptions(), shadow === true ? {} : (shadow || {}))
        });
        me.group = surface.getGroup(me.seriesId);
        if (shadow) {
            for (i = 0, l = me.shadowAttributes.length; i < l; i++) {
                me.shadowGroups.push(surface.getGroup(me.seriesId + '-shadows' + i));
            }
        }
    },

    // @private Get chart and data boundaries
    getBounds: function() {
        var me = this,
            chart = me.chart,
            count = me.getRecordCount(),
            axes = [].concat(me.axis),
            bbox, xScale, yScale, ln, minX, minY, maxX, maxY, i, axis, ends;

        me.setBBox();
        bbox = me.bbox;

        for (i = 0, ln = axes.length; i < ln; i++) {
            axis = chart.axes.get(axes[i]);
            if (axis) {
                ends = axis.calcEnds();
                if (axis.position == 'top' || axis.position == 'bottom') {
                    minX = ends.from;
                    maxX = ends.to;
                }
                else {
                    minY = ends.from;
                    maxY = ends.to;
                }
            }
        }
        // If a field was specified without a corresponding axis, create one to get bounds
        if (me.xField && !Ext.isNumber(minX)) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.xField)
            }).calcEnds();
            minX = axis.from;
            maxX = axis.to;
        }
        if (me.yField && !Ext.isNumber(minY)) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.yField)
            }).calcEnds();
            minY = axis.from;
            maxY = axis.to;
        }

        if (isNaN(minX)) {
            minX = 0;
            maxX = count - 1;
            xScale = bbox.width / (count - 1);
        }
        else {
            xScale = bbox.width / (maxX - minX);
        }

        if (isNaN(minY)) {
            minY = 0;
            maxY = count - 1;
            yScale = bbox.height / (count - 1);
        }
        else {
            yScale = bbox.height / (maxY - minY);
        }

        return {
            bbox: bbox,
            minX: minX,
            minY: minY,
            xScale: xScale,
            yScale: yScale
        };
    },

    // @private Build an array of paths for the chart
    getPaths: function() {
        var me = this,
            chart = me.chart,
            enableShadows = chart.shadow,
            group = me.group,
            bounds = me.bounds = me.getBounds(),
            bbox = me.bbox,
            xScale = bounds.xScale,
            yScale = bounds.yScale,
            minX = bounds.minX,
            minY = bounds.minY,
            boxX = bbox.x,
            boxY = bbox.y,
            boxHeight = bbox.height,
            attrs = [],
            x, y, xValue, yValue, sprite;

        me.items = me.items || [];

        me.eachRecord(function(record, i) {
            xValue = record.get(me.xField);
            yValue = record.get(me.yField);
            //skip undefined values
            if (typeof yValue == 'undefined' || (typeof yValue == 'string' && !yValue)) {
                return;
            }
            // Ensure a value
            if (typeof xValue == 'string' || typeof xValue == 'object') {
                xValue = i;
            }
            if (typeof yValue == 'string' || typeof yValue == 'object') {
                yValue = i;
            }
            x = boxX + (xValue - minX) * xScale;
            y = boxY + boxHeight - (yValue - minY) * yScale;
            attrs.push({
                x: x,
                y: y
            });

            me.items.push({
                series: me,
                value: [xValue, yValue],
                point: [x, y],
                storeItem: record
            });

            // When resizing, reset before animating
            if (chart.animate && chart.resizing) {
                sprite = group.getAt(i);
                if (sprite) {
                    me.resetPoint(sprite);
                    if (enableShadows) {
                        me.resetShadow(sprite);
                    }
                }
            }
        });

        return attrs;
    },

    // @private translate point to the center
    resetPoint: function(sprite) {
        var bbox = this.bbox;
        sprite.setAttributes({
            translate: {
                x: (bbox.x + bbox.width) / 2,
                y: (bbox.y + bbox.height) / 2
            }
        }, true);
    },

    // @private translate shadows of a sprite to the center
    resetShadow: function(sprite) {
        var me = this,
            shadows = sprite.shadows,
            shadowAttributes = me.shadowAttributes,
            ln = me.shadowGroups.length,
            bbox = me.bbox,
            i, attr;
        for (i = 0; i < ln; i++) {
            attr = Ext.apply({}, shadowAttributes[i]);
            if (attr.translate) {
                attr.translate.x += (bbox.x + bbox.width) / 2;
                attr.translate.y += (bbox.y + bbox.height) / 2;
            }
            else {
                attr.translate = {
                    x: (bbox.x + bbox.width) / 2,
                    y: (bbox.y + bbox.height) / 2
                };
            }
            shadows[i].setAttributes(attr, true);
        }
    },

    // @private create a new point
    createPoint: function(attr, type) {
        var me = this,
            group = me.group,
            bbox = me.bbox;

        return Ext.chart.Shape[type](me.getSurface(), Ext.apply({}, {
            x: 0,
            y: 0,
            group: group,
            translate: {
                x: (bbox.x + bbox.width) / 2,
                y: (bbox.y + bbox.height) / 2
            }
        }, attr));
    },

    // @private create a new set of shadows for a sprite
    createShadow: function(sprite, endMarkerStyle, type) {
        var me = this,
            shadowGroups = me.shadowGroups,
            shadowAttributes = me.shadowAttributes,
            lnsh = shadowGroups.length,
            bbox = me.bbox,
            i, shadow, shadows, attr;

        sprite.shadows = shadows = [];

        for (i = 0; i < lnsh; i++) {
            attr = Ext.apply({}, shadowAttributes[i]);
            if (attr.translate) {
                attr.translate.x += (bbox.x + bbox.width) / 2;
                attr.translate.y += (bbox.y + bbox.height) / 2;
            }
            else {
                Ext.apply(attr, {
                    translate: {
                        x: (bbox.x + bbox.width) / 2,
                        y: (bbox.y + bbox.height) / 2
                    }
                });
            }
            Ext.apply(attr, endMarkerStyle);
            shadow = Ext.chart.Shape[type](me.getSurface(), Ext.apply({}, {
                x: 0,
                y: 0,
                group: shadowGroups[i]
            }, attr));
            shadows.push(shadow);
        }
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            chart = me.chart,
            store = chart.substore || chart.store,
            group = me.group,
            enableShadows = chart.shadow,
            shadowGroups = me.shadowGroups,
            shadowAttributes = me.shadowAttributes,
            lnsh = shadowGroups.length,
            sprite, attrs, attr, ln, i, endMarkerStyle, shindex, type, shadows,
            rendererAttributes, shadowAttribute;

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Scatter.superclass.drawSeries.call(this);

        endMarkerStyle = Ext.apply(me.markerStyle.style || {}, me.markerConfig);
        type = endMarkerStyle.type;
        delete endMarkerStyle.type;

        //if the store is empty then there's nothing to be rendered
        if (!me.getRecordCount() || me.seriesIsHidden) {
            me.getSurface().items.hide(true);
            return;
        }

        me.unHighlightItem();
        me.cleanHighlights();

        attrs = me.getPaths();
        ln = attrs.length;
        for (i = 0; i < ln; i++) {
            attr = attrs[i];
            sprite = group.getAt(i);
            Ext.apply(attr, endMarkerStyle);

            // Create a new sprite if needed (no height)
            if (!sprite) {
                sprite = me.createPoint(attr, type);
                if (enableShadows) {
                    me.createShadow(sprite, endMarkerStyle, type);
                    sprite.setAttributes(me.shadowOptions, true);
                }
            }

            shadows = sprite.shadows;
            if (chart.animate) {
                rendererAttributes = me.renderer(sprite, store.getAt(i), { translate: attr }, i, store);
                sprite._to = rendererAttributes;
                me.onAnimate(sprite, {
                    to: rendererAttributes
                });
                //animate shadows
                for (shindex = 0; shindex < lnsh; shindex++) {
                    shadowAttribute = Ext.apply({}, shadowAttributes[shindex]);
                    rendererAttributes = me.renderer(shadows[shindex], store.getAt(i), Ext.apply({}, {
                        translate: {
                            x: attr.x + (shadowAttribute.translate? shadowAttribute.translate.x : 0),
                            y: attr.y + (shadowAttribute.translate? shadowAttribute.translate.y : 0)
                        }
                    }, shadowAttribute), i, store);
                    me.onAnimate(shadows[shindex], { to: rendererAttributes });
                }
            }
            else {
                rendererAttributes = me.renderer(sprite, store.getAt(i), { translate: attr }, i, store);
                sprite._to = rendererAttributes;
                sprite.setAttributes(rendererAttributes, true);
                //update shadows
                for (shindex = 0; shindex < lnsh; shindex++) {
                    shadowAttribute = Ext.apply({}, shadowAttributes[shindex]);
                    rendererAttributes = me.renderer(shadows[shindex], store.getAt(i), Ext.apply({}, {
                        translate: {
                            x: attr.x + (shadowAttribute.translate? shadowAttribute.translate.x : 0),
                            y: attr.y + (shadowAttribute.translate? shadowAttribute.translate.y : 0)
                        }
                    }, shadowAttribute), i, store);
                    shadows[shindex].setAttributes(rendererAttributes, true);
                }
            }
            me.items[i].sprite = sprite;
        }

        // Hide unused sprites
        ln = group.getCount();
        for (i = attrs.length; i < ln; i++) {
            group.getAt(i).hide(true);
        }
        me.renderLabels();
        me.renderCallouts();

        me.fireEvent('draw', me);
    },

    // @private callback for when creating a label sprite.
    onCreateLabel: function(storeItem, item, i, display) {
        var me = this,
            group = me.labelsGroup,
            config = me.label,
            endLabelStyle = Ext.apply({}, config, me.labelStyle.style || {}),
            bbox = me.bbox;

        return me.getSurface().add(Ext.apply({
            type: 'text',
            group: group,
            x: item.point[0],
            y: bbox.y + bbox.height / 2
        }, endLabelStyle));
    },

    // @private callback for when placing a label sprite.
    onPlaceLabel: function(label, storeItem, item, i, display, animate) {
        var me = this,
            chart = me.chart,
            resizing = chart.resizing,
            config = me.label,
            format = config.renderer,
            field = config.field,
            bbox = me.bbox,
            x = item.point[0],
            y = item.point[1],
            radius = item.sprite.attr.radius,
            bb, width, height, anim;

        label.setAttributes({
            text: format(storeItem.get(field)),
            hidden: true
        }, true);

        if (display == 'rotate') {
            label.setAttributes({
                'text-anchor': 'start',
                'rotation': {
                    x: x,
                    y: y,
                    degrees: -45
                }
            }, true);
            //correct label position to fit into the box
            bb = label.getBBox();
            width = bb.width;
            height = bb.height;
            x = x < bbox.x? bbox.x : x;
            x = (x + width > bbox.x + bbox.width)? (x - (x + width - bbox.x - bbox.width)) : x;
            y = (y - height < bbox.y)? bbox.y + height : y;

        } else if (display == 'under' || display == 'over') {
            //TODO(nicolas): find out why width/height values in circle bounding boxes are undefined.
            bb = item.sprite.getBBox();
            bb.width = bb.width || (radius * 2);
            bb.height = bb.height || (radius * 2);
            y = y + (display == 'over'? -bb.height : bb.height);
            //correct label position to fit into the box
            bb = label.getBBox();
            width = bb.width/2;
            height = bb.height/2;
            x = x - width < bbox.x ? bbox.x + width : x;
            x = (x + width > bbox.x + bbox.width) ? (x - (x + width - bbox.x - bbox.width)) : x;
            y = y - height < bbox.y? bbox.y + height : y;
            y = (y + height > bbox.y + bbox.height) ? (y - (y + height - bbox.y - bbox.height)) : y;
        }

        if (!chart.animate) {
            label.setAttributes({
                x: x,
                y: y
            }, true);
            label.show(true);
        }
        else {
            if (resizing) {
                anim = item.sprite.getActiveAnimation();
                if (anim) {
                    anim.on('afteranimate', function() {
                        label.setAttributes({
                            x: x,
                            y: y
                        }, true);
                        label.show(true);
                    });
                }
                else {
                    label.show(true);
                }
            }
            else {
                me.onAnimate(label, {
                    to: {
                        x: x,
                        y: y
                    }
                });
            }
        }
    },

    // @private callback for when placing a callout sprite.
    onPlaceCallout: function(callout, storeItem, item, i, display, animate, index) {
        var me = this,
            chart = me.chart,
            cur = item.point,
            normal,
            bbox = callout.label.getBBox(),
            offsetFromViz = 30,
            offsetBox = 3,
            boxx, boxy, boxw, boxh,
            p, clipRect = me.bbox,
            x, y;

        //position
        normal = [Math.cos(Math.PI /4), -Math.sin(Math.PI /4)];
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;

        //box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;

        //now check if we're out of bounds and invert the normal vector correspondingly
        //this may add new overlaps between labels (but labels won't be out of bounds).
        if (boxx < clipRect[0] || (boxx + boxw) > (clipRect[0] + clipRect[2])) {
            normal[0] *= -1;
        }
        if (boxy < clipRect[1] || (boxy + boxh) > (clipRect[1] + clipRect[3])) {
            normal[1] *= -1;
        }

        //update positions
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;

        //update box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;

        if (chart.animate) {
            //set the line from the middle of the pie to the box.
            me.onAnimate(callout.lines, {
                to: {
                    path: ["M", cur[0], cur[1], "L", x, y, "Z"]
                }
            }, true);
            //set box position
            me.onAnimate(callout.box, {
                to: {
                    x: boxx,
                    y: boxy,
                    width: boxw,
                    height: boxh
                }
            }, true);
            //set text position
            me.onAnimate(callout.label, {
                to: {
                    x: x + (normal[0] > 0? offsetBox : -(bbox.width + offsetBox)),
                    y: y
                }
            }, true);
        } else {
            //set the line from the middle of the pie to the box.
            callout.lines.setAttributes({
                path: ["M", cur[0], cur[1], "L", x, y, "Z"]
            }, true);
            //set box position
            callout.box.setAttributes({
                x: boxx,
                y: boxy,
                width: boxw,
                height: boxh
            }, true);
            //set text position
            callout.label.setAttributes({
                x: x + (normal[0] > 0? offsetBox : -(bbox.width + offsetBox)),
                y: y
            }, true);
        }
        for (p in callout) {
            callout[p].show(true);
        }
    },

    // @private handles sprite animation for the series.
    onAnimate: function(sprite, attr) {
        sprite.show();
        Ext.chart.series.Scatter.superclass.onAnimate.apply(this, arguments);
    },

    isItemInPoint: function(x, y, item) {
        var point,
            tolerance = 10;

        point = item.point;
        return (point[0] - tolerance <= x && point[0] + tolerance >= x &&
            point[1] - tolerance <= y && point[1] + tolerance >= y);
    }
});


/**
 * @class Ext.chart.series.Area
 * @extends Ext.chart.series.Cartesian
 * 
 <p>
    Creates a Stacked Area Chart. The stacked area chart is useful when displaying multiple aggregated layers of information.
    As with all other series, the Area Series must be appended in the *series* Chart array configuration. See the Chart 
    documentation for more information. A typical configuration object for the area series could be:
 </p>
{@img Ext.chart.series.Area/Ext.chart.series.Area.png Ext.chart.series.Area chart series} 
  <pre><code>
   var store = new Ext.data.JsonStore({
        fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
        data: [
            {'name':'metric one', 'data1':10, 'data2':12, 'data3':14, 'data4':8, 'data5':13},
            {'name':'metric two', 'data1':7, 'data2':8, 'data3':16, 'data4':10, 'data5':3},
            {'name':'metric three', 'data1':5, 'data2':2, 'data3':14, 'data4':12, 'data5':7},
            {'name':'metric four', 'data1':2, 'data2':14, 'data3':6, 'data4':1, 'data5':23},
            {'name':'metric five', 'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33}                                                
        ]
    });
    
    new Ext.chart.Chart({
        renderTo: Ext.getBody(),
        width: 500,
        height: 300,
        store: store,
        axes: [{
            type: 'Numeric',
            grid: true,
            position: 'left',
            fields: ['data1', 'data2', 'data3', 'data4', 'data5'],
            title: 'Sample Values',
            grid: {
                odd: {
                    opacity: 1,
                    fill: '#ddd',
                    stroke: '#bbb',
                    'stroke-width': 1
                }
            },
            minimum: 0,
            adjustMinimumByMajorUnit: 0
        }, {
            type: 'Category',
            position: 'bottom',
            fields: ['name'],
            title: 'Sample Metrics',
            grid: true,
            label: {
                rotate: {
                    degrees: 315
                }
            }
        }],
        series: [{
            type: 'area',
            highlight: false,
            axis: 'left',
            xField: 'name',
            yField: ['data1', 'data2', 'data3', 'data4', 'data5'],
            style: {
                opacity: 0.93
            }
        }]
    });
   </code></pre>
 
  
 <p>
  In this configuration we set `area` as the type for the series, set highlighting options to true for highlighting elements on hover, 
  take the left axis to measure the data in the area series, set as xField (x values) the name field of each element in the store, 
  and as yFields (aggregated layers) seven data fields from the same store. Then we override some theming styles by adding some opacity 
  to the style object.
 </p>
  
 * @xtype area
 * 
 */
Ext.chart.series.Area = Ext.extend(Ext.chart.series.Cartesian, {

    type: 'area',

    // @private Area charts are alyways stacked
    stacked: true,

    /**
     * @cfg {Object} style 
     * Append styling properties to this object for it to override theme properties.
     */

    constructor: function(config) {
        Ext.chart.series.Area.superclass.constructor.apply(this, arguments);
        var me = this,
            surface = me.getSurface(),
            i, l;
        Ext.apply(me, config, {
            __excludes: [],
            highlightCfg: {
                lineWidth: 3,
                stroke: '#55c',
                opacity: 0.8,
                color: '#f00'
            }
        });
        if (me.highlight !== false) {
            me.highlightSprite = surface.add({
                type: 'path',
                path: ['M', 0, 0],
                zIndex: 1000,
                opacity: 0.3,
                lineWidth: 5,
                hidden: true,
                stroke: '#444'
            });
        }
        me.group = surface.getGroup(me.seriesId);
    },

    // @private Shrinks dataSets down to a smaller size
    shrink: function(xValues, yValues, size) {
        var len = xValues.length,
            ratio = Math.floor(len / size),
            i, j,
            xSum = 0,
            yCompLen = this.areas.length,
            ySum = [],
            xRes = [],
            yRes = [];
        //initialize array
        for (j = 0; j < yCompLen; ++j) {
            ySum[j] = 0;
        }
        for (i = 0; i < len; ++i) {
            xSum += xValues[i];
            for (j = 0; j < yCompLen; ++j) {
                ySum[j] += yValues[i][j];
            }
            if (i % ratio == 0) {
                //push averages
                xRes.push(xSum/ratio);
                for (j = 0; j < yCompLen; ++j) {
                    ySum[j] /= ratio;
                }
                yRes.push(ySum);
                //reset sum accumulators
                xSum = 0;
                for (j = 0, ySum = []; j < yCompLen; ++j) {
                    ySum[j] = 0;
                }
            }
        }
        return {
            x: xRes,
            y: yRes
        };
    },

    // @private Get chart and data boundaries
    getBounds: function() {
        var me = this,
            chart = me.chart,
            xValues = [],
            allYValues = [],
            infinity = Infinity,
            minX = infinity,
            maxX = -infinity,
            minY, maxY,
            math = Math,
            mmin = math.min,
            mmax = math.max,
            bbox, xScale, yScale, ln, sumValues, axis, out, recordYValues;

        me.setBBox();
        bbox = me.bbox;

        // Run through the axis
        if (me.axis) {
            axis = chart.axes.get(me.axis);
            if (axis) {
                out = axis.calcEnds();
                minY = out.from;
                maxY = out.to;
            }
        }

        if (me.yField && !Ext.isNumber(minY)) {
            axis = new Ext.chart.axis.Axis({
                chart: chart,
                fields: [].concat(me.yField)
            });
            out = axis.calcEnds();
            minY = out.from;
            maxY = out.to;
        }

        if (!Ext.isNumber(minY)) {
            minY = 0;
        }
        if (!Ext.isNumber(maxY)) {
            maxY = 0;
        }

        function eachYValue(yValue) {
            if (typeof yValue == 'number') {
                recordYValues.push(yValue);
            }
        }

        me.eachRecord(function(record, i) {
            var xValue = record.get(me.xField);
            if (typeof xValue != 'number') {
                xValue = i;
            }
            xValues.push(xValue);

            recordYValues = [];
            me.eachYValue(record, eachYValue);

            minX = math.min(minX, xValue);
            maxX = math.max(maxX, xValue);
            allYValues.push(recordYValues);
        });

        xScale = bbox.width / ((maxX - minX) || 1);
        yScale = bbox.height / ((maxY - minY) || 1);

        ln = xValues.length;
        if ((ln > bbox.width) && me.areas) {
            sumValues = me.shrink(xValues, allYValues, bbox.width);
            xValues = sumValues.x;
            allYValues = sumValues.y;
        }

        return {
            bbox: bbox,
            minX: minX,
            minY: minY,
            xValues: xValues,
            yValues: allYValues,
            xScale: xScale,
            yScale: yScale,
            areasLen: me.getYValueCount()
        };
    },

    // @private Build an array of paths for the chart
    getPaths: function() {
        var me = this,
            first = true,
            bounds = me.getBounds(),
            bbox = bounds.bbox,
            items = me.items = [],
            componentPaths = [],
            componentPath,
            paths = [],
            i, ln, x, y, xValue, yValue, acumY, areaIndex, prevAreaIndex, areaElem, path;

        ln = bounds.xValues.length;
        // Start the path
        for (i = 0; i < ln; i++) {
            xValue = bounds.xValues[i];
            yValue = bounds.yValues[i];
            x = bbox.x + (xValue - bounds.minX) * bounds.xScale;
            acumY = 0;
            for (areaIndex = 0; areaIndex < bounds.areasLen; areaIndex++) {
                // Excluded series
                if (me.isExcluded(areaIndex)) {
                    continue;
                }
                if (!componentPaths[areaIndex]) {
                    componentPaths[areaIndex] = [];
                }
                areaElem = yValue[areaIndex];
                acumY += areaElem;
                y = bbox.y + bbox.height - (acumY - bounds.minY) * bounds.yScale;
                if (!paths[areaIndex]) {
                    paths[areaIndex] = ['M', x, y];
                    componentPaths[areaIndex].push(['L', x, y]);
                } else {
                    paths[areaIndex].push('L', x, y);
                    componentPaths[areaIndex].push(['L', x, y]);
                }
                if (!items[areaIndex]) {
                    items[areaIndex] = {
                        pointsUp: [],
                        pointsDown: [],
                        series: me
                    };
                }
                items[areaIndex].pointsUp.push([x, y]);
            }
        }
        
        // Close the paths
        for (areaIndex = 0; areaIndex < bounds.areasLen; areaIndex++) {
            // Excluded series
            if (me.isExcluded(areaIndex)) {
                continue;
            }
            path = paths[areaIndex];
            // Close bottom path to the axis
            if (areaIndex == 0 || first) {
                first = false;
                path.push('L', x, bbox.y + bbox.height,
                          'L', bbox.x, bbox.y + bbox.height,
                          'Z');
            }
            // Close other paths to the one before them
            else {
                componentPath = componentPaths[prevAreaIndex];
                componentPath.reverse();
                path.push('L', x, componentPath[0][2]);
                for (i = 0; i < ln; i++) {
                    path.push(componentPath[i][0],
                              componentPath[i][1],
                              componentPath[i][2]);
                    items[areaIndex].pointsDown[ln -i -1] = [componentPath[i][1], componentPath[i][2]];
                }
                path.push('L', bbox.x, path[2], 'Z');
            }
            prevAreaIndex = areaIndex;
        }
        return {
            paths: paths,
            areasLen: bounds.areasLen
        };
    },

    /**
     * Draws the series for the current chart.
     */
    drawSeries: function() {
        var me = this,
            chart = me.chart,
            store = chart.substore || chart.store,
            surface = me.getSurface(),
            animate = chart.animate,
            group = me.group,
            areas = me.areas,
            endLineStyle = me.style,
            colorArrayStyle = me.colorArrayStyle,
            colorArrayLength = colorArrayStyle && colorArrayStyle.length || 0,
            areaIndex, areaElem, paths, path, rendererAttributes;

        if (me.fireEvent('beforedraw', me) === false) {
            return;
        }

        Ext.chart.series.Area.superclass.drawSeries.call(this);
        me.unHighlightItem();
        me.cleanHighlights();

        if (!me.getRecordCount()) {
            surface.items.hide(true);
            return;
        }
        
        paths = me.getPaths();

        if (!areas) {
            areas = me.areas = [];
        }

        for (areaIndex = 0; areaIndex < paths.areasLen; areaIndex++) {
            // Excluded series
            if (me.isExcluded(areaIndex)) {
                continue;
            }
            if (!areas[areaIndex]) {
                me.items[areaIndex].sprite = areas[areaIndex] = surface.add(Ext.apply({}, {
                    type: 'path',
                    group: group,
                    // 'clip-rect': me.clipBox,
                    path: paths.paths[areaIndex],
                    stroke: endLineStyle.stroke || colorArrayStyle[areaIndex % colorArrayLength],
                    fill: colorArrayStyle[areaIndex % colorArrayLength]
                }, endLineStyle || {}));
            }
            areaElem = areas[areaIndex];
            path = paths.paths[areaIndex];
            if (animate) {
                //Add renderer to line. There is not a unique record associated with this.
                rendererAttributes = me.renderer(areaElem, false, { 
                    path: path,
                    // 'clip-rect': me.clipBox,
                    fill: colorArrayStyle[areaIndex % colorArrayLength],
                    stroke: endLineStyle.stroke || colorArrayStyle[areaIndex % colorArrayLength]
                }, areaIndex, store);
                //fill should not be used here but when drawing the special fill path object
                me.animation = me.onAnimate(areaElem, {
                    to: rendererAttributes
                });
            } else {
                rendererAttributes = me.renderer(areaElem, false, { 
                    path: path,
                    // 'clip-rect': me.clipBox,
                    hidden: false,
                    fill: colorArrayStyle[areaIndex % colorArrayLength],
                    stroke: endLineStyle.stroke || colorArrayStyle[areaIndex % colorArrayLength]
                }, areaIndex, store);
                areas[areaIndex].setAttributes(rendererAttributes, true);
            }
        }

        // Hide leftover area sprites
        for (; areaIndex < areas.length; areaIndex++) {
            areas[areaIndex].hide();
        }

        me.renderLabels();
        me.renderCallouts();

        me.fireEvent('draw', me);
    },

    // @private
    onAnimate: function(sprite, attr) {
        sprite.show();
        Ext.chart.series.Area.superclass.onAnimate.apply(this, arguments);
    },

    // @private
    onCreateLabel: function(storeItem, item, i, display) {
        var me = this,
            group = me.labelsGroup,
            config = me.label,
            bbox = me.bbox,
            endLabelStyle = Ext.apply({}, config, me.labelStyle.style);

        return me.getSurface().add(Ext.apply({
            'type': 'text',
            'text-anchor': 'middle',
            'group': group,
            'x': item.point[0],
            'y': bbox.y + bbox.height / 2
        }, endLabelStyle || {}));
    },

    // @private
    onPlaceLabel: function(label, storeItem, item, i, display, animate, index) {
        var me = this,
            chart = me.chart,
            resizing = chart.resizing,
            config = me.label,
            format = config.renderer,
            field = config.field,
            bbox = me.bbox,
            x = item.point[0],
            y = item.point[1],
            bb, width, height;
        
        label.setAttributes({
            text: format(storeItem.get(field[index])),
            hidden: true
        }, true);
        
        bb = label.getBBox();
        width = bb.width / 2;
        height = bb.height / 2;
        
        x = x - width < bbox.x? bbox.x + width : x;
        x = (x + width > bbox.x + bbox.width) ? (x - (x + width - bbox.x - bbox.width)) : x;
        y = y - height < bbox.y? bbox.y + height : y;
        y = (y + height > bbox.y + bbox.height) ? (y - (y + height - bbox.y - bbox.height)) : y;

        if (me.chart.animate && !me.chart.resizing) {
            label.show(true);
            me.onAnimate(label, {
                to: {
                    x: x,
                    y: y
                }
            });
        } else {
            label.setAttributes({
                x: x,
                y: y
            }, true);
            if (resizing) {
                me.animation.on('afteranimate', function() {
                    label.show(true);
                });
            } else {
                label.show(true);
            }
        }
    },

    // @private
    onPlaceCallout : function(callout, storeItem, item, i, display, animate, index) {
        var me = this,
            chart = me.chart,
            surface = me.getSurface(),
            resizing = chart.resizing,
            config = me.callouts,
            items = me.items,
            prev = (i == 0) ? false : items[i -1].point,
            next = (i == items.length -1) ? false : items[i +1].point,
            cur = item.point,
            dir, norm, normal, a, aprev, anext,
            bbox = callout.label.getBBox(),
            offsetFromViz = 30,
            offsetToSide = 10,
            offsetBox = 3,
            boxx, boxy, boxw, boxh,
            p, clipRect = me.clipRect,
            x, y;

        //get the right two points
        if (!prev) {
            prev = cur;
        }
        if (!next) {
            next = cur;
        }
        a = (next[1] - prev[1]) / (next[0] - prev[0]);
        aprev = (cur[1] - prev[1]) / (cur[0] - prev[0]);
        anext = (next[1] - cur[1]) / (next[0] - cur[0]);
        
        norm = Math.sqrt(1 + a * a);
        dir = [1 / norm, a / norm];
        normal = [-dir[1], dir[0]];
        
        //keep the label always on the outer part of the "elbow"
        if (aprev > 0 && anext < 0 && normal[1] < 0 || aprev < 0 && anext > 0 && normal[1] > 0) {
            normal[0] *= -1;
            normal[1] *= -1;
        } else if (Math.abs(aprev) < Math.abs(anext) && normal[0] < 0 || Math.abs(aprev) > Math.abs(anext) && normal[0] > 0) {
            normal[0] *= -1;
            normal[1] *= -1;
        }

        //position
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;
        
        //box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;
        
        //now check if we're out of bounds and invert the normal vector correspondingly
        //this may add new overlaps between labels (but labels won't be out of bounds).
        if (boxx < clipRect[0] || (boxx + boxw) > (clipRect[0] + clipRect[2])) {
            normal[0] *= -1;
        }
        if (boxy < clipRect[1] || (boxy + boxh) > (clipRect[1] + clipRect[3])) {
            normal[1] *= -1;
        }

        //update positions
        x = cur[0] + normal[0] * offsetFromViz;
        y = cur[1] + normal[1] * offsetFromViz;
        
        //update box position and dimensions
        boxx = x + (normal[0] > 0? 0 : -(bbox.width + 2 * offsetBox));
        boxy = y - bbox.height /2 - offsetBox;
        boxw = bbox.width + 2 * offsetBox;
        boxh = bbox.height + 2 * offsetBox;
        
        //set the line from the middle of the pie to the box.
        callout.lines.setAttributes({
            path: ["M", cur[0], cur[1], "L", x, y, "Z"]
        }, true);
        //set box position
        callout.box.setAttributes({
            x: boxx,
            y: boxy,
            width: boxw,
            height: boxh
        }, true);
        //set text position
        callout.label.setAttributes({
            x: x + (normal[0] > 0? offsetBox : -(bbox.width + offsetBox)),
            y: y
        }, true);
        for (p in callout) {
            callout[p].show(true);
        }
    },
    
    isItemInPoint: function(x, y, item, i) {
        var me = this,
            pointsUp = item.pointsUp,
            pointsDown = item.pointsDown,
            abs = Math.abs,
            dist = Infinity, p, pln, point;
        
        for (p = 0, pln = pointsUp.length; p < pln; p++) {
            point = [pointsUp[p][0], pointsUp[p][1]];
            if (dist > abs(x - point[0])) {
                dist = abs(x - point[0]);
            } else {
                point = pointsUp[p -1];
                if (y >= point[1] && (!pointsDown.length || y <= (pointsDown[p -1][1]))) {
                    item.storeIndex = p -1;
                    item.storeField = me.yField[i];
                    item.storeItem = me.chart.store.getAt(p -1);
                    item._points = pointsDown.length? [point, pointsDown[p -1]] : [point];
                    return true;
                } else {
                    break;
                }
            }
        }
        return false;
    },

    /**
     * Highlight this entire series.
     * @param {Object} item Info about the item; same format as returned by #getItemForPoint.
     */
    highlightSeries: function() {
        var area, to, fillColor;
        if (this._index !== undefined) {
            area = this.areas[this._index];
            if (area.__highlightAnim) {
                area.__highlightAnim.paused = true;
            }
            area.__highlighted = true;
            area.__prevOpacity = area.__prevOpacity || area.attr.opacity || 1;
            area.__prevFill = area.__prevFill || area.attr.fill;
            area.__prevLineWidth = area.__prevLineWidth || area.attr.lineWidth;
            fillColor = Ext.draw.Color.fromString(area.__prevFill);
            to = {
                lineWidth: (area.__prevLineWidth || 0) + 2
            };
            if (fillColor) {
                to.fill = fillColor.getLighter(0.2).toString();
            }
            else {
                to.opacity = Math.max(area.__prevOpacity - 0.3, 0);
            }
            if (this.chart.animate) {
                area.__highlightAnim = new Ext.fx.Anim(Ext.apply({
                    target: area,
                    to: to
                }, this.chart.animate));
            }
            else {
                area.setAttributes(to, true);
            }
        }
    },

    /**
     * UnHighlight this entire series.
     * @param {Object} item Info about the item; same format as returned by #getItemForPoint.
     */
    unHighlightSeries: function() {
        var area;
        if (this._index !== undefined) {
            area = this.areas[this._index];
            if (area.__highlightAnim) {
                area.__highlightAnim.paused = true;
            }
            if (area.__highlighted) {
                area.__highlighted = false;
                area.__highlightAnim = new Ext.fx.Anim({
                    target: area,
                    to: {
                        fill: area.__prevFill,
                        opacity: area.__prevOpacity,
                        lineWidth: area.__prevLineWidth
                    }
                });
            }
        }
    },

    /**
     * Highlight the specified item. If no item is provided the whole series will be highlighted.
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    highlightItem: function(item) {
        var me = this,
            highlightSprite = me.highlightSprite,
            points, path;
        if (!item) {
            this.highlightSeries();
            return;
        }
        points = item._points;
        path = points.length == 2? ['M', points[0][0], points[0][1], 'L', points[1][0], points[1][1]]
                : ['M', points[0][0], points[0][1], 'L', points[0][0], me.bbox.y + me.bbox.height];
        if (highlightSprite) {
            //make sure we apply the stylesheet styles.
            Ext.applyIf(this.highlightCfg, this.highlightStyle.style || {});
            
            highlightSprite.setAttributes(Ext.apply({
                path: path,
                hidden: false
            }, this.highlightCfg), true);
        }
        //added for canvas rendering.
        me.getSurface().renderFrame();
    },

    /**
     * un-highlights the specified item. If no item is provided it will un-highlight the entire series.
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    unHighlightItem: function(item) {
        if (!item) {
            this.unHighlightSeries();
        }

        if (this.highlightSprite) {
            this.highlightSprite.hide(true);
        }
    },

    // @private
    hideAll: function() {
        var me = this,
            index = me._index;
        if (!isNaN(index)) {
            me.__excludes[index] = true;
            me.areas[index].hide(true);
            me.chart.axes.each(function(axis) {
                axis.drawAxis();
            });
            me.drawSeries();
        }
    },

    // @private
    showAll: function() {
        var me = this,
            index = me._index;
        if (!isNaN(index)) {
            me.__excludes[index] = false;
            me.areas[index].show(true);
            me.chart.axes.each(function(axis) {
                axis.drawAxis();
            });
            me.drawSeries();
        }
    },

    /**
     * Returns the color of the series (to be displayed as color for the series legend item).
     * @param item {Object} Info about the item; same format as returned by #getItemForPoint
     */
    getLegendColor: function(index) {
        var me = this,
            colorArrayStyle = me.colorArrayStyle;
        return me.getColorFromStyle(colorArrayStyle[index % colorArrayStyle.length]);
    }
});

Ext.ns('Ext.chart.interactions');

/**
 * @class Ext.chart.interactions.Manager
 * @singleton
 *
 * A singleton manager instance for chart interactions. Interaction classes register
 * themselves by name with this manager.
 */
Ext.chart.interactions.Manager = new Ext.AbstractManager();
/**
 * @class Ext.chart.interactions.Abstract
 * @extends Ext.util.Observable
 *
 * Defines a common abstract parent class for all interactions.
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.Abstract = Ext.extend(Ext.util.Observable, {

    /**
     * @cfg {String} gesture
     * Specifies which gesture type should be used for starting the interaction.
     * Defaults to `tap`.
     */
    gesture: 'tap',

    constructor: function(config) {
        var me = this;
        Ext.chart.interactions.Abstract.superclass.constructor.call(me, config);
        me.ownerCt = me.chart;
    },

    /**
     * @protected
     * A method to be implemented by subclasses where all event attachment should occur.
     */
    initEvents: function() {
        var me = this;
        
        //check whether we're using drag events then initialize them in the surface.
        if (me.gesture && me.gesture == 'drag' || me.panGesture && me.panGesture == 'drag') {
            me.chart.getSurface('events').initializeDragEvents();
        }
        
        me.addChartListener(me.gesture, me.onGesture, me);
    },

    /**
     * @protected
     * Placeholder method.
     */
    onGesture: Ext.emptyFn,

    /**
     * @protected Find and return a single series item corresponding to the given event,
     * or null if no matching item is found.
     * @param {Event} e
     * @return {Object} the item object or null if none found.
     */
    getItemForEvent: function(e) {
        var me = this,
            chart = me.chart,
            chartXY = chart.getEventXY(e);
        return chart.getItemForPoint(chartXY[0], chartXY[1]);
    },

    /**
     * @protected Find and return all series items corresponding to the given event.
     * @param {Event} e
     * @return {Array} array of matching item objects
     */
    getItemsForEvent: function(e) {
        var me = this,
            chart = me.chart,
            chartXY = chart.getEventXY(e);
        return chart.getItemsForPoint(chartXY[0], chartXY[1]);
    },

    /**
     * @protected Add an event listener to this interaction's chart. All ineteraction event listeners
     * should be attached using this method, since it adds logic for honoring event locks.
     * @param name
     * @param fn
     * @param scope
     * @param opts
     */
    addChartListener: function(name, fn, scope, opts) {
        var me = this,
            locks = me.getLocks();
        me.chart.on(
            name,
            // wrap the handler so it does not fire if the event is locked by another interaction
            function() {
                if (!(name in locks) || locks[name] === me) {
                    fn.apply(this, arguments);
                }
            },
            scope,
            opts
        );
    },

    lockEvents: function() {
        var me = this,
            locks = me.getLocks(),
            args = arguments,
            i = args.length;
        while (i--) {
            locks[args[i]] = me;
        }
    },

    unlockEvents: function() {
        var locks = this.getLocks(),
            args = arguments,
            i = args.length;
        while (i--) {
            delete locks[args[i]];
        }
    },

    getLocks: function() {
        var chart = this.chart;
        return chart.lockedEvents || (chart.lockedEvents = {});
    },

    isMultiTouch: function() {
        return !(Ext.is.MultiTouch === false || (Ext.is.Android && !Ext.is.hasOwnProperty('MultiTouch')) || Ext.is.Desktop);
    },

    initializeDefaults: Ext.emptyFn,

    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    //filled by the constructor.
    ownerCt: null,

    getItemId: function() {
        return this.id || (this.id = Ext.id());
    },

    initCls: function() {
        return (this.cls || '').split(' ');
    },

    isXType: function(xtype) {
        return xtype === 'interaction';
    },

    getRefItems: function(deep) {
        return [];
    }
});

/**
 * @class Ext.chart.interactions.DelayedSync
 *
 * This is a mixin for chart interactions which gives them basic support for synchronizing
 * the chart to the user's interaction after a configurable {@link #syncDelay delay}. This
 * is useful for example in interactions which perform fast CSS3 transformation during the
 * interaction's gesture, but needs to perform a full synchronization to that transformation
 * for full quality after a delay.
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.DelayedSync = Ext.extend(Object, {

    /**
     * @cfg {Number} syncDelay
     * Specifies a timeout in milliseconds between when the user finishes an interaction
     * gesture and when the chart are synced and redrawn to match.
     * Defaults to 500.
     */
    syncDelay: 500,

    /**
     * @cfg {String} syncWaitText
     * The text to be displayed while the chart is redrawing itself after the interaction sync.
     * Defaults to 'Rendering...'.
     */
    syncWaitText: 'Rendering...',

    constructor: function() {
        var me = this,
            DelayedTask = Ext.util.DelayedTask;

        me.startSyncTask = new DelayedTask(me.startSync, me);
        me.doSyncTask = new DelayedTask(me.doSync, me);
        me.unlockInteractionTask = new DelayedTask(me.unlockInteraction, me);
    },

    sync: Ext.emptyFn,

    needsSync: function() {
        return true;
    },

    startSync: function() {
        var me = this;
        if (me.needsSync()) {
            me.lockInteraction();
            // Must delay the actual rerender to allow the lock/mask to take effect
            me.doSyncTask.delay(1);
        }
    },

    doSync: function() {
        var me = this;

        // Invoke the class's sync logic
        if (me.needsSync()) {
            me.sync();
        }

        // Must delay the unlock otherwise the browser will queue the events during
        // render and apply them immediately afterward
        me.unlockInteractionTask.delay(1);
    },

    cancelSync: function() {
        var me = this;
        me.startSyncTask.cancel();
        me.doSyncTask.cancel();
        me.unlockInteractionTask.cancel();
    },

    delaySync: function() {
        var me = this;
        me.cancelSync();
        me.startSyncTask.delay(me.syncDelay);
    },

    lockInteraction: function() {
        var me = this,
            chart = me.chart,
            chartEl = chart.el,
            stopEvent = me.stopEvent;

        me.unlockInteraction();
        chartEl.on({
            touchstart: stopEvent,
            touchmove: stopEvent,
            touchend: stopEvent,
            capture: true
        });

        // chartEl.mask(me.syncWaitText, Ext.baseCSSPrefix + 'chart-wait', false);
        // Ext.repaint(); //mask doesn't get sized properly otherwise
    },

    unlockInteraction: function() {
        var me = this,
            chart = me.chart,
            chartEl = chart.el,
            stopEvent = me.stopEvent;

        chartEl.un({
            touchstart: stopEvent,
            touchmove: stopEvent,
            touchend: stopEvent,
            capture: true
        });

        // chartEl.unmask();
    },

    stopEvent: function(e) {
        e.stopEvent();
    }

});

/**
 * @class Ext.chart.interactions.PanZoom
 * @extends Ext.chart.interactions.Abstract
 *
 * The PanZoom interaction allows the user to navigate the data for one or more chart
 * axes by panning and/or zooming. Navigation can be limited to particular axes. Zooming is
 * performed by pinching on the chart or axis area; panning is performed by single-touch dragging.
 *
 * For devices which do not support multiple-touch events, zooming can not be done via pinch
 * gestures; in this case the interaction will allow the user to perform both zooming and
 * panning using the same single-touch drag gesture. Tapping the chart will switch between
 * the two modes, {@link #modeIndicatorDuration briefly displaying a graphical indicator}
 * showing whether it is in zoom or pan mode.
 *
 * You can attach this interaction to a chart by including an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `panzoom` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...some series options... ],
 *         interactions: [{
 *             type: 'panzoom',
 *             axes: {
 *                 left: {
 *                     maxZoom: 5,
 *                     startZoom: 2
 *                 },
 *                 bottom: {
 *                     maxZoom: 2
 *                 }
 *             }
 *         }]
 *     });
 *
 * The configuration object for the `panzoom` interaction type should specify which axes
 * will be made navigable via the `axes` config. See the {@link #axes} config documentation
 * for details on the allowed formats. If the `axes` config is not specified, it will default
 * to making all axes navigable with the default axis options.
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.PanZoom = Ext.extend(Ext.chart.interactions.Abstract, {

    /**
     * @cfg {Object/Array} axes
     * Specifies which axes should be made navigable. The config value can take the following formats:
     *
     * - An Object whose keys correspond to the {@link Ext.chart.axis.Axis#position position} of each
     *   axis that should be made navigable. Each key's value can either be an Object with further
     *   configuration options for each axis or simply `true` for a default set of options.
     *       {
     *           type: 'panzoom',
     *           axes: {
     *               left: {
     *                   maxZoom: 5,
     *                   allowPan: false
     *               },
     *               bottom: true
     *           }
     *       }
     *
     *   If using the full Object form, the following options can be specified for each axis:
     *
     *   - minZoom (Number) A minimum zoom level for the axis. Defaults to `1` which is its natural size.
     *   - maxZoom (Number) A maximum zoom level for the axis. Defaults to `10`.
     *   - startZoom (Number) A starting zoom level for the axis. Defaults to `1`.
     *   - allowZoom (Boolean) Whether zooming is allowed for the axis. Defaults to `true`.
     *   - allowPan (Boolean) Whether panning is allowed for the axis. Defaults to `true`.
     *   - startPan (Boolean) A starting panning offset for the axis. Defaults to `0`.
     *
     * - An Array of strings, each one corresponding to the {@link Ext.chart.axis.Axis#position position}
     *   of an axis that should be made navigable. The default options will be used for each named axis.
     *
     *       {
     *           type: 'panzoom',
     *           axes: ['left', 'bottom']
     *       }
     *
     * If the `axes` config is not specified, it will default to making all axes navigable with the
     * default axis options.
     */
    axes: {
        top: {},
        right: {},
        bottom: {},
        left: {}
    },

    /**
     * @cfg {Boolean} showOverflowArrows
     * If `true`, arrows will be conditionally shown at either end of each axis to indicate that the
     * axis is overflowing and can therefore be panned in that direction. Set this to `false` to
     * prevent the arrows from being displayed. Defaults to `true`.
     */
    showOverflowArrows: true,

    /**
     * @cfg {Object} overflowArrowOptions
     * A set of optional overrides for the overflow arrow sprites' options. Only relevant when
     * {@link #showOverflowArrows} is `true`.
     */

    gesture: 'pinch',
    panGesture: 'drag',

    constructor: function(config) {
        var me = this,
            interactionsNS = Ext.chart.interactions,
            zoomModeCls = Ext.baseCSSPrefix + 'zooming',
            axesConfig;

        interactionsNS.PanZoom.superclass.constructor.call(me, config);
        interactionsNS.DelayedSync.prototype.constructor.call(me, config);

        if (me.showOverflowArrows) {
            me.chart.on('redraw', me.updateAllOverflowArrows, me);
        }

        // Normalize the `axes` config
        axesConfig = me.axes;
        if (Ext.isArray(axesConfig)) {
            // array of axis names - translate to full object form
            me.axes = {};
            Ext.each(axesConfig, function(axis) {
                me.axes[axis] = {};
            });
        } else if (Ext.isObject(axesConfig)) {
            Ext.iterate(axesConfig, function(key, val) {
                // axis name with `true` value -> translate to object
                if (val === true) {
                    axesConfig[key] = {};
                }
            });
        }
        else {
            Ext.Error.raise("Invalid value for panzoom interaction 'axes' config: '" + axesConfig + "'");
        }
        
        // Add pan/zoom toggle button to toolbar if needed
        if (!me.isMultiTouch()) {
            me.zoomOnPanGesture = true; //default to zooming
            me.modeToggleButton = me.chart.getToolbar().add({
                cls: Ext.baseCSSPrefix + 'panzoom-toggle ' + zoomModeCls,
                iconCls: Ext.baseCSSPrefix + 'panzoom-toggle-icon',
                iconMask: true,
                handler: function() {
                    var button = this,
                        zoom = me.zoomOnPanGesture = !me.zoomOnPanGesture;
                    if (zoom) {
                        button.addCls(zoomModeCls);
                    } else {
                        button.removeCls(zoomModeCls);
                    }
                }
            });
        }
    },

    initEvents: function() {
        var me = this;
        Ext.chart.interactions.PanZoom.superclass.initEvents.call(me, arguments);
        me.addChartListener(me.gesture + 'start', me.onGestureStart, me);
        me.addChartListener(me.gesture + 'end', me.onGestureEnd, me);
        me.addChartListener(me.panGesture + 'start', me.onPanGestureStart, me);
        me.addChartListener(me.panGesture, me.onPanGesture, me);
        me.addChartListener(me.panGesture + 'end', me.onPanGestureEnd, me);
    },

    initializeDefaults: function(opt) {
        var me = this;

        if (!opt || opt.type == 'beforerender') {
            me.onGestureStart();
            me.onPanGestureStart();

            me.chart.axes.each(function(axis) {
                if (!me.axes[axis.position]) {
                    return;
                }

                var config = me.axes[axis.position],
                    startPan = config.startPan || 0,
                    startZoom = config.startZoom || 1;

                if (startPan != 0 || startZoom != 1) {
                    me.transformAxisBy(axis, startPan, startPan, startZoom, startZoom);
                }
            });
        }

        if (!opt || opt.type == 'afterrender') {
            me.onGestureEnd();
            me.onPanGestureEnd();
        }
    },

    getInteractiveAxes: function() {
        var me = this,
            axisConfigs = me.axes;
        return me.chart.axes.filterBy(function(axis) {
            return !!axisConfigs[axis.position];
        });
    },

    isEventOnAxis: function(e, axis) {
        // TODO right now this uses the current event position but really we want to only
        // use the gesture's start event. Pinch does not give that to us though.
        var util = Ext.util;
        return !util.Region.getRegion(axis.getSurface().el).isOutOfBound(util.Point.fromEvent(e));
    },

    sync: function() {
        var me = this,
            chart = me.chart,
            anim = chart.animate,
            axes = me.getInteractiveAxes();

        chart.animate = false;
        chart.endsLocked = true;

        axes.each(function(axis) {
            if (axis.hasFastTransform()) {
                axis.syncToFastTransform();

                // redraw the axis
                axis.drawAxis();
                axis.renderFrame();
            }
        });

        // sync all series bound to this axis
        me.getSeriesForAxes(axes).each(function(series) {
            if (series.hasFastTransform()) {
                series.syncToFastTransform();

                // redraw the series
                series.drawSeries();
                series.getSurface().renderFrame();
            }
        });

        chart.endsLocked = false;
        chart.animate = anim;
    },

    needsSync: function() {
        return !!this.getInteractiveAxes().findBy(function(axis) {
            return axis.hasFastTransform();
        });
    },

    transformAxisBy: function(axis, panX, panY, zoomX, zoomY) {
        var me = this,
            config = me.axes[axis.position],
            minZoom = config.minZoom || 1,
            maxZoom = config.maxZoom || 4,
            isNumber = Ext.isNumber,
            length = axis.length,
            isSide = axis.isSide(),
            pan = isSide ? panY : panX,
        zoom = isSide ? zoomY : zoomX;

        function doTransform(target) {
            var matrix = target.getTransformMatrix().clone(),
                currentZoom, inverse, inset;

            if (pan !== 0) {
                matrix.translate(isSide ? 0 : pan, isSide ? pan : 0);
            }

            if (zoom !== 1) {
                // constrain to minZoom/maxZoom zoom
                currentZoom = matrix.get(+isSide, +isSide);
                if (isNumber(minZoom)) {
                    zoom = Math.max(zoom, minZoom / currentZoom);
                }
                if (isNumber(maxZoom)) {
                    zoom = Math.min(zoom, maxZoom / currentZoom);
                }

                // use the matrix's inverse to find the scale origin that lines up with the middle of the axis
                inverse = matrix.invert();
                matrix.scale(
                isSide ? 1 : zoom, isSide ? zoom : 1, inverse.x(length / 2, 0), inverse.y(0, length / 2));
            }

            // constrain pan
            inset = matrix[isSide ? 'y' : 'x'](0, 0);
            if (inset > 0) {
                matrix.translate(isSide ? 0 : -inset, isSide ? -inset : 0);
            }
            inset = length - matrix[isSide ? 'y' : 'x'](length, length);
            if (inset > 0) {
                matrix.translate(isSide ? 0 : inset, isSide ? inset : 0);
            }

            target.setTransformMatrixFast(matrix);
        }

        doTransform(axis);
        axis.getBoundSeries().each(doTransform);

        if (me.showOverflowArrows) {
            me.updateAxisOverflowArrows(axis);
        }
    },

    getPannableAxes: function(e) {
        var me = this,
            axisConfigs = me.axes,
            config;
        return me.chart.axes.filterBy(function(axis) {
            config = axisConfigs[axis.position];
            return config && config.allowPan !== false && me.isEventOnAxis(e, axis);
        });
    },

    panBy: function(axes, x, y) {
        axes.each(function(axis) {
            this.transformAxisBy(axis, x, y, 1, 1);
        },
        this);
    },

    onPanGestureStart: function(e) {
        if (!e || !e.touches || e.touches.length < 2) { //Limit drags to single touch
            var me = this;
            me.cancelSync();
            if (me.zoomOnPanGesture) {
                me.onGestureStart(e);
            }
        }
    },

    onPanGesture: function(e) {
        if (!e.touches || e.touches.length < 2) { //Limit drags to single touch
            var me = this;
            if (me.zoomOnPanGesture) {
                me.zoomBy(
                me.getZoomableAxes(e), (e.previousX + e.previousDeltaX) / e.previousX, e.previousY / (e.previousY + e.previousDeltaY));
            } else {
                me.panBy(me.getPannableAxes(e), e.previousDeltaX, e.previousDeltaY);
            }
        }
    },

    onPanGestureEnd: function(e) {
        var me = this;
        if (me.zoomOnPanGesture) {
            me.onGestureEnd(e);
        } else {
            me.delaySync();
        }
    },

    getSeriesForAxes: function(axes) {
        var series = new Ext.util.MixedCollection(false, function(s) {
            return s.seriesId;
        });
        axes.each(function(axis) {
            series.addAll(axis.getBoundSeries().items);
        });
        return series;
    },

    getZoomableAxes: function(e) {
        var me = this,
            axisConfigs = me.axes,
            config;
        return me.chart.axes.filterBy(function(axis) {
            config = axisConfigs[axis.position];
            return config && config.allowZoom !== false && (!e || me.isEventOnAxis(e, axis));
        });
    },

    zoomBy: function(axes, zoomX, zoomY) {
        axes.each(function(axis) {
            this.transformAxisBy(axis, 0, 0, zoomX, zoomY);
        },
        this);
    },

    onGestureStart: function(e) {
        var me = this;
        me.cancelSync();

        // Hide axis labels while zooming
        me.getZoomableAxes(e).each(function(axis) {
            axis.hideLabels();
            axis.getLabelSurface().renderFrame();
        });
    },

    onGesture: function(e) {
        var me = this,
            abs = Math.abs,
            xDistance = abs(e.secondPageX - e.firstPageX),
            yDistance = abs(e.secondPageY - e.firstPageY),
            lastDistances = me.lastZoomDistances || [xDistance, yDistance],
            zoomX = xDistance < 30 ? 1 : xDistance / (lastDistances[0] || xDistance),
        zoomY = yDistance < 30 ? 1 : yDistance / (lastDistances[1] || yDistance);
        me.zoomBy(me.getZoomableAxes(e), zoomX, zoomY);
        me.lastZoomDistances = [xDistance, yDistance];
    },

    onGestureEnd: function(e) {
        var me = this;

        // If there is no transform, unhide the axis tick labels
        me.getZoomableAxes(e).each(function(axis) {
            if (!axis.hasFastTransform()) {
                axis.drawLabel();
                axis.getLabelSurface().renderFrame();
            }
        });

        me.delaySync();
        delete me.lastZoomDistances;
    },

    getOverflowArrow: function(axis, direction, opts) {
        var me = this,
            axisPos = axis.position,
            allIndicators = me.overflowIndicators || (me.overflowIndicators = {}),
            axisIndicators = allIndicators[axisPos] || (allIndicators[axisPos] = {});
        return axisIndicators[direction] || (
        axisIndicators[direction] = Ext.chart.Shape.arrow(me.chart.getEventsSurface(), opts));
    },

    updateAxisOverflowArrows: function(axis) {
        var me = this,
            isSide = axis.isSide(),
            axisPos = axis.position,
            allowPan = me.axes[axisPos].allowPan !== false,
            length = axis.length,
            chart = me.chart,
            bbox = chart.chartBBox,
            matrix = axis.getTransformMatrix(),
            spriteOpts = Ext.apply({
                hidden: true,
                radius: 5,
                opacity: 0.3,
                fill: axis.style.stroke
            }, me.overflowArrowOptions),
            math = Math,
            ceil = math.ceil,
            floor = math.floor,
            upSprite = me.getOverflowArrow(axis, 'up', spriteOpts),
            downSprite = me.getOverflowArrow(axis, 'down', spriteOpts),
            path;

        if (allowPan && (isSide ? ceil(matrix.y(0, 0)) < 0 : floor(matrix.x(length, 0)) > length)) {
            // Top
            if (isSide) {
                path = ['M', bbox.x, bbox.y, 'l', bbox.width / 2, 0, 0, 5, -10, 10, 20, 0, -10, -10, 0, -5, bbox.width / 2, 0, 0, 20, -bbox.width, 0, 'z'].join(',');
            }
            // Right
            else {
                path = ['M', bbox.x + bbox.width, bbox.y, 'l', 0, bbox.height / 2, -5, 0, -10, -10, 0, 20, 10, -10, 5, 0, 0, bbox.height / 2, -20, 0, 0, -bbox.height, 'z'].join(',');
            }
            upSprite.setAttributes({
                hidden: false,
                path: path
            });
        } else {
            upSprite.hide();
        }

        if (allowPan && (isSide ? floor(matrix.y(0, length)) > length : ceil(matrix.x(0, 0)) < 0)) {
            // Bottom
            if (isSide) {
                path = ['M', bbox.x, bbox.y + bbox.height, 'l', bbox.width / 2, 0, 0, -5, -10, -10, 20, 0, -10, 10, 0, 5, bbox.width / 2, 0, 0, -20, -bbox.width, 0, 'z'].join(',');
            }
            // Left
            else {
                path = ['M', bbox.x, bbox.y, 'l', 0, bbox.height/ 2, 5, 0, 10, -10, 0, 20, -10, -10, -5, 0, 0, bbox.height / 2, 20, 0, 0, -bbox.height, 'z'].join(',');
            }
            downSprite.setAttributes({
                hidden: false,
                path: path
            });
        } else {
            downSprite.hide();
        }

        if (upSprite.dirtyTransform || upSprite.dirtyHidden || downSprite.dirtyTransform || downSprite.dirtyHidden) {
            me.chart.getEventsSurface().renderFrame();
        }
    },

    updateAllOverflowArrows: function() {
        var me = this;
        me.getInteractiveAxes().each(me.updateAxisOverflowArrows, me);
    }
});

Ext.applyIf(Ext.chart.interactions.PanZoom.prototype, Ext.chart.interactions.DelayedSync.prototype);

Ext.chart.interactions.Manager.registerType('panzoom', Ext.chart.interactions.PanZoom);

/**
 * @class Ext.chart.interactions.PieGrouping
 * @extends Ext.chart.interactions.Abstract
 * 
 * The PieGrouping interaction allows the user to select a group of consecutive slices
 * in a {@link Ext.chart.series.Pie pie series} to get additional information about the
 * group. It provides an interactive user interface with handles that can be dragged
 * around the pie to add/remove slices in the selection group.
 *
 * You can attach this interaction to a chart by including an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `piegrouping` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         series: [ ...pie series options... ],
 *         interactions: [{
 *             type: 'piegrouping'
 *         }]
 *     });
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.PieGrouping = Ext.extend(Ext.chart.interactions.Abstract, {

    type: 'piegrouping',

    /**
     * @cfg {String} gesture
     * Specifies the gesture that, when performed on a slice in the pie series, initializes the
     * selection UI on that slice. Defaults to `tap`.
     */
    gesture: 'tap',

    // TODO does it make sense to make this configurable? Are any other gestures relevant for this?
    resizeGesture: 'drag',

    /**
     * @cfg {Number} outset
     * Specifies how far beyond the pie circle radius the selection overlay extends.
     * Defaults to `6`.
     */
    outset: 6,

    /**
     * @cfg {Boolean} snapWhileDragging
     * If set to `true`, the selection overlay will snap to the nearest pie slice continuously
     * while the user is dragging the handles, firing the {@link #selectionchange} event each
     * time it snaps. Otherwise, the selection will only snap to the nearest slice when the user
     * releases the handle drag, firing the event once. Defaults to `false`.
     */

    /**
     * @cfg {Function} onSelectionChange
     * A handler function that can be implemented to handle selection changes, as an alternative
     * to adding a listener for the {@link #selectionchange} event. The function will be passed
     * the same parameters as are passed to selectionchange listeners.
     */
    onSelectionChange: Ext.emptyFn,


    constructor: function(config) {
        this.addEvents(
            /**
             * @event selectionchange
             * Fired when the set of selected pie slice items changes.
             * @param {Ext.chart.interactions.PieGrouping} interaction
             * @param {Array} selectedItems
             */
            'selectionchange'
        );

        Ext.chart.interactions.PieGrouping.superclass.constructor.call(this, config);

        this.handleStyle = new Ext.chart.interactions.PieGrouping.HandleStyle();
        this.sliceStyle = new Ext.chart.interactions.PieGrouping.SliceStyle();
    },

    initEvents: function() {
        Ext.chart.interactions.PieGrouping.superclass.initEvents.call(this, arguments);
        var me = this,
            resizeGesture = me.resizeGesture;
        me.addChartListener(resizeGesture + 'start', me.onResizeStart, me);
        me.addChartListener(resizeGesture, me.onResize, me);
        me.addChartListener(resizeGesture + 'end', me.onResizeEnd, me);
    },

    onGesture: function(e) {
        var me = this,
            outset = me.outset,
            item = me.getItemForEvent(e),
            handleStyle = me.handleStyle.style,
            sliceStyle = me.sliceStyle.style,
            surface, startAngle, endAngle, handleLine;

        // If already active, allow tap outside the pie to cancel selection, or tapping an item
        // not within the selection to start a new selection.
        if (me.active && (!item || me.getSelectedItems().indexOf(item) < 0)) {
            me.cancel();
        }

        // Start selection at the tapped item's boundaries
        if (!me.active && item) {
            surface = me.getSeries().getOverlaySurface();
            startAngle = item.startAngle;
            endAngle = item.endAngle;

            me.slice = {
                startAngle: startAngle,
                endAngle: endAngle,
                sprite: surface.add(Ext.applyIf({
                    type: 'path'
                }, sliceStyle))
            };

            handleLine = 'M' + Math.max(item.startRho - outset, 0) + ',0L' + (item.endRho + outset) + ',0';
            me.startHandle = {
                angle: startAngle,
                sprite: surface.add(Ext.applyIf({
                    type: 'path',
                    path: handleLine + 'l5,-8l-10,0l5,8',
                    fill: handleStyle.stroke
                }, handleStyle))
            };
            me.endHandle = {
                angle: endAngle,
                sprite: surface.add(Ext.applyIf({
                    type: 'path',
                    path: handleLine + 'l5,8l-10,0l5,-8',
                    fill: handleStyle.stroke
                }, handleStyle))
            };

            me.mon(me.getSeries(), 'draw', me.onSeriesDraw, me);

            me.active = true;
            me.updateSprites();
            me.fireSelectionChange();
        }
    },

    onResizeStart: function(e) {
        var me = this,
            abs = Math.abs,
            normalizeAngle = me.normalizeAngle,
            startHandle = me.startHandle,
            endHandle = me.endHandle,
            resizeGesture = me.resizeGesture,
            activeHandle, angle;
        if (me.active) {
            angle = normalizeAngle(me.getAngleForEvent(e));
            if (abs(angle - normalizeAngle(startHandle.angle)) < 10) {
                activeHandle = startHandle;
            }
            else if (abs(angle - normalizeAngle(endHandle.angle)) < 10) {
                activeHandle = endHandle;
            }

            if (activeHandle) {
                me.lockEvents(resizeGesture + 'start', resizeGesture, resizeGesture + 'end');
            }
            me.activeHandle = activeHandle;
        }
    },

    onResize: function(e) {
        var me = this,
            handle = me.activeHandle,
            snapWhileDragging = me.snapWhileDragging,
            slice = me.slice,
            sliceStartAngle, sliceEndAngle,
            sliceChanged = false,
            handleAngle;
        if (handle) {
            sliceStartAngle = slice.startAngle;
            sliceEndAngle = slice.endAngle;
            handleAngle = me.getAngleForEvent(e);
            handle.angle = handleAngle;

            if (handle === me.startHandle) {
                sliceStartAngle = snapWhileDragging ? me.snapToItemAngles(handleAngle, 0)[0] : handleAngle;
                while (sliceStartAngle > sliceEndAngle) {
                    sliceStartAngle -= 360;
                }
                while (sliceStartAngle <= sliceEndAngle) {
                    sliceStartAngle += 360;
                }
                if (slice.startAngle !== sliceStartAngle || !snapWhileDragging) {
                    sliceChanged = true;
                }
                slice.startAngle = sliceStartAngle;
            } else {
                sliceEndAngle = snapWhileDragging ? me.snapToItemAngles(0, handleAngle)[1] : handleAngle;
                while (sliceStartAngle > sliceEndAngle) {
                    sliceEndAngle += 360;
                }
                while (sliceStartAngle <= sliceEndAngle) {
                    sliceEndAngle -= 360;
                }
                if (slice.endAngle !== sliceEndAngle || !snapWhileDragging) {
                    sliceChanged = true;
                }
                slice.endAngle = sliceEndAngle;
            }

            me.updateSprites();
            if (sliceChanged && snapWhileDragging) {
                me.fireSelectionChange();
            }
        }
    },

    onResizeEnd: function(e) {
        var me = this,
            handle = me.activeHandle,
            startHandle = me.startHandle,
            endHandle = me.endHandle,
            slice = me.slice,
            closestAngle = me.closestAngle,
            resizeGesture = me.resizeGesture,
            snappedAngles, sliceStartAngle, sliceEndAngle;
        
        if (handle) {
            snappedAngles = me.snapToItemAngles(startHandle.angle, endHandle.angle);
            sliceStartAngle = slice.startAngle;
            sliceEndAngle = slice.endAngle;

            if (handle === startHandle) {
                startHandle.angle = closestAngle(snappedAngles[0], startHandle.angle, 1);
                sliceStartAngle = snappedAngles[0];
                while (sliceStartAngle > sliceEndAngle) {
                    sliceStartAngle -= 360;
                }
                while (sliceStartAngle <= sliceEndAngle) {
                    sliceStartAngle += 360;
                }
                slice.startAngle = sliceStartAngle;
            } else {
                endHandle.angle = closestAngle(snappedAngles[1], endHandle.angle, 0);
                sliceEndAngle = snappedAngles[1];
                while (sliceStartAngle > sliceEndAngle) {
                    sliceEndAngle += 360;
                }
                while (sliceStartAngle <= sliceEndAngle) {
                    sliceEndAngle -= 360;
                }
                slice.endAngle = sliceEndAngle;
            }

            me.updateSprites(true);
            if (!me.snapWhileDragging) {
                me.fireSelectionChange();
            }
            delete me.activeHandle;

            me.unlockEvents(resizeGesture + 'start', resizeGesture, resizeGesture + 'end');
        }
    },

    /**
     * @private tries to sync the selection overlay to the series when it is redrawn
     */
    onSeriesDraw: function() {
        var me = this,
            startHandle = me.startHandle,
            endHandle = me.endHandle,
            slice = me.slice,
            lastSelection = me.lastSelection,
            oldStartItem, oldEndItem,
            newStartItem, newEndItem;
        if (me.active && lastSelection) {
            oldStartItem = lastSelection[0];
            oldEndItem = lastSelection[lastSelection.length - 1];

            newStartItem = me.findItemByRecord(oldStartItem.storeItem);
            newEndItem = me.findItemByRecord(oldEndItem.storeItem);

            if (!newStartItem || !newEndItem) {
                me.cancel();
            } else {
                startHandle.angle = slice.startAngle = newStartItem.startAngle;
                endHandle.angle = slice.endAngle = newEndItem.endAngle;
                while (slice.startAngle > slice.endAngle) {
                    slice.startAngle -= 360;
                }
                while (slice.startAngle <= slice.endAngle) {
                    slice.startAngle += 360;
                }
                me.updateSprites();
                me.fireSelectionChange();
            }
        }
    },

    findItemByRecord: function(record) {
        var items = this.getSeries().items,
            i = items.length;
        while (i--) {
            if (items[i] && items[i].storeItem === record) {
                return items[i];
            }
        }
    },

    normalizeAngle: function(angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    },

    fireSelectionChange: function() {
        var me = this,
            items = me.getSelectedItems();
        me.onSelectionChange(me, items);
        me.fireEvent('selectionchange', me, items);
        me.lastSelection = items;
    },

    renderFrame: function() {
        this.getSeries().getOverlaySurface().renderFrame();
    },

    updateSprites: function(animate) {
        var me = this,
            series = me.getSeries(),
            startHandle = me.startHandle,
            endHandle = me.endHandle,
            angle1 = startHandle.angle,
            angle2 = endHandle.angle,
            centerX = series.centerX,
            centerY = series.centerY,
            slice = me.slice,
            outset = me.outset,
            item1, item2, attrs;

        if (me.active) {
            // Start handle
            attrs = {
                rotate: {
                    degrees: angle1,
                    x: 0,
                    y: 0
                },
                translate: {
                    x: centerX,
                    y: centerY
                }
            };
            if (animate) {
                startHandle.sprite.stopAnimation();
                startHandle.sprite.animate({to: attrs});
            } else {
                startHandle.sprite.setAttributes(attrs, true);
            }

            // End handle
            attrs = {
                rotate: {
                    degrees: angle2,
                    x: 0,
                    y: 0
                },
                translate: {
                    x: centerX,
                    y: centerY
                }
            };
            if (animate) {
                endHandle.sprite.stopAnimation();
                endHandle.sprite.animate({to: attrs});
            } else {
                endHandle.sprite.setAttributes(attrs, true);
            }

            // Slice
            item1 = series.getItemForAngle(angle1 - 1e-9);
            item2 = series.getItemForAngle(angle2 + 1e-9);
            attrs = {
                segment: {
                    startAngle: slice.startAngle,
                    endAngle: slice.endAngle,
                    startRho: Math.max(Math.min(item1.startRho, item2.startRho) - outset, 0),
                    endRho: Math.min(item1.endRho, item2.endRho) + outset
                }
            };
            if (animate) {
                slice.sprite.stopAnimation();
                slice.sprite.animate({to: attrs});
            } else {
                slice.sprite.setAttributes(attrs, true);
            }

            if (!animate) {
                me.renderFrame();
            }
        }
    },

    snapToItemAngles: function(startAngle, endAngle) {
        var me = this,
            series = me.getSeries(),
            item1 = series.getItemForAngle(startAngle - 1e-9),
            item2 = series.getItemForAngle(endAngle + 1e-9);
        return [item1.startAngle, item2.endAngle];
    },

    closestAngle: function(target, current, dir) {
        if (dir) {
            while (target > current) {
                target -= 360;
            }
            while (target < current) {
                target += 360;
            }
        } else {
            while (target < current) {
                target += 360;
            }
            while (target > current) {
                target -= 360;
            }
        }
        return target;
    },

    cancel: function() {
        var me = this,
            series;
        if (me.active) {
            series = me.getSeries();
            Ext.destroy(me.startHandle.sprite, me.endHandle.sprite, me.slice.sprite);
            me.active = false;
            me.startHandle = me.endHandle = me.slice = null;
            me.fireSelectionChange();
            me.renderFrame();
            me.mun(series, 'draw', me.onSeriesDraw, me);
        }
    },

    getSelectedItems: function() {
        var me = this,
            slice = me.slice,
            selectedItems,
            series = me.getSeries(),
            allItems, item1Index, item2Index;

        if (me.active) {
            allItems = me.getSeries().items;
            item1Index = allItems.indexOf(series.getItemForAngle(slice.startAngle - 1e-9));
            item2Index = allItems.indexOf(series.getItemForAngle(slice.endAngle + 1e-9));

            if (item1Index <= item2Index) {
                selectedItems = allItems.slice(item1Index, item2Index + 1);
            } else {
                selectedItems = allItems.slice(item1Index).concat(allItems.slice(0, item2Index + 1));
            }

            // prune undefined items
            selectedItems = selectedItems.filter(function(item, i, arr) {
                return i in arr;
            });
        }
        return selectedItems || [];
    },

    getAngleForEvent: function(e) {
        var me = this,
            series = me.getSeries(),
            seriesXY = series.getSurface().el.getXY();
        return Ext.draw.Draw.degrees(
            Math.atan2(e.pageY - series.centerY - seriesXY[1], e.pageX - series.centerX - seriesXY[0])
        );
    },

    getSeries: function() {
        var me = this,
            series = me._series;
        if (!series) {
            series = me._series = me.chart.series.findBy(function(series) {
                return series.type === 'pie';
            });
            series.getOverlaySurface().customAttributes.segment = function(opts) {
                return series.getSegment(opts);
            };
        }
        return series;
    },



    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    getRefItems: function(deep) {
        return [this.handleStyle, this.sliceStyle];
    }

});

Ext.chart.interactions.Manager.registerType('piegrouping', Ext.chart.interactions.PieGrouping);


Ext.chart.interactions.PieGrouping.HandleStyle = Ext.extend(Ext.chart.theme.Style, {
    isXType: function(xtype) {
        return xtype === 'handle';
    }
});
Ext.chart.interactions.PieGrouping.SliceStyle = Ext.extend(Ext.chart.theme.Style, {
    isXType: function(xtype) {
        return xtype === 'slice';
    }
});


/**
 * @class Ext.chart.interactions.Rotate
 * @extends Ext.chart.interactions.Abstract
 *
 * The Rotate interaction allows rotation of a Pie or Radar chart series. By default rotation
 * is performed via a single-finger drag around the center of the series, but can be configured
 * to use a two-finger pinch-rotate gesture by setting `gesture: 'pinch'`.
 *
 * To attach this interaction to a chart, include an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `rotate` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         series: [ ...pie/radar series options... ],
 *         interactions: [{
 *             type: 'rotate'
 *         }]
 *     });
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.Rotate = Ext.extend(Ext.chart.interactions.Abstract, {
    /**
     * @cfg {String} gesture
     * Defines the gesture type that will be used to rotate the chart. Currently only
     * supports `pinch` for two-finger rotation and `drag` for single-finger rotation.
     * Defaults to `drag`.
     */
    gesture: 'drag',

    constructor: function(config) {
        var me = this,
            interactionsNS = Ext.chart.interactions;
        interactionsNS.Rotate.superclass.constructor.call(me, config);
        interactionsNS.DelayedSync.prototype.constructor.call(me, config);
    },

    initEvents: function() {
        Ext.chart.interactions.Rotate.superclass.initEvents.call(this, arguments);
        var me = this,
            gesture = me.gesture;
        me.addChartListener(gesture + 'start', me.onGestureStart, me);
        me.addChartListener(gesture + 'end', me.onGestureEnd, me);
    },

    onGestureStart: function() {
        var me = this,
            axis = me.getAxis();
        me.cancelSync();
        me.getSeries().each(function(series) {
            series.unHighlightItem();
            series.origHighlight = series.highlight;
            series.highlight = false;
            if (series.callouts) {
                series.hideCallouts(0);
                series.getSurface().renderFrame();
            }
        });
        if (axis && axis.position === 'radial') {
            axis.hideLabels();
            axis.renderFrame();
        }
    },

    onGesture: function(e) {
        var me = this,
            oldAngle = me.lastAngle,
            firstPageX, secondPageX, firstPageY, secondPageY,
            series, seriesXY, newAngle, undef;

        if (me.gesture === 'pinch') {
            // Multi-touch pinch event - use angle between two touches
            firstPageX = e.firstPageX;
            firstPageY = e.firstPageY;
            secondPageX = e.secondPageX;
            secondPageY = e.secondPageY;
        } else {
            // Single-touch event - use angle between touch point and series center
            series = me.getSeries().get(0);
            seriesXY = series.getSurface().el.getXY();
            firstPageX = series.centerX + seriesXY[0];
            firstPageY = series.centerY + seriesXY[1];
            secondPageX = e.pageX;
            secondPageY = e.pageY;
        }
        newAngle = Ext.draw.Draw.degrees(Math.atan2(secondPageY - firstPageY, secondPageX - firstPageX));

        if (oldAngle === undef) {
            oldAngle = newAngle;
        }

        if (oldAngle !== newAngle) {
            me.rotateBy(newAngle - oldAngle);
        }

        me.lastAngle = newAngle;
    },

    onGestureEnd: function() {
        var me = this;
        me.delaySync();
        me.getSeries().each(function(series) {
            series.highlight = series.origHighlight;
        });
        delete me.lastAngle;
    },

    rotateBy: function(angle) {
        var me = this,
            series = me.getSeries(),
            axis = me.getAxis(),
            matrix;

        me.rotation = (me.rotation || 0) + angle;

        series.each(function(series) {
            matrix = series.getFastTransformMatrix();
            matrix.rotate(angle, series.centerX, series.centerY);
            series.setFastTransformMatrix(matrix);
        });

        if (axis) {
            matrix = axis.getFastTransformMatrix();
            matrix.rotate(angle, axis.centerX, axis.centerY);
            axis.setFastTransformMatrix(matrix);
        }
    },

    seriesFilter: function(series) {
        return series.type === 'pie' || series.type === 'radar';
    },

    getSeries: function() {
        return this.chart.series.filter(this.seriesFilter);
    },

    axisFilter: function(axis) {
        return axis.position === 'radial';
    },

    getAxis: function() {
        return this.chart.axes.findBy(this.axisFilter);
    },

    sync: function() {
        var me = this,
            chart = me.chart,
            axis = me.getAxis(),
            anim = chart.animate;

        chart.animate = false;
        me.getSeries().each(function(series) {
            series.rotation -= me.rotation;
            series.drawSeries();
            series.getSurface().renderFrame();
            series.clearTransform();
        });
        if (axis) {
            axis.rotation -= me.rotation;
            axis.drawAxis();
            axis.renderFrame();
            axis.clearTransform();
        }
        chart.animate = anim;

        me.rotation = 0;
    },

    needsSync: function() {
        return !!this.rotation;
    }
});

Ext.applyIf(Ext.chart.interactions.Rotate.prototype, Ext.chart.interactions.DelayedSync.prototype);

Ext.chart.interactions.Manager.registerType('rotate', Ext.chart.interactions.Rotate);

/**
 * @class Ext.chart.interactions.ItemCompare
 * @extends Ext.chart.interactions.Abstract
 *
 * The ItemCompare interaction allows the user to select two data points in a series and
 * see a trend comparison between the two. An arrowed line will be drawn between the two points.
 *
 * You can attach this interaction to a chart by including an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `itemcompare` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...some series options... ],
 *         interactions: [{
 *             type: 'itemcompare'
 *         }]
 *     });
 *
 * The display of the arrowed line {@link Ext.draw.Sprite sprites} can be customized via the
 * {@link #circle}, {@link #line}, and {@link #arrow} configs. It can also be given a global
 * {@link #offset position offset}.
 *
 * Use the {@link #show} and {@link #hide} events to perform additional actions when the trend
 * is displayed or hidden, such as displaying the trend change percentage to the user. Handlers
 * for these events are passed a reference to the ItemCompare interaction instance, so you
 * can access data from the {@link #item1} and {@link #item2} properties.
 *
 * @author Nicolas Garcia Belmonte <nicolas@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.ItemCompare = Ext.extend(Ext.chart.interactions.Abstract, {

    /**
     * @cfg {Object} circle
     * Custom {@link Ext.draw.Sprite} configuration to be applied to the sprite for the trend
     * line's starting circle.
     */

    /**
     * @cfg {Object} line
     * Custom {@link Ext.draw.Sprite} configuration to be applied to the sprite for the trend
     * line's connecting line.
     */

    /**
     * @cfg {Object} arrow
     * Custom {@link Ext.draw.Sprite} configuration to be applied to the sprite for the trend
     * line's ending arrow.
     */

    /**
     * @cfg {Object} offset
     * An optional x and y offset for the trend line's sprites in relation to the series items'
     * target points. Defaults to `{x:0, y:0}`.
     */

    /**
     * @property item1
     * @type {Object}
     * An object containing information about the first selected data point item if any.
     */

    /**
     * @property item2
     * @type {Object}
     * An object containing information about the second selected data point item if any.
     */

    /**
     * @cfg {String} gesture
     * Specifies which gesture type should be used for selecting the items to be compared.
     * Defaults to `tap`.
     */
    gesture: 'tap',

    type: 'itemcompare',
    constructor: function(config) {
        var me = this;

        me.addEvents(
        /**
         * @event hide
         * Fired when the point-to-point comparison is displayed
         * @param {Ext.chart.interactions.ItemCompare} this interaction instance
         */
        'show',

        /**
         * @event hide
         * Fired when the point-to-point comparison is hidden
         * @param {Ext.chart.interactions.ItemCompare} this interaction instance
         */
        'hide');

        me.circleStyle = new (Ext.extend(Ext.chart.theme.Style, {isXType: function(xtype) {return xtype === 'circle';}}))(config.circle);
        me.lineStyle = new (Ext.extend(Ext.chart.theme.Style, {isXType: function(xtype) {return xtype === 'line';}}))(config.line);
        me.arrowStyle = new (Ext.extend(Ext.chart.theme.Style, {isXType: function(xtype) {return xtype === 'arrow';}}))(config.arrow);

        delete config.line;
        delete config.circle;
        delete config.arrow;

        config.chart.on('refresh', me.reset, me);

        Ext.chart.interactions.ItemCompare.superclass.constructor.call(this, config);
    },

    onGesture: function(e) {
        var me = this,
            item = me.getItemForEvent(e);

        if (item) {
            //if we were already showing the overlay for previous items, then reset
            if (me.item1 && me.item2) {
                me.reset();
            }

            if (me.item1) {
                if (me.item1.series != item.series) {
                    me.reset();
                }
                else if (item !== me.item1) {
                    me.item2 = item;
                    item.series.highlightItem(item);
                    me.showOverlay();
                }
            } else {
                me.item1 = item;
                item.series.highlightItem(item);
            }
        } else {
            me.reset();
        }
    },

    /**
     * Resets any selected comparison items, removes the overlay arrow if present, and fires
     * the 'hide' event.
     */
    reset: function() {
        var me = this,
            series = me.activeSeries;

        if (series) {
            me.line.remove();
            me.circle.remove();
            me.arrow.remove();
            series.unHighlightItem();
            series.un('transform', me.onSeriesTransform, me);
            series.getOverlaySurface().renderFrame();
            delete me.activeSeries;
        }

        me.item1 = me.item2 = null;
        me.fireEvent('hide', me);
    },

    onSeriesTransform: function(obj, fast) {
        if (!fast) {
            this.renderSprites();
        }
    },

    showOverlay: function() {
        var me = this,
            series = me.item1.series; //both items are always from the same series
        me.activeSeries = series;
        series.on('transform', me.onSeriesTransform, me);

        me.renderSprites();
        me.fireEvent('show', me);
    },

    initSprites: function() {
        var me = this,
            Sprite = Ext.draw.Sprite,
            arrowStyle = me.arrowStyle.style,
            arrowRadius;

        if (!me.line) {
            me.line = new Sprite(
                Ext.apply({
                    type: 'path',
                    path: ['M', 0, 0]
                },
                me.lineStyle.style)
            );

            me.circle = new Sprite(
                Ext.apply({
                    type: 'circle',
                    radius: 3
                },
                me.circleStyle.style)
            );

            arrowRadius = arrowStyle.radius || 3;
            me.arrow = new Sprite(
                Ext.apply({
                    type: 'path',
                    path: "M".concat("0,0m0-", arrowRadius * 0.58, "l", arrowRadius * 0.5, ",", arrowRadius * 0.87, "-", arrowRadius, ",0z")
                },
                arrowStyle)
            );
        }
    },

    renderSprites: function() {
        var me = this,
            item1 = me.item1,
            item2 = me.item2,
            series = item1.series, //both items are always from the same series
            overlaySurface, p1, p2, offset, offsetX, offsetY, x1, y1, x2, y2, line, circle, arrow;

        if (series) {
            me.initSprites();

            overlaySurface = series.getOverlaySurface();
            p1 = item1.point;
            p2 = item2.point;
            offset = me.offset || {};
            offsetX = offset.x || 0;
            offsetY = offset.y || 0;
            x1 = (p1[0] + offsetX);
            y1 = (p1[1] + offsetY);
            x2 = (p2[0] + offsetX);
            y2 = (p2[1] + offsetY);
            line = me.line;
            circle = me.circle;
            arrow = me.arrow;

            line.setAttributes({
                path: ['M', x1, y1, 'L', x2, y2]
            });

            circle.setAttributes({
                translate: {
                    x: x1,
                    y: y1
                }
            });

            arrow.setAttributes({
                translate: {
                    x: x2,
                    y: y2
                },
                rotate: {
                    x: 0,
                    y: 0,
                    degrees: (Math.atan2(p2[1] - p1[1], p2[0] - p1[0]) * 180 / Math.PI - 90) + 180
                }
            });

            overlaySurface.add(line, circle, arrow);
            overlaySurface.renderFrame();
        }
    },


    /* ---------------------------------
      Methods needed for ComponentQuery
     ----------------------------------*/

    getRefItems: function(deep) {
        var me = this;
        return [me.arrowStyle, me.lineStyle, me.circleStyle];
    }

});

Ext.chart.interactions.Manager.registerType('itemcompare', Ext.chart.interactions.ItemCompare);

/**
 * @class Ext.chart.interactions.ItemHighlight
 * @extends Ext.chart.interactions.Abstract
 *
 * The ItemHighlight interaction allows highlighting of series data items on the chart.
 * This interaction enables triggering and clearing the highlight on certain events, but
 * does not control how the highlighting is implemented or styled; that is handled by
 * each individual Series type and the {@link Ext.chart.Highlight} mixin. See the documentation
 * for that mixin for how to customize the highlighting effect.
 *
 * To attach this interaction to a chart, include an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `itemhighlight` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...some series options... ],
 *         interactions: [{
 *             type: 'itemhighlight'
 *         }]
 *     });

 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.ItemHighlight = Ext.extend(Ext.chart.interactions.Abstract, {

    gesture: 'tap',
    unHighlightEvent: 'touchstart',

    initEvents: function() {
        var me = this;
        Ext.chart.interactions.ItemHighlight.superclass.initEvents.call(me, arguments);
        me.addChartListener(me.unHighlightEvent, me.onUnHighlightEvent, me);
    },

    onGesture: function(e) {
        var me = this,
            items = me.getItemsForEvent(e),
            item, highlightedItem, series,
            i, len;

        for(i = 0, len = items.length; i < len; i++) {
            item = items[i];
            series = item.series;
            highlightedItem = series.highlightedItem;
            if (highlightedItem !== item) {
                if (highlightedItem) {
                    highlightedItem.series.unHighlightItem();
                }
                series.highlightItem(item);
                series.highlightedItem = item;
            }
        }
    },

    onUnHighlightEvent: function(e) {
        var me = this,
            chart = me.chart,
            xy = chart.getEventXY(e),
            highlightedItem;
        chart.series.each(function(series) {
            highlightedItem = series.highlightedItem;
            if (highlightedItem && highlightedItem !== series.getItemForPoint(xy[0], xy[1])) {
                series.unHighlightItem();
                delete series.highlightedItem;
            }
        });
    }

});

Ext.chart.interactions.Manager.registerType('itemhighlight', Ext.chart.interactions.ItemHighlight);

/**
 * @class Ext.chart.interactions.ItemInfo
 * @extends Ext.util.Observable
 *
 * The ItemInfo interaction allows displaying detailed information about a series data
 * point in a popup panel.
 *
 * To attach this interaction to a chart, include an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `iteminfo` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...some series options... ],
 *         interactions: [{
 *             type: 'iteminfo',
 *             listeners: {
 *                 show: function(me, item, panel) {
 *                     panel.update('Stock Price: $' + item.storeItem.get('price'));
 *                 }
 *             }
 *         }]
 *     });

 * @author Nicolas Garcia Belmonte <nicolas@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.ItemInfo = Ext.extend(Ext.chart.interactions.Abstract, {

    /**
     * @cfg {String} gesture
     * Defines the gesture type that should trigger the item info panel to be displayed.
     * Defaults to `tap`.
     */
    gesture: 'tap',

    /**
     * @cfg {Object} panel
     * An optional set of configuration overrides for the {@link Ext.Panel} that gets
     * displayed. This object will be merged with the default panel configuration.
     */

    constructor: function(config) {
        var me = this;

        me.addEvents(
            /**
             * @event show
             * Fires when the info panel is shown.
             * @param {Ext.chart.interactions.ItemInfo} this The interaction instance
             * @param {Object} item The item whose info is being displayed
             * @param {Ext.Panel} panel The panel for displaying the info
             */
            'show'
        );

        Ext.chart.interactions.ItemInfo.superclass.constructor.call(me, config);
    },

    getPanel: function() {
        var me = this,
            panel = me.infoPanel;
        if (!panel) {
            panel = me.infoPanel = new Ext.Panel(Ext.apply({
                floating: true,
                modal: true,
                centered: true,
                width: 250,
                styleHtmlContent: true,
                scroll: 'vertical',
                dockedItems: [{
                    dock: 'top',
                    xtype: 'toolbar',
                    title: 'Item Detail'
                }],
                stopMaskTapEvent: false,
                fullscreen: false,
                listeners: {
                    hide: me.reset,
                    scope: me
                }
            },
            me.panel));
        }
        return panel;
    },

    onGesture: function(e) {
        var me = this,
            item = me.getItemForEvent(e),
            panel;
        if (item) {
            me.item = item;
            item.series.highlightItem(item);
            panel = me.getPanel();
            me.fireEvent('show', me, item, panel);
            panel.show('pop');
        }
    },

    reset: function() {
        var me = this,
            item = me.item;
        if (item) {
            item.series.unHighlightItem(item);
            delete me.item;
        }
    }

});

Ext.chart.interactions.Manager.registerType('iteminfo', Ext.chart.interactions.ItemInfo);

/**
 * @class Ext.chart.interactions.Reset
 * @extends Ext.chart.interactions.Abstract
 *
 * The Reset interaction allows resetting of all previous user interactions with
 * the chart. By default the reset is triggered by a double-tap on the empty chart
 * area; to customize the event use the {@link #event} config.
 *
 * To attach this interaction to a chart, include an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `reset` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...some series options... ],
 *         interactions: [{
 *             type: 'reset'
 *         }]
 *     });

 * @author Nicolas Garcia Belmonte <nicolas@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.Reset = Ext.extend(Ext.chart.interactions.Abstract, {

    /**
     * @cfg {String} gesture
     * Defines the gesture type that should trigger the chart reset. Gestures that occur on a series
     * item will be ignored. Defaults to `doubletap`.
     */
    gesture: 'doubletap',

    /**
     * @cfg {Boolean} confirm
     * If set to `true`, a dialog will be presented to the user to confirm that they want to reset
     * the chart. Defaults to `false`.
     */

    /**
     * @cfg {String} confirmTitle
     * Specifies the title displayed in the confirmation dialog, if {@link #confirm} is `true`.
     * Defaults to `'Reset'`
     */
    confirmTitle: 'Reset',

    /**
     * @cfg {String} confirmText
     * Specifies the text displayed in the confirmation dialog, if {@link #confirm} is `true`.
     * Defaults to `'Reset the chart?'`
     */
    confirmText: 'Reset the chart?',

    onGesture: function(e) {
        var me = this,
            chart = me.chart;
        if (!me.getItemForEvent(e)) {
            if (me.confirm) {
                Ext.Msg.confirm(me.confirmTitle, me.confirmText, function(button) {
                    if (button === 'yes') {
                        chart.reset();
                    }
                });
            } else {
                chart.reset();
            }
        }
    }
});

Ext.chart.interactions.Manager.registerType('reset', Ext.chart.interactions.Reset);

/**
 * @class Ext.chart.interactions.ToggleStacked
 * @extends Ext.chart.interactions.Abstract
 *
 * The ToggleStacked interaction allows toggling a {@link Ext.chart.series.Bar bar} or
 * {@link Ext.chart.series.Column column} series between stacked and grouped orientations
 * for multiple yField values. By default this is triggered via a `swipe` event; to customize
 * the trigger event modify the {@link #event} config.
 *
 * To attach this interaction to a chart, include an entry in the chart's
 * {@link Ext.chart.Chart#interactions interactions} config with the `togglestacked` type:
 *
 *     new Ext.chart.Chart({
 *         renderTo: Ext.getBody(),
 *         width: 800,
 *         height: 600,
 *         store: store1,
 *         axes: [ ...some axes options... ],
 *         series: [ ...bar or column series options... ],
 *         interactions: [{
 *             type: 'togglestacked',
 *             event: 'doubletap'
 *         }]
 *     });
 *
 * @author Jason Johnston <jason@sencha.com>
 * @docauthor Jason Johnston <jason@sencha.com>
 */
Ext.chart.interactions.ToggleStacked = Ext.extend(Ext.chart.interactions.Abstract, {

    /**
     * @cfg {String} gesture
     * Defines the gesture type that should trigger the toggle. Defaults to `swipe`.
     */
    gesture: 'swipe',

    /**
     * @cfg {Boolean} animateDirect
     * If set to `true`, then animation will skip the intermediate disjoint state and simply
     * animate directly from the stacked to grouped orientation. Only relevant if the chart
     * is configured to allow animation. Defaults to `false`.
     */

    onGesture: function(e) {
        var me = this,
            chart = me.chart,
            series = me.getSeries();

        if (series) {
            if (chart.animate && !me.animateDirect) {
                if (!me.locked) {
                    me.lock();
                    if (series.stacked) {
                        series.disjointStacked = true;
                        me.afterAnim(series, function() {
                            series.stacked = series.disjointStacked = false;
                            me.afterAnim(series, me.unlock);
                            chart.redraw();
                        });
                        series.drawSeries();
                    }
                    else {
                        series.stacked = series.disjointStacked = true;
                        me.afterAnim(series, function() {
                            series.disjointStacked = false;
                            me.afterAnim(series, me.unlock);
                            series.drawSeries();
                        });
                        chart.redraw();
                    }
                }
            } else {
                series.stacked = !series.stacked;
                chart.redraw();
            }
        }
    },

    lock: function() {
        this.locked = true;
    },

    unlock: function() {
        this.locked = false;
    },

    afterAnim: function(series, fn) {
        // TODO this fires too soon if the series is configured with bar labels
        series.on('afterrender', fn, this, {
            single: true,
            // must delay slightly so the handler can set another afterrender
            // listener without it getting called immediately:
            delay: 1
        });
    },

    getSeries: function() {
        return this.chart.series.findBy(function(series) {
            return series.type === 'bar' || series.type === 'column';
        });
    }
});

Ext.chart.interactions.Manager.registerType('togglestacked', Ext.chart.interactions.ToggleStacked);



