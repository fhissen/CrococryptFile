package org.crococryptfile.ui.cui;

import java.io.Console;

import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;

public class PasswordInputConsole {
	public void main(SimpleCallback<char[]> cb) {
		Console con = System.console();
		
		if(con == null){
			System.err.println("Your system does not support an interactive console for this operation");
			return;
		}
		
		CPrint.print(_T.PasswordDecrypt_title + ": ");
		char[] pass = con.readPassword();
		
		if(pass == null || pass.length == 0){
			cb.callbackValue(this, null);
			return;
		}
		
		cb.callbackValue(this, pass);
	}
}
