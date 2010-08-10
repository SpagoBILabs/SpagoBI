package it.eng.spagobi.engines.documentcomposition.exporterUtils;

import java.io.File;

public class DocumentContainer {

	byte[] content;
	

	String extension;
	String documentType;
	String documentLabel;
	
	MetadataStyle style;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public MetadataStyle getStyle() {
		return style;
	}

	public void setStyle(MetadataStyle style) {
		this.style = style;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentLabel() {
		return documentLabel;
	}

	public void setDocumentLabel(String documentLabel) {
		this.documentLabel = documentLabel;
	}


	
	
	
}
