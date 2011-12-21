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
package it.eng.spagobi.analiticalmodel.functionalitytree.init;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * This class initialize the functionalities tree using some configuration
 * parameters. The class is called from Spago Framework during the Application
 * Start-Up.
 * 
 */

public class TreeInitializer implements InitializerIFace {

    static private Logger logger = Logger.getLogger(TreeInitializer.class);

    private SourceBean _config;

    /**
     * Create and initialize all the repositories defined in the configuration
     * SourceBean, the method is called automatically from Spago Framework at
     * application start up if the Spago initializers.xml file is configured
     * 
     * @param config
     *                the config
     */
    public void init(SourceBean config) {
	logger.debug("IN");
	_config = config;

	initialize();
	logger.debug("OUT");
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.eng.spago.init.InitializerIFace#getConfig()
     */
    public SourceBean getConfig() {
	return _config;
    }

    private void initialize() {
	logger.debug("IN");
	try {
	    ILowFunctionalityDAO functionalityDAO = DAOFactory.getLowFunctionalityDAO();
	    List functions = functionalityDAO.loadAllLowFunctionalities(false);
	    if (functions != null && functions.size() > 0) {
		logger.debug("Tree already initialized");

	    } else {
		List nodes = _config.getAttributeAsList("TREE_INITIAL_STRUCTURE.NODE");
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
		    SourceBean node = (SourceBean) it.next();
		    String code = (String) node.getAttribute("code");
		    String name = (String) node.getAttribute("name");
		    String description = (String) node.getAttribute("description");
		    String codeType = (String) node.getAttribute("codeType");
		    String parentPath = (String) node.getAttribute("parentPath");
		    LowFunctionality functionality = new LowFunctionality();
		    functionality.setCode(code);
		    functionality.setName(name);
		    functionality.setDescription(description);
		    functionality.setCodType(codeType);
		    functionality.setPath(parentPath + "/" + code);
		    if (parentPath != null && !parentPath.trim().equals("")) {
			// if it is not the root load the id of the parent path
			LowFunctionality parentFunctionality = functionalityDAO.loadLowFunctionalityByPath(parentPath,
				false);
			functionality.setParentId(parentFunctionality.getId());
		    } else {
			// if it is the root the parent path id is set to null
			functionality.setParentId(null);
		    }
		    // sets no permissions
		    functionality.setDevRoles(new Role[0]);
		    functionality.setExecRoles(new Role[0]);
		    functionality.setTestRoles(new Role[0]);
		    functionality.setCreateRoles(new Role[0]);
		    functionalityDAO.insertLowFunctionality(functionality, null);
		}
	    }
	} catch (EMFUserError e) {
	    logger.error("EMFUserError", e);

	}
	logger.debug("OUT");
    }

}
