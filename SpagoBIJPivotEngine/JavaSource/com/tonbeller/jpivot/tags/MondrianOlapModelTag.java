/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.tags;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.jpivotaddins.roles.SpagoBIMondrianRole;


import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;



import org.xml.sax.SAXException;

import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.tags.MondrianModelFactory.Config;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * jsp tag that defines a mondrian query
 */
public class MondrianOlapModelTag extends OlapModelTag {

  String dataSource;
  String jdbcDriver;
  String jdbcUser;
  String jdbcPassword;
  String jdbcUrl;
  String catalogUri;
  String config;
  String role;
  String dynResolver;
  String dynLocale;
  String connectionPooling;
  String dataSourceChangeListener;
  
  Resources res = Resources.instance();

  protected OlapModel getOlapModel(RequestContext context) throws JspException, OlapException, SAXException, IOException {
    MondrianModelFactory.Config cfg = new MondrianModelFactory.Config();
    URL schemaUrl;
    if (catalogUri.startsWith("/"))
      schemaUrl = pageContext.getServletContext().getResource(catalogUri);
    else
      schemaUrl = new URL(catalogUri);
    if (schemaUrl == null)
      throw new JspException("could not find Catalog \"" + catalogUri + "\"");


    cfg.setMdxQuery(getBodyContent().getString());
    // Add the schema URL.  Enclose the value in quotes to permit
    // schema URLs that include things like ;jsessionid values.
    cfg.setSchemaUrl("\"" + schemaUrl.toExternalForm() + "\"");
    cfg.setJdbcUrl(jdbcUrl);
    cfg.setJdbcDriver(jdbcDriver);
    cfg.setJdbcUser(jdbcUser);
    cfg.setJdbcPassword(jdbcPassword);
    cfg.setDataSource(dataSource);
    cfg.setRole(role);
    cfg.setDynResolver(dynResolver);
    cfg.setDynLocale(dynLocale);
    cfg.setConnectionPooling(connectionPooling);
    cfg.setDataSourceChangeListener(dataSourceChangeListener);

    allowOverride(context, cfg);

    URL url;
    if (config == null)
      url = getDefaultConfig();
    else
      url = pageContext.getServletContext().getResource(config);

    HttpSession session = context.getRequest().getSession();
    IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
    
    String filers=(String)session.getAttribute("filters");
    
    MondrianModel mm = MondrianModelFactory.instance(filers,url, cfg, profile);
    
    //MondrianModel mm = MondrianModelFactory.instance(url, cfg);
    //mm.setRole(new SpagoBIMondrianRole(filers,profile));
    OlapModel om = (OlapModel) mm.getTopDecorator();
    om.setLocale(context.getLocale());
    om.setServletContext(context.getSession().getServletContext());
    return om;
  }

  /**
   * default implementation delegates to {@link Config#allowOverride(RequestContext)}
   */
  protected void allowOverride(RequestContext context, Config cfg) {
    cfg.allowOverride(context);
  }

  protected URL getDefaultConfig() {
    return MondrianOlapModelTag.class.getResource("/com/tonbeller/jpivot/mondrian/config.xml");
  }


  /**
   * Returns the catalogUri.
   *
   * @return String
   */
  public String getCatalogUri() {
    return catalogUri;
  }

  /**
   * Returns the jdbcDriver.
   *
   * @return String
   */
  public String getJdbcDriver() {
    return jdbcDriver;
  }

  /**
   * Returns the jdbcUrl.
   *
   * @return String
   */
  public String getJdbcUrl() {
    return jdbcUrl;
  }

  /**
   * Sets the catalogUri.
   *
   * @param catalogUri
   *          The catalogUri to set
   */
  public void setCatalogUri(String catalogUri) {
    this.catalogUri = catalogUri;
  }

  /**
   * Sets the jdbcDriver.
   *
   * @param jdbcDriver
   *          The jdbcDriver to set
   */
  public void setJdbcDriver(String jdbcDriver) {
    this.jdbcDriver = jdbcDriver;
  }

  /**
   * Sets the jdbcUrl.
   *
   * @param jdbcUrl
   *          The jdbcUrl to set
   */
  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  /**
   * Returns the jdbcPassword.
   *
   * @return String
   */
  public String getJdbcPassword() {
    return jdbcPassword;
  }

  /**
   * Returns the jdbcUser.
   *
   * @return String
   */
  public String getJdbcUser() {
    return jdbcUser;
  }

  /**
   * Sets the jdbcPassword.
   *
   * @param jdbcPassword
   *          The jdbcPassword to set
   */
  public void setJdbcPassword(String jdbcPassword) {
    this.jdbcPassword = jdbcPassword;
  }

  /**
   * Sets the jdbcUser.
   *
   * @param jdbcUser
   *          The jdbcUser to set
   */
  public void setJdbcUser(String jdbcUser) {
    this.jdbcUser = jdbcUser;
  }

  /**
   * Returns the config.
   *
   * @return String
   */
  public String getConfig() {
    return config;
  }

  /**
   * Sets the config.
   *
   * @param config
   *          The config to set
   */
  public void setConfig(String config) {
    this.config = config;
  }

  /**
   * @param string
   */
  public void setDataSource(String string) {
    dataSource = string;
  }

  /**
   * @param role
   *          The role to set.
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * @return the dynamic variable resolver class name
   */
  public String getDynResolver() {
    return dynResolver;
  }

  /**
   * @param dynresolver - the dynamic variable resolver class name
   */
  public void setDynResolver(String dynResolver) {
    this.dynResolver = dynResolver;
  }

  /**
   * @param connectionPooling - "false" : Mondrian must not pool JDBC connections
   */
  public void setConnectionPooling(String connectionPooling) {
    this.connectionPooling = connectionPooling;
  }

  /**
   * @return "false" if Mondrion must not pool JDBC connections
   */
  public String getConnectionPooling() {
    return connectionPooling;
  }
  public String getDataSource() {
    return dataSource;
  }
  public String getRole() {
    return role;
  }
/**
 * Getter for property locale.
 * @return Value of property locale.
 */
  public String getDynLocale() {
      return this.dynLocale;
  }
  /**
   * Setter for property locale.
   * @param locale New value of property locale.
   */
  public void setDynLocale(String dynLocale) {
      this.dynLocale = dynLocale;
  }

/**
 * @return Returns the dataSourceChangeListener.
 */
public String getDataSourceChangeListener() {
    return dataSourceChangeListener;
}

/**
 * @param dataSourceChangeListener The dataSourceChangeListener to set.
 */
public void setDataSourceChangeListener(String dataSourceChangeListener) {
    this.dataSourceChangeListener = dataSourceChangeListener;
}

    
}
