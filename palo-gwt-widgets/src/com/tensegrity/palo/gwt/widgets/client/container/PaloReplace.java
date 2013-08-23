/*
*
* @file PaloInsert.java
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
* @version $Id: PaloReplace.java,v 1.2 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.container;

import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class PaloReplace extends BoxComponent {

	  private int height = -1;
	  
	  public PaloReplace(int height) {
	    setShadow(false);
	    this.height = height;
	    RootPanel.get().add(this);
	    setTitle("PaloReplace");
	  }

	  public void remove() {
		  RootPanel.get().remove(this);
	  }
	  
	  public void setInsertHeight(int newHeight) {
		  height = newHeight;
	  }
	  
	  public int getInsertHeight() {
		  return height;
	  }
	  
	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append("<img src=\"images/replace-bg.png\"/>");
//	    String h = "" + (height == -1 ? 6 : height);
//	    sb.append("<table class=x-insert-bar height=" + h + " cellspacing=0 cellpadding=0><tbody><tr>");
//	    sb.append("<td height=" + h + " class=x-insert><div style='width: 3px'></div></td>");
//	    sb.append("<td class=x-insert width=100%>&nbsp;</td>");
//	    sb.append("<td class=x-insert><div style='width: 3px'></div></td>");
//	    sb.append("</tr></tbody></table>");

	    setElement(XDOM.create(sb.toString()), target, index);

	    el().setVisible(false);
	  }

	}
