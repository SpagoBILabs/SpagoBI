/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.transformer;

import java.util.List;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public interface IDataTransformer {
	
    List transformData(List records);
    
    /**
     * IDataTransformer is a general interface for transformer. this method is specific of one kind of
     * transformer so it must be removed from here
     * 
     * @deprectade
     */
    List transformData(List records, String pivotColumn,  String pivotRow, String pivotValue);
}
