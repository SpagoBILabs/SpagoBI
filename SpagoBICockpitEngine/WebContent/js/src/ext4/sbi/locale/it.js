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


//=====================================================================================================
//GENERIC
//=====================================================================================================

Sbi.locale.ln['sbi.generic.author'] = 'Autore';
Sbi.locale.ln['sbi.generic.creationdate'] = 'Data di creazione';
Sbi.locale.ln['sbi.generic.owner']= 'Autore';
Sbi.locale.ln['sbi.generic.name']= 'Nome';
Sbi.locale.ln['sbi.generic.save'] = 'Salva ';
Sbi.locale.ln['sbi.generic.saveAndGoBack'] = 'Salva ed esci ';
Sbi.locale.ln['sbi.generic.cancel'] = 'Annulla ';
Sbi.locale.ln['sbi.generic.resultMsg'] = 'Operazione riuscita';

//===================================================================
//WIZARD 
//===================================================================
Sbi.locale.ln['sbi.ds.wizard.general']= 'Salva il dataset';
Sbi.locale.ln['sbi.ds.wizard.detail']= 'Carica file';
Sbi.locale.ln['sbi.ds.wizard.metadata']= 'Definisci dati';
Sbi.locale.ln['sbi.ds.wizard.validation']= 'Valida dati';
Sbi.locale.ln['sbi.ds.wizard.back']= '< Indietro';
Sbi.locale.ln['sbi.ds.wizard.next']= 'Sucessivo >';
Sbi.locale.ln['sbi.ds.wizard.confirm']= 'Conferma';
Sbi.locale.ln['sbi.ds.wizard.cancel']= 'Annulla';
Sbi.locale.ln['sbi.ds.wizard.close']= 'Chiudi';
Sbi.locale.ln['sbi.ds.wizard.startMsg']= 'Seleziona e carica il file XLS o CSV ...';
Sbi.locale.ln['sbi.ds.wizard.file']= 'File';
Sbi.locale.ln['sbi.ds.wizard.successLoad']= ' caricato con successo!';
Sbi.locale.ln['sbi.ds.wizard.selectedFile']='File selezionato ';
Sbi.locale.ln['sbi.ds.wizard.loadedFile']='Caricato file ';
Sbi.locale.ln['sbi.ds.wizard.selectFile']='Seleziona il file';

Sbi.locale.ln['sbi.ds.orderComboLabel']= 'Ordina per ...';
Sbi.locale.ln['sbi.ds.filterLabel']= 'Filtra per ...';
Sbi.locale.ln['sbi.ds.moreRecent']= 'Recenti';


//===================================================================
//BROWSER 
//===================================================================
Sbi.locale.ln['sbi.browser.document.searchDatasets']  = 'Cerca fra i dataset';
Sbi.locale.ln['sbi.browser.document.searchKeyword']  = 'Cerca per parola chiave...';

//===================================================================
//MYDATA 
//===================================================================
Sbi.locale.ln['sbi.mydata.useddataset'] = "Data Set in uso";
Sbi.locale.ln['sbi.mydata.mydataset'] = "I Miei Data Set";
Sbi.locale.ln['sbi.mydata.enterprisedataset'] = "Data Set Certificati";
Sbi.locale.ln['sbi.mydata.shareddataset'] = "Data Set Condivisi";
Sbi.locale.ln['sbi.mydata.alldataset'] = "Tutti i Data Set";
Sbi.locale.ln['sbi.mydata.sharedataset'] = "Condividi Data Set";
Sbi.locale.ln['sbi.mydata.unsharedataset'] = "Rendi privato Data Set";

//===================================================================
//SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.savewin.title'] = 'Inserisci ulteriori dettagli e salva il tuo documento... ';
Sbi.locale.ln['sbi.savewin.name'] = 'Nome';
Sbi.locale.ln['sbi.savewin.description'] = 'Descrizione';
Sbi.locale.ln['sbi.savewin.previewfile'] = 'Preview file';
Sbi.locale.ln['sbi.savewin.saveWarning']  = 'Inserire il Nome prima di salvare il documento';

//===================================================================
//COCKPIT
//===================================================================
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditormainpanel.emptymsg'] = 'Trascina qui un componente dalla palette';
Sbi.locale.ln['sbi.cockpit.core.WidgetDesigner.title'] = 'Definizione tabella piatta';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields.emptymsg'] = 'Campi visibili';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields'] = 'Trascina qui gli attributi e le misure che vuoi visualizzare nella tabella';
Sbi.locale.ln['sbi.cockpit.queryfieldspanel.title'] = 'Campi selezionati';

Sbi.locale.ln['sbi.cockpit.mainpanel.btn.selections'] = 'Selezioni';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.filters'] = 'Filtri';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.associations'] = 'Associazioni';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.addWidget'] = 'Aggiungi Widget';

//ASSOCIATIONS
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.title'] = 'Editor Associazioni';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.ds.columnColumn'] = 'Colonna';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.ds.columnType'] = 'Tipo';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.columnId'] = 'Identificativo';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.columnAssociation'] = 'Associazione';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.title'] = 'Lista Associazioni';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.add'] = 'Aggiungi';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.add.tooltip'] = 'Aggiungi Associazione';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.modify'] = 'Modifica';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.modify.tooltip'] = 'Modifica Associazione';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.delete'] = 'Cancella';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.delete.tooltip'] = 'Cancella Associazione';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.autodetect'] = 'Rilevamento automatico';
Sbi.locale.ln['sbi.cockpit.association.editor.wizard.list.autodetect.tooltip'] = 'Rilevamento automatico delle Associazioni';

Sbi.locale.ln['sbi.cockpit.association.editor.msg.modify'] = 'Per procedere con l\aggiornamento � necessario selezionare l\'associazione da modificare dalla lista!';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.duplicate'] = 'Associazione gia esistente!';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.differentType'] ='Non tutte le tipologie dei campi selezionati coincidono. Si intende proseguire con l\'aggiunta dell\'associazione?';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.selectFields'] ='Selezionare le colonne per l\'associazione';
Sbi.locale.ln['sbi.cockpit.association.editor.msg.confirmDelete'] ='Confermi la cancellazione dell\'associazione ';

//FILTERS
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.title'] = 'Editor Filtri';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.nameObj'] = 'Oggetto di riferimento';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.typeObj'] = 'Tipologia';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.namePar'] = 'Nome parametro';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.typePar'] = 'Tipologia parametro';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.initialValue'] = 'Valore iniziale';
Sbi.locale.ln['sbi.cockpit.filter.editor.wizard.list.scope'] = 'Ambito';

Sbi.locale.ln['sbi.cockpit.core.selections.title'] = 'Lista delle selezioni attive';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnWidget'] = 'Componente';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnField'] = 'Campo selezione';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnValues'] = 'Valori';
Sbi.locale.ln['sbi.cockpit.core.selections.list.items'] = 'Elementi';