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
package it.eng.qbe.datasource.configuration.dao.fileimpl;

import it.eng.qbe.datasource.configuration.dao.IInLineFunctionsDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implementation of IInLineFunctionsDAO that read functions code (ie. data functions)
 * 
 * @author Antonella Giachino
 */
public class InLineFunctionsDAOFileImpl implements IInLineFunctionsDAO {
	
	public static transient Logger logger = Logger.getLogger(ViewsDAOFileImpl.class);
	
	private HashMap mapInLineFunctions = new HashMap();
	
	
	
	
	public List loadInLineFunctions(String dialect){
		StringBuffer buffer = new StringBuffer();
		
		
		InputStream is = null;
		is = getClass().getClassLoader().getResourceAsStream("functions.xml");
		
		BufferedReader reader = new BufferedReader( new InputStreamReader(is) );
		String line = null;
		try {
			while( (line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return buffer.toString();
		return null;
	}
	
	public void addInLineFunction(InLineFunction func) {
		mapInLineFunctions.put(func.name, func);
	}
	
	public HashMap<String, String> getInLineFunctions() {
		return mapInLineFunctions;
	}
	
	public InLineFunction getInLineFunctionByName(String name) {
		return (InLineFunction)mapInLineFunctions.get(name);
	}
	
	public List<InLineFunction> getInLineFunctionsByDialect(String dialect) {
		List toReturn = new ArrayList();
		for (int i=0; i<mapInLineFunctions.size(); i++){
			InLineFunction func =(InLineFunction) mapInLineFunctions.get(i);
			if (func.dialect.contains(dialect))
				toReturn.add(func);
		}
		return toReturn;
	}
	
	public static class InLineFunction {
		String group;
		String name;
		String desc;
		String code;
		String dialect;
		Integer nParams;
		
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the code
		 */
		public String getCode() {
			return code;
		}
		/**
		 * @param code the code to set
		 */
		public void setCode(String code) {
			this.code = code;
		}
		/**
		 * @return the group
		 */
		public String getGroup() {
			return group;
		}
		/**
		 * @param group the group to set
		 */
		public void setGroup(String group) {
			this.group = group;
		}
		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}
		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}
		/**
		 * @return the nParams
		 */
		public Integer getnParams() {
			return nParams;
		}
		/**
		 * @param nParams the nParams to set
		 */
		public void setnParams(Integer nParams) {
			this.nParams = nParams;
		}
		/**
		 * @return the dialect
		 */
		public String getDialect() {
			return dialect;
		}
		/**
		 * @param dialect the dialect to set
		 */
		public void setDialect(String dialect) {
			this.dialect = dialect;
		}

	}
}
