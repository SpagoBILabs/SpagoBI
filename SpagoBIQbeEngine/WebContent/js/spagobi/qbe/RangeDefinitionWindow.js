/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.ZONE
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
  * RangeDefinitionWindow - short description
  * 
  * Object documentation ...
  * 
  * by Monica Franceschini
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.RangeDefinitionWindow = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		title: 'Range Definition'
		, width: 400
		, height: 150
		, hasBuddy: false	
		
	});

	Ext.apply(this, c);
	
	this.initMainPanel(c);	
	
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	Sbi.qbe.RangeDefinitionWindow.superclass.constructor.call(this, c);  
	this.add(this.mainPanel);
  
};

Ext.extend(Sbi.qbe.RangeDefinitionWindow, Ext.Window, {
	
	hasBuddy: null
    , buddy: null
    , slotPanel : null
   
    , mainPanel: null
    , rangeFrom: null
    , rangeTo: null
    , rangeToSave: {}
    
	, initMainPanel: function(c) {
		
		this.slotPanel = c.slotPanel;
		var record = c.record;
		
		var btnFinish = new Ext.Button({
	        text: 'Save',
	        disabled: false,
	        scope: this,
	        handler : this.save.createDelegate(this, [record])
		});
		
	      var valuesFrom = new Ext.data.SimpleStore({
	          fields: ['id', 'value'],
	          data : [['1','&gt;'],['2','&gt;=']]
	      });
	      var valuesTo = new Ext.data.SimpleStore({
	          fields: ['id', 'value'],
	          data : [['3',Ext.util.Format.htmlEncode('<')],['4','&lt;=']]
	      });
	      
		this.rangeFrom = new Ext.form.ComboBox({
		    allowBlank: false,
		    width: 50,
		    triggerAction: 'all',
		    lazyRender:true,
		    mode: 'local',
		    store: valuesFrom,
		    valueField: 'id',
		    displayField: 'value'
		});
		this.rangeFromValue = new Ext.form.TextField({
			width: 70
		});
		
		this.rangeTo = new Ext.form.ComboBox({
		    allowBlank: false,
		    width: 50,
		    triggerAction: 'all',
		    lazyRender:true,
		    mode: 'local',
		    store: valuesTo,
		    valueField: 'id',
		    displayField: 'value'
		});
		this.rangeToValue = new Ext.form.TextField({
			width: 70
		});
		
		this.mainPanel = new Ext.form.FormPanel({  
			    layout: 'column',  
			    scope: this,
				width: 385,
				height: 120,
				
			    defaults: {border:false},  
			    bbar: ['->',
			           btnFinish
			    ], 
			    items: [{
			    	//rang from
			        xtype:'fieldset',
			        columnWidth: 0.48,
			        layout: 'hbox',   
			        title: 'MIN',
			        labelWidth: 0,
			        //autoHeight:true,
			        style: 'margin: 2px; border: 1px solid silver; float: left;',
			        defaultType: 'textfield',
			        padding:5,
			        items :[
			                this.rangeFrom, this.rangeFromValue
			        ]
			    } , {
			    	//rang to
			        xtype:'fieldset',
			        columnWidth: 0.48,
			        layout: 'hbox',   
			        labelWidth: 0,
			        style: 'margin: 2px; border: 1px solid silver; float: left;',
			        title: 'MAX',
			        //autoHeight:true,
			        padding:5,
			        defaultType: 'textfield',
			        items :[
			                this.rangeTo, this.rangeToValue
			        ]
			    } ]
		});  

		this.mainPanel.doLayout();

    }
	, save: function(rec){
		
		this.rangeToSave.from ={};
		this.rangeToSave.to ={};
		this.rangeToSave.desc ={};
		
		this.rangeToSave.from.operand = this.rangeFrom.value;
		this.rangeToSave.from.value = this.rangeFromValue.getValue();
		this.rangeToSave.to.operand = this.rangeTo.value;
		this.rangeToSave.to.value = this.rangeToValue.getValue();

		var descr = this.rangeToSave.from.operand+' '+this.rangeToSave.from.value+ ' '+this.rangeToSave.to.operand+' '+this.rangeToSave.to.value;
		this.rangeToSave.desc =descr;
		
		this.slotPanel.addRange(this.rangeToSave, rec);
		
		this.close();
	}

});