/*
*
* @file CubeViewEditor.java
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
* @version $Id: CubeViewEditor.java,v 1.149 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintConfiguration;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintResult;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.widgets.client.container.Container;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerListener;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerWidget;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;
import com.tensegrity.palo.gwt.widgets.client.cubevieweditor.HierarchySelectionWidget;
import com.tensegrity.palo.gwt.widgets.client.cubevieweditor.HierarchyWidget;
import com.tensegrity.palo.gwt.widgets.client.cubevieweditor.HierarchyWidgetListener;
import com.tensegrity.palo.gwt.widgets.client.cubevieweditor.ViewEditorPanel;
import com.tensegrity.palo.gwt.widgets.client.dnd.PickupDragController;
import com.tensegrity.palo.gwt.widgets.client.palotable.Cell;
import com.tensegrity.palo.gwt.widgets.client.palotable.CellChangedListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.Content;
import com.tensegrity.palo.gwt.widgets.client.palotable.ExpandListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.ItemClickListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.ItemExpandListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;
import com.tensegrity.palo.gwt.widgets.client.util.Limiter;
import com.tensegrity.wpalo.client.DisplayFlags;
import com.tensegrity.wpalo.client.WPaloEvent;
import com.tensegrity.wpalo.client.WPaloPropertyServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.async.WaitCursorCallback;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.ILocalMessages;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.services.folder.WPaloFolderServiceProvider;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.TestFastTreeDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowser;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewEditorTab;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.PaloSuiteViewCreationDialog;
import com.tensegrity.wpalo.client.ui.mvc.viewbrowser.importer.ViewImportDialog;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
import com.tensegrity.wpalo.client.ui.widgets.BusyIndicatorPanel;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

public class CubeViewEditor extends LayoutContainer implements ContainerListener, Listener<ToolBarEvent>, ItemExpandListener, ItemClickListener, HierarchyWidgetListener, ClickHandler {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	protected transient final ILocalMessages  messages  = Resources.getInstance().getMessages();

	private static final String SAVE_BTN = "vieweditor.button.save";
	private static final String SAVE_AS_BTN = "vieweditor.button.saveas";
	private static final String PRINT_BTN = "vieweditor.button.print";
	private static final String CONNECTION_PICKER_BTN = "vieweditor.button.pickConnection";
	private static final String REVERSE_ROWS_BTN = "vieweditor.button.reverserows";
	private static final String REVERSE_COLS_BTN = "vieweditor.button.reversecols";
	private static final String HIDE_EMPTY_BTN = "vieweditor.button.hideempty";
	private static final String SHOW_RULES_BTN = "vieweditor.button.showrules";
	private static final String REFRESH_BTN = "vieweditor.button.refresh";	
	private static final String SHOW_INFO_BTN = "vieweditor.button.showinfo";
	
	private ContentPanel dimRepository = new ContentPanel();
	
	private ContentPanel viewPanel = new ContentPanel();
	
	private ViewEditorPanel editorPanel;
	private Container repositoryContainer;
	private XViewModel view;
	private ToggleToolItem hideEmptyCells;
	private ToggleToolItem reverseColumns;
	private ToggleToolItem reverseRows;
	private ToggleToolItem showRules;
	private TextToolItem showInfoButton;
	private ToolBar toolbar;
	
	DisplayFlags displayFlags;
	private boolean hideInfo = true;
	
	public static final int RIGHT_NONE   =  0;
	public static final int RIGHT_READ   =  1;
	public static final int RIGHT_WRITE  =  2;
	public static final int RIGHT_DELETE =  4;
	public static final int RIGHT_CREATE =  8;
	public static final int RIGHT_GRANT  = 16;
	
	//-1 no yet set, 0 no write right, 1 write right
	private int writeRight = -1;
	public static native void refreshSubobjects(String id) /*-{	  
		try{
	  		$wnd.sendMessage({'id': id, 'msg': 'Sub Object Saved!!'},'subobjectsaved');	  
	  	}catch(e){
	  	}
	}-*/;
	
	//drag n drop support:
	private final PickupDragController dragController = 
		new PickupDragController(RootPanel.get(), false);
	
	//for dis-/enabling via mark dirty:
	TextToolItem save = new TextToolItem();
	TextToolItem saveAs = new TextToolItem();
	TextToolItem print = new TextToolItem();
	
	private final EditorTab tab;
	private boolean isDirty;
	private BorderLayoutData dimRepoLayoutData;
	private SetupHandler setupHandler;
	private BusyIndicatorPanel waitPanel = null;
	private final HandlerRegistration cubeViewResizeHandler;
	
	private final HashMap <String, XElementNode []> visibles = new HashMap <String, XElementNode []>();
	private final HashMap <String, XElementNode []> oldVisibles = new HashMap <String, XElementNode []>();
//	public static boolean hasBeenResized = false;
//	public static boolean fromDirectLink = false;
	
//	private int SHOW_WAIT_CURSOR_THRESHOLD = 0;	
	private String tableColumnsMaxWidth;
	private String tableRowsHeaderMaxWidth;
	private String tableDataCellToLongString;
	
	private final void initMaxWidths() {
		WPaloPropertyServiceProvider.getInstance().getStringProperty("tableColumnsMaxWidth", new AsyncCallback<String>() {				
			private final void determineWidth(String r) {
				tableColumnsMaxWidth = r;
				TextToolItem measure = new TextToolItem(r);
				editorPanel.add(measure);
				int size = measure.getOffsetWidth();
				if (size > 0) {
					Content.MAX_COLUMN_WIDTH = size;
				}
				editorPanel.remove(measure);
				
			}
			
			public void onFailure(Throwable arg0) {					
				determineWidth("-999.999.999.999.999.999,90");
			}

			public void onSuccess(String result) {
				if (result == null || result.isEmpty()) {
					determineWidth("-999.999.999.999.999.999,90");
				} else {
					determineWidth(result);
				}
			}
		});

		WPaloPropertyServiceProvider.getInstance().getStringProperty("tableRowsHeaderMaxWidth", new AsyncCallback<String>() {				
			private final void determineWidth(String r) {
				tableRowsHeaderMaxWidth = r;
				TextToolItem measure = new TextToolItem(r);
				editorPanel.add(measure);
				int size = measure.getOffsetWidth();
				if (size > 0) {
					Content.MAX_ROWS_COL_WIDTH = size;
				}
				editorPanel.remove(measure);
			}
			
			public void onFailure(Throwable arg0) {					
				determineWidth("-999.999.999.999.999.999,90");
			}

			public void onSuccess(String result) {
				if (result == null || result.isEmpty()) {
					determineWidth("-999.999.999.999.999.999,90");
				} else {
					determineWidth(result);
				}
			}
		});

		WPaloPropertyServiceProvider.getInstance().getStringProperty("tableDataCellToLongString", new AsyncCallback<String>() {				
			public void onFailure(Throwable arg0) {					
				tableDataCellToLongString = "#####";
				Content.FILL_STRING = "#####";
			}

			public void onSuccess(String result) {
				if (result == null || result.isEmpty()) {
					Content.FILL_STRING = "#####";
					tableDataCellToLongString = "#####";
				} else {
					Content.FILL_STRING = result;
					tableDataCellToLongString = result;
				}
			}
		});		
	}
		
	public CubeViewEditor(EditorTab tab) {		
		WPaloPropertyServiceProvider.getInstance().getBooleanProperty("hideInfoButtonInView", false,
				new AsyncCallback<Boolean>() {
					private final void checkToolbar() {
						if (toolbar != null) {
							if (hideInfo) {
								ToolItem it = toolbar.getItemByItemId(SHOW_INFO_BTN); 
								if (it != null) {									
									toolbar.remove(it);
									toolbar.remove(toolbar.getItem(toolbar.getItemCount() - 1));
								}
							} else {
								ToolItem it = toolbar.getItemByItemId(SHOW_INFO_BTN); 
								if (it == null) {									
									toolbar.add(new SeparatorToolItem());
									toolbar.add(showInfoButton);									
								}								
							}
						}
					}
					
					public void onFailure(Throwable arg0) {
						hideInfo = false;
						checkToolbar();
					}

					public void onSuccess(Boolean result) {
						hideInfo = result;
						checkToolbar();
					}
				});
		cubeViewResizeHandler = 
			Window.addResizeHandler(new ResizeHandler() {
				public void onResize(ResizeEvent arg0) {
					if (editorPanel.getTable().isChangeSize()) {
						editorPanel.getTable().setChangeSize(false);
						editorPanel.setVisible(false);
						if (waitPanel == null) {
							waitPanel = new BusyIndicatorPanel();
							waitPanel.show(constants.resizing(), false);
						}
					}
				}
			});
		this.tab = tab;
		setLayout(new BorderLayout());
		setMonitorWindowResize(true);
	}	
			
	protected void onDetach() {
		super.onDetach();
		if (cubeViewResizeHandler != null) {
			cubeViewResizeHandler.removeHandler();
		}
	}
	
	protected void onRender(final Element parent, final int index) {
		super.onRender(parent, index);
	}

	public final void initialize(boolean showSaveButtons, DisplayFlags displayFlags) {
		//the editor consists of two panels:
		this.displayFlags = displayFlags;
		if (this.displayFlags == null) {
			this.displayFlags = DisplayFlags.empty;
		}
		initDimensionPanel();
		initViewPanel(showSaveButtons);
		initDnD();
		initEventHandling();
		markDirty(false);		
	}
	
	final int getWritePermission() {
		return writeRight;
	}

	protected void onWindowResize(int width, int height) {		
		editorPanel.getTable().setChangeSize(true);
		editorPanel.setVisible(true);
		if (waitPanel != null) {			
			waitPanel.hide();
			waitPanel = null;			
		}
//		hasBeenResized = true;
		super.onWindowResize(width, height);
	}
	
	public final void setInput(final XViewModel view) {
		this.view = view;
		editorPanel.hideEmptyCells(view.isHideEmptyCells());
		editorPanel.reverseColumns(view.isColumnsReversed());
		editorPanel.reverseRows(view.isRowsReversed());
		editorPanel.setInput(view);
		fastSetInputInternal(view);				
		_layout();
		restoreRepository(view);
	}
	
	private final void fastSetInput(XViewModel view) {
		this.view = view;
		editorPanel.fastSetInput(view);
		fastSetInputInternal(view);		
		
		int clientWidth = displayFlags.isHideStaticFilter() ? Limiter.setClientWidth(0) : Limiter.setClientWidth(repositoryContainer, dimRepository.getInnerWidth()-2);
		int clientHeight = displayFlags.isHideStaticFilter() ? Limiter.setClientHeight(0) : Limiter.setClientHeight(repositoryContainer, dimRepository.getInnerHeight() - 1);
		if (clientWidth <= 0) {
			clientWidth = 22;
		}
		if (clientHeight <= 0) {
			clientHeight = 480;
		}
		if (clientWidth > 200) {
			clientWidth = 22;
		}
		if (!displayFlags.isHideStaticFilter()) {
			repositoryContainer.layout(clientWidth, clientHeight);			
		} else {
			repositoryContainer.layout(2, 2);
			repositoryContainer.setVisible(false);
		}
		restoreRepository(view);
	}
	
	private final void fastSetInputInternal(XViewModel view) {
		initialiseRepository(view);
		if (!displayFlags.isHideStaticFilter()) {
			dimRepository.setHeading(constants.staticFilters());
		}
		viewPanel.setHeading(messages.view(modify(view.getName())));
		
		hideEmptyCells.toggle(view.isHideEmptyCells());
		if (view.isHideEmptyCells()) {
			editorPanel.hideEmptyCells(true);
		}
		reverseColumns.toggle(view.isColumnsReversed());
		if (view.isColumnsReversed()) {
			editorPanel.reverseColumns(true);
		}
		reverseRows.toggle(view.isRowsReversed());
		if (view.isRowsReversed()) {
			editorPanel.reverseRows(true);
		}
		showRules.toggle(view.isShowRules());
		if (view.isShowRules()) {
			editorPanel.markRuleBasedCells(true);
		}
	}
			
	private final void restoreRepository(XViewModel view) {
		//initial width:
		if (!displayFlags.isHideStaticFilter()) {
			XAxis repositoryAxis = view.getRepositoryAxis();		
			int width = getPreferredWidth(repositoryAxis.getProperty(XAxis.PREFERRED_WIDTH));			
			setupHandler.setupWidth(width);
			setupHandler.setupDone();
		}
				
	}
	
	public final boolean isDirty() {
		return isDirty;
	}
	
	private final void initialiseRepository(XViewModel view) {
		XAxis repositoryAxis = view.getRepositoryAxis();
		addRepositoryAxes(repositoryAxis.getAxisHierarchies());
		checkDnDState();
	}
	private final void addRepositoryAxes(List<XAxisHierarchy> hierarchies) {
		repositoryContainer.removeAll();
		for(XAxisHierarchy hierarchy : hierarchies)
			repositoryContainer.add(hierarchy);

	}
	private final int getPreferredWidth(String width) {
		if(width != null) {
			try {
				return Integer.parseInt(width);
			}catch(Exception ex) {
				//ignore, we return 0 
			}
		}
		return 0;
	}
	public final XViewModel getView() {
		return view;
	}

	//TODO bug in gxt?? how to retrieve expand/collapse events??
	public boolean remove(Widget widget) {
		if (setupHandler != null) {
			setupHandler.regionExpanded();
		}
		boolean result = super.remove(widget);
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void result) {
				checkDnDState();
			}
		});				
		return result;
	}
	
	private final void modifyView(XView xView, final String viewId) {
		markDirty(false);
		tab.close();
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.creatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloFolderServiceProvider.getInstance().importView(sessionId,
				xView,
					new Callback<XView>(constants.couldNotCreateView()) {
						public void onSuccess(XView xView) {
							hideWaitCursor();
							((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.openingView());
							XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
							List <Boolean> dispFlags = new ArrayList<Boolean>();
							dispFlags.add(displayFlags.isHideTitleBar());
							dispFlags.add(displayFlags.isHideToolBar());
							dispFlags.add(displayFlags.isHideSave());
							dispFlags.add(displayFlags.isHideSaveAs());
							dispFlags.add(displayFlags.isHideFilter());
							dispFlags.add(displayFlags.isHideStaticFilter());
							dispFlags.add(displayFlags.isHideHorizontalAxis());
							dispFlags.add(displayFlags.isHideVerticalAxis());
							dispFlags.add(displayFlags.isHideConnectionPicker());
							DisplayFlags.setDisplayFlagsFor(xView, user, dispFlags, null);
							xView.setExternalId(viewId);
							Dispatcher.get().dispatch(WPaloEvent.EDIT_VIEWBROWSER_VIEW, xView);
						}
					});
	}		
	
	public void handleEvent(final ToolBarEvent tbe) {
		String button = tbe.item.getId();
	
		if(button.equals(SAVE_BTN))
			doSave();
		else if(button.equals(SAVE_AS_BTN)){
			doSaveAs();
		}else if (button.equals(PRINT_BTN)){
			doPrint();
		}else if(button.equals(REVERSE_ROWS_BTN)) {
			editorPanel.reverseRows(((ToggleToolItem) tbe.item).isPressed());
//			updateView(null);
			markDirty(true);
		}
		else if(button.equals(REVERSE_COLS_BTN)) {
			editorPanel.reverseColumns(((ToggleToolItem) tbe.item).isPressed());
//			updateView(null);
			markDirty(true);
		}
		else if(button.equals(HIDE_EMPTY_BTN)) {
			showWaitDialog(constants.updatingView());
			final boolean pressed = ((ToggleToolItem) tbe.item).isPressed();
			final int cellsToDisplay = editorPanel.previewHideEmptyCells(pressed);
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
			removeLocalFilter();
			view.setNeedsRestore(true);
			WPaloCubeViewServiceProvider.getInstance().updateLoadInfo(sessionId, view, cellsToDisplay,
					new Callback<XLoadInfo>(constants.updatingViewFailed()) {
				public void onFailure(Throwable caught) {
					restoreLocalFilter(view);
					view.setNeedsRestore(false);
					hideWaitCursor();
					super.onFailure(caught);
				}
				public void onSuccess(final XLoadInfo loadInfo) {
					ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
						public void cancel() {
							restoreLocalFilter(view);
							view.setNeedsRestore(false);
							hideWaitCursor();
							((ToggleToolItem) tbe.item).toggle(!pressed);
						}
						public void proceed(boolean state) {							
							restoreLocalFilter(view);
							view.setNeedsRestore(false);
							editorPanel.hideEmptyCells(pressed);
							markDirty(true);
							hideWaitCursor();
						}
					};
					LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
				}
			});
//			updateView(null);
		}
		else if(button.equals(SHOW_RULES_BTN)) {
			editorPanel.markRuleBasedCells(((ToggleToolItem) tbe.item).isPressed());
//			updateView(null);
			markDirty(true);
		}
		else if(button.equals(REFRESH_BTN)) {
			doRefresh();
		} else if (button.equals(SHOW_INFO_BTN)) {
			doShowAbout();
		} else if (button.equals(CONNECTION_PICKER_BTN)) {
			final PaloSuiteViewCreationDialog dia =
				new PaloSuiteViewCreationDialog();
			dia.addButtonListener(ViewImportDialog.BUTTON_OK,
					new Listener<BaseEvent>() {
						public void handleEvent(BaseEvent be) {
							final XView[] xViews = dia.getSelectedViews();
							if (xViews != null && xViews.length == 1) {
								final String extId = view.getExternalId();
								view.setExternalId("_");
								((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.applyingModifications());
								String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
								WPaloCubeViewServiceProvider.getInstance().saveView(sessionId,
										view, new Callback<XViewModel>(){
									public void onSuccess(XViewModel arg0) {
										((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
										xViews[0].setExternalId(extId);
										modifyView(xViews[0], extId);
									}
								});
							}
						}
					});
			dia.show();			
		}
	}
	
	public void dropped(final ContainerWidget widget, int atIndex) {
		markDirty(true);
		//check if row or column container has changed:
		if(hasRowChanged() || hasColumnChanged()) {
		    willUpdateView();			
		} else {
			//PR 724: we have to always update server model, but 
			//        if neither rows nor columns have changed,
			//        we do not have to layout the table.
			adjustView();
			String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();			
			removeLocalFilter();
			view.setNeedsRestore(true);
			WPaloCubeViewServiceProvider.getInstance().proceedUpdateViewWithoutTable(sessionId, 
					view,
					new Callback<XViewModel>(messages.updatingViewFailed(ViewBrowserModel.modify(view.getName()))) {
						public void onSuccess(XViewModel view) {
							restoreLocalFilter(CubeViewEditor.this.view);
							restoreLocalFilter(view);
							view.setNeedsRestore(false);
							fastSetInput(view);
						}
					});
		}
	}
	
	private boolean isInRowOrColumn(XAxisHierarchy hier) {
		for (XAxisHierarchy r: editorPanel.getRowHierarchies()) {
			if (r.equals(hier)) {
				return true;
			}
		}
		for (XAxisHierarchy c: editorPanel.getColumnHierarchies()) {
			if (c.equals(hier)) {
				return true;
			}
		}		
		return false;
	}
	
	private boolean hasRowChanged() {
		return !areEqual(editorPanel.getRowHierarchies(), view.getRowAxis()
				.getAxisHierarchies());
	}

	private boolean hasColumnChanged() {
		return !areEqual(editorPanel.getColumnHierarchies(), view
				.getColumnAxis().getAxisHierarchies());
	}
	
	private final void removeFilterNodes(XAxis axis) {
		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
			XElementNode [] oldVis = hier.getOldVisibleElements();
			if (oldVis != null && oldVis.length > 0) {
				hier.setOldVisibleElements(null);
				oldVisibles.put(hier.getId(), oldVis);
			}
			XElementNode [] vis = hier.getVisibleElements();
			if (vis != null && vis.length > 0) {
				hier.setVisibleElements(null);
				visibles.put(hier.getId(), vis);
			}
		}		
	}
	
	private final static void removeFilterNodesForGood(XAxis axis) {
		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
			XElementNode [] oldVis = hier.getOldVisibleElements();
			if (oldVis != null && oldVis.length > 0) {
				hier.setOldVisibleElements(null);
			}
			XElementNode [] vis = hier.getVisibleElements();
			if (vis != null && vis.length > 0) {
				hier.setVisibleElements(null);
			}
		}		
	}

	private final void restoreFilterNodes(XAxis axis) {
		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
			if (oldVisibles.containsKey(hier.getId())) {
				hier.setOldVisibleElements(oldVisibles.get(hier.getId()));
//				oldVisibles.remove(hier.getId());
			} else {
				hier.setOldVisibleElements(null);
			}
			if (visibles.containsKey(hier.getId())) {
				hier.setVisibleElements(visibles.get(hier.getId()));
//				visibles.remove(hier.getId());				
			} else {
				hier.setVisibleElements(null);
			}
		}		
	}

	private final void restoreSwappedFilterNodes(XAxis axis, XAxis newAxis) {
		for (XAxisHierarchy hier: axis.getAxisHierarchies()) {
			if (oldVisibles.containsKey(hier.getId())) {
				newAxis.getAxisHierarchy(hier.getId()).setOldVisibleElements(oldVisibles.get(hier.getId()));
			} else {
				newAxis.getAxisHierarchy(hier.getId()).setOldVisibleElements(null);
			}
			if (visibles.containsKey(hier.getId())) {
				newAxis.getAxisHierarchy(hier.getId()).setVisibleElements(visibles.get(hier.getId()));
			} else {
				newAxis.getAxisHierarchy(hier.getId()).setVisibleElements(null);
			}
		}		
	}

	private final void removeLocalFilter() {
		if (view == null) {
			return;
		}
		removeFilterNodes(view.getSelectionAxis());
		removeFilterNodes(view.getColumnAxis());
		removeFilterNodes(view.getRepositoryAxis());
		removeFilterNodes(view.getRowAxis());
	}
	
	public final static void removeLocalFilter(XViewModel view) {
		if (view == null) {
			return;
		}
		removeFilterNodesForGood(view.getSelectionAxis());
		removeFilterNodesForGood(view.getColumnAxis());
		removeFilterNodesForGood(view.getRepositoryAxis());
		removeFilterNodesForGood(view.getRowAxis());		
	}

	private final void restoreLocalFilter(XViewModel view) {
		if (view == null) {
			return;
		}
		restoreFilterNodes(view.getSelectionAxis());
		restoreFilterNodes(view.getColumnAxis());
		restoreFilterNodes(view.getRepositoryAxis());
		restoreFilterNodes(view.getRowAxis());		
	}
	
//	private final void restoreSwappedLocalFilter(XViewModel view) {
//		if (view == null) {
//			return;
//		}
//		restoreFilterNodes(view.getSelectionAxis());
//		restoreSwappedFilterNodes(view.getColumnAxis(), view.getRowAxis());
//		restoreFilterNodes(view.getRepositoryAxis());
//		restoreSwappedFilterNodes(view.getRowAxis(), view.getColumnAxis());		
//	}

	private final void willUpdateView() {
		showWaitDialog(constants.updatingView());
		adjustView();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		removeLocalFilter();
		view.setNeedsRestore(true);
		WPaloCubeViewServiceProvider.getInstance().willUpdateView(sessionId, view,
				new Callback<XLoadInfo>(constants.updatingViewFailed()) {
			public void onFailure(Throwable caught) {
				restoreLocalFilter(view);
				view.setNeedsRestore(false);
				hideWaitCursor();				
				super.onFailure(caught);
			}
			public void onSuccess(final XLoadInfo loadInfo) {
				ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
					public void cancel() {
						cancelUpdateView();
					}
					public void proceed(boolean state) {
						proceedUpdateView(loadInfo);
					}
				};
				LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
			}
		});
	}
	private final void proceedUpdateView(XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedUpdateView(sessionId, 
				view,
				new Callback<XViewModel>(messages.updatingViewFailed(ViewBrowserModel.modify(view.getName()))) {
					public void onSuccess(XViewModel view) {
						if (view.needsRestore()) {
							restoreLocalFilter(view);
							view.setNeedsRestore(false);
						}
						restoreLocalFilter(view);						
						reset();
						setInput(view);
						hideWaitCursor();
						markDirty(true);
					}
				});
	}
	private final void cancelUpdateView() {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().cancelUpdateView(sessionId, 
				view,
				new WaitCursorCallback<XViewModel>(constants.cancelingUpdate(),
						messages.cancelViewUpdateFailed(ViewBrowserModel.modify(view.getName()))) {
					public void onSuccess(XViewModel view) {
						if (view.needsRestore()) {
							restoreLocalFilter(view);
							view.setNeedsRestore(false);
						}
						CubeViewEditor.this.view = view;
						initialiseRepository(view);
						editorPanel.initRowContainer(view.getRowAxis());
						editorPanel.initColumnContainer(view.getColumnAxis());
						editorPanel.initSelectionContainer(view.getSelectionAxis());
						_layout();
						hideWaitCursor();
					}
				});
	}
	public void removed(ContainerWidget widget) {
	}
	
	protected void onAfterLayout() {		
		super.onAfterLayout();		
		_layout();		
	}

	final void checkDnDState() {
		if (writeRight == -1 && view != null) {
			XUser usr = ((Workbench)Registry.get(Workbench.ID)).getUser();
			WPaloCubeViewServiceProvider.getInstance().isOwner(usr.getSessionId(), view.getId(), new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {
					XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
					writeRight = 0;
					if (user.hasRoleName("EDITOR")) {
						writeRight = 1;
						repositoryContainer.register(dragController);						
					}
				}

				public void onSuccess(Boolean result) {
					if (result) {
						writeRight = 1;
					} else {
						XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
						writeRight = 0;
						if (user.hasRoleName("EDITOR")) {
							writeRight = 1;
							repositoryContainer.register(dragController);						
						}
					}
				}
			});
		} else if (writeRight == 1) {
			repositoryContainer.register(dragController);
		}
	}
	
	private final void initDnD() {
		//initializes the drag and drop behavior within the editor
		editorPanel.register(dragController);
	}

	private final void initDimensionPanel() {
		//	- a panel for all cube dimensions, at west position
		dimRepoLayoutData = new BorderLayoutData(LayoutRegion.WEST);
		dimRepoLayoutData.setMargins(new Margins(0,5,0,0));
		dimRepoLayoutData.setSplit(true);
		dimRepoLayoutData.setCollapsible(true);
		dimRepoLayoutData.setFloatable(false); //true);
		dimRepoLayoutData.setSize(160);
		dimRepoLayoutData.setMinSize(40);
		dimRepoLayoutData.setMaxSize(220);
		dimRepoLayoutData.setHidden(displayFlags.isHideStaticFilter());
//		dimRepository = new ContentPanel(new RowLayout(Orientation.VERTICAL));
		dimRepository.setLayoutOnChange(true);
		dimRepository.setScrollMode(Scroll.AUTOY);
		dimRepository.setLayout(new RowLayout(Orientation.VERTICAL));
		dimRepository.setHeight("100%");
		dimRepository.setData("layout_region", LayoutRegion.WEST);
		dimRepository.sinkEvents(Events.BeforeExpand);
		dimRepository.addListener(Events.BeforeExpand, new Listener(){
			public void handleEvent(BaseEvent be) {
				if (writeRight != 1) {
					be.doit = false;
				}
			}
		});
		if (displayFlags.isHideStaticFilter()) {
			dimRepository.setVisible(false);
		}
		if (displayFlags.isHideStaticFilter()) {
			dimRepoLayoutData.setCollapsible(false);
			dimRepoLayoutData.setFloatable(false);
			dimRepoLayoutData.setMinSize(0);
			dimRepoLayoutData.setMaxSize(0);
			dimRepoLayoutData.setSize(0);
			add(new LabelField(), dimRepoLayoutData);
		} else {
			add(dimRepository, dimRepoLayoutData);
		}
		initRepositoryContainer();
		if (displayFlags.isHideStaticFilter()) {
			dimRepository.hide();
		}
//		if (!displayFlags.isHideStaticFilter()) {
			dimRepository.add(repositoryContainer, new RowData(1,1));				
			if(getLayout() instanceof BorderLayout)
				setupHandler = new SetupHandler(this, LayoutRegion.WEST);
//		}
	}
	
	private final void initRepositoryContainer() {
		repositoryContainer = new Container(new DimensionRepositoryRenderer(displayFlags.isHideStaticFilter())){
			protected void onAttach() {
				super.onAttach();
				checkDnDState();
			}			
		};
		repositoryContainer.setStyleName("static-filter");
		Label emptyLabel = new Label(constants.dropToFillStaticFilters());
		emptyLabel.setStyleName("empty-label");
		DOM.setStyleAttribute(emptyLabel.getElement(), "textAlign", "center");
		repositoryContainer.setEmptyLabel(emptyLabel);
		if (displayFlags.isHideStaticFilter()) {
			repositoryContainer.setVisible(false);			
		}
		
	}
	private final void initViewPanel(boolean showSaveButtons) {
		//	- a panel to display the current view, at center position
		BorderLayoutData layoutData = new BorderLayoutData(LayoutRegion.CENTER);
		layoutData.setSize(1.0f);
		layoutData.setCollapsible(false);
		layoutData.setFloatable(false);
		layoutData.setHidden(false);
		viewPanel.clearState();
		viewPanel.setLayout(new RowLayout());
		viewPanel.setCollapsible(false);
		viewPanel.setHideCollapseTool(true);		
		toolbar = new ToolBar();
		initMaxWidths();
		
		//the toolbar:
		save.setId(SAVE_BTN);
		save.setIconStyle("ten-icons-save");
		save.addListener(Events.Select, this);
		save.setToolTip(constants.saveView());
		if (!displayFlags.isHideSave()) {
			toolbar.add(save);
		}
		
		saveAs.setId(SAVE_AS_BTN);
		saveAs.setIconStyle("ten-icons-save-as");
		saveAs.addListener(Events.Select, this);
		saveAs.setToolTip(constants.saveViewAsSubobject());

		final String[] hideSaveSubobjects = {"false"};

		WPaloCubeViewServiceProvider.getInstance().getSpagoBIUserMode(new Callback<String>(){
			public void onFailure(Throwable t) {	
				//MessageBox.alert("Save as", "failure", null);
				super.onFailure(t);
			}
			public void onSuccess(String id) {
				//MessageBox.alert("Save as", "is dev:"+id, null);
				if(id!= null && id.equalsIgnoreCase("true")){
					 //toolbar.remove(saveAs);
					 hideSaveSubobjects[0] = "true";	
					 
				}
				if ((hideSaveSubobjects[0]).equals("false")) {					
					//MessageBox.alert("Save as", "not DEV", null);
					if(!displayFlags.isHideSaveAs()){
						toolbar.add(saveAs);	
					}
				}
/*				if (!displayFlags.isHideSave() || !displayFlags.isHideSaveAs() || (hideSaveSubobjects[0]).equals("true")) {
					toolbar.add(new SeparatorToolItem());
				}*/
			}
		});

		
		if (((Workbench)Registry.get(Workbench.ID)).isPaloSuite()) {
			if (!displayFlags.isHideConnectionPicker()) {
				TextToolItem txtItem = new TextToolItem();
				txtItem.setId(CONNECTION_PICKER_BTN);
				txtItem.setIconStyle("icon-import-view");
				txtItem.setToolTip(constants.changeCube());
				txtItem.addListener(Events.Select, this);
				toolbar.add(txtItem);
				toolbar.add(new SeparatorToolItem());
			}
		}
		print.setId(PRINT_BTN);
		print.setIconStyle("icon-print");
		print.addListener(Events.Select, this);
		print.setToolTip(constants.printView());
		if (!displayFlags.isHidePrint()) {
			toolbar.add(print);
			toolbar.add(new SeparatorToolItem());
		}
		
		hideEmptyCells = new ToggleToolItem();
		hideEmptyCells.setId(HIDE_EMPTY_BTN);
		hideEmptyCells.setIconStyle("ten-icons-hideEmpty");
		hideEmptyCells.addListener(Events.Select, this);
		hideEmptyCells.setToolTip(constants.hideEmpty());
		toolbar.add(hideEmptyCells);
		
		reverseColumns = new ToggleToolItem();
		reverseColumns.setId(REVERSE_COLS_BTN);
		reverseColumns.setIconStyle("ten-icons-reverseCols");
		reverseColumns.setToolTip(constants.reverseColumns());
		reverseColumns.addListener(Events.Select, this);
		toolbar.add(reverseColumns);
		// ~
		reverseRows = new ToggleToolItem();
		reverseRows.setId(REVERSE_ROWS_BTN);
		reverseRows.setIconStyle("ten-icons-reverseRows");
		reverseRows.addListener(Events.Select, this);
		reverseRows.setToolTip(constants.reverseRows());
		toolbar.add(reverseRows);		
		
		showRules = new ToggleToolItem();
		showRules.setId(SHOW_RULES_BTN);
		showRules.setIconStyle("ten-icons-viewRules");
		showRules.setToolTip(constants.markRules());
		showRules.addListener(Events.Select, this);
		toolbar.add(showRules);
		
		toolbar.add(new SeparatorToolItem());
		TextToolItem txtItem = new TextToolItem();
		txtItem.setId(REFRESH_BTN);
		txtItem.setIconStyle("icon-refresh-on");
		txtItem.setToolTip(constants.refresh());
		txtItem.addListener(Events.Select, this);
		toolbar.add(txtItem);
				
		showInfoButton = new TextToolItem();
		showInfoButton.setId(SHOW_INFO_BTN);
		showInfoButton.setIconStyle("icon-info");
		showInfoButton.setToolTip(constants.about());
		showInfoButton.addListener(Events.Select, this);
		if (!hideInfo) {
			toolbar.add(new SeparatorToolItem());
			toolbar.add(showInfoButton);
		}
		
		if (!displayFlags.isHideToolBar()) {
			viewPanel.setTopComponent(toolbar);
		} else {
			// Leave this alone!
			// Due to browser caching the top component _has_ to be set to an
			// existing value, before it can safely be overwritten by null.
			LabelField label = new LabelField();
			viewPanel.setTopComponent(label);
			viewPanel.setTopComponent(null);
		}
		//the view editor panel:		
		editorPanel = new ViewEditorPanel(this,
				displayFlags.isHideFilter(),
				displayFlags.isHideHorizontalAxis(),
				displayFlags.isHideVerticalAxis());
		editorPanel.addClickListener(this);
		editorPanel.register((ItemExpandListener) this);
		editorPanel.register((ItemClickListener) this);
		
		if (displayFlags.isHideTitleBar()) {
			viewPanel.setHeaderVisible(false);
		}
		viewPanel.add(editorPanel, new RowData(1, 1));		
		viewPanel.setLayoutData(viewPanel, layoutData);		
		add(viewPanel);
		viewPanel.expand();
	}

	private final void initEventHandling() {
		editorPanel.addContainerListener(this);
		repositoryContainer.addContainerListener(this);
		editorPanel.addCellChangedListener(new CellChangedListener() {
			public void changed(final Cell cell, final String oldValue) {
				showWaitDialog(constants.writingCellContents());
				String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
				removeLocalFilter();
				view.setNeedsRestore(true);
				WPaloCubeViewServiceProvider.getInstance().writeCell(sessionId, 
						cell.getXCell(), view, new Callback<XCellCollection>() {
							public void onFailure(Throwable caught) {
								restoreLocalFilter(view);
								view.setNeedsRestore(false);
								hideWaitCursor();
								String message = caught.getMessage();
								if (message == null) {
									MessageBox.alert(constants.error(),
											constants.writingCellFailed(), null);																			
								} else if (message.indexOf("(1009) insufficient access rights") != -1) {
									MessageBox.alert(constants.notEnoughRights(),
											constants.notEnoughRightsToModifyCell(), null);																			
								} else if (message.indexOf("(5012) sum of weights is") != -1) {
									MessageBox.alert(constants.splashingError(),
											constants.splashingHint(), null);																													
								} else {
										MessageBox.alert(constants.error(),
											constants.writingCellFailed(), null);																			
								}
								cell.setValue(oldValue, true);
							}

							public void onSuccess(XCellCollection cells) {
								try {
									restoreLocalFilter(view);
									view.setNeedsRestore(false);									
									editorPanel.setCells(cells, view);
									_layout();
								} finally {
									hideWaitCursor();
								}
							}
						});
			}
		});
		editorPanel.addExpandListener(new ExpandListener() {
			public void willCollapse(HeaderItem item) {
			}
			public void willExpand(HeaderItem item) {			
			}
		});
	}
	private final void showWaitDialog(String msg) {
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(msg);
	}
	private final void hideWaitDialog() {
		((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
	}
		
	private final void _layout() {
		//we call layout on each component:
		int clientWidth = displayFlags.isHideStaticFilter() ? Limiter.setClientWidth(1) : Limiter.setClientWidth(repositoryContainer, dimRepository.getInnerWidth()-2);
		int clientHeight = displayFlags.isHideStaticFilter() ? Limiter.setClientHeight(1) : Limiter.setClientHeight(repositoryContainer, dimRepository.getInnerHeight() - 1);
		if (clientWidth <= 0) {
			clientWidth = 22;
		}
		if (clientHeight <= 0) {
			clientHeight = 480;
		}
		if (clientWidth > 200) {
			clientWidth = 22;
		}
		if (!displayFlags.isHideStaticFilter()) {
			repositoryContainer.layout(clientWidth, clientHeight);
		} else {
			repositoryContainer.layout(22, 2);			
			repositoryContainer.setVisible(false);
		}
		
		clientWidth = Limiter.setClientWidth(editorPanel, viewPanel.getInnerWidth());
		clientHeight = Limiter.setClientHeight(editorPanel, viewPanel.getInnerHeight());
		
		if (clientWidth <= 0) {
			clientWidth = 640;
		}
		if (clientHeight <= 0) {
			clientHeight = 480;
		}		
		editorPanel.layout(clientWidth, clientHeight, displayFlags.isHideStaticFilter() ? 0 : 0);
	}
	
	private final boolean areEqual(XAxisHierarchy[] hierarchies, List<XAxisHierarchy> list) {
		boolean result = false;
		if(hierarchies.length == list.size()) {
			result = true;
			for(int i=0;i<hierarchies.length;++i) {
				if(!hierarchies[i].equals(list.get(i))) {
					result = false;
					break;
				}
			}			
		}
		return result;
	}

	public final void save(Callback<XViewModel> callback) {
		updateAndSaveView(callback);
	}
		
	private final void doSaveAfterCheck() {
		showWaitDialog(constants.savingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void none) {
				updateAndSaveView(new Callback<XViewModel>(constants.savingViewFailed()) {
					public void onFailure(Throwable caught) {
						restoreLocalFilter(view);
						view.setNeedsRestore(false);
						hideWaitCursor();
						super.onFailure(caught);
					}
					
					public void onSuccess(XViewModel view) {
						if (view == null) {
							restoreLocalFilter(CubeViewEditor.this.view);
							CubeViewEditor.this.view.setNeedsRestore(false);
							String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
							WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>(){
								public void onSuccess(Void arg0) {
									hideWaitCursor();
								}});
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToSaveView(), null);
							return;
						}
						restoreLocalFilter(view);
						view.setNeedsRestore(false);
						markDirty(false);
						reset();							
						setInput(view);
						String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
						WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>(){
							public void onSuccess(Void arg0) {
								hideWaitCursor();
							}});
					}
				});								
			}
		});		
	}
	private final void doSave() {
		if (writeRight == -1) {
			XUser usr = ((Workbench)Registry.get(Workbench.ID)).getUser();
			WPaloCubeViewServiceProvider.getInstance().isOwner(usr.getSessionId(), view.getId(), new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {
					XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
					writeRight = 0;
					if (user.hasRoleName("EDITOR")) {
						writeRight = 1;
						doSaveAfterCheck();						
					}
					if (writeRight == 0) {
						MessageBox.alert(constants.notEnoughRights(),
								constants.notEnoughRightsToSaveView(), null);
					}					
				}

				public void onSuccess(Boolean result) {
					if (result) {
						writeRight = 1;
					} else {
						XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
						writeRight = 0;
						if (user.hasRoleName("EDITOR")) {
							writeRight = 1;
							doSaveAfterCheck();						
						}
						if (writeRight == 0) {
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToSaveView(), null);
						}						
					}
				}
			});
		} else if (writeRight == 1) {
			doSaveAfterCheck();
		} else if (writeRight == 0) {
			MessageBox.alert(constants.notEnoughRights(),
					constants.notEnoughRightsToSaveView(), null);			
		}
	}
	
	private final void executePrint(XPrintConfiguration config) {
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		removeLocalFilter();
		view.setNeedsRestore(true);
		config.setMaxWidths(tableColumnsMaxWidth, tableRowsHeaderMaxWidth, tableDataCellToLongString);
		WPaloCubeViewServiceProvider.getInstance().generatePDF(sessionId, view, config, new Callback<XPrintResult>() {
			public void onSuccess(final XPrintResult result) {
				hideWaitCursor();
				if (result == null || result.getFilename() == null) {
					MessageBox.alert(constants.printError(),
							constants.errorsWhilePrinting(), null);
				} else {
					String fileName = result.getFilename(); //URL.encode(result.getFilename());

					int index = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));
					if (index != -1) {
						fileName = fileName.substring(index + 1);
					}
					fileName = URL.encode(fileName);

					final String link = GWT.getModuleBaseURL() + "downloads/" + fileName;

					WPaloPropertyServiceProvider.getInstance().getBooleanProperty("askForPDFFileDeletion", false, new Callback<Boolean>() {
						public void onSuccess(Boolean res) {
							if (res) {
								MessageBox.confirm(constants.downloadPDFHeader(),
										messages.downloadPDF(link), 
										new Listener<WindowEvent>() {
											public void handleEvent(WindowEvent be) {
												if (be.buttonClicked.getItemId().equalsIgnoreCase(Dialog.YES)) {
													WPaloCubeViewServiceProvider.getInstance().deleteFile(result.getFilename(), new Callback<Void>() {
														public void onSuccess(Void arg0) {
														}
													});
												}
											}
										});								
							} else {
								MessageBox.info(constants.downloadPDFHeader(),
										messages.downloadPDFOnlyClose(link),
										new Listener<WindowEvent>(){
											public void handleEvent(
													WindowEvent be) {
												WPaloCubeViewServiceProvider.getInstance().deleteFile(result.getFilename(), new Callback<Void>() {
													public void onSuccess(Void arg0) {
													}
												});												
											}});
							}
						}
					});
					
//					String fileName = URL.encode(result.getFilename());
//					String link = GWT.getModuleBaseURL() + "wpalo-download.srv" +
//				 		"?fileName=" + fileName + "&viewName=" + view.getName() + ".pdf";
//					DOM.setElementAttribute(RootPanel.get("wpalo_download").getElement(),
//						"src", link);
				}		
				view.setHideEmptyCells(hideEmptyCells.isPressed());
				view.setColumnsReversed(reverseColumns.isPressed());
				view.setRowsReversed(reverseRows.isPressed());
				editorPanel.hideEmptyCells(view.isHideEmptyCells());
				editorPanel.reverseColumns(view.isColumnsReversed());
				editorPanel.reverseRows(view.isRowsReversed());
				XViewModel view = result.getView();
				restoreLocalFilter(view);
				view.setNeedsRestore(false);
				reset();							
				setInput(view);				
			}
		});				
	}
	
	private final void doPrint() {
		if (view.getColumnAxis().getAxisHierarchies().isEmpty() ||
			view.getRowAxis().getAxisHierarchies().isEmpty()) {
			MessageBox.alert(constants.printError(),
					constants.cantPrintEmptyView(), null);
			return;
		}
		
		final PrintDialog pd = new PrintDialog(view);
		pd.addButtonListener(PrintDialog.BUTTON_OK,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						showWaitDialog(constants.printingView());
						final XPrintConfiguration config = pd.getPrintConfiguration();
						view.setHideEmptyCells(hideEmptyCells.isPressed());
						view.setColumnsReversed(reverseColumns.isPressed());
						view.setRowsReversed(reverseRows.isPressed());
						updateView(new Callback<Void>() {
							public void onFailure(Throwable t) {
								super.onFailure(t);
								System.err.println("Call Failed...");
								hideWaitCursor();
							}
							
							public void onSuccess(Void arg0) {
								executePrint(config);				
							}
						});						
					}
				});
		pd.show();
	}
	

	
	private final void doSaveAsAfterCheck() {
		final String[] usedNames = getViewNames();
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		int permission = user.isAdmin() ? 0 : 16;		
		WPaloCubeViewServiceProvider.getInstance().checkPermission(user.getSessionId(), permission, new AsyncCallback <Boolean>(){
			private final void showDialog(boolean showBoxes) {
				final SaveAsDialog saveAsDlg = new SaveAsDialog(view.getName(), showBoxes);
				saveAsDlg.setUsedViewNames(usedNames);
				// add close listener:
				saveAsDlg.addListener(Events.Close, new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						try {
						// which button was pressed:
						if (be.buttonClicked.getItemId().equals(SaveAsDialog.SAVE)) {

							showWaitDialog(constants.savingView());
							updateView(new Callback<Void>() {
								public void onFailure(Throwable t) {
									//Window.alert("fallito!");
									super.onFailure(t);
									restoreLocalFilter(view);
									view.setNeedsRestore(false);
								}
								public void onSuccess(Void arg0) {
									String viewName = saveAsDlg.getViewName();
									//Window.alert("ok!");
									((ViewEditorTab) tab).saveAs(viewName, view,
											saveAsDlg.isPublic(), saveAsDlg.isEditable(), new Callback<Boolean>(){
												public void onSuccess(Boolean result) {
													WPaloCubeViewServiceProvider.getInstance().getSubobjectId(new Callback<String>(){
														public void onFailure(Throwable t) {						
															super.onFailure(t);
														}
														public void onSuccess(String id) {
															 refreshSubobjects(id);
														}
													});
													if (result) {
														save.setEnabled(false);
														tab.close();
													}
												}
											});
								}
							});
						}
						} catch (Throwable t) {
							Log.error(t.getMessage(), t);
							t.printStackTrace();
						}
					}
				});
				saveAsDlg.show();						
			}
			
			public void onSuccess(Boolean result) {
				if (result) {
					showDialog(true);
				} else {
					showDialog(false);
				}
			}

			public void onFailure(Throwable arg0) {
				showDialog(false);
			}			
		});		
	}
	
	private final void doSaveAs() {
		if (writeRight == -1) {
			XUser usr = ((Workbench)Registry.get(Workbench.ID)).getUser();
			WPaloCubeViewServiceProvider.getInstance().isOwner(usr.getSessionId(), view.getId(), new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {
					XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
					writeRight = 0;
					if (user.hasRoleName("EDITOR")) {
						writeRight = 1;
						doSaveAsAfterCheck();						
					}
					if (writeRight == 0) {
						MessageBox.alert(constants.notEnoughRights(),
								constants.notEnoughRightsToSaveView(), null);
					}
				}

				public void onSuccess(Boolean result) {
					if (result) {
						writeRight = 1;
						doSaveAsAfterCheck();
					} else {
						XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
						writeRight = 0;
						if (user.hasRoleName("EDITOR")) {
							writeRight = 1;
							doSaveAsAfterCheck();						
						}
						if (writeRight == 0) {
							MessageBox.alert(constants.notEnoughRights(), constants.notEnoughRightsToSaveView(), null);
						}
					}
				}
			});
		} else if (writeRight == 1) {
			doSaveAsAfterCheck();
		} else if (writeRight == 0) {
			MessageBox.alert(constants.notEnoughRights(), constants.notEnoughRightsToSaveView(), null);
		}		
	}
	private final String[] getViewNames() {
		ViewBrowser viewBrowser = (ViewBrowser) Registry.get(ViewBrowser.ID);
		String[] names = null;
		try{
			XView[] views = viewBrowser.getViews();
			names =  new String[views.length];
			for(int i = 0; i < views.length; ++i) {
				if (views[i] == null || views[i].getName() == null) {
					names[i] = constants.deletedView(); 
				} else {
					names[i] = views[i].getName();
				}
			}
		}catch(Exception re){
			names =  new String[1];
			names[0]="";
		}
		return names;
	}
	private final void doShowAbout() {
		WPaloPropertyServiceProvider.getInstance().getBuildNumber(new AsyncCallback<String>() {
			public void onFailure(Throwable arg0) {
				ViewBrowser.displayAboutDialog("<" + constants.unknown() + ">", null);
			}

			public void onSuccess(final String result) {
				WPaloPropertyServiceProvider.getInstance().getCurrentBuildNumber(new AsyncCallback<String []>(){
					public void onFailure(Throwable arg0) {
						ViewBrowser.displayAboutDialog(result, null);
					}

					public void onSuccess(String [] buildInformation) {
						ViewBrowser.displayAboutDialog(result, buildInformation);
					}
				});						
			}
		});		
	}
	
	private final void doRefresh() {
		showWaitDialog(constants.refreshingView());
		editorPanel.initWithCurrentState();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		removeLocalFilter();
		view.setNeedsRestore(true);
		WPaloCubeViewServiceProvider.getInstance().willReload(sessionId, view,
				new Callback<XLoadInfo>(constants.refreshingViewFailed()) {
					public void onFailure(Throwable caught) {
						restoreLocalFilter(view);
						view.setNeedsRestore(false);
						hideWaitCursor();
						super.onFailure(caught);
					}
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								restoreLocalFilter(view);
								view.setNeedsRestore(false);
								hideWaitCursor();
							}
							public void proceed(boolean state) {
								proceedRefresh(loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
				});		
	}
	private final void proceedRefresh(XLoadInfo loadInfo) {
		showWaitDialog(constants.refreshingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedReload(sessionId, view, 
				new Callback<XViewModel>(constants.refreshingViewFailed()) {
					public void onFailure(Throwable t) {
						super.onFailure(t);
						restoreLocalFilter(view);
						view.setNeedsRestore(false);						
					}
					
					public void onSuccess(XViewModel view) {
						restoreLocalFilter(view);
						view.setNeedsRestore(false);
						reset();							
						setInput(view);
						markDirty(true);
						hideWaitCursor();
					}
				});
	}
	
	private final void changeSelectedElement(final HierarchySelectionWidget hierWidget, final XElement selectedElement) {
		showWaitDialog(constants.changingSelectedElement());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		removeLocalFilter();
		WPaloCubeViewServiceProvider.getInstance().willChangeSelectedElement(sessionId, 
				view, hierWidget.getHierarchy(),
				new Callback<XLoadInfo>(constants.changingSelectedElementFailed()) {
					public void onFailure(Throwable caught) {
						restoreLocalFilter(view);
						hideWaitCursor();
						super.onFailure(caught);
					}
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								restoreLocalFilter(view);
								hideWaitCursor();
							}
							public void proceed(boolean state) {
								proceedChangeSelectedElement(hierWidget,
										selectedElement, loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
		});
	}
	private final void proceedChangeSelectedElement(final HierarchySelectionWidget hierWidget,
			final XElement selectedElement, XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.changingSelectedElement());
		final XAxisHierarchy xAxisHierarchy = hierWidget.getHierarchy();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedChangeSelectedElement(sessionId,
						view, xAxisHierarchy, selectedElement,
						new Callback<XViewModel>(constants.changingSelectedElementFailed()) {
							public void onSuccess(XViewModel view) {
								restoreLocalFilter(view);
								hierWidget.setSelectedElement(selectedElement);
								editorPanel.initWithCurrentState();
								reset();							
								setInput(view);
								markDirty(true);
								hideWaitCursor();
							}
						});
	}
	private final void adjustView() {
		editorPanel.saveState(view);
		if (!displayFlags.isHideStaticFilter()) {
			if(dimRepository.isExpanded())
				setWidthOfRepositoryAxis(dimRepository.getInnerWidth());
			else
				setWidthOfRepositoryAxis(0);
		} else {
			setWidthOfRepositoryAxis(0);
		}
		adjustAxis(view.getRepositoryAxis(), getHierarchies(repositoryContainer));
		adjustAxis(view.getSelectionAxis(), editorPanel.getSelectionHierarchies());
		adjustAxis(view.getRowAxis(), editorPanel.getRowHierarchies());
		adjustAxis(view.getColumnAxis(), editorPanel.getColumnHierarchies());
	}

	private final void setWidthOfRepositoryAxis(int width) {
		if(view != null) {
			XAxis repositoryAxis = view.getRepositoryAxis();
			String _width = Integer.toString(width);
			repositoryAxis.addProperty(XAxis.PREFERRED_WIDTH, _width);
		}		
	}

	private final void updateView(Callback<Void> updateCallback) {
		adjustView();
		if (updateCallback == null) {
			updateCallback = new Callback<Void>() {
				public void onFailure(Throwable t) {
					restoreLocalFilter(view);
					view.setNeedsRestore(false);
				}
				public void onSuccess(Void arg0) {
					restoreLocalFilter(view);
					view.setNeedsRestore(false);
				}
			};
		}
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		removeLocalFilter();
		view.setNeedsRestore(true);
		WPaloCubeViewServiceProvider.getInstance().updateView(sessionId, view,
				updateCallback);
		
		
/*		WPaloCubeViewServiceProvider.getInstance().getSubobjectId(new Callback<String>(){
			public void onFailure(Throwable t) {						
				super.onFailure(t);
			}
			public void onSuccess(String id) {
				refreshSubobjects(id);
			}
		});*/
		
		
	}
	
	private final void updateAndSaveView(final Callback<XViewModel> callbackAfterSave) {
		updateView(new Callback<Void>() {
			public void onSuccess(Void arg0) {
				String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
				WPaloCubeViewServiceProvider.getInstance().saveView(sessionId,
						view, new Callback<XViewModel>(){
							public void onFailure(Throwable t) {
								restoreLocalFilter(view);
								view.setNeedsRestore(false);								
								super.onFailure(t);
								callbackAfterSave.onFailure(t);
							}
							public void onSuccess(XViewModel xViewModel) {
								restoreLocalFilter(xViewModel);
								xViewModel.setNeedsRestore(false);								
								callbackAfterSave.onSuccess(xViewModel);
							}
						});
			}
		});
	}
		
	private final void adjustAxis(XAxis axis, XAxisHierarchy[] hierarchies) {
		axis.clear();
		for(XAxisHierarchy hierarchy : hierarchies) {
			axis.add(hierarchy);
			hierarchy.setAxisId(axis.getId(), axis.getViewId());
		}
	}
	
	private final XAxisHierarchy[] getHierarchies(XObjectContainer container) {
		XObject[] xObjects = container.getXObjects();
		XAxisHierarchy[] hierarchies = new XAxisHierarchy[xObjects.length];
		for (int i = 0; i < xObjects.length; ++i)
			hierarchies[i] = (XAxisHierarchy) xObjects[i];
		return hierarchies;
	}

	private final void reset() {
		editorPanel.reset();
		repositoryContainer.reset();		
	}

	public void pressedFilter(final HierarchyWidget widget) {
		showFilterSelection(widget);
	}

	public void pressedSelectElement(HierarchySelectionWidget widget) {
		showMenu(widget);
	}
	
	private final void showMenu(final HierarchySelectionWidget widget) {		
		((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.displayingElementTree(), true);
		final XAxisHierarchy xAxisHierarchy = widget.getHierarchy();
		
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		String elementId = xAxisHierarchy.getSelectedElement() == null ? "" : xAxisHierarchy.getSelectedElement().getId();
		WPaloCubeViewServiceProvider.getInstance().getNumberOfChildren(sessionId, xAxisHierarchy.getId(), xAxisHierarchy.getViewId(), xAxisHierarchy.getAxisId(), elementId, new Callback <Integer>(){
			public void onFailure(Throwable t) {
				((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
				super.onFailure(t);
			}
			
			public void onSuccess(Integer result) {
				((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
				LargeQueryWarningDialog.confirm(result, new ConfirmLoadDialogListener() {
					public void proceed(final boolean state) {
						((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.displayingElementTree(), true);
						try {
							final SelectElementDialog menu = new SelectElementDialog(view, xAxisHierarchy, state);
							menu.addSelectionListener(new Listener<SelectionEvent>() {
								public void handleEvent(SelectionEvent se) {
									XObject selection = se.getSelection();
									XElement selectedElement = null;
									if (selection instanceof XElement) {
										selectedElement = (XElement) selection;
									} else if(selection instanceof XElementNode) {
										XElementNode node = (XElementNode) selection;
										selectedElement = node.getElement();
									}
									((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
									if (selectedElement != null
											&& !selectedElement.equals(xAxisHierarchy.getSelectedElement()))
										changeSelectedElement(widget, selectedElement);	
								}
							});
							menu.show(widget);
						    menu.focus();
							} catch (Throwable t) {
								t.printStackTrace();
							}
					}
					
					public void cancel() {
					}
				});
			}
		});
	}
	
	private final boolean contains(XElementNode [] nodes, XElement element) {
		if (nodes == null) {
			return true;
		}
		for (XElementNode node: nodes) {
			if (node.getElement().equals(element)) {
				return true;
			}
			if (contains(node.getChildren(), element)) {
				return true;
			}
		}
		return false;
	}
	
	private final void checkSubset(final XAxisHierarchy hierarchy, final XAlias oldAlias, final XSubset oldSubset, final XElement oldSelectedElement,
				final XSubset selectedSubset, final String oldPaths, final String oldAliasFormat, final XElementNode [] initialVisibleElements, final HierarchyWidget widget) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().containsElement(sessionId, hierarchy.getId(), hierarchy.getViewId(), hierarchy.getAxisId(), oldSelectedElement, selectedSubset,
			new Callback<Boolean>(){
				public void onSuccess(Boolean result) {
					if (result) {
						adjustView();
						String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
						WPaloCubeViewServiceProvider.getInstance().proceedUpdateViewWithoutTable(sessionId,
								view,
								new Callback<XViewModel>(messages.updatingViewFailed(ViewBrowserModel.modify(view.getName()))) {
									public void onSuccess(XViewModel view) {
										markDirty(true);
										fastSetInput(view);	
										if (widget instanceof HierarchySelectionWidget) {
											((HierarchySelectionWidget) widget).setSelectedElement(hierarchy.getSelectedElement());
										}
										widget.update();
										hideWaitDialog();
									}
								});														
						} else {
							willUpdateHierarchy(widget, oldSubset, oldAlias, initialVisibleElements, 
									oldPaths, oldAliasFormat, oldSelectedElement);
						}
					}
			});		
	}
		
	private final void showFilterSelection(final HierarchyWidget widget, boolean fastTreeTest) {
		final TestFastTreeDialog tftd = new TestFastTreeDialog(view, widget.getHierarchy());
		tftd.show();		
	}
	
	private final void applyFilterSettings(final FilterSelectionDialog dlg, final XElementNode [] initialVisibleElements, final HierarchyWidget widget) {
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
			public void onSuccess(Void arg0) {
				final String oldPaths = widget.getHierarchy().getProperty("filterPaths");
				final String oldAliasFormat = widget.getHierarchy().getProperty("aliasFormat");
				final XAxisHierarchy hierarchy = widget.getHierarchy();
				// get old settings:
				final XAlias oldAlias = hierarchy.getActiveAlias();
				final XSubset oldSubset = hierarchy.getActiveSubset();
				final XElement oldSelectedElement = hierarchy.getSelectedElement();							
				hierarchy.setOldVisibleElements(hierarchy.getVisibleElements());				
				if (hierarchy.getVisibleElements() == null || hierarchy.getVisibleElements().length == 0) {
					oldVisibles.remove(hierarchy.getId());
				} else {
					oldVisibles.put(hierarchy.getId(), hierarchy.getVisibleElements());
				}
				
				XElementNode [] newSelElems = dlg.getVisibleElements();
				if (newSelElems == null || newSelElems.length == 0) {
					visibles.remove(hierarchy.getId());
				} else {
					visibles.put(hierarchy.getId(), newSelElems);
				}
				
//				//apply changes:							
				hierarchy.setActiveAlias(dlg.getSelectedAlias());
				hierarchy.setActiveSubset(dlg.getSelectedSubset());
				hierarchy.setVisibleElements(newSelElems);
				hierarchy.setSelectedElement(dlg.getSelectedElement());
				final boolean containsSelected = contains(hierarchy.getVisibleElements(), oldSelectedElement);				
				if (isInRowOrColumn(hierarchy)) {
					willUpdateHierarchy(widget, oldSubset, oldAlias, initialVisibleElements, oldPaths, oldAliasFormat, oldSelectedElement);
				} else {
					if (!containsSelected || (dlg.getOldSelectedElement() == null && hierarchy.getSelectedElement() != null)) {
						willUpdateHierarchy(widget, oldSubset, oldAlias, initialVisibleElements, oldPaths, oldAliasFormat, dlg.getOldSelectedElement());
					} else {
						if (dlg.getSelectedSubset() != null) {
							checkSubset(hierarchy, oldAlias, oldSubset, oldSelectedElement,
									dlg.getSelectedSubset(), oldPaths, oldAliasFormat, initialVisibleElements, widget);
							
						} else {
							showWaitDialog(constants.applyingSelection());
							adjustView();					
							WPaloCubeViewServiceProvider.getInstance().proceedUpdateViewWithoutTable(sessionId,
								view,
								new Callback<XViewModel>(messages.updatingViewFailed(ViewBrowserModel.modify(view.getName()))) {
									public void onSuccess(XViewModel view) {
										markDirty(true);
										fastSetInput(view);
										if (widget instanceof HierarchySelectionWidget) {
											((HierarchySelectionWidget) widget).setSelectedElement(hierarchy.getSelectedElement());
										}													
										widget.update();
										hideWaitDialog();
									}
								});										
						}
					}
				}		
			}
		});
	}
	
	private final void showFilterSelection(final HierarchyWidget widget) {
		final XElementNode[] initialVisibleElements = 
			widget.getHierarchy().getVisibleElements();
		final FilterSelectionDialog dlg = 
				new FilterSelectionDialog(widget.getHierarchy(), view);

		dlg.addListener(Events.Close,
				new Listener<WindowEvent>() {
					public void handleEvent(WindowEvent be) {
						// which button was pressed:
						String buttonId = be.buttonClicked.getItemId();
						if (buttonId.equals(FilterSelectionDialog.APPLY)) {
							showWaitDialog(constants.applyingSelection());
							applyFilterSettings(dlg, initialVisibleElements, widget);
						} else if (buttonId.equals(FilterSelectionDialog.CANCEL)) {
							//we set visible elements again, because we have 
							//removed them on open!!!
							showWaitDialog(constants.updatingView());
							String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
							WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
								public void onSuccess(Void arg0) {
									XAxisHierarchy hierarchy = widget.getHierarchy();
									hierarchy.setVisibleElements(initialVisibleElements);							
									updateHierarchy(hierarchy); //AndReloadView();							
								}
							});
						}
					}
				});
		dlg.show(widget);
	}
	private final void willUpdateHierarchy(final HierarchyWidget hierWidget,
			final XSubset oldSubset, final XAlias oldAlias, 
			final XElementNode[] oldVisibleElements,
			final String oldPaths, final String oldAliasFormat, 
			final XElement oldSelectedElement) {
		showWaitDialog(constants.updatingHierarchy());
		final XAxisHierarchy hierarchy = hierWidget.getHierarchy();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().willUpdateAxisHierarchy(sessionId,
				hierarchy,
				new Callback<XLoadInfo>(constants.updatingHierarchyFailed()) {
					public void onFailure(Throwable caught) {
						hideWaitCursor();
						super.onFailure(caught);
					}
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								//we cancel update:
								hierarchy.setActiveAlias(oldAlias);
								hierarchy.setActiveSubset(oldSubset);											
								hierarchy.setVisibleElements(oldVisibleElements);
								hierarchy.setSelectedElement(oldSelectedElement);
								if (oldPaths == null) {
									hierarchy.removeProperty("filterPaths");
								} else {
									hierarchy.addProperty("filterPaths", oldPaths);
								}
								if (oldAliasFormat == null) {
									hierarchy.removeProperty("aliasFormat");
								} else {
									hierarchy.addProperty("aliasFormat", oldAliasFormat);
								}
								updateHierarchy(hierarchy);
							}
							public void proceed(boolean state) {
								proceedUpdateHierarchy(hierWidget, loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
		});

	}
	
	private final void willUpdateHierarchy(final XAxisHierarchy hierarchy,
			final XSubset oldSubset, final XAlias oldAlias, 
			final XElementNode[] oldVisibleElements,
			final String oldPaths,
			final XElement oldSelectedElement) {
		showWaitDialog(constants.updatingHierarchy());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().willUpdateAxisHierarchy(sessionId,
				hierarchy,
				new Callback<XLoadInfo>(constants.updatingHierarchyFailed()) {
					public void onFailure(Throwable caught) {
						hideWaitCursor();
						super.onFailure(caught);
					}
					public void onSuccess(final XLoadInfo loadInfo) {
						proceedUpdateHierarchy(hierarchy, loadInfo);
//						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
//							public void cancel() {
//								//we cancel update:
//								hierarchy.setActiveAlias(oldAlias);
//								hierarchy.setActiveSubset(oldSubset);											
//								hierarchy.setVisibleElements(oldVisibleElements);
//								hierarchy.setSelectedElement(oldSelectedElement);
//								if (oldPaths == null) {
//									hierarchy.removeProperty("filterPaths");
//								} else {
//									hierarchy.addProperty("filterPaths", oldPaths);
//								}
//								updateHierarchy(hierarchy);
//							}
//							public void proceed(boolean state) {
//								
//							}
//						};
//						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
		});

	}

	private final void proceedUpdateHierarchy(final XAxisHierarchy hierarchy, XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		//PR 727: we have to update and reload view completely... 
		adjustView();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().updateAndReloadView(sessionId, view, 
				new Callback<XViewModel>(constants.updatingHierarchyFailed()) {
					public void onSuccess(XViewModel xView) {
//						editorPanel.saveState(view);
						reset();							
						setInput(xView);
						markDirty(true);
						hideWaitCursor();
					}
		});
	}
	
	private final void proceedUpdateHierarchy(final HierarchyWidget hierWidget, XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		//PR 727: we have to update and reload view completely... 
		adjustView();
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().updateAndReloadView(sessionId, view, 
				new Callback<XViewModel>(constants.updatingHierarchyFailed()) {
					public void onSuccess(XViewModel xView) {
//						editorPanel.saveState(view);
						reset();							
						setInput(xView);
						markDirty(true);
						hideWaitCursor();
					}
		});
	}

	private final void updateHierarchy(final XAxisHierarchy xAxisHierarchy) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().updateAxisHierarchy(sessionId,
				xAxisHierarchy, new Callback<XElement>(constants.updatingHierarchyFailed()) {
					public void onSuccess(XElement v) {
						xAxisHierarchy.setSelectedElement(v);
						hideWaitCursor();
					}
				});
	}
	protected String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}

	public final void markDirty(final boolean doIt) {
		if (writeRight == -1 && view != null) {
			XUser usr = ((Workbench)Registry.get(Workbench.ID)).getUser();
			WPaloCubeViewServiceProvider.getInstance().isOwner(usr.getSessionId(), view.getId(), new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {					
					XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
					writeRight = 0;
					if (user.hasRoleName("EDITOR")) {
						writeRight = 1;
						isDirty = doIt;
						Widget parent = getParent();
						if(parent instanceof TabItem) {
							TabItem tab = (TabItem) parent;
							String title = doIt ? "*"+view.getName() : view.getName();							
							tab.setText(modify(title));
						}
						save.setEnabled(doIt);
					}
					if (writeRight == 0) {
						save.setEnabled(false);
					}
				}

				public void onSuccess(Boolean result) {
					if (result) {
						writeRight = 1;
						isDirty = doIt;
						Widget parent = getParent();
						if(parent instanceof TabItem) {
							TabItem tab = (TabItem) parent;
							String title = doIt ? "*"+view.getName() : view.getName();
							tab.setText(modify(title));
						}
						save.setEnabled(doIt);
					} else {
						XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
						writeRight = 0;
						if (user.hasRoleName("EDITOR")) {
							writeRight = 1;
							isDirty = doIt;
							Widget parent = getParent();
							if(parent instanceof TabItem) {
								TabItem tab = (TabItem) parent;
								String title = doIt ? "*"+view.getName() : view.getName();
								tab.setText(modify(title));
							}
							save.setEnabled(doIt);
						}
						if (writeRight == 0) {
							save.setEnabled(false);
						}
					}
				}
			});
		} else if (writeRight == 1) {
			isDirty = doIt;
			Widget parent = getParent();
			if(parent instanceof TabItem) {
				TabItem tab = (TabItem) parent;
				String title = doIt ? "*"+view.getName() : view.getName();
				tab.setText(modify(title));
			}
			save.setEnabled(doIt);
		}
	}

	public void collapse(XAxisItem item, String viewId, String axisId,
			boolean column) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().collapse(sessionId, item, viewId,
				axisId, new Callback<Void>(messages.collapseItemFailed(item.getName())) {
							public void onSuccess(Void v) {
							}
				});
	}
	
	private final void proceedWithCollapse(XAxisItem item, String viewId,
			String axisId, final boolean column, XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedCollapse(sessionId, item, viewId, axisId, 
				new Callback<Void>(messages.collapseItemFailed(item.getName())) {
					public void onSuccess(Void v) {
						editorPanel.proceedCollapse();
						markDirty(true);
						hideWaitCursor();
					}
				});
	}
	private final void cancelCollapse(XAxisItem item, String viewId, String axisId, boolean column) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().cancelCollapse(sessionId, item, viewId,
				axisId, new Callback<Void>(messages.expandingItemFailed(item.getName())) {
							public void onSuccess(Void v) {
								hideWaitCursor();
							}
				});
	}
	
	public void willCollapse(final XAxisItem item, final String viewId, final String axisId, final boolean column) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		showWaitDialog(messages.collapsingItem(item.getName()));
		WPaloCubeViewServiceProvider.getInstance().willCollapse(sessionId, item, viewId, axisId, 
				new Callback<XLoadInfo>(messages.collapseItemFailed(item.getName())) {
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								cancelCollapse(item, viewId, axisId, column);
							}
							public void proceed(boolean state) {
								proceedWithCollapse(item, viewId, axisId, column, loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
				});		
	}
	
	public void willExpand(final XAxisItem item, final String viewId, final String axisId,
			final boolean column) {		
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		showWaitDialog(messages.expandingItem(item.getName()));
		WPaloCubeViewServiceProvider.getInstance().willExpand(sessionId, item, viewId, axisId, 
				new Callback<XLoadInfo>(messages.expandingItemFailed(item.getName())) {
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								cancelExpand(item, viewId, axisId, column);
							}
							public void proceed(boolean state) {
								proceedWithExpand(item, viewId, axisId, column, loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
				});
	}
	private final void proceedWithExpand(XAxisItem item, String viewId,
			String axisId, final boolean column, XLoadInfo loadInfo) {
//		if(loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedExpand(sessionId, item, viewId, axisId, 
				new Callback<XDelta>(messages.expandingItemFailed(item.getName())) {
					public void onSuccess(XDelta delta) {
						editorPanel.insert(delta, column);
						markDirty(true);
						hideWaitCursor();
					}
				});
	}
	private final void cancelExpand(XAxisItem item, String viewId, String axisId, boolean column) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().cancelExpand(sessionId, item, viewId,
				axisId, new Callback<Void>(messages.collapseItemFailed(item.getName())) {
							public void onSuccess(Void v) {
								hideWaitCursor();
							}
				});
	}

	public void setExpandState(final XAxisItem[] expanded, final XAxisItem[] collapsed,
			int expandDepth, final String viewId, String axisId, final boolean column) {
		showWaitDialog(constants.expandingLevel());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().willSetExpandState(sessionId,				
				expanded, collapsed, expandDepth, viewId, axisId,
				new Callback<XLoadInfo>(constants.settingExpandStateFailed()) {
					public void onSuccess(final XLoadInfo loadInfo) {
						ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
							public void cancel() {
								cancelSetExpandState(viewId);
							}
							public void proceed(boolean state) {
								proceedSetExpandState(viewId, column, loadInfo);
							}
						};
						LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
					}
				});
	}

	private final void proceedSetExpandState(String viewId,
			final boolean column, XLoadInfo loadInfo) {
//		if (loadInfo.loadCells > SHOW_WAIT_CURSOR_THRESHOLD)
		showWaitDialog(constants.updatingView());
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().proceedSetExpandState(sessionId, 
				viewId, new Callback<XDelta[]>(constants.settingExpandStateFailed()) {
					public void onSuccess(XDelta[] deltas) {
						editorPanel.insert(deltas, column);
						markDirty(true);
						hideWaitCursor();
					}
				});
	}
	private final void cancelSetExpandState(String viewId) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().cancelSetExpandState(sessionId, 
				viewId, 
				new Callback<Void>(constants.settingExpandStateFailed()) {
					public void onSuccess(Void v) {
						hideWaitCursor();
					}
				});
	}
	
	final void checkWritePermission(AsyncCallback<Boolean> cb) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().checkPermission(sessionId, view.getId(),
				RIGHT_WRITE, cb);
	}
	
	public boolean layout() {
		if (setupHandler != null) {
			setupHandler.checkExpand();
		}
		return super.layout();
	}

	private final void hideElement(final XAxisItem item, final List <XAxisItem> roots, String viewId, String axisId, boolean column, boolean hideLevel) {
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		if (hideLevel) {
			showWaitDialog(constants.hideLevel());
		} else {
			showWaitDialog(messages.hideElement(item.getName()));
		}
		WPaloCubeViewServiceProvider.getInstance().hideItem(sessionId, item, roots, viewId, axisId, column, hideLevel, new AsyncCallback <String []>(){
			public void onFailure(Throwable t) {
				System.err.println("Failed.");
				t.printStackTrace();
				hideWaitDialog();
			}

			public void onSuccess(final String [] result) {
				WPaloCubeViewServiceProvider.getInstance().runAsync(sessionId, 0, new Callback<Void>() {
					public void onSuccess(Void arg0) {
						XAxisHierarchy hierarchy = view.getAxisHierarchy(item.getHierarchyId());
						final String oldPaths = hierarchy.getProperty("filterPaths");
						final String oldAliasFormat = hierarchy.getProperty("aliasFormat");

						// get old settings:
						final XAlias oldAlias = hierarchy.getActiveAlias();
						final XSubset oldSubset = hierarchy.getActiveSubset();
						final XElement oldSelectedElement = hierarchy.getSelectedElement();						
						ArrayList <XElementNode> visEls = new ArrayList<XElementNode>();
						final XElementNode [] currentVisible = hierarchy.getVisibleElements();
						hierarchy.setOldVisibleElements(currentVisible);
						
//						//apply changes:
						if (result != null) {
							StringBuffer filterPaths = new StringBuffer();
							HashMap <String, XElementNode> parents = new HashMap<String, XElementNode>();
							int counter = 0;
							for (int i = 0; i < result.length; i += 5) {
								XElement xElement = new XElement(result[i], result[i + 1],
										XElementType.fromString(result[i + 2]));
								XElementNode xElemNode = new XElementNode(xElement, item.getHierarchyId(), view.getId());
								filterPaths.append(result[i + 3]);
								filterPaths.append(",");
								if (!result[i + 4].equals("-1")) {
									XElementNode xParent = parents.get(result[i + 4]);
									xParent.forceAddChild(xElemNode);
									xElemNode.setParent(xParent);
								} else {
									visEls.add(xElemNode);
								}
								parents.put("" + counter, xElemNode);
								counter++;																							
							}
							hierarchy.addProperty("filterPaths", filterPaths.toString());
						}
						hierarchy.setVisibleElements(visEls.toArray(new XElementNode[0]));
						willUpdateHierarchy(
								hierarchy, oldSubset, oldAlias, currentVisible, oldPaths, oldSelectedElement);
					}
				});
			}
		});		
	}
	
	private final boolean isHideAllowed(XAxisItem item, List <XAxisItem> siblings, boolean isLevel) {
		if (siblings == null || siblings.isEmpty()) {
			return item.isExpanded && item.hasChildren();
		}
		if (isLevel) {
			for (XAxisItem i: siblings) {
				if (i.isExpanded && i.hasChildren()) {
					return true;
				}
			}
			return false;
		} else {
			return siblings.size() > 1 || (item.isExpanded && item.hasChildren());
		}
	}
	
	public void leftClicked(final XAxisItem item, final List <XAxisItem> roots, final String viewId, final String axisId,
			final boolean column, int x, int y) {
		Menu contextMenu = new Menu();
		contextMenu.setWidth(140);
		
		MenuItem hideElement = new MenuItem();
		String name = item.getName();
		if (name == null) {
			name = constants.element();
		}
		if (name.length() > 15) {
			name = name.substring(0, 12) + "...";
		}
		hideElement.setText(messages.hideElement(name));
		hideElement.setIconStyle("icon-filter-remove");
		hideElement.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (item.depth != 0 || isHideAllowed(item, roots, false)) {
					hideElement(item, null, viewId, axisId, column, false);
				} else {
					MessageBox.info(constants.information(),
							constants.hideForbiddenLastElement(), null);
				}
			}
		});
		contextMenu.add(hideElement);
		
		MenuItem hideLevel = new MenuItem();
		hideLevel.setText(constants.hideLevel());
		hideLevel.setIconStyle("icon-filter-remove-level");
		hideLevel.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (item.depth == 0) {
					if (isHideAllowed(item, roots, true)) {
						hideElement(item, roots, viewId, axisId, column, true);
					} else {
						MessageBox.info(constants.information(),
								constants.hideForbidden(), null);						
					}
				} else {
					hideElement(item, null, viewId, axisId, column, true);
				}
			}
		});
		contextMenu.add(hideLevel);

		XAxisHierarchy hierarchy = view.getAxisHierarchy(item.getHierarchyId());
		boolean enabled = hierarchy != null && hierarchy.getVisibleElements() != null && hierarchy.getVisibleElements().length > 0;
		MenuItem showAllElements = new MenuItem();
		showAllElements.setText(constants.showAllElements());
		showAllElements.setIconStyle("icon-filter");
		showAllElements.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				XAxisHierarchy hierarchy = view.getAxisHierarchy(item.getHierarchyId());
				final String oldPaths = hierarchy.getProperty("filterPaths");

				// get old settings:
				final XAlias oldAlias = hierarchy.getActiveAlias();
				final XSubset oldSubset = hierarchy.getActiveSubset();
				final XElement oldSelectedElement = hierarchy.getSelectedElement();						
				ArrayList <XElementNode> visEls = new ArrayList<XElementNode>();
				final XElementNode [] currentVisible = hierarchy.getVisibleElements();
				hierarchy.setOldVisibleElements(currentVisible);				
				hierarchy.setVisibleElements(null);
				willUpdateHierarchy(
						hierarchy, oldSubset, oldAlias, currentVisible, oldPaths, oldSelectedElement);
			}
		});
		contextMenu.add(showAllElements);
		showAllElements.setEnabled(enabled);
		
		contextMenu.showAt(x, y);
	}

	public void rightClicked(XAxisItem item, List <XAxisItem> roots, String viewId, String axisId,
			boolean column, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public void onClick(ClickEvent ignored) {
		showWaitDialog(constants.updatingView());
		final String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		WPaloCubeViewServiceProvider.getInstance().willSwapAxes(sessionId, view.getId(), new Callback<XLoadInfo>() {
			public void onSuccess(XLoadInfo loadInfo) {
				ConfirmLoadDialogListener dlgListener = new ConfirmLoadDialogListener() {
					public void cancel() {
						hideWaitCursor();
					}
					public void proceed(boolean state) {
						removeLocalFilter();
						view.setNeedsRestore(true);
						WPaloCubeViewServiceProvider.getInstance().proceedSwapAxes(sessionId, view, new Callback<XViewModel>() {
							public void onSuccess(XViewModel view) {
								restoreLocalFilter(view);
								view.setNeedsRestore(false);								
								reset();							
								setInput(view);
								markDirty(true);
								hideWaitCursor();
							}
						});
					}
				};
				LargeQueryWarningDialog.confirm(loadInfo, dlgListener);
			}
		});

	}		
}

class SetupHandler implements Listener<BaseEvent> {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private final BorderLayout layout;
	private final Component managedComponent;
	private final CubeViewEditor editor;
	private final LayoutRegion region;
	private boolean initiallyExpand = false;
	private boolean setupDone = false;
	private int lastWidth = 0;
	
	SetupHandler(CubeViewEditor editor, LayoutRegion region) {
		this.editor = editor;
		this.region = region;
		this.layout = (BorderLayout)editor.getLayout();
		this.managedComponent = getRegionWidget(region);
		if (managedComponent != null) {
			//managedComponent.setVisible(!editor.displayFlags.isHideStaticFilter());
			if (editor.displayFlags.isHideStaticFilter()) {
				managedComponent.hide();
			}
		}
		layout.addListener(Events.AfterLayout, this);
	}
	

	void setupDone() {
		layout.removeListener(Events.AfterLayout, this);
		managedComponent.addListener(Events.Resize,
				new Listener<BoxComponentEvent>() {
					public void handleEvent(BoxComponentEvent be) {
						if (setupDone) {
							int variance = Math.abs(be.width - lastWidth);
							if (variance > 3)
								editor.markDirty(true);
						}
						lastWidth = be.width;
					}
				});
		setupDone = true;
	}

	void checkExpand() {
		if(setupDone && isExpand()) {
			//expand only if we have enough permission:
			expandOnPermission();
		}
	}
	private final boolean isExpand() {
		Map<String, Object> state = managedComponent.getState();
		return !state.containsKey("collapsed") && !editor.displayFlags.isHideStaticFilter();
	}
	private final void expandOnPermission() {
		if (editor.displayFlags.isHideStaticFilter()) {
			return;
		}
		if (initiallyExpand) //we are allow to expand if it was initially expanded...
			expand(true);
		else { //check write permission...
			int writePermission = editor.getWritePermission();
			if (writePermission == -1) {
				editor.checkWritePermission(new Callback<Boolean>() {
					public void onSuccess(Boolean granted) {
						if (!granted) {
							MessageBox.alert(constants.notEnoughRights(),
									constants.notEnoughRightsToModifyStaticFilters(), null);
						}
						expand(granted);
					}
				});
			} else {
				if (writePermission != 1) {
					MessageBox.alert(constants.notEnoughRights(),
							constants.notEnoughRightsToModifyStaticFilters(), null);					
				}
				expand(writePermission == 1);		
			}		
		}
	}
	private void expand(boolean doIt) {		
		if(doIt)
			layout.expand(region);
		else
			layout.collapse(region);
	}


	
	private Component getRegionWidget(LayoutRegion region) {
		for (int i = 0, n = editor.getItemCount(); i < n; ++i) {
			Component c = editor.getItem(i);
			Object data = c.getData("layout_region");
			if (data != null && data.equals(region))
				return c;
		}
		return null;
	}

	/** called from editor whenever a region is expanded or collapsed */
	void regionExpanded() {
		if(setupDone) {
			editor.markDirty(true);
		}
	}
	
	void setupWidth(int width) {
		if(setupDone)
			return;
		lastWidth = width;
		initiallyExpand = width > 0 ? true : false;
		expand(initiallyExpand && !editor.displayFlags.isHideStaticFilter());
		if(initiallyExpand && !editor.displayFlags.isHideStaticFilter()) {
			BorderLayoutData layoutData = (BorderLayoutData) 
						ComponentHelper.getLayoutData(managedComponent);
			layoutData.setSize(width);
			editor.checkDnDState();
		}
	}
	
	public void handleEvent(BaseEvent be) {
		if (be.type == Events.AfterLayout) {
			if(!setupDone)
				setup();
		}
	}
	
	private final void setup() {
		if(initiallyExpand) {
			layout.expand(region);
			editor.checkDnDState();
		} else {
			layout.collapse(region);
		}
	}
	
}