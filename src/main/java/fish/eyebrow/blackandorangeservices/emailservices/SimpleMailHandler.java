package fish.eyebrow.blackandorangeservices.emailservices;

import fish.eyebrow.blackandorangeservices.emailservices.enums.SecurityTypes;
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
	private SecurityTypes securityLayer;

	private boolean credentialsSet = false;

	public void sendEmail(InternetAddress[] recipients, String subject, String text)
			throws MessagingException, CredentialNotSetException {
		if (!credentialAreSet())
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
		mimeMessage.setContent(text, "text/html; charset=UTF-8");

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
		final String[] setProperties = {
				(String) properties.get("senderAddress"),
				(String) properties.get("senderPassword"),
				(String) properties.get("emailHost"),
				(String) properties.get("emailPort"),
				(String) properties.get("requiresAuthentication"),
				(String) properties.get("securityLayer")
		};

		for (String property : setProperties) {
			if (property == null) {
				credentialsSet = false;
				throw new MissingPropertyException();
			}
		}

		setSenderAddress(setProperties[0]);
		setSenderPassword(setProperties[1]);
		setEmailHost(setProperties[2]);
		setEmailPort(setProperties[3]);
		setRequiresAuthentication(setProperties[4]);
		setSecurityLayer(setProperties[5]);

		credentialsSet = true;
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

	public SecurityTypes getSecurityLayer() {
		return securityLayer;
	}

	public void setSecurityLayer(SecurityTypes securityLayer) {
		this.securityLayer = securityLayer;
	}

	public void setSecurityLayer(String securityLayer) {
		final String lowerSecurityLayer = securityLayer.toLowerCase();

		if (lowerSecurityLayer.equals("tls"))
			setSecurityLayer(SecurityTypes.TLS);
		else if (lowerSecurityLayer.equals("ssl"))
			setSecurityLayer(SecurityTypes.SSL);
		else {
			final boolean inputIsNumber = Character.isDigit(securityLayer.charAt(0));
			final int inputAsNumber = Integer.valueOf(String.valueOf(securityLayer.charAt(0)));

			if (inputIsNumber)
				this.securityLayer = inputAsNumber != SecurityTypes.NONE.getNumber() ?
						(inputAsNumber == SecurityTypes.TLS.getNumber() ? SecurityTypes.TLS :
								inputAsNumber == SecurityTypes.SSL.getNumber() ? SecurityTypes.SSL :
										SecurityTypes.NONE) :
						SecurityTypes.NONE;
			else
				this.securityLayer = SecurityTypes.NONE;
		}
	}

	public boolean credentialAreSet() {
		return credentialsSet;
	}
}
