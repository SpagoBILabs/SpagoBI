/**
 * @class Ext.dom.AbstractElement
 */
Ext.dom.AbstractElement.addInheritableStatics({
    unitRe: /\d+(px|em|%|en|ex|pt|in|cm|mm|pc)$/i,
    camelRe: /(-[a-z])/gi,
    cssRe: /([a-z0-9-]+)\s*:\s*([^;\s]+(?:\s*[^;\s]+)*);?/gi,
    opacityRe: /alpha\(opacity=(.*)\)/i,
    propertyCache: {},
    defaultUnit : "px",
    borders: {l: 'border-left-width', r: 'border-right-width', t: 'border-top-width', b: 'border-bottom-width'},
    paddings: {l: 'padding-left', r: 'padding-right', t: 'padding-top', b: 'padding-bottom'},
    margins: {l: 'margin-left', r: 'margin-right', t: 'margin-top', b: 'margin-bottom'},
    /**
     * Test if size has a unit, otherwise appends the passed unit string, or the default for this Element.
     * @param size {Object} The size to set
     * @param units {String} The units to append to a numeric size value
     * @private
     * @static
     */
    addUnits: function(size, units) {
        // Most common case first: Size is set to a number
        if (typeof size == 'number') {
            return size + (units || this.defaultUnit || 'px');
        }

        // Size set to a value which means "auto"
        if (size === "" || size == "auto" || size === undefined || size === null) {
            return size || '';
        }

        // Otherwise, warn if it's not a valid CSS measurement
        if (!this.unitRe.test(size)) {
            //<debug>
            if (Ext.isDefined(Ext.global.console)) {
                Ext.global.console.warn("Warning, size detected as NaN on Element.addUnits.");
            }
            //</debug>
            return size || '';
        }

        return size;
    },

    /**
     * @static
     * @private
     */
    isAncestor: function(p, c) {
        var ret = false;

        p = Ext.getDom(p);
        c = Ext.getDom(c);
        if (p && c) {
            if (p.contains) {
                return p.contains(c);
            } else if (p.compareDocumentPosition) {
                return !!(p.compareDocumentPosition(c) & 16);
            } else {
                while ((c = c.parentNode)) {
                    ret = c == p || ret;
                }
            }
        }
        return ret;
    },

    /**
     * Parses a number or string representing margin sizes into an object. Supports CSS-style margin declarations
     * (e.g. 10, "10", "10 10", "10 10 10" and "10 10 10 10" are all valid options and would return the same result)
     * @static
     * @param {Number/String} box The encoded margins
     * @return {Object} An object with margin sizes for top, right, bottom and left
     */
    parseBox: function(box) {
        if (typeof box != 'string') {
            box = box.toString();
        }
        var parts  = box.split(' '),
            ln = parts.length;

        if (ln == 1) {
            parts[1] = parts[2] = parts[3] = parts[0];
        }
        else if (ln == 2) {
            parts[2] = parts[0];
            parts[3] = parts[1];
        }
        else if (ln == 3) {
            parts[3] = parts[1];
        }

        return {
            top   :parseFloat(parts[0]) || 0,
            right :parseFloat(parts[1]) || 0,
            bottom:parseFloat(parts[2]) || 0,
            left  :parseFloat(parts[3]) || 0
        };
    },

    /**
     * Parses a number or string representing margin sizes into an object. Supports CSS-style margin declarations
     * (e.g. 10, "10", "10 10", "10 10 10" and "10 10 10 10" are all valid options and would return the same result)
     * @static
     * @param {Number/String} box The encoded margins
     * @param {String} units The type of units to add
     * @return {String} An string with unitized (px if units is not specified) metrics for top, right, bottom and left
     */
    unitizeBox: function(box, units) {
        var A = this.addUnits,
            B = this.parseBox(box);

        return A(B.top, units) + ' ' +
               A(B.right, units) + ' ' +
               A(B.bottom, units) + ' ' +
               A(B.left, units);

    },

    // private
    camelReplaceFn: function(m, a) {
        return a.charAt(1).toUpperCase();
    },

    /**
     * Normalizes CSS property keys from dash delimited to camel case JavaScript Syntax.
     * For example:
     *
     * - border-width -> borderWidth
     * - padding-top -> paddingTop
     *
     * @static
     * @param {String} prop The property to normalize
     * @return {String} The normalized string
     */
    normalize: function(prop) {
        // TODO: Mobile optimization?
        if (prop == 'float') {
            prop = Ext.supports.Float ? 'cssFloat' : 'styleFloat';
        }
        return this.propertyCache[prop] || (this.propertyCache[prop] = prop.replace(this.camelRe, this.camelReplaceFn));
    },

    /**
     * Retrieves the document height
     * @static
     * @return {Number} documentHeight
     */
    getDocumentHeight: function() {
        return Math.max(!Ext.isStrict ? document.body.scrollHeight : document.documentElement.scrollHeight, this.getViewportHeight());
    },

    /**
     * Retrieves the document width
     * @static
     * @return {Number} documentWidth
     */
    getDocumentWidth: function() {
        return Math.max(!Ext.isStrict ? document.body.scrollWidth : document.documentElement.scrollWidth, this.getViewportWidth());
    },

    /**
     * Retrieves the viewport height of the window.
     * @static
     * @return {Number} viewportHeight
     */
    getViewportHeight: function(){
        return window.innerHeight;
    },

    /**
     * Retrieves the viewport width of the window.
     * @static
     * @return {Number} viewportWidth
     */
    getViewportWidth: function() {
        return window.innerWidth;
    },

    /**
     * Retrieves the viewport size of the window.
     * @static
     * @return {Object} object containing width and height properties
     */
    getViewSize: function() {
        return {
            width: window.innerWidth,
            height: window.innerHeight
        };
    },

    /**
     * Retrieves the current orientation of the window. This is calculated by
     * determing if the height is greater than the width.
     * @static
     * @return {String} Orientation of window: 'portrait' or 'landscape'
     */
    getOrientation: function() {
        if (Ext.supports.OrientationChange) {
            return (window.orientation == 0) ? 'portrait' : 'landscape';
        }

        return (window.innerHeight > window.innerWidth) ? 'portrait' : 'landscape';
    },

    /**
     * Returns the top Element that is located at the passed coordinates
     * @static
     * @param {Number} x The x coordinate
     * @param {Number} y The y coordinate
     * @return {String} The found Element
     */
    fromPoint: function(x, y) {
        return Ext.get(document.elementFromPoint(x, y));
    },

    /**
     * Converts a CSS string into an object with a property for each style.
     *
     * The sample code below would return an object with 2 properties, one
     * for background-color and one for color.
     *
     *     var css = 'background-color: red;color: blue; ';
     *     console.log(Ext.dom.Element.parseStyles(css));
     *
     * @static
     * @param {String} styles A CSS string
     * @return {Object} styles
     */
    parseStyles: function(styles){
        var out = {},
            cssRe = this.cssRe,
            matches;

        if (styles) {
            // Since we're using the g flag on the regex, we need to set the lastIndex.
            // This automatically happens on some implementations, but not others, see:
            // http://stackoverflow.com/questions/2645273/javascript-regular-expression-literal-persists-between-function-calls
            // http://blog.stevenlevithan.com/archives/fixing-javascript-regexp
            cssRe.lastIndex = 0;
            while ((matches = cssRe.exec(styles))) {
                out[matches[1]] = matches[2];
            }
        }
        return out;
    }
});

//TODO Need serious cleanups
(function(){
    var doc = document,
        AbstractElement = Ext.dom.AbstractElement,
        activeElement = null,
        isCSS1 = doc.compatMode == "CSS1Compat",
        flyInstance,
        fly = function (el) {
            if (!flyInstance) {
                flyInstance = new AbstractElement.Fly();
            }
            flyInstance.attach(el);
            return flyInstance;
        };

    // If the browser does not support document.activeElement we need some assistance.
    // This covers old Safari 3.2 (4.0 added activeElement along with just about all
    // other browsers). We need this support to handle issues with old Safari.
    if (!('activeElement' in doc) && doc.addEventListener) {
        doc.addEventListener('focus',
            function (ev) {
                if (ev && ev.target) {
                    activeElement = (ev.target == doc) ? null : ev.target;
                }
            }, true);
    }

    /*
     * Helper function to create the function that will restore the selection.
     */
    function makeSelectionRestoreFn (activeEl, start, end) {
        return function () {
            activeEl.selectionStart = start;
            activeEl.selectionEnd = end;
        };
    }

    AbstractElement.addInheritableStatics({
        /**
         * Returns the active element in the DOM. If the browser supports activeElement
         * on the document, this is returned. If not, the focus is tracked and the active
         * element is maintained internally.
         * @return {HTMLElement} The active (focused) element in the document.
         */
        getActiveElement: function () {
            return doc.activeElement || activeElement;
        },

        /**
         * Creates a function to call to clean up problems with the work-around for the
         * WebKit RightMargin bug. The work-around is to add "display: 'inline-block'" to
         * the element before calling getComputedStyle and then to restore its original
         * display value. The problem with this is that it corrupts the selection of an
         * INPUT or TEXTAREA element (as in the "I-beam" goes away but ths focus remains).
         * To cleanup after this, we need to capture the selection of any such element and
         * then restore it after we have restored the display style.
         *
         * @param {Ext.dom.Element} target The top-most element being adjusted.
         * @private
         */
        getRightMarginFixCleaner: function (target) {
            var supports = Ext.supports,
                hasInputBug = supports.DisplayChangeInputSelectionBug,
                hasTextAreaBug = supports.DisplayChangeTextAreaSelectionBug;

            if (hasInputBug || hasTextAreaBug) {
                var activeEl = doc.activeElement || activeElement, // save a call
                    tag = activeEl && activeEl.tagName,
                    start,
                    end;

                if ((hasTextAreaBug && tag == 'TEXTAREA') ||
                    (hasInputBug && tag == 'INPUT' && activeEl.type == 'text')) {
                    if (Ext.dom.Element.isAncestor(target, activeEl)) {
                        start = activeEl.selectionStart;
                        end = activeEl.selectionEnd;

                        if (Ext.isNumber(start) && Ext.isNumber(end)) { // to be safe...
                            // We don't create the raw closure here inline because that
                            // will be costly even if we don't want to return it (nested
                            // function decls and exprs are often instantiated on entry
                            // regardless of whether execution ever reaches them):
                            return makeSelectionRestoreFn(activeEl, start, end);
                        }
                    }
                }
            }

            return Ext.emptyFn; // avoid special cases, just return a nop
        },

        getViewWidth: function(full) {
            return full ? Ext.dom.Element.getDocumentWidth() : Ext.dom.Element.getViewportWidth();
        },

        getViewHeight: function(full) {
            return full ? Ext.dom.Element.getDocumentHeight() : Ext.dom.Element.getViewportHeight();
        },

        getDocumentHeight: function() {
            return Math.max(!isCSS1 ? doc.body.scrollHeight : doc.documentElement.scrollHeight, Ext.dom.Element.getViewportHeight());
        },

        getDocumentWidth: function() {
            return Math.max(!isCSS1 ? doc.body.scrollWidth : doc.documentElement.scrollWidth, Ext.dom.Element.getViewportWidth());
        },

        getViewportHeight: function(){
            return Ext.isIE ?
                   (Ext.isStrict ? doc.documentElement.clientHeight : doc.body.clientHeight) :
                   self.innerHeight;
        },

        getViewportWidth: function() {
            return (!Ext.isStrict && !Ext.isOpera) ? doc.body.clientWidth :
                   Ext.isIE ? doc.documentElement.clientWidth : self.innerWidth;
        },

        getY: function(el) {
            return Ext.dom.Element.getXY(el)[1];
        },

        getX: function(el) {
            return Ext.dom.Element.getXY(el)[0];
        },

        getXY: function(el) {
            var p,
                pe,
                b,
                bt,
                bl,
                dbd,
                x = 0,
                y = 0,
                scroll,
                hasAbsolute,
                bd = (doc.body || doc.documentElement),
                ret = [0,0];

            el = Ext.getDom(el);

            if(el != bd){
                hasAbsolute = fly(el).isStyle("position", "absolute");

                if (el.getBoundingClientRect) {
                    b = el.getBoundingClientRect();
                    scroll = fly(document).getScroll();
                    ret = [Math.round(b.left + scroll.left), Math.round(b.top + scroll.top)];
                } else {
                    p = el;

                    while (p) {
                        pe = fly(p);
                        x += p.offsetLeft;
                        y += p.offsetTop;

                        hasAbsolute = hasAbsolute || pe.isStyle("position", "absolute");

                        if (Ext.isGecko) {
                            y += bt = parseInt(pe.getStyle("borderTopWidth"), 10) || 0;
                            x += bl = parseInt(pe.getStyle("borderLeftWidth"), 10) || 0;

                            if (p != el && !pe.isStyle('overflow','visible')) {
                                x += bl;
                                y += bt;
                            }
                        }
                        p = p.offsetParent;
                    }

                    if (Ext.isSafari && hasAbsolute) {
                        x -= bd.offsetLeft;
                        y -= bd.offsetTop;
                    }

                    if (Ext.isGecko && !hasAbsolute) {
                        dbd = fly(bd);
                        x += parseInt(dbd.getStyle("borderLeftWidth"), 10) || 0;
                        y += parseInt(dbd.getStyle("borderTopWidth"), 10) || 0;
                    }

                    p = el.parentNode;
                    while (p && p != bd) {
                        if (!Ext.isOpera || (p.tagName != 'TR' && !fly(p).isStyle("display", "inline"))) {
                            x -= p.scrollLeft;
                            y -= p.scrollTop;
                        }
                        p = p.parentNode;
                    }
                    ret = [x,y];
                }
            }
            return ret;
        },

        setXY: function(el, xy) {
            (el = Ext.fly(el, '_setXY')).position();

            var pts = el.translatePoints(xy),
                style = el.dom.style,
                pos;

            for (pos in pts) {
                if (!isNaN(pts[pos])) {
                    style[pos] = pts[pos] + "px";
                }
            }
        },

        setX: function(el, x) {
            Ext.dom.Element.setXY(el, [x, false]);
        },

        setY: function(el, y) {
            Ext.dom.Element.setXY(el, [false, y]);
        },

        /**
         * Serializes a DOM form into a url encoded string
         * @param {Object} form The form
         * @return {String} The url encoded form
         */
        serializeForm: function(form) {
            var fElements = form.elements || (document.forms[form] || Ext.getDom(form)).elements,
                hasSubmit = false,
                encoder = encodeURIComponent,
                name,
                data = '',
                type,
                hasValue;

            Ext.each(fElements, function(element){
                name = element.name;
                type = element.type;

                if (!element.disabled && name) {
                    if (/select-(one|multiple)/i.test(type)) {
                        Ext.each(element.options, function(opt){
                            if (opt.selected) {
                                hasValue = opt.hasAttribute ? opt.hasAttribute('value') : opt.getAttributeNode('value').specified;
                                data += Ext.String.format("{0}={1}&", encoder(name), encoder(hasValue ? opt.value : opt.text));
                            }
                        });
                    } else if (!(/file|undefined|reset|button/i.test(type))) {
                        if (!(/radio|checkbox/i.test(type) && !element.checked) && !(type == 'submit' && hasSubmit)) {
                            data += encoder(name) + '=' + encoder(element.value) + '&';
                            hasSubmit = /submit/i.test(type);
                        }
                    }
                }
            });
            return data.substr(0, data.length - 1);
        }
    });
})();
