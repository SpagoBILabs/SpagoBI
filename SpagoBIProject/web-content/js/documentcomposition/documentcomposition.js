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
var asTitleDocs = new Object();
var asLinkedDocs = new Object();
var asLinkedFields = new Object();
var asStylePanels = new Object();
var numDocs = 0;


function setDocs(pUrls, pTitle){
	for (i in pUrls)
	{
	   numDocs++;
	}
	asUrls = pUrls;
	asTitleDocs = pTitle;
}

function setLinkedDocs(pLinkedDocs){
	asLinkedDocs = pLinkedDocs;
}

function setLinkedFields(pLinkedFields){
	asLinkedFields = pLinkedFields;
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
	var reload = false;
	
	for(var docMaster in asUrls){
		var sbiLabelMasterDoc = docMaster;
		var generalLabelDoc = "";
		if (sbiLabelMasterDoc == labelDocClicked){
			for (var docLabel in asLinkedDocs){ 
				if (docLabel.indexOf(sbiLabelMasterDoc) >= 0){
					generalLabelDoc = asLinkedDocs[docLabel];
					var sbiLabelDocLinked = generalLabelDoc[0];
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
						var totalLabelPar =  asLinkedFields[fieldLabel];
						//var labelPar 	= totalLabelPar[0].substring(totalLabelPar[0].indexOf('|')+1);
						var	sbiLabelPar = totalLabelPar[0];
						var sbiSubDoc 	= fieldLabel.substring(0, fieldLabel.indexOf("__"));
	
						if (sbiSubDoc == sbiLabelDocLinked){
							if (tmpOldSbiSubDoc != sbiSubDoc){
								newUrl = asUrls[sbiSubDoc]; //final url
							 	tmpUrl = newUrl[0].substring(newUrl[0].indexOf("?")+1);
							 	finalUrl = newUrl[0];
								tmpOldSbiSubDoc = sbiSubDoc;
							}
							var paramsNewValues = parameters.split("&");;
							var tmpNewValue = "";
							var tmpOldValue = "";	
							if (paramsNewValues != null && paramsNewValues.length > 0) {
								for (j = 0; j < paramsNewValues.length; j++) {
									//var idPar = fieldLabel.substring(fieldLabel.indexOf("__")+2);
									//sbiParMaster = asLinkedFields["SBI_LABEL_PAR_MASTER__" + idPar.substring(0,4)];
									var idParSupp = "";
									var idPar = fieldLabel.substring(fieldLabel.indexOf("__")+2);
									idParSupp = idPar.substring(0,idPar.indexOf("__"))+"__";
									idParSupp = idParSupp+idPar.substring(idParSupp.length,idPar.indexOf("__",idParSupp.length));
									sbiParMaster = asLinkedFields["SBI_LABEL_PAR_MASTER__" + idParSupp];
									tmpNewValue = paramsNewValues[j];
									if (tmpNewValue.substring(0, tmpNewValue.indexOf("=")) == sbiParMaster){
										reload = true; //reload only if document target has the parameter inline
										tmpNewValue = tmpNewValue.substring(tmpNewValue.indexOf("=")+1);
										var paramsOldValues = null;
									 	paramsOldValues = tmpUrl.split("&");
										if (paramsOldValues != null && paramsOldValues.length > 0) {
											for (k = 0; k < paramsOldValues.length; k++) {
												//gets old value of parameter:
												if (paramsOldValues[k].substring(0, paramsOldValues[k].indexOf("=")) == sbiLabelPar){
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
					} //for (var fieldLabel in asLinkedFields){ 	
					//updated general url  with new values
					if (reload){
						asUrls[generalLabelDoc][0]=newUrl[0];
						RE = new RegExp("&amp;", "ig");
						var lastUrl = newUrl[0];
						lastUrl = lastUrl.replace(RE, "&");
						sendUrl(nameIframe,lastUrl);
						reload = false; 
					}
				}//if (docLabel.indexOf(sbiLabelMasterDoc) >= 0){
			}//for (var docLabel in asLinkedDocs){ 
		}
	}   
  
	return;
}

function sendUrl(nameIframe, url){
	//alert("SendURL - nameIframe: " + nameIframe +  " - url: "+ url);
	//document.getElementById(nameIframe).src = url;
	Ext.get(nameIframe).setSrc(url);
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
 
//create panels for each document
Ext.onReady(function() {  
	if (numDocs > 0){   
  			for (var docLabel in asUrls){ 			
  				var totalDocLabel=docLabel;	
  				var strDocLabel = totalDocLabel.substring(totalDocLabel.indexOf('|')+1);
  				//gets style (width and height)
  				var style = asStylePanels[strDocLabel];
  				var titleDoc = asTitleDocs[strDocLabel];
				var widthPx = "";
				var heightPx = "";
				if (style != null){
					widthPx = style[0].substring(0, style[0].indexOf("|"));
					heightPx = style[0].substring(style[0].indexOf("|")+1);
					widthPx = widthPx.substring(widthPx.indexOf("WIDTH_")+6);
		       		heightPx = heightPx.substring(heightPx.indexOf("HEIGHT_")+7);
				}
				//create panel with iframe
				var p = new  Ext.ux.ManagedIframePanel({
						frameConfig:{autoCreate:{id:'iframe_' + strDocLabel, name:'iframe_' + strDocLabel}}
						,renderTo   : 'divIframe_'+ strDocLabel
		                ,title      : (titleDoc==null || titleDoc== "")?null:titleDoc
		                ,defaultSrc : asUrls[docLabel]+""
		                ,loadMask   : true//(Ext.isIE)?true:false
		                ,border		: false //the border style should be defined into document template within the "style" tag
						,height		: Number(heightPx)
						,scrolling  : 'auto'	 //possible values: yes, no, auto  
						/*
						, listeners  : {
				        	'message:crossnavigation' : {
				        		fn: function(srcFrame, message){
									alert('message:crossnavigation da doc composto');
									try {
										execCrossNavigation(message.data.windowName, message.data.label, message.data.parameters);
									} catch (e) {alert(e); alert(e.description);} 
				        		}
				        		, scope: this
				            }
						}
						*/
				});
				
				/*
				p.on('documentloaded', function() {
				//p.on('domready', function() {
					this.iframe.execScript("parent = document;", true);
					var scriptFn = 	"parent.execCrossNavigation = function(d,l,p) {" +
									"	alert('invio il messaggio');" +
									"	try{" +
									"		sendMessage({'label': l, parameters: p, windowName: d},'crossnavigation');" +
									"	} catch (e) {alert(e); alert(e.description);}" +
									"};";
					this.iframe.execScript(scriptFn, true);
					this.iframe.execScript("uiType = 'ext';", true);
				}, p);
				*/
				
				/*	
					var p = new Ext.Panel({
						id:'p'+i,
				        bodyBorder : false,
				        border:false,
				        collapsible:true,
				        height:Number(heightPx),
				        bodyCfg: {
							tag:'div',
							cls:'x-panel-body',
							children:[{
								tag:'iframe',
			      				src: asUrls[docLabel],
			      				frameBorder:0,
			      				width:'100%',
			      				height:'100%',
			      				id: 'iframe_' + strDocLabel,
			      				name: 'iframe_' + strDocLabel,
			      				style: {overflow:'auto'},
			      				scrolling:'auto'  //possible values: yes, no, auto  
			 				}]
						},
				        renderTo: 'divIframe_'+ strDocLabel
					    });
			    p.show(this);
  			}
  			*/
  	}}
}); 

