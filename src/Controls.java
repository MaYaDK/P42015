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
	public int send;
	//
	public double distanceToGoal;
	boolean isVisible = false;
	
	public int xBackground = 0, yBackground = 0, backgroundWidth = screenWidth*6, backgroundHeight = screenWidth*6, velX = 0, velY = 0; //Background
	
	public Controls()
	{
		tm.start(); //start timer
		addKeyListener(this); //Not interfering
		setFocusable(true);
		setFocusTraversalKeysEnabled(false); //Wont be using shift, tab.. keys
		try {
	        image2 = ImageIO.read(new File("src/water.jpg")); //Background
	          
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
				g.drawString("Beacon reached: "+ d.numberOfBeaconsReached, 350, 450);
				p.drawShip(g);
				//Display path only visible by key pressed A.
				if(isVisible == true){
					g.setColor(Color.RED);
					//Line distance to goal
					p.playerPointX = p.xPlayer;
					p.playerPointY = p.yPlayer;
					g.drawLine(p.playerPointX, p.playerPointY,d.goalPointX,d.goalPointY); //could be used to measure distance from player to goal
				}
			}
			//If player has reached goal
			if(d.goalPointX > p.xPlayer && d.goalPointX < p.xPlayer+p.playerWidth && d.goalPointY > p.yPlayer && d.goalPointY < p.yPlayer+p.playerHeight){
				d.checkPlayerAtGoal(); //method which checks which beacons is already reached.	
			}
		}
		screen.checker(g);
	}
	public void actionPerformed(ActionEvent e){
		//d.counter++;
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
		sendToUDP();
		sendToDacUDP();
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
		double x_a = p.playerPointX-d.goalPointX;
		double y_a = p.playerPointY-d.goalPointY;
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
           /*
            if(p.xPlayer> d.goalPointX){ //player is right to the goal
            	s2 = "1";  //send one byte
            }
            if(p.xPlayer< d.goalPointX){ //player is left to the goal
            	s2 = "11";  //send two byte
            }
            if(d.goalPointX > p.xPlayer && d.goalPointX < p.xPlayer+p.playerWidth){ //player equal to goal
            	s2 = "111";  //send three byte
            }
            */
            //byte[] b = send.getBytes();
            send = 100;
            final ByteArrayOutputStream baos=new ByteArrayOutputStream();
            final DataOutputStream daos=new DataOutputStream(baos);
            daos.writeInt(send);
            daos.close();
            final byte[] bytes=baos.toByteArray();
            DatagramPacket  dp = new DatagramPacket(bytes , bytes.length, host , port);
            //sock.send(dp);
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
		                
	        //byte[] b = distanceToGoal.g
	        ByteBuffer b = ByteBuffer.allocate(8);
	        b.putDouble(distanceToGoal);
	        //.putInt((int) distanceToGoal);
	        DatagramPacket  dp = new DatagramPacket(b.array() , b.array().length , host , port);
	         //DatagramPacket  dp = new DatagramPacket(distanceToGoal, b.length , host , port);
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

