/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.ns("Sbi.geo.tools");

Sbi.geo.tools.MeasureCatalogue = function(config) {

	var defaultSettings = {
			layout: 'fit',
			contextPath: "SpagoBI"
	};

	if(Sbi.settings && Sbi.settings.geo && Sbi.settings.geo.tools && Sbi.settings.geo.tools.measurecatalogue) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.geo.tools.measurecatalogue);
	}
	
	Ext.apply(this,defaultSettings);
	
	var expander = this.getExpander();
	var sm = new Ext.grid.CheckboxSelectionModel({SingleSelect:false, hideable:true});
	var cm = this.getColumns(sm, expander);
	
	var c = ({
		store: this.getStore(),
		view: new Ext.grid.GroupingView({
			forceFit:true,
			groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
		}),
		
		tbar: this.getToolbar(this),
		cm: cm,
		sm: new Ext.grid.CheckboxSelectionModel({SingleSelect:false}),
		plugins: expander
	});



	Sbi.geo.tools.MeasureCatalogue.superclass.constructor.call(this,c);
};


Ext.extend(Sbi.geo.tools.MeasureCatalogue, Ext.grid.GridPanel, {

	getColumns: function(sm, expander){
		return new Ext.grid.ColumnModel([
		                                 expander,
		                    			{id: 'alias',
		                    				header: 'Alias',
		                    				sortable: true,
		                    				dataIndex: 'alias',
		                    				width: 20
		                    			},
		                    			{
		                    				header: 'DS Name',
		                    				sortable: true,
		                    				dataIndex: 'dsName',
		                    				width: 20
		                    			},
		                    			{
		                    				header: 'DS Label',
		                    				sortable: true,
		                    				dataIndex: 'dsLabel',
		                    				width: 20
		                    			},
		                    			{
		                    				header: 'DS Category',
		                    				sortable: true,
		                    				dataIndex: 'dsCategory',
		                    				width: 20
		                    			},
		                    			{
		                    				header: 'DS Type',
		                    				sortable: true,
		                    				dataIndex: 'dsType',
		                    				width: 20
		                    			},
		                    			sm
		                    		]);
	},

	getStore: function(){
		return new Ext.data.GroupingStore({

			proxy:new Ext.data.HttpProxy({
				type: 'json',
				url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'measures', baseUrl:{contextPath: this.contextPath}})
			}),
			reader: new  Ext.data.JsonReader({
				fields: [
				         "id",
				         "alias",
				         "columnName",
				         "classType",
				         "dsType",
				         "dsId",
				         "dsCategory",
				         "dsName",
				         "dsLabel"
				         ],
				         groupField:'alias',
				         root: 'measures'
			}),

			autoLoad:true,
			
			sortInfo:{field: 'alias', direction: "ASC"}

		});
	},
	
	getToolbar: function(grid){
		
		var thisPanel = this;
		
		var joinMeasuresButton = new Ext.Toolbar.Button({
			text    : OpenLayers.Lang.translate('sbi.tools.catalogue.measures.join.btn'),
			tooltip : OpenLayers.Lang.translate('sbi.tools.catalogue.measures.join.tooltip'),
			handler : function() {
				thisPanel.executeJoin();
			}
		});

		var tb = new Ext.Toolbar([joinMeasuresButton]);

		return tb;
		
	},
	
	executeJoin: function(){
		var measuresIds = new Array();
		var selected = this.getSelectionModel().getSelections();
		if(selected!=null && selected!=undefined && selected.length>0){
			for(var i=0; i<selected.length; i++){
				measuresIds.push(selected[i].data.id);
			}
			if(measuresIds.length<2){
				alert("daasdas");
			}

			Ext.Ajax.request({
				url: Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'measures/join', baseUrl:{contextPath: this.contextPath}}),
				params: {ids: measuresIds},
				success : function(response, options) {
					if(response !== undefined && response.responseText !== undefined && response.statusText=="OK") {
						if(response.responseText!=null && response.responseText!=undefined){
							if(response.responseText.indexOf("error.mesage.description")>=0){
								Sbi.exception.ExceptionHandler.handleFailure(response);
							}else{
								alert("Join ok.. look at the responce");
							}
						}
					} else {
						Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
					}
				},
				scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure,  
				scope: this
			});
		}
	},
	
	getExpander: function(){
		
	    var expander = new Ext.grid.RowExpander({
	        tpl : new Ext.Template(
	        		'<div class="htmltable">',
	        		'<div class="measure-detail-container"><div class="measure-detail-title"><h2><div class="group-header" style="background-image: none!important">'+OpenLayers.Lang.translate('sbi.tools.catalogue.measures.measure.properties')+'</div></h2></div>',
	        		'<table>',
	        		'		<tr style="height: 90px">',
	        		'			<td class="measure-detail-measure">',
	        		'			</td>',
	        		'			<td><table>',
	        		'					<tr><td style="width: 100px"><p><b>Name:</b></td><td><p>{alias}</p></td></tr>',
	        		'					<tr><td><p><b>Type:</b></td><td><p>{classType}</p></td>	</tr>',
	        		'					<tr><td><p><b>Column Name:</b></td><td><p>{columnName}</p></td>	</tr>',
	        		'			</table></td>',			
	        		'		</tr>',
	        		'</table></div>',
	        		'<div class="dataset-detail-container"><div class="measure-detail-title"><h2><div class="group-header" style="background-image: none!important">'+OpenLayers.Lang.translate('sbi.tools.catalogue.measures.dataset.properties')+'</div></h2></div>',
	        		'<table>',
	        		'		<tr style="height: 100px">',
	        		'			<td class="measure-detail-dataset">',
	        		'			</td>',
	        		'			<td><table>',
	        		'					<tr><td style="width: 100px"><p><b>Label:</b></td><td><p>{dsLabel}</p></td></tr>',
	        		'					<tr><td><p><b>Name:</b></td><td><p>{dsName}</p></td></tr>',
	        		'					<tr><td><p><b>Category:</b></td><td><p>{dsCategory}</p></td></tr>',
	        		'					<tr><td><p><b>Type:</b></td><td><p>{dsType}</p></td></tr>',
	        		'			</table></td>',
	        		'		</tr>',
	        		'</table></div>',
	        		'</div>'
	        )
	    });
		
		
	    return expander;
	}

});