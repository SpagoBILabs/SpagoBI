/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

//once all the labels have been translated remove the following block

//< block START
Sbi.locale.unsupportedmsg = 'Sorry, french localization is not yet supported by SpagoBIGeoReportEngine.'
	+ 'In order to add fench localization you can modify properly the file ' 
	+ 'SpagoBIGeoReportEngine/js/spagobi/locale/fr.js translating text from english to french.'
	+ 'Once done please contribute it back to the project.';

Ext.Msg.show({
	   title:'Unimplemented functionality',
	   msg: Sbi.locale.unsupportedmsg,
	   buttons: Ext.Msg.OK,
	   icon: Ext.MessageBox.INFO
});
//block END >

Sbi.locale.formats = {
		/*
		number: {
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		*/
		
		float: {
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		int: {
			decimalSeparator: '.',
			decimalPrecision: 0,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		
		string: {
			trim: true,
    		maxLength: null,
    		ellipsis: true,
    		changeCase: null, // null | 'capitalize' | 'uppercase' | 'lowercase'
    		//prefix: '',
    		//suffix: '',
    		nullValue: ''
		},
		
		date: {
			dateFormat: 'm/Y/d',
    		nullValue: ''
		},
		
		boolean: {
			trueSymbol: 'true',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};

//===================================================================
// CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Navigation';
Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Niveaux';

Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'Analyse';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'Ajouter indicateurs';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indicateur';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 	'Choisir un indicateur';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'M\u00e9thode';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'Nombre de classes';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'De cette couleur';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'A cette couleur';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'Set Default';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'Les valeurs par d\u00e9faut d\u00e9finies correctement';

Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'L\u00E9gende';
Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Change style'; 
Sbi.locale.ln['sbi.geo.earthpanel.title'] = 'Navigation 3D';

//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.savewin.title'] = 'Ins\u00e9rez plus de d\u00e9tails et enregistrer votre document... ';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.name'] = 'Nom';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.description'] = 'Description';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.visibility'] = 'Visibilit\u00e9 du document';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.previewfile'] = 'Aper\u00e7u du fichier';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.community'] = 'Community';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.scope'] = 'Scope';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.saveWarning']  = 'Avant de enregistrer il est n\u00e9cessaire ins\u00e9rer le nom de la carte et s\u00e9lectionnez aumoin un dossier.';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = ' ';

//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Carte';

//===================================================================
//OPENLAYERS
//===================================================================
Sbi.locale.ln['mf.print.mapTitle'] =  'Titre';
Sbi.locale.ln['mf.print.comment'] =  'Commentaires';
Sbi.locale.ln['mf.print.loadingConfig'] =  'Chargement de la configuration...';
Sbi.locale.ln['mf.print.serverDown'] =  'Le service impression semble pas fonctionner';
Sbi.locale.ln['mf.print.unableToPrint'] =  "Impossible d'imprimer";
Sbi.locale.ln['mf.print.generatingPDF'] =  "G\u00e9n\u00e9rer un PDF...";
Sbi.locale.ln['mf.print.dpi'] =  'DPI';
Sbi.locale.ln['mf.print.scale'] =  'Scale';
Sbi.locale.ln['mf.print.rotation'] =  'Rotation';
Sbi.locale.ln['mf.print.print'] =  'Imprimer';
Sbi.locale.ln['mf.print.resetPos'] =  'Reset Pos.';
Sbi.locale.ln['mf.print.layout'] =  'Layout';
Sbi.locale.ln['mf.print.addPage'] =  'Ajouter une page';
Sbi.locale.ln['mf.print.remove'] =  'Retirez la page';
Sbi.locale.ln['mf.print.clearAll'] =  'Effacer tout';
Sbi.locale.ln['mf.print.popupBlocked'] =  'Fen\u00eatres pop-up bloqu\u00e9es par votre navigateur.<br />' +
                       '<br />Utilisez ce lien pour t\u00e9l\u00e9charger le document] = ';
Sbi.locale.ln['mf.print.noPage'] =  'Aucune page s\u00e9lectionn\u00e9e; cliquez sur le bouton "Ajouter une page" pour en ajouter une.';
Sbi.locale.ln['mf.error'] =  'Erreur';
Sbi.locale.ln['mf.warning'] =  'Warning';
Sbi.locale.ln['mf.information'] =  'Information';
Sbi.locale.ln['sbi.tools.catalogue.measures.measure.properties'] = 'Propri\u00e9t\u00e9s Mesure';
Sbi.locale.ln['sbi.tools.catalogue.measures.dataset.properties'] = 'Propri\u00e9t\u00e9s Jeu de donn\u00e9es';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Join';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Ex\u00e9cuter jointure entre les mesures choisies';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.tooltip'] = 'Ouvrez le cadre de s\u00e9lection';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.btn'] = 'S\u00e9lection';
Sbi.locale.ln['sbi.tools.catalogue.measures.window.title'] =  'Catalogue mesures';
Sbi.locale.ln['error.mesage.description.measure.join.no.common.dimension'] = 'Impossible d\'ex\u00e9cuter la jointure entre les mesures. L\'ensembles de donn\u00e9es associ\u00e9s n\'ont aucune dimension en commun.';
Sbi.locale.ln['error.mesage.description.measure.join.no.complete.common.dimension'] = 'Impossible d\ex\u00e9cuter la jointure entre les mesures. Les ensembles de donn\u00e9es associ\u00e9s n\'ont aucune dimension compl\u00e8te en commun.';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.alias'] = 'Alias';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsName'] = 'Nom';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsLabel'] = '\u00e9tiquette';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsCategory'] = 'cat\u00e9gorie';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsType'] = 'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.label'] = 'Label';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.descr'] = 'Description';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.type'] = 'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.baseLayer'] = 'Couche de base';

