/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
