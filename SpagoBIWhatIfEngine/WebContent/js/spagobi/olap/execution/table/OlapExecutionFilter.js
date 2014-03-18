/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The filter member..
 * The panel contains 3 subpanels:
 * <ul>
 * <li>a panel with the name of the hierarchy: with the text of the filter</li>
 * <li>selectedValuePanel: with the text of the filter</li>
 * <li>a panel with the funnel iconr</li>
 * </ul>
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionFilter', {
	extend: 'Sbi.olap.execution.table.OlapExecutionHierarchy',
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
		/**
		 * @cfg {boolean} multiSelection
		 * true to allow the multi selection of member, false otherwise
		 */
		multiSelection: false,
		cls: "x-column-header",
		bodyStyle: "background-color: transparent;",
		style: "margin-right: 3px; padding: 0px;"
	},

	/**
     * @property {Ext.Panel} selectedValuePanel
     *  Panel with the selected value of the filter
     */
	selectedValuePanel: null,
	
	/**
     * @property {Ext.Panel} titlePanel
     *  Panel with the hierarchy name
     */
	titlePanel: null,
	
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionFilter) {
			Ext.apply(this, Sbi.settings.olap.execution.OlapExecutionFilter);
		}

		this.selectedValuePanel = Ext.create("Ext.Panel",{
			flex: 1,
			html:"...", 
			border: false, 
			bodyCls: "filter-value"}
		);
		
		this.titlePanel = Ext.create("Ext.Panel",{
			region: 'north',
			flex: 1,
			html: "",
			border: true,
			bodyCls: "filter-title "
		});

		this.callParent(arguments);

		this.initTitlePanel();
		this.initValuePanel();
	},

	
	
	

	initComponent: function() {

		
		var thisPanel = this;
		Ext.apply(this, {

			items: [this.titlePanel , {
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
				       this.selectedValuePanel,
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
				    						   hierarchy: thisPanel.hierarchy,
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
			frame: true

		});
		this.callParent();
	},
	
	/**
	 * @private
	 * Initializes the panel with the name of the hierarchy 
	 */
	initTitlePanel: function(){
		
		//get the name of the hierarchy
		var hierarchyName = this.getHierarchyName();
		var hierarchyNameTooltip = this.getHierarchyName();
		
		//add the ellipses if the text is to long
		if(hierarchyName.length>this.hierarchyMaxtextLength){
			hierarchyName = hierarchyName.substring(0,this.hierarchyMaxtextLength-2)+"..";
		}
		this.titlePanel.html= hierarchyName;
				
		//creates the tooltip
		this.titlePanel.on("render", function(){
			 Ext.create('Ext.tip.ToolTip', {
				    target: this.titlePanel.el,
				    html: hierarchyNameTooltip
				});
		},this);
		
	},

	/**
	 * @private
	 * Initializes the panel with the value of the slicer 
	 */
	initValuePanel: function(){
		
					
		//get the slicers
		var slicers = this.hierarchy.get("slicers");
		if(slicers){
			
			//creates the tooltip
			var hierarchyValueTooltip="";
			for(var i=0; i<slicers.length; i++){
				hierarchyValueTooltip = hierarchyValueTooltip+", "+slicers[i].name;
			}
			hierarchyValueTooltip = hierarchyValueTooltip.substring(2);
			
			//creates the value
			var slicersValue = hierarchyValueTooltip;
			
			//add the ellipses if the text is to long
			if(slicersValue.length>this.hierarchyMaxtextLength){
				slicersValue = slicersValue.substring(0,this.hierarchyMaxtextLength-2)+"..";
			}
			
			this.selectedValuePanel.html=slicersValue;
			
			//add the tooltip
			this.selectedValuePanel.on("render", function(){
				 Ext.create('Ext.tip.ToolTip', {
					    target: this.selectedValuePanel.el,
					    html: hierarchyValueTooltip
					});
			},this);
			
			//if there is a slicer initialize the local variable this.selectedMember
			var selected =  Ext.create(Ext.ModelMgr.getModel('Sbi.olap.MemberModel'),slicers[0] );
			this.selectedMember = selected;
			
		}

		
	},
	
    /**
     * Sets the value of the filter
     * @param {Sbi.olap.MemberModel} member the value of the filter
     */
	setFilterValue: function(member){
		var isChanged = false;
		if(member && member.raw){
			if(this.selectedMember){
				isChanged = (this.selectedMember.raw.uniqueName != member.raw.uniqueName);
			}else{
				isChanged=true;
			}
			
			this.selectedMember = member;
			//updates the text
			var name =  this.selectedMember.raw.name;
			if(name.length>this.memberMaxtextLength){
				name = name.substring(0,this.memberMaxtextLength-2)+"..";
			}
			this.selectedValuePanel.update(name);
		}
		if(isChanged){
			Sbi.olap.eventManager.addSlicer(this.hierarchy, this.selectedMember, this.multiSelection);
		}
	}



});