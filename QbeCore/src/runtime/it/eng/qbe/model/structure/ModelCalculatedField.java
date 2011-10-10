/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.model.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrea Gioia
 */
public class ModelCalculatedField extends ModelField {
	
	String expression;
	boolean inLine;
	List<Slot> slots;
	
	public ModelCalculatedField(String name, String type, String expression) {
		setName(name);
		setType(type);
		setExpression(expression);
		inLine = false;
		slots = new ArrayList<Slot>();
		initProperties();
	}
	
	public ModelCalculatedField(String name, String type, String expression, boolean inLine) {
		setName(name);
		setType(type);
		setExpression(expression);
		this.inLine = inLine;
		slots = new ArrayList<Slot>();
	}
	
	public ModelCalculatedField(String name, IModelEntity parent, String type, String expression) {
		super(name, parent);
		setType(type);
		setExpression(expression);
		slots = new ArrayList<Slot>();
	}
	
	public boolean hasSlots() {
		return slots.size() > 0;
	}
	
	public void addSlot(Slot slot) {
		slots.add(slot);
	}
	
	public void addSlots(List<Slot> slots) {
		slots.addAll(slots);
	}
	
	public List<Slot> getSlots() {
		return slots;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}	
	
	public boolean isBoundToDataMart() {
		return getStructure() != null && getParent() != null;
	}

	public boolean isInLine() {
		return inLine;
	}

	public void setInLine(boolean inLine) {
		this.inLine = inLine;
	}
	
	public IModelField clone(IModelEntity newParent){
		IModelField field = new ModelCalculatedField(expression, newParent, getType(), expression);
		field.setProperties(properties);
		return field;
	}
	
	public static class Slot {
		String value;
		List<MappedValuesDescriptor> mappedValues;
		
		public interface MappedValuesDescriptor {}
		
		public static class MappedValuesRangeDescriptor implements MappedValuesDescriptor {
			public String minValue;
			public boolean includeMinValue;
			public String maxValue;
			public boolean includeMaxValue;
			
			public MappedValuesRangeDescriptor(String minValue, String maxValue) {
				this.minValue = minValue;
				includeMinValue = true;
				this.maxValue = maxValue;
				includeMaxValue = false;
			}
			
			public String getMinValue() { return minValue; }
			public void setMinValue(String minValue) { this.minValue = minValue; } 
			public boolean isIncludeMinValue() { return includeMinValue; }
			public void setIncludeMinValue(boolean includeMinValue) { this.includeMinValue = includeMinValue; }
			public String getMaxValue() { return maxValue; } 
			public void setMaxValue(String maxValue) { this.maxValue = maxValue; } 
			public boolean isIncludeMaxValue() { return includeMaxValue; }
			public void setIncludeMaxValue(boolean includeMaxValue) { this.includeMaxValue = includeMaxValue; }
		}
		
		public static class MappedValuesPunctualDescriptor implements MappedValuesDescriptor {
			public Set<String> punctualValues;
			
			public MappedValuesPunctualDescriptor() {
				punctualValues = new HashSet();
			}
			
			public void addValue(String v) { punctualValues.add(v); }
			public Set<String> getValues(String v) { return punctualValues; }
		}
		
		public Slot(String value) {
			this.value = value;
			mappedValues = new ArrayList<MappedValuesDescriptor>();
		}
		
		public void addMappedValuesDescriptors(MappedValuesDescriptor descriptor) {
			mappedValues.add(descriptor);
		}
		
		public List<MappedValuesDescriptor> getMappedValuesDescriptors() {
			return mappedValues;
		}
		
		
	}
	
	
}
