package br.ufrj.ppgi.greco.expedit.tese.commons;

import java.text.Normalizer;

public class StringUtils {

	public static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return s;
	}

}
