/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2009 Engineering Ingegneria Informatica S.p.A.
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
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */


Ext.ns("Sbi.browser");

Sbi.browser.FolderViewTemplate = function(config) { 
	
	//alert('->' + config.metaFolder.toSource());
	//alert('->' + config.metaDocument.toSource());	
	var documentAttributes = '';
	var attributeNameView = '';
	for(var i = 0; i < config.metaDocument.length; i++) {

		var meta = config.metaDocument[i];
		if(meta.visible) {		
			// translate meta.id if present
			attributeNameView = LN(meta.id);
			documentAttributes += '<p id="' + meta.id + '">';
			if(meta.showLabel) {
				//documentAttributes += '<span class="field-label">' + meta.id + ':</span>';
				documentAttributes += '<span class="field-label">' + attributeNameView + ':</span>';
			}
			if(meta.maxChars) {
				documentAttributes += '<span class="field-value" title="{' + meta.id + '}"> {[Ext.util.Format.ellipsis(values.' + meta.id + ', ' + meta.maxChars + ')]}</span>';
			} else {
				documentAttributes += '<span class="field-value"> {' + meta.id + '}</span>';
			}

			documentAttributes += '</p>';
		}
	}

	var documentTpl = '' +
	'<div id="document-item-icon" class="document-item-icon">' +
	
	'<tpl if="this.isSearchResult(summary) == true">'+
		'<img src="' + Ext.BLANK_IMAGE_URL + '" class="{typeCode}-icon" ext:qtip="<b>{views}</b><br/>{summary}"></img>' +
	'</tpl>'+
	'<tpl if="this.isSearchResult(summary) == false">'+
		'<img src="' + Ext.BLANK_IMAGE_URL + '" class="{typeCode}-icon"></img>' +
	'</tpl>'+	    
	'</div>' +
    '<div class="item-desc">' +
    documentAttributes +
    '</div>';
	
	
	
	var folderAttributes = '';
	for(var i = 0; i < config.metaFolder.length; i++) {
		var meta = config.metaFolder[i];
		if(meta.visible) {		
			
			folderAttributes += '<p id="' + meta.id + '">';
			if(meta.showLabel) {
				folderAttributes += '<span class="field-label">' + meta.id + ':</span>';
			}
			if(meta.maxChars) {
				folderAttributes += '<span class="field-value"> {[Ext.util.Format.ellipsis(values.' + meta.id + ', ' + meta.maxChars + ')]}</span>';
			} else {
				folderAttributes += '<span class="field-value"> {' + meta.id + '}</span>';
			}
			folderAttributes += '</p>';
		}
	}
	
	var folderTpl = '' + 
	'<tpl if="this.isHomeFolder(codType) == true">' +
		'<div id="icon" class="folder_home"></div>' +
    '</tpl>' +
    '<tpl if="this.isHomeFolder(codType) == false">' + 
    	'<div id="icon" class="folder"></div>' + 
	'</tpl>' +
    '<div class="item-desc">' +
        folderAttributes +
    '</div>';
	
	

	
	var summaryTpl =''+
		'<div id="summary" class="item-desc">{summary}</div>';
	
	var tooltip = new Ext.ToolTip({
	    title: 'Summary',
	    plain: true,
	    showDelay: 0,
	    hideDelay: 0,
	    trackMouse: true
	}); 

	var noItem = LN('sbi.browser.folderdetailpanel.emptytext');
	
	Sbi.browser.FolderViewTemplate.superclass.constructor.call(this, 
			 '<div id="sample-ct">',
	            '<tpl for=".">',
	            '<div class="group">',
	            '<h2><div class="group-header">{titleLabel} ({[values.samples.length]})</div></h2>',
	            '<dl class="group-body">',
	            	'<tpl if="samples.length == 0">',
	            		'<div id="empty-group-message">',
	            		noItem,
	            		'</div>',
	            	'</tpl>',
	                '<tpl for="samples">',   
	                	'{[engine=""]}',
	                	'{[summary=""]}',
	                	'{[views=""]}',
	                    '<dd class="group-item">',
	                        '<div class="item-control-panel">',	 
	                        	'<tpl for="actions">',   
	                            	'<div class="button"><img class="action-{name}" title="{description}" src="' + Ext.BLANK_IMAGE_URL + '"/></div>',
	                            '</tpl>',
	                        '</div>',
	                        // -- DOCUMENT -----------------------------------------------
	                        '<tpl if="this.exists(engine) == true">',
	                        	documentTpl,
	                        '</tpl>',
	                        // -- FOLDER -----------------------------------------------
	                        '<tpl if="this.exists(engine) == false">',
	                        	folderTpl,
	                        '</tpl>',
	                    '</dd>',
	                '</tpl>',
	            '<div style="clear:left"></div></dl></div>',
	            '</tpl>',
	        '</div>', {
	        	exists: function(o){
	        		return typeof o != 'undefined' && o != null && o!='';
	        	}
	        	, isHomeFolder: function(s) {
	        		return s == 'USER_FUNCT';
	        	}
	        	, isSearchResult: function(o) {
	        		if((typeof o != undefined) && o != null && o!=''){
	        			return true;
	        		}else{
	        			return false;
	        		}
	        		
	        	}
	        }
	);
}; 
   
    
Ext.extend(Sbi.browser.FolderViewTemplate, Ext.XTemplate, {
	
});

