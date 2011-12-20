/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * Authors
 * 		Alberto Ghedin  (alberto.ghedin@eng.it)
 * 		Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetTitlePanel = function(config) { 
	
	var defaultSettings = {		
		border: false,
		frame:true,
		style:'padding:5px 15px 2px 15px',  
		fileUpload: true,
		height: 100
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetTitlePanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetTitlePanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	Ext.QuickTips.init();  // enable tool tips

	var formItems = [];
	var formRows = 0;	
	
	// services
	this.services = this.services || new Array();
	// there also the upload service, but it cannot be built with Sbi.config.serviceRegistry.getServiceUrl,
	// since parameters (ACTION_NAME, ...) cannot be put on the service url, but they must be POST parameters 
	
	//row of the title
	this.addEvents();
	formItems = this.addTitle(formItems);
	formItems = this.addImage(formItems);
	
	c = {
        items: [{
            layout:'column',
            items: formItems
        }]
	};

	Sbi.worksheet.designer.SheetTitlePanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.SheetTitlePanel, Ext.FormPanel, {
	loadImageCombo: null,
	loadImageFileBrows: null,
	titlePanel: null,
	imgTriggerField: null,
	imgFile: null,
	imgPosition: null,
	chooseImageWindow: null,
	
	
	//add the title row in the items of the panel
	addTitle:function(items){
		
		this.titlePanel =
			new Ext.form.HtmlEditor({
            	columnWidth:		.6,
            	anchor:				'95%',
                height: 			75,
        	    enableLinks :  		false,
        	    enableLists :		false,
        	    enableSourceEdit : 	false
            });
		items.push(this.titlePanel);
		return items;
	},
	
	//add the image row in the items of the panel
	addImage: function(items){
		
		var imgTriggerFieldId = Ext.id();
		var imgPositionId = Ext.id();
		
		this.imgTriggerField = new Ext.form.TwinTriggerField({
			name : 'img'
			, id: imgTriggerFieldId
			, fieldLabel : LN('sbi.worksheet.designer.title.image')
			, triggerClass : 'x-form-search-trigger'
			, editable : false
			, allowBlank : true
			, trigger1Class:'x-form-search-trigger'
			, trigger2Class:'x-form-clear-trigger'
			, onTrigger1Click: this.imgTriggerFieldHandler.createDelegate(this)
			, onTrigger2Click: this.cleanTriggerFieldHandler.createDelegate(this)
			, scope: this
			, anchor:'95%'
		});	
		
		//Combo box with positions
		this.imgPosition = new Ext.form.ComboBox({
			id:				imgPositionId,
			xtype:          'combo',
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			fieldLabel:     LN('sbi.worksheet.designer.title.position'),
			name:           'position',
			displayField:   'position',
			valueField:     'value',
			anchor:			'95%',
			store:          new Ext.data.JsonStore({
				fields : ['position', 'value'],
				data   : this.getAvailablePositions()
			})
		});
	
		this.on('afterrender',this.addToolTips.createDelegate(this, [imgTriggerFieldId, imgPositionId]),this);
		
		items.push({
			columnWidth:.4,
			style:'padding-left: 10px; padding-top: 7px;',
			layout: 'form',
			items: [this.imgTriggerField,this.imgPosition]
		});
		
		return items;
	},
	
	//returns the array with the available positions for the image 
	getAvailablePositions:function(){
		var array = [];	
		array.push({value: 'left', position:  LN('sbi.worksheet.designer.title.position.left')});
		array.push({value: 'center', position:  LN('sbi.worksheet.designer.title.position.center')});
		array.push({value: 'right', position:  LN('sbi.worksheet.designer.title.position.right')});
		return array;
	},
	
	imgTriggerFieldHandler: function() {
		if (this.chooseImageWindow == null) {
			this.chooseImageWindow = new Sbi.worksheet.designer.ChooseImageWindow({
				width: 450
				, height: 400
				, closeAction: 'hide'
			});
			this.chooseImageWindow.on('select', function(theChooseImageWindow, fileName) {
				this.imgTriggerField.setValue(fileName);
				this.chooseImageWindow.hide();
			}, this);
		}
		this.chooseImageWindow.show();
	},
	
	cleanTriggerFieldHandler: function() {
		this.imgTriggerField.setValue('');
	},
	
	addToolTips: function(imgTriggerFieldId, imgPositionId) {
		var sharedConf ={anchor: 'top',width:200,trackMouse:true};

		new Ext.ToolTip(Ext.apply({
			target: imgTriggerFieldId,
			html: LN('sbi.worksheet.designer.title.imgTriggerFieldId')
		},sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: imgPositionId,
			html: LN('sbi.worksheet.designer.title.imgPositionId')
		},sharedConf));
	},
	

	
	getTitleState: function(messageBox){
		var values={};
		var titleToNormalize =  this.titlePanel.getValue();

		if(titleToNormalize!=undefined && titleToNormalize!=null ){
			titleToNormalize = titleToNormalize.replace(/&nbsp;/g," ");
			titleToNormalize = titleToNormalize.replace(/\u200B/g,"");
			titleToNormalize = titleToNormalize.replace(/&gt;/g,">");
			values.title = titleToNormalize.replace(/&lt;/g,"<");
		}else{
			values.title='';
		}
		
		values.img =  this.imgTriggerField.getValue();
		if(values.img==''){
			values.img =null;
		}
		values.position = this.imgPosition.getValue();
		if(values.position==undefined  || values.position==null){
			values.position ='';
		}
		return values;
	},
	
	setTitleState: function(values){
		if(values!=undefined && values!=null){
			if(values.title!=undefined && values.title!=null){
				this.titlePanel.setValue(values.title);
			}
			if(values.img!=undefined && values.img!=null){
				this.imgTriggerField.setValue(values.img);
			}
			if(values.position!=undefined && values.position!=null){
				this.imgPosition.setValue(values.position);
			}
		}
	}
	
});