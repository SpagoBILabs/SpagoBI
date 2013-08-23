/*
*
* @file ViewEditorPanel.java
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
* @version $Id: ViewEditorPanel.java,v 1.51 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.cubevieweditor;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerListener;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerWidget;
import com.tensegrity.palo.gwt.widgets.client.container.ScrollableContainer;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;
import com.tensegrity.palo.gwt.widgets.client.dnd.PickupDragController;
import com.tensegrity.palo.gwt.widgets.client.i18n.ILocalConstants;
import com.tensegrity.palo.gwt.widgets.client.i18n.Resources;
import com.tensegrity.palo.gwt.widgets.client.palotable.CellChangedListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.ExpandListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.ItemClickListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.ItemExpandListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.PaloTable;
import com.tensegrity.palo.gwt.widgets.client.separator.HorizontalSeparator;
import com.tensegrity.palo.gwt.widgets.client.util.Limiter;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>EditorPanel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewEditorPanel.java,v 1.51 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
public class ViewEditorPanel extends AbsolutePanel implements ContainerListener {
	protected final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String STYLE = "palo-gwt-vieweditor";	
	private static final String STYLE_FILTER_LABEL ="filter-label";
	private static final String STYLE_ROW_CONTAINER = "container-row";
	private static final String STYLE_COLUMN_CONTAINER = "container-column";
	private static final String STYLE_SELECTION_CONTAINER = "container-selection";
	private static final String STYLE_SEPARATOR = "separator";
	
	//our gui parts:
	private final Label lblFilter = new Label(constants.filter());
	private final Button swapAxes = new Button();
	private final HorizontalSeparator separator = new HorizontalSeparator();
	private final ScrollableContainer selectionContainer; 
	private final ScrollableContainer rowContainer;	
	private final ScrollableContainer columnContainer;
	private final PaloTable paloTable = new PaloTable();
	private final boolean hidePov;
	private final boolean hideRows;
	private final boolean hideCols;
	private XViewModel view;
	
	private final ArrayList <ClickHandler> listeners = new ArrayList<ClickHandler>();
	
	public ViewEditorPanel(HierarchyWidgetListener widgetListener, boolean hidePov, boolean hideCols, boolean hideRows) {		
		this.hidePov = hidePov;
		this.hideRows = hideRows;
		this.hideCols = hideCols;
		swapAxes.setIconStyle("icon-swap");
//		swapAxes.setTitle(constants.swapAxes());
		swapAxes.setToolTip(constants.swapAxes());
		selectionContainer = new ScrollableContainer(new SelectionContainerRenderer(widgetListener));
		rowContainer = new ScrollableContainer(new RowContainerRenderer(widgetListener));		
		columnContainer = new ScrollableContainer(new ColumnContainerRenderer(widgetListener));
		initComponents(hidePov, hideCols, hideRows);
		initEventhandling();		
	}

	public void addClickListener(ClickHandler handler) {
		listeners.add(handler);
	}
	
	public void setInput(XViewModel xView) {
		view = xView;
		//fill containers:
		//pov:
		List<XAxisHierarchy> hierarchies = xView.getSelectionAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			selectionContainer.add(hierarchy);
		//rows:
		hierarchies = xView.getRowAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			rowContainer.add(hierarchy);
		//cols:
		hierarchies = xView.getColumnAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			columnContainer.add(hierarchy);
		
		paloTable.setInput(view);
		paloTable.layout();
	}

	public void fastSetInput(XViewModel xView) {
		view = xView;
		//fill containers:
		//pov:
		List<XAxisHierarchy> hierarchies = xView.getSelectionAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			selectionContainer.add(hierarchy);
		//rows:
		hierarchies = xView.getRowAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			rowContainer.add(hierarchy);		
		//cols:
		hierarchies = xView.getColumnAxis().getAxisHierarchies();
		for(XAxisHierarchy hierarchy : hierarchies)
			columnContainer.add(hierarchy);
	}

	public final void initRowContainer(XAxis xAxis) {
		rowContainer.removeAll();
		for(XAxisHierarchy hierarchy : xAxis.getAxisHierarchies())
			rowContainer.add(hierarchy);

	}
	public final void initColumnContainer(XAxis xAxis) {
		columnContainer.removeAll();
		for(XAxisHierarchy hierarchy : xAxis.getAxisHierarchies())
			columnContainer.add(hierarchy);
	}

	public final void initSelectionContainer(XAxis xAxis) {
		selectionContainer.removeAll();
		for(XAxisHierarchy hierarchy : xAxis.getAxisHierarchies())
			selectionContainer.add(hierarchy);
	}
	public final PaloTable getTable() {
		return paloTable;
	}
	
	public final void markRuleBasedCells(boolean doIt) {
		paloTable.markRuleBasedCells(doIt);
		view.setShowRules(doIt);
	}
	public final void hideEmptyCells(boolean doIt) {
		paloTable.hideEmptyCells(doIt);
		if (view != null) {
			view.setHideEmptyCells(doIt);
		}
	}
	public final int previewHideEmptyCells(boolean doIt) {
		return paloTable.previewHideEmptyCells(doIt);
	}
	public final boolean isHideEmptyCells() {
		return view.isHideEmptyCells();
	}
	
	public final void reverseRows(boolean doIt) {
		paloTable.reverseRows(doIt);
		if (view != null) {
			view.setRowsReversed(doIt);
		}
	}
	public final void reverseColumns(boolean doIt) {
		paloTable.reverseColumns(doIt);
		if (view != null) {
			view.setColumnsReversed(doIt);
		}
	}

	public final void addCellChangedListener(CellChangedListener listener) {
		paloTable.addCellChangedListener(listener);
	}
	public final void addExpandListener(ExpandListener listener) {
		paloTable.addExpandListener(listener);
	}
	
	public final XAxisHierarchy[] getRowHierarchies() {
		return getHierarchies(rowContainer);
	}
	public final XAxisHierarchy[] getColumnHierarchies() {
		return getHierarchies(columnContainer);
	}
	public final XAxisHierarchy[] getSelectionHierarchies() {
		return getHierarchies(selectionContainer);
	}
	
	public final ScrollableContainer getSelectionContainer() {
		return selectionContainer;
	}
	
	public final void addContainerListener(ContainerListener listener) {
		rowContainer.addContainerListener(listener);
		columnContainer.addContainerListener(listener);
		selectionContainer.addContainerListener(listener);
	}
	
//	public final void register(ItemLoader itemLoader) {
//		paloTable.register(itemLoader);
//	}

	public final void register(ItemClickListener clickListener) {
		paloTable.register(clickListener);
	}
	
	public final void register(ItemExpandListener expandListener) {
		paloTable.register(expandListener);
	}

	public final void insert(XDelta delta, boolean columns) {
		paloTable.insert(delta, columns);
	}
	public final void proceedCollapse() {
		paloTable.proceedCollapse();
	}
	public final void insert(XDelta[] deltas, boolean columns) {
		paloTable.insert(deltas, columns);
	}
	
	public final void setCells(XCellCollection cells, XViewModel view) {
		paloTable.setCells(cells, view);
	}
	
	public final void saveState(XViewModel view) {
		paloTable.saveState(view);
	}
	
	public final void register(PickupDragController dragController) {
		rowContainer.register(dragController);
		columnContainer.register(dragController);
		selectionContainer.register(dragController);
	}
	
	public void fastLayout(int clientWidth, int clientHeight, int indent) {
		int INDENT = indent;
		int width = clientWidth > 0 ? clientWidth : getOffsetWidth();
		int height = clientHeight > 0 ? clientHeight : getOffsetHeight();
		
		if (width <= 0 || height <= 0) {
			System.err.println("Width <= 0 || height <= 0 ViewEditorPanel");
		}
		
		int lblOffX = lblFilter.getOffsetWidth();
		int lblOffY = lblFilter.getOffsetHeight();
		
		int gap = 2 * INDENT;
		int selWidth = width - lblOffX - INDENT - gap;
		
		//selection container:
		int inner = Limiter.setClientWidth(selectionContainer, selWidth);
		Point selSize = selectionContainer.layout(inner, 0);
		Limiter.setClientHeight(selectionContainer, selSize.y);
						
		//finally we arrange parts:
		int y = INDENT;
		setWidgetPosition(lblFilter, INDENT + 10, INDENT + (selSize.y - lblOffY)/2);
		setWidgetPosition(selectionContainer, INDENT + lblOffX + INDENT, y);
		y = selSize.y + gap;
		separator.setWidth((width - INDENT) + "px");
		setWidgetPosition(separator, INDENT/2, y);
	}
	
	public synchronized void layout(int clientWidth, int clientHeight, int indent) {	
		int INDENT = indent;
		int width = clientWidth > 0 ? clientWidth : getOffsetWidth();
		int height = clientHeight > 0 ? clientHeight : getOffsetHeight();

		if (width <= 0 || height <= 0) {
			System.err.println("Width <= 0 || height <= 0 ViewEditorPanel");
		}
		
		int lblOffX = lblFilter.getOffsetWidth();
		int lblOffY = lblFilter.getOffsetHeight();
		
		int gap = 2 * INDENT;
		int selWidth = width - lblOffX - INDENT - gap;
		
		//selection container:
		int inner = Limiter.setClientWidth(selectionContainer, selWidth);
		Point selSize = selectionContainer.layout(inner, 0);
		Limiter.setClientHeight(selectionContainer, selSize.y);
		
		//row & column container:
		int tableWidth = width;
		tableWidth -= gap;
		if (!hideRows) {			
			tableWidth -= rowContainer.getMinWidth();
		}
		int tableHeight = height;				
		if (!hidePov) {			
			tableHeight -= selSize.y;
			tableHeight -= gap;
		}
		
		if (hidePov && hideCols)
			tableHeight -= gap;
		
		if (!hideCols) {
			tableHeight -= columnContainer.getMinHeight();
			tableHeight -= gap;
		}
				
		Limiter.setClientWidth(rowContainer, rowContainer.getMinWidth());
		inner = Limiter.setClientHeight(rowContainer, tableHeight);
		Point rowSize = rowContainer.layout(0, inner);
		inner = Limiter.setClientWidth(columnContainer, tableWidth);
		Point colSize = columnContainer.layout(inner, 0);
		Limiter.setClientHeight(columnContainer, colSize.y);

		// palo table:
//		if (tableHeight > 20) {
//			if (UserAgent.getInstance().isIE) {
//				tableHeight -= 18;
//			} else {
//				tableHeight -= 26;
//			}
//		}
		
		paloTable.setSize(tableWidth, tableHeight);
//		Limiter.setClientWidth(paloTable, tableWidth);
//		Limiter.setClientHeight(paloTable, tableHeight);
//		paloTable.layout();
		
		
		//finally we arrange parts:
		int y = INDENT;
		setWidgetPosition(lblFilter, INDENT + 10, INDENT + (selSize.y - lblOffY)/2);
		setWidgetPosition(selectionContainer, INDENT + lblOffX + INDENT, y);
		y = selSize.y + gap;
		separator.setWidth((width - INDENT) + "px");
		setWidgetPosition(separator, INDENT/2, y);		
		y += separator.getHeight();
		y += INDENT;
		if (hidePov) {
			y -= selSize.y;
			y -= gap;
		}
		setWidgetPosition(columnContainer, INDENT + rowSize.x - 1, y);
		setWidgetPosition(swapAxes, INDENT + 9, y + 9);
		if (hidePov) {
			y += selSize.y;
			y += gap;
		}
		y += colSize.y;
		setWidgetPosition(rowContainer, INDENT, y - 1);		
		int yPos = y - 1;
		if (hidePov || hideCols) {
			yPos -= gap;
		}
		setWidgetPosition(paloTable, INDENT + rowSize.x - 1, yPos);
	}

	public final void reset() {
		rowContainer.reset();
		columnContainer.reset();
		selectionContainer.reset();
		paloTable.reset();		
	}
		
	public final void initWithCurrentState() {
		paloTable.initWithCurrentState();
	}
	
	public void removed(ContainerWidget widget) {
		updateScrollButtons(widget.getContainer());
	}
	public void dropped(ContainerWidget widget, int atIndex) {
		updateScrollButtons(widget.getContainer());
	}

	private final void fireClickEvent() {
		for (ClickHandler listener: listeners) {
			listener.onClick(null);
		}
	}
	
	private final void initComponents(boolean hidePov, boolean hideCols, boolean hideRows) {
		add(lblFilter);
		add(selectionContainer);
		add(separator);
		if (hidePov) {
			lblFilter.setVisible(false);
			selectionContainer.setVisible(false);
			separator.setVisible(false);
		}
		add(rowContainer);
		if (hideRows) {
			rowContainer.setVisible(false);
		}
		add(columnContainer);
		if (hideCols) {
			columnContainer.setVisible(false);
		}
		
		swapAxes.setSize("22px", "22px");
		add(swapAxes);
		
		if (hideRows || hideCols) {
			swapAxes.setVisible(false);
		} else {
			swapAxes.addSelectionListener(new SelectionListener<ComponentEvent>() {
				public void componentSelected(ComponentEvent ce) {
					fireClickEvent();
				}
			});
		}
		add(paloTable);		
		
		//selection container:
		selectionContainer.setEmptyLabel(constants.dropToFillFilters());
//				"Drop to fill filters");				
//		rowContainer.setEmptyLabel("Drop to fill vertical axis");
		Image emptyLabelImg = new Image(constants.dropToFillVerticalAxis());//, 0, 0, 15, 218));
		emptyLabelImg.setPixelSize(15, 218); //PR 690: specifing img bounds in constructor leads to clipped image which in turn leads to border...
		rowContainer.setEmptyLabel(emptyLabelImg);//, 0, 0, 15, 218));
		columnContainer.setEmptyLabel(constants.dropToFillHorizontalAxis());
//				"Drop to fill horizontal axis");
		
		//set styles:
		setStyleName(STYLE);
		lblFilter.setStyleName(STYLE_FILTER_LABEL);
		rowContainer.setStyleName(STYLE_ROW_CONTAINER);
		columnContainer.setStyleName(STYLE_COLUMN_CONTAINER);
		selectionContainer.setStyleName(STYLE_SELECTION_CONTAINER);
		separator.setStyleName(STYLE_SEPARATOR);
	}
	
	private final void initEventhandling() {
		selectionContainer.addContainerListener(this);
		rowContainer.addContainerListener(this);
		columnContainer.addContainerListener(this);
	}

	private final XAxisHierarchy[] getHierarchies(XObjectContainer container) {
		XObject[] xObjects = container.getXObjects();
		XAxisHierarchy[] hierarchies = new XAxisHierarchy[xObjects.length];
		for (int i = 0; i < xObjects.length; ++i)
			hierarchies[i] = (XAxisHierarchy) xObjects[i];
		return hierarchies;
	}
	
	//TODO this should definitely go to ScrollableContainer!!!
	private final void updateScrollButtons(XObjectContainer container) {
		if(container instanceof ScrollableContainer)
			((ScrollableContainer) container).updateScrollButtons();
	}
}
