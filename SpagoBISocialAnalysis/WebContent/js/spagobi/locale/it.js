/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

Sbi.locale.formats = {

		float: {
			decimalSeparator: ',',
			decimalPrecision: 2,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '�',
			nullValue: ''
		},
		int: {
			decimalSeparator: ',',
			decimalPrecision: 0,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '�',
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

//===================================================================
//SEARCH FORM MESSAGE
//===================================================================
Sbi.locale.ln['sbi.social.analysis.search'] = 'Cerca';
Sbi.locale.ln['sbi.social.analysis.searchType'] = 'Tipo di ricerca';
Sbi.locale.ln['sbi.social.analysis.onlinemonitoring'] = 'Monitoraggio on-line';
Sbi.locale.ln['sbi.social.analysis.historicaldata'] = 'Ricerca storica';
Sbi.locale.ln['sbi.social.analysis.startingfrom'] = 'Inizia da';
Sbi.locale.ln['sbi.social.analysis.dayago'] = 'giorni indietro';
Sbi.locale.ln['sbi.social.analysis.repeatevery'] = 'Ripeti ogni';
Sbi.locale.ln['sbi.social.analysis.hour'] = 'Ora/e';
Sbi.locale.ln['sbi.social.analysis.day'] = 'Giorno/i';
Sbi.locale.ln['sbi.social.analysis.week'] = 'Settimana/e';
Sbi.locale.ln['sbi.social.analysis.month'] = 'Mese/i';
Sbi.locale.ln['sbi.social.analysis.logicalidentifier'] = 'Etichetta:';
Sbi.locale.ln['sbi.social.analysis.keywords'] = 'Termini di ricerca';
Sbi.locale.ln['sbi.social.analysis.keywordsfieldalertmessage'] = 'Usare una virgola per separare i termini di ricerca. Massimo 5 termini permessi';
Sbi.locale.ln['sbi.social.analysis.twitter'] = 'Twitter';
Sbi.locale.ln['sbi.social.analysis.facebook'] = 'Facebook';
Sbi.locale.ln['sbi.social.analysis.linkedin'] = 'Linkedin';
Sbi.locale.ln['sbi.social.analysis.accountstomonitor'] = 'Account da monitorare:';
Sbi.locale.ln['sbi.social.analysis.accountstomonitorfieldalertmessage'] = 'Controllare il formato di input. Es. @Account1, @Account2, etc. Massimo 3 account permessi';
Sbi.locale.ln['sbi.social.analysis.resourcestomonitor'] = 'Risorse da monitorare:';
Sbi.locale.ln['sbi.social.analysis.resourcestomonitorfieldalertmessage'] = 'Controllare il formato di input. Es. http://bit.ly/yourbitly1, http://bit.ly/yourbitly2, etc. Massimo 3 link bitly permessi';
Sbi.locale.ln['sbi.social.analysis.documentstomonitor'] = 'Ritorno sul business:';
Sbi.locale.ln['sbi.social.analysis.documentstomonitorfieldalertmessage'] = 'Usare una virgola per separare i documenti. Massimo 3 documenti permessi';
Sbi.locale.ln['sbi.social.analysis.frequency'] = 'Frequenza';
Sbi.locale.ln['sbi.social.analysis.timelyscanning'] = 'Scansione temporale';
Sbi.locale.ln['sbi.social.analysis.continuousscanning'] = 'Scansione continua';
Sbi.locale.ln['sbi.social.analysis.label'] = 'Etichetta';
Sbi.locale.ln['sbi.social.analysis.lastactivation'] = 'Ultima attivazione';
Sbi.locale.ln['sbi.social.analysis.documents'] = 'Documenti';
Sbi.locale.ln['sbi.social.analysis.upto'] = 'Fino a:';
Sbi.locale.ln['sbi.social.analysis.startstop'] = 'Avvia/Interrompi';
Sbi.locale.ln['sbi.social.analysis.stopstreammessage'] = 'Interrompere la ricerca in streaming?';
Sbi.locale.ln['sbi.social.analysis.startstreammessage'] = 'Avviare questo Stream interromperà un altro eventuale Stream attivo. Confermare?';
Sbi.locale.ln['sbi.social.analysis.delete'] = 'Cancella';
Sbi.locale.ln['sbi.social.analysis.deletingmessage'] = 'Sei sicuro di voler cancellare questa ricerca?';
Sbi.locale.ln['sbi.social.analysis.analyse'] = 'Analizza';
Sbi.locale.ln['sbi.social.analysis.scheduler'] = 'Pianificazione';
Sbi.locale.ln['sbi.social.analysis.schedulertooltip'] = 'Interrompi la pianificazione per la ricerca storica';
Sbi.locale.ln['sbi.social.analysis.stopsearchscheduler'] = 'Confermare di rimuovere la pianificazione per la ricerca?';
Sbi.locale.ln['sbi.social.analysis.searchfailedmessage'] = ' - ricerca fallita a causa di problemi di connessione. Caricamento del risultato parziale..';
Sbi.locale.ln['sbi.social.analysis.and'] = 'AND';
Sbi.locale.ln['sbi.social.analysis.or'] = 'OR';
Sbi.locale.ln['sbi.social.analysis.free'] = 'Libero';


//===================================================================
//TABS MESSAGE
//===================================================================
Sbi.locale.ln['sbi.social.analysis.summary'] = 'Sommario';
Sbi.locale.ln['sbi.social.analysis.topics'] = 'Argomenti';
Sbi.locale.ln['sbi.social.analysis.network'] = 'Rete' ;
Sbi.locale.ln['sbi.social.analysis.distribution'] = 'Distribuzione';
Sbi.locale.ln['sbi.social.analysis.sentiment'] = 'Emozioni';
Sbi.locale.ln['sbi.social.analysis.impact'] = 'Impatto';
Sbi.locale.ln['sbi.social.analysis.roi'] = 'ROI';
Sbi.locale.ln['sbi.social.analysis.searchome'] = 'Home';

//SUMMARY
Sbi.locale.ln['sbi.social.analysis.users'] = 'utenti';
Sbi.locale.ln['sbi.social.analysis.searchrange'] = 'Periodo di ricerca';
Sbi.locale.ln['sbi.social.analysis.timescale'] = 'Scala Temporale';
Sbi.locale.ln['sbi.social.analysis.hours'] = 'Ore';
Sbi.locale.ln['sbi.social.analysis.days'] = 'Giorni';
Sbi.locale.ln['sbi.social.analysis.weeks'] = 'Settimane';
Sbi.locale.ln['sbi.social.analysis.months'] = 'Mesi';
Sbi.locale.ln['sbi.social.analysis.tweetssummary'] = 'Sommario Tweet';
Sbi.locale.ln['sbi.social.analysis.tweetssources'] = 'Sorgenti Tweet';
Sbi.locale.ln['sbi.social.analysis.toptweets'] = 'Top Tweet';
Sbi.locale.ln['sbi.social.analysis.recenttweets'] = 'Tweet Recenti';

//TOPICS
Sbi.locale.ln['sbi.social.analysis.hashtagscloud'] = 'Cloud Hashtags';
Sbi.locale.ln['sbi.social.analysis.topicscloud'] = 'Cloud Argomenti';

//NETWORK
Sbi.locale.ln['sbi.social.analysis.topinfluencers'] = 'Influenzatori Top';
Sbi.locale.ln['sbi.social.analysis.usersmentions'] = 'Menzioni Utenti';
Sbi.locale.ln['sbi.social.analysis.usersinteractionsgraph'] = 'Grafico Interazioni Utenti';
Sbi.locale.ln['sbi.social.analysis.usersinteractionsmap'] = 'Mappa Interazioni Utenti';


//DISTRIBUTION
Sbi.locale.ln['sbi.social.analysis.locationtweets'] = 'Posizione Tweet';

//SENTIMENT
Sbi.locale.ln['sbi.social.analysis.tweetspolarity'] = 'Polarita\' Tweet';
Sbi.locale.ln['sbi.social.analysis.sentimentradar'] = 'Radar Sentiment';
Sbi.locale.ln['sbi.social.analysis.positivestopics'] = 'Argomenti Positivi';
Sbi.locale.ln['sbi.social.analysis.neutralstopics'] = 'Argomenti Neutrale';
Sbi.locale.ln['sbi.social.analysis.negativestopics'] = 'Argomenti Negativi';

//IMPACT
Sbi.locale.ln['sbi.social.analysis.accountsfollowerstimeline'] = 'Timeline Account Follower';
Sbi.locale.ln['sbi.social.analysis.bitlyclickstimeline'] = 'Timeline Bitly Click';