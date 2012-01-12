/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
	        width: 550,
	        minSize: 500,
	        maxSize: 600,
	        collapsible: true,
	        layout:'accordion',
	        items: []
	    };
	    this.initDetail(config);
	    this.initDescription();
	    this.initDocCollegato();
	    this.initComments(config);
	    this.initHistorical(config);
		this.initAccordion(c);
		
		
		Sbi.kpi.KpiAccordionPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiAccordionPanel , Ext.Panel, {
	detail: null
	, description: null
	, docs: null
	, comments: null
	, historical: null
	, itemDetail: null
	, itemDocColl: null
	, itemNoKpi: null
	
	, initAccordion: function(c){

		this.itemNoKpi = new Ext.form.DisplayField({value: 'Nessun Kpi associato.', 
			style: 'font-weight: bold; align:center; margin: 20px;'});
		
	    this.itemDetail = new Ext.Panel({
	        title: 'Dettaglio',
	        items: [this.detail, this.itemNoKpi ],
	        autoScroll: true,
            listeners : {
                expand: function(p){
                    p.doLayout();
                }
            },
	        cls:'empty'
	    });
	    
	    var item2 = new Ext.Panel({
	        title: 'Descrizione',
	        items: [this.description],
	        cls:'empty'
	    });

	    this.itemDocColl = new Ext.Panel({
	        title: 'Doc collegato',
	        items: [this.docs],
	        scope: this,
            listeners : {
                expand: function(p){
                    p.doLayout();
                }
            },
	        autoScroll: true
	    });

	    var item4 = new Ext.Panel({
	        title: 'Commenti',
	        scope: this,
	        items: [this.comments],
	        
	        listeners : {
	            expand: function(p){
	                p.doLayout();
	            }
	        }
	    });

	    var item5 = new Ext.Panel({
	        title: 'Storico',
	        items: [this.historical],
	        autoScroll: true,
	        cls:'empty'
	    });
	    c.items = [this.itemDetail, item2, this.itemDocColl, item4, item5];
	}
	, initDetail: function(c){
		this.detail = new Sbi.kpi.KpiGUIDetail(c);

	}
	, initDescription: function(){
		this.description = new Sbi.kpi.KpiGUIDescription();
	}
	, initHistorical: function(c){
		this.historical = new Sbi.kpi.KpiGUIHistorical(c);
	}
	, initDocCollegato: function(){
		this.docs = new Sbi.kpi.KpiGUIDocCollegato();

	}
	, initComments: function(c){
		this.comments = new Sbi.kpi.KpiGUIComments(c);	

	}
	, updateAccordion: function(field){
		//detail
		if(field.attributes == undefined || field.attributes.kpiName === undefined || field.attributes.kpiName === null){
			this.detail.updateEmpy();
			this.itemNoKpi.show();
		}else{
			this.detail.update(field);
			this.itemNoKpi.hide();
		}
		
		//description
		this.description.update(field);
		
		//linked docs
		if(field.attributes != undefined && field.attributes.documentLabel != undefined){
			this.itemDocColl.setTitle('Doc collegato '+field.attributes.documentLabel);
			this.itemDocColl.show();
		}else{
			this.itemDocColl.hide();
		}
		this.docs.update(field);
		//comments
		this.comments.update(field);
		//historical
		this.historical.update(field);
		this.render();
	}
});