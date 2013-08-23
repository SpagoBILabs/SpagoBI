/*
*
* @file HeaderItem.java
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
* @version $Id: HeaderItem.java,v 1.35 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.widgets.client.palotable.Content;
import com.tensegrity.palo.gwt.widgets.client.palotable.ExpandListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.MouseClickListener;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;

/**
 * <code>HeaderItem</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HeaderItem.java,v 1.35 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public class HeaderItem extends Composite {
	
	//STYLE:
	private static final String STYLE = "item";
	private static final String STYLE_CAPTION = "item-caption";
	public static final String STYLE_LEFT_BORDER = "item-left-border";
	public static final String STYLE_RIGHT_BORDER = "item-right-border";
	public static final String STYLE_TOP_BORDER = "item-top-border";
	public static final String STYLE_BOTTOM_BORDER = "item-bottom-border";
	private static final int SPACING = 2;
	//ICON STYLES:	
	private static final String PLUS_ICON = "item-icon-plus";
	private static final String MINUS_ICON = "item-icon-minus";
	private static final String MENU_ICON = "item-icon-menu";
	
	//an header item consists of an	
	private Label icon;
	private Label menuIcon;
	private final Label caption = new Label();
	private final AbsolutePanel content = new AbsolutePanel();
	
	//tree hierarchy:	
	private final List<HeaderItem> children = new ArrayList<HeaderItem>();
	private final List<HeaderItem> rootsInNextLevel = new ArrayList<HeaderItem>();
	private final HeaderItem parentInPreviousLevel;
	
	private ExpandListener expandListener;
	private MouseClickListener clickListener;
	
	/** the underlying model item: */
	private final XAxisItem item;

	private boolean hidden;
	private final Point innerSize = new Point();
	private final Point initialSize = new Point();
	private int captionXPos;
	private int iconYPos;
	private final boolean isVertical;
	
	public HeaderItem(XAxisItem item, HeaderItem parentInPreviousLevel, boolean isVertical) {
		this.isVertical = isVertical;
		this.item = item;
		this.parentInPreviousLevel = parentInPreviousLevel;
		init();
	}
		
	public final void clear() {
		children.clear();
		rootsInNextLevel.clear();
		//physical detach:
		if(icon != null)
			content.remove(icon);
		if (menuIcon != null) {
			content.remove(menuIcon);
		}
		content.remove(caption);
	}
	public final boolean hasChildren() {
		return item.hasChildren();
	}
	
	public final HeaderItem getParentInPreviousHierarchy() {
		return parentInPreviousLevel;
	}
	
	public final boolean isHidden() {
		return hidden;
	}
	public final void hide(boolean doIt) {
		hidden = doIt;
	}
	
    public void setVisible(boolean visible) {
    	super.setVisible(visible);
    }
	
	public void onAttach() {
		super.onAttach();
	}
	
	public final void addChild(HeaderItem child) {
		if(!children.contains(child))
			children.add(child);
	}
	
	public final List<HeaderItem> getChildren() {
		return children;
	}
	public final void removeChildren() {
		for(HeaderItem child : children)
			child.clear();
	}
	
	public final boolean hasRootsInNextLevel() {
		return !rootsInNextLevel.isEmpty();
	}
	public final HeaderItem getFirstRootInNextLevel() {	
		return hasRootsInNextLevel() ? rootsInNextLevel.get(0) : null;
	}
	public final List<HeaderItem> getRootsInNextLevel() {
		return rootsInNextLevel;
	}
	public final void addRootInNextLevel(HeaderItem root) {
		if(root != null)
			rootsInNextLevel.add(root);
	}
	public final void setIsExpanded(boolean expanded) {
		if(item.hasChildren()) {
			item.isExpanded = expanded;
			String styleClass = expanded ? MINUS_ICON : PLUS_ICON;
			DOM.setElementProperty(icon.getElement(), "className", styleClass);
			icon.getElement().setAttribute("border", "0");
		}
	}
	
	public final String getName() {
		return item.getName();
	}
	
	public final String getPath() {
		return item.getPath();
	}
	
	public final boolean isExpanded() {
		return item.isExpanded;
	}
	
	public final boolean isLoaded() {
		return item.isLoaded;
	}
	
	public final void setIsLoaded(boolean isLoaded) {
		item.isLoaded = isLoaded;
	}
	
	public final int getLeafIndex() {
		return item.leafIndex;
	}
	
	public final void setLeafIndex(int index) {
		item.leafIndex = index;
	}
	
	public final int getLevel() {
		return item.level;
	}
	public final int getDepth() {
		return item.depth;
	}
	
	public final void register(ExpandListener expandListener) {
		this.expandListener = expandListener;
	}
	
	public final void register(MouseClickListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public String toString() {
		return getName() + "[" + getPath() + "]";
	}

	public final XAxisItem getModel() {
		return item;
	}
	
	protected void onLoad() {
		super.onLoad();
		layout();
	}

	private final void init() {
		initWidget(content);
		item.leafIndex = -1;
		//icon?
		if(item.hasChildren())
			createAndAddIcon();
		createAndAddMenuIcon();
		
		//caption:
		content.add(caption);
		
		caption.setText(item.getName());
		caption.setTitle(item.getName());
		
		setIsExpanded(item.isExpanded);
		
		//styles:
		setStyleName(STYLE);
		caption.setStyleName(STYLE_CAPTION);
//		caption.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				fireClickEvent();
//			}
//		});
	}
	
	public final int getInnerWidth() {
		return innerSize.x;
	}
	
//	public final int getLargestInnerWidth() {
//		int largest = innerSize.x;
//		HeaderItem hi = this;
//		while (hi.parentInPreviousLevel != null) {
//			hi = hi.parentInPreviousLevel;
//			if (hi.getInnerWidth() > largest) {
//				largest = hi.getInnerWidth();
//			}
//		}
//		return largest;
//	}
	
	public final int getInnerHeight() {
		return innerSize.y;
	}
//	public final Point getInnerSize() {
//	return new Point(innerSize.x, innerSize.y);
//}

	public final void setInnerHeight(int h) {
		innerSize.y = h;
		Ruler.setClientHeight(this, h);
	}
	public final void setInnerWidth(int w) {
		setInnerWidth(w, false);
	}
	//TODO this should go to layouter!!!
	public final void setInnerWidth(int w, boolean adjustParentInPrevLvl) {
		innerSize.x = w;
		Ruler.setClientWidth(this, w);
		if (adjustParentInPrevLvl) {
			// adjust parent in previous hierarchy:
			if (parentInPreviousLevel != null) {
				int parentWidth = parentInPreviousLevel.getInnerWidth(); //Size().x;
				if (w > parentWidth)
					parentInPreviousLevel.setInnerWidth(w,adjustParentInPrevLvl);
			}
		}
	}
	
	protected final void shorten(Label name, int max) {
		if (max < 0) {
			max = 5;
		}
		String txt = name.getText();
		if (txt.length() > 40) {
			txt = txt.substring(0, 40);
		}
		name.getElement().setInnerHTML("<div>" + txt + "</div>");
		// TODO is there a better way to do this??
		boolean modify = false;
		
		if (UserAgent.getInstance().isIE) {
			name.setWidth(max + "px");
		}
		while (name.getOffsetWidth() > max && txt.length() > 2) {			
			txt = txt.substring(0, txt.length() - 1);
			name.getElement().setInnerHTML("<div>" + txt + "..." + "</div>");
			modify = true;
		}
		if (modify) {
			txt += "...";
		}
		name.setText(txt);
	}		
		
	public final void layout() {
		int iconW = item.hasChildren() ? 11 : 0;
		int captionW = caption.getOffsetWidth();		
		if (isVertical) {
			int adjust = VerticalLayouter.INDENT - 12; 
			if (captionW > (Content.MAX_ROWS_COL_WIDTH - 24 - SPACING - SPACING - 7 + item.depth * adjust)) {
				shorten(caption, Content.MAX_ROWS_COL_WIDTH - 24 - SPACING - SPACING - 7 + item.depth * adjust);
				captionW = caption.getOffsetWidth();
			}
		} else {
			if (captionW > (Content.MAX_COLUMN_WIDTH - 16 - SPACING - SPACING - 7)) {
				shorten(caption, Content.MAX_COLUMN_WIDTH - 16 - SPACING - SPACING - 7);
				captionW = caption.getOffsetWidth();
			}			
		}
		int captionH = caption.getOffsetHeight();
		int height = 11 + /*Math.max(iconW, captionH) +*/ 2 * SPACING;
		int width = captionW + 2 * SPACING;
		int xPos = SPACING;
		if(item.hasChildren()) {
			width += iconW + SPACING;
			xPos += iconW + SPACING;
			Ruler.setPosition(icon, SPACING, (height-iconW)/2);
			iconYPos = (height - iconW) / 2;
		} 
		// MenuIcon
		width += 7 + SPACING;
		int menuIconPos = width - 9;
		Ruler.setPosition(menuIcon, menuIconPos, (height - 11) / 2);
		
		setInnerWidth(width + SPACING);
		setInnerHeight(height + SPACING);
		Ruler.setPosition(caption, xPos, 1); //SPACING);
		captionXPos = xPos;
		
		initialSize.x = width + SPACING;
		initialSize.y = height + SPACING;
	}
	
	public final void placeMenuIcon(int width) {
		Ruler.setPosition(menuIcon, width - 7, (initialSize.y - SPACING - 11) / 2);
	}
	
	public final void resetWidth(boolean isReverse) {
		setInnerWidth(initialSize.x);
		if (UserAgent.getInstance().isGecko) {
			if (item.hasChildren()) {
				if (isReverse) {
					Ruler.setPosition(icon, SPACING + 1, iconYPos);
				} else {
					Ruler.setPosition(icon, SPACING, iconYPos);
				}
			}
			if (isReverse) {
				Ruler.setPosition(caption, captionXPos + 1, 1); //SPACING);
			} else {
				Ruler.setPosition(caption, captionXPos, 1); //SPACING);
			}			
		}
	}
	public final void resetHeight(boolean isReverse) {
		setInnerHeight(initialSize.y);
		if (UserAgent.getInstance().isGecko) {
			if (item.hasChildren()) {
				if (isReverse) {
					Ruler.setPosition(icon, SPACING, iconYPos + 1);
				} else {
					Ruler.setPosition(icon, SPACING, iconYPos);
				}
			}
			if (isReverse) {
				Ruler.setPosition(caption, captionXPos, 2); //SPACING);
			} else {
				Ruler.setPosition(caption, captionXPos, 1); //SPACING);
			}
		}
	}

	private final void createAndAddIcon() {
		icon = new Label();		
		content.add(icon);
		//style:
		DOM.setElementProperty(icon.getElement(), "className", PLUS_ICON);
		icon.getElement().setAttribute("border", "0");
		//events:
		icon.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
//				setIsExpanded(!item.isExpanded);
				fireExpandEvent(!item.isExpanded);
			}
		});
	}
	
	private final void createAndAddMenuIcon() {
		menuIcon = new Label();
		content.add(menuIcon);
		DOM.setElementProperty(menuIcon.getElement(), "className", MENU_ICON);
		menuIcon.getElement().setAttribute("border", "0");
		menuIcon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireClickEvent(menuIcon.getAbsoluteLeft() + 5,
						       menuIcon.getAbsoluteTop() + 5);				
			}
		});
	}
	
	private final void fireExpandEvent(boolean expand) {
		if(expandListener != null) {
			if(expand)
				expandListener.willExpand(this);
			else
				expandListener.willCollapse(this);
		}
	}
	
	private final void fireClickEvent(int x, int y) {
		if (clickListener != null) {
			clickListener.leftClicked(this, x, y);
		}
	}
//	private final void fireExpandEvent() {
//		if(expandListener != null) {
//			if(item.isExpanded)
//				expandListener.expanded(this);
//			else
//				expandListener.collapsed(this);
//		}
//	}
}
 