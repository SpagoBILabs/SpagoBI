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
		
		return new Ext.grid.ColumnModel([
		                                 expander,
		                    			{id: 'alias',
		                    				header: 'Alias',
		                    				sortable: true,
		                    				dataIndex: 'alias',
		                    				renderer: highlightSearchString
		                    			},
		                    			{
		                    				header: 'DS Name',
		                    				sortable: true,
		                    				dataIndex: 'dsName',
		                    				renderer: highlightSearchString
		                    			},
		                    			{
		                    				header: 'DS Label',
		                    				sortable: true,
		                    				dataIndex: 'dsLabel',
		                    				renderer: highlightSearchString
		                    			},
		                    			{
		                    				header: 'DS Category',
		                    				sortable: true,
		                    				dataIndex: 'dsCategory',
		                    				renderer: highlightSearchString
		                    			},
		                    			{
		                    				header: 'DS Type',
		                    				sortable: true,
		                    				dataIndex: 'dsType',
		                    				renderer: highlightSearchString
		                    			},
		                    			sm
		                    		]);
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
	    		thisPanel.filter("")
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