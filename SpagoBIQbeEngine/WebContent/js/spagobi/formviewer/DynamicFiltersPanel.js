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

Sbi.formviewer.DynamicFiltersPanel = function(dynamicFilters, config) {
	
	var defaultSettings = {
		// set default values here
		title: LN('sbi.formviewer.dynamicfilterspanel.title')
		, autoScroll: true
		, frame: true
		, autoHeight: true
		, style:'padding:10px'
	};
	if (Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.dynamicFiltersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.dynamicFiltersPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});

	this.init(dynamicFilters);
	
	Ext.apply(c, {
        items: this.items
	});
	
	// constructor
    Sbi.formviewer.DynamicFiltersPanel.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.formviewer.DynamicFiltersPanel, Ext.Panel, {
    
	services: null
	, dynamicFilters: new Array()
	   
	// private methods
	   
	, init: function(dynamicFiltersConfig) {
		this.items = [];
		for(var i = 0; i < dynamicFiltersConfig.length; i++) {
			var aDynamicFilter = new Sbi.formviewer.DynamicFilter(dynamicFiltersConfig[i]);
			this.items.push(aDynamicFilter);
			this.dynamicFilters.push(aDynamicFilter);
		}
	}

	// public methods
	
	, getFormState: function() {
		var state = {};
		for(var i = 0; i < this.dynamicFilters.length; i++) {
			var aDynamicFilter = this.dynamicFilters[i];
			var aDynamicFilterState = aDynamicFilter.getFormState();
			state[aDynamicFilter.id] = aDynamicFilterState;
		}
		return state;
	}

	
	, getErrors: function() {
		var errors = new Array();
		for(var i = 0; i < this.dynamicFilters.length; i++) {
			var aDynamicFilter = this.dynamicFilters[i];
			if (!aDynamicFilter.isValid()) {
				var validationErrors = aDynamicFilter.getValidationErrors();
				errors = errors.concat(validationErrors);
			}
		}
		return errors;
	}
	
});