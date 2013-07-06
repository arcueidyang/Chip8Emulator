package gui;

import hardware.CPU;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FileMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 401309302645382484L;
	
	
	private JMenuItem loadItem;
	private JMenuItem startItem;
	private CPU myCPU;
	
	public FileMenu(CPU cpu) {
		super("File");
		myCPU = cpu;
		createLoadItem();
		createStartItem();
	}
	
	private void createLoadItem() {
		loadItem = new JMenuItem("load");
		loadItem.setMnemonic(KeyEvent.VK_L);
		
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = getFileChooser(CPU.DEFAULT_PATH);
				fileChooser.showDialog(loadItem, "Load");
			}
		};
		loadItem.addActionListener(listener);
		add(loadItem);
	}
	
	private void createStartItem() {
	    startItem = new JMenuItem("start");
	    startItem.setMnemonic(KeyEvent.VK_S);
	    
	    ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				myCPU.startThread();
				
			}
	    };
	    startItem.addActionListener(listener);
	    add(startItem);
	}
	
	private JFileChooser getFileChooser(String relativePath) {
		JFileChooser chooser = new JFileChooser(relativePath);
		chooser.setControlButtonsAreShown(true);
		
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser theFileChooser = (JFileChooser)e.getSource();
			    String command = e.getActionCommand();
			    if(command == JFileChooser.APPROVE_SELECTION) {
			    	File selectedFile = theFileChooser.getSelectedFile();	
			        myCPU.load(selectedFile);
			        myCPU.startThread();
			    }
			}
		};
		
		chooser.addActionListener(listener);
		return chooser;
	}
}
