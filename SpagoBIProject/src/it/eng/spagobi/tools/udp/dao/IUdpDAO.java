/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.udp.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;

import java.util.List;

import org.hibernate.Session;


/**
 * 
 * @see it.eng.spagobi.udp.bo.Udp
 * @author Antonella Giachino
 */
public interface IUdpDAO {

    public Integer insert(SbiUdp prop);
    
    public void insert(Session session, SbiUdp prop);

    public void update(SbiUdp prop);
    
    public void update(Session session, SbiUdp prop);
	
    public void delete(SbiUdp prop);
    
    public void delete(Session session, SbiUdp prop);

    public void delete(Integer id);
    
    public void delete(Session session, Integer id);
	
    public SbiUdp findById(Integer id);

    public List<SbiUdp> findAll();

    public List<Udp> loadAllByFamily(String familyCode) throws EMFUserError;

    public Udp loadByLabel(String label) throws EMFUserError;

    public Udp loadByLabelAndFamily(String label, String family) throws EMFUserError;

    public Udp loadById(Integer id);
    
    public List<SbiUdp> loadPagedUdpList(Integer offset, Integer fetchSize)throws EMFUserError;
	
	public Integer countUdp()throws EMFUserError;

}

