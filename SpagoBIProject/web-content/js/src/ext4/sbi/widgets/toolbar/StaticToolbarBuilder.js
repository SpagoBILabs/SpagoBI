Ext.define('Sbi.widget.toolbar.StaticToolbarBuilder', {
	statics: {
		
		/**
		 * Adds to the grid the button columns according to the configuration conf. This configuration object is a map 
		 * like this one: {"selectbutton": true, "deletebutton":true, "copybutton":true}
		 * @param {Object} configuration object
		 * @param {Array} list of columns of the grid
		 * @param {grid} the grid that fire the events handled from the buttons
		 */
		buildToolbar: function(conf, parentPanel){
			
			var toolbarConf = Ext.apply({},conf);
			toolbarConf.items = [ { xtype: 'tbspacer' }]
			var toolbar = Ext.create('Ext.toolbar.Toolbar', toolbarConf);
			
			for(var i=0; i<conf.items.length; i++){
				toolbar.add(Sbi.widget.toolbar.StaticToolbarBuilder.buildButtons(toolbar,conf.items[i]));
			}
			return toolbar;

		}

		,buildButtons: function(toolbar, item){
			switch(item.name)
			{
			case 'new':
				return Sbi.widget.toolbar.StaticToolbarBuilder.addNewItemToolbarButton(toolbar, item);
			case 'clone':
				return Sbi.widget.toolbar.StaticToolbarBuilder.addCloneItemToolbarButton(toolbar, item);
			case 'save':
				return Sbi.widget.toolbar.StaticToolbarBuilder.addSaveItemToolbarButton(toolbar, item);
			case 'test':
				return Sbi.widget.toolbar.StaticToolbarBuilder.addTestItemToolbarButton(toolbar, item);
			case '->':
				return '->';
			default:
				return item;
			}
		}

		,addNewItemToolbarButton: function(toolbar, conf){
			var toolbarconf = {
					//text: LN('sbi.generic.add'),
			         iconCls: 'icon-add',
			        handler: function(button, event) {
			        	toolbar.fireEvent("addnew");
					},
			        scope: this
			    };
			return Ext.apply(toolbarconf,conf||{});
		}
		
		,addCloneItemToolbarButton: function(toolbar, conf){
			var toolbarconf = {
					//text: LN('sbi.generic.clone'),
			         iconCls: 'icon-clone',
			        handler: function(button, event) {
			        	toolbar.fireEvent("clone");
					},
			        scope: this
			    };
			return Ext.apply(toolbarconf,conf||{});
		}
		
		,addSaveItemToolbarButton: function(toolbar, conf){
			var toolbarconf = {
					//text: LN('sbi.generic.update'),
			         iconCls: 'icon-save',
			        handler: function(button, event) {
			        	toolbar.fireEvent("save");
					},
			        scope: this
			    };
			return Ext.apply(toolbarconf,conf||{});
		}
		
		,addTestItemToolbarButton: function(toolbar, conf){
			var toolbarconf = {
					//text: LN('sbi.generic.update'),
			         iconCls: 'icon-test',
			        handler: function(button, event) {
			        	toolbar.fireEvent("test");
					},
			        scope: this
			    };
			return Ext.apply(toolbarconf,conf||{});
		}
		
	}
});