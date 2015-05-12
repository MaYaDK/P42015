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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


//UDP
import java.net.*;
import java.nio.ByteBuffer;

public class Controls extends JPanel implements ActionListener, KeyListener{
	private BufferedImage image2; //BackGround
	
	public static int screenWidth = 1920, screenHeight = 1080-80;
	Timer tm = new Timer(5, this);
	Screens screen = new Screens();
	Player p = new Player();
	Destination d = new Destination();
	String s = ""; //No value = no sound, 10 highest
	String s2 = "";
	public double intensity;
	
	public double distanceToGoal;
	public double bLength;
	public double cLength;
	public double aLength;
	public double angle;
	public double degree;
	public double dB;
	boolean isVisible = false;
	
	public int xBackground = 0, yBackground = 0, backgroundWidth = screenWidth*6, backgroundHeight = screenWidth*6, velX = 0, velY = 0; //Background
	
	public Controls()
	{
		tm.start(); //start timer
		addKeyListener(this); //Not interfering
		setFocusable(true);
		setFocusTraversalKeysEnabled(false); //Wont be using shift, tab.. keys
		try {
	        image2 = ImageIO.read(new File("src/WaterThirdVersion.jpg")); //Background
	          
	    } catch (IOException ex) {
	            // handle exception...
	       }
	}

	//////////////////////////////////////////////GAMEPLAY
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(screen.isGameStarted == true){
			//If the game is not won, display game.
			if(screen.isGoalReach == false){
				g.fillRect(0,0,screenWidth,screenHeight);
				g.drawImage(image2, xBackground-600, yBackground-600, backgroundWidth, backgroundHeight, null); //Background
				//Beacons reached
				g.setColor(Color.WHITE);
				p.drawShip(g);
				//Position the line and calculation point to the middle of the ship/player.
				p.playerPointX = p.xPlayer+p.playerWidth/2;
				p.playerPointY = p.yPlayer+p.playerHeight/2;
				//Display path only visible by key pressed A.
				if(isVisible == true){
					//Line to calculate the angle. Always pointing upwards.
					//cLine
					g.setColor(Color.GREEN);
					g.drawLine(p.playerPointX, p.playerPointY, p.playerPointX, p.playerPointY-400);
					//aLine
					g.setColor(Color.BLUE);
					g.drawLine(d.goalPointX, d.goalPointY,p.playerPointX, p.playerPointY-400);
					//bLine/Line distance to goal/
					g.setColor(Color.RED);
					g.drawLine(p.playerPointX, p.playerPointY,d.goalPointX,d.goalPointY); //could be used to measure distance from player to goal
				}
			}
			//If player has reached goal
			if(d.goalPointX > p.xPlayer+p.playerWidth/2 && d.goalPointX < p.xPlayer+p.playerWidth/2+100 && d.goalPointY > p.yPlayer+p.playerHeight/2 && d.goalPointY < p.yPlayer+p.playerHeight/2+100){
				if(d.numberOfBeaconsReached == 3){
					destinationReached();
				}else
				d.checkPlayerAtGoal(); //method which checks which beacons is already reached.	
			}
		}
		screen.checker(g);
	}
	private void destinationReached() {
		screen.isGoalReach = true;
		d.newBeaconX += 1500; //Have to give new pos, so timer only prints one time.
		d.newBeaconY += 100;
		System.out.println("GOAL reached");
		System.out.println("Timer:"+ d.min + ":" + d.sec + ":" + d.counter);
		
	}

	public void actionPerformed(ActionEvent e){
		if(screen.isGameStarted == true){
			d.timer();
			//Debug
			//System.out.println("Timer:"+ d.min + ":" + d.sec + ":" + d.counter); //Display time when reached.
		}
		d.goalPointX = xBackground + 400 + d.newBeaconX;
		d.goalPointY = yBackground + 400 + d.newBeaconY;
		
		xBackground = xBackground + velX; //xBackground if background should move. xPlayer if player should move
		yBackground = yBackground + velY; //yBackground if background should move. yPlayer if player should move
		p.checkPosition();
		repaint();
		calculateDistanceTogoal();
		calculateIntensity();
		calcuteA();
		calcuteC();
		calculateAngle();
		calculateDB();
		//sendToUDP();
		sendToDacUDP();
		sendToUDP_degree();
		//System.out.println(""+distanceToGoal);
	}
	
	
	public void keyPressed(KeyEvent e){
		int c = e.getKeyCode();
		//Player movement
		if(c == KeyEvent.VK_LEFT){ //Moving player left
			velX = 2;
			velY = 0;
			p.isLeft = true;
			//
			p.isRight = false;
			p.isUp = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_RIGHT){ //Moving player right
			velX = -2;
			velY = 0;
			p.isRight = true;
			//
			p.isLeft = false;
			p.isUp = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_UP){ ////Moving line up.. if wanted to move player up: velX = 0; velY = -1;
			velX = 0; 
			velY = 2;
			p.isUp = true;
			//
			p.isLeft = false;
			p.isRight = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_DOWN){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			velX = 0; 
			velY = -2;
			p.isDown = true;
			//
			p.isLeft = false;
			p.isRight = false;
			p.isUp = false;
			
		}
		//ON/OFF show path
		if(c == KeyEvent.VK_ENTER){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			isVisible = true;
		}
		if(c == KeyEvent.VK_SPACE){
			screen.i += 1;
		}
	}
	
	///////////////////////////////////UDP
	
	public void calculateDistanceTogoal(){
		//Take current length of line and substitute with prev. 
		//If longer decrease. else if shorter increase 
		double x_b = p.playerPointX-d.goalPointX;
		double y_b = p.playerPointY-d.goalPointY;
		distanceToGoal = Math.sqrt(Math.pow(x_b,2) + Math.pow(y_b,2));
		bLength = distanceToGoal;
		//System.out.println(distanceToGoal); //debug
	}
	public void calcuteC(){
		//p.playerPointX, p.playerPointY, p.playerPointX, p.playerPointY-1000)
		double x_c = p.playerPointX-p.playerPointX;
		double y_c = p.playerPointY-p.playerPointY-400;
		cLength = Math.sqrt(Math.pow(x_c,2) + Math.pow(y_c,2));
	}
	
	public void calcuteA(){
		double x_a = p.playerPointX-d.goalPointX;
		double y_a = (p.playerPointY-400)-d.goalPointY;
		aLength = Math.sqrt(Math.pow(x_a,2) + Math.pow(y_a,2));
	}
	public void calculateIntensity(){
		//intensity = 1/Math.pow(distanceToGoal,2);
		//System.out.println(""+intensity);
		//intensity = (70*Math.pow(distanceToGoal/8, 2))/Math.pow(283, 2);
		
		
		//= P/(4*pi*d^2)
				double p = 10000;
		intensity = p/(4*Math.PI*(Math.pow((distanceToGoal-50)/8,2)));
		//System.out.println(distanceToGoal);
		//System.out.println("dB"+ intensity);
	}

	public void calculateAngle(){
		degree = Math.acos(angle)*(180/Math.PI);
		angle = ((Math.pow(bLength,2) + Math.pow(cLength,2)-Math.pow(aLength,2))/(2*bLength*cLength));
		//System.out.println("Degree"+degree);
	}
	public void calculateDB(){
		
		if(degree>90){
			degree = (degree-180)*-1;
		}
		if(p.playerPointX > d.goalPointX){ //player is right to the goal playerPointX, playerPointY
			degree = (degree)*-1;
		}
		//degree = (degree+90)/180;
		degree = degree+90; //This gave the angle value from 0 to 1.
		
		//System.out.println(degree);
	}
	public void sendToDacUDP(){
		DatagramSocket sock = null;
        int port = 3333;   
        try
        {
            sock = new DatagramSocket(); 
            InetAddress host = InetAddress.getByName("localhost");
           
            if(p.xPlayer > d.goalPointX){ //player is right to the goal
            	if(d.numberOfBeaconsReached >=3){
        			s2 = "1111";
            	}else 
            	s2 = "1";  //send one byte
            }
            if(p.xPlayer < d.goalPointX){ //player is left to the goal
            	if(d.numberOfBeaconsReached >=3){
            		s2 = "11111";
            	}else
            	
            	s2 = "11";  //send two byte
            }
            if(d.goalPointX > p.xPlayer && d.goalPointX < p.xPlayer+p.playerWidth){ //player equal to goal
            	if(d.numberOfBeaconsReached >=3){
            	s2 = "111111";
        	}else
            	s2 = "111";  //send three byte
            }
            byte[] b = s2.getBytes();
            DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
            sock.send(dp);
            sock.close();
        } 
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
	}
	/*
	public void sendToUDP(){
		//UDP
		DatagramSocket sock = null;
		int port = 1111;         
		try
        {
    	   sock = new DatagramSocket();           
    	   InetAddress host = InetAddress.getByName("localhost");
		           
	        ByteBuffer i = ByteBuffer.allocate((int)distanceToGoal);
	        DatagramPacket  dp = new DatagramPacket(i.array(), i.array().length, host , port);
	        sock.send(dp);
	        //System.out.println((int) distanceToGoal);
       }         
       catch(IOException e)
       {
    	   System.err.println("IOException " + e);
       }
	}
	*/
	
	public void sendToUDP_degree(){
		//UDP
		int port = 2222;         
		try
        {
			DatagramSocket socket = new DatagramSocket();           
    	   InetAddress host = InetAddress.getByName("localhost");      
	        ByteBuffer i = ByteBuffer.allocate((int)degree);
	        DatagramPacket  dp = new DatagramPacket(i.array(), i.array().length, host , port);
	        socket.send(dp);
	        socket.close();
	       // System.out.println((int) distanceToGoal);
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

