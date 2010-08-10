/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.widgets");

Sbi.widgets.GenericDropTarget = function(targetPanel, config) {
	
	var c = Ext.apply({
		//ddGroup must be provided by input config object!!
		copy       : false
	}, config || {});
	
	Ext.apply(this, c);
	
	this.targetPanel = targetPanel;
	
	// constructor
    Sbi.widgets.GenericDropTarget.superclass.constructor.call(this, this.targetPanel.getEl(), c);
};

Ext.extend(Sbi.widgets.GenericDropTarget, Ext.dd.DropTarget, {
    
	targetPanel: null
	
	/*
    , notifyOver : function(ddSource, e, data) {
		return this.dropAllowed;
	}
	*/
	
	, notifyDrop : function(ddSource, e, data) {
		if (this.onFieldDrop) {
			this.onFieldDrop.call(this.targetPanel, ddSource);
		}
	}
	
});