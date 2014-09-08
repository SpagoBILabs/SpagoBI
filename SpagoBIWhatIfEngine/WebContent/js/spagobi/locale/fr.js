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
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		int: {
			decimalSeparator: '.',
			decimalPrecision: 0,
			groupingSeparator: ',',
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

//===================================================================
//HELP
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.help.content'] = 'Sélectionner les membres visibiles pour la dimension sélectionnée. Ces membres seront inclus dans l\'instruction sélectionnée d\'une requête MDX';
Sbi.locale.ln['sbi.olap.help.title'] = 'Aide';
Sbi.locale.ln['sbi.olap.execution.table.dimension.cannotchangehierarchy'] = 'Vous ne pouvez pas changer la hiérarchie lorsque vous avez des modifications à faire dans les données; vous devez préliminairement terminer les modifications.';

//===================================================================
//COMMONS
//===================================================================
Sbi.locale.ln['sbi.common.cancel'] = 'Annuler';
Sbi.locale.ln['sbi.common.close'] = 'Fermer';
Sbi.locale.ln['sbi.common.ok'] = 'Ok';
Sbi.locale.ln['sbi.common.select'] = 'Sélectionner';
Sbi.locale.ln['sbi.common.warning'] = 'Avertisement';
Sbi.locale.ln['sbi.common.next'] = 'Aprés';
Sbi.locale.ln['sbi.common.prev'] = 'Avant';
Sbi.locale.ln['sbi.common.wait'] = 'Attendre...';
Sbi.locale.ln['sbi.common.wait.long'] = 'S\'il vous plait, attendre! L\'opération va terminer dans quelques minutes.';

//===================================================================
//TOOLBAR
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_MDX'] = 'Requête Mdx';
Sbi.locale.ln['sbi.olap.toolbar.drill.mode'] = 'Drill';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_UNDO'] = 'Back';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FLUSH_CACHE'] = 'Recharger le model';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_FATHER_MEMBERS'] = 'Montrer les membres parents';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_SPANS'] = 'Cacher les spans';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SHOW_PROPERTIES'] = 'Montrer les propriétés';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_HIDE_EMPTY'] = 'Supprimer les lignes/colonnes vides';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE'] = 'Sauver';
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_SAVE_NEW'] = "Sauver comme une nouvelle version";
Sbi.locale.ln['sbi.olap.toolbar.lock'] = "Verrouiller le model";
Sbi.locale.ln['sbi.olap.toolbar.unlock'] = " Déverrouiller le model";
Sbi.locale.ln['sbi.olap.toolbar.lock_other'] = "Model verrouiller par d\'autres utilisateurs";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_VERSION_MANAGER'] = "Effacer la version";
Sbi.locale.ln['sbi.olap.toolbar.BUTTON_EXPORT_OUTPUT'] = "Assistant de sortie";


//===================================================================
//FILTRES
//===================================================================

Sbi.locale.ln['sbi.olap.execution.table.filter.collapse'] = 'Effacer tout';
Sbi.locale.ln['sbi.olap.execution.table.filter.expand'] = 'Etendre tout';
Sbi.locale.ln['sbi.olap.execution.table.filter.filter.title'] = 'Sélectionner un slicer';
Sbi.locale.ln['sbi.olap.execution.table.filter.dimension.title'] = 'Sélectionner les membres visibiles';
Sbi.locale.ln['sbi.olap.execution.table.filter.no.measure'] = 'Vous ne pouvez pas utiliser une mésure comme un filtre';
Sbi.locale.ln['sbi.olap.execution.table.filter.empty'] = ' Glisser-déposer ici le membre si vous désirez l\'utiliser comme un slicer';


//===================================================================
//DIMENSIONS
//===================================================================
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy'] = 'La hiérarchie sélectionnée est: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.selected.hierarchy.2'] = ' Vous pouvez la changer en opérant avec la forme suivante.';
Sbi.locale.ln['sbi.olap.execution.table.dimension.available.hierarchies'] = 'Hiérarchies possibles: ';
Sbi.locale.ln['sbi.olap.execution.table.dimension.no.enough'] = 'Vous devez avoir au moins une dimension dans les colonnes et dans les lignes';

//===================================================================
//WRITEBACK
//===================================================================
Sbi.locale.ln['sbi.olap.writeback.persist.error'] = 'Une erreur est arrivé lorsque vous étiez en train de sauver les modifications';
Sbi.locale.ln['sbi.olap.writeback.edit.no.zero'] =  'Il n\'est pas possible de modifier la cellule!! L\'algorithme de propagation courrent préserve les poids entre les cellules frères et sœurs; quand vous modifiez une cellule vide ou zéro, la contrainte ne sera plus valide dans les cellules enfants. Le déclenchement suivant que vous aurez, il prévoit nombreux algorithmes de propagation.';
Sbi.locale.ln['sbi.olap.writeback.edit.no.locked'] =  'Il n\'est pas possible de modifier le model si vous n'\avez pas déverrouillé le model.';


//===================================================================
//LOCK
//===================================================================
Sbi.locale.ln['sbi.olap.artifact.lock.error'] = 'L\'opération ne marche pas, le model vient d’être déverrouillé';
Sbi.locale.ln['sbi.olap.artifact.unlock.error'] = 'L\'opération de décharge ne marche pas, le model est encore enfermé';
Sbi.locale.ln['sbi.olap.artifact.unlock.errorOther'] = 'L\'opération de décharge ne marche pas, le model est verrouillé par l’utilisateur ';

//===================================================================
//MENU
//===================================================================
Sbi.locale.ln['sbi.olap.execution.menu.buttonMenu'] = 'Boutons du menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToMenu'] = 'Ajouter au menu';
Sbi.locale.ln['sbi.olap.execution.menu.addToToolbar'] = 'Ajouter à la toolbar';

//===================================================================
//VERSION MANAGER
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all'] = 'Vous ne pouvez pas éliminer toutes les versions';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current'] = 'Vous ne pouvez pas éliminer les versions courrentes';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.title'] = 'Sélectionner la version à effacer';
Sbi.locale.ln['sbi.olap.toolbar.versionmanagerwindow.version.select.warning'] = 'Attention: l’opération va continuer dans quelques minutes.';
Sbi.locale.ln['sbi.olap.control.controller.delete.version.ok'] = 'La version a étée effacée correctement';
//Sbi.locale.ln['sbi.olap.control.controller.delete.version.error'] = 'Une erreur est arrivé lorsque vous avez éliminé les versions.';

//===================================================================
//OUTPUT
//===================================================================
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.name'] = "Titre de la Table";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table.description'] = "Comment va se nommer la table du processus final?";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.row.delimiter'] = "Séparateur de lignes";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.filter.delimiter'] = "Délimiteur du filtre";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv.description'] = "S\'il vous plait, remplir le choix CSV";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.table'] = "table";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.csv'] = "ficher";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type'] = "Type de sortie";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.type.description'] = "Sélectionner le type de sortie pour l\'analyse. L\'opération va continuer dans quelques minutes. Vous pouvez travailler encore avec votre cube";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.title'] = "Wizard de sortie";
Sbi.locale.ln['sbi.olap.toolbar.export.wizard.version'] = "Version à exporter";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.ok'] = "Analyse exportée";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.error'] = "Erreur lorsque vous étiez en train d\'exporter les données";
Sbi.locale.ln['sbi.olap.toolbar.exportoutput.csv.window'] = "<h1>Exporter le résultat en CSV</h1><p>L\'opération va continuer dans quelques minutes. Vous pouvez travailler encore avec votre cube</p>";
