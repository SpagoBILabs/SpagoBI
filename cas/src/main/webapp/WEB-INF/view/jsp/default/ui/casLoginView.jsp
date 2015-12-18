<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:directive.include file="includes/top.jsp" />
			<form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
			     <input type="hidden" name="lt" value="${flowExecutionKey}" />
           		 <input type="hidden" name="_eventId" value="submit" />
			    <form:errors path="*" cssClass="errors" id="status" element="div" />
			         <div id="msgMandatory" style='display:none;color:red;font-size:11pt;'>
                     Login and password fields are mandatory!<br />
                     <br />
			          </div> 	          
			          <table valign="middle" align="center">
		        			<tr>
		        				<td width = "100px">
		        				   <img src="./images/wapp/loginUser64.png"/>
		        				</td>
		        				<td>
		        				    <br/> 
		        				    <table>
		        				    	<tr>    		        				    	
  		        				    		<td width="150px">    		        				    		 
  		        								  <label for="username"><spring:message code="screen.welcome.label.netid" /></label>
  		        							 </td>
    		        						 <td width="30px">&nbsp;</td>
    		        						 <td>
    		        							<c:if test="${not empty sessionScope.openIdLocalId}">
		                      						<strong>${sessionScope.openIdLocalId}</strong>
		                      						<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
	                    						</c:if>
	                    						<br/>
	                    						<c:if test="${empty sessionScope.openIdLocalId}">
		                      						<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
		                      						<form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
                    							</c:if>
    		        						</td>	        		        					
		        						</tr>
		        						<tr>
  		        				    	<td width="150px">
  		        							   <label for="password"><spring:message code="screen.welcome.label.password" /></label>
  		        							</td>
  		        							<td width="30px">&nbsp;</td>
  		        							<td>
  		        								<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
          						        		<form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
  		        							</td>	      		        					
		        						</tr>
		        						<tr>
		        							<td width="150px">
  		        							  	<a href="/SpagoBI/ChangePwdServlet?start_url=${service}">
					             						Change Password
					             				</a>
  		        							</td>
		        						</tr>
		        					</table>			        					
		        				</td>  
                     						
		        				<td>
		        					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		        					<input name="submit"  src="./images/wapp/next32.png" value="<spring:message code="screen.welcome.button.login" />" type="image" />
		        				</td>      		        		
		        			</tr>    		        		    		
		        		</table>
		        		<!--
                <div class="row">
                    <label for="username"><spring:message code="screen.welcome.label.netid" /></label>
      						<c:if test="${not empty sessionScope.openIdLocalId}">
          						<strong>${sessionScope.openIdLocalId}</strong>
          						<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
      						</c:if>
      						<br/>
      						<c:if test="${empty sessionScope.openIdLocalId}">
        						<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
        						<form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
      						</c:if>
                </div>
                
                <div class="row">
                    <label for="password"><spring:message code="screen.welcome.label.password" /></label>
                    <BR/>
        						<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
        						<form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
                </div>
                <BR/>

                <div class="row btn-row">
        						<input type="hidden" name="lt" value="${flowExecutionKey}" />
        						<input type="hidden" name="_eventId" value="submit" />

                    <input class="btn-submit" name="submit" accesskey="l" value="<spring:message code="screen.welcome.button.login" />" tabindex="4" type="submit" />
                    <input class="btn-reset" name="reset" accesskey="c" value="<spring:message code="screen.welcome.button.clear" />" tabindex="5" type="reset" />
                </div>
                -->
             
        	</form:form>
<jsp:directive.include file="includes/bottom.jsp" />
