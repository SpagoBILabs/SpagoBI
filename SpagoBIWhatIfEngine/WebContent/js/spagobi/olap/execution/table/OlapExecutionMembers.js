/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * container of the columns definition of the pivot table
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */




Ext.define('Sbi.olap.execution.table.OlapExecutionMembers', {
	extend: 'Ext.panel.Panel',
			
	config:{
		/**
	     * @cfg {Ext.data.Store} store
	     * The store with the Sbi.olap.execution.table.OlapExecutionMember
	     */
		store: Ext.create('Ext.data.Store', {
		    model: 'Sbi.olap.MemberModel'
		}),
		/**
	     * @cfg {Sbi.olap.execution.table.OlapExecutionPivot} pivotContainer
	     * The container of the columns
	     */
		pivotContainer: null,
		/**
	     * @cfg {String} memberClassName
	     * The name of the children classes
	     */
		memberClassName: null
//		,style: {
//			backgroundColor: "transparent",
//			border: "none"
//		},
//		bodyStyle: {
//			backgroundColor: "transparent"
//		},
	    //cls: "empty-member"
    },
	
	initComponent: function() {
		
		var items = new Array();
		
		if(this.store && this.store.getCount()>0){
			var membersCount = this.store.getCount( );
			for(var i=0; i<membersCount; i++) {
				items.push(Ext.create(this.memberClassName,{member: this.store.getAt(i), pivotContainer: this.pivotContainer, containerPanel: this}));
			}
			Ext.apply(this, {items: items});
//			this.removeCls("empty-member");
		}
		Ext.apply(this, {frame: true});
		this.callParent();
	},
	
    /**
     * Adds the member from in member container
     * @param {Sbi.olap.execution.table.OlapExecutionMember} member the member to add
     */
	addMember: function(member){
//		if(this.store.getCount()==0){
//			this.removeCls("empty-member");
//		}
		this.store.add(member.member);
		this.refreshItems();
	},
	
    /**
     * Removes the member from the member container
     * @param {Sbi.olap.execution.table.OlapExecutionMember} member the member to remove
     */
	removeMember: function(member){
		this.store.remove(member.member);
		this.remove(member, true);
//		if(this.store.getCount()==0){
//			this.addCls("empty-member");
//		}
	},
	
    /**
     * refreshes the list of items. It removes all the items from the container and rebuild them from the store
     */
	refreshItems: function(){
		this.removeAll();
		
		if(this.store){
			var membersCount = this.store.getCount( );
			for(var i=0; i<membersCount; i++) {
				this.add(Ext.create(this.memberClassName,{member: this.store.getAt(i), pivotContainer: this.pivotContainer, containerPanel: this}));
			}
		}
	}
});





