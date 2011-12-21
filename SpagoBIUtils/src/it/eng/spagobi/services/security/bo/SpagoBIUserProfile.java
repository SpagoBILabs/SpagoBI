/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */

package it.eng.spagobi.services.security.bo;

public class SpagoBIUserProfile  implements java.io.Serializable {
    private java.util.HashMap attributes;

    private java.lang.String[] functions;

    private java.lang.String[] roles;

    private java.lang.String userId;

    private java.lang.String uniqueIdentifier;

    private java.lang.String userName;

    public SpagoBIUserProfile() {
    }

    public SpagoBIUserProfile(
           java.util.HashMap attributes,
           java.lang.String[] functions,
           java.lang.String[] roles,
           java.lang.String userId,
           java.lang.String uniqueIdentifier,
           java.lang.String userName) {
           this.attributes = attributes;
           this.functions = functions;
           this.roles = roles;
           this.userId = userId;
           this.uniqueIdentifier = uniqueIdentifier;
           this.userName = userName;
    }


    /**
     * Gets the attributes value for this SpagoBIUserProfile.
     * 
     * @return attributes
     */
    public java.util.HashMap getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this SpagoBIUserProfile.
     * 
     * @param attributes
     */
    public void setAttributes(java.util.HashMap attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the functions value for this SpagoBIUserProfile.
     * 
     * @return functions
     */
    public java.lang.String[] getFunctions() {
        return functions;
    }


    /**
     * Sets the functions value for this SpagoBIUserProfile.
     * 
     * @param functions
     */
    public void setFunctions(java.lang.String[] functions) {
        this.functions = functions;
    }


    /**
     * Gets the roles value for this SpagoBIUserProfile.
     * 
     * @return roles
     */
    public java.lang.String[] getRoles() {
        return roles;
    }


    /**
     * Sets the roles value for this SpagoBIUserProfile.
     * 
     * @param roles
     */
    public void setRoles(java.lang.String[] roles) {
        this.roles = roles;
    }


    /**
     * Gets the userId value for this SpagoBIUserProfile.
     * 
     * @return userId
     */
    public java.lang.String getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this SpagoBIUserProfile.
     * 
     * @param userId
     */
    public void setUserId(java.lang.String userId) {
        this.userId = userId;
    }


    /**
     * Gets the uniqueIdentifier value for this SpagoBIUserProfile.
     * 
     * @return uniqueIdentifier
     */
    public java.lang.String getUniqueIdentifier() {
        return uniqueIdentifier;
    }


    /**
     * Sets the uniqueIdentifier value for this SpagoBIUserProfile.
     * 
     * @param uniqueIdentifier
     */
    public void setUniqueIdentifier(java.lang.String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }


    /**
     * Gets the userName value for this SpagoBIUserProfile.
     * 
     * @return userName
     */
    public java.lang.String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this SpagoBIUserProfile.
     * 
     * @param userName
     */
    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SpagoBIUserProfile)) return false;
        SpagoBIUserProfile other = (SpagoBIUserProfile) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes()))) &&
            ((this.functions==null && other.getFunctions()==null) || 
             (this.functions!=null &&
              java.util.Arrays.equals(this.functions, other.getFunctions()))) &&
            ((this.roles==null && other.getRoles()==null) || 
             (this.roles!=null &&
              java.util.Arrays.equals(this.roles, other.getRoles()))) &&
            ((this.userId==null && other.getUserId()==null) || 
             (this.userId!=null &&
              this.userId.equals(other.getUserId()))) &&
            ((this.uniqueIdentifier==null && other.getUniqueIdentifier()==null) || 
             (this.uniqueIdentifier!=null &&
              this.uniqueIdentifier.equals(other.getUniqueIdentifier()))) &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName())));
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
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        if (getFunctions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFunctions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFunctions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRoles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRoles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRoles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUserId() != null) {
            _hashCode += getUserId().hashCode();
        }
        if (getUniqueIdentifier() != null) {
            _hashCode += getUniqueIdentifier().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SpagoBIUserProfile.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bo.security.services.spagobi.eng.it", "SpagoBIUserProfile"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("functions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "functions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "roles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uniqueIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "uniqueIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userName"));
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
