/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/**

  * Authors
  *
  * - Giorgio Federici (giorgio.federici@eng.it)
  */

	Ext.define('Sbi.fonts.editor.main.FontEditorTabsPanel', {
	extend: 'Ext.tab.Panel'
	, layout: 'fit'
	, config:{
		  services: null
		, store: null
		, storesList: null
		, fonts: null
		, border: false
		, height: 180
		, autoScroll: false
		, tabPosition: 'right'
		, margin: 0
		, padding: 0
		, bodyStyle: 'width: 100%; height: 100%'
	}
	
	/**
	 * @property fontEditorBarChartTab
	 *  Tab for bar chart font options
	 */
	 , fontEditorBarChartTab: null
	 
	 /**
	 * @property fontEditorLineChartTab
	 *  Tab for line chart font options
	 */
	 , fontEditorLineChartTab: null
	 
	 /**
	 * @property fontEditorPieChartTab
	 *  Tab for pie chart font options
	 */
	 , fontEditorPieChartTab: null
	 
	 /**
	 * @property fontEditorTableTab
	 *  Tab for table font options
	 */
	 , fontEditorTableTab: null
	 
	 /**
	 * @property fontEditorCrosstabTab
	 *  Tab for crosstab font options
	 */
	 , fontEditorCrosstabTab: null

	, constructor : function(config) {
		Sbi.trace("[FontEditorTabsPanel.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(arguments);
		Sbi.trace("[FontEditorTabsPanel.constructor]: OUT");
	}

	, initComponent: function() {
        Ext.apply(this, {
			items:[
		         this.fontEditorBarChartTab,
		         this.fontEditorLineChartTab,
		         this.fontEditorPieChartTab,
		         this.fontEditorTableTab,
		         this.fontEditorCrosstabTab
		         ]
        });
        this.callParent();
	}

    // =================================================================================================================
	// METHODS
	// =================================================================================================================

    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * @method
	 *
	 * Initialize the GUI
	 */
	, init: function() {
		//this.initStore();
		this.initTabs();
	}
	
	, initTabs: function() {
		this.fontEditorBarChartTab = Ext.create('Sbi.fonts.views.BarChartFontTabPanel',{});
        this.fontEditorLineChartTab = Ext.create('Sbi.fonts.views.LineChartFontTabPanel',{});
        this.fontEditorPieChartTab = Ext.create('Sbi.fonts.views.PieChartFontTabPanel',{});
        this.fontEditorTableTab = Ext.create('Sbi.fonts.views.TableFontTabPanel',{});
        this.fontEditorCrosstabTab = Ext.create('Sbi.fonts.views.CrosstabFontTabPanel',{});
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , getFontsList: function(){
    	if (this.fonts == null) this.fonts = [];
    	console.log(this.fontEditorBarChartTab);
//    	for (var i=0; i<this.store.data.length;i++){
//    		var rec = this.store.getAt(i);
//    		var filter = {
//    				id:  rec.get('id')
//    			  , labelObj: rec.get('labelObj')
//    			  , typeObj: rec.get('typeObj')
//    			  , nameObj: rec.get('nameObj')
//    			  , namePar: rec.get('namePar')
//    			  , typePar: rec.get('typePar')
//    			  , scope: rec.get('scope')
//    			  , initialValue: rec.get('initialValue')
//    		};
//    		this.filters.push(filter);
//    	}
    	
    	return this.fonts;
    }

    , setFontList: function(f){
    	this.fonts = f;
    }

    , removeAllFonts: function(){
    	this.fonts = new Array();
    }


	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------

});