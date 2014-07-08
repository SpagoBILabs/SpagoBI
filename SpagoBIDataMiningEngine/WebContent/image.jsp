<%--
/**
 *
 *	LICENSE: see COPYING file
 *
**/
--%>
<%@ page import="java.io.*,
				 java.util.*,
				 javax.servlet.ServletOutputStream" %>

<%
    String mapName = request.getParameter("mapname");
	//Map imagesMap = (Map)session.getAttribute("IMAGES_MAP");
	Map imagesMap = (Map)session.getAttribute(mapName);
	
	if(imagesMap != null){
		String imageName = request.getParameter("image");
		if (imageName != null) {
			byte[] imageData = (byte[])imagesMap.get(imageName);
			imagesMap.remove(imageName);
			if(imagesMap.isEmpty()){
				session.removeAttribute(mapName);
			}
			response.setContentLength(imageData.length);
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(imageData, 0, imageData.length);
			ouputStream.flush();
			ouputStream.close();
		}
	}
%>
