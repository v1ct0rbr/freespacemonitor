package com.victorqueiroga.utils;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class MailUtils {

	public void sendEmail(Properties emailConfig, List<String> recipients, String subject, String content) {

		String username = emailConfig.getProperty("mail.smtp.user");
		String password = emailConfig.getProperty("mail.smtp.password");

		Session session = Session.getInstance(emailConfig, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			for (String recipient : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);
			System.out.println("Email sent successfully!");

		} catch (MessagingException e) {
			System.err.println("Email send failed: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
