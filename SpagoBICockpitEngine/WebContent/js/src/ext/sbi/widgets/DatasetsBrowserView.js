/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.widgets");

Sbi.widgets.DatasetsBrowserView = function(config) { 
	
	Sbi.trace("[DatasetsBrowserView.constructor]: IN");
	
	var defaultSettings = {		
			autoScroll: true
	};
		
	if(Sbi.settings && Sbi.cockpit && Sbi.widgets && Sbi.widgets.datasetsBrowserView) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.widgets.datasetsBrowserView);
	}

	this.initTemplate();
	
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	Sbi.widgets.DatasetsBrowserView.superclass.constructor.call(this, c);
	
	Sbi.trace("[DatasetsBrowserView.constructor]: OUT");
};

Ext.extend(Sbi.widgets.DatasetsBrowserView, Ext.DataView, {
	  itemSelector : 'dd' 
	, trackOver : true
	, overClass : 'box.over'
	, frame : true
	, emptyText : LN('No Documents')
	, inline : {wrap : false}
	, scrollable : 'horizontal'
	, services: null
	, store: null
	, widgetManager: null
	, activeFilter: null
    , tpl: null

    //override of the DataView.collectData method to manage visibility correctly
	,collectData: function(records, startIndex){
		var toReturn = new Array();
		var sm = this.widgetManager.getStoreManager();
		
		if (this.activeFilter == 'UsedDataSet' &&
				(sm == null || sm.getCount()== 0 || (sm.getCount() == 1 && sm.get('testStore') != undefined))){
			return toReturn;
		}
		
		for(var i=0; i < records.length; i++){	
			var addRecord = false;
			var lkey = sm.get(records[i].data.label);
			records[i] = this.prepareData(records[i].data, startIndex + i, records[i]);
			if (lkey !== null && lkey !== undefined){															
				records[i].isUsed = 'true';
				addRecord = true;
			}else if (this.activeFilter != 'UsedDataSet'){
				records[i].isUsed = 'false';
				addRecord = true;
			}
			if (addRecord) toReturn.push(records[i]);
		}
		return toReturn;
		
	}

   
	//Build the TPL
	,initTemplate : function() {

		Sbi.debug('DatasetsBrowserView building the tpl...');

		var tpl = null;
		var documentTpl = this.getDocumentTemplate();

		
		tpl = new Ext.XTemplate(
				 '<div id="list-container" class="main-datasets-list">',
				 	'<dl>',
					'<tpl for=".">',
					    '{[isUsed=""]}',
					    '{[label=""]}',
				 		'<dd id="{label}" class="box">',
				 		documentTpl,
						'<div class="fav-container" >',
						 '<tpl if="this.isAlreadyUsed(isUsed, label) == false">'+
							'	<div class="select"  title="'+LN('sbi.mydata.selectdocument')+'">',
							'    <a href="#"><span class="icon"></span></a> '+
							'	</div>',
				            '</tpl>'+
				            '<tpl if="this.isAlreadyUsed(isUsed, label) == true">'+
							'	<div class="select"  title="'+LN('sbi.mydata.unselectdocument')+'">',
							'    <a href="#"><span class="iconActive"></span></a> '+
							'	</div>',
				            '</tpl>'+
						'</div>',		

					    '</dd>',
				    '</tpl>',
				    '<div style="clear:left"></div>',
				    '</dl>',
			      '</div>', {
			      isAlreadyUsed: function(v, l) {
			    	  return v == 'true';		        		
		        	}
				 }
				);
		Sbi.debug('DatasetsBrowserView tpl built.');
	
		this.tpl = tpl;
	}

	
	,getDocumentTemplate : function(){
		var img = Ext.BLANK_IMAGE_URL ;

		var classImg = ' class="measure-detail-dataset" ';
		
		var author = LN('sbi.generic.author');
//		var changed = LN('sbi.ds.changedon');

		var currentUser = Sbi.config.userId;
		
		
		var documentTpl = ''+
		'<div class="box-container">'+
			'<div id="document-item-icon" class="box-figure">'+
				'<img  align="center" src="' + img + '" '+ classImg+'" + ext:qtip="<b>{views}</b><br/>{summary}"></img>' +
				'<span class="shadow"></span>'+		
			'</div>'+ //box-figure
			'<div title="{name}" class="box-text">'+
				'<h2>{name}</h2>'+
//				'<p>{[Ext.String.ellipsis(values.description, 100, false)]}</p>'+
				'<p>{description}</p>'+
				'<p><b>'+author+':</b> {owner}</p>'+
//				'<p class="modified">'+changed+' {dateIn}</p>'+
			'</div>'+
		'</div>';
		
		return documentTpl;
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});
