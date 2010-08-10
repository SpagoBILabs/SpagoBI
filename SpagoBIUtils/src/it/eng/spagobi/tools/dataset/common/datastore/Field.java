/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datastore;



/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class Field implements IField {
	Object value = null;



    public Field(Object value) {
		super();
		this.value = value;
	}
    


	public Field() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Object getValue() {
    	return value;
    }


	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return "" + getValue();
	}

}
