/*
*
* @file XMLAServerInfo.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: XMLAServerInfo.java,v 1.8 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

import com.tensegrity.palojava.ServerInfo;

public class XMLAServerInfo implements ServerInfo {
	private String id;
	private String name;
	private String description;
	private String url;
	private String authentication;
	
	XMLAServerInfo(String name) {
		this.id = name;
		this.name = name;
	}
	
	public int getBugfixVersion() {
		return 0;
	}

	public int getBuildNumber() {
		return 0;
	}

	public int getMajor() {
		return 4;
	}

	public int getMinor() {
		return 0;
	}

	public boolean isLegacy() {
		return false;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public int getType() {
		return 3;
	}
	
	public String getDescription() {
		return description;	
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public boolean canBeModified() {
		return false;
	}

	public boolean canCreateChildren() {
		return false;
	}

	public int getEncryption() {
		return 0;
	}

	public int getHttpsPort() {
		return 0;
	}

	public String getServerType() {
		return "XMLA";
	}

	public String getVersion() {
		// TODO implement getVersion
		return "0";
	}

	public String [] getProperties() {
		return new String[] {"SecurityInfoProperty", authentication,
				             "DescriptionProperty", getDescription()};
	}
}
