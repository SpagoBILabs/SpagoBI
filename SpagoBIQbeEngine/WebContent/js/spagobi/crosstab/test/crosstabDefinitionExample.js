var crosstabDefinition = 
  {
    rows:
      [
        {id:"it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily", alias:"Product Family", iconCls:"attribute", nature:"attribute"}
        , {id:"it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productDepartment", alias:"Product Department", iconCls:"attribute", nature:"attribute"}
      ]
      , columns:
      [
        {id:"it.eng.spagobi.SalesFact1998::timeByDay(time_id):theYear", alias:"Year", iconCls:"attribute", nature:"attribute"}
        , {id:"it.eng.spagobi.SalesFact1998::timeByDay(time_id):monthOfYear", alias:"Month of Year", iconCls:"attribute", nature:"attribute"}
      ] 
      , measures:
      [
        {id:"it.eng.spagobi.SalesFact1998:unitSales", alias:"Unit Sales", funct: "SUM", iconCls:"measure", nature:"measure"}
        , {id:"it.eng.spagobi.SalesFact1998:storeSales", alias:"Store Sales", funct: "SUM", iconCls:"measure", nature:"measure"}
        , {id:"it.eng.spagobi.SalesFact1998:storeCost", alias:"Store Cost", funct: "SUM", iconCls:"measure", nature:"measure"}
      ] 
      , config:
        {
          measureson:"rows"
          , calculatetotalsonrows:"on"
          , calculatesubtotalsonrows:"on" 
          //, calculatetotalsoncolumns:"on" 
          //, calculatesubtotalsoncolumns:"on"
        }
  };