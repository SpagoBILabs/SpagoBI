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
//HELP
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.help.content'] = 'Selezionare i membri da visualizzare per la dimensione. I membri saranno inseriti nella clausola select della query mdx';
Sbi.locale.ln['sbi.olap.help.title'] = 'Help';
Sbi.locale.ln['sbi.olap.execution.table.dimension.cannotchangehierarchy'] = 'Non puoi cambiare la gerarchia dal momento che ci sono delle modifiche pendenti sui dati: devi prima persistere le modifiche.';

//===================================================================
//COMMONS
//===================================================================
Sbi.locale.ln['sbi.common.cancel'] = 'Annulla';
Sbi.locale.ln['sbi.common.close'] = 'Chiudi';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Ok';
Sbi.locale.ln['sbi.common.warning'] = 'Attenzione';
Sbi.locale.ln['sbi.common.next'] = 'Successivo';
Sbi.locale.ln['sbi.common.prev'] = 'Precedente';
Sbi.locale.ln['sbi.common.wait'] = 'Attendere prego...';
Sbi.locale.ln['sbi.common.wait.long'] = 'Attendere prego.. Questa operazione potrebbe richiedere qualche minuto...';
//===================================================================
//TOOLBAR
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_MDX'] = 'Mdx Query';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Tipi di Drill';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_UNDO'] = 'Undo';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FLUSH_CACHE'] = 'Pulire cache';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FATHER_MEMBERS'] = 'Mostra membri padri';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_SPANS'] = 'Nascondi spans';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SHOW_PROPERTIES'] = 'Mostra proprieta';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_EMPTY'] = 'Sopprimi righe/colonne vuote';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE'] = 'Salva';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE_NEW'] = 'Salva come nuova version';
Sbi.locale.ln['sbi.olap.toolbar.lock'] = "Blocca il modello";
Sbi.locale.ln['sbi.olap.toolbar.unlock'] = "Sblocca il modello";
Sbi.locale.ln['sbi.olap.toolbar.lock_other'] = "Modello bloccato da un altro utente";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_VERSION_MANAGER'] = "Elimina delle versioni";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_EXPORT_OUTPUT'] = "Output wizard";

//===================================================================
//FILTERS
//===================================================================

Sbi.locale.ln['sbi.olap.execution.table.filter.collapse'] = 'Chiudi tutti';
Sbi.locale.ln['sbi.olap.execution.table.filter.expand'] = 'Espandi tutti';
Sbi.locale.ln['sbi.olap.execution.table.filter.filter.title'] = 'Seleziona uno slicer';
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.title'] = 'Seleziona i membri da visualizzare';
Sbi.locale.ln['sbi.olap.execution.table.filter.no.measure'] = 'Non \u00e8 possibile mettere le misure tra i filtri';
Sbi.locale.ln['sbi.olap.execution.table.filter.empty'] = 'Draggare il membro in questa sezione per usarlo come slicer';

//===================================================================
//DIMENSIONS
//===================================================================

Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy'] = 'La gerarchia selezionata \u00e8 ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy.2'] = ' Puoi cambiare la gerarchia selezionando il valore tramite il seguente form.';
Sbi.locale.ln['sbi.olap.execution.table.dimension.available.hierarchies'] = 'Gerarchie disponibili: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.no.enough'] = 'Ci deve esserre almeno una dimensione sia nelle righe che nelle colonne';


//===================================================================
//WRITEBACK
//===================================================================
Sbi.locale.ln['sbi.olap.writeback.persist.error'] = 'Errore nel salvataggio delle modifiche';
Sbi.locale.ln['sbi.olap.writeback.edit.no.zero'] =  'Non \u00E9 possibile modificare questa cella!! L\'algoritmo di propagazione attualmente in uso mantiene inalterato il peso tra le celle figlie della cella modificata, ma questo non \u00E9 possibile se quest\'ultima \u00E9 vuota oppure se ha valore 0. Nelle prossime release saranno implementati altri algoritmi di propagazione.';
Sbi.locale.ln['sbi.olap.writeback.edit.no.locked'] =  'Non \u00E9 possibile modificare il modello se non \u00E9 stato prima loccato dall\'utente corrente.';

//===================================================================
//LOCK
//===================================================================
Sbi.locale.ln['sbi.olap.artifact.lock.error'] = 'Operazione di blocco non riusciata, il modello \u00E9 ancora sbloccato.';
Sbi.locale.ln['sbi.olap.artifact.unlock.error'] = 'Operazione di sblocco non riusciata, il modello \u00E9 ancora bloccato.';
Sbi.locale.ln['sbi.olap.artifact.unlock.errorOther'] = 'Operazione di sblocco non riusciata, il modello \u00E9 ancora bloccato dall\'utente ';


//===================================================================
//MENU
//===================================================================
Sbi.locale.ln['sbi.olap.execution.menu.buttonMenu'] = 'Menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToMenu'] = 'Add to menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToToolbar'] = 'Add to toolbar';
//===================================================================
//VERSION MANAGER
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all'] = 'Non \u00E9 possibile cancellare tutte le versioni';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current'] = 'Non \u00E9 possibile cancellare la versione attuale';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.title'] = 'Selezionare le version da cancellare';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.select.warning'] = 'Attenzione: questa operazione potrebbe richiedere qualche minuto. ';
Sbi.locale.ln['sbi.olap.control.controller.delete.version.ok'] = 'Le versioni sono state eliminate correttamente.';
//Sbi.locale.ln['sbi.olap.control.controller.delete.version.error'] = 'Si \u00E9 verificato un errore nella cancellazione delle versioni. Controllare il log.';

//===================================================================
//OUTPUT
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.name'] = "Nome tabella";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.description'] = "Inserire il nome della tabella di output";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.row.delimiter'] = "Delimitatore di riga";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.filter.delimiter'] = "Delimitatore tra i campi";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.description'] = "Inserire le opzioni per il file csv. L'export CSV \u00E9 un operazione da eseguire solo con cubi piccoli, altrimenti la richiesta di risorse potrebbe essere considerevole";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table'] = "tabella";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv'] = "file";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type'] = "Output type";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.description'] = "Selezionare il tipo di output per l\'analisi. Quest'operazione potr\u00E2 richiedere qualche minuto. Nel frattempo \u00E9 comunuqe possibile lavorare sul cubo di analisi";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.title'] = "Output wizard";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.version'] = "Versione da esportare";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.ok'] = "Analisi esportatta correttamente";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.error'] = "Si \u00e8 verificato un errore in fase di export";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.csv.window'] = "<h1>Export the output in CSV</h1><p>Questa operazione potrebbe richiedere qualche minuto. \u00C8 comunque possibile continuare a lavorare sul cubo di analisi</p>";

