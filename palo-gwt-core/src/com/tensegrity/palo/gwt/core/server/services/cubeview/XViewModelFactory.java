/*
*
* @file XViewModelFactory.java
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
* @version $Id: XViewModelFactory.java,v 1.6 2010/03/02 08:58:27 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.axis.AxisModel;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

/**
 * <code>XViewModelFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XViewModelFactory.java,v 1.6 2010/03/02 08:58:27 PhilippBouillon Exp $
 **/
class XViewModelFactory {
	
	static final XViewModel createX(ViewModel viewModel, String id, UserSession userSession) {
		XViewModel xView = new XViewModel(id, viewModel.getName());

		createRowAxis(viewModel, xView, userSession);
		createColumnAxis(viewModel, xView, userSession);
		createRepositoryAxis(viewModel, xView, userSession);
		createSelectionAxis(viewModel, xView, userSession);
		return xView;
	
	}

	private static final void createRowAxis(ViewModel viewModel,
			XViewModel xView, UserSession userSession) {
		AxisModel rowAxis = viewModel.getRowAxis();
		XAxis xRowAxis = XAxisFactory.createX(viewModel.getRowAxis().getAxis(),xView.getId(),userSession);
		xView.addRowAxis(xRowAxis);
		List<XAxisItem> rowRoots = createRootAxisItems(rowAxis, xRowAxis);
		for (XAxisItem root : rowRoots)
			xView.addRowRoot(root);
	}

	private static final void createColumnAxis(ViewModel viewModel,
			XViewModel xView, UserSession userSession) {
		AxisModel colAxis = viewModel.getColumnAxis();
		XAxis xColAxis = XAxisFactory.createX(viewModel.getColumnAxis().getAxis(),xView.getId(),userSession);
		xView.addColumnAxis(xColAxis);
		List<XAxisItem> colRoots = createRootAxisItems(colAxis, xColAxis);
		for (XAxisItem root : colRoots)
			xView.addColumnRoot(root);
	}

	private static final List<XAxisItem> createRootAxisItems(AxisModel axis,
			XAxis xAxis) {
		List<XAxisItem> roots = new ArrayList<XAxisItem>();
		for (AxisItem root : axis.getRoots()) {
			XAxisItem xRoot = XAxisItemFactory.createXRoot(root);
			roots.add(xRoot);
		}
		return roots;
	}
	
	private static final void createRepositoryAxis(ViewModel viewModel, XViewModel xView, UserSession userSession) {
		XAxis repository = XAxisFactory.createX(viewModel.getRepositoryAxis(),xView.getId(),userSession);
		xView.addRepositoryAxis(repository);
	}

	private static final void createSelectionAxis(ViewModel viewModel, XViewModel xView, UserSession userSession) {
		XAxis selection = XAxisFactory.createX(viewModel.getSelectionAxis(),xView.getId(),userSession);
		xView.addPovAxis(selection);
	}
}
