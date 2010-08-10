package it.eng.spagobi.analiticalmodel.document.bo;

import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;

public class ObjMetaDataAndContent {
	
	private ObjMetadata meta = null;
	private ObjMetacontent metacontent = null;
	
	public ObjMetadata getMeta() {
		return meta;
	}
	public void setMeta(ObjMetadata meta) {
		this.meta = meta;
	}
	public ObjMetacontent getMetacontent() {
		return metacontent;
	}
	public void setMetacontent(ObjMetacontent metacontent) {
		this.metacontent = metacontent;
	}

}
