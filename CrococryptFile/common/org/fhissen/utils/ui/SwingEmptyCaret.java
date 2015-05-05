package org.fhissen.utils.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.text.DefaultCaret;

public class SwingEmptyCaret extends DefaultCaret{
	private static final long serialVersionUID = 1L;

	@Override
	public void moveDot(int dot) {
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void paint(Graphics g) {
	}
}
