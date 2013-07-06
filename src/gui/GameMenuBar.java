package gui;

import java.awt.event.KeyEvent;

import hardware.CPU;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * 
 * @author Richard Yang
 *
 */
public class GameMenuBar extends JMenuBar{

	
	private JMenu fileMenu;
	private JMenu optionMenu;
	private JMenu helpMenu;
	
	public GameMenuBar(CPU cpu) {
		super();
		createFileMenu(cpu);
		createOptionMenu();
		createHelpMenu();
	}
	
	private void createFileMenu(CPU cpu) {
		fileMenu = new FileMenu(cpu);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);
	}
	
	private void createOptionMenu() {
		optionMenu = new JMenu("Option");
	    optionMenu.setMnemonic(KeyEvent.VK_O);
	    add(optionMenu);
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenu();
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);
	}
	
}
