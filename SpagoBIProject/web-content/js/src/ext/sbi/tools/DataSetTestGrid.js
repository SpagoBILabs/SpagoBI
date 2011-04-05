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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Chiara Chiarelli
 */
Ext.ns("Sbi.tools");

Sbi.tools.DataSetTestGrid = function(config) { 
	

	var paramsTest = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "DATASET_TEST"};
	
	this.services = new Array();
	
	this.services['dataSetTestService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: paramsTest
	});
	
	 this.initStore();
	 

	    // create the editor grid
	    var grid = {
	    	xtype: 'grid',
	        store: this.store,
	        layout: 'fit',
	        //width: 400,
	       // height: 250,
	        cm: this.colModel
	    };

    var c = Ext.apply( {}, config, grid);

    // constructor
    Sbi.tools.DataSetTestGrid.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.tools.DataSetTestGrid, Ext.grid.GridPanel, {
  
	
  	reader:null
  	,services:null
  	,colModel:null
  	,store:null
  	,gridColItems:null
  	,editor:null
  	,userGrid:null
  	,fields: null
  	
  	, initStore: function() {
		
		 this.store = new Ext.data.Store({			
			   proxy: new Ext.data.HttpProxy({
					url: this.services['dataSetTestService']
				  , timeout : 300000     
				  , failure: this.onDataStoreLoadException
				})		
			    , reader: new Ext.data.JsonReader() 
		 		, remoteSort: true	
		 });
		 
		 this.colModel = new Ext.grid.ColumnModel([
		   	                         			new Ext.grid.RowNumberer(), 
		   	                         			{
		   	                         				header: "Data",
		   	                         				dataIndex: 'data',
		   	                         				width: 75
		   	                         			}
		   	                         		]);
		
		this.store.on('metachange', function( store, meta ) {
			
			alert(store.toSource());
			meta.fields[0] = new Ext.grid.RowNumberer();
			this.alias2FieldMetaMap = {};
			var fields = meta.fields;
			var newColumns = new Array();
			newColumns.length = fields.length;
			
			//The following code is used for keep the order of the columns after the execution of the query 
			this.firstPage = true;
			//1) first of all we check if the list of fields is changed 
			if(this.firstPage){
				for(var i = 0; i <meta.fields.length ;i++) {
					newColumns[i]= meta.fields[i].header;
				}	
			}
			
			var val=true;
			if(this.oldColumns != null){
				if(this.oldColumns.length != newColumns.length){
					val=false;
				}else{
					for(var i = 1; i <this.oldColumns.length ;i++) {
						val = val && (this.oldColumns[i]==newColumns[i]);
					}	
				}
			}
			
			//2) if the list of fields is changed we should reload the columnsPosition and columnsWidth arrays
			if(this.oldColumns != null && this.firstPage && !val){

				
				
//				Suppose the columns in the select clause before the re executions are
//				A B C D E . After some operation the visualization in the data store panel is C B E D A
//				So we have the following arrays
//				oldColumns = A B C D E  
//				columnsPosition= 4 1 0 3 2
//				Now suppose the new fields are: A X D K. 
//				The new visualization should keeps the order and so should look like D A X K

				
//				The first step is calculate the array fieldsOrder that maps the new fields in the oldColumns array:
//				fieldsOrder: 0, , 3,    : A live in the position 0 in oldColumns, D in position 3, and the new fields have no position.

				var filedsOrder = new Array();
				var name;
				filedsOrder.length = fields.length;
				for(var i = 0; i <fields.length ;i++) {
					name = fields[i].header;
					for(var j = 0; j < this.oldColumns.length; j++) {
						if(name == this.oldColumns[j]){
							filedsOrder[i] = j;
							this.oldColumns[j]="";
							break;
						}			   
					}
				}

				
//				Now we change the indexes in fieldsOrder with previous position of the linked element. 
//				In code fieldsOrder[i] = columnsPosition[fieldsOrder[i]] and fieldsOrder: 4, , 3,    
//				Clean the array fieldsOrder filtering the empty spaces. 
//				The result is saved in the array cleanFreshPos = 4,3.

				
				var cleanFreshPos = new Array();
				var sortedCleanFreshPos = new Array();
				for(var i = 0; i <filedsOrder.length ;i++) {
					if(filedsOrder[i]!=null){
						cleanFreshPos.push(this.columnsPosition[filedsOrder[i]]);
						sortedCleanFreshPos.push(this.columnsPosition[filedsOrder[i]]);
						filedsOrder[i]=this.columnsPosition[filedsOrder[i]];
					}
				}
				
				var width = new Array();
				width.length = filedsOrder.length;

				sortedCleanFreshPos.sort();

//				Normalize the array cleanFreshPos: force the indexes to be an enumeration between 1 to cleanFreshPos.length. 
//				So normalizedCleanFreshPos = 2,1
//				We have to normalize the array because these values are the new position of the linked elements. 
				
				var normalizedCleanFreshPos = new Array();
				normalizedCleanFreshPos.length = sortedCleanFreshPos.length;

				for(var j = 0; j <cleanFreshPos.length ;j++) {
					for(var y=0; y<sortedCleanFreshPos.length; y++){
						if(sortedCleanFreshPos[y]==cleanFreshPos[j]){
							normalizedCleanFreshPos[j]=y+1;
							break;
						}
					}
				}
				
				
//				At the end we create the new array columnsPosition.
//				We take the fields we have also in the previous query and we save them at the beginning (with the normalizedCleanFreshPos array) 
//				of the array columnsPosition. 
//				Than we push the new fields in the tail of the array. 

				
				this.columnsPosition = new Array();
				this.columnsPosition.length = filedsOrder.length;
				this.columnsPosition[0]=0;//the position 0 is for the column with the row indexes
				
				var k=1;
				var m=0;
				for(var i = 1; i <filedsOrder.length ;i++) {
					if(filedsOrder[i]==null){//new fields
						this.columnsPosition[i]=k+cleanFreshPos.length;//in the tail
						width[k+cleanFreshPos.length] = 100;
						k++;
					}else{//old fields
						for(var j = 0; j <cleanFreshPos.length ;j++) {
							if(cleanFreshPos[j]==filedsOrder[i]){
								this.columnsPosition[i]=normalizedCleanFreshPos[m];
								width[normalizedCleanFreshPos[m]]=this.columnsWidth[filedsOrder[i]];
								m++;
							}
						}
					}
				}
				this.oldColumns=null;
				this.columnsWidth = width;
			}

	    	
			for(var i = 0; i < meta.fields.length; i++) {
			   if(meta.fields[i].type) {
				   var t = meta.fields[i].type;
				   //if(t === 'float' || t ==='int') t = 'number';
				   if (meta.fields[i].format) { // format is applied only to numbers
					   var format = Sbi.qbe.commons.Format.getFormatFromJavaPattern(meta.fields[i].format);
					   var f = Ext.apply( Sbi.locale.formats[t], format);
					   meta.fields[i].renderer = Sbi.qbe.commons.Format.numberRenderer(f);
				   } else {
					   meta.fields[i].renderer = Sbi.locale.formatters[t];
				   }			   
			   }
			   
			   if(meta.fields[i].subtype && meta.fields[i].subtype === 'html') {
				   meta.fields[i].renderer  =  Sbi.locale.formatters['html'];
			   }
			   
			   if(meta.fields[i].subtype && meta.fields[i].subtype === 'timestamp') {
				   meta.fields[i].renderer  =  Sbi.locale.formatters['timestamp'];
			   }
		   }

		   for(var i = 0, l = fields.length, f; i < l; i++) {
			   f = fields[i];
			   if( typeof f === 'string' ) {
				   f = {name: f};
			   }
			     
			   f.header = f.header || f.name;
			   this.alias2FieldMetaMap[f.header] = f;
		   }
		   
		   if(this.oldColumns == null){
				this.oldColumns = new Array();
				for(var i = 0; i <meta.fields.length ;i++) {
					this.oldColumns[i]= meta.fields[i].header;
				}
			}
		   
		   if(this.columnsPosition!=null){
			  var fields2 = new Array();
			  fields2.length = this.columnsPosition.length;
	
			  for(var i = 0; i<fields.length; i++) {
				  fields2[this.columnsPosition[i]] = fields[i];
			  }
			  
			  meta.fields = fields2;
		
		  	}else{
			  this.columnsPosition = new Array();
			  this.columnsWidth = new Array();
			  for(var i = 0; i <fields.length ;i++) {
				  this.columnsPosition[i]= i;
			  }
			  this.columnsWidth[0]=23;
			  for(var i = 1; i <fields.length ;i++) {
				  this.columnsWidth[i]= 100;
			  }
				
		  	}
		    
		   	this.grid.getColumnModel().setConfig(meta.fields);
		   	
		    for(var y=1; y<this.columnsWidth.length; y++){
		    	this.grid.getColumnModel().setColumnWidth(y,this.columnsWidth[y]);
		   	}

			this.firstPage = false;
			this.getView().refresh();


		}, this);
		
		this.store.on('load', this.onDataStoreLoaded, this);
		
	}

	, onDataStoreLoaded: function(store) {
		 var recordsNumber = store.getTotalCount();
	  	 if(recordsNumber == 0) {
	  		Ext.Msg.show({
				   title: LN('sbi.qbe.messagewin.info.title'),
				   msg: LN('sbi.qbe.datastorepanel.grid.emptywarningmsg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.INFO
			});
	  	 }
	  	 
	  	 if (Sbi.config.queryLimit.maxRecords !== undefined && recordsNumber > Sbi.config.queryLimit.maxRecords) {
	  		if (Sbi.config.queryLimit.isBlocking) {
	  			Sbi.exception.ExceptionHandler.showErrorMessage(this.warningMessageItem, LN('sbi.qbe.messagewin.error.title'));
	  		} else {
	  			this.warningMessageItem.show();
	  		}
	  	 } else {
	  		this.warningMessageItem.hide();
	  	 }
	}
	
	, onDataStoreLoadException: function(response, options) {
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
	}

	/*, test: function(params) {	
		
		 Ext.Ajax.request({
	         url: this.services['dataSetTestService'],
	         params: params,
	         method: 'GET',
	         success: function(response, options) {
					if (response !== undefined) {			
			      		if(response.responseText !== undefined) {

			      			var content = Ext.util.JSON.decode( response.responseText );
			      			this.fields = content.metaData.fields;
			      			this.colModel = new Ext.grid.ColumnModel(this.fields);
			      			this.store.loadData(content.rows);
			      			this.commitChanges();
			      			Ext.MessageBox.show({
			      					title: LN('sbi.generic.result'),
			                        msg: LN('sbi.generic.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });        				 

			      		} else {
			      			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.serviceResponseEmpty'), LN('sbi.generic.serviceError'));
			      		}
					} else {
						Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.savingItemError'), LN('sbi.generic.serviceError'));
					}
	         },
	         failure: function(response) {
		      		if(response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			var errMessage ='';
						for (var count = 0; count < content.errors.length; count++) {
							var anError = content.errors[count];
		        			if (anError.localizedMessage !== undefined && anError.localizedMessage !== '') {
		        				errMessage += anError.localizedMessage;
		        			} else if (anError.message !== undefined && anError.message !== '') {
		        				errMessage += anError.message;
		        			}
		        			if (count < content.errors.length - 1) {
		        				errMessage += '<br/>';
		        			}
						}

		                Ext.MessageBox.show({
		                	title: LN('sbi.generic.validationError'),
		                    msg: errMessage,
		                    width: 400,
		                    buttons: Ext.MessageBox.OK
		               });
		      		}else{
		                Ext.MessageBox.show({
		                	title: LN('sbi.generic.error'),
		                    msg: LN('sbi.generic.savingItemError'),
		                    width: 150,
		                    buttons: Ext.MessageBox.OK
		               });
		      		}
	         }
	         ,scope: this
	     });
		
	}*/
	

});

