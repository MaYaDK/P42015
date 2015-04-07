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

//UDP
import java.io.*;
import java.net.*;

public class Controls extends JPanel implements ActionListener, KeyListener{
	Timer tm = new Timer(5, this);
	//Player variables
	int xPlayer = 0, yPlayer = 400, playerWidth = 50, playerHeight = 50, velX = 0, velY = 0;
	//Line from player to goal
	int playerPointX, playerPointY, goalPointX = 250, goalPointY = 0;
	//Path. p = path
	int p1PosX = 50, p1PosY = 250, p1Width = 50, p1Height = 250, p2PosX =50, p2PosY = 250, p2Width = 250, p2Height = 50, p3PosX = 250, p3PosY = 0, p3Width = 50, p3Height = 250;
	String s = ""; //Make to int? //No value = no sound, 10 highest
	
	
	double distanceToGoal;
	boolean isOnPath=false;
	boolean isVisible = false;
	
	public Controls()
	{
		tm.start(); //start timer
		addKeyListener(this); //Not interfering
		setFocusable(true);
		setFocusTraversalKeysEnabled(false); //Wont be using shift, tab.. keys
	}

	//////////////////////////////////////////////GAMEPLAY
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		//Background
		g.setColor(Color.BLACK);
		g.fillRect(0,0,500,500);
		
		//Display path only visible by key pressed A.
		g.setColor(Color.BLACK);
		if(isVisible == true){
			g.setColor(Color.RED);
		}
		g.fillRect(p1PosX, p1PosY, p1Width, p1Height);
		g.fillRect(p2PosX, p2PosY, p2Width, p2Height);
		g.fillRect(p3PosX, p3PosY, p3Width, p3Height);
		
		playerPointX = xPlayer;
		playerPointY = yPlayer;
		g.drawLine(playerPointX, playerPointY,goalPointX,goalPointY); //could be used to measure distance from player to goal
		//Display player
		g.setColor(Color.BLUE);
		g.fillRect(xPlayer,yPlayer,playerWidth,playerHeight);
	}
	public void actionPerformed(ActionEvent e){
		xPlayer = xPlayer + velX;
		yPlayer = yPlayer + velY;
		repaint();
		calculateDistanceTogoal();
		checkingOnPath();
		sendToUDP();
		
	}
	public void keyPressed(KeyEvent e){
		int c = e.getKeyCode();
		//Player movement
		if(c == KeyEvent.VK_LEFT){ //Moving player left
			velX = -1;
			velY = 0;
		}
		if(c == KeyEvent.VK_RIGHT){ //Moving player right
			velX = 1;
			velY = 0;
		}
		if(c == KeyEvent.VK_UP){ ////Moving line up.. if wanted to move player up: velX = 0; velY = -1;
			velX = 0; 
			velY = -1;
		}
		if(c == KeyEvent.VK_DOWN){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			velX = 0; 
			velY = 1;
		}
		//ON/OFF show path
		if(c == KeyEvent.VK_ENTER){ ////Moving line down. if wanted to move player up: velX = 0; velY = 1;
			isVisible = true;
		}
	}
	
	
	
	
	///////////////////////////////////UPD
	
	
	public void checkingOnPath(){
		if(xPlayer>p1PosX && xPlayer<p1PosX+p1Width && yPlayer>p1PosY && yPlayer<p1PosY+p1Height){
			System.out.println("isOnPath p1");
			isOnPath = true;
		}
		if(xPlayer>p2PosX && xPlayer<p2PosX+p2Width && yPlayer>p2PosY && yPlayer<p2PosY+p2Height){
			System.out.println("isOnPath p2");
			isOnPath = true;
		}
		if(xPlayer>p3PosX && xPlayer<p3PosX+p3Width && yPlayer>p3PosY && yPlayer<p3PosY+p3Height){
			System.out.println("isOnPath p3");
			isOnPath = true;
		}
			
	}
	public void calculateDistanceTogoal(){
		//Take current lenght of line and substitute with prev. 
		//If longer decrease. else if shorter increase 
		double x_a = playerPointX-goalPointX;
		double y_a = playerPointY-goalPointY;
		distanceToGoal = Math.sqrt(Math.pow(x_a,2) + Math.pow(y_a,2));
		System.out.println(distanceToGoal); //debug
	}
	
	//if on path and distancetogoal is decreasing, play sound 
	
	public void sendNoSoundUDP(){
		/*
		DatagramSocket sock = null;
        int port = 1111;
        
        System.out.println(yRect); //+x //for debugging
         
        try
        {
            sock = new DatagramSocket();
             
            InetAddress host = InetAddress.getByName("localhost");
           
            //if(isOnPath == true){ //make one for each distance interface. if distance>100 = all 10 cifre. if distance>90 = 9 cifre, if distance>80 = 8 cifre, if distance>70, if distance>60....
           
            System.out.println("Sending no sound UDP"); //debug
            if(isOnPath == false){
            	s = "";
              	System.out.println("Not on");
            }
            byte[] b = s.getBytes();
            DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
            sock.send(dp);
        }
         
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
        */
	}
	
	public void sendToUDP(){
		//UDP
		DatagramSocket sock = null;
        int port = 1111;
		         
       try
       {
    	   sock = new DatagramSocket();
		             
    	   InetAddress host = InetAddress.getByName("localhost");
		           
		            //if(isOnPath == true){ //make one for each distance interface. if distance>100 = all 10 cifre. if distance>90 = 9 cifre, if distance>80 = 8 cifre, if distance>70, if distance>60....
		                
		        
		//if(isOnPath == true){
    	   if(distanceToGoal>50 && distanceToGoal <100){
    	   		s = "111111111";
    	   }
	       if(distanceToGoal>100 && distanceToGoal <150){
	        	s = "11111111";
	        }
	        if(distanceToGoal>150 && distanceToGoal<200){
		        	s = "1111111";
	        }
	        if(distanceToGoal>250 && distanceToGoal<300){
	        	s = "111111";
	        }
	        if(distanceToGoal>300 && distanceToGoal <350){
	        	s = "11111";
	        }
	       if(distanceToGoal>350 && distanceToGoal<400){
		       	s = "1111";
	        }
	       if(distanceToGoal>400 && distanceToGoal<450){
		     	s = "111";
	        }
	        if(distanceToGoal>450 && distanceToGoal<500){ // && isOnPath == true
	        	s = "11";
	        }
		//}
		if(isOnPath = false){
			System.out.println("no sound");
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
		jf.setSize(500, 500); //Setting size of frame (x,y);
		jf.setVisible(true); //Display the frame.
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		jf.add(t);
		
	}
}

