/**
 * @class Ext.dom.Element
 */
(function() {

var Element = Ext.dom.Element,
    VISIBILITY      = "visibility",
    DISPLAY         = "display",
    NONE            = "none",
    HIDDEN          = 'hidden',
    OFFSETS = "offsets",
    ASCLASS = "asclass",
    NOSIZE = 'nosize',
    ORIGINALDISPLAY = 'originalDisplay',
    VISMODE = 'visibilityMode',
    ISVISIBLE = 'isVisible',
    getDisplay = function(el){
        var data = (el.$cache || el.getCache()).data,
            display = data[ORIGINALDISPLAY];
            
        if (display === undefined) {
            data[ORIGINALDISPLAY] = display = '';
        }
        return display;
    },
    getVisMode = function(el){
        var data = (el.$cache || el.getCache()).data,
            visMode = data[VISMODE];
            
        if (visMode === undefined) {
            data[VISMODE] = visMode = Ext.dom.Element.VISIBILITY;
        }
        return visMode;
    };

Element.override({
    /**
     * The element's default display mode.
     */
    originalDisplay : "",
    visibilityMode : 1,

    /**
     * Sets the visibility of the element (see details). If the visibilityMode is set to Element.DISPLAY, it will use
     * the display property to hide the element, otherwise it uses visibility. The default is to hide and show using the visibility property.
     * @param {Boolean} visible Whether the element is visible
     * @param {Boolean/Object} [animate] True for the default animation, or a standard Element animation config object
     * @return {Ext.dom.Element} this
     */
    setVisible : function(visible, animate){
        var me = this, isDisplay, isVisibility, isOffsets, isNosize,
            dom = me.dom,
            visMode = getVisMode(me);


        // hideMode string override
        if (typeof animate == 'string'){
            switch (animate) {
                case DISPLAY:
                    visMode = Ext.dom.Element.DISPLAY;
                    break;
                case VISIBILITY:
                    visMode = Ext.dom.Element.VISIBILITY;
                    break;
                case OFFSETS:
                    visMode = Ext.dom.Element.OFFSETS;
                    break;
                case NOSIZE:
                case ASCLASS:
                    visMode = Ext.dom.Element.ASCLASS;
                    break;
            }
            me.setVisibilityMode(visMode);
            animate = false;
        }

        if (!animate || !me.anim) {
            if(visMode == Ext.dom.Element.ASCLASS ){

                me[visible?'removeCls':'addCls'](me.visibilityCls || Ext.dom.Element.visibilityCls);

            } else if (visMode == Ext.dom.Element.DISPLAY){

                return me.setDisplayed(visible);

            } else if (visMode == Ext.dom.Element.OFFSETS){

                if (!visible){
                    // Remember position for restoring, if we are not already hidden by offsets.
                    if (!me.hideModeStyles) {
                        me.hideModeStyles = {
                            position: me.getStyle('position'),
                            top: me.getStyle('top'),
                            left: me.getStyle('left')
                        };
                    }
                    me.applyStyles({position: 'absolute', top: '-10000px', left: '-10000px'});
                }

                // Only "restore" as position if we have actually been hidden using offsets.
                // Calling setVisible(true) on a positioned element should not reposition it.
                else if (me.hideModeStyles) {
                    me.applyStyles(me.hideModeStyles || {position: '', top: '', left: ''});
                    delete me.hideModeStyles;
                }

            }else{
                me.fixDisplay();
                // Show by clearing visibility style. Explicitly setting to "visible" overrides parent visibility setting.
                dom.style.visibility = visible ? '' : HIDDEN;
            }
        }else{
            // closure for composites
            if(visible){
                me.setOpacity(0.01);
                me.setVisible(true);
            }
            if (!Ext.isObject(animate)) {
                animate = {
                    duration: 350,
                    easing: 'ease-in'
                };
            }
            me.animate(Ext.applyIf({
                callback: function() {
                    if (!visible) {
                        me.setVisible(false).setOpacity(1);
                    }
                },
                to: {
                    opacity: (visible) ? 1 : 0
                }
            }, animate));
        }
        (me.$cache || me.getCache()).data[ISVISIBLE] = visible;
        return me;
    },


    /**
     * @private
     * Determine if the Element has a relevant height and width available based
     * upon current logical visibility state
     */
    hasMetrics  : function(){
        var visMode = getVisMode(this);
        return this.isVisible() || (visMode == Ext.dom.Element.OFFSETS) || (visMode == Ext.dom.Element.VISIBILITY);
    },

    /**
     * Toggles the element's visibility or display, depending on visibility mode.
     * @param {Boolean/Object} [animate] True for the default animation, or a standard Element animation config object
     * @return {Ext.dom.Element} this
     */
    toggle : function(animate){
        var me = this;
        me.setVisible(!me.isVisible(), me.anim(animate));
        return me;
    },

    /**
     * Sets the CSS display property. Uses originalDisplay if the specified value is a boolean true.
     * @param {Boolean/String} value Boolean value to display the element using its default display, or a string to set the display directly.
     * @return {Ext.dom.Element} this
     */
    setDisplayed : function(value) {
        if(typeof value == "boolean"){
           value = value ? getDisplay(this) : NONE;
        }
        this.setStyle(DISPLAY, value);
        return this;
    },

    // private
    fixDisplay : function(){
        var me = this;
        if (me.isStyle(DISPLAY, NONE)) {
            me.setStyle(VISIBILITY, HIDDEN);
            me.setStyle(DISPLAY, getDisplay(me)); // first try reverting to default
            if (me.isStyle(DISPLAY, NONE)) { // if that fails, default to block
                me.setStyle(DISPLAY, "block");
            }
        }
    },

    /**
     * Hide this element - Uses display mode to determine whether to use "display" or "visibility". See {@link #setVisible}.
     * @param {Boolean/Object} [animate] true for the default animation or a standard Element animation config object
     * @return {Ext.dom.Element} this
     */
    hide : function(animate){
        // hideMode override
        if (typeof animate == 'string'){
            this.setVisible(false, animate);
            return this;
        }
        this.setVisible(false, this.anim(animate));
        return this;
    },

    /**
     * Show this element - Uses display mode to determine whether to use "display" or "visibility". See {@link #setVisible}.
     * @param {Boolean/Object} [animate] true for the default animation or a standard Element animation config object
     * @return {Ext.dom.Element} this
     */
    show : function(animate){
        // hideMode override
        if (typeof animate == 'string'){
            this.setVisible(true, animate);
            return this;
        }
        this.setVisible(true, this.anim(animate));
        return this;
    }
});

})();

