/**
 * 
 */
package it.eng.qbe.test.jpa;

import it.eng.qbe.datasource.jpa.DBConnection;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.jpa.JPQLStatement;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * @author giachino
 *
 */
public class JPAStatementTest {
    
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
		
		jpaDS.setDataMartModelAccessModality(new DataMartModelAccessModality());
		try{
			jpaDS.getEntityManager().getTransaction().begin();

			java.sql.Connection sqlConn = jpaDS.getEntityManager().unwrap(java.sql.Connection.class);
			if (sqlConn != null) {
				DBConnection conn = new DBConnection();
				DatabaseMetaData dbMeta  = sqlConn.getMetaData();
				conn.setDialect(dbMeta.getDatabaseProductName());
				conn.setUrl(dbMeta.getURL());
				conn.setUsername(dbMeta.getUserName());
				conn.setDriverClass(dbMeta.getDriverName());
			}
		}catch (Exception e){
			System.out.println("Error: " + e.getLocalizedMessage());
		}
		
		//fine forzature
		
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
		
		em.close();
		
	}

}
