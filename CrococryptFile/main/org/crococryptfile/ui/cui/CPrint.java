package org.crococryptfile.ui.cui;


public class CPrint {
	public static final void print(Object o){
		System.out.print(o);
	}
	
	public static final void line(Object o){
		System.out.println(o);
	}

	public static final void linefeed(){
		System.out.println();
	}

	public static final void error(Object o){
		System.err.println(o);
	}
}
