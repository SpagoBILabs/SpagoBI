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
package it.eng.spagobi.commons.deserializer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DocumentMetadataProperty;
import it.eng.spagobi.analiticalmodel.document.bo.ObjNote;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.bo.Viewpoint;
import it.eng.spagobi.analiticalmodel.execution.service.GetParametersForExecutionAction;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.serializer.TriggerXMLSerializer;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.Periodicity;
import it.eng.spagobi.kpi.config.metadata.SbiKpiComments;
import it.eng.spagobi.kpi.goal.metadata.bo.Goal;
import it.eng.spagobi.kpi.goal.metadata.bo.GoalNode;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelExtended;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResourcesExtended;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrant;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNodeWithGrant;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bo.UserBO;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.metadata.SbiCustomDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class XMLDeserializer implements Deserializer {
	
	Map<Class, Deserializer> mappings;
	
	public XMLDeserializer() {
		mappings = new HashMap();
		mappings.put( Job.class, new JobXMLDeserializer() );
		mappings.put( Trigger.class, new TriggerXMLDeserializer() );
	}

	public Object deserialize(Object o, Class clazz) throws DeserializationException {
		Object result = null;	
		
		try {
			Assert.assertNotNull(o, "Input parameter [" + o + "] cannot be null");
			Assert.assertNotNull(o, "Input parameter [" + clazz + "] cannot be null");
			
			SourceBean xml = null;
			if(o instanceof SourceBean) {
				xml = (SourceBean)o;
			} else if (o instanceof String) {
				xml = SourceBean.fromXMLString( (String)o);
			} else {
				throw new DeserializationException("Impossible to deserialize from an object of type [" + o.getClass().getName() +"]");
			}
			
			Deserializer deserializer = mappings.get(clazz);
			if(deserializer == null) {
				throw new DeserializationException("Impossible to deserialize to an object of type [" + clazz.getName() +"]");
			}
			
			if(xml.getAttribute("ROWS") != null) {
				List list = new ArrayList();
				List<SourceBean> rows = xml.getAttributeAsList("ROWS.ROW");
				for(SourceBean row: rows) {
					list.add( deserializer.deserialize(row, clazz) );
				}
				result = list;
			} else {
				result = deserializer.deserialize(o, clazz);
			}
		} catch (Throwable t) {
			throw new DeserializationException("An error occurred while deserializing object: " + o, t);
		} finally {
			
		}
		
		return result;	
	}


}
