/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 Ext.ns("Sbi.locale");

Sbi.locale.dummyFormatter = function(v){return v;};

Sbi.locale.formatters = {
	'int': Sbi.locale.dummyFormatter,
	'float': Sbi.locale.dummyFormatter,
	'string': Sbi.locale.dummyFormatter,		
	'date': Sbi.locale.dummyFormatter,		
	'timestamp': Sbi.locale.dummyFormatter,
	'boolean': Sbi.locale.dummyFormatter,
	'html': Sbi.locale.dummyFormatter,
	inlineBar: Sbi.locale.dummyFormatter,
	inlinePoint: Sbi.locale.dummyFormatter
};


if(Sbi.console.commons.Format){
	if(Sbi.locale.formats) {
		Sbi.locale.formatters['int']  = Sbi.console.commons.Format.numberRenderer(Sbi.locale.formats['int']);		
		Sbi.locale.formatters['float']  = Sbi.console.commons.Format.numberRenderer(Sbi.locale.formats['float']);		
		Sbi.locale.formatters['string']  = Sbi.console.commons.Format.stringRenderer(Sbi.locale.formats['string']);		
		Sbi.locale.formatters['date']   = Sbi.console.commons.Format.dateRenderer(Sbi.locale.formats['date']);		
		Sbi.locale.formatters['timestamp']   = Sbi.console.commons.Format.dateRenderer(Sbi.locale.formats['timestamp']);
		Sbi.locale.formatters['boolean'] = Sbi.console.commons.Format.booleanRenderer(Sbi.locale.formats['boolean']);
		Sbi.locale.formatters['html']    = Sbi.console.commons.Format.htmlRenderer();
		Sbi.locale.formatters.inlineBar    = Sbi.console.commons.Format.inlineBarRenderer();
		Sbi.locale.formatters.inlinePoint = Sbi.console.commons.Format.inlinePointRenderer();
	} else {
		Sbi.locale.formatters['int'] = Sbi.console.commons.Format.numberRenderer( );	
		Sbi.locale.formatters['float'] = Sbi.console.commons.Format.numberRenderer( );	
		Sbi.locale.formatters['string']  = Sbi.console.commons.Format.stringRenderer( );		
		Sbi.locale.formatters['date']   = Sbi.console.commons.Format.dateRenderer( );		
		Sbi.locale.formatters['timestamp'] = Sbi.console.commons.Format.timestampRenderer( );
		Sbi.locale.formatters['boolean'] = Sbi.console.commons.Format.booleanRenderer( );
		Sbi.locale.formatters['html']     = Sbi.console.commons.Format.htmlRenderer();
		Sbi.locale.formatters.inlineBar   = Sbi.console.commons.Format.inlineBarRenderer();
		Sbi.locale.formatters.inlinePoint = Sbi.console.commons.Format.inlinePointRenderer();
	}
}


Sbi.locale.localize = function(key) {
	if(!Sbi.locale.ln) {return key;}
	return Sbi.locale.ln[key] || key;
};

Sbi.locale.getLNValue = function(obj){
    var value = obj; 
	if (obj !== undefined && obj.indexOf('LN(')>=0){					
			var lenIdx = (obj.indexOf(')')) - (obj.indexOf('LN(')+3);
			var idx  = obj.substr(obj.indexOf('LN(')+3,lenIdx);
			value = LN(idx);    			
	}
	return value;
};

// alias
LN = Sbi.locale.localize;
FORMATTERS = Sbi.locale.formatters;




