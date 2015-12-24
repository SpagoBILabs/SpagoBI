<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  <%@ page language="java"
   extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPage"
   contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
   session="true"
   errorPage="/jsp/errors/error.jsp"
%>




<HTML>
 <HEAD>
  <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <TITLE>Service Error</TITLE>
 </HEAD>
 <body>

  <table cellspacing="20px">
    <tr>
     
      <td width="80%" valign="top">
      
      <H1>Error</H1>
      <hr>
      <H2>Error Highcharts Engine not available</H2>
      <hr>
      <br/>
      <b>Description:</b> Highcharts Engine not contained in this distribution. If you want to use Highcharts in this distribution please contact <a href="http://www.spagobi.org/homepage/contact-us/" target="_blank">SpagoBI Labs</a>
      
      <br/>
      
      </td>
    </tr>
  </table>
  
 </body>
</HTML>