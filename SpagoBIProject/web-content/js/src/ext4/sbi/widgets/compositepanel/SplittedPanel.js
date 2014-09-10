/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Marco Cortella (marco.cortella@eng.it)
 */

Ext.define('Sbi.widgets.compositepannel.SplittedPanel', {
    extend: 'Ext.Panel'

    ,config: {
    	
    	/**
    	 * the panel on the left
    	 */
    	leftPanel: null,
    	/**
    	 * the panel on the right
    	 */
    	rightPanel: null,
    	/**
    	 * the main panel title show on the header
    	 */
    	mainTitle: null
    }

	/**
	 * In this constructor you must pass the leftPanel and the rightPanel 
	 */
	, constructor: function(config) {
		this.initConfig(config);
		this.layout = 'border';
		
		//TODO: scommentare quando l'implementazione sarà completa
		
		if( !this.leftPanel ) {
			alert('The leftPanel must be defined');
			throw "The leftPanel must be defined";
		}
		
		if( !this.rightPanel ) {
			alert('The rightPanel must be defined');
			throw "The rightPanel must be defined";
		}
		
		
		Ext.apply(this,config||{});
		/*
		this.grid = Ext.create('Sbi.widgets.grid.FixedGridPanelInMemoryFiltered', fixedGridPanelConf);
		

		this.grid.region = "west";
		this.grid.width = "35%";
		this.detailPanel.region = "center";
		*/
		
		this.leftPanel.region = "west";
		this.leftPanel.width = "50%";

		this.rightPanel.region = "center"; 
		this.rightPanel.width = "50%";

		
		//TODO: prendere i pannelli passati in input
		/*
		this.items = [
		              {
		            	   xtype: 'toolbar',
		            	   region: 'north',
		            	   height: 30,
		            	   html: '<b>Toolbar</b>',
		            	  },       
		       {
			   xtype: 'panel',
			   region: 'west',
			   html: 'Western Region',
			   width: '50%',
			   title: 'Western Region'
			   //collapseDirection: 'left',
			   //collapsible: true,
			   //split: true
			  },
			  {
			   xtype: 'panel',
			   width: '50%',
			   itemId: 'centerregion',
			   region: 'center',
			   title: 'Center region',
			   html: 'Center region'
			  }];
		*/
		this.items = [{
     	   xtype: 'toolbar',
    	   region: 'north',
    	   height: 30,
    	   html: '<b>'+this.mainTitle+'</b>',
    	  }, this.leftPanel,this.rightPanel];
		this.callParent(arguments);
		

	}
	

	



	
});