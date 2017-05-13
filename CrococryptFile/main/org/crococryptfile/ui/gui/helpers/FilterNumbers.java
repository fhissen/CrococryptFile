package org.crococryptfile.ui.gui.helpers;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class FilterNumbers extends DocumentFilter {
	private static final int MAXLEN = (""+Integer.MAX_VALUE).length();
	
	public static final boolean validate(String text) {
		if(text == null || text.length() > MAXLEN)
			return false;
		if(text.length() == 0)
			return true;
		if(text.charAt(0) == '-')
			return false;
		try {
			Integer.parseInt(text);
			
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.insert(offset, string);

		if(validate(sb.toString())) {
			super.insertString(fb, offset, string, attr);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		if(validate(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);

		if(validate(sb.toString())) {
			super.remove(fb, offset, length);
		}
	}
}
