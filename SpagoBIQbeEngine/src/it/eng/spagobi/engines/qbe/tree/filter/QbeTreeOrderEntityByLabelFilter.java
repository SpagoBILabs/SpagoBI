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
package it.eng.spagobi.engines.qbe.tree.filter;

import it.eng.qbe.cache.QbeCacheManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.i18n.ModelI18NProperties;
import it.eng.qbe.model.structure.DataMartEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class QbeTreeOrderEntityFilter.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeTreeOrderEntityByLabelFilter extends ComposableQbeTreeEntityFilter{

	private Locale locale;
	
	/**
	 * Instantiates a new qbe tree order entity filter.
	 */
	public QbeTreeOrderEntityByLabelFilter() {
		super();
	}
	
	public QbeTreeOrderEntityByLabelFilter(IQbeTreeEntityFilter parentFilter, Locale locale) {
		super(parentFilter);
		this.setLocale( locale );
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.qbe.tree.filter.ComposableQbeTreeEntityFilter#filter(it.eng.qbe.model.IDataMartModel, java.util.List)
	 */
	public List filter(IDataSource dataSource, List entities) {
		List list = null;
		
		ComparableEntitiesList comparableEntities = new ComparableEntitiesList(dataSource, locale);
		comparableEntities.addEntities( entities );
		list = comparableEntities.getEntitiesOrderedByLabel();
		
		return list;
	}
	
	/**
	 * The Class ComparableEntitiesList.
	 */
	private class ComparableEntitiesList {

		/** The list. */
		private List list;
		
		/** The datamart model. */
		private IDataSource dataSource;
		private ModelI18NProperties datamartLabels;
		
		/**
		 * Instantiates a new comparable entities list.
		 * 
		 * @param dataSource the datamart model
		 */
		ComparableEntitiesList(IDataSource dataSource, Locale locale) {
			
			list = new ArrayList();
			this.dataSource = dataSource;
			//setDatamartLabels( QbeCacheManager.getInstance().getLabels( dataSource , locale ) );
			setDatamartLabels( dataSource.getModelI18NProperties(locale) );
			if( getDatamartLabels() == null) {
				setDatamartLabels( new ModelI18NProperties() );
			}
		}
		
		/**
		 * Adds the entity.
		 * 
		 * @param entity the entity
		 */
		void addEntity(DataMartEntity entity) {
			String label = geEntityLabel( entity );	
			EntityWrapper field = new EntityWrapper(label, entity);
			list.add(field);
		}
		
		private String geEntityLabel(DataMartEntity entity) {
			String label;
			label = getDatamartLabels().getLabel(entity);
			return label==null? entity.getName(): label;
		}
		
		
		
		/**
		 * Adds the entities.
		 * 
		 * @param entities the entities
		 */
		void addEntities(Set entities) {
			if (entities != null && entities.size() > 0) {
				Iterator it = entities.iterator();
				while (it.hasNext()) {
					DataMartEntity relation = (DataMartEntity) it.next();
					addEntity(relation);
				}
			}
		}
		
		/**
		 * Adds the entities.
		 * 
		 * @param relations the relations
		 */
		void addEntities(List relations) {
			if (relations != null && relations.size() > 0) {
				Iterator it = relations.iterator();
				while (it.hasNext()) {
					DataMartEntity entity = (DataMartEntity) it.next();
					addEntity(entity);
				}
			}
		}
		
		/**
		 * Gets the entities ordered by label.
		 * 
		 * @return the entities ordered by label
		 */
		List getEntitiesOrderedByLabel () {
			Collections.sort(list);
			List toReturn = new ArrayList();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				EntityWrapper field = (EntityWrapper) it.next();
				toReturn.add(field.getEntity());
			}
			return toReturn;
		}

		private ModelI18NProperties getDatamartLabels() {
			return datamartLabels;
		}

		private void setDatamartLabels(ModelI18NProperties datamartLabels) {
			this.datamartLabels = datamartLabels;
		}
		
	}
	
	
	/**
	 * The Class EntityWrapper.
	 */
	private class EntityWrapper implements Comparable {
		
		/** The entity. */
		private DataMartEntity entity;
		
		/** The label. */
		private String label;
		
		/**
		 * Instantiates a new entity wrapper.
		 * 
		 * @param label the label
		 * @param entity the entity
		 */
		EntityWrapper (String label, DataMartEntity entity) {
			this.entity = entity;
			this.label = label;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			if (o == null) throw new NullPointerException();
			if (!(o instanceof EntityWrapper)) throw new ClassCastException();
			EntityWrapper anotherEntity = (EntityWrapper) o;
			return this.getLabel().compareTo(anotherEntity.getLabel());
		}
		
		/**
		 * Gets the entity.
		 * 
		 * @return the entity
		 */
		public DataMartEntity getEntity() {
			return entity;
		}
		
		/**
		 * Sets the entity.
		 * 
		 * @param entity the new entity
		 */
		public void setEntity(DataMartEntity entity) {
			this.entity = entity;
		}
		
		/**
		 * Gets the label.
		 * 
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		
		/**
		 * Sets the label.
		 * 
		 * @param label the new label
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		
	}


	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
