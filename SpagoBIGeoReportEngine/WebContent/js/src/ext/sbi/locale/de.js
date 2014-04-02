/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
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
			decimalSeparator: ',',
			decimalPrecision: 2,
			groupingSeparator: '.',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		int: {
			decimalSeparator: ',',
			decimalPrecision: 0,
			groupingSeparator: '.',
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

//=====================================================================================================
//=====================================================================================================
//DA TRADURRE per utente FINALE
//=====================================================================================================
//=====================================================================================================

Sbi.locale.ln['sbi.generic.delete'] = 'Delete';

Sbi.locale.ln['sbi.geo.legendpanel.changeStyle'] = 'Stil ändern'; 

Sbi.locale.ln['sbi.geo.layerpanel.layer'] = ' layer';
Sbi.locale.ln['sbi.geo.layerpanel.add'] = 'In Schicht';
Sbi.locale.ln['sbi.geo.layerpanel.catalogue'] = 'Layer-Katalog';

Sbi.locale.ln['sbi.geo.controlpanel.savewin.saveWarning']  = 'Bevor Dokument zu speichern, ist notwendig, um den Namen der Karte und wählen Sie Einfügen fast einen Ordner.';
//Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = 'Wählen Sie den Ordner, in dem die Karte veröffentlichen. Speichern in der [public] jeder sehen Sie die Karte. Speichern in der [private] nur angemeldete Benutzer sind in der Lage, um die Karte zu sehen.';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.title']  = 'Denden Feedback';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.label']  = 'Die Mitteilung';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.btn.send']  = 'Senden';
Sbi.locale.ln['sbi.geo.controlpanel.feedback.sendOK']  = 'Feedback zu Ersteller des Dokuments gesendet';

Sbi.locale.ln['sbi.geo.controlpanel.control.share.title']  = 'Teilen Karte';







//=====================================================================================================
//=====================================================================================================
//già TRADOTTE
//=====================================================================================================
//=====================================================================================================

//===================================================================
//LABELS
//===================================================================


Sbi.locale.ln['sbi.dataset.no.visible'] = 'Es tut uns leid, aber es ist zu imposible Dokument ausführen [%1], weil die Datenmenge [%0] verbunden, um es nicht mehr sichtbar ist';
	


//===================================================================
//GENERIC
//===================================================================

Sbi.locale.ln['sbi.generic.add'] = 'In Indikatoren aus dem Katalog';//'Add';
Sbi.locale.ln['sbi.generic.select'] = 'Wählen Indikatoren aus dem Katalog';//'Select';
Sbi.locale.ln['sbi.generic.cancel'] = 'abbrechen';//'Cancel';
Sbi.locale.ln['sbi.generic.modify'] = 'ändern';//'Modify';
Sbi.locale.ln['sbi.generic.save'] = 'speichern';//'Save ';
Sbi.locale.ln['sbi.generic.newmap'] = 'neue Karte';//'new map';
Sbi.locale.ln['sbi.generic.savenewmap'] = 'Speichern Sie neue Karten'; //'Save new map';
Sbi.locale.ln['sbi.generic.wait'] = 'Bitte warten Sie ...'; //'Please wait...';
Sbi.locale.ln['sbi.generic.info'] = 'Info'; //'Info';
Sbi.locale.ln['sbi.generic.error'] = 'Fehler'; //'Error';
Sbi.locale.ln['sbi.generic.error.msg'] = 'Operation fehlgeschlagen'; //'Operation failed';
Sbi.locale.ln['sbi.generic.ok'] = 'Informationen'; //'Information';
Sbi.locale.ln['sbi.generic.ok.msg'] = 'Operation erfolgreich beendet'; //'Operation succesfully ended';
Sbi.locale.ln['sbi.generic.resultMsg'] = 'Bedienung gelungen'; //'Operation succeded';
Sbi.locale.ln['sbi.generic.result'] = 'Updates gespeichert'; //'Updates saved';
Sbi.locale.ln['sbi.generic.serviceError'] = 'Service-Fehler'; //'Service Error';
Sbi.locale.ln['sbi.generic.serviceResponseEmpty'] = 'Server Antwort ist leer'; //'Server response is empty';
Sbi.locale.ln['sbi.generic.savingItemError'] = 'Fehler beim Speichern der Elements'; //'Error while saving item';
Sbi.locale.ln['not-enabled-to-call-service'] = 'Dem Benutzer ist es nicht erlaubt, diesen Vorgang auszuführen'; //'The user is not allowed to do this operation';
Sbi.locale.ln['sbi.generic.deletingItemError'] = 'Fehler beim Löschen des Elements. Kontrollieren Sie eventuelle Positionen, denen sie zugeordnet ist, und dann versuchen Sie es erneut.'; //'Error while deleting item. Control eventual items to which it is associated and then try to delete it again!';
Sbi.locale.ln['sbi.generic.warning'] = 'Warnung'; //'Warning';
Sbi.locale.ln['sbi.generic.pleaseConfirm'] = 'Bitte bestätigen'; //'Please confirm';




//===================================================================
// CONTROL PANEL
//===================================================================

Sbi.locale.ln['sbi.geo.controlpanel.title'] = 'Systemsteuerung'; //'Control Panel';
Sbi.locale.ln['sbi.geo.controlpanel.defaultname'] = 'Neue Kartenname ...'; //'New Map name...';
Sbi.locale.ln['sbi.geo.controlpanel.defaultdescr'] = 'Neue Kartenbeschreibung ...'; //'New Map description...';
Sbi.locale.ln['sbi.geo.controlpanel.publishedby'] = 'Veröffentlicht von '; //'Published by ';
Sbi.locale.ln['sbi.geo.controlpanel.sendfeedback'] = 'Feedback senden'; //' Send feedback ';
Sbi.locale.ln['sbi.geo.controlpanel.indicators'] = 'Indikatoren'; //' Indicators ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionlabel'] = 'Diese Karte ist:'; //'This map is: ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionprivate'] = 'Privat'; //'Private ';
Sbi.locale.ln['sbi.geo.controlpanel.permissionpublic'] = 'Öffentlich'; //'Public ';
Sbi.locale.ln['sbi.geo.controlpanel.map'] = 'Karte'; //'Map ';
Sbi.locale.ln['sbi.geo.controlpanel.zone'] = 'Zone'; //'zone ';
Sbi.locale.ln['sbi.geo.controlpanel.point'] = 'Punkt'; //'point ';

Sbi.locale.ln['sbi.geo.controlpanel.filters'] = 'Filter';

Sbi.locale.ln['sbi.geo.layerpanel.title'] = 'Layers'; //'Layers';
Sbi.locale.ln['sbi.geo.layerpanel.addremove'] = 'Hinzufügen/Entfernen Layer'; //'Add/Reomove Layer';



Sbi.locale.ln['sbi.geo.analysispanel.title'] = 'Analyse'; //'Analysis';
Sbi.locale.ln['sbi.geo.analysispanel.addindicators'] = 'Indikatoren hinzufügen'; //'Add indicators';
Sbi.locale.ln['sbi.geo.analysispanel.indicator'] = 'Indikator'; //'Indicator';
Sbi.locale.ln['sbi.geo.analysispanel.emptytext'] = 'Wählen Sie einen Indikator';//'Select an indicator';
Sbi.locale.ln['sbi.geo.analysispanel.method'] = 'Verfahren'; //'Method';
Sbi.locale.ln['sbi.geo.analysispanel.classes'] = 'Anzahl der Klassen'; //'Number of classes';
Sbi.locale.ln['sbi.geo.analysispanel.fromcolor'] = 'Von Farbe'; //'From color';
Sbi.locale.ln['sbi.geo.analysispanel.tocolor'] = 'zu Farbe'; //'To color';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default'] = 'Betreibseinstellungen zurücksetzen'; //'Set Default';
Sbi.locale.ln['sbi.geo.analysispanel.filter.default.ok'] = 'Default-Werte richtig eingestellt'; //'Default values correctly set';

	

Sbi.locale.ln['sbi.geo.legendpanel.title'] = 'Legende'; //'Legend';
Sbi.locale.ln['sbi.geo.earthpanel.title'] = '3D-Navigation'; //'3D Navigation';


//===================================================================
//CONTROL PANEL - SAVE WINDOW
//===================================================================

Sbi.locale.ln['sbi.geo.controlpanel.savewin.title'] = 'Mehr Details hinzufügen und Dokument speichern ...'; //'Insert more details and save your document... ';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.name'] = 'Name'; //'Name';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.description'] = 'Beschreibung'; //'Description';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.visibility'] = 'Dokument Sichtbarkeit'; //'Document visibility';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.previewfile'] = 'Dateivorschau'; //'Preview file';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.community'] = 'Gemeinschaft'; //'Community';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.scope'] = 'Scope'; //'Scope';
Sbi.locale.ln['sbi.geo.controlpanel.savewin.msgDetail']  = 'Wählen Sie den Ordner, in dem die Karte veröffentlichen. Die Ordnerauswahl wird die Sichtbarkeit von anderen Benutzern zu bestimmen';


//===================================================================
// MAP PANEL
//===================================================================

Sbi.locale.ln['sbi.geo.mappanel.title'] = 'Karte'; //'Map';

//===================================================================
//OPENLAYERS
//===================================================================
Sbi.locale.ln['mf.print.mapTitle'] ='Titel';//  'Title';
Sbi.locale.ln['mf.print.comment'] = 'Kommentare';// 'Comments';
Sbi.locale.ln['mf.print.loadingConfig'] = 'Laden der Konfiguration ...';//'Loading the configuration...';
Sbi.locale.ln['mf.print.serverDown'] ='Der Druckerdienst funktioniert nicht';//  'The print service is not working';
Sbi.locale.ln['mf.print.unableToPrint'] = 'Drucken nicht möglich';// "Unable to print";
Sbi.locale.ln['mf.print.generatingPDF'] = 'Generiere PDF ...';// "Generating PDF...";
Sbi.locale.ln['mf.print.dpi'] = 'DPI';// 'DPI';
Sbi.locale.ln['mf.print.scale'] =  'Maßstab';//'Scale';
Sbi.locale.ln['mf.print.rotation'] = 'Drehung';// 'Rotation';
Sbi.locale.ln['mf.print.print'] =  'drucken';//'Print';
Sbi.locale.ln['mf.print.resetPos'] =  'Pos. zurücksetzen';//'Reset Pos.';
Sbi.locale.ln['mf.print.layout'] = 'Layout';// 'Layout';
Sbi.locale.ln['mf.print.addPage'] = 'Seite hinzufügen';// 'Add page';
Sbi.locale.ln['mf.print.remove'] = 'Seite entfernen';// 'Remove page';
Sbi.locale.ln['mf.print.clearAll'] =  'alle löschen';//'Clear all';
Sbi.locale.ln['mf.print.popupBlocked'] =  'Popup Fenster sind durch Browser blockiert.<br />' +
                         '<br />Benutzen Sie diesen Link zum herrunterladen des Dokuments] = ';
Sbi.locale.ln['mf.print.noPage'] = 'Keine Seite ausgewählt; klicken Sie auf "Seite hinzufügen", um eine hinzuzufügen.';// 'No page selected; click on the "Add page" button to add one.';
Sbi.locale.ln['mf.error'] = 'Fehler';// 'Error';
Sbi.locale.ln['mf.warning'] = 'Warnung';// 'Warning';
Sbi.locale.ln['mf.information'] = 'Informationen';// 'Information';
Sbi.locale.ln['sbi.tools.catalogue.measures.measure.properties'] = 'Eigenschaften der Messung'; //'Measure Properties';
Sbi.locale.ln['sbi.tools.catalogue.measures.dataset.properties'] = 'Datensatz Eigenschaften'; //'Data Set Properties';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.btn'] = 'Auf der Karte zeigen'; //'Show on map';
Sbi.locale.ln['sbi.tools.catalogue.measures.join.tooltip'] = 'Generieren Sie eine thematische Karte mit den ausgewählten Maßnahmen';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.tooltip'] = 'Öffnen Sie den Auswahlrahmen'; //'Open the selection frame';
Sbi.locale.ln['sbi.tools.catalogue.measures.select.btn'] = 'Auswahl'; //'Selection';
Sbi.locale.ln['sbi.tools.catalogue.measures.window.title'] =  'Messungen-Katalog';//'Measures Catalogue';
Sbi.locale.ln['error.mesage.description.measure.join.no.common.dimension'] = 'Ausführen des Joins nicht moeglich, keine gemeinsamen Dimensionen'; //'Impossible to execute the join between measures. The associated datasets haven\'t any dimension in common.';
Sbi.locale.ln['error.mesage.description.measure.join.no.complete.common.dimension'] = 'Ausführen des Joins nicht moeglich, die Datensätze haben keine kompletten Dimensionen gemeinsam'; //'Impossible to execute the join between measures. The associated datasets haven\'t any complete dimension in common.';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.alias'] = 'Alias'; //'Alias';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsName'] = 'Name'; //'Name';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsLabel'] = 'Etikett'; //'Label';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsCategory'] = 'Kategorie'; //'Category';
Sbi.locale.ln['sbi.tools.catalogue.measures.column.header.dsType'] = 'Typ'; //'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.label'] = 'Etikett'; //'Label';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.descr'] = 'Beschreibung'; //'Description';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.type'] = 'Typ'; //'Type';
Sbi.locale.ln['sbi.tools.catalogue.layers.column.header.baseLayer'] = 'Basis Layer'; //'Base Layer';
