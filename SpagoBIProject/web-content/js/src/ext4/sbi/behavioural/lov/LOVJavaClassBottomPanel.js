Ext.define
(
		"Sbi.behavioural.lov.LOVJavaClassBottomPanel", 
		
		{
			create: function()
			{		
				Sbi.debug("[IN] Creating LOVJavaClassBottomPanel");
				
				this.javaClassName = Ext.create
				(
					"Ext.form.field.Text",
					
					{
						fieldLabel: LN('sbi.behavioural.lov.details.javaClassName'), 
						name: 'JAVA_CLASS_NAME',
						id: "JAVA_CLASS_NAME",
						width: 500,
						padding: '10 0 10 0',
						allowBlank: true					
						//name: "LOV_LABEL"		    		
					}
				);	
				
				Sbi.debug("[OUT] Creating LOVJavaClassBottomPanel");
			}			
		}
);