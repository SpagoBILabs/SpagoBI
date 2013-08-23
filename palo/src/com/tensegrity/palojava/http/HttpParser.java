/*
*
* @file HttpParser.java
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
* @version $Id: HttpParser.java,v 1.9 2010/02/17 14:32:19 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 * All rights reserved
 */
package com.tensegrity.palojava.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.tensegrity.palojava.PaloException;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: HttpParser.java,v 1.9 2010/02/17 14:32:19 PhilippBouillon Exp $
 */
public class HttpParser {

	public static final String DEFAULT_CHARACTER_ENCODING="UTF-8"; 
	private static final char QUOTE = '"'; 
	private static final char TAG_START = '<';
	private static final char TAG_END = '>';
	private static final char AMPERSAND = '&';	//XML ENTITY IN RESPONSE!!
	private static final char SEMICOLON = ';';
	private static final Pattern lineEnd = Pattern.compile(";[\r\n]");

	public static final String readLine(InputStream in) throws IOException {
		return readLine(in,DEFAULT_CHARACTER_ENCODING);
	}
	
	public static final String readLine(InputStream in,String charset) throws IOException {
        byte[] rawdata = readBytes(in);
        if (rawdata == null) {
            return null;
        }
        // strip CR and LF from the end
        int len = rawdata.length;
        int offset = 0;
        if (len > 0) {
            if (rawdata[len - 1] == '\n') {
                offset++;
                if (len > 1) {
                    if (rawdata[len - 2] == '\r') {
                        offset++;
                    }
                }
            }
        }
        try {
			return new String(rawdata, 0, len - offset, charset);
		} catch (UnsupportedEncodingException e) {
			return new String(rawdata, 0, len - offset);
		}
	}

	public static final byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) >= 0) {
            buf.write(ch);
            if (ch == '\n') { // be tolerant (RFC-2616 Section 19.3)
                break;
            }
        }
        if (buf.size() == 0) {
            return null;
        }
        return buf.toByteArray();
	}

	public static final String readRawLine(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int ch;
        int lastCh=-1;
        boolean inQuote = false;
        while ((ch = in.read()) >= 0) {
//        	System.err.print((char)ch);
            buf.write(ch);
            if (ch == '"') {
            	inQuote = !inQuote;
            }
            if(ch == '\n' && lastCh == ';' && !inQuote)
            	break;
            lastCh = ch;
        }
        if (buf.size() == 0) {
            return null;
        }        
        try {
        	return new String(buf.toByteArray(),DEFAULT_CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return new String(buf.toByteArray());
		}
	}
	
	/**
	 * Encodes the given parameter value as csv . A numeric value is represented 
	 * as string and a string value is quoted.
	 * @param paramValue the parameter value to encode
	 * @return the csv encoded parameter value string
	 */
	protected static final String encode(String paramValue) {
		try {
			return URLEncoder.encode(paramValue,DEFAULT_CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return paramValue;
	}
	
	
	/**
	 * Encodes the given values. The result is a colon separated list of url 
	 * encoded values
	 * @param paramValue
	 * @return
	 */
	protected static final String encode(Object[] paramValue) {
		StringBuffer res = new StringBuffer();
		int max = paramValue.length-1;		
		for(int i=0;i<paramValue.length;i++) {
			res.append(encode(paramValue[i].toString())); //,true));
			if(i<max)
				res.append(":");
		}
		return res.toString();
	}

	/**
	 * Parses the given string. The given string can contain more the one line.
	 * The regular expression ';[\r\n]' is used to determine the line end. 
	 * @param response the response string from the palo server
	 * @param delim the values delimiter
	 * @return the decoded values
	 */
	public static final String[][] parse(String response, char delim) {
//		String[] lines = response.split(";[\r\n]");
		String[] lines = lineEnd.split(response,0);
		String[][] res = new String[lines.length][];		
		for(int i=0; i<lines.length;i++) {
			//line termination check:
			lines[i] = lines[i]+";";
			res[i] = parseLine(lines[i],delim);
//			checkResponse(res[i]);
		}
		return res;
	}

	/**
	 * Parses the given csv line. 
	 * @param str the csv line
	 * @param delim the values delimiter
	 * @return the decoded values
	 */
	public static synchronized final String[] parseLine(String str,char delim) {
		ArrayList entries = new ArrayList();
		StringBuffer entry = new StringBuffer();
		char current = ' ';
		char next; // look ahead
		boolean inQuotes = false;	//are we inside a quote, i.e. string value
		boolean inField = true;		//are we inside a value entry
		boolean inTag = false;		//are we inside a xml/html tag <...>
		boolean readAmpersand = false;	//did we read an ampersand?
		str = str.trim();
		for (int i = 0, n = str.length(); i < n; i++) {
			current = str.charAt(i);
			next = (i + 1) < n ? str.charAt(i + 1) : ' ';

			if (inField) {
				if (current == QUOTE) {	//handle quote...
					if(!inTag) { 
						// read in a quote:
						if (!inQuotes) {
							// now we're in the quote
							inQuotes = true;
						} else if (next == QUOTE) {
							// replace double quote with one
							entry.append(QUOTE);
							i++; // skip one quote!!
						} else {
							// end quote
							inQuotes = false;
							// inField = false;
						}
					} else {
						entry.append(QUOTE); //simply append it...
					}
				} else if (current == TAG_START && !inQuotes) {
					if (!inTag) {
						inTag = true;
						entry.append(TAG_START);
					}
				} else if (current == TAG_END && !inQuotes) {
					if(inTag) {
						inTag = false;
						entry.append(TAG_END);
					}
				} else if(current == AMPERSAND && !inQuotes) {
					readAmpersand = true;
					entry.append(AMPERSAND);
				} else if (current == SEMICOLON && readAmpersand) {
					readAmpersand = false;
					entry.append(SEMICOLON);
				} else {
					// not a quote
					if (current == delim && !inQuotes && !inTag) {
						// we've got a separator and we're not in quotes
						// so this entry is done
						entries.add(entry.toString());
						entry.delete(0, entry.length());
					} else {
						// normal character:
						entry.append(current);
					}
				}
			} else {
				// read until we reach next field
				if (current == delim)
					inField = true;
			}
		}
		if(entry.length()>0)
			entries.add(entry.toString());

		return (String[])entries.toArray(new String[entries.size()]);
	}
	
	/**
	 * Checks the given server response contains an error. Errors are
	 * defined as <code>HttpErrorCode</code> objects, see {@link PaloErrorCodes}
	 * In case of an error a <code>PaloException</code> is thrown  
	 * @param responseLine
	 */
	public static final void checkResponse(String[] response) {
		if(response.length < 1)
			throw new PaloException("No response from server!!");
		if (response[0].startsWith("ERROR")) {
			response[0] = response[0].substring(5);
			String errCode = response[0];
			if(PaloErrorCodes.contains(errCode)) {
				if (response.length >= 3) {
					String errMsg = response[1];
					String errReason = response[2];
					// errMsg must be a string...
					if (isString(errMsg))
						throw new PaloException(errCode, errMsg, errReason);
				}
			}
		}
	}
	
	private static final boolean isString(String str) {
		return !Character.isDigit(str.trim().charAt(0));
	}
}
