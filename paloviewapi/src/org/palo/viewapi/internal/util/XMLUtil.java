/*
*
* @file XMLUtil.java
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
* @author Stepan Rutz
*
* @version $Id: XMLUtil.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.viewapi.internal.util;

/**
 * <code></code>
 *
 * @author Stepan Rutz
 * @version $Id: XMLUtil.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class XMLUtil
{	
    private XMLUtil()
    {
    }
 
    /**
     * Simply surrounds given string with double-quotes
     * @param str
     * @return
     */
    public static final String quote(String str) {
    	return "\""+str+"\"";
    }
    
	/**
	 * Simply removes double-quotes from the beginning and end of given string
	 * @param str
	 * @return
	 */
	public static final String dequote(String str) {
		if(str.startsWith("\""))
			str = str.substring(1);
		if(str.endsWith("\""))
			str = str.substring(0, str.length()-1);
		return str;
	}

    /**
     * Replaces all html/xml expressions for newline and carriage return within
     * the given string value with their java counterpart, namely '\n' and '\r'.
     * @param strVal
     * @return
     */
    public static String dequoteString(String strVal) {
    	String newStr = strReplace(strVal, "&#13;", "\r"); //$NON-NLS-1$ //$NON-NLS-2$
    	return strReplace(newStr, "&#10;", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Replaces all newlines and carriage returns within the given string value
     * with the corresponding html/xml expression
     * @param strVal
     * @return
     */
    public static String quoteString(String strVal)
    {
        String newStr = strVal;
        newStr = strReplace(newStr, "\r", "&#13;"); //$NON-NLS-1$ //$NON-NLS-2$
        newStr = strReplace(newStr, "\n", "&#10;"); //$NON-NLS-1$ //$NON-NLS-2$
        return newStr;
    }
    
    public static String strReplace(String string, String token, String replaceString)
    {
        String newStr = string;
        int i = string.indexOf(token);
        while (i > -1)
        {
            newStr = string.substring(0, i);
            newStr += replaceString;
            if (string.length() > (i+token.length()))
            {
                newStr += string.substring(i+token.length());
            }
            string = newStr;
            i = string.indexOf(token);
        }
        
        return newStr;
    }
    
    public static String printQuoted(int i) {
    	return printQuoted(Integer.toString(i));
    }
    
    public static String printQuoted (String s)
    {
        /* this method quotes xml(!!) attribute values which may not
           contain any of the following literal chars  & < " 
           if they are enclosed in double-quotes ", if the quoting is
           ' then double-quotes are legal, but ' must be quoted. we
           assume double-quotes here though. /sr */
        // pre-quote linefeeds
        s = quoteString(s);
        
        // Quote other harmful characters
        StringBuffer bf = new StringBuffer();
        for (int i = 0, j = s.length(); i < j; ++i)
        {
            char c = s.charAt(i);
            switch (c)
            {
                case '&':
                case '<':
                case '"':
                bf.append("&#x"); //$NON-NLS-1$
                bf.append(Integer.toHexString(c));
                bf.append(';');
                break;
                
                default:
                bf.append(c);
            }
        }
        return bf.toString();
    }
    

}
