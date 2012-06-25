/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
