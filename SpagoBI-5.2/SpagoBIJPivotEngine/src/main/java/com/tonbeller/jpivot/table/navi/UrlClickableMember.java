/*
 * Copyright (c) 1971-2003 TONBELLER AG, Bensheim.
 * All rights reserved.
 */
package com.tonbeller.jpivot.table.navi;

import java.text.MessageFormat;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.table.SpanBuilder.SBContext;
import com.tonbeller.wcf.charset.CharsetFilter;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * creates a hyperlink in the table with a specified url. The URL may contain
 * the unique name of the member that belongs to the hyperlink.
 * 
 * Overridden to change parameter passage
 * 
 *  @author giulio gavardi
 * @author av
 * @since Mar 27, 2006
 */
public class UrlClickableMember extends AbstractClickableMember {
	/** 
	 * urlPattern contains {0} which is replaced with the unique name 
	 * of the member
	 */
	private String urlPattern;
	private String menuLabel;

	/**
	 * @param uniqueName name of level, hierarchy, dimension that shall be clickable
	 * 
	 * @param urlPattern any url. {0} will be replaced with the unique name of the
	 * selected member
	 */

	protected UrlClickableMember(String uniqueName, String menuLabel, String urlPattern) {
		super(uniqueName);
		this.menuLabel = menuLabel;
		this.urlPattern = urlPattern;
	}

	/**
	 * unique name in url
	 */
	private String getPatternUrl(Member member) {
		String pattern = urlPattern == null ? "?param={0}" : urlPattern;
		//String uname = CharsetFilter.urlEncode(parser.unparse(member));
		// uname is [hierarchy].[level].[member]     // I want only the last one
		String ename=parser.unparse(member);
		int lastIndex=ename.lastIndexOf("].[");
		String newName=ename.substring(lastIndex+3, ename.length()-1);
		Object[] args = new Object[] {newName};
		return MessageFormat.format(pattern, args);
	}

	public void decorate(SBContext sbctx, Displayable obj) {
		if (!(obj instanceof Member))
			return;

		Member m = (Member) obj;
		if (match(m)) {
			sbctx.addClickable(getPatternUrl(m), menuLabel);
		}
	}

	/**
	 * ignore
	 */
	public void request(RequestContext context) throws Exception {
	}

	/**
	 * ignore
	 */
	public void modelChanged(ModelChangeEvent e) {
	}

	/**
	 * ignore
	 */
	public void structureChanged(ModelChangeEvent e) {
	}

}
