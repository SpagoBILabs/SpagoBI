/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The row member
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionRow', {
	extend: 'Sbi.olap.execution.table.OlapExecutionMember',
	
	config:{
		cls: "x-column-header rotate",
		bodyStyle: "background-color: transparent",
		style: "margin-bottom: 3px;"
	},
	
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionRow) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionRow);
		}
		//this.height= this.member.get("name").length*12+4;
		this.callParent(arguments);
		this.on("render",function(){this.setHeight(this.member.get("name").length*6.3+12);},this);
	}
	
	
});