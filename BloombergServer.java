import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;

public class BloombergServer {

    private DatagramSocket udpSocket;
    private int port;
    private ArrayList<Data> bloomberg = new ArrayList<>();
    private int ttl = 3;
    private int cur = 0;
    private HashSet<Integer> bought = new HashSet<>();
    
    public BloombergServer(int port) throws SocketException, IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port);
    }
    private void listen() throws Exception {
        System.out.println("-- Running Bloomberg at " + InetAddress.getLocalHost() + "--");
        String msg;
        
        while (cur < bloomberg.size() + ttl - 1) {
            
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            udpSocket.receive(packet);
            msg = new String(packet.getData()).trim();
            String[] split = msg.split("-");
            String reply = "";
            System.out.println(
                "[" + System.currentTimeMillis() + "]" + " Request from " + 
                packet.getAddress().getHostAddress() + ": " + msg);
            if (split[0].charAt(0) == 'b') {
                int num = Integer.parseInt(split[1]);
                if (cur - ttl + 1 > num) {
                    reply = "fail";
                    System.out.println("[" + System.currentTimeMillis() + "]" + " Buy failed, expired " + (cur-ttl+1-num) + " ago" );
                } else {
                    reply = "success";
                    System.out.println("[" + System.currentTimeMillis() + "]" + " Buy successful");
                }
            } else if (split[0].charAt(0) == 's') {
                int num = Integer.parseInt(split[1]);
                if (cur - ttl + 1 > num) {
                    reply = "fail";
                    System.out.println("[" + System.currentTimeMillis() + "]" + " Sell failed, expired " + (cur-ttl+1-num) + " ago");
                } else {
                    reply = "success";
                    System.out.println("[" + System.currentTimeMillis() + "]" + " Sell successful");
                }
            } else {
                reply = "nope";
                System.out.println("[" + System.currentTimeMillis() + "]" + " Do nothing");
            }

            System.out.println("--------------------------------");
            byte[] rep = reply.getBytes();
            DatagramPacket resp = new DatagramPacket(
                rep, rep.length, packet.getAddress(), packet.getPort());
            this.udpSocket.send(resp);

            byte[] message;
            if (cur < bloomberg.size()) {
                message = bloomberg.get(cur).toString().getBytes(); 
            } else {
                message = "None".getBytes();
            }
            DatagramPacket first = new DatagramPacket(
                message, message.length, packet.getAddress(), packet.getPort());
            cur++;
            this.udpSocket.send(first);
        }
    }

    private ArrayList<Data> readDataFromCsv(String file) {
        ArrayList<Data> data = new ArrayList<>();
        Path pathToFile = Paths.get(file);
        try (
            BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.UTF_8)) {

            String line = br.readLine();
            while (line != null) {

                String[] attributes = line.split(",");
                attributes[0] = attributes[0].trim();
                if (attributes[0].charAt(0) == '\uFEFF') {
                    attributes[0] = attributes[0].substring(1);
                }
                data.add(new Data(attributes[0], Double.parseDouble(attributes[3]), Double.parseDouble(attributes[2]), 3));
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return data;
    }

    private void generateData(String file) {
        bloomberg = readDataFromCsv(file);
    }
    
    public static void main(String[] args) throws Exception {
        BloombergServer client = new BloombergServer(8001);
        client.generateData(args[0]);
        client.listen();
        System.out.println("[" + System.currentTimeMillis() + "]" + " No more data");
    }
}