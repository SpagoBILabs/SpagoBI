/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.datasource.jpa;

import java.io.File;

import it.eng.qbe.classloader.ClassLoaderManager;
import it.eng.qbe.datasource.AbstractDataSourceWithClassLoader;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class JPADataSourceWithClassLoader extends AbstractDataSourceWithClassLoader{
	
	public JPADataSourceWithClassLoader(IDataSource wrappedDataSource) {
		super(wrappedDataSource);
	}


	public void open() {
		File jarFile = null;
		
		FileDataSourceConfiguration configuration = ((JPADataSource)wrappedDataSource).getFileDataSourceConfiguration();
		
		jarFile = configuration.getFile();
		if(jarFile == null) return;
		
		myClassLoader = ClassLoaderManager.updateCurrentClassLoader(jarFile);
		
	}


		
	
	
}
