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
 
  
Ext.define('Sbi.social.analysis.search.store.RepeatTypeStore', {
	
	extend: 'Ext.data.Store',
	fields: ['name', 'type'],
    data : [
        {"name": LN('sbi.social.analysis.day'),  "type": 'Day'},
        {"name": LN('sbi.social.analysis.hour'), "type": 'Hour'}

    ]           
	
});