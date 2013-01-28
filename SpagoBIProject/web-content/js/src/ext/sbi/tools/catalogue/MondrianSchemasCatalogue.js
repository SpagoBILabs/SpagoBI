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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.tools.catalogue");

Sbi.tools.catalogue.MondrianSchemasCatalogue = function(config) {
	
	var defaultSettings = {
		configurationObject : {
			panelTitle : LN('sbi.tools.catalogue.mondrianSchemasCatalogue')
			, listTitle : LN('sbi.tools.catalogue.mondrianSchemasCatalogue')
		}
	};
	  
	if (Sbi.settings && Sbi.settings.tools && Sbi.settings.tools.catalogue && Sbi.settings.tools.catalogue.mondrianschemascatalogue) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.tools.catalogue.mondrianschemascatalogue);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	  
	Ext.apply(this, c);
	
	var baseParams = {
		LIGHT_NAVIGATOR_DISABLED: 'TRUE'
		, type : 'MONDRIAN_SCHEMA'
	};
	
	// start services for main catalog list
	c.mainListServices = {
		'manageListService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_ARTIFACTS_ACTION'
				, baseParams: baseParams
		})
		, 'saveItemService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_ARTIFACT_ACTION'
				, baseParams: baseParams
		})
		, 'deleteItemService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'DELETE_ARTIFACT_ACTION'
				, baseParams: baseParams
		})
	};
	// end services for main catalog list 
	
	// start services for item versions list
	c.singleItemServices = {
		'getVersionsService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_ARTIFACT_VERSIONS_ACTION'
			, baseParams: baseParams
		})
		, 'deleteVersionsService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'DELETE_ARTIFACT_VERSIONS_ACTION'
			, baseParams: baseParams
		})
		, 'downloadVersionService' : Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'DOWNLOAD_ARTIFACT_VERSION_ACTION'
			, baseParams: baseParams
		})
	};
	// end services for item versions list

	Sbi.tools.catalogue.MondrianSchemasCatalogue.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.tools.catalogue.MondrianSchemasCatalogue, Sbi.widgets.Catalogue, {
	
});
