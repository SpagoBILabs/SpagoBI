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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.ChartCategoryPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.chartcategorypanel.title')
		, frame: true
		, emptyMsg: LN('sbi.worksheet.designer.chartcategorypanel.emptymsg')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.chartCategoryPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.chartCategoryPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		items: [this.emptyMsgPanel]
	});

	// constructor	
	Sbi.worksheet.designer.ChartCategoryPanel.superclass.constructor.call(this, c);
	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.designer.ChartCategoryPanel, Ext.Panel, {
	
	emptyMsg : null
	, emptyMsgPanel : null
    , category : null
	, content : null
	
	, init: function() {
		this.initEmptyMsgPanel();
		this.content = this.emptyMsgPanel;
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: this.ddGroup
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {

		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'queryFieldsPanel') {
			this.notifyDropFromQueryFieldsPanel(ddSource);
		} else {
			Ext.Msg.show({
			   title: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.title'),
			   msg: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.unknownsource'),
			   buttons: Ext.Msg.OK,
			   icon: Ext.MessageBox.WARNING
			});
		}
	}
	
	, notifyDropFromQueryFieldsPanel: function(ddSource) {
		var rows = ddSource.dragData.selections;
		if (rows.length > 1) {
			Ext.Msg.show({
				   title:'Drop not allowed',
				   msg: 'You can move only one field at a time',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
		} else {
			var aRow = rows[0];
			// if the field is a measure show a warning
			if (aRow.data.nature === 'measure' || aRow.data.nature === 'mandatory_measure') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.measures'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.WARNING
				});
				return;
			}
			// if the field is a postLineCalculated show an error
			if (aRow.data.nature === 'postLineCalculated') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.designer.chartcategorypanel.cannotdrophere.postlinecalculated'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
				return;
			}
			this.setCategory(aRow.data);
		}

	}
	
	, setCategory: function (category) {
		this.category = category;
		this.content.destroy();
		var panel = this.createCategoryPanel();
		this.add(panel);
		this.content = panel;
		this.doLayout();
	}
	
	, getCategory: function () {
		return this.category;
	}
	
	, removeCategory: function() {
		this.category = null;
		this.content.destroy();
		this.initEmptyMsgPanel();
		this.add(this.emptyMsgPanel);
		this.content = this.emptyMsgPanel;
		this.doLayout();
	}
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, height: 40
		});
	}

	, createCategoryPanel: function() {
		
		var thePanel = new Ext.Panel({
   			html: '<div style="cursor: pointer;">' + this.category.alias + '</div>'
   		});
		
		thePanel.on('render', function(panel) {
			panel.getEl().on('dblclick', function() {
		     	var chooserWindow = new Sbi.worksheet.designer.AttributeValuesChooserWindow({
		     		attribute : this.category
		     	});
			}, this);
		}, this);
		
		var item = new Ext.Panel({
            layout: {
                type:'column'
            }
			, style:'padding:5px 5px 5px 5px'
       		, items: [
       		  thePanel
       		  , new Ext.Button({
       		    template: new Ext.Template(
       		         '<div class="smallBtn">',
       		             '<div class="delete-icon"></div>',
       		             '<div class="btnText"></div>',
       		         '</div>')
       		     , buttonSelector: '.delete-icon'
       		  	 , iconCls: 'delete-icon'
       		     , text: '&nbsp;&nbsp;&nbsp;&nbsp;'
       		     , handler: this.removeCategory
       		     , scope: this
       		})]
		});
		return item;
	}
    
});