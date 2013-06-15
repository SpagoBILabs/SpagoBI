Ext.define('Sbi.tools.dataset.ManageDatasetFieldMetadata', {
	extend: 'Ext.grid.Panel'

	,config: {
		id: 'dsMetaGrid',
		border: false,
		frame: false,
		fieldsColumns:null,
		selModel:null,
		emptyStore: true,
		xtype: 'grid',
        store: null,		        
        frame: true,
        autoscroll: true,
        loadMask: true
	}

	, constructor: function(config) {
		Ext.apply(this, config || {});
//		this.initConfig(config);	

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
		    	width: '50%', 
				id:'name',
				sortable: true, 
				dataIndex: 'name' 
		    },{
	        	header: LN('sbi.ds.field.metadata'),
	            dataIndex: 'fieldType',
	            width: '50%',
	            editor: new Ext.form.ComboBox({
	            	typeAhead: true,
	                triggerAction: 'all',
	                // transform the data already specified in html
	                transform: 'fieldTypeSelect',
	                lazyRender: true,
	                listClass: 'x-combo-list-small',	               
	            })
	        }			
		]; 
	    
		
		
		this.columns = this.fieldsColumns;
		 
		this.fieldStore = new Ext.data.JsonStore({
			    id : 'metaStoreData',
			    fields: ['name', 'fieldType','type' ],
			    idIndex: 0,
			    data:this.meta || []
			});
		 

		this.selModel= {selType: 'cellmodel'};
		this.store = this.fieldStore;
//		this.store.on('load',this.onLoad);
	    var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	        clicksToEdit: 1
	    });
		this.plugins = [cellEditing];

	    this.callParent(arguments);
	}
	
	,loadItems: function(fieldsColumns, record){
  		this.record = record;
  		if(fieldsColumns){
  			this.fieldStore.loadData(fieldsColumns);
  			this.emptyStore = false;
  		}else{
  			this.emptyStore = true;
  		}
	}

	,getFormState: function(){
		var data = this.fieldStore.data.items;
		var values =[];
		for(var i=0; i<data.length; i++){
			values.push(data[i].data);
		}
		return values;
	}

	,updateRecord: function(){

		this.record.data.meta = this.getFormState();
	}

	,updateData: function(meta){
		var newFieldStore = new Ext.data.Store({
		    fields: ['name', 'fieldType','type' ],
		    data:meta
		});
		this.store.loadData(meta,false);
		this.doLayout();	
	}

});
