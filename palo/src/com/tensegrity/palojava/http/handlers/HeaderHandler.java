/*
*
* @file HeaderHandler.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author ArndHouben
*
* @version $Id: HeaderHandler.java,v 1.5 2010/02/17 14:32:19 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 * All rights reserved
 */
package com.tensegrity.palojava.http.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.HttpParser;

/**
* {@<describe>}
* <p>
* This <code>HttpHandler</code> is used to parse header strings of a palo
* server http response. It provides usefull methods for accessing the fields.
* Furthermore it will notice if a header field changed due to changes in the
* palo server. In future version one can register an listener to get notified
* about those changes
* </p>
* {@</describe>}
*
* @author ArndHouben
* @version $Id: HeaderHandler.java,v 1.5 2010/02/17 14:32:19 PhilippBouillon Exp $
*/
public class HeaderHandler {
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final HeaderHandler instance = new HeaderHandler();
	public static final HeaderHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private int contentLength;
	private HttpConnection connection;
	private int errorCode;
	private String errorMessage;
	
//	private int srvToken;			//altered when a server was changed, e.g. a database added
//	private long databaseToken;		//altered when a database was changed
//	private long dimensionToken;	//altered when a dimension was changed
//	private long cubeToken;			//altered when a cube was changed
//	private boolean legacy;			//is it a legacy server
//	private boolean serverStructureChanged;
	
	private HeaderHandler() {		
	}
	
	/**
	 * Parses the palo server response and reads out all known header fields
	 * @param reader a <code>BufferedReader</code> which contains the palo 
	 * server response
	 */
	public final void parse(InputStream in)
			throws NumberFormatException, IOException, SocketException {
//		int token;
		String response = null;
       for(;;) {
			response = HttpParser.readLine(in);
			if ((response == null) || (response.trim().length() < 1)) {
				break;
			}
			
			if (response.startsWith("HTTP/1.1")) {
				String code = response.substring(9).trim();				
				int index = code.indexOf(" ");
				setErrorCode(Integer.parseInt(code.substring(0, index)));
				setErrorMessage(code.substring(index).trim());				
			}
			// if cascade:
			if (response.startsWith("Content-Length:"))
				setContentLength(Integer.parseInt(response.substring(16)));
			else if (response.startsWith("X-PALO-SV:")) {
				connection.setServerToken(
						Integer.parseInt(response.substring(11)));
			}
			// else if (response.startsWith("X-PALO-DB:")) {
//				token = Integer.parseInt(response.substring(11));
//				if (token != databaseToken) {
//					databaseToken = token;
//					headerEvent = ServerEvent.DATABASE_CHANGED;
//				}
//			} else if (response.startsWith("X-PALO-DIM:")) {
//				token = Integer.parseInt(response.substring(12));
//				if (token != dimensionToken) {
//					dimensionToken = token;
//					headerEvent = ServerEvent.DIMENSION_CHANGED;
//				}
//			} else if (response.startsWith("X-PALO-CB:")) {
//				token = Integer.parseInt(response.substring(11));
//				if (token != cubeToken) {
//					cubeToken = token;
//					headerEvent = ServerEvent.CUBE_CHANGED;
//				}
//			}
       }
	}
	
	private final synchronized void use(HttpConnection connection) {
		this.connection = connection;
	}
	
	private final synchronized void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	private final synchronized void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Returns the length of the subsequent content data
	 * @return the content length
	 */
	public final synchronized int getContentLength() {
		return contentLength;
	}
	
	public final synchronized int getErrorCode() {
		return errorCode;
	}
	
	public final synchronized String getErrorMessage() {
		return errorMessage;
	}
	
//	/**
//	 * Returns the value of the server token header field
//	 * @return the server token value
//	 */
//	public synchronized final long getServerToken() {
//		return serverToken;
//	}
//	public final synchronized boolean serverStructureChanged() {
//		return serverStructureChanged;		
//	}
//	
//	public final synchronized void setServerStructureChanged(boolean b) {
//		serverStructureChanged=b;		
//	}
//	
//	private final synchronized void setServerToken(int srvToken) {
//		this.srvToken = srvToken;
//	}
//	
//	public final synchronized int getServerToken() {
//		return this.srvToken;
//	}

	private final synchronized void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

}