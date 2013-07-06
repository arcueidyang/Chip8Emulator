package hardware;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class CPU implements Runnable{
	
	public static final String GAME_VERSION = "1.0";
	
	public static final String DEFAULT_PATH = "./roms/";
	
	private static final int SLEEP_TIME = 20;
	
	private static final int MEMORY_START = 0x0200;
	
	private static final int STACK_POINTER_START = 0x01E0;
	
	//the memory, 4k in total
	private char[] memory;
	
    // the opcode, which is 2 bytes
	private Integer opCode;

	//16 CPU registers
	private char[] V;
	
	//index register, from 0x000 to 0xFFF
	private int indexRegister;
	
	//program counter, from 0x000 to 0xFFF
	private int pc;
	
	private Display myDisplay;
	
	private char delayTimer;

	private char soundTimer;
	
	private int period;
	private int fullPeriod;
	
	//stack pointer 
	private int sp;
	
	//whether a certain key has been pressed
	private boolean[] keyPressed;
	
	//whether a key has been pressed during this cycle
	private boolean isKeyPressed;
	
	//whether a different key has been pressed during this cycle
	private boolean isDifferentKeyPressed;
	
	//the last key pressed
	private char lastKeyPressed;
	
	//last different key pressed
	private char currentKeyPressed;
	
	private boolean isRunning;
	
	private Thread CPUThread;
	
	private Random myRandom;
	
	 private final char[] Font =  {
		      0xf9,0x99,0xf2,0x62,0x27,
		      0xf1,0xf8,0xff,0x1f,0x1f,
		      0x99,0xf1,0x1f,0x8f,0x1f,
		      0xf8,0xf9,0xff,0x12,0x44,
		      0xf9,0xf9,0xff,0x9f,0x1f,
		      0xf9,0xf9,0x9e,0x9e,0x9e,
		      0xf8,0x88,0xfe,0x99,0x9e,
		      0xf8,0xf8,0xff,0x8f,0x88 };
	
	// key map
	private final char[] keyMap = {
		0x01,0x02,0x03,0x0C,
		0x04,0x05,0x06,0x0D,
		0x07,0x08,0x09,0x0E,
		0x0A,0x00,0x0B,0x0F };
	
	private final byte[] keyUnMap = {
		0x0D,0x00,0x01,0x02,
		0x04,0x05,0x06,0x08,
		0x09,0x0A,0x0C,0x0E,
		0x03,0x07,0x0B,0x0F	};
	
	public CPU() {
		memory = new char[4096];
		V = new char[16];
		myRandom = new Random();
		myDisplay = new Display();
		keyPressed = myDisplay.getPressedKeys();
		
		init();
	}
	
	public void init() {
		pc = MEMORY_START;
		opCode = 0;
		indexRegister = 0;
		sp = STACK_POINTER_START;
		period = 0;
		fullPeriod = 25;
		
		myDisplay.clear();
		clearVRegisters(); 
		clearMemory(); 
		loadFontSet();
		
	}
	
	public void startThread() {
		if(CPUThread == null) {
			isRunning = true;
			CPUThread = new Thread(this);
			CPUThread.start();
		}
	}
	
	public void suspendThread() {
		CPUThread.suspend();
	}
	
	public void resumeThread() {
		CPUThread.resume();
	}
	
	public void stopThread() {
		isRunning = false;
		CPUThread = null;
	}
 
	
	public void loadFontSet() {
		for(int i=0; i<40; i++) {
	         memory[i << 1] = (char)(Font[i] & 0xf0);
	         memory[(i << 1) + 1] = (char)((Font[i] << 4) & 0xf0);
	      }
	}
	
	public void resetTimers() {
		delayTimer = '0';
		soundTimer = '0';
	}
	
	public void load(File gameFile) {
		FileInputStream stream;
		try {
			stream = new FileInputStream(gameFile);
			int count = stream.available();
			System.out.println("COUNT" + count);
			for(int i = 0 ; i < count ; i++) {
				memory[i+512] = (char)(stream.read() & 0x00FF);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void load(String fileName) {
		File gameFile = new File(DEFAULT_PATH + fileName);
		load(gameFile);
	}
	
	public void execute() {
		emulateCycle();
		
		updateOpCode();
		
		isKeyPressed = false;
		isDifferentKeyPressed = false;
		
		for(int i=0 ; i<16 ; i++) {
			if(keyPressed[i]) {
				currentKeyPressed = keyMap[i];
				isKeyPressed = true;
			}
		}
		
		if(isKeyPressed) {
			if(currentKeyPressed != lastKeyPressed) {
				lastKeyPressed = currentKeyPressed;
				isDifferentKeyPressed = true;
			}
		} else {
			lastKeyPressed = 0xFF;
		}
		
		timing();
		
	}
	
	public void updateOpCode() {
		opCode = (memory[pc] << 8 | memory[pc + 1]) & 0xFFFF;
	}
	
	public void emulateCycle() {
		
		char opCode1 = (char)((opCode & 0xF000) >> 12);
		char opCode2 = (char)((opCode & 0x0F00) >> 8);
		char opCode3 = (char)((opCode & 0x00F0) >> 4);
		char opCode4 = (char)(opCode & 0x000F);
		
		switch(opCode1) {
		case 0x0:{
			switch(opCode3) {
			case 0xE:
				switch(opCode4) {
				    case 0x0: {
					    myDisplay.clear();
					    pc += 2;
					    break;
				    }
				    
				    case 0xE: {
				    	pc = memory[sp]<<8;
                        sp = sp+1;
                        pc = pc + memory[sp];
                        sp = sp+1;
                        pc += 2;
                        break;
				    }	
				}
			}
			break;
		}
		
		case 0x1: {
			pc = opCode & 0x0FFF;
			break;
		}	
		case 0x2: {
			sp = sp-1;
            memory[sp] = (char)(pc & 0x00FF);
            sp = sp-1;
            memory[sp] = (char)(pc >> 8);
            // jump
            pc = (opCode & 0x0FFF);
            break;
		}    
			
		case 0x3: {
            if(V[opCode2] == getLower(opCode)) pc += 2;
            pc += 2;
		    break;
		}
		
		case 0x4: {
			if(V[opCode2] != getLower(opCode)) pc += 2;
            pc += 2;
            System.out.println("PC : " + Integer.toHexString(pc));
		    break;
		}
		
		case 0x5: {
			if(V[opCode2] == V[opCode3]) pc += 2;
			pc += 2;
			break;
		}
		
		case 0x6: {
			V[opCode2] = (char)getLower(opCode);
			pc += 2;
			break;
		}
		
		case 0x7: {
			V[opCode2] = (char)((V[opCode2] + getLower(opCode)) & 0x00FF);
			pc += 2;
			break;
		}
			
		case 0x8: {
			switch(opCode4) {
			
			case 0x0: {
				V[opCode2] = V[opCode3];
				break;
			}
			
			case 0x1: {
				V[opCode2] = (char)(V[opCode2] | V[opCode3]);
				break;
			}
			
			case 0x2: {
				V[opCode2] = (char)(V[opCode2] & V[opCode3]);
				break;
			}
			
			case 0x3: {
				V[opCode2] = (char)(V[opCode2] ^ V[opCode3]);	
				break;
			}
			
			case 0x4: {
				int sum = V[opCode2] + V[opCode3];
				V[opCode2] = (char)(sum & 0x00FF);
				V[15] = (char)((sum & 0x0F00) >> 8);
				break;
			}
				
			case 0x5: {
				V[15] = (char)(((V[opCode2] - V[opCode3]) >= 0) ? 0x01:0x00);
				V[opCode2] = (char)((V[opCode2] - V[opCode3]) & 0x00FF);
				break;
			}
				
			case 0x6: {
				V[15] = (char)(((V[opCode2]&0x000F) > 0)? 0x01 : 0x00);
				V[opCode2] >>=1;
				break;	
			}
			
			case 0x7: {
				V[15] = (char)(((V[opCode3] - V[opCode2]) >= 0) ? 0x01:0x00);
				V[opCode2] = (char)(V[opCode3] - V[opCode2]);
				break;
			}
				
			case 0xE: {
				V[15] = (char)(((V[opCode2]>>7) > 0)? 0x01 : 0x00);
				V[opCode2] = (char)((V[opCode2] << 1) & 0x00FF);
				break;
			}
			
			default: break;
			}
			pc += 2;
			break;
		}
		
		case 0x9: {
			if(V[opCode2] != V[opCode3]) pc += 2;
			pc += 2;
			break;
		}
		
		case 0xA: {
			indexRegister = opCode & 0x0FFF;
			pc += 2;
			break;
		}
		
		case 0xB: {
			pc = (opCode & 0x0FFF + V[0]) & 0x0FFF;
		    break;
		}	
		case 0xC: {
			V[opCode2] = (char)((opCode & 0x00FF) & (myRandom.nextInt(0xFFFF) & 0x00FF)); 
		    pc += 2;
			break;
		}	
		case 0xD: {
			int x = V[opCode2];
			int y = V[opCode3];
			int height = opCode4;
			boolean isFlipped = false;
			
			for(int i =0 ; i < height; i++) {
				myDisplay.draw8Bit(x, y + i, memory[indexRegister + i], isFlipped);
			}
			myDisplay.repaint();
			V[15] = (char)(isFlipped ? 0x01 : 0x00);
			pc += 2;
			break;
		}
		
		case 0xE: {
			
			switch(getLower(opCode)) {
			case 0x9E: {
				if(keyPressed[keyUnMap[V[opCode2]]]) {
					pc += 2;
				}
				break;	
			}
			
			case 0xA1: {
				if(!keyPressed[keyUnMap[V[opCode2]]]) {
					pc += 2;
				}
				break;
			}
			
			}
			pc += 2;
			break;
		}
		
		case 0xF: {
			switch(getLower(opCode)) {
			
			case 0x07: {
				V[opCode2] = (char)delayTimer;
				pc += 2;
				break;
			}
				
			case 0x0A: {
				if(isDifferentKeyPressed) {
					V[opCode2] = (char)currentKeyPressed;
					pc += 2;
				}
				break;
			}
				
			case 0x15: {
				delayTimer = V[opCode2];
				pc += 2;
				break;
			}
			
			case 0x18: {
				soundTimer = V[opCode2];
				pc += 2;
				break;
			}
				
			case 0x1E: {
				//V[15] = (char)((indexRegister + V[opCode2]) > 0x0FFF ? 0x01 : 0x00); 
				indexRegister = (indexRegister +  V[opCode2]) & 0x0FFF;
				pc += 2;
				break;
			}
			
			case 0x29: {
				indexRegister = (V[opCode2] & 0x0F) * 5;
				pc += 2;
				break;
			}
			case 0x33: {
				memory[indexRegister] = (char)(V[opCode2] / 100);
				memory[indexRegister + 1] = (char)((V[opCode2] / 10) % 10);
				memory[indexRegister + 2] = (char)(V[opCode2] % 10);
				pc += 2;
				break;
			}
				
			case 0x55: {
			    for(int i = 0, j = opCode2; i <= j ; i++) {
			    	memory[indexRegister + i] = V[i];
			    }
			    pc += 2;
			    break;
			}
				
			case 0x65: {
				for(int i = 0, j = opCode2; i <= j ; i++) {
			        V[i] = memory[indexRegister + i];
			    }
			    pc += 2;
			    break;
			}
		    }
		    break;	
		
	    }
			
		default : {
			System.out.println("Unknown Code");
			break;
		}
		}
		
	}
	
	public void timing() {
		period ++;
		
		if(period == fullPeriod) {
			period = 0;
			
			if(delayTimer > 0) {
				delayTimer --;
			}
			if(soundTimer > 0) {
				if(soundTimer == 1) soundTimer --;
			}
			
			try {
				CPUThread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	private int getLower(int opCode) {
		return opCode & 0x00FF;
	}
	
	public void clearVRegisters() {
		for(int i = 0 ; i < 16 ; i++) {
			V[i] = 0;
		}
	}
	
	public void clearMemory() {
		for(int i =0 ; i < 4096 ; i++) {
			memory[i] = 0;
		}
	}
	
	public Display getCanvas() {
		return myDisplay;
	}

	@Override
	public void run() {
		while(isRunning) {
			execute();
		}
	}
}
