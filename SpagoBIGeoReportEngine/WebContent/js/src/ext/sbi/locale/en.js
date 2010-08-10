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
		
		boolean: {
			trueSymbol: 'true',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};

//===================================================================
// CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.georeport.controlpanel.title'] = 'Control Panel';
Sbi.locale.ln['sbi.georeport.layerpanel.title'] = 'Layers';
Sbi.locale.ln['sbi.georeport.analysispanel.title'] = 'Analysis';
Sbi.locale.ln['sbi.georeport.legendpanel.title'] = 'Legend';
Sbi.locale.ln['sbi.georeport.earthpanel.title'] = '3D Navigation';





//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.georeport.mappanel.title'] = 'Map';
