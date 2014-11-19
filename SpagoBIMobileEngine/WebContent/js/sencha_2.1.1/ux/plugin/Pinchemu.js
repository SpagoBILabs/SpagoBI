/**
 * @filename Pinchemu.js
 *
 * @name Pinch emulator plugin for Sencha Touch
 * @fileOverview Emulation of double touch pinch event for desktops
 *
 * @author Constantine V. Smirnov kostysh(at)gmail.com
 * @date 20120801
 * @version 1.0
 * @license GNU GPL v3.0
 *
 * @requires Sencha Touch 2.0
 * 
 * Usage:
 
 .....
 items: [
            {
                xtype: 'panel',
                id: 'mypinchitem',
                plugins: [
                    {
                        xclass: 'Ext.ux.plugin.Pinchemu',
                        helpers: true//enable touches visualization
                    }
                ]
            }
        ]
 
 *
 */

Ext.define('Ext.ux.plugin.Pinchemu', {
    extend: 'Ext.Component',
    alias: 'plugin.pinchemu',
    
    requires: [
        'Ext.Button'
    ],
    
    config: {
        
        /**
         * @cfg {Boolean} helpers Visualization of touches (enable/disable)
         * @accessor
         */
        helpers: false
    },
    
    /**
     * Plugin initialization
     * @private
     */
    init: function(cmp) {
        var me = this;        
        me.touchHelpers = [];
        
        // Build two helpers based on Button
        me.touchHelpers[0] = Ext.create('Ext.Button', {
            top: 0,
            left: 0,
            style: 'opacity: 0.6;',
            iconMask: true,
            round: true,
            hidden: true
        });
        
        me.touchHelpers[1] = Ext.create('Ext.Button', {
            top: 0,
            left: 0,
            style: 'opacity: 0.6;',
            iconMask: true,
            round: true,
            hidden: true
        });
        
        // Add helpers to viewport
        Ext.Viewport.add(me.touchHelpers[0]);
        Ext.Viewport.add(me.touchHelpers[1]);
        
        me.cmp = cmp;
        me.cmp.on({
            scope: me,
            painted: me.initPinchsim
        });
    },
    
    /**
     * @private
     */
    initPinchsim: function() {
        var me = this;        
        me.pinchStarted = false;        
        var item = me.cmp;
        
        if (!item.pinchSimEnabled) {

            if (item.rendered) {
                me.initHandlers(item);
            } else {
                item.on({
                    painted: me.initHandlers
                });
            }
        }
    },
    
    /**
     * @private
     */
    initHandlers: function(item) {
        var me = this;
        
        // Setup touch handlers on enabled item
        item.element.on({
            scope: me,
            touchstart: function(ev) {
                if ((ev.event.ctrlKey || ev.event.shiftKey) && 
                    me.pinchStarted === false) {
                    me.pinchStarted = true;
                    
                    if (ev.event.ctrlKey) {
                        me.zoomStart = 100;
                        me.zoomDirection = 1;
                    } else if (ev.event.shiftKey) {
                        me.zoomStart = 340;
                        me.zoomDirection = -1;
                    }
                    
                    me.zoomFactor = 1;
                    
                    me.onTouchStart(item, ev);
                }
            },            
            touchend: function(ev) {
                if (me.pinchStarted) {
                    me.pinchStarted = false;
                    me.onTouchEnd(item, ev);
                }
            },            
            touchcancel: function(ev) {
                if (me.pinchStarted) {
                    me.pinchStarted = false;
                    me.onTouchEnd(item, ev);
                }
            },            
            touchmove: function(ev) {
                if ((ev.event.ctrlKey || ev.event.shiftKey) && 
                    this.pinchStarted === true) {
                    me.onTouchMove(item, ev);
                } else if (me.pinchStarted) {
                    me.pinchStarted = false;
                    me.onTouchEnd(item, ev);
                }
            }
        });
        
        // Mark item as pinchSimEnabled
        item.pinchSimEnabled = true;
    },
    
    /**
     * @private
     */
    showHelpers: function(ev) {
        var touches = ev.touches;
        if (typeof touches === 'object' && this.getHelpers()) {
            this.moveHelpers(touches);
            this.setHelpersArrows(ev);
            this.touchHelpers[0].show();
            this.touchHelpers[1].show();
        }
    },
    
    /**
     * @private
     */
    setHelpersArrows: function(ev) {
        if (ev.event.ctrlKey) {
            this.touchHelpers[0].setIconCls('arrow_right');
            this.touchHelpers[1].setIconCls('arrow_left');
        } else {
            this.touchHelpers[0].setIconCls('arrow_left');
            this.touchHelpers[1].setIconCls('arrow_right');
        }        
    },
    
    /**
     * @private
     */
    moveHelpers: function(touches) {
        this.touchHelpers[0].setTop(touches[0].point.y);
        this.touchHelpers[0].setLeft(touches[0].point.x);
        this.touchHelpers[1].setTop(touches[1].point.y);
        this.touchHelpers[1].setLeft(touches[1].point.x);
    },
    
    /**
     * @private
     */
    hideHelpers: function() {
        this.touchHelpers[0].hide();
        this.touchHelpers[1].hide();
    },
    
    /**
     * Converting of single touch event to double touch
     * @private
     */
    convertEvent: function(ev) {
        var me = this;
        
        // Clone of original touch object
        var touches = Ext.clone(ev.touches);
        
        if (!touches) {
            touches = me.lastTouches;//at the pinchend only
        }
        
        ev.touches = touches;        
        
        if (touches.length > 0) {
            
            if (!me.touchStartPoint) {
                
                var startX = touches[0].point.x;
                var startY = touches[0].point.y;
                var startPageX = touches[0].pageX;
                var startPageY = touches[0].pageY;
                
                touches[0].point.x = touches[0].point.x + me.zoomStart / 2;
                touches[0].pageX = touches[0].pageX + me.zoomStart / 2;
                
                // Build new touch point
                touches[1] = {};
                touches[1].identifier = 2;
                touches[1].pageX = startPageX - me.zoomStart / 2;
                touches[1].pageY = startPageY;
                touches[1].point = touches[0].point.clone();
                touches[1].point.x = startX - me.zoomStart / 2;
                touches[1].point.y = touches[0].point.y;
                touches[1].target = touches[0].target;
                touches[1].targets = touches[0].targets;
                touches[1].timeStamp = touches[0].timeStamp;
                
                // Remember the current start point
                this.touchStartPoint = {
                    x: startX,
                    y: startY,
                    pageX: startPageX,
                    pageY: startPageY,
                    distance: touches[0].point.getDistanceTo(touches[1].point)
                };                
            } else {
                
                // Replace original by previous
                touches[0].point = Ext.clone(me.lastTouches[0].point);
                touches[0].point.x = Ext.Number.constrain(me.lastTouches[0].point.x + 
                                                          me.zoomFactor * me.zoomDirection, 
                                                          me.touchStartPoint.x + 
                                                          me.zoomFactor);
                touches[0].pageX = Ext.Number.constrain(me.lastTouches[0].pageX + 
                                                        me.zoomFactor * me.zoomDirection, 
                                                        me.touchStartPoint.x + 
                                                        me.zoomFactor);
                
                touches[1] = {};
                touches[1].point = me.lastTouches[1].point.clone();
                touches[1].point.x = Ext.Number.constrain(me.lastTouches[1].point.x - me.zoomFactor * me.zoomDirection, 
                                                          me.touchStartPoint.x + me.zoomFactor);
                touches[1].pageX = Ext.Number.constrain(me.lastTouches[1].pageX - me.zoomFactor * me.zoomDirection, 
                                                        me.touchStartPoint.x + me.zoomFactor);
                touches[1].pageY = me.lastTouches[1].pageY;
                touches[1].target = touches[0].target;
                touches[1].targets = touches[0].targets;
                touches[1].timeStamp = touches[0].timeStamp;                
            }
            
            me.lastTouches = touches;
        }
        
        ev.scale = me.getNewScale(ev);
        return ev;
    },
    
    /**
     * @private
     */
    getNewScale: function(ev) {
        var me = this;
        
        if (ev.touches.length > 0) {
            var newDistance = ev.touches[0].point.getDistanceTo(ev.touches[1].point);
            me.lastScale = newDistance / me.touchStartPoint.distance;            
            return me.lastScale;
        } else {
            return me.lastScale;
        }        
    },
    
    /**
     * @private
     */
    onTouchStart: function() {
        this.lastScale = 1;
        var ev = this.convertEvent(arguments[1]);
        this.showHelpers(ev);
    },
    
    /**
     * @private
     */
    onTouchMove: function() {
        var ev = this.convertEvent(arguments[1]);
        this.lastTouches = Array.prototype.slice.call(ev.touches);
        this.moveHelpers(ev.touches);
    },
    
    /**
     * @private
     */
    onTouchEnd: function() {
        var ev = this.convertEvent(arguments[1]);
        this.hideHelpers();
        this.touchStartPoint = null;
        this.lastTouches = null;
        this.lastScale = null;
    }
});