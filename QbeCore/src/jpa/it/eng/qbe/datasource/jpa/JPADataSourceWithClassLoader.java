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
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class JPADataSourceWithClassLoader extends JPADataSource{
	
	protected static ClassLoader defoutlClassLoader;
	
	protected ClassLoader myClassLoader;

	public JPADataSourceWithClassLoader(String dataSourceName, IDataSourceConfiguration configuration){
		super(dataSourceName, configuration);
		if(defoutlClassLoader==null){
			defoutlClassLoader = Thread.currentThread().getContextClassLoader();
		}else{
			Thread.currentThread().setContextClassLoader(defoutlClassLoader);
		}
		myClassLoader = defoutlClassLoader;
	}
	
	@Override
	public IModelStructure getModelStructure() {
		Thread.currentThread().setContextClassLoader(myClassLoader);
		return super.getModelStructure();
	}

	@Override
	public IStatement createStatement(Query query) {
		Thread.currentThread().setContextClassLoader(myClassLoader);
		return super.createStatement(query);
	}

	@Override
	public void open() {
		File jarFile = null;
		
		FileDataSourceConfiguration configuration = getFileDataSourceConfiguration();
		
		jarFile = configuration.getFile();
		if(jarFile == null) return;
		
		myClassLoader = ClassLoaderManager.updateCurrentClassLoader(jarFile);
		
		super.initEntityManagerFactory( getConfiguration().getModelName() );
		
	}


		
	
	
}
