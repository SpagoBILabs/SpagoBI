/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.chart.bo.charttypes.utils;


import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.data.general.ValueDataset;


/** 
 *  @author Antonella Giachino (antonella.giachino@eng.it)
 */

public class MyDialPlot extends DialPlot {
	/**
    * An optional collection of legend items that can be returned by the
    * getLegendItems() method.
    */
	 private LegendItemCollection legendItems;
	 

	 
	/**
     * Returns the legend items for the plot.  By default, this method returns
     * <code>null</code>.  Subclasses should override to return a
     * {@link LegendItemCollection}.
     *
     * @return The legend items for the dial plot (possibly <code>null</code>).
     */
     public LegendItemCollection getLegendItems() {
    	return this.legendItems;
     }

	/**
	 * @param legendItems the legendItems to set
	 */
	public void setLegendItems(LegendItemCollection legendItems) {
		this.legendItems = legendItems;
	}

}
