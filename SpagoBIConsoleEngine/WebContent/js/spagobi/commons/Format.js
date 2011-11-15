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
  * Public Functions
  * 
  *  [list]
  * 
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.console.commons");

Sbi.console.commons.Format = function(){
 
	return {
		/**
         * Cut and paste from Ext.util.Format
         */
        date : function(v, format){
			
		
		
		
			format = format || "m/d/Y";
			
			if(typeof format === 'string') {
				format = {
					dateFormat: format,
			    	nullValue: ''
				};
			}
			
			
            if(!v){
                return format.nullValue;
            }
            
            if(!(v instanceof Date)){
                v = new Date(Date.parse(v));
            }
          
            
            v = v.dateFormat(format.dateFormat);
         
            return v;
        }

        /**
         * Cut and paste from Ext.util.Format
         */
        , dateRenderer : function(format){
            return function(v){
                return Sbi.console.commons.Format.date(v, format);
            };
         
        }
        
        
        /**
         * thanks to Condor: http://www.extjs.com/forum/showthread.php?t=48600
         */
        , number : function(v, format)  {
    		
        	format = Ext.apply({}, format || {}, {
	    		decimalSeparator: '.',
	    		decimalPrecision: 2,
	    		groupingSeparator: ',',
	    		groupingSize: 3,
	    		currencySymbol: '',
	    		nullValue: ''
	    		
    		});

        	if(v === undefined || v === null) {
        		return format.nullValue;
        	}
        	
        	if (typeof v !== 'number') {
    			v = String(v);
    			if (format.currencySymbol) {
    				v = v.replace(format.currencySymbol, '');
    			}
    			if (format.groupingSeparator) {
    				v = v.replace(new RegExp(format.groupingSeparator, 'g'), '');
    			}
    			if (format.decimalSeparator !== '.') {
    				v = v.replace(format.decimalSeparator, '.');
    			}
    			v = parseFloat(v);
    		}
    		var neg = v < 0;
    		v = Math.abs(v).toFixed(format.decimalPrecision);
    		var i = v.indexOf('.');
    		if (i >= 0) {
    			if (format.decimalSeparator !== '.') {
    				v = v.slice(0, i) + format.decimalSeparator + v.slice(i + 1);
    			}
    		} else {
    			i = v.length;
    		}
    		if (format.groupingSeparator) {
    			while (i > format.groupingSize) {
    				i -= format.groupingSize;
    				v = v.slice(0, i) + format.groupingSeparator + v.slice(i);
    			}
    		}
    		if (format.currencySymbol) {
    			v = format.currencySymbol + v;
    		}
    		if (neg) {
    			v = '-' + v;
    		}
    		return v;
        }   
        
        , numberRenderer : function(format){
            return function(v){
                return Sbi.console.commons.Format.number(v, format);
            };
        }
        
        , string : function(v, format) {
        	format = Ext.apply({}, format || {}, {
	    		trim: true,
	    		maxLength: null,
	    		ellipsis: true,
	    		changeCase: null, // null | 'capitalize' | 'uppercase' | 'lowercase'
	    		prefix: '',
	    		suffix: '',
	    		nullValue: ''
    		});
        	
        	if(!v){
                return format.nullValue;
            }
        	
        	if(format.trim) v = Ext.util.Format.trim(v);
        	if(format.maxLength) {
        		if(format.ellipsis === true) {
        			v = Ext.util.Format.ellipsis(v, format.maxLength);
        		} else {
        			v = Ext.util.Format.substr(v, 0, format.maxLength);
        		}
        	}
        	if(format.changeCase){
        		if(format.changeCase === 'capitalize') {
        			v = Ext.util.Format.capitalize(v);
        		} else if(format.changeCase === 'uppercase') {
        			v = Ext.util.Format.uppercase(v);
        		} else if(format.changeCase === 'lowercase') {
        			v = Ext.util.Format.lowercase(v);
        		}        		
        	}
        	if(format.prefix) v = format.prefix+ v;
        	if(format.suffix) v =  v + format.suffix;
        	
        	return v;
        }
        
        , stringRenderer : function(format){
            return function(v){
                return Sbi.console.commons.Format.string(v, format);
            };
        }
        
        , boolean : function(v, format) {
        	format = Ext.apply({}, format || {}, {
	    		trueSymbol: 'true',
	    		falseSymbol: 'false',
	    		nullValue: ''
    		});
        	
        	if(v === true){
        		 v = format.trueSymbol;
            } else if(v === true){
            	 v = format.falseSymbol;
            } else {
            	 v = format.nullValue;
            }
        	
        	return v;
        }
        
        , booleanRenderer : function(format){
            return function(v){
                return Sbi.console.commons.Format.boolean(v, format);
            };
        }
        
        
        , html : function(v, format) {
        	// format is not used yet but it is reserve for future use
        	// ex. format.cls, format.style
        	v = Ext.util.Format.htmlDecode(v);
        	return v;
        }
       
        , htmlRenderer : function(format){
            return function(v){
                return Sbi.console.commons.Format.html(v, format);
            };
        }
      
        , inlineBarRenderer : function(format){
            return function(v){
                v = (v / format.totValue) * 100; 
                v = Sbi.console.commons.Format.number(v, {decimalPrecision: 2});
                var tip = (format.tooltip !== undefined)? format.tooltip : v;
                return '<div title="'+ tip + '" style="width:' +  v  + '%;height:10px;border:1px solid #000;background:' + format.color + ';"/>'
            };
        }

        , inlinePointRenderer : function(format){  
        	/* v: value
        	 * p: position (?)
        	 * rec: actual record data
        	 * */
            return function(v, p, rec){        
               var localThreshold = format.threshold;
               var localTooltip = format.tooltip;
               //gets threshold from each rows of the dataset and gets relative tooltip
               if (format.thresholdType == 'dataset' && format.threshold !== undefined ){            	 
            	 localThreshold = rec.get(format.nameFieldThreshold);            	      
            	 if (format.nameTooltipField && format.nameTooltipValue){								
						var tmpTooltipValue = rec.get(format.nameTooltipValue);
						if (tmpTooltipValue){
							var newTooltip = format.tooltip.replace("$F{" + format.nameTooltipField + "}", tmpTooltipValue);
							localTooltip =  newTooltip;							
						}
					}
               }
                               
               if (v > localThreshold) { 
                  var width = (format.width === undefined) ? "100%" : format.width+"px";                  
                  return '<div align=center title="'+ localTooltip + '" style="width:'+ width +'"><img  src="../img/ico_point_'+ format.color +'.gif"></img></div>';             
                } else {  
                  return '';

                }
            };
        }
        
        , inlineSemaphoreRenderer : function(format){  
        	/* v: value
        	 * p: position (?)
        	 * rec: actual record data
        	 * */
            return function(v, p, rec){     
               var localThreshold = format.threshold;
               var localTooltipLower = format.tooltipLower ||  format.tooltip;
               var localTooltipHigher = format.tooltipHigher ||  format.tooltip;
               //gets threshold from each rows of the dataset and gets relative tooltip
               if (format.thresholdType == 'dataset' && format.threshold !== undefined ){            	 
            	 localThreshold = rec.get(format.nameFieldThreshold);            	      
            	 if (format.nameTooltipLowerValue && format.nameTooltipLowerField){	
						var tmpTooltipLowerValue = rec.get(format.nameTooltipLowerValue);
						if (tmpTooltipLowerValue){
							var newTooltipLower = format.tooltipLower.replace("$F{" + format.nameTooltipLowerField + "}", tmpTooltipLowerValue);
							localTooltipLower =  newTooltipLower;
						}
            	 }
            	 if (format.nameTooltipHigherValue && format.nameTooltipHigherField){	
						var tmpTooltipHigherValue = rec.get(format.nameTooltipHigherValue);
						if (tmpTooltipHigherValue){
							var newTooltipHigher = format.tooltipHigher.replace("$F{" + format.nameTooltipHigherField + "}", tmpTooltipHigherValue);
							localTooltipHigher =  newTooltipHigher;
						}
					}
               }
                               
               if (v > localThreshold) { 
                  var width = (format.width === undefined) ? "100%" : format.width+"px";                    
                  return '<div align=center title="'+ localTooltipHigher + '" style="width:'+ width +'"><img  src="../img/ico_point_red.gif"></img></div>';
                } else {  
                    var width = (format.width === undefined) ? "100%" : format.width+"px";                  
                    return '<div align=center title="'+ localTooltipLower + '" style="width:'+ width +'"><img  src="../img/ico_point_green.gif"></img></div>';  
                }
            };
        }
        
	};
	
}();







	