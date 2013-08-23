/*
*
* @file CubeViewController.java
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
* @version $Id: CubeViewController.java,v 1.81 2010/04/15 09:54:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;


import org.palo.api.Attribute;
import org.palo.api.Cell;
import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;
import org.palo.viewapi.View;
import org.palo.viewapi.VirtualElement;
import org.palo.viewapi.exporters.PaloCSVExporter;
import org.palo.viewapi.exporters.PaloHTMLExporter;
import org.palo.viewapi.exporters.PaloPDFExporter;
import org.palo.viewapi.exporters.PaloPDFExporterConfiguration;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.ServerConnectionPool;
import org.palo.viewapi.internal.ViewImpl;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;

import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.CubeViewConverter;
import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.FormatConverter;
import com.tensegrity.palo.gwt.core.server.services.cubeview.util.ViewModelUtils;


/**
 * <code>CubeViewController</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewController.java,v 1.81 2010/04/15 09:54:49 PhilippBouillon Exp $
 **/
public class CubeViewController {

	private static final Map<String, Map<String,CubeViewController>> session2controller = new HashMap<String, Map<String,CubeViewController>>();
	private static final String SPAGOBI_STATE ="spagobi_state";
	
	public ViewModelController getViewModelController() {
		return viewModelController;
	}
	
	public static final synchronized CubeViewController getController(AuthUser user, String sessionId, View forView, HttpSession session) 
		throws PaloGwtCoreException {
		if(sessionId == null || forView == null)
			return null;

		Map<String, CubeViewController> viewControllers = 
			getOrCreateViewControllersMap(sessionId);
		String viewId = forView.getId();
		CubeViewController controller = viewControllers.get(viewId);
		System.out.println("controller:: "+controller);
		String spagoBIState = (String)session.getAttribute(SPAGOBI_STATE);
		System.out.println("getController .. spagoBIState:: "+spagoBIState);
		if (controller == null) {
			try {			
				if(spagoBIState != null && !spagoBIState.equals("")){
					ViewService viewService = ServiceProvider.getViewService(user);
					viewService.setDefinition(spagoBIState, forView);
				}
				CubeView cv = forView.createCubeView(user, sessionId);
				controller = new CubeViewController(forView, cv);
				viewControllers.put(viewId, controller);
			} catch (PaloIOException e) {
				String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
				Throwable cause = e.getCause() == null ? e : e.getCause();
				throw new PaloGwtCoreException(msg, cause);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(forView.getAccount(), sessionId, "CubeViewController.getController");
			}
		}
		return controller;
	}
	
//	public static final CubeViewController getController(UserSession userSession, View forView) {
//		if(userSession == null || forView == null)
//			return null;
//
//		Map<String, CubeViewController> viewControllers = 
//			getOrCreateViewControllersMap(userSession);
//		
//		String viewId = forView.getId();
//		CubeViewController controller = viewControllers.get(viewId);
//		if (controller == null) {
//			try {
//				controller = new CubeViewController(forView, forView
//					.createCubeView(userSession.getId()));
//				viewControllers.put(viewId, controller);
//			} finally {
//				ConnectionPoolManager.getInstance().disconnect(forView.getAccount(), userSession.getId());
//			}
//		}
//		return controller;
//	}
		
//	private static Map<String, CubeViewController> getOrCreateViewControllersMap(
//			UserSession userSession) {
//		Map<String, CubeViewController> viewControllers = 
//				session2controller.get(userSession.getId());
//		if (viewControllers == null) {
//			viewControllers = new HashMap<String, CubeViewController>();
//			session2controller.put(userSession.getId(), viewControllers);
//		}
//		return viewControllers;
//	}
	
	private static Map<String, CubeViewController> getOrCreateViewControllersMap(
			String userSession) {
		Map<String, CubeViewController> viewControllers = 
				session2controller.get(userSession);
		if (viewControllers == null) {
			viewControllers = new HashMap<String, CubeViewController>();
			session2controller.put(userSession, viewControllers);
		}
		return viewControllers;
	}

//	public static final CubeViewController getController(UserSession userSession, String forView) {
//		Map<String, CubeViewController> viewControllers = getOrCreateViewControllersMap(userSession);
//		return viewControllers.get(forView);
//	}
	
	public static final CubeViewController getController(String sessionId, String forView) {
		Map<String, CubeViewController> viewControllers = getOrCreateViewControllersMap(sessionId);
		if (forView == null) {
//			Thread.dumpStack();
			return null;
		}
		return viewControllers.get(forView);
	}
	
	public static final void removeControllerFor(String sessionId, String forView) {
		Map<String, CubeViewController> viewControllers = getOrCreateViewControllersMap(sessionId);
		viewControllers.remove(forView);
	}
	
//	public static final View getViewById(UserSession userSession, String id) {
//		CubeViewController controller = getController(userSession, id);
//		if(controller != null)
//			return controller.getView();
//		return null;
//	}
	public static final View getViewById(String userSession, String id) {
		CubeViewController controller = getController(userSession, id);
		if(controller != null)
			return controller.getView();
		return null;
	}
	
	public static final void removeAllViews(UserSession userSession) {
		Map<String, CubeViewController> viewControllers = 
				session2controller.get(userSession.getSessionId());
		if (viewControllers != null)
			viewControllers.clear();
	}
	public static final void removeAllViewsAndShutdownConnectionPool(UserSession userSession) {
		Map<String, CubeViewController> viewControllers = 
			session2controller.get(userSession.getSessionId());
		if (viewControllers != null) {
			HashSet <Account> accounts = new HashSet<Account>();
			for (CubeViewController c: viewControllers.values()) {
				if (c.getView() != null && c.getView().getAccount() != null) {
					accounts.add(c.getView().getAccount());
				}
			}			
			session2controller.remove(userSession.getSessionId());
			for (Account account: accounts) {
				ConnectionPoolManager.getInstance().getPool(account, userSession.getSessionId()).disconnectAll();
			}
			if (accounts.isEmpty()) {
				if (userSession.getUser() != null) {
					for (Account account: userSession.getUser().getAccounts()) {
						if (account != null) {
							ServerConnectionPool pool = ConnectionPoolManager.getInstance().onlyGetPool(account, userSession.getSessionId());
							if (pool != null) {
								pool.disconnectAll();
							}
						}
					}
				}
			}
		}		
	}
	
	public static final void removeView(UserSession userSession, View view) {
		removeViewById(userSession, view.getId());
	}
	public static final void removeViewById(UserSession userSession, String viewId) {
		Map<String, CubeViewController> viewControllers = 
				session2controller.get(userSession.getSessionId());
		if(viewControllers != null)
			viewControllers.remove(viewId);
	}
	
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	/** the {@link CubeView} to display */
//	private CubeView cubeView;
	private final View view;
	private final ViewModelController viewModelController;
	private final Set<Integer> notEmptyRows = new HashSet<Integer>();
	private final Set<Integer> notEmptyColumns = new HashSet<Integer>();
	private Map<Hierarchy, Integer> hierarchyIndex;
	public final Map<String, ElementNode> elementNodes = new HashMap<String, ElementNode>();
	
	private CubeViewController(View view, CubeView cubeView) {
		this.view = view;
//		this.cubeView = cubeView;
		this.viewModelController = new ViewModelController(cubeView);
		createHierarchyIndexMap(cubeView);
	}
	private final void createHierarchyIndexMap(CubeView cubeView) {
		//create the hierarchy index:
		Cube cube = cubeView.getCube();
		Dimension[] cubeDims = cube.getDimensions();
		hierarchyIndex = new HashMap<Hierarchy, Integer>();
		for(int i=0;i<cubeDims.length;++i)
			hierarchyIndex.put(cubeDims[i].getDefaultHierarchy(), i);
	}
	
	public final View getView() {
		return view;
	}
	
	public final Cube getCube() {
		return view.getCubeView().getCube();
		//return getCubeView().getCube();
	}
	public final CubeView getCubeView() {
		return viewModelController.getModel().getCubeView();
	}
	
	
	public final synchronized XLoadInfo willOpenView() {
		int visibleCells = viewModelController.getVisibleCellCount();
		System.out.println("visibleCells: "+visibleCells);
		return createLoadInfo(visibleCells, visibleCells, true);		
	}
	private final XLoadInfo createLoadInfo(int loadCells, int visibleCells, boolean newModel) {
		XLoadInfo loadInfo = new XLoadInfo();
		loadInfo.loadCells = loadCells;
		loadInfo.visibleCells = visibleCells < 0 ? 
				viewModelController.getVisibleCellCount() : visibleCells;
		loadInfo.totalCells = newModel ? loadCells : viewModelController.getLoadLevel() + loadInfo.loadCells;
//		loadInfo.totalCells = loadCells + viewModelController.getLoadLevel();
		System.out.println("loadInfo.loadCells: "+loadInfo.loadCells);
		System.out.println("loadInfo.totalCells: "+loadInfo.totalCells);
		System.out.println("loadInfo.visibleCells: "+loadInfo.visibleCells);
		return loadInfo;
	}
	//no need for cancelOpenView, since view is removed then...
	public final synchronized XViewModel proceedOpenView(UserSession userSession, XViewModel oldView, AuthUser user, NumberFormat format) throws PaloGwtCoreException, PaloAPIException {		
		Expanded expanded = initWithCurrentState();
		XViewModel xViewModel = createXView(userSession, user, viewModelController.getModel());
		XCellCollection cells = null;
		cells = loadCells(expanded, format);
		xViewModel.setCells(cells);
		if (oldView != null) {
			xViewModel.setHideEmptyCells(oldView.isHideEmptyCells());
			xViewModel.setColumnsReversed(oldView.isColumnsReversed());			
			xViewModel.setRowsReversed(oldView.isRowsReversed());
			xViewModel.setShowRules(oldView.isShowRules());
			xViewModel.setExternalId(oldView.getExternalId());
			xViewModel.setOwnerId(oldView.getOwnerId());
		}
		return xViewModel;
	}
	public final Expanded initWithCurrentState() {
		notEmptyRows.clear();
		notEmptyColumns.clear();
		return viewModelController.initWithCurrentModelState();
	}
	public final XViewModel createXView(UserSession userSession, AuthUser user, ViewModel model) throws PaloGwtCoreException {
		Account accountToUse = null;
		for (Account acc: user.getAccounts()) {
			if (acc.getId().equals(view.getAccount().getId())) {
				accountToUse = acc;
				break;
			}
			if (acc.getLoginName().equals(view.getAccount().getLoginName())) {
				if (acc.getConnection().getHost().equals(view.getAccount().getConnection().getHost()) &&
					acc.getConnection().getService().equals(view.getAccount().getConnection().getService())) {
					accountToUse = acc;
				}
			} else {
				if (acc.getConnection().getHost().equals(view.getAccount().getConnection().getHost()) &&
						acc.getConnection().getService().equals(view.getAccount().getConnection().getService())) {
						if (accountToUse == null) {
							accountToUse = acc;
						}
					}
			}
		}
		if (accountToUse == null) {
			throw new PaloGwtCoreException("No account for this view");
		}
		if (!accountToUse.getId().equals(view.getAccount().getId())) {
			try {
				((ViewImpl) view).setAccount(user, accountToUse, userSession.getSessionId());				
			} catch (PaloIOException e) {
				throw new PaloGwtCoreException(e.getMessage(), e);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(accountToUse, userSession.getSessionId(), "CubeViewController.createXView");
			}
		}
		XViewModel xViewModel = XViewModelFactory.createX(model, view.getId(), userSession);
		CubeView cView = view.getCubeView();
		if (cView != null) {
			boolean hide = "true".equalsIgnoreCase((String) view.getCubeView()
					.getPropertyValue(CubeView.PROPERTY_ID_HIDE_EMPTY));
			boolean revH = "true".equalsIgnoreCase((String) view.getCubeView()
					.getPropertyValue(
							CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT));
			boolean revV = "true".equalsIgnoreCase((String) view.getCubeView()
					.getPropertyValue(
							CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT));
			boolean rule = "true".equalsIgnoreCase((String) view.getCubeView()
					.getPropertyValue(CubeView.PROPERTY_ID_SHOW_RULES));
			String extId = (String) view.getCubeView().getPropertyValue("paloSuiteID");
			xViewModel.setHideEmptyCells(hide);
			xViewModel.setColumnsReversed(revH);
			xViewModel.setRowsReversed(revV);
			xViewModel.setShowRules(rule);
			xViewModel.setExternalId(extId);
			if (view.getOwner() != null) {
				xViewModel.setOwnerId(view.getOwner().getId());
			}
		}
		return xViewModel;
	}
	
	public final XCellCollection loadCells(Expanded expanded, NumberFormat format) throws PaloAPIException {
		try {
			List<ViewCell> cells = expanded.getCellsToLoad();
			if (cells != null) {
				Element[][] coordinates = getCoordinatesFor(cells);
				XCellCollection cellCollection = getCells(coordinates, expanded,
						format);
				cellCollection.setRowCount(expanded.getRows());
				cellCollection.setColumnCount(expanded.getColumns());
				setEmptyRowsAndColumns(cellCollection);
				return cellCollection;
			}
		} catch (Throwable t) {
			return new XCellCollection(0);
		}
		return new XCellCollection(0);
	}
	
	private final void setEmptyRowsAndColumns(XCellCollection cellCollection) {
		for(int row = 0, n = cellCollection.getRowCount(); row < n; row++) {
			if(!notEmptyRows.contains(row))
				cellCollection.addEmptyRow(row);
		}
		for(int col = 0, n = cellCollection.getColumnCount(); col < n; col++) {
			if(!notEmptyColumns.contains(col))
				cellCollection.addEmptyColumn(col);
		}
	}
	
	private final Element[][] getCoordinatesFor(List<ViewCell> cells) {
		ViewModel viewModel = viewModelController.getModel();
		//updateAxisItemStructure(viewModel);
		
		Element[] coord = new Element[hierarchyIndex.size()];
		
		AxisHierarchy[] selectionHierarchies = 
			viewModel.getRepositoryAxis().getAxisHierarchies();
		fillCoordinateWithSelectedElements(coord, selectionHierarchies);
		
		selectionHierarchies = viewModel.getSelectionAxis().getAxisHierarchies();
		fillCoordinateWithSelectedElements(coord, selectionHierarchies);

		int cellsCount = cells.size();
		Element[][] coordinates = new Element[cellsCount][];

		for (int i = 0; i < cellsCount; ++i) {
			coordinates[i] = coord.clone();
			fill(coordinates[i], cells.get(i));
		}
		return coordinates;
	}
	private final void fillCoordinateWithSelectedElements(Element[] coordinate,
			AxisHierarchy[] selectionHierarchies) {
		for (int i = 0; i < selectionHierarchies.length; ++i) {
			int index = 
					hierarchyIndex.get(selectionHierarchies[i].getHierarchy());
			Element[] selElements = selectionHierarchies[i]
					.getSelectedElements();
			// palo can use only the first selected element:
			if (selElements != null && selElements.length > 0) {
				coordinate[index] = selElements[0];
			}
		}
	}
	private final void fill(Element[] coord, ViewCell cell) {
		fill(coord, cell.row);
		fill(coord, cell.column);
	}
	private final void fill(Element[] coord, AxisItem item) {
		if (item == null)
			return;
		coord[hierarchyIndex.get(item.getHierarchy())] = item.getElement();		
		if (item.getParentInPrevHierarchy() == null) {
			AxisItem i = item.getParent();
			while (i != null) {
				if (i.getParentInPrevHierarchy() != null) {
					item.setParentInPreviousHierarchy(i.getParentInPrevHierarchy());
					break;
				}
				i = i.getParent();
			}			
		}
		fill(coord, item.getParentInPrevHierarchy());
	}

	private final void adjustNotEmptyColumnIndices(int insert, int loaded) {		
		Integer [] nonEmpty = notEmptyColumns.toArray(new Integer[0]);
		notEmptyColumns.clear();
		for (Integer i: nonEmpty) {
			if (i >= insert) {
				notEmptyColumns.add(i + loaded);
			} else {
				notEmptyColumns.add(i);
			}
		}
	}
	
	private final void adjustNotEmptyRowIndices(int insert, int loaded) {		
		Integer [] nonEmpty = notEmptyRows.toArray(new Integer[0]);
		notEmptyRows.clear();
		for (Integer i: nonEmpty) {
			if (i >= insert) {
				notEmptyRows.add(i + loaded);
			} else {
				notEmptyRows.add(i);
			}
		}
	}

	private final XCellCollection getCells(Element[][] coordinates,
			Expanded expanded, NumberFormat format) throws PaloAPIException {
		XCellCollection xCells = new XCellCollection(expanded.insertIndex());
		xCells.setLoadedRowsCount(expanded.getLoadedRows());
		xCells.setLoadedColumnsCount(expanded.getLoadedColumns());
		xCells.setVisibleCellCount(viewModelController.getVisibleCellCount());
		
		List<ViewCell> loadedCells = expanded.getCellsToLoad();
		
		if (loadedCells.isEmpty())
			return xCells;

		Cell [] cells = loadCellsFrom(getCube(), coordinates);
		
		int index = 0;		
//		setEmptyCells = false;
		if (expanded.inColumn()) {
			adjustNotEmptyColumnIndices(xCells.getInsertIndex(), xCells.getLoadedColumnsCount());
		} else {
			adjustNotEmptyRowIndices(xCells.getInsertIndex(), xCells.getLoadedRowsCount());
		}
		for (Cell cell : cells) {
			ViewCell loadedCell = loadedCells.get(index);
			if (cell == null) {
				System.err.println("Cell == null!");
				continue;
			}
			boolean isEmpty = cell.isEmpty();
			if (!isEmpty) {
				if (cell.getType() == Cell.NUMERIC) {
					try {
						Double d = Double.parseDouble(cell.getValue().toString());
						if (Math.abs(d) < 0.000001) {
							isEmpty = true;
						}
					} catch (Exception e) {						
					}
				}
			}
			if(!isEmpty) {
				setNotEmptyRow(loadedCell.row.leafIndex, expanded.getLoadedRows());
				setNotEmptyColumn(loadedCell.column.leafIndex, expanded.getLoadedColumns());
			}
			xCells.add(createXCell(cell, loadedCell.row.leafIndex,
					loadedCell.column.leafIndex, format));
			index++;
		}
		return xCells;
	}
		
	private final void setNotEmptyRow(int row, int loadedRows) {
//		if (loadedRows == 0) {
//			// PR 740: Prevent endless loop
//			return;
//		}
		notEmptyRows.add(row);
//		while(!notEmptyRows.add(row))
//			row += loadedRows;
	}
	private final void setNotEmptyColumn(int column, int loadedColumns) {
//		if (loadedColumns == 0) {
//			// PR 740: Prevent endless loop
//			return;
//		}
//		while(!notEmptyColumns.add(column))
//			column += loadedColumns;
		notEmptyColumns.add(column);
	}

	private final XCell createXCell(Cell cell, int row, int col, NumberFormat format) {
		XCell xCell = new XCell(row, col);
		xCell.type = cell.getType() == Cell.NUMERIC ? XCell.TYPE_NUMERIC : XCell.TYPE_STRING;
		format(xCell, cell);
		if(xCell.format == null)
			CellFormatter.applyDefaultFormat(xCell, cell, format);
		xCell.isRuleBased = cell.hasRule() && cell.getType() != Cell.STRING;
//        try {
//			xCell.isRuleBased = getCube().getRule(cell.getCoordinate()) != null;
//		} catch (Throwable t) {
//			xCell.isRuleBased = true;
//		}
		xCell.isConsolidated = cell.isConsolidated();
		return xCell;
	}
	private final void format(XCell xCell, Cell cell) {
		Format[] formats = getCubeView().getFormats();
		cell.getCoordinate();
		for(Format format : formats) {
			for(FormatRangeInfo range : format.getRanges()) {
				RangePosition positionInRange = getPositionInRange(range, cell);
				if(!positionInRange.equals(RangePosition.NONE))
					CellFormatter.applyFormat(format, xCell, cell, positionInRange);
			}
		}
	}
	private final RangePosition getPositionInRange(FormatRangeInfo range,
			Cell cell) {
		Element[][] cells = range.getCells();
		Element[] coordinate = cell.getCoordinate();
		Property<Object> rangeProperty = getCubeView()
				.getProperty(FormatConverter.PROPERTY_FORMAT_RANGES);
		if (rangeProperty != null) {
			String fromTo = (String)rangeProperty.getValue();
			if (fromTo != null) {
				int[] indices = FormatConverter.getFromToIndices(fromTo);
				for (int i = 0; i < cells.length; i++) {
					if (coordinatesAreEqual(cells[i], coordinate)) {
						RangePosition positionInRange = RangePosition
								.getPositionInRange(i, indices[0], indices[1],
										indices[2], indices[3]);
						if (!positionInRange.equals(RangePosition.NONE))
							return positionInRange;
					}
				}
			}
		}
		return RangePosition.NONE; // false;
	}
	private final boolean coordinatesAreEqual(Element[] coord1,
			Element[] coord2) {
		if (coord1.length == coord2.length) {
			for (int i = 0; i < coord1.length; i++) {
				if (!coord1[i].equals(coord2[i]))
					return false;
			}
			return true;
		}
		return false;
	}
	
	
	public final void collapse(XAxisItem item, String axisId) {
		viewModelController.willCollapse(item, axisId);
		viewModelController.proceedCollapse();
	}

	public XLoadInfo willCollapse(XAxisItem xItem, String axisId) {
		viewModelController.willCollapse(xItem, axisId);
		return createLoadInfo(0, -1, false);
	}
	
	public void proceedCollapse() {
		viewModelController.proceedCollapse();
	}
	
	public final void cancelCollapse(XAxisItem item, String axisId) {
		viewModelController.cancelCollapse();
	}
	
	public final XLoadInfo willExpand(XAxisItem xItem, String axisId) {
		return createLoadInfo(viewModelController.willExpand(xItem, axisId), -1, false);
	}
	
	public final XLoadInfo willSwapAxes() {
		return createLoadInfo(0, viewModelController.getVisibleCellCount(), false);
	}
	
	public final XViewModel proceedSwapAxes(UserSession userSession, AuthUser user, NumberFormat format, XViewModel oldView) {
		viewModelController.proceedSwapAxes();		
		try {
			Expanded expanded = initWithCurrentState();
			XViewModel xViewModel = createXView(userSession, user, viewModelController.getModel());			
			XCellCollection cells = null;
			cells = loadCells(expanded, format);
			xViewModel.setCells(cells);
			if (oldView != null) {
				xViewModel.setHideEmptyCells(oldView.isHideEmptyCells());
				xViewModel.setColumnsReversed(oldView.isRowsReversed());			
				xViewModel.setRowsReversed(oldView.isColumnsReversed());
				xViewModel.setShowRules(oldView.isShowRules());
				xViewModel.setExternalId(oldView.getExternalId());
				xViewModel.setOwnerId(oldView.getOwnerId());
			}
			return xViewModel;
		} catch (PaloAPIException e) {
			e.printStackTrace();
		} catch (PaloGwtCoreException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public final XDelta proceedExpand(XAxisItem parent, String axisId, NumberFormat format) {
		Expanded expanded = viewModelController.proceedExpand(); //parent, axisId);
		XDelta delta = new XDelta(parent);
		//load cells:
		XCellCollection cells = loadCells(expanded, format);
		delta.setCells(cells);
		if (!cells.isEmpty()) {
			// expanded items:
			for (AxisItem expandedItem : expanded.getItem().getChildren())
				delta.add(XAxisItemFactory.createX(expandedItem, parent.level,
						parent.depth + 1, parent));
		}
		return delta;
	}
	
	public final String [] hideItem(XAxisItem item, List <XAxisItem> roots, String axisId, String viewId, boolean hideLevel) {
		return viewModelController.hideItem(item, roots, axisId, viewId, hideLevel);
	}
	
	public final void cancelExpand(XAxisItem item, String axisId) {
		viewModelController.cancelExpand(); //item, axisId);
	}
	

	public final XLoadInfo willSetExpandState(XAxisItem[] expanded,
			XAxisItem[] collapsed, int expandDepth, String axisId) {
		return createLoadInfo(viewModelController.willSetExpandState(expanded, collapsed,
				axisId, expandDepth), -1, false);
	}
	public final XDelta[] proceedSetExpandState(NumberFormat format) {
		List<Expanded> allExpanded = 
			viewModelController.proceedSetExpandStateExpanded();
		List<XDelta> deltas = new ArrayList<XDelta>();
		XDelta delta = null;
		for(Expanded expanded : allExpanded) {
			delta = createXDelta(expanded, delta, format, deltas);
		}
		//we use the last delta to collapse:
		List<Collapsed> allCollapsed = 
			viewModelController.proceedSetExpandStateCollapsed();
		if(delta == null) {
			delta = new XDelta(null);
			deltas.add(delta);
		}
		for(Collapsed collapsed : allCollapsed) {
			XAxisItem xItem = collapsed.getXItem();
			if(xItem != null)
				delta.addCollapsed(xItem);
		}
		return deltas.toArray(new XDelta[0]);
	}

	private final XDelta createXDelta(Expanded expanded, XDelta parentDelta,
			NumberFormat format, List<XDelta> allDeltas) {
		XAxisItem expandedXItem = expanded.getXItem();
		if (expandedXItem != null) {
			// create a new XDelta
			parentDelta = createXDelta(expanded, format);
			allDeltas.add(parentDelta);
		} else {
			// add cells to parent delta
			if(parentDelta != null) {
				XCellCollection cells = loadCells(expanded, format);
				int val;
				if (expanded.inColumn()) {
					val = parentDelta.getCells().getLoadedColumnsCount();
				} else {
					val = parentDelta.getCells().getLoadedRowsCount();
				}
				parentDelta.addCells(cells);
				if (expanded.inColumn()) {
					parentDelta.getCells().setLoadedColumnsCount(val + expanded.getLoadedColumns());
				} else {
					parentDelta.getCells().setLoadedRowsCount(val + expanded.getLoadedRows());
				}
			}			
		}
		return parentDelta;
	}
	private final XDelta createXDelta(Expanded expanded, NumberFormat format) {
		XDelta delta = new XDelta(expanded.getXItem());
		// load cells:
		XCellCollection cells = loadCells(expanded, format);
		delta.setCells(cells);
		if (!cells.isEmpty()) {
			XAxisItem parent = expanded.getXItem();
			// expanded items:
			for (AxisItem expandedItem : expanded.getItem().getChildren())
				delta.add(XAxisItemFactory.createX(expandedItem, parent.level,
						parent.depth + 1, parent));
		}
		return delta;
	}
	public final void cancelSetExpandState() {
		viewModelController.cancelSetExpandState();
	}
	

	public final List<XElementNode> getRoots(XAxisHierarchy ofHierarchy) {
		List<XElementNode> roots = new ArrayList<XElementNode>();
		Axis axis = getCubeView().getAxis(ofHierarchy.getAxisId());
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(ofHierarchy.getId());
		String axisHierarchyId = ofHierarchy.getId();
		String viewId = ofHierarchy.getViewId();
		if(axisHierarchy != null) {
			for(ElementNode rootElement : axisHierarchy.getRootNodes()) {
				XElementNode xRoot = XElementFactory.createX(rootElement, axisHierarchyId, viewId);				
				xRoot.setChildCount(rootElement.getChildCount());
				roots.add(xRoot);
				elementNodes.put(xRoot.getId(), rootElement);
			}
		}
		return roots;
	}
	
	public final List <XElementNode> getRoots(String axisId, String hierarchyId, String viewId) {
		List<XElementNode> roots = new ArrayList<XElementNode>();
		Axis axis = getCubeView().getAxis(axisId);
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(hierarchyId);
		if(axisHierarchy != null) {
			for(ElementNode rootElement : axisHierarchy.getRootNodes()) {
				XElementNode xRoot = XElementFactory.createX(rootElement, hierarchyId, viewId);				
				xRoot.setChildCount(rootElement.getChildCount());
				roots.add(xRoot);
				elementNodes.put(xRoot.getId(), rootElement);
			}
		}
		return roots;		
	}
	
	public final List<XElementNode> loadChildren(String elementNodeId) {
		List<XElementNode> children = new ArrayList<XElementNode>();
		ElementNode elementNode = elementNodes.get(elementNodeId);
		for(ElementNode child : elementNode.getChildren()) {
			XElementNode xChild = XElementFactory.createX(child, null, null);
			xChild.setChildCount(child.getChildCount());
			elementNodes.put(xChild.getId(), child);
			children.add(xChild);
		}
		return children;		
	}
	
	private final void addChildren(int currentDepth, int level, ElementNode [] rootNodes, List <XElementNode> nodes, XAxisHierarchy hierarchy, HashMap <ElementNode, XElementNode> parents) {
		if (currentDepth >= level && level != -1) {
			return;
		}
		ArrayList <ElementNode> nextLevel = new ArrayList<ElementNode>();
		String hierarchyId = hierarchy.getId();
		String viewId = hierarchy.getViewId();
		for (ElementNode root: rootNodes) {			
			XElementNode parent = parents.get(root);
			for (ElementNode kid: root.getChildren()) {
				nextLevel.add(kid);
				XElementNode xKid = XElementFactory.createX(kid, hierarchyId, viewId);
				xKid.setParent(parent);
				xKid.setChildCount(kid.getChildCount());
				parents.put(kid, xKid);
				nodes.add(xKid);
			}
		}
		if (nextLevel.size() != 0 && ((currentDepth + 1) < level || level == -1)) {
			addChildren(currentDepth + 1, level, nextLevel.toArray(new ElementNode[0]), nodes, hierarchy, parents);
		}
	}
	
	public final List <XElementNode> loadElements(XAxisHierarchy hierarchy, int level) {
		List<XElementNode> nodes = new ArrayList<XElementNode>();
		Axis axis = getCubeView().getAxis(hierarchy.getAxisId());
		AxisHierarchy axisHierarchy = axis.getAxisHierarchy(hierarchy.getId());
		if(axisHierarchy != null) {
			ElementNode [] rootNodes = axisHierarchy.getRootNodes();
			HashMap<ElementNode, XElementNode> parents = new HashMap<ElementNode, XElementNode>();
			String axisHierarchyId = hierarchy.getId();
			String viewId = hierarchy.getViewId();
			for (ElementNode rootElement: rootNodes) {
				XElementNode xRoot = XElementFactory.createX(rootElement, axisHierarchyId, viewId);				
				xRoot.setChildCount(rootElement.getChildCount());
				nodes.add(xRoot);
				elementNodes.put(xRoot.getId(), rootElement);
				if (rootElement.getChildCount() != 0) {
					parents.put(rootElement, xRoot);
				}
			}
			int currentDepth = 1;
			addChildren(currentDepth, level, rootNodes, nodes, hierarchy, parents);
		}
		return nodes;		
	}
	
	public final List<XElementNode> loadChildren(XElementNode node) {
		String axisHierarchyId = node.getAxisHierarchyId();
		String viewId = node.getViewId();
		List<XElementNode> children = new ArrayList<XElementNode>();
		ElementNode elementNode = elementNodes.get(node.getId());
		for(ElementNode child : elementNode.getChildren()) {
			XElementNode xChild = XElementFactory.createX(child, axisHierarchyId, viewId);
			xChild.setChildCount(child.getChildCount());
			elementNodes.put(xChild.getId(), child);
			children.add(xChild);
		}
		return children;
	}
	
	public final List<XElementNode> loadChildren(XAxisHierarchy hier, String elementNodeId) {
		List<XElementNode> children = new ArrayList<XElementNode>();
		ElementNode elementNode = elementNodes.get(elementNodeId);
		String axisHierarchyId = hier.getId();
		String viewId = hier.getViewId();
		for(ElementNode child : elementNode.getChildren()) {
			XElementNode xChild = XElementFactory.createX(child, axisHierarchyId, viewId);
			xChild.setChildCount(child.getChildCount());
			elementNodes.put(xChild.getId(), child);
			children.add(xChild);
		}
		return children;		
	}
	
	public final XLoadInfo willChangeSelectedElement() {
		int visibleCells = viewModelController.getVisibleCellCount();
		return createLoadInfo(visibleCells, visibleCells, true);
	}
	public final XViewModel proceedChangeSelectedElement(UserSession userSession, AuthUser user,
			XViewModel xViewModel, XAxisHierarchy xAxisHierarchy,
			XElement selectedElement, NumberFormat format)
			throws SessionExpiredException, PaloGwtCoreException {
		AxisHierarchy axisHierarchy = getNative(xAxisHierarchy);
		Hierarchy hierarchy = axisHierarchy.getHierarchy();
		axisHierarchy.clearSelectedElements();
		axisHierarchy.addSelectedElement(hierarchy
				.getElementById(selectedElement.getId()));
		return proceedOpenView(userSession, xViewModel, user, format);
	}	
	private final AxisHierarchy getNative(XAxisHierarchy xAxisHierarchy) {
		Axis axis = getCubeView().getAxis(xAxisHierarchy.getAxisId());
		AxisHierarchy hier = axis.getAxisHierarchy(xAxisHierarchy.getId());
		hier.setAxis(axis);
		return hier;
	}
		
	private final AxisHierarchy getNative(String xAxisId, String xAxisHierarchyId) {
		Axis axis = getCubeView().getAxis(xAxisId);
		AxisHierarchy hier = axis.getAxisHierarchy(xAxisHierarchyId);
		hier.setAxis(axis);
		return hier;
	}

	public final XElement update(XAxisHierarchy xAxisHierarchy) {
		AxisHierarchy axisHierarchy = getNative(xAxisHierarchy);
		return AxisHierarchyUpdater.update(axisHierarchy, xAxisHierarchy);
	}
	public final XLoadInfo willUpdate(XAxisHierarchy xAxisHierarchy) {
		AxisHierarchy axisHierarchy = getNative(xAxisHierarchy);
		if (isSelectionAxis(axisHierarchy.getAxis()))
			return willUpdateSelectionAxis(xAxisHierarchy);

		// check difference via snapshot
		AxisHierarchySnapshot snapshotBefore = 
				AxisHierarchySnapshot.createSnapshot(axisHierarchy);
		AxisHierarchyUpdater.update(axisHierarchy, xAxisHierarchy);
		
		AxisHierarchySnapshot snapshotAfter = 
				AxisHierarchySnapshot.createSnapshot(axisHierarchy);
		boolean filterDeactivated = xAxisHierarchy.getVisibleElements() == null && 
			xAxisHierarchy.getOldVisibleElements() != null;
		boolean createNewModel = !snapshotAfter.equals(snapshotBefore) || filterDeactivated;		
		xAxisHierarchy.setOldVisibleElements(xAxisHierarchy.getVisibleElements());
		if(createNewModel) {
			viewModelController.refreshAxis(axisHierarchy.getAxis());
		}
		int visibleCells = viewModelController.getVisibleCellCount();
		int loadCells = createNewModel ? visibleCells : viewModelController.getNumberOfCellsToLoad();
		return createLoadInfo(loadCells, visibleCells, createNewModel);
	}
	
	public final XLoadInfo willUpdate(String viewId, String axisHierarchyId,
			String axisId) {
		AxisHierarchy axisHierarchy = getNative(axisId, axisHierarchyId);
//		if (isSelectionAxis(axisHierarchy.getAxis()))
//			return willUpdateSelectionAxis(xAxisHierarchy);

		viewModelController.refreshAxis(axisHierarchy.getAxis());
		int visibleCells = viewModelController.getVisibleCellCount();
		int loadCells = visibleCells;
		return createLoadInfo(loadCells, visibleCells, true);
	}
	
	private final boolean isSelectionAxis(Axis axis) {
		ViewModel model = viewModelController.getModel();
		if (model == null || axis == null) {
			return false;
		}
		return axis.equals(model.getSelectionAxis());
	}
	private final XLoadInfo willUpdateSelectionAxis(XAxisHierarchy xAxisHierarchy) {
		AxisHierarchy axisHierarchy = getNative(xAxisHierarchy);
		AxisHierarchyUpdater.update(axisHierarchy, xAxisHierarchy);
		int loadCells = 0;
		int visibleCells = viewModelController.getVisibleCellCount();
		if (selectedElementChanged(axisHierarchy, 
				xAxisHierarchy.getSelectedElement())) {
			loadCells = visibleCells;
		}		
		return createLoadInfo(loadCells, visibleCells, true);
	}
	private final boolean selectedElementChanged(AxisHierarchy axisHierarchy,
			XElement newSelectedElement) {
		Element[] selectedElements = axisHierarchy.getSelectedElements();
		if (selectedElements != null && selectedElements.length > 0 && selectedElements[0] != null)
			return !selectedElements[0].getId().equals(
					newSelectedElement.getId());
		return newSelectedElement != null;
	}
	public final void update(XViewModel xViewModel) {
		ViewModelUpdater.updateNative(viewModelController.getModel(),xViewModel, false);
		viewModelController.initWithCurrentModelState();
	}
	public final void updateNonInit(XViewModel xViewModel) {
		ViewModelUpdater.updateNative(viewModelController.getModel(),xViewModel, false);
//		viewModelController.initWithCurrentModelState();		
	}

	public final XViewModel updateAndReload(UserSession userSession, AuthUser user, XViewModel xViewModel,
			NumberFormat format) throws PaloGwtCoreException {
		update(xViewModel);
		return proceedOpenView(userSession, xViewModel, user, format);
	}
	public final XLoadInfo willUpdate(XViewModel xViewModel) {
		//we create a temp. new viewmodel from xViewModel		
		ViewModelController viewController = new ViewModelController(getCubeView().copy());		
		//update
		ViewModelUpdater.updateNative(viewController.getModel(), xViewModel, false);
//		viewController.initWithCurrentModelState(); //marks all visible cells as loaded...
		int visibleCells = viewController.getVisibleCellCount();
//		int loadCount = viewController.getNumberOfCellsToLoad();
		XLoadInfo lInfo = createLoadInfo(visibleCells, visibleCells, true);
		return lInfo;
	}

	public final XLoadInfo updateLoadInfo(XViewModel xViewModel, int cellsToDisplay) {
		return createLoadInfo(0, cellsToDisplay, false);
	}
	
	public final synchronized XViewModel proceedUpdate(UserSession userSession, AuthUser user, XViewModel xViewModel, NumberFormat format) throws PaloGwtCoreException {
		//update
		ViewModelUpdater.updateNative(viewModelController.getModel(), xViewModel, false);
		return proceedOpenView(userSession, xViewModel, user, format);
	}
	
	public final XViewModel proceedUpdateWithoutTable(AuthUser user, XViewModel xViewModel, NumberFormat format) throws PaloGwtCoreException {
		ViewModelUpdater.updateNativeWithoutTable(viewModelController.getModel(), xViewModel);		
		return xViewModel;
	}
	
	public final XViewModel cancelUpdateView(UserSession userSession, AuthUser user) throws PaloGwtCoreException {
		return createXView(userSession, user, viewModelController.getModel());
	}
	
	public final synchronized XLoadInfo willReloadView() {
		int visibleCells = viewModelController.getVisibleCellCount();
		return createLoadInfo(visibleCells, visibleCells, true);
	}
	public final synchronized XViewModel proceedReloadView(UserSession userSession, XViewModel view, AuthUser user, NumberFormat format) throws PaloGwtCoreException {		
		return proceedOpenView(userSession, view, user, format);
	}
	
	public final View updateCubeView(XViewModel xView) {
		ViewModel viewModel = viewModelController.getModel();
		CubeView cubeView = viewModel.getCubeView();
		Axis row = viewModel.getRowAxis().getAxis();
		updateExpandedPathsFor(row, xView.getRowAxis());
		
		Axis column = viewModel.getColumnAxis().getAxis();
		updateExpandedPathsFor(column, xView.getColumnAxis());
				
		if (xView.isHideEmptyCells()) {
			cubeView.addProperty(CubeView.PROPERTY_ID_HIDE_EMPTY, "true");
		} else {
			cubeView.removeProperty(CubeView.PROPERTY_ID_HIDE_EMPTY);
		}
		
		if (xView.isColumnsReversed()) {
			cubeView.addProperty(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT, "true");
		} else {
			cubeView.removeProperty(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT);
		}
		
		if (xView.isRowsReversed()) {
			cubeView.addProperty(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT, "true");
		} else {
			cubeView.removeProperty(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT);
		}
		
		if (xView.isShowRules()) {
			cubeView.addProperty(CubeView.PROPERTY_ID_SHOW_RULES, "true");
		} else {
			cubeView.removeProperty(CubeView.PROPERTY_ID_SHOW_RULES);
		}
		
		String extId = xView.getExternalId();
		if (extId != null) {
			cubeView.addProperty("paloSuiteID", extId);
		}
		view.setCubeView(cubeView);
		
		return view;
	}
	private final void updateExpandedPathsFor(Axis axis,
			XAxis xAxis) {
		axis.removeAllExpandedPaths();
		XAxisItem[] expandedItems = xAxis.getExpandedItems();
		for (XAxisItem xItem : expandedItems) {
			ElementPath path = ElementPath.restore(axis.getHierarchies(), xItem
					.getPath());
			axis.addExpanded(path);
		}
	}
	
	public final XCellCollection writeCell(XCell cell, NumberFormat format, AuthUser user) {
		Element[] coordinate = getCoordinate(cell.row, cell.col);
		CellWriter.getInstance().writeCell(cell, coordinate, getCube(), format, user);
		return reloadCells(format);
	}
	private final XCellCollection reloadCells(NumberFormat format) {
		Expanded expanded = initWithCurrentState();
		return loadCells(expanded, format);
	}

	private final Element[] getCoordinate(int row, int col) {
		ViewModel viewModel = viewModelController.getModel();		
		Element[] coordinate = new Element[hierarchyIndex.size()];
		
		AxisHierarchy[] selectionHierarchies = 
			viewModel.getRepositoryAxis().getAxisHierarchies();
		fillCoordinateWithSelectedElements(coordinate, selectionHierarchies);
		selectionHierarchies = viewModel.getSelectionAxis().getAxisHierarchies();
		fillCoordinateWithSelectedElements(coordinate, selectionHierarchies);

		List<AxisItem> rowLeafs = ViewModelUtils.getLeafs(viewModel.getRowAxis());
		List<AxisItem> colLeafs = ViewModelUtils.getLeafs(viewModel.getColumnAxis());
			
		fill(coordinate, rowLeafs.get(row));
		fill(coordinate, colLeafs.get(col));
		return coordinate;
	}
	
	private final Cell[] loadCellsFrom(Cube cube, Element[][] coordinates) throws PaloAPIException {
		Cell[] cells = new Cell[coordinates.length];
		List<Element[]> loadCoordinates = new ArrayList<Element[]>();
		int index = 0;
		for (; index < coordinates.length; index++) {
			Element[] coord = coordinates[index];
			if (isVirtual(coord)) {
				cells[index] = new VirtualCell("", coord);
				if(!loadCoordinates.isEmpty()) {
					Cell[] _cells = cube.getCells(loadCoordinates.toArray(new Element[0][]));
					loadCoordinates.clear();
					System.arraycopy(_cells, 0, cells, index - _cells.length, _cells.length);
				}				
			} else {
				loadCoordinates.add(coord);
			}
		}
		if(!loadCoordinates.isEmpty()) {			
			Cell[] _cells = cube.getCells(loadCoordinates.toArray(new Element[0][]));
			System.arraycopy(_cells, 0, cells, index - _cells.length, _cells.length);
		}
		return cells;
	}

	private final boolean isVirtual(Element[] coordinate) {
		for(Element element : coordinate) {
			if(element instanceof VirtualElement)
				return true;
		}
		return false;
	}
}

class ViewModelSnapshot {
	
	static final ViewModelSnapshot createSnapshot(ViewModel model) {
		ViewModelSnapshot snapshot = new ViewModelSnapshot(model);
		snapshot.create();
		return snapshot;
	}
	
	private final ViewModel model;
	private List<Element> selectedElements;
	private List<AxisItem> rowLeafs;
	private List<AxisItem> columnLeafs;
	
	private ViewModelSnapshot(ViewModel model) {
		this.model = model;
	}
	
	public boolean equals(ViewModelSnapshot snapshot) {
		return selectedElementsAreEqual(snapshot.selectedElements)
				&& leafsAreEqual(rowLeafs, snapshot.rowLeafs)
				&& leafsAreEqual(columnLeafs, snapshot.columnLeafs);
	}
	private final boolean selectedElementsAreEqual(List<Element> otherSelectedElements) {
		return listsAreEqual(selectedElements, otherSelectedElements);
	}
	private final boolean leafsAreEqual(List<AxisItem> myLeafs, List<AxisItem> otherLeafs) {		
		return listsAreEqual(myLeafs, otherLeafs);
	}
	private final boolean listsAreEqual(List<?> list1, List<?> list2) {
		if (list1.size() == list2.size()) {
			List<?> list2Copy = new ArrayList<Object>(list2);
			for (Object obj : list1)
				list2Copy.remove(obj);
			return list2Copy.isEmpty();
		}
		return false;
	}
	private final void create() {
		selectedElements = getAllSelectedElements();
		rowLeafs = getRowLeafs();
		columnLeafs = getColumnLeafs();
	}
	
	private final List<Element> getAllSelectedElements() {
		List<Element> selectedElements = new ArrayList<Element>();
		AxisHierarchy[] axisHierarchies = model.getSelectionAxis().getAxisHierarchies();
		for(AxisHierarchy hierarchy : axisHierarchies)
			selectedElements.add(hierarchy.getSelectedElements()[0]);
		return selectedElements;
	}
	private final List<AxisItem> getRowLeafs() {
		return ViewModelUtils.getLeafs(model.getRowAxis());
	}
	private final List<AxisItem> getColumnLeafs() {
		return ViewModelUtils.getLeafs(model.getColumnAxis());
	}

}

class AxisHierarchySnapshot {
	
	static final AxisHierarchySnapshot createSnapshot(AxisHierarchy hierarchy) {
		AxisHierarchySnapshot snapshot = new AxisHierarchySnapshot(hierarchy);
		snapshot.create();
		return snapshot;
	}
	
	
	private Subset2 subset;
	private Attribute alias;
	private Element selectedElement;
	private ElementNode[] visibleElements;
	private final AxisHierarchy axisHierarchy;
	
	private AxisHierarchySnapshot(AxisHierarchy axisHierarchy) {
		this.axisHierarchy = axisHierarchy;
	}
	
	public boolean equals(AxisHierarchySnapshot snapshot) {
		boolean areEqual = aliasesAreEqual(snapshot.alias)
				&& subsetsAreEqual(snapshot.subset)
				&& selectedElement.equals(snapshot.selectedElement);
		return areEqual && visibleElementsAreEqual(snapshot.visibleElements);
	}
	
	private final boolean aliasesAreEqual(Attribute other) {
		if(alias == null && other == null)
			return true;
		return alias != null && other != null && alias.equals(other);
	}
	private final boolean subsetsAreEqual(Subset2 other) {
		if(subset == null && other == null)
			return true;
		return subset != null && other != null && subset.equals(other);
		
	}

	private final boolean visibleElementsAreEqual(ElementNode[] others) {
		if(visibleElements == null && others == null)
			return true;

		if(visibleElements != null && others != null && visibleElements.length == others.length) {
			ArrayList<ElementNode> otherNodes = new ArrayList<ElementNode>();
			for (ElementNode n: others) {
				otherNodes.add(n);
			}
			for(ElementNode node : visibleElements) {
				otherNodes.remove(node);
			}
			return otherNodes.isEmpty();		
		}
		return false;
	}
	private final void create() {
		alias = getAlias(axisHierarchy);
		subset = axisHierarchy.getSubset();
		selectedElement = axisHierarchy.getSelectedElements()[0];
		LocalFilter localFilter = axisHierarchy.getLocalFilter();
		if(localFilter != null)
			visibleElements = localFilter.getVisibleElements();		
	}
	private static final Attribute getAlias(AxisHierarchy axisHierarchy) {
		Property<?> aliasProperty = 
			axisHierarchy.getProperty(AxisHierarchy.USE_ALIAS);
		if(aliasProperty != null) {
			return (Attribute)aliasProperty.getValue();
		}
		return null;
	}
}
