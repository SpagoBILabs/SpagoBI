/**
 * @docauthor Robert Dougan <rob@sencha.com>
 *
 * This class creates a multiline text field, which can be used as a direct replacement for traditional
 * textarea fields. In addition, it supports automatically {@link #grow growing} the height of the textarea to
 * fit its content.
 *
 * All of the configuration options from {@link Ext.form.field.Text} can be used on TextArea.
 *
 * Example usage:
 *
 *     @example
 *     Ext.create('Ext.form.FormPanel', {
 *         title      : 'Sample TextArea',
 *         width      : 400,
 *         bodyPadding: 10,
 *         renderTo   : Ext.getBody(),
 *         items: [{
 *             xtype     : 'textareafield',
 *             grow      : true,
 *             name      : 'message',
 *             fieldLabel: 'Message',
 *             anchor    : '100%'
 *         }]
 *     });
 *
 * Some other useful configuration options when using {@link #grow} are {@link #growMin} and {@link #growMax}.
 * These allow you to set the minimum and maximum grow heights for the textarea.
 */
Ext.define('Ext.form.field.TextArea', {
    extend:'Ext.form.field.Text',
    alias: ['widget.textareafield', 'widget.textarea'],
    alternateClassName: 'Ext.form.TextArea',
    requires: [
        'Ext.XTemplate', 
        'Ext.layout.component.field.TextArea',
        'Ext.util.DelayedTask'
    ],

    fieldSubTpl: [
        '<textarea id="{id}" {inputAttrTpl}',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="rows"> rows="{rows}" </tpl>',
            '<tpl if="cols"> cols="{cols}" </tpl>',
            '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
            '<tpl if="size"> size="{size}"</tpl>',
            '<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
            '<tpl if="readOnly"> readonly="readonly"</tpl>',
            '<tpl if="disabled"> disabled="disabled"</tpl>',
            '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
            ' class="{fieldCls} {typeCls}" ',
            '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
            ' autocomplete="off">',
            '<tpl if="value">{value}</tpl>',
        '</textarea>',
        {
            compiled: true,
            disableFormats: true
        }
    ],

    /**
     * @cfg {Number} growMin
     * The minimum height to allow when {@link #grow}=true
     */
    growMin: 60,

    /**
     * @cfg {Number} growMax
     * The maximum height to allow when {@link #grow}=true
     */
    growMax: 1000,

    /**
     * @cfg {String} growAppend
     * A string that will be appended to the field's current value for the purposes of calculating the target field
     * size. Only used when the {@link #grow} config is true. Defaults to a newline for TextArea to ensure there is
     * always a space below the current line.
     */
    growAppend: '\n-',

    /**
     * @cfg {Number} cols
     * An initial value for the 'cols' attribute on the textarea element. This is only used if the component has no
     * configured {@link #width} and is not given a width by its container's layout.
     */
    cols: 20,

    /**
     * @cfg {Number} rows
     * An initial value for the 'rows' attribute on the textarea element. This is only used if the component has no
     * configured {@link #height} and is not given a height by its container's layout. Defaults to 4.
     */
    rows: 4,

    /**
     * @cfg {Boolean} enterIsSpecial
     * True if you want the ENTER key to be classed as a special key and the {@link #specialkey} event to be fired
     * when ENTER is pressed.
     */
    enterIsSpecial: false,

    /**
     * @cfg {Boolean} preventScrollbars
     * true to prevent scrollbars from appearing regardless of how much text is in the field. This option is only
     * relevant when {@link #grow} is true. Equivalent to setting overflow: hidden.
     */
    preventScrollbars: false,

    // private
    componentLayout: 'textareafield',
    
    setGrowSizePolicy: Ext.emptyFn,

    // private
    getSubTplData: function() {
        var me = this,
            fieldStyle = me.getFieldStyle(),
            ret = me.callParent();

        if (me.grow) {
            if (me.preventScrollbars) {
                ret.fieldStyle = (fieldStyle||'') + ';overflow:hidden;height:' + me.growMin + 'px';
            }
        }

        Ext.applyIf(ret, {
            cols: me.cols,
            rows: me.rows
        });

        return ret;
    },

    afterRender: function () {
        var me = this;

        me.callParent(arguments);

        me.needsMaxCheck = me.enforceMaxLength && !Ext.supports.TextAreaMaxLength;
        if (me.needsMaxCheck) {
            me.inputEl.on('paste', me.onPaste, me);
        }
    },

    onPaste: function(e){
        var me = this;
        if (!me.pasteTask) {
            me.pasteTask = new Ext.util.DelayedTask(me.pasteCheck, me);
        }
        // since we can't get the paste data, we'll give the area a chance to populate
        me.pasteTask.delay(1);
    },
    
    pasteCheck: function(){
        var me = this,
            value = me.getValue(),
            max = me.maxLength;
            
        if (value.length > max) {
            value = value.substr(0, max);
            me.setValue(value);
        }
    },

    // private
    fireKey: function(e) {
        var me = this,
            value;
            
        if (e.isSpecialKey() && (me.enterIsSpecial || (e.getKey() !== e.ENTER || e.hasModifier()))) {
            me.fireEvent('specialkey', me, e);
        }
        
        if (me.needsMaxCheck) {
            value = me.getValue();
            if (value.length >= me.maxLength) {
                e.stopEvent();
            }
        }
    },

    /**
     * Automatically grows the field to accomodate the height of the text up to the maximum field height allowed. This
     * only takes effect if {@link #grow} = true, and fires the {@link #autosize} event if the height changes.
     */
    autoSize: function() {
        var me = this,
            height;

        if (me.grow && me.rendered) {
            me.doComponentLayout();
            height = me.inputEl.getHeight();
            if (height !== me.lastInputHeight) {
                /**
                 * @event autosize
                 * Fires when the {@link #autoSize} function is triggered and the field is resized according to
                 * the grow/growMin/growMax configs as a result. This event provides a hook for the developer
                 * to apply additional logic at runtime to resize the field if needed.
                 * @param {Ext.form.field.Text} this
                 * @param {Number} height
                 */
                me.fireEvent('autosize', me, height);
                me.lastInputHeight = height;
            }
        }
    },

    // private
    initAria: function() {
        this.callParent(arguments);
        this.getActionEl().dom.setAttribute('aria-multiline', true);
    },
    
    beforeDestroy: function(){
        var task = this.pasteTask;
        if (task) {
            task.delay();
        }    
        this.callParent();
    }
});