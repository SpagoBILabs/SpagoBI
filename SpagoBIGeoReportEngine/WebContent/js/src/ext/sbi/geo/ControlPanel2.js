/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 
Ext.ns("Sbi.geo");


/**
 * Every time you create a new class add it to the following files:
 *  - importSbiJS.jspf
 *  - ant-files/SpagoBI-2.x-source/SpagoBIProject/ant/build.xml
 */
Sbi.geo.ControlPanel2 = function(config) {
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	// init properties...
	var defaultSettings = {
		// set default values here
		//title       : LN('sbi.geo.controlpanel.title'),
		region      : 'east',
		split       : false,
		width       : 365,
		collapsible : true,
		collapsed   : false,
		autoScroll	: true,
		layout		: 'fit',
		margins     : '0 0 0 0',
		cmargins    : '0 0 0 0',
		collapseMode: 'none',
        hideCollapseTool: true,
		hideBorders: true,
		border		: false,
		frame: false,
		id:'controlPanel',
		singleSelectionIndicator: true
	};
	
	if (Sbi.settings && Sbi.settings.geo && Sbi.settings.geo.ControlPanel2) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.geo.ControlPanel2);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	// init events...
	//this.addEvents();
	
	this.initServices();
	this.init();

	c = Ext.apply(c, {
	     items: this.innerPanel 
	});
	
	// constructor
    Sbi.geo.ControlPanel2.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.geo.ControlPanel2
 * @extends Ext.Panel
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.geo.ControlPanel2, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	/**
	 * @method 
	 * 
	 * Controls that the configuration object passed in to the class constructor contains all the compulsory properties. 
	 * If it is not the case an exception is thrown. Use it when there are properties necessary for the object
	 * construction for whom is not possible to find out a valid default value.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the config object received as input
	 */
	, validateConfigObject: function(config) {
		
	}
	

	/**
	 * @method 
	 * 
	 * Modify the configuration object passed in to the class constructor adding/removing properties. Use it for example to 
	 * rename a property or to filter out not necessary properties.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the modified version config object received as input
	 * 
	 */
	, adjustConfigObject: function(config) {
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - none
	 */
	, initServices: function() {
//		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
//		
//		this.services = this.services || new Array();
//		
//		this.services['exampleService'] = this.services['exampleService'] || Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXAMPLE_ACTION'
//			, baseParams: params
//		});	
	}


	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.isFinalUser = (Sbi.template.role.indexOf('user') >= 0);
		this.isOwner = (Sbi.config.userId === Sbi.config.docAuthor)?true:false;
		this.isInsertion = (Sbi.config.docLabel === '')?true:false;

		this.innerPanel = new Ext.Panel({
			layout: 'fit', 
			autoScroll: true,
			html: ' <main class="main main-map" id="main"> ' +
					    '<div id="panel" class="panel">' +
					    	'<form class="panel-form" action="#" method="post">' +
					            '<div class="scroll" id="scroll">' +
					               '<div class="scroll-content">' +  
					               		this.getMapTypeDiv() + 			
					               		this.getIndicatorsDiv() +
					                    this.getPermissionDiv() + 
					                '</div>' +
					            '</div>' + this.getPanelButtonsDiv() + 
					        '</form>' +
					    '</div>' +
					'</main>'
		});
		
		this.innerPanel.on('render', function() {			
			this.initInnerPannelCallbacks.defer(2000, this);
		}, this);
	}
	
	
	, initInnerPannelCallbacks: function() {
		var thisPanel = this;
		//alert('initInnerPannelCallbacks');
		var elAuthorBtn = Ext.get("authorButton");
		if(elAuthorBtn && elAuthorBtn !== null) {
			elAuthorBtn.on('click', function() {
				//alert('xxx');
			});
			//alert('Registered handler on element [authorButton]');
		} else {
			//alert('Impossible to find element [authorButton]');
		}
		
		var elFeedbackMail = Ext.get("feedback_mail");
		if(elFeedbackMail && elFeedbackMail !== null) {
			elFeedbackMail.on('click', function() {
				this.showFeedbackWindow();
			},this);
			//alert('Registered handler on element [feedback_mail]');
		} else {
			//alert('Impossible to find element [feedback_mail]');
		}
		
		var elPermissions1 = Ext.get("permissions-1");
		if(elPermissions1 && elPermissions1 !== null) {
			elPermissions1.on('click', function() {
				//alert("permissions-1 "+ el.getValue());
				var el1 =  Ext.get("div-perm1");
				var el2 =  Ext.get("div-perm2");
				Ext.fly(el2).removeClass('checked');
				Ext.fly(el1).addClass('checked');
			},this);
			//alert('Registered handler on element [permission-1]');
		} else {
			//alert('Impossible to find element [permission-1]');
		}
		
		var elPermissions2 = Ext.get("permissions-2");
		if(elPermissions2 && elPermissions2 !== null) {
			elPermissions2.on('click', function() {				
				var el1 =  Ext.get("div-perm1");
				var el2 =  Ext.get("div-perm2");
				Ext.fly(el1).removeClass('checked');
				Ext.fly(el2).addClass('checked');
			},this);
			//alert('Registered handler on element [permission-2]');
		} else {
			//alert('Impossible to find element [permission-2]');
		}		
		var flyUlEl = Ext.select('.map-type');
		var elMapZone = Ext.get("li-map-zone");
		if(elMapZone && elMapZone !== null) {
			elMapZone.on('click', function() {
					this.refreshList(elMapZone, flyUlEl);					
			}, thisPanel);
		}				
		var elMapComparation = Ext.get("li-map-comparation");
		
		if(elMapComparation && elMapComparation !== null) {
			elMapComparation.on('click', function() {						
					this.refreshList(elMapComparation, flyUlEl);					
			}, thisPanel);
		}
		var elMapPoint = Ext.get("li-map-point");
		if(elMapPoint && elMapPoint !== null) {
			elMapPoint.on('click', function() {
					this.refreshList(elMapPoint, flyUlEl);					
			}, thisPanel);
		}
		var elMapHeat = Ext.get("li-map-heat");
		if(elMapHeat && elMapHeat !== null) {
			elMapHeat.on('click', function() {
					this.refreshList(elMapHeat, flyUlEl);					
			}, thisPanel);
		}
		
		var closeMapH = null;
		var openMapH = null;
		var elMapType = Ext.get("mapType");	
		if(elMapType && elMapType !== null) {
			elMapType.on('click', function() {				
				
				var el1 =  Ext.get("mapType");
				
				if (closeMapH == null){
					closeMapH =  Ext.fly(el1).getHeight();
					openMapH = closeMapH*4;
				}
				if (Ext.fly(el1).hasClass('open')){
					Ext.fly(el1).dom.style.height = closeMapH-1; 
					Ext.fly(el1).removeClass('open');
				}else{
					Ext.fly(el1).dom.style.height = openMapH; 
					Ext.fly(el1).addClass('open');
				}
			});
		} else {
			//alert('Impossible to find element [maptype]');
		}
		
		var elBtnNewMap = Ext.get("btn-new-map");
		if(elBtnNewMap && elBtnNewMap !== null) {
			elBtnNewMap.on('click', function() {
					this.showSaveWindow(true);
			}, this);
		}
		
		var elAddIndicator = Ext.get("addIndicatorButton");
		if(elAddIndicator && elAddIndicator !== null) {
			elAddIndicator.on('click', function() {
				this.showMeasureCatalogueWindow();
			},this);
			//alert('Registered handler on element [feedback_mail]');
		} else {
			alert('Impossible to find element [addIndicatorButton]');
		}
		
		
		var elBtnModifyMap = Ext.get("btn-modify-map");
		if(elBtnModifyMap && elBtnModifyMap !== null) {
			elBtnModifyMap.on('click', function() {					
					this.showSaveWindow(false);
			}, this);
		}
		
		//Initialize thematizerControlPanel form state
		this.thematizerControlPanel.on('ready', function(){
			Sbi.debug("[AnalysisControlPanel]: [ready] event fired");
			this.setAnalysisConf( this.thematizerControlPanel.analysisConf );
		}, this);		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	, showFeedbackWindow: function(){
		if(this.feedbackWindow != null){			
			this.feedbackWindow.destroy();
			this.feedbackWindow.close();
		}
		
		this.messageField = new Ext.form.TextArea({
			fieldLabel: 'Message text',
            width: '100%',
            name: 'message',
            maxLength: 2000,
            height: 100,
            autoCreate: {tag: 'textArea', type: 'text',  autocomplete: 'off', maxlength: '2000'}
		});
		
		this.sendButton = new Ext.Button({
			xtype: 'button',
			handler: function() {
				var msgToSend = this.messageField.getValue();
				sendMessage({'label': Sbi.config.docLabel, 'msg': msgToSend},'sendFeedback');
       		},
       		scope: this ,
       		text:'Send',
	        width: '100%'
		});

		
		var feedbackWindowPanel = new Ext.form.FormPanel({
			layout: 'form',
			defaults: {
	            xtype: 'textfield'
	        },

	        items: [this.messageField,this.sendButton]
		});
		
		
		this.feedbackWindow = new Ext.Window({
            layout      : 'fit',
	        width		: 700,
	        height		: 170,
            closeAction :'destroy',
            plain       : true,
            title		: 'Send Feedback',
            items       : [feedbackWindowPanel]
		});
		
		this.feedbackWindow.show();
	}
	
	, getPermissionDiv: function(){
		var toReturn = '';
		
//		if (!this.isFinalUser){
		if (Sbi.config.userId === Sbi.config.docAuthor || !this.isFinalUser){
			toReturn = '<div class="map-permissions">' +
		    	'<div class="radio">' +
		        	'<span class="label">Questa mappa è:</span>' ;
		
			if (Sbi.config.docIsPublic == 'false'){
				toReturn += '' +
					'<div  id="div-perm1" class="radio-option checked">' +
			        	'<input id="scopePrivate" type="radio" name="permissions" value="0" checked />' +
			            '<label for="permissions-1">Privata</label>' +
		            '</div>' +
		            '<div  id="div-perm2" class="radio-option ">' +
			        	'<input id="scopePublic" type="radio" name="permissions" value="1" />' +
			            '<label for="permissions-2">&nbsp;Pubblica</label>' +
			        '</div>';
			}else{
				toReturn += '' +
					'<div id="div-perm1" class="radio-option ">' +
			        	'<input id="scopePrivate" type="radio" name="permissions" value="0"  />' +
			            '<label for="permissions-1">Privata</label>' +
		            '</div>' +
		            '<div id="div-perm2" class="radio-option checked">' +
			        	'<input id="scopePublic" type="radio" name="permissions" value="1" checked />' +
			            '<label for="permissions-2">&nbsp;Pubblica</label>' +
			        '</div>';
			}
			strChecked = ''; 
			if (Sbi.config.docIsVisible == 'true' || this.isInsertion ){
				strChecked = 'checked';
			}
			toReturn += '' +
				'<div  id="div-perm3" class="radio-option checked">' +
		        	'<input id="visibility" type="checkbox" value="1" ' +strChecked +' />' +
		            '<label for="permissions-1">Visibile</label>' +
	            '</div>' ;		           
			toReturn += '' +
		        '</div>' +
	        '</div>' ;
		}
		return toReturn;
	}
	
	, getPanelButtonsDiv: function(){
		var toReturn = '' ;
				
		if (this.isOwner){
			toReturn += ''+
				'<!-- // Mapper modify own map -->' +
		        '<div class="panel-buttons-container map-owner">' +
		             '<div class="panel-buttons">' +
		                 //'<a href="#" class="btn-2">Annulla</a>' +
		                 '<input type="submit" id="btn-cancel" class="btn-2" value="Annulla" />' +
//		                 '<input type="submit" id="btn-modify-map" class="btn-1" value="Aggiorna" />' +
		                 '<a href="#" id="btn-modify-map" class="btn-1">Aggiorna</a>'  +
		             '</div>' +
		             '<p>salva <a  id="btn-new-map" href="#">nuova mappa</a></p>' +
		         '</div>';
		}else if (!this.isInsertion){
			toReturn += ''+
			     '<!-- // Mapper modify sombody else map -->' +
			     '<div class="panel-buttons-container">' +
			         '<div class="panel-buttons">' +
//			             '<a href="#" class="btn-2">Annulla</a>' +
			             '<input type="submit" id="btn-cancel" class="btn-2" value="Annulla" />' +
			             '<a href="#" id="btn-new-map" class="btn-1">Salva nuova mappa</a>'  +
//			             '<input type="submit" id="btn-new-map" class="btn-1" value="Salva nuova mappa" />' +
			         '</div>' +
			     '</div>';
		}else if (this.isInsertion){
			toReturn += ''+
				'<!-- // Mapper new map -->' +
		         '<div id="panel-buttons-container" class="panel-buttons-container">' +
		             '<div class="panel-buttons">	' +
//		                 '<input type="submit" id="btn-modify-map" class="btn-1" value="salva" />' +
		                 '<a href="#" id="btn-new-map" class="btn-1">Salva</a>'  +
		             '</div>' +
		         '</div>';
		}
		
		return toReturn;
	}
	
	
	, getMapTypeDiv: function(){
		var toReturn = '' +
		 '<div class="map-description">' +
//	         '<h1 class="titleButton">'+Sbi.config.docName+'</h1>' +
	         '<input  type="text" id="docName" class="mapTitle" value="'+Sbi.config.docName+'" /> '+
//	         '<p>'+Sbi.config.docDescription+'</p>' +
	         '<textarea rows="2" cols="40" id="docDesc" class="mapDescription" />'+Sbi.config.docDescription+' </textarea>'+	         
	 		'<p id="author" class="published">Pubblicata da <a id="authorButton" class="authorButton" href="#">'+Sbi.config.docAuthor+'</a> <span class="separator">/</span> <a id="feedback_mail" href="#" class="feedback">invia feedback</a></p>' +
	     '</div>' +
	     '<ul id="mapType" class="map-type">' +
	     	'<li id="li-map-zone" class="map-zone active"><a href="#">Mappa a <span>zone</span><span class="arrow"></span></a></li>' +
	        '<li id="li-map-comparation" class="map-comparation"><a  href="#">Mappa di <span>comparazione</span></a></li>' +
	        '<li id="li-map-point" class="map-point"><a href="#">Mappa <span>puntiforme</span></a></li>' +
	        '<li id="li-map-heat" class="map-heat last"><a href="#">Mappa di <span>calore</span></a></li>' +
	     '</ul>' ;
		
		return toReturn;
	}
	
	, getIndicatorsDiv: function(){
		if ( this.thematizerControlPanel.indicators != null &&  this.thematizerControlPanel.indicators !== undefined){
			
			var toReturn = '' +
			'<div class="indicators">' +
		    	'<h2>Indicatori</h2>' +
		        '<ul id="ul-indicators" class="group">';		
				for(var i=0; i< this.thematizerControlPanel.indicators.length; i++){
					var indEl = this.thematizerControlPanel.indicators[i];
					var clsName = (i==0)?'first':'disabled';
					toReturn += ''+
					'<li class="'+clsName+'" id="indicator'+i+'"><span class="button">'+
						'<a href="#" class="tick" onclick="javascript:Ext.getCmp(\'controlPanel\').indicatorSelected(\'indicator'+i+'\',\''+indEl[0]+'\');"></a>'+ indEl[1]+
		            '</li>' ;	
				}
		       toReturn +=''+
		       	'</ul>' +
		        '<span id="addIndicatorButton" class="btn-2">Aggiungi</span>' +
		    '</div>';
			
			return toReturn;
		}
	}
	
	, refreshList: function(el, list){
		if (el.id != list.item(0).first().id){
			var items = Ext.query('.active');
			Ext.each(items, function (item) {
		        item = Ext.get(item);
		        item.removeClass('active');
		        item.addClass('last');	        
		    }, this);
							
			//change position of arrow
			var dh = Ext.DomHelper;								
			Ext.fly(el).addClass('active');
			var lItems = Ext.fly(el).down('a');
			dh.append(lItems, '<span class=\"arrow\" />');
			//refresh the list items (move the selected elem as first)
			var len = list.item(0).dom.childElementCount;
			var newList = [el.dom];
			for(var z=0;z<len;z++){
				var optEl =list.item(0).dom.childNodes[z];							
				if (optEl !== undefined && el.id != optEl.id){
					newList.push(optEl);
				}
			}
			//clear			
			this.clearList(list);
			//add
			this.addElemsToList(list, newList);

		}
	}
	
	, clearList: function(list){
		for(var z=0;z<list.item(0).dom.childElementCount;z++){				
			list.item(0).dom.childNodes[z].remove();
		}
	}
	
	, addElemsToList: function(list, elems){
		for(var z=0;z<elems.length;z++){				
			list.item(0).appendChild(elems[z]);
		}
	}
	
	, openIndicatorDetail: function(el){
		alert("openIndicatorDetail: " + el);
	}
	
	, setAnalysisConf: function(analysisConf) {
		//This inizialize the required options for thematizerControlPanel
		Sbi.debug("[ControlPanel2.setAnalysisConf]: IN");
		
		Sbi.debug("[ControlPanel2.setAnalysisConf]: analysisConf = " + Sbi.toSource(analysisConf));
		
		var formState = Ext.apply({}, analysisConf || {});
		
		formState.method = formState.method || 'CLASSIFY_BY_QUANTILS';
		formState.classes =  formState.classes || 5;
		
		formState.fromColor =  formState.fromColor || '#FFFF99';
		formState.toColor =  formState.toColor || '#FF6600';
	
		if(formState.indicator && this.indicatorContainer === 'layer') {
			formState.indicator = formState.indicator.toUpperCase();
		}
		if(!formState.indicator && this.thematizerControlPanel.indicators && this.thematizerControlPanel.indicators.length > 0) {
			formState.indicator = this.thematizerControlPanel.indicators[0][0];
		}
		
		this.thematizerControlPanel.setFormState(formState, true);
		
		Sbi.debug("[ControlPanel2.setAnalysisConf]: OUT");
	}
	
	, indicatorSelected: function(elementId, indicator){
		
		//Set selected indicator in the thematizerControlPanel form state
		var geostasticFormState = this.thematizerControlPanel.getFormState();
		var currentIndicator = geostasticFormState.indicator;
		geostasticFormState.indicator = indicator;
		this.thematizerControlPanel.setFormState(geostasticFormState, true); //<- this will update the thematizer of the map
		//*****************************************
		
		
		var el = Ext.get(elementId);
		if ((el != null) && (el !== undefined )){
			var currentClass = el.dom.className;
			//single selection / multiple selection management 
			if (this.singleSelectionIndicator == true){
				if (currentClass == 'disabled'){

					var indicatorsUl = Ext.get('ul-indicators').dom.childNodes;
					//enable this element and disable all others
					for(var i=0; i< indicatorsUl.length; i++){
						if (indicatorsUl[i].id == elementId){
							indicatorsUl[i].className = 'first';
						} else {
							indicatorsUl[i].className = 'disabled';
						}
					}
					
				} else {
					//disable the only active indicator
					el.dom.className = 'disabled';
				}
			} else {
				if (currentClass == 'first'){
					el.dom.className = 'disabled';
				} else {
					el.dom.className = 'first';
				}
			}

		}
	}
	, showMeasureCatalogueWindow: function(){
		if(this.measureCatalogueWindow==null){
			var measureCatalogue = new Sbi.geo.tools.MeasureCatalogue();
			measureCatalogue.on('storeLoad', this.onStoreLoad, this);
			
			this.measureCatalogueWindow = new Ext.Window({
	            layout      : 'fit',
		        width		: 700,
		        height		: 350,
	            closeAction :'hide',
	            plain       : true,
	            title		: OpenLayers.Lang.translate('sbi.tools.catalogue.measures.window.title'),
	            items       : [measureCatalogue]
			});
		}
		
		
		this.measureCatalogueWindow.show();
	}
	
	, showSaveWindow: function(isInsert){

		if(this.saveWindow != null){			
			this.saveWindow.destroy();
			this.saveWindow.close();
		}

		var template = this.controlledPanel.validate();	
		if (template == null) {
    		alert("Impossible to get template");
    		return;
    	}
    	
    	Sbi.debug('[ControlPanel.showSaveWindow]: ' + template);
    	
		var documentWindowsParams = {				
				'OBJECT_TYPE': 'MAP',
				'OBJECT_TEMPLATE': template,
				'typeid': 'GEOREPORT' 
		};
		
		var formState = {};
		//gets the input values (name, desccription,..)
		var el = Ext.get('docName');
		if ((el != null) && (el !== undefined ) && (el.getValue() !== '' )){
			formState.docName = el.getValue();
		}/*else{
			alert('Nome documento obbligatorio');
		}*/
		var el = Ext.get('docDesc');
		if ((el != null) && (el !== undefined )){
			formState.docDescr = el.getValue();
		}
		var el = Ext.get('scopePublic')
		if ((el != null) && (el !== undefined )){
			formState.scope = (el.dom.checked)?"true":"false";			
		}else{
			formState.scope = "false"; //default
		}
		var el = Ext.get('visibility');
		if ((el != null) && (el !== undefined )){
			if (isInsert){
				formState.visibility = true;
			}else{
				formState.visibility = el.dom.checked; //default
			}
		}
		
		formState.docFunctionalities  = Sbi.config.docFunctionalities;
		
		if (isInsert){
			formState.docLabel = 'map__' + Math.floor((Math.random()*1000000000)+1); 
			if (Sbi.config.docDatasetLabel) 
				documentWindowsParams.dataset_label= Sbi.config.docDatasetLabel;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_DATASET';
		}else{
			formState.docLabel = Sbi.config.docLabel;
			documentWindowsParams.MESSAGE_DET= 'MODIFY_GEOREPORT';	
		}
		documentWindowsParams.formState = formState;		
		
		this.saveWindow = new Sbi.service.SaveDocumentWindowExt(documentWindowsParams);
		this.saveWindow.show();

		}
		
		
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
	//this.addEvents(
	/**
     * @event eventone
     * Fired when ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Ext.Toolbar} ...
     */
	//'eventone'
	/**
     * @event eventtwo
     * Fired before ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Object} ...
     */
	//'eventtwo'
	//);	
});