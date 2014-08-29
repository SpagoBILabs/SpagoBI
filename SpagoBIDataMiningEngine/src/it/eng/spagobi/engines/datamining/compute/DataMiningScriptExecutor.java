/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spagobi.engines.datamining.DataMiningEngineConfig;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.bo.DataMiningResult;
import it.eng.spagobi.engines.datamining.model.FileDataset;
import it.eng.spagobi.engines.datamining.model.Output;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class DataMiningScriptExecutor {
	static private Logger logger = Logger.getLogger(DataMiningScriptExecutor.class);
	private static String PLOT_OUTPUT = "plot";
	private static String VIDEO_OUTPUT = "video";

	private static final String OUTPUT_PLOT_EXTENSION = "jpg";
	private static final String OUTPUT_PLOT_IMG = "jpeg";

	private final String DATAMINING_FILE_PATH = DataMiningEngineConfig.getInstance().getEngineConfig().getResourcePath() + "\\datamining\\";

	private Rengine re;

	/**
	 * @param dataminingInstance
	 * @return
	 */
	public List<DataMiningResult> executeScript(DataMiningEngineInstance dataminingInstance) {
		List<DataMiningResult> results = new ArrayList<DataMiningResult>();
		// new R-engine
		re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(new String[] { "--vanilla" }, false, null);
		}

		if (!re.waitForR()) {
			logger.error("Cannot load R");
			return null;
		}

		// datasets preparation
		evalDatasets(dataminingInstance);

		// prepares output
		// evalOutput(dataminingInstance);

		// evaluates script code
		results = evalScript(dataminingInstance);

		// re.end();//has some problems
		return results;
	}

	private List<DataMiningResult> evalScript(DataMiningEngineInstance dataminingInstance) {
		List<DataMiningResult> results = new ArrayList<DataMiningResult>();
		String scriptToExecute = dataminingInstance.getScript();
		String ret = createTemporarySourceScript(scriptToExecute);
		re.eval("source(\"" + ret + "\")");
		if (dataminingInstance.getOutputs() != null && !dataminingInstance.getOutputs().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getOutputs().iterator(); dsIt.hasNext();) {
				Output out = (Output) dsIt.next();
				DataMiningResult res = new DataMiningResult();
				res.setVariablename(out.getOutputValue());
				if (out.getOutputType().equalsIgnoreCase(PLOT_OUTPUT) && out.getOutputName() != null && out.getOutputValue() != null) {
					String plotName = out.getOutputName();
					re.eval(getPlotFilePath(plotName));
					re.eval("plot(" + out.getOutputValue() + ", type='l', col=2)");
					re.eval("dev.off()");
					res.setOutputType(out.getOutputType());
					res.setResult(getPlotImageAsBase64(out.getOutputName()));
					res.setPlotName(plotName);
					results.add(res);

					deleteTemporarySourceScript(DATAMINING_FILE_PATH + "temp\\" + plotName + "." + OUTPUT_PLOT_EXTENSION);

				} else if (out.getOutputType().equalsIgnoreCase(VIDEO_OUTPUT) && out.getOutputValue() != null && out.getOutputDataType() != null) {
					REXP rexp = re.eval(out.getOutputValue());
					if (rexp != null) {
						res.setOutputType(out.getOutputType());
						res.setResult(getResultAsString(rexp));
						results.add(res);
					}

				}
			}

		}

		deleteTemporarySourceScript(ret);
		return results;
	}

	private void deleteTemporarySourceScript(String path) {
		boolean success = (new File(path)).delete();
	}

	private String createTemporarySourceScript(String code) {
		String name = RandomStringUtils.randomAlphabetic(10);
		File temporarySource = new File(DATAMINING_FILE_PATH + "temp\\" + name + ".R");
		FileWriter fw = null;
		String ret = "";
		try {
			fw = new FileWriter(temporarySource);
			fw.write(code);
			fw.close();
			ret = temporarySource.getPath();
			ret = ret.replaceAll("\\\\", "/");
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}

		return ret;

	}

	// private void evalOutput(DataMiningEngineInstance dataminingInstance) {
	// // plot(x, type='l', col=2)
	// if (dataminingInstance.getOutputType().equalsIgnoreCase(PLOT_OUTPUT) &&
	// dataminingInstance.getOutputName() != null) {
	// String plotName = dataminingInstance.getOutputName();
	// re.eval(getPlotFilePath(plotName));
	// }
	// }

	private void evalDatasets(DataMiningEngineInstance dataminingInstance) {
		if (dataminingInstance.getDatasets() != null && !dataminingInstance.getDatasets().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getDatasets().iterator(); dsIt.hasNext();) {
				FileDataset ds = (FileDataset) dsIt.next();
				if (ds.getType().equalsIgnoreCase("file")) {
					File fileDSDir = new File(DATAMINING_FILE_PATH + ds.getName());
					// /find file in dir
					File[] dsfiles = fileDSDir.listFiles();
					String fileDSPath = dsfiles[0].getPath();

					fileDSPath = fileDSPath.replaceAll("\\\\", "/");

					String stringToEval = ds.getName() + "<-read." + ds.getReadType() + "(\"" + fileDSPath + "\"," + ds.getOptions() + ");";
					re.eval(stringToEval);
				}
			}
		}
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
		}

		return result;

	}

	private String getPlotFilePath(String plotName) {
		String path = null;
		if (plotName != null && !plotName.equals("")) {
			String filePath = DATAMINING_FILE_PATH.replaceAll("\\\\", "/");
			path = OUTPUT_PLOT_IMG + "(\"" + filePath + "temp/" + plotName + "." + OUTPUT_PLOT_EXTENSION + "\") ";
		}
		return path;
	}

	public String getPlotImageAsBase64(String plotName) {
		String fileImg = DATAMINING_FILE_PATH + "temp\\" + plotName + "." + OUTPUT_PLOT_EXTENSION;
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
