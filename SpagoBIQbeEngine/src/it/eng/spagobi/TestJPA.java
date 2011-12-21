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
package it.eng.spagobi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;

public class TestJPA {

	private static EntityManagerFactory emf;
	
	private EntityManagerFactory createEMF() {
		try {
			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("foodmart");

			return emf;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}
	}
	protected final EntityManagerFactory getEMF() {
		if (emf == null) {
			emf = createEMF();
		}

		return emf;
	}	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestJPA t= new TestJPA();
		t.createEMF();
		EntityManager em = t.getEMF().createEntityManager();
		String query = " SELECT t_0.storeCost, t_0.storeId.storeCountry FROM  SalesFact1998 t_0 WHERE t_0.storeId.storeCountry='USA' AND  t_0.promotionId=t_0.promotionId";
		Query q  = em.createQuery(query);
		
		
		List<String> queryParameters = new ArrayList<String>();
		queryParameters.add("USA");
		EJBQueryImpl qi = (EJBQueryImpl)q;
		String sqlQueryString = qi.getDatabaseQuery().getSQLString();
		System.out.println(sqlQueryString);
		EJBQueryImpl countQuery = (EJBQueryImpl)em.createNativeQuery("SELECT COUNT(*) FROM (" + sqlQueryString + ") temp");
		for(int i=0; i<queryParameters.size(); i++ ){
			countQuery.setParameter(1+i, queryParameters.get(i));
		}
		System.out.println("result "+countQuery.getDatabaseQuery().getSQLString());


		System.out.println(((Long)countQuery.getResultList().get(0)).intValue());

	}

}
