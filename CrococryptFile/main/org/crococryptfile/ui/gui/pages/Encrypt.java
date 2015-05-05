package org.crococryptfile.ui.gui.pages;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.crococryptfile.CrococryptParameters;
import org.crococryptfile.Settings;
import org.crococryptfile.suites.BasicFileinfo;
import org.crococryptfile.suites.SUITES;
import org.crococryptfile.ui.CbIEncrypt;
import org.crococryptfile.ui.UICenter;
import org.crococryptfile.ui.gui.FileSelectdialog;
import org.crococryptfile.ui.gui.Page;
import org.crococryptfile.ui.gui.PageLauncher;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.utils.FileUtils;
import org.fhissen.utils.SystemUtils;
import org.fhissen.utils.os.OSFolders;

public class Encrypt extends Page{
	private enum OPT{
		choose,
		encrypt,
		
		
		provider,
		file,
	}
	
	private CrococryptParameters co;
	private File destfile;
	private CbIEncrypt cb;
	private PageLauncher pl = null;
	
	public Encrypt(CbIEncrypt cb, CrococryptParameters co, File destfile){
		this.cb = cb;
		this.co = co;
		setFile(destfile);
	}
	
	public void setFile(File destfile){
		this.destfile = destfile;
	}
	
	@Override
	protected void generate(HashMap<String, String> params){
		String lastprov = ResourceCenter.getSettings().get(Settings.lastprovider);
		if(co.suite != null) lastprov = co.suite.name();
		
		final String listbase = "<option SELECTED value=\"CODE\">DESCR</option>";
		SUITES[] suites = SUITES.values();
		StringBuilder sbmenu = new StringBuilder();
		for(int i=0; i<suites.length; i++){
			SUITES suite = suites[i];
			if(!suite.isAvailable()) continue;
			String tmp = listbase;
			if(!suite.name().equals(lastprov)) tmp = tmp.replace("SELECTED", "");
			tmp = tmp.replace("CODE", suite.name());
			tmp = tmp.replace("DESCR", SUITES.descriptor.get(i));
			sbmenu.append(tmp);
		}
		
		params.put("providerlist", sbmenu.toString());
		params.put("title", _T.EncryptWindow_title.val());
		params.put("destination", _T.EncryptWindow_destination.val());
		
		String text = null;
		File tmp = co.filesanddirs.get(0);
		if(!OSFolders.isRoot(tmp)) tmp = tmp.getParentFile();
		String s_source = FileUtils.shortenDisplayFile(tmp).replace(" ", "&nbsp;");
		if(!co.singlesource)
			s_source = _T.EncryptWindow_multisources.val();
		
		if(co.dircount == 0){
			if(co.filecount == 1)
				text = _T.EncryptWindow_Singlefile.msg(co.filesanddirs.get(0).getName(), s_source);
			else
				text = _T.EncryptWindow_Multifile.msg(co.filecount, s_source);
		}
		else if(co.filecount == 0){
			if(co.dircount == 1)
				text = _T.EncryptWindow_Singledir.msg(co.filesanddirs.get(0).getName(), s_source);
			else
				text = _T.EncryptWindow_Multidir.msg(co.dircount, s_source);
		}
		else{
			if(co.filecount == 1 && co.dircount == 1)
				text = _T.EncryptWindow_SinglefileSingledir.msg(s_source);
			else if(co.filecount > 1 && co.dircount > 1)
				text = _T.EncryptWindow_MultifileMultidir.msg(co.filecount, co.dircount, s_source);
			else if(co.filecount > 1)
				text = _T.EncryptWindow_MultifileSingledir.msg(co.filecount, s_source);
			else if(co.dircount > 1)
				text = _T.EncryptWindow_SinglefileMultidir.msg(co.dircount, s_source);
		}

		params.put("text", text);

		String defaultFile = null;
		if(destfile != null) defaultFile = destfile.getAbsolutePath();
		if(defaultFile == null) defaultFile = new File(OSFolders.getUserChooserstart(), _T.EncryptWindow_EncryptedFile + BasicFileinfo.FILEEXTENSIONwDOT).getAbsolutePath();
		params.put("startfile", _quoteString(defaultFile)); 
	}
	
	@Override
	public Object extractAction(String tmp) {
		return SystemUtils.s2E(super.extractAction(tmp), OPT.class);
	}

	@Override
	public Object extractParam(String tmp) {
		return SystemUtils.s2E(super.extractParam(tmp), OPT.class);
	}
	
	@Override
	protected void action(PageLauncher pl, Object action, HashMap<Object, String> params) {
		OPT opt = (OPT) action;
		if(opt == null) opt = OPT.encrypt;
		
		switch (opt) {
		case choose:
			this.pl = pl;
			choose();
			return;

		case encrypt:
			String fstring = params.get(OPT.file);
			if(fstring == null) return;
			File tmpdst = new File(fstring);
			File sanidst = sanitize(tmpdst);
			
			if(sanidst == null){
				UICenter.message(_T.FilepathFailure);
				return;
			}

			SUITES suitex = SystemUtils.s2E(params.get(OPT.provider), SUITES.class);
			if(suitex != null && !suitex.name().equals(ResourceCenter.getSettings().get(Settings.lastprovider))){
				ResourceCenter.getSettings().set(Settings.lastprovider, suitex.name());
				ResourceCenter.getSettings().save();
			}

			if(!tmpdst.equals(sanidst) || !fstring.equals(sanidst.getAbsolutePath())){
				setFile(sanidst);
				pl.refresh();
			}
			
			if(sanidst.exists()){
				int retval = JOptionPane.showConfirmDialog(pl.getWindow(), 
		                _T.EncryptWindow_already, ResourceCenter.TITLE,
		                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if(retval != JOptionPane.YES_OPTION) return;
			}
			else if(!sanidst.getParentFile().exists()){
				int retval = JOptionPane.showConfirmDialog(pl.getWindow(), 
		                _T.EncryptWindow_createdir, ResourceCenter.TITLE,
		                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if(retval != JOptionPane.YES_OPTION) return;
			}
			
			switch (suitex) {
			case PBE1_AES:
				cb.callbackEncrypt(this, sanidst, suitex);
				return;
			case CAPI_RSAAES:
				cb.callbackEncrypt(this, sanidst, suitex);
				return;
			}
		}

		UICenter.message("Unknown Operation");
	}
	
	private final void choose(){
		FileSelectdialog.Options opt = new FileSelectdialog.Options();
		opt.multiselect = false;
		opt.startFolder = destfile.getParentFile();
		while(opt.startFolder != null && !opt.startFolder.exists())
			opt.startFolder = opt.startFolder.getParentFile();
		
		FileSelectdialog.select(new SimpleCallback<File[]>() {
			@Override
			public void callbackValue(Object source, File[] ret) {
				if(ret == null || ret.length == 0) return;
				
				File tmp = sanitize(ret[0]);
				if(tmp == null) return;

				setFile(tmp);
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
		if(tmp == null) return null;
		
		if(tmp.exists() && tmp.isDirectory()){
			tmp = new File(tmp, _T.EncryptWindow_EncryptedFile + BasicFileinfo.FILEEXTENSIONwDOT);
		}
		else if(!tmp.getName().endsWith(BasicFileinfo.FILEEXTENSIONwDOT)){
			tmp = new File(tmp.getParentFile(), tmp.getName() + BasicFileinfo.FILEEXTENSIONwDOT);
		}
		
		return tmp;
	}
}
