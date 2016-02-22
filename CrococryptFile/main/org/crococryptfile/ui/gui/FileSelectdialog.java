package org.crococryptfile.ui.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.crococryptfile.ui.resources.ResourceCenter;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.utils.os.OSFolders;
import org.fhissen.utils.ui.SwingHacks_FileSystemView;

public class FileSelectdialog {
	public static class Options{
		public boolean foldersonly = false;
		public boolean multiselect = true;
		public File startFolder;
		public String title = ResourceCenter.TITLE;
		public String buttoncaption = null;
	}

	public static final void select(SimpleCallback<File[]> cb) {
		select(cb, null);
	}

	public static final void select(SimpleCallback<File[]> cb, Options opt) {
		if(opt == null){
			opt = new Options();
			opt.startFolder = OSFolders.getUserChooserstart();
		}

		JFrame jf = new JFrame(ResourceCenter.TITLE);
		jf.setIconImages(ResourceCenter.icons);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setSize(200, 100);
		jf.setLocationRelativeTo(null);
		
		JFileChooser chooser = null;
		
	    try {
		    chooser = new JFileChooser();
		} catch (Exception e) {
		    chooser = new JFileChooser(new SwingHacks_FileSystemView());
		}
		
	    chooser.setDialogTitle(opt.title);
	    
	    if(opt.foldersonly){
		    chooser.setMultiSelectionEnabled(false);
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);
	    }
	    else{
		    chooser.setMultiSelectionEnabled(opt.multiselect);
		    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    chooser.setAcceptAllFileFilterUsed(true);
	    }
	    
	    chooser.setCurrentDirectory(opt.startFolder);
	    if(opt.buttoncaption != null) chooser.setApproveButtonText(opt.buttoncaption);

	    if (chooser.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
	    	File[] files = chooser.getSelectedFiles();
	    	if(files == null) files = new File[0];
	    	if(files.length == 0 && chooser.getSelectedFile() != null)
	    		files = new File[]{chooser.getSelectedFile()};
	    	
	    	cb.callbackValue(FileSelectdialog.class, files);
	    }
	    else{
	    	cb.callbackValue(FileSelectdialog.class, null);
	    }
	    
	    jf.dispose();
	}
}
