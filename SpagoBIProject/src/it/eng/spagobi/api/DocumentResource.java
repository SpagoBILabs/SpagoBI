/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 */
@Path("/1.0/documents")
public class DocumentResource {
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDocuments() {
		return null;
	}
	
	@GET
	@Path("/{label}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDocumentDetails(@PathParam("label") String label) {
		return null;
	}
	
	@POST
	@Path("/{label}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String insertDocument(@PathParam("label") String label) {
		return null;
	}
	
	@PUT
	@Path("/{label}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String updateDocument(@PathParam("label") String label) {
		return null;
	}
	
	@GET
	@Path("/{label}/meta")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDocumentMeta(@PathParam("label") String label) {
		return null;
	}
}
