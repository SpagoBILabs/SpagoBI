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
/*
 * Created on 13-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.metadata.SbiDomains;

import java.util.List;

/**
 * Defines the interfaces for all methods needed to operate with a domain.
 */
public interface IDomainDAO {
	
	/**
	 * Loads all possible domain that refer to a given domain type, storing each
	 * of them into a <code>Domain</objects> and after putting all objects into
	 * a list, which is returned.
	 * 
	 * @param domainType The String identifying the domain type
	 * 
	 * @return The list of all domains
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	
	public List loadListDomainsByType(String domainType) throws EMFUserError;

	/**
	 * Returns the domain identified by the input parameter <code>id</code>,
	 * storing it in a <code>Domain</code> object.
	 * 
	 * @param id The identifier domain id
	 * 
	 * @return The <code>Domain</code> object storing the domain
	 * 
	 * @throws EMFUserError if an Exception occurs
	 */
	public  Domain loadDomainById(Integer id)
			throws EMFUserError;
	
	/**
	 * Returns the domain identified by the two input parameters <code>codeDomain</code>
	 * and <code>codeValue</code>, storing it in a <code>Domain</code> object.
	 * 
	 * @param codeDomain The identifier domain code
	 * @param codeValue The identifier domain value code
	 * 
	 * @return The <code>Domain</code> object storing the domain
	 * 
	 * @throws EMFUserError if an Exception occurs
	 */
	public  Domain loadDomainByCodeAndValue(String codeDomain, String codeValue)
			throws EMFUserError;
	
	/**
	 * Returns the domain identified by the two input parameters <code>codeDomain</code>
	 * and <code>codeValue</code>, storing it in a <code>Domain</code> object.
	 * 
	 * @param codeDomain The identifier domain code
	 * @param codeValue The identifier domain value code
	 * 
	 * @return The <code>Domain</code> object storing the domain
	 * 
	 * @throws EMFUserError if an Exception occurs
	 */
	public  SbiDomains loadSbiDomainByCodeAndValue(String codeDomain, String codeValue)
			throws EMFUserError;
	
	
	/**
	 * Loads all possible domain, storing each
	 * of them into a <code>Domain</objects> and after putting all objects into
	 * a list, which is returned.
	 * 
	 * @return The list of all domains
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	
	public List loadListDomains() throws EMFUserError;
	
}