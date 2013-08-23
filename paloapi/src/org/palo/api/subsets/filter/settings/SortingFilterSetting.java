/*
*
* @file SortingFilterSetting.java
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
* @version $Id: SortingFilterSetting.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.Element;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.filter.SortingFilter;


/**
 * <code>SortingFilterSetting</code>
 * <p>
 * Manages the settings for the {@link SortingFilter}.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: SortingFilterSetting.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class SortingFilterSetting extends AbstractFilterSettings {

	
	public static final int HIERARCHICAL_MODE_DISABLED = 0;
	public static final int HIERARCHICAL_MODE_SHOW_CHILDREN = 1;
	public static final int HIERARCHICAL_MODE_HIDE_CHILDREN = 2;
	
	public static final int SORT_TYPE_DISABLED = 0;
	public static final int SORT_TYPE_LEAFS_ONLY = 1;
	public static final int SORT_TYPE_CONSOLIDATIONS_ONLY = 2;
	
	public static final int ORDER_MODE_REVERSE_DISABLED = 0; 
	public static final int ORDER_MODE_REVERSE_TOTAL = 1; 
	public static final int ORDER_MODE_REVERSE_PER_LEVEL = 2; 
//	public static final int ORDER_MODE_REVERSE_HIERARCHY = 3;

	public static final int SORT_CRITERIA_DEFINITION = 0; 
	public static final int SORT_CRITERIA_DATA = 1; 
	public static final int SORT_CRITERIA_LEXICAL = 2; 
	public static final int SORT_CRITERIA_ALIAS = 3; 
	
	
	//SETTINGS:
	private IntegerParameter sortCriteria;
	private IntegerParameter orderMode;
	private IntegerParameter sortTypeMode;
	private IntegerParameter hierarchicalMode;
	private IntegerParameter sortLevel;
	private IntegerParameter showDuplicates;
	/** @deprecated */
	private StringParameter sortLevelElementId;
	private StringParameter sortAttribute;
	
	public SortingFilterSetting() {
		sortCriteria = new IntegerParameter();
		orderMode = new IntegerParameter();
		sortTypeMode = new IntegerParameter();
		hierarchicalMode = new IntegerParameter();
		sortLevelElementId = new StringParameter();
		sortAttribute = new StringParameter();
		showDuplicates = new IntegerParameter();
		sortLevel = new IntegerParameter();
		
		//default settings...
		reset();
	}
	

	/**
	 * Returns whether duplicate {@link Element}s should be shown or not, i.e.
	 * if duplicates should be displayed the value of the returned 
	 * <code>IntegerParameter</code> is <code>1</code>, otherwise <code>0</code>.
	 * @return the show duplicates setting
	 */
	public final IntegerParameter getShowDuplicates() {
		return showDuplicates;
	}
	
	/**
	 * Specifies whether duplicate {@link Element}s should be shown or not.
	 * @param showDuplicates pass <code>1</code> to show duplicate elements,
	 * pass <code>0</code> to hide duplicates
	 */
	public final void setShowDuplicates(int showDuplicates) {
		this.showDuplicates.setValue(showDuplicates);
	}
	/**
	 * Specifies whether duplicate {@link Element}s should be shown or not.
	 * @param showDuplicates to show duplicate elements the value of the passed 
	 * <code>IntegerParameter</code> must be <code>1</code>. To hide duplicates
	 * the value must be <code>0</code>.
	 */
	public final void setShowDuplicates(IntegerParameter showDuplicates) {
		this.showDuplicates = showDuplicates;
	}
	
	/**
	 * Returns the currently set sorting criteria, i.e. one of the predefined
	 * sort criteria constants
	 * @return the set sorting criteria
	 */
	public final IntegerParameter getSortCriteria() {
		return sortCriteria;
	}
	/**
	 * Sets the sorting criteria, i.e. one of the predefined sort criteria 
	 * constants.
	 * @param sortCriteria should be one of the predefined sort criteria 
	 * constants
	 */
	public final void setSortCriteria(int sortCriteria) {
		this.sortCriteria.setValue(sortCriteria);
	}
	/**
	 * Sets the sorting criteria, i.e. the parameter value should be one of the 
	 * predefined sort criteria constants.
	 * @param sortCriteria the new <code>IntegerParameter</code> to use for the
	 * sorting criteria
	 */
	public final void setSortCriteria(IntegerParameter sortCriteria) {
		this.sortCriteria = sortCriteria;
		this.sortCriteria.bind(subset);
	}

	
	/**
	 * Checks if the reverse order mode is active 
	 * @return <code>true</code> if reverse order mode should be used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doReverseOrder() {
		return orderMode.getValue() != ORDER_MODE_REVERSE_DISABLED;
	}
	/**
	 * Returns the reverse order mode
	 * @return the reverse order mode, i.e. one of the predefined order mode
	 * constants
	 */
	public final IntegerParameter getOrderMode() {
		return orderMode;
	}
	/**
	 * Sets the reverse order mode. One of the predefined order mode constants
	 * should be used
	 * @param orderMode the new order mode
	 */
	public final void setOrderMode(int orderMode) {
		this.orderMode.setValue(orderMode);
	}
	/**
	 * Sets the reverse order mode, i.e. the parameter value should be one of 
	 * the predefined order mode constants.
	 * @param orderMode the new <code>IntegerParameter</code> to use for the
	 * order mode
	 */
	public final void setOrderMode(IntegerParameter orderMode) {
		this.orderMode = orderMode;
		this.orderMode.bind(subset);
	}

	
	/**
	 * Checks if the sort by type mode is active 
	 * @return <code>true</code> if sort by type mode should be used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doSortByType() {
		return sortTypeMode.getValue() != SORT_TYPE_DISABLED;
	}
	
	/**
	 * Returns the sort by type mode
	 * @return the sort by type mode, i.e. one of the predefined sort by type
	 * mode constants
	 */
	public final IntegerParameter getSortTypeMode() {
		return sortTypeMode;
	}
	/**
	 * Sets the sort by type mode. One of the predefined sort by type mode 
	 * constants should be used
	 * @param sortTypeMode the new sort by type mode
	 */
	public final void setSortTypeMode(int sortTypeMode) {
		this.sortTypeMode.setValue(sortTypeMode);
	}
	/**
	 * Sets the sort by type mode, i.e. the parameter value should be one of the 
	 * predefined sort by type mode constants.
	 * @param sortTypeMode the new <code>IntegerParameter</code> to use for the
	 * sort by type mode
	 */
	public final void setSortTypeMode(IntegerParameter sortTypeMode) {
		this.sortTypeMode = sortTypeMode;
		this.sortTypeMode.bind(subset);
	}

	/**
	 * Checks if a hierarchy should be created
	 * @return <code>true</code> if a hierarchy mode is active, 
	 * <code>false</code> otherwise
	 */
	public final boolean doHierarchy() {
		return hierarchicalMode.getValue() != HIERARCHICAL_MODE_DISABLED;
	}
	
	/**
	 * Returns the hierarchical mode
	 * @return the hierarchical mode, i.e. one of the predefined hierarchical 
	 * mode constants
	 */
	public final IntegerParameter getHierarchicalMode() {
		return hierarchicalMode;
	}
	/**
	 * Sets the hierarchical mode. One of the predefined hierarchical mode 
	 * constants should be used
	 * @param hierarchicalMode the new hierarchical mode
	 */
	public final void setHierarchicalMode(int hierarchicalMode) {
		this.hierarchicalMode.setValue(hierarchicalMode);		
	}
	/**
	 * Sets the hierarchical mode. The parameter value should be one of the 
	 * predefined hierarchical mode constants.
	 * @param hierarchicalMode the new <code>IntegerParameter</code> to use for 
	 * the hierarchical mode
	 */
	public final void setHierarchicalMode(IntegerParameter hierarchicalMode) {
		this.hierarchicalMode = hierarchicalMode;
		this.hierarchicalMode.bind(subset);
	}


	/**
	 * Checks if the sort per level mode is active 
	 * @return <code>true</code> if sort per level mode should be used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doSortPerLevel() {
		return sortLevelElementId.getValue() != null || sortLevel.getValue()>-1;
	}
	
	/** 
	 * Returns the level at which the elements should be sorted
	 * @return the sort level 
	 */
	public final IntegerParameter getSortLevel() {
		return sortLevel;
	}
	
	/**
	 * Sets the level to sort the elements at
	 * @param level the sort level
	 */
	public final void setSortLevel(int level) {
		sortLevel.setValue(level);
	}
	
	/**
	 * Sets the level to sort the elements at as {@link IntegerParameter}
	 * @param sortLevel the sort level
	 */
	public final void setSortLevel(IntegerParameter sortLevel) {
		this.sortLevel.unbind();
		this.sortLevel = sortLevel;
		this.sortLevel.bind(subset);
		
	}
	
	/**
	 * Returns the identifier of the reference element for sort per level mode
	 * @return the sort per level reference element id
	 * @deprecated use {@link #getSortLevel()}
	 */
	public final StringParameter getSortLevelElement() {
		return sortLevelElementId;
	}
	/**
	 * Sets the identifier of the reference element for sort per level mode 
	 * @param sortLevelElementId the sort per level reference element id
	 * @deprecated please use {@link #setSortLevel(int)}
	 */
	public final void setSortLevelElement(String sortLevelElementId) {
		this.sortLevelElementId.setValue(sortLevelElementId);		
	}
	/**
	 * Sets the identifier of the sort per level element, i.e. the parameter 
	 * value should contain the id of the reference element. 
	 * @param sortLevelElementId the new <code>StringParameter</code> to use for 
	 * the sort per level mode
	 * @deprecated please use {@link #setSortLevel(IntegerParameter)}
	 */
	public final void setSortLevelElement(StringParameter sortLevelElement) {
		this.sortLevelElementId = sortLevelElement;
		sortLevelElement.bind(subset);
	}

	/**
	 * Checks if the sort by attribute mode is active 
	 * @return <code>true</code> if sort by attribute mode should be used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doSortByAttribute() {
		return sortAttribute.getValue() != null;
	}
	/**
	 * Returns the attribute identifier to sort after
	 * @return
	 */
	public final StringParameter getSortAttribute() {
		return sortAttribute;
	}
	/**
	 * Sets the attribute id to sort after 
	 * @param sortAttribute
	 */
	public final void setSortAttribute(String attributeId) {
		this.sortAttribute.setValue(attributeId);
	}
	/**
	 * Sets the identifier of attribute to sort after, i.e. the parameter 
	 * value should contain the id of the attribute. 
	 * @param sortAttribute the new <code>StringParameter</code> to use for 
	 * the sort by attribute mode
	 */
	public final void setSortAttribute(StringParameter sortAttribute) {
		this.sortAttribute = sortAttribute;
		sortAttribute.bind(subset);
	}



	public final void reset() {
		sortCriteria.setValue(SORT_CRITERIA_DEFINITION);
		orderMode.setValue(ORDER_MODE_REVERSE_DISABLED);
		sortTypeMode.setValue(SORT_TYPE_DISABLED);
		hierarchicalMode.setValue(HIERARCHICAL_MODE_DISABLED);
		sortLevelElementId.setValue(null);
		sortAttribute.setValue(null);
		showDuplicates.setValue(1);	//we show duplicates by default
		sortLevel.setValue(-1);		//by default no sort level defined	
	}
	
	public final void bind(Subset2 subset) {
		super.bind(subset);
		//bind internal:
		sortCriteria.bind(subset);
		orderMode.bind(subset);
		sortTypeMode.bind(subset);
		hierarchicalMode.bind(subset);
		sortLevelElementId.bind(subset);
		sortAttribute.bind(subset);
		showDuplicates.bind(subset);
		sortLevel.bind(subset);
	}
	public final void unbind() {
		super.unbind();
		//unbind internal:
		sortCriteria.unbind();
		orderMode.unbind();
		sortTypeMode.unbind();
		hierarchicalMode.unbind();
		sortLevelElementId.unbind();
		sortAttribute.unbind();
		showDuplicates.unbind();
	}

	public final void adapt(FilterSetting from) {
		if(!(from instanceof SortingFilterSetting))
			return;
		SortingFilterSetting setting = (SortingFilterSetting) from;
		reset();
		setSortCriteria(setting.getSortCriteria().getValue());
		setOrderMode(setting.getOrderMode().getValue());
		setSortTypeMode(setting.getSortTypeMode().getValue());
		setHierarchicalMode(setting.getHierarchicalMode().getValue());
		setSortLevelElement(setting.getSortLevelElement().getValue());
		setSortLevel(setting.getSortLevel().getValue());
		setSortAttribute(setting.getSortAttribute().getValue());
		setShowDuplicates(setting.getShowDuplicates().getValue());
		
	}

}
