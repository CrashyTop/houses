package com.czhouses.api;

public class secToMin {

	String time;

	public secToMin(int i) {
		int ds = i / 86400;
		int hs = i % 86400 / 3600;
		int ms = i % 86400 % 3600 / 60;
		int ss = i % 86400 % 3600 % 60;

		String d = (ds < 10 ? "0" : "") + ds;
		String h = (hs < 10 ? "0" : "") + hs;
		String m = (ms < 10 ? "0" : "") + ms;
		String s = (ss < 10 ? "0" : "") + ss;
		String f = null;

		if (d.contains("-")) {
			d = d.replace("-", "");
		}
		if (h.contains("-")) {
			h = h.replace("-", "");
		}
		if (m.contains("-")) {
			m = m.replace("-", "");
		}
		if (s.contains("-")) {
			s = s.replace("-", "");
		}

		f = d + "d " + h + "h " + m + "m " + s + "s";

		if (d.equalsIgnoreCase("00")) {
			f = f.replace(d + "d ", "");
		}
		if (h.equalsIgnoreCase("00")) {
			f = f.replace(h + "h ", "");
		}
		if (m.equalsIgnoreCase("00")) {
			f = f.replace(m + "m ", "");
		}
		if (s.equalsIgnoreCase("00")) {
			f = f.replace(s + "s", "");
		}

		if (f.equalsIgnoreCase("")) {
			f = "0s";
		}

		time = f;
	}

	public String toTime() {
		return time;
	}

	public static String ns(int i) {
		return i > 1 ? "s" : "";
	}

}
