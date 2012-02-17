/**
 * @class Ext.dom.Element
 */
(function() {

var Element = Ext.dom.Element,
    view = document.defaultView;

var adjustDirect2DTableRe = /table-row|table-.*-group/,
    INTERNAL = '_internal',
    HIDDEN = 'hidden',
    ISCLIPPED = 'isClipped',
    OVERFLOW = 'overflow',
    OVERFLOWX = 'overflow-x',
    OVERFLOWY = 'overflow-y',
    ORIGINALCLIP = 'originalClip';

// These property values are read from the parentNode if they cannot be read
// from the child:
Element.inheritedProps = {
    fontSize: 1,
    fontStyle: 1,
    opacity: 1
};

if (!view || !view.getComputedStyle) {
    Element.override({
        getStyle: Ext.isIE6 ?
            // IE6 flavor:
            function (prop) {
                var me = this,
                    dom = me.dom,
                    hook = me.styleHooks[prop],
                    name, cs;

                if (dom == document) {
                    return null;
                }
                if (!hook) {
                    me.styleHooks[prop] = hook = { name: Element.normalize(prop) };
                }
                if (hook.get) {
                    return hook.get(dom, me);
                }

                name = hook.name;

                do {
                    try {
                        return dom.style[name] || ((cs = dom.currentStyle) ? cs[name] : null);
                    } catch (e) {
                        // in some cases, IE6 will throw Invalid Argument for properties
                        // like fontSize (see in /examples/tabs/tabs.html).
                    }

                    if (!Element.inheritedProps[name]) {
                        break;
                    }

                    dom = dom.parentNode;
                    // this is _not_ perfect, but we can only hope that the style we
                    // need is inherited from a parentNode. If not and since IE won't
                    // give us the info we need, we are never going to be 100% right.
                } while (dom);

                //<debug>
                Ext.log({
                    level: 'warn',
                    msg: 'Failed to get '+me.dom.id+'.currentStyle.'+prop // not dom.id!
                });
                //</debug>
                return null;
            } :
            // IE7+ flavor:
            function (prop) {
                var me = this,
                    dom = me.dom,
                    hook = me.styleHooks[prop],
                    name, cs;

                if (dom == document) {
                    return null;
                }
                if (!hook) {
                    me.styleHooks[prop] = hook = { name: Element.normalize(prop) };
                }
                if (hook.get) {
                    return hook.get(dom, me);
                }

                name = hook.name;

                return dom.style[name] || ((cs = dom.currentStyle) ? cs[name] : null);
            }
    });
}

Element.override({
    getHeight: function(contentHeight, preciseHeight) {
        var me = this,
            dom = me.dom,
            hidden = Ext.isIE && me.isStyle('display', 'none'),
            height, overflow, style, floating;

        // IE Quirks mode acts more like a max-size measurement unless overflow is hidden during measurement.
        // We will put the overflow back to it's original value when we are done measuring.
        if (Ext.isIEQuirks) {
            style = dom.style;
            overflow = style.overflow;
            me.setStyle({ overflow: 'hidden'});
        }

        height = dom.offsetHeight;

        height = Math.max(height, hidden ? 0 : dom.clientHeight) || 0;

        // IE9 Direct2D dimension rounding bug
        if (!hidden && Ext.supports.Direct2DBug) {
            floating = me.adjustDirect2DDimension('height');
            if (preciseHeight) {
                height += floating;
            }
            else if (floating > 0 && floating < 0.5) {
                height++;
            }
        }

        if (contentHeight) {
            height -= (me.getBorderWidth("tb") + me.getPadding("tb"));
        }

        if (Ext.isIEQuirks) {
            me.setStyle({ overflow: overflow});
        }

        if (height < 0) {
            height = 0;
        }
        return height;
    },

    getWidth: function(contentWidth, preciseWidth) {
        var me = this,
            dom = me.dom,
            hidden = Ext.isIE && me.isStyle('display', 'none'),
            rect, width, overflow, style, floating, parentPosition;

        // IE Quirks mode acts more like a max-size measurement unless overflow is hidden during measurement.
        // We will put the overflow back to it's original value when we are done measuring.
        if (Ext.isIEQuirks) {
            style = dom.style;
            overflow = style.overflow;
            me.setStyle({overflow: 'hidden'});
        }

        // Fix Opera 10.5x width calculation issues
        if (Ext.isOpera10_5) {
            if (dom.parentNode.currentStyle.position === 'relative') {
                parentPosition = dom.parentNode.style.position;
                dom.parentNode.style.position = 'static';
                width = dom.offsetWidth;
                dom.parentNode.style.position = parentPosition;
            }
            width = Math.max(width || 0, dom.offsetWidth);

            // Gecko will in some cases report an offsetWidth that is actually less than the width of the
            // text contents, because it measures fonts with sub-pixel precision but rounds the calculated
            // value down. Using getBoundingClientRect instead of offsetWidth allows us to get the precise
            // subpixel measurements so we can force them to always be rounded up. See
            // https://bugzilla.mozilla.org/show_bug.cgi?id=458617
            // Rounding up ensures that the width includes the full width of the text contents.
        } else if (Ext.supports.BoundingClientRect) {
            rect = dom.getBoundingClientRect();
            width = rect.right - rect.left;
            width = preciseWidth ? width : Math.ceil(width);
        } else {
            width = dom.offsetWidth;
        }

        width = Math.max(width, hidden ? 0 : dom.clientWidth) || 0;

        // IE9 Direct2D dimension rounding bug
        if (!hidden && Ext.supports.Direct2DBug) {
            // get the fractional portion of the sub-pixel precision width of the element's text contents
            floating = me.adjustDirect2DDimension('width');
            if (preciseWidth) {
                width += floating;
            }
            // IE9 also measures fonts with sub-pixel precision, but unlike Gecko, instead of rounding the offsetWidth down,
            // it rounds to the nearest integer.  This means that in order to ensure that the width includes the full
            // width of the text contents we need to increment the width by 1 only if the fractional portion is less than 0.5
            else if (floating > 0 && floating < 0.5) {
                width++;
            }
        }

        if (contentWidth) {
            width -= (me.getBorderWidth("lr") + me.getPadding("lr"));
        }

        if (Ext.isIEQuirks) {
            me.setStyle({ overflow: overflow});
        }

        if (width < 0) {
            width = 0;
        }
        return width;
    },

    setWidth: function(width, animate) {
        var me = this;
        width = me.adjustWidth(width);
        if (!animate || !me.anim) {
            me.dom.style.width = me.addUnits(width);
        }
        else {
            if (!Ext.isObject(animate)) {
                animate = {};
            }
            me.animate(Ext.applyIf({
                to: {
                    width: width
                }
            }, animate));
        }
        return me;
    },

    setHeight : function(height, animate) {
        var me = this;

        height = me.adjustHeight(height);
        if (!animate || !me.anim) {
            me.dom.style.height = me.addUnits(height);
        }
        else {
            if (!Ext.isObject(animate)) {
                animate = {};
            }
            me.animate(Ext.applyIf({
                to: {
                    height: height
                }
            }, animate));
        }

        return me;
    },

    applyStyles: function(style) {
        Ext.DomHelper.applyStyles(this.dom, style);
        return this;
    },

    setSize: function(width, height, animate) {
        var me = this;

        if (Ext.isObject(width)) { // in case of object from getSize()
            animate = height;
            height = width.height;
            width = width.width;
        }

        width = me.adjustWidth(width);
        height = me.adjustHeight(height);

        if (!animate || !me.anim) {
            me.dom.style.width = me.addUnits(width);
            me.dom.style.height = me.addUnits(height);
        }
        else {
            if (animate === true) {
                animate = {};
            }
            me.animate(Ext.applyIf({
                to: {
                    width: width,
                    height: height
                }
            }, animate));
        }

        return me;
    },

    getViewSize : function() {
        var me = this,
            dom = me.dom,
            isDoc = (dom == Ext.getDoc().dom || dom == Ext.getBody().dom),
            style, overflow, ret;

        // If the body, use static methods
        if (isDoc) {
            ret = {
                width : Element.getViewWidth(),
                height : Element.getViewHeight()
            };

            // Else use clientHeight/clientWidth
        }
        else {
            // IE 6 & IE Quirks mode acts more like a max-size measurement unless overflow is hidden during measurement.
            // We will put the overflow back to it's original value when we are done measuring.
            if (Ext.isIE6 || Ext.isIEQuirks) {
                style = dom.style;
                overflow = style.overflow;
                me.setStyle({ overflow: 'hidden'});
            }
            ret = {
                width : dom.clientWidth,
                height : dom.clientHeight
            };
            if (Ext.isIE6 || Ext.isIEQuirks) {
                me.setStyle({ overflow: overflow });
            }
        }
        return ret;
    },

    getSize: function(contentSize) {
        return {width: this.getWidth(contentSize), height: this.getHeight(contentSize)};
    },

    // TODO: Look at this

    // private  ==> used by Fx
    adjustWidth : function(width) {
        var me = this,
            isNum = (typeof width == 'number');

        if (isNum && me.autoBoxAdjust && !me.isBorderBox()) {
            width -= (me.getBorderWidth("lr") + me.getPadding("lr"));
        }
        return (isNum && width < 0) ? 0 : width;
    },

    // private   ==> used by Fx
    adjustHeight : function(height) {
        var me = this,
            isNum = (typeof height == "number");

        if (isNum && me.autoBoxAdjust && !me.isBorderBox()) {
            height -= (me.getBorderWidth("tb") + me.getPadding("tb"));
        }
        return (isNum && height < 0) ? 0 : height;
    },

    /**
     * Return the CSS color for the specified CSS attribute. rgb, 3 digit (like `#fff`) and valid values
     * are convert to standard 6 digit hex color.
     * @param {String} attr The css attribute
     * @param {String} defaultValue The default value to use when a valid color isn't found
     * @param {String} [prefix] defaults to #. Use an empty string when working with
     * color anims.
     */
    getColor : function(attr, defaultValue, prefix) {
        var v = this.getStyle(attr),
            color = prefix || prefix === '' ? prefix : '#',
            h;

        if (!v || (/transparent|inherit/.test(v))) {
            return defaultValue;
        }
        if (/^r/.test(v)) {
            Ext.each(v.slice(4, v.length - 1).split(','), function(s) {
                h = parseInt(s, 10);
                color += (h < 16 ? '0' : '') + h.toString(16);
            });
        } else {
            v = v.replace('#', '');
            color += v.length == 3 ? v.replace(/^(\w)(\w)(\w)$/, '$1$1$2$2$3$3') : v;
        }
        return(color.length > 5 ? color.toLowerCase() : defaultValue);
    },

    /**
     * Set the opacity of the element
     * @param {Number} opacity The new opacity. 0 = transparent, .5 = 50% visibile, 1 = fully visible, etc
     * @param {Boolean/Object} [animate] a standard Element animation config object or `true` for
     * the default animation (`{duration: .35, easing: 'easeIn'}`)
     * @return {Ext.dom.Element} this
     */
    setOpacity: function(opacity, animate) {
        var me = this;

        if (!me.dom) {
            return me;
        }

        if (!animate || !me.anim) {
            me.setStyle('opacity', opacity);
        }
        else {
            if (!Ext.isObject(animate)) {
                animate = {
                    duration: 350,
                    easing: 'ease-in'
                };
            }

            me.animate(Ext.applyIf({
                to: {
                    opacity: opacity
                }
            }, animate));
        }
        return me;
    },

    /**
     * Clears any opacity settings from this element. Required in some cases for IE.
     * @return {Ext.dom.Element} this
     */
    clearOpacity : function() {
        return this.setOpacity('');
    },

    /**
     * @private
     * Returns 1 if the browser returns the subpixel dimension rounded to the lowest pixel.
     * @return {Number} 0 or 1
     */
    adjustDirect2DDimension: function(dimension) {
        var me = this,
            dom = me.dom,
            display = me.getStyle('display'),
            inlineDisplay = dom.style.display,
            inlinePosition = dom.style.position,
            originIndex = dimension === 'width' ? 0 : 1,
            floating;

        if (display === 'inline') {
            dom.style.display = 'inline-block';
        }

        dom.style.position = display.match(adjustDirect2DTableRe) ? 'absolute' : 'static';

        // floating will contain digits that appears after the decimal point
        // if height or width are set to auto we fallback to msTransformOrigin calculation
        floating = (parseFloat(me.getStyle(dimension)) || parseFloat(dom.currentStyle.msTransformOrigin.split(' ')[originIndex]) * 2) % 1;

        dom.style.position = inlinePosition;

        if (display === 'inline') {
            dom.style.display = inlineDisplay;
        }

        return floating;
    },

    /**
     * Store the current overflow setting and clip overflow on the element - use {@link #unclip} to remove
     * @return {Ext.dom.Element} this
     */
    clip : function() {
        var me = this,
            data = (me.$cache || me.getCache()).data;

        if (!data[ISCLIPPED]) {
            data[ISCLIPPED] = true;
            data[ORIGINALCLIP] = {
                o: me.getStyle(OVERFLOW),
                x: me.getStyle(OVERFLOWX),
                y: me.getStyle(OVERFLOWY)
            };
            me.setStyle(OVERFLOW, HIDDEN);
            me.setStyle(OVERFLOWX, HIDDEN);
            me.setStyle(OVERFLOWY, HIDDEN);
        }
        return me;
    },

    /**
     * Return clipping (overflow) to original clipping before {@link #clip} was called
     * @return {Ext.dom.Element} this
     */
    unclip : function() {
        var me = this,
            data = (me.$cache || me.getCache()).data,
            clip;

        if (data[ISCLIPPED]) {
            data[ISCLIPPED] = true;
            clip = data[ORIGINALCLIP];
            if (clip.o) {
                me.setStyle(OVERFLOW, clip.o);
            }
            if (clip.x) {
                me.setStyle(OVERFLOWX, clip.x);
            }
            if (clip.y) {
                me.setStyle(OVERFLOWY, clip.y);
            }
        }
        return me;
    },

    /**
     * Returns an object with properties matching the styles requested as computed by the browser based upon applicable
     * CSS rules as well as inline styles.
     *
     * For example:
     *
     *     el.getStyles('color', 'font-size', 'width');
     *
     * might return:
     *
     *     {'color': '#FFFFFF', 'font-size': '13px', 'width': '100px'}
     *
     * If ```true``` is passed as the last parameter, *inline* styles are returned instead of computed styles.
     *
     * @param {String...} styles A variable number of style names
     * @return {Object} The style object
     */
    getStyles : function() {
        var styles = {},
            len = arguments.length,
            i = 0, style,
            inline = false;

        if (arguments[len - 1] === true) {
            --len;
            inline = true;
        }
        for (; i < len; ++i) {
            style = arguments[i];
            styles[style] = inline ? this.dom.style[Ext.Element.normalize(style)] : this.getStyle(style);
        }
        return styles;
    },

    /**
     * Wraps the specified element with a special 9 element markup/CSS block that renders by default as
     * a gray container with a gradient background, rounded corners and a 4-way shadow.
     *
     * This special markup is used throughout Ext when box wrapping elements ({@link Ext.button.Button},
     * {@link Ext.panel.Panel} when {@link Ext.panel.Panel#frame frame=true}, {@link Ext.window.Window}).
     * The markup is of this form:
     *
     *     Ext.dom.Element.boxMarkup =
     *     '<div class="{0}-tl"><div class="{0}-tr"><div class="{0}-tc"></div></div></div>
     *     <div class="{0}-ml"><div class="{0}-mr"><div class="{0}-mc"></div></div></div>
     *     <div class="{0}-bl"><div class="{0}-br"><div class="{0}-bc"></div></div></div>';
     *
     * Example usage:
     *
     *     // Basic box wrap
     *     Ext.get("foo").boxWrap();
     *
     *     // You can also add a custom class and use CSS inheritance rules to customize the box look.
     *     // 'x-box-blue' is a built-in alternative -- look at the related CSS definitions as an example
     *     // for how to create a custom box wrap style.
     *     Ext.get("foo").boxWrap().addCls("x-box-blue");
     *
     * @param {String} [class='x-box'] A base CSS class to apply to the containing wrapper element.
     * Note that there are a number of CSS rules that are dependent on this name to make the overall effect work,
     * so if you supply an alternate base class, make sure you also supply all of the necessary rules.
     * @return {Ext.dom.Element} The outermost wrapping element of the created box structure.
     */
    boxWrap : function(cls) {
        cls = cls || Ext.baseCSSPrefix + 'box';
        var el = Ext.get(this.insertHtml("beforeBegin", "<div class='" + cls + "'>" + Ext.String.format(Element.boxMarkup, cls) + "</div>"));
        Ext.DomQuery.selectNode('.' + cls + '-mc', el.dom).appendChild(this.dom);
        return el;
    },

    /**
     * Returns either the offsetHeight or the height of this element based on CSS height adjusted by padding or borders
     * when needed to simulate offsetHeight when offsets aren't available. This may not work on display:none elements
     * if a height has not been set using CSS.
     * @return {Number}
     */
    getComputedHeight : function() {
        var me = this,
            h = Math.max(me.dom.offsetHeight, me.dom.clientHeight);
        if (!h) {
            h = parseFloat(me.getStyle('height')) || 0;
            if (!me.isBorderBox()) {
                h += me.getFrameWidth('tb');
            }
        }
        return h;
    },

    /**
     * Returns either the offsetWidth or the width of this element based on CSS width adjusted by padding or borders
     * when needed to simulate offsetWidth when offsets aren't available. This may not work on display:none elements
     * if a width has not been set using CSS.
     * @return {Number}
     */
    getComputedWidth : function() {
        var me = this,
            w = Math.max(me.dom.offsetWidth, me.dom.clientWidth);

        if (!w) {
            w = parseFloat(me.getStyle('width')) || 0;
            if (!me.isBorderBox()) {
                w += me.getFrameWidth('lr');
            }
        }
        return w;
    },

    /**
     * Returns the sum width of the padding and borders for the passed "sides". See getBorderWidth()
     * for more information about the sides.
     * @param {String} sides
     * @return {Number}
     */
    getFrameWidth : function(sides, onlyContentBox) {
        return onlyContentBox && this.isBorderBox() ? 0 : (this.getPadding(sides) + this.getBorderWidth(sides));
    },

    /**
     * Sets up event handlers to add and remove a css class when the mouse is over this element
     * @param {String} className
     * @return {Ext.dom.Element} this
     */
    addClsOnOver : function(className) {
        var dom = this.dom;
        this.hover(
                function() {
                    Ext.fly(dom, INTERNAL).addCls(className);
                },
                function() {
                    Ext.fly(dom, INTERNAL).removeCls(className);
                }
                );
        return this;
    },

    /**
     * Sets up event handlers to add and remove a css class when this element has the focus
     * @param {String} className
     * @return {Ext.dom.Element} this
     */
    addClsOnFocus : function(className) {
        var me = this,
                dom = me.dom;
        me.on("focus", function() {
            Ext.fly(dom, INTERNAL).addCls(className);
        });
        me.on("blur", function() {
            Ext.fly(dom, INTERNAL).removeCls(className);
        });
        return me;
    },

    /**
     * Sets up event handlers to add and remove a css class when the mouse is down and then up on this element (a click effect)
     * @param {String} className
     * @return {Ext.dom.Element} this
     */
    addClsOnClick : function(className) {
        var dom = this.dom;
        this.on("mousedown", function() {
            Ext.fly(dom, INTERNAL).addCls(className);
            var d = Ext.getDoc(),
                    fn = function() {
                        Ext.fly(dom, INTERNAL).removeCls(className);
                        d.removeListener("mouseup", fn);
                    };
            d.on("mouseup", fn);
        });
        return this;
    },

    /**
     * Returns the dimensions of the element available to lay content out in.
     *
     * getStyleSize utilizes prefers style sizing if present, otherwise it chooses the larger of offsetHeight/clientHeight and
     * offsetWidth/clientWidth. To obtain the size excluding scrollbars, use getViewSize.
     *
     * Sizing of the document body is handled at the adapter level which handles special cases for IE and strict modes, etc.
     * 
     * @return {Object} Object describing width and height.
     * @return {Number} return.width
     * @return {Number} return.height
     */
    getStyleSize : function() {
        var me = this,
                doc = document,
                d = this.dom,
                isDoc = (d == doc || d == doc.body),
                s = d.style,
                w, h;

        // If the body, use static methods
        if (isDoc) {
            return {
                width : Element.getViewWidth(),
                height : Element.getViewHeight()
            };
        }
        // Use Styles if they are set
        if (s.width && s.width != 'auto') {
            w = parseFloat(s.width);
            if (me.isBorderBox()) {
                w -= me.getFrameWidth('lr');
            }
        }
        // Use Styles if they are set
        if (s.height && s.height != 'auto') {
            h = parseFloat(s.height);
            if (me.isBorderBox()) {
                h -= me.getFrameWidth('tb');
            }
        }
        // Use getWidth/getHeight if style not set.
        return {width: w || me.getWidth(true), height: h || me.getHeight(true)};
    },

    /**
     * Enable text selection for this element (normalized across browsers)
     * @return {Ext.Element} this
     */
    selectable : function() {
        var me = this;
        me.dom.unselectable = "off";
        // Prevent it from bubles up and enables it to be selectable
        me.on('selectstart', function (e) {
            e.stopPropagation();
            return true;
        });
        me.applyStyles("-moz-user-select: text; -khtml-user-select: text;");
        me.removeCls(Ext.baseCSSPrefix + 'unselectable');
        return me;
    },
        
    /**
     * Disables text selection for this element (normalized across browsers)
     * @return {Ext.dom.Element} this
     */
    unselectable : function() {
        var me = this;
        me.dom.unselectable = "on";

        me.swallowEvent("selectstart", true);
        me.applyStyles("-moz-user-select:-moz-none;-khtml-user-select:none;");
        me.addCls(Ext.baseCSSPrefix + 'unselectable');

        return me;
    }
});

})();

// This reduces the lookup of 'me.styleHooks' by one hop in the prototype chain. It is
// the same object.
Ext.dom.Element.prototype.styleHooks = Ext.dom.AbstractElement.prototype.styleHooks;

Ext.onReady(function () {
    var opacityRe = /alpha\(opacity=(.*)\)/i,
        trimRe = /^\s+|\s+$/g;

    // Ext.supports flags are not populated until onReady...
    if (!Ext.supports.Opacity && Ext.isIE) {
        Ext.dom.Element.prototype.styleHooks.opacity = {
            name: 'opacity',
            get: function (dom) {
                var filter = dom.style.filter,
                    match, opacity;
                if (filter.match) {
                    match = filter.match(opacityRe);
                    if (match) {
                        opacity = parseFloat(match[1]);
                        if (!isNaN(opacity)) {
                            return opacity ? opacity / 100 : 0;
                        }
                    }
                }
                return 1;
            },
            set: function (dom, value) {
                var style = dom.style,
                    val = style.filter.replace(opacityRe, '').replace(trimRe, '');

                style.zoom = 1; // ensure dom.hasLayout

                // value can be a number or '' or null... so treat falsey as no opacity
                if (typeof(value) == 'number' && value >= 0 && value < 1) {
                    value *= 100;
                    style.filter = val + (val.length ? ' ' : '') + 'alpha(opacity='+value+')';
                } else {
                    style.filter = val;
                }
            }
        };
    }
    // else there is no work around for the lack of opacity support. Should not be a
    // problem given that this has been supported for a long time now...
});

// override getStyle for border-*-width
if (Ext.isIEQuirks || Ext.isIE && Ext.ieVersion <= 8){
    Ext.Array.forEach('Top Right Bottom Left'.split(' '), function(side){
        var borderWidth = 'border' + side + 'Width',
            borderStyle = 'border' + side + 'Style';

        Ext.dom.Element.prototype.styleHooks['border-' + side.toLowerCase() + '-width'] = {
            name: borderWidth,
            get: function (dom) {
                var currentStyle = dom.currentStyle;
                if (currentStyle[borderStyle] == 'none'){
                    return '0px';
                }
                return currentStyle[borderWidth];
            }
        };
    });
}
