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
    this.services['DeleteMassiveExportZip'] = this.services['DeleteMassiveExportZip'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'DELETE_MASSIVE_EXPORT_ZIP'
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
	this.downloadButtonPanels = new Object();
	this.deleteButtonPanels = new Object();
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
	, downloadButtonPanels : null
	, deleteButtonPanels : null
	, canAccess: true
		// Progress Bar creation
	, initPanels : function(){
		this.startedPanel = new Ext.Panel({  
			title: 'Started Export',
			layout: 'anchor',  
			scope: this,
			height: 120,
			autoWidth: true,
			defaults: {border:false}
		});
		this.add(this.startedPanel);
		this.doLayout();
		
		this.downloadedPanel = new Ext.Panel({  
			title: 'Download Exports',
			layout: 'column',
			scope: this,
			height: 320,
			defaults: {border:false
			}
		});
		this.add(this.downloadedPanel);
		this.doLayout();
		
		this.scheduledPanel = new Ext.Panel({  
			title: 'Scheduled Exports',
			layout: 'anchor', 
			scope: this,
			height: 120,
			autoWidth: true,
			defaults: {border:false}
		});
		this.add(this.scheduledPanel);
		this.doLayout();
		
		
	}
	, createProgressBar : function(functCd, randomKey) {
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

	, updateProgressStatus: function(cycling){ 

			// search for pending thrread in database
		Ext.Ajax.request({
      	        url: this.services['GetMassiveExportProgressStatus'],
      	        params: {//MESSAGE : 'STARTED'
      	        	},
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
				this.createDownloadForm(progressBar, functCd, randomKey, prog.progressThreadId);
				}
			else if(prog.message && ( prog.message=='STARTED' || prog.message=='PREPARED')){
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
					this.deleteWork(key); 
				}
			}
		}
	}
	, deleteWork : function(key){
			if(this.progressGroup[key]){
				if(this.progressGroup[key].rendered){
					this.progressGroup[key].updateProgress(1, 'Exporting '+key+' item finished');
				}
				this.toBeDeleted.push(this.progressGroup[key]);
				var that = this;
				// destroy bar only after a while
				setTimeout(function(){
					for(i=0;i<that.toBeDeleted.length;i++){
						var progBar = 	that.toBeDeleted[i];
              	        progBar.reset(true);
						progBar.destroy();
					}
					that.doLayout();
					that.toBeDeleted = new Array();
				}, 5000);
				}
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
		}
		else{
			var msg = functCd+' - '+randomKey
			progressBar.updateProgress(1, msg);
		}
    	// delete the progressBar
		if(progressBar){
			this.deleteWork(functCd+''+randomKey);
		}
    	
	    if(this.downloadButtonPanels[functCd+randomKey]){
	    }
	    else{
	    	// create panel and put inside button
	    	this.downloadButtonPanels[functCd+randomKey] = new Ext.Panel({  
				//title: 'Started Export',
				//layout: 'fit',  
				scope: this,
				//height: 120,
				autoWidth: true,
				//columnWidth : 0.5,
				defaults: {border:false}
			});

	    	var buttonText = ''+functCd+'-'+randomKey;
	    	// remove milliseconds
	    	buttonText = buttonText.substring(0, (buttonText.length-7));
	    	
	    	var button = new Ext.Button({
	    		id: functCd+randomKey+'download',
	    		text: buttonText,
	    		disabled: false,
	    		scope: this,
	    		disabled: true,
	    		handler: function(){
	    			window.open(urlToCall,'name','resizable=1,height=750,width=1000');
					}
				});
	    	button.enable();
	    	this.downloadButtonPanels[functCd+randomKey].add(button);
	    	
	    }
	   // this.downloadButtons[functCd+randomKey].enable();
	   
	    
	    this.downloadedPanel.add(this.downloadButtonPanels[functCd+randomKey]);
	    //this.downloadedPanel.add(this.downloadButtons[functCd+randomKey]);

	    this.createDeleteForm(functCd, randomKey, progressThreadId);

	    this.downloadedPanel.doLayout();
	    this.doLayout();
	
	}
	, createDeleteForm: function(functCd, randomKey, progressThreadId){
	   
		var pars = {FUNCT_CD: functCd, RANDOM_KEY: randomKey, PROGRESS_THREAD_ID: progressThreadId };
		
		if(this.deleteButtonPanels[functCd+randomKey]){
	    }
	    else{
	    	
	    	this.deleteButtonPanels[functCd+randomKey] = new Ext.Panel({  
				//title: 'Started Export',
				//layout: 'fit',  
				scope: this,
				//height: 120,
				autoWidth: true,
				//columnWidth : 0.5,
				defaults: {border:false}
			});
	    	
	    	
	    	
	    	var button = new Ext.Button({
	    		id: functCd+randomKey+'delete',
	    		//text: 'delete '+functCd+'-'+randomKey,
				iconCls: 'icon-clear',
	    		disabled: false,
	    		scope: this,
	    		disabled: true,
	    		handler: function(){
	    			Ext.Ajax.request({
	    	        url: this.services['DeleteMassiveExportZip'],
	    	        params: pars,
	    	        success : function(response, options) {
	    				if(response !== undefined) {   
	    	    			this.downloadButtonPanels[functCd+randomKey].hide();
	    	    			this.downloadButtonPanels[functCd+randomKey].destroy();
	    	    			this.downloadButtonPanels[functCd+randomKey] = null;
	    					this.deleteButtonPanels[functCd+randomKey].hide();
	    	    			this.deleteButtonPanels[functCd+randomKey].destroy();
	    	    			this.deleteButtonPanels[functCd+randomKey] = null;
	    				}
	    			},
	    	        scope: this,
	    			failure: Sbi.exception.ExceptionHandler.handleFailure      
	    		});	
				}
			});
	    	button.enable();
	    	this.deleteButtonPanels[functCd+randomKey].add(button);
	    }

	    this.downloadedPanel.add(this.deleteButtonPanels[functCd+randomKey]);
	
	}
	, cycleProgress: function(){
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

