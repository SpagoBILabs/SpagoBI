package it.eng.spagobi.commons.metadata;

import java.io.Serializable;

public class SbiHibernateModel implements Serializable {

	private SbiCommonInfo commonInfo=new SbiCommonInfo();

	public SbiCommonInfo getCommonInfo() {
		return commonInfo;
	}

	public void setCommonInfo(SbiCommonInfo commonInfo) {
		this.commonInfo = commonInfo;
	}
}
