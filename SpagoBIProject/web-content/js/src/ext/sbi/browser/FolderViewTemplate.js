/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */


Ext.ns("Sbi.browser");

Sbi.browser.FolderViewTemplate = function(config) { 

	var documentAttributes = '';
	var attributeNameView = '';
	var img = Ext.BLANK_IMAGE_URL ;
	var classImg = ' class="{typeCode}-icon" ';
	var pathPreview = '';
	if (Sbi.settings.widgets.FileUploadPanel && Sbi.settings.widgets.FileUploadPanel.imgUpload){
		pathPreview = Sbi.settings.widgets.FileUploadPanel.imgUpload.directory || '';		
	}
	
	this.services = this.services || new Array();
	
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	params.directory = pathPreview;
	params.operation = 'DOWNLOAD';
	this.services['getImageContent'] = this.services['getImageContent'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_FILE_ACTION'
		, baseParams: params
	});

	for(var i = 0; i < config.metaDocument.length; i++) {

		var meta = config.metaDocument[i];

		if(meta.visible) {		
			// translate meta.id if present
			attributeNameView = LN(meta.id);
			documentAttributes += '<p id="' + meta.id + '">';
			if(meta.showLabel) {
				documentAttributes += '<span><h2>{'+attributeNameView+'}</h2></span>';
			}
			if(meta.maxChars) {
				documentAttributes += '<span> {[Ext.util.Format.ellipsis(values.' + meta.id + ', ' + meta.maxChars + ')]}</span>';
			} else {
				documentAttributes += '<span> {' + meta.id + '}</span>';
			}
			documentAttributes += '</p>';
		}		
	}

	var documentTpl = ''+
	'<div class="box-container">'+
		'<div id="document-item-icon"  class="box-figure">'+
			'<tpl if="this.isSearchResult(summary) == true">'+
				'<tpl if="this.exists(previewFile) == true">'+
					'<img align="center" class="preview-icon" src="'+this.services['getImageContent']+'&fileName={previewFile}" + ext:qtip="<b>{views}</b><br/>{summary}"></img>' +
				'</tpl>' +
				'<tpl if="this.exists(previewFile) == false">'+
					'<img align="center" src="' + img + '" '+ classImg+'" + ext:qtip="<b>{views}</b><br/>{summary}"></img>' +
				'</tpl>' +				
			'</tpl>'+
			'<tpl if="this.isSearchResult(summary) == false">'+ 
				'<tpl if="this.exists(previewFile) == true">'+
					'<img align="center" class="preview-icon" src="'+this.services['getImageContent']+'&fileName={previewFile}"></img>' +
				'</tpl>' +
				'<tpl if="this.exists(previewFile) == false">'+
					'<img align="center" src="' + img + '" '+ classImg+'" ></img>' +
				'</tpl>' +
			'</tpl>'+	
			'<span class="shadow"></span>'+
			'<div class="hover">'+
	        	'<div class="box-actions-container">'+
	            '    <ul class="box-actions">'+	    
	            '		<tpl for="actions">'+  
//	        	' 			<tpl if="this.isAction(name) == true && this.isAbleToCreateDocument(name) ">'+
	            ' 			<tpl if="name != \'delete\'">'+
		        ' 	       		<li class="{name}"><a href="#"></a></li>'+
		        '			</tpl>'+
		        '		</tpl>'+
	            '    </ul>'+
	            '</div>'+
	            '<a href="#" class="delete">Cancella</a>'+
	        '</div>'+
		'</div>'+
		'<div class="box-text">'+documentAttributes +'</div>'+
		'  <div class="fav-container"> '+
		'    <div class="fav"> '+
		'         <span class="icon"></span> '+
		'         <span class="counter">12</span> '+
		'     </div> '+
		'  </div>' +
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
		'<div id="icon" class="folder_home" ></div>' +
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
//	            '<tpl if="{[values.samples.length]} &gt; 0">',
	            	'<div class="group">',
	            	'<h2><div class="group-header">{titleLabel} ({[values.samples.length]})</div></h2>',
	            	'<dl class="group-body">',
	            		'<tpl if="samples.length == 0">',
	            			'<div id="empty-group-message">',
	            			noItem,
	            			'</div>',
	            		'</tpl>',
//	            	'</tpl>',
	                '<tpl for="samples">',   
	                	'{[engine=""]}',
	                	'{[summary=""]}',
	                	'{[views=""]}',
	                	'{[previewFile=""]}',
	                        // -- DOCUMENT -----------------------------------------------
	                        '<tpl if="this.exists(engine) == true">',
	                        	'<dd class="box">', //document
	                        	documentTpl,
	                        '</tpl>',
	                        '<tpl if="this.exists(description) == false">',
	                        	'<br>',
	                        '</tpl>',
	                        // -- FOLDER -----------------------------------------------
	                        '<tpl if="this.exists(engine) == false">',
	                        	'<dd class="group-item">', //Folder
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
	        	, isAction: function(o) {
	        		if(typeof o != undefined  && o != null && o!='delete'){
	        			return true;
	        		}else{
	        			return false;
	        		}
	        		
	        	}
	        	, isAbleToCreateDocument: function(o){
	        		if (o!='detail') return true;
	        		
	    	    	var funcs = Sbi.user.functionalities;
	    	    	if (funcs == null || funcs == undefined) return false;
	    	    	
	    	    	for (f in funcs){
	    	    		if (funcs[f] == this.DETAIL_DOCUMENT || funcs[f] == this.CREATE_DOCUMENT){	    	    			
	    	    			return true;
	    	    			break;
	    	    		}
	    	    	}
	    	    	
	    	    	return false;
	    	    }
	        }
	);
}; 
   
    
Ext.extend(Sbi.browser.FolderViewTemplate, Ext.XTemplate, {
	//constants
    DETAIL_DOCUMENT: 'DocumentDetailManagement'
  , CREATE_DOCUMENT: 'CreateDocument'
  , services : null
});

