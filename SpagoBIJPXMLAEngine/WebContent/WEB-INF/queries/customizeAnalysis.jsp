<%--
/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page session="true" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.tonbeller.jpivot.olap.model.OlapModel,
    com.tonbeller.jpivot.chart.ChartComponentTag,
    com.tonbeller.jpivot.chart.ChartComponent,
    com.tonbeller.wcf.controller.RequestContext,
    com.tonbeller.wcf.controller.RequestContextFactoryFinder,it.eng.spagobi.jpivotaddins.bean.AnalysisBean,com.tonbeller.jpivot.olap.navi.ClickableExtension,com.tonbeller.jpivot.olap.navi.ClickableExtensionImpl,it.eng.spagobi.jpivotaddins.util.ChartCustomizer,java.util.List,java.util.ArrayList"%>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%
	AnalysisBean analysis = (AnalysisBean) session.getAttribute("analysisBean");
	OlapModel om = (OlapModel) session.getAttribute("query01");
	List clickables = new ArrayList();
	ClickableExtension ext = (ClickableExtension) om.getExtension(ClickableExtension.ID);
	if (ext == null) {
		ext = new ClickableExtensionImpl();
	   	om.addExtension(ext);
	}
	ext.setClickables(clickables);
	RequestContext context = RequestContextFactoryFinder.createContext(request, response, true);
	RequestContext.setInstance(context);
	// create object of enhanced chart tag/
	ChartComponentTag chartTag = new ChartComponentTag();
	// set query to chart tag.
	chartTag.setQuery("query01");
	// create enhanced chart component from chart tag object
	ChartComponent chart = (ChartComponent) chartTag.createComponent(RequestContext.instance());
	chart.initialize(context);
	ChartCustomizer.customizeChart(analysis,chart);
	session.setAttribute("chart01", chart);
	session.removeAttribute("drillthrough");
%>