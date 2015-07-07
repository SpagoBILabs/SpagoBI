/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**     
 * @author
 * Lazar Kostic (lazar.kostic@mht.net)
 */

Ext.define('Sbi.behavioural.analyticalDriver.AnalyticalDriverModel',{

	extend: 'Ext.data.Model',
	fields: [

	         "ID",
	         "LABEL",
	         "DESCRIPTION",
	         "NAME",
	         "FUNCTIONALFLAG",
	         "TEMPORALFLAG",
	         "INPUTTYPECD"


	         ],

	         idProperty: "ID",

	         proxy:{

	        	 type: 'rest',
	        	 url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'analyticalDriver'}),
	        	 appendId: false,
	        	 reader: {

	        		 type: "json",
	        		 root: "root"

	        	 }
	         }

});