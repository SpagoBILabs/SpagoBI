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
Sbi.locale.ln['sbi.console.messagewin.warning.title'] = 'Warning message';
Sbi.locale.ln['sbi.console.messagewin.error.title'] = 'Error message';
Sbi.locale.ln['sbi.console.messagewin.info.title'] = 'Info message';

Sbi.locale.ln['sbi.console.detailpage.title'] = 'Detail Page';
Sbi.locale.ln['sbi.console.consolepanel.title'] = 'Console';

//error / alarms window
Sbi.locale.ln['sbi.console.error.btnClose'] = 'Close';
Sbi.locale.ln['sbi.console.error.btnSetChecked'] = 'Marck as checked';
Sbi.locale.ln['sbi.console.error.btnSetNotChecked'] = 'Mark as not checked';

//download window
Sbi.locale.ln['sbi.console.downloadlogs.title'] = 'Specify parameters to download log files:';
Sbi.locale.ln['sbi.console.downloadlogs.initialDate'] = 'Initial date';
Sbi.locale.ln['sbi.console.downloadlogs.finalDate'] = 'Final date';
Sbi.locale.ln['sbi.console.downloadlogs.initialTime'] = 'Initial time';
Sbi.locale.ln['sbi.console.downloadlogs.finalTime'] = 'Final time';
Sbi.locale.ln['sbi.console.downloadlogs.path'] = 'Path';
Sbi.locale.ln['sbi.console.downloadlogs.btnClose'] = 'Close';
Sbi.locale.ln['sbi.console.downloadlogs.btnDownload'] = 'Download';
Sbi.locale.ln['sbi.console.downloadlogs.initialDateMandatory'] = 'The start date is mandatory';
Sbi.locale.ln['sbi.console.downloadlogs.finalDateMandatory'] = 'The final date is mandatory';
Sbi.locale.ln['sbi.console.downloadlogs.initialTimeMandatory'] = 'The initial time is mandatory';
Sbi.locale.ln['sbi.console.downloadlogs.finalTimeMandatory'] = 'The final time is mandatory';
Sbi.locale.ln['sbi.console.downloadlogs.pathsMandatory'] = 'The download path is mandatory'
Sbi.locale.ln['sbi.console.downloadlogs.rangeInvalid'] = 'Incorrect range of date'
//propmtables window
Sbi.locale.ln['sbi.console.promptables.btnOK'] = 'OK';
Sbi.locale.ln['sbi.console.promptables.btnClose'] = 'Close';
Sbi.locale.ln['sbi.console.promptables.lookup.Annulla'] = 'Cancel';
Sbi.locale.ln['sbi.console.promptables.lookup.Confirm'] = 'Confirm';
Sbi.locale.ln['sbi.console.promptables.lookup.Select'] = 'Select';
//internationalization
Sbi.locale.ln['sbi.console.localization.columnsKO'] = 'Error during the internationalizzation. Check the dataset!';