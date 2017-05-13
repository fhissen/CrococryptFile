package org.crococryptfile.ui.gui;

import java.io.File;
import java.util.HashMap;

import org.crococryptfile.Settings;
import org.crococryptfile.suites.SuitePARAM;
import org.crococryptfile.suites.pgpaes.PGPUtils;
import org.crococryptfile.suites.pgpaes.PGPUtils.PGPSecretKeyFinder;
import org.crococryptfile.ui.UICenter;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SUCCESS;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.callbacks.SuccessCallback;


public class PGP_PrivatekeyDialog {
	private SimpleCallback<HashMap<SuitePARAM, Object>> cb;
	private HashMap<SuitePARAM, Object> params = new HashMap<>();
	Object source = null;
	
	private PGP_PrivatekeyDialog(Object src, SimpleCallback<HashMap<SuitePARAM, Object>> cb){
		this.cb = cb;
		source = src;
		
		File sec = null;
		String tmp = ResourceCenter.getSettings().get(Settings.lastpgp_privfile);
		if(tmp == null){
			sec = PGPUtils.getPrivRing();
		}
		else{
			File ftmp = new File(tmp);
			if(ftmp.exists() && ftmp.isFile()) sec = ftmp;
			else sec = PGPUtils.getPrivRing();
		}
		
		if(sec != null && sec.length() > 1){
			params.put(SuitePARAM.pgp_privkeyfile, sec.getAbsolutePath());
			new PasswordInputdialog(source).main(cbpass, _T.PGP_privkeypasstitle.val());
		}
		else{
			startSequence();
		}
	}
	
	private SimpleCallback<char[]> cbpass= new SimpleCallback<char[]>() {
		@Override
		public void callbackValue(Object source, char[] ret) {
			params.put(SuitePARAM.password, ret);
			cb.callbackValue(this, params);
		}
	};
	
	private SimpleCallback<File[]> cbfiles = new SimpleCallback<File[]>(){
		@Override public void callbackValue(Object source, File[] ret) {
			receive(ret);
		}
	};
	
	private void receive(File[] ret){
		boolean succ = true;
		
		if(ret == null || ret.length == 0 || !ret[0].isFile() || !ret[0].exists()) succ = false;

		if(succ){
			PGPSecretKeyFinder finder = new PGPSecretKeyFinder(ret[0]);
			if(!finder.validFile()){
				finder.close();
				succ = false;
			}
		}

		if(!succ){
			UICenter.message(_T.PGP_privkey_wrongfile.val());
			return;
		}
		
		String tmppath = ResourceCenter.getSettings().get(Settings.lastpgp_privfile);
		if(tmppath == null || !tmppath.equals(ret[0].getAbsolutePath())){
			ResourceCenter.getSettings().set(Settings.lastpgp_privfile, ret[0].getAbsolutePath());
			ResourceCenter.getSettings().save();
		}
		
		params.put(SuitePARAM.pgp_privkeyfile, ret[0].getAbsolutePath());
		new PasswordInputdialog(source).main(cbpass, _T.PGP_privkeypasstitle.val());
	}
	
	private void startSequence(){
		SimpleDialogs.message(_T.PGP_selprivkey, new SuccessCallback() {
			@Override
			public void callbackValue(Object source, SUCCESS ret) {
				FileSelectdialog.Options opt = new FileSelectdialog.Options();
				opt.buttoncaption = _T.FileSelection_selectbutton.val();
				opt.title = _T.PGP_selprivkeyTitle.val();
				opt.multiselect = false;
				opt.foldersonly = false;
				
				FileSelectdialog.select(cbfiles, opt);
			}
		});
	}
	
	public static final void requestParams(Object source, SimpleCallback<HashMap<SuitePARAM, Object>> cb){
		new PGP_PrivatekeyDialog(source, cb);
	}
}
