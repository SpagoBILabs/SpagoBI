Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverUsePanel', {
	extend: 'Ext.form.Panel'

		,config: {
			bodyPadding: '5 5 5',
			defaults: {
				width: "100%",
				layout:'fit'
			},   

			fieldDefaults: {
				labelAlign: 'right',
				msgTarget: 'side'
			},
			border: false
		},

		constructor: function(config) {

			this.initConfig(config);
			this.initFields();
			this.items=[this.left, this.right];
			this.callParent(arguments);
		},

		initFields: function(){

			this.adid = Ext.create("Ext.form.field.Hidden", {
				name: "ADID",
				fieldLabel: "AD ID"
			});

			this.adid.setRawValue(this.exampleID);

			this.useid = Ext.create("Ext.form.field.Hidden", {
				name: "USEID",
				fieldLabel: "Use ID"
			});

			this.name = Ext.create("Ext.form.field.Text", {
				allowBlank: false,
				fieldLabel: "Name"
			});

			this.label = Ext.create("Ext.form.field.Text", {
				allowBlank: false,
				fieldLabel: "Label"
			});

			this.description = Ext.create("Ext.form.field.Text", {
				allowBlank: false,
				fieldLabel: "Description"
			});

			this.manualinput = Ext.create("Ext.form.field.Radio",{
				hideEmptyLabel: false,
				fieldLabel: 'Manual input/LOV',
				boxLabel: "Manual input",
				checked: true,
				name: 'lovorman'

			});

			this.expendable = Ext.create("Ext.form.Checkbox",{

				hideEmptyLabel: false,
				boxLabel: 'Expendable'

			});

			this.lov = Ext.create("Ext.form.field.Radio",{
				hideEmptyLabel: false,
				boxLabel: "LOV",
				name: 'lovorman'

			});

			this.useLovStore = Ext.create('Ext.data.Store',{
				model: "LovModel",
			});

			this.lovpopupid = Ext.create("Ext.form.field.Hidden", {
				hideEmptyLabel: false,
				disabled: true
			});

			var thisPanel = this;
			this.lovpopup = Ext.create('Ext.form.field.Trigger', {
				triggerCls:'x-form-question-trigger',
				disabled: true,
				hideEmptyLabel: false,
				onTriggerClick: function(e) {

					if(!thisPanel.win){

						var p = new Ext.grid.Panel({
							bodyStyle: 'background-color: white;',
							store: thisPanel.useLovStore,

							columns: [
							          { text: 'id',  dataIndex: 'LOV_ID', hidden: true },
							          { text: 'Name',  dataIndex: 'LOV_NAME' },
							          { text: 'Description', dataIndex: 'LOV_DESCRIPTION', flex: 1 }
							          ]
						});


						thisPanel.win = new Ext.Window({
							layout:'fit',
							bodyStyle: 'background-color: white;',
							width:500,
							height:300,
							title: 'List of values',
							closeAction:'hide',
							plain: true,
							items: [p],
							listeners: {
								dblclick: {
									element: 'body',
									fn: function(){
										thisPanel.lovpopupid.setValue(p.getSelectionModel().getSelection()[0].data.LOV_ID);
										thisPanel.lovpopup.setValue(p.getSelectionModel().getSelection()[0].data.LOV_NAME);
										thisPanel.win.hide();
									}
								}
							},
							buttons: [{
								text: 'Close',
								handler: function(){
									thisPanel.win.hide();
								}
							}]
						});
					}
					thisPanel.win.show(this);
				}

			});

			this.selections = Ext.create('Ext.form.ComboBox',{

				hideEmptyLabel: false,
				editable: false,
				displayField: 'VALUE_NM',
				valueField: 'VALUE_CD',
				disabled: true

			});

			this.lov.addListener('change', this.manlov, this);

			this.nonedv = Ext.create("Ext.form.field.Radio",{
				hideEmptyLabel: false,
				fieldLabel: 'Default Value',
				boxLabel: "None",
				checked: true,
				name: 'defaultvalue'

			});

			this.nonedv.addListener('change', this.dvnone, this);

			this.uselov = Ext.create("Ext.form.field.Radio",{
				hideEmptyLabel: false,
				boxLabel: "Use a LOV",
				name: 'defaultvalue'

			});

			this.uselov.addListener('change', this.dvuselov, this);
			
			this.dllovpopupid = Ext.create("Ext.form.field.Hidden", {
				hideEmptyLabel: false,
				disabled: true
			});

			var thisPanel = this;
			this.dllovpopup = Ext.create('Ext.form.field.Trigger', {
				triggerCls:'x-form-question-trigger',
				disabled: true,
				hideEmptyLabel: false,
				onTriggerClick: function(e) {

					if(!thisPanel.dvwin){

						var dvp = new Ext.grid.Panel({
							bodyStyle: 'background-color: white;',
							store: thisPanel.useLovStore,

							columns: [
							          { text: 'id',  dataIndex: 'LOV_ID', hidden: true },
							          { text: 'Name',  dataIndex: 'LOV_NAME' },
							          { text: 'Description', dataIndex: 'LOV_DESCRIPTION', flex: 1 }
							          ]
						});


						thisPanel.dvwin = new Ext.Window({
							layout:'fit',
							bodyStyle: 'background-color: white;',
							width:500,
							height:300,
							title: 'List of values',
							closeAction:'hide',
							plain: true,
							items: [dvp],
							listeners: {
								dblclick: {
									element: 'body',
									fn: function(){
										thisPanel.dllovpopupid.setValue(dvp.getSelectionModel().getSelection()[0].data.LOV_ID);
										thisPanel.dllovpopup.setValue(dvp.getSelectionModel().getSelection()[0].data.LOV_NAME);
										thisPanel.dvwin.hide();
									}
								}
							},
							buttons: [{
								text: 'Close',
								handler: function(){
									thisPanel.dvwin.hide();
								}
							}]
						});
					}
					thisPanel.dvwin.show(this);
				}

			});

			this.pickup = Ext.create("Ext.form.field.Radio",{
				hideEmptyLabel: false,
				boxLabel: "Pick up",
				name: 'defaultvalue'

			});

			this.pickup.addListener('change', this.dvpickup, this);

			this.pickupcombo = Ext.create('Ext.form.ComboBox',{

				editable: false,
				hideEmptyLabel: false,
				disabled: true,
				displayField: 'defaultFormulaDesc',
				valueField: 'defaultFormulaValue'

			});

			this.general = Ext.create('Ext.panel.Panel',{
				title: 'Analytical driver use mode details',
				items: [this.adid, this.useid, this.name, this.label, this.description],
				layout: 'anchor',
				bodyPadding: '5 5 5',
				border: false

			});

			this.manlov = Ext.create('Ext.panel.Panel',{

				items: [this.manualinput, this.expendable, this.lov, this.lovpopupid, this.lovpopup, this.selections],
				layout: 'anchor',
				bodyPadding: '5 5 5',
				border: false

			});

			this.defvalue = Ext.create('Ext.panel.Panel',{

				items: [this.nonedv, this.uselov, this.dllovpopupid, this.dllovpopup, this.pickup, this.pickupcombo],
				layout: 'anchor', 
				bodyPadding: '5 5 5',
				border: false

			});

			this.adUsesList = Ext.create('Ext.grid.Panel', {

				border: true,
				hideHeaders: true,
				overflowY: 'auto',
				layout: 'fit',
				maxHeight: 200,
				columns: [{
					hidden: true,
					dataIndex: 'ID'
				}, {
					flex: 1,
					dataIndex: 'DESCRIPTION',
					field: {
						xtype: 'textfield'
					}
				}]
			});

			this.useRolesStore =  Ext.create('Ext.data.Store',{
				model: "RoleModel"
			});

			this.rolesList = Ext.create('Ext.grid.Panel', {
				store: this.useRolesStore,
				border: true,
				hideHeaders: true,
				overflowY: 'auto',
				overflowX: 'auto',
				layout: 'anchor',
				maxHeight: 200,
				columns: [{
					hidden: true,
					dataIndex: 'id'
				}, {
					flex: 1,
					dataIndex: 'description',
					field: {
						xtype: 'textfield'
					}
				}, {
					xtype: 'checkcolumn',
					dataIndex: 'CHECKED',
					width: 60
				}]
			});

			this.roles = Ext.create('Ext.panel.Panel',{

				title: 'Roles Associations',
				items: [this.rolesList],
				border: false,
				bodyPadding: '5 5 5',
				autoScroll : true

			});

			this.useConstStore =  Ext.create('Ext.data.Store',{
				model: "ConstraintModel"
			});

			this.constraintsList = Ext.create('Ext.grid.Panel', {
				store: this.useConstStore,
				border: true,
				layout: 'anchor',
				maxHeight: 200,
				hideHeaders: true,
				overflowY: 'auto',
				overflowX: 'auto',
				columns: [{
					hidden: true,
					dataIndex: 'VALUE_CD'
				}, {

					flex: 1,
					dataIndex: 'VALUE_DS',
					field: {
						xtype: 'textfield'
					}
				}, {
					xtype: 'checkcolumn',
					dataIndex: 'CHECKED',
					width: 60
				}]
			});

			this.constraints = Ext.create('Ext.form.Panel',{

				title: 'Predefined values constraints',
				items: [this.constraintsList],
				border: false,
				width: '100%',
				bodyPadding: '5 5 5',
				autoScroll : true

			});

			this.left = Ext.create('Ext.form.Panel',{

				items: [this.general, this.manlov, this.defvalue],
				width: "50%",
				bodyPadding: '5 5 5',
				border: false

			});

			this.right = Ext.create('Ext.panel.Panel',{

				items: [this.roles,this.constraints],
				width: "49%",
				bodyPadding: '5 5 5',
				border: false

			});


		},


		manlov: function(check, checked){

			var lovsel = checked;
			if(lovsel != null && lovsel == true){

				this.expendable.setValue(false);
				this.expendable.disable();
				this.lovpopupid.enable();
				this.lovpopup.enable();
				this.lovpopup.setValue("--Choose LOV--");
				this.selections.enable();
				this.selections.setValue("--Choose selection--");

			}
			else{

				this.expendable.enable();
				this.lovpopupid.disable();
				this.lovpopupid.setValue('');
				this.lovpopup.disable();
				this.lovpopup.setValue('');
				this.selections.disable();
				this.selections.setValue('');

			}

		},
		dvnone: function(check, checked){

			var nonesel = checked;
			if(nonesel != null && nonesel == true){
				
				this.dllovpopupid.setValue('');
				this.dllovpopupid.disable();
				
				this.dllovpopup.setValue('');
				this.dllovpopup.disable();
				

				this.pickupcombo.setValue('');
				this.pickupcombo.disable();


			} 	

		},
		dvuselov: function(check, checked){

			var uselovsel = checked;
			if(uselovsel != null && uselovsel == true){
				
				this.dllovpopupid.enable();
				
				this.dllovpopup.enable();
				this.dllovpopup.setValue("--Choose LOV--");
				
				

				this.pickupcombo.setValue('');
				this.pickupcombo.disable();


			} 	

		},
		dvpickup: function(check, checked){

			var pickupsel = checked;
			if(pickupsel != null && pickupsel == true){
				
				this.dllovpopupid.setValue('');
				this.dllovpopupid.disable();
				
				this.dllovpopup.setValue('');
				this.dllovpopup.disable();

				this.pickupcombo.enable();
				this.pickupcombo.setValue("--Choose pickup--");

			} 	

		}

});