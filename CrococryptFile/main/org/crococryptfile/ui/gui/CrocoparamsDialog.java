package org.crococryptfile.ui.gui;

import java.io.File;
import java.util.Collections;

import org.crococryptfile.CrococryptParameters;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;

public class CrocoparamsDialog{
	private SimpleCallback<CrococryptParameters> cb;
	private CrococryptParameters params = new CrococryptParameters();
	
	private CrocoparamsDialog(SimpleCallback<CrococryptParameters> cb){
		this.cb = cb;
		
		startSequence();
	}
	
	private SimpleCallback<File[]> cbfiles = new SimpleCallback<File[]>(){
		@Override public void callbackValue(Object source, File[] ret) {
			receive(ret);
		}
	};
	
	private void receive(File[] ret){
		if(ret == null || ret.length == 0) return;

		Collections.addAll(params.filesanddirs, ret);

		for(File f: ret){
			if(f.isFile())
				params.filecount++;
			else
				params.dircount++;
		}
		
		cb.callbackValue(this, params);
	}
	
	private void startSequence(){
		FileSelectdialog.Options opt = new FileSelectdialog.Options();
		opt.buttoncaption = _T.FileSelection_selectbutton.val();
		opt.title = _T.FileSelection_title.val();
		opt.multiselect = true;
		opt.foldersonly = false;
		
		FileSelectdialog.select(cbfiles, opt);
	}

	public static final void requestParams(SimpleCallback<CrococryptParameters> cb){
		new CrocoparamsDialog(cb);
	}
}
