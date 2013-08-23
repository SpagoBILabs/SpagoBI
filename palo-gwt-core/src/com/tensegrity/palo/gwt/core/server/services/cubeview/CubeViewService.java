/*
*
* @file CubeViewService.java
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
* @version $Id: CubeViewService.java,v 1.60 2010/04/12 11:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import it.eng.spagobi.util.spagobi.JPaloSavingUtil;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.palo.api.Cube;
import org.palo.api.ElementNode;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.Right;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.exporters.PaloPDFExporter;
import org.palo.viewapi.exporters.PaloPDFExporterConfiguration;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.PaloAccountImpl;
import org.palo.viewapi.internal.ServerConnectionPool;
import org.palo.viewapi.internal.ViewImpl;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewIO;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XLoadInfo;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintConfiguration;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XPrintResult;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.BasePaloServiceServlet;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.CubeViewConverter;

/**
 * <code>CubeViewService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewService.java,v 1.60 2010/04/12 11:14:15 PhilippBouillon Exp $
 **/
public class CubeViewService extends BasePaloServiceServlet {

	/** generated */
	private static final long serialVersionUID = -9111761248973226696L;
	private static transient Logger logger = Logger.getLogger(CubeViewService.class);
	
	public static final String getPath(ElementNode node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(node.getElement().getId());
		if (node.getParent() != null) {
			int rep = 0;
			for (ElementNode kid: node.getParent().getChildren()) {
				if (kid.equals(node)) {
					break;
				}
				if (kid.getElement().equals(node.getElement())) {
					rep++;
				}					
			}
			if (rep != 0) {
				buffer.append("(");
				buffer.append(rep);
				buffer.append(")");
			}
			buffer.append(":");
			return getPath(node.getParent()) + buffer.toString();
		} else {
			buffer.append(":");
		}
		return buffer.toString();
	}
	
	public final synchronized XLoadInfo willOpenView(String sessionId, XView xView) throws SessionExpiredException,
			PaloGwtCoreException {
		try {
			Account accountToUse = null;
			AuthUser user = getLoggedInUser(sessionId);
			System.out.println("+++++++++++++++++"+user);
			Account referenceAccount = (Account) MapperRegistry.getInstance().getAccountManagement().find(xView.getAccountId());
			System.out.println("+++++++++++++++++"+referenceAccount);
			if (referenceAccount != null) {
				for (Account acc: user.getAccounts()) {
					if (acc.getId().equals(referenceAccount.getId())) {
						accountToUse = acc;
						
						break;
					}
					if (acc.getLoginName().equals(referenceAccount.getLoginName())) {
						if (acc.getConnection().getHost().equals(referenceAccount.getConnection().getHost()) &&
							acc.getConnection().getService().equals(referenceAccount.getConnection().getService())) {
							accountToUse = acc;
						}
					} else {
						if (acc.getConnection().getHost().equals(referenceAccount.getConnection().getHost()) &&
								acc.getConnection().getService().equals(referenceAccount.getConnection().getService())) {
								if (accountToUse == null) {
									accountToUse = acc;
								}
							}
					}
				}
				System.out.println("+++++++++++++++++"+accountToUse.getLoginName());
				System.out.println("+++++++++++++++++"+accountToUse.getConnection().getName());
				System.out.println("+++++++++++++++++"+accountToUse.getUser().getLoginName());
				if (accountToUse == null) {
					throw new PaloGwtCoreException("No account for this view");
				}
				if (accountToUse instanceof PaloAccount) {
					ServerConnectionPool pool = ConnectionPoolManager.getInstance().getPool(accountToUse, sessionId);
					((PaloAccountImpl) accountToUse).setConnection(pool.getConnection("willOpenView"));
//					((PaloAccount) accountToUse).login();
				}				
			}
			//xView.getAccountId()
			
			CubeViewController viewController = getControllerFor(sessionId, xView, accountToUse);
			View view = viewController.getView();
			System.out.println("-------------"+view.getName());
			return viewController.willOpenView();
		} catch (PaloAPIException e) {
			System.out.println("::::::::::::1"+e.getMessage());
			throw new PaloGwtCoreException(e.getMessage(), e);
		} catch (NullPointerException e) {
			String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
			Throwable c = e.getCause() == null ? e : e.getCause();
			System.out.println("::::::::::::2"+c.getMessage());
			throw new PaloGwtCoreException(msg, c);
		} catch (Throwable t) {
			System.out.println("::::::::::::3"+t.getMessage());
			throw new PaloGwtCoreException(t.getLocalizedMessage(), t);
		}
	}
	public synchronized final XViewModel proceedOpenView(String sessionId, AuthUser user, String viewId)
			throws SessionExpiredException, PaloGwtCoreException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, viewId);
			XViewModel model = viewController.proceedOpenView(getUserSession(sessionId), null, user, getNumberFormat(sessionId));
			return model;
		} catch (PaloAPIException e) {
			CubeViewController.removeControllerFor(sessionId, viewId);
			throw new PaloGwtCoreException(e.getMessage(), e);
		} catch (Throwable t) {
			throw new PaloGwtCoreException(t.getLocalizedMessage(), t);
		}
	}
	public final void cancelOpenView(String sessionId, String viewId) throws SessionExpiredException {
		UserSession userSession = getUserSession(sessionId);
		CubeViewController.removeViewById(userSession, viewId);
	}

	
	public void update(String sessionId, XViewModel xViewModel) throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			viewController.update(xViewModel);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}

	public XViewModel updateAndReloadView(String sessionId, XViewModel viewModel)
			throws SessionExpiredException {
		try {
			UserSession userSession = getUserSession(sessionId);
			CubeViewController viewController = getControllerFor(sessionId, viewModel);
			return viewController.updateAndReload(userSession, userSession.getUser(),
					viewModel, getNumberFormat(sessionId));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session expired!");
		}
	}


	public final synchronized XLoadInfo willUpdateView(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		XLoadInfo lInfo = viewController.willUpdate(xViewModel);
		return lInfo;
	}

	public final synchronized XLoadInfo willUpdateView(String sessionId, String viewId, String axisHierarchyId,
			String axisId)
	throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		XLoadInfo lInfo = viewController.willUpdate(viewId, axisHierarchyId, axisId);
		return lInfo;
	}

	public final synchronized XViewModel proceedUpdateView(String sessionId, AuthUser user, XViewModel xViewModel)
			throws SessionExpiredException, PaloGwtCoreException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		return viewController.proceedUpdate(getUserSession(sessionId), user, xViewModel, getNumberFormat(sessionId));
	}

	public String [] hideItem(String sessionId, XAxisItem item, List <XAxisItem> roots, String viewId, String axisId, boolean hideLevel)
		throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.hideItem(item, roots, axisId, viewId, hideLevel);
	}
	
	public final XViewModel proceedUpdateViewWithoutTable(String sessionId, AuthUser user, XViewModel xViewModel)
			throws SessionExpiredException, PaloGwtCoreException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		return viewController.proceedUpdateWithoutTable(user, xViewModel, getNumberFormat(sessionId));
	}

	public final XViewModel cancelUpdateView(String sessionId, AuthUser user, XViewModel xViewModel)
			throws SessionExpiredException, PaloGwtCoreException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		return viewController.cancelUpdateView(getUserSession(sessionId), user);
	}

	public void collapse(String sessionId, XAxisItem item, String axisId, String viewId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		viewController.collapse(item, axisId);
	}

	public XLoadInfo willExpand(String sessionId, XAxisItem item, String viewId,
			String axisId) throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.willExpand(item, axisId);
	}

	public XLoadInfo willSwapAxes(String sessionId, String viewId)
		throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.willSwapAxes();
	}
	
	public XDelta proceedExpand(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.proceedExpand(item, axisId, getNumberFormat(sessionId));
	}
	
	public XViewModel proceedSwapAxes(String sessionId, XViewModel xViewModel)
		throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		AuthUser user = getLoggedInUser(sessionId);
		return viewController.proceedSwapAxes(getUserSession(sessionId), user, getNumberFormat(sessionId), xViewModel);
	}

	public void cancelExpand(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		viewController.cancelExpand(item, axisId);
	}


	public XLoadInfo willCollapse(String sessionId, XAxisItem item, String viewId,
			String axisId) throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.willCollapse(item, axisId);
	}

	public void proceedCollapse(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		viewController.proceedCollapse();
	}

	public void cancelCollapse(String sessionId, XAxisItem item, String viewId, String axisId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		viewController.cancelCollapse(item, axisId);
	}
	
	public XLoadInfo willSetExpandState(String sessionId, XAxisItem[] expanded, XAxisItem[] collapsed, int expandDepth, String viewId, String axisId) throws SessionExpiredException {	
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.willSetExpandState(expanded, collapsed, expandDepth, axisId);
	}
	public XDelta[] proceedSetExpandState(String sessionId, String viewId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		return viewController.proceedSetExpandState(getNumberFormat(sessionId));
	}

	public void cancelSetExpandState(String sessionId, String viewId)
			throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, viewId);
		viewController.cancelSetExpandState();
	}

	public XLoadInfo willChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxisHierarchy) throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		return viewController.willChangeSelectedElement();
	}

	public XViewModel proceedChangeSelectedElement(String sessionId, XViewModel xViewModel,
			XAxisHierarchy xAxisHierarchy, XElement selectedElement) throws SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		try {
			return viewController.proceedChangeSelectedElement(getUserSession(sessionId), getLoggedInUser(sessionId), xViewModel,
					xAxisHierarchy, selectedElement, getNumberFormat(sessionId));
		} catch (PaloGwtCoreException e) {
			throw new SessionExpiredException(e.getMessage());
		}
	}


	public final synchronized XLoadInfo willReload(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			return viewController.willReloadView();
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}

	public final synchronized XViewModel proceedReload(String sessionId, AuthUser user, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			return viewController.proceedReloadView(getUserSession(sessionId), xViewModel, user, getNumberFormat(sessionId));
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}

	public final XLoadInfo willUpdateAxisHierarchy(String sessionId, XAxisHierarchy hierarchy)
			throws SessionExpiredException {
		try {
		String viewId = hierarchy.getViewId();
		CubeViewController viewController = CubeViewController.getController(
				sessionId, viewId);
		return viewController.willUpdate(hierarchy);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new SessionExpiredException("Session Expired.", t);
		}
	}
	public XElement update(String sessionId, XAxisHierarchy xAxisHierarchy)
			throws SessionExpiredException {
		String viewId = xAxisHierarchy.getViewId();
		CubeViewController viewController = CubeViewController.getController(
				sessionId, viewId);
		return viewController.update(xAxisHierarchy);
	}
	private final CubeViewController getControllerFor(String sessionId, String viewId)
			throws SessionExpiredException {
		return CubeViewController.getController(sessionId, viewId);
	}
//	private final CubeViewController getControllerFor(String viewId, String sessionId)
//		throws SessionExpiredException {
//		return CubeViewController.getController(sessionId, viewId);
//	}
	
	private synchronized final CubeViewController getControllerFor(String sessionId, XView xView, Account accountToUse)
			throws SessionExpiredException, PaloGwtCoreException, PaloIOException {
		UserSession userSession = getUserSession(sessionId);
		ViewService viewService = ServiceProvider.getViewService(userSession.getUser());		
		View view = viewService.getView(xView.getId());
		
		if (!accountToUse.getId().equals(view.getAccount().getId())) {
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				((ViewImpl) view).setAccount(accountToUse.getUser(), accountToUse, sessionId);				
			} finally {
				CubeViewReader.CHECK_RIGHTS = true;
				ConnectionPoolManager.getInstance().disconnect(accountToUse, sessionId, "CubeViewService.willOpenView");
			}
		}
		if (!accountToUse.getUser().hasPermission(Right.READ, view)) {
			throw new NoPermissionException(
					"User has no permission to open this view.", view,
					accountToUse.getUser(), Right.READ);
		}
		
//		Account acc = view.getAccount();		
//		for (Account usAc: getLoggedInUser().getAccounts()) {
//			if (acc.getConnection().getId().equals(usAc.getConnection().getId())) {
//				try {
//					((ViewImpl) view).setAccount(usAc);
//					break;
//				} catch (Throwable t) {					
//				}
//			}
//		}					
		return CubeViewController.getController(getLoggedInUser(sessionId), sessionId, view, getSession());
	}
	private final CubeViewController getControllerFor(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		return CubeViewController.getController(sessionId, xViewModel.getId());
	}
	
//	public final XLoadInfo willUpdate(String sessionId, XViewModel xViewModel)
//			throws SessionExpiredException {
//		try {
//			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
//			return viewController.willUpdate(xViewModel);
//		} catch (Throwable t) {
//			throw new SessionExpiredException("Session expired!");
//		}
//	}
	
	public XLoadInfo updateLoadInfo(String sessionId, XViewModel xViewModel, int cellsToDisplay)
		throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			return viewController.updateLoadInfo(xViewModel, cellsToDisplay);
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public final XViewModel proceedUpdate(String sessionId, AuthUser user, XViewModel xViewModel) throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			return viewController.proceedUpdate(getUserSession(sessionId), user, xViewModel, getNumberFormat(sessionId));
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public XViewModel save(String sessionId, XViewModel xViewModel) throws OperationFailedException, SessionExpiredException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);
		View view = viewController.updateCubeView(xViewModel);		
		UserSession userSession = getUserSession(sessionId);
		ViewService viewService = ServiceProvider.getViewService(userSession.getUser());
		if(view != null) {
			CubeView  cubeView = view.getCubeView();
			String xml = CubeViewIO.toXML(cubeView);
			
			JPaloSavingUtil jpaloUtil = new JPaloSavingUtil();

			String isSpagoBIDev = (String) getSession().getAttribute("isdeveloper");
			
			//SpagoBI User (executing document)
			if(isSpagoBIDev == null || isSpagoBIDev.equals("")){
				//System.out.println("USER");
				//logger.info("USER");
				jpaloUtil.saveSubobjectForJPalo(getSession(), view.getName(), view.getName()+ " View definitions/changes", xml);

			}else{
				//System.out.println("DEVELOPER");
				//logger.info("DEVELOPER");
				//SpagoBI Developer (editing detail)				
				//saving SpagoBI Template
				jpaloUtil.saveTemplateForJPalo(getSession(), view);
				//saving JPalo view
				viewService.save(view);				
			}

			try {
				return proceedOpenView(sessionId, getLoggedInUser(sessionId), view.getId());
			} catch (PaloGwtCoreException e) {
				e.printStackTrace();
				throw new SessionExpiredException("Rebuilding view model failed.");
			}
		}
		return null;
	}

	public synchronized XView saveAs(String sessionId, String name, XViewModel xViewModel)
			throws OperationFailedException, SessionExpiredException {
		ViewService viewService = ServiceProvider
				.getViewService(getLoggedInUser(sessionId));
		Cube cube = getCube(sessionId, xViewModel);
		View newView = viewService.createViewAsSubobject(name, cube, getLoggedInUser(sessionId), sessionId,
				xViewModel.getExternalId());		
		String currentXml = null;
		try {
			currentXml = getDefinition(sessionId, xViewModel);
		} catch (Throwable t) {
			if (t.getMessage() != null && t.getMessage().toLowerCase().indexOf("not enough rights") != -1) {
				return null;
			}
		}
		XView xview = null;
		if (currentXml != null) {
			viewService.setDefinition(currentXml, newView);
			// 	TODO remove this hack!! Problem: viewService.save() overwrites
			// any previously set definition!!!!
			Account acc = newView.getAccount();
			for (Account usAc: getLoggedInUser(sessionId).getAccounts()) {
				if (acc.getConnection().getId().equals(usAc.getConnection().getId())) {
					try {
						((ViewImpl) newView).setAccount(getLoggedInUser(sessionId), usAc, sessionId);
					} catch (PaloIOException e) {
						//return null;
						throw new OperationFailedException(e.getMessage(), e);
						
					}
					break;
				}
			}	
			
			try {				
				//newView.createCubeView(getLoggedInUser(sessionId), sessionId);
				JPaloSavingUtil jpaloUtil = new JPaloSavingUtil();

				String isSpagoBIDev = (String) getSession().getAttribute("isdeveloper");
				
				//SpagoBI User (executing document)
				if(isSpagoBIDev == null || isSpagoBIDev.equals("")){
					//System.out.println("USER");
					//logger.info("USER");
					jpaloUtil.saveSubobjectForJPalo(getSession(), name, newView.getName()+ " View definitions/changes", currentXml);
					
					return null;
				}else{
					//System.out.println("DEVELOPER");
					//logger.info("DEVELOPER");
					newView.createCubeView(getLoggedInUser(sessionId), sessionId);
					//SpagoBI Developer (editing detail)				
					//saving SpagoBI Template
					jpaloUtil.saveTemplateForJPalo(getSession(), newView);
					//saving JPalo view
					viewService.save(newView);	
					xview = (XView) XConverter.createX(newView);
				}
				//viewService.save(newView);
			} catch (PaloIOException e) {
				throw new OperationFailedException(e.getMessage(), e);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(newView.getAccount(), sessionId, "CubeViewService.saveAs");
			}
		}
		return xview;
		
	}
	
	private final String getDefinition(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException, Throwable {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			View view = viewController.updateCubeView(xViewModel);
			CubeView currentView = view.getCubeView();
			return currentView != null ? CubeViewIO.toXML(currentView) : null;
		} catch (Throwable t) {
			if (t.getMessage() != null && t.getMessage().toLowerCase().indexOf("not enough rights") != -1) {
				throw t;
			}
			throw new SessionExpiredException("Session expired!");
		}
	}
	private final Cube getCube(String sessionId, XViewModel xViewModel)
			throws SessionExpiredException {
		try {
			CubeViewController viewController = getControllerFor(sessionId, xViewModel);
			return viewController.getCube();
		} catch (Throwable t) {
			throw new SessionExpiredException("Session expired!");
		}
	}
	
	public void delete(String sessionId, XView xView) throws OperationFailedException,
			SessionExpiredException {
		ViewService viewService = ServiceProvider
				.getViewService(getLoggedInUser(sessionId));
		View view = viewService.getView(xView.getId());
		if (view != null)
			viewService.delete(view);
	}
	
	public XCellCollection writeCell(String sessionId, XCell cell, XViewModel xViewModel, NumberFormat format)
			throws SessionExpiredException {
		try {
			CubeViewController controller = getControllerFor(sessionId, xViewModel.getId()); 
//				getControllerFor(sessionId, xViewModel);
			AuthUser user = getLoggedInUser(sessionId);
			
			if (controller != null) {
				return controller.writeCell(cell, format, user);
			}
			return new XCellCollection(0);
		} catch (Throwable t) {
			throw new SessionExpiredException(t.getMessage());
		}
	}
	
	public void remove(String sessionId, XViewModel xViewModel) throws SessionExpiredException {
		if(xViewModel == null)
			return;
		String viewId = xViewModel.getId();
		UserSession userSession = getUserSession(sessionId);
		ViewService viewService = ServiceProvider.getViewService(userSession.getUser());
		View view = CubeViewController.getViewById(sessionId, viewId);
		if(view != null) {
			try {
				viewService.setDefinition(view.getDefinition(), view);
			} catch (NoPermissionException e) {
				// Ignore and don't modify the definition...
			}
		}
		CubeViewController.removeViewById(userSession, viewId);
	}
	
	public View convert(String sessionId, org.palo.api.CubeView legacyView, String newViewName)
			throws OperationFailedException, SessionExpiredException {
		return CubeViewConverter.toView(newViewName, legacyView,
				getLoggedInUser(sessionId), sessionId);
	}
//	public View createDefaultViewFor(String sessionId, Cube cube, String viewName, String accountId)
//			throws OperationFailedException, SessionExpiredException {
//		return CubeViewConverter.createDefaultView(viewName, cube, accountId,
//				getLoggedInUser(sessionId), sessionId);
//	}
	
	public void rename(String sessionId, XView xView, String newName)
			throws OperationFailedException, SessionExpiredException {
		ViewService viewService = ServiceProvider
				.getViewService(getLoggedInUser(sessionId));
		View view = viewService.getView(xView.getId());
		if (view != null) {
			viewService.setName(newName, view);
			viewService.save(view);
		}
	}
	
	private final void setOrRemoveProperty(CubeView view, String property, boolean set) {
		if (set) {
			view.addProperty(property, "true");
		} else {
			view.removeProperty(property);
		}
	}
	
	private final void updateProperties(CubeView cubeView, XViewModel model) {
		setOrRemoveProperty(cubeView, CubeView.PROPERTY_ID_HIDE_EMPTY, model.isHideEmptyCells());
		setOrRemoveProperty(cubeView, CubeView.PROPERTY_ID_SHOW_RULES, model.isShowRules());
		setOrRemoveProperty(cubeView, CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT, model.isColumnsReversed());
		setOrRemoveProperty(cubeView, CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT, model.isRowsReversed());
	}
	
	public final XPrintResult generatePdf(String sessionId, AuthUser user, XViewModel xViewModel, XPrintConfiguration config) 
		throws SessionExpiredException, PaloGwtCoreException {
		CubeViewController viewController = getControllerFor(sessionId, xViewModel);		
		try {	
			viewController.update(xViewModel);
			CubeView cubeView = viewController.getCubeView();		
			updateProperties(cubeView, xViewModel);
			PaloPDFExporterConfiguration conf =
				new PaloPDFExporterConfiguration();
			
			conf.setPageFormat(config.getPaperFormat());
			conf.setPortrait(config.getPaperOrientation() == XPrintConfiguration.PORTRAIT);
			
			conf.setTitle(config.getTitle());
			conf.setShowTitle(config.isShowTitle());
			conf.setShowPOV(config.isShowPOV());
			conf.setShowExpansionStates(config.isShowExpansionStateIcons());
			conf.setIndent(config.isShowExpansionStateIcons() == false ? config.isIndent() : true);
			conf.setShowPageNumbers(config.isPrintPageNumbers());
			conf.setMaxWidths(config.getMaxColString(),
						      config.getMaxRowsHeaderString(),
						      config.getCellReplaceString());
//			System.out.println("Path == " + getServletContext().getRealPath("/"));
			conf.setPath(getServletContext().getRealPath("/") + "com.tensegrity.wpalo.SpagoBIJPaloEngine/downloads/");
			File file = new File(getServletContext().getRealPath("/") + "com.tensegrity.wpalo.SpagoBIJPaloEngine/downloads/");
			if (!file.exists()) {
				file.mkdirs();
			}
			PaloPDFExporter exporter = new PaloPDFExporter(conf);
			exporter.export(cubeView);
			XViewModel view = proceedReload(sessionId, user, xViewModel);
//			System.err.println("Path to pdf: " + exporter.getOutFile().getAbsolutePath());
			return new XPrintResult(exporter.getOutFile().getAbsolutePath(), view);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new PaloGwtCoreException(t.getLocalizedMessage(), t);
		}
	}
}
