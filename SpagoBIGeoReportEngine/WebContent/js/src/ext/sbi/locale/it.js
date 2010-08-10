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
Sbi.locale.ln['sbi.georeport.controlpanel.title'] = 'Pannello di controllo';
Sbi.locale.ln['sbi.georeport.earthpanel.title'] = 'Navigazione 3D';
Sbi.locale.ln['sbi.georeport.layerpanel.title'] = 'Livelli';
Sbi.locale.ln['sbi.georeport.analysispanel.title'] = 'Analisi';
Sbi.locale.ln['sbi.georeport.legendpanel.title'] = 'Legenda';



//===================================================================
//MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.georeport.mappanel.title'] = 'Mappa';