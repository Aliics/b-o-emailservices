package fish.eyebrow.blackandorangeservices.emailservices;

import fish.eyebrow.blackandorangeservices.emailservices.exceptions.CredentialNotSetException;
import fish.eyebrow.blackandorangeservices.emailservices.exceptions.MissingPropertyException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SimpleMailHandler {
	private String senderAddress;
	private String senderPassword;

	private String emailHost;
	private int emailPort;

	private boolean requiresAuthentication;
	private int securityLayer;

	public void sendEmail(InternetAddress[] recipients, String subject, String text)
			throws MessagingException, CredentialNotSetException {
		if (senderAddress == null || senderPassword == null || emailHost == null)
			throw new CredentialNotSetException();

		Properties properties = new Properties();
		properties.put("mail.smtp.host", emailHost);
		properties.put("mail.smtp.port", emailPort);
		properties.put("mail.smtp.auth", requiresAuthentication);
		properties.put("mail.smtp.starttls.enable", securityLayer == SecurityTypes.TLS);
		properties.put("mail.smtp.ssl.enable", securityLayer == SecurityTypes.SSL);

		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderAddress, senderPassword);
			}
		};

		Session session = Session.getInstance(properties, authenticator);

		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(senderAddress);
		mimeMessage.setRecipients(MimeMessage.RecipientType.TO, recipients);
		mimeMessage.setSubject(subject);
		mimeMessage.setText(text);

		Transport.send(mimeMessage);
	}

	public void sendEmail(String[] recipients, String subject, String text)
			throws MessagingException, CredentialNotSetException {
		InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
		for (int i = 0; i < recipientAddresses.length; i++) {
			recipientAddresses[i] = InternetAddress.parse(recipients[i])[0];
		}

		sendEmail(recipientAddresses, subject, text);
	}

	public void generateCredentials(Properties properties) throws MissingPropertyException {
		final String senderAddress = (String) properties.get("senderAddress");
		final String senderPassword = (String) properties.get("senderPassword");
		final String emailHost = (String) properties.get("emailHost");
		final String emailPort = (String) properties.get("emailPort");
		final String requiresAuthentication = (String) properties.get("requiresAuthentication");
		final String securityLayer = (String) properties.get("securityLayer");

		if (senderAddress == null || senderPassword == null || emailHost == null || emailPort == null ||
				requiresAuthentication == null || securityLayer == null)
			throw new MissingPropertyException();

		setSenderAddress(senderAddress);
		setSenderPassword(senderPassword);
		setEmailHost(emailHost);
		setEmailPort(emailPort);
		setRequiresAuthentication(requiresAuthentication);
		setSecurityLayer(securityLayer);
	}

	public void generateCredentials(String propertiesPath) throws IOException, MissingPropertyException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesPath));

		generateCredentials(properties);
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderPassword() {
		return senderPassword;
	}

	public void setSenderPassword(String senderPassword) {
		this.senderPassword = senderPassword;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		final String[] separateEmailAddress = senderAddress.split("@");
		final String autoEmailHost = "smtp.".concat(separateEmailAddress[1]);

		this.emailHost = !senderAddress.isEmpty() ? (emailHost.equals("auto") ? autoEmailHost : emailHost) : emailHost;
	}

	public int getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = Integer.valueOf(emailPort);
	}

	public boolean requiresAuthentication() {
		return requiresAuthentication;
	}

	public void setRequiresAuthentication(boolean requiresAuthentication) {
		this.requiresAuthentication = requiresAuthentication;
	}

	public void setRequiresAuthentication(String requiresAuthentication) {
		this.requiresAuthentication = Boolean.valueOf(requiresAuthentication);
	}

	public int getSecurityLayer() {
		return securityLayer;
	}

	public void setSecurityLayer(int securityLayer) {
		this.securityLayer = securityLayer < 3 && securityLayer > -1 ? securityLayer : SecurityTypes.NONE;
	}

	public void setSecurityLayer(String securityLayer) {
		final String lowerSecurityLayer = securityLayer.toLowerCase();

		if (lowerSecurityLayer.equals("tls"))
			setSecurityLayer(SecurityTypes.TLS);
		else if (lowerSecurityLayer.equals("ssl"))
			setSecurityLayer(SecurityTypes.SSL);
		else {
			this.securityLayer = Character.isDigit(securityLayer.charAt(0)) ?
					Integer.valueOf(String.valueOf(securityLayer.charAt(0))) : SecurityTypes.NONE;
		}
	}

	class SecurityTypes {
		public static final int NONE = 0;
		public static final int TLS = 1;
		public static final int SSL = 2;
	}
}
