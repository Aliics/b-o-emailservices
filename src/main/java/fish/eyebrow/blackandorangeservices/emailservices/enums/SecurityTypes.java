package fish.eyebrow.blackandorangeservices.emailservices.enums;

public enum SecurityTypes {
	NONE(0, "none"),
	TLS(1, "tls"),
	SSL(2, "ssl");

	private final int number;
	private final String string;

	SecurityTypes(int number, String string) {
		this.number = number;
		this.string = string;
	}

	public int getNumber() {
		return number;
	}

	public String getString() {
		return string;
	}
}
