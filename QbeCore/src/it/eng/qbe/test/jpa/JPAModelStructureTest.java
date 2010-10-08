/**
 * 
 */
package it.eng.qbe.test.jpa;

import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.builder.DataMartStructureBuilderFactory;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Metamodel;

/**
 * @author giachino
 *
 */
public class JPAModelStructureTest {
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//test for jpa
		
		//create jpaDataSource (entityManager)
		
		
		//forzature: da capire come valorizzare ed eliminarle
		JPADataSource jpaDS = new JPADataSource("TEST_JPA");
		jpaDS.setDatamartName("TEST_JPA");
		List ar = new ArrayList();
		ar.add("TEST_JPA");
		jpaDS.setDatamartNames(ar);
		//fine forzature
		
		//EntityManagerFactory emf = jpaDS.getEntityManagerFactory();
		EntityManager em = jpaDS.getEntityManager();
		IDataMartStructureBuilder dmb = DataMartStructureBuilderFactory.getDataMartStructureBuilder(jpaDS);
		//builds the jpa structure
		DataMartModelStructure dms = dmb.build();
		
		
		//close operations
		//em.clear();
		em.close();
		
	}

}
