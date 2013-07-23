Ext.define('Sbi.tools.dataset.ValidateDataset', {
	extend: 'Ext.Panel'
	
	,config: {
		id: 'dsValidationGrid',
		border: false,
		frame: false,
		fieldsColumns:null,
		selModel:null,
		emptyStore: true,
        store: null,		        
        frame: true,
        autoscroll: true
	}


	, constructor: function(config) {
		
		var panelItems;
		panelItems = this.initDatasetValidationPanel(panelItems,config);
		
		config.items = [panelItems];
		
		thisDatasetValidationPanel = this;


		
		Ext.apply(this, config || {});


		
	    this.callParent(arguments);
	    
		//invokes before each ajax request 
	    Ext.Ajax.on('beforerequest', this.showMask, this);   
	    // invokes after request completed 
	    Ext.Ajax.on('requestcomplete', this.hideMask, this);            
	    // invokes if exception occured 
	    Ext.Ajax.on('requestexception', this.hideMask, this); 
	}
	
	,initDatasetValidationPanel : function(items,config){
		
		

		
	
		//Store for Columns Grid Dataset validation
		/*
		this.storeDataset = new Ext.data.JsonStore({
		    id : 'datasetStoreData',
		    fields: ['column', 'pname','pvalue' ],
		    idIndex: 0,
		    data: []
		});
		*/
		
		//Load Metadata if already present
//		if ((config.meta != undefined) && (config.meta.columns != undefined)){
//			//iterate store to modify type and remove prefix java.lang.
//			var typeValue;
//			for (var i = 0; i < config.meta.columns.length; i++) {
//				var element = config.meta.columns[i];
//				if (element.pname.toUpperCase() == 'type'.toUpperCase()){
//					typeValue = element.pvalue;
//					typeValue = typeValue.replace("java.lang.","");
//					element.pvalue = typeValue;
//				}
//			}
//
//			this.storeMetadata.loadData(config.meta.columns,false); 
//			this.doLayout();	
//		}
		

		
		
		//-----------------------------------------
		


		
		// Columns Metadata Grid  --------	

		/*
		var columnsDefinition =  [
				             		{
				             	    	header: 'Column', 
				             	    	width: '33%', 
				            			sortable: true, 
				             			id:'column',
				             			dataIndex:'column'
				             			
				             	    },{
				             	    	header: 'Attribute', 
				             	    	width: '33%', 
				            			sortable: true, 
				             			id:'pname',
				             			dataIndex:'pname'
				             	    },{
				             	    	header: 'Value', 
				             	    	width: '33%', 
				            			sortable: true, 
				             			id:'pvalue',
				             			dataIndex:'pvalue'
				             	    }			
				             	];		
		
		
		
	
		
		this.gridDatasetValidation = new Ext.grid.Panel({        
	        store: this.storeDataset,
	        columns: columnsDefinition,
	        width: '100%',
	        height: 320,
	        title: 'Dataset Validation',
	        autoscroll: true,
			selModel: {selType: 'rowmodel'}
			//plugins: [cellEditing],
	        //tbar: toolbarColumnsMetadata
	    });
		//----------------------------------------------
		*/
		

		
		// Main Panel ----------------------
		
		this.mainPanel = new Ext.Panel({
			  margins: '50 50 50 50',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
			  defaultType: 'textfield',
			  height:500,
			  layout: 'form',
			  items: []
			});
		
		return this.mainPanel;
	}
	
	//Public Methods
	
	, createDynamicGrid: function(values){
		//remove previous instance of grid (if any)
		if ((this.gridDataset != null) && (this.gridDataset != undefined)){
			this.mainPanel.remove(this.gridDataset);

		}
		this.gridDataset = new Sbi.tools.dataset.ValidateDatasetGrid(values);
		this.mainPanel.add(this.gridDataset);
	}
	
	
	/*
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

		var data = this.storeMetadata.data.items;
		var values =[];
		for(var i=0; i<data.length; i++){
			values.push(data[i].data);
		}
		
		var dataDs = this.datasetMetadataStore.data.items;
		var valuesDs =[];
		for(var i=0; i<dataDs.length; i++){
			valuesDs.push(dataDs[i].data);
		}
		
		var jsonData = {				
					version: 1,
					dataset: [],
					columns: []		
		};

		jsonData.columns = values;	
		jsonData.dataset = valuesDs;				


		return jsonData;
	}

	,updateRecord: function(){

		this.record.data.meta = this.getFormState();
	}

	,updateData: function(columnlist){
		this.fieldStore.loadData(columnlist,false);
		this.doLayout();	
	}
	
	//Update the Store of the Dataset Grid and Column Grid
	,updateGridData: function(meta){
		if ((meta != undefined) && (meta.dataset != undefined)){
			this.datasetMetadataStore.loadData(meta.dataset,false); 			
		}
		
		
		
		this.doLayout();	
	}
	*/
	
	

	

	

	
	
	/**
	 * Opens the loading mask 
	*/
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {    		
    		this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg: "  Wait...  "});
    	}
    	if (this.loadMask){
    		this.loadMask.show();
    	}
    }

	/**
	 * Closes the loading mask
	*/
	, hideMask: function() {
    	if (this.loadMask && this.loadMask != null) {	
    		this.loadMask.hide();
    	}
	} 
});
