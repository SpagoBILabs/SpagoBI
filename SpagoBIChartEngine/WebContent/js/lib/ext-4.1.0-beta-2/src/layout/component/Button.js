/**
 * Component layout for buttons
 * @private
 */
Ext.define('Ext.layout.component.Button', {

    /* Begin Definitions */

    alias: ['layout.button'],

    extend: 'Ext.layout.component.Auto',

    /* End Definitions */

    type: 'button',

    cellClsRE: /-btn-(tl|br)\b/,
    htmlRE: /<.*>/,

    constructor: function () {
        this.callParent(arguments);

        this.hackWidth = (Ext.isIE6 || Ext.isIE7) && Ext.isStrict;
    },

    // TODO - use last run results if text has not changed?

    beginLayout: function (ownerContext) {
        this.callParent(arguments);

        this.cacheTargetInfo(ownerContext);
    },

    beginLayoutCycle: function(ownerContext) {
        var empty = '',
            owner = this.owner;

        this.callParent(arguments);

        owner.btnInnerEl.setStyle('overflow', empty);

        // Clear all element widths
        owner.el.setStyle('width', empty);
        owner.btnEl.setStyle('width', empty);
        owner.btnInnerEl.setStyle('width', empty);
        owner.btnIconEl.setStyle('width', empty);
    },

    calculateOwnerHeightFromContentHeight: function (ownerContext, contentHeight) {
        return contentHeight;
    },

    calculateOwnerWidthFromContentWidth: function (ownerContext, contentWidth) {
        return contentWidth;
    },

    measureContentWidth: function (ownerContext) {
        var me = this,
            owner = me.owner,
            btnEl = owner.btnEl,
            btnInnerEl = owner.btnInnerEl,
            btnFrameWidth, metrics, sizeIconEl, width, btnElContext, btnInnerElContext;

        // In IE7 strict mode button elements with width:auto get strange extra side margins within
        // the wrapping table cell, but they go away if the width is explicitly set. So we measure
        // the size of the text and set the width to match.
        if (owner.text && me.hackWidth && btnEl && btnEl.getWidth() > 20) {
            btnFrameWidth = me.btnFrameWidth;
            metrics = Ext.util.TextMetrics.measure(btnInnerEl, owner.text);
            width = metrics.width + btnFrameWidth + me.adjWidth;

            btnElContext = ownerContext.getEl('btnEl');
            btnInnerElContext = ownerContext.getEl('btnInnerEl');
            sizeIconEl = (owner.icon || owner.iconCls) && 
                    (owner.iconAlign == "top" || owner.iconAlign == "bottom");

            // This cheat works (barely) with publishOwnerWidth which calls setProp also
            // to publish the width. Since it is the same value we set here, the dirty bit
            // we set true will not be cleared by publishOwnerWidth.
            ownerContext.setWidth(width); // not setWidth (no framing)

            btnElContext.setWidth(metrics.width + btnFrameWidth);
            btnInnerElContext.setWidth(metrics.width + btnFrameWidth);

            if (sizeIconEl) {
                owner.btnIconEl.setWidth(metrics.width + btnFrameWidth);
            }
        } else {
            width = ownerContext.el.getWidth();
        }

        return width;
    },

    measureContentHeight: function (ownerContext) {
        var me = this,
            owner = me.owner,
            btnInnerEl = owner.btnInnerEl,
            height;

        if (owner.vertical) {
            height = Ext.util.TextMetrics.measure(btnInnerEl, owner.text).width;
            height += me.btnFrameHeight + me.adjHeight;

            // Vertical buttons need height explicitly set
            ownerContext.setHeight(height, /*dirty=*/true, /*force=*/true);
        } else {
            height = ownerContext.el.getHeight();
        }

        return height;
    },

    publishInnerHeight: function(ownerContext, height) {
        var me = this,
            owner = me.owner,
            isNum = Ext.isNumber,
            btnItem = ownerContext.getEl('btnEl'),
            btnInnerEl = owner.btnInnerEl,
            btnInnerItem = ownerContext.getEl('btnInnerEl'),
            btnHeight = isNum(height) ? height - me.adjHeight : height,
            btnFrameHeight = me.btnFrameHeight,
            text = owner.getText(),
            textHeight;

        btnItem.setHeight(btnHeight);
        btnInnerItem.setHeight(btnHeight);

        // Only need the line-height setting for regular, horizontal Buttons
        if (!owner.vertical && btnHeight >= 0) {
            btnInnerItem.setProp('line-height', btnHeight - btnFrameHeight + 'px');
        }

        // Button text may contain markup that would force it to wrap to more than one line (e.g. 'Button<br>Label').
        // When this happens, we cannot use the line-height set above for vertical centering; we instead reset the
        // line-height to normal, measure the rendered text height, and add padding-top to center the text block
        // vertically within the button's height. This is more expensive than the basic line-height approach so
        // we only do it if the text contains markup.
        if (text && me.htmlRE.test(text)) {
            btnInnerItem.setProp('line-height', 'normal');
            textHeight = Ext.util.TextMetrics.measure(btnInnerEl, text).height;
            btnInnerItem.setProp('padding-top',
                me.btnFrameTop + Math.max(btnInnerEl.getHeight() - btnFrameHeight - textHeight, 0) / 2);
            btnInnerItem.setHeight(btnHeight);
        }
    },

    publishInnerWidth: function(ownerContext, width) {
        var me = this,
            isNum = Ext.isNumber,
            btnItem = ownerContext.getEl('btnEl'),
            btnInnerItem = ownerContext.getEl('btnInnerEl'),
            btnWidth = isNum(width) ? width - me.adjWidth : width;

        btnItem.setWidth(btnWidth);
        btnInnerItem.setWidth(btnWidth);
    },

    cacheTargetInfo: function(ownerContext) {
        var me = this,
            padding, frameSize, btnWrapPadding, innerFrameSize;

        if (!('adjWidth' in me)) {
            padding = ownerContext.getPaddingInfo();
            frameSize = ownerContext.getFrameInfo();
            btnWrapPadding = ownerContext.getEl('btnWrap').getPaddingInfo();
            innerFrameSize = ownerContext.getEl('btnInnerEl').getPaddingInfo();

            Ext.apply(me, {
                // Width adjustment must take into account the arrow area. The btnWrap is the <em> which has padding to accommodate the arrow.
                adjWidth       : btnWrapPadding.width + frameSize.width + padding.width,
                adjHeight      : btnWrapPadding.height + frameSize.height + padding.height,
                btnFrameWidth  : innerFrameSize.width,
                btnFrameHeight : innerFrameSize.height,
                btnFrameTop    : innerFrameSize.top
            });
        }

        me.callParent(arguments);
    }
});
