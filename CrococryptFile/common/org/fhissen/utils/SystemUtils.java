package org.fhissen.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemUtils {
	public static final <X extends Enum<X>>X s2E(Object tmp, Class<X> enumclass) {
		if(tmp == null) return null;
		return s2E(tmp.toString(), enumclass);
	}

	@SuppressWarnings("unchecked")
	public static final <X extends Enum<X>>X s2E(String tmp, Class<X> enumclass) {
		if(tmp == null) return null;
		Enum<X> e = null;
		
		try {
			e = Enum.valueOf(enumclass, tmp);
		} catch (Exception ex) {}
		
		return (X)e;
	}
	
	public static final String e2s(Exception e){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		ps.close();
		return baos.toString();
	}
}
