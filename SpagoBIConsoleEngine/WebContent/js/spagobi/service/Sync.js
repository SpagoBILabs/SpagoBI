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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi");

Sbi.Sync = function(){
    
	// private variables
	var FORM_ID = 'download-form';
	var METHOD = 'post';
	var createHtmlFn;
	
    // public space
	return {
		
		form: null
		
		, request: function(o) {
			var f = this.getForm();
			if(o.method) f.method = o.method;
			
			f.action = o.url;
			
			if(f.method === 'post') {
				this.resetForm();
				if(o.params) {
					this.replaceDomHelper();
					for(p in o.params) {
						this.addHiddenInput(p, o.params[p]);
					}
					this.restoreDomHelper();
				}
			}else {
				if(o.params) {
					f.action = Ext.urlAppend(f.action, Ext.urlEncode(o.params) );
				}
			}
			
			f.submit();
			
		}
	
		, resetForm: function() {
			var f = Ext.get(FORM_ID);
			var childs = f.query('input');
			
			for(var i = 0, l = childs.length; i < l; i++) {
				 var child = Ext.get(childs[i]);			
				 child.remove();
				
			}
		}
		
		, replaceDomHelper: function() {
			createHtmlFn = Ext.DomHelper.createHtml;
			Ext.DomHelper.createHtml = function(o){
		        var b = '',
	            attr,
	            val,
	            key,
	            keyVal,
	            cn;

		        if(Ext.isString(o)){
		            b = o;
		        } else if (Ext.isArray(o)) {
		            for (var i=0; i < o.length; i++) {
		                if(o[i]) {
		                    b += createHtml(o[i]);
		                }
		            };
		        } else {
		            b += '<' + (o.tag = o.tag || 'div');
		            Ext.iterate(o, function(attr, val){
		                if(!/tag|children|cn|html$/i.test(attr)){
		                    if (Ext.isObject(val)) {
		                        b += ' ' + attr + '=\'';
		                        Ext.iterate(val, function(key, keyVal){
		                            b += key + ':' + keyVal + ';';
		                        });
		                        b += '\'';
		                    }else{
		                        b += ' ' + ({cls : 'class', htmlFor : 'for'}[attr] || attr) + '=\'' + val + '\'';
		                    }
		                }
		            });
		            // Now either just close the tag or try to add children and close the tag.
		            var emptyTags = /^(?:br|frame|hr|img|input|link|meta|range|spacer|wbr|area|param|col)$/i;
		            if (emptyTags.test(o.tag)) {
		                b += '/>';
		            } else {
		                b += '>';
		                if ((cn = o.children || o.cn)) {
		                    b += createHtml(cn);
		                } else if(o.html){
		                    b += o.html;
		                }
		                b += '</' + o.tag + '>';
		            }
		        }
		        return b;
			}
		}
		
		, restoreDomHelper: function() {
			if(!createHtmlFn) {
				alert("Impossible to restore createHtml in DomHelper object");
				return;
			}
			Ext.DomHelper.createHtml = createHtmlFn;
		}
		
		
		, addHiddenInput: function(name, value) {			
			var f = Ext.get(FORM_ID);
			var dh = Ext.DomHelper;
			dh.append(f, {
			    tag: 'input'
			    , type: 'hidden'
			    , name: name
			    , value: value
			});
		}
	
		, getForm: function() {
			//by unique request
			if(this.form === null) {
				this.form = document.getElementById(FORM_ID);
				if(!this.form) {
					var dh = Ext.DomHelper;
					this.form = dh.append(Ext.getBody(), {
					    id: FORM_ID
					    , tag: 'form'
					    , method: METHOD
					    , cls: 'download-form'
					});
				}
			}
			return this.form;
		}
	
	}
}();	