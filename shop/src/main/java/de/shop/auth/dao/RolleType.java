package de.shop.auth.dao;

public enum RolleType {
	ADMIN(1),
	MITARBEITER(2),
	ABTEILUNGSLEITER(3),
	KUNDE(4);
	
	private int value;
	
	RolleType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
