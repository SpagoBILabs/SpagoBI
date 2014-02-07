/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.widgets");

Sbi.widgets.DatasetsBrowserView = function(config) { 
	
	Sbi.trace("[DatasetsBrowserView.constructor]: IN");
	
	var defaultSettings = {		
		autoScroll: false
		, height: '350px'
	};
	var settings = Sbi.getObjectSettings('Sbi.widgets.DatasetsBrowserView', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.initTemplate();

	Sbi.widgets.DatasetsBrowserView.superclass.constructor.call(this, c);
	
	Sbi.trace("[DatasetsBrowserView.constructor]: OUT");
};

Ext.extend(Sbi.widgets.DatasetsBrowserView, Ext.DataView, {
	  itemSelector : 'dd' 
	, trackOver : true
	, overClass : 'over'
	, frame : true
	, emptyText : LN('No Documents')
	, inline : {wrap : false}
	, scrollable : 'horizontal'
	, services: null
	, store: null
	, widgetManager: null
	, filterOnType: null
    , tpl: null

    //override of the DataView.collectData method to manage visibility correctly
	, collectData: function(records, startIndex){
		Sbi.trace("[DatasetsBrowserView.collectData]: IN");
		var toReturn = new Array();

		var storeManager = this.widgetManager.getStoreManager();

		if (this.filterOnType == 'UsedDataSet' &&
				(storeManager == null || storeManager.getCount()== 0 )){
			Sbi.trace("[DatasetsBrowserView.collectData]: There are no datasets in use");
			return toReturn;
		}
		
		Sbi.trace("[DatasetsBrowserView.collectData]: There are [" + records.length + "] dataset in use");
		for(var i=0; i < records.length; i++){	
			var addRecord = false;
			var lkey = storeManager.get(records[i].data.label);
			records[i] = this.prepareData(records[i].data, startIndex + i, records[i]);
			if (lkey !== null && lkey !== undefined){															
				records[i].isUsed = 'true';
				addRecord = true;
			} else if (this.filterOnType != 'UsedDataSet'){
				records[i].isUsed = 'false';
				addRecord = true;
			}
						
			if(this.selectedDatasetLabel == records[i].label)
				records[i].isMyDataset = 'true';
			else
				records[i].isMyDataset = 'false';
			
			if (addRecord) toReturn.push(records[i]);
		}
		
		Sbi.trace("[DatasetsBrowserView.collectData]: OUT");
		
		return toReturn;
	}

   
	//Build the TPL
	, initTemplate : function() {

		Sbi.trace("[DatasetsBrowserView.initTemplate]: IN");
		
		var tpl = null;
		var datasetsTpl = this.getDatasetsTemplate();

		
		tpl = new Ext.XTemplate(
				 '<div id="list-container" class="main-datasets-list">',
				 	'<dl>',
					'<tpl for=".">',
					    '{[isUsed=""]}',
					    '{[label=""]}',
					    '{[isMyDataset=""]}',
					    '<tpl if="this.checkMyDataset(isMyDataset, label) == true">'+		
					 		'<dd id="{label}" class="box selectboxDS">',
					 			datasetsTpl,
						    '</dd>',
					    '</tpl>'+
					    '<tpl if="this.checkMyDataset(isMyDataset, label) == false && this.isAlreadyUsed(isUsed, label) == true">'+		
					 		'<dd id="{label}" class="box selectbox">',
					 			datasetsTpl,
						    '</dd>',
					    '</tpl>'+
				        '<tpl if="this.checkMyDataset(isMyDataset, label) == false && this.isAlreadyUsed(isUsed, label) == false">'+
					        '<dd id="{label}" class="box">',
					 			datasetsTpl,
						    '</dd>',
					    '</tpl>'+
				    '</tpl>',
				    '<div style="clear:left"></div>',
				    '</dl>',
			      '</div>', {
			        isAlreadyUsed: function(v, l) {
			    	  return v == 'true';		        		
		        	},
	        	    checkMyDataset: function(v, l) {
			    	  return v == 'true';		        		
		        	},
		        	shorten: function(text){
		                return Ext.util.Format.ellipsis(text,55,false);
		            }
				 }
				);
			
		this.tpl = tpl;
		
		Sbi.trace("[DatasetsBrowserView.initTemplate]: OUT");
	}

	
	,getDatasetsTemplate : function(){
		var img = Ext.BLANK_IMAGE_URL ;

		var classImg = ' class="measure-detail-dataset" ';
		
		var author = LN('sbi.generic.author');
//		var changed = LN('sbi.ds.changedon');
		
		var datasetTpl = ''+
		'<div class="box-container">'+
	        '<div id="box-figure-{label}" class="box-figure">'+
//				'<img  align="center" src="' + img + '" '+ classImg+'" + ext:qtip="<b>{views}</b><br/>{summary}"></img>' +	
				'<img  align="center" src="' + img + '" '+ classImg +'"></img>' +
			'</div>'+ //box-figure
			'<tpl if="this.isAlreadyUsed(isUsed, label) == true">'+	
				'<div id="box-text-{label}" title="{name}" class="box-text box-text-select">'+
					'<h2>{name}</h2>'+
					'<p>{[this.shorten(values.description)]}</p>'+
//					'<p>{description}</p>'+				
					'<p><b>'+author+':</b> {owner}</p>'+
				'</div>'+
			'</tpl>'+
	        '<tpl if="this.isAlreadyUsed(isUsed, label) == false">'+
		        '<div id="box-text-{label}" title="{name}" class="box-text">'+
					'<h2>{name}</h2>'+
						'<p>{[this.shorten(values.description)]}</p>'+
//					'<p>{description}</p>'+
					'<p><b>'+author+':</b> {owner}</p>'+
				'</div>'+
			'</tpl>'+
		'</div>';
		
		return datasetTpl;
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
