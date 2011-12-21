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
package it.eng.spagobi.tools.udp.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.tools.udp.bo.UdpValue;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;

import java.util.List;

import org.hibernate.Session;


/**
 * 
 * @see it.eng.spagobi.udp.bo.Udp
 * @author Antonella Giachino
 */
public interface IUdpValueDAO extends ISpagoBIDao{

    public Integer insert(SbiUdpValue prop);
    
    public void insert(Session session, SbiUdpValue propValue);

    public void update(SbiUdpValue propValue);
    
    public void update(Session session, SbiUdpValue propValue);
	
    public void delete(SbiUdpValue propValue);
    
    public void delete(Session session, SbiUdpValue propValue);

    public void delete(Integer id);
    
    public void delete(Session session, Integer id);
	
    public SbiUdpValue findById(Integer id);

    public List<SbiUdpValue> findAll();
    
    public UdpValue loadById(Integer id);

	public List findByReferenceId(Integer kpiId, String family);

	public UdpValue loadByReferenceIdAndUdpId(Integer referenceId, Integer udpId, String family);
	
	public void insertOrUpdateRelatedUdpValues(Object object, Object sbiObject, Session aSession, String family) throws EMFUserError;
	
}

