import java.net.*;
import java.io.*;

public class Executor {
	private Socket socket            = null; 
    private DataInputStream input   = null; 
    private DataOutputStream out     = null; 
    long start = System.currentTimeMillis();
    long end = start + 5*1000; 

    public Executor(String address, int port) {
    	try { 
            socket = new Socket(address, port); 
            System.out.println("Connected"); 
  
            // takes input from terminal 
            input = new DataInputStream(System.in); 
  
            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream()); 
        } 
        catch(UnknownHostException u) { 
            System.out.println(u); 
        } 
        catch(IOException i) { 
            System.out.println(i); 
        } 
        // string to read message from input 
        String line = ""; 

        // keep reading until "Over" is input 
        while (System.currentTimeMillis() < end) { 
            try { 
                line = input.readLine(); 
                out.writeUTF(line); 
            } 
            catch(IOException i) { 
                System.out.println(i); 
            } 
        } 
        // close the connection 
        try { 
            input.close(); 
            out.close(); 
            socket.close(); 
        } 
        catch(IOException i) { 
            System.out.println(i); 
        } 
    }

    public static void main(String args[]) { 
        Executor client = new Executor("127.0.0.1", 12000); 
    } 
}