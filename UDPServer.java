import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;
import java.util.*;

public class UDPServer {

    private DatagramSocket udpSocket;
    private int port;
    private ArrayList<Data> bloomberg = new ArrayList<>();
    
    public UDPServer(int port) throws SocketException, IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port);
        generateData();
    }
    private void listen() throws Exception {
        System.out.println("-- Running Bloomberg at " + InetAddress.getLocalHost() + "--");
        String msg;
        
        while (true) {
            
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            udpSocket.receive(packet);
            msg = new String(packet.getData()).trim();
            
            System.out.println(
                "Message from " + packet.getAddress().getHostAddress() + ": " + msg);

            byte[] message = bloomberg.get(0).toString().getBytes(); 
            bloomberg.remove(0);
            DatagramPacket first = new DatagramPacket(
                message, message.length, packet.getAddress(), packet.getPort());

            this.udpSocket.send(first);
        }
    }

    private void generateData() {
        Data data1 = new Data("0:00:00", 1.2, 1.224);
        Data data2 = new Data("0:00:01", 1.2, 1.2099);
        Data data3 = new Data("0:00:02", 1.205, 1.209);
        Data data4 = new Data("0:00:03", 1.207, 1.2095);
        Data data5 = new Data("0:00:04", 1.2095, 1.21);
        Data data6 = new Data("0:00:05", 1.2095, 1.2098);
        Data data7 = new Data("0:00:06", 1.209, 1.2105);
        Data data8 = new Data("0:00:07", 1.2087, 1.2104);
        Data data9 = new Data("0:00:08", 1.2089, 1.2103);
        Data data10 = new Data("0:00:09", 1.2084, 1.2106);
        Data data11 = new Data("0:00:10", 1.208, 1.21);
        Data data12 = new Data("0:00:11", 1.2079, 1.2099);
        Data data13 = new Data("0:00:12", 1.2093, 1.2103);
        Data data14 = new Data("0:00:13", 1.2099, 1.2115);
        Data data15 = new Data("0:00:14", 1.2043, 1.2074);
        Data data16 = new Data("0:00:15", 1.2071, 1.2112);
        Data data17 = new Data("0:00:16", 1.2107, 1.2111);
        Data data18 = new Data("0:00:17", 1.2089, 1.2126);
        Data data19 = new Data("0:00:18", 1.2341, 1.2348);
        Data data20 = new Data("0:00:19", 1.2244, 1.2249);
        bloomberg.add(data1);
        bloomberg.add(data2);
        bloomberg.add(data3);
        bloomberg.add(data4);
        bloomberg.add(data5);
        bloomberg.add(data6);
        bloomberg.add(data7);
        bloomberg.add(data8);
        bloomberg.add(data9);
        bloomberg.add(data10);
        bloomberg.add(data11);
        bloomberg.add(data12);
        bloomberg.add(data13);
        bloomberg.add(data14);
        bloomberg.add(data15);
        bloomberg.add(data16);
        bloomberg.add(data17);
        bloomberg.add(data18);
        bloomberg.add(data19);
        bloomberg.add(data20);
    }
    
    public static void main(String[] args) throws Exception {
        UDPServer client = new UDPServer(8001);
        client.listen();
    }
}