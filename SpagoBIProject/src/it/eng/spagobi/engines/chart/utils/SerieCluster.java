package it.eng.spagobi.engines.chart.utils;

public class SerieCluster {

	String serie;
	
	double[] x;
	double[] y;
	double[] z;
	
	
	public SerieCluster(String serie, double[] x, double[] y, double[] z) {
		super();
		this.serie = serie;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public double[] getX() {
		return x;
	}
	public void setX(double[] x) {
		this.x = x;
	}
	public double[] getY() {
		return y;
	}
	public void setY(double[] y) {
		this.y = y;
	}
	public double[] getZ() {
		return z;
	}
	public void setZ(double[] z) {
		this.z = z;
	}

	
	
}
