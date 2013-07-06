package gui;

import hardware.CPU;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * 
 * @author Richard Yang
 *
 */
public class FileMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 401309302645382484L;
	
	
	private JMenuItem loadItem;
	private JMenuItem startItem;
	private JMenuItem pauseItem;
	private JMenuItem resumeItem;
	private JMenuItem closeItem;
	private CPU myCPU;
	
	public FileMenu(CPU cpu) {
		super("File");
		myCPU = cpu;
		createLoadItem();
		createStartItem();
		createPauseItem();
		createResumeItem();
		createCloseItem();
	}
	
	private void createLoadItem() {
		loadItem = new JMenuItem("load");
		loadItem.setMnemonic(KeyEvent.VK_L);
		
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				myCPU.stopThread();
				myCPU.init();
				JFileChooser fileChooser = getFileChooser(CPU.DEFAULT_PATH);
				fileChooser.showDialog(loadItem, "Load");
			}
		};
		loadItem.addActionListener(listener);
		add(loadItem);
	}
	
	private void createStartItem() {
	    startItem = new JMenuItem("Start");
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
	
	private void createPauseItem() {
		pauseItem = new JMenuItem("Pause");
	    pauseItem.setMnemonic(KeyEvent.VK_P);
	    ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myCPU.suspendThread();
			}
	    };
	    pauseItem.addActionListener(listener);
	    add(pauseItem);
	}
	
	private void createResumeItem() {
		resumeItem = new JMenuItem("Resume");
	    resumeItem.setMnemonic(KeyEvent.VK_R);
	    ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myCPU.resumeThread();
			}
	    };
	    resumeItem.addActionListener(listener);
	    add(resumeItem);		
	}
	
	private void createCloseItem() {
		closeItem = new JMenuItem("Close");
		closeItem.setMnemonic(KeyEvent.VK_C);
		
		ActionListener closeActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				myCPU.suspendThread();
				int response = JOptionPane.showOptionDialog(closeItem, "DO YOU REALLY WANT TO CLOSE THIS GAME?", 
						                      "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				
				if(response == 0) {
					System.exit(0);
				}else if(response == 1) {
					myCPU.resumeThread();
				}
			}
		};
		closeItem.addActionListener(closeActionListener);
		add(closeItem);
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
			    }
			}
		};
		
		chooser.addActionListener(listener);
		return chooser;
	}
}
