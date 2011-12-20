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
  * SaveAnalysisWindow - short description
  * 
  * Object documentation ...
  * 
  * by Andrea Gioia (andrea.gioia@eng.it)
  */

Sbi.geo.SaveAnalysisWindow = function(config) {	
	
	this.saveServiceSequence = undefined;
	this.buddy = undefined;
	
	//this.addEvents();
	
	Ext.apply(this, config);
	
	
	this.analysisName = new Ext.form.TextField({
		id:'analysisName',
		name:'analysisName',
		allowBlank:false, 
		inputType:'text',
		maxLength:50,
		width:250,
		fieldLabel:'Name' 
	});
	    
	this.analysisDescription = new Ext.form.TextField({
		id:'analysisDescription',
		name:'analysisDescription',
		allowBlank:false, 
		inputType:'text',
		maxLength:50,
		width:250,
		fieldLabel:'Description' 
	});
	    
	    
	   
	var scopeComboBoxData = [
		['PUBLIC','Public', 'Everybody that can execute this document will see also your saved subobject'],
		['PRIVATE', 'Private', 'The saved quary will be visible only to you']
	];
		
	var scopeComboBoxStore = new Ext.data.SimpleStore({
		fields: ['value', 'field', 'description'],
		data : scopeComboBoxData 
	});
		    
		    
	this.analysisScope = new Ext.form.ComboBox({
	   	tpl: '<tpl for="."><div ext:qtip="{field}: {description}" class="x-combo-list-item">{field}</div></tpl>',	
	   	editable  : false,
	   	fieldLabel : 'Scope',
	   	forceSelection : true,
	   	mode : 'local',
	   	name : 'analysisScope',
	   	store : scopeComboBoxStore,
	   	displayField:'field',
	    valueField:'value',
	    emptyText:'Select scope...',
	    typeAhead: true,
	    triggerAction: 'all',
	    selectOnFocus:true
	});
	
	var formPanel = new Ext.form.FormPanel({
		frame:true,
	    bodyStyle:'padding:5px 5px 0',
	    buttonAlign : 'center',
	    items: [this.analysisName,this.analysisDescription,this.analysisScope],
	    buttons: [{
			text: 'Save',
		    handler: function(){
            	this.hide();
            	if(this.saveServiceSequence === undefined) {
            		alert('Error: saveServiceSequence is undefined' );
            	} else {
            	
            		this.saveServiceSequence.run();
            	}
        	}
        	, scope: this
	    },{
		    text: 'Cancel',
		    handler: function(){
            	this.hide();
        	}
        	, scope: this
		}]
	 });
	
	// constructor
    Sbi.geo.SaveAnalysisWindow.superclass.constructor.call(this, {
    	id:'id1',
		layout:'fit',
		width:500,
		height:300,
		closeAction:'hide',
		plain: true,
		title: 'Save as ...',
		items: [formPanel]
    });
    
    if(this.buddy === undefined) {
    	this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
    }
    
};

Ext.extend(Sbi.geo.SaveAnalysisWindow, Ext.Window, {
    
    // static contens and methods definitions
   
   
    // public methods
	getAnalysisMeta : function() {
          	
      	var analysisMeta = {};
      	analysisMeta.analysisName= this.analysisName.getValue();
	    analysisMeta.analysisDescription= this.analysisDescription.getValue();
	    analysisMeta.analysisScope= this.analysisScope.getValue();
      	
      	return analysisMeta;
    }

	,setAnalysisMeta : function(analysisMeta) {            
		if(analysisMeta.analysisName !== undefined) this.analysisName.setValue(analysisMeta.analysisName);
		if(analysisMeta.analysisDescription !== undefined) this.analysisDescription.setValue(analysisMeta.analysisDescription);
		if(analysisMeta.analysisScope !== undefined) this.analysisScope.setValue(analysisMeta.analysisScope.toUpperCase());
	}
});