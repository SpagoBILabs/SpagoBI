app.views.DocumentPreview = Ext.extend(Ext.Panel,

		{
	    dockedItems: [],
	    flex:2,

	    previewItem: null,
	    listeners: {
	
		},
		initComponent: function ()	{
			this.title = 'Document preview';
			this.items = [];
			console.log('init document preview');
			
			app.views.DocumentPreview.superclass.initComponent.apply(this, arguments);
			
		}
		, showPreview: function (tpl){
			this.removeAll();
			this.update('');
		    this.previewItem = new Ext.DataView({
			    frame:true
			    , overItemCls: 'over'
			    , itemSelector: 'dd'
			    , groups: null
			    , lookup: null
			    , viewState: null
			    , ready: false				    
			    , tpl : tpl	
			    , store: null
			    , listeners:{
		    		itemtap: function( dataView , index, item, e ){
		    			alert('aaaaaaaaaaaaaaaaaHHH');
		    		}
		    	}
		    });
		    this.add(this.previewItem);
		    this.update(tpl);
		    console.log('preview items: '+this.items.length);
		}

	});