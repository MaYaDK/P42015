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
	String s = ""; 
	public double distanceToGoal;
	public double bLength;
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
				g.drawImage(image2, xBackground-600, yBackground-600, backgroundWidth, backgroundHeight/4, null); //Background
				p.drawShip(g);
				//Position the line and calculation point to the middle of the ship/player.
				p.playerPointX = p.xPlayer+(p.playerWidth/2);
				p.playerPointY = p.yPlayer+(p.playerHeight/2);
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
					g.drawLine(p.playerPointX, p.playerPointY,d.goalPointX,d.goalPointY); 
				}
			}
			//If player has reached goal
			if(d.goalPointX > p.playerPointX-50 && d.goalPointX < p.playerPointX+50 && d.goalPointY > p.playerPointY-50 && d.goalPointY < p.yPlayer+p.playerPointY+50){
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
		System.out.println(d.min + ":" + d.sec + ":" + d.counter);
	}

	public void actionPerformed(ActionEvent e){
		if(screen.isGameStarted == true){
			d.timer();
		}
		d.goalPointX = xBackground + 400 + d.newBeaconX;
		d.goalPointY = yBackground + 400 + d.newBeaconY;
		
		xBackground = xBackground + velX; 
		yBackground = yBackground + velY; 
		p.checkPosition();
		repaint();
		calculateDistanceTogoal();
		sendToUDP();
		sendToDacUDP();
	}
	public void keyPressed(KeyEvent e){
		int c = e.getKeyCode();
		//Player movement
		if(c == KeyEvent.VK_LEFT){
			velX = 2;
			velY = 0;
			p.isLeft = true;
			//
			p.isRight = false;
			p.isUp = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_RIGHT){
			velX = -2;
			velY = 0;
			p.isRight = true;
			//
			p.isLeft = false;
			p.isUp = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_UP){
			velX = 0; 
			velY = 2;
			p.isUp = true;
			//
			p.isLeft = false;
			p.isRight = false;
			p.isDown = false;
		}
		if(c == KeyEvent.VK_DOWN){
			velX = 0; 
			velY = -2;
			p.isDown = true;
			//
			p.isLeft = false;
			p.isRight = false;
			p.isUp = false;
			
		}
		//ON/OFF show path
		if(c == KeyEvent.VK_ENTER){
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
	}
	public void sendToDacUDP(){
		DatagramSocket sock = null;
        int port = 3333;   
        try
        {
            sock = new DatagramSocket(); 
            InetAddress host = InetAddress.getByName("localhost");
            if(d.numberOfBeaconsReached >=3){
        			s = "1111";
            }
            byte[] b = s.getBytes();
            DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
            sock.send(dp);
            sock.close();
        } 
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
	}
	
	public void sendToUDP(){
		//UDP
		DatagramSocket sock = null;
		int port = 1111;         
		try
        {
    	   sock = new DatagramSocket();           
    	   InetAddress host = InetAddress.getByName("localhost");       
	        ByteBuffer i = ByteBuffer.allocate((int)distanceToGoal); //intensity
	        DatagramPacket  dp = new DatagramPacket(i.array(), i.array().length, host , port);
	        sock.send(dp);
	        sock.close();
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