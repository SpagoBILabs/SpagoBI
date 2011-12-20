/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Chiara Chiarelli (chiara.chiarelli@eng.it)
  */


Ext.ns("Sbi.widgets");


Sbi.widgets.TriggerFieldMultiButton = function(config) {    
	
	
	var c = Ext.apply({}, config, {});   
	    
	Sbi.widgets.TriggerFieldMultiButton.superclass.constructor.call(this, c);  

};


Sbi.widgets.TriggerFieldMultiButton = Ext.extend(Ext.form.TwinTriggerField, {
    
	validationEvent:false,
	validateOnBlur:false,
	trigger1Class:'x-form-search-trigger',
	trigger2Class:'trigger-up',
	
	initComponent : function(){
		Sbi.widgets.TriggerFieldMultiButton.superclass.initComponent.call(this);       
    },  
    
    onRender : function(ct, position){
    	
    	Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = this.wrap.createChild(this.triggerConfig ||
                {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: this.triggerClass});
        this.initTrigger();
    	
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        } else {
        }    	
    }

});
