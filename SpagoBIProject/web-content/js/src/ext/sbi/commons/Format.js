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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * -  Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.commons");

Sbi.commons.Format = function(){
    //var trimRe = /^\s+|\s+$/g;
    
	return {
        
		/*
		 *  deprecated: why not to use the lib function Ext.util.JSON.encode(o)?
		 */
        toString : function(o){
			var str = '{';
			for (p in o) {
				var obj = o[p];
				if (typeof obj == 'object') {
					str += p + ': ['
					for (count in obj) {
						var temp = obj[count];
						if (typeof temp == 'function') {
							continue;
						}
						if (typeof temp == 'string') {
							// the String.escape function escapes the passed string for ' and \
							temp = String.escape(temp);
							str += '\'' + temp + '\', ';
						} else if (typeof temp == 'date') {
							temp = Ext.util.Format.date(temp, Sbi.config.clientServerDateFormat);
							str += p + ': \'' +  temp + '\', ';
						} else {
							str += temp + ', ';
						}
					}
					// removing last ', ' string
					if (str.length > 1 && str.substring(str.length - 3, str.length - 1) == ', ') {
						str = str.substring(0, str.length - 3);
					}
					str += '], ';
				} else if (typeof obj == 'string') {
					// the String.escape function escapes the passed string for ' and \
					obj = String.escape(obj);
					str += p + ': \'' +  obj + '\', ';
				} else if (typeof obj == 'date') {
					obj = Ext.util.Format.date(obj, Sbi.config.clientServerDateFormat);
					str += p + ': \'' +  obj + '\', ';
				} else {
					// case number or boolean
					str += p + ': ' +  obj + ', ';
				}
			}
			if (str.length > 1 && str.substring(str.length - 3, str.length - 1) == ', ') {
				str = str.substring(0, str.length - 3);
			}
			str += '}';
			
			return str;
        }
	
		
		, toStringOldSyntax: function(o) {
			var str = '';
			for (p in o) {
				var obj = o[p];
				if (typeof obj == 'object') {
					str += p + '='
					for (count in obj) {
						var temp = obj[count];
						if (typeof temp == 'function') {
							continue;
						}
						if (typeof obj == 'string') {
							// the String.escape function escapes the passed string for ' and \
							temp = String.escape(temp);
							str += temp + ';'; // using ';' as separator between values (TODO: change separator)
						} else {
							str += temp + ';'; // using ';' as separator between values (TODO: change separator)
						}
					}
					// removing last ';' string
					if (str.length > 1 && str.substring(str.length - 2, str.length - 1) == ';') {
						str = str.substring(0, str.length - 2);
					}
					str += '&';
				} else if (typeof obj == 'string') {
					// the String.escape function escapes the passed string for ' and \
					obj = String.escape(obj);
					str += p + '=' +  obj + '&';
				} else {
					// case number or boolean
					str += p + '=' +  obj + '&';
				}
			}
			if (str.length > 1 && str.substring(str.length - 2, str.length - 1) == '&') {
				str = str.substring(0, str.length - 2);
			}
			
			return str;
		}
	}

       
}();