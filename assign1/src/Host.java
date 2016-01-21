import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Intermediate host that connects to the client and sends the requests
 * to the server. Holds two sockets, one for the client and one for
 * the server.
 *
 * @author Nicolas McCallum #100936816
 */
public class Host {
    private static final Logger LOG = Logger.getLogger("host");
    private static final int BUFFER_SIZE = 128;
    private DatagramSocket receiveSocket, sendReceiveSocket;
    private InetAddress address;
    private int serverPort;

    public Host(String ip, int hostPort, int serverPort) {
        this.serverPort = serverPort;

        // Obtain the ip address and create the sockets needed for client and server
        try {
            address = InetAddress.getByName(ip);
            receiveSocket = new DatagramSocket(hostPort, address);
            sendReceiveSocket = new DatagramSocket();
        } catch (IOException e) {
            LOG.severe("Could not create host sockets: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Continuously waits for client packets to be sent to socket and sends them to the server.
     *
     * Waits for a response from the server and sends it back to the original client port.
     */
    public void run() {
        while(true) {
            int clientPort = 0;

            // Create new buffer and data packet
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            // Wait for client connection and save port
            try {
                receiveSocket.receive(receivePacket);
                clientPort = receivePacket.getPort();
            } catch (IOException e) {
                LOG.severe("Could not receive data packet from client: " + e.getLocalizedMessage());
                System.exit(1);
            }

            // Print packet information
            LOG.info("Received from " + receivePacket.getAddress().toString() + " data: " +
                new String(receivePacket.getData()) + " (Byte form " +  Arrays.toString(receivePacket.getData()) + ")");

            LOG.info("Preparing to send data to server...");

            // Create new packet to send to server using the data from the client packet
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, serverPort);
            sendPacket.setData(receivePacket.getData());

            try {
                sendReceiveSocket.send(sendPacket);
                LOG.info("Data sent to server. Waiting for response...");
            } catch (IOException e) {
                LOG.severe("Could not send data packet to server: " + e.getLocalizedMessage());
                System.exit(1);
            }

            // Reset the buffer and wait to receive a response from the server
            buffer = new byte[BUFFER_SIZE];
            try {
                receivePacket = new DatagramPacket(buffer, buffer.length);
                sendReceiveSocket.receive(receivePacket);
                LOG.info("Response received from server containing: " + Arrays.toString(receivePacket.getData()));
            } catch (IOException e) {
                LOG.severe("Could not receive data packet from server: " + e.getLocalizedMessage());
                System.exit(1);
            }

            // Send the response packet back to the client
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
