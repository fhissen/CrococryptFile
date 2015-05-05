package org.crococryptfile.ui;

import java.io.File;

import org.crococryptfile.suites.SUITES;

public interface CbIEncrypt{
	public void callbackEncrypt(Object source, File destination, SUITES provider);
}
