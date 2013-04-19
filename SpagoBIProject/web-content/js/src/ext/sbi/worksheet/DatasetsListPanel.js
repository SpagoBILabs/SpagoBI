/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */


Ext.ns("Sbi.worksheet");

Sbi.worksheet.DatasetsListPanel = function(config) {

	var defaultSettings = {
		frame : true
		, pagingSize : 15
		, keyPressedDelay : 400
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.datasetslistpanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.datasetslistpanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.services = this.services || new Array(); 
	this.services['getDatasetsList'] = this.services['getDatasetsList'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_DATASETS_USER_LIST'
		, baseParams: new Object()
	});
	
	this.init();
	
	c = Ext.apply(c, {
		items: [this.datasetsGrid]
	});
	
	// constructor
    Sbi.worksheet.DatasetsListPanel.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.worksheet.DatasetsListPanel, Ext.Panel, {
	
	datasetsStore : null
	, datasetsGrid : null
	, searchInput : null
	, keyPressedDelay : null
	
	,
	init : function () {
		
		this.datasetsStore = new Sbi.widgets.store.InMemoryFilteredStore({
	    	autoLoad: false    	  
			, url: this.services['getDatasetsList']
			, reader : new Ext.data.JsonReader({
				root: 'rows'
				, id : 'id'
		    	, fields: ['id', 'name',
							'label', 'description', 'dsTypeCd',
							'catTypeVn', 'usedByNDocs', 'fileName',
							'query', 'dataSource', 'wsAddress',
							'wsOperation', 'script', 'scriptLanguage',
							'jclassName', 'customData', 'pars', 'trasfTypeCd',
							'pivotColName', 'pivotColValue',
							'pivotRowName', 'pivotIsNumRows', 'dsVersions',
							'qbeSQLQuery', 'qbeJSONQuery', 'qbeDataSource',
							'qbeDatamarts',	'userIn', 'dateIn', 'versNum', 'versId']
			})
		});
		this.datasetsStore.load({params : {start: 0, limit: this.pagingSize}});
		
		var pagingBar = new Ext.PagingToolbar({
			pageSize : this.pagingSize
			, store : this.datasetsStore
			, displayInfo : false
			, scope : this
			, emptyMsg : "No topics to display"
	        , items : this.extraButtons
		});
		
		this.searchInput = new Ext.form.TextField({
			emptyText: LN('sbi.generic.search.msg')
			, enableKeyEvents : true
			, listeners : {
				keyup : this.keyupHandler
				, scope: this
			 }
		});
		
		var toolbar =  new Ext.Toolbar([
		    {xtype: 'tbtext', text: LN('sbi.generic.search.title'), style: {'padding-left': 20}}
			, this.searchInput
			, {xtype: 'tbspacer', width: 30}
			, {
				iconCls : 'icon-clear'
				, handler : this.clearFilterForm
				, scope : this
				, tooltip: LN('sbi.config.manageconfig.fields.clearFilter')
			}
		]);
		
		this.datasetsGrid = new Ext.grid.GridPanel({
		    store: this.datasetsStore
		    , tbar : toolbar
		    , columns : [
		        {header: LN('sbi.generic.label'), dataIndex: 'label', scope : this, renderer: function (value) {return '<b>' + this.highlightSearchString(value) + '</b>';}}
		        , {header: LN('sbi.generic.name'), dataIndex: 'name', scope : this, renderer: function (value) {return '<b>' + this.highlightSearchString(value) + '</b>';}}
		        , {header: LN('sbi.generic.type'), dataIndex : 'dsTypeCd', scope : this, renderer: this.highlightSearchString }
		        , {header: LN('sbi.generic.author'), dataIndex : 'userIn', scope : this, renderer: this.highlightSearchString }
		    ]
		    , viewConfig : {
		        forceFit : true
	            , enableRowBody : true
	            , getRowClass : this.getRowClass.createDelegate(this)
				, scope : this
		    }
		    , sm : new Ext.grid.RowSelectionModel({singleSelect:true})
            , bbar : pagingBar
		});
		
	}

	,
	getRowClass : function(record, rowIndex, p, store) {
    	var description = record.data.description;
        p.body = (description == null || description == '') ? 
        		'<p class="x-grid-empty">[' + LN('sbi.generic.missing.description') + ']</p>'
        		: '<p class="x-grid3-cell-inner">' + this.highlightSearchString(description) + '</p>';
        return 'x-grid3-row-expanded';
    }

	,
	keyupHandler : function () {
	    if (this.keyPressedTimeOut) {
	        clearTimeout(this.keyPressedTimeOut);
	    }           
	    this.keyPressedTimeOut = this.filterStore.defer(this.keyPressedDelay, this);
	}

	,
	highlightSearchString : function (value) {
		var searchString = this.searchInput.getValue().toUpperCase();
		if (value != undefined && value != null && searchString != '') {
			return this.highlightSearchStringInternal(value, 0, searchString);
		}
		return value;
	}

	,
	/**
	 * @Private
	 */
	highlightSearchStringInternal: function (value, startIndex, searchString) {
        var startPosition = value.toLowerCase().indexOf(searchString.toLowerCase(), startIndex);
        if (startPosition >= 0 ) {
            var prefix = "";
            if (startPosition > 0 ) {
                prefix = value.substring(0, startPosition);
            }
            var filterSpan = "<span class='x-livesearch-match'>" + value.substring(startPosition, startPosition + searchString.length) + "</span>";
            var suffix = value.substring(startPosition + searchString.length);
            var newValue = prefix + filterSpan + suffix;
            return this.highlightSearchStringInternal(newValue, startPosition + filterSpan.length, searchString);
        } else {
        	return value;
        }
	}

	,
	getSelectedRecord : function () {
		var record = this.datasetsGrid.getSelectionModel().getSelected();
		return record;
	}
	
	,
	filterStore : function () {
		var searchString = this.searchInput.getValue().toUpperCase();
		var filter = {};
		if (searchString !== '') {
			filter.filterString = searchString;
			filter.columnsToFilter = ['label', 'name', 'description', 'dsTypeCd', 'userIn'];
		}
		this.datasetsStore.load(
			{
				params : {
					start: 0
					, limit: this.pagingSize
					, filter : filter
				}
			}
		);
	}
	
	,
	clearFilterForm : function () {
		this.searchInput.setValue('');
		this.datasetsStore.load({params : {start: 0, limit: this.pagingSize, filter: {}}});
	}

});