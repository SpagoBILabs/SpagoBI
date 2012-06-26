/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.importexport.transformers;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.importexport.ITransformer;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class TransformerFrom3_4_0To3_5_0 implements ITransformer {

	static private Logger logger = Logger.getLogger(TransformerFrom3_4_0To3_5_0.class);

	public byte[] transform(byte[] content, String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		try {
			TransformersUtilities.decompressArchive(pathImpTmpFolder, archiveName, content);
		} catch(Exception e) {
			logger.error("Error while unzipping exported archive", e);	
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
			fixParuses(conn);
			fixRoles(conn);
			fixDatasets(conn);
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

	private void fixDatasets(Connection conn) throws Exception {
		logger.debug("IN");
		Statement stmt = conn.createStatement();
		String sql = "";
		try {
			sql = "ALTER TABLE SBI_DATA_SET_HISTORY ADD COLUMN QUERY_SCRIPT VARCHAR DEFAULT NULL;";
			stmt.execute(sql);
			sql = "ALTER TABLE SBI_DATA_SET_HISTORY ADD COLUMN QUERY_SCRIPT_LANGUAGE VARCHAR DEFAULT NULL;";
			stmt.execute(sql);
		} catch (Exception e) {
			logger.error(
					"Error adding column: if add column fails may mean that column already esists; means you are not using an exact version spagobi DB",
					e);
		}
		logger.debug("OUT");
	}
	
	private void fixRoles(Connection conn) throws Exception {
		logger.debug("IN");
		Statement stmt = conn.createStatement();
		String sql = "";
		try {
			sql = "ALTER TABLE SBI_EXT_ROLES ADD COLUMN EDIT_WORKSHEET BOOLEAN DEFAULT TRUE;";
			stmt.execute(sql);
		} catch (Exception e) {
			logger.error(
					"Error adding column: if add column fails may mean that column already esists; means you are not using an exact version spagobi DB",
					e);
		}
		logger.debug("OUT");
	}

	private void fixParuses(Connection conn) throws Exception {
		logger.debug("IN");
		Statement stmt = conn.createStatement();
		String sql = "";
		try {
			sql = "ALTER TABLE SBI_PARUSE ADD COLUMN MAXIMIZER_ENABLED BOOLEAN DEFAULT FALSE;";
			stmt.execute(sql);
			sql = "UPDATE SBI_PARUSE SET MAXIMIZER_ENABLED = FALSE;";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			logger.error(
					"Error adding column: if add column fails may mean that column already esists; means you are not using an exact version spagobi DB",
					e);
		}
		logger.debug("OUT");
	}

}

