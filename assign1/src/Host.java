import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class Host {
    private static final Logger LOG = Logger.getLogger("host");
    private static final int BUFFER_SIZE = 128;
    private DatagramSocket receiveSocket, sendReceiveSocket;
    private InetAddress address;
    private int serverPort;

    public Host(String ip, int hostPort, int serverPort) {
        this.serverPort = serverPort;

        try {
            address = InetAddress.getByName(ip);
            receiveSocket = new DatagramSocket(hostPort, address);
            sendReceiveSocket = new DatagramSocket();
        } catch (IOException e) {
            LOG.severe("Could not create host sockets: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public void run() {
        while(true) {
            int clientPort = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            try {
                receiveSocket.receive(receivePacket);
                clientPort = receivePacket.getPort();
            } catch (IOException e) {
                LOG.severe("Could not receive data packet from client: " + e.getLocalizedMessage());
                System.exit(1);
            }

            LOG.info("Received from " + receivePacket.getAddress().toString() + " data: " +
                new String(receivePacket.getData()) + " (Byte form " +  Arrays.toString(receivePacket.getData()) + ")");

            LOG.info("Preparing to send data to server...");

            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, serverPort);
            sendPacket.setData(receivePacket.getData());

            try {
                sendReceiveSocket.send(sendPacket);
                LOG.info("Data sent to server. Waiting for response...");
            } catch (IOException e) {
                LOG.severe("Could not send data packet to server: " + e.getLocalizedMessage());
                System.exit(1);
            }

            buffer = new byte[BUFFER_SIZE];
            try {
                receivePacket = new DatagramPacket(buffer, buffer.length);
                sendReceiveSocket.receive(receivePacket);
                LOG.info("Response received from server containing: " + Arrays.toString(receivePacket.getData()));
            } catch (IOException e) {
                LOG.severe("Could not receive data packet from server: " + e.getLocalizedMessage());
                System.exit(1);
            }

            try {
                sendPacket = new DatagramPacket(buffer, buffer.length, address, clientPort);
                sendPacket.setData(receivePacket.getData());
                sendReceiveSocket.send(sendPacket);
                LOG.info("Sent data to client.");
            } catch (IOException e) {
                LOG.severe("Could not send data packet to client: " + e.getLocalizedMessage());
                System.exit(1);
            }
        }
    }

    public static void main(String args[]) {
        Host host = new Host("localhost", 68, 69);
        host.run();
    }
}
