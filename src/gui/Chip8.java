package gui;

import hardware.CPU;
import hardware.Display;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * 
 * @author Richard Yang
 *
 */
public class Chip8 {
	
	public static final String NAME = "Chip-8 Emulator";

	private JFrame myFrame;
	private GameMenuBar myMenuBar;
	private CPU myCPU;
	
	public Chip8() {
		myCPU = new CPU();
		myFrame = new JFrame(NAME);
		myMenuBar = new GameMenuBar(myCPU);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.add(myCPU.getCanvas(),BorderLayout.CENTER);
		myFrame.setJMenuBar(myMenuBar);
		myFrame.setSize(Display.PIXEL_WIDTH * Display.SCREEN_WIDTH + 10, Display.PIXEL_HEIGHT * Display.SCREEN_HEIGHT + 50);
		myFrame.setResizable(false);
		myFrame.setLocationRelativeTo(null);
		myFrame.setJMenuBar(myMenuBar);
		myFrame.setVisible(true);
	}
}
