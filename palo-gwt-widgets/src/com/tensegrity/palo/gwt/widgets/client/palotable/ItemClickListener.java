package com.tensegrity.palo.gwt.widgets.client.palotable;

import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;

public interface ItemClickListener {
	public void leftClicked(XAxisItem item, List <XAxisItem> roots, String viewId, String axisId, boolean column, int x, int y);
	public void rightClicked(XAxisItem item, List <XAxisItem> roots, String viewId, String axisId, boolean column, int x, int y);
}
