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


//===================================================================
//LABELS
//===================================================================

Sbi.locale.ln['sbi.dataset.no.visible'] = 'Ci dispiace ma è imposible eseguire il documento [%1] perché il dataset [%0] ad esso associato non è più visibile';

//===================================================================
//GENERIC
//===================================================================
Sbi.locale.ln['sbi.generic.add'] = 'Aggiungi indicatori dal catalogo';//'Add';
Sbi.locale.ln['sbi.generic.select'] = 'Seleziona indicatori dal catalogo';//'Select';
Sbi.locale.ln['sbi.generic.delete'] = 'Rimuovi';
Sbi.locale.ln['sbi.generic.cancel'] = 'Annulla';
Sbi.locale.ln['sbi.generic.modify'] = 'Aggiorna';
Sbi.locale.ln['sbi.generic.save'] = 'Salva ';
Sbi.locale.ln['sbi.generic.newmap'] = 'Nuova mappa';
Sbi.locale.ln['sbi.generic.savenewmap'] = 'Salva nuova mappa';
Sbi.locale.ln['sbi.generic.wait'] = 'Attendere prego...';
Sbi.locale.ln['sbi.generic.info'] = 'Informazione';
Sbi.locale.ln['sbi.generic.error'] = 'Errore';
Sbi.locale.ln['sbi.generic.error.msg'] = 'Operazione fallita';
Sbi.locale.ln['sbi.generic.ok'] = 'Informazione';
Sbi.locale.ln['sbi.generic.ok.msg'] = 'Operazione avvenuta con successo';
Sbi.locale.ln['sbi.generic.resultMsg'] = 'Operazione riuscita';
Sbi.locale.ln['sbi.generic.result'] = 'Modifiche salvate con successo';
Sbi.locale.ln['sbi.generic.serviceError'] = 'Service Error';
Sbi.locale.ln['sbi.generic.serviceResponseEmpty'] = 'Server response vuota';
Sbi.locale.ln['sbi.generic.savingItemError'] = 'Errore nel salvataggio dell\'oggetto';
Sbi.locale.ln['sbi.generic.deletingItemError'] = 'Errore nell\'eliminazione dell\'oggetto. Controllare gli eventuali oggetti a cui \u00e8 associato e poi riprovare!';
Sbi.locale.ln['sbi.generic.warning'] = 'Attenzione';
Sbi.locale.ln['sbi.generic.pleaseConfirm'] = 'Per favore confermare';


//===================================================================
//CONTROL PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Pannello di controllo';
Sbi.locale.ln['sbi.geo.controlpanel.defaultname'] = 'Nome nuova mappa...';
Sbi.locale.ln['sbi.geo.controlpanel.defaultdescr'] = 'Descrizione nuova mappa...';
Sbi.locale.ln['sbi.geo.controlpanel.publishedby'] = 'Pubblicato da ';
Sbi.locale.ln['sbi.geo.controlpanel.sendfeedback'] = ' Invia feedback ';
Sbi.locale.ln['sbi.geo.controlpanel.indicators'] = ' Indicatori ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionlabel'] = 'Questa mappa e\': ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionprivate'] = 'Privata ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionpublic'] = 'Pubblica ';
Sbi.locale.ln['sbi.geo.controlpanel.map'] = 'Mappa ';
Sbi.locale.ln['sbi.geo.controlpanel.zone'] = 'zone ';
Sbi.locale.ln['sbi.geo.controlpanel.point'] = 'puntiforme ';

Sbi.locale.ln['sbi.geo.earthpanel.title'] = 'Navigazione 3D';
Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Livelli';
Sbi.locale.ln['sbi.geo.layerpanel.layer'] = ' livello';
Sbi.locale.ln['sbi.geo.layerpanel.add'] = 'Aggiungi livello';
Sbi.locale.ln['sbi.geo.layerpanel.catalogue'] = 'Catalogo Livelli';
Sbi.locale.ln['sbi.geo.layerpanel.addremove'] = 'Aggiungi/Rimuovi Layer';


Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'Analisi';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'Aggiungi indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 	'Seleziona un indicatore';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'Metodo';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'Numero di classi';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'Dal colore';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'Al colore';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'Set Default';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'Valori di default valorizzati correttamente';

Sbi.locale.ln['sbi.geo.controlpanel.filters'] = 'Filtri';


Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'Legenda';
Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Modifica stile'; 

//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.geo.controlpanel.savewin.title'] = 'Inserisci ulteriori dettagli e salva il tuo documento... ';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.name'] = 'Nome';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.description'] = 'Descrizione';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.visibility'] = 'Visibilita documento';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.previewfile'] = 'Preview file';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.community'] = 'Community';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.scope'] = 'Scope';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.saveWarning']  = 'Prima di salvare il documento, inserire il Nome e selezionare almeno una cartella dove posizionare il documento';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = 'Selezionare la cartella in cui pubblicare la mappa. Salvando nella cartella [public] tutti potranno vedere la mappa. Salvando nella cartella [private] solo gli utenti loggati potranno vedere la mappa.';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.title']  = 'Invia Feedback';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.label']  = 'Testo del messaggio';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.btn.send']  = 'Invia';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.sendOK']  = 'Feedback inviato al creatore del documento';

Sbi.locale.ln['sbi.geo.controlpanel.control.share.title']  = 'Condividi mappa';

//===================================================================
//MAP PANEL
//===================================================================
Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Mappa';

//===================================================================
//OPENLAYERS
//===================================================================
Sbi.locale.ln['mf.print.mapTitle'] =  'Titolo';
Sbi.locale.ln['mf.print.comment'] =  'Commenti';
Sbi.locale.ln['mf.print.loadingConfig'] =  'Caricamento configurazione...';
Sbi.locale.ln['mf.print.serverDown'] =  'Il servizio di stampa non è disponibile';
Sbi.locale.ln['mf.print.unableToPrint'] =  "UnImpossibile stampare";
Sbi.locale.ln['mf.print.generatingPDF'] =  "Generazione PDF...";
Sbi.locale.ln['mf.print.dpi'] =  'DPI';
Sbi.locale.ln['mf.print.scale'] =  'Scala';
Sbi.locale.ln['mf.print.rotation'] =  'Rotazione';
Sbi.locale.ln['mf.print.print'] =  'Stampa';
Sbi.locale.ln['mf.print.resetPos'] =  'Reset Pos.';
Sbi.locale.ln['mf.print.layout'] =  'Layout';
Sbi.locale.ln['mf.print.addPage'] =  'Aggiungi pagina';
Sbi.locale.ln['mf.print.remove'] =  'Rimuovi pagina';
Sbi.locale.ln['mf.print.clearAll'] =  'Pulisci tutto';
Sbi.locale.ln['mf.print.popupBlocked'] =  'Finestra di popup bloccata dal browser.<br />' +
                       '<br />Usa questa url per scaricare il tuo documento] = ';
Sbi.locale.ln['mf.print.noPage'] =  'Non ci sono pagine selezionate; click sul bottone "Aggiungi pagina" per aggiungerne una.';
Sbi.locale.ln['mf.error'] =  'Errore';
Sbi.locale.ln['mf.warning'] =  'Warning';
Sbi.locale.ln['mf.information'] =  'Informazione';
Sbi.locale.ln['sbi.tools.catalogue.measures.measure.properties'] = 'Proprietà Misura';
Sbi.locale.ln['sbi.tools.catalogue.measures.dataset.properties'] = 'Proprietà Data Set';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Mostra sulla mappa';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Genara una mappa tematica a partire dalle misure selezionate';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.tooltip'] = 'Apri il frame di selezione';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.btn'] = 'Selezione';
Sbi.locale.ln['sbi.tools.catalogue.measures.window.title'] =  'Catalogo delle Misure';
Sbi.locale.ln['error.mesage.description.measure.join.no.common.dimension'] = 'Impossibile eseguire la join tra le misure. I datasets associati non hanno misure in comune.';
Sbi.locale.ln['error.mesage.description.measure.join.no.complete.common.dimension'] = 'Impossibile eseguire la join tra le misure. I datasets associati non hanno dimensioni in comune.';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.alias'] = 'Alias';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsName'] = 'Nome';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsLabel'] = 'Etichetta';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsCategory'] = 'Categoria';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsType'] = 'Tipo';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.label'] = 'Etichetta';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.descr'] = 'Descrizione';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.type'] = 'Tipo';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.baseLayer'] = 'Livello base';
