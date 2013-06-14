/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
  
Ext.define('Sbi.selfservice.SelfServiceExecutionIFrame', {
	extend: 'Sbi.widgets.EditorIFramePanelContainer'

		
	, modelName: null
	, datasetLabel: null

	
	, init: function(config){
		this.callParent(config);
		//adds the toolbar
		this.initToolbar();
		
	}


	, initToolbar : function () {

		this.tbar  = Ext.create('Ext.toolbar.Toolbar');
		this.tbar.add('->');
		this.tbar.add({
			iconCls : 'icon-saveas' 
			, tooltip: LN('sbi.execution.executionpage.toolbar.saveas')
			, scope : this
		    , handler : this.saveWorksheet
		});
		
	}
	
	,saveWorksheet : function() {
		
		var theWindow = this.iframe.getWin();
		Sbi.debug('[WorksheetEditorIframePanelExt3.saveWorksheet]: got window');
		
		//the worksheet has been constructed starting from a qbe document
		var thePanel = theWindow.qbe;
		Sbi.debug('[WorksheetEditorIframePanelExt3.saveWorksheet]: qbe panel is ' + thePanel);
		if (thePanel == null) {
			Sbi.debug('[WorksheetEditorIframePanelExt3.saveWorksheet]: qbe panel is null, getting woskheet panel ...');
			//the worksheet is alone with out the qbe
			thePanel = theWindow.workSheetPanel;
			Sbi.debug('[WorksheetEditorIframePanelExt3.saveWorksheet]: woskheet panel is ' + thePanel);
		}
		
    	var template = thePanel.validate();	
    	if (template == null){
    		return;
    	}
    	var templateJSON = Ext.JSON.decode(template);
		var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
		var worksheetQuery = templateJSON.OBJECT_QUERY;
		var documentWindowsParams = {
				'OBJECT_TYPE': 'WORKSHEET',
				'template': wkDefinition,
				'OBJECT_WK_DEFINITION': wkDefinition,
				'OBJECT_QUERY': worksheetQuery,
				'model_name': this.modelName,
				'typeid': 'WORKSHEET' 
		};

		if(this.datasetLabel!=null){
			documentWindowsParams.dataset_label= this.datasetLabel;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_DATASET';
		}else if(this.modelName!=null){
			documentWindowsParams.model_name= this.modelName;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_MODEL';
		}
		
		this.win_saveDoc = Ext.create("Sbi.execution.SaveDocumentWindowExt4",documentWindowsParams);
		this.win_saveDoc.show();
    
    }
	
		
	
});