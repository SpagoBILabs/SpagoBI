/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.locale");

Sbi.locale.ln = Sbi.locale.ln || new Array();

//===================================================================
//CROSSTAB DESIGNER
//===================================================================
Sbi.locale.ln['sbi.crosstab.crosstabdesignerpanel.title'] = 'Crosstab designer';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.title'] = 'Définition d\'une Crosstab';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.tools.preview'] = 'Afficher l\'aperçu de la Crosstab';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.rows'] = 'Lignes';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.columns'] = 'Colonnes';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.measures'] = 'Mésures';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.title'] = 'Vous ne pouvez pas déposer ici';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresent'] = 'Le champ est déjà présent ici';
//Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresentfilters'] = 'Le champ est déjà présent dans les filtres';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.measures'] = 'On peut pas mettre des mesures dans les lignes ou colonnes: vous devez les mettre dans la partie centrale de la crosstab';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.tools.tt.removeall'] = ['Supprimer tout'];


Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.title'] = 'On peut pas déposer ici';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.measurealreadypresent'] = 'Cette mesure est déjà présente';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.attributes'] = 'On peut pas mettre des attributs dans la partie centrale de la crosstab: vous devez les mettre dans les lignes ou colonnes';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.postlinecalculated'] = 'On peut pas utiliser des script dans une crosstab';

Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.showdetailswizard'] = 'Voir les détails';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.removeall'] = ['Supprimer tout'];

Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.title'] = 'Détails de la crosstab';

Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.measureson'] = 'Mesures de';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.rows'] = 'lignes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.columns'] = 'colonnes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.onrows'] = 'Sur les lignes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.oncolumns'] = 'Sur les colonnes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsonrows'] = 'Montrer les totals';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsonrows'] = 'Montrer les sous-totals';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsoncolumns'] = 'Montrer les totals';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsoncolumns'] = 'Montrer les sous-totals';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.cancel'] = 'Effacer';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.percenton'] = 'Pourcentages par rapport au totals de';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.column'] = 'colonne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.row'] = 'ligne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.nopercent'] = 'nul';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.maxcellnumber'] = 'Nombre maximum de cellules';


Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.title'] = 'Choisir la fonction d\'agrégation pour la mesure';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.cancel'] = 'Effacer';

Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.title'] = 'Afficher l\'aperçu de la Crosstab';
Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.overflow.warning'] = 'Le nombre de cellules dépasse la limite fixée. Pour avoir la crosstab complète on doit exporter le document en XLS.';


Sbi.locale.ln['sbi.crosstab.crossTabValidation.title'] = 'Validation de la crosstab nulle';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMeasure'] = 'Il n\'est pas été inclus toute mesure dans le tableau des pivots';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noAttribute'] = 'Il n\'est pas été inclus toute attribut dans le tableau des pivots';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noSegmentAttribute'] = 'Il faut utiliser l\'attribut de la catégorie';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMandatoryMeasure'] = 'Il faut utiliser la mesure obligatoire';


//===================================================================
//CROSSTAB
//===================================================================

Sbi.locale.ln['sbi.crosstab.header.total.text'] = 'Total';

Sbi.locale.ln['sbi.crosstab.menu.addcalculatedfield'] = 'Ajouter le champ calculé';
Sbi.locale.ln['sbi.crosstab.menu.removecalculatedfield'] = 'Supprimer le champ calculé';
Sbi.locale.ln['sbi.crosstab.menu.modifycalculatedfield'] = 'Modifier le champ calculé';
Sbi.locale.ln['sbi.crosstab.menu.hideheader'] = 'Cacher le header';
Sbi.locale.ln['sbi.crosstab.menu.hideheadertype'] = 'Cacher tous les headers de ce type';
Sbi.locale.ln['sbi.crosstab.menu.hidemeasure'] = 'Mesures Visibiles';
Sbi.locale.ln['sbi.crosstab.menu.hiddenheader'] = 'Headers cachés';

//===================================================================
//CROSSTAB CALCULATED FIELDS WIZARD
//===================================================================
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.title'] = 'Champ Calculé';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.validate'] = 'Valide';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.clear'] = 'Nettoyer';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.ok'] = 'OK';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.info'] = 'Un champ calculé est composé des opérateurs mathématiques, des costantes et des variables. Les variables sont les identificateurs des colonnes. Pour insérer une colonne dans le tableau cliquer sur l header corréspondent. Nous notons que on peut insérer seulement des colonnes ou des groupes de colonnes, présents au niveau où on veut définir le champ calculé.';