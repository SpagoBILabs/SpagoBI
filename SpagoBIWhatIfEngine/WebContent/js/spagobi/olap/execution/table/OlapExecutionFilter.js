/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The filter member..
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionFilter', {
	extend: 'Sbi.olap.execution.table.OlapExecutionMember',
	layout: "border",

	config:{
		/**
		 * @cfg {Sbi.olap.MemberModel} selectedMember
		 * The value of the filter
		 */
		selectedMember: null,
		/**
		 * @cfg {int} hierarchyMaxtextLength
		 * The max length of the text.. If the text is longer we cut it and add 2 dots
		 */
		hierarchyMaxtextLength: 17,
		/**
		 * @cfg {int} memberMaxtextLength
		 * The max length of the text.. If the text is longer we cut it and add 2 dots
		 */
		memberMaxtextLength: 17,
		/**
		 * @cfg {int} width
		 * The width of the filter
		 */
		width: 120,
		cls: "x-column-header",
		bodyStyle: "background-color: transparent;",
		style: "margin-right: 3px; padding: 0px;"
	},

	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionFilter) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionFilter);
		}

		this.selectedvalue = Ext.create("Ext.Panel",{flex: 1,html:"...", border: false, bodyStyle: "background-color: transparent;padding: 4px; text-align: center;"});

		this.callParent(arguments);
	},


	initComponent: function() {

		var name = this.getMemberName();

		if(name.length>this.hierarchyMaxtextLength){
			name = name.substring(0,this.hierarchyMaxtextLength-2)+"..";
		}
		var thisPanel = this;
		Ext.apply(this, {

			items: [{
				region: 'north',
				flex: 1,
				html: name,
				border: true,
				bodyCls: "filter-title "
			}, {
				region: 'center',
				style: 'height: "100%"; background-color: transparent; margin: 4px',
				bodyStyle: 'background-color: transparent;',

				layout: {
					type: 'hbox',
					align:'stretch'
				},
				defaults: {
					style: "background-color: transparent;"
				},
				border: false,
				items:[
				       this.selectedvalue,
				       {
				    	   width:20, 
				    	   html:" ", 
				    	   cls:"filter-funnel-image",
				    	   border: true, 
				    	   bodyCls: "filter-funnel-body",

				    	   listeners: {
				    		   el: {
				    			   click: {
				    				   fn: function (event, html, eOpts) {
				    					   var win =   Ext.create("Sbi.olap.execution.table.OlapExecutionFilterTree",{
				    						   hierarchy: thisPanel.member,
				    						   selectedMember: this.selectedMember
				    					   });
				    					   win.show();
				    					   win.on("select", function(member){
				    						   this.setFilterValue(member);
				    					   },this);
				    				   },
				    				   scope: this
				    			   }
				    		   }
				    	   }
				       }
				       ]
			}],
			frame: true,

		});
		this.callParent();
	},

    /**
     * Sets the value of the filter
     * @param {Sbi.olap.MemberModel} member the value of the filter
     */
	setFilterValue: function(member){
		this.selectedMember = member;
		//updates the text
		var name =  this.selectedMember.raw.name;
		if(name.length>this.memberMaxtextLength){
			name = name.substring(0,this.memberMaxtextLength-2)+"..";
		}
		this.selectedvalue.update(name);
	}



});