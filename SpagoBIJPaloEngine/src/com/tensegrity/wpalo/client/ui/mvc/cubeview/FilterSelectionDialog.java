/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>FilterSelectionDialog</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: FilterSelectionDialog.java,v 1.43 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
class FilterSelectionDialog extends Window {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();

	public static final String APPLY = "apply";
	public static final String CANCEL = "cancel";
		
	private AliasComboBox aliases;
	private EnhancedSimpleComboBox <AliasFormat> aliasFormats;
	private SubsetComboBox subsets;
	private LocalFilterFieldSet localFilter;
	
	private /*final*/ XAxisHierarchy hierarchy;
	private /*final*/ XViewModel xViewModel;
	private boolean needsReset = false;
	private XElement oldSelectedElement;
	
	FilterSelectionDialog(XAxisHierarchy hierarchy, XViewModel xViewModel) {
		try {
			this.hierarchy = hierarchy;
			this.oldSelectedElement = hierarchy.getSelectedElement();
			this.xViewModel = xViewModel;
			init();
			add(createForm());
			subsets.setInput(hierarchy);
			aliases.setInput(hierarchy);
			aliasFormats.setEnabled(aliases.getSelection() != null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	private final void init() {
		setClosable(false);
		setCloseAction(CloseAction.CLOSE);
		setPixelSize(660, 575);
		setIconStyle("icon-filter");
		setHeading(constants.specifyFilters());
		
		setResizable(false);
		setShim(false);
		setModal(true);
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
	}

	public void show(final Widget widget) {
		// defaultAlign = "tl-bl?";
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.openingFilterDialog());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void none) {
				show(widget.getElement(), "tl-bl?");
				((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
			}
		});
	}

	public void show(Element elem, String pos) {
		disableComboBoxEvents(true);
		super.show();
		center();
//		el().makePositionable(true);
		onShow();
//		el().alignTo(elem, pos, new int[] { 0, 0 });
		focus();
		disableComboBoxEvents(false);
		updateHierarchy(null, true, false, true);
	}

	public final XSubset getSelectedSubset() {
		XSubset subset = subsets.getSelection();
		return subset;
	}
	
	public final XAlias getSelectedAlias() {
		XAlias alias = aliases.getSelection();
		return alias;
	}
	
	public final XElementNode[] getVisibleElements() {
		if (localFilter.isExpanded()) {
			Object [] all = localFilter.getVisibleElements();
			hierarchy.addProperty("filterPaths", (String) all[1]);
			return (XElementNode []) all[0];
		}
		hierarchy.removeProperty("filterPaths");
		return null;
	}
	
	public final XElement getSelectedElement() {
		XElement element = hierarchy.getSelectedElement();
		return element;
	}
	
	private FormPanel createForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		panel.setWidth(646);	
						
		addAliasCombo(panel);
		addAliasFormatCombo(panel);
		addSubsetCombo(panel);
		addLocalFilter(panel);
		addApplyCancelButtons();
		return panel;
	}
	
	private final void addSubsetCombo(FormPanel form) {
		subsets = new SubsetComboBox();
		ComboBox<XObjectModel> subsetCombo = subsets.getComboBox();
		subsetCombo.addSelectionChangedListener(
			new SelectionChangedListener<XObjectModel>() {
					public void selectionChanged(
							SelectionChangedEvent<XObjectModel> se) {
						applySubsetSelection(subsets.getSelection());
					}
			});
		form.add(subsetCombo);
	}
	
	private final void addAliasCombo(FormPanel form) {
		aliases = new AliasComboBox();
		ComboBox<XObjectModel> aliasCombo = aliases.getComboBox();
		aliasCombo.addSelectionChangedListener(
			new SelectionChangedListener<XObjectModel>() {
					public void selectionChanged(
							SelectionChangedEvent<XObjectModel> se) {
						applyAliasSelection(aliases.getSelection());
					}
			});
		form.add(aliasCombo);
	}
	
	private final void addAliasFormatCombo(FormPanel form) {
		aliasFormats = new EnhancedSimpleComboBox<AliasFormat>();		
		aliasFormats.add(new AliasFormat("aliasFormat", constants.aliasFormat()));
		aliasFormats.add(new AliasFormat("elementName", constants.elementName()));
		aliasFormats.add(new AliasFormat("elementNameDashAlias", constants.elementNameDashAlias()));
		aliasFormats.add(new AliasFormat("aliasDashElementName", constants.aliasDashElementName()));		
		aliasFormats.add(new AliasFormat("elementNameParenAlias", constants.elementNameParenAlias()));
		aliasFormats.add(new AliasFormat("aliasParenElementName", constants.aliasParenElementName()));		
		aliasFormats.add(new AliasFormat("elementNameAlias", constants.elementNameCommaAlias()));
		aliasFormats.add(new AliasFormat("aliasElementName", constants.aliasCommaElementName()));
		
		aliasFormats.setFieldLabel(constants.aliasFormatLabel());
		String prop = hierarchy.getProperty("aliasFormat");
		if (prop == null) {
			aliasFormats.setValue(aliasFormats.findModel(new AliasFormat("aliasFormat", constants.aliasFormat())));
		} else {
			aliasFormats.setValue(aliasFormats.findModel(new AliasFormat(prop, "ignored")));
		}
		aliasFormats.setEnabled(aliases.getSelection() != null);
		aliasFormats.addSelectionChangedListener(
				new SelectionChangedListener() {
						public void selectionChanged(
								SelectionChangedEvent se) {							
							if (localFilter.getAxisHierarchy() != null) {
								applyAliasSelection(aliases.getSelection());
							}
						}
				});
		
		form.add(aliasFormats);
	}
	
	private final void addLocalFilter(FormPanel form) {
		localFilter = new LocalFilterFieldSet();
		localFilter.setHeading(constants.useLocalFilter());
		localFilter.setCheckboxToggle(true);
		
		if (UserAgent.getInstance().isIE) {
			localFilter.setHeight(390);
			localFilter.setWidth(614);
		} else {
			localFilter.setHeight(380);
			localFilter.setWidth(594);
		}
		localFilter.setExpanded(hierarchy.getVisibleElements() != null);
		
		FormLayout layout = new FormLayout();
		form.setLayout(layout);
		FormData layoutData = new FormData();
		layoutData.setMargins(new Margins(20,0,0,0));
		form.add(localFilter, layoutData);
	}
	private final void addApplyCancelButtons() {
		SelectionListener<ComponentEvent> listener = new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (ce.component instanceof Button) {
					final Button pressedButton = (Button) ce.component;
					if(pressedButton.getItemId().equals(CANCEL)) {
						resetAndCloseFilterDialog(pressedButton);
					} else {
						updateAndCloseFilterDialog(pressedButton);
					}
				}
			}
		};
		setButtonAlign(HorizontalAlignment.RIGHT);
		final Button apply = new Button(constants.apply());
		apply.setItemId(APPLY);
		final Button cancel = new Button(constants.cancel());
		cancel.setItemId(CANCEL);
		apply.addSelectionListener(listener);
		cancel.addSelectionListener(listener);
		addButton(apply);
		addButton(cancel);
	}
	
	public XElement getOldSelectedElement() {
		return oldSelectedElement;
	}
	
	private final void updateAndCloseFilterDialog(final Button pressedButton) {
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.updatingFilter());
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				disableComboBoxEvents(true);
				// PR 699: we have to reset hierarchy and set selection again after
				// update this is required to correctly track changes in view!
				// (see WPaloCubeViewService:updateView/reloadView)
				final XAlias activeAlias = aliases.getSelection();
				final XSubset activeSubset = subsets.getSelection();
				aliases.reset();
				subsets.reset();
				hierarchy.setActiveSubset(subsets.getSelection());
				hierarchy.setActiveAlias(aliases.getSelection());
				hierarchy.addProperty(LocalFilterFieldSet.FILTER_ON_RIGHT, ""
						+ localFilter.isFilterOnRight());
				hierarchy.addProperty(LocalFilterFieldSet.FILTER_ON_LEFT, ""
						+ (localFilter.isLeft()));
				updateHierarchy(new UpdateListener() {
					public void updateFinished() {
						subsets.selectSubset(activeSubset);
						aliases.selectAlias(activeAlias);
						if (localFilter.isEnabled() && localFilter.getAxisHierarchy() != null) {
							Object[] all = localFilter.getVisibleElements();
							hierarchy.addProperty("filterPaths", (String) all[1]);
							hierarchy.setVisibleElements((XElementNode[]) all[0]);
						} else {
							hierarchy.removeProperty("filterPaths");
						}
						if (aliasFormats.isEnabled()) {
							hierarchy.addProperty("aliasFormat", aliasFormats.getValue().getValue().id);
						} else {
							hierarchy.removeProperty("aliasFormat");
						}
						closeDialog(pressedButton);
						// TODO WaitCursorCheck -- may I keep it here?
//						((Workbench) Registry.get(Workbench.ID))
//							.hideWaitCursor();						
					}
				}, !localFilter.isEnabled(), false, false);
			}
		});
	}
	private final void resetAndCloseFilterDialog(final Button pressedButton) {
		final String sessionId = ((Workbench) Registry.get(Workbench.ID))
				.getUser().getSessionId();
		((Workbench) Registry.get(Workbench.ID))
				.showWaitCursor(constants.resetFilter());
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0,
				new Callback<Void>() {
					public void onSuccess(Void arg0) {
						disableComboBoxEvents(true);
						subsets.reset();
						hierarchy.setActiveSubset(subsets.getSelection());
						aliases.reset();
						hierarchy.setActiveAlias(aliases.getSelection());
						if (needsReset) {
							updateHierarchy(new UpdateListener() {
								public void updateFinished() {
									localFilter.reset();
									closeDialog(pressedButton);
								}
							}, true, false, false);
						} else {
							localFilter.reset();
							closeDialog(pressedButton);
						}
						((Workbench) Registry.get(Workbench.ID))
							.hideWaitCursor();
					}
				});
	}
	private final void closeDialog(final Button pressedButton) {
		// we close dialog on button press:
		if (closeAction == CloseAction.CLOSE)
			close(pressedButton);
		else
			hide(pressedButton);						
	}
	
	private final void applySubsetSelection(final XSubset subset) {
		hierarchy.setActiveSubset(subset);
		needsReset = true;
		if (localFilter.isExpanded()) {
			hierarchy.setVisibleElements((XElementNode []) localFilter.getVisibleElements()[0]);
		}
		updateHierarchy(null, true, false, true);
	}
	private final void applyAliasSelection(XAlias alias) {
		aliasFormats.setEnabled(alias != null);
		hierarchy.setActiveAlias(alias);
		if (alias != null) {
			hierarchy.addProperty("aliasFormat", aliasFormats.getValue().getValue().id);
		} else {
			hierarchy.removeProperty("aliasFormat");
		}
		needsReset = true;	
		if (localFilter.isExpanded()) {
			hierarchy.setVisibleElements((XElementNode []) localFilter.getVisibleElements()[0]);
		}
		updateHierarchy(null, true, true, true);
	}
	
	private final void updateHierarchy(final UpdateListener listener, final boolean disableLocal, final boolean updateAlias, final boolean setInput) {
		final XElementNode[] visibleElements = hierarchy.getVisibleElements();
		final boolean checked = localFilter.isExpanded();
		if (disableLocal) {
			hierarchy.setVisibleElements(null); // disable visible elements!!
			hierarchy.setOldVisibleElements(null);
		}
		localFilter.setEnabled(visibleElements != null);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().updateAxisHierarchy(sessionId, 
				hierarchy, new Callback<XElement>() {
					public void onFailure(Throwable t) {
						hideWaitCursor();
						if(handled(t) || handled(t.getCause())) {
							FilterSelectionDialog.this.hide();
						}							
						if(listener != null)
							listener.updateFinished();
					}
					public void onSuccess(XElement newSelectedElement) {
						if (updateAlias || (hierarchy.getSelectedElement() == null && newSelectedElement != null)) {
							hierarchy.setSelectedElement(newSelectedElement);
						} 
						localFilter.setInput(hierarchy, visibleElements, setInput, xViewModel);
						localFilter.setExpanded(checked);
						if(listener != null)
							listener.updateFinished();
					}
				});
	}
	
	private final void disableComboBoxEvents(boolean doIt) {
		subsets.getComboBox().disableEvents(doIt);
		aliases.getComboBox().disableEvents(doIt);
	}
}	

interface UpdateListener {
	public void updateFinished();
//	public void updateSuccess();
//	public void updateFailed();
}
