
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
  * Public Functions
  * 
  *  [list]
  * 
  * 
  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */
/*
Ext.define('Sbi.locale', {
//	statics: {
		 locale.dummyFormatter = function(v){return v;};
		 Sbi.locale.formatters = {
			int: Sbi.locale.dummyFormatter,
			float: Sbi.locale.dummyFormatter,
			string: Sbi.locale.dummyFormatter,		
			date: Sbi.locale.dummyFormatter,		
			boolean: Sbi.locale.dummyFormatter,
			html: Sbi.locale.dummyFormatter
		};


		if(Sbi.chart.commons.Format){
			if(Sbi.locale.formats) {
				Sbi.locale.formatters.int  = Sbi.chart.commons.Format.numberRenderer(Sbi.locale.formats['int']);		
				Sbi.locale.formatters.float  = Sbi.chart.commons.Format.numberRenderer(Sbi.locale.formats['float']);		
				Sbi.locale.formatters.string  = Sbi.chart.commons.Format.stringRenderer(Sbi.locale.formats['string']);		
				Sbi.locale.formatters.date    = Sbi.chart.commons.Format.dateRenderer(Sbi.locale.formats['date']);		
				Sbi.locale.formatters.boolean = Sbi.chart.commons.Format.booleanRenderer(Sbi.locale.formats['boolean']);
				Sbi.locale.formatters.html    = Sbi.chart.commons.Format.htmlRenderer();
			} else {
				Sbi.locale.formatters.int  = Sbi.chart.commons.Format.numberRenderer( );	
				Sbi.locale.formatters.float  = Sbi.chart.commons.Format.numberRenderer( );	
				Sbi.locale.formatters.string  = Sbi.chart.commons.Format.stringRenderer( );		
				Sbi.locale.formatters.date    = Sbi.chart.commons.Format.dateRenderer( );		
				Sbi.locale.formatters.boolean = Sbi.chart.commons.Format.booleanRenderer( );
				Sbi.locale.formatters.html    = Sbi.chart.commons.Format.htmlRenderer();
			}
		};


		Sbi.locale.localize = function(key) {
			if(!Sbi.locale.ln) return key;
			return Sbi.locale.ln[key] || key;
		};	
		
		// alias
		LN = Sbi.locale.localize;
		FORMATTERS = Sbi.locale.formatters;

	//}//statics
});
*/

Ext.ns("Sbi.locale");

Sbi.locale.dummyFormatter = function(v){return v;};
Sbi.locale.formatters = {
	//number: Sbi.locale.dummyFormatter,
	int: Sbi.locale.dummyFormatter,
	float: Sbi.locale.dummyFormatter,
	string: Sbi.locale.dummyFormatter,		
	date: Sbi.locale.dummyFormatter,		
	boolean: Sbi.locale.dummyFormatter,
	html: Sbi.locale.dummyFormatter
};


if(Sbi.chart.commons.Format){
	if(Sbi.locale.formats) {
		Sbi.locale.formatters.int  = Sbi.chart.commons.Format.numberRenderer(Sbi.locale.formats['int']);		
		Sbi.locale.formatters.float  = Sbi.chart.commons.Format.numberRenderer(Sbi.locale.formats['float']);		
		Sbi.locale.formatters.string  = Sbi.chart.commons.Format.stringRenderer(Sbi.locale.formats['string']);		
		Sbi.locale.formatters.date    = Sbi.chart.commons.Format.dateRenderer(Sbi.locale.formats['date']);		
		Sbi.locale.formatters.boolean = Sbi.chart.commons.Format.booleanRenderer(Sbi.locale.formats['boolean']);
		Sbi.locale.formatters.html    = Sbi.chart.commons.Format.htmlRenderer();
	} else {
		Sbi.locale.formatters.int  = Sbi.chart.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.float  = Sbi.chart.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.string  = Sbi.chart.commons.Format.stringRenderer( );		
		Sbi.locale.formatters.date    = Sbi.chart.commons.Format.dateRenderer( );		
		Sbi.locale.formatters.boolean = Sbi.chart.commons.Format.booleanRenderer( );
		Sbi.locale.formatters.html    = Sbi.chart.commons.Format.htmlRenderer();
	}
}


Sbi.locale.localize = function(key) {
	if(!Sbi.locale.ln) {return key;}
	return Sbi.locale.ln[key] || key;
};

// alias
LN = Sbi.locale.localize;
FORMATTERS = Sbi.locale.formatters;
