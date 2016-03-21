/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Store for a Search Model
 *
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */
 
  
Ext.define('Sbi.social.analysis.search.store.StreamingSearchStore', {
	
	extend: 'Ext.data.Store',
    model: 'Sbi.social.analysis.search.model.StreamingSearchModel',
    autoLoad: true
//    data: [
//           { id: '1000',    label: 'Test1', keywords: 'spagobi, opensource', lastActivation: '01/01/2014', accounts: '@themonkey86', links: 'bitly.link' }
//       ]            
	
});