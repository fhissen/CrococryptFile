package org.crococryptfile.ui.gui;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SUCCESS;
import org.fhissen.callbacks.SuccessCallback;

public class SimpleDialogs {
	public static final void message(_T text, SuccessCallback cb){
		message(text.val(), cb);
	}

	public static final void message(_T text){
		message(text.val(), null);
	}

	public static final void message(final String text, final SuccessCallback cb){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
				    JOptionPane jop = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
					showup(jop);
					if(cb != null) cb.callbackValue(SimpleDialogs.class, SUCCESS.TRUE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static final void message(final String text){
		message(text, null);
	}

	
	public static void questionYesNo(final String text, final SuccessCallback cb){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JOptionPane jop = new JOptionPane(text, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
					showup(jop);

			        if(Integer.valueOf(JOptionPane.YES_OPTION).equals(jop.getValue())){
						cb.callbackValue(SimpleDialogs.class, SUCCESS.TRUE);
						return;
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				cb.callbackValue(SimpleDialogs.class, SUCCESS.FALSE);
			}
		});
	}
	
	private static final void showup(JOptionPane jop){
        JDialog dialog = jop.createDialog(ResourceCenter.TITLE);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setIconImages(ResourceCenter.icons);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.dispose();
	}
}
