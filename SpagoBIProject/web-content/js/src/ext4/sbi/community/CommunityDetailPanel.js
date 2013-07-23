Ext.define('Sbi.community.CommunityDetailPanel', {
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
/*	fields: [
	         "communityId",
	         "name",
	         "description",
	         "owner"]*/
	, constructor: function(config) {
		this.initConfig(config);
		this.initFields();
		this.items=[this.communityId, this.name, this.description , this.owner]
		
		this.addEvents('save');
		this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[{name:'->'},{name:'save'}]},this);
		this.tbar.on("save",function(){
			if(this.validateForm()){
				this.fireEvent("save", this.getValues());
			}else{
				Sbi.exception.ExceptionHandler.showErrorMessage('sbi.datasource.validation.error','sbi.generic.validationError');
			}
			
		},this)

		this.callParent(arguments);
		this.on("render",function(){this.hide()},this);
    }

	, initFields: function(){
		this.dataSourceId = Ext.create("Ext.form.field.Hidden",{
			name: "communityId"
		});
		this.dataSourceLabel = Ext.create("Ext.form.field.Text",{
			name: "name",
			fieldLabel: LN('sbi.datasource.label'),
			allowBlank: false
		});
		this.dataSourceDescription = Ext.create("Ext.form.field.Text",{
			name: "description",
			fieldLabel: LN('sbi.datasource.description')
		});

		Ext.create("Ext.form.field.Text",{
			name: "owner",
			fieldLabel: LN('sbi.datasource.dialect'),
			allowBlank: false
		});	   
    	

		
	}
//
//	
//	, validateForm: function(){
//		var valid = true;
//		var v = this.getValues();
//
//		valid = valid && (v.DIALECT_ID!=null && v.DIALECT_ID!=undefined &&  v.DIALECT_ID!="");
//		valid = valid && (v.DATASOURCE_LABEL!=null && v.DATASOURCE_LABEL!=undefined &&  v.DATASOURCE_LABEL!="");
//		if(v.TYPE == 'jndi'){
//			valid = valid && (v.JNDI_URL!=null && v.JNDI_URL!=undefined &&  v.JNDI_URL!="");
//		}else{
//			valid = valid && (v.CONNECTION_URL!=null && v.CONNECTION_URL!=undefined &&  v.CONNECTION_URL!="");
//			valid = valid && (v.USER!=null && v.USER!=undefined &&  v.USER!="");
//			valid = valid && (v.PASSWORD!=null && v.PASSWORD!=undefined &&  v.PASSWORD!="");
//			valid = valid && (v.DRIVER!=null && v.DRIVER!=undefined &&  v.DRIVER!="");
//		}
//		return valid;
//	}
	
});
    