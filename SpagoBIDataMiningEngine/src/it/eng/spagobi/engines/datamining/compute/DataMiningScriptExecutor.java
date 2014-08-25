/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.model.FileDataset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class DataMiningScriptExecutor {
	static private Logger logger = Logger.getLogger(DataMiningScriptExecutor.class);
	private static String PLOT_OUTPUT = "plot";
	private static String VIDEO_OUTPUT = "video";

	private static final String OUTPUT_SERVER_DIR = "D:/";
	private static final String OUTPUT_PLOT_EXTENSION = "jpg";
	private static final String OUTPUT_PLOT_IMG = "jpeg";

	/**
	 * @param dataminingInstance
	 * @return
	 */
	public String executeScript(DataMiningEngineInstance dataminingInstance) {
		String result = null;
		// new R-engine
		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(new String[] { "--vanilla" }, false, null);
		}

		if (!re.waitForR()) {
			logger.error("Cannot load R");
			return null;
		}

		String scriptToExecute = dataminingInstance.getScript();
		if (dataminingInstance.getOutputType().equalsIgnoreCase(PLOT_OUTPUT) && dataminingInstance.getOutputName() != null) {
			String plotName = dataminingInstance.getOutputName();
			re.eval(getPlotFilePath(plotName));
		}
		if (dataminingInstance.getDatasets() != null && !dataminingInstance.getDatasets().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getDatasets().iterator(); dsIt.hasNext();) {
				FileDataset ds = (FileDataset) dsIt.next();
				re.eval(ds.getName() + "<-read." + ds.getReadType() + "(\"D:/progetti/RIntegration/Integrazione_R_prova_dataset/rats.txt\",header=T);");
			}

		}
		String[] linesOfCode = scriptToExecute.split("\n");
		for (int i = 0; i < linesOfCode.length; i++) {
			REXP rexp = re.eval(linesOfCode[i]);
			if (dataminingInstance.getOutputType().equalsIgnoreCase(VIDEO_OUTPUT) && (i == linesOfCode.length - 1)) {
				result = getResultAsString(rexp);
			}
		}

		// re.end();//has some problems
		return result;
	}

	private String getResultAsString(REXP rexp) {
		String result = null;

		int rexpType = rexp.getType();

		if (rexpType == REXP.XT_ARRAY_INT) {
			result = "";
			int[] intArr = rexp.asIntArray();
			for (int i = 0; i < intArr.length; i++) {
				result += intArr[i] + ",";
			}
		} else if (rexpType == REXP.XT_ARRAY_DOUBLE) {
			result = "";
			double[] doubleArr = rexp.asDoubleArray();
			for (int i = 0; i < doubleArr.length; i++) {
				result += doubleArr[i] + ",";
			}
		} else if (rexpType == REXP.XT_ARRAY_STR || (rexpType == REXP.XT_ARRAY_BOOL)) {
			result = "";
			String[] strArr = rexp.asStringArray();
			for (int i = 0; i < strArr.length; i++) {
				result += strArr[i] + ",";
			}
			// } else if (rexpType == REXP.XT_ARRAY_BOOL) {
			// logger.debug(rexp.asStringArray());
			// result = rexp.asInt() + "";
		} else if (rexpType == REXP.XT_INT) {
			logger.debug(rexp.asInt());
			result = rexp.asInt() + "";
		} else if (rexpType == REXP.XT_BOOL) {
			logger.debug(rexp.asBool());
			result = rexp.asBool().toString();
		} else if (rexpType == REXP.XT_DOUBLE) {
			logger.debug(rexp.asDouble());
			result = rexp.asDouble() + "";
		} else if (rexpType == REXP.XT_LIST) {
			logger.debug(rexp.asList());
			result = rexp.asList().getBody().asString();
		} else if (rexpType == REXP.XT_STR) {
			logger.debug(rexp.asString());
			result = rexp.asString();
		} else if (rexpType == REXP.XT_VECTOR) {
			logger.debug(rexp.asVector());
			result = rexp.asVector().toString();
		}

		return result;

	}

	private String getPlotFilePath(String plotName) {
		String path = null;
		if (plotName != null && !plotName.equals("")) {
			path = OUTPUT_PLOT_IMG + "(\"" + OUTPUT_SERVER_DIR + plotName + "." + OUTPUT_PLOT_EXTENSION + "\") ";
		}
		return path;
	}

	public String getPlotImageAsBase64(String plotName) {
		String fileImg = OUTPUT_SERVER_DIR + plotName + "." + OUTPUT_PLOT_EXTENSION;
		BufferedImage img = null;
		String imgstr = null;
		try {
			img = ImageIO.read(new File(fileImg));
			imgstr = encodeToString(img, OUTPUT_PLOT_EXTENSION);
		} catch (IOException e) {
			logger.error(e);
		}
		return imgstr;

	}

	private static String encodeToString(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();

			Base64 encoder = new Base64();
			imageString = encoder.encodeBase64String(imageBytes);

			bos.close();
		} catch (IOException e) {
			logger.error(e);
		}
		return imageString;
	}

}
