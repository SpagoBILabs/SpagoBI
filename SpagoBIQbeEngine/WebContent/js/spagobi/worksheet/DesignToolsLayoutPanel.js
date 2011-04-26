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
 * Public Methods
 * 
 * getLayoutValue(): returns a string with the selected layout. 
 * 			Available values:
 * 				'layout-headerfooter' (default)
 * 				'layout-header'
 * 				'layout-footer'
 * 				'layout-content'
 * 
 * 
 * setLayoutValue(value): sets the layout value..
 * 			The available values are the same of getLayoutValue
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet");

Sbi.worksheet.DesignToolsLayoutPanel = function(config) { 

	this.layoutRadioGroup = new Ext.form.RadioGroup({
		hideLabel: true,
		columns: 2,
		items: [
		        {name: 'layout', height: 40, id:'layout-headerfooter', ctCls:'layout-headerfooter',inputValue: 'layout_headerfooter', checked: true},
		        {name: 'layout', height: 40, id:'layout-header', ctCls:'layout-header',inputValue: 'layout_header'},
		        {name: 'layout', height: 40, id:'layout-footer', ctCls:'layout-footer', inputValue: 'layout_footer'},
		        {name: 'layout', height: 40, id:'layout-content', ctCls:'layout-content', inputValue: 'layout_content'},
		        ]
	});

	var conf ={
			title:  LN('sbi.worksheet.designtoolslayoutpanel.title'),
			border: false,
			bodyStyle: 'padding-top: 15px; padding-left: 15px',
			items: [this.layoutRadioGroup]
	};

	Sbi.worksheet.DesignToolsLayoutPanel.superclass.constructor.call(this, conf);	

	this.on('afterLayout',this.addToolTips,this);


};

Ext.extend(Sbi.worksheet.DesignToolsLayoutPanel, Ext.FormPanel, {
	layoutRadioGroup: null,

	addToolTips: function(){

		var sharedConf ={anchor: 'top',width:200,trackMouse:true};

		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-layout-headerfooter',
			html: LN('sbi.worksheet.designtoolslayoutpanel.tooltip.headerfooter'),
		},sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-layout-header',
			html: LN('sbi.worksheet.designtoolslayoutpanel.tooltip.header')
		},sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-layout-footer',
			html: LN('sbi.worksheet.designtoolslayoutpanel.tooltip.footer')
		},sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-layout-content',
			html: LN('sbi.worksheet.designtoolslayoutpanel.tooltip.content')
		},sharedConf));
		this.on('afterLayout',this.addToolTips,this);
	},

	//returns a string with the selected layout (for the available values look the..
	//.. class comment)
	getLayoutValue: function(){
		if(this.layoutRadioGroup!=null && this.layoutRadioGroup.getValue()!=null && this.layoutRadioGroup.getValue().getGroupValue()!=null){
			return this.layoutRadioGroup.getValue().getGroupValue();
		}else{
			this.layoutRadioGroup.setValue('layout-headerfooter');
			return 'layout-headerfooter';
		}
	},

	//set the layout (for the available values look the..
	//.. class comment)
	setLayoutValue: function(value){
		this.layoutRadioGroup.setValue(value);
	}


});