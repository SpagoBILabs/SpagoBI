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
	this.store = config.store;
	this.store.on('load', this.refreshOptions, this);
	this.store.load();
	
	//this.addEvents('change');
	
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
	
	originalIndex: null,
	thumbRendered: false,
	storeLoaded: false,
	
	  /**
     * Initialize the component.
     * @private
     */
    initComponent : function() { 	
    	Sbi.trace("[Sbi.SliderField.initComponent] : [" + this.name + "] : IN");
    	this.sliderCfgProperties.push('values');
    	this.sliderCfgProperties.push('value');
       
    	Sbi.widgets.SliderField.superclass.initComponent.call(this);
    	Sbi.trace("[Sbi.SliderField.initComponent] : [" + this.name + "] : OUT");
    }, 
    
    initSlider : function(cfg) {
    	Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : IN");
    	this.slider = new Ext.slider.MultiSlider(cfg);
    	this.slider.store = this.store;
    	Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] :  OUT");
    	return this.slider;
    }, 
    
    afterRender : function(){
    	Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : IN");
    	Sbi.widgets.SliderField.superclass.afterRender.call(this);
        if(this.multiSelect === true) {
        	// the add an extra tab
        	this.slider.addThumb();
        	Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : second thumb added succesfully to the multiselect slider");
        } else {
        	Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : the slider is not multiselect so there is no need to add the second thumb");
        }
        this.originalIndex = this.multiSelect===true? [0,this.slider.maxValue]: [0];
        Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : originalIndex set equal to " + this.originalIndex);
        
        this.thumbRendered = true;
        Sbi.trace("[Sbi.SliderField.afterRender] : [" + this.name + "] : OUT");
    },
    
    refreshOptions : function() {
    	Sbi.trace("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : IN");
    	var recordNo = this.store.getTotalCount();
    	Sbi.debug("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : loaded store contains [" + recordNo + "] records");
    	Sbi.debug("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : slider alredy rendered [" + this.slider.rendered + "]");
    	this.slider.setMaxValue(recordNo-1); // first record index is 0, last is recordNo-1
    	Sbi.trace("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : indexes (pre): " + this.getIndexes());
    	var indexes = this.multiSelect===true? [0,this.slider.maxValue]: [0]; 
    	this.setIndexes(indexes);
    	Sbi.trace("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : indexes (post): " + this.getIndexes());
    	this.originalIndex = indexes;
    	Sbi.trace("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : originalIndex set equal to: " + this.originalIndex);
    	this.storeLoaded = true;
    	Sbi.debug("[Sbi.SliderField.refreshOptions] : [" + this.name + "] : OUT");
    },
    
    reset : function (){
    	Sbi.trace("[Sbi.SliderField.reset] : [" + this.name + "] :  IN");
        this.setIndexes(this.originalIndex);
        this.clearInvalid();
        Sbi.trace("[Sbi.SliderField.reset] : [" + this.name + "] : OUT");
    },
    
    
   
    
	/**
     * Sets the value for this field.
     * @param {Number} v The new value.
     * @param {Boolean} animate (optional) Whether to animate the transition. If not specified, it will default to the animate config.
     * @return {Ext.form.SliderField} this
     */
    setIndexes : function(v, animate, silent){
    	Sbi.trace("[Sbi.SliderField.setIndexes] : [" + this.name + "] :  IN");
    	
    	Sbi.debug("[Sbi.SliderField.setIndex] : [" + this.name + "] :  set value to [" + v + "]");
    	
    	if(v === "" || v === undefined) { // it's a reset...
        	v = [this.slider.minValue];
        	if(this.multiSelect == true){
        		v.push(this.slider.maxValue);
        	}
        }        
        if(!Ext.isArray(v)) {
        	v = [v];
        }
        
        // silent is used if the setValue method is invoked by the slider
        // which means we don't need to set the value on the slider.
        if(!silent){
        	this.slider.setValues(v, animate);
        }
    	Sbi.trace("[Sbi.SliderField.setIndexes] : [" + this.name + "] : OUT");
    	return this;
    },
    
    /**
     * Sets the value for this field.
     * @param {Number} v The new value.
     * @param {Boolean} animate (optional) Whether to animate the transition. If not specified, it will default to the animate config.
     * @return {Ext.form.SliderField} this
     */
    setValue : function(v, animate, silent){
    	Sbi.trace("[Sbi.SliderField.setValue] : [" + this.name + "] : IN");
    	
    	Sbi.debug("[Sbi.SliderField.setValue] : [" + this.name + "] : set value to [" + v + "]");
    	
    	if(v === "" || v === undefined) { // it's a reset...
        	v = [this.slider.minValue];
        	if(this.multiSelect == true){
        		v.push(this.slider.maxValue);
        	}
        }        
        if(!Ext.isArray(v)) {
        	v = [v];
        }
        
       
        var index;
        
        index = this.store.find(this.valueField, v[0]);
        if(index === -1) {
        	Sbi.warn("[Sbi.SliderField.setValue] : [" + this.name + "] : value [" + v[0] + "] is not contained in the dataset");
        } else {
        	Sbi.trace("[Sbi.SliderField.setValue] : [" + this.name + "] : index of value [" + v[0] + "] is equal to [" + index + "]");
        	this.slider.setValue(0, index);
        }
        
       
        
        if(this.multiSelect == true) {
        	index = this.store.find(this.valueField, v[v.length-1]);
        	if(index === -1) {
            	Sbi.warn("[Sbi.SliderField.setValue] : [" + this.name + "] : value [" + v[v.length-1] + "] is not contained in the dataset");
            } else {
            	Sbi.trace("[Sbi.SliderField.setValue] : [" + this.name + "] : index of value [" + v[v.length-1] + "] is equal to [" + index + "]");
            	this.slider.setValue(1, index);
            }
        }
       
    	Sbi.trace("[Sbi.SliderField.setValue] : [" + this.name + "] : OUT");
    	
        return this;
    },
    
    /**
     * Gets the current value for this field.
     * @return {Number} The current value.
     */
    getIndexes : function(){
        return this.slider.getValues();    
    },
       
    /**
     * Gets the current value for this field.
     * @return {Number} The current value.
     */
    getValue : function(){
    	Sbi.trace("[Sbi.SliderField.getValue] : [" + this.name + "] : IN");
    	if(this.storeLoaded == false) {
    		Sbi.warn("[Sbi.SliderField.getValue] : [" + this.name + "] : value not set because the store has no been yet loaded");
    		return undefined;
    	}
    	var values = this.getValues();
    	var value = this.multiSelect == true? values: values[0];
    	Sbi.trace("[Sbi.SliderField.getValue] : [" + this.name + "] : value is equal to " + value + "");
        
    	Sbi.trace("[Sbi.SliderField.getValue] : [" + this.name + "] : OUT");
        return value;
    },
    
    getValues : function() {
    	Sbi.trace("[Sbi.SliderField.getValues] : [" + this.name + "] : IN");
    	
    	if(this.storeLoaded == false) {
    		Sbi.trace("[Sbi.SliderField.getValues] : Value is not set because the store has not be loaded yet");
    		return undefined;
    	}
    	
    	var records;
    	var indexes = this.getIndexes();
    	if(this.multiSelect == true) {
    		records = this.store.getRange(indexes[0], indexes[1]);
    	} else {
    		records = [this.store.getAt(indexes[0])];
    	}
    	Sbi.trace("[Sbi.SliderField.getValues] : [" + this.name + "] : Selected values numeber is equalt to [" + records.length + "]");
    	
    	var values = [];
    	for(var i  = 0; i < records.length; i++) {
    		var record = records[i];
    		values.push( record.get(this.valueField) );
    	}
	
    	Sbi.trace("[Sbi.SliderField.getValues] : [" + this.name + "] : OUT");
    	
    	return values;
    },
    
    getRawValue : function() {
    	var values = this.getValues();
		return values? values.join() : values;
	}

});
