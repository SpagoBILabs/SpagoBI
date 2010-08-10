package it.eng.spagobi.kpi.model.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.functionalitytree.presentation.ITreeHtmlGenerator;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.commons.utilities.urls.IUrlBuilder;
import it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.utilities.themes.ThemesManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ModelStructureTreeHtmlGenerator implements ITreeHtmlGenerator {

	private IUrlBuilder urlBuilder;
	private RequestContainer reqCont;
	private IMessageBuilder msgBuilder;
	private Integer parentId;
	protected String currTheme="";

	public StringBuffer makeAccessibleTree(List objectsList,
			HttpServletRequest httpRequest, String initialPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public StringBuffer makeTree(List objectsList,
			HttpServletRequest httpRequest, String initialPath, String treename) {
		// TODO Auto-generated method stub
		return null;
	}

	public StringBuffer makeTree(List objectsList,
			HttpServletRequest httpRequest, String initialPath) {
		reqCont = ChannelUtilities.getRequestContainer(httpRequest);
		urlBuilder = UrlBuilderFactory.getUrlBuilder();
		msgBuilder = MessageBuilderFactory.getMessageBuilder();
		SessionContainer sessionContainer = reqCont.getSessionContainer();
		SessionContainer permanentSession = sessionContainer
				.getPermanentContainer();

    	currTheme=ThemesManager.getCurrentTheme(reqCont);
    	if(currTheme==null)currTheme=ThemesManager.getDefaultTheme();
		
		StringBuffer htmlStream = new StringBuffer();

		htmlStream.append("<LINK rel='StyleSheet' href='"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/css/dtree.css", currTheme)
				+ "' type='text/css' />");
		makeConfigurationDtree(htmlStream, httpRequest);

		// String nameTree = getTreeName(objectsList);
		htmlStream.append("<SCRIPT language='JavaScript' src='"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/js/dtree.js",currTheme)
				+ "'></SCRIPT>");
		htmlStream.append("<SCRIPT language='JavaScript' src='"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/js/contextMenu.js",currTheme)
				+ "'></SCRIPT>");
		htmlStream.append("<table width='100%'>");
		htmlStream.append("	<tr height='1px'>");
		htmlStream.append("		<td width='10px'>&nbsp;</td>");
		htmlStream.append("		<td>&nbsp;</td>");
		htmlStream.append("	</tr>");
		htmlStream.append("	<tr>");
		htmlStream.append("		<td>&nbsp;</td>");
		htmlStream.append("		<td>");
		htmlStream.append("			<script language=\"JavaScript1.2\">\n");
		htmlStream.append("				var nameTree = 'treeCMS';\n");
		String context=httpRequest.getContextPath();
		if (!(context.charAt(context.length() - 1) == '/')) {
			context += '/';
		}
		
		context+="themes/"+currTheme+"/";
		
		htmlStream.append("				treeCMS = new dTree('treeCMS','"
				+ context + "');\n");

		addItems(htmlStream, objectsList, httpRequest, initialPath);

		htmlStream.append("				document.write(treeCMS);\n");
		makeJSFunctionForMenu(htmlStream, httpRequest);
		htmlStream.append("			</script>\n");
		htmlStream.append("		</td>");
		htmlStream.append("	</tr>");
		htmlStream.append("</table>");
		htmlStream
				.append("<div id='divmenuFunct' class='dtreemenu' onmouseout='hideMenu(event);' >");
		htmlStream.append("		menu");
		htmlStream.append("</div>");
		return htmlStream;
	}

	protected void makeConfigurationDtree(StringBuffer htmlStream,
			HttpServletRequest httpRequest) {
		htmlStream.append("<SCRIPT language=JavaScript>\n");
		htmlStream.append("		function dTree(objName) {\n");
		htmlStream.append("			this.config = {\n");
		htmlStream.append("				target			: null,\n");
		htmlStream.append("				folderLinks		: true,\n");
		htmlStream.append("				useSelection	: false,\n");
		htmlStream.append("				useCookies		: true,\n");
		htmlStream.append("				useLines		: true,\n");
		htmlStream.append("				useIcons		: true,\n");
		htmlStream.append("				useStatusText	: true,\n");
		htmlStream.append("				closeSameLevel	: false,\n");
		htmlStream.append("				inOrder			: false\n");
		htmlStream.append("			}\n");
		htmlStream.append("			this.icon = {\n");
		htmlStream.append("				root		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treebase.gif",currTheme)
				+ "',\n");
		htmlStream.append("				folder		: '"
				+ urlBuilder
						.getResourceLinkByTheme(httpRequest, "/img/treefolder.gif",currTheme)
				+ "',\n");
		htmlStream.append("				folderOpen	: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treefolderopen.gif",currTheme) + "',\n");
		htmlStream.append("				node		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treepage.gif",currTheme)
				+ "',\n");
		htmlStream.append("				empty		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treeempty.gif",currTheme)
				+ "',\n");
		htmlStream.append("				line		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treeline.gif",currTheme)
				+ "',\n");
		htmlStream.append("				join		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treejoin.gif",currTheme)
				+ "',\n");
		htmlStream.append("				joinBottom	: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treejoinbottom.gif",currTheme) + "',\n");
		htmlStream.append("				plus		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treeplus.gif",currTheme)
				+ "',\n");
		htmlStream.append("				plusBottom	: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treeplusbottom.gif",currTheme) + "',\n");
		htmlStream.append("				minus		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest, "/img/treeminus.gif",currTheme)
				+ "',\n");
		htmlStream.append("				minusBottom	: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treeminusbottom.gif",currTheme) + "',\n");
		htmlStream.append("				nlPlus		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treenolines_plus.gif",currTheme) + "',\n");
		htmlStream.append("				nlMinus		: '"
				+ urlBuilder.getResourceLinkByTheme(httpRequest,
						"/img/treenolines_minus.gif",currTheme) + "'\n");
		htmlStream.append("			};\n");
		htmlStream.append("			this.obj = objName;\n");
		htmlStream.append("			this.aNodes = [];\n");
		htmlStream.append("			this.aIndent = [];\n");
		htmlStream.append("			this.root = new Node(-1);\n");
		htmlStream.append("			this.selectedNode = null;\n");
		htmlStream.append("			this.selectedFound = false;\n");
		htmlStream.append("			this.completed = false;\n");
		htmlStream.append("		};\n");
		htmlStream.append("</SCRIPT>\n");
	}

	protected void addItems(StringBuffer htmlStream, List items,
			HttpServletRequest httpRequest, String initialPath) {
		Iterator it = items.iterator();
		while (it.hasNext()) {
			Model aModel = (Model) it.next();
			parentId = aModel.getId();
			recursiveStepAddItems(htmlStream, aModel, httpRequest);
		}
	}

	private void recursiveStepAddItems(StringBuffer htmlStream, Model aModel,
			HttpServletRequest httpRequest) {
		String name = aModel.getName();
		String code = aModel.getCode();
		Integer id = aModel.getId();
		Integer parentId = aModel.getParentId();
		String kpi = null;
		if(aModel.getKpiId()!= null){
			try {
				kpi = DAOFactory.getKpiDAO().loadKpiDefinitionById(aModel.getKpiId()).getCode();
			} catch (EMFUserError e) {
			}
		}
			
		if (parentId == null)
			parentId = -1;
		// parent
		addItemForJSTree(htmlStream, aModel, id, parentId, code, name, true, false,
				kpi, httpRequest);
		List modelChildren = aModel.getChildrenNodes();
		// children
		for (Iterator iterator = modelChildren.iterator(); iterator.hasNext();) {
			Model child = (Model) iterator.next();
			recursiveStepAddItems(htmlStream, child, httpRequest);
		}
	}

	protected void addItemForJSTree(StringBuffer htmlStream, Model item,
			int id, int parentId, String code, String name, boolean isInitialPath,
			boolean isFinalPath, String kpi, HttpServletRequest httpRequest) {
		String skpi = ""; 
		if (kpi != null)
			skpi = "&nbsp;-&nbsp;<span class=\\'kpi\\'>[" + kpi + "]</span>";
		String empty = "";

		String title = name;
		if (name.length() > 30) {
			name = name.substring(0, 29);
			name += "...";
		}

		if(parentId == -1){
			htmlStream.append("	treeCMS.add(" + id + ", " + parentId + ",'" + code + "&nbsp;-&nbsp;" +name
					+ skpi + "', 'javascript:linkEmpty()','" + title
					+ "', '', '', '', '', 'menu(event, \\'"
					+ createAddObjectLink(item, httpRequest) + "\\', \\'"
					+ createDetailObjectLink(item, httpRequest) + "\\', \\'"
					+ empty + "\\', \\'\\')' );\n");
			
		}else {
			htmlStream.append("	treeCMS.add(" + id + ", " + parentId + ",'" + code + "&nbsp;-&nbsp;" + name
					+ skpi + "', 'javascript:linkEmpty()','" + title
					+ "', '', '', '', '', 'menu(event, \\'"
					+ createAddObjectLink(item, httpRequest) + "\\', \\'"
					+ createDetailObjectLink(item, httpRequest) + "\\', \\'"
					+ createDeleteObjectLink(item, httpRequest) + "\\', \\'\\')' );\n");
		}
		

	}

	protected void makeJSFunctionForMenu(StringBuffer htmlStream,
			HttpServletRequest httpRequest) {
		String messageBundle = "component_kpi_messages";
		htmlStream
				.append("		function menu(event, urlAdd, urlDetail, urlErase) {\n");
		htmlStream
				.append("			divM = document.getElementById('divmenuFunct');\n");
		htmlStream.append("			divM.innerHTML = '';\n");
		String capAdd = msgBuilder.getMessage("sbi.modelTree.captionAdd",
				messageBundle, httpRequest);
		htmlStream
				.append("			if(urlAdd!='') divM.innerHTML = divM.innerHTML + '<div onmouseout=\"this.style.backgroundColor=\\'white\\'\"  onmouseover=\"this.style.backgroundColor=\\'#eaf1f9\\'\" ><a class=\"dtreemenulink\" href=\"'+urlAdd+'\">"
						+ capAdd + "</a></div>';\n");
		String capDetail = msgBuilder.getMessage("sbi.modelTree.captionDetail",
				messageBundle, httpRequest);
		htmlStream
				.append("			if(urlDetail!='') divM.innerHTML = divM.innerHTML + '<div onmouseout=\"this.style.backgroundColor=\\'white\\'\"  onmouseover=\"this.style.backgroundColor=\\'#eaf1f9\\'\" ><a class=\"dtreemenulink\" href=\"'+urlDetail+'\">"
						+ capDetail + "</a></div>';\n");
		String capErase = msgBuilder.getMessage("sbi.modelTree.captionDelete",
				messageBundle, httpRequest);
		htmlStream
				.append("         if(urlErase!='') divM.innerHTML = divM.innerHTML + '<div onmouseout=\"this.style.backgroundColor=\\'white\\'\"  onmouseover=\"this.style.backgroundColor=\\'#eaf1f9\\'\" ><a class=\"dtreemenulink\" href=\"javascript:actionConfirm(\\'"
						+ capErase
						+ "\\', \\''+urlErase+'\\');\">"
						+ capErase
						+ "</a></div>';\n");
		htmlStream.append("				showMenu(event, divM);\n");
		htmlStream.append("		}\n");

		htmlStream.append("		function linkEmpty() {\n");
		htmlStream.append("		}\n");

		String confirmCaption = msgBuilder.getMessage(
				"sbi.modelTree.captionConfirm", "component_kpi_messages", httpRequest);
		htmlStream.append("     function actionConfirm(message, url){\n");
		htmlStream.append("         if (confirm('" + confirmCaption
				+ " ' + message + '?')){\n");
		htmlStream.append("             location.href = url;\n");
		htmlStream.append("         }\n");
		htmlStream.append("     }\n");
	}

	protected String createDetailObjectLink(Model aModel, HttpServletRequest httpRequest) {
		HashMap editUrlParMap = new HashMap();
		editUrlParMap.put(SpagoBIConstants.PAGE, "ModelTreePage");
		editUrlParMap.put("MODULE","DetailModelTreeModule");
		editUrlParMap.put("MESSAGE", "DETAIL_SELECT");
		editUrlParMap.put("ID", parentId);
		editUrlParMap.put("MODEL_ID", aModel.getId());
		String editUrl = urlBuilder.getUrl(httpRequest, editUrlParMap);

		return editUrl;
	}

	private Map getItemParams(Model aModel) {
		// TODO Auto-generated method stub
		return null;
	}

	protected String createAddObjectLink(Model aModel, HttpServletRequest httpRequest) {
		HashMap addUrlParMap = new HashMap();
		addUrlParMap.put(SpagoBIConstants.PAGE,"ModelTreePage");
		addUrlParMap.put("MODULE", "DetailModelTreeModule");
		addUrlParMap.put("MESSAGE", "DETAIL_NEW");
		addUrlParMap.put("ID", parentId);
		addUrlParMap.put("MODEL_ID", aModel.getId());
		String addUrl = urlBuilder.getUrl(httpRequest, addUrlParMap);
		return addUrl;
	}

	protected String createDeleteObjectLink(Model aModel, HttpServletRequest httpRequest) {
		HashMap deleteUrlParMap = new HashMap();
		deleteUrlParMap.put(SpagoBIConstants.PAGE, "ModelTreePage");
		deleteUrlParMap.put("MODULE","ListModelTreeModule");
		deleteUrlParMap.put("MESSAGE", "DETAIL_DELETE");
		deleteUrlParMap.put("ID", parentId);
		deleteUrlParMap.put("MODEL_ID", aModel.getId());
		String addUrl = urlBuilder.getUrl(httpRequest, deleteUrlParMap);
		return addUrl;
	}

	private Map getItemParamsDelete(Model aModel) {
		// TODO Auto-generated method stub
		return null;
	}
}
