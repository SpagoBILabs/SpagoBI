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
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Warning';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Messaggio di Errore';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Informazione';

//LookupField 
Sbi.locale.ln['sbi.lookup.Confirm'] = 'Conferma';
Sbi.locale.ln['sbi.lookup.Annulla'] = 'Elimina';
Sbi.locale.ln['sbi.lookup.Select']= 'Seleziona ...';
Sbi.locale.ln['sbi.lookup.asA'] = 'as a';

//Execution
Sbi.locale.ln['sbi.dm.execution.run.text'] = 'Esegui script';
Sbi.locale.ln['sbi.dm.execution.load.dataset.wait'] = 'Caricamento in corso...';
Sbi.locale.ln['sbi.dm.execution.load.dataset.ok'] = 'Caricamento avvenuto con successo';
Sbi.locale.ln['sbi.dm.execution.msg'] = 'Risultato esecuzione';
Sbi.locale.ln['sbi.dm.execution.upload.btn'] = 'Upload';
Sbi.locale.ln['sbi.dm.execution.load.btn'] = 'Carica';
Sbi.locale.ln['sbi.dm.execution.reset.btn'] = 'Reset';
Sbi.locale.ln['sbi.dm.execution.loading'] = 'Operazione in corso...';
Sbi.locale.ln['sbi.dm.execution.save.btn'] = 'Salva';
Sbi.locale.ln['sbi.dm.execution.ok'] = 'Operatione avvenuta con successo';