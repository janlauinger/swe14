package de.shop.test.util;

import java.util.Arrays;
import java.util.List;

import de.shop.test.domain.BestellungTest;
import de.shop.test.domain.ProduktTest;

public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus *VERSCHIEDENEN* Packages auflisten:
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	//	BestellungTest.class,
	private List<Class<? extends AbstractTest>> classes = 
			Arrays.asList(AbstractTest.class, ProduktTest.class, BestellungTest.class);
	
	public static /*void*/ Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}