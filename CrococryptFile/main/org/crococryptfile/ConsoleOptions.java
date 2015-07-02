package org.crococryptfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.crococryptfile.suites.SUITES;
import org.crococryptfile.ui.cui.CPrint;


public class ConsoleOptions {
	public enum ConsoleOptions_Params{
		c,
		d,
		e,
		t,
		prov,
		cred,
		cloak,
		verbose,
	}
	
	public static final HashMap<ConsoleOptions_Params, String> description = new HashMap<>();
	static{
		description.put(ConsoleOptions_Params.c, "\t\tForce console interaction and output");
		description.put(ConsoleOptions_Params.d, "\t\tDecrypt mode");
		description.put(ConsoleOptions_Params.e, "\t\tEncrypt mode");
		description.put(ConsoleOptions_Params.t, " <DIR|FILE>\tTarget (directory for decrypt or file for encrypt)");
		description.put(ConsoleOptions_Params.prov, " <STRING>\tCrococryptFile's crypto suite to use, e.g.: " + SUITES.providerListAsLine());
		description.put(ConsoleOptions_Params.cred, " <STRING>\tCollection of credentials (NOT RECOMMENDED due to security risks\n\t\t\tthrough providing passwords etc. via a console!)");
		description.put(ConsoleOptions_Params.cloak, "\tTry to decrypt a cloaked file");
		description.put(ConsoleOptions_Params.verbose, "\t\tDetailed output");
	}
	
	public static CrococryptParameters parse(String[] args) {
		if(args == null || args.length == 0) return null;
		
		CrococryptParameters opt = new CrococryptParameters();
		
		File parent = null;
		for(int i=0; i<args.length; i++){
			String s = args[i];
			if(s == null) continue;
			s = s.trim();

			if(checkParam(s, ConsoleOptions_Params.c)){
				opt.forceconsole = true;
			}
			else if(checkParam(s, ConsoleOptions_Params.d)){
				opt.decmode = true;
			}
			else if(checkParam(s, ConsoleOptions_Params.e)){
				opt.decmode = false;
			}
			else if(checkParam(s, ConsoleOptions_Params.t)){
				String tmp = next(args, i);
				if(tmp != null){
					try {
						opt.destination = new File(tmp).getCanonicalFile();
						i++;
					} catch (Exception e) {}
				}
			}
			else if(checkParam(s, ConsoleOptions_Params.prov)){
				String tmp = next(args, i);
				if(tmp == null) continue;
				
				i++;
				SUITES suite = null;
				try {
					suite = SUITES.valueOf(tmp);
				} catch (Exception e) {}
				if(suite == null){
					try {
						suite = SUITES.valueOf(tmp.toUpperCase());
					} catch (Exception e) {}
				}
				
				if(suite == null){
					CPrint.print("Specified provider unknown, valid providers are: ");
					CPrint.print(SUITES.providerListAsLine());
					CPrint.linefeed();
				}
				else{
					opt.suite = suite;
				}
			}
			else if(checkParam(s, ConsoleOptions_Params.cred)){
				String tmp = next(args, i);
				if(tmp != null){
					if(opt.rawcreds == null) opt.rawcreds = new ArrayList<>();
					opt.rawcreds.add(tmp);
					i++;
				}
			}
			else if(checkParam(s, ConsoleOptions_Params.verbose)){
				opt.verboseconsole = true;
			}
			else if(checkParam(s, ConsoleOptions_Params.cloak)){
				opt.cloakedfile = true;
				opt.decmode = true;
			}
			else{
				File tmp = null;
				try {
					tmp = new File(s).getCanonicalFile();
				} catch (Exception e) {}

				if(tmp != null && tmp.exists()){
					if(!opt.filesanddirs.contains(tmp))
						opt.filesanddirs.add(tmp);
					else
						continue;
					
					if(tmp.isFile())
						opt.filecount++;
					else
						opt.dircount++;
					
					if(parent == null){
						parent = tmp.getParentFile();
						if(parent == null) parent = tmp;
					}
					else{
						if(opt.singlesource){
							if(!parent.equals(tmp.getParentFile())) opt.singlesource = false;
						}
					}
				}
				else{
					CPrint.line(s + " does not exist");
				}
			}
		}
		
		return opt;
	}
	
	private static final boolean checkParam(String tobechecked, ConsoleOptions_Params checkfor){
		if(tobechecked.equalsIgnoreCase("/" + checkfor.name()) || tobechecked.equalsIgnoreCase("-" + checkfor.name())) return true;
		return false;
	}
	
	private static final String next(String args[], int cur){
		cur++;
		if(args.length <= cur) return null;
		if(args[cur] == null || args[cur].length() == 0) return null;
		return args[cur];
	}
}
