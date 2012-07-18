/*
*
* @file SeparatorField.java
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
* @version $Id: SeparatorField.java,v 1.2 2009/12/17 16:14:21 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * <code>SeparatorField</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: SeparatorField.java,v 1.2 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class SeparatorField extends Field {

	public SeparatorField() {
		setLabelSeparator("");
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void markInvalid(String msg) {

	}

	public void setFieldLabel(String fieldLabel) {
		if (rendered) {
			El elem = el().findParent(".x-form-item", 5);
			if (elem != null) {
				elem = elem.firstChild();
				if (elem != null) {
					elem.setInnerHtml("<hr>"); //fieldLabel + labelSeparator);
				}
			}
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		Element sep = DOM.createElement("sep");
		sep.setInnerHTML("<hr>");
		setElement(sep, parent, index);
	}

	@Override
	protected boolean validateValue(String value) {
		return true;
	}
}
