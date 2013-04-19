/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.tools");

Sbi.tools.ManageDatasetFieldMetadata = function(config) { 

	
	this.border =  false;
	this.frame = false;
	

	//Add to the dom the select used from the combo..
	//it is referenced by Id from the transform
	var selectElement = document.getElementById("fieldTypeSelect");
	if(!selectElement){
		var select = '<select name="fieldTypeSelect" id="fieldTypeSelect" style="display: none;">'+
    	'<option value="ATTRIBUTE">ATTRIBUTE</option>'+
    	'<option value="MEASURE">MEASURE</option>'+
    	'</select>';
		var bodyElement = document.getElementsByTagName('body');
		Ext.DomHelper.append(bodyElement[0].id, select );
	}

	this.fieldsColumns =  [
	    {
	    	header: LN('sbi.ds.field.name'), 
	    	width: 160, 
			id:'name',
			sortable: true, 
			dataIndex: 'name' 
	    },{
        	header: LN('sbi.ds.field.metadata'),
            dataIndex: 'fieldType',
            width: 150,
            editor: new Ext.form.ComboBox({
            	typeAhead: true,
                triggerAction: 'all',
                // transform the data already specified in html
                transform: 'fieldTypeSelect',
                lazyRender: true,
                listClass: 'x-combo-list-small'
            })
        }			
	];
    
	 var cm = new Ext.grid.ColumnModel({
	        columns: this.fieldsColumns
	    });
	 
	 this.fieldStore = new Ext.data.JsonStore({
		    id : 'name',
		    fields: ['name', 'fieldType','type' ],
		    idIndex: 0,
		    data:{}
		});
	 
		 
		 var sm = new Ext.grid.RowSelectionModel({
	         singleSelect: true
	     });

	    // create the editor grid
	    var grid = {
	    	xtype: 'grid',
	        store: this.fieldStore,
	        layout: 'fit',
	        cm: cm,
	        sm: sm,
	        frame: true,
	        autoscroll: true
	    };

    var c = Ext.apply( {}, config, grid);

    // constructor
    Sbi.tools.ManageDatasetFieldMetadata.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.tools.ManageDatasetFieldMetadata, Ext.grid.EditorGridPanel, {
  
	fieldsColumns:null,
	emptyStore: true

  	,loadItems: function(fieldsColumns, record){
  		this.record = record;
  		if(fieldsColumns){
  			this.fieldStore.loadData(fieldsColumns);
  			this.emptyStore = false;
  		}else{
  			this.emptyStore = true;
  		}
	}

	,getValues: function(){
		var data = this.fieldStore.data.items;
		var values =[];
		for(var i=0; i<data.length; i++){
			values.push(data[i].data);
		}
		return values;
	}

	,updateRecord: function(){

		this.record.data.meta = this.getValues();
	}

});

