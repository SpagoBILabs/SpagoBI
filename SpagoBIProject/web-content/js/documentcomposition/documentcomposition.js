/*
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/


var asUrls = new Object();
var asDocTypes = new Object();
var asTitleDocs = new Object();
var asZoomDocs = new Object();
var asExportDSDocs = new Object();
var asLinkedDocs = new Object();
var asLinkedFields = new Object();
var asLinkedCross = new Object();
var asStylePanels = new Object();
var asExportTypes = new Object();
var numDocs = 0;


function setDocs(pUrls, pTitle, pZoom, pExport, pExportTypes, pDocTypes){
	for (i in pUrls)
	{
	   numDocs++;
	}
	asUrls = pUrls;
	asTitleDocs = pTitle;
	asZoomDocs = pZoom;
	asExportDSDocs = pExport;
	asExportTypes = pExportTypes;
	asDocTypes = pDocTypes;
	
}

function setLinkedDocs(pLinkedDocs){
	asLinkedDocs = pLinkedDocs;
}

function setLinkedFields(pLinkedFields){
	asLinkedFields = pLinkedFields;
}

function setLinkedCross(pLinkedCross){
	asLinkedCross = pLinkedCross;
}

function setStylePanels(pStylePanels){
	asStylePanels = pStylePanels;
}


function execDrill(name, url) {
	alert("The 'execDrill' function is deprecated. For the refresh of the document call execCrossNavigation(windowName, labelDoc, parameters).");
}

/* Update the input url with value for refresh linked documents and execute themes */
function execCrossNavigation(windowName, label, parameters) {
	var baseName = "iframe_";
	var labelDocClicked = windowName.substring(baseName.length);
	var tmpUrl = "";
	var extDocsExecute = [];
	
	for(var docMaster in asUrls){
		var reload = false;
		var	typeCross = "";
		var sbiLabelMasterDoc = docMaster;
		var generalLabelDoc = "";
		if (sbiLabelMasterDoc == labelDocClicked){
						
			for (var docLabel in asLinkedDocs){
							
				if (docLabel.indexOf(sbiLabelMasterDoc) >= 0){					
					generalLabelDoc = asLinkedDocs[docLabel];
					var sbiLabelDocLinked = generalLabelDoc[0];
					
					//gets the cross type (internal or external) of tge target
					for (var fieldCross in asLinkedCross){
						var totalCrossPar =  asLinkedCross[fieldCross];
						typeCross = totalCrossPar[0];
						var crossTypeDoc = fieldCross.substring(0, fieldCross.indexOf("__"));
						if (crossTypeDoc == sbiLabelDocLinked){
							break;
						}
					}	
					if (typeCross === 'EXTERNAL' && reload) {
						//checks if the target document is been yet loaded  												
						reload = false;
						for (var f=0, flen=extDocsExecute.length; f<flen; f++){
							if (extDocsExecute[f] == sbiLabelDocLinked) {
								reload = true;
								break;
							}
						}
						break; 
					}
					//gets iframe element
					var nameIframe = "iframe_" + sbiLabelDocLinked;
					var element = document.getElementById(nameIframe);
					
					//updating url with fields found in object
					var j=0; 
					var sbiParMaster = "";
					var tmpOldSbiSubDoc = "";
					var newUrl = "";
					tmpUrl = "";
					var finalUrl = "";
					

					for (var fieldLabel in asLinkedFields){ 
						if (typeCross === 'EXTERNAL' && reload) {							
							//it means that the target document is already loaded (tipical EXTERNAL cross case)							
							break; 
						}
						var totalLabelPar =  asLinkedFields[fieldLabel];
						var	sbiLabelPar = totalLabelPar[0];
						var sbiSubDoc 	= fieldLabel.substring(0, fieldLabel.indexOf("__"));
	
						if (sbiSubDoc == sbiLabelDocLinked){
							if (tmpOldSbiSubDoc != sbiSubDoc){
								newUrl = asUrls[sbiSubDoc]; //final url
								if (newUrl === undefined ) {
									//check if the url is an external type
									newUrl = asUrls["EXT__" + sbiSubDoc]; 
								}
							 	tmpUrl = newUrl[0].substring(newUrl[0].indexOf("?")+1);
							 	finalUrl = newUrl[0];
								tmpOldSbiSubDoc = sbiSubDoc;
							}
							var paramsNewValues = parameters.split("&");
							var tmpNewValue = "";
							var tmpNewLabel = "";
							var tmpOldValue = "";	
							var tmpOldLabel = "";								
							if (paramsNewValues != null && paramsNewValues.length > 0) {
								for (j = 0; j < paramsNewValues.length; j++) {
									tmpNewValue = paramsNewValues[j];
									tmpNewLabel = tmpNewValue.substring(0,tmpNewValue.indexOf("="));
									var paramsOldValues = null;
								 	paramsOldValues = tmpUrl.split("&");
								 	//EXTERNAL navigation: it updates all new parameters in a time (because parameters contains all new values)
									if (typeCross === 'EXTERNAL'){
										if (paramsOldValues != null && paramsOldValues.length > 0) {
											for (k = 0; k < paramsOldValues.length; k++) {
												tmpOldLabel = paramsOldValues[k].substring(0, paramsOldValues[k].indexOf("="));
												//replace all old values of parameter:											
												if (tmpOldLabel == tmpNewLabel){
													reload = true; 
													tmpNewValue = tmpNewValue.substring(tmpNewValue.indexOf("=")+1);
													tmpOldValue = paramsOldValues[k] ;
													tmpOldValue = tmpOldValue.substring(tmpOldValue.indexOf("=")+1);
													//if ( tmpNewValue != ""){													
													    if (tmpNewValue == "%") tmpNewValue = "%25";
														finalUrl = finalUrl.replace(tmpOldLabel+"="+tmpOldValue, tmpNewLabel+"="+tmpNewValue);
														newUrl[0] = finalUrl;
														tmpOldValue = "";
														tmpNewValue = "";
														break;														
													//}
												}
											}
										}
									}
									else{
										//old management (INTERNAL navigation)
										var idParSupp = "";
										var idPar = fieldLabel.substring(fieldLabel.indexOf("__")+2);
										idParSupp = idPar.substring(0,idPar.indexOf("__"))+"__";
										idParSupp = idParSupp+idPar.substring(idParSupp.length,idPar.indexOf("__",idParSupp.length));
										sbiParMaster = asLinkedFields["SBI_LABEL_PAR_MASTER__" + idParSupp];
	
										if ((tmpNewValue.substring(0, tmpNewValue.indexOf("=")) == sbiParMaster) ){
											reload = true; //reload only if document target has the parameter inline
											tmpNewValue = tmpNewValue.substring(tmpNewValue.indexOf("=")+1);
											
											if (paramsOldValues != null && paramsOldValues.length > 0) {
												for (k = 0; k < paramsOldValues.length; k++) {
													tmpOldLabel = paramsOldValues[k].substring(0, paramsOldValues[k].indexOf("="));
													//gets old value of parameter:											
													if (tmpOldLabel == sbiLabelPar){
														tmpOldValue = paramsOldValues[k] ;
														tmpOldValue = tmpOldValue.substring(tmpOldValue.indexOf("=")+1);
														if (tmpOldValue != "" && tmpNewValue != ""){
														    if (tmpNewValue == "%") tmpNewValue = "%25";
															finalUrl = finalUrl.replace(sbiLabelPar+"="+tmpOldValue, sbiLabelPar+"="+tmpNewValue);															
															newUrl[0] = finalUrl;
															tmpOldValue = "";
															tmpNewValue = "";
															break;
														}
													}
												}
											}
										}
									}
								}						
							}
		
						}
					} //for (var fieldLabel in asLinkedFields){ 	
					//updated general url  with new values
					if (reload){
						if (asUrls[generalLabelDoc] !== undefined){
							asUrls[generalLabelDoc][0]=newUrl[0];
						}else{
							asUrls["EXT__" + generalLabelDoc][0]=newUrl[0];
							extDocsExecute.push(generalLabelDoc[0]);
						}
						RE = new RegExp("&amp;", "ig");
						var lastUrl = newUrl[0];
						lastUrl = lastUrl.replace(RE, "&");					
						var msg = {
								label: sbiLabelDocLinked
							  , windowName: this.name//docLabel
							  , typeCross: typeCross
						  	  };						
						sendUrl(nameIframe,lastUrl,msg);
						//reload = false; 
					}
				}//if (docLabel.indexOf(sbiLabelMasterDoc) >= 0){
			}//for (var docLabel in asLinkedDocs){ 
		}
	}   
  
	return;
}

function sendUrl(nameIframe, url, msg){
	if (msg !== null && msg !== undefined && msg.typeCross === 'EXTERNAL'){
		//EXTERNAL cross management
		var params =  url.substring(url.indexOf("?")+1);
		if (params.substring(0,1) === '&') params = params.substring(1);
		msg.parameters = params;
		msg.target = 'self';
        sendMessage(msg, 'crossnavigation');
	}else{
		//INTERNAL cross management
		Ext.get(nameIframe).setSrc(url);
	}
	return;	
}

function pause(interval)
{
    var now = new Date();
    var exitTime = now.getTime() + interval;

    while(true)
    {
        now = new Date();
        if(now.getTime() > exitTime) return;
    }
}
function changeDocumentExecutionUrlParameter(parameterName, parameterValue, sbiLabelDocLinked) {
	var nameIframe = "iframe_" + sbiLabelDocLinked;

	var CurrentUrl = document.getElementById(nameIframe).contentWindow.location.href;

    var parurl = Ext.urlDecode(CurrentUrl);
    parurl[parameterName] = parameterValue;
    parurl = Ext.urlEncode(parurl);
    var endUrl = parurl;
    return CurrentUrl +"&"+parameterName+"="+parameterValue;
}

function exportChartExecution(exportType, sbiLabelDocLinked) {
	var nameIframe = "iframe_" + sbiLabelDocLinked;
	document.getElementById(nameIframe).contentWindow.exportChart(exportType);
}

function  exportExecution(item) {
	var  exportType = item.text;
	var doclabel = item.document;
    //alert('Export document '+doclabel+' as '+exportType);
    var endUrl = this.changeDocumentExecutionUrlParameter('outputType', exportType, doclabel);
    //alert(endUrl);
    if(item.docType == 'REPORT' || (item.docType == 'MAP')){
    	window.open(endUrl, 'name', 'resizable=1,height=750,width=1000');
    
    }else if(item.docType == 'DASH'){
    	exportChartExecution(exportType, doclabel);
    }
	
} 
//create panels for each document
Ext.onReady(function() {  
	if (numDocs > 0){   
  			for (var docLabel in asUrls){ 	
  				if (docLabel.substring(0,5) !== 'EXT__'){
	  				var totalDocLabel=docLabel;	
	  				var strDocLabel = totalDocLabel.substring(totalDocLabel.indexOf('|')+1);
	  				//gets style (width and height)
	  				var style = asStylePanels[strDocLabel];	  				
	  				var zoomDoc = asZoomDocs[strDocLabel] || "false";
	  				var exportDSDoc = asExportDSDocs[strDocLabel] || "false";
	  				//the title drives the header's visualization
	  				var titleDoc = asTitleDocs[strDocLabel] ;
	  				var itemTitleArr = [];
	  				var itemTitleDoc = {};
	  				if (titleDoc[0] === "" && (zoomDoc[0] === "false" || exportDSDoc[0] === "false")){
	  					titleDoc = null;	  					
	  				}else{
	  					itemTitleDoc.text = titleDoc;
	  					itemTitleArr.push(itemTitleDoc);
	  				}
					var widthPx = "";
					var heightPx = "";
					if (style != null){
						widthPx = style[0].substring(0, style[0].indexOf("|"));
						heightPx = style[0].substring(style[0].indexOf("|")+1);
						widthPx = widthPx.substring(widthPx.indexOf("WIDTH_")+6);
			       		heightPx = heightPx.substring(heightPx.indexOf("HEIGHT_")+7);
					}
					//defines the tools (header's buttons):
					var menuItems = new Array();

					var tb = new Ext.Toolbar({
					    style: {
				            background: '#ffffff',
				            margin: 0,
				            border: '0',
				            color: '#000000',
				            align: 'right',
				            padding: 0,
				            'padding-left': 10,
				            'z-index': 100
				        },
				        buttonAlign: 'right',
				        items: []
				        //items: itemTitleArr
					});
					if (exportDSDoc !== undefined && exportDSDoc[0] === "true"){
						var docsExpArrays= asExportTypes[strDocLabel];
						if(docsExpArrays !== undefined && docsExpArrays !== null && docsExpArrays.length != 0){		
							var docType = asDocTypes[strDocLabel];
							if(docsExpArrays.length > 1){
								for(k=0; k< docsExpArrays.length; k++){
									var type = docsExpArrays[k];
								
									var iconname = 'icon-'+type.toLowerCase();
									
									var itemExp = new Ext.menu.Item({
				                        text: type
				                        , group: 'group_2'
				                        , iconCls: iconname 
								     	, scope: this
										, width: 15
								    	//, handler : function() { exportExecution(type, strDocLabel); }
										, listeners:{
											click: function() { exportExecution(this); }								
										}
										, href: ''
										, document: strDocLabel
										, docType : docType
				                    })	
									menuItems.push(itemExp); 
								}
	
								var menu0 = new Ext.menu.Menu({
									id: 'basicMenu_0',
									items: menuItems    
									});	
								var menuBtn = new Ext.Toolbar.MenuButton({
									id: Ext.id()
						            , tooltip: 'Exporters'
									, path: 'Exporters'	
									, iconCls: 'icon-export' 	
						            , menu: menu0
						            , width: 15
						            , cls: 'x-btn-menubutton x-btn-text-icon bmenu '
						        });
								tb = new Ext.Toolbar({
								    style: {
							            background: '#ffffff',
							            margin: 0,	
							            border: '0',
							            color: '#000000',
							            align: 'right',
							            padding: 0, 
							            'padding-left': 10,
							            'z-index': 100
							        },
							        buttonAlign: 'right',
							        //items: [itemTitleDoc, menuBtn]
							        items: [menuBtn]
								});
							}else if(docsExpArrays.length == 1){
								var type = docsExpArrays[0];								
								var iconname = 'icon-'+type.toLowerCase();
								var btnSingle = new Ext.Toolbar.Button({
			                        text: type
			                        , group: 'group_2'
			                        , iconCls: iconname 
							     	, scope: this
									, width: 15
							    	//, handler : function() { exportExecution(type, strDocLabel); }
									, listeners:{
										click: function() { exportExecution(this); }								
									}
									, href: ''
									, document: strDocLabel
									, docType : docType
								});
								tb = new Ext.Toolbar({
								    style: {
							            background: '#ffffff',
							            margin: 0, 
							            border: '0',
							            color: '#000000',
							            align: 'right',
							            padding: 0,	 
							            'padding-left': 10,
							            'z-index': 100
							        },
							        buttonAlign: 'right',
							        //items: [itemTitleDoc, btnSingle]
							        items: [btnSingle]
								});
							}
						}

					}
					
					
					//create panel with iframe
					//alert("titleDoc: -" + titleDoc  + "-");
					var p = new   Ext.ux.ManagedIframePanel({
						frameConfig:{autoCreate:{id:'iframe_' + strDocLabel, name:'iframe_' + strDocLabel}}
						,renderTo   : 'divIframe_'+ strDocLabel
		                ,title      : titleDoc
		                ,defaultSrc : asUrls[docLabel]+""
		                ,loadMask   : true//(Ext.isIE)?true:false
		                ,border		: false //the border style should be defined into document template within the "style" tag
						,height		: Number(heightPx)
						,scrolling  : 'auto'	 //possible values: yes, no, auto  
						//,collapsible: true
						,tbar		: tb
						,bodyStyle	:'padding:10px'
						//,preventHeader: true
						,scope: this

				});
  				}
  	}}
	
	
}); 

