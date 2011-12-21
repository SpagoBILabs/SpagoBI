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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * <code>EnhancedComboBox</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: EnhancedComboBox.java,v 1.3 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class EnhancedComboBox <T extends ModelData> extends ComboBox<T> {
	
	  public void select(int index) {
		  //PR 544: BUG IN GXT? it automatically selects next element, so we simply take first one ;)
		  if(index > 0) index--;
		  super.select(index);
	  }
}
