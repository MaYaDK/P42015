/**
    Java ECHO client with UDP sockets example
    Silver Moon (m00n.silv3r@gmail.com)
*/
 
import java.io.*;
import java.net.*;


 
public class udp_client
{
	//Get the control class, position of rect.
	Controls c = new Controls();
	//public int x = c.xRect;
	
    public static void main(String args[])
    {
        DatagramSocket sock = null;
        int port = 1111;
        String s; //Make to int? //No value = no sound, 10 highest
        int x = 100; //x = distance to point. 
        System.out.println(""); //+x //for debugging
         
        try
        {
            sock = new DatagramSocket();
             
            InetAddress host = InetAddress.getByName("localhost");
            
            if(x >= 100){ //make one for each distance interface. if distance>100 = all 10 cifre. if distance>90 = 9 cifre, if distance>80 = 8 cifre, if distance>70, if distance>60....
                s = "10";
                
                byte[] b = s.getBytes();
                 
                DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
                sock.send(dp);
            }
        }
         
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
    }
    
}