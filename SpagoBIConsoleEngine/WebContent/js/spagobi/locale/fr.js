/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

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
		
		timestamp: {
			dateFormat: 'm/Y/d H:i:s',
    		nullValue: ''
		},
		
		boolean: {
			trueSymbol: 'true',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};

//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Message d\'alerte';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Message d\'erreur';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Message d\'information'

Sbi.locale.ln['sbi.console.detailpage.title'] = 'Page de d\u00e9tails';
Sbi.locale.ln['sbi.console.consolepanel.title'] = 'Console';

//error / alarms window
Sbi.locale.ln['sbi.console.error.btnClose'] = 'Fermer';
Sbi.locale.ln['sbi.console.error.btnSetChecked'] = 'Marquer comme v\u00e9rifi\u00e9' //oppure: coch\u00e9 la case
Sbi.locale.ln['sbi.console.error.btnSetNotChecked'] = 'Marquer comme non v\u00e9rifi\u00e9';

//download window
Sbi.locale.ln['sbi.console.downloadlogs.title'] = 'Sp\u00e9cifiez les param\u00e8tres pour t\u00e9l\u00e9charger des fichiers de log'; 
Sbi.locale.ln['sbi.console.downloadlogs.initialDate'] = 'Date de d\u00e9part';
Sbi.locale.ln['sbi.console.downloadlogs.finalDate'] = 'Date d\'arriv\u00e9e';
Sbi.locale.ln['sbi.console.downloadlogs.initialTime'] = 'Temps de d\u00e9part';
Sbi.locale.ln['sbi.console.downloadlogs.finalTime'] = 'Temps d\'arriv\u00e9e';
Sbi.locale.ln['sbi.console.downloadlogs.path'] = 'Chemin';
Sbi.locale.ln['sbi.console.downloadlogs.btnClose'] = 'Fermer';
Sbi.locale.ln['sbi.console.downloadlogs.btnDownload'] = 'T\u00e9l\u00e9charger';
Sbi.locale.ln['sbi.console.downloadlogs.initialDateMandatory'] = 'La date de d\u00e9part est obligatoire';
Sbi.locale.ln['sbi.console.downloadlogs.finalDateMandatory'] = 'La date d\'arriv\u00e9e est obligatoire';
Sbi.locale.ln['sbi.console.downloadlogs.initialTimeMandatory'] = 'Le temps de d\u00e9par est obligatoire';
Sbi.locale.ln['sbi.console.downloadlogs.finalTimeMandatory'] = 'Le temps d\'arriv\u00e9e est obligatoire';
Sbi.locale.ln['sbi.console.downloadlogs.pathsMandatory'] = 'Le chemin de t\u00e9l\u00e9chargement est obligatoire' //da verificare
Sbi.locale.ln['sbi.console.downloadlogs.rangeInvalid'] = '\u00c9ventail incorrecte des dates'
//propmtables window
Sbi.locale.ln['sbi.console.promptables.btnOK'] = 'OK';
Sbi.locale.ln['sbi.console.promptables.btnClose'] = 'Fermer';
Sbi.locale.ln['sbi.console.promptables.lookup.Annulla'] = 'Annuler';
Sbi.locale.ln['sbi.console.promptables.lookup.Confirm'] = 'Confirmer';
Sbi.locale.ln['sbi.console.promptables.lookup.Select'] = 'S\u00e9lectionner';
//internationalization
Sbi.locale.ln['sbi.console.localization.columnsKO'] = 'Erreur lors de la internationalizzation. V\u00e9rifiez l\'ensemble des donn\u00e9es!';
