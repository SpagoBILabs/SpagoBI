/**
 * 
 */
package it.eng.qbe.test.jpa;

import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.builder.DataMartStructureBuilderFactory;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

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
		JPADataSource jpaDS = new JPADataSource("TEST_JPA", new FileDataSourceConfiguration("TEST_JPA", null));
		//fine forzature
		
		//EntityManagerFactory emf = jpaDS.getEntityManagerFactory();
		EntityManager em = jpaDS.getEntityManager();
		IDataMartStructureBuilder dmb = DataMartStructureBuilderFactory.getDataMartStructureBuilder(jpaDS);
		//builds the jpa structure
		DataMartModelStructure dms = dmb.build();
		
		//gets structure's informations
		List allEntities = dms.getRootEntities("TEST_JPA");
		for (int i=0; i< allEntities.size(); i++){
			DataMartEntity entity = (DataMartEntity)allEntities.get(i);
			System.out.println("*** Entity uniqueName: " + entity.getUniqueName());
			System.out.println("* Entity name: " + entity.getName());
			System.out.println("* Entity uniqueType: " + entity.getUniqueType());
			System.out.println("* Entity type: " + entity.getType());			
			System.out.println("* Entity path: " + entity.getPath());
			System.out.println("* Entity role: " + entity.getRole());
			
			List keyFields = entity.getKeyFields();
			for (int k=0; k< keyFields.size(); k++){
				DataMartField key = (DataMartField)keyFields.get(k);				
				System.out.println("*** key Unique Name: " + key.getUniqueName());
				System.out.println("* key Name: " + key.getName());
				System.out.println("* key type: " + key.getType());
				System.out.println("* key Length: " + key.getLength());
				System.out.println("* key Precision: " + key.getPrecision());
			}
			
			List fields = entity.getAllFields();
			for (int j=0; j< fields.size(); j++){
				DataMartField field = (DataMartField)fields.get(j);				
				System.out.println("*** Field Unique Name: " + field.getUniqueName());
				System.out.println("* Field Name: " + field.getName());
				System.out.println("* Field type: " + field.getType());
				System.out.println("* Field Length: " + field.getLength());
				System.out.println("* Field Precision: " + field.getPrecision());
			}
		}
		
		
		//close operations
		//em.clear();
		em.close();
		
	}

}
