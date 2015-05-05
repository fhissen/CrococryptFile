package org.crococryptfile.ui.cui;

import java.io.Console;
import java.util.Arrays;

import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoUtils;

public class PasswordCreateConsole {
	public void main(SimpleCallback<char[]> cb) {
		Console con = System.console();
		
		if(con == null){
			System.err.println("Your system does not support an interactive console for this operation");
			return;
		}
		
		CPrint.print(_T.PasswordEncrypt_title + ": ");
		char[] pass = con.readPassword();
		CPrint.print(_T.PasswordEncrypt_retype + ": ");
		char[] pass2 = con.readPassword();
		
		if(pass == null || pass2 == null || pass.length == 0 || pass2.length == 0){
			cb.callbackValue(this, null);
			return;
		}
		
		int minlen = CryptoCodes.STANDARD_PBKDF2_PWLEN;

		if(Arrays.equals(pass, pass2) && pass.length >= minlen){
			CryptoUtils.kill(pass2);
			cb.callbackValue(this, pass);
		}
		else{
			CryptoUtils.kill(pass);
			CryptoUtils.kill(pass2);
			
			if(pass.length < minlen)
				CPrint.line(_T.PasswordEncrypt_len.msg(minlen));
			else
				CPrint.line(_T.PasswordEncrypt_nomatch);
			
			main(cb);
		}
	}
}
