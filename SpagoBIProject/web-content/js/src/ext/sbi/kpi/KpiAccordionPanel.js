/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/

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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiAccordionPanel =  function(config) {
		
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiAccordionPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiAccordionPanel);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
	
	    c = {
	        region:'east',
	        fill: true,
	        split:true,
	        width: 300,
	        minSize: 275,
	        maxSize: 600,
	        collapsible: true,
	        layout:'accordion',
	        items: []
	    };
	    this.initDetail();
	    this.initDescription();
	    this.initHistorical();
		this.initAccordion(c);
   
		Sbi.kpi.KpiAccordionPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiAccordionPanel , Ext.Panel, {
	detail: null
	, description: null
	, historical: null
	
	, initAccordion: function(c){

	    var item1 = new Ext.Panel({
	        title: 'Dettaglio',
	        items: [this.detail],
	        autoScroll: true,
	        cls:'empty'
	    });
	    
	    var item2 = new Ext.Panel({
	        title: 'Descrizione',
	        items: [this.description],
	        cls:'empty'
	    });

	    var item3 = new Ext.Panel({
	        title: 'Doc collegati',
	        html: '&lt;empty panel&gt;',
	        cls:'empty'
	    });

	    var item4 = new Ext.Panel({
	        title: 'Commenti',
	        html: '&lt;empty panel&gt;',
	        cls:'empty'
	    });

	    var item5 = new Ext.Panel({
	        title: 'Storico',
	        items: [this.historical],
	        autoScroll: true,
	        cls:'empty'
	    });
	    c.items = [item1, item2, item3, item4, item5];
	}
	, initDetail: function(){
		this.detail = new Sbi.kpi.KpiGUIDetail();
	}
	, initDescription: function(){
		this.description = new Sbi.kpi.KpiGUIDescription();
	}
	, initHistorical: function(){
		this.historical = new Sbi.kpi.KpiGUIHistorical();
	}
	, updateAccordion: function(field){
		//detail
		this.detail.update(field);
		//description
		this.description.update(field);
		//historical
		this.historical.update(field);
		this.render();
	}
});