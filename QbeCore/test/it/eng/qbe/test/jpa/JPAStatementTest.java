/**
 * 
 */
package it.eng.qbe.test.jpa;

import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.datasource.jpa.JPADriver;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.jpa.JPQLDataSet;
import it.eng.qbe.statment.jpa.JPQLStatement;

import javax.persistence.EntityManager;

/**
 * @author giachino
 *
 */
public class JPAStatementTest {
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		//test for jpa
		
		//create jpaDataSource (entityManager)
		
		JPADriver driver = new JPADriver();
		JPADataSource jpaDS = (JPADataSource)driver.getDataSource(new FileDataSourceConfiguration("TEST_JPA", null));	
		
		
		
		//EntityManagerFactory emf = jpaDS.getEntityManagerFactory();
		EntityManager em = jpaDS.getEntityManager();

		Query queryTest = new Query();
		queryTest.setDescription("queryJPA-1");
		queryTest.setId("q1");
		queryTest.setName("queryJPA-1");
		queryTest.addSelectFiled("it.eng.spagobi.jpa.SbiExtRole:code", null, "code", true, true, false, null, null);
		queryTest.addSelectFiled("it.eng.spagobi.jpa.SbiExtRole:name", null, "name", true, true, false, null, null);
		queryTest.addSelectFiled("it.eng.spagobi.jpa.SbiExtRole:descr", null, "descr", true, true, false, null, null);
	
		JPQLStatement stmt = new JPQLStatement(jpaDS, queryTest);
		stmt.prepare();
		JPQLDataSet jpqlds = new JPQLDataSet(stmt);
		jpqlds.loadData();
		
		em.close();
		
	}

}
