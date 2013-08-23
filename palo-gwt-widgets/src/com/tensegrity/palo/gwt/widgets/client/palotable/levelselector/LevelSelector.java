/*
*
* @file LevelSelector.java
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
* @version $Id: LevelSelector.java,v 1.6 2010/02/16 13:54:10 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable.levelselector;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.widgets.client.i18n.ILocalConstants;
import com.tensegrity.palo.gwt.widgets.client.i18n.Resources;
import com.tensegrity.palo.gwt.widgets.client.separator.Separator;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>LevelButtonPanel</code> TODO DOCUMENT ME
 * 
 * @version $Id: LevelSelector.java,v 1.6 2010/02/16 13:54:10 PhilippBouillon Exp $
 **/
public abstract class LevelSelector extends Composite {
	protected final ILocalConstants constants = Resources.getInstance().getConstants();

	protected static final int SPACING = 2;
	protected static final int ICON_SIDE_X = 15;
	protected static final int ICON_SIDE_Y = 13;

	private XAxis axis;
	protected Label[] selectors;
	protected Separator[] separators;
	protected final AbsolutePanel content = new AbsolutePanel();	
	private final List<LevelSelectorListener> listeners = new ArrayList<LevelSelectorListener>();

	
	public LevelSelector() {
		initWidget(content);
	}


	public final void addListener(LevelSelectorListener listener) {
		removeListener(listener);
		listeners.add(listener);
	}
	public final void removeListener(LevelSelectorListener listener) {
		listeners.remove(listener);
	}
	
	public final void reset() {
		removeSelectors();
		removeSeparators();
	}
	
	public final Separator getSeparator(int forLevel) {
		return separators[forLevel];
	}
	public final Label getSelectorIcon(int forLevel) {
		return selectors[forLevel];
	}
	
	public final void setAxis(XAxis axis) {
		this.axis = axis;
		init();
	}

	public abstract Point layout(int level, int atX, int atY);
	protected abstract Separator createSeparator();
	
	private XAxisHierarchy getAxisHierarchyAt(int index) {
		return axis.getAxisHierarchies().get(index);
	}
	private int getHierarchyCount() {
		if(axis == null)
			return 0;
		return axis.getAxisHierarchies().size();
	}


	private final void init() {
		initComponents();
		initEventHandling();
	}
	private final void initComponents() {		
		createSelectors();
		addSelectors();
		createSeparators();
		addSeparators();
	}

	private final void initEventHandling() {
		// events:
		for(Label selector : selectors)
		selector.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				popUpLevelSelection(sender);
			}
		});
	}
	private final void popUpLevelSelection(final Widget selector) {
		final SelectorIcon sel = (SelectorIcon) selector;
		XAxisHierarchy axisHierarchy = getAxisHierarchyAt(sel.index);
		int levels = getLevels(axisHierarchy);
		LevelSelectorPopup popup = new LevelSelectorPopup(levels);
		popup.addPopupListener(new PopupListener() {
			public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
				int selectedLevel = 
					((LevelSelectorPopup) sender).getSelectedLevel();
				if(selectedLevel > -1) {
					fireClick(sel.index, selectedLevel);
				}
			}
		});
		popup.show(selector);
	}
	private final int getLevels(XAxisHierarchy axisHierarchy) {
		if (axisHierarchy.getActiveSubset() != null
				|| axisHierarchy.getVisibleElements() != null)
			return 9;
		return axisHierarchy.getMaxDepth() + 1;
	}
	private final void fireClick(int hierarchy, int level) {
		for(LevelSelectorListener listener : listeners)
			listener.selected(hierarchy, level);
	}
	private final void createSelectors() {
		int hierarchyCount = getHierarchyCount();
		selectors = new Label[hierarchyCount];
		for (int i = 0; i < hierarchyCount; ++i) {
			SelectorIcon selector = new SelectorIcon();
			selector.setStyleName("icon-level-select");
//			selector.setUrl("images/selectLevel.gif");
			selector.setTitle(constants.chooseExpandLevel());
			selector.index = i;
			selectors[i] = selector;
		}
	}
	private final void addSelectors() {
		for(Label selector : selectors)
			content.add(selector);
	}
	private final void removeSelectors() {
		if(selectors != null) {
			for(Label selector : selectors) {
				content.remove(selector);
				selector.removeFromParent();
			}				
		}
		selectors = null;
	}
	
	private final void createSeparators() {
		separators = new Separator[getHierarchyCount()];
		for (int i = 0; i < separators.length; ++i)
			separators[i] = createSeparator();
	}

	private final void addSeparators() {
		for (Separator sep : separators)
			content.add(sep);
	}
	private final void removeSeparators() {
		if (separators != null) {
			for (Separator sep : separators) {
				content.remove(sep);
				sep.removeFromParent();
			}
		}
		separators = null;
	}
}
