/*
*
* @file EditViewPropertiesDialog.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: EditViewPropertiesDialog.java,v 1.7 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminServiceProvider;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.EnhancedComboBox;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>ViewImporter</code> TODO DOCUMENT ME
 * 
 * @version $Id: EditViewPropertiesDialog.java,v 1.7 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class EditViewPropertiesDialog extends Window {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	public static final String XOBJECT_TYPE = "viewimporterobject";

	public static final String BUTTON_OK = "apply";
	public static final String BUTTON_CANCEL = "cancel";

	private CheckBox makePublicView;
	private CheckBox makeEditableView;
	
	private EnhancedComboBox <XObjectWrapper> ownerCombo;
	private EnhancedComboBox <XObjectWrapper> connectionCombo;
	private EnhancedComboBox <XObjectWrapper> databaseCombo;
	private EnhancedComboBox <XObjectWrapper> cubeCombo;
	
	private ListStore<XObjectWrapper> ownerStore;
	private ListStore<XObjectWrapper> connectionStore;
	private ListStore<XObjectWrapper> databaseStore;
	private ListStore<XObjectWrapper> cubeStore;	
	
	private Button okButton;
	private Button cancelButton;
	private final XView xView;
	private final HashMap <String, String> connection2Account = new HashMap<String, String>();
	
	public EditViewPropertiesDialog(XView view) {
		setClosable(false);
		xView = view;
		setCloseAction(CloseAction.CLOSE);
		setHeading(messages.editProperties(ViewBrowserModel.modify(view.getName())));
		setPixelSize(470, 420);
		setModal(true);
		setLayout(new RowLayout());
		add(createForm());
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().getRoles(sessionId, view.getId(), new Callback <Boolean []>(){
			public void onSuccess(Boolean[] result) {			
				makePublicView.setValue(result[0]);
				makeEditableView.setValue(result[1]);
			}
		});		
	}

	public final void addButtonListener(String buttonId,
			Listener<BaseEvent> listener) {
		if (buttonId.equals(BUTTON_OK))
			okButton.addListener(Events.Select, listener);
		else if (buttonId.equals(BUTTON_CANCEL))
			cancelButton.addListener(Events.Select, listener);
	}

	private FormPanel createForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		// panel.setIconStyle("icon-filter");
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		// panel.setHeading("Select views to import");
		panel.setSize(456, -1);
		panel.add(createDatabaseSet());
		panel.add(createVisibilitySet());
		
		// finally the apply/cancel button:
		SelectionListener<ComponentEvent> listener = new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (ce.component instanceof Button) {
					Button pressedButton = (Button) ce.component;
					// we close dialog on button press:
					if (closeAction == CloseAction.CLOSE)
						close(pressedButton);
					else
						hide(pressedButton);
				}
			}
		};
		okButton = new Button(constants.apply());
		okButton.setItemId(BUTTON_OK);
		cancelButton = new Button(constants.cancel());
		cancelButton.setItemId(BUTTON_CANCEL);
		okButton.addSelectionListener(listener);
		cancelButton.addSelectionListener(listener);
		panel.addButton(okButton);
		panel.addButton(cancelButton);

		return panel;
	}

	private final LabelField createLabel(String text, int width) {
		LabelField l = new LabelField(text);
		l.setWidth(width);
		return l;
	}
	
	private final FieldSet createDatabaseSet() {
		FieldSet panel = new FieldSet();
		panel.setHeading(constants.olap());
		
		panel.setLayout(new TableLayout(2));
		
		TableData d1 = new TableData();
		d1.setColspan(2);

		panel.add(new LabelField(constants.editPropertiesDescription()), d1);
		
		connectionCombo = new EnhancedComboBox<XObjectWrapper>();
		connectionCombo.setWidth(260);
		connectionCombo.setEditable(false);
		connectionCombo.setHideLabel(true);
		connectionCombo.setDisplayField("name");
		connectionCombo.setEmptyText(constants.chooseConnection());
		connectionStore = new ListStore<XObjectWrapper>();
		connectionCombo.setStore(connectionStore);
		
		databaseCombo = new EnhancedComboBox<XObjectWrapper>();
		databaseCombo.setWidth(260);
		databaseCombo.setEditable(false);
		databaseCombo.setHideLabel(true);
		databaseCombo.setDisplayField("name");
		databaseCombo.setEmptyText(constants.chooseDatabase());
		databaseStore = new ListStore<XObjectWrapper>();
		databaseCombo.setStore(databaseStore);

		cubeCombo = new EnhancedComboBox<XObjectWrapper>();
		cubeCombo.setWidth(260);
		cubeCombo.setEditable(false);
		cubeCombo.setHideLabel(true);
		cubeCombo.setDisplayField("name");
		cubeCombo.setEmptyText(constants.chooseCube());
		cubeStore = new ListStore<XObjectWrapper>();
		cubeCombo.setStore(cubeStore);

		panel.add(createLabel(constants.connection() + ":", 130));
		panel.add(connectionCombo);
		panel.add(createLabel(constants.database() + ":", 130));
		panel.add(databaseCombo);
		panel.add(createLabel(constants.cube() + ":", 130));
		panel.add(cubeCombo);
		
		initOlapCombos();
		
		return panel;
	}
	
	private final void initOlapCombos() {
		connectionCombo.addSelectionChangedListener(new SelectionChangedListener<XObjectWrapper>() {
			public synchronized void selectionChanged(SelectionChangedEvent<XObjectWrapper> se) {
				databaseStore.removeAll();
				XObjectWrapper sel = se.getSelectedItem();
				if (sel != null) {
					fillDatabases((XConnection) sel.xObj);
				}
			}
		});
		
		databaseCombo.addSelectionChangedListener(new SelectionChangedListener<XObjectWrapper>() {
			public synchronized void selectionChanged(SelectionChangedEvent<XObjectWrapper> se) {
				cubeStore.removeAll();
				XObjectWrapper sel = se.getSelectedItem();
				if (sel != null) {
					fillCubes((XDatabase) sel.xObj);
				}
			}
		});
		
		fillConnections();
	}
	
	private final synchronized void fillConnections() {
		final XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();
		connectionStore.removeAll();
		WPaloAdminServiceProvider.getInstance().getConnection(sessionId, xView, new Callback <XConnection>(){
			public synchronized void onFailure(Throwable t) {
				super.onFailure(t);
//				databaseCombo.setEnabled(false);
//				cubeCombo.setEnabled(false);				
			}
			
			public synchronized void onSuccess(XConnection con) {
				final String id = con == null ? null : con.getId();
				WPaloAdminServiceProvider.getInstance().listAccounts(sessionId, user, new Callback<XAccount[]>() {
					public synchronized void onSuccess(XAccount[] accounts) {						
						if (accounts == null || accounts.length == 0) {
//							connectionCombo.setEnabled(false);
//							databaseCombo.setEnabled(false);
//							cubeCombo.setEnabled(false);
							return;
						}
						boolean selected = false;
						connectionCombo.setEnabled(true);
						databaseCombo.setEnabled(true);
						cubeCombo.setEnabled(true);		
						connection2Account.clear();
						for (XAccount acc: accounts) {
							if (acc.getConnection() != null) {
								XObjectWrapper wrap = new XObjectWrapper(acc.getConnection());
								connectionStore.add(wrap);
								if (acc.getConnection().getId().equals(id)) {
									connectionCombo.setValue(wrap);
									selected = true;
								}
								connection2Account.put(acc.getConnection().getId(), acc.getId());
							}
						}
						if (!selected) {
							if (connectionStore.getModels().size() > 0) {
								connectionCombo.setValue(connectionStore.getAt(0));
							} 							
						}						
//						if (connectionCombo.getValue() != null) {
//							fillDatabases((XConnection) connectionCombo.getValue().xObj);
//						}
					}
				});				
			}
		});
	}
	
	private final synchronized void fillDatabases(XConnection con) {
		final XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();

		databaseStore.removeAll();
		WPaloAdminServiceProvider.getInstance().getDatabases(sessionId, con, new Callback<XDatabase[]>(){
			protected boolean handled(Throwable cause) {
				if (!super.handled(cause)) {
					if (cause != null && cause.getCause() != null) {
						if (cause.getCause().getMessage() != null) {
							String msg = cause.getCause().getMessage();								
							MessageBox.info(constants.couldNotRetrieveDatabases(), msg, null);
							return true;
						}
					}
					return false;
				} else {
					return true;
				}
			}

			public void onFailure(Throwable t) {				
				super.onFailure(t);				
				databaseStore.removeAll();
//				databaseCombo.setEnabled(false);
//				cubeCombo.setEnabled(false);				
			}

			public synchronized void onSuccess(XDatabase[] databases) {				
				databaseStore.removeAll();
				if (databases == null || databases.length == 0) {
//					databaseCombo.setEnabled(false);
//					cubeCombo.setEnabled(false);
					return;
				}
				boolean selected = false;
				databaseCombo.setEnabled(true);
				cubeCombo.setEnabled(true);						
				for (XDatabase db: databases) {
					XObjectWrapper wrap = new XObjectWrapper(db);
					databaseStore.add(wrap);
					if (db.getId().equals(xView.getDatabaseId())) {
						databaseCombo.setValue(wrap);
						selected = true;
					}
				}
				if (!selected) {
					if (databaseStore.getModels().size() > 0) {
						databaseCombo.setValue(databaseStore.getAt(0));
					} 
				}				
//				if (databaseCombo.getValue() != null) {
//					fillCubes((XDatabase) databaseCombo.getValue().xObj);
//				}
			}
		});
	}
	
	private final synchronized void fillCubes(XDatabase db) {	
		if (connectionCombo.getValue() == null) {
			return;
		}
		final XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();
		cubeStore.removeAll();
		final XConnection con = (XConnection) connectionCombo.getValue().xObj;
		WPaloAdminServiceProvider.getInstance().getCubes(sessionId, con, db, new Callback<XCube[]>(){
			public void onFailure(Throwable t) {
				t.printStackTrace();
				super.onFailure(t);
//				cubeCombo.setEnabled(false);				
			}
			
			public synchronized void onSuccess(XCube[] cubes) {
				if (cubes == null || cubes.length == 0) {	
//					cubeCombo.setEnabled(false);
					return;
				}
				boolean selected = false;
				cubeCombo.setEnabled(true);						
				cubeStore.removeAll();
				for (XCube c: cubes) {
					XObjectWrapper wrap = new XObjectWrapper(c);
					cubeStore.add(wrap);
					if (c.getId().equals(xView.getCubeId())) {
						cubeCombo.setValue(wrap);
						cubeCombo.setEnabled(true);
						selected = true;
					}
				}
				if (!selected) {
					if (cubeStore.getModels().size() > 0) {
						cubeCombo.setValue(cubeStore.getAt(0));
						cubeCombo.setEnabled(true);
					} 
				}				
			}
		});
	}
	
	private final FieldSet createVisibilitySet() {
		FieldSet panel = new FieldSet();
		panel.setHeading(constants.visibility());

//		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setLayout(new TableLayout(2));

		ownerCombo = new EnhancedComboBox<XObjectWrapper>();
		ownerCombo.setWidth(260);
		ownerCombo.setEditable(false);
		ownerCombo.setHideLabel(true);
		ownerCombo.setDisplayField("name");
		ownerCombo.setEmptyText(constants.chooseOwner());
		ownerStore = new ListStore<XObjectWrapper>();
		ownerCombo.setStore(ownerStore);
						
//		HorizontalPanel ownerCont = new HorizontalPanel();

		panel.add(createLabel(constants.owner() + ":", 130));
		panel.add(ownerCombo);

				
//		panel.add(ownerCont);  
		
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloAdminServiceProvider.getInstance().getUsersForConnection(sessionId, xView.getId(), new AsyncCallback<XUser[]>() {			
			public void onSuccess(XUser[] users) {
				ownerStore.removeAll();
				boolean selected = false;
				for (XUser user: users) {
					XObjectWrapper wrap = new XObjectWrapper(user);
					ownerStore.add(wrap);					
					if (user.getId().equals(xView.getOwnerId())) {
						ownerCombo.setValue(wrap);
						selected = true;
					}
				}
				if (!selected) {
					if (ownerStore.getModels().size() > 0) {
						ownerCombo.setValue(ownerStore.getAt(0));
					} 
//					else {
//						ownerCombo.setEnabled(false);
//					}
				}
				
			}
			
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});

		panel.add(ownerCombo);
		
		// Checkboxes for public/editable:
		LayoutContainer rights = new LayoutContainer();		
		RowLayout rLayout = new RowLayout();
		rights.setLayout(rLayout);
		
		makePublicView = new CheckBox();
		makePublicView.setBoxLabel(constants.visibleForAllViewers());
		
		makeEditableView = new CheckBox();
		makeEditableView.setBoxLabel(constants.visibleAndEditableForAllEditors());
				
		rights.add(makePublicView);
		rights.add(makeEditableView);
		
		makePublicView.setValue(false);
		makeEditableView.setValue(false);
		
		TableData d = new TableData();
		d.setColspan(2);
		panel.add(rights, d);
		
		LabelField label2 = new LabelField();
		label2.setHeight("20px");
		TableData d3 = new TableData();
		d3.setColspan(2);
		panel.add(label2, d3);

		TableData d1 = new TableData();
		d1.setColspan(2);

		panel.add(new LabelField(constants.visibilityHint()), d1);
		
		return panel;
	}
	
	public boolean isPublic() {
		return makePublicView.getValue();
	}
	
	public boolean isEditable() {
		return makeEditableView.getValue();
	}
	
	public String getOwner() {
		if (ownerCombo.isEnabled() && ownerCombo.getValue() != null) {
			return ((XUser) ownerCombo.getValue().xObj).getId();
		}
		return xView.getOwnerId();
	}
	
	public String getAccountId() {
		if (connectionCombo.isEnabled() && databaseCombo.isEnabled() && cubeCombo.isEnabled() && connectionCombo.getValue() != null && connection2Account.get(connectionCombo.getValue().xObj.getId()) != null) {
			return connection2Account.get(connectionCombo.getValue().xObj.getId());
		}
		return xView.getAccountId();
	}
	
	public String getDatabaseId() {
		if (connectionCombo.isEnabled() && databaseCombo.isEnabled() && cubeCombo.isEnabled() && databaseCombo.getValue() != null) {
			return databaseCombo.getValue().xObj.getId();
		}
		return xView.getDatabaseId();
	}
	
	public String getCubeId() {
		if (connectionCombo.isEnabled() && databaseCombo.isEnabled() && cubeCombo.isEnabled() && cubeCombo.getValue() != null) {
			return cubeCombo.getValue().xObj.getId();
		}
		return xView.getCubeId();		
	}
	
	class XObjectWrapper extends BaseModel {		
		private static final long serialVersionUID = 5073581456853340071L;

		private final XObject xObj;
		public XObjectWrapper(XObject xObj) {
			this.xObj = xObj;
			set("name", xObj.getName());
		}
		
		public final XObject getXObject() {
			return xObj;
		}		
	}	
}
