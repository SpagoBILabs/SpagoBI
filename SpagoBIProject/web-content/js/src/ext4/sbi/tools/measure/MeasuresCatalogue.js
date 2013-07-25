/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.tools.measure.MeasuresCatalogue', {
	extend: 'Sbi.widgets.grid.GroupedGrid'

		, constructor: function(config) {

			var columns = this.buildColumns();
			thisPanel = this;
			var joinMeasuresButton = Ext.create('Ext.Button', {
				text    : 'Join',
				tooltip :'Join',
				hidden	:true,
				handler : function() {
					this.hide();
					selectMeasuresButton.show();
					thisPanel.columns[thisPanel.columns.length-1].hide();
				}
			});

			var selectMeasuresButton = Ext.create('Ext.Button', {
				text    : 'Select',
				tooltip :'Select',
				handler : function() {
					this.hide();
					joinMeasuresButton.show();
					thisPanel.columns[thisPanel.columns.length-1].show();
				}
			});

			var myconfig = {
					store: this.buildDataStore(), 
					columns: columns,
					dockedItems : [{
						xtype: 'toolbar',
						items: [selectMeasuresButton, joinMeasuresButton]
					}],
					selModel: Ext.create('Ext.selection.CheckboxModel', {
						injectCheckbox: columns.length+1,
						
					    getHeaderConfig: function() {
					        var me = this,
					            showCheck = me.showHeaderCheckbox !== false;

					        return {
					            isCheckerHd: showCheck,
					            text : '&#160;',
					            width: me.headerWidth,
					            sortable: false,
					            draggable: false,
					            resizable: false,
					            hideable: false,
					            menuDisabled: true,
					            hidden: true,//TO HIDE THE COLUMN AT THE BIGINNING
					            dataIndex: '',
					            cls: showCheck ? Ext.baseCSSPrefix + 'column-header-checkbox ' : '',
					            renderer: Ext.Function.bind(me.renderer, me),
					            editRenderer: me.editRenderer || me.renderEmpty,
					            locked: me.hasLockedHeader()
					        };
					    }
					}),
					plugins: [{
						ptype: 'rowexpander',
						rowBodyTpl : [
						              'ssssssssssss'
						              ]
					}]
			};

			this.callParent([myconfig]);

//			this.on("render",function(){
//				this.columns[this.columns.length-1].hide();
//			},this);
		},

		buildColumns: function(){
			var columns = [{
				text: 'Name',
				flex: 1,
				dataIndex: 'alias'
			},{
				text: 'Cuisine',
				flex: 1,
				dataIndex: 'id'
			}];

			return columns;
		},

		buildDataStore: function(){
			
			return Ext.create('Ext.data.Store', {
			    model: 'Sbi.tools.measure.MeasureModel',
			    autoLoad: true
			});
			
//			Ext.define('Restaurant', {
//				extend: 'Ext.data.Model',
//				fields: ['name', 'cuisine','add']
//			});
//
//			var restaurants = Ext.create('Ext.data.Store', {
//				storeId: 'restaraunts',
//				model: 'Restaurant',
//				// groupField: 'cuisine',
//				sorters: ['cuisine','name'],
//				data: [{
//					name: 'Cheesecake Factory',
//					cuisine: 'American'
//						,add:'a'},{
//							name: 'University Cafe',
//							cuisine: 'American'
//								,add:'a'},{
//									name: 'Slider Bar',
//									cuisine: 'American'
//										,add:'a'},{
//											name: 'Shokolaat',
//											cuisine: 'American'
//												,add:'a'},{
//													name: 'Gordon Biersch',
//													cuisine: 'American'
//														,add:'a'},{
//															name: 'Crepevine',
//															cuisine: 'American'
//																,add:'a'},{
//																	name: 'Creamery',
//																	cuisine: 'American'
//																		,add:'a'},{
//																			name: 'Old Pro',
//																			cuisine: 'American'
//																				,add:'a'},{
//																					name: 'Nola\'s',
//																					cuisine: 'Cajun'
//																						,add:'a'},{
//																							name: 'House of Bagels',
//																							cuisine: 'Bagels'
//																								,add:'a'},{
//																									name: 'The Prolific Oven',
//																									cuisine: 'Sandwiches'
//																										,add:'a'},{
//																											name: 'La Strada',
//																											cuisine: 'Italian'
//																												,add:'a'},{
//																													name: 'Buca di Beppo',
//																													cuisine: 'Italian'
//																														,add:'a'},{
//																															name: 'Pasta?',
//																															cuisine: 'Italian'
//																																,add:'a'},{
//																																	name: 'Madame Tam',
//																																	cuisine: 'Asian'
//																																		,add:'a'},{
//																																			name: 'Sprout Cafe',
//																																			cuisine: 'Salad'
//																																				,add:'a'},{
//																																					name: 'Pluto\'s',
//																																					cuisine: 'Salad'
//																																						,add:'a'},{
//																																							name: 'Junoon',
//																																							cuisine: 'Indian'
//																																								,add:'a'},{
//																																									name: 'Bistro Maxine',
//																																									cuisine: 'French'
//																																										,add:'a'},{
//																																											name: 'Three Seasons',
//																																											cuisine: 'Vietnamese'
//																																												,add:'a'},{
//																																													name: 'Sancho\'s Taquira',
//																																													cuisine: 'Mexican'
//																																														,add:'a'},{
//																																															name: 'Reposado',
//																																															cuisine: 'Mexican'
//																																																,add:'a'},{
//																																																	name: 'Siam Royal',
//																																																	cuisine: 'Thai'
//																																																		,add:'a'},{
//																																																			name: 'Krung Siam',
//																																																			cuisine: 'Thai'
//																																																				,add:'a'},{
//																																																					name: 'Thaiphoon',
//																																																					cuisine: 'Thai'
//																																																						,add:'a'},{
//																																																							name: 'Tamarine',
//																																																							cuisine: 'Vietnamese'
//																																																								,add:'a'},{
//																																																									name: 'Joya',
//																																																									cuisine: 'Tapas'
//																																																										,add:'a'},{
//																																																											name: 'Jing Jing',
//																																																											cuisine: 'Chinese'
//																																																												,add:'a'},{
//																																																													name: 'Patxi\'s Pizza',
//																																																													cuisine: 'Pizza'
//																																																														,add:'a'},{
//																																																															name: 'Evvia Estiatorio',
//																																																															cuisine: 'Mediterranean'
//																																																																,add:'a'},{
//																																																																	name: 'Cafe 220',
//																																																																	cuisine: 'Mediterranean'
//																																																																		,add:'a'},{
//																																																																			name: 'Cafe Renaissance',
//																																																																			cuisine: 'Mediterranean'
//																																																																				,add:'a'},{
//																																																																					name: 'Kan Zeman',
//																																																																					cuisine: 'Mediterranean'
//																																																																						,add:'a'},{
//																																																																							name: 'Gyros-Gyros',
//																																																																							cuisine: 'Mediterranean'
//																																																																								,add:'a'},{
//																																																																									name: 'Mango Caribbean Cafe',
//																																																																									cuisine: 'Caribbean'
//																																																																										,add:'a'},{
//																																																																											name: 'Coconuts Caribbean Restaurant &amp; Bar',
//																																																																											cuisine: 'Caribbean'
//																																																																												,add:'a'},{
//																																																																													name: 'Rose &amp; Crown',
//																																																																													cuisine: 'English'
//																																																																														,add:'a'},{
//																																																																															name: 'Baklava',
//																																																																															cuisine: 'Mediterranean'
//																																																																																,add:'a'},{
//																																																																																	name: 'Mandarin Gourmet',
//																																																																																	cuisine: 'Chinese'
//																																																																																		,add:'a'},{
//																																																																																			name: 'Bangkok Cuisine',
//																																																																																			cuisine: 'Thai'
//																																																																																				,add:'a'},{
//																																																																																					name: 'Darbar Indian Cuisine',
//																																																																																					cuisine: 'Indian'
//																																																																																						,add:'a'},{
//																																																																																							name: 'Mantra',
//																																																																																							cuisine: 'Indian'
//																																																																																								,add:'a'},{
//																																																																																									name: 'Janta',
//																																																																																									cuisine: 'Indian'
//																																																																																										,add:'a'},{
//																																																																																											name: 'Hyderabad House',
//																																																																																											cuisine: 'Indian'
//																																																																																												,add:'a'},{
//																																																																																													name: 'Starbucks',
//																																																																																													cuisine: 'Coffee'
//																																																																																														,add:'a'},{
//																																																																																															name: 'Peet\'s Coffee',
//																																																																																															cuisine: 'Coffee'
//																																																																																																,add:'a'},{
//																																																																																																	name: 'Coupa Cafe',
//																																																																																																	cuisine: 'Coffee'
//																																																																																																		,add:'a'},{
//																																																																																																			name: 'Lytton Coffee Company',
//																																																																																																			cuisine: 'Coffee'
//																																																																																																				,add:'a'},{
//																																																																																																					name: 'Il Fornaio',
//																																																																																																					cuisine: 'Italian'
//																																																																																																						,add:'a'},{
//																																																																																																							name: 'Lavanda',
//																																																																																																							cuisine: 'Mediterranean'
//																																																																																																								,add:'a'},{
//																																																																																																									name: 'MacArthur Park',
//																																																																																																									cuisine: 'American'
//																																																																																																										,add:'a'},{
//																																																																																																											name: 'St Michael\'s Alley',
//																																																																																																											cuisine: 'Californian'
//																																																																																																												,add:'a'},{
//																																																																																																													name: 'Osteria',
//																																																																																																													cuisine: 'Italian'
//																																																																																																														,add:'a'},{
//																																																																																																															name: 'Vero',
//																																																																																																															cuisine: 'Italian'
//																																																																																																																,add:'a'},{
//																																																																																																																	name: 'Cafe Renzo',
//																																																																																																																	cuisine: 'Italian'
//																																																																																																																		,add:'a'},{
//																																																																																																																			name: 'Miyake',
//																																																																																																																			cuisine: 'Sushi'
//																																																																																																																				,add:'a'},{
//																																																																																																																					name: 'Sushi Tomo',
//																																																																																																																					cuisine: 'Sushi'
//																																																																																																																						,add:'a'},{
//																																																																																																																							name: 'Kanpai',
//																																																																																																																							cuisine: 'Sushi'
//																																																																																																																								,add:'a'},{
//																																																																																																																									name: 'Pizza My Heart',
//																																																																																																																									cuisine: 'Pizza'
//																																																																																																																										,add:'a'},{
//																																																																																																																											name: 'New York Pizza',
//																																																																																																																											cuisine: 'Pizza'
//																																																																																																																												,add:'a'},{
//																																																																																																																													name: 'California Pizza Kitchen',
//																																																																																																																													cuisine: 'Pizza'
//																																																																																																																														,add:'a'},{
//																																																																																																																															name: 'Round Table',
//																																																																																																																															cuisine: 'Pizza'
//																																																																																																																																,add:'a'},{
//																																																																																																																																	name: 'Loving Hut',
//																																																																																																																																	cuisine: 'Vegan'
//																																																																																																																																		,add:'a'},{
//																																																																																																																																			name: 'Garden Fresh',
//																																																																																																																																			cuisine: 'Vegan'
//																																																																																																																																				,add:'a'},{
//																																																																																																																																					name: 'Cafe Epi',
//																																																																																																																																					cuisine: 'French'
//																																																																																																																																						,add:'a'},{
//																																																																																																																																							name: 'Tai Pan',
//																																																																																																																																							cuisine: 'Chinese'
//																																																																																																																																								,add:'a'
//																																																																																																																																						}]
//			});
//			return restaurants;
		}



});
