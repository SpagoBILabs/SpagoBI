/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.commonj.exception;

import it.eng.spago.base.SourceBean;

public class TemplateParseException extends CommonjEngineException {
	
	private SourceBean template;
	
	public TemplateParseException(SourceBean template, String message) {
    	super(message);
    	setTemplate(template);
    }
	
   
    public TemplateParseException(SourceBean template, String message, Throwable ex) {
    	super(message, ex);
    	setTemplate(template);
    }
    
  
    public TemplateParseException(SourceBean template, Throwable ex) {
    	super(ex);
    	setTemplate(template);
    }
    
    public SourceBean getTemplate() {
		return template;
	}


	public void setTemplate(SourceBean template) {
		this.template = template;
	}
}
