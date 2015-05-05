package org.fhissen.utils;

import org.apache.commons.codec.binary.Hex;

public class _U {
	public static final void p(Object o){
		System.out.println(o);
	}
	
	public static final void h(byte[] b){
		System.out.println(Hex.encodeHexString(b));
	}
}
