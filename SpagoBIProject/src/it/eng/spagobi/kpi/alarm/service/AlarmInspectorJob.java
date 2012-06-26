/* SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.kpi.alarm.service;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.kpi.alarm.bo.AlertSendingItem;
import it.eng.spagobi.kpi.alarm.dao.SbiAlarmContactDAOHibImpl;
import it.eng.spagobi.kpi.alarm.dao.SbiAlarmEventDAOHibImpl;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmEvent;
import it.eng.spagobi.tools.scheduler.jobs.AbstractSpagoBIJob;
import it.eng.spagobi.tools.scheduler.to.DispatchContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AlarmInspectorJob  extends AbstractSpagoBIJob implements Job {

	static private Logger logger = Logger.getLogger(AlarmInspectorJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */

	SbiAlarmEventDAOHibImpl sae = new SbiAlarmEventDAOHibImpl();
	SbiAlarmContactDAOHibImpl sac = new SbiAlarmContactDAOHibImpl();

	private Map<SbiAlarmContact, List<AlertSendingItem>> alertSendingSessionMap = new HashMap<SbiAlarmContact, List<AlertSendingItem>>();
	private List<AlertSendingItem> alertSendingSessionList = null;
	private AlertSendingItem alertSendingItem = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("IN");
		try {
			this.setTenant(jobExecutionContext);
			this.executeInternal(jobExecutionContext);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
	}
	

	private void executeInternal(JobExecutionContext jex) throws JobExecutionException {
		logger.debug("IN");
		org.hibernate.Session hsession = null;
		List<SbiAlarmEvent> activeSbiAlarmEventList = null;
		SbiAlarm sbiAlarm = null;

		try {
			hsession = sae.getSession();

			activeSbiAlarmEventList = sae.findActive();
			for (SbiAlarmEvent sbiAlarmEvent : activeSbiAlarmEventList) {

				if (logger.isInfoEnabled())
					logger.info("Found AlarmEvent: "
							+ sbiAlarmEvent.getKpiName());

				sbiAlarm = sbiAlarmEvent.getSbiAlarms();
				String resource = sbiAlarmEvent.getResources();

				// creo un item e gli imposto l'evento e l'allarme
				alertSendingItem = new AlertSendingItem(sbiAlarm, sbiAlarmEvent);

				if (logger.isDebugEnabled())
					logger.debug("Created AlertSendingItem: "
							+ alertSendingItem);

				List<SbiAlarmContact> sbiAlarmContactList = new ArrayList<SbiAlarmContact>();
				List<SbiAlarmContact> associatedContactList = new ArrayList<SbiAlarmContact>(
						sbiAlarm.getSbiAlarmContacts());

				if (resource != null) {
					if (logger.isDebugEnabled())
						logger.debug("Resource enhanced: " + resource);

					for (SbiAlarmContact associatedContact : associatedContactList) {
						if (resource.equals(associatedContact.getResources())
								|| associatedContact.getResources() == null) {
							sbiAlarmContactList.add(associatedContact);

							if (logger.isDebugEnabled())
								logger.debug("Contact '" + associatedContact
										+ "' added.");
						}
					}
				} else {
					if (logger.isDebugEnabled())
						logger.debug("Resource not enhanced.");

					for (SbiAlarmContact associatedContact : associatedContactList) {
						if (associatedContact.getResources() == null) {
							sbiAlarmContactList.add(associatedContact);

							if (logger.isDebugEnabled())
								logger.debug("Contact '" + associatedContact
										+ "' added.");
						}
					}
				}
				if (logger.isDebugEnabled())
					logger.debug("Distribution list: " + sbiAlarmContactList
							+ "\n");

				for (SbiAlarmContact sbiAlarmContact : sbiAlarmContactList) {
					alertSendingSessionList = alertSendingSessionMap
							.get(sbiAlarmContact);
					if (alertSendingSessionList == null) {

						if (logger.isDebugEnabled())
							logger.debug("alertSendingSessionList null");

						alertSendingSessionList = new ArrayList<AlertSendingItem>();
					}

					alertSendingSessionList.add(alertSendingItem);

					if (logger.isDebugEnabled())
						logger.debug("Contact '" + sbiAlarmContact.getName()
								+ "' added to alertSendingSessionList.");

					alertSendingSessionMap.put(sbiAlarmContact,
							alertSendingSessionList);
				}

				// Se l'event è autodisabilitante
				if (sbiAlarm.isAutoDisabled()) {
					// if(sbiAlarm.isSingleEvent()){
					if (logger.isDebugEnabled())
						logger.debug("Single alarm '" + sbiAlarm.getLabel()
								+ "' disabled.");
					sbiAlarmEvent.setActive(false);
					sae.update(sbiAlarmEvent);
				}
			}

			startEmailSession(alertSendingSessionMap);

		} catch (Throwable e) {
			logger.error("Error while executiong job ", e);
			e.printStackTrace();
		} finally {
			if (hsession != null)
				hsession.close();
			logger.debug("OUT");
		}
	}

	private void startEmailSession(
			Map<SbiAlarmContact, List<AlertSendingItem>> alertSendingSessionMap) {
		logger.debug("IN");

		Set<SbiAlarmContact> keySet = alertSendingSessionMap.keySet();
		DispatchContext sInfo = new DispatchContext();

		if (logger.isDebugEnabled())
			logger.debug("Distribution list parsing.");

		for (SbiAlarmContact sbiAlarmContact : keySet) {
			if (logger.isDebugEnabled())
				logger.debug("Found contact '" + sbiAlarmContact.getName()
						+ "'.");

			List<AlertSendingItem> alertSendingList = alertSendingSessionMap
					.get(sbiAlarmContact);

			SbiAlarm sbiAlarm = null;
			SbiAlarmEvent sbiAlarmEvent = null;

			StringBuffer subject = new StringBuffer();
			StringBuffer text = new StringBuffer();
			for (AlertSendingItem alertSendingItem : alertSendingList) {
				sbiAlarm = alertSendingItem.getSbiAlarm();
				sbiAlarmEvent = alertSendingItem.getSbiAlarmEvent();

				if (logger.isDebugEnabled())
					logger.debug("Found alarm " + sbiAlarm.getName() + ".");

				subject.append(sbiAlarm.getLabel());

				text.append("<font size=\"4\">Allarme </font><font color=\"red\" size=\"4\"><b>");
				text.append(sbiAlarm.getName());
				text.append("</b></font><ul>");

				text.append("<li><font size=\"2\">Lable: ");
				text.append(sbiAlarm.getLabel());
				text.append("</font></li>");
				text.append("<li><font size=\"2\">Text: ");
				text.append(sbiAlarm.getText());
				text.append("</font></li>");
				text.append("<li><font size=\"2\">Description: ");
				text.append(sbiAlarm.getDescr());
				text.append("</font></li>");
				text.append("</ul><br>");
				text.append("<font size=\"3\">Dettaglio KPI:</font><ul>");
				text.append("<li><font size=\"2\">Name: ");
				text.append(sbiAlarmEvent.getKpiName());
				text.append("</font></li>");
				text.append("<li><font size=\"2\">Date: ");
				text.append(sbiAlarmEvent.getEventTs());
				text.append("</font></li>");
				text.append("<li><font size=\"2\">Value: ");
				text.append(sbiAlarmEvent.getKpiValue());
				text.append("</font></li>");
				text.append("<li><font size=\"2\">Threshold: ");
				text.append(sbiAlarmEvent.getThresholdValue());
				text.append("</font></li>");

				String res = sbiAlarmEvent.getResources();
				if (res != null) {
					text.append("<li><font size=\"2\">resources:");
					text.append(res);
					text.append("</font></li>");
				}

				text.append("</ul><hr width=\"90%\">");
			}

			String link = sbiAlarm.getUrl();

			text.append(link);

			String email = sbiAlarmContact.getEmail();
			if (email != null) {
				sInfo.setMailTos(email);
				sInfo.setMailSubj("MGC alarm: " + new Date() + " ["
						+ sbiAlarmContact.getName() + "]");
				sInfo.setMailTxt(text.toString());
			}

			if (logger.isDebugEnabled())
				logger.debug("Sending email to: " + sInfo.getMailTos());

			sendMail(sInfo, null, null, null);
		}

		logger.debug("OUT");

	}

	private void sendMail(DispatchContext sInfo, byte[] response, String retCT,
			String fileExt) {
		logger.debug("IN");
		try {

			String smtphost = SingletonConfig.getInstance().getConfigValue(
					"MAIL.PROFILES.kpi_alarm.smtphost");

			String smtpport = SingletonConfig.getInstance().getConfigValue(
					"MAIL.PROFILES.kpi_alarm.smtpport");
			int smptPort = 25;

			if ((smtphost == null) || smtphost.trim().equals(""))
				throw new Exception("Smtp host not configured");
			if ((smtpport == null) || smtpport.trim().equals("")) {
				throw new Exception("Smtp host not configured");
			} else {
				smptPort = Integer.parseInt(smtpport);
			}

			String from = SingletonConfig.getInstance().getConfigValue(
					"MAIL.PROFILES.kpi_alarm.from");

			if ((from == null) || from.trim().equals(""))
				from = "spagobi.scheduler@eng.it";

			String user = SingletonConfig.getInstance().getConfigValue(
					"MAIL.PROFILES.kpi_alarm.user");

			if ((user == null) || user.trim().equals(""))
				throw new Exception("Smtp user not configured");

			String pass = SingletonConfig.getInstance().getConfigValue(
					"MAIL.PROFILES.kpi_alarm.password");

			if ((pass == null) || pass.trim().equals(""))
				throw new Exception("Smtp password not configured");

			String mailTos = sInfo.getMailTos();

			if ((mailTos == null) || mailTos.trim().equals("")) {
				throw new Exception("No recipient address found");

			}
			String mailSubj = sInfo.getMailSubj();
			String mailTxt = sInfo.getMailTxt();

			String[] recipients = mailTos.split(",");

			// Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", smtphost);
			props.put("mail.smtp.port", smptPort);
			props.put("mail.smtp.auth", "true");

			// create autheticator object
			Authenticator auth = new SMTPAuthenticator(user, pass);

			// open session
			Session session = Session.getDefaultInstance(props, auth);

			// create a message
			MimeMessage msg = new MimeMessage(session);

			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			// IMessageBuilder msgBuilder =
			// MessageBuilderFactory.getMessageBuilder();
			String subject = mailSubj;
			msg.setSubject(subject);

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(mailTxt);

			// create the second message part
			// MimeBodyPart mbp2 = new MimeBodyPart();

			// attach the file to the message
			// SchedulerDataSource sds = new SchedulerDataSource(response,
			// retCT, sbiAlarmEvent.getKpiName() + fileExt);
			// mbp2.setDataHandler(new DataHandler(sds));
			// mbp2.setFileName(sds.getName());

			// create the Multipart and add its parts to it
			MimeMultipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			// mp.addBodyPart(mbp2);

			// add the Multipart to the message
			msg.setContent(mailTxt, "text/html");

			// send message
			Transport.send(msg);
		} catch (Exception e) {
			logger.error("Error while sending schedule result mail", e);
		} catch (Throwable t) {
			logger.error("Error while sending schedule result mail", t);
		} finally {
			logger.debug("OUT");
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		private String username = "";
		private String password = "";

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}

		public SMTPAuthenticator(String user, String pass) {
			this.username = user;
			this.password = pass;
		}
	}

	@SuppressWarnings("unused")
	private class SchedulerDataSource implements DataSource {
		byte[] content = null;
		String name = null;
		String contentType = null;

		public String getContentType() {
			return contentType;
		}

		public InputStream getInputStream() throws IOException {
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			return bais;
		}

		public String getName() {
			return name;
		}

		public OutputStream getOutputStream() throws IOException {
			return null;
		}

		public SchedulerDataSource(byte[] content, String contentType,
				String name) {
			this.content = content;
			this.contentType = contentType;
			this.name = name;
		}
	}
}
