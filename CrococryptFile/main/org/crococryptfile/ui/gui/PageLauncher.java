package org.crococryptfile.ui.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;

import org.crococryptfile.ui.UICenter;
import org.crococryptfile.ui.resources.ResourceCenter;
import org.crococryptfile.ui.resources._T;
import org.fhissen.utils.ui.SwingEmptyCaret;
import org.fhissen.utils.ui.SwingHacks;


public class PageLauncher {
	static {
		UICenter.INIT();
		SwingHacks.makeHTMLButtonSpacable();
	}

	public static class Options{
		public String title = ResourceCenter.TITLE;
		public boolean resizable = false;
		public boolean forcescrollpane = false;
		public Window mainwindow = null;
		
		public Options(){}
		
		public Options(Object page){
			if(page instanceof Page)
				mainwindow = ((Page)page).getPageLauncher().getWindow();
		}
	}

	public static PageLauncher launch(Page page){
		return launch(null, page);
	}

	public static PageLauncher launch(Options opt, Page page){
		return new PageLauncher(opt, page);
	}

	private Window window;
	private JEditorPane epane;
	private Page cur;

	private PageLauncher(Options opt, Page page){
		if(opt == null) opt = new Options();
		
		cur = page;

		if(opt.mainwindow == null)
			window = makeJFrameWindow(opt);
		else
			window = makeJDialogWindow(opt);

		epane = new JEditorPane();
		JScrollPane jsc = new JScrollPane(epane);
		jsc.setBorder(null);
		jsc.setBackground(Color.WHITE);
		if(opt.forcescrollpane) jsc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		epane.setContentType("text/html");

		initHTML();
		show(page);

		window.setMinimumSize(cur.getSize());
		window.setIconImages(ResourceCenter.icons);
		window.setBackground(Color.WHITE);
		window.add(jsc);
		window.setSize(cur.getSize());
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		epane.requestFocus();
	}
	
	public Window getWindow(){
		return window;
	}

	public Page getPage(){
		return cur;
	}
	
	public void exit(){
		window.dispose();
	}

	public void refresh() {
		show(cur);
	}

	private void show(Page page) {
		epane.setText(page.HTML(this));
		epane.setCaretPosition(0);
	}

	private JFrame makeJFrameWindow(Options opt){
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jf.setResizable(opt.resizable);
		jf.setTitle(opt.title);
		jf.getContentPane().setBackground(Color.WHITE);
		
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int ret = JOptionPane.showConfirmDialog(window, _T.Launcher_reallyquit, ResourceCenter.TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ret == JOptionPane.YES_OPTION)
					window.dispose();
			}
		});

		return jf;
	}
	
	private JDialog makeJDialogWindow(Options opt){
		JDialog jd = new JDialog(opt.mainwindow);
		jd.setModal(true);
		jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jd.setResizable(opt.resizable);
		jd.setTitle(opt.title);
		jd.getContentPane().setBackground(Color.WHITE);
		
		return jd;
	}
	
	private void initHTML() {
		epane.setBackground(Color.WHITE);
		epane.setOpaque(false);
		epane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.FALSE);
		epane.setEditable(false);
		epane.setCaret(new SwingEmptyCaret());
		epane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				epane.requestFocus();
			}
		});
		((HTMLEditorKit) epane.getEditorKit()).setAutoFormSubmission(false);

		epane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e instanceof FormSubmitEvent) {
					try {
						FormSubmitEvent fe = (FormSubmitEvent) e;
						String data = URLDecoder.decode(fe.getData(), Charset.defaultCharset().toString());
						if (data == null || data.length() == 0) return;
						
						String[] parts = data.split("&");
						HashMap<Object, String> params = new HashMap<>();
						Object act = null;
						for(String tmp: parts){
							String[] paramlist = tmp.split("=");
							String check = paramlist[0];
							if (check == null || check.length() == 0) continue;
							
							if(cur.isAction(check)){
								if(check.endsWith(".x") || check.endsWith(".y")) check = check.substring(0, check.length() - 2);
								if(act == null){
									act = cur.extractAction(check);
								}
							}
							else{
								Object param = cur.extractParam(check);
								if(param != null && paramlist.length > 1){
									String tmpval = paramlist[1];
									if(tmpval != null){
										if(tmpval.startsWith("\"") && tmpval.endsWith("\"") && tmpval.length() >= 2)
											tmpval = tmpval.substring(1, tmpval.length() - 1);
										tmpval = tmpval.trim();
									}
									params.put(param, tmpval);
								}
							}
						}
						
						cur.action(PageLauncher.this, act, params);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				else {
					if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
					try {
						if (e.getURL() == null) return;
						URL theurl = e.getURL();

						if (theurl.getProtocol().startsWith("http")) {
							try {
								Desktop.getDesktop().browse(theurl.toURI());
							} catch (Exception e2) {}
							return;
						}

						String url = e.getURL().getFile();

						if (url.startsWith("/NOOP")) return;
						if (url == null || url.length() == 0) return;

						url = url.substring(1);
						cur.action(PageLauncher.this, cur.extractAction(url), null);
					} catch (Exception e2) {}
				}
			}
		});
	}
}
