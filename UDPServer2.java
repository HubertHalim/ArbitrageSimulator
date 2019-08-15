import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer2 {

    private DatagramSocket udpSocket;
    private int port;
 
    public UDPServer2(int port) throws SocketException, IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port);
    }
    private void listen() throws Exception {
        System.out.println("-- Running Reuters at " + InetAddress.getLocalHost() + "--");
        String msg;
        
        while (true) {
            
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            udpSocket.receive(packet);
            msg = new String(packet.getData()).trim();
            
            System.out.println(
                "Message from " + packet.getAddress().getHostAddress() + ": " + msg);

            byte[] message = "from second".getBytes(); 
            DatagramPacket second = new DatagramPacket(
                message, message.length, packet.getAddress(), packet.getPort());
            this.udpSocket.send(second);
        }
    }
    
    public static void main(String[] args) throws Exception {
        UDPServer2 client = new UDPServer2(8002);
        client.listen();
    }
}