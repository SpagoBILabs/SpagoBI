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
				Sbi.exception.ExceptionHandler.showErrorMessage('sbi.community.validation.error','sbi.generic.validationError');
			}
			
		},this)

		this.callParent(arguments);
		this.on("render",function(){this.hide()},this);
    }

	, initFields: function(){
		this.communityId = Ext.create("Ext.form.field.Hidden",{
			name: "communityId"
		});
		this.name = Ext.create("Ext.form.field.Text",{
			name: "name",
			fieldLabel: LN('sbi.community.name'),
			allowBlank: false
		});
		this.description = Ext.create("Ext.form.field.Text",{
			name: "description",
			fieldLabel: LN('sbi.community.descr')
		});

		this.owner = Ext.create("Ext.form.field.Text",{
			name: "owner",
			fieldLabel: LN('sbi.community.owner'),
			allowBlank: false
		});	   
   
	}
	, setFormState: function(values){
		var v = values;

		this.getForm().setValues(v);
	}
	, getValues: function(){
		var values = this.callParent();
		return values;
	}

	
	, validateForm: function(){
		var valid = true;
		
		return valid;
	}
	
});
    