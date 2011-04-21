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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.adhoc");

Sbi.adhoc.SheetTitlePanel = function(config) { 

	var formItems = [];
	var formRows = 0;
	
	
	if(config.title){
		formRows++;
		formItems = this.addTitle(formItems);
	}
	
	if(config.img){
		formRows++;
		formItems = this.addImage(formItems);
	}
	
	var c ={
		border: false,
		frame:true,
		style:'padding:5px 15px 0',  
		fileUpload: true,
		height: 10+formRows*20,
		defaults: {
			anchor: '95%',
		},
        items: [{
            layout:'column',
            items: formItems
        }]
	}
	Sbi.adhoc.SheetTitlePanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.adhoc.SheetTitlePanel, Ext.FormPanel, {
	
	addTitle:function(items){
		items.push({
			columnWidth:.7,
			layout: 'form',
			items: [{
				xtype:		'textfield',
				fieldLabel: 'Titolo',
				name: 		'first',
				anchor:		'95%'
			}]
		});
		items.push({
			columnWidth:.3,
			layout: 'form',
			items: [{
				xtype:          'combo',
				mode:           'local',
				triggerAction:  'all',
				forceSelection: true,
				editable:       false,
				fieldLabel:     'Size',
				name:           'size',
				displayField:   'size',
				valueField:     'size',
				anchor:			'95%',
				store:          new Ext.data.JsonStore({
					fields : ['size'],
					data   : this.getAvailableSizes(10,50,2)
				})
			}]
		});
		return items;
	},
	
	addImage: function(items){
		items.push({
			columnWidth:.5,
			layout: 'form',
			items: [{xtype: 'textfield',
				inputType:	'file',
				fieldLabel: 'Immagine',
				anchor:		'100%'
			}]
		});
		items.push({
			columnWidth:.5,
			layout: 'form',
			items: [{
				xtype:          'combo',
				mode:           'local',
				triggerAction:  'all',
				forceSelection: true,
				editable:       false,
				fieldLabel:     'Position',
				name:           'position',
				displayField:   'position',
				valueField:     'position',
				anchor:			'95%',
				store:          new Ext.data.JsonStore({
					fields : ['position'],
					data   : this.getAvailablePositions()
				})
			}]
		});
		return items;
	},
	
	getAvailablePositions:function(min,max,step){
		var array = [];
		array.push({position: 'left'});
		array.push({position: 'top'});
		array.push({position: 'right'});
		array.push({position: 'bottom'});	
		return array;
	},
	
	getAvailableSizes:function(min,max,step){
		var array = [];
		var i=min;
		while(i<=max){
			array.push({size: ''+i});
			i=i+step;
		}
		return array;
	}
});