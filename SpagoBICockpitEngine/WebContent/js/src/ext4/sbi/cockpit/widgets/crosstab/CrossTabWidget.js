/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.widgets.crosstab");

Sbi.cockpit.widgets.crosstab.CrossTabWidget = function(config) {	
	Sbi.trace("[CrossTabWidget.constructor]: IN");
	
	var defaultSettings = {	
	};
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.crosstab.CrossTabWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	
	Ext.apply(this, c);
		
	Sbi.cockpit.widgets.crosstab.CrossTabWidget.superclass.constructor.call(this, c);
		
	
	
	this.on("afterRender", function(){
//		this.getStore().load();
		Sbi.storeManager.loadStore(this.storeId);
		//this.refresh();
		Sbi.trace("[CrossTabWidget.onRender]: store loaded");
	}, this);

	this.addEvents('selection');
	
	Sbi.trace("[CrossTabWidget.constructor]: OUT");
};

Ext.extend(Sbi.cockpit.widgets.crosstab.CrossTabWidget, Sbi.cockpit.core.WidgetRuntime, {
    
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	onRender: function(ct, position) {	
		Sbi.trace("[DummyWidget.onRender]: IN");
		Sbi.cockpit.widgets.dummy.DummyWidget.superclass.onRender.call(this, ct, position);	
		
		this.dummyContent = new Ext.Panel({
			border: false
			, bodyBorder: false
			, hideBorders: true
			, frame: false
			, html: this.msg || 'Sono un widget qualunque'
		});
		
		this.items.each( function(item) {
			this.items.remove(item);
	        item.destroy();           
	    }, this);   
		
		if(this.chart !== null) {
			this.add(this.dummyContent);
			this.doLayout();
		}	
		Sbi.trace("[DummyWidget.onRender]: OUT");
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
    // -----------------------------------------------------------------------------------------------------------------
	
});


Sbi.registerWidget('crosstab', {
	name: 'Static Pivot Table'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/crosstab/table_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.crosstab.CrossTabWidget'
	, designerClass: 'Sbi.cockpit.widgets.crosstab.CrossTabWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});