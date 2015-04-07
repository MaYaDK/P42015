/**
    Java ECHO server with UDP sockets example
    Silver Moon (m00n.silv3r@gmail.com)
*/
 
import java.io.*;
import java.net.*;
 
public class udp_server
{
    public static void main(String args[])
    {
        DatagramSocket sock = null;
         
        try
        {
            //1. creating a server socket, parameter is local port number
            sock = new DatagramSocket(8888); //4444 = PD file, 7777 = linked to udp java client
             
            //buffer to receive incoming data
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
             
            //2. Wait for an incoming data
            echo("Server socket created. Waiting for incoming data...");
             
            //communication loop
            while(true)
            {
                sock.receive(incoming);
                byte[] data = incoming.getData();
                String s = new String(data, 0, incoming.getLength());
                 
                //echo the details of incoming data - client ip : client port - client message
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                 
                s = "OK : " + s;
                DatagramPacket dp = new DatagramPacket(s.getBytes() , s.getBytes().length , incoming.getAddress() , incoming.getPort());
                sock.send(dp);
            }
        }
         
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
    }
     
    //simple function to echo data to terminal
    public static void echo(String msg)
    {
        System.out.println(msg);
    }
}