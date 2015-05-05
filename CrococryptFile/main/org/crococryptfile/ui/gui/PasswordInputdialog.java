package org.crococryptfile.ui.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;

public class PasswordInputdialog {
	private static final String DUMMYTEXT = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	

	private Window mainwindow;
	private Window thiswindow;
	
	public PasswordInputdialog(){
	}

	public PasswordInputdialog(Window mainwindow){
		this.mainwindow = mainwindow;
	}

	public PasswordInputdialog(Object page){
		if(page instanceof Page)
			mainwindow = ((Page)page).getPageLauncher().getWindow();
	}
	
	public void main(final SimpleCallback<char[]> sr) {
		if(mainwindow == null){
			JFrame jf = new JFrame();
			jf.setTitle(_T.PasswordDecrypt_title.val());
			jf.setResizable(false);
	    	jf.setIconImages(ResourceCenter.icons);
			jf.getContentPane().setLayout(new FlowLayout()); 
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			thiswindow = jf;
		}
		else{
			JDialog jd = new JDialog(mainwindow);
			jd.setModal(true);
			jd.setTitle(_T.PasswordDecrypt_title.val());
			jd.setResizable(false);
			jd.setIconImages(ResourceCenter.icons);
			jd.getContentPane().setLayout(new FlowLayout()); 
			jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			thiswindow = jd;
		}
		
		final JPasswordField tf = new JPasswordField();
		tf.setPreferredSize(new Dimension(200, 25));
		JButton b = new JButton("OK");
		
		ActionListener act = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				char[] pw = tf.getPassword();
				
				if(pw == null || pw.length == 0) return;
				
				tf.setText(DUMMYTEXT);
				tf.setText("");

				thiswindow.dispose();
				sr.callbackValue(PasswordInputdialog.this, pw);
			}
		};
		

		b.addActionListener(act);
		tf.addActionListener(act);
		
		thiswindow.add(tf);
		thiswindow.add(b);
		
		thiswindow.pack();
		
		thiswindow.setLocationRelativeTo(mainwindow);
		thiswindow.setVisible(true);
		

		if(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)){
			JOptionPane.showMessageDialog(thiswindow, _T.Password_caps);
		}
	}
}
