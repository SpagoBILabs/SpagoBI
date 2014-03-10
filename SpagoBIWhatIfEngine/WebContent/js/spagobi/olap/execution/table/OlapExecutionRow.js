/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The row hierarchy
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionRow', {
	extend: 'Sbi.olap.execution.table.OlapExecutionAxisHierarchy',
	
	config:{
		style: "margin-bottom: 3px;",
		cls: "x-column-header",
   		bodyStyle: "background-color: transparent",
   		roundText: true
	},
	
	subPanelLayout: "auto",
	
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionRow) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionRow);
		}
		this.roundText = this.roundText && (Ext.isChrome);
		this.callParent(arguments);
	},
	
	
    /**
     * Builds the central panel with the name of the hierarchy
     */
	buildHierarchyPanel: function(){
		var hierarchyConf =  {
				xtype: "panel",
				border: false,
		   		
		   		bodyStyle: "background-color: transparent; white-space: nowrap",
		   		style: "background-color: transparent",
		    	html: this.getText()
			};
		if(this.roundText){
			hierarchyConf.height = this.getHierarchyName().length*6.3+4;
			hierarchyConf.cls= "rotate";
		}
		
		
		this.hierarchyPanel = Ext.create("Ext.Panel",hierarchyConf);
	},
	
    /**
     * Gets the text to show in the panel
     * If the browser is IE the text is from left to right and a <br> follow every char.
     * If the browser is not IE the text is rounded via CSS
     */
	getText: function(){
		if(!this.roundText){
			var text ="";
			var n = this.getHierarchyName();
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
			return this.getHierarchyName();
		}
	},

	
	/**
	 * Builds the central panel with the name of the hierarchy
	 */
	buildUpPanelConf: function(){
		var conf = this.callParent();
		return Ext.apply(conf,{height: 13, cls: 'up-arrow'});
	},
	
	/**
	 * Builds the central panel with the name of the hierarchy
	 */
	buildDownPanelConf: function(){
		var conf = this.callParent();
		return Ext.apply(conf,{height: 10, cls: 'down-arrow'});
	}
	
});