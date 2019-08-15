import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Executor {
	private Socket socket            = null; 
    private DataInputStream input   = null; 
    private DataOutputStream out     = null; 
    private DataInputStream fromServer = null;
    long start = System.currentTimeMillis();
    long end = start + 5*1000; 

    Runnable helloRunnable = new Runnable() {
	    public void run() {
	    	System.out.println("time now " + System.currentTimeMillis());
	        try { 
	        	String line = "";
            	line = Long.toString(System.currentTimeMillis());
            	byte[] message = "hello\r\n".getBytes();
                out.write(message); 
                System.out.println("done sending");
                // byte[] data = new byte[100];
                String data = fromServer.readLine();
                // String ans = Base64.getEncoder().encodeToString(data);
                System.out.println(data);
            } 
            catch(IOException i) { 
                System.out.println(i); 
            } 
	    }
	};

    public Executor(String address, int port) {
    	try { 
            socket = new Socket(address, port); 
            System.out.println("Connected"); 
  
            // takes input from terminal 
            input = new DataInputStream(System.in); 
  			fromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream()); 
        } 
        catch(UnknownHostException u) { 
            System.out.println(u); 
        } 
        catch(IOException i) { 
            System.out.println(i); 
        } 
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        // string to read message from input 
        // String line = ""; 

        executor.scheduleAtFixedRate(helloRunnable, 0, 1, TimeUnit.SECONDS);
        if (System.currentTimeMillis() > end) { 
            // try { 
            // 	line = Long.toString(System.currentTimeMillis());
            //     out.writeUTF(line); 
            //     System.out.println("done sending");
            //     String ans = fromServer.readUTF();
            //     System.out.println(ans);
            // } 
            // catch(IOException i) { 
            //     System.out.println(i); 
            // }
            // close the connection 
        	System.out.println("done");
	        try { 
	            input.close(); 
	            out.close(); 
	            socket.close();
	        } 
	        catch(IOException i) { 
	            System.out.println(i); 
	        }  
	        System.exit(0);
        } 
    }

    public static void main(String args[]) { 
        Executor client = new Executor("127.0.0.1", 5000); 
    } 
}