/**
 * 
 * LICENSE: see LICENSE.txt file
 * 
 */
package com.tonbeller.jpivot.table.navi;

import it.eng.spagobi.jpivotaddins.crossnavigation.SpagoBICrossNavigationConfig;

import javax.servlet.http.HttpSession;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.mondrian.MondrianCell;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.mondrian.SpagoBICrossNavigation;
import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.table.CellBuilder;
import com.tonbeller.jpivot.table.CellBuilderDecorator;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.jpivot.table.TableComponentExtensionSupport;
import com.tonbeller.wcf.component.RendererParameters;
import com.tonbeller.wcf.controller.Dispatcher;
import com.tonbeller.wcf.controller.DispatcherSupport;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.table.EmptyTableModel;
import com.tonbeller.wcf.table.TableModel;
import com.tonbeller.wcf.table.TableModelDecorator;
import com.tonbeller.wcf.utils.DomUtils;

/**
 * This class is an extension to JPivot table UI: it must be declared as
 * <code>&lt;extension enabled="false" class="com.tonbeller.jpivot.table.navi.SpagoBICrossNavigationUI""/&gt;</code>
 * in file com.tonbeller.jpivot.table.config.xml to take effect.
 * When cross navigation functionality is enabled, an image on each cell is displayed: this image is obtained adding a 
 * <code>cross-navigation</code> Element in the xml table representation (see also SpagoBIJPivotEngine/WEB-INF/jpivot/table/mdxtable.xsl
 * for the XSLT trasformation of <code>cross-navigation</code> Element). The image has an 'onclick' javascript function that 
 * shows a context menu for the available cross-navigation targets.
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public class SpagoBICrossNavigationUI extends TableComponentExtensionSupport implements ModelChangeListener {

  boolean available;
  boolean renderActions;
  Dispatcher dispatcher = new DispatcherSupport();
  SpagoBICrossNavigation extension;

  TableModelDecorator tableModel = new TableModelDecorator(EmptyTableModel.instance());

  public static final String ID = "crossNavigation";
  public String getId() {
    return ID;
  }

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    super.initialize(context, table);
    table.getOlapModel().addModelChangeListener(this);

    // does the underlying data model support drill?
    if (!initializeExtension()) {
      available = false;
      return;
    }
    available = true;

    // extend the controller
    table.getDispatcher().addRequestListener(null, null, dispatcher);
    
    HttpSession session = context.getSession();
    SpagoBICrossNavigationConfig cninfo = (SpagoBICrossNavigationConfig) session.getAttribute(SpagoBICrossNavigationConfig.ID);
    DomDecorator cr = new DomDecorator(table.getCellBuilder(), cninfo);
    table.setCellBuilder(cr);

  }

  public void startBuild(RequestContext context) {
    super.startBuild(context);
    renderActions = RendererParameters.isRenderActions(context);
    if (renderActions)
      dispatcher.clear();
  }

  class DomDecorator extends CellBuilderDecorator {

	private SpagoBICrossNavigationConfig cninfo = null;
	  
    DomDecorator(CellBuilder delegate, SpagoBICrossNavigationConfig cninfo) {
      super(delegate);
      this.cninfo = cninfo;
    }

    public Element build(Cell cell, boolean even) {
      Element parent = super.build(cell, even);

      if (!enabled || !renderActions || extension == null)
        return parent;

      String id = DomUtils.randomId();
      // add a drill through child node to cell element
      Element cnElem = table.insert("cross-navigation", parent);
      cnElem.setAttribute("id", id);
      cnElem.setAttribute("title", "Cross navigation");

      int choices = cninfo.getChoicesNumber();
      for (int i = 0; i < choices; i++) {
    	  Element targetElement = table.insert("cross-navigation-target", cnElem);
    	  mondrian.olap.Cell mondrianCell = ((MondrianCell) cell.getRootDecoree()).getMonCell();
    	  MondrianModel mondrianModel = (MondrianModel) extension.getModel();
    	  String[] choice = cninfo.getChoice(i, mondrianCell, mondrianModel);
    	  targetElement.setAttribute("title", choice[0]);
    	  targetElement.setAttribute("crossNavigationJSFunction", choice[1]);
      }
      return parent;
    }
  }

  /*
  class CrossNavigationHandler implements RequestListener {
    Cell cell;
    CrossNavigationHandler(Cell cell) {
      this.cell = cell;
    }
    public void request(RequestContext context) throws Exception {
        HttpSession session = context.getSession();
        SpagoBICrossNavigationConfig cninfo = (SpagoBICrossNavigationConfig) session.getAttribute(SpagoBICrossNavigationConfig.ID);
        final String crossNavigationTableRef = table.getOlapModel().getID() + ".crossnavigationtable";
        ITableComponent tc =
          (ITableComponent) session.getAttribute(crossNavigationTableRef);
        // get a new drill through table model
        TableModel tm = crossNavigation(cninfo);
        tc.setModel(tm);
        tc.setVisible(true);
        TableColumn[] tableColumns = null;
        if (tc instanceof EditableTableComponent) {
          tableColumns =
              ((EditableTableComponent) tc).getTableComp().getTableColumns();
        } else if (tc instanceof com.tonbeller.wcf.table.TableComponent) {
          tableColumns = ((com.tonbeller.wcf.table.TableComponent) tc).getTableColumns();
        }
        if (tableColumns != null) {
          for (int i = 0; i < tableColumns.length; i++) {
            TableColumn tableColumn = tableColumns[i];
            tableColumn.setHidden(false);
          }
        }
    }
	private TableModel crossNavigation(SpagoBICrossNavigationConfig config) {
		return extension.crossNavigation((Cell) cell.getRootDecoree(), config);
	}
  }
  */

  /** @return true if extension is available */
  protected boolean initializeExtension() {
    OlapModel om = table.getOlapModel();
    extension = (SpagoBICrossNavigation) om.getExtension(SpagoBICrossNavigation.ID);
    return extension != null;
  }

  public boolean isAvailable() {
    return available;
  }

  public void modelChanged(ModelChangeEvent e) {
  }

  public void structureChanged(ModelChangeEvent e) {
    initializeExtension();
    dispatcher.clear();
  }

  public TableModel getTableModel() {
    return tableModel;
  }
}
