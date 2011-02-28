package it.eng.spagobi;

import it.eng.qbe.statment.jpql.JPQLStatement;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.mappings.DatabaseMapping;

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
