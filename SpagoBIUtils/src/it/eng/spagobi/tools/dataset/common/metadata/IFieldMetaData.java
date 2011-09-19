/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.metadata;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IFieldMetaData {
	
    String getName();
    String getAlias();
    Class getType();
    Object getProperty(String propertyName);
   
    void setName(String name);
    void setAlias(String alias);
    void setType(Class type);
    void setProperty(String propertyName, Object propertyValue);
}
