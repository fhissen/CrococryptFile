package org.crococryptfile.ui.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import org.crococryptfile.suites.SuitePARAM;
import org.crococryptfile.ui.gui.helpers.FilterNumbers;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.callbacks.SimpleCallback;
import org.fhissen.crypto.CryptoCodes;
import org.fhissen.crypto.CryptoUtils;


public class PasswordCreatedialogWithOptions {
	private static final String DUMMYTEXT = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	

	private Window mainwindow;
	private Window thiswindow;
	
	private HashMap<SuitePARAM, Object> suiteParams = new HashMap<>();

	public PasswordCreatedialogWithOptions(){
	}

	public PasswordCreatedialogWithOptions(Window mainwindow){
		this.mainwindow = mainwindow;
	}
	
	public PasswordCreatedialogWithOptions(Object page){
		if(page instanceof Page)
			mainwindow = ((Page)page).getPageLauncher().getWindow();
	}
	
	public void main(final SimpleCallback<HashMap<SuitePARAM, Object>> sr) {
		if(mainwindow == null){
			JFrame jf = new JFrame();
			jf.setTitle(_T.PasswordEncrypt_title.val());
			jf.setResizable(false);
	    	jf.setIconImages(ResourceCenter.icons);
			jf.getContentPane().setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS)); 
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			thiswindow = jf;
		}
		else{
			JDialog jd = new JDialog(mainwindow);
			jd.setModal(true);
			jd.setTitle(_T.PasswordEncrypt_title.val());
			jd.setResizable(false);
			jd.setIconImages(ResourceCenter.icons);
			jd.getContentPane().setLayout(new BoxLayout(jd.getContentPane(), BoxLayout.Y_AXIS)); 
			jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			thiswindow = jd;
		}
		
		JPanel line1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel lineok = new JPanel(new GridBagLayout());
		JPanel line3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JPanel lineopt = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		final JPasswordField tf = new JPasswordField();
		tf.setPreferredSize(new Dimension(200, 25));
		
		final JPasswordField tf2 = new JPasswordField();
		tf2.setPreferredSize(new Dimension(200, 25));
		
		final JTextField jtf = new JTextField(""+CryptoCodes.STANDARD_PBKDF2_ITERATIONS); 
		
		JButton b = new JButton("OK");
		
		ActionListener act = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				char[] s1 = tf.getPassword();
				char[] s2 = tf2.getPassword();
				
				if(s1 == null || s2 == null || s1.length == 0 || s2.length == 0)
					return;
				
				int minlen = CryptoCodes.STANDARD_PBKDF2_PWLEN;

				tf.setText(DUMMYTEXT);
				tf2.setText(DUMMYTEXT);
				
				tf.setText("");
				tf2.setText("");
				
				if(Arrays.equals(s1, s2) && s1.length >= minlen){
					thiswindow.dispose();
					CryptoUtils.kill(s2);


					suiteParams.put(SuitePARAM.password, s1);
					if(lineopt.isVisible()){
						String tmp = jtf.getText();
						if(FilterNumbers.validate(tmp)) suiteParams.put(SuitePARAM.itcount, tmp);
					}
					sr.callbackValue(this, suiteParams);
				}
				else{
					CryptoUtils.kill(s1);
					CryptoUtils.kill(s2);
					tf.requestFocus();
					
					if(s1.length < minlen)
						JOptionPane.showMessageDialog(thiswindow, _T.PasswordEncrypt_len.msg(minlen));
					else
						JOptionPane.showMessageDialog(thiswindow, _T.PasswordEncrypt_nomatch);
				}
			}
		};
		
		b.addActionListener(act);
		tf.addActionListener(act);
		tf2.addActionListener(act);
		jtf.addActionListener(act);
		
		JLabel jl = new JLabel(_T.PasswordEncrypt_label + ": ");
		line1.add(jl);
		line1.add(tf);
		thiswindow.add(line1);

		jl = new JLabel(_T.PasswordEncrypt_retype + ": ");
		line2.add(jl);
		line2.add(tf2);
		thiswindow.add(line2);
		
		b.setPreferredSize(new Dimension(200, 35));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = gbc.weighty = 1.0;
		lineok.add(b, gbc);
		thiswindow.add(lineok);
		
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(7000);

		((PlainDocument) jtf.getDocument()).setDocumentFilter(new FilterNumbers());
		jtf.setPreferredSize(new Dimension(200, 25));
		jtf.setToolTipText(_T.PasswordEncrypt_itcountdescr.val());
		jl = new JLabel(_T.PasswordEncrypt_itcount + ": ");
		jl.setToolTipText(_T.PasswordEncrypt_itcountdescr.val());
		lineopt.add(jl);
		lineopt.add(jtf);
		
		final JButton jb = new JButton(new ImageIcon(ResourceCenter.optsup));
        line3.add(jb);
		thiswindow.add(line3);
		
		thiswindow.add(lineopt);
		lineopt.setVisible(false);

		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(lineopt.isVisible()){
					lineopt.setVisible(false);
					jb.setIcon(new ImageIcon(ResourceCenter.optsup));
				}
				else{
					lineopt.setVisible(true);
					jb.setIcon(new ImageIcon(ResourceCenter.optsdown));
				}
				
				thiswindow.pack();
			}
		});
		
		thiswindow.pack();
		
		thiswindow.setLocationRelativeTo(mainwindow);
		thiswindow.setVisible(true);
		
		thiswindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					tf.setText(DUMMYTEXT);
					tf2.setText(DUMMYTEXT);
					
					tf.setText("");
					tf2.setText("");
				} catch (Exception ex) {}
			}
		});
		
		if(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)){
			JOptionPane.showMessageDialog(thiswindow, _T.Password_caps);
		}
	}
}
