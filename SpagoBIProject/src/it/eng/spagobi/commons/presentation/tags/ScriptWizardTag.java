/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.commons.presentation.tags;

import it.eng.spago.base.Constants;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.tracing.TracerSingleton;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.commons.utilities.urls.IUrlBuilder;
import it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory;
import it.eng.spagobi.utilities.scripting.ScriptUtilities;
import it.eng.spagobi.utilities.themes.ThemesManager;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Presentation tag for Script details. 
 */
public class ScriptWizardTag extends CommonWizardLovTag {

	private HttpServletRequest httpRequest = null;
	protected RequestContainer requestContainer = null;
	protected ResponseContainer responseContainer = null;
	protected IUrlBuilder urlBuilder = null;
	protected IMessageBuilder msgBuilder = null;
	private String script;
	private String languageScript;
	protected String currTheme="";

	String readonly = "readonly" ;
	boolean isreadonly = true ;
	String disabled = "disabled" ;

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {

		httpRequest = (HttpServletRequest) pageContext.getRequest();
		requestContainer = ChannelUtilities.getRequestContainer(httpRequest);
		responseContainer = ChannelUtilities.getResponseContainer(httpRequest);
		urlBuilder = UrlBuilderFactory.getUrlBuilder();
		msgBuilder = MessageBuilderFactory.getMessageBuilder();
		
    	currTheme=ThemesManager.getCurrentTheme(requestContainer);
    	if(currTheme==null)currTheme=ThemesManager.getDefaultTheme();
		
		TracerSingleton.log(SpagoBIConstants.NAME_MODULE, TracerSingleton.DEBUG, 
		"ScriptWizardTag::doStartTag:: invocato");
		RequestContainer aRequestContainer = RequestContainer.getRequestContainer();
		SessionContainer aSessionContainer = aRequestContainer.getSessionContainer();
		SessionContainer permanentSession = aSessionContainer.getPermanentContainer();
		IEngUserProfile userProfile = (IEngUserProfile)permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		boolean isable = false;
		try {
			isable = userProfile.isAbleToExecuteAction(SpagoBIConstants.LOVS_MANAGEMENT);
		} catch (EMFInternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isable){
			isreadonly = false;
			readonly = "";
			disabled = "";
		}
		StringBuffer output = new StringBuffer();

		String lanuageScriptLabel = msgBuilder.getMessage("SBISet.ListDataSet.languageScript", "messages", httpRequest);
		String scriptLabel = msgBuilder.getMessage("SBIDev.scriptWiz.scriptLbl", "messages", httpRequest);


		output.append("<table width='100%' cellspacing='0' border='0'>\n");
		output.append("	<tr>\n");
		output.append("		<td class='titlebar_level_2_text_section' style='vertical-align:middle;'>\n");
		output.append("			&nbsp;&nbsp;&nbsp;"+ msgBuilder.getMessage("SBIDev.scriptWiz.wizardTitle", "messages", httpRequest) +"\n");
		output.append("		</td>\n");
		output.append("		<td class='titlebar_level_2_empty_section'>&nbsp;</td>\n");
		output.append("		<td class='titlebar_level_2_button_section'>\n");
		output.append("			<a style='text-decoration:none;' href='javascript:opencloseScriptWizardInfo()'> \n");
		output.append("				<img width='22px' height='22px'\n");
		output.append("				 	 src='" + urlBuilder.getResourceLinkByTheme(httpRequest, "/img/info22.jpg",currTheme)+"'\n");
		output.append("					 name='info'\n");
		output.append("					 alt='"+msgBuilder.getMessage("SBIDev.scriptWiz.showSintax", "messages", httpRequest)+"'\n");
		output.append("					 title='"+msgBuilder.getMessage("SBIDev.scriptWiz.showSintax", "messages", httpRequest)+"'/>\n");
		output.append("			</a>\n");
		output.append("		</td>\n");
		String urlImgProfAttr = urlBuilder.getResourceLinkByTheme(httpRequest, "/img/profileAttributes22.jpg",currTheme);
		output.append(generateProfAttrTitleSection(urlImgProfAttr));
		output.append("	</tr>\n");
		output.append("</table>\n");

		output.append("<br/>\n");


		output.append("<div class='div_detail_area_forms_lov'>\n");


		//LANGUAGE SCRIPT COMBO		

		output.append("		<div class='div_detail_label_lov'>\n");
		output.append("			<span class='portlet-form-field-label'>\n");
		output.append(lanuageScriptLabel);
		output.append("			</span>\n");
		output.append("		</div>\n");
		output.append("		<div class='div_detail_form'>\n");
		output.append("			<select  style='width:180px;' class='portlet-form-input-field' name='LANGUAGESCRIPT' id='LANGUAGESCRIPT' >\n");


		Map engineNames=ScriptUtilities.getEngineFactoriesNames();
		String selected="";
		for(Iterator it=engineNames.keySet().iterator();it.hasNext();){
			String engName=(String)it.next(); 
			String alias=(String)engineNames.get(engName);
			alias = StringEscapeUtils.escapeHtml(alias);
			selected="";
			if(languageScript.equalsIgnoreCase(alias)){

				selected="selected='selected'";
			}		
			String aliasName=ScriptUtilities.bindAliasEngine(alias);


			/*Map engineNames=ScriptManager.getEngineFactoriesNames();		
				String selected="";
				for (Iterator iterator = factories.iterator(); iterator.hasNext();) {
					ScriptEngineFactory factory = (ScriptEngineFactory) iterator.next();
					String engName = factory.getEngineName();
				    String engVersion = factory.getEngineVersion();
				    String langName = factory.getLanguageName();
				    String langVersion = factory.getLanguageVersion();
				    List<String> engNames = factory.getNames();
				    String alias=engName;

				    if(engNames.size()>=1){
				    	alias=engNames.get(0);
				    }
				    selected="";

					if(langName.equalsIgnoreCase(languageScript)){
						selected="selected='selected'";
						}*/

			output.append("<option value='"+alias+"' label='"+alias+"' "+selected+">");
			output.append(aliasName);	
			output.append("</option>");
		}
		output.append("</select>");
		output.append("</div>");

		// FINE LANGUAGE SCRIPT


		output.append("		<div class='div_detail_label_lov'>\n");
		output.append("			<span class='portlet-form-field-label'>\n");
		output.append(scriptLabel);
		output.append("			</span>\n");
		output.append("		</div>\n");
		output.append("		<div style='height:110px;' class='div_detail_form'>\n");
		output.append("			<textarea style='height:100px;'  "+disabled+" class='portlet-text-area-field' name='SCRIPT' onchange='setLovProviderModified(true);'  cols='50'>" + script + "</textarea>\n");
		output.append("		</div>\n");
		output.append("		<div class='div_detail_label_lov'>\n");
		output.append("			&nbsp;\n");
		output.append("		</div>\n");


		// fine DETAIL AREA FORMS
		output.append("</div>\n");



		output.append("<script>\n");
		output.append("		var infowizardscriptopen = false;\n");
		output.append("		var winSWT = null;\n");
		output.append("		function opencloseScriptWizardInfo() {\n");
		output.append("			if(!infowizardscriptopen){\n");
		output.append("				infowizardscriptopen = true;");
		output.append("				openScriptWizardInfo();\n");
		output.append("			}\n");
		output.append("		}\n");
		output.append("		function openScriptWizardInfo(){\n");
		output.append("			if(winSWT==null) {\n");
		output.append("				winSWT = new Window('winSWTInfo', {className: \"alphacube\", title:\""+msgBuilder.getMessage("SBIDev.scriptWiz.showSintax", "messages", httpRequest)+"\",width:680, height:150, destroyOnClose: false});\n");
		output.append("         	winSWT.setContent('scriptwizardinfodiv', true, false);\n");
		output.append("         	winSWT.showCenter(false);\n");
		output.append("         	winSWT.setLocation(40,50);\n");
		output.append("		    } else {\n");
		output.append("         	winSWT.showCenter(false);\n");
		output.append("		    }\n");
		output.append("		}\n");
		output.append("		observerSWT = { onClose: function(eventName, win) {\n");
		output.append("			if (win == winSWT) {\n");
		output.append("				infowizardscriptopen = false;");
		output.append("			}\n");
		output.append("		  }\n");
		output.append("		}\n");
		output.append("		Windows.addObserver(observerSWT);\n");
		output.append("</script>\n");

		output.append("<div id='scriptwizardinfodiv' style='display:none;'>\n");	
		output.append(msgBuilder.getMessageTextFromResource("it/eng/spagobi/commons/presentation/tags/info/scriptwizardinfo", httpRequest));
		output.append("</div>\n");	

		try {
			pageContext.getOut().print(output.toString());
		}
		catch (Exception ex) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.CRITICAL, "ScriptWizardTag::doStartTag::", ex);
			throw new JspException(ex.getMessage());
		}

		return SKIP_BODY;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
		TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG, "ScriptWizardTag::doEndTag:: invocato");
		return super.doEndTag();
	}


	/**
	 * Gets the script.
	 * 
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * Sets the script.
	 * 
	 * @param script the new script
	 */
	public void setScript(String script) {
		this.script = script;
	}

	public String getLanguageScript() {
		return languageScript;
	}

	public void setLanguageScript(String languageScript) {
		this.languageScript = languageScript;
	}


}
