/*
*
* @file HierarchicalFilterSetting.java
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
* @version $Id: HierarchicalFilterSetting.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.filter.settings;

import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.filter.HierarchicalFilter;


/**
 * <code>HierarchicalFilterSetting</code>
 * <p>
 * Manages the settings for the {@link HierarchicalFilter}.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: HierarchicalFilterSetting.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class HierarchicalFilterSetting extends AbstractFilterSettings {
	
	//HIDE MODES:
	public static final int HIDE_MODE_DISABLED = 0;
	public static final int HIDE_MODE_LEAFS = 1;
	public static final int HIDE_MODE_CONSOLIDATIONS = 2;	
	//REVOLVE_ADD_MODE:
	public static final int REVOLVE_ADD_DISABLED = 0;
	public static final int REVOLVE_ADD_BELOW = 1;
	public static final int REVOLVE_ADD_ABOVE = 2;
	
	//filter state:
	private StringParameter refElementId;	//for above/below selection
	private BooleanParameter exclusive;
	private BooleanParameter above;
	
	private IntegerParameter hideMode;		//hide 
	
	/** @deprecated */
	private StringParameter startElementId;	//start/end selection
	/** @deprecated */
	private StringParameter endElementId;
	
	private IntegerParameter startLevel;
	private IntegerParameter endLevel;
	
	private StringParameter revolveElementId;	//revolving
	private IntegerParameter revolveElementsCount;
	private IntegerParameter revolveMode;		//either add above or below...
	
	public HierarchicalFilterSetting() {
		//create parameters with default values:
		//above/below selection:
		refElementId = new StringParameter();
		exclusive = new BooleanParameter();
		above = new BooleanParameter();
		
		//hide:
		hideMode = new IntegerParameter(); 
		
		//start/end selection:
		startElementId = new StringParameter();	
		endElementId = new StringParameter();
		startLevel = new IntegerParameter();
		endLevel = new IntegerParameter();
		
		//revolve:
		revolveElementId =new StringParameter();
		revolveElementsCount = new IntegerParameter();
		revolveMode = new IntegerParameter();
		
		//default settings...
		reset();
	}
	
	public final void reset() {
		//above/below selection
		refElementId.setValue(null);
		above.setValue(false);
		exclusive.setValue(false);
		
		//hide
		hideMode.setValue(HIDE_MODE_DISABLED);
		
		//level selection
		startLevel.setValue(-1);
		endLevel.setValue(-1);
		startElementId.setValue(null);
		endElementId.setValue(null);
		refElementId.setValue(null);

		//revolve
		revolveElementId.setValue(null);
		revolveElementsCount.setValue(0);
		revolveMode.setValue(REVOLVE_ADD_DISABLED);		
	}
	
	/**
	 * Checks if a reference element is specified for above/below selection
	 * @return <code>true</code> if a reference element is specified,
	 * <code>false</code> otherwise
	 */
	public final boolean doAboveBelowSelection() {
		return refElementId.getValue() != null;
	}
	/**
	 * Checks if start or end elements are specified for level selection
	 * @return <code>true</code> if either a start or an end element is 
	 * specified, <code>false</code> otherwise
	 */
	public final boolean doLevelSelection() {
		return (startElementId.getValue() != null)
				|| (endElementId.getValue() != null)
				|| (startLevel.getValue() > -1) 
				|| (endLevel.getValue() > -1);
	}	
	/**
	 * Checks if hide mode is active
	 * @return <code>true</code> if a hide mode should be used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doHide() {
		return hideMode.getValue() != HIDE_MODE_DISABLED;
	}
	/**
	 * Checks if a revolve element is specified.
	 * @return <code>true</code> if a revolve element is specified,
	 * <code>false</code> otherwise
	 */
	public final boolean doRevolve() {
		return revolveElementId.getValue() != null;
	}
	/**
	 * Returns the identifier of the specified reference element for above/below
	 * selection
	 * @return reference element id
	 */
	public final StringParameter getRefElement() {
		return refElementId;
	}
	/**
	 * Sets the identifier of the reference element for above/below selection
	 * @param elementId the reference element
	 */
	public final void setRefElement(String elementId) {
		this.refElementId.setValue(elementId);		
	}
	/**
	 * Sets the identifier of the reference element for above/below selection,
	 * i.e. the parameter value should contain the id of the reference element. 
	 * @param element the new <code>StringParameter</code> to use for the 
	 * above/below selection 
	 */
	public final void setRefElement(StringParameter element) {
		this.refElementId = element;
		this.refElementId.bind(subset);
	}

	/**
	 * Checks if all elements above the reference element should be used for
	 * above/below selection
	 * @return <code>true</code> if all elements above reference element should
	 * be used, <code>false</code> otherwise
	 */
	public final BooleanParameter getAbove() {
		return above;
	}
	/**
	 * Specifies if all elements above the reference element should be used for
	 * above/below selection
	 * @param above set to <code>true</code> if all elements above reference
	 * elements should be used, to <code>false</code> otherwise
	 */
	public final void setAbove(boolean above) {
		this.above.setValue(above);
	}
	/**
	 * Specifies if all elements above the reference element should be included
	 * in selection 
	 * @param above
	 */
	public final void setAbove(BooleanParameter above) {
		this.above = above;
		this.above.bind(subset);
	}
	
	/**
	 * Checks if reference element should be considered for above/below 
	 * selection too
	 * @return <code>true</code> if reference element should not be considered 
	 * too, <code>false</code> otherwise
	 */
	public final BooleanParameter getExclusive() {
		return exclusive;
	}
	/**
	 * Specifies if reference element should be considered for above/below
	 * selection too
	 * @param exclusive set to <code>true</code> to exclude reference element,
	 * to <code>false</code> to include it 
	 */
	public final void setExclusive(boolean exclusive) {
		this.exclusive.setValue(exclusive);
	}
	/**
	 * Specifies if the reference element should be included or excluded in
	 * the selection 
	 * @param exclusive
	 */
	public final void setExclusive(BooleanParameter exclusive) {
		this.exclusive = exclusive;
		this.exclusive.bind(subset);
	}
	
	/**
	 * Returns the hide mode to use, i.e. one of the predefined hide mode
	 * constants
	 * @return the hide mode
	 */
	public final IntegerParameter getHideMode() {
		return hideMode;
	}
	/**
	 * Sets the hide mode. One of the predefined hide mode constants should be
	 * used
	 * @param hideMode the new hide mode
	 */
	public final void setHideMode(int hideMode) {
		this.hideMode.setValue(hideMode);
	}
	/**
	 * Sets the hide mode. The parameter value should be one of the 
	 * predefined hide mode constants.
	 * @param hideMode the new <code>IntegerParameter</code> to use for 
	 * the hide mode
	 */
	public final void setHideMode(IntegerParameter hideMode) {
		this.hideMode = hideMode;
		this.hideMode.bind(subset);		
	}

	/**
	 * Returns the start level for selection based on element level
	 * @return start level
	 */
	public final IntegerParameter getStartLevel() {
		return startLevel;
	}

	/**
	 * Sets the start level for selection based on element level
	 * @param level the start level
	 */
	public final void setStartLevel(int level) {
		startLevel.setValue(level);
	}
	
	/**
	 * Sets the start level for selection based on element level
	 * @param level the start level
	 */
	public final void setStartLevel(IntegerParameter level) {
		startLevel.unbind();
		startLevel = level;
		startLevel.bind(subset);
	}
	
	/**
	 * Returns the end level for selection based on element level
	 * @return end level
	 */
	public final IntegerParameter getEndLevel() {
		return endLevel;
	}
	
	/**
	 * Sets the end level for selection based on element level
	 * @param level the end level 
	 */
	public final void setEndLevel(int level) {
		endLevel.setValue(level);
	}
	/**
	 * Sets the end level for selection based on element level
	 * @param level the end level
	 */
	public final void setEndLevel(IntegerParameter level) {
		endLevel.unbind();
		endLevel = level;
		endLevel.bind(subset);
	}

	/**
	 * Returns the identifier of the specified start element for level selection
	 * @return start element id
	 * @deprecated please use {@link #getStartLevel()}
	 */
	public final StringParameter getStartElement() {
		return startElementId;
	}
	
	/**
	 * Sets the identifier of the start element for level selection
	 * @param elementId the start element
	 * @deprecated please use {@link #setStartLevel(int)}
	 */
	public final void setStartElement(String elementId) {
		this.startElementId.setValue(elementId);		
	}
	/**
	 * Sets the identifier of the start element for level selection,
	 * i.e. the parameter value should contain the id of the start element. 
	 * @param element the new <code>StringParameter</code> to use as start
	 * element for the level selection 
	 * @deprecated please use {@link #setStartLevel(IntegerParameter)}
	 */
	public final void setStartElement(StringParameter element) {
		this.startElementId = element;
		this.startElementId.bind(subset);
	}

	/**
	 * Returns the identifier of the specified end element for level selection
	 * @return end element id
	 * @deprecated please use {@link #getEndLevel()}
	 */
	public final StringParameter getEndElement() {
		return endElementId;
	}
	/**
	 * Sets the identifier of the end element for level selection
	 * @param elementId the end element
	 * @deprecated please use {@link #setEndLevel(int)}
	 */
	public final void setEndElement(String elementId) {
		this.endElementId.setValue(elementId);		
	}
	/**
	 * Sets the identifier of the end element for level selection,
	 * i.e. the parameter value should contain the id of the end element. 
	 * @param element the new <code>StringParameter</code> to use as end
	 * element for the level selection 
	 * @deprecated please use {@link #setEndLevel(IntegerParameter)}
	 */
	public final void setEndElement(StringParameter element) {
		this.endElementId = element;
		this.endElementId.bind(subset);
	}
	
	/**
	 * Returns the identifier of the specified revolve element for revolve 
	 * selection
	 * @return revolve element id
	 */
	public final StringParameter getRevolveElement() {
		return revolveElementId;
	}
	/**
	 * Sets the identifier of the revolve element for revolve selection
	 * @param elementId the revolve element
	 */
	public final void setRevolveElement(String elementId) {
		this.revolveElementId.setValue(elementId);		
	}
	/**
	 * Sets the identifier of the revolve element for revolve selection,
	 * i.e. the parameter value should contain the id of the revolve element. 
	 * @param element the new <code>StringParameter</code> to use as revolve
	 * element for revolve selection 
	 */
	public final void setRevolveElement(StringParameter elementId) {
		this.revolveElementId = elementId;
		this.revolveElementId.bind(subset);
	}
	
	/**
	 * Returns the number of elements to take for revolve selection  
	 * @return revolve count
	 */
	public final IntegerParameter getRevolveCount() {
		return revolveElementsCount;
	}
	/**
	 * Specifies the number of elements to take for revolve selection
	 * @param revolveElementsCount the new revolve count
	 */
	public final void setRevolveCount(int revolveElementsCount) {
		this.revolveElementsCount.setValue(revolveElementsCount);
	}
	/**
	 * Sets the revolve count, i.e. the parameter value should contain the 
	 * number of elements to take for revolve selection
	 * @param revolveElementsCount the new <code>IntegerParameter</code> to use 
	 * as revolve count
	 */
	public final void setRevolveCount(IntegerParameter revolveElementsCount) {
		this.revolveElementsCount = revolveElementsCount;
		this.revolveElementsCount.bind(subset);
	}
	
	/**
	 * Returns the revolve mode, i.e. one of the predefined revolve mode
	 * constants
	 * @return the revolve mode
	 */
	public final IntegerParameter getRevolveMode() {
		return revolveMode;
	}
	/**
	 * Sets the revolve mode. One of the predefined revolve mode constants 
	 * should be used
	 * @param revolveMode the new revolve mode
	 */
	public final void setRevolveMode(int revolveMode) {
		this.revolveMode.setValue(revolveMode);
	}
	/**
	 * Sets the revolve mode, i.e. the parameter value should be one of the 
	 * predefined revolve mode constants 
	 * @param revolveMode the new <code>IntegerParameter</code> to use for  
	 * revolve mode
	 */
	public final void setRevolveMode(IntegerParameter revolveMode) {
		this.revolveMode = revolveMode;
		this.revolveMode.bind(subset);
	}
	
	public final void bind(Subset2 subset) {
		super.bind(subset);
		//bind internal:
		refElementId.bind(subset);
		exclusive.bind(subset);
		above.bind(subset);
		hideMode.bind(subset); 
		startElementId.bind(subset);
		endElementId.bind(subset);
		startLevel.bind(subset);
		endLevel.bind(subset);
		revolveElementId.bind(subset);
		revolveElementsCount.bind(subset);
		revolveMode.bind(subset);		
	}
	public final void unbind() {
		super.unbind();
		//unbind internal:
		refElementId.unbind();
		exclusive.unbind();
		above.unbind();
		hideMode.unbind(); 
		startElementId.unbind();
		endElementId.unbind();
		revolveElementId.unbind();
		revolveElementsCount.unbind();
		revolveMode.unbind();
	}

	public final void adapt(FilterSetting from) {
		if(!(from instanceof HierarchicalFilterSetting))
			return;
		
		HierarchicalFilterSetting setting = (HierarchicalFilterSetting)from;
		reset();
		setRefElement(setting.getRefElement().getValue());
		setExclusive(setting.getExclusive().getValue());
		setAbove(setting.getAbove().getValue());
		
		setHideMode(setting.getHideMode().getValue());

		setStartElement(setting.getStartElement().getValue());
		setEndElement(setting.getEndElement().getValue());
		setEndLevel(setting.getEndLevel().getValue());
		setStartLevel(setting.getStartLevel().getValue());
		
		setRevolveElement(setting.getRevolveElement().getValue());
		setRevolveCount(setting.getRevolveCount().getValue());
		setRevolveMode(setting.getRevolveMode().getValue());
	}
}
