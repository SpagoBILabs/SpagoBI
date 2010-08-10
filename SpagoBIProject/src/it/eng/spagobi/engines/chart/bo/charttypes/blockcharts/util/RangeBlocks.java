package it.eng.spagobi.engines.chart.bo.charttypes.blockcharts.util;

import java.awt.Color;

public class RangeBlocks {

	String label;
	String pattern;
	Color color;

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public RangeBlocks(String label, String pattern, Color color) {
		super();
		this.label = label;
		this.pattern = pattern;
		this.color = color;
	}
	

	
	
}
