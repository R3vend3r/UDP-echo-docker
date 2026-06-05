import java.net.*;

public class UDPEchoClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress address = InetAddress.getByName(host);

        System.out.println("UDP Echo Client connect in " + host + ":" + port);
        System.out.println("Sending a test message every 5 second...");

        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
           System.out.println("Shutting down......");
            socket.close();
        }));

        int counter = 1;
        while (true) {
            String message = "Test message " + counter;
            byte[] buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            System.out.println("Sending: " + message);

            try {
                byte[] response = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(response, response.length);
                socket.receive(responsePacket);
                System.out.println("Received echo: " + new String(responsePacket.getData(), 0, responsePacket.getLength()));

            } catch (SocketTimeoutException e)  {
                System.err.println("Waiting timeout(5s)");
            }

            counter++;
            Thread.sleep(5000);
        }
    }
}