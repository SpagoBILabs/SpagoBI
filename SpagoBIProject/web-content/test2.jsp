
<head>
</head>
<body>





<script type="text/javascript" src='js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>
    
<script type="text/javascript" src='js/src/ext/sbi/service/ServiceRegistry.js'/></script>
<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/sbi_default/css/analiticalmodel/browser/groupview.css">
<link rel="stylesheet" type="text/css" href="/SpagoBI/themes/sbi_default/css/tools/tools.css">

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


<div class="group-view"><h2><div class="group-header" style="background-image: none!important">Measure Properties</div></h2></div>
<table>
		<tr style="height: 90px">
			<td class="measure-detail-measure">
			</td>
			<td><table>
					<tr><td style="width: 150px"><p><b>Name:</b></td><td><p>{alias}</p></td></tr>
					<tr><td><p><b>Type:</b></td><td><p>{classType}</p></td>	</tr>
					<tr><td><p><b>Column Name:</b></td><td><p>{columnName}</p></td>	</tr>
			</table></td>			
		</tr>
</table>


<div class="group-view"><h2><div class="group-header" style="background-image: none!important">DataSet properties</div></h2></div>
<table>
		<tr>
			<td class="measure-detail-dataset">
			</td>
			<td><table>
					<tr><td style="width: 150px"><p><b>Label:</b></td><td><p>{dsLabel}</p></td></tr>
					<tr><td><p><b>Name:</b></td><td><p>{dsName}</p></td></tr>
					<tr><td><p><b>Category:</b></td><td><p>{dsCategory}</p></td></tr>
					<tr><td><p><b>Type:</b></td><td><p>{dsType}</p></td></tr>
			</table></td>
		</tr>
</table>




</body>