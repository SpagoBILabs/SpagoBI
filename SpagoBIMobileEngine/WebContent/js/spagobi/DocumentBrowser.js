app.views.DocumentBrowser = Ext.extend (Ext.NestedList,
		{	
	    scroll: 'vertical',
	    dock : 'left',
	    layout:'card',
	   
		cardSwitchAnimation: 'slide',
	    backText : '&lt;',
	    store: null,
	    data: null,
	    flex:1,
	    displayField: 'name',
	    
		initComponent: function(){
			
			this.data = {'samples':[{"devRoles":["it.eng.spagobi.commons.bo.Role@7cc9c9","it.eng.spagobi.commons.bo.Role@a3ce3f"],
					"biObjects":[],
					"code":"mobiledemo",
					"codType":"LOW_FUNCT",
					"id":70,
					"testRoles":["it.eng.spagobi.commons.bo.Role@39b99d","it.eng.spagobi.commons.bo.Role@1c8f59c"],
					"parentId":1,
					"prog":1,
					"description":"mobiledemo",
					"name":"mobiledemo",
					"path":"/Functionalities/mobiledemo",
					"execRoles":["it.eng.spagobi.commons.bo.Role@74f160","it.eng.spagobi.commons.bo.Role@1930c3a"],
					"actions":[]}]};
			
	
			this.store = new Ext.data.TreeStore({
			    model: 'folder',
			    root: this.data,
			    proxy: {
			        type: 'ajax',
			        reader: {
			            type: 'tree',
			            root: 'samples'
			        }
			    }
			});

			this.store.sync();

			
			app.views.DocumentBrowser.superclass.initComponent.apply(this, arguments);
		
		
		},
		populateList: function()	{
			
/*			var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
			this.services = new Array();
			this.services['loadFolderContentService'] = Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: 'GET_FOLDER_CONTENT_ACTION'
				, baseParams: params
			});
		    this.store = new Ext.data.TreeStore({
		    	model: 'folderContent',
				proxy: {
		            type: 'ajax',
		            url: this.services['loadFolderContentService'],
		            reader: {
		                type: 'tree',
		                root: 'samples'
		            }
		        }
				 , root: 'folderContent'
				 , fields: ['title', 'icon', 'samples']
		    });
		    
		    this.store.load();
		    */
		    
		    
/*			this.store = new Ext.data.JsonStore({
				proxy: {
		            type: 'ajax',
		            url: this.services['loadFolderContentService'],
		            reader: {
		                type: 'tree',
		                root: 'folderContent'
		            }
		        }
				 , root: 'folderContent'
				 , fields: ['title', 'icon', 'samples']
			});	*/ 

/*		    Ext.Ajax.request({
                url: this.services['loadFolderContentService'],
                scope: this,
                method: 'post',
                success: function(response, opts) {
              	  if(response.responseText.indexOf('<') == -1){
                  	  var content = Ext.util.JSON.decode( response.responseText );	              		      			 
                      alert(content);
              	  }else{
              		  alert('cannot !');
                  	  return;
              	  }
                }
		    });    */

			
		}
});