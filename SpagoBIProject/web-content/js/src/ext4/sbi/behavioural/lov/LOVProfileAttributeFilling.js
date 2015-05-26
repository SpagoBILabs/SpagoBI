Ext.define
(
		"Sbi.behavioural.lov.LOVProfileAttributeFilling", 
		
		{			
			create: function()
			{								
				this.lovFixedListForm = Ext.create
	    		(
	    			"Ext.form.Panel",
	    			
	    			{
	    				title: 'Type new or choose previously defined values',
	    				width: "100%"
	    			}
	    		);
			}						
		}
);