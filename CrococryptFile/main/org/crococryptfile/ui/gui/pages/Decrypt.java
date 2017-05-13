package org.crococryptfile.ui.gui.pages;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.crococryptfile.suites.SUITES;
import org.crococryptfile.ui.CbIDecrypt;
import org.crococryptfile.ui.UICenter;
import org.crococryptfile.ui.gui.FileSelectdialog;
import org.crococryptfile.ui.gui.Page;
import org.crococryptfile.ui.gui.PageActionparameters;
import org.crococryptfile.ui.gui.PageLauncher;
import org.crococryptfile.ui.gui.SimpleDialogs;
import org.crococryptfile.ui.gui.PageLauncher.Options;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.SystemUtils;
import org.fhissen.utils.os.OSFolders;

public class Decrypt extends Page{
	private enum OPT{
		choose,
		decrypt,
		
		infobutton,
		
		
		folder,
	}
	
	
	private CbIDecrypt cb;
	private File destFolder;
	private PageLauncher pl = null;

	public Decrypt(CbIDecrypt cb){
		this.cb = cb;
	}
	
	public void setFolder(File f){
		this.destFolder = f;
	}

	@Override
	protected void generate(HashMap<String, String> params){
		final String listbase = "<option SELECTED value=\"CODE\">DESCR</option>";
		SUITES[] suites = SUITES.values();
		StringBuilder sbmenu = new StringBuilder();
		for(int i=0; i<suites.length; i++){
			SUITES suite = suites[i];
			String tmp = listbase;
			tmp = tmp.replace("SELECTED", "");
			tmp = tmp.replace("CODE", suite.name());
			tmp = tmp.replace("DESCR", SUITES.descriptor.get(i));
			sbmenu.append(tmp);
		}

		params.put("providerlist", sbmenu.toString());
		params.put("title", _T.DecryptWindow_title.val());
		params.put("text", _T.DecryptWindow_text.val());
		params.put("destination", _T.DecryptWindow_destination.val());
		
		String defaultFile = null;
		if(destFolder != null) defaultFile = destFolder.getAbsolutePath();
		if(defaultFile == null) defaultFile = OSFolders.getUserChooserstart().getAbsolutePath();
		params.put("startfile", _quoteString(defaultFile)); 
	}
	
	@Override
	protected Object extractAction(String tmp) {
		return SystemUtils.s2E(super.extractAction(tmp), OPT.class);
	}

	@Override
	protected Object extractParam(String tmp) {
		return SystemUtils.s2E(super.extractParam(tmp), OPT.class);
	}

	@Override
	protected void action(PageLauncher pl, Object action, PageActionparameters params) {
		OPT opt = (OPT) action;
		if(opt == null) opt = OPT.decrypt;
		
		switch (opt) {
		case infobutton:
			PageLauncher.launch(new Options(this), new InfoWindow());
			return;

		case choose:
			if(cb.isDecryptRunning(this)) return;

			this.pl = pl;
			choose();
			return;
			
		case decrypt:
			if(cb.isDecryptRunning(this)) return;

			String fstring = params.getString(OPT.folder);
			if(fstring == null) return;
			File tmpdst = new File(fstring);
			File sanidst = sanitize(tmpdst);
			
			if(sanidst == null){
				UICenter.message(_T.DecryptWindow_invalid);
				return;
			}

			if(!tmpdst.equals(sanidst) || !fstring.equals(sanidst.getAbsolutePath())){
				setFolder(sanidst);
				pl.refresh();
			}
			
			if(sanidst.exists()){
				if(sanidst.isDirectory() && FileUtils.noOfFiles(sanidst.toPath()) > 0){
					int retval = JOptionPane.showConfirmDialog(pl.getWindow(), 
			                _T.DecryptWindow_already, ResourceCenter.TITLE,
			                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if(retval != JOptionPane.YES_OPTION) return;
				}
				else if(sanidst.isFile()){
					SimpleDialogs.message(_T.DecryptWindow_folderisfile);
					return;
				}
			}

			cb.callbackDecrypt(this, sanidst);
			return;
		}
		
		UICenter.message("Unknown Operation");
	}
	
	private final void choose(){
		FileSelectdialog.Options opt = new FileSelectdialog.Options();
		opt.multiselect = false;
		opt.foldersonly = true;
		opt.startFolder = destFolder.getParentFile();
		while(opt.startFolder != null && !opt.startFolder.exists())
			opt.startFolder = opt.startFolder.getParentFile();
		
		FileSelectdialog.select(new SimpleCallback<File[]>() {
			@Override
			public void callbackValue(Object source, File[] ret) {
				if(ret == null || ret.length == 0) return;
				
				File tmp = sanitize(ret[0]);
				if(tmp == null) return;
				
				setFolder(tmp);
				if(pl != null) pl.refresh();
			}
		}, opt);
	}
	
	private File sanitize(File dest){
		File tmp = null;
		try {
			tmp = dest.getCanonicalFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}
}
