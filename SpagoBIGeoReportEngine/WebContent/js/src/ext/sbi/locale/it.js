/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.locale");


Sbi.locale.ln = Sbi.locale.ln || new Array();

Sbi.locale.formats = {
		/*
		number: {
			decimalSeparator: ',',
			decimalPrecision: 2,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '€',
			nullValue: ''
		},
		*/
		float: {
			decimalSeparator: ',',
			decimalPrecision: 2,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '€',
			nullValue: ''
		},
		int: {
			decimalSeparator: ',',
			decimalPrecision: 0,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '€',
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
			dateFormat: 'd/m/Y',
    		nullValue: ''
		},
		
		boolean: {
			trueSymbol: 'vero',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};

//===================================================================
//CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Pannello di controllo';
Sbi.locale.ln['sbi.geo.earthpanel.title'] = 'Navigazione 3D';
Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Livelli';

Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'Analisi';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'Aggiungi indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 	'Seleziona un indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'Metodo';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'Numero di classi';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'Dal colore';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'Al colore';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'Set Default';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'Valori di default valorizzati correttamente';

Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'Legenda';




//===================================================================
//MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Mappa';