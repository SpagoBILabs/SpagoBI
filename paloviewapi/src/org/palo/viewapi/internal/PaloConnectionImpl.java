/*
*
* @file PaloConnectionImpl.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: PaloConnectionImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import org.palo.viewapi.PaloConnection;

/**
 * <code>Connection</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: PaloConnectionImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class PaloConnectionImpl extends DomainObjectImpl implements PaloConnection {

	private int type;
	private String name;
	private String host;
	private String service;
	private String description;	
	
	PaloConnectionImpl(String id) {
		super(id);
	}
	private PaloConnectionImpl(Builder builder) {
		super(builder.id);
		type = builder.type;
		name = builder.name;
		host = builder.host;
		service = builder.service;
		description = builder.description;			
	}
	
	public final String getDescription() {
		return description != null ? description : "";
	}

	public final String getHost() {
		return host != null ? host : "";
	}

	public final String getName() {
		return name != null ? name : "";
	}
	
	public final String getService() {
		return service != null ? service : "";
	}

	public final int getType() {
		return type;
	}
	
	public final boolean equals(Object obj) {
		if (obj instanceof PaloConnection) {
			PaloConnection other = (PaloConnection) obj;
			return getId().equals(other.getId()) 
					&& getType() == other.getType()
					&& getHost().equals(other.getHost())
					&& getService().equals(other.getService());
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 17;
		hc += 31 * type;
		hc += 31 * getId().hashCode();
		hc += 31 * getHost().hashCode();
		hc += 31 * getService().hashCode();
		return hc;
	}

	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	final void setDescription(String description) {
		this.description = description;
	}	
	final void setHost(String host) {
		this.host = host;
	}
	final void setName(String name) {
		this.name = name;
	}
	final void setService(String service) {
		this.service = service;
	}	
	final void setType(int type) {
		this.type = type;
	}
	
	
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private int type;
		private String name;
		private String host;
		private String service;
		private String description;	
		
		public Builder(String id) {
			AccessController.checkAccess(PaloConnection.class);
			this.id = id;
		}

		public Builder type(int type) {
			this.type = type;
			return this;
		}
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		public Builder host(String host) {
			this.host = host;
			return this;
		}
		public Builder service(String service) {
			this.service = service;
			return this;
		}
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		public PaloConnection build() {
			return new PaloConnectionImpl(this);
		}
	}

}
