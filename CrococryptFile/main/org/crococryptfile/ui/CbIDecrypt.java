package org.crococryptfile.ui;

import java.io.File;

public interface CbIDecrypt{
	public void callbackDecrypt(Object source, File destinationFolder);
	public boolean isDecryptRunning(Object source);
}
