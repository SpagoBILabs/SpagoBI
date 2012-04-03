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
package it.eng.spagobi.tools.importexport.transformers;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.importexport.ITransformer;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class TransformerFrom3_3_0To3_4_0 implements ITransformer {

	static private Logger logger = Logger.getLogger(TransformerFrom3_3_0To3_4_0.class);

	public byte[] transform(byte[] content, String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		try {
			TransformersUtilities.decompressArchive(pathImpTmpFolder, archiveName, content);
		} catch(Exception e) {
			logger.error("Error while unzipping 3.1.0 exported archive", e);	
		}
		archiveName = archiveName.substring(0, archiveName.lastIndexOf('.'));
		changeDatabase(pathImpTmpFolder, archiveName);
		// compress archive
		try {
			content = TransformersUtilities.createExportArchive(pathImpTmpFolder, archiveName);
		} catch (Exception e) {
			logger.error("Error while creating creating the export archive", e);	
		}
		// delete tmp dir content
		File tmpDir = new File(pathImpTmpFolder);
		GeneralUtilities.deleteContentDir(tmpDir);
		logger.debug("OUT");
		return content;
	}

	private void changeDatabase(String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		Connection conn = null;
		try {
			conn = TransformersUtilities.getConnectionToDatabase(pathImpTmpFolder, archiveName);
			fixExtRoles(conn);
			fixDataset(conn);
			conn.commit();
		} catch (Exception e) {
			logger.error("Error while changing database", e);	
		} finally {
			logger.debug("OUT");
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("Error closing connection to export database", e);
			}
		}
	}

	/*
	 * Adjust ExtRoles Table
	 * 
	 * @param conn The jdbc connection to export database
	 * @throws Exception
	 */
	private void fixExtRoles(Connection conn) throws Exception {
		logger.debug("IN");

		Statement stmt = conn.createStatement();
		String sql =  "";
		try{
			sql =  "ALTER TABLE SBI_EXT_ROLES ADD COLUMN DO_MASSIVE_EXPORT BOOLEAN DEFAULT TRUE;";
			stmt.execute(sql);
		}
		catch (Exception e) {
			logger.error("Error adding column: if add column fails may mean that column already esists; means you ar enot using an exact version spagobi DB",e);	
		}

		logger.debug("OUT");
	}

	/**
	 *  This fix is referring to an update from version 3.1
	 *  Because of a bug in updating the scripts, it is needed that from 3.3 to 3.4 
	 *  the column CUSTOM_DATA is added,
	 *  if the exported version is lesser than 3.2 this fix will produce an error that is catched and traced, but the import goes on 
	 * @param conn
	 * @throws Exception
	 */
	private void fixDataset(Connection conn) throws Exception {
		logger.debug("IN");

		try{
			Statement stmt = conn.createStatement();

			String sql = "ALTER TABLE SBI_DATA_SET_HISTORY ADD COLUMN CUSTOM_DATA VARCHAR DEFAULT NULL;";

			stmt.executeUpdate(sql);
		}
		catch (Exception e) {
			logger.warn("Could not add the table CUSTOM_DATA: this is just a bugfix from 3.3 to 3.4, if your exported version is lesser than 3.1 this is not needed");
		}

		logger.debug("OUT");
	}


}
