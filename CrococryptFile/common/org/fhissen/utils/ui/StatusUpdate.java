package org.fhissen.utils.ui;

public interface StatusUpdate {
	public void start();
	public void finished();
	public boolean isActive();
	public void receiveMessageSummary(String msg);
	public void receiveMessageDetails(String msg);
	public void receiveProgress(int perc);
	public void receiveDetailsProgress(int perc);
}
