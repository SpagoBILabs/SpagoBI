/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 /**
  * ...
  * by Andrea Gioia
  */
 

Sbi.geo.DrillControlPanel = function(config) {
	
	this.analysisState = undefined;
    
    var Hierarchy = Ext.data.Record.create([
    	{name: 'id'}                 
    	, {name: 'name'} 
    	, {name: 'description'} 
    	, {name: 'levels'} 
	]);
    
        
  	this.hierarchyComboBoxStore = new Ext.data.Store({
	   proxy: new Ext.data.HttpProxy({
           //url: 'AdapterHTTP?ACTION_NAME=GET_HIERARCHIES_ACTION',
           url: Sbi.geo.app.serviceRegistry.getServiceUrl('GET_HIERARCHIES_ACTION'),
           success: function(response){
           	 // do nothing 
           },
   		   failure: Sbi.commons.ExceptionHandler.handleFailure      
        })
        , reader: new Ext.data.JsonReader({   
    		root: "hierarchies",                        
    		id: "id" }, Hierarchy)
        //, autoLoad: true
        , listeners: {
  			datachanged : {
  				fn: this.handleHierarchySelectorDataChanged
          		, scope: this
  			}
        }      
	});  
		    
	this.hierarchyComboBox = new Ext.form.ComboBox({
	   tpl: '<tpl for="."><div ext:qtip="{name}: {description}" class="x-combo-list-item">{name}</div></tpl>',	
	   editable  : false,
	   fieldLabel : 'Hiearachy',
	   forceSelection : true,
	   mode : 'local',
	   name : 'hierarchy',
	   store : this.hierarchyComboBoxStore,
	   displayField:'name',
		 valueField:'id',
		 emptyText:'Select hierarchy...',
		 typeAhead: true,
		 triggerAction: 'all',
		 width: 150,
		 selectOnFocus:true,
		 listeners: {
		    'select': {
          fn: this.handleSelectHierarchy
          , scope: this
        }
      }
	  });	  
    
    var hierarchySelector = new Ext.FormPanel({
        labelWidth: 65,
        frame:false,
        bodyStyle:'padding:5px 5px 0',
        margins:'10 10 10 15',
        border: true,
        defaultType: 'textfield',
        items: [this.hierarchyComboBox]
    });    
    
    var hierarchyRootNode = new Ext.tree.TreeNode({text:'Hierarchy', expanded:true});
    
    this.hierarchyTree = new Ext.tree.TreePanel({
      root:hierarchyRootNode,
      enableDD:false,
      expandable:true,
      collapsible:true,
      bodyStyle:'padding:5px 0px 5px; margin: 5px 0;',
      leaf:false,
      lines:true,
      border: false,
      animate:true,
      listeners: {
		    'checkchange': {
          fn: this.handleChangeHierarchyLevel
          , scope: this
        }
      }
    });
    
    var topPanel = new Ext.Panel({
      autoScroll: true,
      border: false,
      items: [hierarchySelector, this.hierarchyTree]
    });
    
    
    
    
    // --------------------------------------------------------------------------
		
	var Map = Ext.data.Record.create([
    	{name: 'id'}                 
    	, {name: 'name'} 
    	, {name: 'description'} 
    	, {name: 'features'} 
	]);	
		
	this.mapComboBoxStore = new Ext.data.Store({
	   proxy: new Ext.data.HttpProxy({
	   	   params: {
	   	   	featureName: undefined
	   	   },	   	  
           //url: 'AdapterHTTP?ACTION_NAME=X_GET_MAPS_ACTION',
           url: Sbi.geo.app.serviceRegistry.getServiceUrl('X_GET_MAPS_ACTION'),
           success: function(response){
           	 // do nothing 
           },
   		   failure: Sbi.commons.ExceptionHandler.handleFailure      
        })
        , reader: new Ext.data.JsonReader({   
    		root: "maps",                        
    		id: "id" }, Map)
        //, autoLoad: true
        , listeners: {
  			datachanged : {
  				fn: this.handleMapSelectorDataChanged
          		, scope: this
  			}
        }        
	}); 
		    
		    
	this.mapComboBox = new Ext.form.ComboBox({
	    	tpl: '<tpl for="."><div ext:qtip="{name}: {description}" class="x-combo-list-item">{name}</div></tpl>',	
	    	editable  : false,
	    	fieldLabel : 'Map',
	    	forceSelection : true,
	    	mode : 'local',
	    	name : 'map',
	    	store : this.mapComboBoxStore,
	    	displayField:'name',
		    valueField:'id',
		    emptyText:'Select map...',
		    typeAhead: true,
		    triggerAction: 'all',
		    width: 150,
		    selectOnFocus:true,
		    listeners: {
  		    'select': {
            fn: this.handleSelectMap
            , scope: this
          }
        }
	    });
    
    var mapSelector = new Ext.FormPanel({
        labelWidth: 65,
        frame:false,
        bodyStyle:'padding:5px 5px 0',
        margins:'10 10 10 15',
        border: true,
        defaultType: 'textfield',
        items: [this.mapComboBox]
    });
    
    var featuresRootNode = new Ext.tree.TreeNode({text:'Features', expanded:true});
    
    this.featuresTree = new Ext.tree.TreePanel({
      root:featuresRootNode,
      enableDD:false,
      expandable:true,
      collapsible:true,
      bodyStyle:'padding:5px 0px 5px; margin: 5px 0;',
      leaf:false,
      lines:true,
      border: false,
      animate:true
      /*
      listeners: {
		    'checkchange': {
          fn: this.handleChangeHierarchyLevel
          , scope: this
        }
      }
      */
    });
    
    
    var bottomPanel = new Ext.Panel({
        autoScroll: true,
        border: false,
        items: [mapSelector, this.featuresTree]
    });
    
    // constructor
    Sbi.geo.DrillControlPanel.superclass.constructor.call(this, {
           
        border:false,
        //autoScroll: true,             
        layout:'border',
        layoutConfig:{
          animate:true
        },
        //baseCls : 'x-accordion-hd',
        items: [{
          title: 'Geo Dimension',  
          region: 'north',
          split: true,         
          autoScroll: true,
          //collapsible: true,   
          //collapsed: false,
          hideCollapseTool: true,
          titleCollapse: true,
          //collapseMode: 'mini',
          height: 200,
          //anchor: '100%, 50%',
          border:true,
          bodyStyle:'padding:3px 3px 3px 3px',
          items: [topPanel]
        },{
          title: 'Map', 
          region: 'center',          
          autoScroll: true,
          //collapsible: true,   
          //collapsed: false,
          hideCollapseTool: true,
          titleCollapse: true,
          //collapseMode: 'mini',
          //anchor: '100%, 50%',
          border:true,
          bodyStyle:'padding:3px 3px 3px 3px',
          items: [bottomPanel]
                       
        }]
    });
    
    this.syncronizeAnaysisState();
    //this.hierarchyComboBoxStore.load();
}




Ext.extend(Sbi.geo.DrillControlPanel, Ext.Panel, {
    
    // static contens and methods definitions
   
   
    // public methods
    getAnalysisState : function() {
      
      //var params = {};
      
      var analysisState = {};
      
      analysisState.hierarchy = this.hierarchyComboBox.getValue();
      analysisState.level = this.hierarchyTree.getChecked()[0].text;
      
      analysisState.map = this.mapComboBox.getValue();
      //analysisState.features = [];
      analysisState.features = '';
      var checkedFeatures = this.featuresTree.getChecked();
      for(var i = 0; i < checkedFeatures.length; i++) {
      	if(i > 0) analysisState.features += ',';
      	analysisState.features += checkedFeatures[i].text;
      }      
      
      return analysisState;
      
      //params.analysisState = analysisState;
      //return params;
    }, 
    
    // public methods
    setAnalysisState : function(analysisState) {
    	this.analysisState = analysisState;
		this.hierarchyComboBoxStore.load();
        //alert(analysisState.toSource());
    }, 
    
    syncronizeAnaysisState : function(){
   		Ext.Ajax.request({
			url: Sbi.geo.app.serviceRegistry.getServiceUrl('GET_ANALYSIS_STATE_ACTION'),
			success: function(response, options) {
				var analysisState = Ext.util.JSON.decode( response.responseText );
				this.setAnalysisState(analysisState);
			}
			, failure: Sbi.commons.ExceptionHandler.handleFailure
			, scope: this
   		});  
    },
    
    // private handlers
    
    handleHierarchySelectorDataChanged: function(store) {
    	// select one hierachy
    	var index;
    	if(this.analysisState == undefined) {
    		index = 0;
    	} else {
    		index = store.find('id', this.analysisState.hierarchy);
    	}    	
    	var hierarchy = store.getAt( index );
    	this.hierarchyComboBox.setValue(hierarchy.id);  
    	
    	this.hierarchyComboBox.fireEvent('select', this.hierarchyComboBox, hierarchy, 0);    	
    },
    
    handleSelectHierarchy : function(combo, record, index){
      // load selected hierarcy's levels
      var hierarchyName = record.data['name'];
      var levels = record.data['levels'];
      var hierarchyRoot = new Ext.tree.TreeNode({text:hierarchyName, expanded: true});
      for(var i = 0; i < levels.length; i++) {
        var node;    
        
        node = new Ext.tree.TreeNode({
          text: levels[i].name,
          iconCls: 'noimage', 
          checked:false
        });
        
        Ext.apply(node.attributes, levels[i]);
     
        hierarchyRoot.appendChild( node );
      }
      this.hierarchyTree.setRootNode(hierarchyRoot);
      
      // select one level into the hierachy
      var node;
      if(this.analysisState == undefined) {
      	node = this.hierarchyTree.getRootNode().firstChild;
      } else {
      	node = this.hierarchyTree.getRootNode().findChild('id', this.analysisState.level);
      }
            
      node.getUI().toggleCheck(true);     
       
      this.hierarchyTree.fireEvent('checkchange', node, true);      
    },
    
    handleChangeHierarchyLevel : function(node, checked){
      if(!checked) {
        node.getUI().toggleCheck(true);
      } else {
        this.hierarchyTree.getRootNode().cascade(function(n){
          n.getUI().toggleCheck(false);
        });
        node.getUI().toggleCheck(true);
      }
      this.mapComboBoxStore.load({params: {featureName: node.attributes['feature']}});
    },
    
    handleMapSelectorDataChanged: function(store) {
    	// select one map
    	var index;
    	
    	if(this.analysisState == undefined) {
      		index = 0;
	    } else {
	      	index = store.find('id', this.analysisState.map);
	    }
    	var map = store.getAt( index );
    	this.mapComboBox.setValue(map.id);
    	
    	this.mapComboBox.fireEvent('select', this.mapComboBox, map, 0);
    },
    
    handleSelectMap  : function(combo, record, index){
    	// load selected map's features
      	var mapName = record.data['name'];
		var features = record.data['features'];
      	var hierarchyRoot = new Ext.tree.TreeNode({text:mapName, expanded: true});
      	for(var i = 0; i < features.length; i++) {
        	var node;    
        
        	node = new Ext.tree.TreeNode({
          		text: features[i].name,
          		iconCls: 'noimage', 
          		checked:false
        	});
        
         	Ext.apply(node.attributes, features[i]);
         
        	hierarchyRoot.appendChild( node );
      	}
      	this.featuresTree.setRootNode(hierarchyRoot);
      
      	// select features into the map
      	if(this.analysisState == undefined) {
      		var node = this.featuresTree.getRootNode().findChild('id', this.mapComboBoxStore.lastOptions.params.featureName);
      		node.getUI().toggleCheck(true);
      	} else {
      		
      		var featureMap = [];
      		
      		for(i = 0; i < this.analysisState.features.length; i++) {
      			featureMap[ this.analysisState.features[i] ] = true;
      		}
      		
      		this.featuresTree.getRootNode().cascade( function(node) {
      			if(featureMap[ node.attributes['id'] ]) {
      				node.getUI().toggleCheck(true);
      			}
      			
      		}, this);
      	}
      	
      	this.analysisState = undefined;
    }
    
    
    
});