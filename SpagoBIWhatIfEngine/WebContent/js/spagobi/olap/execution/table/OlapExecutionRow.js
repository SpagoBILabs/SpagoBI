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
		//The text is rounded so we must fix the height of the panel
		if(!Ext.isIE){
			this.on("render",function(){this.setHeight(this.member.get("name").length*6.3+12);},this);
		}
	},
	
    /**
     * Gets the text to show in the panel
     * If the browser is IE the text is from left to right and a <br> follow every char.
     * If the browser is not IE the text is rounded via CSS
     */
	getText: function(){
		if(Ext.isIE){
			var text ="";
			var n = this.member.get("name");
			if(n){
				for(var i=0; i<n.length; i++){
					text = text + n.charAt(i) +'<br>';
					if(i>10){
						text=text+"..";
						return text;
					}
				}
				if(text.length>0){
					text = text.substring(0,text.length-4);
				}
			}
			return text;
		}else{
			return this.member.get("name");
		}
	}
	
	
});