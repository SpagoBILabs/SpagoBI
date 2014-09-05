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
			//currencySymbol: 'â‚¬',
			nullValue: ''
		},
		*/
		float: {
			decimalSeparator: ',',
			decimalPrecision: 2,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: 'â‚¬',
			nullValue: ''
		},
		int: {
			decimalSeparator: ',',
			decimalPrecision: 0,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: 'â‚¬',
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
Sbi.locale.ln['sbi.cockpit.editor.widget.widgeteditorcustomconfpanel.emptymsg'] = 'Trascina qui un componente dalla palette';
Sbi.locale.ln['sbi.cockpit.core.WidgetDesigner.title'] = 'Definizione tabella piatta';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields.emptymsg'] = 'Campi visibili';
Sbi.locale.ln['sbi.cockpit.widgets.table.tabledesignerpanel.fields'] = 'Trascina qui gli attributi e le misure che vuoi visualizzare nella tabella';
Sbi.locale.ln['sbi.cockpit.queryfieldspanel.title'] = 'Campi selezionati';

//===================================================================
//Sbi.cockpit.widgets.ChartCategoryPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.title'] = 'Categoria';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.emptymsg'] = 'Trascina qui un attributo della query come categoria';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.emptymsg'] = 'Trascina qui un attributo della query come categoria';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.title'] = 'Drop non consentito';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.unknownsource'] = 'Sorgente sconosciuta';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.measures'] = 'Non puoi trascinare qui le misure';
Sbi.locale.ln['sbi.cockpit.widgets.chartcategorypanel.cannotdrophere.postlinecalculated'] = 'Non puoi trascinare qui i campi calcolati basati su script';

//===================================================================
//Sbi.cockpit.widgets.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.title'] = 'Serie';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.emptymsg'] = 'Trascina qui alcune misure della query come serie';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.queryfield'] = 'Campo';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.seriename'] = 'Nome';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.color'] = 'Colore';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.showcomma'] = 'Mostra separatore delle migliaia';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.precision'] = 'Precisione';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.columns.suffix'] = 'Suffisso';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.title'] = 'Drop non consentito';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.measurealreadypresent'] = 'La misura è già presente';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.attributes'] = 'Non puoi inserire degli attributi tra le serie del grafico';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.cannotdrophere.postlinecalculated'] = 'Non puoi utilizzare i campi calcolati basati su script tra le serie del grafico';
Sbi.locale.ln['sbi.cockpit.widgets.chartseriespanel.tools.tt.removeall'] = ['Rimuovi tutti'];

//===================================================================
//Sbi.cockpit.widgets.ChartSeriesPanel
//===================================================================
Sbi.locale.ln['sbi.cockpit.widgets.seriesgroupingpanel.emptymsg'] = 'Drag & drop here a query attribute as the series\' grouping variable';
Sbi.locale.ln['sbi.cockpit.widgets.seriesgroupingpanel.title'] = 'Series\' grouping variable';


//column headers
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.include'] = 'Includi';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.having'] = 'Condizione su raggruppamento';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.visible'] = 'Visibile';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Raggruppa';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.filter'] = 'Filtri';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.entity'] = 'Entita\'';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.alias'] = 'Alias';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.order'] = 'Ordinamento';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Raggruppa';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Funzione';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.field'] = 'Campo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete'] = 'Elimina tutti';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete.column'] = 'Elimina';

//aggregation functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'nessuno';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'somma';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'media';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'massimo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'minimo';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'conteggio';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'conteggio distinti';


Sbi.locale.ln['sbi.cockpit.mainpanel.btn.selections'] = 'Selezioni';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.clearselections'] = 'Cancella selezioni';
Sbi.locale.ln['sbi.cockpit.mainpanel.btn.parameters'] = 'Parametri';
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

Sbi.locale.ln['sbi.cockpit.association.editor.msg.modify'] = 'Per procedere con l\aggiornamento è necessario selezionare l\'associazione da modificare dalla lista!';
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

Sbi.locale.ln['sbi.cockpit.core.selections.list.columnAssociation'] = 'Associazione';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnWidget'] = 'Componente';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnField'] = 'Campo selezione';
Sbi.locale.ln['sbi.cockpit.core.selections.list.columnValues'] = 'Valori';
Sbi.locale.ln['sbi.cockpit.core.selections.list.items'] = 'Elementi';


//===================================================================
//Sbi.cockpit.core.WidgetContainerComponent
//===================================================================
Sbi.locale.ln['sbi.cockpit.window.toolbar.editor'] = 'Configura';
Sbi.locale.ln['sbi.cockpit.window.toolbar.refresh'] = 'Aggiorna';
Sbi.locale.ln['sbi.cockpit.window.toolbar.clone'] = 'Clona';