/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 // create namespace
Ext.ns("Sbi.commons");

Sbi.commons = function(){
	// do NOT access DOM from here; elements don't exist yet
 
    // private variables
   
 
    // public space
	return {
	
		init : function() {
			//alert("init: Sbi.commons");
		}
		
		, toStr : function(o) {
			var str = "";
			
			if(o === 'undefined') return 'undefined';
			
			str += "Type: [" + typeof(o) + "]\n------------------------\n";
			
	        for(p in o) {
	        	str += p + ": " +  o[p] + "\n";
	        }
	        return str;
		}
		
		, dump : function(o) {
			alert(this.toStr(o));
		}
		
		
		//, log: function(msg) {}
		
		
		, log: function(msg) {
			if(console === undefined) {
				alert(msg);
			} else {
				console.log(msg);
			}
		}
		
        
	};
}();


						