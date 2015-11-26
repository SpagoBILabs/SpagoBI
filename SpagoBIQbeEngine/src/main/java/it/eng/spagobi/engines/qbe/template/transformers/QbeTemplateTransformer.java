package it.eng.spagobi.engines.qbe.template.transformers;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;

import org.apache.log4j.Logger;

public class QbeTemplateTransformer {

	public static transient Logger logger = Logger.getLogger(QbeTemplateTransformer.class);

	/**
	 * Apply transformation for back compatibility
	 * 
	 * Old parameter syntax P{} New parameters syntax $P{}
	 * 
	 * @param template
	 * @return
	 * @throws SourceBeanException
	 */

	public SourceBean applyTransformations(SourceBean template) throws SourceBeanException {
		logger.debug("IN");
		String xml = template.toXML();
		xml = xml.replaceAll("P\\{", "\\$P\\{");
		// there could be some $$P now
		xml = xml.replaceAll("\\$\\$", "\\$");
		template = SourceBean.fromXMLString(xml);
		logger.debug("OUT");
		return template;
	}

}
