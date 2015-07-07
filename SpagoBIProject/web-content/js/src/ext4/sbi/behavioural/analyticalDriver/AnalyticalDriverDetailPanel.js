/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**     
 * @author
 * Lazar Kostic (lazar.kostic@mht.net)
 */

Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverDetailPanel', {
	extend: 'Ext.form.Panel'
		,config: {

			bodyPadding: '5 5 0',
			defaults: {
				width: 700,
				layout:'fit'
			},        
			fieldDefaults: {
				labelAlign: 'right',
				msgTarget: 'side'
			},
			border: true,
			services:[]
		},

		constructor: function(config) {

			this.initConfig(config);
			this.initFields();
			this.items=[this.upper];
			this.addEvents('save');
			this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[{name:'->'},{name:'save'}]},this);

			this.tbar.on("save",function(){

				this.fireEvent("save", this.getValues());
				this.setFormState(this.getValues());

			},this);
			this.callParent(arguments);
			this.on("render",function(){this.hide()},this);
		},

		initFields: function(){

			this.adid = Ext.create("Ext.form.field.Hidden", {
				name: "ID",
				fieldLabel: "ID",
				readOnly: true
			});

			this.name = Ext.create("Ext.form.field.Text", {
				name: "NAME",
				fieldLabel: "Name",
				allowBlank: false
			});

			this.label = Ext.create("Ext.form.field.Text", {
				name: "LABEL",
				fieldLabel: "Label",
				allowBlank: false
			});

			this.description = Ext.create("Ext.form.field.Text", {
				name: "DESCRIPTION",
				fieldLabel: "Description",
				allowBlank: false
			});
			
			this.left = Ext.create('Ext.panel.Panel',{

				items: [this.adid, this.name, this.label, this.description],
				layout: 'anchor', 
				bodyPadding: '5 5 5',
				border: false

			});

			Ext.define("TypesModel", {
				extend: 'Ext.data.Model',
				fields: ["VALUE_ID","VALUE_DS","VALUE_NM","VALUE_CD"]
			});

			var typesStore=  Ext.create('Ext.data.Store',{
				model: "TypesModel",
				proxy: {
					type: 'ajax',
					extraParams : {DOMAIN_TYPE:"PAR_TYPE"},
					url:  this.services['getTypes'],
					reader: {
						type:"json"
					}
				}
			});

			typesStore.load();

			this.adTypes = new Ext.create('Ext.form.ComboBox', {
				name: 'INPUTTYPECD',
				fieldLabel: 'Type',
				store: typesStore,
				valueField:'VALUE_CD',
				displayField:'VALUE_NM',
				allowBlank: false,
				editable: false

			});

			this.functionalflag = Ext.create("Ext.form.Checkbox", {
				padding: "2 0 0 65",
				name: "FUNCTIONALFLAG",
				fieldLabel: "Functional"
			});

			this.temporalflag = Ext.create("Ext.form.Checkbox", {
				padding: "2 0 0 65",
				name: "TEMPORALFLAG",
				fieldLabel: "Temporal"
			});
			
			this.right = Ext.create('Ext.panel.Panel',{

				items: [this.adTypes, this.functionalflag, this.temporalflag],
				layout: 'vbox', 
				bodyPadding: '5 5 5',
				border: false

			});
			
			this.upper = Ext.create('Ext.panel.Panel',{

				items: [this.left, this.right],
				layout: 'hbox', 
				bodyPadding: '5 5 20',
				border: false

			});

		},

		setFormState: function(values, recordID){

			var scope = this;
			var v = values;
			this.getForm().setValues(v);
			this.adTypes.setValue(values.INPUTTYPECD);

			if (this.tabPanel){

				this.tabPanel.destroy();

			}

			if(this.adid.value){

				this.tabPanel = Ext.create("Sbi.behavioural.analyticalDriver.AnalyticalDriverUseTabPanel", {

					width : "35%",
					exampleID: this.adid.value

				});

				this.add(this.tabPanel);
			}
		},

		getValues: function(){

			var values = this.callParent();
			return values;

		}


});