/*
*
* @file Resource.java
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
* @author AndreasEbbert
*
* @version $Id: Resource.java,v 1.2 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

/* (c) 2007 Tensegrity Software */
package com.tensegrity.palo.xmla.builders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Empty class to load the resources.
 * @author AndreasEbbert
 * @version $Id: Resource.java,v 1.2 2009/04/29 10:35:37 PhilippBouillon Exp $
 */
public class Resource {
  private static String baseFunctions = null;
  private Resource() {}
  
  public final static String getBaseFunctions() {
	  if (baseFunctions == null) {
		  baseFunctions = getXMLString("baseFunctions.xml");
	  }
	  return baseFunctions;
  }

  final static String getXMLString(String res) {
    InputStream in = Resource.class.getResourceAsStream(res);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    StringBuffer sb = new StringBuffer();
    try {
      String line = null;
      while ((line = br.readLine()) != null)
        sb.append(line);
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
    finally {
      try {
        in.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }
}
