/**
 * Given a component hierarchy of this:
 *
 *      {
 *          xtype: 'panel',
 *          id: 'ContainerA',
 *          layout: 'hbox',
 *          renderTo: Ext.getBody(),
 *          items: [
 *              {
 *                  id: 'ContainerB',
 *                  xtype: 'container',
 *                  items: [
 *                      { id: 'ComponentA' }
 *                  ]
 *              }
 *          ]
 *      }
 *
 * The rendering of the above proceeds roughly like this:
 *
 *  - ContainerA's initComponent calls #render passing the `renderTo` property as the
 *    container argument.
 *  - `render` calls the `getRenderTree` method to get a complete {@link Ext.DomHelper} spec.
 *  - `getRenderTree` fires the "beforerender" event and calls the #beforeRender
 *    method. Its result is obtained by calling #getElConfig.
 *  - The #getElConfig method uses the `renderTpl` and its render data as the content
 *    of the `autoEl` described element.
 *  - The result of `getRenderTree` is passed to {@link Ext.DomHelper#append}.
 *  - The `renderTpl` contains calls to render things like docked items, container items
 *    and raw markup (such as the `html` or `tpl` config properties). These calls are to
 *    methods added to the {@link Ext.XTemplate} instance by #setupRenderTpl.
 *  - The #setupRenderTpl method adds methods such as `renderItems`, `renderContent`, etc.
 *    to the template. These are directed to "doRenderItems", "doRenderContent" etc..
 *  - The #setupRenderTpl calls traverse from components to their {@link Ext.layout.Layout}
 *    object.
 *  - When a container is rendered, it also has a `renderTpl`. This is processed when the
 *    `renderContainer` method is called in the component's `renderTpl`. This call goes to
 *    Ext.layout.container.Container#doRenderContainer. This method repeats this
 *    process for all components in the container.
 *  - After the top-most component's markup is generated and placed in to the DOM, the next
 *    step is to link elements to their components and finish calling the component methods
 *    `onRender` and `afterRender` as well as fire the corresponding events.
 *  - The first step in this is to call #finishRender. This method descends the
 *    component hierarchy and calls `onRender` and fires the `render` event. These calls
 *    are delivered top-down to approximate the timing of these calls/events from previous
 *    versions.
 *  - During the pass, the component's `el` is set. Likewise, the `renderSelectors` and
 *    `childEls` are applied to capture references to the component's elements.
 *  - These calls are also made on the {@link Ext.layout.container.Container} layout to
 *    capture its elements. Both of these classes use {@link Ext.util.ElementContainer} to
 *    handle `childEls` processing.
 *  - Once this is complete, a similar pass is made by calling #finishAfterRender.
 *    This call also descends the component hierarchy, but this time the calls are made in
 *    a bottom-up order to `afterRender`.
 *
 * @private
 */
Ext.define('Ext.util.Renderable', {
    requires: [
        'Ext.dom.Element'
    ],

    frameCls: Ext.baseCSSPrefix + 'frame',

    frameIdRegex: /[\-]frame\d+[TMB][LCR]$/,

    frameElementCls: {
        tl: [],
        tc: [],
        tr: [],
        ml: [],
        mc: [],
        mr: [],
        bl: [],
        bc: [],
        br: []
    },

    frameTpl: [
        '{%this.renderDockedItems(out,values,0);%}',
        '<tpl if="top">',
            '<tpl if="left"><div id="{fgid}TL" class="{frameCls}-tl {baseCls}-tl {baseCls}-{ui}-tl<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tl</tpl></tpl>" style="background-position: {tl}; padding-left: {frameWidth}px" role="presentation"></tpl>',
                '<tpl if="right"><div id="{fgid}TR" class="{frameCls}-tr {baseCls}-tr {baseCls}-{ui}-tr<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tr</tpl></tpl>" style="background-position: {tr}; padding-right: {frameWidth}px" role="presentation"></tpl>',
                    '<div id="{fgid}TC" class="{frameCls}-tc {baseCls}-tc {baseCls}-{ui}-tc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tc</tpl></tpl>" style="background-position: {tc}; height: {frameWidth}px" role="presentation"></div>',
                '<tpl if="right"></div></tpl>',
            '<tpl if="left"></div></tpl>',
        '</tpl>',
        '<tpl if="left"><div id="{fgid}ML" class="{frameCls}-ml {baseCls}-ml {baseCls}-{ui}-ml<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-ml</tpl></tpl>" style="background-position: {ml}; padding-left: {frameWidth}px" role="presentation"></tpl>',
            '<tpl if="right"><div id="{fgid}MR" class="{frameCls}-mr {baseCls}-mr {baseCls}-{ui}-mr<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-mr</tpl></tpl>" style="background-position: {mr}; padding-right: {frameWidth}px" role="presentation"></tpl>',
                '<div id="{fgid}MC" class="{frameCls}-mc {baseCls}-mc {baseCls}-{ui}-mc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-mc</tpl></tpl>" role="presentation">',
                    '{%this.applyRenderTpl(out, values)%}',
                '</div>',
            '<tpl if="right"></div></tpl>',
        '<tpl if="left"></div></tpl>',
        '<tpl if="bottom">',
            '<tpl if="left"><div id="{fgid}BL" class="{frameCls}-bl {baseCls}-bl {baseCls}-{ui}-bl<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-bl</tpl></tpl>" style="background-position: {bl}; padding-left: {frameWidth}px" role="presentation"></tpl>',
                '<tpl if="right"><div id="{fgid}BR" class="{frameCls}-br {baseCls}-br {baseCls}-{ui}-br<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-br</tpl></tpl>" style="background-position: {br}; padding-right: {frameWidth}px" role="presentation"></tpl>',
                    '<div id="{fgid}BC" class="{frameCls}-bc {baseCls}-bc {baseCls}-{ui}-bc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-bc</tpl></tpl>" style="background-position: {bc}; height: {frameWidth}px" role="presentation"></div>',
                '<tpl if="right"></div></tpl>',
            '<tpl if="left"></div></tpl>',
        '</tpl>',
        '{%this.renderDockedItems(out,values,1);%}'
    ],

    frameTableTpl: [
        '{%this.renderDockedItems(out,values,0);%}',
        '<table><tbody>',
            '<tpl if="top">',
                '<tr>',
                    '<tpl if="left"><td id="{fgid}TL" class="{frameCls}-tl {baseCls}-tl {baseCls}-{ui}-tl<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tl</tpl></tpl>" style="background-position: {tl}; padding-left:{frameWidth}px" role="presentation"></td></tpl>',
                    '<td id="{fgid}TC" class="{frameCls}-tc {baseCls}-tc {baseCls}-{ui}-tc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tc</tpl></tpl>" style="background-position: {tc}; height: {frameWidth}px" role="presentation"></td>',
                    '<tpl if="right"><td id="{fgid}TR" class="{frameCls}-tr {baseCls}-tr {baseCls}-{ui}-tr<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-tr</tpl></tpl>" style="background-position: {tr}; padding-left: {frameWidth}px" role="presentation"></td></tpl>',
                '</tr>',
            '</tpl>',
            '<tr>',
                '<tpl if="left"><td id="{fgid}ML" class="{frameCls}-ml {baseCls}-ml {baseCls}-{ui}-ml<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-ml</tpl></tpl>" style="background-position: {ml}; padding-left: {frameWidth}px" role="presentation"></td></tpl>',
                '<td id="{fgid}MC" class="{frameCls}-mc {baseCls}-mc {baseCls}-{ui}-mc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-mc</tpl></tpl>" style="background-position: 0 0;" role="presentation">',
                    '{%this.applyRenderTpl(out, values)%}',
                '</td>',
                '<tpl if="right"><td id="{fgid}MR" class="{frameCls}-mr {baseCls}-mr {baseCls}-{ui}-mr<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-mr</tpl></tpl>" style="background-position: {mr}; padding-left: {frameWidth}px" role="presentation"></td></tpl>',
            '</tr>',
            '<tpl if="bottom">',
                '<tr>',
                    '<tpl if="left"><td id="{fgid}BL" class="{frameCls}-bl {baseCls}-bl {baseCls}-{ui}-bl<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-bl</tpl></tpl>" style="background-position: {bl}; padding-left: {frameWidth}px" role="presentation"></td></tpl>',
                    '<td id="{fgid}BC" class="{frameCls}-bc {baseCls}-bc {baseCls}-{ui}-bc<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-bc</tpl></tpl>" style="background-position: {bc}; height: {frameWidth}px" role="presentation"></td>',
                    '<tpl if="right"><td id="{fgid}BR" class="{frameCls}-br {baseCls}-br {baseCls}-{ui}-br<tpl if="uiCls"><tpl for="uiCls"> {parent.baseCls}-{parent.ui}-{.}-br</tpl></tpl>" style="background-position: {br}; padding-left: {frameWidth}px" role="presentation"></td></tpl>',
                '</tr>',
            '</tpl>',
        '</tbody></table>',
        '{%this.renderDockedItems(out,values,1);%}'
    ],

    /**
     * Allows addition of behavior after rendering is complete. At this stage the Component’s Element
     * will have been styled according to the configuration, will have had any configured CSS class
     * names added, and will be in the configured visibility and the configured enable state.
     *
     * @template
     * @protected
     */
    afterRender : function() {
        var me = this,
            data = {},
            protoEl = me.protoEl,
            target = me.getTargetEl(),
            item;

        me.finishRenderChildren();

        if (me.styleHtmlContent) {
            target.addCls(me.styleHtmlCls);
        }
        
        protoEl.writeTo(data);
        
        // Here we apply any styles that were set on the protoEl during the rendering phase
        // A majority of times this will not happen, but we still need to handle it
        
        item = data.removed;
        if (item) {
            target.removeCls(item);
        }
        
        item = data.cls;
        if (item.length) {
            target.addCls(item);
        }
        
        item = data.style;
        if (data.style) {
            target.setStyle(item);
        }
        
        me.protoEl = null;

        // If this is the outermost Container, lay it out as soon as it is rendered.
        if (!me.ownerCt) {
            me.updateLayout();
        }
    },

    afterFirstLayout : function() {
        var me = this,
            hasX = Ext.isDefined(me.x),
            hasY = Ext.isDefined(me.y),
            pos, xy;

        // For floaters, calculate x and y if they aren't defined by aligning
        // the sized element to the center of either the container or the ownerCt
        if (me.floating && (!hasX || !hasY)) {
            if (me.floatParent) {
                xy = me.el.getAlignToXY(me.floatParent.getTargetEl(), 'c-c');
                pos = me.floatParent.getTargetEl().translatePoints(xy[0], xy[1]);
            } else {
                xy = me.el.getAlignToXY(me.container, 'c-c');
                pos = me.container.translatePoints(xy[0], xy[1]);
            }
            me.x = hasX ? me.x : pos.left;
            me.y = hasY ? me.y : pos.top;
            hasX = hasY = true;
        }

        if (hasX || hasY) {
            me.setPosition(me.x, me.y);
        }
        me.onBoxReady();
        me.fireEvent('boxready', me);
    },
    
    onBoxReady: Ext.emptyFn,

    /**
     * Sets references to elements inside the component. This applies {@link #renderSelectors}
     * as well as {@link #childEls}.
     * @private
     */
    applyRenderSelectors: function() {
        var me = this,
            selectors = me.renderSelectors,
            el = me.el,
            dom = el.dom,
            selector;

        me.applyChildEls(el);

        // We still support renderSelectors. There are a few places in the framework that
        // need them and they are a documented part of the API. In fact, we support mixing
        // childEls and renderSelectors (no reason not to).
        if (selectors) {
            for (selector in selectors) {
                if (selectors.hasOwnProperty(selector) && selectors[selector]) {
                    me[selector] = Ext.get(Ext.DomQuery.selectNode(selectors[selector], dom));
                }
            }
        }
    },

    beforeRender: function () {
        var me = this,
            layout = me.getComponentLayout();

        if (!layout.initialized) {
            layout.initLayout();
        }

        me.setUI(me.ui);

        if (me.disabled) {
            // pass silent so the event doesn't fire the first time.
            me.disable(true);
        }
    },

    /**
     * @private
     * Called from the selected frame generation template to insert this Component's inner structure inside the framing structure.
     *
     * When framing is used, a selected frame generation template is used as the primary template of the #getElConfig instead
     * of the configured {@link #renderTpl}. The {@link #renderTpl} is invoked by this method which is injected into the framing template.
     */
    doApplyRenderTpl: function(out, values) {
        // Careful! This method is bolted on to the frameTpl so all we get for context is
        // the renderData! The "this" pointer is the frameTpl instance!

        var me = values.$comp,
            tpl;

        // Don't do this if the component is already rendered:
        if (!me.rendered) {
            tpl = me.initRenderTpl();
            tpl.applyOut(values.renderData, out);
        }
    },

    /**
     * Handles autoRender.
     * Floating Components may have an ownerCt. If they are asking to be constrained, constrain them within that
     * ownerCt, and have their z-index managed locally. Floating Components are always rendered to document.body
     */
    doAutoRender: function() {
        var me = this;
        if (!me.rendered) {
            if (me.floating) {
                me.render(document.body);
            } else {
                me.render(Ext.isBoolean(me.autoRender) ? Ext.getBody() : me.autoRender);
            }
        }
    },

    doRenderContent: function (out, renderData) {
        // Careful! This method is bolted on to the renderTpl so all we get for context is
        // the renderData! The "this" pointer is the renderTpl instance!

        var me = renderData.$comp;

        if (me.html) {
            Ext.DomHelper.generateMarkup(me.html, out);
            delete me.html;
        }

        if (me.tpl) {
            // Make sure this.tpl is an instantiated XTemplate
            if (!me.tpl.isTemplate) {
                me.tpl = new Ext.XTemplate(me.tpl);
            }

            if (me.data) {
                //me.tpl[me.tplWriteMode](target, me.data);
                me.tpl.applyOut(me.data, out);
                delete me.data;
            }
        }
    },

    doRenderFramingDockedItems: function (out, renderData, after) {
        // Careful! This method is bolted on to the frameTpl so all we get for context is
        // the renderData! The "this" pointer is the frameTpl instance!

        var me = renderData.$comp;

        // Most components don't have dockedItems, so check for doRenderDockedItems on the
        // component (also, don't do this if the component is already rendered):
        if (!me.rendered && me.doRenderDockedItems) {
            // The "renderData" property is placed in scope for the renderTpl, but we don't
            // want to render docked items at that level in addition to the framing level:
            renderData.renderData.$skipDockedItems = true;

            // doRenderDockedItems requires the $comp property on renderData, but this is
            // set on the frameTpl's renderData as well:
            me.doRenderDockedItems.call(this, out, renderData, after);
        }
    },

    /**
     * This method visits the rendered component tree in a "top-down" order. That is, this
     * code runs on a parent component before running on a child. This method calls the
     * {@link #onRender} method of each component.
     * @param {Number} containerIdx The index into the Container items of this Component.
     *
     * @private
     */
    finishRender: function(containerIdx) {
        var me = this,
            tpl, data, contentEl, el, pre, hide, target;

        // We are typically called w/me.el==null as a child of some ownerCt that is being
        // rendered. We are also called by render for a normal component (w/o a configured
        // me.el). In this case, render sets me.el and me.rendering (indirectly). Lastly
        // we are also called on a component (like a Viewport) that has a configured me.el
        // (body for a Viewport) when render is called. In this case, it is not flagged as
        // "me.rendering" yet becasue it does not produce a renderTree. We use this to know
        // not to regen the renderTpl.

        if (!me.el || me.$pid) {
            if (me.container) {
                el = me.container.getById(me.id, true);
            } else {
                el = Ext.getDom(me.id);
            }

            if (!me.el) {
                // Typical case: we produced the el during render
                me.wrapPrimaryEl(el);
            } else {
                // We were configured with an el and created a proxy, so now we can swap
                // the proxy for me.el:
                delete me.$pid;

                if (!me.el.dom) {
                    // make sure me.el is an Element
                    me.wrapPrimaryEl(me.el);
                }
                el.parentNode.insertBefore(me.el.dom, el);
                Ext.removeNode(el); // remove placeholder el
                // TODO - what about class/style?
            }
        } else if (!me.rendering) {
            // We were configured with an el and then told to render (e.g., Viewport). We
            // need to generate the proper DOM below us:
            tpl = me.initRenderTpl();
            if (tpl) {
                data = me.initRenderData();
                tpl.append(me.getTargetEl(), data);
            }
        }
        // else we are rendering

        if (!me.container) {
            // top-level rendered components will already have me.container set up
            me.container = Ext.get(me.el.dom.parentNode);
        }

        if (me.ctCls) {
            me.container.addCls(me.ctCls);
        }

        // Sets the rendered flag and clears the redering flag
        me.onRender(me.container, containerIdx);

        // Initialize with correct overflow attributes
        target = me.getTargetEl();
        target.setStyle(me.getOverflowStyle());

        // Tell the encapsulating element to hide itself in the way the Component is configured to hide
        // This means DISPLAY, VISIBILITY or OFFSETS.
        me.el.setVisibilityMode(Ext.Element[me.hideMode.toUpperCase()]);

        if (me.overCls) {
            me.el.hover(me.addOverCls, me.removeOverCls, me);
        }

        me.fireEvent('render', me);

        if (me.contentEl) {
            pre = Ext.baseCSSPrefix;
            hide = pre + 'hide-';
            contentEl = Ext.get(me.contentEl);
            contentEl.removeCls([pre+'hidden', hide+'display', hide+'offsets', hide+'nosize']);
            target.appendChild(contentEl.dom);
        }

        me.afterRender(); // this can cause a layout
        me.fireEvent('afterrender', me);
        me.initEvents();

        if (me.hidden) {
            // Hiding during the render process should not perform any ancillary
            // actions that the full hide process does; It is not hiding, it begins in a hidden state.'
            // So just make the element hidden according to the configured hideMode
            me.el.hide();
        }
    },

    finishRenderChildren: function () {
        var layout = this.getComponentLayout();

        layout.finishRender();
    },

    getElConfig : function() {
        var me = this,
            autoEl = me.autoEl,
            frameInfo = me.getFrameInfo(),
            frameGenId,
            config = {
                tag: 'div',
                id: me.id,
                tpl: frameInfo ? me.initFramingTpl(frameInfo.table) : me.initRenderTpl()
            };

        me.initStyles(me.protoEl);
        me.protoEl.writeTo(config);
        me.protoEl.flush();

        if (Ext.isString(autoEl)) {
            config.tag = autoEl;
        } else {
            Ext.apply(config, autoEl); // harmless if !autoEl
        }

        if (config.tpl) {
            // Use the framingTpl as the main content creating template. It will call out to this.applyRenderTpl(out, values)
            if (frameInfo) {
                me.frameGenId = 1;
                frameGenId = me.id + '-frame1';
                config.tplData = Ext.apply({}, {
                    $comp:      me,
                    fgid:       frameGenId,
                    ui:         me.ui,
                    uiCls:      me.uiCls,
                    frameCls:   me.frameCls,
                    baseCls:    me.baseCls,
                    frameWidth: frameInfo.maxWidth,
                    top:        !!frameInfo.top,
                    left:       !!frameInfo.left,
                    right:      !!frameInfo.right,
                    bottom:     !!frameInfo.bottom,
                    renderData: me.initRenderData()
                }, me.getFramePositions(frameInfo));

                // Add the childEls for each of the frame elements
                Ext.each(['TL','TC','TR','ML','MC','MR','BL','BC','BR'], function (suffix) {
                    me.addChildEls({ name: 'frame' + suffix, id: frameGenId + suffix });
                });

                // Panel must have a frameBody
                me.addChildEls({
                    name: 'frameBody',
                    id: frameGenId + 'MC'
                });
            } else {
                config.tplData = me.initRenderData();
            }
        }

        return config;
    },

    // Create the framingTpl from the string.
    // Poke in a reference to applyRenderTpl(frameInfo, out)
    initFramingTpl: function(table) {
        var tpl = table ? this.getTpl('frameTableTpl') : this.getTpl('frameTpl');

        if (tpl && !tpl.applyRenderTpl) {
            this.setupFramingTpl(tpl);
        }

        return tpl;
    },

    /**
     * @private
     * Inject a reference to the function which applies the render template into the framing template. The framing template
     * wraps the content.
     */
    setupFramingTpl: function(frameTpl) {
        frameTpl.applyRenderTpl = this.doApplyRenderTpl;
        frameTpl.renderDockedItems = this.doRenderFramingDockedItems;
    },

    /**
     * This function takes the position argument passed to onRender and returns a
     * DOM element that you can use in the insertBefore.
     * @param {String/Number/Ext.dom.Element/HTMLElement} position Index, element id or element you want
     * to put this component before.
     * @return {HTMLElement} DOM element that you can use in the insertBefore
     */
    getInsertPosition: function(position) {
        // Convert the position to an element to insert before
        if (position !== undefined) {
            if (Ext.isNumber(position)) {
                position = this.container.dom.childNodes[position];
            }
            else {
                position = Ext.getDom(position);
            }
        }

        return position;
    },

    getRenderTree: function() {
        var me = this;

        me.beforeRender();

        if (me.fireEvent('beforerender', me) !== false) {
            // Flag to let the layout's finishRenderItems and afterFinishRenderItems
            // know which items to process
            me.rendering = true;

            if (me.el) {
                // Since we are producing a render tree, we produce a "proxy el" that will
                // sit in the rendered DOM precisely where me.el belongs. We replace the
                // proxy el in the finishRender phase.
                return {
                    tag: 'div',
                    id: (me.$pid = Ext.id())
                };
            }

            return me.getElConfig();
        }

        return null;
    },

    initContainer: function(container) {
        var me = this;

        // If you render a component specifying the el, we get the container
        // of the el, and make sure we dont move the el around in the dom
        // during the render
        if (!container && me.el) {
            container = me.el.dom.parentNode;
            me.allowDomMove = false;
        }
        me.container = container.dom ? container : Ext.get(container);

        return me.container;
    },

    /**
     * Initialized the renderData to be used when rendering the renderTpl.
     * @return {Object} Object with keys and values that are going to be applied to the renderTpl
     * @private
     */
    initRenderData: function() {
        var me = this;

        return Ext.apply({
            $comp: me,
            id: me.id,
            ui: me.ui,
            uiCls: me.uiCls,
            baseCls: me.baseCls,
            componentCls: me.componentCls,
            frame: me.frame
        }, me.renderData);
    },

    /**
     * Initializes the renderTpl.
     * @return {Ext.XTemplate} The renderTpl XTemplate instance.
     * @private
     */
    initRenderTpl: function() {
        var tpl = this.getTpl('renderTpl');

        if (tpl && !tpl.renderContent) {
            this.setupRenderTpl(tpl);
        }

        return tpl;
    },

    /**
     * Template method called when this Component's DOM structure is created.
     *
     * At this point, this Component's (and all descendants') DOM structure *exists* but it has not
     * been layed out (positioned and sized).
     *
     * Subclasses which override this to gain access to the structure at render time should
     * call the parent class's method before attempting to access any child elements of the Component.
     *
     * @param {Ext.core.Element} parentNode The parent Element in which this Component's encapsulating element is contained.
     * @param {Number} containerIdx The index within the parent Container's child collection of this Component.
     *
     * @template
     * @protected
     */
    onRender: function(parentNode, containerIdx) {
        var me = this,
            x = me.x,
            y = me.y,
            lastBox, width, height,
            el = me.el;

        // After the container property has been collected, we can wrap the Component in a reset wraper if necessary
        if (Ext.scopeResetCSS && !me.ownerCt) {
            // If this component's el is the body element, we add the reset class to the html tag
            if (el.dom == Ext.getBody().dom) {
                el.parent().addCls(Ext.resetCls);
            }
            else {
                // Else we wrap this element in an element that adds the reset class.
                me.resetEl = el.wrap({
                    cls: Ext.resetCls
                });
            }
        }

        me.applyRenderSelectors();

        // Flag set on getRenderTree to flag to the layout's postprocessing routine that
        // the Component is in the process of being rendered and needs postprocessing.
        delete me.rendering;

        me.rendered = true;

        // We need to remember these to avoid writing them during the initial layout:
        lastBox = null;

        if (x !== undefined) {
            lastBox = lastBox || {};
            lastBox.x = x;
        }
        if (y !== undefined) {
            lastBox = lastBox || {};
            lastBox.y = y;
        }
        // Framed components need their width/height to apply to the frame, which is
        // best handled in layout at present.
        // If we're using the content box model, we also cannot assign initial sizes since we do not know the border widths to subtract
        if (!me.getFrameInfo() && Ext.isBorderBox) {
            width = me.width;
            height = me.height;

            if (typeof width == 'number') {
                lastBox = lastBox || {};
                lastBox.width = width;
            }
            if (typeof height == 'number') {
                lastBox = lastBox || {};
                lastBox.height = height;
            }
        }

        me.lastBox = me.el.lastBox = lastBox;
    },

    render: function(container, position) {
        var me = this,
            el = me.el && (me.el = Ext.get(me.el)), // ensure me.el is wrapped
            tree,
            nextSibling;

        Ext.suspendLayouts();

        container = me.initContainer(container);

        nextSibling = me.getInsertPosition(position);

        if (!el) {
            tree = me.getRenderTree();
            if (nextSibling) {
                el = Ext.DomHelper.insertBefore(nextSibling, tree);
            } else {
                el = Ext.DomHelper.append(container, tree);
            }
            me.wrapPrimaryEl(el);
        } else {
            // Set configured styles on pre-rendered Component's element
            me.initStyles(el);
            if (me.allowDomMove !== false) {
                //debugger; // TODO
                if (nextSibling) {
                    container.dom.insertBefore(el.dom, nextSibling);
                } else {
                    container.dom.appendChild(el.dom);
                }
            }
        }

        me.finishRender(position);

        Ext.resumeLayouts(!container.isDetachedBody);
    },

    /**
     * Ensures that this component is attached to `document.body`. If the component was
     * rendered to {@link Ext#getDetachedBody}, then it will be appended to `document.body`.
     * Any configured position is also restored.
     * @param {Boolean} [runLayout=false] True to run the component's layout.
     */
    ensureAttachedToBody: function (runLayout) {
        var comp = this,
            body;

        while (comp.ownerCt) {
            comp = comp.ownerCt;
        }

        if (comp.container.isDetachedBody) {
            comp.container = body = Ext.getBody();
            body.appendChild(comp.el.dom);
            if (runLayout) {
                comp.updateLayout();
            }
            if (typeof comp.x == 'number' || typeof comp.y == 'number') {
                comp.setPosition(comp.x, comp.y);
            }
        }
    },

    setupRenderTpl: function (renderTpl) {
        renderTpl.renderBody = renderTpl.renderContent = this.doRenderContent;
    },

    wrapPrimaryEl: function (dom) {
        this.el = Ext.get(dom, true);
    },

    /**
     * @private
     */
    initFrame : function() {
        if (Ext.supports.CSS3BorderRadius) {
            return false;
        }

        var me = this,
            frameInfo = me.getFrameInfo(),
            frameWidth, frameTpl, frameGenId;

        if (frameInfo) {
            frameWidth = frameInfo.maxWidth;
            frameTpl = me.getFrameTpl(frameInfo.table);

            // since we render id's into the markup and id's NEED to be unique, we have a
            // simple strategy for numbering their generations.
            me.frameGenId = frameGenId = (me.frameGenId || 0) + 1;
            frameGenId = me.id + '-frame' + frameGenId;

            // Here we render the frameTpl to this component. This inserts the 9point div or the table framing.
            frameTpl.insertFirst(me.el, Ext.apply({
                $comp:      me,
                fgid:       frameGenId,
                ui:         me.ui,
                uiCls:      me.uiCls,
                frameCls:   me.frameCls,
                baseCls:    me.baseCls,
                frameWidth: frameWidth,
                top:        !!frameInfo.top,
                left:       !!frameInfo.left,
                right:      !!frameInfo.right,
                bottom:     !!frameInfo.bottom
            }, me.getFramePositions(frameInfo)));

            // The frameBody is returned in getTargetEl, so that layouts render items to the correct target.
            me.frameBody = me.el.down('.' + me.frameCls + '-mc');

            // Clean out the childEls for the old frame elements (the majority of the els)
            me.removeChildEls(function (c) {
                return c.id && me.frameIdRegex.test(c.id);
            });

            // Grab referennces to the childEls for each of the new frame elements
            Ext.each(['TL','TC','TR','ML','MC','MR','BL','BC','BR'], function (suffix) {
                me['frame' + suffix] = me.el.getById(frameGenId + suffix);
            });
        }
    },

    updateFrame: function() {
        if (Ext.supports.CSS3BorderRadius) {
            return false;
        }

        var me = this,
            wasTable = this.frameSize && this.frameSize.table,
            oldFrameTL = this.frameTL,
            oldFrameBL = this.frameBL,
            oldFrameML = this.frameML,
            oldFrameMC = this.frameMC,
            newMCClassName;

        this.initFrame();

        if (oldFrameMC) {
            if (me.frame) {

                // Store the class names set on the new MC
                newMCClassName = this.frameMC.dom.className;

                // Framing elements have been selected in initFrame, no need to run applyRenderSelectors
                // Replace the new mc with the old mc
                oldFrameMC.insertAfter(this.frameMC);
                this.frameMC.remove();

                // Restore the reference to the old frame mc as the framebody
                this.frameBody = this.frameMC = oldFrameMC;

                // Apply the new mc classes to the old mc element
                oldFrameMC.dom.className = newMCClassName;

                // Remove the old framing
                if (wasTable) {
                    me.el.query('> table')[1].remove();
                }
                else {
                    if (oldFrameTL) {
                        oldFrameTL.remove();
                    }
                    if (oldFrameBL) {
                        oldFrameBL.remove();
                    }
                    if (oldFrameML) {
                        oldFrameML.remove();
                    }
                }
            }
            else {
                // We were framed but not anymore. Move all content from the old frame to the body

            }
        }
        else if (me.frame) {
            this.applyRenderSelectors();
        }
    },

    /**
     * @private
     * On render, reads an encoded style attribute, "background-position" from the style of this Component's element.
     * This information is memoized based upon the CSS class name of this Component's element.
     * Because child Components are rendered as textual HTML as part of the topmost Container, a dummy div is inserted
     * into the document to recieve the document element's CSS class name, and therefore style attributes.
     */
    getFrameInfo: function() {
        var me = this;
        if (Ext.supports.CSS3BorderRadius) {
            return false;
        }

        var frameInfoCache = me.frameInfoCache,
            el = me.el ? me.el : me.protoEl,
            cls = el.dom ? el.dom.className : el.classList.join(' '),
            styleEl,
            left,
            top,
            ownerCt = me.ownerCt,
            info, frameInfo, max,
            frameKey = me.getXType();

        if (me.dock) {
            frameKey += '-dock-' + me.dock; // ex: 'toolbar-dock-top'
            if (ownerCt.isWindow) {
                frameKey = 'window:' + frameKey; // ex: 'window:toolbar-dock-top'
            }
        } else if (ownerCt && ownerCt.dock) {
            frameKey = ownerCt.getXType() + '-dock-' + ownerCt.dock + ':' + frameKey;
            // ex: 'tabbar-dock-top:tab'
        }
        // TODO - frameKey needs to use css classes most likely to ensure correct answers

        // If we have already looked up framing info for this Component class, just return it.
        //frameInfo = frameInfoCache[frameKey];
        if (typeof frameInfo == 'undefined') {
            // Get the singleton frame style proxy with our el class name stamped into it.
            styleEl = Ext.fly(me.getStyleProxy(cls), 'frame-style-el');
            left = styleEl.getStyle('background-position-x');
            top = styleEl.getStyle('background-position-y');

            // Some browsers dont support background-position-x and y, so for those
            // browsers let's split background-position into two parts.
            if (!left && !top) {
                info = styleEl.getStyle('background-position').split(' ');
                left = info[0];
                top = info[1];
            }

            frameInfo = me.calculateFrame(left, top);
            if (frameInfo) {
                // Just to be sure we set the background image of the el to none.
                el.setStyle('background-image', 'none');
            }

            // This happens when you set frame: true explicitly without using the x-frame mixin in sass.
            // This way IE can't figure out what sizes to use and thus framing can't work.
            //<debug error>
            if (me.frame === true && !frameInfo) {
                Ext.log.error('You have set frame: true explicity on this component (' + frameKey +
                        ') and it does not have any framing defined in the CSS template. In this ' +
                        'case IE cannot figure out what sizes to use and thus framing on this ' +
                        'component will be disabled.');
            }

            // turn on if we restore frameInfoCache
            //Ext.log.info('No frameInfo cached for ', frameKey, ': ', Ext.encode(frameInfo));

            //</debug>

            frameInfoCache[frameKey] = frameInfo || false;
        }

        me.frame = !!frameInfo;
        me.frameSize = frameInfo || false;

        return frameInfo;
    },
    
    calculateFrame: function(left, top){
        // We actually pass a string in the form of '[type][tl][tr]px [direction][br][bl]px' as
        // the background position of this.el from the CSS to indicate to IE that this component needs
        // framing. We parse it here.
        if (!(parseInt(left, 10) >= 1000000 && parseInt(top, 10) >= 1000000)) {
            return;
        }
        var max = Math.max,
            tl = parseInt(left.substr(3, 2), 10),
            tr = parseInt(left.substr(5, 2), 10),
            br = parseInt(top.substr(3, 2), 10),
            bl = parseInt(top.substr(5, 2), 10),
            frameInfo = {
                // Table markup starts with 110, div markup with 100.
                table: left.substr(0, 3) == '110',

                // Determine if we are dealing with a horizontal or vertical component
                vertical: top.substr(0, 3) == '110',

                // Get and parse the different border radius sizes
                top:    max(tl, tr),
                right:  max(tr, br),
                bottom: max(bl, br),
                left:   max(tl, bl)
            };

        frameInfo.maxWidth = max(frameInfo.top, frameInfo.right, frameInfo.bottom, frameInfo.left);
        frameInfo.width = frameInfo.left + frameInfo.right;
        frameInfo.height = frameInfo.top + frameInfo.bottom;
        return frameInfo;
    },

    /**
     * @private
     * Returns an offscreen div with the same class name as the element this is being rendered.
     * This is because child item rendering takes place in a detached div which, being ot part of the document, has no styling.
     */
    getStyleProxy: function(cls) {
        var result = this.styleProxyEl || (Ext.AbstractComponent.prototype.styleProxyEl = Ext.getBody().createChild({
                style: {
                    position: 'absolute',
                    top: '-10000px'
                }
            }, null, true));

        result.className = cls;
        return result;
    },

    getFramePositions: function(frameInfo) {
        var me = this,
            frameWidth = frameInfo.maxWidth,
            dock = me.dock,
            positions, tc, bc, ml, mr;

        if (frameInfo.vertical) {
            tc = '0 -' + (frameWidth * 0) + 'px';
            bc = '0 -' + (frameWidth * 1) + 'px';

            if (dock && dock == "right") {
                tc = 'right -' + (frameWidth * 0) + 'px';
                bc = 'right -' + (frameWidth * 1) + 'px';
            }

            positions = {
                tl: '0 -' + (frameWidth * 0) + 'px',
                tr: '0 -' + (frameWidth * 1) + 'px',
                bl: '0 -' + (frameWidth * 2) + 'px',
                br: '0 -' + (frameWidth * 3) + 'px',

                ml: '-' + (frameWidth * 1) + 'px 0',
                mr: 'right 0',

                tc: tc,
                bc: bc
            };
        } else {
            ml = '-' + (frameWidth * 0) + 'px 0';
            mr = 'right 0';

            if (dock && dock == "bottom") {
                ml = 'left bottom';
                mr = 'right bottom';
            }

            positions = {
                tl: '0 -' + (frameWidth * 2) + 'px',
                tr: 'right -' + (frameWidth * 3) + 'px',
                bl: '0 -' + (frameWidth * 4) + 'px',
                br: 'right -' + (frameWidth * 5) + 'px',

                ml: ml,
                mr: mr,

                tc: '0 -' + (frameWidth * 0) + 'px',
                bc: '0 -' + (frameWidth * 1) + 'px'
            };
        }

        return positions;
    },

    /**
     * @private
     */
    getFrameTpl : function(table) {
        return this.getTpl(table ? 'frameTableTpl' : 'frameTpl');
    },

    // Cache the frame information object for all unique Component xtypes so as not to
    // cause style recaclculations.
    frameInfoCache: {},
    _frameInfoCache: {
        // Components
        panel: {
            "table": false,
            "vertical": false,
            "top": 4,
            "right": 4,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 8
        },
        window: {
            "table": false,
            "vertical": false,
            "top": 5,
            "right": 5,
            "bottom": 5,
            "left": 5,
            "maxWidth": 5,
            "width": 10,
            "height": 10
        },
        form: {
            "table": false,
            "vertical": false,
            "top": 4,
            "right": 4,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 8
        },
        grid: {
            "table": false,
            "vertical": false,
            "top": 4,
            "right": 4,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 8
        },
        button: {
            "table": true,
            "vertical": false,
            "top": 3,
            "right": 3,
            "bottom": 3,
            "left": 3,
            "maxWidth": 3,
            "width": 6,
            "height": 6
        },
        buttongroup: {
            "table": false,
            "vertical": false,
            "top": 2,
            "right": 2,
            "bottom": 2,
            "left": 2,
            "maxWidth": 2,
            "width": 4,
            "height": 4
        },
        splitbutton: {
            "table": true,
            "vertical": false,
            "top": 3,
            "right": 3,
            "bottom": 3,
            "left": 3,
            "maxWidth": 3,
            "width": 6,
            "height": 6
        },
        tip: {
            "table": true,
            "vertical": false,
            "top": 5,
            "right": 5,
            "bottom": 5,
            "left": 5,
            "maxWidth": 5,
            "width": 10,
            "height": 10
        },

        // Headers in non-Windows (Panels):
        'header-dock-top': {
            "table": false,
            "vertical": false,
            "top": 4,
            "right": 4,
            "bottom": 0,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 4
        },
        /* TODO - this is not being read properly from the DOM yet... missing classes??
        'header-dock-bottom': {
            "table": false,
            "vertical": false,
            "top": 0,
            "right": 4,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 4
        },*/
        'header-dock-left': {
            "table": false,
            "vertical": true,
            "top": 4,
            "right": 0,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 4,
            "height": 8
        },
        'header-dock-right': {
            "table": false,
            "vertical": true,
            "top": 4,
            "right": 4,
            "bottom": 4,
            "left": 0,
            "maxWidth": 4,
            "width": 4,
            "height": 8
        },

        // Headers in Windows:
        'window:header-dock-top': {
            "table": false,
            "vertical": false,
            "top": 5,
            "right": 5,
            "bottom": 0,
            "left": 5,
            "maxWidth": 5,
            "width": 10,
            "height": 5
        },
        'window:header-dock-right': {
            "table": false,
            "vertical": false,
            "top": 5,
            "right": 5,
            "bottom": 5,
            "left": 0,
            "maxWidth": 5,
            "width": 5,
            "height": 10
        },
        'window:header-dock-bottom': {
            "table": false,
            "vertical": false,
            "top": 0,
            "right": 5,
            "bottom": 5,
            "left": 5,
            "maxWidth": 5,
            "width": 10,
            "height": 5
        },
        'window:header-dock-left': {
            "table": false,
            "vertical": false,
            "top": 5,
            "right": 0,
            "bottom": 5,
            "left": 5,
            "maxWidth": 5,
            "width": 5,
            "height": 10
        },

        // Tabs in a TabBar:
        'tabbar-dock-top:tab': {
            "table": true,
            "vertical": false,
            "top": 4,
            "right": 4,
            "bottom": 0,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 4
        },
        'tabbar-dock-bottom:tab': {
            "table": true,
            "vertical": false,
            "top": 0,
            "right": 4,
            "bottom": 4,
            "left": 4,
            "maxWidth": 4,
            "width": 8,
            "height": 4
        }
    }
});
