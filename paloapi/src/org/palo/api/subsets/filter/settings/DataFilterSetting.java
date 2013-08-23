/*
*
* @file DataFilterSetting.java
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
* @version $Id: DataFilterSetting.java,v 1.9 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import java.util.ArrayList;

import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.filter.DataFilter;

/**
 * <code>DataFilterSetting</code>
 * <p>
 * Manages the settings for the {@link DataFilter}. Each data filter is based
 * on a source cube to determine filter values for elements. A slice can be
 * defined for this source cube. Note that the array length of this slice
 * corresponds to the dimension count of the source cube and that the elements
 * of e.g. slice[index] are part of the dimension which is returned by 
 * <code>sourceCube.getDimensionAt(index)</code> 
 * </p>
 *
 * @author ArndHouben
 * @version $Id: DataFilterSetting.java,v 1.9 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class DataFilterSetting extends AbstractFilterSettings {

	//VALID CELL OPERATORS:
	public static final int SUM_OP = 0;
	public static final int ALL_OP = 1;
	public static final int AVG_OP = 2;
	public static final int MAX_OP = 3;
	public static final int ANY_OP = 4;
	public static final int MIN_OP = 5;
	public static final int STR_OP = 7;
		
	//required parameters:
//	private ObjectParameter criteria;
	private DataCriteria criteria;
	
	//optional parameters:
	private IntegerParameter top;
	private IntegerParameter cellOperator;	
	private StringParameter sourceCube;
	private DoubleParameter upperPercentage;
	private DoubleParameter lowerPercentage;
	private BooleanParameter useRules;
//	private ObjectParameter slice;
	private final ArrayList<ObjectParameter> slice;
	
	/**
	 * Creates a new <code>DataFilterSetting</code> instance for the given
	 * source cube. {@link DataCriteria#GREATER} is used as default criteria. 
	 * @param sourceCube a valid identifier of the source cube to use 
	 */
	public DataFilterSetting(String sourceCube) {
		this(sourceCube, new DataCriteria(DataCriteria.GREATER, ""));
	}
	/**
	 * Creates a new <code>DataFilterSetting</code> instance for the given
	 * source cube and criteria. 
	 * @param sourceCube a valid identifier of the source cube to use
	 * @param criteria the data criteria to use
	 */
	public DataFilterSetting(String sourceCube, DataCriteria criteria) {		
		//parameters:		
		top = new IntegerParameter();
		cellOperator = new IntegerParameter();			
		upperPercentage = new DoubleParameter();
		lowerPercentage = new DoubleParameter();
		useRules = new BooleanParameter();
		this.sourceCube = new StringParameter();
		this.sourceCube.setValue(sourceCube);
		this.slice = new ArrayList<ObjectParameter>(); //new ObjectParameter();
//		this.slice.setValue(new String[0]);		
		reset();
	}

	/**
	 * Returns the number of top elements to select
	 * @return the number of top elements
	 */
	public final IntegerParameter getTop() {
		return top;
	}
	/**
	 * Sets the number of top elements to select
	 * @param top the new number of top elements
	 */
	public final void setTop(int top) {
		this.top.setValue(top);		
	}
	/**
	 * Sets the top count, i.e. the parameter value should contain the number
	 * of elements to select.
	 * @param top the new <code>IntegerParameter</code> which specifies the
	 * number of top elements to select
	 */
	public final void setTop(IntegerParameter top) {
		this.top = top;
		this.top.bind(subset);
	}

	/**
	 * Returns the cell operator to use, i.e. one of the predefined operator
	 * constants.
	 * @return the cell operator
	 */
	public final IntegerParameter getCellOperator() {
		return cellOperator;
	}
	/**
	 * Sets the cell operator. One of the predefined operator constants should
	 * be used
	 * @param cellOperator the new cell operator
	 */
	public final void setCellOperator(int cellOperator) {
		this.cellOperator.setValue(cellOperator);		
	}
	/**
	 * Sets the cell operator, i.e. the parameter value should be one of the 
	 * predefined operator constants.
	 * @param cellOperator the new <code>IntegerParameter</code> which specifies
	 * the cell operator
	 */
	public final void setCellOperator(IntegerParameter cellOperator) {
		this.cellOperator = cellOperator;
		this.cellOperator.bind(subset);
	}
	
	/**
	 * Sets the data criteria to use
	 * @param criteria the new data criteria
	 */
	public final void setCriteria(DataCriteria criteria) {
		if(criteria != null) {
			this.criteria = criteria;
			this.criteria.bind(subset);
		}
	}
	
//	/**
//	 * Sets the criteria to use. If the parameter value is not of type
//	 * {@link DataCriteria} calling this method has no effect 
//	 * @param criteria the new <code>ObjectParameter</code> which contains the
//	 * <code>DataCriteria</code> to use
//	 */
//	public final void setCriteria(ObjectParameter criteria) {
//		if(criteria.getValue() instanceof DataCriteria) {
//			this.criteria = criteria;
//		}
//	}
	
	/**
	 * Returns the currently used data criteria
	 * @return the used data criteria
	 */
	public final DataCriteria getCriteria() {
		return criteria;
	}
	/**
	 * Specifies if an existing rule should be used to determine the element 
	 * value. Has only effect if corresponding cube cell has a rule.
	 * @param useRules set to <code>true</code> to use existing rule for 
	 * determining element value, to <code>false</code> otherwise
	 */
	public final void setUseRules(boolean useRules) {
		this.useRules.setValue(useRules);
	}
	/**
	 * Specifies if an existing rule should be used to determine the element 
	 * value. Has only effect if corresponding cube cell has a rule.
	 * @param useRules the new <code>BooleanParameter</code> to use
	 */
	public final void setUseRules(BooleanParameter useRules) {
		this.useRules = useRules;
		this.useRules.bind(subset);
	}
		
	/**
	 * Checks if an existing rule should be used to determine element value.
	 * @return <code>true</code> if rule should be used, <code>false</code>
	 * otherwise
	 */
	public final BooleanParameter getUseRules() {
		return useRules;
	}
	/**
	 * Returns the identifier of the source cube of this data filter
	 * @return the source cube id
	 */
	public final StringParameter getSourceCube() {
		return sourceCube;
	}
	/**
	 * Sets the identifier of the source cube to use
	 * @param sourceCube the source cube id
	 */
	public final void setSourceCube(String sourceCube) { 
		this.sourceCube.setValue(sourceCube);		
	}
	/**
	 * Sets the identifier of the source cube to use, i.e. the parameter 
	 * value should contain the id of the source cube. 
	 * @param sourceCube the new <code>StringParameter</code> to use as 
	 * source cube
	 */
	public final void setSourceCube(StringParameter sourceCube) {
		this.sourceCube = sourceCube;
		this.sourceCube.bind(subset);
	}
	
	/**
	 * Returns the percentage of all upper elements to select
	 * @return the upper percentage
	 */
	public final DoubleParameter getUpperPercentage() {
		return upperPercentage;
	}
	/**
	 * Specifies the percentage of all upper elements to select
	 * @param upperPercentage the new upper percentage
	 */
	public final void setUpperPercentage(double upperPercentage) {
		this.upperPercentage.setValue(upperPercentage);
	}
	/**
	 * Specifies the percentage of all upper elements to select, i.e. the
	 * parameter value contains the p
	 * @param upperPercentage
	 */
	public final void setUpperPercentage(DoubleParameter upperPercentage) {
		this.upperPercentage = upperPercentage;
		this.upperPercentage.bind(subset);
	}

	/**
	 * Returns the percentage of all lower elements to select
	 * @return the lower percentage
	 */
	public final DoubleParameter getLowerPercentage() {
		return lowerPercentage;
	}
	/**
	 * Sets the percentage of all lower elements to select
	 * @param lowerPercentage the new lower percentage
	 */
	public final void setLowerPercentage(double lowerPercentage) {
		this.lowerPercentage.setValue(lowerPercentage);
	}
	public final void setLowerPercentage(DoubleParameter lowerPercentage) {
		this.lowerPercentage = lowerPercentage;
		this.lowerPercentage.bind(subset);
	}

	/**
	 * Returns an {@link ObjectParameter} array which contains the currently 
	 * used slice of the source cube. The length of the slice array corresponds 
	 * to the number of dimensions of the source cube. Each slice entry 
	 * contains a {@link ObjectParameter} with a string array as its value. 
	 * This array holds the ids of the elements which should be used for the
	 * slice at the current dimension.
	 * @return the current slice parameter
	 */
	public final ObjectParameter[] getSliceParameters() {
		return slice.toArray(new ObjectParameter[0]);
	}
	
	/**
	 * Returns the element ids which should be used for the slice. <b>NOTE:</b>
	 * if the id array has a length of 1 and the only id is either -1 or * then
	 * all elements of the corresponding dimension should be taken.
	 * @return
	 */
	public final String[][] getSlice() {
		ObjectParameter[] params = getSliceParameters();
		String[][] slice = new String[params.length][];
		for(int i=0;i<slice.length;i++) {
			String[] elIds = (String[])params[i].getValue();
			slice[i] = elIds.clone();			
		}
		return slice;
	}
	
	public final void addSliceElement(String id, int index) {
		ObjectParameter dimSlice = getDimensionSlice(index);
		String[] ids = (String[]) dimSlice.getValue();
		if (ids == null)
			dimSlice.setValue(new String[] { id });
		else {
			String[] newIds = new String[ids.length + 1];
			System.arraycopy(ids, 0, newIds, 0, ids.length);
			newIds[ids.length] = id;
			dimSlice.setValue(newIds);
		}
	}

	public final void setSliceElements(String[] ids, int index) {
		ObjectParameter dimSlice = getDimensionSlice(index);
		dimSlice.setValue(ids.clone());
	}

	/**
	 * Returns the slice part for the given dimension index. 
	 * @see #addNextSliceDimension()
	 * @see #addSliceElement(String);
	 * @return
	 */
	public final ObjectParameter getSlicePart(int index) {
		return slice.get(index);
	}

	public final void clearSlice() {
		this.slice.clear();
		markDirty();
	}
	
	
	/** internally used method */ 
	public final void addSlicePart(ObjectParameter part) {
		slice.add(part);
		part.bind(subset);
	}
	/** internally used method */
	public final ObjectParameter getSlicePart() {
		return slice.get(slice.size()-1);
	}
	/** internally used method */
	public final void addSliceElement(String id) {
		addSliceElement(id, slice.size() - 1);
	}

	final void setSlice(ObjectParameter[] slice) {
		clearSlice();
		for(ObjectParameter param : slice) {
			this.slice.add(param);
			param.bind(subset);
		}
	}

	public final void reset() {
		//we reset all optional parameters; negative values means "do not restrict"
		top.setValue(-1);
		upperPercentage.setValue(-1d);
		lowerPercentage.setValue(-1d);

		cellOperator.setValue(SUM_OP);	
		useRules.setValue(false);
		
		if(slice != null)
			clearSlice(); //slice.clear(); //setValue(new String[0]); //.clear();
		criteria = new DataCriteria(DataCriteria.GREATER, "");
		criteria.bind(subset);
	}
	
	public final void bind(Subset2 subset) {
		super.bind(subset);
		//bind internal:
		criteria.bind(subset);
		top.bind(subset);
		cellOperator.bind(subset);	
		sourceCube.bind(subset);
		upperPercentage.bind(subset);
		lowerPercentage.bind(subset);
		useRules.bind(subset);
//		private ObjectParameter slice;
		for(ObjectParameter params : slice)
			params.bind(subset);
	}
	public final void unbind() {
		super.unbind();
		//unbind internal:
		criteria.unbind();
		top.unbind();
		cellOperator.unbind();	
		sourceCube.unbind();
		upperPercentage.unbind();
		lowerPercentage.unbind();
		useRules.unbind();
//		private ObjectParameter slice;
		for(ObjectParameter params : slice)
			params.unbind();
	}

	public final void adapt(FilterSetting from) {
		if(!(from instanceof DataFilterSetting))
			return;
		DataFilterSetting setting = (DataFilterSetting) from;
		reset();
		setSourceCube(setting.getSourceCube().getValue());		
		setSlice(setting.getSliceParameters());		
		DataCriteria fromCriteria = (DataCriteria)setting.getCriteria();
		if(fromCriteria != null)
			setCriteria(fromCriteria.copy());		
		setTop(setting.getTop().getValue());
		setCellOperator(setting.getCellOperator().getValue());		
		setUpperPercentage(setting.getUpperPercentage().getValue());
		setLowerPercentage(setting.getLowerPercentage().getValue());
		setUseRules(setting.getUseRules().getValue());
	}
	
	private final ObjectParameter getDimensionSlice(int index) {
		if(index == slice.size()) {
			ObjectParameter param = new ObjectParameter();
			param.bind(subset);
			slice.add(param);
		}
		return slice.get(index);
	}
}