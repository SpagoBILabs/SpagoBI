/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.WaitWindow = function(config) {
	

	var defaultSettings = Ext.apply({}, config || {}, {
		title: 'Please wait'
		, width: 500
		, height:150
		, hasBuddy: false		
	});
	
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.waitWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.waitWindow);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
	this.initMainPanel(c);	


	this.okButton = new Ext.Button({
		text: LN('sbi.console.error.btnClose'),
		handler: function(){
        	this.hide();
        }
        , scope: this
	});
	
	
	
	c = Ext.apply(c, {  	
		layout: 'fit',
		closeAction:'hide',
		constrain: true,
		plain: true,
		modal:true,
		title: this.title,
		buttonAlign : 'center',
		buttons: [this.okButton],
		items: [this.mainPanel]
	});

	// constructor
	Sbi.console.WaitWindow.superclass.constructor.call(this, c);    
};

Ext.extend(Sbi.console.WaitWindow, Ext.Window, {
    
	mainPanel: null
   , okButton: null
   , statusText: null
   , startingTxt: 'Please wait, somethings is happening at the server side'
   , startedTxt:  'Server side finished to do whatever it was doing'
   , progressTickNo: null 
  
    
    // public methods
    
   	, start: function() {
		this.progressTickNo = 0;
		this.statusText.setText( this.startingTxt );
		this.okButton.disable();
		this.progressBar.wait({
            interval:200,
            increment:15
        });
	}

	, stop: function(msg) {
		this.progressBar.reset();
		this.progressBar.updateProgress(1,'',true);
		this.statusText.setText(msg || this.startedTxt);
		this.okButton.enable();
		
	}
   
   
    // private methods
    
    , initMainPanel: function() {
		this.progressBar = new Ext.ProgressBar({
		    
	    });
		
		this.progressBar.on('update', function(pb){
			this.progressTickNo++;
			var tailingDotsNum = this.progressTickNo%5;
			var tailingDotsStr = '.';
			for(var i = 0; i < tailingDotsNum; i++) {
				tailingDotsStr += '.'
			}
			this.statusText.setText( this.startingTxt + tailingDotsStr);
	    }, this);
		
		this.statusText = new Ext.form.Label({
	        text: 'Proecess is starting up ...'
	        , style:'padding-top:10px;font-size:16;'
	    });
	
		this.mainPanel = new Ext.Panel({
			layout: {
            	type:'vbox',
            	padding:'10',
            	align:'stretch'
        	},

		    frame: false, 
		    border: false,
		    bodyStyle:'background:#E8E8E8;',
		    style:'padding:3px;',
		    items: [this.progressBar, this.statusText]
		});
    }
    
});