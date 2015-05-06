package org.crococryptfile.ui;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.crococryptfile.ui.cui.CPrint;
import org.crococryptfile.ui.gui.SimpleDialogs;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.utils.os.OSDetector;


public class UICenter {
	private static boolean CONSOLE = false;
	private static boolean INITIALIZED = false;
	
	static{
		INIT();
	}

	public static final void INIT(){
		if(INITIALIZED) return;
		INITIALIZED = true;
		
		try {
			boolean headless = false;
			try {
				headless = GraphicsEnvironment.isHeadless();
			} catch (Throwable t) {
				headless = true;
				System.err.println(t.getLocalizedMessage());
			}
			
			if(headless){
				CONSOLE = true;
			}
			else{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				JOptionPane.setDefaultLocale(ResourceCenter.getLocale());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(OSDetector.isMac()){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", ResourceCenter.TITLE);
		}
	}
	
	public static final boolean isGUI(){
		return CONSOLE == false;
	}

	public static final boolean isCUI(){
		return CONSOLE;
	}
	
	public static final void setCUI(){
		CONSOLE = true;
	}
	
	public static final void setGUI(){
		if(GraphicsEnvironment.isHeadless()) return;
		CONSOLE = false;
	}
	
	
	public static final void message(String s){
		if(isGUI())
			SimpleDialogs.message(s);
		else
			CPrint.line(s);
	}
	
	public static final void message(_T text){
		message(text.val());
	}
}
