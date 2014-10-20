/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Marco Cortella (marco.cortella@eng.it)
 */

Ext.define('Sbi.tools.hierarchieseditor.HierarchiesEditorContextMenu', {
	extend:'Ext.menu.Menu',
	itemId : 'idTreeContextMenu',
	items : [new Ext.Action({
	    text: 'Delete node',
	    handler: function(){
	    	var tree = Ext.getCmp('customTreePanel');
	    	var selectedNode = tree.selModel.getSelection()[0];
	    	//selectedNode.parentNode.removeChild(selectedNode,true);
	    	selectedNode.remove();
	        Ext.Msg.alert('Click', 'Node deleted');
	    },
	    iconCls: 'button-remove',
	    itemId: 'myAction'
	})]

});