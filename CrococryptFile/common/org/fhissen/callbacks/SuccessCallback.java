package org.fhissen.callbacks;

public abstract class SuccessCallback implements SimpleCallback<SUCCESS> {
	@Override
	public abstract void callbackValue(Object source, SUCCESS ret);
}
