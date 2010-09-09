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
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */
Ext.ns("Sbi.crosstab.core");

Sbi.crosstab.core.CrossTabCFWizard = function(level, horizontal) {

	this.initMainPanel();

	var c = {
			title: LN('sbi.crosstab.calculatefieldwizard.title'),
			layout: 'fit',
			width: 350,
			height: 250,
			items:[this.mainPanel],
		    buttons: [{
				text: LN('sbi.crosstab.calculatefieldwizard.ok'),
			    handler: function(){
		    		var expression= this.getExpression();
		    		var cfName= this.cfNameField.getValue()
		    		if(expression!=null && expression!="" && cfName!=null && cfName!="" && this.validate(false)){
		    			this.fireEvent('applyCalculatedField', this.activeLevel, this.horizontal, expression, cfName);
		    			this.close();
		    		}
	        	}
	        	, scope: this
		    }], 
		    tools: [{
	          id: 'help',
	          handler: function(event, toolEl, panel) {
	        	  var aWindow = new Ext.Window({
	        		  width: 300,
	        		  style: 'padding: 5px;',
	        		  items: [{
	        			  xtype: 'panel',
	        			  html: LN('sbi.crosstab.calculatefieldwizard.info')
	        		  }]
	        	  });
	        	  aWindow.show();
	          },
	          scope: this
		    }]
	};
	
	
	// constructor
	if(level!=null){
		this.activeLevel = level;
	}
	if(horizontal!=null){
		this.horizontal = horizontal;
	}
	
	Sbi.crosstab.core.CrossTabCFWizard.superclass.constructor.call(this, c);
	
};
	
Ext.extend(Sbi.crosstab.core.CrossTabCFWizard, Ext.Window, {
	textField: null
	,activeLevel: null
	,horizontal: null
	,cfNameField: null
	,mainPanel: null
	
	
	,addField: function(text, level, horizontal){
		if(this.activeLevel==null){
			this.activeLevel = level;
		}
		if(this.horizontal==null){
			this.horizontal = horizontal;
		}
		if(this.activeLevel != level || this.horizontal!=horizontal){
			return;
		}
		this.textField.insertAtCursor(text); 
	}


	,isActiveLevel: function(level, horizontal){
		if(this.activeLevel==null){
			return true;
		}else{
			return (this.activeLevel == level) && (this.horizontal == horizontal);
		}
	}
	
	, getExpression: function() {
		var expression;
		if(this.textField) {
	  		expression = this.textField.getValue();
	  		expression = Ext.util.Format.stripTags( expression );
	  		expression = expression.replace(/&nbsp;/g," ");
	  		expression = expression.replace(/\u200B/g,"");
	  		expression = expression.replace(/&gt;/g,">");
	  		expression = expression.replace(/&lt;/g,"<");
		}
		return expression;
	}

	,validate: function(showSuccess){
		var error = Sbi.crosstab.core.ArithmeticExpressionParser.module.validateCrossTabCalculatedField(this.getExpression());
		if(error==""){
			if(showSuccess){
				Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.qbe.calculatedFields.validationwindow.success.text'), LN('sbi.qbe.calculatedFields.validationwindow.success.title'));
			}
			return true;
		}else{
			Sbi.exception.ExceptionHandler.showWarningMessage(error, LN('sbi.qbe.calculatedFields.validationwindow.fail.title'));
			return false;
		}
	}
	
	
	
	,initTextField: function(){

		var buttonclear = new Ext.Button({
		    text: LN('sbi.crosstab.calculatefieldwizard.clear'),
		    icon: 'null',
		    iconCls:'remove'
		});
		buttonclear.addListener('click', function(){this.textField.reset();}, this);

		
		var buttonvalidate = new Ext.Button({
		    text: LN('sbi.crosstab.calculatefieldwizard.validate'),
		    icon: 'null',
		    iconCls:'option'
		});
		buttonvalidate.addListener('click', function(){this.validate(true);}, this);

	
		this.textField = new Ext.form.HtmlEditor({
    		name:'expression',
    	    enableAlignments : false,
    	    enableColors : false,
    	    enableFont :  false,
    	    enableFontSize : false, 
    	    enableFormat : false,
    	    enableLinks :  false,
    	    enableLists : false,
    	    enableSourceEdit : false,
    	    listeners:{
		    	'render': function(editor){
					var tb = editor.getToolbar();
					tb.add(buttonclear);
					tb.add(buttonvalidate);
		        },
    	        'initialize': {
		        	fn: function(){
						this.onFirstFocus();
	    	        } 
    	        } 
    	    }
    	});
	}
	
	, initMainPanel: function() {
		
		this.initTextField();
		
		this.cfNameField = new Ext.form.TextField({
			name:'name',
			allowBlank: false, 
			fieldLabel: 'Nome'
		});
		
		this.mainPanel = new Ext.Panel({
			layout: 'border',
		    items: [
		             new Ext.form.FormPanel({
		     	    	region:'north',
		     	    	height: 30,
		    		    border: true,
		    		    frame: false, 
			            items: [this.cfNameField],
			            bodyStyle: "background-color: transparent; border-color: transparent; padding-top: 2px; padding-left: 10px;"
			         }),
		             new Ext.Panel({
			            region:'center',
			            layout: "fit",
			    		border: true,
			    		frame: false, 
			            items: [this.textField]
			         })
		           ]
		 });
    }
});