import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;

public class Executor {
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    private Scanner scanner;
    private int cur = 0;
    private ArrayList<Data> bloomberg = new ArrayList<>();
    private ArrayList<Data> ebs = new ArrayList<>();
    private ArrayList<Data> reuters = new ArrayList<>();
    private final int BLOOMBERGTTL = 3;
    private final int REUTERSTTL = 4;
    private final int EBSTTL = 5;
    private double profit = 0;

    private Executor(String destinationAddr, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(destinationAddr);
        this.port = port;
        udpSocket = new DatagramSocket(this.port);
        scanner = new Scanner(System.in);
    }

    // private List<Pairing> getAllPair() {
    //     // return [["b", "00:00:01", "r", "00:00:02", "0.001"],["b", "00:00:03", "r", "00:00:04", "0.0015"]]
    // }

    private Pairing getBestPair() {
        int highb = 0;
        int highr = 0;
        int highe = 0;
        int lowb = 0;
        int lowr = 0;
        int lowe = 0;
        for (int i = 1; i < bloomberg.size(); i++) {
            if (bloomberg.get(i).sell > bloomberg.get(highb).sell) {
                highb = i;
            }
            if (bloomberg.get(i).buy < bloomberg.get(lowb).buy) {
                lowb = i;
            }
        }
        for (int i = 1; i < reuters.size(); i++) {
            if (reuters.get(i).sell > reuters.get(highr).sell) {
                highr = i;
            }
            if (reuters.get(i).buy < reuters.get(lowr).buy) {
                lowr = i;
            }
        }
        for (int i = 1; i < ebs.size(); i++) {
            if (ebs.get(i).sell > ebs.get(highe).sell) {
                highe = i;
            }
            if (ebs.get(i).buy < ebs.get(lowe).buy) {
                lowe = i;
            }
        }
        PriorityQueue<Pairing> que = new PriorityQueue<>();
        que.add(new Pairing("b", bloomberg.get(lowb).timeStamp, "r", reuters.get(highr).timeStamp, reuters.get(highr).sell - bloomberg.get(lowb).buy));
        que.add(new Pairing("b", bloomberg.get(lowb).timeStamp, "e", ebs.get(highe).timeStamp, reuters.get(highr).sell - bloomberg.get(lowb).buy));
        que.add(new Pairing("r", reuters.get(lowr).timeStamp, "b", bloomberg.get(highb).timeStamp, bloomberg.get(highb).sell - reuters.get(lowr).buy));
        que.add(new Pairing("r", reuters.get(lowr).timeStamp, "e", ebs.get(highe).timeStamp, ebs.get(highe).sell - reuters.get(lowr).buy));
        que.add(new Pairing("e", ebs.get(lowe).timeStamp, "b", bloomberg.get(highb).timeStamp, bloomberg.get(highb).sell - ebs.get(lowe).buy));
        que.add(new Pairing("e", ebs.get(lowe).timeStamp, "r", reuters.get(highr).timeStamp, reuters.get(highr).sell - ebs.get(lowe).buy));
        Pairing chosen = que.poll();
        System.out.println("best pair : " + chosen);
        if (chosen.profit < 0) {
            return null;
        }
        if (chosen.buyC == "b") {
            for (int i = 0; i < bloomberg.size(); i++) {
                if (chosen.buyT == bloomberg.get(i).timeStamp) {
                    bloomberg.remove(i);
                    break;
                }
            }
        } else if (chosen.buyC == "r") {
            for (int i = 0; i < reuters.size(); i++) {
                if (chosen.buyT == reuters.get(i).timeStamp) {
                    reuters.remove(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < ebs.size(); i++) {
                if (chosen.buyT == ebs.get(i).timeStamp) {
                    ebs.remove(i);
                    break;
                }
            }
        }

        return chosen;
    }

    private void cleanUp() {
        int remove = 0;
        for (int i = bloomberg.size() - 1; i >= 0; i--) {
            bloomberg.get(i).ttl--;
            if (bloomberg.get(i).ttl == 0) {
                remove++;
                bloomberg.remove(i);
            }
        } 
        for (int i = reuters.size() - 1; i >= 0; i--) {
            reuters.get(i).ttl--;
            if (reuters.get(i).ttl == 0) {
                remove++;
                reuters.remove(i);
            }
        } 
        for (int i = ebs.size() - 1; i >= 0; i--) {
            ebs.get(i).ttl--;
            if (ebs.get(i).ttl == 0) {
                remove++;
                ebs.remove(i);
            }
        } 
        System.out.println("[" + System.currentTimeMillis() + "]" + " Done cleaning, removed " + remove);
    }

    Runnable helloRunnable = new Runnable() {
        public void run() {
            cleanUp();
            Pairing bestPair = null;
            String one = "n";
            String two = "n";
            String three = "n";
            if (cur != 0) {
                bestPair = getBestPair();
            }
            if (bestPair != null && cur != 0) {
                String[] buyT = bestPair.buyT.split(":");
                String[] sellT = bestPair.sellT.split(":");
                int buyIndex = Integer.parseInt(buyT[0])*3600 + Integer.parseInt(buyT[1])*60 + Integer.parseInt(buyT[2]);
                int sellIndex = Integer.parseInt(sellT[0])*3600 + Integer.parseInt(sellT[1])*60 + Integer.parseInt(sellT[2]);
                if (bestPair.buyC == "b") {
                    one = "b-" + buyIndex;
                } else if (bestPair.buyC == "r") {
                    two = "b-" + buyIndex;
                } else {
                    three = "b-" + buyIndex;
                }
                if (bestPair.sellC == "b") {
                    one = "s-" + sellIndex;
                } else if (bestPair.sellC == "r") {
                    two = "s-" + sellIndex;
                } else {
                    three = "s-" + sellIndex;
                }
            }
            byte[] message1 = one.getBytes();
            byte[] message2 = two.getBytes();
            byte[] message3 = three.getBytes();
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
                int suc = 0;
                String[] splits;
                // Send first 
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(first); 
                udpSocket.receive(packet);
                String resp = new String(packet.getData()).trim();
                if (resp.equals("success")) {
                    suc++;
                }
                System.out.println("[" + System.currentTimeMillis() + "]" + " From bloomberg status : " + resp);
                buf = new byte[2048];
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.receive(packet);
                String msg = new String(packet.getData()).trim();
                System.out.println("[" + System.currentTimeMillis() + "]" + " From bloomberg data : " + msg);
                if (!msg.equals("None")) {    
                    splits = msg.split(" ");
                    bloomberg.add(new Data(splits[0], Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), 3));
                }
                buf = new byte[2048];

                // send secnond
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(second);
                udpSocket.receive(packet);
                resp = new String(packet.getData()).trim();
                if (resp.equals("success")) {
                    suc++;
                }
                System.out.println("[" + System.currentTimeMillis() + "]" + " From reuters status : " + resp);
                buf = new byte[2048];
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.receive(packet);
                msg = new String(packet.getData()).trim();
                System.out.println("[" + System.currentTimeMillis() + "]" + " From reuters data : " + msg);
                if (!msg.equals("None")) {    
                    splits = msg.split(" ");
                    reuters.add(new Data(splits[0], Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), 4));
                }
                buf = new byte[2048];

                // send third
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.send(third);
                udpSocket.receive(packet);
                resp = new String(packet.getData()).trim();
                if (resp.equals("success")) {
                    suc++;
                }
                System.out.println("[" + System.currentTimeMillis() + "]" + " From ebs status : " + resp);
                buf = new byte[2048];
                packet = new DatagramPacket(buf, buf.length);
                udpSocket.receive(packet);
                msg = new String(packet.getData()).trim();
                System.out.println("[" + System.currentTimeMillis() + "]" + " From ebs data : " + msg);
                if (!msg.equals("None")) {        
                    splits = msg.split(" ");
                    ebs.add(new Data(splits[0], Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), 5));
                }

                if (suc == 2) {
                    profit = profit + bestPair.profit;
                }
                System.out.print("[" + System.currentTimeMillis() + "]" + " Current Profit: ");
                System.out.println(String.format("%.5f", profit));
                System.out.println("--------------------------------------");
                cur++;
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    };

    private void start() {
        helloRunnable.run();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 1, 1, TimeUnit.SECONDS);
    }

    private void handleInput() {
    }

    public static void main(String[] args) throws NumberFormatException, IOException {        
        Executor sender = new Executor("", 8000);
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.start();
    }
}