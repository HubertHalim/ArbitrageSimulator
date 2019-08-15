import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.*;
public class UDPClient {
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    private Scanner scanner;
    private UDPClient(String destinationAddr, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(destinationAddr);
        this.port = port;
        udpSocket = new DatagramSocket(this.port);
        scanner = new Scanner(System.in);
    }

    Runnable helloRunnable = new Runnable() {
        public void run() {
            System.out.println("time now " + System.currentTimeMillis());
        
            String line = "";
            line = Long.toString(System.currentTimeMillis());
            byte[] message1 = "hello".getBytes();
            byte[] message2 = "hola".getBytes();
            byte[] message3 = "aloha".getBytes();
            DatagramPacket first = new DatagramPacket(
                message1, message1.length, serverAddress, 8001);
            DatagramPacket second = new DatagramPacket(
                message2, message2.length, serverAddress, 8002);
            DatagramPacket third = new DatagramPacket(
                message3, message3.length, serverAddress, 8003);
            // System.out.println(data);
            try {
                byte[] buf = new byte[2048];
                DatagramPacket packet = null;

                // Send first 
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(first); 
                udpSocket.receive(packet);
                String msg = new String(packet.getData()).trim();
                System.out.println("From first : " + msg);

                // send secnond
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(second);
                udpSocket.receive(packet);
                msg = new String(packet.getData()).trim();
                System.out.println("From second : " + msg);

                // send third
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(third);
                udpSocket.receive(packet);
                msg = new String(packet.getData()).trim();
                System.out.println("From third : " + msg);

            } catch (IOException i) {
                System.out.println(i);
            }
        }
    };

    private void start() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 1, TimeUnit.SECONDS);
    }

    private void handleInput() {
    }

    public static void main(String[] args) throws NumberFormatException, IOException {        
        UDPClient sender = new UDPClient("", 8000);
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.start();
    }
}