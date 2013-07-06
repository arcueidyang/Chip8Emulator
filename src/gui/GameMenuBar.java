package gui;

import java.awt.event.KeyEvent;

import hardware.CPU;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class GameMenuBar extends JMenuBar{

	
	private JMenu fileMenu;
	private JMenu optionMenu;
	
	
	public GameMenuBar(CPU cpu) {
		super();
		createFileMenu(cpu);
		createOptionMenu();
	}
	
	private void createFileMenu(CPU cpu) {
		fileMenu = new FileMenu(cpu);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);
	}
	
	private void createOptionMenu() {
			
	}
	
}
