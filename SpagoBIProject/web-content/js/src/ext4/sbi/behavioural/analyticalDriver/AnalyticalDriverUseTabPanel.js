Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverUseTabPanel', {
	extend: 'Ext.tab.Panel'

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
		}
,constructor: function(config) {

	this.exampleID = config.exampleID;
	this.initConfig(config);
	this.services = [];
	this.initServices();
	this.initFields();
	this.items=[this.newUse];
	this.addEvents('removeUse','saveUse');
	this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[{name:'->'},{name: 'remove'},{name:'save'}]},this);
	this.tbar.on("save",function(){
		this.fireEvent("saveUse", this.saveADUse());
	},this);
	this.tbar.on("remove",function(){
		this.deleteADUse();

	},this);

	this.callParent(arguments);


},

initFields: function(){

	Ext.define("RoleModel", {
		extend: 'Ext.data.Model',
		fields: ["id","name","description", "CHECKED"]
	});

	this.rolesStore =  Ext.create('Ext.data.Store',{
		model: "RoleModel",
		autoLoad: true,
		proxy: {
			type: 'rest',
			url: Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'analyticalDriverUse'}),
			extraParams: {adid:this.exampleID},
			reader: {
				type:"json",
				root:"ROLES"
			}
		}
	});

	this.usesStore=  Ext.create('Ext.data.Store',{
		model: "Sbi.behavioural.analyticalDriver.AnalyticalDriverUseModel"
			,proxy:{

				type: 'rest',
				url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'analyticalDriverUse'}),
				appendId: false,
				extraParams: {adid:this.exampleID},
				reader: {

					type: "json",
					root: "ADUSE"

				}
			}
	});

	this.rolesStore.load({
		callback: function(){this.usesStore.load();},
		scope:this

	});

	var scope = this;

	this.usesStore.on(
			"load", 

			function(usesStore)
			{ 
				for (var i=0; i<usesStore.getCount();i++){

					var record = usesStore.getAt(i);

					var use = Ext.create("Sbi.behavioural.analyticalDriver.AnalyticalDriverUsePanel", {

						title: record.data.LABEL,
						width : "35%",
						layout: 'hbox'
					});
					use.lovs.bindStore(scope.lovStore);
					use.selections.bindStore(scope.selectionStore);
					use.uselovcombo.bindStore(scope.lovStore);
					use.pickupcombo.bindStore(scope.pickupstore);
					data = scope.rolesStore.data;
					use.useRolesStore.removeAll();
					for(var j=0;j<data.length;j++){
						use.useRolesStore.add(data.items[j].data);
					}
					for(var jj=0;jj<record.data.ROLESLIST.length;jj++){

						for(var z=0; z< use.useRolesStore.getCount();z++){

							if(use.useRolesStore.data.items[z].data.name==record.data.ROLESLIST[jj].name){
								use.useRolesStore.data.items[z].data.CHECKED=true;
							}
						}

					}
					dataConst = scope.constraintStore.data;
					use.useConstStore.removeAll();
					for(var k=0;k<dataConst.length;k++){
						use.useConstStore.add(dataConst.items[k].data);
					}
					for(var kk=0;kk<record.data.CONSTLIST.length;kk++){

						for(var x=0; x< use.useConstStore.getCount();x++){

							if(use.useConstStore.data.items[x].data.VALUE_NM==record.data.CONSTLIST[kk].NAME){
								use.useConstStore.data.items[x].data.CHECKED=true;
							}
						}

					}

					use.adid.setValue(record.data.ID);
					use.useid.setValue(record.data.USEID);
					use.name.setValue(record.data.NAME);
					use.label.setValue(record.data.LABEL);
					use.description.setValue(record.data.DESCRIPTION);
					if (record.data.MANUALINPUT==1){
						use.manualinput.setValue(true);
						use.lov.setValue(false);
						use.expendable.setValue(record.data.EXPENDABLE);
					}
					else {
						use.manualinput.setValue(false);
						use.lov.setValue(true);
						use.lovs.setValue(record.data.LOVID);	
						use.selections.setValue(record.data.SELECTIONTYPE);
					}
					if (record.data.DEFAULTLOVID){
						use.uselov.setValue(true);
						use.uselovcombo.setValue(record.data.DEFAULTLOVID);
					}
					else if (record.data.DEFAULTFORMULA){
						use.pickup.setValue(true);
						use.pickupcombo.setValue(record.data.DEFAULTFORMULA);
					}
					scope.add(use);
				}});

	Ext.define("SelectionModel", {
		extend: 'Ext.data.Model',
		fields: ["VALUE_ID","VALUE_DS","VALUE_NM", "VALUE_CD"]
	});

	this.selectionStore=  Ext.create('Ext.data.Store',{
		model: "SelectionModel",
		proxy: {
			type: 'ajax',
			extraParams : {DOMAIN_TYPE:"SELECTION_TYPE"},
			url: Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'domains/listValueDescriptionByType'}),
			reader: {
				type:"json"
			}

		}
	});

	this.selectionStore.load();

	Ext.define("LovModel", {
		extend: 'Ext.data.Model',
		fields: [{name: 'LOV_ID',type: 'string'},"LOV_NAME", "LOV_DESCRIPTION", "LOV_PROVIDER"]
	});

	this.lovStore = Ext.create('Ext.data.Store',{

		model: "LovModel",
		proxy: {
			type: 'ajax',
			url:  this.services['getLovs'],
			reader: {
				type:"json"
			}
		}
	});

	this.lovStore.load();

	this.pickupstore = Ext.create('Ext.data.Store', {
		fields: ['defaultFormulaValue', 'defaultFormulaDesc'],
		data : [
		        {"defaultFormulaValue":"FIRST", "defaultFormulaDesc":"Main LOV's first item"},
		        {"defaultFormulaValue":"LAST", "defaultFormulaDesc":"Main LOV's last item"}
		        ]
	});



	Ext.define("ConstraintModel", {
		extend: 'Ext.data.Model',
		fields: ["VALUE_ID","VALUE_DS","VALUE_NM", "VALUE_CD", "CHECKED"]
	});

	this.constraintStore =  Ext.create('Ext.data.Store',{
		model: "ConstraintModel",
		proxy: {
			type: 'rest',
			extraParams : {DOMAIN_TYPE:"PRED_CHECK"},
			url: Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'domains/listValueDescriptionByType'}),
			reader: {
				type:"json"
			}

		}
	});

	this.constraintStore.load();

	this.newUse = Ext.create('Sbi.behavioural.analyticalDriver.AnalyticalDriverUsePanel',{

		title: 'New...',
		border: false,
		layout: 'hbox'
	});

	this.newUse.adid.setValue(this.exampleID);
	this.newUse.lovs.bindStore(this.lovStore);
	this.newUse.selections.bindStore(this.selectionStore);
	this.newUse.uselovcombo.bindStore(this.lovStore);
	this.newUse.pickupcombo.bindStore(this.pickupstore);
	this.newUse.constraintsList.reconfigure(this.constraintStore);
	this.newUse.rolesList.reconfigure(this.rolesStore);


},

initServices: function(baseParams){
	this.services["getLovs"]= Sbi.config.serviceRegistry.getRestServiceUrl({
		serviceName: 'LOV'
			, baseParams: baseParams
	});


},

getValues: function(){
	var values = {

			USEID: this.activeTab.useid.getValue(),
			ID: this.activeTab.adid.getValue(),
			NAME: this.activeTab.name.getValue(),
			LABEL: this.activeTab.label.getValue(),
			DESCRIPTION: this.activeTab.description.getValue(),
			MANUALINPUT: this.activeTab.manualinput.getValue(),
			EXPENDABLE: this.activeTab.expendable.getValue(),
			LOV: this.activeTab.lov.getValue(),
			LOVID: this.activeTab.lovs.getValue(),
			NONE: this.activeTab.nonedv.getValue(),
			USELOV: this.activeTab.uselov.getValue(),
			DEFAULTLOVID: this.activeTab.uselovcombo.getValue(),
			PICKUP: this.activeTab.pickup.getValue(),
			SELECTIONTYPE: this.activeTab.selections.getValue(),
			EXPENDABLE: this.activeTab.expendable.getValue(),
			DEFAULTFORMULA: this.activeTab.pickupcombo.getValue(),
			DEFAULTLOVID: this.activeTab.uselovcombo.getValue()

	};

	var rolesArray = this.activeTab.rolesList.getStore().getRange();
	var rolesList = [];

	var rolesCount = 0;
	for (var i = 0, len = rolesArray.length; i < len; i++) {
		var rolesRow = rolesArray[i].getData();
		if(rolesRow.CHECKED){

			rolesList[rolesCount++] = rolesRow;

		}
	};

	var constraintsArray = this.activeTab.constraintsList.getStore().getRange();
	var constraintsList = [];

	var constraintsCount = 0;
	for (var i = 0, len = constraintsArray.length; i < len; i++) {
		var constraintsRow = constraintsArray[i].getData();
		if(constraintsRow.CHECKED){

			constraintsList[constraintsCount++] = constraintsRow;

		}
	};

	values.ROLESLIST = rolesList;
	values.CONSTLIST = constraintsList;

	return values;

},

saveADUse: function(){

	var scope = this;

	var values = this.getValues();

	if(values.DESCRIPTION && values.NAME && values.LABEL && values.ROLESLIST.length !=0){

		var usetosave = Ext.create('Sbi.behavioural.analyticalDriver.AnalyticalDriverUseModel', values);
		usetosave.save({
			success: function(object, response, options){

				if(this.activeTab.title == "New..."){
					this.newUse1 = Ext.create('Sbi.behavioural.analyticalDriver.AnalyticalDriverUsePanel',{

						title: 'New...',
						border: false,
						layout: 'hbox'
					});

					this.newUserolesStore =  Ext.create('Ext.data.Store',{
						model: "RoleModel"
					});

					data = scope.rolesStore.data;
					this.newUserolesStore.removeAll();
					for(var j=0;j<data.length;j++){
						data.items[j].data.CHECKED=false;
						this.newUserolesStore.add(data.items[j].data);
					}

					this.newUseconstStore =  Ext.create('Ext.data.Store',{
						model: "ConstraintModel"
					});

					dataConst = scope.constraintStore.data;
					this.newUseconstStore.removeAll();
					for(var k=0;k<dataConst.length;k++){
						dataConst.items[k].data.CHECKED=false;
						this.newUseconstStore.add(dataConst.items[k].data);

					}

					this.newUse1.adid.setValue(this.exampleID);
					this.newUse1.lovs.bindStore(scope.lovStore);
					this.newUse1.selections.bindStore(scope.selectionStore);
					this.newUse1.uselovcombo.bindStore(scope.lovStore);
					this.newUse1.pickupcombo.bindStore(scope.pickupstore);
					this.newUse1.constraintsList.reconfigure(this.newUseconstStore);
					this.newUse1.rolesList.reconfigure(this.newUserolesStore);

					this.insert(0,this.newUse1);
				}
				this.activeTab.setTitle(values.LABEL);
				this.activeTab.useid.setValue(Ext.decode(response.response.responseText).USEID);

			},
			scope:this
		});
	}
},
deleteADUse: function(){

	var values = this.getValues();
	var usetodelete = Ext.create('Sbi.behavioural.analyticalDriver.AnalyticalDriverUseModel', values);

	if(usetodelete.data.USEID){

		usetodelete.destroy({
			success: function(object, response, options){
				this.remove(this.activeTab);
				this.usesStore.remove(usetodelete);
				this.usesStore.commitChanges();

			},
			scope:this
		});
	}
}
});