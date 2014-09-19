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
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Warning message';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Error message';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Info message';

//LookupField 
Sbi.locale.ln['sbi.lookup.Confirm'] = 'Confirm';
Sbi.locale.ln['sbi.lookup.Annulla'] = 'Cancel';
Sbi.locale.ln['sbi.lookup.Select']= 'Select Value ...';
Sbi.locale.ln['sbi.lookup.ValueOfColumn'] ='The value of the column';
Sbi.locale.ln['sbi.lookup.asA'] = 'as a';

Sbi.locale.ln['sbi.dm.execution.run.text'] = 'Run script';
Sbi.locale.ln['sbi.dm.execution.load.dataset.wait'] = 'Uploading your file...';
Sbi.locale.ln['sbi.dm.execution.load.dataset.ok'] = 'File successfully loaded';
Sbi.locale.ln['sbi.dm.execution.msg'] = 'Operation result';
Sbi.locale.ln['sbi.dm.execution.upload.btn'] = 'Upload';
Sbi.locale.ln['sbi.dm.execution.load.btn'] = 'Load';
Sbi.locale.ln['sbi.dm.execution.reset.btn'] = 'Reset';
Sbi.locale.ln['sbi.dm.execution.loading'] = 'Operation in progress...';
Sbi.locale.ln['sbi.dm.execution.save.btn'] = 'Save';
Sbi.locale.ln['sbi.dm.execution.wait'] = 'Uploading your file...';
Sbi.locale.ln['sbi.dm.execution.ok'] = 'Operation succeded';
