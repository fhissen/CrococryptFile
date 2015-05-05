package org.crococryptfile.ui.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.crococryptfile.ui.resources.ResourceCenter;
import org.fhissen.utils.ui.StatusUpdate;

public class ProgressWindow {
	private Window parent;
	private JDialog jd;
	
	public ProgressWindow(Window parent){
		this.parent = parent;
	}

	public StatusUpdate prepare() {
		jd = new JDialog(parent);
		jd.setTitle(ResourceCenter.TITLE);
		jd.setResizable(false);
		jd.setLayout(null);
		jd.getContentPane().setLayout(null);

		jd.setIconImages(ResourceCenter.icons);
		jd.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		final JProgressBar jp = new JProgressBar(0, 100);
		jp.setSize(new Dimension(350, 35));
		jp.setLocation(0, 0);
		jp.setBorderPainted(true);
		jp.setStringPainted(true);
		jp.setForeground(Color.decode("#608AEE"));
		jd.add(jp);

		final DefaultListModel<String> model = new DefaultListModel<>();
		JList<String> jl = new JList<>(model);
		jl.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSelectionInterval(int i0, int i1) {
				super.setSelectionInterval(-1, -1);
			}
		});
		model.addElement(" ");

		jl.setVisibleRowCount(5);
		jl.setSize(new Dimension(jp.getWidth(), jl.getPreferredScrollableViewportSize().height + 10));
		jl.setLocation(0, jp.getHeight());
		jl.setBorder(new EmptyBorder(5, 5, 5, 5));
		jd.add(jl);

		Dimension thesize = new Dimension(jp.getWidth(), jp.getHeight() + jl.getHeight());
		jd.getContentPane().setPreferredSize(thesize);
		jd.setSize(thesize);
		jd.setLocationRelativeTo(null);
		
		model.remove(0);

		return new StatusUpdate() {
			private boolean active = true;
			
			private WindowListener wl = new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					finished();
					active = false;
				}
			};
			
			{
				jd.addWindowListener(wl);
			}

			@Override
			public void start() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jd.setVisible(true);
						jd.pack();
						jd.setLocationRelativeTo(null);
					}
				});
			}
			
			@Override
			public void finished() {
				if(active){
					jd.removeWindowListener(wl);
					jd.dispose();
				}
			}
			
			@Override
			public void receiveProgress(int perc) {
				jp.setValue(perc);
			}
			
			@Override
			public void receiveDetailsProgress(int perc) {
			}

			@Override
			public void receiveMessageSummary(String msg) {
				receiveMessageDetails(msg);
			}

			@Override
			public void receiveMessageDetails(final String msg) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							while (model.size() >= 5) {
								model.remove(0);
							}
							model.addElement(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public boolean isActive() {
				return active;
			}
		};
	}
}
