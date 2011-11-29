/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIDetail =  function(config) {
		
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiGUIDetail) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiGUIDetail);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		this.initDetail(c);
   
		Sbi.kpi.KpiGUIDetail.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUIDetail , Ext.form.FormPanel, {
	items: null,
	threshFields: null,
	valueItem: null,
	weightItem: null,
	targetItem: null,
	
	initDetail: function(){	
		this.defaultType= 'label';		
		this.threshFields = new Ext.form.FieldSet({
	        // Fieldset thresholds
	        xtype:'fieldset',
	        layout:'fit',
	        border: false,
	        defaultType: 'fieldset',
	        items: []
	    });
		this.items =[this.threshFields];
	}
	, update:  function(field){
		if(this.valueItem != null){
			this.remove(this.valueItem);
		}
		if(this.weightItem != null){
			this.remove(this.weightItem);
		}
		if(this.targetItem != null){
			this.remove(this.targetItem);
		}
		//value
		var val = field.attributes.actual;
		this.valueItem = new Ext.form.DisplayField({fieldLabel: 'Valore', 
													value: val, 
													style: 'font-style: italic;'});
		this.add(this.valueItem );
		
		var thrArray = field.attributes.thresholds;
		this.threshFields.removeAll();
		if(thrArray != undefined && thrArray !== null){
			for(i=0; i<thrArray.length; i++){
				var bItems = [];
				var thrLine = new Ext.form.FieldSet({
			        xtype:'fieldset',			        
			        style: 'padding:0px; margin: 1px;',
			        border:false,
			        layout: 'column',
			        autoHeight:true,
			        minHeight:20,
			        defaults: {
			            anchor: '0'
			        },
			        defaultType: 'displayfield',
			        items : bItems
			    });
				
				
				var thr = thrArray[i];	
				var colorBox = new Ext.form.DisplayField({value: thr.color, 
															columnWidth:0.3,
															style: 'background-color:'+thr.color});
				
				thrLine.add([ {value: thr.label, columnWidth:0.3, style:'font-weight: bold'}, 
				              {columnWidth:0.2, value: thr.min }, 
				              {value: thr.max, columnWidth:0.2}, 
				              colorBox]);
				
				this.threshFields.add(thrLine);


			}

		}
		//weight
		var weight = field.attributes.weight;
		this.weightItem = new Ext.form.DisplayField({fieldLabel: 'Peso', 
													value: weight});
		this.add(this.weightItem );
		//target
		var target = field.attributes.target;
		this.targetItem = new Ext.form.DisplayField({fieldLabel: 'Target', 
													value: target});
		this.add(this.targetItem );
		this.doLayout();
        this.render();
	}
});