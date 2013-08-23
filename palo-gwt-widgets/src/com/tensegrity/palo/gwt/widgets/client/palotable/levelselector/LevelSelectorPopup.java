/*
*
* @file LevelSelectorPopup.java
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
* @version $Id: LevelSelectorPopup.java,v 1.6 2010/02/16 13:54:10 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable.levelselector;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.i18n.ILocalConstants;
import com.tensegrity.palo.gwt.widgets.client.i18n.Resources;

/**
 * <code>LevelSelectorPopup</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: LevelSelectorPopup.java,v 1.6 2010/02/16 13:54:10 PhilippBouillon Exp $
 **/
public class LevelSelectorPopup extends PopupPanel {
	protected final ILocalConstants constants = Resources.getInstance().getConstants();
	
	private static final String STYLE = "level-selector-popup";
	private static final String CAPTION_STYLE = "caption";
	
	protected static final int ICON_SIDE = 11;

	protected Label[] icons;
	private final String caption = constants.expandCompleteLevelInAllRepetitions();	
	private final VerticalPanel content = new VerticalPanel();
	private final HorizontalPanel levelPanel = new HorizontalPanel();

	private int selectedLevel = -1;
	
	public LevelSelectorPopup(int levels) {
		super(true);
		initComponent(levels);
		initEventHandling();
	}
	
	public final int getSelectedLevel() {
		return selectedLevel;
	}
	
	private final void initComponent(int levelCount) {
		HTML message = new HTML(caption);
		message.setStyleName(CAPTION_STYLE);
		content.add(message);
//		addTestButton();
		createIcons(levelCount);
		addIcons();
		levelPanel.setSpacing(4);
		content.add(levelPanel);
		content.setStyleName(STYLE);
		setWidget(content);
	}
	
	private final void createIcons(int levelCount) {
		if (levelCount > 9)
			levelCount = 9;
		icons = new Label[levelCount];
		for (int i = 0; i < levelCount; ++i) {
			SelectorIcon icon = new SelectorIcon();
			icon.setStyleName("icon-level-select-" + (i + 1));
			icon.index = i;
			icons[i] = icon; 
		}
	}

	private final void addIcons() {
		// add all icons:
		for (Label icon : icons)
			levelPanel.add(icon);
	}

	private final void initEventHandling() {
		// events:
		for (Label icon : icons)
			icon.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {					
					SelectorIcon icon = (SelectorIcon) sender;
					selectedLevel = icon.index;
					hide();
				}
			});
	}

	public final void show(Widget widget) {		
		int left = widget.getAbsoluteLeft();
		int top = widget.getAbsoluteTop();
		int width = widget.getOffsetWidth();
		setPopupPosition(left + width + 2, top);
	    show();
	}	
}
