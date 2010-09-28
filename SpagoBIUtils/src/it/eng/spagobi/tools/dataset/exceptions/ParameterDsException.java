/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.tools.dataset.exceptions;

import it.eng.spago.error.EMFInternalError;

public class ParameterDsException extends DatasetException {

	public static final String USER_MESSAGE = "Errors in the following parameter(s): ";



	public ParameterDsException(String severity, int code,
			EMFInternalError e, String parameters) {
		super(severity, code, e);
		message = USER_MESSAGE + parameters;
	}

	public ParameterDsException(String severity, int code,
			EMFInternalError e) {
		super(severity, code, e);
	}

	public ParameterDsException(String severity, int code,
			Exception e, String parameters) {
		super(severity, code, e);
		message = USER_MESSAGE + parameters;
	}


}
