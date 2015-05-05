package org.crococryptfile.ui.cui;

import org.fhissen.utils.ui.StatusUpdate;

public class StatusConsole implements StatusUpdate{
	@Override
	public void start() {
		CPrint.line("Starting...");
	}

	@Override
	public void finished() {
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void receiveMessageSummary(String msg) {
		CPrint.linefeed();
		CPrint.line("===" + msg + "===");
	}

	@Override
	public void receiveMessageDetails(String msg) {
		CPrint.linefeed();
		CPrint.line(msg);
	}

	@Override
	public void receiveProgress(int perc) {
		CPrint.linefeed();
		CPrint.line("Overall: " + perc + "%");
	}

	@Override
	public void receiveDetailsProgress(int perc) {
		if(perc <= 0) return;
		if(perc % 100 == 0) CPrint.print(" " + perc + "%");
	}
}
