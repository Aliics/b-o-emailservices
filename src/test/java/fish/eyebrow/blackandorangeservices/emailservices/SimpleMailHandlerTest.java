package fish.eyebrow.blackandorangeservices.emailservices;

import fish.eyebrow.blackandorangeservices.emailservices.exceptions.CredentialNotSetException;
import fish.eyebrow.blackandorangeservices.emailservices.exceptions.MissingPropertyException;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class SimpleMailHandlerTest {
	private static final String GENERATE_CREDENTIALS_PATH = "src/test/resources/generate-credentials.properties";

	private static SimpleMailHandler simpleMailHandler;

	@Test
	void sendEmail() throws IOException, MissingPropertyException, MessagingException, CredentialNotSetException {
		simpleMailHandler = new SimpleMailHandler();

		simpleMailHandler.generateCredentials(GENERATE_CREDENTIALS_PATH);

		InternetAddress[] internetAddresses = InternetAddress.parse("Aliics@hotmail.com");
		String subject = "Send Email Test Case 0";
		String text = "Hello, World!";

		simpleMailHandler.sendEmail(internetAddresses, subject, text);
	}

	@Test
	void sendEmail1() throws IOException, MissingPropertyException, CredentialNotSetException, MessagingException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(GENERATE_CREDENTIALS_PATH));

		simpleMailHandler = new SimpleMailHandler();
		simpleMailHandler.generateCredentials(properties);

		String[] recipients = {
				"Aliics@hotmail.com",
				"larissaruecker7@gmail.com"
		};
		String subject = "Send Email Test Case 1";
		String text = "Hello, World!";

		simpleMailHandler.sendEmail(recipients, subject, text);
	}

	@Test
	void generateCredentials() throws MissingPropertyException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(GENERATE_CREDENTIALS_PATH));

		simpleMailHandler = new SimpleMailHandler();

		simpleMailHandler.generateCredentials(properties);
	}

	@Test
	void generateCredentials1() throws IOException, MissingPropertyException {
		simpleMailHandler = new SimpleMailHandler();

		simpleMailHandler.generateCredentials(GENERATE_CREDENTIALS_PATH);
	}
}