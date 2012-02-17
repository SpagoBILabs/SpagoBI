/**
 * This class provides an abstract grid editing plugin on selected {@link Ext.grid.column.Column columns}.
 * The editable columns are specified by providing an {@link Ext.grid.column.Column#editor editor}
 * in the {@link Ext.grid.column.Column column configuration}.
 *
 * **Note:** This class should not be used directly. See {@link Ext.grid.plugin.CellEditing} and
 * {@link Ext.grid.plugin.RowEditing}.
 */
Ext.define('Ext.grid.plugin.Editing', {
    alias: 'editing.editing',

    requires: [
        'Ext.grid.column.Column',
        'Ext.util.KeyNav'
    ],

    mixins: {
        observable: 'Ext.util.Observable'
    },

    /**
     * @cfg {Number} clicksToEdit
     * The number of clicks on a grid required to display the editor.
     */
    clicksToEdit: 2,

    // private
    defaultFieldXType: 'textfield',

    // cell, row, form
    editStyle: '',

    constructor: function(config) {
        var me = this;
        Ext.apply(me, config);

        me.addEvents(
            /**
             * @event beforeedit
             * Fires before editing is triggered. Return false from event handler to stop the editing.
             *
             * @param {Ext.grid.plugin.Editing} editor
             * @param {Object} e An edit event with the following properties:
             *
             * - grid - The grid
             * - record - The record being edited
             * - field - The field name being edited
             * - value - The value for the field being edited.
             * - row - The grid table row
             * - column - The grid {@link Ext.grid.column.Column Column} defining the column that is being edited.
             * - rowIdx - The row index that is being edited
             * - colIdx - The column index that is being edited
             * - cancel - Set this to true to cancel the edit or return false from your handler.
             * - originalValue - Alias for value (only when using {@link Ext.grid.plugin.CellEditing CellEditing}).
             */
            'beforeedit',

            /**
             * @event edit
             * Fires after a editing. Usage example:
             *
             *     grid.on('edit', function(editor, e) {
             *         // commit the changes right after editing finished
             *         e.record.commit();
             *     });
             *
             * @param {Ext.grid.plugin.Editing} editor
             * @param {Object} e An edit event with the following properties:
             *
             * - grid - The grid
             * - record - The record that was edited
             * - field - The field name that was edited
             * - value - The value being set
             * - row - The grid table row
             * - column - The grid {@link Ext.grid.column.Column Column} defining the column that was edited.
             * - rowIdx - The row index that was edited
             * - colIdx - The column index that was edited
             * - originalValue - The original value for the field, before the edit (only when using {@link Ext.grid.plugin.CellEditing CellEditing})
             * - originalValues - The original values for the field, before the edit (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - newValues - The new values being set (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - view - The grid view (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - store - The grid store (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             */
            'edit',

            /**
             * @event validateedit
             * Fires after editing, but before the value is set in the record. Return false from event handler to
             * cancel the change.
             *
             * Usage example showing how to remove the red triangle (dirty record indicator) from some records (not all). By
             * observing the grid's validateedit event, it can be cancelled if the edit occurs on a targeted row (for example)
             * and then setting the field's new value in the Record directly:
             *
             *     grid.on('validateedit', function(editor, e) {
             *       var myTargetRow = 6;
             *
             *       if (e.rowIdx == myTargetRow) {
             *         e.cancel = true;
             *         e.record.data[e.field] = e.value;
             *       }
             *     });
             *
             * @param {Ext.grid.plugin.Editing} editor
             * @param {Object} e An edit event with the following properties:
             *
             * - grid - The grid
             * - record - The record being edited
             * - field - The field name being edited
             * - value - The value being set
             * - row - The grid table row
             * - column - The grid {@link Ext.grid.column.Column Column} defining the column that is being edited.
             * - rowIdx - The row index that is being edited
             * - colIdx - The column index that is being edited
             * - cancel - Set this to true to cancel the edit or return false from your handler.
             * - originalValue - The original value for the field, before the edit (only when using {@link Ext.grid.plugin.CellEditing CellEditing})
             * - originalValues - The original values for the field, before the edit (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - newValues - The new values being set (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - view - The grid view (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             * - store - The grid store (only when using {@link Ext.grid.plugin.RowEditing RowEditing})
             */
            'validateedit',
            /**
             * @event canceledit
             * Fires when the user started editing but then cancelled the edit.
             * @param {Ext.grid.plugin.Editing} editor
             * @param {Object} e An edit event with the following properties:
             * 
             * - grid - The grid
             * - record - The record that was edited
             * - field - The field name that was edited
             * - value - The value being set
             * - row - The grid table row
             * - column - The grid {@link Ext.grid.column.Column Column} defining the column that was edited.
             * - rowIdx - The row index that was edited
             * - colIdx - The column index that was edited
             * - view - The grid view
             * - store - The grid store
             */
            'canceledit'

        );
        me.mixins.observable.constructor.call(me);
        // TODO: Deprecated, remove in 5.0
        me.on("edit", function(editor, e) {
            me.fireEvent("afteredit", editor, e);
        });
    },

    // private
    init: function(grid) {
        var me = this;

        me.grid = grid;
        me.view = grid.view;
        me.initEvents();
        me.mon(grid, 'reconfigure', me.onReconfigure, me);
        me.onReconfigure();

        grid.relayEvents(me, [
            /**
             * @event beforeedit
             * Forwarded event from Ext.grid.plugin.Editing.
             * @member Ext.panel.Table
             * @inheritdoc Ext.grid.plugin.Editing#beforeedit
             */
            'beforeedit',
            /**
             * @event edit
             * Forwarded event from Ext.grid.plugin.Editing.
             * @member Ext.panel.Table
             * @inheritdoc Ext.grid.plugin.Editing#edit
             */
            'edit',
            /**
             * @event validateedit
             * Forwarded event from Ext.grid.plugin.Editing.
             * @member Ext.panel.Table
             * @inheritdoc Ext.grid.plugin.Editing#validateedit
             */
            'validateedit',
            /**
             * @event canceledit
             * Forwarded event from Ext.grid.plugin.Editing.
             * @member Ext.panel.Table
             * @inheritdoc Ext.grid.plugin.Editing#canceledit
             */
            'canceledit'
        ]);
        // Marks the grid as editable, so that the SelectionModel
        // can make appropriate decisions during navigation
        grid.isEditable = true;
        grid.editingPlugin = grid.view.editingPlugin = me;
    },

    /**
     * Fires after the grid is reconfigured
     * @private
     */
    onReconfigure: function(){
        this.initFieldAccessors(this.view.getGridColumns());
    },

    /**
     * @private
     * AbstractComponent calls destroy on all its plugins at destroy time.
     */
    destroy: function() {
        var me = this,
            grid = me.grid,
            headerCt = grid.headerCt,
            events = grid.events;

        Ext.destroy(me.keyNav);
        me.removeFieldAccessors(grid.getView().getGridColumns());

        // Clear all listeners from all our events, clear all managed listeners we added to other Observables
        me.clearListeners();

        delete me.grid.editingPlugin;
        delete me.grid.view.editingPlugin;
        delete me.grid;
        delete me.view;
        delete me.editor;
        delete me.keyNav;
    },

    // private
    getEditStyle: function() {
        return this.editStyle;
    },

    // private
    initFieldAccessors: function(column) {
        var me = this;

        if (Ext.isArray(column)) {
            Ext.Array.forEach(column, me.initFieldAccessors, me);
            return;
        }

        // Augment the Header class to have a getEditor and setEditor method
        // Important: Only if the header does not have its own implementation.
        Ext.applyIf(column, {
            getEditor: function(record, defaultField) {
                return me.getColumnField(this, defaultField);
            },

            setEditor: function(field) {
                me.setColumnField(this, field);
            }
        });
    },

    // private
    removeFieldAccessors: function(column) {
        var me = this;

        if (Ext.isArray(column)) {
            Ext.Array.forEach(column, me.removeFieldAccessors, me);
            return;
        }

        delete column.getEditor;
        delete column.setEditor;
    },

    // private
    // remaps to the public API of Ext.grid.column.Column.getEditor
    getColumnField: function(columnHeader, defaultField) {
        var field = columnHeader.field;

        if (!field && columnHeader.editor) {
            field = columnHeader.editor;
            delete columnHeader.editor;
        }

        if (!field && defaultField) {
            field = defaultField;
        }

        if (field) {
            if (Ext.isString(field)) {
                field = { xtype: field };
            }
            if (!field.isFormField) {
                field = Ext.ComponentManager.create(field, this.defaultFieldXType);
            }
            columnHeader.field = field;
 
            Ext.apply(field, {
                name: columnHeader.dataIndex
            });

            return field;
        }
    },

    // private
    // remaps to the public API of Ext.grid.column.Column.setEditor
    setColumnField: function(column, field) {
        if (Ext.isObject(field) && !field.isFormField) {
            field = Ext.ComponentManager.create(field, this.defaultFieldXType);
        }
        column.field = field;
    },

    // private
    initEvents: function() {
        var me = this;
        me.initEditTriggers();
        me.initCancelTriggers();
    },

    // @abstract
    initCancelTriggers: Ext.emptyFn,

    // private
    initEditTriggers: function() {
        var me = this,
            view = me.view,
            clickEvent = me.clicksToEdit === 1 ? 'click' : 'dblclick';

        // Start editing
        me.mon(view, 'cell' + clickEvent, me.startEditByClick, me);
        view.on('render', me.addHeaderEvents, me, {single: true});
    },
    
    addHeaderEvents: function(){
        var me = this;
        me.mon(me.grid.headerCt, {
            scope: me,
            add: me.onColumnAdd,
            remove: me.onColumnRemove
        });
        
        me.keyNav = Ext.create('Ext.util.KeyNav', me.view.el, {
            enter: me.onEnterKey,
            esc: me.onEscKey,
            scope: me
        });
    },
    
    // private
    onColumnAdd: function(ct, column) {
        if (column.isHeader) {
            this.initFieldAccessors(column);
        }
    },

    // private
    onColumnRemove: function(ct, column) {
        if (column.isHeader) {
            this.removeFieldAccessors(column);
        }
    },

    // private
    onEnterKey: function(e) {
        var me = this,
            grid = me.grid,
            selModel = grid.getSelectionModel(),
            record,
            pos,
            columnHeader = grid.headerCt.getHeaderAtIndex(0);

        // Calculate editing start position from SelectionModel
        // CellSelectionModel
        if (selModel.getCurrentPosition) {
            pos = selModel.getCurrentPosition();
            record = grid.store.getAt(pos.row);
            columnHeader = grid.headerCt.getHeaderAtIndex(pos.column);
        }
        // RowSelectionModel
        else {
            record = selModel.getLastSelected();
        }
        me.startEdit(record, columnHeader);
    },

    // private
    onEscKey: function(e) {
        this.cancelEdit();
    },

    // private
    startEditByClick: function(view, cell, colIdx, record, row, rowIdx, e) {
        // cancel editing if the element that was clicked was a tree expander
        if(!view.expanderSelector || !e.getTarget(view.expanderSelector)) {
            this.startEdit(record, view.getHeaderAtIndex(colIdx));
        }
    },

    /**
     * @private
     * @template
     * Template method called before editing begins.
     * @param {Object} context The current editing context
     * @return {Boolean} Return false to cancel the editing process
     */
    beforeEdit: Ext.emptyFn,

    /**
     * Starts editing the specified record, using the specified Column definition to define which field is being edited.
     * @param {Ext.data.Model/Number} record The Store data record which backs the row to be edited, or index of the record in Store.
     * @param {Ext.grid.column.Column/Number} columnHeader The Column object defining the column to be edited, or index of the column.
     */
    startEdit: function(record, columnHeader) {
        var me = this,
            context = me.getEditingContext(record, columnHeader);

        if (me.beforeEdit(context) === false || me.fireEvent('beforeedit', me, context) === false || context.cancel) {
            return false;
        }

        me.context = context;
        me.editing = true;
    },

    /**
     * @private
     * Collects all information necessary for any subclasses to perform their editing functions.
     * @param record
     * @param columnHeader
     * @returns {Object} The editing context based upon the passed record and column
     */
    getEditingContext: function(record, columnHeader) {
        var me = this,
            grid = me.grid,
            view = grid.getView(),
            node = view.getNode(record),
            rowIdx, colIdx, value;

        // Coerce the column index to the closest visible column
        columnHeader = grid.headerCt.getVisibleHeaderClosestToIndex(Ext.isNumber(columnHeader) ? columnHeader : columnHeader.getIndex());
        colIdx = columnHeader.getIndex();

        view = columnHeader.ownerCt.lockableInjected ? (columnHeader.locked ? view.lockedView : view.normalView) : view;

        if (Ext.isNumber(record)) {
            // look up record if numeric row index was passed
            rowIdx = record;
            record = view.getRecord(node);
        } else {
            rowIdx = view.indexOf(node);
        }

        value = record.get(columnHeader.dataIndex);
        return {
            grid: grid,
            record: record,
            field: columnHeader.dataIndex,
            value: value,
            row: view.getNode(rowIdx),
            column: columnHeader,
            rowIdx: rowIdx,
            colIdx: colIdx
        };
    },

    /**
     * Cancels any active edit that is in progress.
     */
    cancelEdit: function() {
        var me = this;

        me.editing = false;
        me.fireEvent('canceledit', me, me.context);
    },

    /**
     * Completes the edit if there is an active edit in progress.
     */
    completeEdit: function() {
        var me = this;

        if (me.editing && me.validateEdit()) {
            me.fireEvent('edit', me, me.context);
        }

        delete me.context;
        me.editing = false;
    },

    // @abstract
    validateEdit: function() {
        var me = this,
            context = me.context;

        return me.fireEvent('validateedit', me, context) !== false && !context.cancel;
    }
});
