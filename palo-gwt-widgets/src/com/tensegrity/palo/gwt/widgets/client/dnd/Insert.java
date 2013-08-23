/*
*
* @file Insert.java
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
* @version $Id: Insert.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.dnd;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A custom component used to show insert locations with drop targets.
 */
public class Insert extends Widget {

  private static Insert instance;

  public static Insert get() {
    if (instance == null) {
      instance = new Insert();
      RootPanel.get().add(instance);
    }
    return instance;
  }

  Insert() {
  }

  public static Element create(String html) {
	    Element div = DOM.createDiv();
	    DOM.setInnerHTML(div, html);
	    Element firstChild = DOM.getFirstChild(div);
	    // support text node creation
	    return (firstChild != null) ? firstChild : div;
	  }

  public void render() {
    StringBuffer sb = new StringBuffer();
    sb.append("<table class=x-insert-bar height=6 cellspacing=0 cellpadding=0><tbody><tr>");
    sb.append("<td height=6 class=x-insert-left><div style='width: 3px'></div></td>");
    sb.append("<td class=x-insert-mid width=100%>&nbsp;</td>");
    sb.append("<td class=x-insert-right><div style='width: 3px'></div></td>");
    sb.append("</tr></tbody></table>");

    getElement().setInnerHTML(create(sb.toString()).getInnerHTML());
  }

}
