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

//=====================================================================================================
//Example
//=====================================================================================================

Sbi.locale.ln['sbi.generic.example'] = 'example';


Sbi.locale.ln['sbi.generic.label'] = 'Titre';
Sbi.locale.ln['sbi.generic.name'] = 'Nom';
Sbi.locale.ln['sbi.generic.descr'] = 'Déscription';
Sbi.locale.ln['sbi.generic.scope'] = 'Sujet';
Sbi.locale.ln['sbi.generic.scope.private'] = 'Privé';
Sbi.locale.ln['sbi.generic.scope.public'] = 'Public';
Sbi.locale.ln['sbi.generic.actions.save'] = 'Sauver';

Sbi.locale.ln['sbi.generic.wait'] = "Attendre...";
Sbi.locale.ln['sbi.generic.error'] = "Erreur générique";
Sbi.locale.ln['sbi.generic.success'] = "Succès";
Sbi.locale.ln['sbi.generic.operationSucceded'] = "Opération réussie";
Sbi.locale.ln['sbi.generic.query.SQL'] = "Requête SQL";
Sbi.locale.ln['sbi.generic.query.JPQL'] = "Requête JPLQ";