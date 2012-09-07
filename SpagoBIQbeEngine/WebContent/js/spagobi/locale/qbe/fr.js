/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();


//===================================================================
//MESSAGE BOX BUTTONS
//===================================================================
Ext.Msg.buttonText.yes = 'Oui'; 
Ext.Msg.buttonText.no = 'Non';


//===================================================================
//MESSAGE GENERAL
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.yes'] = 'Oui';
Sbi.locale.ln['sbi.qbe.messagewin.no'] = 'No';
Sbi.locale.ln['sbi.qbe.messagewin.cancel'] = 'Annuler';


//===================================================================
// MESSAGE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.messagewin.warning.title'] = 'Attention!';
Sbi.locale.ln['sbi.qbe.messagewin.error.title'] = 'Message d\'erreur';
Sbi.locale.ln['sbi.qbe.messagewin.info.title'] = 'Information';


//===================================================================
//SESSION EXPIRED
//===================================================================
Sbi.locale.ln['sbi.qbe.sessionexpired.msg'] = 'Votre session a expir\u00E9e. Essayer de r\u00E9-ex\u00E9cuter le document';


//===================================================================
//QBE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.qbepanel.worksheetdesignerpanel.tools.preview'] = 'Previsualiser feuille de calcul';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerytitle'] = 'La requ\u00EAte est vide';
Sbi.locale.ln['sbi.qbe.qbepanel.emptyquerymessage'] = 'La requ\u00EAte est vide et vous n\'avez pas la permission de cr\u00E9er de nouvelles requ\u00EAte. S\u00E9lectionnez une requ\u00EAte enregistr\u00E9e dans la liste des vues personnalis\u00E9es.';


//===================================================================
//QUERY EDITOR PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.queryeditor.title'] = 'Requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.title'] = 'Sch\u00E9ma';
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.expand'] = 'D\u00E9ployer'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.collapse'] = 'R\u00E9duire'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.save'] = 'Sauvegarder'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.flat'] = 'Vue aplatie'; 
Sbi.locale.ln['sbi.qbe.queryeditor.westregion.tools.addcalculated'] = 'Ajouter les champs calcul\u00E9s'; 

Sbi.locale.ln['sbi.qbe.queryeditor.savequery'] = 'Enregistrer la requ\u00EAte ...';
Sbi.locale.ln['sbi.qbe.queryeditor.querysaved'] = 'Requ\u00EAte sauvegard\u00E9e!';
Sbi.locale.ln['sbi.qbe.queryeditor.querysavedsucc'] = 'Requ\u00EAte sauvegard\u00E9e avec succ\u00E8s';
Sbi.locale.ln['sbi.qbe.queryeditor.msgwarning'] = 'La requ\u00EAte est incorrecte, voulez-vous l\'enregistrer?';
Sbi.locale.ln['sbi.qbe.queryeditor.saveqasview'] = 'Enregistrer la requ\u00EAte en tant que vue...';

Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.title'] = 'Editeur de requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.save'] = 'Sauvegarder la requ\u00EAte en tant que sous-projet';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.view'] = 'Enregistrer la requ\u00EAte en tant que vue';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.execute'] = 'Ex\u00E9cuter la requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.validate'] = 'Valider la requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.centerregion.tools.help'] = 'Aidez-moi';

Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.title'] = 'Biblioth\u00E8que de requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.delete'] = 'Supprimer la requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.add'] = 'Ajouter une requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.insert'] = 'Ins\u00E9rer une requ\u00EAte';
Sbi.locale.ln['sbi.qbe.queryeditor.eastregion.tools.wanringEraseRoot'] = 'Impossible de supprimer la requ\u00E8te principale';


//===================================================================
//EXPRESSION EDITOR
//===================================================================
Sbi.locale.ln['sbi.qbe.expreditor.title'] = 'Editeur d\'expression';
Sbi.locale.ln['sbi.qbe.expreditor.items'] = '\u00E9l\u00E9ments d\'Exp.';
Sbi.locale.ln['sbi.qbe.expreditor.operands'] = 'Op\u00E9randes';
Sbi.locale.ln['sbi.qbe.expreditor.operators'] = 'Op\u00E9rateurs';
Sbi.locale.ln['sbi.qbe.expreditor.structure'] = 'Exp. Structure';
Sbi.locale.ln['sbi.qbe.expreditor.clear'] = 'Effacer tout';
Sbi.locale.ln['sbi.qbe.expreditor.expression'] = 'Expression';
Sbi.locale.ln['sbi.qbe.expreditor.log'] =  'Log';
Sbi.locale.ln['sbi.qbe.expreditor.refresh'] =  'Actualiser la structure de l\'expression';
Sbi.locale.ln['sbi.qbe.expreditor.clearttp'] =  'Vider tous les champs s\u00E9lectionn\u00E9s';
Sbi.locale.ln['sbi.qbe.expreditor.filterdesc'] = 'Mettre la description du filtre ici';
Sbi.locale.ln['sbi.qbe.expreditor.operatordesc'] =  'Mettre ici la description des op\u00E9rateurs';


//===================================================================
//DATASTORE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.title'] = 'R\u00E9sultats';

Sbi.locale.ln['sbi.qbe.datastorepanel.grid.displaymsg'] = 'Affiche {0} - {1} of {2}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptymsg'] = 'Aucune donn\u00E9e \u00E0 afficher';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.emptywarningmsg'] = 'La requ\u00EAte ne renvoie aucune donn\u00E9es';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforeoverflow'] = 'Limite max du nombre d\'enregistrements';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afteroverflow'] = 'd\u00E9pass\u00E9';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.beforepagetext'] = 'Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.afterpagetext'] = 'de {0}';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.firsttext'] = 'Premi\u00E8re page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.prevtext'] = 'Page pr\u00E9c\u00E9dente';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.nexttext'] = 'Page suivante';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.lasttext'] = 'Derni\u00E8re Page';
Sbi.locale.ln['sbi.qbe.datastorepanel.grid.refreshtext'] = 'Actualiser';

Sbi.locale.ln['sbi.qbe.datastorepanel.button.tt.exportto'] = 'Exporter vers';


//===================================================================
//SAVE WINDOW
//===================================================================
Sbi.locale.ln['sbi.qbe.savewindow.desc'] = 'Description';
Sbi.locale.ln['sbi.qbe.savewindow.name'] = 'Nom' ;
Sbi.locale.ln['sbi.qbe.savewindow.saveas'] = 'Enregistrer sous ...' ;
Sbi.locale.ln['sbi.qbe.savewindow.selectscope'] = 'Choisir une port\u00E9e...' ;
Sbi.locale.ln['sbi.qbe.savewindow.scope'] = 'Port\u00E9e...';
Sbi.locale.ln['sbi.qbe.savewindow.save'] = 'Enregistrer';
Sbi.locale.ln['sbi.qbe.savewindow.cancel'] = 'Annuler';
Sbi.locale.ln['sbi.qbe.savewindow.public'] = 'Publique';
Sbi.locale.ln['sbi.qbe.savewindow.private'] = 'Priv\u00E9e';
Sbi.locale.ln['sbi.qbe.savewindow.publicdesc'] = 'Toute personne ex\u00E9cutant ce document verra vos sous-projets sauvegard\u00E9s';
Sbi.locale.ln['sbi.qbe.savewindow.privatedesc'] = 'La requ\u00EAte enregistr\u00E9e sera visible uniquement pour vous';
Sbi.locale.ln['sbi.qbe.savewindow.selectmetadata'] = 'Ins\u00E9rer m\u00E9tadonn\u00E9es';


//===================================================================
//FILTER GRID
//===================================================================
Sbi.locale.ln['sbi.qbe.filtergridpanel.title'] = 'Where clause';

Sbi.locale.ln['sbi.qbe.filtergridpanel.namePrefix'] = 'Filtre';

//column headers
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.name'] = 'Nom du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.desc'] = 'Filtre d\u00E9croissant.';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.loval'] = 'Valeur de l\'op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodesc'] = 'Op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lotype'] = 'Type de l\'op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lodef'] = 'Valeur par d\u00E9faut de l\'op\u00E9rande';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.lolast'] = 'Derni\u00E8re valeur op\u00E9rande gauche';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.operator'] = 'Op\u00E9rateur';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.roval'] = 'Valeur de l\'op\u00E9rande droit';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodesc'] = 'Op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rotype'] = 'Type de l\'op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rodef'] = 'Valeur par d\u00E9faut de l\'op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.rolast'] = 'Derni\u00E8re valeur op\u00E9rande droite';

Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.isfree'] = 'est pour l\'invite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.delete'] = 'Supprimer tout';
Sbi.locale.ln['sbi.qbe.filtergridpanel.headers.boperator'] = 'Ope. bool\u00E9en';

//column tooltip
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.notdef'] = 'Aide tooltip pas encore d\u00E9finie';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.name'] = 'ID du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.desc'] = 'Description de l\'objectif du filtre';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.loval'] = 'Valeur de l\'op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodesc'] = 'Op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lotype'] = 'Type de l\'op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lodef'] = 'Valeur par d\u00E9faut de l\'op\u00E9rande gauche';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.lolast'] = 'Derni\u00E8re valeur op\u00E9rande gauche';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.operator'] = 'Op\u00E9rateur';

Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.roval'] = 'Valeur de l\'op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodesc'] = 'Op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rotype'] = 'Type de l\'op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rodef'] = 'Valeur par d\u00E9faut de l\'op\u00E9rande droite';
Sbi.locale.ln['sbi.qbe.filtergridpanel.tooltip.rolast'] = 'Derni\u00E8re valeur op\u00E9rande droite';


//boolean operators
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.and'] = 'ET';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.name.or'] = 'OU';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.and'] = 'Relier ce filtre et le filtre suivant avec l\'op\u00E9rateur bool\u00E9en ET';
Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.desc.or'] = 'Relier ce filtre et le filtre suivant avec l\'op\u00E9rateur bool\u00E9en OU';

Sbi.locale.ln['sbi.qbe.filtergridpanel.boperators.editor.emptymsg'] = 'S\u00E9lectionner un op\u00E9rateur...';


//filter operators

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.none'] = 'aucun';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eq'] = '\u00E9gale \u00E0';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.noteq'] = 'n\'est pas \u00E9gale \u00E0';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.gt'] = 'plus grand que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqgt'] = 'plus grand ou \u00E9gale \u00E0';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.lt'] = 'plus petit que';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.eqlt'] = 'plus petit ou \u00E9gale \u00E0';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.starts'] = 'commence par';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notstarts'] = 'ne commence pas par';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.ends'] = 'finit par';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notends'] = 'ne finit pas par';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.contains'] = 'contient';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notcontains'] = 'ne contient pas';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.between'] = 'compris entre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notbetween'] = 'n\'est pas compris';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.in'] = 'dans';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notin'] = 'n\'est pas dans';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.notnull'] = 'n\'est pas nul';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.name.isnull'] = 'est nul';

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.none'] = 'aucun filtre appliqu\u00E9';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eq'] = 'vrai si la valeur du champ est \u00E9gale \u00E0 la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.noteq'] = 'vrai si la valeur du champ est diff\u00E9rente de la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.gt'] = 'vrai si la valeur du champ est plus grande que la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eqgt'] = 'vrai si la valeur du champ est \u00E9gale ou sup\u00E9rieur \u00E0 la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.lt'] = 'vrai si la valeur du champ est plus petite que la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.eqlt'] = 'vrai si la valeur du champ commence par la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.starts'] = 'vrai si la valeur du champ est inf\u00E9rieure ou \u00E9gale \u00E0 la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notstarts'] = 'true iff the field\'s value doesn\'t start with filter\'s value';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.ends'] = 'vrai si la valeur du champs fini par la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notends'] = 'vrai si la valeur du champ ne fini pas par la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.contains'] = 'vrai si la valeur du champ contient la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notcontains'] = 'vrai si la valeur du champ ne contient pas la valeur du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.between'] = 'vrai si la valeur du champ est comprise dans l\'intervalle sp\u00E9cifi\u00E9 par le filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notbetween'] = 'vrai si la valeur du champ n\'est pas comprise dans l\'intervalle sp\u00E9cifi\u00E9 dans le filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.in'] = 'vrai si la valeur du champ est \u00E9gale \u00E0 l\'une des valeurs du filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notin'] = 'vrai si la valeur du champ est diff\u00E9rente de toutes les valeurs sp\u00E9cifi\u00E9es dans le filtre';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.notnull'] = 'vrai si la valeur du champ n\'est pas nul';
Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.desc.isnull'] = 'vrai si la valeur du champ est nul';

Sbi.locale.ln['sbi.qbe.filtergridpanel.foperators.editor.emptymsg'] = 'S\u00E9lectionner un op\u00E9rateur...';

//buttons 
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.add'] = 'Nouveau';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.add'] = 'Cr\u00E9er un nouveau filtre';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.delete'] = 'Supprimer tout';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.delete'] = 'Supprimer tous les filtres';

Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.text.wizard'] = 'Assistant Exp.';
Sbi.locale.ln['sbi.qbe.filtergridpanel.buttons.tt.wizard'] = 'Visualiser l\'assistant de composition des filtres';

//warnings
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.title'] = 'Supprimer le filtre ?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.delete.msg'] = 'Vous \u00EAtes sur le point de supprimer un filtre qui est utilis\u00E9 par une expression (voir l\'assistant des expressions). Le supprimer r\u00E9initialise compl\u00E8tement l\'expression. Souhaitez-vous toujours le supprimer ?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.title'] = 'Supprimer toutes les filtres ?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.deleteAll.msg'] = 'Vous \u00EAtes sur le point de supprimer tous les filtres. Etes vous s\u00DBr de vouloir les supprimer ?';


Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.title'] = 'Changer le connecteur booleen ?';
Sbi.locale.ln['sbi.qbe.filtergridpanel.warning.changebolop.msg'] = 'Changer le connecteur booleen de ce filtre r\u00E9initialise l\'expression associ\u00E9e (voir l\'assistant des expressions). Souhaitez-vous toujours le supprimer ?';


// ===================================================================
//	SELECT GRID
// ===================================================================
Sbi.locale.ln['sbi.qbe.selectgridpanel.title'] = 'S\u00E9lection des champs';

// column headers
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.visible'] = 'Visible';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.include'] = 'Inclure';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.group'] = 'Groupes';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.filter'] = 'Filtre';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.having'] = 'Having';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.entity'] = 'Entit\u00E9';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.alias'] = 'Alias';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.order'] = 'Ordonner';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.function'] = 'Fonction';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.field'] = 'Champ';
Sbi.locale.ln['sbi.qbe.selectgridpanel.headers.delete'] = 'Supprimer tout';

//aggregation functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.none'] = 'aucun';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.sum'] = 'somme';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.avg'] = 'moyenne';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.max'] = 'maximum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.min'] = 'minimum';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.count'] = 'd\u00E9ecompte';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'] = 'D\u00E9compte distinct';

Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.none'] = 'Aucune fonction d\'agr\u00E9gation appliqu\u00E9e';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.sum'] = 'Calcule la somme des valeurs du groupe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.avg'] = 'Calcule la moyenne de toutes les valeurs du groupe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.max'] = 'Calcule la valeur maximale dans le groupe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.min'] = 'Calcule la valeur minimale dans le groupe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.count'] = 'Calcule le nombre de valeurs pr\u00E9sentes dans le groupe';
Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct'] = 'Retourner le d\u00E9compte de valeurs distinctes dans le groupe';

Sbi.locale.ln['sbi.qbe.selectgridpanel.aggfunc.editor.emptymsg'] = 'S\u00E9lectionner une fonction...';


// sorting functions
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.none'] = 'aucun';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.asc'] = 'croissant';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.name.desc'] = 'd\u00E9croissant';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.none'] = 'Aucun d\'ordre appliqu\u00E9 \u00E0 ces colonnes';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.asc'] = 'Appliquer un ordre croissant sur les colonnes';
Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.desc.desc'] = 'Appliquer un ordre d\u00E9croissant sur les colonnes';

Sbi.locale.ln['sbi.qbe.selectgridpanel.sortfunc.editor.emptymsg'] = 'Choisir l\'ordre...';

//buttons 
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.hide'] = 'Cacher les invisibles';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.hide'] = 'Cacher tous les champs invisibles';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.group'] = 'Grouper par entit\u00E9';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.group'] = 'Grouper les champs par entit\u00E9s parentes';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.add'] = 'Ajouter un champ calcul\u00E9';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.add'] = 'Ajouter un champ calcul\u00E9 ad-hoc (ex. valide uniquement pour une requ\u00EAte)';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.expert'] = 'Utilisateur Expert';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.delete'] = 'Supprimer tout';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.delete'] = 'Supprimer les champ s\u00E9lectionner';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.deleteall'] = 'Supprimer tout';
Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.tt.deleteall'] = 'Supprimer tous les champs s\u00E9lectionn\u00E9s';

Sbi.locale.ln['sbi.qbe.selectgridpanel.buttons.text.distinct'] = 'Valeurs distinctes';

Sbi.locale.ln['sbi.qbe.freeconditionswindow.title'] = 'Remplir les conditions libres';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.apply'] = 'Appliquer';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.cancel'] = 'Annuler';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restoredefaults'] = 'Restaurer les valeurs par d\u00E9faut';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.saveasdefaults'] = 'Enregistrer comme par d\u00E9faut';
Sbi.locale.ln['sbi.qbe.freeconditionswindow.buttons.text.restorelast'] = 'Restaurer dernier';

//===================================================================
//QUERY CATALOGUE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.cataloguepanel.title'] = 'Biblioth\u00E8que de requ\u00EAte';

//===================================================================
//HAVING CLAUSE PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.havinggridpanel.title'] = 'Condition de groupement';

//===================================================================
//DOCUMENT PARAMETERS PANEL
//===================================================================
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.title'] = 'Param\u00E8tre du document';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.emptytext'] = 'Ce document n\'a pas de param\u00E8tre';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.headers.label'] = 'Titre';
Sbi.locale.ln['sbi.qbe.documentparametersgridpanel.parameterreference'] = 'Param\u00E8tre';
Sbi.locale.ln['sbi.qbe.parametersgridpanel.parameterreference'] = 'Param\u00E8tre';


//===================================================================
//DATA STORE PANEL AND EXTERNAL SERVICES
//===================================================================
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.title'] = 'Le service a correctement \u00E9t\u00E9 invoqu\u00E9';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.serviceresponse'] = 'Le service a retourn\u00E9 ce message :';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.title'] = 'Erruer';
Sbi.locale.ln['sbi.qbe.datastorepanel.externalservices.errors.missingcolumns'] = 'Le service a besoin des colonnes suivantes : ';
Sbi.locale.ln['sbi.qbe.datastore.refreshgrid'] = 'Restaurer le style grille'

//===================================================================
//CALCULATED FIELD WIZARD
//===================================================================
Sbi.locale.ln['sbi.qbe.inlineCalculatedFields.title'] = 'Assistant Champ Calcul\u00e9';

Sbi.locale.ln['sbi.qbe.calculatedFields.title'] = 'Assistant Champ Calcul\u00e9 (Modalit\u00e9 Expert)';
Sbi.locale.ln['sbi.qbe.inlineCalculatedFields.title'] = 'Assistant Champ Calcul\u00e9 (Modalit\u00e9 Simple)';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.title'] = 'Validation';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.success.text'] = 'Validation OK';
Sbi.locale.ln['sbi.qbe.calculatedFields.validationwindow.fail.title'] = 'Validation \u00E9chou\u00E9e';
Sbi.locale.ln['sbi.qbe.calculatedFields.expert.nofilterwindow.title'] = 'Attention: impossible d\'utiliser un filtre avec ce type de champ ';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.ok'] = 'OK';
Sbi.locale.ln['sbi.qbe.calculatedFields.buttons.text.cancel'] = 'Annuler';

Sbi.locale.ln['sbi.qbe.calculatedFields.fields'] = 'Champs';
Sbi.locale.ln['sbi.qbe.calculatedFields.attributes'] = 'Attributs';
Sbi.locale.ln['sbi.qbe.calculatedFields.parameters'] = 'Param\u00e8tres';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions'] = 'Fonctions';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.arithmentic'] = 'Fonctions arithm\u00e9tiques';
Sbi.locale.ln['sbi.qbe.calculatedFields.functions.script'] = 'Fonctions Groovy ';
Sbi.locale.ln['sbi.qbe.calculatedFields.aggrfunctions'] = 'Fonctions d\'agr\u00e9gation';
Sbi.locale.ln['sbi.qbe.calculatedFields.datefunctions'] = 'Fonction Date';
Sbi.locale.ln['sbi.qbe.calculatedFields.string.type'] = 'Si l\'expression du script retourne une cha\u00eene de caract\u00e8res brut';
Sbi.locale.ln['sbi.qbe.calculatedFields.html.type'] = 'Si l\'expression du script retourne un fragment html valide';
Sbi.locale.ln['sbi.qbe.calculatedFields.num.type'] = 'Si l\'expression du script retourne un nombre';
Sbi.locale.ln['sbi.qbe.calculatedFields.date.type'] = 'Si l\'expression du script renvoie une date';
Sbi.locale.ln['sbi.qbe.calculatedFields.add'] = 'Ajouter champ calcul\u00e9';
Sbi.locale.ln['sbi.qbe.calculatedFields.remove'] = 'Supprimer champ calcul\u00e9';
Sbi.locale.ln['sbi.qbe.calculatedFields.edit'] = 'Modifier champ calcul\u00e9';
Sbi.locale.ln['sbi.qbe.calculatedFields.add.error'] = 'Impossible d\'ajouter champ calcul\u00e9 \u00e0 un noeud de type [{0}]';
