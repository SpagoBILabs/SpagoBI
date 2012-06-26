/* Copyright 2012 Engineering Ingegneria Informatica S.p.A. – SpagoBI Competency Center
 * The original code of this file is part of Spago java framework, Copyright 2004-2007.

 * This Source Code Form is subject to the term of the Mozilla Public Licence, v. 2.0. If a copy of the MPL was not distributed with this file, 
 * you can obtain one at http://Mozilla.org/MPL/2.0/.

 * Alternatively, the contents of this file may be used under the terms of the LGPL License (the “GNU Lesser General Public License”), in which 
 * case the  provisions of LGPL are applicable instead of those above. If you wish to  allow use of your version of this file only under the 
 * terms of the LGPL  License and not to allow others to use your version of this file under  the MPL, indicate your decision by deleting the 
 * provisions above and  replace them with the notice and other provisions required by the LGPL.  If you do not delete the provisions above, 
 * a recipient may use your version  of this file under either the MPL or the GNU Lesser General Public License. 

 * Spago is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or any later version. Spago is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License 
 * for more details.
 * You should have received a copy of the GNU Lesser General Public License along with Spago. If not, see: http://www.gnu.org/licenses/. The complete text of 
 * Spago license is included in the  COPYING.LESSER file of Spago java framework.
 */
package it.eng.spagobi.engines.geo;


import it.eng.spagobi.utilities.engines.EngineVersion;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class GeoEngineVersion extends EngineVersion {
	
	public static final String ENGINE_NAME = "SpagoBIGeoEngine";
    public static final String AUTHOR = "Engineering Ingegneria Informatica S.p.a.";
    public static final String WEB = "http://spagobi.eng.it/";
    
    public static final String MAJOR = "2";
    public static final String MINOR = "0";
    public static final String REVISION = "0";
    public static final String CODENAME = "Stable";
    
   
    
	
	private static GeoEngineVersion instance;
	
	public static GeoEngineVersion getInstance() {
		if(instance == null) {
			instance = new GeoEngineVersion(MAJOR, MINOR, REVISION, CODENAME);
		}
		
		return instance;
	}
	
	private GeoEngineVersion(String major, String minor, String revision, String codename) {
		super(major, minor, revision, codename);
	}
	
	
	public String getFullName() {
		return ENGINE_NAME + "-" + this.toString();
	}
	    
	
	public String getInfo() {
		return getFullName() + " [ " + WEB +" ]";
	}
}
