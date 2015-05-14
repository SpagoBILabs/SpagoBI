Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverDetailPanel', {
	extend: 'Ext.form.Panel'
		,config: {

			bodyPadding: '5 5 0',
			defaults: {
				width: 400,
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
			this.items=[this.adid, this.name, this.label, this.description, this.adTypes, this.functionalflag, this.temporalflag];
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
				name: "FUNCTIONALFLAG",
				fieldLabel: "Functional"
			});

			this.temporalflag = Ext.create("Ext.form.Checkbox", {
				name: "TEMPORALFLAG",
				fieldLabel: "Temporal"
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