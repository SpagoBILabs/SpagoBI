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
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.console.messagewin.warning.title'] = 'Warning';
Sbi.locale.ln['sbi.console.messagewin.error.title'] = 'Messaggio di Errore';
Sbi.locale.ln['sbi.console.messagewin.info.title'] = 'Informazione';

Sbi.locale.ln['sbi.console.detailpage.title'] = 'Dettaglio Pagina';
Sbi.locale.ln['sbi.console.consolepanel.title'] = 'Console';

//error / alarms window
Sbi.locale.ln['sbi.console.error.btnClose'] = 'Chiudi';
Sbi.locale.ln['sbi.console.error.btnSetChecked'] = 'Visionato';
Sbi.locale.ln['sbi.console.error.btnSetNotChecked'] = 'Non visionato';

	//downalod window
Sbi.locale.ln['sbi.console.downloadlogs.title'] = 'Specifica i parametri per il download dei files:';
Sbi.locale.ln['sbi.console.downloadlogs.initialDate'] = 'Data inizio';
Sbi.locale.ln['sbi.console.downloadlogs.finalDate'] = 'Data fine';
Sbi.locale.ln['sbi.console.downloadlogs.initialTime'] = 'Ora inizio';
Sbi.locale.ln['sbi.console.downloadlogs.finalTime'] = 'Ora fine';
Sbi.locale.ln['sbi.console.downloadlogs.btnClose'] = 'Chiudi';
Sbi.locale.ln['sbi.console.downloadlogs.btnDownload'] = 'Download';
Sbi.locale.ln['sbi.console.downloadlogs.initialDateMandatory'] = 'Data inizio obbligatoria';
Sbi.locale.ln['sbi.console.downloadlogs.finalDateMandatory'] = 'Data fine obbligatoria';
Sbi.locale.ln['sbi.console.downloadlogs.initialTimeMandatory'] = 'Ora inizio obbligatoria';
Sbi.locale.ln['sbi.console.downloadlogs.finalTimeMandatory'] = 'Ora fine obbligatoria';
Sbi.locale.ln['sbi.console.downloadlogs.rangeInvalid'] = 'Intervallo date errato';

//propmtables window
Sbi.locale.ln['sbi.console.promptables.btnOK'] = 'OK';
Sbi.locale.ln['sbi.console.promptables.btnClose'] = 'Chiudi';
Sbi.locale.ln['sbi.console.promptables.lookup.Annulla'] = 'Annulla';
Sbi.locale.ln['sbi.console.promptables.lookup.Confirm'] = 'Conferma';
Sbi.locale.ln['sbi.console.promptables.lookup.Select'] = 'Seleziona';

