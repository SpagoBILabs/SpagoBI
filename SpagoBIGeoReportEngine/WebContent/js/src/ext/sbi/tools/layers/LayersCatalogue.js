/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
Ext.ns("Sbi.geo.tools");

Sbi.geo.tools.LayersCatalogue = function(config) {
	var defaultSettings = {
			layout: 'fit',
			contextPath: "SpagoBI",
			columnsRef: ['label', 'descr', 'type','baseLayer']

	};
	
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.tools && Sbi.settings.georeport.tools.layerscatalogue) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.tools.layerscatalogue);
	}
	
	Ext.apply(this,defaultSettings);
	
	var sm = new Ext.grid.CheckboxSelectionModel({SingleSelect:false});
	var cm = this.buildColumns(sm);
	
	var c = ({
		store: this.buildStore(),
		cm: cm,
		sm: sm,
		viewConfig: {
			 forceFit: true
		}
	});
	
	Sbi.geo.tools.LayersCatalogue.superclass.constructor.call(this,c);

};

Ext.extend(Sbi.geo.tools.LayersCatalogue, Ext.grid.GridPanel, {
	buildColumns: function(sm){
		var thisPanel = this;
		
		var columnsDesc = [];
		columnsDesc.push(sm);
		
		//Builds the columns of the grid
		for(var i=0; i<this.columnsRef.length; i++){
			var column = this.columnsRef[i];
			var object = {
					header: OpenLayers.Lang.translate('sbi.tools.catalogue.layers.column.header.'+column), 
					sortable: true,
					dataIndex: column
				};

			columnsDesc.push(object);
		}
		
		return new Ext.grid.ColumnModel(columnsDesc);
	}
	,buildStore: function(){
		return new Ext.data.Store({
			
			proxy:new Ext.data.HttpProxy({
				type: 'json',
				url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'layers' ,baseUrl:{contextPath: this.contextPath}
				})
			}),
			reader: new  Ext.data.JsonReader({
				fields: [
				         "id",
				         "type",
				         "name",
				         "label",
				         "type",
				         "descr",
				         "baseLayer"
				         ],
				         root: 'root' 
			}),

			autoLoad:true,
			
			sortInfo:{field: 'type', direction: "ASC"}


		});
	}
	
	,getSelectedLayers: function(){
		var layersLabels = new Array();
		var selected = this.getSelectionModel().getSelections();
		if(selected!=null && selected!=undefined && selected.length>0){
			for(var i=0; i<selected.length; i++){
				layersLabels.push(selected[i].data.label);
			}
		}
		return layersLabels;
	}
});