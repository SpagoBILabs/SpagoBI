/**
 * 
 * LICENSE: see BIRT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.birt.exceptions;

public class ConnectionDefinitionException extends Exception {
	
	protected String description;
	
	/**
	 * Instantiates a new connection definition exception.
	 */
	public ConnectionDefinitionException() {
		super();
	}
	
	/**
	 * Instantiates a new connection definition exception.
	 * 
	 * @param msg the msg
	 */
	public ConnectionDefinitionException(String msg) {
		super(msg);
		this.description = msg;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
