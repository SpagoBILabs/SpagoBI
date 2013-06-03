/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/**TODO anto: aggiornare documentazione!!!
 * 
 * Data view for a browser style. It define a layout and provides the stubs methods for the icons browser.
 * This methods should be overridden to define the logic. Only the methods onAddNewRow, onGridSelect and onCloneRow are implemented.
 * The configuration dataView must be passed to the object. It should contains the method setFormState (it is used by onGridSelect)
 * 
 * 
 * 		@example
 *	Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
 *   extend: 'Sbi.widgets.compositepannel.ListDetailPanel'
 *	, constructor: function(config) {
 *		//init services
 *		this.initServices();
 *		//init form
 *		this.form =  Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services: this.services});
 *		this.columns = [{dataIndex:"DATASOURCE_LABEL", header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
 *		this.fields = ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
 *		this.form.on("save",this.onFormSave,this);
 *    	this.callParent(arguments);
 *    }
 *	, initServices: function(baseParams){
 *		this.services["getAllValues"]= Sbi.config.serviceRegistry.getRestServiceUrl({
 *			    							serviceName: 'datasources/listall'
 *			    							, baseParams: baseParams
 *			    						});
 *		...
 *		    	
 *	}
 *	, onDeleteRow: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["delete"],
 *  	        params: {DATASOURCE_ID: record.get('DATASOURCE_ID')},
 *  	       ...
 *	}
 *	, onFormSave: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["save"],
 *  	        params: record,
 *  	   ...
 *	}
 *});
 *			... 
 *
 * 
 * @author
 * Antonella Giachino (antonella.giachino@eng.it)
 */

Ext.define('Sbi.widgets.wizard.WizardPanel', {
    extend: 'Ext.Panel'

    ,config: {    	  	     	    	
    	tbar: null,
    	bbar: null
    }

	/**
	 * In this constructor you must pass configuration
	 */
	, constructor: function(config) {
		config.layout='card';
		this.initConfig(config);		
		
		var toolbar =  Ext.create('Ext.toolbar.Toolbar',{renderTo: Ext.getBody(),height:30});
		toolbar.add({ id: 'move-prev',
	                  text: 'Back',
	                  handler: function(btn) {
	                      this.navigate(btn.up("panel"), "prev");
	                  }, 
	                  scope: this,
	                  disabled: true
	              });
		toolbar.add('->');
		toolbar.add({id: 'move-next',
	                  text: 'Next',
	                  handler: function(btn) {
	                      this.navigate(btn.up("panel"), "next");
	                  }, scope: this
	              	});
		
		this.bbar = toolbar;
		
		this.items = [{
			              id: 'card-0',
			              html: '<h1>Welcome to the Wizard!</h1><p>Step 1 of 3</p>'
			          },{
			              id: 'card-1',
			              html: '<p>Step 2 of 3</p>'
			          },{
			              id: 'card-2',
			              html: '<h1>Congratulations!</h1><p>Step 3 of 3 - Complete</p>'
			          }];
		
		this.callParent(arguments);
	}
	, navigate: function(panel, direction){
		         // This routine could contain business logic required to manage the navigation steps.
		         // It would call setActiveItem as needed, manage navigation button state, handle any
		          // branching logic that might be required, handle alternate actions like cancellation
		          // or finalization, etc.  A complete wizard implementation could get pretty
		          // sophisticated depending on the complexity required, and should probably be
		          // done as a subclass of CardLayout in a real-world implementation.
	      var layout = panel.getLayout();
	      layout[direction]();
	      Ext.getCmp('move-prev').setDisabled(!layout.getPrev());
	      Ext.getCmp('move-next').setDisabled(!layout.getNext());
	}
});