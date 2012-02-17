/**
 * @class Ext.dom.Element
 */
(function() {

var Element = Ext.dom.Element,
    LEFT = "left",
    RIGHT = "right",
    TOP = "top",
    BOTTOM = "bottom",
    POSITION = "position",
    STATIC = "static",
    RELATIVE = "relative",
    AUTO = "auto",
    ZINDEX = "z-index";

Element.override({

    getX: function() {
        return Element.getX(this.dom);
    },

    getY: function() {
        return Element.getY(this.dom);
    },

    /**
      * Gets the current position of the element based on page coordinates.
      * Element must be part of the DOM tree to have page coordinates
      * (display:none or elements not appended return false).
      * @return {Number[]} The XY position of the element
      */
    getXY: function() {
        return Element.getXY(this.dom);
    },

    /**
      * Returns the offsets of this element from the passed element. Both element must be part
      * of the DOM tree and not have display:none to have page coordinates.
      * @param {String/HTMLElement/Ext.Element} element The element to get the offsets from.
      * @return {Number[]} The XY page offsets (e.g. `[100, -200]`)
      */
    getOffsetsTo : function(el){
        var o = this.getXY(),
                e = Ext.fly(el, '_internal').getXY();
        return [o[0] - e[0],o[1] - e[1]];
    },

    setX: function(x, animate) {
        return this.setXY([x, this.getY()], animate);
    },

    setY: function(y, animate) {
        return this.setXY([this.getX(), y], animate);
    },

    setLeft: function(left) {
        this.setStyle(LEFT, this.addUnits(left));
        return this;
    },

    setTop: function(top) {
        this.setStyle(TOP, this.addUnits(top));
        return this;
    },

    setRight: function(right) {
        this.setStyle(RIGHT, this.addUnits(right));
        return this;
    },

    setBottom: function(bottom) {
        this.setStyle(BOTTOM, this.addUnits(bottom));
        return this;
    },

    /**
     * Sets the position of the element in page coordinates, regardless of how the element
     * is positioned. The element must be part of the DOM tree to have page coordinates
     * (`display:none` or elements not appended return false).
     * @param {Number[]} pos Contains X & Y [x, y] values for new position (coordinates are page-based)
     * @param {Boolean/Object} [animate] True for the default animation, or a standard Element
     * animation config object
     * @return {Ext.Element} this
     */
    setXY: function(pos, animate) {
        var me = this;
        if (!animate || !me.anim) {
            Element.setXY(me.dom, pos);
        }
        else {
            if (!Ext.isObject(animate)) {
                animate = {};
            }
            me.animate(Ext.applyIf({ to: { x: pos[0], y: pos[1] } }, animate));
        }
        return me;
    },

    getLeft: function(local) {
        return !local ? this.getX() : parseInt(this.getStyle(LEFT), 10) || 0;
    },

    getRight: function(local) {
        var me = this;
        return !local ? me.getX() + me.getWidth() : (me.getLeft(true) + me.getWidth()) || 0;
    },

    getTop: function(local) {
        return !local ? this.getY() : parseInt(this.getStyle(TOP), 10) || 0;
    },

    getBottom: function(local) {
        var me = this;
        return !local ? me.getY() + me.getHeight() : (me.getTop(true) + me.getHeight()) || 0;
    },

    translatePoints: function(x, y) {
        if (Ext.isArray(x)) {
            y = x[1];
            x = x[0];
        }
        var me = this,
                relative = me.isStyle(POSITION, RELATIVE),
                o = me.getXY(),
                left = parseInt(me.getStyle(LEFT), 10),
                top = parseInt(me.getStyle(TOP), 10);

        if (!Ext.isNumber(left)) {
            left = relative ? 0 : me.dom.offsetLeft;
        }
        if (!Ext.isNumber(top)) {
            top = relative ? 0 : me.dom.offsetTop;
        }
        left = (Ext.isNumber(x)) ? x - o[0] + left : undefined;
        top = (Ext.isNumber(y)) ? y - o[1] + top : undefined;
        return {
            left: left,
            top: top
        };
    },



    setBox: function(box, adjust, animate) {
        var me = this,
                w = box.width,
                h = box.height;
        if ((adjust && !me.autoBoxAdjust) && !me.isBorderBox()) {
            w -= (me.getBorderWidth("lr") + me.getPadding("lr"));
            h -= (me.getBorderWidth("tb") + me.getPadding("tb"));
        }
        me.setBounds(box.x, box.y, w, h, animate);
        return me;
    },

    getBox: function(contentBox, local) {
        var me = this,
                xy,
                left,
                top,
                getBorderWidth = me.getBorderWidth,
                getPadding = me.getPadding,
                l, r, t, b, w, h, bx;

        if (!local) {
            xy = me.getXY();
        } else {
            left = parseInt(me.getStyle("left"), 10) || 0;
            top = parseInt(me.getStyle("top"), 10) || 0;
            xy = [left, top];
        }

        w = me.getWidth();
        h = me.getHeight();

        if (!contentBox) {
            bx = {
                x: xy[0],
                y: xy[1],
                0: xy[0],
                1: xy[1],
                width: w,
                height: h
            };
        } else {
            l = getBorderWidth.call(me, "l") + getPadding.call(me, "l");
            r = getBorderWidth.call(me, "r") + getPadding.call(me, "r");
            t = getBorderWidth.call(me, "t") + getPadding.call(me, "t");
            b = getBorderWidth.call(me, "b") + getPadding.call(me, "b");
            bx = {
                x: xy[0] + l,
                y: xy[1] + t,
                0: xy[0] + l,
                1: xy[1] + t,
                width: w - (l + r),
                height: h - (t + b)
            };
        }
        bx.right = bx.x + bx.width;
        bx.bottom = bx.y + bx.height;
        return bx;
    },

    getPageBox: function(getRegion) {
        var me = this,
                el = me.dom,
                isDoc = el === document.body,
                w = isDoc ? Ext.dom.AbstractElement.getViewWidth() : el.offsetWidth,
                h = isDoc ? Ext.dom.AbstractElement.getViewHeight() : el.offsetHeight,
                xy = me.getXY(),
                t = xy[1],
                r = xy[0] + w,
                b = xy[1] + h,
                l = xy[0];

        if (getRegion) {
            return new Ext.util.Region(t, r, b, l);
        }
        else {
            return {
                left: l,
                top: t,
                width: w,
                height: h,
                right: r,
                bottom: b
            };
        }
    },

    /**
     * Sets the position of the element in page coordinates, regardless of how the element
     * is positioned. The element must be part of the DOM tree to have page coordinates
     * (`display:none` or elements not appended return false).
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {Boolean/Object} [animate] True for the default animation, or a standard Element
     * animation config object
     * @return {Ext.dom.AbstractElement} this
     */
    setLocation : function(x, y, animate) {
        return this.setXY([x, y], animate);
    },

    /**
     * Sets the position of the element in page coordinates, regardless of how the element
     * is positioned. The element must be part of the DOM tree to have page coordinates
     * (`display:none` or elements not appended return false).
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {Boolean/Object} [animate] True for the default animation, or a standard Element
     * animation config object
     * @return {Ext.dom.AbstractElement} this
     */
    moveTo : function(x, y, animate) {
        return this.setXY([x, y], animate);
    },

    /**
     * Initializes positioning on this element. If a desired position is not passed, it will make the
     * the element positioned relative IF it is not already positioned.
     * @param {String} [pos] Positioning to use "relative", "absolute" or "fixed"
     * @param {Number} [zIndex] The zIndex to apply
     * @param {Number} [x] Set the page X position
     * @param {Number} [y] Set the page Y position
     */
    position : function(pos, zIndex, x, y) {
        var me = this;

        if (!pos && me.isStyle(POSITION, STATIC)) {
            me.setStyle(POSITION, RELATIVE);
        } else if (pos) {
            me.setStyle(POSITION, pos);
        }
        if (zIndex) {
            me.setStyle(ZINDEX, zIndex);
        }
        if (x || y) {
            me.setXY([x || false, y || false]);
        }
    },

    /**
     * Clears positioning back to the default when the document was loaded.
     * @param {String} [value=''] The value to use for the left, right, top, bottom. You could use 'auto'.
     * @return {Ext.dom.AbstractElement} this
     */
    clearPositioning : function(value) {
        value = value || '';
        this.setStyle({
            left : value,
            right : value,
            top : value,
            bottom : value,
            "z-index" : "",
            position : STATIC
        });
        return this;
    },

    /**
     * Gets an object with all CSS positioning properties. Useful along with #setPostioning to get
     * snapshot before performing an update and then restoring the element.
     * @return {Object}
     */
    getPositioning : function() {
        var l = this.getStyle(LEFT);
        var t = this.getStyle(TOP);
        return {
            "position" : this.getStyle(POSITION),
            "left" : l,
            "right" : l ? "" : this.getStyle(RIGHT),
            "top" : t,
            "bottom" : t ? "" : this.getStyle(BOTTOM),
            "z-index" : this.getStyle(ZINDEX)
        };
    },

    /**
     * Set positioning with an object returned by #getPositioning.
     * @param {Object} posCfg
     * @return {Ext.dom.AbstractElement} this
     */
    setPositioning : function(pc) {
        var me = this,
                style = me.dom.style;

        me.setStyle(pc);

        if (pc.right == AUTO) {
            style.right = "";
        }
        if (pc.bottom == AUTO) {
            style.bottom = "";
        }

        return me;
    },

    /**
     * Move this element relative to its current position.
     * @param {String} direction Possible values are:
     *
     * - `"l"` (or `"left"`)
     * - `"r"` (or `"right"`)
     * - `"t"` (or `"top"`, or `"up"`)
     * - `"b"` (or `"bottom"`, or `"down"`)
     *
     * @param {Number} distance How far to move the element in pixels
     * @param {Boolean/Object} [animate] true for the default animation or a standard Element
     * animation config object
     */
    move: function(direction, distance, animate) {
        var me = this,
            xy = me.getXY(),
            x = xy[0],
            y = xy[1],
            left = [x - distance, y],
            right = [x + distance, y],
            top = [x, y - distance],
            bottom = [x, y + distance],
            hash = {
                l: left,
                left: left,
                r: right,
                right: right,
                t: top,
                top: top,
                up: top,
                b: bottom,
                bottom: bottom,
                down: bottom
            };

        direction = direction.toLowerCase();
        me.moveTo(hash[direction][0], hash[direction][1], animate);
    },

    /**
     * Conveniently sets left and top adding default units.
     * @param {String} left The left CSS property value
     * @param {String} top The top CSS property value
     * @return {Ext.dom.Element} this
     */
    setLeftTop: function(left, top) {
        var style = this.dom.style;

        style.left = Element.addUnits(left);
        style.top = Element.addUnits(top);

        return this;
    },

    /**
     * Returns the region of this element.
     * The element must be part of the DOM tree to have a region
     * (display:none or elements not appended return false).
     * @return {Ext.util.Region} A Region containing "top, left, bottom, right" member data.
     */
    getRegion: function() {
        return this.getPageBox(true);
    },

    /**
     * Returns the **content** region of this element. That is the region within the borders and padding.
     * @return {Ext.util.Region} A Region containing "top, left, bottom, right" member data.
     */
    getViewRegion: function() {
        var me = this,
            isBody = me.dom === document.body,
            scroll, pos, top, left, width, height;

        // For the body we want to do some special logic
        if (isBody) {
            scroll = me.getScroll();
            left = scroll.left;
            top = scroll.top;
            width = Ext.dom.AbstractElement.getViewportWidth();
            height = Ext.dom.AbstractElement.getViewportHeight();
        }
        else {
            pos = me.getXY();
            left = pos[0] + me.getBorderWidth('l') + me.getPadding('l');
            top = pos[1] + me.getBorderWidth('t') + me.getPadding('t');
            width = me.getWidth(true);
            height = me.getHeight(true);
        }

        return new Ext.util.Region(top, left + width, top + height, left);
    },

    /**
     * Sets the element's position and size in one shot. If animation is true then width, height,
     * x and y will be animated concurrently.
     *
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {Number/String} width The new width. This may be one of:
     *
     * - A Number specifying the new width in this Element's {@link #defaultUnit}s (by default, pixels)
     * - A String used to set the CSS width style. Animation may **not** be used.
     *
     * @param {Number/String} height The new height. This may be one of:
     *
     * - A Number specifying the new height in this Element's {@link #defaultUnit}s (by default, pixels)
     * - A String used to set the CSS height style. Animation may **not** be used.
     *
     * @param {Boolean/Object} [animate] true for the default animation or a standard Element
     * animation config object
     *
     * @return {Ext.dom.AbstractElement} this
     */
    setBounds: function(x, y, width, height, animate) {
        var me = this;
        if (!animate || !me.anim) {
            me.setSize(width, height);
            me.setLocation(x, y);
        } else {
            if (!Ext.isObject(animate)) {
                animate = {};
            }
            me.animate(Ext.applyIf({
                to: {
                    x: x,
                    y: y,
                    width: me.adjustWidth(width),
                    height: me.adjustHeight(height)
                }
            }, animate));
        }
        return me;
    },

    /**
     * Sets the element's position and size the specified region. If animation is true then width, height,
     * x and y will be animated concurrently.
     *
     * @param {Ext.util.Region} region The region to fill
     * @param {Boolean/Object} [animate] true for the default animation or a standard Element
     * animation config object
     * @return {Ext.dom.AbstractElement} this
     */
    setRegion: function(region, animate) {
        return this.setBounds(region.left, region.top, region.right - region.left, region.bottom - region.top, animate);
    }
});

})();

