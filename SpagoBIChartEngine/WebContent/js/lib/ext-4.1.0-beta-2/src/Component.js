/**
 * Base class for all Ext components. All subclasses of Component may participate in the automated Ext component
 * lifecycle of creation, rendering and destruction which is provided by the {@link Ext.container.Container Container}
 * class. Components may be added to a Container through the {@link Ext.container.Container#cfg-items items} config option
 * at the time the Container is created, or they may be added dynamically via the
 * {@link Ext.container.Container#method-add add} method.
 *
 * The Component base class has built-in support for basic hide/show and enable/disable and size control behavior.
 *
 * All Components are registered with the {@link Ext.ComponentManager} on construction so that they can be referenced at
 * any time via {@link Ext#getCmp Ext.getCmp}, passing the {@link #id}.
 *
 * All user-developed visual widgets that are required to participate in automated lifecycle and size management should
 * subclass Component.
 *
 * See the Creating new UI controls chapter in [Component Guide][1] for details on how and to either extend
 * or augment Ext JS base classes to create custom Components.
 *
 * Every component has a specific xtype, which is its Ext-specific type name, along with methods for checking the xtype
 * like {@link #getXType} and {@link #isXType}. See the [Component Guide][1] for more information on xtypes and the
 * Component hierarchy.
 *
 * This is the list of all valid xtypes:
 *
 *     xtype            Class
 *     -------------    ------------------
 *     button           {@link Ext.button.Button}
 *     buttongroup      {@link Ext.container.ButtonGroup}
 *     colorpalette     {@link Ext.picker.Color}
 *     component        {@link Ext.Component}
 *     container        {@link Ext.container.Container}
 *     cycle            {@link Ext.button.Cycle}
 *     dataview         {@link Ext.view.View}
 *     datepicker       {@link Ext.picker.Date}
 *     editor           {@link Ext.Editor}
 *     editorgrid       {@link Ext.grid.plugin.Editing}
 *     grid             {@link Ext.grid.Panel}
 *     multislider      {@link Ext.slider.Multi}
 *     panel            {@link Ext.panel.Panel}
 *     progressbar      {@link Ext.ProgressBar}
 *     slider           {@link Ext.slider.Single}
 *     splitbutton      {@link Ext.button.Split}
 *     tabpanel         {@link Ext.tab.Panel}
 *     treepanel        {@link Ext.tree.Panel}
 *     viewport         {@link Ext.container.Viewport}
 *     window           {@link Ext.window.Window}
 *
 *     Toolbar components
 *     ---------------------------------------
 *     pagingtoolbar    {@link Ext.toolbar.Paging}
 *     toolbar          {@link Ext.toolbar.Toolbar}
 *     tbfill           {@link Ext.toolbar.Fill}
 *     tbitem           {@link Ext.toolbar.Item}
 *     tbseparator      {@link Ext.toolbar.Separator}
 *     tbspacer         {@link Ext.toolbar.Spacer}
 *     tbtext           {@link Ext.toolbar.TextItem}
 *
 *     Menu components
 *     ---------------------------------------
 *     menu             {@link Ext.menu.Menu}
 *     menucheckitem    {@link Ext.menu.CheckItem}
 *     menuitem         {@link Ext.menu.Item}
 *     menuseparator    {@link Ext.menu.Separator}
 *     menutextitem     {@link Ext.menu.Item}
 *
 *     Form components
 *     ---------------------------------------
 *     form             {@link Ext.form.Panel}
 *     checkbox         {@link Ext.form.field.Checkbox}
 *     combo            {@link Ext.form.field.ComboBox}
 *     datefield        {@link Ext.form.field.Date}
 *     displayfield     {@link Ext.form.field.Display}
 *     field            {@link Ext.form.field.Base}
 *     fieldset         {@link Ext.form.FieldSet}
 *     hidden           {@link Ext.form.field.Hidden}
 *     htmleditor       {@link Ext.form.field.HtmlEditor}
 *     label            {@link Ext.form.Label}
 *     numberfield      {@link Ext.form.field.Number}
 *     radio            {@link Ext.form.field.Radio}
 *     radiogroup       {@link Ext.form.RadioGroup}
 *     textarea         {@link Ext.form.field.TextArea}
 *     textfield        {@link Ext.form.field.Text}
 *     timefield        {@link Ext.form.field.Time}
 *     trigger          {@link Ext.form.field.Trigger}
 *
 *     Chart components
 *     ---------------------------------------
 *     chart            {@link Ext.chart.Chart}
 *     barchart         {@link Ext.chart.series.Bar}
 *     columnchart      {@link Ext.chart.series.Column}
 *     linechart        {@link Ext.chart.series.Line}
 *     piechart         {@link Ext.chart.series.Pie}
 *
 * It should not usually be necessary to instantiate a Component because there are provided subclasses which implement
 * specialized Component use cases which cover most application needs. However it is possible to instantiate a base
 * Component, and it will be renderable, or will particpate in layouts as the child item of a Container:
 *
 *     @example
 *     Ext.create('Ext.Component', {
 *         html: 'Hello world!',
 *         width: 300,
 *         height: 200,
 *         padding: 20,
 *         style: {
 *             color: '#FFFFFF',
 *             backgroundColor:'#000000'
 *         },
 *         renderTo: Ext.getBody()
 *     });
 *
 * The Component above creates its encapsulating `div` upon render, and use the configured HTML as content. More complex
 * internal structure may be created using the {@link #renderTpl} configuration, although to display database-derived
 * mass data, it is recommended that an ExtJS data-backed Component such as a {@link Ext.view.View View}, or {@link
 * Ext.grid.Panel GridPanel}, or {@link Ext.tree.Panel TreePanel} be used.
 *
 * [2]: #!/guide/components
 */
Ext.define('Ext.Component', {

    /* Begin Definitions */

    alias: ['widget.component', 'widget.box'],

    extend: 'Ext.AbstractComponent',

    requires: [
        'Ext.util.DelayedTask'
    ],

    uses: [
        'Ext.Layer',
        'Ext.resizer.Resizer',
        'Ext.util.ComponentDragger'
    ],

    mixins: {
        floating: 'Ext.util.Floating'
    },

    statics: {
        // Collapse/expand directions
        DIRECTION_TOP: 'top',
        DIRECTION_RIGHT: 'right',
        DIRECTION_BOTTOM: 'bottom',
        DIRECTION_LEFT: 'left',

        VERTICAL_DIRECTION_Re: /^(?:top|bottom)$/,

        // RegExp whih specifies characters in an xtype which must be translated to '-' when generating auto IDs.
        // This includes dot, comma and whitespace
        INVALID_ID_CHARS_Re: /[\.,\s]/g
    },

    /* End Definitions */

    /**
     * @cfg {Boolean/Object} resizable
     * Specify as `true` to apply a {@link Ext.resizer.Resizer Resizer} to this Component after rendering.
     *
     * May also be specified as a config object to be passed to the constructor of {@link Ext.resizer.Resizer Resizer}
     * to override any defaults. By default the Component passes its minimum and maximum size, and uses
     * `{@link Ext.resizer.Resizer#dynamic}: false`
     */

    /**
     * @cfg {String} resizeHandles
     * A valid {@link Ext.resizer.Resizer} handles config string. Only applies when resizable = true.
     */
    resizeHandles: 'all',

    /**
     * @cfg {Boolean} [autoScroll=false]
     * `true` to use overflow:'auto' on the components layout element and show scroll bars automatically when necessary,
     * `false` to clip any overflowing content.
     * This should not be combined with {@link #overflowX} or  {@link #overflowY}.
     */

    /**
     * @cfg {String} overflowX
     * Possible values are:
     *  * `'auto'` to enable automatic horizontal scrollbar (overflow-x: 'auto').
     *  * `'scroll'` to always enable horizontal scrollbar (overflow-x: 'scroll').
     * The default is overflow-x: 'hidden'. This should not be combined with {@link #autoScroll}.
     */

    /**
     * @cfg {String} overflowY
     * Possible values are:
     *  * `'auto'` to enable automatic vertical scrollbar (overflow-y: 'auto').
     *  * `'scroll'` to always enable vertical scrollbar (overflow-y: 'scroll').
     * The default is overflow-y: 'hidden'. This should not be combined with {@link #autoScroll}.
     */

    /**
     * @cfg {Boolean} floating
     * Specify as true to float the Component outside of the document flow using CSS absolute positioning.
     *
     * Components such as {@link Ext.window.Window Window}s and {@link Ext.menu.Menu Menu}s are floating by default.
     *
     * Floating Components that are programatically {@link Ext.Component#render rendered} will register themselves with
     * the global {@link Ext.WindowManager ZIndexManager}
     *
     * ### Floating Components as child items of a Container
     *
     * A floating Component may be used as a child item of a Container. This just allows the floating Component to seek
     * a ZIndexManager by examining the ownerCt chain.
     *
     * When configured as floating, Components acquire, at render time, a {@link Ext.ZIndexManager ZIndexManager} which
     * manages a stack of related floating Components. The ZIndexManager brings a single floating Component to the top
     * of its stack when the Component's {@link #toFront} method is called.
     *
     * The ZIndexManager is found by traversing up the {@link #ownerCt} chain to find an ancestor which itself is
     * floating. This is so that descendant floating Components of floating _Containers_ (Such as a ComboBox dropdown
     * within a Window) can have its zIndex managed relative to any siblings, but always **above** that floating
     * ancestor Container.
     *
     * If no floating ancestor is found, a floating Component registers itself with the default {@link Ext.WindowManager
     * ZIndexManager}.
     *
     * Floating components _do not participate in the Container's layout_. Because of this, they are not rendered until
     * you explicitly {@link #method-show} them.
     *
     * After rendering, the ownerCt reference is deleted, and the {@link #floatParent} property is set to the found
     * floating ancestor Container. If no floating ancestor Container was found the {@link #floatParent} property will
     * not be set.
     */
    floating: false,

    /**
     * @cfg {Boolean} toFrontOnShow
     * True to automatically call {@link #toFront} when the {@link #method-show} method is called on an already visible,
     * floating component.
     */
    toFrontOnShow: true,

    /**
     * @property {Ext.ZIndexManager} zIndexManager
     * Only present for {@link #floating} Components after they have been rendered.
     *
     * A reference to the ZIndexManager which is managing this Component's z-index.
     *
     * The {@link Ext.ZIndexManager ZIndexManager} maintains a stack of floating Component z-indices, and also provides
     * a single modal mask which is insert just beneath the topmost visible modal floating Component.
     *
     * Floating Components may be {@link #toFront brought to the front} or {@link #toBack sent to the back} of the
     * z-index stack.
     *
     * This defaults to the global {@link Ext.WindowManager ZIndexManager} for floating Components that are
     * programatically {@link Ext.Component#render rendered}.
     *
     * For {@link #floating} Components which are added to a Container, the ZIndexManager is acquired from the first
     * ancestor Container found which is floating. If no floating ancestor is found, the global {@link Ext.WindowManager ZIndexManager} is
     * used.
     *
     * See {@link #floating} and {@link #zIndexParent}
     * @readonly
     */

    /**
     * @property {Ext.Container} floatParent
     * Only present for {@link #floating} Components which were inserted as child items of Containers.
     *
     * Floating Components that are programatically {@link Ext.Component#render rendered} will not have a `floatParent`
     * property.
     *
     * For {@link #floating} Components which are child items of a Container, the floatParent will be the owning Container.
     *
     * For example, the dropdown {@link Ext.view.BoundList BoundList} of a ComboBox which is in a Window will have the
     * Window as its `floatParent`
     *
     * See {@link #floating} and {@link #zIndexManager}
     * @readonly
     */

    /**
     * @property {Ext.Container} zIndexParent
     * Only present for {@link #floating} Components which were inserted as child items of Containers, and which have a floating
     * Container in their containment ancestry.
     *
     * For {@link #floating} Components which are child items of a Container, the zIndexParent will be a floating
     * ancestor Container which is responsible for the base z-index value of all its floating descendants. It provides
     * a {@link Ext.ZIndexManager ZIndexManager} which provides z-indexing services for all its descendant floating
     * Components.
     *
     * Floating Components that are programatically {@link Ext.Component#render rendered} will not have a `zIndexParent`
     * property.
     *
     * For example, the dropdown {@link Ext.view.BoundList BoundList} of a ComboBox which is in a Window will have the
     * Window as its `zIndexParent`, and will always show above that Window, wherever the Window is placed in the z-index stack.
     *
     * See {@link #floating} and {@link #zIndexManager}
     * @readonly
     */

    /**
     * @cfg {Boolean/Object} [draggable=false]
     * Specify as true to make a {@link #floating} Component draggable using the Component's encapsulating element as
     * the drag handle.
     *
     * This may also be specified as a config object for the {@link Ext.util.ComponentDragger ComponentDragger} which is
     * instantiated to perform dragging.
     *
     * For example to create a Component which may only be dragged around using a certain internal element as the drag
     * handle, use the delegate option:
     *
     *     new Ext.Component({
     *         constrain: true,
     *         floating: true,
     *         style: {
     *             backgroundColor: '#fff',
     *             border: '1px solid black'
     *         },
     *         html: '<h1 style="cursor:move">The title</h1><p>The content</p>',
     *         draggable: {
     *             delegate: 'h1'
     *         }
     *     }).show();
     */

    hideMode: 'display',
    // Deprecate 5.0
    hideParent: false,

    bubbleEvents: [],

    actionMode: 'el',
    monPropRe: /^(?:scope|delay|buffer|single|stopEvent|preventDefault|stopPropagation|normalized|args|delegate)$/,

    //renderTpl: new Ext.XTemplate(
    //    '<div id="{id}" class="{baseCls} {cls} {cmpCls}<tpl if="typeof ui !== \'undefined\'"> {uiBase}-{ui}</tpl>"<tpl if="typeof style !== \'undefined\'"> style="{style}"</tpl>></div>', {
    //        compiled: true,
    //        disableFormats: true
    //    }
    //),

    /**
     * Creates new Component.
     * @param {Ext.Element/String/Object} config The configuration options may be specified as either:
     *
     * - **an element** : it is set as the internal element and its id used as the component id
     * - **a string** : it is assumed to be the id of an existing element and is used as the component id
     * - **anything else** : it is assumed to be a standard config object and is applied to the component
     */
    constructor: function(config) {
        var me = this;

        config = config || {};
        if (config.initialConfig) {

            // Being initialized from an Ext.Action instance...
            if (config.isAction) {
                me.baseAction = config;
            }
            config = config.initialConfig;
            // component cloning / action set up
        }
        else if (config.tagName || config.dom || Ext.isString(config)) {
            // element object
            config = {
                applyTo: config,
                id: config.id || config
            };
        }

        me.callParent([config]);

        // If we were configured from an instance of Ext.Action, (or configured with a baseAction option),
        // register this Component as one of its items
        if (me.baseAction){
            me.baseAction.addComponent(me);
        }
    },

    /**
     * The initComponent template method is an important initialization step for a Component. It is intended to be
     * implemented by each subclass of Ext.Component to provide any needed constructor logic. The
     * initComponent method of the class being created is called first, with each initComponent method
     * up the hierarchy to Ext.Component being called thereafter. This makes it easy to implement and,
     * if needed, override the constructor logic of the Component at any step in the hierarchy.
     *
     * The initComponent method **must** contain a call to {@link Ext.Base#callParent callParent} in order
     * to ensure that the parent class' initComponent method is also called.
     *
     * All config options passed to the constructor are applied to `this` before initComponent is called,
     * so you can simply access them with `this.someOption`.
     *
     * The following example demonstrates using a dynamic string for the text of a button at the time of
     * instantiation of the class.
     *
     *     Ext.define('DynamicButtonText', {
     *         extend: 'Ext.button.Button',
     *
     *         initComponent: function() {
     *             this.text = new Date();
     *             this.renderTo = Ext.getBody();
     *             this.callParent();
     *         }
     *     });
     *
     *     Ext.onReady(function() {
     *         Ext.create('DynamicButtonText');
     *     });
     *
     * @template
     * @protected
     */
    initComponent: function() {
        var me = this;

        me.callParent();

        if (me.listeners) {
            me.on(me.listeners);
            me.listeners = null; //change the value to remove any on prototype
        }
        me.enableBubble(me.bubbleEvents);
        me.mons = [];
    },

    
    // private
    afterRender: function() {
        var me = this;

        me.callParent();

        if (!(me.x && me.y) && (me.pageX || me.pageY)) {
            me.setPagePosition(me.pageX, me.pageY);
        }

        if (me.resizable) {
            me.initResizable(me.resizable);
        }

        if (me.draggable) {
            me.initDraggable();
        }
    },

    /**
     * Sets the overflow on the content element of the component.
     * @param {Boolean} scroll True to allow the Component to auto scroll.
     * @return {Ext.Component} this
     */
    setAutoScroll : function(scroll) {
        var me = this,
            layout,
            targetEl;

        me.autoScroll = !!scroll;

        // Scrolling styles must be applied to the element into which content is rendered.
        // This means the layout's target if we are using a layout.
        if (me.rendered) {
            targetEl = (layout = me.getLayout && me.getLayout()) ? layout.getRenderTarget() : me.getTargetEl();
            targetEl.setStyle(me.getOverflowStyle());
        }

        return me;
    },

    /**
     * Sets the overflow x/y on the content element of the component. The x/y overflow
     * values can be any valid CSS overflow (e.g., 'auto' or 'scroll'). By default, the
     * value is 'hidden'. Passing null for one of the values will erase the inline style.
     * Passing `undefined` will preserve the current value.
     *
     * @param {String} overflowX The overflow-x value.
     * @param {String} overflowY The overflow-y value.
     * @return {Ext.Component} this
     */
    setOverflowXY: function(overflowX, overflowY) {
        var me = this,
            layout,
            targetEl,
            argCount = arguments.length;

        if (argCount) {
            me.overflowX = overflowX || '';
            if (argCount > 1) {
                me.overflowY = overflowY || '';
            }
        }

        // Scrolling styles must be applied to the element into which content is rendered.
        // This means the layout's target if we are using a layout.
        if (me.rendered) {
            targetEl = (layout = me.getLayout && me.getLayout()) ? layout.getRenderTarget() : me.getTargetEl();
            targetEl.setStyle(me.getOverflowStyle());
        }

        return me;
    },

    beforeRender: function () {
        var me = this,
            floating = me.floating,
            cls;

        if (floating) {
            me.addCls(Ext.baseCSSPrefix + 'layer');

            cls = floating.cls;
            if (cls) {
                me.addCls(cls);
            }
        }

        return me.callParent();
    },

    // private
    makeFloating : function (dom) {
        this.mixins.floating.constructor.call(this, dom);
    },

    wrapPrimaryEl: function (dom) {
        if (this.floating) {
            this.makeFloating(dom);
        } else {
            this.callParent(arguments);
        }
    },

    initResizable: function(resizable) {
        var me = this;

        resizable = Ext.apply({
            target: me,
            dynamic: false,
            constrainTo: me.constrainTo || (me.floatParent ? me.floatParent.getTargetEl() : null),
            handles: me.resizeHandles
        }, resizable);
        resizable.target = me;
        me.resizer = new Ext.resizer.Resizer(resizable);
    },

    getDragEl: function() {
        return this.el;
    },

    initDraggable: function() {
        var me = this,
            ddConfig = Ext.applyIf({
                el: me.getDragEl(),
                constrainTo: me.constrain ? (me.constrainTo || (me.floatParent ? me.floatParent.getTargetEl() : me.el.getScopeParent())) : undefined
            }, me.draggable);

        // Add extra configs if Component is specified to be constrained
        if (me.constrain || me.constrainDelegate) {
            ddConfig.constrain = me.constrain;
            ddConfig.constrainDelegate = me.constrainDelegate;
        }

        me.dd = new Ext.util.ComponentDragger(me, ddConfig);
    },

    /**
     * Scrolls this Component's {@link #getTargetEl target element} by the passed delta values, optionally animating.
     * 
     * All of the following are equivalent:
     *
     *      comp.scrollBy(10, 10, true);
     *      comp.scrollBy([10, 10], true);
     *      comp.scrollBy({ x: 10, y: 10 }, true);
     *
     * @param {Number/Number[]/Object} deltaX Either the x delta, an Array specifying x and y deltas or
     * an object with "x" and "y" properties.
     * @param {Number/Boolean/Object} deltaY Either the y delta, or an animate flag or config object.
     * @param {Boolean/Object} animate Animate flag/config object if the delta values were passed separately.
     */
    scrollBy: function(deltaX, deltaY, animate) {
        var el;

        if ((el = this.getTargetEl()) && el.dom) {
            el.scrollBy.apply(el, arguments);
        }
    },
    
    /**
     * This method allows you to show or hide a LoadMask on top of this component.
     *
     * @param {Boolean/Object/String} load True to show the default LoadMask, a config object that will be passed to the
     * LoadMask constructor, or a message String to show. False to hide the current LoadMask.
     * @param {Boolean} [targetEl=false] True to mask the targetEl of this Component instead of the `this.el`. For example,
     * setting this to true on a Panel will cause only the body to be masked.
     * @return {Ext.LoadMask} The LoadMask instance that has just been shown.
     */
    setLoading : function(load, targetEl) {
        var me = this,
            config;

        if (me.rendered) {
            Ext.destroy(me.loadMask);
            me.loadMask = null;
            
            if (load !== false && !me.collapsed) {
                if (Ext.isObject(load)) {
                    config = Ext.apply({}, load);
                } else if (Ext.isString(load)) {
                    config = {msg: load};
                } else {
                    config = {};
                }
                if (targetEl) {
                    Ext.applyIf(config, {
                        useTargetEl: true
                    });
                }
                me.loadMask = new Ext.LoadMask(me, config);
                me.loadMask.show();
            }
        }
        return me.loadMask;
    },

    beforeSetPosition: function () {
        var me = this,
            pos = me.callParent(arguments), // pass all args on for signature decoding
            adj;

        if (pos) {
            adj = me.adjustPosition(pos.x, pos.y);
            pos.x = adj.x;
            pos.y = adj.y;
        }
        return pos || null;
    },

    afterSetPosition: function(ax, ay) {
        this.onPosition(ax, ay);
        this.fireEvent('move', this, ax, ay);
    },

    /**
     * Displays component at specific xy position.
     * A floating component (like a menu) is positioned relative to its ownerCt if any.
     * Useful for popping up a context menu:
     *
     *     listeners: {
     *         itemcontextmenu: function(view, record, item, index, event, options) {
     *             Ext.create('Ext.menu.Menu', {
     *                 width: 100,
     *                 height: 100,
     *                 margin: '0 0 10 0',
     *                 items: [{
     *                     text: 'regular item 1'
     *                 },{
     *                     text: 'regular item 2'
     *                 },{
     *                     text: 'regular item 3'
     *                 }]
     *             }).showAt(event.getXY());
     *         }
     *     }
     *
     * @param {Number} x The new x position
     * @param {Number} y The new y position
     * @param {Boolean/Object} [animate] True to animate the Component into its new position. You may also pass an
     * animation configuration.
     */
    showAt: function(x, y, animate) {
        var me = this;

        if (!me.rendered && (me.autoRender || me.floating)) {
            me.doAutoRender();
        }
        if (me.floating) {
            me.setPosition(x, y, animate);
        } else {
            me.setPagePosition(x, y, animate);
        }
        me.show();
    },

    /**
     * Sets the page XY position of the component. To set the left and top instead, use {@link #setPosition}.
     * This method fires the {@link #move} event.
     * @param {Number} x The new x position
     * @param {Number} y The new y position
     * @param {Boolean/Object} [animate] True to animate the Component into its new position. You may also pass an
     * animation configuration.
     * @return {Ext.Component} this
     */
    setPagePosition: function(x, y, animate) {
        var me = this,
            p,
            floatParentBox;

        if (Ext.isArray(x)) {
            y = x[1];
            x = x[0];
        }
        me.pageX = x;
        me.pageY = y;

        if (me.floating) {
 
            // Floating Components which are registered with a Container have to have their x and y properties made relative
            if (me.isContainedFloater()) {
                floatParentBox = me.floatParent.getTargetEl().getViewRegion();
                if (Ext.isNumber(x) && Ext.isNumber(floatParentBox.left)) {
                    x -= floatParentBox.left;
                }
                if (Ext.isNumber(y) && Ext.isNumber(floatParentBox.top)) {
                    y -= floatParentBox.top;
                }
            }
            me.setPosition(x, y, animate);
        } else {
            p = me.el.translatePoints(x, y);
            me.setPosition(p.left, p.top, animate);
         }

        return me;
    },

    // Utility method to determine if a Component is floating, and has an owning Container whose coordinate system
    // it must be positioned in when using setPosition.
    isContainedFloater: function() {
        return (this.floating && this.floatParent);
    },

    /**
     * Gets the current box measurements of the component's underlying element.
     * @param {Boolean} [local=false] If true the element's left and top are returned instead of page XY.
     * @return {Object} box An object in the format {x, y, width, height}
     */
    getBox : function(local){
        var pos = local ? this.getPosition(local) : this.el.getXY(),
            size = this.getSize();

        size.x = pos[0];
        size.y = pos[1];
        return size;
    },

    /**
     * Sets the current box measurements of the component's underlying element.
     * @param {Object} box An object in the format {x, y, width, height}
     * @return {Ext.Component} this
     */
    updateBox : function(box){
        this.setSize(box.width, box.height);
        this.setPagePosition(box.x, box.y);
        return this;
    },

    // Include margins
    getOuterSize: function() {
        var el = this.el;
        return {
            width: el.getWidth() + el.getMargin('lr'),
            height: el.getHeight() + el.getMargin('tb')
        };
    },

    // private
    adjustPosition: function(x, y) {
        var me = this,
            floatParentBox;

        // Floating Components being positioned in their ownerCt have to be made absolute.
        if (me.isContainedFloater()) {
            floatParentBox = me.floatParent.getTargetEl().getViewRegion();
            x += floatParentBox.left;
            y += floatParentBox.top;
        }

        return {
            x: x,
            y: y
        };
    },

    /**
     * Gets the current XY position of the component's underlying element.
     * @param {Boolean} [local=false] If true the element's left and top are returned instead of page XY.
     * @return {Number[]} The XY position of the element (e.g., [100, 200])
     */
    getPosition: function(local) {
        var me = this,
            el = me.el,
            xy,
            isContainedFloater = me.isContainedFloater(),
            floatParentBox;

        // Floating Components which were just rendered with no ownerCt return local position.
        if ((local === true) || !isContainedFloater) {
            return [el.getLeft(true), el.getTop(true)];
        }

        // Use our previously set x and y properties if possible.
        if (me.x !== undefined && me.y !== undefined) {
            xy = [me.x, me.y];
        } else {
            xy = me.el.getXY();

            // Floating Components in an ownerCt have to have their positions made relative
            if (isContainedFloater) {
                floatParentBox = me.floatParent.getTargetEl().getViewRegion();
                xy[0] -= floatParentBox.left;
                xy[1] -= floatParentBox.top;
            }
        }
        return xy;
    },

    getId: function() {
        var me = this,
            xtype;

        if (!me.id) {
            xtype = me.getXType();
            if (xtype) {
                xtype = xtype.replace(Ext.Component.INVALID_ID_CHARS_Re, '-');
            } else {
                xtype = Ext.name.toLowerCase() + '-comp';
            }
            me.id = xtype + '-' + me.getAutoId();
        }
        return me.id;
    },

    /**
     * Shows this Component, rendering it first if {@link #autoRender} or {@link #floating} are `true`.
     *
     * After being shown, a {@link #floating} Component (such as a {@link Ext.window.Window}), is activated it and
     * brought to the front of its {@link #zIndexManager z-index stack}.
     *
     * @param {String/Ext.Element} [animateTarget=null] **only valid for {@link #floating} Components such as {@link
     * Ext.window.Window Window}s or {@link Ext.tip.ToolTip ToolTip}s, or regular Components which have been configured
     * with `floating: true`.** The target from which the Component should animate from while opening.
     * @param {Function} [callback] A callback function to call after the Component is displayed.
     * Only necessary if animation was specified.
     * @param {Object} [scope] The scope (`this` reference) in which the callback is executed.
     * Defaults to this Component.
     * @return {Ext.Component} this
     */
    show: function(animateTarget, cb, scope) {
        var me = this;

        if (me.rendered && me.isVisible()) {
            if (me.toFrontOnShow && me.floating) {
                me.toFront();
            }
        } else if (me.fireEvent('beforeshow', me) !== false) {
            me.hidden = false;

            // Render on first show if there is an autoRender config, or if this is a floater (Window, Menu, BoundList etc).
            if (!me.rendered && (me.autoRender || me.floating)) {
                me.doAutoRender();
            }
            if (me.rendered) {
                me.beforeShow();
                me.onShow.apply(me, arguments);
                me.afterShow.apply(me, arguments);
            }
        }
        return me;
    },

    /**
     * Invoked before the Component is shown.
     *
     * @method
     * @template
     * @protected
     */
    beforeShow: Ext.emptyFn,

    /**
     * Allows addition of behavior to the show operation. After
     * calling the superclass's onShow, the Component will be visible.
     *
     * Override in subclasses where more complex behaviour is needed.
     *
     * Gets passed the same parameters as #show.
     *
     * @param {String/Ext.Element} [animateTarget]
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     * @template
     * @protected
     */
    onShow: function() {
        var me = this;

        me.el.show();
        me.callParent(arguments);
        if (me.floating && me.constrain) {
            me.doConstrain();
        }
    },

    /**
     * Invoked after the Component is shown (after #onShow is called).
     *
     * Gets passed the same parameters as #show.
     *
     * @param {String/Ext.Element} [animateTarget]
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     * @template
     * @protected
     */
    afterShow: function(animateTarget, cb, scope) {
        var me = this,
            fromBox,
            toBox,
            ghostPanel;

        // Default to configured animate target if none passed
        animateTarget = animateTarget || me.animateTarget;

        // Need to be able to ghost the Component
        if (!me.ghost) {
            animateTarget = null;
        }
        // If we're animating, kick of an animation of the ghost from the target to the *Element* current box
        if (animateTarget) {
            animateTarget = animateTarget.el ? animateTarget.el : Ext.get(animateTarget);
            toBox = me.el.getBox();
            fromBox = animateTarget.getBox();
            me.el.addCls(Ext.baseCSSPrefix + 'hide-offsets');
            ghostPanel = me.ghost();
            ghostPanel.el.stopAnimation();

            // Shunting it offscreen immediately, *before* the Animation class grabs it ensure no flicker.
            ghostPanel.el.setX(-10000);

            ghostPanel.el.animate({
                from: fromBox,
                to: toBox,
                listeners: {
                    afteranimate: function() {
                        delete ghostPanel.componentLayout.lastComponentSize;
                        me.unghost();
                        me.el.removeCls(Ext.baseCSSPrefix + 'hide-offsets');
                        me.onShowComplete(cb, scope);
                    }
                }
            });
        }
        else {
            me.onShowComplete(cb, scope);
        }
    },

    /**
     * Invoked after the #afterShow method is complete.
     *
     * Gets passed the same `callback` and `scope` parameters that #afterShow received.
     *
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     * @template
     * @protected
     */
    onShowComplete: function(cb, scope) {
        var me = this;
        if (me.floating) {
            me.toFront();
            me.onFloatShow();
        }
        Ext.callback(cb, scope || me);
        me.fireEvent('show', me);
        delete me.hiddenByLayout;
    },

    /**
     * Hides this Component, setting it to invisible using the configured {@link #hideMode}.
     * @param {String/Ext.Element/Ext.Component} [animateTarget=null] **only valid for {@link #floating} Components
     * such as {@link Ext.window.Window Window}s or {@link Ext.tip.ToolTip ToolTip}s, or regular Components which have
     * been configured with `floating: true`.**. The target to which the Component should animate while hiding.
     * @param {Function} [callback] A callback function to call after the Component is hidden.
     * @param {Object} [scope] The scope (`this` reference) in which the callback is executed.
     * Defaults to this Component.
     * @return {Ext.Component} this
     */
    hide: function() {
        var me = this;

        // Clear the flag which is set if a floatParent was hidden while this is visible.
        // If a hide operation was subsequently called, that pending show must be hidden.
        me.showOnParentShow = false;

        if (!(me.rendered && !me.isVisible()) && me.fireEvent('beforehide', me) !== false) {
            me.hidden = true;
            if (me.rendered) {
                me.onHide.apply(me, arguments);
            }
        }
        return me;
    },

    /**
     * Possibly animates down to a target element.
     *
     * Allows addition of behavior to the hide operation. After
     * calling the superclass’s onHide, the Component will be hidden.
     *
     * Gets passed the same parameters as #hide.
     *
     * @param {String/Ext.Element/Ext.Component} [animateTarget]
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     * @template
     * @protected
     */
    onHide: function(animateTarget, cb, scope) {
        var me = this,
            ghostPanel,
            toBox;

        // Default to configured animate target if none passed
        animateTarget = animateTarget || me.animateTarget;

        // Need to be able to ghost the Component
        if (!me.ghost) {
            animateTarget = null;
        }
        // If we're animating, kick off an animation of the ghost down to the target
        if (animateTarget) {
            animateTarget = animateTarget.el ? animateTarget.el : Ext.get(animateTarget);
            ghostPanel = me.ghost();
            ghostPanel.el.stopAnimation();
            toBox = animateTarget.getBox();
            toBox.width += 'px';
            toBox.height += 'px';
            ghostPanel.el.animate({
                to: toBox,
                listeners: {
                    afteranimate: function() {
                        delete ghostPanel.componentLayout.lastComponentSize;
                        ghostPanel.el.hide();
                        me.afterHide(cb, scope);
                    }
                }
            });
        }
        me.el.hide();
        if (!animateTarget) {
            me.afterHide(cb, scope);
        }
    },

    /**
     * Invoked after the Component has been hidden.
     *
     * Gets passed the same `callback` and `scope` parameters that #onHide received.
     *
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     * @template
     * @protected
     */
    afterHide: function(cb, scope) {
        var me = this;
        delete me.hiddenByLayout;

        // we are the back-end method of onHide at this level, but our call to our parent
        // may need to be async... so callParent won't quite work here...
        Ext.AbstractComponent.prototype.onHide.call(this);

        Ext.callback(cb, scope || me);
        me.fireEvent('hide', me);
    },

    /**
     * Allows addition of behavior to the destroy operation.
     * After calling the superclass’s onDestroy, the Component will be destroyed.
     *
     * @template
     * @protected
     */
    onDestroy: function() {
        var me = this;

        // Ensure that any ancillary components are destroyed.
        if (me.rendered) {
            Ext.destroy(
                me.proxy,
                me.proxyWrap,
                me.resizer
            );
            // Different from AbstractComponent
            if (me.actionMode == 'container' || me.removeMode == 'container') {
                me.container.remove();
            }
        }
        delete me.focusTask;
        me.callParent();
    },

    deleteMembers: function() {
        var args = arguments,
            len = args.length,
            i = 0;
        for (; i < len; ++i) {
            delete this[args[i]];
        }
    },

    /**
     * Try to focus this component.
     * @param {Boolean} [selectText] If applicable, true to also select the text in this component
     * @param {Boolean/Number} [delay] Delay the focus this number of milliseconds (true for 10 milliseconds).
     * @return {Ext.Component} The focused Component. Usually <code>this</code> Component. Some Containers may
     * delegate focus to a descendant Component ({@link Ext.window.Window Window}s can do this through their
     * {@link Ext.window.Window#defaultFocus defaultFocus} config option.
     */
    focus: function(selectText, delay) {
        var me = this,
            focusEl,
            focusElDom;


        if (me.rendered && !me.isDestroyed && me.isVisible(true) && (focusEl = me.getFocusEl())) {

            // getFocusEl might return a Component if a Container wishes to delegate focus to a descendant.
            // Window can do this via its defaultFocus configuration which can reference a Button.
            if (focusEl.isComponent) {
                return focusEl.focus(selectText, delay);
            }

            // If delay is wanted, queue a call to this function.
            if (delay) {
                if (!me.focusTask) {
                    me.focusTask = new Ext.util.DelayedTask(me.focus);
                }
                me.focusTask.delay(Ext.isNumber(delay) ? delay : 10, null, me, [selectText, false]);
                return me;
            }

            // If it was an Element with a dom property
            if ((focusElDom = focusEl.dom)) {

                // Not a natural focus holding element, add a tab index to make it programatically focusable.
                if (focusEl.needsTabIndex()) {
                    focusElDom.tabIndex = -1;
                }

                // Focus the element.
                // The focusEl has a DOM focus listener on it which invokes the Component's onFocus method
                // to perform Component-specific focus processing
                focusEl.focus();
                if (selectText === true) {
                    focusElDom.select();
                }
            }

            // Focusing a floating Component brings it to the front of its stack.
            // this is performed by its zIndexManager. Pass preventFocus true to avoid recursion.
            if (me.floating) {
                me.toFront(true);
            }
        }
        return me;
    },

    // private
    blur: function() {
        var focusEl;
        if (this.rendered && (focusEl = this.getFocusEl())) {
            focusEl.blur();
        }
        return this;
    },

    getEl: function() {
        return this.el;
    },

    // Deprecate 5.0
    getResizeEl: function() {
        return this.el;
    },

    // Deprecate 5.0
    getPositionEl: function() {
        return this.el;
    },

    // Deprecate 5.0
    getActionEl: function() {
        return this.el;
    },

    // Deprecate 5.0
    getVisibilityEl: function() {
        return this.el;
    },

    // Deprecate 5.0
    onResize: Ext.emptyFn,

    // private
    getBubbleTarget: function() {
        return this.ownerCt;
    },

    // private
    getContentTarget: function() {
        return this.el;
    },

    /**
     * Clone the current component using the original config values passed into this instance by default.
     * @param {Object} overrides A new config containing any properties to override in the cloned version.
     * An id property can be passed on this object, otherwise one will be generated to avoid duplicates.
     * @return {Ext.Component} clone The cloned copy of this component
     */
    cloneConfig: function(overrides) {
        overrides = overrides || {};
        var id = overrides.id || Ext.id(),
            cfg = Ext.applyIf(overrides, this.initialConfig),
            self;

        cfg.id = id;

        self = Ext.getClass(this);

        // prevent dup id
        return new self(cfg);
    },

    /**
     * Gets the xtype for this component as registered with {@link Ext.ComponentManager}. For a list of all available
     * xtypes, see the {@link Ext.Component} header. Example usage:
     *
     *     var t = new Ext.form.field.Text();
     *     alert(t.getXType());  // alerts 'textfield'
     *
     * @return {String} The xtype
     */
    getXType: function() {
        return this.self.xtype;
    },

    /**
     * Find a container above this component at any level by a custom function. If the passed function returns true, the
     * container will be returned.
     * @param {Function} fn The custom function to call with the arguments (container, this component).
     * @return {Ext.container.Container} The first Container for which the custom function returns true
     */
    findParentBy: function(fn) {
        var p;

        // Iterate up the ownerCt chain until there's no ownerCt, or we find an ancestor which matches using the selector function.
        for (p = this.ownerCt; p && !fn(p, this); p = p.ownerCt) {
            // do nothing
        }
        return p || null;
    },

    /**
     * Find a container above this component at any level by xtype or class
     *
     * See also the {@link Ext.Component#up up} method.
     *
     * @param {String/Ext.Class} xtype The xtype string for a component, or the class of the component directly
     * @return {Ext.container.Container} The first Container which matches the given xtype or class
     */
    findParentByType: function(xtype) {
        return Ext.isFunction(xtype) ?
            this.findParentBy(function(p) {
                return p.constructor === xtype;
            })
        :
            this.up(xtype);
    },

    /**
     * Bubbles up the component/container heirarchy, calling the specified function with each component. The scope
     * (*this*) of function call will be the scope provided or the current component. The arguments to the function will
     * be the args provided or the current component. If the function returns false at any point, the bubble is stopped.
     *
     * @param {Function} fn The function to call
     * @param {Object} [scope] The scope of the function. Defaults to current node.
     * @param {Array} [args] The args to call the function with. Defaults to passing the current component.
     * @return {Ext.Component} this
     */
    bubble: function(fn, scope, args) {
        var p = this;
        while (p) {
            if (fn.apply(scope || p, args || [p]) === false) {
                break;
            }
            p = p.ownerCt;
        }
        return this;
    },

    getProxy: function() {
        var me = this,
            target;

        if (!me.proxy) {
            target = Ext.getBody();
            if (Ext.scopeResetCSS) {
                me.proxyWrap = target = Ext.getBody().createChild({
                    cls: Ext.resetCls
                });
            }
            me.proxy = me.el.createProxy(Ext.baseCSSPrefix + 'proxy-el', target, true);
        }
        return me.proxy;
    }
});
