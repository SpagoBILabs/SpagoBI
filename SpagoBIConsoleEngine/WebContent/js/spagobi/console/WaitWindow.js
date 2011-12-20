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