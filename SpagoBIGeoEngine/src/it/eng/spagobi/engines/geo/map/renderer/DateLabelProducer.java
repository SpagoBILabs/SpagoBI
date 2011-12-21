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
package it.eng.spagobi.engines.geo.map.renderer;

import it.eng.spago.base.SourceBean;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class DateLabelProducer.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DateLabelProducer extends AbstractLabelProducer {
	
	/** The day format. */
	private String dayFormat = "dd/MM/yyyy";
	
	/** The hour format. */
	private String hourFormat = "HH:mm";
	
	/** The text. */
	private String text = "Ultimo aggiornamento del ${day} alle ore ${hour}";
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.renderer.LabelProducer#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean conf) {
		super.init(conf);
		SourceBean formatSB = (SourceBean)conf.getAttribute("FORMAT");
		dayFormat = (String)formatSB.getAttribute("day");
		hourFormat = (String)formatSB.getAttribute("hour");
		SourceBean textSB = (SourceBean)conf.getAttribute("TEXT");
		text = textSB.getCharacters();
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.renderer.LabelProducer#getLabel()
	 */
	public String getLabel(){
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat df = null;
		
		df = new SimpleDateFormat( dayFormat );
		String day = df.format(date);
		
		df = new SimpleDateFormat( hourFormat );
		String hour = df.format(date);
		
		String label = text;
		label = label.replaceAll("\\$\\{day\\}", day);
		label = label.replaceAll("\\$\\{hour\\}", hour);
		
		return label;
	}
}
