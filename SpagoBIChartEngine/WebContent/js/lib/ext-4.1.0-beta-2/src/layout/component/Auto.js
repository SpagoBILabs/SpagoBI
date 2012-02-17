/**
 * The class is the default component layout for {@link Ext.Component} when no explicit
 * `{@link Ext.Component#componentLayout componentLayout}` is configured.
 *
 * This class uses template methods to perform the individual aspects of measurement,
 * calculation and publication of results. The methods called depend on the component's
 * {@link Ext.AbstractComponent#getSizeModel size model}.
 * 
 * ## configured / calculated
 *
 * In either of these size models, the dimension of the outer element is of a known size.
 * The size is found in the `ownerContext` (the {@link Ext.layout.ContextItem} for the owner
 * component) as either "width" or "height". This value, if available, is passed to the
 * {@link #publishInnerWidth} or {@link #publishInnerHeight} method, respectively.
 * 
 * ## shrinkWrap
 *
 * When a dimension uses the `shrinkWrap` size model, that means the content is measured,
 * then the outer (owner) size is calculated and published.
 * 
 * For example, for a shrinkWrap width, the following sequence of calls are made:
 * 
 * * {@link Ext.layout.component.Component#measureContentWidth}
 * * {@link #publishOwnerWidth}
 *    * {@link #calculateOwnerWidthFromContentWidth}
 *    * {@link #publishInnerWidth} (in the event of hitting a min/maxWidth constraint)
 *
 * ## natural
 *
 * When a dimension uses the `natural` size model, the measurement is made on the outer
 * (owner) element. This size is then used to determine the content area in much the same
 * way as if the outer element had a `configured` or `calculated` size model.
 * 
 * * {@link Ext.layout.component.Component#measureOwnerWidth}
 * * {@link #publishInnerWidth}
 *
 * @protected
 */
Ext.define('Ext.layout.component.Auto', {

    /* Begin Definitions */

    alias: 'layout.autocomponent',

    extend: 'Ext.layout.component.Component',

    /* End Definitions */

    type: 'autocomponent',

    /**
     * @cfg {Boolean} [setHeightInDom=false]
     * @protected
     * When publishing height of an auto Component, it is usually not written to the DOM.
     * Setting this to `true` overrides this behaviour.
     */
    setHeightInDom: false,

    /**
     * @cfg {Boolean} [setWidthInDom=false]
     * @protected
     * When publishing width of an auto Component, it is usually not written to the DOM.
     * Setting this to `true` overrides this behaviour.
     */
    setWidthInDom: false,

    waitForOuterHeightInDom: false,
    waitForOuterWidthInDom: false,

    calculate: function(ownerContext) {
        var me = this,
            measurement = me.measureAutoDimensions(ownerContext),
            heightModel = ownerContext.heightModel,
            widthModel = ownerContext.widthModel,
            width, height;

        // It is generally important to process widths before heights, since widths can
        // often effect heights...
        if (measurement.gotWidth) {
            if (widthModel.shrinkWrap) {
                me.publishOwnerWidth(ownerContext, measurement.contentWidth);
            } else if (me.publishInnerWidth) {
                me.publishInnerWidth(ownerContext, measurement.width);
            }
        } else if (!widthModel.auto && me.publishInnerWidth) {
            width = me.waitForOuterWidthInDom ? ownerContext.getDomProp('width')
                        : ownerContext.getProp('width');
            if (width === undefined) {
                me.done = false;
            } else {
                me.publishInnerWidth(ownerContext, width);
            }
        }

        if (measurement.gotHeight) {
            if (heightModel.shrinkWrap) {
                me.publishOwnerHeight(ownerContext, measurement.contentHeight);
            } else if (me.publishInnerHeight) {
                me.publishInnerHeight(ownerContext, measurement.height);
            }
        } else if (!heightModel.auto && me.publishInnerHeight) {
            height = me.waitForOuterHeightInDom ? ownerContext.getDomProp('height')
                        : ownerContext.getProp('height');
            if (height === undefined) {
                me.done = false;
            } else {
               me.publishInnerHeight(ownerContext, height);
            }
        }

        if (!measurement.gotAll) {
            me.done = false;
        }
    },

    calculateOwnerHeightFromContentHeight: function (ownerContext, contentHeight) {
        return contentHeight + ownerContext.getFrameInfo().height;
    },

    calculateOwnerWidthFromContentWidth: function (ownerContext, contentWidth) {
        return contentWidth + ownerContext.getFrameInfo().width;
    },

    publishOwnerHeight: function (ownerContext, contentHeight) {
        var me = this,
            owner = me.owner,
            height = me.calculateOwnerHeightFromContentHeight(ownerContext, contentHeight),
            constrainedHeight, dirty;

        if (isNaN(height)) {
            me.done = false;
        } else {
            constrainedHeight = Ext.Number.constrain(height, owner.minHeight, owner.maxHeight);

            if (constrainedHeight == height) {
                dirty = me.setHeightInDom;
            } else {
                ownerContext.heightModel = me.sizeModels.configured;
                height = constrainedHeight;
                if (me.publishInnerHeight) {
                    me.publishInnerHeight(ownerContext, height);
                }
            }
            
            ownerContext.setHeight(height, dirty);
        }
    },

    publishOwnerWidth: function (ownerContext, contentWidth) {
        var me = this,
            owner = me.owner,
            width = me.calculateOwnerWidthFromContentWidth(ownerContext, contentWidth),
            constrainedWidth, dirty;

        if (isNaN(width)) {
            me.done = false;
        } else {
            constrainedWidth = Ext.Number.constrain(width, owner.minWidth, owner.maxWidth);

            if (constrainedWidth == width) {
                dirty = me.setWidthInDom;
            } else {
                ownerContext.widthModel = me.sizeModels.configured;
                width = constrainedWidth;
                if (me.publishInnerWidth) {
                    me.publishInnerWidth(ownerContext, width);
                }
            }

            ownerContext.setWidth(width, dirty);
        }
    }
});
