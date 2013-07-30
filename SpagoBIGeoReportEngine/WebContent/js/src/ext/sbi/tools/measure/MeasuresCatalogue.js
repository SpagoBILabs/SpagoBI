/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
Ext.ns("Sbi.geo.tools");

Sbi.geo.tools.MeasureCatalogue = function(config) {

	var defaultSettings = {
			layout: 'fit',
			contextPath: "SpagoBI",
			columnsRef: ['dsName', 'dsLabel', 'dsCategory', 'dsType'],
			measuresProperties: [{header:'Alias', dataIndex:'alias'},{header:'Type', dataIndex:'classType'},{header:'Column', dataIndex:'columnName'}],
			datasetsProperties: [{header:'Name', dataIndex:'dsName'},{header:'Label', dataIndex:'dsLabel'},{header:'Category', dataIndex:'dsCategory'},{header:'Type', dataIndex:'dsType'}]
	};

	
	
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.tools && Sbi.settings.georeport.tools.measurecatalogue) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.tools.measurecatalogue);
	}
	
	Ext.apply(this,defaultSettings);
	
	var tb =  this.getToolbar(this);
	var expander = this.getExpander();
	var sm = new Ext.grid.CheckboxSelectionModel({SingleSelect:false, hideable:true});
	var cm = this.getColumns(sm, expander);
	
	var c = ({
		store: this.buildStore(),
		view: new Ext.grid.GroupingView({
			forceFit:true,
			groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
		}),
		
		tbar: tb,
		cm: cm,
		sm: new Ext.grid.CheckboxSelectionModel({SingleSelect:false}),
		plugins: expander
	});



	Sbi.geo.tools.MeasureCatalogue.superclass.constructor.call(this,c);
};


Ext.extend(Sbi.geo.tools.MeasureCatalogue, Ext.grid.GridPanel, {

	getColumns: function(sm, expander){
		var thisPanel = this;
		
		var highlightSearchString = function (value, a, b) {
			var searchString = thisPanel.search.getValue().toUpperCase();
			if (value != undefined && value != null && searchString != '') {
				return thisPanel.highlightSearchStringInternal(value, 0, searchString, thisPanel);
			}
			return value;
		};
		
		var columnsDesc = [expander];
		
		for(var i=0; i<this.columnsRef.length; i++){
			var column = this.columnsRef[i];
			columnsDesc.push({
				header: OpenLayers.Lang.translate('sbi.tools.catalogue.measures.column.header.'+column),
				sortable: true,
				dataIndex: column,
				renderer: highlightSearchString
			});
		}
		columnsDesc.push(sm);
		
		return new Ext.grid.ColumnModel(columnsDesc);
	},

	buildStore: function(){
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

		this.search = new Ext.form.TriggerField({
			enableKeyEvents: true,
			cls: ' x-form-text-search',
			triggerClass:'x-form-clear-trigger',
	    	onTriggerClick: function(e) {
	    		if(this.el.dom.className.indexOf("x-form-text-search")<0){
            		this.el.dom.className+=" x-form-text-search";
            	}
	    		this.setValue("");
	    		thisPanel.filter("");
			},
			listeners:{
				keyup:function(textField, event){
					thisPanel.filter(textField.getValue());
	            	if(textField.getValue()==""){
	            		textField.el.dom.className+=" x-form-text-search";
	            	}else if(textField.el.dom.className.indexOf("x-form-text-search")>=0){
	            		textField.el.dom.className=textField.el.dom.className.replace("x-form-text-search","");
	            	}
				},
				scope: thisPanel
			}
		});

		
		var tb = new Ext.Toolbar([joinMeasuresButton,'->',this.search]);

		return tb;
		
	},
	
	filter: function(value){
		if(value!=null && value!=undefined && value!=''){
			this.getStore().filterBy(function(record,id){
				
				if(record!=null && record!=undefined){
					var data = record.data;
					if(data!=null && data!=undefined){
						for(var p in data){
							if(data[p]!=null && data[p]!=undefined && ((""+data[p]).toUpperCase()).indexOf(value.toUpperCase())>=0){
								return true;
							}
						}
					}
				}
				return false;		
			});
		}else{
			this.getStore().clearFilter();
		}
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
		
    	var measuresProperties = "";
    	for(var i=0; i<this.measuresProperties.length; i++){
    		measuresProperties = measuresProperties+'<tr><td style="width: 100px"><p><b>'+this.measuresProperties[i].header+':</b></td><td><p>{'+this.measuresProperties[i].dataIndex+'}</p></td></tr>';
    	}
    	
    	var datasetsProperties = "";
    	for(var i=0; i<this.datasetsProperties.length; i++){
    		datasetsProperties = datasetsProperties+'<tr><td style="width: 100px"><p><b>'+this.datasetsProperties[i].header+':</b></td><td><p>{'+this.datasetsProperties[i].dataIndex+'}</p></td></tr>';
    	}
    	
		
	    var expander = new Ext.grid.RowExpander({

	    	

	        tpl : new Ext.Template(
	        		'<div class="htmltable">',
	        		'<div class="measure-detail-container"><div class="measure-detail-title"><h2><div class="group-header" style="background-image: none!important">'+OpenLayers.Lang.translate('sbi.tools.catalogue.measures.measure.properties')+'</div></h2></div>',
	        		'<table>',
	        		'		<tr style="height: 90px">',
	        		'			<td class="measure-detail-measure">',
	        		'			</td>',
	        		'			<td><table>',
	        						measuresProperties,
	        		'			</table></td>',			
	        		'		</tr>',
	        		'</table></div>',
	        		'<div class="dataset-detail-container"><div class="measure-detail-title"><h2><div class="group-header" style="background-image: none!important">'+OpenLayers.Lang.translate('sbi.tools.catalogue.measures.dataset.properties')+'</div></h2></div>',
	        		'<table>',
	        		'		<tr style="height: 100px">',
	        		'			<td class="measure-detail-dataset">',
	        		'			</td>',
	        		'			<td><table>',
	        						datasetsProperties,
	        		'			</table></td>',
	        		'		</tr>',
	        		'</table></div>',
	        		'</div>'
	        )
	    });
		
		
	    return expander;
	},
	


	
	/**
	 * @Private
	 */
	highlightSearchStringInternal: function (value, startIndex, searchString, thisPanel) {
        var startPosition = value.toLowerCase().indexOf(searchString.toLowerCase(), startIndex);
        if (startPosition >= 0 ) {
            var prefix = "";
            if (startPosition > 0 ) {
                prefix = value.substring(0, startPosition);
            }
            var filterSpan = "<span class='x-livesearch-match'>" + value.substring(startPosition, startPosition + searchString.length) + "</span>";
            var suffix = value.substring(startPosition + searchString.length);
            var newValue = prefix + filterSpan + suffix;
            return thisPanel.highlightSearchStringInternal(newValue, startPosition + filterSpan.length, searchString, thisPanel);
        } else {
        	return value;
        }
	}

});