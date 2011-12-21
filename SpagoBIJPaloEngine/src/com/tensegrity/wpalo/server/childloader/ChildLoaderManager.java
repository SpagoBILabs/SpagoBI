/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import java.util.ArrayList;

import com.tensegrity.palo.gwt.core.client.exceptions.PaloGwtCoreException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.server.childloader.reportstructure.DatabaseLoader;
import com.tensegrity.wpalo.server.childloader.reportstructure.HierarchyLoader;
import com.tensegrity.wpalo.server.childloader.reportstructure.ReportFolderLoader;
import com.tensegrity.wpalo.server.childloader.reportstructure.ServerLoader;
import com.tensegrity.wpalo.server.childloader.reportstructure.SubsetLoader;

public class ChildLoaderManager {
	private static final ChildLoaderManager instance = new ChildLoaderManager();
	private ArrayList <ChildLoader> childLoaders;
	private final ElementNodeLoader elNodeLoader;
	
	private ChildLoaderManager() {
		childLoaders = new ArrayList<ChildLoader>();

		// Register all different child loaders:
		childLoaders.add(new AccountViewLoader());
		childLoaders.add(new FolderChildLoader());
		childLoaders.add(new ReportFolderChildLoader());
		childLoaders.add(new ServerLoader());
		childLoaders.add(new DatabaseLoader());
		childLoaders.add(new HierarchyLoader());
		childLoaders.add(new SubsetLoader());
		childLoaders.add(new NodeChildLoader());
		childLoaders.add(new AdminChildLoader());
		childLoaders.add(new ReportFolderLoader());
		childLoaders.add(new AccountConnectionChildLoader());
		childLoaders.add(new ModellerChildLoader());
		childLoaders.add(new ApplicationLoader());
		childLoaders.add(new ApplicationTemplateLoader());
		elNodeLoader = new ElementNodeLoader();
		childLoaders.add(elNodeLoader);
//		childLoaders.add(new ViewItemLoader());
		childLoaders.add(new ViewBrowserTreeLoader());
		childLoaders.add(new ViewImporterTreeLoader());
	}
	
	public static ChildLoaderManager getInstance() {
		return instance;
	}
		
	public XObject[] loadChildren(XObject parent, UserSession userSession)
			throws PaloGwtCoreException {
		try {
			if (parent == null) {
				return new XObject[0];
			}
			for (ChildLoader cl : childLoaders) {
				if (cl.accepts(parent)) {
					XObject [] result =
						cl.loadChildren(parent, userSession);
					return result;
				}
			}
			return new XObject[0];
		} catch (Throwable t) {
			throw new PaloGwtCoreException(t.getLocalizedMessage(), t);
		}
	}
	
	public XObject [] loadChildren(String parentType, String viewId, String axisId, String parentId, UserSession userSession)
			throws PaloGwtCoreException {
		try {
			if (parentId == null || parentType == null) {
				return new XObject[0];
			}
			if (elNodeLoader.accepts(parentType)) {
				return elNodeLoader.loadChildren(parentType, viewId, axisId, parentId, userSession);
			}
			return new XObject[0];
		} catch (Throwable t) {
			throw new PaloGwtCoreException(t.getLocalizedMessage(), t);
		}
	}	
}