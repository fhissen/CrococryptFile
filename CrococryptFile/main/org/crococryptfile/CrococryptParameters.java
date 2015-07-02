package org.crococryptfile;

import java.io.File;
import java.util.ArrayList;

import org.crococryptfile.suites.SUITES;

public class CrococryptParameters {
	public boolean forceconsole = false;
	public boolean decmode = false;
	public boolean cloakedfile = false;
	public ArrayList<File> filesanddirs = new ArrayList<>();
	public int dircount, filecount;
	public boolean singlesource = true;
	public File destination = null;
	public SUITES suite;
	public ArrayList<String> rawcreds = null;
	public boolean verboseconsole = false;
}
