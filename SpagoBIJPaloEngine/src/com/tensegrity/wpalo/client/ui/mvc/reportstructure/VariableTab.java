/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.reportstructure;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XReport;
import com.tensegrity.palo.gwt.core.client.models.reports.XVariableDescriptor;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.async.Callback;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.widgets.EditorTab;

class VariableMatch extends BaseModelData {
	private static final long serialVersionUID = 6447743651276690846L;
	public VariableMatch() {	    
	  }
	  
	  public VariableMatch(XObject structureVariable, String defVal, String sheetVariable) {
		  setStructureVariable(structureVariable);
		  setDefaultValue(defVal);
		  setSheetVariable(sheetVariable);
	  }
	  
	  public XObject getStructureVariable() {
		  return get("variablefromstructure");
	  }

	  public void setStructureVariable(XObject var) {
		  set("variablefromstructure", var);
	  }
	  
	  public String getDefaultValue() {
		  return get("default");
	  }

	  public void setDefaultValue(String val) {
		  set("default", val);
	  }

	  public String getSheetVariable() {
		  return get("reportvariable");
	  }

	  public void setSheetVariable(String var) {
		  set("reportvariable", var);
	  }	  	  
}

class VariableTab extends EditorTab {
	private LabelField instructions;
	private EditorGrid <VariableMatch> grid;
	private ContentPanel panel;
	private final ListStore <VariableMatch> store = new ListStore<VariableMatch>();
	private final SimpleComboBox<String> combo = new SimpleComboBox<String>();
	private final ReportStructureEditor editor;
	private XUser user;
	
	private final String instructionText =
		"Here, you can assign variables in a report to variables resulting " +
		"from the report structure.";
//		"Hier k&ouml;nnen Sie den im Bericht vorhandenen Variablen " +
//		"die Variablen zuordnen, die sich aus der Berichtsstruktur " +
//		"ergeben.";
	
	private final String chooseHint =
		"First, please select a report.";
//		"W&auml;hlen Sie hierzu zun&auml;chst einen Bericht aus.";
		
	private XReport report;
	
	VariableTab(ReportStructureEditor edi) {
		super("Variables");
		editor = edi;
		editor.markDirty();
		combo.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				editor.markDirty();				
			}
		});
		setText("Variables");
		setIconStyle("icon-variable");
		setWidth(200);
		setClosable(false);
		setLayout(new FitLayout());
		//setScrollMode(Scroll.AUTO);
		add(createPanel());		
	}

	public boolean save(XObject input) {
		if (input instanceof XReport) {
			store.commitChanges();
			List <VariableMatch> matches = store.getModels();
			List <XObject> keys = new ArrayList<XObject>();
			List <String> values = new ArrayList<String>(); 
			for (VariableMatch vm: matches) {
				keys.add(vm.getStructureVariable());
				values.add(vm.getSheetVariable());
			}
			WPaloServiceProvider.getInstance().applyMapping(
					(XReport) input, keys.toArray(new XObject[0]),
					values.toArray(new String[0]), user, 
					new Callback<Boolean>(){
						public void onSuccess(Boolean arg0) {
						}
					}); 
		}
		return true;
	}
	public void saveAs(String name, XObject input) {		
	}

	public void saveReport(XReport report) {
		if (!XConstants.TYPE_FOLDER_ELEMENT_SHEET.equals(report.getReceiverType())) {
			return;
		}
		this.report = report;
		setLabelText(new Callback<XVariableDescriptor>(){
			public void onSuccess(XVariableDescriptor arg0) {
				save(VariableTab.this.report);
			}
		});		
	}
	
	public void set(XObject input) {
		if (input instanceof XUser) {
			user = (XUser) input;
		}
		if (input instanceof XReport) {
			report = (XReport) input;
			if (!XConstants.TYPE_FOLDER_ELEMENT_SHEET.equals(report.getReceiverType())) {
				report = null;
			}
		} else {
			report = null;
		}
		setLabelText(null);
	}
	
	private final void setLabelText(final Callback<XVariableDescriptor> globalCallback) {
		if (report == null) {
			panel.removeAll();
			StringBuffer text = new StringBuffer(instructionText);
			text.append(" ");
			text.append(chooseHint);
			instructions.setText(text.toString());
			panel.removeAll();
			panel.add(instructions);
			panel.layout();
		} else {
			panel.removeAll();
			store.removeAll();
			combo.removeAll();
			WPaloServiceProvider.getInstance().getVariables(report, user, new Callback<XVariableDescriptor>(){
				public void onSuccess(XVariableDescriptor desc) {
					List <String> vars = desc.getReportVariables();
					combo.add(vars);
					String def = "no variables in sheet";
					if (vars.size() > 0) {
						def = vars.get(0);
					} else {
						combo.add(def);
					}				
					
					combo.setEditable(false);
					if (editor.getInput() instanceof TreeNode) {
						TreeNode node = (TreeNode) editor.getInput();
						while (node.getParent() != null) {
							node = node.getParent();
							if (node.getXObject() != null &&
									node.getXObject() instanceof XDynamicReportFolder) {
								XDynamicReportFolder df = (XDynamicReportFolder) node.getXObject();														
								if (df.getSourceSubset() != null) {										
									XSubset key = df.getSourceSubset();
									String match = desc.getVariableMapping().get(key);
									if (match == null) {
										match = def;
										editor.markDirty();
									}
									store.add(new VariableMatch(key, "", match));							
								} else if (df.getSourceHierarchy() != null) {
									XHierarchy key = df.getSourceHierarchy();
									String match = desc.getVariableMapping().get(key);
									if (match == null) {
										match = def;
										editor.markDirty();
									}
									store.add(new VariableMatch(key, "", match));
								} 
							}
						}						
					}					
					//store.add(new VariableMatch("Test", report.getName(), "Test 3"));
					panel.add(grid);
					panel.layout();
					if (globalCallback != null) {
						globalCallback.onSuccess(desc);
					}
				}
			});
		}
	}
	
	private final ContentPanel createPanel() {
		panel = new ContentPanel();
		//panel.setScrollMode(Scroll.AUTO);
		panel.setHeading("Assign variables");
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setStyleAttribute("padding", "20");
		panel.setLayout(new FitLayout());
		
		instructions = new LabelField();
		instructions.addStyleName("main-font");
		panel.add(instructions);
				
		List <ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig("variablefromstructure",
				"Variable from structure", 150);
		column.setRenderer(new GridCellRenderer<VariableMatch>(){
			public String render(VariableMatch model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableMatch> store) {
				Object val = model.get(property);
				if (val instanceof XObject) {
					return ((XObject) val).getName() + " Selection";
				}
				if (val == null) {
					return "";
				}
				return val.toString();
			}
		});
		configs.add(column);
		
		column = new ColumnConfig("default", "Default value", 150);
		configs.add(column);
		
		column = new ColumnConfig("reportvariable", "Variable in report", 150);		
		
		CellEditor editor = new CellEditor(combo) {
			public Object preProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				return combo.findModel(value.toString());
			}
			
			public Object postProcessValue(Object value) {
				if (value == null) {
					return null;
				}
				return ((ModelData) value).get("value");
			}
		};
		
		column.setEditor(editor);
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);  
		   		
		grid = new EditorGrid <VariableMatch>(store, cm);  
		//grid.setAutoExpandColumn("variablefromstructure");  
		grid.setBorders(true);  
		//panel.add(grid);  		
		//panel.add(editPanel);

		setLabelText(null);

		return panel;
	}
}
