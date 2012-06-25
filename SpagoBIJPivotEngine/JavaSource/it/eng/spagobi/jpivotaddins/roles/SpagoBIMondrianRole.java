/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.roles;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;

import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.tonbeller.jpivot.tags.MondrianModelFactory;

import mondrian.olap.*;

/**
 * <code>RoleImpl</code> is Mondrian's default implementation for the
 * <code>Role</code> interface.
 *
 * @author jhyde
 * @since Oct 5, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/RoleImpl.java#3 $
 */
public class SpagoBIMondrianRole implements Role {

	  private static Logger logger = Logger.getLogger(SpagoBIMondrianRole.class);
	  
	private IEngUserProfile profile = null;
	// Filtered dimension
	private Set<String> filters=null;
	
	private boolean allDimension=false;
    /**
     * Creates a role with no permissions.
     * @param profile 
     */
    public SpagoBIMondrianRole(String filersStr,IEngUserProfile profile) {
    	this.profile = profile;
    	this.filters=new HashSet<String>();
    	if (filersStr!=null){
	    	try {
	    		InputStream is = new java.io.ByteArrayInputStream(filersStr.getBytes());
	    		org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
	    		org.dom4j.Document document;	
				document = reader.read(is);
				
				org.dom4j.Node attribute = document.selectSingleNode("//DATA-ACCESS/FILTERED-DIMENSIONS");
				String filterAll = attribute.valueOf("@filterAll");
				if (filterAll!=null && filterAll.equalsIgnoreCase("true")){
					allDimension=true;
					logger.debug("ALL dimension will be filtered");
				}
				List dimensions = document.selectNodes("//DATA-ACCESS/FILTERED-DIMENSIONS/DIMENSION");
				if (dimensions!=null){
					Iterator it = dimensions.iterator();
					while (it.hasNext()) {
						org.dom4j.Node aDimension = (org.dom4j.Node) it.next();
						String aDimensionName = aDimension.valueOf("@name");
						filters.add(aDimensionName);
						logger.debug("ADD dimension to filtered SET");
					}
				}
	
	    	
			} catch (DocumentException e) {
				logger.error("DocumentException, reading template");
			}	
    	}
    	
    }
    

    public Access getAccess(Schema schema) {
        return Access.ALL;
    }

    public Access getAccess(Cube cube) {
        return Access.ALL;
    }

    public Access getAccess(Dimension dimension) {
    	// Never invoked
    	String name=dimension.getName();
    	if (allDimension || filters.contains(name)) {
    		logger.debug("Filtering this dimension:"+name);
    		return Access.CUSTOM;
    	}
        return Access.ALL;
    }

    public Access getAccess(Hierarchy hierarchy) {
    	String name=hierarchy.getDimension().getName();
    	if (allDimension || filters.contains(name)) {
    		logger.debug("Filtering this dimension:"+name);
    		return Access.CUSTOM;
    	}
    	return Access.ALL;
    }

    public HierarchyAccess getAccessDetails(Hierarchy hierarchy) {
        return new SpagoBIHierarchyAccess(hierarchy);
    }

    public Access getAccess(Level level) {
    	SpagoBIHierarchyAccess h = new SpagoBIHierarchyAccess(level.getHierarchy());
    	return h.getAccess(level);
    }

    public Access getAccess(Member member) {
    	SpagoBIHierarchyAccess h = new SpagoBIHierarchyAccess(member.getHierarchy());
    	return h.getAccess(member);
    }

    public Access getAccess(NamedSet set) {
        Util.assertPrecondition(set != null, "set != null");
        return Access.ALL;
    }

    public boolean canAccess(OlapElement olapElement) {
        Util.assertPrecondition(olapElement != null, "olapElement != null");
        if (olapElement instanceof Member) {
            return getAccess((Member) olapElement) != Access.NONE;
        } else if (olapElement instanceof Level) {
            return getAccess((Level) olapElement) != Access.NONE;
        } else if (olapElement instanceof NamedSet) {
            return getAccess((NamedSet) olapElement) != Access.NONE;
        } else if (olapElement instanceof Hierarchy) {
            return getAccess((Hierarchy) olapElement) != Access.NONE;
        } else if (olapElement instanceof Cube) {
            return getAccess((Cube) olapElement) != Access.NONE;
        } else if (olapElement instanceof Dimension) {
            return getAccess((Dimension) olapElement) != Access.NONE;
        } else {
            return false;
        }
    }

    public class SpagoBIHierarchyAccess implements HierarchyAccess {

	    private final Map<Member, Access> memberGrants =
	        new HashMap<Member, Access>();
		
		protected Hierarchy hierarchy;
		
		protected Access access = null;
		
		public SpagoBIHierarchyAccess (Hierarchy hierarchy) {
			this.hierarchy = hierarchy;
	    	String name=hierarchy.getDimension().getName();
	    	if (allDimension || filters.contains(name)) {
	    		logger.debug("Filtering this dimension:"+name);
	    		this.access = Access.CUSTOM;
			} else {
				this.access = Access.ALL;
			}
		}
		
	    public Access getAccess(Level level) {
	        if (this.access != Access.CUSTOM) {
	            return this.access;
	        }
	        if (level.getDepth() < getTopLevelDepth()) {
	            // no access
	            return Access.NONE;
	        } else if (level.getDepth() > getBottomLevelDepth()) {
	            // no access
	            return Access.NONE;
	        }
	        return this.access;
	    }
	    
	    public Access getAccess(Member member) {
	        if (this.access != Access.CUSTOM) {
	            return this.access;
	        }
	        return Access.ALL;
	
	    }
	
	    public int getTopLevelDepth() {
	        return 0;
	    }
	
	    public int getBottomLevelDepth() {
	        return hierarchy.getLevels().length - 1;
	    }
	
	    public RollupPolicy getRollupPolicy() {
	    	String name=hierarchy.getDimension().getName();
	    	if (allDimension || filters.contains(name)) {
	    		logger.debug("Filtering this dimension:"+name);
	    		return RollupPolicy.PARTIAL;
	    	}
	    	return RollupPolicy.FULL;
	    }
	
	    public boolean hasInaccessibleDescendants(Member member) {
	    	boolean toReturn = false;
	    	if (member == null) toReturn = false;
	    	else {
		    	if (member.isAll()) {
		    		toReturn = true;
	        	} else {
	        		toReturn = false;
	        	}
	    	}
	    	logger.debug("hasInaccessibleDescendants: returning " + toReturn + " for member " + member);
        	return toReturn;
	    }
	    
	}


}

