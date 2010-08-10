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

Sbi.formviewer.FormViewerPage = function(template, config) {
	
	var defaultSettings = {
		// set default values here
		title: LN('sbi.formviewer.formviewerpage.title')
		, layout: 'fit'
		, autoScroll: true
		//, bodyStyle: 'padding:30px'
	};
	
	if (Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.formViewerPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.formViewerPage);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	// add events
    this.addEvents('submit');
	
	this.init(template);
	
	this.toolbar = new Ext.Toolbar({
		items: [
		    '->'
		    , {
				text: LN('sbi.formviewer.formviewerpage.execute'),
				handler: function() {
		    		this.validateForm(function() {
		    			var state = this.getFormState();
		    			this.fireEvent('submit', state);
		    		}, this);
		    	},
				scope: this
		    }
		  ]
	});
	
	Ext.apply(c, {
		tbar: this.toolbar
  		, items: this.items
	});
	
	// constructor
    Sbi.formviewer.FormViewerPage.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.formviewer.FormViewerPage, Ext.Panel, {
    
    services: null
    , staticClosedFiltersPanel: null
    , staticOpenFiltersPanel: null
    , dynamicFiltersPanel: null
    , groupingVariablesPanel: null
   
    // private methods
    , init: function(template) {
		this.items = [];
		if (template.staticClosedFilters !== undefined && template.staticClosedFilters !== null && template.staticClosedFilters.length > 0) {
			this.staticClosedFiltersPanel = new Sbi.formviewer.StaticClosedFiltersPanel(template.staticClosedFilters); 
			this.items.push(this.staticClosedFiltersPanel);
		}
		if (template.staticOpenFilters !== undefined && template.staticOpenFilters !== null && template.staticOpenFilters.length > 0) {
			this.staticOpenFiltersPanel = new Sbi.formviewer.StaticOpenFiltersPanel(template.staticOpenFilters); 
			this.items.push(this.staticOpenFiltersPanel);
		}
		if (template.dynamicFilters !== undefined && template.dynamicFilters !== null && template.dynamicFilters.length > 0) {
			this.dynamicFiltersPanel = new Sbi.formviewer.DynamicFiltersPanel(template.dynamicFilters); 
			this.items.push(this.dynamicFiltersPanel);
		}
		if (template.groupingVariables !== undefined && template.groupingVariables !== null && template.groupingVariables.length > 0) {
			this.groupingVariablesPanel = new Sbi.formviewer.GroupingVariablesPanel(template.groupingVariables); 
			this.items.push(this.groupingVariablesPanel);
		}
		
		// work-around for layout management on resize event, since components are not automatically resized
	    if (this.staticClosedFiltersPanel != null) {
			this.staticClosedFiltersPanel.on('resize', function (component, adjWidth, adjHeight, rawWidth, rawHeight) {
		    	if (this.staticOpenFiltersPanel != null) {
		    		this.staticOpenFiltersPanel.doLayout();
		    	}
		    	if (this.dynamicFiltersPanel != null) {
		    		this.dynamicFiltersPanel.doLayout();
		    	}
		    	if (this.groupingVariablesPanel != null) {
		    		this.groupingVariablesPanel.doLayout();
		    	}
		    }, this);
	    }
		
	}
    
	, validateForm: function(successHandler, obj) {
		var errors = new Array();
		if (this.staticOpenFiltersPanel !== null) {
			var openFiltersErrors = this.staticOpenFiltersPanel.getErrors();
			errors = errors.concat(openFiltersErrors);
		}
		if (this.dynamicFiltersPanel !== null) {
			var dynamicFiltersErrors = this.dynamicFiltersPanel.getErrors();
			errors = errors.concat(dynamicFiltersErrors);
		}
		if (errors.length == 0 && successHandler !== undefined) {
			successHandler.call(obj || this);
		} else {
			Sbi.exception.ExceptionHandler.showErrorMessage(errors.join('<br/>'), LN('sbi.formviewer.formviewerpage.validation.error'));
		}
	}

    // public methods

	, getFormState: function() {
		var state = {};
		if (this.staticClosedFiltersPanel !== null) {
			state.staticClosedFilters = this.staticClosedFiltersPanel.getFormState();
		}
		if (this.staticOpenFiltersPanel !== null) {
			state.staticOpenFilters = this.staticOpenFiltersPanel.getFormState();
		}
		if (this.dynamicFiltersPanel !== null) {
			state.dynamicFilters = this.dynamicFiltersPanel.getFormState();
		}
		if (this.groupingVariablesPanel !== null) {
			state.groupingVariables = this.groupingVariablesPanel.getFormState();
		}
		return state;
	}
  	
});