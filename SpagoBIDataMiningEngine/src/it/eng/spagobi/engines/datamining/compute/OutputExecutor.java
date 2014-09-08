/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spagobi.engines.datamining.DataMiningEngineConfig;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.bo.DataMiningResult;
import it.eng.spagobi.engines.datamining.common.utils.DataMiningConstants;
import it.eng.spagobi.engines.datamining.model.Output;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class OutputExecutor {
	static private Logger logger = Logger.getLogger(OutputExecutor.class);

	private static final String OUTPUT_PLOT_EXTENSION = "jpg";
	private static final String OUTPUT_PLOT_IMG = "jpeg";

	private final String DATAMINING_FILE_PATH = DataMiningEngineConfig.getInstance().getEngineConfig().getResourcePath()
			+ DataMiningConstants.DATA_MINING_PATH_SUFFIX;
	private Rengine re;
	DataMiningEngineInstance dataminingInstance;

	public OutputExecutor(DataMiningEngineInstance dataminingInstance) {
		this.dataminingInstance = dataminingInstance;
	}

	public Rengine getRe() {
		return re;
	}

	public void setRe(Rengine re) {
		this.re = re;
	}

	protected DataMiningResult evalOutput(Output out, ScriptExecutor scriptExecutor) throws IOException {

		// output -->if image and function --> execute function then prepare
		// output
		// output -->if script --> execute script then prepare output

		DataMiningResult res = new DataMiningResult();

		if (out.getOutputType().equalsIgnoreCase(DataMiningConstants.IMAGE_OUTPUT) && out.getOutputName() != null) {
			res.setVariablename(out.getOutputValue());// could be multiple value
														// comma separated
			String plotName = out.getOutputName();
			re.eval(getPlotFilePath(plotName));
			String function = out.getOutputFunction();

			if (function.equals("hist")) {
				re.eval(function + "(" + out.getOutputValue() + ", col=4)");
			} else if (function.equals("plot") || function.equals("biplot")) {
				re.eval(function + "(" + out.getOutputValue() + ", col=3)");
			} else {
				re.eval("plot(" + out.getOutputValue() + ", col=4)");
			}

			re.eval("dev.off()");
			res.setOutputType(out.getOutputType());
			res.setResult(getPlotImageAsBase64(out.getOutputName()));
			res.setPlotName(plotName);
			// scriptExecutor.deleteTemporarySourceScript(DATAMINING_FILE_PATH +
			// DataMiningConstants.DATA_MINING_TEMP_PATH_SUFFIX + plotName + "."
			// + OUTPUT_PLOT_EXTENSION);

		} else if (out.getOutputType().equalsIgnoreCase(DataMiningConstants.TEXT_OUTPUT) && out.getOutputValue() != null && out.getOutputName() != null) {
			res.setVariablename(out.getOutputValue());// could be multiple value
														// comma separated
			REXP rexp = re.eval(out.getOutputValue());
			if (rexp != null) {
				res.setOutputType(out.getOutputType());
				res.setResult(getResultAsString(rexp));
			} else {
				res.setOutputType(out.getOutputType());
				res.setResult("No result");
			}

		} else if (out.getOutputType().equalsIgnoreCase(DataMiningConstants.SCRIPT_OUTPUT) && out.getOutputValue() != null && out.getOutputName() != null) {
			// looks up for the script if needed!
			// ex : type="script" name="d" value="scriptC"
			String scriptName = out.getOutputValue();// in this case contains
														// the script
			String plotName = out.getOutputName();
			re.eval(getPlotFilePath(plotName));

			scriptExecutor.evalScript(scriptName);

			re.eval("dev.off()");
			res.setOutputType(out.getOutputType());
			res.setResult(getPlotImageAsBase64(out.getOutputName()));
			res.setPlotName(plotName);
		}
		return res;
	}

	private String getPlotFilePath(String plotName) {
		String path = null;
		if (plotName != null && !plotName.equals("")) {
			String filePath = DATAMINING_FILE_PATH.replaceAll("\\\\", "/");
			path = OUTPUT_PLOT_IMG + "(\"" + filePath + DataMiningConstants.DATA_MINING_TEMP_FOR_SCRIPT + plotName + "." + OUTPUT_PLOT_EXTENSION + "\") ";
		}
		return path;
	}

	public String getPlotImageAsBase64(String plotName) throws IOException {
		String fileImg = DATAMINING_FILE_PATH + DataMiningConstants.DATA_MINING_TEMP_PATH_SUFFIX + plotName + "." + OUTPUT_PLOT_EXTENSION;
		BufferedImage img = null;
		String imgstr = null;
		img = ImageIO.read(new File(fileImg));
		imgstr = encodeToString(img, OUTPUT_PLOT_EXTENSION);
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

	private String getResultAsString(REXP rexp) {
		String result = "";

		int rexpType = rexp.getType();

		if (rexpType == REXP.XT_ARRAY_INT) {
			int[] intArr = rexp.asIntArray();
			result = Arrays.toString(intArr);
		} else if (rexpType == REXP.XT_ARRAY_DOUBLE) {
			double[] doubleArr = rexp.asDoubleArray();
			result = Arrays.toString(doubleArr);
		} else if (rexpType == REXP.XT_ARRAY_STR || (rexpType == REXP.XT_ARRAY_BOOL)) {
			String[] strArr = rexp.asStringArray();
			result = Arrays.toString(strArr);
		} else if (rexpType == REXP.XT_INT) {
			result = rexp.asInt() + "";
		} else if (rexpType == REXP.XT_BOOL) {
			result = rexp.asBool().toString();
		} else if (rexpType == REXP.XT_DOUBLE) {
			result = rexp.asDouble() + "";
		} else if (rexpType == REXP.XT_LIST) {
			result = rexp.asList().getBody().asString();
		} else if (rexpType == REXP.XT_STR) {
			result = rexp.asString();
		} else if (rexpType == REXP.XT_VECTOR) {
			result = rexp.asVector().toString();
		} else if (rexpType == REXP.XT_ARRAY_BOOL_INT) {
			int[] doubleArr = rexp.asIntArray();
			result = Arrays.toString(doubleArr);
		}

		return result;

	}
}
