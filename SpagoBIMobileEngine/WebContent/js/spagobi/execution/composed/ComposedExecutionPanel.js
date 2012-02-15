app.views.ComposedExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function (options)	{

			console.log('init composed execution');
		    
			app.views.ComposedExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setComposedWidget: function(resp){
			var title = resp.title;
			
			var documentsList = resp.documents.docs;
			var documentWidth = resp.documents.totWidth;
			var documentHeight = resp.documents.totHeight;
			
			var items = new Array();
			
			if(documentsList!=undefined && documentsList!=null){
				for(var i=0; i<documentsList.size(); i++){
					var subDocumentPanel = this.buildPanel(documentsList[i]);
					var executionInstance ={
							IS_FROM_COMPOSED : true
					};
					app.controllers.ComposedExecutionController.executeSubDocument(executionInstance, subDocumentPanel);
					items.push(subDocumentPanel);
				}
			}
			
			var composedDocumentContainerConfig =	new Ext.chart.Panel({
				title: title,
	            bodyMargin: '50px 50px 100px 50px',
	            items: items});

			if(documentWidth && documentHeight){
				composedDocumentContainerConfig.height =documentHeight;
				composedDocumentContainerConfig.width =documentWidth;
			}
			
			var composedDocumentContainer =	new Ext.chart.Panel(composedDocumentContainerConfig);
			app.views.composed =  composedDocumentContainer;
			this.add(app.views.composed);

		},
		
		buildPanel: function(config){
			config = Ext.apply(config,{style: 'float: left', html: '&nbsp;'});
			var panel = new Ext.Panel(config);
			return panel;
		}

		
});