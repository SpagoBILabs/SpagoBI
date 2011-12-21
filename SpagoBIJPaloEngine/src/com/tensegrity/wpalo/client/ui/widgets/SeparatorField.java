/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
