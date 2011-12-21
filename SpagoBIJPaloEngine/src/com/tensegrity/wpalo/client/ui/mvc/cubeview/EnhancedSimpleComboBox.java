/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

/**
 * <code>EnhancedComboBox</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: EnhancedSimpleComboBox.java,v 1.1 2010/03/02 08:59:12 PhilippBouillon Exp $
 **/
public class EnhancedSimpleComboBox <T> extends SimpleComboBox<T> {
	
	  public void select(int index) {
		  //PR 544: BUG IN GXT? it automatically selects next element, so we simply take first one ;)
		  if(index > 0) index--;
		  super.select(index);
	  }
}
