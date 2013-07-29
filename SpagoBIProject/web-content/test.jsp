
<head>
</head>
<body>





<script type="text/javascript" src='js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>
    
<script type="text/javascript" src='js/src/ext/sbi/service/ServiceRegistry.js'/></script>

<script type="text/javascript">


    // general SpagoBI configuration
    Ext.ns("Sbi.config");
    var url = {
    	host: 'localhost'
    	, port: '8080'
    	, contextPath: 'SpagoBI'
    };

    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
    });
	

</script>

<script type="text/javascript" src='js/src/ext4/sbi/widgets/grid/GrouppedGrid.js'/></script>
<script type="text/javascript" src='js/src/ext4/sbi/tools/measure/MeasuresCatalogue.js'/></script>
<script type="text/javascript" src='js/src/ext4/sbi/tools/measure/MeasureModel.js'/></script>
    

    
<!-- Include Ext stylesheets here: -->
<link id="extall"     rel="styleSheet" href ="js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />

<script type="text/javascript" >









Ext.onReady(function(){



    
	var datasourceDetail = Ext.create('Sbi.tools.measure.MeasuresCatalogue',{}); //by alias
	
	
	var datasourceDetailViewport = Ext.create('Ext.container.Viewport', {
		layout:'fit',
     	items: [datasourceDetail]
    });
});
</script>

</body>