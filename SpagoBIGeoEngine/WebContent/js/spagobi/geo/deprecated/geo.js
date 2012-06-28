/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * 
 *  @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */

	var actionUrl = null;
	
	var hierarchies = new Array();
	var selectedHierarchy = null;
	
	var hierarchyLevels = new Array();
	var hierarchyFeatures = new Array();
	
	var selectedLevel = null;
	var selectedFeature = null;
	
	var maps = new Array();
	var selectedMap = null;
	
	var layers = new Array();
	var selectedLayersMap = new Array();
	var selectedLayers = "";


	
	

	// ==== BASE FUNCS ========================================================================================= 

	function init() {
		var alertOnClose = { 
			onClose: function(eventName, win) { 
				windowCloseHandler(eventName, win);
			}
		};
			
		Windows.addObserver(alertOnClose);
	}
	
	function draw() {
		drawHierarchiesBlock();
		drawHierarchyLevelsBlock();
		drawMapsBlock();
		drawLayersBlock();
	}
	
	

	function createRadio(name, value, selected, callback_script) {
    	var radio;
      
      	if(document.all && !window.opera && document.createElement) {
      		if(selected) {
            	radio = document.createElement("<input type='radio' id='" + name + "' name='" + name + "' value='" + value + "' onClick='" + callback_script + "' checked>");
            } else {
            	radio = document.createElement("<input type='radio' id='" + name + "' name='" + name + "' value='" + value + "' onClick='" + callback_script + "'>");
            }
      	} else {
        	radio = document.createElement("input");
            radio.setAttribute("type", "radio");
            radio.setAttribute("id", name);
            radio.setAttribute("name", name);
            radio.setAttribute("value", value);
            radio.setAttribute("onClick",callback_script);
            if(selected) radio.setAttribute("checked", "true");
      	}
      	
      	
      	return radio;
    }
    
    function createCheckBox(name, value, selected, callback_script) {
      var checkBox;
      
      if(document.all && !window.opera && document.createElement) {
      		  if(selected) {
              	checkBox = document.createElement("<input type='checkbox' id='" + name + "' name='" + name + "' value='" + value + "' onClick='" + callback_script +"' checked>");
              } else {
              	checkBox = document.createElement("<input type='checkbox' id='" + name + "' name='" + name + "' value='" + value + "' onClick='" + callback_script +"' >");
              }
      } else {
             checkBox = document.createElement("input");
	         checkBox.setAttribute("type", "checkbox");
	         checkBox.setAttribute("name", name);
	         checkBox.setAttribute("value",value);	 
	         checkBox.setAttribute("onClick", callback_script);
	         if(selected) checkBox.setAttribute("checked", "true");
      }
      return checkBox;
    }
    
// ==== AJAX FUNCS ========================================================================================== 

	var pendingAjaxCall = false;
	
	function getMapsByFeature(featureName) {
	  	var url = '../servlet/AdapterHTTP';
		var pars = 'ACTION_NAME=GET_MAPS_ACTION&featureName=' + featureName;
		
		var ajaxRequest = new Ajax.Request(url, 
									       { method: 'post', 
											 parameters: pars, 
											 onComplete: getMapsByFeatureCallback });
		pendingAjaxCall = true;
  	}
  
	function getMapsByFeatureCallback(req) {
		maps = new Array();
  		var childs = req.responseXML.getElementsByTagName("MAPS")[0].childNodes;
  		for (i = 0; i< childs.length; i++) {  		
	  		var value = childs[i].childNodes[0].nodeValue;
	  		maps[i] = value;
  		}
  		
  		// set also base map using a simple heuristic
		var preservePreviousMap = false;
		for(i = 0; i < maps.length; i++) {
			if(maps[i] == selectedMap) {
				preservePreviousMap = true;
				break;
			}
		}		
		if(!preservePreviousMap) selectedMap = maps[0];
		
		updateMaps();
  		  		
  		pendingAjaxCall = false;
  		draw();
  	}
  	
  	function getLayersInMap(mapName) {
  		var url = '../servlet/AdapterHTTP';
		var pars = 'ACTION_NAME=GET_LAYERS_ACTION&mapName=' + mapName;
		
		var ajaxRequest = new Ajax.Request(url, 
									       { method: 'post', 
											 parameters: pars, 
											 onComplete: getLayersInMapCallback });
		pendingAjaxCall = true;
  	}
  	
  	function getLayersInMapCallback(req) {
  		layers = new Array();
  		var newSelectedLayersMap = new Array();
  		
  		var childs = req.responseXML.getElementsByTagName("LAYERS")[0].childNodes;
  		for (i = 0; i< childs.length; i++) {  		
  			var value = childs[i].childNodes[0].nodeValue;
  			layers[i] = value;
  			newSelectedLayersMap[layers[i]] = selectedLayersMap[layers[i]];
  			if(layers[i] == selectedLevel) newSelectedLayersMap[layers[i]] = true;
  		}
  		
  		selectedLayersMap = newSelectedLayersMap;
  		
  		pendingAjaxCall = false;
  		draw();
  	}

	
	function windowCloseHandler(eventName, win){
	  	if(win.getId() == 'analysisDetailsWin') {
	  		var mapIFrame = document.getElementById("mapIFrame");
			//mapIFrame.style.display = 'inline';
			mapIFrame.style.visibility = 'visible';
			//var executionForm = document.getElementById('executionForm');
        	//executionForm.submit();
	  	}  	
  	}
  	
  	function refreshAnalysis() {
		var form = document.getElementById('optionForm');
		//form.action = 'http://www.google.it';
		form.action = actionUrl;
		form.submit();
	}
	
	function saveAnalysis() {
		var mapIFrame = document.getElementById("mapIFrame");
		//mapIFrame.style.display = 'none';
		mapIFrame.style.visibility = 'hidden';
	  	var url = "http://www.spagobi.org";
	  	url = "../servlet/AdapterHTTP?ACTION_NAME=SHOW_ANALYSIS_DETAILS_ACTION";
	  	url += "&selected_hierachy=" + selectedHierarchy;
	  	url += "&selected_hierarchy_level=" + selectedLevel;
	  	url += "&selected_map=" + selectedMap;
	  	url += "&selected_layers=" + selectedLayers;
	  	
	    var hierarchyWin = new Window("analysisDetailsWin", {className: "dialog", title: "Save Analysis", 
	                                              top:70, left:100, width:850, height:400, 
	                                              resizable: true, url: url })
		hierarchyWin.setDestroyOnClose();
		hierarchyWin.show(true); 						
  	}
// ==== HIERARCHIES ========================================================================================= 

	function drawHierarchiesBlock() {
		
		var hierarchyNameOpt = document.getElementById("hierarchyName");       
	    child = hierarchyNameOpt.firstChild;				      
		while(child) {
	        var nextChild = child.nextSibling;
			hierarchyNameOpt.removeChild(child);
			child = nextChild;
		}
	     
	    for(i = 0; i < hierarchies.length; i++) {
	    	var option = document.createElement("option");
	        option.setAttribute("id",hierarchies[i]);
	        option.setAttribute("value",hierarchies[i]);
	        if(hierarchies[i] == selectedHierarchy) {
	        	option.setAttribute("selected", "true");
	        }
	        var label = document.createTextNode(hierarchies[i]);
	        option.appendChild(label);
	        hierarchyNameOpt.appendChild(option);
	    }   
	}
	
	function updateHierarchySelection() {		
		var hierarchyNameOpt = document.getElementById("hierarchyName"); 
		var selectedValue = hierarchyNameOpt.options[hierarchyNameOpt.selectedIndex].value;
		selectedHierarchy = selectedValue;	
		
		updateHierarchy();
	}
	
	function updateHierarchy() {
			
		// set also base hierarchy level using a simple heuristic
		var preservePreviousLevel = false;
		for(i = 0; i < hierarchyLevels[selectedHierarchy].length; i++) {
			if(hierarchyLevels[selectedHierarchy][i] == selectedLevel) {
				preservePreviousLevel = true;
				break;
			}
		}		
		if(!preservePreviousLevel) {
			selectedLevel = hierarchyLevels[selectedHierarchy][0];
		}
		
		updateHierarchyLevel();
	}


// ==== HIERARCHY LEVELS ========================================================================================= 

	function drawHierarchyLevelsBlock() {
	
		var hierarchyLevelRadio = document.getElementById("hierarchyLevelRadio");     
		child = hierarchyLevelRadio.firstChild;    	      
		while(child) {
			var nextChild = child.nextSibling;
			hierarchyLevelRadio.removeChild(child);
			child = nextChild;
		}     
		
		
		for(i = 0; i < hierarchyLevels[selectedHierarchy].length; i++) {
			var selected = false;
		    if(hierarchyLevels[selectedHierarchy][i] == selectedLevel) {
		    	selected = true;
		    }
		    var radio = createRadio("level", hierarchyLevels[selectedHierarchy][i], selected, "updateHierarchyLevelSelection(this);draw();");
		    var label = document.createTextNode(" " + hierarchyLevels[selectedHierarchy][i]);
		    var lf = document.createElement("br");
		    hierarchyLevelRadio.appendChild(radio);
		    hierarchyLevelRadio.appendChild(label);
		    hierarchyLevelRadio.appendChild(lf);        
		}  
	}
	
	function updateHierarchyLevelSelection(obj) {				

		selectedLevel = obj.value;
		updateHierarchyLevel();
	}
	
	function updateHierarchyLevel() {				
		
		selectedHierarchyLevelIndex = -1;
		for(i = 0; i < hierarchyLevels[selectedHierarchy].length; i++) {
			if(hierarchyLevels[selectedHierarchy][i] == selectedLevel) selectedHierarchyLevelIndex = i;
		}
		if(selectedHierarchyLevelIndex == -1) alert("Error: " + selectedLevel + "is not a valid hierarchy level name !")
		
		selectedFeature = hierarchyFeatures[selectedHierarchy][selectedHierarchyLevelIndex];
		
		getMapsByFeature(selectedFeature);
	}
	
	
// ==== MAPS ========================================================================================= 

	function drawMapsBlock() {
	    var mapRadio = document.getElementById("mapRadio");      
	    child = mapRadio.firstChild;		  		      
		while(child) {
	        var nextChild = child.nextSibling;
			mapRadio.removeChild(child);
			child = nextChild;
		} 
	    for(i = 0; i < maps.length; i++) {
	    	var selected = false;
	    	if(selectedMap != null && maps[i] == selectedMap) {
	    		selected = true;
	    	}
	        var radio = createRadio("map", maps[i], selected, "updateMapsSelection(this);draw();");	        
	        var label = document.createTextNode(" " + maps[i]);
	        var lf = document.createElement("br");
	        mapRadio.appendChild(radio);
	        mapRadio.appendChild(label);
	        mapRadio.appendChild(lf);        
	    }      
	}
	
	function updateMapsSelection(obj) {
		selectedMap = obj.value;
		updateMaps();
	}
	
	function updateMaps() {
		getLayersInMap(selectedMap);
	}
	
	
	
// ==== MAPS ========================================================================================= 

	function drawLayersBlock() {
		var layerCheckBox = document.getElementById("layerCheckBox");      
	    child = layerCheckBox.firstChild;			      
		while(child) {
	        var nextChild = child.nextSibling;
			layerCheckBox.removeChild(child);
			child = nextChild;
		}     
		
	    for(i = 0; i < layers.length; i++) {
	         var selected = false;
	         if(selectedLayersMap != null) {
	         	selected = selectedLayersMap[layers[i]];
	         } else if(layers[i] == selectedFeature) {
	    	 	selected = true;
	    	 }
	    	 
	         var checkBox = createCheckBox("layer", layers[i], selected, "updateLayersSelection();draw();");	         
	         var label = document.createTextNode(" " + layers[i]);
	         var lf = document.createElement("br");
	         layerCheckBox.appendChild(checkBox);
	         layerCheckBox.appendChild(label);
	         layerCheckBox.appendChild(lf);       
	    }      
	}
	
	function updateLayersSelection(obj) {
		var selectedValue = "";
		selectedLayersMap = new Array();
		
		var layerCheckBox = document.getElementById("layerCheckBox");     
	    child = layerCheckBox.firstChild;    	      
		while(child) {
	        var nextChild = child.nextSibling;
			if(child != null && child.name == "layer" && child.checked) {
				selectedValue += child.value + ";";
				selectedLayersMap[child.value] = true;
			} else {
				selectedLayersMap[child.value] = false;
			}
			
			child = nextChild;
		}    		
		selectedLayers = selectedValue;
	}


