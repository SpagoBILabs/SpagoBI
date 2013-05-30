<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>


<script type="text/javascript">

   var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
    	   				  request.getContextPath().substring(1):
    	   				  request.getContextPath()%>'
    	    
    };

    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
    });
	
    Ext.define('Image', {
	    extend: 'Ext.data.Model',
	    fields: [
	        { name:'src', type:'string' },
	        { name:'caption', type:'string' }
	    ]
	});

	var store = Ext.create('Ext.data.Store', {
	    id:'imagesStore',
	    model: 'Image',
	    data: [
	        { src:'http://www.sencha.com/img/20110215-feat-drawing.png', caption:'Drawing & Charts' },
	        { src:'http://www.sencha.com/img/20110215-feat-data.png', caption:'Advanced Data' },
	        { src:'http://www.sencha.com/img/20110215-feat-html5.png', caption:'Overhauled Theme' },
	        { src:'http://www.sencha.com/img/20110215-feat-perf.png', caption:'Performance Tuned' }
	    ]
	});
	
	var imageTpl = new Ext.XTemplate(
		    '<tpl for=".">',
		        '<div style="margin-bottom: 10px;" class="thumb-wrap">',
		          '<img src="{src}" />',
		          '<br/><span>{caption}</span>',
		        '</div>',
		    '</tpl>'
		);
	
	var config = {};
	config.store = store;
	config.tpl = imageTpl;

    Ext.onReady(function(){
		//var datasetList = Ext.create('Sbi.widgets.dataview.DataViewPanel',config); //by alias
		var datasetList = Ext.create('Sbi.tools.dataset.SelfServiceDatasetsBrowser',config); //by alias
		var datasetListViewport = Ext.create('Ext.container.Viewport', {
			layout:'fit',
	     	items: [datasetList]
	    });
    });
	
</script>
 

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>