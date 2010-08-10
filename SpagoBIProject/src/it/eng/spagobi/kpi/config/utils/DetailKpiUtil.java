package it.eng.spagobi.kpi.config.utils;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

public class DetailKpiUtil {

	public static void selectKpi(int id, SourceBean serviceResponse)
			throws EMFUserError, SourceBeanException {
		Kpi toReturn = DAOFactory.getKpiDAO().loadKpiDefinitionById(id);
		serviceResponse.setAttribute("KPI", toReturn);
	}

	public static void updateKpiFromRequest(SourceBean serviceRequest,
			Integer id) throws EMFUserError {
		Kpi kpi = getKpiFromRequest(serviceRequest);
		kpi.setKpiId(id);
		DAOFactory.getKpiDAO().modifyKpi(kpi);
	}

	static private Kpi getKpiFromRequest(SourceBean serviceRequest)
			throws EMFUserError {
		String name = (String) serviceRequest.getAttribute("name");
		String description = (String) serviceRequest
				.getAttribute("description");
		String code = (String) serviceRequest.getAttribute("code");
		String metric = (String) serviceRequest.getAttribute("metric");
		String sWeight = (String) serviceRequest.getAttribute("weight");
		String documentLabel = (String) serviceRequest
				.getAttribute("document_label");
		String sDs_id = (String) serviceRequest.getAttribute("ds_id");

		String sThresold_id = (String) serviceRequest
				.getAttribute("threshold_id");

		String interpretation = (String) serviceRequest
				.getAttribute("interpretation");
		String inputAttribute = (String) serviceRequest
				.getAttribute("inputAttribute");
		String modelReference = (String) serviceRequest
				.getAttribute("modelReference");
		String targetAudience = (String) serviceRequest
				.getAttribute("targetAudience");

		String sKpiTypeId = (String) serviceRequest.getAttribute("kpi_type_id");
		String sMetricScaleId = (String) serviceRequest
				.getAttribute("metric_scale_type_id");
		String sMeasureTypeId = (String) serviceRequest
				.getAttribute("mesure_type_id");

		Integer kpiTypeId = null;
		Integer metricScaleId = null;
		Integer measureTypeId = null;

		if (sKpiTypeId != null && (!sKpiTypeId.equals("-1"))) {
			kpiTypeId = Integer.parseInt(sKpiTypeId);
		}

		if (sMetricScaleId != null && (!sMetricScaleId.equals("-1"))) {
			metricScaleId = Integer.parseInt(sMetricScaleId);
		}

		if (sMeasureTypeId != null && (!sMeasureTypeId.equals("-1"))) {
			measureTypeId = Integer.parseInt(sMeasureTypeId);
		}

		Double weight = null;
		if (sWeight != null && !sWeight.trim().equals(""))
			try {
				weight = new Double(sWeight);
			} catch (NumberFormatException nfe) {
				weight = null;
			}

		Integer ds_id = null;
		if (sDs_id != null && !sDs_id.trim().equals("") && !sDs_id.trim().equals("-1")) {
			ds_id = Integer.parseInt(sDs_id);
		}

		Integer threshold_id = null;
		Threshold threshold = null;
		if (sThresold_id != null && !sThresold_id.trim().equals("")) {
			threshold_id = Integer.parseInt(sThresold_id);
			// try {
			threshold = DAOFactory.getThresholdDAO().loadThresholdById(
					threshold_id);
			// } catch (EMFUserError e) {
			// e.printStackTrace();
			// }
		}

		if (documentLabel != null && documentLabel.trim().equals(""))
			documentLabel = null;

		Kpi toReturn = new Kpi();

		toReturn.setKpiName(name);
		toReturn.setDescription(description);
		toReturn.setCode(code);
		toReturn.setMetric(metric);
		toReturn.setStandardWeight(weight);
		toReturn.setDocumentLabel(documentLabel);
		toReturn.setKpiDsId(ds_id);
		toReturn.setThreshold(threshold);

		toReturn.setInterpretation(interpretation);
		toReturn.setInputAttribute(inputAttribute);
		toReturn.setModelReference(modelReference);
		toReturn.setTargetAudience(targetAudience);

		toReturn.setKpiTypeId(kpiTypeId);
		toReturn.setMeasureTypeId(measureTypeId);
		toReturn.setMetricScaleId(metricScaleId);

		return toReturn;
	}

	public static void newKpi(SourceBean serviceRequest,
			SourceBean serviceResponse) throws EMFUserError,
			SourceBeanException {
		Kpi toCreate = getKpiFromRequest(serviceRequest);

		Integer kpiId = DAOFactory.getKpiDAO().insertKpi(toCreate);

		serviceResponse.setAttribute("ID", kpiId);
		serviceResponse.setAttribute("MESSAGE", SpagoBIConstants.DETAIL_SELECT);
		selectKpi(kpiId, serviceResponse);
	}

	public static void restoreKpiValue(Integer id, SourceBean serviceRequest,
			SourceBean serviceResponse) throws Exception {
		Kpi toReturn = getKpiFromRequest(serviceRequest);
		if (id != null) {
			toReturn.setKpiId(id);
		}
		serviceResponse.setAttribute("KPI", toReturn);
	}

}
