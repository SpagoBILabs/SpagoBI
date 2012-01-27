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
 * 
 * @author Giulio Gavardi (giulio.gavardi@eng.it)
 */

Ext.ns("Sbi.browser");


Sbi.browser.ProgressPanel = function(config) { 
	
	var defaultSettings = {
			bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF'
			, autoScroll: true			
	}
	
	var c = Ext.apply(defaultSettings, config || {} );

	
	Sbi.browser.ProgressPanel.superclass.constructor.call(this, c);   

	this.progressGroup = new Object();

	
    this.services = this.services || new Array();
    this.services['GetMassiveExportProgressStatus'] = this.services['GetMassiveExportProgressStatus'] || Sbi.config.serviceRegistry.getServiceUrl({
    			serviceName: 'GET_MASSIVE_EXPORT_PROGRESS_STATUS'
    			, baseParams: new Object()
    			});
    this.services['DownloadMassiveExportZip'] = this.services['DownloadMassiveExportZip'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'DOWNLOAD_MASSIVE_EXPORT_ZIP'
		, baseParams: new Object()
		});
	
    
    
	this.on('expand',
			function () {
			this.expanded = true;
			// false because is executing only for one time because of expansion
			if(this.canAccess == true){
				this.canAccess=false;
				this.updateProgressStatus(false); // NO MORE CALL to avoid synchronization problem
		
				this.canAccess=true;
			}
			this.doLayout();
	}
					   , this
					);
	
	this.on('collapse',
			function () {
				this.expanded = false;
					   }
					   , this
					);
	
	// keep track of current works going on (as from database)
	this.currentWorks = new Object();
	this.downloadButtons = new Object();
	// 
	this.toBeDeleted = new Array();

	this.initPanels();
	// Start cycle
	this.cycleProgress();

	

};

Ext.extend(Sbi.browser.ProgressPanel, Ext.Panel, {
    
	progressGroup : null
	, startedPanel : null
	, downloadedPanel : null
	, scheduledPanel : null
	, services : null
	, expanded : false
	, currentWorks : null
	, toBeDeleted : null
	, downloadButtons : null
	, canAccess: true
		// Progress Bar creation
	, initPanels : function(){
		this.startedPanel = new Ext.Panel({  
			title: 'Started Export',
			//layout: 'card',  
			layout: 'anchor',  
			//activeItem: 0,  
			scope: this,
			height: 220,
			autoWidth: true,
			//html: '<h1>Picchio</h1>',
			defaults: {border:false}
		});
		this.add(this.startedPanel);
		this.doLayout();
		
		this.downloadedPanel = new Ext.Panel({  
			title: 'Download Exports',
		//	layout: 'card',  
			//layout: 'vBox',
			layout: 'anchor', 
			//activeItem: 0,  
			scope: this,
			height: 320,
			autoWidth: true,
			//html: '<h1>Picchio</h1>',
			defaults: {border:false}
		});
		this.add(this.downloadedPanel);
		this.doLayout();
		
		
	}
	, createProgressBar : function(functCd, randomKey) {
		// create progress bar
		//alert('98 - '+functCd+''+randomKey);  
		var progressBar = new Ext.ProgressBar({
            text:'Initializing...'+functCd+' - '+randomKey
         });
        // add progress bar to array
        this.progressGroup[functCd+''+randomKey] = progressBar;
    	this.startedPanel.add(progressBar);
    	this.startedPanel.doLayout();
    	this.currentWorks[functCd+''+randomKey] = true;
		this.progressGroup[functCd+''+randomKey].on('render', function() {
			this.doLayout();
		} , this );
		}
	, createCompletedProgressBar : function(functCd, randomKey) {
		// create progress bar
	    //alert('112 - '+functCd+''+randomKey);  
		var progressBar = new Ext.ProgressBar({
			text: functCd+''+randomKey
		});
		// add progress bar to array
		this.progressGroup[functCd+''+randomKey] = progressBar;
		this.startedPanel.add(progressBar);
		this.startedPanel.doLayout();
		this.currentWorks[functCd+''+randomKey] = true;
		
		this.progressGroup[functCd+''+randomKey].on('render', function() {
			////alert('eccomi 121 '+functCd+''+randomKey);
		    //alert('123 - '+functCd+''+randomKey); 
			this.progressGroup[functCd+''+randomKey].updateProgress(1, functCd+' - '+randomKey);
			//this.doLayout();
	} , this );
	}

	, updateProgressStatus: function(cycling){ 

			// search for pending trhread in database
		Ext.Ajax.request({
      	        url: this.services['GetMassiveExportProgressStatus'],
      	        params: {MESSAGE : 'STARTED'},
      	        success : function(response, options){
      		  	if(response !== undefined) {   
      	      		if(response.responseText !== undefined) {
      	      			var content = Ext.util.JSON.decode( response.responseText );
      	      			if(content !== undefined) {
      	      				// get array
      	      				var worksFound = new Object();
      	      				for(i = 0; i< content.length;i++){
      	      					var prog = content[i];
      	      					var functCd = prog.functCd;
      	      					var randomKey= prog.randomKey;
      	      					this.handleProgressThreadResult(prog, functCd, randomKey, worksFound);
      	      				}
      	      				
      	      				// clean work no more present
      	      				this.cleanNoMorePresentWork(worksFound);
        	      			}
          	      		} 
      	      			
      	      		// only if called from cycle
      	      		if(cycling == true){
      	      			// if(expanded timeout is faster, else take more time before next call
      	      			var that = this;
      	      			if(this.expanded== true){
      	      				setTimeout(function(){that.cycleProgress()}, 2000);
      	      			} else{ // wait longer if not expanded
      	      				setTimeout(function(){that.cycleProgress()}, 5000);
      	      			}
      	      			
      	      		}
      		  	}else {
      	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
          	      		}
      	        },
      	        scope: this,
      			failure: Sbi.exception.ExceptionHandler.handleFailure      
      	   });
	
	}
	, handleProgressThreadResult : function(prog, functCd, randomKey, worksFound){	
		worksFound[functCd+''+randomKey]= true;
			// value progress thread status
			var progressBar = this.progressGroup[functCd+''+randomKey];

			// if in download state make download and delete work
			if(prog.message && prog.message=='DOWNLOAD'){
				
				// if progressBar is null means execution eneded before progress bar was created, it means we must hide a completed progressBar
					
				// call action that downloads zip
				this.createDownloadForm(progressBar, functCd, randomKey, prog.progressThreadId);
//				if(progressBar){
//						this.deleteWork(functCd);
//					}
				}
			else if(prog.message && prog.message=='STARTED'){
					// check if progress exist then update otherwise create
					var partial = prog.partial;
					var total = prog.total;
					this.handleStartedProgressThreadResult(progressBar, functCd, randomKey, partial, total);
				}
	}
	
	, handleStartedProgressThreadResult: function(progressBar, functCd, randomKey, partial, total){
		// if progress bar has already been created update it otherwise create
		if(progressBar){
			if(progressBar.rendered){
			    //alert('202 - '+functCd+''+randomKey); 
				progressBar.updateProgress(partial/total, 'Exporting '+functCd+' item ' + partial + ' of '+total+'...');
				this.doLayout();
			}
		}
		else{
			// create: no progress bar store with functCd
			this.createProgressBar(functCd, randomKey);

		this.progressGroup[functCd+''+randomKey].on('render', function() {
		    //alert('212 - '+functCd+''+randomKey); 
				this.progressGroup[functCd+''+randomKey].updateProgress(partial/total, 'Exporting '+functCd+' item ' + partial + ' of '+total+'...');
			this.doLayout();
		} , this );
			
		}	
	
	}
	, cleanNoMorePresentWork : function(worksFound){

		for (var key in this.currentWorks) {
			var obj = this.currentWorks[key];
			if(obj && obj == true){
				// if it is not among works found delete it
				if(!(worksFound[key] && worksFound[key]==true)){
					//alert('delete 232 '+key);
					this.deleteWork(key); 
				}
			}
		}
	}
	, deleteWork : function(key){
			if(this.progressGroup[key]){
				if(this.progressGroup[key].rendered){
				    //alert('235 - '+key); 
					this.progressGroup[key].updateProgress(1, 'Exporting '+key+' item finished');
				}
				this.toBeDeleted.push(this.progressGroup[key]);

				var that = this;
				// destroy bar only after a while
				setTimeout(function(){
				// clean bar finished
					for(i=0;i<that.toBeDeleted.length;i++){
						var progBar = 	that.toBeDeleted[i];
              	        progBar.reset(true);
						progBar.destroy();
					}
					that.doLayout();
					that.toBeDeleted = new Array();
					
				}, 5000);
				}
				
				//delete this.progressGroup.'functCd';
			this.progressGroup[key] = null;	
			this.currentWorks[key]=null;
				this.doLayout();

	}
	
	, createDownloadForm: function(progressBar, functCd, randomKey, progressThreadId){
		var urlToCall = Sbi.config.serviceRegistry.getBaseUrlStr({});	
		urlToCall += '?ACTION_NAME=DOWNLOAD_MASSIVE_EXPORT_ZIP';
		urlToCall += '&FUNCT_CD='+functCd;
		urlToCall += '&RANDOM_KEY='+randomKey;
		urlToCall += '&PROGRESS_THREAD_ID='+progressThreadId;
		
		if(!progressBar){
			//this.createCompletedProgressBar(functCd, randomKey);
		}
		else{
			var msg = functCd+' - '+randomKey
			////alert('eccomi 269 '+msg);
			
			progressBar.updateProgress(1, msg);
		}
		
    	// delete th progressBar
		if(progressBar){
			//alert('delete 299');
			this.deleteWork(functCd+''+randomKey);
		}
    	
	    if(this.downloadButtons[functCd+randomKey]){
	    	
	    }
	    else{
	    	this.downloadButtons[functCd+randomKey] = new Ext.Button({
	    		id: functCd+randomKey,
	    		text: 'download '+functCd+'-'+randomKey,
	    		disabled: false,
	    		scope: this,
	    		disabled: true,
	    		handler: function(){
	    			window.open(urlToCall,'name','resizable=1,height=750,width=1000');
//	    			if(progressBar){
//	    				//alert('delete 299');
//	    				this.deleteWork(functCd+''+randomKey);
//	    			}
	    			this.downloadButtons[functCd+randomKey].hide();
	    			this.downloadButtons[functCd+randomKey].destroy();
					}
				});
	    }
	    this.downloadButtons[functCd+randomKey].enable();
	    this.downloadedPanel.add(this.downloadButtons[functCd+randomKey]);
	    //this.add(this.downloadButtons[functCd+randomKey]);
	    this.downloadedPanel.doLayout();
	    this.doLayout();
	    
	    
	    
	
	}

	,cycleProgress: function(){
		// for better performances wanted to draw bars only when expanded, but execution must go on aniway
		// true means to cycle
		if(this.canAccess==true){		
			this.canAccess=false;
			this.updateProgressStatus(true);
			this.doLayout();
			this.canAccess=true;
		}
		else{
				var that = this;
				setTimeout(function(){that.cycleProgress()}, 5000);
		}

	}
	
    
});

