/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//CROSSTAB DESIGNER
//===================================================================
Sbi.locale.ln['sbi.crosstab.crosstabdesignerpanel.title'] = 'Concepteur de tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.tools.preview'] = 'Pr\u00E9visualiser le tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.title'] = 'D\u00E9finition du tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.rows'] = 'Lignes';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.columns'] = 'Colonnes';
Sbi.locale.ln['sbi.crosstab.crosstabdefinitionpanel.measures'] = 'Mesures';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.title'] = 'Suppression impossible';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresent'] = 'L\'attribut a d\u00E9j\u00E0 \u00E9t\u00E9 choisi';
//Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.attributealreadypresentfilters'] = 'L\'attribut a d\u00E9j\u00E0 \u00E9t\u00E9 choisi';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.cannotdrophere.measures'] = 'Impossible de glisser la mesure sur les lignes ou les colonnes : vous devez les glisser dans la section centrale du tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.attributescontainerpanel.tools.tt.removeall'] = ['Tout Supprimer'];

Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.title'] = 'Suppression impossible';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.measurealreadypresent'] = 'La mesure a d\u00E9j\u00E0 \u00E9t\u00E9 choisie';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.attributes'] = 'Impossible de glisser les attributs dans la section centrale : vous devez les placer sur les lignes ou les colonnes';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.cannotdrophere.postlinecalculated'] = 'Vous ne pouvez pas utiliser les champs calcul\u00E9s dans un tableau crois\u00E9';

Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.showdetailswizard'] = 'Pr\u00E9senter les d\u00E9tails';
Sbi.locale.ln['sbi.crosstab.measurescontainerpanel.tools.tt.removeall'] = ['Tout supprimer'];

Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.title'] = 'D\u00E9tails du tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.measureson'] = 'Mesures en';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.rows'] = 'Ligne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.columns'] = 'Colonne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.onrows'] = 'En lignes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.oncolumns'] = 'En colonnes';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsonrows'] = 'Pr\u00E9senter totaux';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsonrows'] = 'Pr\u00E9senter sous-totaux';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatetotalsoncolumns'] = 'Pr\u00E9senter totaux';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.calculatesubtotalsoncolumns'] = 'Pr\u00E9senter sous-totaux';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.buttons.cancel'] = 'Annuler';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.percenton'] = 'Pourcentage calcul\u00e9 sur';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.row'] = 'Ligne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.column'] = 'Colonne';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.nopercent'] = 'non';
Sbi.locale.ln['sbi.crosstab.crosstabdetailswizard.maxcellnumber'] = 'Nombre maxium de cellules';

Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.title'] = 'Choisissez la fonction d\'agr\u00E9gation pour la mesure';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.crosstab.chooseaggregationfunctionwindow.buttons.cancel'] = 'Annuler';

Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.title'] = 'Aper\u00E7u du tableau crois\u00E9';
Sbi.locale.ln['sbi.crosstab.crosstabpreviewpanel.overflow.warning'] = 'Le nombre de cellules est sup\u00e9rieur \u00e0 celui par d\u00e9faut. Vous pouvez trouver toutes les donn\u00e9es avec l\'exportation en XLS.';

Sbi.locale.ln['sbi.crosstab.crossTabValidation.title'] = 'Le tableau crois\u00E9 n\'est pas valide';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMeasure'] = 'Utiliser au moins une mesure';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noAttribute'] = 'Utiliser au moins un attribut';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noSegmentAttribute'] = 'Il faut utiliser un segment attribut';
Sbi.locale.ln['sbi.crosstab.crossTabValidation.noMandatoryMeasure'] = 'Il faut utiliser une mesure';

//===================================================================
//CROSSTAB
//===================================================================

Sbi.locale.ln['sbi.crosstab.header.total.text'] = 'Total';

Sbi.locale.ln['sbi.crosstab.menu.addcalculatedfield'] = 'Ajouter un champ calcul\u00E9';
Sbi.locale.ln['sbi.crosstab.menu.removecalculatedfield'] = 'Supprimer un champ calcul\u00E9';
Sbi.locale.ln['sbi.crosstab.menu.modifycalculatedfield'] = 'Modifier un champ calcul\u00E9';
Sbi.locale.ln['sbi.crosstab.menu.hideheader'] = 'Masquer l\'ent\u00EAte';
Sbi.locale.ln['sbi.crosstab.menu.hideheadertype'] = 'Masquer toutes les ent\u00EAtes de ce type';
Sbi.locale.ln['sbi.crosstab.menu.hidemeasure'] = 'Mesures';
Sbi.locale.ln['sbi.crosstab.menu.hiddenheader'] = 'Masquer les ent\u00EAtes';

//===================================================================
//CROSSTAB CALCULATED FIELDS WIZARD
//===================================================================
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.title'] = 'Champ caclcul\u00E9';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.validate'] = 'Valider';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.clear'] = 'Effacer';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.ok'] = 'OK';
Sbi.locale.ln['sbi.crosstab.calculatefieldwizard.info'] = 'Un champ calcul\u00E9 est une expression compos\u00E9e par op\u00E9rateurs math\u00E9matiques, des constantes et des variables. Les variables sont les identifiants des colonnes ou des lignes. Pour ins\u00E9rer une ligne ou une colonne vous devez cliquer au niveau des ent\u00EAtes correspondantes. Vous pouvez cliquer sur les ent\u00EAtes des niveaux o\u00F9 le champ calcul\u00E9 sera ajout\u00E9.';


