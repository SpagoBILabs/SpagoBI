Ext.define('Sbi.tools.datasource.DataSourceDetailPanel', {
    extend: 'Ext.form.Panel'

    ,config: {
    	//frame: true,
    	bodyPadding: '5 5 0',
    	defaults: {
            width: 400
        },        
        fieldDefaults: {
            labelAlign: 'right',
            msgTarget: 'side'
        },
        border: false,
        services:[]
    }

	, constructor: function(config) {
		this.initConfig(config);
		this.initFields();
		this.items=[this.dataSourceId, this.dataSourceDialectId, this.dataSourceLabel , this.dataSourceDescription, this.dataSourceDialect, this.dataSourceMultischema , this.dataSourceMultischemaAttribute, this.dataSourceTypeJdbc, this.dataSourceTypeJndi,this.dataSourceJndiName, this.dataSourceJdbcUrl, this.dataSourceJdbcUser, this.dataSourceJdbcPassword ,this.dataSourceDriver]
		
		this.addEvents('save');
		this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[{name:'->'},{name:'test'},{name:'save'}]},this);
		this.tbar.on("save",function(){
			if(this.validateForm()){
				this.fireEvent("save", this.getValues());
			}else{
				Sbi.exception.ExceptionHandler.showErrorMessage('sbi.datasource.validation.error','sbi.generic.validationError');
			}
			
		},this)
		this.tbar.on("test",function(){
			this.fireEvent("test", this.getValues());
		},this)
		this.callParent(arguments);
		this.on("render",function(){this.hide()},this);
    }

	, initFields: function(){
		this.dataSourceId = Ext.create("Ext.form.field.Hidden",{
			name: "DATASOURCE_ID"
		});
		this.dataSourceLabel = Ext.create("Ext.form.field.Text",{
			name: "DATASOURCE_LABEL",
			fieldLabel: LN('sbi.datasource.label'),
			allowBlank: false
		});
		this.dataSourceDescription = Ext.create("Ext.form.field.Text",{
			name: "DESCRIPTION",
			fieldLabel: LN('sbi.datasource.description')
		});

		Ext.create("Ext.form.field.Text",{
			name: "DIALECT_NAME",
			fieldLabel: LN('sbi.datasource.dialect'),
			allowBlank: false
		});	   
    	
    	Ext.define("DialectModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var dialectStore=  Ext.create('Ext.data.Store',{
    		model: "DialectModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"DIALECT_HIB"},
    			url:  this.services['getDialects'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	dialectStore.load();
		this.dataSourceDialect = new Ext.create('Ext.form.ComboBox', {
			fieldLabel: LN('sbi.datasource.dialect'),
	        store: dialectStore,
	        name: "DIALECT_ID",
	        displayField:'VALUE_DS',
	        valueField:'VALUE_ID',
	        allowBlank: false
	    });
    	
		this.dataSourceMultischema = Ext.create("Ext.form.Checkbox",{
	        fieldLabel: LN('sbi.datasource.multischema'),
	        name: "MULTISCHEMA",
	        value: false
		});
		
		this.dataSourceMultischemaAttribute = Ext.create("Ext.form.field.Text",{
			name: "SCHEMA",
			fieldLabel: LN('sbi.datasource.multischema.attribute'),
			allowBlank: true,
			hidden: true
		});
		
		this.dataSourceMultischema.on("change", function(field, newValue, oldValue, eOpts){
			if(newValue){
				this.dataSourceMultischemaAttribute.show();
			}else{
				this.dataSourceMultischemaAttribute.hide();
			}
		},this);
		
		this.dataSourceTypeJdbc = Ext.create("Ext.form.field.Radio",{
			fieldLabel: LN('sbi.datasource.type'),
			boxLabel: LN('sbi.datasource.type.jdbc'), 
			name: 'TYPE' , 
			inputValue:'jdbc'
		})
		
		this.dataSourceTypeJndi = Ext.create("Ext.form.field.Radio",{
            hideEmptyLabel: false,
			boxLabel: LN('sbi.datasource.type.jndi'), 
			checked : true,
			name: 'TYPE' , 
			inputValue:'jndi'
		});

		this.dataSourceJndiName= Ext.create("Ext.form.field.Text",{
			name: "JNDI_URL",
			fieldLabel: LN('sbi.datasource.type.jndi.name'),
			allowBlank: false
		});	 
		
		this.dataSourceJdbcUser = Ext.create("Ext.form.field.Text",{
			name: "USER",
			fieldLabel: LN('sbi.datasource.type.jdbc.user'),
			allowBlank: false,
			hidden: true
		});
		
		this.dataSourceJdbcUrl = Ext.create("Ext.form.field.Text",{
			name: "CONNECTION_URL",
			fieldLabel: LN('sbi.datasource.type.jdbc.url'),
			allowBlank: false,
			hidden: true
		});
		
		this.dataSourceJdbcPassword = Ext.create("Ext.form.field.Text",{
			name: "PASSWORD",
			inputType: 'password',
			fieldLabel: LN('sbi.datasource.type.jdbc.password'),
			allowBlank: false,
			hidden: true
			
		});
		
		this.dataSourceDriver = Ext.create("Ext.form.field.Text",{
			name: "DRIVER",
			fieldLabel: LN('sbi.datasource.driver'),
			allowBlank: false,
			hidden: true
		});	

		this.dataSourceTypeJdbc.on("change", function(field, newValue, oldValue, eOpts){
			if(newValue){
				this.dataSourceJdbcPassword.show();
				this.dataSourceJdbcUrl.show();
				this.dataSourceJdbcUser.show();
				this.dataSourceDriver.show();
			}else{
				this.dataSourceJdbcPassword.hide();
				this.dataSourceJdbcUrl.hide();
				this.dataSourceJdbcUser.hide();
				this.dataSourceDriver.hide();
			}
		},this);
		
		this.dataSourceTypeJndi.on("change", function(field, newValue, oldValue, eOpts){
			if(newValue){
				this.dataSourceJndiName.show();
			}else{
				this.dataSourceJndiName.hide();

			}
		},this);
	}
	

	, setFormState: function(values){
		var v = values;
		if(v.JNDI_URL){
			v.TYPE='jndi';
		}else{
			v.TYPE='jdbc';
		}
		this.getForm().setValues(v);
	}
	
	, validateForm: function(){
		var valid = true;
		var v = this.getValues();

		valid = valid && (v.DIALECT_ID!=null && v.DIALECT_ID!=undefined &&  v.DIALECT_ID!="");
		valid = valid && (v.DATASOURCE_LABEL!=null && v.DATASOURCE_LABEL!=undefined &&  v.DATASOURCE_LABEL!="");
		if(v.TYPE == 'jndi'){
			valid = valid && (v.JNDI_URL!=null && v.JNDI_URL!=undefined &&  v.JNDI_URL!="");
		}else{
			valid = valid && (v.CONNECTION_URL!=null && v.CONNECTION_URL!=undefined &&  v.CONNECTION_URL!="");
			valid = valid && (v.USER!=null && v.USER!=undefined &&  v.USER!="");
			valid = valid && (v.PASSWORD!=null && v.PASSWORD!=undefined &&  v.PASSWORD!="");
			valid = valid && (v.DRIVER!=null && v.DRIVER!=undefined &&  v.DRIVER!="");
		}
		return valid;
		
	}
});
    