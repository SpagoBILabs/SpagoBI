package it.eng.spagobi.engines.datamining.model;

public class Output {
	/**
	 * outputType: possible values plot or video
	 */
	private String outputType;
	/**
	 * outputName: name to be used by plot image (internally)
	 */
	private String outputName;
	/**
	 * outputValue: value of the variable to be displayed in the result
	 */
	private String outputValue;
	/**
	 * outputDataType: data type of the result (internally)
	 */
	private String outputDataType;

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getOutputValue() {
		return outputValue;
	}

	public void setOutputValue(String outputValue) {
		this.outputValue = outputValue;
	}

	public String getOutputDataType() {
		return outputDataType;
	}

	public void setOutputDataType(String outputDataType) {
		this.outputDataType = outputDataType;
	}

}
