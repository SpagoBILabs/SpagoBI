package it.eng.spagobi.twitter.analysis.cache;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TwitterHibernateListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		TwitterHibernateUtil.getSessionFactory();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		TwitterHibernateUtil.getSessionFactory().close();
	}
}
