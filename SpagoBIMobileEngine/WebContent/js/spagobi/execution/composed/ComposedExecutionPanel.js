app.views.ComposedExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	     fullscreen: true
		, initComponent: function (options)	{

			console.log('init composed execution');
		    
			app.views.ComposedExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setComposedWidget: function(resp){
			var title = resp.title.value;
			
			var documentsList = resp.documents.docs;
			var documentWidth = resp.documents.totWidth;
			var documentHeight = resp.documents.totHeight;
			
			var items = new Array();
			
			if(documentsList!=undefined && documentsList!=null){
				for(var i=0; i<documentsList.length; i++){
					var subDocumentPanel = this.buildPanel(documentsList[i]);
					var executionInstance = Ext.apply({}, resp.executionInstance);
					Ext.apply(executionInstance, documentsList[i]);
					executionInstance.IS_FROM_COMPOSED = true;
					app.controllers.composedExecutionController.executeSubDocument(executionInstance, subDocumentPanel);
					items.push(subDocumentPanel);
				}
			}

			var composedDocumentContainerConfig = {
				fullscreen: true,
	            bodyMargin: '20px 50px 100px 50px',
	            items: items
	        };
//
//			if(documentWidth && documentHeight){
//				composedDocumentContainerConfig.height =documentHeight;
//				composedDocumentContainerConfig.width =documentWidth;
//			}
			
			var composedDocumentContainer =	new Ext.Panel(composedDocumentContainerConfig);
			app.views.composed =  composedDocumentContainer;
			this.add(app.views.composed);

		},
		
		buildPanel: function(config){

			var panel;
			config = Ext.apply(config,{style: 'float: left;', bodyMargin:10});
			
			if(config.type == 'chart'){
				panel = new app.views.ChartExecutionPanel(config);
			}else{
				panel = new app.views.TableExecutionPanel(config);
			}
		
			return panel;
		}
		


		
});