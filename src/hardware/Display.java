package hardware;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * 
 * @author Richard Yang
 *
 */
public class Display extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4017774770379994845L;
	
	private static final String GAME_FRAME_NAME = "CHIP-8 Display";
	
	private static final Dimension DEFAULT_FRAME_DIMENSION = new Dimension(640, 320);
	
	public static final int PIXEL_WIDTH = 10;
	public static final int PIXEL_HEIGHT = 10;
	
	public static final int SCREEN_WIDTH = 64;
	public static final int SCREEN_HEIGHT = 32;

	private char[][] myPixels;
	
	private boolean[] keyPressed; 
	
	private final Map<Integer, Integer> keyMap;
	
	public Display() {
		super();
		makeSettings();
		myPixels = new char[SCREEN_HEIGHT][SCREEN_WIDTH];
		keyPressed = new boolean[16];
		keyMap = new HashMap<Integer, Integer>();
		initializeKeyMap();
		addKeyListener();
	}
	
	private void makeSettings() {
		this.setName(GAME_FRAME_NAME);
		this.setPreferredSize(DEFAULT_FRAME_DIMENSION);
		setSize(DEFAULT_FRAME_DIMENSION);
		this.setFocusable(true);
		setVisible(true);
	}
	
	private void initializeKeyMap() {
		keyMap.put(KeyEvent.VK_1, 0);
		keyMap.put(KeyEvent.VK_2, 1);
		keyMap.put(KeyEvent.VK_3, 2);
		keyMap.put(KeyEvent.VK_4, 3);
		keyMap.put(KeyEvent.VK_Q, 4);
		keyMap.put(KeyEvent.VK_W, 5);
		keyMap.put(KeyEvent.VK_E, 6);
		keyMap.put(KeyEvent.VK_R, 7);
		keyMap.put(KeyEvent.VK_A, 8);
		keyMap.put(KeyEvent.VK_S, 9);
		keyMap.put(KeyEvent.VK_D, 10);
		keyMap.put(KeyEvent.VK_F, 11);
		keyMap.put(KeyEvent.VK_Z, 12);
		keyMap.put(KeyEvent.VK_X, 13);
		keyMap.put(KeyEvent.VK_C, 14);
		keyMap.put(KeyEvent.VK_V, 15);
	}
	
	
	public void clear() {
	    for(int i=0 ; i<SCREEN_HEIGHT ; i++) {
	    	for(int j = 0 ; j < SCREEN_WIDTH ; j++) {
	    		myPixels[i][j] = '0';
	    	}
	    }	
	}
	
	public void draw8Bit(int x, int y, char info, boolean isFlipped) {
		System.out.println("X : " + x);
		System.out.println("Y : " + y);
		System.out.println("Information : " + Integer.toBinaryString(info));
		int width = Math.min(8, 64 - x);
		int realY = Math.min(y, 31);
		for(int i =0 ; i<width ; i++) {
			int input = getBit(info, 7-i);
			char origin = myPixels[realY][x+i];
			if(input == 1) {
				if(origin == '1'){
					isFlipped = true;
					myPixels[realY][x+i] = '0';
				}else {
					myPixels[realY][x+i] = '1';
				}
			}
		}
	}
	
	private int getBit(char info, int position) {
		return (info >> position) & 0x01;
	}
	
    private void printPixels() {
    	for(int i = 0 ; i < SCREEN_HEIGHT ; i++) {
    		for(int j = 0; j < SCREEN_WIDTH; j++) {
    			System.out.print(myPixels[i][j]);
    			System.out.print(" ");
    		}
    		System.out.println("\n");
    	}
    }
	
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("./image/white.png"));
		} catch (IOException e) {
				e.printStackTrace();
		}
		Graphics g2 = image.createGraphics();
		this.paintComponents(g2);
		g2.setColor(Color.BLACK);
		
		for(int i = 0 ; i < SCREEN_HEIGHT ; i++) {
			for(int j =0 ; j < SCREEN_WIDTH ; j++) {
				if(myPixels[i][j] == '1') {
				    g2.fillRect(j * PIXEL_WIDTH, i * PIXEL_HEIGHT, PIXEL_WIDTH, PIXEL_HEIGHT);
				}
			}
		}
		
		g.drawImage(image, 0, 0, this);
	}
	
	private void addKeyListener() {
		KeyListener listener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println("Key Pressed : " + arg0.getKeyChar());
				if(keyMap.containsKey(arg0.getKeyCode())) {
					keyPressed[keyMap.get(arg0.getKeyCode())] = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if(keyMap.containsKey(arg0.getKeyCode())) {
					keyPressed[keyMap.get(arg0.getKeyCode())] = false;
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
		};
		this.addKeyListener(listener);
	} 
	
	public boolean[] getPressedKeys() {
		return keyPressed;
	}

}
