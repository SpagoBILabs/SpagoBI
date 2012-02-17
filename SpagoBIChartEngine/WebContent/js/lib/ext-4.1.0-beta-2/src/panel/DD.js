// private - DD implementation for Panels
Ext.define('Ext.panel.DD', {
    extend: 'Ext.dd.DragSource',
    requires: ['Ext.panel.Proxy'],

    constructor : function(panel, cfg){
        var me = this;
        
        me.panel = panel;
        me.dragData = {panel: panel};
        me.panelProxy = new Ext.panel.Proxy(panel, cfg);
        me.proxy = me.panelProxy.proxy;

        me.callParent([panel.el, cfg]);
        me.setupEl(panel);
    },
    
    setupEl: function(panel){
        var me = this,
            header = panel.header,
            el = panel.body;
            
        if (header) {
            me.setHandleElId(header.id);
            el = header.el;
        }
        if (el) {
            el.setStyle('cursor', 'move');
            me.scroll = false;
        } else {
            // boxready fires after first layout, so we'll definitely be rendered
            panel.on('boxready', me.setupEl, me, {single: true});
        }
    },

    showFrame: Ext.emptyFn,
    startDrag: Ext.emptyFn,
    
    b4StartDrag: function(x, y) {
        this.panelProxy.show();
    },
    
    b4MouseDown: function(e) {
        var x = e.getPageX(),
            y = e.getPageY();
            
        this.autoOffset(x, y);
    },
    
    onInitDrag : function(x, y){
        this.onStartDrag(x, y);
        return true;
    },
    
    createFrame : Ext.emptyFn,
    
    getDragEl : function(e){
        return this.panelProxy.ghost.el.dom;
    },
    
    endDrag : function(e){
        this.panelProxy.hide();
        this.panel.saveState();
    },

    autoOffset : function(x, y) {
        x -= this.startPageX;
        y -= this.startPageY;
        this.setDelta(x, y);
    }
});