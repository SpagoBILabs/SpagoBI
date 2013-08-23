/*
*
* @file DataFilter.java
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
* @author ArndHouben
*
* @version $Id: DataFilter.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.filter.settings.DataCriteria;
import org.palo.api.subsets.filter.settings.DataFilterSetting;

/**
 * <code>DataFilter</code>
 * <p>
 * A data filter is a restrictive filter as well as an affective filter.
 * It is restrictive in the sense that elements which go into the subset can be 
 * filtered out based on their corresponding cell values of a selected source 
 * cube. It is affective in the sense that this filter can influence the 
 * {@link SortingFilter}.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: DataFilter.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public class DataFilter extends AbstractSubsetFilter implements RestrictiveFilter, EffectiveFilter {

	
	private final int[] effectiveFilters = new int[]{TYPE_SORTING};
	private final DataFilterSetting setting;
	private final HashMap<Element, DataCell> dataElements = 
		new HashMap<Element, DataCell>();
	
	/**
	 * Creates a new <code>DataFilter</code> instance for the given 
	 * dimension
	 * @param dimension the dimension to create the filter for
	 * @deprecated use {@link DataFilter(Hierarchy, String)} instead.
	 */
	public DataFilter(Dimension dimension, String sourceCube) {
		this(dimension.getDefaultHierarchy(), new DataFilterSetting(sourceCube));
	}

	/**
	 * Creates a new <code>DataFilter</code> instance for the given 
	 * hierarchy
	 * @param hierarchy the hierarchy to create the filter for
	 */
	public DataFilter(Hierarchy hierarchy, String sourceCube) {
		this(hierarchy, new DataFilterSetting(sourceCube));
	}
	
	/**
	 * Creates a new <code>DataFilter</code> instance for the given 
	 * dimension with the given settings
	 * @param dimension the dimension to create the filter for
	 * @param setting the filter settings to use
	 * @deprecated use {@link DataFilter(Hierarchy, DataFilterSetting)} instead.
	 */
	public DataFilter(Dimension dimension, DataFilterSetting setting) {
		super(dimension.getDefaultHierarchy());
		this.setting = setting;
	}

	/**
	 * Creates a new <code>DataFilter</code> instance for the given 
	 * hierarchy with the given settings
	 * @param hierarchy the hierarchy to create the filter for
	 * @param setting the filter settings to use
	 */
	public DataFilter(Hierarchy hierarchy, DataFilterSetting setting) {
		super(hierarchy);
		this.setting = setting;
	}

	public final DataFilter copy() {
		DataFilter copy = 
				new DataFilter(hierarchy, setting.getSourceCube().getValue());
		copy.getSettings().adapt(setting);
		return copy;
	}

	public final int getType() {
		return TYPE_DATA;
	}

	
	public final void initialize() {
	}

	
	public final DataFilterSetting getSettings() {
		return setting;
	}


	
	public final void filter(Set<Element> elements) {
		if(elements.isEmpty())
			return; //nothing to filter ;)
		
		//we first check source cube, because without it this filter doesn't work...
		String srcCubeId = setting.getSourceCube().getValue();
		if(srcCubeId == null || srcCubeId.equals(""))
			return;
		Database database = hierarchy.getDimension().getDatabase();
		Cube srcCube = database.getCubeById(srcCubeId);
		if(srcCube == null)
			return;
		
		//setting.getSubCube()
		String[][] slice = setting.getSlice();
		
		int dimIndex = getDimensionIndex(srcCube);
		DataCell[] dataCells = new DataCell[elements.size()];
		ArrayList<Element[]> coordinates = new ArrayList<Element[]>();
		int index = 0;
		ArrayList <Hierarchy> allHiers = new ArrayList<Hierarchy>();
		for (Dimension d: srcCube.getDimensions()) {
			allHiers.addAll(Arrays.asList(d.getHierarchies()));
		}
		Hierarchy [] hierarchies = allHiers.toArray(new Hierarchy[allHiers.size()]);
		for(Element element : elements) {
			slice[dimIndex][0] = element.getId();
			DataCell dataCell = 
				new DataCell(element,hierarchies,slice);
			Element[][] coords = dataCell.getCoordinates();
			coordinates.addAll(Arrays.asList(coords));			
			dataCells[index++] = dataCell;
			dataElements.put(element,dataCell);
		}		
		Object[] values = srcCube.getDataBulk(
				coordinates.toArray(new Element[coordinates.size()][]));
		
		int start = 0;
		for(int i=0;i<dataCells.length;i++) {
			int valCount = dataCells[i].getCoordinates().length;
			Object[] cellValues = new Object[valCount];
			int end = start+valCount;
			for(int j=start;j<end;j++) {
				cellValues[j%valCount] = values[j]; 
			}
			start = end;
			dataCells[i].setValues(cellValues);
		}
		
		ArrayList<DataCell> sortedValues = 
			new ArrayList<DataCell>(Arrays.asList(dataCells));
		Collections.sort(sortedValues, 
				new DataCellComparator(setting.getCellOperator().getValue()));

		
		int top = setting.getTop().getValue();
		if(top < 0)
			top = elements.size(); //take all...

		double upper = setting.getUpperPercentage().getValue();
		double lower = setting.getLowerPercentage().getValue();
				
		//now we remove each elements which doesn't fulfill criteria...
		int count = 0;
		for(int i=sortedValues.size()-1;i>=0;--i) {
			DataCell cell = sortedValues.get(i);
			if(count>=top)
				elements.remove(cell.getElement());
			if( !fulFillsCriteria(cell))
				elements.remove(cell.getElement());
			else
				++count;
		}
		
		//upper/lower percentage:
		if(upper > 0 || lower > 0) {
			double total = getTotalValue(dataCells);
			if (upper > 0) {
				double partial = 0;
				double bound = total * upper / 100d;
				for (int i = sortedValues.size() - 1; i >= 0; --i) {
					DataCell cell = sortedValues.get(i);
					// check upper and lower percentage
					if (lower < 0) {
						if (partial > bound)
							elements.remove(cell.getElement());
						if(!cell.isString())
							partial += 
								(Double)cell.getValue(setting.getCellOperator().getValue());
					} else {
						if (partial < bound)
							elements.remove(cell.getElement());
						if(!cell.isString())
							partial += 
								(Double)cell.getValue(setting.getCellOperator().getValue());
					}
				}
			}
			
			if (lower > 0) {
				double partial = 0;
				double bound = total * lower / 100d;
				for (int i = 0,n=sortedValues.size(); i<n; ++i) {
					DataCell cell = sortedValues.get(i);
					if(upper > 0) {
						if(partial < bound)
							elements.remove(cell.getElement());
					} else if(partial > bound)
						elements.remove(cell.getElement());
					if(!cell.isString())
						partial += 
							(Double)cell.getValue(setting.getCellOperator().getValue());
				}
			}
		}
	}


	public final int[] getEffectiveFilter() {
		return effectiveFilters;
	}

	public final void validateSettings() throws PaloIOException {
		Database database = hierarchy.getDimension().getDatabase();
		Cube srcCube = database.getCubeById(setting.getSourceCube().getValue());
		if(srcCube == null)
			throw new PaloIOException("DataFilter: unknown source cube with id '"+srcCube+"'!");		
		String[][] slice = setting.getSlice();
		if(slice == null)
			throw new PaloIOException("DataFilter: the slice is not set!");
//		String[] sliceIds = (String[]) slice.getValue();
		if(slice.length != srcCube.getDimensionCount())
			throw new PaloIOException("DataFilter: wrong slice!");
	}
	
	/**
	 * Returns the value to use for the given element.
	 * @param element the element to return the value for
	 * @return the corresponding element value as string
	 */
	final String getValue(Element element) {
		DataCell dataElement = dataElements.get(element);
		if(dataElement == null)
			return element.getName();
		
		return dataElement.getValue(setting.getCellOperator().getValue()).toString();
	}

	private final double getTotalValue(DataCell[] dataCells) {
		double total = 0d;
		for(DataCell cell : dataCells) {
			if(!cell.isString())
				total += (Double)cell.getValue(setting.getCellOperator().getValue());
		}
		return total;
	}
	
	private final int getDimensionIndex(Cube srcCube) {
		Dimension[] dimensions = srcCube.getDimensions();
		for(int i=0; i< dimensions.length;i++) {
			if(dimensions[i].equals(hierarchy.getDimension()))
				return i;
		}
		return -1;
	}
	
	private final boolean fulFillsCriteria(DataCell cell) {
		return cell.fulfills(
				setting.getCellOperator().getValue(), 
				setting.getCriteria());
	}
}

class DataCell {
	
	private final Element element;
	private final Element[][] coordinates;
	private Object[] values;
	private boolean isString = false;
	
	DataCell(Element element, Hierarchy[] cubeHiers, String[][] slices) {
		this.element = element;
		this.coordinates = determineCoordinates(cubeHiers, slices);
	}
	
	final Element getElement() {
		return element;
	}
	
	final Element[][] getCoordinates() {
		return coordinates;
	}
	
	final boolean isString() {
		return false;
	}
	
	final void setValues(Object[] values) {
		this.values = values;
	}
	
	final boolean fulfills(int cellOperator, DataCriteria criteria) { 
		switch(cellOperator) {
		case DataFilterSetting.ALL_OP: return areAll(criteria);
		case DataFilterSetting.ANY_OP: return isAny(criteria);
		case DataFilterSetting.STR_OP:
			if(values.length<1)
				return isInCriteriaRegion("", criteria);
			return isInCriteriaRegion(values[0].toString(), criteria);
		}
		Object value = getValue(cellOperator);
		return isInCriteriaRegion(value, criteria);
	}
	
	
	final Object getValue(int operator) {
		switch (operator) {
		case DataFilterSetting.MAX_OP:
			return getMax();
		case DataFilterSetting.MIN_OP:
			return getMin();
		case DataFilterSetting.AVG_OP:
			return getAvg();
		case DataFilterSetting.STR_OP:
			return values.length > 0 ? values[0].toString() : "";
		}
		// as default:
		return getSum();
	}

	private final boolean isAny(DataCriteria criteria) {
		for(Object value : values) {
			if(isInCriteriaRegion(value, criteria))
				return true;
		}
		return false;
	}

	private final boolean areAll(DataCriteria criteria) {
		for(Object value : values) {
			if(!isInCriteriaRegion(value, criteria))
				return false;
		}
		return true;
	}

	private final double getSum() {
		double sum = 0;
		for(Object value : values) {
			if(value instanceof String) {
				sum += getValueFrom(value.toString());
//					String val = value.toString();
//					if (val.length() > 0)
//						sum += Double.parseDouble(val);
			} else
				sum += ((Double)value).doubleValue();
		}
		return sum;
	}
	
	private final double getMax() {
		double max = Double.MIN_VALUE;
		for(Object value : values) {
			double _val = 0;
			if(value instanceof String) {
				_val = getValueFrom(value.toString());
//				String val = value.toString();
//				if(val.length()>0)
//					_val = Double.parseDouble(val);
			} else
				_val = ((Double)value).doubleValue();
			if(Double.compare(_val, max) > 0)
				max = _val;
		}
		return max;
	}
	
	private final double getMin() {
		double min = Double.MAX_VALUE;
		for(Object value : values) {			
			double _val = 0;
			if(value instanceof String) {
				_val = getValueFrom(value.toString());
//				String val = value.toString();
//				if(val.length()>0)
//					_val = Double.parseDouble(val);
			} else
				_val = ((Double)value).doubleValue();
			if(Double.compare(_val, min) < 0)
				min = _val;
		}
		return min;
	}
	
	private final double getAvg() {
		return getSum()/values.length;
	}
	
	private final Element[][] determineCoordinates(Hierarchy[] cubeHiers,
			String[][] slices) {
//		String[][] _slices = determineSlices(cubeDims, slices);
		//do we have to take all elements?
		for(int i=0;i<slices.length;i++) {
			String template = slices[i][0];
			if(template.equals("*") || template.equals("-1"))
				slices[i] = getAllElementIds(cubeHiers[i]);
		}
		return cartesianProduct(cubeHiers, slices);
	}
	
//	private final String[][] determineSlices(Dimension[] cubeDims, String[] slice) {
//		String[][] slices = new String[slice.length][];
//		for(int i=0;i<slice.length;++i) {
//			if(slice[i] != null && slice[i].length() > 0 && !slice[i].equals("*"))
//				slices[i] = slice[i].split(",");
//			else { 
//				//take all:
//				Element[] allElements = cubeDims[i].getElements();
//				slices[i] = new String[allElements.length];
//				for(int j=0;j<allElements.length;++j)
//					slices[i][j] = allElements[j].getId();
//			}
//		}
//		return slices;
//	}

	private final Element[][] cartesianProduct(Hierarchy [] cubeHiers, String[][] slices) {
		int coordsCount = 1;
        //determine the numbers of coordinates(= |a1|*|a2|*...*|aN|):
        for (int i = 0; i < slices.length; coordsCount *= slices[i].length, i++);
		//now determine all coordinates:
        Element[][] coordinates = new Element[coordsCount][];
        while(coordsCount-- > 0) {
        	int i = 1;        	
            int index = 0;
            Element[] coordinate = new Element[slices.length];
            for(String[] slice : slices) {
            	int pos = (coordsCount/i)%slice.length;
            	coordinate[index] = cubeHiers[index].getElementById(slice[pos]);
                i *= slice.length;
                if (!isString
						&& coordinate[index].getType() == Element.ELEMENTTYPE_STRING)
					isString = true;

// System.out.print(coordinate[index].getName()+" ");
                ++index;
                	
            }
            coordinates[coordsCount] = coordinate;
//            System.out.println();
        }
        return coordinates;
    }
	
	private final boolean isInCriteriaRegion(Object val, DataCriteria criteria) {
		return pass(val, criteria.getFirstOperator(), 
					criteria.getFirstOperand().getValue())
				&& pass(val, criteria.getSecondOperator(), 
						criteria.getSecondOperand().getValue());
	}
	private final boolean pass(Object val, String operator, String operand) {
		if(val == null || operand == null || operator == null || operand.length() == 0)
			return true;
		
		if(val instanceof Double) {
			try {				
				Double op = new Double(operand);
				Double _val = (Double)val;
				return compare(_val, op, operator);
			}catch(NumberFormatException nfe) {
				/* ignore, we go on with strings... */
			}
		}
		String valStr = val.toString();
		return compare(valStr,operand, operator);
	}
	
	private final boolean compare(Double d1, Double d2, String operator) {
		int result = d1.compareTo(d2);
		return compare(d1, d2, operator, result);
	}

	private final boolean compare(String str1, String str2, String operator) {
		int result = str1.compareTo(str2);
		return compare(str1, str2, operator, result);
	}

	private final boolean compare(Object o1, Object o2, String operator, int result) {
		if (operator.equals(DataCriteria.GREATER))
			return result > 0;
		else if (operator.equals(DataCriteria.GREATER_EQUAL))
			return result >= 0;
		else if (operator.equals(DataCriteria.LESSER))
			return result < 0;
		else if (operator.equals(DataCriteria.LESSER_EQUAL))
			return result <= 0;
		else if (operator.equals(DataCriteria.NOT_EQUAL))
			return !(o1.equals(o2));

		return o1.equals(o2);
	}
	
	private final double getValueFrom(String str) {
		double val = 0;
		try {
			if (str.length() > 0)
				val = Double.parseDouble(str);
		} catch (NumberFormatException e) {

		}
		return val;
	}
	
	private final String[] getAllElementIds(Hierarchy hierarchy) {
		Element[] elements = hierarchy.getElements();
		String[] ids = new String[elements.length];
		for(int i=0;i<ids.length;i++)
			ids[i] = elements[i].getId();
		return ids;
	}
}

class DataCellComparator implements Comparator<DataCell> {

	private final int cellOperator;
	
	public DataCellComparator(int cellOperator) {
		this.cellOperator = cellOperator;
	}
	
	public int compare(DataCell dc1, DataCell dc2) {
		// return Double.compare(dc1.getValue(cellOperator), dc2
		// .getValue(cellOperator));
		Object o1 = dc1.getValue(cellOperator);
		Object o2 = dc2.getValue(cellOperator);

		if (o1 instanceof Double && o2 instanceof Double) {
			Double d1 = (Double) o1;
			Double d2 = (Double) o2;
			return d1.compareTo(d2);
		}
		String s1 = o1.toString();
		String s2 = o2.toString();
		return s1.compareTo(s2);
	}
}
