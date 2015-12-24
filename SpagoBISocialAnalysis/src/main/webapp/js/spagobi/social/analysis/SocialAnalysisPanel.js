/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * UI container of the Social Analysis Search.<br>
 * 
 * It contains:
 * <ul>
 *		<li>Social Analysis Search Form</li>
 *		<li>Social Analysis Streaming Grid</li>
 *		<li>Social Analysis Historical Grid</li>
 *	</ul>
 * 
 *     
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */
  
Ext.define('Sbi.social.analysis.SocialAnalysisPanel', {
	extend: 'Ext.panel.Panel',
	
	layout: {
	    type: 'vbox',
	    align : 'stretch',
	},
	
	flex: 1,
	
	config:{

	},
	
	/**
     * @property {Sbi.social.analysis.search.view.SocialAnalysisSearchForm} socialAnalysisSearchForm
     *  Search Form
     */
	socialAnalysisSearchForm: null,
	
	/**
     * @property {Sbi.social.analysis.search.view.SocialAnalysisSearchStreamingGrid} socialAnalysisSearchStreamingGrid
     *  Grid for streaming search
     */
	socialAnalysisSearchStreamingGrid: null,
	
	/**
     * @property {Sbi.social.analysis.search.view.SocialAnalysisSearchHistoricalGrid} socialAnalysisSearchHistoricalGrid
     *  Grid for historical search
     */
	socialAnalysisSearchHistoricalGrid: null,
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.socialAnalysisSearchForm = Ext.create('Sbi.social.analysis.search.view.SocialAnalysisSearchForm', {});
		this.socialAnalysisSearchStreamingGrid = Ext.create('Sbi.social.analysis.search.view.SocialAnalysisSearchStreamingGrid', {});
		this.socialAnalysisSearchHistoricalGrid = Ext.create('Sbi.social.analysis.search.view.SocialAnalysisSearchHistoricalGrid', {});
		
		
		this.callParent(arguments);
		
		this.initEvents();
	},
	
	initComponent: function() {

		Ext.apply(this, {
			items: [this.socialAnalysisSearchForm, this.socialAnalysisSearchStreamingGrid, this.socialAnalysisSearchHistoricalGrid]
		});
		
		this.socialAnalysisSearchForm.on('searchSubmit', this.updateSearchGrids, this);
		this.socialAnalysisSearchForm.on('refreshGrids', this.updateSearchGrids, this);
		
		
		
		this.callParent();
	},
	
	updateSearchGrids: function(){
		this.socialAnalysisSearchStreamingGrid.getStore().load();
		this.socialAnalysisSearchHistoricalGrid.getStore().load();
	}
	
});