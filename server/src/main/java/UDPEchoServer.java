import java.net.*;

public class UDPEchoServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(8080);
        byte[] buffer = new byte[1024];
        System.out.println("UDP Echo Server on port 8080");

        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            System.out.println("Shutting down......");
            socket.close();
        }));

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            socket.send(new DatagramPacket(packet.getData(), packet.getLength(),
                    packet.getAddress(), packet.getPort()));
            System.out.println("Echoed " + packet.getLength() + " bytes");
        }
    }
}