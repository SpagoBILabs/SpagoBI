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
package it.eng.qbe.model.i18n;

import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.spagobi.commons.utilities.StringUtilities;

import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class DatamartLabels.
 * 
 * @author Andrea Gioia
 */
public class DatamartLabels {
	
	/** The properties. */
	private Properties  properties;
	
	/**
	 * Instantiates a new datamart labels.
	 */
	public DatamartLabels() {
		this(new Properties());
	}	
	
	/**
	 * Instantiates a new datamart labels.
	 * 
	 * @param properties the properties
	 */
	public DatamartLabels(Properties  properties) {
		setProperties(properties);
	}	
	
	private String getItemUniqueNameInFile( DataMartEntity entity ) {
		return entity.getUniqueName().replaceAll(":", "/");
	}
	
	private String getItemUniqueNameInFile( DataMartField field ) {
		return field.getUniqueName().replaceAll(":", "/");
	}
	
	public String getLabel(Object datamartItem) {
		String label;
		String itemUniqueNameInFile;
		
		if(properties == null) {
			return null;
		}
		
		if( datamartItem instanceof DataMartEntity ) {
			itemUniqueNameInFile = getItemUniqueNameInFile( (DataMartEntity)datamartItem );
		} else if( datamartItem instanceof DataMartField ) {
			itemUniqueNameInFile = getItemUniqueNameInFile( (DataMartField)datamartItem );
		} else {
			// fail fast
			throw new IllegalArgumentException("[datamartItem] is an instance of class " + datamartItem.getClass().getName() + ".[datamartItem] can be only an instance of class DataMartEntity or of class DataMartField");
		}
		label = (String)properties.get( itemUniqueNameInFile );		
		return StringUtilities.isNull( label )? null: label.trim();
	}
	
	public String getTooltip(Object datamartItem) {
		String tooltip;
		String itemUniqueNameInFile;
		
		if(properties == null) {
			return null;
		}
		
		if( datamartItem instanceof DataMartEntity ) {
			itemUniqueNameInFile = getItemUniqueNameInFile( (DataMartEntity)datamartItem );
		} else if( datamartItem instanceof DataMartField ) {
			itemUniqueNameInFile = getItemUniqueNameInFile( (DataMartField)datamartItem );
		} else {
			// fail fast
			throw new IllegalArgumentException("[datamartItem] is an instance of class " + datamartItem.getClass().getName() + ".[datamartItem] can be only an instance of class DataMartEntity or of class DataMartField");
		}
		tooltip = (String)properties.get( itemUniqueNameInFile + ".tooltip");		
		return StringUtilities.isNull( tooltip )? null: tooltip.trim();
	}
	
	
	private Properties getProperties() {
		return properties;
	}

	private void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void addDatamartLabels(DatamartLabels labels) {
		if (labels != null && labels.properties != null && !labels.properties.isEmpty()) {
			this.properties.putAll(labels.properties);
		}
	}
}
