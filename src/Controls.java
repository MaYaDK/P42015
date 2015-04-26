//Access keyboard
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//Access window
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
//Access graphics
import java.awt.Color;
import java.awt.Graphics;
//Access picture creation.
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

//UDP
import java.io.*;
import java.net.*;

public class Controls extends JPanel implements ActionListener, KeyListener{
	private BufferedImage shipLeft; //Player
	private BufferedImage shipRight; //Player
	private BufferedImage ship; //Player
	private BufferedImage shipDown; //Player
	private BufferedImage shipDisplay; //only for displaying the current ship.
	
	private BufferedImage image2; //BackGround
	
	private BufferedImage intro1;
	private BufferedImage intro2;
	private BufferedImage intro3;
	private BufferedImage intro4;
	private BufferedImage intro5;
	private BufferedImage intro6;
	private BufferedImage intro7;
	private BufferedImage intro8;
	private BufferedImage intro9;
	private BufferedImage displayIntro;
	
	private BufferedImage end;
	
	public static int screenWidth = 1920, screenHeight = 1080-80;
	Timer tm = new Timer(5, this);
	//Player variables
	int xBackground = 0, yBackground = 0, backgroundWidth = screenWidth*6, backgroundHeight = screenWidth*6, velX = 0, velY = 0; //Background
	int playerWidth = 600, playerHeight = 800;
	int xPlayer = screenWidth/2-playerHeight/2, yPlayer = screenHeight/2-playerWidth/2, newBeaconX, newBeaconY;
	//Line from player to goal
	int playerPointX, playerPointY; //goalPointX = 250, goalPointY = 0;
	//
	int goalPointX;
	int goalPointY;
	String s = ""; //No value = no sound, 10 highest
	String s2 = "";
	//number of beacons reached
	int numberOfBeaconsReached = 0;
	//
	double distanceToGoal;
	boolean isVisible = false;
	//beacon
	boolean isBeacon1Reached = false;
	boolean isBeacon2Reached = false;
	boolean isBeacon3Reached = false;
	//final goal/delivery point
	boolean isGoalReach = false;
	
	//Time
	public int counter = 0;
	
	public int i = 0;
	
	//Game screen
	boolean isGameStarted = false;
	
	public Controls()
	{
		tm.start(); //start timer
		addKeyListener(this); //Not interfering
		setFocusable(true);
		setFocusTraversalKeysEnabled(false); //Wont be using shift, tab.. keys
		try {	
	        ship = ImageIO.read(new File("src/ship.png")); //Player
	        shipLeft = ImageIO.read(new File("src/shipLeft.png")); //Player
	    	shipRight = ImageIO.read(new File("src/shipRight.png")); //Player
	    	shipDown = ImageIO.read(new File("src/shipDown.png")); //Player
	        
	        
	        image2 = ImageIO.read(new File("src/Water.jpg")); //Background
	        intro1 = ImageIO.read(new File("src/1.png")); //Background
	        
	        intro2 = ImageIO.read(new File("src/2.png")); //Background
	        intro3 = ImageIO.read(new File("src/3.png")); //Background
	        intro4 = ImageIO.read(new File("src/4.png")); //Background
	        intro5 = ImageIO.read(new File("src/5.png")); //Background
	        intro6 = ImageIO.read(new File("src/6.png")); //Background
	        intro7 = ImageIO.read(new File("src/7.png")); //Background
	        intro8 = ImageIO.read(new File("src/8.png")); //Background
	        intro9 = ImageIO.read(new File("src/9.png")); //Background
	        
	        end = ImageIO.read(new File("src/end.jpg")); //Background
	          
	    } catch (IOException ex) {
	            // handle exception...
	       }
		shipDisplay = ship; // initialize the first picture to shipUp.
		displayIntro = intro1;
	}

	//////////////////////////////////////////////GAMEPLAY
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(isGameStarted == true){
			//If the game is not won, display game.
			if(isGoalReach == false){
				g.fillRect(0,0,screenWidth,screenHeight);
				g.drawImage(image2, xBackground-600, yBackground-600, backgroundWidth, backgroundHeight, null); //Background
				//Beacons reached
				g.setColor(Color.WHITE);
				g.drawString("Beacon reached: "+ numberOfBeaconsReached, 350, 450);
				g.drawImage(shipDisplay, xPlayer, yPlayer, playerWidth, playerHeight, null); //Player
				//Display path only visible by key pressed A.
				if(isVisible == true){
					g.setColor(Color.RED);
					//Line distance to goal
					playerPointX = xPlayer;
					playerPointY = yPlayer;
					g.drawLine(playerPointX, playerPointY,goalPointX,goalPointY); //could be used to measure distance from player to goal
				}
			}
			if(isGoalReach == true){
				g.drawImage(end, 0, 0, screenWidth, screenHeight, null); //Player
			}
			//If player has reached goal
			if(goalPointX > xPlayer && goalPointX < xPlayer+playerWidth && goalPointY > yPlayer && goalPointY < yPlayer+playerHeight){
				checkPlayerAtGoal(); //method which checks which beacons is already reached.	
			}
		}
		if(isGameStarted == false){
			
			//Display picture.
			//Change picture for each press of SPACE
			if(i ==1){
				displayIntro = intro2;
			}
			if(i ==2){
				displayIntro = intro3;
			}
			if(i ==3){
				displayIntro = intro4;
			}
			if(i ==4){
				displayIntro = intro5;
			}
			if(i ==5){
				displayIntro = intro6;
			}
			if(i ==6){
				displayIntro = intro7;
			}
			if(i ==7){
				displayIntro = intro8;
			}
			if(i ==8){
				displayIntro = intro9;
			}
			if(i ==9){
				isGameStarted = true;
			}
			g.drawImage(displayIntro, 0, 0, screenWidth, screenHeight, null); //Player
		}
	}
	public void actionPerformed(ActionEvent e){
		counter++;
		goalPointX = xBackground + 400 + newBeaconX;
		goalPointY = yBackground + 400 + newBeaconY;
		
		xBackground = xBackground + velX; //xBackground if background should move. xPlayer if player should move
		yBackground = yBackground + velY; //yBackground if background should move. yPlayer if player should move
		
		repaint();
		calculateDistanceTogoal();
		//if(isGoalReach == false && isBeacon3Reached == true){
		sendToUDP();
		//}else
		sendToDacUDP();
	}
	public void checkPlayerAtGoal(){
		if(isGoalReach == false && isBeacon3Reached == true && isBeacon2Reached == true && isBeacon1Reached == true){
			//set new position of beacon. 
			newBeaconX = 100;
			newBeaconY = 200;
			
			isGoalReach = true;
			System.out.println("GOAL reached");
			System.out.println("" + counter); //Display time when reached.
		}
		if(isBeacon3Reached == false && isBeacon2Reached == true && isBeacon1Reached == true){
			//set new position of beacon. 
			newBeaconX = 200;
			newBeaconY = 200;
			
			isBeacon3Reached = true;
			System.out.println("Beacon three reached");
			numberOfBeaconsReached +=1;
			System.out.println("" + counter); //Display time when reached.
		}
		if(isBeacon2Reached == false && isBeacon1Reached == true){
			//set new position of beacon. 
			newBeaconX = 1000;
			newBeaconY = 1000;
			
			isBeacon2Reached = true;
			System.out.println("Beacon two reached");
			numberOfBeaconsReached +=1;
			System.out.println("" + counter); //Display time when reached.
		}
		if(isBeacon1Reached == false){
			//set new position of beacon. 
			newBeaconX += 1500;
			newBeaconY += 100;
			
			isBeacon1Reached = true;
			System.out.println("Beacon one reached");
			numberOfBeaconsReached +=1;
			System.out.println("" + counter); //Display time when reached.
		}
	}
	
	public void keyPressed(KeyEvent e){
		int c = e.getKeyCode();
		//Player movement
		if(c == KeyEvent.VK_LEFT){ //Moving player left
			velX = 2;
			velY = 0;
			//Have to resize picture by swithing the width and height.
			playerWidth = 800; 
			playerHeight = 600;
			shipDisplay = shipLeft;
		}
		if(c == KeyEvent.VK_RIGHT){ //Moving player right
			velX = -2;
			velY = 0;
			playerWidth = 800; 
			playerHeight = 600;
			shipDisplay = shipRight;
		}
		if(c == KeyEvent.VK_UP){ ////Moving line up.. if wanted to move player up: velX = 0; velY = -1;
			velX = 0; 
			velY = 2;
			playerWidth = 600; 
			playerHeight = 800;
			shipDisplay = ship;
		}
		if(c == KeyEvent.VK_DOWN){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			velX = 0; 
			velY = -2;
			playerWidth = 600; 
			playerHeight = 800;
			shipDisplay = shipDown;
		}
		//ON/OFF show path
		if(c == KeyEvent.VK_ENTER){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			isVisible = true;
		}
		if(c == KeyEvent.VK_SPACE){
			i += 1;
		}
	}
	
	///////////////////////////////////UDP
	
	public void calculateDistanceTogoal(){
		//Take current length of line and substitute with prev. 
		//If longer decrease. else if shorter increase 
		double x_a = playerPointX-goalPointX;
		double y_a = playerPointY-goalPointY;
		distanceToGoal = Math.sqrt(Math.pow(x_a,2) + Math.pow(y_a,2));
		//System.out.println(distanceToGoal); //debug
	}
	
	public void sendToDacUDP(){
		
		DatagramSocket sock = null;
        int port = 2222;
         
        try
        {
            sock = new DatagramSocket(); 
            InetAddress host = InetAddress.getByName("localhost");
           
            if(xPlayer> goalPointX){ //player is right to the goal
            	s2 = "1";  //send one byte
            }
            if(xPlayer< goalPointX){ //player is left to the goal
            	s2 = "11";  //send two byte
            }
            if(goalPointX > xPlayer && goalPointX < xPlayer+playerWidth){ //player equal to goal
            	s2 = "111";  //send three byte
            }
            byte[] b = s2.getBytes();
            DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
            sock.send(dp);
        } 
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
	}
	
	public void sendToUDP(){
		//System.out.println(""+ distanceToGoal);
		//UDP
		DatagramSocket sock = null;
        int port = 1111;
		         
       try
       {
    	   sock = new DatagramSocket();           
    	   InetAddress host = InetAddress.getByName("localhost");
		           
		//make one for each distance interface. if distance>100 = all 10 cifre. if distance>90 = 9 cifre, if distance>80 = 8 cifre, if distance>70, if distance>60....
    	   if(distanceToGoal>50 && distanceToGoal <100){
    	   		s = "11111111";
    	   }
	       if(distanceToGoal>100 && distanceToGoal <150){
	        	s = "1111111";
	        }
	        if(distanceToGoal>150 && distanceToGoal <200){
		        s = "111111";
	        }
	        if(distanceToGoal>250 && distanceToGoal <300){
	        	s = "11111";
	        }
	        if(distanceToGoal>300 && distanceToGoal <350){
	        	s = "1111";
	        }
	       if(distanceToGoal>350 && distanceToGoal <400){
		       	s = "111";
	        }
	       if(distanceToGoal>400 && distanceToGoal <450){
		     	s = "11";
	        }
	        if(distanceToGoal>450 && distanceToGoal <500){
	        	s = "1";
	        }
		                
	        byte[] b = s.getBytes();            
	         DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
	         sock.send(dp);
       }         
       catch(IOException e)
       {
    	   System.err.println("IOException " + e);
       }
	}
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){
		//Stops the player movement when key released.
		velX = 0;
		velY = 0;
		//only visible while key is down
		isVisible = false;
	}
	public static void main(String[] args)
	{
		Controls t = new Controls();
		JFrame jf = new JFrame();
		jf.setTitle(""); //Displaying the title on the frame
		jf.setSize(screenWidth, screenHeight); //Setting size of frame (x,y);
		jf.setVisible(true); //Display the frame.
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		jf.add(t);
	}
}

