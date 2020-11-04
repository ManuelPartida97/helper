
import javax.jws.WebParam.Mode;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Robot.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TestHelper
{

	public static boolean stopRobot = false;
	public static int clickNumber = 0;
	public static JFrame window;
	public static JTextField textField, delayField, loadField;
	
	public static int delayClick = 15;
	
	public static BufferedImage screenshot;
	public static Image screen;
	
	public static int xClick = 10;
	public static int yClick = 500;
	
	public static double screenShotScale = 0.8;
	
	public static ArrayList<AutomateEvent> autoEvents = new ArrayList<AutomateEvent>();
	
	public static boolean drag = false;
	public static boolean shiftPressed = false;
	
	public static Canvas canvas;
	
	public static AutomateEvent actualEvent;
	
	public static Point lastPoint;

	public static JScrollPane treeView;
	public static JTree tree;
	public static DefaultMutableTreeNode node;
	public static DefaultTreeModel model;
	
	public static int selectedEvent = -1;
	
	public static void main(String[] args) {
		test();
		
		// while(true)
		//	 sleepLoop();
	}
	
	public static void test() {
		loadAutomateEvents("C:/scripts/automateEvents.txt");
		initializeWindow();
		takeScreenshot();
		printScreenShot();
		displayWindow();
	}
	
	public static void loadAutomateEvents(String path) {

		File file = new File(path);
		
		try {
			if(!file.exists())
				file.createNewFile();
		
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		autoEvents = new ArrayList<AutomateEvent>();
		
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buff = new BufferedReader(reader);
			String line, cmd;
			
			while((line = buff.readLine()) != null) {
				cmd = line;
				if(cmd.equals("PNT")) {
					int x = (int) (double) Double.valueOf(buff.readLine());
					int y = (int) (double) Double.valueOf(buff.readLine());
					
					AutomateEvent event = new AutomateEvent();
					event.point = new Point(x, y);
					event.isPoint = true;
					event.isDrag = false;
					autoEvents.add(event);
					
				} else if(cmd.equals("DRG")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = true;
					int x = (int) (double) Double.valueOf(buff.readLine());
					int y = (int) (double) Double.valueOf(buff.readLine());
					event.drag = new Point(x, y);
					
					x = (int) (double) Double.valueOf(buff.readLine());
					y = (int) (double) Double.valueOf(buff.readLine());
					event.drop = new Point(x, y);
					autoEvents.add(event);
					
				} else if(cmd.equals("TXT")) {
					AutomateEvent event = new AutomateEvent(buff.readLine());
					autoEvents.add(event);
					
				} else if(cmd.equals("CPY")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isCopy = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("PST")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isPaste = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("UPT")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isUp = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("DWN")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isDown = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("LFT")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isLeft = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("RGT")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isRight = true;
					autoEvents.add(event); 
					
				} else if(cmd.equals("BCK")) {
					AutomateEvent event = new AutomateEvent();
					event.isDrag = false;
					event.isBack = true;
					autoEvents.add(event);
					
				} else if(cmd.equals("DLY")) {
					int delay = (int) (double) Double.valueOf(buff.readLine());
					
					AutomateEvent event = new AutomateEvent();
					event.delay = delay;
					event.isDelay = true;
					event.isDrag = false;
					autoEvents.add(event);
					
				} else if(cmd.equals("SHF")) {
					AutomateEvent event = new AutomateEvent();
					event.isShift = true;
					event.isDrag = false;
					
					autoEvents.add(event);
					
				} else if(cmd.equals("SCR")) {
					AutomateEvent event = new AutomateEvent();
					event.isScreenshot = true;
					event.isDrag = false;
					
					autoEvents.add(event);
					
				} 
			}
			buff.close();
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void takeScreenshot() {
		try {
			Robot bot = new Robot();
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			screenshot = bot.createScreenCapture(screenRect);
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		screen = screenshot.getScaledInstance(screenshot.getWidth(), screenshot.getHeight(), BufferedImage.SCALE_SMOOTH);
		
	}
	
	public static void printScreenShot() {
		
		canvas = new Canvas() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				try { 
					Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					int width = (int)(r.width * screenShotScale);
					int height = (int)(r.height * screenShotScale);
					g.drawImage(screen, 0, 0, width, height, this);

					for(AutomateEvent autoEvent:autoEvents) {
						if(autoEvent.isPoint) {
							Point point = autoEvent.point;
							g.setColor(Color.RED);
							g.fillRect((int) (point.getX() * screenShotScale - 2), (int) (point.getY() * screenShotScale - 2), 4, 4);
							lastPoint = point;
							
						} else if (autoEvent.isDrag) {
							Point drag = autoEvent.drag;
							g.setColor(Color.BLUE);
							g.fillRect((int) (drag.getX() * screenShotScale - 2), (int) (drag.getY() * screenShotScale - 2), 4, 4);
							
							Point drop = autoEvent.drop;
							g.setColor(Color.BLUE);
							g.fillRect((int) (drop.getX() * screenShotScale - 2), (int) (drop.getY() * screenShotScale - 2), 4, 4);
							
							
							int centerX = (int) drop.getX();
							int centerY = (int) drop.getY();
							lastPoint = new Point(centerX, centerY);
							
						} else {
							if(lastPoint == null)
								return;

							g.setColor(Color.GREEN);
							int x = (int) (lastPoint.getX() * screenShotScale);
							int y = (int) (lastPoint.getY() * screenShotScale);
							g.fillRect(x + 2, y, 4, 4);
							
							if (autoEvent.isText) {
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString(autoEvent.text, x + 4, y);
								
							} else if (autoEvent.isCopy) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Copy}", x + 4, y);
								
							} else if (autoEvent.isPaste) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Paste}", x + 4, y);
								
							} else if (autoEvent.isBack) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Back}", x + 4, y);
								
							} else if (autoEvent.isShift) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Shift}", x + 4, y);
								
							} else if (autoEvent.isUp) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Up}", x + 4, y);
								
							} else if (autoEvent.isDown) {
								g.setColor(Color.RED);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Down}", x + 4, y);
								
							} else if (autoEvent.isScreenshot) {
								g.setColor(Color.MAGENTA);
								g.setFont(new Font("Arial", Font.PLAIN, 8));
								g.drawString("{Screen}", x + 4, y);
								
							}
						}
					}
					
				} catch(Exception e) {
					e.printStackTrace();
					
				}
			}
		};
		Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		int width = (int)(r.width * screenShotScale);
		int height = (int)(r.height * screenShotScale);
		canvas.setSize(width, height);
		
		canvas.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(drag) {
					int x = (int)(e.getX() / screenShotScale);
					int y = (int)(e.getY() / screenShotScale);
					actualEvent = new AutomateEvent();
					actualEvent.drag = new Point(x, y);
					
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {

				if(drag) {
					int x = (int)(e.getX() / screenShotScale);
					int y = (int)(e.getY() / screenShotScale);
					actualEvent.drop = new Point(x, y);
	
					if(selectedEvent > -1) {
						autoEvents.add(selectedEvent + 1, actualEvent);
						selectedEvent++;
					} else
						autoEvents.add(actualEvent);
					
					actualEvent = null;
					
				} else {
					int x = (int)(e.getX() / screenShotScale);
					int y = (int)(e.getY() / screenShotScale);
	
					if(selectedEvent > -1) {
						autoEvents.add(selectedEvent + 1, new AutomateEvent(x, y));
						selectedEvent++;
					} else
						autoEvents.add(new AutomateEvent(x, y));
				}
				canvas.repaint();
				drag = false;
				reloadTree();
			}
			
		});
		
		window.add(canvas);
	}
	
	public static void startAutomateProcess() {
		window.setVisible(false);
		try {
			Robot bot = new Robot();
			for(AutomateEvent autoEvent:autoEvents) {
				
				if(autoEvent.isPoint) {
					Point point = autoEvent.point;
					bot.mouseMove((int) point.getX(), (int) point.getY());
					bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					
				} else if(autoEvent.isText) {
					String upperText = autoEvent.text.toUpperCase();
					char[] characters = autoEvent.text.toCharArray();
					char upperCharacter;
					
					for(int i = 0; i < upperText.length(); i ++) { // 65 - 90 [A-Z]
						upperCharacter = upperText.charAt(i);	   // [a-z] > 90
						
						if(characters[i] < 91)
							bot.keyPress(KeyEvent.VK_SHIFT);
						
						bot.keyPress(upperCharacter);
						bot.keyRelease(upperCharacter);
						
						if(characters[i] > 64)
							bot.keyRelease(KeyEvent.VK_SHIFT);
						
						TimeUnit.MILLISECONDS.sleep(200);
					}
					
				} else if (autoEvent.isDrag) {
					Point drag = autoEvent.drag;
					bot.mouseMove((int) drag.getX(), (int) drag.getY());
					bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					
					Point drop = autoEvent.drop;
					bot.mouseMove((int) drop.getX(), (int) drop.getY());
					bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					
				} else if (autoEvent.isCopy)  {
					bot.keyPress(KeyEvent.VK_CONTROL);
					bot.keyPress(KeyEvent.VK_C);
					bot.keyRelease(KeyEvent.VK_C);
					bot.keyRelease(KeyEvent.VK_CONTROL);
					
				} else if (autoEvent.isPaste)  {
					bot.keyPress(KeyEvent.VK_CONTROL);
					bot.keyPress(KeyEvent.VK_V);
					bot.keyRelease(KeyEvent.VK_V);
					bot.keyRelease(KeyEvent.VK_CONTROL);
					
				} else if (autoEvent.isDelay)  {
					TimeUnit.SECONDS.sleep(autoEvent.delay);
					
				} else if (autoEvent.isBack)  {
					bot.keyPress(KeyEvent.VK_BACK_SPACE);
					bot.keyRelease(KeyEvent.VK_BACK_SPACE);
					
				} else if (autoEvent.isUp)  {
					bot.keyPress(KeyEvent.VK_UP);
					bot.keyRelease(KeyEvent.VK_UP);
					
				} else if (autoEvent.isDown)  {
					bot.keyPress(KeyEvent.VK_DOWN);
					bot.keyRelease(KeyEvent.VK_DOWN);
					
				} else if (autoEvent.isLeft)  {
					bot.keyPress(KeyEvent.VK_LEFT);
					bot.keyRelease(KeyEvent.VK_LEFT);
					
				} else if (autoEvent.isRight)  {
					bot.keyPress(KeyEvent.VK_RIGHT);
					bot.keyRelease(KeyEvent.VK_RIGHT);
					
				} else if (autoEvent.isShift)  {
					if(!shiftPressed)
						bot.keyPress(KeyEvent.VK_SHIFT);
					else
						bot.keyRelease(KeyEvent.VK_SHIFT);
					
					shiftPressed = !shiftPressed;
					
				} else if (autoEvent.isScreenshot)  {
					bot.keyPress(KeyEvent.VK_PRINTSCREEN);
					bot.keyRelease(KeyEvent.VK_PRINTSCREEN);
					
				}
				
				TimeUnit.SECONDS.sleep(1);
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		shiftPressed = false;
		window.setVisible(true);
	}
	
	public static void sleepLoop() {
		try {
			TimeUnit.SECONDS.sleep(TestHelper.delayClick);
			
			if(!TestHelper.stopRobot)
				doClick();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initializeWindow() {
		window = new JFrame("Testing Helper");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initializeButtons();
		
		window.setSize(220, 180);
		window.setLocation(70, 5);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.setLayout(null);
		
	}
	
	public static void displayWindow() {
		window.setVisible(true);
		
	}
	
	public static void initializeButtons() {
		Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		int width = (int)(r.width * screenShotScale);
		
		JButton stopBtn = new JButton("Exit");
		stopBtn.setBounds(width + 10, 40, 100, 30);
		
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		window.add(stopBtn);
		
		JButton startBtn = new JButton("Start");
		startBtn.setBounds(width + 10, 90, 100, 30);
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startAutomateProcess();
				window.setVisible(false);
				takeScreenshot();
				window.setVisible(true);
				canvas.repaint();

				reloadTree();
			}
		});
		window.add(startBtn);
		
		JButton loopBtn = new JButton("Loop");
		loopBtn.setBounds(width + 120, 90, 100, 30);
		loopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String userInput = (String) JOptionPane.showInputDialog("How many repetitions?");
				int repetitions = 0;
				
				if(userInput != null)
					repetitions = Integer.valueOf(userInput);
				
				for(int i = 0; i < repetitions; i++) {
					startAutomateProcess();
				}
				window.setVisible(false);
				takeScreenshot();
				window.setVisible(true);
				canvas.repaint();

				reloadTree();
			}
		});
		window.add(loopBtn);
		
		textField = new JTextField();
		textField.setBounds(width + 10, 130, 130, 30);
		window.add(textField);
		
		JButton textBtn = new JButton("Add Text");
		textBtn.setBounds(width + 10, 170, 130, 30);
		
		textBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, new AutomateEvent(textField.getText()));
					selectedEvent++;
				} else
					autoEvents.add(new AutomateEvent(textField.getText()));
				
				textField.setText("");
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(textBtn);
		
		JButton takeScreenShot = new JButton("Take Screenshot");
		takeScreenShot.setBounds(width + 10, 210, 130, 30);
		
		takeScreenShot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				takeScreenshot();
				window.setVisible(true);
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(takeScreenShot);

		JButton dragButton = new JButton("Drag pointer");
		dragButton.setBounds(width + 10, 250, 190, 30);
		dragButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drag = true;
			}
		});
		window.add(dragButton);

		JButton copy = new JButton("Copy selection");
		copy.setBounds(width + 10, 290, 230, 30);
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isCopy = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(copy);
		
		JButton paste = new JButton("Paste selection");
		paste.setBounds(width + 10, 330, 230, 30);
		paste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isPaste = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(paste);
		
		delayField = new JTextField();
		delayField.setBounds(width + 10, 370, 130, 30);
		window.add(delayField);
		
		JButton delayBtn = new JButton("Add Delay");
		delayBtn.setBounds(width + 10, 410, 130, 30);
		
		delayBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, new AutomateEvent(Integer.valueOf(delayField.getText())));
					selectedEvent++;
				} else
					autoEvents.add(new AutomateEvent(Integer.valueOf(delayField.getText())));
				
				delayField.setText("");
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(delayBtn);
		
		JButton backBtn = new JButton("Back");
		backBtn.setBounds(width + 10, 450, 230, 30);
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isBack = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(backBtn);
		
		JButton leftBtn = new JButton("L");
		leftBtn.setBounds(width + 10, 490, 60, 20);
		leftBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isLeft = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(leftBtn);
		
		JButton upBtn = new JButton("U");
		upBtn.setBounds(width + 70, 490, 60, 20);
		upBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isUp = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				reloadTree();
				canvas.repaint();
			}
		});
		window.add(upBtn);
		
		JButton downBtn = new JButton("D");
		downBtn.setBounds(width + 130, 490, 60, 20);
		downBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isDown = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(downBtn);
		
		JButton rightBtn = new JButton("R");
		rightBtn.setBounds(width + 190, 490, 60, 20);
		rightBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isRight = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(rightBtn);

		tree = new JTree();
		model = (DefaultTreeModel) tree.getModel();
		node = (DefaultMutableTreeNode) model.getRoot();
		node.removeAllChildren();
		model.reload(node);
		tree.repaint();
		treeView = new JScrollPane(tree);
		treeView.setBounds(width + 10, 520, 300, 250);
		
		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selectedEvent = tree.getRowForLocation(e.getX(), e.getY()) - 1;
			}
		});
		window.add(treeView);
		reloadTree();
		
		JButton deleteEvent = new JButton("Delete Event");
		deleteEvent.setBounds(width - 120, 640, 120, 40);
		deleteEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int count = 0;
				
				for(AutomateEvent autoEvent:autoEvents) {
					if(count == selectedEvent) {
						autoEvents.remove(autoEvent);
						selectedEvent = -1;
						break;
					}
					count++;
				}
				reloadTree();
			}
		});
		window.add(deleteEvent);
		window.add(treeView);
		
		JButton save = new JButton("Save");
		save.setBounds(width - 120, 690, 120, 30);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(loadField.getText());
			}
		});
		window.add(save);
		
		loadField = new JTextField("C:/scripts/automateEvents.txt");
		loadField.setBounds(width - 140, 730, 130, 30);
		window.add(loadField);
		
		JButton load = new JButton("Load");
		load.setBounds(width - 280, 730, 120, 30);
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAutomateEvents(loadField.getText());
				reloadTree();
				canvas.repaint();
			}
		});
		window.add(load);
		
		JButton shift = new JButton("Shift");
		shift.setBounds(width + 150, 170, 100, 30);
		shift.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isShift = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(shift);
		
		JButton screenshotBtn = new JButton("Screenshot");
		screenshotBtn.setBounds(width + 150, 210, 100, 30);
		screenshotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutomateEvent newAutoEvent = new AutomateEvent();
				newAutoEvent.isDrag = false;
				newAutoEvent.isScreenshot = true;
				
				if(selectedEvent > -1) {
					autoEvents.add(selectedEvent + 1, newAutoEvent);
					selectedEvent++;
				} else
					autoEvents.add(newAutoEvent);
				
				canvas.repaint();
				reloadTree();
			}
		});
		window.add(screenshotBtn);
		
	}
	
	public static void save(String path) {
		// BCK, RGT, LFT, DWN, UPT, PST, CPY, TXT, DRG, PNT, DLY
		File file = new File(path);
		
		try {
			if(!file.exists())
				file.createNewFile();
		
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter buff = new BufferedWriter(writer);
			
			for(AutomateEvent autoEvent:autoEvents) {
				
				if(autoEvent.isPoint) {
					buff.write("PNT");
					buff.newLine();
					buff.write(String.valueOf(autoEvent.point.getX()));
					buff.newLine();
					buff.write(String.valueOf(autoEvent.point.getY()));
					buff.newLine();
					
				} else if(autoEvent.isText) {
					buff.write("TXT");
					buff.newLine();
					buff.write(autoEvent.text);
					buff.newLine();
					
				} else if (autoEvent.isDrag) {
					buff.write("DRG");
					buff.newLine();
					buff.write(String.valueOf(autoEvent.drag.getX()));
					buff.newLine();
					buff.write(String.valueOf(autoEvent.drag.getY()));
					buff.newLine();
					buff.write(String.valueOf(autoEvent.drop.getX()));
					buff.newLine();
					buff.write(String.valueOf(autoEvent.drop.getY()));
					buff.newLine();
					
				} else if (autoEvent.isCopy)  {
					buff.write("CPY");
					buff.newLine();
					
				} else if (autoEvent.isPaste)  {
					buff.write("PST");
					buff.newLine();
					
				} else if (autoEvent.isDelay)  {
					buff.write("DLY");
					buff.newLine();
					buff.write(String.valueOf(autoEvent.delay));
					buff.newLine();
					
				} else if (autoEvent.isBack)  {
					buff.write("BCK");
					buff.newLine();
					
				} else if (autoEvent.isUp)  {
					buff.write("UPT");
					buff.newLine();
					
				} else if (autoEvent.isDown)  {
					buff.write("DNW");
					buff.newLine();
					
				} else if (autoEvent.isLeft)  {
					buff.write("LFT");
					buff.newLine();
					
				} else if (autoEvent.isRight)  {
					buff.write("RGT");
					buff.newLine();
					
				} else if (autoEvent.isShift)  {
					buff.write("SHF");
					buff.newLine();
					
				} else if (autoEvent.isShift)  {
					buff.write("SCR");
					buff.newLine();
					
				}
				
			}
			
			buff.close();
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void reloadTree() {

		node.removeAllChildren();
		boolean shiftPressedTree = false;
		
		for(AutomateEvent autoEvent:autoEvents) {
			
			if(autoEvent.isPoint) {
				DefaultMutableTreeNode newNode = 
							new DefaultMutableTreeNode("Point: " + autoEvent.point.getX() + ", " + autoEvent.point.getY());
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if(autoEvent.isText) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Text: " + autoEvent.text);
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isDrag) {
				DefaultMutableTreeNode newNode = 
						new DefaultMutableTreeNode("Drag: " + autoEvent.drag.getX() + ", " + autoEvent.drag.getY());
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isCopy)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Copy");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isPaste)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Paste");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isDelay)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Delay:" + autoEvent.delay);
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isBack)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Back");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isUp)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Up");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isDown)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Down");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isLeft)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Left");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isRight)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Right");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isShift)  {
				DefaultMutableTreeNode newNode;
				if(!shiftPressedTree)
					newNode = new DefaultMutableTreeNode("Shift Pressed");
				else 
					newNode = new DefaultMutableTreeNode("Shift Released");
				
				shiftPressedTree = !shiftPressedTree;
				
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			} else if (autoEvent.isScreenshot)  {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Screenshot");
				model.insertNodeInto(newNode, node, node.getChildCount());
				
			}
			
			model.reload(node);
			tree.repaint();
			
		}
	}
	
	public static void doClick() {
		TestHelper.clickNumber++;
		TestHelper.click(TestHelper.xClick, TestHelper.yClick);
	}
	
	public static void click(int x, int y) {
		try {
			Robot bot = new Robot();
			// bot.mouseMove(x, y);
			bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopLoop() {
		TestHelper.stopRobot = true;
	}
	
	public static void startLoop() {
		TestHelper.stopRobot = false;
	}
	
}

class AutomateEvent{
	
	public Point point;
	public String text;
	public boolean isPoint = false;
	public boolean isText = false;
	public boolean isDrag = false;
	public boolean isCopy = false;
	public boolean isPaste = false;
	public boolean isDelay = false;
	public boolean isBack = false;
	public boolean isShift = false;
	public boolean isScreenshot = false;
	
	public boolean isUp = false;
	public boolean isDown = false;
	public boolean isLeft = false;
	public boolean isRight = false;
	
	public int delay = 0;
	public Point drag;
	public Point drop;
	
	public AutomateEvent() {
		this.isDrag = true;
	}
	
	public AutomateEvent(int delay) {
		isDelay = true;
		this.delay = delay;
	}
	
	public AutomateEvent(boolean isPoint) {
		this.isPoint = isPoint;
		this.isText = !isPoint;
		this.isDrag = false;
	}
	
	public AutomateEvent(String text) {
		this.isPoint = false;
		this.isText = !isPoint;
		
		this.text = text;
	}
	
	public AutomateEvent(int x, int y) {
		this.isPoint = true;
		this.isText = !isPoint;
		
		point = new Point(x, y);
	}
	
}
