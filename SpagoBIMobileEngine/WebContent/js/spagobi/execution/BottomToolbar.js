app.views.BottomToolbar = Ext.extend(Ext.Toolbar,
		{
			xtype : 'toolbar',
			dock : 'bottom',
			defaults : {
				ui : 'plain',
				iconMask : true
			},
			scroll : 'horizontal',
			layout : {
				pack : 'center'
			},
			breadCrumbs : new Array(),

			initComponent : function() {

				console.log('init bottom toolbar view');

				var par = this.parameters;

				this.docParams = new Ext.Button( {
					title : 'Parameters',
					iconCls : 'compose',
					text : 'Parameters',
					scope : this,
					listeners : {
						scope : this,
						tap : function() {

							Ext.dispatch( {
								controller : app.controllers.mobileController,
								action : 'backToParametersView',
								params : this.parameters
							});
						}
					}
				});
				this.docHome = {
					title : 'Home',
					iconCls : 'reply',
					text : 'Home',
					handler : function() {
						Ext.dispatch( {
							controller : app.controllers.mobileController,
							action : 'backToBrowser'
						});

					}
				};

				this.items = [ this.docHome, this.docParams ];

				app.views.BottomToolbar.superclass.initComponent.apply(this,
						arguments);
				this.configureItems(this.parameters);

			}

			,
			configureItems : function(par) {

				if (par == undefined || par == null) {
					this.docParams.hide();
				}

			},
			setBreadCrumb : function(objectLabel, objectId, typeCode,
					parameters) {

				var current = this.breadCrumbs.indexOf(objectLabel);
				if (current == -1) {

					this.breadCrumbs.push(objectLabel);
					var pos = this.breadCrumbs.length;

					this.insert(pos, {
						title : objectLabel,
						iconCls : 'arrow_left',
						text : objectLabel,
						disabled : true,
						handler : function() {
							Ext.dispatch( {
								controller : app.controllers.mobileController,
								action : 'getRoles',
								label : objectLabel,
								id : objectId,
								typeCode : typeCode,
								parameters : parameters,
								isFromCross : true
							});

						}
					});
					// disables current link
					// enables previous
					var idxPrev = pos-1;
					if (idxPrev != 0) {
						var prev = this.items.items[idxPrev];
						prev.setDisabled(false);
					}
					this.doLayout();

				} else {
					//on the way back
					var curr = this.items.items[current + 1];
					if (curr == undefined) {
						this.breadCrumbs = new Array();
						return;
					}
					for (i = 1; i < this.breadCrumbs.length + 1; i++) {
						var other = this.items.items[i];
						other.setDisabled(false);
					}
					curr.setDisabled(true);
					this.doLayout();
				}

			},
			clearNavigation : function() {
				this.breadCrumbs = new Array();
				this.removeAll();
				this.add(this.docHome);
				/*
				 * this.toolbarForCross.removeAll(); this.add(this.docHome);
				 */
			}
		});