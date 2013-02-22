/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

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
Ext.ns("Sbi.widgets");

Sbi.widgets.TreeLookUpField = function(config) {

	this.rootConfig = {
		text : 'root',
		triggerClass : 'tree-look-up',
		expanded : true,
		id : 'lovroot___SEPA__0'
	};

	Ext.apply(this, config);
	this.initWin();

	var c = Ext.apply({}, config, {
		triggerClass : 'tree-look-up',
		enableKeyEvents : true,
		width : 150
	// , readOnly: true
	});

	// constructor
	Sbi.widgets.TreeLookUpField.superclass.constructor.call(this, c);

	this.on("render", function(field) {
		field.trigger.on("click", function(e) {
			if (!this.disabled) {
				this.onLookUp();
			}
		}, this);
	}, this);

	this.on("render", function(field) {
		field.el.on("keyup", function(e) {
			this.xdirty = true;
		}, this);
	}, this);

	this.addEvents('select');

};

Ext.extend(Sbi.widgets.TreeLookUpField, Ext.form.TriggerField, {

	win : null,
	xvalues : new Array()

	,
	initWin : function() {
		var thisPanel = this;
		this.treeLoader = new Ext.tree.TreeLoader({
			dataUrl : this.service,
			baseParams : this.params,
			createNode : function(attr) {
				attr.text = attr.description;

				if (attr.leaf && thisPanel.multivalue) {
					attr.iconCls = 'parameter-leaf';
					if (thisPanel.xvalues
							&& thisPanel.xvalues.indexOf(attr.value) >= 0) {
						attr.checked = true;
					} else {
						attr.checked = false;
					}

				}
				var node = Ext.tree.TreeLoader.prototype.createNode.call(this,
						attr);
//				if (attr.checked) {
//					node.on('append',
//							function(tree, thisNode, childNode, index) {
//								thisNode.getUI().toggleCheck(false);
//							}, this);
//				}
				
				if (!thisPanel.multivalue && attr.leaf ) {
					node.on('click',
							function(node, e) {
								thisPanel.onOkSingleValue(node);
							}, this);
				}

				return node;
			}
		});

		this.tree = new Ext.tree.TreePanel({
			width : 200,
			autoScroll : true,
			rootVisible : false,
			loader : this.treeLoader,
			root : new Ext.tree.AsyncTreeNode(this.rootConfig)
		// , rootVisible: false
		});

		this.win = new Ext.Window({
			title : LN('sbi.lookup.Select'),
			layout : 'fit',
			width : 580,
			height : 300,
			closeAction : 'hide',
			plain : true,
			items : [ this.tree ],
			buttons : [ {
				text : LN('sbi.lookup.Annulla'),
				listeners : {
					'click' : {
						fn : this.onCancel,
						scope : this
					}
				}
			}, {
				text : LN('sbi.lookup.Confirm'),
				listeners : {
					'click' : {
						fn : this.onOk,
						scope : this
					}
				}
			} ]
		});

	}

	,
	onLookUp : function() {
		this.fireEvent('lookup', this);
		this.win.show(this);
	}

	,
	onOk : function() {
		var d = this.getCheckedValue();
		this.setValue(d);
		var v = this.getCheckedDescription();
		this.setRawValue(v);
		this.fireEvent('select', this, v);
		this.win.hide();
	}

	,
	onOkSingleValue : function(value) {

		this.setValue(value.attributes.value);
		this.setRawValue(value.attributes.description);
		this.fireEvent('select', this, value.attributes.value);
		this.win.hide();
	}
	
	,
	onCancel : function() {
		this.win.hide();
	}

	,
	setValue : function(values) {
		var pvalues = "";

		if (values) {
			if(values instanceof Array) {
				for ( var i = 0; i < values.length; i++) {
					pvalues = pvalues + ";" + (values[i]);
				}
				pvalues = pvalues.substring(1);
			}else{
				pvalues = values;
				values = values.split(";");
			}
			Sbi.widgets.LookupField.superclass.setValue.call( this, pvalues);
		}
		this.xvalues = values;

	}

	,
	setRawValue : function(values) {
		var pvalues = "";
		

		if (values) {
			if(values instanceof Array) {
				for ( var i = 0; i < values.length; i++) {
					pvalues = pvalues + ";" + (values[i]);
				}
				pvalues = pvalues.substring(1);
			}else{
				pvalues = values;
				values = values.split(";");
			}
			Sbi.widgets.LookupField.superclass.setRawValue.call( this, pvalues);
		}
		this.xvalues = values;

	}

	,
	getCheckedValue : function() {
		var checked = this.tree.getChecked();
		var values = [];
		if (checked) {
			for ( var i = 0; i < checked.length; i++) {
				values.push(checked[i].attributes.value);
			}
		}
		this.xvalues = values;
		return values;
	}
	
	,
	getCheckedDescription : function() {
		var checked = this.tree.getChecked();
		var descriptions = [];
		if (checked) {
			for ( var i = 0; i < checked.length; i++) {
				descriptions.push(checked[i].attributes.description);
			}
		}
		return descriptions;
	}

	,
	getValue : function() {
		var v = Sbi.widgets.LookupField.superclass.getValue.call( this);
		var values = [];
		if(v){
			values = v.split(";");
		}
		this.xvalues = values;
		return this.xvalues;
	}



	// if the parameters has been change we reload the tree
	,
	reloadTree : function(formParams) {
		if (formParams && formParams != this.oldFormParams) {
			if(formParams && formParams!=this.oldFormParams){
				this.params.PARAMETERS =  formParams;
				this.treeLoader.baseParams =this.params;
				var newRoot = new Ext.tree.AsyncTreeNode(this.rootConfig);
				this.tree.setRootNode(newRoot);
			}

		}
	}

});