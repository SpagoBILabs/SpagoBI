/*
*
* @file CubeViewHandlerLegacy.java
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
* @author ArndHouben
*
* @version $Id: CubeViewHandlerLegacy.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.persistence.PersistenceError;
import org.xml.sax.Attributes;

/**
 * <code>CubeViewHandlerLegacy</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: CubeViewHandlerLegacy.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewHandlerLegacy extends CubeViewHandler {
	private Dimension currDim;
	private String currDimId;
	private final String key;
	
	CubeViewHandlerLegacy(Database database, String key) {
		super(database);
		this.key = key;
	}
	
	protected void registerEndHandlers() {
		clearEndHandlers();
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/columns/column";
			}

			public void endElement(String uri, String localName, String qName) {
				handleDimensionEnd();
			}
		});
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/columns";
			}

			public void endElement(String uri, String localName, String qName) {
				handleAxisEnd();
			}
		});
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/rows/row";
			}

			public void endElement(String uri, String localName, String qName) {
				handleDimensionEnd();
			}
		});
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/rows";
			}

			public void endElement(String uri, String localName, String qName) {
				handleAxisEnd();
			}
		});
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/selects/select";
			}

			public void endElement(String uri, String localName, String qName) {
				handleDimensionEnd();
			}
		});
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/selects";
			}

			public void endElement(String uri, String localName, String qName) {
				handleAxisEnd();
			}
		});
	}

	protected final void registerStartHandlers() {
		clearStartHandlers();
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				CubeViewBuilder viewBuilder = new CubeViewBuilder();
				viewBuilder.setId(key);
				viewBuilder.setName(attributes.getValue("name"));
				viewBuilder.setDescription(XMLUtil.dequoteString(attributes
						.getValue("description")));
				String cubeId = attributes.getValue("cube");
				// I guess we get the wrong name here, it contains an @...
				cubeId = CubeViewReader.getLeafName(cubeId);
				viewBuilder.setCube(database.getCubeByName(cubeId));
				cubeView = viewBuilder.createView(CubeViewHandlerLegacy.this);
				// we abort loading if creation failed...
				if (cubeView == null)
					throw new PaloAPIException("CubeView creation failed!");

			}
		});
		// ------------ ROWS ------------------
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/rows";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				currAxis = cubeView.getAxis("rows");
				if (currAxis == null)
					currAxis = cubeView.addAxis("rows", "rows");
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/rows/row";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleAxisProperties(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/rows/row/hidden";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleHidden(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/rows/row/expanded";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleExpanded(attributes);
			}
		});

		// ------------ COLOUMNS ------------------
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/columns";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				currAxis = cubeView.getAxis("cols");
				if (currAxis == null)
					currAxis = cubeView.addAxis("cols", "columns");
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/columns/column";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleAxisProperties(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/columns/column/hidden";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleHidden(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/columns/column/expanded";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleExpanded(attributes);
			}
		});

		// ------------ SELECTED ------------------
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/selects";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				currAxis = cubeView.getAxis("selected");
				if (currAxis == null)
					currAxis = cubeView.addAxis("selected", "selects");
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/selects/select";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleAxisProperties(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/selects/select/hidden";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleHidden(attributes);
			}
		});
		registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/selects/select/expanded";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				handleExpanded(attributes);
			}
		});
	}

	private final void handleDimensionEnd() {
		currDim = null;
	}
	private final void handleAxisEnd() {
		currAxis = null;
	}
	
	private final void handleAxisProperties(Attributes attributes) {
		currDimId = attributes.getValue("dimension"); //$NON-NLS-1$
		String subsetName = attributes.getValue("subset"); //$NON-NLS-1$
		String selElement = attributes.getValue("selectedElement"); //$NON-NLS-1$
		boolean useSubset = Boolean
				.valueOf(attributes.getValue("useSubset")).booleanValue(); //$NON-NLS-1$

		currDimId = CubeViewReader.getLeafName(currDimId);
		currDim = database.getDimensionByName(currDimId);
		if (currDim == null) {
			addError("CubeViewHandlerLegacy: unknown dimension '" + currDimId
					+ "'!!", cubeView.getId(), cubeView, database, currDimId,					
					PersistenceError.UNKNOWN_DIMENSION,
					null, PersistenceError.TARGET_UNKNOWN);
			return;
		}
		currAxis.add(currDim);

		if (useSubset) {
			Subset activeSub = currDim.getSubset(subsetName);
			if (activeSub == null)
				activeSub = getSubsetByName(currDim, subsetName);
			currAxis.setActiveSubset(currDim, activeSub);
		} else
			currAxis.setActiveSubset(currDim, null);
		currAxis.setSelectedElement(currDim, currDim.getDefaultHierarchy().
				getElementByName(selElement));

	}
	
	private final void handleHidden(Attributes attributes) {
		if (currDim == null) {
			addError(
					"CubeViewHandlerLegacy: (hidden path) no dimension defined!!",
					cubeView.getId(), cubeView, database, currDimId,
					PersistenceError.UNKNOWN_DIMENSION,
					currAxis, PersistenceError.TARGET_HIDDEN_PATH);
			return;
		}
		String path = attributes.getValue("path");
		if (path == null) {
			addError("CubeViewHandlerLegacy: no hidden path defined!!",
					cubeView.getId(), cubeView, currAxis, path,
					PersistenceError.UNKNOWN_PATH,
					currAxis, PersistenceError.TARGET_HIDDEN_PATH);
			return;

		}
		Element[] elPath = getPath(path, currDim.getDefaultHierarchy(), currAxis,
				PersistenceError.TARGET_HIDDEN_PATH);
		currAxis.addHidden(currDim, elPath);

	}
	
	private final void handleExpanded(Attributes attributes) {
		if (currDim == null) {
			addError(
					"CubeViewHandlerLegacy: (expanded path) no dimension defined!!",
					cubeView.getId(), cubeView, database, currDimId,
					PersistenceError.UNKNOWN_DIMENSION,
					currAxis, PersistenceError.TARGET_EXPANDED_PATH);
			return;
		}
		String path = attributes.getValue("path");
		String reps = attributes.getValue("repetitions");
		int[] repetitions = CubeViewReader.getRepetitions(reps);
		if (path == null) {
			addError("CubeViewHandlerLegacy: no expanded path defined!!",
					cubeView.getId(), cubeView, currAxis, path,
					PersistenceError.UNKNOWN_PATH,
					currAxis, PersistenceError.TARGET_EXPANDED_PATH);
			return;
		}
		Element[] elPath = getPath(path, currDim.getDefaultHierarchy(), currAxis,
				PersistenceError.TARGET_EXPANDED_PATH);
		for (int i = 0; i < repetitions.length; ++i)
			currAxis.addExpanded(currDim, elPath, repetitions[i]);
	}
	
	private final Subset getSubsetByName(Dimension dim, String subName) {
		Subset[] subsets = dim.getSubsets();
		for (int i = 0; i < subsets.length; ++i) {
			if (subsets[i].getName().equals(subName))
				return subsets[i];
		}
		return null;
	}
	
}