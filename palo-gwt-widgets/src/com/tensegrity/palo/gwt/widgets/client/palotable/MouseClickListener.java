package com.tensegrity.palo.gwt.widgets.client.palotable;

import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;

public interface MouseClickListener {
	public void leftClicked(HeaderItem item, int x, int y);
	public void rightClicked(HeaderItem item, int x, int y);
}
