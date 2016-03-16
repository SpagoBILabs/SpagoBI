/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.importexport.transformers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.importexport.ITransformer;

public class TransformerFrom5_1_0To5_2_0 implements ITransformer {

	static private Logger logger = Logger.getLogger(TransformerFrom5_1_0To5_2_0.class);

	@Override
	public byte[] transform(byte[] content, String pathImpTmpFolder, String archiveName) {
		logger.debug("IN");
		try {
			TransformersUtilities.decompressArchive(pathImpTmpFolder, archiveName, content);
		} catch (Exception e) {
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

			logger.debug("change POSITION keyword modifying archive");
			changeKeyWord(pathImpTmpFolder, archiveName);

			conn = TransformersUtilities.getConnectionToDatabase(pathImpTmpFolder, archiveName);
			fixSbiParuse(conn);
			fixSbiAlarm(conn);

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

	private void changeKeyWord(String path, String archiveName) {
		logger.debug("IN");
		FileWriter fw = null;
		try {
			String pathScript = path + "/" + archiveName;
			pathScript = pathScript.replaceAll("\\\\/", "/");
			pathScript += "/metadata";
			String pathFileScript = pathScript + "/metadata.script";

			logger.debug("take file at " + pathFileScript);
			File scriptFile = new File(pathFileScript);

			if (scriptFile.exists() == true) {
				logger.debug("file exists");
				InputStream targetStream = new FileInputStream(scriptFile);
				String content = StringUtilities.convertStreamToString(targetStream);
				content = content.replaceAll("POSITION INTEGER DEFAULT NULL", "KPI_POSITION INTEGER DEFAULT NULL");

				fw = new FileWriter(scriptFile, false);
				fw.write(content);

				logger.debug("wrote the content on metadata.script");

			} else {
				logger.debug("File does not exist");
			}

		} catch (Exception e) {
			logger.error("Error in alterating the archive file", e);
		} finally {
			if (fw != null) {
				try {
					fw.flush();
					fw.close();
				} catch (IOException e) {
					logger.error("Error closing stream", e);
				}
			}

		}

		logger.debug("OUT");
	}

	private void fixSbiParuse(Connection conn) throws Exception {
		logger.debug("IN");
		Statement stmt = conn.createStatement();
		String sql = "";
		try {
			sql = "ALTER TABLE SBI_PARUSE ADD COLUMN OPTIONS VARCHAR(4000) DEFAULT NULL;";
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			logger.error("Error in altering SBI_PARUSE", e);
		}

		logger.debug("OUT");

	}

	private void fixSbiAlarm(Connection conn) throws Exception {
		logger.debug("IN");
		Statement stmt = conn.createStatement();
		String sql = "";
		try {
			sql = "ALTER TABLE SBI_ALARM ADD COLUMN MAIL_SUBJ VARCHAR(256) DEFAULT NULL;";
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			logger.error("Error in altering SBI_PARUSE", e);
		}

		logger.debug("OUT");

	}

}
