/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection;

/**
 * @author Andrea Gioia
 *
 */
public interface IConnection {
	public static int JNDI_CONNECTION = 1;
	public static int JDBC_CONNECTION = 2;
	public static int XMLA_CONNECTION = 3;
	
	public String getName();
	public int getType();
}
