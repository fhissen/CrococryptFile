package org.crococryptfile.ui.gui.pages;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.crococryptfile.Settings;
import org.crococryptfile.suites.SuitePARAM;
import org.crococryptfile.suites.pgpaes.PGPPublickeyList;
import org.crococryptfile.suites.pgpaes.PGPPublickeyList.PGPPublickeyListItem;
import org.crococryptfile.suites.pgpaes.PGPUtils;
import org.crococryptfile.ui.gui.FileSelectdialog;
import org.crococryptfile.ui.gui.Page;
import org.crococryptfile.ui.gui.PageActionparameters;
import org.crococryptfile.ui.gui.PageLauncher;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.os.OSDetector;


public class PGP_Keylist extends Page{
	private final String alias = "alias";
	
	private PageLauncher pl = null;
	private HashMap<SuitePARAM, Object> suiteParams = new HashMap<>();
	private SimpleCallback<HashMap<SuitePARAM, Object>> cb;	
	
	private File pubkeyfile = null;
	{
		String tmp = ResourceCenter.getSettings().get(Settings.lastpgp_pubfile);
		if(tmp == null){
			pubkeyfile = PGPUtils.getPubRing();
		}
		else{
			File ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isFile()) pubkeyfile = ftmp;
		}
	}

	public PGP_Keylist(SimpleCallback<HashMap<SuitePARAM, Object>> cb){
		this.cb = cb;
	}

	@Override
	protected void action(PageLauncher pl, Object action, PageActionparameters params) {
		this.pl = pl;
		if(action == null) return;
		String act = action.toString();
		
		if("ok".equals(act)){
			if(params == null || !params.exists(alias)) return;
			
			ArrayList<Long> kids = new ArrayList<>();
			if(params.isList(alias)){
				@SuppressWarnings("unchecked")
				ArrayList<String> arr = (ArrayList<String>) params.getRaw(alias);
				for(String s: arr)
					kids.add(Long.parseLong(s));
			}
			else{
				kids.add(Long.parseLong(params.getString(alias)));
			}
			
			suiteParams.put(SuitePARAM.pgp_pubkeyfile, pubkeyfile.getAbsolutePath());
			suiteParams.put(SuitePARAM.pgp_keyidlist, kids);
			
			cb.callbackValue(this, suiteParams);
			pl.exit();
		}
		else if("choose".equals(act)){
			choose();
		}
	}

	@Override
	protected void generate(HashMap<String, String> uiparams) {
		uiparams.put("title", _T.EncryptListChooseMulti.val());
		
		if(pubkeyfile == null || !pubkeyfile.exists() || !pubkeyfile.isFile())
			uiparams.put("keyfile", _T.PGP_nokeyfile.val());
		else
			uiparams.put("keyfile", FileUtils.shortenDisplayFile(pubkeyfile));
		
		try {
			if(pubkeyfile != null) PGPPublickeyList.main(pubkeyfile);
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		
		String tmppath = ResourceCenter.getSettings().get(Settings.lastpgp_pubfile);
		if(pubkeyfile != null && (tmppath == null || !tmppath.equals(pubkeyfile.getAbsolutePath()))){
			ResourceCenter.getSettings().set(Settings.lastpgp_pubfile, pubkeyfile.getAbsolutePath());
			ResourceCenter.getSettings().save();
		}
		
		StringBuilder sb = new StringBuilder();
		final String listbase = "<option value=\"CODE\">DESCR</option>";
		
		for(int i=0; i<PGPPublickeyList.items.size(); i++){
			PGPPublickeyListItem item = PGPPublickeyList.items.get(i);
			
			String tmp = listbase;
			tmp = tmp.replace("CODE", "" + item.id);
			tmp = tmp.replace("DESCR", _quoteStringFull(item.show));
			sb.append(tmp);
		}
		
		uiparams.put("keylist", sb.toString());
	}
	
	private final void choose(){
		FileSelectdialog.Options opt = new FileSelectdialog.Options();
		opt.multiselect = false;
		opt.foldersonly = false;
		opt.title = _T.PGP_selpubkey.val();
		
		FileSelectdialog.select(new SimpleCallback<File[]>() {
			@Override
			public void callbackValue(Object source, File[] ret) {
				if(ret == null || ret.length == 0) return;
				
				File tmp = sanitize(ret[0]);
				if(tmp == null) return;
				
				pubkeyfile = tmp;
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
	
	
	@Override
	public Dimension getSize() {
		Dimension dim = super.getSize();
		
		if(OSDetector.isLin())
			dim.height += 150;
		
		return dim;
	}
}
