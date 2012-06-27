/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

//===================================================================
//FORM BUILDER PAGE
//===================================================================
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.title'] = 'Concepteur';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.toolbar.save'] = 'Enregistrer le mod\u00E8le';

Sbi.locale.ln['sbi.formbuilder.formbuilderpage.templatesaved.title'] = 'Mod\u00E8le enregistr\u00E9';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.templatesaved.msg'] = 'Mod\u00E8 enregistr\u00E9 avec succ\u00E8s!!';
Sbi.locale.ln['sbi.formbuilder.formbuilderpage.validationerrors.title'] = 'Erreur';

Sbi.locale.ln['sbi.formbuilder.formpanel.title'] = 'Concepteur de formulaire';

Sbi.locale.ln['sbi.formbuilder.formpreviewpage.title'] = 'Pr\u00E9visualiser';

Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.title'] = 'Champs s\u00E9lectionn\u00E9s';
Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.tools.refresh'] = 'Rafra\u00E9chier les champs de la requ\u00EAte';
Sbi.locale.ln['sbi.formbuilder.queryfieldspanel.fieldname'] = 'Nom Champ';

Sbi.locale.ln['sbi.formbuilder.templateeditorpanel.title'] = 'Conpteur de formulaire';

Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.documentfield.label'] = 'Document';
Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.documentfield.emptytext'] = 'Selectionner a document...';
Sbi.locale.ln['sbi.formtemplate.documenttemplatebuilder.startediting'] = 'D\u00E9marrer le concepteur...';

Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.title'] = 'Filtre dynamique';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.emptymsg'] = 'Cliquer sur un bouton en haut \u00E0 doirte pour ajouter un filtre dynamique';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.grouptitle'] = 'Groupe de flitres dynamiques';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltereditorpanel.filteritemname'] = 'Groupe de filtres dynamiques';

Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.grouptitle'] = 'Groupe de filtres dynamiques';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.emptymsg'] = 'Faire glisser un champ ici pour ajouter une option du filtre dynamique';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.edit'] = 'Editer';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupeditor.remove'] = 'Supprimer';


Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.title'] = 'D\u00E9finition du groupe de filtres dynamiques';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.fields.filtername.label'] = 'Nom';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.fields.operatorfield.label'] = 'Op\u00E9rateur';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.formbuilder.dynamicfiltergroupwizard.buttons.cancel'] = 'Annuler';


Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.title'] = 'Variable de rupture';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.emptymsg'] = 'Glisser un champs pour ajouter une nouvelle variable de rupture';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.grouptitle'] = 'Variable';
Sbi.locale.ln['sbi.formbuilder.variableeditorpanel.validationerrors.missingadmissiblefields'] = 'Manque un champ utile pour cr\u00E9er la variable de rupture';

Sbi.locale.ln['sbi.formbuilder.variablegroupeditor.grouptitle'] = 'Variable de rupture';
Sbi.locale.ln['sbi.formbuilder.variablegroupeditor.emptymsg'] = 'Glisser un champ ici pour ajouter une nouvelle valeur \u00E0 la variable';

Sbi.locale.ln['sbi.formbuilder.editordroptarget.wrongdropmsg.title'] = 'Mauvaise source gliss\u00E9e';
Sbi.locale.ln['sbi.formbuilder.editordroptarget.wrongdropmsg.msg'] = 'S\u00E9lectionner juste un champ';


Sbi.locale.ln['sbi.formbuilder.editorpanel.add'] = 'Ajouter';
Sbi.locale.ln['sbi.formbuilder.editorpanel.clearall'] = 'Tout effacer';

Sbi.locale.ln['sbi.formbuilder.inlineeditor.edit'] = 'Editer';
Sbi.locale.ln['sbi.formbuilder.inlineeditor.remove'] = 'Supprimer';


Sbi.locale.ln['sbi.formbuilder.staticopenfiltereditorpanel.title'] = 'Filtres statiques ouvert';
Sbi.locale.ln['sbi.formbuilder.staticopenfiltereditorpanel.emptymsg'] = 'Glisser un champ ici pour cr\u00E9er un nouveau filtre statique ouvert';

Sbi.locale.ln['sbi.formbuilder.staticopenfiltergroupeditor.edit'] = 'Editer';
Sbi.locale.ln['sbi.formbuilder.staticopenfiltergroupeditor.remove'] = 'Supprimer';


Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.title'] = 'D\u00E9finition filtres statiques ouverts';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filtername.label'] = 'Nom';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filterentity.label'] = 'Champ';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.filteroperator.label'] = 'Op\u00E9rateur';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.maxselectionnumber.label'] = 'Selection Max';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.orderbyfield.label'] = 'Ordonn\u00E9 par';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.fields.ordertype.label'] = 'Type d\'ordonnancement';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.title'] = 'Lookup query details';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.promptvalues'] = 'Pr\u00E9senter l\'invite de valeur';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.donotqueryrootentity'] = 'Il y a des champs autoris\u00E9s pour les filtres';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.lookupquerydetailssection.queryrootentity'] = 'Il y a des champs autoris\u00E9s pour la racine';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.buttons.cancel'] = 'Annuler';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.customquerydetailssection.title'] = 'Requ\u00EAte personnalis\u00E9e';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.standardquerydetailssection.title'] = 'Requ\u00EAte Standard';
Sbi.locale.ln['sbi.formbuilder.staticopenfilterwizard.customquerydetailssection.lookupquery'] = 'Requ\u00EAte de recherche';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.title'] = 'Filtres statiques ferm\u00E9s';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.emptymsg'] = 'Cliquer sur le bouton en haut \u00E0 doite pour ajouter un grope de flitre';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltereditorpanel.filteritemname'] = 'Groupe de filtres statiques ferm\u00E9s';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.add'] = 'Ajouter';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.edit'] = 'Editer';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupeditor.toolbar.remove'] = 'Supprimer';


Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.title'] = 'D\u00E9finition filtres statiques ferm\u00E9s';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.noselectiontext'] = 'Tous';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.grouptitle.label'] = 'Nom';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.label'] = 'Activer s\u00E9lection simple';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.no'] = 'Non';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.enablesingleselection.yes'] = 'Oui';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.label'] = 'Autoriser "Aucune s\u00E9lection"';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.no'] = 'Non';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.allownoselection.yes'] = 'Oui';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.noselectionoptionlabel.label'] = 'Pas de s\u00E9lection de libell\u00E9 d\'option';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.label'] = 'Connecteur bouleen';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.and'] = 'Et';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.booleanconnector.or'] = 'Ou';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.fields.options'] = 'Options';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.formbuilder.staticclosefiltergroupwizard.buttons.cancel'] = 'Annuler';


Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.title'] = 'D\u00E9finition filtres statiques ferm\u00E9s';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.filtertitle.label'] = 'Nom';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.leftoperand.label'] = 'Champ';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.operator.label'] = 'Op\u00E9rateur';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.fields.rightoperand.label'] = 'Valeur';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.buttons.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.formbuilder.staticclosefilterwizard.buttons.cancel'] = 'Annuler';


