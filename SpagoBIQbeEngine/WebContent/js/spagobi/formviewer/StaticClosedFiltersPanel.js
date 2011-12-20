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

Ext.ns("Sbi.formviewer");

Sbi.formviewer.StaticClosedFiltersPanel = function(staticFilters, config) {
	
	var defaultSettings = {
		// set default values here
		title: LN('sbi.formviewer.staticclosedfilterspanel.title')
		, layout: 'table'
	    , layoutConfig: {
	        columns: staticFilters.length
	    }
		, frame: true
		, autoScroll: true
		, autoWidth: true
		, autoHeight: true
		, style:'padding:10px'
	};
	if (Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.staticClosedFiltersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.staticClosedFiltersPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	this.init(staticFilters);
	
	Ext.apply(c, {
  		items: this.forms
	});
	
	// constructor
    Sbi.formviewer.StaticClosedFiltersPanel.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.formviewer.StaticClosedFiltersPanel, Ext.Panel, {
    
	services: null
	, forms: null
	, xorFilters: new Array()
	, onOffFilters: new Array()
	   
	// private methods
	   
	, init: function(staticFilters) {
		this.forms = [];
		for (var i = 0; i < staticFilters.length; i++) {
			var aStaticFiltersGroup = staticFilters[i];
			var aStaticFiltersForm = null;
			var config = {};
			if (aStaticFiltersGroup.width !== undefined) {
				config.width = aStaticFiltersGroup.width;
			}
			if (aStaticFiltersGroup.height !== undefined) {
				config.height = aStaticFiltersGroup.height;
			}
			if (aStaticFiltersGroup.singleSelection) {
				aStaticFiltersForm = new Sbi.formviewer.StaticClosedXORFiltersPanel(aStaticFiltersGroup, config);
				this.xorFilters.push(aStaticFiltersForm);
			} else {
				aStaticFiltersForm = new Sbi.formviewer.StaticClosedOnOffFiltersPanel(aStaticFiltersGroup, config);
				this.onOffFilters.push(aStaticFiltersForm);
			}
			this.forms.push(aStaticFiltersForm);
		}
		
	}

	   
	// public methods
	
	, getFormState: function() {
		var state = {};
		state.xorFilters = {};
		state.onOffFilters = {};
		if (this.xorFilters !== null) {
			for (var i = 0; i < this.xorFilters.length; i++) {
				var aXORFilter = this.xorFilters[i];
				Ext.apply(state.xorFilters, aXORFilter.getFormState());
			}
		}
		if (this.onOffFilters !== null) {
			for (var i = 0; i < this.onOffFilters.length; i++) {
				var aOnOffFilter = this.onOffFilters[i];
				state.onOffFilters[aOnOffFilter.id] = aOnOffFilter.getFormState();
			}
		}
		return state;
	}
	
	, setFormState: function(values) {
		var XORFilters = values.xorFilters;
		for(var j in XORFilters){
			if (this.xorFilters !== null) {
				for (var i = 0; i < this.xorFilters.length; i++) {
					var aXORFilter = this.xorFilters[i];
					if(j==aXORFilter.items.items[0].name){
						aXORFilter.setFormState(XORFilters[j]);
						break;
					}
				}
			}
		}
		var onOffFilters = values.onOffFilters;
		for(var j in onOffFilters){
			if (this.onOffFilters !== null) {
				for (var i = 0; i < this.onOffFilters.length; i++) {
					var aOnOffFilter = this.onOffFilters[i];
					if(j==aOnOffFilter.items.items[0].name){
						aOnOffFilter.setFormState(onOffFilters[j]);
						break;
					}
				}
			}
		}
	}
  	
});