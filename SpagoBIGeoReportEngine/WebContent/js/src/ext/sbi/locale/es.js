Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

// once all the labels have been translated remove the following block

// < block START
Sbi.locale.unsupportedmsg = 'Sorry, spanish localization is not yet supported by SpagoBIGeoReportEngine.'
	+ 'In order to add spanish localization you can modify properly the file ' 
	+ 'SpagoBIGeoReportEngine/js/spagobi/locale/es.js translating text from english to spanish.'
	+ 'Once done please contribute it back to the project.';

Ext.Msg.show({
	   title:'Unimplemented functionality',
	   msg: Sbi.locale.unsupportedmsg,
	   buttons: Ext.Msg.OK,
	   icon: Ext.MessageBox.INFO
});
// block END >

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
Sbi.locale.ln['sbi.georeport.controlpanel.title'] = 'Navigation';
Sbi.locale.ln['sbi.georeport.layerpanel.title'] = 'Layers';
Sbi.locale.ln['sbi.georeport.analysispanel.title'] = 'Analysis';
Sbi.locale.ln['sbi.georeport.legendpanel.title'] = 'Legend';





//===================================================================
// MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.georeport.mappanel.title'] = 'Map';
