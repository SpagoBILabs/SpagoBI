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
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;

import java.util.List;

import org.hibernate.Session;


/**
 * 
 * @see it.eng.spagobi.udp.bo.Udp
 * @author Antonella Giachino
 */
public interface IUdpDAO extends ISpagoBIDao{

    public Integer insert(SbiUdp prop);

    public void update(SbiUdp prop);
	
    public void delete(SbiUdp prop);

    public void delete(Integer id);

	
    public SbiUdp findById(Integer id);

    public List<SbiUdp> findAll();

    public List<Udp> loadAllByFamily(String familyCode) throws EMFUserError;

    public Udp loadByLabel(String label) throws EMFUserError;

    public Udp loadByLabelAndFamily(String label, String family) throws EMFUserError;

    public Udp loadById(Integer id);
    
    public List<SbiUdp> loadPagedUdpList(Integer offset, Integer fetchSize)throws EMFUserError;
	
	public Integer countUdp()throws EMFUserError;

}

