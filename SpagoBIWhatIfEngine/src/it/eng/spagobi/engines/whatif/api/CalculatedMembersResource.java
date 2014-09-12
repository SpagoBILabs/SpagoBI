package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.calculatedmember.Author;
import it.eng.spagobi.engines.whatif.calculatedmember.CalculatedMemberManager;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;
import it.eng.spagobi.engines.whatif.serializer.SerializationException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Member;

@Path("/1.0/calculatedmembers")
public class CalculatedMembersResource extends AbstractWhatIfEngineService {
	public static transient Logger logger = Logger.getLogger(CalculatedMembersResource.class);

	// private AxisDimensionManager axisBusiness;
	// private CalculatedMemberManager calculatedMemberManager;
	// input parameters
	// public static final String EXPRESSION = "expression";
	/*
	 * private CalculatedMemberManager getCalculatedMemberBusiness() {
	 * WhatIfEngineInstance ei = getWhatIfEngineInstance();
	 * 
	 * if(calculatedMemberManager==null){ calculatedMemberManager = new
	 * CalculatedMemberManager(ei.getPivotModel()); } return
	 * calculatedMemberManager; }
	 */

	// prova di caricamento dati nella view
	@GET
	// @Path("/print")
	// @Produces("text/html; charset=UTF-8")
	public String printName() {
		logger.debug("IN");
		List<Author> authors = new ArrayList<Author>();
		Author a = new Author(1, new String("Autore 1"));
		Author b = new Author(2, new String("Autore 2"));
		authors.add(a);
		authors.add(b);
		logger.debug("OUT");
		String serializedName;
		try {
			serializedName = serialize(authors);
		} catch (SerializationException e) {
			logger.error("Error serializing versions");
			throw new SpagoBIEngineRestServiceRuntimeException(getLocale(), e);
		}
		return serializedName;
	}

	// free marker template example
	/*
	 * @POST
	 * 
	 * @Path("/execute/{expression}")
	 * 
	 * @Produces("text/html; charset=UTF-8") public String
	 * execute(@javax.ws.rs.core.Context HttpServletRequest
	 * req,@PathParam("expression") String exp){ WhatIfEngineInstance ei =
	 * getWhatIfEngineInstance(); PivotModel model = ei.getPivotModel(); String
	 * query =
	 * "WITH MEMBER [Measures].[Calc Cost] AS '[Measures].[Store Cost] * ${ratio}' "
	 * +
	 * "SELECT {[Measures].[Calc Cost], [Measures].[Store Sales]} ON COLUMNS, "
	 * + "{([Product].[All Products])} ON ROWS FROM [Sales_V]";
	 * 
	 * String query2 =
	 * "SELECT {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON COLUMNS, "
	 * + "{(" +
	 * "$[cube.dimensions.get(\"Product\").defaultHierarchy.defaultMember.uniqueName]"
	 * + ")} ON ROWS FROM [Sales_V]"; model.setMdx(query2); model.initialize();
	 * //model.getExpressionContext().put("ratio", "2.5"); CellSet cellSet =
	 * model.getCellSet(); String table = renderModel(model);
	 * logger.debug("OUT"); return table;
	 * 
	 * }
	 */

	@POST
	@Path("/execute/{calculateFieldName}/{calculateFieldFormula}/{parentMemberUniqueName}/{axisOrdinal}")
	@Produces("text/html; charset=UTF-8")
	public String execute(@javax.ws.rs.core.Context HttpServletRequest req,
			@PathParam("calculateFieldName") String calculateFieldName,
			@PathParam("calculateFieldFormula") String calculateFieldFormula,
			@PathParam("parentMemberUniqueName") String parentMemberUniqueName,
			@PathParam("axisOrdinal") int axisOrdinal) {
		logger.debug("IN");

		Member parentMember;

		logger.debug("expression= " + calculateFieldFormula);
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		CalculatedMemberManager cm = new CalculatedMemberManager(ei);
		Axis axis;

		try {
			parentMember = CubeUtilities.getMember(ei.getPivotModel().getCube(), parentMemberUniqueName);
			axis = CubeUtilities.getAxis(axisOrdinal);
		} catch (OlapException e) {
			logger.error("Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
			throw new SpagoBIEngineRestServiceRuntimeException("sbi.olap.celculated.definition.error", getLocale(),
					"Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
		}

		try {
			cm.injectCalculatedIntoMdxQuery(calculateFieldName, calculateFieldFormula, parentMember, axis);
		} catch (SpagoBIEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String table = renderModel(ei.getPivotModel());
		logger.debug("OUT");
		return table;
	}
}

// public String executeOneVersion(@javax.ws.rs.core.Context HttpServletRequest
// req,@PathParam("expression") String exp){
// commenti vari
// CallNode cn_A = new CallNode(null, "()",Syntax.Parentheses, nodoA);
// CallNode cn_B = new CallNode(null, "()",Syntax.Parentheses, nodoB);
// List<IdentifierNode>
// nodi=selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).getDimensionProperties();
// nodi.add(nodoCalcolato);
// selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).setExpression();
// CallNode prova=new CallNode(null,"Members",Syntax.Property,nodoCalcolato);
// List<IdentifierSegment>
// list=IdentifierNode.ofNames("Measures").getSegmentList();
// IdentifierNode n=IdentifierNode.ofNames("Measures");
// selectNode.getAxisList().add(new AxisNode(null,false,Axis.COLUMNS, new
// ArrayList<IdentifierNode>(), nodoCalcolato));

// Query queryModel = new Query("", model.getCube());
// QueryDimension productDim = queryModel.getDimension("Measures");

// queryModel.getAxis(Axis.COLUMNS).addDimension(index, dimension);
// ParseTreeNode bup=selectNode.getAxisList().get(0).getExpression().deepCopy();
// ParseTreeNode tree = new
// CallNode(null,"()",Syntax.Parentheses,bup,nodoCalcolato);

// org.olap4j.query.QueryDimension measDim = zQuery.getDimension("Measures");
// measDim.createSelection(nodoCalcolato.getSegmentList());
// selectNode.getAxisList().add(zQuery.getSelect().getAxisList().get(0));
// AxisNode nodiColonne

// ParseTreeNode
// column=selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).getExpression();
// ParseTreeNode tree = new
// CallNode(null,"()",Syntax.Parentheses,nodoCalcolato);
// selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).setExpression(tree);

/*
 * 9: .get("Sales") 10: .getDimensions() 11: .get("Product"); 12: selectNode
 */
/*
 * selectNode.getAxisList().get(Axis.COLUMNS.axisOrdinal()).setExpression( new
 * CallNode(null,"{}",Syntax.Braces,n,nodoCalcolato));
 */
// new IdentifierNode(IdentifierNode.ofNames("Measures").getSegmentList()),
// new IdentifierNode(IdentifierNode.ofNames("Unit Sales").getSegmentList()))));
// selectNode.getAxisList().add(new AxisNode(region, nonEmpty, axis,
// dimensionProperties, expression));
// model.getCellSet().
// PlaceMembersOnAxes pm = model.getTransform(PlaceMembersOnAxes.class);
// List<Member> membersList=pm.findVisibleMembers(CubeUtilities.getAxis(0));
// List<Measure> misure=model.getCube().getMeasures();

/*
 * logger.debug("IN"); logger.debug("expression= "+exp); WhatIfEngineInstance ei
 * = getWhatIfEngineInstance(); PivotModel model = ei.getPivotModel();
 * org.olap4j.metadata.Cube cubo= model.getCube(); String currentMdx =
 * model.getMdx(); MdxParser p = createParser(); SelectNode selectNode =
 * p.parseSelect(currentMdx);
 */

// SelectNode selectNode = new SelectNode();
// String nuovoA =
// "WITH MEMBER [Measures].["+exp+"] AS '[Measures].[Store Sales] * [Measures].[Store Sales]' ";

/*
 * IdentifierNode nodoA = new IdentifierNode(new NameSegment("Measures"), new
 * NameSegment("Sales Count")); IdentifierNode nodoB = new IdentifierNode(new
 * NameSegment("Measures"), new NameSegment("Sales Count")); IdentifierNode
 * nodoCalcolato = new IdentifierNode(new NameSegment("Measures"), new
 * NameSegment(exp));
 */

// IdentifierNode startDate =new IdentifierNode(new NameSegment("Measures"),new
// NameSegment("Date"), new NameSegment("2010-01-03"));
// IdentifierNode endDate = new IdentifierNode(new NameSegment("Measures"),new
// NameSegment("Date"), new NameSegment("2010-10-03"));
// IdentifierNode name = new IdentifierNode( new NameSegment("Measures"),new
// NameSegment("Date Range"));
// CallNode cn = new CallNode(null, ":", Syntax.Infix, startDate, endDate);

/* CallNode cn = new CallNode(null, "+", Syntax.Infix, nodoA, nodoB); */

// ParseTreeNode expression = new CallNode( null, "Aggregate", Syntax.Function,
// new CallNode(null,"{}",Syntax.Braces,cn));
/*
 * ParseTreeNode expression = new CallNode( null, "", Syntax.Function, new
 * CallNode(null,"",Syntax.Parentheses,cn)); WithMemberNode withMemberNode = new
 * WithMemberNode(null, nodoCalcolato, expression,
 * Collections.<PropertyValueNode>emptyList());
 */
// WithMemberNode withMemberNode = new WithMemberNode(null, name, expression,
// Collections.<PropertyValueNode>emptyList());
// selectNode.setFrom(IdentifierNode.parseIdentifier(currentMdx.substring(77,88)));
// selectNode.setFrom(IdentifierNode.parseIdentifier("Sales"));
/*
 * selectNode.getWithList().add(withMemberNode); List<AxisNode>
 * list=selectNode.getAxisList();
 */

/*
 * String queryString = selectNode.toString(); model.setMdx(queryString);
 * model.refresh();
 */
// ModelUtilities u= new ModelUtilities();
// u.reloadModel(ei, model);
// List<Measure> misure=cubo.getMeasures();

/*
 * String table = renderModel(model); logger.debug("OUT"); return table; }
 */

/*
 * public String executeZeroVersion(@javax.ws.rs.core.Context HttpServletRequest
 * req,@PathParam("expression") String exp){
 * 
 * logger.debug("IN"); logger.debug("expression= "+exp); WhatIfEngineInstance ei
 * = getWhatIfEngineInstance(); PivotModel model = ei.getPivotModel();
 * SpagoBIPivotModel modelWrapper = (SpagoBIPivotModel) model; ModelConfig
 * config = ei.getModelConfig();
 * 
 * 
 * Map m = this.getServletRequest().getParameterMap(); String currentMdx =
 * model.getMdx();
 */

/*
 * try { JSONObject json =
 * RestUtilities.readBodyAsJSONObject(getServletRequest()); expression =
 * json.getString( EXPRESSION ); } catch (Exception e) { throw new
 * SpagoBIEngineRestServiceRuntimeException("generic.error", this.getLocale(),
 * e); }
 */
/*
 * logger.debug("expression = [" + exp + "]"); String nuovoA =
 * "WITH MEMBER [Measures].["
 * +exp+"] AS '[Measures].[Store Sales] * [Measures].[Store Sales]' "; //String
 * withMemberSentence
 * ="WITH MEMBER [Measures].[Nuovo Campo mk] AS '${s:ratio} * ${s:ratio}' ";
 * //ExpressionContext ec=model.getExpressionContext(); //ec.put("ratio", "5");
 * String nuovoB="SELECT {[Measures].["+exp+"],"; String
 * vecchio=currentMdx.substring(8); model.setMdx(nuovoA + nuovoB+ vecchio);
 */
/*
 * String queryNew = "SELECT" + " {[Measures].[Store Sales]} ON COLUMNS, " +
 * "{[Product].[Food]} ON ROWS FROM [Sales_V] " +
 * "WHERE CrossJoin([Version].[1], [Region].[Mexico Central])";
 * 
 * 
 * String query =
 * "WITH MEMBER [Measures].[Calc Cost] AS '[Measures].[Store Cost] * [Measures].[Store Cost]' "
 * + "SELECT {[Measures].[Calc Cost], [Measures].[Store Sales]} ON COLUMNS, " +
 * "{([Promotion Media].[All Media], [Product].[All Products])} ON ROWS FROM [Sales]"
 * ; model.setMdx(query);
 */

/* CellSet cset=model.getCellSet(); */

// model.initialize();

/*
 * model.refresh();
 * 
 * PlaceMembersOnAxes pm = model.getTransform(PlaceMembersOnAxes.class);
 * List<Member> visibleMembers =
 * pm.findVisibleMembers(CubeUtilities.getAxis(0)); org.olap4j.metadata.Cube
 * cubo= model.getCube();
 */
// List<Measure> misure=cubo.getMeasures();

/*
 * List<Member> members = getMembersFromBody(); List<SbiMember> sbiMembers =
 * null;
 * 
 * 
 * if(members.size()>0){
 * getAxisBusiness().updateAxisHierarchyMembers(members.get(0).getHierarchy(),
 * members); }
 */
/*
 * String table = renderModel(model); logger.debug("OUT");
 */

/*
 * logger.debug("Getting the members from the request"); List<SbiMember>
 * sbiMembers = null; List<Member> members = new ArrayList<Member>(); String
 * membersString=null;
 * 
 * try { membersString = RestUtilities.readBody(getServletRequest());
 * TypeReference<List<SbiMember>> type = new TypeReference<List<SbiMember>>()
 * {}; sbiMembers = (List<SbiMember>)deserialize(membersString, type); for (int
 * i = 0; i < sbiMembers.size(); i++) {
 * members.add(sbiMembers.get(i).getMember(getPivotModel().getCube())); } }
 * catch (Exception e) {
 * logger.error("Error loading the members from the request ", e); throw new
 * SpagoBIEngineRestServiceRuntimeException
 * ("generic.error.request.members.getting", getLocale(), e); }
 */

/*
 * return table; }
 */

