/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.services.dataset.bo;

public class SpagoBiDataSet  implements java.io.Serializable {
    private java.lang.String adress;

    private java.lang.Integer categoryId;

    private it.eng.spagobi.services.datasource.bo.SpagoBiDataSource dataSource;

    private java.lang.String datamarts;

    private java.lang.String description;

    private int dsId;

    private java.lang.String dsMetadata;

    private java.lang.String executorClass;

    private java.lang.String fileName;

    private java.lang.String javaClassName;

    private java.lang.String jsonQuery;

    private java.lang.String label;

    private java.lang.String languageScript;

    private java.lang.String name;

    private boolean numRows;

    private java.lang.String operation;

    private java.lang.String parameters;

    private java.lang.String pivotColumnName;

    private java.lang.String pivotColumnValue;

    private java.lang.String pivotRowName;

    private java.lang.String query;
    
    private java.lang.String queryScript;
    
    private java.lang.String queryScriptLanguage;

    private java.lang.String script;

    private java.lang.Integer transformerId;

    private java.lang.String type;

    private java.lang.String customData;

    
    public SpagoBiDataSet() {
    }

    public SpagoBiDataSet(
           java.lang.String adress,
           java.lang.Integer categoryId,
           it.eng.spagobi.services.datasource.bo.SpagoBiDataSource dataSource,
           java.lang.String datamarts,
           java.lang.String description,
           int dsId,
           java.lang.String dsMetadata,
           java.lang.String executorClass,
           java.lang.String fileName,
           java.lang.String javaClassName,
           java.lang.String jsonQuery,
           java.lang.String label,
           java.lang.String languageScript,
           java.lang.String name,
           boolean numRows,
           java.lang.String operation,
           java.lang.String parameters,
           java.lang.String pivotColumnName,
           java.lang.String pivotColumnValue,
           java.lang.String pivotRowName,
           java.lang.String query,
           java.lang.String queryScript,
           java.lang.String queryScriptLanguage,
           java.lang.String script,
           java.lang.Integer transformerId,
           java.lang.String type,
           java.lang.String customData) {
           this.adress = adress;
           this.categoryId = categoryId;
           this.dataSource = dataSource;
           this.datamarts = datamarts;
           this.description = description;
           this.dsId = dsId;
           this.dsMetadata = dsMetadata;
           this.executorClass = executorClass;
           this.fileName = fileName;
           this.javaClassName = javaClassName;
           this.jsonQuery = jsonQuery;
           this.label = label;
           this.languageScript = languageScript;
           this.name = name;
           this.numRows = numRows;
           this.operation = operation;
           this.parameters = parameters;
           this.pivotColumnName = pivotColumnName;
           this.pivotColumnValue = pivotColumnValue;
           this.pivotRowName = pivotRowName;
           this.query = query;
           this.queryScript = queryScript;
           this.queryScriptLanguage = queryScriptLanguage;
           this.script = script;
           this.transformerId = transformerId;
           this.type = type;
           this.customData = customData;
    }


    /**
     * Gets the adress value for this SpagoBiDataSet.
     * 
     * @return adress
     */
    public java.lang.String getAdress() {
        return adress;
    }


    /**
     * Sets the adress value for this SpagoBiDataSet.
     * 
     * @param adress
     */
    public void setAdress(java.lang.String adress) {
        this.adress = adress;
    }


    /**
     * Gets the categoryId value for this SpagoBiDataSet.
     * 
     * @return categoryId
     */
    public java.lang.Integer getCategoryId() {
        return categoryId;
    }


    /**
     * Sets the categoryId value for this SpagoBiDataSet.
     * 
     * @param categoryId
     */
    public void setCategoryId(java.lang.Integer categoryId) {
        this.categoryId = categoryId;
    }


    /**
     * Gets the dataSource value for this SpagoBiDataSet.
     * 
     * @return dataSource
     */
    public it.eng.spagobi.services.datasource.bo.SpagoBiDataSource getDataSource() {
        return dataSource;
    }


    /**
     * Sets the dataSource value for this SpagoBiDataSet.
     * 
     * @param dataSource
     */
    public void setDataSource(it.eng.spagobi.services.datasource.bo.SpagoBiDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Gets the datamarts value for this SpagoBiDataSet.
     * 
     * @return datamarts
     */
    public java.lang.String getDatamarts() {
        return datamarts;
    }


    /**
     * Sets the datamarts value for this SpagoBiDataSet.
     * 
     * @param datamarts
     */
    public void setDatamarts(java.lang.String datamarts) {
        this.datamarts = datamarts;
    }


    /**
     * Gets the description value for this SpagoBiDataSet.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this SpagoBiDataSet.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the dsId value for this SpagoBiDataSet.
     * 
     * @return dsId
     */
    public int getDsId() {
        return dsId;
    }


    /**
     * Sets the dsId value for this SpagoBiDataSet.
     * 
     * @param dsId
     */
    public void setDsId(int dsId) {
        this.dsId = dsId;
    }


    /**
     * Gets the dsMetadata value for this SpagoBiDataSet.
     * 
     * @return dsMetadata
     */
    public java.lang.String getDsMetadata() {
        return dsMetadata;
    }


    /**
     * Sets the dsMetadata value for this SpagoBiDataSet.
     * 
     * @param dsMetadata
     */
    public void setDsMetadata(java.lang.String dsMetadata) {
        this.dsMetadata = dsMetadata;
    }


    /**
     * Gets the executorClass value for this SpagoBiDataSet.
     * 
     * @return executorClass
     */
    public java.lang.String getExecutorClass() {
        return executorClass;
    }


    /**
     * Sets the executorClass value for this SpagoBiDataSet.
     * 
     * @param executorClass
     */
    public void setExecutorClass(java.lang.String executorClass) {
        this.executorClass = executorClass;
    }


    /**
     * Gets the fileName value for this SpagoBiDataSet.
     * 
     * @return fileName
     */
    public java.lang.String getFileName() {
        return fileName;
    }


    /**
     * Sets the fileName value for this SpagoBiDataSet.
     * 
     * @param fileName
     */
    public void setFileName(java.lang.String fileName) {
        this.fileName = fileName;
    }


    /**
     * Gets the javaClassName value for this SpagoBiDataSet.
     * 
     * @return javaClassName
     */
    public java.lang.String getJavaClassName() {
        return javaClassName;
    }


    /**
     * Sets the javaClassName value for this SpagoBiDataSet.
     * 
     * @param javaClassName
     */
    public void setJavaClassName(java.lang.String javaClassName) {
        this.javaClassName = javaClassName;
    }


    /**
     * Gets the jsonQuery value for this SpagoBiDataSet.
     * 
     * @return jsonQuery
     */
    public java.lang.String getJsonQuery() {
        return jsonQuery;
    }


    /**
     * Sets the jsonQuery value for this SpagoBiDataSet.
     * 
     * @param jsonQuery
     */
    public void setJsonQuery(java.lang.String jsonQuery) {
        this.jsonQuery = jsonQuery;
    }


    /**
     * Gets the label value for this SpagoBiDataSet.
     * 
     * @return label
     */
    public java.lang.String getLabel() {
        return label;
    }


    /**
     * Sets the label value for this SpagoBiDataSet.
     * 
     * @param label
     */
    public void setLabel(java.lang.String label) {
        this.label = label;
    }


    /**
     * Gets the languageScript value for this SpagoBiDataSet.
     * 
     * @return languageScript
     */
    public java.lang.String getLanguageScript() {
        return languageScript;
    }


    /**
     * Sets the languageScript value for this SpagoBiDataSet.
     * 
     * @param languageScript
     */
    public void setLanguageScript(java.lang.String languageScript) {
        this.languageScript = languageScript;
    }


    /**
     * Gets the name value for this SpagoBiDataSet.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this SpagoBiDataSet.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the numRows value for this SpagoBiDataSet.
     * 
     * @return numRows
     */
    public boolean isNumRows() {
        return numRows;
    }


    /**
     * Sets the numRows value for this SpagoBiDataSet.
     * 
     * @param numRows
     */
    public void setNumRows(boolean numRows) {
        this.numRows = numRows;
    }


    /**
     * Gets the operation value for this SpagoBiDataSet.
     * 
     * @return operation
     */
    public java.lang.String getOperation() {
        return operation;
    }


    /**
     * Sets the operation value for this SpagoBiDataSet.
     * 
     * @param operation
     */
    public void setOperation(java.lang.String operation) {
        this.operation = operation;
    }


    /**
     * Gets the parameters value for this SpagoBiDataSet.
     * 
     * @return parameters
     */
    public java.lang.String getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this SpagoBiDataSet.
     * 
     * @param parameters
     */
    public void setParameters(java.lang.String parameters) {
        this.parameters = parameters;
    }


    /**
     * Gets the pivotColumnName value for this SpagoBiDataSet.
     * 
     * @return pivotColumnName
     */
    public java.lang.String getPivotColumnName() {
        return pivotColumnName;
    }


    /**
     * Sets the pivotColumnName value for this SpagoBiDataSet.
     * 
     * @param pivotColumnName
     */
    public void setPivotColumnName(java.lang.String pivotColumnName) {
        this.pivotColumnName = pivotColumnName;
    }


    /**
     * Gets the pivotColumnValue value for this SpagoBiDataSet.
     * 
     * @return pivotColumnValue
     */
    public java.lang.String getPivotColumnValue() {
        return pivotColumnValue;
    }


    /**
     * Sets the pivotColumnValue value for this SpagoBiDataSet.
     * 
     * @param pivotColumnValue
     */
    public void setPivotColumnValue(java.lang.String pivotColumnValue) {
        this.pivotColumnValue = pivotColumnValue;
    }


    /**
     * Gets the pivotRowName value for this SpagoBiDataSet.
     * 
     * @return pivotRowName
     */
    public java.lang.String getPivotRowName() {
        return pivotRowName;
    }


    /**
     * Sets the pivotRowName value for this SpagoBiDataSet.
     * 
     * @param pivotRowName
     */
    public void setPivotRowName(java.lang.String pivotRowName) {
        this.pivotRowName = pivotRowName;
    }


    /**
     * Gets the query value for this SpagoBiDataSet.
     * 
     * @return query
     */
    public java.lang.String getQuery() {
        return query;
    }


    /**
     * Sets the query value for this SpagoBiDataSet.
     * 
     * @param query
     */
    public void setQuery(java.lang.String query) {
        this.query = query;
    }
    
    


    public java.lang.String getQueryScript() {
		return queryScript;
	}

	public void setQueryScript(java.lang.String queryScript) {
		this.queryScript = queryScript;
	}

	public java.lang.String getQueryScriptLanguage() {
		return queryScriptLanguage;
	}

	public void setQueryScriptLanguage(java.lang.String queryScriptLanguage) {
		this.queryScriptLanguage = queryScriptLanguage;
	}

	/**
     * Gets the script value for this SpagoBiDataSet.
     * 
     * @return script
     */
    public java.lang.String getScript() {
        return script;
    }


    /**
     * Sets the script value for this SpagoBiDataSet.
     * 
     * @param script
     */
    public void setScript(java.lang.String script) {
        this.script = script;
    }


    /**
     * Gets the transformerId value for this SpagoBiDataSet.
     * 
     * @return transformerId
     */
    public java.lang.Integer getTransformerId() {
        return transformerId;
    }


    /**
     * Sets the transformerId value for this SpagoBiDataSet.
     * 
     * @param transformerId
     */
    public void setTransformerId(java.lang.Integer transformerId) {
        this.transformerId = transformerId;
    }


    /**
     * Gets the type value for this SpagoBiDataSet.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this SpagoBiDataSet.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }
    
    /**
     * Gets the type value for this SpagoBiDataSet.
     * 
     * @return type
     */
    public java.lang.String getCustomData() {
        return customData;
    }


    /**
     * Sets the type value for this SpagoBiDataSet.
     * 
     * @param type
     */
    public void setCustomData(java.lang.String customData) {
        this.customData = customData;
    }
    

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SpagoBiDataSet)) return false;
        SpagoBiDataSet other = (SpagoBiDataSet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.adress==null && other.getAdress()==null) || 
             (this.adress!=null &&
              this.adress.equals(other.getAdress()))) &&
            ((this.categoryId==null && other.getCategoryId()==null) || 
             (this.categoryId!=null &&
              this.categoryId.equals(other.getCategoryId()))) &&
            ((this.dataSource==null && other.getDataSource()==null) || 
             (this.dataSource!=null &&
              this.dataSource.equals(other.getDataSource()))) &&
            ((this.datamarts==null && other.getDatamarts()==null) || 
             (this.datamarts!=null &&
              this.datamarts.equals(other.getDatamarts()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.dsId == other.getDsId() &&
            ((this.dsMetadata==null && other.getDsMetadata()==null) || 
             (this.dsMetadata!=null &&
              this.dsMetadata.equals(other.getDsMetadata()))) &&
            ((this.executorClass==null && other.getExecutorClass()==null) || 
             (this.executorClass!=null &&
              this.executorClass.equals(other.getExecutorClass()))) &&
            ((this.fileName==null && other.getFileName()==null) || 
             (this.fileName!=null &&
              this.fileName.equals(other.getFileName()))) &&
            ((this.javaClassName==null && other.getJavaClassName()==null) || 
             (this.javaClassName!=null &&
              this.javaClassName.equals(other.getJavaClassName()))) &&
            ((this.jsonQuery==null && other.getJsonQuery()==null) || 
             (this.jsonQuery!=null &&
              this.jsonQuery.equals(other.getJsonQuery()))) &&
            ((this.label==null && other.getLabel()==null) || 
             (this.label!=null &&
              this.label.equals(other.getLabel()))) &&
            ((this.languageScript==null && other.getLanguageScript()==null) || 
             (this.languageScript!=null &&
              this.languageScript.equals(other.getLanguageScript()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.numRows == other.isNumRows() &&
            ((this.operation==null && other.getOperation()==null) || 
             (this.operation!=null &&
              this.operation.equals(other.getOperation()))) &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              this.parameters.equals(other.getParameters()))) &&
            ((this.pivotColumnName==null && other.getPivotColumnName()==null) || 
             (this.pivotColumnName!=null &&
              this.pivotColumnName.equals(other.getPivotColumnName()))) &&
            ((this.pivotColumnValue==null && other.getPivotColumnValue()==null) || 
             (this.pivotColumnValue!=null &&
              this.pivotColumnValue.equals(other.getPivotColumnValue()))) &&
            ((this.pivotRowName==null && other.getPivotRowName()==null) || 
             (this.pivotRowName!=null &&
              this.pivotRowName.equals(other.getPivotRowName()))) &&
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery()))) &&
            ((this.queryScript==null && other.getQueryScript()==null) || 
             (this.queryScript!=null &&
              this.queryScript.equals(other.getQueryScript()))) &&
            ((this.queryScriptLanguage==null && other.getQueryScriptLanguage()==null) || 
             (this.queryScriptLanguage!=null && 
              this.queryScriptLanguage.equals(other.getQueryScriptLanguage()))) &&
            ((this.script==null && other.getScript()==null) || 
             (this.script!=null &&
              this.script.equals(other.getScript()))) &&
            ((this.transformerId==null && other.getTransformerId()==null) || 
             (this.transformerId!=null &&
              this.transformerId.equals(other.getTransformerId()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.customData==null && other.getCustomData()==null) || 
             (this.customData!=null &&
              this.customData.equals(other.getCustomData())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAdress() != null) {
            _hashCode += getAdress().hashCode();
        }
        if (getCategoryId() != null) {
            _hashCode += getCategoryId().hashCode();
        }
        if (getDataSource() != null) {
            _hashCode += getDataSource().hashCode();
        }
        if (getDatamarts() != null) {
            _hashCode += getDatamarts().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += getDsId();
        if (getDsMetadata() != null) {
            _hashCode += getDsMetadata().hashCode();
        }
        if (getExecutorClass() != null) {
            _hashCode += getExecutorClass().hashCode();
        }
        if (getFileName() != null) {
            _hashCode += getFileName().hashCode();
        }
        if (getJavaClassName() != null) {
            _hashCode += getJavaClassName().hashCode();
        }
        if (getJsonQuery() != null) {
            _hashCode += getJsonQuery().hashCode();
        }
        if (getLabel() != null) {
            _hashCode += getLabel().hashCode();
        }
        if (getLanguageScript() != null) {
            _hashCode += getLanguageScript().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += (isNumRows() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOperation() != null) {
            _hashCode += getOperation().hashCode();
        }
        if (getParameters() != null) {
            _hashCode += getParameters().hashCode();
        }
        if (getPivotColumnName() != null) {
            _hashCode += getPivotColumnName().hashCode();
        }
        if (getPivotColumnValue() != null) {
            _hashCode += getPivotColumnValue().hashCode();
        }
        if (getPivotRowName() != null) {
            _hashCode += getPivotRowName().hashCode();
        }
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        if (getQueryScript() != null) {
            _hashCode += getQueryScript().hashCode();
        }
        if (getQueryScriptLanguage() != null) {
            _hashCode += getQueryScriptLanguage().hashCode();
        }
        if (getScript() != null) {
            _hashCode += getScript().hashCode();
        }
        if (getTransformerId() != null) {
            _hashCode += getTransformerId().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getCustomData() != null) {
            _hashCode += getCustomData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SpagoBiDataSet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bo.dataset.services.spagobi.eng.it", "SpagoBiDataSet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "adress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("categoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "categoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataSource");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataSource"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bo.datasource.services.spagobi.eng.it", "SpagoBiDataSource"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datamarts");
        elemField.setXmlName(new javax.xml.namespace.QName("", "datamarts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dsId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dsId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dsMetadata");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dsMetadata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("executorClass");
        elemField.setXmlName(new javax.xml.namespace.QName("", "executorClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fileName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("javaClassName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "javaClassName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jsonQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jsonQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("label");
        elemField.setXmlName(new javax.xml.namespace.QName("", "label"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("languageScript");
        elemField.setXmlName(new javax.xml.namespace.QName("", "languageScript"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numRows");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numRows"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "operation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameters");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parameters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pivotColumnName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pivotColumnName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pivotColumnValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pivotColumnValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pivotRowName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pivotRowName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("", "query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("script");
        elemField.setXmlName(new javax.xml.namespace.QName("", "script"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transformerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
