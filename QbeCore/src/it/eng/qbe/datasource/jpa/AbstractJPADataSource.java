/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.bo.DatamartJarFile;
import it.eng.qbe.conf.QbeCoreSettings;
import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.datasource.jpa.DBConnection;
import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.model.io.IDataMartModelRetriever;
import it.eng.qbe.utility.IDBSpaceChecker;
import it.eng.qbe.utility.Utils;
import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.utilities.DynamicClassLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;



public abstract class AbstractJPADataSource extends AbstractDataSource implements IJpaDataSource {

	private Map dblinkMap = null;
	private DBConnection connection = null;
	
	private static transient Logger logger = Logger.getLogger(AbstractJPADataSource.class);
	
	
	/**
	 * Gets the datamart jar file.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the datamart jar file
	 */
	protected File getDatamartJarFile(String datamartName){
		//File datamartJarFile = null;
		File datamartFile = null;
		
		try{
			//IDataMartModelRetriever dataMartModelRetriever = QbeCoreSettings.getInstance().getDataMartModelRetriever();
			//datamartJarFile = dataMartModelRetriever.getDatamartJarFile(datamartName);
			DatamartJarFile datamartJarFile = DAOFactory.getDatamartJarFileDAO().loadDatamartJarFile(datamartName);
			System.out.println("JPA datamartJarFile: " + datamartJarFile);
			datamartFile = datamartJarFile.getFile() ;
		}catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		//return datamartJarFile;
		return datamartFile;
	}
	
	/**
	 * Gets the view names.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the view names
	 */
	protected List getViewNames(String datamartName) {
		List viewNames = null;
		IDataMartModelRetriever dataMartModelRetriever;
		try {
			dataMartModelRetriever = QbeCoreSettings.getInstance().getDataMartModelRetriever();
			viewNames = dataMartModelRetriever.getViewNames(datamartName);
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}		
		
		return viewNames;
	}
	
	/**
	 * Gets the view jar file.
	 * 
	 * @param datamartName the datamart name
	 * @param viewName the view name
	 * 
	 * @return the view jar file
	 */
	protected File getViewJarFile(String datamartName, String viewName){
		File viewJarFile = null;
		
		try{
			IDataMartModelRetriever dataMartModelRetriever = QbeCoreSettings.getInstance().getDataMartModelRetriever();
			viewJarFile =  dataMartModelRetriever.getViewJarFile(datamartName, viewName);
		}catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		return viewJarFile;
	}

	
	
	/**
	 * Load formula file.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the file
	 */
	protected File loadFormulaFile(String datamartName) {
		String formulaFile = getDatamartJarFile( datamartName ).getParent() + "/formula.xml";
		return new File(formulaFile);
	}
	
	/**
	 * Update current class loader.
	 * 
	 * @param jarFile the jar file
	 */
	protected static void updateCurrentClassLoader(File jarFile){
		
		boolean wasAlreadyLoaded = false;
		ApplicationContainer container = null;
		
		logger.debug("IN");
		
		try {
			
			logger.debug("jar file to be loaded: " + jarFile.getAbsoluteFile());
			
			container = ApplicationContainer.getInstance();
			if (container != null) {
				ClassLoader cl = (ClassLoader) container.getAttribute("DATAMART_CLASS_LOADER");
				if (cl != null) {
					logger.debug("Found a cached loader of type: " + cl.getClass().getName());
					logger.debug("Set as current loader the one previusly cached");
					Thread.currentThread().setContextClassLoader(cl);
				}
			}
			
			JarFile jar = new JarFile(jarFile);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String entryName = entry.getName();
					String className = entryName.substring(0, entryName.lastIndexOf(".class"));
					className = className.replaceAll("/", ".");
					className = className.replaceAll("\\\\", ".");
					try {
						logger.debug("loading class [" + className  + "]" + " with class loader [" + Thread.currentThread().getContextClassLoader().getClass().getName()+ "]");
						Thread.currentThread().getContextClassLoader().loadClass(className);
						wasAlreadyLoaded = true;
						logger.debug("Class [" + className  + "] has been already loaded (?");
						break;
					} catch (Exception e) {
						wasAlreadyLoaded = false;
						logger.debug("Class [" + className  + "] hasn't be loaded yet (?)");
						break;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		logger.debug("Jar file [" + jarFile.getName()  + "] already loaded: " + wasAlreadyLoaded);
		
		try {
			/*
			 * TEMPORARY: the next instruction forcing the loading of all classes in the path...
			 * (ie. for some qbe that have in common any classes but not all and that at the moment they aren't loaded correctly)
			 */
			wasAlreadyLoaded = false;

			if (!wasAlreadyLoaded) {
				
				ClassLoader previous = Thread.currentThread().getContextClassLoader();
    		    DynamicClassLoader current = new DynamicClassLoader(jarFile, previous);
			    Thread.currentThread().setContextClassLoader(current);

				//ClassLoader current = URLClassLoader.newInstance(new URL[]{jarFile.toURL()}, previous);				
				//Thread.currentThread().setContextClassLoader(current);
				
				if (container != null) container.setAttribute("DATAMART_CLASS_LOADER", current);

			}
			
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
	}


	/**
	 * Gets the dblink map.
	 * 
	 * @return the dblink map
	 */
	public Map getDblinkMap() {
		return dblinkMap;
	}


	/**
	 * Sets the dblink map.
	 * 
	 * @param dblinkMap the new dblink map
	 */
	public void setDblinkMap(Map dblinkMap) {
		this.dblinkMap = dblinkMap;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IJPADataSource#getConnection()
	 */
	public DBConnection getConnection() {
		return connection;
	}


	/**
	 * Sets the connection.
	 * 
	 * @param connection the new connection
	 */
	public void setConnection(DBConnection connection) {
		this.connection = connection;
	}
	
	private boolean checkSpace(Connection aSqlConnection){
		
		if (QbeCoreSettings.getInstance().isSpaceCheckerEnabled()){
			try{
				IDBSpaceChecker spaceChecker = QbeCoreSettings.getInstance().getDbSpaceChecker();
				int freeSpace = spaceChecker.getPercentageOfFreeSpace(aSqlConnection);
				if (freeSpace < QbeCoreSettings.getInstance().getFreeSpaceLbLimit())
					return false;
				else
					return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}else{
			logger.debug(" check of the space disabled...");
			return true;
		}
	}
	
	
	/**
	 * View reverse engineering.
	 * 
	 * @param name the name
	 * @param packageName the package name
	 * @param destDir the dest dir
	 * @param columnNames the column names
	 * @param columnHibernateTypes the column hibernate types
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void viewReverseEngineering(String name, String packageName, File destDir, List columnNames, List columnAliases, List columnHibernateTypes) throws IOException {
		
		String directoryOfPackage = Utils.packageAsDir(packageName);
		String destinationFoder = destDir.toString() + File.separator + directoryOfPackage + File.separator;
		File f = new File(destinationFoder);
		if (!f.exists()){
			f.mkdirs();
		}
		
		BufferedWriter bwHbm = new BufferedWriter(new FileWriter(destinationFoder + Utils.asJavaClassIdentifier(name)+".hbm.xml"));
		BufferedWriter bwJava = new BufferedWriter(new FileWriter(destinationFoder + Utils.asJavaClassIdentifier(name)+"Id.java"));
		BufferedWriter labelProps = new BufferedWriter(new FileWriter(destDir + File.separator + "label.properties"));
		BufferedWriter qbeProps = new BufferedWriter(new FileWriter(destDir + File.separator + "qbe.properties"));
		
		
		bwJava.write("package "+packageName+";\n");
		bwJava.write("import java.util.Date;\n");
		bwJava.write("import java.math.*;\n");
		bwJava.write("import java.lang.*;\n");				
		bwJava.write("public class "+ Utils.asJavaClassIdentifier(name) + "Id implements java.io.Serializable {\n");
		
		bwHbm.write("<?xml version=\"1.0\"?>");
		bwHbm.write("<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n");
		bwHbm.write("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
		bwHbm.write("<hibernate-mapping>\n");
		bwHbm.write("<class name=\""+packageName+"."+Utils.asJavaClassIdentifier(name)+"\" table=\""+name+"\">\n");
		bwHbm.write("  <composite-id name=\"id\" class=\""+packageName+"."+Utils.asJavaClassIdentifier(name)+"Id\">\n");
		
		labelProps.write(packageName + "." + Utils.asJavaClassIdentifier(name) + "//" + Utils.asJavaClassIdentifier(name) + "=" + Utils.asJavaClassIdentifier(name) + "\n\n");
		qbeProps.write(packageName + "." + Utils.asJavaClassIdentifier(name) + "//" + Utils.asJavaClassIdentifier(name) + ".type=view");		
		
		for(int i = 0; i < columnNames.size(); i++) {
			String columnName = (String)columnNames.get(i);
			String columnHibernateType = (String)columnHibernateTypes.get(i);
			
			
			String javaFldName = Utils.asJavaPropertyIdentifier(columnName);
			bwHbm.write("<key-property name=\""+javaFldName+"\" type=\""+ columnHibernateType + "\">\n");
			bwHbm.write("<column name=\""+columnName+"\"/>\n");
            bwHbm.write("</key-property>\n");
            
            bwJava.write("private "+ getJavaTypeForHibType(columnHibernateType) + " " + javaFldName+";\n");		            
            
            bwJava.write("public "+ getJavaTypeForHibType(columnHibernateType) + " get"+Utils.capitalize(javaFldName)+"(){\n");
            bwJava.write("    return this."+javaFldName+";\n");
            bwJava.write("}\n");
            
            bwJava.write("public void  set"+Utils.capitalize(javaFldName)+"("+ getJavaTypeForHibType(columnHibernateType)+" par ){\n");
            bwJava.write("    this."+javaFldName+"=par;\n");
            bwJava.write("}\n");
            
            labelProps.write(packageName + "." + Utils.asJavaClassIdentifier(name) + "/id." + javaFldName + "=" + (String)columnAliases.get(i) + "\n");
		}
		bwJava.write("}\n");
		
		bwHbm.write("  </composite-id>\n");
		bwHbm.write("</class>\n");
		bwHbm.write("</hibernate-mapping>\n");
		
		bwJava.flush();
		bwHbm.flush();
		bwJava.close();
		bwHbm.close();
		labelProps.flush();
		labelProps.close();
		qbeProps.flush();
		qbeProps.close();
		
		
		BufferedWriter bwJavaMain = new BufferedWriter(new FileWriter(destinationFoder+ Utils.asJavaClassIdentifier(name)+".java"));
		bwJavaMain.write("package "+packageName+";\n");
		bwJavaMain.write("import java.util.Date;\n");
		bwJavaMain.write("import java.math.*;\n");
		bwJavaMain.write("import java.lang.*;\n");
		
		bwJavaMain.write("public class "+ Utils.asJavaClassIdentifier(name) + " implements java.io.Serializable {\n");
		bwJavaMain.write("    private "+Utils.asJavaClassIdentifier(name) + "Id id;\n");
		
		bwJavaMain.write("	  public " + Utils.asJavaClassIdentifier(name)+ "("+Utils.asJavaClassIdentifier(name) + "Id id){\n");
		bwJavaMain.write("    	this.id=id;");
		bwJavaMain.write("	  }\n");
		
		
		bwJavaMain.write("	  public "+Utils.asJavaClassIdentifier(name) + "Id getId(){\n");
		bwJavaMain.write("    		return this.id;\n");
		bwJavaMain.write("	   }\n");
		
		bwJavaMain.write("	  public void setId("+Utils.asJavaClassIdentifier(name) + "Id id){\n");
		bwJavaMain.write("    	this.id=id;");
		bwJavaMain.write("	  }\n");
		
		bwJavaMain.write("}\n");
		bwJavaMain.flush();
		bwJavaMain.close();
	}
	
	/**
	 * Gets the java type for hib type.
	 * 
	 * @param hibType the hib type
	 * 
	 * @return the java type for hib type
	 */
	public String getJavaTypeForHibType(String hibType){
		
		if (hibType.equalsIgnoreCase("integer")){
			return "Integer";
		}else if (hibType.equalsIgnoreCase("integer")){
			return "Long";
		}else if (hibType.equalsIgnoreCase("short")){
			return "Short";
		}else if (hibType.equalsIgnoreCase("character") || hibType.equalsIgnoreCase("string")){
			return "String";
		}else if (hibType.equalsIgnoreCase("boolean")){
			return "Boolean";
		}if (  hibType.equalsIgnoreCase("date") 
			|| hibType.equalsIgnoreCase("time") 
			|| hibType.equalsIgnoreCase("timestamp")){
			return "Date";
		}if (hibType.equalsIgnoreCase("big_decimal")){
			return "BigDecimal";
		}else{
			return "String";
		}		
	}
	
	private void compileJavaClasses(File srcDir) {
		Project project = new Project();
		Javac javacTask = new Javac();
		javacTask.setProject(project);
		
		Path path = new Path(project, srcDir.toString());  		
		javacTask.setSrcdir(path);
		javacTask.setSource("1.4");
		javacTask.setDestdir(srcDir);
		
		javacTask.execute();
	}
	
	private void createJar(File sourceDir, File destJarFile) {
		Project project = new Project();
		Jar jarTask = new Jar();
		jarTask.setProject(project);
		
		jarTask.setBasedir(sourceDir);
		jarTask.setDestFile(destJarFile);
		
		jarTask.execute();
	}
}
