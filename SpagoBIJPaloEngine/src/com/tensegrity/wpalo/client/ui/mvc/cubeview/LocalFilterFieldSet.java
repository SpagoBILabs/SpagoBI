/*
*
* @file LocalFilterFieldSet.java
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
* @version $Id: LocalFilterFieldSet.java,v 1.31 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTree;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

class LocalFilterFieldSet extends FieldSet implements Listener<ComponentEvent>, SelectionCountListener {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	static final String FILTER_ON_RIGHT = "rightFilter";
	static final String FILTER_ON_LEFT = "leftFilter";
	
	private static final String BTN_ADD = "icons-move_right_on";
	private static final String BTN_DELETE = "icons-move_left";
	private static final String BTN_MOVE_UP = "icons-move_up_on";
	private static final String BTN_MOVE_DOWN = "icons-move_down_on";
	private static final String BTN_CONSTRAINT = "icons-sum"; 

	private static final String BTN_EXPAND = "icons-expand";
	private static final String BTN_COLLAPSE = "icons-collapse";
	private static final String BTN_EXPAND_ALL = "icons-expand-all";
	private static final String BTN_COLLAPSE_ALL = "icons-collapse-all";
	
	private static final String BTN_ADD_NODE = "icons-add_on";
	private static final String BTN_REMOVE_NODE = "icons-remove_on"; 
	private static final String BTN_EMPTY_LIST = "emptyList";
	
	private static final String BTN_ONE = "1";
	private static final String BTN_TWO = "2";
	private static final String BTN_THREE = "3";
	private static final String BTN_FOUR = "4";
	private static final String BTN_FIVE = "5";
	private static final String BTN_LEAVES = "B";
	private static final String BTN_ALL = "All";
	private static final String BTN_INVERT = "Invert";
	private static final String BTN_SELECT_BRANCH = "Branch";
	private static final String BTN_SEARCH_SELECT = "Search";
	
	private HierarchyTree sourceHierarchyTree;
	private DnDHierarchyTree targetHierarchyTree;
	private LayoutContainer filterPanel;
	private XAxisHierarchy xAxisHierarchy;
	private Button addFilterButton;
	private CheckBox showOnRight;
	private boolean inputSet = false;
	private TextField <String> regExField;
	
	private boolean propagate;
	private XElementNode [] initialSelectedElements;
	private XViewModel xViewModel;
	private boolean isShiftPressed;
	private boolean isCtrlPressed;
	
	public LocalFilterFieldSet() {		
		super();
		init();
	}
	
	private final void init() {
		setLayout(new RowLayout());
		filterPanel = createLocalFilter();
		add(filterPanel);
//		final LayoutContainer panel = new LayoutContainer();		
//		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
//		panel.setSize(500, 15); //have to hard code to make it visible in FF...
//		RowData layoutData = new RowData();
//		layoutData.setMargins(new Margins(0, 5, 0, 0));
//		Label l = new Label(constants.shiftCtrlHint());
//		l.setSize("500px", "15px");
//		panel.add(l, layoutData);
//		add(panel);
//		filterPanel.addListener(Events.Enable, new Listener<BaseEvent>() {
//			public void handleEvent(BaseEvent be) {
//				panel.setEnabled(true);
//			}
//		});
//		filterPanel.addListener(Events.Disable, new Listener<BaseEvent>() {
//			public void handleEvent(BaseEvent be) {
//				panel.setEnabled(false);
//			}
//		});
		sourceHierarchyTree.getTree().addSelectionCountListener(this);
		targetHierarchyTree.addSelectionCountListener(this);
	}
	
	public void selectionCountChanged(int n) {
		if (!isExpanded()) {
			return;
		}
		int number = 0;
		if (sourceHierarchyTree != null && targetHierarchyTree != null) {
			if (targetHierarchyTree.isEmpty()) {
				number = sourceHierarchyTree.getNumberOfSelectedElements();	
			} else {
				number = targetHierarchyTree.getNumberOfElements();		
			}
		}
		setHeading(messages.useLocalFilter(number));
	}
	
	public void collapse() {
	}
	
	public void setExpanded(boolean expand) {
		super.setExpanded(expand);
		expand();
		filterPanel.setEnabled(expand);
		if (expand) {
			int number = 0;
			if (xAxisHierarchy != null) {
				if (sourceHierarchyTree != null && targetHierarchyTree != null) {
					if (targetHierarchyTree.isEmpty()) {
						number = sourceHierarchyTree.getNumberOfSelectedElements();	
					} else {
						number = targetHierarchyTree.getNumberOfElements();		
					}
				}
			}
			setHeading(messages.useLocalFilter(number));
		} else {
			setHeading(constants.useLocalFilter());
		}
		if (expand && !inputSet && xAxisHierarchy != null) {
			setInputInternal();
		}
	}
	public boolean isExpanded() {
		boolean result = filterPanel.isEnabled();
		return result;
	}
	
	public void setEnabled(boolean doIt) {
		setExpanded(doIt);
	}
		
	/**
	 * Returns the root nodes of the selected elements tree
	 * @return
	 */
	public final Object [] getVisibleElements() {
		final List<XElementNode> roots = new ArrayList<XElementNode>();				
		StringBuffer paths = new StringBuffer();

		//if right side is empty, we take selection of left side:
		if(targetHierarchyTree.isEmpty()) {
//			final Map <FastMSTreeItem, XElementNode> parents = new HashMap<FastMSTreeItem, XElementNode>();
			final LinkedHashSet <FastMSTreeItem> currentSelection = sourceHierarchyTree.getSelection(); 
			for (FastMSTreeItem it: currentSelection) {
				paths.append(it.getModel().getPath());
				paths.append(",");
			}
			sourceHierarchyTree.traverse(new FastMSTreeItemVisitor() {
				public boolean visit(FastMSTreeItem item, FastMSTreeItem parent) {
					XElementNode elNode = getElementNodeCopyFrom(item);
//					elNode.removeChildren();
//					parents.put(item, elNode);
					item.setElementNode(elNode);
					XElementNode xParent = getParent(parent); //, parents); //parents.get(parent);
					if(xParent == null)
						roots.add(elNode);
					else {
						xParent.forceAddChild(elNode);
						elNode.setParent(xParent);
					}
					return true;
				}
			});
			xAxisHierarchy.addProperty("filterPaths", paths.toString());
		} else {
			final Map<FastMSTreeItem, XElementNode> parents = new HashMap<FastMSTreeItem, XElementNode>();
			final List <String> filterPaths = new ArrayList <String>();
			targetHierarchyTree.traverse(new FastMSTreeItemVisitor(){
				public boolean visit(FastMSTreeItem item, FastMSTreeItem parent) {
					XObjectModel node = item.getXObjectModel();
					String path = node.get("filterPath");
					if (path != null) {
						filterPaths.add(path);
					}
					return item.getChildCount() > 0;
				}
			});
			for (String f: filterPaths) {
				paths.append(f);
				paths.append(",");
			}
			targetHierarchyTree.traverse(new FastMSTreeItemVisitor() {
				public boolean visit(FastMSTreeItem item, FastMSTreeItem parent) {
					XElementNode elNode = getElementNodeCopyFrom(item);
					elNode.removeChildren();
					item.setElementNode(elNode);
					XElementNode xParent = getParent(parent); //parents.get(parent);
					if(xParent == null)
						roots.add(elNode);
					else {
						xParent.forceAddChild(elNode);
						elNode.setParent(xParent);
					}
					return true;
				}
			});			
			xAxisHierarchy.addProperty("filterPaths", paths.toString());
		}
		return new Object [] {roots.toArray(new XElementNode[0]), paths.toString()};
	}
	
	private final XElementNode getElementNodeCopyFrom(TreeItem item) {
		XObject xObject; 
		ModelData model = item.getModel();
		if(model instanceof TreeNode)
			xObject = ((TreeNode) model).getXObject();
		else
			xObject = ((XObjectModel) model).getXObject();
		XElementNode oldXElementNode = (XElementNode) xObject;
		// We need to return a copy of the element node in case
		// we have multiple consolidations.
		XElementNode newXElementNode = new XElementNode(oldXElementNode.getElement(), oldXElementNode.getAxisHierarchyId(), oldXElementNode.getViewId());
		return newXElementNode;
	}
	public final XAxisHierarchy getAxisHierarchy() {
		return xAxisHierarchy;
	}
	
	private final XElementNode getElementNodeCopyFrom(FastMSTreeItem item) {
		XObject xObject; 
		ModelData model = item.getModel();
		if (model != null) {
			xObject = ((TreeNode) model).getXObject();
		} else {
			xObject = ((XObjectModel) item.getXObjectModel()).getXObject();
		}
		XElementNode oldXElementNode = (XElementNode) xObject;
		// We need to return a copy of the element node in case
		// we have multiple consolidations.
		XElementNode newXElementNode = new XElementNode(oldXElementNode.getElement(), oldXElementNode.getAxisHierarchyId(), oldXElementNode.getViewId());
		return newXElementNode;
	}

	private final XElementNode getParent(TreeItem item, Map<TreeItem, XElementNode> parents) {
		while(!parents.containsKey(item)) {
			if(item == null) break;
			item = item.getParentItem();
		}
		return parents.get(item);
	}
	
	private final XElementNode getParent(FastMSTreeItem item) {
		if (item == null) {
			return null;
		}
		while(item.getElementNode() == null) {
			item = item.getParentItem();
			if(item == null) return null;
		}
		return item.getElementNode();		
	}

	public boolean isLeft() {
		return targetHierarchyTree.isEmpty() && sourceHierarchyTree.getTree().getChildCount() > 0;
	}
	
	private final void setInputInternal() {
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.initializingLocalFilter());

		final String ior = xAxisHierarchy.getProperty(FILTER_ON_RIGHT);
		final String paths = xAxisHierarchy.getProperty("filterPaths");
//		System.err.println("Paths == " + paths);
		final boolean initOnRight = ior != null && Boolean.parseBoolean(ior);
		if (showOnRight != null) {
			showOnRight.setValue(ior != null && Boolean.parseBoolean(ior));
		}	
		final String iol = xAxisHierarchy.getProperty(FILTER_ON_LEFT);
		boolean initOnLeft = iol != null && Boolean.parseBoolean(iol);
//		targetHierarchyTree.applyAlias(xAxisHierarchy);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		boolean showLeft = (targetHierarchyTree.isEmpty() && sourceHierarchyTree.getTree().getChildCount() > 0) || initOnLeft;
		inputSet = true;
//		final XAxis oldAxis = xAxisHierarchy.getAxis();
//		xAxisHierarchy.setAxis(null);
		WPaloCubeViewServiceProvider.getInstance().checkLocalFilter(sessionId, xAxisHierarchy.getId(), xAxisHierarchy.getViewId(), xAxisHierarchy.getAxisId(), xAxisHierarchy.getProperty("filterPaths"),
				initialSelectedElements, showLeft, new AsyncCallback<Boolean>() {
					public void onSuccess(Boolean result) {		
						if (propagate) {
							final boolean doRight = initOnRight || !result;
							sourceHierarchyTree.addLoadListener(new LoadListener() {			
								public void loaderLoad(LoadEvent le) {
									((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.initializingLocalFilter());
									sourceHierarchyTree.apply(initialSelectedElements,
											targetHierarchyTree, doRight, paths, xAxisHierarchy);
									sourceHierarchyTree.removeLoadListener(this);
									
								}
							});
							sourceHierarchyTree.setInput(xAxisHierarchy, xViewModel);
						}
					}
					
					public void onFailure(Throwable arg0) {
						if (propagate) {
							sourceHierarchyTree.addLoadListener(new LoadListener() {			
								public void loaderLoad(LoadEvent le) {
									((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.initializingLocalFilter());
									sourceHierarchyTree.apply(initialSelectedElements,
										targetHierarchyTree, /*initOnRight*/ true, paths, xAxisHierarchy);
									sourceHierarchyTree.removeLoadListener(this);
								}
							});
							sourceHierarchyTree.setInput(xAxisHierarchy, xViewModel);
						}
					}
				});		
	}
	
	public final void setInput(final XAxisHierarchy hierarchy,
			final XElementNode[] initialSelectedElements, final boolean propagate, final XViewModel xViewModel) {

		xAxisHierarchy = hierarchy;
		this.initialSelectedElements = initialSelectedElements;
		this.propagate = propagate;
		this.xViewModel = xViewModel;
		
		if (isExpanded()) {
			setInputInternal();
		} else {
			inputSet = false;
		}
//		if (initialSelectedElements != null && initialSelectedElements.length > 0) {
//			xAxisHierarchy.setVisibleElements(initialSelectedElements);
//		}
	}
	
//	final void activate(boolean doIt) {
//		setExpanded(doIt);
//	}
	
	final void reset() {
		targetHierarchyTree.reset();
//		if(hierarchyTree != null)
//			hierarchyTree.reset();
	}
//	private final boolean isActive() {
//		return filterPanel.isEnabled();
//	}

	private final LayoutContainer createLocalFilter() {
		LayoutContainer panel = new LayoutContainer();
		
		panel.setSize(570, 365);
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		//source tree:
		panel.add(createSourcePanel());
		
		//buttons:
		RowData btnPanelData = new RowData();
		btnPanelData.setMargins(new Margins(0, 0, 0, 5));
		panel.add(createButtonsPanel(), btnPanelData);
		
		//target tree:
		RowData data = new RowData();
		data.setMargins(new Margins(0, 0, 0, 10));
		LayoutContainer targetTree = createTargetPanel();
		panel.add(targetTree, data);
		
		return panel;
	}
	private final LayoutContainer createSourcePanel() {
		//tree panel:
		LayoutContainer treePanel = createTreePanel(300);		
		sourceHierarchyTree = new FullHierarchyTree(true);
		FastMSTree hTree = sourceHierarchyTree.getTree();
		treePanel.add(hTree);

		//buttons:
		LayoutContainer headerButtons = createHeaderButtons();
		LayoutContainer extendedButtonRow = createExtendedButtons();
		LayoutContainer levelButtonRow = createSelectionButtons();		
		LayoutContainer regexPanel = createRegexPanel();
		
		//all together
		LayoutContainer result = arrangeCaptionTreeAndButtons(headerButtons,
				constants.selectElementsIncludingOriginalHierarchy(), treePanel, extendedButtonRow, levelButtonRow, regexPanel);
		
		return result;
	}
	
	private final LayoutContainer createHeaderButtons() {
		LayoutContainer panel = new LayoutContainer();		
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(300, 22); //have to hard code to make it visible in FF...
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 5, 0, 0));
		addIconButton(BTN_EXPAND, panel, layoutData, constants.expandLevel());
		RowData spaceData = new RowData();
		spaceData.setMargins(new Margins(0, 2, 0, 0));
		panel.add(new LabelField(), spaceData);
		addIconButton(BTN_COLLAPSE, panel, layoutData, constants.collapseLevel());
		panel.add(new LabelField(), spaceData);
		panel.add(new LabelField(), spaceData);
		panel.add(new LabelField(), spaceData);
		addIconButton(BTN_EXPAND_ALL, panel, layoutData, constants.expandAll());
		panel.add(new LabelField(), spaceData);
		addIconButton(BTN_COLLAPSE_ALL, panel, layoutData, constants.collapseAll());		
		return panel;
	}
	
	private final LayoutContainer createSelectionButtons() {
		LayoutContainer panel = new LayoutContainer();		
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(300, 22); //have to hard code to make it visible in FF...
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 5, 0, 0));
		addButton(BTN_ONE, BTN_ONE, panel, layoutData, false, constants.showSelectLevel1());
		addButton(BTN_TWO, BTN_TWO, panel, layoutData, false, constants.showSelectLevel2());
		addButton(BTN_THREE, BTN_THREE, panel, layoutData, false, constants.showSelectLevel3());
		addButton(BTN_FOUR, BTN_FOUR, panel, layoutData, false, constants.showSelectLevel4());
		addButton(BTN_FIVE, BTN_FIVE, panel, layoutData, false, constants.showSelectLevel5());
		RowData spaceData = new RowData();
		spaceData.setMargins(new Margins(0, 2, 0, 0));
		panel.add(new LabelField(), spaceData);
		addButton(BTN_LEAVES, constants.shortHandForBaseElements(), panel, layoutData, false, constants.showSelectBase());
		RowData spaceData2 = new RowData();
		spaceData2.setMargins(new Margins(0, 5, 0, 0));
		panel.add(new LabelField(), spaceData2);
		addButton(BTN_ALL, constants.shortHandForAllElements(), panel, layoutData, false, constants.showSelectAll());
		return panel;
	}

	private final LayoutContainer createExtendedButtons() {
		LayoutContainer panel = new LayoutContainer();		
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(300, 22); //have to hard code to make it visible in FF...
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 5, 0, 0));
//		addButton(BTN_ALL, constants.shortHandForAllElements(), panel, layoutData, false, constants.showSelectAll());
//		RowData spaceData = new RowData();
//		spaceData.setMargins(new Margins(0, 2, 0, 0));
//		panel.add(new LabelField(), spaceData);
		addButton(BTN_SELECT_BRANCH, constants.shortHandForSelectBranch(), panel, layoutData, false, constants.showSelectBranch());
		RowData spaceData2 = new RowData();
		spaceData2.setMargins(new Margins(0, 2, 0, 0));
		panel.add(new LabelField(), spaceData2);
		addButton(BTN_INVERT, constants.shortHandForInvertSelection(), panel, layoutData, false, constants.invertSelection());
		return panel;
	}
	
	private final LayoutContainer createRegexPanel() {
		LayoutContainer panel = new LayoutContainer();		
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(300, 22); //have to hard code to make it visible in FF...
		RowData layoutData = new RowData();
		layoutData.setHeight(22);
		
		regExField = new TextField <String>();
		regExField.setEmptyText(constants.regEx());
		regExField.setToolTip(constants.regularExpressionHint());
		regExField.setSize(-1, 22);
		panel.add(regExField, layoutData);
		
		RowData spaceData = new RowData();
		spaceData.setMargins(new Margins(0, 2, 0, 0));
		panel.add(new LabelField(), spaceData);
		RowData lData = new RowData();
		lData.setMargins(new Margins(0, 5, 0, 0));
		addButton(BTN_SEARCH_SELECT, constants.shortHandForSearchSelect(), panel, lData, false, constants.showSearchSelect());
		return panel;		
	}
	
	private final LayoutContainer createTargetPanel() {
		//tree panel:
		LayoutContainer treePanel = createTreePanel(230);
		targetHierarchyTree = new DnDHierarchyTree((AbsolutePanel) treePanel.getParent());
		treePanel.add(targetHierarchyTree.getTree());
				
		//button row:
		LayoutContainer buttonRow = createAddRemoveButtons();
		
		//all together
		LayoutContainer panel = new LayoutContainer();		
		RowLayout rLayout = new RowLayout(Orientation.HORIZONTAL);
		rLayout.setExtraStyle("align-right");
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(300, 22);
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 5, 0, 0));
		addButton(BTN_EMPTY_LIST, constants.emptyList(), panel, layoutData, false, constants.emptyList());
		
		LayoutContainer cont = arrangeCaptionTreeAndButtons(panel, 
				constants.createCustomizedHierarchy(), treePanel, null, buttonRow, null);
		return cont;
	}
	private final LayoutContainer createAddRemoveButtons() {
		LayoutContainer panel = new LayoutContainer();		
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser(); 
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(280, 22); //have to hard code to make it visible in FF...
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 5, 0, 0));
		showOnRight = new CheckBox();
		showOnRight.setBoxLabel(constants.alwaysShowFilterOnRight());
		showOnRight.setValue(false);
		panel.add(showOnRight, layoutData);
//		addIconButton(BTN_ADD_NODE, panel, layoutData);
//		addIconButton(BTN_REMOVE_NODE, panel, layoutData);
		return panel;
	}
	
	private final LayoutContainer createTreePanel(int width) {
		AbsolutePanel treePanel = new AbsolutePanel();
		treePanel.setPixelSize(width, 230);
		LayoutContainer lc = new LayoutContainer();
		lc.setSize(width, 230);
		lc.setScrollMode(Scroll.AUTOY);
		lc.setStyleAttribute("backgroundColor", "white");
		treePanel.add(lc);
		return lc;
	}
	private final LayoutContainer arrangeCaptionTreeAndButtons(LayoutContainer headerButtons, String caption,
			LayoutContainer treePanel, LayoutContainer eButtons, LayoutContainer buttonRow, LayoutContainer regex) {
		LayoutContainer content = new LayoutContainer();
		content.setHeight(365);
		content.setLayout(new RowLayout());
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(5, 0, 0, 0));
		
		if (headerButtons != null) {
			content.add(headerButtons);
		}
		content.add(new Label(caption));
		content.add(treePanel, layoutData);
		if (eButtons != null) {
			content.add(eButtons, layoutData);
		}
		content.add(buttonRow, layoutData);
		if (regex != null) {
			content.add(regex, layoutData);
		} else {
			LayoutContainer panel = new LayoutContainer();		
			panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
			panel.setSize(230, 50); //have to hard code to make it visible in FF...
			RowData lData = new RowData();
			lData.setMargins(new Margins(5, 5, 0, 0));
			LabelField l = new LabelField();
			l.setText(constants.shiftCtrlHint());
			panel.add(l, lData);
			content.add(panel, layoutData);
		}

		return content;
	}

	class KeyButton extends Button {
		KeyButton(String caption) {
			super(caption);
			sinkEvents(Event.ONKEYDOWN | Event.ONKEYUP |
					   Event.ONMOUSEDOWN | Event.ONMOUSEUP);
		}
		
		  public void onBrowserEvent(Event event) {
			  int type = DOM.eventGetType(event);
			  if (type == Event.ONKEYDOWN) {
				  if (event.getShiftKey() && !isShiftPressed) {
					  isShiftPressed = true;
				  }
				  if (event.getCtrlKey() && !isCtrlPressed) {
					  isCtrlPressed = true;
				  }
			  } else if (type == Event.ONKEYUP) {
				  if (!event.getShiftKey() && isShiftPressed) {
					  isShiftPressed = false;
				  }
				  if (!event.getCtrlKey() && isCtrlPressed) {
					  isCtrlPressed = false;
				  }
			  } else if (type == Event.ONMOUSEDOWN) {
				  if (event.getShiftKey() && !isShiftPressed) {
					  isShiftPressed = true;
				  }
				  if (event.getCtrlKey() && !isCtrlPressed) {
					  isCtrlPressed = true;
				  }
			  } else if (type == Event.ONMOUSEUP) {
				  if (!event.getShiftKey() && isShiftPressed) {
					  isShiftPressed = false;
				  }				  
				  if (!event.getCtrlKey() && isCtrlPressed) {
					  isCtrlPressed = false;
				  }
			  }
			  super.onBrowserEvent(event);
		  }		
	}
	
	private final void addButton(String id, String caption, LayoutContainer panel, LayoutData layoutData, boolean toggle, String tooltip) {
		Button btn = toggle ? new ToggleButton(caption) : new KeyButton(caption);
		btn.setId(id);
		btn.addListener(Events.Select, this);		
		btn.setToolTip(tooltip);
		panel.add(btn, layoutData);
	}
	
	private final LayoutContainer createButtonsPanel() {
		LayoutContainer buttons = new LayoutContainer();
		LayoutContainer panel = new LayoutContainer();		
		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panel.setSize(25, 22); //have to hard code to make it visible in FF...		
		
		buttons.setAutoHeight(true);
		buttons.setLayout(new RowLayout());
		RowData data = new RowData();
		data.setMargins(new Margins(0, 0, 25, 6));
		buttons.add(panel);
		buttons.add(new Label(constants.filterOr()), data);
		
		RowData layoutData = new RowData();
		layoutData.setMargins(new Margins(0, 0, 5, 0));		
		addFilterButton = addIconButton(BTN_ADD, buttons, layoutData, constants.addElementToFilter());
		addIconButton(BTN_DELETE, buttons, layoutData, constants.removeElementFromFilter());
		addIconButton(BTN_MOVE_UP, buttons, layoutData, constants.moveElementUp());
		addIconButton(BTN_MOVE_DOWN, buttons, layoutData, constants.moveElementDown());
		RowData constraintBtnData = new RowData();
		constraintBtnData.setMargins(new Margins(15, 0, 0, 0));
//		addIconButton(BTN_CONSTRAINT, buttons, constraintBtnData);
		return buttons;
	}
	private final Button addIconButton(String icon, LayoutContainer panel, LayoutData layoutData, String tooltip) {
		Button btn = new Button();
		btn.setIconStyle(icon);
		btn.setId(icon);
		btn.setToolTip(tooltip);
		btn.addListener(Events.Select, this);
		panel.add(btn, layoutData);
		return btn;
	}
	
	boolean isFilterOnRight() {
		if (showOnRight == null) {
			return false;
		}
		return showOnRight.getValue();
	}
	
	public void handleEvent(ComponentEvent be) {
		String btnId = be.component.getId();
		if (btnId.equals(BTN_ADD)) {
			addSelection();
		} else if (btnId.equals(BTN_DELETE)) {
			removeSelection();
		} else if (btnId.equals(BTN_MOVE_UP)) {
			moveUpSelection();
		} else if (btnId.equals(BTN_MOVE_DOWN)) {
			moveDownSelection();
		} else if (btnId.equals(BTN_ADD_NODE)) {
			addNode();
		} else if (btnId.equals(BTN_REMOVE_NODE)) {
			removeNode();
		} else if (btnId.equals(BTN_EMPTY_LIST)) {
			targetHierarchyTree.clearSelection();
		} else if (btnId.equals(BTN_LEAVES)) {
			sourceHierarchyTree.selectLeafs(true, isShiftPressed || isCtrlPressed);
		} else if (btnId.equals(BTN_ALL)) {
			sourceHierarchyTree.selectAllVisible();
		} else if (isLevelSelectionButton(btnId)) {
			sourceHierarchyTree.selectLevel(getLevel(btnId), true, isShiftPressed || isCtrlPressed);
		} else if (btnId.equals(BTN_SELECT_BRANCH)) {
			sourceHierarchyTree.selectBranch();
		} else if (btnId.equals(BTN_SEARCH_SELECT)) {
			sourceHierarchyTree.selectByRegEx(regExField.getValue(), true, isShiftPressed || isCtrlPressed);
		} else if (btnId.equals(BTN_INVERT)) {
			sourceHierarchyTree.invertSelection();
		} else if (btnId.equals(BTN_EXPAND)) {
			sourceHierarchyTree.expandNextLevel();
		} else if (btnId.equals(BTN_EXPAND_ALL)) {
			sourceHierarchyTree.expandAll();
		} else if (btnId.equals(BTN_COLLAPSE)) {
			sourceHierarchyTree.collapseDeepestLevel();
		} else if (btnId.equals(BTN_COLLAPSE_ALL)) {
			sourceHierarchyTree.collapseAll();
		}
	}
	
	private final boolean isLevelSelectionButton(String id) {
		return id.equals(BTN_ONE) || id.equals(BTN_TWO) || id.equals(BTN_THREE)
				|| id.equals(BTN_FOUR) || id.equals(BTN_FIVE);
	}
	private final int getLevel(String lvl) {
		int level = -1;
		try {
			level = Integer.parseInt(lvl);
		}catch(Exception e) { /* ignore */ }
		return level;
	}
		
	final XElement[] getSelectedElements() {
		if(isExpanded()) {
			XElement [] result = sourceHierarchyTree.getSelectedElements();
			return result;
		}
		return null;
	}
	
	
	private final void addSelection() {
		if (sourceHierarchyTree.getSelection().isEmpty()) {
			return;
		}
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.copyingElements());
		String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new AsyncCallback<Void>() {
			private final void doAddSelection() {
				final List<TreeNode> orderedSelection = new ArrayList<TreeNode>();
				final LinkedHashSet<FastMSTreeItem> currentSelection = sourceHierarchyTree.getSelection();
				sourceHierarchyTree.traverse(new FastMSTreeItemVisitor() {
					public boolean visit(FastMSTreeItem item, FastMSTreeItem parent) {
						if (currentSelection.contains(item)) {
							TreeNode node = (TreeNode) item.getModel();							
							node.set("filterPath", node.getPath());
							orderedSelection.add(node);
						}
						return item.getChildCount() > 0;
					}
				});
				targetHierarchyTree.addSelection(orderedSelection);
			}
			
			public void onFailure(Throwable arg0) {
				doAddSelection();
			}

			public void onSuccess(Void arg0) {
				doAddSelection();
			}
		});
//		for(TreeNode node : sourceHierarchyTree.getSelection())
//			targetHierarchyTree.add(node.getXObject());
//		targetHierarchyTree.addSelection(sourceHierarchyTree.getSelection());
	}
	private final void removeSelection() {
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.removingElements());
		String sessionId = ((Workbench) Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new AsyncCallback<Void>() {
			private final void doRemoveSelection() {
				try {
					targetHierarchyTree.removeSelection();
					if (addFilterButton == null) {
						return;
					}
					int selectionCount = 0;
					if (targetHierarchyTree.getTree().getSelectedItems() != null) {
						selectionCount = targetHierarchyTree.getTree().getSelectedItems().size();
					}
					if (selectionCount <= 1) {
						addFilterButton.setEnabled(true);
					} else {
						addFilterButton.setEnabled(false);
					}
				} finally {
					((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
				}
			}

			public void onFailure(Throwable arg0) {
				doRemoveSelection();
			}

			public void onSuccess(Void arg0) {
				doRemoveSelection();
			}
		});
	}
	private final void moveUpSelection() {
		targetHierarchyTree.moveUpSelection();
	}
	private final void moveDownSelection() {
		targetHierarchyTree.moveDownSelection();
	}
	private final void addNode() {
		final MessageBox prompt = MessageBox.prompt("Add custom element",
				"Please specify element name:");
		// TODO BUG IN GXT: TextField is not attached!!!
		// => GET NO KEYEVENTS, so do it myself...
		ComponentHelper.doAttach(prompt.getTextBox());
		prompt.getTextBox().addKeyListener(new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				super.componentKeyUp(event);
				if (pressedEnter(event.getKeyCode())) {
					prompt.getDialog().close(
							prompt.getDialog().getButtonById(Dialog.OK));
				}
			}
		});
		prompt.addCallback(new Listener<WindowEvent>() {
			public void handleEvent(WindowEvent be) {
				// have to detach TextField too!!!
				ComponentHelper.doDetach(prompt.getTextBox());
				if (be.buttonClicked != null
						&& be.buttonClicked.getItemId().equals(Dialog.OK)) {
					createAndAddNode(((MessageBoxEvent) be).value);
				}
			}
		});
		prompt.show();
	}
	
	private final boolean pressedEnter(int keyCode) {
		return keyCode == 13; //KeyboardListener.KEY_ENTER;
	}

	private final void createAndAddNode(String name) {
		// ATTENTION:
		// NOTE: the id is important!!!! otherwise xvirtualelement is not unique
		// => many problems will result, e.g. in gxt tree and my
		// dndhierarchytree implementation!!!
		// THIS MUST HOLD FOR ALL XOBJECTS!!!!!
		// IN A TREE THERE SHOULD BE NO OBJECT WITH SAME ID!!!!
		// => IF WE ALLOW TO ADD SAME XOBJECT TWICE, WE SHOULD WRAP IT!!!
//		XVirtualElement xVirtualElement = new XVirtualElement(name);
//		XElementNode xVirtualNode = new XElementNode(xVirtualElement, xAxisHierarchy.getId(), xAxisHierarchy.getViewId());
//		targetHierarchyTree.add(xVirtualNode);		
	}
	private final void removeNode() {
		targetHierarchyTree.removeSelection();
	}
}