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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageGoalsDetailsPanel = function(config, ref) { 

	var c = this.initForm(config);
	
	Sbi.kpi.ManageGoalsDetailsPanel.superclass.constructor.call(this, c);	 	


};

Ext.extend(Sbi.kpi.ManageGoalsDetailsPanel, Ext.FormPanel, {

	 detailFieldLabel: null 
	,detailFieldName: null  
	,detailFieldDescr: null
    ,detailFieldFrom: null
    ,detailFieldTo: null
    ,detailFieldGrant: null
	
	,initForm: function(config){
		var thisPanel = this;
		
		//fileds of the detail panel
		this.detailFieldLabel = new Ext.form.TextField({
			minLength:1,
			fieldLabel:LN('sbi.generic.label'),
			allowBlank: false,
			//validationEvent:true,
			name: 'label'
		});	  
	
		this.detailFieldName = new Ext.form.TextField({
			maxLength:100,
			minLength:1,
			fieldLabel: LN('sbi.generic.name'),
			allowBlank: false,
			name: 'name'
		});

		this.detailFieldDescr = new Ext.form.TextArea({
			maxLength:400,
			width : 250,
			height : 80,
			fieldLabel: LN('sbi.generic.descr'),
			name: 'description',
			allowBlank: false
		});
	
		this.detailFieldFrom = new Ext.form.DateField({
			id: 'from',
			name: 'from',
			fieldLabel: LN('sbi.generic.from'),
			format: 'd/m/Y',
			allowBlank: false
		});
	
		this.detailFieldTo = new Ext.form.DateField({
			id: 'to',
			name: 'to',
			fieldLabel: LN('sbi.generic.to'),
			format: 'd/m/Y',
			allowBlank: false
		});
		
		
		this.detailFieldGrant = new Ext.form.TextField({
			minLength:1,
			fieldLabel:'Grant',
			allowBlank: false,
			//validationEvent:true,
			name: 'grant'
		});	  
	
		var baseConfig = {drawFilterToolbar:false}; 
	
	
		var grantStore = new Ext.data.JsonStore({
			url: config.manageGrantListService,
			root: 'rows',
			fields: ['id','label','name','description','modelinstance']
		});
	
		this.detailFieldGrant = new Sbi.widgets.LookupField(Ext.apply( baseConfig, {
			name: 'name',
			valueField: 'modelinstance',
			displayField: 'label',
			descriptionField: 'description',
			fieldLabel: LN('sbi.grants.listTitle'),
			store: grantStore,
			singleSelect: true,
			allowBlank: false,
			cm: new Ext.grid.ColumnModel([
			                              new Ext.grid.RowNumberer(),
			                              {   header: LN('sbi.generic.label'),
			                            	  dataIndex: 'label',
			                            	  width: 75
			                              },
			                              {   header: LN('sbi.generic.name'),
			                            	  dataIndex: 'name',
			                            	  width: 75
			                              },
			                              {   header: LN('sbi.generic.descr'),
			                            	  dataIndex: 'description',
			                            	  width: 75
			                              }
			                              ])
		}));
	
		var tbSave2 = new Ext.Toolbar( {
			buttonAlign : 'right',
			items : [ 
			         new Ext.Toolbar.Button( {
			        	 text : LN('sbi.generic.update'),
			        	 iconCls : 'icon-save',
			        	 handler : this.save,
			        	 width : 30,
			        	 scope : thisPanel
			         })
			         ]
		});
	
		
		var conf = {
       	 title: LN('sbi.generic.details')
    	 , itemId: 'detail'
    	 , tbar: tbSave2
    	 , width: 430
    	 , items: [{
    		 id: 'items-detail',   	
    		 itemId: 'items-detail',               
    		 columnWidth: 2,
    		 xtype: 'fieldset',
    		 labelWidth: 150,
    		 defaults: {width: 200, border:false},    
    		 defaultType: 'textfield',
    		 autoHeight: true,
    		 autoScroll  : true,
    		 bodyStyle: Ext.isIE ? 'padding:15 0 5px 10px;' : 'padding:10px 15px;',
    		 border: false,
    		 style: {
    			 "margin-left": "10px", 
    			 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
    		 },
    		 items: [this.detailFieldLabel, this.detailFieldName,  this.detailFieldDescr,
    		        this.detailFieldFrom, this.detailFieldTo, this.detailFieldGrant]
    	 }]};

		return conf;
	}
});


