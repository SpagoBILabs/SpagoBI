/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * Sbi.widgets.SliderField
 * 
 * Authors
 *  - Andrea Gioia (mail)
 */

Ext.ns("Sbi.widgets");

Sbi.widgets.SliderField = function(config) {

//	var c = Ext.apply({
//		columns : 1,
//		items : [ {
//			boxLabel : 'Loading options...',
//			name : 'loading-mask',
//			value : 'mask'
//		}]
//	}, config || {});
//	
//	this.store = config.store;
//	this.store.on('load', this.refreshOptions, this);
//	this.store.load();
//	
//	this.addEvents('change');
	
	// constructor
	Sbi.widgets.SliderField.superclass.constructor.call(this, config);
	
};

Ext.extend(Sbi.widgets.SliderField, Ext.form.SliderField , {
	
	 /**
     * @cfg {Boolean} multiSelect
     * True to have two thums instead that only one in order to allow the user to select a range 
     * and not only e punctual value. Defaults to <tt>false</tt>.
     */
	multiSelect: false,
	
	  /**
     * Initialize the component.
     * @private
     */
    initComponent : function() { 	
    	this.sliderCfgProperties.push('values');
    	this.sliderCfgProperties.push('value');
       
    	Sbi.widgets.SliderField.superclass.initComponent.call(this);
    }, 
    
    initSlider : function(cfg) {
    	Sbi.debug("[SliderField.initSlider] :  MultiSlider");
    	this.slider = new Ext.slider.MultiSlider(cfg);
    	return this.slider;
    }, 
    
    reset : function (){
    	
        this.setValue(this.originalValue);
        this.clearInvalid();
    },
    
	/**
     * Sets the value for this field.
     * @param {Number} v The new value.
     * @param {Boolean} animate (optional) Whether to animate the transition. If not specified, it will default to the animate config.
     * @return {Ext.form.SliderField} this
     */
    setValue : function(v, animate, /* private */ silent){
    	Sbi.debug("[SliderField.setValue] :  set value to [" + v + "]");
    	Sbi.debug("[SliderField.setValue] :  this.reset : " + this.reset);
    	
        if(v === "" || v === undefined) { // it's a reset...
        	v = [];
        	for(var i = 0; i < this.slider.thumbs.length; i++) {
        		v.push(this.slider.minValue);
        	}
      		
        	Sbi.debug("[SliderField.setValue] :  this is a reset [" + this.slider.minValue + "]");
        }
        
        if(!Ext.isArray(v)) {
        	v = [v];
        }
        
        // silent is used if the setValue method is invoked by the slider
        // which means we don't need to set the value on the slider.
    	if(!silent){
    		for(var i = 0; i < v.length; i++) {
    			this.slider.setValue(i, v[i], animate);
    		}	
        }
    	
        return Sbi.widgets.SliderField.superclass.setValue.call(this, this.slider.getValue(), animate, true);
    },
    
    /**
     * Gets the current value for this field.
     * @return {Number} The current value.
     */
    getValues : function(){
        return this.slider.getValues();    
    }, 
    
    getValue : function(){
        return this.getValues();    
    }
});
