/**
 * 
 * LICENSE: see LICENSE.txt file
 * 
 */
package com.tonbeller.jpivot.mondrian;

import com.tonbeller.jpivot.core.ExtensionSupport;

/**
 * This class is an extension to JPivot MondrianModel: it must be declared as
 * <code>&lt;extension id="crossNavigation" class="com.tonbeller.jpivot.mondrian.SpagoBICrossNavigation"/&gt;</code>
 * in file com.tonbeller.jpivot.mondrian.config.xml to take effect.
 * When user clicks on a cell, the cross navigation context menu is built according to the
 * <code>SpagoBICrossNavigationConfig</code> object in user session.
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 * @see com.tonbeller.jpivot.table.navi.SpagoBICrossNavigationUI
 *
 */
public class SpagoBICrossNavigation extends ExtensionSupport {

  public static final String ID = "crossNavigation";
	
  /**
   * Constructor sets ID
   */
  public SpagoBICrossNavigation() {
	  super.setId(ID);
  }
  
}