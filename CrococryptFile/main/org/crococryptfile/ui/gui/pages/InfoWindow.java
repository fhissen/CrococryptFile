package org.crococryptfile.ui.gui.pages;

import java.awt.Dimension;
import java.util.HashMap;

import org.crococryptfile.ui.gui.Page;
import org.crococryptfile.ui.gui.PageActionparameters;
import org.crococryptfile.ui.gui.PageLauncher;

public class InfoWindow extends Page{
	@Override
	protected void generate(HashMap<String, String> params){
	}
	
	@Override
	protected void action(PageLauncher pl, Object action, PageActionparameters params) {
		if(params == null || action == null || !action.equals("ok")) return;
		
		pl.exit();
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension(400, 275);
	}
}
